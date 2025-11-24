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
    
    /// Name of the pet (from backend, not displayed in current UI design)
    let petName: String
    
    /// URL string for the pet's photo (nullable, show fallback if nil)
    let photoUrl: String?
    
    /// Status of the pet report (ACTIVE, FOUND, or CLOSED from API)
    /// Note: ViewModel maps ACTIVE → "MISSING" for display
    let status: String
    
    /// Date when the pet was last seen (YYYY-MM-DD format from API)
    let lastSeenDate: String
    
    /// Species of the pet (e.g., "DOG", "CAT")
    let species: String
    
    /// Sex of the pet (MALE, FEMALE, or UNKNOWN - uppercase from backend)
    let gender: String
    
    /// Additional description text (required in backend, multi-line)
    let description: String
    
    /// City where pet was last seen (required in backend)
    let location: String
    
    /// Owner's phone number (required in backend)
    let phone: String
    
    // MARK: - Optional Fields
    
    /// Owner's email address (optional)
    let email: String?
    
    /// Breed of the pet (optional)
    let breed: String?
    
    /// Search radius around location in kilometers (optional, number from backend)
    /// Note: ViewModel formats to "±X km" for display
    let locationRadius: Int?
    
    /// Microchip number (optional, will be mocked until backend adds this field)
    let microchipNumber: String?
    
    /// Approximate age of the pet (optional, will be mocked until backend adds this field)
    let approximateAge: String?
    
    /// Reward amount text (optional, will be mocked until backend adds this field)
    let reward: String?
    
    // MARK: - Metadata (not displayed in UI)
    
    /// Timestamp when announcement was created
    let createdAt: String
    
    /// Timestamp when announcement was last updated
    let updatedAt: String
}
```

**Field Mapping from API** (`GET /api/v1/announcements/:id`):

| Domain Field | API Field | Notes |
|--------------|-----------|-------|
| `id` | `id` | String identifier |
| `petName` | `petName` | String (not displayed in UI) |
| `photoUrl` | `photoUrl` | String URL (nullable) |
| `status` | `status` | Values: ACTIVE, FOUND, CLOSED |
| `lastSeenDate` | `lastSeenDate` | YYYY-MM-DD format (e.g., "2025-11-18") |
| `species` | `species` | String (uppercase: DOG, CAT, BIRD, RABBIT, OTHER) |
| `gender` | `gender` | String (uppercase: MALE, FEMALE, UNKNOWN) |
| `description` | `description` | String (required, multi-line) |
| `location` | `location` | String (required, city name) |
| `phone` | `phone` | String (required) |
| `email` | `email` | Optional string |
| `breed` | `breed` | Optional string |
| `locationRadius` | `locationRadius` | Optional number (kilometers) |
| `microchipNumber` | N/A | **MOCKED** (not in current API) |
| `approximateAge` | N/A | **MOCKED** (not in current API) |
| `reward` | N/A | **MOCKED** (not in current API) |
| `createdAt` | `createdAt` | ISO 8601 timestamp |
| `updatedAt` | `updatedAt` | ISO 8601 timestamp |

**Validation Rules**:

- `id`: Must be non-empty string (UUID format from backend)
- `petName`: Must be non-empty string (not displayed in current UI)
- `photoUrl`: Optional; if nil, ViewModel displays "Image not available" fallback
- `status`: Must be one of: "ACTIVE", "FOUND", "CLOSED" (ViewModel maps ACTIVE → "MISSING" for display)
- `lastSeenDate`: YYYY-MM-DD format (ViewModel formats to "MMM DD, YYYY")
- `species`: Must be non-empty string (uppercase: DOG, CAT, BIRD, RABBIT, OTHER)
- `gender`: Must be one of: "MALE", "FEMALE", "UNKNOWN" (ViewModel maps to symbols ♂/♀/?)
- `description`: Must be non-empty string (required in backend)
- `location`: Must be non-empty string (required in backend)
- `phone`: Must be non-empty string (required in backend)
- `email`: Optional string
- `locationRadius`: Optional integer; ViewModel formats to "±X km" for display
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

## Backend Integration

**Backend endpoint `GET /api/v1/announcements/:id` is already implemented!** ✅

In **Phase 1 (mock-first)**, the iOS implementation of `AnimalRepositoryProtocol` (`AnimalRepository`) will return hardcoded `PetDetails` instances matching this contract structure via a new `getPetDetails(id: String)` method, without making network calls. In **Phase 2**, the same `AnimalRepository` implementation will call the real backend endpoint:

**Real API response example**:

```swift
PetDetails(
    id: "11111111-1111-1111-1111-111111111111",
    petName: "Fredi Kamionka Gmina Burzenin",
    photoUrl: "https://images.dog.ceo/breeds/terrier-yorkshire/n02094433_1010.jpg",
    status: "ACTIVE",
    lastSeenDate: "2025-11-18",
    species: "DOG",
    gender: "MALE",
    description: "Zaginął piesek York wabi się Fredi Kamionka gmina burzenin",
    location: "Kamionka",
    phone: "+48 123 456 789",
    email: "spotterka@example.pl",
    breed: "York",
    locationRadius: 5,
    microchipNumber: nil, // Not yet in backend
    approximateAge: nil, // Not yet in backend
    reward: nil, // Not yet in backend
    createdAt: "2025-11-19T15:47:14.000Z",
    updatedAt: "2025-11-19T15:47:14.000Z"
)
```

**Implementation approach**:
- `AnimalRepository` (conforming to `AnimalRepositoryProtocol`) calls `GET /api/v1/announcements/:id` via HTTP client
- Parse JSON response using `Codable` conformance
- Handle errors: 404 (pet not found), 500 (server error), network errors
- Fields not available from backend (`microchipNumber`, `approximateAge`, `reward`) remain `nil` until backend adds them
- ViewModel handles nil values with fallback "—" in UI

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
- **Mock strategy**: Hardcoded data in repository for Phase 1 (endpoint already exists; integration deferred to Phase 2)
- **Testing focus**: ViewModel state transitions, model mapping, error handling

Next phase: Generate API contracts (mock JSON structure) and quickstart guide.

