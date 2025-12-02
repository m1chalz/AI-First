package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

/**
 * Sealed interface for user intents on Chip Number screen (Step 1/4).
 * Represents all possible user actions on this screen.
 */
sealed interface ChipNumberUserIntent {

    /**
     * User typed/edited the microchip number field.
     * @param value Raw input from TextField (may contain non-digits)
     */
    data class UpdateChipNumber(val value: String) : ChipNumberUserIntent

    /**
     * User tapped the Continue button.
     * Saves chip number to flow state and navigates to Photo screen.
     */
    data object ContinueClicked : ChipNumberUserIntent

    /**
     * User tapped the back button (TopAppBar or system back).
     * Exits the flow without saving chip number.
     */
    data object BackClicked : ChipNumberUserIntent
}

