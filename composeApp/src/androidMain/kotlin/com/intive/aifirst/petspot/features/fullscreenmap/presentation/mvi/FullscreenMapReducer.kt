package com.intive.aifirst.petspot.features.fullscreenmap.presentation.mvi

import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.domain.models.LocationCoordinates

/**
 * Pure reducer for Fullscreen Map state transitions.
 * No side effects - testable in isolation.
 */
object FullscreenMapReducer {
    /**
     * Returns loading state, clears previous error.
     */
    fun reduceAnimalsLoading(state: FullscreenMapUiState): FullscreenMapUiState =
        state.copy(isLoadingAnimals = true, error = null)

    /**
     * Returns success state with loaded animals.
     */
    fun reduceAnimalsSuccess(
        state: FullscreenMapUiState,
        animals: List<Animal>,
    ): FullscreenMapUiState =
        state.copy(
            isLoadingAnimals = false,
            animals = animals,
            error = null,
        )

    /**
     * Returns error state with error type.
     */
    fun reduceAnimalsError(
        state: FullscreenMapUiState,
        error: FullscreenMapError,
    ): FullscreenMapUiState =
        state.copy(
            isLoadingAnimals = false,
            error = error,
        )

    /**
     * Returns state with user location set.
     */
    fun reduceLocationLoaded(
        state: FullscreenMapUiState,
        location: LocationCoordinates,
    ): FullscreenMapUiState = state.copy(userLocation = location)

    /**
     * Returns state with selected animal for popup display.
     */
    fun reduceAnimalSelected(
        state: FullscreenMapUiState,
        animal: Animal,
    ): FullscreenMapUiState = state.copy(selectedAnimal = animal)

    /**
     * Returns state with popup dismissed (selectedAnimal cleared).
     */
    fun reducePopupDismissed(state: FullscreenMapUiState): FullscreenMapUiState = state.copy(selectedAnimal = null)
}
