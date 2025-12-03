# Quickstart: Android Animal Description Screen

**Feature**: 042-android-animal-description-screen  
**Date**: 2025-12-03

## Prerequisites

1. **Spec 018 merged**: Report Missing Pet flow navigation scaffolding exists
2. **Spec 026 merged**: Location permission handling available
3. **Spec 038 pattern**: Chip Number screen provides MVI reference implementation

## Setup Steps

### 1. Verify Existing Infrastructure

```bash
# Check flow state exists
cat composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/state/ReportMissingFlowState.kt

# Check description placeholder exists
cat composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/description/DescriptionScreen.kt

# Check location repository exists (from spec 026)
cat composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/domain/repositories/LocationRepository.kt
```

### 2. Run Existing Tests

```bash
# Ensure existing tests pass before making changes
./gradlew :composeApp:testDebugUnitTest
```

### 3. Build & Run App

```bash
# Build debug APK
./gradlew :composeApp:assembleDebug

# Or run directly on connected device/emulator
./gradlew :composeApp:installDebug
```

## Implementation Order

### Phase 1: Data Models & State

1. **Create `AnimalGender` enum**
   - Location: `features/reportmissing/domain/models/AnimalGender.kt`
   
2. **Create `SpeciesTaxonomy` object**
   - Location: `features/reportmissing/data/SpeciesTaxonomy.kt`
   - Bundled species list

3. **Extend `FlowData` in `ReportMissingFlowState`**
   - Add animal description fields
   - Add update methods

4. **Create MVI classes**
   - `AnimalDescriptionUiState.kt`
   - `AnimalDescriptionUserIntent.kt`
   - `AnimalDescriptionUiEffect.kt`

### Phase 2: Validation & ViewModel

5. **Create `AnimalDescriptionValidator`**
   - Location: `features/reportmissing/util/AnimalDescriptionValidator.kt`
   - Write tests first (TDD)

6. **Create `AnimalDescriptionViewModel`**
   - Location: `features/reportmissing/presentation/viewmodels/AnimalDescriptionViewModel.kt`
   - Write tests with Turbine

7. **Register ViewModel in Koin**
   - Update `ReportMissingModule.kt`

### Phase 3: UI Components

8. **Create reusable components**
   - `DatePickerField.kt`
   - `SpeciesDropdown.kt`
   - `GenderSelector.kt`
   - `GpsLocationSection.kt`
   - `CharacterCounterTextField.kt`

9. **Replace placeholder `AnimalDescriptionContent`**
   - Convert `DescriptionContent.kt` to full implementation
   - Add previews with `PreviewParameterProvider`

10. **Update `DescriptionScreen`**
    - Wire up ViewModel
    - Handle effects (navigation, snackbar)

### Phase 4: Testing & Polish

11. **Unit Tests**
    - ViewModel tests with Turbine
    - Validator tests
    - Reducer tests (if extracted)

12. **E2E Tests**
    - Create Screen Object
    - Create Cucumber feature file
    - Create step definitions

## Key Files Reference

| Purpose | Path |
|---------|------|
| Flow State | `features/reportmissing/presentation/state/ReportMissingFlowState.kt` |
| NavGraph | `features/reportmissing/ui/ReportMissingNavGraph.kt` |
| Step Header | `features/reportmissing/ui/components/StepHeader.kt` |
| Location Repo | `domain/repositories/LocationRepository.kt` |
| Koin Module | `di/ReportMissingModule.kt` (or create if needed) |

## Common Patterns

### MVI ViewModel Template

```kotlin
class AnimalDescriptionViewModel(
    private val flowState: ReportMissingFlowState,
    private val locationRepository: LocationRepository,
) : ViewModel() {
    
    private val _state = MutableStateFlow(AnimalDescriptionUiState.Initial)
    val state: StateFlow<AnimalDescriptionUiState> = _state.asStateFlow()
    
    private val _effects = MutableSharedFlow<AnimalDescriptionUiEffect>()
    val effects: SharedFlow<AnimalDescriptionUiEffect> = _effects.asSharedFlow()
    
    init {
        // Load initial data from flow state
        viewModelScope.launch {
            flowState.data.collect { data ->
                _state.update { currentState ->
                    currentState.copy(
                        disappearanceDate = data.disappearanceDate,
                        animalSpecies = data.animalSpecies,
                        // ... etc
                    )
                }
            }
        }
    }
    
    fun dispatch(intent: AnimalDescriptionUserIntent) {
        when (intent) {
            is AnimalDescriptionUserIntent.UpdateSpecies -> handleUpdateSpecies(intent.species)
            is AnimalDescriptionUserIntent.ContinueClicked -> handleContinue()
            // ... etc
        }
    }
    
    private fun handleContinue() {
        val validation = AnimalDescriptionValidator.validate(_state.value)
        if (!validation.isValid) {
            _state.update { it.copy(
                speciesError = validation.speciesError,
                raceError = validation.raceError,
                // ... etc
            )}
            viewModelScope.launch {
                _effects.emit(AnimalDescriptionUiEffect.ShowSnackbar("Please correct the errors"))
            }
            return
        }
        
        // Save to flow state
        flowState.updateAnimalDescription(/* ... */)
        
        // Navigate
        viewModelScope.launch {
            _effects.emit(AnimalDescriptionUiEffect.NavigateToContactDetails)
        }
    }
}
```

### Stateless Content Composable Template

```kotlin
@Composable
fun AnimalDescriptionContent(
    state: AnimalDescriptionUiState,
    modifier: Modifier = Modifier,
    onDateClick: () -> Unit = {},
    onSpeciesSelected: (String) -> Unit = {},
    onRaceChanged: (String) -> Unit = {},
    onGenderSelected: (AnimalGender) -> Unit = {},
    onAgeChanged: (String) -> Unit = {},
    onRequestGps: () -> Unit = {},
    onLatitudeChanged: (String) -> Unit = {},
    onLongitudeChanged: (String) -> Unit = {},
    onDescriptionChanged: (String) -> Unit = {},
    onContinueClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    Column(modifier = modifier.fillMaxSize()) {
        StepHeader(
            title = "Animal description",
            currentStep = 3,
            onBackClick = onBackClick,
        )
        
        // Form content in scrollable column
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // Form fields...
        }
        
        // Continue button
        Button(
            onClick = onContinueClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("animalDescription.continueButton"),
        ) {
            Text("Continue")
        }
    }
}
```

## Test Commands

```bash
# Run unit tests
./gradlew :composeApp:testDebugUnitTest

# Run with coverage
./gradlew :composeApp:testDebugUnitTest koverHtmlReport

# View coverage report
open composeApp/build/reports/kover/html/index.html

# Run E2E tests (Android)
mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@android and @animal-description"
```

## Troubleshooting

### GPS Not Working in Emulator

1. Open Extended Controls (three dots) in emulator
2. Go to Location tab
3. Set coordinates or use "Set Location" button

### DatePicker Crashes

Ensure Material 3 dependency is up to date:
```kotlin
implementation("androidx.compose.material3:material3:1.2.0")
```

### Flow State Not Persisting

Check ViewModel is properly scoped to NavGraph:
```kotlin
// In ReportMissingNavGraph.kt
val flowState: ReportMissingFlowState = koinInject()
```

