# Tasks: iOS Fullscreen Map - Fetch and Display Pins

**Input**: Design documents from `/specs/KAN-32-ios-fullscreen-map-fetch-pins/`
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, contracts/announcements-api.md ✅, quickstart.md ✅

**Platform**: iOS only (no Android, Web, or Backend changes required)

**Tests**: Test requirements for this project:

**MANDATORY - iOS Unit Tests**:
- Location: `/iosApp/iosAppTests/Features/LandingPage/Views/`
- Framework: XCTest with Swift Concurrency (async/await)
- Coverage: 80% minimum for FullscreenMapViewModel
- Run: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
- Convention: MUST follow Given-When-Then structure with descriptive names (`testMethod_whenCondition_shouldBehavior`)

**E2E Tests**: Deferred to future spec (pin interaction feature)

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

---

## Phase 1: Setup

**Purpose**: Verify prerequisites and prepare development environment

- [X] T001 Verify branch `KAN-32-ios-fullscreen-map-fetch-pins` is checked out
- [X] T002 Verify iOS 18 SDK available (Xcode 16+) and iPhone 16 Simulator configured
- [X] T003 Verify backend server running at configured API URL (existing endpoint required)
- [X] T004 Verify `KAN-32-ios-display-fullscreen-map` feature is implemented (interactive map with legend)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T005 [P] Create `MKCoordinateRegion+Radius.swift` extension in `/iosApp/iosApp/Domain/Models/MKCoordinateRegion+Radius.swift` with `radiusInKilometers` computed property
- [X] T006 [P] Create `MapPin.swift` model in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/MapPin.swift` conforming to `Identifiable` and `Equatable`
- [X] T007 [P] Reuse existing `FakeAnnouncementRepository` in `/iosApp/iosAppTests/Fakes/FakeAnnouncementRepository.swift` for unit testing (no new mock file)

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Display Missing Animal Pins on Map Load (Priority: P1) 🎯 MVP

**Goal**: Users see pins for missing animals when opening the fullscreen map so they can immediately spot cases in their area.

**Independent Test**: Open fullscreen map and verify pins appear for missing animals in the initially visible area; verify pins are positioned correctly using last-seen coordinates.

### Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**iOS Unit Tests** (Given-When-Then structure):

- [X] T008 [P] [US1] Unit test `testLoadPins_whenViewAppears_shouldSetIsLoadingTrue` in `/iosApp/iosAppTests/Features/LandingPage/Views/FullscreenMapViewModelTests.swift`
- [X] T009 [P] [US1] Unit test `testLoadPins_whenRepositoryReturnsAnnouncements_shouldMapAllToMapPins` in `/iosApp/iosAppTests/Features/LandingPage/Views/FullscreenMapViewModelTests.swift`
- [X] T010 [P] [US1] Unit test `testLoadPins_whenRepositoryReturnsAnnouncements_shouldMapToMapPins` in `/iosApp/iosAppTests/Features/LandingPage/Views/FullscreenMapViewModelTests.swift`
- [X] T011 [P] [US1] Unit test `testLoadPins_whenRepositoryReturnsEmptyArray_shouldSetEmptyPins` in `/iosApp/iosAppTests/Features/LandingPage/Views/FullscreenMapViewModelTests.swift`
- [X] T012 [P] [US1] Unit test `testLoadPins_whenRepositoryThrowsError_shouldKeepExistingPins` in `/iosApp/iosAppTests/Features/LandingPage/Views/FullscreenMapViewModelTests.swift`
- [X] T013 [P] [US1] Unit test `testLoadPins_whenComplete_shouldSetIsLoadingFalse` in `/iosApp/iosAppTests/Features/LandingPage/Views/FullscreenMapViewModelTests.swift`

### Implementation for User Story 1

**iOS Implementation**:

- [X] T014 [US1] Add `repository: AnnouncementRepositoryProtocol` dependency to `FullscreenMapViewModel` initializer in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift`
- [X] T015 [US1] Add `@Published private(set) var pins: [MapPin] = []` property to `FullscreenMapViewModel` in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift`
- [X] T016 [US1] Add `@Published private(set) var isLoading = false` property to `FullscreenMapViewModel` in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift`
- [X] T017 [US1] Add `private var fetchTask: Task<Void, Never>?` property for task cancellation in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift`
- [X] T018 [US1] Implement `loadPins() async` method that fetches announcements and maps all to `MapPin` array (no status filtering) in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift`
- [X] T019 [US1] Implement silent error handling in `loadPins()` - catch errors, log to console, keep existing pins in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift`
- [X] T020 [US1] Update `HomeCoordinator.showFullscreenMap()` to inject `repository` dependency into ViewModel in `/iosApp/iosApp/Features/LandingPage/Coordinators/HomeCoordinator.swift`
- [X] T021 [US1] Add `.task { await viewModel.loadPins() }` modifier to Map in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapView.swift`
- [X] T022 [US1] Add `ForEach(viewModel.pins)` with `Annotation` displaying classic map pin markers (`mappin.circle.fill` SF Symbol, red) in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapView.swift`
- [X] T023 [US1] Update pins without animation (instant update; no `withAnimation` fade-in)
- [X] T024 [US1] Add `.accessibilityIdentifier("fullscreenMap.pin.\(pin.id)")` to each pin Annotation in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapView.swift`

**Checkpoint**: At this point, User Story 1 should be fully functional - pins appear on map load

---

## Phase 4: User Story 2 - Refresh Pins After Map Movement (Priority: P2)

**Goal**: Pins update automatically after moving or zooming the map so users always see relevant missing animals in the current view.

