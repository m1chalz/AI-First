package com.intive.aifirst.petspot.features.animallist.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListEffect
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListIntent
import com.intive.aifirst.petspot.features.animallist.presentation.viewmodels.AnimalListViewModel
import com.intive.aifirst.petspot.navigation.navigateToAnimalDetail
import com.intive.aifirst.petspot.navigation.navigateToReportFound
import com.intive.aifirst.petspot.navigation.navigateToReportMissing
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

/**
 * Main screen for displaying list of animals.
 * Follows MVI architecture with ViewModel managing state and effects.
 * 
 * Features:
 * - Scrollable list of animal cards (LazyColumn)
 * - Loading indicator
 * - Error message display
 * - Empty state message
 * - "Report a Missing Animal" button (fixed at bottom)
 * - Reserved space for future search component
 * 
 * Layout per FR-010: This is the primary entry point screen.
 * Navigation handled via NavController - effects trigger navigation actions.
 * 
 * @param navController Navigation controller for managing screen transitions
 * @param viewModel ViewModel injected via Koin
 */
@Composable
fun AnimalListScreen(
    navController: NavController,
    viewModel: AnimalListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is AnimalListEffect.NavigateToDetails ->
                    navController.navigateToAnimalDetail(effect.animalId)

                AnimalListEffect.NavigateToReportMissing ->
                    navController.navigateToReportMissing()

                AnimalListEffect.NavigateToReportFound ->
                    navController.navigateToReportFound()
            }
        }
    }

    AnimalListContent(
        state = state,
        onReportMissing = { viewModel.dispatchIntent(AnimalListIntent.ReportMissing) },
        onAnimalClick = { id ->
            viewModel.dispatchIntent(AnimalListIntent.SelectAnimal(id))
        },
        onRetry = { viewModel.dispatchIntent(AnimalListIntent.Refresh) }
    )
}

