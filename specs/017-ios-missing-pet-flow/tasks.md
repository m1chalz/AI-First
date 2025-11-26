# Tasks: Missing Pet Report Flow (iOS)

**Input**: Design documents from `/specs/017-ios-missing-pet-flow/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Tests**: Test requirements for this project:

**MANDATORY - Platform-Specific Unit Tests** (iOS only):
- iOS: `/iosApp/iosAppTests/Features/ReportMissingPet/` (XCTest), 80% coverage
  - Scope: ReportMissingPetFlowState model, all 5 ViewModels (ChipNumber, Photo, Description, ContactDetails, Summary)
  - Run: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
  - Convention: MUST follow Given-When-Then structure with descriptive names

**MANDATORY - End-to-End Tests** (Java + Maven + Cucumber per Constitution v2.3.0):
- Mobile: `/e2e-tests/java/src/test/resources/features/mobile/017-ios-missing-pet-flow.feature` (Gherkin with @ios tag)
- All user stories MUST have E2E test coverage
- Use Screen Object Model pattern (Java with dual annotations: @AndroidFindBy + @iOSXCUITFindBy)
- Convention: MUST structure scenarios with Given-When-Then phases (Gherkin format)
- Run command: `mvn -f e2e-tests/java/pom.xml test -Dcucumber.filter.tags="@ios"`
- Report location: `/e2e-tests/java/target/cucumber-reports/ios/index.html`

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

## Path Conventions

- iOS-only feature: `/iosApp/iosApp/Features/ReportMissingPet/`
- Tests: `/iosApp/iosAppTests/Features/ReportMissingPet/`
- E2E: `/e2e-tests/mobile/specs/` and `/e2e-tests/mobile/screens/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project structure initialization and localization setup

