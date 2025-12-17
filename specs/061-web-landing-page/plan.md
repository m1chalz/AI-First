# Implementation Plan: Web Application Landing Page

**Branch**: `061-web-landing-page` | **Date**: 2025-12-17 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/061-web-landing-page/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Create a landing page for the PetSpot web application featuring a hero section with gradient background, four informational feature cards (Search Database, Report Missing, Found a Pet, Location Based), a "Recently Lost Pets" section displaying up to 5 most recent MISSING pet announcements from the backend API, and a comprehensive footer with branding, quick links, and contact information. The page must be responsive for tablet (768px+) and desktop (1024px+) screens and integrate with existing tab navigation system.

## Technical Context

**Language/Version**: TypeScript (React 18, strict mode enabled)  
**Primary Dependencies**: React 18, Vite, React Router, CSS modules (no Tailwind per project rules)  
**Storage**: N/A (no local storage or state persistence needed for landing page)  
**Testing**: Vitest + React Testing Library  
**Target Platform**: Web browser (tablet 768px+, desktop 1024px+, mobile not supported)  
**Project Type**: Web application (affects `/webApp` only)  
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)  
**Constraints**: Tablet/desktop only (screens < 768px not supported, native mobile apps handle mobile users)  
**Scale/Scope**: Single landing page with static content + dynamic pet list (up to 5 items)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is a web-only feature (affects only `/webApp` module). Android, iOS, and backend-related checks are marked N/A. Focus on Web Architecture & Quality Standards checks.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (no changes to `/composeApp`)
  - iOS: N/A (no changes to `/iosApp`)
  - Web: ✅ Components, hooks, lib, services in `/webApp` (fully independent)
  - Backend: N/A (no changes to `/server`, assumes existing API endpoint)
  - NO shared compiled code between platforms
  - Violation justification: N/A (web-only feature, compliant)

- [x] **Android MVI Architecture**: N/A (no Android changes)
  - Violation justification: N/A (web-only feature)

- [x] **iOS MVVM-C Architecture**: N/A (no iOS changes)
  - Violation justification: N/A (web-only feature)

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Android: N/A (no changes)
  - iOS: N/A (no changes)
  - Web: ✅ Service interfaces in `/webApp/src/services/` for API calls
  - Backend: N/A (no changes, assumes existing API endpoint)
  - Implementations in platform-specific data/repositories modules
  - Use cases reference interfaces, not concrete implementations
  - Violation justification: N/A (compliant)

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: N/A (no changes)
  - iOS: N/A (no changes)
  - Web: ✅ React Context or native patterns in `/webApp/src/contexts/` (if needed for state management)
  - Backend: N/A (no changes)
  - Violation justification: N/A (compliant)

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: N/A (no changes)
  - iOS: N/A (no changes)
  - Web: ✅ Tests in `/webApp/src/hooks/__test__/`, `/webApp/src/lib/__test__/`, run `npm test --coverage`
  - Backend: N/A (no changes)
  - Coverage target: 80% line + branch coverage for web
  - Violation justification: N/A (compliant)

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: ✅ Java/Selenium/Cucumber tests in `/e2e-tests/java/src/test/resources/features/web/landing-page.feature`
  - Mobile: N/A (no mobile changes)
  - All tests written in Java with Gherkin scenarios
  - Page Object Model used
  - Each user story has at least one E2E test
  - Violation justification: N/A (compliant)

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: N/A (no changes)
  - iOS: N/A (no changes)
  - Web: ✅ Native `async`/`await` for API calls (no Promise chains)
  - Backend: N/A (no changes)
  - No Combine, RxJava, RxSwift, or callback-based patterns for new code
  - Violation justification: N/A (compliant)

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: N/A (no changes)
  - iOS: N/A (no changes)
  - Web: ✅ `data-testid` attribute on all interactive elements (pet cards, links, buttons)
  - Naming convention: `{screen}.{element}.{action}` (e.g., `landing.petCard.click`, `landing.viewAllLink.click`)
  - List items use stable IDs (e.g., `landing.petCard.${petId}`)
  - Violation justification: N/A (compliant)

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Kotlin: N/A (no changes)
  - Swift: N/A (no changes)
  - TypeScript: ✅ JSDoc format (`/** ... */`) for complex hooks/functions only
  - Documentation must be concise and high-level (1-3 sentences: WHAT/WHY, not HOW)
  - Document only when purpose is not clear from name alone
  - Skip documentation for self-explanatory methods, variables, and constants
  - Violation justification: N/A (compliant)

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then)
  - Web tests use `// given`, `// when`, `// then` comments
  - E2E tests structure scenarios with Given-When-Then phases
  - Test names follow platform conventions (descriptive strings for TypeScript)
  - Comments mark test phases in all tests
  - Violation justification: N/A (compliant)

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A (no backend changes, assumes existing API endpoint)
  - Violation justification: N/A (web-only feature, backend not affected)

- [x] **Backend Code Quality**: N/A (no backend changes)
  - Violation justification: N/A (web-only feature, backend not affected)

- [x] **Backend Dependency Management**: N/A (no backend changes)
  - Violation justification: N/A (web-only feature, backend not affected)

### Web Architecture & Quality Standards (if `/webApp` affected)

- [x] **Web Technology Stack**: Plan uses modern React 18 + TypeScript stack for `/webApp` module
  - Framework: ✅ React 18
  - Language: ✅ TypeScript with strict mode enabled
  - Build Tool: ✅ Vite
  - Testing: ✅ Vitest + React Testing Library
  - Violation justification: N/A (compliant)

