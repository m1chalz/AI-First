# Implementation Plan: Announcements Location Query

**Branch**: `033-announcements-location-query` | **Date**: 2025-11-29 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/033-announcements-location-query/spec.md`

## Summary

Add location-based filtering to the `/api/v1/announcements` endpoint. Accept optional query parameters (`lat`, `lng`, `range`) to filter announcements within a specified radius (default 5km) from given coordinates. Haversine distance calculation implemented in SQL query for efficiency (no application-layer filtering needed). Backend-only change in `/server` module with comprehensive validation and backward compatibility.

## Technical Context

**Language/Version**: TypeScript 5+ with Node.js v24 (LTS)
**Primary Dependencies**: Express.js, Knex (query builder)
**Storage**: SQLite (current), designed for PostgreSQL migration
**Testing**: Vitest (unit tests) + SuperTest (integration tests)
**Target Platform**: Node.js backend server
**Project Type**: Backend API (single module)
**Performance Goals**: N/A (explicitly stated: low traffic, small dataset - performance not critical)
**Constraints**: N/A (no specific constraints mentioned)
**Scale/Scope**: Backend-only change affecting single endpoint

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is a backend-only feature affecting only `/server` module. Frontend-related checks are marked N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (backend-only change)
  - iOS: N/A (backend-only change)
  - Web: N/A (backend-only change)
  - Backend: Changes isolated to `/server` module
  - NO shared compiled code between platforms: ✅ (backend-only)
  - Violation justification: N/A

- [x] **Android MVI Architecture**: Android features follow the mandated Compose MVI loop
  - N/A: Backend-only feature, no Android changes
  - Violation justification: N/A

- [x] **iOS MVVM-C Architecture**: iOS features follow MVVM-Coordinator pattern
  - N/A: Backend-only feature, no iOS changes
  - Violation justification: N/A

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Android: N/A (no Android changes)
  - iOS: N/A (no iOS changes)
  - Web: N/A (no Web changes)
  - Backend: Will use existing repository pattern in `/server/src/database/repositories/`
  - Violation justification: N/A

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: N/A (no Android changes)
  - iOS: N/A (no iOS changes)
  - Web: N/A (no Web changes)
  - Backend: Manual DI with factory functions (existing pattern)
  - Violation justification: N/A

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: N/A (no Android changes)
  - iOS: N/A (no iOS changes)
  - Web: N/A (no Web changes)
  - Backend: Unit tests in `/server/src/lib/__test__/`, integration tests in `/server/src/__test__/`
  - Coverage target: 80% line + branch coverage for new code
  - Violation justification: N/A

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - N/A: Backend-only API change. E2E tests would require frontend implementation to consume the new parameters.
  - API integration tests will be written using SuperTest to verify endpoint behavior
  - Violation justification: E2E tests deferred until frontend platforms consume the new parameters. API integration tests provide equivalent coverage for backend-only changes.

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: N/A (no Android changes)
  - iOS: N/A (no iOS changes)
  - Web: N/A (no Web changes)
  - Backend: Native `async`/`await` (Express async handlers) ✅
  - Violation justification: N/A

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - N/A: Backend-only feature, no UI changes
  - Violation justification: N/A

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Backend: JSDoc format (`/** ... */`) for complex distance calculation functions
  - Documentation will be concise (1-3 sentences: WHAT/WHY)
  - Simple validation functions skip documentation if self-explanatory
  - Violation justification: N/A

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - All unit tests will separate setup (Given), action (When), verification (Then)
  - Integration tests will structure API request/response scenarios with Given-When-Then
  - Test names use descriptive strings (TypeScript convention)
  - Comments mark test phases for complex validation scenarios
  - Violation justification: N/A

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: Plan uses modern Node.js stack for `/server` module
  - Runtime: Node.js v24 (LTS) ✅
  - Framework: Express.js ✅
  - Language: TypeScript with strict mode enabled ✅
  - Database: Knex query builder + SQLite (designed for PostgreSQL migration) ✅
  - Violation justification: N/A

- [x] **Backend Code Quality**: Plan enforces quality standards for `/server` code
  - ESLint with TypeScript plugin configured and enabled ✅
  - Clean Code principles:
    - Small, focused functions: distance calculation, validation (single responsibility) ✅
    - Descriptive naming: `calculateHaversineDistance`, `validateCoordinates` ✅
    - Maximum 3 nesting levels: validation logic extracted to separate functions ✅
    - DRY principle: reusable distance calculation in `/lib`, validation utilities ✅
    - JSDoc documentation for Haversine formula and complex validation logic ✅
  - Violation justification: N/A

- [x] **Backend Dependency Management**: Plan minimizes dependencies in `/server/package.json`
  - NO new dependencies required ✅
  - Haversine distance calculation implemented as pure function (native JavaScript Math API)
  - All validation logic uses native TypeScript
  - Knex query builder (existing dependency) sufficient for database filtering
  - Violation justification: N/A

- [x] **Backend Directory Structure**: Plan follows standardized layout in `/server/src/`
  - `/lib/` - New: `location-validation.ts` (coordinate and range validation)
  - `/lib/__test__/` - New: `location-validation.test.ts` (unit tests for validation)
  - `/routes/` - Modified: `announcements.ts` (add query param handling)
  - `/database/` - Modified: announcement repository/query (add Haversine SQL calculation)
  - `/__test__/` - Modified: announcement endpoint integration tests
  - Violation justification: N/A

- [x] **Backend TDD Workflow**: Plan follows Test-Driven Development (Red-Green-Refactor)
  - Phase 0 (Research): Document Haversine formula (SQL) and validation rules
  - Phase 1 (Red): Write failing tests for:
    1. Coordinate pair validation (unit tests)
    2. Range validation (unit tests)
    3. API endpoint behavior with distance filtering (integration tests)
    4. Distance calculation accuracy verification (integration tests with known coordinates)
  - Phase 2 (Green): Implement minimal code to pass tests (validation + SQL query)
  - Phase 3 (Refactor): Extract reusable validation utilities, optimize query if needed
  - Violation justification: N/A

- [x] **Backend Testing Strategy**: Plan includes comprehensive test coverage for `/server`
  - Unit tests (Vitest):
    - Location: `/src/lib/__test__/haversine-distance.test.ts`
    - Location: `/src/lib/__test__/location-validation.test.ts`
    - Coverage target: 80%+ line + branch coverage
    - Scope: Distance calculation, coordinate validation, range validation
  - Integration tests (Vitest + SuperTest):
    - Location: `/src/__test__/announcements.test.ts` (modify existing)
    - Coverage target: 80%+ for new endpoint behavior
    - Scope: Full request → response cycle with location parameters
  - All tests follow Given-When-Then structure
  - Run commands: `npm test`, `npm test -- --coverage`
  - Violation justification: N/A

## Project Structure

### Documentation (this feature)

```text
specs/033-announcements-location-query/
├── plan.md              # This file
├── research.md          # Phase 0: Haversine formula, validation patterns
├── data-model.md        # Phase 1: Announcement entity with location fields
├── quickstart.md        # Phase 1: How to test location filtering locally
├── contracts/           # Phase 1: OpenAPI spec for updated endpoint
│   └── announcements-api.yaml
└── spec.md              # Feature specification (already exists)
```

### Source Code (repository root)

```text
server/
├── src/
│   ├── lib/
│   │   ├── location-validation.ts       # NEW: Coordinate and range validation utilities
│   │   └── __test__/
│   │       └── location-validation.test.ts  # NEW: Unit tests for validation
│   ├── routes/
│   │   └── announcements.ts             # MODIFIED: Add query param handling
│   ├── database/
│   │   └── repositories/                # MODIFIED: Add Haversine SQL to announcement query
│   └── __test__/
│       └── announcements.test.ts        # MODIFIED: Add integration tests for location filtering
└── package.json                          # NO CHANGES (no new dependencies)
```

**Structure Decision**: Single backend module (`/server`). Changes isolated to:
1. New validation utilities in `/src/lib/location-validation.ts`
2. Modified route handler in `/src/routes/announcements.ts` to accept and validate query params
3. Modified database query to include Haversine distance calculation using Knex raw SQL
4. Comprehensive unit and integration tests following TDD

**Naming Convention**: All new files use **kebab-case** per user requirement:
- `location-validation.ts` (not `locationValidation.ts`)
- Test files follow same convention with `.test.ts` suffix

**Implementation Note**: Haversine distance calculation is implemented directly in SQL query (see research.md Decision 5) for efficiency. No separate utility file needed.

## Complexity Tracking

> No violations to report. All constitution checks pass.

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| N/A | N/A | N/A |
