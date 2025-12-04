# Data Model: Web Browser Location for Pet Listings

**Date**: 2025-11-29  
**Feature**: 032-web-location-query

## Overview

This document defines the TypeScript interfaces, types, and data structures for the web location feature. All types follow existing project conventions in `/webApp/src/`.

---

## Core Domain Types

### 1. Location Coordinates

Represents geographic coordinates obtained from the browser Geolocation API.

```typescript
/**
 * Geographic coordinates (latitude/longitude) in decimal degrees.
 * Coordinates are rounded to 4 decimal places (~11m accuracy).
 */
interface Coordinates {
  /** Latitude in decimal degrees (range: -90 to 90) */
  lat: number;
  
  /** Longitude in decimal degrees (range: -180 to 180) */
  lng: number;
}
```

**Validation Rules**:
- `lat` must be between -90 and 90
- `lng` must be between -180 and 180
- Both values rounded to 4 decimal places before API submission

**Example**:
```typescript
const coords: Coordinates = { lat: 52.2297, lng: 21.0122 }; // Warsaw, Poland
```

---

### 2. Permission State

Represents the current browser geolocation permission status.

```typescript
/**
 * Browser geolocation permission state.
 * Maps to PermissionState from Permissions API plus 'loading' for initial fetch.
 */
type PermissionState = 
  | 'granted'   // User allowed location access
  | 'denied'    // User blocked location access
  | 'prompt'    // Permission not yet requested
  | 'loading';  // Permission query in progress
```

**State Transitions**:
```
Initial: 'loading'
  ↓
After permission query:
  → 'granted' (user previously allowed)
  → 'denied' (user previously blocked)
  → 'prompt' (not yet requested)
  ↓
After user responds to prompt (if 'prompt'):
  → 'granted' (user clicked "Allow")
  → 'denied' (user clicked "Block" or dismissed)
```

---

### 3. Geolocation Hook State

Complete state managed by the `useGeolocation` custom hook.

```typescript
/**
 * State object returned by useGeolocation hook.
 * Manages location fetching, permission state, and error handling.
 */
interface GeolocationState {
  /** Current user coordinates (null if unavailable or not yet fetched) */
  coordinates: Coordinates | null;
  
  /** Current permission state for geolocation */
  permissionState: PermissionState;
  
  /** Geolocation error (null if no error occurred) */
  error: GeolocationPositionError | null;
  
  /** True while location is being fetched */
  isLoading: boolean;
}
```

**Example States**:

Success case:
```typescript
{
  coordinates: { lat: 52.2297, lng: 21.0122 },
  permissionState: 'granted',
  error: null,
  isLoading: false
}
```

Denied permission:
```typescript
{
  coordinates: null,
  permissionState: 'denied',
  error: null, // No GeolocationPositionError, just denied state
  isLoading: false
}
```

Timeout error:
```typescript
{
  coordinates: null,
  permissionState: 'granted', // Permission was granted but fetch timed out
  error: GeolocationPositionError { code: 3, message: 'Timeout expired' },
  isLoading: false
}
```

---

### 4. Pet Listings Fetch Options

Extended options for fetching pet listings with optional location filtering.

```typescript
/**
 * Options for fetching pet listings from the API.
 * Extends existing AnimalRepository.getAnimals() with location support.
 */
interface PetListingsFetchOptions {
  /** Optional location for proximity-based filtering */
  location?: Coordinates;
  
  // Future: Add other filters here (species, age, size, etc.)
}
```

**Backward Compatibility**:
- `location` is optional - existing calls to `getAnimals()` without parameters remain valid
- No breaking changes to existing codebase

---

## Browser API Types (Reference)

These types are provided by TypeScript's built-in DOM types. Documented here for completeness.

### GeolocationPosition (Native)

