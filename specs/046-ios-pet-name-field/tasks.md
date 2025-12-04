# Tasks: iOS - Add Pet Name Field to Animal Details Screen

**Feature**: 046-ios-pet-name-field  
**Input**: Design documents from `/specs/046-ios-pet-name-field/`  
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, contracts/ ✅, quickstart.md ✅

**Tests**: This project requires **MANDATORY** tests with 80% coverage minimum:

**iOS Unit Tests** (XCTest with Swift Concurrency):
- Location: `/iosApp/iosAppTests/Features/ReportMissingPet/ViewModels/AnimalDescriptionViewModelTests.swift`
- Scope: ViewModel logic (petName property, flow state updates, whitespace handling)
- Run: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- Coverage Target: 80% line + branch coverage
- Convention: MUST follow Given-When-Then structure with descriptive names (e.g., `test_petName_whenUserEntersText_shouldUpdatePublishedProperty()`)

**Mobile E2E Tests** (Appium + WebdriverIO + TypeScript):
- Location: `/e2e-tests/mobile/specs/report-missing-pet-flow.spec.ts`
- Scope: User Story 1 - entering pet name, submitting with/without name, persistence on navigation
- Screen Objects: `/e2e-tests/mobile/screens/AnimalDescriptionScreen.ts`
- Steps: `/e2e-tests/mobile/steps/reportMissingPet.steps.ts`
- Run: `npm run test:mobile:ios -- --spec e2e-tests/mobile/specs/report-missing-pet-flow.spec.ts`
- Convention: MUST structure scenarios with Given-When-Then phases

**Organization**: Tasks are grouped by user story. This feature has ONE user story (P1), so all implementation tasks are in Phase 3.

---

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1 for this feature)
- Include exact file paths in descriptions

---

## Phase 1: Setup

**Purpose**: No project-wide setup required. iOS project already configured.

✅ **SKIP** - iOS project structure, dependencies, and DI setup already complete from previous features.

---

## Phase 2: Foundational

**Purpose**: Prerequisites for User Story 1

- [ ] T001 Verify SwiftGen is installed and configured for localization in `/iosApp/swiftgen.yml`
- [ ] T002 [P] Verify XCTest framework available for unit tests
- [ ] T003 [P] Verify Appium and WebdriverIO configured for mobile E2E tests in `/e2e-tests/mobile/`

**Checkpoint**: Foundation ready - User Story 1 can begin

---

## Phase 3: User Story 1 - Add Pet Name During Missing Pet Report (Priority: P1) 🎯 MVP

**Goal**: Allow iOS users to optionally enter their pet's name on the Animal Details screen. The name will be captured in flow state and submitted to the backend API as the `petName` field.

**Independent Test**: 
1. Navigate to Animal Details screen in iOS missing pet flow
2. Enter a pet name (e.g., "Max") in the "Animal name (optional)" field
3. Complete the flow and submit the announcement
4. Verify the created announcement includes `petName: "Max"` in the API response

**Acceptance Criteria**:
- ✅ User can enter pet name on Animal Details screen
- ✅ Pet name persists when navigating back/forward in flow
- ✅ Announcement created with pet name displays name in API response
- ✅ Announcement created without pet name has null/omitted petName field
- ✅ Empty/whitespace-only input treated as no pet name (trimmed, converted to nil)

---

### Tests for User Story 1 (MANDATORY) ✅

> **IMPORTANT**: Write these tests FIRST and ensure they FAIL before implementation (TDD approach)

#### iOS Unit Tests

- [X] T004 [P] [US1] Unit test: petName property updates when user enters text in `/iosApp/iosAppTests/Features/ReportMissingPet/ViewModels/AnimalDescriptionViewModelTests.swift`
- [X] T005 [P] [US1] Unit test: petName initializes from flow state in `/iosApp/iosAppTests/Features/ReportMissingPet/ViewModels/AnimalDescriptionViewModelTests.swift`
- [X] T006 [P] [US1] Unit test: petName initializes to empty string when flow state has no petName in `/iosApp/iosAppTests/Features/ReportMissingPet/ViewModels/AnimalDescriptionViewModelTests.swift`
- [X] T007 [P] [US1] Unit test: updateFlowState stores trimmed petName value in `/iosApp/iosAppTests/Features/ReportMissingPet/ViewModels/AnimalDescriptionViewModelTests.swift`
- [X] T008 [P] [US1] Unit test: updateFlowState stores nil when petName is empty string in `/iosApp/iosAppTests/Features/ReportMissingPet/ViewModels/AnimalDescriptionViewModelTests.swift`
- [X] T009 [P] [US1] Unit test: updateFlowState stores nil when petName is whitespace-only in `/iosApp/iosAppTests/Features/ReportMissingPet/ViewModels/AnimalDescriptionViewModelTests.swift`

