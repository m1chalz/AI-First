# Quickstart: Android Microchip Number Screen

**Feature Branch**: `038-android-chip-number-screen`  
**Date**: 2025-12-02  
**Phase**: Phase 1 - Implementation Guide

## Prerequisites

Before starting implementation:

1. **Merge spec 018** (018-android-missing-pet-flow) - provides navigation infrastructure
2. **Android Studio** with Compose preview support
3. **Kotlin 2.2.20** configured in project

## Implementation Steps

### Step 1: Create MVI Components (15 min)

Create the MVI state, intent, and effect classes:

```bash
# Create directory structure
mkdir -p composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi
```

**File: `ChipNumberUiState.kt`**
```kotlin
package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

data class ChipNumberUiState(
    val chipNumber: String = ""
) {
    companion object {
        val Initial = ChipNumberUiState()
    }
    
    val isComplete: Boolean get() = chipNumber.length == 15
}
```

**File: `ChipNumberUserIntent.kt`**
```kotlin
package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

sealed interface ChipNumberUserIntent {
    data class UpdateChipNumber(val value: String) : ChipNumberUserIntent
    data object ContinueClicked : ChipNumberUserIntent
    data object BackClicked : ChipNumberUserIntent
}
```

**File: `ChipNumberUiEffect.kt`**
```kotlin
package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

sealed interface ChipNumberUiEffect {
    data object NavigateToPhoto : ChipNumberUiEffect
    data object NavigateBack : ChipNumberUiEffect
}
```

### Step 2: Create Formatter Utility (20 min)

```bash
mkdir -p composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/util
```

**File: `MicrochipNumberFormatter.kt`**
```kotlin
package com.intive.aifirst.petspot.features.reportmissing.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

object MicrochipNumberFormatter {
    const val MAX_DIGITS = 15
    
    fun format(input: String): String {
        val digits = extractDigits(input).take(MAX_DIGITS)
        return buildString {
            digits.forEachIndexed { index, char ->
                if (index == 5 || index == 10) append('-')
                append(char)
            }
        }
    }
    
    fun extractDigits(input: String): String = input.filter { it.isDigit() }
}

class MicrochipVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val formatted = MicrochipNumberFormatter.format(text.text)
        return TransformedText(
            AnnotatedString(formatted),
            MicrochipOffsetMapping(text.text.length)
        )
    }
}

private class MicrochipOffsetMapping(private val originalLength: Int) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int = when {
        offset <= 5 -> offset
        offset <= 10 -> offset + 1
        else -> offset + 2
    }
    
    override fun transformedToOriginal(offset: Int): Int = when {
        offset <= 5 -> offset
        offset <= 11 -> offset - 1
        else -> offset - 2
    }.coerceIn(0, originalLength)
}
```

### Step 3: Create ViewModel (20 min)

```bash
mkdir -p composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels
```

**File: `ChipNumberViewModel.kt`**
```kotlin
package com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intive.aifirst.petspot.features.reportmissing.domain.model.ReportMissingPetFlowState
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.*
import com.intive.aifirst.petspot.features.reportmissing.util.MicrochipNumberFormatter
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChipNumberViewModel(
    private val flowState: ReportMissingPetFlowState
) : ViewModel() {
    
    private val _state = MutableStateFlow(
        ChipNumberUiState(chipNumber = flowState.chipNumber)
    )
    val state: StateFlow<ChipNumberUiState> = _state.asStateFlow()
    
    private val _effects = MutableSharedFlow<ChipNumberUiEffect>()
    val effects: SharedFlow<ChipNumberUiEffect> = _effects.asSharedFlow()
    
    fun handleIntent(intent: ChipNumberUserIntent) {
        when (intent) {
            is ChipNumberUserIntent.UpdateChipNumber -> {
                val digitsOnly = MicrochipNumberFormatter.extractDigits(intent.value)
                    .take(MicrochipNumberFormatter.MAX_DIGITS)
                _state.update { it.copy(chipNumber = digitsOnly) }
            }
            ChipNumberUserIntent.ContinueClicked -> {
                flowState.chipNumber = _state.value.chipNumber
                viewModelScope.launch { _effects.emit(ChipNumberUiEffect.NavigateToPhoto) }
            }
            ChipNumberUserIntent.BackClicked -> {
                viewModelScope.launch { _effects.emit(ChipNumberUiEffect.NavigateBack) }
            }
        }
    }
}
```

### Step 4: Create UI Components (30 min)

```bash
mkdir -p composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui
```

