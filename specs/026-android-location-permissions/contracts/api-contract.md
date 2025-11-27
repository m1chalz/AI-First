# API Contract: Location-Aware Animal Listings

**Feature**: Android Location Permissions Handling  
**Date**: 2025-11-27  
**Type**: REST API Contract (Backend)

## Overview

This document defines the expected API contract for location-aware animal listings. The Android app will pass optional location coordinates when available, and the backend should handle location-based filtering (if implemented) or return all animals when coordinates are not provided.

**Note**: This contract defines the **expected** API behavior. Backend implementation may be done in a separate feature. Android implementation will work with mock data until backend is ready.

## Endpoint

### GET /api/animals (or GET /api/pets)

Retrieves list of animals, optionally filtered by user's current location.

## Request

### Query Parameters (Optional)

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `latitude` | `number` (double) | No | User's current latitude coordinate (-90.0 to 90.0) |
| `longitude` | `number` (double) | No | User's current longitude coordinate (-180.0 to 180.0) |

**Validation Rules**:
- If `latitude` is provided, `longitude` MUST also be provided (and vice versa)
- If only one coordinate is provided, backend should ignore both and return all animals
- Latitude must be between -90.0 and 90.0 (inclusive)
- Longitude must be between -180.0 and 180.0 (inclusive)
- If coordinates are invalid, backend should return all animals (graceful degradation)

**Request Examples**:

```
# With location coordinates (location-aware query)
GET /api/animals?latitude=52.2297&longitude=21.0122

# Without location coordinates (fallback mode)
GET /api/animals
```

## Response

### Success Response (200 OK)

**Content-Type**: `application/json`

**Body**: Array of Animal objects (same structure as existing `/api/animals` endpoint)

```json
[
  {
    "id": "1",
    "name": "Fluffy",
    "photoUrl": "placeholder_cat",
    "location": {
      "city": "Warsaw",
      "radiusKm": 5
    },
    "species": "CAT",
    "breed": "Maine Coon",
    "gender": "MALE",
    "status": "MISSING",
    "lastSeenDate": "18/11/2025",
    "description": "Friendly orange tabby cat...",
    "email": "john.doe@example.com",
    "phone": "+48 123 456 789"
  },
  // ... more animals
]
```

**Behavior**:
- When `latitude` and `longitude` are provided: Backend MAY filter animals by proximity (implementation-dependent)
- When coordinates are not provided: Backend returns all animals (no location filtering)
- Response structure is identical regardless of location parameters (same Animal schema)

### Error Responses

**400 Bad Request**: Invalid coordinate values (out of range)
- Backend should still return animals (graceful degradation), but may log warning

**500 Internal Server Error**: Server error (same as existing endpoint)

## Backend Implementation Notes

**Current Phase**: Backend implementation is **not required** for this Android feature. Android app will work with mock data until backend is ready.

**Future Implementation** (when backend is implemented):
- Backend SHOULD accept optional `latitude` and `longitude` query parameters
- Backend MAY implement location-based filtering (proximity search) when coordinates provided
- Backend MUST return all animals when coordinates are not provided (fallback mode)
- Backend MUST handle invalid coordinates gracefully (return all animals, log warning)

## Android Implementation

**Repository Method**:
```kotlin
suspend fun getAnimals(location: LocationCoordinates? = null): List<Animal>
```

**HTTP Request Logic**:
- When `location` is null: `GET /api/animals` (no query parameters)
- When `location` is non-null: `GET /api/animals?latitude=${location.latitude}&longitude=${location.longitude}`

**Error Handling**:
- Network errors: Return empty list or throw exception (per existing error handling)
- Invalid coordinates: Should not occur (validated in domain layer), but handle gracefully if backend returns error

## Testing

**Test Scenarios**:
1. Request with valid coordinates → Verify query parameters included
2. Request without coordinates → Verify no query parameters
3. Backend returns 400 (invalid coordinates) → Handle gracefully (fallback to all animals)
4. Network error → Handle per existing error handling strategy

## Summary

- **Endpoint**: `GET /api/animals` (or existing pets endpoint)
- **Query Parameters**: Optional `latitude` and `longitude` (both required if either provided)
- **Response**: Same Animal array structure (location filtering is backend implementation detail)
- **Backend Status**: Not required for this Android feature (mock data until backend ready)
- **Android Status**: Will pass coordinates when available, no coordinates when unavailable

