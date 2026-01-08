# Implementation Plan: iOS Project Structure Refactoring for Report Missing & Found Pet

**Branch**: `KAN-34-ios-prepare-project-structure` | **Date**: 2026-01-08 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/KAN-34-ios-prepare-project-structure/spec.md`

## Summary

Restructure iOS `ReportMissingPet` feature directory to prepare for future `ReportFoundPet` implementation. Create parent `ReportMissingAndFoundPet/` directory containing `ReportMissingPet/` (coordinator + full-screen views) and `Common/` (shared helpers, services, models, components). Rename all full-screen views and coordinator with `MissingPet` prefix for clear ownership.

## Technical Context

**Language/Version**: Swift 5.9+, iOS 18+
**Primary Dependencies**: SwiftUI, UIKit (coordinators only), XCTest
**Storage**: N/A (refactoring only, no new storage)
**Testing**: XCTest with Swift Concurrency (async/await)
**Target Platform**: iOS 18+ (iPhone)
**Project Type**: Mobile (iOS only)
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)
**Constraints**: N/A (refactoring only)
**Scale/Scope**: 41 production files + 16 test files to reorganize

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is an iOS-only refactoring task. Backend, Android, Web checks marked as N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (no changes)
  - iOS: Restructuring within `/iosApp` only - maintains independence
  - Web: N/A (no changes)
  - Backend: N/A (no changes)
  - NO shared compiled code between platforms ✓
  - Violation justification: _N/A - compliant_

- [ ] **Android MVI Architecture**: N/A - no Android changes

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - UIKit-based coordinators manage navigation ✓ (ReportMissingPetCoordinator → MissingPetReportCoordinator)
  - ViewModels conform to `ObservableObject` with `@Published` properties ✓
  - ViewModels communicate with coordinators via closures ✓ (onNext, onBack callbacks)
  - SwiftUI views observe ViewModels (no business/navigation logic in views) ✓
  - Violation justification: _N/A - architecture preserved after refactoring_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - iOS: Repository protocols in `/iosApp/iosApp/Domain/Repositories/` ✓
  - Services use protocol pattern (PhotoAttachmentCacheProtocol, etc.) ✓
  - Violation justification: _N/A - no new interfaces needed; existing preserved_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - iOS: MUST use manual DI - setup in `/iosApp/iosApp/DI/` ✓
  - Coordinator injects dependencies via constructor ✓
  - Violation justification: _N/A - DI pattern preserved_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - iOS: Tests in `/iosApp/iosAppTests/` ✓
  - All existing tests will be moved to match new structure
  - Coverage will remain identical (no functional changes)
  - Violation justification: _N/A - test coverage preserved_

- [ ] **End-to-End Tests**: N/A - no functional changes requiring new E2E tests
  - Violation justification: _Refactoring only - existing E2E tests remain valid_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor` ✓
  - No Combine usage in affected code ✓
  - Violation justification: _N/A - async patterns preserved_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - iOS: `accessibilityIdentifier` modifier on all interactive views ✓
  - All existing test identifiers preserved (no UI changes)
  - Violation justification: _N/A - no new UI elements_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Swift: SwiftDoc format (`/// ...`) ✓
  - Existing documentation preserved after refactoring
  - Violation justification: _N/A - documentation preserved_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Existing tests already follow convention
  - Test structure preserved after file moves/renames
  - Violation justification: _N/A - test structure preserved_

### Backend Architecture & Quality Standards (if `/server` affected)

- [ ] **Backend Technology Stack**: N/A - `/server` not affected

- [ ] **Backend Code Quality**: N/A - `/server` not affected

- [ ] **Backend Dependency Management**: N/A - `/server` not affected

### Web Architecture & Quality Standards (if `/webApp` affected)

- [ ] **Web Technology Stack**: N/A - `/webApp` not affected

- [ ] **Web Code Quality**: N/A - `/webApp` not affected

- [ ] **Web Dependency Management**: N/A - `/webApp` not affected

- [ ] **Web Business Logic Extraction**: N/A - `/webApp` not affected

- [ ] **Web TDD Workflow**: N/A - `/webApp` not affected

