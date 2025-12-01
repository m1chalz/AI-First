# Data Model: Announcements Location Query

**Feature**: 033-announcements-location-query  
**Date**: 2025-11-29  
**Purpose**: Document entities and data structures for location-based announcement filtering

## Overview

This feature adds location-based filtering capability to the existing announcements API. No database schema changes are required - location data (`lat`, `lng`) is already mandatory for all announcements.

---

## Entities

### Announcement (Existing Entity - No Changes)

Represents a pet announcement with location information. All fields documented below already exist in the system.

**Storage**: SQLite table `announcement` (existing)

**Fields**:

| Field | Type | Required | Description | Validation |
|-------|------|----------|-------------|------------|
| `id` | INTEGER | Yes | Primary key, auto-increment | Unique, > 0 |
| `petName` | TEXT | Yes | Name of the pet | Non-empty string |
| `species` | TEXT | Yes | Type of animal | Enum: dog, cat, bird, etc. |
| `description` | TEXT | No | Additional details about the pet | Max length varies |
| `lat` | REAL | Yes | Latitude coordinate | -90 to 90 (mandatory per spec) |
| `lng` | REAL | REAL | Longitude coordinate | -180 to 180 (mandatory per spec) |
| `status` | TEXT | Yes | Announcement status | Enum: active, resolved, etc. |
| `createdAt` | TEXT | Yes | Creation timestamp | ISO 8601 format |
| `...` | ... | ... | Other fields (photos, contact, etc.) | ... |

**Key Constraints**:
- Location data (`lat`, `lng`) is **mandatory** for all announcements (confirmed in research phase)
- No NULL values for location fields in existing data
- Coordinate values validated at application layer before database insert

**Note**: This entity is not modified by this feature. Location fields already exist and are populated.

---

## Request Parameters

### Query Parameters (New for this Feature)

Location filtering parameters added to `/api/v1/announcements` GET endpoint.

**Parameters**:

| Parameter | Type | Required | Default | Description | Validation |
|-----------|------|----------|---------|-------------|------------|
| `lat` | number | Conditional | N/A | Latitude of search origin | -90 to 90, required if `lng` provided |
| `lng` | number | Conditional | N/A | Longitude of search origin | -180 to 180, required if `lat` provided |
| `range` | number | Optional | 5 | Search radius in kilometers | > 0, ignored if no `lat`/`lng` |

**Validation Rules**:
1. **Coordinate Pair**: `lat` and `lng` must both be present or both absent
2. **Range Dependency**: `range` only applies when `lat` and `lng` are provided
3. **Type Safety**: All parameters must be parseable as numbers (float)
4. **Value Ranges**: See validation column above

**Examples**:

```http
# No filtering (backward compatible)
GET /api/v1/announcements

# Filter with default 5km radius
GET /api/v1/announcements?lat=50.0&lng=20.0

# Filter with custom 10km radius
GET /api/v1/announcements?lat=50.0&lng=20.0&range=10

# Invalid: missing lng (HTTP 400)
GET /api/v1/announcements?lat=50.0

# Invalid: range without coordinates (ignored, returns all)
GET /api/v1/announcements?range=10
```

---

## Response Format

### Announcement List Response (Unchanged)

The response format remains **identical** to the existing implementation. No additional fields are added.

**Response Structure**:

```typescript
{
  "announcements": [
    {
      "id": 1,
      "petName": "Max",
      "species": "dog",
      "description": "Friendly golden retriever",
      "lat": 50.0614,
      "lng": 19.9383,
      "status": "active",
      "createdAt": "2025-11-20T10:30:00Z",
      // ... other fields
    },
    // ... more announcements
  ],
  "total": 15
}
```

**Filtering Behavior**:
- Announcements within the specified range are **included**
- Announcements outside the specified range are **excluded**
- No distance information added to response (confirmed in research phase)
- Response schema remains backward compatible

**HTTP Status Codes**:
- `200 OK`: Successful query (empty array if no results)
- `400 Bad Request`: Invalid query parameters (missing pair, out of range, invalid values)
- `500 Internal Server Error`: Server-side error

---

## Validation Error Responses

### Error Response Format (Existing Pattern)

```typescript
{
  "error": string,          // Human-readable error message
  "details"?: string        // Optional additional context
}
```

### Validation Error Messages

**Coordinate Pair Validation**:

```json
{
  "error": "Parameter 'lng' is required when 'lat' is provided"
}
```

```json
{
  "error": "Parameter 'lat' is required when 'lng' is provided"
}
```

**Coordinate Range Validation**:

```json
{
  "error": "Parameter 'lat' must be between -90 and 90"
}
```

```json
{
  "error": "Parameter 'lng' must be between -180 and 180"
}
```

**Range Validation**:

```json
{
  "error": "Parameter 'range' must be greater than zero"
}
```

```json
{
  "error": "Parameter 'range' must be a positive number"
}
```

**Type Validation**:

```json
{
  "error": "Parameter 'lat' must be a valid number"
}
```

---

## Internal Data Structures

### Location Filter (Internal Type)

Used internally by service layer to encapsulate location filtering parameters.

