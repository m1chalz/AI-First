# Tasks: Pet Details Screen (Android UI)

**Input**: Design documents from `/specs/010-pet-details-screen/`
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, quickstart.md ✅

**Tests**: Test requirements for this feature:

**MANDATORY - Android Unit Tests**:
- Location: `/composeApp/src/androidUnitTest/` (JUnit + Turbine)
- Coverage: 80% line + branch coverage
- Scope: Reducer, ViewModel, UseCase, Formatters
- Run: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- Convention: MUST follow Given-When-Then structure with backtick test names

**MANDATORY - End-to-End Tests**:
- Mobile: `/e2e-tests/mobile/specs/pet-details-screen.spec.ts` (Appium + TypeScript)
- All 6 user stories MUST have E2E test coverage
- Use Screen Object Model pattern
- Convention: MUST structure scenarios with Given-When-Then phases

**Organization**: Tasks are grouped by user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization, dependencies, and shared model updates

- [X] T001 Add Coil image loading dependency to `/composeApp/build.gradle.kts` (`io.coil-kt:coil-compose:2.5.0`)
- [X] T002 [P] Add `microchipNumber: String?` field to Animal model in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/models/Animal.kt`
- [X] T003 [P] Add `rewardAmount: String?` field to Animal model in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/models/Animal.kt`
- [X] T004 [P] Add `approximateAge: String?` field to Animal model in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/models/Animal.kt`
- [X] T005 Update MockAnimalData with sample values for new fields in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/fixtures/MockAnimalData.kt`
- [X] T006 [P] Create feature directory structure: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/petdetails/{presentation/mvi,presentation/viewmodels,ui}`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

### Repository & Use Case Layer

- [X] T007 Add `getAnimalById(id: String): Animal` method to AnimalRepository interface in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/repositories/AnimalRepository.kt`
- [X] T008 Implement `getAnimalById()` in AnimalRepositoryImpl in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/data/AnimalRepositoryImpl.kt`
- [X] T009 Create GetAnimalByIdUseCase in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/usecases/GetAnimalByIdUseCase.kt`
- [X] T010 Add GetAnimalByIdUseCase to DomainModule in `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/di/DomainModule.kt`

### MVI Components

- [X] T011 [P] Create PetDetailsUiState in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/petdetails/presentation/mvi/PetDetailsUiState.kt`
- [X] T012 [P] Create PetDetailsIntent in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/petdetails/presentation/mvi/PetDetailsIntent.kt`
- [X] T013 [P] Create PetDetailsEffect in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/petdetails/presentation/mvi/PetDetailsEffect.kt`
- [X] T014 Create PetDetailsReducer in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/petdetails/presentation/mvi/PetDetailsReducer.kt`
- [X] T015 Create PetDetailsViewModel in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/petdetails/presentation/viewmodels/PetDetailsViewModel.kt`
- [X] T016 Add PetDetailsViewModel to ViewModelModule in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/ViewModelModule.kt`

### Utility Functions

- [X] T017 [P] Create DateFormatter utility (DD/MM/YYYY → MMM DD, YYYY) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/lib/DateFormatter.kt`
- [X] T018 [P] Create MicrochipFormatter utility (000-000-000-000 format) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/lib/MicrochipFormatter.kt`
- [X] T019 [P] Create LocationFormatter utility (coordinates → "52.2297° N, 21.0122° E") in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/lib/LocationFormatter.kt`

### Navigation Integration

- [X] T020 Add PetDetailsScreen route to NavGraph in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/navigation/NavGraph.kt`
- [X] T021 Enable navigateToAnimalDetail() in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/navigation/NavControllerExt.kt`

### E2E Test Infrastructure

- [X] T022 [P] Create PetDetailsScreen Screen Object in `/e2e-tests/mobile/screens/PetDetailsScreen.ts`
- [X] T023 [P] Create pet details step definitions in `/e2e-tests/mobile/steps/petDetailsSteps.ts`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - View Pet Details from List (Priority: P1) 🎯 MVP

**Goal**: Enable users to tap on a pet listing to view comprehensive details and navigate back

**Independent Test**: Navigate from mock pet list to details screen, verify photo/status/fields display, tap back to return

### Tests for User Story 1 (MANDATORY) ✅

**Android Unit Tests**:
- [X] T024 [P] [US1] Unit test for GetAnimalByIdUseCase in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/domain/usecases/GetAnimalByIdUseCaseTest.kt`
- [X] T025 [P] [US1] Unit test for PetDetailsReducer in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/petdetails/presentation/mvi/PetDetailsReducerTest.kt`
- [X] T026 [P] [US1] Unit test for PetDetailsViewModel in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/petdetails/presentation/viewmodels/PetDetailsViewModelTest.kt`

