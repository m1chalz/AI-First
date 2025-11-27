# Tasks: Web Pet Details Screen

**Input**: Design documents from `/specs/027-web-pet-details-screen/`
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, contracts/ ✅

**Tests**: Test requirements for this project:

**MANDATORY - Platform-Specific Unit Tests** (Web):
- Web: `/webApp/src/__tests__/` (Vitest + React Testing Library), 80% coverage
  - Scope: Domain models, services, custom hooks, components
  - Run: `npm test -- --coverage` (from webApp/)
  - Convention: MUST follow Given-When-Then structure with descriptive names

**MANDATORY - End-to-End Tests**:
- Web: `/e2e-tests/web/specs/pet-details-modal.spec.ts` (Playwright + TypeScript)
  - All user stories MUST have E2E test coverage
  - Use Page Object Model pattern
  - Convention: MUST structure scenarios with Given-When-Then phases

**Organization**: Tasks are organized by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Create project structure per implementation plan in `/webApp/src/`
- [ ] T002 [P] Verify React 18.2.0+ and TypeScript 5.0+ dependencies in `/webApp/package.json`
- [ ] T003 [P] Verify Vitest and React Testing Library configured in `/webApp/vite.config.ts`
- [ ] T004 [P] Verify CSS Modules support configured in `/webApp/vite.config.ts`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T005 Update `Location` interface in `/webApp/src/types/animal.ts` from `{city: string, radiusKm: number}` to `{latitude?: number, longitude?: number}`
- [ ] T006 [P] Create `PetDetails` type definition in `/webApp/src/types/pet-details.ts` with `PetStatus` and `PetSex` enums
- [ ] T007 [P] Update `AnimalRepository.getAnimals()` in `/webApp/src/services/animal-repository.ts` to call `GET /api/v1/announcements` instead of mock data
- [ ] T008 [P] Map backend response format to `Animal` type in `/webApp/src/services/animal-repository.ts` (status: MISSING→ACTIVE, FOUND→FOUND, CLOSED→CLOSED; map locationLatitude/locationLongitude to location object)
- [ ] T009 Add `getPetById(id: string): Promise<PetDetails>` method in `/webApp/src/services/animal-repository.ts` calling `GET /api/v1/announcements/:id`
- [ ] T010 [P] Create date formatter utility in `/webApp/src/utils/date-formatter.ts` (format ISO date to "MMM DD, YYYY")
- [ ] T011 [P] Create coordinate formatter utility in `/webApp/src/utils/coordinate-formatter.ts` (format lat/lng to "XX.XXXX° N/S, XX.XXXX° E/W")
- [ ] T012 [P] Create microchip formatter utility in `/webApp/src/utils/microchip-formatter.ts` (format to "000-000-000-000")
- [ ] T013 [P] Create map URL builder utility in `/webApp/src/utils/map-url-builder.ts` (build Google Maps/OpenStreetMap URLs)

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Open Pet Details Modal from List (Priority: P1) 🎯 MVP

**Goal**: Users can click the "Details" button on any animal card in the list to view comprehensive details about that pet in a modal overlay.

**Independent Test**: Can be fully tested by clicking the "Details" button on a mock pet list card and verifying the modal opens with correct pet information.

### Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

**Web Unit Tests**:
- [ ] T014 [P] [US1] Unit test for `useModal` hook in `/webApp/src/__tests__/hooks/use-modal.test.ts` (test open/close state, selectedPetId)
- [ ] T015 [P] [US1] Unit test for `usePetDetails` hook in `/webApp/src/__tests__/hooks/use-pet-details.test.ts` (test loading, success, error states, retry)
- [ ] T016 [P] [US1] Unit test for `AnimalRepository.getPetById` in `/webApp/src/__tests__/services/animal-repository.test.ts` (test API call, error handling, timeout)
- [ ] T017 [P] [US1] Unit test for `PetDetailsModal` component in `/webApp/src/__tests__/components/PetDetailsModal.test.tsx` (test modal opens/closes, displays content, handles ESC/backdrop click)

