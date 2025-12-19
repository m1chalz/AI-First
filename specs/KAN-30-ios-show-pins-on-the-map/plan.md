# Implementation Plan: iOS Map Preview - Display Missing Pet Pins

**Branch**: `KAN-30-ios-show-pins-on-the-map` | **Date**: 2025-12-19 | **Spec**: [spec.md](./spec.md)  
**Input**: Feature specification from `/specs/KAN-30-ios-show-pins-on-the-map/spec.md`

## Summary

Implements pin markers on the iOS landing page map preview showing locations of missing pet announcements. Pins are derived from the same announcements payload used by the landing page list (no separate fetch). The map preview remains static and non-interactive - tapping pins or the map performs no action.

## Technical Context

**Language/Version**: Swift 5.9+, iOS 18+  
**Primary Dependencies**: SwiftUI, MapKit  
**Storage**: N/A (consumes backend API)  
**Testing**: XCTest with Swift Concurrency (async/await)  
**Target Platform**: iOS 18+, iPhone  
**Project Type**: mobile  
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)  
**Constraints**: N/A  
**Scale/Scope**: N/A

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - iOS: Domain models, repositories, ViewModels in `/iosApp`
  - This feature is iOS-only (FR-013), no other platforms affected
  - NO shared compiled code between platforms
  - Violation justification: N/A - compliant

- [ ] **Android MVI Architecture**: N/A - iOS-only feature
  - Violation justification: Feature explicitly scoped to iOS only (FR-013)

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - `LandingPageViewModel` (ObservableObject) manages map preview state
  - `MapPreviewView` (SwiftUI) observes ViewModel via @Published properties
  - No navigation logic in views (map preview is non-interactive)
  - Coordinator handles settings navigation (existing)
  - Violation justification: N/A - compliant

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - iOS: Uses existing `AnnouncementRepositoryProtocol` - no changes needed
  - Violation justification: N/A - compliant

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - iOS: Uses existing manual DI via `ServiceContainer`
  - No new dependencies to inject
  - Violation justification: N/A - compliant

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - iOS: Tests in `/iosApp/iosAppTests/Features/LandingPage/`
  - Will test: `LandingPageViewModel` pin model creation, `AnnouncementCardsListViewModel` filtering
  - Coverage target: 80% line + branch coverage
  - Violation justification: N/A - compliant

- [ ] **End-to-End Tests**: N/A for this feature
  - Map preview pins are visual-only with no user interaction
  - E2E would only verify pins appear (visual testing out of scope)
  - Violation justification: Pins are non-interactive (FR-007, FR-008), no E2E scenarios applicable

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor`
  - Existing patterns in `LandingPageViewModel` already compliant
  - No Combine usage
  - Violation justification: N/A - compliant

- [ ] **Test Identifiers for UI Controls**: N/A for this feature
  - Pins are non-interactive (FR-007) - no tap handling
  - Map already has `landingPage.mapPreview` identifier
  - Violation justification: No new interactive elements (pins are visual-only)

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Swift: SwiftDoc format (`/// ...`)
  - New `MapPinModel` will have documentation
  - Skip for self-explanatory computed properties
  - Violation justification: N/A - compliant

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests will separate setup (Given), action (When), verification (Then)
  - Test names follow Swift convention: `testMethod_whenCondition_shouldExpectedBehavior`
  - Violation justification: N/A - compliant

### Backend Architecture & Quality Standards (if `/server` affected)

- [ ] **Backend Technology Stack**: N/A - iOS-only feature
- [ ] **Backend Code Quality**: N/A - iOS-only feature
- [ ] **Backend Dependency Management**: N/A - iOS-only feature
- [ ] **Backend Directory Structure**: N/A - iOS-only feature
- [ ] **Backend TDD Workflow**: N/A - iOS-only feature
- [ ] **Backend Testing Strategy**: N/A - iOS-only feature

### Web Architecture & Quality Standards (if `/webApp` affected)

- [ ] **Web Technology Stack**: N/A - iOS-only feature
- [ ] **Web Code Quality**: N/A - iOS-only feature
- [ ] **Web Dependency Management**: N/A - iOS-only feature
- [ ] **Web Business Logic Extraction**: N/A - iOS-only feature
- [ ] **Web TDD Workflow**: N/A - iOS-only feature
- [ ] **Web Testing Strategy**: N/A - iOS-only feature

## Project Structure

### Documentation (this feature)

```text
specs/KAN-30-ios-show-pins-on-the-map/
├── plan.md              # This file
├── research.md          # Phase 0 output - COMPLETE
├── data-model.md        # Phase 1 output - COMPLETE
├── quickstart.md        # Phase 1 output - COMPLETE
├── contracts/           # Phase 1 output - COMPLETE (no new contracts)
│   └── README.md
└── tasks.md             # Phase 2 output (via /speckit.tasks)
```

### Source Code (repository root)

```text
iosApp/iosApp/
├── Domain/
│   └── Models/
│       └── AnnouncementListQuery.swift         # MODIFY: Add range parameter
├── Features/
│   └── LandingPage/
│       └── Views/
│           ├── Components/
│           │   ├── MapPreviewView.swift            # MODIFY: Add pin rendering
│           │   ├── MapPreviewView_Model.swift      # MODIFY: Add pins array to .map case
│           │   └── MapPreviewView_PinModel.swift   # ADD: PinModel struct
│           └── LandingPageViewModel.swift          # MODIFY: Create pin models

iosAppTests/
└── Features/
    └── LandingPage/
        └── Views/
            └── LandingPageViewModelTests.swift     # MODIFY: Add pin tests
```

**Note**: `AnnouncementCardsListViewModel` does NOT require changes - backend returns only `active` (lost) and `found` announcements, so no status filtering needed. Pins are built directly from all announcements in `cardViewModels`.

**Structure Decision**: iOS mobile app with feature-based organization. All changes within existing `/iosApp` module structure. No new modules or packages required.

## Complexity Tracking

No constitution violations requiring justification.
