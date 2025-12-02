package com.intive.aifirst.petspot.features.reportmissing.ui.chipnumber

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ChipNumberUserIntent
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.ChipNumberViewModel

/**
 * State host composable for Chip Number screen (Step 1/4).
 * Collects state from ViewModel, handles system back, and dispatches intents.
 *
 * Navigation is handled via callbacks injected into the ViewModel,
 * not via effects - following the hybrid pattern.
 *
 * @param viewModel Screen-specific ViewModel (injected with flowState + callbacks)
 * @param modifier Modifier for the component
 */
@Composable
fun ChipNumberScreen(
    viewModel: ChipNumberViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
