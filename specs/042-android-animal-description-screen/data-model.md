# Data Model: Android Animal Description Screen

**Feature**: 042-android-animal-description-screen  
**Date**: 2025-12-03

## Overview

Data models for the Animal Description screen (Step 3/4) in the Report Missing Pet flow.

## Entities

### 1. AnimalGender (Enum)

**Location**: `/composeApp/src/androidMain/.../features/reportmissing/domain/models/AnimalGender.kt`

```kotlin
enum class AnimalGender {
    FEMALE,
    MALE
}
```

**Purpose**: Represents the binary gender selection for the missing pet.

### 2. SpeciesTaxonomyOption (Data Class)

**Location**: `/composeApp/src/androidMain/.../features/reportmissing/data/SpeciesTaxonomy.kt`

```kotlin
data class SpeciesTaxonomyOption(
    val id: String,
    val displayName: String,
)

object SpeciesTaxonomy {
    val species: List<SpeciesTaxonomyOption> = listOf(
        SpeciesTaxonomyOption("DOG", "Dog"),
        SpeciesTaxonomyOption("CAT", "Cat"),
        SpeciesTaxonomyOption("BIRD", "Bird"),
        SpeciesTaxonomyOption("RABBIT", "Rabbit"),
        SpeciesTaxonomyOption("OTHER", "Other"),
    )
}
```

**Purpose**: Bundled static list of animal species aligned with Web platform (`types/animal.ts`). No network dependency.

**Cross-platform alignment**: Uses uppercase IDs to match Web's `ANIMAL_SPECIES` constant and backend API format.

### 3. FlowData Extension (Shared State)

**Location**: `/composeApp/src/androidMain/.../features/reportmissing/presentation/state/ReportMissingFlowState.kt`

```kotlin
data class FlowData(
    // Step 1/4 - Chip Number
    val chipNumber: String = "",
    
    // Step 2/4 - Photo
    val photoUri: String? = null,
    
    // Step 3/4 - Animal Description (NEW)
    val disappearanceDate: LocalDate = LocalDate.now(),
    val animalSpecies: String = "",
    val animalRace: String = "",
    val animalGender: AnimalGender? = null,
    val animalAge: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val additionalDescription: String = "",
    
    // Step 4/4 - Contact Details
    val contactEmail: String = "",
    val contactPhone: String = "",
)
```

**Changes from current**:
- Renamed `description` to `additionalDescription` for clarity
- Added all animal description fields
- Uses `LocalDate` for date (not String) for type safety

### 4. AnimalDescriptionUiState (MVI State)

**Location**: `/composeApp/src/androidMain/.../features/reportmissing/presentation/mvi/AnimalDescriptionUiState.kt`

```kotlin
data class AnimalDescriptionUiState(
    // Form fields
    val disappearanceDate: LocalDate = LocalDate.now(),
    val animalSpecies: String = "",
    val animalRace: String = "",
    val animalGender: AnimalGender? = null,
    val animalAge: String = "",  // String for text field, empty = null
    val latitude: String = "",
    val longitude: String = "",
    val additionalDescription: String = "",
    
    // Validation errors (null = no error)
    val speciesError: String? = null,
    val raceError: String? = null,
    val genderError: String? = null,
    val ageError: String? = null,
    val latitudeError: String? = null,
    val longitudeError: String? = null,
    
    // Loading states
    val isGpsLoading: Boolean = false,
    val gpsSuccessMessage: String? = null,
    
    // Derived
    val isRaceFieldEnabled: Boolean = false,  // Enabled when species selected
) {
    companion object {
        val Initial = AnimalDescriptionUiState()
    }
    
    /** True if all required fields have values (not necessarily valid) */
    val hasRequiredFields: Boolean
        get() = animalSpecies.isNotBlank() 
            && animalRace.isNotBlank() 
            && animalGender != null
    
    /** Character count for description field */
    val descriptionCharCount: Int
        get() = additionalDescription.length
    
    val descriptionMaxChars: Int = 500
}
```

### 5. AnimalDescriptionUserIntent (MVI Intent)

**Location**: `/composeApp/src/androidMain/.../features/reportmissing/presentation/mvi/AnimalDescriptionUserIntent.kt`

```kotlin
sealed interface AnimalDescriptionUserIntent {
    data class UpdateDate(val date: LocalDate) : AnimalDescriptionUserIntent
    data class UpdateSpecies(val species: String) : AnimalDescriptionUserIntent
    data class UpdateRace(val race: String) : AnimalDescriptionUserIntent
    data class UpdateGender(val gender: AnimalGender) : AnimalDescriptionUserIntent
    data class UpdateAge(val age: String) : AnimalDescriptionUserIntent
    data object RequestGpsPosition : AnimalDescriptionUserIntent
    data class UpdateLatitude(val latitude: String) : AnimalDescriptionUserIntent
    data class UpdateLongitude(val longitude: String) : AnimalDescriptionUserIntent
    data class UpdateDescription(val description: String) : AnimalDescriptionUserIntent
    data object ContinueClicked : AnimalDescriptionUserIntent
    data object BackClicked : AnimalDescriptionUserIntent
    data object OpenDatePicker : AnimalDescriptionUserIntent
    data object DismissDatePicker : AnimalDescriptionUserIntent
}
```

### 6. AnimalDescriptionUiEffect (MVI Effect)

**Location**: `/composeApp/src/androidMain/.../features/reportmissing/presentation/mvi/AnimalDescriptionUiEffect.kt`

