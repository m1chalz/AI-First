# Implementation Plan: Remove Location Fields

**Branch**: `014-remove-location-fields` | **Date**: 2025-01-27 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/014-remove-location-fields/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Remove deprecated location fields (`location`, `locationCity`, `locationRadius`) from the announcement data model across database schema, TypeScript types, DTOs, validation schemas, service layer, repository layer, tests, and API documentation. This simplifies the data model by relying solely on latitude/longitude coordinates for location information.

## Technical Context

**Language/Version**: TypeScript (strict mode)  
**Primary Dependencies**: Express.js, Knex, Zod, Vitest, SuperTest  
**Storage**: SQLite (development), PostgreSQL-ready (production)  
**Testing**: Vitest (unit + integration) + SuperTest (API integration)  
**Target Platform**: Node.js v24 (LTS)  
**Project Type**: Backend API (Node.js/Express)  
**Performance Goals**: N/A (refactoring task, no performance impact expected)  
**Constraints**: N/A  
**Scale/Scope**: Backend-only refactoring affecting announcement data model

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is a backend-only feature affecting only `/server` module. Frontend-related checks are marked as N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: N/A (backend-only feature)
  - This feature only affects `/server` module
  - No impact on Android, iOS, or Web platforms
  - Violation justification: N/A

- [x] **Android MVI Architecture**: N/A (backend-only feature)
  - Violation justification: N/A

- [x] **iOS MVVM-C Architecture**: N/A (backend-only feature)
  - Violation justification: N/A

- [x] **Interface-Based Design**: ✅ Compliant
  - Repository interfaces remain unchanged (`IAnnouncementRepository`)
  - Only implementation details change (field removal)
  - Violation justification: N/A

- [x] **Dependency Injection**: ✅ Compliant
  - Manual DI pattern maintained (constructor injection)
  - No changes to DI structure
  - Violation justification: N/A

- [x] **80% Test Coverage - Platform-Specific**: ✅ Compliant
  - Tests in `/server/src/services/__test__/`, `/server/src/lib/__test__/`, `/server/src/__test__/`
  - Coverage target: 80% line + branch coverage maintained
  - Existing tests will be updated to remove location field references
  - Violation justification: N/A

- [x] **End-to-End Tests**: N/A (backend-only feature)
  - No E2E tests required for backend refactoring
  - Violation justification: N/A

- [x] **Asynchronous Programming Standards**: ✅ Compliant
  - Native `async`/`await` patterns maintained
  - Express async handlers unchanged
  - Violation justification: N/A

- [x] **Test Identifiers for UI Controls**: N/A (backend-only feature)
  - Violation justification: N/A

- [x] **Public API Documentation**: ✅ Compliant
  - API documentation (README.md) will be updated
  - JSDoc comments maintained where needed
  - Violation justification: N/A

- [x] **Given-When-Then Test Structure**: ✅ Compliant
  - All tests follow Given-When-Then convention
  - Test structure maintained during updates
  - Violation justification: N/A

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: ✅ Compliant
  - Runtime: Node.js v24 (LTS)
  - Framework: Express.js
  - Language: TypeScript with strict mode enabled
  - Database: Knex query builder + SQLite (designed for PostgreSQL migration)
  - Violation justification: N/A

- [x] **Backend Code Quality**: ✅ Compliant
  - ESLint with TypeScript plugin configured
  - Clean Code principles applied
  - Small, focused functions maintained
  - Descriptive naming preserved
  - Violation justification: N/A

- [x] **Backend Dependency Management**: ✅ Compliant
  - No new dependencies added
  - Existing dependencies remain unchanged
  - Violation justification: N/A

- [x] **Backend Directory Structure**: ✅ Compliant
  - `/middlewares/` - No changes
  - `/routes/` - No changes (thin routing layer)
  - `/services/` - Remove location field processing
  - `/database/` - Migration to remove columns, repository updates
  - `/lib/` - Remove location field validation
  - `/__test__/` - Update integration tests
  - Violation justification: N/A

- [x] **Backend TDD Workflow**: ✅ Compliant
  - Tests updated before implementation changes
  - Red-Green-Refactor cycle maintained
  - Violation justification: N/A

- [x] **Backend Testing Strategy**: ✅ Compliant
  - Unit tests: `/src/services/__test__/`, `/src/lib/__test__/`
  - Integration tests: `/src/__test__/`
  - Coverage target: 80% maintained after updates
  - All tests follow Given-When-Then structure
  - Violation justification: N/A

## Project Structure

### Documentation (this feature)

```text
specs/014-remove-location-fields/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
server/
├── src/
│   ├── types/
│   │   └── announcement.d.ts          # Remove location fields from interfaces
│   ├── lib/
│   │   ├── announcement-validation.ts  # Remove locationCity, locationRadius from schema
│   │   └── __test__/
│   │       └── announcement-validation.test.ts  # Update tests
│   ├── services/
│   │   ├── announcement-service.ts     # Remove locationCity sanitization
│   │   └── __test__/
│   │       └── announcement-service.test.ts  # Update tests
│   ├── database/
│   │   ├── migrations/
│   │   │   └── [timestamp]_remove_location_fields.ts  # New migration
│   │   ├── repositories/
│   │   │   └── announcement-repository.ts  # Remove field mapping
│   │   └── seeds/
│   │       └── 001_announcements.ts  # Remove location fields from seed data
│   ├── routes/
│   │   └── announcements.ts          # No changes (thin routing layer)
│   └── __test__/
│       └── announcements.test.ts      # Update integration tests
├── README.md                           # Update API documentation
└── package.json                        # No changes
```

**Structure Decision**: Backend-only refactoring following existing `/server` directory structure. Changes span types, validation, services, database (migration + repository), tests, and documentation.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| N/A | N/A | N/A |
