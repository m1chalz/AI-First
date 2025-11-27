# Tasks: Android Location Permissions Handling

**Input**: Design documents from `/specs/026-android-location-permissions/`
**Prerequisites**: plan.md ✓, spec.md ✓, research.md ✓, data-model.md ✓, contracts/api-contract.md ✓

**Tests**: Test requirements for this project:

**MANDATORY - Android Unit Tests**:
- Location: `/composeApp/src/androidUnitTest/` (JUnit 6 + Turbine + MockK)
- Scope: Domain models, use cases, ViewModels (MVI architecture), Reducers
- Coverage: 80% line + branch coverage
- Run: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- Convention: MUST follow Given-When-Then structure with descriptive names (Kotlin backticks)

**MANDATORY - End-to-End Tests**:
- Mobile: `/e2e-tests/java/src/test/resources/features/mobile/android-location-permissions.feature` (Java + Cucumber)
- Screen Objects: `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/screens/`
- Step Definitions: `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/mobile/`
- All 5 user stories MUST have E2E test coverage
- Use Screen Object Model pattern with `@AndroidFindBy`
- Convention: MUST structure scenarios with Given-When-Then (Cucumber Gherkin)

**Note**: This is an Android-only feature. iOS, Web, and Backend tasks are NOT applicable.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Includes exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization, dependencies, and Android manifest configuration

