# Implementation Plan: iOS Display Fullscreen Interactive Map with Legend

**Branch**: `KAN-32-ios-display-fullscreen-map` | **Date**: 2025-01-07 | **Spec**: [spec.md](./spec.md)  
**Input**: Feature specification from `/specs/KAN-32-ios-display-fullscreen-map/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Replace the empty fullscreen map placeholder with an interactive MapKit-based map centered on user's location with zoom/pan/double-tap gestures. Add a static legend overlay explaining future pin meanings (missing/found animal markers).

## Technical Context

**Language/Version**: Swift, iOS 18+  
**Primary Dependencies**: SwiftUI, MapKit (native iOS frameworks)  
**Storage**: N/A  
**Testing**: XCTest with Swift Concurrency (async/await)  
**Target Platform**: iOS 18+, iPhone 16 (simulator for tests)  
**Project Type**: mobile (iOS)  
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)  
**Constraints**: N/A  
**Scale/Scope**: Single fullscreen view with map + legend overlay

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is iOS-only feature. Backend, Web, Android checks marked as N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (iOS-only feature)
  - iOS: Domain models, repositories, ViewModels in `/iosApp`
  - Web: N/A (iOS-only feature)
  - Backend: N/A (no backend changes needed)
  - NO shared compiled code between platforms
  - Violation justification: _N/A - compliant_

- [x] **Android MVI Architecture**: N/A (iOS-only feature)
  - Violation justification: _N/A - iOS-only feature_

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - UIKit-based coordinators manage navigation and create `UIHostingController` instances ✅ (HomeCoordinator already handles this)
  - ViewModels conform to `ObservableObject` with `@Published` properties ✅
  - ViewModels communicate with coordinators via methods or closures ✅
  - SwiftUI views observe ViewModels (no business/navigation logic in views) ✅
  - Violation justification: _N/A - compliant_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Android: N/A
  - iOS: `LocationServiceProtocol` already exists in `/iosApp/iosApp/Domain/`
  - Web: N/A
  - Backend: N/A
  - Violation justification: _N/A - compliant, reusing existing LocationServiceProtocol_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: N/A
  - iOS: MUST use manual DI - existing `LocationPermissionHandler` injected via constructor
  - Web: N/A
  - Backend: N/A
  - Violation justification: _N/A - compliant_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: N/A
  - iOS: Tests in `/iosApp/iosAppTests/Features/LandingPage/Views/FullscreenMap/`
  - Web: N/A
  - Backend: N/A
  - Coverage target: 80% line + branch coverage for FullscreenMapViewModel
  - Violation justification: _N/A - compliant_

- [x] **End-to-End Tests**: N/A for this feature
  - Feature focuses on map display/interactions - not suitable for E2E automation
  - Map gesture testing requires platform-specific tools beyond Appium capabilities
  - Violation justification: _E2E tests not applicable - map gesture interactions (pinch-zoom, pan) cannot be reliably automated with Appium. Visual verification required for map rendering._

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: N/A
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor` ✅
  - Web: N/A
  - Backend: N/A
  - No Combine, RxJava, RxSwift, or callback-based patterns for new code ✅
  - Violation justification: _N/A - compliant_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: N/A
  - iOS: `accessibilityIdentifier` modifier on all interactive views
  - Web: N/A
  - Naming convention: `fullscreenMap.{element}` (e.g., `fullscreenMap.map`, `fullscreenMap.legend`)
  - Violation justification: _N/A - compliant_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Swift: SwiftDoc format (`/// ...`)
  - Documentation must be concise and high-level (1-3 sentences: WHAT/WHY, not HOW)
  - Violation justification: _N/A - compliant_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then)
  - Test names follow Swift conventions: `test{Method}_when{Condition}_should{ExpectedBehavior}`
  - Violation justification: _N/A - compliant_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A (no backend changes)
- [x] **Backend Code Quality**: N/A (no backend changes)
- [x] **Backend Dependency Management**: N/A (no backend changes)

### Web Architecture & Quality Standards (if `/webApp` affected)

- [x] **Web Technology Stack**: N/A (iOS-only feature)
- [x] **Web Code Quality**: N/A (iOS-only feature)
- [x] **Web Dependency Management**: N/A (iOS-only feature)
- [x] **Web Business Logic Extraction**: N/A (iOS-only feature)
- [x] **Web TDD Workflow**: N/A (iOS-only feature)
- [x] **Web Testing Strategy**: N/A (iOS-only feature)

- [x] **Backend Directory Structure**: N/A (no backend changes)
- [x] **Backend TDD Workflow**: N/A (no backend changes)
- [x] **Backend Testing Strategy**: N/A (no backend changes)

## Project Structure

### Documentation (this feature)

```text
specs/KAN-32-ios-display-fullscreen-map/
├── spec.md              # Feature specification
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
└── tasks.md             # Phase 2 output (created by /speckit.tasks)
```

### Source Code (repository root)

```text
iosApp/iosApp/
├── Features/
│   └── LandingPage/
│       ├── Coordinators/
│       │   └── HomeCoordinator.swift       # EXISTING - already handles fullscreen map navigation
│       └── Views/
│           └── FullscreenMap/
│               ├── FullscreenMapView.swift           # MODIFY - add Map + Legend
│               ├── FullscreenMapViewModel.swift      # MODIFY - add location state
├── Components/
│   ├── MapSectionHeaderView.swift        # MODIFY - make title optional
│   ├── MapSectionHeaderView_Model.swift  # MODIFY - title: String?
│   └── MapSectionHeaderView_Model+LandingPage.swift  # EXISTING - factory for landing page
│   └── MapSectionHeaderView_Model+FullscreenMap.swift  # NEW - factory for fullscreen map
├── Domain/
│   ├── Models/
│   │   └── Coordinate.swift                # EXISTING - reuse mapRegion() helper
│   └── Services/
│       └── LocationPermissionHandler.swift  # EXISTING - reuse for location
└── Resources/
    ├── en.lproj/Localizable.strings         # EXISTING - strings already defined
    └── pl.lproj/Localizable.strings         # EXISTING - strings already defined

iosApp/iosAppTests/
└── Features/
    └── LandingPage/
        └── FullscreenMap/
            └── FullscreenMapViewModelTests.swift    # NEW - ViewModel tests
```

**Structure Decision**: iOS mobile structure with MVVM-C architecture. Fullscreen map feature lives under `Features/LandingPage/Views/FullscreenMap/` since it's accessed from landing page. Legend reuses existing `MapSectionHeaderView` component with optional title (title hidden for fullscreen map, navigation bar provides context).

## Complexity Tracking

> **No violations requiring justification**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| - | - | - |
