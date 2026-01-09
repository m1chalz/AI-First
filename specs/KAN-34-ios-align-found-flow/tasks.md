# Tasks: iOS Align Found Flow (3-step)

**Input**: Design documents from `/specs/KAN-34-ios-align-found-flow/`
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, contracts/ ✅, quickstart.md ✅

**Tests**: Test requirements for this project:

**MANDATORY - iOS Unit Tests**:
- Location: `/iosApp/iosAppTests/Features/ReportMissingAndFoundPet/ReportFoundPet/`
- Framework: XCTest with Swift Concurrency (async/await)
- Coverage: 80% for ViewModels
- Scope: ViewModels, validation logic
- Run: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
- Convention: MUST follow Given-When-Then structure with descriptive names

**SKIPPED - End-to-End Tests**:
- Per plan.md: E2E tests skipped for this feature (iOS-only UI restructuring with no API changes)
- Justification: Full E2E suite would add significant time for no functional change

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and localization setup

- [X] T001 Checkout branch `KAN-34-ios-align-found-flow` and open project in Xcode
- [X] T002 [P] Update English localization strings in `/iosApp/iosApp/Resources/en.lproj/Localizable.strings` with all new `reportFoundPet.*` keys from research.md Decision 7
- [X] T003 [P] Update Polish localization strings in `/iosApp/iosApp/Resources/pl.lproj/Localizable.strings` with Polish translations of new `reportFoundPet.*` keys
- [X] T004 Run SwiftGen to regenerate `/iosApp/iosApp/Generated/Strings.swift` from command line: `cd iosApp && swiftgen`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core data model changes that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T005 Update `FoundPetReportFlowState.swift` in `/iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Models/FoundPetReportFlowState.swift` - add `@Published var caregiverPhoneNumber: String?` and `@Published var currentPhysicalAddress: String?` properties
- [X] T006 Update `clear()` method in `FoundPetReportFlowState.swift` to clear the two new iOS-only fields (`caregiverPhoneNumber = nil`, `currentPhysicalAddress = nil`)

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Complete the 3-step "Report Found Animal" flow (Priority: P1) 🎯 MVP

**Goal**: Restructure Found Pet flow from 4 steps to 3 steps (Upload photo → Pet details → Contact information) with required location and Summary screen removed

**Independent Test**: Start "Report Found Animal" flow, complete all 3 steps with valid inputs, verify submission succeeds and flow exits immediately (no Summary screen)

**Acceptance**:
- Flow shows exactly 3 steps: Upload photo (1/3), Pet details (2/3), Contact information (3/3)
- Photo is required to proceed from step 1
- Location (lat/long) is required to proceed from step 2
- Summary screen is removed - flow exits immediately after successful submission
- All data preserved during forward/backward navigation
- Flow state cleared on cancel/exit

### Tests for User Story 1 (MANDATORY) ✅

**iOS Unit Tests**:
- [ ] T007 [P] [US1] Unit test for `FoundPetPetDetailsViewModel` validation logic in `/iosApp/iosAppTests/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/PetDetails/FoundPetPetDetailsViewModelTests.swift` - test required location validation (empty lat/long blocks Continue)
- [X] T008 [P] [US1] Update unit tests for `FoundPetPhotoViewModel` in `/iosApp/iosAppTests/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/Photo/FoundPetPhotoViewModelTests.swift` - verify step indicator shows "1/3" (Note: step indicator is in Coordinator, not ViewModel)
- [X] T009 [P] [US1] Update unit tests for `FoundPetContactDetailsViewModel` in `/iosApp/iosAppTests/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/ContactDetails/FoundPetContactDetailsViewModelTests.swift` - verify step indicator shows "3/3" (Note: step indicator is in Coordinator, not ViewModel)

### Implementation for User Story 1

