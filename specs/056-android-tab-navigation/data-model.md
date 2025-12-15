# Data Model: Android Tab Navigation

**Feature**: 056-android-tab-navigation  
**Date**: December 15, 2025  
**Architecture**: MVI (Model-View-Intent) with Jetpack Compose

## Overview

This document defines the data structures for tab navigation state management following MVI architecture. All models are immutable Kotlin data classes or sealed classes.

**Important**: This project uses **type-safe navigation with kotlinx-serialization** (not string-based routes). The `TabDestination` enum is for UI state management only and maps to type-safe `TabRoute` objects for actual navigation.

## Domain Models

### TabDestination (Enum)

Represents the 5 available tabs in the bottom navigation bar. This is a **UI-only enum** used for state management. It maps to type-safe `TabRoute` objects for actual navigation.

```kotlin
package [your.package].domain.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import [your.package].navigation.TabRoute

/**
 * Enum representing the 5 tab destinations in bottom navigation.
 * Used for UI state management only - maps to type-safe TabRoute for navigation.
 */
enum class TabDestination(
    val label: String,
    val icon: ImageVector,
    val testId: String
) {
    HOME(
        label = "Home",
        icon = Icons.Filled.Home,
        testId = "homeTab"
    ),
    LOST_PET(
        label = "Lost Pet",
        icon = Icons.Filled.Pets,
        testId = "lostPetTab"
    ),
    FOUND_PET(
        label = "Found Pet",
        icon = Icons.Filled.Pets,
        testId = "foundPetTab"
    ),
    CONTACT_US(
        label = "Contact Us",
        icon = Icons.Filled.ContactSupport,
        testId = "contactTab"
    ),
    ACCOUNT(
        label = "Account",
        icon = Icons.Filled.AccountCircle,
        testId = "accountTab"
    );
    
    /**
     * Maps this UI enum to type-safe navigation route.
     * Used when triggering navigation via NavController.
     */
    fun toRoute(): TabRoute = when (this) {
        HOME -> TabRoute.Home
        LOST_PET -> TabRoute.LostPet
        FOUND_PET -> TabRoute.FoundPet
        CONTACT_US -> TabRoute.Contact
        ACCOUNT -> TabRoute.Account
    }
    
    companion object {
        /**
         * Returns the default tab shown on app startup (Home).
         */
        fun default(): TabDestination = HOME
        
        /**
         * Maps type-safe TabRoute back to UI enum.
         * Used for determining active tab from NavController state.
         */
        fun fromRoute(route: TabRoute): TabDestination = when (route) {
            TabRoute.Home -> HOME
            TabRoute.LostPet -> LOST_PET
            TabRoute.FoundPet -> FOUND_PET
            TabRoute.Contact -> CONTACT_US
            TabRoute.Account -> ACCOUNT
        }
    }
}
```

**Fields**:
- `label`: Display text shown in bottom navigation bar
- `icon`: Material Design icon (from Compose Material Icons)
- `testId`: Test identifier for E2E testing (e.g., "homeTab")

**Methods**:
- `toRoute()`: Converts UI enum to type-safe `TabRoute` for navigation
- `fromRoute()`: Converts type-safe `TabRoute` back to UI enum (for determining active tab)

**Key Design Decision**:
- **No `route: String` field**: Navigation uses type-safe `TabRoute` objects instead
- Enum is for **UI state management only** (selected tab, icons, labels)
- Type-safe navigation routes live in separate `TabRoute` sealed interface

**Lifecycle**: Immutable enum, lives for entire app session

**Validation Rules**: None (enum values are compile-time constants)

---

## Presentation Models (MVI)

### TabNavigationUiState

Immutable data class representing the current tab navigation UI state.

```kotlin
package [your.package].presentation.navigation

import [your.package].domain.models.TabDestination

/**
 * UI state for tab navigation.
 * Tracks the currently selected tab.
 */
data class TabNavigationUiState(
    val selectedTab: TabDestination
) {
    companion object {
        /**
         * Initial state with Home tab selected (per FR-015).
         */
        fun initial(): TabNavigationUiState = TabNavigationUiState(
            selectedTab = TabDestination.default()
        )
    }
}
```

