# Quickstart Guide: Android Tab Navigation

**Feature**: 056-android-tab-navigation  
**Date**: December 15, 2025  
**Audience**: Android developers working with PetSpot tab navigation

## Overview

This guide shows you how to work with the Android tab navigation system - adding new screens to tabs, modifying tab behavior, and testing navigation flows.

**⚠️ IMPORTANT**: This project uses **type-safe navigation with kotlinx-serialization** (NOT string-based routes). All code examples use `@Serializable` sealed interfaces and `composable<RouteType>` syntax, matching the existing codebase pattern.

## Prerequisites

- Android Studio with Kotlin plugin
- Jetpack Compose knowledge
- Understanding of MVI architecture
- Familiarity with Jetpack Navigation Component

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

**UI Enum** - All 5 tabs are defined in `TabDestination` enum (for UI state only):

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
        // ... etc
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
       navController: NavController,  // For type-safe navigation
       modifier: Modifier = Modifier
   ) {
       // Your screen implementation
   }
   ```

3. **Add screen to Lost Pet navigation graph** (TYPE-SAFE):
   ```kotlin
   // composeApp/src/androidMain/kotlin/.../ui/navigation/MainScaffoldContent.kt
   
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
                   },
                   navController = navController
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

3. **Test the navigation**:
   ```bash
   ./gradlew :composeApp:testDebugUnitTest
   ```

### 2. Adding a New Tab (Rare - Type-Safe)

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

3. **Add navigation graph in NavHost** (TYPE-SAFE):
   ```kotlin
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

4. **Update bottom navigation** (if using manual configuration):
   The `BottomNavigationBar` composable iterates over all enum values automatically and uses `tab.toRoute()` for navigation.

### 3. Changing Tab Icons

**Scenario**: Product wants to change the Lost Pet tab icon to use a magnifying glass instead of paw print.

**Steps**:

1. **Update icon in UI enum** (routes don't change):
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

### 4. Testing Tab Navigation

#### Unit Tests (ViewModel)

```kotlin
// composeApp/src/androidUnitTest/kotlin/.../presentation/navigation/TabNavigationViewModelTest.kt

@Test
fun `when user taps Lost Pet tab from Home, then selected tab updates to Lost Pet`() = runTest {
    // given
    val viewModel = TabNavigationViewModel(SavedStateHandle())
    
    // when
    viewModel.dispatchIntent(TabNavigationUserIntent.SelectTab(TabDestination.LOST_PET))
    
    // then
    viewModel.state.test {
        assertEquals(TabDestination.LOST_PET, awaitItem().selectedTab)
    }
}
```

#### E2E Tests (Java/Cucumber)

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

### 5. Debugging Navigation Issues (Type-Safe)

#### Check Current Tab Selection

```kotlin
// In your ViewModel or composable
Log.d("Navigation", "Current tab: ${uiState.selectedTab}")
Log.d("Navigation", "Current tab route: ${uiState.selectedTab.toRoute()}")
```

#### Check NavController Back Stack (Type-Safe)

```kotlin
// In composable
val currentBackStackEntry = navController.currentBackStackEntry
val currentRoute = currentBackStackEntry?.toRoute<TabRoute>()
Log.d("Navigation", "Current route object: $currentRoute")
// Example output: TabRoute.LostPet
```

#### Verify Current Navigation Graph

```kotlin
// In MainScaffoldContent or any screen
val currentBackStackEntry = navController.currentBackStackEntry

// For type-safe navigation, check the route instance
try {
    val route = currentBackStackEntry?.toRoute<LostPetRoute.Details>()
    Log.d("Navigation", "Current route: LostPetRoute.Details(petId=${route.petId})")
} catch (e: Exception) {
    Log.d("Navigation", "Not a LostPetRoute.Details")
}

// Or check parent graph
val parentRoute = currentBackStackEntry?.destination?.parent?.route
Log.d("Navigation", "Parent graph ID: $parentRoute")
```

#### Type-Safe Route Inspection

```kotlin
// Get current route and determine which tab it belongs to
when (val route = navController.currentBackStackEntry?.toRoute<TabRoute>()) {
    TabRoute.Home -> Log.d("Navigation", "On Home tab")
    TabRoute.LostPet -> Log.d("Navigation", "On Lost Pet tab")
    TabRoute.FoundPet -> Log.d("Navigation", "On Found Pet tab")
    TabRoute.Contact -> Log.d("Navigation", "On Contact tab")
    TabRoute.Account -> Log.d("Navigation", "On Account tab")
    null -> Log.d("Navigation", "Unknown route")
}
```

## Architecture Patterns

### MVI Flow

```
User taps tab
    ↓
TabNavigationUserIntent.SelectTab
    ↓
ViewModel.dispatchIntent()
    ↓
Reducer function (pure)
    ↓
New TabNavigationUiState + Optional TabNavigationUiEffect
    ↓
StateFlow emission → UI recomposes
    ↓
