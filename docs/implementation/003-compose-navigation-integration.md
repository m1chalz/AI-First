# Compose Navigation Integration Proposal

**Date**: 2024-11-20  
**Feature**: Navigation Framework Integration  
**Status**: Proposal  

## Current State Analysis

### Problems with Current Implementation

1. **Callback-Based Navigation**: Screen receives navigation callbacks as parameters
   ```kotlin
   fun AnimalListScreen(
       onNavigateToDetails: (String) -> Unit = {},
       onNavigateToReportMissing: () -> Unit = {},
       onNavigateToReportFound: () -> Unit = {}
   )
   ```
2. **No Actual Navigation**: Just `println()` statements, no real screen transitions
3. **Not Scalable**: Adding new screens requires updating multiple callback chains
4. **Testing Complexity**: Must mock all navigation callbacks in tests
5. **No Deep Linking Support**: Can't handle URLs or intents to specific screens
6. **No Back Stack Management**: Can't handle back navigation properly

### iOS Comparison

iOS uses **MVVM-C (Coordinator)** pattern:
- `AppCoordinator` manages root navigation
- `AnimalListCoordinator` handles feature-specific navigation
- ViewModel has coordinator closures (`onAnimalSelected`, `onReportMissing`)
- Clean separation between presentation and navigation logic

### Android Equivalent

For Android, **Compose Navigation** is the standard approach:
- Type-safe navigation with sealed classes
- NavHost manages the navigation graph
- NavController handles navigation actions
- Built-in back stack management
- Deep linking support
- Testable with TestNavHostController

---

## Proposed Solution

### Architecture Overview

```
┌─────────────────────────────────────────────────────┐
│                    MainActivity                      │
│                         ↓                            │
│                      App()                           │
│                         ↓                            │
│                   NavHost (root)                     │
│                         ↓                            │
│        ┌────────────────┼────────────────┐          │
│        ↓                ↓                 ↓          │
│  AnimalListScreen  AnimalDetailScreen  ReportScreen │
│        ↓                                             │
│   AnimalListViewModel                                │
│        ↓                                             │
│  Effects → NavController                             │
└─────────────────────────────────────────────────────┘
```

**Navigation Flow**:
1. User action → Intent
2. ViewModel emits Effect
3. Screen collects Effect
4. NavController navigates to destination

---

## Implementation Steps

### Step 1: Add Navigation Dependency

**File**: `gradle/libs.versions.toml`

```toml
[versions]
# ... existing versions ...
androidx-navigation = "2.9.0"

[libraries]
# ... existing libraries ...
androidx-navigation-compose = { module = "androidx.navigation:navigation-compose", version.ref = "androidx-navigation" }
androidx-navigation-testing = { module = "androidx.navigation:navigation-testing", version.ref = "androidx-navigation" }
```

**File**: `composeApp/build.gradle.kts`

```kotlin
sourceSets {
    androidMain.dependencies {
        // ... existing dependencies ...
        implementation(libs.androidx.navigation.compose)
    }
    androidUnitTest.dependencies {
        // ... existing dependencies ...
        implementation(libs.androidx.navigation.testing)
    }
}
```

---

### Step 2: Create Navigation Routes

**File**: `composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/navigation/NavRoute.kt`

```kotlin
package com.intive.aifirst.petspot.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes using Kotlin Serialization.
 * Each route represents a screen in the app.
 * 
 * Requires kotlinx-serialization plugin in build.gradle.kts.
 */
sealed interface NavRoute {
    
    /**
     * Animal List screen - primary entry point per FR-010.
     * Route: "animal_list"
     */
    @Serializable
    data object AnimalList : NavRoute
    
    /**
     * Animal Detail screen - shows details for specific animal.
     * Route: "animal_detail/{animalId}"
     * 
     * @param animalId ID of the animal to display
     */
    @Serializable
    data class AnimalDetail(val animalId: String) : NavRoute
    
    /**
     * Report Missing Animal screen - form to report missing pet.
     * Route: "report_missing"
     */
    @Serializable
    data object ReportMissing : NavRoute
    
    /**
     * Report Found Animal screen - form to report found pet.
     * Route: "report_found"
     */
    @Serializable
    data object ReportFound : NavRoute
}
```

---

### Step 3: Create Navigation Graph

**File**: `composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/navigation/NavGraph.kt`

