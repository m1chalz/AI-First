package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

/**
 * Sealed interface for one-off effects in Missing Pet Report flow.
 * Navigation events handled via SharedFlow, not state.
 */
sealed interface ReportMissingEffect {
    /**
     * Navigate to next screen in flow.
     */
    data class NavigateToStep(val step: FlowStep) : ReportMissingEffect

    /**
     * Navigate back to previous screen.
     * Note: From step 1, popBackStack() automatically exits nested graph to AnimalList.
     */
    data object NavigateBack : ReportMissingEffect

    /**
     * Exit the entire flow and return to AnimalList.
     * Only handled by SummaryScreen after submission.
     */
    data object ExitFlow : ReportMissingEffect
}
