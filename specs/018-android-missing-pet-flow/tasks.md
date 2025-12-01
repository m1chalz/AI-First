# Tasks: Android Missing Pet Report Flow

**Input**: Design documents from `/specs/018-android-missing-pet-flow/`
**Prerequisites**: plan.md ✓, spec.md ✓, research.md ✓, data-model.md ✓, quickstart.md ✓

**Tests**: Test requirements for this project:

**MANDATORY - Platform-Specific Unit Tests** (per platform):
- Android: `/composeApp/src/androidUnitTest/` (JUnit + Turbine), 80% coverage
  - Scope: MVI components (Reducer, UiState), ViewModel
  - Run: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- Convention: MUST follow Given-When-Then structure with descriptive names

**MANDATORY - End-to-End Tests**:
- Mobile: `/e2e-tests/java/src/test/resources/features/mobile/` (Cucumber + Appium)
- All user stories MUST have E2E test coverage
- Use Screen Object Model pattern (existing in `/e2e-tests/java/.../screens/`)
- Convention: MUST structure scenarios with Given-When-Then phases

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project structure and navigation route definitions

**Pre-existing (already implemented):**
- ✅ "Report Missing Animal" floating button exists in `AnimalListContent.kt` with testTag `animalList.reportButton`
- ✅ `AnimalListIntent.ReportMissing` and `AnimalListEffect.NavigateToReportMissing` exist
- ✅ `AnimalListViewModel` handles intent and emits navigation effect
- ✅ `AnimalListScreen` collects effect and calls `navigateToReportMissing()`
- ✅ `NavRoute.ReportMissing` placeholder exists (needs nested routes)
- ⚠️ `navigateToReportMissing()` is stubbed - will be enabled in T029 when NavGraph is updated

**Tasks:**
- [ ] T001 [P] Define `ReportMissingRoute` sealed interface with 5 nested routes (`ChipNumber`, `Photo`, `Description`, `ContactDetails`, `Summary`) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/navigation/NavRoute.kt`
- [ ] T002 [P] Create feature package structure `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/` with subdirectories: `presentation/mvi/`, `presentation/viewmodels/`, `ui/components/`, `ui/chipnumber/`, `ui/photo/`, `ui/description/`, `ui/contactdetails/`, `ui/summary/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core MVI infrastructure and reusable components that ALL user stories depend on

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T003 [P] Create `FlowStep` enum in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/ReportMissingUiState.kt`
- [ ] T004 [P] Create `ReportMissingUiState` data class with computed properties (`showProgressIndicator`, `progressStepNumber`) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/ReportMissingUiState.kt`
- [ ] T005 [P] Create `ReportMissingIntent` sealed interface in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/ReportMissingIntent.kt`
- [ ] T006 [P] Create `ReportMissingEffect` sealed interface in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/ReportMissingEffect.kt`
- [ ] T007 Create `ReportMissingReducer` with pure state transition functions in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/ReportMissingReducer.kt`
- [ ] T008 Create `ReportMissingViewModel` with StateFlow + SharedFlow in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/ReportMissingViewModel.kt`
- [ ] T009 Register `ReportMissingViewModel` in Koin module `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/ViewModelModule.kt`
- [ ] T010 [P] Create `StepProgressIndicator` composable (circular progress with "X/4" text) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/components/StepProgressIndicator.kt`
- [ ] T011 [P] Create `StepHeader` composable (back button + title + progress) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/components/StepHeader.kt`
- [ ] T012 [P] Create `ReportMissingUiStatePreviewProvider` for Compose previews in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/ReportMissingUiState.kt`

**Unit Tests** (MANDATORY - write FIRST, verify FAIL before implementation):

- [ ] T013 [P] Unit test for `ReportMissingUiState` computed properties in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/ReportMissingUiStateTest.kt`
- [ ] T014 [P] Unit test for `ReportMissingReducer` state transitions in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/ReportMissingReducerTest.kt`
- [ ] T015 Unit test for `ReportMissingViewModel` intent dispatch and effects in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/ReportMissingViewModelTest.kt`

**Checkpoint**: Foundation ready - MVI infrastructure complete, reusable components ready, unit tests passing

---

## Phase 3: User Story 1 - Complete Missing Pet Report (Priority: P1) 🎯 MVP

