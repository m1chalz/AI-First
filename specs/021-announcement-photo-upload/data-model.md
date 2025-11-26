# Data Model: Announcement Photo Upload

**Feature**: 021-announcement-photo-upload  
**Date**: 2025-11-26  
**Status**: Phase 1 Complete

## Entities

### Announcement (MODIFIED)

Represents a lost or found pet announcement. This feature modifies the `photoUrl` field behavior.

**Fields**:
- `id` (string, primary key): Unique identifier for the announcement
- `managementPassword` (string, required): Password for managing the announcement (used for photo upload authorization)
- `photoUrl` (string, nullable): Relative path to uploaded photo (e.g., `images/abc123.jpg`)
  - **MODIFIED**: Now set via separate photo upload endpoint, not during creation
  - Initially `null` or empty string when announcement created
  - Updated after successful photo upload
  - Format: `images/{announcementId}.{extension}`
- Other fields: (name, species, description, etc. - not modified by this feature)

**Validation Rules**:
- `photoUrl` field MUST be ignored/removed from POST /api/v1/announcements input validation
- `photoUrl` field MUST accept `null` or empty string
- `photoUrl` field MUST be updated only via photo upload endpoint

**State Transitions**:
```
[Announcement Created] → photoUrl: null
         ↓
[Photo Uploaded] → photoUrl: "images/abc123.jpg"
         ↓
[Photo Replaced] → photoUrl: "images/abc123.png" (old file deleted)
```

---

### Photo File (NEW)

Physical image file stored in filesystem. Not a database entity, but managed as part of the announcement lifecycle.

**Location**: `server/public/images/`

**Filename Format**: `{announcementId}.{extension}`
- Example: `abc123.jpg`, `xyz789.png`
- `announcementId`: ID of associated announcement (ensures uniqueness)
- `extension`: Original file extension from uploaded image (jpg, png, gif, webp, bmp, tiff, heic, heif)

**File Metadata**:
- **Max Size**: 20 MB (20 * 1024 * 1024 bytes)
- **Allowed MIME Types**:
  - `image/jpeg` (.jpg, .jpeg)
  - `image/png` (.png)
  - `image/gif` (.gif)
  - `image/webp` (.webp)
  - `image/bmp` (.bmp)
  - `image/tiff` (.tiff, .tif)
  - `image/heic` (.heic)
  - `image/heif` (.heif)

**Lifecycle**:
1. **Upload**: File saved with announcement ID as base name
2. **Replace**: Old file deleted before saving new file
3. **Delete**: (Future feature) When announcement deleted, photo should be deleted

---

## Relationships

### Announcement ↔ Photo File (1:1)

- One announcement can have **at most one** photo file
- Photo file is identified by announcement ID (filename = `{announcementId}.{extension}`)
- Relationship stored in `photoUrl` field (relative path to photo)
- No foreign key constraint (filesystem-based relationship)

**Referential Integrity**:
- Announcement MUST exist before photo can be uploaded (404 if not found)
- Photo MUST be deleted when announcement is deleted (future enhancement)
- Photo replacement MUST delete old file before saving new file

---

## Database Schema Changes

### announcement Table (MODIFIED)

**BEFORE** (existing schema):
```sql
CREATE TABLE announcement (
  id TEXT PRIMARY KEY,
  managementPassword TEXT NOT NULL,
  photoUrl TEXT,
  -- other fields...
);
```

**AFTER** (no schema changes required):
- Schema remains the same
- Only validation/business logic changes:
  - `photoUrl` field is now **write-only via photo upload endpoint**
  - `photoUrl` field is **excluded from POST /api/v1/announcements input validation**

**Migration**: No database migration needed (only application-level validation changes)

---

## Request/Response Models

### Photo Upload Request

**Endpoint**: `POST /api/v1/announcements/:id/photos`

**Headers**:
```
Authorization: Basic <base64(announcementId:managementPassword)>
Content-Type: multipart/form-data
```

**Body** (multipart/form-data):
- `photo` (file, required): Image file to upload

**Example** (curl):
```bash
curl -X POST \
  -H "Authorization: Basic YWJjMTIzOnNlY3JldDEyMw==" \
  -F "photo=@/path/to/image.jpg" \
  http://localhost:3000/api/v1/announcements/abc123/photos
```

---

### Photo Upload Response (Success)

**Status**: `201 Created`

**Body**: None (empty response body)

---

### Photo Upload Response (Errors)

**401 Unauthorized** (missing Authorization header):
```json
{
  "error": {
    "code": "UNAUTHENTICATED",
    "message": "Authorization header is required"
  }
}
```

