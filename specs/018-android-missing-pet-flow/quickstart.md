# Quickstart: Android Missing Pet Report Flow

**Feature Branch**: `018-android-missing-pet-flow`  
**Date**: 2025-12-01

## Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 installed and configured
- Kotlin plugin updated to latest stable
- Android SDK with API 34 (or project target SDK)

## Setup Steps

### 1. Checkout Branch

```bash
git checkout 018-android-missing-pet-flow
```

### 2. Sync Gradle

Open project in Android Studio and sync Gradle:
- File → Sync Project with Gradle Files
- Or: `./gradlew :composeApp:assembleDebug`

### 3. Verify Build

```bash
./gradlew :composeApp:assembleDebug
```

Expected: BUILD SUCCESSFUL

### 4. Run Tests

```bash
# Run unit tests
./gradlew :composeApp:testDebugUnitTest

# Run tests with coverage report
./gradlew :composeApp:testDebugUnitTest koverHtmlReport

# View coverage report
open composeApp/build/reports/kover/html/index.html
```

### 5. Run App

- Open Android Studio
- Select device/emulator (API 24+)
- Run `composeApp` configuration
- Navigate to Animal List → tap "Report a Missing Animal"

## Feature Structure

```
composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/
└── features/reportmissing/
    ├── presentation/
    │   ├── mvi/
    │   │   ├── ReportMissingUiState.kt
    │   │   ├── ReportMissingIntent.kt
    │   │   ├── ReportMissingEffect.kt
    │   │   └── ReportMissingReducer.kt
    │   └── viewmodels/
    │       └── ReportMissingViewModel.kt
    └── ui/
        ├── ReportMissingNavGraph.kt
        ├── components/
        │   ├── StepHeader.kt              # Reusable: back + title + progress
        │   └── StepProgressIndicator.kt   # Reusable: circular "X/4" indicator
        ├── chipnumber/
        │   ├── ChipNumberScreen.kt
        │   └── ChipNumberContent.kt
        ├── photo/
        │   ├── PhotoScreen.kt
        │   └── PhotoContent.kt
        ├── description/
        │   ├── DescriptionScreen.kt
        │   └── DescriptionContent.kt
        ├── contactdetails/
        │   ├── ContactDetailsScreen.kt
        │   └── ContactDetailsContent.kt
        └── summary/
            ├── SummaryScreen.kt
            └── SummaryContent.kt
```

## Development Workflow

### Creating a New Screen

Each screen follows the two-layer composable pattern:

1. **State Host** (`*Screen.kt`):
```kotlin
@Composable
fun ChipNumberScreen(
    viewModel: ReportMissingViewModel = koinNavGraphViewModel(),
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ReportMissingEffect.NavigateToStep -> onNext()
                is ReportMissingEffect.NavigateBack -> onBack()
                else -> {}
            }
        }
    }
    
    ChipNumberContent(
        state = state,
        onChipNumberChange = { viewModel.dispatchIntent(ReportMissingIntent.UpdateChipNumber(it)) },
        onNextClick = { viewModel.dispatchIntent(ReportMissingIntent.NavigateNext) },
        onBackClick = { viewModel.dispatchIntent(ReportMissingIntent.NavigateBack) },
    )
}
```

2. **Stateless Content** (`*Content.kt`):
```kotlin
@Composable
fun ChipNumberContent(
    state: ReportMissingUiState,
    onChipNumberChange: (String) -> Unit = {},
    onNextClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Progress indicator
        if (state.showProgressIndicator) {
            StepProgressIndicator(
                currentStep = state.progressStepNumber,
                totalSteps = state.progressTotalSteps,
                modifier = Modifier.testTag("reportMissing.progressIndicator")
            )
        }
        
        // Screen content
        OutlinedTextField(
            value = state.chipNumber,
            onValueChange = onChipNumberChange,
            modifier = Modifier.testTag("reportMissing.chipNumber.textField.input")
        )
        
        Button(
            onClick = onNextClick,
            modifier = Modifier.testTag("reportMissing.chipNumber.nextButton.click")
        ) {
            Text("Next")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChipNumberContentPreview(
    @PreviewParameter(ReportMissingUiStatePreviewProvider::class) state: ReportMissingUiState
) {
    MaterialTheme {
        ChipNumberContent(state = state)
    }
}
```

### Using Reusable Components

