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
**Performance Goals**: [Optional for low-traffic projects; specify if needed, e.g., 1000 req/s, 10k lines/sec, 60 fps or N/A]  
**Constraints**: [Optional for low-traffic projects; specify if critical, e.g., <200ms p95, <100MB memory, offline-capable or N/A]  
**Scale/Scope**: [Optional for low-traffic projects; specify if known, e.g., 10k users, 1M LOC, 50 screens or N/A]

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: For backend-only features (affecting only `/server` module), you may mark frontend-related checks (Thin Shared Layer, Native Presentation, Android MVI, Test Identifiers for UI, E2E Tests for mobile/web) as N/A. Focus on Backend Architecture & Quality Standards checks.

### KMP Architecture Compliance

- [ ] **Thin Shared Layer**: Feature design keeps `/shared` limited to domain models, repository interfaces, and use cases
  - No UI components in `/shared`
  - No ViewModels in `/shared`
  - No platform-specific code in `commonMain`
  - Violation justification: _[Required if not compliant]_

- [ ] **Native Presentation**: Each platform implements its own presentation layer
  - Android ViewModels in `/composeApp`
  - iOS ViewModels in Swift in `/iosApp`
  - Web state management in React in `/webApp`
  - Violation justification: _[Required if not compliant]_

- [ ] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - Single `StateFlow<UiState>` source of truth with immutable data classes
  - Sealed `UserIntent` and optional `UiEffect` types co-located with feature packages
  - Reducers implemented as pure functions (no side effects) and unit-tested
  - `dispatchIntent` entry wired from UI → ViewModel → reducer, with effects delivered via `SharedFlow`
  - Violation justification: _[Required if Android diverges from MVI]_

- [ ] **Interface-Based Design**: Domain logic uses interfaces for repositories
  - Repository interfaces in `/shared/src/commonMain/.../repositories/`
  - Implementations in platform-specific modules
  - Use cases reference interfaces, not concrete implementations
  - Violation justification: _[Required if not compliant]_

- [ ] **Dependency Injection**: Plan includes Koin setup for all platforms
  - Shared domain module defined in `/shared/src/commonMain/.../di/`
  - Android DI modules in `/composeApp/src/androidMain/.../di/`
  - iOS Koin initialization in `/iosApp/iosApp/DI/`
  - Web DI setup (if applicable) in `/webApp/src/di/`
  - Violation justification: _[Required if DI not using Koin]_

- [ ] **80% Test Coverage - Shared Module**: Plan includes unit tests for shared domain logic
  - Tests located in `/shared/src/commonTest`
  - Coverage target: 80% line + branch coverage
  - Run command: `./gradlew :shared:test koverHtmlReport`
  - Tests use Koin Test for DI in tests
  - Violation justification: _[Required if coverage < 80%]_

- [ ] **80% Test Coverage - ViewModels**: Plan includes unit tests for ViewModels on each platform
  - Android: Tests in `/composeApp/src/androidUnitTest/`, run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
  - iOS: Tests in `/iosApp/iosAppTests/ViewModels/`, run via XCTest
  - Web: Tests in `/webApp/src/__tests__/hooks/`, run `npm test -- --coverage`
  - Coverage target: 80% line + branch coverage per platform
  - Violation justification: _[Required if coverage < 80%]_

- [ ] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: Playwright tests in `/e2e-tests/web/specs/[feature-name].spec.ts`
  - Mobile: Appium tests in `/e2e-tests/mobile/specs/[feature-name].spec.ts`
  - All tests written in TypeScript
  - Page Object Model / Screen Object Model used
  - Each user story has at least one E2E test
  - Violation justification: _[Required if E2E tests missing]_

- [ ] **Platform Independence**: Shared code uses expect/actual for platform dependencies
  - No direct UIKit/Android SDK/Browser API imports in `commonMain`
  - Platform-specific implementations in `androidMain`, `iosMain`, `jsMain`
  - Repository implementations provided via DI, not expect/actual
  - Violation justification: _[Required if not compliant]_

- [ ] **Clear Contracts**: Repository interfaces and use cases have explicit APIs
  - Typed return values (`Result<T>`, sealed classes)
  - KDoc documentation for public APIs
  - `@JsExport` for web consumption where needed
  - Violation justification: _[Required if not compliant]_

- [ ] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Shared: Kotlin Coroutines with `suspend` functions
  - Android: Kotlin Coroutines (`viewModelScope`) + Flow for state
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor`
  - Web: Native `async`/`await` (no Promise chains)
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
  - Run commands: `npm test`, `npm test -- --coverage`
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
