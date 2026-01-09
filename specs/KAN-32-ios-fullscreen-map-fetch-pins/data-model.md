# Data Model: iOS Fullscreen Map - Fetch and Display Pins

**Branch**: `KAN-32-ios-fullscreen-map-fetch-pins` | **Date**: 2025-01-07

## Entities

### Existing Entities (Reused)

#### Announcement (Domain Model)
**Location**: `/iosApp/iosApp/Domain/Models/Announcement.swift`

| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| id | String | Unique identifier | Required, non-empty |
| name | String? | Animal name | Optional |
| photoUrl | String | Photo URL or placeholder | Required |
| coordinate | Coordinate | Last-seen location (lat/lng) | Required for pin placement |
| species | AnimalSpecies | Dog, Cat, Bird, etc. | Required |
| breed | String? | Specific breed name | Optional |
| gender | AnimalGender | Male, Female, Unknown | Required |
| status | AnnouncementStatus | Active, Found, Closed | Required |
| lastSeenDate | String | Date formatted DD/MM/YYYY | Required |
| description | String? | Detailed text | Optional |
| email | String? | Contact email | Optional |
| phone | String? | Contact phone | Optional |

**Pin Display**: All announcements returned by the server are displayed as pins (no client-side status filtering).

---

#### Coordinate (Domain Model)
**Location**: `/iosApp/iosApp/Domain/Models/Coordinate.swift`

| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| latitude | Double | Latitude degrees | -90 to 90 |
| longitude | Double | Longitude degrees | -180 to 180 |

**Methods**:
- `mapRegion(radiusMeters:) -> MKCoordinateRegion`: Creates MapKit region centered on coordinate

---

#### AnnouncementStatus (Domain Enum)
**Location**: `/iosApp/iosApp/Domain/Models/AnnouncementStatus.swift`

| Case | Raw Value | Backend Mapping | Pin Display |
|------|-----------|-----------------|-------------|
| active | "ACTIVE" | MISSING | ✅ Show as pin |
| found | "FOUND" | FOUND | ❌ Do not show |
| closed | "CLOSED" | CLOSED | ❌ Do not show |

---

### New/Extended Entities

#### MapPin (View Model State)
**Location**: `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/`

Lightweight pin representation for map display. Derived from `Announcement` with only fields needed for rendering.

| Field | Type | Description | Source |
|-------|------|-------------|--------|
| id | String | Unique identifier | `announcement.id` |
| coordinate | CLLocationCoordinate2D | Pin position | `announcement.coordinate` |
| species | AnimalSpecies | For pin icon variation (future) | `announcement.species` |

**Conformances**: `Identifiable`, `Equatable`

**Rationale**: 
- Separates presentation concerns from domain model
- `Identifiable` enables SwiftUI `ForEach` and optimized diffing
- `Equatable` enables selection comparison for future callout toggle

**Future Callout Extension** (separate spec):
When tap interaction is implemented, `MapPin` will need additional fields for callout:
- `name: String?` - pet name
- `photoUrl: String?` - pet photo
- `lastSeenDate: String` - date
- `locationName: String?` - location description
- `contactEmail: String?` - contact email
- `contactPhone: String?` - contact phone
- `status: AnnouncementStatus` - for badge

See Figma design: node `1192:5893` in PetSpot-wireframes

---

#### FullscreenMapViewModel State
**Location**: `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift`

Extended ViewModel state for pin fetching:

| Property | Type | Description | Initial Value |
|----------|------|-------------|---------------|
| mapRegion | MKCoordinateRegion | Current visible region | User location, 10km radius |
| legendModel | MapSectionHeaderView.Model | Legend configuration | `.fullscreenMap()` |
| pins | [MapPin] | Displayed pins on map | `[]` (empty) |
| isLoading | Bool | Internal loading state (no UI) | `false` |

**Methods**:
| Method | Signature | Description |
|--------|-----------|-------------|
| loadPins | `loadPins() async` | Fetch pins for current region on view load |
| handleRegionChange | `handleRegionChange(region:) async` | Fetch pins after gesture ends |

---

## Relationships

```
┌─────────────────────────────────────────────────────────────────┐
│                    FullscreenMapView                            │
│  - Observes ViewModel state                                     │
│  - Displays Map with pins                                       │
│  - Triggers handleRegionChange on gesture end                   │
└─────────────────────────────────┬───────────────────────────────┘
                                  │ @ObservedObject
                                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                 FullscreenMapViewModel                          │
│  @Published pins: [MapPin]                                      │
│  @Published isLoading: Bool                                     │
│  - Calls repository.getAnnouncements()                          │
│  - Maps all announcements to MapPin                             │
│  - Maps Announcement → MapPin                                   │
└─────────────────────────────────┬───────────────────────────────┘
                                  │ dependency injection
                                  ▼
┌─────────────────────────────────────────────────────────────────┐
│            AnnouncementRepositoryProtocol                       │
│  getAnnouncements(near:range:) async throws -> [Announcement]   │
└─────────────────────────────────┬───────────────────────────────┘
                                  │ HTTP
                                  ▼
┌─────────────────────────────────────────────────────────────────┐
│              Backend API /api/v1/announcements                  │
│  GET ?lat={lat}&lng={lng}&range={range}                         │
│  Returns: { data: [AnnouncementDTO, ...] }                      │
└─────────────────────────────────────────────────────────────────┘
```

## State Transitions

### Pin Loading Flow

```
┌─────────────┐     onAppear/      ┌─────────────┐     Success      ┌─────────────┐
│    Idle     │ ──────────────────>│   Loading   │ ───────────────> │   Loaded    │
│  pins: []   │  onRegionChange    │ isLoading:  │  pins updated    │  pins: [...]│
│             │                    │   true      │  instantly       │             │
└─────────────┘                    └──────┬──────┘                  └─────────────┘
                                          │
                                          │ Error (silent)
                                          ▼
                                   ┌─────────────┐
                                   │   Error     │
                                   │ keep old    │
                                   │   pins      │
                                   └─────────────┘
```

### Map Region Change Flow

```
User Gesture (pan/zoom)
         │
         ▼
┌─────────────────────┐
│  Gesture in progress │
│   (no fetch)         │
└──────────┬──────────┘
           │ gesture ends
           ▼
┌─────────────────────┐
│ onMapCameraChange   │
│ (frequency: .onEnd) │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ ViewModel.handle    │
│ RegionChange(region)│
└──────────┬──────────┘
           │ cancel previous task
           │ fetch new pins
           ▼
┌─────────────────────┐
│ Remove old pins     │
│ (instant)           │
│ Add new pins        │
│ (instant)           │
└─────────────────────┘
```

## Validation Rules

| Rule | Entity | Constraint |
|------|--------|------------|
| Pin coordinate validity | MapPin | Latitude -90..90, Longitude -180..180 |
| No status filter | Announcement | All announcements shown as pins |
| Non-empty ID | Announcement | Required for pin identification |
| Coordinate required | Announcement | Must have valid lat/lng for pin placement |

## Notes

- **No new domain entities**: Feature reuses existing `Announcement` and `Coordinate` models
- **Presentation model**: `MapPin` is view-layer convenience, not domain concept
- **Backward compatibility**: Existing `Announcement` model unchanged
- **Future extensibility**: `MapPin.species` prepared for species-specific pin icons

