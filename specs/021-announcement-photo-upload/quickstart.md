# Quickstart: Announcement Photo Upload

**Feature**: 021-announcement-photo-upload  
**Branch**: `021-announcement-photo-upload`  
**Status**: Ready for Implementation

## Overview

Backend-only feature that adds photo upload capability to existing announcements via new POST endpoint. Users authenticate with management password and upload photos via multipart/form-data. Photos are saved to `public/images/` with announcement ID as filename.

## Key Technical Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| **File Upload Middleware** | Multer | Industry standard, robust, well-maintained |
| **File Type Validation** | file-type library (magic bytes) | Prevents MIME type spoofing, reliable format detection |
| **Filename Strategy** | `{announcementId}.{extension}` | Deterministic, prevents collisions, simplifies replacement |
| **Photo Replacement** | Delete old file immediately | Prevents disk bloat, no versioning requirement |
| **Static Serving** | Express `express.static()` | Built-in, secure, no additional dependencies |
| **Authentication** | Basic auth (`announcementId:managementPassword`) | Simple, matches existing auth pattern |

## New Dependencies

```bash
cd server
npm install multer@^1.4.5-lts.1
npm install --save-dev @types/multer@^1.4.11 file-type@^19.0.0
```

## Quick Reference

### Endpoint

```
POST /api/v1/announcements/:id/photos
```

**Authentication**: Basic `announcementId:managementPassword` (base64 encoded)  
**Body**: multipart/form-data with `photo` field  
**Max Size**: 20 MB  
**Formats**: JPEG, PNG, GIF, WebP, BMP, TIFF, HEIC, HEIF

### Example Request (curl)

```bash
# 1. Encode credentials (announcementId:managementPassword)
echo -n "abc123:secret123" | base64
# Output: YWJjMTIzOnNlY3JldDEyMw==

# 2. Upload photo
curl -X POST \
  -H "Authorization: Basic YWJjMTIzOnNlY3JldDEyMw==" \
  -F "photo=@/path/to/image.jpg" \
  http://localhost:3000/api/v1/announcements/abc123/photos
```

### Example Response (Success)

```
HTTP/1.1 201 Created
(no response body)
```

### Example Response (Error - Missing Auth)

```json
{
  "error": {
    "code": "UNAUTHENTICATED",
    "message": "Authorization header is required"
  }
}
```

### Example Response (Error - Invalid Credentials)

```json
{
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Invalid credentials"
  }
}
```

## Implementation Checklist

### Phase 1: Setup & Dependencies
- [ ] Install multer, @types/multer, file-type
- [ ] Create `server/public/images/` directory
- [ ] Configure static file serving in `server/src/server.ts`
- [ ] Add `.gitkeep` to `public/images/` to track empty directory

### Phase 2: File Validation (TDD)
- [ ] Write tests for file type validation (`lib/__test__/file-validation.test.ts`)
- [ ] Implement `validateImageFormat()` using file-type library
- [ ] Implement `sanitizeFilename()` to remove path traversal sequences
- [ ] Implement `generatePhotoFilename()` to create deterministic filenames
- [ ] Run tests: `npm test -- lib/__test__/file-validation.test.ts`

### Phase 3: Photo Upload Service (TDD)
- [ ] Write tests for photo upload service (`services/__test__/photo-upload-service.test.ts`)
- [ ] Implement `uploadAnnouncementPhoto()` service function
- [ ] Handle photo replacement logic (delete old file)
- [ ] Update announcement's photoUrl in database
- [ ] Run tests: `npm test -- services/__test__/photo-upload-service.test.ts`

### Phase 4: Basic Auth Middleware
- [ ] Create `middlewares/basic-auth.ts` with reusable auth logic
- [ ] Parse Authorization header (Basic scheme)
- [ ] Decode base64 credentials (`announcementId:managementPassword`)
- [ ] Return 401 UNAUTHENTICATED if header missing
- [ ] Attach parsed credentials to request object for route handlers
- [ ] Add unit tests for auth middleware

