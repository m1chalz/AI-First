# Implementation Plan: Pet Details API Endpoint

**Branch**: `008-pet-details-api` | **Date**: 2025-11-21 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/008-pet-details-api/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implement a REST API endpoint `GET /api/v1/announcements/:id` that retrieves a single pet announcement by its unique identifier. The endpoint returns HTTP 200 with the full announcement object for existing pets, or HTTP 404 with a structured error response for non-existent or malformed IDs. The implementation will use the existing `announcement` table, follow TDD workflow with Vitest + SuperTest, and maintain 80% test coverage for both unit and integration tests.

## Technical Context

**Language/Version**: TypeScript with Node.js v24 (LTS)  
**Primary Dependencies**: Express.js (REST framework), Knex (query builder), SQLite3 (database driver), Vitest (testing), SuperTest (API integration tests)  
**Storage**: SQLite database (designed for PostgreSQL migration) using existing `announcement` table  
**Testing**: Vitest for unit tests (services, utilities) + SuperTest for integration tests (API endpoints)  
**Target Platform**: Node.js server (backend REST API)  
**Project Type**: Backend-only (no frontend changes)  
**Performance Goals**: Response time < 500ms for single record retrieval (direct database query, no caching)  
**Constraints**: Return 404 for malformed UUIDs (treated as non-existent); include all required fields with null for optional fields  
**Scale/Scope**: Single GET endpoint, extends existing `/api/v1/announcements` API with detail view capability

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is a **backend-only feature** (affecting only `/server` module). Frontend-related checks are marked N/A.

### KMP Architecture Compliance

- [x] **Thin Shared Layer**: N/A - Backend-only feature, no `/shared` module changes

- [x] **Native Presentation**: N/A - Backend-only feature, no frontend presentation layer changes

- [x] **Android MVI Architecture**: N/A - Backend-only feature, no Android UI changes

- [x] **Interface-Based Design**: N/A - Backend uses TypeScript repository pattern, not KMP interfaces

- [x] **Dependency Injection**: N/A - Backend uses native TypeScript DI (no Koin)

- [x] **80% Test Coverage - Shared Module**: N/A - Backend-only feature, no shared module changes

- [x] **80% Test Coverage - ViewModels**: N/A - Backend-only feature, no ViewModels

- [x] **End-to-End Tests**: N/A - Backend API endpoint; will be covered by backend integration tests (Vitest + SuperTest)

- [x] **Platform Independence**: N/A - Backend-only feature using Node.js platform

- [x] **Clear Contracts**: ✅ COMPLIANT - OpenAPI contract will define explicit API schema with typed request/response

- [x] **Asynchronous Programming Standards**: ✅ COMPLIANT - Using native TypeScript `async`/`await` pattern (no Promise chains)

- [x] **Test Identifiers for UI Controls**: N/A - Backend-only feature, no UI controls

- [x] **Public API Documentation**: ✅ COMPLIANT - JSDoc for business logic functions and route handlers with description of params, returns, and errors

- [x] **Given-When-Then Test Structure**: ✅ COMPLIANT - All unit and integration tests will follow Given-When-Then structure with descriptive test names

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: ✅ COMPLIANT
  - Runtime: Node.js v24 (LTS) ✅
  - Framework: Express.js ✅
  - Language: TypeScript with strict mode enabled ✅
  - Database: Knex query builder + SQLite (using existing `announcement` table) ✅

- [x] **Backend Code Quality**: ✅ COMPLIANT
  - ESLint with TypeScript plugin configured and enabled ✅
  - Clean Code principles to be applied:
    - Small, focused functions (single responsibility) ✅
    - Descriptive naming (e.g., `getAnnouncementById`, not `getAnn`) ✅
    - Maximum 3 nesting levels ✅
    - DRY principle (extract reusable validation/formatting to `/lib`) ✅
    - JSDoc documentation for all public APIs ✅

- [x] **Backend Dependency Management**: ✅ COMPLIANT
  - No new dependencies required (uses existing Express, Knex, SQLite3) ✅
  - Existing dependencies are well-maintained and security-audited ✅
  - No micro-dependencies added ✅

- [x] **Backend Directory Structure**: ✅ COMPLIANT
  - Route handler: `/server/src/routes/announcementRoutes.ts` (extend existing) ✅
  - Business logic: `/server/src/services/announcementService.ts` (extend existing) ✅
  - Repository: `/server/src/database/announcementRepository.ts` (extend existing) ✅
  - Unit tests: `/server/src/services/__test__/announcementService.test.ts` ✅
  - Integration tests: `/server/src/__test__/announcementRoutes.test.ts` ✅

- [x] **Backend TDD Workflow**: ✅ COMPLIANT
  - RED: Write failing unit test for `getAnnouncementById` service function ✅
  - GREEN: Implement minimal code to pass test ✅
  - REFACTOR: Extract reusable validation/error handling ✅
  - RED: Write failing integration test for GET `/api/v1/announcements/:id` ✅
  - GREEN: Wire route handler to service ✅
  - REFACTOR: Improve error response structure ✅

- [x] **Backend Testing Strategy**: ✅ COMPLIANT
  - Unit tests (Vitest):
    - Location: `/server/src/services/__test__/announcementService.test.ts` ✅
    - Coverage target: 80% line + branch coverage ✅
    - Scope: `getAnnouncementById` service function with fake repository ✅
  - Integration tests (Vitest + SuperTest):
    - Location: `/server/src/__test__/announcementRoutes.test.ts` ✅
    - Coverage target: 80% for GET `/api/v1/announcements/:id` endpoint ✅
    - Scope: HTTP 200 (success), HTTP 404 (not found), HTTP 404 (malformed UUID) ✅
  - All tests follow Given-When-Then structure ✅
  - Run commands: `npm test`, `npm test -- --coverage` (from `/server` directory) ✅

## Project Structure

### Documentation (this feature)

```text
specs/008-pet-details-api/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
│   └── announcement-api.openapi.yaml
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (Backend Module)

```text
server/src/
├── routes/
│   └── announcement-routes.ts              # Extend with GET /:id endpoint
├── services/
│   ├── announcement-service.ts             # Extend with getAnnouncementById()
│   └── __test__/
│       └── announcement-service.test.ts    # Unit tests for getAnnouncementById()
├── database/
│   └── announcement-repository.ts          # Extend with findById()
├── lib/
│   └── validators.ts                       # UUID validation utilities (if needed)
└── __test__/
    └── announcement-routes.test.ts         # Integration tests for GET /api/v1/announcements/:id
```

**Structure Decision**: Backend-only feature extending the existing `/server` Node.js module. The implementation will add a new GET endpoint to the existing `announcement-routes.ts` router, extend the `announcement-service.ts` with a `getAnnouncementById` function, and extend the `announcement-repository.ts` with a `findById` method. Tests will be added following the established TDD pattern with unit tests for the service layer and integration tests for the API endpoint. All new files follow kebab-case naming convention.

## Complexity Tracking

No constitution violations. All checks are either compliant or N/A for this backend-only feature.
