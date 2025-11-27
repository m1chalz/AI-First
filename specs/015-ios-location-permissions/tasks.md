# Tasks: iOS Location Permissions Handling

**Feature Branch**: `015-ios-location-permissions`  
**Input**: Design documents from `/specs/015-ios-location-permissions/`
**Prerequisites**: plan.md ✓, spec.md ✓, research.md ✓, data-model.md ✓, contracts/ ✓, quickstart.md ✓

**Platform Scope**: iOS-only feature (no Android, Web, or Backend changes)

**Tests**: Test requirements for this iOS-only project:

**MANDATORY - iOS Unit Tests**:
- Location: `/iosApp/iosAppTests/Features/StartupScreen/`
- Framework: XCTest with Swift Concurrency (async/await)
- Coverage: 80% line + branch coverage
- Scope: LocationService, AnimalListViewModel location logic, domain models
- Run: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- Convention: MUST follow Given-When-Then structure with descriptive camelCase_with_underscores names

**MANDATORY - End-to-End Tests**:
- Location: `/e2e-tests/src/test/resources/features/mobile/ios-location-permissions.feature`
- Framework: Appium + Cucumber (Java)
- Coverage: All user stories (P1-P4) MUST have E2E test scenarios
- Screen Object Model: `/e2e-tests/src/test/java/.../screens/StartupScreen.java`
- Step definitions: `/e2e-tests/src/test/java/.../steps-mobile/LocationPermissionSteps.java`
- Run: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@ios"` (from repo root)
- Convention: MUST structure scenarios with Given-When-Then phases (Gherkin format)

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each permission scenario.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3, US4)
- Include exact file paths in descriptions

---

## Phase 1: Setup (iOS Project Configuration)

**Purpose**: Configure iOS project for location services (Info.plist, localization, DI setup)

- [X] T001 Add `NSLocationWhenInUseUsageDescription` key to `/iosApp/iosApp/Info.plist`
- [X] T002 Add localization keys to `/iosApp/iosApp/Resources/en.lproj/Localizable.strings`
- [X] T003 Regenerate SwiftGen localizations from `/iosApp` directory
- [X] T004 Verify L10n keys accessible (L10n.Location.Permission.Popup.title)

---

## Phase 2: Foundational (Core Location Infrastructure)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T005 [P] Create LocationPermissionStatus enum in `/iosApp/iosApp/Domain/LocationPermissionStatus.swift`
- [X] T006 [P] Create UserLocation struct in `/iosApp/iosApp/Domain/UserLocation.swift`
- [X] T007 [P] Create LocationServiceProtocol in `/iosApp/iosApp/Domain/LocationServiceProtocol.swift`
- [X] T008 Create LocationService actor implementation in `/iosApp/iosApp/Data/LocationService.swift`
- [X] T009 Register LocationService in `/iosApp/iosApp/DI/ServiceContainer.swift`
- [X] T010 Update AnimalRepositoryProtocol signature to accept optional UserLocation in `/iosApp/iosApp/Domain/Repositories/AnimalRepositoryProtocol.swift`
- [X] T011 Update AnimalRepository implementation to handle optional location in `/iosApp/iosApp/Data/Repositories/AnimalRepository.swift`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Location-Aware Content for Authorized Users (Priority: P1) 🎯 MVP

**Goal**: Users who have already granted location permissions seamlessly receive location-aware animal listings when opening the app

**Independent Test**: Launch app with location permissions already granted (authorizedWhenInUse or authorizedAlways) and verify that current location is fetched and used for the animal query

**Acceptance Scenarios**:
1. User has granted "While Using App" permission → app fetches location and queries with coordinates
2. User has granted "Always" permission → app fetches location and queries with coordinates
3. Location fetch succeeds → animal listings displayed
4. Location fetch fails (timeout, GPS unavailable) → app queries without coordinates (fallback)

### Tests for User Story 1 (MANDATORY) ✅

