# Tasks: Web Microchip Number Screen

**Feature**: 034-web-chip-number-screen  
**Input**: Design documents from `/specs/034-web-chip-number-screen/`  
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/  
**TDD Approach**: Each task follows Red-Green-Refactor cycle (write failing test → minimal implementation → run tests → linting)

**Tests**: Test requirements for this project:

**MANDATORY - Web Unit Tests**:
- Location: `/webApp/src/__tests__/`
- Framework: Vitest + React Testing Library
- Coverage: 80% line + branch coverage
- Scope: Pure utility functions, custom hooks, React components
- Run: `npm test -- --coverage` (from webApp/)
- Convention: MUST follow Given-When-Then structure with descriptive test names

**MANDATORY - End-to-End Tests**:
- Location: `/e2e-tests/src/test/` (Java + Cucumber)
- Framework: Selenium WebDriver + Cucumber (Gherkin)
- All user stories MUST have E2E test coverage
- Use Page Object Model pattern
- Convention: MUST structure scenarios with Given-When-Then phases
- Run: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web and @reportMissingPet"`

**Organization**: Tasks are grouped by user story to enable independent implementation and testing.

---

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3, US4)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 Verify React Router v6 is installed in `/webApp/package.json` (required dependency)
- [x] T002 Verify Vitest and React Testing Library are configured in `/webApp/vite.config.ts`
- [x] T003 Verify E2E test infrastructure exists in `/e2e-tests/` (Selenium + Cucumber)
- [x] T004 Create feature directory structure in `/webApp/src/components/ReportMissingPet/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T005 [P] RED: Write failing unit test for `formatMicrochipNumber` utility in `/webApp/src/utils/__tests__/microchip-formatter.test.ts`
- [x] T006 [P] GREEN: Implement `formatMicrochipNumber` function in `/webApp/src/utils/microchip-formatter.ts` (pure utility for 00000-00000-00000 formatting)
- [x] T007 [P] REFACTOR: Add JSDoc documentation to `formatMicrochipNumber` and edge case tests
- [x] T008 [P] LINT: Run `npm run lint` in webApp/ and fix any ESLint violations
- [x] T009 [P] RED: Write failing unit test for `stripNonDigits` utility in `/webApp/src/utils/__tests__/microchip-formatter.test.ts`
- [x] T010 [P] GREEN: Implement `stripNonDigits` function in `/webApp/src/utils/microchip-formatter.ts` (sanitizes input to digits only)
- [x] T011 [P] LINT: Run `npm run lint` in webApp/ and verify no violations
- [x] T012 [P] Create TypeScript flow state interfaces in `/webApp/src/models/ReportMissingPetFlow.ts` (copy from contracts/FlowState.ts)
- [x] T013 [P] Create route configuration constants in `/webApp/src/routes/report-missing-pet-routes.tsx` (copy from contracts/routes.ts)
- [x] T014 [P] RED: Write failing test for `ReportMissingPetFlowContext` in `/webApp/src/contexts/__tests__/ReportMissingPetFlowContext.test.tsx`
- [x] T015 GREEN: Implement `ReportMissingPetFlowContext` provider in `/webApp/src/contexts/ReportMissingPetFlowContext.tsx` (React Context with state management)
- [x] T016 REFACTOR: Add JSDoc documentation to context provider and consumer hook
- [x] T017 TEST: Run `npm test` and verify context tests pass
- [x] T018 LINT: Run `npm run lint` in webApp/ and fix violations
- [x] T019 [P] Create reusable Header component stub in `/webApp/src/components/ReportMissingPet/Header.tsx` (back button, title, progress indicator)
- [x] T020 [P] Create base Page Object Model in `/e2e-tests/src/test/java/.../pages/ReportMissingPetStep1Page.java` (Selenium locators)
- [x] T021 [P] Create base step definitions in `/e2e-tests/src/test/java/.../steps-web/ReportMissingPetStep1Steps.java` (Cucumber steps)

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Report Missing Pet with Known Microchip (Priority: P1) 🎯 MVP

**Goal**: Allow users to enter a microchip number with automatic formatting (00000-00000-00000), save to flow state, and proceed to next step

**Independent Test**: Navigate to /report-missing/microchip, enter 15 digits, verify formatting displays correctly, click Continue, verify navigation to photo step with data saved in flow state

### Tests for User Story 1 (TDD - Write First) ✅

**Unit Tests - Custom Hook**:
- [x] T022 [P] [US1] RED: Write failing test for `useMicrochipFormatter` hook in `/webApp/src/hooks/__tests__/use-microchip-formatter.test.ts` (test typing digits, state updates)
- [x] T023 [P] [US1] RED: Add failing test cases for paste with non-numeric characters in `/webApp/src/hooks/__tests__/use-microchip-formatter.test.ts`
- [x] T024 [P] [US1] RED: Add failing test case for max length enforcement (15 digits) in `/webApp/src/hooks/__tests__/use-microchip-formatter.test.ts`

**Unit Tests - Components**:
- [x] T025 [P] [US1] RED: Write failing test for `MicrochipNumberContent` presentational component in `/webApp/src/components/ReportMissingPet/__tests__/MicrochipNumberContent.test.tsx` (test rendering, callbacks)
- [x] T026 [P] [US1] RED: Write failing test for `MicrochipNumberScreen` container in `/webApp/src/components/ReportMissingPet/__tests__/MicrochipNumberScreen.test.tsx` (test integration with hook and context)

**E2E Tests**:
- [x] T027 [P] [US1] Create Gherkin feature file in `/e2e-tests/src/test/resources/features/web/report-missing-pet-step1.feature` (Scenario 1: Enter microchip with formatting)
- [x] T028 [P] [US1] Implement Page Object methods in `ReportMissingPetStep1Page.java` (enterMicrochipNumber, clickContinue, getFormattedValue)
- [x] T029 [P] [US1] Implement step definitions in `ReportMissingPetStep1Steps.java` for Scenario 1

### Implementation for User Story 1

**Custom Hook**:
- [x] T030 [US1] GREEN: Implement `useMicrochipFormatter` hook in `/webApp/src/hooks/use-microchip-formatter.ts` (minimal code to pass tests: handleChange, handlePaste, state management)
- [x] T031 [US1] REFACTOR: Extract formatting logic to use utility functions from `/webApp/src/utils/microchip-formatter.ts`
- [x] T032 [US1] REFACTOR: Add JSDoc documentation to hook explaining formatting behavior
- [x] T033 [US1] TEST: Run `npm test -- /webApp/src/hooks/__tests__/use-microchip-formatter.test.ts` and verify all tests pass
- [x] T034 [US1] LINT: Run `npm run lint` and fix violations

**Flow State Hook**:
- [x] T035 [P] [US1] RED: Write failing test for `useReportMissingPetFlow` consumer hook in `/webApp/src/hooks/__tests__/use-report-missing-pet-flow.test.ts`
- [x] T036 [US1] GREEN: Implement `useReportMissingPetFlow` consumer hook in `/webApp/src/hooks/use-report-missing-pet-flow.ts` (exports context consumer with error handling)
- [x] T037 [US1] TEST: Run hook tests and verify they pass
- [x] T038 [US1] LINT: Run `npm run lint` and fix violations

**Presentational Component**:
- [x] T039 [US1] GREEN: Implement `MicrochipNumberContent` presentational component in `/webApp/src/components/ReportMissingPet/MicrochipNumberContent.tsx` (pure component: header, input, button - no hooks)
- [x] T040 [US1] REFACTOR: Add data-testid attributes to all interactive elements (format: `reportMissingPet.step1.{element}.{action}`)
- [x] T041 [US1] REFACTOR: Extract input field to separate component if complex
- [x] T042 [US1] TEST: Run `npm test -- /webApp/src/components/ReportMissingPet/__tests__/MicrochipNumberContent.test.tsx` and verify tests pass
- [x] T043 [US1] LINT: Run `npm run lint` and fix violations

**Container Component**:
- [x] T044 [US1] GREEN: Implement `MicrochipNumberScreen` container in `/webApp/src/components/ReportMissingPet/MicrochipNumberScreen.tsx` (stateful: uses useMicrochipFormatter, useReportMissingPetFlow, handles Continue click)
- [x] T045 [US1] REFACTOR: Implement Continue button handler (save microchipNumber to flow state, update currentStep to Photo, navigate to /report-missing/photo)
- [x] T046 [US1] REFACTOR: Add JSDoc documentation to complex logic (flow state updates, navigation)
- [x] T047 [US1] TEST: Run `npm test -- /webApp/src/components/ReportMissingPet/__tests__/MicrochipNumberScreen.test.tsx` and verify tests pass
- [x] T048 [US1] LINT: Run `npm run lint` and fix violations

**Routing Integration**:
- [x] T049 [US1] Add route for microchip screen in `/webApp/src/routes/report-missing-pet-routes.tsx` (nested route under /report-missing with context provider)
- [x] T050 [US1] Update main App.tsx to include ReportMissingPet routes
- [x] T051 [US1] Create placeholder PhotoScreen component in `/webApp/src/components/ReportMissingPet/PhotoScreen.tsx` (displays "Photo step - Coming soon" and flow state for testing)
- [x] T052 [US1] Add photo route to routes configuration for navigation testing
- [x] T053 [US1] LINT: Run `npm run lint` and fix violations

**E2E Verification**:
- [x] T054 [US1] Run E2E test for Scenario 1: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web and @reportMissingPet and @US1"`
- [x] T055 [US1] Verify E2E test passes (Given user on pet list → When initiate flow → Then see microchip screen → enter "123456789012345" → verify formatted as "12345-67890-12345" → click Continue → verify navigation to photo)

