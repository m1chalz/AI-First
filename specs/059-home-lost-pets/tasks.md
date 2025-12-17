# Tasks: Home Lost Pets Teaser

**Input**: Design documents from `/specs/059-home-lost-pets/`
**Prerequisites**: plan.md, spec.md, data-model.md, research.md

**Tests**: Test requirements for this project:

**MANDATORY - Android Unit Tests**:
- Location: `/composeApp/src/androidUnitTest/`
- Framework: JUnit 6 + Kotlin Test + Turbine (Flow testing)
- Coverage target: 80% line + branch coverage
- Scope: Use case, Reducer, ViewModel
- Run: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- Convention: MUST follow Given-When-Then structure with backtick descriptive names

**MANDATORY - End-to-End Tests**:
- Location: `/e2e-tests/java/src/test/resources/features/mobile/`
- Framework: Java 21 + Maven + Appium + Cucumber
- Screen Objects: `/e2e-tests/java/src/test/java/.../screens/`
- Step definitions: `/e2e-tests/java/src/test/java/.../steps/mobile/`
- All user stories MUST have E2E test coverage

**Organization**: Tasks are grouped by user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)

---

## Phase 1: Setup

**Purpose**: Verify project structure and dependencies are ready

- [ ] T001 Verify existing project compiles with `./gradlew :composeApp:assembleDebug`
- [ ] T002 Verify existing tests pass with `./gradlew :composeApp:testDebugUnitTest`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Create MVI classes, use case, and DI wiring that ALL user stories depend on

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

### Domain Layer

- [ ] T003 [P] Create `GetRecentAnimalsUseCase` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/domain/usecases/GetRecentAnimalsUseCase.kt` (filters MISSING, sorts by date, limits to 5)
- [ ] T004 [P] Create `GetRecentAnimalsUseCaseTest` in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/composeapp/domain/usecases/GetRecentAnimalsUseCaseTest.kt`

### MVI Classes

- [ ] T005 [P] Create `LostPetsTeaserUiState` data class in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/lostPetsTeaser/presentation/mvi/LostPetsTeaserUiState.kt`
- [ ] T006 [P] Create `LostPetsTeaserIntent` sealed interface in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/lostPetsTeaser/presentation/mvi/LostPetsTeaserIntent.kt`
- [ ] T007 [P] Create `LostPetsTeaserEffect` sealed class in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/lostPetsTeaser/presentation/mvi/LostPetsTeaserEffect.kt`
- [ ] T008 [P] Create `LostPetsTeaserReducer` object in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/lostPetsTeaser/presentation/mvi/LostPetsTeaserReducer.kt`
- [ ] T009 [P] Create `LostPetsTeaserReducerTest` in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/lostPetsTeaser/presentation/mvi/LostPetsTeaserReducerTest.kt`

### ViewModel

- [ ] T010 Create `LostPetsTeaserViewModel` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/lostPetsTeaser/presentation/viewmodels/LostPetsTeaserViewModel.kt`
- [ ] T011 Create `LostPetsTeaserViewModelTest` in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/lostPetsTeaser/presentation/viewmodels/LostPetsTeaserViewModelTest.kt`

### DI Registration

- [ ] T012 Add `GetRecentAnimalsUseCase` to `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/DomainModule.kt`
- [ ] T013 Add `LostPetsTeaserViewModel` to `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/ViewModelModule.kt`

**Checkpoint**: Foundation ready - run `./gradlew :composeApp:testDebugUnitTest` and verify all tests pass

---

## Phase 3: User Story 1 - View Lost Pets Teaser on Home Page (Priority: P1) 🎯 MVP

**Goal**: Display up to 5 recent lost pet entries on the home page with loading, empty, and error states

**Independent Test**: Navigate to home tab and verify teaser displays with pet entries using same visual design as full lost pets list

### Unit Tests for US1

- [ ] T014 [P] [US1] Add test `should emit loading then success state when LoadData dispatched` to `LostPetsTeaserViewModelTest.kt`
- [ ] T015 [P] [US1] Add test `should emit loading then error state when repository fails` to `LostPetsTeaserViewModelTest.kt`
- [ ] T016 [P] [US1] Add test `should show empty state when no MISSING pets available` to `LostPetsTeaserViewModelTest.kt`
- [ ] T017 [P] [US1] Add test `should filter only MISSING status animals` to `GetRecentAnimalsUseCaseTest.kt`
- [ ] T018 [P] [US1] Add test `should sort by lastSeenDate descending` to `GetRecentAnimalsUseCaseTest.kt`
- [ ] T019 [P] [US1] Add test `should limit results to 5` to `GetRecentAnimalsUseCaseTest.kt`

### UI Implementation for US1

- [ ] T020 [US1] Create `LostPetsTeaserContent` stateless composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/lostPetsTeaser/ui/LostPetsTeaserContent.kt` (displays list of AnimalCards, loading, empty, error states)
- [ ] T021 [US1] Add test tags to `LostPetsTeaserContent`: `lostPetsTeaser.container`, `lostPetsTeaser.loading`, `lostPetsTeaser.error`, `lostPetsTeaser.emptyState`, `lostPetsTeaser.petCard.${petId}`
- [ ] T022 [US1] Create `PreviewParameterProvider<LostPetsTeaserUiState>` with Loading, Success (5 pets), Success (2 pets), Empty, Error states
- [ ] T023 [US1] Add `@Preview` function for `LostPetsTeaserContent` using `@PreviewParameter`
- [ ] T024 [US1] Create `LostPetsTeaser` stateful composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/lostPetsTeaser/ui/LostPetsTeaser.kt` (gets ViewModel via koinViewModel, handles effects)
- [ ] T025 [US1] Create `HomeScreen` composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/home/ui/HomeScreen.kt` (LazyColumn container embedding LostPetsTeaser)
- [ ] T026 [US1] Update `MainScaffold.kt` to use `HomeScreen` instead of `PlaceholderScreen` for `HomeRoute.Root`

