# Feature Specification: Announcement Photo Upload

**Feature Branch**: `021-announcement-photo-upload`  
**Created**: 2025-11-26  
**Status**: Draft  
**Input**: User description: "Na backendzie potrzebujemy endpointu do uploadu zdjęć POST /api/v1/announcements/:id/photos. Ma przyjmować header Authorization o wartości Basic <management password jako base64>. Jeśli management password nie pasuje do hasła ogłoszenia o podanym ID, zwracamy 403 z kodem UNAUTHORIZED. Jeśli upload się powiedzie, zwracamy 201. Zdjęcia mają być zapisywane w katalogu public. Nazwa ma być zmieniona na ID ogłoszenia (z zachowaniem rozszerzenia). Ogłoszenie o podanym ID ma być zaktualizowane o photoUrl. Musimy też zmienić endpoint do dodawania nowego ogłoszenia tak żeby nie przyjmował photoUrl - usuwamy to z modelu wejściowego."

## Clarifications

### Session 2025-11-26

- Q: How should Basic Authorization credentials be encoded for photo uploads? → A: Encode `announcementId:managementPassword`
- Q: What should the photoUrl value format be when a photo is uploaded? → A: Relative path format `images/{announcementId}.{extension}` (e.g., `images/abc123.jpg`)
- Q: When replacing a photo, should the old file be deleted or kept? → A: Delete the old photo file immediately when a new photo is uploaded
- Q: How should the photo file be submitted in the POST request? → A: Use multipart/form-data with a form field named `photo` containing the image
- Q: How should error responses be formatted? → A: JSON object with nested `error` object: `{"error": {"code": "ERROR_CODE", "message": "Error message"}}`

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Upload Photo to Existing Announcement (Priority: P1)

A user who created a lost/found pet announcement needs to add a photo of the animal after the initial announcement was created. They use their management password to authenticate and upload the photo.

**Why this priority**: This is the core functionality that enables users to visually document their pet announcements. Photos are critical for pet identification and significantly increase the likelihood of successful pet recovery.

**Independent Test**: Can be fully tested by creating an announcement, then uploading a photo using the management password, and verifying the photo is saved and the announcement is updated with the photo URL.

**Acceptance Scenarios**:

1. **Given** an announcement exists with ID "abc123" and management password "secret123", **When** user sends POST request to /api/v1/announcements/abc123/photos with Basic auth header containing base64-encoded "abc123:secret123" and multipart/form-data containing `photo` field with a valid image file, **Then** system returns 201 status with no response body, saves the photo with filename "abc123.jpg" (or appropriate extension), and updates the announcement's photoUrl field to `images/abc123.jpg`
2. **Given** an announcement exists with a photo already uploaded, **When** user uploads a new photo with correct management password, **Then** system replaces the existing photo file and updates the photoUrl field
3. **Given** an announcement exists with ID "xyz789", **When** user uploads a photo with valid image format (JPEG, PNG, GIF, WebP), **Then** system preserves the original file extension in the saved filename (e.g., "xyz789.png")

---

### User Story 2 - Prevent Unauthorized Photo Upload (Priority: P1)

A user attempts to upload a photo to someone else's announcement without proper authorization. The system must protect announcements from unauthorized modifications.

**Why this priority**: Security is critical to prevent malicious users from modifying or vandalizing other users' announcements. This ensures data integrity and user trust.

**Independent Test**: Can be tested by attempting to upload a photo with an incorrect management password and verifying that access is denied with appropriate error code.

**Acceptance Scenarios**:

1. **Given** an announcement exists with ID "abc123" and management password "secret123", **When** user sends upload request with incorrect password "wrongpass", **Then** system returns 403 status with response body `{"error": {"code": "UNAUTHORIZED", "message": "Invalid credentials"}}`
2. **Given** an announcement exists with ID "abc123", **When** user sends upload request without Authorization header, **Then** system returns 401 status with response body `{"error": {"code": "UNAUTHENTICATED", "message": "Authorization header is required"}}`
3. **Given** an announcement with ID "nonexistent" does not exist, **When** user attempts to upload photo to that ID, **Then** system returns 404 status indicating announcement not found

---

### User Story 3 - Create Announcement Without Photo (Priority: P2)

A user creates a new announcement for a lost or found pet but doesn't have a photo available at the time of creation. They can submit the announcement without a photo and add it later.

