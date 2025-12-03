# Implementation Plan: Web Browser Location for Pet Listings

**Branch**: `032-web-location-query` | **Date**: 2025-11-29 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/032-web-location-query/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Add browser-based geolocation support to the web pet listings page. When users load the page, the app will request location permission, fetch the user's coordinates using the browser Geolocation API, and append `?lat=LATITUDE&lng=LONGITUDE` query parameters to the existing pet listings API call. The implementation extends the existing `AnimalRepository.getAnimals()` method with optional location parameters and adds minimal UI components for permission handling (loading spinner, error states, informational banner for blocked permissions). Users can still browse all pets if location is unavailable or blocked, ensuring a non-blocking experience.

## Technical Context

**Language/Version**: TypeScript 5.x (ES2022 target)
**Primary Dependencies**: React 18.x, React Router 6.x, existing Fetch API
**Storage**: N/A (location used only for current query, not persisted)
**Testing**: Vitest + React Testing Library
**Target Platform**: Modern browsers with Geolocation API support (Chrome 5+, Firefox 3.5+, Safari 5+, Edge 12+)
**Project Type**: Web (React TypeScript SPA)
**Performance Goals**: Location fetch timeout 3 seconds, coordinates rounded to 4 decimal places
**Constraints**: HTTPS required in production for Geolocation API (graceful fallback on HTTP), non-blocking UX (pets load with or without location)
**Scale/Scope**: Single page feature (pet listings), ~3-4 new React components/hooks

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is a web-only feature, so Android/iOS checks are marked N/A. Backend checks are N/A as no server changes are required (existing `/api/v1/announcements` endpoint already supports `lat`/`lng` query parameters per specification).

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (not affected)
  - iOS: N/A (not affected)
  - Web: Feature isolated to `/webApp` with React TypeScript implementation
  - Backend: N/A (no backend changes, existing endpoint supports query parameters)
  - NO shared compiled code between platforms
  - Violation justification: _None - web-only feature_

- [ ] **Android MVI Architecture**: N/A - web-only feature
  - Violation justification: _N/A - no Android changes_

- [ ] **iOS MVVM-C Architecture**: N/A - web-only feature
  - Violation justification: _N/A - no iOS changes_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Android: N/A (not affected)
  - iOS: N/A (not affected)
  - Web: Existing `AnimalRepository` class extended with location parameter support
  - Backend: N/A (not affected)
  - Violation justification: _None - extending existing service class with backward-compatible method signature_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: N/A (not affected)
  - iOS: N/A (not affected)
  - Web: Existing singleton `animalRepository` instance used, no DI changes needed
  - Backend: N/A (not affected)
  - Violation justification: _None - using existing DI pattern_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: N/A (not affected)
  - iOS: N/A (not affected)
  - Web: Tests planned in `/webApp/src/__tests__/` for hooks and components
    - `useGeolocation.test.ts` - custom hook unit tests
    - `LocationBanner.test.tsx` - banner component tests
    - Coverage target: 80% line + branch coverage
  - Backend: N/A (not affected)
  - Violation justification: _None - tests planned for all new code_

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: Playwright tests planned in `/e2e-tests/web/specs/location-pet-listings.spec.ts`
    - User Story 1: Location-authorized users (pre-granted permission)
    - User Story 2: First-time permission request
    - User Story 3: Blocked permission recovery path
  - Mobile: N/A (not affected)
  - Violation justification: _None - E2E tests planned for all user stories_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: N/A (not affected)
  - iOS: N/A (not affected)
  - Web: Native `async`/`await` for Geolocation API and repository calls
  - Backend: N/A (not affected)
  - Violation justification: _None - using native async/await_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: N/A (not affected)
  - iOS: N/A (not affected)
  - Web: `data-testid` attributes planned:
    - `petList.loading.spinner` - loading overlay
    - `petList.locationBanner.close` - close button for blocked permission banner
    - `petList.error.retry` - retry button for API errors
    - `petList.empty.message` - empty state container
  - Violation justification: _None - test IDs planned for all interactive elements_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Kotlin: N/A (not affected)
  - Swift: N/A (not affected)
  - TypeScript: JSDoc format for new hook and utility functions:
    - `useGeolocation` hook - fetches and manages location state
    - `formatCoordinates` utility - rounds coordinates to 4 decimal places
    - Documentation kept concise (1-3 sentences: WHAT/WHY)
  - Violation justification: _None - JSDoc planned for new APIs_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests separate setup (Given), action (When), verification (Then)
  - Descriptive test names (TypeScript string format)
  - Comments mark test phases in complex tests
  - Violation justification: _None - all tests follow Given-When-Then_

