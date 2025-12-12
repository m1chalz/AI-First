# Implementation Plan: [FEATURE]

**Branch**: `[###-feature-name]` | **Date**: [DATE] | **Spec**: [link]
**Input**: Feature specification from `/specs/[###-feature-name]/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

[Extract from feature spec: primary requirement + technical approach from research]

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: [e.g., Python 3.11, Swift 5.9, Rust 1.75 or NEEDS CLARIFICATION]  
**Primary Dependencies**: [e.g., FastAPI, UIKit, LLVM or NEEDS CLARIFICATION]  
**Storage**: [if applicable, e.g., PostgreSQL, CoreData, files or N/A]  
**Testing**: [e.g., pytest, XCTest, cargo test or NEEDS CLARIFICATION]  
**Target Platform**: [e.g., Linux server, iOS 15+, WASM or NEEDS CLARIFICATION]
**Project Type**: [single/web/mobile - determines source structure]  
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)  
**Constraints**: [Optional; specify if critical, e.g., offline-capable or N/A. Performance constraints are NOT applicable]  
**Scale/Scope**: [Optional for low-traffic projects; specify if known, e.g., 10k users, 1M LOC, 50 screens or N/A]

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: For backend-only features (affecting only `/server` module), you may mark frontend-related checks (Platform Independence, Android MVI, iOS MVVM-C, Test Identifiers for UI, E2E Tests for mobile/web) as N/A. Focus on Backend Architecture & Quality Standards checks.

### Platform Architecture Compliance

- [ ] **Platform Independence**: Each platform implements full stack independently
  - Android: Domain models, use cases, repositories, ViewModels in `/composeApp`
  - iOS: Domain models, use cases, repositories, ViewModels in `/iosApp`
  - Web: Domain models, services, state management in `/webApp`
  - Backend: Independent Node.js/Express API in `/server`
  - NO shared compiled code between platforms
  - Violation justification: _[Required if not compliant]_

- [ ] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - Single `StateFlow<UiState>` source of truth with immutable data classes
  - Sealed `UserIntent` and optional `UiEffect` types co-located with feature packages
  - Reducers implemented as pure functions (no side effects) and unit-tested
  - `dispatchIntent` entry wired from UI → ViewModel → reducer, with effects delivered via `SharedFlow`
  - Navigation MUST use Jetpack Navigation Component (androidx.navigation:navigation-compose)
  - Navigation graph defined with `NavHost` composable
  - ViewModels trigger navigation via `UiEffect`, not direct `NavController` calls
  - Composable screens follow two-layer pattern: state host (stateful) + stateless content composable
  - Stateless composables MUST have `@Preview` with `@PreviewParameter` using custom `PreviewParameterProvider<UiState>`
  - Callback lambdas MUST be defaulted to no-ops in stateless composables
  - Previews focus on light mode only (no dark mode previews required)
  - Violation justification: _[Required if Android diverges from MVI, Navigation Component, or Composable pattern]_

- [ ] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - UIKit-based coordinators manage navigation and create `UIHostingController` instances
  - ViewModels conform to `ObservableObject` with `@Published` properties
  - ViewModels communicate with coordinators via methods or closures
  - SwiftUI views observe ViewModels (no business/navigation logic in views)
  - Violation justification: _[Required if iOS diverges from MVVM-C]_

- [ ] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Android: Repository interfaces in `/composeApp/src/androidMain/.../domain/repositories/`
  - iOS: Repository protocols in `/iosApp/iosApp/Domain/Repositories/`
  - Web: Service interfaces in `/webApp/src/services/`
  - Backend: Repository interfaces in `/server/src/database/repositories/`
  - Implementations in platform-specific data/repositories modules
  - Use cases reference interfaces, not concrete implementations
  - Violation justification: _[Required if not compliant]_

- [ ] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: MUST use Koin - DI modules in `/composeApp/src/androidMain/.../di/`
  - iOS: MUST use manual DI - setup in `/iosApp/iosApp/DI/` (ServiceContainer with constructor injection)
  - Web: SHOULD use React Context - setup in `/webApp/src/di/`
  - Backend: Manual DI in `/server/src/` (constructor injection, factory functions)
  - Violation justification: _[Required if DI not using specified approach]_

- [ ] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: Tests in `/composeApp/src/androidUnitTest/`, run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
  - iOS: Tests in `/iosApp/iosAppTests/`, run via XCTest
  - Web: Tests in `/webApp/src/hooks/__test__/`, `/webApp/src/lib/__test__/`, run `npm test --coverage`
  - Backend: Tests in `/server/src/services/__test__/`, `/server/src/lib/__test__/`, `/server/src/__test__/`, run `npm test --coverage`
  - Coverage target: 80% line + branch coverage per platform
  - Violation justification: _[Required if coverage < 80%]_

- [ ] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: Playwright tests in `/e2e-tests/web/specs/[feature-name].spec.ts`
  - Mobile: Appium tests in `/e2e-tests/mobile/specs/[feature-name].spec.ts`
  - All tests written in TypeScript
  - Page Object Model / Screen Object Model used
  - Each user story has at least one E2E test
  - Violation justification: _[Required if E2E tests missing]_

- [ ] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: Kotlin Coroutines (`viewModelScope`) + Flow for state
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor`
  - Web: Native `async`/`await` (no Promise chains)
  - Backend: Native `async`/`await` (Express async handlers)
  - No Combine, RxJava, RxSwift, or callback-based patterns for new code
  - Violation justification: _[Required if using prohibited patterns]_

