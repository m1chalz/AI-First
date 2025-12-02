package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

/**
 * Immutable UI state for Chip Number screen (Step 1/4).
 * Contains only screen-relevant state derived from shared flow state.
 *
 * @param chipNumber Raw digits (no hyphens) for microchip number. Max 15 digits.
 */
data class ChipNumberUiState(
    val chipNumber: String = "",
) {
    companion object {
        val Initial = ChipNumberUiState()
    }
}