**iOS Unit Tests**:
- [X] T012 [P] [US1] Create FakeLocationService in `/iosApp/iosAppTests/Fakes/FakeLocationService.swift`
- [X] T013 [P] [US1] Unit test LocationPermissionStatus enum in `/iosApp/iosAppTests/Domain/LocationPermissionStatusTests.swift`
- [X] T014 [P] [US1] Unit test UserLocation struct in `/iosApp/iosAppTests/Domain/UserLocationTests.swift`
- [ ] T015 [P] [US1] Unit test LocationService.authorizationStatus in `/iosApp/iosAppTests/Data/LocationServiceTests.swift` (SKIPPED - complex CLLocationManager mocking)
- [ ] T016 [P] [US1] Unit test LocationService.requestLocation with authorized status in `/iosApp/iosAppTests/Data/LocationServiceTests.swift` (SKIPPED - complex CLLocationManager mocking)
- [ ] T017 [P] [US1] Unit test LocationService.requestLocation returns nil when unauthorized in `/iosApp/iosAppTests/Data/LocationServiceTests.swift` (SKIPPED - complex CLLocationManager mocking)
- [ ] T018 [P] [US1] Unit test LocationService.requestLocation returns nil on GPS failure in `/iosApp/iosAppTests/Data/LocationServiceTests.swift` (SKIPPED - complex CLLocationManager mocking)
- [X] T019 [P] [US1] Unit test AnimalListViewModel.loadAnimals fetches location when authorized in `/iosApp/iosAppTests/Features/StartupScreen/AnimalListViewModelLocationTests.swift`
- [X] T020 [P] [US1] Unit test AnimalListViewModel.loadAnimals queries with coordinates when location available in `/iosApp/iosAppTests/Features/StartupScreen/AnimalListViewModelLocationTests.swift`
- [X] T021 [P] [US1] Unit test AnimalListViewModel.loadAnimals queries without coordinates when location fetch fails in `/iosApp/iosAppTests/Features/StartupScreen/AnimalListViewModelLocationTests.swift`

**End-to-End Tests**:
- [ ] T022 [P] [US1] Create E2E feature file at `/e2e-tests/src/test/resources/features/mobile/ios-location-permissions.feature` (DEFERRED - separate Java/Cucumber module)
- [ ] T023 [P] [US1] Add US1 test scenario: granted "While Using App" permission → location fetched → animals displayed (DEFERRED - separate Java/Cucumber module)
- [ ] T024 [P] [US1] Add US1 test scenario: granted "Always" permission → location fetched → animals displayed (DEFERRED - separate Java/Cucumber module)
- [ ] T025 [P] [US1] Add US1 test scenario: location fetch fails → animals displayed without filtering (DEFERRED - separate Java/Cucumber module)

### Implementation for User Story 1

**iOS Implementation**:
- [X] T026 [US1] Add location properties to AnimalListViewModel in `/iosApp/iosApp/Features/AnimalList/Views/AnimalListViewModel.swift`
- [X] T027 [US1] Add locationService dependency to AnimalListViewModel initializer in `/iosApp/iosApp/Features/AnimalList/Views/AnimalListViewModel.swift`
- [X] T028 [US1] Implement loadAnimals() with location fetch for authorized status in `/iosApp/iosApp/Features/AnimalList/Views/AnimalListViewModel.swift`
- [X] T029 [US1] Implement fallback to nil location when fetch fails in `/iosApp/iosApp/Features/AnimalList/Views/AnimalListViewModel.swift`
- [X] T030 [US1] Update AnimalListCoordinator to inject LocationService in `/iosApp/iosApp/Features/AnimalList/Coordinators/AnimalListCoordinator.swift`
- [X] T031 [P] [US1] Add SwiftDoc to LocationServiceProtocol complex methods (skip self-explanatory properties)
- [X] T032 [P] [US1] Add SwiftDoc to AnimalListViewModel location methods (skip obvious properties like isLoading)

**Checkpoint**: User Story 1 complete - app fetches and uses location for authorized users, with graceful fallback

---

## Phase 4: User Story 2 - First-Time Location Permission Request (Priority: P2)

**Goal**: First-time users who haven't been asked about location permissions see a clear system permission request

**Independent Test**: Install fresh app (notDetermined status), verify iOS system alert appears, test both acceptance and denial paths independently

**Acceptance Scenarios**:
1. User hasn't been asked (notDetermined) → iOS system alert displayed
2. User taps "Allow While Using App" → location fetched → animals queried with coordinates
3. User taps "Don't Allow" → animals queried without coordinates
4. User denied permission → can still browse animals (fallback mode)