**Final Verification**:
- [x] T056 [US1] Run all unit tests with coverage: `npm test -- --coverage` and verify 80%+ coverage
- [x] T057 [US1] Manual test: Navigate to http://localhost:5173/report-missing/microchip and complete User Story 1 scenario
- [x] T058 [US1] LINT: Run `npm run lint` for entire webApp/ and fix any violations

**Checkpoint**: User Story 1 is fully functional - users can enter formatted microchip number and proceed to next step

---

## Phase 4: User Story 2 - Report Missing Pet Without Microchip (Priority: P2)

**Goal**: Allow users to skip the optional microchip field and continue with empty value

**Independent Test**: Navigate to /report-missing/microchip, leave input empty, click Continue, verify flow proceeds to photo step with empty microchipNumber in flow state

### Tests for User Story 2 (TDD - Write First) ✅

**Unit Tests**:
- [ ] T059 [P] [US2] RED: Add failing test to `MicrochipNumberScreen.test.tsx` for empty input Continue scenario (verify Continue button enabled, flow state updated with empty string)

**E2E Tests**:
- [ ] T060 [P] [US2] Add Scenario 2 to `/e2e-tests/src/test/resources/features/web/report-missing-pet-step1.feature` (User skips microchip number)
- [ ] T061 [P] [US2] Implement step definitions in `ReportMissingPetStep1Steps.java` for Scenario 2 (click Continue without entering data)