- [ ] **Web Testing Strategy**: N/A - `/webApp` not affected

- [ ] **Backend Directory Structure**: N/A - `/server` not affected

- [ ] **Backend TDD Workflow**: N/A - `/server` not affected

- [ ] **Backend Testing Strategy**: N/A - `/server` not affected

## Project Structure

### Documentation (this feature)

```text
specs/KAN-34-ios-prepare-project-structure/
├── plan.md              # This file
├── research.md          # File inventory and technical decisions
├── data-model.md        # N/A (refactoring only)
├── quickstart.md        # N/A (refactoring only)
├── contracts/           # N/A (refactoring only)
└── tasks.md             # Will be generated by /speckit.tasks
```

### Source Code (repository root)

**BEFORE Refactoring**:
```text
iosApp/iosApp/Features/
├── ReportMissingPet/
│   ├── Coordinators/
│   │   └── ReportMissingPetCoordinator.swift
│   ├── Helpers/
│   │   ├── AnimalPhotoTransferable.swift
│   │   ├── MicrochipNumberFormatter.swift
│   │   └── PhotoSelectionProcessor.swift
│   ├── Models/
│   │   ├── PhotoAttachmentMetadata+MimeType.swift
│   │   ├── PhotoAttachmentState.swift
│   │   ├── PhotoSelection.swift
│   │   └── ReportMissingPetFlowState.swift
│   ├── Services/
│   │   ├── PhotoAttachmentCache.swift
│   │   └── ToastScheduler.swift
│   └── Views/
│       ├── AnimalDescription/
│       │   ├── AnimalDescriptionView.swift
│       │   ├── AnimalDescriptionViewModel.swift
│       │   ├── Components/ (12 form component files)
│       │   └── (3 helper type files)
│       ├── ChipNumber/ (2 files)
│       ├── Components/ (2 Toast files)
│       ├── ContactDetails/ (2 files)
│       ├── Photo/
│       │   ├── PhotoView.swift
│       │   ├── PhotoViewModel.swift
│       │   └── Components/ (3 photo browser files)
│       └── Summary/ (3 files)
```

**AFTER Refactoring**:
```text
iosApp/iosApp/Features/
├── ReportMissingAndFoundPet/           # NEW parent directory
│   ├── Common/                          # NEW shared components
│   │   ├── Components/
│   │   │   ├── Form/                    # 12 form component files
│   │   │   ├── Photo/                   # 3 photo browser files
│   │   │   └── Toast/                   # 2 Toast files
│   │   ├── Helpers/                     # 3 helper files
│   │   ├── Models/                      # 6 model files (shared only)
│   │   └── Services/                    # 2 service files
│   └── ReportMissingPet/                # MOVED + restructured
│       ├── Coordinators/
│       │   └── MissingPetReportCoordinator.swift  # RENAMED
│       ├── Models/
│       │   └── ReportMissingPetFlowState.swift    # STAYS (flow-specific)
│       └── Views/                       # Flattened (no subdirs)
│           ├── MissingPetAnimalDescriptionView.swift      # RENAMED
│           ├── MissingPetAnimalDescriptionViewModel.swift # RENAMED
│           ├── MissingPetChipNumberView.swift             # RENAMED
│           ├── MissingPetChipNumberViewModel.swift        # RENAMED
│           ├── MissingPetContactDetailsView.swift         # RENAMED
│           ├── MissingPetContactDetailsViewModel.swift    # RENAMED
│           ├── MissingPetPhotoView.swift                  # RENAMED
│           ├── MissingPetPhotoViewModel.swift             # RENAMED
│           ├── MissingPetSummaryView.swift                # RENAMED
│           ├── MissingPetSummaryView+Constants.swift      # RENAMED
│           └── MissingPetSummaryViewModel.swift           # RENAMED
```

