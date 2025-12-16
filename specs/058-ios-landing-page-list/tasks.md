# Tasks: iOS Landing Page (Home Tab)

**Input**: Design documents from `/specs/058-ios-landing-page-list/`
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, quickstart.md ✅, contracts/ ✅

**Platform Scope**: iOS only (Android, Web, Backend not affected)
- Backend: Reuses existing GET /api/v1/announcements endpoint (no changes)
- iOS: Full implementation in `/iosApp/`

**Tests**: Test requirements for this project:

**MANDATORY - iOS Unit Tests**:
- Location: `/iosApp/iosAppTests/`
- Framework: XCTest with Swift Concurrency (async/await)
- Coverage: 80% line + branch for ViewModels
- Scope: AnnouncementCardsListViewModel, LandingPageViewModel, HomeCoordinator
- Convention: MUST follow Given-When-Then structure with Swift naming (camelCase_with_underscores)
- Run: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`

**MANDATORY - End-to-End Tests**:
- Mobile: `/e2e-tests/java/src/test/resources/features/mobile/landing-page.feature` (Java 21 + Cucumber + Appium)
- All user stories MUST have E2E test coverage
- Use Screen Object Model pattern
- Convention: Gherkin Given-When-Then structure
- Run: `mvn test -Dtest=IosTestRunner` (from e2e-tests/java/)

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story?] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

---

## Phase 1: Setup

**Purpose**: Project initialization and model creation

- [ ] T001 Create `AnnouncementListQuery` model with factory methods in `/iosApp/iosApp/Domain/Models/AnnouncementListQuery.swift`
- [ ] T002 Create directory structure for LandingPage feature: `/iosApp/iosApp/Features/LandingPage/Views/` and `/iosApp/iosApp/Features/LandingPage/Coordinators/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T003 Create `AnnouncementCardsListViewModel` (autonomous component) in `/iosApp/iosApp/Views/AnnouncementCardsListViewModel.swift`
- [ ] T004 Create `AnnouncementCardsListView` (reusable UI component) in `/iosApp/iosApp/Views/AnnouncementCardsListView.swift`
- [ ] T005 [P] Reuse existing fake repository in `/iosApp/iosAppTests/Fakes/FakeAnnouncementRepository.swift` (create only if missing)
- [ ] T006 [P] Reuse existing fakes for location flow in `/iosApp/iosAppTests/Fakes/` (create only if missing; avoid introducing a new `Mocks/` convention)

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - View Recent Pet Announcements on Home Tab (Priority: P1) 🎯 MVP

**Goal**: Users opening the app land on a Home tab that displays the 5 most recent pet announcements, sorted by creation date (newest first). Empty state, loading state, and error handling are properly displayed.

**Independent Test**: Can be fully tested by launching the app, landing on the Home tab, and verifying that 5 most recent announcements are displayed with correct details (name, species, status, photo, location). Delivers value by providing quick access to recent pet reports.

### Unit Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [ ] T007 [P] [US1] Create `AnnouncementCardsListViewModelTests.swift` in `/iosApp/iosAppTests/Views/AnnouncementCardsListViewModelTests.swift`
  - Test: `setQuery_whenQueryLimitIs5_shouldTriggerLoadAndDisplayFirst5MostRecent`
  - Test: `setQuery_whenRepositoryReturnsLessThan5Items_shouldDisplayAllAvailable`
  - Test: `setQuery_whenRepositoryReturnsEmptyArray_shouldResultInEmptyCardViewModels`
  - Test: `setQuery_whenRepositoryThrowsError_shouldSetErrorMessage`
  - Test: `reload_shouldCancelPreviousTaskAndStartNew`
  - Test: `applyQuery_shouldSortByCreatedAtDescending`
  - Test: `onAnnouncementTapped_shouldInvokeClosureWithCorrectId`
- [ ] T008 [P] [US1] Create `LandingPageViewModelTests.swift` in `/iosApp/iosAppTests/Features/LandingPage/Views/LandingPageViewModelTests.swift`
  - Test: `init_shouldCreateListViewModelWithLandingPageQuery`
  - Test: `loadData_shouldFetchLocationAndSetQueryOnListViewModel`
  - Test: `loadData_whenLocationDenied_shouldSetQueryWithNilLocation`
