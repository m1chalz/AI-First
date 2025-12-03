# Contract: AnimalRepository.getAnimals() Extension

**Date**: 2025-11-29  
**Feature**: 032-web-location-query  
**Contract Type**: Service Method Extension (Web Frontend)

## Overview

This document describes the extension to the existing `AnimalRepository.getAnimals()` method to support optional location-based filtering. The change is **backward compatible** - existing calls without parameters continue to work unchanged.

---

## Service Method Contract

### AnimalRepository.getAnimals()

**File**: `/webApp/src/services/animal-repository.ts`

**Current Signature** (before change):
```typescript
async getAnimals(): Promise<Animal[]>
```

**New Signature** (after change):
```typescript
async getAnimals(options?: PetListingsFetchOptions): Promise<Animal[]>
```

---

## Request Parameters

### PetListingsFetchOptions

Optional parameter object for filtering pet listings.

```typescript
interface PetListingsFetchOptions {
  /** Optional location coordinates for proximity-based filtering */
  location?: {
    /** Latitude in decimal degrees, rounded to 4 decimal places */
    lat: number;
    
    /** Longitude in decimal degrees, rounded to 4 decimal places */
    lng: number;
  };
}
```

**Parameter Constraints**:
- `options` is optional (default: `{}`)
- `location` is optional within `options`
- `lat` must be between -90 and 90
- `lng` must be between -180 and 180
- Both `lat` and `lng` MUST be rounded to 4 decimal places before API submission

**Example Calls**:

Without location (existing behavior):
```typescript
const pets = await animalRepository.getAnimals();
// API call: GET /api/v1/announcements
```

With location (new behavior):
```typescript
const pets = await animalRepository.getAnimals({ 
  location: { lat: 52.2297, lng: 21.0122 } 
});
// API call: GET /api/v1/announcements?lat=52.2297&lng=21.0122
```

---

## URL Construction

### Without Location

```
GET /api/v1/announcements
```

### With Location

```
GET /api/v1/announcements?lat=52.2297&lng=21.0122
```

**Query Parameters**:
- `lat` (optional): Latitude in decimal degrees (4 decimal places)
- `lng` (optional): Longitude in decimal degrees (4 decimal places)

**Implementation**:
```typescript
async getAnimals(options: PetListingsFetchOptions = {}): Promise<Animal[]> {
  const url = new URL(`${this.apiBaseUrl}/api/v1/announcements`);
  
  if (options.location) {
    url.searchParams.append('lat', options.location.lat.toFixed(4));
    url.searchParams.append('lng', options.location.lng.toFixed(4));
  }
  
  const response = await fetch(url.toString());
  
  if (!response.ok) {
    throw new Error(`Failed to fetch animals: ${response.status} ${response.statusText}`);
  }
  
  const data: BackendAnnouncementsResponse = await response.json();
  return data.data;
}
```

---

## Response Contract

### Success Response

**HTTP Status**: 200 OK

**Response Body** (unchanged):
```typescript
interface BackendAnnouncementsResponse {
  data: Animal[];
}
```

**Response Type**: `Animal[]`

The response format is **unchanged** from the existing implementation. The backend API already supports the `lat` and `lng` query parameters (per feature spec) and returns the same data structure regardless of whether location parameters are provided.

---

## Error Handling

### Client-Side Errors (unchanged)

**Network Errors** (e.g., no internet, DNS failure):
```typescript
throw new Error('Failed to fetch animals: Network error');
```

**HTTP Errors** (4xx, 5xx status codes):
```typescript
throw new Error(`Failed to fetch animals: ${status} ${statusText}`);
// Examples:
// - 404: "Failed to fetch animals: 404 Not Found"
// - 500: "Failed to fetch animals: 500 Internal Server Error"
```

### Error Handling Behavior (unchanged)

The error handling behavior is **unchanged** from the existing implementation:
- Throws `Error` for all failed requests
- Error message includes HTTP status code and status text
- Caller (hook/component) is responsible for catching and displaying errors

---

## Backward Compatibility

### Existing Code (no changes required)

All existing calls to `getAnimals()` without parameters continue to work:

```typescript
// ✅ Still valid - no breaking changes
const pets = await animalRepository.getAnimals();
```

### New Code (optional location parameter)

New calls can optionally provide location:

```typescript
// ✅ New behavior - location-based filtering
const pets = await animalRepository.getAnimals({
  location: { lat: 52.2297, lng: 21.0122 }
});
```

