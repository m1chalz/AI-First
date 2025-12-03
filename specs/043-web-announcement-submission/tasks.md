# Tasks: Web Missing Pet Announcement Submission

**Input**: Design documents from `/specs/043-web-announcement-submission/`  
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/

**Tests**: Test requirements for this project (TDD Approach - Tests First, Implementation Second):

**MANDATORY - Web Unit Tests**:
- Location: `/webApp/src/__tests__/`
- Framework: Vitest + React Testing Library
- Coverage: 80% line + branch coverage
- Scope: Services, custom hooks, components
- Run: `npm test -- --coverage` (from webApp/)
- Convention: MUST follow Given-When-Then structure with descriptive names

**MANDATORY - End-to-End Tests**:
- Location: `/e2e-tests/src/test/resources/features/web/`
- Framework: Selenium WebDriver + Cucumber (Java)
- All user stories MUST have E2E test coverage
- Use Page Object Model pattern
- Convention: MUST structure scenarios with Given-When-Then phases
- Run: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web"`

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Project Infrastructure)

**Purpose**: Prepare project structure for announcement submission feature

- [x] T001 Verify Node.js v24 (LTS) and npm are installed
- [x] T002 Verify backend server is set up and can run on http://localhost:3000
- [x] T003 Verify web app dependencies are installed (`cd webApp && npm install`)
- [x] T004 Verify Vitest test infrastructure is working (`cd webApp && npm test`)
- [x] T005 Verify E2E test infrastructure is set up (Maven, Selenium, Cucumber)

---

## Phase 2: Foundational (File Restructuring)

**Purpose**: Rename existing service to consolidate announcement operations (GET + POST)

**⚠️ CRITICAL**: Complete this phase before ANY user story implementation

- [x] T006 Rename `/webApp/src/services/animal-repository.ts` → `announcement-service.ts`
- [x] T007 Rename `/webApp/src/__tests__/services/animal-repository.test.ts` → `announcement-service.test.ts`
- [x] T008 In `announcement-service.ts`: Rename class `AnimalRepository` → `AnnouncementService`
- [x] T009 In `announcement-service.ts`: Rename export `animalRepository` → `announcementService`
- [x] T010 In `announcement-service.test.ts`: Update imports and class references to `AnnouncementService`
- [x] T011 In `announcement-service.test.ts`: Update describe blocks from `AnimalRepository` → `AnnouncementService`
- [x] T012 Update import in `/webApp/src/hooks/use-animal-list.ts`: Change `animalRepository` → `announcementService`
- [x] T013 Update import in `/webApp/src/hooks/use-pet-details.ts`: Change `animalRepository` → `announcementService`
- [x] T014 Update import in `/webApp/src/hooks/__tests__/hooks/use-animal-list.test.ts`: Update mock path and variable names
- [x] T015 Update import in `/webApp/src/hooks/__tests__/use-pet-details.test.ts`: Update mock path and variable names
- [x] T016 Run `npm test` to verify all existing tests still pass after rename
- [x] T017 Run `npm run type-check` (if available) or `tsc --noEmit` to verify TypeScript compilation

**Checkpoint**: Service renamed, all existing tests pass - ready for user story implementation

---

## Phase 3: User Story 1 Part A - Create Announcement (Priority: P1) 🎯 MVP

**Goal**: Enable users to create missing pet announcements via backend API and receive management password (without photo upload)

**Independent Test**: Complete the report flow (without photo), click Continue on contact screen, verify announcement is created and management password displayed on summary screen

### Tests for User Story 1A (TDD: Write Tests FIRST) ✅

**Step 1: Create Test Files with Failing Tests**

