# Implementation Plan: Pet Details Screen (iOS UI)

**Branch**: `012-ios-pet-details-screen` | **Date**: November 24, 2025 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/012-ios-pet-details-screen/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implementing a new iOS screen that displays comprehensive pet details with navigation from the pet list. Users can view pet photos, status badges, identification information, location data, contact details, and additional descriptions. The screen follows iOS MVVM-C architecture with SwiftUI views, UIKit-based coordinator navigation, and repository-based data fetching. This is a UI-only implementation with placeholder actions for map view and report removal.

## Technical Context

**Language/Version**: Swift 5.9+  
**Primary Dependencies**: SwiftUI (UI layer), UIKit (coordinator navigation), URLSession or Alamofire (HTTP client)  
**Storage**: N/A (data fetched from repository, no local persistence for this feature)  
**Testing**: XCTest with Swift Concurrency (async/await), 80% coverage target  
**Target Platform**: iOS 15+
**Project Type**: Mobile (iOS native application)  
**Performance Goals**: Standard iOS UI performance (60 fps scrolling, instant navigation transitions)  
**Constraints**: N/A (no specific performance or memory constraints defined)  
**Scale/Scope**: Single screen with multiple reusable components (pet photo with badges, label-value pairs), full-screen loading/error states

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is an iOS-only UI feature. Android, Web, and Backend checks are marked as N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (not affected by this iOS-only feature)
  - iOS: ✅ Domain models, repositories, ViewModels in `/iosApp` (no use cases per constitution)
  - Web: N/A (not affected by this iOS-only feature)
  - Backend: N/A (no backend changes, repository will mock data until endpoint available)
  - NO shared compiled code between platforms
  - Violation justification: N/A - compliant

- [ ] **Android MVI Architecture**: N/A - iOS-only feature
  - Violation justification: Not applicable

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - ✅ UIKit-based coordinator will manage navigation (PetDetailsCoordinator)
  - ✅ ViewModel will conform to `ObservableObject` with `@Published` properties (PetDetailsViewModel)
  - ✅ ViewModel will call repository directly (no use cases per constitution)
  - ✅ ViewModel communicates with coordinator via closures (onBack, optional navigation callbacks)
  - ✅ SwiftUI view (PetDetailsView) observes ViewModel (no business/navigation logic in views)
  - Violation justification: N/A - fully compliant

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories
  - Android: N/A
  - iOS: ✅ Repository protocol `PetRepository` in `/iosApp/iosApp/Domain/Repositories/` with method `getPetDetails(id: String) async throws -> PetDetails`
  - ✅ Implementation `PetRepositoryImpl` in `/iosApp/iosApp/Data/Repositories/` (mocked data until backend endpoint available)
  - ✅ ViewModel references protocol, not concrete implementation
  - Web: N/A
  - Backend: N/A
  - Violation justification: N/A - compliant

- [x] **Dependency Injection**: Plan includes DI setup
  - Android: N/A
  - iOS: ✅ Manual DI via ServiceContainer in `/iosApp/iosApp/DI/`
  - ✅ Coordinator receives repository via constructor injection
  - ✅ ViewModel receives repository via constructor injection from coordinator
  - Web: N/A
  - Backend: N/A
  - Violation justification: N/A - using mandated manual DI approach

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests
  - Android: N/A
  - iOS: ✅ Unit tests in `/iosApp/iosAppTests/ViewModels/PetDetailsViewModelTests.swift`
  - ✅ Tests cover: loading state, loaded state with data, error state, retry functionality, coordinator callbacks
  - ✅ Run via: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
  - ✅ Target: 80% line + branch coverage for ViewModel and reusable components
  - Web: N/A
  - Backend: N/A
  - Violation justification: N/A - compliant

- [x] **End-to-End Tests**: Plan includes E2E tests for user stories
  - Web: N/A
  - Mobile: ✅ Appium tests in `/e2e-tests/mobile/specs/012-ios-pet-details-screen.spec.ts`
  - ✅ Tests cover all 7 user stories from spec.md
  - ✅ TypeScript with Screen Object Model pattern
  - ✅ Each user story has at least one E2E scenario
  - Violation justification: N/A - compliant

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns
  - Android: N/A
  - iOS: ✅ Swift Concurrency (`async`/`await`) with `@MainActor` for ViewModel
  - ✅ Repository method: `func getPetDetails(id: String) async throws -> PetDetails`
  - ✅ ViewModel loading method: `func loadPetDetails() async`
  - ✅ No Combine, no callback-based patterns
  - Web: N/A
  - Backend: N/A
  - Violation justification: N/A - compliant

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers
  - Android: N/A
  - iOS: ✅ All interactive views use `.accessibilityIdentifier()` modifier
  - ✅ Naming convention: `petDetails.{element}.{action}`
  - ✅ Examples: `petDetails.phone.tap`, `petDetails.email.tap`, `petDetails.showMap.button`, `petDetails.removeReport.button`, `petDetails.retry.button`
  - Web: N/A
  - Violation justification: N/A - compliant