### E2E Tests for US1

- [ ] T027 [P] [US1] Create `LostPetsTeaserScreen.java` Screen Object in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/screens/LostPetsTeaserScreen.java`
- [ ] T028 [P] [US1] Create `home_lost_pets_teaser.feature` with scenario "View lost pets teaser on home page" in `/e2e-tests/java/src/test/resources/features/mobile/home_lost_pets_teaser.feature`
- [ ] T029 [US1] Create `HomeLostPetsTeaserSteps.java` step definitions in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/mobile/HomeLostPetsTeaserSteps.java`

**Checkpoint**: User Story 1 complete - home page shows lost pets teaser with loading/success/empty/error states

---

## Phase 4: User Story 2 - Navigate to Full Lost Pets List (Priority: P1)

**Goal**: "View All Lost Pets" button navigates to Lost Pets tab

**Independent Test**: Tap "View All Lost Pets" button and verify Lost Pets tab becomes active

### Unit Tests for US2

- [ ] T030 [P] [US2] Add test `should emit NavigateToLostPetsList effect when ViewAllClicked dispatched` to `LostPetsTeaserViewModelTest.kt`

### UI Implementation for US2

- [ ] T031 [US2] Add "View All Lost Pets" button to `LostPetsTeaserContent.kt` with blue styling and test tag `lostPetsTeaser.viewAllButton`
- [ ] T032 [US2] Handle `ViewAllClicked` intent in `LostPetsTeaserViewModel` to emit `NavigateToLostPetsList` effect
- [ ] T033 [US2] Handle `NavigateToLostPetsList` effect in `HomeScreen` to switch to Lost Pets tab via NavController

### E2E Tests for US2

- [ ] T034 [P] [US2] Add scenario "Navigate to full lost pets list" to `home_lost_pets_teaser.feature`
- [ ] T035 [US2] Add step definitions for View All navigation to `HomeLostPetsTeaserSteps.java`

**Checkpoint**: User Story 2 complete - "View All" button navigates to Lost Pets tab

---

## Phase 5: User Story 3 - View Lost Pet Details from Teaser (Priority: P1)

**Goal**: Tapping a pet card navigates to Lost Pets tab and shows pet details

**Independent Test**: Tap any pet card in teaser, verify navigation to Lost Pets tab with that pet's details displayed

### Unit Tests for US3

- [ ] T036 [P] [US3] Add test `should emit NavigateToPetDetails effect with petId when PetClicked dispatched` to `LostPetsTeaserViewModelTest.kt`

### UI Implementation for US3

- [ ] T037 [US3] Handle `PetClicked` intent in `LostPetsTeaserViewModel` to emit `NavigateToPetDetails(petId)` effect
- [ ] T038 [US3] Handle `NavigateToPetDetails` effect in `HomeScreen` to switch to Lost Pets tab and navigate to pet details
- [ ] T039 [US3] Wire `AnimalCard` onClick in `LostPetsTeaserContent` to dispatch `PetClicked(petId)` intent

