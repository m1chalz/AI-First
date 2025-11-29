# Implementation Tasks: Web Browser Location for Pet Listings

**Feature**: 032-web-location-query  
**Branch**: `032-web-location-query`  
**Generated**: 2025-11-29

## Overview

This document provides atomic, test-driven tasks for implementing browser-based geolocation in the web pet listings application. Each task follows TDD workflow: **write test → implement → verify test passes → lint**.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing. Each user story phase delivers a complete, testable increment.

---

## Task Summary

| Phase | User Story | Task Count | Can Run in Parallel |
|-------|-----------|------------|---------------------|
| Phase 1: Setup | N/A | 3 tasks | No (sequential) |
| Phase 2: Foundational | N/A | 6 tasks | Yes (4 parallel) |
| Phase 3: User Story 1 | Location-Aware Content (P1) | 8 tasks | Yes (5 parallel) |
| Phase 4: User Story 2 | First-Time Permission (P2) | 4 tasks | Yes (2 parallel) |
| Phase 5: User Story 3 | Blocked Permission Banner (P3) | 6 tasks | Yes (4 parallel) |
| Phase 6: Polish | N/A | 3 tasks | Yes (2 parallel) |
| **Total** | **3 user stories** | **30 tasks** | **17 parallelizable** |

---

## Dependencies

```
Phase 1 (Setup)
    ↓
Phase 2 (Foundational - blocking)
    ↓
Phase 3 (US1 - P1) ←─── MVP scope (first complete user story)
    ↓
Phase 4 (US2 - P2) ←─── Independent (can run in parallel with Phase 5)
    ↓ (or parallel)
Phase 5 (US3 - P3) ←─── Independent (can run in parallel with Phase 4)
    ↓
Phase 6 (Polish & Cross-Cutting)
```

**Independent Testing**: Each user story (Phase 3-5) can be tested independently per acceptance criteria in spec.md.

**MVP Scope**: Phase 1 + Phase 2 + Phase 3 (User Story 1) delivers core value - location-aware pet listings for returning users.

---

## Phase 1: Setup (Sequential)

**Goal**: Initialize project structure and foundational types.

- [x] T001 Create TypeScript types file `/webApp/src/types/location.ts` with Coordinates, PermissionState, GeolocationState interfaces from data-model.md
- [x] T002 Create utility file `/webApp/src/utils/location.ts` with formatCoordinates function stub (returns input unchanged)
- [x] T003 Verify project structure: run `npm run build` from `/webApp` to ensure TypeScript compilation succeeds

**Completion Criteria**: TypeScript builds without errors, new files created. ✅ COMPLETE

---

## Phase 2: Foundational (Blocking Prerequisites)

**Goal**: Implement core location utilities and extend repository for location support. These are blocking for all user stories.

### Utility: Coordinate Formatting

- [x] T004 [P] Write test `/webApp/src/__tests__/utils/location.test.ts` for formatCoordinates function (test cases: 4 decimal rounding, edge values -90/90, -180/180)
- [x] T005 Implement formatCoordinates in `/webApp/src/utils/location.ts` (round to 4 decimals using toFixed)
- [x] T006 Run test: `npm test -- location.test.ts` from `/webApp` (must pass)
- [x] T007 Run lint: `npm run lint` from `/webApp` (must have no errors)

### Repository: Location Parameter Support

- [x] T008 [P] Write test `/webApp/src/__tests__/services/animal-repository.test.ts` for getAnimals with location parameter (test cases: with location params, without location params, verify URL construction)
- [x] T009 Extend getAnimals method in `/webApp/src/services/animal-repository.ts` to accept optional PetListingsFetchOptions parameter and append lat/lng query params using URLSearchParams
- [x] T010 Run test: `npm test -- animal-repository.test.ts` from `/webApp` (must pass all new and existing tests)
- [x] T011 Run lint: `npm run lint` from `/webApp` (must have no errors)

**Completion Criteria**: Utility and repository tests pass, no lint errors. ✅ COMPLETE

---

## Phase 3: User Story 1 - Location-Aware Content for Location-Authorized Users (P1)

**Story Goal**: Users with granted location permissions automatically get location-filtered pet listings on page load.

**Independent Test**: Launch app with location permission already granted in browser → verify location fetched → verify API called with `?lat=X&lng=Y` params.

**Acceptance Scenarios**:
1. Location permission granted → fetch location → query with lat/lng
2. Location fetch succeeds → display pets with location filtering
3. Location fetch fails/timeout → query without location (fallback)
4. Location fetch in progress → show full-page spinner

