package com.intive.aifirst.petspot.features.reportmissing.ui.contactdetails

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.OwnerDetailsUiEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.OwnerDetailsUserIntent
import com.intive.aifirst.petspot.features.reportmissing.presentation.state.ReportMissingFlowState
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.OwnerDetailsViewModel
import com.intive.aifirst.petspot.navigation.ReportMissingRoute

/**
 * State host composable for Contact Details screen (Step 4/4).
 * Collects state from ViewModel, handles effects, and dispatches intents.
 *
 * Following the same pattern as ChipNumberScreen and PhotoScreen (no Scaffold).
 *
 * @param viewModel OwnerDetailsViewModel for this screen (scoped to nav graph)
 * @param flowState Shared flow state for persisting data across screens
 * @param navController Shared NavController for navigation
 * @param modifier Modifier for the component
 */
@Composable
fun ContactDetailsScreen(
    viewModel: OwnerDetailsViewModel,
    flowState: ReportMissingFlowState,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle one-off effects
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is OwnerDetailsUiEffect.NavigateToSummary -> {
                    // Store management password in flow state before navigating
                    flowState.updateManagementPassword(effect.managementPassword)
                    navController.navigate(ReportMissingRoute.Summary) {
                        launchSingleTop = true
                    }
                }
                is OwnerDetailsUiEffect.NavigateBack -> {
                    navController.popBackStack()
                }
                is OwnerDetailsUiEffect.ShowSnackbar -> {
                    val result =
                        snackbarHostState.showSnackbar(
                            message = effect.message,
                            actionLabel = effect.actionLabel,
                            duration = SnackbarDuration.Long,
                        )
                    if (result == SnackbarResult.ActionPerformed && effect.actionLabel != null) {
                        viewModel.dispatchIntent(OwnerDetailsUserIntent.RetryClicked)
                    }
                }
            }
        }
    }

    // Handle system back button/gesture - block when submitting
    BackHandler(enabled = !state.isSubmitting) {
        viewModel.dispatchIntent(OwnerDetailsUserIntent.BackClicked)
    }

    ContactDetailsContent(
        state = state,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onPhoneChange = { viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdatePhone(it)) },
        onEmailChange = { viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdateEmail(it)) },
        onRewardChange = { viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdateReward(it)) },
        onBackClick = { viewModel.dispatchIntent(OwnerDetailsUserIntent.BackClicked) },
        onContinueClick = { viewModel.dispatchIntent(OwnerDetailsUserIntent.ContinueClicked) },
    )
}