- [x] **Web Code Quality**: Plan enforces quality standards for `/webApp` code
  - ESLint with TypeScript plugin configured and enabled: ✅ Already configured in project
  - Clean Code principles applied:
    - Small, focused functions (single responsibility): ✅ Will follow
    - Descriptive naming (avoid unclear abbreviations): ✅ Will follow
    - Maximum 3 nesting levels: ✅ Will follow
    - DRY principle (extract reusable logic): ✅ Will extract shared CSS and components
    - JSDoc documentation ONLY for complex functions: ✅ Will follow (minimal documentation)
  - Violation justification: N/A (compliant)

- [x] **Web Dependency Management**: Plan minimizes dependencies in `/webApp/package.json`
  - Only add dependencies providing significant value: ✅ No new dependencies needed (using existing React, React Router, CSS modules)
  - Prefer well-maintained, security-audited packages: ✅ Using standard React ecosystem packages
  - Avoid micro-dependencies: ✅ No micro-dependencies planned
  - Document rationale for each dependency: ✅ N/A (no new dependencies)
  - Regular `npm audit` security checks planned: ✅ Already in place
  - Violation justification: N/A (compliant)

- [x] **Web Business Logic Extraction**: Plan ensures business logic is extracted to testable functions
  - Business logic in `/webApp/src/hooks/` (custom React hooks): ✅ Reuses existing `useAnnouncementList()` hook
  - Pure utility functions in `/webApp/src/lib/` (framework-agnostic): ✅ Will create date/distance formatting utilities
  - Components remain thin (presentation layer only): ✅ Components will delegate to hooks and utilities
  - All hooks and lib functions covered by unit tests: ✅ Will achieve 80% coverage for new utilities
  - Violation justification: N/A (compliant)

- [x] **Web TDD Workflow**: Plan follows Test-Driven Development (Red-Green-Refactor)
  - RED: Write failing test first: ✅ Will follow
  - GREEN: Write minimal code to pass test: ✅ Will follow
  - REFACTOR: Improve code quality without changing behavior: ✅ Will follow
  - Tests written BEFORE implementation code: ✅ Will follow TDD workflow
  - Violation justification: N/A (compliant)

- [x] **Web Testing Strategy**: Plan includes comprehensive test coverage for `/webApp`
  - Unit tests (Vitest):
    - Location: ✅ `/src/hooks/__test__/`, `/src/lib/__test__/`
    - Coverage target: ✅ 80% line + branch coverage
    - Scope: ✅ Business logic in hooks and lib functions
  - Component tests (Vitest + React Testing Library):
    - Location: ✅ `/src/components/.../__tests__/`
    - Scope: ✅ Component rendering and user interactions (recommended)
  - All tests follow Given-When-Then structure: ✅ Will use `// given`, `// when`, `// then` comments
  - Run commands: ✅ `npm test`, `npm test --coverage`
  - Violation justification: N/A (compliant)

- [x] **Backend Directory Structure**: N/A (no backend changes)
  - Violation justification: N/A (web-only feature, backend not affected)

- [x] **Backend TDD Workflow**: N/A (no backend changes)
  - Violation justification: N/A (web-only feature, backend not affected)

- [x] **Backend Testing Strategy**: N/A (no backend changes)
  - Violation justification: N/A (web-only feature, backend not affected)

## Project Structure

### Documentation (this feature)

```text
specs/061-web-landing-page/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
│   └── api-contracts.md # Backend API contract documentation
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
webApp/src/
├── components/
│   └── home/                      # Landing page components (organized together)
│       ├── LandingPage.tsx        # Main landing page component (orchestrates all sections)
│       ├── LandingPage.module.css # Landing page styles
│       ├── HeroSection.tsx        # Hero section with gradient background and feature cards
│       ├── HeroSection.module.css # Hero section styles
│       ├── FeatureCard.tsx        # Individual feature card component (reusable)
│       ├── FeatureCard.module.css # Feature card styles
│       ├── RecentPetsSection.tsx  # Recently lost pets section
│       ├── RecentPetsSection.module.css # Recently lost pets section styles
│       ├── LandingPageCard.tsx    # Individual pet card component (simplified for landing page)
│       ├── LandingPageCard.module.css # Landing page card styles
│       ├── Footer.tsx             # Footer component with branding, links, contact
│       ├── Footer.module.css      # Footer styles
│       └── __tests__/             # Component tests
│           ├── LandingPage.test.tsx
│           ├── HeroSection.test.tsx
│           ├── FeatureCard.test.tsx
│           ├── RecentPetsSection.test.tsx
│           └── Footer.test.tsx
├── lib/
│   ├── date-utils.ts              # Date formatting utilities
│   ├── distance-utils.ts          # Distance formatting utilities
│   └── __test__/
│       ├── date-utils.test.ts     # Unit tests for date utilities (80% coverage)
│       └── distance-utils.test.ts # Unit tests for distance utilities (80% coverage)

e2e-tests/java/src/test/
├── resources/features/web/
│   └── landing-page.feature       # Gherkin scenarios for landing page E2E tests
└── java/.../pages/
    ├── LandingPage.java           # Page Object Model for landing page
    └── steps-web/
        └── LandingPageSteps.java  # Step definitions for landing page tests
```

**Structure Decision**: Web application structure (Option 2 - frontend only). This feature affects only the `/webApp` module, adding new React components in `/components/home/` directory (for better organization), utilities in `/lib/`, and E2E tests. No backend changes are required (assumes existing pet announcements API endpoint). 

**Reuses Existing Solutions**:
- `useAnnouncementList()` hook from `/webApp/src/hooks/use-announcement-list.ts`
- `announcementService` from `/webApp/src/services/announcement-service.ts`
- `Announcement` type from `/webApp/src/types/announcement.ts`
- `GeolocationContext` for user location

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

N/A - No constitution violations. All checks passed.
