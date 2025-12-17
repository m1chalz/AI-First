# Implementation Plan: Web App Navigation Bar

**Branch**: `057-web-navigation` | **Date**: 2025-12-16 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/057-web-navigation/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implement a horizontal navigation bar for the PetSpot web application that enables users to navigate between main sections: Home landing page (`/`), Lost Pet announcements (`/lost-pets`), Found Pet announcements (`/found-pets`), Contact Us (`/contact`), and Account (`/account`). The navigation bar follows the Figma design specifications, displays appropriate icons and labels, highlights the currently active section, and is visible on desktop screens (≥768px) only. Built with React 18, TypeScript, React Router v6, and CSS Modules.

## Technical Context

**Language/Version**: TypeScript 5.x (strict mode), React 18  
**Primary Dependencies**: React Router v6 (client-side routing), CSS Modules (styling), react-icons v5.5.0 (icon library - ALREADY INSTALLED, resolved via research.md)  
**Storage**: N/A (no data persistence required for navigation)  
**Testing**: Vitest + React Testing Library (unit/component tests), Java 21 + Maven + Selenium (E2E tests)  
**Target Platform**: Modern web browsers (Chrome, Firefox, Safari, Edge), desktop screens ≥768px width
**Project Type**: Web application (`/webApp` module)  
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)  
**Constraints**: Desktop-only implementation (mobile navigation deferred to future iteration), navigation scrolls with page content (not fixed/sticky)  
**Scale/Scope**: 5 navigation items, 5 route destinations, responsive breakpoint at 768px

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is a Web-only feature affecting `/webApp` module. Android, iOS, and Backend checks are marked as N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (not affected by this feature)
  - iOS: N/A (not affected by this feature)
  - Web: Navigation component in `/webApp/src/components/`, routes configuration in `/webApp/src/routes/`
  - Backend: N/A (not affected by this feature)
  - NO shared compiled code between platforms
  - Violation justification: N/A - compliant

- [x] **Android MVI Architecture**: N/A (Android not affected by this feature)

- [x] **iOS MVVM-C Architecture**: N/A (iOS not affected by this feature)

- [x] **Interface-Based Design**: N/A (no domain logic or data layer changes required - pure presentation/navigation feature)

- [x] **Dependency Injection**: N/A (navigation component uses React Context for routing, no custom DI required)

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for Web
  - Android: N/A
  - iOS: N/A
  - Web: Component tests in `/webApp/src/components/__tests__/`, hook tests in `/webApp/src/hooks/__test__/` (if custom navigation hooks added)
  - Backend: N/A
  - Coverage target: 80% line + branch coverage for navigation component and hooks
  - Violation justification: N/A - compliant

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: Java 21 + Maven + Selenium tests in `/e2e-tests/java/src/test/resources/features/web/057-navigation.feature`
  - Mobile: N/A
  - Page Object Model used for web navigation
  - Each user story has at least one E2E scenario
  - Violation justification: N/A - compliant

- [x] **Asynchronous Programming Standards**: N/A (navigation is synchronous, no async operations required)

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: N/A
  - iOS: N/A
  - Web: `data-testid` attribute on all navigation items
  - Naming convention: `navigation.{item}.link` (e.g., `navigation.home.link`, `navigation.lostPet.link`)
  - Logo: `navigation.logo.link`
  - Violation justification: N/A - compliant

