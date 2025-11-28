# Tasks: Android Backend API Integration

**Input**: Design documents from `/specs/029-android-api-integration/`
**Prerequisites**: plan.md ✓, spec.md ✓, research.md ✓, data-model.md ✓, contracts/ ✓

**Tests**: Test requirements for this feature:

**MANDATORY - Android Unit Tests**:
- Location: `/composeApp/src/androidUnitTest/`
- Framework: JUnit 6 + Kotlin Test + Turbine
- Coverage target: 80% for new code (mappers, repository)
- Scope: DTO mappers, repository implementation
- Run: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- Convention: MUST follow Given-When-Then structure with backtick test names

**NOT REQUIRED for this feature**:
- iOS tests (Android-only feature)
- Web tests (Android-only feature)
- Backend tests (not modifying server)
- E2E tests (existing E2E tests cover these screens)

**Organization**: Tasks are grouped by user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3, US4)
- Include exact file paths in descriptions

## Path Conventions

- Android: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/`
- Tests: `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/`

---

## Phase 1: Setup (Dependencies)

**Purpose**: Add Ktor HTTP client dependencies to Android project

- [X] T001 Add Ktor dependencies to `/composeApp/build.gradle.kts` (ktor-client-core, ktor-client-okhttp, ktor-client-content-negotiation, ktor-serialization-kotlinx-json, ktor-client-logging)
- [X] T002 [P] Add Ktor test dependency to `/composeApp/build.gradle.kts` (ktor-client-mock)
- [X] T003 [P] Add Kotlinx Serialization plugin to `/composeApp/build.gradle.kts` if not present
- [X] T004 Sync Gradle and verify dependencies resolve correctly

**Checkpoint**: Dependencies ready - run `./gradlew :composeApp:dependencies` to verify

---

## Phase 2: Foundational (Shared Infrastructure)

**Purpose**: Create shared DTOs, mappers, and HTTP client that ALL user stories depend on

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T005 [P] Create `AnnouncementDto.kt` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/data/api/dto/AnnouncementDto.kt` with @Serializable annotations (exclude managementPassword per FR-009)
- [X] T006 [P] Create `AnnouncementsResponseDto.kt` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/data/api/dto/AnnouncementsResponseDto.kt` for list wrapper
- [X] T007 Create `AnnouncementMapper.kt` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/data/mappers/AnnouncementMapper.kt` with `toDomain()` extension function
- [X] T008 [P] Create `AnnouncementMapperTest.kt` in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/data/mappers/AnnouncementMapperTest.kt` with Given-When-Then tests for: all field mappings, null optional field fallbacks (FR-007), unknown status coercion to MISSING (FR-010)
- [X] T009 Create `AnnouncementApiClient.kt` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/data/api/AnnouncementApiClient.kt` with Ktor HttpClient wrapper
- [X] T010 Update `DataModule.kt` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/di/DataModule.kt` to provide Ktor HttpClient and AnnouncementApiClient via Koin
- [X] T011 Add API base URL configuration to `/composeApp/build.gradle.kts` BuildConfig (10.0.2.2:3000 for debug)

**Checkpoint**: Foundation ready - DTOs compile, mapper tests pass, HttpClient configured

---

## Phase 3: User Story 1 - View List of Pet Announcements (Priority: P1) 🎯 MVP

**Goal**: Replace mock data with real API call for `GET /api/v1/announcements` in AnimalListScreen

**Independent Test**: Launch app → Animal list shows real data from backend server

### Tests for User Story 1 (MANDATORY) ✅

- [ ] T012 [P] [US1] Create `AnimalRepositoryImplTest.kt` in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/data/AnimalRepositoryImplTest.kt`
- [ ] T013 [US1] Add test `should return list of animals when API returns success` using Ktor MockEngine
- [ ] T014 [US1] Add test `should return empty list when API returns empty data array`
- [ ] T015 [US1] Add test `should throw exception when API returns error` (generic error handling per clarification)

### Implementation for User Story 1