### Backend Architecture & Quality Standards (if `/server` affected)

- [ ] **Backend Technology Stack**: N/A - no backend changes required
  - Violation justification: _N/A - existing `/api/v1/announcements` endpoint already supports `lat`/`lng` query parameters_

- [ ] **Backend Code Quality**: N/A - no backend changes required
  - Violation justification: _N/A_

- [ ] **Backend Dependency Management**: N/A - no backend changes required
  - Violation justification: _N/A_

- [ ] **Backend Directory Structure**: N/A - no backend changes required
  - Violation justification: _N/A_

- [ ] **Backend TDD Workflow**: N/A - no backend changes required
  - Violation justification: _N/A_

- [ ] **Backend Testing Strategy**: N/A - no backend changes required
  - Violation justification: _N/A_

## Project Structure

### Documentation (this feature)

```text
specs/032-web-location-query/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
└── contracts/           # Phase 1 output (/speckit.plan command)
```

### Source Code (repository root)

```text
webApp/
├── src/
│   ├── services/
│   │   └── animal-repository.ts           # EXTEND: Add optional location params to getAnimals()
│   ├── hooks/
│   │   ├── use-geolocation.ts             # NEW: Custom hook for browser location
│   │   └── use-animal-list.ts             # EXTEND: Integrate location into data fetching
│   ├── components/
│   │   ├── AnimalList/
│   │   │   ├── AnimalList.tsx             # EXTEND: Integrate location logic
│   │   │   └── EmptyState.tsx             # EXISTS: Already has empty state (may need update for location context)
│   │   ├── LocationBanner/
│   │   │   └── LocationBanner.tsx         # NEW: Banner for blocked permissions
│   │   └── LoadingOverlay/
│   │       └── LoadingOverlay.tsx         # NEW: Full-page spinner during fetch
│   ├── utils/
│   │   └── location.ts                    # NEW: Coordinate formatting utility
│   └── __tests__/
│       ├── hooks/
│       │   ├── use-geolocation.test.ts    # NEW: Unit tests for location hook
│       │   └── use-animal-list.test.ts    # EXTEND: Add location scenarios
│       ├── components/
│       │   ├── LocationBanner.test.tsx    # NEW: Banner component tests
│       │   └── LoadingOverlay.test.tsx    # NEW: Loading component tests
│       └── utils/
│           └── location.test.ts           # NEW: Coordinate formatting tests
│
e2e-tests/
└── web/
    ├── specs/
    │   └── location-pet-listings.spec.ts  # NEW: E2E tests for location feature
    └── pages/
        └── AnimalListPage.ts              # EXTEND: Add location-related selectors
```

**Structure Decision**: Web-only feature isolated to `/webApp` module. Minimal changes to existing code:
1. **EXTEND** `AnimalRepository.getAnimals()` with optional `lat`/`lng` parameters (backward compatible)
2. **NEW** custom hooks for location management (`use-geolocation`, integrate into `use-animal-list`)
3. **NEW** UI components for location banner and loading overlay (`EmptyState` already exists, may need minor updates)
4. **EXTEND** existing `AnimalList.tsx` to orchestrate location fetch and display logic

No backend changes required - existing `/api/v1/announcements` endpoint already supports `?lat=X&lng=Y` query parameters per spec.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

_No violations - all constitution checks passed._
