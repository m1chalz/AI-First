# Data Model: Android Microchip Number Screen

**Feature Branch**: `038-android-chip-number-screen`  
**Date**: 2025-12-02  
**Phase**: Phase 1 - Data Model Design

## Entity Overview

This feature works with existing flow state infrastructure (from spec 018) and adds screen-specific MVI components:

1. **ReportMissingPetFlowState** - Flow-level state shared across all 4 steps (✅ EXISTING from spec 018)
2. **ChipNumberUiState** - Screen-specific UI state (🆕 NEW)
3. **ChipNumberUserIntent** - User actions (🆕 NEW)
4. **ChipNumberUiEffect** - Navigation effects (🆕 NEW)
5. **MicrochipNumberFormatter** - Formatting utility (🆕 NEW)

---

## 1. ReportMissingPetFlowState ✅ EXISTING (from spec 018)

**Type**: `data class`  
**Location**: `/composeApp/src/androidMain/kotlin/.../features/reportmissing/domain/model/ReportMissingPetFlowState.kt`  
**Purpose**: Shared mutable state for the entire 4-step "Report Missing Pet" flow  
**Lifecycle**: Scoped to nested NavGraph, cleared when flow exits

### Properties (relevant to this screen)

| Property | Type | Description | Constraints |
|----------|------|-------------|-------------|
| `chipNumber` | `String` | Raw microchip number (digits only) | Optional, max 15 digits |

### Kotlin Implementation (from spec 018)

```kotlin
data class ReportMissingPetFlowState(
    val chipNumber: String = "",
    val photoUri: String? = null,
    val description: String = "",
    val contactEmail: String = "",
    val contactPhone: String = ""
)
```

---

## 2. ChipNumberUiState 🆕 NEW

**Type**: `data class`  
**Location**: `/composeApp/src/androidMain/kotlin/.../features/reportmissing/presentation/mvi/ChipNumberUiState.kt`  
**Purpose**: Immutable UI state for the chip number screen

### Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `chipNumber` | `String` | `""` | Raw digits only (no hyphens), max 15 chars |

### Kotlin Implementation

```kotlin
package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

/**
 * Immutable UI state for Chip Number screen (step 1/4).
 * Contains raw digits only; formatting applied via VisualTransformation.
 */
data class ChipNumberUiState(
    val chipNumber: String = ""
) {
    companion object {
        val Initial = ChipNumberUiState()
    }
    
    /** Returns true if chip number has maximum 15 digits entered. */
    val isComplete: Boolean get() = chipNumber.length == 15
}
```

### Design Rationale

**Why raw digits only in state?**
- Hyphens are display-only formatting (via VisualTransformation)
- Simplifies validation (just count digits)
- Matches backend expectation (raw digits)
- Consistent with iOS implementation

**Why no loading/error states?**
- This screen has no async operations (no API calls)
- Continue button always enabled (per spec FR-013)
- No validation errors to display

---

## 3. ChipNumberUserIntent 🆕 NEW

**Type**: `sealed interface`  
**Location**: `/composeApp/src/androidMain/kotlin/.../features/reportmissing/presentation/mvi/ChipNumberUserIntent.kt`  
**Purpose**: Represents all possible user actions on chip number screen

### Variants

| Intent | Parameters | Description |
|--------|-----------|-------------|
| `UpdateChipNumber` | `value: String` | User typed/deleted in input field |
| `ContinueClicked` | - | User tapped Continue button |
| `BackClicked` | - | User tapped back button or system back |

### Kotlin Implementation

```kotlin
package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

/**
 * User intents for Chip Number screen.
 * Each intent maps to a user action that triggers state changes.
 */
sealed interface ChipNumberUserIntent {
    /** User entered/modified chip number. Value contains raw digits only. */
    data class UpdateChipNumber(val value: String) : ChipNumberUserIntent
    
    /** User tapped Continue to proceed to step 2/4. */
    data object ContinueClicked : ChipNumberUserIntent
    
    /** User tapped back button (TopAppBar or system) to exit flow. */
    data object BackClicked : ChipNumberUserIntent
}
```

