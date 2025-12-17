# Tasks: iOS Landing Page - Top Panel

**Input**: Design documents from `/specs/060-ios-landing-page/`
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, quickstart.md ✅

**Tests**: Test requirements for this project:

**MANDATORY - iOS Unit Tests**:
- Location: `/iosApp/iosAppTests/Features/LandingPage/Views/Components/`
- Framework: XCTest with Swift Concurrency (async/await)
- Scope: Presentation models (HeroPanelView_Model, ListHeaderRowView_Model)
- Coverage: 80% line + branch coverage
- Run: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
- Convention: MUST follow Given-When-Then structure with camelCase_with_underscores naming

**MANDATORY - End-to-End Tests**:
- Location: `/e2e-tests/java/src/test/resources/features/mobile/landing-page-top-panel.feature`
- Framework: Java 21 + Maven + Appium + Cucumber
- Scope: All user stories (US1: See top panel, US2: Use top panel actions)
- Pattern: Screen Object Model for iOS (unified for mobile)
- Run: `mvn test -Dtest=IosTestRunner` (from e2e-tests/java/)
- Reports: `e2e-tests/java/target/cucumber-reports/ios/index.html`
- Convention: MUST structure scenarios with Given-When-Then phases

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and localization setup

- [ ] T001 Verify iOS project builds successfully: `cd iosApp && xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16'`
- [ ] T002 Create Components directory in `/iosApp/iosApp/Features/LandingPage/Views/Components/`
- [ ] T003 Create Components test directory in `/iosApp/iosAppTests/Features/LandingPage/Views/Components/`
- [ ] T004 Add localization strings to `/iosApp/iosApp/Resources/en.lproj/Localizable.strings` (5 keys: hero title, 2 button labels, list header title, view all label)
- [ ] T005 Run swiftgen to generate L10n accessors: `cd iosApp && swiftgen`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Refactor existing FloatingActionButton component to support SF Symbols and unified gradient styles

**⚠️ CRITICAL**: This phase MUST be complete before any user story work can begin

- [ ] T006 Refactor FloatingActionButton.Style enum in `/iosApp/iosApp/Views/FloatingActionButton.swift` to use unified gradient styles (primary = blue, secondary = red/orange)
- [ ] T007 Add IconSource enum to `/iosApp/iosApp/Views/FloatingActionButton.swift` (asset vs. sfSymbol support)
- [ ] T008 Add IconPosition enum to `/iosApp/iosApp/Views/FloatingActionButton.swift` (left vs. right)
- [ ] T009 Update FloatingActionButton body to support icon positioning in `/iosApp/iosApp/Views/FloatingActionButton.swift`
- [ ] T010 Update FloatingActionButtonModel in `/iosApp/iosApp/Views/FloatingActionButtonModel.swift` to use IconSource and IconPosition
- [ ] T011 Update existing FloatingActionButton usages to use new API (likely in `/iosApp/iosApp/Features/AnnouncementList/Views/AnnouncementListView.swift`)
- [ ] T012 Build project and verify no compilation errors: `cd iosApp && xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16'`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - See top panel on Home (Priority: P1) 🎯 MVP

**Goal**: Display hero panel ("Find Your Pet" title + "Lost Pet"/"Found Pet" buttons) and list header row ("Recent Reports"/"View All") above existing Home list without affecting list behavior

**Independent Test**: Open app → switch to Home tab → verify hero panel renders above list header row → verify list header row renders above existing list → tap list item → verify existing navigation to details still works

**Acceptance Scenarios** (from spec.md):
1. Hero section shows title "Find Your Pet" and two buttons "Lost Pet" and "Found Pet"
2. Row directly above list shows title "Recent Reports" and action "View All"
3. Existing Home list content and behavior remain unchanged (no regressions in loading, item tap, navigation to details)

### Tests for User Story 1 (MANDATORY) ✅

