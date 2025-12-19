# Research: iOS Map Preview - Display Missing Pet Pins

**Feature Branch**: `KAN-30-ios-show-pins-on-the-map`  
**Created**: 2025-12-19  
**Status**: Complete

## Research Tasks

### 1. SwiftUI Map API for Annotations (iOS 17+)

**Decision**: Use `Map { ForEach { Marker(...) } }` content builder pattern

**Rationale**:
- iOS 18+ target allows using latest MapKit SwiftUI API
- `Map` with content closure and `Marker` provides declarative pin rendering
- `Marker` is native iOS map marker with standard appearance (red pin)
- Simpler than deprecated `Map(coordinateRegion:annotationItems:)` API
- Works seamlessly with `interactionModes: []` for static preview

**Alternatives considered**:
- `Map(coordinateRegion:annotationItems:)` - deprecated in iOS 17+
- `MapAnnotation` with custom view - overkill for simple pin requirement
- Custom UIKit `MKMapView` wrapper - unnecessary complexity

**Implementation**:
```swift
Map(initialPosition: .region(region), interactionModes: []) {
    ForEach(pins) { pin in
        Marker("", coordinate: pin.clLocationCoordinate)
            .tint(.red)
    }
}
```

### 2. Pin Data Source Architecture

**Decision**: Expose filtered announcements from `AnnouncementCardsListViewModel` via new computed property

**Rationale**:
- FR-015 requires pins from same payload as landing page list (no dedicated fetch)
- `AnnouncementCardsListViewModel` already fetches and stores announcements
- Adding computed property `pinEligibleAnnouncements` maintains encapsulation
- Parent `LandingPageViewModel` consumes this for map preview model

**Alternatives considered**:
- Separate fetch for pins - violates FR-015, wasteful
- Expose raw `cardViewModels` - wrong abstraction, doesn't filter by status
- Direct access to repository from `LandingPageViewModel` - duplicates fetch

**Implementation**:
- `AnnouncementCardsListViewModel.pinEligibleAnnouncements: [Announcement]`
- Filters by status: `.active` or `.found` (not `.closed`)
- Filters by valid coordinates

### 3. MapPreviewView Model Extension

**Decision**: Extend `.map` case with optional `pins: [PinModel]` parameter

**Rationale**:
- Minimal change to existing model
- Default empty array maintains backward compatibility
- `PinModel` lightweight struct with id + coordinate (in separate file per convention)
- Equatable implementation compares pin count for diffing

**Alternatives considered**:
- New `.mapWithPins` case - breaks exhaustive switch, unnecessary
- Pass announcements directly - too heavy, wrong abstraction level
- Global state/environment - violates MVVM-C pattern

**Implementation**:
```swift
// MapPreviewView_PinModel.swift
extension MapPreviewView {
    struct PinModel: Identifiable, Equatable {
        let id: String
        let coordinate: Coordinate
    }
}

// MapPreviewView_Model.swift
enum Model: Equatable {
    case loading
    case map(region: MKCoordinateRegion, pins: [PinModel] = [], onTap: () -> Void)
    case permissionRequired(message: String, onGoToSettings: () -> Void)
}
```

### 4. Range Parameter for Landing Page

**Decision**: Use `range=10` km for landing page announcements query

**Rationale**:
- FR-001 explicitly requires iOS to use `range=10` km for map preview
- Current implementation defaults to 100 km
- `AnnouncementListQuery.landingPageQuery(location:)` should specify range
- Repository already supports `range` parameter

**Alternatives considered**:
- Keep 100 km default - violates FR-001
- Hardcode in repository call - inflexible
- Configure at ViewModel level - misses architectural pattern

**Implementation**:
- Add `range: Int` to `AnnouncementListQuery`
- Update `landingPageQuery(location:)` to use `range: 10`
- Pass through to repository via `getAnnouncements(near:range:)`

### 5. Non-Interactive Pins Requirement

**Decision**: Use `interactionModes: []` + overlay tap gesture pattern (existing)

**Rationale**:
- FR-007, FR-008 require no-op on pin/map tap
- Current `MapPreviewView` already uses this pattern
- `Map` `interactionModes: []` disables all user gestures
- Overlay captures taps for future fullscreen navigation

**Alternatives considered**:
- Remove tap handler entirely - loses future extensibility
- Use `.disabled(true)` only - may still allow some interactions
- Custom gesture recognizer - overcomplicated

**Confirmation**: Existing implementation meets requirements.

### 6. Pin Status Filtering Logic

**Decision**: No client-side filtering needed - backend returns only eligible announcements

**Rationale**:
- Backend API returns only `active` (lost) and `found` announcements
- No `closed` status announcements in response
- FR-005: Coordinate validation already occurs in `AnnouncementMapper` - invalid coordinates result in nil mapping
- Simplifies iOS implementation - use all announcements from `cardViewModels`

**Implementation**:
```swift
// In LandingPageViewModel.updateMapPreviewModel()
let pins = listViewModel.cardViewModels.map { cardVM in
    MapPreviewView.PinModel(id: cardVM.id, coordinate: cardVM.announcement.coordinate)
}
```

Note: No filtering needed - all announcements from backend are pin-eligible.

## Resolved Clarifications

| Original Unknown | Resolution |
|-----------------|------------|
| SwiftUI Map API for pins | `Map { Marker }` content builder (iOS 17+) |
| Pin data architecture | Use `cardViewModels` directly from list ViewModel |
| Range parameter | Add to `AnnouncementListQuery`, use 10 km |
| Non-interactive handling | Existing pattern with `interactionModes: []` |
| Status filtering | Not needed - backend returns only active/found |

## Dependencies Verified

- **MapKit SwiftUI API**: Available iOS 17+, project targets iOS 18+
- **Backend API**: Already supports `lat`, `lng`, `range` query parameters
- **Existing components**: `MapPreviewView`, `LandingPageViewModel`, `AnnouncementCardsListViewModel` ready for extension

