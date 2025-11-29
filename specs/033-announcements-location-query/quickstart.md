# Quickstart: Announcements Location Query

**Feature**: 033-announcements-location-query  
**Purpose**: Quick guide to test location-based filtering locally

## Overview

This guide shows how to test the new location query parameters for the `/api/v1/announcements` endpoint. You'll learn how to set up test data, make filtered requests, and verify the results.

---

## Prerequisites

- Backend server running locally (Node.js v24 + Express + SQLite)
- Database seeded with announcement test data
- HTTP client (curl, Postman, or browser)

### Starting the Backend Server

```bash
# From repository root
cd server

# Install dependencies (if not already done)
npm install

# Run development server
npm run dev

# Server should start on http://localhost:3000
```

**Verify Server is Running**:
```bash
curl http://localhost:3000/api/v1/announcements
```

Expected: JSON response with announcements list

---

## Test Data Setup

### Sample Announcements with Known Locations

For testing, use announcements at known geographic locations:

**Krakow, Poland** (Origin):
- Lat: 50.0614, Lng: 19.9383
- Announcements: Place 2-3 announcements here

**Nearby (2km from Krakow)**:
- Lat: 50.0700, Lng: 19.9500
- Announcements: Place 1-2 announcements here

**Warsaw, Poland** (252km from Krakow):
- Lat: 52.2297, Lng: 21.0122
- Announcements: Place 1-2 announcements here

### Seeding Test Data

**Option 1: Manual Insert via SQL**

```sql
-- Krakow announcements
INSERT INTO announcement (petName, species, description, lat, lng, status, createdAt)
VALUES 
  ('Max', 'dog', 'Golden retriever', 50.0614, 19.9383, 'active', datetime('now')),
  ('Luna', 'cat', 'Black cat', 50.0620, 19.9390, 'active', datetime('now'));

-- Nearby (2km) announcement
INSERT INTO announcement (petName, species, description, lat, lng, status, createdAt)
VALUES ('Buddy', 'dog', 'Beagle', 50.0700, 19.9500, 'active', datetime('now'));

-- Warsaw (252km) announcement
INSERT INTO announcement (petName, species, description, lat, lng, status, createdAt)
VALUES ('Charlie', 'cat', 'Tabby cat', 52.2297, 21.0122, 'active', datetime('now'));
```

**Option 2: Use Existing Announcements**

If your database already has announcements, note their coordinates for testing.

---

## Testing Scenarios

### Scenario 1: No Filtering (Backward Compatibility)

**Request**: No query parameters

```bash
curl "http://localhost:3000/api/v1/announcements"
```

**Expected Result**:
- HTTP 200 OK
- All announcements returned (regardless of location)
- Response format unchanged from existing implementation

**Verification**:
```bash
# Count total announcements
curl -s "http://localhost:3000/api/v1/announcements" | jq '.total'
```

---

### Scenario 2: Filter with Default 5km Radius

**Request**: Provide `lat` and `lng` without `range`

```bash
curl "http://localhost:3000/api/v1/announcements?lat=50.0614&lng=19.9383"
```

**Expected Result**:
- HTTP 200 OK
- Only announcements within 5km of Krakow (50.0614, 19.9383)
- Should include: Max, Luna (at origin), Buddy (2km away)
- Should exclude: Charlie (252km away in Warsaw)

**Verification**:
```bash
# Should return 3 announcements (Max, Luna, Buddy)
curl -s "http://localhost:3000/api/v1/announcements?lat=50.0614&lng=19.9383" | jq '.total'
```

---

### Scenario 3: Filter with Custom Range

**Request**: Provide `lat`, `lng`, and custom `range`

```bash
# 10km radius
curl "http://localhost:3000/api/v1/announcements?lat=50.0614&lng=19.9383&range=10"
```

**Expected Result**:
- HTTP 200 OK
- Only announcements within 10km of Krakow
- Same as 5km test (no announcements between 5-10km in test data)

```bash
# 1km radius (very narrow)
curl "http://localhost:3000/api/v1/announcements?lat=50.0614&lng=19.9383&range=1"
```

**Expected Result**:
- HTTP 200 OK
- Only announcements within 1km of Krakow
- Should include: Max, Luna (at origin, ~0km)
- Should exclude: Buddy (2km away), Charlie (252km away)

