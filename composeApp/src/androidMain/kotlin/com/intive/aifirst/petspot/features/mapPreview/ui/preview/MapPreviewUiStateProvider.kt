package com.intive.aifirst.petspot.features.mapPreview.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.intive.aifirst.petspot.composeapp.domain.fixtures.MockAnimalData
import com.intive.aifirst.petspot.domain.models.LocationCoordinates
import com.intive.aifirst.petspot.domain.models.PermissionStatus
import com.intive.aifirst.petspot.features.mapPreview.presentation.mvi.MapPreviewError
import com.intive.aifirst.petspot.features.mapPreview.presentation.mvi.MapPreviewUiState

/**
 * PreviewParameterProvider for MapPreviewContent composable.
 * Provides various UI states for Compose Preview.
 */
class MapPreviewUiStateProvider : PreviewParameterProvider<MapPreviewUiState> {
    override val values: Sequence<MapPreviewUiState> =
        sequenceOf(
            // Loading state
            MapPreviewUiState(
                permissionStatus = PermissionStatus.Granted(fineLocation = false, coarseLocation = true),
                isLoading = true,
            ),
            // Success state with pins
            MapPreviewUiState(
                permissionStatus = PermissionStatus.Granted(fineLocation = false, coarseLocation = true),
                isLoading = false,
                userLocation = LocationCoordinates(52.2297, 21.0122),
                animals = MockAnimalData.generateMockAnimals(8),
            ),
            // Error state - location unavailable
            MapPreviewUiState(
                permissionStatus = PermissionStatus.Granted(fineLocation = false, coarseLocation = true),
                isLoading = false,
                error = MapPreviewError.LocationNotAvailable,
            ),
            // Error state - network error
            MapPreviewUiState(
                permissionStatus = PermissionStatus.Granted(fineLocation = false, coarseLocation = true),
                isLoading = false,
                error = MapPreviewError.NetworkError,
            ),
            // Permission not requested
            MapPreviewUiState(
                permissionStatus = PermissionStatus.NotRequested,
            ),
            // Permission denied
            MapPreviewUiState(
                permissionStatus = PermissionStatus.Denied(shouldShowRationale = true),
            ),
        )
}
