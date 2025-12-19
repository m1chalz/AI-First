# Tasks: Android Landing Page Map Preview

**Input**: Design documents from `/specs/067-android-landing-map-preview/`  
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md  
**Platform**: Android only (no iOS, Web, or Backend changes)

**Tests**: Test requirements for this project:

**MANDATORY - Android Unit Tests**:
- Location: `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/mapPreview/`
- Framework: JUnit 6 + Kotlin Test + Turbine (Flow testing)
- Scope: Use cases, ViewModels (MVI architecture), reducers
- Coverage target: 80% line + branch coverage
- Run: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- Convention: MUST follow Given-When-Then structure with descriptive names

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

---

## Phase 1: Setup

**Purpose**: Add Google Maps dependency and create feature module structure

- [ ] T001 Add Google Maps Compose dependencies to `/composeApp/build.gradle.kts` (`maps-compose:6.12.2`, `play-services-maps:19.2.0`)
- [ ] T002 [P] Verify Google Maps API key is configured in `/composeApp/src/androidMain/AndroidManifest.xml` (add meta-data if missing)
- [ ] T003 [P] Verify `ACCESS_COARSE_LOCATION` permission is declared in `/composeApp/src/androidMain/AndroidManifest.xml`
- [ ] T004 Create feature module directory structure at `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/mapPreview/{domain/usecases,presentation/mvi,presentation/viewmodels,ui}`
- [ ] T005 [P] Create test directory structure at `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/mapPreview/{domain/usecases,presentation/viewmodels}`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core MVI artifacts and use case that MUST be complete before UI implementation

**⚠️ CRITICAL**: No UI work can begin until this phase is complete

- [ ] T006 Create `MapPreviewUiState` data class in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/mapPreview/presentation/mvi/MapPreviewUiState.kt` (use existing `PermissionStatus`, `LocationCoordinates`, `Animal` models)
- [ ] T007 [P] Create `MapPreviewIntent` sealed interface in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/mapPreview/presentation/mvi/MapPreviewIntent.kt` (`LoadMap`, `RequestPermission`, `PermissionGranted`, `PermissionDenied`, `Retry`)
- [ ] T008 [P] Create `MapPreviewEffect` sealed interface in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/mapPreview/presentation/mvi/MapPreviewEffect.kt` (empty for now, reserved for future navigation)
- [ ] T009 [P] Create `MapPreviewError` sealed interface in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/mapPreview/presentation/mvi/MapPreviewError.kt` (`LocationNotAvailable`, `NetworkError`, `MapLoadFailed`)
- [ ] T010 [P] Create `MapPreviewReducer` object in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/mapPreview/presentation/mvi/MapPreviewReducer.kt` (pure function: `reduce(state, result) -> state`)
- [ ] T011 Create `GetNearbyAnimalsForMapUseCase` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/mapPreview/domain/usecases/GetNearbyAnimalsForMapUseCase.kt` (wraps existing `AnimalRepository` with 10km radius)
- [ ] T012 Add `GetNearbyAnimalsForMapUseCase` factory to `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/DomainModule.kt`
- [ ] T013 [P] Create `LocationCoordinates.toLatLng()` extension function in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/mapPreview/domain/extensions/LocationExtensions.kt`
- [ ] T014 [P] Create `AnimalStatus.toMarkerIcon()` extension function in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/mapPreview/ui/extensions/MarkerExtensions.kt`

**Checkpoint**: Foundation ready - ViewModel and UI implementation can now begin

---

## Phase 3: User Story 1 - View Map Preview on Landing Page (Priority: P1) 🎯 MVP

**Goal**: User sees a map preview section on the landing page showing nearby lost/found pets with pins

**Independent Test**: Open landing page with location permission granted and verify map preview appears with visible pins

### Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**Existing Fakes to Reuse** (no new fakes needed):
- `FakeAnimalRepository` at `/composeApp/src/androidMain/.../composeapp/domain/repositories/FakeAnimalRepository.kt`
- `FakeLocationRepository` at `/composeApp/src/androidUnitTest/.../fakes/FakeLocationRepository.kt`

