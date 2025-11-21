package com.intive.aifirst.petspot.features.animallist.presentation.mvi

import com.intive.aifirst.petspot.domain.models.Animal

/**
 * Pure reducer for Animal List screen state transitions.
 * Takes current state and result, produces new state.
 * No side effects - testable in isolation.
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
    ): AnimalListUiState {
        return result.fold(
            onSuccess = { animals ->
                AnimalListUiState(
                    animals = animals,
                    isLoading = false,
                    error = null,
                )
            },
            onFailure = { exception ->
                AnimalListUiState(
                    // Preserve previous data on error
                    animals = currentState.animals,
                    isLoading = false,
                    error = exception.message ?: "Unknown error",
                )
            },
        )
    }

    /**
     * Returns loading state.
     */
    fun loading(currentState: AnimalListUiState): AnimalListUiState {
        return currentState.copy(isLoading = true, error = null)
    }
}
