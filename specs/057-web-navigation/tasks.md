# Tasks: Web App Navigation Bar

**Input**: Design documents from `/specs/057-web-navigation/`
**Prerequisites**: plan.md (complete), spec.md (complete), research.md (complete), data-model.md (complete), contracts/ (complete)

**Tests**: Test requirements for this project:

**MANDATORY - Web Platform Unit Tests**:
- Location: `/webApp/src/components/__tests__/` (Vitest + React Testing Library), 80% coverage
- Scope: NavigationBar component rendering, active state logic, responsive behavior
- Run: `npm test --coverage` (from webApp/)
- TDD Workflow: Red-Green-Refactor cycle (write failing test, minimal implementation, refactor)
- Convention: MUST follow Given-When-Then structure with descriptive test names

**MANDATORY - End-to-End Tests**:
- Location: `/e2e-tests/java/src/test/resources/features/web/057-navigation.feature` (Java 21 + Maven + Selenium + Cucumber)
- All user stories MUST have E2E test coverage
- Use Page Object Model pattern
- Convention: MUST structure scenarios with Given-When-Then phases
- Run: `mvn test -Dtest=WebTestRunner` (from e2e-tests/java/)

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Project Infrastructure)

**Purpose**: Verify existing project setup and dependencies - no new dependencies required

- [x] T001 Verify webApp dependencies in `/Users/pawelkedra/code/AI-First/webApp/package.json` (confirm react-icons v5.5.0 installed)
- [x] T002 [P] Verify ESLint configuration in `/Users/pawelkedra/code/AI-First/webApp/eslint.config.mjs`
- [x] T003 [P] Verify Vitest configuration in `/Users/pawelkedra/code/AI-First/webApp/vite.config.ts` (coverage thresholds: 80%)
- [x] T004 Create components directory structure `/Users/pawelkedra/code/AI-First/webApp/src/components/__tests__/` (if not exists)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core route structure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T005 Create Home route placeholder in `/Users/pawelkedra/code/AI-First/webApp/src/routes/Home.tsx` (simple "Welcome to PetSpot" message)
- [x] T006 [P] Create FoundPets route placeholder in `/Users/pawelkedra/code/AI-First/webApp/src/routes/FoundPets.tsx` (simple "Coming soon" message)
- [x] T007 [P] Create Contact route placeholder in `/Users/pawelkedra/code/AI-First/webApp/src/routes/Contact.tsx` (simple "Coming soon" message)
- [x] T008 [P] Create Account route placeholder in `/Users/pawelkedra/code/AI-First/webApp/src/routes/Account.tsx` (simple "Coming soon" message)
- [x] T009 Move existing lost pets list logic to `/Users/pawelkedra/code/AI-First/webApp/src/routes/LostPets.tsx` (from current `/` route)
- [x] T010 Create E2E Page Object base in `/Users/pawelkedra/code/AI-First/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/pages/web/NavigationPage.java`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Navigate Between Main Sections (Priority: P1) 🎯 MVP

**Goal**: Users can use the horizontal navigation bar at the top of the screen to quickly access different sections of the PetSpot application (Home, Lost Pet, Found Pet, Contact Us, Account).

**Independent Test**: Click each navigation item and verify navigation to the appropriate section. Verify active state highlights current section.

### Tests for User Story 1 (MANDATORY - TDD: Red-Green-Refactor) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**Web Component Tests** (TDD: RED phase):
- [x] T011 [P] [US1] RED: Write failing test for NavigationBar rendering all items in `/Users/pawelkedra/code/AI-First/webApp/src/components/__tests__/NavigationBar.test.tsx` (Given-When-Then structure)
- [x] T012 [P] [US1] RED: Write failing test for NavigationBar logo rendering in `/Users/pawelkedra/code/AI-First/webApp/src/components/__tests__/NavigationBar.test.tsx` (Given-When-Then structure)
- [x] T013 [P] [US1] RED: Write failing test for navigation item click handler in `/Users/pawelkedra/code/AI-First/webApp/src/components/__tests__/NavigationBar.test.tsx` (Given-When-Then structure)
- [x] T014 [P] [US1] RED: Write failing test for active state detection (Home active on `/`) in `/Users/pawelkedra/code/AI-First/webApp/src/components/__tests__/NavigationBar.test.tsx` (Given-When-Then structure)
- [x] T015 [P] [US1] RED: Write failing test for active state detection (LostPet active on `/lost-pets`) in `/Users/pawelkedra/code/AI-First/webApp/src/components/__tests__/NavigationBar.test.tsx` (Given-When-Then structure)
- [x] T016 [US1] Run `npm test` from webApp/ directory and verify all tests FAIL (RED phase validation)

