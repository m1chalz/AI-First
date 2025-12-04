# Implementation Plan: iOS Animal Photo Screen

**Branch**: `028-ios-animal-photo-screen` | **Date**: 2025-11-27 | **Spec**: [`specs/028-ios-animal-photo-screen/spec.md`](./spec.md)  
**Input**: Feature specification from `specs/028-ios-animal-photo-screen/spec.md`

**Note**: Generated via `/speckit.plan` following `.specify/templates/commands/plan.md`.

## Summary

Implement the iOS Report Missing Pet photo step (2/4) so it mirrors Figma nodes `297:7991` (empty state) and `297:8041` (confirmation). The screen must use SwiftUI `PhotosPicker` (iOS 16+ requirement) scoped to supported formats, persist the selected attachment inside the draft session, enforce the mandatory photo rule via a toast, and surface resilient UX for picker cancellation, iCloud downloads, and transfer errors. We will extend the existing MVVM-C stack with a richer `PhotoViewModel`, SwiftUI components for the empty + confirmation states, a disk-backed attachment cache, and stateless toast/banner handling so the confirmation card survives navigation/backgrounding.

## Technical Context

**Language/Version**: Swift 5.0 toolchain (per Xcode project) targeting iOS 15+ modal flow  
**Primary Dependencies**: SwiftUI, UIKit `NavigationBackHiding`, SwiftUI `PhotosPicker` (`Transferable`), SwiftGen, XCTest, Appium+Cucumber (`@ios` tag)  
**Storage**: FileManager-backed `PhotoAttachmentCache` that writes image data to `~/Library/Caches/PetSpot/ReportMissingPet/<uuid>.img` plus lightweight metadata inside `ReportMissingPetFlowState` so selection persists through navigation/backgrounding.  
**Testing**: XCTest (async/await ViewModel + cache tests), SwiftUI previews for stateless components, Appium+Cucumber scenario validating browse/select/remove/mandatory gating  
**Target Platform**: iOS 16+ Report Missing Pet flow (modal UINavigationController owned by `ReportMissingPetCoordinator`)  
**Project Type**: Mobile (SwiftUI front-end with MVVM-C + manual DI)  
**Performance Goals**: Maintain 60 fps when rendering confirmation card and load cached card within 100 ms after returning to screen (UI responsiveness target)  
**Constraints**: UI must match Figma nodes `297:7991` and `297:8041` pixel-perfectly, rely solely on Photos picker (no camera capture), enforce Apple HIG copy and accessibility identifiers, disallow unsupported mimetypes  
**Scale/Scope**: Single iOS screen plus FlowState persistence, PhotosPicker integration, and toast-driven enforcement; Android, Web, and backend remain untouched

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently  
  ✅ Scope limited to `/iosApp`; feature reuses the ReportMissingPet coordinator without introducing shared Kotlin/Web/backend modules.  
  Violation justification: _N/A_

- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop  
  ✅ Android codebase is untouched; no divergence introduced by this iOS-only feature.  
  Violation justification: _N/A_

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern  
  ✅ Photo screen keeps coordinator-managed navigation, ViewModel remains `ObservableObject`, SwiftUI view stays declarative with no navigation logic.  
  Violation justification: _N/A_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)  
  ✅ Plan introduces `PhotoAttachmentCacheProtocol` + `PhotoPickerCoordinating` abstractions so ViewModel/coordinator do not depend on concrete implementations.  
  Violation justification: _N/A_

