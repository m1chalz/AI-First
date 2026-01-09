package com.intive.aifirst.petspot.features.fullscreenmap.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLngBounds
import com.intive.aifirst.petspot.domain.models.LocationCoordinates
import com.intive.aifirst.petspot.domain.usecases.GetCurrentLocationUseCase
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapEffect
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapError
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapIntent
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapIntent.Initialize
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapIntent.OnAnimalTapped
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapIntent.OnBackPressed
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapIntent.OnPopupDismissed
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapIntent.OnRetryTapped
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapIntent.OnViewportChanged
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapReducer
import com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi.FullscreenMapUiState
import com.intive.aifirst.petspot.features.mapPreview.domain.usecases.GetNearbyAnimalsForMapUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * ViewModel for Fullscreen Map screen following MVI architecture.
 * Manages state for fullscreen map display with location-based animal pins.
 *
 * State flow: Intent → ViewModel → UseCase → Reducer → State → UI
 */
class FullscreenMapViewModel(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getNearbyAnimalsForMapUseCase: GetNearbyAnimalsForMapUseCase,
) : ViewModel() {
    // State
    private val _state = MutableStateFlow(FullscreenMapUiState.Initial)
    val state: StateFlow<FullscreenMapUiState> = _state.asStateFlow()

    // Effects (one-off events)
    private val _effects = Channel<FullscreenMapEffect>(capacity = Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    // Track last loaded bounds to avoid redundant loads
    private var lastLoadedBounds: LatLngBounds? = null

    /**
     * Processes user intents and updates state accordingly.
     * Entry point for all user actions.
     */
    fun dispatchIntent(intent: FullscreenMapIntent) {
        when (intent) {
            is Initialize -> handleInitialize()
            is OnViewportChanged -> handleViewportChanged(intent.bounds)
            is OnAnimalTapped -> handleAnimalTapped(intent.animalId)
            is OnPopupDismissed -> handlePopupDismissed()
            is OnRetryTapped -> handleRetry()
            is OnBackPressed -> handleBackPressed()
        }
    }

    /**
     * Initializes the map by loading user location and nearby animals.
     */
    private fun handleInitialize() {
        viewModelScope.launch {
            _state.value = FullscreenMapReducer.reduceAnimalsLoading(_state.value)

            val locationResult = getCurrentLocationUseCase()

            locationResult.fold(
                onSuccess = { location ->
                    if (location == null) {
                        _state.value =
                            FullscreenMapReducer.reduceAnimalsError(
                                _state.value,
                                FullscreenMapError.PermissionDenied,
                            )
                        return@launch
                    }

                    _state.value = FullscreenMapReducer.reduceLocationLoaded(_state.value, location)
                    loadAnimalsForLocation(location)
                },
                onFailure = { error ->
                    _state.value =
                        FullscreenMapReducer.reduceAnimalsError(
                            _state.value,
                            mapExceptionToErrorType(error),
                        )
                },
            )
        }
    }

    /**
     * Handles viewport change by loading animals for the new visible area.
     */
    private fun handleViewportChanged(bounds: LatLngBounds) {
        // Skip if bounds are the same as last load
        if (bounds == lastLoadedBounds) return

        viewModelScope.launch {
            _state.value = FullscreenMapReducer.reduceAnimalsLoading(_state.value)

            // Calculate center of bounds
            val center =
                LocationCoordinates(
                    latitude = bounds.center.latitude,
                    longitude = bounds.center.longitude,
                )

            loadAnimalsForLocation(center)
            lastLoadedBounds = bounds
        }
    }

    /**
     * Loads animals for a given location.
     */
    private suspend fun loadAnimalsForLocation(location: LocationCoordinates) {
        val animalsResult = getNearbyAnimalsForMapUseCase(location)

        animalsResult.fold(
            onSuccess = { animals ->
                _state.value = FullscreenMapReducer.reduceAnimalsSuccess(_state.value, animals)
            },
            onFailure = { error ->
                _state.value =
                    FullscreenMapReducer.reduceAnimalsError(
                        _state.value,
                        mapExceptionToErrorType(error),
                    )
            },
        )
    }

    /**
     * Handles pin tap by selecting the animal for popup display.
     */
    private fun handleAnimalTapped(animalId: String) {
        val animal = _state.value.animals.find { it.id == animalId }
        if (animal != null) {
            _state.value = FullscreenMapReducer.reduceAnimalSelected(_state.value, animal)
        }
    }

    /**
     * Handles popup dismissal by clearing the selected animal.
     */
    private fun handlePopupDismissed() {
        _state.value = FullscreenMapReducer.reducePopupDismissed(_state.value)
    }

    /**
     * Handles retry by re-initializing the map.
     */
    private fun handleRetry() {
        handleInitialize()
    }

    /**
     * Handles back press by emitting NavigateBack effect.
     */
    private fun handleBackPressed() {
        viewModelScope.launch {
            _effects.send(FullscreenMapEffect.NavigateBack)
        }
    }

    /**
     * Maps exceptions to error types.
     * UI layer is responsible for converting these to user-facing strings.
     */
    private fun mapExceptionToErrorType(error: Throwable): FullscreenMapError =
        when (error) {
            is SecurityException -> FullscreenMapError.PermissionDenied
            is IOException -> FullscreenMapError.NetworkError
            else -> FullscreenMapError.Unknown
        }
}
