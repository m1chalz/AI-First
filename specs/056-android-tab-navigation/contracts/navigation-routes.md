# Navigation Routes Contract

**Feature**: 056-android-tab-navigation  
**Date**: December 15, 2025  
**Type**: Navigation Architecture Contract

## Overview

This document defines the navigation route structure for the Android tab navigation system using **type-safe navigation with kotlinx-serialization** (matching existing codebase pattern). It specifies the root-level tab routes and placeholder for per-tab navigation graphs.

**Important**: All routes use `@Serializable` sealed interfaces/objects, NOT string-based routes. This matches the existing codebase pattern (`composable<NavRoute.AnimalList>`).

## Root-Level Navigation Structure

The app uses a single `NavHost` with nested navigation graphs (one per tab) inside a Scaffold with Material Design Bottom Navigation Bar.

```
MainScaffold (root)
├── Bottom Navigation Bar (fixed, always visible)
│   ├── Home Tab → navigates to TabRoute.Home graph
│   ├── Lost Pet Tab → navigates to TabRoute.LostPet graph
│   ├── Found Pet Tab → navigates to TabRoute.FoundPet graph
│   ├── Contact Us Tab → navigates to TabRoute.Contact graph
│   └── Account Tab → navigates to TabRoute.Account graph
│
└── Content Area (Single NavHost)
    └── NavHost (with nested navigation graphs, type-safe)
        ├── navigation<TabRoute.Home>(startDestination = HomeRoute.Root)
        ├── navigation<TabRoute.LostPet>(startDestination = LostPetRoute.List)
        ├── navigation<TabRoute.FoundPet>(startDestination = FoundPetRoute.List)
        ├── navigation<TabRoute.Contact>(startDestination = ContactRoute.Root)
        └── navigation<TabRoute.Account>(startDestination = AccountRoute.Root)
```

## Tab Routes (Type-Safe)

### Type Definitions

All routes are defined as `@Serializable` sealed interfaces:

```kotlin
// File: composeApp/src/androidMain/kotlin/.../navigation/TabRoutes.kt

@Serializable
sealed interface TabRoute {
    @Serializable data object Home : TabRoute
    @Serializable data object LostPet : TabRoute
    @Serializable data object FoundPet : TabRoute
    @Serializable data object Contact : TabRoute
    @Serializable data object Account : TabRoute
}

@Serializable sealed interface HomeRoute {
    @Serializable data object Root : HomeRoute
}

@Serializable sealed interface LostPetRoute {
    @Serializable data object List : LostPetRoute
}

@Serializable sealed interface FoundPetRoute {
    @Serializable data object List : FoundPetRoute
}

@Serializable sealed interface ContactRoute {
    @Serializable data object Root : ContactRoute
}

@Serializable sealed interface AccountRoute {
    @Serializable data object Root : AccountRoute
}
```

### Navigation Graph Routes (Tab-Level)

Each tab has a type-safe navigation graph route (used in `navigation<T>()` function):

| Tab | Graph Route Type | Start Destination Type | Status |
|-----|------------------|------------------------|--------|
| Home | `TabRoute.Home` | `HomeRoute.Root` | Landing page or placeholder |
| Lost Pet | `TabRoute.LostPet` | `LostPetRoute.List` | Lost pet list or placeholder |
| Found Pet | `TabRoute.FoundPet` | `FoundPetRoute.List` | Found pet list or placeholder |
| Contact Us | `TabRoute.Contact` | `ContactRoute.Root` | Placeholder ("Coming soon") - FR-005 |
| Account | `TabRoute.Account` | `AccountRoute.Root` | Placeholder ("Coming soon") - FR-006 |

### Screen Routes (Composable-Level)

Each tab's start destination screen uses type-safe routes:

| Tab | Screen Route Type | Composable | Status |
|-----|-------------------|------------|--------|
| Home | `HomeRoute.Root` | HomeScreen or PlaceholderScreen | Implementation-dependent |
| Lost Pet | `LostPetRoute.List` | LostPetListScreen or PlaceholderScreen | Implementation-dependent |
| Found Pet | `FoundPetRoute.List` | FoundPetListScreen or PlaceholderScreen | Implementation-dependent |
| Contact Us | `ContactRoute.Root` | PlaceholderScreen | Always placeholder |
| Account | `AccountRoute.Root` | PlaceholderScreen | Always placeholder |

### Route Naming Conventions

- **Type names**: PascalCase (e.g., `TabRoute`, `HomeRoute`)
- **Object names**: PascalCase (e.g., `Home`, `LostPet`, `Root`)
- **Sealed interfaces**: Group related routes together
- **Data objects**: Simple routes without parameters
- **Data classes**: Routes with parameters (e.g., `data class Details(val id: String)`)
- **Uniqueness**: Compiler-enforced uniqueness through type system
- **Stability**: Types should not change (deep linking compatibility in future)

