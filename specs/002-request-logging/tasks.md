# Tasks: Request and Response Logging with Correlation ID

**Feature**: 002-request-logging  
**Input**: Design documents from `/specs/002-request-logging/`  
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, quickstart.md ✅

**Tests**: Unit tests provided for utility functions (`/src/lib/__test__/`) per Constitution Principle XIII (80% coverage requirement). Manual validation tasks (Phase 6) verify middleware integration.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `- [ ] [ID] [P?] [Story?] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Project Dependencies)

**Purpose**: Install dependencies and configure project infrastructure

- [ ] T001 Install Pino dependency in `/Users/pawelkedra/code/AI-First/server/package.json` (run `npm install pino@^8.0.0` from server/)
- [ ] T002 [P] Install pino-http dependency in `/Users/pawelkedra/code/AI-First/server/package.json` (run `npm install pino-http@^8.0.0` from server/)
- [ ] T003 [P] Verify ESLint configuration in `/Users/pawelkedra/code/AI-First/server/eslint.config.mjs` includes @typescript-eslint/eslint-plugin
- [ ] T004 [P] Verify TypeScript strict mode is enabled in `/Users/pawelkedra/code/AI-First/server/tsconfig.json`

---

## Phase 2: Foundational (Core Utilities)

**Purpose**: Core infrastructure that MUST be complete before user stories can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T005 Create request ID generator utility in `/Users/pawelkedra/code/AI-First/server/src/lib/requestIdGenerator.ts` (generates 10-character alphanumeric IDs using crypto.randomInt)
- [ ] T006 Create AsyncLocalStorage context manager in `/Users/pawelkedra/code/AI-First/server/src/lib/requestContext.ts` (exports requestContextStorage, getRequestId, setRequestContext)
- [ ] T007 Create log serializers utility in `/Users/pawelkedra/code/AI-First/server/src/lib/logSerializers.ts` (implements truncateBody for 10KB limit, isBinaryContent detection, serializeBody with truncation and binary omission)

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 2.5: Unit Tests for Utilities (TDD - Red Phase) ⚠️ CONSTITUTION COMPLIANCE

**Purpose**: Achieve 80% test coverage for utility functions per Constitution Principle XIII

**⚠️ CRITICAL**: Constitution Principle XIII (Backend Architecture & Quality Standards) is NON-NEGOTIABLE and requires 80% test coverage for `/src/lib/` utilities. These tests MUST be written following TDD workflow (Red-Green-Refactor).

**TDD Workflow**:
1. **RED**: Write failing test first (verify it fails)
2. **GREEN**: Implement minimal code to pass test
3. **REFACTOR**: Improve code quality while keeping tests passing

**Coverage Target**: 80% line + branch coverage for `/server/src/lib/`

- [ ] T007a [P] [TDD-RED] Create unit tests for requestIdGenerator in `/Users/pawelkedra/code/AI-First/server/src/lib/__test__/requestIdGenerator.test.ts` (test: 10-char length, alphanumeric only, uniqueness across 1000 calls, no collisions)
- [ ] T007b [P] [TDD-RED] Create unit tests for requestContext in `/Users/pawelkedra/code/AI-First/server/src/lib/__test__/requestContext.test.ts` (test: setRequestContext stores ID, getRequestId retrieves ID, getRequestId returns undefined when no context)
- [ ] T007c [P] [TDD-RED] Create unit tests for logSerializers in `/Users/pawelkedra/code/AI-First/server/src/lib/__test__/logSerializers.test.ts` (test: truncateBody at 10240 bytes, isBinaryContent detection for image/*, application/pdf, serializeBody with truncated flag, serializeBody with binaryOmitted flag)

**Checkpoint**: After Phase 2.5, run `npm test -- --coverage` from server/ and verify 80%+ coverage for `/src/lib/` before proceeding to Phase 3

**Note**: Middleware tests (T008, T012) are considered integration-level and tested via manual validation tasks in Phase 6 (T023-T028). Pure utility functions MUST have unit tests per constitution.

---

## Phase 3: User Story 1 - Request/Response Tracing for Debugging (Priority: P1) 🎯 MVP

**Goal**: Log all incoming HTTP requests and outgoing responses with complete details (method, URL, body, headers) in structured JSON format to enable debugging and troubleshooting.

**Independent Test**: Make any HTTP request to the backend (e.g., `curl http://localhost:3000/api/pets`) and verify that both request and response logs appear in stdout with all details (method, URL, body, headers, timestamps).

