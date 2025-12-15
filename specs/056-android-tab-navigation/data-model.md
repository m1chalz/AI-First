# Data Model: Android Tab Navigation

**Feature**: 056-android-tab-navigation  
**Date**: December 15, 2025  
**Architecture**: Standard Jetpack Compose Navigation (framework-managed state)

## Overview

This document defines the data structure for tab navigation using standard Jetpack Compose Navigation patterns. Navigation state is managed by `NavController` (framework-provided), so no custom state models are needed.

**Important**: This project uses **type-safe navigation with kotlinx-serialization** (not string-based routes). The `TabDestination` enum provides UI configuration (labels, icons, test IDs) and maps to type-safe `TabRoute` objects for actual navigation.

## Domain Model

### TabDestination (Enum)

Represents the 5 available tabs in the bottom navigation bar. This enum provides UI configuration and mapping to type-safe navigation routes.

```kotlin
package com.intive.aifirst.petspot.domain.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.intive.aifirst.petspot.navigation.TabRoute

/**
 * Enum representing the 5 tab destinations in bottom navigation.
 * Provides UI configuration (label, icon, testId) and maps to type-safe routes.
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
}
```

**Fields**:
- `label`: Display text shown in bottom navigation bar (e.g., "Home", "Lost Pet")
- `icon`: Material Design icon from Compose Material Icons library
- `testId`: Test identifier for E2E testing, follows convention without "bottomNav." prefix (added in UI)

**Methods**:
- `toRoute()`: Converts UI enum to type-safe `TabRoute` for navigation operations

**Usage in UI**:
```kotlin
@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                TabDestination.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = /* determine from navController state */,
                        onClick = { navController.navigate(tab.toRoute()) },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(tab.label) },
                        modifier = Modifier.testTag("bottomNav.${tab.testId}")
                    )
                }
            }
        }
    ) { /* NavHost content */ }
}
```

**Lifecycle**: Immutable enum, exists for entire app session

**Validation Rules**: None (enum values are compile-time constants)

---

## Navigation State Management

Unlike features with business logic, tab navigation state is **managed by NavController** (framework-provided):

### Current Tab Selection

**Determined by**: `navController.currentBackStackEntryAsState()`

```kotlin
val currentBackStackEntry by navController.currentBackStackEntryAsState()
val currentRoute = currentBackStackEntry?.destination?.route

// Check if on specific tab
val isOnHomeTab = currentRoute?.startsWith(TabRoute.Home::class.simpleName ?: "") == true
```

### Tab Switching

**Managed by**: `navController.navigate()` with back stack preservation flags

```kotlin
navController.navigate(tab.toRoute()) {
    popUpTo(navController.graph.findStartDestination().id) {
        saveState = true   // Save current tab's back stack
    }
    launchSingleTop = true
    restoreState = true    // Restore target tab's back stack
}
```

### Re-tap Behavior

**Handled inline**: Check current route before navigating

```kotlin
if (currentRoute?.startsWith(tab.toRoute()::class.simpleName ?: "") == true) {
    // Already on this tab - pop to root
    navController.popBackStack(tab.toRoute(), inclusive = false)
} else {
    // Switch to different tab
    navController.navigate(tab.toRoute()) { /* ... */ }
}
```

### Configuration Changes

**Automatic preservation**: `rememberNavController()` automatically survives configuration changes (rotation, dark mode). No manual `SavedStateHandle` needed.

### Back Stack Per Tab

**Automatic management**: Navigation Component handles per-tab back stacks via `saveState`/`restoreState` flags. No custom state tracking needed.

---

## Testing Strategy

### Unit Tests

**Location**: `/composeApp/src/androidUnitTest/.../domain/models/TabDestinationTest.kt`

**Scope**: TabDestination enum only (navigation logic is framework-managed)

**Test Cases**:
```kotlin
@Test
fun `given TabDestination-HOME, when toRoute called, then returns TabRoute-Home`() {
    // given
    val destination = TabDestination.HOME
    
    // when
    val route = destination.toRoute()
    
    // then
    assertEquals(TabRoute.Home, route)
}

@Test
fun `given TabDestination entries, when ordered, then matches spec order`() {
    // given
    val entries = TabDestination.entries
    
    // when / then
    assertEquals(TabDestination.HOME, entries[0])
    assertEquals(TabDestination.LOST_PET, entries[1])
    assertEquals(TabDestination.FOUND_PET, entries[2])
    assertEquals(TabDestination.CONTACT_US, entries[3])
    assertEquals(TabDestination.ACCOUNT, entries[4])
}

@Test
fun `given all TabDestination values, when checking count, then has exactly 5 tabs`() {
    // given / when
    val count = TabDestination.entries.size
    
    // then
    assertEquals(5, count)
}
```

### Integration Tests

**Primary test coverage**: E2E tests (Java/Cucumber) verify actual navigation behavior

**Why minimal unit tests**: NavController is framework code tested by Google. Our tests focus on:
- Enum correctness (toRoute mapping, order, count)
- E2E navigation behavior (tab switching, back stack preservation, re-tap)

---

## File Location

```
composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/
└── domain/
    └── models/
        └── TabDestination.kt
```

---

## Summary

| Aspect | Implementation |
|--------|----------------|
| **State Management** | Framework-managed (NavController) |
| **UI Configuration** | TabDestination enum (label, icon, testId) |
| **Navigation Routes** | Type-safe `TabRoute` sealed interface (see contracts/navigation-routes.md) |
| **Current Tab Detection** | `navController.currentBackStackEntryAsState()` |
| **Tab Switching** | `navController.navigate()` with saveState/restoreState |
| **Back Stack Preservation** | Automatic via Navigation Component flags |
| **Configuration Changes** | Automatic via `rememberNavController()` |
| **Unit Tests** | Minimal (enum only) |
| **Integration Tests** | Comprehensive E2E coverage |

This simplified approach leverages Jetpack Navigation Component's built-in state management instead of introducing custom MVI layers for framework-managed concerns.