**End-to-End Tests**:
- [ ] T018 [P] [US1] Web E2E test in `/e2e-tests/web/specs/pet-details-modal.spec.ts` (test clicking "Details" button opens modal, modal displays pet info, modal closes via X/ESC/backdrop)
- [ ] T019 [P] [US1] Page Object for PetDetailsModal in `/e2e-tests/web/pages/PetDetailsModalPage.ts` (methods: openModal, closeModal, getPetInfo, waitForModalOpen/Close)

### Implementation for User Story 1

**Web** (Full Stack Implementation):
- [ ] T020 [P] [US1] Create `useModal` hook in `/webApp/src/hooks/use-modal.ts` (manages `isOpen` and `selectedPetId` state)
- [ ] T021 [P] [US1] Create `usePetDetails` hook in `/webApp/src/hooks/use-pet-details.ts` (fetches pet details by ID, handles loading/error/retry states, 10-second timeout)
- [ ] T022 [US1] Create `PetDetailsModal` component in `/webApp/src/components/PetDetailsModal/PetDetailsModal.tsx` (main modal component using React Portal, handles open/close, focus trap, body scroll lock)
- [ ] T023 [US1] Create `PetDetailsModal.module.css` in `/webApp/src/components/PetDetailsModal/PetDetailsModal.module.css` (backdrop, modal container, close button styles)
- [ ] T024 [US1] Create `PetDetailsContent` component in `/webApp/src/components/PetDetailsModal/PetDetailsContent.tsx` (stateless content component displaying pet details)
- [ ] T025 [US1] Create `PetDetailsContent.module.css` in `/webApp/src/components/PetDetailsModal/PetDetailsContent.module.css` (content layout styles)
- [ ] T026 [US1] Update `AnimalList` component in `/webApp/src/components/AnimalList/AnimalList.tsx` to manage modal state (use `useModal` hook, pass `selectedPetId` and `isOpen` to `PetDetailsModal`)
- [ ] T027 [US1] Update `AnimalCard` component in `/webApp/src/components/AnimalList/AnimalCard.tsx` to add "Details" button with `data-testid="animalList.card.detailsButton.click"` that calls `onDetailsClick(animal.id)` callback
- [ ] T028 [US1] Implement focus trap in `PetDetailsModal` (trap focus within modal when open, return focus to "Details" button on close)
- [ ] T029 [US1] Implement body scroll lock in `PetDetailsModal` (prevent background scrolling when modal is open)
- [ ] T030 [US1] Add ESC key handler in `PetDetailsModal` (close modal on ESC key press)
- [ ] T031 [US1] Add backdrop click handler in `PetDetailsModal` (close modal on backdrop click)
- [ ] T032 [US1] Add close button (X) in `PetDetailsModal` header with `data-testid="petDetails.closeButton.click"`
- [ ] T033 [US1] Add loading spinner in `PetDetailsModal` (display while fetching pet details)
- [ ] T034 [US1] Add error state in `PetDetailsModal` (display "Failed to load pet details" message with "Retry" button on API error/timeout)
- [ ] T035 [P] [US1] Add JSDoc documentation to complex Web APIs in hooks and components (skip self-explanatory functions)

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - Review Pet Identification Information (Priority: P2)

**Goal**: Users can view critical identification information including microchip number, species, breed, sex, and approximate age to verify if a pet matches one they've found or lost.

**Independent Test**: Can be tested by loading the details page with various identification data combinations and verifying each field displays with proper labels and formatting.

### Tests for User Story 2 (MANDATORY) ✅

**Web Unit Tests**:
- [ ] T036 [P] [US2] Unit test for microchip formatter in `/webApp/src/__tests__/utils/microchip-formatter.test.ts` (test formatting to "000-000-000-000")
- [ ] T037 [P] [US2] Unit test for `PetDetailsContent` component displaying identification fields in `/webApp/src/__tests__/components/PetDetailsContent.test.tsx` (test microchip, species, breed, sex, age display)

