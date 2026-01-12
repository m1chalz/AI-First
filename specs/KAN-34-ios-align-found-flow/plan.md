# Implementation Plan: iOS Align Found Flow (3-step)

**Branch**: `KAN-34-ios-align-found-flow` | **Date**: 2026-01-09 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/KAN-34-ios-align-found-flow/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Restructure the iOS "Report Found Animal" wizard from 4 steps to 3 steps:
1. **Upload photo** (new entry point)
2. **Pet details** (combines microchip + description, with required location)
3. **Contact information** (adds optional caregiver phone + current address)

Key changes:
- Merge microchip field into Pet details screen
- Make location (lat/long) required for Found flow
- Add two iOS-only optional fields (not sent to backend)
- Update localization strings per FR-017
- **Remove Summary screen** - flow exits immediately after successful submission

## Technical Context

**Language/Version**: Swift 5.9, iOS 18+
**Primary Dependencies**: SwiftUI, UIKit (coordinators), XCTest
**Storage**: In-memory `FoundPetReportFlowState` (session-scoped)
**Testing**: XCTest with Swift Concurrency (async/await)
**Target Platform**: iOS 18+, iPhone 16 Simulator
**Project Type**: Mobile (iOS)
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)
**Constraints**: Offline retry UX already handled by existing submission service
**Scale/Scope**: Single feature, ~10 files modified/created, ~500 lines of code

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is an iOS-only feature. Backend, Android, Web checks marked as N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - iOS: Domain models, repositories, ViewModels in `/iosApp` ✅
  - Android: N/A (no Android changes)
  - Web: N/A (no web changes)
  - Backend: N/A (no backend changes per FR-016)
  - NO shared compiled code between platforms ✅
  - Violation justification: _N/A - compliant_

- [x] **Android MVI Architecture**: N/A - iOS-only feature
  - Violation justification: _N/A - no Android changes_

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern ✅
  - UIKit-based coordinators manage navigation (`FoundPetReportCoordinator`) ✅
  - ViewModels conform to `ObservableObject` with `@Published` properties ✅
  - ViewModels communicate with coordinators via closures (`onNext`, `onBack`, `onContinue`) ✅
  - SwiftUI views observe ViewModels (no business/navigation logic in views) ✅
  - Violation justification: _N/A - compliant_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - iOS: `PhotoAttachmentCacheProtocol`, `LocationServiceProtocol`, `AnnouncementSubmissionServiceProtocol` ✅
  - Violation justification: _N/A - compliant_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - iOS: MUST use manual DI - coordinator injects dependencies via initializers ✅
  - Violation justification: _N/A - compliant_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - iOS: Tests in `/iosApp/iosAppTests/` ✅
  - New `FoundPetPetDetailsViewModelTests.swift` planned
  - Updated tests for `FoundPetContactDetailsViewModelTests.swift`
  - Coverage target: 80% line + branch coverage ✅
  - Violation justification: _N/A - compliant_

- [x] **End-to-End Tests**: N/A for this feature
  - This is UI restructuring with existing backend - E2E tests optional
  - Violation justification: _E2E tests not required for iOS-only UI restructuring that doesn't change API behavior_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor` ✅
  - No Combine usage ✅
  - Violation justification: _N/A - compliant_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - iOS: `accessibilityIdentifier` modifier on all interactive views ✅
  - Existing patterns reused: `reportFoundPet.progressIndicator`, `reportFoundPet.backButton`
  - New identifiers for Pet details fields planned
  - Violation justification: _N/A - compliant_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Swift: SwiftDoc format (`/// ...`) ✅
  - Documentation for complex logic only (existing pattern) ✅
  - Violation justification: _N/A - compliant_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then) ✅
  - Test names follow Swift conventions (`test{Method}_when{Condition}_should{ExpectedBehavior}`) ✅
  - Violation justification: _N/A - compliant_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - no backend changes
  - Violation justification: _N/A - /server not affected per FR-016_

- [x] **Backend Code Quality**: N/A - no backend changes
  - Violation justification: _N/A - /server not affected_

- [x] **Backend Dependency Management**: N/A - no backend changes
  - Violation justification: _N/A - /server not affected_

- [x] **Backend Directory Structure**: N/A - no backend changes
  - Violation justification: _N/A - /server not affected_

- [x] **Backend TDD Workflow**: N/A - no backend changes
  - Violation justification: _N/A - /server not affected_

- [x] **Backend Testing Strategy**: N/A - no backend changes
  - Violation justification: _N/A - /server not affected_

### Web Architecture & Quality Standards (if `/webApp` affected)

- [x] **Web Technology Stack**: N/A - iOS-only feature
  - Violation justification: _N/A - /webApp not affected_

- [x] **Web Code Quality**: N/A - iOS-only feature
  - Violation justification: _N/A - /webApp not affected_

- [x] **Web Dependency Management**: N/A - iOS-only feature
  - Violation justification: _N/A - /webApp not affected_

- [x] **Web Business Logic Extraction**: N/A - iOS-only feature
  - Violation justification: _N/A - /webApp not affected_

- [x] **Web TDD Workflow**: N/A - iOS-only feature
  - Violation justification: _N/A - /webApp not affected_

