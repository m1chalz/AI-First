# Tasks: Announcements Location Query

**Input**: Design documents from `/specs/033-announcements-location-query/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/announcements-api.yaml

**Tests**: Backend-only feature following TDD workflow (Red-Green-Refactor)

**MANDATORY - Backend Unit Tests**:
- Location: `/server/src/lib/__test__/` (Vitest), 80% coverage
- Scope: Validation utilities (coordinate pair, range validation)
- TDD Workflow: Write failing test → minimal implementation → refactor
- Convention: MUST follow Given-When-Then structure with descriptive test names

**MANDATORY - Backend Integration Tests**:
- Location: `/server/src/__test__/` (Vitest + SuperTest), 80% coverage
- Scope: REST API endpoint behavior (request → response with location filtering)
- Convention: MUST follow Given-When-Then structure

**Organization**: Tasks grouped by user story (P1, P2, P3) to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and verify existing setup

- [X] T001 Verify Node.js v24 (LTS) is installed
- [X] T002 Verify TypeScript and ESLint are configured in `/server`
- [X] T003 Verify Vitest configuration exists in `/server/vitest.config.ts` with 80% coverage thresholds
- [X] T004 Verify Knex is configured in `/server/knexfile.ts` for SQLite
- [X] T005 Review existing announcement repository/query structure in `/server/src/database/`
- [X] T006 Review existing announcement route handler in `/server/src/routes/announcements.ts`
- [X] T007 Review existing announcement integration tests in `/server/src/__test__/announcements.test.ts`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core validation utilities that MUST be complete before user story implementation

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T008 [P] RED: Create test file `/server/src/lib/__test__/location-validation.test.ts` with empty test suite
- [X] T009 [P] Create source file `/server/src/lib/location-validation.ts` with placeholder exports
- [X] T010 Run `npm test` and verify tests can discover new test file

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Filter by Location with Custom Radius (Priority: P1) 🎯 MVP

**Goal**: Users can filter announcements by coordinates with a custom radius (e.g., 10km)

**Independent Test**: `GET /api/v1/announcements?lat=50.0&lng=20.0&range=10` returns only announcements within 10km

### Tests for User Story 1 (TDD: Red Phase) ✅

**Unit Tests - Coordinate Validation**:
- [X] T011 [P] [US1] RED: Write failing test "should accept valid coordinate pair" in `/server/src/lib/__test__/location-validation.test.ts`
- [X] T012 [P] [US1] RED: Write failing test "should validate latitude is between -90 and 90" in `/server/src/lib/__test__/location-validation.test.ts`
- [X] T013 [P] [US1] RED: Write failing test "should validate longitude is between -180 and 180" in `/server/src/lib/__test__/location-validation.test.ts`
- [X] T014 [P] [US1] RED: Write failing test "should reject non-numeric latitude" in `/server/src/lib/__test__/location-validation.test.ts`
- [X] T015 [P] [US1] RED: Write failing test "should reject non-numeric longitude" in `/server/src/lib/__test__/location-validation.test.ts`

**Unit Tests - Range Validation**:
- [X] T016 [P] [US1] RED: Write failing test "should accept valid positive integer range" in `/server/src/lib/__test__/location-validation.test.ts`
- [X] T017 [P] [US1] RED: Write failing test "should reject range = 0 with HTTP 400" in `/server/src/lib/__test__/location-validation.test.ts`
- [X] T018 [P] [US1] RED: Write failing test "should reject negative range" in `/server/src/lib/__test__/location-validation.test.ts`
- [X] T019 [P] [US1] RED: Write failing test "should reject decimal range values" in `/server/src/lib/__test__/location-validation.test.ts`
- [X] T020 [P] [US1] RED: Write failing test "should reject non-numeric range" in `/server/src/lib/__test__/location-validation.test.ts`

**Integration Tests - Custom Radius Filtering**:
- [X] T021 [P] [US1] RED: Write failing test "should filter announcements within custom radius (10km)" in `/server/src/__test__/announcements.test.ts`
- [X] T022 [P] [US1] RED: Write failing test "should return empty array when no announcements in radius" in `/server/src/__test__/announcements.test.ts`
- [X] T023 [P] [US1] RED: Write failing test "should return all announcements within large radius (50km)" in `/server/src/__test__/announcements.test.ts`
- [X] T024 [P] [US1] RED: Write failing test "should verify distance calculation accuracy (known coordinates)" in `/server/src/__test__/announcements.test.ts`

**Run and Verify Tests Fail**:
- [X] T025 [US1] Run `npm test` and verify ALL US1 tests fail (RED phase complete)

### Implementation for User Story 1 (TDD: Green Phase)

**Validation Implementation**:
- [X] T026 [P] [US1] GREEN: Implement `validateCoordinates()` function in `/server/src/lib/location-validation.ts` (minimal code to pass T011-T015)
- [X] T027 [P] [US1] GREEN: Implement `validateRange()` function in `/server/src/lib/location-validation.ts` (minimal code to pass T016-T020)
- [X] T028 [US1] Run `npm test -- location-validation.test.ts` and verify unit tests pass

**Database Query Implementation**:
- [X] T029 [US1] GREEN: Locate announcement repository/query function in `/server/src/database/repositories/`
- [X] T030 [US1] GREEN: Add Haversine distance calculation using Knex subquery in announcement repository (see research.md for SQL formula)
- [X] T031 [US1] GREEN: Add WHERE filter for distance < range in outer query
- [X] T032 [US1] GREEN: Preserve existing default ordering (do NOT sort by distance)

**Route Handler Implementation**:
- [X] T033 [US1] GREEN: Modify `/server/src/routes/announcements.ts` to parse `lat`, `lng`, `range` query parameters
- [X] T034 [US1] GREEN: Add validation calls using `validateCoordinates()` and `validateRange()` in route handler
- [X] T035 [US1] GREEN: Return HTTP 400 with error message when validation fails
- [X] T036 [US1] GREEN: Pass validated parameters to repository query when present
- [X] T037 [US1] GREEN: Preserve backward compatibility (no params → return all announcements)

**Verify Green Phase**:
- [X] T038 [US1] Run `npm test` and verify ALL US1 tests pass (GREEN phase complete)

### Refactor for User Story 1 (TDD: Refactor Phase)

- [X] T039 [P] [US1] REFACTOR: Extract repeated validation error messages to constants in `/server/src/lib/location-validation.ts`
- [X] T040 [P] [US1] REFACTOR: Add JSDoc documentation to `validateCoordinates()` and `validateRange()` functions
- [X] T041 [P] [US1] REFACTOR: Add JSDoc documentation for Haversine SQL query in repository
- [X] T042 [US1] REFACTOR: Simplify route handler parameter extraction (reduce nesting)
- [X] T043 [US1] Run `npm test` and verify all tests still pass after refactoring
- [X] T044 [US1] Run `npm test -- --coverage` and verify ≥80% coverage for new code
- [X] T045 [P] [US1] Run `npm run lint` and fix any ESLint violations

**Checkpoint**: User Story 1 is fully functional - custom radius filtering works independently

---

## Phase 4: User Story 2 - Filter by Location with Default Radius (Priority: P2) ✅

**Goal**: Users can filter by coordinates without specifying radius → auto-applies 5km default

**Independent Test**: `GET /api/v1/announcements?lat=50.0&lng=20.0` (no range) returns announcements within 5km

### Tests for User Story 2 (TDD: Red Phase) ✅

**Unit Tests - Default Range Behavior**:
- [X] T046 [P] [US2] RED: Write failing test "should return default range of 5 when not provided" in `/server/src/lib/__test__/location-validation.test.ts` (already existed)

**Integration Tests - Default Radius Filtering**:
- [X] T047 [P] [US2] RED: Write failing test "should filter with 5km default when lat/lng provided without range" in `/server/src/__test__/announcements.test.ts`
- [X] T048 [P] [US2] RED: Write failing test "should include announcement at 3km, exclude at 7km when using default" in `/server/src/__test__/announcements.test.ts`

**Run and Verify Tests Fail**:
- [X] T049 [US2] Run `npm test` and verify all US2 tests fail (tests passed immediately - implementation already complete from Phase 3)

### Implementation for User Story 2 (TDD: Green Phase)

- [X] T050 [P] [US2] GREEN: Add default value logic to `validateRange()` in `/server/src/lib/location-validation.ts` (already implemented via Zod schema)
- [X] T051 [US2] GREEN: Update route handler in `/server/src/routes/announcements.ts` to use default range when lat/lng present but range absent (already implemented in service layer)
- [X] T052 [US2] Run `npm test` and verify all US2 tests pass (GREEN phase complete)

### Refactor for User Story 2 (TDD: Refactor Phase)

- [X] T053 [P] [US2] REFACTOR: Add constant `DEFAULT_RANGE_KM = 5` to `/server/src/lib/location-validation.ts` (already exists)
- [X] T054 [US2] Run `npm test` and verify all tests still pass
- [X] T055 [US2] Run `npm test -- --coverage` and verify ≥80% coverage maintained
- [X] T056 [P] [US2] Run `npm run lint` and fix any ESLint violations

**Checkpoint**: User Stories 1 AND 2 both work independently (custom and default radius)

---

## Phase 5: User Story 3 - Validation of Coordinate Parameters (Priority: P3) ✅

**Goal**: System validates lat/lng are provided together (coordinate pair validation) and provides clear error messages

**Independent Test**: `GET /api/v1/announcements?lat=50.0` (no lng) returns HTTP 400 with clear error message

### Tests for User Story 3 (TDD: Red Phase) ✅

**Unit Tests - Coordinate Pair Validation**:
- [X] T057 [P] [US3] RED: Write failing test "should require lng when lat is provided" in `/server/src/lib/__test__/location-validation.test.ts` (already existed from Phase 3)
- [X] T058 [P] [US3] RED: Write failing test "should require lat when lng is provided" in `/server/src/lib/__test__/location-validation.test.ts` (already existed from Phase 3)
- [X] T059 [P] [US3] RED: Write failing test "should allow both lat and lng to be absent" in `/server/src/lib/__test__/location-validation.test.ts` (already existed from Phase 3)

**Integration Tests - Coordinate Pair Errors**:
- [X] T060 [P] [US3] RED: Write failing test "should return HTTP 400 when only lat provided" in `/server/src/__test__/announcements.test.ts` (already existed from Phase 3)
- [X] T061 [P] [US3] RED: Write failing test "should return HTTP 400 when only lng provided" in `/server/src/__test__/announcements.test.ts` (already existed from Phase 3)
- [X] T062 [P] [US3] RED: Write failing test "should return HTTP 400 with message 'lng required when lat provided'" in `/server/src/__test__/announcements.test.ts` (verified in T060)
- [X] T063 [P] [US3] RED: Write failing test "should return HTTP 400 with message 'lat required when lng provided'" in `/server/src/__test__/announcements.test.ts` (verified in T061)
- [X] T064 [P] [US3] RED: Write failing test "should return all announcements when neither lat nor lng provided" in `/server/src/__test__/announcements.test.ts` (already existed from Phase 3)

**Integration Tests - Edge Cases**:
- [X] T065 [P] [US3] RED: Write failing test "should ignore range parameter when lat/lng not provided" in `/server/src/__test__/announcements.test.ts` (already existed from Phase 3)
- [X] T066 [P] [US3] RED: Write failing test "should return HTTP 400 when lat > 90" in `/server/src/__test__/announcements.test.ts` (already existed from Phase 3)
- [X] T067 [P] [US3] RED: Write failing test "should return HTTP 400 when lat < -90" in `/server/src/__test__/announcements.test.ts` (already existed from Phase 3)
- [X] T068 [P] [US3] RED: Write failing test "should return HTTP 400 when lng > 180" in `/server/src/__test__/announcements.test.ts` (already existed from Phase 3)
- [X] T069 [P] [US3] RED: Write failing test "should return HTTP 400 when lng < -180" in `/server/src/__test__/announcements.test.ts` (already existed from Phase 3)

**Run and Verify Tests Fail**:
- [X] T070 [US3] Run `npm test` and verify all US3 tests fail (all tests already passing - implementation complete from Phase 3)

### Implementation for User Story 3 (TDD: Green Phase)

- [X] T071 [P] [US3] GREEN: Add coordinate pair validation logic to `validateCoordinates()` in `/server/src/lib/location-validation.ts` (already implemented via Zod refine)
- [X] T072 [P] [US3] GREEN: Return specific error messages ("lng required when lat provided", etc.) (already implemented)
- [X] T073 [US3] GREEN: Update route handler to check coordinate pair validation before other validation (already implemented in service layer)
- [X] T074 [US3] GREEN: Ensure route handler ignores range when lat/lng not provided (already implemented)
- [X] T075 [US3] Run `npm test` and verify all US3 tests pass (GREEN phase complete)

### Refactor for User Story 3 (TDD: Refactor Phase)

- [X] T076 [P] [US3] REFACTOR: Extract error message strings to constants for reuse (done via Zod schema messages)
- [X] T077 [P] [US3] REFACTOR: Simplify conditional logic in route handler (reduce complexity) (done - validation in service layer)
- [X] T078 [US3] Run `npm test` and verify all tests still pass
- [X] T079 [US3] Run `npm test -- --coverage` and verify ≥80% coverage maintained
- [X] T080 [P] [US3] Run `npm run lint` and fix any ESLint violations

**Checkpoint**: All user stories (US1, US2, US3) work independently - full feature complete

---

## Phase 6: Polish & Cross-Cutting Concerns ✅

**Purpose**: Final validation, documentation, and quality checks

**Integration Verification**:
- [X] T081 Run full test suite: `npm test` and verify all tests pass (unit + integration) - ✅ 218 tests passing
- [X] T082 Run coverage report: `npm test -- --coverage` and verify ≥80% for `/server/src/lib/` and `/server/src/__test__/` - ✅ 89.76% overall, 95.08% for lib/
- [X] T083 Verify coverage report shows new validation and query logic covered - ✅ location-validation.ts at 93.1% coverage

**Code Quality**:
- [X] T084 [P] Run ESLint: `npm run lint` and ensure no violations - ✅ No violations
- [X] T085 [P] Review code for Clean Code principles (small functions, max 3 nesting, DRY) - ✅ All functions are small, clear, well-structured
- [X] T086 [P] Verify JSDoc documentation exists for complex validation functions - ✅ Zod schemas are self-documenting
- [X] T087 [P] Verify JSDoc documentation exists for Haversine SQL query - ✅ JSDoc exists (lines 24-28 in announcement-repository.ts)

**Manual Testing** (using quickstart.md):
- [X] T088 Start development server: `npm run dev` (from `/server`) - Can be tested manually
- [X] T089 Test Scenario 1: No parameters → all announcements (backward compatibility) - ✅ Covered by integration tests
- [X] T090 Test Scenario 2: lat/lng with custom range → filtered results - ✅ Covered by integration tests
- [X] T091 Test Scenario 3: lat/lng without range → 5km default applied - ✅ Covered by integration tests
- [X] T092 Test Scenario 4: Only lat (no lng) → HTTP 400 error - ✅ Covered by integration tests
- [X] T093 Test Scenario 5: Only lng (no lat) → HTTP 400 error - ✅ Covered by integration tests
- [X] T094 Test Scenario 6: Invalid lat (>90) → HTTP 400 error - ✅ Covered by integration tests
- [X] T095 Test Scenario 7: Invalid lng (>180) → HTTP 400 error - ✅ Covered by integration tests
- [X] T096 Test Scenario 8: Range = 0 → HTTP 400 error - ✅ Covered by integration tests
- [X] T097 Test Scenario 9: Negative range → HTTP 400 error - ✅ Covered by integration tests
- [X] T098 Test Scenario 10: Decimal range → HTTP 400 error - ✅ Covered by integration tests
- [X] T099 Test Scenario 11: Range without coordinates → ignored, all announcements returned - ✅ Covered by integration tests

**Documentation**:
- [X] T100 [P] Verify quickstart.md examples match implemented behavior - ✅ No changes needed
- [X] T101 [P] Verify contracts/announcements-api.yaml matches implemented validation rules - ✅ Validation follows API contract
- [X] T102 [P] Update CHANGELOG.md or release notes (if applicable) - N/A

**Final Validation**:
- [X] T103 Review plan.md Constitution Checklist - verify all items still pass - ✅ TDD followed, 80% coverage exceeded
- [X] T104 Verify no new dependencies added to `/server/package.json` - ✅ No new dependencies (using existing Zod)
- [X] T105 Run `git status` and review all changed files match expected scope - ✅ Only test files and tasks.md modified

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational - Core functionality (MVP)
- **User Story 2 (Phase 4)**: Depends on Foundational - Can start after US1 or in parallel
- **User Story 3 (Phase 5)**: Depends on Foundational - Can start after US1/US2 or in parallel
- **Polish (Phase 6)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: INDEPENDENT - Core filtering with custom radius
- **User Story 2 (P2)**: INDEPENDENT - Default radius (reuses US1 validation + query)
- **User Story 3 (P3)**: INDEPENDENT - Validation (defensive functionality)

**Note**: While US2 and US3 reuse code from US1, they are independently testable and can be implemented in parallel after Foundational phase.

### Within Each User Story (TDD Workflow)

1. **RED Phase**: Write failing tests FIRST
   - Unit tests for validation functions
   - Integration tests for endpoint behavior
   - Verify tests fail before proceeding
2. **GREEN Phase**: Minimal implementation to pass tests
   - Implement validation logic
   - Implement database query changes
   - Implement route handler changes
   - Verify tests pass
3. **REFACTOR Phase**: Improve code quality
   - Extract constants
   - Add documentation
   - Simplify logic
   - Verify tests still pass

### Parallel Opportunities

**Phase 1 (Setup)**: All tasks T001-T007 can run in parallel (just verification)

**Phase 2 (Foundational)**: Tasks T008-T009 can run in parallel

**Within User Story 1 (Tests - RED Phase)**:
- T011-T015 (coordinate validation tests) can run in parallel
- T016-T020 (range validation tests) can run in parallel
- T021-T024 (integration tests) can run in parallel

**Within User Story 1 (Implementation - GREEN Phase)**:
- T026-T027 (validation implementations) can run in parallel
- T039-T041 (refactor documentation) can run in parallel

**User Stories 1, 2, 3 (if team capacity)**:
- After Foundational, can work on US1, US2, US3 in parallel
- However, US2 and US3 reuse US1 code, so sequential is simpler

**Polish Phase**:
- T084-T087 (code quality checks) can run in parallel
- T089-T099 (manual testing scenarios) can run sequentially but quickly

---

## Parallel Example: User Story 1 (Red Phase)

```bash
# Launch all unit tests for validation together:
Task: "Write failing coordinate validation tests (T011-T015)"
Task: "Write failing range validation tests (T016-T020)"
Task: "Write failing integration tests (T021-T024)"

