# Research: Announcement Photo Upload

**Feature**: 021-announcement-photo-upload  
**Date**: 2025-11-26  
**Status**: Phase 0 Complete

## Research Tasks

### 1. File Upload Middleware for Express

**Question**: Which file upload middleware should be used for handling multipart/form-data in Express?

**Options Evaluated**:
- **Multer**: Most popular Express middleware for `multipart/form-data`
- **Formidable**: Low-level, more control over parsing
- **Busboy**: Stream-based, lower-level than multer
- **Express-fileupload**: Simpler API but less maintained

**Decision**: **Multer**

**Rationale**:
- Most widely adopted Express middleware for file uploads (8.5M+ weekly downloads)
- Well-maintained by Express.js team
- Built on top of busboy for robust multipart parsing
- Provides high-level API with disk/memory storage engines
- Excellent TypeScript support with `@types/multer`
- Built-in file size limits and filtering
- Easy integration with Express middleware chain
- Security-focused: prevents directory traversal, allows MIME type filtering

**Alternatives Considered**:
- **Formidable**: Too low-level, requires more manual configuration
- **Busboy**: Too low-level, multer provides better API on top of it
- **Express-fileupload**: Less maintained, smaller community

**Implementation Notes**:
- Use `multer.diskStorage()` to save files directly to `public/images/`
- Configure `fileFilter` to validate MIME types
- Set `limits.fileSize` to 20MB (20 * 1024 * 1024 bytes)
- Use `single('photo')` middleware to accept one file with field name 'photo'

---

### 2. File Type Validation Library

**Question**: Which library should be used for robust file type validation (magic bytes detection)?

**Options Evaluated**:
- **file-type**: Popular library for detecting file types via magic bytes
- **mmmagic**: Node.js bindings for libmagic
- **mime-types**: MIME type detection based on file extension only (insufficient)
- Manual magic bytes validation (custom implementation)

**Decision**: **file-type**

**Rationale**:
- Pure JavaScript implementation (no native dependencies)
- Actively maintained (11M+ weekly downloads)
- Detects file types by checking magic bytes (first bytes of file buffer)
- Supports all required image formats: JPEG, PNG, GIF, WebP, BMP, TIFF, HEIC, HEIF
- TypeScript-first with excellent type definitions
- Simple async API: `await fileTypeFromBuffer(buffer)`
- Returns MIME type and extension
- More reliable than MIME type from HTTP headers (which can be spoofed)

**Alternatives Considered**:
- **mmmagic**: Requires native libmagic bindings, adds complexity and deployment issues
- **mime-types**: Only checks file extension, not actual file content (insecure)
- **Manual implementation**: Reinventing the wheel, error-prone

**Implementation Notes**:
- Read first 4100 bytes of uploaded file to detect type (file-type requirement)
- Validate detected MIME type against allowlist: `image/jpeg`, `image/png`, `image/gif`, `image/webp`, `image/bmp`, `image/tiff`, `image/heic`, `image/heif`
- Reject files if magic bytes don't match expected image format (prevent MIME type spoofing)
- Use detected extension from `file-type` result, not from uploaded filename

---

### 3. Basic Authentication Middleware

**Question**: Should authentication logic be reusable for other endpoints?

**Decision**: Create separate `basicAuth` middleware

**Rationale**:
- Promotes code reusability (can be used by other endpoints requiring Basic auth)
- Separation of concerns (auth logic isolated from business logic)
- Easier to test in isolation
- Follows Express middleware best practices
- Returns 401 UNAUTHENTICATED if Authorization header missing (standard HTTP semantics)
- Returns 403 UNAUTHORIZED if credentials invalid (access denied after authentication)

**Implementation**:
- Middleware parses `Authorization: Basic <base64>` header
- Decodes base64 credentials as `announcementId:managementPassword`
- Attaches parsed credentials to `req.auth` for downstream route handlers
- Returns 401 if header missing (not authenticated)
- Route handlers verify credentials and return 403 if invalid (not authorized)

**HTTP Status Semantics**:
- **401 Unauthenticated**: No credentials provided (missing Authorization header)
- **403 Unauthorized**: Invalid credentials provided (password mismatch, wrong announcement)

---

### 4. Best Practices for File Upload Security

**Research Summary**:

**File Validation**:
- ✓ Validate file size before processing (multer `limits.fileSize`)
- ✓ Validate MIME type using magic bytes (file-type library)
- ✓ Use allowlist for permitted MIME types (reject unknown formats)
- ✓ Sanitize filename (remove path traversal sequences like `../`, non-ASCII chars)
- ✓ Generate deterministic filenames based on announcement ID (prevent collisions)