## Per-Tab Navigation Graphs

Each tab has its own nested navigation graph within the single NavHost. Nested graphs are created using the `navigation()` function.

### Complete Navigation Structure (Type-Safe)

```kotlin
@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = { BottomNavigationBar(/*...*/) }
    ) {
        NavHost(
            navController = navController,
            startDestination = TabRoute.Home  // Type-safe default tab
        ) {
            // Home Tab Navigation Graph
            navigation<TabRoute.Home>(
                startDestination = HomeRoute.Root
            ) {
                composable<HomeRoute.Root> {
                    // Landing page or PlaceholderScreen()
                }
                // Future: composable<HomeRoute.Details> { backStackEntry ->
                //     val route = backStackEntry.toRoute<HomeRoute.Details>()
                //     HomeDetailsScreen(id = route.id)
                // }
            }
            
            // Lost Pet Tab Navigation Graph
            navigation<TabRoute.LostPet>(
                startDestination = LostPetRoute.List
            ) {
                composable<LostPetRoute.List> {
                    // Lost pet announcements list or PlaceholderScreen()
                }
                // Future: composable<LostPetRoute.Details> { backStackEntry ->
                //     val route = backStackEntry.toRoute<LostPetRoute.Details>()
                //     PetDetailsScreen(petId = route.petId)
                // }
                // Future: composable<LostPetRoute.Create> { /* ... */ }
            }
            
            // Found Pet Tab Navigation Graph
            navigation<TabRoute.FoundPet>(
                startDestination = FoundPetRoute.List
            ) {
                composable<FoundPetRoute.List> {
                    // Found pet announcements list or PlaceholderScreen()
                }
                // Future: composable<FoundPetRoute.Details> { /* ... */ }
                // Future: composable<FoundPetRoute.Create> { /* ... */ }
            }
            
            // Contact Us Tab Navigation Graph
            navigation<TabRoute.Contact>(
                startDestination = ContactRoute.Root
            ) {
                composable<ContactRoute.Root> {
                    PlaceholderScreen()  // Fixed placeholder (FR-005)
                }
            }
            
            // Account Tab Navigation Graph
            navigation<TabRoute.Account>(
                startDestination = AccountRoute.Root
            ) {
                composable<AccountRoute.Root> {
                    PlaceholderScreen()  // Fixed placeholder (FR-006)
                }
            }
        }
    }
}
```

## Placeholder Route Pattern

The shared placeholder screen is not a route itself - it's a composable reused in multiple type-safe routes.

**Usage Pattern (Type-Safe)**:
```kotlin
composable<ContactRoute.Root> {
    PlaceholderScreen()
}
```

**NOT**:
```kotlin
@Serializable data object Placeholder  // ❌ Don't create generic placeholder route
composable<Placeholder> {
    PlaceholderScreen()
}
```

**Rationale**: Each tab route should navigate to its intended destination. If that destination isn't implemented yet, the route's composable shows the placeholder. This allows seamless replacement when the real screen is implemented (change composable content, not route type).

## Navigation State Management

### Tab Selection (Type-Safe)

Tab selection is managed by bottom navigation bar with NavController using type-safe navigation:

```kotlin
BottomNavigationBar(
    selectedTab = getCurrentTab(navController),
    onTabSelected = { tab ->
        // Convert UI enum to type-safe route
        val route = when (tab) {
            TabDestination.HOME -> TabRoute.Home
            TabDestination.LOST_PET -> TabRoute.LostPet
            TabDestination.FOUND_PET -> TabRoute.FoundPet
            TabDestination.CONTACT_US -> TabRoute.Contact
            TabDestination.ACCOUNT -> TabRoute.Account
        }
        
        navController.navigate(route) {  // Type-safe route object
            // CRITICAL: These flags enable per-tab back stack preservation
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true   // Save current tab's back stack
            }
            launchSingleTop = true  // Avoid multiple copies of destination
            restoreState = true     // Restore target tab's back stack
        }
    }
)
```

**Critical Configuration**:
- `saveState = true`: Saves the current tab's entire back stack when navigating away
- `restoreState = true`: Restores the target tab's back stack when navigating to it
- `popUpTo(findStartDestination())`: Ensures clean navigation hierarchy
- `launchSingleTop = true`: Prevents duplicate destinations
- **Type-safe navigation**: Uses `TabRoute` sealed interface objects instead of strings

### Per-Tab Back Stack

Single NavController with nested graphs manages separate back stacks:

