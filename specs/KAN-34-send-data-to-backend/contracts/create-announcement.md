# API Contract: Create Announcement

**Endpoint**: `POST /api/v1/announcements`  
**Source**: `server/README.md`, `server/src/lib/announcement-validation.ts`

## Overview

Creates a new pet announcement (lost or found pet listing). iOS uses this endpoint to submit Missing and Found reports with appropriate status.

## Request

### Headers

| Header | Value | Required |
|--------|-------|----------|
| Content-Type | application/json | Yes |

### Body Schema

```json
{
  "species": "string",
  "sex": "string",
  "lastSeenDate": "string (YYYY-MM-DD)",
  "status": "MISSING | FOUND",
  "locationLatitude": "number (-90 to 90)",
  "locationLongitude": "number (-180 to 180)",
  "email": "string (optional if phone provided)",
  "phone": "string (optional if email provided)",
  "petName": "string (optional)",
  "breed": "string (optional)",
  "age": "number (optional, positive integer)",
  "description": "string (optional)",
  "microchipNumber": "string (optional, digits only)",
  "reward": "string (optional)"
}
```

### Field Details

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| species | string | ✓ | Non-empty, trimmed |
| sex | string | ✓ | Non-empty, trimmed |
| lastSeenDate | string | ✓ | ISO 8601 date (YYYY-MM-DD), must not be future |
| status | string | ✓ | Enum: `"MISSING"` or `"FOUND"` |
| locationLatitude | number | ✓ | -90 to 90 |
| locationLongitude | number | ✓ | -180 to 180 |
| email | string | ✓* | Valid email format |
| phone | string | ✓* | Contains at least one digit |
| petName | string | | Trimmed |
| breed | string | | Trimmed |
| age | number | | Positive integer |
| description | string | | Trimmed |
| microchipNumber | string | | Digits only, unique |
| reward | string | | Trimmed |

*At least one of `email` or `phone` is required. iOS requires BOTH.

### Validation

- Schema is **strict**: unknown fields are rejected
- All string fields are trimmed
- `lastSeenDate` cannot be in the future
- `microchipNumber` must be unique across all announcements

## Response

### Success (201 Created)

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "species": "Golden Retriever",
  "sex": "MALE",
  "lastSeenDate": "2025-11-20",
  "photoUrl": null,
  "status": "MISSING",
  "locationLatitude": 40.785091,
  "locationLongitude": -73.968285,
  "email": "john@example.com",
  "phone": "+1 555 123 4567",
  "petName": "Buddy",
  "breed": "Purebred",
  "age": 5,
  "description": "Friendly dog with brown fur",
  "microchipNumber": "123456789012345",
  "reward": "500 USD",
  "managementPassword": "847362",
  "createdAt": "2025-11-24T12:34:56.789Z",
  "updatedAt": "2025-11-24T12:34:56.789Z"
}
```

**Important**: `managementPassword` is a 6-digit code needed for photo upload authentication.

### Error Responses

#### 400 Bad Request - Validation Error

```json
{
  "error": {
    "code": "MISSING_VALUE",
    "message": "cannot be empty",
    "field": "species"
  }
}
```

Error codes:
- `MISSING_VALUE` - Required field is missing/empty
- `INVALID_FORMAT` - Field format is invalid (e.g., wrong status value)

#### 409 Conflict - Duplicate Microchip

```json
{
  "error": {
    "code": "CONFLICT",
    "message": "Microchip number already exists",
    "field": "microchipNumber"
  }
}
```

## iOS Mapping

### Domain → DTO Mapping

| iOS Domain | iOS DTO | Backend JSON |
|------------|---------|--------------|
| `AnnouncementStatus.active` | `AnnouncementStatusDTO.missing` | `"MISSING"` |
| `AnnouncementStatus.found` | `AnnouncementStatusDTO.found` | `"FOUND"` |

### Request Examples

**Missing Pet Report**:
```json
{
  "species": "Dog",
  "sex": "MALE",
  "lastSeenDate": "2026-01-08",
  "status": "MISSING",
  "locationLatitude": 52.2297,
  "locationLongitude": 21.0122,
  "email": "owner@example.com",
  "phone": "+48123456789",
  "petName": "Rex",
  "breed": "German Shepherd"
}
```

**Found Pet Report**:
```json
{
  "species": "Cat",
  "sex": "FEMALE",
  "lastSeenDate": "2026-01-09",
  "status": "FOUND",
  "locationLatitude": 52.2297,
  "locationLongitude": 21.0122,
  "email": "finder@example.com",
  "phone": "+48987654321",
  "description": "Spotted near park"
}
```

