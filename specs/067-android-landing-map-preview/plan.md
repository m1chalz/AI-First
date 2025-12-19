# Implementation Plan: Android Landing Page Map Preview

**Branch**: `067-android-landing-map-preview` | **Date**: 2025-12-19 | **Spec**: [spec.md](./spec.md)  
**Input**: Feature specification from `/specs/067-android-landing-map-preview/spec.md`

## Summary

Implement a static map preview component for the Android landing page (HomeScreen) that displays nearby missing (red) and found (blue) pet announcements within a 10km radius of the user's location. The component uses Google Maps SDK for map rendering, the existing announcements API with location filter for data, and follows MVI architecture with location permission handling.

## Technical Context

**Language/Version**: Kotlin (JVM 17)  
**Primary Dependencies**: Jetpack Compose, Google Maps SDK (Compose Maps), Koin, Ktor HTTP Client  
**Storage**: N/A (data from backend API)  
**Testing**: JUnit 6 + Kotlin Test + Turbine (Flow testing)  
**Target Platform**: Android (minSdk defined in project)  
**Project Type**: Mobile (Android)  
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)  
**Constraints**: Requires `ACCESS_COARSE_LOCATION` permission  
**Scale/Scope**: Single feature module integration into existing HomeScreen

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: Domain models, use cases, repositories, ViewModels in `/composeApp`
  - NO shared compiled code between platforms
  - Violation justification: N/A - Android only feature

- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - Single `StateFlow<MapPreviewUiState>` source of truth with immutable data classes
  - Sealed `MapPreviewIntent` for user interactions (LoadMap, RequestPermission, Retry)
  - Reducers implemented as pure functions and unit-tested
  - `dispatchIntent` entry wired from UI → ViewModel → reducer
  - Navigation not applicable (no navigation in this feature - out of scope)
  - Composable screens follow two-layer pattern: `MapPreviewSection` (stateful) + `MapPreviewContent` (stateless)
  - Stateless composables have `@Preview` with `@PreviewParameter` using `MapPreviewUiStateProvider`
  - Callback lambdas defaulted to no-ops in stateless composables
  - Violation justification: N/A - fully compliant

- [ ] **iOS MVVM-C Architecture**: N/A - Android only feature

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories
  - Android: Repository interface `MapPreviewRepository` in `/composeApp/src/androidMain/.../features/mapPreview/domain/repositories/`
  - Implementation in `.../data/repositories/`
  - Use case `GetNearbyAnnouncementsUseCase` references interface, not concrete implementation
  - Violation justification: N/A - fully compliant

- [x] **Dependency Injection**: Plan includes DI setup
  - Android: MUST use Koin - DI modules in `/composeApp/src/androidMain/.../di/`
  - New `mapPreviewModule` defining ViewModel, UseCase, Repository
  - Violation justification: N/A - fully compliant

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests
  - Android: Tests in `/composeApp/src/androidUnitTest/kotlin/.../features/mapPreview/`
  - Scope: MapPreviewViewModel, MapPreviewReducer, GetNearbyAnnouncementsUseCase
  - Coverage target: 80% line + branch coverage
  - Violation justification: N/A - fully compliant

- [ ] **End-to-End Tests**: Plan includes E2E tests
  - Mobile: Appium tests in `/e2e-tests/java/src/test/resources/features/mobile/map-preview.feature`
  - Screen Object Model for map preview section
  - At least one E2E test per user story
  - Violation justification: E2E tests to be added separately

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns
  - Android: Kotlin Coroutines (`viewModelScope`) + Flow for state
  - No Combine, RxJava, or callback-based patterns
  - Violation justification: N/A - fully compliant

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers
  - `testTag` modifier on all interactive composables
  - Tags: `mapPreview.container`, `mapPreview.header`, `mapPreview.legend`, `mapPreview.map`, `mapPreview.overlay`, `mapPreview.permissionButton`, `mapPreview.retryButton`, `mapPreview.loading`
  - Violation justification: N/A - fully compliant

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - KDoc format for complex functions (ViewModel, UseCase, Repository)
  - Skip documentation for self-explanatory methods
  - Violation justification: N/A - fully compliant

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then)
  - Test names use backticks: `` `should show map when permission granted and data loaded` ``
  - Violation justification: N/A - fully compliant

### Backend Architecture & Quality Standards (if `/server` affected)

- [ ] **Backend Technology Stack**: N/A - `/server` not affected (uses existing API)
- [ ] **Backend Code Quality**: N/A - `/server` not affected
- [ ] **Backend Dependency Management**: N/A - `/server` not affected
- [ ] **Backend Directory Structure**: N/A - `/server` not affected
- [ ] **Backend TDD Workflow**: N/A - `/server` not affected
- [ ] **Backend Testing Strategy**: N/A - `/server` not affected

### Web Architecture & Quality Standards (if `/webApp` affected)

