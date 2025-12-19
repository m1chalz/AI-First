package com.intive.aifirst.petspot.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.intive.aifirst.petspot.domain.models.TabDestination
import com.intive.aifirst.petspot.features.animallist.ui.AnimalListScreen
import com.intive.aifirst.petspot.features.home.ui.HomeScreen
import com.intive.aifirst.petspot.features.petdetails.ui.PetDetailsScreen
import com.intive.aifirst.petspot.features.reportmissing.ui.reportMissingNavGraph
import com.intive.aifirst.petspot.navigation.AccountRoute
import com.intive.aifirst.petspot.navigation.ContactRoute
import com.intive.aifirst.petspot.navigation.FoundPetRoute
import com.intive.aifirst.petspot.navigation.HomeRoute
import com.intive.aifirst.petspot.navigation.LostPetRoute
import com.intive.aifirst.petspot.navigation.NavRoute
import com.intive.aifirst.petspot.navigation.TabRoute
import com.intive.aifirst.petspot.ui.theme.BottomNavColors

/**
 * Main scaffold with bottom navigation bar.
 * Uses single NavHost with nested navigation graphs for per-tab back stack preservation.
 *
 * Navigation state is managed by NavController (framework-provided).
 * Tab selection uses saveState/restoreState flags for back stack preservation.
 *
 * Modal flows (ReportMissing) are at root level and hide the bottom nav bar.
 */