```bash
# 300km radius (very large)
curl "http://localhost:3000/api/v1/announcements?lat=50.0614&lng=19.9383&range=300"
```

**Expected Result**:
- HTTP 200 OK
- All test announcements included (Krakow + Nearby + Warsaw)

---

### Scenario 4: Empty Results

**Request**: Search in location with no announcements

```bash
# Middle of the ocean
curl "http://localhost:3000/api/v1/announcements?lat=0.0&lng=0.0&range=10"
```

**Expected Result**:
- HTTP 200 OK
- Empty announcements array: `{ "announcements": [], "total": 0 }`

---

### Scenario 5: Validation Errors

**Test 5.1: Missing lng**

```bash
curl "http://localhost:3000/api/v1/announcements?lat=50.0614"
```

**Expected Result**:
- HTTP 400 Bad Request
- Error message: `"Parameter 'lng' is required when 'lat' is provided"`

**Test 5.2: Missing lat**

```bash
curl "http://localhost:3000/api/v1/announcements?lng=19.9383"
```

**Expected Result**:
- HTTP 400 Bad Request
- Error message: `"Parameter 'lat' is required when 'lng' is provided"`

**Test 5.3: Invalid latitude (out of range)**

```bash
curl "http://localhost:3000/api/v1/announcements?lat=95&lng=19.9383"
```

**Expected Result**:
- HTTP 400 Bad Request
- Error message: `"Parameter 'lat' must be between -90 and 90"`

**Test 5.4: Invalid longitude (out of range)**

```bash
curl "http://localhost:3000/api/v1/announcements?lat=50.0614&lng=200"
```

**Expected Result**:
- HTTP 400 Bad Request
- Error message: `"Parameter 'lng' must be between -180 and 180"`

**Test 5.5: Range = 0 (invalid)**

```bash
curl "http://localhost:3000/api/v1/announcements?lat=50.0614&lng=19.9383&range=0"
```

**Expected Result**:
- HTTP 400 Bad Request
- Error message: `"Parameter 'range' must be greater than zero"`

**Test 5.6: Negative range**

```bash
curl "http://localhost:3000/api/v1/announcements?lat=50.0614&lng=19.9383&range=-5"
```

**Expected Result**:
- HTTP 400 Bad Request
- Error message: `"Parameter 'range' must be a positive number"`

**Test 5.7: Non-numeric coordinates**

```bash
curl "http://localhost:3000/api/v1/announcements?lat=abc&lng=xyz"
```

**Expected Result**:
- HTTP 400 Bad Request
- Error message: `"Parameter 'lat' must be a valid number"`

---

### Scenario 6: Range Without Coordinates

**Request**: Provide `range` but no `lat`/`lng`

```bash
curl "http://localhost:3000/api/v1/announcements?range=10"
```

**Expected Result**:
- HTTP 200 OK
- Range parameter ignored
- All announcements returned (same as no parameters)

---

## Using Postman

### Import Collection

Create a Postman collection with these requests:

**1. All Announcements (No Filter)**
- Method: GET
- URL: `http://localhost:3000/api/v1/announcements`

**2. Default 5km Radius**
- Method: GET
- URL: `http://localhost:3000/api/v1/announcements?lat=50.0614&lng=19.9383`

**3. Custom 10km Radius**
- Method: GET
- URL: `http://localhost:3000/api/v1/announcements?lat=50.0614&lng=19.9383&range=10`

**4. Narrow 1km Radius**
- Method: GET
- URL: `http://localhost:3000/api/v1/announcements?lat=50.0614&lng=19.9383&range=1`

**5. Validation Error: Missing lng**
- Method: GET
- URL: `http://localhost:3000/api/v1/announcements?lat=50.0614`
- Expected: 400 Bad Request

---

## Verifying Distance Calculations

### Manual Distance Verification

To verify Haversine distance calculations are correct:

**Krakow to Warsaw**:
- Expected: ~252 km
- Coordinates: (50.0614, 19.9383) → (52.2297, 21.0122)
- Tool: https://www.movable-type.co.uk/scripts/latlong.html

**Krakow to Nearby**:
- Expected: ~2 km
- Coordinates: (50.0614, 19.9383) → (50.0700, 19.9500)

### Using Online Calculator

1. Visit https://www.movable-type.co.uk/scripts/latlong.html
2. Enter coordinates:
   - Point 1: 50.0614, 19.9383 (Krakow)
   - Point 2: 52.2297, 21.0122 (Warsaw)
