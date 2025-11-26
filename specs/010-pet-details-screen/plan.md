# Implementation Plan: Pet Details Screen (Android UI)

**Branch**: `010-pet-details-screen` | **Date**: 2025-11-25 | **Spec**: `/specs/010-pet-details-screen/spec.md`
**Input**: Feature specification from `/specs/010-pet-details-screen/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implement a Pet Details Screen for Android that displays comprehensive information about a selected pet from the animal list. The screen shows pet photo, status badge, identification data (species, breed, sex, age, microchip), location information, contact details, reward information, and additional description. Users can navigate to this screen by tapping on any pet list item. The screen follows Android MVI architecture with Jetpack Compose UI, using type-safe navigation and maintaining consistency with existing Animal List screen patterns.

## Technical Context

**Language/Version**: Kotlin (JVM 17 target)  
**Primary Dependencies**: 
  - Jetpack Compose (UI framework)
  - Jetpack Compose Navigation (type-safe navigation with kotlinx-serialization)
  - Koin (dependency injection - mandatory)
  - Kotlin Coroutines + Flow (async/state management)
  - Coil (image loading library - to be added: `io.coil-kt:coil-compose:2.5.0`)
  - Material 3 (design system)
**Storage**: N/A (UI-only feature, data fetched from repository)  
**Testing**: 
  - JUnit 6 + Kotlin Test (unit testing)
  - Turbine (Flow testing)
  - Compose UI testing (for UI component tests)
**Target Platform**: Android (minimum SDK 24 - Android 7.0 Nougat, confirmed from project config)  
**Project Type**: Mobile (Android app)  
**Performance Goals**: N/A (UI screen, standard Android performance expectations)  
**Constraints**: 
  - Must handle slow network connections (loading states)
  - Must handle image loading failures gracefully
  - Must support scrolling for long content
  - Must maintain 60fps during scrolling
**Scale/Scope**: Single screen feature, part of larger Android app

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is an Android-only UI feature. iOS and Web checks are marked N/A. Backend checks are N/A unless backend API changes are needed.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: Domain models, use cases, repositories, ViewModels in `/composeApp` ✓
  - iOS: N/A (Android-only feature)
  - Web: N/A (Android-only feature)
  - Backend: N/A (UI-only feature, no backend changes required)
  - NO shared compiled code between platforms ✓ (Note: Current codebase uses `/shared` module, but this feature will use Android-specific models if needed)
  - Violation justification: _N/A_

- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - Single `StateFlow<UiState>` source of truth with immutable data classes ✓ (will follow AnimalListScreen pattern)
  - Sealed `UserIntent` and optional `UiEffect` types co-located with feature packages ✓ (will follow existing pattern)
  - Reducers implemented as pure functions (no side effects) and unit-tested ✓ (will follow AnimalListReducer pattern)
  - `dispatchIntent` entry wired from UI → ViewModel → reducer, with effects delivered via `SharedFlow` ✓ (will follow existing pattern)
  - Violation justification: _N/A_

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - N/A (Android-only feature)
  - Violation justification: _N/A_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Android: Repository interfaces exist in shared module, will extend `AnimalRepository` interface with `getAnimalById(id: String)` method ✓
  - iOS: N/A (Android-only feature)
  - Web: N/A (Android-only feature)
  - Backend: N/A (no backend changes)
  - Implementations in platform-specific data/repositories modules ✓
  - Use cases reference interfaces, not concrete implementations ✓
  - Violation justification: _N/A_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: MUST use Koin - DI modules in `/composeApp/src/androidMain/.../di/` ✓ (will add GetAnimalByIdUseCase to existing ViewModelModule)
  - iOS: N/A (Android-only feature)
  - Web: N/A (Android-only feature)
  - Backend: N/A (no backend changes)
  - Violation justification: _N/A_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: Tests in `/composeApp/src/androidUnitTest/`, run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` ✓ (will add tests for ViewModel, reducer, use case)
  - iOS: N/A (Android-only feature)
  - Web: N/A (Android-only feature)
  - Backend: N/A (no backend changes)
  - Coverage target: 80% line + branch coverage per platform ✓
  - Violation justification: _N/A_

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: N/A (Android-only feature)
  - Mobile: Appium tests in `/e2e-tests/mobile/specs/pet-details-screen.spec.ts` ✓ (will add E2E tests for all 7 user stories)
  - All tests written in TypeScript ✓
  - Screen Object Model used ✓
  - Each user story has at least one E2E test ✓
  - Violation justification: _N/A_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: Kotlin Coroutines (`viewModelScope`) + Flow for state ✓ (will follow existing pattern)
  - iOS: N/A (Android-only feature)
  - Web: N/A (Android-only feature)
  - Backend: N/A (no backend changes)
  - No Combine, RxJava, RxSwift, or callback-based patterns for new code ✓
  - Violation justification: _N/A_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: `testTag` modifier on all interactive composables ✓ (will use format `petDetails.{element}.{action}`)
  - iOS: N/A (Android-only feature)
  - Web: N/A (Android-only feature)
  - Naming convention: `{screen}.{element}.{action}` (e.g., `petDetails.backButton.click`, `petDetails.removeReportButton.click`) ✓
  - List items use stable IDs: N/A (no lists in details screen)
  - Violation justification: _N/A_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Kotlin: KDoc format (`/** ... */`) ✓ (will document complex methods, skip self-explanatory ones)
  - Swift: N/A (Android-only feature)
  - TypeScript: N/A (Android-only feature)
  - Documentation must be concise and high-level (1-3 sentences: WHAT/WHY, not HOW) ✓
  - Document only when purpose is not clear from name alone ✓
  - Skip documentation for self-explanatory methods, variables, and constants ✓
  - Violation justification: _N/A_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then) ✓ (will follow existing test patterns)
  - ViewModel tests use Given-When-Then pattern with descriptive names ✓ (will use backtick test names)
  - E2E tests structure scenarios with Given-When-Then phases ✓
  - Test names follow platform conventions (backticks for Kotlin) ✓
  - Comments mark test phases in complex tests ✓
  - Violation justification: _N/A_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: Plan uses modern Node.js stack for `/server` module
  - N/A (UI-only feature, no backend changes required)
  - Violation justification: _N/A_

