# Implementation Plan: Web Microchip Number Screen

**Branch**: `034-web-chip-number-screen` | **Date**: 2025-12-01 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/034-web-chip-number-screen/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implement the first step (1/4) of the "report missing pet" flow for the web application, allowing users to enter a microchip number with automatic formatting. The screen includes:
- Header with back button, title, and progress indicator (1/4)
- Optional microchip number input field with automatic formatting (00000-00000-00000)
- Continue button (always enabled)
- Flow state management using React Context
- React Router integration for URL-based navigation
- Browser back button and refresh handling

Technical approach: React functional component with custom hook for input formatting, React Router for navigation, React Context for flow state persistence across steps, and browser history API integration for back button handling.

## Technical Context

**Language/Version**: TypeScript 5.x + React 18.x  
**Primary Dependencies**: React Router v6, React 18, TypeScript 5  
**Storage**: In-memory flow state (React Context, no localStorage/sessionStorage persistence)  
**Testing**: Vitest + React Testing Library  
**Target Platform**: Web browsers (Chrome, Firefox, Safari, Edge) - responsive design for mobile (320px+), tablet (768px+), desktop (1024px+)  
**Project Type**: Web application (frontend only, no backend changes)  
**Performance Goals**: Input formatting < 100ms response time, no visible lag during typing  
**Constraints**: No data persistence across page refreshes, flow state cleared on browser refresh/back  
**Scale/Scope**: Single screen (step 1 of 4-step flow), part of missing pet flow feature

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: For backend-only features (affecting only `/server` module), you may mark frontend-related checks (Platform Independence, Android MVI, iOS MVVM-C, Test Identifiers for UI, E2E Tests for mobile/web) as N/A. Focus on Backend Architecture & Quality Standards checks.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: Domain models, use cases, repositories, ViewModels in `/composeApp` - N/A (not affected)
  - iOS: Domain models, use cases, repositories, ViewModels in `/iosApp` - N/A (not affected)
  - Web: Domain models, services, state management in `/webApp` - ✅ COMPLIANT (this feature)
  - Backend: Independent Node.js/Express API in `/server` - N/A (not affected)
  - NO shared compiled code between platforms - ✅ COMPLIANT
  - Violation justification: _None - web platform implements independently_

- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop - N/A (web-only feature)
  - Violation justification: _N/A - This is a web-only feature_

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern - N/A (web-only feature)
  - Violation justification: _N/A - This is a web-only feature_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Android: Repository interfaces - N/A (not affected)
  - iOS: Repository protocols - N/A (not affected)
  - Web: Service interfaces in `/webApp/src/services/` - ✅ COMPLIANT (no services needed for this screen - only state management)
  - Backend: Repository interfaces - N/A (not affected)
  - Violation justification: _None - This screen uses React Context for state, no repository layer needed_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: MUST use Koin - N/A (not affected)
  - iOS: MUST use manual DI - N/A (not affected)
  - Web: SHOULD use React Context - ✅ COMPLIANT (flow state managed via React Context)
  - Backend: Manual DI - N/A (not affected)
  - Violation justification: _None - Using React Context for flow state DI_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: Tests in `/composeApp/src/androidUnitTest/` - N/A (not affected)
  - iOS: Tests in `/iosApp/iosAppTests/` - N/A (not affected)
  - Web: Tests in `/webApp/src/__tests__/` - ✅ COMPLIANT (unit tests for formatting hook, component tests, integration tests)
  - Backend: Tests in `/server/src/` - N/A (not affected)
  - Coverage target: 80% line + branch coverage per platform
  - Violation justification: _None - Will achieve 80%+ coverage for web components and hooks_

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: Selenium tests in `/e2e-tests/src/test/resources/features/web/` - ✅ COMPLIANT (E2E tests for all 4 user stories)
  - Mobile: Appium tests - N/A (not affected)
  - All tests written in Java + Cucumber (Gherkin)
  - Page Object Model used
  - Each user story has at least one E2E test
  - Violation justification: _None - E2E tests will cover all user stories_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: Kotlin Coroutines - N/A (not affected)
  - iOS: Swift Concurrency - N/A (not affected)
  - Web: Native `async`/`await` - ✅ COMPLIANT (React hooks with async/await for any async operations)
  - Backend: Native `async`/`await` - N/A (not affected)
  - No Combine, RxJava, RxSwift, or callback-based patterns for new code - ✅ COMPLIANT
  - Violation justification: _None - Using native async/await_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: `testTag` modifier - N/A (not affected)
  - iOS: `accessibilityIdentifier` modifier - N/A (not affected)
  - Web: `data-testid` attribute - ✅ COMPLIANT (all interactive elements will have data-testid)
  - Naming convention: `{screen}.{element}.{action}` (e.g., `reportMissingPet.step1.backButton.click`)
  - List items use stable IDs - ✅ COMPLIANT (not applicable - no lists on this screen)
  - Violation justification: _None - All interactive elements will have data-testid attributes_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Kotlin: KDoc format - N/A (not affected)
  - Swift: SwiftDoc format - N/A (not affected)
  - TypeScript: JSDoc format - ✅ COMPLIANT (custom hooks and utility functions will have JSDoc)
  - Documentation must be concise and high-level (1-3 sentences: WHAT/WHY, not HOW)
  - Document only when purpose is not clear from name alone
  - Skip documentation for self-explanatory methods, variables, and constants
  - Violation justification: _None - Will document non-obvious logic (formatting algorithm, browser history handling)_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then)
  - Component tests use Given-When-Then pattern with descriptive names
  - E2E tests structure scenarios with Given-When-Then phases
  - Test names follow platform conventions (descriptive strings for TypeScript)
  - Comments mark test phases in complex tests
  - Violation justification: _None - All tests will follow Given-When-Then structure_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - `/server` module not affected by this feature
  - Violation justification: _N/A - This is a frontend-only feature with no backend changes_

