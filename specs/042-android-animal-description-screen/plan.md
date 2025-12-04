# Implementation Plan: Android Animal Description Screen

**Branch**: `042-android-animal-description-screen` | **Date**: 2025-12-03 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/042-android-animal-description-screen/spec.md`

## Summary

Implement the Animal Description screen for Android as step 3/4 of the Report Missing Pet flow. This screen collects:
- Date of disappearance (required, date picker, no future dates)
- Animal species (required, dropdown from bundled list)
- Animal race/breed (required text field, enabled after species selection)
- Gender (required, two-card selector)
- Animal age (optional, numeric 0-40)
- GPS location (optional, Request GPS button + manual lat/long inputs)
- Additional description (optional, 500 char max with live counter)

Uses MVI architecture with validation on submit, NavGraph-scoped flow state shared with other flow screens.

## Technical Context

**Language/Version**: Kotlin 2.2.20 (Android)  
**Primary Dependencies**: Jetpack Compose, Navigation Compose, Koin, Kotlin Coroutines  
**Storage**: In-memory flow state (NavGraph-scoped via `ReportMissingPetFlowState`) - no backend persistence  
**Testing**: JUnit 6 + Kotlin Test + Turbine (Flow testing)  
**Target Platform**: Android (minSdk 24, targetSdk 36)
**Project Type**: Mobile (Android)  
**Performance Goals**: N/A (UI screen, GPS request should complete within reasonable time)  
**Constraints**: N/A  
**Scale/Scope**: Single screen within 4-step flow, extends existing placeholder

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: Domain models, ViewModels, UI in `/composeApp`
  - This feature is Android-only, no cross-platform code
  - NO shared compiled code between platforms
  - Violation justification: _N/A - compliant_

- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - Single `StateFlow<AnimalDescriptionUiState>` source of truth with immutable data class
  - Sealed `AnimalDescriptionUserIntent` for all user actions (UpdatePetName, UpdateDate, UpdateSpecies, UpdateRace, UpdateGender, UpdateAge, RequestGPS, UpdateLatitude, UpdateLongitude, UpdateDescription, Continue, Back)
  - Sealed `AnimalDescriptionUiEffect` for navigation and one-off events (NavigateToContactDetails, NavigateBack, ShowSnackbar, OpenDatePicker, OpenSettings)
  - Navigation via Jetpack Navigation Component (nested NavGraph from spec 018)
  - Two-layer Composable pattern: `DescriptionScreen` (state host) + `AnimalDescriptionContent` (stateless)
  - `@Preview` with `@PreviewParameter` using `AnimalDescriptionUiStateProvider`
  - Violation justification: _N/A - fully compliant_

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - N/A - Android-only feature
  - Violation justification: _N/A - not applicable_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Reuses existing `GetCurrentLocationUseCase` → `LocationRepository` for GPS functionality
  - Follows same pattern as `AnimalListViewModel` (use case injection)
  - No new repository interfaces needed - screen uses bundled static species list
  - Violation justification: _N/A - reuses existing interfaces and use cases_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: MUST use Koin - ViewModel registered in existing `ReportMissingModule`
  - NavGraph-scoped ViewModel for flow state sharing (established in spec 018)
  - `GetCurrentLocationUseCase` injected for GPS functionality (already in Koin modules)
  - Violation justification: _N/A - compliant_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: Tests in `/composeApp/src/androidUnitTest/`
  - ViewModel tests with Turbine for StateFlow testing
  - Validation logic tests
  - GPS request handling tests (mocked LocationRepository)
  - Coverage target: 80% line + branch coverage
  - Violation justification: _N/A - compliant_

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Mobile: Appium tests in `/e2e-tests/` with Cucumber tags (@android)
  - Screen Object Model with `@AndroidFindBy` annotations
  - Each user story covered by E2E scenario
  - Violation justification: _N/A - compliant_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: Kotlin Coroutines (`viewModelScope`) + StateFlow for state
  - GPS request uses suspend function from LocationRepository
  - Violation justification: _N/A - compliant_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: `testTag` modifier on all interactive composables
  - Naming convention: `animalDescription.{element}` (no .{action} suffix per spec clarification)
  - Examples: `animalDescription.datePicker`, `animalDescription.speciesDropdown`, `animalDescription.genderFemale`, `animalDescription.genderMale`, `animalDescription.continueButton`
  - Violation justification: _N/A - compliant_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Kotlin: KDoc format for non-obvious APIs
  - Validation functions documented
  - ViewModel public methods documented if complex
  - Violation justification: _N/A - compliant_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests with backtick names and Given/When/Then comments
  - E2E tests with Gherkin scenarios
  - Violation justification: _N/A - compliant_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - no backend changes
- [x] **Backend Code Quality**: N/A - no backend changes
- [x] **Backend Dependency Management**: N/A - no backend changes
- [x] **Backend Directory Structure**: N/A - no backend changes
- [x] **Backend TDD Workflow**: N/A - no backend changes
- [x] **Backend Testing Strategy**: N/A - no backend changes

## Project Structure

### Documentation (this feature)

```text
specs/042-android-animal-description-screen/
├── spec.md              # Feature specification
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── checklists/          # Quality checklists
│   └── requirements.md
└── tasks.md             # Phase 2 output (created by /speckit.tasks)
```

### Source Code (repository root)

```text
composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/
├── features/
│   └── reportmissing/
│       ├── data/
│       │   └── SpeciesTaxonomy.kt              # Bundled species list
│       ├── presentation/
│       │   ├── mvi/
│       │   │   ├── AnimalDescriptionUiState.kt
│       │   │   ├── AnimalDescriptionUserIntent.kt
│       │   │   ├── AnimalDescriptionUiEffect.kt
│       │   │   └── AnimalDescriptionReducer.kt
│       │   ├── state/
│       │   │   └── ReportMissingFlowState.kt   # EXTEND with animal description fields
│       │   └── viewmodels/
│       │       └── AnimalDescriptionViewModel.kt
│       ├── ui/
│       │   └── description/
│       │       ├── DescriptionScreen.kt        # State host (MODIFY)
│       │       ├── AnimalDescriptionContent.kt # Stateless + previews (REPLACE placeholder)
│       │       └── components/
│       │           ├── DatePickerField.kt
│       │           ├── SpeciesDropdown.kt
│       │           ├── GenderSelector.kt
│       │           ├── GpsLocationSection.kt
│       │           └── CharacterCounterTextField.kt
│       └── util/
│           └── AnimalDescriptionValidator.kt   # Validation logic
│
├── di/
│   └── ReportMissingModule.kt                  # Add AnimalDescriptionViewModel

composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/
├── features/
│   └── reportmissing/
│       ├── presentation/
│       │   ├── mvi/
│       │   │   └── AnimalDescriptionReducerTest.kt
│       │   └── viewmodels/
│       │       └── AnimalDescriptionViewModelTest.kt
│       └── util/
│           └── AnimalDescriptionValidatorTest.kt

e2e-tests/src/test/
├── java/.../screens/
│   └── AnimalDescriptionScreen.java            # Screen Object Model
├── java/.../steps-mobile/
│   └── AnimalDescriptionSteps.java             # Step definitions
└── resources/features/mobile/
    └── animal-description.feature              # Cucumber scenarios
```

**Structure Decision**: Android mobile feature following MVI architecture with feature-based package organization. Extends existing `reportmissing` feature structure from specs 018 and 038.

## Complexity Tracking

No violations to justify - all Constitution checks pass.
