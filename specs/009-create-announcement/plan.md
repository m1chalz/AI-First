# Implementation Plan: Create Announcement Endpoint

**Branch**: `009-create-announcement` | **Date**: 2025-11-24 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/009-create-announcement/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implement a public POST endpoint (`/api/v1/announcements`) for creating pet announcements (lost/found pet listings) with comprehensive validation, security features (XSS prevention, password hashing, PII redaction), and fail-fast error handling. The endpoint accepts pet details, location coordinates, contact information, and generates a one-time management password for future announcement management operations.

**Technical Approach**:
- Backend-only feature (Node.js + Express + TypeScript)
- SQLite database with migration path to PostgreSQL
- Input validation using `Zod` (TypeScript-first schema validation)
- Text sanitization using `xss` (XSS prevention)
- Password hashing using Node.js built-in `scrypt` (no external dependencies)
- Fail-fast validation with field-specific error codes
- 80% test coverage (unit tests for services/lib, integration tests for API endpoint)

## Technical Context

**Language/Version**: TypeScript (strict mode) on Node.js v24 (LTS)  
**Primary Dependencies**: Express.js, Knex (query builder), Zod (validation), xss (sanitization), Node.js crypto (scrypt)  
**Storage**: SQLite (development) → PostgreSQL (production migration path)  
**Testing**: Vitest (unit tests) + SuperTest (API integration tests)  
**Target Platform**: Linux/macOS server (Node.js backend)
**Project Type**: Backend API (single module: `/server`)  
**Performance Goals**: < 2 seconds response time for announcement creation (SC-001)  
**Constraints**: 10 MB maximum request payload, fail-fast validation (first error only), 80% test coverage  
**Scale/Scope**: Low-traffic MVP (performance optimizations intentionally skipped - no database indexes, no query optimization)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is a backend-only feature affecting `/server` module. Frontend-related checks are marked N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: N/A (backend-only feature, no cross-platform concerns)

- [x] **Android MVI Architecture**: N/A (backend-only feature, no Android UI)

- [x] **iOS MVVM-C Architecture**: N/A (backend-only feature, no iOS UI)

- [x] **Interface-Based Design**: ✅ COMPLIANT
  - Backend repository interface planned in `/server/src/database/repositories/`
  - Service layer will use repository interface, not direct database access
  - Enables unit testing with fake repositories

- [x] **Dependency Injection**: ✅ COMPLIANT
  - Backend uses manual DI (constructor injection, factory functions)
  - Services receive dependencies via constructor parameters
  - No DI framework required (simplicity preferred per constitution)

- [x] **80% Test Coverage - Platform-Specific**: ✅ COMPLIANT
  - Backend unit tests planned in `/server/src/services/__test__/`, `/server/src/lib/__test__/`
  - Integration tests planned in `/server/src/__test__/`
  - Coverage target: 80% line + branch coverage
  - Run command: `npm test -- --coverage` (from server/)

- [x] **End-to-End Tests**: N/A (backend-only API endpoint, web E2E tests not required for MVP)
  - Note: E2E tests may be added later when frontend consumes this endpoint

- [x] **Asynchronous Programming Standards**: ✅ COMPLIANT
  - Backend uses native `async`/`await` (Express async handlers)
  - No callbacks, Promise chains, or prohibited patterns

- [x] **Test Identifiers for UI Controls**: N/A (backend-only feature, no UI elements)

- [x] **Public API Documentation**: ✅ COMPLIANT
  - JSDoc documentation planned for all public service functions
  - Utility functions documented when purpose not clear from name
  - Focus on WHAT/WHY (1-3 sentences), not HOW

- [x] **Given-When-Then Test Structure**: ✅ COMPLIANT
  - All unit tests will follow Given-When-Then structure
  - Integration tests will structure scenarios with Given-When-Then phases
  - Descriptive test names in string format (TypeScript convention)

### Backend Architecture & Quality Standards

- [x] **Backend Technology Stack**: ✅ COMPLIANT
  - Runtime: Node.js v24 (LTS) ✅
  - Framework: Express.js ✅
  - Language: TypeScript with strict mode enabled ✅
  - Database: Knex query builder + SQLite (designed for PostgreSQL migration) ✅

- [x] **Backend Code Quality**: ✅ COMPLIANT
  - ESLint with TypeScript plugin already configured in `/server` ✅
  - Clean Code principles enforced:
    - Small, focused functions (single responsibility) ✅
    - Descriptive naming (avoid unclear abbreviations) ✅
    - Maximum 3 nesting levels ✅
    - DRY principle (extract reusable logic to `/lib`) ✅
    - JSDoc documentation for all public APIs ✅

- [x] **Backend Dependency Management**: ✅ COMPLIANT
  - Only 2 new dependencies added: `xss` (XSS prevention), `zod` (input validation)
  - Both dependencies provide significant value (security + code quality)
  - Both are well-maintained and lightweight (xss: 1M+ weekly downloads, zod: 5M+ weekly downloads, zero sub-dependencies)
  - Password hashing uses Node.js built-in `scrypt` (no external dependency needed)
  - Rationale documented in research.md ✅
  - No micro-dependencies added ✅

