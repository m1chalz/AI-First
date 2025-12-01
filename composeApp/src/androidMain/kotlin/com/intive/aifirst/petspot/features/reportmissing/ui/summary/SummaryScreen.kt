
package com.intive.aifirst.petspot.features.reportmissing.ui.summary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.FlowStep
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ReportMissingEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ReportMissingIntent
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.ReportMissingViewModel
import com.intive.aifirst.petspot.navigation.NavRoute

/**
 * State host composable for Summary screen (No progress indicator).
 * Collects state from ViewModel, handles effects, and dispatches intents.
 *
 * @param viewModel Shared ViewModel for the flow (scoped to nav graph)
 * @param navController Shared NavController for navigation
 * @param modifier Modifier for the component
 */
@Composable
fun SummaryScreen(
    viewModel: ReportMissingViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Update ViewModel with current step
    LaunchedEffect(Unit) {
        viewModel.updateCurrentStep(FlowStep.SUMMARY)
    }

    // Handle navigation effects
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ReportMissingEffect.NavigateBack -> {
                    // From summary, pop back to AnimalList (exit entire flow)
                    navController.popBackStack(NavRoute.AnimalList, inclusive = false)
                }
                else -> { /* Other effects not handled on Summary */ }
            }
        }
    }

    SummaryContent(
        state = state,
        modifier = modifier,
        onCloseClick = { viewModel.dispatchIntent(ReportMissingIntent.Submit) },
    )
}