- [X] T001 Create feature directory structure `/iosApp/iosApp/Features/ReportMissingPet/` with subdirectories: `Coordinators/`, `Models/`, `Views/`
- [X] T002 Create test directory structure `/iosApp/iosAppTests/Features/ReportMissingPet/` with subdirectories: `Models/`, `Views/`
- [X] T003 [P] Add localized strings to `/iosApp/iosApp/Resources/en.lproj/Localizable.strings` and `/iosApp/iosApp/Resources/pl.lproj/Localizable.strings` (screen titles, button labels, progress labels)
- [X] T004 [P] Run SwiftGen to regenerate `/iosApp/iosApp/Generated/Strings.swift` from updated Localizable.strings

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T005 Create `ReportMissingPetFlowState` (ObservableObject) in `/iosApp/iosApp/Features/ReportMissingPet/Models/ReportMissingPetFlowState.swift` with @Published properties (chipNumber, photo, description, contactEmail, contactPhone), clear() method, and computed validation properties (hasChipNumber, hasPhoto, hasDescription, hasContactInfo, formattedChipNumber)
- [X] T006 Create E2E Screen Object Model in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/screens/ReportMissingPetScreen.java` with dual annotations (@AndroidFindBy, @iOSXCUITFindBy) for all 5 screens (AnimalListScreen selectors, ChipNumberScreen, PhotoScreen, DescriptionScreen, ContactDetailsScreen, SummaryScreen with progress indicator and back button locators)
- [X] T007 [P] Create E2E feature file in `/e2e-tests/java/src/test/resources/features/mobile/017-ios-missing-pet-flow.feature` with Gherkin scenarios for User Story 1 (complete flow) and User Story 2 (backward navigation), tagged with @ios and @mobile

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Complete Missing Pet Report (Priority: P1) 🎯 MVP

**Goal**: User can navigate through all 5 screens of the missing pet report flow (4 data collection screens with progress indicator + summary screen without progress indicator) by tapping "report missing animal" button and using "Continue" buttons.

**Independent Test**: Tap "report missing animal" button on animal list screen, navigate through all 5 screens by tapping "Continue", verify progress indicator shows 1/4, 2/4, 3/4, 4/4 on data collection screens and is not displayed on summary screen.

### Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**iOS Unit Tests**:

- [X] T008 [P] [US1] Unit test for ReportMissingPetFlowState in `/iosApp/iosAppTests/Features/ReportMissingPet/Models/ReportMissingPetFlowStateTests.swift` (MANDATORY - 80% coverage target):
  - Test `clear()` method: verify all @Published properties reset to nil
  - Test `hasChipNumber` computed property: returns true only when chipNumber is non-empty string
  - Test `hasPhoto` computed property: returns true only when photo UIImage is not nil
  - Test `hasDescription` computed property: returns true only when description is non-empty string
  - Test `hasContactInfo` computed property: returns true when email OR phone is provided (not nil/empty)
  - Test `formattedChipNumber` computed property: formats 15 digits into XX-XXXXX-XXXXX pattern, returns nil if empty
- [X] T009 [P] [US1] Unit test for ChipNumberViewModel in `/iosApp/iosAppTests/Features/ReportMissingPet/Views/ChipNumberViewModelTests.swift` (test handleNext triggers onNext callback, handleBack triggers onBack callback, init stores flowState reference)
- [X] T010 [P] [US1] Unit test for PhotoViewModel in `/iosApp/iosAppTests/Features/ReportMissingPet/Views/PhotoViewModelTests.swift` (test handleNext triggers onNext callback, handleBack triggers onBack callback)
- [X] T011 [P] [US1] Unit test for DescriptionViewModel in `/iosApp/iosAppTests/Features/ReportMissingPet/Views/DescriptionViewModelTests.swift` (test handleNext triggers onNext callback, handleBack triggers onBack callback)
- [X] T012 [P] [US1] Unit test for ContactDetailsViewModel in `/iosApp/iosAppTests/Features/ReportMissingPet/Views/ContactDetailsViewModelTests.swift` (test handleNext triggers onNext callback, handleBack triggers onBack callback)
- [X] T013 [P] [US1] Unit test for SummaryViewModel in `/iosApp/iosAppTests/Features/ReportMissingPet/Views/SummaryViewModelTests.swift` (test handleSubmit triggers onSubmit callback, handleBack triggers onBack callback)

**End-to-End Tests**:

- [X] T014 [P] [US1] Create Gherkin scenario "Complete Missing Pet Report Flow" in `/e2e-tests/java/src/test/resources/features/mobile/017-ios-missing-pet-flow.feature` (Given user on animal list, When tap "report missing animal", Then chip number screen displays with progress 1/4; When tap Continue, Then photo screen displays with progress 2/4; When tap Continue, Then description screen displays with progress 3/4; When tap Continue, Then contact details screen displays with progress 4/4; When tap Continue, Then summary screen displays without progress indicator) tagged @ios @mobile @us1

### Implementation for User Story 1

**iOS** (Full Stack Implementation):

**Step 1: Coordinator Setup**

- [X] T015 [US1] Create ReportMissingPetCoordinator in `/iosApp/iosApp/Features/ReportMissingPet/Coordinators/ReportMissingPetCoordinator.swift` with CoordinatorInterface conformance, parentNavigationController property, flowState property, init(parentNavigationController:), start(animated:) method (creates modal UINavigationController with .fullScreen style, creates ReportMissingPetFlowState, navigates to chip number screen), exitFlow() method (dismisses modal, clears flowState, notifies parent)
- [X] T016 [US1] Add private helper methods to ReportMissingPetCoordinator: configureProgressIndicator(hostingController:step:total:) for text-only UIBarButtonItem displaying "1/4", "2/4", "3/4", or "4/4" on right side of navigation bar, configureCustomBackButton(hostingController:action:) for chevron-left UIBarButtonItem
- [X] T017 [US1] Implement AnimalListCoordinator.showReportMissing() method in `/iosApp/iosApp/Features/AnimalList/Coordinators/AnimalListCoordinator.swift` - CURRENTLY PLACEHOLDER. Implementation: creates ReportMissingPetCoordinator(parentNavigationController: navigationController!), sets parentCoordinator, appends to childCoordinators, awaits coordinator.start(animated: true). Replace placeholder print statement with full implementation.
- [X] T018 [US1] Verify AnimalListView "Report a Missing Animal" button triggers viewModel.reportMissing() action (button already exists at line 100-107, just verify it's wired correctly to ViewModel closure)

**Step 2: Chip Number Screen (Step 1/4)**

- [X] T019 [P] [US1] Create ChipNumberViewModel in `/iosApp/iosApp/Features/ReportMissingPet/Views/ChipNumberViewModel.swift` with @MainActor, ObservableObject conformance, flowState dependency, onNext/onBack closures, handleNext()/handleBack() methods, init(flowState:)
- [X] T020 [US1] Create ChipNumberView in `/iosApp/iosApp/Features/ReportMissingPet/Views/ChipNumberView.swift` with SwiftUI View, @ObservedObject viewModel, VStack with placeholder Text("Chip Number Screen"), Continue button at bottom (calls viewModel.handleNext), .accessibilityIdentifier("chipNumber.continueButton")
- [X] T021 [US1] Add navigateToChipNumber() method to ReportMissingPetCoordinator (called from start(), creates ChipNumberViewModel with flowState injection, sets onNext to navigateToPhoto, sets onBack to exitFlow, wraps view in NavigationBackHiding, configures progress indicator 1/4, configures custom back button, pushes to modal nav controller)

**Step 3: Photo Screen (Step 2/4)**

- [X] T022 [P] [US1] Create PhotoViewModel in `/iosApp/iosApp/Features/ReportMissingPet/Views/PhotoViewModel.swift` with @MainActor, ObservableObject conformance, flowState dependency, onNext/onBack closures, handleNext()/handleBack() methods, init(flowState:)
- [X] T023 [US1] Create PhotoView in `/iosApp/iosApp/Features/ReportMissingPet/Views/PhotoView.swift` with SwiftUI View, @ObservedObject viewModel, VStack with placeholder Text("Photo Screen"), Continue button at bottom, .accessibilityIdentifier("photo.continueButton")
- [X] T024 [US1] Add navigateToPhoto() method to ReportMissingPetCoordinator (creates PhotoViewModel with flowState injection, sets onNext to navigateToDescription, sets onBack to pop, wraps view in NavigationBackHiding, configures progress indicator 2/4, configures custom back button, pushes to nav controller)

**Step 4: Description Screen (Step 3/4)**

- [X] T025 [P] [US1] Create DescriptionViewModel in `/iosApp/iosApp/Features/ReportMissingPet/Views/DescriptionViewModel.swift` with @MainActor, ObservableObject conformance, flowState dependency, onNext/onBack closures, handleNext()/handleBack() methods, init(flowState:)
- [X] T026 [US1] Create DescriptionView in `/iosApp/iosApp/Features/ReportMissingPet/Views/DescriptionView.swift` with SwiftUI View, @ObservedObject viewModel, VStack with placeholder Text("Description Screen"), Continue button at bottom, .accessibilityIdentifier("description.continueButton")
- [X] T027 [US1] Add navigateToDescription() method to ReportMissingPetCoordinator (creates DescriptionViewModel with flowState injection, sets onNext to navigateToContactDetails, sets onBack to pop, wraps view in NavigationBackHiding, configures progress indicator 3/4, configures custom back button, pushes to nav controller)

**Step 5: Contact Details Screen (Step 4/4)**

- [X] T028 [P] [US1] Create ContactDetailsViewModel in `/iosApp/iosApp/Features/ReportMissingPet/Views/ContactDetailsViewModel.swift` with @MainActor, ObservableObject conformance, flowState dependency, onNext/onBack closures, handleNext()/handleBack() methods, init(flowState:)
- [X] T029 [US1] Create ContactDetailsView in `/iosApp/iosApp/Features/ReportMissingPet/Views/ContactDetailsView.swift` with SwiftUI View, @ObservedObject viewModel, VStack with placeholder Text("Contact Details Screen"), Continue button at bottom, .accessibilityIdentifier("contactDetails.continueButton")
- [X] T030 [US1] Add navigateToContactDetails() method to ReportMissingPetCoordinator (creates ContactDetailsViewModel with flowState injection, sets onNext to navigateToSummary, sets onBack to pop, wraps view in NavigationBackHiding, configures progress indicator 4/4, configures custom back button, pushes to nav controller)

**Step 6: Summary Screen (Step 5 - No Progress Indicator)**

- [X] T031 [P] [US1] Create SummaryViewModel in `/iosApp/iosApp/Features/ReportMissingPet/Views/SummaryViewModel.swift` with @MainActor, ObservableObject conformance, flowState dependency, onSubmit/onBack closures, handleSubmit()/handleBack() methods, init(flowState:)
- [X] T032 [US1] Create SummaryView in `/iosApp/iosApp/Features/ReportMissingPet/Views/SummaryView.swift` with SwiftUI View, @ObservedObject viewModel, VStack with placeholder Text("Summary Screen"), Submit button at bottom (calls viewModel.handleSubmit), .accessibilityIdentifier("summary.submitButton")
- [X] T033 [US1] Add navigateToSummary() method to ReportMissingPetCoordinator (creates SummaryViewModel with flowState injection, sets onSubmit to exitFlow, sets onBack to pop, wraps view in NavigationBackHiding, NO progress indicator configured, configures custom back button, pushes to nav controller)

**Step 7: Integration & Testing**

- [ ] T034 [US1] Run iOS unit tests and verify 80% coverage: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- [X] T035 [US1] Create Java step definitions in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/mobile/ReportMissingPetSteps.java` with Given/When/Then methods for complete flow (app launch, navigate to report missing, tap continue buttons, verify progress indicator)
- [ ] T036 [US1] Run E2E test for US1 complete flow navigation: `mvn -f e2e-tests/java/pom.xml test -Dcucumber.filter.tags="@ios AND @us1"`
- [X] T037 [P] [US1] Add SwiftDoc documentation to complex APIs in ReportMissingPetCoordinator (helper methods), ReportMissingPetFlowState (computed properties), skip self-explanatory methods
- [ ] T038 [US1] Manual test on iPhone 15 simulator: verify flow navigation, progress indicator visibility, Continue button functionality

