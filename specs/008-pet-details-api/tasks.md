# Tasks: Pet Details API Endpoint

**Input**: Design documents from `/specs/008-pet-details-api/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/, quickstart.md

**Tests**: Test requirements for this backend-only feature:

**MANDATORY - Backend Unit Tests** (TDD: Red-Green-Refactor):
- Location: `/server/src/services/__test__/`, `/server/src/lib/__test__/`
- Coverage: 80% line + branch coverage
- Framework: Vitest
- Scope: Business logic (services) and utility functions
- TDD Workflow: Red-Green-Refactor cycle (write failing test, minimal implementation, refactor)
- Convention: MUST follow Given-When-Then structure with descriptive test names

**MANDATORY - Backend Integration Tests** (TDD: Red-Green-Refactor):
- Location: `/server/src/__test__/`
- Coverage: 80% for REST API endpoints
- Framework: Vitest + SuperTest
- Scope: End-to-end API tests (request → response)
- Convention: MUST follow Given-When-Then structure

**Organization**: Tasks organized by user story with TDD workflow. This backend-only feature has one user story and extends existing backend infrastructure.

## Format: `- [ ] [ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1)
- Include exact file paths in descriptions

## Path Conventions

All paths are in `/server` backend module:
- Services: `/server/src/services/`
- Routes: `/server/src/routes/`
- Database: `/server/src/database/`
- Utilities: `/server/src/lib/`
- Service tests: `/server/src/services/__test__/`
- Integration tests: `/server/src/__test__/`

---

## Phase 1: Setup (Backend Infrastructure Validation)

**Purpose**: Verify existing backend infrastructure is ready for extension

- [X] T001 Verify Node.js v24 (LTS) is installed: `node --version`
- [X] T002 Verify existing `/server` directory structure (src/, routes/, services/, database/)
- [X] T003 Verify existing files: `announcement-routes.ts`, `announcement-service.ts`, `announcement-repository.ts`
- [X] T004 Verify existing `announcement` database table schema matches data-model.md requirements
- [X] T005 [P] Verify ESLint config exists at `/server/.eslintrc.js` with TypeScript plugin
- [X] T006 [P] Verify Vitest config exists at `/server/vitest.config.ts` with coverage thresholds (80%)
- [X] T007 [P] Verify SuperTest is installed: check `/server/package.json` for `supertest` dependency
- [X] T008 Run `npm test` from `/server` to verify test infrastructure works

---

## Phase 2: Foundational (Backend Test Infrastructure)

**Purpose**: Ensure test utilities and helpers are available for TDD workflow

**⚠️ CRITICAL**: This phase must complete before User Story 1 implementation begins

- [X] T009 Verify or create test database helper at `/server/src/__test__/test-db-helper.ts` (setupTestDatabase, teardownTestDatabase, clearDatabase, seedDatabase functions)
- [X] T010 [P] Verify or create fake repository pattern: check if `/server/src/database/__test__/` directory exists for test doubles
- [X] T011 [P] Verify existing error response structure in codebase (structured JSON with code + message)
- [X] T012 Run `npm test -- --coverage` from `/server` to establish baseline coverage

**Checkpoint**: Backend test infrastructure ready - User Story 1 TDD workflow can begin

---

## Phase 3: User Story 1 - Retrieve Single Pet Announcement by ID (Priority: P1) 🎯 MVP

**Goal**: Implement GET `/api/v1/announcements/:id` endpoint that retrieves a single pet announcement by UUID, returning HTTP 200 with announcement object or HTTP 404 for non-existent/malformed IDs

**Independent Test**: Make a GET request to `/api/v1/announcements/{valid-uuid}` and verify:
- HTTP 200 response with complete announcement JSON object (all 14 fields)
- GET to non-existent UUID returns HTTP 404 with structured error: `{ error: { code: "NOT_FOUND", message: "Resource not found" } }`
- GET to malformed UUID returns HTTP 404 (treated as non-existent)
- Optional fields (breed, email, photoUrl, locationRadius) included with `null` value when database has NULL

**Acceptance Criteria from spec.md**:
1. Valid ID exists → HTTP 200 with announcement object
2. Non-existent ID → HTTP 404 with structured error
3. Response contains all required fields with correct data types

### Tests for User Story 1 (MANDATORY) ✅

