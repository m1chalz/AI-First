# Tasks: Announcement Photo Upload

**Feature**: 021-announcement-photo-upload  
**Branch**: `021-announcement-photo-upload`  
**Generated**: 2025-11-26  
**Spec**: [spec.md](./spec.md) | **Plan**: [plan.md](./plan.md)

## Overview

Backend-only feature implementing photo upload endpoint for pet announcements. Users authenticate with management password and upload photos via multipart/form-data. Photos are saved to `public/images/` with announcement ID as filename. TDD approach with 80% test coverage target.

**Key Technologies**: Node.js v24, TypeScript, Express.js, Multer, file-type, Vitest, SuperTest

## Task Summary

- **Total Tasks**: 34
- **Parallelizable**: 15 tasks marked with [P]
- **User Stories**: 2 (US1 & US2 combined as P1, US3 as P2)
- **Test Coverage Target**: 80% line + branch coverage
- **MVP Scope**: User Story 1 & 2 (photo upload with security)

## Implementation Strategy

**MVP-First Approach**:
1. Start with Phase 1 (Setup) and Phase 2 (Foundational)
2. Complete Phase 3 (US1 & US2 - Photo Upload with Security) for MVP
3. Phase 4 (US3 - Announcement Creation Changes) can be delivered separately
4. Phase 5 (Polish) after all user stories complete

**Incremental Delivery**:
- After Phase 3: Deliverable MVP (photo upload endpoint functional and secure)
- After Phase 4: Complete feature (announcement creation validation updated)
- After Phase 5: Production-ready (coverage verified, documentation complete)

---

## Phase 1: Setup

**Goal**: Install dependencies and create project structure

### Tasks

- [x] T001 Install Multer and file-type dependencies in /Users/pawelkedra/code/AI-First/server/package.json
- [x] T002 Create public/images directory in /Users/pawelkedra/code/AI-First/server/public/images/
- [x] T003 Add .gitkeep file to track empty images directory in /Users/pawelkedra/code/AI-First/server/public/images/.gitkeep
- [x] T004 Configure static file serving for /images route in /Users/pawelkedra/code/AI-First/server/src/server.ts

**Validation**: Dependencies installed, images directory created, static serving configured

---

## Phase 2: Foundational (Blocking Prerequisites)

**Goal**: Implement reusable authentication middleware and error classes that all user stories depend on

### Tasks

- [x] T005 [P] Create UnauthenticatedError class (401) in /Users/pawelkedra/code/AI-First/server/src/lib/errors.ts
- [x] T006 [P] Write unit tests for basic-auth middleware in /Users/pawelkedra/code/AI-First/server/src/middlewares/__test__/basic-auth.test.ts
- [x] T007 Implement basic-auth middleware with Authorization header parsing in /Users/pawelkedra/code/AI-First/server/src/middlewares/basic-auth.ts
- [x] T008 Run basic-auth middleware tests and verify all pass
- [x] T009 [P] Create migration to make `announcement.photo_url` column nullable in /Users/pawelkedra/code/AI-First/server/src/database/migrations/20251126184000_make_photo_url_nullable.ts
- [x] T010 Run database migrations (knex migrate:latest) and verify schema reflects nullable `photo_url`

**Validation**: UnauthenticatedError class created, basic-auth middleware implemented and tested, returns 401 when Authorization header missing

**Blocking**: Must complete before any user story implementation (all stories require authentication)

---

## Phase 3: User Story 1 & 2 (P1) - Photo Upload with Security

**Story Goal**: Enable users to upload photos to existing announcements with proper authentication and authorization. Prevent unauthorized photo uploads.

**Why P1**: Core functionality for visual pet documentation + critical security to prevent unauthorized modifications

**Independent Test Criteria**:
- ✅ Can create announcement, then upload photo with valid credentials → 201 (no body), photo saved, photoUrl updated
- ✅ Upload with invalid credentials → 403 UNAUTHORIZED
- ✅ Upload without Authorization header → 401 UNAUTHENTICATED
- ✅ Upload to non-existent announcement → 404 NOT_FOUND
- ✅ Upload invalid format → 400 INVALID_FILE_FORMAT
- ✅ Upload > 20 MB → 413 PAYLOAD_TOO_LARGE
- ✅ Replace existing photo → old file deleted, new file saved

### File Validation Utilities

- [ ] T011 [P] [US1] Write unit tests for validateImageFormat function in /Users/pawelkedra/code/AI-First/server/src/lib/__test__/file-validation.test.ts
- [ ] T012 [P] [US1] Write unit tests for sanitizeFilename function in /Users/pawelkedra/code/AI-First/server/src/lib/__test__/file-validation.test.ts
- [ ] T013 [P] [US1] Write unit tests for generatePhotoFilename function in /Users/pawelkedra/code/AI-First/server/src/lib/__test__/file-validation.test.ts
- [ ] T014 [US1] Implement validateImageFormat using file-type library in /Users/pawelkedra/code/AI-First/server/src/lib/file-validation.ts
- [ ] T015 [US1] Implement sanitizeFilename to prevent path traversal in /Users/pawelkedra/code/AI-First/server/src/lib/file-validation.ts
- [ ] T016 [US1] Implement generatePhotoFilename for deterministic filenames in /Users/pawelkedra/code/AI-First/server/src/lib/file-validation.ts
- [ ] T017 [US1] Run file-validation unit tests and verify 80%+ coverage

### Photo Upload Service

- [ ] T018 [P] [US1] Write unit tests for uploadAnnouncementPhoto service in /Users/pawelkedra/code/AI-First/server/src/services/__test__/photo-upload-service.test.ts
- [ ] T019 [US1] Implement uploadAnnouncementPhoto service with photo replacement logic in /Users/pawelkedra/code/AI-First/server/src/services/photo-upload-service.ts
- [ ] T020 [US1] Run photo-upload-service unit tests and verify 80%+ coverage

### Repository Updates

- [ ] T021 [P] [US1] Add updatePhotoUrl method to announcement repository in /Users/pawelkedra/code/AI-First/server/src/database/repositories/announcement-repository.ts

### Multer Middleware

- [ ] T022 [P] [US1] Create multer upload middleware with disk storage configuration and 20 MB `limits.fileSize` + preliminary MIME filter in /Users/pawelkedra/code/AI-First/server/src/middlewares/upload.ts

### API Endpoint Integration

- [ ] T023 [P] [US1] Write integration tests for POST /api/v1/announcements/:id/photos endpoint (including 413 PAYLOAD_TOO_LARGE scenario) in /Users/pawelkedra/code/AI-First/server/src/__test__/photo-upload.test.ts
- [ ] T024 [US1] Add POST /:id/photos route with basic-auth and upload middlewares in /Users/pawelkedra/code/AI-First/server/src/routes/announcements.ts
- [ ] T025 [US1] Implement route handler with validation and service call in /Users/pawelkedra/code/AI-First/server/src/routes/announcements.ts
- [ ] T026 [US1] Run photo-upload integration tests and verify all scenarios pass

**Story Complete**: Photo upload endpoint functional, secure, and independently tested

---

## Phase 4: User Story 3 (P2) - Reject photoUrl in Announcement Creation

**Story Goal**: Establish clear workflow by rejecting photoUrl field in announcement creation endpoint

**Why P2**: Simplifies creation process and prevents confusion, but not blocking for core photo upload functionality

**Independent Test Criteria**:
- ✅ Create announcement without photoUrl → 201, photoUrl is null
- ✅ Create announcement with photoUrl in body → 400 INVALID_FIELD
- ✅ Retrieve created announcement → photoUrl is null

### Tasks

- [ ] T027 [P] [US3] Write integration test for rejecting photoUrl field in /Users/pawelkedra/code/AI-First/server/src/__test__/announcements.test.ts
- [ ] T028 [US3] Update POST /api/v1/announcements validation to reject photoUrl field in /Users/pawelkedra/code/AI-First/server/src/routes/announcements.ts
- [ ] T029 [US3] Run announcement creation tests and verify photoUrl rejection

**Story Complete**: Announcement creation rejects photoUrl field with proper validation error

---

## Phase 5: Polish & Cross-Cutting Concerns

**Goal**: Verify coverage, run linter, and ensure production readiness

### Tasks

- [ ] T030 Run full test suite with coverage in /Users/pawelkedra/code/AI-First/server/
- [ ] T031 Verify 80%+ coverage for all new code (services, lib, integration)
- [ ] T032 Run ESLint and fix any violations in /Users/pawelkedra/code/AI-First/server/src/
- [ ] T033 Manual testing: Upload photos with curl/Postman to verify end-to-end flow
- [ ] T034 Verify error responses match spec for all error scenarios

**Validation**: 80%+ coverage achieved, no linter errors, manual tests pass, all error codes correct

---

## Dependency Graph

### Story Completion Order

```
Setup (Phase 1) → Foundational (Phase 2) → [US1 & US2] → [US3] → Polish
                                              (P1)         (P2)
```

### Story Dependencies

- **US1 & US2** (P1): Depends on Foundational (basic-auth middleware, error classes)
- **US3** (P2): Independent (can be implemented in parallel with US1 & US2 if desired, but lower priority)
- **Polish**: Depends on all user stories complete

### Task Dependencies Within Phases

**Phase 2 (Foundational)**:
- T005 (error class) → can run in parallel with T006-T008
- T006 (tests) → T007 (implementation) → T008 (verify)

**Phase 3 (US1 & US2)**:
- File Validation: T011-T013 (tests parallel) → T014-T016 (implementations) → T017 (verify)
- Service: T018 (tests) → T019 (implementation) → T020 (verify)
- Repository: T021 (independent, can run in parallel with validation)
- Multer: T022 (independent, can run in parallel)
- Integration: T023 (tests) → T024-T025 (implementation) → T026 (verify)

**Phase 4 (US3)**:
- T027 (tests) → T028 (implementation) → T029 (verify)

---

## Parallel Execution Opportunities

### Phase 1 (Setup)
- All setup tasks (T001-T004) can run sequentially (low overhead)

### Phase 2 (Foundational)
- **Parallel Group 1**: T005 (error class) + T006 (auth tests)
- **Sequential**: T007 (auth implementation) → T008 (verify)

### Phase 3 (US1 & US2)
- **Parallel Group 1**: T011, T012, T013 (all file-validation tests)
- **Sequential**: T014, T015, T016 (implementations) → T017 (verify)
- **Parallel Group 2**: T018 (service tests) + T021 (repository) + T022 (multer)
- **Sequential**: T019 (service implementation) → T020 (verify)
- **Parallel Group 3**: T023 (integration tests)
- **Sequential**: T024-T025 (route implementation) → T026 (verify)

### Phase 4 (US3)
- **Parallel**: T027 (tests, independent from Phase 3 if desired)
- **Sequential**: T028 (implementation) → T029 (verify)

### Phase 5 (Polish)
- All polish tasks run sequentially (verification phase)

---

## Testing Strategy

### Unit Tests (80% coverage target)

**File Validation** (`/server/src/lib/__test__/file-validation.test.ts`):
- ✅ validateImageFormat: valid JPEG/PNG/GIF/WebP/BMP/TIFF/HEIC/HEIF → success
- ✅ validateImageFormat: invalid format → error
- ✅ validateImageFormat: spoofed MIME type → detected by magic bytes
- ✅ sanitizeFilename: path traversal sequences → sanitized
- ✅ generatePhotoFilename: announcement ID + extension → deterministic name

**Photo Upload Service** (`/server/src/services/__test__/photo-upload-service.test.ts`):
- ✅ uploadAnnouncementPhoto: new photo → saves file, updates photoUrl
- ✅ uploadAnnouncementPhoto: replace existing → deletes old file, saves new
- ✅ uploadAnnouncementPhoto: file save failure → no database update
- ✅ uploadAnnouncementPhoto: database update failure → rollback file

**Basic Auth Middleware** (`/server/src/middlewares/__test__/basic-auth.test.ts`):
- ✅ Missing Authorization header → 401 UNAUTHENTICATED
- ✅ Valid Basic auth → credentials parsed and attached to req
- ✅ Invalid base64 encoding → 401 UNAUTHENTICATED
- ✅ Malformed credentials format → 401 UNAUTHENTICATED

### Integration Tests (80% coverage target)

**Photo Upload Endpoint** (`/server/src/__test__/photo-upload.test.ts`):
- ✅ Upload with valid credentials → 201 (no body), photo saved, photoUrl updated
- ✅ Upload without Authorization header → 401 UNAUTHENTICATED
- ✅ Upload with invalid credentials → 403 UNAUTHORIZED
- ✅ Upload to non-existent announcement → 404 NOT_FOUND
- ✅ Upload invalid file format → 400 INVALID_FILE_FORMAT
- ✅ Upload file > 20 MB → 413 PAYLOAD_TOO_LARGE
- ✅ Replace existing photo → old file deleted, new file saved
- ✅ Upload with missing photo field → 400 MISSING_FILE

**Announcement Creation** (`/server/src/__test__/announcements.test.ts`):
- ✅ Create without photoUrl → 201, photoUrl is null
- ✅ Create with photoUrl in body → 400 INVALID_FIELD

### Test Execution Commands

```bash
# Unit tests
cd /Users/pawelkedra/code/AI-First/server
npm test -- lib/__test__/file-validation.test.ts
npm test -- services/__test__/photo-upload-service.test.ts
npm test -- middlewares/__test__/basic-auth.test.ts

# Integration tests
npm test -- __test__/photo-upload.test.ts
npm test -- __test__/announcements.test.ts

# Full suite with coverage
npm test -- --coverage

# ESLint
npm run lint
```

---

## Task Format Reference

All tasks follow the format: `- [ ] [TaskID] [P?] [Story?] Description with file path`

**Legend**:
- `[P]` = Parallelizable (different files, no dependencies)
- `[US1]` = User Story 1 task
- `[US2]` = User Story 2 task (combined with US1 in this feature)
- `[US3]` = User Story 3 task

---

## Notes

### File Naming Convention
All new files use kebab-case: `basic-auth.ts`, `photo-upload-service.ts`, `file-validation.ts`

### Error Codes
Using existing error codes from codebase:
- 401: UNAUTHENTICATED (new class to add)
- 403: UNAUTHORIZED (existing)
- 404: NOT_FOUND (existing)
- 413: PAYLOAD_TOO_LARGE (existing)
- 500: INTERNAL_SERVER_ERROR (existing)

### Success Response
Photo upload success returns HTTP 201 with no response body (following REST conventions)

### TDD Workflow
Each implementation task follows Red-Green-Refactor:
1. Write failing test
2. Implement minimal code to pass
3. Refactor for quality

### MVP Scope
Minimum viable product includes Phase 1, 2, and 3 (photo upload with security). Phase 4 (announcement creation validation) can be delivered separately.

