# API Contracts: Pet Details Screen

**Feature**: 012-ios-pet-details-screen  
**Date**: November 24, 2025  
**Status**: ✅ Backend Endpoint Already Implemented

## Overview

This directory contains API contract definitions for the Pet Details Screen feature. The backend endpoint `GET /api/v1/announcements/:id` is **already implemented** on the main branch, so iOS `PetRepositoryImpl` will call the real API instead of using mock data.

## Contracts

### pet-details-response.json

**Endpoint**: `GET /api/v1/announcements/:id` ✅

**Description**: Retrieves comprehensive details for a single pet by ID.

**Implementation**: Backend endpoint is live on main branch. iOS `PetRepositoryImpl` calls this endpoint via HTTP client.

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
  "id": "11111111-1111-1111-1111-111111111111",
  "petName": "Fredi Kamionka Gmina Burzenin",
  "photoUrl": "https://images.dog.ceo/breeds/terrier-yorkshire/n02094433_1010.jpg",
  "status": "ACTIVE",
  "lastSeenDate": "2025-11-18",
  "species": "DOG",
  "breed": "York",
  "gender": "MALE",
  "description": "Zaginął piesek York wabi się Fredi Kamionka gmina burzenin",
  "location": "Kamionka",
  "locationRadius": 5,
  "phone": "+48 123 456 789",
  "email": "spotterka@example.pl",
  "createdAt": "2025-11-19T15:47:14.000Z",
  "updatedAt": "2025-11-19T15:47:14.000Z"
}
```

**Error Responses**:

- **404 Not Found**: Pet with specified ID does not exist
  ```json
  {
    "error": {
      "code": "NOT_FOUND",
      "message": "Resource not found"
    }
  }
  ```

- **500 Internal Server Error**: Server error
  ```json
  {
    "error": {
      "code": "INTERNAL_SERVER_ERROR",
      "message": "Internal server error"
    }
  }
  ```

**Field Mapping**:

| JSON Field | iOS Model Field | Type | Required | Notes |
|------------|----------------|------|----------|-------|
| `id` | `id` | String | Yes | Unique identifier (UUID) |
| `petName` | `petName` | String | Yes | Pet name (not displayed in UI) |
| `photoUrl` | `photoUrl` | String? | No | URL to pet photo (nullable) |
| `status` | `status` | String | Yes | Values: ACTIVE, FOUND, CLOSED |
| `lastSeenDate` | `lastSeenDate` | String | Yes | YYYY-MM-DD format |
| `species` | `species` | String | Yes | Pet species (uppercase: DOG, CAT, etc.) |
| `gender` | `gender` | String | Yes | MALE, FEMALE, UNKNOWN (uppercase) |
| `description` | `description` | String | Yes | Multi-line description |
| `location` | `location` | String | Yes | City name |
| `phone` | `phone` | String | Yes | Owner's phone |
| `email` | `email` | String? | No | Owner's email |
| `breed` | `breed` | String? | No | Pet breed |
| `locationRadius` | `locationRadius` | Int? | No | Search radius in kilometers (number) |
| `createdAt` | `createdAt` | String | Yes | ISO 8601 timestamp |
| `updatedAt` | `updatedAt` | String | Yes | ISO 8601 timestamp |
| N/A | `microchipNumber` | String? | No | **MOCKED** (not in backend API) |
| N/A | `approximateAge` | String? | No | **MOCKED** (not in backend API) |
| N/A | `reward` | String? | No | **MOCKED** (not in backend API) |

**Business Rules**:
- `phone` is required in backend schema (not nullable)
- `email` is optional (nullable)
- `status` value "ACTIVE" is displayed as "MISSING" in iOS UI
- `gender` values (MALE, FEMALE, UNKNOWN) are displayed as symbols (♂, ♀, ?) in iOS UI
- `locationRadius` is a number (kilometers); ViewModel formats to "±X km" for display

**Mocked Fields**:
The following fields are NOT available in the current backend API (`GET /api/v1/announcements`) and are mocked in iOS repository until backend adds them:
- `microchipNumber`
- `approximateAge`
- `reward`

When these fields are added to the backend, update `PetRepositoryImpl` to parse them from the real API response.

---

## Implementation Approach

**Current State** ✅:
- Backend endpoint `GET /api/v1/announcements/:id` is **already live** on main branch
- iOS `PetRepositoryImpl` will call real API endpoint via HTTP client
- Seed data available for development and testing

**Implementation Steps**:
1. iOS `PetRepositoryImpl` calls `GET /api/v1/announcements/:id` via HTTP client (URLSession or Alamofire)
2. Parse JSON response using `Codable` conformance on `PetDetails` model
3. Handle network errors:
   - 404 Not Found → show error state "Unable to load pet details"
   - 500 Server Error → show error state with retry button
   - Network timeout/no connection → show error state with retry button
4. Missing fields (`microchipNumber`, `approximateAge`, `reward`) remain `nil` until backend adds them
5. E2E tests use real backend with seeded data (or test backend)

**Future Enhancement**:
When backend adds missing fields (`microchipNumber`, `approximateAge`, `reward`), iOS implementation requires no changes - fields will automatically populate from API response thanks to `Codable`.

---

## Testing

**Test Data Usage**:
- **Unit Tests**: Use fake repository returning specific `PetDetails` instances (no network calls)
- **E2E Tests**: Use real backend with seeded test data (seed file: `server/src/database/seeds/001_announcements.ts`)
- **Development**: Use real backend running locally with seed data

**Available Test Pet IDs** (from seed data):
- `11111111-1111-1111-1111-111111111111` - Fredi (DOG, ACTIVE, with photo)
- `22222222-2222-2222-2222-222222222222` - Luna (CAT, ACTIVE, with photo, no email, no radius)
- `33333333-3333-3333-3333-333333333333` - Piorun (BIRD, ACTIVE, no phone)
- `44444444-4444-4444-4444-444444444444` - Burek (DOG, FOUND, with photo)

**Test Scenarios**:
1. Successfully load pet details (happy path) - use any valid ID from seed data
2. Pet not found (404 error) - use invalid ID like "invalid-uuid"
3. Network error (timeout, no connection) - stop backend server
4. Invalid response format (malformed JSON) - unlikely with backend, test with unit tests
5. Missing optional fields - use Luna (no email, no locationRadius)
6. Status FOUND - use Burek (status: FOUND, not ACTIVE)

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