- [x] **Dependency Injection**: Plan includes DI setup for each platform  
  ✅ Manual DI inside `ServiceContainer` creates attachment cache + picker coordinator and injects them into `PhotoViewModel`; no other platforms affected.  
  Violation justification: _N/A_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform  
  ✅ Adds XCTest coverage for the PhotoViewModel state machine + attachment cache; other platform test suites unchanged.  
  Violation justification: _N/A_

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories  
  ✅ New Appium+Cucumber scenario tagged `@ios @missingPetPhoto` covers browse/select/remove/mandatory gating.  
  Violation justification: _N/A_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform  
  ✅ PhotoViewModel uses Swift Concurrency (async/await) for picker bridging and cache IO; no callback-based permission flow is required.  
  Violation justification: _N/A_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements  
  ✅ All controls (`Browse`, `Continue`, `Remove`, toast CTA) expose `.accessibilityIdentifier("animalPhoto.*")` per FR-009.  
  Violation justification: _N/A_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed  
  ✅ New protocols (`PhotoAttachmentCacheProtocol`, `PhotoPickerCoordinating`) get concise SwiftDoc describing responsibilities.  
  Violation justification: _N/A_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention  
  ✅ Upcoming XCTest/Appium scenarios explicitly separate Given/When/Then and use descriptive names.  
  Violation justification: _N/A_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: Plan uses modern Node.js stack for `/server` module  
  ✅ N/A – backend API not touched.  
  Violation justification: _N/A_

- [x] **Backend Code Quality**: Plan enforces quality standards for `/server` code  
  ✅ N/A – no backend code modified.  
  Violation justification: _N/A_

- [x] **Backend Dependency Management**: Plan minimizes dependencies in `/server/package.json`  
  ✅ N/A – backend dependencies unchanged.  
  Violation justification: _N/A_

- [x] **Backend Directory Structure**: Plan follows standardized layout in `/server/src/`  
  ✅ N/A – `/server` structure unaffected.  
  Violation justification: _N/A_

- [x] **Backend TDD Workflow**: Plan follows Test-Driven Development (Red-Green-Refactor)  
  ✅ N/A – backend TDD scope not triggered.  
  Violation justification: _N/A_

- [x] **Backend Testing Strategy**: Plan includes comprehensive test coverage for `/server`  
  ✅ N/A – backend integration/unit suites remain untouched.  
  Violation justification: _N/A_

> **Gate Result (Post Phase 1)**: PASS – iOS-only design adheres to all constitution principles (v2.3.0).

## Project Structure

### Documentation (this feature)

```text
specs/028-ios-animal-photo-screen/
├── plan.md              # This file (/speckit.plan)
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
└── tasks.md             # Phase 2 output (/speckit.tasks)
```

### Source Code (repository root)

```text
iosApp/
├── iosApp/Features/ReportMissingPet/
│   ├── Models/
│   │   ├── ReportMissingPetFlowState.swift
│   │   └── PhotoAttachmentState.swift              # new attachment metadata + status
│   ├── Services/
│   │   └── PhotoAttachmentCache.swift              # disk-backed cache abstraction
│   ├── Views/
│   │   ├── PhotoViewModel.swift
│   │   ├── PhotoView.swift
│   │   └── Components/
│   │       ├── AnimalPhotoEmptyStateView.swift
│   │       └── AnimalPhotoConfirmationCard.swift
│   ├── Coordinators/
│   │   └── ReportMissingPetCoordinator.swift
├── iosApp/DI/
│   └── ServiceContainer.swift                      # wires cache + picker coordinator
├── iosApp/Resources/
│   ├── en.lproj/Localizable.strings
│   └── pl.lproj/Localizable.strings
└── iosAppTests/Features/ReportMissingPet/
    ├── Models/PhotoAttachmentStateTests.swift
    ├── Services/PhotoAttachmentCacheTests.swift
    └── Views/PhotoViewModelTests.swift

e2e-tests/
└── src/test/resources/features/mobile/
    └── missing_pet_photo.feature                   # new Appium+Cucumber scenario
```

**Structure Decision**: Extend `/iosApp/Features/ReportMissingPet` with dedicated models, picker/cache services, and SwiftUI components so the feature stays self-contained. Wiring lives in `iosApp/DI/ServiceContainer.swift`, new localized copy is stored under `/iosApp/Resources`, and regression/E2E coverage stays in `/iosAppTests` plus `/e2e-tests/src/test/resources/features/mobile`.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|--------------------------------------|
| _None_ | – | – |
