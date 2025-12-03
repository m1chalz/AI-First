# Research: Web Browser Location for Pet Listings

**Date**: 2025-11-29  
**Feature**: 032-web-location-query

## Overview

This document captures research findings for implementing browser-based geolocation in the React TypeScript web application. All technical unknowns from the planning phase have been resolved.

---

## Research Tasks

### 1. Browser Geolocation API Best Practices

**Decision**: Use `navigator.geolocation.getCurrentPosition()` with timeout and error handling

**Rationale**:
- Widely supported across modern browsers (Chrome 5+, Firefox 3.5+, Safari 5+, Edge 12+)
- Provides straightforward permission handling via browser native prompt
- Returns `GeolocationPosition` with `coords.latitude` and `coords.longitude` (both as decimal degrees)
- Supports timeout configuration (spec requires 3-second timeout)
- Permission states accessible via `navigator.permissions.query({ name: 'geolocation' })`

**Implementation Pattern**:
```typescript
const options = {
  timeout: 3000,           // 3-second timeout per spec
  enableHighAccuracy: false, // Don't need GPS precision, network location sufficient
  maximumAge: 0            // Don't use cached location
};

navigator.geolocation.getCurrentPosition(
  (position) => {
    const lat = position.coords.latitude;
    const lng = position.coords.longitude;
    // Success callback
  },
  (error) => {
    // Error callback - handles PERMISSION_DENIED, TIMEOUT, POSITION_UNAVAILABLE
  },
  options
);
```

**Alternatives Considered**:
- `watchPosition()`: Rejected - spec requires single fetch on page load, not continuous tracking
- Third-party geolocation libraries: Rejected - native API sufficient and zero dependencies
- IP-based geolocation services: Rejected - less accurate and requires backend integration