### Implementation for User Story 2

- [ ] T062 [US2] GREEN: Verify `MicrochipNumberScreen` handles empty input correctly (Continue button always enabled, saves empty string to flow state)
- [ ] T063 [US2] TEST: Run `npm test -- /webApp/src/components/ReportMissingPet/__tests__/MicrochipNumberScreen.test.tsx` and verify empty input test passes
- [ ] T064 [US2] LINT: Run `npm run lint` and fix violations
- [ ] T065 [US2] Run E2E test for Scenario 2: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web and @reportMissingPet and @US2"`
- [ ] T066 [US2] Verify E2E test passes (Given on microchip screen → When leave input empty → click Continue → Then navigate to photo with empty microchipNumber)
- [ ] T067 [US2] Manual test: Complete User Story 2 scenario (skip microchip, verify flow continues)

**Checkpoint**: User Story 2 is functional - users can skip optional microchip field

---

## Phase 5: User Story 3 - Navigate Back from Flow (Priority: P3)

**Goal**: Allow users to cancel the flow and return to pet list, clearing all flow state

**Independent Test**: Navigate to /report-missing/microchip, optionally enter data, click back arrow in header, verify navigation to /pets and flow state cleared

### Tests for User Story 3 (TDD - Write First) ✅

**Unit Tests**:
- [ ] T068 [P] [US3] RED: Write failing test for Header component back button in `/webApp/src/components/ReportMissingPet/__tests__/Header.test.tsx` (test onBack callback invocation)
- [ ] T069 [P] [US3] RED: Add failing test to `MicrochipNumberScreen.test.tsx` for back button handler (verify clearFlowState called, navigation to /pets)

