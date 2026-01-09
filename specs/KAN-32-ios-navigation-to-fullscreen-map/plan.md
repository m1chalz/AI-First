# Implementation Plan: iOS Navigation to Fullscreen Map View

**Branch**: `KAN-32-ios-navigation-to-fullscreen-map` | **Date**: 2025-01-07 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/KAN-32-ios-navigation-to-fullscreen-map/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implement navigation from landing page map preview to an empty fullscreen map view using UINavigationController push animation. Users tap the map preview on landing page to open fullscreen view, then use standard iOS back button or edge swipe gesture to return. The fullscreen view is a placeholder (no MapKit rendering yet) with navigation title "Pet Locations".

## Technical Context

**Language/Version**: Swift 5.9+, iOS 18+  
**Primary Dependencies**: UIKit (navigation), SwiftUI (views), MapKit (future, not used in this ticket)  
**Storage**: N/A (no persistence required)  
**Testing**: XCTest with Swift Concurrency  
**Target Platform**: iOS 18+, iPhone 16 Simulator  
**Project Type**: Mobile (iOS-only feature)  
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)  
**Constraints**: N/A  
**Scale/Scope**: Single screen addition with navigation flow modification

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is an iOS-only feature. Backend, Web, and Android checks are marked N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (iOS-only feature)
  - iOS: Domain models, ViewModels in `/iosApp` ✓
  - Web: N/A (iOS-only feature)
  - Backend: N/A (no API changes)
  - NO shared compiled code between platforms ✓

- [ ] **Android MVI Architecture**: N/A (iOS-only feature)

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - UIKit-based coordinators manage navigation and create `UIHostingController` instances ✓
  - ViewModels conform to `ObservableObject` with `@Published` properties ✓
  - ViewModels communicate with coordinators via methods or closures ✓
  - SwiftUI views observe ViewModels (no business/navigation logic in views) ✓
  - Navigation title set in UIKit (coordinator level), NOT in SwiftUI views ✓

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - iOS: N/A (no new repository needed - placeholder view only)

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - iOS: Manual DI - coordinator creates ViewModel with injected dependencies ✓

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - iOS: Tests in `/iosApp/iosAppTests/` ✓
  - Coverage target: 80% for ViewModel (minimal logic in this feature)

- [ ] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Mobile: E2E tests in `/e2e-tests/java/src/test/resources/features/mobile/`
  - Will be addressed in separate ticket (navigation-only E2E testing)
  - Violation justification: E2E tests for navigation can be added post-implementation; unit tests cover ViewModel behavior

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor` ✓
  - No Combine, RxSwift, or callback-based patterns ✓

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - iOS: `accessibilityIdentifier` modifier on all interactive views ✓
  - Naming convention: `{screen}.{element}` (e.g., `fullscreenMap.backButton`)

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Swift: SwiftDoc format (`/// ...`) for coordinators and ViewModels
  - Skip documentation for self-explanatory methods ✓

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Test names follow Swift conventions: `test{Method}_when{Condition}_should{ExpectedBehavior}` ✓

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A (iOS-only feature, no server changes)
- [x] **Backend Code Quality**: N/A
- [x] **Backend Dependency Management**: N/A
- [x] **Backend Directory Structure**: N/A
- [x] **Backend TDD Workflow**: N/A
- [x] **Backend Testing Strategy**: N/A

### Web Architecture & Quality Standards (if `/webApp` affected)

- [x] **Web Technology Stack**: N/A (iOS-only feature)
- [x] **Web Code Quality**: N/A
- [x] **Web Dependency Management**: N/A
- [x] **Web Business Logic Extraction**: N/A
- [x] **Web TDD Workflow**: N/A
- [x] **Web Testing Strategy**: N/A

## Project Structure

### Documentation (this feature)

```text
specs/KAN-32-ios-navigation-to-fullscreen-map/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output (empty - no API changes)
└── tasks.md             # Phase 2 output (/speckit.tasks command)
```

### Source Code (repository root)

```text
iosApp/iosApp/
├── Features/
│   ├── LandingPage/
│   │   ├── Coordinators/
│   │   │   └── HomeCoordinator.swift         # MODIFY: Add showFullscreenMap()
│   │   └── Views/
│   │       └── LandingPageViewModel.swift    # MODIFY: Add coordinator callback
│   └── FullscreenMap/                        # NEW FEATURE MODULE
│       ├── Coordinators/
│       │   └── FullscreenMapCoordinator.swift  # NEW: Navigation coordinator
│       └── Views/
│           ├── FullscreenMapView.swift         # NEW: SwiftUI placeholder view
│           └── FullscreenMapViewModel.swift    # NEW: ViewModel (minimal)
├── Resources/
│   ├── en.lproj/
│   │   └── Localizable.strings               # MODIFY: Add fullscreenMap.* strings
│   └── pl.lproj/
│       └── Localizable.strings               # MODIFY: Add fullscreenMap.* strings
└── Generated/
    └── Strings.swift                         # REGENERATE: Run swiftgen

iosAppTests/
└── Features/
    └── FullscreenMap/
        └── FullscreenMapViewModelTests.swift   # NEW: ViewModel unit tests
```

**Structure Decision**: New feature module `FullscreenMap` follows existing feature structure pattern (see `LandingPage`, `PetDetails`). Coordinator manages navigation, ViewModel handles presentation logic, View is pure SwiftUI.

## Complexity Tracking

No constitution violations requiring justification.
