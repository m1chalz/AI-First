package com.intive.aifirst.petspot.features.reportmissing.ui

import androidx.lifecycle.ViewModel
import com.intive.aifirst.petspot.features.reportmissing.presentation.state.ReportMissingFlowState

/**
 * Simple ViewModel wrapper to scope ReportMissingFlowState to the NavGraph.
 * 
 * This ViewModel exists solely to provide NavGraph lifecycle scoping for the flow state.
 * It doesn't contain any business logic - that's handled by screen-specific ViewModels.
 *
 * Usage: Get this from parent NavBackStackEntry to share flow state across screens.
 */
class FlowStateHolder : ViewModel() {
    val flowState = ReportMissingFlowState()
}

