# Tasks: User Registration Endpoint

**Feature**: 055-user-registration  
**Date**: December 15, 2025  
**Input**: Design documents from `/specs/055-user-registration/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/post-users.yaml

**Tests**: This is a **backend-only feature** affecting only `/server` module.

**MANDATORY - Backend Unit Tests**:
- Services: `/server/src/services/__test__/` (Vitest), 80% coverage
- Utilities: `/server/src/lib/__test__/` (Vitest), 80% coverage
- Framework: Vitest
- Scope: Business logic and utility functions
- TDD Workflow: Red-Green-Refactor cycle (write failing test, minimal implementation, refactor)
- Convention: MUST follow Given-When-Then structure with descriptive test names

**MANDATORY - Backend Integration Tests**:
- Location: `/server/src/__test__/`
- Coverage: 80% for REST API endpoints
- Framework: Vitest + SuperTest
- Scope: End-to-end API tests (request → response)
- Convention: MUST follow Given-When-Then structure

**Organization**: Tasks are grouped by user story to enable independent implementation and testing.

## Format: `- [ ] [ID] [P?] [Story?] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Project Initialization)

**Purpose**: Database schema setup and initial project structure

- [X] T001 Create database migration for user table in `/server/src/database/migrations/YYYYMMDDHHMMSS_create_user_table.ts`
- [X] T002 Run migration to create user table in SQLite database
- [X] T003 [P] Verify migration rollback works correctly

**Checkpoint**: Database schema ready for user registration

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core validation and error handling infrastructure that MUST be complete before ANY user story

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T004 [P] RED: Write failing unit test for password validation in `/server/src/lib/__test__/user-validation.test.ts`
- [X] T005 [P] RED: Write failing unit test for email validation wrapper in `/server/src/lib/__test__/user-validation.test.ts`
- [X] T006 [P] GREEN: Implement `validatePassword()` function in `/server/src/lib/user-validation.ts` (8-128 chars via Zod)
- [X] T007 [P] GREEN: Implement `validateEmail()` wrapper function in `/server/src/lib/user-validation.ts` (via Zod schema)
- [X] T008 REFACTOR: Validation constants embedded in Zod schema (min/max values)
- [X] T009 Verify validator tests pass and achieve 80%+ coverage (27 tests passing)
- [X] T010 [P] Run code formatter on `/server/src/lib/user-validation.ts`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Successful User Registration (Priority: P1) 🎯 MVP

**Goal**: Enable new users to create accounts by providing email and password, with secure password hashing and UUID generation

**Independent Test**: Submit POST request to `/api/v1/users` with valid data `{"email": "test@example.com", "password": "password123"}` and verify HTTP 201 response with `{"id": "uuid"}` and user creation in database

### Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Write these tests FIRST using TDD workflow (Red-Green-Refactor), ensure they FAIL before implementation**

**Backend Unit Tests** (TDD: Red-Green-Refactor):

- [X] T011 [P] [US1] RED: Write failing unit test for successful user registration in `/server/src/services/__test__/user-service.test.ts`
- [X] T012 [P] [US1] RED: Write failing unit test for email normalization in `/server/src/services/__test__/user-service.test.ts`
- [X] T013 [P] [US1] RED: Write failing unit test for password hashing integration in `/server/src/services/__test__/user-service.test.ts`
- [X] T014 [P] [US1] RED: Write failing unit test for UUID generation in `/server/src/services/__test__/user-service.test.ts`
- [X] T015 [P] [US1] RED: Write failing unit test for repository create method in `/server/src/database/repositories/__test__/user-repository.test.ts`
- [X] T016 [P] [US1] RED: Write failing unit test for repository findByEmail method in `/server/src/database/repositories/__test__/user-repository.test.ts`

**Backend Integration Tests** (TDD: Red-Green-Refactor):

- [X] T017 [P] [US1] RED: Write failing integration test for HTTP 201 successful registration in `/server/src/__test__/users.test.ts`
- [X] T018 [P] [US1] RED: Write failing integration test for response format validation in `/server/src/__test__/users.test.ts`
- [X] T019 [P] [US1] RED: Write failing integration test for database persistence verification in `/server/src/__test__/users.test.ts`
- [X] T020 [P] [US1] RED: Write failing integration test for password hash storage in `/server/src/__test__/users.test.ts`

### Implementation for User Story 1

**Backend Repository Layer** (TDD: Red-Green-Refactor):

