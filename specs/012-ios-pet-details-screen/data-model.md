# Data Model: Pet Details Screen (iOS UI)

**Feature**: 012-ios-pet-details-screen  
**Date**: November 24, 2025  
**Platform**: iOS (Swift)

## Overview

This document defines the domain model for the Pet Details Screen feature. The model is specific to the iOS platform and follows Swift conventions (structs, optionals, native types).

## Domain Entities

### PetDetails

Represents comprehensive information about a pet for display on the details screen.

**Location**: `/iosApp/iosApp/Domain/Models/PetDetails.swift`

**Definition**:

```swift
/// Represents comprehensive pet information displayed on the details screen.
struct PetDetails: Identifiable, Codable {
    // MARK: - Required Fields
    
    /// Unique identifier for the pet
    let id: String
    
    /// URL string for the pet's photo
    let photoUrl: String
    
    /// Status of the pet report (ACTIVE, FOUND, or CLOSED from API)
    /// Note: ViewModel maps ACTIVE → "MISSING" for display
    let status: String
    
    /// Date when the pet was last seen (ISO 8601 format from API)
    let lastSeenDate: String
    
    /// Species of the pet (e.g., "Dog", "Cat")
    let species: String
    
    /// Sex of the pet ("male" or "female")
    let gender: String
    
    // MARK: - Contact Information (at least one required)
    
    /// Owner's phone number (optional, but at least one of phone/email required)
    let phone: String?
    
    /// Owner's email address (optional, but at least one of phone/email required)
    let email: String?
    
    // MARK: - Optional Fields
    
    /// Breed of the pet (optional)
    let breed: String?
    
    /// Microchip number (optional, will be mocked until API provides)
    let microchipNumber: String?
    
    /// Approximate age of the pet (optional, will be mocked until API provides)
    let approximateAge: String?
    
    /// Reward amount text (optional, will be mocked until API provides)
    let reward: String?
    
    /// City where pet was last seen (optional)
    let location: String?
    
    /// Search radius around location (optional, e.g., "15 km")
    let locationRadius: String?
    
    /// Additional description text (optional, multi-line)
    let description: String?
}
```

**Field Mapping from API** (`GET /api/v1/announcements`):

| Domain Field | API Field | Notes |
|--------------|-----------|-------|
| `id` | `id` | String identifier |
| `photoUrl` | `photoUrl` | String URL |
| `status` | `status` | Values: ACTIVE, FOUND, CLOSED |
| `lastSeenDate` | `lastSeenDate` | ISO 8601 string |
| `species` | `species` | String |
| `gender` | `gender` | String ("male" or "female") |
| `phone` | `phone` | Optional string |
| `email` | `email` | Optional string |
| `breed` | `breed` | Optional string |
| `microchipNumber` | N/A | **MOCKED** (not in current API) |
| `approximateAge` | N/A | **MOCKED** (not in current API) |
| `reward` | N/A | **MOCKED** (not in current API) |
| `location` | `location` | Optional string (city name) |
| `locationRadius` | `locationRadius` | Optional string (e.g., "15 km") |
| `description` | `description` | Optional string |

**Validation Rules**:

- `id`: Must be non-empty string
- `photoUrl`: Must be valid URL string (validated at ViewModel layer)
- `status`: Must be one of: "ACTIVE", "FOUND", "CLOSED" (ViewModel maps ACTIVE → "MISSING" for display)
- `lastSeenDate`: ISO 8601 date string (ViewModel formats to "MMM DD, YYYY")
- `species`: Must be non-empty string
- `gender`: Must be "male" or "female" (ViewModel maps to symbols ♂/♀)
- `phone` OR `email`: At least one must be non-nil (business rule validated at ViewModel layer)
- `microchipNumber`: If present, formatted as "000-000-000-000" (ViewModel formats)
- Optional fields: Can be `nil`, display "—" as fallback in UI

**State Transitions**: N/A (read-only data model, no state changes in this feature)

---

## Supporting Types

### PetDetailsUiState

Represents the UI state for the Pet Details Screen (used in ViewModel).

**Location**: `/iosApp/iosApp/ViewModels/PetDetailsViewModel.swift` (nested enum)

**Definition**:

```swift
/// UI state for the Pet Details Screen
enum PetDetailsUiState: Equatable {
    /// Initial state or loading data
    case loading
    
    /// Successfully loaded pet details
    case loaded(PetDetails)
    
    /// Failed to load pet details
    case error(String)
}
```

**State transitions**:
- `loading` → `loaded(petDetails)` (success)
- `loading` → `error(message)` (failure)
- `error(message)` → `loading` (retry)

---

### PetPhotoWithBadgesModel

Configuration model for the reusable pet photo component.

**Location**: `/iosApp/iosApp/Views/Components/PetPhotoWithBadges.swift` (nested struct via extension)

**Definition**:

