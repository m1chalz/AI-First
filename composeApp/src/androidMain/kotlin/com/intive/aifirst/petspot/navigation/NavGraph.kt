@file:Suppress("ktlint:standard:function-naming") // Composable functions use PascalCase

package com.intive.aifirst.petspot.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.intive.aifirst.petspot.features.animallist.ui.AnimalListScreen
import com.intive.aifirst.petspot.features.petdetails.ui.PetDetailsScreen
import com.intive.aifirst.petspot.features.reportmissing.ui.reportMissingNavGraph

/**
 * Main navigation graph for the application.
 * Defines all available routes and their associated composables.
 *
 * Uses type-safe navigation with kotlinx-serialization.
 *
 * @param modifier Modifier for the NavHost
 * @param navController Navigation controller (defaults to rememberNavController)
 * @param startDestination Starting route (defaults to AnimalList per FR-010)
 */
@Composable
fun PetSpotNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: NavRoute = NavRoute.AnimalList,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        // Animal List Screen (Primary entry point per FR-010)
        composable<NavRoute.AnimalList> {
            AnimalListScreen(
                navController = navController,
            )
        }

        // Pet Details Screen
        composable<NavRoute.AnimalDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.AnimalDetail>()
            PetDetailsScreen(
                animalId = route.animalId,
                navController = navController,
            )
        }

        // Report Missing Pet Flow (Nested navigation graph)
        reportMissingNavGraph(navController)

        // TODO: Add ReportFound screen when implemented
        // composable<NavRoute.ReportFound> {
        //     ReportFoundScreen(navController = navController)
        // }
    }
}
