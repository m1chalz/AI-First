# Quickstart: iOS Location Permissions

**Feature Branch**: `015-ios-location-permissions`  
**Date**: 2025-11-26  
**Estimated Setup Time**: 15 minutes

## Prerequisites

- Xcode 15+ installed
- iOS 15+ deployment target configured
- Existing PetSpot iOS app with AnimalListViewModel and StartupCoordinator
- SwiftGen configured for localization (L10n)

## Setup Steps

### 1. Add Info.plist Privacy Key (REQUIRED)

iOS requires privacy description before requesting location permissions.

**File**: `/iosApp/iosApp/Info.plist`

Add the following key-value pair:

```xml
<key>NSLocationWhenInUseUsageDescription</key>
<string>We use your location to show you nearby pets available for adoption.</string>
```

**⚠️ Critical**: Missing this key causes runtime crash when requesting permissions.

**Verification**:
```bash
# Check Info.plist contains location key
grep -A 1 "NSLocationWhenInUseUsageDescription" iosApp/iosApp/Info.plist
```

---

### 2. Add Localization Keys to Localizable.strings

**File**: `/iosApp/iosApp/Resources/en.lproj/Localizable.strings`

Add the following keys:

```strings
// Permission popup UI (custom alert for denied/restricted states)
"location.permission.popup.title" = "Location Access Needed";
"location.permission.popup.message" = "Enable location access in Settings to see nearby pets.";
"location.permission.popup.settings.button" = "Go to Settings";
"location.permission.popup.cancel.button" = "Cancel";
```

**Note**: No error messages needed for LocationError - app uses silent fallback per FR-014.

**Regenerate SwiftGen** after adding keys:
```bash
cd iosApp
swiftgen
```

**Verification**:
```swift
// Generated code should be accessible
let title = L10n.Location.Permission.Popup.title
let message = L10n.Location.Permission.Popup.message
```

---

### 3. Create LocationServiceProtocol and Implementation

**File**: `/iosApp/iosApp/Domain/LocationServiceProtocol.swift`

Copy from: `specs/015-ios-location-permissions/contracts/LocationServiceProtocol.swift`

**File**: `/iosApp/iosApp/Data/LocationService.swift`

Create actor implementation wrapping CLLocationManager:

**Design Note**: `requestLocation()` returns `Optional` instead of throwing errors. All location failures (permission denied, GPS unavailable, timeout) result in the same behavior: silent fallback to query without coordinates. Optional is simpler and more idiomatic than `throws` for this use case.

```swift
import CoreLocation

actor LocationService: NSObject, CLLocationManagerDelegate, LocationServiceProtocol {
    private let locationManager = CLLocationManager()
    private var permissionContinuation: CheckedContinuation<LocationPermissionStatus, Never>?
    private var locationContinuation: CheckedContinuation<UserLocation?, Never>?
    
    override init() {
        super.init()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyHundredMeters
    }
    
    var authorizationStatus: LocationPermissionStatus {
        get async {
            LocationPermissionStatus(from: locationManager.authorizationStatus)
        }
    }
    
    func requestWhenInUseAuthorization() async -> LocationPermissionStatus {
        let current = LocationPermissionStatus(from: locationManager.authorizationStatus)
        
        // Jeśli status już znany → zwróć od razu
        guard !current.isAuthorized else {
            return current  // Already authorized
        }
        
        guard current == .notDetermined else {
            return current  // Already denied/restricted
        }
        
        return await withCheckedContinuation { continuation in
            // Zabezpieczenie przed nadpisaniem continuation
            if permissionContinuation != nil {
                continuation.resume(returning: current)
                return
            }
            
            permissionContinuation = continuation
            locationManager.requestWhenInUseAuthorization()
        }
    }
    
    func requestLocation() async -> UserLocation? {
        let status = locationManager.authorizationStatus
        
        guard status == .authorizedWhenInUse || status == .authorizedAlways else {
            return nil  // Permission not granted
        }
        
        return await withCheckedContinuation { continuation in
            // Zabezpieczenie przed nadpisaniem continuation
            if locationContinuation != nil {
                continuation.resume(returning: nil)
                return
            }
            
            locationContinuation = continuation
            locationManager.requestLocation()
        }
    }
    
    // MARK: - CLLocationManagerDelegate
    
    nonisolated func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        Task {
            let status = LocationPermissionStatus(from: manager.authorizationStatus)
            await resumePermissionContinuation(with: status)
        }
    }
    
    nonisolated func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        Task {
            if let location = locations.first {
                let userLocation = UserLocation(
                    latitude: location.coordinate.latitude,
                    longitude: location.coordinate.longitude,
                    timestamp: location.timestamp
                )
                await resumeLocationContinuation(with: userLocation)
            } else {
                await resumeLocationContinuation(with: nil)
            }
        }
    }
    
    nonisolated func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        Task {
            await resumeLocationContinuation(with: nil)
        }
    }
    
    // MARK: - Private Helpers
    
    private func resumePermissionContinuation(with status: LocationPermissionStatus) {
        permissionContinuation?.resume(returning: status)
        permissionContinuation = nil
    }
    
    private func resumeLocationContinuation(with location: UserLocation?) {
        locationContinuation?.resume(returning: location)
        locationContinuation = nil
    }
}
```

