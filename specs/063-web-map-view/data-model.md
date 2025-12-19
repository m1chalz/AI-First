# Data Model: Web Map Component on Landing Page

**Feature**: 063-web-map-view  
**Date**: 2025-12-18  
**Phase**: 1 (Design & Contracts)

## Overview

This document defines the data model for the interactive map component on the web landing page. The model includes entities for map state, location coordinates, error handling, and user interactions.

---

## 1. Core Entities

### 1.1 MapState

Represents the current state of the map component, including center coordinates, zoom level, loading status, and error states.

```typescript
interface MapState {
  /** Center coordinates of the map (latitude, longitude) */
  center: Coordinates;
  
  /** Zoom level (13 = ~10 km radius) */
  zoom: number;
  
  /** Loading state for map initialization */
  isLoading: boolean;
  
  /** Current error state (null if no error) */
  error: MapError | null;
  
  /** Whether permission prompt should be displayed */
  showPermissionPrompt: boolean;
}
```

**Validation Rules**:
- `zoom`: Must be between 10 (min) and 18 (max)
- `center`: Must be valid coordinates (see Coordinates entity)

**State Transitions**:
1. Initial → Loading (map initialization)
2. Loading → Success (map loaded successfully)
3. Loading → Error (map load failed)
4. Success → Error (runtime error)

---

### 1.2 Coordinates

Represents a geographic location with latitude and longitude.

```typescript
interface Coordinates {
  /** Latitude in decimal degrees (-90 to 90) */
  latitude: number;
  
  /** Longitude in decimal degrees (-180 to 180) */
  longitude: number;
}
```

**Validation Rules**:
- `latitude`: Must be between -90 and 90 (inclusive)
- `longitude`: Must be between -180 and 180 (inclusive)

**Special Values**:
- **Wrocław fallback**: `{ latitude: 51.1079, longitude: 17.0385 }`

**Relationships**:
- Used in `MapState.center`
- Provided by `GeolocationContext`

---

### 1.3 MapError

Represents different error states that can occur with the map component.

```typescript
type MapErrorType = 
  | 'PERMISSION_DENIED'
  | 'PERMISSION_NOT_REQUESTED'
  | 'LOCATION_UNAVAILABLE'
  | 'MAP_LOAD_FAILED';

interface MapError {
  /** Type of error */
  type: MapErrorType;
  
  /** Human-readable error message */
  message: string;
  
  /** Whether to show map in fallback mode (for LOCATION_UNAVAILABLE) */
  showFallbackMap: boolean;
}
```

**Error Types**:

| Type | Message | Show Fallback Map | User Action |
|------|---------|-------------------|-------------|
| `PERMISSION_NOT_REQUESTED` | "Location permission is required to display the map" | No | Click consent button |
| `PERMISSION_DENIED` | "Location access was denied. Please enable location in your browser settings." | No | Enable in browser settings |
| `LOCATION_UNAVAILABLE` | "Unable to get your location. Please refresh the page to try again." | Yes | Refresh page |
| `MAP_LOAD_FAILED` | "Failed to load map. Please refresh the page to try again." | No | Refresh page |

**State Lifecycle**:
1. Error created when failure occurs
2. Error displayed in UI (MapErrorState or MapPermissionPrompt component)
3. Error cleared when user refreshes page

---

### 1.4 MapViewProps

Props interface for the main MapView component.

```typescript
interface MapViewProps {
  /** Additional CSS class name for styling */
  className?: string;
  
  /** Test identifier for E2E tests */
  'data-testid'?: string;
}
```

---

## 2. Derived State

### 2.1 Center Calculation Logic

The map center is determined by the following priority:

```typescript
function determineMapCenter(): Coordinates {
  // 1. Try current location from GeolocationContext
  if (geolocation.coordinates) {
    return geolocation.coordinates;
  }
  
  // 2. Fallback to Wrocław, PL
  return { latitude: 51.1079, longitude: 17.0385 };
}
```

---

## 3. Relationships

```
GeolocationContext
       │
       │ provides
       ▼
   Coordinates ────────────► MapState.center
                                   │
                                   │ used by
                                   ▼
                            MapView Component
```

---

## 4. Data Flow

### 4.1 Initial Load (Permission Granted, Location Available)

```
1. User lands on page
2. GeolocationContext requests location
3. Browser returns coordinates
4. MapView component renders with center = coordinates
5. Map displays centered on user location
```

### 4.2 Initial Load (Permission Denied)

```
1. User lands on page
2. GeolocationContext checks permission
3. Permission = denied
4. MapView renders MapPermissionPrompt
5. User clicks "Allow Location Access"
6. Browser permission dialog appears
7a. User grants permission → Go to 4.1
7b. User denies permission → Show error message
```

### 4.3 Location Unavailable (Permission Granted)

```
1. User lands on page
2. GeolocationContext requests location
3. Location timeout / GPS disabled
4. Error: LOCATION_UNAVAILABLE
5. Map displays at fallback location (Wrocław) with error message "Please refresh the page to try again"
6. User refreshes page → Go to step 1
```

### 4.4 Map Load Failure

```
1. MapView renders MapContainer
2. Leaflet fails to load tiles (network error)
3. Error: MAP_LOAD_FAILED
4. MapErrorState displays error message "Please refresh the page to try again"
5. User refreshes page → Go to step 1
```

---

## 5. Constants

```typescript
/** Default zoom level for 10 km radius viewport */
export const DEFAULT_ZOOM = 13;

/** Minimum allowed zoom level */
export const MIN_ZOOM = 10;

/** Maximum allowed zoom level */
export const MAX_ZOOM = 18;

/** Default fallback location (Wrocław, PL) */
export const FALLBACK_LOCATION: Coordinates = {
  latitude: 51.1079,
  longitude: 17.0385,
};
```

---

## 6. Migration Notes

### Current State

- GeolocationContext already provides `Coordinates` interface
- No existing map state management

### Changes Required

1. **New Types**: Add `MapState`, `MapError` types
2. **Constants**: Add map-specific constants

### Backward Compatibility

- No breaking changes (new feature, no existing map state)
- GeolocationContext remains unchanged
- Existing landing page components (HeroSection, RecentPetsSection) unaffected

---

## Summary

**Key Entities**:
1. `MapState` - Current map state (center, zoom, loading, error)
2. `Coordinates` - Geographic location (lat, lng)
3. `MapError` - Error states with user instructions

**Key Relationships**:
- GeolocationContext → Coordinates → MapState.center
- MapError → UI rendering (MapPermissionPrompt, MapErrorState)