**Checkpoint**: At this point, User Story 1 should be fully functional - users can navigate through all 5 screens with progress indicator on steps 1-4

---

## Phase 4: User Story 2 - Navigate Backwards Through Flow (Priority: P2)

**Goal**: User can navigate backwards through the flow to review or edit previous screens. Back button or swipe gesture returns to previous screen. From step 1, back button exits the flow and returns to animal list screen.

**Independent Test**: Navigate to any screen (e.g., step 3), tap back button, verify previous screen displays. Navigate to step 1, tap back button, verify flow exits and animal list screen displays. Verify progress indicator updates correctly when navigating backwards.

### Tests for User Story 2 (MANDATORY) ✅

**iOS Unit Tests** (Additional test cases):

- [X] T039 [P] [US2] Add unit test to ChipNumberViewModelTests in `/iosApp/iosAppTests/Features/ReportMissingPet/Views/ChipNumberViewModelTests.swift` (test handleBack on step 1 triggers onBack callback which should exit flow)
- [X] T040 [P] [US2] Add unit test to PhotoViewModelTests in `/iosApp/iosAppTests/Features/ReportMissingPet/Views/PhotoViewModelTests.swift` (test handleBack triggers onBack callback which should navigate to previous screen)
- [X] T041 [P] [US2] Add unit test to SummaryViewModelTests in `/iosApp/iosAppTests/Features/ReportMissingPet/Views/SummaryViewModelTests.swift` (test handleBack from summary navigates back to contact details screen)

