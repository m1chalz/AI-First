---

description: "Task list for iOS MVVM-C Architecture implementation"
---

# Tasks: iOS MVVM-C Architecture Setup

**Input**: Design documents from `/specs/001-ios-mvvmc-architecture/`
**Prerequisites**: plan.md, spec.md, research.md, quickstart.md

**Tests**: Test requirements for this iOS-only feature:

**DEFERRED - ViewModel Unit Tests**:
- Location: `/iosApp/iosAppTests/Coordinators/`
- Coverage: N/A (coordinators have minimal logic, deferred to future features)
- Framework: XCTest with Swift Concurrency
- Rationale: This architecture feature establishes coordinator infrastructure only. ViewModels and their tests will be added when business logic is introduced.

**MANDATORY - End-to-End Tests**:
- Mobile (iOS): `/e2e-tests/mobile/specs/001-ios-mvvmc-architecture.spec.ts` (Appium + TypeScript)
- Coverage: All 3 user stories (splash screen, coordinator navigation, lifecycle)
- Use Screen Object Model pattern
- Verify: Navigation flow (splash → list), navigation bar styling, memory management, lifecycle transitions

**Organization**: Tasks are grouped by user story (US1-US3) from spec.md, following priority order P1→P2→P3.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

This feature uses iOS-only paths in `/iosApp/`:
- Coordinators: `/iosApp/iosApp/Coordinators/`
- Views: `/iosApp/iosApp/Views/`
- App lifecycle: `/iosApp/iosApp/` (AppDelegate, SceneDelegate)
- Tests: `/e2e-tests/mobile/specs/` and `/e2e-tests/mobile/screens/`

---

## Phase 1: Setup (Project Structure)

**Purpose**: Prepare iOS project for UIKit lifecycle and MVVM-C architecture

- [ ] T001 Review quickstart.md implementation guide in `/specs/001-ios-mvvmc-architecture/quickstart.md`
- [ ] T002 Create Coordinators directory in Xcode: `/iosApp/iosApp/Coordinators/`
- [ ] T003 [P] Create Views directory in Xcode: `/iosApp/iosApp/Views/`
- [ ] T004 [P] Move existing ContentView.swift to `/iosApp/iosApp/Views/ContentView.swift`
- [ ] T005 Verify Xcode project structure matches plan.md layout

---

## Phase 2: Foundational (Core Infrastructure)

**Purpose**: Establish coordinator protocol and base app lifecycle - BLOCKS all user stories

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T006 Create CoordinatorInterface protocol in `/iosApp/iosApp/Coordinators/CoordinatorInterface.swift`
- [ ] T007 Add SwiftDoc documentation to CoordinatorInterface: protocol purpose, start() method, navigationController property
- [ ] T008 Create AppDelegate with UIApplicationDelegate conformance in `/iosApp/iosApp/AppDelegate.swift`
- [ ] T009 Add @main attribute to AppDelegate class
- [ ] T010 Add SwiftDoc documentation to AppDelegate: app lifecycle management, scene configuration
- [ ] T011 Create SceneDelegate with UIWindowSceneDelegate conformance in `/iosApp/iosApp/SceneDelegate.swift`
- [ ] T012 Add SwiftDoc documentation to SceneDelegate: scene lifecycle, window setup, coordinator initialization
- [ ] T013 Delete `/iosApp/iosApp/iOSApp.swift` file (no longer needed with UIKit lifecycle)
- [ ] T014 Update Info.plist: Add UIApplicationSceneManifest with UISceneDelegateClassName pointing to SceneDelegate
- [ ] T015 Update Info.plist: Remove UISceneStoryboardFile key (no storyboards)
- [ ] T016 Verify project builds successfully after UIKit lifecycle migration

**Checkpoint**: Foundation ready - UIKit lifecycle established, coordinator protocol defined

---

## Phase 3: User Story 1 - Core Navigation Infrastructure (Priority: P1) 🎯 MVP

**Goal**: Establish fundamental navigation structure with splash screen and transition to main content