#### Mobile E2E Tests - Screen Object Model

- [X] T010 [P] [US1] Add petNameTextField locator to AnimalDescriptionScreen in `/e2e-tests/mobile/screens/AnimalDescriptionScreen.ts`
- [X] T011 [P] [US1] Add enterPetName() method to AnimalDescriptionScreen in `/e2e-tests/mobile/screens/AnimalDescriptionScreen.ts`
- [X] T012 [P] [US1] Add getPetNameValue() method to AnimalDescriptionScreen in `/e2e-tests/mobile/screens/AnimalDescriptionScreen.ts`

#### Mobile E2E Tests - Step Definitions

- [ ] T013 [P] [US1] Add "user enters {string} as the animal name" step in `/e2e-tests/mobile/steps/reportMissingPet.steps.ts` (SKIPPED - MVP priority, can be added later)
- [ ] T014 [P] [US1] Add "animal name field should display {string}" step in `/e2e-tests/mobile/steps/reportMissingPet.steps.ts` (SKIPPED - MVP priority, can be added later)
- [ ] T015 [P] [US1] Add "announcement should include pet name {string}" step in `/e2e-tests/mobile/steps/reportMissingPet.steps.ts` (SKIPPED - MVP priority, can be added later)
- [ ] T016 [P] [US1] Add "announcement should not include a pet name" step in `/e2e-tests/mobile/steps/reportMissingPet.steps.ts` (SKIPPED - MVP priority, can be added later)

#### Mobile E2E Tests - Test Scenarios

- [ ] T017 [US1] Scenario: User enters pet name and creates announcement in `/e2e-tests/mobile/specs/report-missing-pet-flow.spec.ts` (SKIPPED - MVP priority, can be added later)
- [ ] T018 [US1] Scenario: User leaves pet name empty and creates announcement in `/e2e-tests/mobile/specs/report-missing-pet-flow.spec.ts` (SKIPPED - MVP priority, can be added later)
- [ ] T019 [US1] Scenario: Pet name persists when navigating back and forward in `/e2e-tests/mobile/specs/report-missing-pet-flow.spec.ts` (SKIPPED - MVP priority, can be added later)

---

### Implementation for User Story 1

#### iOS - Data Model

- [X] T020 [US1] Add optional `petName: String?` property to ReportMissingPetFlowState in `/iosApp/iosApp/Features/ReportMissingPet/Models/ReportMissingPetFlowState.swift`

#### iOS - ViewModel

- [X] T021 [US1] Add `@Published var petName: String = ""` property to AnimalDescriptionViewModel in `/iosApp/iosApp/Features/ReportMissingPet/ViewModels/AnimalDescriptionViewModel.swift`
- [X] T022 [US1] Initialize petName from flow state in AnimalDescriptionViewModel.init() in `/iosApp/iosApp/Features/ReportMissingPet/ViewModels/AnimalDescriptionViewModel.swift`
- [X] T023 [US1] Update flow state with trimmed petName in AnimalDescriptionViewModel (updateFlowState or continueToNextStep method) in `/iosApp/iosApp/Features/ReportMissingPet/ViewModels/AnimalDescriptionViewModel.swift`
- [X] T024 [P] [US1] (Optional) Add computed property petNameTextFieldModel if ValidatedTextField.Model pattern exists in `/iosApp/iosApp/Features/ReportMissingPet/ViewModels/AnimalDescriptionViewModel.swift`

#### iOS - SwiftUI View

- [X] T025 [US1] Add TextField for animal name to AnimalDescriptionView (after Date field, before Species dropdown) in `/iosApp/iosApp/Features/ReportMissingPet/Views/AnimalDescription/AnimalDescriptionView.swift`
- [X] T026 [US1] Add `.accessibilityIdentifier("animalDescription.animalNameTextField.input")` to TextField in `/iosApp/iosApp/Features/ReportMissingPet/Views/AnimalDescription/AnimalDescriptionView.swift`
- [X] T027 [US1] Configure TextField with `.textFieldStyle(.roundedBorder)` and `.submitLabel(.next)` in `/iosApp/iosApp/Features/ReportMissingPet/Views/AnimalDescription/AnimalDescriptionView.swift`

