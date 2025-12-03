# Tasks: iOS Report Created Confirmation Screen

**Feature Branch**: `043-ios-report-created-screen`  
**Input**: Design documents from `/specs/043-ios-report-created-screen/`  
**Prerequisites**: plan.md, spec.md, data-model.md, research.md, quickstart.md, contracts/

**Platform Scope**: iOS-only feature (no Android, Web, or Backend changes)

**Tests**: Test requirements for this iOS-only project:

**MANDATORY - iOS Unit Tests**:
- Location: `/iosApp/iosAppTests/` (XCTest)
- Coverage: 80% line + branch coverage
- Scope: FlowState updates, ViewModel logic (password display, clipboard copy)
- Run: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
- Convention: MUST follow Given-When-Then structure with descriptive names

**MANDATORY - End-to-End Tests**:
- Mobile: `/e2e-tests/mobile/specs/report-created-confirmation.spec.ts` (Appium + TypeScript)
- All 3 user stories MUST have E2E test coverage
- Use Screen Object Model pattern
- Convention: MUST structure scenarios with Given-When-Then phases

**Organization**: Tasks are grouped by user story to enable independent testing of each acceptance criterion.

---

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Localization & Design Constants)

**Purpose**: Add localized strings and create design constants extension for Figma specs

- [ ] T001 [P] Add English localization keys to `/iosApp/iosApp/Resources/en.lproj/Localizable.strings` (5 new keys: report_created.title, report_created.body_paragraph_1, report_created.body_paragraph_2, report_created.code_copied, existing close button key)
- [ ] T002 [P] Add Polish localization keys to `/iosApp/iosApp/Resources/pl.lproj/Localizable.strings` (5 new keys matching English)
- [ ] T003 Run SwiftGen to regenerate `L10n.swift` from updated Localizable.strings files
- [ ] T004 [P] Create design constants extension in `/iosApp/iosApp/Features/ReportMissingPet/Views/Summary/SummaryView+Constants.swift` (colors, fonts, spacing, dimensions from Figma)
- [ ] T005 [P] Verify `Color+Hex` extension exists at `/iosApp/iosApp/FoundationAdditions/Color+Hex.swift` (supports 6-char RGB and 8-char ARGB formats)

---

## Phase 2: Foundational (Add managementPassword Property)

**Purpose**: Add managementPassword property to flow state for confirmation screen

**⚠️ MERGE CONFLICT WARNING**: Branch `035-ios-owners-details-screen` already has `managementPassword` property AND uses `OwnerContactDetails` struct instead of separate `contactEmail`/`contactPhone` fields. When merging branches, resolve conflicts by:
1. Keeping `managementPassword` property (already on 035)
2. Accepting contact details structure from branch being merged into
3. Ensuring `clear()` method clears managementPassword

- [ ] T006 Add `@Published var managementPassword: String?` property to `ReportMissingPetFlowState` in `/iosApp/iosApp/Features/ReportMissingPet/Models/ReportMissingPetFlowState.swift` (add after Step 4 contact details section with MARK comment "// MARK: - Submission Result")
- [ ] T007 Update `ReportMissingPetFlowState.clear()` method to clear managementPassword (add `managementPassword = nil` in same file)
- [ ] T008 [P] Verify `ToastView` component exists at `/iosApp/iosApp/Features/ReportMissingPet/Views/Components/ToastView.swift`
- [ ] T009 [P] Verify `ToastScheduler` protocol and implementation exist at `/iosApp/iosApp/Features/ReportMissingPet/Services/ToastScheduler.swift`
- [ ] T010 [P] Verify `ToastSchedulerFake` exists at `/iosApp/iosAppTests/Features/ReportMissingPet/Support/ToastSchedulerFake.swift`

**Checkpoint**: Foundation ready - User Story implementation can now begin

---

## Phase 3: User Stories 1 & 2 (P1) - Confirmation Screen with Password 🎯 MVP

**Combined Goal**: 
- US1: Display confirmation messaging (title + body paragraphs) matching Figma design
- US2: Display management password in gradient module with clipboard copy functionality

**Why Combined**: Both are P1 priority and implemented in the same SwiftUI view file. Tests remain separate to verify each acceptance criterion independently.

**Independent Tests**: 
- US1: Verify title/body render correctly independent of password logic
- US2: Verify password display, copy, and nil handling independent of messaging

### Tests for User Stories 1 & 2 (MANDATORY) ✅

**iOS Unit Tests**:

