# Implementation Plan: iOS Fullscreen Map - Display Pin Annotation Details

**Branch**: `KAN-32-ios-fullscreen-map-annotation` | **Date**: 2025-01-08 | **Spec**: [spec.md](./spec.md)  
**Input**: Feature specification from `/specs/KAN-32-ios-fullscreen-map-annotation/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implement interactive annotation callouts for map pins in the iOS fullscreen map view. When a user taps a pin, a custom callout card appears displaying pet details (photo, name, species/breed, location, date, contact info, description, status badge). The callout follows Figma design (node 1192:5893) with white card styling, rounded corners, drop shadow, and pointer arrow. Tapping elsewhere or the same pin dismisses the callout.

**Technical Approach**: Extend the existing `FullscreenMapView` and `FullscreenMapViewModel` to handle pin selection via SwiftUI Map's `selection` binding. Create a custom `AnnotationCalloutView` component that appears above selected pins with positioning logic for edge cases. Reuse existing patterns for placeholder images, status badges, and coordinate/date formatting from `PetDetailsViewModel`.

## Technical Context

**Language/Version**: Swift 5.9+, iOS 18+  
**Primary Dependencies**: SwiftUI, MapKit (Map, Annotation, selection binding)  
**Storage**: N/A (data already fetched from repository in existing pin loading flow)  
**Testing**: XCTest with Swift Concurrency  
**Target Platform**: iOS 18+ (iPhone 16 Simulator)  
**Project Type**: Mobile (iOS only)  
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)  
**Constraints**: N/A  
**Scale/Scope**: Single screen feature enhancement

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is an iOS-only feature. Backend, Web, and Android checks are marked N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (iOS-only feature)
  - iOS: Full implementation in `/iosApp` - ViewModel, View components, Models
  - Web: N/A (iOS-only feature)
  - Backend: N/A (no backend changes - data already available from announcement fetch)
  - NO shared compiled code between platforms ✓
  - Violation justification: _Not applicable_

- [x] **Android MVI Architecture**: N/A (iOS-only feature)
  - Violation justification: _Not applicable - iOS only_

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - UIKit-based coordinators manage navigation ✓ (existing `HomeCoordinator`)
  - ViewModels conform to `ObservableObject` with `@Published` properties ✓
  - ViewModels communicate with coordinators via methods or closures ✓
  - SwiftUI views observe ViewModels (no business/navigation logic in views) ✓
  - Violation justification: _Compliant_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - iOS: Repository protocols in `/iosApp/iosApp/Domain/Repositories/` ✓
  - `AnnouncementRepositoryProtocol` already exists and provides announcement data
  - Violation justification: _Compliant - uses existing repository_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - iOS: Manual DI via `ServiceContainer` ✓
  - Violation justification: _Compliant - uses existing DI setup_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - iOS: Tests in `/iosApp/iosAppTests/Features/LandingPage/Views/FullscreenMap/`
  - New tests required for:
    - `FullscreenMapViewModel` selection handling
    - `AnnotationCalloutView.Model` mapping
    - Coordinate/date formatting (reuse or extend from PetDetailsViewModel)
  - Coverage target: 80% line + branch coverage ✓
  - Violation justification: _Compliant_

- [x] **End-to-End Tests**: N/A for this feature
  - Mobile E2E tests would require Appium setup which is out of scope for this annotation UI feature
  - Violation justification: _E2E tests are optional for UI-only feature without navigation flow changes_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor` ✓
  - No Combine, RxSwift usage ✓
  - Violation justification: _Compliant_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - iOS: `accessibilityIdentifier` modifier on all interactive views ✓
  - Planned identifiers:
    - `fullscreenMap.annotation.{id}` - Annotation callout
    - `fullscreenMap.annotation.photo` - Pet photo
    - `fullscreenMap.annotation.statusBadge` - Status badge
    - `fullscreenMap.annotation.dismiss` - Dismiss area (map tap)
  - Violation justification: _Compliant_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Swift: SwiftDoc format (`/// ...`)
  - Documentation for new public types: `AnnotationCalloutView`, `AnnotationCalloutView.Model`
  - Violation justification: _Compliant_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - All new tests will use Given-When-Then structure
  - Test names: `test{Method}_when{Condition}_should{ExpectedBehavior}`
  - Violation justification: _Compliant_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A (no backend changes)
- [x] **Backend Code Quality**: N/A (no backend changes)
- [x] **Backend Dependency Management**: N/A (no backend changes)
- [x] **Backend Directory Structure**: N/A (no backend changes)
- [x] **Backend TDD Workflow**: N/A (no backend changes)
- [x] **Backend Testing Strategy**: N/A (no backend changes)

### Web Architecture & Quality Standards (if `/webApp` affected)

- [x] **Web Technology Stack**: N/A (iOS-only feature)
- [x] **Web Code Quality**: N/A (iOS-only feature)
- [x] **Web Dependency Management**: N/A (iOS-only feature)
- [x] **Web Business Logic Extraction**: N/A (iOS-only feature)
- [x] **Web TDD Workflow**: N/A (iOS-only feature)
- [x] **Web Testing Strategy**: N/A (iOS-only feature)

## Project Structure

### Documentation (this feature)

```text
specs/KAN-32-ios-fullscreen-map-annotation/
├── spec.md              # Feature specification (complete)
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
└── contracts/           # Phase 1 output - N/A (no API changes)
```

### Source Code (repository root)

```text
iosApp/iosApp/
├── Features/
│   └── LandingPage/
│       └── Views/
│           └── FullscreenMap/
│               ├── FullscreenMapView.swift          # MODIFY: Add selection binding, callout overlay
│               ├── FullscreenMapViewModel.swift     # MODIFY: Add selection state, callout model
│               ├── MapPin.swift                     # MODIFY: Extend with annotation data
│               ├── AnnotationCalloutView.swift      # NEW: Custom callout card component
│               └── AnnotationCalloutView_Model.swift # NEW: Model for callout (separate file)
├── Views/
│   └── (existing shared components)
└── Generated/
    └── Strings.swift    # Auto-generated after adding new localization strings

iosApp/iosAppTests/
└── Features/
    └── LandingPage/
        └── Views/
            └── FullscreenMap/
                ├── FullscreenMapViewModelTests.swift  # MODIFY: Add selection tests
                └── AnnotationCalloutViewModelTests.swift # NEW: Callout model mapping tests
```

**Structure Decision**: iOS-only feature using existing FullscreenMap module. Custom callout component created as subview with Model pattern (static data, no ViewModel needed).

## Complexity Tracking

> **No violations - feature follows constitution patterns**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| _None_ | _N/A_ | _N/A_ |
