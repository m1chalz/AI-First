# Tasks: Android Tab Navigation

**Feature**: 056-android-tab-navigation  
**Input**: Design documents from `/specs/056-android-tab-navigation/`  
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/, quickstart.md

**Platform Scope**: Android only (no backend, iOS, or web changes)

**Tests**: Test requirements for this project:

**MANDATORY - Android Unit Tests**:
- Location: `/composeApp/src/androidUnitTest/`
- Framework: JUnit 6 + Kotlin Test
- Scope: Domain models (TabDestination enum only) - minimal unit tests since navigation logic is framework-managed
- Run: `./gradlew :composeApp:testDebugUnitTest`
- Convention: MUST follow Given-When-Then structure with descriptive backtick names
- Note: Primary test coverage through E2E tests (navigation behavior testing)

**MANDATORY - End-to-End Tests**:
- Location: `/e2e-tests/java/`
- Framework: Java 21 + Maven + Selenium/Appium + Cucumber
- Coverage: All 6 acceptance scenarios from spec.md
- Feature file: `src/test/resources/features/mobile/056-tab-navigation.feature`
- Screen objects: BottomNavigationScreen, PlaceholderScreen
- Run: `mvn test -Dtest=AndroidTestRunner` (from e2e-tests/java/)
- Convention: MUST structure scenarios with Given-When-Then phases

**Organization**: Tasks are grouped by implementation phase. Since this is a single user story (tab navigation infrastructure), all tasks contribute to one cohesive deliverable.

**Total Tasks**: 26 tasks organized across 4 phases (simplified from original 42 by removing unnecessary MVI complexity)

**Task Breakdown by Phase**:
- **Phase 1 (Setup)**: 5 tasks - Verify dependencies and project structure
- **Phase 2 (Foundational)**: 1 task - Core navigation routes (no DI needed)
- **Phase 3 (User Story 1)**: 15 tasks - Tab Navigation System implementation (MVP)
- **Phase 4 (Polish)**: 5 tasks - Documentation, validation, and cleanup

## Format: `- [ ] [ID] [P?] [Story?] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[US1]**: User Story 1 label (all tasks belong to the tab navigation story)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Verify dependencies and project structure for tab navigation feature

- [X] T001 Verify Jetpack Compose Material 3 dependency in `/composeApp/build.gradle.kts` (androidx.compose.material3)
- [X] T002 [P] Verify Jetpack Navigation Compose 2.9.0 dependency in `/composeApp/build.gradle.kts` (androidx.navigation:navigation-compose)
- [X] T003 [P] Verify Koin 3.5.3 dependency in `/composeApp/build.gradle.kts` (io.insert-koin:koin-androidx-compose)
- [X] T004 [P] Verify kotlinx-serialization 1.8.0 dependency in `/composeApp/build.gradle.kts` (kotlinx-serialization-json)
- [X] T005 [P] Verify JUnit 6 + Turbine dependencies in `/composeApp/build.gradle.kts` for unit testing

**Checkpoint**: All required dependencies confirmed - ready for foundational setup

---

## Phase 2: Foundational (Core Navigation Infrastructure)

**Purpose**: Set up navigation routes that the main implementation depends on

**⚠️ CRITICAL**: This task must be complete before implementing User Story 1

- [X] T006 Create type-safe navigation route definitions in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/navigation/TabRoutes.kt` (@Serializable sealed interfaces: TabRoute, HomeRoute, LostPetRoute, FoundPetRoute, ContactRoute, AccountRoute)

**Checkpoint**: Navigation routes defined - User Story 1 implementation can begin

---

## Phase 3: User Story 1 - Navigate to Portal Sections Using Tabs (Priority: P1) 🎯 MVP

**Goal**: Users can use the tab navigation system to access different sections of the portal (browse lost pets, browse found pets, view placeholders for future features). The navigation is available from any screen in the application.

**Independent Test**: Can be fully tested by tapping each tab item and verifying navigation to the appropriate section. Delivers value by enabling users to accomplish their specific goals.

**Acceptance Scenarios** (from spec.md):
1. User taps "Home" tab → navigates to landing page
2. User taps "Lost Pet" tab → navigates to lost pet announcements list
3. User taps "Found Pet" tab → navigates to found pet announcements list
4. User taps "Contact Us" tab → navigates to placeholder screen
5. User taps "Account" tab → navigates to placeholder screen
6. Current tab is visually indicated (highlighted/selected state)

### Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**Android Unit Tests** (Given-When-Then):

- [X] T007 [P] [US1] Unit test for TabDestination enum in `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/domain/models/TabDestinationTest.kt` (test toRoute() mapping, entries order matches spec, verify all 5 tabs present)

**End-to-End Tests** (Java/Cucumber):

- [X] T008 [P] [US1] Create Cucumber feature file in `/e2e-tests/java/src/test/resources/features/mobile/056-tab-navigation.feature` with all 6 acceptance scenarios (Given-When-Then structure, @android and @mobile tags)
- [X] T009 [P] [US1] Create BottomNavigationScreen object in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/screens/BottomNavigationScreen.java` (methods: tapHomeTab(), tapLostPetTab(), tapFoundPetTab(), tapContactTab(), tapAccountTab(), isHomeTabSelected(), etc.)
- [X] T010 [P] [US1] Create PlaceholderScreen object in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/screens/PlaceholderScreen.java` (methods: isComingSoonTextDisplayed())
- [X] T011 [US1] Create step definitions in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/mobile/TabNavigationSteps.java` (implement all Given-When-Then steps for 6 scenarios)
- [X] T012 [P] [US1] Update AndroidTestRunner in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/runners/AndroidTestRunner.java` to include @mobile tag

### Implementation for User Story 1

**Domain Models**:

- [X] T013 [P] [US1] Create TabDestination enum in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/domain/models/TabDestination.kt` (5 tabs: HOME, LOST_PET, FOUND_PET, CONTACT_US, ACCOUNT with label, icon, testId; toRoute() method to map to type-safe routes)

