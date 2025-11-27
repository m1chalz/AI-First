# Data Model: iOS Location Permissions

**Feature Branch**: `015-ios-location-permissions`  
**Date**: 2025-11-26  
**Platform**: iOS (Swift)

## Overview

This document defines the domain models and state management for iOS location permission handling. Models follow Swift conventions with value types (structs) for immutable data and reference types (actors) for stateful services.

## Domain Entities

### 1. LocationPermissionStatus (Enum)

**Purpose**: Represents the current authorization state for location services (domain model)

**Type**: Swift enum abstracting `CLAuthorizationStatus` from CoreLocation framework

**Definition**:
```swift
enum LocationPermissionStatus: Equatable {
    case notDetermined       // User hasn't been asked yet
    case authorizedWhenInUse // Granted "While Using App" permission
    case authorizedAlways    // Granted "Always" permission (not used in this feature)
    case denied              // User explicitly denied permission
    case restricted          // Permission restricted by system policies (parental controls, etc.)
    
    var isAuthorized: Bool {
        self == .authorizedWhenInUse || self == .authorizedAlways
    }
}
    
    /// Converts CoreLocation status to domain model (abstraction layer).
    init(from clStatus: CLAuthorizationStatus) {
        switch clStatus {
        case .notDetermined:
            self = .notDetermined
        case .authorizedWhenInUse:
            self = .authorizedWhenInUse
        case .authorizedAlways:
            self = .authorizedAlways
        case .denied:
            self = .denied
        case .restricted:
            self = .restricted
        @unknown default:
            self = .notDetermined
        }
    }
}
```

**Validation Rules**:
- Must map 1:1 with CLAuthorizationStatus cases
- `isAuthorized` computed property determines if location can be fetched (domain business rule)
- **Domain abstraction**: LocationServiceProtocol returns this type, NOT CLAuthorizationStatus

**Presentation Extension** (defined in ViewModel layer):
```swift
extension LocationPermissionStatus {
    /// Whether to show custom permission popup (presentation logic).
    /// NOT part of domain - this is UI concern.
    var shouldShowCustomPopup: Bool {
        self == .denied || self == .restricted
    }
}
```
- **Rationale**: "Should show popup" is UI decision, not domain rule
- **Location**: Defined in `/iosApp/iosApp/Features/StartupScreen/AnimalListViewModel.swift`
- **Separation of concerns**: Domain knows about permission states, presentation decides how to display them

**State Transitions**:
```
notDetermined → [user taps "Allow While Using App"] → authorizedWhenInUse (permanent)
notDetermined → [user taps "Allow Once"] → authorizedWhenInUse (temporary, expires after session)
notDetermined → [user taps "Don't Allow"] → denied

authorizedWhenInUse (Allow Once expired) → notDetermined (next app launch)
authorizedWhenInUse (permanent) → authorizedWhenInUse (persists across sessions)

denied → [user enables in Settings] → authorizedWhenInUse
authorizedWhenInUse → [user disables in Settings] → denied
```

**"Allow Once" Behavior (iOS 14+)**:
- User taps "Allow Once" → status becomes `.authorizedWhenInUse` (indistinguishable from permanent)
- Permission expires after app termination or after ~1 use
- Next app launch → status reverts to `.notDetermined`
- App will show system alert again on next startup
- **No special handling required** - iOS manages expiration automatically

---

### 2. UserLocation (Struct)

**Purpose**: Represents geographic coordinates obtained from device location services (pure domain type)

**Type**: Value type (struct)

**Definition**:
```swift
struct UserLocation: Equatable {
    let latitude: Double
    let longitude: Double
    let timestamp: Date
    
    init(latitude: Double, longitude: Double, timestamp: Date = Date()) {
        self.latitude = latitude
        self.longitude = longitude
        self.timestamp = timestamp
    }
}
```

**Validation Rules**:
- Latitude: -90.0 to +90.0 (valid geographic range)
- Longitude: -180.0 to +180.0 (valid geographic range)
- Timestamp: Captures when location was fetched (for staleness checks)

