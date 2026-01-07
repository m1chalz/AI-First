package com.intive.aifirst.petspot.features.mapPreview.presentation.mvi

import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.domain.models.LocationCoordinates
import com.intive.aifirst.petspot.domain.models.PermissionStatus

/**
 * Pure reducer for Map Preview state transitions.
 * No side effects - testable in isolation.
 */
object MapPreviewReducer {
    /**
     * Returns loading state, clears previous error.
     */
    fun loading(state: MapPreviewUiState): MapPreviewUiState = state.copy(isLoading = true, error = null)

    /**
     * Returns success state with location and animals.
     */
    fun success(
        state: MapPreviewUiState,
        location: LocationCoordinates,
        animals: List<Animal>,
    ): MapPreviewUiState =
        state.copy(
            isLoading = false,
            userLocation = location,
            animals = animals,
            error = null,
        )

    /**
     * Returns error state with specified error type.
     */
    fun error(
        state: MapPreviewUiState,
        error: MapPreviewError,
    ): MapPreviewUiState =
        state.copy(
            isLoading = false,
            error = error,
        )

    /**
     * Updates permission status to granted.
     */
    fun permissionGranted(state: MapPreviewUiState): MapPreviewUiState =
        state.copy(
            permissionStatus =
                PermissionStatus.Granted(
                    fineLocation = false,
                    coarseLocation = true,
                ),
        )

    /**
     * Updates permission status to denied.
     */
    fun permissionDenied(
        state: MapPreviewUiState,
        shouldShowRationale: Boolean = false,
    ): MapPreviewUiState =
        state.copy(
            permissionStatus = PermissionStatus.Denied(shouldShowRationale = shouldShowRationale),
        )

    /**
     * Updates permission status to requesting.
     */
    fun permissionRequesting(state: MapPreviewUiState): MapPreviewUiState =
        state.copy(permissionStatus = PermissionStatus.Requesting)
}
