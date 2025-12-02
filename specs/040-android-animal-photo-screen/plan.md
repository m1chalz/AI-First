# Implementation Plan: Android Animal Photo Screen

**Branch**: `040-android-animal-photo-screen` | **Date**: 2025-12-02 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/040-android-animal-photo-screen/spec.md`

## Summary

Implement the photo selection step (step 2/4) in the Android missing pet report flow. Users must attach a photo via Android Photo Picker before continuing. The UI displays a confirmation card with thumbnail, filename, and size after selection. Photo selection is mandatory - attempting to continue without a photo shows the Android standard long toast (`Toast.LENGTH_LONG`). State persists through configuration changes and navigation within the flow but is cleared on process death, matching the spec.

**Technical Approach**: Extend existing MVI architecture with photo-specific state and intents. Use `ActivityResultContracts.PickVisualMedia` for Photo Picker with fallback to `ACTION_GET_CONTENT` for older devices. Store photo metadata in `ReportMissingFlowState` (nav graph scoped) for consistency with chip number step - state survives navigation and rotation but not process death.

## Technical Context

**Language/Version**: Kotlin 2.2.20, Android API 24+ (minSdk), targetSdk 36  
**Primary Dependencies**: Jetpack Compose, Koin DI, AndroidX Activity Result APIs, Coil (image loading)  
**Storage**: In-memory ViewModel state + ReportMissingFlowState (nav graph scoped, no database)  
**Testing**: JUnit 6 + Kotlin Test + Turbine (Flow testing)  
**Target Platform**: Android 7.0+ (API 24), Photo Picker available on Android 13+ (backported via Google Play Services)
**Project Type**: Mobile (Android)  
**Performance Goals**: N/A  
**Constraints**: Photo metadata must survive navigation/rotation (not process death - consistent with chip number); URI permissions must be persistable for flow duration

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: This feature extends existing `/composeApp` implementation
  - No cross-platform code affected
  - Violation justification: _N/A - compliant_

- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - Extends existing `ReportMissingUiState` with photo attachment state
  - New photo-specific intents added to existing `ReportMissingIntent` sealed class
  - Reducer functions remain pure and side-effect free
  - Navigation via existing `UiEffect` pattern through `SharedFlow`
  - Jetpack Navigation Component already in use
  - PhotoContent follows stateless composable pattern with previews
  - Violation justification: _N/A - compliant_

- [x] **iOS MVVM-C Architecture**: N/A - Android-only feature

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories
  - No new repositories required - photo selection is UI-only
  - Future upload will use existing API patterns
  - Violation justification: _N/A - compliant_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Koin already configured in `/composeApp/src/androidMain/.../di/`
  - No new modules required - ViewModel already scoped to nav graph
  - Violation justification: _N/A - compliant_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests
  - Tests in `/composeApp/src/androidUnitTest/`
  - Coverage target: 80% for new photo state, intents, and reducer logic
  - ViewModel photo handling tested with Turbine
  - Violation justification: _N/A - compliant_

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Mobile E2E tests in `/e2e-tests/src/test/resources/features/mobile/`
  - Screen Object Model with `@AndroidFindBy` annotations
  - Test tags exposed: `animalPhoto.browse.click`, `animalPhoto.remove.click`, `animalPhoto.continue.click`
  - Violation justification: _N/A - compliant_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns
  - Kotlin Coroutines with `viewModelScope`
  - StateFlow for reactive state
  - No Combine, RxJava, or callbacks
  - Violation justification: _N/A - compliant_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers
  - `testTag` modifier on all interactive composables
  - Naming: `animalPhoto.browse.click`, `animalPhoto.remove.click`, `animalPhoto.continue.click`, `animalPhoto.filename.text`, `animalPhoto.filesize.text`
  - Violation justification: _N/A - compliant_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation
  - KDoc for new data classes and public functions
  - Documentation for PhotoAttachmentState, photo-related intents
  - Violation justification: _N/A - compliant_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow convention
  - All unit tests with Given-When-Then comments
  - Descriptive backtick test names (Kotlin style)
  - Violation justification: _N/A - compliant_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - `/server` not affected (front-end only feature)
- [x] **Backend Code Quality**: N/A - `/server` not affected
- [x] **Backend Dependency Management**: N/A - `/server` not affected
- [x] **Backend Directory Structure**: N/A - `/server` not affected
- [x] **Backend TDD Workflow**: N/A - `/server` not affected
- [x] **Backend Testing Strategy**: N/A - `/server` not affected

## Project Structure

### Documentation (this feature)

```text
specs/040-android-animal-photo-screen/
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
├── features/reportmissing/
│   ├── presentation/
│   │   ├── mvi/
│   │   │   ├── ReportMissingUiState.kt      # MODIFY: Add PhotoAttachmentState
│   │   │   ├── ReportMissingIntent.kt       # MODIFY: Add photo intents
│   │   │   ├── ReportMissingEffect.kt       # MODIFY: Add ShowToast effect
│   │   │   └── ReportMissingReducer.kt      # MODIFY: Add photo reducers
│   │   └── viewmodels/
│   │       └── PhotoViewModel.kt            # NEW: Photo step ViewModel (follows ChipNumberViewModel pattern)
│   └── ui/
│       └── photo/
│           ├── PhotoScreen.kt               # MODIFY: Add photo picker launcher
│           ├── PhotoContent.kt              # MODIFY: Replace placeholder with real UI
│           └── components/                  # NEW: Photo-specific components
│               ├── PhotoConfirmationCard.kt # NEW: Thumbnail + filename + size + remove
│               └── PhotoEmptyState.kt       # NEW: Browse button + helper text
└── core/
    └── util/
        └── FileSizeFormatter.kt             # NEW: Format bytes to KB/MB

composeApp/src/androidUnitTest/kotlin/.../features/reportmissing/
├── presentation/
│   ├── mvi/
│   │   └── PhotoReducerTest.kt              # NEW: Test photo state transitions
│   └── viewmodels/
│       └── PhotoViewModelTest.kt            # NEW: Test photo handling
└── ui/photo/
    └── PhotoContentTest.kt                  # NEW: Compose UI tests

e2e-tests/src/test/
├── java/.../screens/
│   └── AnimalPhotoScreen.java               # NEW: Screen Object for photo step
├── java/.../steps-mobile/
│   └── AnimalPhotoSteps.java                # NEW: Step definitions
└── resources/features/mobile/
    └── android-animal-photo.feature         # NEW: Gherkin scenarios
```

**Structure Decision**: Extends existing Android report missing flow structure. New photo components in `ui/photo/components/`, new utility in `core/util/`, tests mirror source structure.

## Complexity Tracking

> No Constitution Check violations - no complexity justification required.

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| _None_ | _N/A_ | _N/A_ |
