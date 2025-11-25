# Tasks: Remove Location Fields

**Input**: Design documents from `/specs/014-remove-location-fields/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Test requirements for this project:

**MANDATORY - Backend Unit Tests** (if `/server` affected):
- Services: `/server/src/services/__test__/` (Vitest), 80% coverage
- Utilities: `/server/src/lib/__test__/` (Vitest), 80% coverage
- Framework: Vitest
- Scope: Business logic and utility functions
- Workflow: Update existing tests to reflect field removal
- Convention: MUST follow Given-When-Then structure with descriptive test names

**MANDATORY - Backend Integration Tests** (if `/server` affected):
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

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Verify project structure exists per implementation plan in `/server/`
- [ ] T002 [P] Verify TypeScript configuration in `/server/tsconfig.json`
- [ ] T003 [P] Verify ESLint configuration in `/server/eslint.config.mjs`
- [ ] T004 [P] Verify Vitest configuration in `/server/vite.config.ts`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T005 [P] Verify database connection setup in `/server/src/database/db-utils.ts`
- [ ] T006 [P] Verify Knex migration infrastructure in `/server/knexfile.ts`
- [ ] T007 [P] Verify error handling middleware in `/server/src/middlewares/error-handler-middleware.ts`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - API Consumers Cannot Send Location Fields (Priority: P1) 🎯 MVP

**Goal**: API rejects requests containing location fields (`location`, `locationCity`, `locationRadius`) with `INVALID_FIELD` error code via Zod strict mode validation.

**Independent Test**: Send POST request with location fields and verify API rejects with `INVALID_FIELD` error code.

### Tests for User Story 1 (MANDATORY) ✅

> **NOTE: Update existing tests to verify location fields are rejected**

**Backend Unit Tests**:
- [X] T008 [P] [US1] Update existing test to verify validation rejects `locationCity` in `/server/src/lib/__test__/announcement-validation.test.ts`
- [X] T009 [P] [US1] Update existing test to verify validation rejects `locationRadius` in `/server/src/lib/__test__/announcement-validation.test.ts`
- [X] T010 [P] [US1] Update existing test to verify validation rejects `location` field in `/server/src/lib/__test__/announcement-validation.test.ts`

**Backend Integration Tests**:
- [X] T011 [P] [US1] Update existing test to verify POST `/announcements` rejects `locationCity` in `/server/src/__test__/announcements.test.ts`
- [X] T012 [P] [US1] Update existing test to verify POST `/announcements` rejects `locationRadius` in `/server/src/__test__/announcements.test.ts`
- [X] T013 [P] [US1] Update existing test to verify POST `/announcements` rejects `location` field in `/server/src/__test__/announcements.test.ts`

### Implementation for User Story 1

**Backend**:
- [X] T014 [US1] Remove `locationCity` from Zod schema in `/server/src/lib/announcement-validation.ts`
- [X] T015 [US1] Remove `locationRadius` from Zod schema in `/server/src/lib/announcement-validation.ts`
- [X] T016 [US1] Verify strict mode automatically rejects unknown fields (no code changes needed)
- [X] T017 [US1] Remove location field test cases from `/server/src/lib/__test__/announcement-validation.test.ts`
- [X] T018 [US1] Run `npm test -- --coverage` and verify 80% coverage maintained
- [X] T019 [P] [US1] Run `npm run lint` and fix ESLint violations

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently - API rejects location fields with `INVALID_FIELD` error

---

## Phase 4: User Story 2 - API Responses Do Not Include Location Fields (Priority: P1)

**Goal**: API responses exclude location fields (`location`, `locationCity`, `locationRadius`) from all announcement objects.

**Independent Test**: Retrieve announcements via GET endpoints and verify location fields are absent from all response payloads.

### Tests for User Story 2 (MANDATORY) ✅

**Backend Unit Tests**:
- [X] T020 [P] [US2] Update existing test to verify repository mapping excludes `locationCity` in `/server/src/services/__test__/announcement-service.test.ts`
- [X] T021 [P] [US2] Update existing test to verify repository mapping excludes `locationRadius` in `/server/src/services/__test__/announcement-service.test.ts`
- [X] T022 [P] [US2] Update existing test to verify service excludes `locationCity` from response in `/server/src/services/__test__/announcement-service.test.ts`
- [X] T023 [P] [US2] Update existing test to verify service excludes `locationRadius` from response in `/server/src/services/__test__/announcement-service.test.ts`

**Backend Integration Tests**:
- [X] T024 [P] [US2] Update existing test to verify GET `/announcements/:id` excludes location fields in `/server/src/__test__/announcements.test.ts`
- [X] T025 [P] [US2] Update existing test to verify GET `/announcements` excludes location fields from all items in `/server/src/__test__/announcements.test.ts`

### Implementation for User Story 2

**Backend**:
- [X] T026 [US2] Remove `locationCity` from `CreateAnnouncementDto` in `/server/src/types/announcement.d.ts`
- [X] T027 [US2] Remove `locationRadius` from `CreateAnnouncementDto` in `/server/src/types/announcement.d.ts`
- [X] T028 [US2] Remove `locationCity` from `AnnouncementDto` in `/server/src/types/announcement.d.ts`
- [X] T029 [US2] Remove `locationRadius` from `AnnouncementDto` in `/server/src/types/announcement.d.ts`
- [X] T030 [US2] Remove `locationCity` from `Announcement` interface in `/server/src/types/announcement.d.ts`
- [X] T031 [US2] Remove `locationRadius` from `Announcement` interface in `/server/src/types/announcement.d.ts`
- [X] T032 [US2] Remove `location_city` from `AnnouncementRow` interface in `/server/src/types/announcement.d.ts`
- [X] T033 [US2] Remove `location_radius` from `AnnouncementRow` interface in `/server/src/types/announcement.d.ts`
- [X] T034 [US2] Remove `locationCity` mapping in `mapRowToAnnouncement()` in `/server/src/database/repositories/announcement-repository.ts`
- [X] T035 [US2] Remove `locationRadius` mapping in `mapRowToAnnouncement()` in `/server/src/database/repositories/announcement-repository.ts`
- [X] T036 [US2] Remove `locationCity` sanitization in `createAnnouncement()` in `/server/src/services/announcement-service.ts`
- [X] T037 [US2] Remove location field references from test code in `/server/src/services/__test__/announcement-service.test.ts`
- [X] T038 [US2] Run `npm test -- --coverage` and verify 80% coverage maintained
- [X] T039 [P] [US2] Run `npm run lint` and fix ESLint violations

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently - API rejects location fields in requests and excludes them from responses

---

## Phase 5: User Story 3 - Database Schema Does Not Store Location Fields (Priority: P2)

**Goal**: Database schema updated to remove columns for `location`, `location_city`, and `location_radius`. Migration drops columns directly, permanently removing existing data.

**Independent Test**: Verify database schema changes and ensure new announcements cannot be stored with location fields.

### Tests for User Story 3 (MANDATORY) ✅

**Backend Integration Tests**:
- [X] T040 [P] [US3] Update existing test to verify migration removes `location_city` column in `/server/src/__test__/announcements.test.ts`
- [X] T041 [P] [US3] Update existing test to verify migration removes `location_radius` column in `/server/src/__test__/announcements.test.ts`
- [X] T042 [P] [US3] Update existing test to verify repository `create()` doesn't persist location fields in `/server/src/__test__/announcements.test.ts`

### Implementation for User Story 3

**Backend**:
- [X] T043 [US3] Create migration file `[timestamp]_remove_location_fields.ts` in `/server/src/database/migrations/`
- [X] T044 [US3] Implement `up()` function to drop `location_city` column in migration file
- [X] T045 [US3] Implement `up()` function to drop `location_radius` column in migration file
- [X] T046 [US3] Implement `down()` function to recreate columns for rollback in migration file
- [X] T047 [US3] Remove `location_city` mapping in `create()` method in `/server/src/database/repositories/announcement-repository.ts`
- [X] T048 [US3] Remove `location_radius` mapping in `create()` method in `/server/src/database/repositories/announcement-repository.ts`
- [X] T049 [US3] Remove `location_city` from seed data in `/server/src/database/seeds/001_announcements.ts`
- [X] T050 [US3] Remove `location_radius` from seed data in `/server/src/database/seeds/001_announcements.ts`
- [X] T051 [US3] Run migration: `npm run knex:migrate:latest` (from server/)
- [X] T052 [US3] Test migration rollback: `npm run knex:migrate:rollback` (from server/)
- [X] T053 [US3] Re-apply migration: `npm run knex:migrate:latest` (from server/)
- [X] T054 [US3] Remove location field references from test code in `/server/src/__test__/announcements.test.ts`
- [X] T055 [US3] Run `npm test -- --coverage` and verify 80% coverage maintained
- [X] T056 [P] [US3] Run `npm run lint` and fix ESLint violations

**Checkpoint**: All user stories should now be independently functional - Database schema updated, location fields removed from all layers

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [X] T057 [P] Update API documentation in `/server/README.md` - Remove location field descriptions
- [X] T058 [P] Update API documentation in `/server/README.md` - Remove location field examples from request body
- [X] T059 [P] Update API documentation in `/server/README.md` - Remove location field examples from response body
- [X] T060 [P] Update API documentation in `/server/README.md` - Update error response examples
- [X] T061 [P] Verify all tests pass: `npm test` (from server/)
- [X] T062 [P] Verify test coverage: `npm test -- --coverage` (from server/) - ensure 80%+ maintained
- [X] T063 [P] Verify linting: `npm run lint` (from server/)
- [X] T064 [P] Verify TypeScript compilation: `npm run build` (if available) or `tsc --noEmit` (from server/)
- [X] T065 Run quickstart.md validation - verify all manual testing steps work

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed sequentially in priority order (P1 → P1 → P2)
  - US1 and US2 are both P1 but US2 depends on US1 (type updates needed first)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P1)**: Depends on US1 completion - Needs type updates from US1
- **User Story 3 (P2)**: Depends on US1 and US2 completion - Needs repository updates from US2

### Within Each User Story

- Update existing tests to reflect field removal
- Type updates before repository/service updates
- Repository updates before service updates
- Migration before repository updates (for US3)
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Test tasks within a user story marked [P] can run in parallel
- Type updates within US2 marked [P] can run in parallel (different interfaces)
- Documentation updates in Polish phase marked [P] can run in parallel

---

## Parallel Example: User Story 1

```bash
# Launch all test updates for User Story 1 together:
Task: "Update existing test to verify validation rejects locationCity"
Task: "Update existing test to verify validation rejects locationRadius"
Task: "Update existing test to verify validation rejects location field"
Task: "Update existing test to verify POST rejects locationCity"
Task: "Update existing test to verify POST rejects locationRadius"
Task: "Update existing test to verify POST rejects location field"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently - API rejects location fields
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Each story adds value without breaking previous stories

### Sequential Strategy (Recommended for Refactoring)

Since this is a refactoring task with dependencies:

1. Team completes Setup + Foundational together
2. Complete User Story 1 (validation) → Test → Commit
3. Complete User Story 2 (types + responses) → Test → Commit
4. Complete User Story 3 (database migration) → Test → Commit
5. Complete Polish phase → Final validation → Deploy

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Update existing tests to reflect field removal (no new tests needed)
- Commit after each user story completion
- Stop at any checkpoint to validate story independently
- Migration rollback tested before proceeding
- All location field references must be removed from codebase

