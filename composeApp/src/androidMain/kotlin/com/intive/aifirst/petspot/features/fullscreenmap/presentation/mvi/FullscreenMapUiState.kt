package com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi

import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.domain.models.LocationCoordinates
import com.intive.aifirst.petspot.domain.models.PermissionStatus

/**
 * Immutable UI state for Fullscreen Map screen.
 * Single source of truth for Compose UI rendering.
 */
data class FullscreenMapUiState(
    val userLocation: LocationCoordinates? = null,
    val animals: List<Animal> = emptyList(),
    val isLoadingAnimals: Boolean = false,
    val error: FullscreenMapError? = null,
    val selectedAnimal: Animal? = null,
    val permissionStatus: PermissionStatus = PermissionStatus.NotRequested,
) {
    companion object {
        val Initial = FullscreenMapUiState()
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

    /** True when popup should be visible. */
    val isPopupVisible: Boolean get() = selectedAnimal != null

    /** True when an error occurred. */
    val hasError: Boolean get() = error != null
}
