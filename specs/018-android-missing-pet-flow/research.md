# Research: Android Missing Pet Report Flow

**Feature Branch**: `018-android-missing-pet-flow`  
**Date**: 2025-12-01  
**Status**: Complete

## Research Tasks Resolved

### 1. Navigation Pattern for Multi-Step Wizard Flow

**Decision**: Use nested navigation graph with shared ViewModel scoped to the nav graph.

**Rationale**:
- Jetpack Navigation Component supports nested graphs via `navigation()` builder
- ViewModel can be scoped to the navigation graph using `navGraphViewModels` (or `hiltNavGraphViewModel` equivalent with Koin)
- State automatically survives configuration changes (device rotation)
- Back navigation handled automatically by the nav stack
- Consistent with existing project patterns (uses same `NavHost` infrastructure)

**Alternatives Considered**:
1. **Separate routes in main nav graph without nesting**: Rejected - harder to scope ViewModel to flow, no logical grouping
2. **Single screen with internal state machine**: Rejected - violates navigation best practices, breaks deep linking potential
3. **Multiple ViewModels with shared state object**: Rejected - adds complexity without benefit, harder to test

**Implementation**:
```kotlin
// In NavRoute.kt - add nested route sealed class
sealed interface ReportMissingRoute {
    @Serializable data object ChipNumber : ReportMissingRoute
    @Serializable data object Photo : ReportMissingRoute
    @Serializable data object Description : ReportMissingRoute
    @Serializable data object ContactDetails : ReportMissingRoute
    @Serializable data object Summary : ReportMissingRoute
}

// In NavGraph.kt - add nested navigation
navigation<NavRoute.ReportMissing>(startDestination = ReportMissingRoute.ChipNumber) {
    composable<ReportMissingRoute.ChipNumber> { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry<NavRoute.ReportMissing>()
        }
        val viewModel: ReportMissingViewModel = koinNavGraphViewModel(backStackEntry = parentEntry)
        ChipNumberScreen(viewModel = viewModel, navController = navController)
    }
    // ... other screens
}
```

### 2. ViewModel Scoping with Koin in Nested Nav Graph

**Decision**: Use `koinNavGraphViewModel()` to scope ViewModel to the navigation graph.

**Rationale**:
- Koin provides `koinNavGraphViewModel()` extension for Compose Navigation
- ViewModel instance shared across all screens in the flow
- Automatically cleared when user exits the flow (navigates back from first screen)
- No manual cleanup needed

**Implementation**:
```kotlin
// Requires: io.insert-koin:koin-androidx-compose-navigation
@Composable
fun ChipNumberScreen(
    navController: NavController,
    viewModel: ReportMissingViewModel = koinNavGraphViewModel()
) {
    // ...
}
```

**Dependency**: Already included in project via Koin Compose integration.

### 3. Progress Indicator Pattern

**Decision**: Create reusable `ProgressIndicator` composable that accepts current step and total steps.

**Rationale**:
- Encapsulates step visualization logic
- Reusable across all 4 data collection screens
- Easy to customize styling (active/inactive colors)
- Matches iOS implementation pattern for consistency

**Implementation**:
```kotlin
@Composable
fun StepProgressIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(totalSteps) { index ->
            val isActive = index < currentStep
            val isCurrent = index == currentStep - 1
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        color = when {
                            isActive || isCurrent -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = RoundedCornerShape(2.dp)
                    )
                    .testTag("reportMissing.progressIndicator.step$index")
            )
        }
    }
}
```

### 4. State Preservation Across Configuration Changes

**Decision**: ViewModel state automatically preserved via `StateFlow` + `collectAsStateWithLifecycle()`.

**Rationale**:
- MVI architecture with immutable `UiState` in `StateFlow` naturally survives rotation
- `collectAsStateWithLifecycle()` properly handles lifecycle
- No `SavedStateHandle` needed for simple form state (ViewModel survives config changes)
- Data cleared when exiting flow (ViewModel destroyed when nav graph entry is popped)

**Edge Cases Handled**:
- Device rotation: ✓ State preserved in ViewModel
- App backgrounding: ✓ State preserved while process alive
- Process death: State lost (acceptable per spec - fresh start on re-entry)

### 5. Back Navigation Handling

**Decision**: System back handled by Navigation Component; first screen exits to animal list automatically.

**Rationale**:
- Navigation Component handles back stack automatically for nested nav graphs
- From screens 2-5: `navController.popBackStack()` returns to previous screen in nested graph
- From screen 1: `navController.popBackStack()` pops entire nested graph, returning to AnimalList
- No step-aware logic needed in ViewModel - single `NavigateBack` effect works for all screens
- State cleared when ViewModel is destroyed (flow exit)
- Aligns with existing pattern (PetDetailsScreen uses same approach, no BackHandler)

**Implementation**:
```kotlin
// In StepHeader composable - back button callback
StepHeader(
    onBackClick = { viewModel.dispatchIntent(ReportMissingIntent.NavigateBack) }
)

// In ViewModel - simple effect emission (no step checking)
private fun handleNavigateBack() {
    viewModelScope.launch {
        _effects.emit(ReportMissingEffect.NavigateBack)
    }
}

// In NavGraph - effect handling
when (effect) {
    is ReportMissingEffect.NavigateBack -> navController.popBackStack()
    is ReportMissingEffect.NavigateToStep -> navController.navigate(effect.step.route)
}
```

**Note**: No `BackHandler` composable needed - system back gesture is handled correctly by Navigation Component's automatic back stack management for nested graphs.

### 6. Test Tag Naming Convention

**Decision**: Follow established `{screen}.{element}.{action}` pattern with `reportMissing` prefix.

**Examples**:
- `reportMissing.chipNumber.textField.input`
- `reportMissing.chipNumber.nextButton.click`
- `reportMissing.photo.nextButton.click`
- `reportMissing.progressIndicator.step0`
- `reportMissing.summary.submitButton.click`

**Rationale**: Consistent with existing project patterns (e.g., `petList.addButton.click`).

### 7. Reusable Components Strategy

**Decision**: Create two reusable components for consistent UI across data collection screens.

**Components**:

1. **StepHeader** - Header bar with back button, title, and progress indicator
   - Used on: Steps 1-4 (data collection screens)
   - NOT used on: Step 5 (Summary)
   - Contains: Back button (circular), centered title, StepProgressIndicator

2. **StepProgressIndicator** - Circular progress with step text
   - Visual: Partial arc showing progress + "X/4" text
   - Used by: StepHeader component
   - Can also be used standalone if needed

**Rationale**:
- Figma designs show identical header layout on screens 1-4
- Single source of truth for styling and test identifiers
- Reduces code duplication across 4 screens
- Easier to maintain and update

**Alternatives Considered**:
1. **Full StepContainer wrapper**: Rejected - over-engineering for navigation scaffolding phase
2. **Copy-paste layout per screen**: Rejected - violates DRY, harder to maintain
3. **No components (inline everything)**: Rejected - inconsistent UI, harder to test

## Summary

All technical unknowns resolved. The implementation will use:

1. **Nested navigation graph** for the 5-screen flow
2. **Shared ViewModel** scoped to the nav graph via Koin
3. **MVI architecture** with single `StateFlow<UiState>` for all screens
4. **Automatic state preservation** via ViewModel lifecycle
5. **Standard back navigation** handled by Navigation Component
6. **Two reusable components**: `StepHeader` and `StepProgressIndicator` for steps 1-4