**Acceptance Criteria**:
- ✅ Every HTTP request is logged with method, URL, body, headers, and ISO8601 timestamp
- ✅ Every HTTP response is logged with method, URL, status, body, headers, response time, and ISO8601 timestamp
- ✅ Request/response bodies exceeding 10KB are truncated with `"truncated": true` flag
- ✅ Binary content is omitted from logs with `"binaryOmitted": true` flag
- ✅ Authorization header value is redacted to `***`
- ✅ All logs are structured JSON format

### Implementation for User Story 1

- [ ] T008 [P] [US1] Create Pino logger middleware in `/Users/pawelkedra/code/AI-First/server/src/middlewares/loggerMiddleware.ts` (configure pino-http with custom serializers, redact Authorization header, integrate logSerializers for body truncation/binary detection, configure timestamp: pino.stdTimeFunctions.isoTime for ISO8601 format per FR-008)
- [ ] T009 [US1] Register logger middleware in `/Users/pawelkedra/code/AI-First/server/src/app.ts` (import loggerMiddleware and add app.use(loggerMiddleware) BEFORE route registration)
- [ ] T010 [US1] Remove old console.log middleware from `/Users/pawelkedra/code/AI-First/server/src/app.ts` (delete lines 15-18: simple Request logging middleware)
- [ ] T011 [P] [US1] Add JSDoc documentation to loggerMiddleware.ts explaining Pino configuration, custom serializers, and redaction rules

**Checkpoint**: At this point, User Story 1 should be fully functional - all requests/responses are logged with structured JSON, truncation, binary detection, and header redaction

---

## Phase 4: User Story 2 - Request Correlation with Request ID (Priority: P1)

**Goal**: Generate a unique 10-character alphanumeric request ID for each HTTP request and include it in all log entries (request, response, and application logs) and in the response header to enable correlation and tracing across the entire request lifecycle.

**Independent Test**: Make a single HTTP request that triggers some application logging (e.g., `curl http://localhost:3000/api/pets`) and verify: (1) a unique 10-character alphanumeric request ID is generated, (2) the same request ID appears in request log, response log, and any application logs, (3) the response includes a `request-id` header with the generated ID, (4) making a second request generates a different request ID.

**Acceptance Criteria**:
- ✅ Each HTTP request generates a unique 10-character alphanumeric request ID (A-Z, a-z, 0-9)
- ✅ Request ID is included in all log entries for that request (request, response, application logs)
- ✅ Request ID is available throughout the request lifecycle via AsyncLocalStorage
- ✅ Response includes `request-id` header with the generated ID
- ✅ All application logs generated during request processing include the request ID

### Implementation for User Story 2

- [ ] T012 [P] [US2] Create request ID middleware in `/Users/pawelkedra/code/AI-First/server/src/middlewares/requestIdMiddleware.ts` (generate request ID using requestIdGenerator, set AsyncLocalStorage context, attach ID to req.id, add request-id response header)
- [ ] T013 [US2] Register request ID middleware in `/Users/pawelkedra/code/AI-First/server/src/app.ts` (import requestIdMiddleware and add app.use(requestIdMiddleware) BEFORE loggerMiddleware to ensure ID is available)
- [ ] T014 [US2] Integrate request ID into logger middleware in `/Users/pawelkedra/code/AI-First/server/src/middlewares/loggerMiddleware.ts` (configure pino-http to include requestId from AsyncLocalStorage in all logs via genReqId option)
- [ ] T015 [P] [US2] Add JSDoc documentation to requestIdMiddleware.ts explaining request ID generation, AsyncLocalStorage context, and header injection
- [ ] T016 [P] [US2] Add JSDoc documentation to requestIdGenerator.ts explaining 10-character format, uniqueness guarantees, and collision probability
- [ ] T017 [P] [US2] Add JSDoc documentation to requestContext.ts explaining AsyncLocalStorage usage and how to access request ID in services