### Phase 5: Multer Middleware
- [ ] Create `middlewares/upload.ts` with multer configuration
- [ ] Configure disk storage: `public/images/` directory
- [ ] Set file size limit: 20 MB
- [ ] Add file filter for preliminary MIME type check
- [ ] Export `uploadSingle` middleware for route use

### Phase 6: API Endpoint (TDD)
- [ ] Write integration tests (`__test__/photo-upload.test.ts`)
- [ ] Add POST route in `routes/announcements.ts`
- [ ] Use basicAuth middleware to parse credentials
- [ ] Verify announcement exists (404 NOT_FOUND if not)
- [ ] Validate management password (403 UNAUTHORIZED if mismatch)
- [ ] Call photo upload service
- [ ] Return 201 with no response body on success
- [ ] Run tests: `npm test -- __test__/photo-upload.test.ts`

### Phase 7: Modify Announcement Creation
- [ ] Update POST /api/v1/announcements validation to reject photoUrl field
- [ ] Return 400 VALIDATION_ERROR if photoUrl is provided
- [ ] Ensure photoUrl is not saved during announcement creation
- [ ] Add integration test to verify photoUrl is rejected
- [ ] Run tests: `npm test -- __test__/announcements.test.ts`

### Phase 8: Coverage & Cleanup
- [ ] Run full test suite: `npm test -- --coverage`
- [ ] Verify 80%+ coverage for services, lib, and integration tests
- [ ] Run linter: `npm run lint`
- [ ] Fix any ESLint violations
- [ ] Manual testing with curl or Postman

## File Structure

```
server/
├── public/
│   └── images/              # NEW: Photo storage
│       └── .gitkeep         # Track empty directory
├── src/
│   ├── middlewares/
│   │   ├── basic-auth.ts    # NEW: Basic auth middleware (reusable)
│   │   └── upload.ts        # NEW: Multer configuration
│   ├── routes/
│   │   └── announcements.ts # MODIFIED: Add POST /:id/photos
│   ├── services/
│   │   ├── __test__/
│   │   │   └── photo-upload-service.test.ts  # NEW
│   │   └── photo-upload-service.ts           # NEW
│   ├── database/
│   │   └── repositories/
│   │       └── announcement-repository.ts  # MODIFIED: Add updatePhotoUrl
│   ├── lib/
│   │   ├── __test__/
│   │   │   └── file-validation.test.ts  # NEW
│   │   └── file-validation.ts           # NEW
│   ├── __test__/
│   │   └── photo-upload.test.ts  # NEW
│   └── server.ts             # MODIFIED: Static file serving
└── package.json              # MODIFIED: Add dependencies
```

## Key Functions to Implement

### 1. Basic Auth Middleware (`middlewares/basic-auth.ts`)

```typescript
/**
 * Express middleware that parses Basic authentication credentials.
 * Returns 401 UNAUTHENTICATED if Authorization header is missing.
 * Attaches parsed credentials to req object for downstream handlers.
 */
export const basicAuth = (req: Request, res: Response, next: NextFunction) => {
  const authHeader = req.headers.authorization;
  
  if (!authHeader) {
    return res.status(401).json({
      error: {
        code: 'UNAUTHENTICATED',
        message: 'Authorization header is required'
      }
    });
  }
  
  // Parse Basic auth: "Basic base64(announcementId:password)"
  // Attach to req.auth = { announcementId, password }
  // Call next()
};
```

### 2. File Validation (`lib/file-validation.ts`)

```typescript
/**
 * Validates image format using magic bytes detection.
 * Returns detected MIME type and extension if valid.
 */
export async function validateImageFormat(
  buffer: Buffer
): Promise<{ mimeType: string; extension: string }>;

/**
 * Sanitizes filename to prevent path traversal attacks.
 */
export function sanitizeFilename(filename: string): string;

/**
 * Generates deterministic filename based on announcement ID.
 */
export function generatePhotoFilename(
  announcementId: string,
  extension: string
): string;
```

### 3. Photo Upload Service (`services/photo-upload-service.ts`)

