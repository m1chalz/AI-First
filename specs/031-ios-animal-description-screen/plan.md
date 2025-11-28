# Implementation Plan: Animal Description Screen (iOS)

**Branch**: `031-ios-animal-description-screen` | **Date**: November 28, 2025 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/031-ios-animal-description-screen/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implement Step 3/4 of the iOS Missing Pet flow: Animal Description screen. This screen collects structured pet data (date, species, breed/race, gender, age, location coordinates, description) before proceeding to contact details. The screen uses SwiftUI with MVVM-C architecture, reusable form components with validation, and integrates with the existing LocationService for GPS coordinate capture. Navigation and flow scaffolding are already implemented per spec 017.

## Technical Context

**Language/Version**: Swift 5.9+ (Xcode 15+)  
**Primary Dependencies**: SwiftUI (iOS 18+), UIKit (coordinators), CoreLocation (GPS)  
**Storage**: In-memory session container (constructor-injected to ViewModel)  
**Testing**: XCTest with Swift Concurrency (async/await), XCUITest for accessibility identifiers  
**Target Platform**: iOS 18+ (92% device coverage per spec clarifications)  
**Project Type**: Mobile (iOS)  
**Performance Goals**: N/A (form input screen, no performance constraints)  
**Constraints**: 
- iOS 18+ minimum (92% adoption)
- Offline-capable (bundled curated species list)
- No VoiceOver support (accessibility identifiers for testing only)
- Submit-based validation (not real-time)
**Scale/Scope**: Single screen (Step 3/4 of Missing Pet flow), ~8 input fields, reusable form components

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is an iOS-only feature affecting `/iosApp` module. Android/Web/Backend checks marked as N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - iOS: ViewModels, domain logic, LocationService integration, session management in `/iosApp`
  - Android/Web/Backend: N/A (iOS-only feature)
  - NO shared compiled code between platforms ✅
  - Violation justification: _N/A - compliant_

- [x] **Android MVI Architecture**: N/A (iOS-only feature)
  - Violation justification: _N/A - Android not affected_

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern ✅
  - Coordinator manages navigation (already implemented per spec 017)
  - ViewModel conforms to `ObservableObject` with `@Published` properties
  - ViewModel calls repositories directly (NO use cases per iOS architecture)
  - ViewModel communicates with coordinator via closures (e.g., `onContinue`, `onBack`)
  - SwiftUI views observe ViewModel (no business/navigation logic)
  - Session container injected to ViewModel via constructor (manual DI)
  - LocationService injected to ViewModel for GPS coordinate capture
  - All formatting logic in ViewModel (dates, coordinates), not in views
  - Violation justification: _N/A - fully compliant with iOS MVVM-C_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform) ✅
  - iOS: LocationServiceProtocol already exists in `/iosApp/iosApp/Domain/Services/` (used by existing features)
  - iOS: NO new repository protocols needed for this feature (session-based form data only)
  - Android/Web/Backend: N/A (iOS-only feature)
  - Violation justification: _N/A - compliant (reuses existing LocationServiceProtocol)_

