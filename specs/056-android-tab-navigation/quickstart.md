# Quickstart Guide: Android Tab Navigation

**Feature**: 056-android-tab-navigation  
**Date**: December 15, 2025  
**Audience**: Android developers working with PetSpot tab navigation

## Overview

This guide shows you how to work with the Android tab navigation system - adding new screens to tabs, modifying tab behavior, and testing navigation flows using standard Jetpack Compose Navigation patterns.

**⚠️ IMPORTANT**: This project uses **type-safe navigation with kotlinx-serialization** (NOT string-based routes). All code examples use `@Serializable` sealed interfaces and `composable<RouteType>` syntax, matching the existing codebase pattern.

**Architecture**: Navigation state is managed by **NavController** (framework-provided). No custom ViewModel, UiState, or MVI components - this is the standard idiomatic Compose Navigation approach.

## Prerequisites

- Android Studio with Kotlin plugin
- Jetpack Compose knowledge
- Understanding of Jetpack Navigation Component
- Familiarity with type-safe navigation (kotlinx-serialization)

## Quick Start

### Running the Tab Navigation

1. **Build and run the app**:
   ```bash
   ./gradlew :composeApp:assembleDebug
   ```

2. **Default behavior**:
   - App starts on **Home** tab (always)
   - Tap any tab to navigate
   - Tap active tab to pop to root (if deep in stack)
   - Back button navigates within current tab, then exits app

### Tab Configuration

**UI Enum** - All 5 tabs are defined in `TabDestination` enum:

```kotlin
// composeApp/src/androidMain/kotlin/.../domain/models/TabDestination.kt
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
    
    // Convert UI enum to type-safe route for navigation
    fun toRoute(): TabRoute = when (this) {
        HOME -> TabRoute.Home
        LOST_PET -> TabRoute.LostPet
        FOUND_PET -> TabRoute.FoundPet
        CONTACT_US -> TabRoute.Contact
        ACCOUNT -> TabRoute.Account
    }
}
```

**Type-Safe Routes** - Navigation uses `@Serializable` sealed interfaces:

```kotlin
// composeApp/src/androidMain/kotlin/.../navigation/TabRoutes.kt
@Serializable
sealed interface TabRoute {
    @Serializable data object Home : TabRoute
    @Serializable data object LostPet : TabRoute
    @Serializable data object FoundPet : TabRoute
    @Serializable data object Contact : TabRoute
    @Serializable data object Account : TabRoute
}

@Serializable
sealed interface LostPetRoute {
    @Serializable data object List : LostPetRoute
    @Serializable data class Details(val petId: String) : LostPetRoute
}
```

## Common Tasks

### 1. Adding a Screen to a Tab's Navigation Graph (Type-Safe)

**Scenario**: You've implemented the Lost Pet announcements list and want to add it to the Lost Pet tab.

**Steps**:

1. **Define your route type** (if not already defined):
   ```kotlin
   // composeApp/src/androidMain/kotlin/.../navigation/TabRoutes.kt
   @Serializable
   sealed interface LostPetRoute {
       @Serializable data object List : LostPetRoute
       @Serializable data class Details(val petId: String) : LostPetRoute
   }
   ```

2. **Define your screen composable**:
   ```kotlin
   // composeApp/src/androidMain/kotlin/.../ui/lostpet/LostPetListScreen.kt
   @Composable
   fun LostPetListScreen(
       onPetClick: (petId: String) -> Unit,
       modifier: Modifier = Modifier
   ) {
       // Your screen implementation
   }
   ```

3. **Add screen to Lost Pet navigation graph in MainScaffold** (TYPE-SAFE):
   ```kotlin
   // composeApp/src/androidMain/kotlin/.../ui/navigation/MainScaffold.kt
   
   // Find the Lost Pet navigation graph and update it:
   NavHost(navController, startDestination = TabRoute.Home) {  // Type-safe
       // ... other tabs
       
       navigation<TabRoute.LostPet>(  // Type-safe tab route
           startDestination = LostPetRoute.List  // Type-safe start
       ) {
           // List screen
           composable<LostPetRoute.List> {
               LostPetListScreen(  // Replace PlaceholderScreen()
                   onPetClick = { petId ->
                       // Type-safe navigation with route object
                       navController.navigate(LostPetRoute.Details(petId))
                   }
               )
           }
           
           // Detail screen with automatic argument extraction
           composable<LostPetRoute.Details> { backStackEntry ->
               val route = backStackEntry.toRoute<LostPetRoute.Details>()
               PetDetailsScreen(petId = route.petId)  // Type-safe access
           }
       }
   }
   ```

**Key Differences from String-Based Navigation**:
- No manual argument parsing with `navArgument()` - kotlinx-serialization handles it
- No string interpolation like `"lost_pet_details/$petId"` - use route objects
- Compile-time safety - typos in route names cause compiler errors
- IDE autocomplete for all routes and arguments