```typescript
/**
 * Uploads photo to announcement and updates photoUrl.
 * Deletes old photo if exists.
 */
export async function uploadAnnouncementPhoto(
  announcementId: string,
  photoBuffer: Buffer,
  originalFilename: string
): Promise<void>;  // No return value (201 with no body)
```

### 4. Multer Middleware (`middlewares/upload.ts`)

```typescript
/**
 * Multer middleware for single file upload.
 * Validates file size and preliminary MIME type.
 */
export const uploadSingle = multer({
  storage: diskStorage({
    destination: 'public/images',
    filename: (req, file, cb) => {
      // Generate filename based on announcement ID
    }
  }),
  limits: { fileSize: 20 * 1024 * 1024 },
  fileFilter: (req, file, cb) => {
    // Preliminary MIME type check
  }
}).single('photo');
```

### 5. Route Handler (`routes/announcements.ts`)

```typescript
router.post('/:id/photos', basicAuth, uploadSingle, async (req, res, next) => {
  try {
    // 1. Get parsed credentials from req.auth (set by basicAuth middleware)
    // 2. Verify announcement exists (throw NotFoundError with 404 if not)
    // 3. Verify management password (throw 403 UNAUTHORIZED if mismatch)
    // 4. Validate file format (magic bytes)
    // 5. Call photo upload service
    // 6. Return 201 with photoUrl
  } catch (error) {
    next(error);
  }
});
```

## Testing Strategy

### Unit Tests (Vitest)
- `lib/__test__/fileValidation.test.ts`: Format validation, filename sanitization
- `services/__test__/photoUploadService.test.ts`: Upload logic, photo replacement

### Integration Tests (Vitest + SuperTest)
- `__test__/photoUpload.test.ts`: Full endpoint flow (auth, upload, validation, errors)

### Coverage Target
- 80%+ line and branch coverage for all new code
- Run: `npm test -- --coverage`

### Test Scenarios
1. ✅ Upload photo with valid credentials → 201
2. ✅ Upload photo with invalid credentials → 403 UNAUTHORIZED
3. ✅ Upload photo without Authorization header → 401 UNAUTHENTICATED
4. ✅ Upload photo to non-existent announcement → 404 NOT_FOUND
5. ✅ Upload invalid file format → 400 INVALID_FILE_FORMAT
6. ✅ Upload file exceeding 20 MB → 413 PAYLOAD_TOO_LARGE
7. ✅ Replace existing photo → old file deleted, new file saved
8. ✅ Create announcement with photoUrl in body → 400 VALIDATION_ERROR

## Common Pitfalls

❌ **Don't** trust MIME type from HTTP headers (can be spoofed)  
✅ **Do** validate file format using magic bytes (file-type library)

❌ **Don't** use uploaded filename directly (path traversal risk)  
✅ **Do** generate deterministic filename based on announcement ID

❌ **Don't** forget to delete old photo when replacing  
✅ **Do** implement photo replacement logic in service

❌ **Don't** expose internal errors to client (e.g., filesystem paths)  
✅ **Do** return generic error messages with specific codes

❌ **Don't** block event loop with synchronous file operations  
✅ **Do** use `fs.promises` for async file operations

## Deployment Checklist

- [ ] Verify `public/images/` directory exists in production
- [ ] Ensure write permissions for Node.js process
- [ ] Configure static file serving for `/images` route
- [ ] Test with production-like file sizes (e.g., 15-20 MB images)
- [ ] Monitor disk space usage (add alerts if needed)
- [ ] Document photo cleanup strategy for deleted announcements

## Related Documentation

- [Feature Spec](./spec.md) - Full requirements and user scenarios
- [Research](./research.md) - Technology choices and rationale
- [Data Model](./data-model.md) - Entity relationships and validation rules
- [API Contract](./contracts/photo-upload-api.yaml) - OpenAPI specification

## Support

For questions or issues during implementation:
1. Review research.md for technology choices
2. Check data-model.md for validation rules
3. Refer to contracts/photo-upload-api.yaml for API details
4. Consult constitution.md for backend quality standards