---

## 4. ChipNumberUiEffect 🆕 NEW

**Type**: `sealed interface`  
**Location**: `/composeApp/src/androidMain/kotlin/.../features/reportmissing/presentation/mvi/ChipNumberUiEffect.kt`  
**Purpose**: One-off navigation events (not part of UI state)

### Variants

| Effect | Description |
|--------|-------------|
| `NavigateToPhoto` | Navigate to step 2/4 (photo screen) |
| `NavigateBack` | Exit entire flow, return to pet list |

### Kotlin Implementation

```kotlin
package com.intive.aifirst.petspot.features.reportmissing.presentation.mvi

/**
 * One-off effects for Chip Number screen navigation.
 * Consumed by UI layer to trigger navigation actions.
 */
sealed interface ChipNumberUiEffect {
    /** Navigate to Photo screen (step 2/4). */
    data object NavigateToPhoto : ChipNumberUiEffect
    
    /** Exit entire Report Missing Pet flow, return to pet list. */
    data object NavigateBack : ChipNumberUiEffect
}
```

---

## 5. MicrochipNumberFormatter 🆕 NEW

**Type**: `object` (stateless utility) + `VisualTransformation`  
**Location**: `/composeApp/src/androidMain/kotlin/.../features/reportmissing/util/MicrochipNumberFormatter.kt`  
**Purpose**: Formats microchip numbers for display and provides VisualTransformation for TextField

### Methods

| Method | Parameters | Returns | Purpose |
|--------|-----------|---------|---------|
| `format` | `input: String` | `String` | Formats raw digits with hyphens: 00000-00000-00000 |
| `extractDigits` | `input: String` | `String` | Extracts only digits from input (removes non-digits) |

### Kotlin Implementation

```kotlin
package com.intive.aifirst.petspot.features.reportmissing.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Formats microchip numbers for display and provides VisualTransformation.
 * Format: 00000-00000-00000 (hyphens at positions 6 and 12)
 */
object MicrochipNumberFormatter {
    
    const val MAX_DIGITS = 15
    
    /**
     * Formats raw digits with hyphens: 00000-00000-00000
     * 
     * Examples:
     * - "12345" → "12345"
     * - "123456" → "12345-6"
     * - "123456789012345" → "12345-67890-12345"
     */
    fun format(input: String): String {
        val digits = extractDigits(input).take(MAX_DIGITS)
        return buildString {
            digits.forEachIndexed { index, char ->
                if (index == 5 || index == 10) append('-')
                append(char)
            }
        }
    }
    
    /**
     * Extracts only numeric digits from input string.
     * 
     * Examples:
     * - "12345-67890-12345" → "123456789012345"
     * - "abc123def456" → "123456"
     */
    fun extractDigits(input: String): String = input.filter { it.isDigit() }
}

/**
 * VisualTransformation that displays microchip numbers with hyphens.
 * Raw digits stored in state; formatted display shown to user.
 */
class MicrochipVisualTransformation : VisualTransformation {
    
    override fun filter(text: AnnotatedString): TransformedText {
        val formatted = MicrochipNumberFormatter.format(text.text)
        return TransformedText(
            AnnotatedString(formatted),
            MicrochipOffsetMapping(text.text.length)
        )
    }
}

/**
 * Maps cursor positions between raw digits and formatted display.
 */
private class MicrochipOffsetMapping(private val originalLength: Int) : OffsetMapping {
    
    override fun originalToTransformed(offset: Int): Int {
        // Add 1 for each hyphen before this position
        return when {
            offset <= 5 -> offset
            offset <= 10 -> offset + 1  // After first hyphen
            else -> offset + 2           // After second hyphen
        }
    }
    
    override fun transformedToOriginal(offset: Int): Int {
        // Subtract hyphens from position
        return when {
            offset <= 5 -> offset
            offset <= 11 -> offset - 1   // Account for first hyphen
            else -> offset - 2            // Account for both hyphens
        }.coerceIn(0, originalLength)
    }
}
```

---

## 6. ChipNumberViewModel 🆕 NEW