- [ ] T011 [P] [US1] Unit test for `ReportMissingPetFlowState` managementPassword property in `/iosApp/iosAppTests/Features/ReportMissingPet/Models/ReportMissingPetFlowStateTests.swift` (test property exists, clear() sets to nil)
- [ ] T012 [P] [US2] Unit test for `SummaryViewModel.displayPassword` when password is nil in `/iosApp/iosAppTests/Features/ReportMissingPet/Views/SummaryViewModelTests.swift` (expect empty string)
- [ ] T013 [P] [US2] Unit test for `SummaryViewModel.displayPassword` when password exists in same file (expect password value returned)
- [ ] T014 [P] [US2] Unit test for `SummaryViewModel.copyPasswordToClipboard()` when password exists in same file (expect UIPasteboard contains password, showsCodeCopiedToast is true, toast scheduled for 2 seconds)
- [ ] T015 [P] [US2] Unit test for `SummaryViewModel.copyPasswordToClipboard()` when password is nil in same file (expect clipboard unchanged, showsCodeCopiedToast remains false)

**End-to-End Tests**:

- [ ] T016 [P] [US1] Update `SummaryScreen` page object with selectors in `/e2e-tests/mobile/screens/SummaryScreen.ts` (title, bodyParagraph1, bodyParagraph2, password, toast, closeButton)
- [ ] T017 [P] [US1] Create E2E test for confirmation messaging in `/e2e-tests/mobile/specs/report-created-confirmation.spec.ts` (verify title "Report created" and both paragraphs display)
- [ ] T018 [P] [US2] Create E2E test for password display in same spec file (verify password is visible and non-empty)
- [ ] T019 [P] [US2] Create E2E test for clipboard copy in same spec file (tap password, verify toast appears with "Code copied to clipboard" message)

### Implementation for User Stories 1 & 2

**ViewModel Updates**:

- [ ] T020 [US1+US2] Update `SummaryViewModel` to add ToastScheduler dependency in `/iosApp/iosApp/Features/ReportMissingPet/Views/Summary/SummaryViewModel.swift` (add private let toastScheduler: ToastSchedulerProtocol property)
- [ ] T021 [US1+US2] Update `SummaryViewModel` init to accept toastScheduler parameter in same file (update init signature)
- [ ] T022 [P] [US2] Add `@Published var showsCodeCopiedToast = false` property to `SummaryViewModel` in same file
- [ ] T023 [P] [US2] Add `displayPassword` computed property to `SummaryViewModel` in same file (returns flowState.managementPassword ?? "")
- [ ] T024 [US2] Implement `copyPasswordToClipboard()` method in `SummaryViewModel` in same file (copy to UIPasteboard, show toast for 2 seconds)
- [ ] T025 [US2] Add deinit to `SummaryViewModel` to cancel scheduled toasts in same file

**View Implementation**:

- [ ] T026 [US1+US2] Replace placeholder `SummaryView` implementation in `/iosApp/iosApp/Features/ReportMissingPet/Views/Summary/SummaryView.swift` with report confirmation UI (white background, ScrollView with VStack for content)
- [ ] T027 [P] [US1] Add title text to `SummaryView` using `L10n.ReportCreated.title` with Constants.titleFont and titleColor
- [ ] T028 [P] [US1] Add body paragraph 1 text to `SummaryView` using `L10n.ReportCreated.bodyParagraph1` with Constants.bodyFont, bodyColor, and lineSpacing
- [ ] T029 [P] [US1] Add body paragraph 2 text to `SummaryView` using `L10n.ReportCreated.bodyParagraph2` with same styling as paragraph 1
- [ ] T030 [US2] Create `passwordContainer` computed property in `SummaryView` with gradient background (LinearGradient from Constants.gradientStartColor to gradientEndColor, glow overlay with blur)
- [ ] T031 [US2] Add password text display in `passwordContainer` using `viewModel.displayPassword` with Constants.passwordFont, passwordKerning, white color
- [ ] T032 [US2] Wrap `passwordContainer` in Button with `viewModel.copyPasswordToClipboard` action
- [ ] T033 [US2] Add toast display in bottom VStack when `viewModel.showsCodeCopiedToast` is true using existing `ToastView` component
- [ ] T034 [P] [US1] Add `.accessibilityIdentifier("summary.title")` to title text
- [ ] T035 [P] [US1] Add `.accessibilityIdentifier("summary.bodyParagraph1")` to first body paragraph
- [ ] T036 [P] [US1] Add `.accessibilityIdentifier("summary.bodyParagraph2")` to second body paragraph
- [ ] T037 [P] [US2] Add `.accessibilityIdentifier("summary.password")` to password text
- [ ] T038 [P] [US2] Add `.accessibilityIdentifier("summary.toast")` to toast view
- [ ] T039 [P] [US1+US2] Add SwiftUI preview for `SummaryView` with mock data (one with password "5216577", one with nil password)