**Independent Test**: Pan/zoom the map and verify pins refresh after the gesture ends; verify new pins appear and out-of-view pins are removed.

### Tests for User Story 2 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**iOS Unit Tests** (Given-When-Then structure):

- [X] T025 [P] [US2] Unit test `testHandleRegionChange_whenCalled_shouldFetchPinsForNewRegion` in `/iosApp/iosAppTests/Features/LandingPage/Views/FullscreenMapViewModelTests.swift`
- [X] T026 [P] [US2] Unit test `testHandleRegionChange_whenCalledRapidly_shouldCancelPreviousTask` in `/iosApp/iosAppTests/Features/LandingPage/Views/FullscreenMapViewModelTests.swift`
- [X] T027 [P] [US2] Unit test `testHandleRegionChange_whenRepositoryFails_shouldKeepExistingPins` in `/iosApp/iosAppTests/Features/LandingPage/Views/FullscreenMapViewModelTests.swift`
- [X] T028 [P] [US2] Unit test `testRadiusInKilometers_whenRegionProvided_shouldCalculateCorrectRadius` in `/iosApp/iosAppTests/Domain/Models/MKCoordinateRegionRadiusTests.swift`

### Implementation for User Story 2

**iOS Implementation**:

- [X] T029 [US2] Implement `handleRegionChange(_ region: MKCoordinateRegion) async` method in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift`
- [X] T030 [US2] Implement task cancellation in `handleRegionChange()` - cancel `fetchTask` before starting new fetch in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift`
- [X] T031 [US2] Extract shared `fetchPins(for region: MKCoordinateRegion) async` private method used by both `loadPins()` and `handleRegionChange()` in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift`
- [X] T032 [US2] Add `.onMapCameraChange(frequency: .onEnd) { context in Task { await viewModel.handleRegionChange(context.region) } }` modifier to Map in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapView.swift`

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently - pins load on appear AND refresh on gesture

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [X] T033 Add SwiftDoc documentation to `MapPin` model in `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/MapPin.swift`
- [X] T034 Add SwiftDoc documentation to `MKCoordinateRegion.radiusInKilometers` extension in `/iosApp/iosApp/Domain/Models/MKCoordinateRegion+Radius.swift`
- [ ] T035 Run `xcodebuild test` with coverage and verify `FullscreenMapViewModel` has ≥ 80% coverage
- [ ] T036 Run quickstart.md verification checklist to validate all scenarios work correctly
- [X] T037 Clean up any TODO comments or debug print statements in modified files

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational phase completion
- **User Story 2 (Phase 4)**: Depends on Foundational phase completion (can run parallel to US1 if separate developer)
- **Polish (Phase 5)**: Depends on User Stories 1 and 2 being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Builds on same ViewModel but is independently testable

### Within Each User Story

- Tests MUST be written and FAIL before implementation (TDD)
- Foundation models (MapPin, extension) before ViewModel changes
- ViewModel implementation before View changes
- Coordinator injection before View can use new ViewModel initializer

### Parallel Opportunities

**Phase 2 (Foundational)** - All [P] tasks can run in parallel:
- T005 (MKCoordinateRegion extension), T006 (MapPin model), T007 (reuse FakeAnnouncementRepository)

**Phase 3 (User Story 1)** - Tests can run in parallel:
- T008-T013 (all unit tests for US1)

**Phase 4 (User Story 2)** - Tests can run in parallel:
- T025-T028 (all unit tests for US2)

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together (after T007 mock is created):
Task T008: "Unit test testLoadPins_whenViewAppears_shouldSetIsLoadingTrue"
Task T009: "Unit test testLoadPins_whenRepositoryReturnsAnnouncements_shouldMapAllToMapPins"
Task T010: "Unit test testLoadPins_whenRepositoryReturnsAnnouncements_shouldMapToMapPins"
Task T011: "Unit test testLoadPins_whenRepositoryReturnsEmptyArray_shouldSetEmptyPins"
Task T012: "Unit test testLoadPins_whenRepositoryThrowsError_shouldKeepExistingPins"
Task T013: "Unit test testLoadPins_whenComplete_shouldSetIsLoadingFalse"

# Then implement sequentially (dependencies within ViewModel):
Task T014-T024: ViewModel modifications → Coordinator update → View modifications
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently - pins appear on map load
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → **MVP: Pins appear on load**
3. Add User Story 2 → Test independently → **Full feature: Pins refresh on gesture**
4. Each story adds value without breaking previous stories

---

## Estimation Update (After TASKS)

| Phase | SP | Days | Confidence | Key Discovery |
|-------|-----|------|------------|---------------|
| Initial | 2 | 10.4 | ±50% | Gut feel from feature title |
| After SPEC | 2 | 10.4 | ±30% | Reduced scope (silent errors, no debounce) |
| After PLAN | 2 | 10.4 | ±20% | API/repository exist, iOS 18 camera APIs simplify gesture detection |
| **After TASKS** | **2** | **10.4** | **±15%** | **37 tasks total, all iOS-only, minimal new code** |

### Per-Platform Breakdown

| Platform | Tasks | Days | Notes |
|----------|-------|------|-------|
| Backend | 0 | 0 | API exists, no changes needed |
| iOS | 37 | ~5-6 | Full implementation + tests |
| Android | 0 | 0 | N/A, iOS only |
| Web | 0 | 0 | N/A, iOS only |
| **Total** | **37** | **~5-6** | Within 10.4 day budget |

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing (TDD workflow)
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Pin tap interaction and callout bubble deferred to future spec (see Figma node `1192:5893`)

