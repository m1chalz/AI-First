# API Contracts: iOS Align Found Flow

**Branch**: `KAN-34-ios-align-found-flow` | **Date**: 2026-01-09

## No Backend API Changes

This feature is **iOS-only** and does not require any backend API changes.

### Reasoning

Per spec requirements (FR-016):
> "On successful submission, the iOS app MUST NOT include the new optional fields (caregiver phone number, current physical address) in the backend payload (iOS-only fields); they are captured for UI purposes and kept in the in-progress state."

### Existing API Used

The Found Pet flow continues to use the existing announcement submission endpoint:

```
POST /api/announcements
```

With the existing payload structure (no changes):

```json
{
  "type": "found",
  "photo": "<base64 or multipart>",
  "chipNumber": "123456789012345",
  "disappearanceDate": "2026-01-09",
  "species": "dog",
  "race": "Golden Retriever",
  "gender": "male",
  "age": 3,
  "latitude": 52.2297,
  "longitude": 21.0122,
  "additionalDescription": "Brown collar, friendly",
  "contactPhone": "+48123456789",
  "contactEmail": "finder@example.com",
  "rewardDescription": null
}
```

### iOS-Only Fields

The following fields exist only in `FoundPetReportFlowState` for UI purposes and are **NOT** sent to backend:

| Field | Purpose |
|-------|---------|
| `caregiverPhoneNumber` | Contact for animal's current caregiver (different from finder) |
| `currentPhysicalAddress` | Address where the found animal is currently located |

These fields are:
- Captured in the iOS flow state
- Displayed during the active wizard session
- Cleared when the flow exits
- Never transmitted to the backend API