**Integration**:

- [ ] T040 [US1+US2] Update `ReportMissingPetCoordinator` to pass ToastScheduler to SummaryViewModel init in `/iosApp/iosApp/Features/ReportMissingPet/Coordinators/ReportMissingPetCoordinator.swift` (only change: add toastScheduler parameter to existing SummaryViewModel init call)

**Checkpoint**: At this point, User Stories 1 and 2 should be fully functional - confirmation screen displays with title, body text, and copyable password

---

## Phase 4: User Story 3 (P2) - Close Button Functionality

**Goal**: Close button dismisses entire flow and returns to home/dashboard

**Independent Test**: Trigger Close button via UI automation, verify navigation resets to home/dashboard

**⚠️ NOTE**: Close button functionality (`viewModel.handleSubmit()` → coordinator dismisses flow) is already implemented in existing SummaryView/SummaryViewModel. This phase only adds tests to verify the behavior.

### Tests for User Story 3 (MANDATORY) ✅

**iOS Unit Tests**:

- [ ] T041 [P] [US3] Unit test for `SummaryViewModel.handleSubmit()` in `/iosApp/iosAppTests/Features/ReportMissingPet/Views/SummaryViewModelTests.swift` (verify onSubmit closure is called)

**End-to-End Tests**:

- [ ] T042 [P] [US3] Create E2E test for Close button in `/e2e-tests/mobile/specs/report-created-confirmation.spec.ts` (tap Close, verify flow dismissed and home/dashboard displayed)

### Implementation for User Story 3

**View Updates**:

- [ ] T043 [US3] Verify Close button exists in `SummaryView` at bottom of screen in `/iosApp/iosApp/Features/ReportMissingPet/Views/Summary/SummaryView.swift` (uses `L10n.ReportMissingPet.Button.close`, calls `viewModel.handleSubmit()`, styled with Constants)
- [ ] T044 [P] [US3] Add `.accessibilityIdentifier("summary.closeButton")` to Close button in same file

**Checkpoint**: At this point, all 3 user stories should be fully functional and independently testable

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Final verification and documentation

- [ ] T045 Run iOS unit tests with coverage: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES`
- [ ] T046 Verify 80% test coverage for SummaryViewModel and ReportMissingPetFlowState in Xcode coverage report
- [ ] T047 Run E2E tests: `cd e2e-tests && npm run test:mobile:ios`
- [ ] T048 Visual QA: Compare SummaryView against Figma design (typography, colors, spacing, gradient) - tolerance ±1pt
- [ ] T049 Manual test: Switch device to Polish language, verify all text displays correctly
- [ ] T050 Manual test: Test with nil managementPassword (should display empty string in gradient pill, no crash)
- [ ] T051 Manual test: Test clipboard copy on physical device (tap password, verify toast, paste in Notes app)
- [ ] T052 Code review: Verify MVVM pattern compliance (ViewModel business logic, View presentation only)
- [ ] T053 Code review: Verify all interactive elements have accessibility identifiers
- [ ] T054 Code review: Verify Given-When-Then test structure in all unit tests

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion (localization must be ready)
- **User Stories 1 & 2 (Phase 3)**: Depends on Foundational phase completion
- **User Story 3 (Phase 4)**: Depends on Phase 3 completion (Close button is part of same view)
- **Polish (Phase 5)**: Depends on all user stories being complete

### Task Dependencies Within Phases

**Phase 1 (Setup)**:
- T001, T002, T004, T005 can run in parallel [P]
- T003 depends on T001 and T002 (must add strings before running SwiftGen)

**Phase 2 (Foundational)**:
- T006-T007 must complete sequentially (add property, then update clear method in same file)
- T008-T010 can run in parallel [P] (verification only)

**Phase 3 (User Stories 1 & 2)**:
- Tests (T011-T019) should be written first and MUST fail before implementation
- Tests within the phase marked [P] can run in parallel
- ViewModel updates (T020-T025): T020-T021 must complete before T022-T025
- View implementation (T026-T039): T026 must complete before T027-T039, then tasks marked [P] can run in parallel
- T040 (coordinator update) depends on T021 (ViewModel init signature change)

**Phase 4 (User Story 3)**:
- Tests (T041-T042) can run in parallel [P]
- T043 is verification only (Close button already exists)
- T044 can run in parallel with T041-T042 [P]

**Phase 5 (Polish)**:
- T045-T054 should run sequentially (tests first, then manual QA, then code review)

### Parallel Opportunities

- **Phase 1**: T001, T002, T004, T005 (4 tasks in parallel)
- **Phase 2**: T008-T010 (3 verification tasks in parallel, after T006-T007 complete)
- **Phase 3**: 
  - Unit tests: T011, T012, T013, T014, T015 (5 tests in parallel)
  - E2E tests: T016, T017, T018, T019 (4 tests in parallel)
  - View elements: T027-T029, T034-T038 (8 accessibility/text tasks in parallel)
- **Phase 4**: T041, T042, T044 (3 tasks in parallel)

---

## Parallel Example: User Stories 1 & 2

```bash
# Write all unit tests in parallel:
Task T011: Test managementPassword property (US1)
Task T012: Test displayPassword when nil (US2)
Task T013: Test displayPassword when exists (US2)
Task T014: Test copyPasswordToClipboard when exists (US2)
Task T015: Test copyPasswordToClipboard when nil (US2)