- [x] **Backend Code Quality**: Plan enforces quality standards for `/server` code
  - N/A (UI-only feature, no backend changes required)
  - Violation justification: _N/A_

- [x] **Backend Dependency Management**: Plan minimizes dependencies in `/server/package.json`
  - N/A (UI-only feature, no backend changes required)
  - Violation justification: _N/A_

- [x] **Backend Directory Structure**: Plan follows standardized layout in `/server/src/`
  - N/A (UI-only feature, no backend changes required)
  - Violation justification: _N/A_

- [x] **Backend TDD Workflow**: Plan follows Test-Driven Development (Red-Green-Refactor)
  - N/A (UI-only feature, no backend changes required)
  - Violation justification: _N/A_

- [x] **Backend Testing Strategy**: Plan includes comprehensive test coverage for `/server`
  - N/A (UI-only feature, no backend changes required)
  - Violation justification: _N/A_

## Project Structure

### Documentation (this feature)

```text
specs/010-pet-details-screen/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command) - N/A (no API contracts needed for UI-only feature
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/
├── features/
│   └── petdetails/                    # NEW: Pet Details feature module
│       ├── presentation/
│       │   ├── mvi/
│       │   │   ├── PetDetailsUiState.kt        # Immutable UI state
│       │   │   ├── PetDetailsIntent.kt        # Sealed interface for user intents
│       │   │   ├── PetDetailsEffect.kt        # Sealed interface for one-off effects
│       │   │   └── PetDetailsReducer.kt      # Pure reducer functions
│       │   └── viewmodels/
│       │       └── PetDetailsViewModel.kt     # MVI ViewModel
│       └── ui/
│           ├── PetDetailsScreen.kt           # Main screen composable
│           ├── PetDetailsContent.kt          # Content composable (scrollable)
│           ├── PetDetailsHeader.kt           # Header with back button
│           ├── PetPhotoSection.kt            # Hero image with status/reward badges
│           ├── PetInfoSection.kt             # Identification fields (species, breed, etc.)
│           ├── PetLocationSection.kt         # Location info with map button
│           ├── PetContactSection.kt          # Contact information
│           ├── PetDescriptionSection.kt      # Additional description
│           └── PetActionsSection.kt          # Remove Report button
│
├── domain/
│   └── usecases/
│       └── GetAnimalByIdUseCase.kt           # NEW: Use case to fetch single animal by ID
│
├── data/
│   └── AnimalRepositoryImpl.kt               # UPDATE: Add getAnimalById() method
│
├── di/
│   ├── DomainModule.kt                       # UPDATE: Add GetAnimalByIdUseCase binding
│   └── ViewModelModule.kt                    # UPDATE: Add PetDetailsViewModel binding
│
└── navigation/
    ├── NavGraph.kt                           # UPDATE: Add PetDetailsScreen route
    └── NavControllerExt.kt                   # UPDATE: Enable navigateToAnimalDetail()

composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/
└── features/
    └── petdetails/
        ├── presentation/
        │   ├── mvi/
        │   │   └── PetDetailsReducerTest.kt   # Unit tests for reducer
        │   └── viewmodels/
        │       └── PetDetailsViewModelTest.kt  # Unit tests for ViewModel
        └── domain/
            └── usecases/
                └── GetAnimalByIdUseCaseTest.kt # Unit tests for use case

e2e-tests/mobile/
└── specs/
    └── pet-details-screen.spec.ts             # NEW: E2E tests for all user stories
```

