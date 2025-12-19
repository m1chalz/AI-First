package com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi

import com.intive.aifirst.petspot.composeapp.domain.models.Animal

/**
 * Pure reducer for Lost Pets Teaser state transitions.
 * No side effects - testable in isolation.
 */
object LostPetsTeaserReducer {
    /**
     * Returns loading state.
     */
    fun loading(state: LostPetsTeaserUiState): LostPetsTeaserUiState = state.copy(isLoading = true, error = null)

    /**
     * Returns success state with animals.
     */
    fun success(
        state: LostPetsTeaserUiState,
        animals: List<Animal>,
    ): LostPetsTeaserUiState = state.copy(isLoading = false, animals = animals, error = null)

    /**
     * Returns error state with message.
     */
    fun error(
        state: LostPetsTeaserUiState,
        message: String,
    ): LostPetsTeaserUiState = state.copy(isLoading = false, error = message)
}