**End-to-End Tests**:

- [X] T042 [P] [US2] Create Gherkin scenario "Navigate Backwards from Middle Steps" in `/e2e-tests/java/src/test/resources/features/mobile/017-ios-missing-pet-flow.feature` (Given user on contact details screen (step 4/4), When tap back button, Then description screen displays with progress 3/4) tagged @ios @mobile @us2
- [X] T043 [P] [US2] Create Gherkin scenario "Exit Flow from Step 1" and "Navigate Back from Summary" in same feature file (Given user on chip number screen (step 1/4), When tap back button, Then animal list screen displays and flow is exited; Given user on summary screen, When tap back button, Then contact details screen displays with progress 4/4) tagged @ios @mobile @us2

### Implementation for User Story 2

**iOS** (Additional functionality):

- [X] T044 [US2] Update configureCustomBackButton() in `/iosApp/iosApp/Features/ReportMissingPet/Coordinators/ReportMissingPetCoordinator.swift` to ensure back button action correctly calls ViewModel handleBack() method (already wired in US1, verify logic is correct)
- [X] T045 [US2] Update exitFlow() in ReportMissingPetCoordinator to add flowState.clear() call before dismissing modal (ensures state is cleared when exiting from step 1)
- [X] T046 [US2] Update all screen navigation methods (navigateToPhoto, navigateToDescription, navigateToContactDetails, navigateToSummary) to verify onBack closure correctly calls navigationController?.popViewController(animated: true) (already implemented in US1, verify correctness)
- [ ] T047 [US2] Add manual test verification on iPhone 15 simulator:
  - Navigate to step 3 (Description), tap back button
  - Verify: Step 2 (Photo) displays with progress indicator "2/4"
  - Tap back button again
  - Verify: Step 1 (Chip Number) displays with progress indicator "1/4"
  - Tap back button one more time
  - Verify: Flow exits (modal dismisses) and Animal List screen displays
  - Verify: No data persists when reopening report flow - fresh start with empty screens
