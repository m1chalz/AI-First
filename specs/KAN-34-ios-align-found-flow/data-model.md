# Data Model: iOS Align Found Flow (3-step)

**Branch**: `KAN-34-ios-align-found-flow` | **Date**: 2026-01-09

## Overview

This document defines the data model changes for the 3-step Found Pet flow on iOS.
**No backend changes** - all modifications are iOS-only per spec FR-016.

---

## Entity: FoundPetReportFlowState

Shared state for Found Pet Report flow. Owned by coordinator, injected into all ViewModels.

### Current Fields (No Changes)

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `chipNumber` | `String?` | No | Microchip number (digits-only, 0-15) |
| `photoAttachment` | `PhotoAttachmentMetadata?` | No | Photo attachment metadata |
| `photoStatus` | `PhotoAttachmentStatus` | Yes | Attachment lifecycle state |
| `disappearanceDate` | `Date?` | No | Date animal was found |
| `animalSpecies` | `AnimalSpecies?` | No | Species enum |
| `animalRace` | `String?` | No | Breed/race text |
| `animalGender` | `AnimalGender?` | No | Gender enum |
| `animalAge` | `Int?` | No | Age in years (0-40) |
| `petName` | `String?` | No | Pet name |
| `animalLatitude` | `Double?` | No | Latitude (-90 to 90) |
| `animalLongitude` | `Double?` | No | Longitude (-180 to 180) |
| `animalAdditionalDescription` | `String?` | No | Additional description (max 500) |
| `contactDetails` | `OwnerContactDetails?` | No | Phone, email, reward |
| `managementPassword` | `String?` | No | Post-submission password (unused in new flow - Summary removed) |

### New Fields (iOS-only per FR-015, FR-016)

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `caregiverPhoneNumber` | `String?` | No | Optional caregiver phone (7-11 digits when provided) |
| `currentPhysicalAddress` | `String?` | No | Optional address where animal is located (multiline, max 500) |

### State Diagram

```
┌──────────────────────────────────────────────────────────────────────────┐
│                        FoundPetReportFlowState                           │
├──────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  Step 1: Upload Photo                                                    │
│  ┌─────────────────────────────────────────────────────────────────┐    │
│  │ photoAttachment: PhotoAttachmentMetadata?                        │    │
│  │ photoStatus: PhotoAttachmentStatus (.empty → .ready)             │    │
│  └─────────────────────────────────────────────────────────────────┘    │
│                              │                                           │
│                              ▼                                           │
│  Step 2: Pet Details                                                     │
│  ┌─────────────────────────────────────────────────────────────────┐    │
│  │ disappearanceDate: Date? (required)                              │    │
│  │ animalSpecies: AnimalSpecies? (required)                         │    │
│  │ animalGender: AnimalGender? (required)                           │    │
│  │ animalLatitude: Double? (required for Found flow)                │    │
│  │ animalLongitude: Double? (required for Found flow)               │    │
│  │ chipNumber: String? (optional, moved from Step 1)                │    │
│  │ animalRace: String? (optional)                                   │    │
│  │ animalAge: Int? (optional)                                       │    │
│  │ petName: String? (optional)                                      │    │
│  │ animalAdditionalDescription: String? (optional)                  │    │
│  └─────────────────────────────────────────────────────────────────┘    │
│                              │                                           │
│                              ▼                                           │
│  Step 3: Contact Information                                             │
│  ┌─────────────────────────────────────────────────────────────────┐    │
│  │ contactDetails.phone: String (required)                          │    │
│  │ contactDetails.email: String (required)                          │    │
│  │ contactDetails.rewardDescription: String? (optional)             │    │
│  │ caregiverPhoneNumber: String? (optional, NEW)                    │    │
│  │ currentPhysicalAddress: String? (optional, NEW)                  │    │
│  └─────────────────────────────────────────────────────────────────┘    │
│                              │                                           │
│                              ▼                                           │
│                        exitFlow()                                        │
│                 (flow ends after successful submission)                  │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

### clear() Method Update

```swift
func clear() async {
    chipNumber = nil
    photoAttachment = nil
    photoStatus = .empty
    disappearanceDate = nil
    animalSpecies = nil
    animalRace = nil
    animalGender = nil
    animalAge = nil
    petName = nil
    animalLatitude = nil
    animalLongitude = nil
    animalAdditionalDescription = nil
    contactDetails = nil
    managementPassword = nil  // Keep for backwards compatibility, but unused
    // NEW: Clear iOS-only fields
    caregiverPhoneNumber = nil
    currentPhysicalAddress = nil

    try? await photoAttachmentCache.clearCurrent()
}
```

**Note**: `managementPassword` field remains in FlowState for backwards compatibility with existing code, but is no longer used since Summary screen is removed.

---

## Entity: OwnerContactDetails (No Changes)

Existing struct used for backend submission.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `phone` | `String` | Yes | Finder's phone number |
| `email` | `String` | Yes | Finder's email address |
| `rewardDescription` | `String?` | No | Optional reward text |

**Note**: `caregiverPhoneNumber` and `currentPhysicalAddress` are NOT added here because they are not sent to backend per FR-016.

---

## Validation Rules

### Step 1: Upload Photo

| Field | Rule | Error Message |
|-------|------|---------------|
| `photoStatus` | Must be `.ready` to proceed | "Photo is mandatory" |

### Step 2: Pet Details

| Field | Rule | Error Message |
|-------|------|---------------|
| `disappearanceDate` | Required, ≤ today | N/A (DatePicker enforces) |
| `animalSpecies` | Required, non-nil | "Please select a species" |
| `animalGender` | Required, non-nil | "Please select a gender" |
| `animalLatitude` | Required, -90 to 90 | "Fill in latitude" / "Latitude must be between -90 and 90" |
| `animalLongitude` | Required, -180 to 180 | "Fill in longitude" / "Longitude must be between -180 and 180" |
| `chipNumber` | Optional, 0-15 digits only | N/A (no validation, just input filtering) |
| `animalAge` | Optional, if provided: 0-40 | "Age must be between 0 and 40" |

### Step 3: Contact Information

| Field | Rule | Error Message |
|-------|------|---------------|
| `phone` | Required, 7-11 digits | "Enter at least 7 digits" |
| `email` | Required, valid format | "Enter a valid email address" |
| `caregiverPhoneNumber` | Optional, but if non-empty: 7-11 digits | "Enter at least 7 digits" |
| `currentPhysicalAddress` | Optional, max 500 chars | N/A (hard cap, no error) |

---

## Microchip Number Formatting

Using existing `MicrochipNumberFormatter`:

| Input | Stored Value | Display Value |
|-------|--------------|---------------|
| "" | nil | "" |
| "123" | "123" | "123" |
| "12345" | "12345" | "12345" |
| "123456" | "123456" | "12345-6" |
| "1234567890" | "1234567890" | "12345-67890" |
| "123456789012345" | "123456789012345" | "12345-67890-12345" |
| "1234567890123456" | "123456789012345" | "12345-67890-12345" (capped) |
| "12a34b56" | "123456" | "12345-6" (non-digits filtered) |

---

## Backend Payload Changes

**None**. Per FR-016, the iOS-only fields are NOT included in backend requests.

Existing submission service (`AnnouncementSubmissionServiceProtocol.submitAnnouncement()`) continues to map only:
- Photo attachment
- Chip number
- Disappearance date
- Animal species, race, gender, age
- Location (lat/long)
- Additional description
- Contact details (phone, email, reward)

The new fields (`caregiverPhoneNumber`, `currentPhysicalAddress`) exist only in `FoundPetReportFlowState` for UI purposes.

---

## File Locations

### Modified Files

```
iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/
├── Models/
│   └── FoundPetReportFlowState.swift    # Add caregiverPhoneNumber, currentPhysicalAddress
├── Coordinators/
│   └── FoundPetReportCoordinator.swift  # Restructure 4→3 steps
└── Views/
    ├── Photo/
    │   ├── FoundPetPhotoView.swift       # Update copy
    │   └── FoundPetPhotoViewModel.swift  # Step 1/3
    └── ContactDetails/
        ├── FoundPetContactDetailsView.swift    # Add new fields
        └── FoundPetContactDetailsViewModel.swift # Handle new fields