### 2. Adding a New Tab (Rare)

**Note**: The spec defines 5 fixed tabs. This is for future reference only.

**Steps**:

1. **Add new type-safe route**:
   ```kotlin
   // composeApp/src/androidMain/kotlin/.../navigation/TabRoutes.kt
   @Serializable
   sealed interface TabRoute {
       // existing tabs...
       @Serializable data object Settings : TabRoute
   }
   
   @Serializable
   sealed interface SettingsRoute {
       @Serializable data object Root : SettingsRoute
   }
   ```

2. **Add new tab to UI enum**:
   ```kotlin
   enum class TabDestination(
       val label: String,
       val icon: ImageVector,
       val testId: String
   ) {
       // existing tabs...
       SETTINGS("Settings", Icons.Filled.Settings, "settingsTab");
       
       fun toRoute(): TabRoute = when (this) {
           // existing mappings...
           SETTINGS -> TabRoute.Settings
       }
   }
   ```

3. **Add navigation graph in MainScaffold** (TYPE-SAFE):
   ```kotlin
   // In MainScaffold.kt NavHost block
   NavHost(navController, startDestination = TabRoute.Home) {
       // ... existing tabs
       
       // Settings Tab Navigation Graph
       navigation<TabRoute.Settings>(
           startDestination = SettingsRoute.Root
       ) {
           composable<SettingsRoute.Root> {
               SettingsScreen()
           }
       }
   }
   ```

4. **Bottom navigation updates automatically**: The `NavigationBar` in `MainScaffold` iterates over `TabDestination.entries`, so the new tab appears automatically.

### 3. Changing Tab Icons

**Scenario**: Product wants to change the Lost Pet tab icon to use a magnifying glass instead of paw print.

**Steps**:

1. **Update icon in UI enum**:
   ```kotlin
   enum class TabDestination(
       val label: String,
       val icon: ImageVector,
       val testId: String
   ) {
       LOST_PET(
           label = "Lost Pet",
           icon = Icons.Filled.Search,  // Changed from Icons.Filled.Pets
           testId = "lostPetTab"
       ),
       // ...
   }
   ```

2. **Rebuild and test**:
   ```bash
   ./gradlew :composeApp:assembleDebug
   ```

**Note**: Icon changes only affect the UI enum, not the type-safe routes. Navigation routes remain unchanged.

### 4. Understanding MainScaffold Structure

The `MainScaffold.kt` file contains all navigation logic in one place:

```kotlin
@Composable
fun MainScaffold() {
    // 1. Create NavController (survives config changes automatically)
    val navController = rememberNavController()
    
    // 2. Observe current route to determine selected tab
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                // 3. Iterate over all tabs
                TabDestination.entries.forEach { tab ->
                    NavigationBarItem(
                        // 4. Determine if this tab is selected
                        selected = currentRoute?.startsWith(
                            tab.toRoute()::class.simpleName ?: ""
                        ) == true,
                        
                        // 5. Handle tab clicks (switch or re-tap)
                        onClick = {
                            val tabRouteName = tab.toRoute()::class.simpleName ?: ""
                            if (currentRoute?.startsWith(tabRouteName) == true) {
                                // Already on this tab - pop to root
                                navController.popBackStack(tab.toRoute(), inclusive = false)
                            } else {
                                // Switch to different tab
                                navController.navigate(tab.toRoute()) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true   // Save current tab's back stack
                                    }
                                    launchSingleTop = true
                                    restoreState = true    // Restore target tab's back stack
                                }
                            }
                        },
                        
                        // 6. Display icon, label, and test tag
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(tab.label) },
                        modifier = Modifier.testTag("bottomNav.${tab.testId}")
                    )
                }
            }
        }
    ) { paddingValues ->
        // 7. Single NavHost with nested navigation graphs
        NavHost(
            navController = navController,
            startDestination = TabRoute.Home,
            modifier = Modifier.padding(paddingValues)
        ) {
            // One navigation graph per tab
            navigation<TabRoute.Home>(startDestination = HomeRoute.Root) {
                composable<HomeRoute.Root> { /* ... */ }
            }
            navigation<TabRoute.LostPet>(startDestination = LostPetRoute.List) {
                composable<LostPetRoute.List> { /* ... */ }
            }
            // ... other tabs
        }
    }
}
```

**Key Points**:
- **No ViewModel**: NavController manages all state
- **No UiState**: Current route is derived from `currentBackStackEntryAsState()`
- **No MVI ceremony**: All logic is inline and straightforward
- **Configuration changes**: Handled automatically by `rememberNavController()`
- **Back stack preservation**: Managed by `saveState`/`restoreState` flags

### 5. Testing Tab Navigation

#### Unit Tests (Minimal)