**Independent Test**: Launch app → splash screen displays 100px red circle on black background → transitions to ContentView → navigation bar is transparent green semi-transparent

### Implementation for User Story 1

**App Lifecycle & Window Setup**:
- [ ] T017 [US1] Implement scene(_:willConnectTo:options:) in SceneDelegate: create UIWindow with windowScene
- [ ] T018 [US1] Configure navigation bar appearance in SceneDelegate: transparent green semi-transparent using UINavigationBarAppearance
- [ ] T019 [US1] Apply navigation bar appearance to all appearance states (standardAppearance, compactAppearance, scrollEdgeAppearance)
- [ ] T020 [US1] Create UINavigationController in SceneDelegate
- [ ] T021 [US1] Set window.rootViewController to UINavigationController in SceneDelegate
- [ ] T022 [US1] Call window.makeKeyAndVisible() in SceneDelegate

**Splash Screen**:
- [ ] T023 [P] [US1] Create SplashScreenView SwiftUI view in `/iosApp/iosApp/Views/SplashScreenView.swift`
- [ ] T024 [US1] Implement splash screen UI: ZStack with black background and 100px red Circle
- [ ] T025 [US1] Add SwiftDoc documentation to SplashScreenView: initial loading screen purpose
- [ ] T026 [US1] Create UIHostingController wrapping SplashScreenView in SceneDelegate
- [ ] T027 [US1] Set splash screen UIHostingController as initial rootViewController in navigationController

**AppCoordinator**:
- [ ] T028 [US1] Create AppCoordinator class conforming to CoordinatorInterface in `/iosApp/iosApp/Coordinators/AppCoordinator.swift`
- [ ] T029 [US1] Add weak var navigationController property to AppCoordinator
- [ ] T030 [US1] Add SwiftDoc documentation to AppCoordinator: root coordinator managing app-level navigation
- [ ] T031 [US1] Implement start(animated:) method in AppCoordinator (will start ListScreenCoordinator)
- [ ] T032 [US1] Initialize AppCoordinator in SceneDelegate after window setup
- [ ] T033 [US1] Set AppCoordinator.navigationController to SceneDelegate's UINavigationController
- [ ] T034 [US1] Call await appCoordinator.start(animated: true) in Task block in SceneDelegate

**Checkpoint**: At this point, app launches with UIKit lifecycle and displays splash screen

---

## Phase 4: User Story 2 - Coordinator Protocol & Hierarchy (Priority: P2)

**Goal**: Implement coordinator hierarchy with ListScreenCoordinator presenting ContentView

**Independent Test**: After splash screen, ListScreenCoordinator transitions navigation to ContentView wrapped in UIHostingController → ContentView displays with "Click me!" button → navigation bar remains transparent green

### Implementation for User Story 2

**ListScreenCoordinator**:
- [ ] T035 [US2] Create ListScreenCoordinator class conforming to CoordinatorInterface in `/iosApp/iosApp/Coordinators/ListScreenCoordinator.swift`
- [ ] T036 [US2] Add weak var navigationController property to ListScreenCoordinator
- [ ] T037 [US2] Add SwiftDoc documentation to ListScreenCoordinator: manages list screen presentation
- [ ] T038 [US2] Implement start(animated:) method: create UIHostingController wrapping ContentView
- [ ] T039 [US2] Call navigationController.setViewControllers([hostingController], animated:) in ListScreenCoordinator.start()
- [ ] T040 [US2] Add guard statement in start() to handle nil navigationController with assertionFailure

**AppCoordinator Integration**:
- [ ] T041 [US2] Add lazy var listScreenCoordinator to AppCoordinator
- [ ] T042 [US2] Initialize ListScreenCoordinator in lazy var with self.navigationController assignment
- [ ] T043 [US2] Update AppCoordinator.start() to call await listScreenCoordinator.start(animated:)
- [ ] T044 [US2] Add SwiftDoc comment documenting sub-coordinator lazy initialization pattern

