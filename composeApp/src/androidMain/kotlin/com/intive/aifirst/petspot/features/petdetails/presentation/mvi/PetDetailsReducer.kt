package com.intive.aifirst.petspot.features.petdetails.presentation.mvi

import com.intive.aifirst.petspot.composeapp.domain.models.Animal

/**
 * Pure reducer functions for Pet Details screen state transitions.
 * All functions are side-effect free and return new state instances.
 */
object PetDetailsReducer {
    
    /**
     * Transitions to loading state.
     * Clears previous error while preserving pet data.
     */
    fun loading(state: PetDetailsUiState): PetDetailsUiState =
        state.copy(isLoading = true, error = null)
    
    /**
     * Reduces Result<Animal> to new UI state.
     * Handles both success and failure cases.
     */
    fun reduce(state: PetDetailsUiState, result: Result<Animal>): PetDetailsUiState =
        result.fold(
            onSuccess = { pet -> 
                state.copy(pet = pet, isLoading = false, error = null) 
            },
            onFailure = { throwable -> 
                state.copy(
                    isLoading = false, 
                    error = throwable.message ?: "Unknown error occurred",
                    pet = null
                ) 
            }
        )
}

