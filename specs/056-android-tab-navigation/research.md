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

### 3. Configuration Change Handling (SavedStateHandle)

**Decision**: Use `SavedStateHandle` in ViewModel + `rememberSaveable` for NavController states

**Rationale**:
- SavedStateHandle persists ViewModel state across process death and configuration changes
- `rememberNavController()` with `rememberSaveable` preserves each tab's back stack
- Compose automatically handles recomposition after configuration changes
- No manual `onSaveInstanceState` / `onRestoreInstanceState` needed
- Works seamlessly with MVI architecture (StateFlow backed by SavedStateHandle)

**Implementation Pattern**:
```kotlin
class TabNavigationViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _state = MutableStateFlow(
        savedStateHandle.get<TabNavigationUiState>("uiState") 
            ?: TabNavigationUiState.initial()
    )
    val state: StateFlow<TabNavigationUiState> = _state.asStateFlow()
    
    private fun updateState(newState: TabNavigationUiState) {
        _state.value = newState
        savedStateHandle["uiState"] = newState  // Persist on every change
    }
}

// In composable
@Composable
fun TabContent(
    visible: Boolean,
    startDestination: String,
    builder: NavGraphBuilder.() -> Unit
) {
    if (visible) {
        val navController = rememberSaveable(
            saver = NavController.saver(LocalContext.current)
        ) {
            NavController(LocalContext.current)
        }
        NavHost(navController, startDestination, builder = builder)
    }
}
```

**Alternatives Considered**:
- ViewModel only (no SavedStateHandle) → Rejected: Loses state on process death
- Manual Bundle save/restore → Rejected: More boilerplate, error-prone
- DataStore for tab state → Rejected: Overkill for in-memory state (spec explicitly says no persistence across app restarts)

---

### 4. MVI Architecture for Navigation State Management

**Decision**: Single `TabNavigationUiState` data class with sealed `TabNavigationUserIntent` and optional `TabNavigationUiEffect`

**Rationale**:
- Tab selection state is UI state (which tab is active)
- Tab switching is a user intent (SelectTab, ReTapActiveTab)
- Navigation effects (PopToRoot) are one-off events → use UiEffect
- Pure reducer function ensures predictable state transitions
- Easy to test (given state + intent → expected new state)

**Data Models**:
```kotlin
data class TabNavigationUiState(
    val selectedTab: TabDestination
) {
    companion object {
        fun initial() = TabNavigationUiState(
            selectedTab = TabDestination.HOME  // FR-015: Default to Home
        )
    }
}

sealed class TabNavigationUserIntent {
    data class SelectTab(val tab: TabDestination) : TabNavigationUserIntent()
    data class ReTapActiveTab(val tab: TabDestination) : TabNavigationUserIntent()
}

sealed class TabNavigationUiEffect {
    data class PopToRoot(val tab: TabDestination) : TabNavigationUiEffect()
}

enum class TabDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val testId: String
) {
    HOME("home", "Home", Icons.Filled.Home, "homeTab"),
    LOST_PET("lost_pet", "Lost Pet", Icons.Filled.Pets, "lostPetTab"),
    FOUND_PET("found_pet", "Found Pet", Icons.Filled.Pets, "foundPetTab"),
    CONTACT_US("contact", "Contact Us", Icons.Filled.ContactSupport, "contactTab"),
    ACCOUNT("account", "Account", Icons.Filled.AccountCircle, "accountTab")
}
```

**Reducer Logic**:
```kotlin
private fun reduce(currentState: TabNavigationUiState, intent: TabNavigationUserIntent): Pair<TabNavigationUiState, TabNavigationUiEffect?> {
    return when (intent) {
        is TabNavigationUserIntent.SelectTab -> {
            if (intent.tab == currentState.selectedTab) {
                // Re-tap on active tab → emit PopToRoot effect (if not at tab root)
                currentState to TabNavigationUiEffect.PopToRoot(intent.tab)
            } else {
                // Switch to new tab → emit NavigateToTab effect
                val newState = currentState.copy(selectedTab = intent.tab)
                newState to TabNavigationUiEffect.NavigateToTab(intent.tab)
            }
        }
        
        is TabNavigationUserIntent.ReTapActiveTab -> {
            // Explicit re-tap intent → always pop to root
            currentState to TabNavigationUiEffect.PopToRoot(intent.tab)
        }
    }
}
```

**Note**: With single NavHost approach, navigation is handled via NavController, but we still emit effects to trigger navigation from the ViewModel in a testable way.

**Alternatives Considered**:
- Single intent for both select and re-tap → Chosen: Simpler, re-tap detection in reducer
- Separate ViewModels per tab → Rejected: Over-complication, single ViewModel manages all tab state
- No UiEffect (navigation in ViewModel) → Rejected: Violates MVI principle (ViewModel shouldn't hold NavController)

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
| State Persistence | SavedStateHandle + saveState/restoreState flags | Survives configuration changes, built-in NavController support |
| State Management | MVI (StateFlow + Intent + Effect) | Predictable, testable, follows constitution |
| Back Handling | NavController automatic | Default Navigation Component behavior |
| Placeholder | Single shared composable | DRY principle, spec requirement |

**Note**: This decision was updated after research to align with existing codebase patterns and use the standard Navigation Component approach with native multi-back-stack support (Navigation 2.4.0+).

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


