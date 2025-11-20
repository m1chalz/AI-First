# Implementation Plan: Lost Pets API Endpoint

**Branch**: `006-pets-api` | **Date**: 2025-11-19 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/006-pets-api/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implement a public REST API endpoint `GET /api/v1/announcements` that returns all lost pet announcements from a SQLite database. The endpoint must return JSON responses with HTTP 200 for successful requests (including empty results) and HTTP 500 for database failures. Each announcement includes pet details (name, species, breed, gender, description), location information (last seen location with optional radius), temporal data (last seen date in ISO 8601 format), contact information (email, phone), optional photo URL, and announcement status (ACTIVE, FOUND, CLOSED). The implementation follows TDD workflow with 80% test coverage for both unit tests (business logic) and integration tests (API endpoints).

## Technical Context

**Language/Version**: TypeScript with Node.js v24 (LTS), strict mode enabled  
**Primary Dependencies**: Express.js (web framework), Knex (query builder), SQLite3 (development database), Vitest (test runner), SuperTest (API testing), ESLint with TypeScript plugin  
**Storage**: SQLite (initial phase), designed for easy migration to PostgreSQL using Knex migrations  
**Testing**: Vitest for unit tests (`/src/services/__test__/`, `/src/lib/__test__/`) and integration tests (`/src/__test__/`) with SuperTest for HTTP assertions  
**Target Platform**: Node.js v24 server (backend REST API)
**Project Type**: Backend API (single service in `/server` directory, NOT part of KMP shared module)  
**Performance Goals**: Response time <2 seconds for datasets up to 1000 announcements (per SC-001)  
**Constraints**: Public endpoint (no authentication), graceful database error handling with HTTP 500, ignore unexpected query parameters, return null for missing optional fields  
**Scale/Scope**: Initial implementation with 5-10 seed announcements, single GET endpoint, no filtering/pagination/search in this phase

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### KMP Architecture Compliance

- [x] **Thin Shared Layer**: Feature design keeps `/shared` limited to domain models, repository interfaces, and use cases
  - No UI components in `/shared`
  - No ViewModels in `/shared`
  - No platform-specific code in `commonMain`
  - Violation justification: _N/A - Backend-only feature, does not touch `/shared` module_

- [x] **Native Presentation**: Each platform implements its own presentation layer
  - Android ViewModels in `/composeApp`
  - iOS ViewModels in Swift in `/iosApp`
  - Web state management in React in `/webApp`
  - Violation justification: _N/A - Backend-only feature, no presentation layer_

- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - Single `StateFlow<UiState>` source of truth with immutable data classes
  - Sealed `UserIntent` and optional `UiEffect` types co-located with feature packages
  - Reducers implemented as pure functions (no side effects) and unit-tested
  - `dispatchIntent` entry wired from UI → ViewModel → reducer, with effects delivered via `SharedFlow`
  - Violation justification: _N/A - Backend-only feature, no Android UI_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories
  - Repository interfaces in `/shared/src/commonMain/.../repositories/`
  - Implementations in platform-specific modules
  - Use cases reference interfaces, not concrete implementations
  - Violation justification: _N/A - Backend uses repository pattern within `/server`, not `/shared`. Backend repositories follow interface-based design per Backend Architecture standards._

- [x] **Dependency Injection**: Plan includes Koin setup for all platforms
  - Shared domain module defined in `/shared/src/commonMain/.../di/`
  - Android DI modules in `/composeApp/src/androidMain/.../di/`
  - iOS Koin initialization in `/iosApp/iosApp/DI/`
  - Web DI setup (if applicable) in `/webApp/src/di/`
  - Violation justification: _N/A - Backend module (`/server`) does not use Koin (as specified in constitution), uses native TypeScript DI patterns_

- [x] **80% Test Coverage - Shared Module**: Plan includes unit tests for shared domain logic
  - Tests located in `/shared/src/commonTest`
  - Coverage target: 80% line + branch coverage
  - Run command: `./gradlew :shared:test koverHtmlReport`
  - Tests use Koin Test for DI in tests
  - Violation justification: _N/A - Backend-only feature, does not touch `/shared` module_

- [x] **80% Test Coverage - ViewModels**: Plan includes unit tests for ViewModels on each platform
  - Android: Tests in `/composeApp/src/androidUnitTest/`, run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
  - iOS: Tests in `/iosApp/iosAppTests/ViewModels/`, run via XCTest
  - Web: Tests in `/webApp/src/__tests__/hooks/`, run `npm test -- --coverage`
  - Coverage target: 80% line + branch coverage per platform
  - Violation justification: _N/A - Backend-only feature, no ViewModels_

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: Playwright tests in `/e2e-tests/web/specs/[feature-name].spec.ts`
  - Mobile: Appium tests in `/e2e-tests/mobile/specs/[feature-name].spec.ts`
  - All tests written in TypeScript
  - Page Object Model / Screen Object Model used
  - Each user story has at least one E2E test
  - Violation justification: _N/A - Backend API endpoint, tested via integration tests. E2E tests will be added when frontend consumes this API._