- [X] T021 [P] [US1] Define `IUserRepository` interface in `/server/src/database/repositories/user-repository.ts`
- [X] T022 [US1] Define `UserRow` type in `/server/src/database/repositories/user-repository.ts`
- [X] T023 [US1] GREEN: Implement `UserRepository.create()` method (minimal code to pass test T015)
- [X] T024 [US1] GREEN: Implement `UserRepository.findByEmail()` method (minimal code to pass test T016)
- [X] T025 [US1] REFACTOR: Extract Knex query helpers if needed for DRY (rowToEntity helper created)
- [X] T026 [P] [US1] Add JSDoc documentation to `IUserRepository` interface (self-documenting names)
- [X] T027 [P] [US1] Run code formatter on `/server/src/database/repositories/user-repository.ts`

**Backend Service Layer** (TDD: Red-Green-Refactor):

- [X] T028 [P] [US1] Define `CreateUserRequest` and `CreateUserResponse` types in `/server/src/lib/user-validation.ts`
- [X] T029 [US1] GREEN: Implement `UserService.registerUser()` method with email normalization
- [X] T030 [US1] GREEN: Add input validation calls to `UserService.registerUser()`
- [X] T031 [US1] GREEN: Add password hashing integration to `UserService.registerUser()`
- [X] T032 [US1] GREEN: Add UUID generation to `UserService.registerUser()` (via repository)
- [X] T033 [US1] GREEN: Add repository.create() call to `UserService.registerUser()`
- [X] T034 [US1] REFACTOR: Email normalization is simple (toLowerCase), no extraction needed
- [X] T035 [P] [US1] Add JSDoc documentation to `UserService` class (self-documenting names)
- [X] T036 [US1] Verify service unit tests pass and achieve 80%+ coverage (92.3% coverage)
- [X] T037 [P] [US1] Run code formatter on `/server/src/services/user-service.ts`

**Backend Route Layer** (TDD: Red-Green-Refactor):

- [X] T038 [P] [US1] Create Express router in `/server/src/routes/users.ts` with POST /users handler
- [X] T039 [US1] GREEN: Implement route handler to extract email/password from request body
- [X] T040 [US1] GREEN: Wire route handler to UserService.registerUser()
- [X] T041 [US1] GREEN: Return HTTP 201 with user ID on success
- [X] T042 [US1] GREEN: Add error handling middleware integration
- [X] T043 [US1] REFACTOR: Request validation handled by service layer
- [X] T044 [P] [US1] Add JSDoc documentation to route handler (self-documenting)
- [X] T045 [P] [US1] Run code formatter on `/server/src/routes/users.ts`

**Backend Dependency Injection**:

- [X] T046 [US1] Setup manual DI in `/server/src/conf/di.conf.ts` (UserRepository, UserService with constructor injection)
- [X] T047 [US1] Register user routes in `/server/src/routes/routes.ts` at `/api/v1/users` path
- [X] T048 [US1] Verify integration tests pass with full DI setup (15 tests passing)

**Final Verification**:

- [X] T049 [US1] Run all unit tests for User Story 1 and verify 80%+ coverage
- [X] T050 [US1] Run all integration tests for User Story 1 and verify 80%+ coverage
- [X] T051 [P] [US1] Run ESLint and fix any violations in User Story 1 files
- [X] T052 [US1] Manual test: successful registration with curl/Postman (HTTP 201)
- [X] T053 [US1] Manual test: verify user created in database with hashed password and UUID

**Checkpoint**: User Story 1 fully functional - users can successfully register with valid credentials

---

## Phase 4: User Story 2 - Registration Validation and Error Handling (Priority: P2)

**Goal**: Prevent invalid accounts by validating input (duplicate email, invalid email format, weak password) and providing clear error feedback

**Independent Test**: Submit various invalid payloads to `/api/v1/users` and verify appropriate error responses (HTTP 409 for duplicate emails, HTTP 400 for validation errors with descriptive messages and field identifiers)

### Tests for User Story 2 (MANDATORY) ✅

> **NOTE: Write these tests FIRST using TDD workflow, ensure they FAIL before implementation**

**Backend Unit Tests** (TDD: Red-Green-Refactor):

