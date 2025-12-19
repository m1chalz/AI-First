# Research: iOS Map Preview Component

**Feature**: KAN-30 - Embed Map in Landing Page
**Date**: 2025-12-19

## Research Tasks

### 1. Map Rendering Approach

**Decision**: Use SwiftUI Map with disabled interactions

**Rationale**:
- Native SwiftUI component - zero external dependencies
- Disabling interactions (`interactionModes: []`) makes it behave like static preview
- Built-in tile caching and loading states
- No async snapshot generation complexity
- No task cancellation needed
- Region-based - just pass coordinates and it renders

**Alternatives Considered**:
| Alternative | Why Rejected |
|------------|--------------|
| MKMapSnapshotter | Complex async flow, task cancellation, timing issues with width |
| MKMapView (UIKit) | Requires UIViewRepresentable wrapper, more code |
| Third-party libraries | Unnecessary dependency |

**Implementation Pattern**:
```swift
Map(coordinateRegion: .constant(region), interactionModes: [])
    .disabled(true)  // Extra safety - no gestures
    .allowsHitTesting(false)  // Tap handled by overlay
```

### 2. Location Permission Integration

**Decision**: Keep all permission logic in LandingPageViewModel (existing infrastructure)

**Rationale**:
- LandingPageViewModel already uses LocationPermissionHandler
- No need for separate service or ViewModel for map
- Simple Model enum passed to MapPreviewView
- Permission changes trigger model update

**Integration Pattern**:
- `loadData()` checks permissions and gets location
- Sets `mapPreviewModel` based on result
- View just renders based on Model

### 3. Region Calculation (10km Radius)

**Decision**: Use MKCoordinateRegion with latitudinalMeters/longitudinalMeters

**Implementation**:
```swift
let region = MKCoordinateRegion(
    center: CLLocationCoordinate2D(latitude: lat, longitude: lng),
    latitudinalMeters: 20_000,  // 10 km radius = 20 km span
    longitudinalMeters: 20_000
)
```

### 4. Tap Gesture Handling

**Decision**: Overlay with tap gesture on parent container

**Rationale**:
- Map with `interactionModes: []` doesn't respond to taps
- Wrap in container with `.onTapGesture`
- Closure in Model.map case provides callback

**Implementation**:
```swift
case .map(let region, let onTap):
    Map(coordinateRegion: .constant(region), interactionModes: [])
        .disabled(true)
        .allowsHitTesting(false)
        .overlay {
            Color.clear
                .contentShape(Rectangle())
                .onTapGesture { onTap() }
        }
```

### 5. State Management

**Decision**: Simple Model enum without error state

**Rationale**:
- Map handles its own loading/offline states internally
- No async snapshot = no explicit error handling needed
- Only two real states: have location (show map) or don't (show permission prompt)

**Model**:
```swift
enum Model: Equatable {
    case loading
    case map(region: MKCoordinateRegion, onTap: () -> Void)
    case permissionRequired(message: String, onGoToSettings: () -> Void)
}
```

### 6. Localization

**Strings needed** (simplified - no error state):
```text
"mapPreview.permission.message" = "Enable location to see nearby area.";
"mapPreview.permission.settingsButton" = "Go to Settings";
```

## Resolution Summary

| Item | Resolution |
|------|------------|
| Map rendering | SwiftUI Map with `interactionModes: []` |
| Async pattern | None needed - Map is synchronous |
| Permission integration | Existing LandingPageViewModel logic |
| Region calculation | MKCoordinateRegion with 20km span |
| State management | Simple 3-case enum (loading, map, permissionRequired) |
| Tap handling | Overlay with Color.clear + onTapGesture |
| Error handling | Not needed - Map handles internally |

## Dependencies

**No new dependencies** - uses only:
- MapKit (for MKCoordinateRegion)
- SwiftUI Map view
- Existing LocationPermissionHandler