---

### 4. Register LocationService in DI Container

**File**: `/iosApp/iosApp/DI/ServiceContainer.swift`

Add LocationService to container:

```swift
class ServiceContainer {
    static let shared = ServiceContainer()
    
    // Existing services
    lazy var animalRepository: AnimalRepositoryProtocol = AnimalRepository(
        httpClient: httpClient
    )
    
    lazy var httpClient: HTTPClient = HTTPClientImpl()
    
    // NEW: LocationService
    lazy var locationService: LocationServiceProtocol = LocationService()
}
```

---

### 5. Update AnimalListViewModel

**File**: `/iosApp/iosApp/Features/StartupScreen/AnimalListViewModel.swift`

Add presentation extension and location properties:

```swift
// NEW: Presentation layer extension for UI decisions
// Place this BEFORE the ViewModel class definition
extension LocationPermissionStatus {
    /// Whether to show custom permission popup (presentation logic, not domain logic).
    /// This is UI concern - domain should not know about "popups".
    var shouldShowCustomPopup: Bool {
        self == .denied || self == .restricted
    }
}

@MainActor
class AnimalListViewModel: ObservableObject {
    // Existing properties
    @Published var animals: [Animal] = []
    @Published var isLoading = false
    @Published var errorMessage: String?
    
    // NEW: Location properties
    @Published var locationPermissionStatus: LocationPermissionStatus = .notDetermined
    @Published var showPermissionDeniedAlert = false  // Controls CUSTOM alert (NOT iOS system alert)
    @Published var currentLocation: UserLocation?
    
    private let animalRepository: AnimalRepositoryProtocol
    private let locationService: LocationServiceProtocol // NEW
    private var hasShownPermissionAlert = false // NEW
    
    // NEW: Coordinator callback for Settings navigation
    var onOpenAppSettings: (() -> Void)?
    
    // Updated initializer
    init(
        animalRepository: AnimalRepositoryProtocol,
        locationService: LocationServiceProtocol // NEW parameter
    ) {
        self.animalRepository = animalRepository
        self.locationService = locationService
    }
    
    // NEW: Load animals with location support
    func loadAnimals() async {
        isLoading = true
        defer { isLoading = false }
        
        do {
            // Check permission status
            let status = await locationService.authorizationStatus
            locationPermissionStatus = status
            
            // Request permission if not determined
            if status == .notDetermined {
                // ⚠️ iOS AUTOMATICALLY shows SYSTEM ALERT (3 buttons: Allow While/Once/Don't)
                let newStatus = await locationService.requestWhenInUseAuthorization()
                locationPermissionStatus = newStatus
            }
            
            // Show CUSTOM popup for denied/restricted (once per session)
            // ⚠️ This is OUR SwiftUI alert (2 buttons: Settings/Cancel), NOT iOS system alert
            // NOTE: Alert is NON-BLOCKING - query continues immediately (FR-007, SC-001)
            if status.shouldShowCustomPopup && !hasShownPermissionAlert {
                showPermissionDeniedAlert = true
                hasShownPermissionAlert = true
            }
            
            // Fetch location if authorized
            if locationPermissionStatus.isAuthorized {
                currentLocation = await locationService.requestLocation()
            }
            
            // Query animals with optional location (non-blocking - runs immediately)
            self.animals = try await animalRepository.fetchAnimals(near: currentLocation)
            
        } catch {
            errorMessage = error.localizedDescription
        }
    }
    
    // NEW: Check permission status when app returns to foreground (FR-010, FR-011)
    func checkPermissionStatusChange() async {
        let newStatus = await locationService.authorizationStatus
        
        // If permission changed from denied/notDetermined → authorized, refresh
        if newStatus.isAuthorized && !locationPermissionStatus.isAuthorized {
            locationPermissionStatus = newStatus
            await loadAnimals() // Refresh with location (FR-011)
        } else {
            locationPermissionStatus = newStatus
        }
    }
    
    // NEW: Handle Settings navigation (delegates to coordinator)
    func openSettings() {
        onOpenAppSettings?()
    }
    
    // NEW: Handle popup cancellation
    func continueWithoutLocation() {
        // User cancelled popup, continue query without location
        Task {
            do {
                self.animals = try await animalRepository.fetchAnimals(near: nil)
            } catch {
                errorMessage = error.localizedDescription
            }
        }
    }
}
```