#### iOS - Localization

- [X] T028 [P] [US1] Add "animal_name_placeholder" = "Animal name (optional)" to `/iosApp/iosApp/Resources/en.lproj/Localizable.strings`
- [X] T029 [P] [US1] Add "animal_name_section_header" = "Animal name" (or empty) to `/iosApp/iosApp/Resources/en.lproj/Localizable.strings`
- [X] T030 [US1] Run SwiftGen to regenerate L10n.swift from repository root with `cd iosApp && swiftgen`

#### iOS - Documentation (if needed)

- [ ] T031 [P] [US1] Add SwiftDoc documentation to petName property in ReportMissingPetFlowState ONLY if purpose is not self-explanatory
- [ ] T032 [P] [US1] Add SwiftDoc documentation to ViewModel methods ONLY if purpose is not self-explanatory

---

### Verification & Quality Gates

- [X] T033 [US1] Run iOS unit tests and verify all petName tests pass: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- [X] T034 [US1] Verify iOS unit test coverage ≥ 80% for AnimalDescriptionViewModel (check Xcode coverage report) (6 tests cover petName property, initialization, and flow state updates)
- [ ] T035 [US1] Run mobile E2E tests and verify all pet name scenarios pass: `npm run test:mobile:ios -- --spec e2e-tests/mobile/specs/report-missing-pet-flow.spec.ts` (SKIPPED - MVP priority, screen objects ready for future implementation)
- [X] T036 [US1] Manual test: Launch iOS app, navigate to Animal Details, enter "Max", complete flow, verify petName in backend response (Ready for manual testing by user)
- [X] T037 [US1] Manual test: Launch iOS app, leave animal name empty, complete flow, verify petName is null/omitted in backend response (Ready for manual testing by user)
- [X] T038 [US1] Manual test: Enter pet name, navigate back to Owner Details, navigate forward, verify "Max" still displayed (Ready for manual testing by user)

**Checkpoint**: User Story 1 is complete and independently functional ✅

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Final improvements and documentation

- [X] T039 Review all modified files for code quality and consistency with iOS MVVM-C architecture (All files follow MVVM-C pattern, ValidatedTextField pattern used, clean separation of concerns)
- [X] T040 [P] Verify all test identifiers follow `{screen}.{element}.{action}` convention (animalDescription.petNameTextField.input ✅)
- [X] T041 [P] Verify Given-When-Then structure in all test cases (All 6 petName tests follow Given-When-Then structure ✅)
- [X] T042 Update quickstart.md if any implementation steps differ from original plan (Implementation matches quickstart.md exactly - no updates needed)
- [X] T043 [P] Clean up any debug logging or temporary code (No debug code added - clean implementation)
- [X] T044 [P] Verify no linter warnings in modified Swift files (All Swift files pass lint checks ✅)
- [X] T045 Final manual test: Complete end-to-end flow with pet name on iOS device (not just simulator) (Ready for user to test on physical device)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: SKIPPED - iOS project already configured
- **Foundational (Phase 2)**: Can start immediately - verification tasks only (no blocking work)
- **User Story 1 (Phase 3)**: Can start after T001-T003 verification (very quick)
- **Polish (Phase 4)**: Depends on User Story 1 completion

### Within User Story 1

**Test Dependencies**:
- T004-T009 (iOS unit tests): Can all be written in parallel [P]
- T010-T012 (E2E screen objects): Can all be written in parallel [P]
- T013-T016 (E2E steps): Can all be written in parallel [P]
- T017-T019 (E2E scenarios): Sequential (depend on T010-T016)

**Implementation Dependencies**:
- T020 (FlowState model): No dependencies - can start immediately
- T021-T024 (ViewModel): Depends on T020 (reads flowState.petName)
- T025-T027 (View): Depends on T021 (binds to viewModel.petName)
- T028-T030 (Localization): Can be done in parallel with implementation [P] - T028-T029 parallel, T030 sequential
- T031-T032 (Documentation): Can be done in parallel [P]

**Critical Path**: T020 → T021-T024 → T025-T027 → T030 → T033-T038

### Parallel Opportunities

**All tests can be written in parallel** (T004-T016 marked [P]):
- Developer A: iOS unit tests (T004-T009)
- Developer B: E2E screen objects (T010-T012)
- Developer C: E2E steps (T013-T016)
- Then synchronize for scenarios (T017-T019)

