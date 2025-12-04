package com.intive.aifirst.petspot.features.reportmissing.ui

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.intive.aifirst.petspot.features.reportmissing.presentation.state.ReportMissingFlowState
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.AnimalDescriptionViewModel
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.ChipNumberViewModel
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.PhotoViewModel
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.OwnerDetailsViewModel
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.ReportMissingViewModel
import com.intive.aifirst.petspot.features.reportmissing.ui.chipnumber.ChipNumberScreen
import com.intive.aifirst.petspot.features.reportmissing.ui.contactdetails.ContactDetailsScreen
import com.intive.aifirst.petspot.features.reportmissing.ui.description.DescriptionScreen
import com.intive.aifirst.petspot.features.reportmissing.ui.photo.PhotoScreen
import com.intive.aifirst.petspot.features.reportmissing.ui.summary.SummaryScreen
import com.intive.aifirst.petspot.navigation.NavRoute
import com.intive.aifirst.petspot.navigation.ReportMissingRoute
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Adds the Report Missing Pet nested navigation graph to the NavGraphBuilder.
 *
 * Architecture:
 * - ChipNumberScreen uses hybrid pattern: FlowState (observable) + callbacks (navigation)
 * - Other screens still use shared ReportMissingViewModel (to be migrated incrementally)
 *
 * @param navController Shared NavController for navigation
 */
fun NavGraphBuilder.reportMissingNavGraph(navController: NavController) {
    navigation<NavRoute.ReportMissing>(
        startDestination = ReportMissingRoute.ChipNumber,
    ) {
        composable<ReportMissingRoute.ChipNumber> { backStackEntry ->
            // Get parent entry for NavGraph-scoped state
            val parentEntry =
                remember(backStackEntry) {
                    navController.getBackStackEntry<NavRoute.ReportMissing>()
                }

            // Shared flow state (NavGraph-scoped via parent ViewModel's SavedStateHandle alternative)
            // For now, get from Koin - scoped to parent entry
            val flowState: ReportMissingFlowState =
                koinViewModel<FlowStateHolder>(
                    viewModelStoreOwner = parentEntry,
                ).flowState

            // Screen ViewModel with hybrid pattern: state holder + navigation callbacks
            val viewModel: ChipNumberViewModel =
                koinViewModel {
                    parametersOf(
                        flowState,
                        // onNavigateToPhoto
                        {
                            navController.navigate(
                                ReportMissingRoute.Photo,
                            ) { launchSingleTop = true }
                        },
                        // onExitFlow
                        { navController.popBackStack() },
                    )
                }

            ChipNumberScreen(viewModel = viewModel)
        }

        composable<ReportMissingRoute.Photo> { backStackEntry ->
            // Get parent entry for NavGraph-scoped state
            val parentEntry =
                remember(backStackEntry) {
                    navController.getBackStackEntry<NavRoute.ReportMissing>()
                }

            // Shared flow state (NavGraph-scoped)
            val flowState: ReportMissingFlowState =
                koinViewModel<FlowStateHolder>(
                    viewModelStoreOwner = parentEntry,
                ).flowState

            // Screen ViewModel with hybrid pattern: repository + state holder + navigation callbacks
            val viewModel: PhotoViewModel =
                koinViewModel {
                    parametersOf(
                        flowState,
                        // onNavigateToDescription
                        {
                            navController.navigate(
                                ReportMissingRoute.Description,
                            ) { launchSingleTop = true }
                        },
                        // onNavigateBack
                        { navController.popBackStack() },
                    )
                }

            PhotoScreen(viewModel = viewModel)
        }

        composable<ReportMissingRoute.Description> { backStackEntry ->
            // Get parent entry for NavGraph-scoped state
            val parentEntry =
                remember(backStackEntry) {
                    navController.getBackStackEntry<NavRoute.ReportMissing>()
                }

            // Shared flow state (NavGraph-scoped)
            val flowState: ReportMissingFlowState =
                koinViewModel<FlowStateHolder>(
                    viewModelStoreOwner = parentEntry,
                ).flowState

            // Screen ViewModel with hybrid pattern: FlowState + navigation callbacks
            val viewModel: AnimalDescriptionViewModel =
                koinViewModel {
                    parametersOf(
                        flowState,
                        // onNavigateToContactDetails
                        {
                            navController.navigate(
                                ReportMissingRoute.ContactDetails,
                            ) { launchSingleTop = true }
                        },
                        // onNavigateBack
                        { navController.popBackStack() },
                    )
                }

            DescriptionScreen(viewModel = viewModel)
        }

        composable<ReportMissingRoute.ContactDetails> { backStackEntry ->
            // Get parent entry for NavGraph-scoped state
            val parentEntry =
                remember(backStackEntry) {
                    navController.getBackStackEntry<NavRoute.ReportMissing>()
                }

            // Shared flow state (NavGraph-scoped)
            val flowState: ReportMissingFlowState =
                koinViewModel<FlowStateHolder>(
                    viewModelStoreOwner = parentEntry,
                ).flowState

            // Screen ViewModel with MVI pattern
            val viewModel: OwnerDetailsViewModel =
                koinViewModel {
                    parametersOf(flowState)
                }

            ContactDetailsScreen(viewModel = viewModel, navController = navController)
        }

        composable<ReportMissingRoute.Summary> { backStackEntry ->
            val parentEntry =
                remember(backStackEntry) {
                    navController.getBackStackEntry<NavRoute.ReportMissing>()
                }
            val viewModel: ReportMissingViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
            SummaryScreen(viewModel = viewModel, navController = navController)
        }
    }
}
