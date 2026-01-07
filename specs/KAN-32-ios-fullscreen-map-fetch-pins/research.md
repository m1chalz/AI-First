# Research: iOS Fullscreen Map - Fetch and Display Pins

**Branch**: `KAN-32-ios-fullscreen-map-fetch-pins` | **Date**: 2025-01-07

## Research Tasks

### 1. Backend API for Location-Based Announcements

**Question**: Does the backend API support fetching announcements by location and radius?

**Decision**: Use existing `GET /api/v1/announcements?lat={lat}&lng={lng}&range={range}` endpoint

**Rationale**: 
- Endpoint already implemented in `/server/src/routes/announcements.ts` (line 16-23)
- Accepts `lat`, `lng`, `range` query parameters
- Returns `{ data: announcements[] }` response format
- iOS repository already consumes this endpoint via `AnnouncementRepository.getAnnouncements(near:range:)`

**Alternatives Considered**:
- Creating new dedicated `/pins` endpoint → Rejected: unnecessary duplication
- Adding status filter parameter → Rejected: not needed, display all announcements

### 2. iOS Repository Layer for Announcements

**Question**: Does iOS have repository infrastructure for fetching announcements?

**Decision**: Use existing `AnnouncementRepositoryProtocol.getAnnouncements(near:range:)`

**Rationale**:
- Protocol defined in `/iosApp/iosApp/Domain/Repositories/AnnouncementRepositoryProtocol.swift`
- Implementation in `/iosApp/iosApp/Data/Repositories/AnnouncementRepository.swift`
- Already handles URL construction with lat/lng/range query params
- Returns `[Announcement]` with `coordinate` and `status` fields
- Includes Task cancellation support for rapid gesture scenarios

**Alternatives Considered**:
- Creating new `MapPinRepository` → Rejected: violates DRY, same data source
- Adding new protocol method → Rejected: existing method sufficient

### 3. Status Filtering for "Missing" Announcements

**Question**: Should the client filter announcements by status?

**Decision**: No client-side filtering - display all announcements returned by server

**Rationale**:
- Server already filters by location (lat/lng/range)
- All returned announcements are relevant for the visible map region
- Simplifies client logic - no status filtering needed
- Future filtering can be done server-side via query parameters if needed

**Alternatives Considered**:
- Client-side filter `announcements.filter { $0.status == .active }` → Rejected: display all announcements, let server handle filtering if needed
- Backend filter parameter `?status=MISSING` → Rejected: not needed for current requirements

### 4. iOS 18 MapKit Camera API for Gesture Detection

**Question**: How to detect when user finishes pan/zoom gesture on map?

**Decision**: Use SwiftUI `Map.onMapCameraChange(frequency:_:)` modifier with `.onEnd` frequency

**Rationale**:
- iOS 18+ requirement enables latest MapKit APIs
- `onMapCameraChange(frequency: .onEnd) { context in }` fires after gesture completion
- `MapCameraUpdateContext` provides `region` with center and span
- Calculate radius from region span for API request
- Native API, no third-party dependencies

**Alternatives Considered**:
- Combine-based `@Published` region binding → Rejected: Combine prohibited by constitution
- UIKit `MKMapViewDelegate.regionDidChangeAnimated` → Rejected: unnecessary UIKit complexity
- Timer-based debouncing → Rejected: spec requires immediate fetch, no debounce

### 5. Pin Display with Annotations (No Pin Update Animations)

**Question**: How to display pins on SwiftUI Map (and keep a path for future rich callouts)?

**Decision**: Use `Annotation` with custom SwiftUI view for rich callout support

**Rationale**:
- **Rich callout requirement**: Future spec requires callout bubble with photo, pet details, contact info, and status badge (see Figma design)
- **Callout with tail/arrow**: Design shows speech-bubble style callout with triangle pointing at pin
- `Annotation` allows fully custom SwiftUI content including callout bubble
- Tap on pin toggles callout visibility with animation
- Pin updates remain instant (no animation) per spec; only callout selection may animate

**Figma Design Reference**: 
- Node: `1192:5893` in PetSpot-wireframes
- Callout contents: Photo (216×120), Name (bold), Species•Breed, 📍Location, 📅Date, 📧Email, 📞Phone, Description, Status badge
- Styling: White bg, rounded corners (12px), shadow, tail/arrow pointing to pin

