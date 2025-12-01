# Implementation Plan: Android Missing Pet Report Flow

**Branch**: `018-android-missing-pet-flow` | **Date**: 2025-12-01 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/018-android-missing-pet-flow/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implement a 5-screen wizard flow for reporting missing pets on Android using Jetpack Compose with MVI architecture. The flow consists of 4 data collection screens (chip number, photo, description, contact details) with a progress indicator (1/4 through 4/4) and a final summary screen without progress indicator. This is a UI-only feature with no backend integration - screens contain basic input placeholders and "Next" navigation buttons.

**Technical approach**: Use Jetpack Navigation Component with a nested nav graph for the flow. A shared ViewModel (`ReportMissingPetViewModel`) scoped to the nav graph manages flow state across all screens, following MVI architecture with `StateFlow<UiState>`, sealed `UserIntent`, and `SharedFlow<UiEffect>` for navigation events.

## Figma Designs

| Step | Screen | Figma Link |
|------|--------|------------|
| 1/4 | Microchip Number | [297:7954](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-7954&m=dev) |
| 2/4 | Animal Photo | [297:7991](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-7991&m=dev) |
| 3/4 | Animal Description | [297:8209](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8209&m=dev) |
| 4/4 | Contact Details | [297:8113](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8113&m=dev) |
| Summary | Report Created | [297:8193](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8193&m=dev) |

> **Implementation Scope**: Per spec, this phase implements **navigation scaffolding only** - screens will have placeholder UI with titles, progress indicators, and "Continue" buttons. Full form implementations from Figma designs will be added in subsequent features.

## Technical Context

**Language/Version**: Kotlin (JVM 17 target)  
**Primary Dependencies**: Jetpack Compose, Navigation Compose (androidx.navigation:navigation-compose), Koin  
**Storage**: N/A (UI-only, state managed in ViewModel memory only)  
**Testing**: JUnit 6 + Kotlin Test + Turbine (for Flow testing)  
**Target Platform**: Android (minSdk consistent with project, likely API 24+)
**Project Type**: Mobile (Android platform)  
**Performance Goals**: N/A (simple UI flow, no heavy processing)  
**Constraints**: State must survive configuration changes (device rotation) via ViewModel  
**Scale/Scope**: 5 screens, ~15 Compose files, 1 ViewModel, 1 nav graph addition

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is an Android-only, UI-only feature. Backend checks marked N/A. iOS and Web checks marked N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: All code in `/composeApp` following established patterns
  - iOS: N/A (Android-only feature)
  - Web: N/A (Android-only feature)
  - Backend: N/A (UI-only, no API calls)
  - NO shared compiled code between platforms ✓
  - Violation justification: _N/A - compliant_

- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - Single `StateFlow<UiState>` source of truth with immutable data classes ✓
  - Sealed `UserIntent` and `UiEffect` types co-located with feature package ✓
  - Reducers implemented as pure functions (no side effects) and unit-tested ✓
  - `dispatchIntent` entry wired from UI → ViewModel → reducer ✓
  - Navigation MUST use Jetpack Navigation Component ✓
  - Navigation graph defined with `NavHost` composable (nested graph for flow) ✓
  - ViewModels trigger navigation via `UiEffect`, not direct `NavController` calls ✓
  - Composable screens follow two-layer pattern: state host + stateless content ✓
  - Stateless composables MUST have `@Preview` with `@PreviewParameter` ✓
  - Callback lambdas MUST be defaulted to no-ops in stateless composables ✓
  - Violation justification: _N/A - compliant_

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - N/A - Android-only feature
  - Violation justification: _N/A - not applicable_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - N/A - UI-only feature, no repository layer needed
  - No domain/data layer changes required
  - Violation justification: _N/A - no repositories needed for UI-only feature_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: MUST use Koin ✓
  - ViewModel registered in existing `ViewModelModule.kt`
  - Violation justification: _N/A - compliant_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: Tests in `/composeApp/src/androidUnitTest/` ✓
  - ViewModel, Reducer, and UiState tests planned ✓
  - Coverage target: 80% line + branch coverage ✓
  - Violation justification: _N/A - compliant_

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Mobile: E2E tests in `/e2e-tests/` with Java/Cucumber/Appium ✓
  - US1 (forward navigation) and US2 (backward navigation) covered ✓
  - Violation justification: _N/A - compliant_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: Kotlin Coroutines (`viewModelScope`) + Flow for state ✓
  - No Combine, RxJava, RxSwift, or callback-based patterns ✓
  - Violation justification: _N/A - compliant_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: `testTag` modifier on all interactive composables ✓
  - Naming convention: `{screen}.{element}.{action}` ✓
  - Examples: `reportMissing.chipNumber.nextButton.click`, `reportMissing.progressIndicator`
  - Violation justification: _N/A - compliant_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Kotlin: KDoc format (`/** ... */`) ✓
  - Document ViewModel, UiState, Intent classes with high-level purpose ✓
  - Skip documentation for self-explanatory methods ✓
  - Violation justification: _N/A - compliant_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests use Given-When-Then pattern with descriptive backtick names ✓
  - E2E tests structure scenarios with Given-When-Then phases ✓
  - Violation justification: _N/A - compliant_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - `/server` not affected (UI-only feature)
