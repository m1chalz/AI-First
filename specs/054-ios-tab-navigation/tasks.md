---
description: "Actionable task list for iOS tab navigation implementation"
---

# Tasks: iOS Tab Navigation

**Input**: Design documents from `/specs/054-ios-tab-navigation/`
**Prerequisites**: plan.md (required), spec.md (required for user stories)

**Tests**: Test requirements for this project:

**MANDATORY - iOS Unit Tests**:
- Location: `/iosApp/iosAppTests/` (XCTest with Swift Concurrency), 80% coverage
- Scope: Coordinators (TabCoordinator, PlaceholderCoordinator), ViewModels (PlaceholderViewModel)
- Run: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- Convention: MUST follow Given-When-Then structure with descriptive camelCase_with_underscores names

**INTENTIONAL CONSTITUTION VIOLATION - End-to-End Tests (Principle XII)**:
- Decision: We will NOT add E2E tests in this feature.
- Rationale: Placeholder-only content; unit tests + manual validation are deemed sufficient for now.
- Follow-up: Add E2E coverage when tab content features are implemented.

**Organization**: Tasks are grouped by implementation phase following the natural dependency order from plan.md. Since this is infrastructure (navigation), tasks are organized by technical layers rather than user stories, but all work toward delivering User Story 1 (P1): Navigate to Portal Sections Using Tabs.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1 for all tasks in this feature)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Project Structure)

**Purpose**: Prepare project structure and localization infrastructure

- [ ] T001 Create placeholder feature directory structure in `/iosApp/iosApp/Features/Placeholder/Views/`
- [ ] T002 [P] Verify SwiftGen is installed and configured for localization string generation

---

## Phase 2: Foundational (Localization)

**Purpose**: Add localization strings needed for tab navigation and placeholder screens

**⚠️ CRITICAL**: Complete before any implementation work begins

- [ ] T003 Add tab navigation strings to `/iosApp/iosApp/Resources/en.lproj/Localizable.strings` (tabs.home, tabs.lostPet, tabs.foundPet, tabs.contactUs, tabs.account)
- [ ] T004 Add placeholder screen strings to `/iosApp/iosApp/Resources/en.lproj/Localizable.strings` (placeholder.title, placeholder.message)
- [ ] T005 Add tab navigation strings to `/iosApp/iosApp/Resources/pl.lproj/Localizable.strings` (Polish translations)
- [ ] T006 Add placeholder screen strings to `/iosApp/iosApp/Resources/pl.lproj/Localizable.strings` (Polish translations)
- [ ] T007 Run SwiftGen to regenerate `/iosApp/iosApp/Generated/Strings.swift` with new localization keys

**Checkpoint**: Localization ready - implementation can now begin

---

## Phase 3: User Story 1 - Navigate to Portal Sections Using Tabs (Priority: P1) 🎯 MVP

**Goal**: Users can use the tab navigation system to access different sections of the portal (browse lost pets, browse found pets, contact support, or manage account/login). The navigation is available from any screen in the application.

**Independent Test**: Launch app → verify 5 tabs appear in tab bar → tap each tab → verify navigation to correct screen (Lost Pet shows announcements list, other tabs show "Coming soon" placeholder)

### Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**iOS Unit Tests**:
- [ ] T008 [P] [US1] Create unit test file `/iosApp/iosAppTests/Features/Placeholder/Views/PlaceholderViewModelTests.swift`
- [ ] T009 [P] [US1] Test: PlaceholderViewModel initializes with correct title in PlaceholderViewModelTests.swift (Given-When-Then structure)
- [ ] T010 [P] [US1] Test: PlaceholderViewModel exposes correct localized message in PlaceholderViewModelTests.swift (Given-When-Then structure)
- [ ] T011 [P] [US1] Create unit test file `/iosApp/iosAppTests/Coordinators/TabCoordinatorTests.swift`
- [ ] T012 [P] [US1] Test: TabCoordinator init creates 5 child coordinators (stored in internal array) in TabCoordinatorTests.swift (Given-When-Then structure)
- [ ] T013 [P] [US1] Test: Each child coordinator has non-nil UINavigationController after init in TabCoordinatorTests.swift (Given-When-Then structure)
- [ ] T014 [P] [US1] Test: tabBarController computed property returns UITabBarController with 5 view controllers (navigation controllers) in TabCoordinatorTests.swift (Given-When-Then structure)
- [ ] T015 [P] [US1] Test: Tab bar items have correct titles (localized) in TabCoordinatorTests.swift (Given-When-Then structure)
- [ ] T016 [P] [US1] Test: Tab bar items have correct SF Symbols icons in TabCoordinatorTests.swift (Given-When-Then structure)
- [ ] T017 [P] [US1] Test: Accessibility identifiers set correctly on tab bar items in TabCoordinatorTests.swift (Given-When-Then structure)
- [ ] T018 [P] [US1] Test: Child coordinators started successfully when start() is called in TabCoordinatorTests.swift (Given-When-Then structure, async/await)