**End-to-End Tests**:
- [ ] T038 [P] [US2] Web E2E test in `/e2e-tests/web/specs/pet-details-modal.spec.ts` (test identification fields display correctly with formatting)

### Implementation for User Story 2

**Web** (Full Stack):
- [ ] T039 [P] [US2] Update `PetDetailsContent` component in `/webApp/src/components/PetDetailsModal/PetDetailsContent.tsx` to display microchip number (formatted using `formatMicrochip` utility, show "—" if null)
- [ ] T040 [US2] Update `PetDetailsContent` component to display species and breed side-by-side in two-column layout (labeled "Animal Species" and "Animal Race")
- [ ] T041 [US2] Update `PetDetailsContent` component to display sex with appropriate icon (male/female symbol)
- [ ] T042 [US2] Update `PetDetailsContent` component to display age (show "—" if null)
- [ ] T043 [US2] Update `PetDetailsContent.module.css` to add styles for identification fields section (two-column layout for species/breed)
- [ ] T044 [P] [US2] Add JSDoc documentation to complex US2 Web APIs (skip self-explanatory)

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - Access Location and Contact Information (Priority: P2)

**Goal**: Users can view where the pet was last seen or found, including precise geographic coordinates (latitude and longitude), along with contact information for reaching the pet owner.

**Independent Test**: Can be tested by displaying location data with various coordinate values, and verifying contact information is properly displayed.

### Tests for User Story 3 (MANDATORY) ✅

**Web Unit Tests**:
- [ ] T045 [P] [US3] Unit test for coordinate formatter in `/webApp/src/__tests__/utils/coordinate-formatter.test.ts` (test formatting to "XX.XXXX° N/S, XX.XXXX° E/W")
- [ ] T046 [P] [US3] Unit test for map URL builder in `/webApp/src/__tests__/utils/map-url-builder.test.ts` (test Google Maps/OpenStreetMap URL generation)
- [ ] T047 [P] [US3] Unit test for `PetDetailsContent` displaying location and contact info in `/webApp/src/__tests__/components/PetDetailsContent.test.tsx` (test coordinates display, map button, phone/email display)

**End-to-End Tests**:
- [ ] T048 [P] [US3] Web E2E test in `/e2e-tests/web/specs/pet-details-modal.spec.ts` (test location coordinates display, "Show on the map" button opens external map, contact info displays)

### Implementation for User Story 3

**Web** (Full Stack):
- [ ] T049 [P] [US3] Create `PetDetailsHeader` component in `/webApp/src/components/PetDetailsModal/PetDetailsHeader.tsx` (displays date, phone, email in header row)
- [ ] T050 [P] [US3] Create `PetDetailsHeader.module.css` in `/webApp/src/components/PetDetailsModal/PetDetailsHeader.module.css` (header layout styles)
- [ ] T051 [US3] Update `PetDetailsContent` component to display location coordinates (formatted using `formatCoordinates` utility, show location icon, hide section if coordinates unavailable)
- [ ] T052 [US3] Update `PetDetailsContent` component to add "Show on the map" button next to coordinates (opens external map URL in new tab using `buildMapUrl` utility, disabled if coordinates unavailable)
- [ ] T053 [US3] Update `PetDetailsHeader` component to display phone number exactly as received from API (with phone icon, show "—" if null)
- [ ] T054 [US3] Update `PetDetailsHeader` component to display email in full (with email icon, show "—" if null)
- [ ] T055 [US3] Update `PetDetailsContent.module.css` to add styles for location section and map button
- [ ] T056 [P] [US3] Add JSDoc documentation to complex US3 Web APIs (skip self-explanatory)

**Checkpoint**: At this point, User Stories 1, 2, AND 3 should all work independently

---

## Phase 6: User Story 4 - Review Additional Pet Details (Priority: P3)

**Goal**: Users can read additional descriptive information about the pet, including physical description and behavior traits that help identify the animal.

**Independent Test**: Can be tested by loading pets with various text descriptions and verifying multi-line text display and empty state handling.

### Tests for User Story 4 (MANDATORY) ✅

