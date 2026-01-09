# Implementation Plan: iOS Fullscreen Map - Fetch and Display Pins

**Branch**: `KAN-32-ios-fullscreen-map-fetch-pins` | **Date**: 2025-01-07 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/KAN-32-ios-fullscreen-map-fetch-pins/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Extend iOS fullscreen map to fetch and display pins for animal announcements. On view load and after pan/zoom gestures, the app fetches announcements from existing backend API (`GET /api/v1/announcements?lat=X&lng=Y&range=Z`) and displays all returned announcements as pins (no animation, no client-side filtering). Silent error handling - failures keep existing pins without user notification.

## Technical Context

**Language/Version**: Swift 5.9+, iOS 18+  
**Primary Dependencies**: MapKit (SwiftUI Map API), URLSession (networking via existing repository)  
**Storage**: N/A (stateless - pins fetched per visible region)  
**Testing**: XCTest with Swift Concurrency (async/await)  
**Target Platform**: iOS 18+ (iPhone 16 Simulator for tests)  
**Project Type**: Mobile (iOS only)  
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)  
**Constraints**: N/A  
**Scale/Scope**: Single platform feature, extends existing fullscreen map view

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (iOS only feature)
  - iOS: Domain models, repositories, ViewModels in `/iosApp` ✅
  - Web: N/A (iOS only feature)
  - Backend: Independent Node.js/Express API in `/server` (existing, unchanged)
  - NO shared compiled code between platforms ✅
  - Violation justification: _N/A - iOS only feature_

- [x] **Android MVI Architecture**: N/A - iOS only feature
  - Violation justification: _iOS only feature, Android not affected_

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - UIKit-based coordinators manage navigation ✅ (HomeCoordinator)
  - ViewModels conform to `ObservableObject` with `@Published` properties ✅
  - ViewModels communicate with coordinators via methods or closures ✅
  - SwiftUI views observe ViewModels (no business/navigation logic in views) ✅
  - Violation justification: _Fully compliant_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories
  - iOS: Repository protocols in `/iosApp/iosApp/Domain/Repositories/` ✅
  - Using existing `AnnouncementRepositoryProtocol` - no new interfaces needed
  - Violation justification: _Fully compliant - reuses existing protocol_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - iOS: Manual DI via `ServiceContainer.shared.announcementRepository` ✅
  - Coordinator injects repository into ViewModel
  - Violation justification: _Fully compliant_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - iOS: Tests in `/iosApp/iosAppTests/Features/LandingPage/Views/` ✅
  - Extend existing `FullscreenMapViewModelTests.swift`
  - Test cases: initial load, mapping to pins, error handling, gesture handling, task cancellation
  - Violation justification: _Fully compliant_

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - E2E tests not required for this feature (per spec - no E2E section)
  - Can be added in future spec for pin interaction
  - Violation justification: _E2E tests deferred to pin interaction spec_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - iOS: Swift Concurrency (`async`/`await`) ✅
  - `@MainActor` for ViewModel ✅
  - No Combine, RxSwift, or callbacks ✅
  - Violation justification: _Fully compliant_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - iOS: `accessibilityIdentifier` on map and pins ✅
  - `fullscreenMap.map`, `fullscreenMap.pin.{id}`
  - Violation justification: _Fully compliant_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Swift: SwiftDoc format for ViewModel methods ✅
  - Minimal documentation for self-explanatory methods
  - Violation justification: _Fully compliant_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - All test cases designed with Given-When-Then structure ✅
  - Test naming: `testMethod_whenCondition_shouldBehavior`
  - Violation justification: _Fully compliant_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - Server not modified
  - Violation justification: _Backend API already exists, no changes needed_

- [x] **Backend Code Quality**: N/A - Server not modified
  - Violation justification: _Backend not affected_

- [x] **Backend Dependency Management**: N/A - Server not modified
  - Violation justification: _Backend not affected_

### Web Architecture & Quality Standards (if `/webApp` affected)

- [x] **Web Technology Stack**: N/A - iOS only feature
  - Violation justification: _Web not affected_

- [x] **Web Code Quality**: N/A - iOS only feature
  - Violation justification: _Web not affected_

- [x] **Web Dependency Management**: N/A - iOS only feature
  - Violation justification: _Web not affected_

