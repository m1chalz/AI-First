# Implementation Plan: Android Fullscreen Interactive Map

**Branch**: `071-android-fullscreen-map` | **Date**: 2026-01-08 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/071-android-fullscreen-map/spec.md`

## Summary

Implement a fullscreen interactive map screen for Android that opens when users tap the landing page map preview. Screen includes a header bar with back arrow and "Pet Locations" title, a legend showing pin colors (Missing = red, Found = blue), and the map with zoom/pan gestures. Displays red/blue pins for missing/found pets and shows a bottom sheet pop-up with pet details when a pin is tapped. Bottom navigation remains visible. Navigation back to landing page via back arrow or system back button.

**Technical Approach**: MVI architecture with Google Maps SDK for Android, following the two-layer composable pattern. ViewModel manages map state, pin data loading, and pop-up visibility. Use cases fetch pet announcements from existing backend API with location filtering.

## Technical Context

**Language/Version**: Kotlin 2.2.20  
**Primary Dependencies**: Jetpack Compose 1.9.1, maps-compose 6.12.2, play-services-maps 19.2.0, Koin 3.5.3, Kotlin Coroutines 1.9.0, Navigation Compose 2.9.0  
**Storage**: N/A (consumes backend API via existing repository)  
**Testing**: JUnit Jupiter 6.0.1 + Kotlin Test + Turbine 1.1.0 (Flow testing)  
**Target Platform**: Android (minSdk 26, targetSdk 36, compileSdk 36)  
**Project Type**: Mobile (Android)  
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)  
**Constraints**: None - Google Maps API key already configured from map preview feature (067)  
**Scale/Scope**: Displays all pins in visible area (no client-side limit)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: Domain models, use cases, repositories, ViewModels in `/composeApp`
  - ✅ This feature is Android-only, all code in `/composeApp`
  - NO shared compiled code between platforms

- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - ✅ Single `StateFlow<FullscreenMapUiState>` source of truth with immutable data class
  - ✅ Sealed `FullscreenMapIntent` (Initialize, OnViewportChanged, OnAnimalTapped, OnPopupDismissed, OnRetryTapped, OnBackPressed)
  - ✅ Sealed `FullscreenMapEffect` (NavigateBack)
  - ✅ Reducers as pure functions, unit-tested
  - ✅ Navigation via Jetpack Navigation Component
  - ✅ Two-layer composable pattern: `FullscreenMapScreen` (stateful) + `FullscreenMapContent` (stateless)
  - ✅ `@Preview` with `@PreviewParameter` for stateless composable

- [x] **iOS MVVM-C Architecture**: N/A - Android only feature

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories
  - ✅ `AnnouncementsRepository` interface exists in domain layer
  - ✅ `GetAnnouncementsByLocationUseCase` will use repository interface

- [x] **Dependency Injection**: Plan includes DI setup
  - ✅ FullscreenMapViewModel registered in existing `ViewModelModule.kt`
  - ✅ Use case registered in existing `DomainModule.kt`
  - ✅ Follows centralized DI module pattern (not per-feature modules)

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests
  - ✅ Tests in `/composeApp/src/androidUnitTest/`
  - ✅ ViewModel tests with Turbine for StateFlow testing
  - ✅ Use case tests
  - ✅ Reducer tests (pure functions)

- [x] **End-to-End Tests**: Plan includes E2E tests
  - ✅ Appium tests via Cucumber in `/e2e-tests/java/`
  - ✅ Feature file: `mobile/fullscreen-map.feature`
  - ✅ Screen Object Model for map interactions

- [x] **Asynchronous Programming Standards**: Correct async patterns
  - ✅ Kotlin Coroutines with `viewModelScope`
  - ✅ `StateFlow` for UI state
  - ✅ `SharedFlow` for effects
  - ✅ No RxJava or LiveData

- [x] **Test Identifiers for UI Controls**: Test identifiers included
  - ✅ `fullscreenMap.header`, `fullscreenMap.backButton`, `fullscreenMap.title`
  - ✅ `fullscreenMap.legend`, `fullscreenMap.container`, `fullscreenMap.loading`
  - ✅ `fullscreenMap.error`, `fullscreenMap.retryButton`
  - ✅ `fullscreenMap.pin.${petId}`, `fullscreenMap.petPopup`

- [x] **Public API Documentation**: Public APIs documented when needed
  - ✅ KDoc for ViewModel, Use Cases, and complex functions
  - ✅ Skip documentation for self-explanatory methods

- [x] **Given-When-Then Test Structure**: All tests follow convention
  - ✅ Unit tests with backtick naming: `` `should load pins when map becomes visible` ``
  - ✅ Clear Given-When-Then phases in test body

### Backend Architecture & Quality Standards

- [x] **Backend Technology Stack**: N/A - No backend changes required (uses existing API)
- [x] **Backend Code Quality**: N/A - No backend changes
- [x] **Backend Dependency Management**: N/A - No backend changes
- [x] **Backend Directory Structure**: N/A - No backend changes
- [x] **Backend TDD Workflow**: N/A - No backend changes
- [x] **Backend Testing Strategy**: N/A - No backend changes

### Web Architecture & Quality Standards

- [x] **Web Technology Stack**: N/A - Android only feature
- [x] **Web Code Quality**: N/A - Android only feature
- [x] **Web Dependency Management**: N/A - Android only feature
- [x] **Web Business Logic Extraction**: N/A - Android only feature
- [x] **Web TDD Workflow**: N/A - Android only feature
- [x] **Web Testing Strategy**: N/A - Android only feature

## Project Structure

### Documentation (this feature)

```text
specs/071-android-fullscreen-map/
├── spec.md              # Feature specification
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # N/A (no new API contracts)
├── checklists/
│   └── requirements.md  # Quality checklist
└── tasks.md             # Phase 2 output (created by /speckit.tasks)
```

### Source Code (repository root)

```text
composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/
├── features/
│   └── fullscreenmap/
│       ├── ui/
│       │   ├── FullscreenMapScreen.kt          # State host composable
│       │   └── FullscreenMapContent.kt         # Stateless composable + previews
│       └── presentation/
│           ├── mvi/
│           │   ├── FullscreenMapUiState.kt     # Immutable UI state
│           │   ├── FullscreenMapIntent.kt      # Sealed intent class
│           │   ├── FullscreenMapEffect.kt      # Sealed effect class
│           │   └── FullscreenMapReducer.kt     # Pure reducer functions
│           └── viewmodels/
│               └── FullscreenMapViewModel.kt   # MVI ViewModel
├── domain/
│   └── usecases/
│       └── GetAnnouncementsByLocationUseCase.kt  # Existing or new use case
├── di/
│   ├── ViewModelModule.kt                      # Add FullscreenMapViewModel here
│   └── DomainModule.kt                         # Add use case here (if new)

composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/
├── features/
│   └── fullscreenmap/
│       ├── presentation/
│       │   ├── FullscreenMapViewModelTest.kt
│       │   └── FullscreenMapReducerTest.kt
│       └── domain/
│           └── GetAnnouncementsByLocationUseCaseTest.kt

e2e-tests/java/src/test/
├── resources/features/mobile/
│   └── fullscreen-map.feature                  # Cucumber scenarios
├── java/.../screens/
│   └── FullscreenMapScreen.java               # Screen Object Model
└── java/.../steps/mobile/
    └── FullscreenMapSteps.java                # Step definitions
```

**Structure Decision**: Android feature module pattern following existing codebase conventions. All code resides in `/composeApp` with MVI architecture co-located by feature.

### Reused Components

| Component | Location | Usage |
|-----------|----------|-------|
| `MapPreviewLegend` | `features/mapPreview/ui/components/MapPreviewLegend.kt` | Legend showing "● Missing ● Found" |
| `MapPreviewColors` | `features/mapPreview/ui/components/MapPreviewColors.kt` | Pin colors (red/blue) |
| `Animal` | `composeapp/domain/models/Animal.kt` | Pet data for pins and popup |
| `AnimalStatus` | `composeapp/domain/models/AnimalStatus.kt` | MISSING/FOUND status |
| `Location` | `composeapp/domain/models/Location.kt` | Coordinates |

## Complexity Tracking

No constitution violations requiring justification. Feature follows standard MVI pattern with existing infrastructure.