SharedFlow emission → Effect consumed once
```

### Stateless Composable Pattern

**State Host** (connects ViewModel to UI):
```kotlin
@Composable
fun MainScaffold(viewModel: TabNavigationViewModel = koinViewModel()) {
    val uiState by viewModel.state.collectAsState()
    val navController = rememberNavController()
    
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is TabNavigationUiEffect.NavigateToTab -> {
                    // TYPE-SAFE: Convert UI enum to route object
                    val route = effect.tab.toRoute()
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
                is TabNavigationUiEffect.PopToRoot -> {
                    // TYPE-SAFE: Pop using route type
                    navController.popBackStack<TabRoute>(inclusive = false)
                }
            }
        }
    }
    
    MainScaffoldContent(
        uiState = uiState,
        onTabSelected = { tab -> 
            viewModel.dispatchIntent(TabNavigationUserIntent.SelectTab(tab))
        },
        navController = navController
    )
}
```

**Stateless Content** (pure UI, no ViewModel):
```kotlin
@Composable
fun MainScaffoldContent(
    uiState: TabNavigationUiState,
    onTabSelected: (TabDestination) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavigationBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = onTabSelected
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = TabRoute.Home,  // Type-safe
            modifier = Modifier.padding(paddingValues)
        ) {
            // Nested navigation graphs for each tab
            navigation(startDestination = "home", route = "home_tab") {
                composable("home") { /* ... */ }
            }
            // ... other tabs
        }
    }
}

@Preview
@Composable
private fun MainScaffoldContentPreview(
    @PreviewParameter(TabNavigationPreviewProvider::class) uiState: TabNavigationUiState
) {
    MaterialTheme {
        MainScaffoldContent(
            uiState = uiState,
            onTabSelected = {},  // No-op for preview
            navController = rememberNavController()
        )
    }
}
```

## Best Practices

### ✅ Do

- **Use test tags** on all interactive elements (`Modifier.testTag("bottomNav.homeTab")`)
- **Follow Given-When-Then** in all unit tests
- **Keep reducers pure** (no side effects, easy to test)
- **Use effects for navigation** (not direct NavController calls in ViewModel)
- **Preserve back stacks** with `rememberSaveable`
- **Add previews** for all stateless composables with `PreviewParameterProvider`

### ❌ Don't

- **Don't persist tab state across app restarts** (spec says always start on Home)
- **Don't add animations** to tab switching (spec says instant switch per FR-018)
- **Don't hide bottom nav on scroll** (spec says always visible per FR-019)
- **Don't call NavController directly** in ViewModel (use effects)
- **Don't create multiple placeholder routes** (reuse single PlaceholderScreen composable)

## Troubleshooting

### Issue: Tab doesn't switch when tapped

**Check**:
1. Is `onTabSelected` lambda connected to `viewModel.dispatchIntent`?
2. Is reducer logic correct (does it update `selectedTab`)?
3. Is StateFlow emitting new state?

**Debug**:
```kotlin
viewModel.dispatchIntent(TabNavigationUserIntent.SelectTab(tab))
Log.d("Tab", "Dispatched intent: $tab")
```

### Issue: Back stack lost after configuration change

**Check**:
1. Is NavController using `rememberNavController()`?
2. Are you using `saveState = true` and `restoreState = true` in navigation?
3. Is ViewModel using `SavedStateHandle`?

**Fix**:
```kotlin
// NavController automatically handles configuration changes
val navController = rememberNavController()

// Ensure saveState/restoreState flags are set when navigating between tabs
navController.navigate(tabRoute) {
    popUpTo(navController.graph.findStartDestination().id) {
        saveState = true  // CRITICAL
    }
    restoreState = true  // CRITICAL
    launchSingleTop = true
}
```

### Issue: Re-tap doesn't pop to root

**Check**:
1. Is `PopToRoot` effect being emitted?
2. Is effect being collected in `LaunchedEffect`?
3. Is `popBackStack` being called with correct route?

**Debug**:
```kotlin
LaunchedEffect(Unit) {
    viewModel.effects.collect { effect ->
        Log.d("Effect", "Received: $effect")
        when (effect) {
            is TabNavigationUiEffect.PopToRoot -> {
                Log.d("Navigation", "Popping to root for: ${effect.tab}")
                // ... popBackStack call
            }
        }
    }
}
```

## Performance Notes

**Performance is NOT a concern for this project** (Principle XIV). However, some implementation notes:

- Tab switching is instant (no animations per FR-018)
- NavHost composables are conditionally rendered (only visible tab is active)
- Back stacks are preserved in memory (lightweight, no disk I/O)
- State updates are synchronous (StateFlow emits immediately)

## Related Documentation

- [spec.md](./spec.md) - Feature requirements
- [data-model.md](./data-model.md) - MVI state models
- [contracts/navigation-routes.md](./contracts/navigation-routes.md) - Navigation architecture
- [research.md](./research.md) - Technical decisions and alternatives

## Support

For questions or issues:
1. Check this quickstart guide first
2. Review spec.md for requirements
3. Check unit tests for usage examples
4. Ask in team chat/Slack