### Implementation for User Story 1

**iOS Implementation (MVVM-C Architecture)**:

#### Placeholder Feature (Views + ViewModel + Coordinator)

- [ ] T019 [P] [US1] Create PlaceholderViewModel in `/iosApp/iosApp/Features/Placeholder/Views/PlaceholderViewModel.swift` (ObservableObject with @Published var title: String)
- [ ] T020 [P] [US1] Create PlaceholderView in `/iosApp/iosApp/Features/Placeholder/Views/PlaceholderView.swift` (SwiftUI view observing PlaceholderViewModel)
- [ ] T021 [US1] Add SF Symbol clock.fill icon to PlaceholderView
- [ ] T022 [US1] Add test identifiers to PlaceholderView (placeholder.comingSoon.icon, placeholder.comingSoon.message)
- [ ] T023 [US1] Create PlaceholderCoordinator in `/iosApp/iosApp/Coordinators/PlaceholderCoordinator.swift` (root coordinator pattern: creates own UINavigationController in init(title:))
- [ ] T024 [US1] Implement PlaceholderCoordinator.init(title:) to create UINavigationController and set as navigationController property
- [ ] T025 [US1] Implement PlaceholderCoordinator.start(animated:) async to create PlaceholderViewModel, PlaceholderView, wrap in UIHostingController (@MainActor)
- [ ] T026 [P] [US1] Add SwiftDoc documentation to PlaceholderViewModel public methods (skip self-explanatory properties)
- [ ] T027 [P] [US1] Add SwiftDoc documentation to PlaceholderCoordinator public methods documenting root coordinator pattern

#### Tab Coordinator (UITabBarController Management)