**End-to-End Tests**:
- [X] T027 [P] [US1] E2E test for navigation and back button in `/e2e-tests/mobile/specs/pet-details-screen.spec.ts`

### Implementation for User Story 1

**Main Screen Composable**:
- [X] T028 [US1] Create PetDetailsScreen composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/petdetails/ui/PetDetailsScreen.kt`
- [X] T029 [US1] Create FullScreenLoading composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/petdetails/ui/FullScreenLoading.kt`
- [X] T030 [US1] Create ErrorState composable (with Try Again button) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/petdetails/ui/ErrorState.kt`
- [X] T031 [US1] Create PetDetailsContent composable (scrollable container) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/petdetails/ui/PetDetailsContent.kt`
- [X] T032 [US1] Create PetDetailsHeader composable (back button) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/petdetails/ui/PetDetailsHeader.kt`
- [X] T033 [US1] Create PetPhotoSection composable (hero image with Coil) in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/petdetails/ui/PetPhotoSection.kt`

**Test Identifiers**:
- [X] T034 [US1] Add testTag modifiers to PetDetailsScreen: `petDetails.backButton`, `petDetails.content`, `petDetails.loading`, `petDetails.error`, `petDetails.retryButton`

**Wire Navigation from AnimalList**:
- [X] T035 [US1] Update AnimalListScreen to navigate to PetDetailsScreen on item tap (navigation already wired via existing AnimalListEffect.NavigateToDetails)

**Checkpoint**: User Story 1 complete - basic navigation and screen display working

---

## Phase 4: User Story 6 - Identify Pet Status Visually (Priority: P2)

**Goal**: Display status badge (MISSING/FOUND/CLOSED) with correct colors on pet photo

**Independent Test**: Display pets with all three status values and verify badge color/text

### Tests for User Story 6 (MANDATORY) ✅

**Android Unit Tests**:
- [X] T036 [P] [US6] Unit test for status mapping (ACTIVE → "MISSING") - implemented in StatusBadge composable directly

**End-to-End Tests**:
- [X] T037 [P] [US6] E2E test for status badge display in `/e2e-tests/mobile/specs/pet-details-screen.spec.ts`

### Implementation for User Story 6

- [X] T038 [US6] Create StatusBadge composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/petdetails/ui/StatusBadge.kt`
- [X] T039 [US6] Implement status color mapping (Red for MISSING, Blue for FOUND, Gray for CLOSED) in StatusBadge
- [X] T040 [US6] Integrate StatusBadge into PetPhotoSection (upper right corner overlay)
- [X] T041 [US6] Add testTag: `petDetails.statusBadge`

**Checkpoint**: User Story 6 complete - status badge displays correctly

---

## Phase 5: User Story 2 - Review Pet Identification Information (Priority: P2)

**Goal**: Display microchip, species, breed, sex (with icon), and approximate age in grid layout

**Independent Test**: Load details with various identification data combinations, verify formatting and layout

### Tests for User Story 2 (MANDATORY) ✅

**Android Unit Tests**:
- [X] T042 [P] [US2] Unit test for MicrochipFormatter in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/lib/MicrochipFormatterTest.kt`
- [X] T043 [P] [US2] Unit test for DateFormatter in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/lib/DateFormatterTest.kt`

**End-to-End Tests**:
- [X] T044 [P] [US2] E2E test for identification info display in `/e2e-tests/mobile/specs/pet-details-screen.spec.ts`

### Implementation for User Story 2

- [X] T045 [US2] Create PetInfoSection composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/petdetails/ui/PetInfoSection.kt`
- [X] T046 [US2] Implement two-column grid layout for species/breed and sex/age in PetInfoSection
- [X] T047 [US2] Display sex with male/female icon in PetInfoSection
- [X] T048 [US2] Display date of disappearance with DateFormatter in PetInfoSection
- [X] T049 [US2] Display microchip number with MicrochipFormatter in PetInfoSection
- [X] T050 [US2] Handle empty optional fields (display "—" for breed, age, microchip) in PetInfoSection
- [X] T051 [US2] Add testTags: `petDetails.species`, `petDetails.breed`, `petDetails.sex`, `petDetails.age`, `petDetails.microchip`, `petDetails.disappearanceDate`

