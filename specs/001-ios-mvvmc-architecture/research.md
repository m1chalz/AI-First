# Research: iOS MVVM-C Architecture

**Feature**: iOS MVVM-C Architecture Setup  
**Branch**: `001-ios-mvvmc-architecture`  
**Date**: 2025-11-17

## Overview

This document consolidates research findings for establishing MVVM-C (Model-View-ViewModel-Coordinator) architecture in the iOS app, combining UIKit-based navigation coordinators with SwiftUI views.

## Research Topics

### 1. MVVM-C Pattern Fundamentals

**Decision**: Adopt coordinator pattern with UIKit UINavigationController for navigation management, separate from ViewModels and Views.

**Rationale**:
- **Separation of Concerns**: Coordinators handle navigation flow, ViewModels handle presentation logic, Views handle UI rendering
- **Testability**: Each layer can be tested independently
- **Reusability**: ViewModels and Views become navigation-agnostic
- **Scalability**: Easy to add new navigation flows without modifying existing screens

**Alternatives Considered**:
- **Pure SwiftUI with NavigationStack**: Rejected because navigation logic would be tightly coupled to views, harder to test, and difficult to coordinate complex flows
- **VIPER**: Rejected as too heavyweight for current project scope (additional Interactor and Presenter layers unnecessary)
- **MVVM without Coordinators**: Rejected because navigation responsibility would leak into ViewModels or Views

**Key Principles**:
- Coordinators own UINavigationController (or child coordinators for nested flows)
- Coordinators create and configure ViewModels
- Coordinators handle navigation events triggered by ViewModels
- Parent coordinators manage child coordinator lifecycle

### 2. UIKit + SwiftUI Integration

**Decision**: Use UIHostingController to wrap SwiftUI views within UIKit navigation hierarchy.

**Rationale**:
- **Best of Both Worlds**: Leverage UIKit's mature navigation APIs (UINavigationController) while keeping modern SwiftUI for views
- **Incremental Migration**: Can gradually refactor existing SwiftUI views without full rewrite
- **Native Performance**: UIHostingController is Apple's official bridge, optimized for performance
- **Flexibility**: Enables complex navigation patterns (modals, custom transitions) not easily achievable in pure SwiftUI

**Alternatives Considered**:
- **Pure UIKit with UIViewController**: Rejected because we lose SwiftUI's declarative UI benefits and would require massive view rewrite
- **Pure SwiftUI with NavigationStack**: Rejected due to limited control over navigation bar customization and complex flows
- **UIViewRepresentable/UIViewControllerRepresentable**: Rejected as unnecessarily complex for coordinator pattern (these are for embedding UIKit in SwiftUI, not the inverse)

**Implementation Pattern**:
```swift
// Coordinator creates UIHostingController with SwiftUI view
func presentListScreen() {
    let viewModel = ListViewModel()
    let view = ListView(viewModel: viewModel)
    let hostingController = UIHostingController(rootView: view)
    navigationController?.pushViewController(hostingController, animated: true)
}
```

**Navigation Bar Configuration**:
- Configure UINavigationBarAppearance in SceneDelegate for global styling
- Use `.navigationBarTitleDisplayMode()` and `.toolbar()` modifiers in SwiftUI views for per-screen customization
- Set appearance on all three appearance proxies: `standardAppearance`, `compactAppearance`, `scrollEdgeAppearance`

### 3. Memory Management & Retain Cycles

**Decision**: Coordinators store `weak var navigationController` and sub-coordinators as `lazy var` (no children array for non-tab coordinators).

**Rationale**:
- **Prevent Retain Cycles**: UINavigationController strongly owns its view controllers, which may reference coordinators. Weak navigationController reference breaks cycle.
- **Lazy Initialization**: Sub-coordinators only created when needed, reducing memory footprint
- **Simplified Lifecycle**: No need to manually manage children array for simple navigation flows (push/pop)
- **Exception for Tab Bar**: Tab bar coordinators maintain children array because all tabs must exist simultaneously

