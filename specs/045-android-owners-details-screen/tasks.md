# Tasks: Android Owner's Details Screen

**Input**: Design documents from `/specs/045-android-owners-details-screen/`  
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, contracts/ ✅

**Tests**: Test requirements for this feature:

**MANDATORY - Android Unit Tests**:
- Location: `/composeApp/src/androidUnitTest/.../features/reportmissing/`
- Framework: JUnit Jupiter 6.0.1 + Kotlin Test + Turbine
- Coverage: 80% for ViewModel, UseCase, Validator
- Run: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- Convention: Given-When-Then structure with backtick test names

**E2E Tests**: Deferred (will be added after all 4 flow steps are implemented)

**Organization**: Tasks grouped by user story for independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: US1, US2, US3, US4 (from spec.md)
- Exact file paths included

---

## Phase 1: Setup

**Purpose**: No project setup needed - existing codebase with navigation scaffolding from spec 018.

*No tasks - proceed to Foundational phase.*

---

## Phase 2: Foundational (Domain Layer & Infrastructure)

**Purpose**: Core infrastructure that MUST be complete before user story implementation.

**⚠️ CRITICAL**: No UI work can begin until this phase is complete.

### Domain Models & DTOs

- [ ] T001 [P] Create `AnnouncementCreateRequest` DTO in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/domain/models/AnnouncementModels.kt`
- [ ] T002 [P] Create `AnnouncementResponse` DTO in same file `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/domain/models/AnnouncementModels.kt`

### Repository Interface

- [ ] T003 Create `AnnouncementRepository` interface with `createAnnouncement()` and `uploadPhoto()` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/domain/repositories/AnnouncementRepository.kt`

### Repository Implementation

- [ ] T004 Implement `AnnouncementRepositoryImpl` with Ktor client in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/data/repositories/AnnouncementRepositoryImpl.kt`

### Use Case

- [ ] T005 Create `SubmitAnnouncementUseCase` orchestrating 2-step submission (announcement + photo) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/domain/usecases/SubmitAnnouncementUseCase.kt`

### Validator Utility

- [ ] T006 Create `OwnerDetailsValidator` with `validatePhone()` and `validateEmail()` pure functions in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/util/OwnerDetailsValidator.kt`

### Flow State Extension

- [ ] T007 Add `rewardDescription` field and `updateRewardDescription()` method to `ReportMissingFlowState.kt` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/state/ReportMissingFlowState.kt`

### DI Registration

- [ ] T008 Register `ContentResolver`, `AnnouncementRepository`, and `SubmitAnnouncementUseCase` in Koin module `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/ReportMissingModule.kt`

**Checkpoint**: Foundation ready - MVI and UI implementation can now begin.

---

## Phase 3: User Story 2 - Validation (Priority: P2) 🎯 MVP Foundation

**Goal**: Implement validation logic and error display. This MUST be complete before submission can work correctly.

**Independent Test**: Enter invalid email "owner@", tap Continue, observe red border and error text "Enter a valid email address", no submission occurs.

### Unit Tests for US2 (MANDATORY) ✅

> **Write tests FIRST, ensure they FAIL before implementation**

- [ ] T009 [P] [US2] Create unit test for `OwnerDetailsValidator` (phone: 7-11 digits, leading +, reject letters; email: RFC 5322) in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/util/OwnerDetailsValidatorTest.kt`

### MVI Components (shared with US1)

- [ ] T010 [P] [US2] Create `OwnerDetailsUiState` immutable data class (includes phoneError, emailError fields) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/OwnerDetailsUiState.kt`
- [ ] T011 [P] [US2] Create `OwnerDetailsUserIntent` sealed interface in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/OwnerDetailsUserIntent.kt`
- [ ] T012 [P] [US2] Create `OwnerDetailsUiEffect` sealed interface (NavigateToSummary, NavigateBack, ShowSnackbar) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/mvi/OwnerDetailsUiEffect.kt`

### ViewModel with Validation