**Implementation Pattern**:
```swift
Annotation("", coordinate: pin.coordinate, anchor: .bottom) {
    VStack(spacing: 0) {
        // Callout bubble (shown when selected)
        if selectedPin == pin {
            PinCalloutView(pin: pin)
                .transition(.scale.combined(with: .opacity))
        }
        
        // Pin marker (always visible) - classic map pin
        Image(systemName: "mappin.circle.fill")
            .font(.title)
            .foregroundStyle(.red)
    }
    .onTapGesture {
        withAnimation(.easeInOut(duration: 0.2)) {
            selectedPin = selectedPin == pin ? nil : pin
        }
    }
}
```

**Alternatives Considered**:
- `Marker` with sheet → Rejected: design requires inline callout bubble, not modal sheet
- `Marker` with selection + overlay → Rejected: complex positioning, callout won't follow pin during pan
- UIKit `MKAnnotationView` → Rejected: adds UIKit complexity

### 6. Radius Calculation from Map Region

**Question**: How to calculate radius in meters from visible map region?

**Decision**: Calculate diagonal distance using `CLLocation.distance(from:)` as `MKCoordinateRegion` extension

**Rationale**:
- Diagonal covers entire visible area (corner to corner)
- `CLLocation.distance(from:)` uses geodesic calculation (accurate)
- Extension on `MKCoordinateRegion` provides clean, reusable API
- Returns radius (half of diagonal) in kilometers for API

**Implementation** (`Coordinate.swift` or new `MKCoordinateRegion+Radius.swift`):
```swift
extension MKCoordinateRegion {
    /// Calculates radius in kilometers covering the visible region.
    /// Uses diagonal distance (corner to corner) for complete coverage.
    var radiusInKilometers: Int {
        let topLeft = CLLocation(
            latitude: center.latitude + span.latitudeDelta / 2,
            longitude: center.longitude - span.longitudeDelta / 2
        )
        let bottomRight = CLLocation(
            latitude: center.latitude - span.latitudeDelta / 2,
            longitude: center.longitude + span.longitudeDelta / 2
        )
        
        let diagonalMeters = topLeft.distance(from: bottomRight)
        let radiusMeters = diagonalMeters / 2
        
        return Int(radiusMeters / 1000)
    }
}
```

**Usage in ViewModel**:
```swift
let radiusKm = region.radiusInKilometers
let announcements = try await repository.getAnnouncements(near: center, range: radiusKm)
```

**Alternatives Considered**:
- Latitude delta only → Rejected: ignores longitude, misses corners
- Fixed radius regardless of zoom → Rejected: misses pins at high zoom
- Mathematical formula → Rejected: less accurate than geodesic

### 7. Internal Loading State (No UI Indicator)

**Question**: How to track loading state without visible UI indicator?

**Decision**: Use `@Published private(set) var isLoading = false` (internal state only)

**Rationale**:
- Spec FR-006 requires internal loading state without visible indicator
- Private setter prevents external modification
- Enables unit testing of loading state transitions
- Future extensibility if loading indicator needed

**Alternatives Considered**:
- No loading state tracking → Rejected: loses testability
- Loading enum with multiple states → Rejected: over-engineering for binary state

### 8. Silent Error Handling

**Question**: How to handle pin fetch failures silently?

**Decision**: Catch errors in ViewModel, log to console, keep existing pins displayed

**Rationale**:
- Spec FR-011 requires silent failure without error messages
- `do { try await } catch { print("Error: \(error)") }` pattern
- Existing pins remain in state on failure
- Next gesture triggers new fetch attempt automatically

**Alternatives Considered**:
- Show toast/alert → Rejected: violates spec (silent failure)
- Retry with exponential backoff → Rejected: over-engineering, next gesture retries
- Clear pins on error → Rejected: poor UX, spec says keep existing

### 9. Concurrent Request Handling

**Question**: How to handle rapid gestures that could trigger multiple concurrent requests?

**Decision**: ViewModel cancels previous fetch task when starting a new fetch

**Rationale**:
- Spec notes: "Repository layer concern"
- Matches existing codebase conventions (ViewModels cancel in-flight tasks on refresh)
- Prevents outdated responses from racing and overwriting pins for the latest region

**Alternatives Considered**:
- Serial queue → Rejected: adds complexity, cancellation is simpler
- Debouncing → Rejected: spec explicitly says no debounce
- Allow parallel requests → Rejected: wasteful, may cause UI flicker

## Summary

All technical decisions align with existing iOS architecture (MVVM-C) and leverage existing infrastructure:
- **Backend**: Existing API endpoint ready to use
- **Repository**: Existing protocol and implementation sufficient
- **MapKit**: iOS 18 camera APIs provide clean gesture detection
- **SwiftUI**: Map `Annotation` supports custom pin content and future rich callouts
- **Error handling**: Simple silent failure pattern

No new dependencies required. Implementation focuses on extending existing `FullscreenMapViewModel` and `FullscreenMapView`.

