# Implementation Plan: Web Map Pins

**Branch**: `064-web-map-pins` | **Date**: 2025-12-19 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/064-web-map-pins/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Display interactive map pins on the PetSpot landing page showing locations of missing and found pets. Users can click pins to view pet details in a pop-up overlay. Pins use color-coded teardrop markers (red for missing, blue for found) with status icons. The feature extends the existing Leaflet map component (from 063-web-map-view) with marker layer management, pop-up rendering, and independent loading/error states.

## Technical Context

**Language/Version**: TypeScript 5.x with strict mode enabled  
**Primary Dependencies**: React 18, Leaflet.js 1.9.x, React-Leaflet 4.x, Vite 5.x  
**Storage**: N/A (data fetched from existing backend API)  
**Testing**: Vitest + React Testing Library for unit tests, Playwright for E2E tests  
**Target Platform**: Modern web browsers (Chrome, Firefox, Safari, Edge - latest 2 versions)
**Project Type**: Web application (frontend only, `/webApp` module)  
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)  
**Constraints**: 
- Must integrate with existing Leaflet map component from 063-web-map-view
- Must use existing `/api/v1/announcements` endpoint (no backend changes)
- Pin markers must match Figma design (color-coded teardrop markers)
- Loading/error states independent from map loading/error states  
**Scale/Scope**: Expected 10-100 pins per viewport (bounded by ~10km radius map view)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

**Phase 0 Evaluation**: ✅ Passed (2025-12-19 before research)  
**Phase 1 Re-evaluation**: ✅ Passed (2025-12-19 after data model & contracts)  
**Status**: All checks compliant - ready to proceed to tasks phase

> **Note**: For backend-only features (affecting only `/server` module), you may mark frontend-related checks (Platform Independence, Android MVI, iOS MVVM-C, Test Identifiers for UI, E2E Tests for mobile/web) as N/A. Focus on Backend Architecture & Quality Standards checks.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (no changes)
  - iOS: N/A (no changes)
  - Web: Map pin logic, state management in `/webApp` only
  - Backend: N/A (reuses existing `/api/v1/announcements` endpoint)
  - NO shared compiled code between platforms
  - Violation justification: _Compliant - web-only feature_

- [x] **Android MVI Architecture**: N/A - No Android changes in this feature
  - Violation justification: _N/A - web-only feature_

- [x] **iOS MVVM-C Architecture**: N/A - No iOS changes in this feature
  - Violation justification: _N/A - web-only feature_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Android: N/A
  - iOS: N/A
  - Web: Will use existing `AnnouncementService` interface in `/webApp/src/services/`
  - Backend: N/A
  - Pin rendering logic will be abstracted into custom React hooks for testability
  - Violation justification: _Compliant - reuses existing service layer_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: N/A
  - iOS: N/A
  - Web: Will leverage existing React Context for AnnouncementService (already established in codebase)
  - Backend: N/A
  - Violation justification: _Compliant - reuses existing DI pattern_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: N/A
  - iOS: N/A
  - Web: Tests for custom hooks in `/webApp/src/hooks/__test__/`, utilities in `/webApp/src/lib/__test__/`
    - `use-map-pins.test.ts` - hook for fetching and managing pin data
    - `map-pin-helpers.test.ts` - utilities for pin marker icon creation
  - Backend: N/A
  - Coverage target: 80% line + branch coverage for all hooks and utilities
  - Run: `npm test --coverage` from `/webApp`
  - Violation justification: _Compliant - comprehensive unit tests planned_

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: Playwright tests in `/e2e-tests/web/specs/064-web-map-pins.spec.ts`
    - Test pin display on landing page
    - Test pin click and pop-up display
    - Test pop-up dismissal (close button and click-outside)
    - Test loading/error states
  - Mobile: N/A (web-only feature)
  - All tests written in TypeScript with Page Object Model
  - Each user story (2 stories) covered by E2E tests
  - Violation justification: _Compliant - comprehensive E2E tests planned_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: N/A
  - iOS: N/A
  - Web: Native `async`/`await` for API calls (no Promise chains)
    - Custom hooks will use `async`/`await` for fetching announcements
    - React state updates handled via `useState` + `useEffect`
  - Backend: N/A
  - No prohibited patterns (Combine, RxJS, callbacks) used
  - Violation justification: _Compliant - modern async/await patterns_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: N/A
  - iOS: N/A
  - Web: `data-testid` on all interactive elements (specified in FR-013):
    - `landingPage.map.pin.{petId}` - each pin marker
    - `landingPage.map.popup` - pop-up container
    - `landingPage.map.popup.close` - close button
    - `landingPage.map.pinsLoading` - loading indicator
    - `landingPage.map.pinsError` - error state
    - `landingPage.map.pinsRetry` - retry button
  - Naming follows `{screen}.{element}.{action}` convention
  - Violation justification: _Compliant - test IDs specified in spec_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Kotlin: N/A
  - Swift: N/A
  - TypeScript: JSDoc format for complex hooks only
  - Documentation concise (1-3 sentences: WHAT/WHY)
  - Skip self-explanatory function names
  - Violation justification: _Compliant - JSDoc for complex APIs_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests (Vitest) clearly separate setup → action → verification
  - Hook tests use descriptive test names: `it('should load pins when map viewport changes', ...)`
  - E2E tests (Playwright) structure scenarios with Given-When-Then phases
  - TypeScript test names use descriptive strings
  - Comments mark test phases in complex tests
  - Violation justification: _Compliant - all tests follow GWT structure_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - No backend changes (reuses existing `/api/v1/announcements` endpoint)
  - Violation justification: _N/A - web-only feature_

