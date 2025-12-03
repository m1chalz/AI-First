# Tasks: Android Animal Description Screen

**Input**: Design documents from `/specs/042-android-animal-description-screen/`
**Prerequisites**: plan.md ✓, spec.md ✓, research.md ✓, data-model.md ✓, contracts/ ✓, quickstart.md ✓

**Tests**: Test requirements for this project:

**MANDATORY - Android Unit Tests**:
- Location: `/composeApp/src/androidUnitTest/` (JUnit + Turbine), 80% coverage
- Scope: ViewModel (MVI architecture), Validator
- Run: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- Convention: MUST follow Given-When-Then structure with descriptive backtick names

**MANDATORY - End-to-End Tests**:
- Mobile: `/e2e-tests/src/test/` (Maven + Cucumber + Appium)
- Screen Object Model with `@AndroidFindBy` annotations
- Convention: MUST structure scenarios with Given-When-Then phases

**Note**: This is an **Android-only UI feature** with no backend changes. Data stored in in-memory flow state.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project verification and domain model creation

- [X] T001 Verify prerequisites exist: `ReportMissingPetFlowState` contract (`ReportMissingFlowState.kt`), `DescriptionScreen.kt` placeholder, `LocationRepository.kt`, `GetCurrentLocationUseCase.kt`
- [X] T002 [P] Create `AnimalGender` enum in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/domain/models/AnimalGender.kt`
- [X] T003 [P] Create `SpeciesTaxonomy` object with bundled species list in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/data/SpeciesTaxonomy.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core MVI infrastructure and state management that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T004 Extend `ReportMissingPetFlowState.FlowData` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/state/ReportMissingFlowState.kt` with animal description fields (petName, disappearanceDate, animalSpecies, animalRace, animalGender, animalAge, latitude, longitude, additionalDescription)
- [X] T005 Create `AnimalDescriptionUiState` data class in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/AnimalDescriptionUiState.kt`
- [X] T006 [P] Create `AnimalDescriptionUserIntent` sealed interface in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/AnimalDescriptionUserIntent.kt`
- [X] T007 [P] Create `AnimalDescriptionUiEffect` sealed interface in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/AnimalDescriptionUiEffect.kt`
- [X] T008 Create `AnimalDescriptionValidator` with `ValidationResult` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/util/AnimalDescriptionValidator.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Provide animal context before contact (Priority: P1) 🎯 MVP

**Goal**: Reporters can enter descriptive data (pet name, date, species, race, gender) so responders understand the case before reading contact details

**Independent Test**: Launch Step 3 via navigation from Step 2, populate required fields, advance to Step 4

### Tests for User Story 1 (MANDATORY) ✅

**Android Unit Tests**:
- [X] T009 [P] [US1] Unit test for `AnimalDescriptionValidator` (species, race, gender validation) in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/util/AnimalDescriptionValidatorTest.kt`
- [X] T010 [P] [US1] Unit test for `AnimalDescriptionViewModel` initial state and field updates in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/AnimalDescriptionViewModelTest.kt`

**End-to-End Tests**:
- [X] T011 [P] [US1] Create `AnimalDescriptionScreen.java` Screen Object in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/screens/AnimalDescriptionScreen.java`
- [X] T012 [P] [US1] Create `animal-description.feature` Cucumber scenarios for US1 in `/e2e-tests/java/src/test/resources/features/mobile/animal-description.feature`
- [X] T013 [P] [US1] Create `AnimalDescriptionSteps.java` step definitions in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/mobile/AnimalDescriptionSteps.java`

### Implementation for User Story 1

**Android** (MVI Architecture):
- [X] T014 [US1] Create `AnimalDescriptionViewModel` with intent handling for UpdatePetName, UpdateDate, UpdateSpecies, UpdateRace, UpdateGender in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/AnimalDescriptionViewModel.kt`
- [X] T015 [US1] Register `AnimalDescriptionViewModel` in Koin module `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/ViewModelModule.kt`
- [X] T016 [P] [US1] Create `DatePickerField` composable with Material 3 DatePickerDialog (future dates disabled) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/description/components/DatePickerField.kt`
- [X] T017 [P] [US1] Create `SpeciesDropdown` composable with ExposedDropdownMenuBox in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/description/components/SpeciesDropdown.kt`
- [X] T018 [P] [US1] Create `GenderSelector` composable with two selectable cards in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/description/components/GenderSelector.kt`
- [X] T019 [US1] Create stateless `AnimalDescriptionContent` composable with date picker, pet name, species dropdown, race field, gender selector, age field, continue button in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/description/AnimalDescriptionContent.kt`
- [X] T020 [US1] Update `DescriptionScreen` (state host) to wire ViewModel, collect state, and handle effects (validation Snackbars, GPS failure Snackbar, and OpenSettings deep link) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/description/DescriptionScreen.kt`
- [X] T021 [US1] Create `AnimalDescriptionUiStateProvider` PreviewParameterProvider and add @Preview functions in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/description/AnimalDescriptionContent.kt`
- [X] T022 [US1] Add testTag modifiers to all US1 interactive composables: `animalDescription.datePickerField`, `animalDescription.petNameField`, `animalDescription.speciesDropdown`, `animalDescription.raceField`, `animalDescription.genderFemale`, `animalDescription.genderMale`, `animalDescription.ageField`, `animalDescription.continueButton`

