# Implementation Plan: User Registration Endpoint

**Branch**: `055-user-registration` | **Date**: December 15, 2025 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/055-user-registration/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Backend-only feature adding user registration endpoint at `POST /api/v1/users`. Accepts email and password (8-128 chars), stores user with UUID, hashed password (scrypt), and timestamps in database. Returns HTTP 201 with user ID on success, HTTP 409 for duplicate emails, HTTP 400 for validation errors. Uses existing scrypt implementation, structured error responses, and database unique constraints for race condition handling.

## Technical Context

**Language/Version**: TypeScript with Node.js v24 (LTS)
**Primary Dependencies**: Express.js, Knex (query builder), existing scrypt implementation, crypto (UUID generation)
**Storage**: SQLite (development) → PostgreSQL (production migration path)
**Testing**: Vitest + SuperTest (integration tests), Vitest (unit tests)
**Target Platform**: Node.js backend server
**Project Type**: Backend module (affects `/server` only)
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)
**Constraints**: Must reuse existing scrypt implementation, must follow existing error response format, email max 254 chars (RFC 5321), password 8-128 chars
**Scale/Scope**: Single endpoint, single table (user), estimated ~200 LOC

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is a backend-only feature affecting only `/server` module. Frontend-related checks are marked N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: N/A - Backend-only feature, no frontend platforms affected

- [x] **Android MVI Architecture**: N/A - Backend-only feature

- [x] **iOS MVVM-C Architecture**: N/A - Backend-only feature

- [x] **Interface-Based Design**: Compliant
  - Repository interface for user data access
  - Implementation in `/server/src/database/repositories/`
  - Service layer references interface, not concrete implementation

- [x] **Dependency Injection**: Compliant
  - Backend uses manual DI (constructor injection, factory functions)
  - Repository injected into service layer
  - Service layer injected into route handlers

- [x] **80% Test Coverage - Platform-Specific**: Compliant
  - Backend unit tests in `/server/src/services/__test__/`, `/server/src/lib/__test__/`
  - Backend integration tests in `/server/src/__test__/`
  - Coverage target: 80% line + branch coverage
  - Run: `npm test --coverage` (from server/)

- [x] **End-to-End Tests**: N/A - Backend-only feature, no UI flows

- [x] **Asynchronous Programming Standards**: Compliant
  - Backend uses native `async`/`await` (Express async handlers)
  - No Promises chains, no callbacks for new code

- [x] **Test Identifiers for UI Controls**: N/A - Backend-only feature, no UI

- [x] **Public API Documentation**: Compliant
  - JSDoc format (`/** ... */`) for all public APIs
  - Concise and high-level (1-3 sentences: WHAT/WHY, not HOW)
  - Document only when purpose is not clear from name alone

- [x] **Given-When-Then Test Structure**: Compliant
  - All tests follow Given-When-Then convention
  - Comments mark test phases in complex tests
  - Descriptive test names for unit and integration tests

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: Compliant
  - Runtime: Node.js v24 (LTS)
  - Framework: Express.js
  - Language: TypeScript with strict mode enabled
  - Database: Knex query builder + SQLite (designed for PostgreSQL migration)

- [x] **Backend Code Quality**: Compliant
  - ESLint with TypeScript plugin configured and enabled
  - Clean Code principles:
    - Small, focused functions (single responsibility)
    - Descriptive naming (avoid unclear abbreviations)
    - Maximum 3 nesting levels
    - DRY principle (extract reusable logic to /lib)
    - JSDoc documentation for all public APIs

- [x] **Backend Dependency Management**: Compliant
  - No new dependencies required (uses existing scrypt, Knex, Express)
  - crypto (built-in) for UUID generation
  - Regular `npm audit` security checks planned

### Web Architecture & Quality Standards (if `/webApp` affected)

- [x] **Web Technology Stack**: N/A - Backend-only feature

- [x] **Web Code Quality**: N/A - Backend-only feature

- [x] **Web Dependency Management**: N/A - Backend-only feature

- [x] **Web Business Logic Extraction**: N/A - Backend-only feature