- [ ] T032 [P] [US1] Extend `AnnouncementMapperTests.swift` in `/iosApp/iosAppTests/Data/Mappers/AnnouncementMapperTests.swift` for malformed DTO handling (FR-016)
  - Test: `map_whenDtoHasEmptyId_shouldReturnNil`
  - Test: `map_whenDtoHasInvalidCoordinates_shouldReturnNil` (e.g., NaN)
- [ ] T033 [P] [US1] Extend `AnnouncementCardViewModelTests.swift` in `/iosApp/iosAppTests/Features/AnnouncementList/Views/AnnouncementCardViewModelTests.swift` to cover invalid photo URLs (FR-016)
  - Test: `photoURL_whenPhotoUrlIsEmpty_shouldBeNil`
  - Test: `photoURL_whenPhotoUrlIsInvalid_shouldBeNil`

### E2E Tests for User Story 1 (MANDATORY) ✅

- [ ] T009 [P] [US1] Create E2E feature file `/e2e-tests/java/src/test/resources/features/mobile/landing-page.feature`
  - Scenario: Display 5 most recent announcements
  - Scenario: Display all announcements when backend has fewer than 5
  - Scenario: Display empty state when no announcements
  - Scenario: Display error message when backend is unavailable
  - Scenario: Display location coordinates when location permissions granted
  - Scenario: Hide location coordinates when location permissions denied
- [ ] T010 [P] [US1] Create Screen Object `LandingPageScreen.java` in `/e2e-tests/java/src/test/java/com/petspot/e2e/screens/LandingPageScreen.java`
- [ ] T011 [P] [US1] Create step definitions `LandingPageSteps.java` in `/e2e-tests/java/src/test/java/com/petspot/e2e/steps/mobile/LandingPageSteps.java`

### Implementation for User Story 1

**iOS** (Full Stack Implementation):

- [ ] T012 [US1] Implement `LandingPageViewModel` in `/iosApp/iosApp/Features/LandingPage/Views/LandingPageViewModel.swift`
  - Properties: `listViewModel: AnnouncementCardsListViewModel`, `locationHandler: LocationPermissionHandler`
  - Methods: `loadData()` async, `refreshIfNeeded()` async
  - Pattern: Thin wrapper, delegates list logic to child ViewModel
- [ ] T013 [US1] Implement `LandingPageView` in `/iosApp/iosApp/Features/LandingPage/Views/LandingPageView.swift`
  - Composite view - list is only part of the screen (future: header, sections, etc.)
  - Compose `AnnouncementCardsListView` (handles its own empty/error/loading states internally)
  - Add `.task { await viewModel.loadData() }`
  - NO NavigationView (coordinator manages UINavigationController)
  - NO custom empty state in LandingPageView - delegated to AnnouncementCardsListView
- [ ] T014 [US1] Implement `HomeCoordinator` in `/iosApp/iosApp/Features/LandingPage/Coordinators/HomeCoordinator.swift`
  - Follow same pattern as `AnnouncementListCoordinator`
  - Simple `init(onShowPetDetails: @escaping (String) -> Void)`
  - `start(animated:)` fetches dependencies from `ServiceContainer.shared`
  - Creates `LandingPageViewModel` and presents `LandingPageView` in UIHostingController
- [ ] T015 [US1] Modify `TabCoordinator` to use `HomeCoordinator` instead of `PlaceholderCoordinator` in `/iosApp/iosApp/Coordinators/TabCoordinator.swift`
  - Replace `PlaceholderCoordinator(title: L10n.Tabs.home)` with `HomeCoordinator(onShowPetDetails: { ... })`
  - Add `showPetDetailsFromHome(_ announcementId: String)` private method (placeholder for US2)
- [ ] T016 [US1] Add accessibility identifiers to `LandingPageView` and `AnnouncementCardsListView`:
  - Loading: `landingPage.loading`
  - Error: `landingPage.error`
  - Empty state: `landingPage.emptyState`
  - List: `landingPage.list`
  - Cards inherit from `AnnouncementCardView`
