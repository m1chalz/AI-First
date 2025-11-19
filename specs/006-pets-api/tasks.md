# Tasks: Lost Pets API Endpoint

**Feature**: 006-pets-api  
**Branch**: `006-pets-api`  
**Input**: Design documents from `/specs/006-pets-api/`

**Prerequisites**: 
- ✅ plan.md (tech stack, implementation strategy)
- ✅ spec.md (user stories, requirements)
- ✅ research.md (technology decisions)
- ✅ data-model.md (database schema, TypeScript types)
- ✅ contracts/announcements-api.yaml (OpenAPI specification)

**Project Type**: Backend API (Node.js + Express in `/server` directory)

**Test Requirements**:

**MANDATORY - Backend Unit Tests** (TDD: Red-Green-Refactor):
- Location: `/server/src/services/__test__/`, `/server/src/lib/__test__/`
- Coverage: 80% line + branch coverage
- Framework: Vitest
- Scope: Business logic (services) and utility functions
- Convention: MUST follow Given-When-Then structure with descriptive test names

**MANDATORY - Backend Integration Tests** (TDD: Red-Green-Refactor):
- Location: `/server/src/__test__/`
- Coverage: 80% for REST API endpoints
- Framework: Vitest + SuperTest
- Scope: End-to-end API tests (HTTP request → response)
- Convention: MUST follow Given-When-Then structure

**Organization**: Tasks follow TDD workflow (Red-Green-Refactor) for the single user story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[US1]**: User Story 1 task
- Include exact file paths in descriptions

---

## Phase 1: Setup (Project Infrastructure)

**Purpose**: Database schema and environment setup

- [ ] T001 Create database migration for announcement table in `/server/src/database/migrations/YYYYMMDDHHMMSS_create_announcement_table.ts` using Knex schema builder (no CHECK constraints, use IF NOT EXISTS)
- [ ] T002 Create seed file in `/server/src/database/seeds/001_announcements.ts` with 5-10 example announcements (all species, mix of statuses, optional fields both present and null)
- [ ] T003 Run migration to create announcement table in SQLite database: `npm run knex:migrate`
- [ ] T004 Run seed to populate test data: `npm run knex:seed`

**Checkpoint**: Database schema ready with test data

---

## Phase 2: Foundational (Core TypeScript Types)

**Purpose**: Define shared TypeScript types that all layers depend on

**⚠️ CRITICAL**: No implementation work can begin until types are defined

- [ ] T005 Create TypeScript types in `/server/src/types/announcement.d.ts` (Species enum, Gender enum, AnnouncementStatus enum, Announcement interface, AnnouncementRow interface per data-model.md)

**Checkpoint**: Type definitions ready - TDD workflow can now begin

---

## Phase 3: User Story 1 - View All Lost Pet Announcements (Priority: P1) 🎯 MVP

**Goal**: Implement GET /api/v1/announcements endpoint that returns all lost pet announcements from the database with proper error handling and validation.

**Independent Test**: Make HTTP GET request to /api/v1/announcements and verify response structure matches OpenAPI spec with HTTP 200 status code.

**Acceptance Criteria** (from spec.md):
1. GET /api/v1/announcements returns HTTP 200 with JSON response containing all announcements in data array
2. Empty database returns HTTP 200 with empty data array
3. Each announcement includes all required fields (id, petName, species, breed, gender, description, location, locationRadius, lastSeenDate, email, phone, photoUrl, status)

### TDD Cycle 1: RED - Write Failing Tests ✅

> **CRITICAL**: Write ALL tests first and verify they FAIL before any implementation

**Integration Tests** (API endpoint behavior):
- [ ] T006 [P] [US1] RED: Create integration test file `/server/src/__test__/announcements.test.ts` with test "should return 200 with announcements array when database has data" (use SuperTest, Given-When-Then structure)
- [ ] T007 [P] [US1] RED: Add integration test "should return 200 with empty array when database is empty" in `/server/src/__test__/announcements.test.ts`

**Unit Tests** (service layer):
- [ ] T008 [P] [US1] RED: Create service unit test file `/server/src/services/__test__/announcement-service.test.ts` with test "should return all announcements when repository returns data" (use fake repository, Given-When-Then structure)
- [ ] T009 [P] [US1] RED: Add service unit test "should return empty array when repository returns no data" in `/server/src/services/__test__/announcement-service.test.ts`

**Unit Tests** (validation utilities):
- [ ] T010 [P] [US1] RED: Create validator unit test file `/server/src/lib/__test__/validators.test.ts` with tests for isValidEmail (valid formats, invalid formats, edge cases like no @, no domain, etc.)
- [ ] T011 [P] [US1] RED: Add validator unit tests for isValidPhone in `/server/src/lib/__test__/validators.test.ts` (valid formats with digits, no digits, empty string)

**Verify RED Phase**:
- [ ] T012 [US1] Run `npm test` from `/server` directory and confirm ALL tests fail with "not implemented" or "module not found" errors