- [ ] T013 [US2] Implement `OwnerDetailsViewModel` with StateFlow, SharedFlow, dispatchIntent() - focus on validation logic first (ContinueClicked validates before any submission) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/OwnerDetailsViewModel.kt`
- [ ] T014 [US2] Register `OwnerDetailsViewModel` in Koin DI module `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/ReportMissingModule.kt`
- [ ] T015 [P] [US2] Create unit test for `OwnerDetailsViewModel` validation (invalid phone → phoneError set, invalid email → emailError set, ShowSnackbar effect) in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/OwnerDetailsViewModelTest.kt`

### UI with Error Display

- [ ] T016 [US2] Implement `ContactDetailsScreen` (state host) collecting state and dispatching intents in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/contactdetails/ContactDetailsScreen.kt`
- [ ] T017 [US2] Implement `ContactDetailsContent` (stateless) with phone and email OutlinedTextFields showing isError and supportingText, Continue button in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/contactdetails/ContactDetailsContent.kt`
- [ ] T018 [US2] Add testTag modifiers to all interactive composables (ownersDetails.phoneInput, ownersDetails.emailInput, ownersDetails.continueButton, etc.) in ContactDetailsContent
- [ ] T019 [US2] Create `OwnerDetailsUiStateProvider` PreviewParameterProvider with Initial, ValidationError states in same file as ContactDetailsContent
- [ ] T020 [US2] Add @Preview function for ContactDetailsContent using @PreviewParameter in same file

### Navigation

- [ ] T021 [US2] Update ContactDetails route in `ReportMissingNavGraph.kt` to use `OwnerDetailsViewModel` and wire to `ContactDetailsScreen` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/ReportMissingNavGraph.kt`

### Run Tests for US2

- [ ] T022 [US2] Run `./gradlew :composeApp:testDebugUnitTest` and verify US2 tests pass (validator + validation logic)

**Checkpoint**: Validation works - invalid inputs show errors, block any submission attempt.

---

## Phase 4: User Story 1 - Submit Report (Priority: P1) 🎯 MVP

**Goal**: User can enter valid phone and email, tap Continue, execute 2-step backend submission, and navigate to summary with managementPassword.

**Depends on**: Phase 3 (validation must work first)

**Independent Test**: Fill phone "+48123456789" and email "owner@example.com", tap Continue, verify API calls complete and navigation to summary.

### Unit Tests for US1 (MANDATORY) ✅

- [ ] T023 [P] [US1] Create unit test for `SubmitAnnouncementUseCase` (success path, step 1 fail, step 2 fail) in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/domain/usecases/SubmitAnnouncementUseCaseTest.kt`
- [ ] T024 [P] [US1] Create fake `FakeAnnouncementRepository` for testing in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/fakes/FakeAnnouncementRepository.kt`
- [ ] T025 [P] [US1] Add submission flow tests to `OwnerDetailsViewModelTest` (ContinueClicked with valid inputs → isSubmitting=true → NavigateToSummary effect) in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/OwnerDetailsViewModelTest.kt`

### Implementation for US1

- [ ] T026 [US1] Add submission logic to `OwnerDetailsViewModel` - after validation passes, call SubmitAnnouncementUseCase, handle success/failure in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/OwnerDetailsViewModel.kt`
- [ ] T027 [US1] Update `ContactDetailsContent` to show CircularProgressIndicator in Continue button when isSubmitting=true in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/contactdetails/ContactDetailsContent.kt`
- [ ] T028 [US1] Update `OwnerDetailsUiStateProvider` to include Loading state in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/contactdetails/ContactDetailsContent.kt`

### Run Tests for US1

- [ ] T029 [US1] Run `./gradlew :composeApp:testDebugUnitTest` and verify US1 tests pass
- [ ] T030 [US1] Run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` and verify 80% coverage for ViewModel and UseCase

**Checkpoint**: Submit flow works - valid inputs trigger 2-step submission, navigate to summary on success.

---

## Phase 5: User Story 4 - Failure Handling (Priority: P2)

**Goal**: When submission fails (network/backend error), show Snackbar with "Retry" action. User can retry full 2-step submission.

**Depends on**: Phase 4 (submission must work first)

**Independent Test**: Fill valid phone/email, simulate network failure, observe Snackbar with Retry, tap Retry, verify submission retries.

