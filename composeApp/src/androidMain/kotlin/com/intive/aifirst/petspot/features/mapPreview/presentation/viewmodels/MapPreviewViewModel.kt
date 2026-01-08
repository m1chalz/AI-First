package com.intive.aifirst.petspot.features.mapPreview.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intive.aifirst.petspot.domain.usecases.GetCurrentLocationUseCase
import com.intive.aifirst.petspot.features.mapPreview.domain.usecases.GetNearbyAnimalsForMapUseCase
import com.intive.aifirst.petspot.features.mapPreview.presentation.mvi.MapPreviewEffect
import com.intive.aifirst.petspot.features.mapPreview.presentation.mvi.MapPreviewError
import com.intive.aifirst.petspot.features.mapPreview.presentation.mvi.MapPreviewIntent
import com.intive.aifirst.petspot.features.mapPreview.presentation.mvi.MapPreviewIntent.LoadMap
import com.intive.aifirst.petspot.features.mapPreview.presentation.mvi.MapPreviewIntent.PermissionDenied
import com.intive.aifirst.petspot.features.mapPreview.presentation.mvi.MapPreviewIntent.PermissionGranted
import com.intive.aifirst.petspot.features.mapPreview.presentation.mvi.MapPreviewIntent.RequestPermission
import com.intive.aifirst.petspot.features.mapPreview.presentation.mvi.MapPreviewIntent.Retry
import com.intive.aifirst.petspot.features.mapPreview.presentation.mvi.MapPreviewReducer
import com.intive.aifirst.petspot.features.mapPreview.presentation.mvi.MapPreviewUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * ViewModel for Map Preview component following MVI architecture.
 * Manages state for map display with location-based animal pins.
 *
 * State flow:
 * Intent → ViewModel → UseCase → Reducer → State → UI
 */
class MapPreviewViewModel(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getNearbyAnimalsForMapUseCase: GetNearbyAnimalsForMapUseCase,
) : ViewModel() {
    // State
    private val _state = MutableStateFlow(MapPreviewUiState.Initial)
    val state: StateFlow<MapPreviewUiState> = _state.asStateFlow()

    // Effects (one-off events)
    private val _effects = Channel<MapPreviewEffect>(capacity = Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        dispatchIntent(LoadMap)
    }

    /**
     * Processes user intents and updates state accordingly.
     * Entry point for all user actions.
     */
    fun dispatchIntent(intent: MapPreviewIntent) {
        when (intent) {
            is LoadMap -> handleLoadMap()
            is RequestPermission -> handleRequestPermission()
            is PermissionGranted -> handlePermissionGranted()
            is PermissionDenied -> handlePermissionDenied()
            is Retry -> handleRetry()
        }
    }

    /**
     * Handles LoadMap intent: fetches location and nearby animals.
     */
    private fun handleLoadMap() {
        viewModelScope.launch {
            _state.value = MapPreviewReducer.loading(_state.value)

            // Step 1: Get current location
            val locationResult = getCurrentLocationUseCase()

            locationResult.fold(
                onSuccess = { location ->
                    if (location == null) {
                        _state.value =
                            MapPreviewReducer.error(
                                _state.value,
                                MapPreviewError.LocationNotAvailable,
                            )
                        return@launch
                    }

                    // Step 2: Fetch nearby animals
                    val animalsResult = getNearbyAnimalsForMapUseCase(location)

                    animalsResult.fold(
                        onSuccess = { animals ->
                            _state.value =
                                MapPreviewReducer.success(
                                    _state.value,
                                    location,
                                    animals,
                                )
                        },
                        onFailure = { error ->
                            _state.value =
                                MapPreviewReducer.error(
                                    _state.value,
                                    mapExceptionToError(error),
                                )
                        },
                    )
                },
                onFailure = { error ->
                    _state.value =
                        MapPreviewReducer.error(
                            _state.value,
                            mapExceptionToError(error),
                        )
                },
            )
        }
    }

    /**
     * Handles RequestPermission intent: updates state to requesting.
     */
    private fun handleRequestPermission() {
        _state.value = MapPreviewReducer.permissionRequesting(_state.value)
    }

    /**
     * Handles PermissionGranted intent: updates permission status and triggers load.
     */
    private fun handlePermissionGranted() {
        _state.value = MapPreviewReducer.permissionGranted(_state.value)
        handleLoadMap()
    }

    /**
     * Handles PermissionDenied intent: updates permission status to denied.
     */
    private fun handlePermissionDenied() {
        _state.value = MapPreviewReducer.permissionDenied(_state.value, shouldShowRationale = false)
    }

    /**
     * Handles Retry intent: re-attempts LoadMap flow.
     */
    private fun handleRetry() {
        handleLoadMap()
    }

    /**
     * Maps exceptions to appropriate MapPreviewError types.
     */
    private fun mapExceptionToError(error: Throwable): MapPreviewError =
        when (error) {
            is SecurityException -> MapPreviewError.LocationNotAvailable
            is IOException -> MapPreviewError.NetworkError
            else -> MapPreviewError.MapLoadFailed
        }
}