**Checkpoint**: All tests written and failing - ready for implementation

---

### TDD Cycle 2: GREEN - Minimal Implementation ✅

> **Goal**: Write minimal code to make tests pass (don't optimize yet)

**Repository Layer** (database queries):
- [ ] T013 [US1] GREEN: Create repository interface IAnnouncementRepository in `/server/src/database/repositories/announcement-repository.ts` with findAll() method signature
- [ ] T014 [US1] GREEN: Implement AnnouncementRepository class in `/server/src/database/repositories/announcement-repository.ts` with findAll() using Knex query builder (SELECT * FROM announcement, map snake_case to camelCase)

**Service Layer** (business logic):
- [ ] T015 [US1] GREEN: Implement AnnouncementService in `/server/src/services/announcement-service.ts` with getAllAnnouncements() function that calls repository.findAll()

**Validation Layer** (utilities):
- [ ] T016 [P] [US1] GREEN: Implement isValidEmail() in `/server/src/lib/validators.ts` using basic regex /^[^\s@]+@[^\s@]+\.[^\s@]+$/ per research.md
- [ ] T017 [P] [US1] GREEN: Implement isValidPhone() in `/server/src/lib/validators.ts` using /\d/.test(phone) per research.md

**Route Handler** (HTTP endpoint):
- [ ] T018 [US1] GREEN: Create Express router in `/server/src/routes/announcements.ts` with GET /api/v1/announcements handler that calls AnnouncementService.getAllAnnouncements()
- [ ] T019 [US1] GREEN: Register announcements router in `/server/src/routes/index.ts` at path /api/v1/announcements
- [ ] T020 [US1] GREEN: Mount router in `/server/src/app.ts` (ensure announcements router is registered with Express app)

**Verify GREEN Phase**:
- [ ] T021 [US1] Run `npm test` from `/server` directory and confirm ALL tests now pass

**Checkpoint**: Tests passing with minimal implementation - ready for refactoring

---

### TDD Cycle 3: REFACTOR - Improve Code Quality ✅

> **Goal**: Apply Clean Code principles without changing behavior (tests must still pass)

**Code Quality Improvements**:
- [ ] T022 [P] [US1] REFACTOR: Add JSDoc documentation ONLY for non-obvious logic in `/server/src/services/announcement-service.ts` (skip if function name is self-explanatory, keep brief if needed)
- [ ] T023 [P] [US1] REFACTOR: Add JSDoc documentation ONLY for non-obvious validation logic in `/server/src/lib/validators.ts` (skip simple validators with clear names)
- [ ] T024 [P] [US1] REFACTOR: Review `/server/src/database/repositories/announcement-repository.ts` and add JSDoc only if query logic is complex (skip obvious CRUD operations)
- [ ] T025 [P] [US1] REFACTOR: Review `/server/src/routes/announcements.ts` route handler and add brief JSDoc only if behavior is non-obvious
- [ ] T026 [US1] REFACTOR: Extract reusable helper functions if any complex logic exists (check for max 3 nesting levels per Clean Code rules)
- [ ] T027 [US1] REFACTOR: Review naming in all files for clarity (no unclear abbreviations except id, db, api)
- [ ] T028 [US1] REFACTOR: Apply DRY principle - extract duplicated logic to reusable utilities

**Linting and Type Safety**:
- [ ] T029 [US1] Run `npm run lint` from `/server` directory and fix all ESLint violations
- [ ] T030 [US1] Run `npx tsc --noEmit` from `/server` directory and fix all TypeScript type errors

**Coverage Verification**:
- [ ] T031 [US1] Run `npm test -- --coverage` from `/server` directory and verify 80% coverage for services and lib
- [ ] T032 [US1] If coverage below 80%, add missing test cases to reach threshold

**Final Verification**:
- [ ] T033 [US1] Run `npm test` to confirm all tests still pass after refactoring
- [ ] T034 [US1] Manual smoke test: Start server with `npm run dev`, verify GET http://localhost:3000/api/v1/announcements returns seed data with HTTP 200

**Checkpoint**: User Story 1 complete - fully functional, tested, and documented

---

## Phase 4: Polish & Validation

**Purpose**: Final checks and documentation validation

- [ ] T035 [P] Verify all success criteria from spec.md are met (SC-001 through SC-006)
- [ ] T036 [P] Run quickstart.md manual testing section to validate setup instructions
- [ ] T037 [P] Verify OpenAPI contract matches actual API behavior (use curl or Postman)
- [ ] T038 Run final coverage report and confirm 80% threshold: `npm test -- --coverage`
- [ ] T039 Run final linting and confirm no violations: `npm run lint`
- [ ] T040 Verify database has seed data: `sqlite3 pets.db "SELECT COUNT(*) FROM announcement;"`

**Final Checkpoint**: Feature complete and ready for code review

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup (Phase 1) - BLOCKS all implementation
- **User Story 1 (Phase 3)**: Depends on Foundational (Phase 2)
  - **RED cycle** (T006-T012): Write all tests first
  - **GREEN cycle** (T013-T021): Implement to pass tests
  - **REFACTOR cycle** (T022-T034): Improve code quality
- **Polish (Phase 4)**: Depends on User Story 1 completion

### TDD Workflow Within User Story 1

**MANDATORY SEQUENCE** (DO NOT SKIP):
1. **RED**: Write failing tests (T006-T012) → Run `npm test` → ALL MUST FAIL
2. **GREEN**: Minimal implementation (T013-T021) → Run `npm test` → ALL MUST PASS
3. **REFACTOR**: Improve code (T022-T034) → Run `npm test` → ALL MUST STILL PASS

**Why this matters**: TDD ensures we build exactly what tests specify, no more, no less.

### Parallel Opportunities

**Within RED Phase**:
- All test file creation tasks (T006-T011) can run in parallel (different files)

**Within GREEN Phase**:
- T016 and T017 (validators) can run in parallel with other tasks
- T022-T028 (documentation and refactoring) can run in parallel (different files)

**Within REFACTOR Phase**:
- T022, T023, T024, T025 (JSDoc tasks) can all run in parallel (different files)
- T029 and T030 (linting and type checking) can run in parallel

**Within Polish Phase**:
- T035, T036, T037 can all run in parallel (independent verifications)

---

## Parallel Example: RED Phase

```bash
# Launch all test creation tasks together (different files):
Task T006: "Create integration test for 200 with data"
Task T007: "Add integration test for 200 empty array"
Task T008: "Create service unit test for returns data"
Task T009: "Add service unit test for empty array"
Task T010: "Create validator tests for email"
Task T011: "Add validator tests for phone"

# Then verify:
Task T012: "Run npm test and confirm ALL fail"
```

---

## Implementation Strategy

### TDD First Approach (MANDATORY)

This feature MUST follow strict TDD discipline:

1. **Complete Phase 1**: Setup (database schema)
2. **Complete Phase 2**: Foundational (TypeScript types)
3. **RED Phase**: Write ALL failing tests (T006-T012)
   - **STOP**: Run `npm test` → verify all FAIL
4. **GREEN Phase**: Minimal implementation (T013-T021)
   - **STOP**: Run `npm test` → verify all PASS
5. **REFACTOR Phase**: Improve quality (T022-T034)
   - **STOP**: Run `npm test` → verify still PASS
6. **Complete Phase 4**: Polish and validate
7. **DONE**: Feature ready for code review

### Why Only One User Story?

This feature has a single, well-defined scope:
- One entity (Announcement)
- One endpoint (GET /api/v1/announcements)
- No complex user journeys
- Delivers complete value in one increment

**Result**: Entire feature = MVP (no incremental delivery needed)

---

## Success Criteria Checklist

Verify implementation meets all criteria from spec.md:

- [ ] **SC-001**: API responds in <2 seconds for datasets up to 1000 records
- [ ] **SC-002**: Returns HTTP 200 for 100% of successful requests
- [ ] **SC-003**: Returns valid JSON structure in 100% of requests
- [ ] **SC-004**: Empty database returns empty array with HTTP 200
- [ ] **SC-005**: All announcements include required fields with valid data types (UUID, ISO 8601 date)
- [ ] **SC-006**: Seed data successfully populates 5-10 example announcements

---

## Notes

- **[P] tasks**: Different files, no dependencies, can run in parallel
- **[US1] label**: Maps task to User Story 1 for traceability
- **TDD discipline**: RED → GREEN → REFACTOR sequence is MANDATORY
- **80% coverage**: Non-negotiable target per project constitution
- **ESLint compliance**: All code must pass linting with TypeScript plugin
- **Documentation philosophy**: 
  - Only document non-obvious logic and behavior
  - Skip documentation for self-explanatory function/variable names
  - Keep all documentation short and concise (1-2 sentences max)
  - Don't comment simple, obvious code
- **Clean Code**: Max 3 nesting levels, descriptive naming, DRY principle
- **Given-When-Then**: All tests must follow this structure
- **No new dependencies**: Use Node.js built-in crypto.randomUUID() per research.md
- **Commit strategy**: Commit after each phase or logical task group
- **Stop at checkpoints**: Verify tests pass/fail at each TDD phase transition

---

## Total Task Count

- **Phase 1 (Setup)**: 4 tasks
- **Phase 2 (Foundational)**: 1 task
- **Phase 3 (User Story 1)**:
  - RED: 7 tasks
  - GREEN: 9 tasks
  - REFACTOR: 13 tasks
  - **Subtotal**: 29 tasks
- **Phase 4 (Polish)**: 6 tasks

**TOTAL**: 40 tasks for complete feature implementation

**Estimated Parallel Opportunities**: 15+ tasks can run in parallel within phases

**Suggested MVP Scope**: Complete all phases (feature is atomic, no partial delivery)