**Type**: `class` extending `ViewModel`  
**Location**: `/composeApp/src/androidMain/kotlin/.../features/reportmissing/presentation/viewmodels/ChipNumberViewModel.kt`  
**Purpose**: MVI ViewModel managing chip number screen state and effects

### Dependencies

| Dependency | Type | Purpose |
|------------|------|---------|
| `flowState` | `ReportMissingPetFlowState` | Shared flow state (from NavGraph scope) |

### Kotlin Implementation

```kotlin
package com.intive.aifirst.petspot.features.reportmissing.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intive.aifirst.petspot.features.reportmissing.domain.model.ReportMissingPetFlowState
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ChipNumberUiEffect
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ChipNumberUiState
import com.intive.aifirst.petspot.features.reportmissing.presentation.mvi.ChipNumberUserIntent
import com.intive.aifirst.petspot.features.reportmissing.util.MicrochipNumberFormatter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for Chip Number screen (step 1/4 of Report Missing Pet flow).
 * Manages UI state and navigation effects following MVI pattern.
 */
class ChipNumberViewModel(
    private val flowState: ReportMissingPetFlowState
) : ViewModel() {
    
    private val _state = MutableStateFlow(
        ChipNumberUiState(chipNumber = flowState.chipNumber)
    )
    val state: StateFlow<ChipNumberUiState> = _state.asStateFlow()
    
    private val _effects = MutableSharedFlow<ChipNumberUiEffect>()
    val effects: SharedFlow<ChipNumberUiEffect> = _effects.asSharedFlow()
    
    /**
     * Process user intent and update state or emit effects.
     */
    fun handleIntent(intent: ChipNumberUserIntent) {
        when (intent) {
            is ChipNumberUserIntent.UpdateChipNumber -> updateChipNumber(intent.value)
            ChipNumberUserIntent.ContinueClicked -> onContinue()
            ChipNumberUserIntent.BackClicked -> onBack()
        }
    }
    
    private fun updateChipNumber(value: String) {
        val digitsOnly = MicrochipNumberFormatter.extractDigits(value)
            .take(MicrochipNumberFormatter.MAX_DIGITS)
        _state.update { it.copy(chipNumber = digitsOnly) }
    }
    
    private fun onContinue() {
        // Save to flow state before navigating
        flowState.chipNumber = _state.value.chipNumber
        viewModelScope.launch {
            _effects.emit(ChipNumberUiEffect.NavigateToPhoto)
        }
    }
    
    private fun onBack() {
        // Don't save - just exit flow
        viewModelScope.launch {
            _effects.emit(ChipNumberUiEffect.NavigateBack)
        }
    }
}
```

---

## State Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                    ChipNumberScreen (UI)                        │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ collectAsStateWithLifecycle() ──► Render ChipNumberContent│  │
│  │                                                           │  │
│  │ User types ──► onValueChange ──► handleIntent(Update)     │  │
│  │ User taps Continue ──► handleIntent(ContinueClicked)      │  │
│  │ User taps Back ──► handleIntent(BackClicked)              │  │
│  └───────────────────────────────────────────────────────────┘  │
└──────────────────────────────┬──────────────────────────────────┘
                               │ Intent
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                   ChipNumberViewModel                           │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ handleIntent(intent)                                      │  │
│  │   ├─ UpdateChipNumber ──► _state.update(chipNumber)       │  │
│  │   ├─ ContinueClicked ──► flowState.chipNumber = state     │  │
│  │   │                      _effects.emit(NavigateToPhoto)   │  │
│  │   └─ BackClicked ──► _effects.emit(NavigateBack)          │  │
│  └───────────────────────────────────────────────────────────┘  │
│                                                                 │
│  StateFlow<ChipNumberUiState>  SharedFlow<ChipNumberUiEffect>   │
└─────────────────────────────────────────────────────────────────┘
                               │
                               │ On ContinueClicked
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│              ReportMissingPetFlowState (NavGraph-scoped)        │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ chipNumber: String ◄── saved from ChipNumberUiState       │  │
│  │ photoUri: String?    (step 2/4)                           │  │
│  │ description: String  (step 3/4)                           │  │
│  │ contactEmail: String (step 4/4)                           │  │
│  │ contactPhone: String (step 4/4)                           │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Test Cases