**UI Layer** (Composables):

- [X] T014 [P] [US1] Create PlaceholderScreen composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/ui/navigation/PlaceholderScreen.kt` (stateless, centered "Coming soon" text, testTag "placeholder.comingSoonText", includes @Preview)
- [X] T015 [US1] Create MainScaffold composable in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/ui/navigation/MainScaffold.kt`:
  - Create navController using rememberNavController()
  - Observe current destination using navController.currentBackStackEntryAsState()
  - Scaffold with Material 3 NavigationBar in bottomBar slot
  - NavigationBar iterates over TabDestination.entries to create NavigationBarItem for each tab
  - Selected tab determined by matching currentRoute with tab routes
  - NavigationBarItem onClick handles tab switching with saveState/restoreState flags
  - NavigationBarItem onClick handles re-tap behavior (if already on tab, popBackStack to tab root)
  - Add testTag to each NavigationBarItem (e.g., "bottomNav.homeTab")
  - Single NavHost with nested navigation graphs (one per tab)
  - Each navigation graph uses type-safe routes (TabRoute.Home, TabRoute.LostPet, etc.)
  - All tabs navigate to PlaceholderScreen for now (implementations TBD)
  - Add Modifier.padding(paddingValues) to NavHost
  - Add @Preview function with mock NavController
- [X] T016 [US1] Wire MainScaffold into app's main navigation entry point (replace or integrate with existing NavGraph in `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/navigation/NavGraph.kt` or MainActivity)

**Verification Tasks**:

- [X] T017 [US1] Run Android unit tests: `./gradlew :composeApp:testDebugUnitTest` (verify TabDestination enum tests pass)
- [X] T018 [P] [US1] Fix any Android linter warnings reported by detekt or Android Lint
- [X] T019 [US1] Build Android app and verify no compilation errors: `./gradlew :composeApp:assembleDebug`
- [ ] T020 [US1] Run E2E tests for Android: `mvn test -Dtest=AndroidTestRunner` (from e2e-tests/java/)
- [ ] T021 [P] [US1] Manually test tab navigation on Android emulator or device:
  - Verify all 6 acceptance scenarios
  - Test back button: navigate within tab, press back to pop within tab, press back at tab root to verify app behavior
  - Test re-tap behavior on active tab (should pop to tab root)
  - Test configuration changes (rotation, dark mode)

**Checkpoint**: At this point, User Story 1 (Tab Navigation System) should be fully functional and testable independently

---

## Phase 4: Polish & Documentation

**Purpose**: Final validation, documentation updates, and cleanup

- [X] T022 [P] Update quickstart.md if any implementation patterns differ from documented examples (simplified to NavController-only approach)
- [X] T023 [P] Verify all test identifiers (testTag) follow naming convention "{screen}.{element}.{action}" in MainScaffold and PlaceholderScreen
- [X] T024 [P] Review code for KDoc documentation completeness (skip self-explanatory methods, document only complex logic if any)
- [ ] T025 Validate quickstart.md scenarios work as documented (add new tab, change icon, debug navigation)
- [ ] T026 Final E2E test run for all acceptance scenarios: `mvn test -Dtest=AndroidTestRunner`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS User Story 1
- **User Story 1 (Phase 3)**: Depends on Foundational phase completion
- **Polish (Phase 4)**: Depends on User Story 1 completion

### User Story Dependencies

- **User Story 1**: Only one user story for this feature - no inter-story dependencies

### Within User Story 1