**Why this priority**: Removing photoUrl from the creation endpoint simplifies the announcement creation process and establishes a clear workflow: create first, then upload photo separately. This prevents confusion about when photos should be provided.

**Independent Test**: Can be tested by creating a new announcement without providing photoUrl in the request body and verifying that the announcement is created successfully without a photo.

**Acceptance Scenarios**:

1. **Given** user has all required announcement data except photo, **When** user sends POST request to /api/v1/announcements with all required fields but no photoUrl, **Then** system creates the announcement successfully and returns 201 status
2. **Given** user attempts to create announcement, **When** user includes photoUrl field in the request body, **Then** system returns 400 status with error code "INVALID_FIELD" and message indicating photoUrl field is not allowed
3. **Given** announcement is created without photo, **When** announcement is retrieved, **Then** photoUrl field is either null or empty string

---

### Edge Cases

- What happens when user uploads a file that is not a valid image format (not JPEG/PNG/GIF/WebP/BMP/TIFF/HEIC/HEIF)? → System returns 400 status
- What happens when the uploaded file exceeds 20 MB size limit? → System returns 413 status
- What happens when the public directory doesn't exist or lacks write permissions? → System returns 500 status with appropriate error message
- What happens when multiple photos are uploaded simultaneously to the same announcement? → Last successful upload wins, previous photo file is replaced
- What happens if the announcement ID contains special characters that are invalid for filenames? → System sanitizes the filename or returns validation error
- What happens when uploading a photo to an announcement that was deleted? → System returns 404 status
- What happens when the image file has no extension? → System attempts to detect format from file content (magic bytes) or rejects with 400 status

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide a POST endpoint at /api/v1/announcements/:id/photos for uploading photos to existing announcements (accepts multipart/form-data with `photo` field)
- **FR-002**: System MUST authenticate photo upload requests using Basic authentication, encoding credentials as `announcementId:managementPassword`
- **FR-003**: System MUST return 403 status with error code "UNAUTHORIZED" when the provided management password does not match the announcement's management password
- **FR-004**: System MUST return 401 status with error code "UNAUTHENTICATED" when no Authorization header is provided
- **FR-005**: System MUST return 201 status with no response body when photo upload succeeds
- **FR-006**: System MUST return 404 status when attempting to upload photo to a non-existent announcement
- **FR-007**: System MUST save uploaded photos to the public/images directory with filename format: {announcementId}.{extension}
- **FR-008**: System MUST preserve the original file extension when saving photos
- **FR-009**: System MUST update the announcement's photoUrl field with relative path format: `images/{announcementId}.{extension}` (e.g., `images/abc123.jpg`)
- **FR-010**: System MUST replace existing photo files when a new photo is uploaded to the same announcement (delete old file, keep only current photo)
- **FR-011**: System MUST reject photoUrl field in the POST /api/v1/announcements endpoint's input validation
- **FR-012**: System MUST return 400 status with error code "INVALID_FIELD" when photoUrl field is provided in announcement creation requests
- **FR-013**: System MUST validate that uploaded files are valid image formats (JPEG, PNG, GIF, WebP, BMP, TIFF, HEIC, HEIF)
- **FR-014**: System MUST enforce maximum file size limit of 20 MB for uploaded photos
- **FR-015**: System MUST return 400 status when uploaded file format is invalid and 413 status when file size exceeds the limit
- **FR-016**: System MUST return error responses in JSON format: `{"error": {"code": "ERROR_CODE", "message": "descriptive error message"}}` for all error statuses (400, 401, 403, 404, 413, 500) using existing error codes (UNAUTHENTICATED, UNAUTHORIZED, NOT_FOUND, PAYLOAD_TOO_LARGE, INTERNAL_SERVER_ERROR)

### Key Entities

- **Announcement**: Represents a lost or found pet announcement with fields including id (unique identifier), managementPassword (for authorization), photoUrl (URL path to uploaded photo, nullable)
- **Photo File**: Physical image file stored in public/images directory with filename based on announcement ID and original file extension

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can upload a photo to an existing announcement and see it reflected in the announcement details within 2 seconds
- **SC-002**: System successfully prevents unauthorized photo uploads with 100% accuracy (all requests with invalid credentials are rejected)
- **SC-003**: Users can create announcements without photos and the creation process completes successfully in under 1 second
- **SC-004**: Photo upload endpoint handles concurrent upload requests to different announcements without data corruption or conflicts
- **SC-005**: System properly manages storage by replacing old photo files when new photos are uploaded, preventing storage bloat