**E2E Tests**:
- [ ] T070 [P] [US3] Add Scenario 3 to `/e2e-tests/src/test/resources/features/web/report-missing-pet-step1.feature` (User cancels flow via back button)
- [ ] T071 [P] [US3] Implement Page Object methods for back button in `ReportMissingPetStep1Page.java` (clickBackButton)
- [ ] T072 [P] [US3] Implement step definitions for Scenario 3 in `ReportMissingPetStep1Steps.java`

### Implementation for User Story 3

**Header Component**:
- [ ] T073 [P] [US3] GREEN: Implement Header component in `/webApp/src/components/ReportMissingPet/Header.tsx` (back button, title, progress indicator with data-testid attributes)
- [ ] T074 [US3] TEST: Run `npm test -- /webApp/src/components/ReportMissingPet/__tests__/Header.test.tsx` and verify tests pass
- [ ] T075 [US3] LINT: Run `npm run lint` and fix violations

**Back Button Handler**:
- [ ] T076 [US3] GREEN: Implement back button handler in `MicrochipNumberScreen` (call clearFlowState, navigate to /pets)
- [ ] T077 [US3] REFACTOR: Extract navigation logic to utility function if complex
- [ ] T078 [US3] TEST: Run `npm test -- /webApp/src/components/ReportMissingPet/__tests__/MicrochipNumberScreen.test.tsx` and verify back button test passes
- [ ] T079 [US3] LINT: Run `npm run lint` and fix violations

**E2E Verification**:
- [ ] T080 [US3] Run E2E test for Scenario 3: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web and @reportMissingPet and @US3"`
- [ ] T081 [US3] Verify E2E test passes (Given on microchip screen → enter data → click back → Then navigate to /pets and flow state cleared)
- [ ] T082 [US3] Manual test: Complete User Story 3 scenario (cancel flow, verify return to pet list)

**Checkpoint**: User Story 3 is functional - users can cancel flow and return to pet list

---

## Phase 6: User Story 4 - Resume Flow with Previously Entered Data (Priority: P2)

**Goal**: Allow users to navigate forward in flow and back to step 1, preserving previously entered microchip data

**Independent Test**: Navigate to /report-missing/microchip, enter data, click Continue to reach photo step, navigate back to microchip step (in-flow back, not cancel), verify data still populated

### Tests for User Story 4 (TDD - Write First) ✅

**Unit Tests**:
- [ ] T083 [P] [US4] RED: Add failing test to `MicrochipNumberScreen.test.tsx` for flow state restoration (mount with existing microchipNumber in context, verify input populated)
- [ ] T084 [P] [US4] RED: Add failing test for editing previously entered data (change value, click Continue, verify updated value saved)

