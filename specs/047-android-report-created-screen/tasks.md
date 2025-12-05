# Tasks: Android Report Created Confirmation Screen

**Input**: Design documents from `/specs/047-android-report-created-screen/`
**Prerequisites**: plan.md (required), spec.md (required), research.md, data-model.md, quickstart.md

**Tests**: Test requirements for this project:

**MANDATORY - Android Unit Tests**:
- Android: `/composeApp/src/androidUnitTest/` (JUnit + Kotlin Test + Turbine), 80% coverage
  - Scope: SummaryViewModel (MVI state management, intent processing, effects)
  - Run: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- Convention: MUST follow Given-When-Then structure with backtick test names

**MANDATORY - End-to-End Tests**:
- Mobile: `/e2e-tests/java/` (Appium + Cucumber + Java 21)
- All 3 user stories MUST have E2E test coverage
- Use Screen Object Model pattern with `@AndroidFindBy` annotations
- Convention: MUST structure scenarios with Given-When-Then phases in Gherkin

**Note**: This is an **Android-only** feature. No backend, iOS, or web implementation required.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Android**: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/`
- **Android Tests**: `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/`
- **E2E Tests**: `/e2e-tests/java/`

> **Note**: In task descriptions, `...` is shorthand for `com/intive/aifirst/petspot` to improve readability.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Verify prerequisites and project structure

- [X] T001 Verify branch `047-android-report-created-screen` is checked out and project builds with `./gradlew :composeApp:assembleDebug`
- [X] T002 Verify existing `ReportMissingFlowState.kt` contains `managementPassword: StateFlow<String?>` property in `/composeApp/src/androidMain/.../features/reportmissing/presentation/state/ReportMissingFlowState.kt`
- [X] T003 Verify existing `SummaryScreen.kt` placeholder exists in `/composeApp/src/androidMain/.../features/reportmissing/ui/summary/SummaryScreen.kt`

---

## Phase 2: Foundational (MVI Components - Blocking Prerequisites)

**Purpose**: Create core MVI infrastructure that ALL user stories depend on

**⚠️ CRITICAL**: No user story implementation can begin until MVI components exist

- [X] T004 [P] Create `SummaryUiState.kt` with immutable data class in `/composeApp/src/androidMain/.../features/reportmissing/presentation/mvi/SummaryUiState.kt`
- [X] T005 [P] Create `SummaryUserIntent.kt` with sealed interface in `/composeApp/src/androidMain/.../features/reportmissing/presentation/mvi/SummaryUserIntent.kt`
- [X] T006 [P] Create `SummaryUiEffect.kt` with sealed interface in `/composeApp/src/androidMain/.../features/reportmissing/presentation/mvi/SummaryUiEffect.kt`
- [X] T007 Create `SummaryViewModel.kt` skeleton with StateFlow, SharedFlow, and dispatchIntent in `/composeApp/src/androidMain/.../features/reportmissing/presentation/viewmodels/SummaryViewModel.kt`
- [X] T008 Register `SummaryViewModel` in Koin DI module in `/composeApp/src/androidMain/.../di/ViewModelModule.kt`
- [X] T009 Create `SummaryViewModelTest.kt` test file skeleton in `/composeApp/src/androidUnitTest/.../features/reportmissing/presentation/viewmodels/SummaryViewModelTest.kt`

**Checkpoint**: MVI foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Understand Confirmation Outcome (Priority: P1) 🎯 MVP

**Goal**: Android users see success confirmation messaging when report submission completes

**Independent Test**: Launch screen with mocked flowState and verify header/body copy render exactly as specified

### Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**Android Unit Tests**:
- [X] T010 [P] [US1] Unit test for initial state creation with password from flowState in `/composeApp/src/androidUnitTest/.../features/reportmissing/presentation/viewmodels/SummaryViewModelTest.kt`
- [X] T011 [P] [US1] Unit test for empty password handling (null → empty string) in `/composeApp/src/androidUnitTest/.../features/reportmissing/presentation/viewmodels/SummaryViewModelTest.kt`

**End-to-End Tests**:
- [X] T012 [P] [US1] Create Gherkin feature file with US1 scenario "User sees confirmation messaging" in `/e2e-tests/java/src/test/resources/features/mobile/report-created-confirmation.feature`

### Implementation for User Story 1

**Android** (MVI ViewModel + UI):
- [X] T013 [US1] Implement ViewModel initialization logic to read `flowState.managementPassword` and map to `SummaryUiState` in `/composeApp/src/androidMain/.../features/reportmissing/presentation/viewmodels/SummaryViewModel.kt`
- [X] T014 [US1] Update `SummaryScreen.kt` state host composable to inject ViewModel and collect state in `/composeApp/src/androidMain/.../features/reportmissing/ui/summary/SummaryScreen.kt`
- [X] T015 [US1] Update `SummaryContent.kt` stateless composable with title "Report created" and both body paragraphs in `/composeApp/src/androidMain/.../features/reportmissing/ui/summary/SummaryContent.kt`
- [X] T016 [US1] Add design constants (colors, spacing, typography) matching Figma spec to `SummaryContent.kt`
- [X] T017 [US1] Add testTag modifiers `summary.title`, `summary.bodyParagraph1`, `summary.bodyParagraph2` to UI elements
- [X] T017b [US1] Verify SummaryScreen has NO TopAppBar with navigation icon (per FR-011 - this is a terminal screen with only Close button at bottom)

**Checkpoint**: User Story 1 complete - confirmation messaging displays correctly

---

## Phase 4: User Story 2 - Retrieve and Safeguard Management Password (Priority: P1)

**Goal**: Android users can clearly read and copy the unique management password from the gradient container

**Independent Test**: Provide mock password via flowState, verify gradient container renders digits, tap copies to clipboard with Snackbar confirmation

> **Note**: Per FR-006, only tap gesture is required for copy action. Long-press is not in scope.

### Tests for User Story 2 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**Android Unit Tests**:
- [X] T018 [P] [US2] Unit test for `CopyPasswordClicked` intent emitting `ShowSnackbar` effect in `/composeApp/src/androidUnitTest/.../features/reportmissing/presentation/viewmodels/SummaryViewModelTest.kt`
- [X] T019 [P] [US2] Unit test for password display with non-null value in `/composeApp/src/androidUnitTest/.../features/reportmissing/presentation/viewmodels/SummaryViewModelTest.kt`

**End-to-End Tests**:
- [X] T020 [P] [US2] Add Gherkin scenario "User copies password to clipboard" to `/e2e-tests/java/src/test/resources/features/mobile/report-created-confirmation.feature`

### Implementation for User Story 2

**Android** (Password Container + Clipboard):
- [X] T021 [US2] Implement gradient password container with `Brush.horizontalGradient` (#5C33FF → #F84BA1) and glow effect in `/composeApp/src/androidMain/.../features/reportmissing/ui/summary/SummaryContent.kt`
- [X] T022 [US2] Add password text display (60sp, white, -1.5sp letter spacing) inside gradient container in `/composeApp/src/androidMain/.../features/reportmissing/ui/summary/SummaryContent.kt`
- [X] T023 [US2] Implement `CopyPasswordClicked` intent handler in ViewModel with `ClipboardManager` copy logic in `/composeApp/src/androidMain/.../features/reportmissing/presentation/viewmodels/SummaryViewModel.kt`
- [X] T024 [US2] Add `SnackbarHost` to `SummaryScreen.kt` and wire `ShowSnackbar` effect collection with message "Code copied to clipboard"
- [X] T025 [US2] Make password container clickable and dispatch `CopyPasswordClicked` intent on tap
- [X] T026 [US2] Add testTag modifiers `summary.passwordContainer`, `summary.passwordText`, `summary.snackbar` to UI elements

**Checkpoint**: User Story 2 complete - password displays and copies correctly

---

## Phase 5: User Story 3 - Exit the Flow Safely (Priority: P2)

**Goal**: Android users can exit the flow via Close button or system back, clearing state and returning to pet list

**Independent Test**: Trigger Close button via UI automation, verify navigation resets to pet list; repeat with system back gesture

### Tests for User Story 3 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**Android Unit Tests**:
- [X] T027 [P] [US3] Unit test for `CloseClicked` intent emitting `DismissFlow` effect in `/composeApp/src/androidUnitTest/.../features/reportmissing/presentation/viewmodels/SummaryViewModelTest.kt`

**End-to-End Tests**:
- [X] T028 [P] [US3] Add Gherkin scenario "User exits flow via Close button" to `/e2e-tests/java/src/test/resources/features/mobile/report-created-confirmation.feature`
- [X] T029 [P] [US3] Add Gherkin scenario "User exits flow via system back" to `/e2e-tests/java/src/test/resources/features/mobile/report-created-confirmation.feature`

### Implementation for User Story 3

**Android** (Close Button + Navigation):
- [X] T030 [US3] Implement Close button (full-width, 52dp height, #155DFC background, 10dp radius) in `/composeApp/src/androidMain/.../features/reportmissing/ui/summary/SummaryContent.kt`
- [X] T031 [US3] Dispatch `CloseClicked` intent on Close button click in `/composeApp/src/androidMain/.../features/reportmissing/ui/summary/SummaryContent.kt`
- [X] T032 [US3] Implement `CloseClicked` intent handler in ViewModel emitting `DismissFlow` effect in `/composeApp/src/androidMain/.../features/reportmissing/presentation/viewmodels/SummaryViewModel.kt`
- [X] T033 [US3] Add `BackHandler` to `SummaryScreen.kt` dispatching `CloseClicked` on system back
- [X] T034 [US3] Wire `DismissFlow` effect handler in `SummaryScreen.kt` to clear flowState and navigate back
- [X] T035 [US3] Add testTag modifier `summary.closeButton` to Close button

**Checkpoint**: User Story 3 complete - flow exits correctly via Close or system back

---

## Phase 6: E2E Test Infrastructure

**Purpose**: Complete E2E test implementation with Screen Objects and Step Definitions

- [X] T036 [P] Create `SummaryScreen.java` with element selectors (@AndroidFindBy) for all testTag identifiers in `/e2e-tests/java/src/test/java/.../screens/SummaryScreen.java`
- [X] T037 Create `ReportCreatedSteps.java` with step definitions for all Gherkin scenarios in `/e2e-tests/java/src/test/java/.../steps/mobile/ReportCreatedSteps.java`
- [ ] T038 Verify E2E tests pass with `mvn test -Dtest=AndroidTestRunner -Dcucumber.filter.tags="@android and @report-created"` from `/e2e-tests/java/`

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Quality assurance, documentation, and code cleanup

- [X] T039 Create `SummaryUiStateProvider` PreviewParameterProvider with sample states (with password, empty password) in `/composeApp/src/androidMain/.../features/reportmissing/ui/summary/SummaryContent.kt`
- [X] T040 Add `@Preview` function for `SummaryContent` using `@PreviewParameter` (light mode, callbacks defaulted to no-ops)
- [X] T041 Run `./gradlew :composeApp:testDebugUnitTest` and verify unit tests pass for SummaryViewModel
- [ ] T042 Verify landscape orientation support - UI adapts without clipping, ViewModel state survives rotation
- [ ] T043 Run manual smoke test per quickstart.md checklist (9 verification items)
- [X] T044 [P] Add KDoc documentation to complex ViewModel methods (skip self-explanatory dispatchIntent, etc.)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational (MVI components must exist)
- **User Story 2 (Phase 4)**: Depends on Phase 3 (builds on SummaryContent and ViewModel)
- **User Story 3 (Phase 5)**: Depends on Phase 3 (builds on SummaryScreen and ViewModel)
- **E2E Infrastructure (Phase 6)**: Depends on all user stories complete
- **Polish (Phase 7)**: Depends on all user stories complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - Foundation for screen content
- **User Story 2 (P1)**: Depends on US1 - Adds password container to existing screen
- **User Story 3 (P2)**: Depends on US1 - Adds close/navigation to existing screen
- **Note**: US2 and US3 are independent of each other but both build on US1

### Within Each User Story

- Tests (if included) MUST be written and FAIL before implementation
- MVI components before UI updates
- UI updates before navigation wiring
- Core implementation before integration

### Parallel Opportunities

- T004, T005, T006 (MVI data classes) can run in parallel
- T010, T011 (US1 unit tests) can run in parallel
- T018, T019 (US2 unit tests) can run in parallel
- T028, T029 (US3 E2E scenarios) can run in parallel
- US2 and US3 can run in parallel after US1 completes (different aspects of same screen)

---

## Parallel Example: Foundational Phase

```bash
# Launch all MVI data class creations together:
Task: "Create SummaryUiState.kt in /composeApp/src/androidMain/.../presentation/mvi/"
Task: "Create SummaryUserIntent.kt in /composeApp/src/androidMain/.../presentation/mvi/"
Task: "Create SummaryUiEffect.kt in /composeApp/src/androidMain/.../presentation/mvi/"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (MVI components)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test confirmation messaging renders correctly
5. Continue with US2 and US3

### Incremental Delivery

1. Complete Setup + Foundational → MVI foundation ready
2. Add User Story 1 → Test independently → Screen displays messaging (MVP!)
3. Add User Story 2 → Test independently → Password copy works
4. Add User Story 3 → Test independently → Flow exit works
5. Each story adds functionality without breaking previous work

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- This is Android-only: skip iOS, Web, and Backend sections
- Verify tests fail before implementing (TDD approach)
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
- All user stories share the same screen - US1 creates foundation, US2/US3 add features

---

## Summary

| Metric | Value |
|--------|-------|
| **Total Tasks** | 45 |
| **Phase 1 (Setup)** | 3 tasks |
| **Phase 2 (Foundational)** | 6 tasks |
| **Phase 3 (US1 - P1)** | 9 tasks |
| **Phase 4 (US2 - P1)** | 9 tasks |
| **Phase 5 (US3 - P2)** | 9 tasks |
| **Phase 6 (E2E)** | 3 tasks |
| **Phase 7 (Polish)** | 6 tasks |
| **Parallel Opportunities** | 14 tasks marked [P] |
| **MVP Scope** | Setup + Foundational + User Story 1 (18 tasks) |

