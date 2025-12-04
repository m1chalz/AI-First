# Data Model: Android Owner's Details Screen

**Feature**: 045-android-owners-details-screen  
**Date**: 2025-12-04

## Entities

### 1. OwnerDetailsUiState

**Purpose**: Immutable UI state for Owner's Details screen.

**Location**: `/composeApp/src/androidMain/.../presentation/mvi/OwnerDetailsUiState.kt`

```kotlin
data class OwnerDetailsUiState(
    // Form field values
    val phone: String = "",
    val email: String = "",
    val reward: String = "",
    
    // Validation errors (null = no error)
    val phoneError: String? = null,
    val emailError: String? = null,
    
    // Submission state
    val isSubmitting: Boolean = false,
    
    // Character counter for reward field
    val rewardCharacterCount: Int = 0,
    val rewardMaxLength: Int = 120
) {
    companion object {
        val Initial = OwnerDetailsUiState()
    }
    
    val isValid: Boolean
        get() = phoneError == null && emailError == null && phone.isNotBlank() && email.isNotBlank()
}
```

**Validation Rules**:
- `phone`: 7-11 digits after stripping non-digits (except leading +)
- `email`: RFC 5322 basic pattern (local@domain.tld)
- `reward`: Optional, max 120 UTF-8 characters

---

### 2. OwnerDetailsUserIntent

**Purpose**: Sealed class for all user interactions on this screen.

**Location**: `/composeApp/src/androidMain/.../presentation/mvi/OwnerDetailsUserIntent.kt`

```kotlin
sealed interface OwnerDetailsUserIntent {
    // Field updates
    data class UpdatePhone(val phone: String) : OwnerDetailsUserIntent
    data class UpdateEmail(val email: String) : OwnerDetailsUserIntent
    data class UpdateReward(val reward: String) : OwnerDetailsUserIntent
    
    // Actions
    data object ContinueClicked : OwnerDetailsUserIntent
    data object BackClicked : OwnerDetailsUserIntent
    data object RetryClicked : OwnerDetailsUserIntent
    data object SnackbarDismissed : OwnerDetailsUserIntent
}
```

---

### 3. OwnerDetailsUiEffect

**Purpose**: One-off events for navigation and feedback.

**Location**: `/composeApp/src/androidMain/.../presentation/mvi/OwnerDetailsUiEffect.kt`

```kotlin
sealed interface OwnerDetailsUiEffect {
    data class NavigateToSummary(val managementPassword: String) : OwnerDetailsUiEffect
    data object NavigateBack : OwnerDetailsUiEffect
    data class ShowSnackbar(
        val message: String,
        val action: SnackbarAction? = null
    ) : OwnerDetailsUiEffect
}

sealed interface SnackbarAction {
    data object Retry : SnackbarAction
}
```

---

### 4. FlowData Extension

**Purpose**: Add reward field to shared flow state.

**Location**: `/composeApp/src/androidMain/.../presentation/state/ReportMissingFlowState.kt`

**Modification to existing `FlowData`**:

```kotlin
data class FlowData(
    // ... existing fields (Step 1-3)
    
    // Step 4/4 - Contact Details
    val contactEmail: String = "",
    val contactPhone: String = "",
    val rewardDescription: String = "",  // NEW: Optional reward text
)
```

**New method in `ReportMissingFlowState`**:

```kotlin
/** Update reward description (Step 4/4) */
fun updateRewardDescription(reward: String) {
    _data.update { it.copy(rewardDescription = reward) }
}
```

---

### 5. AnnouncementCreateRequest

**Purpose**: DTO for POST /api/v1/announcements request body.

**Location**: `/composeApp/src/androidMain/.../domain/models/AnnouncementModels.kt`