```kotlin
package com.intive.aifirst.petspot.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.intive.aifirst.petspot.features.animallist.ui.AnimalListScreen
import com.intive.aifirst.petspot.features.animaldetail.ui.AnimalDetailScreen
import com.intive.aifirst.petspot.features.reportmissing.ui.ReportMissingScreen
import com.intive.aifirst.petspot.features.reportfound.ui.ReportFoundScreen

/**
 * Main navigation graph for the application.
 * Defines all available routes and their associated composables.
 * 
 * Uses type-safe navigation with kotlinx-serialization.
 * 
 * @param modifier Modifier for the NavHost
 * @param navController Navigation controller (defaults to rememberNavController)
 * @param startDestination Starting route (defaults to AnimalList per FR-010)
 */
@Composable
fun PetSpotNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: NavRoute = NavRoute.AnimalList
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Animal List Screen (Primary entry point)
        composable<NavRoute.AnimalList> {
            AnimalListScreen(
                navController = navController
            )
        }
        
        // Animal Detail Screen
        composable<NavRoute.AnimalDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.AnimalDetail>()
            AnimalDetailScreen(
                animalId = route.animalId,
                navController = navController
            )
        }
        
        // Report Missing Animal Screen
        composable<NavRoute.ReportMissing> {
            ReportMissingScreen(
                navController = navController
            )
        }
        
        // Report Found Animal Screen
        composable<NavRoute.ReportFound> {
            ReportFoundScreen(
                navController = navController
            )
        }
    }
}
```

---

### Step 4: Create Navigation Extensions

**File**: `composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/navigation/NavControllerExt.kt`

```kotlin
package com.intive.aifirst.petspot.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

/**
 * Extension functions for type-safe navigation with NavController.
 * Provides convenient methods to navigate to specific routes.
 */

/**
 * Navigate to Animal List screen.
 */
fun NavController.navigateToAnimalList(
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(NavRoute.AnimalList, builder)
}

/**
 * Navigate to Animal Detail screen.
 * 
 * @param animalId ID of the animal to display
 */
fun NavController.navigateToAnimalDetail(
    animalId: String,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(NavRoute.AnimalDetail(animalId), builder)
}

/**
 * Navigate to Report Missing Animal screen.
 */
fun NavController.navigateToReportMissing(
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(NavRoute.ReportMissing, builder)
}

/**
 * Navigate to Report Found Animal screen.
 */
fun NavController.navigateToReportFound(
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(NavRoute.ReportFound, builder)
}

/**
 * Navigate back to previous screen.
 * Returns true if navigation was successful.
 */
fun NavController.navigateBack(): Boolean {
    return navigateUp()
}
```

---

### Step 5: Update App.kt

**File**: `composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/App.kt`

```kotlin
package com.intive.aifirst.petspot

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.intive.aifirst.petspot.navigation.PetSpotNavGraph
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Main application composable.
 * Sets up navigation with AnimalListScreen as primary entry point per FR-010.
 */
@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        PetSpotNavGraph(navController = navController)
    }
}
```

---

### Step 6: Update AnimalListScreen

**File**: `composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListScreen.kt`

```kotlin
package com.intive.aifirst.petspot.features.animallist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListEffect
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListIntent
import com.intive.aifirst.petspot.features.animallist.presentation.viewmodels.AnimalListViewModel
import com.intive.aifirst.petspot.navigation.navigateToAnimalDetail
import com.intive.aifirst.petspot.navigation.navigateToReportFound
import com.intive.aifirst.petspot.navigation.navigateToReportMissing
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

/**
 * Main screen for displaying list of animals.
 * Follows MVI architecture with ViewModel managing state and effects.
 * 
 * Navigation handled via NavController - effects trigger navigation actions.
 * 
 * @param navController Navigation controller for managing screen transitions
 * @param viewModel ViewModel injected via Koin
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalListScreen(
    navController: NavController,
    viewModel: AnimalListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    // Collect effects and handle navigation
    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is AnimalListEffect.NavigateToDetails -> {
                    navController.navigateToAnimalDetail(effect.animalId)
                }
                is AnimalListEffect.NavigateToReportMissing -> {
                    navController.navigateToReportMissing()
                }
                is AnimalListEffect.NavigateToReportFound -> {
                    navController.navigateToReportFound()
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Missing animals list",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF2D2D2D)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFAFAFA)
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFAFAFA),
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = { viewModel.dispatchIntent(AnimalListIntent.ReportMissing) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("animalList.reportMissingButton"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2D2D2D)
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "Report a Missing Animal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        },
        containerColor = Color(0xFFFAFAFA)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("animalList.loadingIndicator"),
                        color = Color(0xFF2D2D2D)
                    )
                }
                state.error != null -> {
                    Text(
                        text = "Error: ${state.error}",
                        fontSize = 16.sp,
                        color = Color(0xFFFF0000),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp)
                            .testTag("animalList.errorMessage")
                    )
                }
                state.isEmpty -> {
                    EmptyState()
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("animalList.list"),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(56.dp))
                        }
                        
                        items(
                            items = state.animals,
                            key = { animal -> animal.id }
                        ) { animal ->
                            AnimalCard(
                                animal = animal,
                                onClick = {
                                    viewModel.dispatchIntent(AnimalListIntent.SelectAnimal(animal.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
```