**Checkpoint**: User Story 1 complete - required fields form working, navigation to Step 4

---

## Phase 4: User Story 2 - Capture last known location details (Priority: P2)

**Goal**: Caregivers can use GPS shortcut or manually enter latitude/longitude for last known pet location

**Independent Test**: Tap Request GPS position, verify permissions and auto-fill, modify coordinates manually, confirm validation

### Tests for User Story 2 (MANDATORY) ✅

**Android Unit Tests**:
- [ ] T023 [P] [US2] Unit test for `AnimalDescriptionValidator` coordinate validation (latitude -90 to 90, longitude -180 to 180) in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/util/AnimalDescriptionValidatorTest.kt`
- [ ] T024 [P] [US2] Unit test for `AnimalDescriptionViewModel` GPS request handling (success + permission denied), coordinate updates, and emitted Snackbar/OpenSettings effects in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/AnimalDescriptionViewModelTest.kt`

**End-to-End Tests**:
- [ ] T025 [P] [US2] Add GPS location scenarios (happy path + permission denied fallback with Snackbar + Settings action + verify helper text clarifying coordinates are the only location fallback per FR-015) to `animal-description.feature` in `/e2e-tests/src/test/resources/features/mobile/animal-description.feature`
- [ ] T026 [P] [US2] Add GPS step definitions to `AnimalDescriptionSteps.java` in `/e2e-tests/src/test/java/com/petspot/stepsmobile/AnimalDescriptionSteps.java`, covering the permission denied Snackbar and Settings deep link flow

### Implementation for User Story 2

**Android** (MVI Architecture):
- [ ] T027 [US2] Extend `AnimalDescriptionViewModel` with RequestGpsPosition, UpdateLatitude, UpdateLongitude intent handling (inject `GetCurrentLocationUseCase`), including error states that emit `ShowSnackbar` + `OpenSettings` effects when permissions fail
- [ ] T028 [P] [US2] Create `GpsLocationSection` composable with Request GPS button (loading state), latitude/longitude fields, and inline helper copy explaining that coordinates are the only location fallback in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/description/components/GpsLocationSection.kt`
- [ ] T029 [US2] Integrate `GpsLocationSection` into `AnimalDescriptionContent` with proper callbacks, helper text display, and state binding for success and failure cases
- [ ] T030 [US2] Add testTag modifiers for GPS elements: `animalDescription.requestGpsButton`, `animalDescription.latitudeField`, `animalDescription.longitudeField`

**Checkpoint**: User Story 2 complete - GPS request and manual coordinate entry working

---

## Phase 5: User Story 3 - Maintain validation, persistence, and safe exits (Priority: P3)

**Goal**: Reporters see validation errors, data persists across navigation, and incomplete submissions are blocked

**Independent Test**: Populate fields, navigate back to Step 2, return to Step 3, confirm persistence; clear required field, tap Continue, verify Snackbar and inline errors

### Tests for User Story 3 (MANDATORY) ✅

**Android Unit Tests**:
- [ ] T031 [P] [US3] Unit test for `AnimalDescriptionViewModel` ContinueClicked with validation failure (shows Snackbar, sets field errors) in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/AnimalDescriptionViewModelTest.kt`
- [ ] T032 [P] [US3] Unit test for `AnimalDescriptionViewModel` state persistence (init from flow state, save to flow state) in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/AnimalDescriptionViewModelTest.kt`

**End-to-End Tests**:
- [ ] T033 [P] [US3] Add validation and persistence scenarios to `animal-description.feature` in `/e2e-tests/src/test/resources/features/mobile/animal-description.feature`
- [ ] T034 [P] [US3] Add validation step definitions to `AnimalDescriptionSteps.java` in `/e2e-tests/src/test/java/com/petspot/stepsmobile/AnimalDescriptionSteps.java`

### Implementation for User Story 3

**Android** (MVI Architecture):
- [ ] T035 [US3] Implement `AnimalDescriptionViewModel` ContinueClicked with full validation, Snackbar effect, and NavigateToContactDetails effect
- [ ] T036 [US3] Implement `AnimalDescriptionViewModel` BackClicked with state save and NavigateBack effect
- [ ] T037 [US3] Implement `AnimalDescriptionViewModel` initialization from `ReportMissingPetFlowState` (restore previous values including date persistence per FR-004)
- [ ] T038 [P] [US3] Create `CharacterCounterTextField` composable with 500 char limit and live counter in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/description/components/CharacterCounterTextField.kt`
- [ ] T039 [US3] Add additional description field to `AnimalDescriptionContent` using `CharacterCounterTextField`
- [ ] T040 [US3] Add Snackbar host and effect handling in `DescriptionScreen` for validation error messages (extends T020 initial effect wiring with US3-specific validation Snackbar logic)
- [ ] T041 [US3] Wire up BackClicked intent to TopAppBar back arrow in `DescriptionScreen`
- [ ] T042 [US3] Add testTag for description field: `animalDescription.descriptionField`

