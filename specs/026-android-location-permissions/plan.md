# Implementation Plan: Android Location Permissions Handling

**Branch**: `026-android-location-permissions` | **Date**: 2025-11-27 | **Spec**: `/specs/026-android-location-permissions/spec.md`
**Input**: Feature specification from `/specs/026-android-location-permissions/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implement Android location permissions handling on the startup screen to enable location-aware animal listings. The feature handles all Android permission states (not yet requested, granted, denied, "Don't ask again") with appropriate user flows including system dialogs, custom rationale dialogs, and Settings navigation. Location is fetched when permissions are granted, with a 10-second timeout fallback to no-location mode. The app always queries the server for animal listings regardless of permission status, including location coordinates when available.

## Technical Context

**Language/Version**: Kotlin (JVM 17 target)  
**Primary Dependencies**: 
- AndroidX Activity (for Activity Result API support)
- AndroidX Lifecycle (for lifecycle-aware components)
- Jetpack Compose (for UI)
- Kotlin Coroutines + Flow (for async operations and state management)
- Koin (for dependency injection - mandatory)
- Accompanist Permissions (for declarative permission handling - `com.google.accompanist:accompanist-permissions`)
- Android LocationManager (native Android API - no additional dependency required)
- Jetpack Navigation Component (for navigation - mandatory)

**Storage**: N/A (permission state managed by Android system, location coordinates passed to server query)  
**Testing**: 
- JUnit 6 + Kotlin Test + Turbine (for Flow testing)
- AndroidX Test (for permission testing utilities)
- MockK (for mocking Android system components, including LocationManager)
- Run command: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`

**Target Platform**: Android (API level 24+ - minSdk 24, runtime permissions supported, API level 29+ for "Only this time" option, API level 31+ for "Approximate" location option)  
**Project Type**: mobile (Android-only feature)  
**Performance Goals**: N/A (permission handling is user-initiated, no performance-critical paths)  
**Constraints**: 
- Location fetch timeout: 10 seconds before fallback to no-location mode
- Permission rationale dialogs displayed once per app session when permission is denied
- Must handle permission changes while app is in foreground (lifecycle-aware observation)

**Scale/Scope**: 
- Single screen (startup screen) with permission handling logic
- 5 user stories covering all permission states
- Location permission types: ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is an Android-only feature. iOS, Web, and Backend-related checks are marked as N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: Domain models, use cases, repositories, ViewModels in `/composeApp` ✓
  - iOS: N/A (Android-only feature)
  - Web: N/A (Android-only feature)
  - Backend: N/A (no backend changes required - location coordinates passed in existing API calls)
  - NO shared compiled code between platforms ✓
  - Violation justification: _N/A - compliant_

- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - Single `StateFlow<UiState>` source of truth with immutable data classes ✓
  - Sealed `UserIntent` and optional `UiEffect` types co-located with feature packages ✓
  - Reducers implemented as pure functions (no side effects) and unit-tested ✓
  - `dispatchIntent` entry wired from UI → ViewModel → reducer, with effects delivered via `SharedFlow` ✓
  - Navigation MUST use Jetpack Navigation Component (androidx.navigation:navigation-compose) ✓
  - Navigation graph defined with `NavHost` composable ✓
  - ViewModels trigger navigation via `UiEffect`, not direct `NavController` calls ✓
  - Composable screens follow two-layer pattern: state host (stateful) + stateless content composable ✓
  - Stateless composables MUST have `@Preview` with `@PreviewParameter` using custom `PreviewParameterProvider<UiState>` ✓
  - Callback lambdas MUST be defaulted to no-ops in stateless composables ✓
  - Previews focus on light mode only (no dark mode previews required) ✓
  - Violation justification: _N/A - compliant_

