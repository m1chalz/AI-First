package com.intive.aifirst.petspot.features.animallist.presentation.mvi

import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.domain.models.LocationCoordinates
import com.intive.aifirst.petspot.domain.models.PermissionStatus

/**
 * Pure reducer for Animal List screen state transitions.
 * Takes current state and result, produces new state.
 * No side effects - testable in isolation.
 * Extended with permission and location state transitions.
 */
object AnimalListReducer {
    /**
     * Reduces state based on use case result.
     * Maps Result<List<Animal>> to appropriate UiState.
     *
     * @param currentState Current UI state
     * @param result Result from GetAnimalsUseCase
     * @return New UI state after reduction
     */
    fun reduce(
        currentState: AnimalListUiState,
        result: Result<List<Animal>>,
    ): AnimalListUiState =
        result.fold(
            onSuccess = { animals ->
                currentState.copy(
                    animals = animals,
                    isLoading = false,
                    error = null,
                )
            },
            onFailure = { exception ->
                currentState.copy(
                    // Preserve previous data on error
                    isLoading = false,
                    error = exception.message ?: "Unknown error",
                )
            },
        )

    /**
     * Returns loading state.
     */
    fun loading(currentState: AnimalListUiState): AnimalListUiState = currentState.copy(isLoading = true, error = null)

    // ========================================
    // Permission State Reducers (US1-US5)
    // ========================================

    /**
     * Reduces permission granted state.
     * Transitions to Granted status and clears any previous error.
     */
    fun permissionGranted(
        currentState: AnimalListUiState,
        fineLocation: Boolean,
        coarseLocation: Boolean,
    ): AnimalListUiState =
        currentState.copy(
            permissionStatus =
                PermissionStatus.Granted(
                    fineLocation = fineLocation,
                    coarseLocation = coarseLocation,
                ),
            isLocationLoading = true,
            error = null,
        )

    /**
     * Reduces permission denied state.
     * Clears location when transitioning from Granted state (permission revoked).
     */
    fun permissionDenied(
        currentState: AnimalListUiState,
        shouldShowRationale: Boolean,
    ): AnimalListUiState {
        // Clear location if permission was previously granted (revoked via Settings)
        val wasGranted = currentState.permissionStatus is PermissionStatus.Granted
        return currentState.copy(
            permissionStatus = PermissionStatus.Denied(shouldShowRationale = shouldShowRationale),
            isLocationLoading = false,
            location = if (wasGranted) null else currentState.location,
        )
    }

    /**
     * Reduces location fetched successfully state.
     */
    fun locationFetched(
        currentState: AnimalListUiState,
        latitude: Double,
        longitude: Double,
    ): AnimalListUiState =
        currentState.copy(
            location = LocationCoordinates(latitude, longitude),
            isLocationLoading = false,
        )

    /**
     * Reduces location fetch failed/timeout state.
     * Continues without location (fallback mode).
     */
    fun locationFetchFailed(currentState: AnimalListUiState): AnimalListUiState =
        currentState.copy(
            location = null,
            isLocationLoading = false,
        )

    /**
     * Reduces state to requesting permission.
     */
    fun requestingPermission(currentState: AnimalListUiState): AnimalListUiState =
        currentState.copy(
            permissionStatus = PermissionStatus.Requesting,
        )

    /**
     * Sets location loading state.
     */
    fun locationLoading(currentState: AnimalListUiState): AnimalListUiState =
        currentState.copy(
            isLocationLoading = true,
        )

    // ========================================
    // Rationale Dialog Reducers (US3, US4)
    // ========================================

    /**
     * Marks that rationale dialog has been shown this session.
     * Per FR-015, rationale is shown once per session.
     */
    fun rationaleShown(currentState: AnimalListUiState): AnimalListUiState =
        currentState.copy(
            rationaleShownThisSession = true,
        )
}