**Usage**:
- Fetched from LocationService when permission is granted
- `requestLocation()` returns `Optional` - nil when fetch fails (any reason)
- Passed directly to AnimalRepository (domain type, no conversion needed)
- NOT persisted (ephemeral, refetched on each app launch)

**Design Note**:
- Pure domain type - NO CoreLocation dependencies (no CLLocationCoordinate2D conversion)
- Optional semantics: `nil` = location unavailable (permission denied, GPS off, timeout, etc.)
- All failures handled identically → silent fallback to query without location
- Simpler than `throws`: No need to differentiate error types

---

## ViewModel State

### AnimalListViewModel Extensions

**New Published Properties**:
```swift
@MainActor
class AnimalListViewModel: ObservableObject {
    // Existing properties
    @Published var animals: [Animal] = []
    @Published var isLoading = false
    @Published var errorMessage: String?
    
    // NEW: Location permission properties
    @Published var locationPermissionStatus: LocationPermissionStatus = .notDetermined
    @Published var showPermissionDeniedAlert = false  // Controls CUSTOM alert (denied/restricted)
    @Published var currentLocation: UserLocation?
    
    // NEW: Session-level flag (not published)
    private var hasShownPermissionAlert = false
    
    // NEW: Coordinator callback for Settings navigation (MVVM-C pattern)
    var onOpenAppSettings: (() -> Void)?
}
```

**State Management Rules**:
- `locationPermissionStatus`: Updated on every delegate callback from LocationService
- `showPermissionDeniedAlert`: Controls **CUSTOM alert** (not iOS system alert!)
  - Set to `true` when denied/restricted AND not shown in session
  - iOS system alert is shown automatically by `requestWhenInUseAuthorization()`
- `currentLocation`: Set when location fetch succeeds, `nil` otherwise
- `hasShownPermissionAlert`: Prevents multiple CUSTOM popups per session (FR-013)
- `onOpenAppSettings`: **Coordinator callback** for Settings navigation
  - Set by Coordinator during ViewModel initialization
  - ViewModel calls this closure when user taps "Go to Settings"
  - **MVVM-C pattern**: View → ViewModel → Coordinator → UIApplication

**Two Alert Types**:
1. **iOS System Alert** (automatic): Shown by `requestWhenInUseAuthorization()` when status = `.notDetermined`
2. **Custom SwiftUI Alert** (manual): Controlled by `showPermissionDeniedAlert` when status = `.denied`/`.restricted`

**Navigation Responsibility (MVVM-C)**:
- ❌ View MUST NOT call `UIApplication.shared.open()` directly
- ✅ View calls `viewModel.openSettings()`
- ✅ ViewModel calls `onOpenAppSettings?()` closure
- ✅ Coordinator handles actual `UIApplication.shared.open(settingsUrl)`

---

## Service Layer

### LocationServiceProtocol (Interface)

**Purpose**: Abstract interface for location permission and coordinate fetching

**Methods**:
```swift
protocol LocationServiceProtocol {
    /// Current authorization status (reactive, domain type)
    var authorizationStatus: LocationPermissionStatus { get async }
    
    /// Requests "When In Use" location permission from user
    /// - Returns: Updated authorization status after user responds (domain type)
    func requestWhenInUseAuthorization() async -> LocationPermissionStatus
    
    /// Fetches current device location (requires authorized status)
    /// - Returns: UserLocation with coordinates and timestamp, or nil if unavailable
    /// - Note: Returns nil for any failure (silent fallback to query without coordinates)
    func requestLocation() async -> UserLocation?
}
```

**Implementation Notes**:
- Implemented by `LocationService` (actor wrapping CLLocationManager)
- **Returns domain types**: `LocationPermissionStatus` (NOT `CLAuthorizationStatus`)
- Implementation converts `CLAuthorizationStatus` to `LocationPermissionStatus` via `init(from:)`
- **Optional return**: `requestLocation()` returns `nil` instead of throwing errors (idiomatic Swift)
- Protocol enables unit testing with FakeLocationService
- All methods use Swift Concurrency (async/await)

---

## Repository Updates