### MicrochipNumberFormatterTest

**Location**: `/composeApp/src/androidUnitTest/kotlin/.../features/reportmissing/util/MicrochipNumberFormatterTest.kt`

```kotlin
class MicrochipNumberFormatterTest {
    
    // format() tests
    
    @Test
    fun `format should return unchanged when input has fewer than 5 digits`() {
        // Given
        val input = "1234"
        // When
        val result = MicrochipNumberFormatter.format(input)
        // Then
        assertEquals("1234", result)
    }
    
    @Test
    fun `format should add first hyphen after 5 digits`() {
        // Given
        val input = "123456"
        // When
        val result = MicrochipNumberFormatter.format(input)
        // Then
        assertEquals("12345-6", result)
    }
    
    @Test
    fun `format should add second hyphen after 10 digits`() {
        // Given
        val input = "12345678901"
        // When
        val result = MicrochipNumberFormatter.format(input)
        // Then
        assertEquals("12345-67890-1", result)
    }
    
    @Test
    fun `format should format complete 15 digit number correctly`() {
        // Given
        val input = "123456789012345"
        // When
        val result = MicrochipNumberFormatter.format(input)
        // Then
        assertEquals("12345-67890-12345", result)
    }
    
    @Test
    fun `format should truncate input exceeding 15 digits`() {
        // Given
        val input = "123456789012345678"
        // When
        val result = MicrochipNumberFormatter.format(input)
        // Then
        assertEquals("12345-67890-12345", result)
    }
    
    @Test
    fun `format should handle already formatted input`() {
        // Given
        val input = "12345-67890-12345"
        // When
        val result = MicrochipNumberFormatter.format(input)
        // Then
        assertEquals("12345-67890-12345", result)
    }
    
    @Test
    fun `format should extract digits from mixed input`() {
        // Given
        val input = "abc123def456"
        // When
        val result = MicrochipNumberFormatter.format(input)
        // Then
        assertEquals("12345-6", result)
    }
    
    @Test
    fun `format should return empty for empty input`() {
        // Given
        val input = ""
        // When
        val result = MicrochipNumberFormatter.format(input)
        // Then
        assertEquals("", result)
    }
    
    // extractDigits() tests
    
    @Test
    fun `extractDigits should return unchanged for digits only`() {
        // Given
        val input = "123456789012345"
        // When
        val result = MicrochipNumberFormatter.extractDigits(input)
        // Then
        assertEquals("123456789012345", result)
    }
    
    @Test
    fun `extractDigits should remove hyphens`() {
        // Given
        val input = "12345-67890-12345"
        // When
        val result = MicrochipNumberFormatter.extractDigits(input)
        // Then
        assertEquals("123456789012345", result)
    }
    
    @Test
    fun `extractDigits should remove letters`() {
        // Given
        val input = "abc123def456"
        // When
        val result = MicrochipNumberFormatter.extractDigits(input)
        // Then
        assertEquals("123456", result)
    }
    
    @Test
    fun `extractDigits should return empty for non-digit input`() {
        // Given
        val input = "abcdef"
        // When
        val result = MicrochipNumberFormatter.extractDigits(input)
        // Then
        assertEquals("", result)
    }
}
```

### ChipNumberViewModelTest

**Location**: `/composeApp/src/androidUnitTest/kotlin/.../features/reportmissing/presentation/viewmodels/ChipNumberViewModelTest.kt`