```kotlin
data class AnnouncementCreateRequest(
    val species: String,           // From flow state (animalSpecies)
    val sex: String,               // From flow state (animalGender - "MALE" or "FEMALE")
    val lastSeenDate: String,      // ISO 8601 date from flow state (disappearanceDate)
    val locationLatitude: Double,  // From flow state
    val locationLongitude: Double, // From flow state
    val email: String,             // From this screen
    val phone: String,             // From this screen
    val status: String = "MISSING",
    val microchipNumber: String? = null,  // Optional from flow state (chipNumber)
    val description: String? = null,      // Optional from flow state (additionalDescription)
    val reward: String? = null,           // Optional from this screen
)
```

---

### 6. AnnouncementResponse

**Purpose**: DTO for POST /api/v1/announcements response.

**Location**: `/composeApp/src/androidMain/.../domain/models/AnnouncementModels.kt`

```kotlin
data class AnnouncementResponse(
    val id: String,                  // UUID for photo upload
    val managementPassword: String,  // 6-digit string for user reference
    // Other fields not needed for this feature
)
```

---

### 7. ValidationResult

**Purpose**: Result type for validation functions.

**Location**: `/composeApp/src/androidMain/.../features/reportmissing/util/OwnerDetailsValidator.kt`

```kotlin
sealed interface ValidationResult {
    data object Valid : ValidationResult
    data class Invalid(val message: String) : ValidationResult
}
```

---

## Relationships

```
┌─────────────────────────────────────────────────────────────────┐
│                    ContactDetailsScreen                          │
│  (State Host - collects state, dispatches intents)              │
└──────────────────────────┬──────────────────────────────────────┘
                           │ observes
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                   OwnerDetailsViewModel                          │
│  - state: StateFlow<OwnerDetailsUiState>                        │
│  - effects: SharedFlow<OwnerDetailsUiEffect>                    │
│  - dispatchIntent(intent: OwnerDetailsUserIntent)               │
└───────┬─────────────────┬──────────────────────┬────────────────┘
        │                 │                      │
        │ reads/writes    │ calls                │ emits
        ▼                 ▼                      ▼
┌───────────────┐  ┌──────────────────────┐  ┌────────────────────┐
│FlowState      │  │SubmitAnnouncement    │  │OwnerDetailsUiEffect│
│(shared data)  │  │UseCase               │  │(navigation/snackbar)│
└───────────────┘  └──────────┬───────────┘  └────────────────────┘
                              │ orchestrates
                              ▼
                   ┌──────────────────────┐
                   │AnnouncementRepository│
                   │  - createAnnouncement│
                   │  - uploadPhoto       │
                   └──────────────────────┘
```

## State Transitions

### OwnerDetailsUiState Transitions

```
Initial State
    │
    ├── UpdatePhone(phone) → state.copy(phone = phone, phoneError = null)
    ├── UpdateEmail(email) → state.copy(email = email, emailError = null)
    ├── UpdateReward(reward) → state.copy(reward = reward.take(120), rewardCharacterCount = ...)
    │
    ├── ContinueClicked (validation fails)
    │   → state.copy(phoneError = "...", emailError = "...")
    │   → ShowSnackbar("Please correct the highlighted fields")
    │
    ├── ContinueClicked (validation succeeds)
    │   → state.copy(isSubmitting = true)
    │   │
    │   ├── Submission succeeds
    │   │   → state.copy(isSubmitting = false)
    │   │   → NavigateToSummary(managementPassword)
    │   │
    │   └── Submission fails
    │       → state.copy(isSubmitting = false)
    │       → ShowSnackbar("Something went wrong...", action = Retry)
    │
    ├── RetryClicked
    │   → state.copy(isSubmitting = true)
    │   → [repeat submission flow]
    │
    └── BackClicked
        → NavigateBack
```

## Validation Rules

| Field | Rule | Error Message |
|-------|------|---------------|
| Phone | 7-11 digits after stripping non-digits (leading + allowed) | "Enter at least 7 digits" |
| Email | Matches `/^[^\s@]+@[^\s@]+\.[^\s@]+$/` | "Enter a valid email address" |
| Reward | Max 120 characters (optional) | N/A (hard limit enforced) |

