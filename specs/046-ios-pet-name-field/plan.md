# Implementation Plan: iOS - Add Pet Name Field to Animal Details Screen

**Branch**: `046-ios-pet-name-field` | **Date**: December 4, 2025 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/046-ios-pet-name-field/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Add an optional "Animal name" text field to the iOS Animal Details screen (step 3/4 of the missing pet flow). The field will be positioned after "Date of disappearance" and before "Animal species" dropdown. The entered pet name will be stored in the flow state and submitted to the backend API as the `petName` field when creating an announcement. The implementation follows iOS MVVM-C architecture with SwiftUI for UI, manual dependency injection, and includes unit tests for ViewModel logic.

## Technical Context

**Language/Version**: Swift 5.9+ (iOS 15+ deployment target)  
**Primary Dependencies**: SwiftUI, Foundation, URLSession (existing HTTP client)  
**Storage**: In-memory flow state (`ReportMissingPetFlowState`), persisted to backend via REST API  
**Testing**: XCTest with Swift Concurrency (async/await)  
**Target Platform**: iOS 15+
**Project Type**: Mobile (iOS)  
**Performance Goals**: N/A (simple text field addition, no performance concerns)  
**Constraints**: N/A (single text field, no offline requirements)  
**Scale/Scope**: Single screen modification, 1 new field, affects 3 files (View, ViewModel, FlowState model)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

**Status**: ✅ ALL CHECKS PASSED (Phase 0 & Phase 1)

> **Note**: For backend-only features (affecting only `/server` module), you may mark frontend-related checks (Platform Independence, Android MVI, iOS MVVM-C, Test Identifiers for UI, E2E Tests for mobile/web) as N/A. Focus on Backend Architecture & Quality Standards checks.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - iOS-only feature implementing full stack in `/iosApp`
  - NO shared compiled code between platforms
  - Violation justification: N/A - compliant

- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - Violation justification: N/A - iOS-only feature (Android not affected)

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - ViewModel: `AnimalDescriptionViewModel` with `@Published var petName: String`
  - Existing coordinator manages navigation (no changes needed)
  - SwiftUI view: `AnimalDescriptionView` observes ViewModel (no business logic in view)
  - Violation justification: N/A - compliant

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - No new repositories required (uses existing flow state pattern)
  - Violation justification: N/A - compliant (no repository changes)

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - iOS: Uses existing manual DI setup in `/iosApp/iosApp/DI/` (no changes needed)
  - Violation justification: N/A - compliant (existing DI setup used)

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - iOS: Tests in `/iosApp/iosAppTests/Features/ReportMissingPet/ViewModels/AnimalDescriptionViewModelTests.swift`
  - Tests cover: petName property updates, flow state updates, empty/whitespace handling, API request payload
  - Coverage target: 80% line + branch coverage
  - Violation justification: N/A - compliant

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Mobile (iOS): Appium tests in `/e2e-tests/mobile/specs/report-missing-pet-flow.spec.ts` (extended)
  - Screen Object Model: Update `/e2e-tests/mobile/screens/AnimalDescriptionScreen.ts`
  - Steps: Update `/e2e-tests/mobile/steps/reportMissingPet.steps.ts`
  - Covers: entering pet name, submitting with/without pet name, pet name in API request
  - Violation justification: N/A - compliant

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor` (existing API call uses this pattern)
  - No new async operations required (only flow state update)
  - Violation justification: N/A - compliant

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - iOS: `accessibilityIdentifier("animalDescription.animalNameTextField.input")` on TextField
  - Naming convention: `{screen}.{element}.{action}` - compliant
  - Violation justification: N/A - compliant

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Swift: SwiftDoc format for non-obvious properties/methods only
  - `petName` property: self-explanatory (no doc needed)
  - `petNameTextFieldModel`: computed property returning TextField model (will add doc if complex)
  - Violation justification: N/A - compliant

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - All unit tests will follow Given-When-Then pattern
  - Test names: `test_petName_whenUserEntersText_shouldUpdateFlowState()` format
  - E2E tests: Gherkin scenarios with Given-When-Then structure
  - Violation justification: N/A - compliant

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: Plan uses modern Node.js stack for `/server` module
  - Violation justification: N/A - `/server` not affected (backend already supports `petName` field)

- [x] **Backend Code Quality**: Plan enforces quality standards for `/server` code
  - Violation justification: N/A - `/server` not affected (no backend code changes)

- [x] **Backend Dependency Management**: Plan minimizes dependencies in `/server/package.json`
  - Violation justification: N/A - `/server` not affected (no dependency changes)

- [x] **Backend Directory Structure**: Plan follows standardized layout in `/server/src/`
  - Violation justification: N/A - `/server` not affected (no backend structure changes)

- [x] **Backend TDD Workflow**: Plan follows Test-Driven Development (Red-Green-Refactor)
  - Violation justification: N/A - `/server` not affected (no backend implementation)

- [x] **Backend Testing Strategy**: Plan includes comprehensive test coverage for `/server`
  - Violation justification: N/A - `/server` not affected (no backend tests needed)

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
iosApp/iosApp/
├── Features/
│   └── ReportMissingPet/
│       ├── Models/
│       │   └── ReportMissingPetFlowState.swift       # MODIFY: Add optional petName property
│       ├── ViewModels/
│       │   └── AnimalDescriptionViewModel.swift      # MODIFY: Add petName @Published property and textFieldModel
│       └── Views/
│           └── AnimalDescription/
│               └── AnimalDescriptionView.swift       # MODIFY: Add TextField for animal name
│
iosApp/iosAppTests/
└── Features/
    └── ReportMissingPet/
        └── ViewModels/
            └── AnimalDescriptionViewModelTests.swift # MODIFY: Add tests for petName handling

e2e-tests/
├── mobile/
│   ├── screens/
│   │   └── AnimalDescriptionScreen.ts                # MODIFY: Add petNameTextField locator
│   ├── specs/
│   │   └── report-missing-pet-flow.spec.ts           # MODIFY: Add scenarios for petName field
│   └── steps/
│       └── reportMissingPet.steps.ts                 # MODIFY: Add step for entering pet name
```

