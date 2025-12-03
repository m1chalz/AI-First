package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

/**
 * UI effects for the Animal Description screen (Step 3/4).
 * One-off events that should be consumed exactly once.
 */
sealed interface AnimalDescriptionUiEffect {
    data object NavigateToContactDetails : AnimalDescriptionUiEffect

    data object NavigateBack : AnimalDescriptionUiEffect

    data class ShowSnackbar(val message: String) : AnimalDescriptionUiEffect

    data object OpenLocationSettings : AnimalDescriptionUiEffect
}
