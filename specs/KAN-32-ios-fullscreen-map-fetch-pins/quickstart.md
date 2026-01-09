# Quickstart: iOS Fullscreen Map - Fetch and Display Pins

**Branch**: `KAN-32-ios-fullscreen-map-fetch-pins` | **Date**: 2025-01-07

## Prerequisites

- Xcode 16+ (for iOS 18 SDK)
- iOS 18+ Simulator (iPhone 16)
- Backend server running at `localhost:3000` (or configured API URL)
- Previous spec implemented: `KAN-32-ios-display-fullscreen-map` (interactive map with legend)

## Project Setup

### 1. Checkout Feature Branch

```bash
git checkout KAN-32-ios-fullscreen-map-fetch-pins
```

### 2. Open Project

```bash
open /Users/msz/dev/ai-first/AI-First/iosApp/iosApp.xcodeproj
```

### 3. Verify Target Settings

- **Deployment Target**: iOS 18.0
- **Device**: iPhone 16 (Simulator)
- **Scheme**: iosApp

## Implementation Files

### Files to Modify

| File | Changes |
|------|---------|
| `Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift` | Add repository dependency, pins state, async loading |
| `Features/LandingPage/Views/FullscreenMap/FullscreenMapView.swift` | Add Map annotations, gesture handling |
| `Features/LandingPage/Coordinators/HomeCoordinator.swift` | Inject repository into ViewModel |

### Files to Create

| File | Purpose |
|------|---------|
| `Features/LandingPage/Views/FullscreenMap/MapPin.swift` | Lightweight pin model for map display |
| `Domain/Models/MKCoordinateRegion+Radius.swift` | Extension for radius calculation |
| `iosAppTests/Features/LandingPage/Views/FullscreenMapViewModelTests.swift` | Unit tests (extend existing) |

## Key Implementation Patterns

### ViewModel with Repository Injection

```swift
@MainActor
class FullscreenMapViewModel: ObservableObject {
    @Published private(set) var pins: [MapPin] = []
    @Published private(set) var isLoading = false
    
    let mapRegion: MKCoordinateRegion
    let legendModel: MapSectionHeaderView.Model
    
    private let repository: AnnouncementRepositoryProtocol
    private var fetchTask: Task<Void, Never>?
    
    /// Creates ViewModel with dependencies injected by coordinator.
    /// - Parameters:
    ///   - userLocation: User's current location (from landing page)
    ///   - repository: Repository for fetching announcements (from HomeCoordinator)
    init(userLocation: Coordinate, repository: AnnouncementRepositoryProtocol) {
        self.mapRegion = userLocation.mapRegion()
        self.legendModel = .fullscreenMap()
        self.repository = repository
    }
    
    func loadPins() async {
        await fetchPins(for: mapRegion)
    }
    
    func handleRegionChange(_ region: MKCoordinateRegion) async {
        await fetchPins(for: region)
    }
    
    private func fetchPins(for region: MKCoordinateRegion) async {
        fetchTask?.cancel()
        
        fetchTask = Task {
            isLoading = true
            defer { isLoading = false }
            
            let center = Coordinate(
                latitude: region.center.latitude,
                longitude: region.center.longitude
            )
            
            do {
                let announcements = try await repository.getAnnouncements(
                    near: center,
                    range: region.radiusInKilometers  // API expects kilometers
                )
                
                // Map all announcements to pins (no status filtering)
                let newPins = announcements.map { MapPin(from: $0) }
                
                // Instant update (no animation)
                self.pins = newPins
            } catch {
                // Silent failure - keep existing pins
                print("Pin fetch error: \(error)")
            }
        }
        
        await fetchTask?.value
    }
}
```

### SwiftUI Map with Pins and Gesture Detection

```swift
struct FullscreenMapView: View {
    @ObservedObject var viewModel: FullscreenMapViewModel
    @State private var selectedPin: MapPin?  // For future callout display
    
    var body: some View {
        VStack(spacing: 0) {
            MapSectionHeaderView(model: viewModel.legendModel)
            
            Map(initialPosition: .region(viewModel.mapRegion)) {
                ForEach(viewModel.pins) { pin in
                    Annotation("", coordinate: pin.coordinate, anchor: .bottom) {
                        VStack(spacing: 0) {
                            // Future: Callout bubble when pin selected
                            // if selectedPin == pin {
                            //     PinCalloutView(pin: pin)
                            //         .transition(.scale.combined(with: .opacity))
                            // }
                            
                            // Pin marker - classic map pin
                            Image(systemName: "mappin.circle.fill")
                                .font(.title)
                                .foregroundStyle(.red)
                        }
                        // Future: .onTapGesture { withAnimation { selectedPin = pin } }
                    }
                    .accessibilityIdentifier("fullscreenMap.pin.\(pin.id)")
                }
            }
            .onMapCameraChange(frequency: .onEnd) { context in
                Task {
                    await viewModel.handleRegionChange(context.region)
                }
            }
            .task {
                await viewModel.loadPins()
            }
            .accessibilityIdentifier("fullscreenMap.map")
        }
        .accessibilityIdentifier("fullscreenMap.container")
    }
}
```

**Note**: Uses `Annotation` to enable future callout bubble feature (rich popup with photo, pet details - see Figma node `1192:5893`). Pin marker uses SF Symbol `mappin.circle.fill` for classic pin appearance.

### Coordinator Injection

```swift
// In HomeCoordinator.showFullscreenMap()
// HomeCoordinator already has `repository` as dependency (line 25)
let viewModel = FullscreenMapViewModel(
    userLocation: userLocation,
    repository: repository  // Use coordinator's injected repository
)
```

## Running Tests

### Unit Tests

```bash
xcodebuild test \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 16' \
  -enableCodeCoverage YES
```

### Verify Coverage

- Open Xcode → Product → Show Test Report
- Check coverage for `FullscreenMapViewModel` ≥ 80%

## Test Scenarios

| Test | Description |
|------|-------------|
| `testLoadPins_whenViewAppears_shouldFetchPinsFromRepository` | Verify initial load |
| `testLoadPins_whenRepositoryReturnsData_shouldMapAllToMapPins` | All announcements mapped to pins |
| `testLoadPins_whenRepositoryFails_shouldKeepExistingPins` | Silent error handling |
| `testHandleRegionChange_whenCalled_shouldFetchNewPins` | Gesture handling |
| `testHandleRegionChange_whenCalledRapidly_shouldCancelPreviousTask` | Concurrent request handling |

## Verification Checklist

- [ ] Open fullscreen map → pins appear within 3 seconds
- [ ] Pan map to new area → old pins removed, new pins appear (no animation)
- [ ] Zoom in/out → pins update after gesture ends
- [ ] Disable network → existing pins remain, no error shown
- [ ] Rapid pan gestures → only final position triggers fetch (previous request cancelled)
- [ ] Pins display as classic map pin markers (red `mappin.circle.fill`)

