# Changes Summary: Announcement Photo Upload Planning

**Date**: 2025-11-26  
**Phase**: Planning adjustments based on feedback

## Changes Made

### 1. ✅ Separate Reusable Basic Auth Middleware

**Change**: Created dedicated `basicAuth` middleware for authentication logic

**Files Updated**:
- `plan.md`: Added `middlewares/basicAuth.ts` to project structure
- `quickstart.md`: Added Phase 4 for basicAuth middleware implementation
- `research.md`: Added section 3 explaining basicAuth middleware decision

**Rationale**: 
- Promotes code reusability for other endpoints requiring Basic auth
- Separation of concerns (auth logic isolated from business logic)
- Easier to test in isolation
- Follows Express middleware best practices

---

### 2. ✅ Changed Missing Auth Header Error (401 UNAUTHENTICATED)

**Change**: Missing Authorization header now returns 401 with code "UNAUTHENTICATED" (not 403 with "UNAUTHORIZED")

**Files Updated**:
- `spec.md`: FR-004 updated, acceptance scenario updated
- `data-model.md`: Error response examples updated, validation table updated
- `contracts/photo-upload-api.yaml`: Added 401 response, updated error codes enum
- `quickstart.md`: Example responses updated, test scenarios updated
- `research.md`: Added HTTP status semantics explanation

**HTTP Status Semantics**:
- **401 Unauthenticated**: No credentials provided (missing Authorization header)
- **403 Unauthorized**: Invalid credentials provided (password mismatch)

**Error Response**:
```json
{
  "error": {
    "code": "UNAUTHENTICATED",
    "message": "Authorization header is required"
  }
}
```

---

### 3. ✅ Announcement Not Found Error (NOT_FOUND)

**Change**: Announcement not found uses existing "NOT_FOUND" code with "Resource not found" message

**Files Updated**:
- `spec.md`: FR-016 updated to mention existing error codes
- `data-model.md`: Error response updated
- `contracts/photo-upload-api.yaml`: Error code updated to NOT_FOUND
- `research.md`: Added section 7 documenting error code standards

**Before**:
```json
{
  "error": {
    "code": "ANNOUNCEMENT_NOT_FOUND",
    "message": "Announcement not found"
  }
}
```

**After**:
```json
{
  "error": {
    "code": "NOT_FOUND",
    "message": "Resource not found"
  }
}
```

---

### 4. ✅ File Size Exceeded Error (PAYLOAD_TOO_LARGE)

**Change**: 413 status uses existing "PAYLOAD_TOO_LARGE" code (not "FILE_TOO_LARGE")

**Files Updated**:
- `data-model.md`: Error response updated, validation table updated
- `contracts/photo-upload-api.yaml`: Error code updated to PAYLOAD_TOO_LARGE
- `quickstart.md`: Test scenario updated
- `research.md`: Error code mapping documented

**Before**:
```json
{
  "error": {
    "code": "FILE_TOO_LARGE",
    "message": "File size exceeds maximum limit of 20 MB"
  }
}
```

**After**:
```json
{
  "error": {
    "code": "PAYLOAD_TOO_LARGE",
    "message": "Payload too large"
  }
}
```

---

### 5. ✅ Internal Server Error (INTERNAL_SERVER_ERROR)

**Change**: 500 status uses existing "INTERNAL_SERVER_ERROR" code with generic "Internal server error" message

**Files Updated**:
- `data-model.md`: Error response updated
- `contracts/photo-upload-api.yaml`: Error code and message updated
- `research.md`: Error code mapping documented

**Before**:
```json
{
  "error": {
    "code": "INTERNAL_ERROR",
    "message": "Failed to upload photo"
  }
}
```

**After**:
```json
{
  "error": {
    "code": "INTERNAL_SERVER_ERROR",
    "message": "Internal server error"
  }
}
```

**Rationale**: Generic message doesn't expose internal details (filesystem paths, database errors, etc.)

---

### 6. ✅ Reject photoUrl Field in Announcement Creation

**Change**: POST /api/v1/announcements now REJECTS photoUrl field (not ignores it)

**Files Updated**:
- `spec.md`: FR-011, FR-012 updated, acceptance scenario updated
- `data-model.md`: Announcement creation model updated with validation error example
- `quickstart.md`: Phase 7 updated, test scenario updated

**Before** (ignore photoUrl):
```json
// Request
{
  "name": "Max",
  "photoUrl": "this-will-be-ignored.jpg"
}

// Response: 201 Created (photoUrl ignored)
{
  "id": "abc123",
  "name": "Max",
  "photoUrl": null
}
```

**After** (reject photoUrl):
```json
// Request
{
  "name": "Max",
  "photoUrl": "some-photo.jpg"
}

// Response: 400 Bad Request
{
  "error": {
    "code": "INVALID_FIELD",
    "message": "photoUrl field is not allowed",
    "field": "photoUrl"
  }
}
```

---

## Implementation Impact

