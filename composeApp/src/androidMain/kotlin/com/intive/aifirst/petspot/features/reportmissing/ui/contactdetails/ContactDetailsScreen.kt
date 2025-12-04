package com.intive.aifirst.petspot.features.reportmissing.ui.contactdetails

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.OwnerDetailsUiEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.OwnerDetailsUserIntent
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.OwnerDetailsViewModel
import com.intive.aifirst.petspot.navigation.ReportMissingRoute

/**
 * State host composable for Contact Details screen (Step 4/4).
 * Collects state from ViewModel, handles effects, and dispatches intents.
 *
 * Following the same pattern as ChipNumberScreen and PhotoScreen (no Scaffold).
 *
 * @param viewModel OwnerDetailsViewModel for this screen (scoped to nav graph)
 * @param navController Shared NavController for navigation
 * @param modifier Modifier for the component
 */
@Composable
fun ContactDetailsScreen(
    viewModel: OwnerDetailsViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Handle one-off effects
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is OwnerDetailsUiEffect.NavigateToSummary -> {
                    navController.navigate(ReportMissingRoute.Summary) {
                        launchSingleTop = true
                    }
                }
                is OwnerDetailsUiEffect.NavigateBack -> {
                    navController.popBackStack()
                }
                is OwnerDetailsUiEffect.ShowSnackbar -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Handle system back button/gesture
    BackHandler {
        viewModel.dispatchIntent(OwnerDetailsUserIntent.BackClicked)
    }

    ContactDetailsContent(
        state = state,
        modifier = modifier,
        onPhoneChange = { viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdatePhone(it)) },
        onEmailChange = { viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdateEmail(it)) },
        onRewardChange = { viewModel.dispatchIntent(OwnerDetailsUserIntent.UpdateReward(it)) },
        onBackClick = { viewModel.dispatchIntent(OwnerDetailsUserIntent.BackClicked) },
        onContinueClick = { viewModel.dispatchIntent(OwnerDetailsUserIntent.ContinueClicked) },
    )
}
