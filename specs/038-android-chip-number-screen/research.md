# Research: Android Microchip Number Screen

**Feature Branch**: `038-android-chip-number-screen`  
**Date**: 2025-12-02  
**Phase**: Phase 0 - Research

## Research Tasks

### 1. VisualTransformation for Microchip Number Formatting

**Decision**: Use Compose `VisualTransformation` to display formatted microchip numbers (00000-00000-00000) while storing raw digits in state.

**Rationale**:
- Native Compose approach - no third-party libraries needed
- Separates display formatting from stored data
- Handles cursor positioning automatically via `OffsetMapping`
- Consistent with Material Design TextField patterns

**Alternatives Considered**:
- Manual formatting in `onValueChange`: Rejected - requires complex cursor management
- Custom TextField: Rejected - unnecessary complexity for standard formatting
- Format on blur only: Rejected - spec requires real-time formatting as user types

**Implementation Pattern**:
```kotlin
class MicrochipVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val formatted = formatWithHyphens(text.text)
        return TransformedText(
            AnnotatedString(formatted),
            MicrochipOffsetMapping(text.text.length)
        )
    }
}
```

### 2. NavGraph-Scoped ViewModel for Flow State

**Decision**: Use Koin's `koinNavViewModel()` or Navigation Compose's `hiltViewModel()` pattern adapted for Koin to scope ViewModel to nested navigation graph.

**Rationale**:
- Flow state persists across all 4 screens in Report Missing Pet flow
- Automatically cleared when navigation graph is popped
- Matches iOS pattern of coordinator-owned flow state
- Infrastructure provided by spec 018

**Alternatives Considered**:
- Activity-scoped ViewModel: Rejected - persists too long, doesn't clear on flow exit
- SavedStateHandle: Rejected - adds persistence complexity not required by spec
- Singleton state holder: Rejected - manual lifecycle management needed

**Integration with Spec 018**:
- Spec 018 provides `ReportMissingPetFlowState` class
- Spec 018 provides nested NavGraph setup
- This screen consumes the shared state via Koin injection

### 3. Numeric Keyboard Input Filtering

**Decision**: Use `KeyboardOptions(keyboardType = KeyboardType.Number)` combined with input filtering in `onValueChange`.

**Rationale**:
- Numeric keyboard prevents most non-digit input at source
- Filter function catches edge cases (paste, accessibility input)
- Simple implementation with `.filter { it.isDigit() }`

**Alternatives Considered**:
- KeyboardType.NumberPassword: Rejected - hides input which is undesirable
- Custom IME: Rejected - massive overkill for this use case
- Regex validation only: Rejected - doesn't show numeric keyboard

**Implementation Pattern**:
```kotlin
OutlinedTextField(
    value = state.chipNumber,
    onValueChange = { newValue ->
        val digitsOnly = newValue.filter { it.isDigit() }.take(15)
        onChipNumberChange(digitsOnly)
    },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    visualTransformation = MicrochipVisualTransformation()
)
```

### 4. Back Navigation Behavior

**Decision**: TopAppBar back button and system back gesture both exit the entire flow via `UiEffect.NavigateBack`.

**Rationale**:
- Spec FR-017/FR-018 require both to have same behavior
- Exits entire flow (not just this screen) from step 1
- Navigation handled via effect, not direct NavController manipulation
- Consistent with MVI architecture

**Alternatives Considered**:
- Different behavior for system vs UI back: Rejected - confusing UX
- Confirmation dialog: Rejected - not in spec requirements
- Save draft on back: Rejected - spec says "close without saving"

**Implementation Pattern**:
```kotlin
// In ViewModel
fun handleIntent(intent: ChipNumberUserIntent) {
    when (intent) {
        is ChipNumberUserIntent.BackClicked -> {
            viewModelScope.launch { _effects.emit(ChipNumberUiEffect.NavigateBack) }
        }
        // ...
    }
}

// In Screen, handle system back
BackHandler { viewModel.handleIntent(ChipNumberUserIntent.BackClicked) }
```