- N/A - `/webApp` not affected

## Project Structure

### Documentation (this feature)

```text
specs/067-android-landing-map-preview/
├── spec.md              # Feature specification
├── plan.md              # This file
├── research.md          # Phase 0 output - resolved unknowns
├── data-model.md        # Phase 1 output - domain entities
├── quickstart.md        # Phase 1 output - developer guide
├── contracts/           # Phase 1 output - N/A (uses existing API)
├── checklists/
│   └── requirements.md  # Quality checklist
└── tasks.md             # Phase 2 output (created by /speckit.tasks)
```

### Source Code (repository root)

```text
composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/
├── features/
│   └── mapPreview/                    # NEW: Map preview feature module
│       ├── domain/
│       │   ├── models/
│       │   │   └── MapPin.kt          # Domain model for map pins
│       │   ├── repositories/
│       │   │   └── MapPreviewRepository.kt    # Repository interface
│       │   └── usecases/
│       │       └── GetNearbyAnnouncementsUseCase.kt  # Use case
│       ├── data/
│       │   └── repositories/
│       │       └── MapPreviewRepositoryImpl.kt  # Repository implementation
│       ├── presentation/
│       │   ├── mvi/
│       │   │   ├── MapPreviewUiState.kt     # Immutable UI state
│       │   │   ├── MapPreviewIntent.kt       # Sealed user intents
│       │   │   ├── MapPreviewEffect.kt       # One-off effects (if needed)
│       │   │   └── MapPreviewReducer.kt      # Pure reducer functions
│       │   └── viewmodels/
│       │       └── MapPreviewViewModel.kt    # MVI ViewModel
│       └── ui/
│           ├── MapPreviewSection.kt          # Stateful composable (state host)
│           ├── MapPreviewContent.kt          # Stateless composable (pure UI)
│           ├── MapPreviewLegend.kt           # Legend component
│           ├── MapPreviewOverlay.kt          # "Tap to view" overlay
│           └── PermissionRequestContent.kt   # Permission request UI
├── features/
│   └── home/
│       └── ui/
│           └── HomeScreen.kt          # MODIFIED: Add MapPreviewSection
├── di/
│   └── MapPreviewModule.kt            # NEW: Koin module for map preview
└── PetSpotApp.kt                      # MODIFIED: Register mapPreviewModule

composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/mapPreview/
├── presentation/
│   ├── viewmodels/
│   │   └── MapPreviewViewModelTest.kt
│   └── mvi/
│       └── MapPreviewReducerTest.kt
└── domain/
    └── usecases/
        └── GetNearbyAnnouncementsUseCaseTest.kt
```

**Structure Decision**: Feature module pattern following existing `lostPetsTeaser` and `animallist` modules. Map preview is a new feature under `/features/mapPreview/` with standard MVI architecture.

## Integration Points

### Existing Components to Reuse

| Component | Path | Usage |
|-----------|------|-------|
| `AnnouncementApiClient` | `data/api/AnnouncementApiClient.kt` | Already has `getAnnouncements(lat, lng, range)` with location filter |
| `AnnouncementDto` | `data/api/dto/AnnouncementDto.kt` | Has `locationLatitude`, `locationLongitude`, `status` fields |
| `AnimalStatus` | `domain/models/AnimalStatus.kt` | LOST/FOUND enum for pin colors |
| `HomeScreen` | `features/home/ui/HomeScreen.kt` | Add `MapPreviewSection` between hero and teaser |

### New Dependencies Required

| Dependency | Purpose | Gradle Artifact |
|------------|---------|-----------------|
| Google Maps Compose | Map rendering in Compose | `com.google.maps.android:maps-compose:4.x.x` |
| Google Play Services Location | Location services | `com.google.android.gms:play-services-location:21.x.x` |
| Accompanist Permissions | Permission handling | `com.google.accompanist:accompanist-permissions:0.x.x` |

### API Integration

The existing `AnnouncementApiClient.getAnnouncements(lat, lng, range)` already supports location-based filtering:

```kotlin
// Existing API call - no backend changes needed
suspend fun getAnnouncements(
    lat: Double? = null,
    lng: Double? = null,
    range: Int? = null,  // in km
): AnnouncementsResponseDto
```

Usage for 10km radius: `apiClient.getAnnouncements(lat = userLat, lng = userLng, range = 10)`

## Complexity Tracking

> No constitution violations requiring justification.

| Aspect | Decision | Rationale |
|--------|----------|-----------|
| No new backend endpoint | Reuse existing `/api/v1/announcements` with location filter | API already supports `lat`, `lng`, `range` parameters |
| Static map (no interaction) | Google Maps Compose with `MapProperties(gesturesEnabled = false)` | Simpler than Static Maps API URL approach, allows pins overlay |
| Permission handling | Accompanist Permissions library | Declarative permission handling in Compose, widely used |