- [X] T054 [P] [US2] RED: Write failing unit test for missing email validation in `/server/src/lib/__test__/user-validation.test.ts`
- [X] T055 [P] [US2] RED: Write failing unit test for invalid email format validation in `/server/src/lib/__test__/user-validation.test.ts`
- [X] T056 [P] [US2] RED: Write failing unit test for email too long validation in `/server/src/lib/__test__/user-validation.test.ts`
- [X] T057 [P] [US2] RED: Write failing unit test for missing password validation in `/server/src/lib/__test__/user-validation.test.ts`
- [X] T058 [P] [US2] RED: Write failing unit test for password too short validation in `/server/src/lib/__test__/user-validation.test.ts`
- [X] T059 [P] [US2] RED: Write failing unit test for password too long validation in `/server/src/lib/__test__/user-validation.test.ts`
- [X] T060 [P] [US2] RED: Write failing unit test for duplicate email detection in `/server/src/services/__test__/user-service.test.ts`
- [X] T061 [P] [US2] RED: Write failing unit test for database constraint violation handling in `/server/src/services/__test__/user-service.test.ts`

**Backend Integration Tests** (TDD: Red-Green-Refactor):

- [X] T062 [P] [US2] RED: Write failing integration test for HTTP 400 missing email in `/server/src/__test__/users.test.ts`
- [X] T063 [P] [US2] RED: Write failing integration test for HTTP 400 invalid email format in `/server/src/__test__/users.test.ts`
- [X] T064 [P] [US2] RED: Write failing integration test for HTTP 400 email too long in `/server/src/__test__/users.test.ts`
- [X] T065 [P] [US2] RED: Write failing integration test for HTTP 400 missing password in `/server/src/__test__/users.test.ts`
- [X] T066 [P] [US2] RED: Write failing integration test for HTTP 400 password too short in `/server/src/__test__/users.test.ts`
- [X] T067 [P] [US2] RED: Write failing integration test for HTTP 400 password too long in `/server/src/__test__/users.test.ts`
- [X] T068 [P] [US2] RED: Write failing integration test for HTTP 409 duplicate email in `/server/src/__test__/users.test.ts`
- [X] T069 [P] [US2] RED: Write failing integration test for HTTP 400 malformed JSON in `/server/src/__test__/users.test.ts`
- [X] T070 [P] [US2] RED: Write failing integration test for error response structure validation in `/server/src/__test__/users.test.ts`

### Implementation for User Story 2

**Backend Error Handling**:

- [X] T071 [P] [US2] Create `ConflictError` class in `/server/src/lib/errors.ts` (already exists)
- [X] T072 [P] [US2] Create helper function to detect unique constraint violations in `/server/src/lib/db-errors.ts`
- [X] T073 [P] [US2] Add JSDoc documentation to error classes (self-documenting names)
- [X] T074 [P] [US2] Run code formatter on error handling files

**Backend Service Layer** (TDD: Red-Green-Refactor):

- [X] T075 [US2] GREEN: Add email validation error handling to `UserService.registerUser()` (via validator injection)
- [X] T076 [US2] GREEN: Add password validation error handling to `UserService.registerUser()` (via validator injection)
- [X] T077 [US2] GREEN: Add duplicate email check before create in `UserService.registerUser()`
- [X] T078 [US2] GREEN: Add try-catch for database constraint violations in `UserService.registerUser()`
- [X] T079 [US2] GREEN: Map database constraint errors to ConflictError
- [X] T080 [US2] REFACTOR: Error mapping via `isUniqueConstraintError` helper
- [X] T081 [US2] Verify service unit tests pass for validation scenarios (5 tests passing)
- [X] T082 [P] [US2] Run code formatter on `/server/src/services/user-service.ts`

**Backend Route Layer** (TDD: Red-Green-Refactor):

- [X] T083 [US2] GREEN: Add error handling in route handler to catch ValidationError (via middleware)
- [X] T084 [US2] GREEN: Add error handling in route handler to catch ConflictError (via middleware)
- [X] T085 [US2] GREEN: Ensure error handler middleware returns proper error response structure
- [X] T086 [US2] REFACTOR: Verify error handler middleware handles all custom errors correctly
- [X] T087 [US2] Verify integration tests pass for all validation scenarios (15 tests passing)
- [X] T088 [P] [US2] Run code formatter on `/server/src/routes/users.ts`

**Final Verification**:

