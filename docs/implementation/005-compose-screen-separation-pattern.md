# Compose Screen Separation Pattern

**Date**: 2024-11-20  
**Status**: Proposal  
**Pattern**: Wrapper + Content Separation

## Problem Statement

Current screen composables mix two responsibilities:
1. **State Management**: ViewModel injection, state collection, effect handling
2. **UI Rendering**: Visual layout and user interactions

This creates issues:
- ❌ Can't preview screens in Android Studio (VM injection fails)
- ❌ Harder to test UI with different states
- ❌ Tight coupling between state management and rendering
- ❌ Can't easily test all UI states (loading, error, empty, success)

## Proposed Solution

Split each screen into **two composable functions**:

### 1. **Wrapper Function** (Public API)
- Handles ViewModel injection via Koin
- Collects state using `collectAsStateWithLifecycle()`
- Handles effects (navigation, snackbars, etc.)
- Maps ViewModel intents to lambda callbacks
- **NOT previewable** (due to VM dependencies)

### 2. **Content Function** (Internal Implementation)
- Pure UI function taking `UiState` and callbacks
- Stateless and deterministic
- **Fully previewable** with mock data
- Easy to test with different states
- Can be `private` or `internal`

---

## Pattern Structure

```kotlin
// ============================================
// PUBLIC WRAPPER - State Management
// ============================================
@Composable
fun FeatureScreen(
    navController: NavController,
    viewModel: FeatureViewModel = koinViewModel()
) {
    // Collect state
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    // Handle effects (navigation, one-off events)
    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is FeatureEffect.Navigate -> { /* handle */ }
                is FeatureEffect.ShowError -> { /* handle */ }
            }
        }
    }
    
    // Delegate to content with state + callbacks
    FeatureContent(
        state = state,
        onAction = { viewModel.dispatchIntent(FeatureIntent.Action(it)) },
        onBackClick = { navController.navigateBack() }
    )
}

// ============================================
// INTERNAL CONTENT - Pure UI
// ============================================
@Composable
internal fun FeatureContent(
    state: FeatureUiState,
    onAction: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // All UI code here
    Scaffold { /* ... */ }
}

// ============================================
// PREVIEWS - All UI States
// ============================================
@Preview(name = "Success State")
@Composable
private fun FeatureContentPreview() {
    MaterialTheme {
        FeatureContent(
            state = FeatureUiState(/* success data */),
            onAction = {},
            onBackClick = {}
        )
    }
}

@Preview(name = "Loading State")
@Composable
private fun FeatureContentLoadingPreview() {
    MaterialTheme {
        FeatureContent(
            state = FeatureUiState(isLoading = true),
            onAction = {},
            onBackClick = {}
        )
    }
}

@Preview(name = "Error State")
@Composable
private fun FeatureContentErrorPreview() {
    MaterialTheme {
        FeatureContent(
            state = FeatureUiState(error = "Network error"),
            onAction = {},
            onBackClick = {}
        )
    }
}

@Preview(name = "Empty State")
@Composable
private fun FeatureContentEmptyPreview() {
    MaterialTheme {
        FeatureContent(
            state = FeatureUiState(items = emptyList()),
            onAction = {},
            onBackClick = {}
        )
    }
}
```

---

## Implementation Example: AnimalListScreen

### Current Structure (Single Function)

```kotlin
@Composable
fun AnimalListScreen(
    navController: NavController,
    viewModel: AnimalListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) { /* effects */ }
    
    Scaffold(
        topBar = { /* ... */ },
        bottomBar = { /* ... */ }
    ) { paddingValues ->
        // 150+ lines of UI code mixed with state logic
    }
}
```

**Problems**:
- Can't preview in Android Studio
- Hard to test different UI states
- 180+ lines in single function

---

### Proposed Structure (Separated)

**File**: `AnimalListScreen.kt` (updated)

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.intive.aifirst.petspot.domain.models.*
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListEffect
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListIntent
import com.intive.aifirst.petspot.features.animallist.presentation.mvi.AnimalListUiState
import com.intive.aifirst.petspot.features.animallist.presentation.viewmodels.AnimalListViewModel
import com.intive.aifirst.petspot.navigation.navigateToAnimalDetail
import com.intive.aifirst.petspot.navigation.navigateToReportFound
import com.intive.aifirst.petspot.navigation.navigateToReportMissing
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