```typescript
interface LocationFilter {
  lat: number;       // Validated latitude (-90 to 90)
  lng: number;       // Validated longitude (-180 to 180)
  range: number;     // Validated range in km (> 0, default 5)
}
```

**Usage**: Passed from route handler to service layer after validation.

### Distance Calculation Input (Internal)

Parameters for Haversine distance calculation utility.

```typescript
interface DistanceCalculationInput {
  lat1: number;      // Origin latitude
  lon1: number;      // Origin longitude
  lat2: number;      // Destination latitude
  lon2: number;      // Destination longitude
}
```

**Output**: Distance in kilometers (number)

---

## Database Queries

### Existing Query (Unmodified)

Current implementation fetches all announcements:

```sql
SELECT * FROM announcement WHERE status = 'active' ORDER BY createdAt DESC;
```

**No Changes Required**: Query remains identical. Filtering applied in-memory after fetch.

### Location-Based Query (Implemented)

Query with Haversine distance calculation (using subquery for correct filtering):

```typescript
// Step 1: Subquery calculates distance for each announcement
const subquery = knex('announcement')
  .select(
    '*',
    knex.raw(`
      (6371 * acos(
        cos(radians(?)) *
        cos(radians(lat)) *
        cos(radians(lng) - radians(?)) +
        sin(radians(?)) *
        sin(radians(lat))
      )) AS distance
    `, [searchLat, searchLng, searchLat])
  )
  .where('status', 'active');

// Step 2: Filter by distance and sort
const results = await knex
  .from(subquery.as('announcements_with_distance'))
  .where('distance', '<', range)
  .orderBy('distance', 'asc');
```

**Query Explanation**:
- Inner query: Adds calculated `distance` column to each announcement
- Outer query: Filters where `distance < range` (uses WHERE, not HAVING)
- Results sorted by distance (nearest first)

**Why subquery**:
- Cannot use `WHERE distance < range` in single query (distance doesn't exist yet)
- Cannot use `HAVING distance < range` without GROUP BY (semantically incorrect)
- Subquery calculates distance first, then outer query filters correctly

---

## State Transitions

### Location Filter Lifecycle

```
┌─────────────────┐
│ Parse Request   │ ← lat, lng, range from req.query
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Validate Params │ → Invalid? → HTTP 400 Error Response
└────────┬────────┘
         │ Valid or No Params
         ▼
┌─────────────────┐
│ Fetch All       │ ← SELECT * FROM announcement
│ Announcements   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Filter by       │ ← If lat/lng present: calculate distance,
│ Distance        │   include if distance <= range
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Return Filtered │ → HTTP 200 OK with filtered list
│ Results         │
└─────────────────┘
```

---

## Relationships

### No New Relationships

This feature does not introduce new entity relationships. It adds filtering capability to existing announcement queries.

**Existing Relationships** (unchanged):
- Announcement → User (creator/owner)
- Announcement → Photos (one-to-many)
- Announcement → Status updates (audit trail)

---

## Data Integrity Constraints

### Application-Level Constraints

**Enforced by Validation Layer**:
1. Coordinate pair completeness (both or neither)
2. Latitude range: -90 ≤ lat ≤ 90
3. Longitude range: -180 ≤ lng ≤ 180
4. Range positivity: range > 0
5. Type safety: all params parseable as numbers

**Enforced by Existing Database Constraints**:
1. Location data mandatory (NOT NULL constraints on `lat`, `lng`)
2. Primary key uniqueness
3. Foreign key constraints (user, status references)

---

## Testing Considerations

### Test Data Requirements

**Unit Tests**: Use known coordinate pairs with calculated distances
```typescript
const testLocations = {
  krakow: { lat: 50.0614, lng: 19.9383 },
  warsaw: { lat: 52.2297, lng: 21.0122 },  // ~252 km from Krakow
  nearby:  { lat: 50.0700, lng: 19.9500 }  // ~2 km from Krakow
};
```

**Integration Tests**: Seed database with announcements at known distances
```typescript
const announcements = [
  { id: 1, petName: "Max", lat: 50.0614, lng: 19.9383 },     // Origin
  { id: 2, petName: "Luna", lat: 50.0700, lng: 19.9500 },    // 2 km away
  { id: 3, petName: "Buddy", lat: 52.2297, lng: 21.0122 }    // 252 km away
];

// Test: range=5km from Krakow should return Max and Luna only
```

### Boundary Cases

- Announcements exactly at range boundary (e.g., 5.0000 km)
- Zero results (no announcements within range)
- All results (very large range encompasses all announcements)
- Coordinates near poles (-90, 90 latitude)
- Coordinates near date line (-180, 180 longitude)

---

## Summary

**Database Changes**: None (location data already exists and is mandatory)

**New Data Structures**: Internal only (`LocationFilter`, validation types)

**API Changes**: Query parameters added to existing endpoint (backward compatible)

**Response Format**: Unchanged (no additional fields)

**Validation**: Comprehensive application-layer validation for new parameters

**Next Steps**: Phase 1 continuation - create OpenAPI contract and quickstart guide

