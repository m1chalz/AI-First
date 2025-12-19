# Quickstart: iOS Map Preview Component

**Feature**: KAN-30 - Embed Map in Landing Page
**Date**: 2025-12-19

## Design Philosophy

**Simple SwiftUI Map** - no snapshots, no async complexity. Map renders synchronously with disabled interactions. All logic stays in LandingPageViewModel.

## File Structure After Implementation

```
iosApp/iosApp/Features/LandingPage/Views/
├── LandingPageView.swift              # MODIFIED
├── LandingPageViewModel.swift         # MODIFIED
└── Components/
    ├── MapPreviewView.swift           # NEW
    └── MapPreviewView_Model.swift     # NEW

iosApp/iosApp/Domain/Models/
└── Coordinate.swift                   # MODIFIED (add mapRegion helper)

iosApp/iosApp/Resources/
├── en.lproj/Localizable.strings       # MODIFIED
└── pl.lproj/Localizable.strings       # MODIFIED

iosAppTests/Features/LandingPage/
└── LandingPageViewModelTests.swift    # MODIFIED
```

## Implementation Steps

### Step 1: Add Localization Strings

**en.lproj/Localizable.strings**:
```text
// MARK: - Map Preview
"mapPreview.permission.message" = "Enable location to see nearby area.";
"mapPreview.permission.settingsButton" = "Go to Settings";
```

**pl.lproj/Localizable.strings**:
```text
// MARK: - Map Preview
"mapPreview.permission.message" = "Włącz lokalizację, aby zobaczyć okolicę.";
"mapPreview.permission.settingsButton" = "Przejdź do Ustawień";
```

Run: `cd iosApp && swiftgen`

### Step 2: Add Coordinate Extension

**Add to** `iosApp/iosApp/Domain/Models/Coordinate.swift`:

```swift
import MapKit

extension Coordinate {
    /// Creates map region with specified radius centered on this coordinate.
    func mapRegion(radiusMeters: Double = 10_000) -> MKCoordinateRegion {
        MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: latitude, longitude: longitude),
            latitudinalMeters: radiusMeters * 2,
            longitudinalMeters: radiusMeters * 2
        )
    }
}
```

### Step 3: Create MapPreviewView Model

**Location**: `iosApp/iosApp/Features/LandingPage/Views/Components/MapPreviewView_Model.swift`

```swift
import MapKit

extension MapPreviewView {
    enum Model: Equatable {
        case loading
        case map(region: MKCoordinateRegion, onTap: () -> Void)
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

### Step 4: Create MapPreviewView

**Location**: `iosApp/iosApp/Features/LandingPage/Views/Components/MapPreviewView.swift`

```swift
import SwiftUI
import MapKit

struct MapPreviewView: View {
    let model: Model
    
    var body: some View {
        contentView
            .frame(maxWidth: .infinity)
            .background(Color(.systemGray6))
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .accessibilityIdentifier("landingPage.mapPreview")
    }
    
    @ViewBuilder
    private var contentView: some View {
        switch model {
        case .loading:
            loadingView
            
        case .map(let region, let onTap):
            mapView(region: region, onTap: onTap)
            
        case .permissionRequired(let message, let onGoToSettings):
            permissionView(message: message, onGoToSettings: onGoToSettings)
        }
    }
    
    // MARK: - State Views
    