- [X] T001 Add Accompanist Permissions dependency to `/composeApp/build.gradle.kts` (com.google.accompanist:accompanist-permissions)
- [X] T002 [P] Add location permissions to `/composeApp/src/androidMain/AndroidManifest.xml` (ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
- [X] T003 [P] Create LocationModule Koin module file at `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/LocationModule.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core domain models and repository interfaces that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

### Domain Models

- [X] T004 [P] Create `LocationCoordinates` data class in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/domain/models/LocationCoordinates.kt`
- [X] T005 [P] Create `PermissionStatus` sealed class in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/domain/models/PermissionStatus.kt` (NotRequested, Requesting, Granted, Denied states)
- [X] T006 [P] Create `RationaleDialogType` sealed class in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/domain/models/RationaleDialogType.kt` (Educational, Informational types)

### Repository Interfaces

- [X] T007 Create `LocationRepository` interface in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/domain/repositories/LocationRepository.kt`

### E2E Test Infrastructure

- [X] T008 Create AnimalListScreen Screen Object in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/screens/AnimalListScreen.java` (with @AndroidFindBy for permission dialog elements)
- [X] T009 [P] Create LocationPermissionSteps step definitions file in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/mobile/LocationPermissionSteps.java`
- [X] T010 [P] Create E2E feature file skeleton in `/e2e-tests/java/src/test/resources/features/mobile/android-location-permissions.feature`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Location-Aware Content for Location-Authorized Users (Priority: P1) 🎯 MVP

**Goal**: Users who have already granted location permissions should seamlessly receive location-aware animal listings when opening the app.

**Independent Test**: Launch app with location permissions already granted (ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION) and verify that current location is fetched and used for the animal query.

### Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**Unit Tests**:
- [X] T011 [P] [US1] Create `FakeLocationRepository` in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/fakes/FakeLocationRepository.kt`
- [X] T012 [P] [US1] Unit test for `GetCurrentLocationUseCase` in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/domain/usecases/GetCurrentLocationUseCaseTest.kt` (test both stages: cached hit, cached miss with fresh success, both fail)
- [X] T013 [P] [US1] Unit test for `LocationRepositoryImpl` in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/data/repositories/LocationRepositoryImplTest.kt` (test cached location, fresh location, timeout)

**E2E Tests**:
- [X] T014 [US1] Add Cucumber scenarios for US1 to `/e2e-tests/java/src/test/resources/features/mobile/android-location-permissions.feature` (permission already granted, location fetch success, location fetch timeout/failure)

### Implementation for User Story 1

**Domain Layer**:
- [X] T015 [US1] Create `GetCurrentLocationUseCase` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/domain/usecases/GetCurrentLocationUseCase.kt` (two-stage: cached first, then fresh with 10s timeout)

**Data Layer**:
- [X] T016 [US1] Implement `LocationRepositoryImpl` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/data/repositories/LocationRepositoryImpl.kt` (two-stage: getLastKnownLocation + requestSingleUpdate with 10s timeout)

**DI**:
- [X] T017 [US1] Register `LocationRepository` and `GetCurrentLocationUseCase` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/LocationModule.kt`

**Presentation Layer**:
- [X] T018 [US1] Extend `AnimalListUiState` with `permissionStatus: PermissionStatus` and `location: LocationCoordinates?` fields in existing MVI state file
- [X] T019 [US1] Add location-related intents to `AnimalListIntent` sealed class (CheckPermission, PermissionGranted, LocationFetched, LocationFetchFailed)
- [X] T020 [US1] Extend `AnimalListReducer` with permission state transitions for US1 (granted → fetching → fetched/failed)
- [X] T021 [US1] Extend `AnimalListViewModel` to inject `GetCurrentLocationUseCase` and handle two-stage location fetch flow

**UI Layer**:
- [X] T022 [US1] Extend `AnimalListScreen` state host composable with Accompanist `rememberMultiplePermissionsState` for location permissions
- [X] T023 [US1] Add loading indicator to `AnimalListContent` stateless composable while fetching location
- [X] T024 [US1] Add testTag modifiers to location-related UI elements (`animalList.loadingIndicator`, `animalList.locationStatus`)

**Checkpoint**: User Story 1 complete - users with granted permissions can see location-aware listings

---

## Phase 4: User Story 2 - First-Time Location Permission Request (Priority: P2)

**Goal**: First-time users who haven't been asked about location permissions should see a clear system permission request.

**Independent Test**: Install fresh app (permission not yet requested) and verify system dialog appears, then test both acceptance and denial paths.

### Tests for User Story 2 (MANDATORY) ✅

**Unit Tests**:
- [X] T025 [P] [US2] Unit test for `CheckLocationPermissionUseCase` in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/domain/usecases/CheckLocationPermissionUseCaseTest.kt`
- [X] T026 [P] [US2] Extend `AnimalListReducerTest` with permission request state transitions in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/animallist/presentation/mvi/AnimalListReducerTest.kt`

**E2E Tests**:
- [X] T027 [US2] Add Cucumber scenarios for US2 to feature file (first-time permission request, user allows, user denies)

### Implementation for User Story 2

**Domain Layer**:
- [X] T028 [US2] Create `CheckLocationPermissionUseCase` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/domain/usecases/CheckLocationPermissionUseCase.kt` (checks ContextCompat.checkSelfPermission)

**DI**:
- [X] T029 [US2] Register `CheckLocationPermissionUseCase` in LocationModule

**Presentation Layer**:
- [X] T030 [US2] Add permission request intents to `AnimalListIntent` (RequestPermission, PermissionDenied)
- [X] T031 [US2] Extend `AnimalListReducer` with NotRequested → Requesting → Granted/Denied transitions
- [X] T032 [US2] Add `RequestPermission` effect to `AnimalListEffect` sealed class for triggering system dialog
- [X] T033 [US2] Extend `AnimalListViewModel` to inject `CheckLocationPermissionUseCase` and emit RequestPermission effect

**UI Layer**:
- [X] T034 [US2] Add `LaunchedEffect` in `AnimalListScreen` to observe permission state changes from Accompanist
- [X] T035 [US2] Wire permission result callback to dispatch `PermissionGranted` or `PermissionDenied` intent to ViewModel

**Checkpoint**: User Story 2 complete - first-time users see system permission dialog

---

## Phase 5: User Story 3 - Recovery Path for Denied Permissions (Priority: P3)

**Goal**: Users who previously denied location access should have a clear path to enable it through device Settings.

**Independent Test**: Launch app with denied permission status and verify custom rationale dialog appears with working Settings navigation and Cancel fallback.

### Tests for User Story 3 (MANDATORY) ✅

**Unit Tests**:
- [ ] T036 [P] [US3] Extend `AnimalListViewModelTest` with rationale dialog effect tests in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/animallist/presentation/viewmodels/AnimalListViewModelTest.kt`
- [ ] T037 [P] [US3] Extend `AnimalListReducerTest` with denied state transitions and rationale shown flag

**E2E Tests**:
- [ ] T038 [US3] Add Cucumber scenarios for US3 to feature file (denied permission, rationale dialog, Go to Settings, Cancel)

### Implementation for User Story 3

**Presentation Layer**:
- [ ] T039 [US3] Add `ShowRationaleDialog(type: RationaleDialogType.Informational)` effect to `AnimalListEffect`
- [ ] T040 [US3] Add `OpenSettings` effect to `AnimalListEffect` for navigating to app settings
- [ ] T041 [US3] Add `rationaleShownThisSession: Boolean` tracking to `AnimalListUiState` (per FR-015)
- [ ] T042 [US3] Extend reducer and ViewModel to handle denied state with rationale display logic

**UI Layer**:
- [ ] T043 [US3] Create `InformationalRationaleDialog` composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/components/InformationalRationaleDialog.kt`
- [ ] T044 [US3] Add dialog with "Go to Settings" and "Cancel" buttons with benefit-focused messaging
- [ ] T045 [US3] Implement Settings navigation via `Intent.ACTION_APPLICATION_DETAILS_SETTINGS` in `AnimalListScreen`
- [ ] T046 [US3] Add testTag modifiers to rationale dialog elements (`animalList.rationaleDialog.goToSettingsButton`, `animalList.rationaleDialog.cancelButton`)

**Checkpoint**: User Story 3 complete - users can recover denied permissions via Settings

---

## Phase 6: User Story 4 - Permission Rationale Before System Dialog (Priority: P4)

**Goal**: When `shouldShowRequestPermissionRationale` returns true, show educational rationale before system permission dialog.

**Independent Test**: Deny permission once (without "Don't ask again"), reopen app, and verify rationale appears before system dialog.

### Tests for User Story 4 (MANDATORY) ✅

**Unit Tests**:
- [ ] T047 [P] [US4] Extend `CheckLocationPermissionUseCaseTest` with shouldShowRationale detection tests
- [ ] T048 [P] [US4] Extend `AnimalListReducerTest` with educational rationale state transitions

**E2E Tests**:
- [ ] T049 [US4] Add Cucumber scenarios for US4 to feature file (shouldShowRationale, Continue button, Not Now button)

### Implementation for User Story 4

**Presentation Layer**:
- [ ] T050 [US4] Add `ShowRationaleDialog(type: RationaleDialogType.Educational)` effect handling
- [ ] T051 [US4] Add `RationaleContinue` and `RationaleNotNow` intents to `AnimalListIntent`
- [ ] T052 [US4] Extend reducer to transition from Educational rationale to Requesting (system dialog)

**UI Layer**:
- [ ] T053 [US4] Create `EducationalRationaleDialog` composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/components/EducationalRationaleDialog.kt`
- [ ] T054 [US4] Add dialog with "Continue" and "Not Now" buttons with benefit-focused messaging
- [ ] T055 [US4] Wire Continue button to launch system permission request via Accompanist
- [ ] T056 [US4] Add testTag modifiers to educational dialog elements (`animalList.educationalDialog.continueButton`, `animalList.educationalDialog.notNowButton`)

**Checkpoint**: User Story 4 complete - educational rationale shown before system dialog when appropriate

---

## Phase 7: User Story 5 - Dynamic Permission Change Handling (Priority: P5)

**Goal**: Users who change location permissions while app is open should see the app respond without requiring restart.

**Independent Test**: Change location permission (via Settings) while app is on startup screen and verify app reacts appropriately without restart.

### Tests for User Story 5 (MANDATORY) ✅

**Unit Tests**:
- [ ] T057 [P] [US5] Extend `AnimalListViewModelTest` with permission change observation tests
- [ ] T058 [P] [US5] Extend `AnimalListReducerTest` with dynamic permission change transitions (denied→granted, granted→denied)

**E2E Tests**:
- [ ] T059 [US5] Add Cucumber scenarios for US5 to feature file (permission change while app open, auto-refresh on grant)

### Implementation for User Story 5

**Presentation Layer**:
- [ ] T060 [US5] Add `PermissionStateChanged(granted: Boolean, shouldShowRationale: Boolean)` intent to `AnimalListIntent`
- [ ] T061 [US5] Extend reducer to handle dynamic state transitions (Denied→Granted triggers location fetch, Granted→Denied continues without location)

**UI Layer**:
- [ ] T062 [US5] Add `LaunchedEffect` observing `permissionState.allPermissionsGranted` to detect Settings changes
- [ ] T063 [US5] Dispatch `PermissionStateChanged` intent when Accompanist detects permission change on lifecycle resume

**Checkpoint**: User Story 5 complete - app responds dynamically to permission changes

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Integration testing, documentation, and code quality improvements

- [ ] T064 [P] Run full unit test suite with coverage: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- [ ] T065 [P] Verify 80% test coverage in Kover report at `/composeApp/build/reports/kover/html/index.html`
- [ ] T066 [P] Add KDoc documentation to public APIs (use cases, repository interface) where purpose is not self-explanatory
- [ ] T067 [P] Verify all MVI artifacts follow co-location pattern in `/features/animallist/presentation/mvi/`
- [ ] T068 [P] Create `AnimalListUiStatePreviewProvider` implementing `PreviewParameterProvider<AnimalListUiState>` with sample states
- [ ] T069 [P] Add `@Preview` function for `AnimalListContent` using `@PreviewParameter` (light mode only)
- [ ] T070 [P] Run Android lint and fix any warnings: `./gradlew :composeApp:lint`
- [ ] T071 [P] Run E2E tests for all 5 user stories and verify pass rate
- [ ] T072 Perform final integration testing following quickstart.md validation scenarios

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-7)**: All depend on Foundational phase completion
  - US1 (P1): Can start first after Foundational
  - US2 (P2): Depends on US1 (extends permission state handling)
  - US3 (P3): Depends on US2 (extends denied state handling)
  - US4 (P4): Depends on US3 (extends rationale dialog system)
  - US5 (P5): Depends on US1 (extends permission observation)
- **Polish (Phase 8)**: Depends on all user stories being complete

### Within Each User Story

- Tests MUST be written and FAIL before implementation (TDD approach)
- Domain models/use cases before ViewModels
- ViewModels before UI
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

**Setup Phase**:
- T002 and T003 can run in parallel (manifest and DI file creation)

**Foundational Phase**:
- T004, T005, T006 can run in parallel (all domain models)
- T008, T009, T010 can run in parallel (E2E infrastructure)

**Within User Stories**:
- Unit tests marked [P] can run in parallel
- After tests pass, implementation tasks follow sequentially

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together (TDD Red phase):
Task: T011 - FakeLocationRepository
Task: T012 - GetLastKnownLocationUseCaseTest
Task: T013 - LocationRepositoryImplTest

# After tests fail (Red), implement sequentially:
Task: T015 → T016 → T017 → T018 → T019 → T020 → T021 → T022 → T023 → T024
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (dependencies, manifest)
2. Complete Phase 2: Foundational (domain models, repository interfaces)
3. Complete Phase 3: User Story 1 (location fetch for authorized users)
4. **STOP and VALIDATE**: Test US1 independently - users with granted permissions see location-aware listings
5. Demo if ready - core value proposition delivered!

### Incremental Delivery

1. **MVP (US1)**: Users with granted permissions get location-aware listings
2. **+US2**: First-time users see system permission dialog
3. **+US3**: Denied users can recover via Settings
4. **+US4**: Educational rationale improves grant rates
5. **+US5**: Dynamic permission handling - polish feature

Each story adds value without breaking previous stories.

---

## Summary

| Metric | Value |
|--------|-------|
| **Total Tasks** | 72 |
| **Setup Phase** | 3 tasks |
| **Foundational Phase** | 7 tasks |
| **User Story 1 (P1)** | 14 tasks |
| **User Story 2 (P2)** | 11 tasks |
| **User Story 3 (P3)** | 11 tasks |
| **User Story 4 (P4)** | 10 tasks |
| **User Story 5 (P5)** | 7 tasks |
| **Polish Phase** | 9 tasks |
| **Parallel Opportunities** | 25 tasks marked [P] |
| **Suggested MVP Scope** | US1 only (Phase 1-3) |

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently testable after completion
- Verify tests fail before implementing (TDD Red-Green-Refactor)
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- This is Android-only - iOS/Web/Backend tasks are not applicable