// ============================================
// PUBLIC WRAPPER - State Management
// ============================================

/**
 * Animal List screen wrapper - handles state management and navigation.
 * 
 * This is the public API that should be used in NavGraph.
 * Injects ViewModel, collects state, and handles effects.
 * 
 * @param navController Navigation controller for screen transitions
 * @param viewModel ViewModel injected via Koin (for DI and testing)
 */
@Composable
fun AnimalListScreen(
    navController: NavController,
    viewModel: AnimalListViewModel = koinViewModel()
) {
    // Collect state from ViewModel
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    // Handle one-off effects (navigation)
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
    
    // Delegate to content with state and callbacks
    AnimalListContent(
        state = state,
        onAnimalClick = { animalId ->
            viewModel.dispatchIntent(AnimalListIntent.SelectAnimal(animalId))
        },
        onReportMissingClick = {
            viewModel.dispatchIntent(AnimalListIntent.ReportMissing)
        }
    )
}

// ============================================
// INTERNAL CONTENT - Pure UI
// ============================================

/**
 * Animal List content - pure UI rendering based on state.
 * 
 * This is the internal implementation that renders the UI.
 * It's stateless, deterministic, and fully previewable.
 * 
 * @param state Current UI state
 * @param onAnimalClick Callback when animal card is clicked
 * @param onReportMissingClick Callback when report button is clicked
 * @param modifier Optional modifier for styling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AnimalListContent(
    state: AnimalListUiState,
    onAnimalClick: (String) -> Unit,
    onReportMissingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
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
                    onClick = onReportMissingClick,
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
                                onClick = { onAnimalClick(animal.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================
// PREVIEWS - All UI States
// ============================================

@Preview(name = "Success - Animal List", showBackground = true)
@Composable
private fun AnimalListContentSuccessPreview() {
    MaterialTheme {
        AnimalListContent(
            state = AnimalListUiState(
                animals = listOf(
                    Animal(
                        id = "1",
                        species = AnimalSpecies.DOG,
                        breed = "Golden Retriever",
                        location = Location(city = "Warszawa", radiusKm = 5),
                        status = AnimalStatus.ACTIVE,
                        lastSeenDate = "20/11/2024"
                    ),
                    Animal(
                        id = "2",
                        species = AnimalSpecies.CAT,
                        breed = "Maine Coon",
                        location = Location(city = "Kraków", radiusKm = 10),
                        status = AnimalStatus.FOUND,
                        lastSeenDate = "19/11/2024"
                    ),
                    Animal(
                        id = "3",
                        species = AnimalSpecies.DOG,
                        breed = "Husky",
                        location = Location(city = "Gdańsk", radiusKm = 8),
                        status = AnimalStatus.CLOSED,
                        lastSeenDate = "18/11/2024"
                    )
                ),
                isLoading = false,
                error = null
            ),
            onAnimalClick = {},
            onReportMissingClick = {}
        )
    }
}

@Preview(name = "Loading State", showBackground = true)
@Composable
private fun AnimalListContentLoadingPreview() {
    MaterialTheme {
        AnimalListContent(
            state = AnimalListUiState(
                animals = emptyList(),
                isLoading = true,
                error = null
            ),
            onAnimalClick = {},
            onReportMissingClick = {}
        )
    }
}

@Preview(name = "Error State", showBackground = true)
@Composable
private fun AnimalListContentErrorPreview() {
    MaterialTheme {
        AnimalListContent(
            state = AnimalListUiState(
                animals = emptyList(),
                isLoading = false,
                error = "Failed to load animals. Please check your internet connection."
            ),
            onAnimalClick = {},
            onReportMissingClick = {}
        )
    }
}

@Preview(name = "Empty State", showBackground = true)
@Composable
private fun AnimalListContentEmptyPreview() {
    MaterialTheme {
        AnimalListContent(
            state = AnimalListUiState(
                animals = emptyList(),
                isLoading = false,
                error = null
            ),
            onAnimalClick = {},
            onReportMissingClick = {}
        )
    }
}
```

---

## Benefits

### 1. **Previewability** ✅
- Can preview all UI states in Android Studio
- No VM injection issues
- Fast iteration on UI changes

### 2. **Testability** ✅
```kotlin
@Test
fun `content displays loading indicator when loading`() {
    composeTestRule.setContent {
        AnimalListContent(
            state = AnimalListUiState(isLoading = true),
            onAnimalClick = {},
            onReportMissingClick = {}
        )
    }
    
    composeTestRule
        .onNodeWithTag("animalList.loadingIndicator")
        .assertIsDisplayed()
}
```

### 3. **Separation of Concerns** ✅
- Wrapper = State management
- Content = UI rendering
- Clear boundaries and responsibilities

### 4. **Reusability** ✅
- Content function can be reused if needed
- Easy to create variants (tablet, phone, etc.)

### 5. **Pure Functions** ✅
- Content is deterministic: same state = same UI
- No side effects in UI layer
- Easier to reason about

---

## Naming Conventions

### Option 1: `Screen` + `Content` (Recommended)
```kotlin
@Composable
fun FeatureScreen(navController, viewModel) { /* wrapper */ }

