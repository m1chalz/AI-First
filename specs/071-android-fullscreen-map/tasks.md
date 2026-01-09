# Tasks: Android Fullscreen Interactive Map

**Input**: Design documents from `/specs/071-android-fullscreen-map/`  
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, quickstart.md ✅

**Tests**: Test requirements for this project:

**MANDATORY - Android Unit Tests**:
- Location: `/composeApp/src/androidUnitTest/`
- Framework: JUnit Jupiter 6.0.1 + Kotlin Test + Turbine 1.1.0
- Coverage: 80% for ViewModel, Reducer, Use Cases
- Convention: Given-When-Then with backtick naming
- Run: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`

**MANDATORY - End-to-End Tests**:
- Location: `/e2e-tests/java/src/test/resources/features/mobile/`
- Framework: Java 21 + Appium + Cucumber
- All user stories MUST have E2E test coverage
- Use Screen Object Model pattern

**Platform**: Android only (no iOS/Web/Backend changes required)

**Organization**: Tasks are grouped by user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3, US4)
- Paths are relative to repository root

---

## Phase 1: Setup (No New Dependencies Needed)

**Purpose**: Project structure and feature directory creation

**Note**: Maps SDK, API key, and base components already configured from 067-android-landing-map-preview ✅

- [ ] T001 Create feature directory structure at `composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/fullscreenmap/`
- [ ] T002 [P] Create `ui/` subdirectory for composables
- [ ] T003 [P] Create `presentation/mvi/` subdirectory for MVI artifacts
- [ ] T004 [P] Create `presentation/viewmodels/` subdirectory for ViewModel
- [ ] T005 Create test directory at `composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/fullscreenmap/`

---

## Phase 2: Foundational (MVI Contracts + Core Infrastructure)

**Purpose**: Core MVI types that ALL user stories depend on - MUST complete before story implementation

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T006 Create `FullscreenMapUiState.kt` with immutable state data class in `composeApp/src/androidMain/.../features/fullscreenmap/presentation/mvi/FullscreenMapUiState.kt`
- [ ] T007 [P] Create `FullscreenMapIntent.kt` with sealed interface in `composeApp/src/androidMain/.../features/fullscreenmap/presentation/mvi/FullscreenMapIntent.kt`
- [ ] T008 [P] Create `FullscreenMapEffect.kt` with sealed interface in `composeApp/src/androidMain/.../features/fullscreenmap/presentation/mvi/FullscreenMapEffect.kt`
- [ ] T009 Create `FullscreenMapReducer.kt` with pure reducer functions in `composeApp/src/androidMain/.../features/fullscreenmap/presentation/mvi/FullscreenMapReducer.kt`
- [ ] T010 Create `FullscreenMapReducerTest.kt` with unit tests for all reducer functions in `composeApp/src/androidUnitTest/.../features/fullscreenmap/presentation/FullscreenMapReducerTest.kt`
- [ ] T011 Create `FakeGetNearbyAnimalsForMapUseCase.kt` test fake in `composeApp/src/androidUnitTest/.../features/fullscreenmap/fakes/FakeGetNearbyAnimalsForMapUseCase.kt`
- [ ] T012 [P] Create `FakeLocationProvider.kt` test fake in `composeApp/src/androidUnitTest/.../features/fullscreenmap/fakes/FakeLocationProvider.kt`

**Checkpoint**: MVI contracts ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Open Fullscreen Interactive Map (Priority: P1) 🎯 MVP

**Goal**: User can tap map preview on landing page to open fullscreen map, then return via back arrow or system back button

**Independent Test**: Tap landing page map preview → fullscreen map opens; tap back → returns to landing page with state preserved

### Tests for User Story 1 (MANDATORY) ✅

- [ ] T013 [P] [US1] Unit test for back navigation in `composeApp/src/androidUnitTest/.../features/fullscreenmap/presentation/FullscreenMapViewModelTest.kt` - test `OnBackPressed` intent emits `NavigateBack` effect
- [ ] T014 [P] [US1] Unit test for initialization in `composeApp/src/androidUnitTest/.../features/fullscreenmap/presentation/FullscreenMapViewModelTest.kt` - test `Initialize` intent sets initial state

### Implementation for User Story 1

- [ ] T015 [US1] Create `FullscreenMapViewModel.kt` skeleton with state/effects flows and `dispatchIntent()` in `composeApp/src/androidMain/.../features/fullscreenmap/presentation/viewmodels/FullscreenMapViewModel.kt`
- [ ] T016 [US1] Implement `Initialize` and `OnBackPressed` intent handling in ViewModel
- [ ] T017 [US1] Create `FullscreenMapContent.kt` stateless composable with header bar (back arrow + title) and legend in `composeApp/src/androidMain/.../features/fullscreenmap/ui/FullscreenMapContent.kt`
- [ ] T018 [US1] Add `TopAppBar` with testTag (`fullscreenMap.header`), back button (`fullscreenMap.backButton`) and title (`fullscreenMap.title`) to FullscreenMapContent
- [ ] T019 [US1] Import and add `MapPreviewLegend` component with testTag (`fullscreenMap.legend`) to FullscreenMapContent
- [ ] T020 [US1] Add `GoogleMap` composable container with testTag (`fullscreenMap.container`) to FullscreenMapContent
- [ ] T021 [US1] Create `FullscreenMapScreen.kt` state host composable in `composeApp/src/androidMain/.../features/fullscreenmap/ui/FullscreenMapScreen.kt`
- [ ] T022 [US1] Wire `collectAsStateWithLifecycle()` and effect collection in FullscreenMapScreen
- [ ] T023 [US1] Add `BackHandler` for system back button support in FullscreenMapScreen
- [ ] T024 [US1] Register `FullscreenMapViewModel` in `composeApp/src/androidMain/.../di/ViewModelModule.kt`
- [ ] T025 [US1] Add navigation route `"fullscreenMap"` to NavGraph in `composeApp/src/androidMain/.../navigation/`
- [ ] T026 [US1] Wire navigation from map preview tap to fullscreen map route (update landing page preview click handler)
- [ ] T027 [P] [US1] Create E2E feature file `fullscreen-map.feature` in `e2e-tests/java/src/test/resources/features/mobile/fullscreen-map.feature`
- [ ] T028 [P] [US1] Create Screen Object `FullscreenMapScreen.java` in `e2e-tests/java/src/test/java/.../screens/FullscreenMapScreen.java`
- [ ] T029 [US1] Add E2E scenario "User opens fullscreen map from preview" in feature file
- [ ] T030 [US1] Add E2E scenario "User returns to landing page via back button" in feature file

**Checkpoint**: User Story 1 complete - fullscreen map opens/closes, no pins yet

---

## Phase 4: User Story 2 - Navigate the Fullscreen Map (Priority: P2)

**Goal**: User can zoom (pinch, double-tap) and pan (drag) the map to explore different areas

**Independent Test**: Open fullscreen map → use zoom/pan gestures → visible area updates accordingly

### Tests for User Story 2 (MANDATORY) ✅

- [ ] T031 [P] [US2] Unit test for viewport changed intent in `FullscreenMapViewModelTest.kt` - test `OnViewportChanged` triggers pin loading

### Implementation for User Story 2

- [ ] T032 [US2] Add `CameraPositionState` handling in FullscreenMapScreen with `LaunchedEffect` for camera idle detection
- [ ] T033 [US2] Implement `OnViewportChanged` intent dispatch when camera stops moving
- [ ] T034 [US2] Verify GoogleMap composable supports pinch-to-zoom (default behavior) - no code needed, add acceptance note
- [ ] T035 [US2] Verify GoogleMap composable supports pan/drag (default behavior) - no code needed, add acceptance note
- [ ] T036 [US2] Verify GoogleMap composable supports double-tap zoom (default behavior) - no code needed, add acceptance note
- [ ] T037 [P] [US2] Add E2E scenario "User zooms in using pinch gesture" in feature file
- [ ] T038 [P] [US2] Add E2E scenario "User pans the map" in feature file

**Checkpoint**: User Story 2 complete - map is fully navigable

---

## Phase 5: User Story 3 - View Pet Pins on the Map (Priority: P3)

**Goal**: Red pins for missing pets and blue pins for found pets are displayed on the map, updating when viewport changes

**Independent Test**: Open fullscreen map in area with pet announcements → pins appear at correct locations with correct colors

### Tests for User Story 3 (MANDATORY) ✅

- [ ] T039 [P] [US3] Unit test for loading state in `FullscreenMapViewModelTest.kt` - test loading indicator shown while fetching
- [ ] T040 [P] [US3] Unit test for success state in `FullscreenMapViewModelTest.kt` - test animals loaded and displayed
- [ ] T041 [P] [US3] Unit test for error state in `FullscreenMapViewModelTest.kt` - test error message shown on failure
- [ ] T042 [P] [US3] Unit test for retry in `FullscreenMapViewModelTest.kt` - test `OnRetryTapped` re-fetches pins

### Implementation for User Story 3

- [ ] T043 [US3] Implement pin loading logic in ViewModel using `GetNearbyAnimalsForMapUseCase` (reuse existing or create wrapper)
- [ ] T044 [US3] Add loading state handling with `reduceAnimalsLoading` reducer
- [ ] T045 [US3] Add success state handling with `reduceAnimalsSuccess` reducer
- [ ] T046 [US3] Add error state handling with `reduceAnimalsError` reducer
- [ ] T047 [US3] Implement `OnRetryTapped` intent handling in ViewModel
- [ ] T048 [US3] Add `Marker` composables in GoogleMap for each `animalsWithLocation` with testTag (`fullscreenMap.pin.${animalId}`) in FullscreenMapContent
- [ ] T049 [US3] Set marker color based on `AnimalStatus` (RED for MISSING, BLUE for FOUND) using `BitmapDescriptorFactory`
- [ ] T050 [US3] Add loading indicator (`CircularProgressIndicator`) overlay with testTag (`fullscreenMap.loading`) in FullscreenMapContent
- [ ] T051 [US3] Create `ErrorCard` composable (or reuse existing) with error message and retry button in FullscreenMapContent
- [ ] T052 [US3] Add testTags to error state (`fullscreenMap.error`) and retry button (`fullscreenMap.retryButton`)
- [ ] T053 [US3] Register use case in `DomainModule.kt` if new use case created
- [ ] T054 [P] [US3] Add E2E scenario "User sees red pins for missing pets" in feature file
- [ ] T055 [P] [US3] Add E2E scenario "User sees blue pins for found pets" in feature file
- [ ] T056 [P] [US3] Add E2E scenario "Pins update when user pans map" in feature file

**Checkpoint**: User Story 3 complete - pins display correctly with loading/error states

---

## Phase 6: User Story 4 - View Pet Details from a Pin (Priority: P4)

**Goal**: User taps a pin and sees a bottom sheet pop-up with pet photo, name, species, last-seen date, description, and contact info

**Independent Test**: Tap any pin → pop-up appears with pet details; tap outside or swipe → pop-up dismisses

### Tests for User Story 4 (MANDATORY) ✅

- [ ] T057 [P] [US4] Unit test for pin tap in `FullscreenMapViewModelTest.kt` - test `OnAnimalTapped` sets `selectedAnimal`
- [ ] T058 [P] [US4] Unit test for popup dismiss in `FullscreenMapViewModelTest.kt` - test `OnPopupDismissed` clears `selectedAnimal`
- [ ] T059 [P] [US4] Unit test for tap different pin in `FullscreenMapViewModelTest.kt` - test tapping new pin updates `selectedAnimal`

### Implementation for User Story 4

- [ ] T060 [US4] Add `reduceAnimalSelected` and `reducePopupDismissed` calls in ViewModel intent handling
- [ ] T061 [US4] Implement `OnAnimalTapped` intent - find animal by ID and update state
- [ ] T062 [US4] Implement `OnPopupDismissed` intent - clear selectedAnimal
- [ ] T063 [US4] Add `onClick` handler to each `Marker` to dispatch `OnAnimalTapped` intent
- [ ] T064 [US4] Create `AnimalDetailsBottomSheet.kt` composable (or reuse `AnimalDetailsContent`) in `composeApp/src/androidMain/.../features/fullscreenmap/ui/components/AnimalDetailsBottomSheet.kt`
- [ ] T065 [US4] Add pet photo (with placeholder on error), name, species, last-seen date, description to bottom sheet
- [ ] T066 [US4] Add owner contact info (phone/email as plain text, non-tappable) to bottom sheet
- [ ] T067 [US4] Add `ModalBottomSheet` wrapper in FullscreenMapContent when `selectedAnimal != null`
- [ ] T068 [US4] Wire `onDismissRequest` to dispatch `OnPopupDismissed` intent
- [ ] T069 [US4] Add testTag (`fullscreenMap.petPopup`) to bottom sheet content
- [ ] T069a [US4] Add close/dismiss icon button with testTag (`fullscreenMap.petPopup.close`) to bottom sheet header
- [ ] T070 [P] [US4] Add E2E scenario "User views pet details by tapping pin" in feature file
- [ ] T071 [P] [US4] Add E2E scenario "User dismisses popup by tapping outside" in feature file
- [ ] T072 [P] [US4] Add E2E scenario "User taps different pin to update popup" in feature file

**Checkpoint**: User Story 4 complete - pet details popup works

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Previews, documentation, final verification

- [ ] T073 Create `FullscreenMapStateProvider.kt` with preview states (Initial, Loading, Success with pins, Error, Popup visible) in `composeApp/src/androidMain/.../features/fullscreenmap/ui/FullscreenMapStateProvider.kt`
- [ ] T074 Add `@Preview` function with `@PreviewParameter` to `FullscreenMapContent.kt`
- [ ] T075 [P] Add KDoc to `FullscreenMapViewModel` public methods
- [ ] T076 [P] Add KDoc to `FullscreenMapReducer` functions
- [ ] T077 Run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` and verify 80% coverage for fullscreenmap package
- [ ] T078 Run E2E tests with `mvn test -Dtest=AndroidTestRunner` from `e2e-tests/java/` and verify all scenarios pass
- [ ] T079 Update `spec.md` estimation table with final task count and days

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: No dependencies - can start immediately
- **Phase 2 (Foundational)**: Depends on Phase 1 - BLOCKS all user stories
- **Phase 3-6 (User Stories)**: All depend on Phase 2 completion
  - US1 → US2 → US3 → US4 (sequential recommended for MVP-first approach)
  - Or parallel if multiple developers available