> **TDD WORKFLOW: RED-GREEN-REFACTOR - Write these tests FIRST, ensure they FAIL before implementation**

**Backend Unit Tests** (Service Layer):

- [X] T013 [P] [US1] RED: Write failing unit test for `getAnnouncementById` service function - success case in `/server/src/services/__test__/announcement-service.test.ts` (Given: fake repository with mock announcement, When: service called with valid ID, Then: returns announcement object)
- [X] T014 [P] [US1] RED: Write failing unit test for `getAnnouncementById` - not found case in `/server/src/services/__test__/announcement-service.test.ts` (Given: fake repository with empty data, When: service called with non-existent ID, Then: returns null)
- [X] T015 [P] [US1] RED: Write failing unit test for `getAnnouncementById` - optional fields with null in `/server/src/services/__test__/announcement-service.test.ts` (Given: fake repository with announcement having null optional fields, When: service called, Then: returns announcement with nulls preserved)

**Backend Integration Tests** (API Endpoint):

- [X] T016 [P] [US1] RED: Write failing integration test for GET `/api/v1/announcements/:id` - HTTP 200 success case in `/server/src/__test__/announcement-routes.test.ts` (Given: database seeded with test announcement, When: GET request with valid ID, Then: HTTP 200 with announcement JSON)
- [X] T017 [P] [US1] RED: Write failing integration test for GET `/api/v1/announcements/:id` - HTTP 404 not found in `/server/src/__test__/announcement-routes.test.ts` (Given: empty database, When: GET request with non-existent UUID, Then: HTTP 404 with error structure)
- [X] T018 [P] [US1] RED: Write failing integration test for GET `/api/v1/announcements/:id` - HTTP 404 malformed UUID in `/server/src/__test__/announcement-routes.test.ts` (Given: empty database, When: GET request with malformed UUID like "abc-123", Then: HTTP 404 same as non-existent)
- [X] T019 [P] [US1] RED: Write failing integration test for GET `/api/v1/announcements/:id` - optional fields with null in `/server/src/__test__/announcement-routes.test.ts` (Given: database with announcement having null optional fields, When: GET request, Then: response includes null values not omitted)
- [X] T020 [P] [US1] RED: Write failing integration test for GET `/api/v1/announcements/:id` - all status values in `/server/src/__test__/announcement-routes.test.ts` (Given: announcements with ACTIVE/FOUND/CLOSED status, When: GET request for each, Then: returns announcements regardless of status)

### Implementation for User Story 1

> **TDD CYCLE: Implement minimal code to pass tests, then refactor**

**Test Infrastructure** (prerequisite for unit tests):

- [X] T021 [US1] Create `FakeAnnouncementRepository` class in `/server/src/database/__test__/fake-announcement-repository.ts` implementing AnnouncementRepository interface with in-memory storage and `findById` method

**Service Layer Implementation** (GREEN phase):

- [X] T022 [US1] GREEN: Extend AnnouncementRepository interface in `/server/src/database/announcement-repository.ts` to add `findById(id: string): Promise<Announcement | null>` method signature
- [X] T023 [US1] GREEN: Implement `findById` method in KnexAnnouncementRepository class in `/server/src/database/announcement-repository.ts` using Knex `.where('id', id).first()` pattern
- [X] T024 [US1] GREEN: Implement `getAnnouncementById(repository, id)` function in `/server/src/services/announcement-service.ts` that calls repository.findById and returns result
- [X] T025 [US1] Run unit tests for announcement-service.ts and verify all tests pass (T013-T015)
- [X] T026 [US1] REFACTOR: Review service code for Clean Code principles (function size, naming, DRY) and refactor if needed

**API Endpoint Implementation** (GREEN phase):

- [X] T027 [US1] GREEN: Add GET `/api/v1/announcements/:id` route handler in `/server/src/routes/announcement-routes.ts` that calls announcementService.getAnnouncementById
- [X] T028 [US1] GREEN: Implement null check in route handler - if announcement is null, return HTTP 404 with structured error: `{ error: { code: "NOT_FOUND", message: "Resource not found" } }`
- [X] T029 [US1] GREEN: If announcement found, return HTTP 200 with announcement object directly (no data wrapper)
- [X] T030 [US1] Run integration tests for announcement-routes.ts and verify all tests pass (T016-T020)
- [X] T031 [US1] REFACTOR: Extract error response structure to `/server/src/lib/error-responses.ts` if duplicated across routes (optional)