### New Files to Create
1. `server/src/middlewares/basicAuth.ts` - Reusable Basic auth middleware
2. `server/src/lib/errors.ts` - Add `UnauthenticatedError` class (401, "UNAUTHENTICATED")

### Modified Phases
- **Phase 4** (new): Basic Auth Middleware implementation
- **Phase 5**: Multer Middleware (renumbered from Phase 4)
- **Phase 6**: API Endpoint (renumbered from Phase 5)
- **Phase 7**: Modify Announcement Creation - now rejects photoUrl field
- **Phase 8**: Coverage & Cleanup (renumbered from Phase 7)

### Updated Test Scenarios
1. Upload without Authorization header → **401 UNAUTHENTICATED** (was 403)
2. Upload to non-existent announcement → **404 NOT_FOUND** (was ANNOUNCEMENT_NOT_FOUND)
3. Upload file > 20 MB → **413 PAYLOAD_TOO_LARGE** (was FILE_TOO_LARGE)
4. Create announcement with photoUrl → **400 INVALID_FIELD** (was accepted/ignored)

---

## Files Updated

✅ `spec.md` - Functional requirements, acceptance scenarios  
✅ `plan.md` - Project structure, technical context  
✅ `data-model.md` - Error responses, validation rules, announcement creation  
✅ `contracts/photo-upload-api.yaml` - OpenAPI spec with updated error codes  
✅ `quickstart.md` - Implementation checklist, key functions, test scenarios  
✅ `research.md` - Added auth middleware, error code standards sections  

---

## Consistency with Existing Codebase

All changes align with existing error handling patterns:
- Uses existing `NotFoundError` class from `/server/src/lib/errors.ts`
- Uses existing `PAYLOAD_TOO_LARGE` from `/server/src/middlewares/error-handler-middleware.ts`
- Uses existing `INTERNAL_SERVER_ERROR` from error handler middleware
- Adds new `UnauthenticatedError` class following same pattern
- Maintains consistent error response format: `{"error": {"code": "...", "message": "..."}}`

---

---

## Additional Changes (2025-11-26)

### 7. ✅ No Response Body for 201 Success

**Change**: Successful photo upload returns HTTP 201 with no response body

**Files Updated**:
- `data-model.md`: Removed JSON response body for 201 success
- `contracts/photo-upload-api.yaml`: Removed PhotoUploadSuccess schema and response content
- `quickstart.md`: Updated example response, updated service return type to `void`

**Before**:
```json
HTTP/1.1 201 Created
Content-Type: application/json

{
  "message": "Photo uploaded successfully",
  "photoUrl": "images/abc123.jpg"
}
```

**After**:
```
HTTP/1.1 201 Created
(no response body)
```

**Rationale**: Simplifies response handling, follows REST convention (201 indicates success, no additional data needed)

---

### 8. ✅ Kebab-Case File Naming Convention

**Change**: All new files use kebab-case naming convention (not camelCase)

**Files Updated**:
- `plan.md`: Updated all filenames in project structure
- `quickstart.md`: Updated all filenames in file structure, implementation checklist, and key functions

**Filename Changes**:
- `basicAuth.ts` → `basic-auth.ts`
- `photoUploadService.ts` → `photo-upload-service.ts`
- `photoUploadService.test.ts` → `photo-upload-service.test.ts`
- `fileValidation.ts` → `file-validation.ts`
- `fileValidation.test.ts` → `file-validation.test.ts`
- `photoUpload.test.ts` → `photo-upload.test.ts`
- `announcementRepository.ts` → `announcement-repository.ts`

**Rationale**: Consistency with project naming conventions, improves readability

---

## Next Steps

Ready for `/speckit.tasks` command to break down implementation into concrete tasks.

All planning documents are now consistent with:
- Existing error handling patterns
- Standard HTTP status code semantics
- Reusable middleware architecture
- Strict validation (reject, not ignore)
- Kebab-case file naming convention
- Minimal response bodies (201 with no body)

### 9. ✅ Photo URL Rejection Consistency (A1)

- `data-model.md`: Validation rules now state `photoUrl` is rejected during creation (aligns with spec)
- `spec.md` / `quickstart.md`: Acceptance criteria + implementation checklist highlight the 400 INVALID_FIELD response when `photoUrl` is provided
- `tasks.md`: Phase 4 tasks ensure validation logic and tests cover the rejection path

### 10. ✅ File Size Enforcement Tasks (A2)

- `tasks.md`: Summary fixed to 34 tasks; T022 now explicitly sets 20 MB `limits.fileSize`; T023 requires 413 PAYLOAD_TOO_LARGE integration scenario

### 11. ✅ Database Constraint Alignment

- `plan.md`: Summary and backend/database notes call out the need for a nullable `announcement.photo_url` column
- `data-model.md`: Database schema section explicitly requires `photo_url` to be nullable (migration required)
- `quickstart.md`: Setup checklist + file structure include creating and running the nullable `photo_url` migration
- `tasks.md`: Phase 2 now includes T009–T010 for creating and running the migration (total tasks updated)