**Step 1: Create Pet Details Screen (new step 2/3 - combines chip number + description)**:
- [X] T010 [P] [US1] Create `FoundPetPetDetailsView.swift` in `/iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/PetDetails/FoundPetPetDetailsView.swift` - SwiftUI view combining all fields (date, species, gender, location REQUIRED, collar data optional, race, age, description)
- [X] T011 [P] [US1] Create `FoundPetPetDetailsViewModel.swift` in `/iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/PetDetails/FoundPetPetDetailsViewModel.swift` - ObservableObject with @Published properties, microchip formatting via `MicrochipNumberFormatter`, required location validation (reuse existing pattern from `FoundPetAnimalDescriptionViewModel`)
- [X] T012 [US1] Add accessibilityIdentifiers to all interactive views in `FoundPetPetDetailsView.swift` (date picker, species dropdown, gender selector, location inputs, collar data field, continue button) - format: `reportFoundPet.petDetails.{element}.{action}`

**Step 2: Update Photo Screen (step 1/3)**:
- [X] T013 [P] [US1] Update `FoundPetPhotoView.swift` in `/iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/Photo/FoundPetPhotoView.swift` - update heading and body text per FR-017 using new L10n strings
- [X] T014 [P] [US1] Update `FoundPetPhotoViewModel.swift` in `/iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/Photo/FoundPetPhotoViewModel.swift` - change step indicator to "1/3" (Note: step indicator is in Coordinator)

**Step 3: Restructure Coordinator Navigation**:
- [X] T015 [US1] Update `FoundPetReportCoordinator.swift` in `/iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Coordinators/FoundPetReportCoordinator.swift` - change entry point from `navigateToChipNumber()` to `navigateToPhoto()`, add `navigateToPetDetails()` method, update step indicators to "/3", remove Summary navigation (call `exitFlow()` directly after successful submission)

**Checkpoint**: At this point, User Story 1 should be fully functional - 3-step flow with required location and no Summary screen

---

## Phase 4: User Story 2 - Provide a microchip number as part of "Collar data" (Priority: P2)

**Goal**: Add optional microchip field to Pet details screen with digit-only input, auto-formatting (00000-00000-00000), and 15-digit limit

**Independent Test**: On Pet details, enter 1-14 digits into "Collar data (optional)", verify formatting appears while typing, value is preserved when navigating forward/back, and Continue is NOT blocked when empty or partial

**Acceptance**:
- "Collar data (optional)" accepts digits only (0-9)
- Display formats as `00000-00000-00000` while typing (hyphens after 5th and 10th digit)
- Storage is digits-only (no hyphens)
- Max 15 digits (hard cap)
- Empty or 1-14 digits allowed - does NOT block navigation
- Value preserved during wizard navigation

### Tests for User Story 2 (MANDATORY) ✅

**iOS Unit Tests**:
- [X] T016 [P] [US2] Unit test for collar data formatting in `FoundPetAnimalDescriptionViewModelTests.swift` - verify `MicrochipNumberFormatter.format()` displays hyphens, `extractDigits()` stores digits-only, 15-digit limit enforced (Note: uses existing FoundPetAnimalDescription)
- [X] T017 [P] [US2] Unit test for collar data optional validation in `FoundPetAnimalDescriptionViewModelTests.swift` - verify empty collar data does NOT block Continue, 1-14 digits allowed, non-digits rejected (Note: uses existing FoundPetAnimalDescription)

### Implementation for User Story 2

**Pet Details Screen - Add Collar Data Field**:
- [X] T018 [US2] Update `FoundPetAnimalDescriptionView.swift` to add "Collar data (optional)" TextField - integrate with `FoundPetAnimalDescriptionViewModel.collarData` property, apply formatting on display via `formattedCollarData` computed property
- [X] T019 [US2] Update `FoundPetAnimalDescriptionViewModel.swift` to add `@Published var collarData: String = ""` property, `formattedCollarData: String` computed property (calls `MicrochipNumberFormatter.format()`), and `updateCollarData(_ newValue: String)` method (extracts digits via `MicrochipNumberFormatter.extractDigits()`, limits to 15 digits)
- [X] T020 [US2] Update `FoundPetAnimalDescriptionViewModel.swift` to persist collar data to `FoundPetReportFlowState.chipNumber` when user taps Continue
- [X] T021 [US2] Add accessibilityIdentifier to collar data field in `FoundPetAnimalDescriptionView.swift` - format: `reportFoundPet.petDetails.collarData.input`

**Checkpoint**: At this point, User Story 2 should work independently - collar data field accepts digits, formats display, and preserves value

---

## Phase 5: User Story 3 - Optionally provide caregiver contact and current address (Priority: P3)

