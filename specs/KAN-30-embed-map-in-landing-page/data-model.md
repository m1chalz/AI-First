# Data Model: iOS Map Preview Component

**Feature**: KAN-30 - Embed Map in Landing Page
**Date**: 2025-12-19

## Entities

### MapPreviewView.Model (Simple Enum)

Simple value type passed to MapPreviewView. LandingPageViewModel creates and updates it.

**Location**: `iosApp/iosApp/Features/LandingPage/Views/Components/MapPreviewView_Model.swift`

```swift
import MapKit

extension MapPreviewView {
    /// Model determining what MapPreviewView renders.
    enum Model: Equatable {
        /// Initial loading state (before location is determined)
        case loading
        
        /// Show map centered on region
        case map(region: MKCoordinateRegion, onTap: () -> Void)
        
        /// Location permission not granted
        case permissionRequired(message: String, onGoToSettings: () -> Void)
        
        static func == (lhs: Model, rhs: Model) -> Bool {
            switch (lhs, rhs) {
            case (.loading, .loading):
                return true
            case let (.map(lhsRegion, _), .map(rhsRegion, _)):
                return lhsRegion.center.latitude == rhsRegion.center.latitude
                    && lhsRegion.center.longitude == rhsRegion.center.longitude
            case let (.permissionRequired(lhsMsg, _), .permissionRequired(rhsMsg, _)):
                return lhsMsg == rhsMsg
            default:
                return false
            }
        }
    }
}
```

**Cases**:

| Case | Payload | Description |
|------|---------|-------------|
| loading | - | Initial state before location determined |
| map | MKCoordinateRegion, onTap | Show map with region, tap triggers callback |
| permissionRequired | message, onGoToSettings | Show permission prompt with settings button |

**No error case** - SwiftUI Map handles loading/offline states internally.

### Helper: Region Creation

```swift
extension Coordinate {
    /// Creates map region with 10km radius centered on this coordinate.
    func mapRegion(radiusMeters: Double = 10_000) -> MKCoordinateRegion {
        MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: latitude, longitude: longitude),
            latitudinalMeters: radiusMeters * 2,
            longitudinalMeters: radiusMeters * 2
        )
    }
}
```

## State Flow

```
┌─────────────────────────────────────────────────────────────┐
│                  LandingPageViewModel                        │
│                                                             │
│  @Published var mapPreviewModel: MapPreviewView.Model       │
│                                                             │
│  loadData() {                                               │
│    1. Request location permissions                          │
│    2. If authorized + location → .map(region, onTap)        │
│    3. If not authorized → .permissionRequired(...)          │
│  }                                                          │
│                          │                                  │
└──────────────────────────┼──────────────────────────────────┘
                           │
                           ▼
                ┌─────────────────────┐
                │   MapPreviewView    │
                │                     │
                │   switch model {    │
                │     .loading → ...  │
                │     .map → Map(...) │
                │     .permReq → ...  │
                │   }                 │
                └─────────────────────┘
```

## Integration with Existing Domain

### Existing Types Used

| Type | Location | Usage |
|------|----------|-------|
| Coordinate | `/iosApp/Domain/Models/Coordinate.swift` | User location |
| LocationPermissionStatus | `/iosApp/Domain/LocationPermissionStatus.swift` | Permission state |
| LocationPermissionHandler | `/iosApp/Domain/Services/LocationPermissionHandler.swift` | Already in LandingPageViewModel |

## No Service Needed

Unlike MKMapSnapshotter approach:
- **No MapSnapshotService** - Map renders synchronously
- **No async/await** for map generation
- **No task cancellation** logic
- **No protocol/fake** for testing map rendering

Testing focuses on ViewModel logic (setting correct Model based on permission/location).

## No Backend Changes Required

iOS-only feature using device location and Apple Maps tiles.
