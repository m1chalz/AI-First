# Research: Android Tab Navigation

**Feature**: 056-android-tab-navigation  
**Date**: December 15, 2025  
**Status**: Completed

## Overview

This document captures research findings for implementing Material Design Bottom Navigation Bar with per-tab back stacks using Jetpack Navigation Compose.

## Important Note: Type-Safe Navigation

**Existing Codebase Pattern**: This project uses **type-safe navigation with kotlinx-serialization** (not string-based routes).

Example from existing `NavGraph.kt`:
```kotlin
composable<NavRoute.AnimalList> {
    AnimalListScreen(navController = navController)
}

composable<NavRoute.AnimalDetail> { backStackEntry ->
    val route = backStackEntry.toRoute<NavRoute.AnimalDetail>()
    PetDetailsScreen(animalId = route.animalId, navController)
}
```

**For this feature**, all navigation routes will use `@Serializable` sealed interfaces/objects instead of strings. Code examples below show string-based syntax for clarity, but **implementation MUST use type-safe routes** matching the existing codebase pattern.

## Research Topics

### 1. Jetpack Navigation Compose Per-Tab Back Stack Pattern

**Decision**: Use single `NavHost` with nested navigation graphs (one per tab) leveraging Navigation Component 2.4.0+ multi-back-stack support

**Rationale**:
- Navigation Component 2.4.0+ provides native support for multiple back stacks
- Single NavHost with nested `navigation()` graphs is the standard Compose Navigation pattern
- Aligns with existing codebase architecture (current app uses single NavHost pattern)
- Simpler to maintain than multiple NavHost instances
- Each nested graph maintains its own back stack automatically
- State is properly managed by NavController's internal back stack system

**Implementation Pattern**:
```kotlin
@Composable
fun MainScaffold(viewModel: TabNavigationViewModel) {
    val uiState by viewModel.state.collectAsState()
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = { 
            BottomNavigationBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = { tab -> 
                    // Navigate to tab's start destination
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true  // Save back stack state
                        }
                        launchSingleTop = true
                        restoreState = true  // Restore back stack state
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home_tab",
            modifier = Modifier.padding(paddingValues)
        ) {
            // Home tab nested graph
            navigation(startDestination = "home", route = "home_tab") {
                composable("home") { HomeScreen(navController) }
                composable("home_details/{id}") { /* ... */ }
            }
            
            // Lost Pet tab nested graph
            navigation(startDestination = "lost_pet", route = "lost_pet_tab") {
                composable("lost_pet") { LostPetListScreen(navController) }
                composable("lost_pet_details/{id}") { /* ... */ }
            }
            
            // Found Pet tab nested graph
            navigation(startDestination = "found_pet", route = "found_pet_tab") {
                composable("found_pet") { FoundPetListScreen(navController) }
                composable("found_pet_details/{id}") { /* ... */ }
            }
            
            // Contact Us tab (single screen)
            navigation(startDestination = "contact", route = "contact_tab") {
                composable("contact") { PlaceholderScreen() }
            }
            
            // Account tab (single screen)
            navigation(startDestination = "account", route = "account_tab") {
                composable("account") { PlaceholderScreen() }
            }
        }
    }
}
```

**Key Configuration**:
```kotlin
// In bottom navigation tab selection
navController.navigate(tabRoute) {
    popUpTo(navController.graph.findStartDestination().id) {
        saveState = true   // CRITICAL: Saves tab's back stack
    }
    launchSingleTop = true  // Avoids multiple copies
    restoreState = true     // CRITICAL: Restores tab's back stack
}
```

The `saveState = true` and `restoreState = true` flags are essential for per-tab back stack preservation.

**Alternatives Considered**:
- Multiple NavHost instances (one per tab) → Rejected: More complex, not standard pattern, harder to integrate with existing codebase
- Manual back stack management with custom navigation → Rejected: Reinvents Navigation Component features, error-prone
- Single NavHost without nested graphs → Rejected: Doesn't provide per-tab back stack separation