- [ ] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - N/A (Android-only feature)
  - Violation justification: _N/A - not applicable_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Android: Repository interfaces in `/composeApp/src/androidMain/.../domain/repositories/` ✓
  - iOS: N/A (Android-only feature)
  - Web: N/A (Android-only feature)
  - Backend: N/A (no backend changes)
  - Implementations in platform-specific data/repositories modules ✓
  - Use cases reference interfaces, not concrete implementations ✓
  - Violation justification: _N/A - compliant_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: MUST use Koin - DI modules in `/composeApp/src/androidMain/.../di/` ✓
  - iOS: N/A (Android-only feature)
  - Web: N/A (Android-only feature)
  - Backend: N/A (no backend changes)
  - Violation justification: _N/A - compliant_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: Tests in `/composeApp/src/androidUnitTest/`, run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` ✓
  - iOS: N/A (Android-only feature)
  - Web: N/A (Android-only feature)
  - Backend: N/A (no backend changes)
  - Coverage target: 80% line + branch coverage per platform ✓
  - Violation justification: _N/A - compliant_

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: N/A (Android-only feature)
  - Mobile: Appium tests in `/e2e-tests/java/src/test/resources/features/mobile/android-location-permissions.feature` (Java + Cucumber) ✓
  - Tests written in Java (Cucumber Gherkin scenarios) ✓
  - Screen Object Model used ✓
  - Each user story has at least one E2E test ✓
  - Violation justification: _N/A - compliant_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: Kotlin Coroutines (`viewModelScope`) + Flow for state ✓
  - iOS: N/A (Android-only feature)
  - Web: N/A (Android-only feature)
  - Backend: N/A (no backend changes)
  - No Combine, RxJava, RxSwift, or callback-based patterns for new code ✓
  - Violation justification: _N/A - compliant_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: `testTag` modifier on all interactive composables ✓
  - iOS: N/A (Android-only feature)
  - Web: N/A (Android-only feature)
  - Naming convention: `{screen}.{element}.{action}` (e.g., `animalList.rationaleDialog.continueButton.click`) ✓
  - List items use stable IDs (e.g., `animalList.item.${id}`) ✓
  - Violation justification: _N/A - compliant_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Kotlin: KDoc format (`/** ... */`) ✓
  - Documentation must be concise and high-level (1-3 sentences: WHAT/WHY, not HOW) ✓
  - Document only when purpose is not clear from name alone ✓
  - Skip documentation for self-explanatory methods, variables, and constants ✓
  - Violation justification: _N/A - compliant_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then) ✓
  - ViewModel tests use Given-When-Then pattern with descriptive names (Kotlin backticks) ✓
  - E2E tests structure scenarios with Given-When-Then phases (Cucumber Gherkin) ✓
  - Test names follow platform conventions ✓
  - Comments mark test phases in complex tests ✓
  - Violation justification: _N/A - compliant_

### Backend Architecture & Quality Standards (if `/server` affected)

- [ ] **Backend Technology Stack**: Plan uses modern Node.js stack for `/server` module
  - N/A (no backend changes required - location coordinates passed in existing API calls)
  - Violation justification: _N/A - not applicable_

- [ ] **Backend Code Quality**: Plan enforces quality standards for `/server` code
  - N/A (no backend changes required)
  - Violation justification: _N/A - not applicable_

- [ ] **Backend Dependency Management**: Plan minimizes dependencies in `/server/package.json`
  - N/A (no backend changes required)
  - Violation justification: _N/A - not applicable_

- [ ] **Backend Directory Structure**: Plan follows standardized layout in `/server/src/`
  - N/A (no backend changes required)
  - Violation justification: _N/A - not applicable_

- [ ] **Backend TDD Workflow**: Plan follows Test-Driven Development (Red-Green-Refactor)
  - N/A (no backend changes required)
  - Violation justification: _N/A - not applicable_

- [ ] **Backend Testing Strategy**: Plan includes comprehensive test coverage for `/server`
  - N/A (no backend changes required)
  - Violation justification: _N/A - not applicable_

## Project Structure

### Documentation (this feature)

```text
specs/026-android-location-permissions/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/
├── domain/
│   ├── models/
│   │   ├── LocationCoordinates.kt        # Device location (latitude, longitude)
│   │   ├── PermissionStatus.kt           # Sealed class for permission states
│   │   └── RationaleDialogType.kt        # Sealed class for rationale dialog types
│   ├── repositories/
│   │   └── LocationRepository.kt         # Interface for location operations
│   └── usecases/
│       ├── CheckLocationPermissionUseCase.kt
│       └── GetCurrentLocationUseCase.kt  # Two-stage: cached + fresh with 10s timeout
├── data/
│   └── repositories/
│       └── LocationRepositoryImpl.kt      # Implementation using LocationManager
├── features/
│   └── animallist/
│       ├── ui/
│       │   ├── AnimalListScreen.kt       # State host composable
│       │   ├── AnimalListContent.kt      # Stateless composable with previews
│       │   └── components/
│       │       ├── InformationalRationaleDialog.kt
│       │       └── EducationalRationaleDialog.kt
│       └── presentation/
│           ├── mvi/
│           │   ├── AnimalListUiState.kt  # Immutable data class with default companion
│           │   ├── AnimalListIntent.kt   # Sealed class for user intents
│           │   ├── AnimalListEffect.kt   # Sealed class for one-off events (navigation, dialogs)
│           │   └── AnimalListReducer.kt  # Pure reducer function
│           └── viewmodels/
│               └── AnimalListViewModel.kt # MVI ViewModel with StateFlow and SharedFlow
├── di/
│   └── LocationModule.kt                  # Koin module for location dependencies
└── navigation/
    └── AnimalListNavigation.kt            # Navigation effects handler

composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/
├── domain/
│   └── usecases/
│       ├── CheckLocationPermissionUseCaseTest.kt
│       └── GetCurrentLocationUseCaseTest.kt
├── data/
│   └── repositories/
│       └── LocationRepositoryImplTest.kt
├── fakes/
│   └── FakeLocationRepository.kt          # Test double for LocationRepository
└── features/
    └── animallist/
        └── presentation/
            ├── mvi/
            │   └── AnimalListReducerTest.kt  # Pure function tests
            └── viewmodels/
                └── AnimalListViewModelTest.kt # ViewModel tests with Turbine

e2e-tests/java/
├── src/
│   └── test/
│       ├── java/com/intive/aifirst/petspot/e2e/
│       │   ├── screens/
│       │   │   └── AnimalListScreen.java      # Screen Object Model with @AndroidFindBy
│       │   └── steps/mobile/
│       │       └── LocationPermissionSteps.java
│       └── resources/
│           └── features/
│               └── mobile/
│                   └── android-location-permissions.feature  # Cucumber Gherkin scenarios
└── pom.xml
```

**Structure Decision**: This is an Android-only feature implemented in the `/composeApp` module following the MVI architecture pattern. The feature adds location permission handling to the existing AnimalList screen (the app's startup screen). Domain models, use cases, and repository interfaces are added to support location operations. The AnimalList ViewModel is extended to handle permission states and location fetching. All code follows Android MVI architecture with StateFlow, sealed intents, and pure reducers. E2E tests are added to the unified `/e2e-tests` project using Java + Cucumber for mobile testing.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., 4th project] | [current need] | [why 3 projects insufficient] |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient] |