**Memory Management Verification**:
- [ ] T045 [US2] Verify all navigationController references are weak (CoordinatorInterface, AppCoordinator, ListScreenCoordinator)
- [ ] T046 [US2] Verify sub-coordinators use lazy var initialization (no children array)
- [ ] T047 [US2] Run in Xcode Instruments → Leaks to verify no retain cycles

**Checkpoint**: Complete navigation flow working (splash → ContentView), coordinator pattern established

---

## Phase 5: User Story 3 - Scene Lifecycle Management (Priority: P3)

**Goal**: Implement proper scene lifecycle handling for production-ready app state management

**Independent Test**: Background app (Home button) → Foreground app → verify no crashes or state corruption → disconnect scene → verify cleanup

### Implementation for User Story 3

**Scene Lifecycle Methods**:
- [ ] T048 [P] [US3] Implement sceneDidDisconnect(_:) in SceneDelegate: add comment for resource cleanup
- [ ] T049 [P] [US3] Implement sceneDidBecomeActive(_:) in SceneDelegate: add comment for activation handling
- [ ] T050 [P] [US3] Implement sceneWillResignActive(_:) in SceneDelegate: add comment for deactivation handling
- [ ] T051 [P] [US3] Implement sceneWillEnterForeground(_:) in SceneDelegate: add comment for foreground state restoration
- [ ] T052 [P] [US3] Implement sceneDidEnterBackground(_:) in SceneDelegate: add comment for state preservation
- [ ] T053 [US3] Add SwiftDoc documentation to each lifecycle method explaining when it's called and intended use

**AppDelegate Lifecycle**:
- [ ] T054 [P] [US3] Implement application(_:didDiscardSceneSessions:) in AppDelegate: add comment for scene session cleanup
- [ ] T055 [US3] Add SwiftDoc documentation to AppDelegate lifecycle methods

**Lifecycle Testing**:
- [ ] T056 [US3] Manual test: Launch app → background → foreground → verify no memory warnings
- [ ] T057 [US3] Manual test: Launch app → force quit → relaunch → verify clean initialization
- [ ] T058 [US3] Run in Xcode Instruments → Allocations to verify proper memory management during lifecycle transitions

**Checkpoint**: All scene lifecycle events properly handled, app ready for production

---

## Phase 6: End-to-End Testing

**Purpose**: Validate complete navigation flows and architecture implementation with E2E tests

### E2E Test Infrastructure

- [ ] T059 Setup iOS E2E test infrastructure if not exists: Appium config, TypeScript setup
- [ ] T060 Create base Screen Object for iOS in `/e2e-tests/mobile/screens/base/BaseScreen.ts`
- [ ] T061 [P] Create SplashScreen object in `/e2e-tests/mobile/screens/ios/SplashScreen.ts`
- [ ] T062 [P] Create ListScreen object in `/e2e-tests/mobile/screens/ios/ListScreen.ts`

### E2E Tests for All User Stories

- [ ] T063 [US1] Create E2E test spec in `/e2e-tests/mobile/specs/001-ios-mvvmc-architecture.spec.ts`
- [ ] T064 [US1] Write E2E test: Launch app → verify splash screen displayed (will need accessibility identifier)
- [ ] T065 [US2] Write E2E test: Wait for transition → verify ContentView displayed with "Click me!" button
- [ ] T066 [US1] Write E2E test: Verify navigation bar is visible (transparent green - visual check)
- [ ] T067 [US3] Write E2E test: Background app → foreground → verify app still responsive
- [ ] T068 Run iOS E2E tests: `npm run test:mobile:ios` from repo root
- [ ] T069 Verify all E2E tests pass

**Note**: Test identifiers (accessibilityIdentifier) are deferred - E2E tests will use alternative locators initially

---

## Phase 7: Polish & Documentation

**Purpose**: Final cleanup and documentation updates