---

### 6. Update AnimalListView (Existing View)

**File**: `/iosApp/iosApp/Features/StartupScreen/AnimalListView.swift`

Add scenePhase observation and permission popup alert to existing view:

```swift
struct AnimalListView: View {
    @ObservedObject var viewModel: AnimalListViewModel
    @Environment(\.scenePhase) private var scenePhase  // NEW: Detect app foreground
    
    var body: some View {
        ZStack {
            // EXISTING animal list content (keep as-is)
            // ... your existing UI code ...
        }
        // NEW: Observe app returning from Settings (FR-010, FR-011)
        .onChange(of: scenePhase) { oldPhase, newPhase in
            if oldPhase == .background && newPhase == .active {
                // App returned from background (user may have changed permissions in Settings)
                Task {
                    await viewModel.checkPermissionStatusChange()
                }
            }
        }
        // NEW: Permission popup alert
        .alert(
            L10n.Location.Permission.Popup.title,
            isPresented: $viewModel.showPermissionDeniedAlert,
            actions: {
                Button(L10n.Location.Permission.Popup.Settings.button) {
                    viewModel.openSettings()  // ✅ Delegates to ViewModel → Coordinator
                }
                .accessibilityIdentifier("startup.permissionPopup.goToSettings")
                
                Button(L10n.Location.Permission.Popup.Cancel.button, role: .cancel) {
                    viewModel.continueWithoutLocation()
                }
                .accessibilityIdentifier("startup.permissionPopup.cancel")
            },
            message: {
                Text(L10n.Location.Permission.Popup.message)
                    .accessibilityIdentifier("startup.permissionPopup.message")
            }
        )
    }
}
```

**Note**: Don't modify existing UI structure - just add `.onChange(of: scenePhase)` and `.alert()` modifiers to your existing ZStack/VStack.

**⚠️ Important: Two Different Alerts**

This code creates a **CUSTOM SwiftUI alert** that YOU control:
- Shows when: status = `.denied` or `.restricted`
- Controlled by: `showPermissionDeniedAlert` property
- Buttons: "Go to Settings" / "Cancel"