**Web Unit Tests**:
- [ ] T057 [P] [US4] Unit test for `PetDetailsContent` displaying description and special features in `/webApp/src/__tests__/components/PetDetailsContent.test.tsx` (test multi-line description, special features display, empty state "—")

**End-to-End Tests**:
- [ ] T058 [P] [US4] Web E2E test in `/e2e-tests/web/specs/pet-details-modal.spec.ts` (test description and special features display correctly)

### Implementation for User Story 4

**Web** (Full Stack):
- [ ] T059 [P] [US4] Update `PetDetailsContent` component to display additional description under "Animal Additional Description" (full multi-line text, allow scrolling)
- [ ] T060 [US4] Update `PetDetailsContent` component to display special features (show "—" if null/empty)
- [ ] T061 [US4] Update `PetDetailsContent.module.css` to add styles for description section (multi-line, scrollable)
- [ ] T062 [P] [US4] Add JSDoc documentation to complex US4 Web APIs (skip self-explanatory)

**Checkpoint**: At this point, User Stories 1, 2, 3, AND 4 should all work independently

---

## Phase 7: User Story 5 - View Reward Information (Priority: P3)

**Goal**: Users can see if a reward is offered for finding the pet, displayed prominently on the pet's photo.

**Independent Test**: Can be tested by displaying pets with and without rewards and verifying the reward badge appears correctly when present.

### Tests for User Story 5 (MANDATORY) ✅

**Web Unit Tests**:
- [ ] T063 [P] [US5] Unit test for reward badge display in `/webApp/src/__tests__/components/PetDetailsContent.test.tsx` (test badge appears when reward present, hidden when null)

**End-to-End Tests**:
- [ ] T064 [P] [US5] Web E2E test in `/e2e-tests/web/specs/pet-details-modal.spec.ts` (test reward badge displays correctly)

### Implementation for User Story 5

**Web** (Full Stack):
- [ ] T065 [P] [US5] Create `PetHeroImage` component in `/webApp/src/components/PetDetailsModal/PetHeroImage.tsx` (displays pet photo with status badge and reward badge overlay)
- [ ] T066 [P] [US5] Create `PetHeroImage.module.css` in `/webApp/src/components/PetDetailsModal/PetHeroImage.module.css` (hero image styles, badge positioning)
- [ ] T067 [US5] Update `PetHeroImage` component to display reward badge on left side of photo (show reward text as-is with money bag icon, hide if reward is null)
- [ ] T068 [US5] Update `PetHeroImage` component to handle image load failure (display gray box with "Image not available" text)
- [ ] T069 [P] [US5] Add JSDoc documentation to complex US5 Web APIs (skip self-explanatory)

**Checkpoint**: At this point, User Stories 1, 2, 3, 4, AND 5 should all work independently

---

## Phase 8: User Story 6 - Identify Pet Status Visually (Priority: P2)

**Goal**: Users can immediately identify the pet's status (MISSING, FOUND, or CLOSED) through a prominent status badge displayed on the pet photo.

**Independent Test**: Can be tested by displaying pets with all three status values (MISSING, FOUND, CLOSED) and verifying the correct badge color and text appear.

### Tests for User Story 6 (MANDATORY) ✅

**Web Unit Tests**:
- [ ] T070 [P] [US6] Unit test for status badge display in `/webApp/src/__tests__/components/PetHeroImage.test.tsx` (test red badge for MISSING, blue for FOUND, gray for CLOSED)

**End-to-End Tests**:
- [ ] T071 [P] [US6] Web E2E test in `/e2e-tests/web/specs/pet-details-modal.spec.ts` (test status badge displays correctly for all statuses)

### Implementation for User Story 6