- [x] **Platform Independence**: Shared code uses expect/actual for platform dependencies
  - No direct UIKit/Android SDK/Browser API imports in `commonMain`
  - Platform-specific implementations in `androidMain`, `iosMain`, `jsMain`
  - Repository implementations provided via DI, not expect/actual
  - Violation justification: _N/A - Backend-only feature, platform independence handled by Node.js runtime_

- [x] **Clear Contracts**: Repository interfaces and use cases have explicit APIs
  - Typed return values (`Result<T>`, sealed classes)
  - KDoc documentation for public APIs
  - `@JsExport` for web consumption where needed
  - Violation justification: _Compliant - REST API contract defined via OpenAPI, TypeScript types for domain models, JSDoc for all public functions_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Shared: Kotlin Coroutines with `suspend` functions
  - Android: Kotlin Coroutines (`viewModelScope`) + Flow for state
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor`
  - Web: Native `async`/`await` (no Promise chains)
  - No Combine, RxJava, RxSwift, or callback-based patterns for new code
  - Violation justification: _Compliant - Backend uses native `async`/`await` for all async operations (Express async handlers, repository methods, service functions)_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: `testTag` modifier on all interactive composables
  - iOS: `accessibilityIdentifier` modifier on all interactive views
  - Web: `data-testid` attribute on all interactive elements
  - Naming convention: `{screen}.{element}.{action}` (e.g., `petList.addButton.click`)
  - List items use stable IDs (e.g., `petList.item.${id}`)
  - Violation justification: _N/A - Backend-only feature, no UI elements_

- [x] **Public API Documentation**: Plan ensures all public APIs have documentation
  - Kotlin: KDoc format (`/** ... */`)
  - Swift: SwiftDoc format (`/// ...`)
  - TypeScript: JSDoc format (`/** ... */`)
  - Documentation must be concise and high-level (1-3 sentences: WHAT/WHY, not HOW)
  - All public classes, methods, and properties documented
  - Violation justification: _Compliant - All service functions, repository interfaces, and API route handlers will have JSDoc documentation_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then)
  - ViewModel tests use Given-When-Then pattern with descriptive names
  - E2E tests structure scenarios with Given-When-Then phases
  - Test names follow platform conventions (backticks for Kotlin, camelCase_with_underscores for Swift, descriptive strings for TypeScript)
  - Comments mark test phases in complex tests
  - Violation justification: _Compliant - All backend tests (unit + integration) follow Given-When-Then structure with descriptive test names_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: Plan uses modern Node.js stack for `/server` module
  - Runtime: Node.js v24 (LTS)
  - Framework: Express.js
  - Language: TypeScript with strict mode enabled
  - Database: Knex query builder + SQLite (designed for PostgreSQL migration)
  - Violation justification: _Compliant - Using specified stack (Node v24, Express, TypeScript strict mode, Knex + SQLite with PostgreSQL-compatible schema)_

- [x] **Backend Code Quality**: Plan enforces quality standards for `/server` code
  - ESLint with TypeScript plugin configured and enabled
  - Clean Code principles applied:
    - Small, focused functions (single responsibility)
    - Descriptive naming (avoid unclear abbreviations)
    - Maximum 3 nesting levels
    - DRY principle (extract reusable logic)
    - JSDoc documentation for all public APIs
  - Violation justification: _Compliant - All service functions, repositories, and route handlers will follow Clean Code principles with JSDoc_

- [x] **Backend Dependency Management**: Plan minimizes dependencies in `/server/package.json`
  - Only add dependencies providing significant value
  - Prefer well-maintained, security-audited packages
  - Avoid micro-dependencies (e.g., "is-even", "left-pad")
  - Document rationale for each dependency in comments
  - Regular `npm audit` security checks planned
  - Violation justification: _Compliant - Minimal new dependencies. Feature uses existing dependencies (Express, Knex, SQLite3, Vitest, SuperTest). UUID generation uses built-in crypto module. May add validator.js (~5KB) for robust email/phone validation if team decides external library is worthwhile._