# Then implement ViewModel updates:
Task T020-T021: Update ViewModel signature and dependencies (sequential)
Task T022-T025: Add password display and copy logic (can partially parallelize)

# Then implement View (after T026 creates base structure):
Task T027: Add title text (US1) [P]
Task T028: Add paragraph 1 (US1) [P]
Task T029: Add paragraph 2 (US1) [P]
Task T030-T032: Add password module (US2) (sequential within, but separate from text tasks)
Task T034-T038: Add accessibility identifiers (US1+US2) [P]
```

---

## Implementation Strategy

### MVP First (User Stories 1 & 2 Only)

1. Complete Phase 1: Setup (localization + constants)
2. Complete Phase 2: Foundational (verify existing infrastructure)
3. Complete Phase 3: User Stories 1 & 2 (confirmation screen with password)
4. **STOP and VALIDATE**: Test US1 and US2 independently
5. Deploy/demo if ready (MVP with confirmation + password display)

### Full Feature Delivery

1. Complete Phase 1-3 (MVP)
2. Complete Phase 4: User Story 3 (Close button tests - functionality already exists)
3. Complete Phase 5: Polish (visual QA, localization testing, code review)
4. Feature complete and ready for release

### Critical Path

```
Setup → Foundational → ViewModel Updates → View Implementation → Integration → Tests → Polish
 ↓         ↓              ↓                    ↓                   ↓          ↓        ↓
T001-T005  T006-T010      T020-T025            T026-T039           T040      T011-T044 T045-T054
```

Longest path: ~14 sequential steps (with parallelization, many tasks can overlap within phases)

---

## Summary

**Total Tasks**: 54 tasks
- Phase 1 (Setup): 5 tasks
- Phase 2 (Foundational): 5 tasks (2 add + 3 verify)
- Phase 3 (US1+US2 - MVP): 29 tasks (9 tests + 20 implementation)
- Phase 4 (US3): 4 tasks (2 tests + 2 verification)
- Phase 5 (Polish): 10 tasks

**Task Breakdown by User Story**:
- User Story 1 (P1 - Confirmation messaging): 12 tasks (tests + implementation)
- User Story 2 (P1 - Password display/copy): 21 tasks (tests + implementation)
- User Story 3 (P2 - Close button): 4 tasks (tests + verification)
- Shared/Setup: 17 tasks (setup + foundational + polish)

**Parallel Opportunities**: 23 tasks marked [P] can run in parallel within their phases

**Independent Test Criteria**:
- US1: Launch screen with mock data, verify title and body paragraphs display with correct typography
- US2: Provide mock password via flowState, verify gradient module renders, password displays, clipboard copy works, nil displays as empty string
- US3: Tap Close button via E2E test, verify flow dismisses and returns to home/dashboard

**Suggested MVP Scope**: Phase 1-3 (US1+US2) delivers functional confirmation screen with copyable password - ready for user testing

**Platform**: iOS-only (Swift/SwiftUI) - no Android, Web, or Backend tasks required

---

## Notes

- [P] tasks = different files, no dependencies - can run in parallel
- [Story] label maps task to specific user story for traceability
- Tests MUST be written first and MUST fail before implementation (TDD approach)
- Each user story is independently testable per acceptance criteria in spec.md
- Commit after each task or logical group
- Stop at MVP checkpoint (Phase 3) to validate core functionality before moving to Phase 4
- US1 and US2 are combined in implementation (same view) but tested separately (independent acceptance criteria)
- **MERGE CONFLICT**: Branch 035 already has `managementPassword` property - when merging, accept the property from 035 and resolve contact details structure conflicts