**End-to-End Tests**:
- [x] T017 [P] [US1] Create Gherkin feature file in `/Users/pawelkedra/code/AI-First/e2e-tests/java/src/test/resources/features/web/057-navigation.feature` (Given-When-Then scenarios for all navigation items)
- [x] T018 [P] [US1] Implement NavigationPage Page Object methods in `/Users/pawelkedra/code/AI-First/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/pages/web/NavigationPage.java` (clickHome, clickLostPet, etc.)
- [x] T019 [P] [US1] Implement step definitions in `/Users/pawelkedra/code/AI-First/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/web/NavigationSteps.java`

### Implementation for User Story 1 (TDD: GREEN phase)

**Core Navigation Component**:
- [x] T020 [P] [US1] GREEN: Create NavigationBar component in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.tsx` (minimal code to pass T011-T012 tests)
- [x] T021 [US1] GREEN: Define NAVIGATION_ITEMS static config in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.tsx` (5 items with react-icons components)
- [x] T022 [US1] GREEN: Implement NavLink rendering with active state detection in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.tsx` (minimal code to pass T013-T015 tests)
- [x] T023 [US1] GREEN: Add data-testid attributes to all navigation items in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.tsx` (format: `navigation.{id}.link`)
- [x] T024 [US1] Run `npm test` from webApp/ directory and verify all T011-T015 tests PASS (GREEN phase validation)

**React Router Integration**:
- [x] T025 [US1] Update App.tsx in `/Users/pawelkedra/code/AI-First/webApp/src/App.tsx` (add NavigationBar component above Routes)
- [x] T026 [US1] Configure routes in `/Users/pawelkedra/code/AI-First/webApp/src/App.tsx` (5 Route elements: /, /lost-pets, /found-pets, /contact, /account)
- [x] T027 [US1] Verify navigation works in browser (manual test: click all 5 navigation items, check URL changes)

**Refactor Phase**:
- [x] T028 [US1] REFACTOR: Add TypeScript interfaces from contracts in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.tsx` (import from contracts/NavigationBar.types.ts)
- [x] T029 [US1] REFACTOR: Extract NavigationItem as separate subcomponent in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationItem.tsx` (optional - only if complexity justifies it)
- [x] T030 [US1] REFACTOR: Apply Clean Code principles to NavigationBar (descriptive names, max 3 nesting levels, DRY)
- [x] T031 [US1] Run `npm run lint` from webApp/ directory and fix any ESLint violations