**Checkpoint**: At this point, User Stories 1 AND 2 should both work - all requests/responses are logged with unique request IDs that enable correlation across all log entries

---

## Phase 5: User Story 3 - Log Search and Filtering (Priority: P2)

**Goal**: Enable operations engineers to quickly search and filter logs by request ID to retrieve all related log entries for troubleshooting and debugging production issues.

**Independent Test**: (1) Make several HTTP requests to generate logs with different request IDs, (2) capture one request ID from a response header, (3) search logs by that request ID using grep or jq, (4) verify that only logs related to that specific request appear in results (including request log, response log, and all application logs).

**Acceptance Criteria**:
- ✅ Logs can be filtered by request ID using standard tools (grep, jq)
- ✅ All logs for a single request share the same request ID
- ✅ Structured JSON format enables easy parsing and filtering
- ✅ Documentation provides examples of log searching and filtering

### Implementation for User Story 3

- [ ] T018 [P] [US3] Verify structured JSON log format in `/Users/pawelkedra/code/AI-First/server/src/middlewares/loggerMiddleware.ts` (ensure all logs are valid JSON with consistent field names)
- [ ] T019 [P] [US3] Add log correlation examples to quickstart.md (grep and jq examples for filtering by request ID, status code, URL pattern)
- [ ] T020 [P] [US3] Verify quickstart.md includes troubleshooting guide for common log search scenarios (find all logs for request ID, find errors, trace request lifecycle)

**Checkpoint**: All user stories should now be independently functional - logs can be searched, filtered, and correlated by request ID

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final improvements and validation

- [ ] T021 [P] Run ESLint on all new files (run `npm run lint` from server/) and fix any violations
- [ ] T022 [P] Verify all public APIs have JSDoc documentation (requestIdGenerator.ts, requestContext.ts, logSerializers.ts, loggerMiddleware.ts, requestIdMiddleware.ts)
- [ ] T023 [P] Test logging with large request body (>10KB) to verify truncation works correctly
- [ ] T024 [P] Test logging with binary content (image upload) to verify binary omission works correctly
- [ ] T025 [P] Test logging with Authorization header to verify redaction to `***` works correctly
- [ ] T026 [P] Verify ISO8601 timestamp format in logs matches expected format `YYYY-MM-DDTHH:mm:ss.sssZ` (configured in T008 via Pino timestamp option)
- [ ] T027 [P] Test request ID uniqueness by making 100 concurrent requests and verifying no ID collisions
- [ ] T028 [P] Verify response time logging in response logs (responseTime field in milliseconds)
- [ ] T029 Manual smoke test: Start dev server (`npm run dev` from server/), make requests, verify logs appear in stdout with request IDs
- [ ] T030 Update quickstart.md with final validation checklist (verify all features work as documented)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup (T001-T004) completion - BLOCKS all user stories
- **Unit Tests (Phase 2.5)**: Can start in parallel with Phase 2 utilities (T005-T007) using TDD
- **User Story 1 (Phase 3)**: Depends on Foundational (T005-T007) AND Unit Tests (T007a-T007c) completion
- **User Story 2 (Phase 4)**: Depends on User Story 1 (T008-T011) completion (requires logger middleware to be in place)
- **User Story 3 (Phase 5)**: Depends on User Story 2 (T012-T017) completion (requires request ID correlation to work)
- **Polish (Phase 6)**: Depends on all user stories (T008-T020) completion

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P1)**: Depends on User Story 1 - Needs logger middleware to inject request ID into logs
- **User Story 3 (P2)**: Depends on User Story 2 - Needs request ID correlation to enable searching

### Within Each Phase

- Tasks marked [P] can run in parallel (different files, no dependencies)
- Tasks without [P] must run sequentially in order
- Checkpoint after each user story: Verify story is complete before moving to next

### Parallel Opportunities