**Storage Security**:
- ✓ Store files outside web root or configure static serving carefully
- ✓ Use separate directory for uploads (`public/images/`)
- ✓ Set appropriate file permissions (read-only for web server)
- ✓ Implement file size limits to prevent disk exhaustion

**Authentication & Authorization**:
- ✓ Verify management password matches announcement (Basic auth)
- ✓ Encode credentials as `announcementId:managementPassword` in base64
- ✓ Return 403 for authentication failures
- ✓ Return 404 for non-existent announcements (leak prevention)

**Error Handling**:
- ✓ Return 400 for invalid file format
- ✓ Return 413 for file size exceeded
- ✓ Return 500 for filesystem errors (log details server-side only)
- ✓ Use consistent error format: `{"error": {"code": "ERROR_CODE", "message": "..."}}`

---

### 5. Photo Replacement Strategy

**Question**: How should existing photos be replaced when a new photo is uploaded?

**Decision**: Delete old photo immediately when new photo is uploaded

**Rationale**:
- Prevents disk space bloat (only one photo per announcement)
- Simplifies storage management (no need to track old photos)
- Matches user expectation (replace, not version)
- No requirement for photo history/versioning

**Implementation**:
1. Check if announcement already has `photoUrl` field populated
2. If yes, construct absolute path to old photo file (`public/${photoUrl}`)
3. Delete old photo file using `fs.promises.unlink()` (ignore ENOENT errors)
4. Save new photo file with announcement ID as filename
5. Update announcement's `photoUrl` field with new relative path

**Edge Cases**:
- If old photo file doesn't exist (manually deleted), ignore error and proceed
- If old photo deletion fails (permissions), log error but proceed with upload
- If new photo save fails, don't update database (transaction-like behavior)

---

### 6. Static File Serving Configuration

**Question**: How should static files be served from `public/images/` directory?

**Decision**: Use Express `express.static()` middleware

**Configuration**:
```typescript
// In server.ts
app.use('/images', express.static(path.join(__dirname, '../public/images')));
```

**Rationale**:
- Built-in Express middleware (no additional dependencies)
- Automatically handles content-type headers based on file extension
- Supports range requests for efficient image loading
- Secure by default (prevents directory traversal)

**URL Format**:
- Photos accessible at: `http://localhost:3000/images/abc123.jpg`
- Stored in database as: `images/abc123.jpg` (relative path)
- Clients can construct full URL by prepending server base URL

---

### 7. Error Code Standards

**Question**: Which error codes should be used for consistency with existing codebase?

**Decision**: Use existing error codes from `/server/src/lib/errors.ts` and `/server/src/middlewares/error-handler-middleware.ts`

**Error Code Mapping**:
- **401 UNAUTHENTICATED**: Missing Authorization header (new error code to be added)
- **403 UNAUTHORIZED**: Invalid credentials (password mismatch)
- **404 NOT_FOUND**: Resource not found (existing, with message "Resource not found")
- **413 PAYLOAD_TOO_LARGE**: File size exceeds limit (existing)
- **500 INTERNAL_SERVER_ERROR**: Internal server error (existing, with message "Internal server error")

**Rationale**:
- Consistency with existing error handling patterns
- Standard HTTP status code semantics
- Avoids custom error messages that expose internal details
- Reuses existing error classes (NotFoundError, CustomError)

**Implementation**:
- Create new `UnauthenticatedError` class extending `CustomError` (401, "UNAUTHENTICATED")
- Reuse existing error handling middleware for consistent error format
- All error responses follow format: `{"error": {"code": "ERROR_CODE", "message": "error message"}}`

---

## Technology Choices Summary

| Technology | Purpose | Rationale |
|-----------|---------|-----------|
| **Multer** | File upload middleware | Industry standard for Express, robust, well-maintained |
| **file-type** | Magic bytes validation | Pure JS, TypeScript-first, supports all required formats |
| **Express static** | Static file serving | Built-in, secure, no additional dependencies |
| **fs.promises** | File operations | Native Node.js async file API, no external dependencies |

---

## Dependencies to Add

```json
{
  "dependencies": {
    "multer": "^1.4.5-lts.1"
  },
  "devDependencies": {
    "@types/multer": "^1.4.11",
    "file-type": "^19.0.0"
  }
}
```

**Rationale**:
- **multer**: Production dependency for file upload handling
- **@types/multer**: TypeScript type definitions for multer
- **file-type**: Dev dependency (used in validation utils, not exposed to production runtime)

---

## Open Questions Resolved

All NEEDS CLARIFICATION items from Technical Context have been resolved:
- ✅ File upload middleware: **Multer** (industry standard, robust API)
- ✅ File type validation: **file-type** library (magic bytes detection)

No remaining unknowns. Ready to proceed to Phase 1 (Design & Contracts).