**Goal**: User can navigate from Animal List through all 5 screens (chip number → photo → description → contact details → summary) using "Continue" buttons, with progress indicator (1/4 through 4/4) on data collection screens and no progress indicator on summary.

**Independent Test**: Tap "report missing animal" button on Animal List (CTA launches step 1), navigate through all 4 data collection screens (verify progress indicator updates 1/4 → 2/4 → 3/4 → 4/4), reach summary screen (verify no progress indicator and that navigation never skips steps).

### E2E Tests for User Story 1 (MANDATORY) ✅

- [ ] T016 [P] [US1] Create Cucumber feature file for forward navigation in `/e2e-tests/java/src/test/resources/features/mobile/018-android-missing-pet-flow.feature`
- [ ] T017 [P] [US1] Add step definitions for forward navigation in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/mobile/ReportMissingPetSteps.java` (if not already present)

### Implementation for User Story 1

**Screen 1: Chip Number (Step 1/4)**:
- [ ] T018 [P] [US1] Create `ChipNumberContent` stateless composable with StepHeader, placeholder text, Continue button in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/chipnumber/ChipNumberContent.kt`
- [ ] T019 [US1] Create `ChipNumberScreen` state host composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/chipnumber/ChipNumberScreen.kt`

**Screen 2: Photo (Step 2/4)**:
- [ ] T020 [P] [US1] Create `PhotoContent` stateless composable with StepHeader, placeholder text, Continue button in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/photo/PhotoContent.kt`
- [ ] T021 [US1] Create `PhotoScreen` state host composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/photo/PhotoScreen.kt`

**Screen 3: Description (Step 3/4)**:
- [ ] T022 [P] [US1] Create `DescriptionContent` stateless composable with StepHeader, placeholder text, Continue button in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/description/DescriptionContent.kt`
- [ ] T023 [US1] Create `DescriptionScreen` state host composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/description/DescriptionScreen.kt`

**Screen 4: Contact Details (Step 4/4)**:
- [ ] T024 [P] [US1] Create `ContactDetailsContent` stateless composable with StepHeader, placeholder text, Continue button in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/contactdetails/ContactDetailsContent.kt`
- [ ] T025 [US1] Create `ContactDetailsScreen` state host composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/contactdetails/ContactDetailsScreen.kt`

**Screen 5: Summary (No Progress Indicator)**:
- [ ] T026 [P] [US1] Create `SummaryContent` stateless composable WITHOUT StepHeader, with Close button in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/summary/SummaryContent.kt`
- [ ] T027 [US1] Create `SummaryScreen` state host composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/summary/SummaryScreen.kt`

**Navigation Integration**:
- [ ] T028 [US1] Create nested nav graph `ReportMissingNavGraph` composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/ReportMissingNavGraph.kt`
- [ ] T029 [US1] Integrate nested nav graph into main `NavGraph.kt` under `NavRoute.ReportMissing` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/navigation/NavGraph.kt`

**Test Tags** (MANDATORY for E2E - aligned with existing screen objects):
- [ ] T030 [US1] Add testTag modifiers matching existing E2E patterns in `ReportMissingPetScreen.java` and `MicrochipNumberScreen.java`:
  - Shared: `reportMissingPet.backButton`, `reportMissingPet.progressIndicator`
  - Chip Number: `missingPet.microchip.input`, `missingPet.microchip.continueButton`, `missingPet.microchip.backButton`
  - Photo: `animalPhoto.browse`, `animalPhoto.continue`, `animalPhoto.remove`, `animalPhoto.confirmationCard`
  - Description: `description.continueButton`
  - Contact Details: `contactDetails.continueButton`
  - Summary: `summary.submitButton`

**Checkpoint**: User Story 1 complete - forward navigation works, progress indicator updates correctly, E2E tests pass

---

## Phase 4: User Story 2 - Navigate Backwards Through Flow (Priority: P2)

**Goal**: User can navigate backwards using back button (or system back gesture) from any screen. From screens 2-5: return to previous screen. From screen 1: exit flow to Animal List. State is preserved when navigating backwards.

**Independent Test**: Navigate to step 3/4, tap back, verify step 2/4 displays with progress "2/4". Navigate to step 1/4, tap back, verify Animal List screen displays.

### E2E Tests for User Story 2 (MANDATORY) ✅

- [ ] T031 [P] [US2] Add backward navigation scenarios to Cucumber feature file in `/e2e-tests/java/src/test/resources/features/mobile/018-android-missing-pet-flow.feature`
- [ ] T032 [P] [US2] Add step definitions for backward navigation in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/mobile/ReportMissingPetSteps.java`

