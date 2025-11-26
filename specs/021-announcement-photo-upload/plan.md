# Implementation Plan: Announcement Photo Upload

**Branch**: `021-announcement-photo-upload` | **Date**: 2025-11-26 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/021-announcement-photo-upload/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Backend-only feature implementing photo upload endpoint for pet announcements. Users can upload photos to existing announcements using Basic authentication with management password. Photos are saved to `public/images/` directory with filename based on announcement ID and file extension. The announcement's `photoUrl` field is updated with the relative path. The feature also removes `photoUrl` from the announcement creation endpoint's input model to establish a clear workflow: create announcement first, then upload photo separately.

## Technical Context

**Language/Version**: Node.js v24 (LTS), TypeScript (strict mode)  
**Primary Dependencies**: Express.js, Multer (file upload middleware), file-type (magic bytes validation), Knex (database query builder)  
**Storage**: SQLite (current), PostgreSQL (migration path) + filesystem (public/images/ directory for uploaded photos)  
**Testing**: Vitest (unit tests), SuperTest (integration tests for REST endpoints)  
**Target Platform**: Node.js server (Linux/macOS)
**Project Type**: Backend only (affects `/server` module only)  
**Performance Goals**: N/A (low-traffic project)  
**Constraints**: Maximum file size 20 MB, supported image formats: JPEG, PNG, GIF, WebP, BMP, TIFF, HEIC, HEIF  
**Scale/Scope**: N/A (low-traffic project)

**Research Phase Complete**: All technical unknowns resolved. See [research.md](./research.md) for details.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is a backend-only feature affecting only `/server` module. Frontend-related checks are marked N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: N/A (backend-only feature)
  - This feature only affects `/server` module
  - No changes to Android, iOS, or Web platforms
  - Violation justification: _Not applicable_

- [x] **Android MVI Architecture**: N/A (backend-only feature)
  - No Android changes required
  - Violation justification: _Not applicable_

- [x] **iOS MVVM-C Architecture**: N/A (backend-only feature)
  - No iOS changes required
  - Violation justification: _Not applicable_

- [x] **Interface-Based Design**: Repository pattern will be used for database access
  - Backend: Repository interface in `/server/src/database/repositories/` for announcements
  - Implementation will separate interface from concrete database queries
  - Photo upload service will depend on repository interface, not Knex directly
  - Violation justification: _Compliant_

- [x] **Dependency Injection**: Manual DI with constructor injection
  - Backend: Factory functions in `/server/src/services/` will receive repository instances
  - Route handlers will be injected with service instances
  - No DI framework needed (manual constructor injection pattern)
  - Violation justification: _Compliant_

- [x] **80% Test Coverage - Backend Only**: Unit and integration tests planned
  - Unit tests in `/server/src/services/__test__/` for photo upload service
  - Unit tests in `/server/src/lib/__test__/` for file validation utilities
  - Integration tests in `/server/src/__test__/` for `/api/v1/announcements/:id/photos` endpoint
  - Coverage target: 80% line + branch coverage
  - Run command: `npm test -- --coverage` (from server/)
  - Violation justification: _Compliant_

- [x] **End-to-End Tests**: N/A (backend-only feature, integration tests cover API)
  - Integration tests with SuperTest will cover full request/response cycle
  - No frontend E2E tests needed
  - Violation justification: _Not applicable_

- [x] **Asynchronous Programming Standards**: async/await in Express handlers
  - Backend: All route handlers will use native `async`/`await`
  - File upload processing will be async
  - Database operations (via Knex) will use async/await
  - No callback-based patterns
  - Violation justification: _Compliant_

- [x] **Test Identifiers for UI Controls**: N/A (no UI changes)
  - Backend-only feature
  - Violation justification: _Not applicable_

- [x] **Public API Documentation**: JSDoc for complex functions
  - TypeScript: JSDoc format for all public service functions
  - Document file validation logic, upload processing, and authentication
  - Focus on WHAT/WHY (e.g., "Validates uploaded image format using magic bytes")
  - Skip documentation for self-explanatory utility functions
  - Violation justification: _Compliant_

- [x] **Given-When-Then Test Structure**: All tests will follow GWT convention
  - Unit tests: Clear separation of setup (Given), action (When), verification (Then)
  - Integration tests: Same GWT structure for request/response testing
  - Test names: Descriptive strings (e.g., "should return 201 when photo upload succeeds")
  - Comments will mark test phases in complex scenarios
  - Violation justification: _Compliant_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: Plan uses modern Node.js stack for `/server` module
  - Runtime: Node.js v24 (LTS) ✓
  - Framework: Express.js ✓
  - Language: TypeScript with strict mode enabled ✓
  - Database: Knex query builder + SQLite (designed for PostgreSQL migration) ✓
  - Violation justification: _Fully compliant_

