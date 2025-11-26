package com.intive.aifirst.petspot.features.petdetails.presentation.mvi

/**
 * Sealed interface for user intents on Pet Details screen.
 * Each intent represents a user action that the ViewModel should handle.
 */
sealed interface PetDetailsIntent {
    /** Load pet details by ID */
    data class LoadPet(val id: String) : PetDetailsIntent

    /** Navigate back to previous screen */
    data object NavigateBack : PetDetailsIntent

    /** Show pet location on external map app */
    data object ShowOnMap : PetDetailsIntent

    /** Retry loading pet details after error */
    data object RetryLoad : PetDetailsIntent
}