**iOS Unit Tests**:
- [ ] T013 [P] [US1] Unit test for HeroPanelView_Model initialization in `/iosApp/iosAppTests/Features/LandingPage/Views/Components/HeroPanelView_ModelTests.swift` (default values, localized strings, accessibility IDs)
- [ ] T014 [P] [US1] Unit test for HeroPanelView_Model closure invocation in `/iosApp/iosAppTests/Features/LandingPage/Views/Components/HeroPanelView_ModelTests.swift` (verify onLostPetTap and onFoundPetTap are called)
- [ ] T015 [P] [US1] Unit test for HeroPanelView_Model custom initialization in `/iosApp/iosAppTests/Features/LandingPage/Views/Components/HeroPanelView_ModelTests.swift` (SwiftUI preview use case)
- [ ] T016 [P] [US1] Unit test for ListHeaderRowView_Model initialization in `/iosApp/iosAppTests/Features/LandingPage/Views/Components/ListHeaderRowView_ModelTests.swift` (default values, localized strings, accessibility IDs)
- [ ] T017 [P] [US1] Unit test for ListHeaderRowView_Model closure invocation in `/iosApp/iosAppTests/Features/LandingPage/Views/Components/ListHeaderRowView_ModelTests.swift` (verify onActionTap is called)
- [ ] T018 [P] [US1] Unit test for ListHeaderRowView_Model custom initialization in `/iosApp/iosAppTests/Features/LandingPage/Views/Components/ListHeaderRowView_ModelTests.swift` (SwiftUI preview use case)

**End-to-End Tests**:
- [ ] T019 [P] [US1] Create Gherkin feature file in `/e2e-tests/java/src/test/resources/features/mobile/landing-page-top-panel.feature` with scenario for User Story 1 (see top panel)
- [ ] T020 [P] [US1] Create LandingPageTopPanelScreen in `/e2e-tests/java/src/test/java/com/petspot/e2e/screens/LandingPageTopPanelScreen.java` (Screen Object Model for iOS)
- [ ] T021 [US1] Create LandingPageTopPanelSteps in `/e2e-tests/java/src/test/java/com/petspot/e2e/steps/mobile/LandingPageTopPanelSteps.java` (step definitions for US1 scenarios)

### Implementation for User Story 1

**iOS** (Full Stack Implementation):

**Presentation Models**:
- [ ] T022 [P] [US1] Create HeroPanelView_Model struct in `/iosApp/iosApp/Features/LandingPage/Views/Components/HeroPanelView_Model.swift` (properties: title, button titles, icons, closures, accessibility IDs)
- [ ] T023 [P] [US1] Create ListHeaderRowView_Model struct in `/iosApp/iosApp/Features/LandingPage/Views/Components/ListHeaderRowView_Model.swift` (properties: title, action title, closure, accessibility IDs)

**SwiftUI Views**:
- [ ] T024 [P] [US1] Create HeroPanelView in `/iosApp/iosApp/Features/LandingPage/Views/Components/HeroPanelView.swift` (VStack with title + HStack with 2 FloatingActionButtons using SF Symbols)
- [ ] T025 [P] [US1] Add SwiftUI Preview for HeroPanelView in same file (no-op closures)
- [ ] T026 [P] [US1] Create ListHeaderRowView in `/iosApp/iosApp/Features/LandingPage/Views/Components/ListHeaderRowView.swift` (HStack with title Text + "View All" button)
- [ ] T027 [P] [US1] Add SwiftUI Preview for ListHeaderRowView in same file (no-op closure)

**Layout Integration**:
- [ ] T028 [US1] Modify LandingPageView in `/iosApp/iosApp/Features/LandingPage/Views/LandingPageView.swift` to wrap content in VStack (hero panel → list header row → existing AnnouncementCardsListView with .frame(maxHeight: .infinity))
- [ ] T029 [US1] Add accessibility identifiers to all interactive elements per FR-010 (home.hero.title, home.hero.lostPetButton, home.hero.foundPetButton, home.recentReports.title, home.recentReports.viewAll)

**Verification**:
- [ ] T030 [US1] Build and run on simulator: `cd iosApp && xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16'`
- [ ] T031 [US1] Verify hero panel renders above list header row, list header row renders above existing list
- [ ] T032 [US1] Run unit tests: `cd iosApp && xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16'`
- [ ] T033 [US1] Run E2E tests: `cd e2e-tests/java && mvn test -Dtest=IosTestRunner`
- [ ] T034 [US1] Generate coverage report and verify 80% coverage for presentation models: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently (hero panel + list header row visible, existing list unchanged)

---

## Phase 4: User Story 2 - Use top panel actions to reach core flows (Priority: P1)

**Goal**: Enable hero buttons ("Lost Pet"/"Found Pet") and "View All" to navigate to corresponding tabs without affecting existing Home list navigation

