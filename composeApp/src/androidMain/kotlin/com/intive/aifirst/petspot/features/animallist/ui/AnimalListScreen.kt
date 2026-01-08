@file:OptIn(ExperimentalPermissionsApi::class)

package com.intive.aifirst.petspot.features.animallist.ui

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.intive.aifirst.petspot.core.util.openAppSettings
import com.intive.aifirst.petspot.domain.models.RationaleDialogType
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListEffect
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListIntent
import com.intive.aifirst.petspot.features.animallist.presentation.viewmodels.AnimalListViewModel
import com.intive.aifirst.petspot.features.animallist.ui.components.EducationalRationaleDialog
import com.intive.aifirst.petspot.features.animallist.ui.components.InformationalRationaleDialog
import com.intive.aifirst.petspot.navigation.navigateToAnimalDetail
import com.intive.aifirst.petspot.navigation.navigateToReportFound
import com.intive.aifirst.petspot.navigation.navigateToReportMissing
import kotlinx.coroutines.flow.drop
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
    val context = LocalContext.current

    // Dialog state management
    var showRationaleDialog by remember { mutableStateOf<RationaleDialogType?>(null) }

    // Track if initial permission check has been handled (prevents duplicate handling)
    var hasHandledInitialPermission by remember { mutableStateOf(false) }

    // Accompanist permissions state for location with callback for when user responds
    val locationPermissionState =
        rememberMultiplePermissionsState(
            permissions =
                listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
        ) { permissionsResult ->
            // This callback fires when user responds to permission dialog
            val fineResult = permissionsResult[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseResult = permissionsResult[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            val anyResult = fineResult || coarseResult

            // Per spec US2: When user responds to system dialog, just load animals (no rationale)
            // Rationale dialogs (US3/US4) are for subsequent app launches, not immediate responses
            // isFromSystemDialog = true skips the rationale dialog
            viewModel.dispatchIntent(
                AnimalListIntent.PermissionResult(
                    granted = anyResult,
                    fineLocation = fineResult,
                    coarseLocation = coarseResult,
                    shouldShowRationale = false,
                    isFromSystemDialog = true,
                ),
            )
        }

    // Helper to get current permission state
    val fineGranted =
        locationPermissionState.permissions
            .find { it.permission == Manifest.permission.ACCESS_FINE_LOCATION }
            ?.status?.isGranted == true
    val coarseGranted =
        locationPermissionState.permissions
            .find { it.permission == Manifest.permission.ACCESS_COARSE_LOCATION }
            ?.status?.isGranted == true
    val anyGranted = fineGranted || coarseGranted
    val shouldShowRationale = locationPermissionState.shouldShowRationale

    // Initial permission check - MainScaffold handles first-time permission request
    // This just dispatches the current permission status to the ViewModel
    // Uses permission state as keys to handle late initialization from Accompanist
    LaunchedEffect(anyGranted, shouldShowRationale) {
        if (hasHandledInitialPermission) return@LaunchedEffect
        hasHandledInitialPermission = true

        // Dispatch current permission status to ViewModel
        // ViewModel will show appropriate rationale dialogs if needed
        viewModel.dispatchIntent(
            AnimalListIntent.PermissionResult(
                granted = anyGranted,
                fineLocation = fineGranted,
                coarseLocation = coarseGranted,
                shouldShowRationale = shouldShowRationale,
            ),
        )
    }

    // Observe dynamic permission changes (user changed in Settings while app open)
    // Uses PermissionStateChanged intent instead of PermissionResult
    LaunchedEffect(anyGranted) {
        // Use snapshotFlow to properly detect changes after initial composition
        snapshotFlow { anyGranted }
            .drop(1) // Skip initial value
            .collect { granted ->
                viewModel.dispatchIntent(
                    AnimalListIntent.PermissionStateChanged(
                        granted = granted,
                        shouldShowRationale = locationPermissionState.shouldShowRationale,
                    ),
                )
            }
    }

    // Handle effects - use collect (not collectLatest) to ensure all effects are processed
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
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
                    showRationaleDialog = effect.type
                }

                AnimalListEffect.OpenSettings -> {
                    context.openAppSettings()
                }
            }
        }
    }

    // Show Educational Rationale Dialog (US4)
    if (showRationaleDialog is RationaleDialogType.Educational) {
        EducationalRationaleDialog(
            onContinue = {
                // Dispatch intent BEFORE dismissing dialog to avoid timing issues
                viewModel.dispatchIntent(AnimalListIntent.RationaleContinue)
                showRationaleDialog = null
            },
            onNotNow = {
                viewModel.dispatchIntent(AnimalListIntent.RationaleDismissed)
                showRationaleDialog = null
            },
        )
    }

    // Show Informational Rationale Dialog (US3)
    if (showRationaleDialog is RationaleDialogType.Informational) {
        InformationalRationaleDialog(
            onGoToSettings = {
                showRationaleDialog = null
                viewModel.dispatchIntent(AnimalListIntent.OpenSettingsRequested)
            },
            onCancel = {
                showRationaleDialog = null
                viewModel.dispatchIntent(AnimalListIntent.RationaleDismissed)
            },
        )
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
