# Implementation Plan: iOS Prepare Found Pet Flow as Missing Pet Copy

**Branch**: `KAN-34-ios-prepare-found-flow-as-missing-copy` | **Date**: 2026-01-09 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/KAN-34-ios-prepare-found-flow-as-missing-copy/spec.md`

## Summary

Create temporary scaffolding for Report Found Pet flow by copying the existing Report Missing Pet flow structure. This prepares the codebase for future divergence while enabling immediate entry point from announcements list. All MissingPet* types become FoundPet*, all accessibilityIdentifiers change to foundPet/reportFoundPet prefix, and the existing wiring (button, coordinator) is activated.

## Technical Context

**Language/Version**: Swift 5.9+ (iOS 18+ target)
**Primary Dependencies**: UIKit (coordinators), SwiftUI (views), Foundation
**Storage**: N/A (uses existing MissingPetReportFlowState pattern - no backend changes)
**Testing**: XCTest with Swift Concurrency (async/await)
**Target Platform**: iOS 18+ (iPhone 16 Simulator for tests)
**Project Type**: Mobile (iOS only)
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)
**Constraints**: N/A
**Scale/Scope**: N/A

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is iOS-only scaffolding. Backend, Android, Web checks marked N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (no Android changes)
  - iOS: All changes in `/iosApp` - coordinator, views, viewModels, tests
  - Web: N/A (no web changes)
  - Backend: N/A (no backend changes)
  - NO shared compiled code between platforms
  - Violation justification: _N/A - compliant_

- [x] **Android MVI Architecture**: N/A - iOS only feature
  - Violation justification: _N/A - Android not affected_

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - UIKit-based coordinator (`FoundPetReportCoordinator`) manages navigation and creates `UIHostingController` instances
  - ViewModels conform to `ObservableObject` with `@Published` properties (copied pattern)
  - ViewModels communicate with coordinators via closures (`onNext`, `onBack`, `onReportSent`)
  - SwiftUI views observe ViewModels (no business/navigation logic in views)
  - Violation justification: _N/A - compliant (direct copy of existing MVVM-C pattern)_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - iOS: Reuses existing protocols (`LocationServiceProtocol`, `PhotoAttachmentCacheProtocol`, `AnnouncementSubmissionServiceProtocol`)
  - No new repository interfaces needed (scaffolding reuses existing services)
  - Violation justification: _N/A - compliant_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - iOS: Manual DI via constructor injection in `FoundPetReportCoordinator` (same pattern as `MissingPetReportCoordinator`)
  - Violation justification: _N/A - compliant_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - iOS: Tests copied from `ReportMissingPet/` to `ReportFoundPet/` with renamed types
  - Coverage maintained by copying comprehensive test suite (8+ test files)
  - Violation justification: _N/A - compliant (test copy maintains coverage)_

- [x] **End-to-End Tests**: N/A for this iteration
  - E2E tests deferred - this is internal scaffolding that will be tested when Found flow diverges
  - Violation justification: _Scaffolding only - E2E tests added when flow has unique behavior_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor`
  - No Combine, RxSwift, or callback-based patterns
  - Violation justification: _N/A - compliant (pattern preserved from Missing flow)_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - iOS: All `accessibilityIdentifier` values renamed from `reportMissingPet.*`/`missingPet.*` to `reportFoundPet.*`/`foundPet.*`
  - Entry button: `animalList.reportFoundButton` (already defined in existing code)
  - Violation justification: _N/A - compliant_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - SwiftDoc format (`/// ...`)
  - Documentation copied and updated for Found context
  - Violation justification: _N/A - compliant_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Test structure preserved from Missing flow tests
  - Test names updated to `Found*` pattern
  - Violation justification: _N/A - compliant_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - no backend changes
- [x] **Backend Code Quality**: N/A - no backend changes
- [x] **Backend Dependency Management**: N/A - no backend changes
- [x] **Backend Directory Structure**: N/A - no backend changes
- [x] **Backend TDD Workflow**: N/A - no backend changes
- [x] **Backend Testing Strategy**: N/A - no backend changes

### Web Architecture & Quality Standards (if `/webApp` affected)

- [x] **Web Technology Stack**: N/A - no web changes
- [x] **Web Code Quality**: N/A - no web changes
- [x] **Web Dependency Management**: N/A - no web changes
- [x] **Web Business Logic Extraction**: N/A - no web changes
- [x] **Web TDD Workflow**: N/A - no web changes
- [x] **Web Testing Strategy**: N/A - no web changes

## Project Structure

### Documentation (this feature)

```text
specs/KAN-34-ios-prepare-found-flow-as-missing-copy/
├── plan.md              # This file
├── research.md          # Phase 0 output - minimal (no unknowns)
├── data-model.md        # N/A - no new data models
├── quickstart.md        # N/A - scaffolding copy
├── contracts/           # N/A - no API changes
└── tasks.md             # Phase 2 output (/speckit.tasks command)
```