---

### Step 7: Create Placeholder Screens

**File**: `composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animaldetail/ui/AnimalDetailScreen.kt`

```kotlin
package com.intive.aifirst.petspot.features.animaldetail.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.intive.aifirst.petspot.navigation.navigateBack

/**
 * Animal Detail screen placeholder.
 * Shows details for a specific animal.
 * 
 * TODO: Implement full detail view in future iteration
 * 
 * @param animalId ID of the animal to display
 * @param navController Navigation controller for back navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalDetailScreen(
    animalId: String,
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Animal Details") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateBack() },
                        modifier = Modifier.testTag("animalDetail.backButton")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("animalDetail.screen"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Animal Detail Screen\nID: $animalId\n\n(Placeholder - will be implemented in future iteration)",
                modifier = Modifier.padding(32.dp)
            )
        }
    }
}
```

**File**: `composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/ReportMissingScreen.kt`

```kotlin
package com.intive.aifirst.petspot.features.reportmissing.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.intive.aifirst.petspot.navigation.navigateBack

/**
 * Report Missing Animal screen placeholder.
 * Form for reporting a missing pet.
 * 
 * TODO: Implement full report form in future iteration
 * 
 * @param navController Navigation controller for back navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportMissingScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Missing Animal") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateBack() },
                        modifier = Modifier.testTag("reportMissing.backButton")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("reportMissing.screen"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Report Missing Animal Screen\n\n(Placeholder - will be implemented in future iteration)",
                modifier = Modifier.padding(32.dp)
            )
        }
    }
}
```

**File**: `composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportfound/ui/ReportFoundScreen.kt`

```kotlin
package com.intive.aifirst.petspot.features.reportfound.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.intive.aifirst.petspot.navigation.navigateBack

/**
 * Report Found Animal screen placeholder.
 * Form for reporting a found pet.
 * 
 * TODO: Implement full report form in future iteration
 * 
 * @param navController Navigation controller for back navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFoundScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Found Animal") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateBack() },
                        modifier = Modifier.testTag("reportFound.backButton")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("reportFound.screen"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Report Found Animal Screen\n\n(Placeholder - will be implemented in future iteration)",
                modifier = Modifier.padding(32.dp)
            )
        }
    }
}
```

---

### Step 8: Update Tests

**File**: `composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListScreenTest.kt`

```kotlin
package com.intive.aifirst.petspot.features.animallist.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.intive.aifirst.petspot.domain.repositories.FakeAnimalRepository
import com.intive.aifirst.petspot.domain.usecases.GetAnimalsUseCase
import com.intive.aifirst.petspot.features.animallist.presentation.viewmodels.AnimalListViewModel
import com.intive.aifirst.petspot.navigation.NavRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

/**
 * UI tests for AnimalListScreen with navigation.
 * Tests navigation actions and user interactions.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AnimalListScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var navController: TestNavHostController
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun clickingAnimalCard_navigatesToDetailScreen() {
        // Given - Screen with animals loaded
        val fakeRepository = FakeAnimalRepository(animalCount = 3)
        val useCase = GetAnimalsUseCase(fakeRepository)
        val viewModel = AnimalListViewModel(useCase)
        
        composeTestRule.setContent {
            AnimalListScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()
        
        // When - User clicks on first animal card
        composeTestRule
            .onNodeWithTag("animalList.item.animal-0")
            .performClick()
        
        // Then - Navigation to detail screen occurred
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        assertEquals("AnimalDetail/{animalId}", currentRoute)
    }
    
    @Test
    fun clickingReportButton_navigatesToReportMissingScreen() {
        // Given - Screen is displayed
        val fakeRepository = FakeAnimalRepository(animalCount = 3)
        val useCase = GetAnimalsUseCase(fakeRepository)
        val viewModel = AnimalListViewModel(useCase)
        
        composeTestRule.setContent {
            AnimalListScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()
        
        // When - User clicks "Report a Missing Animal" button
        composeTestRule
            .onNodeWithTag("animalList.reportMissingButton")
            .performClick()
        
        // Then - Navigation to report missing screen occurred
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        assertEquals("ReportMissing", currentRoute)
    }
}
```