**Checkpoint**: User Story 3 complete - full validation, persistence, error handling working

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Coverage verification, code quality, final integration

- [ ] T043 Run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` and verify 80% coverage for ViewModel and Validator
- [ ] T044 [P] Add KDoc documentation to complex public APIs in ViewModel and Validator (skip self-explanatory)
- [ ] T045 Run quickstart.md validation steps to verify full feature flow
- [ ] T046 Visual review: verify UI matches Figma design (colors, spacing, typography)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational - can start after Phase 2
- **User Story 2 (Phase 4)**: Depends on Foundational - can start after Phase 2 (parallel with US1 if desired)
- **User Story 3 (Phase 5)**: Depends on Foundational - can start after Phase 2 (parallel with US1/US2 if desired)
- **Polish (Phase 6)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Core form fields - MVP, no dependencies on other stories
- **User Story 2 (P2)**: GPS location - independent of US1, adds GPS functionality
- **User Story 3 (P3)**: Validation/persistence - builds on US1 field validation, adds comprehensive error handling

### Within Each User Story

- Tests MUST be written and FAIL before implementation
- ViewModel logic before UI components
- UI components before integration
- Story complete before moving to next priority

### Parallel Opportunities

- T002, T003 can run in parallel (Setup)
- T006, T007 can run in parallel (MVI artifacts)
- T011, T012, T013 can run in parallel (E2E setup)
- T016, T017, T018 can run in parallel (UI components)
- All US2/US3 tests can run in parallel with US1 implementation (after Foundational)

---

## Parallel Example: User Story 1

```bash
# Launch all US1 tests together (after Phase 2):
Task T009: "Unit test for AnimalDescriptionValidator"
Task T010: "Unit test for AnimalDescriptionViewModel"
Task T011: "Create AnimalDescriptionScreen.java Screen Object"
Task T012: "Create animal-description.feature Cucumber scenarios"
Task T013: "Create AnimalDescriptionSteps.java step definitions"

# Launch UI components in parallel:
Task T016: "Create DatePickerField composable"
Task T017: "Create SpeciesDropdown composable"
Task T018: "Create GenderSelector composable"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (3 tasks)
2. Complete Phase 2: Foundational (5 tasks) - CRITICAL
3. Complete Phase 3: User Story 1 (14 tasks)
4. **STOP and VALIDATE**: Test form fields and navigation independently
5. Demo if ready - basic form flow working

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test GPS functionality → Deploy/Demo
4. Add User Story 3 → Test validation/persistence → Deploy/Demo
5. Polish phase → Final quality checks

### Single Developer Strategy

1. Complete Setup + Foundational
2. User Story 1: Tests first, then implementation, verify independently
3. User Story 2: Tests first, then implementation, verify independently
4. User Story 3: Tests first, then implementation, verify independently
5. Polish phase: Coverage check, documentation

---

## Summary

| Phase | Tasks | Purpose |
|-------|-------|---------|
| Phase 1: Setup | 3 | Prerequisites and domain models |
| Phase 2: Foundational | 5 | MVI infrastructure (BLOCKS user stories) |
| Phase 3: US1 (P1) | 14 | Core form fields - MVP |
| Phase 4: US2 (P2) | 8 | GPS location capture |
| Phase 5: US3 (P3) | 12 | Validation and persistence |
| Phase 6: Polish | 4 | Coverage and quality |
| **Total** | **46** | |

**Parallel opportunities**: 18 tasks marked [P]
**MVP scope**: Phases 1-3 (22 tasks)
**Independent test criteria per story**: ✅ Defined

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Existing infrastructure from spec 018 (navigation), spec 026 (location permissions), spec 038 (MVI pattern)