### Source Code (repository root)

```text
iosApp/iosApp/Features/ReportMissingAndFoundPet/
├── Common/                           # UNCHANGED - shared components
│   ├── Components/                   # Form, Photo, Toast components
│   ├── Helpers/                      # PhotoSelectionProcessor, etc.
│   ├── Models/                       # Shared models
│   └── Services/                     # PhotoAttachmentCache, ToastScheduler
│
├── ReportMissingPet/                 # UNCHANGED - existing Missing flow
│   ├── Coordinators/
│   │   └── MissingPetReportCoordinator.swift
│   ├── Models/
│   │   └── MissingPetReportFlowState.swift
│   └── Views/
│       ├── AnimalDescription/
│       ├── ChipNumber/
│       ├── ContactDetails/
│       ├── Photo/
│       └── Summary/
│
└── ReportFoundPet/                   # NEW - copy of ReportMissingPet
    ├── Coordinators/
    │   └── FoundPetReportCoordinator.swift           # Copy + rename
    ├── Models/
    │   └── FoundPetReportFlowState.swift             # Copy + rename
    └── Views/
        ├── AnimalDescription/
        │   ├── FoundPetAnimalDescriptionView.swift       # Copy + rename
        │   └── FoundPetAnimalDescriptionViewModel.swift  # Copy + rename
        ├── ChipNumber/
        │   ├── FoundPetChipNumberView.swift              # Copy + rename
        │   └── FoundPetChipNumberViewModel.swift         # Copy + rename
        ├── ContactDetails/
        │   ├── FoundPetContactDetailsView.swift          # Copy + rename
        │   └── FoundPetContactDetailsViewModel.swift     # Copy + rename
        ├── Photo/
        │   ├── FoundPetPhotoView.swift                   # Copy + rename
        │   └── FoundPetPhotoViewModel.swift              # Copy + rename
        └── Summary/
            ├── FoundPetSummaryView.swift                 # Copy + rename
            ├── FoundPetSummaryView+Constants.swift       # Copy + rename
            └── FoundPetSummaryViewModel.swift            # Copy + rename

iosApp/iosAppTests/Features/ReportMissingAndFoundPet/
├── Common/                           # UNCHANGED - shared test support
│   ├── Helpers/
│   ├── Models/
│   ├── Services/
│   └── Support/
│
├── ReportMissingPet/                 # UNCHANGED - existing Missing tests
│
└── ReportFoundPet/                   # NEW - copy of ReportMissingPet tests
    ├── FoundPetAnimalDescriptionViewModelTests.swift
    ├── FoundPetChipNumberViewModelTests.swift
    ├── FoundPetContactDetailsViewModelErrorHandlingTests.swift
    ├── FoundPetContactDetailsViewModelRewardTests.swift
    ├── FoundPetContactDetailsViewModelTests.swift
    ├── FoundPetContactDetailsViewModelValidationTests.swift
    ├── FoundPetPhotoViewModelTests.swift
    ├── FoundPetSummaryViewModelTests.swift
    └── Models/
        └── FoundPetReportFlowStateTests.swift
```

### Files to Modify (Wiring)

```text
iosApp/iosApp/Features/AnnouncementList/
├── Views/
│   └── AnnouncementListView.swift    # Uncomment Found button
└── Coordinators/
    └── AnnouncementListCoordinator.swift  # Implement showReportFound()
```

**Structure Decision**: iOS-only mobile scaffolding. Creates parallel `ReportFoundPet/` directory mirroring `ReportMissingPet/` structure. Shared components in `Common/` are reused without duplication.

## Complexity Tracking

> No constitution violations requiring justification.

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| _None_ | — | — |

## Implementation Approach

### Naming Convention

All type renames follow pattern:
- `MissingPet*` → `FoundPet*` (class/struct/enum names)
- `missingPet*` → `foundPet*` (accessibilityIdentifier values)
- `reportMissingPet.*` → `reportFoundPet.*` (accessibilityIdentifier values)

### Files to Copy and Rename

**Source directory**: `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportMissingPet/`
**Target directory**: `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/`