**References**:
- [MDN: Geolocation API](https://developer.mozilla.org/en-US/docs/Web/API/Geolocation_API)
- [W3C Geolocation Spec](https://www.w3.org/TR/geolocation/)

---

### 2. Permission State Detection

**Decision**: Use `navigator.permissions.query()` for proactive permission state detection

**Rationale**:
- Allows detecting permission state BEFORE calling `getCurrentPosition()`
- Returns `PermissionStatus` with `state` property: `'granted'`, `'denied'`, or `'prompt'`
- Enables showing informational banner immediately when permission is blocked
- Supported in Chrome 43+, Firefox 46+, Safari 16+, Edge 79+

**Implementation Pattern**:
```typescript
const permissionStatus = await navigator.permissions.query({ name: 'geolocation' });

switch (permissionStatus.state) {
  case 'granted':
    // User previously allowed - fetch location automatically
    break;
  case 'denied':
    // User previously blocked - show informational banner
    break;
  case 'prompt':
    // Not yet requested - trigger browser prompt
    break;
}
```

**Alternatives Considered**:
- Only relying on `getCurrentPosition()` errors: Rejected - requires triggering the API first, doesn't allow proactive UI decisions
- Storing permission state in localStorage: Rejected - can become stale if user changes browser settings

**Fallback for Unsupported Browsers**:
- If `navigator.permissions` is undefined, assume `'prompt'` state and proceed with `getCurrentPosition()`
- Error handling will catch `PERMISSION_DENIED` and trigger banner display

---

### 3. URL Query Parameter Construction

**Decision**: Use `URLSearchParams` for constructing query strings

**Rationale**:
- Native browser API, no dependencies
- Handles URL encoding automatically
- Works seamlessly with existing base URL structure
- Clean, readable code

**Implementation Pattern**:
```typescript
async getAnimals(location?: { lat: number; lng: number }): Promise<Animal[]> {
  const url = new URL(`${this.apiBaseUrl}/api/v1/announcements`);
  
  if (location) {
    url.searchParams.append('lat', location.lat.toFixed(4));
    url.searchParams.append('lng', location.lng.toFixed(4));
  }
  
  const response = await fetch(url.toString());
  // ... rest of implementation
}
```

**Alternatives Considered**:
- Manual string concatenation: Rejected - error-prone, doesn't handle edge cases
- Query string libraries (qs, query-string): Rejected - unnecessary dependency for simple case

**Coordinate Precision**:
- Round to 4 decimal places (e.g., `52.2297`, `21.0122`) per spec requirement
- 4 decimal places = ~11 meter accuracy (sufficient for pet location matching)
- Use `Number.toFixed(4)` for formatting

---

### 4. React State Management for Location

**Decision**: Create custom `use-geolocation` hook following React hook patterns

**Rationale**:
- Encapsulates location fetching logic in reusable hook
- Manages permission state, loading state, error state, and coordinates in single interface
- Integrates cleanly with existing `use-animal-list` hook pattern
- Follows React best practices for async operations in hooks

**Hook Interface**:
```typescript
interface GeolocationState {
  coordinates: { lat: number; lng: number } | null;
  permissionState: 'granted' | 'denied' | 'prompt' | 'loading';
  error: GeolocationPositionError | null;
  isLoading: boolean;
}

function useGeolocation(): GeolocationState & { requestLocation: () => void };
```

**Alternatives Considered**:
- Context API for global location state: Rejected - location is page-specific, not app-wide
- Redux/Zustand state management: Rejected - overkill for single-page feature
- Direct state in component: Rejected - violates separation of concerns, harder to test

**Integration Strategy**:
- `use-geolocation` hook triggers location fetch on mount (if permission already granted or not yet requested)
- `use-animal-list` hook consumes coordinates from `use-geolocation` and passes to `AnimalRepository.getAnimals()`
- Loading states merged: show spinner until BOTH location AND pets are fetched

---

### 5. Error Handling Strategy

**Decision**: Graceful degradation - always show pets, with or without location

**Rationale**:
- Non-blocking UX per spec requirement (SC-001: 100% of users can browse pets regardless of permission)
- Location failures (timeout, denied, unavailable) fall back to unfiltered pet listings
- Only show error UI for API failures (pet listings fetch), not location failures
- Location errors trigger informational banner for blocked permissions, but don't block content

**Error Categories & Handling**:

| Error Type | Handling Strategy | User Experience |
|------------|------------------|-----------------|
| `PERMISSION_DENIED` | Show informational banner with instructions, load all pets | Banner above listings with X button |
| `TIMEOUT` (3s exceeded) | Silently fall back to no location params, load all pets | No error message - pets load normally |
| `POSITION_UNAVAILABLE` | Silently fall back to no location params, load all pets | No error message - pets load normally |
| Pet API failure (500, timeout) | Show error message with retry button | Full-page error with "Try again" button |
| Pet API returns empty array | Show "No pets nearby" message | Empty state below banner (if shown) |

**Alternatives Considered**:
- Blocking UI until location is resolved: Rejected - violates spec requirement for non-blocking UX
- Showing error messages for all location failures: Rejected - creates alarm fatigue, spec only requires banner for blocked state
- Retrying location fetch automatically: Rejected - spec doesn't require it, would delay initial load

---

### 6. HTTPS Requirement & HTTP Fallback

**Decision**: Document HTTPS requirement, implement graceful fallback for HTTP

**Rationale**:
- Browser Geolocation API requires HTTPS in production (security policy)
- On HTTP, `navigator.geolocation` may be `undefined` or `getCurrentPosition()` will fail with `PERMISSION_DENIED`
- Spec explicitly states: "no validation - if HTTP is used, location feature will not work and app will use fallback mode"

**Implementation**:
- Check for `navigator.geolocation` existence before attempting to use it
- If undefined, skip location fetch and proceed with unfiltered listings
- No error message shown - silent fallback as spec dictates

**Development Environment**:
- `localhost` is exempt from HTTPS requirement (browsers allow geolocation on localhost for dev)
- No special configuration needed for local development

**Deployment Consideration**:
- Document in README/deployment guide that HTTPS is required for location feature
- Feature degrades gracefully if deployed on HTTP (no breaking errors)

---

### 7. Loading UX Pattern

**Decision**: Full-page spinner/overlay blocking interaction until data loads

**Rationale**:
- Spec explicitly requires "full-page spinner/overlay blocking interaction" (FR-012)
- Prevents user from interacting with stale/empty state during fetch
- Simple implementation using overlay with fixed positioning

**Implementation Pattern**:
```tsx
{isLoading && (
  <div 
    data-testid="petList.loading.spinner"
    style={{ 
      position: 'fixed', 
      inset: 0, 
      background: 'rgba(255,255,255,0.8)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 1000
    }}
  >
    <Spinner />
  </div>
)}
```

**Loading State Logic**:
- `isLoading = true` when EITHER location OR pets are being fetched
- `isLoading = false` only when BOTH location (if requested) AND pets are loaded
- Timeout on location fetch (3s) ensures loading doesn't block indefinitely

**Alternatives Considered**:
- Skeleton screens: Rejected - spec requires blocking overlay
- Non-blocking loading indicators: Rejected - spec explicitly requires blocking interaction

---

## Technology Decisions Summary

| Component | Technology Choice | Rationale |
|-----------|------------------|-----------|
| Location API | Native `navigator.geolocation` | Zero dependencies, widely supported, sufficient for requirements |
| Permission Detection | `navigator.permissions.query()` | Proactive state detection, enables better UX |
| URL Construction | `URLSearchParams` | Native API, automatic encoding, clean code |
| State Management | Custom React hook (`use-geolocation`) | Follows existing patterns, testable, reusable |
| Error Handling | Graceful degradation with fallback | Non-blocking UX, spec requirement |
| Coordinate Precision | 4 decimal places (`toFixed(4)`) | 11m accuracy, spec requirement |
| Loading UX | Full-page overlay with spinner | Spec requirement, simple implementation |

---

## Open Questions Resolved

1. ✅ **Q**: Should we store location in localStorage for subsequent visits?  
   **A**: No - spec explicitly states "The app does not store or track location history in browser storage or cookies; location is used only for the current query."

2. ✅ **Q**: Should we provide browser-specific instructions in the banner?  
   **A**: No - spec states "Show generic instructions that work for all browsers, keep the message short."

3. ✅ **Q**: Should the banner persist dismissal across page loads?  
   **A**: No - spec states "Informational banner MUST be displayed every time the page loads when permission is blocked (no persistence of dismissal across page loads)" (FR-004a)

4. ✅ **Q**: What if user changes permission while page is open?  
   **A**: Out of scope - spec states "App does not detect or respond to location permission changes made while the app is open... User must refresh the page to apply permission changes."

5. ✅ **Q**: Should we cancel location fetch if user navigates away?  
   **A**: Yes - spec requires "System MUST cancel location fetch request if user navigates away from page before location is obtained" (FR-013)

---

## Dependencies

**No new dependencies required**. All functionality uses native browser APIs:
- `navigator.geolocation` (Geolocation API)
- `navigator.permissions` (Permissions API)
- `URLSearchParams` (URL API)
- React 18.x hooks (already in project)

---

## Next Steps

Phase 1 outputs:
1. `data-model.md` - Define TypeScript interfaces for location state and API responses
2. `contracts/` - Document extended `getAnimals()` method signature
3. `quickstart.md` - Developer guide for testing location feature locally