**Fields**:
- `selectedTab`: Currently active tab (determines which NavHost is visible)

**State Transitions**:
- Initial state: `TabDestination.HOME` (per FR-015: always start on Home)
- On `SelectTab` intent: Update `selectedTab` to new tab
- On `ReTapActiveTab` intent: No state change (only emits effect)

**Persistence**:
- Saved in ViewModel's `SavedStateHandle` on every state update
- Survives configuration changes (rotation, dark mode)
- NOT persisted across app restarts (app always starts on Home tab)

**Testing**:
- Unit tests verify state transitions via reducer function
- Property-based tests ensure immutability

---

### TabNavigationUserIntent (Sealed Class)

Sealed class hierarchy representing all possible user interactions with tab navigation.

```kotlin
package [your.package].presentation.navigation

import [your.package].domain.models.TabDestination

/**
 * Sealed class representing user intents for tab navigation.
 */
sealed class TabNavigationUserIntent {
    
    /**
     * User taps a tab in bottom navigation bar.
     * Can be same tab (re-tap) or different tab (switch).
     */
    data class SelectTab(val tab: TabDestination) : TabNavigationUserIntent()
    
    /**
     * Explicit intent for re-tapping the currently active tab.
     * Triggers pop-to-root behavior (per FR-012).
     */
    data class ReTapActiveTab(val tab: TabDestination) : TabNavigationUserIntent()
}
```

**Intent Types**:
1. **SelectTab**: User taps any tab (new or current)
   - Payload: `tab` (which tab was tapped)
   - Behavior: If different tab → switch tabs; if same tab → emit PopToRoot effect
   
2. **ReTapActiveTab**: Explicit re-tap (optional, can be detected in SelectTab)
   - Payload: `tab` (currently active tab)
   - Behavior: Always emits PopToRoot effect (per FR-012)

**Usage Pattern**:
```kotlin
// In composable
BottomNavigationBar(
    selectedTab = uiState.selectedTab,
    onTabSelected = { tab -> 
        viewModel.dispatchIntent(TabNavigationUserIntent.SelectTab(tab))
    }
)
```

---

### TabNavigationUiEffect (Sealed Class)

Sealed class hierarchy for one-off navigation events that don't change UI state.

```kotlin
package [your.package].presentation.navigation

import [your.package].domain.models.TabDestination

/**
 * Sealed class representing one-off side effects for tab navigation.
 * Effects are consumed once and don't update UI state.
 */
sealed class TabNavigationUiEffect {
    
    /**
     * Navigate to the specified tab's root screen.
     * Triggered when user taps a different tab.
     */
    data class NavigateToTab(val tab: TabDestination) : TabNavigationUiEffect()
    
    /**
     * Pop the currently active tab's back stack to its root screen.
     * Triggered when user re-taps the currently active tab (per FR-012).
     */
    data class PopToRoot(val tab: TabDestination) : TabNavigationUiEffect()
}
```

**Effect Types**:
1. **NavigateToTab**: Navigate to tab's root screen (with back stack preservation)
   - Payload: `tab` (which tab to navigate to)
   - Trigger: Tapping a different tab
   - Behavior: NavController.navigate() with saveState/restoreState flags

2. **PopToRoot**: Pop current tab's back stack to root
   - Payload: `tab` (which tab to pop to root)
   - Trigger: Re-tapping currently active tab
   - Behavior: NavController.popBackStack() to tab root (per FR-012)

**Usage Pattern (Type-Safe)**:
```kotlin
// In composable
LaunchedEffect(Unit) {
    viewModel.effects.collect { effect ->
        when (effect) {
            is TabNavigationUiEffect.NavigateToTab -> {
                // Navigate to tab with back stack preservation (TYPE-SAFE)
                val route = effect.tab.toRoute()  // Convert enum to type-safe route
                navController.navigate(route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            
            is TabNavigationUiEffect.PopToRoot -> {
                // Pop to tab root using type-safe route
                val route = effect.tab.toRoute()
                navController.popBackStack<TabRoute>(inclusive = false)
                // Or for specific tab type:
                // when (route) {
                //     TabRoute.Home -> navController.popBackStack<TabRoute.Home>(inclusive = false)
                //     TabRoute.LostPet -> navController.popBackStack<TabRoute.LostPet>(inclusive = false)
                //     // ... etc
                // }
            }
        }
    }
}
```