### Hook: use-geolocation

- [x] T012 [P] [US1] Write test `/webApp/src/__tests__/hooks/use-geolocation.test.ts` for useGeolocation hook (test cases: granted permission success, timeout fallback, position unavailable fallback, loading state transitions)
- [x] T013 [US1] Implement useGeolocation hook in `/webApp/src/hooks/use-geolocation.ts` (use navigator.geolocation, navigator.permissions, 3s timeout, format coordinates)
- [x] T014 [US1] Run test: `npm test -- use-geolocation.test.ts` from `/webApp` (must pass)
- [x] T015 [US1] Run lint: `npm run lint` from `/webApp` (must have no errors)

### Hook: use-animal-list Integration

- [x] T016 [P] [US1] Write test cases in `/webApp/src/__tests__/hooks/use-animal-list.test.ts` for location integration (test cases: call getAnimals with location when coordinates available, call getAnimals without location when coordinates null, loading state includes location loading)
- [x] T017 [US1] Extend use-animal-list hook in `/webApp/src/hooks/use-animal-list.ts` to integrate useGeolocation and pass coordinates to animalRepository.getAnimals
- [x] T018 [US1] Run test: `npm test -- use-animal-list.test.ts` from `/webApp` (must pass all new and existing tests)
- [x] T019 [US1] Run lint: `npm run lint` from `/webApp` (must have no errors)

**Completion Criteria**: User Story 1 acceptance scenarios pass, hooks tested, no lint errors. ✅ COMPLETE

**MVP Checkpoint**: ✅ After this phase, core feature (location-aware listings) is functional for returning users with granted permissions.

---

## Phase 4: User Story 2 - First-Time Location Permission Request (P2)

**Story Goal**: First-time users see browser permission prompt and can grant/deny access.

**Independent Test**: Open app in incognito mode → verify permission prompt appears → test "Allow" path and "Block" path independently.

**Acceptance Scenarios**:
1. Permission not yet requested → prompt appears automatically on page load
2. User clicks "Allow" → fetch location → query with lat/lng
3. User clicks "Block"/"dismiss" → query without location → display all pets
4. Permission denied → user can still browse all pets

### Component: LoadingOverlay

- [x] T020 [P] [US2] Write test `/webApp/src/__tests__/components/LoadingOverlay.test.tsx` for LoadingOverlay component (test cases: renders spinner, blocks interaction, displays optional message, test-id present)
- [x] T021 [US2] Implement LoadingOverlay component in `/webApp/src/components/LoadingOverlay/LoadingOverlay.tsx` (full-page overlay, fixed position, z-index 1000, spinner, data-testid="petList.loading.spinner")
- [x] T022 [US2] Run test: `npm test -- LoadingOverlay.test.tsx` from `/webApp` (must pass)
- [x] T023 [US2] Run lint: `npm run lint` from `/webApp` (must have no errors)

**Completion Criteria**: User Story 2 acceptance scenarios pass, LoadingOverlay tested, no lint errors. Permission prompt flow works in incognito mode. ✅ COMPLETE

---

## Phase 5: User Story 3 - Recovery Path for Blocked Permissions (P3)

**Story Goal**: Users who previously blocked location see informational banner with instructions to enable it in browser settings.

**Independent Test**: Launch app with blocked permission → verify informational banner appears → verify X button dismisses banner → verify banner reappears on page reload.

**Acceptance Scenarios**:
1. Permission blocked → banner displayed above pet listings
2. Banner includes benefit message, generic instructions, X button
3. X button click → banner closes, pet listings remain visible
4. Page reload with blocked permission → banner reappears (no persistence)

### Component: LocationBanner

- [ ] T024 [P] [US3] Write test `/webApp/src/__tests__/components/LocationBanner.test.tsx` for LocationBanner component (test cases: renders message, renders instructions, X button present, onClose callback triggered, test-id present)
- [ ] T025 [US3] Implement LocationBanner component in `/webApp/src/components/LocationBanner/LocationBanner.tsx` (banner with benefit message, generic instructions, X button, data-testid="petList.locationBanner.close")
- [ ] T026 [US3] Run test: `npm test -- LocationBanner.test.tsx` from `/webApp` (must pass)
- [ ] T027 [US3] Run lint: `npm run lint` from `/webApp` (must have no errors)

### Integration: AnimalList Component