- [x] **Dependency Injection**: Plan includes DI setup for each platform ✅
  - iOS: Manual DI with constructor injection (ViewModel receives session container + LocationService)
  - Coordinator creates ViewModel with dependencies from ServiceContainer
  - Android/Web/Backend: N/A (iOS-only feature)
  - Violation justification: _N/A - fully compliant with manual DI pattern_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform ✅
  - iOS: Tests in `/iosApp/iosAppTests/Features/ReportMissingPet/AnimalDescription/`
  - Test scope: ViewModel logic (field validation, GPS capture, session updates, coordinator callbacks)
  - Test framework: XCTest with Swift Concurrency (async/await)
  - Coverage target: 80% line + branch coverage (ViewModel + validation logic)
  - Android/Web/Backend: N/A (iOS-only feature)
  - Violation justification: _N/A - will achieve 80% coverage via ViewModel tests_

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories ✅
  - iOS E2E: Appium tests in `/e2e-tests/mobile/specs/animal-description.spec.ts`
  - Framework: Appium + Cucumber (Java) with `@ios` tag
  - Screen Object Model: `/e2e-tests/src/test/java/.../screens/AnimalDescriptionScreen.java`
  - Coverage: All 3 user stories (P1: required fields, P2: GPS capture, P3: validation/persistence)
  - Test identifiers: All interactive elements have `.accessibilityIdentifier()`
  - Android/Web: N/A (iOS-only feature)
  - Violation justification: _N/A - E2E tests planned for iOS_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform ✅
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor` for ViewModel
  - LocationService already uses Swift Concurrency (actor-based)
  - GPS capture: `await locationService.requestLocation()`
  - NO Combine, RxSwift, or callback-based patterns
  - Android/Web/Backend: N/A (iOS-only feature)
  - Violation justification: _N/A - fully compliant with Swift Concurrency_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements ✅
  - iOS: `.accessibilityIdentifier()` modifier on all interactive views (per FR-016)
  - Naming convention: `{screen}.{element}.{action}` (e.g., `animalDescription.continueButton.tap`)
  - Examples:
    - `animalDescription.datePicker.tap`
    - `animalDescription.speciesDropdown.tap`
    - `animalDescription.raceTextField.input`
    - `animalDescription.genderFemale.tap`
    - `animalDescription.requestGPSButton.tap`
    - `animalDescription.latitudeTextField.input`
    - `animalDescription.continueButton.tap`
  - Android/Web: N/A (iOS-only feature)
  - Violation justification: _N/A - compliant (FR-016 mandates accessibilityIdentifier)_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed ✅
  - Swift: SwiftDoc format (`/// ...`) for non-obvious methods
  - Documentation scope:
    - ViewModel: Document complex validation logic (e.g., coordinate range validation)
    - Reusable components: Document when behavior is non-obvious (e.g., field clearing on species change)
    - Skip: Self-explanatory properties (`@Published var species: String?`), simple getters/setters
  - All docs: concise (1-3 sentences), focus on WHAT/WHY not HOW
  - Android/Web/Backend: N/A (iOS-only feature)
  - Violation justification: _N/A - will document complex validation & component behaviors_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention ✅
  - iOS unit tests: XCTest with Given-When-Then structure
  - Test names: `func testMethodName_whenCondition_shouldResult()` (Swift convention)
  - Comments: `// Given`, `// When`, `// Then` in test body
  - E2E tests: Cucumber scenarios with Given-When-Then steps (already standardized)
  - Android/Web/Backend: N/A (iOS-only feature)
  - Violation justification: _N/A - fully compliant with GWT convention_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A (iOS-only feature, no backend changes)
- [x] **Backend Code Quality**: N/A (iOS-only feature, no backend changes)
- [x] **Backend Dependency Management**: N/A (iOS-only feature, no backend changes)
- [x] **Backend Directory Structure**: N/A (iOS-only feature, no backend changes)
- [x] **Backend TDD Workflow**: N/A (iOS-only feature, no backend changes)
- [x] **Backend Testing Strategy**: N/A (iOS-only feature, no backend changes)

## Project Structure

### Documentation (this feature)

```text
specs/031-ios-animal-description-screen/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (component design, validation patterns)
├── data-model.md        # Phase 1 output (session state, validation rules)
├── quickstart.md        # Phase 1 output (dev setup, testing guide)
├── contracts/           # Phase 1 output (ViewModel interface, session container)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (iOS Application)

```text
iosApp/iosApp/
├── Features/
│   └── ReportMissingPet/
│       ├── AnimalDescription/
│       │   ├── AnimalDescriptionViewModel.swift      # ViewModel (ObservableObject)
│       │   ├── AnimalDescriptionView.swift           # Main SwiftUI view
│       │   └── Components/
│       │       ├── ValidatedTextField.swift          # Text field with error display (base)
│       │       ├── DropdownView.swift                # Generic picker with [String] options
│       │       ├── SelectorView.swift                # Generic radio buttons with [String] options
│       │       ├── LocationCoordinateView.swift      # Composes 2x ValidatedTextField + GPS button
│       │       └── TextAreaView.swift                # Multi-line text with char counter
│       │       # Note: Date picker uses native SwiftUI DatePicker (no custom component)
│       └── Session/
│           └── MissingPetFlowSession.swift           # Session container (already exists per spec 017)
├── Domain/
│   ├── Models/
│   │   ├── AnimalDescriptionDetails.swift            # Session-bound struct
│   │   └── SpeciesTaxonomyOption.swift               # Species options model
│   └── Services/
│       └── LocationServiceProtocol.swift             # Already exists
├── Data/
│   ├── LocationService.swift                         # Already exists
│   └── SpeciesTaxonomy.swift                         # Bundled species list
└── DI/
    └── ServiceContainer.swift                        # Already exists

iosApp/iosAppTests/
└── Features/
    └── ReportMissingPet/
        └── AnimalDescription/
            ├── AnimalDescriptionViewModelTests.swift  # ViewModel unit tests
            └── Fakes/
                └── FakeMissingPetFlowSession.swift    # Test double for session
```

**Structure Decision**: iOS mobile app following MVVM-C architecture. This feature adds the Animal Description step (Step 3/4) to the existing Missing Pet flow. Navigation scaffolding and session management already exist per spec 017. Feature-specific code lives in `/iosApp/iosApp/Features/ReportMissingPet/AnimalDescription/` with reusable form components in `Components/` subdirectory.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| _N/A - No constitution violations_ | — | — |