**Web** (Full Stack):
- [ ] T072 [P] [US6] Update `PetHeroImage` component to display status badge in upper right corner of photo (red for MISSING, blue for FOUND, gray for CLOSED, white text)
- [ ] T073 [US6] Update `PetHeroImage.module.css` to add status badge styles (positioning, colors: #FF0000 for MISSING, #155DFC for FOUND, gray for CLOSED)
- [ ] T074 [P] [US6] Add JSDoc documentation to complex US6 Web APIs (skip self-explanatory)

**Checkpoint**: At this point, User Stories 1, 2, 3, 4, 5, AND 6 should all work independently

---

## Phase 9: User Story 7 - Responsive Modal Adaptation (Priority: P1)

**Goal**: Users can access the pet details modal on any device (mobile, tablet, desktop) and experience an optimized layout for their screen size.

**Independent Test**: Can be tested by opening the modal at different viewport sizes (320px, 768px, 1024px, 1440px) and verifying layout adapts appropriately.

### Tests for User Story 7 (MANDATORY) ✅

**Web Unit Tests**:
- [ ] T075 [P] [US7] Unit test for responsive modal layout in `/webApp/src/__tests__/components/PetDetailsModal.test.tsx` (test mobile full-screen, tablet/desktop centered dialog)

**End-to-End Tests**:
- [ ] T076 [P] [US7] Web E2E test in `/e2e-tests/web/specs/pet-details-modal.spec.ts` (test modal responsive behavior at different viewport sizes)

### Implementation for User Story 7

**Web** (Full Stack):
- [ ] T077 [P] [US7] Update `PetDetailsModal.module.css` to add responsive styles (mobile 320px-767px: full-screen 100% width/height, tablet 768px-1023px: centered dialog max-width 640px, desktop 1024px+: centered dialog max-width 768px)
- [ ] T078 [US7] Update `PetDetailsContent.module.css` to add responsive content styles (adjust padding, font sizes, layout for mobile/tablet/desktop)
- [ ] T079 [US7] Update `PetHeroImage.module.css` to add responsive image styles (adjust image size for mobile/tablet/desktop)
- [ ] T080 [US7] Verify modal accessibility on mobile (touch targets minimum 44px, readable text sizes)
- [ ] T081 [P] [US7] Add JSDoc documentation to complex US7 Web APIs (skip self-explanatory)

**Checkpoint**: At this point, ALL user stories should be fully functional and responsive

---

## Phase 10: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T082 [P] Update `AnimalList` component to match Figma design (node-id=168-4656) - sidebar, header, animal cards with coordinates
- [ ] T083 [P] Update `AnimalCard` component to display location coordinates (latitude/longitude format) instead of city/radius
- [ ] T084 [P] Update `AnimalCard.module.css` to match Figma card design (1180px width, 136px height, 14px border-radius, layout with photo/location/species/description/status/date/Details button)
- [ ] T085 [P] Update `AnimalList.module.css` to match Figma page layout (sidebar 219px width #4F3C4C, content area 1181px width, header with PetSpot title and Report button)
- [ ] T086 [P] Add `data-testid` attributes to all interactive elements in `PetDetailsModal` and `PetDetailsContent` (follow naming: `petDetails.{element}.{action}`)
- [ ] T087 [P] Add `data-testid` attributes to all interactive elements in updated `AnimalCard` (follow naming: `animalList.card.{element}.{action}`)
- [ ] T088 [P] Verify Lighthouse accessibility score 90+ for modal and list page
- [ ] T089 [P] Run `npm test -- --coverage` and verify 80% coverage for all components, hooks, services, utils
- [ ] T090 [P] Run `npm run lint` and fix ESLint violations
- [ ] T091 [P] Run quickstart.md validation checklist
- [ ] T092 [P] Update documentation in `/webApp/README.md` if needed

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Depends on US1 (modal structure)
- **User Story 3 (P2)**: Can start after Foundational (Phase 2) - Depends on US1 (modal structure)
- **User Story 4 (P3)**: Can start after Foundational (Phase 2) - Depends on US1 (modal structure)
- **User Story 5 (P3)**: Can start after Foundational (Phase 2) - Depends on US1 (modal structure)
- **User Story 6 (P2)**: Can start after Foundational (Phase 2) - Depends on US1 (modal structure), can be parallel with US5
- **User Story 7 (P1)**: Can start after Foundational (Phase 2) - Depends on US1 (modal structure), affects all modal content

### Within Each User Story

- Tests (if included) MUST be written and FAIL before implementation
- Models/types before services/hooks
- Services/hooks before components
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, user stories can start in parallel (if team capacity allows)
- All tests for a user story marked [P] can run in parallel
- Utils within a story marked [P] can run in parallel
- Component updates for different user stories can be worked on in parallel by different team members

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together:
Task: "Unit test for useModal hook in /webApp/src/__tests__/hooks/use-modal.test.ts"
Task: "Unit test for usePetDetails hook in /webApp/src/__tests__/hooks/use-pet-details.test.ts"
Task: "Unit test for AnimalRepository.getPetById in /webApp/src/__tests__/services/animal-repository.test.ts"
Task: "Unit test for PetDetailsModal component in /webApp/src/__tests__/components/PetDetailsModal.test.tsx"

# Launch all implementation tasks for User Story 1 together:
Task: "Create useModal hook in /webApp/src/hooks/use-modal.ts"
Task: "Create usePetDetails hook in /webApp/src/hooks/use-pet-details.ts"
Task: "Create PetDetailsModal component in /webApp/src/components/PetDetailsModal/PetDetailsModal.tsx"
Task: "Create PetDetailsContent component in /webApp/src/components/PetDetailsModal/PetDetailsContent.tsx"
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
3. Add User Story 7 → Test independently → Deploy/Demo (Responsive)
4. Add User Story 2 → Test independently → Deploy/Demo
5. Add User Story 3 → Test independently → Deploy/Demo
6. Add User Story 6 → Test independently → Deploy/Demo
7. Add User Story 4 → Test independently → Deploy/Demo
8. Add User Story 5 → Test independently → Deploy/Demo
9. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1 (modal structure)
   - Developer B: User Story 2 (identification fields)
   - Developer C: User Story 3 (location/contact)
   - Developer D: User Story 7 (responsive styles)
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
- Backend API endpoint `GET /api/v1/announcements/:id` is already implemented - no backend tasks needed
- All formatting (date, coordinates, microchip) happens in components using utility functions
- Modal state managed via React useState (no URL parameters)
- Error handling uses generic message "Failed to load pet details" with unlimited retry
- External map integration opens Google Maps/OpenStreetMap in new browser tab

---

## Summary

**Total Tasks**: 92 tasks
- **Phase 1 (Setup)**: 4 tasks
- **Phase 2 (Foundational)**: 9 tasks
- **Phase 3 (US1 - MVP)**: 22 tasks (8 tests + 14 implementation)
- **Phase 4 (US2)**: 9 tasks (3 tests + 6 implementation)
- **Phase 5 (US3)**: 12 tasks (4 tests + 8 implementation)
- **Phase 6 (US4)**: 6 tasks (2 tests + 4 implementation)
- **Phase 7 (US5)**: 6 tasks (2 tests + 4 implementation)
- **Phase 8 (US6)**: 5 tasks (2 tests + 3 implementation)
- **Phase 9 (US7)**: 7 tasks (2 tests + 5 implementation)
- **Phase 10 (Polish)**: 11 tasks

**Task Count per User Story**:
- US1: 22 tasks (MVP)
- US2: 9 tasks
- US3: 12 tasks
- US4: 6 tasks
- US5: 6 tasks
- US6: 5 tasks
- US7: 7 tasks

**Parallel Opportunities Identified**: 
- All foundational tasks (T010-T013) can run in parallel
- All test tasks within each user story can run in parallel
- Utils creation (T010-T013) can run in parallel
- Component updates for different stories can run in parallel

**Independent Test Criteria**:
- US1: Click "Details" button → modal opens with pet info
- US2: Identification fields display with proper formatting
- US3: Location coordinates and contact info display correctly
- US4: Description and special features display correctly
- US5: Reward badge appears when reward present
- US6: Status badge displays correct color for each status
- US7: Modal layout adapts to viewport size

**Suggested MVP Scope**: User Story 1 only (Open Pet Details Modal from List) - 22 tasks total