- [x] **Backend Code Quality**: Plan enforces quality standards for `/server` code
  - ESLint with TypeScript plugin configured and enabled ✓
  - Clean Code principles applied:
    - Small, focused functions (single responsibility) - upload service, validation utils
    - Descriptive naming - `uploadAnnouncementPhoto`, `validateImageFormat`, `savePhotoFile`
    - Maximum 3 nesting levels - flatten auth/validation/upload logic
    - DRY principle - extract file validation, auth check, photo replacement logic
    - JSDoc documentation for all public APIs ✓
  - Violation justification: _Fully compliant_

- [x] **Backend Dependency Management**: Plan minimizes dependencies in `/server/package.json`
  - Will add NEEDS CLARIFICATION (file upload middleware - research multer alternatives)
  - Will add NEEDS CLARIFICATION (file type validation - research magic byte libraries)
  - Prefer well-maintained, security-audited packages ✓
  - Avoid micro-dependencies ✓
  - Document rationale for each dependency in comments ✓
  - Regular `npm audit` security checks planned ✓
  - Violation justification: _Compliant (pending research phase)_

- [x] **Backend Directory Structure**: Plan follows standardized layout in `/server/src/`
  - `/middlewares/` - Will add file upload middleware (multipart/form-data handling)
  - `/routes/` - New route: POST /api/v1/announcements/:id/photos
  - `/services/` - Photo upload service (business logic, photo replacement, photoUrl update)
  - `/database/` - Repository updates for announcement photoUrl field
  - `/lib/` - File validation utilities (format detection, size check, filename sanitization)
  - `/__test__/` - Integration tests for photo upload endpoint
  - `app.ts` - Register new route ✓
  - `server.ts` - Configure static file serving for /public/images ✓
  - Violation justification: _Fully compliant_

- [x] **Backend TDD Workflow**: Plan follows Test-Driven Development (Red-Green-Refactor)
  - RED: Write failing tests for photo upload endpoint, validation logic, service functions
  - GREEN: Implement minimal code to pass each test
  - REFACTOR: Extract reusable validation logic to `/lib`, improve service structure
  - Tests written BEFORE implementation code ✓
  - Violation justification: _Fully compliant_

- [x] **Backend Testing Strategy**: Plan includes comprehensive test coverage for `/server`
  - Unit tests (Vitest):
    - Location: `/src/services/__test__/photoUploadService.test.ts` (photo upload business logic)
    - Location: `/src/lib/__test__/fileValidation.test.ts` (format validation, size check)
    - Coverage target: 80% line + branch coverage
    - Scope: Photo upload service, file validation utilities
  - Integration tests (Vitest + SuperTest):
    - Location: `/src/__test__/photoUpload.test.ts` (POST /api/v1/announcements/:id/photos)
    - Coverage target: 80% for API endpoint
    - Scope: Full upload flow (auth, file upload, validation, save, photoUrl update)
  - All tests follow Given-When-Then structure ✓
  - Run commands: `npm test`, `npm test -- --coverage` ✓
  - Violation justification: _Fully compliant_

## Project Structure

### Documentation (this feature)

```text
specs/021-announcement-photo-upload/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
│   └── photo-upload-api.yaml  # OpenAPI spec for POST /api/v1/announcements/:id/photos
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
server/
├── public/
│   └── images/          # NEW: Photo storage directory (announcement photos saved here)
├── src/
│   ├── middlewares/
│   │   ├── basic-auth.ts # NEW: Reusable Basic auth middleware (returns 401 if missing)
│   │   └── upload.ts    # NEW: Multer middleware for multipart/form-data handling
│   ├── routes/
│   │   └── announcements.ts  # MODIFIED: Add POST /:id/photos endpoint
│   ├── services/
│   │   ├── __test__/
│   │   │   └── photo-upload-service.test.ts  # NEW: Unit tests for photo upload logic
│   │   └── photo-upload-service.ts  # NEW: Photo upload business logic
│   ├── database/
│   │   └── repositories/
│   │       └── announcement-repository.ts  # MODIFIED: Add updatePhotoUrl method
│   ├── lib/
│   │   ├── __test__/
│   │   │   └── file-validation.test.ts  # NEW: Unit tests for file validation
│   │   └── file-validation.ts  # NEW: Image format validation, size check, filename sanitization
│   ├── __test__/
│   │   └── photo-upload.test.ts  # NEW: Integration tests for photo upload endpoint
│   └── server.ts        # MODIFIED: Configure static file serving for /public/images
└── package.json         # MODIFIED: Add Multer and file-type dependencies
```

**Structure Decision**: Backend-only feature in `/server` module. New photo upload endpoint follows existing Express architecture with kebab-case file naming convention:
- **basic-auth middleware**: Reusable authentication logic (returns 401 UNAUTHENTICATED if Authorization header missing, parses credentials for route handlers)
- **upload middleware**: Multer for file handling
- **route**: Endpoint definition with auth and upload middlewares
- **service**: Business logic (photo replacement, photoUrl update)
- **repository**: Database access
- **lib**: Reusable utilities (file validation)
Photos stored in `public/images/` directory with static file serving configured. Success response returns HTTP 201 with no response body.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No violations. All constitution checks passed for this backend-only feature.