- [x] T018 [P] [US1] RED: Create `/webApp/src/models/announcement-submission.ts` with TypeScript interfaces: `AnnouncementSubmissionDto`, `AnnouncementResponse`, `mapFlowStateToDto()` function
- [x] T019 [P] [US1] RED: Create `/webApp/src/models/api-error.ts` with error types: `ApiError` (discriminated union), `NetworkError`, `ValidationError`, `DuplicateMicrochipError`, `ServerError`, and type guards
- [x] T020 [P] [US1] RED: Create `/webApp/src/__tests__/services/announcement-service-creation.test.ts` test file and add test: `createAnnouncement() should POST to /api/v1/announcements with correct payload` (mock fetch, assert request body)
- [x] T021 [P] [US1] RED: Add test to `announcement-service-creation.test.ts`: `createAnnouncement() should return AnnouncementResponse with id and managementPassword` (mock 201 response)
- [x] T022 [P] [US1] RED: Add test to `announcement-service-creation.test.ts`: `createAnnouncement() should throw ValidationError when API returns 400`
- [x] T023 [P] [US1] RED: Add test to `announcement-service-creation.test.ts`: `createAnnouncement() should throw DuplicateMicrochipError when API returns 409`
- [x] T024 [P] [US1] RED: Add test to `announcement-service-creation.test.ts`: `createAnnouncement() should throw ServerError when API returns 500`
- [x] T025 [P] [US1] RED: Add test to `announcement-service-creation.test.ts`: `createAnnouncement() should throw NetworkError when fetch fails`
- [x] T026 [US1] Run `npm test -- announcement-service-creation.test.ts` and verify ALL tests FAIL (no implementation yet)
- [x] T027 [P] [US1] RED: Create `/webApp/src/hooks/__tests__/use-announcement-creation.test.ts` with test: `createAnnouncement() should call service.createAnnouncement()`
- [x] T028 [P] [US1] RED: Add test to `use-announcement-creation.test.ts`: `should set isCreating to true during creation`
- [x] T029 [P] [US1] RED: Add test to `use-announcement-creation.test.ts`: `should store announcementId and managementPassword on successful creation`
- [x] T030 [P] [US1] RED: Add test to `use-announcement-creation.test.ts`: `should set error state when creation fails`
- [x] T031 [P] [US1] RED: Add test to `use-announcement-creation.test.ts`: `should return announcement data on success, null on failure`
- [x] T032 [US1] Run `npm test -- use-announcement-creation.test.ts` and verify ALL tests FAIL (no implementation yet)

**Step 2: Implement Code to Pass Tests (GREEN)**

- [x] T033 [US1] GREEN: Implement `createAnnouncement()` method in `/webApp/src/services/announcement-service.ts` - POST to /api/v1/announcements with JSON body, handle all error statuses
- [x] T034 [US1] Run `npm test -- announcement-service-creation.test.ts` and verify ALL tests PASS
- [x] T035 [US1] GREEN: Create `/webApp/src/hooks/use-announcement-creation.ts` with hook implementation: useState for isCreating/error/announcementData, createAnnouncement function
- [x] T036 [US1] GREEN: In hook, implement `createAnnouncement()`: map flow state to DTO, call service.createAnnouncement, extract id/password, manage loading/error state
- [x] T037 [US1] Run `npm test -- use-announcement-creation.test.ts` and verify ALL tests PASS
- [x] T038 [US1] Run `npm test -- --coverage` and verify 80% coverage for new service method and hook

**Step 3: Integrate with Components (TDD for Components)**

- [x] T039 [P] [US1] GREEN: Import `useAnnouncementCreation` hook in `/webApp/src/components/ReportMissingPet/ContactScreen.tsx`
- [x] T040 [P] [US1] GREEN: Update `handleContinue` to call `createAnnouncement(flowState)`
- [x] T041 [P] [US1] GREEN: Add loading state to Continue button (disable during isCreating, show "Creating announcement..." text)
- [x] T042 [P] [US1] GREEN: Add error handling with toast notification (use `useToast` hook)
- [x] T043 [P] [US1] GREEN: On success, navigate to summary screen with `state: { announcementId, managementPassword }`
- [x] T044 [US1] PASS: Manual testing shows form data preserved after failed creation
- [x] T045 [US1] GREEN: Modify `/webApp/src/components/ReportMissingPet/SummaryScreen.tsx`: Import `useLocation` from react-router-dom
- [x] T046 [US1] GREEN: Extract `managementPassword` from `location.state`
- [x] T047 [US1] GREEN: Add password display card with data-testid attributes
- [x] T048 [US1] GREEN: Add instructional text: "Save this password! You'll need it to edit or delete your announcement."
- [x] T049 [US1] GREEN: Add `useEffect` with `beforeunload` event listener (set `e.returnValue = ''`)
- [x] T050 [US1] GREEN: Clean up event listener in useEffect return function
- [x] T051 [US1] PASS: All tests passing