**Alternatives Considered**:
- **Strong navigationController Reference**: Rejected due to potential retain cycles (navigationController → viewController → coordinator → navigationController)
- **Children Array for All Coordinators**: Rejected as unnecessary complexity; coordinators are deallocated when navigation pops
- **Unowned References**: Rejected because unowned crashes if object is deallocated; weak is safer with optional unwrapping

**Memory Management Checklist**:
- ✅ `weak var navigationController: UINavigationController?` in CoordinatorInterface
- ✅ Sub-coordinators as `lazy var` (created on-demand)
- ✅ No strong references from ViewModels back to coordinators (use protocols/closures)
- ✅ Use `[weak self]` in closures within coordinators
- ✅ Test with Instruments (Leaks, Allocations) to verify no cycles

### 4. Coordinator Protocol Design

**Decision**: Define `CoordinatorInterface` protocol with `start(animated: Bool) async` method and weak `navigationController` property.

**Rationale**:
- **Consistent Contract**: All coordinators follow same interface, enabling polymorphism
- **Async Start**: Supports coordinators that need async initialization (e.g., loading data before presenting screen)
- **Flexibility**: Animated parameter allows control over presentation animations
- **Weak Reference**: Protocol enforces memory-safe pattern

**Alternatives Considered**:
- **Class-Based Coordinator Base**: Rejected to avoid inheritance hierarchy; protocol composition is more flexible
- **Synchronous start()**: Rejected because some flows may require async operations before navigation
- **Strong navigationController**: Rejected due to memory management concerns (covered in topic 3)

**Protocol Definition**:
```swift
protocol CoordinatorInterface: AnyObject {
    var navigationController: UINavigationController? { get set }
    func start(animated: Bool) async
}
```

**Extension for Common Logic** (optional):
```swift
extension CoordinatorInterface {
    func start() async {
        await start(animated: true)
    }
}
```

### 5. UIKit App Lifecycle (AppDelegate + SceneDelegate)

**Decision**: Migrate from SwiftUI `@main App` to UIKit lifecycle with `AppDelegate` and `SceneDelegate`.

**Rationale**:
- **Coordinator Initialization**: AppDelegate/SceneDelegate provide natural place to create root coordinator
- **Window Management**: SceneDelegate owns window, required for UIKit navigation
- **Scene-Based Lifecycle**: Supports multi-window on iPad, future-proofs architecture
- **Standard Pattern**: Most UIKit + Coordinator examples use this approach

**Alternatives Considered**:
- **Keep SwiftUI App with UIViewControllerRepresentable**: Rejected because mixing SwiftUI app lifecycle with UIKit navigation is awkward and limits control
- **AppDelegate Only (iOS 12 style)**: Rejected because iOS 13+ scene-based lifecycle is the standard and supports multi-window

**Migration Steps**:
1. Remove `@main` attribute from SwiftUI `App` struct
2. Create `AppDelegate` with `UIApplicationDelegate` conformance
3. Create `SceneDelegate` with `UIWindowSceneDelegate` conformance
4. Update `Info.plist` to declare Application Scene Manifest (remove storyboard references)
5. Initialize window, navigationController, and AppCoordinator in `scene(_:willConnectTo:options:)`

**Info.plist Configuration**:
- Add `UIApplicationSceneManifest` with `UISceneConfigurations`
- Set `UISceneDelegateClassName` to `SceneDelegate`
- Remove `UISceneStoryboardFile` (no storyboards)
- Keep `UIApplicationSupportsMultipleScenes` = `NO` (unless multi-window support needed)

### 6. Navigation Bar Appearance Configuration

**Decision**: Configure transparent green semi-transparent navigation bar appearance in SceneDelegate using UINavigationBarAppearance.

**Rationale**:
- **Global Styling**: Apply appearance once in SceneDelegate, affects all navigation controllers
- **iOS 13+ API**: UINavigationBarAppearance provides granular control over colors, transparency, blur
- **Consistency**: Ensures uniform navigation bar styling across app