- [ ] T070 [P] Add Preview providers to SplashScreenView for Xcode previews
- [ ] T071 [P] Update ContentView with accessibility identifiers (deferred but can add proactively)
- [ ] T072 Add architecture diagram to `/iosApp/README.md` showing coordinator hierarchy
- [ ] T073 Document coordinator pattern usage in `/iosApp/README.md`
- [ ] T074 Run quickstart.md validation: follow step-by-step guide and verify all steps work
- [ ] T075 Update main project README.md with iOS MVVM-C architecture section
- [ ] T076 [P] Code formatting: Run SwiftLint or Xcode format on all new Swift files
- [ ] T077 Final build verification: `xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' clean build`
- [ ] T078 Final memory leak check: Run Instruments → Leaks → navigate screens → verify no leaks

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational phase completion
- **User Story 2 (Phase 4)**: Depends on User Story 1 completion (needs AppCoordinator from US1)
- **User Story 3 (Phase 5)**: Depends on Foundational phase completion (can run in parallel with US2 if needed)
- **E2E Testing (Phase 6)**: Depends on all user stories completion
- **Polish (Phase 7)**: Depends on E2E testing completion

### User Story Dependencies

- **User Story 1 (P1)**: REQUIRED - establishes app lifecycle, window, navigation bar, splash screen, AppCoordinator
- **User Story 2 (P2)**: Depends on US1 - adds ListScreenCoordinator and coordinator hierarchy
- **User Story 3 (P3)**: Independent from US2 - can run after Foundational phase

### Within Each User Story

**User Story 1 (Core Navigation)**:
1. App lifecycle (AppDelegate, SceneDelegate) - sequential
2. Navigation bar configuration - sequential after window setup
3. Splash screen creation - parallel with AppCoordinator creation
4. AppCoordinator - requires Foundational (CoordinatorInterface)

**User Story 2 (Coordinator Hierarchy)**:
1. ListScreenCoordinator creation - requires US1 AppCoordinator
2. AppCoordinator integration - sequential after ListScreenCoordinator
3. Memory verification - final step

**User Story 3 (Lifecycle)**:
1. All lifecycle method implementations are parallelizable (different methods)
2. Documentation can be done in parallel with implementations
3. Testing must be sequential (requires complete implementation)

### Parallel Opportunities

**Phase 1 (Setup)**:
- T003 (Views directory) || T004 (move ContentView) can run in parallel with T002 (Coordinators directory)

**Phase 2 (Foundational)**:
- T008-T010 (AppDelegate) || T011-T012 (SceneDelegate) can be created in parallel
- T014-T015 (Info.plist updates) are independent

**Phase 3 (US1)**:
- T023-T025 (SplashScreenView) can run in parallel with T028-T031 (AppCoordinator initial structure)

**Phase 4 (US2)**:
- Memory verification tasks T045-T047 can run after T044 completes

**Phase 5 (US3)**:
- All lifecycle methods T048-T052 can be implemented in parallel
- T054-T055 (AppDelegate lifecycle) can run in parallel with SceneDelegate methods

**Phase 6 (E2E)**:
- T061-T062 (Screen Objects) can run in parallel
- E2E test writing T064-T067 can run in parallel (different test cases)

**Phase 7 (Polish)**:
- T070-T071 (Previews, accessibility) || T072-T073 (documentation) || T076 (formatting) all parallelizable

---

## Parallel Example: User Story 1

