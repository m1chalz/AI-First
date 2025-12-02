# Tasks: Android Microchip Number Screen

**Input**: Design documents from `/specs/038-android-chip-number-screen/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Tests**: Test requirements for this project:

**MANDATORY - Android Unit Tests**:
- Location: `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/`
- Framework: JUnit 6 + Kotlin Test + Turbine (Flow testing)
- Coverage target: 80% line + branch coverage
- Scope: MicrochipNumberFormatter, ChipNumberViewModel
- Run: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- Convention: MUST follow Given-When-Then structure with backtick test names

**MANDATORY - End-to-End Tests**:
- Mobile: `/e2e-tests/src/test/resources/features/mobile/chip-number.feature` (Cucumber/Appium)
- Screen Object: `/e2e-tests/src/test/java/.../screens/ChipNumberScreen.java`
- All user stories MUST have E2E test coverage

**Organization**: Tasks are grouped by user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3, US4)
- Include exact file paths in descriptions

---

## Phase 1: Setup

**Purpose**: Verify existing infrastructure from spec 018 and prepare for enhancements

- [x] T001 Verify spec 018 infrastructure exists: `ChipNumberContent.kt`, `ChipNumberScreen.kt`, `ReportMissingUiState.kt` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/`
- [x] T002 [P] Create util directory structure at `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/util/`
- [x] T003 [P] Create test directory structure at `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/util/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core utilities and MVI components that ALL user stories depend on

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

### Unit Tests (Write FIRST - must FAIL)

- [x] T004 [P] Create `MicrochipNumberFormatterTest.kt` with format() tests in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/util/MicrochipNumberFormatterTest.kt`
- [x] T005 [P] Add extractDigits() tests to `MicrochipNumberFormatterTest.kt`

### Implementation

- [x] T006 Implement `MicrochipNumberFormatter` object with `format()` and `extractDigits()` methods in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/util/MicrochipNumberFormatter.kt`
- [x] T007 Implement `MicrochipVisualTransformation` class in same file `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/util/MicrochipNumberFormatter.kt`
- [x] T008 Implement `MicrochipOffsetMapping` private class for cursor position mapping in same file
- [x] T009 Run formatter tests and verify they pass: `./gradlew :composeApp:testDebugUnitTest --tests "*MicrochipNumberFormatterTest*"`

**Checkpoint**: Formatter utility complete and tested - user story implementation can begin

---

## Phase 3: User Story 1 - Report Missing Pet with Known Microchip (Priority: P1) 🎯 MVP

**Goal**: User can enter a 15-digit microchip number with auto-formatting and proceed to step 2/4

**Independent Test**: Navigate to chip number screen, enter "123456789012345", verify formatted as "12345-67890-12345", tap Continue and verify navigation to photo screen

### Tests for User Story 1 (MANDATORY) ✅

- [x] T010 [P] [US1] Create `ChipNumberViewModelTest.kt` with initial state test in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/ChipNumberViewModelTest.kt`
- [x] T011 [P] [US1] Add UpdateChipNumber intent tests (digits extraction, 15-digit limit) to `ChipNumberViewModelTest.kt`
- [x] T012 [P] [US1] Add ContinueClicked tests (save to flow state, emit NavigateToPhoto effect) using Turbine to `ChipNumberViewModelTest.kt`

### Implementation for User Story 1