- [x] **Web Business Logic Extraction**: N/A - iOS only feature
  - Violation justification: _Web not affected_

- [x] **Web TDD Workflow**: N/A - iOS only feature
  - Violation justification: _Web not affected_

- [x] **Web Testing Strategy**: N/A - iOS only feature
  - Violation justification: _Web not affected_

- [x] **Backend Directory Structure**: N/A - Server not modified
  - Violation justification: _Backend not affected_

- [x] **Backend TDD Workflow**: N/A - Server not modified
  - Violation justification: _Backend not affected_

- [x] **Backend Testing Strategy**: N/A - Server not modified
  - Violation justification: _Backend not affected_

## Project Structure

### Documentation (this feature)

```text
specs/KAN-32-ios-fullscreen-map-fetch-pins/
├── plan.md              # This file
├── spec.md              # Feature specification
├── research.md          # Phase 0 output - technical decisions
├── data-model.md        # Phase 1 output - entity definitions
├── quickstart.md        # Phase 1 output - implementation guide
├── contracts/           # Phase 1 output
│   └── announcements-api.md  # API contract documentation
└── tasks.md             # Phase 2 output (created by /speckit.tasks)
```

### Source Code (repository root)

```text
iosApp/iosApp/
├── Domain/
│   ├── Models/
│   │   ├── Announcement.swift          # [EXISTING] Domain model with coordinate, status
│   │   ├── Coordinate.swift            # [EXISTING] Geographic coordinate
│   │   ├── AnnouncementStatus.swift    # [EXISTING] Status enum (active = missing)
│   │   └── MKCoordinateRegion+Radius.swift  # [NEW] Extension for radius calculation
│   └── Repositories/
│       └── AnnouncementRepositoryProtocol.swift  # [EXISTING] Repository protocol
├── Data/
│   └── Repositories/
│       └── AnnouncementRepository.swift  # [EXISTING] HTTP implementation
├── DI/
│   └── ServiceContainer.swift          # [EXISTING] DI container with announcementRepository
├── Features/
│   └── LandingPage/
│       ├── Coordinators/
│       │   └── HomeCoordinator.swift   # [MODIFY] Inject repository into ViewModel
│       └── Views/
│           └── FullscreenMap/
│               ├── FullscreenMapView.swift       # [MODIFY] Add pins, gesture handling
│               ├── FullscreenMapViewModel.swift  # [MODIFY] Add repository, pins state
│               └── MapPin.swift                  # [NEW] Lightweight pin model
└── iosAppTests/
    └── Features/
        └── LandingPage/
            └── Views/
                └── FullscreenMapViewModelTests.swift  # [EXTEND] Add pin fetch tests
```

**Structure Decision**: iOS-only feature extending existing fullscreen map module. No new directories needed - all changes within existing `Features/LandingPage/Views/FullscreenMap/` structure.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| _None_ | _N/A_ | _All checks passed_ |

## Estimation Update

### Re-Estimation After PLAN

| Phase | SP | Days | Confidence | Key Discovery |
|-------|-----|------|------------|---------------|
| Initial | 2 | 10.4 | ±50% | Gut feel from feature title |
| After SPEC | 2 | 10.4 | ±30% | Reduced scope (silent errors, no debounce) |
| **After PLAN** | **2** | **10.4** | **±20%** | **API/repository exist, iOS 18 camera APIs simplify gesture detection** |
| After TASKS | — | — | ±15% | [Update when tasks.md complete] |

**Key Discovery**: Existing infrastructure significantly reduces implementation effort:
- Backend API ready (`GET /api/v1/announcements?lat&lng&range`)
- iOS repository with location filtering implemented
- iOS 18 `onMapCameraChange(frequency: .onEnd)` provides clean gesture detection
- Only 3 files to modify, 2 new files to create

## Generated Artifacts

| Artifact | Path | Status |
|----------|------|--------|
| Research | `research.md` | ✅ Complete |
| Data Model | `data-model.md` | ✅ Complete |
| API Contract | `contracts/announcements-api.md` | ✅ Complete |
| Quickstart | `quickstart.md` | ✅ Complete |
| Tasks | `tasks.md` | 🔜 Next phase |