- [x] **Backend Directory Structure**: ✅ COMPLIANT
  - `/server/src/middlewares/` - Error handler middleware planned ✅
  - `/server/src/routes/` - Announcement routes planned ✅
  - `/server/src/services/` - Announcement service with business logic ✅
  - `/server/src/database/` - Migration and repository planned ✅
  - `/server/src/lib/` - Utility functions (sanitization, password, PII redaction, URL validation) ✅
  - `/server/src/__test__/` - Integration tests planned ✅
  - Existing `app.ts` and `index.ts` will be modified ✅

- [x] **Backend TDD Workflow**: ✅ COMPLIANT
  - RED: Write failing test first (demonstrated in quickstart.md) ✅
  - GREEN: Write minimal code to pass test ✅
  - REFACTOR: Improve code quality without changing behavior ✅
  - Tests written BEFORE implementation code (TDD workflow documented) ✅

- [x] **Backend Testing Strategy**: ✅ COMPLIANT
  - Unit tests (Vitest):
    - Location: `/server/src/services/__test__/announcement-service.test.ts`, `/server/src/lib/__test__/*.test.ts` ✅
    - Coverage target: 80% line + branch coverage ✅
    - Scope: Announcement service, text sanitization, password management, PII redaction, URL validation ✅
  - Integration tests (Vitest + SuperTest):
    - Location: `/server/src/__test__/announcements.test.ts` ✅
    - Coverage target: 80% for API endpoints ✅
    - Scope: POST `/api/v1/announcements` endpoint (all user stories) ✅
  - All tests follow Given-When-Then structure ✅
  - Run commands: `npm test`, `npm test -- --coverage` ✅

**Constitution Check Result**: ✅ **PASS** - All applicable checks compliant. No violations.

## Project Structure

### Documentation (this feature)

```text
specs/009-create-announcement/
├── plan.md              # This file (implementation plan)
├── research.md          # Technology decisions and rationale
├── data-model.md        # Database schema and validation rules
├── quickstart.md        # Step-by-step implementation guide
└── contracts/           # API contracts
    └── openapi.yaml     # OpenAPI 3.0 specification
```

### Source Code (Backend Module)

```text
server/
├── src/
│   ├── database/
│   │   ├── migrations/
│   │   │   └── YYYYMMDDHHMMSS_create-announcement-table.ts  # [NEW] Database schema
│   │   └── repositories/
│   │       └── announcement-repository.ts                   # [NEW] Data access layer
│   ├── lib/
│   │   ├── __test__/
│   │   │   ├── text-sanitization.test.ts                    # [NEW] Unit tests
│   │   │   ├── password-management.test.ts                  # [NEW] Unit tests
│   │   │   ├── pii-redaction.test.ts                        # [NEW] Unit tests
│   │   │   └── url-validation.test.ts                       # [NEW] Unit tests
│   │   ├── text-sanitization.ts                             # [NEW] XSS prevention utility
│   │   ├── password-management.ts                           # [NEW] Password hashing utility
│   │   ├── pii-redaction.ts                                 # [NEW] Logging privacy utility
│   │   ├── url-validation.ts                                # [NEW] URL format validation
│   │   ├── errors.ts                                        # [NEW] Custom error classes
│   │   └── validators.ts                                    # [EXISTING] Email/phone validators
│   ├── middlewares/
│   │   └── error-handler.ts                                 # [NEW] Express error middleware
│   ├── routes/
│   │   └── announcements.ts                                 # [NEW] POST /api/v1/announcements
│   ├── services/
│   │   ├── __test__/
│   │   │   └── announcement-service.test.ts                 # [NEW] Unit tests
│   │   └── announcement-service.ts                          # [NEW] Business logic
│   ├── types/
│   │   └── announcement.ts                                  # [NEW] TypeScript types
│   ├── __test__/
│   │   └── announcements.test.ts                            # [NEW] Integration tests
│   ├── app.ts                                               # [MODIFY] Register route + middleware
│   └── index.ts                                             # [EXISTING] Server entry point
├── package.json                                             # [MODIFY] Add dependencies
├── pets.db                                                  # [MODIFY] Database updated by migration
└── README.md                                                # [EXISTING] Backend documentation
```

**Structure Decision**: Backend-only feature using existing `/server` module. No frontend changes required. All code follows backend architecture standards from constitution (middlewares, routes, services, database, lib separation).

**New Files**: 16 new files (4 lib utilities + 4 test files, 1 service + 1 test, 1 route, 1 middleware, 1 repository, 1 migration, 1 types, 1 integration test)  
**Modified Files**: 2 files (`app.ts` for route registration, `package.json` for dependencies)

## Complexity Tracking

> **Not applicable**: No constitution violations. All checks passed.
