# Tasks: Create Announcement Endpoint

**Feature**: POST `/api/v1/announcements` endpoint  
**Branch**: `009-create-announcement`  
**Generated**: 2025-11-24

## Overview

This document provides an actionable, dependency-ordered task list for implementing the create announcement endpoint feature. Tasks are organized by user story to enable independent implementation and testing.

**Total Tasks**: 30  
**Test Tasks**: 12 (TDD workflow)  
**Implementation Tasks**: 18

**Parallel Opportunities**: Tasks marked with [P] can be executed in parallel within their phase.

---

## Implementation Strategy

### MVP Scope (Minimal Viable Product)

**Phase 1-3 constitute the MVP**:
- Setup (Phase 1)
- Foundational utilities (Phase 2)
- User Story 1 - Submit New Announcement (Phase 3)

This provides core functionality: users can create announcements successfully with basic validation.

### Incremental Delivery

- **Phase 1-3**: MVP (submit announcements with basic validation)
- **Phase 4**: Add comprehensive validation error handling
- **Phase 5**: Add duplicate prevention for microchip numbers
- **Phase 6**: Polish and cross-cutting concerns

Each phase delivers a complete, independently testable increment.

---

## Phase 1: Setup & Dependencies

**Goal**: Initialize project dependencies and database schema

**Prerequisites**: None (starting point)

**Completion Criteria**: 
- Dependencies installed and verified
- Database migration created and applied
- Database schema validated with correct columns and constraints

### Tasks

- [X] T001 Install production dependencies in server/package.json (xss@^1.0.14, zod@^3.22.4)
- [X] T002 Verify dependencies installed correctly with `npm list xss zod`
- [X] T003 Create database migration file: server/src/database/migrations/YYYYMMDDHHMMSS_create-announcement-table.ts
- [X] T004 Write migration up() function with announcement table schema (all fields, UNIQUE constraint on microchip_number)
- [X] T005 Write migration down() function to drop announcement table
- [X] T006 Run migration with `npm run knex:migrate` and verify table created
- [X] T007 Verify database schema with `sqlite3 pets.db ".schema announcement"`

---

## Phase 2: Foundational Utilities (TDD)

**Goal**: Implement reusable utility functions needed by all user stories

**Prerequisites**: Phase 1 complete

**Completion Criteria**:
- All utility functions implemented with unit tests
- Test coverage ≥ 80% for utility modules
- All tests passing

**User Stories Supported**: US1, US2, US3 (foundational for all)

### Tasks

#### Text Sanitization Utility

- [X] T008 [P] Write failing tests for text sanitization in server/src/lib/__test__/text-sanitization.test.ts
- [X] T009 [P] Implement sanitizeText() function in server/src/lib/text-sanitization.ts using xss library
- [X] T010 [P] Verify text sanitization tests pass with `npm test -- text-sanitization.test.ts`

#### Password Management Utility

- [X] T011 [P] Write failing tests for password management in server/src/lib/__test__/password-management.test.ts
- [X] T012 [P] Implement generateManagementPassword() in server/src/lib/password-management.ts (6-digit numeric)
- [X] T013 [P] Implement hashPassword() using Node.js scrypt in server/src/lib/password-management.ts (salt:hash format)
- [X] T014 [P] Implement verifyPassword() with timingSafeEqual in server/src/lib/password-management.ts
- [X] T015 [P] Verify password management tests pass with `npm test -- password-management.test.ts`

#### PII Redaction Utility

- [X] T016 [P] Write failing tests for PII redaction in server/src/lib/__test__/pii-redaction.test.ts
- [X] T017 [P] Implement redactPhone() function in server/src/lib/pii-redaction.ts (show last 3 digits)
- [X] T018 [P] Implement redactEmail() function in server/src/lib/pii-redaction.ts (show first letter + @domain)
- [X] T019 [P] Verify PII redaction tests pass with `npm test -- pii-redaction.test.ts`

#### Error Classes

- [X] T020 [P] Create ValidationError class in server/src/lib/errors.ts (code, message, field properties)
- [X] T021 [P] Create ConflictError class in server/src/lib/errors.ts (message, field properties)

---

## Phase 3: User Story 1 - Submit New Announcement (P1)

