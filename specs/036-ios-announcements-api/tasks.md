# Tasks: iOS Announcements API Integration

**Feature Branch**: `036-ios-announcements-api`  
**Input**: Design documents from `/specs/036-ios-announcements-api/`  
**Prerequisites**: plan.md, spec.md, data-model.md, contracts/, research.md, quickstart.md

**Tests**: This feature includes unit tests and E2E tests (80% coverage target for iOS platform)

**MANDATORY - iOS Unit Tests**:
- Location: `/iosApp/iosAppTests/` (XCTest with async/await)
- Scope: HTTP repository, ViewModel integration tests
- Run: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- Coverage target: 80% line + branch coverage
- Convention: MUST follow Given-When-Then structure with descriptive test names

**MANDATORY - End-to-End Tests**:
- Location: `/e2e-tests/mobile/specs/ios-announcements-api.spec.ts`
- Framework: Appium + WebdriverIO + TypeScript
- All user stories MUST have E2E test coverage
- Use Screen Object Model pattern
- Convention: MUST structure scenarios with Given-When-Then phases

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `- [ ] [ID] [P?] [Story?] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic iOS configuration

- [ ] T001 Verify backend server endpoints are available at http://localhost:3000/api/v1/announcements
- [ ] T002 [P] Create API configuration file in `/iosApp/iosApp/Configuration/APIConfig.swift`
- [ ] T003 [P] Add iOS ATS exception to `/iosApp/iosApp/Info.plist` for localhost HTTP connections

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T004 Create RepositoryError enum in `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift` (for error handling across all stories)
- [ ] T005 [P] Create JSONDecoder extension with custom date decoding strategy in `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift`
- [ ] T006 [P] Verify existing `AnimalRepositoryProtocol` in `/iosApp/iosApp/Domain/Repositories/AnimalRepositoryProtocol.swift` supports getAnimals and getAnimalDetails methods
- [ ] T007 [P] Verify existing domain models (Animal, PetDetails, Species, AnimalStatus, Coordinate) in `/iosApp/iosApp/Domain/Models/` are compatible with API contracts

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Display Real Announcements on Animal List (Priority: P1) 🎯 MVP

**Goal**: Replace mock data with real backend API calls for Animal List screen. Users see actual pet announcements from the database with optional location filtering.

**Independent Test**: Launch Animal List screen and verify displayed pets match backend database content. Can test by adding/removing pets in backend database and seeing changes reflected in iOS app.

### Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**iOS Unit Tests - Repository**:
- [ ] T008 [P] [US1] Create MockURLProtocol class in `/iosApp/iosAppTests/Data/Repositories/AnimalRepositoryTests.swift` for URLSession mocking
- [ ] T009 [P] [US1] Test: getAnimals with valid JSON response should return parsed Animal array in `/iosApp/iosAppTests/Data/Repositories/AnimalRepositoryTests.swift`
- [ ] T010 [P] [US1] Test: getAnimals with location parameters should include lat/lng query params in URL in `/iosApp/iosAppTests/Data/Repositories/AnimalRepositoryTests.swift`
- [ ] T011 [P] [US1] Test: getAnimals with HTTP 500 error should throw RepositoryError.httpError in `/iosApp/iosAppTests/Data/Repositories/AnimalRepositoryTests.swift`
- [ ] T012 [P] [US1] Test: getAnimals with invalid JSON should throw RepositoryError.decodingFailed in `/iosApp/iosAppTests/Data/Repositories/AnimalRepositoryTests.swift`
- [ ] T013 [P] [US1] Test: getAnimals with invalid species enum should skip invalid items (compactMap behavior) in `/iosApp/iosAppTests/Data/Repositories/AnimalRepositoryTests.swift`
- [ ] T014 [P] [US1] Test: getAnimals with duplicate IDs should deduplicate and log warning in `/iosApp/iosAppTests/Data/Repositories/AnimalRepositoryTests.swift`
- [ ] T015 [P] [US1] Test: getAnimals with empty list should return empty array in `/iosApp/iosAppTests/Data/Repositories/AnimalRepositoryTests.swift`

**iOS Unit Tests - ViewModel Integration**:
- [ ] T016 [P] [US1] Test: AnimalListViewModel loadAnimals should update animals publisher with API data in `/iosApp/iosAppTests/Features/AnimalList/ViewModels/AnimalListViewModelTests.swift`
- [ ] T017 [P] [US1] Test: AnimalListViewModel with location permissions should pass coordinates to repository in `/iosApp/iosAppTests/Features/AnimalList/ViewModels/AnimalListViewModelTests.swift`
- [ ] T018 [P] [US1] Test: AnimalListViewModel without location permissions should call repository without coordinates in `/iosApp/iosAppTests/Features/AnimalList/ViewModels/AnimalListViewModelTests.swift`
- [ ] T019 [P] [US1] Test: AnimalListViewModel with repository error should set error state in `/iosApp/iosAppTests/Features/AnimalList/ViewModels/AnimalListViewModelTests.swift`

**End-to-End Tests**:
- [ ] T020 [P] [US1] E2E test: Display real announcements from backend on Animal List screen in `/e2e-tests/mobile/specs/ios-announcements-api.spec.ts`
- [ ] T021 [P] [US1] E2E test: Display empty state when backend has no announcements in `/e2e-tests/mobile/specs/ios-announcements-api.spec.ts`
- [ ] T022 [P] [US1] E2E test: Display error message when backend is unavailable in `/e2e-tests/mobile/specs/ios-announcements-api.spec.ts`

### Implementation for User Story 1

**iOS Implementation**:
- [ ] T023 [P] [US1] Create private AnnouncementsListResponse DTO struct in `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift`
- [ ] T024 [P] [US1] Create private AnnouncementDTO struct with CodingKeys (locationLatitude/locationLongitude, phone, email fields) in `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift`
- [ ] T025 [P] [US1] Create failable Animal initializer `init?(from: AnnouncementDTO)` with enum validation (lowercase conversion), date parsing, and correct field mapping in `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift`
- [ ] T026 [US1] Implement AnimalRepository class conforming to AnimalRepositoryProtocol in `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift`
- [ ] T027 [US1] Implement getAnimals method with URLSession, location query params, JSON decoding, compactMap for invalid items, deduplication in `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift`
- [ ] T028 [US1] Add error handling for HTTP errors, network errors, decoding errors with print logging in `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift`
- [ ] T029 [US1] Update ServiceContainer to provide AnimalRepository (HTTP implementation) instead of FakeAnimalRepository in `/iosApp/iosApp/DI/ServiceContainer.swift`
- [ ] T030 [P] [US1] Add SwiftDoc documentation to non-obvious repository methods in `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift`

**Verification**:
- [ ] T031 [US1] Run unit tests: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- [ ] T032 [US1] Verify 80% test coverage for AnimalRepository in Xcode coverage report
- [ ] T033 [US1] Manual test: Start backend, verify Animal List displays real data matching backend database
- [ ] T034 [US1] Manual test: Grant location permissions, verify location query params sent to backend (check backend logs)
- [ ] T035 [US1] Manual test: Deny location permissions, verify all announcements fetched without location filtering
- [ ] T036 [US1] Manual test: Stop backend, verify error message displayed in Animal List

**Checkpoint**: At this point, User Story 1 (MVP) should be fully functional and testable independently

---

## Phase 4: User Story 2 - Display Real Pet Details (Priority: P2)

**Goal**: Replace mock data with real backend API calls for Pet Details screen. Users see complete pet information including contact details, description, microchip, and reward when tapping an animal card.

**Independent Test**: Navigate to Pet Details screen with a known pet ID from backend and verify all fields (including optional ones) match the database record. Can test with pets that have different optional field combinations.

### Tests for User Story 2 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**iOS Unit Tests - Repository**:
- [ ] T037 [P] [US2] Test: getAnimalDetails with valid JSON response should return PetDetails with all fields in `/iosApp/iosAppTests/Data/Repositories/AnimalRepositoryTests.swift`
- [ ] T038 [P] [US2] Test: getAnimalDetails with HTTP 404 error should throw RepositoryError.notFound in `/iosApp/iosAppTests/Data/Repositories/AnimalRepositoryTests.swift`
- [ ] T039 [P] [US2] Test: getAnimalDetails with HTTP 500 error should throw RepositoryError.httpError in `/iosApp/iosAppTests/Data/Repositories/AnimalRepositoryTests.swift`
- [ ] T040 [P] [US2] Test: getAnimalDetails with invalid JSON should throw RepositoryError.decodingFailed in `/iosApp/iosAppTests/Data/Repositories/AnimalRepositoryTests.swift`
- [ ] T041 [P] [US2] Test: getAnimalDetails with invalid species should throw RepositoryError.invalidData in `/iosApp/iosAppTests/Data/Repositories/AnimalRepositoryTests.swift`
- [ ] T042 [P] [US2] Test: getAnimalDetails with missing optional fields should return PetDetails with nil values in `/iosApp/iosAppTests/Data/Repositories/AnimalRepositoryTests.swift`
- [ ] T043a [P] [US2] Test: getAnimalDetails with updatedAt in custom format "YYYY-MM-DD HH:MM:SS" should parse correctly (backend inconsistency) in `/iosApp/iosAppTests/Data/Repositories/AnimalRepositoryTests.swift`

**iOS Unit Tests - ViewModel Integration**:
- [ ] T044 [P] [US2] Test: PetDetailsViewModel loadDetails should update petDetails publisher with API data in `/iosApp/iosAppTests/Features/PetDetails/ViewModels/PetDetailsViewModelTests.swift`
- [ ] T045 [P] [US2] Test: PetDetailsViewModel with 404 error should set appropriate error state in `/iosApp/iosAppTests/Features/PetDetails/ViewModels/PetDetailsViewModelTests.swift`
- [ ] T046 [P] [US2] Test: PetDetailsViewModel with network error should set appropriate error state in `/iosApp/iosAppTests/Features/PetDetails/ViewModels/PetDetailsViewModelTests.swift`

**End-to-End Tests**:
- [ ] T047 [P] [US2] E2E test: Display pet details when tapping animal card from list in `/e2e-tests/mobile/specs/ios-announcements-api.spec.ts`
- [ ] T048 [P] [US2] E2E test: Display all optional fields correctly when present in `/e2e-tests/mobile/specs/ios-announcements-api.spec.ts`
- [ ] T049 [P] [US2] E2E test: Display placeholders for missing optional fields in `/e2e-tests/mobile/specs/ios-announcements-api.spec.ts`

### Implementation for User Story 2

**iOS Implementation**:
- [ ] T050 [P] [US2] Create private PetDetailsDTO struct with CodingKeys (locationLatitude/locationLongitude, phone, email fields, reward as String) in `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift`
- [ ] T051 [P] [US2] Create failable PetDetails initializer `init?(from: PetDetailsDTO)` with enum validation (lowercase conversion), date parsing with fallback (lastSeenDate ISO 8601, createdAt ISO 8601, updatedAt custom "YYYY-MM-DD HH:MM:SS"), reward string parsing ("500 PLN" → 500.0), and correct field mapping in `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift`
- [ ] T052 [US2] Implement getAnimalDetails method with URLSession, path parameter, JSON decoding, error handling (404→notFound, 500→httpError) in `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift`
- [ ] T053 [US2] Add invalidData error handling for failed DTO→Domain conversion in getAnimalDetails in `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift`
- [ ] T054 [P] [US2] Add SwiftDoc documentation to getAnimalDetails method in `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift`

**Verification**:
- [ ] T055 [US2] Run unit tests and verify all US2 tests pass
- [ ] T056 [US2] Verify 80% test coverage maintained for repository and ViewModels
- [ ] T057 [US2] Manual test: Tap animal card from list, verify Pet Details screen loads with all fields
- [ ] T058 [US2] Manual test: Verify optional fields (breed, microchip, email, reward) display correctly when present
- [ ] T059 [US2] Manual test: Verify optional fields show "—" or appropriate placeholder when missing
- [ ] T060 [US2] Manual test: Verify 404 error handling by requesting non-existent pet ID

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - Refresh Data After Creating Announcement (Priority: P3)

**Goal**: Automatically refresh Animal List when user returns from creating a new announcement. Provides immediate feedback confirming the submission was successful.

**Independent Test**: Complete the "Report Missing Pet" flow, submit announcement, return to Animal List, verify new announcement appears in the list. Can test by checking announcement count before and after submission.

### Tests for User Story 3 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**iOS Unit Tests - ViewModel Integration**:
- [ ] T061 [P] [US3] Test: AnimalListViewModel onAppear should trigger automatic refresh when returning from create flow in `/iosApp/iosAppTests/Features/AnimalList/ViewModels/AnimalListViewModelTests.swift`
- [ ] T062 [P] [US3] Test: AnimalListViewModel refresh should cancel previous task if already loading in `/iosApp/iosAppTests/Features/AnimalList/ViewModels/AnimalListViewModelTests.swift`

**End-to-End Tests**:
- [ ] T063 [P] [US3] E2E test: New announcement appears in Animal List after successful submission in `/e2e-tests/mobile/specs/ios-announcements-api.spec.ts`
- [ ] T064 [P] [US3] E2E test: Multiple submissions result in all announcements visible in list in `/e2e-tests/mobile/specs/ios-announcements-api.spec.ts`

### Implementation for User Story 3

**iOS Implementation**:
- [ ] T065 [US3] Add task cancellation logic to AnimalListViewModel (store Task reference, cancel on new loadAnimals call) in `/iosApp/iosApp/Features/AnimalList/ViewModels/AnimalListViewModel.swift`
- [ ] T066 [US3] Update AnimalListCoordinator to trigger ViewModel refresh when returning from create announcement flow in `/iosApp/iosApp/Coordinators/AnimalListCoordinator.swift`
- [ ] T067 [US3] Add Task.checkCancellation() check in repository if needed for proper cleanup in `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift`

**Verification**:
- [ ] T068 [US3] Run unit tests and verify all US3 tests pass
- [ ] T069 [US3] Manual test: Submit new announcement, return to Animal List, verify announcement appears
- [ ] T070 [US3] Manual test: Rapidly switch between screens, verify no stale data displayed (task cancellation works)
- [ ] T071 [US3] Manual test: Submit announcement outside location filter radius, verify it may not appear (correct behavior)

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories and final validation

- [ ] T072 [P] Run full test suite and verify all tests pass: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- [ ] T073 [P] Verify 80% test coverage target achieved for iOS platform (Xcode coverage report)
- [ ] T074 [P] Run E2E tests and verify all scenarios pass: `npm run test:mobile:ios` from repo root
- [ ] T075 [P] Code review: Verify all SwiftDoc comments are concise and high-level (1-3 sentences)
- [ ] T076 [P] Code review: Verify error messages are user-friendly (no technical details exposed)
- [ ] T077 [P] Code review: Verify print statements used for logging (no analytics tracking)
- [ ] T078 [P] Performance test: Verify Animal List loads within 2 seconds under normal network conditions
- [ ] T079 [P] Performance test: Verify Pet Details loads within 1.5 seconds under normal network conditions
- [ ] T080 [P] Performance test: Test with Network Link Conditioner (Very Bad Network) to verify timeout handling
- [ ] T081 [P] Verify quickstart.md instructions are accurate by following step-by-step
- [ ] T082 Update IMPLEMENTATION-COMPLETE document in `/specs/036-ios-announcements-api/` with completion summary
- [ ] T083 Final manual testing: Run through all acceptance scenarios from spec.md

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational phase completion - MVP
- **User Story 2 (Phase 4)**: Depends on Foundational phase completion - Independent of US1 (shares same repository implementation)
- **User Story 3 (Phase 5)**: Depends on Foundational phase completion - Integrates with US1 but testable independently
- **Polish (Phase 6)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories - MVP CANDIDATE
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Shares AnimalRepository with US1 but independently testable with different endpoint
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Enhances US1 (refresh behavior) but US1 works without it

### Within Each User Story

- Tests MUST be written and FAIL before implementation (RED-GREEN-REFACTOR for unit tests)
- DTOs before domain model extensions
- Domain model extensions before repository implementation
- Repository implementation before DI wiring
- DI wiring before ViewModel updates
- All implementation before E2E tests execution
- Story complete and verified before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel (T002, T003)
- All Foundational tasks marked [P] can run in parallel (T005, T006, T007)
- Once Foundational phase completes:
  - User Story 2 can start in parallel with User Story 1 (different endpoint, same repository class)
  - User Story 3 requires US1 ViewModel to exist but can be developed concurrently
- All unit tests for a user story marked [P] can run in parallel
- E2E tests for different user stories marked [P] can run in parallel
- DTO structs within a story marked [P] can be written in parallel (T023, T024 for US1)
- Documentation tasks marked [P] can run in parallel with other tasks in same phase

---

## Parallel Example: User Story 1

After Foundational phase completes, these US1 tasks can run in parallel:

**Testing Phase** (all in parallel):
```
T008 [P] [US1] Create MockURLProtocol
T009 [P] [US1] Test getAnimals with valid JSON
T010 [P] [US1] Test getAnimals with location params
T011 [P] [US1] Test getAnimals with HTTP 500
T012 [P] [US1] Test getAnimals with invalid JSON
T013 [P] [US1] Test getAnimals with invalid species
T014 [P] [US1] Test getAnimals with duplicates
T015 [P] [US1] Test getAnimals with empty list
T016 [P] [US1] Test ViewModel loadAnimals
T017 [P] [US1] Test ViewModel with location
T018 [P] [US1] Test ViewModel without location
T019 [P] [US1] Test ViewModel with error
T020 [P] [US1] E2E test display announcements
T021 [P] [US1] E2E test empty state
T022 [P] [US1] E2E test error message
```

**Implementation Phase** (some in parallel):
```
T023 [P] [US1] Create AnnouncementsListResponse DTO  ← parallel with T024, T025
T024 [P] [US1] Create AnnouncementDTO                ← parallel with T023, T025
T025 [P] [US1] Create Animal failable init           ← parallel with T023, T024
T026 [US1] Implement AnimalRepository                ← sequential (needs DTOs)
T027 [US1] Implement getAnimals method               ← sequential (needs repository)
T028 [US1] Add error handling                        ← sequential (needs getAnimals)
T029 [US1] Update ServiceContainer                   ← sequential (needs complete repository)
T030 [P] [US1] Add SwiftDoc documentation            ← parallel with verification tasks
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (T001-T003)
2. Complete Phase 2: Foundational (T004-T007) - CRITICAL - blocks all stories
3. Complete Phase 3: User Story 1 (T008-T036)
4. **STOP and VALIDATE**: Test User Story 1 independently
   - Run unit tests and verify 80% coverage
   - Run E2E tests for US1 scenarios
   - Manual testing checklist (see quickstart.md)
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
   - Animal List now shows real backend data
   - Location filtering works
   - Error handling complete