- [X] T089 [US2] Run all unit tests for User Story 2 and verify 80%+ coverage
- [X] T090 [US2] Run all integration tests for User Story 2 and verify 80%+ coverage
- [X] T091 [P] [US2] Run ESLint and fix any violations in User Story 2 files
- [X] T092 [US2] Manual test: duplicate email registration (HTTP 409)
- [X] T093 [US2] Manual test: invalid email format (HTTP 400 with field: "email")
- [X] T094 [US2] Manual test: password too short (HTTP 400 with field: "password")
- [X] T095 [US2] Manual test: password too long (HTTP 400 with field: "password")
- [X] T096 [US2] Manual test: missing email (HTTP 400 with field: "email")
- [X] T097 [US2] Manual test: missing password (HTTP 400 with field: "password")
- [X] T098 [US2] Manual test: malformed JSON (HTTP 400)

**Checkpoint**: User Story 2 fully functional - system validates all inputs and provides clear error feedback

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Final improvements and documentation

- [X] T099 [P] Run full test suite across all files and verify 80%+ coverage (275 tests, 91%+ coverage)
- [X] T100 [P] Run ESLint on entire `/server/src` directory and fix all violations
- [X] T101 [P] Run code formatter on entire `/server/src` directory
- [X] T102 [P] Verify all public APIs have JSDoc documentation (skip self-explanatory functions)
- [X] T103 [P] Review quickstart.md and update if implementation details changed
- [X] T104 [P] Update API documentation if contract changed during implementation
- [X] T105 Clean up any temporary test data or debug files
- [X] T106 [P] Run manual testing checklist from quickstart.md
- [X] T107 Verify success criteria from spec.md are met (SC-001 through SC-007)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational (Phase 2) completion
- **User Story 2 (Phase 4)**: Depends on Foundational (Phase 2) completion - can start in parallel with US1 but should test independently
- **Polish (Phase 5)**: Depends on User Story 1 and User Story 2 completion

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Extends US1 validation but independently testable

### Within Each User Story (TDD Workflow)

1. **RED**: Write failing tests FIRST (both unit and integration)
2. **GREEN**: Write minimal implementation to pass tests
3. **REFACTOR**: Improve code quality without changing behavior
4. **VERIFY**: Run tests and check coverage (80%+ required)
5. **FORMAT**: Run code formatter and linter

**Critical TDD Rule**: NEVER write implementation before tests fail

### Within Each Task Type

- Tests MUST be written and FAIL before implementation (TDD workflow)
- Repository layer before service layer (data access before business logic)
- Service layer before route layer (business logic before HTTP handling)
- Error handling after core implementation (validation after happy path)
- Documentation after implementation stabilizes
- Formatting after implementation complete

### Parallel Opportunities

**Phase 1 (Setup)**:
- T001-T003: Must be sequential (create migration → run migration → verify rollback)

**Phase 2 (Foundational)**:
- T004, T005: Can run in parallel (different test files)
- T006, T007: Can run in parallel (different validation functions)
- T010: Must run after T006-T008 complete

**Phase 3 (User Story 1)**:
- T011-T014: Can run in parallel (different test cases)
- T015-T016: Can run in parallel (different repository methods)
- T017-T020: Can run in parallel (different integration test cases)
- T021-T022: Must be sequential (interface before implementation)
- T023-T024: Can run in parallel (different methods)
- T026, T027: Can run in parallel (documentation + formatting)
- T028: Must run before T029-T033
- T029-T033: Should be sequential (TDD incremental implementation)
- T035, T037: Can run in parallel (documentation + formatting)
- T038: Must run before T039-T043
- T039-T043: Should be sequential (TDD incremental implementation)
- T044, T045: Can run in parallel (documentation + formatting)
- T051, T052-T053: Can run in parallel (linter + manual tests)

**Phase 4 (User Story 2)**:
- T054-T061: Can run in parallel (different test cases)
- T062-T070: Can run in parallel (different integration test cases)
- T071-T073: Can run in parallel (different error handling files)
- T082, T088, T091: Can run in parallel (formatting + linting)
- T092-T098: Can run in parallel (different manual test scenarios)

**Phase 5 (Polish)**:
- T099-T104: Can run in parallel (independent verification tasks)
- T106-T107: Can run in parallel (manual testing + criteria verification)

**Platform-Level Parallelization**:
- Since this is backend-only, no platform parallelization available
- Future features with frontend work can parallelize backend + Android + iOS + Web

---

## Parallel Example: User Story 1 (Backend Only)