- [ ] T017 [P] [US1] Add SwiftDoc documentation to `AnnouncementCardsListViewModel`, `LandingPageViewModel`, `HomeCoordinator` public APIs
- [ ] T034 [US1] Implement malformed data skipping in `/iosApp/iosApp/Data/Mappers/AnnouncementMapper.swift` and ensure repository filters out invalid items (FR-016)
  - Return `nil` from mapper for invalid required fields (e.g., empty id) or invalid coordinates
  - Ensure list endpoint mapping uses `compactMap` and does not crash when one item is invalid
- [ ] T035 [US1] Prevent infinite loading placeholder for invalid/empty photo URL in `/iosApp/iosApp/Features/AnnouncementList/Views/AnnouncementCardView.swift` (FR-016)
  - If `photoURL` is nil, show the same error placeholder (pawprint) instead of `AsyncImage`’s `.empty` spinner
- [ ] T018 [US1] Run unit tests and verify 80% coverage for `AnnouncementCardsListViewModel` and `LandingPageViewModel`

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently. Home tab displays 5 recent announcements with proper states (loading, error, empty, success).

---

## Phase 4: User Story 2 - Navigate to Pet Details from Landing Page (Priority: P2)

**Goal**: Users can tap on any announcement card on the Home tab to view complete details about that pet. Navigation switches to the Lost Pets tab and opens the selected pet's detail screen.

**Independent Test**: Can be tested by tapping any announcement card on Home tab and verifying navigation switches to Lost Pets tab with the correct pet detail screen displayed. Delivers value by enabling quick access to full pet information.

### Unit Tests for User Story 2 (MANDATORY) ✅

- [ ] T019 [P] [US2] Add tests to `AnnouncementCardsListViewModelTests.swift` in `/iosApp/iosAppTests/Views/AnnouncementCardsListViewModelTests.swift`
  - Test: `handleAnnouncementAction_whenSelected_shouldInvokeOnAnnouncementTappedWithId`
- [ ] T020 [P] [US2] Create `HomeCoordinatorTests.swift` in `/iosApp/iosAppTests/Features/LandingPage/Coordinators/HomeCoordinatorTests.swift`
  - Test: `start_shouldCreateLandingPageViewAndSetAsRootViewController`
  - Test: `onShowPetDetails_shouldInvokeClosureWithAnnouncementId`

### E2E Tests for User Story 2 (MANDATORY) ✅

- [ ] T021 [P] [US2] Add E2E scenarios to `/e2e-tests/java/src/test/resources/features/mobile/landing-page.feature`
  - Scenario: Navigate to pet details from landing page
  - Scenario: Back navigation returns to Lost Pets tab (not Home tab)
  - Scenario: Tapping Home tab after viewing details returns to landing page

### Implementation for User Story 2

**iOS** (Cross-Tab Navigation):

- [ ] T022 [US2] Implement cross-tab navigation in `TabCoordinator.showPetDetailsFromHome(_:)` in `/iosApp/iosApp/Coordinators/TabCoordinator.swift`
  - Switch `selectedIndex` to Lost Pets tab (index 1)
  - Call `AnnouncementListCoordinator.showPetDetails(for:)` to push detail screen
- [ ] T023 [US2] Verify `AnnouncementListCoordinator.showPetDetails(for:)` method exists and works correctly in `/iosApp/iosApp/Features/AnnouncementList/Coordinators/AnnouncementListCoordinator.swift`
  - If method doesn't exist: implement it to push `PetDetailsCoordinator`
- [ ] T024 [US2] Wire up `onAnnouncementTapped` closure chain:
  - `AnnouncementCardsListViewModel.onAnnouncementTapped` → `LandingPageViewModel` → `HomeCoordinator.onShowPetDetails` → `TabCoordinator.showPetDetailsFromHome`
- [ ] T025 [P] [US2] Add SwiftDoc documentation to cross-tab navigation methods
- [ ] T026 [US2] Run E2E tests for navigation scenarios

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently. Tapping announcement on Home tab navigates to Lost Pets tab with pet details.

---

## Phase 5: Polish & Optional Refactoring

**Purpose**: Improvements that affect code quality and consistency

- [ ] T027 [P] (Optional) Refactor `AnnouncementListViewModel` to use `AnnouncementCardsListViewModel` in `/iosApp/iosApp/Features/AnnouncementList/Views/AnnouncementListViewModel.swift`
  - Extract list logic to child ViewModel
  - Keep parent-specific logic (permissions, floating buttons, foreground observer)
  - Benefits: DRY, consistency with LandingPage pattern