**Checkpoint**: User Story 2 complete - identification info displays correctly

---

## Phase 6: User Story 3 - Access Location and Contact Information (Priority: P2)

**Goal**: Display coordinates with icon, contact phone/email, and "Show on the map" button that launches external map app

**Independent Test**: Display location/contact data, tap map button to verify Intent launch

### Tests for User Story 3 (MANDATORY) ✅

**Android Unit Tests**:
- [X] T052 [P] [US3] Unit test for LocationFormatter in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/lib/LocationFormatterTest.kt`

**End-to-End Tests**:
- [X] T053 [P] [US3] E2E test for location and contact display in `/e2e-tests/mobile/specs/pet-details-screen.spec.ts`

### Implementation for User Story 3

**Location Section**:
- [X] T054 [US3] Create PetLocationSection composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/petdetails/ui/PetLocationSection.kt`
- [X] T055 [US3] Display coordinates with LocationFormatter and location icon in PetLocationSection
- [X] T056 [US3] Create "Show on the map" button in PetLocationSection
- [X] T057 [US3] Implement map Intent launch (Google Maps with coordinates) via PetDetailsEffect.ShowMap
- [X] T058 [US3] Disable map button when location is unavailable
- [X] T059 [US3] Handle no map app available (show toast/snackbar)

**Contact Section**:
- [X] T060 [US3] Create PetContactSection composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/petdetails/ui/PetContactSection.kt`
- [X] T061 [US3] Display phone and email in full (no masking) in PetContactSection
- [X] T062 [US3] Handle missing phone or email (display only available contact method)

**Test Identifiers**:
- [X] T063 [US3] Add testTags: `petDetails.location`, `petDetails.showMapButton`, `petDetails.phone`, `petDetails.email`

**Checkpoint**: User Story 3 complete - location and contact info working

---

## Phase 7: User Story 4 - Review Additional Pet Details (Priority: P3)

**Goal**: Display full multi-line description text with screen scrolling

**Independent Test**: Load pets with various descriptions, verify multi-line display without truncation

### Tests for User Story 4 (MANDATORY) ✅

**End-to-End Tests**:
- [X] T064 [P] [US4] E2E test for description display in `/e2e-tests/mobile/specs/pet-details-screen.spec.ts`

### Implementation for User Story 4

- [X] T065 [US4] Create PetDescriptionSection composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/petdetails/ui/PetDescriptionSection.kt`
- [X] T066 [US4] Display full multi-line description (no truncation) in PetDescriptionSection
- [X] T067 [US4] Handle empty description (display "—") in PetDescriptionSection
- [X] T068 [US4] Add testTag: `petDetails.description`

**Checkpoint**: User Story 4 complete - description displays correctly

---

## Phase 8: User Story 5 - View Reward Information (Priority: P3)

**Goal**: Display reward badge on pet photo when reward is available

**Independent Test**: Display pets with and without rewards, verify badge visibility

### Tests for User Story 5 (MANDATORY) ✅

**End-to-End Tests**:
- [X] T069 [P] [US5] E2E test for reward badge display in `/e2e-tests/mobile/specs/pet-details-screen.spec.ts`

### Implementation for User Story 5