- [x] **Public API Documentation**: Plan ensures documentation where needed
  - Swift: ✅ SwiftDoc format (`/// ...`)
  - ✅ ViewModel class will have doc comment explaining role
  - ✅ Reusable components (PetPhotoWithBadges, LabelValueRow) will have doc comments
  - ✅ Repository protocol method will have doc comment
  - ✅ Self-explanatory properties and simple methods skip documentation
  - Violation justification: N/A - compliant

- [x] **Given-When-Then Test Structure**: All tests follow convention
  - ✅ Unit tests clearly separate Given (setup), When (action), Then (verification)
  - ✅ Test names follow Swift convention: `testMethodName_whenCondition_shouldExpectedBehavior()`
  - ✅ Comments mark test phases in complex tests
  - ✅ E2E tests structure scenarios with Given-When-Then phases
  - Violation justification: N/A - compliant

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - iOS-only feature, no backend changes
  - Violation justification: Not applicable - feature does not affect `/server` module

- [x] **Backend Code Quality**: N/A - iOS-only feature, no backend changes
  - Violation justification: Not applicable - feature does not affect `/server` module

- [x] **Backend Dependency Management**: N/A - iOS-only feature, no backend changes
  - Violation justification: Not applicable - feature does not affect `/server` module

- [x] **Backend Directory Structure**: N/A - iOS-only feature, no backend changes
  - Violation justification: Not applicable - feature does not affect `/server` module

- [x] **Backend TDD Workflow**: N/A - iOS-only feature, no backend changes
  - Violation justification: Not applicable - feature does not affect `/server` module

- [x] **Backend Testing Strategy**: N/A - iOS-only feature, no backend changes
  - Violation justification: Not applicable - feature does not affect `/server` module

## Project Structure

### Documentation (this feature)

```text
specs/012-ios-pet-details-screen/
├── spec.md              # Feature specification (input)
├── plan.md              # This file (implementation plan)
├── research.md          # Phase 0: Technology decisions and patterns
├── data-model.md        # Phase 1: Domain model definitions
├── quickstart.md        # Phase 1: Developer setup guide
├── contracts/           # Phase 1: API contracts (mock data structure)
└── tasks.md             # Phase 2: Implementation tasks (created by /speckit.tasks)
```

### Source Code (iOS platform)

```text
iosApp/iosApp/
├── Domain/
│   ├── Models/
│   │   └── PetDetails.swift              # Domain model for pet details
│   └── Repositories/
│       └── PetRepository.swift           # Repository protocol
│
├── Data/
│   └── Repositories/
│       └── PetRepositoryImpl.swift       # Repository implementation (mocked data)
│
├── Coordinators/
│   └── PetDetailsCoordinator.swift       # UIKit-based navigation coordinator
│
├── ViewModels/
│   └── PetDetailsViewModel.swift         # ObservableObject with @Published state
│
├── Views/
│   ├── PetDetailsView.swift              # Main screen SwiftUI view
│   ├── Components/
│   │   ├── PetPhotoWithBadges.swift      # Reusable photo + status/reward badges
│   │   └── LabelValueRow.swift           # Reusable label-value pair component
│
└── DI/
    └── ServiceContainer.swift            # Manual DI setup (existing, extends with pet details)

iosApp/iosAppTests/
├── ViewModels/
│   └── PetDetailsViewModelTests.swift    # Unit tests for ViewModel (80% coverage)
└── Components/
    ├── PetPhotoWithBadgesTests.swift     # Unit tests for photo component
    └── LabelValueRowTests.swift          # Unit tests for label-value component

e2e-tests/mobile/
├── specs/
│   └── 012-ios-pet-details-screen.spec.ts  # E2E tests for iOS (Appium)
├── screens/
│   └── PetDetailsScreen.ts               # Screen Object Model
└── steps/
    └── petDetailsSteps.ts                # Reusable test steps
```

**Structure Decision**: iOS-only mobile feature following MVVM-C architecture. All iOS code resides in `/iosApp` module with clear separation: Domain (models, repository protocols), Data (repository implementations), Coordinators (UIKit navigation), ViewModels (presentation logic), Views (SwiftUI UI). Manual dependency injection via ServiceContainer. Unit tests in `/iosApp/iosAppTests` target 80% coverage. E2E tests in `/e2e-tests/mobile` cover all user stories.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No violations detected. All constitution checks passed. No complexity tracking required.