@Composable
internal fun FeatureContent(state, callbacks) { /* UI */ }
```

**Pros**: Clear distinction, standard pattern  
**Cons**: Slightly verbose

### Option 2: `Screen` + `ScreenImpl`
```kotlin
@Composable
fun FeatureScreen(navController, viewModel) { /* wrapper */ }

@Composable
private fun FeatureScreenImpl(state, callbacks) { /* UI */ }
```

**Pros**: Clear that it's internal implementation  
**Cons**: "Impl" suffix not idiomatic in Compose

### Option 3: `Screen` (public) + `Screen` (internal in separate file)
```kotlin
// FeatureScreen.kt
@Composable
fun FeatureScreen(navController, viewModel) { /* wrapper */ }

// FeatureScreenContent.kt
@Composable
internal fun FeatureScreen(state, callbacks) { /* UI */ }
```

**Pros**: Clean API, can use same name  
**Cons**: Requires separate files, can be confusing

**Recommendation**: Use Option 1 (`Screen` + `Content`)

---

## Guidelines for All Screens

### Wrapper Function Signature
```kotlin
@Composable
fun [Feature]Screen(
    navController: NavController,
    viewModel: [Feature]ViewModel = koinViewModel(),
    // Optional: additional parameters for deep linking
    // e.g., animalId: String? = null
)
```

**Responsibilities**:
- ✅ Inject ViewModel via Koin
- ✅ Collect state with `collectAsStateWithLifecycle()`
- ✅ Handle effects with `LaunchedEffect`
- ✅ Map intents to callbacks
- ✅ Handle navigation via NavController
- ❌ NO UI code (delegate to Content)

### Content Function Signature
```kotlin
@Composable
internal fun [Feature]Content(
    state: [Feature]UiState,
    on[Action]: (Param) -> Unit,
    on[Action2]: () -> Unit,
    modifier: Modifier = Modifier
)
```

**Responsibilities**:
- ✅ Render UI based on state
- ✅ Call callbacks on user actions
- ✅ Be stateless and deterministic
- ✅ Support testTag for E2E tests
- ❌ NO state collection
- ❌ NO ViewModel injection
- ❌ NO direct navigation calls

### Preview Functions
```kotlin
@Preview(name = "Descriptive Name", showBackground = true)
@Composable
private fun [Feature]Content[State]Preview() {
    MaterialTheme {
        [Feature]Content(
            state = [Feature]UiState(/* mock data */),
            onAction = {},
            onAction2 = {}
        )
    }
}
```

**Best Practices**:
- Create preview for EACH UI state (loading, error, empty, success)
- Use descriptive names
- Wrap in MaterialTheme
- Use `showBackground = true` for better visibility
- Mark as `private` (only for preview, not public API)

---

## Migration Checklist

For each screen:

### Step 1: Analyze Current Screen
- [ ] Identify state collection code
- [ ] Identify effect handling code
- [ ] Identify UI rendering code
- [ ] List all user actions (callbacks)

### Step 2: Create Content Function
- [ ] Copy UI code to new `[Feature]Content` function
- [ ] Replace `viewModel.dispatchIntent()` with lambda parameters
- [ ] Add all required callbacks to function signature
- [ ] Make function `internal`
- [ ] Add KDoc documentation

### Step 3: Update Wrapper Function
- [ ] Keep ViewModel injection
- [ ] Keep state collection
- [ ] Keep effect handling
- [ ] Call `[Feature]Content` with state and callbacks
- [ ] Map callbacks to `viewModel.dispatchIntent()`

### Step 4: Add Previews
- [ ] Create preview for success state
- [ ] Create preview for loading state
- [ ] Create preview for error state
- [ ] Create preview for empty state
- [ ] Test previews in Android Studio

### Step 5: Test
- [ ] Run unit tests (no changes needed)
- [ ] Test in emulator/device
- [ ] Verify navigation works
- [ ] Verify all callbacks work
- [ ] Check previews render correctly

---

## Example: Simple Screen

For simpler screens, the pattern is even more straightforward:

```kotlin
// Simple detail screen
@Composable
fun AnimalDetailScreen(
    animalId: String,
    navController: NavController,
    viewModel: AnimalDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(animalId) {
        viewModel.loadAnimal(animalId)
    }
    
    AnimalDetailContent(
        state = state,
        onBackClick = { navController.navigateBack() },
        onEditClick = { viewModel.editAnimal() }
    )
}

