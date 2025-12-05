package com.intive.aifirst.petspot.features.animallist.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intive.aifirst.petspot.composeapp.domain.usecases.GetAnimalsUseCase
import com.intive.aifirst.petspot.domain.models.PermissionStatus
import com.intive.aifirst.petspot.domain.models.RationaleDialogType
import com.intive.aifirst.petspot.domain.usecases.CheckLocationPermissionUseCase
import com.intive.aifirst.petspot.domain.usecases.GetCurrentLocationUseCase
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListEffect
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListIntent
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListReducer
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Animal List screen following MVI architecture.
 * Manages UI state, processes user intents, and emits one-off effects.
 * Extended with location permission handling and two-stage location fetch.
 *
 * State flow:
 * Intent → ViewModel → UseCase → Reducer → State → UI
 *
 * Effects (one-off events like navigation) emitted via SharedFlow.
 */
class AnimalListViewModel(
    private val getAnimalsUseCase: GetAnimalsUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase? = null,
    private val checkLocationPermissionUseCase: CheckLocationPermissionUseCase? = null,
) : ViewModel() {
    // State
    private val _state = MutableStateFlow(AnimalListUiState.Initial)
    val state: StateFlow<AnimalListUiState> = _state.asStateFlow()

    // Effects (one-off events)
    private val _effects = MutableSharedFlow<AnimalListEffect>()
    val effects: SharedFlow<AnimalListEffect> = _effects.asSharedFlow()

    init {
        // Permission check is handled by UI (Accompanist) on first composition
        // Animals will load after permission flow completes via PermissionResult intent
        // Set loading state to indicate waiting for permission check
        _state.value = _state.value.copy(isLoading = true)
    }

    /**
     * Processes user intents and updates state accordingly.
     * Entry point for all user actions.
     */
    fun dispatchIntent(intent: AnimalListIntent) {
        when (intent) {
            is AnimalListIntent.Refresh -> handleRefresh()
            is AnimalListIntent.SelectAnimal -> handleSelectAnimal(intent.id)
            is AnimalListIntent.ReportMissing -> handleReportMissing()
            is AnimalListIntent.ReportFound -> handleReportFound()
            // Location permission intents
            is AnimalListIntent.CheckPermission -> handleCheckPermission()
            is AnimalListIntent.PermissionResult -> handlePermissionResult(intent)
            is AnimalListIntent.LocationFetched -> handleLocationFetched(intent)
            is AnimalListIntent.LocationFetchFailed -> handleLocationFetchFailed()
            is AnimalListIntent.PermissionStateChanged -> handlePermissionStateChanged(intent)
            // Rationale dialog intents (US3, US4)
            is AnimalListIntent.RationaleDismissed -> handleRationaleDismissed()
            is AnimalListIntent.OpenSettingsRequested -> handleOpenSettingsRequested()
            is AnimalListIntent.RationaleContinue -> handleRationaleContinue()
        }
    }

    /**
     * Handles Refresh intent: loads animals from repository.
     * Passes current location (if available) to filter nearby animals.
     */
    private fun handleRefresh() {
        viewModelScope.launch {
            // Set loading state
            _state.value = AnimalListReducer.loading(_state.value)

            // Call use case with location if available
            val location = _state.value.location
            val result =
                runCatching {
                    getAnimalsUseCase(
                        lat = location?.latitude,
                        lng = location?.longitude,
                    )
                }

            // Reduce result to new state
            _state.value = AnimalListReducer.reduce(_state.value, result)
        }
    }

    /**
     * Handles SelectAnimal intent: emits navigation effect.
     */
    private fun handleSelectAnimal(animalId: String) {
        viewModelScope.launch {
            _effects.emit(AnimalListEffect.NavigateToDetails(animalId))
        }
    }

    /**
     * Handles ReportMissing intent: emits navigation effect.
     */
    private fun handleReportMissing() {
        viewModelScope.launch {
            _effects.emit(AnimalListEffect.NavigateToReportMissing)
        }
    }

    /**
     * Handles ReportFound intent: emits navigation effect.
     */
    private fun handleReportFound() {
        viewModelScope.launch {
            _effects.emit(AnimalListEffect.NavigateToReportFound)
        }
    }

    // ========================================
    // Location Permission Handlers (US1-US5)
    // ========================================

    /**
     * Handles CheckPermission intent: checks current permission status.
     * If permission is already granted, fetches location.
     * If not granted, emits effect to request permission.
     */
    private fun handleCheckPermission() {
        val permissionUseCase = checkLocationPermissionUseCase
        if (permissionUseCase == null) {
            // No permission checker available, emit effect for UI to check
            viewModelScope.launch {
                _effects.emit(AnimalListEffect.CheckPermissionStatus)
            }
            return
        }

        viewModelScope.launch {
            val currentStatus = permissionUseCase()

            when (currentStatus) {
                is PermissionStatus.Granted -> {
                    // Permission already granted, fetch location
                    _state.value =
                        AnimalListReducer.permissionGranted(
                            _state.value,
                            fineLocation = currentStatus.fineLocation,
                            coarseLocation = currentStatus.coarseLocation,
                        )
                    fetchLocation()
                }

                is PermissionStatus.NotRequested -> {
                    // First time - request permission
                    _state.value = AnimalListReducer.requestingPermission(_state.value)
                    _effects.emit(AnimalListEffect.RequestPermission)
                }

                is PermissionStatus.Denied -> {
                    // Permission denied - update state
                    _state.value =
                        AnimalListReducer.permissionDenied(
                            _state.value,
                            shouldShowRationale = currentStatus.shouldShowRationale,
                        )
                    // Refresh animals without location
                    handleRefresh()
                }

                is PermissionStatus.Requesting -> {
                    // Already requesting, do nothing
                }
            }
        }
    }

    /**
     * Handles PermissionResult intent: updates permission state and fetches location if granted.
     * When denied, shows appropriate rationale dialog (once per session).
     */
    private fun handlePermissionResult(intent: AnimalListIntent.PermissionResult) {
        viewModelScope.launch {
            if (intent.granted) {
                // Permission granted - update state and fetch location
                _state.value =
                    AnimalListReducer.permissionGranted(
                        _state.value,
                        fineLocation = intent.fineLocation,
                        coarseLocation = intent.coarseLocation,
                    )
                // Fetch location
                fetchLocation()
            } else {
                // Permission denied - update state
                _state.value =
                    AnimalListReducer.permissionDenied(
                        _state.value,
                        shouldShowRationale = intent.shouldShowRationale,
                    )

                // Show appropriate rationale dialog (once per session per FR-015)
                if (!_state.value.rationaleShownThisSession) {
                    val rationaleType =
                        if (intent.shouldShowRationale) {
                            RationaleDialogType.Educational
                        } else {
                            RationaleDialogType.Informational
                        }
                    _effects.emit(AnimalListEffect.ShowRationaleDialog(rationaleType))
                    _state.value = AnimalListReducer.rationaleShown(_state.value)
                }

                // Refresh animals without location
                handleRefresh()
            }
        }
    }

    /**
     * Handles LocationFetched intent: updates location and refreshes animal list.
     */
    private fun handleLocationFetched(intent: AnimalListIntent.LocationFetched) {
        viewModelScope.launch {
            _state.value =
                AnimalListReducer.locationFetched(
                    _state.value,
                    latitude = intent.latitude,
                    longitude = intent.longitude,
                )
            // Refresh animals with location
            handleRefresh()
        }
    }

    /**
     * Handles LocationFetchFailed intent: continues without location.
     */
    private fun handleLocationFetchFailed() {
        viewModelScope.launch {
            _state.value = AnimalListReducer.locationFetchFailed(_state.value)
            // Refresh animals without location (fallback mode)
            handleRefresh()
        }
    }

    /**
     * Handles PermissionStateChanged intent: reacts to dynamic permission changes.
     */
    private fun handlePermissionStateChanged(intent: AnimalListIntent.PermissionStateChanged) {
        viewModelScope.launch {
            val currentStatus = _state.value.permissionStatus

            if (intent.granted && currentStatus !is PermissionStatus.Granted) {
                // Permission changed from denied to granted
                _state.value =
                    AnimalListReducer.permissionGranted(
                        _state.value,
                        fineLocation = true,
                        coarseLocation = true,
                    )
                fetchLocation()
            } else if (!intent.granted && currentStatus is PermissionStatus.Granted) {
                // Permission changed from granted to denied
                _state.value =
                    AnimalListReducer.permissionDenied(
                        _state.value,
                        shouldShowRationale = intent.shouldShowRationale,
                    )
            }
        }
    }

    // ========================================
    // Rationale Dialog Handlers (US3, US4)
    // ========================================

    /**
     * Handles RationaleDismissed intent: user dismissed rationale dialog.
     * Continues without location (fallback mode).
     */
    private fun handleRationaleDismissed() {
        // Already marked as shown, just continue with animal list
        handleRefresh()
    }

    /**
     * Handles OpenSettingsRequested intent: user wants to open device Settings.
     * Emits OpenSettings effect for UI to navigate.
     */
    private fun handleOpenSettingsRequested() {
        viewModelScope.launch {
            _effects.emit(AnimalListEffect.OpenSettings)
        }
    }

    /**
     * Handles RationaleContinue intent: user tapped Continue on educational rationale.
     * Emits RequestPermission effect to trigger system dialog.
     */
    private fun handleRationaleContinue() {
        viewModelScope.launch {
            _state.value = AnimalListReducer.requestingPermission(_state.value)
            _effects.emit(AnimalListEffect.RequestPermission)
        }
    }

    /**
     * Fetches current location using two-stage approach.
     * Updates state with location or triggers fallback on failure.
     */
    private fun fetchLocation() {
        val locationUseCase = getCurrentLocationUseCase ?: return

        viewModelScope.launch {
            _state.value = AnimalListReducer.locationLoading(_state.value)

            val result = locationUseCase()

            result.fold(
                onSuccess = { coordinates ->
                    if (coordinates != null) {
                        dispatchIntent(
                            AnimalListIntent.LocationFetched(
                                latitude = coordinates.latitude,
                                longitude = coordinates.longitude,
                            ),
                        )
                    } else {
                        // Both stages returned null - fallback mode
                        dispatchIntent(AnimalListIntent.LocationFetchFailed)
                    }
                },
                onFailure = {
                    // Error occurred - fallback mode
                    dispatchIntent(AnimalListIntent.LocationFetchFailed)
                },
            )
        }
    }
}