**User Story**: A user wants to post a new pet announcement (lost/found pet) by submitting announcement details including contact information to the system.

**Priority**: P1 (Core functionality - most critical user action)

**Goal**: Implement core announcement creation with basic validation

**Prerequisites**: Phase 2 complete (foundational utilities available)

**Completion Criteria**:
- POST `/api/v1/announcements` endpoint accepts valid requests
- HTTP 201 returned with created announcement (including managementPassword)
- Database persists announcements correctly
- Integration tests pass for happy path scenarios

**Independent Test**: Submit POST request with valid announcement data and verify HTTP 201 with created announcement

### Tasks

#### Type Definitions

- [X] T022 [US1] Create CreateAnnouncementDto interface in server/src/types/announcement.ts (all request fields)
- [X] T023 [US1] Create AnnouncementDto interface in server/src/types/announcement.ts (extends CreateAnnouncementDto with id, createdAt, managementPassword)

#### Zod Validation Schema

- [X] T024 [US1] Create Zod schema in server/src/services/announcement-service.ts (required fields, basic format validation, .strict() mode)
- [X] T025 [US1] Add contact method refinement to Zod schema (email OR phone required)

#### Service Layer (TDD)

- [X] T026 [US1] Write failing unit tests for createAnnouncement() success case in server/src/services/__test__/announcement-service.test.ts
- [X] T027 [US1] Implement createAnnouncement() function in server/src/services/announcement-service.ts (validation, sanitization, password generation, database insert)
- [X] T028 [US1] Verify service unit tests pass with `npm test -- announcement-service.test.ts`

#### Route Layer

- [X] T029 [US1] Create POST /announcements route handler in server/src/routes/announcements.ts (call service, return 201)
- [X] T030 [US1] Create error handler middleware in server/src/middlewares/error-handler.ts (handle ValidationError, ConflictError, payload size, generic errors)
- [X] T031 [US1] Register announcement routes in server/src/app.ts (set 10MB payload limit, register error handler)

#### Integration Tests (TDD)

- [X] T032 [US1] Write integration test for successful announcement creation in server/src/__test__/announcements.test.ts (email contact)
- [X] T033 [US1] Write integration test for successful announcement creation in server/src/__test__/announcements.test.ts (phone contact)
- [X] T034 [US1] Write integration test for successful announcement creation in server/src/__test__/announcements.test.ts (both email and phone)
- [X] T035 [US1] Write integration test for microchip number in server/src/__test__/announcements.test.ts (unique microchip)
- [X] T036 [US1] Write integration test for status field in server/src/__test__/announcements.test.ts (MISSING status)
- [X] T037 [US1] Write integration test for status field in server/src/__test__/announcements.test.ts (FOUND status)
- [X] T038 [US1] Write integration test for managementPassword in server/src/__test__/announcements.test.ts (6-digit returned in POST)
- [X] T039 [US1] Verify all User Story 1 integration tests pass with `npm test -- announcements.test.ts`

---

## Phase 4: User Story 2 - Receive Clear Validation Errors (P1)

**User Story**: A user submits an announcement with invalid or missing required fields and receives clear, actionable error messages explaining what needs to be corrected.

**Priority**: P1 (Essential for user experience)

**Goal**: Implement comprehensive validation with fail-fast error handling

**Prerequisites**: Phase 3 complete (basic endpoint functional)

**Completion Criteria**:
- All validation rules implemented (required fields, formats, ranges, XSS prevention, unknown fields)
- HTTP 400 returned with detailed error messages in correct format
- Fail-fast validation (returns first error only)
- Integration tests pass for all validation scenarios

**Independent Test**: Submit invalid data (missing fields, wrong formats) and verify HTTP 400 with detailed error messages

### Tasks

#### Enhanced Zod Schema Validation

- [X] T040 [P] [US2] Add all format validations to Zod schema in server/src/services/announcement-service.ts (email, phone, URL, date, microchip, age, status, coordinates)
- [X] T041 [P] [US2] Add range validations to Zod schema in server/src/services/announcement-service.ts (latitude -90 to 90, longitude -180 to 180, age > 0)
- [X] T042 [P] [US2] Add custom date refinement to Zod schema in server/src/services/announcement-service.ts (reject future dates)
- [X] T043 [P] [US2] Add whitespace trimming to all string fields in Zod schema