- [ ] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: `testTag` modifier on all interactive composables
  - iOS: `accessibilityIdentifier` modifier on all interactive views
  - Web: `data-testid` attribute on all interactive elements
  - Naming convention: `{screen}.{element}.{action}` (e.g., `petList.addButton.click`)
  - List items use stable IDs (e.g., `petList.item.${id}`)
  - Violation justification: _[Required if testIDs missing]_

- [ ] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Kotlin: KDoc format (`/** ... */`)
  - Swift: SwiftDoc format (`/// ...`)
  - TypeScript: JSDoc format (`/** ... */`)
  - Documentation must be concise and high-level (1-3 sentences: WHAT/WHY, not HOW)
  - Document only when purpose is not clear from name alone
  - Skip documentation for self-explanatory methods, variables, and constants
  - Violation justification: _[Required if complex APIs lack documentation]_

- [ ] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then)
  - ViewModel tests use Given-When-Then pattern with descriptive names
  - E2E tests structure scenarios with Given-When-Then phases
  - Test names follow platform conventions (backticks for Kotlin, camelCase_with_underscores for Swift, descriptive strings for TypeScript)
  - Comments mark test phases in complex tests
  - Violation justification: _[Required if tests don't follow convention]_

### Backend Architecture & Quality Standards (if `/server` affected)

- [ ] **Backend Technology Stack**: Plan uses modern Node.js stack for `/server` module
  - Runtime: Node.js v24 (LTS)
  - Framework: Express.js
  - Language: TypeScript with strict mode enabled
  - Database: Knex query builder + SQLite (designed for PostgreSQL migration)
  - Violation justification: _[Required if not compliant or N/A if /server not affected]_

- [ ] **Backend Code Quality**: Plan enforces quality standards for `/server` code
  - ESLint with TypeScript plugin configured and enabled
  - Clean Code principles applied:
    - Small, focused functions (single responsibility)
    - Descriptive naming (avoid unclear abbreviations)
    - Maximum 3 nesting levels
    - DRY principle (extract reusable logic)
    - JSDoc documentation for all public APIs
  - Violation justification: _[Required if not compliant or N/A if /server not affected]_

- [ ] **Backend Dependency Management**: Plan minimizes dependencies in `/server/package.json`
  - Only add dependencies providing significant value
  - Prefer well-maintained, security-audited packages
  - Avoid micro-dependencies (e.g., "is-even", "left-pad")
  - Document rationale for each dependency in comments
  - Regular `npm audit` security checks planned
  - Violation justification: _[Required if not compliant or N/A if /server not affected]_

### Web Architecture & Quality Standards (if `/webApp` affected)

- [ ] **Web Technology Stack**: Plan uses modern React 18 + TypeScript stack for `/webApp` module
  - Framework: React 18
  - Language: TypeScript with strict mode enabled
  - Build Tool: Vite
  - Testing: Vitest + React Testing Library
  - Violation justification: _[Required if not compliant or N/A if /webApp not affected]_

- [ ] **Web Code Quality**: Plan enforces quality standards for `/webApp` code
  - ESLint with TypeScript plugin configured and enabled
  - Clean Code principles applied:
    - Small, focused functions (single responsibility)
    - Descriptive naming (avoid unclear abbreviations)
    - Maximum 3 nesting levels
    - DRY principle (extract reusable logic)
    - JSDoc documentation ONLY for complex functions
  - Violation justification: _[Required if not compliant or N/A if /webApp not affected]_

- [ ] **Web Dependency Management**: Plan minimizes dependencies in `/webApp/package.json`
  - Only add dependencies providing significant value
  - Prefer well-maintained, security-audited packages
  - Avoid micro-dependencies (e.g., "is-even", "left-pad")
  - Document rationale for each dependency in comments
  - Regular `npm audit` security checks planned
  - Violation justification: _[Required if not compliant or N/A if /webApp not affected]_

- [ ] **Web Business Logic Extraction**: Plan ensures business logic is extracted to testable functions
  - Business logic in `/webApp/src/hooks/` (custom React hooks)
  - Pure utility functions in `/webApp/src/lib/` (framework-agnostic)
  - Components remain thin (presentation layer only)
  - All hooks and lib functions covered by unit tests
  - Violation justification: _[Required if business logic in components or N/A if /webApp not affected]_

- [ ] **Web TDD Workflow**: Plan follows Test-Driven Development (Red-Green-Refactor)
  - RED: Write failing test first
  - GREEN: Write minimal code to pass test
  - REFACTOR: Improve code quality without changing behavior
  - Tests written BEFORE implementation code
  - Violation justification: _[Required if not compliant or N/A if /webApp not affected]_

- [ ] **Web Testing Strategy**: Plan includes comprehensive test coverage for `/webApp`
  - Unit tests (Vitest):
    - Location: `/src/hooks/__test__/`, `/src/lib/__test__/`
    - Coverage target: 80% line + branch coverage
    - Scope: Business logic in hooks and lib functions
  - Component tests (Vitest + React Testing Library):
    - Location: `/src/components/.../__tests__/`
    - Scope: Component rendering and user interactions (recommended)
  - All tests follow Given-When-Then structure
  - Run commands: `npm test`, `npm test --coverage`
  - Violation justification: _[Required if coverage < 80% or N/A if /webApp not affected]_

- [ ] **Backend Directory Structure**: Plan follows standardized layout in `/server/src/`
  - `/middlewares/` - Express middlewares (auth, logging, error handling)
  - `/routes/` - REST API endpoint definitions (Express routers)
  - `/services/` - Business logic layer (testable, pure functions)
  - `/database/` - Database config, migrations, query repositories
  - `/lib/` - Utility functions, helpers (pure, reusable)
  - `/__test__/` - Integration tests for REST API endpoints
  - `app.ts` - Express app configuration
  - `index.ts` - Server entry point
  - Violation justification: _[Required if not compliant or N/A if /server not affected]_

- [ ] **Backend TDD Workflow**: Plan follows Test-Driven Development (Red-Green-Refactor)
  - RED: Write failing test first
  - GREEN: Write minimal code to pass test
  - REFACTOR: Improve code quality without changing behavior
  - Tests written BEFORE implementation code
  - Violation justification: _[Required if not compliant or N/A if /server not affected]_

- [ ] **Backend Testing Strategy**: Plan includes comprehensive test coverage for `/server`
  - Unit tests (Vitest):
    - Location: `/src/services/__test__/`, `/src/lib/__test__/`
    - Coverage target: 80% line + branch coverage
    - Scope: Business logic and utility functions
  - Integration tests (Vitest + SuperTest):
    - Location: `/src/__test__/`
    - Coverage target: 80% for API endpoints
    - Scope: REST API end-to-end (request → response)
  - All tests follow Given-When-Then structure
  - Run commands: `npm test`, `npm test --coverage`
  - Violation justification: _[Required if coverage < 80% or N/A if /server not affected]_

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
<!--
  ACTION REQUIRED: Replace the placeholder tree below with the concrete layout
  for this feature. Delete unused options and expand the chosen structure with
  real paths (e.g., apps/admin, packages/something). The delivered plan must
  not include Option labels.
-->

```text
# [REMOVE IF UNUSED] Option 1: Single project (DEFAULT)
src/
├── models/
├── services/
├── cli/
└── lib/

tests/
├── contract/
├── integration/
└── unit/

# [REMOVE IF UNUSED] Option 2: Web application (when "frontend" + "backend" detected)
backend/
├── src/
│   ├── models/
│   ├── services/
│   └── api/
└── tests/

frontend/
├── src/
│   ├── components/
│   ├── pages/
│   └── services/
└── tests/

# [REMOVE IF UNUSED] Option 3: Mobile + API (when "iOS/Android" detected)
api/
└── [same as backend above]

ios/ or android/
└── [platform-specific structure: feature modules, UI flows, platform tests]
```

**Structure Decision**: [Document the selected structure and reference the real
directories captured above]

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., 4th project] | [current need] | [why 3 projects insufficient] |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient] |