### 5. MVI State Management Pattern

**Decision**: Single immutable `ChipNumberUiState` data class with `StateFlow`, sealed `UserIntent`, and `SharedFlow<UiEffect>`.

**Rationale**:
- Mandated by constitution (Principle X)
- Single source of truth for UI state
- Pure state transitions (no side effects in reducers)
- Effects for navigation and one-off events

**State Design**:
```kotlin
data class ChipNumberUiState(
    val chipNumber: String = "",  // Raw digits only, no hyphens
    val isLoading: Boolean = false
) {
    companion object {
        val Initial = ChipNumberUiState()
    }
}

sealed interface ChipNumberUserIntent {
    data class UpdateChipNumber(val value: String) : ChipNumberUserIntent
    data object ContinueClicked : ChipNumberUserIntent
    data object BackClicked : ChipNumberUserIntent
}

sealed interface ChipNumberUiEffect {
    data object NavigateToPhoto : ChipNumberUiEffect
    data object NavigateBack : ChipNumberUiEffect
}
```

### 6. Preview Strategy with PreviewParameterProvider

**Decision**: Create `ChipNumberUiStateProvider` with multiple state variations for comprehensive preview coverage.

**Rationale**:
- Constitution requires `@PreviewParameter` with custom provider
- Enables preview of empty, partial, and complete input states
- No ViewModel dependency in previews

**Implementation Pattern**:
```kotlin
class ChipNumberUiStateProvider : PreviewParameterProvider<ChipNumberUiState> {
    override val values = sequenceOf(
        ChipNumberUiState.Initial,  // Empty state
        ChipNumberUiState(chipNumber = "12345"),  // Partial input
        ChipNumberUiState(chipNumber = "123456789012345")  // Complete input
    )
}

@Preview(showBackground = true)
@Composable
private fun ChipNumberContentPreview(
    @PreviewParameter(ChipNumberUiStateProvider::class) state: ChipNumberUiState
) {
    MaterialTheme {
        ChipNumberContent(state = state)
    }
}
```

### 7. Test Tag Naming Convention

**Decision**: Use `reportMissing.chipNumber.{element}` pattern for container elements and `missingPet.microchip.{element}` for input elements, matching existing Report Missing Pet flow conventions.

**Rationale**:
- Follows actual project conventions (not constitution - has known inconsistency to address separately)
- Consistent with existing `reportMissing.*` and `missingPet.*` screens
- Enables E2E test targeting

**Test Tags** (matching existing ChipNumberContent.kt):
| Element | Test Tag |
|---------|----------|
| Content container | `reportMissing.chipNumber.content` |
| Input field | `missingPet.microchip.input` |
| Continue button | `missingPet.microchip.continueButton` |
| Back button | `reportMissing.header.backButton` (from shared StepHeader) |

## Dependencies

### Existing (from spec 018)
- `ReportMissingPetFlowState` - shared flow state class
- `ReportMissingNavGraph` - nested navigation graph
- `ReportMissingFlowViewModel` - NavGraph-scoped ViewModel for flow state

### New Dependencies
None - all required libraries already in project:
- `androidx.compose.material3` - UI components
- `androidx.navigation:navigation-compose` - Navigation
- `io.insert-koin:koin-androidx-compose` - DI

## Risks & Mitigations

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| Spec 018 not merged | Low | High | Coordinate merge before implementation |
| Cursor position issues with VisualTransformation | Medium | Medium | Test extensively with Turbine; use OffsetMapping correctly |
| Flow state not persisting on config change | Low | Medium | Use SavedStateHandle if needed (likely handled by 018) |

## Summary

All technical unknowns resolved. Implementation approach:
1. Create MicrochipNumberFormatter with VisualTransformation
2. Create ChipNumberUiState, UserIntent, UiEffect in MVI package
3. Create ChipNumberViewModel consuming flow state from spec 018
4. Create two-layer Composable (Screen + Content) with previews
5. Register ViewModel in Koin module
6. Write unit tests with Turbine for state/effect testing
7. Write E2E tests with Cucumber/Appium

