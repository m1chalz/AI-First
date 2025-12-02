
package com.intive.aifirst.petspot.features.reportmissing.ui.description

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
import com.intive.aifirst.petspot.navigation.ReportMissingRoute

/**
 * State host composable for Description screen (Step 3/4).
 * Collects state from ViewModel, handles effects, and dispatches intents.
 *
 * @param viewModel Shared ViewModel for the flow (scoped to nav graph)
 * @param navController Shared NavController for navigation
 * @param modifier Modifier for the component
 */
@Composable
fun DescriptionScreen(
    viewModel: ReportMissingViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Update ViewModel with current step
    LaunchedEffect(Unit) {
        viewModel.updateCurrentStep(FlowStep.DESCRIPTION)
    }

    // Handle navigation effects
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ReportMissingEffect.NavigateToStep -> {
                    navController.navigate(effect.step.toRoute()) {
                        launchSingleTop = true
                    }
                }
                is ReportMissingEffect.NavigateBack -> {
                    navController.popBackStack()
                }
                is ReportMissingEffect.ExitFlow -> {
                    // Only handled by SummaryScreen
                }
                is ReportMissingEffect.LaunchPhotoPicker -> {
                    // Only handled by PhotoScreen
                }
                is ReportMissingEffect.ShowToast -> {
                    // Only handled by PhotoScreen
                }
            }
        }
    }

    DescriptionContent(
        state = state,
        modifier = modifier,
        onBackClick = { viewModel.dispatchIntent(ReportMissingIntent.NavigateBack) },
        onContinueClick = { viewModel.dispatchIntent(ReportMissingIntent.NavigateNext) },
    )
}

private fun FlowStep.toRoute(): ReportMissingRoute =
    when (this) {
        FlowStep.CHIP_NUMBER -> ReportMissingRoute.ChipNumber
        FlowStep.PHOTO -> ReportMissingRoute.Photo
        FlowStep.DESCRIPTION -> ReportMissingRoute.Description
        FlowStep.CONTACT_DETAILS -> ReportMissingRoute.ContactDetails
        FlowStep.SUMMARY -> ReportMissingRoute.Summary
    }
