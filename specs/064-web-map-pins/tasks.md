# Tasks: Web Map Pins

**Input**: Design documents from `/specs/064-web-map-pins/`  
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/api-announcements.md

**Tests**: Test requirements for this project:

**MANDATORY - Web Unit Tests**:
- Location: `/webApp/src/hooks/__test__/`, `/webApp/src/lib/__test__/`, `/webApp/src/components/map/__tests__/`
- Framework: Vitest + React Testing Library
- Coverage: 80% line + branch coverage
- Scope: Custom hooks (`use-map-pins`), utilities (`map-pin-helpers`), components (`MapPinLayer`)
- Convention: MUST follow Given-When-Then structure with descriptive test names
- Run: `npm test --coverage` (from webApp/)

**MANDATORY - End-to-End Tests**:
- Location: `/e2e-tests/web/specs/064-web-map-pins.spec.ts`
- Framework: Playwright + TypeScript
- All user stories MUST have E2E test coverage
- Convention: MUST structure scenarios with Given-When-Then phases
- Run: `mvn test -Dtest=WebTestRunner` (from e2e-tests/java/)

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- Web app: `webApp/src/`
- E2E tests: `e2e-tests/web/specs/` or `e2e-tests/java/src/test/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [X] T001 Verify dependencies in `/webApp/package.json` (leaflet@1.9.x, react-leaflet@4.x, @types/leaflet installed)
- [X] T002 [P] Create `/webApp/src/hooks/` directory if not exists
- [X] T003 [P] Create `/webApp/src/lib/` directory if not exists
- [X] T004 [P] Create `/webApp/src/components/map/` directory if not exists
- [X] T005 [P] Verify ESLint config in `/webApp/eslint.config.mjs` includes TypeScript plugin
- [X] T006 [P] Verify Vitest config in `/webApp/vite.config.ts` includes 80% coverage threshold

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T007 Setup Leaflet mock for tests in `/webApp/src/__tests__/setup.ts` (mock `L.divIcon`, `L.Marker`, etc.)
- [X] T008 [P] Verify existing `AnnouncementService` in `/webApp/src/services/` can be reused
- [X] T009 [P] Setup E2E test infrastructure for web in `/e2e-tests/java/` (Java 21 + Maven + Selenium + Cucumber)
- [X] T010 [P] Create base Page Object for landing page in `/e2e-tests/java/src/test/java/.../pages/LandingPage.java` (if not exists)

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - See Missing Animals as Pins (Priority: P1) 🎯 MVP

**Goal**: Display interactive map pins for missing animals on the landing page map. Pins appear at last-seen locations, update when map viewport changes, and show both missing (red) and found (blue) pets with distinct teardrop markers.

**Independent Test**: Open landing page with location permission granted, verify pins appear for announcements within viewport, zoom/pan map and verify pins update.

### Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Write these tests FIRST (TDD workflow), ensure they FAIL before implementation**

**Web Unit Tests** (TDD: Red-Green-Refactor):
- [X] T011 [P] [US1] RED: Write failing unit test for `useMapPins` hook in `/webApp/src/hooks/__tests__/use-map-pins.test.ts` (test: returns empty pins initially, fetches pins when userLocation provided, handles AbortController cleanup)
- [X] T012 [P] [US1] RED: Write failing unit test for `createPinIcon` helper in `/webApp/src/lib/__test__/map-pin-helpers.test.ts` (test: returns Leaflet divIcon with correct color for missing/found status)
- [X] T013 [P] [US1] RED: Write failing component test for `MapPinLayer` in `/webApp/src/components/map/__tests__/MapPinLayer.test.tsx` (test: renders markers for each pin, calls API with userLocation)

**End-to-End Tests**:
- [ ] T014 [P] [US1] Create Gherkin feature file in `/e2e-tests/java/src/test/resources/features/web/064-web-map-pins.feature` (Scenario: Display pins on landing page map)
- [ ] T015 [P] [US1] Create step definitions in `/e2e-tests/java/src/test/java/.../steps/web/MapPinsSteps.java` (steps for US1 scenarios)
- [ ] T016 [P] [US1] Add Page Object methods to `LandingPage.java` for pin interactions (findPinById, getPinCount, verifyPinAtLocation)

### Implementation for User Story 1

**Web** (Full Stack Implementation - TDD: Red-Green-Refactor):
- [X] T017 [P] [US1] Create `PetPin` TypeScript interface in `/webApp/src/models/pet-pin.ts` (id, name, species, status, latitude, longitude, photoUrl, phoneNumber, email, createdAt)
- [X] T018 [P] [US1] Create `MapPinsState` TypeScript interface in `/webApp/src/models/map-pins-state.ts` (pins, loading, error)
- [X] T019 [US1] GREEN: Implement `createPinIcon` helper in `/webApp/src/lib/map-pin-helpers.ts` (minimal code to pass test T012 - use L.divIcon with SVG teardrop, color based on status: red #EF4444 for missing, blue #155DFC for found, white symbol: ! for missing, ✓ for found)
- [X] T020 [US1] REFACTOR: Improve `createPinIcon` code quality (extract SVG template, apply Clean Code principles)
- [X] T021 [US1] GREEN: Implement `useMapPins` hook in `/webApp/src/hooks/use-map-pins.ts` (minimal code to pass test T011 - accept userLocation param, fetch from `/api/v1/announcements?lat=X&lng=Y&range=10`, inline transformation to PetPin, return {pins, loading, error}, use AbortController)
- [X] T022 [US1] REFACTOR: Improve `useMapPins` code quality (extract error handling, apply Clean Code principles)
- [X] T023 [US1] GREEN: Implement `MapPinLayer` component in `/webApp/src/components/map/MapPinLayer.tsx` (minimal code to pass test T013 - call useMapPins hook, map pins to React-Leaflet <Marker> components with createPinIcon, render loading/error overlays)
- [X] T024 [US1] REFACTOR: Improve `MapPinLayer` code quality (extract loading/error UI to separate components if needed)
- [X] T025 [US1] Add `data-testid` attributes to all interactive elements in MapPinLayer (each marker: `landingPage.map.pin.{petId}`, loading: `landingPage.map.pinsLoading`, error: `landingPage.map.pinsError`)
- [X] T026 [US1] Integrate `MapPinLayer` into existing landing page map component (render within map container, pass userLocation from map center)
- [X] T027 [P] [US1] Add JSDoc documentation to `useMapPins` hook (document params, return values, AbortController cleanup)
- [X] T028 [US1] Run `npm test --coverage` and verify 80% coverage for `use-map-pins` and `map-pin-helpers`
- [X] T029 [P] [US1] Run `npm run lint` and fix ESLint violations
- [ ] T030 [US1] Run E2E tests for US1: `mvn test -Dtest=WebTestRunner` (from e2e-tests/java/)
- [ ] T031 [US1] Manual testing: Open landing page, verify pins appear, zoom/pan and verify pins update

**Checkpoint**: At this point, User Story 1 should be fully functional - pins display on map and update with viewport changes

---

## Phase 4: User Story 2 - Open a Pet Details Pop-up (Priority: P2)

**Goal**: Enable users to click a pin and view pet details in a pop-up overlay. Pop-up shows pet photo, name, species, last-seen date, description, and contact info (phone, email). Pop-up dismisses via close button or click-outside.

**Independent Test**: Click a pin on the map, verify pop-up appears with all required pet details, close pop-up via close button, click pin again and close via click-outside, verify map remains interactive.

### Tests for User Story 2 (MANDATORY) ✅

**Web Unit Tests** (TDD: Red-Green-Refactor):
- [X] T032 [P] [US2] RED: Write failing component test for `MapPinLayer` with pop-up in `/webApp/src/components/map/__tests__/MapPinLayer.test.tsx` (test: Leaflet Popup renders pet details when marker clicked, pop-up content includes all required fields)

**End-to-End Tests**:
- [ ] T033 [P] [US2] Add Gherkin scenario to `/e2e-tests/java/src/test/resources/features/web/064-web-map-pins.feature` (Scenario: Open pet details pop-up)
- [ ] T034 [P] [US2] Add step definitions for US2 in `/e2e-tests/java/src/test/java/.../steps/web/MapPinsSteps.java` (steps for pop-up interactions)
- [ ] T035 [P] [US2] Add Page Object methods to `LandingPage.java` for pop-up (clickPin, verifyPopupVisible, getPopupContent, closePopupViaButton, closePopupViaClickOutside)

### Implementation for User Story 2

**Web** (Full Stack Implementation - TDD: Red-Green-Refactor):
- [X] T036 [US2] GREEN: Update `MapPinLayer` component in `/webApp/src/components/map/MapPinLayer.tsx` (minimal code to pass test T032 - add React-Leaflet <Popup> inside each <Marker>, render pet details: photo, name, species, last-seen date, description, phone, email)
- [X] T037 [US2] REFACTOR: Extract pop-up content to separate component if complex (optional - only if pop-up has >20 lines of JSX)
- [X] T038 [US2] Add `data-testid` attributes to pop-up elements (`landingPage.map.popup`, `landingPage.map.popup.close`)
- [X] T039 [US2] Add CSS styling for pop-up in `/webApp/src/components/map/MapPinLayer.css` (or inline styles - match Figma design)
- [X] T040 [P] [US2] Handle placeholder image for missing pet photos (add `onError` handler to <img> tag, fallback to `/images/placeholder-pet.png`)
- [X] T041 [US2] Verify Leaflet pop-up close behavior (close button + click-outside) works by default
- [X] T042 [US2] Run `npm test` and verify component tests pass for pop-up rendering
- [X] T043 [P] [US2] Run `npm run lint` and fix ESLint violations
- [ ] T044 [US2] Run E2E tests for US2: `mvn test -Dtest=WebTestRunner` (from e2e-tests/java/)
- [ ] T045 [US2] Manual testing: Click pin, verify pop-up shows all pet details, test close button and click-outside dismissal

**Checkpoint**: At this point, User Stories 1 AND 2 should both work - pins display and clicking them opens detailed pop-ups

---

## Phase 5: User Story 3 - Handle Errors and Loading (Priority: P3)

**Goal**: Provide clear feedback when pins are loading or fail to load. Show loading indicator during fetch, display user-friendly error message with retry button on failure, allow retry without full page reload.

**Independent Test**: Simulate slow network (DevTools throttling) to see loading indicator, simulate server error (disconnect backend) to see error state, click retry and verify pins reload successfully.

### Tests for User Story 3 (MANDATORY) ✅

**Web Unit Tests** (TDD: Red-Green-Refactor):
- [X] T046 [P] [US3] RED: Write failing unit test for `useMapPins` loading state in `/webApp/src/hooks/__test__/use-map-pins.test.ts` (test: loading is true during fetch, false after success/error)
- [X] T047 [P] [US3] RED: Write failing unit test for `useMapPins` error state in `/webApp/src/hooks/__test__/use-map-pins.test.ts` (test: error is set when fetch fails, error is null on success)
- [X] T048 [P] [US3] RED: Write failing component test for loading/error UI in `/webApp/src/components/map/__tests__/MapPinLayer.test.tsx` (test: renders loading spinner when loading=true, renders error message when error is set, retry button calls retry function)

**End-to-End Tests**:
- [ ] T049 [P] [US3] Add Gherkin scenario to `/e2e-tests/java/src/test/resources/features/web/064-web-map-pins.feature` (Scenario: Handle pin loading errors with retry)
- [ ] T050 [P] [US3] Add step definitions for US3 in `/e2e-tests/java/src/test/java/.../steps/web/MapPinsSteps.java` (steps for simulating network errors, verifying error UI, clicking retry)
- [ ] T051 [P] [US3] Add Page Object methods to `LandingPage.java` for loading/error states (verifyLoadingIndicatorVisible, verifyErrorMessageVisible, clickRetryButton)

### Implementation for User Story 3

**Web** (Full Stack Implementation - TDD: Red-Green-Refactor):
- [X] T052 [US3] GREEN: Update `MapPinLayer` component in `/webApp/src/components/map/MapPinLayer.tsx` (minimal code to pass test T048 - render loading overlay when loading=true, render error overlay when error is set, add retry button that re-triggers fetch)
- [X] T053 [US3] REFACTOR: Extract loading and error overlays to separate components in `/webApp/src/components/map/` (MapPinLoadingOverlay.tsx, MapPinErrorOverlay.tsx) if they have >10 lines each
- [X] T054 [US3] Add `data-testid` attributes to loading/error elements (`landingPage.map.pinsLoading`, `landingPage.map.pinsError`, `landingPage.map.pinsRetry`)
- [X] T055 [US3] Add CSS styling for loading/error overlays (position in top-right corner, semi-transparent background, does not obscure map controls)
- [X] T056 [US3] Implement retry mechanism in `useMapPins` hook (add retry counter to dependencies, increment on retry button click, triggers re-fetch)
- [X] T057 [US3] Run `npm test --coverage` and verify 80% coverage maintained for `use-map-pins` and `MapPinLayer`
- [X] T058 [P] [US3] Run `npm run lint` and fix ESLint violations
- [ ] T059 [US3] Run E2E tests for US3: `mvn test -Dtest=WebTestRunner` (from e2e-tests/java/)
- [ ] T060 [US3] Manual testing: Throttle network (DevTools), verify loading indicator, stop backend server, verify error message and retry button

**Checkpoint**: All user stories should now be independently functional - pins, pop-ups, and error handling all work

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [X] T061 [P] Add comprehensive JSDoc to all exported functions in `/webApp/src/hooks/` and `/webApp/src/lib/` (only where purpose is not obvious from name)
- [X] T062 [P] Code cleanup: Remove console.logs, unused imports, dead code across all new files
- [X] T063 [P] Verify all test identifiers follow convention in FR-013 (landingPage.map.pin.{petId}, landingPage.map.popup, etc.)
- [X] T064 [P] Run full test suite: `npm test --coverage` and verify 80% coverage across all new files
- [ ] T065 [P] Run full E2E suite: `mvn test -Dtest=WebTestRunner` and verify all scenarios pass
- [X] T066 [P] Verify pin markers match Figma design (colors: red #EF4444 for missing, blue #155DFC for found, teardrop shape, white border, drop shadow)
- [ ] T067 [P] Cross-browser testing: Test in Chrome, Firefox, Safari, Edge (latest 2 versions)
- [ ] T068 [P] Accessibility audit: Verify pins and pop-ups have proper ARIA labels and keyboard navigation
- [ ] T069 [P] Run quickstart.md validation: Follow steps in `/specs/064-web-map-pins/quickstart.md` and verify all commands work
- [ ] T070 [P] Update feature status in `/specs/064-web-map-pins/spec.md` (change Status from Draft to Ready for Review)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-5)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Phase 6)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Extends US1 by adding pop-up functionality to existing pins
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Extends US1 by adding loading/error states to existing pin fetching

**Note**: While US2 and US3 extend US1, they should remain independently testable. US2 can be tested by mocking pin data. US3 can be tested by simulating network conditions.

### Within Each User Story

- Tests MUST be written and FAIL before implementation (TDD: Red-Green-Refactor)
- Models/interfaces before hooks
- Hooks before components
- Components before integration into landing page
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, user stories CAN proceed in sequence (recommended due to dependencies) or with careful coordination in parallel
- All tests for a user story marked [P] can run in parallel
- All models/interfaces for a user story marked [P] can run in parallel
- Polish tasks marked [P] can run in parallel (Phase 6)

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together:
Task T011: "RED: Write failing unit test for useMapPins hook"
Task T012: "RED: Write failing unit test for createPinIcon helper"
Task T013: "RED: Write failing component test for MapPinLayer"
Task T014: "Create Gherkin feature file for US1"
Task T015: "Create step definitions for US1"
Task T016: "Add Page Object methods for US1"

# Launch all model/interface creation together:
Task T017: "Create PetPin TypeScript interface"
Task T018: "Create MapPinsState TypeScript interface"

# Then implement in sequence (TDD: Red-Green-Refactor):
Task T019: "GREEN: Implement createPinIcon (pass test T012)"
Task T020: "REFACTOR: Improve createPinIcon quality"
Task T021: "GREEN: Implement useMapPins (pass test T011)"
Task T022: "REFACTOR: Improve useMapPins quality"
Task T023: "GREEN: Implement MapPinLayer (pass test T013)"
Task T024: "REFACTOR: Improve MapPinLayer quality"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready - pins display on map! 🎉

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP! Pins on map)
3. Add User Story 2 → Test independently → Deploy/Demo (Clickable pins with pop-ups)
4. Add User Story 3 → Test independently → Deploy/Demo (Production-ready with error handling)
5. Each story adds value without breaking previous stories

### Single Developer Strategy

1. Complete Setup (Phase 1) - ~30 minutes
2. Complete Foundational (Phase 2) - ~1 hour
3. Complete User Story 1 (Phase 3) - ~4-6 hours
   - Write tests first (TDD)
   - Implement in small increments
   - Verify 80% coverage
4. Complete User Story 2 (Phase 4) - ~2-3 hours
   - Extend existing components
   - Add pop-up functionality
5. Complete User Story 3 (Phase 5) - ~2-3 hours
   - Add loading/error states
   - Implement retry mechanism
6. Polish (Phase 6) - ~1-2 hours

**Total Estimated Time**: 10-15 hours for complete feature

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently testable (with mocking if needed)
- TDD workflow is MANDATORY: Red (failing test) → Green (minimal implementation) → Refactor (improve quality)
- Verify tests fail before implementing (Red phase)
- 80% coverage threshold is MANDATORY per constitution
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, breaking changes to existing landing page map
- This is a web-only feature - no Android, iOS, or backend changes required