3. Add User Story 2 → Test independently → Deploy/Demo
   - Pet Details now shows real backend data
   - US1 + US2 both work
4. Add User Story 3 → Test independently → Deploy/Demo
   - Auto-refresh after creating announcement
   - US1 + US2 + US3 all work
5. Each story adds value without breaking previous stories

### Parallel Development (if multiple developers available)

With 2 developers:

1. Both complete Setup + Foundational together (T001-T007)
2. Once Foundational is done:
   - Developer A: User Story 1 (T008-T036) - Animal List API integration
   - Developer B: User Story 2 (T037-T059) - Pet Details API integration
   - Note: Both modify same AnimalRepository.swift file - coordinate changes or merge carefully
3. After US1 + US2 complete:
   - Either developer: User Story 3 (T060-T070) - Refresh behavior

With 3 developers:

1. All complete Setup + Foundational together (T001-T007)
2. Once Foundational is done:
   - Developer A: User Story 1 tests (T008-T022)
   - Developer B: User Story 1 implementation (T023-T030)
   - Developer C: User Story 2 tests (T037-T048)
3. Continue with implementation and verification tasks

---

## Notes

- **iOS-only feature**: No changes to Android, Web, or Backend platforms
- **No UI changes**: Existing AnimalListView and PetDetailsView already display data from ViewModels - no modifications needed
- **No ViewModel signature changes**: ViewModels already use AnimalRepositoryProtocol - only repository implementation changes
- **[P] tasks**: Different files or independent work, can run in parallel
- **[Story] label**: Maps task to specific user story for traceability
- **Each user story independently completable**: Can deploy US1 alone as MVP, then add US2, then US3
- **Test-first approach**: Write tests before implementation, verify they fail, then implement
- **80% coverage target**: Mandatory for iOS platform - verify with Xcode coverage report
- **Commit strategy**: Commit after each task or logical group of related tasks
- **Stop at checkpoints**: Validate each story works independently before proceeding
- **Backend prerequisite**: Backend server must be running on http://localhost:3000 for testing