@Composable
fun MainScaffold(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Hide bottom nav during modal flows (ReportMissing, etc.)
    val showBottomNav = !isInModalFlow(currentRoute)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomNav) {
                NavigationBar(containerColor = BottomNavColors.BackgroundColor) {
                    val colors =
                        NavigationBarItemDefaults.colors(
                            selectedIconColor = BottomNavColors.ActiveColor,
                            selectedTextColor = BottomNavColors.ActiveColor,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = BottomNavColors.InactiveColor,
                            unselectedTextColor = BottomNavColors.InactiveColor,
                        )
                    TabDestination.entries.forEach { tab ->
                        BottomNavItem(
                            tab = tab,
                            currentRoute = currentRoute,
                            colors = colors,
                            onTabClick = { tabRoute, isCurrentTab ->
                                if (isCurrentTab) {
                                    // Re-tap on current tab: pop to tab root
                                    navController.popBackStack(
                                        route = tabRoute,
                                        inclusive = false,
                                    )
                                } else {
                                    // Switch to different tab
                                    navController.navigate(tabRoute) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        // Only apply BOTTOM padding from Scaffold (for bottom nav bar).
        // Screens handle their own top spacing (status bar, titles, etc.).
        // This prevents double padding since screens have their own layout logic.
        val bottomOnlyPadding =
            if (showBottomNav) {
                PaddingValues(bottom = paddingValues.calculateBottomPadding())
            } else {
                PaddingValues(bottom = 0.dp) // Modal flows handle everything themselves
            }

        NavHost(
            navController = navController,
            startDestination = TabRoute.Home,
            modifier = Modifier.padding(bottomOnlyPadding),
        ) {
            // =========================================
            // TAB NAVIGATION GRAPHS
            // =========================================

            // Home Tab Navigation Graph
            navigation<TabRoute.Home>(
                startDestination = HomeRoute.Root,
            ) {
                composable<HomeRoute.Root> {
                    HomeScreen(navController = navController)
                }
            }

            // Lost Pet Tab Navigation Graph
            navigation<TabRoute.LostPet>(
                startDestination = LostPetRoute.List,
            ) {
                composable<LostPetRoute.List> {
                    // Lost pet announcements list (existing implementation)
                    AnimalListScreen(navController = navController)
                }

                // Pet Details Screen (navigated from list)
                composable<NavRoute.AnimalDetail> { backStackEntry ->
                    val route = backStackEntry.toRoute<NavRoute.AnimalDetail>()
                    PetDetailsScreen(
                        animalId = route.animalId,
                        navController = navController,
                    )
                }
            }

            // Found Pet Tab Navigation Graph
            navigation<TabRoute.FoundPet>(
                startDestination = FoundPetRoute.List,
            ) {
                composable<FoundPetRoute.List> {
                    // Placeholder for now - will show found pet announcements when implemented
                    PlaceholderScreen()
                }
            }

            // Contact Us Tab Navigation Graph
            navigation<TabRoute.Contact>(
                startDestination = ContactRoute.Root,
            ) {
                composable<ContactRoute.Root> {
                    PlaceholderScreen()
                }
            }

            // Account Tab Navigation Graph
            navigation<TabRoute.Account>(
                startDestination = AccountRoute.Root,
            ) {
                composable<AccountRoute.Root> {
                    PlaceholderScreen()
                }
            }

            // =========================================
            // MODAL FLOWS (Root-level, accessible from any tab)
            // =========================================

            // Report Missing Pet Flow - can be launched from any screen
            // Bottom nav is hidden during this flow
            reportMissingNavGraph(navController)
        }
    }
}

/**
 * Bottom navigation item for a single tab.
 * Handles selection state, icon, label, and click behavior.
 *
 * @param tab Tab destination with icon, label, and route mapping
 * @param currentRoute Current navigation route for selection detection
 * @param onTabClick Callback with (tabRoute, isCurrentlySelected) when tab is clicked
 */
@Composable
private fun RowScope.BottomNavItem(
    tab: TabDestination,
    currentRoute: String?,
    onTabClick: (TabRoute, Boolean) -> Unit,
    colors: NavigationBarItemColors = NavigationBarItemDefaults.colors(),
) {
    val tabRoute = tab.toRoute()
    val tabRouteName = tabRoute::class.qualifiedName ?: ""
    val isSelected =
        currentRoute?.startsWith(tabRouteName) == true ||
            isCurrentTabSelected(currentRoute, tab)

    NavigationBarItem(
        selected = isSelected,
        onClick = { onTabClick(tabRoute, isSelected) },
        icon = {
            Icon(
                painter = painterResource(tab.iconRes),
                contentDescription = null,
            )
        },
        colors = colors,
        label = { Text(tab.label) },
        modifier =
            Modifier
                .testTag("bottomNav.${tab.testId}")
                .semantics { contentDescription = "bottomNav.${tab.testId}" },
    )
}

/**
 * Determines if the current route is a modal flow (hides bottom nav).
 */
private fun isInModalFlow(currentRoute: String?): Boolean {
    if (currentRoute == null) return false
    return currentRoute.contains("ReportMissing")
}

/**
 * Determines if the current route belongs to a specific tab.
 * Handles nested routes within each tab's navigation graph.
 */
private fun isCurrentTabSelected(
    currentRoute: String?,
    tab: TabDestination,
): Boolean {
    if (currentRoute == null) return tab == TabDestination.HOME

    return when (tab) {
        TabDestination.HOME -> currentRoute.contains("HomeRoute") || currentRoute.contains("TabRoute.Home")
        TabDestination.LOST_PET -> {
            currentRoute.contains("LostPetRoute") ||
                currentRoute.contains("TabRoute.LostPet") ||
                currentRoute.contains("AnimalDetail")
        }

        TabDestination.FOUND_PET -> currentRoute.contains("FoundPetRoute") || currentRoute.contains("TabRoute.FoundPet")
        TabDestination.CONTACT_US -> currentRoute.contains("ContactRoute") || currentRoute.contains("TabRoute.Contact")
        TabDestination.ACCOUNT -> currentRoute.contains("AccountRoute") || currentRoute.contains("TabRoute.Account")
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScaffoldPreview() {
    MaterialTheme {
        // Preview shows the scaffold structure
        // Note: Navigation won't work in preview, but layout is visible
        MainScaffold()
    }
}