- [ ] T028 [P] (Optional) Refactor `AnnouncementListView` to use `AnnouncementCardsListView` in `/iosApp/iosApp/Features/AnnouncementList/Views/AnnouncementListView.swift`
  - Replace inline list rendering with shared component
  - Keep parent-specific overlays (floating buttons, permission alerts)
- [ ] T029 Run full test suite and verify 80% coverage across all ViewModels
- [ ] T030 Manual testing checklist:
  - [ ] Landing page displays 5 announcements when backend has 10+
  - [ ] Landing page displays 3 announcements when backend has 3
  - [ ] Empty state displays when backend has 0 announcements
  - [ ] Error state displays when backend is unavailable
  - [ ] Tapping announcement navigates to Lost Pets tab with detail screen
  - [ ] Back navigation returns to Lost Pets tab, not Home tab
  - [ ] Location coordinates displayed when location permissions granted
  - [ ] Location coordinates hidden when location permissions denied
- [ ] T031 Run quickstart.md validation (all steps executable)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on T001 completion (AnnouncementListQuery model needed by ViewModel)
- **User Story 1 (Phase 3)**: Depends on Phase 2 completion (autonomous ViewModel and View must exist)
- **User Story 2 (Phase 4)**: Depends on User Story 1 implementation (landing page must exist to navigate from)
- **Polish (Phase 5)**: Depends on User Stories 1 and 2 being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Depends on User Story 1 (needs landing page to tap announcements) - Navigation integration

### Within Each User Story

- Tests MUST be written and FAIL before implementation (TDD)
- Models before ViewModels
- ViewModels before Views
- Views before Coordinators
- Unit tests before E2E tests (for faster feedback loop)

### Parallel Opportunities

**Phase 2 (Foundational)**:
- T005 and T006 can run in parallel (mock creation)

**Phase 3 (User Story 1)**:
- T007 and T008 can run in parallel (unit test files)
- T009, T010, T011 can run in parallel (E2E infrastructure)
- T017 can run in parallel with other implementation tasks

**Phase 4 (User Story 2)**:
- T019 and T020 can run in parallel (unit test additions)
- T025 can run in parallel with implementation

**Phase 5 (Polish)**:
- T027 and T028 can run in parallel (optional refactoring)

---

## Parallel Example: User Story 1

```bash
# Launch all unit tests for User Story 1 together:
Task T007: "AnnouncementCardsListViewModelTests.swift"
Task T008: "LandingPageViewModelTests.swift"

# Launch all E2E infrastructure for User Story 1 together:
Task T009: "landing-page.feature"
Task T010: "LandingPageScreen.java"
Task T011: "LandingPageSteps.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (T001-T003)
2. Complete Phase 2: Foundational (T004-T007)
3. Complete Phase 3: User Story 1 (T008-T019)
4. **STOP and VALIDATE**: Test User Story 1 independently
   - App launches → Home tab visible → 5 announcements displayed
   - Empty state works
   - Error state works
   - Loading state works
5. Deploy/demo if ready (landing page shows announcements but tapping does nothing yet)

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
   - Value: Users can see recent announcements on Home tab
3. Add User Story 2 → Test independently → Deploy/Demo
   - Value: Users can tap announcements to see full details
4. Add Polish (Optional) → Improved code quality
   - Value: DRY codebase, consistent patterns

### Estimated Implementation Time

| Phase | Tasks | Time Estimate |
|-------|-------|---------------|
| Phase 1: Setup | T001-T002 | 10 min |
| Phase 2: Foundational | T003-T006 | 60 min |
| Phase 3: User Story 1 | T007-T018 | 180 min |
| Phase 4: User Story 2 | T019-T026 | 90 min |
| Phase 5: Polish | T027-T031 | 60 min (optional: +60 min) |
| **Total** | **31 tasks** | **~6.5 hours** (+ 1 hour optional) |

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing (TDD)
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
- **iOS-only feature**: No Android, Web, or Backend tasks required
- **Reuses existing**: AnnouncementRepository, LocationPermissionHandler, AnnouncementCardView, EmptyStateView, ErrorView, LoadingView

