
package com.intive.aifirst.petspot.features.reportmissing.ui

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.ReportMissingViewModel
import com.intive.aifirst.petspot.features.reportmissing.ui.chipnumber.ChipNumberScreen
import com.intive.aifirst.petspot.features.reportmissing.ui.contactdetails.ContactDetailsScreen
import com.intive.aifirst.petspot.features.reportmissing.ui.description.DescriptionScreen
import com.intive.aifirst.petspot.features.reportmissing.ui.photo.PhotoScreen
import com.intive.aifirst.petspot.features.reportmissing.ui.summary.SummaryScreen
import com.intive.aifirst.petspot.navigation.NavRoute
import com.intive.aifirst.petspot.navigation.ReportMissingRoute
import org.koin.androidx.compose.koinViewModel

/**
 * Adds the Report Missing Pet nested navigation graph to the NavGraphBuilder.
 *
 * Uses navigation<>() for proper nested graph with shared NavController,
 * ensuring system back gestures work automatically and ViewModel is scoped
 * to the navigation graph.
 *
 * @param navController Shared NavController for navigation
 */
fun NavGraphBuilder.reportMissingNavGraph(navController: NavController) {
    navigation<NavRoute.ReportMissing>(
        startDestination = ReportMissingRoute.ChipNumber,
    ) {
        composable<ReportMissingRoute.ChipNumber> { backStackEntry ->
            val parentEntry =
                remember(backStackEntry) {
                    navController.getBackStackEntry<NavRoute.ReportMissing>()
                }
            val sharedViewModel: ReportMissingViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
            ChipNumberScreen(
                sharedViewModel = sharedViewModel,
                navController = navController,
            )
        }

        composable<ReportMissingRoute.Photo> { backStackEntry ->
            val parentEntry =
                remember(backStackEntry) {
                    navController.getBackStackEntry<NavRoute.ReportMissing>()
                }
            val viewModel: ReportMissingViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
            PhotoScreen(
                viewModel = viewModel,
                navController = navController,
            )
        }

        composable<ReportMissingRoute.Description> { backStackEntry ->
            val parentEntry =
                remember(backStackEntry) {
                    navController.getBackStackEntry<NavRoute.ReportMissing>()
                }
            val viewModel: ReportMissingViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
            DescriptionScreen(
                viewModel = viewModel,
                navController = navController,
            )
        }

        composable<ReportMissingRoute.ContactDetails> { backStackEntry ->
            val parentEntry =
                remember(backStackEntry) {
                    navController.getBackStackEntry<NavRoute.ReportMissing>()
                }
            val viewModel: ReportMissingViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
            ContactDetailsScreen(
                viewModel = viewModel,
                navController = navController,
            )
        }

        composable<ReportMissingRoute.Summary> { backStackEntry ->
            val parentEntry =
                remember(backStackEntry) {
                    navController.getBackStackEntry<NavRoute.ReportMissing>()
                }
            val viewModel: ReportMissingViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
            SummaryScreen(
                viewModel = viewModel,
                navController = navController,
            )
        }
    }
}
