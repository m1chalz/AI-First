# Research: iOS Send Report Data to Backend (Status by Flow)

**Branch**: `KAN-34-send-data-to-backend` | **Date**: 2026-01-09

## Research Tasks

### 1. Current Status Handling Implementation

**Question**: Where is status hardcoded and how does the data flow from UI to backend?

**Findings**:

The status is hardcoded in `CreateAnnouncementMapper.toDTO()` at line 29:

```swift
// iosApp/iosApp/Data/Mappers/CreateAnnouncementMapper.swift
func toDTO(_ data: CreateAnnouncementData) -> CreateAnnouncementRequestDTO {
    // ...
    return CreateAnnouncementRequestDTO(
        // ...
        status: AnnouncementStatusDTO(domain: .active),  // ← HARDCODED HERE
        // ...
    )
}
```

**Data Flow**:
1. User completes flow → ViewModels update `*FlowState`
2. User taps Submit → ViewModel calls `AnnouncementSubmissionService.submitAnnouncement(flowState:)`
3. Service calls `buildAnnouncementData(from: flowState)` → Creates `CreateAnnouncementData` (no status field!)
4. Service calls `repository.createAnnouncement(data:)`
5. Repository calls `mapper.toDTO(data)` → Hardcodes status to `.active`
6. Repository sends JSON to backend

**Decision**: Add `status` field to `CreateAnnouncementData` domain model and pass it through the entire chain.

---

### 2. Backend API Status Requirements

**Question**: What status values does the backend accept? Is schema strict?

**Findings**:

From `server/src/lib/announcement-validation.ts`:

```typescript
status: z.enum(['MISSING', 'FOUND'], {
  errorMap: () => ({ message: 'status must be either MISSING or FOUND' })
})
```

The schema uses `.strict()` which rejects unknown fields.

**Required fields** (from backend validation):
- `species` (string)
- `sex` (string) 
- `lastSeenDate` (string, YYYY-MM-DD format)
- `status` (string, "MISSING" or "FOUND")
- `locationLatitude` (number, -90 to 90)
- `locationLongitude` (number, -180 to 180)
- At least one of: `email` OR `phone`

**Optional fields**:
- `petName`, `breed`, `age`, `description`, `microchipNumber`, `reward`

**Decision**: Backend already supports both statuses. No backend changes required. iOS must send:
- `"MISSING"` for Missing Pet flow (iOS domain: `.active`)
- `"FOUND"` for Found Pet flow (iOS domain: `.found`)

---

### 3. iOS Domain Status Mapping

**Question**: How do iOS domain statuses map to backend values?

**Findings**:

From `iosApp/iosApp/Data/Models/AnnouncementStatusDTO.swift`:

```swift
enum AnnouncementStatusDTO: String, Codable {
    case missing = "MISSING"
    case found = "FOUND"
    case closed = "CLOSED"
    
    init(domain: AnnouncementStatus) {
        switch domain {
        case .active: self = .missing  // ← iOS .active = Backend MISSING
        case .found: self = .found     // ← iOS .found = Backend FOUND
        case .closed: self = .closed
        }
    }
}
```

And from `iosApp/iosApp/Domain/Models/AnnouncementStatus.swift`:

```swift
enum AnnouncementStatus: String, Codable {
    case active = "ACTIVE"
    case found = "FOUND"
    case closed = "CLOSED"
}
```

**Decision**: 
- Missing flow should set `status = .active` (maps to "MISSING")
- Found flow should set `status = .found` (maps to "FOUND")

Existing mapper `AnnouncementStatusDTO(domain:)` handles conversion correctly.

---

### 4. Flow State Protocol Design

**Question**: How should status be added to PetReportFlowStateProtocol?

**Findings**:

Current protocol (`iosApp/iosApp/Features/ReportMissingAndFoundPet/Common/Models/PetReportFlowStateProtocol.swift`) does not include status.

Both `MissingPetReportFlowState` and `FoundPetReportFlowState` conform to this protocol.

**Alternatives Considered**:

| Option | Pros | Cons |
|--------|------|------|
| A. Add `status` to protocol as computed property | Type-safe, each flow returns correct value | Requires protocol change |
| B. Pass status as parameter to service | No protocol change | Service needs to know flow type |
| C. Add `status` as stored property | Flexible, can be changed | Could be set incorrectly |

**Decision**: **Option A** - Add `status` as a computed property (`{ get }`) to `PetReportFlowStateProtocol`. Each concrete flow state returns its correct status:
- `MissingPetReportFlowState.status` returns `.active`
- `FoundPetReportFlowState.status` returns `.found`

This is the cleanest solution because:
1. Flow determines status at compile-time (can't be wrong)
2. No risk of forgetting to set status
3. Follows existing pattern of protocol providing data to service

---

### 5. Payload Field Validation

**Question**: Does iOS send any fields that backend would reject?

**Findings**:

Comparing `CreateAnnouncementRequestDTO` fields with backend schema:

| iOS DTO Field | Backend Schema | Status |
|---------------|----------------|--------|
| species | required | ✓ |
| breed | optional | ✓ |
| sex | required | ✓ |
| age | optional | ✓ |
| lastSeenDate | required | ✓ |
| locationLatitude | required | ✓ |
| locationLongitude | required | ✓ |
| email | required (one of) | ✓ |
| phone | required (one of) | ✓ |
| status | required | ✓ |
| microchipNumber | optional | ✓ |
| petName | optional | ✓ |
| description | optional | ✓ |
| reward | optional | ✓ |

**Decision**: All iOS DTO fields match backend schema. No unknown fields. iOS requires BOTH email AND phone (stricter than backend's "at least one"), which is fine.

---

## Summary of Decisions

| Topic | Decision | Rationale |
|-------|----------|-----------|
| Status location | Add to `CreateAnnouncementData` | Flow through domain layer properly |
| Protocol change | Add `status { get }` to `PetReportFlowStateProtocol` | Compile-time safety |
| Missing flow status | Return `.active` | Maps to "MISSING" per existing DTO mapper |
| Found flow status | Return `.found` | Maps to "FOUND" per existing DTO mapper |
| Mapper change | Use `data.status` instead of hardcoded `.active` | Dynamic status based on flow |
| Backend changes | None required | API already supports both statuses |

## Files Requiring Changes

1. `CreateAnnouncementData.swift` - Add `status: AnnouncementStatus` property
2. `PetReportFlowStateProtocol.swift` - Add `var status: AnnouncementStatus { get }`
3. `MissingPetReportFlowState.swift` - Add computed `var status` returning `.active`
4. `FoundPetReportFlowState.swift` - Add computed `var status` returning `.found`
5. `AnnouncementSubmissionService.swift` - Pass `flowState.status` to `CreateAnnouncementData`
6. `CreateAnnouncementMapper.swift` - Use `data.status` in `toDTO()` method

## Test Files Requiring Updates

1. `CreateAnnouncementMapperTests.swift` - Test status mapping for both values
2. `AnnouncementSubmissionServiceTests.swift` - Test that correct status is passed based on flow