```

### New Files

```
iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/
└── Views/
    └── PetDetails/
        ├── FoundPetPetDetailsView.swift      # New step 2/3
        └── FoundPetPetDetailsViewModel.swift # New ViewModel
```

### Deleted Files

```
iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/
└── Views/
    ├── ChipNumber/
    │   ├── FoundPetChipNumberView.swift       # Delete
    │   └── FoundPetChipNumberViewModel.swift  # Delete
    ├── AnimalDescription/
    │   ├── FoundPetAnimalDescriptionView.swift       # Delete
    │   └── FoundPetAnimalDescriptionViewModel.swift  # Delete
    └── Summary/
        ├── FoundPetSummaryView.swift              # Delete (Summary removed)
        ├── FoundPetSummaryView+Constants.swift    # Delete
        └── FoundPetSummaryViewModel.swift         # Delete
```

---

## Test Data Examples

### Valid Complete Flow State

```swift
let validState = FoundPetReportFlowState(photoAttachmentCache: cache)
validState.photoStatus = .ready
validState.photoAttachment = PhotoAttachmentMetadata(/* ... */)
validState.disappearanceDate = Date()
validState.animalSpecies = .dog
validState.animalGender = .male
validState.animalLatitude = 52.2297
validState.animalLongitude = 21.0122
validState.chipNumber = "123456789012345"
validState.contactDetails = OwnerContactDetails(
    phone: "+48123456789",
    email: "finder@example.com",
    rewardDescription: nil
)
validState.caregiverPhoneNumber = "+48987654321"
validState.currentPhysicalAddress = "123 Main Street, Warsaw, Poland"
```

### Minimal Valid Flow State

```swift
let minimalState = FoundPetReportFlowState(photoAttachmentCache: cache)
minimalState.photoStatus = .ready
minimalState.photoAttachment = PhotoAttachmentMetadata(/* ... */)
minimalState.disappearanceDate = Date()
minimalState.animalSpecies = .cat
minimalState.animalGender = .female
minimalState.animalLatitude = 50.0647
minimalState.animalLongitude = 19.9450
minimalState.contactDetails = OwnerContactDetails(
    phone: "1234567",
    email: "test@test.com",
    rewardDescription: nil
)
// chipNumber: nil
// caregiverPhoneNumber: nil
// currentPhysicalAddress: nil
```