**StepHeader** - Use on all data collection screens (Steps 1-4):

```kotlin
@Composable
fun ChipNumberContent(
    state: ReportMissingUiState,
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Reusable header with back + title + progress
        StepHeader(
            title = "Microchip number",
            currentStep = 1,
            totalSteps = 4,
            onBackClick = onBackClick
        )
        
        // Screen-specific content...
        
        // Continue button at bottom
        Button(
            onClick = onContinueClick,
            modifier = Modifier.testTag("reportMissing.chipNumber.continueButton.click")
        ) {
            Text("Continue")
        }
    }
}
```

**StepProgressIndicator** - Standalone usage (if needed outside StepHeader):

```kotlin
StepProgressIndicator(
    currentStep = 2,
    totalSteps = 4,
    modifier = Modifier.testTag("reportMissing.progress")
)
```

**Note**: Summary screen (Step 5) does NOT use StepHeader - it has no back button or progress indicator.

### Adding Test Tags

Follow naming convention: `reportMissing.{screen}.{element}.{action}`

```kotlin
// Header components (via StepHeader)
Modifier.testTag("reportMissing.header.backButton.click")
Modifier.testTag("reportMissing.header.title")
Modifier.testTag("reportMissing.header.progress")

// Screen-specific buttons
Modifier.testTag("reportMissing.chipNumber.continueButton.click")
Modifier.testTag("reportMissing.photo.continueButton.click")
Modifier.testTag("reportMissing.summary.closeButton.click")

// Input fields (future implementation)
Modifier.testTag("reportMissing.chipNumber.textField.input")
Modifier.testTag("reportMissing.description.textArea.input")
```

### Writing Unit Tests

```kotlin
class ReportMissingViewModelTest {
    
    @Test
    fun `should update chip number when UpdateChipNumber intent dispatched`() = runTest {
        // Given
        val viewModel = ReportMissingViewModel()
        
        // When
        viewModel.dispatchIntent(ReportMissingIntent.UpdateChipNumber("123456"))
        
        // Then
        assertEquals("123456", viewModel.state.value.chipNumber)
    }
    
    @Test
    fun `should emit NavigateToStep effect when NavigateNext intent dispatched from chip number`() = runTest {
        // Given
        val viewModel = ReportMissingViewModel()
        val effects = mutableListOf<ReportMissingEffect>()
        backgroundScope.launch { viewModel.effects.toList(effects) }
        
        // When
        viewModel.dispatchIntent(ReportMissingIntent.NavigateNext)
        advanceUntilIdle()
        
        // Then
        assertTrue(effects.any { it is ReportMissingEffect.NavigateToStep })
    }
}
```

## Key Files to Modify

| File | Purpose | Changes |
|------|---------|---------|
| `NavRoute.kt` | Navigation routes | Add `ReportMissingRoute` sealed interface |
| `NavGraph.kt` | Navigation graph | Add nested `navigation()` for flow |
| `NavControllerExt.kt` | Nav extensions | Enable `navigateToReportMissing()` |
| `ViewModelModule.kt` | Koin DI | Register `ReportMissingViewModel` |
| `AnimalListEffect.kt` | Existing effects | Already has `NavigateToReportMissing` |

## Testing Commands

```bash
# All unit tests
./gradlew :composeApp:testDebugUnitTest

# Specific feature tests
./gradlew :composeApp:testDebugUnitTest --tests "*ReportMissing*"

# Coverage report
./gradlew :composeApp:testDebugUnitTest koverHtmlReport

# Lint checks
./gradlew :composeApp:lint
```

## E2E Testing

E2E tests in `/e2e-tests/` (Java/Cucumber/Appium):

```bash
# Run Android E2E tests
mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@android"
```

## Debugging Tips

1. **Preview not rendering**: Check that `@PreviewParameter` provider returns valid states
2. **Navigation not working**: Verify routes added to `NavGraph.kt` and enabled in `NavControllerExt.kt`
3. **State not updating**: Check `dispatchIntent` is called, verify reducer handles intent type
4. **ViewModel not shared**: Ensure using `koinNavGraphViewModel()` in all screens

## Related Documents

- [Feature Spec](./spec.md) - Requirements and acceptance criteria
- [Plan](./plan.md) - Implementation plan and constitution check
- [Research](./research.md) - Technical decisions and alternatives
- [Data Model](./data-model.md) - Entity definitions and state transitions

