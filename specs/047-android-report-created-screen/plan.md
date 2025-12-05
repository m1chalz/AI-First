# Implementation Plan: Android Report Created Confirmation Screen

**Branch**: `047-android-report-created-screen` | **Date**: 2025-12-04 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/047-android-report-created-screen/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implement Android-only Report Created Confirmation Screen by **updating existing `SummaryScreen`/`SummaryContent`** (currently placeholder) as the final step of the "Report a Missing Animal" flow. The screen displays success confirmation messaging, a copyable management password (displayed as "code" in UI) in a gradient container, and a Close button to dismiss the entire flow. Management password is sourced from `ReportMissingFlowState.managementPassword` property (set by OwnerDetailsViewModel from spec 045). UI must match Figma design exactly (typography, colors, gradient background for password module). Follows MVI architecture with simple ViewModel (no use cases needed - display-only feature).

## Technical Context

**Language/Version**: Kotlin 2.2.20 with Jetpack Compose 1.9.1  
**Primary Dependencies**: Jetpack Compose, Navigation Component, Koin, ClipboardManager  
**Storage**: N/A (UI-only feature, data from flowState)  
**Testing**: JUnit 6 + Kotlin Test + Turbine (Flow testing)  
**Target Platform**: Android API 26+ (Android 8.0 Oreo minimum), targeting API 36
**Project Type**: Mobile (Android)  
**Performance Goals**: N/A (static confirmation screen, no performance-critical operations)  
**Constraints**: Must match Figma design exactly (typography, colors, gradient, spacing). Management password must support clipboard copy with Snackbar confirmation.  
**Scale/Scope**: Single screen, part of existing "Report Missing Animal" flow. No backend changes required (managementPassword already provided by spec 045).

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is an Android-only UI feature with no backend, iOS, or web changes.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: UI-only feature in `/composeApp` - no backend changes required
  - iOS: N/A (Android-only feature)
  - Web: N/A (Android-only feature)
  - Backend: N/A (no backend changes - managementPassword already provided by spec 045)
  - NO shared compiled code between platforms ✓
  - Violation justification: N/A - fully compliant

- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - Single `StateFlow<SummaryUiState>` source of truth with minimal immutable data class
  - Sealed `SummaryUserIntent` for user actions (CopyPasswordClicked, CloseClicked)
  - Sealed `SummaryUiEffect` for one-off events (ShowSnackbar, DismissFlow)
  - `dispatchIntent` entry wired from UI → ViewModel → reducer
  - Navigation uses existing Jetpack Navigation Component (NavHost from spec 018)
  - ViewModel triggers navigation via `SummaryUiEffect.DismissFlow`
  - Composable screen follows two-layer pattern: `SummaryScreen` (state host) + `SummaryContent` (stateless)
  - Stateless composable WILL have `@Preview` with `@PreviewParameter` using `SummaryUiStateProvider`
  - Callback lambdas defaulted to no-ops in stateless composable
  - Violation justification: N/A - follows MVI pattern

- [ ] **iOS MVVM-C Architecture**: N/A (Android-only feature)

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - N/A for this feature - no repository access needed (display-only)
  - Uses existing `ReportMissingFlowState` from flowStateHolder
  - Violation justification: N/A - display-only feature, no repository needed

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: MUST use Koin - `SummaryViewModel` registered in existing `ReportMissingModule`
  - Violation justification: N/A - uses Koin

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for Android
  - Android: Tests in `/composeApp/src/androidUnitTest/.../features/reportmissing/`
  - Coverage target: 80% line + branch coverage for SummaryViewModel
  - Scope: Password display logic, clipboard copy, close/dismiss effects
  - Violation justification: N/A - will achieve 80% coverage

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Mobile: E2E tests in `/e2e-tests/java/` using Appium + Cucumber
  - All 3 user stories covered (confirmation messaging, password copy, close flow)
  - Screen Object Model pattern used with `@AndroidFindBy` annotations
  - Violation justification: N/A - all user stories will have E2E coverage

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns
  - Android: Kotlin Coroutines (`viewModelScope`) + StateFlow for state
  - Clipboard operations handled synchronously (ClipboardManager)
  - Snackbar display via SharedFlow effect
  - No Combine, RxJava, or callback-based patterns
  - Violation justification: N/A - fully compliant

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: `Modifier.testTag()` on all interactive composables
  - Naming convention: `summary.{element}` (e.g., `summary.passwordContainer`, `summary.closeButton`)
  - Violation justification: N/A - all elements will have testTags per FR-012

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Kotlin: KDoc format for complex ViewModel methods
  - Skip documentation for self-explanatory methods (dispatchIntent, etc.)
  - Violation justification: N/A - will document only when needed

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests will use Given-When-Then structure with backtick test names
  - Test phases marked with comments in complex tests
  - Violation justification: N/A - all tests will follow convention

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - Android-only UI feature, no `/server` changes
  - Violation justification: N/A