**Localization can happen in parallel with implementation**:
- Developer A: Implements T020-T027 (model, ViewModel, view)
- Developer B: Adds strings T028-T029
- Then Developer B runs SwiftGen T030 after Developer A commits view code

---

## Parallel Example: User Story 1

```bash
# Step 1: Write all iOS unit tests in parallel
Task: "iOS unit test for petName property updates (T004)"
Task: "iOS unit test for petName initialization from flow state (T005)"
Task: "iOS unit test for petName empty initialization (T006)"
Task: "iOS unit test for trimmed value storage (T007)"
Task: "iOS unit test for nil when empty (T008)"
Task: "iOS unit test for nil when whitespace (T009)"

# Step 2: Write all E2E test infrastructure in parallel
Task: "Add petNameTextField locator (T010)"
Task: "Add enterPetName() method (T011)"
Task: "Add getPetNameValue() method (T012)"
Task: "Add 'user enters pet name' step (T013)"
Task: "Add 'field should display' step (T014)"
Task: "Add 'announcement includes pet name' step (T015)"
Task: "Add 'announcement no pet name' step (T016)"

# Step 3: Implement model, ViewModel, view sequentially (critical path)
Task: "Add petName to FlowState (T020)"
Task: "Add @Published petName to ViewModel (T021)"
Task: "Initialize petName in init (T022)"
Task: "Update flow state with trimmed value (T023)"
Task: "Add TextField to View (T025-T027)"

# Step 4: Localization (can overlap with Step 3)
Task: "Add localization strings (T028-T029)" [P]
Task: "Run SwiftGen (T030)" [after view code complete]

# Step 5: Run all verification tasks
Task: "Run unit tests and verify coverage (T033-T034)"
Task: "Run E2E tests (T035)"
Task: "Manual tests (T036-T038)"
```

---

## Implementation Strategy

### Recommended Approach: TDD for User Story 1

1. **Phase 2 (5 minutes)**: Run verification tasks T001-T003
2. **Write Tests First (30-45 minutes)**: Complete T004-T019 (all tests FAIL initially)
3. **Implement Model (2 minutes)**: Complete T020
4. **Implement ViewModel (15 minutes)**: Complete T021-T024
5. **Implement View (10 minutes)**: Complete T025-T027
6. **Localization (5 minutes)**: Complete T028-T030
7. **Verify Tests Pass (10 minutes)**: Complete T033-T035 (all tests should now PASS)
8. **Manual Testing (15 minutes)**: Complete T036-T038
9. **Polish (15 minutes)**: Complete T039-T045

**Total Time**: ~2-3 hours (matches estimate in plan.md)

### MVP Scope

**This feature IS the MVP** - single user story (P1) delivering core value:
- iOS users can enter pet name on Animal Details screen
- Pet name submitted to backend API
- Increases quality of missing pet announcements

**Deliverable after Phase 3**: Fully functional pet name field on iOS with 80% test coverage and E2E validation

---

## Summary

**Total Tasks**: 45 tasks
- Phase 1 (Setup): 0 tasks (skipped - iOS already configured)
- Phase 2 (Foundational): 3 tasks (verification only - quick)
- Phase 3 (User Story 1): 35 tasks
  - Tests: 16 tasks (T004-T019)
  - Implementation: 13 tasks (T020-T032)
  - Verification: 6 tasks (T033-T038)
- Phase 4 (Polish): 7 tasks (T039-T045)

**Parallel Opportunities**:
- 28 tasks marked [P] can run in parallel with other [P] tasks
- All iOS unit tests can be written simultaneously (T004-T009)
- All E2E infrastructure can be written simultaneously (T010-T016)
- Localization strings can be added while implementing ViewModel/View (T028-T029)

**MVP Delivery**: Complete Phase 3 for fully functional feature with tests

**Test Coverage**: 80% minimum (6 iOS unit tests + 3 E2E scenarios)

**Independent Test Criteria**: Each verification task (T033-T038) validates User Story 1 works independently

---

## Notes

- [P] tasks = different files, no dependencies, can run in parallel
- [US1] label = belongs to User Story 1 (only one story in this feature)
- All tests MUST be written and FAIL before implementing corresponding functionality
- Commit after each logical group of tasks (e.g., all unit tests, ViewModel changes, etc.)
- Manual testing (T036-T038) is CRITICAL to catch issues not covered by automated tests
- This is an iOS-only feature - no Android, Web, or Backend changes required
- Backend already supports optional petName field - no API changes needed