- [x] T013 [P] [US1] Create `ChipNumberUiState.kt` data class in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/ChipNumberUiState.kt`
- [x] T014 [P] [US1] Create `ChipNumberUserIntent.kt` sealed interface in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/ChipNumberUserIntent.kt`
- [x] T015 [P] [US1] Create `ChipNumberUiEffect.kt` sealed interface in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/ChipNumberUiEffect.kt`
- [x] T016 [US1] Create `ChipNumberViewModel.kt` with handleIntent(), state/effects flows in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/ChipNumberViewModel.kt`
- [x] T016b [US1] Register `ChipNumberViewModel` in Koin module at `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/ViewModelModule.kt`
- [x] T017 [US1] Enhance existing `ChipNumberContent.kt` - replace placeholder Text with OutlinedTextField using MicrochipVisualTransformation in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/chipnumber/ChipNumberContent.kt`
- [x] T018 [US1] Add KeyboardOptions(keyboardType = KeyboardType.Number) to OutlinedTextField
- [x] T019 [US1] Add label "Microchip number (optional)" and placeholder "00000-00000-00000" to OutlinedTextField
- [x] T020 [US1] Update `ChipNumberScreen.kt` to use new ChipNumberViewModel, collect state, dispatch intents in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/chipnumber/ChipNumberScreen.kt`
- [x] T021 [US1] Wire effect handling in ChipNumberScreen for NavigateToPhoto effect
- [x] T022 [US1] Create `ChipNumberUiStateProvider` PreviewParameterProvider with empty, partial, complete states in `ChipNumberContent.kt`
- [x] T023 [US1] Update @Preview function to use @PreviewParameter(ChipNumberUiStateProvider::class)
- [x] T024 [US1] Run ViewModel tests and verify pass: `./gradlew :composeApp:testDebugUnitTest --tests "*ChipNumberViewModelTest*"`

**Checkpoint**: User Story 1 complete - users can enter chip number with formatting and continue

---

## Phase 4: User Story 2 - Report Missing Pet Without Microchip (Priority: P2)

**Goal**: User can proceed to step 2/4 without entering any microchip data

**Independent Test**: Navigate to chip number screen, leave field empty, tap Continue and verify navigation to photo screen without errors

### Tests for User Story 2 (MANDATORY) ✅

- [ ] T025 [P] [US2] Add test for ContinueClicked with empty chipNumber saves empty string to flow state in `ChipNumberViewModelTest.kt`

### Implementation for User Story 2

- [ ] T026 [US2] Verify Continue button has no disabled state logic in `ChipNumberContent.kt` (button always enabled per FR-013)
- [ ] T027 [US2] Verify handleIntent(ContinueClicked) works correctly with empty chipNumber in `ChipNumberViewModel.kt`

**Checkpoint**: User Story 2 complete - users can skip chip number entry

---

## Phase 5: User Story 3 - Navigate Back from Flow (Priority: P3)

**Goal**: User can exit the entire Report Missing Pet flow from step 1/4

**Independent Test**: Navigate to chip number screen, optionally enter data, tap back button, verify return to pet list screen

### Tests for User Story 3 (MANDATORY) ✅

- [ ] T028 [P] [US3] Add test for BackClicked emits NavigateBack effect using Turbine in `ChipNumberViewModelTest.kt`
- [ ] T029 [P] [US3] Add test for BackClicked does NOT save chipNumber to flow state in `ChipNumberViewModelTest.kt`

### Implementation for User Story 3

- [ ] T030 [US3] Verify StepHeader back button triggers onBackClick callback in `ChipNumberContent.kt`
- [ ] T031 [US3] Wire effect handling in ChipNumberScreen for NavigateBack effect (pop entire flow)
- [ ] T032 [US3] Add BackHandler composable for system back button/gesture handling in `ChipNumberScreen.kt`

**Checkpoint**: User Story 3 complete - users can exit flow via back button

---

## Phase 6: User Story 4 - Resume Flow with Previously Entered Data (Priority: P2)

**Goal**: Data persists when user navigates back from step 2/4 to step 1/4

**Independent Test**: Enter chip number, tap Continue to step 2/4, navigate back to step 1/4, verify chip number still populated

### Tests for User Story 4 (MANDATORY) ✅

- [ ] T033 [P] [US4] Add test for initial state loads chipNumber from flow state in `ChipNumberViewModelTest.kt`

### Implementation for User Story 4

- [ ] T034 [US4] Verify ChipNumberViewModel constructor reads existing chipNumber from flowState
- [ ] T035 [US4] Verify ChipNumberUiState is initialized with flowState.chipNumber value

**Checkpoint**: User Story 4 complete - data persists during flow navigation

---

## Phase 7: E2E Tests & Polish