```swift
/// Model for configuring the pet photo with overlaid badges
struct PetPhotoWithBadgesModel: Equatable {
    /// URL string for the pet photo
    let imageUrl: String
    
    /// Status text for the badge ("MISSING", "FOUND", or "CLOSED")
    let status: String
    
    /// Optional reward text (nil if no reward)
    let rewardText: String?
    
    /// Convenience initializer mapping from PetDetails
    init(from petDetails: PetDetails) {
        self.imageUrl = petDetails.photoUrl
        // Map ACTIVE → MISSING for display
        self.status = petDetails.status == "ACTIVE" ? "MISSING" : petDetails.status
        self.rewardText = petDetails.reward
    }
}
```

---

### LabelValueRowModel

Configuration model for the reusable label-value row component.

**Location**: `/iosApp/iosApp/Views/Components/LabelValueRow.swift` (nested struct via extension)

**Definition**:

```swift
/// Model for configuring a label-value row component
struct LabelValueRowModel: Equatable {
    /// Label text (e.g., "Date of Disappearance")
    let label: String
    
    /// Value text (e.g., "Nov 18, 2025")
    let value: String
    
    /// Optional processor to format value before display
    let valueProcessor: ((String) -> String)?
    
    /// Optional tap handler for interactive values (e.g., phone, email)
    let onTap: (() -> Void)?
    
    init(
        label: String,
        value: String,
        valueProcessor: ((String) -> String)? = nil,
        onTap: (() -> Void)? = nil
    ) {
        self.label = label
        self.value = value
        self.valueProcessor = valueProcessor
        self.onTap = onTap
    }
    
    // Equatable conformance (ignore closures)
    static func == (lhs: LabelValueRowModel, rhs: LabelValueRowModel) -> Bool {
        lhs.label == rhs.label && lhs.value == rhs.value
    }
}
```

---

## Relationships

```
PetDetailsUiState
    ├── .loading (no data)
    ├── .loaded(PetDetails) ──┐
    └── .error(String)        │
                              │
                              │ maps to ↓
                              │
                    ┌─────────┴─────────┐
                    │                   │
        PetPhotoWithBadgesModel   LabelValueRowModel (multiple instances)
              (1 instance)              (one per field)
```

**Key relationships**:
- `PetDetailsViewModel` exposes `@Published var state: PetDetailsUiState`
- When state is `.loaded(petDetails)`, view maps `PetDetails` to component models:
  - One `PetPhotoWithBadgesModel` for hero image section
  - Multiple `LabelValueRowModel` instances for each information field

---

## Mock Data Strategy

Until backend endpoint `GET /api/v1/announcements/:id` is available, `PetRepositoryImpl` returns hardcoded mock data:

**Mock `PetDetails` example**:

```swift
PetDetails(
    id: "mock-pet-123",
    photoUrl: "https://example.com/pet-photo.jpg",
    status: "ACTIVE",
    lastSeenDate: "2025-11-18T10:30:00Z",
    species: "Dog",
    gender: "male",
    phone: "+48 123 456 789",
    email: "owner@example.com",
    breed: "Golden Retriever",
    microchipNumber: "123-456-789-012", // Mocked field
    approximateAge: "3 years", // Mocked field
    reward: "500 PLN", // Mocked field
    location: "Warsaw",
    locationRadius: "15 km",
    description: "Friendly golden retriever, responds to 'Max'. Last seen near Lazienki Park wearing a red collar."
)
```

**When backend endpoint is ready**:
- Update `PetRepositoryImpl` to call `GET /api/v1/announcements/:id`
- Parse JSON response to `PetDetails` model (Codable conformance)
- Remove mock data logic
- Fields not available from API (`microchipNumber`, `approximateAge`, `reward`) remain `nil` until backend adds them

---

## Testing Considerations

**Unit tests should verify**:
- `PetDetailsViewModel` state transitions: loading → loaded/error
- `PetPhotoWithBadgesModel` convenience init maps ACTIVE → MISSING
- `LabelValueRowModel` equality check (ignores closures)
- Mock repository returns expected `PetDetails` structure
- Error handling when repository throws

**Example test cases**:
- `testLoadPetDetails_whenRepositorySucceeds_shouldUpdateStateToLoaded()`
- `testLoadPetDetails_whenRepositoryFails_shouldUpdateStateToError()`
- `testRetry_whenInErrorState_shouldTransitionToLoading()`
- `testPetPhotoWithBadgesModel_whenStatusIsActive_shouldMapToMissing()`

---

## Summary

- **Primary entity**: `PetDetails` struct with required and optional fields
- **UI state**: `PetDetailsUiState` enum (loading/loaded/error)
- **Component models**: `PetPhotoWithBadgesModel`, `LabelValueRowModel`
- **Mock strategy**: Hardcoded data in repository until backend endpoint available
- **Testing focus**: ViewModel state transitions, model mapping, error handling

Next phase: Generate API contracts (mock JSON structure) and quickstart guide.