**Documentation** (required for public APIs):

- [X] T032 [P] [US1] Add JSDoc to `getAnnouncementById` service function in `/server/src/services/announcement-service.ts` (describe params, returns, behavior - 1-3 sentences)
- [X] T033 [P] [US1] Add JSDoc to `findById` repository method in `/server/src/database/announcement-repository.ts` (describe params, returns)
- [X] T034 [P] [US1] Add JSDoc to GET `/api/v1/announcements/:id` route handler in `/server/src/routes/announcement-routes.ts` (HTTP method, path, params, response codes)

**Quality Assurance**:

- [X] T035 [US1] Run `npm test -- --coverage` from `/server` and verify 80%+ coverage for `announcement-service.ts`
- [X] T036 [US1] Run `npm test -- --coverage` from `/server` and verify 80%+ coverage for `announcement-routes.ts`
- [X] T037 [US1] Run `npm run lint` from `/server` and fix any ESLint violations
- [X] T038 [US1] Run all tests (`npm test`) and verify 100% pass rate
- [X] T039 [US1] Manual test: Start dev server (`npm run dev`) and test endpoint with curl/Postman for all scenarios (200, 404 not found, 404 malformed UUID, optional fields)

**Checkpoint**: User Story 1 complete and independently testable. Endpoint fully functional with 80%+ test coverage.

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Final improvements and validation

- [X] T040 [P] Verify quickstart.md instructions match implemented code structure (file paths, naming conventions)
- [X] T041 [P] Run performance test: verify response time < 500ms for single announcement retrieval (use Apache Bench or similar: `ab -n 100 -c 10 http://localhost:3000/api/v1/announcements/{uuid}`)
- [X] T042 [P] Review all JSDoc documentation for consistency and completeness (services, repositories, routes)
- [X] T043 [P] Run `npm audit` from `/server` to check for security vulnerabilities in dependencies
- [X] T044 Final validation: Run complete test suite with coverage (`npm test -- --coverage`) and verify all success criteria from spec.md
- [X] T045 Create summary of implementation: list all modified files, test coverage percentages, performance metrics

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately (validation only)
- **Foundational (Phase 2)**: Depends on Setup validation - MUST complete before User Story 1
- **User Story 1 (Phase 3)**: Depends on Foundational phase completion - main implementation
- **Polish (Phase 4)**: Depends on User Story 1 completion

### Within User Story 1 (TDD Order)

**CRITICAL: Follow strict TDD order - do NOT implement before tests fail**

1. **RED**: Write all unit tests (T013-T015) - ensure they FAIL
2. **RED**: Write all integration tests (T016-T020) - ensure they FAIL
3. **Setup**: Create fake repository (T021) for unit tests
4. **GREEN**: Implement service layer (T022-T025) - minimal code to pass unit tests
5. **REFACTOR**: Clean up service code (T026)
6. **GREEN**: Implement API endpoint (T027-T030) - minimal code to pass integration tests
7. **REFACTOR**: Extract reusable patterns (T031)
8. **Document**: Add JSDoc to all public APIs (T032-T034)
9. **Validate**: Verify coverage and quality (T035-T039)

### Parallel Opportunities

**Setup Phase (Phase 1)**:
- Tasks T005, T006, T007 can run in parallel (different verification checks)

**Foundational Phase (Phase 2)**:
- Tasks T010, T011 can run in parallel (different infrastructure components)

**User Story 1 - Tests Phase**:
- Unit tests (T013-T015) can be written in parallel (different test cases in same file)
- Integration tests (T016-T020) can be written in parallel (different test cases in same file)
- **BUT**: All test-writing must complete BEFORE any implementation (TDD rule)

**User Story 1 - Documentation Phase**:
- JSDoc tasks (T032-T034) can run in parallel (different files)

**Polish Phase (Phase 4)**:
- Tasks T040, T041, T042, T043 can run in parallel (independent validation checks)

---

## Parallel Example: User Story 1 Tests