#### Validation Error Mapping

- [X] T044 [US2] Implement Zod error code mapping in server/src/services/announcement-service.ts (map Zod errors to API error codes: MISSING_VALUE, INVALID_FORMAT, INVALID_FIELD, MISSING_CONTACT)
- [X] T045 [US2] Update error handler middleware in server/src/middlewares/error-handler.ts (handle HTTP 413 payload too large)

#### Integration Tests for Validation (TDD)

- [X] T046 [US2] Write integration test for missing contact in server/src/__test__/announcements.test.ts (no email or phone)
- [X] T047 [US2] Write integration test for invalid email format in server/src/__test__/announcements.test.ts
- [X] T048 [US2] Write integration test for invalid phone format in server/src/__test__/announcements.test.ts
- [X] T049 [US2] Write integration test for whitespace-only fields in server/src/__test__/announcements.test.ts
- [X] T050 [US2] Write integration test for missing required field in server/src/__test__/announcements.test.ts (species)
- [X] T051 [US2] Write integration test for optional fields in server/src/__test__/announcements.test.ts (breed, description omitted)
- [X] T052 [US2] Write integration test for invalid microchip format in server/src/__test__/announcements.test.ts (non-numeric)
- [X] T053 [US2] Write integration test for unknown fields in server/src/__test__/announcements.test.ts (INVALID_FIELD error)
- [X] T054 [US2] Write integration test for XSS prevention in server/src/__test__/announcements.test.ts (HTML tags stripped)
- [X] T055 [US2] Write integration test for fail-fast validation in server/src/__test__/announcements.test.ts (multiple errors, returns first only)
- [X] T056 [US2] Write integration test for invalid status in server/src/__test__/announcements.test.ts (not MISSING or FOUND)
- [X] T057 [US2] Write integration test for invalid age in server/src/__test__/announcements.test.ts (negative or zero)
- [X] T058 [US2] Write integration test for invalid coordinates in server/src/__test__/announcements.test.ts (non-numeric)
- [X] T059 [US2] Write integration test for out-of-range coordinates in server/src/__test__/announcements.test.ts (latitude > 90)
- [X] T060 [US2] Write integration test for invalid photoUrl in server/src/__test__/announcements.test.ts (not http/https)
- [X] T061 [US2] Write integration test for future lastSeenDate in server/src/__test__/announcements.test.ts
- [X] T062 [US2] Write integration test for payload too large in server/src/__test__/announcements.test.ts (>10MB, HTTP 413)
- [X] T063 [US2] Verify all User Story 2 integration tests pass with `npm test -- announcements.test.ts`

---

## Phase 5: User Story 3 - Prevent Duplicate Microchip Announcements (P2)

**User Story**: A user attempts to create an announcement for a pet with a microchip number that already exists in the system, and the system prevents duplicate announcements and informs the user.

**Priority**: P2 (Important for data integrity)

**Goal**: Implement duplicate microchip prevention

**Prerequisites**: Phase 4 complete (validation fully functional)

**Completion Criteria**:
- Duplicate microchip numbers rejected with HTTP 409
- Unique microchip numbers accepted successfully
- Announcements without microchip numbers processed normally (skip duplicate check)
- Integration tests pass for duplicate scenarios

**Independent Test**: Create announcement with microchip, then attempt duplicate and verify HTTP 409

### Tasks

#### Database Query for Duplicates

- [X] T064 [US3] Implement duplicate microchip check in server/src/services/announcement-service.ts (query database, throw ConflictError if exists)
- [X] T065 [US3] Update error handler middleware to handle ConflictError in server/src/middlewares/error-handler.ts (HTTP 409)

#### Integration Tests for Duplicates (TDD)

- [X] T066 [US3] Write integration test for duplicate microchip in server/src/__test__/announcements.test.ts (HTTP 409 with CONFLICT error)
- [X] T067 [US3] Write integration test for unique microchip in server/src/__test__/announcements.test.ts (successful creation)
- [X] T068 [US3] Write integration test for missing microchip in server/src/__test__/announcements.test.ts (skip duplicate check)
- [X] T069 [US3] Verify all User Story 3 integration tests pass with `npm test -- announcements.test.ts`