- [x] **Backend Directory Structure**: Plan follows standardized layout in `/server/src/`
  - `/middlewares/` - Express middlewares (auth, logging, error handling)
  - `/routes/` - REST API endpoint definitions (Express routers)
  - `/services/` - Business logic layer (testable, pure functions)
  - `/database/` - Database config, migrations, query repositories
  - `/lib/` - Utility functions, helpers (pure, reusable)
  - `/__test__/` - Integration tests for REST API endpoints
  - `app.ts` - Express app configuration
  - `index.ts` - Server entry point
  - Violation justification: _Compliant - Implementation will follow structure:_
    - `/src/routes/announcements.ts` - Express router for announcements endpoint
    - `/src/services/announcement-service.ts` - Business logic for announcements (with `/src/services/__test__/announcement-service.test.ts`)
    - `/src/database/repositories/announcement-repository.ts` - Database queries via Knex
    - `/src/database/migrations/YYYYMMDDHHMMSS_create_announcement_table.ts` - Schema migration
    - `/src/__test__/announcements.test.ts` - API integration tests with SuperTest
    - `/src/lib/validators.ts` - Email/phone validation (with `/src/lib/__test__/validators.test.ts`)

- [x] **Backend TDD Workflow**: Plan follows Test-Driven Development (Red-Green-Refactor)
  - RED: Write failing test first
  - GREEN: Write minimal code to pass test
  - REFACTOR: Improve code quality without changing behavior
  - Tests written BEFORE implementation code
  - Violation justification: _Compliant - Implementation will follow TDD workflow:_
    1. Write failing integration test for GET /api/v1/announcements
    2. Write failing unit tests for announcement service functions
    3. Implement minimal service/repository code to pass tests
    4. Refactor for Clean Code compliance (extract reusable logic, improve naming, reduce nesting)

- [x] **Backend Testing Strategy**: Plan includes comprehensive test coverage for `/server`
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
  - Violation justification: _Compliant - Test plan:_
    - Unit tests:
      - `/src/services/__test__/announcement-service.test.ts` - Test getAllAnnouncements with fake repository (empty DB, data exists)
      - `/src/lib/__test__/validators.test.ts` - Test email/phone validation (valid cases, invalid cases, edge cases)
    - Integration tests:
      - `/src/__test__/announcements.test.ts` - Test GET /api/v1/announcements (200 with data, 200 empty array)
      - **HTTP 500 tests skipped** for this phase (focus on happy path)
    - All tests use Given-When-Then structure with descriptive names
    - Coverage command: `npm test -- --coverage` (run from /server)

## Project Structure

### Documentation (this feature)

```text
specs/006-pets-api/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
│   └── announcements-api.yaml  # OpenAPI 3.0 schema
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
server/
├── src/
│   ├── routes/
│   │   ├── index.ts                      # Existing - register announcements router
│   │   └── announcements.ts              # NEW - GET /api/v1/announcements route handler
│   ├── services/
│   │   ├── __test__/
│   │   │   └── announcement-service.test.ts  # NEW - Unit tests for service
│   │   └── announcement-service.ts       # NEW - Business logic for announcements
│   ├── database/
│   │   ├── repositories/
│   │   │   └── announcement-repository.ts  # NEW - Knex queries for announcement table
│   │   ├── migrations/
│   │   │   └── YYYYMMDDHHMMSS_create_announcement_table.ts  # NEW - Create announcement table
│   │   └── seeds/
│   │       └── 001_announcements.ts        # NEW - Seed data (5-10 example announcements)
│   ├── lib/
│   │   ├── __test__/
│   │   │   └── validators.test.ts        # NEW - Unit tests for validators
│   │   └── validators.ts                 # NEW - Email/phone validation utilities
│   ├── types/
│   │   └── announcement.d.ts             # NEW - TypeScript types for Announcement
│   ├── __test__/
│   │   └── announcements.test.ts         # NEW - API integration tests (SuperTest)
│   ├── app.ts                            # Existing - mount announcements router
│   └── index.ts                          # Existing - no changes
├── pets.db                               # SQLite database file (includes announcement table after migration)
└── package.json                          # Existing - no new dependencies
```

**Structure Decision**: Backend API feature using existing `/server` Node.js/Express module. Follows standardized backend architecture:
- Route handler in `/routes/announcements.ts` (thin, handles HTTP concerns only)
- Business logic in `/services/announcement-service.ts` (testable, framework-agnostic)
- Database queries in `/database/repositories/announcement-repository.ts` (Knex query builder)
- Schema defined in Knex migration (`create_announcement_table.ts`) - uses `IF NOT EXISTS`, no CHECK constraints
- Seed data in `/database/seeds/001_announcements.ts` (5-10 example announcements)
- Validation utilities in `/lib/validators.ts` (reusable, pure functions) - may use validator.js library
- Unit tests co-located with services and utilities (`__test__/` subdirectories)
- Integration tests in `/src/__test__/` (SuperTest for end-to-end API testing) - HTTP 500 tests skipped

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No violations. All constitution requirements are satisfied:
- Backend-only feature (KMP checks N/A)
- Backend Architecture & Quality Standards fully compliant
- TDD workflow with 80% test coverage (unit + integration)
- Clean Code principles enforced
- ESLint configured
- No new dependencies required
- Given-When-Then test structure
- JSDoc documentation for all public APIs
