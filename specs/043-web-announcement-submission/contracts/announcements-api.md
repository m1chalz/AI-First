# API Contracts: Announcements

**Feature**: Web announcement submission integration  
**Date**: 2025-12-03  
**API Version**: v1

## Overview

This document defines the HTTP API contracts for announcement creation and photo upload. The web application consumes these endpoints to submit missing pet announcements.

---

## Endpoint 1: Create Announcement

**Purpose**: Create a new missing pet announcement

### Request

```
POST /api/v1/announcements
Content-Type: application/json
```

**Headers**:
- `Content-Type`: `application/json` (required)

**Body**: `AnnouncementSubmissionDto` (JSON)

```json
{
  "petName": "Max",
  "species": "dog",
  "breed": "Labrador Retriever",
  "sex": "male",
  "age": 5,
  "description": "Friendly golden lab with white chest marking",
  "microchipNumber": "123456789012345",
  "locationLatitude": 52.2297,
  "locationLongitude": 21.0122,
  "email": "owner@example.com",
  "phone": "+48123456789",
  "lastSeenDate": "2025-12-03",
  "status": "MISSING",
  "reward": "500 PLN"
}
```

**Required Fields**:
- `species` (string): Must be one of: "dog", "cat", "other"
- `sex` (string): Must be one of: "male", "female"
- `locationLatitude` (number): Latitude coordinate
- `locationLongitude` (number): Longitude coordinate
- `lastSeenDate` (string): ISO 8601 date (YYYY-MM-DD)
- `status` (string): Must be "MISSING" for new announcements
- At least one of: `email` or `phone`

**Optional Fields**:
- `petName` (string): Pet's name
- `breed` (string): Pet's breed
- `age` (number): Pet's age in years
- `description` (string): Free-text description
- `microchipNumber` (string): 15-digit microchip number
- `reward` (string): Reward amount (free text)

---

### Response: Success (201 Created)

**Status**: `201 Created`

**Headers**:
- `Content-Type`: `application/json`

**Body**: `AnnouncementResponse` (JSON)

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "managementPassword": "a1b2c3d4e5f6",
  "petName": "Max",
  "species": "dog",
  "breed": "Labrador Retriever",
  "sex": "male",
  "age": 5,
  "description": "Friendly golden lab with white chest marking",
  "microchipNumber": "123456789012345",
  "locationLatitude": 52.2297,
  "locationLongitude": 21.0122,
  "email": "owner@example.com",
  "phone": "+48123456789",
  "photoUrl": null,
  "lastSeenDate": "2025-12-03",
  "status": "MISSING",
  "reward": "500 PLN",
  "createdAt": "2025-12-03T12:34:56.789Z",
  "updatedAt": "2025-12-03T12:34:56.789Z"
}
```

**Key Fields**:
- `id` (string, UUID): Unique announcement identifier for photo upload
- `managementPassword` (string): Password for future updates/deletions
- `photoUrl` (string | null): Initially `null`, populated after photo upload
- `createdAt`, `updatedAt` (string, ISO 8601): Timestamps

---

### Response: Validation Error (400 Bad Request)

**Status**: `400 Bad Request`

**Headers**:
- `Content-Type`: `application/json`

**Body**:

```json
{
  "error": "VALIDATION_ERROR",
  "message": "Validation failed: missing required field 'species'",
  "field": "species"
}
```

**Common Validation Errors**:
- Missing required fields: `species`, `sex`, `locationLatitude`, `locationLongitude`, `lastSeenDate`, `status`
- Missing contact method: "At least one contact method (email or phone) is required"
- Invalid status value: "status must be 'MISSING' or 'FOUND'"
- Invalid species: "species must be one of: dog, cat, other"
- Invalid sex: "sex must be one of: male, female"
- Invalid date format: "lastSeenDate must be in YYYY-MM-DD format"

---

### Response: Duplicate Microchip (409 Conflict)

**Status**: `409 Conflict`

**Headers**:
- `Content-Type`: `application/json`

**Body**:

```json
{
  "error": "DUPLICATE_MICROCHIP",
  "message": "Announcement with this microchip number already exists",
  "microchipNumber": "123456789012345"
}
```

**Trigger**: Microchip number is unique constraint in database, duplicate submission rejected.

**User Message**: "This microchip already exists. If this is your announcement, use your management password to update it."

---

### Response: Server Error (500 Internal Server Error)

**Status**: `500 Internal Server Error`

**Headers**:
- `Content-Type`: `application/json`

**Body**:

```json
{
  "error": "INTERNAL_SERVER_ERROR",
  "message": "An unexpected error occurred"
}
```

**User Message**: "Server error. Please try again later."

---

## Endpoint 2: Upload Announcement Photo

**Purpose**: Upload photo for an existing announcement

### Request

```
POST /api/v1/announcements/:id/photos
Content-Type: multipart/form-data
Authorization: Basic <base64(id:managementPassword)>
```

**Path Parameters**:
- `id` (string, UUID, required): Announcement ID from creation response

**Headers**:
- `Content-Type`: `multipart/form-data` (required)
- `Authorization`: `Basic <credentials>` (required)
  - Credentials format: `base64(announcementId:managementPassword)`
  - Example: `Basic NTUwZTg0MDAtZTI5Yi00MWQ0LWE3MTYtNDQ2NjU1NDQwMDAwOmExYjJjM2Q0ZTVmNg==`

**Body**: Multipart form data with `photo` field

```
------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="photo"; filename="pet.jpg"
Content-Type: image/jpeg