- [x] **Backend Code Quality**: N/A - Android-only UI feature, no `/server` changes
  - Violation justification: N/A

- [x] **Backend Dependency Management**: N/A - Android-only UI feature, no `/server` changes
  - Violation justification: N/A

- [x] **Backend Directory Structure**: N/A - Android-only UI feature, no `/server` changes
  - Violation justification: N/A

- [x] **Backend TDD Workflow**: N/A - Android-only UI feature, no `/server` changes
  - Violation justification: N/A

- [x] **Backend Testing Strategy**: N/A - Android-only UI feature, no `/server` changes
  - Violation justification: N/A

## Project Structure

### Documentation (this feature)

```text
specs/047-android-report-created-screen/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command) - N/A for UI-only feature
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/
├── features/
│   ├── reportmissing/
│   │   ├── presentation/
│   │   │   ├── mvi/
│   │   │   │   ├── SummaryUiState.kt              # NEW - Immutable UI state
│   │   │   │   ├── SummaryUserIntent.kt           # NEW - Sealed user intents
│   │   │   │   └── SummaryUiEffect.kt             # NEW - One-off effects
│   │   │   ├── viewmodels/
│   │   │   │   └── SummaryViewModel.kt            # NEW - MVI ViewModel
│   │   │   └── state/
│   │   │       └── ReportMissingFlowState.kt      # EXISTS - Contains managementPassword
│   │   ├── ui/
│   │   │   ├── summary/
│   │   │   │   ├── SummaryScreen.kt               # UPDATE - State host composable
│   │   │   │   └── SummaryContent.kt              # UPDATE - Stateless composable with gradient
│   │   │   └── ReportMissingNavGraph.kt           # UPDATE - Wire SummaryViewModel
│   │   └── di/
│   │       └── (via ReportMissingModule.kt)       # UPDATE - Register SummaryViewModel
│   └── shared/
│       └── ui/
│           └── components/
│               └── (existing shared components)

composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/
└── features/
    └── reportmissing/
        ├── presentation/
        │   └── viewmodels/
        │       └── SummaryViewModelTest.kt        # NEW - ViewModel unit tests
        └── fakes/
            └── (reuse existing fakes if needed)

e2e-tests/java/
├── src/test/resources/features/mobile/
│   └── report-created-confirmation.feature        # NEW - Gherkin scenarios
└── src/test/java/.../
    ├── screens/
    │   └── SummaryScreen.java                     # UPDATE - Add selectors
    └── steps/mobile/
        └── ReportCreatedSteps.java                # NEW - Step definitions
```

**Structure Decision**: Android-only mobile feature. **Update existing `SummaryScreen`/`SummaryContent`** (placeholder from spec 018) with full implementation. Create new MVI components (`SummaryUiState`, `SummaryUserIntent`, `SummaryUiEffect`, `SummaryViewModel`). Reuse existing `ReportMissingFlowState` which already contains `managementPassword` property from spec 045. E2E tests in unified `/e2e-tests/java/` structure with Appium + Cucumber.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No violations - all Constitution Checks passed or marked N/A (Android-only UI feature with no architectural deviations).
