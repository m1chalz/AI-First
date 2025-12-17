package com.intive.aifirst.petspot.features.lostPetsTeaser.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi.LostPetsTeaserEffect.NavigateToLostPetsList
import com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi.LostPetsTeaserEffect.NavigateToPetDetails
import com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi.LostPetsTeaserIntent.LoadData
import com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi.LostPetsTeaserIntent.PetClicked
import com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi.LostPetsTeaserIntent.RefreshData
import com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.mvi.LostPetsTeaserIntent.ViewAllClicked
import com.intive.aifirst.petspot.features.lostPetsTeaser.presentation.viewmodels.LostPetsTeaserViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Stateful wrapper for Lost Pets Teaser.
 * Gets ViewModel via Koin, collects state, and dispatches intents.
 *
 * Self-contained component - can be embedded in any screen.
 * Parent screen handles navigation effects via callback.
 *
 * Data loads once when first displayed (per spec Data Freshness requirement).
 *
 * @param onNavigateToPetDetails Callback when user taps a pet card
 * @param onNavigateToLostPetsList Callback when user taps "View All"
 */
@Composable
fun LostPetsTeaser(
    onNavigateToPetDetails: (String) -> Unit,
    onNavigateToLostPetsList: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LostPetsTeaserViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Load data once on first composition (per spec: no auto-refresh)
    LaunchedEffect(Unit) {
        viewModel.dispatchIntent(LoadData)
    }

    // Handle effects (navigation)
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is NavigateToPetDetails -> onNavigateToPetDetails(effect.petId)
                is NavigateToLostPetsList -> onNavigateToLostPetsList()
            }
        }
    }

    LostPetsTeaserContent(
        state = state,
        onPetClicked = { petId -> viewModel.dispatchIntent(PetClicked(petId)) },
        onViewAllClicked = { viewModel.dispatchIntent(ViewAllClicked) },
        onRetryClicked = { viewModel.dispatchIntent(RefreshData) },
        modifier = modifier,
    )
}
