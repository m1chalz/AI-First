# Research: Android Animal Description Screen

**Feature**: 042-android-animal-description-screen  
**Date**: 2025-12-03

## Overview

Research findings for implementing the Animal Description screen (Step 3/4) in the Android Report Missing Pet flow.

## Key Decisions

### 1. MVI State Structure

**Decision**: Use dedicated `AnimalDescriptionUiState` with validation error states per field

**Rationale**: 
- Consistent with existing MVI patterns in the codebase (see `ChipNumberUiState`, `PetDetailsUiState`)
- Enables granular error display for each field
- Immutable state with copy() for updates

**Alternatives Considered**:
- Single error string - rejected: can't show multiple field errors simultaneously
- Map<FieldName, Error> - rejected: less type-safe than dedicated properties

### 2. Form Validation Strategy

**Decision**: Validate all fields on Continue tap (not inline during typing)

**Rationale**:
- Per spec clarification: "Q: When should field validation occur? → A: On submit"
- Reduces UI noise during data entry
- Continue button always enabled, validation blocks navigation if invalid
- Material error states shown after validation fails

**Alternatives Considered**:
- Inline validation on blur - rejected: spec explicitly requires on-submit validation
- Disable Continue until valid - rejected: spec says Continue always enabled

### 3. Species Dropdown Implementation

**Decision**: Use Material 3 `ExposedDropdownMenuBox` with bundled species list

**Rationale**:
- Native Material Design 3 component
- Works well with OutlinedTextField styling
- Species list is static/bundled, no network dependency
- Supports proper accessibility

**Alternatives Considered**:
- ModalBottomSheet - rejected: more complex for simple list selection
- Custom dialog - rejected: reinventing the wheel

### 4. Date Picker Implementation

**Decision**: Use `DatePickerDialog` from Material 3 with max date constraint

**Rationale**:
- Native Android date picker with Material 3 styling
- Supports `selectableDates` parameter to disable future dates
- Default to today's date per spec

**Alternatives Considered**:
- Inline DatePicker - rejected: takes too much vertical space in a form
- Custom date field - rejected: worse UX than system picker

### 5. GPS Location Request

**Decision**: Reuse `LocationRepository` from spec 026 with loading state in button

**Rationale**:
- Existing infrastructure for location permissions and fetching
- Button shows spinner + "Requesting…" during fetch (per spec clarification)
- Coordinates stored as separate lat/long in flow state

**Alternatives Considered**:
- Direct FusedLocationProviderClient call - rejected: duplicates existing abstraction
- Map picker - rejected: out of scope per spec

### 6. Flow State Extension

**Decision**: Extend existing `ReportMissingFlowState.FlowData` with animal description fields

**Rationale**:
- Maintains single source of truth for flow data
- Consistent with existing pattern (chipNumber, photoUri, description)
- Data persists across navigation within flow

**Implementation**:
```kotlin
data class FlowData(
    // Existing fields
    val chipNumber: String = "",
    val photoUri: String? = null,
    val description: String = "",  // Rename to additionalDescription
    val contactEmail: String = "",
    val contactPhone: String = "",
    // New animal description fields
    val disappearanceDate: LocalDate = LocalDate.now(),
    val animalSpecies: String = "",
    val animalRace: String = "",
    val animalGender: AnimalGender? = null,
    val animalAge: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val additionalDescription: String = "",
)
```

### 7. Gender Selector Component

**Decision**: Two side-by-side cards using `SelectableCard` pattern with radio-style selection

**Rationale**:
- Matches Figma design (two cards: Female, Male)
- Mutually exclusive selection (only one active)
- Clear visual feedback for selection state

**Implementation**: Custom `GenderSelector` composable with two `Card` components

### 8. Character Counter TextField

**Decision**: Custom `CharacterCounterTextField` composable with hard limit enforcement

**Rationale**:
- Standard OutlinedTextField doesn't support character counter
- Need to truncate pasted text that exceeds limit
- Live counter shows current/max (e.g., "123/500")

**Implementation**: Wrapper around `OutlinedTextField` with `onValueChange` filtering

## Technology Research

### Material 3 DatePicker in Compose

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = { /* ... */ }
    ) {
        DatePicker(state = datePickerState)
    }
}
```

### ExposedDropdownMenuBox Pattern

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeciesDropdown(
    selectedSpecies: String,
    options: List<String>,
    onSpeciesSelected: (String) -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = it }
    ) {
        OutlinedTextField(
            value = selectedSpecies.ifEmpty { "Select an option" },
            onValueChange = {},
            readOnly = true,
            isError = isError,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor()
        )
        
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSpeciesSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
```

### Coordinate Validation

```kotlin
object CoordinateValidator {
    fun isValidLatitude(value: String): Boolean {
        val lat = value.toDoubleOrNull() ?: return false
        return lat in -90.0..90.0
    }
    
    fun isValidLongitude(value: String): Boolean {
        val lon = value.toDoubleOrNull() ?: return false
        return lon in -180.0..180.0
    }
}
```

## Dependencies

### Existing (no new dependencies needed)
- `androidx.compose.material3:material3` - Material 3 components
- `androidx.navigation:navigation-compose` - Navigation
- `io.insert-koin:koin-androidx-compose` - DI
- `com.google.android.gms:play-services-location` - GPS (via spec 026)

### From Project
- `LocationRepository` (spec 026) - GPS location fetching
- `ReportMissingFlowState` (spec 018) - Shared flow state
- `StepHeader` component - Header with progress indicator

## Open Questions Resolved

| Question | Resolution |
|----------|------------|
| Min SDK for DatePicker? | API 24+ supported with Material 3 |
| GPS timeout handling? | No explicit timeout per spec, rely on system |
| Species list content? | Bundled static list (Dog, Cat, Bird, etc.) |
| Race field - dropdown or text? | Text field per spec FR-006 |