**File: `ChipNumberContent.kt`** (stateless composable + preview)
```kotlin
package com.intive.aifirst.petspot.features.reportmissing.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ChipNumberUiState
import com.intive.aifirst.petspot.features.reportmissing.util.MicrochipVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChipNumberContent(
    state: ChipNumberUiState,
    onChipNumberChange: (String) -> Unit = {},
    onContinueClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.testTag("reportMissing.chipNumber.content"),
        topBar = {
            TopAppBar(
                title = { Text("Microchip number") },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("reportMissing.header.backButton")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Text(
                        text = "1/4",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Microchip number",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "Microchip identification is the most efficient way to reunite with your pet. If your pet has been microchipped and you know the microchip number, please enter it here.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            OutlinedTextField(
                value = state.chipNumber,
                onValueChange = onChipNumberChange,
                label = { Text("Microchip number (optional)") },
                placeholder = { Text("00000-00000-00000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = MicrochipVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("missingPet.microchip.input")
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onContinueClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("missingPet.microchip.continueButton")
            ) {
                Text("Continue")
            }
        }
    }
}

class ChipNumberUiStateProvider : PreviewParameterProvider<ChipNumberUiState> {
    override val values = sequenceOf(
        ChipNumberUiState.Initial,
        ChipNumberUiState(chipNumber = "12345"),
        ChipNumberUiState(chipNumber = "123456789012345")
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

**File: `ChipNumberScreen.kt`** (state host)
```kotlin
package com.intive.aifirst.petspot.features.reportmissing.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ChipNumberUiEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ChipNumberUserIntent
import com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels.ChipNumberViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChipNumberScreen(
    onNavigateToPhoto: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ChipNumberViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    // Handle system back button
    BackHandler {
        viewModel.handleIntent(ChipNumberUserIntent.BackClicked)
    }
    
    // Handle effects (navigation)
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ChipNumberUiEffect.NavigateToPhoto -> onNavigateToPhoto()
                ChipNumberUiEffect.NavigateBack -> onNavigateBack()
            }
        }
    }
    
    ChipNumberContent(
        state = state,
        onChipNumberChange = { viewModel.handleIntent(ChipNumberUserIntent.UpdateChipNumber(it)) },
        onContinueClick = { viewModel.handleIntent(ChipNumberUserIntent.ContinueClicked) },
        onBackClick = { viewModel.handleIntent(ChipNumberUserIntent.BackClicked) }
    )
}
```

### Step 5: Register in Koin Module (5 min)

Add ViewModel registration to Koin module (in existing or new module):

```kotlin
// In di/ReportMissingModule.kt or existing module
val reportMissingModule = module {
    viewModel { ChipNumberViewModel(get()) }
}
```

### Step 6: Integrate with Navigation (10 min)

Add screen to navigation graph (assuming spec 018 infrastructure exists):

```kotlin
// In ReportMissingNavGraph.kt
composable(route = "chipNumber") {
    ChipNumberScreen(
        onNavigateToPhoto = { navController.navigate("photo") },
        onNavigateBack = { navController.popBackStack(route = "petList", inclusive = false) }
    )
}
```

### Step 7: Write Unit Tests (30 min)

```bash
mkdir -p composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/util
mkdir -p composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels
```

See `data-model.md` for complete test implementations.

## Verification Checklist

- [ ] App builds without errors: `./gradlew :composeApp:assembleDebug`
- [ ] Unit tests pass: `./gradlew :composeApp:testDebugUnitTest`
- [ ] Preview renders in Android Studio
- [ ] Numeric keyboard appears when field focused
- [ ] Hyphens auto-insert at positions 6 and 12
- [ ] Input limited to 15 digits
- [ ] Continue navigates to step 2/4
- [ ] Back button exits entire flow
- [ ] System back gesture exits entire flow
- [ ] Previously entered data persists when navigating back from step 2/4

## Test Commands

```bash
# Build
./gradlew :composeApp:assembleDebug

# Run unit tests
./gradlew :composeApp:testDebugUnitTest

# Run tests with coverage
./gradlew :composeApp:testDebugUnitTest koverHtmlReport
# View: composeApp/build/reports/kover/html/index.html

# Lint check
./gradlew :composeApp:lintDebug
```

## Common Issues

### Issue: VisualTransformation cursor jumps unexpectedly
**Solution**: Verify `MicrochipOffsetMapping` correctly maps offsets in both directions.

### Issue: State not persisting when navigating back
**Solution**: Ensure `flowState` is NavGraph-scoped (from spec 018), not screen-scoped.

### Issue: Koin injection fails
**Solution**: Register ViewModel in Koin module and ensure `ReportMissingPetFlowState` is provided.

## Timeline Estimate

| Task | Time |
|------|------|
| MVI components | 15 min |
| Formatter utility | 20 min |
| ViewModel | 20 min |
| UI components | 30 min |
| Koin registration | 5 min |
| Navigation integration | 10 min |
| Unit tests | 30 min |
| Manual testing | 15 min |
| **Total** | **~2.5 hours** |

## Next Steps

1. Run `/speckit.tasks` to generate detailed task breakdown
2. Implement following this quickstart
3. Create E2E tests after implementation
4. Submit PR for review

