# Implementation Plan: iOS Landing Page - Embed Map View

**Branch**: `KAN-30-embed-map-in-landing-page` | **Date**: 2025-12-19 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/KAN-30-embed-map-in-landing-page/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Embed a static map preview in the iOS landing page between the Hero panel and the Recently Lost Pets section. Uses SwiftUI Map with disabled interactions (no MKMapSnapshotter - simpler approach). MapPreviewView uses simple Model enum. LandingPageViewModel manages the map state via `@Published var mapPreviewModel`. The map displays 16:9 aspect ratio centered on user location (10 km radius).

## Technical Context

**Language/Version**: Swift 5.9+, iOS 18+
**Primary Dependencies**: SwiftUI (Map view), MapKit (MKCoordinateRegion)
**Storage**: N/A (no persistence required)
**Testing**: XCTest with Swift Concurrency (async/await)
**Target Platform**: iOS 18+ (iPhone 16 Simulator for tests)
**Project Type**: Mobile (iOS only - Android/Web explicitly out of scope)
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)
**Constraints**: Offline-capable graceful degradation (show error state when map fails to load)
**Scale/Scope**: Single screen modification (landing page)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is an iOS-only feature. Frontend checks for Android/Web and Backend checks are marked N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (iOS only feature)
  - iOS: New MapPreviewView component with ViewModel in `/iosApp/iosApp/Features/LandingPage/`
  - Web: N/A (iOS only feature)
  - Backend: N/A (no backend changes required)
  - NO shared compiled code between platforms
  - Violation justification: _N/A - Compliant_

- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - N/A - iOS only feature
  - Violation justification: _N/A - iOS only feature_

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - UIKit-based coordinator (HomeCoordinator) already manages navigation ✓
  - MapPreviewView uses simple struct Model pattern (like ErrorView, PetCardView) ✓
  - LandingPageViewModel manages map state via `@Published var mapPreviewModel` ✓
  - MapPreviewView is stateless - receives Model, no business/navigation logic ✓
  - Violation justification: _N/A - Compliant_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - iOS: Uses existing LocationServiceProtocol and LocationPermissionHandler
  - Map snapshotter is framework code (MKMapSnapshotter) - no custom protocol needed
  - Violation justification: _N/A - Compliant (uses existing interfaces)_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - iOS: No new ViewModel or service - MapPreviewView uses simple Model pattern
  - LandingPageViewModel already receives LocationPermissionHandler (no changes needed)
  - SwiftUI Map renders synchronously - no service injection required
  - Violation justification: _N/A - Compliant_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - iOS: Tests added to `/iosApp/iosAppTests/Features/LandingPage/LandingPageViewModelTests.swift`
  - Coverage target: 80% line + branch coverage
  - Test scenarios: map permission states, snapshot loading, error handling in LandingPageViewModel
  - Violation justification: _N/A - Compliant_

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - N/A - E2E tests not required for static map preview (visual verification only)
  - Existing location permission E2E tests cover permission flow
  - Violation justification: _Map preview is visual-only UI; E2E automation not practical for verifying map image rendering_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - iOS: Swift Concurrency (`async`/`await`) for MKMapSnapshotter
  - No Combine, RxSwift, or callback-based patterns
  - Violation justification: _N/A - Compliant_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - iOS: `accessibilityIdentifier` on MapPreviewView and retry button
  - Naming: `landingPage.mapPreview`, `landingPage.mapPreview.retry`
  - Violation justification: _N/A - Compliant_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - SwiftDoc format for MapPreviewViewModel public methods
  - Self-explanatory names (like `loadMapSnapshot()`) will skip documentation
  - Violation justification: _N/A - Compliant_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests structured with Given-When-Then phases
  - Test names follow Swift convention: `testMethod_whenCondition_shouldExpectedBehavior`
  - Violation justification: _N/A - Compliant_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - iOS only feature, no server changes
- [x] **Backend Code Quality**: N/A - iOS only feature
- [x] **Backend Dependency Management**: N/A - iOS only feature
- [x] **Backend Directory Structure**: N/A - iOS only feature
- [x] **Backend TDD Workflow**: N/A - iOS only feature
- [x] **Backend Testing Strategy**: N/A - iOS only feature

### Web Architecture & Quality Standards (if `/webApp` affected)

- [x] **Web Technology Stack**: N/A - iOS only feature
- [x] **Web Code Quality**: N/A - iOS only feature
- [x] **Web Dependency Management**: N/A - iOS only feature
- [x] **Web Business Logic Extraction**: N/A - iOS only feature
- [x] **Web TDD Workflow**: N/A - iOS only feature
- [x] **Web Testing Strategy**: N/A - iOS only feature

## Project Structure

### Documentation (this feature)

```text
specs/KAN-30-embed-map-in-landing-page/
├── plan.md              # This file
├── spec.md              # Feature specification
├── research.md          # Phase 0: MKMapSnapshotter patterns
├── data-model.md        # Phase 1: MapPreviewState enum
├── quickstart.md        # Phase 1: Implementation guide
└── tasks.md             # Phase 2: Task breakdown (separate command)
```

### Source Code (repository root)

```text
iosApp/iosApp/
├── Features/
│   └── LandingPage/
│       └── Views/
│           ├── LandingPageView.swift           # Modified: add MapPreviewView
│           ├── LandingPageViewModel.swift      # Modified: add mapPreviewModel
│           └── Components/
│               ├── MapPreviewView.swift        # NEW: SwiftUI view with Map
│               └── MapPreviewView_Model.swift  # NEW: Model enum
├── Domain/
│   └── Models/
│       └── Coordinate.swift                    # Modified: add mapRegion() helper
├── Resources/
│   ├── en.lproj/
│   │   └── Localizable.strings                # Modified: add map preview strings
│   └── pl.lproj/
│       └── Localizable.strings                # Modified: add map preview strings
└── Generated/
    └── Strings.swift                           # Auto-generated by SwiftGen

iosAppTests/
└── Features/
    └── LandingPage/
        └── LandingPageViewModelTests.swift     # Modified: add map tests
```

**Structure Decision**: iOS with MVVM-C. MapPreviewView uses SwiftUI Map with disabled interactions (no MKMapSnapshotter). Simple Model enum. All logic in LandingPageViewModel.

## Complexity Tracking

> No violations requiring justification. All constitution checks pass.