**403 Forbidden** (invalid credentials):
```json
{
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Invalid credentials"
  }
}
```

**404 Not Found** (announcement doesn't exist):
```json
{
  "error": {
    "code": "NOT_FOUND",
    "message": "Resource not found"
  }
}
```

**400 Bad Request** (invalid file format):
```json
{
  "error": {
    "code": "INVALID_FILE_FORMAT",
    "message": "Invalid image format. Supported formats: JPEG, PNG, GIF, WebP, BMP, TIFF, HEIC, HEIF"
  }
}
```

**413 Payload Too Large** (file size exceeded):
```json
{
  "error": {
    "code": "PAYLOAD_TOO_LARGE",
    "message": "Payload too large"
  }
}
```

**500 Internal Server Error** (filesystem or database error):
```json
{
  "error": {
    "code": "INTERNAL_SERVER_ERROR",
    "message": "Internal server error"
  }
}
```

---

## Announcement Creation Model (MODIFIED)

**Endpoint**: `POST /api/v1/announcements`

**BEFORE** (existing behavior):
- Input model accepted `photoUrl` field

**AFTER** (modified behavior):
- Input validation MUST reject requests containing `photoUrl` field
- `photoUrl` field MUST NOT be accepted in request body
- `photoUrl` field MUST remain `null` after creation (only set via photo upload endpoint)

**Example Request** (valid):
```json
{
  "name": "Max",
  "species": "dog",
  "description": "Brown Labrador"
}
```

**Example Response**:
```json
{
  "id": "abc123",
  "name": "Max",
  "species": "dog",
  "description": "Brown Labrador",
  "photoUrl": null,
  "managementPassword": "secret123",
  "createdAt": "2025-11-26T10:00:00Z"
}
```

**Example Request** (invalid - photoUrl provided):
```json
{
  "name": "Max",
  "species": "dog",
  "description": "Brown Labrador",
  "photoUrl": "some-photo.jpg"
}
```

**Example Response** (400 error):
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "photoUrl field is not allowed",
    "field": "photoUrl"
  }
}
```

---

## Validation Rules Summary

| Field | Rule | Error Code | Status |
|-------|------|-----------|--------|
| `photo` (file) | Required | `MISSING_FILE` | 400 |
| `photo` size | ≤ 20 MB | `PAYLOAD_TOO_LARGE` | 413 |
| `photo` format | JPEG/PNG/GIF/WebP/BMP/TIFF/HEIC/HEIF | `INVALID_FILE_FORMAT` | 400 |
| `Authorization` header | Required | `UNAUTHENTICATED` | 401 |
| Credentials format | `base64(announcementId:managementPassword)` | `UNAUTHORIZED` | 403 |
| Management password | Must match announcement | `UNAUTHORIZED` | 403 |
| Announcement ID | Must exist | `NOT_FOUND` | 404 |

---

## File Storage Structure

```
server/
└── public/
    └── images/
        ├── abc123.jpg       # Photo for announcement abc123
        ├── xyz789.png       # Photo for announcement xyz789
        └── def456.webp      # Photo for announcement def456
```

**Access URL**: `http://localhost:3000/images/abc123.jpg`

**Database Value**: `images/abc123.jpg` (relative path)

---

## Design Decisions

### Why relative paths instead of absolute URLs?

**Decision**: Store `images/abc123.jpg` instead of `http://localhost:3000/images/abc123.jpg`

**Rationale**:
- Environment-agnostic (works in dev, staging, production)
- Allows server URL to change without database migration
- Clients can construct full URL based on their environment
- Reduces storage size (shorter strings)

### Why use announcement ID as filename?

**Decision**: Use `{announcementId}.{extension}` instead of random UUIDs

**Rationale**:
- Deterministic filenames (easy to find photo for announcement)
- Prevents filename collisions (announcement ID is unique)
- Simplifies photo replacement (no need to query database for old filename)
- Enables direct file access if needed (debugging, manual cleanup)

### Why delete old photo immediately?

**Decision**: Delete old photo when new photo uploaded, instead of versioning

**Rationale**:
- No requirement for photo history
- Prevents disk space bloat
- Simplifies storage management
- Matches user expectation (replace, not version)

### Why validate MIME type using magic bytes?

**Decision**: Use `file-type` library to detect file format from magic bytes

**Rationale**:
- HTTP Content-Type header can be spoofed
- File extension can be changed
- Magic bytes provide reliable file type detection
- Prevents malicious files disguised as images