| Source File | Target File |
|-------------|-------------|
| `Coordinators/MissingPetReportCoordinator.swift` | `Coordinators/FoundPetReportCoordinator.swift` |
| `Models/MissingPetReportFlowState.swift` | `Models/FoundPetReportFlowState.swift` |
| `Views/ChipNumber/MissingPetChipNumberView.swift` | `Views/ChipNumber/FoundPetChipNumberView.swift` |
| `Views/ChipNumber/MissingPetChipNumberViewModel.swift` | `Views/ChipNumber/FoundPetChipNumberViewModel.swift` |
| `Views/Photo/MissingPetPhotoView.swift` | `Views/Photo/FoundPetPhotoView.swift` |
| `Views/Photo/MissingPetPhotoViewModel.swift` | `Views/Photo/FoundPetPhotoViewModel.swift` |
| `Views/AnimalDescription/MissingPetAnimalDescriptionView.swift` | `Views/AnimalDescription/FoundPetAnimalDescriptionView.swift` |
| `Views/AnimalDescription/MissingPetAnimalDescriptionViewModel.swift` | `Views/AnimalDescription/FoundPetAnimalDescriptionViewModel.swift` |
| `Views/ContactDetails/MissingPetContactDetailsView.swift` | `Views/ContactDetails/FoundPetContactDetailsView.swift` |
| `Views/ContactDetails/MissingPetContactDetailsViewModel.swift` | `Views/ContactDetails/FoundPetContactDetailsViewModel.swift` |
| `Views/Summary/MissingPetSummaryView.swift` | `Views/Summary/FoundPetSummaryView.swift` |
| `Views/Summary/MissingPetSummaryView+Constants.swift` | `Views/Summary/FoundPetSummaryView+Constants.swift` |
| `Views/Summary/MissingPetSummaryViewModel.swift` | `Views/Summary/FoundPetSummaryViewModel.swift` |

**Total**: 13 source files → 13 target files

### Tests to Copy and Rename

**Source directory**: `iosApp/iosAppTests/Features/ReportMissingAndFoundPet/ReportMissingPet/`
**Target directory**: `iosApp/iosAppTests/Features/ReportMissingAndFoundPet/ReportFoundPet/`

| Source Test File | Target Test File |
|------------------|------------------|
| `MissingPetChipNumberViewModelTests.swift` | `FoundPetChipNumberViewModelTests.swift` |
| `MissingPetPhotoViewModelTests.swift` | `FoundPetPhotoViewModelTests.swift` |
| `MissingPetAnimalDescriptionViewModelTests.swift` | `FoundPetAnimalDescriptionViewModelTests.swift` |
| `MissingPetContactDetailsViewModelTests.swift` | `FoundPetContactDetailsViewModelTests.swift` |
| `MissingPetContactDetailsViewModelValidationTests.swift` | `FoundPetContactDetailsViewModelValidationTests.swift` |
| `MissingPetContactDetailsViewModelErrorHandlingTests.swift` | `FoundPetContactDetailsViewModelErrorHandlingTests.swift` |
| `MissingPetContactDetailsViewModelRewardTests.swift` | `FoundPetContactDetailsViewModelRewardTests.swift` |
| `MissingPetSummaryViewModelTests.swift` | `FoundPetSummaryViewModelTests.swift` |
| `Models/MissingPetReportFlowStateTests.swift` | `Models/FoundPetReportFlowStateTests.swift` |

**Total**: 9 test files → 9 target test files

### String Replacements in Copied Files

Each copied file requires these search-and-replace operations:

1. **Type names**: `MissingPet` → `FoundPet` (case-sensitive)
2. **Variable names**: `missingPet` → `foundPet` (case-sensitive)
3. **Accessibility identifiers**: `reportMissingPet.` → `reportFoundPet.`
4. **Accessibility identifiers**: `missingPet.` → `foundPet.`
5. **Documentation**: Update doc comments referencing "Missing" to "Found"

### Wiring Changes

1. **AnnouncementListView.swift**: Uncomment the Report Found Animal button block (lines 55-64)

2. **AnnouncementListCoordinator.swift**: Replace stub `showReportFound()` with real implementation:
   ```swift
   private func showReportFound() {
       guard let navigationController = navigationController else { return }
       
       let reportCoordinator = FoundPetReportCoordinator(
           parentNavigationController: navigationController,
           locationService: locationService,
           photoAttachmentCache: photoAttachmentCache,
           announcementSubmissionService: announcementSubmissionService
       )
       reportCoordinator.parentCoordinator = self
       
       reportCoordinator.onReportSent = { [weak self] in
           self?.announcementListViewModel?.requestToRefreshData()
       }
       
       childCoordinators.append(reportCoordinator)
       
       Task { @MainActor in
           await reportCoordinator.start(animated: true)
       }
   }
   ```

### Localization Note

Existing localization keys will be reused in this iteration:
- `L10n.AnnouncementList.Button.reportFound` - already exists
- `L10n.ReportMissingPet.*` - reused as-is in Found flow (content unchanged per spec)

Future iteration will add `L10n.ReportFoundPet.*` keys when flow content diverges.

## Estimation Update

| Phase | SP | Days | Confidence | Key Discovery |
|-------|-----|------|------------|---------------|
| Initial | 1 | 5.2 | ±50% | Gut feel from feature title - iOS-only scaffolding |
| After SPEC | 1 | 5.2 | ±30% | Confirmed: copy + rename + wire entry point |
| After PLAN | 1 | 5.2 | ±20% | 13 files + 9 tests + 2 wiring changes; straightforward copy |
| After TASKS | — | — | ±15% | [Update when tasks.md complete] |

**Rationale**: Estimate unchanged. This is mechanical copy-rename work with minimal logic changes. The bulk of effort is ensuring all type names and identifiers are consistently renamed.
