# Research: Web Map Component on Landing Page

**Feature**: 063-web-map-view  
**Date**: 2025-12-18  
**Phase**: 0 (Outline & Research)

## Overview

This document consolidates research findings for implementing an interactive map component on the web landing page using Leaflet.js + OpenStreetMap. The research addresses library selection, React integration patterns, geolocation handling, error states, and testing strategies.

---

## 1. Map Library Selection: Leaflet.js + OpenStreetMap

### Decision

Use **Leaflet.js 1.9+** with **react-leaflet 4.x** for map rendering and **OpenStreetMap** for tile layer.

### Rationale

1. **Open Source & No API Costs**: Leaflet is MIT-licensed, OpenStreetMap is free (ODbL license), no API keys required
2. **Lightweight**: ~40KB gzipped (vs Google Maps ~200KB)
3. **React Integration**: `react-leaflet` provides official React bindings with hooks-based API
4. **Flexibility**: Full control over styling, markers, overlays without vendor lock-in
5. **Offline Support**: Tiles cached by browser automatically
6. **Community**: Large ecosystem, well-documented, actively maintained

### Alternatives Considered

- **Google Maps**: Requires API key, billing setup, vendor lock-in, larger bundle size
- **Mapbox**: Requires API key, free tier limits (50k loads/month), vendor lock-in
- **OpenLayers**: More complex API, steeper learning curve, heavier bundle size

### Implementation Notes

- Install: `npm install leaflet react-leaflet`
- Install types: `npm install -D @types/leaflet`
- Import Leaflet CSS: `import 'leaflet/dist/leaflet.css'` in component
- Attribution requirement: Must display "© OpenStreetMap contributors" (built-in to Leaflet)

---

## 2. React Integration Pattern: react-leaflet Hooks

### Decision

Use **react-leaflet 4.x** hooks-based API with `MapContainer`, `TileLayer`, and `useMap` hook.

### Rationale

1. **Declarative**: React-style declarative component composition
2. **Hooks-Based**: Modern React patterns (useMap, useMapEvents)
3. **Lifecycle Management**: Automatic cleanup, ref management
4. **TypeScript Support**: Full type definitions included

### Best Practices

```typescript
// Basic structure
import { MapContainer, TileLayer, useMap } from 'react-leaflet';

function MapView() {
  return (
    <MapContainer
      center={[lat, lng]}
      zoom={13}
      style={{ height: '400px', width: '100%' }}
    >
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      {/* Custom components with useMap hook */}
    </MapContainer>
  );
}
```

### Key Patterns

1. **MapContainer is immutable**: Cannot change `center` or `zoom` props after mount (use `useMap` hook to imperatively update)
2. **Custom controls**: Use `useMap` hook to access Leaflet instance
3. **Event handling**: Use `useMapEvents` hook for user interactions

---

## 3. Geolocation Handling: GeolocationContext Integration

### Decision

Reuse existing **GeolocationContext** from `/webApp/src/contexts/GeolocationContext.tsx` for location permission and coordinate retrieval.

### Rationale

1. **Already Implemented**: Context provides `coordinates`, `error`, `isLoading`, `permissionCheckCompleted`
2. **Permission Management**: Handles browser permission API, timeouts
3. **State Centralization**: Single source of truth for location state across app
4. **Tested**: Context already has unit tests

### Integration Pattern

```typescript
import { useGeolocation } from '../contexts/GeolocationContext';

function MapView() {
  const { coordinates, error, isLoading, requestPermission } = useGeolocation();

  if (!coordinates) {
    return <MapPermissionPrompt onRequestPermission={requestPermission} />;
  }

  return <MapContainer center={[coordinates.latitude, coordinates.longitude]} zoom={13} />;
}
```

### Fallback Location Strategy

1. **First priority**: User's current location (GeolocationContext)
2. **Second priority**: Default fallback (Wrocław, PL: 51.1079, 17.0385)

---

## 4. Zoom Level Calculation for 10 km Radius

### Decision

Use **zoom level 13** for approximately 10 km radius viewport.

### Rationale

- At zoom level 13, the map displays ~10-12 km radius at typical latitudes (45-55°N)
- Wrocław is at 51°N, which fits this range
- Standard Leaflet zoom levels: 0 (world) → 18 (building-level)

### Zoom Range

- **Min zoom**: 10 (prevent zooming out too far, ~40 km radius)
- **Max zoom**: 18 (street-level detail)
- **Initial zoom**: 13 (~10 km radius)

---

## 5. Error Handling Patterns

### Decision

Implement **three error states** with dedicated components:

1. **Permission Denied/Not Requested**: `MapPermissionPrompt` (informational message + consent button)
2. **Location Unavailable**: Show map in fallback mode (Wrocław) + message "Unable to get your location. Please refresh the page to try again."
3. **Map Load Failure**: `MapErrorState` (error message "Failed to load map. Please refresh the page to try again.")

### Error Recovery

All errors require page refresh to recover. User is instructed to refresh the browser page.

---

## 6. State Management: Custom Hook (use-map-state)

### Decision

Extract map state management to a **custom React hook** (`use-map-state.ts`) following clean architecture principles.

### Rationale

1. **Testability**: Pure logic separated from UI rendering (80% coverage target)
2. **Reusability**: Hook can be reused in other map components (future features)
3. **Single Responsibility**: Component handles rendering, hook handles logic

### Hook Responsibilities

- Integrate with GeolocationContext
- Determine map center (current location → fallback)
- Manage error states (permission, location unavailable, map load failure)
- Provide permission request handler

### Hook Interface

```typescript
interface UseMapStateReturn {
  center: [number, number]; // [lat, lng]
  zoom: number;
  isLoading: boolean;
  error: MapError | null;
  showPermissionPrompt: boolean;
  handleRequestPermission: () => void;
}

function useMapState(): UseMapStateReturn {
  // Implementation
}
```

---

## 7. Testing Strategy

### Decision

Implement **three testing layers**:

1. **Unit Tests (Vitest)**: Hooks and utility functions
2. **Component Tests (Vitest + React Testing Library)**: Component rendering and user interactions
3. **E2E Tests (Playwright)**: Full user flows (permission gating, map interactions)

### Coverage Targets

- **Hooks**: 80% line + branch coverage (`use-map-state.test.ts`)
- **Components**: Recommended coverage for critical interactions

### Test Scenarios (Unit)

- `use-map-state.test.ts`:
  - ✅ Returns current location as center when permission granted
  - ✅ Returns fallback location when permission denied
  - ✅ Shows error message when location unavailable
  - ✅ Shows error message when map load fails

### Test Scenarios (Component)

- `MapView.test.tsx`:
  - ✅ Renders MapContainer with correct center and zoom
  - ✅ Displays permission prompt when permission not granted
  - ✅ Triggers permission request on consent button click
  - ✅ Displays error state on map load failure

### Test Scenarios (E2E)

- `map-view.spec.ts`:
  - ✅ User Story 1: Map displays between Description and Recently Lost Pets
  - ✅ User Story 1: Map centers on user location when permission granted
  - ✅ User Story 1: Map supports zoom and pan interactions
  - ✅ User Story 2: Permission prompt displays when permission denied
  - ✅ User Story 2: Browser permission dialog triggered on consent button click

---

## 8. Dependencies Audit

### New Dependencies

| Package | Version | Size (gzipped) | Rationale | Security Audit |
|---------|---------|----------------|-----------|----------------|
| `leaflet` | 1.9.4 | ~40 KB | Map rendering engine (MIT license) | ✅ No known vulnerabilities |
| `react-leaflet` | 4.2.1 | ~10 KB | React bindings for Leaflet | ✅ No known vulnerabilities |
| `@types/leaflet` | 1.9.8 | Dev only | TypeScript definitions | N/A (dev dependency) |

### Total Bundle Impact

- **Leaflet + react-leaflet**: ~50 KB gzipped (~150 KB uncompressed)
- **Impact**: Acceptable for map rendering (Google Maps is ~200 KB)

### Audit Command

```bash
npm audit
npm outdated
```

---

## Summary of Technical Decisions

| Area | Decision | Rationale |
|------|----------|-----------|
| **Map Library** | Leaflet.js 1.9+ | Open source, lightweight, no API costs, full control |
| **React Integration** | react-leaflet 4.x (hooks-based) | Modern React patterns, declarative API, TypeScript support |
| **Geolocation** | Reuse GeolocationContext | Already implemented, tested, single source of truth |
| **Zoom Level** | 13 (10 km radius) | Standard Leaflet zoom for neighborhood-level view |
| **Error Handling** | 3 error states | Permission prompt, location unavailable (fallback + message), map load failure (message) |
| **State Management** | Custom hook (use-map-state) | Testable, reusable, clean architecture |
| **Testing** | Unit + Component + E2E | 80% coverage target, Vitest + Playwright |

---

## Next Steps (Phase 1)

1. Generate data model (`data-model.md`)
2. Define TypeScript interfaces (`contracts/map-component.interface.ts`)
3. Create quickstart guide (`quickstart.md`)
4. Update agent context with new technologies

