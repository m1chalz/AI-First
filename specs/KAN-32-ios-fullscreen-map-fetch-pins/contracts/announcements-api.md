# API Contract: Announcements Endpoint (Pin Fetching)

**Branch**: `KAN-32-ios-fullscreen-map-fetch-pins` | **Date**: 2025-01-07

## Endpoint

```
GET /api/v1/announcements
```

## Description

Fetches animal announcements with optional location-based filtering. Used by iOS fullscreen map to retrieve pins based on visible map region.

## Request

### Query Parameters

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| lat | float | No | Center latitude for location filtering | `52.2297` |
| lng | float | No | Center longitude for location filtering | `21.0122` |
| range | float | No | Search radius in kilometers | `10` |

**Note**: If `lat`/`lng` provided without `range`, server uses default range.

### Example Request

```http
GET /api/v1/announcements?lat=52.2297&lng=21.0122&range=10
Host: api.petspot.example
Accept: application/json
```

## Response

### Success (200 OK)

```json
{
  "data": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "name": "Buddy",
      "status": "MISSING",
      "species": "DOG",
      "breed": "Golden Retriever",
      "gender": "MALE",
      "lastSeenDate": "2025-01-05",
      "lastSeenLatitude": 52.2297,
      "lastSeenLongitude": 21.0122,
      "description": "Golden fur, blue collar",
      "contactEmail": "owner@example.com",
      "contactPhone": "+48123456789",
      "photoUrl": "https://api.petspot.example/images/buddy.jpg"
    },
    {
      "id": "223e4567-e89b-12d3-a456-426614174001",
      "name": "Luna",
      "status": "FOUND",
      "species": "CAT",
      "breed": "Siamese",
      "gender": "FEMALE",
      "lastSeenDate": "2025-01-06",
      "lastSeenLatitude": 52.2310,
      "lastSeenLongitude": 21.0150,
      "description": "White fur, no collar",
      "contactEmail": "finder@example.com",
      "contactPhone": null,
      "photoUrl": null
    }
  ]
}
```

### Response Fields

| Field | Type | Nullable | Description |
|-------|------|----------|-------------|
| data | array | No | Array of announcement objects |
| data[].id | string | No | Unique announcement identifier |
| data[].name | string | Yes | Animal name |
| data[].status | string | No | `MISSING`, `FOUND`, or `CLOSED` |
| data[].species | string | No | `DOG`, `CAT`, `BIRD`, `RABBIT`, `OTHER` |
| data[].breed | string | Yes | Specific breed name |
| data[].gender | string | No | `MALE`, `FEMALE`, `UNKNOWN` |
| data[].lastSeenDate | string | No | ISO date (YYYY-MM-DD) |
| data[].lastSeenLatitude | float | No | Latitude coordinate |
| data[].lastSeenLongitude | float | No | Longitude coordinate |
| data[].description | string | Yes | Detailed description |
| data[].contactEmail | string | Yes | Contact email |
| data[].contactPhone | string | Yes | Contact phone |
| data[].photoUrl | string | Yes | Photo URL or null |

### Error Responses

| Status | Description |
|--------|-------------|
| 400 Bad Request | Invalid query parameters (non-numeric lat/lng/range) |
| 500 Internal Server Error | Server-side error |

## iOS Client Usage

### Repository Method

```swift
// AnnouncementRepositoryProtocol
func getAnnouncements(near location: Coordinate?, range: Int) async throws -> [Announcement]
```

### Pin Filtering (Client-Side)

```swift
// Filter for "missing" announcements only
let pins = announcements.filter { $0.status == .active }
```

**Status Mapping**:
- Backend `MISSING` → iOS `AnnouncementStatus.active`
- Backend `FOUND` → iOS `AnnouncementStatus.found` (not shown as pins)
- Backend `CLOSED` → iOS `AnnouncementStatus.closed` (not shown as pins)

### Radius Calculation

```swift
// Convert MKCoordinateRegion to radius in meters
func radiusFromRegion(_ region: MKCoordinateRegion) -> Int {
    // latitudeDelta degrees → meters (approx 111km per degree)
    let radiusMeters = region.span.latitudeDelta * 111_000 / 2
    return Int(radiusMeters / 1000) // Convert to km for API
}
```

## Notes

- **No pagination**: API returns all matching announcements (client displays all pins)
- **Location filtering**: When lat/lng provided, server filters by geographic distance
- **Deduplication**: iOS repository handles duplicate IDs (keeps first occurrence)
- **Cancellation**: Repository supports Task cancellation for rapid gesture scenarios