**Structure Decision**: iOS mobile application structure selected. This feature modifies existing files in the ReportMissingPet feature module within `/iosApp/iosApp/Features/`. The iOS project follows MVVM-C architecture with manual dependency injection. Tests are located in `/iosApp/iosAppTests/` mirroring the production code structure. E2E tests extend existing mobile test suite in `/e2e-tests/mobile/`.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No violations detected. All constitution checks passed.

---

## Planning Phase Complete

**Status**: ✅ PHASE 0 & PHASE 1 COMPLETE

### Generated Artifacts

| Artifact | Status | Path |
|----------|--------|------|
| Implementation Plan | ✅ Complete | `/specs/046-ios-pet-name-field/plan.md` |
| Research Document | ✅ Complete | `/specs/046-ios-pet-name-field/research.md` |
| Data Model | ✅ Complete | `/specs/046-ios-pet-name-field/data-model.md` |
| API Contract | ✅ Complete | `/specs/046-ios-pet-name-field/contracts/announcements-api.yaml` |
| Quickstart Guide | ✅ Complete | `/specs/046-ios-pet-name-field/quickstart.md` |
| Agent Context Update | ✅ Complete | Updated `.cursor/rules/specify-rules.mdc` |

### Constitution Compliance

All constitution checks passed:
- ✅ Platform Independence (iOS-only implementation)
- ✅ iOS MVVM-C Architecture (follows existing pattern)
- ✅ 80% Test Coverage (unit tests planned)
- ✅ E2E Tests (mobile tests planned)
- ✅ Test Identifiers (accessibility identifier specified)
- ✅ Given-When-Then Test Structure (all tests will follow)
- ✅ Async Programming Standards (Swift Concurrency used)
- ✅ Public API Documentation (will document when needed)

### Next Steps

**Phase 2**: Break down into implementation tasks using `/speckit.tasks` command:

```bash
/speckit.tasks
```

This will generate `tasks.md` with detailed implementation tasks, test cases, and acceptance criteria.

**Implementation Order**:
1. Update `ReportMissingPetFlowState` model (add `petName` property)
2. Update `AnimalDescriptionViewModel` (add `@Published var petName`, init, update logic)
3. Update `AnimalDescriptionView` (add TextField with accessibility identifier)
4. Add localized strings (SwiftGen)
5. Write unit tests (6+ test cases for 80% coverage)
6. Extend E2E tests (screen object, steps, scenarios)

**Estimated Implementation Time**: 2-3 hours (including tests)

**Branch**: `046-ios-pet-name-field` (already checked out)

---

## Additional Resources

- **Feature Specification**: [spec.md](./spec.md)
- **Research Findings**: [research.md](./research.md)
- **Data Model Documentation**: [data-model.md](./data-model.md)
- **API Contract**: [contracts/announcements-api.yaml](./contracts/announcements-api.yaml)
- **Quick Implementation Guide**: [quickstart.md](./quickstart.md)
- **Project Constitution**: `/.specify/memory/constitution.md` (iOS MVVM-C section)
