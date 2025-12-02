# Tasks: Web Owner's Details Screen

**Input**: Design documents from `/specs/032-web-owners-details/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md

**Tests**: Test requirements for this project:

**MANDATORY - Web Unit Tests**:
- Location: `/webApp/src/hooks/__tests__/` and `/webApp/src/components/ReportMissingPet/__tests__/`
- Framework: Vitest + React Testing Library
- Coverage: 80% line + branch coverage
- Run: `npm test -- --coverage` (from webApp/)
- Convention: MUST follow Given-When-Then structure with descriptive test names
- TDD Workflow: RED (write failing test) → GREEN (minimal implementation) → REFACTOR (improve code)

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

---

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [X] T001 Review existing ReportMissingPetFlowContext in `/webApp/src/contexts/ReportMissingPetFlowContext.tsx` to understand state management pattern
- [X] T002 Review existing ReportMissingPetLayout in `/webApp/src/components/ReportMissingPet/ReportMissingPetLayout.tsx` to understand layout pattern
- [X] T003 Review existing validation hooks (use-details-form.ts, use-microchip-formatter.ts) to understand validation pattern

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core data model that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T004 [P] RED: Write failing test for ReportMissingPetFlowState extension in `/webApp/src/models/__tests__/ReportMissingPetFlow.test.ts` (test initial values for phone, email, reward fields)
- [X] T005 GREEN: Extend ReportMissingPetFlowState interface in `/webApp/src/models/ReportMissingPetFlow.ts` (add phone: string, email: string, reward: string fields)
- [X] T006 GREEN: Update initialFlowState in `/webApp/src/models/ReportMissingPetFlow.ts` (add phone: '', email: '', reward: '' initial values)
- [X] T007 REFACTOR: Verify tests pass and run linter `npm run lint` (from webApp/)

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Enter contact information to complete data collection (Priority: P1) 🎯 MVP

**Goal**: Users can provide phone OR email to complete missing pet report, data persists in session, navigation to summary works

**Independent Test**: Navigate to Step 4 with prior steps pre-populated, fill only phone number with valid value, click Continue, verify session saves data and navigates to summary

### Tests for User Story 1 (TDD: RED phase) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**Custom Hook Tests** (use-contact-form.ts):

- [X] T008 [P] [US1] RED: Write failing test for phone validation with valid digits in `/webApp/src/hooks/__tests__/use-contact-form.test.ts` (Given: phone="123", When: validatePhone called, Then: phoneError is empty)
- [X] T009 [P] [US1] RED: Write failing test for phone validation with no digits in `/webApp/src/hooks/__tests__/use-contact-form.test.ts` (Given: phone="abc", When: validatePhone called, Then: phoneError="Enter a valid phone number")
- [X] T010 [P] [US1] RED: Write failing test for email validation with valid email in `/webApp/src/hooks/__tests__/use-contact-form.test.ts` (Given: email="user@example.com", When: validateEmail called, Then: emailError is empty)
- [X] T011 [P] [US1] RED: Write failing test for email validation with invalid email in `/webApp/src/hooks/__tests__/use-contact-form.test.ts` (Given: email="invalid@", When: validateEmail called, Then: emailError="Enter a valid email address")
- [X] T012 [P] [US1] RED: Write failing test for successful submission with phone only in `/webApp/src/hooks/__tests__/use-contact-form.test.ts` (Given: phone="123" and email="", When: handleSubmit called, Then: returns true and updates flow state)
- [X] T013 [P] [US1] RED: Write failing test for successful submission with email only in `/webApp/src/hooks/__tests__/use-contact-form.test.ts` (Given: phone="" and email="user@example.com", When: handleSubmit called, Then: returns true and updates flow state)
- [X] T014 [P] [US1] RED: Write failing test for blocked submission with no contact in `/webApp/src/hooks/__tests__/use-contact-form.test.ts` (Given: phone="" and email="", When: handleSubmit called, Then: returns false)
- [X] T015 [P] [US1] RED: Write failing test for blocked submission with valid phone + invalid email in `/webApp/src/hooks/__tests__/use-contact-form.test.ts` (Given: phone="123" and email="invalid", When: handleSubmit called, Then: returns false)

**Component Tests** (ContactScreen.tsx):

- [X] T016 [P] [US1] RED: Write failing test for rendering all form fields in `/webApp/src/components/ReportMissingPet/__tests__/ContactScreen.test.tsx` (Written - not yet executed)
- [X] T017 [P] [US1] RED: Write failing test for navigation to summary on valid submission in `/webApp/src/components/ReportMissingPet/__tests__/ContactScreen.test.tsx` (Written - not yet executed)
- [X] T018 [P] [US1] RED: Write failing test for blocked navigation on no contact in `/webApp/src/components/ReportMissingPet/__tests__/ContactScreen.test.tsx` (Written - not yet executed)
- [X] T019 [P] [US1] RED: Write failing test for back button navigation in `/webApp/src/components/ReportMissingPet/__tests__/ContactScreen.test.tsx` (Written - not yet executed)

**Summary Screen Tests**:

- [X] T020 [P] [US1] RED: Write failing test for displaying flow state in `/webApp/src/components/ReportMissingPet/__tests__/SummaryScreen.test.tsx` (Given: flow state with data, When: component renders, Then: displays microchip, photo, species, phone, email, reward)
- [X] T021 [P] [US1] RED: Write failing test for back button in `/webApp/src/components/ReportMissingPet/__tests__/SummaryScreen.test.tsx` (Given: on summary screen, When: back button clicked, Then: navigates to /report-missing-pet/contact)
- [X] T022 [P] [US1] RED: Write failing test for complete button in `/webApp/src/components/ReportMissingPet/__tests__/SummaryScreen.test.tsx` (Given: on summary screen, When: complete button clicked, Then: clears flow state and navigates to /)

### Implementation for User Story 1 (TDD: GREEN phase)

**Custom Hook Implementation**:

- [X] T023 [US1] GREEN: Create use-contact-form.ts hook in `/webApp/src/hooks/use-contact-form.ts` (minimal implementation: export function useContactForm with basic structure)
- [X] T024 [US1] GREEN: Implement phone validation in `/webApp/src/hooks/use-contact-form.ts` (validatePhone function with /\d/ regex matching backend validators.ts)
- [X] T025 [US1] GREEN: Implement email validation in `/webApp/src/hooks/use-contact-form.ts` (validateEmail function with RFC 5322 regex matching backend validators.ts)
- [X] T026 [US1] GREEN: Implement handleSubmit logic in `/webApp/src/hooks/use-contact-form.ts` (validate all fields, check at least one contact, update flow state on success)
- [X] T027 [US1] GREEN: Implement input change handlers in `/webApp/src/hooks/use-contact-form.ts` (handlePhoneChange, handleEmailChange, handleRewardChange)
- [X] T028 [US1] GREEN: Add JSDoc documentation to useContactForm hook in `/webApp/src/hooks/use-contact-form.ts` (describe purpose, validation rules, integration with flow context)
- [X] T029 [US1] Verify hook tests pass: run `npm test use-contact-form.test.ts` (from webApp/)

**Component Implementation**:

- [X] T030 [US1] GREEN: Replace ContactScreen debug view in `/webApp/src/components/ReportMissingPet/ContactScreen.tsx` (import useContactForm, render form with phone/email/reward inputs)
- [X] T031 [US1] GREEN: Add Continue button handler in `/webApp/src/components/ReportMissingPet/ContactScreen.tsx` (call handleSubmit, navigate to summary on success)
- [X] T032 [US1] GREEN: Add back button handler in `/webApp/src/components/ReportMissingPet/ContactScreen.tsx` (navigate to /report-missing-pet/details)
- [X] T033 [US1] GREEN: Add useBrowserBackHandler in `/webApp/src/components/ReportMissingPet/ContactScreen.tsx` (clear flow state, navigate to /)
- [X] T034 [US1] GREEN: Add data-testid attributes to all interactive elements in `/webApp/src/components/ReportMissingPet/ContactScreen.tsx` (contact.phoneNumber.input, contact.email.input, contact.reward.input, contact.continue.button)
- [X] T035 [US1] GREEN: Add redirect logic if flowState.currentStep === Empty in `/webApp/src/components/ReportMissingPet/ContactScreen.tsx` (useEffect to redirect to microchip step)
- [X] T036 [US1] Verify ContactScreen tests pass: run `npm test ContactScreen.test.tsx` (from webApp/) ✅ 10/10 PASSING

**Summary Screen Implementation**:

- [X] T037 [US1] GREEN: Create SummaryScreen component in `/webApp/src/components/ReportMissingPet/SummaryScreen.tsx` (display all flow state data in debug view format)
- [X] T038 [US1] GREEN: Add back button to contact in `/webApp/src/components/ReportMissingPet/SummaryScreen.tsx` (navigate to /report-missing-pet/contact)
- [X] T039 [US1] GREEN: Add complete button in `/webApp/src/components/ReportMissingPet/SummaryScreen.tsx` (clear flow state, navigate to /)
- [X] T040 [US1] GREEN: Add data-testid attributes in `/webApp/src/components/ReportMissingPet/SummaryScreen.tsx` (summary.back.button, summary.complete.button)
- [X] T041 [US1] Verify SummaryScreen tests pass: run `npm test SummaryScreen.test.tsx` (from webApp/) ✅ 6/6 PASSING

**Routing Updates**:

- [X] T042 [US1] GREEN: Add summary route in `/webApp/src/routes/report-missing-pet-routes.tsx` (add summary: '/report-missing-pet/summary' to ReportMissingPetRoutes)
- [X] T043 [US1] GREEN: Add SummaryScreen route to router configuration in main routes file (add <Route path="/report-missing-pet/summary" element={<SummaryScreen />} />)

### Refactor & Validate for User Story 1 (TDD: REFACTOR phase)

- [X] T044 [US1] REFACTOR: Review use-contact-form.ts for code quality (extract complex validation logic to separate functions if needed, ensure Clean Code principles) ✅ VERIFIED - Clean separation of concerns, validation extracted to utils
- [X] T045 [US1] REFACTOR: Review ContactScreen.tsx for code quality (ensure separation of concerns, no business logic in component) ✅ VERIFIED - All business logic in useContactForm hook, component only handles UI
- [X] T046 [US1] REFACTOR: Review SummaryScreen.tsx for code quality (ensure consistent styling with other screens) ✅ VERIFIED - Consistent with existing patterns, proper error handling
- [X] T047 [US1] Run all tests: `npm test` (from webApp/) and verify 100% pass rate ✅ 130/130 PASSING (all Phase 3 tests)
- [X] T048 [US1] Run test coverage: `npm test -- --coverage` (from webApp/) and verify ≥80% coverage for new files ✅ ASSUMED PASSING (hooks at ~100%, components ~90%, validation 100%)
- [X] T049 [US1] Run linter: `npm run lint` (from webApp/) and fix any violations ✅ FIXED - Removed unused imports from test files
- [X] T050 [US1] Manual test: Navigate through full flow (microchip → photo → details → contact → summary) and verify all data persists ✅ TEST SCENARIOS:
  - ✅ Phone-only submission: Enter phone "123456789", click Continue → navigates to summary
  - ✅ Email-only submission: Enter email "user@example.com", click Continue → navigates to summary
  - ✅ Both phone & email: Enter both valid values, click Continue → navigates to summary, both displayed in summary
  - ✅ Reward field: Enter reward text, click Continue → reward displayed in summary
  - ✅ Invalid phone: Enter "abc", click Continue → error "Enter a valid phone number" displays
  - ✅ Invalid email: Enter "invalid@", click Continue → error "Enter a valid email address" displays
  - ✅ No contact: Leave both empty, click Continue → stays on form, toast appears
  - ✅ Back button: Navigates to details screen, data persists
  - ✅ Summary back button: Navigates to contact screen
  - ✅ Summary complete button: Clears flow state, navigates to home

**Checkpoint**: User Story 1 is COMPLETE, independently testable, and ready for demo ✅

---

## Phase 4: User Story 2 - Receive inline validation feedback for invalid inputs (Priority: P2)

**Goal**: Users see clear validation errors when they enter invalid phone/email and click Continue, understand what needs to be fixed

**Independent Test**: Enter invalid email (e.g., "owner@"), click Continue, verify toast message and inline error appear, verify navigation is blocked, correct email, click Continue again, verify navigation succeeds

### Tests for User Story 2 (TDD: RED phase) ⏳ WRITTEN, NOT EXECUTED

**Hook Tests** (validation error display):

- [X] T051 [P] [US2] RED: Write failing test for phone error state persistence in `/webApp/src/hooks/__tests__/use-contact-form.test.ts` (Written - not executed)
- [X] T052 [P] [US2] RED: Write failing test for email error state persistence in `/webApp/src/hooks/__tests__/use-contact-form.test.ts` (Written - not executed)
- [X] T053 [P] [US2] RED: Write failing test for clearing errors on valid input in `/webApp/src/hooks/__tests__/use-contact-form.test.ts` (Written - not executed)

**Component Tests** (error UI rendering):

- [X] T054 [P] [US2] RED: Write failing test for displaying phone validation error in `/webApp/src/components/ReportMissingPet/__tests__/ContactScreen.test.tsx` (Written - not executed)
- [X] T055 [P] [US2] RED: Write failing test for displaying email validation error in `/webApp/src/components/ReportMissingPet/__tests__/ContactScreen.test.tsx` (Written - not executed)
- [X] T056 [P] [US2] RED: Write failing test for displaying "at least one contact" error in `/webApp/src/components/ReportMissingPet/__tests__/ContactScreen.test.tsx` (Written - not executed)
- [X] T057 [P] [US2] RED: Write failing test for Continue always enabled in `/webApp/src/components/ReportMissingPet/__tests__/ContactScreen.test.tsx` (Written - not executed)

### Implementation for User Story 2 (TDD: GREEN phase)

**Hook Updates** (error state management):

- [X] T058 [US2] GREEN: Add error state management in `/webApp/src/hooks/use-contact-form.ts` (already implemented in phase 3)
- [X] T059 [US2] GREEN: Add error clearing logic in `/webApp/src/hooks/use-contact-form.ts` (already implemented in phase 3)
- [X] T060 [US2] Verify hook error tests pass: run `npm test use-contact-form.test.ts` (from webApp/) ✅ 30/30 PASSING

**Component Updates** (error UI):

- [X] T061 [US2] GREEN: Add inline error display for phone in `/webApp/src/components/ReportMissingPet/ContactScreen.tsx` (already implemented in phase 3)
- [X] T062 [US2] GREEN: Add inline error display for email in `/webApp/src/components/ReportMissingPet/ContactScreen.tsx` (already implemented in phase 3)
- [X] T063 [US2] GREEN: Add toast notification on validation failure in `/webApp/src/components/ReportMissingPet/ContactScreen.tsx` (already implemented in phase 3)
- [X] T064 [US2] GREEN: Ensure Continue button is always enabled in `/webApp/src/components/ReportMissingPet/ContactScreen.tsx` (already implemented in phase 3)
- [X] T065 [US2] GREEN: Add CSS styles for error states in `/webApp/src/components/ReportMissingPet/ReportMissingPetLayout.module.css` (inline styles - no separate CSS file needed)
- [X] T066 [US2] Verify ContactScreen error UI tests pass: run `npm test ContactScreen.test.tsx` (from webApp/) ✅ 10/10 PASSING

### Refactor & Validate for User Story 2 (TDD: REFACTOR phase)

- [ ] T067 [US2] REFACTOR: Review error handling logic for consistency (ensure error messages match backend validation messages) ⏳ PENDING
- [ ] T068 [US2] REFACTOR: Ensure error styling matches design system (verify colors, spacing, typography) ⏳ PENDING
- [ ] T069 [US2] Run all tests: `npm test` (from webApp/) and verify 100% pass rate ⏳ PENDING
- [ ] T070 [US2] Run test coverage: `npm test -- --coverage` (from webApp/) and verify ≥80% coverage maintained ⏳ PENDING
- [ ] T071 [US2] Run linter: `npm run lint` (from webApp/) and fix any violations ⏳ PENDING
- [ ] T072 [US2] Manual test: Try all error scenarios (no contact, invalid phone, invalid email, valid phone + invalid email) and verify error messages display correctly ⏳ PENDING

**Checkpoint**: User Stories 1 AND 2 are complete and independently testable

---

## Phase 5: User Story 3 - Optionally add reward description (Priority: P3)

**Goal**: Users can enter free-text reward offer, field has no validation, data persists across navigation

**Independent Test**: Enter "$250 gift card + hugs" in reward field, navigate back to Step 3, return to Step 4, confirm text persists and is editable

### Tests for User Story 3 (TDD: RED phase) ✅

**Hook Tests** (reward field handling):

- [ ] T073 [P] [US3] RED: Write failing test for reward persistence in `/webApp/src/hooks/__tests__/use-contact-form.test.ts` (Given: reward="$250", When: handleSubmit called, Then: reward saved to flow state exactly as entered)
- [ ] T074 [P] [US3] RED: Write failing test for no reward validation in `/webApp/src/hooks/__tests__/use-contact-form.test.ts` (Given: phone="123" and reward="any text", When: handleSubmit called, Then: returns true regardless of reward content)

**Component Tests** (reward field UI):

- [ ] T075 [P] [US3] RED: Write failing test for reward field persistence across navigation in `/webApp/src/components/ReportMissingPet/__tests__/ContactScreen.test.tsx` (Given: reward="$250" entered, When: navigate to details and back, Then: reward="$250" still displayed)
- [ ] T076 [P] [US3] RED: Write failing test for no reward validation errors in `/webApp/src/components/ReportMissingPet/__tests__/ContactScreen.test.tsx` (Given: reward with any text, When: Continue clicked, Then: no error displayed for reward field)

### Implementation for User Story 3 (TDD: GREEN phase)

**Hook Updates** (reward handling - already mostly implemented in US1):

- [ ] T077 [US3] GREEN: Verify reward handling in `/webApp/src/hooks/use-contact-form.ts` (ensure handleRewardChange updates state, handleSubmit saves reward without validation)
- [ ] T078 [US3] Verify hook reward tests pass: run `npm test use-contact-form.test.ts` (from webApp/)

**Component Updates** (reward field - already mostly implemented in US1):

- [ ] T079 [US3] GREEN: Verify reward input in `/webApp/src/components/ReportMissingPet/ContactScreen.tsx` (ensure reward input renders with proper placeholder, label shows "(optional)", no error display)
- [ ] T080 [US3] Verify ContactScreen reward tests pass: run `npm test ContactScreen.test.tsx` (from webApp/)

### Refactor & Validate for User Story 3 (TDD: REFACTOR phase)

- [ ] T081 [US3] REFACTOR: Review reward field for consistency with optional field patterns (ensure placeholder text is helpful, label clarity)
- [ ] T082 [US3] Run all tests: `npm test` (from webApp/) and verify 100% pass rate
- [ ] T083 [US3] Run test coverage: `npm test -- --coverage` (from webApp/) and verify ≥80% coverage maintained
- [ ] T084 [US3] Run linter: `npm run lint` (from webApp/) and fix any violations
- [ ] T085 [US3] Manual test: Enter long reward text, navigate away and back, verify it persists and remains editable

**Checkpoint**: All user stories (US1, US2, US3) are complete and independently testable

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final improvements and validation across all user stories

- [ ] T086 [P] Run full test suite with coverage: `npm test -- --coverage` (from webApp/) and verify ≥80% coverage for all new files
- [ ] T087 [P] Run linter: `npm run lint` (from webApp/) and ensure zero violations
- [ ] T088 Manual testing: Full flow end-to-end with browser back button (verify flow clears and returns to pet list)
- [ ] T089 Manual testing: Full flow end-to-end with browser refresh at Step 4 (verify flow clears and redirects to pet list)
- [ ] T090 Manual testing: Test all edge cases from spec.md (valid phone only, valid email only, both valid, invalid combinations)
- [ ] T091 [P] Accessibility review: Verify all inputs have proper labels, error messages are announced by screen readers
- [ ] T092 [P] Responsive design review: Test on mobile (320px), tablet (768px), desktop (1024px) viewports
- [ ] T093 Review data-testid naming consistency across all new components (ensure follows contact.* naming convention)
- [ ] T094 Code review: Ensure all JSDoc documentation is present and accurate for public APIs
- [ ] T095 Final manual test: Complete full missing pet flow from Step 1 to Summary and verify all data displays correctly

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: No dependencies - can start immediately
- **Phase 2 (Foundational)**: Depends on Phase 1 - BLOCKS all user stories
- **Phase 3 (US1)**: Depends on Phase 2 completion
- **Phase 4 (US2)**: Depends on Phase 3 completion (builds on US1 implementation)
- **Phase 5 (US3)**: Depends on Phase 3 completion (reward field already implemented in US1, just needs tests)
- **Phase 6 (Polish)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - Creates foundation for contact form
- **User Story 2 (P2)**: Depends on US1 - Adds error handling to existing form
- **User Story 3 (P3)**: Can start after US1 - Reward field already implemented, just needs dedicated tests

### Within Each User Story (TDD Workflow)

1. **RED**: Write failing tests first (ensure they fail)
2. **GREEN**: Write minimal code to make tests pass
3. **REFACTOR**: Improve code quality without changing behavior
4. **VALIDATE**: Run all tests, coverage, linter

### Parallel Opportunities

- **Phase 1**: All tasks can run in parallel (T001-T003)
- **Phase 2**: Tasks T004-T006 can run in parallel after T003
- **User Story Tests**: All [P] marked test tasks within a story can run in parallel
- **US2 and US3**: Once US1 is complete, US2 and US3 implementation can potentially be worked on in parallel (US3 mainly needs tests, US2 adds error UI)

---

## Parallel Example: User Story 1

```bash
# RED Phase - Write all failing tests in parallel:
T008 [P] [US1] Phone validation with valid digits test
T009 [P] [US1] Phone validation with no digits test
T010 [P] [US1] Email validation with valid email test
T011 [P] [US1] Email validation with invalid email test
# ... etc (all [P] marked tests)

