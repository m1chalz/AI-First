# Quickstart: iOS Fullscreen Interactive Map with Legend

**Feature Branch**: `KAN-32-ios-display-fullscreen-map`  
**Date**: 2025-01-07

## Prerequisites

- Xcode with iOS 18+ SDK
- iPhone 16 Simulator (for testing)
- Previous spec implemented: `KAN-32-ios-navigation-to-fullscreen-map`

## Files to Modify/Create

### 1. Modify: MapSectionHeaderView_Model (make title optional)

**File**: `iosApp/iosApp/Features/LandingPage/Views/Components/MapSectionHeaderView_Model.swift`

```swift
extension MapSectionHeaderView {
    struct Model {
        /// Section title text (nil = legend only, no title)
        let title: String?  // CHANGED: String → String?
        
        let legendItems: [LegendItem]
        
        let titleAccessibilityId: String?  // CHANGED: also optional
        let legendAccessibilityIdPrefix: String
        
        init(
            title: String?,
            legendItems: [LegendItem],
            titleAccessibilityId: String?,
            legendAccessibilityIdPrefix: String
        ) {
            self.title = title
            self.legendItems = legendItems
            self.titleAccessibilityId = titleAccessibilityId
            self.legendAccessibilityIdPrefix = legendAccessibilityIdPrefix
        }
    }
}
```

### 2. Modify: MapSectionHeaderView (handle optional title)

**File**: `iosApp/iosApp/Features/LandingPage/Views/Components/MapSectionHeaderView.swift`

```swift
var body: some View {
    VStack(alignment: .leading, spacing: 4) {
        // Title - only show if provided
        if let title = model.title {
            Text(title)
                .font(.system(size: 18, weight: .semibold))
                .foregroundColor(Color(hex: "#101828"))
                .accessibilityIdentifier(model.titleAccessibilityId ?? "")
        }
        
        // Legend items
        HStack(spacing: 16) {
            ForEach(model.legendItems) { item in
                legendItemView(item)
            }
        }
    }
    // ... rest unchanged
}
```

### 3. Create: Factory for Fullscreen Map

**File**: `iosApp/iosApp/Features/LandingPage/Views/Components/MapSectionHeaderView_Model+FullscreenMap.swift`

```swift
import Foundation

extension MapSectionHeaderView.Model {
    /// Creates legend-only model for fullscreen map (no title).
    /// Title provided by navigation bar ("Pet Locations").
    static func fullscreenMap() -> MapSectionHeaderView.Model {
        MapSectionHeaderView.Model(
            title: nil,
            legendItems: [
                MapSectionHeaderView.LegendItem(
                    id: "missing",
                    colorHex: "#FF0000",
                    label: L10n.MapSection.Legend.missing
                ),
                MapSectionHeaderView.LegendItem(
                    id: "found",
                    colorHex: "#0074FF",
                    label: L10n.MapSection.Legend.found
                )
            ],
            titleAccessibilityId: nil,
            legendAccessibilityIdPrefix: "fullscreenMap.legend"
        )
    }
}
```

### 4. Modify: FullscreenMapViewModel

**File**: `iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift`

```swift
import Foundation
import MapKit

/// ViewModel for fullscreen map screen.
/// Receives user location via constructor (already available from landing page).
@MainActor
class FullscreenMapViewModel: ObservableObject {
    /// Map region centered on user's location
    let mapRegion: MKCoordinateRegion
    
    /// Legend configuration (reuses existing component)
    let legendModel = MapSectionHeaderView.Model.fullscreenMap()
    
    /// Creates ViewModel with pre-fetched user location.
    /// - Parameter userLocation: User's current location (from landing page)
    init(userLocation: Coordinate) {
        self.mapRegion = userLocation.mapRegion()
    }
}
```

**Note**: No async loading needed - location is passed from landing page where it was already fetched.

### 5. Modify: FullscreenMapView

**File**: `iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapView.swift`

```swift
import SwiftUI
import MapKit

struct FullscreenMapView: View {
    @ObservedObject var viewModel: FullscreenMapViewModel
    
    var body: some View {
        VStack(spacing: 0) {
            // Legend header above map (reuse existing component)
            MapSectionHeaderView(model: viewModel.legendModel)
            
            // Interactive map
            Map(initialPosition: .region(viewModel.mapRegion))
                .accessibilityIdentifier("fullscreenMap.map")
        }
        .accessibilityIdentifier("fullscreenMap.container")
    }
}
```

**Note**: No `.task` or loading state - map renders immediately with pre-fetched location.

### 6. Modify: LandingPageViewModel

**File**: `iosApp/iosApp/Features/LandingPage/Views/LandingPageViewModel.swift`

Change `onShowFullscreenMap` signature to pass location:

```swift
// CHANGE: () -> Void  →  (Coordinate) -> Void
var onShowFullscreenMap: ((Coordinate) -> Void)?

// Update handleMapTap() to pass current location
private func handleMapTap() {
    guard let location = currentLocation else { return }
    onShowFullscreenMap?(location)
}
```

### 7. Modify: HomeCoordinator

**File**: `iosApp/iosApp/Features/LandingPage/Coordinators/HomeCoordinator.swift`

Update callback to receive location and pass to ViewModel:

```swift
// In start() - callback now receives location directly
viewModel.onShowFullscreenMap = { [weak self] location in
    self?.showFullscreenMap(userLocation: location)
}

// Update method signature
private func showFullscreenMap(userLocation: Coordinate) {
    guard let navigationController else { return }
    
    let viewModel = FullscreenMapViewModel(userLocation: userLocation)
    let view = NavigationBackHiding {
        FullscreenMapView(viewModel: viewModel)
    }
    let hostingController = UIHostingController(rootView: view)
    
    hostingController.title = L10n.FullscreenMap.navigationTitle
    // ... rest unchanged (back button setup)
}
```

### 8. Create: Unit Tests

**File**: `iosApp/iosAppTests/Features/LandingPage/FullscreenMap/FullscreenMapViewModelTests.swift`

```swift
import XCTest
import MapKit
@testable import PetSpot

final class FullscreenMapViewModelTests: XCTestCase {
    
    func testInit_shouldSetMapRegionFromUserLocation() {
        // Given
        let userLocation = Coordinate(latitude: 52.0, longitude: 21.0)
        
        // When
        let sut = FullscreenMapViewModel(userLocation: userLocation)
        
        // Then
        XCTAssertEqual(sut.mapRegion.center.latitude, 52.0, accuracy: 0.001)
        XCTAssertEqual(sut.mapRegion.center.longitude, 21.0, accuracy: 0.001)
    }
    
    func testInit_shouldUseCityLevelZoom() {
        // Given
        let userLocation = Coordinate(latitude: 52.0, longitude: 21.0)
        
        // When
        let sut = FullscreenMapViewModel(userLocation: userLocation)
        
        // Then - ~20km span (10km radius * 2)
        XCTAssertEqual(sut.mapRegion.span.latitudeDelta, 0.18, accuracy: 0.05)
    }
    
    func testLegendModel_shouldHaveMissingAndFoundItems() {
        // Given
        let userLocation = Coordinate(latitude: 52.0, longitude: 21.0)
        
        // When
        let sut = FullscreenMapViewModel(userLocation: userLocation)
        
        // Then
        XCTAssertEqual(sut.legendModel.legendItems.count, 2)
        XCTAssertEqual(sut.legendModel.legendItems[0].id, "missing")
        XCTAssertEqual(sut.legendModel.legendItems[1].id, "found")
    }
}
```

**Note**: Tests are simpler now - just verify constructor correctly initializes state from input.

## Build & Test Commands

```bash
# Build
open iosApp/iosApp.xcodeproj
# Press Cmd+B in Xcode

# Run tests
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES

# View coverage
# Open Xcode → Product → Show Coverage Report
```

## Verification Checklist

- [ ] Map displays when opening fullscreen map view
- [ ] Map is centered on user's location (or Warsaw if denied)
- [ ] Pinch-to-zoom works (zoom in/out)
- [ ] Pan gesture works (drag to move map)
- [ ] Double-tap zooms in on location
- [ ] Legend is visible at bottom of screen
- [ ] Legend shows "Missing" and "Found" with colored indicators
- [ ] Legend has semi-transparent background
- [ ] Back button returns to landing page
- [ ] Unit tests pass with 80%+ coverage