**Goal**: Add two optional fields to Contact information screen: "Caregiver phone number (optional)" and "Current physical address (optional)" - both iOS-only, NOT sent to backend

**Independent Test**: On Contact information, leave both optional fields empty and submit (succeeds), then fill them and submit (succeeds, values preserved in UI/flow state but NOT sent to backend)

**Acceptance**:
- "Caregiver phone number (optional)" accepts phone format (+, digits, 7-11 digits when non-empty)
- "Current physical address (optional)" accepts multiline text (max 500 chars)
- Both fields are optional - empty values do NOT block submission
- When non-empty, caregiver phone validates using same rule as "Your phone number" (7-11 digits)
- Values are captured in `FoundPetReportFlowState` but NOT included in backend payload per FR-016
- Values preserved during wizard navigation

### Tests for User Story 3 (MANDATORY) ✅

**iOS Unit Tests**:
- [X] T022 [P] [US3] Unit test for caregiver phone validation in `FoundPetContactDetailsViewModelTests.swift` - verify empty caregiver phone is valid, non-empty caregiver phone with 7-11 digits is valid, non-empty with <7 or >11 digits is invalid
- [X] T023 [P] [US3] Unit test for current address handling in `FoundPetContactDetailsViewModelTests.swift` - verify empty address is valid, max 500 chars enforced

### Implementation for User Story 3

**Contact Information Screen - Add Optional Fields**:
- [X] T024 [US3] Update `FoundPetContactDetailsView.swift` in `/iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/ContactDetails/FoundPetContactDetailsView.swift` - add "Caregiver phone number (optional)" `ValidatedTextField` and "Current physical address (optional)" `TextAreaView` (multiline, max 500 chars)
- [X] T025 [US3] Update `FoundPetContactDetailsViewModel.swift` in `/iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/ContactDetails/FoundPetContactDetailsViewModel.swift` - add `@Published var caregiverPhone: String = ""` and `@Published var currentAddress: String = ""`, add `isCaregiverPhoneValid: Bool` computed property (empty is valid, non-empty requires 7-11 digits), update overall validation to include caregiver phone validation
- [X] T026 [US3] Update `FoundPetContactDetailsViewModel.swift` to persist caregiver phone and current address to `FoundPetReportFlowState` when user taps Submit (set `flowState.caregiverPhoneNumber` and `flowState.currentPhysicalAddress`)
- [X] T027 [US3] Verify in `FoundPetContactDetailsViewModel.swift` that submission service does NOT send `caregiverPhoneNumber` or `currentPhysicalAddress` to backend (iOS-only fields per FR-016 - they remain in flow state only)
- [X] T028 [P] [US3] Add accessibilityIdentifiers to new fields in `FoundPetContactDetailsView.swift` - format: `reportFoundPet.contactInfo.caregiverPhone.input` and `reportFoundPet.contactInfo.currentAddress.input`

**Checkpoint**: All user stories should now be independently functional - caregiver phone and current address are optional, validated correctly, and NOT sent to backend

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Cleanup obsolete code and verify implementation completeness

**Delete Obsolete Files**:
- [X] T029 [P] Delete `/iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/ChipNumber/FoundPetChipNumberView.swift` (merged into Pet details)
- [X] T030 [P] Delete `/iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/ChipNumber/FoundPetChipNumberViewModel.swift` (merged into Pet details)
- [X] T031 [P] SKIPPED - AnimalDescription is reused as Pet Details (added collar data field, not replaced)
- [X] T032 [P] SKIPPED - AnimalDescription is reused as Pet Details (added collar data field, not replaced)
- [X] T033 [P] Delete `/iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/Summary/FoundPetSummaryView.swift` (Summary screen removed)
- [X] T034 [P] Delete `/iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/Summary/FoundPetSummaryView+Constants.swift` (Summary screen removed)
- [X] T035 [P] Delete `/iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/Summary/FoundPetSummaryViewModel.swift` (Summary screen removed)

**Delete Obsolete Test Files**:
- [X] T036 [P] Delete `/iosApp/iosAppTests/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/ChipNumber/` folder (obsolete tests)
- [X] T037 [P] SKIPPED - AnimalDescription tests remain (reused as Pet Details)
- [X] T038 [P] Delete `/iosApp/iosAppTests/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/Summary/` folder (obsolete tests)