**Key Resources**:
- [Jetpack Compose Navigation documentation](https://developer.android.com/jetpack/compose/navigation)
- [Navigation Multi-Back-Stack Support](https://developer.android.com/guide/navigation/backstack/multi-back-stacks)
- [Material Design Bottom Navigation guidance](https://m3.material.io/components/navigation-bar/overview)
- Existing codebase: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/navigation/NavGraph.kt`

---

### 2. Material Design 3 Bottom Navigation Bar Implementation

**Decision**: Use `androidx.compose.material3.NavigationBar` with `NavigationBarItem` composables

**Rationale**:
- Material 3 is the current standard (Material 2 is deprecated)
- `NavigationBar` provides out-of-box accessibility support
- Automatic active state styling (filled icon, label color)
- Supports 3-5 items (our spec has 5 tabs - perfect fit)
- Built-in ripple effects and touch feedback

**Implementation Pattern**:
```kotlin
@Composable
fun BottomNavigationBar(
    selectedTab: TabDestination,
    onTabSelected: (TabDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        TabDestination.values().forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(tab.icon, contentDescription = null) },
                label = { Text(tab.label) },
                modifier = Modifier.testTag("bottomNav.${tab.testId}")
            )
        }
    }
}
```

**Icon Selection** (from Material Icons):
- Home: `Icons.Filled.Home` / `Icons.Outlined.Home`
- Lost Pet: `Icons.Filled.Pets` (with search context) or `Icons.Filled.Search`
- Found Pet: `Icons.Filled.Pets` (with check context) or `Icons.Filled.CheckCircle`
- Contact Us: `Icons.Filled.ContactSupport`
- Account: `Icons.Filled.AccountCircle`

**Alternatives Considered**:
- Material 2 BottomNavigation → Rejected: Deprecated, use Material 3 for new features
- Custom bottom bar → Rejected: Reinvents accessibility, animations, and Material theming

---

### 3. Configuration Change Handling

**Decision**: Use `rememberNavController()` - automatic configuration change survival

**Rationale**:
- `rememberNavController()` automatically survives configuration changes (rotation, dark mode)
- Navigation Component handles back stack preservation internally
- No manual `SavedStateHandle`, `onSaveInstanceState`, or `onRestoreInstanceState` needed
- `saveState`/`restoreState` flags in navigation preserve per-tab back stacks
- Compose automatically handles recomposition after configuration changes

**Implementation Pattern**:
```kotlin
@Composable
fun MainScaffold() {
    // NavController survives configuration changes automatically
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                TabDestination.entries.forEach { tab ->
                    NavigationBarItem(
                        onClick = {
                            navController.navigate(tab.toRoute()) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true   // Preserve tab's back stack
                                }
                                launchSingleTop = true
                                restoreState = true    // Restore tab's back stack
                            }
                        },
                        // ... icon, label, etc.
                    )
                }
            }
        }
    ) {
        NavHost(navController, startDestination = TabRoute.Home) {
            // Navigation graphs - back stacks preserved automatically
        }
    }
}
```

**Alternatives Considered**:
- Manual SavedStateHandle management → Rejected: Not needed, NavController handles it
- Manual Bundle save/restore → Rejected: More boilerplate, error-prone
- DataStore for tab state → Rejected: Overkill for in-memory state (spec explicitly says no persistence across app restarts)

---

### 4. Navigation State Management (No ViewModel Needed)

**Decision**: Use NavController directly - no custom ViewModel, UiState, or MVI components

**Rationale**:
- Tab navigation state is **framework-managed** by NavController
- NavController already provides:
  - Current tab detection via `currentBackStackEntryAsState()`
  - Back stack preservation via `saveState`/`restoreState` flags
  - Configuration change survival via `rememberNavController()`
- Creating a ViewModel wrapper would be unnecessary abstraction
- Tab switching is pure UI navigation - no business logic involved
- Testing covered by E2E tests (NavController behavior tested by Google)

**Implementation Pattern**:
```kotlin
@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                TabDestination.entries.forEach { tab ->
                    NavigationBarItem(
                        // Determine selected tab from NavController state
                        selected = currentRoute?.startsWith(
                            tab.toRoute()::class.simpleName ?: ""
                        ) == true,
                        
                        // Handle tab switching inline (no ViewModel dispatch)
                        onClick = {
                            val tabRouteName = tab.toRoute()::class.simpleName ?: ""
                            if (currentRoute?.startsWith(tabRouteName) == true) {
                                // Re-tap: pop to root
                                navController.popBackStack(tab.toRoute(), inclusive = false)
                            } else {
                                // Switch tabs
                                navController.navigate(tab.toRoute()) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(tab.label) },
                        modifier = Modifier.testTag("bottomNav.${tab.testId}")
                    )
                }
            }
        }
    ) {
        NavHost(navController, startDestination = TabRoute.Home) {
            // Navigation graphs
        }
    }
}
```

**UI Configuration Model**:
```kotlin
enum class TabDestination(
    val label: String,
    val icon: ImageVector,
    val testId: String
) {
    HOME("Home", Icons.Filled.Home, "homeTab"),
    LOST_PET("Lost Pet", Icons.Filled.Pets, "lostPetTab"),
    FOUND_PET("Found Pet", Icons.Filled.Pets, "foundPetTab"),
    CONTACT_US("Contact Us", Icons.Filled.ContactSupport, "contactTab"),
    ACCOUNT("Account", Icons.Filled.AccountCircle, "accountTab");
    
    // Maps to type-safe navigation route
    fun toRoute(): TabRoute = when (this) {
        HOME -> TabRoute.Home
        LOST_PET -> TabRoute.LostPet
        FOUND_PET -> TabRoute.FoundPet
        CONTACT_US -> TabRoute.Contact
        ACCOUNT -> TabRoute.Account
    }
}
```

**When Would You Need a ViewModel**:
- ❌ Tab selection depends on business logic (e.g., fetch permissions) - NOT our case
- ❌ Tab state persists across app restarts - Spec says NO (FR-015)
- ❌ Complex tab orchestration beyond navigation - NOT our case

For this feature, NavController provides all needed functionality.

**Alternatives Considered**:
- MVI ViewModel with StateFlow/UserIntent/UiEffect → Rejected: Over-engineering for framework-managed state
- Separate ViewModels per tab → Rejected: No business logic to manage
- Manual state tracking → Rejected: NavController already provides it

---

### 5. Android Back Button Handling

**Decision**: NavController handles back button automatically with single NavHost approach

**Rationale**:
- Single NavHost with NavController automatically handles back button presses
- NavController.popBackStack() works correctly with nested navigation graphs
- No custom BackHandler needed - Navigation Component handles it
- When at tab root, back press goes to previous tab (default Navigation Component behavior)
- System handles app exit when at overall start destination

**Implementation Pattern**:
```kotlin
// No custom back handling needed - NavController handles it automatically
@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = { BottomNavigationBar(/*...*/) }
    ) {
        NavHost(navController, startDestination = "home_tab") {
            // Navigation graphs
            // Back button automatically pops within current graph
        }
    }
}
```

**Optional**: Custom back handling only if needed for specific UX requirements:
```kotlin
BackHandler(enabled = navController.previousBackStackEntry != null) {
    navController.popBackStack()
}
```

**Alternatives Considered**:
- Custom BackHandler per screen → Rejected: Not needed, NavController handles it
- Manual OnBackPressedCallback at activity level → Rejected: Compose Navigation handles it idiomatically

---

### 6. Placeholder Screen Implementation

**Decision**: Single shared stateless composable with centered "Coming soon" text

**Rationale**:
- Spec specifies single shared placeholder (FR-020)
- No dynamic content needed (static message)
- Can be reused by any tab with missing destination
- Follows DRY principle

**Implementation Pattern**:
```kotlin
@Composable
fun PlaceholderScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("placeholder.comingSoonText"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Coming soon",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
private fun PlaceholderScreenPreview() {
    MaterialTheme {
        PlaceholderScreen()
    }
}
```

**Alternatives Considered**:
- Per-tab placeholders with custom messages → Rejected: Spec says single shared placeholder
- Loading indicator → Rejected: Not a loading state, it's an unimplemented feature indicator

---

## Summary of Architectural Decisions

| Aspect | Decision | Rationale |
|--------|----------|-----------|
| Tab Navigation | Single NavHost with nested graphs | Standard Compose pattern, Navigation 2.4.0+ multi-back-stack support |
| Bottom Nav UI | Material 3 NavigationBar | Current standard, built-in accessibility |
| State Persistence | rememberNavController + saveState/restoreState flags | Automatic configuration change survival, built-in NavController support |
| State Management | Framework-managed (NavController) | No ViewModel needed - navigation is framework concern, not business logic |
| Back Handling | NavController automatic | Default Navigation Component behavior |
| Placeholder | Single shared composable | DRY principle, spec requirement |

**Note**: Simplified from original MVI design. Tab navigation state is framework-managed by NavController, eliminating need for custom ViewModel, UiState, UserIntent, and UiEffect layers. This is the idiomatic Compose Navigation approach for pure UI navigation without business logic.

---

### 7. Type-Safe Navigation Implementation (Existing Codebase Pattern)

**Decision**: Use `@Serializable` sealed interfaces with kotlinx-serialization for type-safe navigation

**Rationale**:
- Existing codebase uses type-safe navigation (`composable<NavRoute.AnimalList>`)
- Compile-time safety prevents routing errors
- Automatic argument serialization/deserialization
- No manual argument parsing required
- IDE autocomplete and refactoring support

**Implementation Pattern**:

**Step 1: Define Tab Route Types**
```kotlin
// composeApp/src/androidMain/kotlin/.../navigation/TabRoutes.kt
package com.intive.aifirst.petspot.navigation

import kotlinx.serialization.Serializable

/**
 * Top-level tab routes for bottom navigation.
 * Each tab is a sealed interface that can contain nested routes.
 */
@Serializable
sealed interface TabRoute {
    @Serializable data object Home : TabRoute
    @Serializable data object LostPet : TabRoute
    @Serializable data object FoundPet : TabRoute
    @Serializable data object Contact : TabRoute
    @Serializable data object Account : TabRoute
}

/**
 * Nested routes within Home tab.
 */
@Serializable
sealed interface HomeRoute {
    @Serializable data object Root : HomeRoute
    @Serializable data class Details(val id: String) : HomeRoute
}

/**
 * Nested routes within Lost Pet tab.
 */
@Serializable
sealed interface LostPetRoute {
    @Serializable data object List : LostPetRoute
    @Serializable data class Details(val petId: String) : LostPetRoute
    @Serializable data object Create : LostPetRoute
}

// Similar for FoundPetRoute, ContactRoute, AccountRoute
```

**Step 2: NavHost Configuration**
```kotlin
NavHost(
    navController = navController,
    startDestination = TabRoute.Home  // Type-safe object
) {
    // Home tab nested graph
    navigation<TabRoute.Home>(startDestination = HomeRoute.Root) {
        composable<HomeRoute.Root> {
            HomeScreen(navController)
        }
        composable<HomeRoute.Details> { backStackEntry ->
            val route = backStackEntry.toRoute<HomeRoute.Details>()
            HomeDetailsScreen(id = route.id, navController)
        }
    }
    
    // Lost Pet tab nested graph
    navigation<TabRoute.LostPet>(startDestination = LostPetRoute.List) {
        composable<LostPetRoute.List> {
            LostPetListScreen(navController)
        }
        composable<LostPetRoute.Details> { backStackEntry ->
            val route = backStackEntry.toRoute<LostPetRoute.Details>()
            PetDetailsScreen(petId = route.petId, navController)
        }
    }
    
    // Other tabs...
}
```

**Step 3: Type-Safe Navigation Calls**
```kotlin
// Navigate to tab
navController.navigate(TabRoute.LostPet) {
    popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}

// Navigate within tab (with arguments)
navController.navigate(LostPetRoute.Details(petId = "123"))

// Pop to tab root
navController.popBackStack<TabRoute.LostPet>(inclusive = false)
```

**Step 4: TabDestination Enum Mapping**
```kotlin
// Map UI enum to type-safe routes
enum class TabDestination {
    HOME, LOST_PET, FOUND_PET, CONTACT_US, ACCOUNT;
    
    fun toRoute(): TabRoute = when (this) {
        HOME -> TabRoute.Home
        LOST_PET -> TabRoute.LostPet
        FOUND_PET -> TabRoute.FoundPet
        CONTACT_US -> TabRoute.Contact
        ACCOUNT -> TabRoute.Account
    }
}
```

**Alternatives Considered**:
- String-based routes (`"home_tab"`) → Rejected: Not type-safe, doesn't match existing codebase pattern
- Manual argument parsing → Rejected: Error-prone, kotlinx-serialization handles it automatically

**Key Resources**:
- Existing codebase: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/navigation/NavGraph.kt`
- [Navigation Type Safety documentation](https://developer.android.com/guide/navigation/design/type-safety)
- kotlinx-serialization library (already in project)

---

## Implementation Readiness

All research topics resolved, including type-safe navigation pattern matching existing codebase. No blocking unknowns remain. Ready to proceed to Phase 1 (Design & Contracts).