**E2E Tests**:
- [ ] T085 [P] [US4] Add Scenario 4 to `/e2e-tests/src/test/resources/features/web/report-missing-pet-step1.feature` (User edits microchip after navigating back)
- [ ] T086 [P] [US4] Implement step definitions for Scenario 4 in `ReportMissingPetStep1Steps.java` (navigate to photo, back to microchip, verify data persists)

### Implementation for User Story 4

**State Restoration**:
- [ ] T087 [US4] GREEN: Update `MicrochipNumberScreen` to initialize hook with flow state microchipNumber on mount (useEffect to populate formattedValue from context)
- [ ] T088 [US4] REFACTOR: Add JSDoc documentation explaining state restoration logic
- [ ] T089 [US4] TEST: Run `npm test -- /webApp/src/components/ReportMissingPet/__tests__/MicrochipNumberScreen.test.tsx` and verify state restoration test passes
- [ ] T090 [US4] LINT: Run `npm run lint` and fix violations

**Photo Screen In-Flow Back**:
- [ ] T091 [P] [US4] Update PhotoScreen placeholder to have back button that navigates to /report-missing/microchip (does NOT clear flow state)
- [ ] T092 [US4] REFACTOR: Distinguish between cancel (header back from step 1) and in-flow back (back from step 2+)

**E2E Verification**:
- [ ] T093 [US4] Run E2E test for Scenario 4: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web and @reportMissingPet and @US4"`
- [ ] T094 [US4] Verify E2E test passes (Given enter "12345-67890-12345" → Continue to photo → back to microchip → Then see "12345-67890-12345" populated → edit to "11111-11111-11111" → Continue → verify updated)
- [ ] T095 [US4] Manual test: Complete User Story 4 scenario (forward navigation, back, verify data persists and can be edited)

**Checkpoint**: User Story 4 is functional - flow state persists across forward/backward navigation

---

## Phase 7: Browser Back Button & Refresh Handling

**Goal**: Handle browser back button (same as in-app back arrow) and page refresh (clears flow state)

**Independent Test**: Use browser back button from microchip screen, verify return to pet list. Refresh page, verify flow state cleared.

### Tests (TDD - Write First) ✅

**Unit Tests**:
- [ ] T096 [P] RED: Write failing test for `useBrowserBackHandler` hook in `/webApp/src/hooks/__tests__/use-browser-back-handler.test.ts` (mock popstate event)
- [ ] T097 [P] RED: Add failing test to `MicrochipNumberScreen.test.tsx` for browser back integration (verify hook called with clearFlowState callback)

**E2E Tests**:
- [ ] T098 [P] Add Scenario 5 to `/e2e-tests/src/test/resources/features/web/report-missing-pet-step1.feature` (Browser back button cancels flow)
- [ ] T099 [P] Add Scenario 6 to feature file (Page refresh clears flow state)
- [ ] T100 [P] Implement step definitions for browser navigation in `ReportMissingPetStep1Steps.java`

### Implementation

**Browser Back Hook**:
- [ ] T101 GREEN: Implement `useBrowserBackHandler` hook in `/webApp/src/hooks/use-browser-back-handler.ts` (listen to popstate event, call onBack callback)
- [ ] T102 REFACTOR: Add JSDoc documentation explaining browser history API usage
- [ ] T103 TEST: Run `npm test -- /webApp/src/hooks/__tests__/use-browser-back-handler.test.ts` and verify tests pass
- [ ] T104 LINT: Run `npm run lint` and fix violations

**Integration**:
- [ ] T105 GREEN: Integrate `useBrowserBackHandler` in `MicrochipNumberScreen` (pass clearFlowState + navigate to /pets)
- [ ] T106 TEST: Run `npm test -- /webApp/src/components/ReportMissingPet/__tests__/MicrochipNumberScreen.test.tsx` and verify browser back test passes
- [ ] T107 LINT: Run `npm run lint` and fix violations