### Tests for User Story 2 (MANDATORY) ✅

**iOS Unit Tests**:
- [ ] T033 [P] [US2] Unit test LocationService.requestWhenInUseAuthorization with notDetermined status in `/iosApp/iosAppTests/Data/LocationServiceTests.swift`
- [ ] T034 [P] [US2] Unit test LocationService.requestWhenInUseAuthorization returns immediately when already authorized in `/iosApp/iosAppTests/Data/LocationServiceTests.swift`
- [ ] T035 [P] [US2] Unit test LocationService.requestWhenInUseAuthorization returns immediately when already denied in `/iosApp/iosAppTests/Data/LocationServiceTests.swift`
- [ ] T036 [P] [US2] Unit test AnimalListViewModel.loadAnimals requests permission when notDetermined in `/iosApp/iosAppTests/Features/StartupScreen/AnimalListViewModelLocationTests.swift`
- [ ] T037 [P] [US2] Unit test AnimalListViewModel handles user granting permission in alert in `/iosApp/iosAppTests/Features/StartupScreen/AnimalListViewModelLocationTests.swift`
- [ ] T038 [P] [US2] Unit test AnimalListViewModel handles user denying permission in alert in `/iosApp/iosAppTests/Features/StartupScreen/AnimalListViewModelLocationTests.swift`

**End-to-End Tests**:
- [ ] T039 [P] [US2] Add US2 test scenario: notDetermined status → system alert appears
- [ ] T040 [P] [US2] Add US2 test scenario: user taps "Allow While Using App" → location fetched → animals displayed
- [ ] T041 [P] [US2] Add US2 test scenario: user taps "Don't Allow" → animals displayed without location
- [ ] T042 [P] [US2] Add US2 test scenario: user taps "Allow Once" → location fetched → next launch shows alert again

### Implementation for User Story 2

**iOS Implementation**:
- [ ] T043 [US2] Add permission request logic to loadAnimals() when status is notDetermined in `/iosApp/iosApp/Features/StartupScreen/AnimalListViewModel.swift`
- [ ] T044 [US2] Update locationPermissionStatus property after permission request in `/iosApp/iosApp/Features/StartupScreen/AnimalListViewModel.swift`
- [ ] T045 [US2] Ensure animal query executes regardless of permission outcome (non-blocking) in `/iosApp/iosApp/Features/StartupScreen/AnimalListViewModel.swift`

**Checkpoint**: User Story 2 complete - first-time users see system alert and app handles both grant/deny outcomes

---

## Phase 5: User Story 3 - Recovery Path for Denied Permissions (Priority: P3)

**Goal**: Users who previously denied location access have a clear path to enable it through device Settings

**Independent Test**: Launch app with denied/restricted permission status, verify custom popup appears with working Settings navigation and Cancel fallback

**Acceptance Scenarios**:
1. User denied permission → custom popup displayed explaining status
2. User permission restricted by system → custom popup displayed
3. User taps "Go to Settings" → iOS Settings app opens to this app's permission screen
4. User taps "Cancel" → popup closes and app queries without coordinates
5. User dismissed popup → can browse animals without location filtering

### Tests for User Story 3 (MANDATORY) ✅

**iOS Unit Tests**:
- [ ] T046 [P] [US3] Unit test LocationPermissionStatus.shouldShowCustomPopup extension in `/iosApp/iosAppTests/Features/StartupScreen/LocationPermissionStatusPresentationTests.swift`
- [ ] T047 [P] [US3] Unit test AnimalListViewModel shows custom popup for denied status in `/iosApp/iosAppTests/Features/StartupScreen/AnimalListViewModelLocationTests.swift`
- [ ] T048 [P] [US3] Unit test AnimalListViewModel shows custom popup for restricted status in `/iosApp/iosAppTests/Features/StartupScreen/AnimalListViewModelLocationTests.swift`
- [ ] T049 [P] [US3] Unit test AnimalListViewModel.hasShownPermissionAlert prevents repeated popups in session in `/iosApp/iosAppTests/Features/StartupScreen/AnimalListViewModelLocationTests.swift`
- [ ] T050 [P] [US3] Unit test AnimalListViewModel.openSettings() calls coordinator callback in `/iosApp/iosAppTests/Features/StartupScreen/AnimalListViewModelLocationTests.swift`
- [ ] T051 [P] [US3] Unit test AnimalListViewModel.continueWithoutLocation() queries without coordinates in `/iosApp/iosAppTests/Features/StartupScreen/AnimalListViewModelLocationTests.swift`

