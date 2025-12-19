# API Contract: Announcements Endpoint

**Feature**: 064-web-map-pins  
**Date**: 2025-12-19  
**Endpoint**: `GET /api/v1/announcements`  
**Status**: **EXISTING** (No backend changes required)

## Overview

This document describes how the web map pins feature uses the existing `/api/v1/announcements` endpoint to fetch pet announcement data for displaying as map pins. No backend changes are required.

---

## Endpoint Details

### GET /api/v1/announcements

Fetches pet announcements for display as map pins.

**Method**: `GET`  
**Path**: `/api/v1/announcements`  
**Authentication**: Not required (public endpoint)

---

## Request

### Query Parameters

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `lat` | number | No | Center latitude for location-based filtering | `52.5170` |
| `lng` | number | No | Center longitude for location-based filtering | `13.3900` |
| `range` | number | No | Search radius in kilometers | `10` |

**Notes**:
- Location parameters (`lat`, `lng`, `range`) filter announcements by proximity to a center point
- All three location parameters must be provided together, or all omitted
- If location parameters are omitted, all announcements are returned
- No pagination or status filtering in current implementation

### Example Request

```http
GET /api/v1/announcements?lat=52.5170&lng=13.3900&range=10
Host: localhost:3000
Accept: application/json
```

---

## Response

### Success Response (200 OK)

```json
{
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "species": "Golden Retriever",
      "sex": "MALE",
      "petName": "Max",
      "description": "Friendly golden retriever, answers to Max",
      "status": "MISSING",
      "locationLatitude": 52.5170,
      "locationLongitude": 13.3900,
      "lastSeenDate": "2025-12-18",
      "email": "owner@example.com",
      "phone": "+49 30 12345678",
      "photoUrl": "http://localhost:3000/images/550e8400-e29b-41d4-a716-446655440000.jpeg",
      "microchipNumber": null,
      "managementPassword": "123456",
      "createdAt": "2025-12-18T16:00:00.000Z"
    },
    {
      "id": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
      "species": "Siamese Cat",
      "sex": "FEMALE",
      "petName": "Luna",
      "description": "Siamese cat with blue eyes",
      "status": "FOUND",
      "locationLatitude": 52.5100,
      "locationLongitude": 13.4000,
      "lastSeenDate": "2025-12-17",
      "email": "finder@example.com",
      "phone": null,
      "photoUrl": null,
      "microchipNumber": null,
      "managementPassword": "654321",
      "createdAt": "2025-12-17T11:00:00.000Z"
    }
  ]
}
```

### Response Schema

```typescript
{
  data: Announcement[];  // Array of announcements
}

interface Announcement {
  id: string;                     // Unique announcement ID (UUID)
  species: string;                // Pet breed/type (e.g., "Golden Retriever", "Siamese Cat")
  sex: 'MALE' | 'FEMALE';         // Pet sex (uppercase)
  petName: string | null;         // Name of the pet (optional)
  description: string | null;     // Detailed description (optional)
  status: 'MISSING' | 'FOUND';    // Announcement status (uppercase)
  locationLatitude: number;       // Last seen latitude [-90, 90]
  locationLongitude: number;      // Last seen longitude [-180, 180]
  lastSeenDate: string;           // Date in YYYY-MM-DD format (not ISO 8601 timestamp)
  email: string | null;           // Contact email (optional)
  phone: string | null;           // Contact phone number (optional)
  photoUrl: string | null;        // Pet photo URL (null if no photo)
  microchipNumber: string | null; // Microchip number (optional)
  managementPassword: string;     // 6-digit password for photo upload/management
  createdAt: string;              // Announcement created timestamp (ISO 8601)
}
```

### Field Notes

| Field | Format | Notes |
|-------|--------|-------|
| `id` | UUID v4 | Unique announcement identifier |
| `species` | string | Pet breed/type (e.g., "Golden Retriever") |
| `sex` | "MALE" \| "FEMALE" | Uppercase enum |
| `petName` | string \| null | Optional pet name |
| `status` | "MISSING" \| "FOUND" | Uppercase enum - determines pin color |
| `locationLatitude` | number | Latitude coordinate |
| `locationLongitude` | number | Longitude coordinate |
| `lastSeenDate` | YYYY-MM-DD | Date string (not ISO timestamp) |
| `email` | string \| null | Optional contact email |
| `phone` | string \| null | Optional contact phone |
| `photoUrl` | string \| null | Null if no photo uploaded |
| `managementPassword` | string | 6-digit password for photo upload |

---

## Error Responses

### 400 Bad Request

Invalid query parameters (e.g., invalid latitude/longitude).

```json
{
  "error": "Bad Request",
  "message": "Invalid bounds: north must be greater than south",
  "statusCode": 400
}
```

### 500 Internal Server Error

Server error (database failure, etc.).

```json
{
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "statusCode": 500
}
```

---

## Usage in Map Pins Feature

### Fetch Pins for User Location

```typescript
// Inside use-map-pins.ts hook
const response = await announcementService.getAnnouncements({
  lat: userLocation.lat,
  lng: userLocation.lng,
  range: 10  // Fixed 10km radius
});

// Inline transformation
const pins = response.data.map(a => ({
  id: a.id,
  name: a.petName,
  species: a.species,
  status: a.status.toLowerCase() as 'missing' | 'found',
  latitude: a.locationLatitude,
  longitude: a.locationLongitude,
  photoUrl: a.photoUrl,
  phoneNumber: a.phone,
  email: a.email,
  createdAt: a.createdAt
}));
```

### Query Strategy

- **Location filtering**: Use user's current location (`lat`, `lng`) with fixed 10km radius
- **User location**: Obtained from map center (provided by 063-web-map-view feature)
- **All or nothing**: All three parameters (`lat`, `lng`, `range`) must be provided together, or omit all to fetch all announcements
- **No pagination**: API returns all matching announcements in a single response
- **No status filter**: API returns both MISSING and FOUND announcements

### Error Handling

```typescript
// Inside use-map-pins.ts hook
try {
  const response = await announcementService.getAnnouncements({ lat, lng, range: 10 });
  setPins(response.data.map(/* inline transformation */));
  setError(null);
} catch (err) {
  if (!controller.signal.aborted) {
    setError(err);  // Component shows error overlay with retry button
  }
}
```

---

## Testing Strategy

### Unit Tests

Test the `use-map-pins` hook (see `/webApp/src/hooks/__test__/use-map-pins.test.ts`):
- Verify API called with correct parameters (lat, lng, range)
- Verify inline transformation (uppercase → lowercase status)
- Verify error handling and AbortController cleanup

### Integration Tests

E2E test will verify actual API integration (see `/e2e-tests/web/specs/064-web-map-pins.spec.ts`).

---

## Future Enhancements (Not in Scope)

- **Real-time updates**: WebSocket subscription for new announcements
- **Clustering**: Group nearby pins when zoomed out
- **Filters**: Allow user to filter by species, status, date range
- **Pagination**: Infinite scroll for >100 pins in viewport

---

## References

- Backend API Documentation: `/server/README.md`
- AnnouncementService: `/webApp/src/services/AnnouncementService.ts`
- use-map-pins hook: `/webApp/src/hooks/use-map-pins.ts`