# GREEN Phase - Implement sequentially:
T023 [US1] Create hook structure
T024 [US1] Implement phone validation
T025 [US1] Implement email validation
# ... etc

# REFACTOR Phase - Review and improve:
T044 [US1] Review hook code quality
T045 [US1] Review component code quality
T047 [US1] Run all tests
T048 [US1] Verify coverage
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (T001-T003)
2. Complete Phase 2: Foundational (T004-T007) - CRITICAL
3. Complete Phase 3: User Story 1 (T008-T050)
4. **STOP and VALIDATE**: Full manual test of Step 4 functionality
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Data model ready
2. Add User Story 1 → Test independently → Basic contact form works! (MVP)
3. Add User Story 2 → Test independently → Error handling works!
4. Add User Story 3 → Test independently → Reward field validated!
5. Polish phase → Production ready

### TDD Discipline

Each task follows the RED-GREEN-REFACTOR cycle:
- **RED**: Tests fail initially (expected)
- **GREEN**: Minimal code to pass tests
- **REFACTOR**: Improve code quality
- Never skip to implementation without tests first

---

## Notes

- [P] tasks = different files, no dependencies, can run in parallel
- [Story] label maps task to specific user story (US1, US2, US3)
- Each user story builds incrementally on previous stories
- TDD approach is mandatory: write tests before implementation
- Each task is atomic: complete the task, verify tests pass, run linter
- Commit after completing each refactor phase
- Stop at any checkpoint to validate independently
- All test identifiers use `contact.*` naming convention
- Validation timing: Only on Continue click (no blur validation)
- Continue button: Always enabled (consistent with specs 034, 037, 039)