**Why Effects vs State**:
- PopToRoot is a one-off action, not persistent UI state
- After popping to root, no state change is needed (tab remains selected)
- Using SharedFlow ensures effect is delivered once and doesn't replay
- **Type-safe navigation**: Uses `TabRoute` objects instead of strings

---

## Relationships

```
TabDestination (Enum)
    ↓ used by
TabNavigationUiState
    ↑ updated by
TabNavigationUserIntent → Reducer → TabNavigationUiState + TabNavigationUiEffect
    ↓ consumed by
UI Layer (Composables)
```

**Flow**:
1. User taps tab → `SelectTab` intent dispatched to ViewModel
2. ViewModel's reducer processes intent + current state
3. Reducer returns new state + optional effect
4. State emitted via `StateFlow` → UI recomposes
5. Effect emitted via `SharedFlow` → collected once, triggers navigation action

---

## Validation Rules

### TabDestination
- No validation needed (compile-time enum safety)

### TabNavigationUiState
- `selectedTab` must be one of 5 enum values (guaranteed by type system)

### TabNavigationUserIntent
- `SelectTab.tab` must be valid TabDestination (guaranteed by type system)
- `ReTapActiveTab.tab` should match current `selectedTab` (not enforced, reducer handles any case)

### TabNavigationUiEffect
- `PopToRoot.tab` must be valid TabDestination (guaranteed by type system)

---

## Testing Strategy

### Unit Tests (TabNavigationViewModelTest.kt)

**State Transition Tests**:
```kotlin
@Test
fun `given Home tab selected, when user selects Lost Pet tab, then state updates to Lost Pet`() = runTest {
    // given
    val viewModel = createViewModel()
    viewModel.state.test {
        assertEquals(TabDestination.HOME, awaitItem().selectedTab)
        
        // when
        viewModel.dispatchIntent(TabNavigationUserIntent.SelectTab(TabDestination.LOST_PET))
        
        // then
        assertEquals(TabDestination.LOST_PET, awaitItem().selectedTab)
    }
}

@Test
fun `given Home tab selected, when user re-taps Home tab, then state remains Home and PopToRoot effect emitted`() = runTest {
    // given
    val viewModel = createViewModel()
    
    // when
    viewModel.dispatchIntent(TabNavigationUserIntent.SelectTab(TabDestination.HOME))
    
    // then
    viewModel.effects.test {
        assertEquals(TabNavigationUiEffect.PopToRoot(TabDestination.HOME), awaitItem())
    }
    assertEquals(TabDestination.HOME, viewModel.state.value.selectedTab)
}
```

**Configuration Change Tests**:
```kotlin
@Test
fun `given Lost Pet tab selected, when ViewModel restored from SavedStateHandle, then state persists`() {
    // given
    val savedStateHandle = SavedStateHandle().apply {
        set("uiState", TabNavigationUiState(selectedTab = TabDestination.LOST_PET))
    }
    
    // when
    val viewModel = TabNavigationViewModel(savedStateHandle)
    
    // then
    assertEquals(TabDestination.LOST_PET, viewModel.state.value.selectedTab)
}
```

---

## File Locations

```
composeApp/src/androidMain/kotlin/.../
├── domain/models/
│   └── TabDestination.kt
└── presentation/navigation/
    ├── TabNavigationUiState.kt
    ├── TabNavigationUserIntent.kt
    └── TabNavigationUiEffect.kt
```

---

## Summary

| Model | Type | Immutable | Persisted | Purpose |
|-------|------|-----------|-----------|---------|
| TabDestination | Enum | Yes (compile-time) | N/A | Define 5 tab options |
| TabNavigationUiState | Data class | Yes | Yes (SavedStateHandle) | Track selected tab |
| TabNavigationUserIntent | Sealed class | Yes | No | User tab interactions |
| TabNavigationUiEffect | Sealed class | Yes | No | One-off navigation actions |

All models follow MVI principles:
- Immutable data structures
- Pure reducer functions (state + intent → new state + effect)
- Separation of state (persistent) and effects (one-off)
- Type-safe sealed hierarchies