**Android Unit Tests**:
- [ ] T015 [P] [US1] Unit test for `GetNearbyAnimalsForMapUseCase` in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/mapPreview/domain/usecases/GetNearbyAnimalsForMapUseCaseTest.kt` (use existing `FakeAnimalRepository`, test success, empty list, failure cases)
- [ ] T016 [P] [US1] Unit test for `MapPreviewReducer` in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/mapPreview/presentation/mvi/MapPreviewReducerTest.kt` (test pure reduce function with various inputs)
- [ ] T017 [P] [US1] Unit test for `MapPreviewViewModel` in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/mapPreview/presentation/viewmodels/MapPreviewViewModelTest.kt` (use existing fakes, test `LoadMap` intent → loading → success state with Turbine)

### Implementation for User Story 1

**ViewModel**:
- [ ] T018 [US1] Create `MapPreviewViewModel` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/mapPreview/presentation/viewmodels/MapPreviewViewModel.kt` (inject `GetCurrentLocationUseCase`, `CheckLocationPermissionUseCase`, `GetNearbyAnimalsForMapUseCase`, uses `MapPreviewReducer`)
- [ ] T019 [US1] Implement `LoadMap` intent handler in `MapPreviewViewModel` (fetch location → fetch animals → call reducer → update state)
- [ ] T020 [US1] Implement `Retry` intent handler in `MapPreviewViewModel` (re-attempt `LoadMap` flow)
- [ ] T021 [US1] Add `MapPreviewViewModel` to `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/ViewModelModule.kt`

**UI Composables**:
- [ ] T022 [P] [US1] Create `MapPreviewUiStateProvider` (PreviewParameterProvider) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/mapPreview/ui/preview/MapPreviewUiStateProvider.kt` (loading, success with pins, error, permission states)
- [ ] T023 [US1] Create `MapPreviewLegend` composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/mapPreview/ui/components/MapPreviewLegend.kt` (red dot "Missing", blue dot "Found")
- [ ] T024 [US1] Create `MapPreviewOverlay` composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/mapPreview/ui/components/MapPreviewOverlay.kt` (white pill "Tap to view interactive map")
- [ ] T025 [US1] Create `MapPreviewContent` stateless composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/mapPreview/ui/MapPreviewContent.kt` (GoogleMap with pins, legend, overlay, loading, error states, 0.667px border per FR-016)
- [ ] T026 [US1] Add `@Preview` function for `MapPreviewContent` using `@PreviewParameter(MapPreviewUiStateProvider::class)` in same file
- [ ] T027 [US1] Add `testTag` modifiers to all interactive elements in `MapPreviewContent` (`mapPreview.container`, `mapPreview.header`, `mapPreview.legend`, `mapPreview.map`, `mapPreview.overlay`, `mapPreview.loading`, `mapPreview.error`, `mapPreview.retryButton`)
- [ ] T028 [US1] Create `MapPreviewSection` state host composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/mapPreview/ui/MapPreviewSection.kt` (collects state from ViewModel, dispatches intents, delegates to `MapPreviewContent`)

**Integration**:
- [ ] T029 [US1] Add `MapPreviewSection` to `HomeScreen` LazyColumn in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/home/ui/HomeScreen.kt` (between hero section and Recent Reports)

**Checkpoint**: User Story 1 is complete - map preview with pins should display when permission is granted

---

## Phase 4: User Story 2 - Grant Location Permission (Priority: P2)

**Goal**: User who hasn't granted location permission sees an informational state with "Enable Location" button

**Independent Test**: Open landing page without location permission and verify permission request UI appears

### Tests for User Story 2 (MANDATORY) ✅

**Android Unit Tests**:
- [ ] T030 [P] [US2] Add test cases to `MapPreviewViewModelTest` for permission flow: `PermissionGranted` → triggers `LoadMap`, `PermissionDenied` → updates state to denied
- [ ] T031 [P] [US2] Add test case for `RequestPermission` intent in `MapPreviewViewModelTest`

### Implementation for User Story 2

