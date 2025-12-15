# Data Model: Android Report Created Confirmation Screen

**Branch**: `047-android-report-created-screen` | **Date**: 2025-12-04

## Overview

This document defines the data structures for the Android Report Created Confirmation Screen. Since this is a UI-only feature, the data model focuses on MVI components (UiState, UserIntent, UiEffect) and reuses the existing `ReportMissingFlowState` for data sourcing.

## Entities

### SummaryUiState

Immutable data class representing the current UI state of the Summary screen.

```kotlin
data class SummaryUiState(
    val managementPassword: String = ""
) {
    companion object {
        val Initial = SummaryUiState()
    }
}
```

**Fields**:
| Field | Type | Description | Default |
|-------|------|-------------|---------|
| `managementPassword` | `String` | The 6-7 digit code from flowState (empty string if null) | `""` |

**Notes**:
- Password container is always tappable - copying an empty string is harmless
- No `isPasswordCopied` flag needed - Snackbar is the feedback mechanism

### SummaryUserIntent

Sealed interface representing all possible user actions on this screen.

```kotlin
sealed interface SummaryUserIntent {
    /**
     * User tapped the password container to copy to clipboard.
     */
    data object CopyPasswordClicked : SummaryUserIntent
    
    /**
     * User tapped the Close button or performed system back action.
     */
    data object CloseClicked : SummaryUserIntent
}
```

**Intents**:
| Intent | Description | Result |
|--------|-------------|--------|
| `CopyPasswordClicked` | User wants to copy password | Copy to clipboard, show Snackbar |
| `CloseClicked` | User wants to exit flow | Clear state, navigate to pet list |

### SummaryUiEffect

Sealed interface for one-off events that should not be replayed on recomposition.

```kotlin
sealed interface SummaryUiEffect {
    /**
     * Show a transient Snackbar message.
     * @param message The message to display
     */
    data class ShowSnackbar(val message: String) : SummaryUiEffect
    
    /**
     * Dismiss the entire Missing Pet flow and navigate to pet list.
     */
    data object DismissFlow : SummaryUiEffect
}
```

**Effects**:
| Effect | Description | Handler |
|--------|-------------|---------|
| `ShowSnackbar` | Display clipboard confirmation | SnackbarHostState in UI |
| `DismissFlow` | Exit flow, clear state | NavController in NavGraph |

## Existing Entities (Referenced)

### ReportMissingFlowState (from spec 045)

The existing flow state container already contains the `managementPassword` property:

```kotlin
// Already exists in: /features/reportmissing/presentation/state/ReportMissingFlowState.kt
class ReportMissingFlowState {
    // ... other properties ...
    
    private val _managementPassword = MutableStateFlow<String?>(null)
    val managementPassword: StateFlow<String?> = _managementPassword.asStateFlow()
    
    fun setManagementPassword(password: String) {
        _managementPassword.value = password
    }
    
    fun clear() {
        // ... clears all properties including managementPassword
        _managementPassword.value = null
    }
}
```

**Usage**: SummaryViewModel reads `flowState.managementPassword` on initialization to populate `SummaryUiState`.

## State Transitions

```
┌─────────────────────────────────────────────────────────────┐
│                    SummaryUiState                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   ┌───────────────┐                                         │
│   │    Initial    │ ← ViewModel init reads flowState        │
│   │ password=""   │                                         │
│   └───────┬───────┘                                         │
│           │                                                 │
│           │ flowState.managementPassword observed           │
│           ▼                                                 │
│   ┌───────────────┐                                         │
│   │   Populated   │                                         │
│   │ password="X"  │                                         │
│   └───────┬───────┘                                         │
│           │                                                 │
│           │ CopyPasswordClicked                             │
│           │ → ShowSnackbar("Code copied...")                │
│           │ (state unchanged - feedback via effect only)    │
│           │                                                 │
│           │ CloseClicked (or system back)                   │
│           ▼                                                 │
│   ┌───────────────┐                                         │
│   │   Dismissed   │ → DismissFlow effect                    │
│   │ (flow exits)  │   flowState.clear()                     │
│   └───────────────┘   navController.popBackStack()          │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## Validation Rules

| Field | Rule | Error Handling |
|-------|------|----------------|
| `managementPassword` | Can be null/empty | Display empty string in UI (FR-007) |
| Clipboard content | Must match password | Copy exact value, no formatting |

## Data Flow

```
ReportMissingFlowState.managementPassword (from spec 045)
        │
        ▼
SummaryViewModel.init()
        │
        ├─► Read password value
        │
        ▼
SummaryUiState(managementPassword = value ?: "")
        │
        ▼
SummaryContent (stateless composable)
        │
        ├─► Display title + body paragraphs
        ├─► Display password in gradient container
        └─► Render Close button
```

## Test Data Requirements

### Unit Test Scenarios

```kotlin
// SummaryUiStateProvider for previews
class SummaryUiStateProvider : PreviewParameterProvider<SummaryUiState> {
    override val values = sequenceOf(
        // With password (typical case)
        SummaryUiState(managementPassword = "5216577"),
        
        // Empty password (null from flowState mapped to empty string)
        SummaryUiState.Initial
    )
}
```

### E2E Test Data

| Scenario | FlowState Setup | Expected Display |
|----------|-----------------|------------------|
| Normal flow | `managementPassword = "5216577"` | Digits visible in gradient |
| Empty password | `managementPassword = null` | Empty gradient container |
| Copy action | Any non-empty password | Snackbar appears |