3. Verify calculated distance matches Haversine implementation

---

## Integration with Frontend (Future)

Once frontend platforms consume this API:

**Web (React)**:
```typescript
// Fetch announcements near user location
const response = await fetch(
  `/api/v1/announcements?lat=${userLat}&lng=${userLng}&range=10`
);
const data = await response.json();
```

**Android (Kotlin)**:
```kotlin
// Repository implementation
suspend fun getAnnouncementsNearby(
  lat: Double,
  lng: Double,
  range: Double = 5.0
): Result<List<Announcement>> {
  val response = api.get("/api/v1/announcements?lat=$lat&lng=$lng&range=$range")
  // ...
}
```

**iOS (Swift)**:
```swift
// Repository implementation
func getAnnouncementsNearby(
  lat: Double,
  lng: Double,
  range: Double = 5.0
) async throws -> [Announcement] {
  let url = "/api/v1/announcements?lat=\(lat)&lng=\(lng)&range=\(range)"
  return try await httpClient.get(url)
}
```

---

## Troubleshooting

### Server Not Starting

**Problem**: `npm run dev` fails

**Solution**:
```bash
# Check Node.js version
node --version  # Should be v24.x

# Clean install
rm -rf node_modules package-lock.json
npm install
```

### Database Connection Error

**Problem**: `Error: SQLITE_ERROR: no such table: announcement`

**Solution**:
```bash
# Run migrations
npm run knex:migrate

# Or create database from scratch
npm run db:init
```

### All Announcements Have Same Location

**Problem**: Can't test distance filtering because all test data at same coordinates

**Solution**: Use SQL from "Test Data Setup" section to insert announcements at different locations

### Validation Not Working

**Problem**: Invalid parameters don't return 400 errors

**Solution**: Ensure validation middleware is implemented and registered before route handler

---

## Performance Testing (Optional)

For load testing the endpoint:

```bash
# Install Apache Bench (if not already installed)
# brew install httpd (macOS)
# apt-get install apache2-utils (Linux)

# Test 100 requests with 10 concurrent
ab -n 100 -c 10 "http://localhost:3000/api/v1/announcements?lat=50.0614&lng=19.9383&range=10"
```

**Note**: Performance testing is optional per specification ("nie przejmuj sie performance'm").

---

## Next Steps

After verifying the backend endpoint works:

1. **Frontend Integration**: Update web/mobile platforms to use new parameters
2. **User Location**: Implement geolocation in frontend to auto-populate lat/lng
3. **UI Enhancements**: Add distance display, map view, radius slider
4. **Analytics**: Track usage of location filtering feature

---

## Common Test Cases Summary

| Test Case | URL | Expected Status | Expected Result |
|-----------|-----|-----------------|-----------------|
| No parameters | `/announcements` | 200 | All announcements |
| Default radius | `/announcements?lat=50.06&lng=19.93` | 200 | Within 5km |
| Custom radius | `/announcements?lat=50.06&lng=19.93&range=10` | 200 | Within 10km |
| Missing lng | `/announcements?lat=50.06` | 400 | Error message |
| Missing lat | `/announcements?lng=19.93` | 400 | Error message |
| Invalid lat | `/announcements?lat=95&lng=19.93` | 400 | Error message |
| Invalid lng | `/announcements?lat=50.06&lng=200` | 400 | Error message |
| Range = 0 | `/announcements?lat=50.06&lng=19.93&range=0` | 400 | Error message |
| Negative range | `/announcements?lat=50.06&lng=19.93&range=-5` | 400 | Error message |
| Range without coords | `/announcements?range=10` | 200 | All announcements (range ignored) |
| Empty results | `/announcements?lat=0&lng=0&range=10` | 200 | Empty array |

---

## Quick Reference

**Default Values**:
- Range: 5 km (when lat/lng provided without range)

**Valid Ranges**:
- Latitude: -90 to 90
- Longitude: -180 to 180
- Range: > 0 (no maximum)

**Backward Compatibility**:
- No parameters → All announcements (unchanged behavior)

**Validation Rules**:
- Coordinate pair: Both lat and lng required together
- Range dependency: Only applies with lat/lng

---

For detailed API contract, see [contracts/announcements-api.yaml](./contracts/announcements-api.yaml)

For implementation details, see [plan.md](./plan.md) and [research.md](./research.md)

