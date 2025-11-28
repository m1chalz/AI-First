package com.intive.aifirst.petspot.features.animallist.presentation.mvi

import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.domain.models.LocationCoordinates
import com.intive.aifirst.petspot.domain.models.PermissionStatus

/**
 * Immutable UI state for Animal List screen.
 * Single source of truth for Compose UI rendering.
 * Extended with location permission and coordinates for location-aware listings.
 */
data class AnimalListUiState(
    val animals: List<Animal> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val permissionStatus: PermissionStatus = PermissionStatus.NotRequested,
    val location: LocationCoordinates? = null,
    val isLocationLoading: Boolean = false,
    val rationaleShownThisSession: Boolean = false,
) {
    /**
     * Computed property: true when data loaded but list is empty.
     * Distinguishes empty state from loading or error states.
     */
    val isEmpty: Boolean
        get() = animals.isEmpty() && !isLoading && error == null

    companion object {
        val Initial = AnimalListUiState()
    }
}
