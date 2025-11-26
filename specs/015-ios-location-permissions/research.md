# Research: iOS Location Permissions Handling

**Feature Branch**: `015-ios-location-permissions`  
**Date**: 2025-11-26  
**Purpose**: Research technical approaches for iOS location permission handling with Swift Concurrency

## Research Topics

### 1. CoreLocation API with Swift Concurrency

**Question**: How to integrate CLLocationManager with Swift Concurrency (async/await) for permission requests and location fetching?

**Decision**: Use CLLocationManagerDelegate with Continuation-based async wrappers

**Rationale**:
- CLLocationManager is callback-based (delegate pattern)
- Swift Concurrency doesn't natively support CLLocationManager
- Use `withCheckedThrowingContinuation` to bridge delegate callbacks to async/await
- Wrap permission requests and location updates in async methods

**Implementation Pattern**:
```swift
actor LocationService: NSObject, CLLocationManagerDelegate, LocationServiceProtocol {
    private let locationManager = CLLocationManager()
    private var permissionContinuation: CheckedContinuation<LocationPermissionStatus, Never>?
    private var locationContinuation: CheckedContinuation<CLLocation, Error>?
    
    func requestWhenInUseAuthorization() async -> LocationPermissionStatus {
        return await withCheckedContinuation { continuation in
            self.permissionContinuation = continuation
            locationManager.requestWhenInUseAuthorization()
        }
    }
    
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        // Convert CoreLocation status to domain type
        let status = LocationPermissionStatus(from: manager.authorizationStatus)
        permissionContinuation?.resume(returning: status)
        permissionContinuation = nil
    }
}
```

**Alternatives Considered**:
- ❌ Combine + CLLocationManager: Violates constitution (no Combine for new code)
- ❌ Callback-based pattern: Not compatible with Swift Concurrency in ViewModels
- ✅ Continuation-based wrapper: Clean async/await interface, testable