- **Switching tabs**: Navigation Component automatically saves/restores back stacks with `saveState`/`restoreState` flags
- **Returning to tab**: Back stack is restored from saved state
- **Configuration changes**: Back stacks preserved automatically by NavController
- **App restart**: All back stacks reset (app starts on Home tab with empty stacks per FR-015)

### Back Button Handling

NavController handles back button automatically with type-safe navigation:

```kotlin
// No custom back handling needed - NavController handles it
@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    
    // Back button automatically pops within current nested graph
    NavHost(navController, startDestination = TabRoute.Home) {  // Type-safe
        // ... navigation graphs
    }
}
```

**Default Back Button Behavior**:
1. If within nested graph (not at tab root) → pop one entry within graph
2. If at tab root → navigate to previous tab (Navigation Component default)
3. If at app start destination → allow system to handle (exit app)

### Re-Tap Behavior (FR-012)

When user taps currently active tab:
- At tab root: Do nothing
- Deep in tab's stack: Pop all the way back to tab root

**Implementation (Type-Safe)**:
```kotlin
// Effect emitted by ViewModel
sealed class TabNavigationUiEffect {
    data class PopToRoot(val tabRoute: TabRoute) : TabNavigationUiEffect()
}

// Consumed in UI
LaunchedEffect(Unit) {
    viewModel.effects.collect { effect ->
        when (effect) {
            is TabNavigationUiEffect.PopToRoot -> {
                // Type-safe popBackStack using route type
                navController.popBackStack<TabRoute>(inclusive = false)
                // Or specific tab route:
                when (effect.tabRoute) {
                    TabRoute.Home -> navController.popBackStack<TabRoute.Home>(inclusive = false)
                    TabRoute.LostPet -> navController.popBackStack<TabRoute.LostPet>(inclusive = false)
                    // ... other tabs
                }
            }
        }
    }
}
```

## Future Extensions

### Deep Linking (Not Implemented)

Future support for external deep links to specific tabs using type-safe routes:

**Deep Link URIs** (user-friendly):
```
petspot://home
petspot://lost_pet
petspot://found_pet
petspot://contact
petspot://account
```

**Type-Safe Route Mapping**:
```kotlin
// AndroidManifest.xml deep link will map to type-safe routes
fun String.toTabRoute(): TabRoute? = when (this) {
    "home" -> TabRoute.Home
    "lost_pet" -> TabRoute.LostPet
    "found_pet" -> TabRoute.FoundPet
    "contact" -> TabRoute.Contact
    "account" -> TabRoute.Account
    else -> null
}
```

**Current Status**: Not implemented (FR-015: app always starts on Home tab)

### Per-Tab Deep Routes (Not Implemented)

Future support for deep links within tab navigation graphs using type-safe routes:

**Deep Link URIs**:
```
petspot://lost_pet/pet/12345  // Navigate to specific pet details in Lost Pet tab
petspot://found_pet/create     // Navigate to create announcement in Found Pet tab
```

**Type-Safe Route Mapping**:
```kotlin
// Map deep link parameters to type-safe route objects
fun parseDeepLink(uri: Uri): TabRoute? {
    return when (uri.pathSegments.firstOrNull()) {
        "lost_pet" -> {
            val petId = uri.pathSegments.getOrNull(2)
            if (petId != null) LostPetRoute.Details(petId) else LostPetRoute.List
        }
        "found_pet" -> {
            when (uri.pathSegments.getOrNull(1)) {
                "create" -> FoundPetRoute.Create
                else -> FoundPetRoute.List
            }
        }
        else -> null
    }
}
```

**Current Status**: Not implemented (tab infrastructure only)

## Testing Contract

### E2E Test Identifiers

Bottom navigation bar test tags (FR-017):

| Tab | Test Tag | Usage |
|-----|----------|-------|
| Home | `bottomNav.homeTab` | Find and click home tab |
| Lost Pet | `bottomNav.lostPetTab` | Find and click lost pet tab |
| Found Pet | `bottomNav.foundPetTab` | Find and click found pet tab |
| Contact Us | `bottomNav.contactTab` | Find and click contact us tab |
| Account | `bottomNav.accountTab` | Find and click account tab |

**Java E2E Example**:
```java
// Navigate to Lost Pet tab
driver.findElement(By.xpath("//*[@content-desc='bottomNav.lostPetTab']")).click();
```

### Placeholder Screen Test Identifier

| Element | Test Tag | Usage |
|---------|----------|-------|
| Coming soon text | `placeholder.comingSoonText` | Verify placeholder is shown |

## Versioning

**Current Version**: 1.0.0  
**Status**: Initial implementation  
**Breaking Changes**: None (initial version)

## Related Documents

- [data-model.md](../data-model.md) - Tab navigation state models
- [spec.md](../spec.md) - Feature requirements
- [quickstart.md](../quickstart.md) - Developer guide