```typescript
interface GeolocationPosition {
  coords: {
    latitude: number;    // Decimal degrees
    longitude: number;   // Decimal degrees
    accuracy: number;    // Accuracy in meters
    altitude: number | null;
    altitudeAccuracy: number | null;
    heading: number | null;
    speed: number | null;
  };
  timestamp: number; // Unix timestamp in milliseconds
}
```

**Usage**: Returned by `navigator.geolocation.getCurrentPosition()` success callback.

---

### GeolocationPositionError (Native)

```typescript
interface GeolocationPositionError {
  code: number;     // Error code (1: PERMISSION_DENIED, 2: POSITION_UNAVAILABLE, 3: TIMEOUT)
  message: string;  // Human-readable error description
}
```

**Error Codes**:
- `1` (PERMISSION_DENIED): User denied permission request
- `2` (POSITION_UNAVAILABLE): Device cannot determine location (GPS disabled, no signal, etc.)
- `3` (TIMEOUT): Location fetch exceeded timeout (3000ms per spec)

**Usage**: Returned by `navigator.geolocation.getCurrentPosition()` error callback.

---

### PermissionStatus (Native)

```typescript
interface PermissionStatus {
  state: 'granted' | 'denied' | 'prompt';
  onchange: ((this: PermissionStatus, ev: Event) => any) | null;
}
```

**Usage**: Returned by `navigator.permissions.query({ name: 'geolocation' })`.

---

## Component Props Interfaces

### 1. LocationBanner Props

Banner displayed when location permission is blocked.

```typescript
/**
 * Props for LocationBanner component.
 * Displays informational message about blocked location permission.
 */
interface LocationBannerProps {
  /** Callback invoked when user clicks the close (X) button */
  onClose: () => void;
}
```

**Example**:
```tsx
<LocationBanner onClose={() => setShowBanner(false)} />
```

---

### 2. LoadingOverlay Props

Full-page spinner displayed during location and pet data fetch.

```typescript
/**
 * Props for LoadingOverlay component.
 * Displays full-page spinner blocking interaction.
 */
interface LoadingOverlayProps {
  /** Optional message to display below spinner (defaults to "Loading...") */
  message?: string;
}
```

**Example**:
```tsx
<LoadingOverlay message="Finding pets near you..." />
```

---

### 3. ErrorMessage Props

Error display with retry functionality for API failures.

```typescript
/**
 * Props for ErrorMessage component.
 * Displays error message with retry button.
 */
interface ErrorMessageProps {
  /** Error message text to display */
  message: string;
  
  /** Callback invoked when user clicks retry button */
  onRetry: () => void;
}
```

**Example**:
```tsx
<ErrorMessage 
  message="Unable to load pets. Please try again." 
  onRetry={() => refetchPets()} 
/>
```

---

### 4. EmptyState Props

Empty state message displayed when API returns zero pets.

```typescript
/**
 * Props for EmptyState component.
 * Displays message when no pets are available.
 */
interface EmptyStateProps {
  /** Message text to display (defaults to "No pets nearby") */
  message?: string;
}
```

**Example**:
```tsx
<EmptyState message="No pets nearby" />
```

---

## Utility Function Signatures

### formatCoordinates

Rounds coordinates to 4 decimal places for API submission.

```typescript
/**
 * Formats coordinates to 4 decimal places for API submission.
 * 
 * @param lat - Latitude in decimal degrees
 * @param lng - Longitude in decimal degrees
 * @returns Formatted coordinates with 4 decimal precision (~11m accuracy)
 * 
 * @example
 * formatCoordinates(52.229676, 21.012229)
 * // Returns: { lat: 52.2297, lng: 21.0122 }
 */
function formatCoordinates(lat: number, lng: number): Coordinates;
```

---

## Extended API Repository Interface

### AnimalRepository.getAnimals (Extended)

Extended signature for existing `getAnimals()` method in `animal-repository.ts`.

**Current Signature** (existing):
```typescript
async getAnimals(): Promise<Animal[]>
```

