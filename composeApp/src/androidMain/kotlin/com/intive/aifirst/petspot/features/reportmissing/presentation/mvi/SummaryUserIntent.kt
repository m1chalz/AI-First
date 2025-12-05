package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

/**
 * Sealed interface representing all possible user actions on the Summary screen.
 */
sealed interface SummaryUserIntent {
    /**
     * User tapped the password container to copy to clipboard.
     */
    data object CopyPasswordClicked : SummaryUserIntent

    /**
     * User tapped the Close button or performed system back action.
     */
    data object CloseClicked : SummaryUserIntent
}
