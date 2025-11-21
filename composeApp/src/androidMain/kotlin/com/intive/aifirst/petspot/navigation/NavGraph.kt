package com.intive.aifirst.petspot.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.intive.aifirst.petspot.features.animallist.ui.AnimalListScreen

/**
 * Main navigation graph for the application.
 * Defines all available routes and their associated composables.
 *
 * Uses type-safe navigation with kotlinx-serialization.
 *
 * Note: Additional screens (AnimalDetail, ReportMissing, ReportFound) will be added
 * when those features are implemented. For now, navigation effects for those screens
 * will be logged but won't navigate anywhere.
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

        // TODO: Add AnimalDetail screen when implemented
        // composable<NavRoute.AnimalDetail> { backStackEntry ->
        //     val route = backStackEntry.toRoute<NavRoute.AnimalDetail>()
        //     AnimalDetailScreen(
        //         animalId = route.animalId,
        //         navController = navController
        //     )
        // }

        // TODO: Add ReportMissing screen when implemented
        // composable<NavRoute.ReportMissing> {
        //     ReportMissingScreen(navController = navController)
        // }

        // TODO: Add ReportFound screen when implemented
        // composable<NavRoute.ReportFound> {
        //     ReportFoundScreen(navController = navController)
        // }
    }
}