- [ ] T028 [P] [US3] Extend AnimalList component in `/webApp/src/components/AnimalList/AnimalList.tsx` to integrate useGeolocation, show LoadingOverlay during fetch, show LocationBanner when permission denied, handle all permission states
- [ ] T029 [US3] Run test: `npm test -- AnimalList` from `/webApp` (must pass all component tests including new location scenarios)
- [ ] T030 [US3] Run lint: `npm run lint` from `/webApp` (must have no errors)

**Completion Criteria**: User Story 3 acceptance scenarios pass, LocationBanner tested, AnimalList integrated, no lint errors. Blocked permission banner displays and dismisses correctly.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Goal**: End-to-end testing, final verification, documentation updates.

### E2E Tests

- [ ] T031 [P] Write E2E test `/e2e-tests/web/specs/location-pet-listings.spec.ts` covering all three user stories (US1: granted permission, US2: first-time prompt, US3: blocked banner) using Playwright
- [ ] T032 Run E2E tests: `npx playwright test location-pet-listings.spec.ts` from `/e2e-tests/web` (must pass all scenarios)

### Final Verification

- [ ] T033 [P] Run full test suite with coverage: `npm test -- --coverage` from `/webApp` (verify 80%+ coverage for new code, check `/webApp/coverage/index.html`)

**Completion Criteria**: All E2E tests pass, 80%+ test coverage achieved, no lint errors across codebase.

---

## Parallel Execution Opportunities

### Phase 2 (Foundational)
```bash
# Can run in parallel after T003:
Terminal 1: T004 → T005 → T006 → T007 (coordinate formatting)
Terminal 2: T008 → T009 → T010 → T011 (repository extension)
```

### Phase 3 (User Story 1)
```bash
# Can run in parallel after Phase 2 complete:
Terminal 1: T012 → T013 → T014 → T015 (useGeolocation hook)
Terminal 2: T016 → T017 → T018 → T019 (use-animal-list integration)
```

### Phase 4 (User Story 2)
```bash
# Can run in parallel after Phase 3 complete:
Single terminal: T020 → T021 → T022 → T023 (LoadingOverlay component)
# OR run in parallel with Phase 5 if different developers
```

### Phase 5 (User Story 3)
```bash
# Can run in parallel with Phase 4 if different developers:
Terminal 1: T024 → T025 → T026 → T027 (LocationBanner component)
Terminal 2: T028 → T029 → T030 (AnimalList integration - depends on T021 and T025)
```

### Phase 6 (Polish)
```bash
# Can run in parallel after Phase 5 complete:
Terminal 1: T031 → T032 (E2E tests)
Terminal 2: T033 (coverage verification)
```

---

## Test Commands Reference

All commands run from `/webApp` directory:

```bash
# Run specific test file
npm test -- location.test.ts

# Run all tests
npm test

# Run tests with coverage
npm test -- --coverage

# Run lint
npm run lint

# Build TypeScript
npm run build

# E2E tests (from /e2e-tests/web)
npx playwright test location-pet-listings.spec.ts
```

---

## Implementation Strategy

### MVP-First Approach (Recommended)

**Phase 1 + Phase 2 + Phase 3** = MVP delivering core value:
- Users with granted permissions get location-filtered pets
- Graceful fallback when location unavailable
- Loading states and error handling

**Estimated**: 19 tasks, ~6-8 hours

### Incremental Delivery

After MVP:
1. **Phase 4** (User Story 2): Add first-time user onboarding flow
2. **Phase 5** (User Story 3): Add blocked permission recovery path
3. **Phase 6**: E2E tests and final polish

---

## Success Criteria

✅ **Each User Story Independently Testable**:
- US1: Test with granted permission in browser
- US2: Test in incognito mode (first-time user)
- US3: Test with blocked permission status

✅ **All Tests Pass**: 80%+ coverage for new code

✅ **No Lint Errors**: ESLint passes on all new/modified files

✅ **Backward Compatible**: Existing animal list functionality unaffected when location unavailable

✅ **Non-Blocking UX**: Users can browse pets regardless of location permission status

---

## Notes

- **No Backend Changes**: Existing `/api/v1/announcements` endpoint already supports `?lat=X&lng=Y` query parameters
- **Browser Compatibility**: Graceful degradation for browsers without Geolocation API support
- **HTTPS Required**: Location feature only works on HTTPS in production (localhost exempt for dev)
- **Documentation**: Minimal per user request - code should be self-explanatory with JSDoc where needed

---

**Ready to implement**: Start with Phase 1, proceed sequentially through phases, leverage parallel execution within phases where possible.
