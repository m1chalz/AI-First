# Data Model: Android Animal Photo Screen

**Feature**: 040-android-animal-photo-screen  
**Date**: 2025-12-02

## Entities

### PhotoAttachmentState

Encapsulates the selected image metadata and UI state for the photo step.

```kotlin
/**
 * State representing photo attachment in the missing pet flow.
 * Immutable - create new instances for state changes.
 * Note: No @Parcelize needed - state persists via ReportMissingFlowState, not SavedStateHandle.
 */
data class PhotoAttachmentState(
    /** Content URI of the selected photo, null if no selection */
    val uri: String? = null,
    
    /** Display filename (truncated to 20 chars with ellipsis in UI) */
    val filename: String? = null,
    
    /** File size in bytes */
    val sizeBytes: Long = 0,
    
    /** Current UI state of photo selection */
    val status: PhotoStatus = PhotoStatus.EMPTY
) {
    
    companion object {
        val Empty = PhotoAttachmentState()
    }
    
    /** Whether a valid photo is currently attached */
    val hasPhoto: Boolean
        get() = uri != null && status == PhotoStatus.CONFIRMED
    
    /** Human-readable file size (e.g., "1.2 MB") */
    val formattedSize: String
        get() = FileSizeFormatter.format(sizeBytes)
    
    /** Filename truncated to max 20 characters with ellipsis */
    val displayFilename: String
        get() = filename?.let {
            if (it.length > 20) it.take(17) + "..." else it
        } ?: ""
}

/**
 * Photo selection status for UI rendering.
 */
enum class PhotoStatus {
    /** No photo selected - show empty state with Browse button */
    EMPTY,
    
    /** Photo selected, processing metadata - show loading indicator */
    LOADING,
    
    /** Photo ready - show confirmation card with thumbnail */
    CONFIRMED,
    
    /** Photo load failed - revert to empty state */
    ERROR
}
```

### ReportMissingUiState (Modified)

Extends existing state to include photo attachment.

```kotlin
/**
 * Immutable UI state for Missing Pet Report flow.
 * Single source of truth for all 5 screens in the wizard.
 * Note: Existing class - modifications shown below.
 */
data class ReportMissingUiState(
    // Current step tracking
    val currentStep: FlowStep = FlowStep.CHIP_NUMBER,
    
    // Step 1: Chip Number
    val chipNumber: String = "",
    
    // Step 2: Photo (NEW - replaces simple photoUri: String?)
    val photoAttachment: PhotoAttachmentState = PhotoAttachmentState.Empty,
    
    // Step 3: Description
    val description: String = "",
    
    // Step 4: Contact Details
    val contactEmail: String = "",
    val contactPhone: String = "",
    
    // UI state flags
    val isLoading: Boolean = false
) {
    
    companion object {
        val Initial = ReportMissingUiState()
    }
    
    // ... existing computed properties unchanged
    
    /** Legacy accessor for backward compatibility */
    @Deprecated("Use photoAttachment.uri instead", ReplaceWith("photoAttachment.uri"))
    val photoUri: String?
        get() = photoAttachment.uri
}
```

### Photo Intents (New)

User interactions specific to photo step.

```kotlin
/**
 * User intents for Missing Pet Report flow.
 */
sealed interface ReportMissingIntent {
    // ... existing intents
    
    // Photo-specific intents (NEW)
    
    /** User tapped Browse button to select a photo */
    data object OpenPhotoPicker : ReportMissingIntent
    
    /** Photo picker returned with a selected URI */
    data class PhotoSelected(val uri: String) : ReportMissingIntent
    
    /** Photo metadata loaded successfully */
    data class PhotoMetadataLoaded(
        val uri: String,
        val filename: String,
        val sizeBytes: Long
    ) : ReportMissingIntent
    
    /** Photo loading failed (corrupted, unavailable) */
    data object PhotoLoadFailed : ReportMissingIntent
    
    /** User tapped Remove (X) button on confirmation card */
    data object RemovePhoto : ReportMissingIntent
    
    /** User dismissed photo picker without selection */
    data object PhotoPickerCancelled : ReportMissingIntent
}
```

### Photo Effects (New)

One-off events for photo step.

```kotlin
/**
 * One-off effects emitted by ViewModel.
 */
sealed interface ReportMissingEffect {
    // ... existing effects
    
    // Photo-specific effects (NEW)
    
    /** Launch photo picker activity */
    data object LaunchPhotoPicker : ReportMissingEffect
    
    /** Show toast message */
    data class ShowToast(
        val messageResId: Int,
        val duration: Int = Toast.LENGTH_SHORT
    ) : ReportMissingEffect
}
```