**Route Guards**:
- [ ] T108 [P] RED: Write failing tests for route guard utilities in `/webApp/src/utils/__tests__/route-guards.test.ts` (test canAccessPhoto, canAccessDetails, canAccessContact)
- [ ] T109 [P] GREEN: Implement route guard utilities in `/webApp/src/utils/route-guards.ts` (pure functions based on contracts/routes.ts)
- [ ] T110 [P] TEST: Run route guard tests and verify they pass
- [ ] T111 [P] LINT: Run `npm run lint` and fix violations
- [ ] T112 Create protected route wrapper component in `/webApp/src/components/ReportMissingPet/ProtectedRoute.tsx` (checks currentStep, redirects to microchip if invalid)
- [ ] T113 Apply ProtectedRoute wrapper to photo/details/contact routes in route configuration

**E2E Verification**:
- [ ] T114 Run E2E tests for Scenarios 5-6: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web and @reportMissingPet and (@US5 or @US6)"`
- [ ] T115 Verify E2E tests pass (browser back cancels flow, refresh clears state and redirects)
- [ ] T116 Manual test: Use browser back button, verify behavior. Refresh page, verify state cleared.

**Checkpoint**: Browser back button and refresh handling complete

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Final improvements, documentation, and comprehensive testing

**Final Testing**:
- [ ] T117 Run full test suite: `npm test -- --coverage` from webApp/ and verify 80%+ line + branch coverage
- [ ] T118 Review coverage report at `webApp/coverage/index.html` and add tests for any gaps below 80%
- [ ] T119 Run all E2E tests: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web and @reportMissingPet"`
- [ ] T120 Verify all 6 E2E scenarios pass without flakiness (run 3 times)

**Code Quality**:
- [ ] T121 Run ESLint on entire webApp: `npm run lint` and fix all violations
- [ ] T122 Run TypeScript type check: `npx tsc --noEmit` in webApp/ and fix all type errors
- [ ] T123 [P] Review and improve JSDoc documentation for public APIs (hooks, utilities, complex components)
- [ ] T124 [P] Review data-testid attributes - ensure all interactive elements have consistent naming (reportMissingPet.step1.*)
- [ ] T125 [P] Code review: Check for code duplication and extract reusable utilities

**Responsive Design**:
- [ ] T126 [P] Test layout on mobile viewport (320px width) in browser DevTools
- [ ] T127 [P] Test layout on tablet viewport (768px width) in browser DevTools
- [ ] T128 [P] Test layout on desktop viewport (1024px+ width) in browser DevTools
- [ ] T129 Fix any layout issues (text clipping, horizontal scroll, button placement)

**Performance**:
- [ ] T130 Test input formatting performance: rapidly type 15 digits and verify no lag (< 100ms response)
- [ ] T131 Test paste performance: paste 100+ character string and verify instant sanitization

**Edge Cases**:
- [ ] T132 Test paste with only non-numeric characters (e.g., "ABCXYZ") - verify empty result
- [ ] T133 Test paste with 20 digits - verify only first 15 accepted
- [ ] T134 Test rapid typing with backspace/delete - verify formatting stays correct
- [ ] T135 Test direct URL access to /report-missing/photo without flow state - verify redirect to microchip

**Documentation**:
- [ ] T136 [P] Verify quickstart.md instructions are accurate (run through all manual test flows)
- [ ] T137 [P] Update quickstart.md if any steps are incorrect or missing
- [ ] T138 [P] Add troubleshooting section to quickstart.md for common issues

