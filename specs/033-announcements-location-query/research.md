# Research: Announcements Location Query

**Feature**: 033-announcements-location-query  
**Date**: 2025-11-29  
**Purpose**: Research distance calculation algorithms and validation patterns for location-based filtering

## Overview

This document captures research decisions for implementing geographic distance-based filtering of pet announcements. Key concerns: accuracy, performance, and validation.

---

## Decision 1: Distance Calculation Algorithm

### Problem

Need to calculate the distance between two geographic coordinates (latitude, longitude pairs) to filter announcements within a specified radius.

### Options Evaluated

1. **Haversine Formula**
   - Pros: Standard geographic distance calculation, high accuracy for short distances (<1000km), well-documented
   - Cons: Assumes spherical Earth (small error ~0.5% for very long distances)
   - Complexity: Moderate (trigonometric functions)

2. **Vincenty Formula**
   - Pros: Most accurate (accounts for Earth's ellipsoid shape), error <0.01%
   - Cons: Complex implementation, computationally expensive, overkill for pet search use case
   - Complexity: High (iterative algorithm)

3. **Euclidean Distance**
   - Pros: Simple calculation
   - Cons: Completely inaccurate for geographic coordinates (treats Earth as flat plane)
   - Complexity: Low
   - **REJECTED**: Produces significant errors even for short distances

### Decision: Haversine Formula

**Rationale**:
- Accuracy within 1% error margin is sufficient for pet announcement searches (typical range: 1-100km)
- Industry-standard approach for location-based services
- Moderate computational cost (acceptable for low-traffic scenario)
- Well-tested implementation pattern available

**Formula**:
```
a = sin²(Δφ/2) + cos(φ1) * cos(φ2) * sin²(Δλ/2)
c = 2 * atan2(√a, √(1−a))
d = R * c
```

Where:
- φ = latitude (in radians)
- λ = longitude (in radians)
- R = Earth's radius (6371 km)
- d = distance between points (km)

**Implementation Notes**:
- Convert degrees to radians: `radians = degrees * (Math.PI / 180)`
- Use Math.sin, Math.cos, Math.atan2, Math.sqrt (native JavaScript)
- Return distance in kilometers (matches `range` parameter unit)

**References**:
- https://en.wikipedia.org/wiki/Haversine_formula
- https://www.movable-type.co.uk/scripts/latlong.html

---

## Decision 2: Coordinate Validation Rules

### Problem

Need to validate latitude and longitude values to prevent invalid queries and ensure data integrity.

### Valid Ranges

Standard geographic coordinate system (WGS84):
- **Latitude**: -90 to +90 degrees
  - -90 = South Pole
  - 0 = Equator
  - +90 = North Pole
- **Longitude**: -180 to +180 degrees
  - -180 / +180 = International Date Line
  - 0 = Prime Meridian (Greenwich)

### Validation Rules

1. **Type Validation**:
   - Both `lat` and `lng` must be numeric (parseable as float)
   - Reject non-numeric strings, null, undefined

2. **Range Validation**:
   - Latitude: `-90 <= lat <= 90`
   - Longitude: `-180 <= lng <= 180`

3. **Pair Validation**:
   - If `lat` provided, `lng` MUST also be provided (and vice versa)
   - Reject incomplete coordinate pairs with HTTP 400

4. **Precision Handling**:
   - Accept standard decimal precision (e.g., 6 decimal places ≈ 0.11m accuracy)
   - No artificial precision limits (database stores as REAL/FLOAT)

### Error Messages

- Missing pair: `"Parameter 'lng' is required when 'lat' is provided"`
- Invalid latitude: `"Parameter 'lat' must be between -90 and 90"`
- Invalid longitude: `"Parameter 'lng' must be between -180 and 180"`
- Non-numeric: `"Parameter 'lat' must be a valid number"`

---

## Decision 3: Range Parameter Validation

### Problem

Need to validate the `range` parameter (search radius in kilometers) to prevent abuse and meaningless queries.

### Validation Rules

1. **Type Validation**:
   - Must be numeric (parseable as positive float)
   - Reject non-numeric strings, null, undefined

2. **Value Validation**:
   - Must be positive number: `range > 0`
   - **Zero is invalid**: Range of 0km doesn't make sense for search (clarification from spec)
   - Negative values rejected

3. **No Maximum Limit**:
   - Specification clarifies: no maximum range limit
   - Allow any positive number (user decision from clarification session)
   - Rationale: Low traffic and small dataset make large ranges acceptable

4. **Default Value**:
   - If `lat`/`lng` provided without `range`: default to 5km
   - If `range` provided without `lat`/`lng`: ignore range parameter

### Error Messages

- Zero value: `"Parameter 'range' must be greater than zero"`
- Negative value: `"Parameter 'range' must be a positive number"`
- Non-numeric: `"Parameter 'range' must be a valid number"`

---

## Decision 4: Query Parameter Handling

### Problem

How should the API handle various combinations of query parameters?

### Behavior Matrix

| lat | lng | range | Behavior |
|-----|-----|-------|----------|
| ❌ | ❌ | ❌ | Return all announcements (backward compatibility) |
| ❌ | ❌ | ✅ | Ignore range, return all announcements |
| ✅ | ❌ | ❌ | HTTP 400: lng required |
| ❌ | ✅ | ❌ | HTTP 400: lat required |
| ✅ | ✅ | ❌ | Filter with default 5km radius |
| ✅ | ✅ | ✅ | Filter with specified radius |

### Implementation Strategy

1. **Parse query parameters** from request.query
2. **Validate coordinate pair**: If one is present, both must be present
3. **Validate coordinate values**: Check ranges (-90 to 90, -180 to 180)
4. **Validate range value** (if provided): Must be positive, greater than zero
5. **Determine filter mode**:
   - No coordinates → return all announcements (no filtering)
   - Valid coordinates → apply distance filtering with range (default 5km)

---

## Decision 5: Database Query Strategy

### Problem

How to efficiently filter announcements by distance without impacting performance?

### Options Evaluated

1. **PostGIS Extension** (PostgreSQL spatial extension)
   - Pros: Native geo-spatial queries, highly optimized, built-in distance functions
   - Cons: Requires PostgreSQL, not available in SQLite, complex setup
   - **REJECTED**: Overkill for low-traffic use case, adds dependency

2. **Haversine in SQL Query** (calculate distance in database)
   - Pros: Efficient (filtering in DB), works with SQLite, no memory overhead, leverages database
   - Cons: Slightly more complex SQL (but manageable with Knex raw queries)
   - **SELECTED**: Best balance of performance and simplicity

3. **In-Memory Filtering** (fetch all, calculate distance, filter)
   - Pros: Simple implementation, no complex SQL
   - Cons: Loads all announcements into memory, inefficient even for small datasets
   - **REJECTED**: Unnecessarily inefficient when DB can handle it

### Decision: Haversine Distance Calculation in SQL Query

**Rationale**:
- Database is designed for filtering - use it for what it does best
- SQLite supports mathematical functions needed for Haversine (sin, cos, acos, radians)
- Knex supports raw SQL for complex calculations
- More memory efficient (only matching results loaded)
- Simpler overall implementation (no separate filtering step in application code)
- Works with both SQLite (current) and PostgreSQL (future migration)

**Query Implementation** (using Knex):

```typescript
await knex('announcement')
  .select(
    'id',
    'petName',
    'species',
    'description',
    'lat',
    'lng',
    'status',
    'createdAt',
    // ... other fields
    knex.raw(`
      (6371 * acos(
        cos(radians(?)) *
        cos(radians(lat)) *
        cos(radians(lng) - radians(?)) +
        sin(radians(?)) *
        sin(radians(lat))
      )) AS distance
    `, [lat, lng, lat])
  )
  .having('distance', '<', range)
  .orderBy('distance', 'asc');
```

**Query Breakdown**:
- `6371`: Earth's radius in kilometers
- `radians(?)`: Convert degrees to radians (parameters: lat, lng)
- `acos(...)`: Inverse cosine for angle calculation
- `cos(radians(lat))`: Cosine of latitude at each announcement location
- Result: Distance in kilometers as calculated column
- `HAVING 'distance' < range`: Filter by calculated distance
- `ORDER BY distance`: Return nearest announcements first

**Database Compatibility**:
- **SQLite**: Supports all needed functions (sin, cos, acos, radians via built-in math)
- **PostgreSQL**: Fully compatible (same functions available)

**Query Flow**:
1. Database calculates distance for each announcement using Haversine
2. Database filters results where distance < range
3. Database sorts by distance (nearest first)
4. Application receives only matching announcements (no post-processing needed)

**Performance Benefits**:
- Database-level filtering (no memory overhead)
- Only matching results loaded into application memory
- Efficient even as dataset grows
- Can add index on (lat, lng) for future optimization if needed

---

## Decision 6: Response Format

### Problem

Should the API response include calculated distances for each announcement?

### Decision: No Distance Information in Response

**From Clarification Session**: User selected "No - return only announcement data without distance information"

**Rationale**:
- Keeps response format unchanged (backward compatibility)
- Simplifies implementation (no schema modifications)
- Client applications can calculate distance independently if needed
- Reduces response payload size

**Response Format**: Existing announcement schema unchanged
- Announcements within range are included
- Announcements outside range are excluded
- No additional fields added to response

---

## Decision 7: Data Assumptions

### Problem

What assumptions can we make about existing announcement data?

### Decision: Location Data is Mandatory

**From Clarification Session**: "Location data is mandatory for all announcements (this edge case doesn't apply)"

**Implications**:
- All announcements in database have valid `lat` and `lng` values
- No need to handle NULL or missing location data during filtering
- No database migration required to add location columns (already exist)
- Simplifies filtering logic (no NULL checks needed)

**Validation**: Existing announcement validation ensures location data is present

---

## Implementation Checklist

Based on research decisions:

- [ ] Implement coordinate validation in `/lib/location-validation.ts`
- [ ] Implement range validation in `/lib/location-validation.ts`
- [ ] Write unit tests for coordinate validation (boundary cases: -90, 90, -180, 180)
- [ ] Write unit tests for range validation (zero, negative, positive cases)
- [ ] Modify route handler to parse and validate query parameters
- [ ] Modify database query to include Haversine distance calculation (Knex raw SQL)
- [ ] Add `.having('distance', '<', range)` filter to query
- [ ] Write integration tests for all parameter combinations (see Behavior Matrix)
- [ ] Test backward compatibility (no parameters → all announcements)
- [ ] Test distance calculation accuracy (compare with known distances)
- [ ] Document Haversine SQL query with JSDoc comments
- [ ] Ensure ESLint passes with no warnings

---

## Technical References

### Haversine Formula Implementation

**SQL Implementation** (used in Knex query):
```sql
-- Distance calculation in kilometers
(6371 * acos(
  cos(radians(:searchLat)) *
  cos(radians(announcement.lat)) *
  cos(radians(announcement.lng) - radians(:searchLng)) +
  sin(radians(:searchLat)) *
  sin(radians(announcement.lat))
)) AS distance
```

**Knex Query Builder Syntax** (using subquery):
```typescript
// Step 1: Create subquery with distance calculation
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
  );

// Step 2: Filter by distance using WHERE (not HAVING)
knex
  .from(subquery.as('announcements_with_distance'))
  .where('distance', '<', range)
  .orderBy('distance', 'asc');
```

**Why subquery instead of HAVING**:
- `WHERE distance < range` would fail because `distance` doesn't exist yet during WHERE clause execution
- `HAVING distance < range` works but is semantically incorrect (HAVING is for aggregations with GROUP BY)
- **Subquery** is the correct solution: calculate `distance` in inner query, filter with `WHERE` in outer query

**Formula Explanation**:
- `6371`: Earth's radius in kilometers
- `radians()`: SQLite/PostgreSQL function to convert degrees to radians
- `cos()`, `sin()`, `acos()`: Trigonometric functions (built-in SQL functions)
- Parameters: `searchLat`, `searchLng` (from user query), `lat`, `lng` (from database row)

### Validation Patterns

Standard Express query parameter validation:
```typescript
function validateCoordinates(lat?: string, lng?: string): ValidationResult {
  // Check pair requirement
  if ((lat && !lng) || (!lat && lng)) {
    return { valid: false, error: 'Both lat and lng must be provided together' };
  }
  
  // If both missing, valid (no filtering)
  if (!lat && !lng) {
    return { valid: true };
  }
  
  // Parse and validate ranges
  const latitude = parseFloat(lat!);
  const longitude = parseFloat(lng!);
  
  if (isNaN(latitude) || isNaN(longitude)) {
    return { valid: false, error: 'Coordinates must be valid numbers' };
  }
  
  if (latitude < -90 || latitude > 90) {
    return { valid: false, error: 'Latitude must be between -90 and 90' };
  }
  
  if (longitude < -180 || longitude > 180) {
    return { valid: false, error: 'Longitude must be between -180 and 180' };
  }
  
  return { valid: true, lat: latitude, lng: longitude };
}
```

---

## Testing Strategy

### Unit Tests (Vitest)

**Coordinate Validation** (`location-validation.test.ts`):
- Valid coordinate pairs
- Missing lng when lat provided (error)
- Missing lat when lng provided (error)
- Both missing (valid, no filtering)
- Latitude boundary cases: -90, -90.001, 90, 90.001
- Longitude boundary cases: -180, -180.001, 180, 180.001
- Non-numeric values: "abc", null, undefined
- Valid precision: 50.123456, 20.987654

**Range Validation** (`location-validation.test.ts`):
- Valid positive values: 1, 5, 10, 100, 1000
- Zero value (invalid, HTTP 400)
- Negative values (invalid, HTTP 400)
- Non-numeric: "abc", null, undefined
- Decimal values: 0.5, 2.75

### Integration Tests (SuperTest)

**Endpoint Behavior** (`announcements.test.ts`):
- No parameters → all announcements (backward compatibility)
- Valid lat/lng without range → filter with 5km default
- Valid lat/lng with custom range → filter with specified range
- Only lat provided → HTTP 400
- Only lng provided → HTTP 400
- Range provided without lat/lng → ignore range, return all
- Invalid lat (>90) → HTTP 400
- Invalid lng (>180) → HTTP 400
- Range = 0 → HTTP 400
- Range < 0 → HTTP 400
- Announcements at various distances → correct filtering
- Empty results (no announcements in range) → 200 OK, empty array

---

## Conclusion

All technical decisions documented. Ready to proceed to Phase 1 (Design & Contracts):
1. Data model documentation (announcements with location fields)
2. API contract (OpenAPI spec with new query parameters)
3. Quickstart guide for local testing

Next command: Continue with Phase 1 implementation plan.