- [x] **Public API Documentation**: Plan ensures minimal documentation
  - Web: JSDoc only when purpose is not clear from name alone
  - Navigation component props and functions should be self-explanatory
  - Skip documentation for simple components (e.g., `NavigationItem`, `isActive`)
  - Violation justification: N/A - compliant

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Component tests clearly separate setup (// given), action (// when), verification (// then)
  - E2E tests structure scenarios with Given-When-Then phases in Gherkin
  - Test names follow TypeScript convention (descriptive strings)
  - Violation justification: N/A - compliant

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A (backend not affected by this feature)

- [x] **Backend Code Quality**: N/A (backend not affected by this feature)

- [x] **Backend Dependency Management**: N/A (backend not affected by this feature)

### Web Architecture & Quality Standards (if `/webApp` affected)

- [x] **Web Technology Stack**: Plan uses modern React 18 + TypeScript stack for `/webApp` module
  - Framework: React 18 ✓
  - Language: TypeScript with strict mode enabled ✓
  - Build Tool: Vite ✓
  - Testing: Vitest + React Testing Library ✓
  - Violation justification: N/A - compliant

- [x] **Web Code Quality**: Plan enforces quality standards for `/webApp` code
  - ESLint with TypeScript plugin configured and enabled ✓
  - Clean Code principles applied:
    - Small, focused functions (single responsibility) ✓
    - Descriptive naming (NavigationBar, NavigationItem, isActive, etc.) ✓
    - Maximum 3 nesting levels ✓
    - DRY principle (shared navigation item styling, icon+label pattern) ✓
    - JSDoc documentation ONLY for complex functions (minimal docs needed) ✓
  - Violation justification: N/A - compliant

- [x] **Web Dependency Management**: Plan adds ZERO new dependencies
  - Existing dependency: react-icons v5.5.0 (already installed in package.json)
  - Rationale: Icon library already exists in project - no new dependency needed
  - Follows constitution's dependency minimization principle
  - No micro-dependencies added
  - Violation justification: N/A - compliant (actually exceeds standard by avoiding new dependencies)

- [x] **Web Business Logic Extraction**: N/A (minimal logic - navigation state is handled by React Router)
  - No custom hooks required (React Router provides `useLocation`, `useNavigate`)
  - No utility functions required (active state determined by URL matching)
  - Components remain thin (presentation only)
  - Violation justification: N/A - compliant

- [x] **Web TDD Workflow**: Plan follows Test-Driven Development (Red-Green-Refactor)
  - RED: Write failing component tests first (navigation rendering, active states, click handlers)
  - GREEN: Implement navigation component to pass tests
  - REFACTOR: Extract reusable NavigationItem subcomponent, optimize CSS
  - Tests written BEFORE implementation code
  - Violation justification: N/A - compliant

- [x] **Web Testing Strategy**: Plan includes comprehensive test coverage for `/webApp`
  - Component tests (Vitest + React Testing Library):
    - Location: `/src/components/__tests__/NavigationBar.test.tsx`
    - Coverage target: 80% line + branch coverage
    - Scope: Navigation rendering, active state logic, click interactions, responsive visibility
  - No unit tests needed (no custom hooks or lib functions)
  - All tests follow Given-When-Then structure with `// given`, `// when`, `// then` comments
  - Run commands: `npm test`, `npm test --coverage` (from webApp/)
  - Violation justification: N/A - compliant

- [x] **Backend Directory Structure**: N/A (backend not affected by this feature)

- [x] **Backend TDD Workflow**: N/A (backend not affected by this feature)

- [x] **Backend Testing Strategy**: N/A (backend not affected by this feature)

## Project Structure

### Documentation (this feature)

```text
specs/057-web-navigation/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output: Icon library decision, responsive strategy
├── data-model.md        # Phase 1 output: Navigation entities and state
├── quickstart.md        # Phase 1 output: Local dev setup and testing guide
├── contracts/           # Phase 1 output: React component interface definitions
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
webApp/
├── src/
│   ├── components/
│   │   ├── NavigationBar.tsx           # Main navigation component
│   │   ├── NavigationBar.module.css    # Navigation styles
│   │   ├── NavigationItem.tsx          # Individual navigation item subcomponent
│   │   ├── NavigationItem.module.css   # Navigation item styles
│   │   └── __tests__/
│   │       └── NavigationBar.test.tsx  # Component unit tests
│   ├── routes/
│   │   ├── index.tsx                   # Route configuration with NavigationBar
│   │   ├── Home.tsx                    # Landing page (/) - NEEDS IMPLEMENTATION
│   │   ├── LostPets.tsx                # Lost pet list (/lost-pets) - MOVE FROM ROOT
│   │   ├── FoundPets.tsx               # Found pet list (/found-pets) - CREATE PLACEHOLDER
│   │   ├── Contact.tsx                 # Contact page (/contact) - CREATE PLACEHOLDER
│   │   └── Account.tsx                 # Account page (/account) - CREATE PLACEHOLDER
│   └── App.tsx                         # Root app component (integrate NavigationBar)
└── package.json                        # Add icon library dependency

e2e-tests/java/
└── src/
    ├── test/
    │   ├── resources/features/web/
    │   │   └── 057-navigation.feature  # Gherkin scenarios for navigation
    │   └── java/.../pages/
    │       └── NavigationPage.java     # Page Object Model for navigation bar
    └── pom.xml                         # Maven dependencies (already configured)
```

**Structure Decision**: Web application structure (Option 2 frontend). This feature only affects the `/webApp` module. The navigation bar will be a reusable component in `/webApp/src/components/` with corresponding route configurations in `/webApp/src/routes/`. E2E tests follow the Java/Maven/Selenium structure established in constitution Principle XII.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No violations. All constitution checks passed.

---

## Phase Completion Status

### Phase 0: Research ✓ COMPLETE

**Artifacts Generated**:
- `research.md` - All technical unknowns resolved

**Key Decisions**:
1. Icon Library: react-icons v5.5.0 (already installed - zero new dependencies)
2. Responsive Strategy: CSS media queries (simplicity and performance)
3. Route Structure: Placeholder components for all destinations
4. Active State: React Router's NavLink component (built-in functionality)

**NEEDS CLARIFICATION Items Resolved**: ✓ All resolved (icon library - use existing react-icons)

---

### Phase 1: Design & Contracts ✓ COMPLETE

**Artifacts Generated**:
- `data-model.md` - Entity definitions and state management
- `contracts/NavigationBar.types.ts` - TypeScript component interfaces
- `contracts/routes.types.ts` - Route configuration types
- `quickstart.md` - Local dev setup and testing guide
- Agent context updated via `.specify/scripts/bash/update-agent-context.sh cursor-agent`

**Constitution Check Re-Evaluation**: ✓ ALL CHECKS PASSING

Post-design verification:
- [x] Web Technology Stack: React 18, TypeScript, Vite, Vitest ✓
- [x] Web Code Quality: ESLint, Clean Code principles ✓
- [x] Web Dependency Management: Only Lucide React added (justified) ✓
- [x] Web Business Logic Extraction: Minimal logic, React Router handles state ✓
- [x] Web TDD Workflow: Test-first approach documented in quickstart ✓
- [x] Web Testing Strategy: 80% coverage target, Given-When-Then structure ✓
- [x] Test Identifiers: All interactive elements have data-testid ✓
- [x] E2E Tests: Gherkin scenarios planned in quickstart ✓

---

### Phase 2: Task Breakdown (DEFERRED to /speckit.tasks command)

This planning phase stops here as per workflow specification. Next steps:

1. Review generated artifacts:
   - `plan.md` (this file)
   - `research.md`
   - `data-model.md`
   - `contracts/*.ts`
   - `quickstart.md`

2. Run `/speckit.tasks` command to break plan into atomic implementation tasks

3. Execute implementation following TDD workflow (Red-Green-Refactor)

---

## Summary

**Implementation Plan Complete**: All planning phases (0-1) finished successfully.

**Branch**: `057-web-navigation` (ready for implementation)

**Implementation Path**: `/webApp` module only (Web-only feature)

**Key Deliverables**:
- NavigationBar component (React + TypeScript + CSS Modules)
- Route configuration (React Router v6)
- 5 placeholder route components (Home, LostPets, FoundPets, Contact, Account)
- Component tests (Vitest + React Testing Library, 80% coverage target)
- E2E tests (Java 21 + Maven + Selenium + Cucumber)

**Dependencies Added**: NONE (using existing `react-icons` v5.5.0)

**Estimated Complexity**: Low-Medium
- Straightforward React component with React Router integration
- Minimal state management (URL-driven)
- CSS-only responsive behavior
- Well-defined test scenarios

**Ready for Implementation**: ✓ YES - All technical unknowns resolved, contracts defined, testing strategy documented