### E2E Tests for US3

- [ ] T040 [P] [US3] Add scenario "View lost pet details from teaser" to `home_lost_pets_teaser.feature`
- [ ] T041 [US3] Add step definitions for pet card navigation to `HomeLostPetsTeaserSteps.java`

**Checkpoint**: User Story 3 complete - pet card clicks navigate to pet details in Lost Pets tab

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final verification, documentation, and cleanup

- [ ] T042 Run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` and verify 80% coverage for new code
- [ ] T043 Run `./gradlew :composeApp:lintDebug` and fix any lint issues
- [ ] T044 Verify all test identifiers follow constitution pattern `{screen}.{element}.{action}`
- [ ] T045 Add KDoc to `GetRecentAnimalsUseCase` and `LostPetsTeaserViewModel` public APIs (skip self-explanatory)
- [ ] T046 Run E2E tests with `mvn test -Dtest=AndroidTestRunner` from `/e2e-tests/java/`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - verification only
- **Foundational (Phase 2)**: Depends on Setup - BLOCKS all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational - MVP delivery
- **User Story 2 (Phase 4)**: Depends on US1 UI completion (needs button in teaser)
- **User Story 3 (Phase 5)**: Depends on US1 UI completion (needs pet cards in teaser)
- **Polish (Phase 6)**: Depends on all user stories being complete

### Within Phase 2 (Foundational)

```
T003 (UseCase) ──┬──► T010 (ViewModel) ──► T012, T013 (DI)
T004 (UseCase Test) ─┘
                    
T005, T006, T007, T008 (MVI classes) ──► T010 (ViewModel)
T009 (Reducer Test) ──┘
```

### Parallel Opportunities

**Phase 2 - All [P] tasks can run in parallel**:
- T003, T004 (UseCase + test)
- T005, T006, T007, T008, T009 (MVI classes + reducer test)

**Phase 3 (US1) - Unit tests can run in parallel**:
- T014, T015, T016 (ViewModel tests)
- T017, T018, T019 (UseCase tests)

**Phase 4 & 5 (US2, US3) - Can run in parallel after US1 UI is complete**:
- T030-T035 (US2) can run parallel with T036-T041 (US3)

---

## Parallel Example: Phase 2 Foundational

```bash
# Launch all MVI class creation in parallel:
Task T005: Create LostPetsTeaserUiState.kt
Task T006: Create LostPetsTeaserIntent.kt
Task T007: Create LostPetsTeaserEffect.kt
Task T008: Create LostPetsTeaserReducer.kt

# Launch use case + test in parallel:
Task T003: Create GetRecentAnimalsUseCase.kt
Task T004: Create GetRecentAnimalsUseCaseTest.kt

# After above complete, then:
Task T010: Create LostPetsTeaserViewModel.kt
Task T011: Create LostPetsTeaserViewModelTest.kt
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (verification)
2. Complete Phase 2: Foundational (MVI + UseCase + ViewModel + DI)
3. Complete Phase 3: User Story 1 (teaser display)
4. **STOP and VALIDATE**: Run tests, verify teaser displays on home page
5. Can demo/deploy with basic teaser functionality

### Full Feature Delivery

1. Setup + Foundational → Foundation ready
2. User Story 1 → Test independently → **MVP complete!**
3. User Story 2 → Test independently → "View All" works
4. User Story 3 → Test independently → Pet card clicks work
5. Polish → Run coverage, E2E, documentation
6. **Feature complete!**

---

## Summary

| Phase | Tasks | Parallel Tasks |
|-------|-------|----------------|
| Setup | 2 | 0 |
| Foundational | 11 | 7 |
| US1 (MVP) | 16 | 9 |
| US2 | 6 | 2 |
| US3 | 6 | 2 |
| Polish | 5 | 0 |
| **Total** | **46** | **20** |

**MVP Scope**: Phases 1-3 (US1) = 29 tasks
**Full Feature**: All phases = 46 tasks

---

## Notes

- [P] tasks = different files, no dependencies within same phase
- [Story] label maps task to specific user story for traceability
- Commit after each task or logical group
- Stop at any checkpoint to validate independently
- All UI composables follow two-layer pattern (stateful host + stateless content)
- Existing `AnimalCard` composable is reused - no new card component needed