```kotlin
class ChipNumberViewModelTest {
    
    @Test
    fun `initial state should load chip number from flow state`() = runTest {
        // Given
        val flowState = ReportMissingPetFlowState(chipNumber = "123456789012345")
        
        // When
        val viewModel = ChipNumberViewModel(flowState)
        
        // Then
        assertEquals("123456789012345", viewModel.state.value.chipNumber)
    }
    
    @Test
    fun `UpdateChipNumber intent should update state with digits only`() = runTest {
        // Given
        val flowState = ReportMissingPetFlowState()
        val viewModel = ChipNumberViewModel(flowState)
        
        // When
        viewModel.handleIntent(ChipNumberUserIntent.UpdateChipNumber("123-456"))
        
        // Then
        assertEquals("123456", viewModel.state.value.chipNumber)
    }
    
    @Test
    fun `UpdateChipNumber intent should limit to 15 digits`() = runTest {
        // Given
        val flowState = ReportMissingPetFlowState()
        val viewModel = ChipNumberViewModel(flowState)
        
        // When
        viewModel.handleIntent(ChipNumberUserIntent.UpdateChipNumber("123456789012345678"))
        
        // Then
        assertEquals("123456789012345", viewModel.state.value.chipNumber)
    }
    
    @Test
    fun `ContinueClicked should save chip number to flow state`() = runTest {
        // Given
        val flowState = ReportMissingPetFlowState()
        val viewModel = ChipNumberViewModel(flowState)
        viewModel.handleIntent(ChipNumberUserIntent.UpdateChipNumber("123456789012345"))
        
        // When
        viewModel.handleIntent(ChipNumberUserIntent.ContinueClicked)
        
        // Then
        assertEquals("123456789012345", flowState.chipNumber)
    }
    
    @Test
    fun `ContinueClicked should emit NavigateToPhoto effect`() = runTest {
        // Given
        val flowState = ReportMissingPetFlowState()
        val viewModel = ChipNumberViewModel(flowState)
        
        viewModel.effects.test {
            // When
            viewModel.handleIntent(ChipNumberUserIntent.ContinueClicked)
            
            // Then
            assertEquals(ChipNumberUiEffect.NavigateToPhoto, awaitItem())
        }
    }
    
    @Test
    fun `BackClicked should emit NavigateBack effect`() = runTest {
        // Given
        val flowState = ReportMissingPetFlowState()
        val viewModel = ChipNumberViewModel(flowState)
        
        viewModel.effects.test {
            // When
            viewModel.handleIntent(ChipNumberUserIntent.BackClicked)
            
            // Then
            assertEquals(ChipNumberUiEffect.NavigateBack, awaitItem())
        }
    }
    
    @Test
    fun `BackClicked should not save chip number to flow state`() = runTest {
        // Given
        val flowState = ReportMissingPetFlowState()
        val viewModel = ChipNumberViewModel(flowState)
        viewModel.handleIntent(ChipNumberUserIntent.UpdateChipNumber("123456789012345"))
        
        // When
        viewModel.handleIntent(ChipNumberUserIntent.BackClicked)
        
        // Then
        assertEquals("", flowState.chipNumber)  // Not saved
    }
}
```

---

## Summary

### Components to Implement

| Component | Type | Status | Lines (est.) |
|-----------|------|--------|--------------|
| ChipNumberUiState | Data class | 🆕 NEW | ~15 |
| ChipNumberUserIntent | Sealed interface | 🆕 NEW | ~15 |
| ChipNumberUiEffect | Sealed interface | 🆕 NEW | ~10 |
| MicrochipNumberFormatter | Object + VisualTransformation | 🆕 NEW | ~60 |
| ChipNumberViewModel | ViewModel | 🆕 NEW | ~50 |
| MicrochipNumberFormatterTest | Test class | 🆕 NEW | ~100 |
| ChipNumberViewModelTest | Test class | 🆕 NEW | ~80 |

### Dependencies on Spec 018 (✅ MERGED)

Spec 018 is already merged and provides:
- `ReportMissingUiState` - shared flow state (used by existing ChipNumberContent)
- `ChipNumberContent.kt` - placeholder implementation to enhance
- `ChipNumberScreen.kt` - state host composable  
- `StepHeader` - shared navigation header component
- Navigation graph integration

### Architecture Compliance

- ✅ MVI pattern with StateFlow, sealed intents, SharedFlow effects
- ✅ Two-layer Composable pattern (state host + stateless content)
- ✅ VisualTransformation for display formatting
- ✅ Koin dependency injection
- ✅ 80% test coverage target
- ✅ Given-When-Then test structure