**Coverage & Documentation**:
- [x] T032 [US1] Run `npm test --coverage` from webApp/ directory and verify ≥80% coverage for NavigationBar component
- [x] T033 [P] [US1] Add JSDoc documentation ONLY to complex functions in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.tsx` (skip self-explanatory code)
- [ ] T034 [US1] Run E2E tests with `mvn test -Dtest=WebTestRunner` from e2e-tests/java/ directory and verify all US1 scenarios pass

**Checkpoint**: At this point, User Story 1 should be fully functional - all 5 navigation items work, active state highlights correctly, URLs navigate properly

---

## Phase 4: User Story 2 - Visual Design Consistency (Priority: P2)

**Goal**: The navigation bar follows the visual design specified in the Figma wireframes, providing a consistent and professional user experience.

**Independent Test**: Compare rendered navigation bar with Figma design for layout, colors, typography, spacing, icons, and active states.

### Tests for User Story 2 (MANDATORY - TDD: Red-Green-Refactor) ✅

**Web Component Tests** (TDD: RED phase):
- [ ] T035 [P] [US2] RED: Write failing test for navigation bar layout (horizontal, logo left, items right) in `/Users/pawelkedra/code/AI-First/webApp/src/components/__tests__/NavigationBar.test.tsx` (Given-When-Then structure)
- [ ] T036 [P] [US2] RED: Write failing test for icon + label rendering for each item in `/Users/pawelkedra/code/AI-First/webApp/src/components/__tests__/NavigationBar.test.tsx` (Given-When-Then structure)
- [ ] T037 [P] [US2] RED: Write failing test for active item styling (blue background #EFF6FF, blue text #155DFC) in `/Users/pawelkedra/code/AI-First/webApp/src/components/__tests__/NavigationBar.test.tsx` (Given-When-Then structure)
- [ ] T038 [P] [US2] RED: Write failing test for inactive item styling (transparent bg, gray text #4A5565) in `/Users/pawelkedra/code/AI-First/webApp/src/components/__tests__/NavigationBar.test.tsx` (Given-When-Then structure)
- [ ] T039 [P] [US2] RED: Write failing test for hover state styling in `/Users/pawelkedra/code/AI-First/webApp/src/components/__tests__/NavigationBar.test.tsx` (Given-When-Then structure)
- [ ] T040 [US2] Run `npm test` from webApp/ directory and verify T035-T039 tests FAIL (RED phase validation)

**End-to-End Tests**:
- [ ] T041 [P] [US2] Add visual design scenarios to Gherkin feature file in `/Users/pawelkedra/code/AI-First/e2e-tests/java/src/test/resources/features/web/057-navigation.feature` (Given-When-Then for Figma design verification)
- [ ] T042 [P] [US2] Implement visual assertion methods in NavigationPage in `/Users/pawelkedra/code/AI-First/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/pages/web/NavigationPage.java` (verifyActiveItemStyle, verifyInactiveItemStyle)
- [ ] T043 [P] [US2] Implement visual step definitions in `/Users/pawelkedra/code/AI-First/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/steps/web/NavigationSteps.java`

### Implementation for User Story 2 (TDD: GREEN phase)

**CSS Styling**:
- [ ] T044 [P] [US2] GREEN: Create CSS Module file in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.module.css` (minimal styles to pass T035-T036 tests)
- [ ] T045 [US2] GREEN: Implement navigation bar layout styles in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.module.css` (flexbox horizontal, logo left, items right - pass T035)
- [ ] T046 [US2] GREEN: Implement logo styles in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.module.css` (height 2.5rem, auto width)
- [ ] T047 [US2] GREEN: Implement navigation items container styles in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.module.css` (flexbox, gap 0.5rem)
- [ ] T048 [US2] GREEN: Implement active item styles in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.module.css` (blue background #EFF6FF, blue text #155DFC - pass T037)
- [ ] T049 [US2] GREEN: Implement inactive item styles in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.module.css` (transparent bg, gray text #4A5565 - pass T038)
- [ ] T050 [US2] GREEN: Implement hover state styles in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.module.css` (background color change - pass T039)
- [ ] T051 [US2] GREEN: Implement icon and label styles in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.module.css` (icon size 20px, label white-space nowrap)
- [ ] T052 [US2] Import CSS Module in NavigationBar component in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.tsx` (apply className to elements)
- [ ] T053 [US2] Run `npm test` from webApp/ directory and verify T035-T039 tests PASS (GREEN phase validation)

**Refactor Phase**:
- [ ] T054 [US2] REFACTOR: Optimize CSS for maintainability in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.module.css` (DRY, consistent naming, CSS variables for colors)
- [ ] T055 [US2] REFACTOR: Add CSS comments for Figma design references in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.module.css`
- [ ] T056 [US2] Verify visual design matches Figma in browser (manual test: compare colors, spacing, typography, icons)

**Coverage & Documentation**:
- [ ] T057 [US2] Run `npm test --coverage` from webApp/ directory and verify ≥80% coverage maintained
- [ ] T058 [US2] Run E2E tests with `mvn test -Dtest=WebTestRunner` from e2e-tests/java/ directory and verify all US2 scenarios pass

**Checkpoint**: At this point, User Stories 1 AND 2 should both work - navigation is functional AND visually matches Figma design

---

## Phase 5: User Story 3 - Maintain Navigation State Across Page Transitions (Priority: P3)

**Goal**: When users navigate between sections, the navigation bar persists and correctly indicates the current section, providing consistent orientation.

**Independent Test**: Navigate between different sections and verify the navigation bar remains visible and the active state updates correctly (including browser back/forward, direct URL access).

### Tests for User Story 3 (MANDATORY - TDD: Red-Green-Refactor) ✅

**Web Component Tests** (TDD: RED phase):
- [ ] T059 [P] [US3] RED: Write failing test for active state update on route change (Home → LostPet) in `/Users/pawelkedra/code/AI-First/webApp/src/components/__tests__/NavigationBar.test.tsx` (Given-When-Then structure)
- [ ] T060 [P] [US3] RED: Write failing test for active state persistence with browser back button in `/Users/pawelkedra/code/AI-First/webApp/src/components/__tests__/NavigationBar.test.tsx` (Given-When-Then structure)
- [ ] T061 [P] [US3] RED: Write failing test for active state on direct URL access (/lost-pets) in `/Users/pawelkedra/code/AI-First/webApp/src/components/__tests__/NavigationBar.test.tsx` (Given-When-Then structure)
- [ ] T062 [P] [US3] RED: Write failing test for navigation bar visibility across all routes in `/Users/pawelkedra/code/AI-First/webApp/src/components/__tests__/NavigationBar.test.tsx` (Given-When-Then structure)
- [ ] T063 [US3] Run `npm test` from webApp/ directory and verify T059-T062 tests FAIL (RED phase validation)

**End-to-End Tests**:
- [ ] T064 [P] [US3] Add navigation state persistence scenarios to Gherkin feature file in `/Users/pawelkedra/code/AI-First/e2e-tests/java/src/test/resources/features/web/057-navigation.feature` (Given-When-Then for browser back/forward, direct URL)
- [ ] T065 [P] [US3] Implement navigation state assertion methods in NavigationPage in `/Users/pawelkedra/code/AI-First/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/pages/web/NavigationPage.java` (verifyActiveItemAfterNavigation, verifyActiveItemOnDirectAccess)
- [ ] T066 [P] [US3] Implement state persistence step definitions in `/Users/pawelkedra/code/AI-First/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/steps/web/NavigationSteps.java`

### Implementation for User Story 3 (TDD: GREEN phase)

**State Management Verification**:
- [ ] T067 [US3] GREEN: Verify React Router NavLink correctly detects active state on route change in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.tsx` (minimal code to pass T059)
- [ ] T068 [US3] GREEN: Verify NavLink active state updates on browser back/forward in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.tsx` (minimal code to pass T060)
- [ ] T069 [US3] GREEN: Verify NavLink active state correct on direct URL access in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.tsx` (minimal code to pass T061)
- [ ] T070 [US3] GREEN: Verify NavigationBar renders on all routes in `/Users/pawelkedra/code/AI-First/webApp/src/App.tsx` (minimal code to pass T062)
- [ ] T071 [US3] Run `npm test` from webApp/ directory and verify T059-T062 tests PASS (GREEN phase validation)

**Manual Verification**:
- [ ] T072 [US3] Manual test: Navigate Home → LostPet → FoundPet and verify active state updates correctly
- [ ] T073 [US3] Manual test: Use browser back button multiple times and verify active state updates correctly
- [ ] T074 [US3] Manual test: Directly access `/lost-pets` URL and verify LostPet is highlighted as active
- [ ] T075 [US3] Manual test: Bookmark `/contact` and verify Contact is highlighted as active on page load

**Refactor Phase**:
- [ ] T076 [US3] REFACTOR: Add comments explaining React Router state management in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.tsx` (if non-obvious)
- [ ] T077 [US3] REFACTOR: Ensure no unnecessary re-renders in NavigationBar component (React.memo if needed)

**Coverage & Documentation**:
- [ ] T078 [US3] Run `npm test --coverage` from webApp/ directory and verify ≥80% coverage maintained
- [ ] T079 [US3] Run E2E tests with `mvn test -Dtest=WebTestRunner` from e2e-tests/java/ directory and verify all US3 scenarios pass

**Checkpoint**: All user stories (US1, US2, US3) should now be independently functional - navigation works, looks good, and maintains state correctly

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final improvements and responsive design

**Responsive Design (Desktop-only)**:
- [ ] T080 [P] Add responsive media query to hide navigation on mobile (<768px) in `/Users/pawelkedra/code/AI-First/webApp/src/components/NavigationBar.module.css` (@media max-width: 767px, display: none)
- [ ] T081 [P] RED: Write failing test for mobile visibility (navigation hidden at 375px width) in `/Users/pawelkedra/code/AI-First/webApp/src/components/__tests__/NavigationBar.test.tsx` (Given-When-Then structure)
- [ ] T082 GREEN: Verify test T081 passes with media query implementation
- [ ] T083 Manual test: Resize browser to <768px and verify navigation hidden
- [ ] T084 Manual test: Resize browser to ≥768px and verify navigation visible

**Final Validation**:
- [ ] T085 [P] Run full test suite with `npm test --coverage` from webApp/ directory (verify 100% pass rate, ≥80% coverage)
- [ ] T086 [P] Run full E2E test suite with `mvn test -Dtest=WebTestRunner` from e2e-tests/java/ directory (verify 100% pass rate)
- [ ] T087 [P] Run ESLint with `npm run lint` from webApp/ directory (verify 0 violations)
- [ ] T088 Verify quickstart.md checklist in `/Users/pawelkedra/code/AI-First/specs/057-web-navigation/quickstart.md` (all verification items complete)
- [ ] T089 View coverage report at `/Users/pawelkedra/code/AI-First/webApp/coverage/index.html` (verify ≥80% for NavigationBar)
- [ ] T090 View E2E report at `/Users/pawelkedra/code/AI-First/e2e-tests/java/target/cucumber-reports/web/index.html` (verify all scenarios passed)

**Documentation**:
- [ ] T091 [P] Add implementation notes to `/Users/pawelkedra/code/AI-First/specs/057-web-navigation/plan.md` (mark Phase 3 complete)
- [ ] T092 [P] Update agent context with `./Users/pawelkedra/code/AI-First/.specify/scripts/bash/update-agent-context.sh cursor-agent`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup (Phase 1) completion - BLOCKS all user stories
- **User Stories (Phase 3-5)**: All depend on Foundational (Phase 2) completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Phase 6)**: Depends on all user stories (Phase 3-5) being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories ✅
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Enhances US1 but independently testable ✅
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Validates US1+US2 behavior but independently testable ✅