```kotlin
// composeApp/src/androidUnitTest/kotlin/.../domain/models/TabDestinationTest.kt

@Test
fun `when HOME tab toRoute called, then returns TabRoute-Home`() {
    // given
    val destination = TabDestination.HOME
    
    // when
    val route = destination.toRoute()
    
    // then
    assertEquals(TabRoute.Home, route)
}
```

**Why minimal unit tests**: NavController is framework code tested by Google. We only test our enum logic.

#### E2E Tests (Primary Coverage)

```java
// e2e-tests/java/src/test/java/.../screens/BottomNavigationScreen.java

public class BottomNavigationScreen {
    private final AppiumDriver driver;
    
    public void tapLostPetTab() {
        driver.findElement(
            By.xpath("//*[@content-desc='bottomNav.lostPetTab']")
        ).click();
    }
    
    public boolean isLostPetTabSelected() {
        WebElement tab = driver.findElement(
            By.xpath("//*[@content-desc='bottomNav.lostPetTab']")
        );
        return tab.getAttribute("selected").equals("true");
    }
}
```

### 6. Debugging Navigation Issues (Type-Safe)

#### Check Current Tab/Route

```kotlin
// In MainScaffold or any screen
val currentBackStackEntry = navController.currentBackStackEntry
Log.d("Navigation", "Current destination: ${currentBackStackEntry?.destination?.route}")
```

#### Check Back Stack

```kotlin
// View entire back stack
navController.currentBackStack.value.forEach { entry ->
    Log.d("Navigation", "Back stack entry: ${entry.destination.route}")
}
```

#### Type-Safe Route Inspection

```kotlin
// Get current route and determine which tab it belongs to
try {
    val homeRoute = navController.currentBackStackEntry?.toRoute<TabRoute.Home>()
    Log.d("Navigation", "On Home tab")
} catch (e: Exception) {
    Log.d("Navigation", "Not on Home tab")
}
```

## Best Practices

### ✅ Do

- **Use test tags** on all interactive elements (`Modifier.testTag("bottomNav.homeTab")`)
- **Follow type-safe navigation** (use route objects, not strings)
- **Keep navigation logic in MainScaffold** (centralized, easy to find)
- **Test via E2E tests** (navigation behavior is best tested integration-style)
- **Add previews** for stateless composables like PlaceholderScreen

### ❌ Don't

- **Don't persist tab state across app restarts** (spec says always start on Home)
- **Don't add animations** to tab switching (spec says instant switch per FR-018)
- **Don't hide bottom nav on scroll** (spec says always visible per FR-019)
- **Don't create a ViewModel for navigation** (NavController is sufficient)
- **Don't over-test framework code** (focus E2E tests on behavior, not NavController internals)

## Troubleshooting

### Issue: Tab doesn't switch when tapped

**Check**:
1. Is `onClick` lambda correctly calling `navController.navigate()`?
2. Are `saveState`/`restoreState` flags set correctly?
3. Is the route correctly mapped in `toRoute()`?

**Debug**:
```kotlin
onClick = {
    Log.d("Tab", "Tapped: ${tab.label}")
    val route = tab.toRoute()
    Log.d("Tab", "Navigating to: $route")
    navController.navigate(route) { /* ... */ }
}
```

### Issue: Back stack lost after configuration change

**Check**:
1. Are you using `rememberNavController()` (not creating new instance)?
2. Are `saveState`/`restoreState` flags set in navigation?

**Fix**: NavController automatically survives configuration changes when using `rememberNavController()`.

### Issue: Re-tap doesn't pop to root

**Check**:
1. Is current route detection correct?
2. Is `popBackStack()` being called with correct route?

**Debug**:
```kotlin
onClick = {
    val tabRouteName = tab.toRoute()::class.simpleName ?: ""
    val isCurrentTab = currentRoute?.startsWith(tabRouteName) == true
    Log.d("Tab", "Current tab? $isCurrentTab, Route: $currentRoute")
    
    if (isCurrentTab) {
        Log.d("Tab", "Popping to root for: ${tab.label}")
        navController.popBackStack(tab.toRoute(), inclusive = false)
    }
}
```

## Performance Notes

**Performance is NOT a concern for this project** (Principle XIV). However:
- Tab switching is instant (no animations per FR-018)
- NavHost composables are conditionally rendered (only visible content is active)
- Back stacks are preserved in memory (lightweight, no disk I/O)
- State updates are synchronous (immediate)

## Related Documentation

- [spec.md](./spec.md) - Feature requirements
- [data-model.md](./data-model.md) - TabDestination enum model
- [contracts/navigation-routes.md](./contracts/navigation-routes.md) - Navigation architecture
- [research.md](./research.md) - Technical decisions and alternatives

## Support

For questions or issues:
1. Check this quickstart guide first
2. Review spec.md for requirements
3. Check E2E tests for usage examples
4. Ask in team chat/Slack