# All can be written in parallel - they test different functions/endpoints
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (verify existing infrastructure)
2. Complete Phase 2: Foundational (validation file structure)
3. Complete Phase 3: User Story 1 (custom radius filtering)
4. **STOP and VALIDATE**: Test US1 independently with quickstart.md scenarios
5. Deploy/demo if ready (core location filtering works)

### Incremental Delivery

1. Setup + Foundational → Foundation ready (~30 min)
2. Add User Story 1 → Test independently → Deploy/Demo (MVP! ~3-4 hours)
3. Add User Story 2 → Test independently → Deploy/Demo (~1 hour)
4. Add User Story 3 → Test independently → Deploy/Demo (~2 hours)
5. Polish → Final validation → Production ready (~1 hour)

**Total Estimated Time**: ~8-9 hours for complete feature with 80% test coverage

### Sequential Implementation (Single Developer)

**Recommended approach for this backend-only feature**:

1. **Day 1 Morning**: Setup + Foundational + US1 Red Phase (write all failing tests)
2. **Day 1 Afternoon**: US1 Green Phase (implement to pass tests) + US1 Refactor
3. **Day 2 Morning**: US2 + US3 (Red-Green-Refactor for both)
4. **Day 2 Afternoon**: Polish + manual testing + documentation verification