### Implementation for User Story 2

**Back Navigation Logic** (simplified - Navigation Component handles step-aware behavior automatically):
- [ ] T033 [US2] Implement `NavigateBack` intent handling in ViewModel - always emit `NavigateBack` effect (no step checking needed) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/ReportMissingViewModel.kt`
- [ ] T034 [US2] Handle `NavigateBack` effect in `ReportMissingNavGraph.kt` - call `popBackStack()` (from step 1 this exits nested graph to AnimalList automatically) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/ReportMissingNavGraph.kt`

**Unit Tests**:
- [ ] T035 [P] [US2] Add unit tests for `NavigateBack` intent handling (verify `NavigateBack` effect emitted) in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/ReportMissingViewModelTest.kt`

**Checkpoint**: User Story 2 complete - backward navigation works, exit from step 1 works, E2E tests pass

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Final quality improvements and validation

- [ ] T036 [P] Add `@Preview` functions with `@PreviewParameter` to all Content composables (ChipNumberContent, PhotoContent, DescriptionContent, ContactDetailsContent, SummaryContent)
- [ ] T044 [P] Add multi-size Compose previews (compact phone, medium foldable, expanded tablet) for each Content composable to validate SC-005 responsiveness
- [ ] T045 Document preview results (screenshots or notes) in `/specs/018-android-missing-pet-flow/quickstart.md#ui-validation` to capture responsive QA evidence
- [ ] T037 [P] Add KDoc documentation to ViewModel, UiState, Intent, Effect, and Reducer classes
- [ ] T038 Run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` and verify 80% coverage for reportmissing feature
- [ ] T039 Run `./gradlew :composeApp:lint` and fix any violations in reportmissing feature
- [ ] T040 Validate quickstart.md - verify all development setup steps work correctly
- [ ] T046 Add navigation stability tests (unit/instrumentation) ensuring CTA launches step 1 and Next/Back never skip steps; document results in quickstart.md QA section

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational phase completion
- **User Story 2 (Phase 4)**: Can start after Phase 2, but integrates with US1 navigation
- **Polish (Phase 5)**: Depends on US1 and US2 being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on US2
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Back navigation builds on existing screens from US1

### Within Each User Story

- Tests (E2E feature file) SHOULD be written before or alongside implementation
- Content composables (stateless) before Screen composables (stateful)
- Navigation integration after all screens exist
- Test tags added as part of Content composable creation

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- All Content composables (T018, T020, T022, T024, T026) can be created in parallel
- E2E tests can be written in parallel with implementation

---

## Parallel Example: User Story 1 Screens

```bash
# Launch all stateless Content composables in parallel:
Task: T018 - ChipNumberContent in .../ui/chipnumber/ChipNumberContent.kt
Task: T020 - PhotoContent in .../ui/photo/PhotoContent.kt
Task: T022 - DescriptionContent in .../ui/description/DescriptionContent.kt
Task: T024 - ContactDetailsContent in .../ui/contactdetails/ContactDetailsContent.kt
Task: T026 - SummaryContent in .../ui/summary/SummaryContent.kt

# Then create Screen composables sequentially (depend on Content):
Task: T019 - ChipNumberScreen
Task: T021 - PhotoScreen
Task: T023 - DescriptionScreen
Task: T025 - ContactDetailsScreen
Task: T027 - SummaryScreen
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (routes, package structure)
2. Complete Phase 2: Foundational (MVI infrastructure, reusable components, unit tests)
3. Complete Phase 3: User Story 1 (5 screens, navigation, E2E tests)
4. **STOP and VALIDATE**: Test forward navigation independently
5. Demo/validate MVP

### Incremental Delivery

1. Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → MVP complete!
3. Add User Story 2 → Test independently → Full feature complete
4. Polish → Production ready

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify unit tests fail before implementing production code
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- This is a UI-only feature - no backend integration needed
- Existing E2E infrastructure (Cucumber + Appium) is already set up in `/e2e-tests/java/`
- Screen Objects already exist in `/e2e-tests/java/.../screens/` - extend as needed

