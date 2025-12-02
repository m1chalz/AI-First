package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

/**
 * Sealed interface for one-off UI effects from Chip Number screen (Step 1/4).
 * Used for navigation events that should only be consumed once.
 */
sealed interface ChipNumberUiEffect {

    /**
     * Navigate to Photo screen (Step 2/4).
     * Emitted after chip number is saved to flow state.
     */
    data object NavigateToPhoto : ChipNumberUiEffect

    /**
     * Navigate back (exit flow).
     * Emitted when user cancels without saving.
     */
    data object NavigateBack : ChipNumberUiEffect
}