- [ ] T048 [US2] Run iOS unit tests with new US2 test cases and verify 80% coverage maintained: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- [ ] T049 [US2] Run E2E tests for US2 backward navigation: `mvn -f e2e-tests/java/pom.xml test -Dcucumber.filter.tags="@ios AND @us2"`
- [ ] T050 [US2] Manual test on iPhone 15 simulator: verify backward navigation from all screens, verify progress indicator updates correctly, verify flow exits cleanly from step 1

**Checkpoint**: At this point, User Stories 1 AND 2 should both work - users can navigate forward through all screens and backward to any previous screen or exit the flow

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [X] T051 [P] Update `/specs/017-ios-missing-pet-flow/IMPLEMENTATION-SUMMARY.md` with completion status, architecture decisions, testing results, known limitations (no form fields yet, placeholder screens only)
- [X] T052 Code cleanup: verify all ViewModels follow consistent pattern (minimal skeleton with only onNext/onBack), verify all views have accessibility identifiers
- [X] T053 [P] Verify all localized strings are used correctly via SwiftGen (L10n.* references), no hardcoded strings in views
- [ ] T054 Run quickstart.md validation: manually test all scenarios described in quickstart guide, verify documentation is accurate
- [ ] T055 [P] Test on multiple device sizes (iPhone SE 3rd gen, iPhone 15, iPhone 15 Pro Max) to verify layout adapts correctly, progress indicator remains visible
- [ ] T056 Test device rotation: verify flow works correctly in portrait and landscape orientations, navigation bar elements remain accessible
- [X] T057 [P] Add inline code comments for complex coordinator logic (modal presentation, navigation stack management, flow state lifecycle)
- [X] T058 Verify AnimalListCoordinator.showReportMissing() placeholder is fully implemented (T017) - should create and start ReportMissingPetCoordinator as child flow

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User Story 1 can start after Foundational
  - User Story 2 depends on User Story 1 (builds on forward navigation logic)
- **Polish (Phase 5)**: Depends on User Story 1 and 2 being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Depends on User Story 1 implementation - backward navigation requires forward navigation to be functional

### Within Each User Story

**User Story 1 Flow**:
1. Tests (T008-T014) MUST be written and FAIL before implementation
2. Coordinator setup (T015-T017) before screen implementations
3. Screens can be implemented sequentially or in parallel:
   - Chip Number (T018-T020) before navigation to Photo works
   - Photo (T021-T023) before navigation to Description works
   - Description (T024-T026) before navigation to ContactDetails works
   - ContactDetails (T027-T029) before navigation to Summary works
   - Summary (T030-T032) completes the flow
4. Integration & Testing (T033-T036) after all screens implemented

**User Story 2 Flow**:
1. Additional tests (T037-T041) MUST be written and FAIL before implementation
2. Back button logic verification/updates (T042-T044) can be done in parallel
3. Manual testing (T045) after logic updates
4. Automated testing (T046-T047) to verify correctness
5. Final manual validation (T048)