```bash
# RED Phase - Write all failing tests in parallel:
Task T013: "Write failing test for getAnnouncementById - success case"
Task T014: "Write failing test for getAnnouncementById - not found case"  
Task T015: "Write failing test for getAnnouncementById - optional fields null"

# Separately (can also be parallel):
Task T016: "Write failing integration test - HTTP 200 success"
Task T017: "Write failing integration test - HTTP 404 not found"
Task T018: "Write failing integration test - HTTP 404 malformed UUID"
Task T019: "Write failing integration test - optional fields null"
Task T020: "Write failing integration test - all status values"

# After ALL tests fail, proceed to GREEN phase sequentially
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

This feature has only one user story, so MVP = complete feature:

1. **Phase 1**: Validate existing infrastructure (5-10 minutes)
2. **Phase 2**: Ensure test helpers exist (10-15 minutes)
3. **Phase 3**: Implement User Story 1 with TDD (2-3 hours)
   - RED: Write 8 failing tests (30-45 minutes)
   - GREEN: Implement service + endpoint (60-90 minutes)
   - REFACTOR: Clean up code (15-20 minutes)
   - Document: Add JSDoc (10-15 minutes)
   - Validate: Coverage + quality checks (15-20 minutes)
4. **Phase 4**: Final polish and validation (20-30 minutes)

**Total Estimated Time**: 3-4 hours for complete feature

### TDD Workflow (Critical for Backend)

**MUST follow strict Red-Green-Refactor cycle**:

1. **RED** (Test First):
   - Write unit test → verify it FAILS
   - Write integration test → verify it FAILS
   - Do NOT proceed until test fails for the right reason

2. **GREEN** (Minimal Implementation):
   - Write simplest code to pass test
   - Run test → verify it PASSES
   - Do NOT add features not covered by tests

3. **REFACTOR** (Clean Up):
   - Improve code quality (Clean Code principles)
   - Extract duplicated logic to utilities
   - Run tests → verify they still PASS
   - Do NOT change behavior

### Quality Gates

Before marking User Story 1 complete, verify:

- ✅ All tests pass (100% pass rate)
- ✅ Coverage ≥ 80% for announcement-service.ts
- ✅ Coverage ≥ 80% for announcement-routes.ts
- ✅ ESLint violations = 0
- ✅ JSDoc present on all public APIs
- ✅ Manual testing confirms all scenarios work
- ✅ Response time < 500ms

---

## Notes

- **[P] marker**: Tasks that can run in parallel (different files, no dependencies)
- **[US1] marker**: All tasks in Phase 3 belong to User Story 1
- **Backend-only**: No frontend changes needed (no Android, iOS, Web, or shared module tasks)
- **TDD Strict Order**: Write tests FIRST, ensure they FAIL, then implement minimal code to pass
- **File Naming**: All new files use kebab-case (announcement-service.ts, not announcementService.ts)
- **Coverage Requirement**: 80% minimum for both service layer and API endpoints
- **Given-When-Then**: All tests must follow Given-When-Then structure with descriptive names
- **Error Structure**: All 404 responses use structured format: `{ error: { code: "NOT_FOUND", message: "Resource not found" } }`
- **Optional Fields**: MUST be included in response with `null` value (not omitted) per FR-009
- **Independent Testing**: User Story 1 can be tested completely in isolation (no dependencies on other features)

---

## Summary

**Total Tasks**: 45 tasks across 4 phases
**User Stories**: 1 (US1 - P1 priority)
**Parallel Opportunities**: 15 tasks marked [P] can run in parallel within their phases
**TDD Workflow**: 8 test tasks (RED phase) → Implementation (GREEN phase) → Refactoring
**Coverage Target**: 80% for service layer and API endpoints
**Estimated Time**: 3-4 hours for complete feature with TDD workflow

**MVP Scope**: Complete Phase 1 + 2 + 3 (User Story 1) = fully functional GET endpoint with tests

**Independent Test Criteria for US1**:
1. GET `/api/v1/announcements/{valid-uuid}` → 200 with announcement JSON
2. GET `/api/v1/announcements/{non-existent-uuid}` → 404 with error JSON
3. GET `/api/v1/announcements/abc-123` → 404 (malformed UUID)
4. Announcement with null optional fields → response includes null values
5. Announcements with any status (ACTIVE/FOUND/CLOSED) → all returned

**Format Validation**: ✅ All tasks follow required checklist format:
- Checkbox: `- [ ]` ✅
- Task ID: T001-T045 (sequential) ✅
- [P] marker: Present on 15 parallelizable tasks ✅
- [Story] label: [US1] present on all Phase 3 tasks ✅
- File paths: Included in all implementation and test tasks ✅

