# iOS App - MVVM-C Architecture

## Overview

The iOS app uses **MVVM-C (Model-View-ViewModel-Coordinator)** architecture, combining UIKit-based navigation coordinators with SwiftUI views.

## Architecture

### Pattern: MVVM-C

- **Model**: Domain models from shared Kotlin Multiplatform module
- **View**: SwiftUI views for declarative UI
- **ViewModel**: Swift ObservableObjects managing presentation logic
- **Coordinator**: UIKit coordinators managing navigation flows

### Coordinator Hierarchy

```
AppCoordinator (root)
└── ListScreenCoordinator
    └── UIHostingController(ContentView)
```

**Diagram**:

```
┌─────────────────────────────────────┐
│  SceneDelegate                      │
│  - Manages UIWindow                 │
│  - Initializes AppCoordinator       │
└──────────┬──────────────────────────┘
           │
           ▼
┌─────────────────────────────────────┐
│  AppCoordinator                     │
│  - Root navigation coordinator      │
│  - Manages sub-coordinators         │
└──────────┬──────────────────────────┘
           │
           ▼
┌─────────────────────────────────────┐
│  ListScreenCoordinator              │
│  - Manages list screen presentation │
│  - Wraps SwiftUI in UIHostingController
└──────────┬──────────────────────────┘
           │
           ▼
┌─────────────────────────────────────┐
│  ContentView (SwiftUI)              │
│  - Declarative UI                   │
│  - Observes ViewModel state         │
└─────────────────────────────────────┘
```

## Project Structure

```
iosApp/
├── iosApp/
│   ├── AppDelegate.swift              # UIKit app lifecycle (@main)
│   ├── SceneDelegate.swift            # Scene lifecycle + window setup
│   ├── Coordinators/
│   │   ├── CoordinatorInterface.swift # Protocol for all coordinators
│   │   ├── AppCoordinator.swift       # Root app coordinator
│   │   └── ListScreenCoordinator.swift # List screen coordinator
│   ├── Views/
│   │   ├── SplashScreenView.swift     # Initial splash screen
│   │   └── ContentView.swift          # Main list view
│   ├── Assets.xcassets/
│   └── Info.plist                     # UIApplicationSceneManifest config
├── iosAppTests/                       # Unit tests (XCTest)
└── iosApp.xcodeproj/                  # Xcode project
```

## Coordinator Pattern Usage

### CoordinatorInterface Protocol

All coordinators conform to `CoordinatorInterface`:

```swift
protocol CoordinatorInterface: AnyObject {
    var navigationController: UINavigationController? { get set }
    func start(animated: Bool) async
}
```

### Key Principles

1. **Weak Navigation Controller**: All coordinators store `weak var navigationController` to prevent retain cycles
2. **Lazy Sub-Coordinators**: Sub-coordinators are lazy vars (no children array for non-tab coordinators)
3. **Async Start**: Coordinators use async/await pattern for modern Swift Concurrency
4. **UIHostingController**: SwiftUI views are wrapped in UIHostingController for UIKit navigation

### Creating a New Coordinator

```swift
import UIKit
import SwiftUI

class MyScreenCoordinator: CoordinatorInterface {
    weak var navigationController: UINavigationController?
    
    func start(animated: Bool) async {
        guard let navigationController = navigationController else {
            assertionFailure("NavigationController is nil")
            return
        }
        
        let view = MyView()
        let hostingController = UIHostingController(rootView: view)
        navigationController.pushViewController(hostingController, animated: animated)
    }
}
```

### Adding to Parent Coordinator

```swift
class ParentCoordinator: CoordinatorInterface {
    weak var navigationController: UINavigationController?
    
    private lazy var myScreenCoordinator: MyScreenCoordinator = {
        let coordinator = MyScreenCoordinator()
        coordinator.navigationController = self.navigationController
        return coordinator
    }()
    
    func showMyScreen() async {
        await myScreenCoordinator.start(animated: true)
    }
}
```

## App Lifecycle

### UIKit Lifecycle

The app uses UIKit lifecycle (iOS 13+ scene-based):

- **AppDelegate**: Application-level lifecycle, global setup
- **SceneDelegate**: Scene-level lifecycle, window configuration

### Navigation Bar Configuration

Transparent green semi-transparent navigation bar configured globally in SceneDelegate:

```swift
private func configureNavigationBarAppearance() {
    let appearance = UINavigationBarAppearance()
    appearance.configureWithTransparentBackground()
    appearance.backgroundColor = UIColor.green.withAlphaComponent(0.5)
    
    UINavigationBar.appearance().standardAppearance = appearance
    UINavigationBar.appearance().compactAppearance = appearance
    UINavigationBar.appearance().scrollEdgeAppearance = appearance
}
```

## Memory Management

### Preventing Retain Cycles

- All coordinator `navigationController` properties are **weak**
- Sub-coordinators use **lazy var** initialization
- No children array maintained (except for tab bar coordinators)

### Verification

Run Instruments → Leaks to verify no retain cycles:

```bash
# Build and profile
xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' 
# Then run Instruments → Leaks
```

## Testing

### Unit Tests

Location: `/iosApp/iosAppTests/`

Run tests:

```bash
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15'
```

### E2E Tests

Location: `/e2e-tests/mobile/specs/`

Run mobile E2E tests:

```bash
npm run test:mobile:ios  # from repo root
```

## Build & Run

### Requirements

- Xcode 15+
- iOS 18.2+ deployment target
- macOS with Apple Silicon or Intel

### Build

```bash
cd iosApp
xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' build
```

### Run

Open `iosApp.xcodeproj` in Xcode and press `Cmd+R`

## References

- [Specification](../specs/001-ios-mvvmc-architecture/spec.md)
- [Implementation Plan](../specs/001-ios-mvvmc-architecture/plan.md)
- [Quickstart Guide](../specs/001-ios-mvvmc-architecture/quickstart.md)
- [Research Findings](../specs/001-ios-mvvmc-architecture/research.md)

