package com.intive.aifirst.petspot.features.petdetails.presentation.mvi

import com.intive.aifirst.petspot.composeapp.domain.models.Animal

/**
 * Immutable UI state for Pet Details screen.
 * Single source of truth for Compose UI rendering.
 */
data class PetDetailsUiState(
    val pet: Animal? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    companion object {
        val Initial = PetDetailsUiState()
    }
}