- [ ] T016 [US1] Add `getAnnouncements(): AnnouncementsResponseDto` suspend function to `AnnouncementApiClient.kt`
- [ ] T017 [US1] Update `AnimalRepositoryImpl.kt` in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/data/AnimalRepositoryImpl.kt` - replace mock `getAnimals()` with API call using AnnouncementApiClient

**Checkpoint**: Animal list displays real backend data - US1 independently testable

---

## Phase 4: User Story 2 - View Pet Details (Priority: P1)

**Goal**: Replace mock data with real API call for `GET /api/v1/announcements/:id` in PetDetailsScreen

**Independent Test**: Tap any pet card → Details screen shows real data from backend server

### Tests for User Story 2 (MANDATORY) ✅

- [ ] T021 [P] [US2] Add test `should return animal when API returns announcement by ID` to `AnimalRepositoryImplTest.kt`
- [ ] T022 [US2] Add test `should throw exception when API returns 404 for unknown ID`

### Implementation for User Story 2

- [ ] T023 [US2] Add `getAnnouncementById(id: String): AnnouncementDto` suspend function to `AnnouncementApiClient.kt`
- [ ] T024 [US2] Update `AnimalRepositoryImpl.kt` - replace mock `getAnimalById()` with API call using AnnouncementApiClient

**Checkpoint**: Pet details displays real backend data - US2 independently testable

---

## Phase 5: User Story 3 - Handle Network Errors Gracefully (Priority: P2)

**Goal**: Verify error handling works correctly with real API failures

**Independent Test**: Disable network → App shows error message with retry → Enable network → Retry works

### Tests for User Story 3 (MANDATORY) ✅

- [ ] T026 [P] [US3] Add test `should throw exception when network is unavailable` to `AnimalRepositoryImplTest.kt` using MockEngine that throws IOException
- [ ] T027 [US3] Add test `should throw exception when API returns 5xx server error`
- [ ] T028 [US3] Add test `should throw exception when API returns 4xx client error` (same handling as 5xx per clarification)

### Implementation for User Story 3

- [ ] T029 [US3] Verify `AnimalRepositoryImpl` wraps Ktor exceptions appropriately (IOException, ClientRequestException, ServerResponseException)
- [ ] T030 [US3] Verify existing `AnimalListReducer` handles repository exceptions and sets error state (code review only - no changes expected)
- [ ] T031 [US3] Verify existing `PetDetailsReducer` handles repository exceptions and sets error state (code review only - no changes expected)

**Checkpoint**: Error states display correctly for all failure scenarios - US3 independently testable

---

## Phase 6: User Story 4 - Experience Responsive Loading States (Priority: P3)

**Goal**: Verify loading indicators display during API calls

**Independent Test**: Observe loading spinner during data fetch

### Verification for User Story 4

- [ ] T032 [US4] Verify `AnimalListUiState.isLoading` is set to true before API call in ViewModel
- [ ] T033 [US4] Verify `PetDetailsUiState.isLoading` is set to true before API call in ViewModel
- [ ] T034 [US4] Manual test: Launch app with slow network → Verify loading spinner appears

**Checkpoint**: Loading states display correctly - US4 verified

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Final cleanup and verification

- [ ] T035 [P] Add KDoc to `AnnouncementApiClient.kt` public methods
- [ ] T036 [P] Add KDoc to `AnnouncementMapper.kt` for complex mapping rules
- [ ] T037 Remove all `MockAnimalData` references and delay() calls from `AnimalRepositoryImpl.kt`
- [ ] T038 Run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` and verify 80% coverage for new code
- [ ] T039 Run `./gradlew :composeApp:assembleDebug` and verify app builds successfully
- [ ] T040 Manual smoke test: Launch app → View list → Tap card → View details → Back → Verify no regressions
- [ ] T041 Verify quickstart.md instructions work for new developers

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **US1 (Phase 3)**: Depends on Foundational - provides MVP value
- **US2 (Phase 4)**: Depends on Foundational - can run parallel to US1 after T016
- **US3 (Phase 5)**: Depends on US1 and US2 implementation
- **US4 (Phase 6)**: Depends on US1 and US2 implementation
- **Polish (Phase 7)**: Depends on all user stories complete

### Within Each User Story

- Tests SHOULD be written first (TDD approach)
- API client methods before repository updates
- Repository updates before manual verification

### Parallel Opportunities

- T002, T003 can run parallel to T001
- T005, T006 can run parallel (separate DTO files)
- T008 can run parallel to T007 (test file vs implementation)
- T012 can start as soon as Phase 2 completes
- US1 and US2 can be worked on in parallel by different developers after Phase 2

---

## Parallel Example: Foundational Phase

```bash
# Launch DTOs in parallel:
Task T005: "Create AnnouncementDto.kt"
Task T006: "Create AnnouncementsResponseDto.kt"

# Then mapper + tests in parallel:
Task T007: "Create AnnouncementMapper.kt"
Task T008: "Create AnnouncementMapperTest.kt"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (dependencies)
2. Complete Phase 2: Foundational (DTOs, mappers, client)
3. Complete Phase 3: User Story 1 (list API)
4. **STOP and VALIDATE**: Animal list shows real data
5. Demo MVP to stakeholders

### Incremental Delivery

1. Setup + Foundational → Infrastructure ready
2. Add US1 (list) → Test independently → **MVP Ready!**
3. Add US2 (details) → Test independently → Full feature
4. Add US3 (errors) → Verify error handling
5. Add US4 (loading) → Verify loading states
6. Polish → Production ready

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story
- Existing MVI layer (ViewModels, Reducers, Screens) unchanged
- Only `AnimalRepositoryImpl` implementation changes
- `MockAnimalData` can be kept for unit tests (FakeAnimalRepository)
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently

