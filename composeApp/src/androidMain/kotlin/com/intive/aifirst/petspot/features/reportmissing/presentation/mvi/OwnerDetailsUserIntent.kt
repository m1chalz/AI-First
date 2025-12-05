package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

/**
 * User intents (actions) for Owner's Details screen.
 * Represents all possible user interactions.
 */
sealed interface OwnerDetailsUserIntent {
    /**
     * User updated the phone number field.
     */
    data class UpdatePhone(val phone: String) : OwnerDetailsUserIntent

    /**
     * User updated the email field.
     */
    data class UpdateEmail(val email: String) : OwnerDetailsUserIntent

    /**
     * User updated the reward description field.
     */
    data class UpdateReward(val reward: String) : OwnerDetailsUserIntent

    /**
     * User tapped the Continue button.
     * Triggers validation, then submission if valid.
     */
    data object ContinueClicked : OwnerDetailsUserIntent

    /**
     * User tapped the Back button.
     */
    data object BackClicked : OwnerDetailsUserIntent

    /**
     * User tapped Retry in the error Snackbar.
     */
    data object RetryClicked : OwnerDetailsUserIntent
}