- [x] **Backend Code Quality**: N/A - `/server` module not affected
  - Violation justification: _N/A - No backend code changes_

- [x] **Backend Dependency Management**: N/A - `/server` module not affected
  - Violation justification: _N/A - No backend dependency changes_

- [x] **Backend Directory Structure**: N/A - `/server` module not affected
  - Violation justification: _N/A - No backend structure changes_

- [x] **Backend TDD Workflow**: N/A - `/server` module not affected
  - Violation justification: _N/A - No backend implementation_

- [x] **Backend Testing Strategy**: N/A - `/server` module not affected
  - Violation justification: _N/A - No backend tests needed_

## Project Structure

### Documentation (this feature)

```text
specs/034-web-chip-number-screen/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
│   ├── FlowState.ts     # TypeScript interface for flow state
│   └── routes.ts        # React Router routes configuration
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
webApp/
├── src/
│   ├── components/
│   │   ├── ReportMissingPet/
│   │   │   ├── MicrochipNumberScreen.tsx      # Main screen component (stateful, uses hooks)
│   │   │   ├── MicrochipNumberContent.tsx     # Presentational component (pure)
│   │   │   ├── Header.tsx                     # Reusable header with back/title/progress
│   │   │   └── __tests__/
│   │   │       ├── MicrochipNumberScreen.test.tsx
│   │   │       └── MicrochipNumberContent.test.tsx
│   │   └── ...
│   ├── hooks/
│   │   ├── use-report-missing-pet-flow.ts     # Flow state management hook (React Context consumer)
│   │   ├── use-microchip-formatter.ts         # Input formatting logic hook
│   │   ├── use-browser-back-handler.ts        # Browser back button handling hook
│   │   └── __tests__/
│   │       ├── use-report-missing-pet-flow.test.ts
│   │       ├── use-microchip-formatter.test.ts
│   │       └── use-browser-back-handler.test.ts
│   ├── contexts/
│   │   └── ReportMissingPetFlowContext.tsx    # React Context for flow state
│   ├── models/
│   │   └── ReportMissingPetFlow.ts            # TypeScript interfaces for flow state
│   ├── utils/
│   │   ├── microchip-formatter.ts             # Pure formatting utility functions
│   │   └── __tests__/
│   │       └── microchip-formatter.test.ts
│   ├── routes/
│   │   └── report-missing-pet-routes.tsx      # React Router route definitions
│   └── App.tsx                                # Updated with new routes
└── ...

e2e-tests/
└── src/
    └── test/
        ├── resources/
        │   └── features/
        │       └── web/
        │           └── report-missing-pet-step1.feature    # Gherkin scenarios
        └── java/
            └── .../
                ├── pages/
                │   └── ReportMissingPetStep1Page.java      # Page Object Model
                └── steps-web/
                    └── ReportMissingPetStep1Steps.java     # Step definitions
```

**Structure Decision**: Web application structure with feature-based component organization. The `ReportMissingPet` directory contains all components for the multi-step flow, with Step 1 (microchip number) implemented first. Hooks are extracted to `/hooks` for reusability and testability. React Context is used for flow state management across steps. E2E tests follow the unified Java + Cucumber structure with Page Object Model.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

_No violations - all checks passed or marked N/A appropriately._