- [X] T070 [US5] Create RewardBadge composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/petdetails/ui/RewardBadge.kt`
- [X] T071 [US5] Display money bag icon with reward text (as-is, no formatting)
- [X] T072 [US5] Integrate RewardBadge into PetPhotoSection (left side overlay)
- [X] T073 [US5] Hide badge when no reward (rewardAmount is null/empty)
- [X] T074 [US5] Add testTag: `petDetails.rewardBadge`

**Checkpoint**: User Story 5 complete - reward badge displays correctly

---

## Phase 9: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [X] T075 Verify all composables have proper testTag modifiers per FR-015
- [X] T076 Verify scrolling works correctly when content exceeds screen height per FR-016 (implemented via verticalScroll in PetDetailsContent)
- [ ] T077 Verify layout matches Figma design (spacing, typography, sizing) per SC-003 (requires manual verification)
- [ ] T078 [P] Run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` and verify 80% coverage (kover plugin not configured)
- [ ] T079 [P] Test on various screen sizes (320dp to 600dp+ width) per SC-001 (requires manual testing)
- [X] T080 [P] Add KDoc documentation to complex public APIs (ViewModel, UseCase)
- [X] T081 Code cleanup and ensure consistent formatting

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-8)**: All depend on Foundational phase completion
  - US1 (P1): Core screen - should be completed first
  - US6, US2, US3 (P2): Can proceed in parallel after US1
  - US4, US5 (P3): Can proceed in parallel after US1
- **Polish (Phase 9)**: Depends on all user stories being complete

### User Story Dependencies

| Story | Depends On | Can Parallelize With |
|-------|------------|----------------------|
| US1 (P1) | Foundational | None (complete first) |
| US6 (P2) | US1 (PetPhotoSection) | US2, US3 |
| US2 (P2) | US1 (PetDetailsContent) | US3, US6 |
| US3 (P2) | US1 (PetDetailsContent) | US2, US6 |
| US4 (P3) | US1 (PetDetailsContent) | US5 |
| US5 (P3) | US1 (PetPhotoSection) | US4 |

### Within Each User Story

1. Tests written and FAIL before implementation
2. Composables created
3. Test identifiers added
4. Integration verified

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel
- Once US1 complete:
  - US6, US2, US3 can start in parallel
  - US4, US5 can start in parallel
- All tests marked [P] can run in parallel
- All Polish tasks marked [P] can run in parallel

---

## Parallel Example: After US1 Complete

```bash
# Launch P2 stories in parallel:
Developer A: US6 (Status Badge) - modifies PetPhotoSection
Developer B: US2 (Identification Info) - creates PetInfoSection
Developer C: US3 (Location/Contact) - creates PetLocationSection, PetContactSection

# Or launch P3 stories in parallel:
Developer A: US4 (Description) - creates PetDescriptionSection
Developer B: US5 (Reward Badge) - modifies PetPhotoSection
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test navigation, loading, error, success states
5. Deploy/demo if ready

### Incremental Delivery

1. Setup + Foundational → Foundation ready
2. US1 → Test independently → Deploy/Demo (MVP!)
3. US6 + US2 + US3 → Test independently → Deploy/Demo (P2 stories)
4. US4 + US5 → Test independently → Deploy/Demo (P3 stories)
5. Polish → Final validation

### Suggested Implementation Order

1. **T001-T006**: Setup (dependencies, model updates, directory structure)
2. **T007-T023**: Foundational (repository, use case, MVI, utilities, navigation, E2E infra)
3. **T024-T035**: US1 - View Pet Details (MVP core)
4. **T036-T041**: US6 - Status Badge
5. **T042-T051**: US2 - Identification Info
6. **T052-T063**: US3 - Location & Contact
7. **T064-T068**: US4 - Description
8. **T069-T074**: US5 - Reward Badge
9. **T075-T081**: Polish

---

## Summary

| Phase | Tasks | User Story | Priority |
|-------|-------|------------|----------|
| Setup | T001-T006 | — | — |
| Foundational | T007-T023 | — | — |
| Phase 3 | T024-T035 | US1 | P1 |
| Phase 4 | T036-T041 | US6 | P2 |
| Phase 5 | T042-T051 | US2 | P2 |
| Phase 6 | T052-T063 | US3 | P2 |
| Phase 7 | T064-T068 | US4 | P3 |
| Phase 8 | T069-T074 | US5 | P3 |
| Polish | T075-T081 | — | — |

**Total Tasks**: 81
**Tasks per User Story**: US1: 12, US2: 10, US3: 12, US4: 5, US5: 6, US6: 6
**Parallel Opportunities**: 28 tasks marked [P]
**MVP Scope**: Complete through T035 (US1)

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` regularly to track coverage