### AnimalRepositoryProtocol Extension

**Modified Method Signature**:
```swift
protocol AnimalRepositoryProtocol {
    /// Fetches animals from server with optional location filtering
    /// - Parameter location: Optional user location for nearby filtering (nil = no filtering)
    /// - Returns: Array of Animal entities
    func fetchAnimals(near location: UserLocation?) async throws -> [Animal]
}
```

**Behavior**:
- When `location` is `nil`: Omit lat/lon query parameters (server returns all animals)
- When `location` is provided: Include `?lat=X&lon=Y` in query (server filters by proximity)

**Design Note**:
- Uses `UserLocation` (domain type), NOT `CLLocationCoordinate2D` (CoreLocation framework type)
- Repository is domain interface - should not depend on external frameworks
- Implementation extracts `latitude` and `longitude` from UserLocation struct

---

## Entity Relationships

```
┌─────────────────────────────────────┐
│  AnimalListViewModel                │
│  ────────────────────────────────   │
│  + locationPermissionStatus         │
│  + currentLocation: UserLocation?   │
│  + showPermissionDeniedAlert        │
│  ────────────────────────────────   │
│  + loadAnimals()                    │
│  + handlePermissionChange()         │
└──────────┬──────────────────────────┘
           │
           │ depends on
           ▼
┌─────────────────────────────────────┐
│  LocationServiceProtocol            │
│  ────────────────────────────────   │
│  + requestWhenInUseAuthorization()  │
│  + requestLocation()                │
└──────────┬──────────────────────────┘
           │
           │ produces
           ▼
┌─────────────────────────────────────┐
│  UserLocation (struct)              │
│  ────────────────────────────────   │
│  + latitude: Double                 │
│  + longitude: Double                │
│  + timestamp: Date                  │
└─────────────────────────────────────┘
           │
           │ passed to
           ▼
┌─────────────────────────────────────┐
│  AnimalRepositoryProtocol           │
│  ────────────────────────────────   │
│  + fetchAnimals(near: UserLocation?)│
└─────────────────────────────────────┘
```

---

## Validation Summary

| Entity | Validation Rules | Enforcement |
|--------|------------------|-------------|
| LocationPermissionStatus | Must match CLAuthorizationStatus cases | Compiler (enum exhaustiveness) |
| UserLocation | Lat: -90 to +90, Lon: -180 to +180 | Geographic coordinate validation |
| AnimalListViewModel | `hasShownPermissionAlert` reset on init | Unit tests verify flag behavior |

---

## Testing Strategy

| Model | Test Focus | Test Location |
|-------|-----------|---------------|
| LocationPermissionStatus | State transitions, computed properties, CLAuthorizationStatus conversion | `LocationPermissionStatusTests.swift` |
| UserLocation | Struct initialization, equality (simple value type - minimal tests) | `UserLocationTests.swift` (optional) |
| AnimalListViewModel | Permission flow integration, nil location handling, repeated `.notDetermined` handling | `AnimalListViewModelLocationTests.swift` |
| LocationService | Permission request, location fetch, nil return on failures | `LocationServiceTests.swift` |

**E2E Test Coverage for "Allow Once"**:
- Test scenario: User taps "Allow Once" → location fetched → app terminates → app relaunches
- Verify: System alert appears again (status reverted to `.notDetermined`)
- Verify: App handles repeated permission requests without errors
- Location: `/e2e-tests/src/test/resources/features/mobile/ios-location-permissions.feature`

---

## Localization Keys

Required SwiftGen keys for user-facing strings (custom permission popup only):

| Key | English Text | Usage |
|-----|--------------|-------|
| `location_permission_popup_title` | "Location Access Needed" | Alert title for denied/restricted popup |
| `location_permission_popup_message` | "Enable location access in Settings to see nearby pets." | Alert message for denied/restricted popup |
| `location_permission_popup_settings_button` | "Go to Settings" | Button to open iOS Settings |
| `location_permission_popup_cancel_button` | "Cancel" | Button to dismiss popup and continue without location |

**Note**: No error messages needed for LocationError cases (silent fallback per FR-014)