---

## Phase 6: Polish & Cross-Cutting Concerns

**Goal**: Finalize implementation with documentation, logging, and code quality

**Prerequisites**: Phases 3-5 complete (all user stories implemented)

**Completion Criteria**:
- Test coverage ≥ 80% verified
- ESLint passes with no errors
- All acceptance scenarios from spec.md covered by tests
- Manual testing completed successfully

### Tasks

#### Code Quality & Testing

- [ ] T070 Run full test suite with coverage: `npm test -- --coverage` (verify ≥ 80% coverage)
- [ ] T071 Run ESLint and fix any issues: `npm run lint` from server/
- [ ] T072 Review test coverage report at server/coverage/index.html (identify gaps)
- [ ] T073 Add missing tests for any uncovered edge cases

#### Manual Testing

- [ ] T074 Start dev server with `npm run dev` from server/
- [ ] T075 Test successful announcement creation with cURL (email contact)
- [ ] T076 Test successful announcement creation with cURL (phone contact)
- [ ] T077 Test validation error with cURL (missing required field)
- [ ] T078 Test duplicate microchip with cURL (HTTP 409)
- [ ] T079 Test XSS prevention with cURL (HTML tags in description)

#### Documentation

- [ ] T080 Update server/README.md with POST /api/v1/announcements endpoint documentation
- [ ] T081 Add JSDoc comments to all public service functions in server/src/services/announcement-service.ts
- [ ] T082 Add JSDoc comments to all utility functions in server/src/lib/ (if missing)
- [ ] T083 Verify OpenAPI contract matches implementation in specs/009-create-announcement/contracts/openapi.yaml

---

## Dependencies & Execution Order

### Story Dependencies

```
Phase 1 (Setup)
    ↓
Phase 2 (Foundational Utilities) - BLOCKING
    ↓
    ├─→ Phase 3 (User Story 1) - INDEPENDENT ✓
    │       ↓
    ├─→ Phase 4 (User Story 2) - Depends on Phase 3 ✓
    │       ↓
    └─→ Phase 5 (User Story 3) - Depends on Phase 4 ✓
            ↓
        Phase 6 (Polish) - Depends on Phases 3-5
```

### Phase 2 (Foundational) Dependencies

All tasks in Phase 2 are parallelizable [P]:
- T008-T010: Text sanitization (independent)
- T011-T015: Password management (independent)
- T016-T019: PII redaction (independent)
- T020-T021: Error classes (independent)

### Phase 3 (User Story 1) Dependencies

- T022-T023: Type definitions (parallel, foundational)
- T024-T025: Zod schema (depends on types)
- T026-T028: Service layer (depends on schema, types)
- T029-T031: Route layer (depends on service)
- T032-T039: Integration tests (depends on route layer)

### Phase 4 (User Story 2) Dependencies

- T040-T043: Enhanced Zod schema (parallel [P])
- T044-T045: Error mapping (depends on enhanced schema)
- T046-T063: Integration tests (depends on error mapping)

### Phase 5 (User Story 3) Dependencies

- T064-T065: Duplicate check (sequential)
- T066-T069: Integration tests (depends on duplicate check)

### Phase 6 (Polish) Dependencies

All tasks can run after Phases 3-5 are complete.

---

## Parallel Execution Examples

### Phase 2: All utilities in parallel
```bash
# Terminal 1
npm test -- text-sanitization.test.ts

# Terminal 2
npm test -- password-management.test.ts

# Terminal 3
npm test -- pii-redaction.test.ts
```

### Phase 4: Enhanced validation in parallel
```bash
# All Zod schema enhancements (T040-T043) can be done simultaneously
# in different branches or by different developers
```

---

## Testing Strategy

### Test Distribution

- **Unit Tests**: 15 tests (utilities, service layer)
  - Text sanitization: 3 tests
  - Password management: 5 tests
  - PII redaction: 2 tests
  - Announcement service: 5 tests

