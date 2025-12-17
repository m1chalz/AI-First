# Feature Specification: Announcements Authentication & User Tracking

**Feature Branch**: `060-announcements-auth`  
**Created**: 2025-12-17  
**Status**: Draft  
**Input**: User description: "Endpoint /api/v1/announcements - tworzenie ogłoszeń (POST) wymaga autentykacji z Bearer token. Przeglądanie (GET) pozostaje publiczne. Upload zdjęć (POST /:id/photos) również wymaga Bearer token zamiast management password. Do tabeli announcements dodajemy kolumnę user_id (uuid, not null). Przy tworzeniu ogłoszenia pobieramy user ID z JWT. Nie zwracamy user_id w odpowiedziach API. Management password authentication jest deprecated."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Authenticated Announcement Creation (Priority: P1)

A logged-in user wants to create a pet announcement (lost or found). The system must verify their identity and automatically associate the announcement with their account without requiring manual user identification.

**Why this priority**: Core functionality that enables user accountability and ownership of announcements. Without this, the feature cannot function.

**Independent Test**: Can be fully tested by authenticating a user, creating an announcement, and verifying the announcement is created and associated with the correct user account in the database.

**Acceptance Scenarios**:

1. **Given** a user is authenticated with a valid Bearer token, **When** they submit a new announcement, **Then** the announcement is created and associated with their user ID from the JWT
2. **Given** a user is authenticated, **When** they create multiple announcements, **Then** all announcements are correctly associated with their user ID

---

### User Story 2 - Unauthorized Creation Prevention (Priority: P1)

An unauthenticated user attempts to create an announcement. The system must deny access and provide clear feedback about the authentication requirement.

**Why this priority**: Security requirement - critical to enforce authentication before announcement creation. Without this, the entire authentication feature is meaningless.

**Independent Test**: Can be tested by making POST requests to the announcements endpoint without authentication and verifying proper rejection with appropriate error messages.

**Acceptance Scenarios**:

1. **Given** a user is not authenticated (no Authorization header), **When** they attempt to create a new announcement, **Then** they receive a 401 Unauthorized response
2. **Given** a user provides an invalid Bearer token, **When** they attempt to create an announcement, **Then** they receive a 401 Unauthorized response with an error message
3. **Given** a user provides an expired Bearer token, **When** they attempt to create an announcement, **Then** they receive a 401 Unauthorized response

---

### User Story 3 - Browse Announcements Publicly (Priority: P2)

Any user (authenticated or not) wants to browse pet announcements. The system displays announcements without exposing internal user identifiers for privacy reasons.

**Why this priority**: Essential for public access to view and search announcements. Browsing must remain open to maximize announcement visibility and help reunite pets with owners.

**Independent Test**: Can be tested by requesting the announcements list without authentication and verifying announcements are returned without user_id fields exposed.

**Acceptance Scenarios**:

1. **Given** any user (authenticated or not), **When** they request the announcements list, **Then** they receive announcements without user_id fields exposed
2. **Given** any user (authenticated or not), **When** they request a specific announcement detail, **Then** they receive the full announcement data without user_id exposed

---

### User Story 4 - Authenticated Photo Upload (Priority: P1)

A logged-in user wants to upload photos to their pet announcement. The system must verify their identity using Bearer token authentication, replacing the deprecated management password approach.

**Why this priority**: Critical security requirement - photo upload must be protected to prevent abuse and ensure only authenticated users can attach images to announcements. Replaces insecure management password authentication.

**Independent Test**: Can be fully tested by authenticating a user with Bearer token, uploading a photo to an announcement, and verifying the photo is successfully stored and associated with the announcement.

**Acceptance Scenarios**:

1. **Given** a user is authenticated with a valid Bearer token, **When** they upload a photo to an announcement, **Then** the photo is successfully uploaded and stored
2. **Given** a user is not authenticated (no Authorization header), **When** they attempt to upload a photo, **Then** they receive a 401 Unauthorized response
3. **Given** a user provides an invalid or expired Bearer token, **When** they attempt to upload a photo, **Then** they receive a 401 Unauthorized response
4. **Given** a user is authenticated, **When** they upload multiple photos to an announcement, **Then** all photos are successfully processed with the same authentication

---

### Edge Cases