**Test Directory AFTER Refactoring**:
```text
iosApp/iosAppTests/Features/
├── ReportMissingAndFoundPet/           # NEW parent directory
│   ├── Common/                          # Shared component tests
│   │   ├── Helpers/
│   │   │   ├── MicrochipNumberFormatterTests.swift
│   │   │   └── PhotoSelectionProcessorTests.swift
│   │   ├── Models/
│   │   │   └── PhotoAttachmentStateTests.swift
│   │   ├── Services/
│   │   │   ├── AnnouncementSubmissionServiceTests.swift
│   │   │   └── PhotoAttachmentCacheTests.swift
│   │   └── Support/                     # Test fakes
│   │       ├── PhotoAttachmentCacheFake.swift
│   │       └── ToastSchedulerFake.swift
│   └── ReportMissingPet/                # Feature-specific tests
│       ├── Models/
│       │   └── ReportMissingPetFlowStateTests.swift  # STAYS (flow-specific)
│       ├── MissingPetAnimalDescriptionViewModelTests.swift
│       ├── MissingPetChipNumberViewModelTests.swift
│       ├── MissingPetContactDetailsViewModelTests.swift
│       ├── MissingPetContactDetailsViewModelValidationTests.swift
│       ├── MissingPetContactDetailsViewModelErrorHandlingTests.swift
│       ├── MissingPetContactDetailsViewModelRewardTests.swift
│       ├── MissingPetPhotoViewModelTests.swift
│       └── MissingPetSummaryViewModelTests.swift
```

**Structure Decision**: iOS-only mobile refactoring. Production files reorganized into `ReportMissingAndFoundPet/` parent with `Common/` (26 shared files) and `ReportMissingPet/` (15 flow-specific files incl. FlowState) subdirectories. Test files mirror production structure.

## Complexity Tracking

> **No violations requiring justification.** Refactoring preserves existing architecture.

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| N/A | N/A | N/A |

## Implementation Approach

### Atomic Commits (revised from IC-001)

> **Note**: Original spec suggested 3 commits, but class renames and reference updates cannot be separated (Xcode "Refactor → Rename" does both atomically). Consolidated to 2 commits.

**Commit 1: Directory Structure (Move Files)**
- Create `ReportMissingAndFoundPet/` parent directory
- Move `ReportMissingPet/` under it
- Create `Common/` with subdirectories
- Move 26 shared files to Common/ (FlowState stays in ReportMissingPet/)
- Move 16 test files to new structure
- ✅ Build + Test + Manual smoke test

**Commit 2: Class Renames + Reference Updates**
- Use Xcode "Refactor → Rename" for each class (updates all references automatically)
- Rename `ReportMissingPetCoordinator` → `MissingPetReportCoordinator`
- Rename 5 full-screen Views with `MissingPet` prefix
- Rename 5 ViewModels with `MissingPet` prefix
- Rename `SummaryView+Constants` → `MissingPetSummaryView+Constants`
- Rename test files to match production class names
- ✅ Build + Test + Manual smoke test

### Verification After Each Commit

1. **Build**: `xcodebuild build -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16'`
2. **Test**: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
3. **Manual Smoke Test**: Run app, complete Report Missing Pet flow (all 5 steps)

### Rollback Strategy

If any commit fails verification:
```bash
git reset --hard HEAD~1
```
Feature branch allows history rewrite per IC-005.

## Dependencies

- **File Inventory**: See `research.md` for complete 41-file mapping (26 to Common/, 15 stay in ReportMissingPet/)
- **Test File Inventory**: See `research.md` for complete 16-file mapping (7 to Common/, 9 to ReportMissingPet/)
- **Xcode**: All file operations MUST be performed in Xcode IDE (not filesystem)

## Estimation Update

| Phase | SP | Days | Confidence | Key Discovery |
|-------|-----|------|------------|---------------|
| Initial | 2 | 10.4 | ±50% | Gut feel from feature title - iOS-only refactoring task |
| After SPEC | 2 | 10.4 | ±30% | 41 production files + 16 test files; straightforward moves |
| After PLAN | 2 | 10.4 | ±20% | Clear atomic commit strategy; no API changes; Xcode-only ops |
| After TASKS | — | — | ±15% | [Update when tasks.md complete] |

**Confidence**: Estimate holds at 2 SP. Task is well-defined with clear file inventory. Risk is primarily in Xcode project file handling, mitigated by atomic commits and verification after each.