**Final Validation**:
- [ ] T139 Run complete quickstart.md validation (all 7 manual test flows from quickstart)
- [ ] T140 Verify no console errors or warnings in browser DevTools during user flows
- [ ] T141 Final commit: Run `npm run lint`, `npm test -- --coverage`, and E2E tests before commit

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-6)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3 → P2)
- **Browser Handling (Phase 7)**: Depends on User Stories 1-3 completion (needs base flow to test)
- **Polish (Phase 8)**: Depends on all previous phases being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Independent (tests same component as US1)
- **User Story 3 (P3)**: Requires US1 complete (needs MicrochipNumberScreen to add back button)
- **User Story 4 (P2)**: Requires US1 complete (needs full flow navigation to test state restoration)

### Within Each User Story (TDD Atomic Tasks)

Each implementation task follows this atomic pattern:
1. **RED**: Write failing tests
2. **GREEN**: Implement minimal code to pass tests
3. **REFACTOR**: Improve code quality, add documentation
4. **TEST**: Run tests and verify they pass
5. **LINT**: Run linting and fix violations

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Within each user story:
  - All RED tasks (writing tests) marked [P] can run in parallel
  - Implementation tasks run sequentially (TDD cycle)
- User Story 2 can start in parallel with User Story 1 (different test scenarios for same component)
- Once US1 is complete, US3 and US4 can proceed in parallel

---

## Parallel Example: Foundational Phase

```bash
# Launch all test writing tasks together (Phase 2):
Task: "T005 - Write failing test for formatMicrochipNumber"
Task: "T009 - Write failing test for stripNonDigits"
Task: "T014 - Write failing test for ReportMissingPetFlowContext"
# Then implement sequentially in TDD cycles
```

---

## Parallel Example: User Story 1

```bash
# Launch all test writing tasks together (US1):
Task: "T022 - Write failing test for useMicrochipFormatter hook"
Task: "T023 - Add test for paste with non-numeric"
Task: "T024 - Add test for max length enforcement"
Task: "T025 - Write failing test for MicrochipNumberContent"
Task: "T026 - Write failing test for MicrochipNumberScreen"
Task: "T027-T029 - Create E2E tests for US1"

# Then implement in sequence:
1. Hook implementation (T030-T034)
2. Component implementation (T039-T048)
3. Routing integration (T049-T053)
4. E2E verification (T054-T058)
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo (optional field support)
4. Add User Story 3 → Test independently → Deploy/Demo (cancel flow)
5. Add User Story 4 → Test independently → Deploy/Demo (edit previous data)
6. Add Browser Handling (Phase 7) → Complete flow robustness
7. Polish (Phase 8) → Production-ready quality

### TDD Workflow (MANDATORY for each implementation task)

For EVERY implementation task (GREEN steps):

1. **Verify tests FAIL**: Run tests before implementation - they MUST fail (RED)
2. **Minimal implementation**: Write just enough code to make tests pass (GREEN)
3. **Refactor**: Improve code quality, add documentation, extract utilities
4. **Verify tests PASS**: Run tests after implementation - they MUST pass
5. **Verify linting PASSES**: Run ESLint - NO violations allowed
6. **Commit**: Only commit when tests pass and linting is clean

### Never Skip Testing Steps

- ❌ DON'T: Implement code before tests
- ❌ DON'T: Commit without running tests
- ❌ DON'T: Skip linting checks
- ✅ DO: Write failing tests first
- ✅ DO: Implement minimal code to pass
- ✅ DO: Refactor and document
- ✅ DO: Verify tests pass before moving on
- ✅ DO: Run linting and fix violations

---

## Notes

- [P] tasks = different files, no dependencies, can run in parallel
- [Story] label maps task to specific user story (US1, US2, US3, US4)
- Each user story should be independently completable and testable
- TDD atomic pattern MUST be followed for every implementation task
- Tests MUST fail before implementation (RED phase)
- Tests MUST pass after implementation (GREEN phase)
- Linting MUST pass with zero violations before task completion
- Commit after each complete task or logical group (when tests + linting pass)
- Stop at any checkpoint to validate story independently
- Avoid: skipping tests, implementing before writing tests, committing with failing tests or lint violations