**Structure Decision**: This feature follows the existing Android feature module pattern established by `animallist`. All MVI components (UiState, Intent, Effect, Reducer) are co-located in the `presentation/mvi/` directory. UI composables are organized by section (header, photo, info, location, contact, description, actions) for maintainability. The feature integrates with existing navigation infrastructure and DI setup.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No violations. All constitution checks passed.

---

## Phase Completion Summary

### Phase 0: Outline & Research ✅ COMPLETE

**Generated Artifacts**:
- `research.md` - Resolved all technical clarifications:
  - Image loading library (Coil)
  - Minimum SDK version (API 24)
  - Status value mapping (ACTIVE → "MISSING")
  - Missing fields in Animal model (microchipNumber, rewardAmount, approximateAge)
  - Date format conversion ("DD/MM/YYYY" → "MMM DD, YYYY")
  - Microchip number formatting ("000-000-000-000")
  - GetAnimalByIdUseCase creation
  - Navigation parameter handling

**Status**: All research questions resolved. Ready for Phase 1.

### Phase 1: Design & Contracts ✅ COMPLETE

**Generated Artifacts**:
- `data-model.md` - Complete data model specification:
  - PetDetails entity (extends Animal)
  - All required and optional fields
  - Validation rules
  - State transitions
  - Edge case handling
  - Data flow diagram

- `contracts/README.md` - API contracts note:
  - UI-only feature, no new API contracts needed
  - Uses existing AnimalRepository interface

- `quickstart.md` - Implementation guide:
  - Step-by-step implementation instructions
  - Code examples for all components
  - Testing checklist
  - References to related documents

- Agent context updated: Cursor IDE context file updated with Kotlin and project type information

**Status**: All design artifacts generated. Ready for Phase 2 (task breakdown).

---

## Next Steps

1. **Phase 2**: Run `/speckit.tasks` command to break plan into implementation tasks
2. **Phase 3**: Run `/speckit.checklist` command to create implementation checklist
3. **Implementation**: Follow tasks.md and quickstart.md to implement feature
4. **Testing**: Achieve 80% test coverage for ViewModel, reducer, and use case
5. **E2E Testing**: Create mobile E2E tests for all 7 user stories

---

## Branch Information

- **Branch**: `010-pet-details-screen`
- **Plan Path**: `/specs/010-pet-details-screen/plan.md`
- **Spec Path**: `/specs/010-pet-details-screen/spec.md`
- **Generated Artifacts**:
  - `research.md`
  - `data-model.md`
  - `quickstart.md`
  - `contracts/README.md`