**Alternatives Considered**:
- **Per-Screen Configuration**: Rejected to avoid code duplication and inconsistency
- **SwiftUI .navigationBarTitleDisplayMode()**: Rejected because limited control over transparency and blur effects
- **UIAppearance Proxy**: Rejected as deprecated; UINavigationBarAppearance is the modern API

**Implementation Pattern**:
```swift
// In SceneDelegate
let appearance = UINavigationBarAppearance()
appearance.configureWithTransparentBackground() // Makes background transparent
appearance.backgroundColor = UIColor.green.withAlphaComponent(0.5) // Semi-transparent green

// Apply to all appearance states
UINavigationBar.appearance().standardAppearance = appearance
UINavigationBar.appearance().compactAppearance = appearance
UINavigationBar.appearance().scrollEdgeAppearance = appearance
```

**Transparency Options**:
- `configureWithTransparentBackground()`: Fully transparent with no blur
- `configureWithDefaultBackground()`: System default (opaque with blur)
- `configureWithOpaqueBackground()`: Opaque without blur
- Manual `backgroundColor` + `backgroundEffect`: Custom transparency and blur

## Implementation Recommendations

### Phase 1: Core Infrastructure
1. Create AppDelegate and SceneDelegate (remove @main from SwiftUI App)
2. Define CoordinatorInterface protocol
3. Implement AppCoordinator (minimal, just sets up window)
4. Configure navigation bar appearance in SceneDelegate
5. Set up initial splash screen (SplashScreenView with UIHostingController)

### Phase 2: First Navigation Flow
1. Create ListScreenCoordinator
2. AppCoordinator starts ListScreenCoordinator
3. ListScreenCoordinator presents ContentView via UIHostingController
4. Verify memory management with Instruments

### Phase 3: Testing & Polish
1. Add E2E tests for navigation flow (splash → list)
2. Verify navigation bar styling
3. Test lifecycle events (background/foreground)
4. Document coordinator pattern in quickstart.md

## Best Practices

### Coordinator Responsibilities
- ✅ Create and configure ViewModels
- ✅ Handle navigation between screens
- ✅ Manage child coordinators lifecycle (for complex flows)
- ✅ Respond to navigation events from ViewModels
- ❌ Do NOT handle business logic (belongs in ViewModels/UseCases)
- ❌ Do NOT directly access views (coordinators don't know about SwiftUI view internals)

### ViewModel Responsibilities
- ✅ Manage presentation state (@Published properties for SwiftUI)
- ✅ Call use cases / repositories for data
- ✅ Transform domain models to view models
- ✅ Expose navigation triggers (e.g., `var onNavigateToDetail: (() -> Void)?`)
- ❌ Do NOT perform navigation (call coordinator via closure/protocol)
- ❌ Do NOT directly reference UIKit types

### View Responsibilities
- ✅ Render UI based on ViewModel state
- ✅ Handle user interactions (button taps, gestures)
- ✅ Call ViewModel methods in response to user actions
- ❌ Do NOT contain business logic
- ❌ Do NOT perform navigation directly

## References

- [Apple Documentation: UIHostingController](https://developer.apple.com/documentation/swiftui/uihostingcontroller)
- [Apple Documentation: UINavigationBarAppearance](https://developer.apple.com/documentation/uikit/uinavigationbarappearance)
- [Apple Documentation: Scenes](https://developer.apple.com/documentation/uikit/app_and_environment/scenes)
- [Coordinator Pattern (Soroush Khanlou)](https://khanlou.com/2015/01/the-coordinator/)
- [Advanced iOS App Architecture (raywenderlich.com)](https://www.raywenderlich.com/books/advanced-ios-app-architecture)

## Open Questions

None - all technical decisions resolved for initial implementation.

## Next Steps

Proceed to Phase 1: Generate quickstart.md with step-by-step implementation guide for developers.