- [x] **Backend Code Quality**: N/A - `/server` not affected
- [x] **Backend Dependency Management**: N/A - `/server` not affected
- [x] **Backend Directory Structure**: N/A - `/server` not affected
- [x] **Backend TDD Workflow**: N/A - `/server` not affected
- [x] **Backend Testing Strategy**: N/A - `/server` not affected

## Project Structure

### Documentation (this feature)

```text
specs/018-android-missing-pet-flow/
├── plan.md              # This file
├── research.md          # Phase 0 output - navigation patterns research
├── data-model.md        # Phase 1 output - flow state and UI models
├── quickstart.md        # Phase 1 output - development setup guide
├── contracts/           # N/A - UI-only feature, no API contracts
└── tasks.md             # Phase 2 output (created by /speckit.tasks)
```

### Source Code (repository root)

```text
composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/
├── features/
│   └── reportmissing/                    # NEW: Missing pet report feature
│       ├── presentation/
│       │   ├── mvi/
│       │   │   ├── ReportMissingUiState.kt      # Immutable UI state (all 5 screens)
│       │   │   ├── ReportMissingIntent.kt       # Sealed interface for user intents
│       │   │   ├── ReportMissingEffect.kt       # One-off navigation events
│       │   │   └── ReportMissingReducer.kt      # Pure state transitions
│       │   └── viewmodels/
│       │       └── ReportMissingViewModel.kt    # Shared ViewModel for entire flow
│       └── ui/
│           ├── ReportMissingNavGraph.kt         # Nested nav graph for flow
│           ├── components/
│           │   ├── StepHeader.kt                # Back button + title + progress (Steps 1-4)
│           │   └── StepProgressIndicator.kt     # Circular progress with "X/4" text
│           ├── chipnumber/
│           │   ├── ChipNumberScreen.kt          # State host composable
│           │   └── ChipNumberContent.kt         # Stateless UI + preview
│           ├── photo/
│           │   ├── PhotoScreen.kt
│           │   └── PhotoContent.kt
│           ├── description/
│           │   ├── DescriptionScreen.kt
│           │   └── DescriptionContent.kt
│           ├── contactdetails/
│           │   ├── ContactDetailsScreen.kt
│           │   └── ContactDetailsContent.kt
│           └── summary/
│               ├── SummaryScreen.kt
│               └── SummaryContent.kt
├── navigation/
│   ├── NavRoute.kt                       # ADD: ReportMissing nested routes
│   ├── NavGraph.kt                       # UPDATE: Add reportMissing navigation()
│   └── NavControllerExt.kt               # UPDATE: Enable navigateToReportMissing()
└── di/
    └── ViewModelModule.kt                # UPDATE: Register ReportMissingViewModel

composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/
└── features/
    └── reportmissing/
        └── presentation/
            ├── ReportMissingViewModelTest.kt    # ViewModel tests
            ├── ReportMissingReducerTest.kt      # Pure reducer tests
            └── ReportMissingUiStateTest.kt      # State model tests
```

**Structure Decision**: Feature module structure follows existing `animallist/` and `petdetails/` patterns. Uses nested navigation graph for the multi-step flow with a single shared ViewModel scoped to the flow.

## Complexity Tracking

> No violations requiring justification - all Constitution checks pass.

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| _None_ | _N/A_ | _N/A_ |