<binary image data>
------WebKitFormBoundary7MA4YWxkTrZu0gW--
```

**Field Requirements**:
- `photo` (File, required): Image file (JPEG, PNG, WEBP)
- Maximum file size: 10MB (enforced client-side)
- Supported MIME types: `image/jpeg`, `image/png`, `image/webp`

---

### Response: Success (201 Created)

**Status**: `201 Created`

**Headers**:
- `Content-Type`: `application/json`

**Body**:

```json
{}
```

**Note**: Empty response body. Photo URL is available via `GET /api/v1/announcements/:id`.

---

### Response: Unauthorized (401 Unauthorized)

**Status**: `401 Unauthorized`

**Headers**:
- `Content-Type`: `application/json`

**Body**:

```json
{
  "error": "UNAUTHORIZED",
  "message": "Invalid credentials"
}
```

**Triggers**:
- Missing `Authorization` header
- Invalid management password
- Malformed Basic Auth credentials

**User Message**: "Authentication failed. Please ensure your management password is correct."

---

### Response: Announcement Not Found (404 Not Found)

**Status**: `404 Not Found`

**Headers**:
- `Content-Type`: `application/json`

**Body**:

```json
{
  "error": "NOT_FOUND",
  "message": "Announcement not found"
}
```

**Trigger**: Announcement ID does not exist.

**User Message**: "Announcement not found. Please try creating it again."

---

### Response: Missing File (400 Bad Request)

**Status**: `400 Bad Request`

**Headers**:
- `Content-Type`: `application/json`

**Body**:

```json
{
  "error": "MISSING_FILE",
  "message": "Photo field is required",
  "field": "photo"
}
```

**Trigger**: No file uploaded in `photo` field.

**User Message**: "Photo is required."

---

### Response: Server Error (500 Internal Server Error)

**Status**: `500 Internal Server Error`

**Headers**:
- `Content-Type`: `application/json`

**Body**:

```json
{
  "error": "INTERNAL_SERVER_ERROR",
  "message": "Failed to upload photo"
}
```

**User Message**: "Photo upload failed. Please try again."

---

## Error Handling Summary

| HTTP Status | Error Type | Frontend Mapping | User Message |
|-------------|-----------|------------------|--------------|
| 400 | Validation Error | `ValidationError` | "Validation error: [specific message]" |
| 401 | Unauthorized | `ApiError` (generic) | "Authentication failed" |
| 404 | Not Found | `ApiError` (generic) | "Announcement not found" |
| 409 | Duplicate Microchip | `DuplicateMicrochipError` | "This microchip already exists. Use your management password to update it." |
| 500 | Server Error | `ServerError` | "Server error. Please try again later." |
| Network failure | Network Error | `NetworkError` | "Network error. Please check your connection." |

---

## Request Flow

```
1. User clicks "Continue" on Contact Screen
   └─> ContactScreen.tsx calls useAnnouncementSubmission.submitAnnouncement()

2. submitAnnouncement() creates announcement
   └─> POST /api/v1/announcements (AnnouncementSubmissionDto)
       └─> Response: AnnouncementResponse { id, managementPassword, ... }

3. submitAnnouncement() uploads photo
   └─> POST /api/v1/announcements/:id/photos (FormData with photo)
       └─> Headers: Authorization: Basic <base64(id:managementPassword)>
       └─> Response: 201 Created (empty body)

4. Navigate to Summary Screen
   └─> Display managementPassword to user
```

---

## Authentication Flow

**Basic Auth Construction**:

```typescript
const announcementId = '550e8400-e29b-41d4-a716-446655440000';
const managementPassword = 'a1b2c3d4e5f6';

// Construct credentials string
const credentials = `${announcementId}:${managementPassword}`;

// Encode to base64
const encodedCredentials = btoa(credentials);

// Set Authorization header
headers: {
  'Authorization': `Basic ${encodedCredentials}`
}
```

**Example**:
- Credentials: `550e8400-e29b-41d4-a716-446655440000:a1b2c3d4e5f6`
- Base64: `NTUwZTg0MDAtZTI5Yi00MWQ0LWE3MTYtNDQ2NjU1NDQwMDAwOmExYjJjM2Q0ZTVmNg==`
- Header: `Authorization: Basic NTUwZTg0MDAtZTI5Yi00MWQ0LWE3MTYtNDQ2NjU1NDQwMDAwOmExYjJjM2Q0ZTVmNg==`

---

## Testing Considerations

**Integration Tests**:
- Test successful announcement creation + photo upload
- Test validation errors (missing fields, invalid values)
- Test duplicate microchip error
- Test unauthorized photo upload (invalid password)
- Test network failure scenarios

**E2E Tests**:
- Complete flow: microchip → photo → details → contact → submit → summary
- Verify management password displayed
- Verify announcement appears in public list
- Verify photo is visible in announcement details

---

**API Contracts Complete**: Ready for service implementation.