**References**:
- Apple Documentation: [CLLocationManagerDelegate](https://developer.apple.com/documentation/corelocation/cllocationmanagerdelegate)
- Swift Concurrency: [withCheckedContinuation](https://developer.apple.com/documentation/swift/withcheckedcontinuation(function:_:))

---

### 2. iOS Permission Status Observation

**Question**: How to observe runtime changes in location permission status when user toggles permissions in Settings?

**Decision**: Poll permission status when app returns to foreground via SwiftUI's `.onChange(of: scenePhase)`

**Rationale**:
- iOS calls `locationManagerDidChangeAuthorization` when permission status changes, but only within app lifecycle
- When user goes to Settings and returns, we need to poll current status
- SwiftUI's `scenePhase` environment value detects app foreground/background transitions
- ViewModel checks status when app becomes active (`.onChange(of: scenePhase)`)
- Simple, effective, no Combine/reactive frameworks needed

**Implementation Pattern**:
```swift
// In ViewModel:
var onOpenAppSettings: (() -> Void)?  // Coordinator callback

func checkPermissionStatusChange() async {
    let newStatus = await locationService.authorizationStatus
    
    if newStatus.isAuthorized && !locationPermissionStatus.isAuthorized {
        locationPermissionStatus = newStatus
        await loadAnimals() // Refresh with location
    } else {
        locationPermissionStatus = newStatus
    }
}

func openSettings() {
    onOpenAppSettings?()  // Delegates to coordinator
}

// In SwiftUI View:
.onChange(of: scenePhase) { oldPhase, newPhase in
    if oldPhase == .background && newPhase == .active {
        Task {
            await viewModel.checkPermissionStatusChange()
        }
    }
}

// In Coordinator:
func start() {
    let viewModel = AnimalListViewModel(...)
    viewModel.onOpenAppSettings = { [weak self] in
        self?.openAppSettings()
    }
}

private func openAppSettings() {
    guard let url = URL(string: UIApplication.openSettingsURLString) else { return }
    UIApplication.shared.open(url)
}
```

**Alternatives Considered**:
- ❌ @Published in LocationService: Would require Combine (prohibited by constitution)
- ❌ Continuous polling with Timer: Inefficient, battery drain
- ❌ NotificationCenter: More complex than scenePhase observation
- ✅ scenePhase + polling on foreground: Simple, efficient, SwiftUI-native, no Combine

**References**:
- Apple Documentation: [locationManagerDidChangeAuthorization(_:)](https://developer.apple.com/documentation/corelocation/cllocationmanagerdelegate/3563956-locationmanagerdidchangeauthoriz)

---

### 3. Info.plist Privacy Keys

**Question**: What Info.plist keys are required for location permission requests on iOS?

**Decision**: Add `NSLocationWhenInUseUsageDescription` key with user-facing explanation

**Rationale**:
- iOS requires privacy description string in Info.plist before showing system permission alert
- Missing key causes runtime crash when requesting permissions
- Key must contain clear explanation of why app needs location (App Store requirement)

**Required Key**:
```xml
<key>NSLocationWhenInUseUsageDescription</key>
<string>We use your location to show you nearby pets available for adoption.</string>
```

**Alternatives Considered**:
- `NSLocationAlwaysUsageDescription`: Not needed (app only needs "when in use" access)
- `NSLocationAlwaysAndWhenInUseUsageDescription`: Overkill for startup screen use case

**References**:
- Apple Documentation: [Requesting Authorization for Location Services](https://developer.apple.com/documentation/corelocation/requesting_authorization_for_location_services)
- App Store Review Guidelines: [Privacy - Location Services](https://developer.apple.com/app-store/review/guidelines/#privacy)

---

### 4. Testing Strategy for CoreLocation

**Question**: How to test LocationService code that depends on CLLocationManager without real GPS hardware?

**Decision**: Use protocol-based dependency injection with FakeLocationService for unit tests

**Rationale**:
- CLLocationManager cannot be easily mocked (system singleton behavior)
- Protocol abstraction (`LocationServiceProtocol`) enables test doubles
- FakeLocationService returns predetermined permission states and coordinates
- ViewModels depend on protocol, not concrete implementation

**Test Pattern**:
```swift
class FakeLocationService: LocationServiceProtocol {
    var stubbedAuthorizationStatus: LocationPermissionStatus = .notDetermined
    var stubbedLocation: UserLocation?  // nil = location unavailable
    var requestAuthorizationCalled = false
    
    var authorizationStatus: LocationPermissionStatus {
        get async { stubbedAuthorizationStatus }
    }
    
    func requestWhenInUseAuthorization() async -> LocationPermissionStatus {
        requestAuthorizationCalled = true
        return stubbedAuthorizationStatus
    }
    
    func requestLocation() async -> UserLocation? {
        return stubbedLocation  // Returns nil to simulate failure
    }
}
```

**Alternatives Considered**:
- ❌ XCTest mocking frameworks: Adds dependency, limited Swift Concurrency support
- ❌ Real device testing only: Slow, fragile, doesn't cover all permission states
- ✅ Protocol-based fakes: Simple, fast, covers all edge cases

**Design Note - Optional vs Throws**:
- `requestLocation()` returns `Optional` instead of throwing errors
- Rationale: All failures handled identically (silent fallback), no need to differentiate error types
- Simpler API: `if let location = await service.requestLocation()` vs `try await` boilerplate
- More idiomatic Swift: `nil` naturally represents "unavailable"

**References**:
- Apple Documentation: [Testing Your Location Code](https://developer.apple.com/documentation/corelocation/testing_your_location_code)
- Constitution Principle III: Interface-Based Design

---

### 5. Custom Permission Popup UI Design

**Question**: How to display custom informational popup when user has denied location permissions?

**Decision**: Use SwiftUI `.alert()` modifier with custom message and Settings navigation

**Critical Distinction - Two Different Alerts**:

**1. iOS System Alert (Automatic)**:
- **When**: Status = `.notDetermined` (first-time users)
- **Trigger**: iOS shows automatically when calling `requestWhenInUseAuthorization()`
- **Control**: ❌ Zero control - iOS manages display, text (from Info.plist), and buttons
- **Buttons**: 3 options - "Allow While Using", "Allow Once", "Don't Allow"
- **Cannot be customized** - iOS system behavior

**2. Custom SwiftUI Alert (Manual)**:
- **When**: Status = `.denied` or `.restricted` (user already denied or system blocked)
- **Trigger**: Our code sets `showPermissionDeniedAlert = true`
- **Control**: ✅ Full control - we decide when, what text, and what buttons
- **Buttons**: 2 options - "Go to Settings", "Cancel"
- **Why needed**: iOS system alert WON'T show again for `.denied` status

**Rationale**:
- SwiftUI `.alert()` provides native iOS modal dialog appearance
- Can customize title, message, and buttons
- "Go to Settings" button uses `UIApplication.open(URL)` to open app settings
- Alert dismissal triggers fallback mode (query without location)

**Implementation Pattern (MVVM-C compliant)**:
```swift
// View - NO navigation logic, delegates to ViewModel
struct AnimalListView: View {
    @ObservedObject var viewModel: AnimalListViewModel
    
    var body: some View {
        ZStack {
            // Existing animal list UI
            // ... (keep existing code as-is) ...
        }
        .alert(
            "Location Access Needed",
            isPresented: $viewModel.showPermissionDeniedAlert,
            actions: {
                Button("Go to Settings") {
                    viewModel.openSettings()  // ✅ Delegates to ViewModel
                }
                Button("Cancel", role: .cancel) {
                    viewModel.continueWithoutLocation()
                }
            },
            message: {
                Text(L10n.Location.Permission.Popup.message)
            }
        )
    }
}

// ViewModel - Delegates to Coordinator via callback
@MainActor
class AnimalListViewModel: ObservableObject {
    var onOpenAppSettings: (() -> Void)?
    
    func openSettings() {
        onOpenAppSettings?()  // Coordinator handles actual navigation
    }
}

// Coordinator - Handles system navigation
class StartupCoordinator {
    func start() {
        let viewModel = AnimalListViewModel(...)
        viewModel.onOpenAppSettings = { [weak self] in
            self?.openAppSettings()
        }
        // ...
    }
    
    private func openAppSettings() {
        guard let url = URL(string: UIApplication.openSettingsURLString) else { return }
        UIApplication.shared.open(url)
    }
}
```

**Alternatives Considered**:
- ❌ Custom modal view: More code, non-native appearance
- ❌ Full-screen overlay: Too intrusive for informational message
- ✅ SwiftUI alert: Native iOS pattern, minimal code, familiar UX

**References**:
- Apple Documentation: [alert(_:isPresented:actions:message:)](https://developer.apple.com/documentation/swiftui/view/alert(_:ispresented:actions:message:)-36yf2)
- Human Interface Guidelines: [Alerts](https://developer.apple.com/design/human-interface-guidelines/alerts)

---

### 6. Permission Popup Session Persistence

**Question**: How to ensure custom permission popup is shown only once per app session for denied/restricted states?

**Decision**: Use ViewModel-level boolean flag `hasShownPermissionAlert` to track display state

**Rationale**:
- Requirement FR-013: "Custom permission popup MUST be displayed once per app session"
- Simple boolean flag in ViewModel tracks if alert was already shown
- Flag resets on app restart (session boundary)
- No persistence needed (UserDefaults not required)

**Implementation Pattern**:
```swift
// Presentation layer extension (UI decision logic)
extension LocationPermissionStatus {
    var shouldShowCustomPopup: Bool {
        self == .denied || self == .restricted
    }
}

@MainActor
class AnimalListViewModel: ObservableObject {
    @Published var showPermissionDeniedAlert = false
    private var hasShownPermissionAlert = false
    
    func handlePermissionStatus(_ status: LocationPermissionStatus) {
        if status.shouldShowCustomPopup && !hasShownPermissionAlert {
            showPermissionDeniedAlert = true
            hasShownPermissionAlert = true
        }
    }
}
```

**Clean Architecture Note**: 
- ViewModel uses `LocationPermissionStatus` (domain type), NOT `CLAuthorizationStatus` (CoreLocation type)
- `shouldShowCustomPopup` is presentation extension, NOT domain property (UI concern, not business rule)
- This maintains separation: domain defines states, presentation decides how to display them

**Alternatives Considered**:
- ❌ UserDefaults persistence: Overcomplicates (requirement says "per session")
- ❌ Coordinator-level flag: Violates MVVM-C separation (state belongs in ViewModel)
- ✅ ViewModel boolean flag: Simple, matches "per session" requirement

**References**:
- Feature Spec: FR-013 (Custom permission popup MUST be displayed once per app session)

---

### 7. Fallback Mode Without Location

**Question**: How to handle animal listing queries when location is unavailable or unauthorized?

**Decision**: Repository accepts optional `UserLocation` parameter, omits from query when nil

**Rationale**:
- Requirement FR-009: "System MUST query server without location coordinates when unavailable"
- AnimalRepository protocol: `func fetchAnimals(near: UserLocation?) async throws -> [Animal]`
- When location is nil, repository omits lat/lon query parameters
- Server returns unfiltered animal listings (existing backend behavior)

**Implementation Pattern**:
```swift
protocol AnimalRepositoryProtocol {
    func fetchAnimals(near location: UserLocation?) async throws -> [Animal]
}

class AnimalRepository: AnimalRepositoryProtocol {
    func fetchAnimals(near location: UserLocation?) async throws -> [Animal] {
        var queryItems: [URLQueryItem] = []
        
        if let location = location {
            queryItems.append(URLQueryItem(name: "lat", value: "\(location.latitude)"))
            queryItems.append(URLQueryItem(name: "lon", value: "\(location.longitude)"))
        }
        
        // Build URL with optional query parameters
        // ...
    }
}
```

**Alternatives Considered**:
- ❌ Separate methods `fetchAnimals()` and `fetchAnimalsNear(_:)`: Code duplication
- ❌ Default coordinates (0, 0): Incorrect semantics, could return wrong results
- ❌ CLLocationCoordinate2D parameter: Framework type in domain interface
- ✅ Optional UserLocation: Clean API, domain type, clear intent, minimal changes

**Design Note**: Using `UserLocation` (domain type) instead of `CLLocationCoordinate2D` (framework type) maintains clean architecture - repository protocol doesn't depend on CoreLocation framework.

**References**:
- Feature Spec: FR-009 (query without location when unavailable)
- Existing backend API: `/api/pets?lat=X&lon=Y` (lat/lon already optional)

---

## Summary of Technical Decisions

| Decision Area | Chosen Approach | Key Reason |
|---------------|-----------------|------------|
| CoreLocation async wrapper | Continuation-based actor | Swift Concurrency compatibility, testable |
| Permission status type | Domain model `LocationPermissionStatus` | Clean architecture, abstracts CoreLocation from domain layer |
| Permission status observation | scenePhase + polling on foreground | Simple, efficient, SwiftUI-native, no Combine |
| Info.plist configuration | NSLocationWhenInUseUsageDescription | iOS requirement, App Store compliance |
| Testing strategy | Protocol-based fakes | Fast unit tests, covers all permission states |
| Custom permission UI | SwiftUI `.alert()` modifier | Native appearance, minimal code |
| Session persistence | ViewModel boolean flag | Simple, matches "per session" requirement |
| Fallback mode | Optional UserLocation parameter | Clean API, domain type, minimal repository changes |
| Location return type | Optional (not throws) | All failures → nil, simpler than error differentiation |

---

## Known iOS Behaviors

### "Allow Once" Permission (iOS 14+)

**Behavior**: When user taps "Allow Once" in system alert:
- CLLocationManager reports `.authorizedWhenInUse` (same as "Allow While Using App")
- Permission is **temporary** - expires after app termination or ~1 use
- Next app launch: status reverts to `.notDetermined`
- System alert will appear again on next startup

**Impact on our implementation**:
- ✅ No special handling required - iOS manages expiration automatically
- ✅ App behavior is correct: request permission again when `.notDetermined`
- ⚠️ User may see permission alert on every app launch if they keep choosing "Allow Once"
- ✅ Fallback mode works: if user repeatedly denies, they can still browse animals without location

**Testing note**: E2E tests should cover "Allow Once" scenario to verify repeated alerts don't cause issues.

---

## Implementation Risks

| Risk | Mitigation Strategy |
|------|---------------------|
| CLLocationManager delegate race conditions | Use Swift `actor` for LocationService (thread-safe) |
| Permission changes during active query | Complete current query, ignore change (FR-015) |
| Missing Info.plist key (runtime crash) | Add to tasks checklist, verify in PR review |
| Location timeout or GPS unavailable | Set timeout (5s), catch errors, fallback to nil coordinates |
| "Allow Once" users see alert every time | Expected iOS behavior, no mitigation needed (user choice) |
| E2E tests flakiness with system alerts | Use Appium capabilities to auto-accept alerts in test mode |

---

## Next Steps

Phase 1 tasks:
1. Generate data model for location entities (UserLocation, LocationPermissionStatus)
2. Generate API contracts for LocationServiceProtocol and AnimalRepositoryProtocol
3. Update quickstart.md with setup instructions (Info.plist, service registration)
4. Update agent context with CoreLocation and Swift Concurrency patterns