### Within Each User Story

**TDD Workflow (MANDATORY)**:
1. RED: Write failing tests FIRST (T011-T016 for US1)
2. Verify tests FAIL (T016 for US1)
3. GREEN: Write minimal code to pass tests (T020-T024 for US1)
4. Verify tests PASS (T024 for US1)
5. REFACTOR: Improve code quality (T028-T031 for US1)
6. Coverage validation (T032-T034 for US1)

**Within Implementation**:
- Tests MUST be written and FAIL before implementation
- Component skeleton before styling
- React Router integration before E2E tests
- Refactor before moving to next story
- Story complete and validated before next priority

### Parallel Opportunities

- **Setup tasks** (T001-T004): All marked [P] can run in parallel
- **Foundational tasks** (T005-T010): All marked [P] can run in parallel within Phase 2
- **Once Foundational completes**: All user stories (US1, US2, US3) can start in parallel (if team capacity allows)
- **Within each user story**:
  - All test writing tasks marked [P] can run in parallel
  - All CSS styling tasks marked [P] can run in parallel
  - Component tests + E2E tests can run in parallel
- **Polish tasks** (T080-T092): All marked [P] can run in parallel

---

## Parallel Example: User Story 1

```bash
# Launch all RED tests for User Story 1 together (TDD: RED phase):
Task T011: "Write failing test for NavigationBar rendering"
Task T012: "Write failing test for logo rendering"
Task T013: "Write failing test for click handler"
Task T014: "Write failing test for Home active state"
Task T015: "Write failing test for LostPet active state"
# Run: npm test (all should FAIL)

# After T016 validation, launch GREEN implementation in parallel:
Task T020: "Create NavigationBar component"
Task T021: "Define NAVIGATION_ITEMS config"
Task T022: "Implement NavLink rendering"
Task T023: "Add data-testid attributes"
# Run: npm test (all should PASS after T024)

# Launch E2E tests in parallel with component tests:
Task T017: "Create Gherkin feature file"
Task T018: "Implement Page Object methods"
Task T019: "Implement step definitions"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (T001-T004)
2. Complete Phase 2: Foundational (T005-T010) - CRITICAL, blocks all stories
3. Complete Phase 3: User Story 1 (T011-T034)
4. **STOP and VALIDATE**: Test User Story 1 independently (all 5 navigation items work)
5. Deploy/demo if ready ✅ **MVP COMPLETE**

### Incremental Delivery

1. Complete Setup (Phase 1) + Foundational (Phase 2) → Foundation ready
2. Add User Story 1 (Phase 3) → Test independently → Deploy/Demo (MVP! ✅)
3. Add User Story 2 (Phase 4) → Test independently → Deploy/Demo (Visual polish added ✨)
4. Add User Story 3 (Phase 5) → Test independently → Deploy/Demo (State management validated ✅)
5. Add Polish (Phase 6) → Test independently → Deploy/Demo (Responsive design + final validation ✨)
6. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

**Option 1: Story-focused**:
1. Team completes Setup (Phase 1) + Foundational (Phase 2) together
2. Once Foundational is done:
   - Developer A: User Story 1 (T011-T034)
   - Developer B: User Story 2 (T035-T058)
   - Developer C: User Story 3 (T059-T079)
3. Stories complete and integrate independently

**Option 2: Test-first focused** (recommended for TDD):
1. Team completes Setup (Phase 1) + Foundational (Phase 2) together
2. Developer A: Write ALL tests for US1, US2, US3 (RED phase)
3. Developer B: Implement US1 (GREEN phase)
4. Developer C: Implement US2 (GREEN phase)
5. Developer D: Implement US3 (GREEN phase)
6. Team: Refactor together

---

## Notes

- **Zero new dependencies**: Uses existing react-icons v5.5.0 (already installed)
- **TDD workflow**: Red-Green-Refactor cycle is MANDATORY for all component tests
- **[P] tasks**: Different files, no dependencies - safe to run in parallel
- **[Story] label**: Maps task to specific user story for traceability
- **Each user story**: Should be independently completable and testable
- **Verify tests FAIL**: Before implementing (RED phase validation)
- **Verify tests PASS**: After implementing (GREEN phase validation)
- **Commit**: After each logical group of tasks
- **Stop at checkpoints**: To validate story independently
- **Avoid**: Vague tasks, same file conflicts, cross-story dependencies that break independence
- **Coverage target**: 80% minimum for NavigationBar component
- **E2E tests**: 100% pass rate required before merge

---

## Summary

**Total Tasks**: 92 tasks across 6 phases
- Phase 1 (Setup): 4 tasks
- Phase 2 (Foundational): 6 tasks
- Phase 3 (User Story 1): 24 tasks
- Phase 4 (User Story 2): 24 tasks
- Phase 5 (User Story 3): 21 tasks
- Phase 6 (Polish): 13 tasks

**Task Count per User Story**:
- US1: 24 tasks (includes TDD cycle, E2E tests, coverage validation)
- US2: 24 tasks (includes visual design, CSS styling, Figma verification)
- US3: 21 tasks (includes state management, navigation persistence)

**Parallel Opportunities Identified**: 45+ tasks marked [P] for parallel execution

**Independent Test Criteria**:
- US1: Click navigation items, verify navigation and active state ✅
- US2: Compare with Figma design for visual consistency ✅
- US3: Test browser back/forward, direct URL access for state persistence ✅

**Suggested MVP Scope**: User Story 1 only (Phase 1 + Phase 2 + Phase 3 = 34 tasks)

**Format Validation**: ✅ ALL 92 tasks follow checklist format (checkbox, ID, labels, file paths)

**Ready for Implementation**: ✅ YES - All tasks are specific, atomic, and immediately executable