### Unit Tests for US4 (MANDATORY) ✅

- [ ] T031 [P] [US4] Add submission failure tests to `OwnerDetailsViewModelTest` (network error → isSubmitting=false, ShowSnackbar with Retry; RetryClicked → isSubmitting=true, retry submission) in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/OwnerDetailsViewModelTest.kt`
- [ ] T032 [P] [US4] Add step 2 failure test to `SubmitAnnouncementUseCaseTest` (step 1 success, step 2 fails → returns failure) in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/domain/usecases/SubmitAnnouncementUseCaseTest.kt`

### Implementation for US4

- [ ] T033 [US4] Add `RetryClicked` intent handling to `OwnerDetailsViewModel` - retry full 2-step submission in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/OwnerDetailsViewModel.kt`
- [ ] T034 [US4] Implement SnackbarHost and effect collection for ShowSnackbar with "Retry" action in `ContactDetailsScreen` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/contactdetails/ContactDetailsScreen.kt`
- [ ] T035 [US4] Disable TopAppBar back navigation when isSubmitting=true in `ContactDetailsContent` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/contactdetails/ContactDetailsContent.kt`

### Run Tests for US4

- [ ] T036 [US4] Run `./gradlew :composeApp:testDebugUnitTest` and verify US4 tests pass

**Checkpoint**: Error handling works - failures show Snackbar, Retry works, back blocked during submission.

---

## Phase 6: User Story 3 - Reward Field (Priority: P3)

**Goal**: User can enter optional reward description (up to 120 characters) with live character counter.

**Independent of**: Phases 3-5 (can be implemented in parallel if desired)

**Independent Test**: Enter "$250 gift card", navigate away and back, confirm text persists. Try typing 121st character, observe it's rejected.

### Unit Tests for US3 (MANDATORY) ✅

- [ ] T037 [P] [US3] Add reward field tests to `OwnerDetailsViewModelTest` (UpdateReward truncates at 120 chars, character count updates, persists in flow state) in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/OwnerDetailsViewModelTest.kt`

### Implementation for US3

- [ ] T038 [US3] Add reward field handling to `OwnerDetailsViewModel` - UpdateReward intent, sync with ReportMissingFlowState in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/presentation/viewmodels/OwnerDetailsViewModel.kt`
- [ ] T039 [US3] Add reward OutlinedTextField with "(optional)" label, maxLength=120, character counter to `ContactDetailsContent` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/reportmissing/ui/contactdetails/ContactDetailsContent.kt`
- [ ] T040 [US3] Add testTag `ownersDetails.rewardInput` to reward field in `ContactDetailsContent`

### Run Tests for US3

- [ ] T041 [US3] Run `./gradlew :composeApp:testDebugUnitTest` and verify US3 tests pass

**Checkpoint**: Reward field works - optional, 120 char limit, persists across navigation.

---

## Phase 7: Polish & Final Verification

**Purpose**: Final validation and documentation

- [ ] T042 Run full test suite `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` and verify 80% coverage
- [ ] T043 [P] Add KDoc to complex public APIs in ViewModel and UseCase (skip self-explanatory)
- [ ] T044 Verify all testTags present for automation (ownersDetails.backButton, phoneInput, emailInput, rewardInput, continueButton, progressBadge, title, subtitle)
- [ ] T045 Run quickstart.md verification checklist manually
- [ ] T046 [P] Code cleanup - remove any TODO comments, ensure consistent formatting

---

## Dependencies & Execution Order

### Phase Dependencies

```
Phase 1: Setup (empty) 
    ↓
Phase 2: Foundational (domain layer, repository, use case, validator, DI)
    ↓ BLOCKS all user stories
Phase 3: US2 - Validation (MVI + UI foundation) ← MUST be first
    ↓
Phase 4: US1 - Submit Report (uses validation)
    ↓
Phase 5: US4 - Failure Handling (extends submission)
    ↓
Phase 6: US3 - Reward Field (independent enhancement)
    ↓
Phase 7: Polish
```

### Why Validation First?