```kotlin
sealed interface AnimalDescriptionUiEffect {
    data object NavigateToContactDetails : AnimalDescriptionUiEffect
    data object NavigateBack : AnimalDescriptionUiEffect
    data class ShowSnackbar(val message: String) : AnimalDescriptionUiEffect
    data object ShowDatePicker : AnimalDescriptionUiEffect
    data object OpenLocationSettings : AnimalDescriptionUiEffect
}
```

### 7. ValidationResult (Helper)

**Location**: `/composeApp/src/androidMain/.../features/reportmissing/util/AnimalDescriptionValidator.kt`

```kotlin
data class ValidationResult(
    val isValid: Boolean,
    val speciesError: String? = null,
    val raceError: String? = null,
    val genderError: String? = null,
    val ageError: String? = null,
    val latitudeError: String? = null,
    val longitudeError: String? = null,
)

object AnimalDescriptionValidator {
    
    fun validate(state: AnimalDescriptionUiState): ValidationResult {
        val speciesError = if (state.animalSpecies.isBlank()) 
            "Please select an animal species" else null
        val raceError = if (state.animalRace.isBlank() && state.animalSpecies.isNotBlank()) 
            "Please enter the animal race" else null
        val genderError = if (state.animalGender == null) 
            "Please select a gender" else null
        
        val ageError = state.animalAge.let { age ->
            if (age.isBlank()) null
            else {
                val ageInt = age.toIntOrNull()
                when {
                    ageInt == null -> "Age must be a number"
                    ageInt < 0 -> "Age cannot be negative"
                    ageInt > 40 -> "Age must be 40 or less"
                    else -> null
                }
            }
        }
        
        val latitudeError = state.latitude.let { lat ->
            if (lat.isBlank()) null
            else {
                val latDouble = lat.toDoubleOrNull()
                when {
                    latDouble == null -> "Invalid latitude format"
                    latDouble < -90 || latDouble > 90 -> "Latitude must be between -90 and 90"
                    else -> null
                }
            }
        }
        
        val longitudeError = state.longitude.let { lon ->
            if (lon.isBlank()) null
            else {
                val lonDouble = lon.toDoubleOrNull()
                when {
                    lonDouble == null -> "Invalid longitude format"
                    lonDouble < -180 || lonDouble > 180 -> "Longitude must be between -180 and 180"
                    else -> null
                }
            }
        }
        
        return ValidationResult(
            isValid = speciesError == null && raceError == null && genderError == null 
                && ageError == null && latitudeError == null && longitudeError == null,
            speciesError = speciesError,
            raceError = raceError,
            genderError = genderError,
            ageError = ageError,
            latitudeError = latitudeError,
            longitudeError = longitudeError,
        )
    }
}
```

## State Transitions

### Flow State Lifecycle

```
┌─────────────────────────────────────────────────────────────────┐
│                    ReportMissingFlowState                       │
├─────────────────────────────────────────────────────────────────┤
│  Created: When Report Missing NavGraph entered                  │
│  Cleared: When flow completed or exited via back from Step 1    │
│                                                                 │
│  Step 1 → Step 2 → Step 3 → Step 4 → Summary → Submit          │
│    ↑         ↑        ↑        ↑                               │
│    │         │        │        │                               │
│    └─────────┴────────┴────────┘ (Back navigation preserves)   │
└─────────────────────────────────────────────────────────────────┘
```

### AnimalDescriptionUiState Transitions

```
Initial State
     │
     ▼
┌─────────────────┐
│ Form empty      │
│ Race disabled   │
│ No errors       │
└────────┬────────┘
         │ User selects species
         ▼
┌─────────────────┐
│ Race enabled    │
│ Clear race if   │
│ species changed │
└────────┬────────┘
         │ User fills form
         ▼
┌─────────────────┐      GPS request
│ Continue tap    │──────────────────┐
└────────┬────────┘                  │
         │ Validation                 ▼
         ▼                    ┌─────────────────┐
┌─────────────────┐           │ isGpsLoading    │
│ Errors? Show    │           │ = true          │
│ Snackbar + set  │           └────────┬────────┘
│ field errors    │                    │ Success/Failure
└────────┬────────┘                    ▼
         │ All valid           ┌─────────────────┐
         ▼                     │ Populate lat/   │
┌─────────────────┐            │ long OR show    │
│ Navigate to     │            │ Snackbar error  │
│ Step 4          │            └─────────────────┘
└─────────────────┘
```

## Relationships

```
┌──────────────────────────────┐
│   ReportMissingFlowState     │
│   (NavGraph-scoped)          │
└──────────────┬───────────────┘
               │ reads/writes
               ▼
┌──────────────────────────────┐
│ AnimalDescriptionViewModel   │
│   - owns UiState             │
│   - emits Effects            │
│   - handles Intents          │
└──────────────┬───────────────┘
               │ provides state
               ▼
┌──────────────────────────────┐
│ AnimalDescriptionContent     │
│   (Stateless Composable)     │
│   - renders UiState          │
│   - emits callbacks          │
└──────────────────────────────┘
```

## Field Constraints Summary

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| Date of disappearance | LocalDate | Yes | No future dates (enforced by picker) |
| Animal species | String | Yes | Must be non-empty |
| Animal race | String | Yes | Must be non-empty (after species selected) |
| Gender | AnimalGender? | Yes | Must be selected |
| Age | String (Int) | No | 0-40 if provided |
| Latitude | String (Double) | No | -90 to 90 if provided |
| Longitude | String (Double) | No | -180 to 180 if provided |
| Additional description | String | No | Max 500 characters |