- [ ] T028 [US1] Create TabCoordinator in `/iosApp/iosApp/Coordinators/TabCoordinator.swift` (manages UITabBarController, does NOT conform to CoordinatorInterface)
- [ ] T029 [US1] Implement TabCoordinator.init() to create UITabBarController instance
- [ ] T030 [US1] Create Home tab child coordinator (PlaceholderCoordinator with L10n.Tabs.home title) in TabCoordinator.init()
- [ ] T031 [US1] Create Lost Pet tab child coordinator (AnnouncementListCoordinator with own UINavigationController) in TabCoordinator.init()
- [ ] T032 [US1] Create Found Pet tab child coordinator (PlaceholderCoordinator with L10n.Tabs.foundPet title) in TabCoordinator.init()
- [ ] T033 [US1] Create Contact Us tab child coordinator (PlaceholderCoordinator with L10n.Tabs.contactUs title) in TabCoordinator.init()
- [ ] T034 [US1] Create Account tab child coordinator (PlaceholderCoordinator with L10n.Tabs.account title) in TabCoordinator.init()
- [ ] T035 [US1] Retrieve UINavigationController from each child coordinator with guard let navigation = coordinator.navigationController else { fatalError("Coordinator must have navigationController") } in TabCoordinator.init()
- [ ] T036 [US1] Configure tab bar items on navigation controllers with SF Symbols icons and localized titles in TabCoordinator.init()
- [ ] T037 [US1] Set accessibility identifiers on tab bar items (tabs.home, tabs.lostPet, tabs.foundPet, tabs.contactUs, tabs.account) in TabCoordinator.init()
- [ ] T038 [US1] Set navigation controllers as tabBarController.viewControllers in TabCoordinator.init()
- [ ] T039 [US1] Store child coordinators in childCoordinators array (strong references) in TabCoordinator.init()
- [ ] T040 [US1] Configure tab bar appearance (#FAFAFA background, #808080 inactive, #FF6B35 active) in TabCoordinator.init()
- [ ] T041 [US1] Add computed property var tabBarController: UITabBarController { get } to expose UITabBarController
- [ ] T042 [US1] Implement TabCoordinator.start(animated:) async to start all child coordinators (@MainActor)
- [ ] T043 [P] [US1] Add SwiftDoc documentation to TabCoordinator public methods documenting synchronous init and asynchronous start

#### Update AnnouncementListCoordinator (Root Coordinator Pattern)

- [ ] T044 [US1] Update AnnouncementListCoordinator in `/iosApp/iosApp/Features/AnnouncementList/Coordinators/AnnouncementListCoordinator.swift` to change init(navigationController:) to no-argument init()
- [ ] T045 [US1] Create own UINavigationController in AnnouncementListCoordinator.init() and set navigationController property (CoordinatorInterface conformance preserved)
- [ ] T046 [P] [US1] Add SwiftDoc documentation to AnnouncementListCoordinator documenting root coordinator pattern change (creates own UINavigationController for tab-based navigation)

#### Update AppCoordinator (Remove NavigationController Dependency)

- [ ] T047 [US1] Remove CoordinatorInterface conformance from AppCoordinator in `/iosApp/iosApp/Coordinators/AppCoordinator.swift` (AppCoordinator manages TabCoordinator, not UINavigationController)
- [ ] T048 [US1] Remove init(navigationController:) parameter from AppCoordinator, create no-argument init()
- [ ] T049 [US1] Create TabCoordinator in AppCoordinator.init() and store as private property
- [ ] T050 [US1] Implement AppCoordinator.start(animated:) async to start TabCoordinator (@MainActor)
- [ ] T051 [US1] Add computed property var tabBarController: UITabBarController { get } to expose TabCoordinator's tabBarController
- [ ] T052 [P] [US1] Add SwiftDoc documentation to AppCoordinator documenting tab navigation setup and why it doesn't conform to CoordinatorInterface

#### Update SceneDelegate (Direct Tab Bar Launch)

- [ ] T054 [US1] Remove splash screen setup from SceneDelegate.swift in `/iosApp/iosApp/SceneDelegate.swift`
- [ ] T055 [US1] Update SceneDelegate.scene(_:willConnectTo:options:) to create window
- [ ] T056 [US1] Create AppCoordinator with init() in SceneDelegate (creates complete UITabBarController structure synchronously)
- [ ] T057 [US1] Set window.rootViewController to AppCoordinator.tabBarController (computed property) in SceneDelegate
- [ ] T058 [US1] Call window.makeKeyAndVisible() in SceneDelegate
- [ ] T059 [US1] Start AppCoordinator asynchronously in Task { @MainActor in await coordinator.start() } in SceneDelegate

#### Detail Screen Tab Bar Hiding

- [ ] T060 [US1] Update PetDetailsCoordinator in `/iosApp/iosApp/Features/PetDetails/Coordinators/PetDetailsCoordinator.swift` to set hidesBottomBarWhenPushed = true before push

#### Verification

- [ ] T061 [US1] Run iOS unit tests with `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
- [ ] T062 [US1] Verify 80% line + branch coverage for TabCoordinator in Xcode coverage report
- [ ] T063 [US1] Verify 80% line + branch coverage for PlaceholderViewModel in Xcode coverage report
- [ ] T064 [US1] Build iOS app with `xcodebuild -scheme iosApp -sdk iphonesimulator -configuration Debug`
- [ ] T065 [US1] Verify no build errors or warnings introduced

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: Polish & Documentation

**Purpose**: Final documentation and cleanup

- [ ] T066 [P] Update documentation in plan.md if implementation revealed new insights
- [ ] T067 [P] Document root coordinator vs sub-coordinator distinction in SwiftDoc comments
- [ ] T068 [P] Document tab bar appearance configuration in SwiftDoc comments
- [ ] T069 [P] Document navigation stack isolation (independent per tab) in SwiftDoc comments
- [ ] T070 [P] Document PlaceholderView reusability for future unimplemented features in SwiftDoc comments

---

## Phase 5: Manual Testing & Validation

**Purpose**: Comprehensive manual testing to validate all success metrics

- [ ] T071 [US1] Manual test: Launch app and verify it opens directly to tab bar (no splash screen)
- [ ] T072 [US1] Manual test: Verify all 5 tabs appear in tab bar with correct icons and labels
- [ ] T073 [US1] Manual test: Tap Home tab and verify "Coming soon" placeholder screen displays
- [ ] T074 [US1] Manual test: Tap Lost Pet tab and verify announcement list screen displays
- [ ] T075 [US1] Manual test: Tap Found Pet tab and verify "Coming soon" placeholder screen displays
- [ ] T076 [US1] Manual test: Tap Contact Us tab and verify "Coming soon" placeholder screen displays
- [ ] T077 [US1] Manual test: Tap Account tab and verify "Coming soon" placeholder screen displays (authentication not yet implemented)
- [ ] T078 [US1] Manual test: Verify tab bar visible on root screens (all 5 tabs)
- [ ] T079 [US1] Manual test: Navigate to Pet Details screen and verify tab bar hidden (hidesBottomBarWhenPushed)
- [ ] T080 [US1] Manual test: Switch from Lost Pet to Home tab, back to Lost Pet, verify navigation state preserved (back stack)
- [ ] T081 [US1] Manual test: Navigate deep into Lost Pet (list → details), switch to another tab, return to Lost Pet, verify back stack preserved
- [ ] T082 [US1] Manual test: Force quit app, relaunch, verify app returns to Home tab (no persistence, fresh state)
- [ ] T083 [US1] Manual test: Verify current tab visually highlighted/selected (iOS native tab bar appearance)
- [ ] T084 [US1] Manual test: Test on iPhone (various sizes) and iPad to verify tab bar layout
- [ ] T085 [US1] Manual test: Test with VoiceOver enabled to verify accessibility identifiers work correctly

---

## Dependencies & Execution Order

### External Feature Dependencies

**Authentication System** (Future Feature):
- FR-006 and FR-007 depend on authentication system (not yet implemented)
- Auth system will provide:
  - User authentication state determination (logged in / logged out)
  - Login screen / form
  - Account screen with "Log out" functionality
- Tab navigation provides infrastructure only: Account tab displays placeholder "Coming soon" until auth feature is implemented
- Auth feature will replace PlaceholderCoordinator for Account tab with actual auth coordinator

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all implementation work
- **User Story 1 (Phase 3)**: Depends on Foundational phase completion
  - Tests MUST be written first and FAIL before implementation
  - Within implementation: Placeholder feature → TabCoordinator → Update AnnouncementListCoordinator → Update AppCoordinator → Update SceneDelegate → Detail screen hiding
- **Polish (Phase 4)**: Depends on Phase 3 implementation completion
- **Manual Testing (Phase 5)**: Depends on Phase 3 implementation completion

### Within User Story 1 Implementation

1. **Tests First** (T008-T018): Write all unit tests, verify they fail
2. **Placeholder Feature** (T019-T027): PlaceholderViewModel → PlaceholderView → PlaceholderCoordinator → Documentation
3. **TabCoordinator** (T028-T043): Create TabCoordinator → Create child coordinators → Configure tab bar → Documentation
4. **Update Coordinators** (T044-T053): AnnouncementListCoordinator → AppCoordinator → Documentation
5. **Update SceneDelegate** (T054-T059): Remove splash → Wire up tab bar
6. **Detail Screen** (T060): Hide tab bar on push
7. **Verification** (T061-T065): Run tests → Check coverage → Build

### Parallel Opportunities

- **Phase 1**: All setup tasks can run in parallel (T001-T002)
- **Phase 2**: Localization tasks can run in parallel (T003-T006), then run SwiftGen (T007)
- **Tests (T008-T018)**: All test creation tasks marked [P] can run in parallel (T008, T009, T010, T011, T012, T013, T014, T015, T016, T017, T018)
- **Placeholder Implementation (T019-T027)**: T019 (ViewModel) and T020 (View) can be worked on in parallel, T023-T025 (Coordinator) depends on both, T026-T027 (Documentation) can run in parallel
- **TabCoordinator Setup (T028-T043)**: T030-T034 (create child coordinators) can conceptually be parallel once T029 completes, but in practice are sequential in init(). T043 (documentation) can run in parallel with other tasks
- **Coordinator Updates (T044-T053)**: T044-T046 (AnnouncementListCoordinator) and T047-T053 (AppCoordinator) depend on TabCoordinator completion, but documentation tasks (T046, T053) can run in parallel
- **Phase 4**: All documentation tasks (T066-T070) can run in parallel
- **Phase 5**: Manual tests must run sequentially as they build on each other

---

## Parallel Example: Test Creation for User Story 1

```bash
# Launch all unit test file creation and test implementations together:
Task T008: "Create unit test file PlaceholderViewModelTests.swift"
Task T009: "Test: PlaceholderViewModel initializes with correct title"
Task T010: "Test: PlaceholderViewModel exposes correct localized message"
Task T011: "Create unit test file TabCoordinatorTests.swift"
Task T012: "Test: TabCoordinator init creates 5 child coordinators"
Task T013: "Test: Each child coordinator creates own UINavigationController"
Task T014: "Test: TabBarController has 5 view controllers"
Task T015: "Test: Tab bar items have correct titles"
Task T016: "Test: Tab bar items have correct SF Symbols icons"
Task T017: "Test: Accessibility identifiers set correctly"
Task T018: "Test: Child coordinators started successfully"
```

---

## Implementation Strategy

### Sequential Delivery (Recommended for Solo Developer)

1. Complete Phase 1: Setup → Foundation structure ready
2. Complete Phase 2: Foundational (localization) → CRITICAL - blocks all implementation
3. Complete Phase 3: User Story 1
   - Write tests first (T008-T018) → All tests FAIL
   - Implement Placeholder feature (T019-T027)
   - Implement TabCoordinator (T028-T043)
   - Update existing coordinators (T044-T053)
   - Update SceneDelegate (T054-T059)
   - Update detail screen (T060)
   - Verify tests pass and coverage (T061-T065)
4. Complete Phase 4: Polish → Documentation complete
5. Complete Phase 5: Manual Testing → VALIDATE independently
6. Deploy/demo

### Feature Validation

After Phase 3 completion:
- Run all unit tests → Verify 80% coverage
- Build app → Verify no errors
- Launch app → Verify tab bar appears
- Test each tab → Verify navigation works
- Test navigation preservation → Verify back stacks maintained
- Test app restart → Verify returns to Home tab

---

## Success Metrics

All tasks in this file work toward achieving these success metrics from plan.md:

- [x] App launches directly to tab bar (no splash screen) - T054-T059, T071
- [x] All 5 tabs visible and interactive - T028-T043, T072
- [x] Tapping each tab navigates to correct destination - T030-T034, T073-T077
- [x] Lost Pet tab shows existing announcement list - T031, T074
- [x] Other tabs show placeholder "Coming soon" screens - T030, T032-T034, T073, T075-T077
- [x] Tab bar visible on root screens, hidden on detail screens - T060, T078-T079
- [x] Each tab maintains independent navigation stack - T035-T038, T080-T081
- [x] App restart returns to Home tab (first tab selected, no persistence) - T082
- [x] AppCoordinator no longer depends on external UINavigationController and doesn't conform to CoordinatorInterface (manages TabCoordinator instead) - T047-T052
- [x] TabCoordinator doesn't conform to CoordinatorInterface (manages UITabBarController, not UINavigationController) - T028
- [x] 80% unit test coverage for TabCoordinator - T008-T018, T061-T063
- [x] Build succeeds without errors - T064-T065
- [x] No linter violations introduced - Verified during build (T064)

---

## Notes

- [P] tasks = different files, no dependencies, can run in parallel
- [US1] label maps all tasks to User Story 1 (single story for this infrastructure feature)
- Tests MUST be written first and FAIL before implementation (TDD workflow)
- Each checkpoint validates independent functionality
- Commit after each logical group of tasks
- This is iOS-only implementation - Android and Web will have separate feature specs
- E2E tests intentionally omitted in this feature (Principle XII violation); add E2E coverage when tab content features are implemented
- Architecture: TabCoordinator and AppCoordinator do NOT conform to CoordinatorInterface (manage different container types)
  - TabCoordinator manages UITabBarController (exposes via computed property)
  - AppCoordinator manages TabCoordinator (exposes tabBarController via computed property)
- Root coordinator pattern: PlaceholderCoordinator and AnnouncementListCoordinator create own UINavigationController and conform to CoordinatorInterface
- Sub-coordinator pattern: PetDetailsCoordinator receives navigationController from parent (existing pattern preserved)