---

## Backend API Contract (Reference)

**Important**: The backend API (`/server/src/routes/announcements.ts`) already supports the `lat` and `lng` query parameters per the feature specification. **No backend changes are required** for this feature.

### Existing Backend Endpoint

```
GET /api/v1/announcements?lat={latitude}&lng={longitude}
```

**Query Parameters** (already supported):
- `lat` (optional): Latitude in decimal degrees
- `lng` (optional): Longitude in decimal degrees

**Response** (unchanged):
```json
{
  "data": [
    {
      "id": "1",
      "name": "Max",
      "species": "Dog",
      "breed": "Golden Retriever",
      "age": 3,
      "sex": "Male",
      "description": "Friendly dog",
      "chipNumber": "123456789",
      "photoUrl": "https://example.com/photo.jpg",
      "createdAt": "2024-01-15T10:30:00Z"
    }
  ]
}
```

**Filtering Behavior** (backend implementation):
- When `lat` and `lng` are provided: Backend filters pets by proximity (implementation details in backend spec)
- When `lat` and `lng` are omitted: Backend returns all available pets (no filtering)

---

## Usage Examples

### Example 1: Fetch pets near user location

```typescript
// In a React component or hook
const location = { lat: 52.2297, lng: 21.0122 };

try {
  const pets = await animalRepository.getAnimals({ location });
  console.log(`Found ${pets.length} pets nearby`);
} catch (error) {
  console.error('Failed to fetch pets:', error.message);
}
```

### Example 2: Fetch all pets (no location)

```typescript
// Fallback when location is unavailable
try {
  const pets = await animalRepository.getAnimals();
  console.log(`Found ${pets.length} total pets`);
} catch (error) {
  console.error('Failed to fetch pets:', error.message);
}
```

### Example 3: Conditional location filtering

```typescript
// In use-animal-list hook
const fetchPets = async (coordinates: Coordinates | null) => {
  const options = coordinates 
    ? { location: coordinates }
    : {}; // Empty options object = no location filtering
  
  return animalRepository.getAnimals(options);
};
```

---

## Testing Contract

### Unit Tests Required

**File**: `/webApp/src/services/__tests__/animal-repository.test.ts`

**Test Cases**:

1. ✅ Should fetch pets without location parameter (existing behavior)
   - Given: `getAnimals()` called without arguments
   - When: API responds with success
   - Then: Returns array of Animal objects
   - Verify: URL is `/api/v1/announcements` (no query params)

2. ✅ Should fetch pets with location parameter (new behavior)
   - Given: `getAnimals({ location: { lat: 52.2297, lng: 21.0122 } })` called
   - When: API responds with success
   - Then: Returns array of Animal objects
   - Verify: URL includes `?lat=52.2297&lng=21.0122`

3. ✅ Should format coordinates to 4 decimal places
   - Given: `getAnimals({ location: { lat: 52.229676, lng: 21.012229 } })`
   - Then: Query params are `lat=52.2297&lng=21.0122` (rounded)

4. ✅ Should throw error on HTTP failure (unchanged behavior)
   - Given: API responds with 500 status
   - When: `getAnimals()` called
   - Then: Throws Error with message including status code

---

## Integration Points

### Consumers of This Method

1. **use-animal-list Hook** (primary consumer)
   - File: `/webApp/src/hooks/use-animal-list.ts`
   - Usage: Calls `getAnimals(options)` with coordinates from `use-geolocation` hook
   - Behavior: Passes location when available, omits when null

2. **AnimalList Component** (indirect consumer)
   - File: `/webApp/src/components/AnimalList/AnimalList.tsx`
   - Usage: Uses `use-animal-list` hook which calls `getAnimals()`
   - Behavior: Displays loading state, error state, or animal list

---

## Summary

**Change Type**: Backward-compatible method extension  
**Breaking Changes**: None  
**New Dependencies**: None (uses native URLSearchParams)  
**Backend Changes**: None (endpoint already supports query params)  
**Migration Required**: No (existing calls continue to work)  

**Key Points**:
- ✅ Optional parameter maintains backward compatibility
- ✅ Default empty object `{}` preserves existing behavior
- ✅ URL construction uses native `URLSearchParams` API
- ✅ Coordinates formatted to 4 decimal places (11m accuracy)
- ✅ Error handling unchanged
- ✅ Backend API already supports location filtering (no changes needed)