---

## Migration Checklist

### Phase 1: Setup (30 minutes)
- [ ] Add navigation dependencies to `libs.versions.toml`
- [ ] Update `composeApp/build.gradle.kts`
- [ ] Add kotlinx-serialization plugin (for type-safe routes)
- [ ] Sync Gradle

### Phase 2: Navigation Infrastructure (1 hour)
- [ ] Create `NavRoute.kt` with sealed interface
- [ ] Create `NavGraph.kt` with NavHost setup
- [ ] Create `NavControllerExt.kt` with extension functions
- [ ] Update `App.kt` to use NavHost

### Phase 3: Update Existing Screen (30 minutes)
- [ ] Update `AnimalListScreen.kt` to use NavController
- [ ] Remove callback parameters
- [ ] Update effect handling to call navigation extensions
- [ ] Add missing test tags

### Phase 4: Create Placeholder Screens (45 minutes)
- [ ] Create `AnimalDetailScreen.kt`
- [ ] Create `ReportMissingScreen.kt`
- [ ] Create `ReportFoundScreen.kt`
- [ ] Add test tags to all screens

### Phase 5: Testing (1 hour)
- [ ] Update ViewModel tests (no changes needed)
- [ ] Add UI tests for navigation
- [ ] Test back navigation
- [ ] Test deep linking (optional)
- [ ] Update E2E tests

### Phase 6: Documentation (30 minutes)
- [ ] Update README with navigation explanation
- [ ] Add architecture diagram
- [ ] Document navigation patterns
- [ ] Add examples for future screens

**Total Estimated Time**: 4-5 hours

---

## Benefits

### Immediate Benefits
1. ✅ **Real Navigation**: Actual screen transitions, not just logs
2. ✅ **Back Stack Management**: Android back button works correctly
3. ✅ **Type Safety**: Compile-time route validation
4. ✅ **Testability**: Can test navigation with TestNavHostController
5. ✅ **Scalability**: Easy to add new screens

### Future Benefits
1. ✅ **Deep Linking**: Can handle URLs (e.g., `petspot://animal/123`)
2. ✅ **Navigation Arguments**: Type-safe argument passing
3. ✅ **Nested Graphs**: Can create sub-graphs for features
4. ✅ **Transitions**: Can add custom animations
5. ✅ **State Restoration**: Handles process death

---

## Comparison with iOS

| Aspect | iOS (MVVM-C) | Android (Compose Navigation) |
|--------|--------------|------------------------------|
| **Pattern** | Coordinator | NavController |
| **ViewModel Role** | Emits coordinator closures | Emits navigation effects |
| **Navigation Logic** | Coordinator methods | NavController extensions |
| **Type Safety** | Swift protocols | Kotlin sealed classes |
| **Back Stack** | UINavigationController | NavBackStackEntry |
| **Deep Linking** | URL Schemes | NavDeepLink |
| **Testing** | Mock coordinators | TestNavHostController |

**Conceptual Alignment**: Both use separation of concerns - navigation logic is separate from presentation logic. iOS uses Coordinators, Android uses NavController.

---

## Alternative Approaches (Not Recommended)

### 1. Manual Fragment Manager
- ❌ Too complex for Compose
- ❌ Requires FragmentContainerView
- ❌ Not idiomatic for Compose

### 2. Custom Coordinator Pattern (like iOS)
- ❌ Reinventing the wheel
- ❌ No library support
- ❌ Missing features (deep linking, state restoration)

### 3. Callback Parameters (Current)
- ❌ Not scalable
- ❌ Testing complexity
- ❌ No back stack management

**Recommendation**: Use Compose Navigation - it's the standard, well-tested solution.

---

## Open Questions

1. **Deep Linking**: Do we need deep linking for MVP? (Probably no, add later)
2. **Transitions**: Should we add custom animations? (Probably no, use defaults)
3. **Nested Graphs**: Should we organize by feature? (Optional, can refactor later)
4. **Bottom Navigation**: Will we add bottom tabs? (If yes, affects architecture)

---

## Next Steps

1. Review this proposal with the team
2. Get approval for navigation approach
3. Implement Phase 1-4 (core navigation)
4. Test thoroughly
5. Update E2E tests
6. Document patterns for future developers

---

**References**:
- [Compose Navigation Official Docs](https://developer.android.com/jetpack/compose/navigation)
- [Type-safe Navigation](https://developer.android.com/guide/navigation/design/type-safety)
- [Navigation Testing](https://developer.android.com/guide/navigation/navigation-testing)

