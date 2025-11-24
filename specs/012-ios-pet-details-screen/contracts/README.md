# API Contracts: Pet Details Screen

**Feature**: 012-ios-pet-details-screen  
**Date**: November 24, 2025  
**Status**: Mock Contract (Backend endpoint not yet implemented)

## Overview

This directory contains API contract definitions for the Pet Details Screen feature. Since the backend endpoint `GET /api/v1/announcements/:id` does not exist yet, these contracts define the expected structure for mock data used by `PetRepositoryImpl`.

## Contracts

### pet-details-response.json

**Endpoint** (future): `GET /api/v1/announcements/:id`

**Description**: Retrieves comprehensive details for a single pet by ID.

**Mock Implementation**: Until the backend endpoint is implemented, `PetRepositoryImpl` (iOS) returns hardcoded data matching this structure.

**Request**:
- Method: `GET`
- URL: `/api/v1/announcements/:id`
- Path Parameters:
  - `id` (string, required): Unique pet identifier
- Headers:
  - `Content-Type: application/json`

**Response** (200 OK):
```json
{
  "id": "mock-pet-123",
  "photoUrl": "https://example.com/pets/mock-pet-123.jpg",
  "status": "ACTIVE",
  "lastSeenDate": "2025-11-18T10:30:00Z",
  "species": "Dog",
  "gender": "male",
  "phone": "+48 123 456 789",
  "email": "owner@example.com",
  "breed": "Golden Retriever",
  "microchipNumber": "123-456-789-012",
  "approximateAge": "3 years",
  "reward": "500 PLN",
  "location": "Warsaw",
  "locationRadius": "15 km",
  "description": "Friendly golden retriever, responds to 'Max'. Last seen near Lazienki Park wearing a red collar."
}
```

**Error Responses**:

- **404 Not Found**: Pet with specified ID does not exist
  ```json
  {
    "error": "Pet not found",
    "message": "No pet with ID 'abc-123' exists"
  }
  ```

- **500 Internal Server Error**: Server error
  ```json
  {
    "error": "Internal server error",
    "message": "An unexpected error occurred"
  }
  ```

**Field Mapping**:

| JSON Field | iOS Model Field | Type | Required | Notes |
|------------|----------------|------|----------|-------|
| `id` | `id` | String | Yes | Unique identifier |
| `photoUrl` | `photoUrl` | String | Yes | URL to pet photo |
| `status` | `status` | String | Yes | Values: ACTIVE, FOUND, CLOSED |
| `lastSeenDate` | `lastSeenDate` | String | Yes | ISO 8601 timestamp |
| `species` | `species` | String | Yes | Pet species |
| `gender` | `gender` | String | Yes | "male" or "female" |
| `phone` | `phone` | String? | No | Owner's phone (at least one of phone/email required) |
| `email` | `email` | String? | No | Owner's email (at least one of phone/email required) |
| `breed` | `breed` | String? | No | Pet breed |
| `microchipNumber` | `microchipNumber` | String? | No | **MOCKED** (not in current API) |
| `approximateAge` | `approximateAge` | String? | No | **MOCKED** (not in current API) |
| `reward` | `reward` | String? | No | **MOCKED** (not in current API) |
| `location` | `location` | String? | No | City name |
| `locationRadius` | `locationRadius` | String? | No | Search radius (e.g., "15 km") |
| `description` | `description` | String? | No | Multi-line description |

**Business Rules**:
- At least one of `phone` or `email` MUST be non-null (validated at application layer)
- `status` value "ACTIVE" is displayed as "MISSING" in iOS UI
- `gender` values ("male", "female") are displayed as symbols (♂, ♀) in iOS UI

**Mocked Fields**:
The following fields are NOT available in the current backend API (`GET /api/v1/announcements`) and are mocked in iOS repository until backend adds them:
- `microchipNumber`
- `approximateAge`
- `reward`

When these fields are added to the backend, update `PetRepositoryImpl` to parse them from the real API response.

---

## Migration Path

**Current State** (Phase 1 - Mock):
- iOS `PetRepositoryImpl` returns hardcoded `PetDetails` struct
- No network calls to backend
- Enables UI development and testing without backend dependency

**Future State** (Phase 2 - Real API):
1. Backend team implements `GET /api/v1/announcements/:id` endpoint
2. Backend adds missing fields (`microchipNumber`, `approximateAge`, `reward`) to response
3. Update iOS `PetRepositoryImpl` to:
   - Call real endpoint via HTTP client
   - Parse JSON response using `Codable`
   - Handle network errors (timeout, no connection, 404, 500)
4. Remove mock data logic from repository
5. Update E2E tests to use real backend (or test backend with seeded data)

**No Breaking Changes**: The iOS domain model (`PetDetails`) remains unchanged. Only repository implementation changes from mock to HTTP client.

---

## Testing

**Mock Data Usage**:
- **Unit Tests**: Use fake repository returning specific `PetDetails` instances
- **E2E Tests**: Use real backend with seeded test data (when backend available)
- **Development**: Use mock repository implementation for local development

**Test Scenarios**:
1. Successfully load pet details (happy path)
2. Pet not found (404 error)
3. Network error (timeout, no connection)
4. Invalid response format (malformed JSON)
5. Missing required fields (validation error)
6. Missing both phone and email (business rule violation)

---

## Related Documents

- [Data Model](../data-model.md) - iOS domain model definitions
- [Feature Specification](../spec.md) - Complete feature requirements
- [Research](../research.md) - Technology decisions and patterns

---

## Notes

- This contract is a **proposed structure** based on feature requirements
- Backend team should validate and adjust based on database schema and API conventions
- When backend endpoint is ready, update this README with actual endpoint documentation
- Consider adding pagination if endpoint returns multiple related entities in the future

