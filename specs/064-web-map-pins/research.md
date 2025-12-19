# Research: Web Map Pins

**Feature**: 064-web-map-pins  
**Date**: 2025-12-19  
**Phase**: Phase 0 - Technical Research

## Overview

This document captures research findings and technical decisions for implementing interactive map pins on the PetSpot landing page. The feature extends the existing Leaflet map (from 063-web-map-view) with custom markers, pop-ups, and independent loading/error states.

## Research Topics

### 1. Custom Leaflet Marker Icons

**Decision**: Use Leaflet's `L.divIcon` with custom HTML/SVG for teardrop markers

**Rationale**:
- Allows full control over marker appearance (color, shape, symbols)
- Supports CSS styling for responsive design
- Can render SVG paths for crisp teardrop shape at any zoom level
- Easy to add status icons (! for missing, ✓ for found)
- Better accessibility (can add ARIA labels)

**Alternatives Considered**:
- **PNG image icons** - Rejected: Fixed resolution, harder to style dynamically, accessibility limitations
- **Leaflet.awesome-markers plugin** - Rejected: Adds dependency, limited to predefined shapes
- **Canvas-based markers** - Rejected: More complex, poor accessibility, harder to debug

**Implementation Details**:
```typescript
const createPinIcon = (status: 'missing' | 'found') => {
  const color = status === 'missing' ? '#EF4444' : '#155DFC';
  const symbol = status === 'missing' ? '!' : '✓';
  
  return L.divIcon({
    html: `
      <div class="custom-marker" style="color: ${color}">
        <svg><!-- teardrop path --></svg>
        <span>${symbol}</span>
      </div>
    `,
    className: 'pet-pin-marker',
    iconSize: [32, 42],
    iconAnchor: [16, 42],
    popupAnchor: [0, -42]
  });
};
```

**References**:
- [Leaflet DivIcon Documentation](https://leafletjs.com/reference.html#divicon)
- [Leaflet Custom Icons Tutorial](https://leafletjs.com/examples/custom-icons/)

---

### 2. React-Leaflet Marker Management

**Decision**: Use React-Leaflet's `<Marker>` component with React state for marker data

**Rationale**:
- Declarative pattern fits React paradigm
- React handles marker lifecycle (mount/unmount) automatically
- Easy to integrate with React state management (hooks)
- Built-in support for event handlers (click, hover)
- No manual Leaflet layer management needed

**Alternatives Considered**:
- **Imperative Leaflet API** - Rejected: Mixing imperative code in React components, manual cleanup required
- **Leaflet MarkerCluster plugin** - Rejected: User explicitly declined clustering in clarification session
- **Custom Canvas layer** - Rejected: Over-engineered for <100 pins, accessibility issues

**Implementation Pattern**:
```typescript
const MapPinLayer = () => {
  const { pins, loading, error } = useMapPins();
  
  return (
    <>
      {pins.map(pin => (
        <Marker
          key={pin.id}
          position={[pin.latitude, pin.longitude]}
          icon={createPinIcon(pin.status)}
          eventHandlers={{ click: () => handlePinClick(pin) }}
        />
      ))}
    </>
  );
};
```

**References**:
- [React-Leaflet Marker Documentation](https://react-leaflet.js.org/docs/api-components/#marker)
- [React-Leaflet Best Practices](https://react-leaflet.js.org/docs/start-introduction/)

---

### 3. Pop-up Handling

**Decision**: Use Leaflet's built-in `<Popup>` component

**Rationale**:
- Leaflet handles pop-up state (open/close/click-outside) automatically
- Built-in accessibility (ESC key, close button)
- No need for separate React state management

**Implementation**:
```typescript
<Marker position={[pin.latitude, pin.longitude]} icon={createPinIcon(pin.status)}>
  <Popup>
    <div>
      <img src={pin.photoUrl} alt={pin.name} />
      <h3>{pin.name}</h3>
      <p>{pin.species}</p>
      {/* ... other pet details */}
    </div>
  </Popup>
</Marker>
```

---

### 4. Asynchronous Pin Loading Strategy

**Decision**: Custom `use-map-pins` hook with React state + abort controller

**Rationale**:
- Separates data fetching logic from UI components (testable)
- Handles loading/error states independently from map loading
- Uses AbortController to cancel stale requests
- Integrates with existing `AnnouncementService`

**Alternatives Considered**:
- **React Query** - Rejected: Adds dependency, over-engineered for simple use case
- **Redux/Zustand** - Rejected: Global state not needed
- **SWR** - Rejected: Caching not critical

**Implementation Pattern**:
```typescript
// File: use-map-pins.ts
const useMapPins = (userLocation: { lat: number; lng: number } | null) => {
  const [pins, setPins] = useState<PetPin[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);
  
  useEffect(() => {
    if (!userLocation) return;
    
    const controller = new AbortController();
    
    (async () => {
      try {
        setLoading(true);
        const response = await announcementService.getAnnouncements({
          lat: userLocation.lat,
          lng: userLocation.lng,
          range: 10
        });
        
        // Inline transformation
        setPins(response.data.map(a => ({
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
        })));
        setError(null);
      } catch (err) {
        if (!controller.signal.aborted) {
          setError(err);
        }
      } finally {
        setLoading(false);
      }
    })();
    
    return () => controller.abort();
  }, [userLocation?.lat, userLocation?.lng]);
  
  return { pins, loading, error };
};
```

**Key Features**:
- Fetches pins for user's current location with fixed 10km radius
- Independent loading/error states
- AbortController for cleanup
- Inline transformation (no separate mapper)

**References**:
- [React useEffect cleanup](https://react.dev/reference/react/useEffect#cleanup-function)
- [AbortController for fetch](https://developer.mozilla.org/en-US/docs/Web/API/AbortController)

---

### 5. Error Handling Patterns

**Decision**: Inline error state within map viewport (no full-page error)

**Rationale**:
- Pin loading errors should not block map interaction
- Users can still use map even if pins fail to load
- Consistent with spec requirement: "independent from map loading/error states"
- Provides clear retry action within map context

**Error UI Design**:
- Small error banner overlaid on map (top-right corner)
- Message: "Unable to load pet locations. [Retry]"
- Does not obscure map controls
- Automatically dismisses on successful retry

**Alternatives Considered**:
- **Toast/Snackbar** - Rejected: Disappears too quickly, user might miss retry option
- **Full map overlay** - Rejected: Blocks map interaction unnecessarily
- **Silent failure** - Rejected: User has no feedback or recovery option

**Implementation**:
```typescript
{error && (
  <div className="map-error-overlay" data-testid="landingPage.map.pinsError">
    <p>Unable to load pet locations.</p>
    <button onClick={retry} data-testid="landingPage.map.pinsRetry">
      Retry
    </button>
  </div>
)}
```

---

### 6. Performance Considerations

**Decision**: No optimization needed for initial implementation

**Rationale**:
- Expected pin count: 10-100 per viewport
- Leaflet handles rendering efficiently for <1000 markers
- No clustering needed (per user clarification)
- Constitution Principle XIV: "Performance is not a concern for this project"