---

## Risk Mitigation

**Risk**: Same AnimalRepository.swift file modified across multiple user stories
- **Mitigation**: Implement sequentially (US1 → US2) OR coordinate closely if parallel development

**Risk**: Backend API unavailable during development
- **Mitigation**: Use existing FakeAnimalRepository for UI testing, switch to HTTP for integration testing

**Risk**: Network timeout on slow connections
- **Mitigation**: URLSession uses system default timeout (~60s), adequate for mobile networks

**Risk**: Test coverage below 80%
- **Mitigation**: Comprehensive test tasks for each user story, verify coverage at checkpoints

**Risk**: Race conditions with rapid screen switching
- **Mitigation**: Task cancellation implemented in US3, tested with manual rapid switching

---

## Summary

**Total Tasks**: 83 tasks across 6 phases  
**MVP Tasks**: T001-T036 (36 tasks for User Story 1 only)  
**Test Tasks**: 44 tasks (unit tests + E2E tests, including updatedAt format test)  
**Implementation Tasks**: 31 tasks (code + configuration)  
**Verification Tasks**: 8 tasks (coverage, manual testing, polish)

**Task Count by User Story**:
- User Story 1 (P1): 29 tasks (tests + implementation + verification) - MVP
- User Story 2 (P2): 24 tasks (tests + implementation + verification, including updatedAt format handling)
- User Story 3 (P3): 11 tasks (tests + implementation + verification)
- Setup + Foundational: 7 tasks
- Polish: 12 tasks

**Parallel Opportunities**: 
- 53 tasks marked [P] can run in parallel with other [P] tasks in same phase
- User Stories 1 and 2 can be developed in parallel after Foundational phase
- All unit tests within a story can run in parallel
- All E2E tests can run in parallel

**Suggested MVP Scope**: Phase 1 + Phase 2 + Phase 3 (User Story 1 only)
- Delivers core value: Real backend data in Animal List
- 36 tasks total
- Estimated time: 4-6 hours (per quickstart.md)
- Independently testable and deployable

**Estimated Total Time**: 
- MVP (US1): 4-6 hours
- US2: 3-4 hours
- US3: 2-3 hours
- Polish: 1-2 hours
- **Total**: 10-15 hours for complete feature

