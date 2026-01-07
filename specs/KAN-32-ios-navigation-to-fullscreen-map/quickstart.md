# Quickstart: iOS Navigation to Fullscreen Map View

**Feature**: KAN-32 | **Date**: 2025-01-07

## Prerequisites

- Xcode 15+ with iOS 18 SDK
- iPhone 16 Simulator (or physical device)
- SwiftGen installed (`brew install swiftgen`)

## Implementation Order

### Step 1: Add Localization Strings

Add to `iosApp/iosApp/Resources/en.lproj/Localizable.strings`:
```
/* MARK: - Fullscreen Map */
"fullscreenMap.navigationTitle" = "Pet Locations";
```

Add to `iosApp/iosApp/Resources/pl.lproj/Localizable.strings`:
```
/* MARK: - Fullscreen Map */
"fullscreenMap.navigationTitle" = "Lokalizacje zwierząt";
```

Run SwiftGen:
```bash
cd iosApp && swiftgen
```

### Step 2: Create FullscreenMapViewModel

Create `iosApp/iosApp/Features/FullscreenMap/Views/FullscreenMapViewModel.swift`:
```swift
import Foundation

/// Minimal ViewModel for fullscreen map screen (MVVM-C compliance).
/// Currently empty placeholder - will be extended with map state in future tickets.
@MainActor
class FullscreenMapViewModel: ObservableObject {
    // Placeholder for future state:
    // - Map annotations
    // - Selected annotation
    // - Loading state
}
```

### Step 3: Create FullscreenMapView

Create `iosApp/iosApp/Features/FullscreenMap/Views/FullscreenMapView.swift`:
```swift
import SwiftUI

/// Fullscreen map placeholder view.
/// Currently displays empty content - MapKit integration in future ticket.
struct FullscreenMapView: View {
    @ObservedObject var viewModel: FullscreenMapViewModel
    
    var body: some View {
        Color(.systemBackground)
            .accessibilityIdentifier("fullscreenMap.container")
    }
}
```

### Step 4: Modify LandingPageViewModel

Add to `LandingPageViewModel.swift`:
```swift
// Add closure property
var onShowFullscreenMap: (() -> Void)?

// Update handleMapTap()
private func handleMapTap() {
    onShowFullscreenMap?()
}
```

> **Note**: Rapid-tap prevention handled automatically by UIKit's navigation controller (blocks interaction during animations).

### Step 5: Modify HomeCoordinator

Add to `HomeCoordinator.swift`:
```swift
// In start() method, after creating viewModel:
viewModel.onShowFullscreenMap = { [weak self] in
    self?.showFullscreenMap()
}

// Add new method:
private func showFullscreenMap() {
    guard let navigationController else { return }
    
    let viewModel = FullscreenMapViewModel()
    let view = FullscreenMapView(viewModel: viewModel)
    let hostingController = UIHostingController(rootView: view)
    
    hostingController.title = L10n.FullscreenMap.navigationTitle
    hostingController.navigationItem.largeTitleDisplayMode = .never
    
    navigationController.pushViewController(hostingController, animated: true)
}
```

### Step 6: Add Unit Tests

Create `iosApp/iosAppTests/Features/FullscreenMap/FullscreenMapViewModelTests.swift`:
```swift
import XCTest
@testable import PetSpot

final class FullscreenMapViewModelTests: XCTestCase {
    
    func testInit_shouldCreateViewModel() {
        // Given/When
        let viewModel = FullscreenMapViewModel()
        
        // Then
        XCTAssertNotNil(viewModel)
    }
}
```

Add test to `LandingPageViewModelTests.swift`:
```swift
func testHandleMapTap_shouldCallCallback() {
    // Given
    var callbackCalled = false
    viewModel.onShowFullscreenMap = { callbackCalled = true }
    
    // When
    viewModel.handleMapTap()
    
    // Then
    XCTAssertTrue(callbackCalled)
}
```

## Verification

### Build
```bash
# Open in Xcode and build
open iosApp/iosApp.xcodeproj
# Cmd+B to build
```

### Run Tests
```bash
xcodebuild test \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 16' \
  -enableCodeCoverage YES
```

### Manual Testing

1. Launch app in simulator
2. Navigate to Home tab (landing page)
3. Tap map preview area
4. Verify: Fullscreen map view opens with "Pet Locations" title
5. Verify: Back button appears in navigation bar
6. Tap back button
7. Verify: Returns to landing page
8. Tap map preview again
9. Perform edge swipe from left
10. Verify: Returns to landing page


## File Checklist

| File | Action | Status |
|------|--------|--------|
| `Resources/en.lproj/Localizable.strings` | Add key | ☐ |
| `Resources/pl.lproj/Localizable.strings` | Add key | ☐ |
| `Generated/Strings.swift` | Regenerate | ☐ |
| `Features/FullscreenMap/Views/FullscreenMapViewModel.swift` | Create | ☐ |
| `Features/FullscreenMap/Views/FullscreenMapView.swift` | Create | ☐ |
| `Features/LandingPage/Views/LandingPageViewModel.swift` | Modify | ☐ |
| `Features/LandingPage/Coordinators/HomeCoordinator.swift` | Modify | ☐ |
| `iosAppTests/Features/FullscreenMap/FullscreenMapViewModelTests.swift` | Create | ☐ |