- [x] **Web TDD Workflow**: N/A - Backend-only feature

- [x] **Web Testing Strategy**: N/A - Backend-only feature

- [x] **Backend Directory Structure**: Compliant
  - `/middlewares/` - Existing error handler, logger (reused)
  - `/routes/` - New user routes in `/server/src/routes/users.ts`
  - `/services/` - New user service in `/server/src/services/user-service.ts`
  - `/database/` - New migration, repository in `/server/src/database/`
  - `/lib/` - Validators (email validation exists, add password validation)
  - `/__test__/` - Integration tests for POST /api/v1/users
  - `/services/__test__/` - Unit tests for user service
  - `/lib/__test__/` - Unit tests for validators (password validation)

- [x] **Backend TDD Workflow**: Compliant
  - RED: Write failing test first
  - GREEN: Write minimal code to pass test
  - REFACTOR: Improve code quality without changing behavior
  - Tests written BEFORE implementation code

- [x] **Backend Testing Strategy**: Compliant
  - Unit tests (Vitest):
    - Location: `/server/src/services/__test__/user-service.test.ts`, `/server/src/lib/__test__/validators.test.ts`
    - Coverage target: 80% line + branch coverage
    - Scope: User registration logic, password validation (email validation already tested)
  - Integration tests (Vitest + SuperTest):
    - Location: `/server/src/__test__/user-registration.test.ts`
    - Coverage target: 80% for POST /api/v1/users endpoint
    - Scope: Full HTTP request → response cycle
  - All tests follow Given-When-Then structure
  - Run commands: `npm test`, `npm test --coverage`

## Project Structure

### Documentation (this feature)

```text
specs/055-user-registration/
├── spec.md                           # Feature specification (already exists)
├── plan.md                          # This file (/speckit.plan output)
├── research.md                      # Phase 0 output (to be created)
├── data-model.md                    # Phase 1 output (to be created)
├── quickstart.md                    # Phase 1 output (to be created)
├── contracts/                       # Phase 1 output (to be created)
│   └── post-users.yaml             # OpenAPI spec for POST /api/v1/users
├── checklists/
│   └── requirements.md             # Requirements checklist (already exists)
└── tasks.md                         # Phase 2 output (/speckit.tasks - not created yet)
```

### Source Code (repository root)

```text
server/
├── src/
│   ├── routes/
│   │   └── users.ts               # NEW: POST /api/v1/users route definition
│   ├── services/
│   │   ├── user-service.ts        # NEW: User registration business logic
│   │   └── __test__/
│   │       └── user-service.test.ts  # NEW: Unit tests for user service
│   ├── database/
│   │   ├── migrations/
│   │   │   └── YYYYMMDDHHMMSS_create_user_table.ts  # NEW: User table migration
│   │   └── repositories/
│   │       └── user-repository.ts # NEW: Interface + Knex implementation
│   ├── lib/
│   │   ├── validators.ts          # MODIFIED: Add password validation (email validation exists)
│   │   ├── password-management.ts # EXISTING: Scrypt implementation (reuse)
│   │   └── __test__/
│   │       └── validators.test.ts # MODIFIED: Add password validation tests
│   ├── middlewares/
│   │   └── error-handler-middleware.ts  # EXISTING: Reuse for error responses
│   ├── __test__/
│   │   └── user-registration.test.ts    # NEW: Integration tests
│   └── app.ts                     # MODIFIED: Register user routes
├── knexfile.ts                    # EXISTING: Database configuration
└── package.json                   # EXISTING: No new dependencies needed
```

**Structure Decision**: Backend-only feature in `/server` module. Single table (`user`), single endpoint (`POST /api/v1/users`), standard three-layer architecture (routes → services → repositories). Validation consolidated in existing `validators.ts`, repository interface + implementation co-located in single file.

## Complexity Tracking

> **No violations - all Constitution checks pass**

No complexity violations. Feature follows all architectural principles:
- Backend-only (no cross-platform concerns)
- Standard Express.js patterns
- TDD workflow with 80% coverage
- Manual DI (constructor injection)
- Existing scrypt implementation reused
- Clean Code principles applied
