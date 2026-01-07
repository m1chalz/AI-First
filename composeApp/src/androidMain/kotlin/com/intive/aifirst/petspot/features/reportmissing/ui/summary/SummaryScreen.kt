package com.intive.aifirst.petspot.features.reportmissing.ui.summary

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.SummaryUiEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.SummaryUserIntent
import com.intive.aifirst.petspot.features.reportmissing.presentation.state.ReportMissingFlowState
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.SummaryViewModel
import com.intive.aifirst.petspot.navigation.LostPetRoute

/**
 * State host composable for Summary/Report Created Confirmation screen.
 * Collects state from ViewModel, handles effects, and dispatches intents.
 *
 * NOTE: This screen does NOT have a TopAppBar with back navigation (FR-011).
 * Only the Close button at bottom exits the flow.
 *
 * @param viewModel SummaryViewModel for this screen
 * @param flowState Shared flow state (used for clearing on dismiss)
 * @param navController Shared NavController for navigation
 * @param modifier Modifier for the component
 */
@Composable
fun SummaryScreen(
    viewModel: SummaryViewModel,
    flowState: ReportMissingFlowState,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Handle one-off effects
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is SummaryUiEffect.DismissFlow -> {
                    // Clear flow state and exit entire flow
                    flowState.clear()
                    navController.popBackStack(LostPetRoute.List, inclusive = false)
                }
            }
        }
    }

    // Handle system back button/gesture - same as Close button (FR-010)
    BackHandler {
        viewModel.dispatchIntent(SummaryUserIntent.CloseClicked)
    }

    SummaryContent(
        state = state,
        modifier = modifier,
        onPasswordContainerClick = { viewModel.dispatchIntent(SummaryUserIntent.CopyPasswordClicked) },
        onCloseClick = { viewModel.dispatchIntent(SummaryUserIntent.CloseClicked) },
    )
}