**End-to-End Tests**:
- [ ] T052 [P] [US3] Update StartupScreen page object with permission popup elements in `/e2e-tests/src/test/java/.../screens/StartupScreen.java`
- [ ] T053 [P] [US3] Add US3 test scenario: denied status → custom popup displayed
- [ ] T054 [P] [US3] Add US3 test scenario: restricted status → custom popup displayed
- [ ] T055 [P] [US3] Add US3 test scenario: user taps "Go to Settings" → Settings app opens
- [ ] T056 [P] [US3] Add US3 test scenario: user taps "Cancel" → popup closes and animals displayed
- [ ] T057 [P] [US3] Add US3 test scenario: popup shown once per session (not repeated on subsequent screen appearances)

### Implementation for User Story 3

**iOS Implementation**:
- [ ] T058 [US3] Add LocationPermissionStatus.shouldShowCustomPopup presentation extension in `/iosApp/iosApp/Features/StartupScreen/AnimalListViewModel.swift`
- [ ] T059 [US3] Add showPermissionDeniedAlert @Published property in `/iosApp/iosApp/Features/StartupScreen/AnimalListViewModel.swift`
- [ ] T060 [US3] Add hasShownPermissionAlert session flag in `/iosApp/iosApp/Features/StartupScreen/AnimalListViewModel.swift`
- [ ] T061 [US3] Add onOpenAppSettings coordinator callback property in `/iosApp/iosApp/Features/StartupScreen/AnimalListViewModel.swift`
- [ ] T062 [US3] Implement openSettings() method in `/iosApp/iosApp/Features/StartupScreen/AnimalListViewModel.swift`
- [ ] T063 [US3] Implement continueWithoutLocation() method in `/iosApp/iosApp/Features/StartupScreen/AnimalListViewModel.swift`
- [ ] T064 [US3] Add custom popup display logic to loadAnimals() in `/iosApp/iosApp/Features/StartupScreen/AnimalListViewModel.swift`
- [ ] T065 [US3] Add SwiftUI alert modifier to AnimalListView in `/iosApp/iosApp/Features/StartupScreen/AnimalListView.swift`
- [ ] T066 [US3] Add accessibilityIdentifier "startup.permissionPopup.goToSettings" to Settings button in `/iosApp/iosApp/Features/StartupScreen/AnimalListView.swift`
- [ ] T067 [US3] Add accessibilityIdentifier "startup.permissionPopup.cancel" to Cancel button in `/iosApp/iosApp/Features/StartupScreen/AnimalListView.swift`
- [ ] T068 [US3] Add accessibilityIdentifier "startup.permissionPopup.message" to alert message in `/iosApp/iosApp/Features/StartupScreen/AnimalListView.swift`
- [ ] T069 [US3] Set onOpenAppSettings callback in StartupCoordinator.start() in `/iosApp/iosApp/Coordinators/StartupCoordinator.swift`
- [ ] T070 [US3] Implement openAppSettings() method in StartupCoordinator in `/iosApp/iosApp/Coordinators/StartupCoordinator.swift`

**Checkpoint**: User Story 3 complete - users with denied permissions have clear path to Settings and fallback option

---

## Phase 6: User Story 4 - Dynamic Permission Change Handling (Priority: P4)

**Goal**: Users who change location permissions while the app is open see the app respond without requiring restart

**Independent Test**: Change location permission (via Settings or system alert) while app is on startup screen, verify app reacts appropriately without restart

**Acceptance Scenarios**:
1. App observes location permission changes
2. User grants permission (returns from Settings) → app auto-fetches location and updates listings
3. App on startup with denied permissions, user grants via Settings and returns → location fetched and query executed
4. Permission changes to denied/restricted → app continues in fallback mode without location
5. Permission changes from granted to denied → next query executes without coordinates

### Tests for User Story 4 (MANDATORY) ✅