- **Integration Tests**: 30 tests (API endpoint)
  - User Story 1: 8 tests (happy path)
  - User Story 2: 19 tests (validation errors)
  - User Story 3: 3 tests (duplicate prevention)

### Test Coverage Target

- **Overall**: ≥ 80% line + branch coverage
- **Services**: ≥ 80% coverage for server/src/services/
- **Utilities**: ≥ 80% coverage for server/src/lib/
- **Integration**: All acceptance scenarios from spec.md covered

### Running Tests

```bash
# Unit tests only
npm test -- text-sanitization.test.ts
npm test -- password-management.test.ts
npm test -- pii-redaction.test.ts
npm test -- announcement-service.test.ts

# Integration tests
npm test -- announcements.test.ts

# All tests with coverage
npm test -- --coverage

# View coverage report
open server/coverage/index.html
```

---

## Implementation Notes

### TDD Workflow

All implementation tasks follow Test-Driven Development (Red-Green-Refactor):
1. **RED**: Write failing test first
2. **GREEN**: Write minimal code to pass test
3. **REFACTOR**: Improve code quality without changing behavior

### Fail-Fast Validation

- Zod automatically stops at first validation error
- Error handler returns only first error to client
- Simplifies debugging for end users

### Security Considerations

- XSS prevention: All text fields sanitized with `xss` library
- Password security: Management passwords hashed with Node.js `scrypt`
- PII protection: Contact information redacted in logs
- Unknown field rejection: Zod `.strict()` mode prevents injection attacks
- Payload size limiting: Express middleware enforces 10 MB limit

### Performance Notes

- No database indexes (intentionally skipped for MVP simplicity)
- Queries will perform full table scans (acceptable for low-traffic MVP)
- Fail-fast validation minimizes processing time
- Text sanitization applied before storage (not on every read)

---

## Success Criteria

### Phase Completion Checklist

- [X] **Phase 1**: Dependencies installed, database schema created
- [X] **Phase 2**: All utility functions implemented with ≥80% test coverage
- [X] **Phase 3**: POST endpoint functional, User Story 1 acceptance scenarios pass
- [X] **Phase 4**: Comprehensive validation implemented, User Story 2 acceptance scenarios pass
- [X] **Phase 5**: Duplicate prevention functional, User Story 3 acceptance scenarios pass
- [ ] **Phase 6**: Code quality verified, manual testing completed, documentation updated

### Overall Success Criteria (from spec.md)

- [ ] **SC-001**: Users can successfully submit a valid announcement and receive confirmation in under 2 seconds
- [ ] **SC-002**: System correctly rejects 100% of announcements missing both email and phone contact information
- [ ] **SC-003**: Users receive clear, actionable error messages for validation failures with specific field identification
- [ ] **SC-004**: System maintains data integrity with no duplicate announcements created for the same microchip number
- [ ] **SC-005**: System correctly rejects 100% of duplicate microchip submissions with HTTP 409 response
- [ ] **SC-006**: System prevents XSS attacks by sanitizing all text input before storage
- [ ] **SC-007**: System rejects 100% of requests containing unknown fields (strict validation)
- [ ] **SC-008**: Management password is never exposed in GET endpoints (100% secure - only returned once in POST response)
- [ ] **SC-009**: Each announcement receives a 6-digit management password for future management operations
- [ ] **SC-010**: Error responses follow the simplified format consistently
- [ ] **SC-011**: All logged data protects user privacy by redacting PII

---

## Task Summary

**Total Tasks**: 83
- Setup: 7 tasks
- Foundational: 14 tasks
- User Story 1: 18 tasks
- User Story 2: 24 tasks
- User Story 3: 6 tasks
- Polish: 14 tasks

**Parallel Tasks**: 19 tasks marked with [P]

**Estimated Timeline**:
- Phase 1: 1 hour
- Phase 2: 3-4 hours (TDD for utilities)
- Phase 3: 4-5 hours (MVP core functionality)
- Phase 4: 5-6 hours (comprehensive validation)
- Phase 5: 2-3 hours (duplicate prevention)
- Phase 6: 2-3 hours (polish and testing)

**Total Estimated Time**: 17-22 hours for full implementation with TDD approach

**MVP Only (Phases 1-3)**: 8-10 hours for basic functional endpoint

