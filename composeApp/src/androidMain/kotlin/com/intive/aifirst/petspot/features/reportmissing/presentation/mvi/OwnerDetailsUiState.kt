package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

/**
 * Immutable UI state for Owner's Details screen.
 * Single source of truth for all UI rendering.
 */
data class OwnerDetailsUiState(
    val phone: String = "",
    val email: String = "",
    val reward: String = "",
    val phoneError: String? = null,
    val emailError: String? = null,
    val isSubmitting: Boolean = false,
) {
    /**
     * Maximum characters allowed for reward description.
     */
    val rewardMaxLength: Int = 120

    /**
     * Current character count for reward field.
     */
    val rewardCharacterCount: Int = reward.length

    /**
     * Whether the form can be submitted (not currently submitting).
     */
    val canSubmit: Boolean = !isSubmitting

    /**
     * Whether there are any validation errors displayed.
     */
    val hasErrors: Boolean = phoneError != null || emailError != null
}

