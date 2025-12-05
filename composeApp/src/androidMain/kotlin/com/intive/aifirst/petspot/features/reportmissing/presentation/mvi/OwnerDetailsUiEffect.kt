package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

/**
 * One-off UI effects for Owner's Details screen.
 * Consumed once by the UI layer (navigation, snackbars).
 */
sealed interface OwnerDetailsUiEffect {
    /**
     * Navigate to summary screen after successful submission.
     * @param managementPassword The 6-digit code to display on summary.
     */
    data class NavigateToSummary(val managementPassword: String) : OwnerDetailsUiEffect

    /**
     * Navigate back to the previous screen.
     */
    data object NavigateBack : OwnerDetailsUiEffect

    /**
     * Show a Snackbar with optional action.
     * @param message The message to display.
     * @param actionLabel Optional action button label (e.g., "Retry").
     * @param action The intent to dispatch when action is clicked.
     */
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val action: OwnerDetailsUserIntent? = null,
    ) : OwnerDetailsUiEffect
}