This is **DIFFERENT** from the **iOS system alert** that iOS shows automatically:
- Shows when: status = `.notDetermined`
- Triggered by: `requestWhenInUseAuthorization()` call
- Buttons: "Allow While Using" / "Allow Once" / "Don't Allow"
- You have ZERO control over iOS system alert (it's automatic)

**Why we need both:**
- iOS system alert: First-time permission request (managed by iOS)
- Custom alert: Recovery path for users who denied (managed by us)

---

### 7. Update StartupCoordinator

**File**: `/iosApp/iosApp/Coordinators/StartupCoordinator.swift`

Inject LocationService into ViewModel:

```swift
class StartupCoordinator {
    private let navigationController: UINavigationController
    private let animalRepository: AnimalRepositoryProtocol
    private let locationService: LocationServiceProtocol // NEW
    
    init(
        navigationController: UINavigationController,
        animalRepository: AnimalRepositoryProtocol,
        locationService: LocationServiceProtocol // NEW parameter
    ) {
        self.navigationController = navigationController
        self.animalRepository = animalRepository
        self.locationService = locationService
    }
    
    func start() {
        // Inject both repositories into ViewModel
        let viewModel = AnimalListViewModel(
            animalRepository: animalRepository,
            locationService: locationService // NEW injection
        )
        
        // NEW: Set coordinator callback for Settings navigation
        viewModel.onOpenAppSettings = { [weak self] in
            self?.openAppSettings()
        }
        
        // Use existing AnimalListView (no new view creation needed)
        let view = AnimalListView(viewModel: viewModel)
        let hostingController = UIHostingController(
            rootView: NavigationBackHiding { view }
        )
        navigationController.pushViewController(hostingController, animated: true)
    }
    
    // NEW: Handle Settings navigation (MVVM-C pattern)
    private func openAppSettings() {
        guard let settingsUrl = URL(string: UIApplication.openSettingsURLString) else {
            return
        }
        UIApplication.shared.open(settingsUrl)
    }
}
```

---

### 8. Update AnimalRepository

**File**: `/iosApp/iosApp/Data/AnimalRepository.swift`

Add optional location parameter to fetchAnimals:

```swift
class AnimalRepository: AnimalRepositoryProtocol {
    private let httpClient: HTTPClient
    
    init(httpClient: HTTPClient) {
        self.httpClient = httpClient
    }
    
    func fetchAnimals(near location: UserLocation?) async throws -> [Animal] {
        var queryItems: [URLQueryItem] = []
        
        // Add location query parameters if provided
        if let location = location {
            queryItems.append(URLQueryItem(name: "lat", value: "\(location.latitude)"))
            queryItems.append(URLQueryItem(name: "lon", value: "\(location.longitude)"))
        }
        
        var urlComponents = URLComponents(string: "\(httpClient.baseURL)/api/pets")
        urlComponents?.queryItems = queryItems.isEmpty ? nil : queryItems
        
        guard let url = urlComponents?.url else {
            throw NetworkError.invalidURL
        }
        
        return try await httpClient.get(url)
    }
}
```

---

## Verification Checklist

After completing setup, verify:

- [ ] **Info.plist**: Contains `NSLocationWhenInUseUsageDescription` key
- [ ] **SwiftGen**: L10n keys accessible (e.g., `L10n.Location.Permission.Popup.title`)
- [ ] **LocationService**: Builds without errors, registered in ServiceContainer
- [ ] **AnimalListViewModel**: Compiles with new location properties
- [ ] **AnimalListView**: Alert and scenePhase modifiers added without errors
- [ ] **Xcode Build**: Project builds successfully (`Cmd+B`)
- [ ] **Simulator Run**: App launches without crashes

---

## Testing Setup

Run unit tests to verify integration:

```bash
xcodebuild test \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 15' \
  -enableCodeCoverage YES
```

Check coverage report in Xcode:
1. Open Xcode
2. Product → Test (Cmd+U)
3. View → Navigators → Show Report Navigator
4. Select latest test run → Coverage tab

Target: 80%+ coverage for LocationService and AnimalListViewModel location logic.

---

## Troubleshooting

### Issue: App crashes on permission request
**Solution**: Verify `NSLocationWhenInUseUsageDescription` key exists in Info.plist

### Issue: SwiftGen L10n keys not found
**Solution**: Run `swiftgen` from iosApp directory, rebuild project

### Issue: Location always returns nil
**Solution**: Check simulator location settings (Debug → Location → Custom Location)

### Issue: iOS system alert not showing (status = .notDetermined)
**Cause**: Simulator/device already has permission decision recorded
**Solution**: Reset simulator permissions (Device → Erase All Content and Settings)

### Issue: Custom alert not showing (status = .denied)
**Cause 1**: Alert already shown once this session (`hasShownPermissionAlert = true`)
**Solution**: Restart app to reset session flag
**Cause 2**: Status is not actually `.denied` or `.restricted`
**Solution**: Check `locationPermissionStatus` value in debugger

### Issue: System alert appears on every app launch
**Cause**: User tapped "Allow Once" (iOS 14+) - permission expires after session
**Expected Behavior**: Alert reappears because status reverts to `.notDetermined`
**Solution**: This is correct iOS behavior - user can choose "Allow While Using App" for permanent access

---

## Next Steps

After setup completion:
1. Run unit tests (`LocationServiceTests`, `AnimalListViewModelLocationTests`)
2. Manual testing on simulator with different permission states
3. E2E tests with Appium (iOS location permission scenarios)
4. Code review and PR submission

See `tasks.md` for detailed implementation tasks (generated by `/speckit.tasks` command).