1. **Tests FIRST** (T007-T012): Write all tests, ensure they fail
2. **Domain Model** (T013): Create TabDestination enum
3. **UI Implementation** (T014-T015): Build PlaceholderScreen and MainScaffold composables
4. **Integration** (T016): Wire MainScaffold into app navigation
5. **Verification** (T017-T021): Run tests and validate

### Parallel Opportunities

**Phase 1 (Setup)**: All 4 tasks marked [P] (T002-T005) can verify dependencies in parallel

**Phase 2 (Foundational)**: Only T006 - no parallel opportunities in this phase

**Phase 3 - Tests**: Tasks T008, T009, T010, T012 (E2E artifacts) can run in parallel

**Phase 3 - Implementation**: Tasks T013 (enum) and T014 (PlaceholderScreen) can run in parallel (different files)

**Phase 3 - Verification**: Tasks T018, T021 can run in parallel

**Phase 4 (Polish)**: Tasks T022, T023, T024 can all run in parallel

---

## Parallel Example: User Story 1

```bash
# Launch E2E artifact tasks together (Phase 3):
Task T008: "Create Cucumber feature file"
Task T009: "Create BottomNavigationScreen object"
Task T010: "Create PlaceholderScreen object"
Task T012: "Update AndroidTestRunner"

# Launch implementation tasks together (Phase 3):
Task T013: "Create TabDestination enum"
Task T014: "Create PlaceholderScreen composable"

# Launch verification tasks together:
Task T018: "Fix linter warnings"
Task T021: "Manual testing on emulator"
```

---

## Implementation Strategy

### MVP (This Entire Feature is MVP)

This feature is foundational navigation infrastructure. All phases must be completed for a functional tab navigation system:

1. **Phase 1**: Verify dependencies (5 minutes)
2. **Phase 2**: Set up navigation routes (minimal DI since no ViewModel) (20 minutes)
3. **Phase 3**: Implement User Story 1 (3-4 hours)
   - Tests first (1-1.5 hours)
   - Domain model (15 minutes)
   - UI implementation (1.5-2 hours for MainScaffold with NavHost + navigation logic)
   - Integration (15 minutes)
   - Verification (30 minutes)
4. **Phase 4**: Polish and documentation (30 minutes)

**Total Estimated Time**: 4-5 hours for single developer (simplified from 6-8 hours)

### Sequential Strategy (Recommended)

Follow the phases in order:

1. Complete Phase 1 (Setup) → verify dependencies
2. Complete Phase 2 (Foundational) → navigation infrastructure ready
3. Complete Phase 3 (User Story 1) → tab navigation functional
4. Complete Phase 4 (Polish) → production-ready

### Parallel Team Strategy (If Multiple Developers)

1. **Setup + Foundational**: One developer (20 minutes)
2. Once Foundational complete:
   - **Developer A**: E2E Tests (T008-T012)
   - **Developer B**: Implementation (T013-T016: enum + UI + integration)
3. **Verification**: Both developers run tests and validate (T017-T021)
4. **Polish**: Divide polish tasks among team (T022-T026)

---

## Notes

- **[P] tasks**: Different files, no dependencies - can run in parallel
- **[US1] label**: All implementation tasks belong to User Story 1 (Tab Navigation)
- **Type-safe navigation**: All routes use `@Serializable` sealed interfaces (matches existing codebase pattern)
- **Simplified architecture**: No ViewModel/MVI needed - NavController manages all navigation state
- **Unit test coverage**: Minimal unit tests (TabDestination enum only) - navigation behavior tested via E2E
- **E2E coverage**: All 6 acceptance scenarios from spec.md (primary test verification)
- **Android-only**: No backend, iOS, or web changes required
- **Test-First**: Write failing E2E tests before implementation
- **Given-When-Then**: All tests must follow GWT structure with descriptive names
- **Material Design 3**: Use NavigationBar component with Material icons
- **Configuration changes**: Handled automatically by rememberNavController
- **No persistence**: Tab state NOT saved across app restarts (always start on Home)

---

## Success Criteria

From spec.md:

- ✅ **SC-001**: 100% of implemented tab navigation items are functional and navigate to their intended destinations
- ✅ **SC-002**: Current tab is visually distinguishable from non-active tabs using Material Design active state
- ✅ **SC-003**: Tab navigation maintains usable layout and tap targets on screen widths from 320dp to 600dp+
- ✅ **SC-004**: Users can successfully switch between any two implemented tabs
- ✅ **SC-005**: Switching away from a tab and back restores the last visited screen within that tab
- ✅ **SC-006**: Tab selection and navigation stacks survive configuration changes (rotation, dark mode toggle)
- ✅ **SC-007**: Android back button navigates within current tab's back stack before allowing system to handle app exit

All success criteria will be validated in Phase 3 verification tasks (T031-T035) and Phase 4 final validation (T041-T042).

