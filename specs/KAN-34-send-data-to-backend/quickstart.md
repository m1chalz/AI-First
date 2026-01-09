# Quickstart: iOS Send Report Data to Backend (Status by Flow)

**Branch**: `KAN-34-send-data-to-backend` | **Date**: 2026-01-09

## Prerequisites

- Xcode with iOS 18+ SDK
- iPhone 16 Simulator
- Running backend server (optional for manual testing)

## Implementation Steps

### Step 1: Add `status` to `CreateAnnouncementData`

**File**: `iosApp/iosApp/Domain/Models/CreateAnnouncementData.swift`

Add the `status` field to the domain model:

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
    let status: AnnouncementStatus  // ← ADD THIS
}
```

---

### Step 2: Add `status` to `PetReportFlowStateProtocol`

**File**: `iosApp/iosApp/Features/ReportMissingAndFoundPet/Common/Models/PetReportFlowStateProtocol.swift`

Add status property requirement to the protocol:

```swift
@MainActor
protocol PetReportFlowStateProtocol: AnyObject {
    // ... existing properties ...
    
    /// Status to be sent to backend: .active for Missing flow, .found for Found flow
    var status: AnnouncementStatus { get }
}
```

---

### Step 3: Implement `status` in `MissingPetReportFlowState`

**File**: `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportMissingPet/Models/MissingPetReportFlowState.swift`

Add computed property:

```swift
// MARK: - Status

var status: AnnouncementStatus {
    .active
}
```

---

### Step 4: Implement `status` in `FoundPetReportFlowState`

**File**: `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Models/FoundPetReportFlowState.swift`

Add computed property:

```swift
// MARK: - Status

var status: AnnouncementStatus {
    .found
}
```

---

### Step 5: Update `AnnouncementSubmissionService`

**File**: `iosApp/iosApp/Domain/Services/AnnouncementSubmissionService.swift`

Update `buildAnnouncementData(from:)` to include status:

```swift
private func buildAnnouncementData(from flowState: PetReportFlowStateProtocol) async throws -> CreateAnnouncementData {
    // ... existing validation ...
    
    return CreateAnnouncementData(
        species: species,
        breed: flowState.animalRace,
        sex: gender,
        age: flowState.animalAge,
        lastSeenDate: disappearanceDate,
        location: (
            latitude: flowState.animalLatitude ?? 0.0,
            longitude: flowState.animalLongitude ?? 0.0
        ),
        contact: (
            email: contactDetails.email.trimmingCharacters(in: .whitespaces),
            phone: contactDetails.phone.filter { $0.isNumber || $0 == "+" }
        ),
        microchipNumber: flowState.chipNumber,
        petName: flowState.petName,
        description: flowState.animalAdditionalDescription,
        reward: contactDetails.rewardDescription,
        status: flowState.status  // ← ADD THIS
    )
}
```

---

### Step 6: Update `CreateAnnouncementMapper`

**File**: `iosApp/iosApp/Data/Mappers/CreateAnnouncementMapper.swift`

Change hardcoded status to use `data.status`:

```swift
func toDTO(_ data: CreateAnnouncementData) -> CreateAnnouncementRequestDTO {
    let dateFormatter = ISO8601DateFormatter()
    dateFormatter.formatOptions = [.withFullDate]
    
    return CreateAnnouncementRequestDTO(
        species: AnimalSpeciesDTO(domain: data.species),
        breed: data.breed,
        sex: AnimalGenderDTO(domain: data.sex),
        age: data.age,
        lastSeenDate: dateFormatter.string(from: data.lastSeenDate),
        locationLatitude: data.location.latitude,
        locationLongitude: data.location.longitude,
        email: data.contact.email,
        phone: data.contact.phone,
        status: AnnouncementStatusDTO(domain: data.status),  // ← CHANGE THIS (was .active)
        microchipNumber: data.microchipNumber,
        petName: data.petName,
        description: data.description,
        reward: data.reward
    )
}
```

---

## Testing

### Unit Tests to Update

1. **CreateAnnouncementMapperTests.swift**
   - Add test for `.active` → `"MISSING"` mapping
   - Add test for `.found` → `"FOUND"` mapping

2. **AnnouncementSubmissionServiceTests.swift**
   - Update fake flow states to include `status` property
   - Verify correct status is passed through

### Run Tests

```bash
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES
```

### Manual Verification

1. Start backend server: `cd server && npm run dev`
2. Build and run iOS app in Simulator
3. Complete Missing Pet flow → Verify backend receives `"MISSING"` status
4. Complete Found Pet flow → Verify backend receives `"FOUND"` status

Check backend logs or database to confirm status values.

---

## Verification Checklist

- [ ] `CreateAnnouncementData` has `status` field
- [ ] `PetReportFlowStateProtocol` has `status { get }` property
- [ ] `MissingPetReportFlowState.status` returns `.active`
- [ ] `FoundPetReportFlowState.status` returns `.found`
- [ ] `AnnouncementSubmissionService` passes `flowState.status` to data model
- [ ] `CreateAnnouncementMapper.toDTO()` uses `data.status` (not hardcoded)
- [ ] All unit tests pass
- [ ] Coverage remains ≥80%
- [ ] Missing flow submits with "MISSING" status
- [ ] Found flow submits with "FOUND" status