### Parallel Opportunities

- All Setup tasks (T001-T004) can run in parallel
- Foundational tasks: T005, T006, T007 can run in parallel (FlowState model + E2E Screen Object + Feature file)
- User Story 1 Tests: T008-T013 (unit tests) can all run in parallel
- User Story 1 Coordinator Setup: T015, T016, T017 should be sequential (T015 → T016 → T017)
- User Story 1 ViewModels: T019, T022, T025, T028, T031 can be created in parallel (different files)
- User Story 1 Views: T020, T023, T026, T029, T032 can be created in parallel (different files)
- User Story 1 Navigation Methods: T021, T024, T027, T030, T033 sequential (build on coordinator)
- User Story 2 Tests: T039-T041 can all run in parallel
- User Story 2 Verification: T044-T046 can be done in parallel (reviewing existing code)
- E2E Scenario Creation: T042-T043 parallel with T035 (step definitions)
- E2E Tests Run: T036, T049 can be done after implementation is complete
- Polish tasks: T051, T053, T055, T057, T058 can run in parallel (different concerns)

---

## Parallel Example: User Story 1 - ViewModels

```bash
# Launch all ViewModel creation tasks together (different files):
Task T018: "Create ChipNumberViewModel in /iosApp/.../ChipNumberViewModel.swift"
Task T021: "Create PhotoViewModel in /iosApp/.../PhotoViewModel.swift"
Task T024: "Create DescriptionViewModel in /iosApp/.../DescriptionViewModel.swift"
Task T027: "Create ContactDetailsViewModel in /iosApp/.../ContactDetailsViewModel.swift"
Task T030: "Create SummaryViewModel in /iosApp/.../SummaryViewModel.swift"

# Launch all View creation tasks together (different files):
Task T019: "Create ChipNumberView in /iosApp/.../ChipNumberView.swift"
Task T022: "Create PhotoView in /iosApp/.../PhotoView.swift"
Task T025: "Create DescriptionView in /iosApp/.../DescriptionView.swift"
Task T028: "Create ContactDetailsView in /iosApp/.../ContactDetailsView.swift"
Task T031: "Create SummaryView in /iosApp/.../SummaryView.swift"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (T001-T004)
2. Complete Phase 2: Foundational (T005-T007) - CRITICAL, blocks all stories
3. Complete Phase 3: User Story 1 (T008-T036)
4. **STOP and VALIDATE**: Test User Story 1 independently (forward navigation works)
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP: forward navigation through 5 screens!)
3. Add User Story 2 → Test independently → Deploy/Demo (enhanced: backward navigation + exit)
4. Complete Polish → Final validation → Deploy/Demo
5. Each story adds value without breaking previous stories

### Sequential Strategy (Recommended for Single Developer)

With one developer working alone:

1. Complete Setup (Phase 1)
2. Complete Foundational (Phase 2)
3. Complete User Story 1 (Phase 3) in order:
   - Write all unit tests first (T008-T013)
   - Write E2E test (T014)
   - Implement coordinator (T015-T017)
   - Implement screens sequentially (T018-T032)
   - Run tests and validate (T033-T036)
4. Complete User Story 2 (Phase 4) in order:
   - Write additional tests (T037-T041)
   - Verify/update logic (T042-T044)
   - Manual testing (T045)
   - Run tests (T046-T047)
   - Final validation (T048)
5. Complete Polish (Phase 5)

---

## Notes

- [P] tasks = different files, no dependencies - can be parallelized if team capacity allows
- [Story] label maps task to specific user story for traceability
- Each user story should be independently testable (US1: forward navigation works standalone, US2: adds backward navigation)
- Verify tests fail before implementing (TDD: Red-Green-Refactor)
- This is iOS-only feature - no Android, Web, or Backend tasks
- Coordinator owns modal UINavigationController and ReportMissingPetFlowState
- All screens are empty placeholders with Continue button only (form fields added in future iterations)
- Progress indicator displayed only on steps 1-4, NOT on summary screen
- Custom back button on all screens (exit from step 1, pop from steps 2-5)
- 80% test coverage target for iOS unit tests
- E2E tests required for both user stories
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently

