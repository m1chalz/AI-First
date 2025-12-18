# Tasks: Web Map Component on Landing Page

**Input**: Design documents from `/specs/063-web-map-view/`  
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/  
**Branch**: `063-web-map-view`

**Tests**: This is a **web-only feature** affecting only `/webApp`. Test requirements:

**MANDATORY - Web Unit Tests** (TDD: Red-Green-Refactor):
- Location: `/webApp/src/hooks/__test__/`, `/webApp/src/components/map/__tests__/`
- Framework: Vitest + React Testing Library
- Coverage: 80% line + branch coverage
- Scope: Business logic in hooks (use-map-state), component rendering
- TDD Workflow: Write failing test → minimal implementation → refactor
- Convention: MUST follow Given-When-Then structure with descriptive test names
- Run: `npm test --coverage` (from webApp/)

**MANDATORY - End-to-End Tests**:
- Location: `/e2e-tests/web/specs/map-view.spec.ts`
- Framework: Playwright + TypeScript
- Scope: User Story 1 (map display) and User Story 2 (permission gating)
- Convention: MUST structure scenarios with Given-When-Then phases
- Run: `npx playwright test specs/map-view.spec.ts` (from e2e-tests/web/)

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Project Initialization)

**Purpose**: Install dependencies and create basic project structure

- [ ] T001 [P] Install leaflet package in `/webApp/package.json` (`npm install leaflet react-leaflet`)
- [ ] T002 [P] Install TypeScript definitions in `/webApp/package.json` (`npm install -D @types/leaflet`)
- [ ] T003 Verify installation with `npm list leaflet react-leaflet` (from webApp/)
- [ ] T004 Create directory structure `/webApp/src/components/map/` with subdirectory `__tests__/`
- [ ] T005 Create directory structure `/webApp/src/hooks/` with subdirectory `__test__/`
- [ ] T006 Create directory `/webApp/src/types/` for TypeScript interfaces
- [ ] T007 Create directory `/e2e-tests/web/specs/` for E2E tests

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: TypeScript interfaces and constants that MUST be complete before ANY user story implementation

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T008 Copy TypeScript interfaces from `/specs/063-web-map-view/contracts/map-component.interface.ts` to `/webApp/src/types/map.ts`
- [ ] T009 Verify TypeScript compilation with `npm run build` (from webApp/)

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - See Map on Landing Page (Priority: P1) 🎯 MVP

**Goal**: Display an interactive map on the landing page between the Description panel and Recently Lost Pets panel. Map centers on user's location (when permission granted) with ~10 km radius viewport (zoom level 13). Users can zoom and pan the map.

**Independent Test**: Can be tested by opening the landing page with location permission granted and verifying that the map is positioned correctly, centers on the user, and supports zooming/panning.

### Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation (TDD Red phase)**

**Web Unit Tests** (TDD: Red-Green-Refactor):
- [ ] T010 [P] [US1] RED: Write failing unit test for `useMapState` hook when permission granted in `/webApp/src/hooks/__test__/use-map-state.test.ts` (Vitest, Given-When-Then: test center = current location, zoom = 13)
- [ ] T011 [P] [US1] RED: Write failing unit test for `useMapState` hook when location unavailable in `/webApp/src/hooks/__test__/use-map-state.test.ts` (Vitest, Given-When-Then: test center = fallback location, error.showFallbackMap = true)
- [ ] T012 [P] [US1] RED: Write failing unit test for `useMapState` hook when map load fails in `/webApp/src/hooks/__test__/use-map-state.test.ts` (Vitest, Given-When-Then: test error.type = MAP_LOAD_FAILED)

**Web Component Tests** (TDD: Red-Green-Refactor):
- [ ] T013 [P] [US1] RED: Write failing component test for `MapView` rendering MapContainer in `/webApp/src/components/map/__tests__/MapView.test.tsx` (Vitest + RTL, Given-When-Then: test MapContainer renders with correct center and zoom)
- [ ] T014 [P] [US1] RED: Write failing component test for `MapView` displaying loading state in `/webApp/src/components/map/__tests__/MapView.test.tsx` (Vitest + RTL, Given-When-Then: test loading message appears when isLoading = true)
- [ ] T015 [P] [US1] RED: Write failing component test for `MapErrorState` in `/webApp/src/components/map/__tests__/MapErrorState.test.tsx` (Vitest + RTL, Given-When-Then: test error message displays correctly)

**End-to-End Tests**:
- [ ] T016 [P] [US1] Write E2E test for map display between Description and Recently Lost Pets in `/e2e-tests/web/specs/map-view.spec.ts` (Playwright, Given-When-Then: grant permission → navigate to landing page → verify map visible with data-testid="landingPage.map")
- [ ] T017 [P] [US1] Write E2E test for map centered on user location in `/e2e-tests/web/specs/map-view.spec.ts` (Playwright, Given-When-Then: grant permission + set geolocation → navigate → verify map center matches user location)
- [ ] T018 [P] [US1] Write E2E test for zoom and pan interactions in `/e2e-tests/web/specs/map-view.spec.ts` (Playwright, Given-When-Then: interact with zoom controls → verify map zoom changes)

### Implementation for User Story 1

> **TDD Workflow**: Implement minimal code to pass failing tests (GREEN phase), then refactor (REFACTOR phase)

**Web Implementation** (TDD: Red-Green-Refactor):

- [ ] T019 [P] [US1] GREEN: Implement `useMapState` hook in `/webApp/src/hooks/use-map-state.ts` (minimal code to pass tests T010-T012: integrate GeolocationContext, determine center, handle error states)
- [ ] T020 [US1] REFACTOR: Improve `useMapState` code quality (extract helper functions like `determineError`, apply Clean Code principles: max 3 nesting levels, descriptive names)
- [ ] T021 [P] [US1] GREEN: Implement `MapErrorState` component in `/webApp/src/components/map/MapErrorState.tsx` (minimal code to pass test T015: display error.message, add data-testid)
- [ ] T022 [P] [US1] Create CSS module for `MapErrorState` in `/webApp/src/components/map/MapErrorState.module.css` (basic styling: centered container, error icon/message)
- [ ] T023 [US1] GREEN: Implement `MapView` component in `/webApp/src/components/map/MapView.tsx` (minimal code to pass tests T013-T014: render MapContainer with center/zoom from useMapState, show loading state, import Leaflet CSS)
- [ ] T024 [US1] Create CSS module for `MapView` in `/webApp/src/components/map/MapView.module.css` (styling: 400px height, container layout, error banner)
- [ ] T025 [US1] REFACTOR: Add conditional rendering for error states in `MapView.tsx` (if error && !showFallbackMap → MapErrorState, if error && showFallbackMap → error banner + map)
- [ ] T026 [US1] Add `TileLayer` component to `MapView.tsx` with OpenStreetMap URL and attribution (url from MAP_CONFIG.TILE_LAYER_URL, attribution from MAP_CONFIG.ATTRIBUTION)
- [ ] T027 [US1] Add `data-testid` attributes to all interactive elements in `MapView.tsx` (map container: "landingPage.map", loading: "landingPage.map.loading", error banner: "landingPage.map.errorBanner")
- [ ] T028 [P] [US1] Add JSDoc documentation ONLY to complex functions in `use-map-state.ts` (document `useMapState` hook, skip self-explanatory helpers)
- [ ] T029 [US1] Integrate `MapView` component into landing page in `/webApp/src/pages/Home.tsx` (import MapView, insert between HeroSection and RecentPetsSection)
- [ ] T030 [US1] Run `npm test -- src/hooks/__test__/use-map-state.test.ts` and verify all hook tests pass (GREEN phase complete)
- [ ] T031 [US1] Run `npm test -- src/components/map/__tests__/` and verify all component tests pass (GREEN phase complete)
- [ ] T032 [US1] Run `npm test --coverage` and verify 80% coverage for `/src/hooks/use-map-state.ts` and `/src/components/map/`
- [ ] T033 [P] [US1] Run `npm run lint` and fix ESLint violations in map-related files

**Web E2E Validation**:
- [ ] T034 [US1] Run E2E tests for User Story 1 with `npx playwright test specs/map-view.spec.ts` (from e2e-tests/web/, verify tests T016-T018 pass)

**Checkpoint**: At this point, User Story 1 should be fully functional - map displays on landing page, centers on user location (when granted), supports zoom/pan interactions

---

## Phase 4: User Story 2 - Grant Location Permission (Priority: P2)

**Goal**: Users who have not granted location permission see an informational message explaining why location is required, with a button to grant permission. Clicking the button triggers the browser permission prompt.

**Independent Test**: Can be tested by loading the map view with location permission denied/blocked and verifying an informational state with a clear action to grant permission is shown.

### Tests for User Story 2 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation (TDD Red phase)**

**Web Unit Tests** (TDD: Red-Green-Refactor):
- [ ] T035 [P] [US2] RED: Write failing unit test for `useMapState` hook when permission denied in `/webApp/src/hooks/__test__/use-map-state.test.ts` (Vitest, Given-When-Then: test showPermissionPrompt = true, error.type = PERMISSION_DENIED)
- [ ] T036 [P] [US2] RED: Write failing unit test for `useMapState` hook when permission not requested in `/webApp/src/hooks/__test__/use-map-state.test.ts` (Vitest, Given-When-Then: test showPermissionPrompt = true, error = null)
- [ ] T037 [P] [US2] RED: Write failing unit test for `handleRequestPermission` in `/webApp/src/hooks/__test__/use-map-state.test.ts` (Vitest, Given-When-Then: test geolocation.requestPermission is called)

**Web Component Tests** (TDD: Red-Green-Refactor):
- [ ] T038 [P] [US2] RED: Write failing component test for `MapPermissionPrompt` rendering in `/webApp/src/components/map/__tests__/MapPermissionPrompt.test.tsx` (Vitest + RTL, Given-When-Then: test "Location Access Required" message renders)
- [ ] T039 [P] [US2] RED: Write failing component test for `MapPermissionPrompt` button click in `/webApp/src/components/map/__tests__/MapPermissionPrompt.test.tsx` (Vitest + RTL, Given-When-Then: test onRequestPermission callback called on button click)
- [ ] T040 [P] [US2] RED: Write failing component test for `MapView` showing permission prompt in `/webApp/src/components/map/__tests__/MapView.test.tsx` (Vitest + RTL, Given-When-Then: test MapPermissionPrompt renders when showPermissionPrompt = true)

**End-to-End Tests**:
- [ ] T041 [P] [US2] Write E2E test for permission prompt display in `/e2e-tests/web/specs/map-view.spec.ts` (Playwright, Given-When-Then: deny permission → navigate → verify permission prompt visible with data-testid="landingPage.map.permissionPrompt")
- [ ] T042 [P] [US2] Write E2E test for consent button triggering browser dialog in `/e2e-tests/web/specs/map-view.spec.ts` (Playwright, Given-When-Then: click consent button → verify browser permission dialog triggered)
- [ ] T043 [P] [US2] Write E2E test for map display after granting permission in `/e2e-tests/web/specs/map-view.spec.ts` (Playwright, Given-When-Then: grant permission after initially denying → navigate → verify map displays without page refresh)

### Implementation for User Story 2

> **TDD Workflow**: Implement minimal code to pass failing tests (GREEN phase), then refactor (REFACTOR phase)

**Web Implementation** (TDD: Red-Green-Refactor):

- [ ] T044 [P] [US2] GREEN: Extend `useMapState` hook in `/webApp/src/hooks/use-map-state.ts` to handle permission states (minimal code to pass tests T035-T037: check permissionCheckCompleted, set showPermissionPrompt, implement handleRequestPermission)
- [ ] T045 [US2] REFACTOR: Improve permission handling logic in `useMapState.ts` (extract helper function for permission state determination, ensure max 3 nesting levels)
- [ ] T046 [P] [US2] GREEN: Implement `MapPermissionPrompt` component in `/webApp/src/components/map/MapPermissionPrompt.tsx` (minimal code to pass tests T038-T039: display message, render consent button, attach onClick handler)
- [ ] T047 [P] [US2] Create CSS module for `MapPermissionPrompt` in `/webApp/src/components/map/MapPermissionPrompt.module.css` (styling: centered layout, title, message, prominent button)
- [ ] T048 [US2] GREEN: Update `MapView` component in `/webApp/src/components/map/MapView.tsx` to conditionally render `MapPermissionPrompt` (minimal code to pass test T040: if showPermissionPrompt → render MapPermissionPrompt, else → render map)
- [ ] T049 [US2] Add `data-testid` attributes to `MapPermissionPrompt` elements (container: "landingPage.map.permissionPrompt", button: "landingPage.map.consentButton")
- [ ] T050 [P] [US2] Add JSDoc documentation to `MapPermissionPrompt` component (document component purpose: displays informational message when permission not granted)
- [ ] T051 [US2] Run `npm test -- src/hooks/__test__/use-map-state.test.ts` and verify all permission-related hook tests pass (GREEN phase complete)
- [ ] T052 [US2] Run `npm test -- src/components/map/__tests__/MapPermissionPrompt.test.tsx` and verify all component tests pass (GREEN phase complete)
- [ ] T053 [US2] Run `npm test -- src/components/map/__tests__/MapView.test.tsx` and verify updated MapView tests pass (GREEN phase complete)
- [ ] T054 [US2] Run `npm test --coverage` and verify 80% coverage maintained for all map-related files
- [ ] T055 [P] [US2] Run `npm run lint` and fix ESLint violations in updated files

**Web E2E Validation**:
- [ ] T056 [US2] Run E2E tests for User Story 2 with `npx playwright test specs/map-view.spec.ts` (from e2e-tests/web/, verify tests T041-T043 pass)

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently - map displays when permission granted, permission prompt displays when permission not granted/denied

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Final improvements affecting both user stories

- [ ] T057 [P] Run full test suite with `npm test` (from webApp/) and verify all tests pass
- [ ] T058 [P] Run full E2E test suite with `npx playwright test` (from e2e-tests/web/) and verify all scenarios pass
- [ ] T059 Verify test coverage report with `npm test --coverage` (from webApp/, view at webApp/coverage/index.html, ensure ≥80% for hooks and components)
- [ ] T060 [P] Manual QA testing in Chrome (verify map display, permission flow, zoom/pan, error states)
- [ ] T061 [P] Manual QA testing in Firefox (verify map display, permission flow, zoom/pan, error states)
- [ ] T062 [P] Manual QA testing in Safari (verify map display, permission flow, zoom/pan, error states)
- [ ] T063 Code cleanup: Remove any console.log statements, unused imports, commented-out code
- [ ] T064 [P] Verify Leaflet CSS is imported in `MapView.tsx` (`import 'leaflet/dist/leaflet.css'`)
- [ ] T065 Verify OSM attribution displays correctly on rendered map (check browser DevTools for attribution element)
- [ ] T066 Final linting pass: Run `npm run lint` (from webApp/) and ensure no violations
- [ ] T067 Update feature branch: Commit all changes with message "feat: add interactive map component to landing page (063-web-map-view)"

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-4)**: Both depend on Foundational phase completion
  - User Story 1 and User Story 2 can proceed in parallel (if staffed)
  - Or sequentially in priority order (US1 → US2)
- **Polish (Phase 5)**: Depends on both User Story 1 and User Story 2 completion

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Integrates with US1 (MapView component) but independently testable

### Within Each User Story

- Tests MUST be written and FAIL before implementation (TDD Red phase)
- Hooks before components (useMapState → MapView, MapPermissionPrompt)
- Components before integration (MapView, MapPermissionPrompt → Home.tsx)
- Unit tests before E2E tests
- Implementation before validation (GREEN phase → REFACTOR phase → E2E validation)
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel (T001, T002)
- All Red phase tests for a user story marked [P] can be written in parallel
- All Green phase implementations for independent components marked [P] can run in parallel
- Component tests and E2E tests can be written in parallel (different files)
- User Story 1 and User Story 2 can be worked on in parallel by different team members (after Foundational phase completes)

---

## Parallel Example: User Story 1

```bash
# RED Phase: Launch all failing tests for User Story 1 together
Task T010: "Write failing unit test for useMapState when permission granted"
Task T011: "Write failing unit test for useMapState when location unavailable"
Task T012: "Write failing unit test for useMapState when map load fails"
Task T013: "Write failing component test for MapView rendering MapContainer"
Task T014: "Write failing component test for MapView loading state"
Task T015: "Write failing component test for MapErrorState"

# GREEN Phase: Implement minimal code to pass tests
Task T019: "Implement useMapState hook (minimal)"
Task T021: "Implement MapErrorState component (minimal)"
Task T023: "Implement MapView component (minimal)"

# REFACTOR Phase: Improve code quality
Task T020: "Refactor useMapState (extract helpers, Clean Code)"
Task T025: "Refactor MapView (conditional rendering)"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1 (map display with location)
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Run E2E tests, manual QA
6. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1 (map display with location)
   - Developer B: User Story 2 (permission gating)
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies, safe to parallelize
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- TDD workflow: RED (failing test) → GREEN (minimal implementation) → REFACTOR (improve code quality)
- Verify tests fail before implementing (RED phase critical)
- Verify 80% coverage target after each user story
- Commit after each logical group of tasks
- Stop at any checkpoint to validate story independently
- This is a **web-only feature** - no Android/iOS/Backend changes required

