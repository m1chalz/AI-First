package com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi

import com.intive.aifirst.petspot.composeapp.domain.models.Animal

/**
 * Immutable UI state for Lost Pets Teaser component.
 * Single source of truth for Compose UI rendering.
 */
data class LostPetsTeaserUiState(
    val animals: List<Animal> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    /**
     * Computed property: true when data loaded but list is empty.
     * Distinguishes empty state from loading or error states.
     */
    val isEmpty: Boolean
        get() = animals.isEmpty() && !isLoading && error == null

    companion object {
        val Initial = LostPetsTeaserUiState()
    }
}