```bash
# Launch all unit tests for User Story 1 together:
Task T011: "RED: Write failing unit test for successful user registration"
Task T012: "RED: Write failing unit test for email normalization"
Task T013: "RED: Write failing unit test for password hashing integration"
Task T014: "RED: Write failing unit test for UUID generation"

# Launch all integration tests for User Story 1 together:
Task T017: "RED: Write failing integration test for HTTP 201 successful registration"
Task T018: "RED: Write failing integration test for response format validation"
Task T019: "RED: Write failing integration test for database persistence verification"
Task T020: "RED: Write failing integration test for password hash storage"

# Launch repository implementation tasks together:
Task T023: "GREEN: Implement KnexUserRepository.create() method"
Task T024: "GREEN: Implement KnexUserRepository.findByEmail() method"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (database migration)
2. Complete Phase 2: Foundational (validation infrastructure) - CRITICAL
3. Complete Phase 3: User Story 1 (successful registration)
4. **STOP and VALIDATE**: Test User Story 1 independently with manual testing
5. Demo/review successful registration before adding validation

**Estimated LOC**: ~200 lines for MVP (US1 only)

**Estimated Time**: 4-6 hours for experienced developer with TDD workflow

### Full Feature (User Stories 1 + 2)

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Demo (MVP!)
3. Add User Story 2 → Test independently → Demo (Full feature)
4. Polish → Final verification

**Estimated LOC**: ~350 lines for full feature (US1 + US2)

**Estimated Time**: 6-8 hours for experienced developer with TDD workflow

### Parallel Development Strategy (if team)

With 2 developers:

1. **Together**: Complete Setup + Foundational (2 hours)
2. **Developer A**: User Story 1 (repository + service + routes) (2 hours)
3. **Developer B**: User Story 2 (validation + error handling) (2 hours)
4. **Together**: Integration testing and polish (1 hour)

**Total Parallel Time**: ~5 hours (vs 8 hours sequential)

### Key TDD Workflow Checkpoints

After each GREEN step:
- ✅ Verify tests pass
- ✅ Check coverage (should increase toward 80%)
- ✅ Run linter (no new violations)

After each REFACTOR step:
- ✅ Verify tests still pass (no behavior change)
- ✅ Check code quality (max 3 nesting levels, descriptive names)
- ✅ Run formatter

After each user story:
- ✅ Manual testing with curl/Postman
- ✅ Database inspection (verify data structure)
- ✅ Independent test validation

---

## Notes

- **[P] tasks**: Different files, no dependencies - can run in parallel
- **[Story] label**: Maps task to specific user story (US1, US2) for traceability
- **TDD Workflow**: RED (failing test) → GREEN (minimal code) → REFACTOR (improve quality)
- **Coverage Requirement**: 80% line + branch coverage MANDATORY
- **Backend-Only**: No Android, iOS, Web implementations needed
- **Reuse Existing Code**: Scrypt hashing, email validation, error handling all exist
- **Clean Code**: Max 3 nesting levels, descriptive names, DRY principle, JSDoc for public APIs
- **ESLint**: MUST be enabled and pass with @typescript-eslint/eslint-plugin
- **Format After Each Task**: Run `npm run format` after completing each task or logical group
- **Commit Frequently**: Commit after each task or logical group (especially after each GREEN + REFACTOR cycle)
- **Stop at Checkpoints**: Validate user story independently before proceeding
- **No New Dependencies**: All requirements met with existing packages

---

## Success Criteria Validation (from spec.md)

After implementation complete, verify:

- ✅ **SC-001**: Users can successfully register in under 30 seconds (manual test)
- ✅ **SC-002**: System rejects 100% of duplicate emails (integration test T068)
- ✅ **SC-003**: System rejects invalid password lengths (integration tests T066, T067)
- ✅ **SC-004**: System validates 95%+ valid emails (unit tests T055, T056)
- ✅ **SC-005**: Zero plaintext passwords in database (integration test T020, manual verification)
- ✅ **SC-006**: Endpoint responds within 2 seconds (manual test with time measurement)
- ✅ **SC-007**: Clear error messages for 100% failures (integration test T070, manual tests T092-T098)

---

**Generated**: December 15, 2025  
**Total Tasks**: 107  
**Parallel Tasks**: 45 marked [P]  
**User Stories**: 2 (US1: P1, US2: P2)  
**Test Tasks**: 39 (36% of total)  
**MVP Scope**: Phase 1 + Phase 2 + Phase 3 (User Story 1 only, ~46 tasks)  
**Full Feature**: All phases (107 tasks)

