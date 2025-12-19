# Data Model: iOS Map Preview - Display Missing Pet Pins

**Feature Branch**: `KAN-30-ios-show-pins-on-the-map`  
**Created**: 2025-12-19

## Entity Changes

### New: MapPreviewView.PinModel

Lightweight presentation model for map pins. Used by `MapPreviewView` to render markers.

**Location**: `/iosApp/iosApp/Features/LandingPage/Views/Components/MapPreviewView_PinModel.swift`

```swift
extension MapPreviewView {
    struct PinModel: Identifiable, Equatable {
        /// Unique identifier (same as source announcement ID)
        let id: String
        
        /// Geographic coordinate for pin placement
        let coordinate: Coordinate
        
        /// Converts to CLLocationCoordinate2D for MapKit
        var clLocationCoordinate: CLLocationCoordinate2D {
            CLLocationCoordinate2D(latitude: coordinate.latitude, longitude: coordinate.longitude)
        }
    }
}
```

**Relationships**:
- Derived from `Announcement` (1:1 mapping)
- Used by `MapPreviewView.Model.map` case

### Modified: MapPreviewView.Model

Extended `.map` case to include pins array.

**Location**: `/iosApp/iosApp/Features/LandingPage/Views/Components/MapPreviewView_Model.swift`

```swift
enum Model: Equatable {
    case loading
    case map(region: MKCoordinateRegion, pins: [PinModel] = [], onTap: () -> Void)
    case permissionRequired(message: String, onGoToSettings: () -> Void)
    
    // Equatable implementation updated to compare pins
}
```

**Changes**:
- Added `pins: [PinModel] = []` parameter with default empty array
- Updated `Equatable` implementation to compare pin IDs

### Modified: AnnouncementListQuery

Added range parameter for geographic filtering.

**Location**: `/iosApp/iosApp/Domain/Models/AnnouncementListQuery.swift`

```swift
struct AnnouncementListQuery: Equatable {
    let limit: Int?
    let sortBy: SortOption
    let location: Coordinate?
    let range: Int  // NEW: Search radius in kilometers
    
    // Factory method updated
    static func landingPageQuery(location: Coordinate?) -> AnnouncementListQuery {
        AnnouncementListQuery(
            limit: 5,
            sortBy: .createdAtDescending,
            location: location,
            range: 10  // 10 km for landing page (FR-001)
        )
    }
    
    static func defaultQuery(location: Coordinate?) -> AnnouncementListQuery {
        AnnouncementListQuery(
            limit: nil,
            sortBy: .createdAtDescending,
            location: location,
            range: 100  // 100 km default
        )
    }
}
```

**Changes**:
- Added `range: Int` property
- Updated factory methods with appropriate range values

## Existing Entities (Reference)

### Announcement

Existing domain model - no changes required.

**Pin-Eligible Criteria** (FR-014, FR-005):
- `status` must be `.active` or `.found` (not `.closed`)
- `coordinate` must have valid latitude/longitude (already enforced by mapper)

```swift
struct Announcement {
    let id: String
    let coordinate: Coordinate  // Used for pin placement
    let status: AnnouncementStatus  // Filter: active, found only
    // ... other fields unchanged
}
```

### Coordinate

Existing domain model - no changes required.

```swift
struct Coordinate: Codable, Equatable {
    let latitude: Double
    let longitude: Double
    
    func mapRegion(radiusMeters: Double = 10_000) -> MKCoordinateRegion
}
```

### AnnouncementStatus

Existing enum - no changes required.

```swift
enum AnnouncementStatus: String, Codable {
    case active = "ACTIVE"   // Backend: MISSING - Show pin
    case found = "FOUND"     // Show pin
    case closed = "CLOSED"   // Do NOT show pin (FR-014)
}
```

## Data Flow

```
┌─────────────────────────────────────────────────────────────────┐
│ AnnouncementRepository.getAnnouncements(near:range:)            │
│ Backend API: GET /api/v1/announcements?lat=X&lng=Y&range=10     │
└──────────────────────────────┬──────────────────────────────────┘
                               │ [Announcement]
                               ▼
┌──────────────────────────────────────────────────────────────────┐
│ AnnouncementCardsListViewModel                                   │
│ - cardViewModels: [AnnouncementCardViewModel]  (for list + pins) │
│   (Backend returns only active/found - no filtering needed)      │
└──────────────────────────────┬───────────────────────────────────┘
                               │ cardViewModels
                               ▼
┌──────────────────────────────────────────────────────────────────┐
│ LandingPageViewModel.updateMapPreviewModel()                     │
│ - Creates [MapPinModel] from listViewModel.cardViewModels        │
│ - Sets mapPreviewModel = .map(region:pins:onTap:)                │
└──────────────────────────────┬───────────────────────────────────┘
                               │ MapPreviewView.Model
                               ▼
┌──────────────────────────────────────────────────────────────────┐
│ MapPreviewView                                                   │
│ - Renders Map with Marker for each pin                           │
│ - interactionModes: [] (static, no pan/zoom)                     │
│ - Overlay captures tap for future navigation                     │
└──────────────────────────────────────────────────────────────────┘
```

**Note**: Backend returns only `active` (lost) and `found` announcements, so no client-side status filtering is needed.

## Validation Rules

| Field | Rule | Enforcement |
|-------|------|-------------|
| `MapPinModel.id` | Must match source Announcement ID | Derived from Announcement |
| `MapPinModel.coordinate` | Valid lat (-90 to 90), lng (-180 to 180) | AnnouncementMapper validation |
| `AnnouncementListQuery.range` | Positive integer (km) | Factory methods |
| Pin status filter | `.active` or `.found` only | Computed property filter |

## State Transitions

### Pin Display States

```
                    ┌─────────────────────┐
                    │  MapPreviewView     │
                    │  Model States       │
                    └─────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
   ┌─────────┐        ┌─────────────┐      ┌─────────────────┐
   │ loading │        │ .map(pins)  │      │ permissionReq.  │
   │         │        │             │      │                 │
   │ No pins │        │ 0-N pins    │      │ No pins         │
   └─────────┘        └─────────────┘      └─────────────────┘
        │                     │
        │ Location OK         │ Location denied
        │ + fetch complete    │
        ▼                     │
   ┌─────────────┐            │
   │ .map(pins)  │ ◄──────────┘
   │             │   (if permission granted later)
   └─────────────┘
```

### Pin Count Scenarios

| Scenario | Pins Array | Visual Result |
|----------|------------|---------------|
| No announcements in 10km | `[]` | Empty map (FR-006) |
| Backend API error | `[]` | Empty map, no error msg (FR-006) |
| 5 announcements, 3 active, 1 found, 1 closed | 4 pins | Pins for active + found |
| All announcements closed | `[]` | Empty map |
| Overlapping coordinates | Multiple pins same location | Pins overlap visually (FR-004) |