- What happens when a user's account is deleted but they have existing announcements? Announcements should remain active and visible publicly (user_id references should be preserved via database constraints preventing user deletion if announcements exist, or via soft delete on users table)
- How does the system handle concurrent announcement creation requests from the same user? Each request should be processed independently with the same user_id
- What happens if JWT is valid but user_id from token doesn't exist in the database during creation? Request should be rejected with 401 Unauthorized (invalid token state)
- How does the system behave if the database user_id column migration fails partially (some rows migrated, others not)? System should prevent deployment until migration is complete and validated
- What happens when JWT contains malformed or missing user_id claim during creation? Request should be rejected with 401 Unauthorized with appropriate error message
- How does system handle GET requests with invalid authentication headers? System should ignore authentication for GET and return announcements regardless (authentication is optional for viewing)
- What happens when a user tries to upload a photo using the old management password method? Request should be rejected with 401 Unauthorized and appropriate error message indicating Bearer token is required
- How does system handle photo upload to an announcement that doesn't exist? Request should be rejected with 404 Not Found after authentication validation
- What happens if a user uploads a photo with valid Bearer token but invalid image format? Authentication passes but upload fails with 400 Bad Request (validation error)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST require a valid Bearer token in the Authorization header for announcement creation operations (POST /api/v1/announcements)
- **FR-002**: System MUST require a valid Bearer token in the Authorization header for photo upload operations (POST /api/v1/announcements/:id/photos)
- **FR-003**: System MUST allow unauthenticated access to announcement viewing operations (GET /api/v1/announcements and GET /api/v1/announcements/:id)
- **FR-004**: System MUST validate Bearer tokens for creation and upload operations and reject requests with invalid, expired, or missing tokens with HTTP 401 Unauthorized
- **FR-005**: System MUST extract user_id from the JWT claims when processing authenticated announcement creation requests
- **FR-006**: System MUST automatically associate new announcements with the authenticated user's ID from the JWT without requiring it in the request body
- **FR-007**: System MUST store user_id as a non-null UUID in the announcements table
- **FR-008**: System MUST NOT include user_id in the response payload for announcements list endpoint (GET /api/v1/announcements)
- **FR-009**: System MUST NOT include user_id in the response payload for announcement detail endpoint (GET /api/v1/announcements/:id)
- **FR-010**: System MUST handle JWT validation errors gracefully with appropriate error messages for authenticated endpoints
- **FR-011**: System MUST return standardized error responses for authentication failures (401 for unauthorized, 403 for forbidden operations)
- **FR-012**: System MUST reject photo upload requests that use deprecated management password authentication with 401 Unauthorized
- **FR-013**: System MUST remove management password authentication support from photo upload endpoint

### Key Entities

- **Announcement**: Represents a pet lost/found announcement with attributes including title, description, location, date, pet details, images, and owner user_id (UUID). Each announcement is now owned by exactly one authenticated user.
- **User**: Represents an authenticated user account. Users can create and own multiple announcements. User identification comes from JWT authentication token.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: All announcement creation requests (POST /api/v1/announcements) without valid authentication tokens are rejected with 401 status code
- **SC-002**: All photo upload requests (POST /:id/photos) without valid Bearer tokens are rejected with 401 status code
- **SC-003**: All announcement viewing requests (GET) are accessible without authentication
- **SC-004**: 100% of new announcements created have a valid user_id associated with them from the JWT
- **SC-005**: Zero user_id fields are exposed in announcement list or detail API responses
- **SC-006**: Authenticated users can successfully create announcements with less than 2 seconds response time
- **SC-007**: Authenticated users can successfully upload photos with less than 3 seconds response time
- **SC-008**: System maintains 99.9% authentication validation accuracy (correct acceptance of valid tokens and rejection of invalid tokens)
- **SC-009**: Unauthenticated users can browse announcements with the same performance as before (no degradation)
- **SC-010**: Zero photo uploads succeed using deprecated management password authentication (100% rejection rate)

## Assumptions

- JWT authentication infrastructure already exists and is functional
- JWT tokens contain a user_id claim that can be extracted
- User accounts exist in a separate users table with UUID primary keys
- All existing announcements in the database will need to be associated with users (migration strategy needed but out of scope for this spec)
- Token validation middleware or library is available or will be implemented
- Standard JWT Bearer token format is used: `Authorization: Bearer <token>`
- User account deletion should be prevented if active announcements exist (database constraint), or users should use soft delete to preserve announcement history
- Database migrations will be executed atomically (all-or-nothing) to prevent partial migration states
- Management password authentication is fully deprecated across the system and will be removed without backward compatibility
- Clients currently using management password for photo uploads will need to immediately switch to Bearer token authentication
- Photo upload endpoint is the only remaining endpoint using management password authentication

## Dependencies

- JWT authentication service must be operational
- Users table must exist with UUID primary keys
- Database migration tooling available for adding user_id column
- Existing announcement creation endpoint must be modified to accept authenticated requests
- Photo upload endpoint must be modified to remove management password authentication (basicAuthMiddleware)
- Photo upload endpoint must be modified to use Bearer token authentication
- Management password middleware (basicAuthMiddleware) must be removed from photo upload route

## Out of Scope

- Migration strategy for existing announcements without user_id (needs to be defined separately)
- User registration or login endpoints (assumed to exist)
- JWT token generation and refresh logic
- Authorization rules for photo uploads (who can upload photos to which announcements - any authenticated user can currently upload to any announcement)
- Ownership verification (preventing users from uploading photos to others' announcements)
- User profile management
- Announcement ownership transfer between users
- Admin override capabilities for managing any announcement
- Rate limiting for announcement creation or photo uploads
- Removal of management password from other endpoints (if any exist) - only photo upload endpoint is in scope
- Migration path or transition period for clients using management password - immediate deprecation and removal
- Notification to existing clients about authentication changes
- Update or delete announcement endpoints (not implemented yet)
