# Tasks: User Login with JWT Authentication

**Input**: Design documents from `/specs/059-user-login-jwt/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/

**Tests**: Test requirements for this project:

**MANDATORY - Backend Unit Tests** (Backend-only feature):
- Services: `/server/src/services/__test__/` (Vitest), 80% coverage
- Utilities: `/server/src/lib/__test__/` (Vitest), 80% coverage
- Framework: Vitest
- Scope: Business logic and utility functions
- TDD Workflow: Red-Green-Refactor cycle (write failing test, minimal implementation, refactor)
- Convention: MUST follow Given-When-Then structure with descriptive test names

**MANDATORY - Backend Integration Tests** (Backend-only feature):
- Location: `/server/src/__test__/`
- Coverage: 80% for REST API endpoints
- Framework: Vitest + SuperTest
- Scope: End-to-end API tests (request → response)
- Convention: MUST follow Given-When-Then structure

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 Install jsonwebtoken dependency in `/server/package.json`
- [x] T002 [P] Install @types/jsonwebtoken dev dependency in `/server/package.json`
- [x] T002a [P] Update npm scripts in `/server/package.json` to use --env-file=.env flag (Node.js v24 native .env support)
- [x] T003 [P] Generate JWT_SECRET and add to `/server/.env` file
- [x] T004 [P] Add JWT_SECRET example to `/server/.env.example` with documentation
- [x] T005 [P] Create AuthResponse type definition in `/server/src/types/auth.d.ts`
- [x] T006 [P] Create JwtPayload type definition in `/server/src/types/auth.d.ts`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T007 Create InvalidCredentialsError class in `/server/src/lib/errors.ts` (401 status for login failures)
- [x] T008 Update error handler middleware to handle InvalidCredentialsError (401 status) in `/server/src/middlewares/error-handler-middleware.ts` - already handled by CustomError
- [x] T009 [P] Verify existing user-validation.ts validateCreateUser() function can be reused for login - ✅ validates email/password with same rules
- [x] T010 [P] Verify existing password-management.ts verifyPassword() function uses constant-time comparison - ✅ uses timingSafeEqual

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Existing User Authentication (Priority: P1) 🎯 MVP

**Goal**: Enable registered users to authenticate using email and password credentials, receiving a JWT access token valid for 1 hour to access protected features.

**Independent Test**: Submit valid credentials to POST /api/v1/users/login and verify that a valid JWT access token and user ID are returned. Verify token contains correct userId, iat, and exp claims.

### Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Write these tests FIRST following TDD workflow, ensure they FAIL before implementation**

**Backend Unit Tests** (TDD: Red-Green-Refactor):

- [x] T011 [P] [US1] RED: Write failing unit test for generateToken() in `/server/src/lib/__test__/jwt-utils.test.ts` (valid token generation)
- [x] T012 [P] [US1] RED: Write failing unit test for verifyToken() in `/server/src/lib/__test__/jwt-utils.test.ts` (valid token verification)
- [x] T013 [P] [US1] RED: Write failing unit test for verifyToken() with expired token in `/server/src/lib/__test__/jwt-utils.test.ts`
- [x] T014 [P] [US1] RED: Write failing unit test for verifyToken() with invalid signature in `/server/src/lib/__test__/jwt-utils.test.ts`
- [x] T015 [P] [US1] RED: Write failing unit test for verifyToken() with malformed token in `/server/src/lib/__test__/jwt-utils.test.ts`
- [x] T016 [P] [US1] RED: Write failing unit test for loginUser() with valid credentials in `/server/src/services/__test__/user-service.test.ts`
- [x] T017 [P] [US1] RED: Write failing unit test for loginUser() with non-existent email in `/server/src/services/__test__/user-service.test.ts`
- [x] T018 [P] [US1] RED: Write failing unit test for loginUser() with incorrect password in `/server/src/services/__test__/user-service.test.ts`
- [x] T019 [P] [US1] RED: Write failing unit test verifying identical error messages for invalid email and wrong password in `/server/src/services/__test__/user-service.test.ts`
- [x] T020 [P] [US1] RED: Write failing unit test for loginUser() with invalid email format (validation) in `/server/src/services/__test__/user-service.test.ts`
- [x] T021 [P] [US1] RED: Write failing unit test for loginUser() with missing password (validation) in `/server/src/services/__test__/user-service.test.ts`

**Backend Integration Tests** (TDD: Red-Green-Refactor):

- [x] T022 [P] [US1] RED: Write failing integration test for POST /api/v1/users/login with valid credentials (200) in `/server/src/__test__/http/login.test.ts`
- [x] T023 [P] [US1] RED: Write failing integration test for POST /api/v1/users/login with invalid email (401) in `/server/src/__test__/http/login.test.ts`
- [x] T024 [P] [US1] RED: Write failing integration test for POST /api/v1/users/login with wrong password (401) in `/server/src/__test__/http/login.test.ts`
- [x] T025 [P] [US1] RED: Write failing integration test for POST /api/v1/users/login with missing fields (400) in `/server/src/__test__/http/login.test.ts`
- [x] T026 [P] [US1] RED: Write failing integration test for POST /api/v1/users/login with invalid email format (400) in `/server/src/__test__/http/login.test.ts`
- [x] T027 [P] [US1] RED: Write failing integration test verifying response time similarity for invalid email vs wrong password in `/server/src/__test__/http/login.test.ts`
- [x] T028 [P] [US1] RED: Write failing integration test verifying JWT token structure and payload in `/server/src/__test__/http/login.test.ts`

### Implementation for User Story 1

**Backend** (TDD: Red-Green-Refactor):

- [x] T029 [P] [US1] GREEN: Implement generateToken() in `/server/src/lib/jwt-utils.ts` (minimal code to pass T011)
- [x] T030 [P] [US1] GREEN: Implement verifyToken() in `/server/src/lib/jwt-utils.ts` (minimal code to pass T012-T015)
- [x] T031 [US1] REFACTOR: Improve jwt-utils.ts code quality (extract helpers, JSDoc documentation, Clean Code principles)
- [x] T032 [US1] GREEN: Extend UserService with loginUser() method in `/server/src/services/user-service.ts` (minimal code to pass T016-T021)
- [x] T033 [US1] GREEN: Implement user enumeration prevention (dummy hash verification) in UserService.loginUser()
- [x] T034 [US1] REFACTOR: Improve loginUser() code quality (extract helpers, JSDoc documentation, max 3 nesting levels)
- [x] T035 [US1] GREEN: Create POST /login route in `/server/src/routes/users.ts` (minimal code to pass T022-T028)
- [x] T036 [US1] REFACTOR: Improve route handler code quality (error handling, JSDoc documentation)
- [x] T037 [US1] Run `npm test --coverage` from `/server` and verify 80% coverage for jwt-utils and user-service
- [x] T038 [P] [US1] Run `npm run lint` from `/server` and fix all ESLint violations

**Checkpoint**: At this point, User Story 1 (login endpoint) should be fully functional and testable independently

---

## Phase 4: User Story 2 - Automatic Authentication After Registration (Priority: P2)

**Goal**: Enable newly registered users to receive authentication credentials (JWT token) immediately upon successful registration, eliminating the need for a separate login step.

**Independent Test**: Create a new user account via POST /api/v1/users and verify that the response includes both user ID and JWT access token. Verify the token is valid and can be used immediately to access protected features.

### Tests for User Story 2 (MANDATORY) ✅

> **NOTE: Write these tests FIRST following TDD workflow, ensure they FAIL before implementation**

**Backend Unit Tests** (TDD: Red-Green-Refactor):

- [ ] T039 [P] [US2] RED: Write failing unit test for registerUser() returning accessToken in `/server/src/services/__test__/user-service.test.ts`
- [ ] T040 [P] [US2] RED: Write failing unit test verifying token contains correct user ID in `/server/src/services/__test__/user-service.test.ts`
- [ ] T041 [P] [US2] RED: Write failing unit test verifying token has correct expiration (1 hour) in `/server/src/services/__test__/user-service.test.ts`

**Backend Integration Tests** (TDD: Red-Green-Refactor):

- [ ] T042 [P] [US2] RED: Write failing integration test for POST /api/v1/users returning 201 with id and accessToken in `/server/src/__test__/http/users.test.ts`
- [ ] T043 [P] [US2] RED: Write failing integration test verifying token is valid JWT in `/server/src/__test__/http/users.test.ts`
- [ ] T044 [P] [US2] RED: Write failing integration test verifying token payload contains userId in `/server/src/__test__/http/users.test.ts`
- [ ] T045 [P] [US2] RED: Write failing integration test verifying token expires after 1 hour in `/server/src/__test__/http/users.test.ts`

### Implementation for User Story 2

**Backend** (TDD: Red-Green-Refactor):

- [ ] T046 [US2] GREEN: Extend UserService.registerUser() to generate and return JWT token in `/server/src/services/user-service.ts` (minimal code to pass T039-T041)
- [ ] T047 [US2] REFACTOR: Improve registerUser() code quality (DRY principle - reuse token generation)
- [ ] T048 [US2] GREEN: Update POST /users route handler to return AuthResponse in `/server/src/routes/users.ts` (minimal code to pass T042-T045)
- [ ] T049 [US2] REFACTOR: Ensure consistent response structure across login and registration
- [ ] T050 [US2] Run `npm test --coverage` from `/server` and verify 80% coverage maintained
- [ ] T051 [P] [US2] Run `npm run lint` from `/server` and fix all ESLint violations

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently (login + registration with JWT)

---

## Phase 5: User Story 3 - Clear Error Feedback on Invalid Input (Priority: P3)

**Goal**: Provide users with clear, specific error messages when they make mistakes during login (typos, validation errors, malformed input) to help them correct their input without requiring support.

**Independent Test**: Submit various invalid inputs (invalid email format, missing fields, password too short, extra fields) and verify that appropriate HTTP status codes (400) and descriptive error messages are returned for each case.

### Tests for User Story 3 (MANDATORY) ✅

> **NOTE: Write these tests FIRST following TDD workflow, ensure they FAIL before implementation**

**Backend Unit Tests** (TDD: Red-Green-Refactor):

- [ ] T052 [P] [US3] RED: Write failing unit test for validation error with extra fields in `/server/src/services/__test__/user-service.test.ts`
- [ ] T053 [P] [US3] RED: Write failing unit test for validation error with password too long in `/server/src/services/__test__/user-service.test.ts`
- [ ] T054 [P] [US3] RED: Write failing unit test for validation error with email too long in `/server/src/services/__test__/user-service.test.ts`

**Backend Integration Tests** (TDD: Red-Green-Refactor):

- [ ] T055 [P] [US3] RED: Write failing integration test for POST /login with password too short (400) in `/server/src/__test__/http/login.test.ts`
- [ ] T056 [P] [US3] RED: Write failing integration test for POST /login with extra fields (400) in `/server/src/__test__/http/login.test.ts`
- [ ] T057 [P] [US3] RED: Write failing integration test for POST /login with empty email (400) in `/server/src/__test__/http/login.test.ts`
- [ ] T058 [P] [US3] RED: Write failing integration test verifying error message format consistency in `/server/src/__test__/http/login.test.ts`
- [ ] T059 [P] [US3] RED: Write failing integration test for POST /users with validation errors (400) in `/server/src/__test__/http/users.test.ts`

### Implementation for User Story 3

**Backend** (TDD: Red-Green-Refactor):

- [ ] T060 [US3] GREEN: Verify existing validation in user-validation.ts handles all error cases (minimal changes if needed)
- [ ] T061 [US3] GREEN: Verify error handler middleware returns consistent validation error format (minimal changes if needed)
- [ ] T062 [US3] REFACTOR: Improve validation error messages for clarity (user-friendly language)
- [ ] T063 [US3] Run `npm test --coverage` from `/server` and verify 80% coverage maintained
- [ ] T064 [P] [US3] Run `npm run lint` from `/server` and fix all ESLint violations

**Checkpoint**: All user stories should now be independently functional with comprehensive error handling

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories and final validation

- [ ] T065 [P] Add JSDoc documentation to all public APIs in `/server/src/lib/jwt-utils.ts`
- [ ] T066 [P] Add JSDoc documentation to new methods in `/server/src/services/user-service.ts`
- [ ] T067 [P] Verify all functions follow Clean Code principles (max 3 nesting levels, DRY, descriptive names)
- [ ] T068 Run final coverage report: `npm test --coverage` from `/server` and verify ≥80%
- [ ] T069 [P] Run final linter check: `npm run lint` from `/server` and ensure zero violations
- [ ] T070 [P] Verify JWT_SECRET configuration in both `server/.env` and `server/.env.example`
- [ ] T071 Manual testing: Follow quickstart.md to validate complete authentication flow
- [ ] T072 [P] Manual testing: Verify user enumeration prevention (timing analysis)
- [ ] T073 [P] Manual testing: Verify JWT token structure using jwt.io
- [ ] T074 [P] Security review: Verify constant-time password verification is used
- [ ] T075 [P] Security review: Verify minimal JWT payload (user ID only)
- [ ] T076 [P] Security review: Verify 1-hour token expiration is configured

---

## Phase 7: Deployment Configuration

**Purpose**: Configure production environment for secure JWT authentication deployment

- [ ] T077 [P] Create `/deployment/.env.example` with JWT_SECRET placeholder and documentation
- [ ] T078 [P] Update `/deployment/docker-compose.yml` to use env_file for backend service
- [ ] T079 Update `/deployment/scripts/deploy.sh` to auto-generate `.env` file with secure JWT_SECRET
- [ ] T080 [P] Add `deployment/.env` to `.gitignore` to prevent committing secrets
- [ ] T081 [P] Add `server/.env` to `.gitignore` (if not already present)
- [ ] T082 Update `/deployment/README.md` with .env file documentation and JWT_SECRET management instructions
- [ ] T083 [P] Document secret rotation procedure in deployment README
- [ ] T084 Manual verification: Deploy to test environment and verify JWT_SECRET is loaded from .env file

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-5)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Phase 6)**: Depends on all user stories being complete
- **Deployment Configuration (Phase 7)**: Can proceed after Phase 6 OR in parallel with Phase 6 (independent tasks)

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Reuses JWT utilities from US1 but independently testable
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Enhances error handling from US1/US2 but independently testable

### Within Each User Story (TDD Workflow)

1. **RED Phase**: Tests MUST be written and FAIL before implementation
2. **GREEN Phase**: Write minimal code to pass tests
3. **REFACTOR Phase**: Improve code quality without changing behavior
4. **Verification**: Run coverage and linter

### Parallel Opportunities

- **Phase 1 (Setup)**: Tasks T002, T002a, T003-T006 marked [P] can run in parallel after T001 completes
- **Phase 2 (Foundational)**: Tasks T009-T010 marked [P] can run in parallel
- **Within User Story 1**: All test tasks T011-T028 marked [P] can be written in parallel
- **Within User Story 2**: All test tasks T039-T045 marked [P] can be written in parallel
- **Within User Story 3**: All test tasks T052-T059 marked [P] can be written in parallel
- **User Stories**: After Foundational phase, US1, US2, and US3 can be implemented in parallel by different developers
- **Phase 6 (Polish)**: Tasks T065-T067, T070, T072-T076 marked [P] can run in parallel
- **Phase 7 (Deployment)**: Tasks T077-T078, T080-T083 marked [P] can run in parallel (T079 must complete before T084)

---

## Parallel Example: User Story 1

```bash
# Launch all test writing tasks for User Story 1 together (RED phase):
Task: "T011 - Write failing unit test for generateToken()"
Task: "T012 - Write failing unit test for verifyToken()"
Task: "T013 - Write failing unit test for verifyToken() with expired token"
Task: "T014 - Write failing unit test for verifyToken() with invalid signature"
Task: "T015 - Write failing unit test for verifyToken() with malformed token"
Task: "T016 - Write failing unit test for loginUser() with valid credentials"
# ... all other test tasks marked [P]