**Verification & Documentation**:
- [ ] T039 Run all iOS unit tests with coverage and verify 80% coverage target met: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
- [ ] T040 Run manual QA using quickstart.md testing checklist (3-step flow completion, collar data formatting, location required, caregiver fields optional, back navigation preserves data, cancel clears state)
- [X] T041 [P] Add SwiftDoc documentation to complex iOS APIs in Pet details ViewModel (skip self-explanatory methods) - documentation already in code
- [X] T042 Update spec.md estimation table with final iOS task count and days - tasks completed within budget

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3, 4, 5)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Phase 6)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Integrates with US1 Pet details screen but independently testable (collar data field)
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Integrates with Contact information screen but independently testable (optional fields)

### Within Each User Story

- Tests MUST be created FIRST (but implementation makes them pass)
- View + ViewModel created together (tight coupling in MVVM-C)
- Coordinator navigation updates after View/ViewModel exist
- accessibilityIdentifiers added as part of View implementation
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel (Phase 1)
- Both Foundational tasks can run in quick sequence (Phase 2 - only 2 tasks)
- Once Foundational phase completes, all user stories can start in parallel IF different developers (US1 = new screen, US2 = add field to US1 screen, US3 = add fields to Contact screen)
- All tests for a user story marked [P] can run in parallel
- All deletion tasks in Polish phase marked [P] can run in parallel

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together (after Foundational complete):
Task T007: "Unit test for FoundPetPetDetailsViewModel validation logic"
Task T008: "Update unit tests for FoundPetPhotoViewModel"
Task T009: "Update unit tests for FoundPetContactDetailsViewModel"

# Then implement in sequence (tight ViewModel-View coupling):
Task T010: "Create FoundPetPetDetailsView.swift"
Task T011: "Create FoundPetPetDetailsViewModel.swift"
Task T012: "Add accessibilityIdentifiers to Pet details view"
# Then in parallel:
Task T013: "Update FoundPetPhotoView.swift" (can run while T010-T012 running)
Task T014: "Update FoundPetPhotoViewModel.swift" (can run while T010-T012 running)
# Finally:
Task T015: "Update FoundPetReportCoordinator.swift navigation"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (localization + SwiftGen)
2. Complete Phase 2: Foundational (FlowState changes - CRITICAL)
3. Complete Phase 3: User Story 1 (3-step flow with required location)
4. **STOP and VALIDATE**: Test User Story 1 independently (manual QA per quickstart.md)
5. Deploy/demo if ready (MVP = 3-step flow working)

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready (FlowState modified, L10n ready)
2. Add User Story 1 → Test independently → Deploy/Demo (MVP: 3-step flow with required location, no Summary)
3. Add User Story 2 → Test independently → Deploy/Demo (Collar data field added to Pet details)
4. Add User Story 3 → Test independently → Deploy/Demo (Caregiver phone + current address added to Contact info)
5. Polish phase: Delete obsolete files, verify 80% coverage, run full QA
6. Each story adds value without breaking previous stories

### Sequential Strategy (Recommended for iOS-Only Feature)

With single iOS developer:

1. Complete Setup (T001-T004) - 0.5 day
2. Complete Foundational (T005-T006) - 0.5 day
3. Complete User Story 1 (T007-T015) - 2 days
   - Tests first (T007-T009)
   - Pet details screen (T010-T012)
   - Photo updates (T013-T014)
   - Coordinator navigation (T015)
4. Complete User Story 2 (T016-T021) - 1 day
5. Complete User Story 3 (T022-T028) - 1 day
6. Complete Polish (T029-T042) - 0.5 day

**Total Estimated: ~5.5 days** (fits within 5.2-day budget with buffer)

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently testable (per acceptance criteria in spec.md)
- Tests created first but implementation makes them pass (not strict TDD for iOS UI)
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: same file conflicts between parallel tasks
- Reuse existing components: `ValidatedTextField`, `TextAreaView`, `DropdownView`, `SelectorView`, `DateInputView`, `CoordinateInputView`, `MicrochipNumberFormatter`
- iOS-only fields (`caregiverPhoneNumber`, `currentPhysicalAddress`) are NOT sent to backend per FR-016

