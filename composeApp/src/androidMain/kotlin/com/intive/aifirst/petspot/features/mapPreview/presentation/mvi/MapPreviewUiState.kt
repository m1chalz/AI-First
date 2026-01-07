package com.intive.aifirst.petspot.features.mapPreview.presentation.mvi

import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.domain.models.LocationCoordinates
import com.intive.aifirst.petspot.domain.models.PermissionStatus

/**
 * Immutable UI state for Map Preview component.
 * Single source of truth for Compose UI rendering.
 */
data class MapPreviewUiState(
    val permissionStatus: PermissionStatus = PermissionStatus.NotRequested,
    val isLoading: Boolean = false,
    val userLocation: LocationCoordinates? = null,
    val animals: List<Animal> = emptyList(),
    val error: MapPreviewError? = null,
) {
    companion object {
        val Initial = MapPreviewUiState()
    }

    /**
     * Derived: animals with valid coordinates for map display.
     * Filters out animals without location data.
     */
    val animalsWithLocation: List<Animal>
        get() =
            animals.filter {
                it.location.latitude != null && it.location.longitude != null
            }

    /**
     * Computed: true when permission is granted (either fine or coarse).
     */
    val isPermissionGranted: Boolean
        get() = permissionStatus is PermissionStatus.Granted

    /**
     * Computed: true when permission was denied by user.
     */
    val isPermissionDenied: Boolean
        get() = permissionStatus is PermissionStatus.Denied

    /**
     * Computed: true when we have everything needed to show the map.
     */
    val canShowMap: Boolean
        get() = isPermissionGranted && userLocation != null && !isLoading && error == null
}