**ViewModel**:
- [ ] T032 [US2] Implement `PermissionGranted` intent handler in `MapPreviewViewModel` (update permission status, trigger `LoadMap`)
- [ ] T033 [US2] Implement `PermissionDenied` intent handler in `MapPreviewViewModel` (update permission status to denied)
- [ ] T034 [US2] Implement `RequestPermission` intent handler in `MapPreviewViewModel` (set permission status to requesting)

**UI Composables**:
- [ ] T035 [US2] Create `PermissionRequestContent` composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/mapPreview/ui/components/PermissionRequestContent.kt` (informational text, "Enable Location" button)
- [ ] T036 [US2] Add `testTag` modifiers to `PermissionRequestContent` (`mapPreview.permissionMessage`, `mapPreview.permissionButton`)
- [ ] T037 [US2] Integrate `PermissionRequestContent` into `MapPreviewContent` (show when permission not granted)
- [ ] T038 [US2] Wire Accompanist `rememberPermissionState` in `MapPreviewSection` (connect to ViewModel intents)

**Checkpoint**: User Story 2 is complete - permission request UI should appear when permission not granted

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T039 Add loading shimmer effect to `MapPreviewContent` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/mapPreview/ui/MapPreviewContent.kt`
- [ ] T040 [P] Add KDoc documentation to `MapPreviewViewModel`, `MapPreviewReducer`, and `GetNearbyAnimalsForMapUseCase` (WHAT/WHY, not HOW)
- [ ] T041 Run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` and verify 80%+ coverage for new code
- [ ] T042 [P] Run lint check and fix any issues in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/mapPreview/`
- [ ] T043 Manual testing on device/emulator (permission flow, map rendering, pin display, verify 2s load time per SC-001)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational phase completion
- **User Story 2 (Phase 4)**: Depends on User Story 1 completion (extends same components)
- **Polish (Phase 5)**: Depends on all user stories being complete

### Within Each User Story

- Tests MUST be written and FAIL before implementation
- Use cases before ViewModels
- ViewModels before UI composables
- Stateless composables before state host composables
- State host composables before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- All test tasks marked [P] can run in parallel (within each phase)
- T022, T023, T024 (UI components) can run in parallel

---

## Parallel Example: User Story 1

```bash
# Launch all tests together (after Foundational phase):
T015: GetNearbyAnimalsForMapUseCase test
T016: MapPreviewReducer test
T017: MapPreviewViewModel test

# Then launch UI components together (after ViewModel complete):
T022: MapPreviewUiStateProvider
T023: MapPreviewLegend
T024: MapPreviewOverlay
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (dependencies, manifest)
2. Complete Phase 2: Foundational (MVI artifacts, use case)
3. Complete Phase 3: User Story 1 (ViewModel, UI, integration)
4. **STOP and VALIDATE**: Test map preview with permission granted
5. Deploy/demo if ready

### Incremental Delivery

1. Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Demo (MVP!)
3. Add User Story 2 → Test independently → Complete feature
4. Polish → Final QA → Release

---

## Summary

| Phase | Task Count | Purpose |
|-------|------------|---------|
| Setup | 5 | Dependencies, manifest, directories |
| Foundational | 9 | MVI artifacts (incl. Reducer), use case, extensions |
| User Story 1 (P1) | 15 | Map preview with pins (MVP) |
| User Story 2 (P2) | 9 | Permission request flow |
| Polish | 5 | Shimmer, docs, coverage, lint, testing |
| **Total** | **43** | |

**MVP Scope**: Phases 1-3 (29 tasks) = Map preview working with location permission granted

**Reused Components**: `Animal`, `AnimalStatus`, `Location`, `LocationCoordinates`, `PermissionStatus`, `AnimalRepository`, `GetCurrentLocationUseCase`, `CheckLocationPermissionUseCase`, `locationModule`, `dataModule`, Accompanist Permissions

**New Components**: 
- `GetNearbyAnimalsForMapUseCase` (thin wrapper)
- `MapPreviewViewModel` (MVI)
- MVI artifacts (UiState, Intent, Effect, Error)
- UI composables (Section, Content, Legend, Overlay, PermissionRequest)
- Extension functions (toLatLng, toMarkerIcon)

