package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

/**
 * Immutable data class representing the UI state of the Summary screen.
 */
data class SummaryUiState(
    /** The 6-7 digit management password/code (empty string if null in flowState). */
    val managementPassword: String = "",
) {
    companion object {
        val Initial = SummaryUiState()
    }
}