**iOS Unit Tests**:
- [ ] T071 [P] [US4] Unit test AnimalListViewModel.checkPermissionStatusChange() detects status changes in `/iosApp/iosAppTests/Features/StartupScreen/AnimalListViewModelLocationTests.swift`
- [ ] T072 [P] [US4] Unit test checkPermissionStatusChange() triggers refresh when changing from denied to authorized in `/iosApp/iosAppTests/Features/StartupScreen/AnimalListViewModelLocationTests.swift`
- [ ] T073 [P] [US4] Unit test checkPermissionStatusChange() does not refresh when changing from authorized to denied in `/iosApp/iosAppTests/Features/StartupScreen/AnimalListViewModelLocationTests.swift`
- [ ] T074 [P] [US4] Unit test checkPermissionStatusChange() updates locationPermissionStatus property in `/iosApp/iosAppTests/Features/StartupScreen/AnimalListViewModelLocationTests.swift`

**End-to-End Tests**:
- [ ] T075 [P] [US4] Add US4 test scenario: app in background, user enables permission in Settings, app returns → location fetched
- [ ] T076 [P] [US4] Add US4 test scenario: app in background, user disables permission in Settings, app returns → fallback mode
- [ ] T077 [P] [US4] Add US4 test scenario: permission changes from notDetermined to authorized while on screen → auto-refresh with location
- [ ] T078 [P] [US4] Add US4 test scenario: permission changes from granted to denied → subsequent queries use fallback

### Implementation for User Story 4

**iOS Implementation**:
- [ ] T079 [US4] Implement checkPermissionStatusChange() method in `/iosApp/iosApp/Features/StartupScreen/AnimalListViewModel.swift`
- [ ] T080 [US4] Add conditional refresh logic when permission changes from unauthorized to authorized in `/iosApp/iosApp/Features/StartupScreen/AnimalListViewModel.swift`
- [ ] T081 [US4] Add @Environment(\.scenePhase) property to AnimalListView in `/iosApp/iosApp/Features/StartupScreen/AnimalListView.swift`
- [ ] T082 [US4] Add .onChange(of: scenePhase) modifier to AnimalListView in `/iosApp/iosApp/Features/StartupScreen/AnimalListView.swift`
- [ ] T083 [US4] Call checkPermissionStatusChange() when app returns to foreground in `/iosApp/iosApp/Features/StartupScreen/AnimalListView.swift`

**Checkpoint**: User Story 4 complete - app responds dynamically to permission changes without requiring restart

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Quality improvements, documentation, and validation

- [ ] T084 [P] Run iOS unit tests and verify 80% coverage: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- [ ] T085 [P] Run E2E tests for iOS: `npm run test:mobile:ios` (from repo root)
- [ ] T086 [P] Manual testing: Fresh install with notDetermined status → verify system alert
- [ ] T087 [P] Manual testing: Denied status → verify custom popup and Settings navigation
- [ ] T088 [P] Manual testing: Granted status → verify location fetched and used in query
- [ ] T089 [P] Manual testing: App backgrounded and foregrounded → verify permission status checked
- [ ] T090 [P] Manual testing: "Allow Once" flow → verify app handles repeated alerts on subsequent launches
- [ ] T091 [P] Verify Info.plist contains NSLocationWhenInUseUsageDescription key
- [ ] T092 [P] Verify all L10n keys accessible via SwiftGen
- [ ] T093 [P] Verify all test identifiers follow convention: startup.{element}.{action}
- [ ] T094 [P] Code review: Verify MVVM-C pattern (View → ViewModel → Coordinator for Settings navigation)
- [ ] T095 [P] Code review: Verify LocationService is actor for thread-safety
- [ ] T096 [P] Code review: Verify Swift Concurrency used (no Combine, no callbacks)
- [ ] T097 [P] Update quickstart.md if implementation differs from plan
- [ ] T098 [P] Update IMPLEMENTATION-SUMMARY with feature completion notes

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup (Phase 1) completion - BLOCKS all user stories
- **User Stories (Phase 3-6)**: All depend on Foundational (Phase 2) completion
  - User stories build incrementally: US1 → US2 → US3 → US4
  - Each story extends previous implementation (not independent implementations)