@Composable
internal fun AnimalDetailContent(
    state: AnimalDetailUiState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Animal Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        // UI code
    }
}

@Preview
@Composable
private fun AnimalDetailContentPreview() {
    MaterialTheme {
        AnimalDetailContent(
            state = AnimalDetailUiState.Success(/* animal data */),
            onBackClick = {},
            onEditClick = {}
        )
    }
}
```

---

## Pros & Cons

### Pros ✅
1. **Better Previews**: Can preview all UI states without VM
2. **Better Tests**: Can test content with different states easily
3. **Separation**: Clear boundaries between state and UI
4. **Pure Functions**: Content is deterministic
5. **Reusability**: Content can be reused or composed differently
6. **Standard Pattern**: Widely used in Compose community

### Cons ⚠️
1. **More Code**: Two functions instead of one
2. **Callback Boilerplate**: Need to map intents to callbacks
3. **Learning Curve**: Team needs to understand the pattern
4. **Extra Indirection**: One more layer between screen and VM

**Verdict**: The pros significantly outweigh the cons. This is a best practice in production Compose apps.

---

## Comparison with Other Patterns

### Pattern 1: Single Function (Current)
```kotlin
@Composable
fun Screen(navController, viewModel) {
    val state = viewModel.state.collectAsState()
    // 200 lines of UI code
}
```
**Pros**: Simple, fewer files  
**Cons**: Not previewable, hard to test

### Pattern 2: Wrapper + Content (Proposed)
```kotlin
@Composable
fun Screen(navController, viewModel) { /* wrapper */ }

@Composable
internal fun ScreenContent(state, callbacks) { /* UI */ }
```
**Pros**: Previewable, testable, clean  
**Cons**: Slightly more boilerplate

### Pattern 3: ViewModel as Parameter (Alternative)
```kotlin
@Composable
fun Screen(
    navController,
    state: State<UiState>,
    onAction: () -> Unit
) {
    // UI code
}
```
**Pros**: Testable, no wrapper needed  
**Cons**: Leaks VM concerns to call site, not idiomatic

**Recommendation**: Pattern 2 (Wrapper + Content) is the best balance.

---

## References

- [Compose State Management Best Practices](https://developer.android.com/jetpack/compose/state)
- [Testing Compose](https://developer.android.com/jetpack/compose/testing)
- [Compose Previews](https://developer.android.com/jetpack/compose/tooling/previews)

---

## Next Steps

1. Review this proposal with the team
2. Apply pattern to AnimalListScreen (reference implementation)
3. Create guidelines document for future screens
4. Apply pattern to new screens as they're created
5. Migrate existing screens gradually (not urgent)