**Independent Test**: On Home tab → tap "Lost Pet" button → verify app switches to Lost Pet tab → return to Home → tap "Found Pet" button → verify app switches to Found Pet tab → return to Home → tap "View All" → verify app switches to Lost Pet tab (full list) → return to Home → tap existing list item → verify navigation to details still works

**Acceptance Scenarios** (from spec.md):
1. Tapping "Lost Pet" navigates to Lost Pet tab
2. Tapping "Found Pet" navigates to Found Pet tab
3. After navigating via hero buttons, returning to Home shows same list state and behavior as before
4. Tapping "View All" navigates to full announcements list screen (existing implementation)

### Tests for User Story 2 (MANDATORY) ✅

**End-to-End Tests**:
- [ ] T035 [P] [US2] Add Gherkin scenario for User Story 2 to `/e2e-tests/java/src/test/resources/features/mobile/landing-page-top-panel.feature` (use top panel actions)
- [ ] T036 [P] [US2] Update LandingPageTopPanelScreen in `/e2e-tests/java/src/test/java/com/petspot/e2e/screens/LandingPageTopPanelScreen.java` to support tab switching verification
- [ ] T037 [US2] Add step definitions for US2 scenarios to `/e2e-tests/java/src/test/java/com/petspot/e2e/steps/mobile/LandingPageTopPanelSteps.java` (tap buttons, verify tab switches)

### Implementation for User Story 2

**iOS** (Navigation Integration):

**Coordinator Changes**:
- [ ] T038 [P] [US2] Add tab navigation closure properties to HomeCoordinator in `/iosApp/iosApp/Features/LandingPage/Coordinators/HomeCoordinator.swift` (onSwitchToLostPetTab, onSwitchToFoundPetTab)
- [ ] T039 [US2] Refactor showPetDetailsFromHome to switchToLostPetTab(withAnnouncementId:) in `/iosApp/iosApp/Coordinators/TabCoordinator.swift` (optional parameter: nil = only switch tab, non-nil = switch + show details)
- [ ] T040 [P] [US2] Add switchToFoundPetTab() method to TabCoordinator in `/iosApp/iosApp/Coordinators/TabCoordinator.swift`
- [ ] T041 [US2] Set tab navigation closures on HomeCoordinator during TabCoordinator init in `/iosApp/iosApp/Coordinators/TabCoordinator.swift` (wire hero button navigation)
- [ ] T042 [US2] Update existing list item navigation to use refactored switchToLostPetTab(withAnnouncementId:) in `/iosApp/iosApp/Coordinators/TabCoordinator.swift`

**ViewModel Changes**:
- [ ] T043 [P] [US2] Add tab navigation closure properties to LandingPageViewModel in `/iosApp/iosApp/Features/LandingPage/Views/LandingPageViewModel.swift` (onSwitchToLostPetTab, onSwitchToFoundPetTab)
- [ ] T044 [US2] Wire tab navigation closures from HomeCoordinator to LandingPageViewModel in `/iosApp/iosApp/Features/LandingPage/Coordinators/HomeCoordinator.swift` (set closures in start() method)

**View Changes**:
- [ ] T045 [US2] Update LandingPageView to create HeroPanelView_Model with ViewModel closures in `/iosApp/iosApp/Features/LandingPage/Views/LandingPageView.swift` (onLostPetTap, onFoundPetTap)
- [ ] T046 [US2] Update LandingPageView to create ListHeaderRowView_Model with ViewModel closure in `/iosApp/iosApp/Features/LandingPage/Views/LandingPageView.swift` (onActionTap → switches to Lost Pet tab)

**Verification**:
- [ ] T047 [US2] Build and run on simulator: `cd iosApp && xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16'`
- [ ] T048 [US2] Manual test: Tap "Lost Pet" button → verify tab switch to Lost Pet
- [ ] T049 [US2] Manual test: Tap "Found Pet" button → verify tab switch to Found Pet
- [ ] T050 [US2] Manual test: Tap "View All" → verify tab switch to Lost Pet tab (full list)
- [ ] T051 [US2] Manual test: Tap existing list item → verify navigation to details still works (regression check)
- [ ] T052 [US2] Run E2E tests: `cd e2e-tests/java && mvn test -Dtest=IosTestRunner`
- [ ] T053 [US2] Verify E2E test report shows all scenarios passing: `open e2e-tests/java/target/cucumber-reports/ios/index.html`

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently (hero panel renders + all navigation works correctly)

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Final verification, edge cases, documentation