## State Transitions

### Photo Selection Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    PhotoAttachmentState                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────┐  OpenPhotoPicker   ┌───────────────┐              │
│  │  EMPTY   │ ──────────────────► │ (Picker Open) │              │
│  └──────────┘                     └───────────────┘              │
│       ▲                                  │                       │
│       │                                  │ PhotoSelected         │
│       │ PhotoPickerCancelled             ▼                       │
│       │ PhotoLoadFailed            ┌──────────┐                  │
│       │ RemovePhoto                │ LOADING  │                  │
│       │                            └──────────┘                  │
│       │                                  │                       │
│       │                                  │ PhotoMetadataLoaded   │
│       │                                  ▼                       │
│       │                           ┌───────────┐                  │
│       └───────────────────────────│ CONFIRMED │                  │
│                                   └───────────┘                  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Continue Button Validation

```
┌─────────────────────────────────────────────────────────────────┐
│                    NavigateNext Intent                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  currentStep == PHOTO?                                           │
│       │                                                          │
│       ├── YES ──► photoAttachment.hasPhoto?                      │
│       │               │                                          │
│       │               ├── YES ──► Emit NavigateToStep(DESCRIPTION)│
│       │               │                                          │
│       │               └── NO ───► Emit ShowToast("Photo is mandatory")
│       │                           (no navigation)                │
│       │                                                          │
│       └── NO ───► Normal navigation (existing behavior)          │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Validation Rules

| Field | Rule | Error Handling |
|-------|------|----------------|
| `photoAttachment.uri` | Required before navigation from PHOTO step | Toast "Photo is mandatory" for 3 seconds |
| `photoAttachment.status` | Must be CONFIRMED for valid photo | LOADING blocks navigation, ERROR clears state |
| File format | JPG, PNG, GIF, WEBP only | Handled by Photo Picker filter |
| File size | No limit (per spec assumptions) | N/A |

## Persistence

### ReportMissingFlowState Integration

Photo data stored in existing `ReportMissingFlowState` (nav graph scoped), consistent with chip number step.

```kotlin
// Extend existing FlowData
data class FlowData(
    val chipNumber: String = "",
    val photoUri: String? = null,           // Existing
    val photoFilename: String? = null,      // NEW
    val photoSizeBytes: Long = 0,           // NEW
    val description: String = "",
    val contactEmail: String = "",
    val contactPhone: String = "",
)

// Extend existing ReportMissingFlowState
class ReportMissingFlowState {
    // ... existing methods
    
    /** Update photo data (Step 2/4) */
    fun updatePhoto(uri: String?, filename: String?, sizeBytes: Long) {
        _data.update { it.copy(
            photoUri = uri,
            photoFilename = filename,
            photoSizeBytes = sizeBytes
        ) }
    }
    
    /** Clear photo data */
    fun clearPhoto() {
        _data.update { it.copy(
            photoUri = null,
            photoFilename = null,
            photoSizeBytes = 0
        ) }
    }
}
```

### Draft Lifecycle

| Event | Behavior |
|-------|----------|
| Configuration change (rotation) | State preserved via ViewModel |
| Navigate between steps (forward) | State preserved via ReportMissingFlowState |
| Navigate back (any step) | State preserved - NO clearing on back navigation |
| Process death | State LOST (consistent with chip number step) |
| App force close | State LOST (acceptable for draft data) |
| Exit flow (leave nav graph) | Clear draft state via `flowState.clear()` |
| Flow completion (submit) | Clear draft state via `flowState.clear()` |

## Test Data

### Preview States

```kotlin
class PhotoAttachmentStateProvider : PreviewParameterProvider<PhotoAttachmentState> {
    override val values = sequenceOf(
        // Empty state
        PhotoAttachmentState.Empty,
        
        // Loading state
        PhotoAttachmentState(
            uri = "content://media/picker/0/photo/1",
            status = PhotoStatus.LOADING
        ),
        
        // Confirmed with short filename
        PhotoAttachmentState(
            uri = "content://media/picker/0/photo/1",
            filename = "dog_photo.jpg",
            sizeBytes = 1_234_567,
            status = PhotoStatus.CONFIRMED
        ),
        
        // Confirmed with long filename (truncation test)
        PhotoAttachmentState(
            uri = "content://media/picker/0/photo/2",
            filename = "my_missing_pet_photo_2024.jpg",
            sizeBytes = 5_678_901,
            status = PhotoStatus.CONFIRMED
        ),
        
        // Error state (displays as empty)
        PhotoAttachmentState(
            status = PhotoStatus.ERROR
        )
    )
}
```