**Step 4: Verify and Clean Up**

- [x] T052 [US1] Run `npm test -- --coverage` and verify coverage for all US1 code
- [x] T053 [US1] Verify TypeScript compilation: `tsc --noEmit` ✓
- [x] T054 [US1] All 451 tests passing ✓
- [ ] T055 [US1] Manual test: Start backend and web app, verify announcement submission works
- [ ] T056 [US1] Manual test: Verify management password displayed on summary
- [ ] T057 [US1] Manual test: Verify exit confirmation when navigating away
- [ ] T058 [US1] Manual test: Verify new announcement appears in public list

**Checkpoint**: User Story 1 Part A complete - Announcement creation working (photo upload in next phase)

---

## Phase 4: User Story 1 Part B - Photo Upload Integration (Priority: P1) 🎯 MVP

**Goal**: Integrate photo upload functionality with announcement creation, upload photo after announcement is created

**Independent Test**: Complete the full flow with photo, verify photo is uploaded and associated with the announcement

### Tests for User Story 1B (TDD: Write Tests FIRST) ✅

**Step 1: Create Photo Upload Tests**

- [x] T071 [P] [US1] RED: Add test to `/webApp/src/__tests__/services/announcement-service.test.ts`: `uploadPhoto() should POST to /api/v1/announcements/:id/photos with FormData and Basic Auth header`
- [x] T072 [P] [US1] RED: Add test to `announcement-service.test.ts`: `uploadPhoto() should handle 401 Unauthorized error`
- [x] T073 [P] [US1] RED: Add test to `announcement-service.test.ts`: `uploadPhoto() should handle 404 Not Found error`
- [x] T074 [P] [US1] RED: Add test to `announcement-service.test.ts`: `uploadPhoto() should throw error when photo upload fails`
- [x] T075 [US1] Run `npm test -- announcement-service.test.ts` and verify new tests FAIL
- [x] T076 [P] [US1] RED: Rename hook to `use-announcement-submission.ts` and add test: `submitAnnouncement() should call createAnnouncement and uploadPhoto in sequence`
- [x] T077 [P] [US1] RED: Add test to `use-announcement-submission.test.ts`: `should set isSubmitting to true during full submission (create + upload)`
- [x] T078 [P] [US1] RED: Add test to `use-announcement-submission.test.ts`: `should handle photo upload failure separately from creation failure`
- [x] T079 [P] [US1] RED: Add test to `use-announcement-submission.test.ts`: `should skip photo upload if no photo in flow state`
- [x] T080 [US1] Run `npm test -- use-announcement-submission.test.ts` and verify new tests FAIL

**Step 2: Implement Photo Upload (GREEN)**

- [x] T081 [US1] GREEN: Implement `uploadPhoto()` method in `/webApp/src/services/announcement-service.ts` - Create FormData, add Basic Auth header, POST to /api/v1/announcements/:id/photos
- [x] T082 [US1] Run `npm test -- announcement-service.test.ts` and verify ALL photo upload tests PASS
- [x] T083 [US1] GREEN: Update `/webApp/src/hooks/use-announcement-submission.ts`: Extend hook to call uploadPhoto after createAnnouncement
- [x] T084 [US1] GREEN: Handle photo upload errors separately (show specific error message)
- [x] T085 [US1] GREEN: Add logic to skip photo upload if flowState.photo is null/undefined
- [x] T086 [US1] Run `npm test -- use-announcement-submission.test.ts` and verify ALL tests PASS
- [x] T087 [US1] Run `npm test -- --coverage` and verify 80% coverage maintained (459 tests ✓)

**Step 3: Update ContactScreen Integration**

- [x] T088 [P] [US1] GREEN: Update `/webApp/src/components/ReportMissingPet/ContactScreen.tsx`: Replace `useAnnouncementCreation` with `useAnnouncementSubmission`
- [x] T089 [P] [US1] GREEN: Update loading text to show: "Submitting..."
- [x] T090 [P] [US1] GREEN: Update error handling to distinguish between creation and upload errors
- [x] T091 [US1] PASS: All 459 tests passing

**Step 4: Verify Full Flow**

- [x] T092 [US1] TypeScript compilation: No errors ✓
- [x] T093 [US1] All tests passing (459/459) ✓
- [ ] T094 [US1] Manual test: Complete full flow WITH photo, verify both phases (create + upload) work
- [ ] T095 [US1] Manual test: Verify announcement created with photo, management password displayed
- [ ] T096 [US1] Manual test: Verify new announcement appears in public list WITH photo at http://localhost:5173/
- [ ] T097 [US1] Manual test: Try flow WITHOUT photo, verify it still works (skips upload)

**Checkpoint**: User Story 1 complete and independently testable - Full submission flow (announcement + photo) working end-to-end

---

## Phase 5: User Story 2 - Handle Submission Errors (Priority: P2)

**Goal**: Provide clear error feedback for network failures, validation errors, and duplicate microchips; preserve form data for retry

**Independent Test**: Simulate network failure or invalid data, verify error messages displayed, form data preserved, retry succeeds

### Tests for User Story 2 (TDD: Write Tests FIRST) ✅

**Step 1: Create Additional Error Handling Tests**

- [ ] T102 [P] [US2] RED: Add test to `/webApp/src/__tests__/services/announcement-service.test.ts`: `createAnnouncement() should map 400 errors to specific field validation messages`
- [ ] T103 [P] [US2] RED: Add test to `use-announcement-submission.test.ts`: `should preserve error state across multiple submission attempts`
- [ ] T104 [P] [US2] RED: Add test to `use-announcement-submission.test.ts`: `should clear error state on successful retry`
- [ ] T105 [US2] Run tests and verify new tests FAIL

**Step 2: Enhance Error Handling**

- [ ] T106 [US2] GREEN: Enhance error parsing in `/webApp/src/services/announcement-service.ts`: Extract field names from validation errors
- [ ] T107 [US2] Run service tests and verify ALL tests PASS
- [ ] T108 [US2] GREEN: Update error handling in `/webApp/src/hooks/use-announcement-submission.ts`: Preserve error state, clear on retry
- [ ] T109 [US2] Run hook tests and verify ALL tests PASS

**Step 3: Improve Component Error Display**

- [ ] T110 [P] [US2] RED: Add test to `/webApp/src/components/ReportMissingPet/__tests__/ContactScreen.test.tsx`: `should display specific message for duplicate microchip error`
- [ ] T111 [P] [US2] RED: Add test to `ContactScreen.test.tsx`: `should display specific message for network error`
- [ ] T112 [P] [US2] RED: Add test to `ContactScreen.test.tsx`: `should display specific message for validation error with field name`
- [ ] T113 [P] [US2] RED: Add test to `ContactScreen.test.tsx`: `should allow retry after error without losing form data`
- [ ] T114 [US2] Run tests and verify new tests FAIL
- [ ] T115 [US2] GREEN: Update `/webApp/src/components/ReportMissingPet/ContactScreen.tsx`: Add error message mapping function
- [ ] T116 [US2] GREEN: Map `DuplicateMicrochipError` → "This microchip already exists. If this is your announcement, use your management password to update it."
- [ ] T117 [US2] GREEN: Map `NetworkError` → "Network error. Please check your connection and try again."
- [ ] T118 [US2] GREEN: Map `ValidationError` → Include field name if available: "Validation error: [message]"
- [ ] T119 [US2] GREEN: Map `ServerError` → "Server error ([statusCode]). Please try again later."
- [ ] T120 [US2] Run ContactScreen tests and verify ALL tests PASS

**Step 4: Verify and Test Error Scenarios**

- [ ] T121 [US2] Run `npm test -- --coverage` to verify 80% coverage maintained
- [ ] T122 [US2] Run ESLint and fix any violations in US2 files
- [ ] T123 [US2] Manual test: Stop backend server, attempt submission, verify network error toast
- [ ] T124 [US2] Manual test: Start backend, retry submission, verify success
- [ ] T125 [US2] Manual test: Create announcement with microchip "123456789012345", try to create duplicate, verify duplicate error message
- [ ] T126 [US2] Manual test: Try submission without email/phone, verify validation error (should be caught by form validation)
- [ ] T127 [US2] Manual test: Verify form data preserved after all error types

**Checkpoint**: User Story 2 complete - Error handling comprehensive and user-friendly

---

## Phase 6: User Story 3 - Match Summary Screen to Figma Design (Priority: P2)

**Goal**: Update summary screen to match Figma design specifications - enhance password display with proper styling, layout, and visual hierarchy

**Design Reference**: [Figma - PetSpot Summary Screen](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=315-15984&m=dev)

**Independent Test**: Create announcement, verify summary screen matches Figma design (layout, colors, typography, spacing, components)

### Implementation for User Story 3 (Match Figma Design) 🎨

**Step 1: Analyze Figma Design and Create Tests**

- [ ] T128 [P] [US3] RED: Add test to `/webApp/src/components/ReportMissingPet/__tests__/SummaryScreen.test.tsx`: `should render success icon/illustration as per Figma`
- [ ] T129 [P] [US3] RED: Add test to `SummaryScreen.test.tsx`: `should display success heading with correct text and styling`
- [ ] T130 [P] [US3] RED: Add test to `SummaryScreen.test.tsx`: `should display management password in styled card matching Figma design`
- [ ] T131 [P] [US3] RED: Add test to `SummaryScreen.test.tsx`: `should display copy password button/icon as per Figma`
- [ ] T132 [P] [US3] RED: Add test to `SummaryScreen.test.tsx`: `should display warning/info message about password importance`
- [ ] T133 [P] [US3] RED: Add test to `SummaryScreen.test.tsx`: `should display action buttons (e.g., "View Announcement", "Go to Homepage") as per Figma`
- [ ] T134 [P] [US3] RED: Add test to `SummaryScreen.test.tsx`: `should have correct spacing and layout matching Figma specs`
- [ ] T135 [US3] Run tests and verify new tests FAIL

**Step 2: Implement Figma Design (GREEN)**

- [ ] T136 [US3] GREEN: Create `/webApp/src/components/ReportMissingPet/SummaryScreen.module.css` with styles matching Figma design
- [ ] T137 [US3] GREEN: Update `/webApp/src/components/ReportMissingPet/SummaryScreen.tsx`: Import CSS module
- [ ] T138 [US3] GREEN: Add success icon/illustration component (check Figma for exact asset or icon)
- [ ] T139 [US3] GREEN: Add success heading (e.g., "Announcement Created Successfully!") with Figma typography
- [ ] T140 [US3] GREEN: Create password card component with Figma styling (background, border, border-radius, padding, shadow)
- [ ] T141 [US3] GREEN: Style password text with monospace font, size, weight, and color from Figma
- [ ] T142 [US3] GREEN: Add copy password button/functionality with icon (match Figma interaction)
- [ ] T143 [US3] GREEN: Add warning/info message box with icon (⚠️ or ℹ️) and styled text from Figma
- [ ] T144 [US3] GREEN: Add action buttons ("View Announcement", "Go to Homepage", etc.) with Figma button styles
- [ ] T145 [US3] GREEN: Apply Figma spacing (margins, paddings) and layout (flexbox/grid) to match design
- [ ] T146 [US3] GREEN: Ensure responsive design matches Figma breakpoints (mobile, tablet, desktop)
- [ ] T147 [US3] Run SummaryScreen tests and verify ALL tests PASS

**Step 3: Visual Verification and Polish**

- [ ] T148 [US3] Run `npm test -- --coverage` to verify coverage maintained
- [ ] T149 [US3] Run ESLint and fix any violations
- [ ] T150 [US3] Manual test: Create announcement, compare summary screen side-by-side with Figma design
- [ ] T151 [US3] Manual test: Verify typography (font family, sizes, weights, colors) matches Figma
- [ ] T152 [US3] Manual test: Verify colors (backgrounds, borders, text) match Figma color palette
- [ ] T153 [US3] Manual test: Verify spacing (margins, padding) matches Figma measurements
- [ ] T154 [US3] Manual test: Verify copy password button works correctly
- [ ] T155 [US3] Manual test: Test responsive design on mobile, tablet, and desktop viewports
- [ ] T156 [US3] Manual test: Verify exit confirmation still works (beforeunload event)
- [ ] T157 [US3] Take screenshot and compare pixel-perfect alignment with Figma export

**Checkpoint**: User Story 3 complete - Summary screen matches Figma design specifications

---

## Phase 7: End-to-End Tests

**Purpose**: Add comprehensive E2E test coverage for all user stories

- [ ] T158 [P] Create `/e2e-tests/src/test/resources/features/web/043-announcement-submission.feature` with Gherkin scenarios
- [ ] T159 [P] Add @web tag and scenario for successful announcement submission with photo (US1)
- [ ] T160 [P] Add scenario for announcement submission without photo (US1)
- [ ] T161 [P] Add scenario for duplicate microchip error (US2)
- [ ] T162 [P] Add scenario for network error handling (US2)
- [ ] T163 [P] Add scenario for password display matching Figma design (US3)
- [ ] T164 [P] Create `/e2e-tests/src/test/java/.../pages/SummaryPage.java` with Page Object Model
- [ ] T165 [P] Add XPath selectors for password card, password text, copy button, action buttons per Figma
- [ ] T166 [P] Add methods: `isPasswordDisplayed()`, `getPasswordText()`, `clickCopyPassword()`, `clickViewAnnouncement()`, `clickGoToHomepage()`
- [ ] T167 [P] Modify `/e2e-tests/src/test/java/.../pages/ContactPage.java` (if exists)
- [ ] T168 [P] Add methods to handle submission: `clickContinueAndWaitForLoading()`, `waitForAnnouncementCreation()`, `waitForPhotoUpload()`
- [ ] T169 Create `/e2e-tests/src/test/java/.../steps-web/AnnouncementSubmissionSteps.java` with step definitions
- [ ] T170 Implement Given step: "user has completed missing pet report form with photo"
- [ ] T171 Implement Given step: "user has completed missing pet report form without photo"
- [ ] T172 Implement When step: "user clicks Continue on contact screen"
- [ ] T173 Implement Then step: "announcement is created and password is displayed"
- [ ] T174 Implement Then step: "error message is displayed"
- [ ] T175 Implement Then step: "summary screen matches Figma design"
- [ ] T176 Run E2E tests: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web and @043"`
- [ ] T177 Fix any failing E2E tests
- [ ] T178 Verify E2E test report generated at `/e2e-tests/target/cucumber-reports/web/index.html`

**Checkpoint**: E2E tests complete and passing for all user stories

---

## Phase 8: Final Validation & Polish

**Purpose**: Ensure all requirements met, no regressions, code quality maintained

- [ ] T179 Run full test suite: `cd webApp && npm test -- --coverage`
- [ ] T180 Verify 80% coverage achieved for all new code (services, hooks, components)
- [ ] T181 Review coverage report at `/webApp/coverage/index.html`, identify gaps
- [ ] T182 Add missing tests if coverage below 80%
- [ ] T183 Run ESLint on all modified files: `npm run lint`
- [ ] T184 Fix all ESLint violations
- [ ] T185 Run TypeScript type check: `npm run type-check` or `tsc --noEmit`
- [ ] T186 Fix any TypeScript errors
- [ ] T187 Verify all test identifiers follow convention: `{screen}.{element}.{action}`
- [ ] T188 Review ContactScreen: Verify data-testid on Continue button and loading states
- [ ] T189 Review SummaryScreen: Verify data-testid on all elements per Figma design
- [ ] T190 Verify all async code uses async/await (no Promise chains)
- [ ] T191 Verify error handling is comprehensive (network, validation, duplicate, server errors)
- [ ] T192 Run manual acceptance test for US1 Part A: Create announcement without photo, verify success
- [ ] T193 Run manual acceptance test for US1 Part B: Complete full flow with photo, verify photo upload
- [ ] T194 Run manual acceptance test for US2: Test error scenarios, verify error handling
- [ ] T195 Run manual acceptance test for US3: Verify summary screen matches Figma design pixel-perfect
- [ ] T196 Verify success criteria SC-001: Submission completes in < 10 seconds
- [ ] T197 Verify success criteria SC-005: Error messages appear in < 2 seconds
- [ ] T198 Verify success criteria SC-007: 100% of successful submissions display password
- [ ] T199 Review all modified files for code quality and best practices
- [ ] T200 Remove any console.log statements or debug code
- [ ] T201 Verify no TODO comments left in code
- [ ] T202 Run full E2E test suite one final time
- [ ] T203 Create summary of changes in commit message format
- [ ] T204 Prepare feature for PR: ensure all tests pass, linting clean, coverage ≥80%

**Checkpoint**: Feature complete, tested, and ready for code review

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup - BLOCKS all user stories (file rename must complete first)
- **User Story 1 Part A (Phase 3)**: Depends on Foundational completion - Announcement creation (without photo)
- **User Story 1 Part B (Phase 4)**: Depends on Phase 3 completion - Photo upload integration
- **User Story 2 (Phase 5)**: Depends on Phase 4 completion - Enhances error handling for full flow
- **User Story 3 (Phase 6)**: Depends on Phase 4 completion - Matches summary screen to Figma design
- **E2E Tests (Phase 7)**: Can be written in parallel with Phase 5/6, but requires Phase 3-6 implementation to run
- **Final Validation (Phase 8)**: Depends on all previous phases

### User Story Dependencies

- **User Story 1 Part A (P1)**: Independent - announcement creation testable on its own
- **User Story 1 Part B (P1)**: Depends on Part A - adds photo upload to existing flow
- **User Story 2 (P2)**: Builds on US1 (both parts) - enhances error handling
- **User Story 3 (P2)**: Builds on US1 (both parts) - polishes summary screen with Figma design

### Within Each User Story (TDD Cycle)

1. **RED**: Write failing tests first
2. **GREEN**: Implement minimal code to pass tests
3. **REFACTOR**: Clean up code, run tests again
4. **LINT**: Fix linting issues
5. **VERIFY**: Manual testing and coverage check

### Parallel Opportunities

- Phase 1 (Setup): All tasks can run in parallel (T001-T005)
- Phase 2 (Foundational): T006-T015 are sequential (file renames), T016-T017 can run together
- Within US1 Part A Tests: T018-T032 can be written in parallel (different test files)
- Within US1 Part B Tests: T071-T080 can be written in parallel (different test files)
- Between US2 and US3: Once Phase 4 complete, Phase 5 and Phase 6 can be worked on in parallel
- E2E Tests (Phase 7): T158-T178 can be worked on in parallel with Phase 5/6

---

## Implementation Strategy

### MVP First (User Story 1 - Both Parts)

