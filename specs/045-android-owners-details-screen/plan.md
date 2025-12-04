# Implementation Plan: Android Owner's Details Screen

**Branch**: `045-android-owners-details-screen` | **Date**: 2025-12-04 | **Spec**: [spec.md](./spec.md)  
**Input**: Feature specification from `/specs/045-android-owners-details-screen/spec.md`

## Summary

Implement the Android Owner's Details screen (Step 4/4) for the Missing Pet flow, replacing the existing placeholder with a full MVI implementation that collects phone, email, and optional reward, validates inputs, executes 2-step backend submission (announcement creation + photo upload), and navigates to the summary screen on success.

## Technical Context

**Language/Version**: Kotlin 2.2.20, Java 17  
**Primary Dependencies**: Jetpack Compose, Koin DI, Kotlin Coroutines, Ktor Client (OkHttp engine)  
**Storage**: In-memory flow state (ReportMissingFlowState), no local persistence  
**Testing**: JUnit Jupiter 6.0.1 + Kotlin Test + Turbine (Flow testing), Ktor Mock Engine, Kover for coverage  
**Target Platform**: Android API 26+ (Android 8.0 Oreo)  
**Project Type**: Mobile (Android)  
**Performance Goals**: N/A (form submission)  
**Constraints**: 2-step submission must complete both API calls before navigation  
**Scale/Scope**: Single screen, ~5 files to create/modify

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: Domain models, use cases, repositories, ViewModels in `/composeApp`
  - This feature is Android-only, implementing full stack in `/composeApp/src/androidMain/`
  - NO shared compiled code between platforms
  - Violation justification: _N/A - compliant_

- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - Single `StateFlow<OwnerDetailsUiState>` source of truth with immutable data class
  - Sealed `OwnerDetailsUserIntent` and `OwnerDetailsUiEffect` types co-located with feature package
  - Reducers implemented as pure functions (no side effects) and unit-tested
  - Navigation uses Jetpack Navigation Component (existing `ReportMissingNavGraph`)
  - ViewModels trigger navigation via `UiEffect`, not direct `NavController` calls
  - Composable screens follow two-layer pattern: `ContactDetailsScreen` (state host) + `ContactDetailsContent` (stateless)
  - Stateless composable has `@Preview` with `@PreviewParameter` using `OwnerDetailsUiStateProvider`
  - Callback lambdas defaulted to no-ops in stateless composable
  - Violation justification: _N/A - compliant_

- [ ] **iOS MVVM-C Architecture**: N/A - Android-only feature

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Repository interface for announcement submission: `AnnouncementRepository`
  - Implementation in data layer consuming backend API
  - Violation justification: _N/A - compliant_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: MUST use Koin - DI module in `/composeApp/src/androidMain/.../di/`
  - ViewModel registered via `viewModel { }` with parametersOf for callbacks
  - Repository registered as `single { }` or `factory { }`
  - Violation justification: _N/A - compliant_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: Tests in `/composeApp/src/androidUnitTest/`
  - Coverage target: 80% line + branch coverage for ViewModel, validation logic, and submission flow
  - Run: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
  - Violation justification: _N/A - compliant_

- [ ] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Mobile E2E tests deferred to separate task (consistent with other Android flow screens)
  - Violation justification: E2E tests for mobile flows are added incrementally after all 4 steps are implemented; ChipNumber, Photo, and Description screens don't have E2E tests yet. E2E will be added in a follow-up spec covering the full flow.

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: Kotlin Coroutines (`viewModelScope`) + Flow for state
  - Repository calls use `suspend` functions
  - No Combine, RxJava, or callback-based patterns
  - Violation justification: _N/A - compliant_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: `testTag` modifier on all interactive composables
  - Naming convention: `ownersDetails.{element}` (e.g., `ownersDetails.phoneInput`, `ownersDetails.continueButton`)
  - List items N/A for this screen
  - Violation justification: _N/A - compliant_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - KDoc for ViewModel public methods and UiState data class
  - Skip documentation for self-explanatory methods
  - Violation justification: _N/A - compliant_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then)
  - ViewModel tests use Given-When-Then pattern with backtick test names
  - Violation justification: _N/A - compliant_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - /server not affected (consuming existing API)
- [x] **Backend Code Quality**: N/A - /server not affected
- [x] **Backend Dependency Management**: N/A - /server not affected
- [x] **Backend Directory Structure**: N/A - /server not affected
- [x] **Backend TDD Workflow**: N/A - /server not affected
- [x] **Backend Testing Strategy**: N/A - /server not affected

## Project Structure

### Documentation (this feature)

```text
specs/045-android-owners-details-screen/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output (API contracts reference)
└── tasks.md             # Phase 2 output (/speckit.tasks command)
```

### Source Code (repository root)

```text
composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/
├── features/reportmissing/
│   ├── data/
│   │   └── repositories/
│   │       └── AnnouncementRepositoryImpl.kt          # NEW: 2-step submission impl
│   ├── domain/
│   │   ├── models/
│   │   │   └── AnnouncementModels.kt                  # NEW: DTOs and response models
│   │   ├── repositories/
│   │   │   └── AnnouncementRepository.kt              # NEW: Repository interface
│   │   └── usecases/
│   │       └── SubmitAnnouncementUseCase.kt           # NEW: 2-step submission orchestration
│   ├── presentation/
│   │   ├── mvi/
│   │   │   ├── OwnerDetailsUiState.kt                 # NEW: MVI state
│   │   │   ├── OwnerDetailsUserIntent.kt              # NEW: MVI intents
│   │   │   └── OwnerDetailsUiEffect.kt                # NEW: MVI effects
│   │   ├── state/
│   │   │   └── ReportMissingFlowState.kt              # MODIFY: Add reward field
│   │   └── viewmodels/
│   │       └── OwnerDetailsViewModel.kt               # NEW: MVI ViewModel
│   └── ui/
│       ├── contactdetails/
│       │   ├── ContactDetailsScreen.kt                # MODIFY: Wire to new ViewModel
│       │   └── ContactDetailsContent.kt               # MODIFY: Full implementation
│       └── ReportMissingNavGraph.kt                   # MODIFY: Update ContactDetails route
├── di/
│   └── ReportMissingModule.kt                         # MODIFY: Register new dependencies

composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/
└── features/reportmissing/
    ├── presentation/viewmodels/
    │   └── OwnerDetailsViewModelTest.kt               # NEW: ViewModel unit tests
    └── domain/usecases/
        └── SubmitAnnouncementUseCaseTest.kt           # NEW: Use case unit tests
```

**Structure Decision**: Following existing `reportmissing` feature structure. New files for OwnerDetails MVI pattern match ChipNumber, Photo, and Description screen patterns. Use case orchestrates 2-step submission, ViewModel manages UI state.

## Complexity Tracking

> No Constitution Check violations requiring justification.

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| E2E tests deferred | Full flow E2E will be added after all 4 steps implemented | Partial E2E for single step doesn't provide meaningful coverage |
