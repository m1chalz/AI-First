package com.intive.aifirst.petspot.features.reportmissing.ui.chipnumber

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ChipNumberUiEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ChipNumberUserIntent
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.FlowStep
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.ChipNumberViewModel
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.ReportMissingViewModel
import com.intive.aifirst.petspot.navigation.ReportMissingRoute
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * State host composable for Chip Number screen (Step 1/4).
 * Collects state from screen-specific ViewModel, handles effects, and dispatches intents.
 *
 * @param sharedViewModel NavGraph-scoped ViewModel for flow state persistence
 * @param navController Shared NavController for navigation
 * @param modifier Modifier for the component
 */
@Composable
fun ChipNumberScreen(
    sharedViewModel: ReportMissingViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    // Screen-specific ViewModel with shared ViewModel as dependency
    val viewModel: ChipNumberViewModel = koinViewModel { parametersOf(sharedViewModel) }

    val state by viewModel.state.collectAsStateWithLifecycle()

    // Update shared ViewModel with current step
    LaunchedEffect(Unit) {
        sharedViewModel.updateCurrentStep(FlowStep.CHIP_NUMBER)
    }

    // Handle navigation effects from screen-specific ViewModel
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ChipNumberUiEffect.NavigateToPhoto -> {
                    navController.navigate(ReportMissingRoute.Photo) {
                        launchSingleTop = true
                    }
                }
                is ChipNumberUiEffect.NavigateBack -> {
                    // Pop entire flow (from step 1/4, goes back to pet list)
                    navController.popBackStack()
                }
            }
        }
    }

    // Handle system back button/gesture (FR-018)
    BackHandler {
        viewModel.handleIntent(ChipNumberUserIntent.BackClicked)
    }

    ChipNumberContent(
        state = state,
        modifier = modifier,
        onChipNumberChange = { viewModel.handleIntent(ChipNumberUserIntent.UpdateChipNumber(it)) },
        onBackClick = { viewModel.handleIntent(ChipNumberUserIntent.BackClicked) },
        onContinueClick = { viewModel.handleIntent(ChipNumberUserIntent.ContinueClicked) },
    )
}