```bash
# These tasks can launch in parallel for US1:
Task T023: "Create SplashScreenView SwiftUI view"
Task T028: "Create AppCoordinator class"

# After both complete, these can run in parallel:
Task T024: "Implement splash screen UI"
Task T029-T031: "Add properties and methods to AppCoordinator"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (5 tasks, ~15 minutes)
2. Complete Phase 2: Foundational (11 tasks, ~45 minutes)
3. Complete Phase 3: User Story 1 (18 tasks, ~1.5 hours)
4. **STOP and VALIDATE**: 
   - Launch app on simulator
   - Verify splash screen appears (red circle)
   - Verify transition to ContentView
   - Verify green navigation bar
5. **MVP COMPLETE** - Basic navigation infrastructure working

**Time Estimate**: ~2.5 hours for MVP

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready (~1 hour)
2. Add User Story 1 → Test manually → **MVP Ready** (displays screens, navigation works)
3. Add User Story 2 → Test navigation flow → **Coordinator pattern complete**
4. Add User Story 3 → Test lifecycle → **Production-ready**
5. Add E2E tests → Automated validation → **Fully tested**
6. Add Polish → Documentation complete → **Ready to merge**

### Sequential Execution (Single Developer)

**Day 1**: Setup + Foundational + US1 (MVP)
- Morning: Phase 1-2 (foundation)
- Afternoon: Phase 3 (US1 implementation)
- End of day: Working app with splash → content navigation

**Day 2**: US2 + US3 + E2E (Production-ready)
- Morning: Phase 4 (coordinator hierarchy)
- Afternoon: Phase 5 (lifecycle) + Phase 6 (E2E tests)

**Day 3**: Polish + Documentation
- Morning: Phase 7 (final cleanup)
- Afternoon: Review, merge preparation

### Parallel Team Strategy

With 2-3 developers after Foundational phase completes:

**Developer A**: User Story 1 (P1 - Core Navigation)
- Focus: App lifecycle, splash screen, navigation bar, AppCoordinator

**Developer B**: User Story 3 (P3 - Scene Lifecycle)
- Focus: Lifecycle methods (independent from US1/US2)
- Can start immediately after Foundational

**Developer A** (after US1 complete): User Story 2 (P2 - Coordinator Hierarchy)
- Focus: ListScreenCoordinator, integration

**Developer C** (anytime): E2E test infrastructure setup
- Focus: Screen Objects, test spec structure

---

## Success Criteria Validation

After implementation, verify all success criteria from spec.md:

- **SC-001**: ✅ Navigation bar appears with transparent green semi-transparent styling immediately upon display
  - **Test**: Launch app, observe navigation bar color and transparency
  
- **SC-002**: ✅ App supports standard iOS scene lifecycle events (background, foreground, disconnect) without crashes or memory leaks
  - **Test**: Background/foreground app multiple times, check console for crashes
  
- **SC-003**: ✅ All coordinators properly manage memory with no retain cycles detectable in Instruments
  - **Test**: Run Instruments → Leaks, navigate screens, verify no leaks
  
- **SC-004**: ✅ Navigation controller hierarchy can be inspected to verify proper UIHostingController wrapping of SwiftUI views
  - **Test**: Debug View Hierarchy in Xcode, verify UINavigationController → UIHostingController → SwiftUI views

---

## Notes

- **[P] tasks**: Different files, no dependencies, can run in parallel
- **[Story] label**: Maps task to specific user story (US1, US2, US3)
- **iOS-only feature**: No Android, Web, or Shared module tasks
- **Test identifiers deferred**: E2E tests will use alternative locators initially
- **ViewModel tests deferred**: Minimal coordinator logic, tests will be added with business logic
- **Commit strategy**: Commit after each phase or logical group (e.g., after AppCoordinator complete)
- **Stop at any checkpoint**: Each phase delivers independently validatable increment
- **Reference quickstart.md**: Use as implementation guide for code examples

---

## Task Count Summary

- **Phase 1 (Setup)**: 5 tasks
- **Phase 2 (Foundational)**: 11 tasks
- **Phase 3 (US1)**: 18 tasks
- **Phase 4 (US2)**: 13 tasks
- **Phase 5 (US3)**: 11 tasks
- **Phase 6 (E2E Testing)**: 11 tasks
- **Phase 7 (Polish)**: 9 tasks

**Total**: 78 tasks

**Parallelizable**: 28 tasks marked [P]

**MVP Scope**: Phases 1-3 (34 tasks, ~2.5 hours)

**Production-Ready**: Phases 1-5 (58 tasks, ~5-6 hours)

**Fully Complete**: All phases (78 tasks, ~8-10 hours including testing and documentation)