- [ ] T054 [P] Test on iPhone SE (smallest screen) - verify hero panel + list header row stay visible, list shrinks and remains scrollable
- [ ] T055 [P] Test with Dynamic Type (larger text sizes) - verify title and buttons remain visible without overlapping
- [ ] T056 [P] Test VoiceOver - verify focus order: title → Lost Pet → Found Pet → Recent Reports → View All → list items
- [ ] T057 [P] Test rapid button taps - verify no crashes or inconsistent navigation state
- [ ] T058 Run full unit test suite and verify 80% coverage: `cd iosApp && xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
- [ ] T059 Run full E2E test suite: `cd e2e-tests/java && mvn test -Dtest=IosTestRunner`
- [ ] T060 Visual QA against Figma design (node 974:4667) - verify spacing, colors, typography match
- [ ] T061 [P] Add SwiftDoc documentation to presentation models if purpose not clear from name alone (minimal, 1-3 sentences)
- [ ] T062 Final regression check - verify all existing Home list behavior unchanged (loading, empty state, error state, item tap navigation)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories (refactors FloatingActionButton)
- **User Story 1 (Phase 3)**: Depends on Foundational completion - can start after Phase 2
- **User Story 2 (Phase 4)**: Depends on User Story 1 completion (needs hero panel + list header row UI to be present)
- **Polish (Phase 5)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P1)**: Depends on User Story 1 (needs UI components to wire navigation closures to)

### Within Each User Story

**User Story 1**:
1. Tests (T013-T021) can run in parallel (marked [P])
2. Presentation models (T022-T023) can run in parallel (marked [P])
3. Views (T024-T027) can run in parallel but need models complete (marked [P])
4. Layout integration (T028-T029) needs views complete
5. Verification (T030-T034) needs all implementation complete

**User Story 2**:
1. Tests (T035-T037) can run in parallel (marked [P])
2. Coordinator changes (T038-T042) need careful sequencing (refactoring existing code)
3. ViewModel + View changes (T043-T046) can run in parallel with coordinator work (marked [P])
4. Verification (T047-T053) needs all implementation complete

### Parallel Opportunities

- **Phase 1 Setup**: All tasks (T001-T005) can run sequentially (quick)
- **Phase 2 Foundational**: Tasks T006-T011 must be sequential (refactoring existing component)
- **Phase 3 User Story 1**:
  - Tests: T013-T021 (9 tasks) can run in parallel
  - Models: T022-T023 (2 tasks) can run in parallel
  - Views: T024-T027 (4 tasks) can run in parallel (after models)
- **Phase 4 User Story 2**:
  - Tests: T035-T037 (3 tasks) can run in parallel
  - Coordinator changes: T038-T042 (5 tasks) sequential (refactoring)
  - ViewModel/View: T043-T046 (4 tasks) can overlap with coordinator work
- **Phase 5 Polish**: Most tasks (T054-T061) can run in parallel (marked [P])

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together:
Task T013: "iOS unit test for HeroPanelView_Model initialization"
Task T014: "iOS unit test for HeroPanelView_Model closure invocation"
Task T015: "iOS unit test for HeroPanelView_Model custom initialization"
Task T016: "iOS unit test for ListHeaderRowView_Model initialization"
Task T017: "iOS unit test for ListHeaderRowView_Model closure invocation"
Task T018: "iOS unit test for ListHeaderRowView_Model custom initialization"
Task T019: "E2E Gherkin feature for User Story 1"
Task T020: "E2E Screen Object Model for iOS"
Task T021: "E2E step definitions for User Story 1"

# After tests written, launch presentation model creation together:
Task T022: "Create HeroPanelView_Model struct"
Task T023: "Create ListHeaderRowView_Model struct"

# After models created, launch view creation together:
Task T024: "Create HeroPanelView SwiftUI component"
Task T025: "Add SwiftUI Preview for HeroPanelView"
Task T026: "Create ListHeaderRowView SwiftUI component"
Task T027: "Add SwiftUI Preview for ListHeaderRowView"

# Then layout integration (sequential):
Task T028: "Modify LandingPageView to use VStack layout"
Task T029: "Add accessibility identifiers"

# Finally verification (sequential):
Tasks T030-T034: Build, run, test, verify coverage
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (5 tasks - ~30 minutes)
2. Complete Phase 2: Foundational (7 tasks - ~2 hours, refactoring FloatingActionButton)
3. Complete Phase 3: User Story 1 (22 tasks - ~3-4 hours)
4. **STOP and VALIDATE**: Test User Story 1 independently
   - Hero panel renders above list header row ✅
   - List header row renders above existing list ✅
   - Existing list behavior unchanged ✅
   - Unit tests pass with 80% coverage ✅
   - E2E tests pass ✅
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP! UI visible but buttons don't navigate)
3. Add User Story 2 → Test independently → Deploy/Demo (Full feature! Navigation works)
4. Polish phase → Final edge case testing and documentation

### Recommended Approach

**Single Developer** (estimated 6-8 hours total):
1. Phase 1: Setup (30 min)
2. Phase 2: Foundational (2 hours)
3. Phase 3: User Story 1 (3-4 hours)
   - Write all tests first (1 hour)
   - Create models + views (1 hour)
   - Layout integration (30 min)
   - Verification (30 min)
4. Phase 4: User Story 2 (2 hours)
   - Write tests (30 min)
   - Coordinator refactoring (1 hour)
   - ViewModel + View wiring (30 min)
5. Phase 5: Polish (1 hour)

**Two Developers** (estimated 4-5 hours total):
1. Both: Phase 1 + Phase 2 together (2.5 hours)
2. Split User Stories:
   - Developer A: User Story 1 implementation (2 hours)
   - Developer B: User Story 2 tests + planning (1 hour)
3. Developer B: User Story 2 implementation after US1 complete (1 hour)
4. Both: Polish phase together (1 hour)

---

## Notes

- **[P] tasks** = different files, no dependencies, can run in parallel
- **[Story] label** maps task to specific user story for traceability
- **Each user story** should be independently completable and testable
- **Verify tests** fail before implementing (TDD where applicable)
- **Commit** after each task or logical group
- **Stop at checkpoints** to validate story independently
- **NO changes** to existing Home list logic or `AnnouncementCardsListView` per feature spec
- **Reuse** existing `FloatingActionButton` component after refactoring in Phase 2
- **80% test coverage** mandatory for presentation models
- **E2E tests** mandatory for both user stories (Java/Appium/Cucumber)

---

## Summary

**Total Tasks**: 62 tasks
- Phase 1 (Setup): 5 tasks
- Phase 2 (Foundational): 7 tasks
- Phase 3 (User Story 1): 22 tasks
- Phase 4 (User Story 2): 19 tasks
- Phase 5 (Polish): 9 tasks

**Task Count per User Story**:
- User Story 1: 22 tasks (UI rendering + tests)
- User Story 2: 19 tasks (Navigation integration + tests)

**Parallel Opportunities Identified**:
- Phase 3 User Story 1: 15 parallel tasks (tests + models + views)
- Phase 4 User Story 2: 7 parallel tasks (tests + some implementation)
- Phase 5 Polish: 8 parallel tasks (edge case testing + documentation)

**Independent Test Criteria**:
- User Story 1: Hero panel + list header row render above existing list, no regressions in list behavior
- User Story 2: All navigation works (Lost Pet button → Lost Pet tab, Found Pet button → Found Pet tab, View All → full list, existing list taps → details)

**Suggested MVP Scope**: User Story 1 only (visual UI complete, buttons present but don't navigate yet)

**Full Feature**: User Story 1 + User Story 2 (all navigation working)

**Estimated Time**: 6-8 hours (single developer), 4-5 hours (two developers with split work)

---

## Format Validation ✅

All tasks follow the checklist format:
- ✅ Checkbox: Every task starts with `- [ ]`
- ✅ Task ID: Sequential (T001, T002, T003...)
- ✅ [P] marker: Present on parallelizable tasks only
- ✅ [Story] label: Present on user story phase tasks (US1, US2)
- ✅ Description: Clear action with exact file path
- ✅ Setup phase: NO story labels
- ✅ Foundational phase: NO story labels
- ✅ User Story phases: ALL tasks have story labels
- ✅ Polish phase: NO story labels