**Purpose**: End-to-end test coverage and final cleanup

### E2E Tests

- [ ] T036 [P] Create `ChipNumberScreen.java` Screen Object Model with element locators in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/screens/ChipNumberScreen.java`
- [ ] T037 [P] Create `chip-number.feature` Cucumber scenarios for all user stories in `/e2e-tests/src/test/resources/features/mobile/chip-number.feature`
- [ ] T038 Create `ChipNumberSteps.java` step definitions in `/e2e-tests/src/test/java/com/intive/aifirst/petspot/steps/ChipNumberSteps.java`

### Polish

- [ ] T039 Run full test suite with coverage: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- [ ] T040 Verify 80% coverage for MicrochipNumberFormatter and ChipNumberViewModel
- [ ] T041 Run lint check: `./gradlew :composeApp:lintDebug`
- [ ] T042 Manual testing verification per quickstart.md checklist

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup - BLOCKS all user stories
- **User Stories (Phases 3-6)**: All depend on Foundational completion
  - US1 (P1): Core functionality - recommended first
  - US2 (P2): Depends on US1 Continue button existing
  - US3 (P3): Depends on US1 back button wiring
  - US4 (P2): Depends on US1 ViewModel structure
- **E2E & Polish (Phase 7)**: Depends on all user stories complete

### User Story Dependencies

- **US1 (P1)**: Can start after Foundational - No dependencies on other stories
- **US2 (P2)**: Technically independent but reuses US1 implementation
- **US3 (P3)**: Technically independent but reuses US1 implementation
- **US4 (P2)**: Technically independent but reuses US1 implementation

### Parallel Opportunities

**Within Phase 2 (Foundational)**:
- T004 and T005 can run in parallel (different test methods)
- T006, T007, T008 must be sequential (same file)

**Within Phase 3 (US1)**:
- T013, T014, T015 can run in parallel (different files)
- T010, T011, T012 can run in parallel (different test methods)

**Across User Stories**:
- After US1 implementation, US2/US3/US4 tests can be written in parallel
- US2/US3/US4 implementation changes are minimal and can be done sequentially

---

## Parallel Example: Phase 3 (User Story 1)

```bash
# Launch all MVI artifacts together (different files):
Task: T013 "Create ChipNumberUiState.kt"
Task: T014 "Create ChipNumberUserIntent.kt"
Task: T015 "Create ChipNumberUiEffect.kt"

# Then sequentially:
Task: T016 "Create ChipNumberViewModel.kt" (depends on T013-T015)
Task: T017 "Enhance ChipNumberContent.kt" (depends on T016)
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (verify infrastructure)
2. Complete Phase 2: Foundational (formatter utility)
3. Complete Phase 3: User Story 1 (chip number entry with formatting)
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Demo: Users can enter formatted chip number and continue

### Incremental Delivery

1. Setup + Foundational → Formatter working
2. Add User Story 1 → Core chip entry functional (MVP!)
3. Add User Story 2 → Empty entry allowed
4. Add User Story 3 → Back navigation works
5. Add User Story 4 → Data persistence verified
6. E2E + Polish → Full test coverage

### Estimated Time

| Phase | Tasks | Time (est.) |
|-------|-------|-------------|
| Setup | 3 | 5 min |
| Foundational | 6 | 25 min |
| US1 (MVP) | 16 | 60 min |
| US2 | 3 | 10 min |
| US3 | 5 | 15 min |
| US4 | 3 | 10 min |
| E2E & Polish | 7 | 30 min |
| **Total** | **43** | **~2.5 hours** |

---

## Notes

- [P] tasks = different files, no dependencies on incomplete tasks
- [Story] label maps task to specific user story for traceability
- Existing infrastructure from spec 018 means we're enhancing, not creating from scratch
- ChipNumberContent.kt placeholder already exists - enhance with real TextField
- ChipNumberScreen.kt exists - update to use new ViewModel
- Test tags follow project convention: `reportMissing.chipNumber.*`, `missingPet.microchip.*`
- Commit after each phase or logical group

