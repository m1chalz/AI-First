# Research: iOS Fullscreen Interactive Map with Legend

**Feature Branch**: `KAN-32-ios-display-fullscreen-map`  
**Date**: 2025-01-07

## Research Tasks

### 1. SwiftUI Map Interaction Modes

**Question**: How to enable interactive gestures (zoom, pan, double-tap) on SwiftUI Map?

**Decision**: Use `Map(initialPosition:)` without `interactionModes` parameter (defaults to all interactions enabled) or explicitly specify `.all`.

**Rationale**: 
- Existing `MapPreviewView.swift` uses `interactionModes: []` to DISABLE interactions
- For fullscreen map, we need the opposite - all interactions enabled
- SwiftUI Map in iOS 17+ uses `Map(initialPosition: .region(region))` initializer
- Omitting `interactionModes` parameter enables all gestures by default (zoom, pan, rotate, pitch)

**Alternatives considered**:
- `interactionModes: [.zoom, .pan]` - explicit subset, but `.all` is simpler and matches spec requirements
- UIViewRepresentable with MKMapView - unnecessary complexity when SwiftUI Map supports all needed features

**Code pattern** (from existing codebase):
```swift
// MapPreviewView.swift - interactions DISABLED
Map(initialPosition: .region(region), interactionModes: [])

// FullscreenMapView.swift - interactions ENABLED (new)
Map(initialPosition: .region(region))  // all interactions by default
```

### 2. Location Access for Initial Centering

**Question**: How to get user's current location to center the map?

**Decision**: Pass `Coordinate` directly to `FullscreenMapViewModel` constructor. Location is already available from landing page.

**Rationale**:
- Fullscreen map is only accessible from landing page where location is already fetched
- No need to re-fetch location - pass it through coordinator
- Simpler ViewModel with no async loading, no dependencies
- Instant map display - no loading state needed
- City-level zoom (~10km radius) achieved via `Coordinate.mapRegion()` helper

**Alternatives considered**:
- Inject `LocationPermissionHandler` - unnecessary complexity, location already available
- Fetch location again in ViewModel - duplicate work, slower UX

**Implementation pattern**:
```swift
// HomeCoordinator passes location to ViewModel
let viewModel = FullscreenMapViewModel(userLocation: location)

// ViewModel is simple - just holds the region
class FullscreenMapViewModel: ObservableObject {
    let mapRegion: MKCoordinateRegion
    
    init(userLocation: Coordinate) {
        self.mapRegion = userLocation.mapRegion()
    }
}
```

### 3. Legend Overlay Component

**Question**: How to implement map legend as static overlay?

**Decision**: Reuse existing `MapSectionHeaderView` with optional title (make `title: String?`).

**Rationale**:
- `MapSectionHeaderView` already exists on landing page with legend functionality
- Making title optional allows reuse for fullscreen map (title in navigation bar)
- Follows DRY principle - no duplicate legend component
- Factory pattern with `.fullscreenMap()` keeps configuration clean
- Localization strings already exist: `mapSection.legend.missing`, `mapSection.legend.found`

**Alternatives considered**:
- Create new `MapLegendView` component - duplication, same layout as existing
- Extract shared `LegendView` - extra refactoring, existing component works fine

**Design approach**:
```swift
// Legend header above map (VStack layout)
VStack(spacing: 0) {
    MapSectionHeaderView(model: .fullscreenMap())  // title = nil
    Map(...)
}
```

**Changes to existing component**:
- `MapSectionHeaderView.Model.title`: `String` → `String?`
- `MapSectionHeaderView.Model.titleAccessibilityId`: `String` → `String?`
- View: conditionally render title when not nil

### 4. Double-Tap Zoom Gesture

**Question**: Does SwiftUI Map support double-tap to zoom natively?

**Decision**: Yes, SwiftUI Map supports double-tap zoom by default when interactions are enabled.

**Rationale**:
- Double-tap to zoom is standard MapKit behavior
- When `interactionModes` includes zoom capability (default), double-tap works automatically
- No custom gesture implementation needed

**Alternatives considered**:
- Custom `onTapGesture(count: 2)` - would conflict with Map's native gesture handling
- UIViewRepresentable with MKMapView - unnecessary complexity

### 5. Localization Strings

**Question**: What localization strings are needed for this feature?

**Decision**: Reuse existing strings; no new strings needed.

**Rationale**: All required strings already exist in `Localizable.strings`:
- `fullscreenMap.navigationTitle` = "Pet Locations" / "Lokalizacje zwierząt"
- `mapSection.legend.missing` = "Missing" / "Zaginione"
- `mapSection.legend.found` = "Found" / "Znalezione"

### 6. Test Strategy for ViewModel

**Question**: What should be tested in `FullscreenMapViewModelTests`?

**Decision**: Test constructor initialization - region calculation from coordinate.

**Test cases**:
1. `testInit_shouldSetMapRegionFromUserLocation` - verify center coordinates
2. `testInit_shouldUseCityLevelZoom` - verify ~10km radius span
3. `testLegendModel_shouldHaveMissingAndFoundItems` - verify legend configuration

**Rationale**:
- ViewModel is simple data holder - tests are straightforward
- No async logic, no mocking needed
- Just verify input → output transformation

## Summary

| Area | Decision | Key File |
|------|----------|----------|
| Map interactions | SwiftUI Map with default `interactionModes` | `FullscreenMapView.swift` |
| Location | Inject `LocationPermissionHandler` | `FullscreenMapViewModel.swift` |
| Legend | Reuse `MapSectionHeaderView` with optional title | `MapSectionHeaderView.swift`, `_Model+FullscreenMap.swift` |
| Double-tap | Native MapKit support | N/A |
| Localization | Existing strings | `Localizable.strings` |
| Testing | ViewModel state tests | `FullscreenMapViewModelTests.swift` |