# Then implement together (GREEN phase):
Task: "T029 - Implement generateToken()"
Task: "T030 - Implement verifyToken()"
# ... followed by sequential refactor and integration
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1 (Login endpoint)
4. **STOP and VALIDATE**: Test login endpoint independently using quickstart.md
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 (Login) → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 (Registration with JWT) → Test independently → Deploy/Demo
4. Add User Story 3 (Error handling) → Test independently → Deploy/Demo
5. Complete Polish phase → Final validation
6. Complete Deployment Configuration → Production ready
7. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1 (Login endpoint with tests)
   - Developer B: User Story 2 (Registration JWT with tests)
   - Developer C: User Story 3 (Error handling with tests)
3. Stories complete and integrate independently

---

## Notes

- **[P]** tasks = different files, no dependencies
- **[Story]** label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- **TDD Workflow**: RED (failing test) → GREEN (minimal implementation) → REFACTOR (code quality)
- Verify tests fail before implementing
- Run coverage after each story (target: 80%)
- Run linter after each story (target: zero violations)
- Stop at any checkpoint to validate story independently
- Backend-only feature: No mobile/web UI changes required
- Reuse existing validation, password management, and repository code
- Security-first: User enumeration prevention, constant-time verification, minimal JWT payload
- **.env Loading**:
  - **Development**: Node.js v24 native --env-file flag (no dotenv dependency)
  - **Production**: Docker Compose env_file with auto-generated secrets
- **Total Tasks**: 85 (7 setup + 4 foundational + 29 US1 + 13 US2 + 13 US3 + 12 polish + 8 deployment)

