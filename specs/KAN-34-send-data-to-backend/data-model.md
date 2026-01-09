# Data Model: iOS Send Report Data to Backend (Status by Flow)

**Branch**: `KAN-34-send-data-to-backend` | **Date**: 2026-01-09

## Entity Changes

### 1. CreateAnnouncementData (Domain Model)

**File**: `iosApp/iosApp/Domain/Models/CreateAnnouncementData.swift`

**Change Type**: ADD field

**Before**:
```swift
struct CreateAnnouncementData {
    let species: AnimalSpecies
    let breed: String?
    let sex: AnimalGender
    let age: Int?
    let lastSeenDate: Date
    let location: (latitude: Double, longitude: Double)
    let contact: (email: String, phone: String)
    let microchipNumber: String?
    let petName: String?
    let description: String?
    let reward: String?
}
```

**After**:
```swift
struct CreateAnnouncementData {
    let species: AnimalSpecies
    let breed: String?
    let sex: AnimalGender
    let age: Int?
    let lastSeenDate: Date
    let location: (latitude: Double, longitude: Double)
    let contact: (email: String, phone: String)
    let microchipNumber: String?
    let petName: String?
    let description: String?
    let reward: String?
    let status: AnnouncementStatus  // ← NEW FIELD
}
```

**Validation Rules**:
- `status` is required (non-optional)
- Must be one of: `.active` (for Missing flow) or `.found` (for Found flow)

---

### 2. PetReportFlowStateProtocol (Protocol)

**File**: `iosApp/iosApp/Features/ReportMissingAndFoundPet/Common/Models/PetReportFlowStateProtocol.swift`

**Change Type**: ADD property requirement

**Before**:
```swift
@MainActor
protocol PetReportFlowStateProtocol: AnyObject {
    // ... existing properties ...
    var contactDetails: OwnerContactDetails? { get }
    var managementPassword: String? { get set }
}
```

**After**:
```swift
@MainActor
protocol PetReportFlowStateProtocol: AnyObject {
    // ... existing properties ...
    var contactDetails: OwnerContactDetails? { get }
    var managementPassword: String? { get set }
    
    /// Status to be sent to backend: .active for Missing flow, .found for Found flow
    var status: AnnouncementStatus { get }  // ← NEW PROPERTY
}
```

**Contract**:
- Computed property (read-only)
- Each concrete implementation returns its flow-specific status

---

### 3. MissingPetReportFlowState (Concrete Implementation)

**File**: `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportMissingPet/Models/MissingPetReportFlowState.swift`

**Change Type**: ADD computed property

**Addition**:
```swift
// MARK: - Status

/// Returns .active status for Missing Pet flow (maps to "MISSING" in backend)
var status: AnnouncementStatus {
    .active
}
```

---

### 4. FoundPetReportFlowState (Concrete Implementation)

**File**: `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Models/FoundPetReportFlowState.swift`

**Change Type**: ADD computed property

**Addition**:
```swift
// MARK: - Status

/// Returns .found status for Found Pet flow (maps to "FOUND" in backend)
var status: AnnouncementStatus {
    .found
}
```

---

## Relationships

```
┌─────────────────────────────────────┐
│  PetReportFlowStateProtocol         │
│  + status: AnnouncementStatus {get} │
└────────────────┬────────────────────┘
                 │ conforms to
        ┌────────┴────────┐
        │                 │
        ▼                 ▼
┌───────────────────┐  ┌──────────────────┐
│ MissingPetReport  │  │ FoundPetReport   │
│ FlowState         │  │ FlowState        │
│ status = .active  │  │ status = .found  │
└────────┬──────────┘  └────────┬─────────┘
         │                      │
         └──────────┬───────────┘
                    │ used by
                    ▼
         ┌─────────────────────────────┐
         │ AnnouncementSubmissionService│
         │ buildAnnouncementData()      │
         └──────────────┬──────────────┘
                        │ creates
                        ▼
         ┌─────────────────────────────┐
         │ CreateAnnouncementData      │
         │ + status: AnnouncementStatus│
         └──────────────┬──────────────┘
                        │ mapped by
                        ▼
         ┌─────────────────────────────┐
         │ CreateAnnouncementMapper    │
         │ toDTO(data) → uses data.status
         └──────────────┬──────────────┘
                        │ creates
                        ▼
         ┌─────────────────────────────┐
         │ CreateAnnouncementRequestDTO│
         │ status: AnnouncementStatusDTO
         └──────────────┬──────────────┘
                        │ sent to
                        ▼
         ┌─────────────────────────────┐
         │ Backend API                 │
         │ status: "MISSING" | "FOUND" │
         └─────────────────────────────┘
```

## State Transitions

No state transitions for this feature. Status is determined by flow type at compile-time:

| Flow Type | iOS Domain Status | Backend Status |
|-----------|-------------------|----------------|
| Missing Pet | `.active` | `"MISSING"` |
| Found Pet | `.found` | `"FOUND"` |

The mapping is fixed and handled by existing `AnnouncementStatusDTO(domain:)` initializer.