- **Phase 1 (Setup)**: T001-T004 can all run in parallel after project initialization
- **Phase 2 (Foundational)**: T005-T007 can run in parallel (different lib files)
- **Phase 2.5 (Unit Tests)**: T007a-T007c can run in parallel (different test files)
- **Phase 3 (User Story 1)**: T008 and T011 can run in parallel (implementation and documentation)
- **Phase 4 (User Story 2)**: T012, T015, T016, T017 can run in parallel (different files)
- **Phase 5 (User Story 3)**: T018-T020 can all run in parallel (verification and documentation)
- **Phase 6 (Polish)**: T021-T030 can run in parallel (independent validation tasks)

---

## Implementation Strategy

### MVP First (User Stories 1 & 2 Only)

1. Complete Phase 1: Setup (T001-T004)
2. Complete Phase 2: Foundational (T005-T007) - CRITICAL
3. Complete Phase 3: User Story 1 (T008-T011)
4. Complete Phase 4: User Story 2 (T012-T017)
5. **STOP and VALIDATE**: Test basic request/response logging with request ID correlation
6. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready (T001-T007)
2. Add User Story 1 → Test independently → Deploy/Demo (T008-T011) - Basic logging MVP
3. Add User Story 2 → Test independently → Deploy/Demo (T012-T017) - Request correlation complete
4. Add User Story 3 → Test independently → Deploy/Demo (T018-T020) - Operational documentation
5. Polish → Final validation (T021-T030)

### Task Completion Order

**Sequential (MUST follow order)**:
1. T001-T004 (Setup) → T005-T007 (Foundational)
2. T005-T007 (Foundational) → T008 (Logger middleware)
3. T008-T011 (US1) → T012 (Request ID middleware)
4. T012-T017 (US2) → T018 (US3 verification)
5. T008-T020 (All stories) → T021-T030 (Polish)

**Parallel within phases** (can run simultaneously):
- T001, T002, T003, T004 (Setup dependencies)
- T005, T006, T007 (Foundational utilities)
- T008 + T011 (US1 implementation + documentation)
- T012, T015, T016, T017 (US2 different files)
- T018, T019, T020 (US3 verification tasks)
- T021-T030 (Polish tasks)

---

## Summary

**Total Tasks**: 33  
**Setup**: 4 tasks  
**Foundational**: 3 tasks  
**Unit Tests (TDD)**: 3 tasks  
**User Story 1 (P1)**: 4 tasks  
**User Story 2 (P1)**: 6 tasks  
**User Story 3 (P2)**: 3 tasks  
**Polish**: 10 tasks

**Parallel Opportunities**: 23 tasks can run in parallel (marked with [P])

**MVP Scope**: Phase 1 (Setup) + Phase 2 (Foundational) + Phase 3 (User Story 1) + Phase 4 (User Story 2) = 17 tasks
- This delivers the core value: complete request/response logging with unique request ID correlation

**Independent Test Criteria**:
- **US1**: Make HTTP request → verify request and response logs appear with all details
- **US2**: Make HTTP request → verify request ID is generated, appears in all logs, and is returned in response header
- **US3**: Make multiple requests → search logs by one request ID → verify only related logs appear

**Format Validation**: ✅ All tasks follow checklist format with checkbox, task ID, optional [P] marker, optional [Story] label, and file paths

---

## Notes

- Unit tests REQUIRED for all utility functions per Constitution Principle XIII (80% coverage target)
- TDD workflow MANDATORY: Write tests first (Red-Green-Refactor cycle)
- Manual validation tasks serve as integration testing for middleware
- All public APIs MUST have JSDoc documentation (Principle XI: Public API Documentation)
- Code MUST follow Clean Code principles: small functions, descriptive naming, max 3 nesting levels, DRY (Principle XIII)
- All new code will be validated by ESLint with TypeScript plugin
- Request ID format is fixed: exactly 10 alphanumeric characters (A-Z, a-z, 0-9)
- Body truncation limit is fixed: 10KB (10,240 bytes)
- AsyncLocalStorage is native Node.js (no external dependency)
- Logging overhead target: ≤5% increase in request processing time
- ISO8601 timestamp format: `YYYY-MM-DDTHH:mm:ss.sssZ` (UTC)

---

**Implementation Ready**: ✅ Tasks are immediately executable with specific file paths and clear acceptance criteria  
**Last Updated**: 2025-11-17