**New Signature** (backward compatible):
```typescript
/**
 * Fetches pet listings from the API with optional location-based filtering.
 * 
 * @param options - Optional fetch options including location coordinates
 * @returns Promise resolving to array of Animal objects
 * @throws Error if API request fails (4xx, 5xx status codes)
 * 
 * @example
 * // Without location (existing behavior)
 * const pets = await repository.getAnimals();
 * 
 * @example
 * // With location (new behavior)
 * const pets = await repository.getAnimals({ 
 *   location: { lat: 52.2297, lng: 21.0122 } 
 * });
 */
async getAnimals(options?: PetListingsFetchOptions): Promise<Animal[]>
```

**Implementation Note**:
- Default parameter `options = {}` maintains backward compatibility
- URL construction uses `URLSearchParams` to append `lat` and `lng` query params when `options.location` is provided
- No changes to return type or error handling behavior

---

## State Transitions

### Pet List Page State Machine

```
┌─────────────────┐
│  Initial Load   │
│  isLoading=true │
└────────┬────────┘
         │
         ├─── Check permission state
         │
         ├─── 'granted' or 'prompt'
         │    ↓
         │    Request location (3s timeout)
         │    ↓
         │    ┌─ Success: { lat, lng }
         │    │  ↓
         │    │  Fetch pets with location
         │    │  ↓
         │    │  Display pets
         │    │
         │    ├─ Timeout/Error
         │    │  ↓
         │    │  Fetch pets without location (fallback)
         │    │  ↓
         │    │  Display all pets
         │    │
         │    └─ User denies prompt
         │       ↓
         │       Fetch pets without location
         │       ↓
         │       Display banner + all pets
         │
         └─── 'denied'
              ↓
              Fetch pets without location
              ↓
              Display banner + all pets
```

---

## Validation Rules

### Coordinates Validation

```typescript
function isValidCoordinates(coords: Coordinates): boolean {
  return (
    coords.lat >= -90 &&
    coords.lat <= 90 &&
    coords.lng >= -180 &&
    coords.lng <= 180 &&
    !isNaN(coords.lat) &&
    !isNaN(coords.lng)
  );
}
```

### Coordinate Formatting

```typescript
function formatCoordinates(lat: number, lng: number): Coordinates {
  return {
    lat: parseFloat(lat.toFixed(4)),
    lng: parseFloat(lng.toFixed(4))
  };
}
```

---

## Example: Complete Flow

```typescript
// 1. Component mounts, use-geolocation hook initializes
const { coordinates, permissionState, isLoading: locationLoading } = useGeolocation();

// 2. use-animal-list hook consumes coordinates
const { pets, isLoading: petsLoading, error, refetch } = useAnimalList(coordinates);

// 3. Combined loading state
const isLoading = locationLoading || petsLoading;

// 4. Render logic
if (isLoading) {
  return <LoadingOverlay />;
}

if (permissionState === 'denied') {
  return (
    <>
      <LocationBanner onClose={() => setShowBanner(false)} />
      <PetList pets={pets} />
    </>
  );
}

if (error) {
  return <ErrorMessage message={error.message} onRetry={refetch} />;
}

if (pets.length === 0) {
  return <EmptyState message="No pets nearby" />;
}

return <PetList pets={pets} />;
```

---

## Summary

**New Types Defined**: 4 (Coordinates, PermissionState, GeolocationState, PetListingsFetchOptions)  
**Component Props Interfaces**: 4 (LocationBanner, LoadingOverlay, ErrorMessage, EmptyState)  
**Utility Functions**: 1 (formatCoordinates)  
**Extended APIs**: 1 (AnimalRepository.getAnimals)  
**Backward Compatibility**: ✅ Maintained (all changes are additive, optional parameters only)

All types are co-located with their respective implementations:
- Domain types: `/webApp/src/types/location.ts` (new file)
- Component props: In respective component files
- Hook state: In `/webApp/src/hooks/use-geolocation.ts`
