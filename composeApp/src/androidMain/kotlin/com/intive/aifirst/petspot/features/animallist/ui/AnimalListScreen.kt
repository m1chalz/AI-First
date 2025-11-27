@file:Suppress("ktlint:standard:function-naming") // Composable functions use PascalCase
@file:OptIn(ExperimentalPermissionsApi::class)

package com.intive.aifirst.petspot.features.animallist.ui

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
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
 * Extended with Accompanist Permissions for location permission handling.
 *
 * Features:
 * - Scrollable list of animal cards (LazyColumn)
 * - Loading indicator (for both data and location fetch)
 * - Error message display
 * - Empty state message
 * - "Report a Missing Animal" button (fixed at bottom)
 * - Location permission handling via Accompanist
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
    viewModel: AnimalListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Accompanist permissions state for location
    val locationPermissionState =
        rememberMultiplePermissionsState(
            permissions =
                listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
        )

    // Observe permission state changes and dispatch to ViewModel
    LaunchedEffect(locationPermissionState.allPermissionsGranted) {
        val fineGranted =
            locationPermissionState.permissions
                .find { it.permission == Manifest.permission.ACCESS_FINE_LOCATION }
                ?.status?.isGranted == true
        val coarseGranted =
            locationPermissionState.permissions
                .find { it.permission == Manifest.permission.ACCESS_COARSE_LOCATION }
                ?.status?.isGranted == true
        val shouldShowRationale = locationPermissionState.shouldShowRationale

        viewModel.dispatchIntent(
            AnimalListIntent.PermissionResult(
                granted = locationPermissionState.allPermissionsGranted || coarseGranted,
                fineLocation = fineGranted,
                coarseLocation = coarseGranted,
                shouldShowRationale = shouldShowRationale,
            ),
        )
    }

    // Handle effects
    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is AnimalListEffect.NavigateToDetails ->
                    navController.navigateToAnimalDetail(effect.animalId)

                AnimalListEffect.NavigateToReportMissing ->
                    navController.navigateToReportMissing()

                AnimalListEffect.NavigateToReportFound ->
                    navController.navigateToReportFound()

                // Location permission effects
                AnimalListEffect.CheckPermissionStatus -> {
                    // Permission state already observed via LaunchedEffect above
                }

                AnimalListEffect.RequestPermission -> {
                    locationPermissionState.launchMultiplePermissionRequest()
                }

                is AnimalListEffect.ShowRationaleDialog -> {
                    // Rationale dialogs handled in AnimalListContent (US3, US4)
                }

                AnimalListEffect.OpenSettings -> {
                    // Settings navigation handled in AnimalListContent (US3)
                }
            }
        }
    }

    AnimalListContent(
        state = state,
        onReportMissing = { viewModel.dispatchIntent(AnimalListIntent.ReportMissing) },
        onAnimalClick = { id ->
            viewModel.dispatchIntent(AnimalListIntent.SelectAnimal(id))
        },
        onRetry = { viewModel.dispatchIntent(AnimalListIntent.Refresh) },
    )
}
