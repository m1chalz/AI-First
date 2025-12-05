package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

/**
 * Sealed interface for one-off events on the Summary screen.
 * Consumed once by the UI layer (navigation).
 */
sealed interface SummaryUiEffect {
    /**
     * Dismiss the entire Missing Pet flow and navigate to pet list.
     */
    data object DismissFlow : SummaryUiEffect
}