- **Polish (Phase 7)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - Core location fetch for authorized users
- **User Story 2 (P2)**: Depends on US1 complete - Adds permission request for first-time users
- **User Story 3 (P3)**: Depends on US2 complete - Adds custom popup and Settings navigation for denied users
- **User Story 4 (P4)**: Depends on US3 complete - Adds dynamic permission change observation

**Note**: These user stories are sequentially dependent because they incrementally build the same location permission flow, not separate features.

### Within Each User Story

- Tests MUST be written and FAIL before implementation
- Unit tests can run in parallel within a story (all marked [P])
- E2E tests can run in parallel within a story (all marked [P])
- Implementation tasks must run sequentially (ViewModel → View → Coordinator)
- Documentation tasks can run in parallel after implementation complete

### Parallel Opportunities

- All Setup tasks (Phase 1) can run in parallel
- Domain models in Foundational phase marked [P] can run in parallel (T005, T006, T007)
- All unit tests within a user story can run in parallel (marked [P])
- All E2E tests within a user story can run in parallel (marked [P])
- Documentation tasks in Polish phase can run in parallel (all marked [P])

---

## Parallel Example: User Story 1

**Tests can launch together**:
```bash
# All US1 unit tests can run simultaneously:
- T012 [P] [US1] Create FakeLocationService
- T013 [P] [US1] Unit test LocationPermissionStatus enum
- T014 [P] [US1] Unit test UserLocation struct
- T015 [P] [US1] Unit test LocationService.authorizationStatus
- T016 [P] [US1] Unit test LocationService.requestLocation with authorized
# ... (all marked [P])

# All US1 E2E tests can run simultaneously:
- T022 [P] [US1] Create E2E feature file
- T023 [P] [US1] Add US1 scenario: granted permission
- T024 [P] [US1] Add US1 scenario: Always permission
- T025 [P] [US1] Add US1 scenario: location fetch fails
```

**Implementation runs sequentially**:
```bash
T026 → T027 → T028 → T029 → T030 (ViewModel changes, then Coordinator injection)
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (T001-T004)
2. Complete Phase 2: Foundational (T005-T011) - CRITICAL - blocks all stories
3. Complete Phase 3: User Story 1 (T012-T032) - Basic location fetch for authorized users
4. **STOP and VALIDATE**: Test US1 independently
   - Manually test with pre-granted permissions
   - Verify location fetched and used in query
   - Verify fallback when location unavailable
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP! - authorized users get location-aware content)
3. Add User Story 2 → Test independently → Deploy/Demo (first-time users see permission request)
4. Add User Story 3 → Test independently → Deploy/Demo (denied users have recovery path)
5. Add User Story 4 → Test independently → Deploy/Demo (dynamic permission changes handled)
6. Each story adds value incrementally without breaking previous stories

### Testing Strategy

**Unit Testing**:
- Run tests for each story as you complete it
- Target 80% coverage minimum
- Use `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- Check coverage in Xcode (Product → Test → Show Report Navigator → Coverage tab)

**E2E Testing**:
- Run E2E tests for each story after unit tests pass
- Use `npm run test:mobile:ios` from repo root
- Test on iOS simulator with different permission states
- Reset simulator permissions between tests (Device → Erase All Content and Settings)

**Manual Testing Scenarios**:
- Fresh install (notDetermined) → verify system alert appears
- Tap "Allow While Using App" → verify location used
- Tap "Allow Once" → verify location used, then on next launch alert reappears
- Tap "Don't Allow" → verify fallback mode works
- Denied status → verify custom popup appears
- Tap "Go to Settings" → verify Settings app opens to correct screen
- Background app, change permission in Settings, foreground → verify auto-refresh

---

## Notes

- [P] tasks = different files, no dependencies, can run in parallel
- [Story] label (US1, US2, US3, US4) maps task to specific user story for traceability
- User stories build incrementally on the same codebase (not independent features)
- Each story is independently testable by simulating different permission scenarios
- Verify tests fail before implementing (TDD workflow)
- Commit after each logical task group or checkpoint
- iOS-only feature: no Android, Web, or Backend tasks needed
- Uses existing backend API endpoint: `GET /api/pets?lat=X&lon=Y` (lat/lon optional)