1. **Validation gates submission** - you can't properly test "submit with valid data" without validation working
2. **MVI foundation** - Phase 3 creates UiState, UserIntent, UiEffect which Phase 4 extends
3. **UI foundation** - Phase 3 creates ContactDetailsScreen/Content which Phase 4 enhances
4. **TDD principle** - implement the simplest behavior first (validation), then add complexity (submission)

### Task Dependencies Within Phases

**Phase 2** (parallel where marked):
- T001, T002 [P] → T003 → T004, T005, T006 [P] → T007 → T008

**Phase 3** (US2 - Validation):
- T009 (validator tests) can start immediately
- T010-T012 (MVI) [P] can start immediately
- T013 (ViewModel) depends on T010-T012
- T014 depends on T013
- T015 (VM tests) depends on T013
- T016-T020 (UI) depends on T010-T012
- T021 (navigation) depends on T016

**Phase 4** (US1 - Submission):
- T023-T025 (tests) [P] can start immediately
- T026-T028 (implementation) depend on Phase 3 completion

**Phases 5-6**: Each depends on previous phase completion

### Parallel Opportunities

```bash
# Phase 2 parallel:
T001, T002 (DTOs) | T006 (Validator) - different files

# Phase 3 parallel - validator tests and MVI:
T009 (validator test) | T010, T011, T012 (MVI) - different files

# Phase 4 parallel - all tests:
T023 (UseCase tests) | T024 (Fake) | T025 (VM tests) - different files

# Phase 5 parallel:
T031 (VM failure tests) | T032 (UseCase failure tests) - different files
```

---

## Parallel Example: Phase 3 (US2 - Validation)

```bash
# Batch 1 - Launch validator test and MVI components together:
Task T009: "Unit test for OwnerDetailsValidator"
Task T010: "Create OwnerDetailsUiState"
Task T011: "Create OwnerDetailsUserIntent"
Task T012: "Create OwnerDetailsUiEffect"

# Batch 2 - After MVI components exist:
Task T013: "Implement OwnerDetailsViewModel (validation focus)"

# Batch 3 - After ViewModel exists:
Task T014: "Register ViewModel in Koin"
Task T015: "Unit test for ViewModel validation"
Task T016: "Implement ContactDetailsScreen"
Task T017: "Implement ContactDetailsContent"
```

---

## Implementation Strategy

### MVP First (US2 → US1 → US4)

1. Complete Phase 2: Foundational (domain layer, DI)
2. Complete Phase 3: US2 (validation + MVI foundation + UI skeleton)
3. Complete Phase 4: US1 (submission logic on top of validation)
4. Complete Phase 5: US4 (error handling)
5. **STOP and VALIDATE**: Full submit flow works with validation and error handling
6. Demo/review

### Why This Order Works

- **Phase 3 (Validation)** creates the MVI structure, ViewModel, and UI that Phase 4 builds upon
- **Phase 4 (Submission)** adds submission logic to an already-working validation flow
- **Phase 5 (Errors)** handles the failure path of submission

### Full Feature

1. Continue with Phase 6: US3 (reward field)
2. Complete Phase 7: Polish
3. Final review

---

## Summary

| Phase | Tasks | Stories | Parallel Opportunities |
|-------|-------|---------|----------------------|
| Setup | 0 | - | - |
| Foundational | 8 | - | T001-T002, T006 |
| US2 (P2) | 14 | Validation ← First! | T009-T012 |
| US1 (P1) | 8 | Submit Report | T023-T025 |
| US4 (P2) | 6 | Failure Handling | T031-T032 |
| US3 (P3) | 5 | Reward Field | T037 |
| Polish | 5 | - | T043, T046 |
| **Total** | **46** | **4 stories** | **Multiple batches** |

**MVP Scope**: Phases 2-5 (36 tasks) - delivers full submit flow with validation and error handling

**Logical Order**: Validation (US2) → Submit (US1) → Errors (US4) → Reward (US3)

**Coverage Target**: 80% for ViewModel, UseCase, Validator

---

## Notes

- [P] = parallelizable (different files)
- [US#] = maps to user story from spec.md
- Tests written FIRST per TDD workflow
- Commit after each logical task group
- Stop at checkpoints to verify independently