- **Phase 7 (Polish)**: Depends on all user stories being complete

### User Story Dependencies

- **US1 (P1)**: Can start after Phase 2 - No dependencies on other stories
- **US2 (P2)**: Can start after Phase 2 - Builds on US1 (map must open first)
- **US3 (P3)**: Can start after Phase 2 - Requires map from US1, viewport tracking from US2
- **US4 (P4)**: Can start after US3 - Requires pins to exist for tap interaction

### Within Each User Story

1. Tests FIRST (verify they fail)
2. ViewModel/logic implementation
3. UI implementation
4. Integration (DI, Navigation)
5. E2E tests

### Parallel Opportunities

- T002, T003, T004 can run in parallel (directory creation)
- T007, T008, T012 can run in parallel (independent MVI files)
- All test tasks marked [P] within a story can run in parallel
- E2E scenario tasks marked [P] can run in parallel

---

## Parallel Example: User Story 3

```bash
# Launch tests in parallel:
T039: Unit test for loading state
T040: Unit test for success state  
T041: Unit test for error state
T042: Unit test for retry

# After tests pass, launch E2E scenarios in parallel:
T054: E2E scenario "User sees red pins"
T055: E2E scenario "User sees blue pins"
T056: E2E scenario "Pins update on pan"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup ✅
2. Complete Phase 2: Foundational (MVI contracts)
3. Complete Phase 3: User Story 1 (open/close map)
4. **STOP and VALIDATE**: Test map opens and closes correctly
5. Deploy/demo if ready

### Incremental Delivery

1. Setup + Foundational → Core infrastructure ready
2. Add US1 → Map opens/closes → Demo ✅
3. Add US2 → Map is navigable → Demo ✅
4. Add US3 → Pins display → Demo ✅
5. Add US4 → Details popup → Demo ✅ (Feature complete!)

### Time Estimates (from quickstart.md)

| Component | Estimate |
|-----------|----------|
| MVI Contracts (Phase 2) | 30 min |
| Reducer + Tests | 1 hour |
| ViewModel | 2 hours |
| Stateless Composable | 2 hours |
| State Host Composable | 30 min |
| Koin Registration | 15 min |
| Navigation | 30 min |
| **Total Implementation** | ~7 hours |
| Unit Tests | +2 hours |
| E2E Tests | +2 hours |
| **Grand Total** | ~11 hours |

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story
- All testTags follow pattern `fullscreenMap.{element}.{action}`
- Reuse existing components: `MapPreviewLegend`, `Animal` model, `GetNearbyAnimalsForMapUseCase`
- No backend changes required - uses existing announcements API
- Maps SDK already configured from 067