- [x] **Web Testing Strategy**: N/A - iOS-only feature
  - Violation justification: _N/A - /webApp not affected_

## Project Structure

### Documentation (this feature)

```text
specs/KAN-34-ios-align-found-flow/
├── plan.md              # This file
├── spec.md              # Feature specification
├── research.md          # Phase 0 output - technical decisions
├── data-model.md        # Phase 1 output - entity definitions
├── quickstart.md        # Phase 1 output - implementation guide
├── contracts/           # Phase 1 output - API contracts
│   └── no-api-changes.md
└── tasks.md             # Phase 2 output (created by /speckit.tasks)
```

### Source Code (repository root)

```text
iosApp/
├── iosApp/
│   ├── Features/
│   │   └── ReportMissingAndFoundPet/
│   │       ├── Common/
│   │       │   ├── Components/Form/       # Reused: ValidatedTextField, TextAreaView, etc.
│   │       │   ├── Helpers/               # Reused: MicrochipNumberFormatter
│   │       │   └── Models/                # Existing: ValidationError, FormField, etc.
│   │       └── ReportFoundPet/
│   │           ├── Coordinators/
│   │           │   └── FoundPetReportCoordinator.swift  # MODIFY: 4→3 steps
│   │           ├── Models/
│   │           │   └── FoundPetReportFlowState.swift    # MODIFY: add 2 fields
│   │           └── Views/
│   │               ├── Photo/                           # MODIFY: step 1/3
│   │               ├── PetDetails/                      # CREATE: new step 2/3
│   │               ├── ContactDetails/                  # MODIFY: step 3/3 + new fields
│   │               ├── ChipNumber/                      # DELETE: merged into PetDetails
│   │               ├── AnimalDescription/               # DELETE: replaced by PetDetails
│   │               └── Summary/                         # DELETE: flow exits after submit
│   ├── Resources/
│   │   ├── en.lproj/Localizable.strings  # MODIFY: add reportFoundPet.* strings
│   │   └── pl.lproj/Localizable.strings  # MODIFY: add reportFoundPet.* strings
│   └── Generated/
│       └── Strings.swift                  # REGENERATE: swiftgen
└── iosAppTests/
    └── Features/
        └── ReportMissingAndFoundPet/
            └── ReportFoundPet/
                └── Views/
                    ├── PetDetails/                      # CREATE: new tests
                    ├── ContactDetails/                  # MODIFY: add tests for new fields
                    ├── Photo/                           # MODIFY: update step indicator tests
                    ├── ChipNumber/                      # DELETE: obsolete tests
                    └── AnimalDescription/               # DELETE: obsolete tests
```

**Structure Decision**: iOS Mobile app with MVVM-C architecture. Feature module pattern with Views, ViewModels, Coordinators, and Models grouped by flow step.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| E2E tests skipped | iOS-only UI restructuring with no API changes | Full E2E suite would add significant time for no functional change |

---

## Phase 0 Artifacts

- ✅ **research.md**: All technical decisions documented
  - Flow restructuring (4→3 steps)
  - Collar data behavior (reuse MicrochipNumberFormatter)
  - Location validation (required for Found flow)
  - Phone validation (7-11 digits, optional caregiver)
  - iOS-only fields (not sent to backend)
  - Component reuse strategy
  - Files to modify/create/delete

## Phase 1 Artifacts

- ✅ **data-model.md**: Entity definitions
  - `FoundPetReportFlowState` with 2 new fields
  - Validation rules per step
  - State diagram

- ✅ **contracts/**: API contracts
  - `no-api-changes.md`: Confirms no backend changes per FR-016

- ✅ **quickstart.md**: Implementation guide
  - Step-by-step implementation order
  - Key code patterns
  - Testing checklist
  - Files reference

---

## Estimation Update (After PLAN)

| Phase | SP | Days | Confidence | Key Discovery |
|-------|-----|------|------------|---------------|
| Initial | 1 | 5.2 | ±50% | Gut feel from feature title - iOS-only flow alignment |
| After SPEC | 1 | 5.2 | ±30% | Scope confirmed: iOS-only, exactly 3 steps, new optional fields + microchip formatting and validation rules clarified |
| **After PLAN** | **1** | **5.2** | **±20%** | **Research complete: reuse existing components (MicrochipNumberFormatter, ValidatedTextField, TextAreaView), clear file list, no backend changes, ~10 files to modify/create** |
| After TASKS | — | — | ±15% | [Update when tasks.md complete] |

### Key Findings from Planning

1. **High component reuse**: All form components exist and can be reused
2. **No backend work**: iOS-only per FR-016 - reduces scope significantly
3. **Clear deletion scope**: 7 obsolete files to remove (ChipNumber + AnimalDescription + Summary views/viewmodels)
4. **Localization overhead**: ~20 new L10n strings needed (manageable)
5. **Test coverage**: Existing test patterns can be followed, main work is new `FoundPetPetDetailsViewModelTests`
6. **Summary removed**: Flow exits immediately after successful submission (simpler UX)

**Estimate remains at 1 SP** - planning confirmed the scope is well-contained iOS-only UI restructuring.
