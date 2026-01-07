# Implementation Plan: Web Map Component on Landing Page

**Branch**: `063-web-map-view` | **Date**: 2025-12-18 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/063-web-map-view/spec.md`

**Note**: This document is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Add an interactive map component to the web landing page positioned between the Description panel and the Recently Lost Pets panel. The map will use Leaflet.js + OpenStreetMap tiles to display a 10 km radius viewport centered on the user's location (zoom level 13). The implementation includes location permission gating (informational message with consent button), fallback mode for location unavailable scenarios (fallback to Wrocław, PL), error handling (user instructed to refresh page), and zoom/pan interactions.

## Technical Context

**Language/Version**: TypeScript (ES2015), React 18  
**Primary Dependencies**: Leaflet.js 1.9+, react-leaflet 4.x, OpenStreetMap tiles (free tier)  
**Storage**: N/A (GeolocationContext provides location state)  
**Testing**: Vitest + React Testing Library (unit tests for hooks), Playwright (E2E tests for map interactions)  
**Target Platform**: Web browsers (Chrome, Firefox, Safari, Edge - latest 2 versions)  
**Project Type**: Web (React SPA)  
**Constraints**: Must use existing GeolocationContext, must respect OSM attribution requirements  
**Scale/Scope**: Single landing page component, ~3-5 new files (MapView component, hooks, tests), 80% test coverage target

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is a **web-only feature** affecting only `/webApp` module. Backend-related checks and mobile platform checks (Android MVI, iOS MVVM-C, mobile test identifiers, mobile E2E tests) are marked as N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (not affected)
  - iOS: N/A (not affected)
  - Web: ✅ Domain models (Location, MapState), services (GeolocationContext), state management (React hooks) in `/webApp`
  - Backend: N/A (not affected)
  - NO shared compiled code between platforms
  - Violation justification: _N/A (compliant)_

- [ ] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - Violation justification: _N/A (Android not affected by this feature)_

- [ ] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - Violation justification: _N/A (iOS not affected by this feature)_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Android: N/A
  - iOS: N/A
  - Web: ✅ GeolocationContext interface already exists, map hook will follow interface pattern
  - Backend: N/A
  - Violation justification: _N/A (compliant)_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: N/A
  - iOS: N/A
  - Web: ✅ React Context pattern used (GeolocationContext already exists)
  - Backend: N/A
  - Violation justification: _N/A (compliant)_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: N/A
  - iOS: N/A
  - Web: ✅ Tests in `/webApp/src/components/map/__tests__/`, `/webApp/src/hooks/__tests__/`, run `npm test --coverage`
  - Backend: N/A
  - Coverage target: 80% line + branch coverage
  - Violation justification: _N/A (compliant)_

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: ✅ Playwright tests in `/e2e-tests/web/specs/map-view.spec.ts` (User Story 1 & 2)
  - Mobile: N/A (mobile not affected)
  - Page Object Model used
  - Each user story has at least one E2E test
  - Violation justification: _N/A (compliant)_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: N/A
  - iOS: N/A
  - Web: ✅ Native `async`/`await` for geolocation, React hooks for async state
  - Backend: N/A
  - No Combine, RxJava, RxSwift, or callback-based patterns for new code
  - Violation justification: _N/A (compliant)_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: N/A
  - iOS: N/A
  - Web: ✅ `data-testid` attributes on all interactive elements (consent button, map container)
  - Naming convention: `{screen}.{element}.{action}` (e.g., `landingPage.map.consentButton`)
  - Violation justification: _N/A (compliant)_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Kotlin: N/A
  - Swift: N/A
  - TypeScript: ✅ JSDoc format for hook functions and complex utility functions
  - Documentation concise and high-level (1-3 sentences: WHAT/WHY, not HOW)
  - Skip documentation for self-explanatory names
  - Violation justification: _N/A (compliant)_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then)
  - Component tests use Given-When-Then pattern with descriptive strings
  - E2E tests structure scenarios with Given-When-Then phases
  - Violation justification: _N/A (compliant)_

### Backend Architecture & Quality Standards (if `/server` affected)

- [ ] **Backend Technology Stack**: Plan uses modern Node.js stack for `/server` module
  - Violation justification: _N/A (/server not affected by this feature)_

- [ ] **Backend Code Quality**: Plan enforces quality standards for `/server` code
  - Violation justification: _N/A (/server not affected by this feature)_

- [ ] **Backend Dependency Management**: Plan minimizes dependencies in `/server/package.json`
  - Violation justification: _N/A (/server not affected by this feature)_

- [ ] **Backend Directory Structure**: Plan follows standardized layout in `/server/src/`
  - Violation justification: _N/A (/server not affected by this feature)_

- [ ] **Backend TDD Workflow**: Plan follows Test-Driven Development (Red-Green-Refactor)
  - Violation justification: _N/A (/server not affected by this feature)_

- [ ] **Backend Testing Strategy**: Plan includes comprehensive test coverage for `/server`
  - Violation justification: _N/A (/server not affected by this feature)_

### Web Architecture & Quality Standards (if `/webApp` affected)

- [x] **Web Technology Stack**: Plan uses modern React 18 + TypeScript stack for `/webApp` module
  - Framework: ✅ React 18
  - Language: ✅ TypeScript with strict mode enabled
  - Build Tool: ✅ Vite
  - Testing: ✅ Vitest + React Testing Library
  - Violation justification: _N/A (compliant)_

- [x] **Web Code Quality**: Plan enforces quality standards for `/webApp` code
  - ESLint with TypeScript plugin configured and enabled ✅
  - Clean Code principles applied:
    - Small, focused functions (single responsibility) ✅
    - Descriptive naming (avoid unclear abbreviations) ✅
    - Maximum 3 nesting levels ✅
    - DRY principle (extract reusable logic) ✅
    - JSDoc documentation ONLY for complex functions ✅
  - Violation justification: _N/A (compliant)_

- [x] **Web Dependency Management**: Plan minimizes dependencies in `/webApp/package.json`
  - Only add dependencies providing significant value: ✅ leaflet (map rendering), react-leaflet (React integration)
  - Prefer well-maintained, security-audited packages: ✅ Both are widely used, actively maintained
  - Avoid micro-dependencies: ✅ No micro-dependencies planned
  - Document rationale for each dependency in comments: ✅ Will document in package.json
  - Regular `npm audit` security checks planned: ✅ Part of CI/CD
  - Violation justification: _N/A (compliant)_

- [x] **Web Business Logic Extraction**: Plan ensures business logic is extracted to testable functions
  - Business logic in `/webApp/src/hooks/`: ✅ `use-map-state.ts` (map initialization, location handling)
  - Components remain thin (presentation layer only): ✅ MapView component renders UI only
  - All hooks covered by unit tests: ✅ 80% coverage target
  - Violation justification: _N/A (compliant)_

- [x] **Web TDD Workflow**: Plan follows Test-Driven Development (Red-Green-Refactor)
  - RED: Write failing test first ✅
  - GREEN: Write minimal code to pass test ✅
  - REFACTOR: Improve code quality without changing behavior ✅
  - Tests written BEFORE implementation code ✅
  - Violation justification: _N/A (compliant)_

- [x] **Web Testing Strategy**: Plan includes comprehensive test coverage for `/webApp`
  - Unit tests (Vitest):
    - Location: ✅ `/src/hooks/__test__/use-map-state.test.ts`
    - Coverage target: ✅ 80% line + branch coverage
    - Scope: ✅ Business logic in hooks
  - Component tests (Vitest + React Testing Library):
    - Location: ✅ `/src/components/map/__tests__/MapView.test.tsx`
    - Scope: ✅ Component rendering and user interactions
  - All tests follow Given-When-Then structure ✅
  - Run commands: ✅ `npm test`, `npm test --coverage`
  - Violation justification: _N/A (compliant)_

## Project Structure

### Documentation (this feature)

```text
specs/063-web-map-view/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
│   └── map-component.interface.ts  # TypeScript interfaces for map component
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
webApp/
├── src/
│   ├── components/
│   │   └── map/
│   │       ├── MapView.tsx                    # Main map component (presentation)
│   │       ├── MapView.module.css             # Map component styles
│   │       ├── MapPermissionPrompt.tsx        # Informational message + consent button
│   │       ├── MapPermissionPrompt.module.css # Permission prompt styles
│   │       ├── MapErrorState.tsx              # Error state (instructs user to refresh)
│   │       ├── MapErrorState.module.css       # Error state styles
│   │       └── __tests__/
│   │           ├── MapView.test.tsx           # Component rendering tests
│   │           ├── MapPermissionPrompt.test.tsx
│   │           └── MapErrorState.test.tsx
│   │
│   ├── hooks/
│   │   ├── use-map-state.ts                   # Map state management hook (business logic)
│   │   └── __test__/
│   │       └── use-map-state.test.ts          # Hook unit tests
│   │
│   ├── pages/
│   │   └── Home.tsx                           # Landing page (integration point for MapView)
│   │
│   └── contexts/
│       └── GeolocationContext.tsx             # Existing context (reused, not modified)
│
└── package.json                                # Add leaflet + react-leaflet dependencies

e2e-tests/web/
└── specs/
    └── map-view.spec.ts                        # Playwright E2E tests for User Story 1 & 2
```

**Structure Decision**: This is a **web application feature** affecting only `/webApp`. The map component follows React component architecture with business logic extracted to custom hooks (`use-map-state.ts`). The component is thin (presentation only) and integrates into the existing landing page (`Home.tsx`) between the Description panel (HeroSection) and Recently Lost Pets panel (RecentPetsSection). No backend changes required.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No violations detected. All constitution checks pass or are N/A (mobile/backend not affected).