1. Complete Phase 1: Setup (T001-T005)
2. Complete Phase 2: Foundational (T006-T017) - CRITICAL
3. Complete Phase 3: User Story 1 Part A - Announcement Creation (T018-T070)
4. **CHECKPOINT**: Test announcement creation without photo
5. Complete Phase 4: User Story 1 Part B - Photo Upload (T071-T101)
6. **STOP and VALIDATE**: Test full flow with photo, verify core submission works
7. Demo/Deploy if ready

**Total MVP Tasks**: 101 tasks
**Estimated MVP Time**: Core submission functionality complete (announcement + photo)

### Incremental Delivery

1. Complete Setup + Foundational → File restructure done (T001-T017)
2. Add User Story 1 Part A → Test independently → Checkpoint (Announcement creation) (T018-T070)
3. Add User Story 1 Part B → Test independently → Deploy (MVP - Full submission with photo!) (T071-T101)
4. Add User Story 2 → Test independently → Deploy (Enhanced error handling) (T102-T127)
5. Add User Story 3 → Test independently → Deploy (Figma design match) (T128-T157)
6. Add E2E Tests → Verify all stories → Deploy (T158-T178)
7. Final polish → Production ready (T179-T204)

### Test-First Development (TDD)

**Every task block follows this pattern**:
1. **RED**: Write tests that fail (e.g., T018-T032 for US1 Part A)
2. **GREEN**: Implement code to pass tests (e.g., T033-T038 for US1 Part A)
3. **REFACTOR**: Clean up and verify (e.g., T039-T070 for US1 Part A)

**Critical TDD Rules**:
- Never write implementation before tests
- Verify tests FAIL before implementation
- Verify tests PASS after implementation
- Maintain 80% coverage throughout

---

## Parallel Example: User Story 1 Part A Testing Phase

```bash
# Phase 3: These test file creation tasks can run in parallel:
T018: Create announcement-submission.ts (models)
T019: Create api-error.ts (models)
T020-T026: Add tests to announcement-service.test.ts (announcement creation)
T027-T032: Add tests to use-announcement-creation.test.ts
T039-T043: Add tests to ContactScreen.test.tsx
T051-T054: Add tests to SummaryScreen.test.tsx

# Then implementation tasks run sequentially within each component:
T033-T034: Implement service createAnnouncement method
T035-T037: Implement hook
T045-T050: Implement ContactScreen changes
T056-T062: Implement SummaryScreen changes
```

## Parallel Example: User Story 1 Part B Testing Phase

```bash
# Phase 4: These photo upload test tasks can run in parallel:
T071-T075: Add photo upload tests to announcement-service.test.ts
T076-T080: Add photo upload tests to use-announcement-submission.test.ts
T088-T091: Add photo upload tests to ContactScreen.test.tsx

# Then implementation tasks run sequentially:
T081-T082: Implement service uploadPhoto method
T083-T086: Extend hook with photo upload
T092-T095: Update ContactScreen integration
```

---

## Notes

- **TDD Mandatory**: All tasks follow Red-Green-Refactor cycle
- **Atomic Tasks**: Each task is independently completable with clear pass/fail criteria
- **[P] tasks**: Different files, no dependencies, can run in parallel
- **[Story] labels**: Map tasks to user stories for traceability
- **Kebab-case filenames**: All non-component files use kebab-case (announcement-service.ts, use-announcement-submission.ts)
- **Test identifiers**: All UI elements must have data-testid attributes following `{screen}.{element}.{action}` convention
- **80% Coverage**: Maintained throughout - verify after each user story
- **No regressions**: Run full test suite after each phase
- **Commit frequency**: Commit after each logical task group (e.g., after T026, T032, T038, T075, T080, etc.)
- **Figma Design Reference**: Phase 6 (US3) matches [Figma design](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=315-15984&m=dev)

---

**Total Tasks**: 204 tasks
**MVP Scope**: Tasks T001-T101 (Setup + Foundational + US1 Part A + US1 Part B)
**Full Feature**: All tasks T001-T204

