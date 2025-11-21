package com.intive.aifirst.petspot.navigation

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

// Extension functions for type-safe navigation with NavController.
// Provides convenient methods to navigate to specific routes.
//
// These extensions wrap the NavController.navigate() calls with type-safe routes,
// making navigation more explicit and preventing typos in route strings.

/**
 * Navigate to Animal List screen.
 *
 * @param builder Optional navigation options (e.g., popUpTo, launchSingleTop)
 */
fun NavController.navigateToAnimalList(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(NavRoute.AnimalList, builder)
}

/**
 * Navigate to Animal Detail screen.
 *
 * Note: This will log a warning until AnimalDetailScreen is implemented.
 * The navigation will be added to NavGraph when the screen is ready.
 *
 * @param animalId ID of the animal to display
 * @param builder Optional navigation options
 */
fun NavController.navigateToAnimalDetail(
    animalId: String,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    Log.w("Navigation", "navigateToAnimalDetail($animalId) called but AnimalDetailScreen not yet implemented")
    // TODO: Uncomment when AnimalDetailScreen is added to NavGraph
    // navigate(NavRoute.AnimalDetail(animalId), builder)
}

/**
 * Navigate to Report Missing Animal screen.
 *
 * Note: This will log a warning until ReportMissingScreen is implemented.
 * The navigation will be added to NavGraph when the screen is ready.
 *
 * @param builder Optional navigation options
 */
fun NavController.navigateToReportMissing(builder: NavOptionsBuilder.() -> Unit = {}) {
    Log.w("Navigation", "navigateToReportMissing() called but ReportMissingScreen not yet implemented")
    // TODO: Uncomment when ReportMissingScreen is added to NavGraph
    // navigate(NavRoute.ReportMissing, builder)
}

/**
 * Navigate to Report Found Animal screen.
 *
 * Note: This will log a warning until ReportFoundScreen is implemented.
 * The navigation will be added to NavGraph when the screen is ready.
 *
 * @param builder Optional navigation options
 */
fun NavController.navigateToReportFound(builder: NavOptionsBuilder.() -> Unit = {}) {
    Log.w("Navigation", "navigateToReportFound() called but ReportFoundScreen not yet implemented")
    // TODO: Uncomment when ReportFoundScreen is added to NavGraph
    // navigate(NavRoute.ReportFound, builder)
}

/**
 * Navigate back to previous screen.
 *
 * @return true if navigation was successful, false if no previous screen exists
 */
fun NavController.navigateBack(): Boolean {
    return navigateUp()
}