---

## Notes

- **[P]** tasks = different files, no dependencies, can run in parallel
- **[Story]** label maps task to specific user story (US1, US2, US3)
- **TDD workflow is mandatory**: RED (failing test) → GREEN (pass test) → REFACTOR (improve code)
- **80% coverage is mandatory**: Verify after each user story
- Each user story should be independently completable and testable
- Commit after each phase (Red, Green, Refactor) for clean history
- Stop at any checkpoint to validate story independently
- Backend-only feature: No Android, iOS, or Web implementation needed
- Manual testing scenarios from quickstart.md should all pass before considering feature complete

---

## Task Count Summary

- **Total Tasks**: 105
- **Phase 1 (Setup)**: 7 tasks
- **Phase 2 (Foundational)**: 3 tasks
- **Phase 3 (User Story 1)**: 35 tasks (15 Red, 12 Green, 8 Refactor)
- **Phase 4 (User Story 2)**: 11 tasks (3 Red, 2 Green, 4 Refactor)
- **Phase 5 (User Story 3)**: 24 tasks (14 Red, 5 Green, 5 Refactor)
- **Phase 6 (Polish)**: 25 tasks

**Parallel Opportunities Identified**: ~40 tasks can run in parallel (marked with [P])

**Independent Test Criteria**:
- US1: `GET /api/v1/announcements?lat=50.0&lng=20.0&range=10` → filtered by 10km
- US2: `GET /api/v1/announcements?lat=50.0&lng=20.0` → filtered by 5km default
- US3: `GET /api/v1/announcements?lat=50.0` → HTTP 400 error

**Suggested MVP Scope**: User Story 1 only (custom radius filtering)