- [x] **Backend Code Quality**: N/A - No backend changes
  - Violation justification: _N/A - web-only feature_

- [x] **Backend Dependency Management**: N/A - No backend changes
  - Violation justification: _N/A - web-only feature_

### Web Architecture & Quality Standards (if `/webApp` affected)

- [x] **Web Technology Stack**: Plan uses modern React 18 + TypeScript stack for `/webApp` module
  - Framework: React 18 ✓
  - Language: TypeScript with strict mode enabled ✓
  - Build Tool: Vite ✓
  - Testing: Vitest + React Testing Library ✓
  - Additional: Leaflet.js 1.9.x + React-Leaflet 4.x for map functionality
  - Violation justification: _Compliant - using established stack_

- [x] **Web Code Quality**: Plan enforces quality standards for `/webApp` code
  - ESLint with TypeScript plugin configured and enabled ✓
  - Clean Code principles applied:
    - Small, focused functions (single responsibility) - custom hooks for pin logic
    - Descriptive naming - `use-map-pins`, `use-map-popup`, `create-pin-marker`
    - Maximum 3 nesting levels - enforced via ESLint
    - DRY principle - shared utilities in `/webApp/src/lib/`
    - JSDoc documentation ONLY for complex functions
  - Violation justification: _Compliant - adhering to established standards_

- [x] **Web Dependency Management**: Plan minimizes dependencies in `/webApp/package.json`
  - New dependencies required:
    - `leaflet` (already installed) - industry-standard open-source map library
    - `react-leaflet` (already installed) - official React bindings for Leaflet
    - `@types/leaflet` (already installed) - TypeScript definitions
  - All dependencies well-maintained with active communities
  - No micro-dependencies added
  - Regular `npm audit` security checks during CI/CD
  - Violation justification: _Compliant - minimal, justified dependencies_

- [x] **Web Business Logic Extraction**: Plan ensures business logic is extracted to testable functions
  - Business logic in `/webApp/src/hooks/`:
    - `use-map-pins.ts` - fetching and managing pin data
  - Pure utility functions in `/webApp/src/lib/`:
    - `map-pin-helpers.ts` - pin marker icon creation (Leaflet divIcon)
  - Components remain thin - presentation only:
    - `MapPinLayer.tsx` - renders Leaflet markers (Leaflet handles pop-up state)
  - All hooks and lib functions covered by unit tests (80% target)
  - Violation justification: _Compliant - proper separation of concerns_

- [x] **Web TDD Workflow**: Plan follows Test-Driven Development (Red-Green-Refactor)
  - RED: Write failing test first (e.g., test `useMapPins` returns empty array initially)
  - GREEN: Write minimal code to pass test (implement basic hook structure)
  - REFACTOR: Improve code quality without changing behavior
  - Tests written BEFORE implementation code for all hooks and utilities
  - Each functional requirement (FR) has corresponding tests before implementation
  - Violation justification: _Compliant - TDD approach planned_

- [x] **Web Testing Strategy**: Plan includes comprehensive test coverage for `/webApp`
  - Unit tests (Vitest):
    - Location: `/src/hooks/__test__/use-map-pins.test.ts`
    - Location: `/src/lib/__test__/map-pin-helpers.test.ts`
    - Coverage target: 80% line + branch coverage
    - Scope: Pin fetching logic, marker icon creation, error handling
  - Component tests (Vitest + React Testing Library):
    - Location: `/src/components/map/__tests__/MapPinLayer.test.tsx`
    - Scope: Component rendering, pin clicks (Leaflet handles pop-up state)
  - All tests follow Given-When-Then structure with descriptive names
  - Run commands: `npm test`, `npm test --coverage` (from `/webApp`)
  - Violation justification: _Compliant - comprehensive test plan with 80% target_

- [x] **Backend Directory Structure**: N/A - No backend changes
  - Violation justification: _N/A - web-only feature_

- [x] **Backend TDD Workflow**: N/A - No backend changes
  - Violation justification: _N/A - web-only feature_

- [x] **Backend Testing Strategy**: N/A - No backend changes
  - Violation justification: _N/A - web-only feature_

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
webApp/
├── src/
│   ├── components/
│   │   └── map/
│   │       ├── MapPinLayer.tsx           # NEW: Renders Leaflet markers with pop-ups
│   │       ├── __tests__/
│   │       │   └── MapPinLayer.test.tsx  # NEW: Component tests
│   │       └── (existing map components from 063)
│   ├── hooks/
│   │   ├── use-map-pins.ts               # NEW: Hook for fetching/managing pins
│   │   └── __test__/
│   │       └── use-map-pins.test.ts      # NEW: Hook unit tests
│   ├── lib/
│   │   ├── map-pin-helpers.ts            # NEW: Pin icon creation (Leaflet divIcon)
│   │   └── __test__/
│   │       └── map-pin-helpers.test.ts   # NEW: Utility unit tests
│   └── services/
│       └── (reuse existing AnnouncementService)

e2e-tests/
└── web/
    └── specs/
        └── 064-web-map-pins.spec.ts      # NEW: E2E tests for pin features
```

**Structure Decision**: Web application structure (Option 2 - frontend only). This feature adds map pin functionality to the existing web application in `/webApp`. No backend changes required (reuses `/api/v1/announcements` endpoint). No mobile platform changes.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., 4th project] | [current need] | [why 3 projects insufficient] |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient] |
