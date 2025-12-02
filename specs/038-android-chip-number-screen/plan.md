# Implementation Plan: Android Microchip Number Screen

**Branch**: `038-android-chip-number-screen` | **Date**: 2025-12-02 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/038-android-chip-number-screen/spec.md`

## Summary

Implement the microchip number input screen for Android as step 1/4 of the Report Missing Pet flow. The screen allows users to optionally enter a 15-digit microchip number with automatic formatting (00000-00000-00000). Uses MVI architecture with VisualTransformation for display formatting, storing raw digits in NavGraph-scoped flow state.

## Technical Context

**Language/Version**: Kotlin 2.2.20 (Android)  
**Primary Dependencies**: Jetpack Compose, Navigation Compose, Koin, Kotlin Coroutines  
**Storage**: In-memory flow state (NavGraph-scoped ViewModel) - no persistence  
**Testing**: JUnit 6 + Kotlin Test + Turbine (Flow testing)  
**Target Platform**: Android (minSdk 24, targetSdk 34)
**Project Type**: Mobile (Android)  
**Performance Goals**: N/A (simple UI screen, no network calls)  
**Constraints**: N/A  
**Scale/Scope**: Single screen within 4-step flow

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: Domain models, use cases, repositories, ViewModels in `/composeApp`
  - This feature is Android-only, no cross-platform code
  - NO shared compiled code between platforms
  - Violation justification: _N/A - compliant_

- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - Single `StateFlow<ChipNumberUiState>` source of truth with immutable data class
  - Sealed `ChipNumberUserIntent` for all user actions
  - Sealed `ChipNumberUiEffect` for navigation events (NavigateToPhoto, NavigateBack)
  - Navigation via Jetpack Navigation Component (nested NavGraph from spec 018)
  - Two-layer Composable pattern: `ChipNumberScreen` (state host) + `ChipNumberContent` (stateless)
  - `@Preview` with `@PreviewParameter` using `ChipNumberUiStateProvider`
  - Violation justification: _N/A - fully compliant_

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - N/A - Android-only feature
  - Violation justification: _N/A - not applicable_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - This screen has no repository dependencies (UI-only, flow state)
  - Flow state shared via NavGraph-scoped ViewModel
  - Violation justification: _N/A - no repositories needed_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: MUST use Koin - ViewModel registered in Koin module
  - NavGraph-scoped ViewModel for flow state sharing
  - Violation justification: _N/A - compliant_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: Tests in `/composeApp/src/androidUnitTest/`
  - ViewModel tests with Turbine for StateFlow testing
  - MicrochipNumberFormatter utility tests
  - Coverage target: 80% line + branch coverage
  - Violation justification: _N/A - compliant_

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Mobile: Appium tests in `/e2e-tests/` with Cucumber tags
  - Screen Object Model with `@AndroidFindBy` annotations
  - Each user story covered by E2E scenario
  - Violation justification: _N/A - compliant_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: Kotlin Coroutines (`viewModelScope`) + StateFlow for state
  - No reactive streams needed (simple state management)
  - Violation justification: _N/A - compliant_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: `testTag` modifier on all interactive composables
  - Naming convention: `chipNumber.{element}.{action}`
  - Examples: `chipNumber.input`, `chipNumber.continueButton.click`, `chipNumber.backButton.click`
  - Violation justification: _N/A - compliant_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Kotlin: KDoc format for non-obvious APIs
  - MicrochipNumberFormatter methods documented
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
specs/038-android-chip-number-screen/
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
│       ├── presentation/
│       │   ├── mvi/
│       │   │   ├── ChipNumberUiState.kt
│       │   │   ├── ChipNumberUserIntent.kt
│       │   │   └── ChipNumberUiEffect.kt
│       │   └── viewmodels/
│       │       └── ChipNumberViewModel.kt
│       ├── ui/
│       │   ├── ChipNumberScreen.kt        # State host composable
│       │   └── ChipNumberContent.kt       # Stateless composable + preview
│       └── util/
│           └── MicrochipNumberFormatter.kt  # VisualTransformation + formatting
│
├── di/
│   └── ReportMissingModule.kt             # Koin module for flow ViewModels

composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/
├── features/
│   └── reportmissing/
│       ├── presentation/
│       │   └── viewmodels/
│       │       └── ChipNumberViewModelTest.kt
│       └── util/
│           └── MicrochipNumberFormatterTest.kt

e2e-tests/src/test/
├── java/.../screens/
│   └── ChipNumberScreen.java              # Screen Object Model
├── java/.../steps-mobile/
│   └── ChipNumberSteps.java               # Step definitions
└── resources/features/mobile/
    └── chip-number.feature                # Cucumber scenarios
```

**Structure Decision**: Android mobile feature following MVI architecture with feature-based package organization. Flow state management via NavGraph-scoped ViewModel (infrastructure from spec 018).

## Complexity Tracking

No violations to justify - all Constitution checks pass.