    private var loadingView: some View {
        ProgressView()
            .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
    
    private func mapView(region: MKCoordinateRegion, onTap: @escaping () -> Void) -> some View {
        Map(coordinateRegion: .constant(region), interactionModes: [])
            .disabled(true)
            .allowsHitTesting(false)
            .overlay {
                Color.clear
                    .contentShape(Rectangle())
                    .onTapGesture { onTap() }
            }
    }
    
    private func permissionView(message: String, onGoToSettings: @escaping () -> Void) -> some View {
        VStack(spacing: 12) {
            Image(systemName: "location.slash.fill")
                .font(.system(size: 32))
                .foregroundColor(.secondary)
            
            Text(message)
                .font(.system(size: 14))
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
            
            Button(L10n.MapPreview.Permission.settingsButton) {
                onGoToSettings()
            }
            .font(.system(size: 16, weight: .medium))
            .accessibilityIdentifier("landingPage.mapPreview.settings")
        }
        .padding()
    }
}
```

### Step 5: Modify LandingPageViewModel

**Add property**:

```swift
/// Model for MapPreviewView
@Published var mapPreviewModel: MapPreviewView.Model = .loading
```

**Modify `loadData()`**:

```swift
func loadData() async {
    let result = await locationHandler.requestLocationWithPermissions()
    
    locationPermissionStatus = result.status
    currentLocation = result.location
    
    // Show custom popup for denied/restricted (once per session)
    if result.status.shouldShowCustomPopup && !hasShownPermissionAlert {
        showPermissionDeniedAlert = true
        hasShownPermissionAlert = true
    }
    
    // Set query on child ViewModel (triggers automatic reload)
    let queryWithLocation = AnnouncementListQuery.landingPageQuery(location: result.location)
    listViewModel.query = queryWithLocation
    
    // Update map preview model
    updateMapPreviewModel(location: result.location)
}

private func updateMapPreviewModel(location: Coordinate?) {
    guard let location else {
        mapPreviewModel = .permissionRequired(
            message: L10n.MapPreview.Permission.message,
            onGoToSettings: { [weak self] in self?.openSettings() }
        )
        return
    }
    
    mapPreviewModel = .map(
        region: location.mapRegion(),
        onTap: { [weak self] in self?.handleMapTap() }
    )
}

private func handleMapTap() {
    print("[LandingPage] Map preview tapped")
    // Future: fullscreen map navigation
}
```

### Step 6: Modify LandingPageView

**Add MapPreviewView between HeroPanelView and ListHeaderRowView**:

```swift
ScrollView {
    LazyVStack(spacing: 0) {
        // Hero panel
        HeroPanelView(
            model: .landingPage(
                onLostPetTap: { viewModel.onSwitchToLostPetTab?(nil) },
                onFoundPetTap: { viewModel.onSwitchToFoundPetTab?() }
            )
        )
        
        // NEW: Map preview
        MapPreviewView(model: viewModel.mapPreviewModel)
            .aspectRatio(16/9, contentMode: .fit)
            .padding(.horizontal, 16)
            .padding(.vertical, 16)
        
        // List header row
        ListHeaderRowView(
            model: .recentReports(
                onViewAllTap: { viewModel.onSwitchToLostPetTab?(nil) }
            )
        )
        
        // Announcement cards
        AnnouncementCardsListView(...)
    }
}
.task {
    await viewModel.loadData()
}
```

### Step 7: Add Unit Tests

**Add to** `LandingPageViewModelTests.swift`:

```swift
// MARK: - Map Preview Tests

func testLoadData_whenLocationAuthorized_shouldSetMapModel() async {
    // Given
    let fakeLocationService = FakeLocationService(
        status: .authorizedWhenInUse,
        location: Coordinate(latitude: 52.23, longitude: 21.01)
    )
    let handler = LocationPermissionHandler(
        locationService: fakeLocationService,
        notificationCenter: NotificationCenter()
    )
    let viewModel = LandingPageViewModel(
        repository: FakeAnnouncementRepository(),
        locationHandler: handler,
        onAnnouncementTapped: { _ in }
    )
    
    // When
    await viewModel.loadData()
    
    // Then
    if case .map(let region, _) = viewModel.mapPreviewModel {
        XCTAssertEqual(region.center.latitude, 52.23, accuracy: 0.01)
        XCTAssertEqual(region.center.longitude, 21.01, accuracy: 0.01)
    } else {
        XCTFail("Expected .map state")
    }
}

func testLoadData_whenLocationDenied_shouldSetPermissionRequired() async {
    // Given
    let fakeLocationService = FakeLocationService(status: .denied)
    let handler = LocationPermissionHandler(
        locationService: fakeLocationService,
        notificationCenter: NotificationCenter()
    )
    let viewModel = LandingPageViewModel(
        repository: FakeAnnouncementRepository(),
        locationHandler: handler,
        onAnnouncementTapped: { _ in }
    )
    
    // When
    await viewModel.loadData()
    
    // Then
    if case .permissionRequired = viewModel.mapPreviewModel {
        // Expected
    } else {
        XCTFail("Expected .permissionRequired state")
    }
}

func testMapTap_shouldLogToConsole() async {
    // Given
    let fakeLocationService = FakeLocationService(
        status: .authorizedWhenInUse,
        location: Coordinate(latitude: 52.23, longitude: 21.01)
    )
    let handler = LocationPermissionHandler(
        locationService: fakeLocationService,
        notificationCenter: NotificationCenter()
    )
    let viewModel = LandingPageViewModel(
        repository: FakeAnnouncementRepository(),
        locationHandler: handler,
        onAnnouncementTapped: { _ in }
    )
    await viewModel.loadData()
    
    // When - extract and call onTap
    if case .map(_, let onTap) = viewModel.mapPreviewModel {
        onTap()  // Should print to console
    }
    
    // Then - manual verification via console output
}
```

## Running Tests

```bash
xcodebuild test \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 16' \
  -enableCodeCoverage YES
```

## Verification Checklist

- [ ] Map preview appears between Hero panel and Recent Reports
- [ ] Map shows 16:9 aspect ratio, full width minus padding
- [ ] Map centers on user location with ~10km visible area
- [ ] Map has no zoom/pan interactions (static preview)
- [ ] Tapping map logs to console
- [ ] Permission required state shows "Go to Settings" button
- [ ] Loading state shows during initial load
- [ ] Unit tests pass
- [ ] Accessibility identifiers present
