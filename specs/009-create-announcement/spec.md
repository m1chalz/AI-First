# Feature Specification: Create Announcement Endpoint

**Feature Branch**: `009-create-announcement`  
**Created**: 2025-11-20  
**Status**: Draft  
**Input**: User description: "Potrzebujemy endpointu backendowego który będzie tworzył nowe ogłoszenie"

## Clarifications

### Session 2025-11-21

- Q: Should the POST `/api/v1/announcements` endpoint require authentication? → A: Public endpoint (no authentication required, anyone can create announcements)
- Q: What status should be assigned to newly created announcements? → A: Always set status to MISSING for new announcements (indicating the pet is currently missing)
- Q: How should the system handle required text fields containing only whitespace? → A: Reject whitespace-only values (treat as empty/missing, return validation error)
- Q: Should the endpoint enforce maximum length validation on text fields? → A: Use the limits defined in announcement DB table (petName: 100, description: 1000, location: 255, email: 255, phone: 50, breed: 100, photoUrl: 500)
- Q: Should validation continue after finding the first error to report all problems at once? → A: Stop at first error, return only that error (fail-fast validation)
- Q: What error response format should be used with fail-fast validation? → A: Simplified format with field-level code directly: `{ error: { code, message, field } }` (no nested details array needed)
- Q: How should unexpected errors (database failures, etc.) be handled? → A: Return HTTP 500 with generic error response `{ error: { code: "INTERNAL_SERVER_ERROR", message: "Internal server error" } }` (no field property, no internal details exposed)
- Q: Should the system include pet microchip number and prevent duplicate announcements? → A: Yes, add optional microchip_number field (max 15 chars, numeric only). Return HTTP 409 with format `{ error: { code: "CONFLICT", message: "An entity with this value already exists", field: "microchipNumber" } }` if duplicate exists
- Q: How should the system handle special characters or encoded content in announcement fields? → A: Sanitize all text input to prevent XSS attacks (escape/remove dangerous HTML/script tags while preserving safe special characters)
- Q: What should happen if the request data contains fields not defined in the model? → A: Reject the request with HTTP 400 (strict validation for security - unknown fields indicate potential attack or API misuse)
- Q: Should the system provide a way to manage announcements without full user authentication? → A: Yes, generate a 6-digit numeric management_password for each announcement. Return it only once in POST response, never expose it in GET endpoints
- Q: Which announcement fields are required in the POST request body? → A: Required: petName, species, gender, location, lastSeenDate, photoUrl, contact (email OR phone). Optional: breed, description, locationRadius, microchipNumber

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Submit New Announcement (Priority: P1)

A user wants to post a new pet announcement (lost/found pet) by submitting announcement details including contact information to the system.

**Why this priority**: Core functionality - without the ability to create announcements, the platform has no content. This is the most critical user action.

**Independent Test**: Can be fully tested by submitting a POST request with valid announcement data and verifying the announcement is created and returned with HTTP 201 status.

**Acceptance Scenarios**:

1. **Given** a user has valid announcement details with email contact, **When** they submit the announcement, **Then** the system creates the announcement and returns it with HTTP 201 status
2. **Given** a user has valid announcement details with phone contact, **When** they submit the announcement, **Then** the system creates the announcement and returns it with HTTP 201 status
3. **Given** a user has valid announcement details with both email and phone, **When** they submit the announcement, **Then** the system creates the announcement and returns it with HTTP 201 status
4. **Given** a user submits a valid announcement with a microchip number, **When** no other announcement exists with that microchip number, **Then** the system creates the announcement and returns it with HTTP 201 status
5. **Given** a user submits a valid announcement, **When** the announcement is created, **Then** the returned announcement has status set to "MISSING"
6. **Given** a user submits a valid announcement, **When** the announcement is created, **Then** the response includes a 6-digit management_password that can be used to manage the announcement
7. **Given** a user creates an announcement, **When** they retrieve announcements via GET endpoint, **Then** the management_password is not included in the response

---

### User Story 2 - Receive Clear Validation Errors (Priority: P1)

A user submits an announcement with invalid or missing required fields and receives clear, actionable error messages explaining what needs to be corrected.

**Why this priority**: Essential for user experience - users need to understand what went wrong and how to fix it. Poor error handling leads to frustration and abandoned submissions.

**Independent Test**: Can be fully tested by submitting invalid data (missing fields, wrong formats) and verifying that HTTP 400 is returned with detailed error messages in the specified format.

**Acceptance Scenarios**:

**Acceptance Scenarios**:

1. **Given** a user submits an announcement without any contact information (no email, no phone), **When** the system validates the request, **Then** it returns HTTP 400 with error details indicating at least one contact method is required
2. **Given** a user submits an announcement with an invalid email format, **When** the system validates the request, **Then** it returns HTTP 400 with error details specifying the email format is invalid
3. **Given** a user submits an announcement with an invalid phone format, **When** the system validates the request, **Then** it returns HTTP 400 with error details specifying the phone format is invalid
4. **Given** a user submits an announcement with required fields containing only whitespace, **When** the system validates the request, **Then** it returns HTTP 400 with error details indicating those fields are required
5. **Given** a user submits an announcement without a required field (e.g., petName, photoUrl), **When** the system validates the request, **Then** it returns HTTP 400 with error code "NOT_EMPTY" for the missing field
6. **Given** a user submits an announcement without optional fields (e.g., description, breed), **When** the system validates the request, **Then** it accepts the announcement and creates it successfully
7. **Given** a user submits an announcement with text fields exceeding maximum length limits, **When** the system validates the request, **Then** it returns HTTP 400 with error details specifying which fields exceed their maximum length
8. **Given** a user submits an announcement with a microchip number containing non-numeric characters, **When** the system validates the request, **Then** it returns HTTP 400 with error code "INVALID_FORMAT" for the microchipNumber field
9. **Given** a user submits an announcement with unknown fields not defined in the model, **When** the system validates the request, **Then** it returns HTTP 400 with error code "INVALID_FIELD" and the name of the unknown field
10. **Given** a user submits an announcement with special characters or HTML tags in text fields, **When** the system processes the input, **Then** it sanitizes the input to prevent XSS attacks and stores the safe version
11. **Given** a user submits an announcement with multiple validation errors, **When** the system validates the request, **Then** it returns HTTP 400 with only the first validation error in the simplified format `{ error: { code, message, field } }`

---

### User Story 3 - Prevent Duplicate Microchip Announcements (Priority: P2)

A user attempts to create an announcement for a pet with a microchip number that already exists in the system, and the system prevents duplicate announcements and informs the user.

**Why this priority**: Important for data integrity - prevents duplicate lost pet announcements for the same pet. However, lower priority than core creation and validation since not all pets have microchips.

**Independent Test**: Can be fully tested by creating an announcement with a microchip number, then attempting to create another announcement with the same microchip number and verifying HTTP 409 is returned.

**Acceptance Scenarios**:

1. **Given** an announcement already exists with microchip number "123456789", **When** a user tries to create a new announcement with the same microchip number, **Then** the system returns HTTP 409 with error code "CONFLICT" and field "microchipNumber"
2. **Given** a user submits an announcement with a unique microchip number, **When** the system checks for duplicates, **Then** the system creates the announcement successfully
3. **Given** a user submits an announcement without a microchip number, **When** the system processes the request, **Then** the system skips duplicate checking and creates the announcement (microchip is optional)

---

### Edge Cases

- **Required vs Optional fields**: System validates that all required fields (petName, species, gender, location, lastSeenDate, photoUrl, and email OR phone) are present and non-empty. Optional fields (breed, description, locationRadius, microchipNumber) can be omitted without error.
- **Whitespace-only fields**: System treats whitespace-only values in required text fields as empty and returns HTTP 400 validation error
- **Extremely long input values**: System enforces maximum length limits from database schema (petName: 100, description: 1000, location: 255, email: 255, phone: 50, breed: 100, photoUrl: 500, microchipNumber: 15) and returns HTTP 400 when exceeded
- **Invalid microchip format**: System validates that microchip numbers contain only digits (numeric characters) and returns HTTP 400 with "INVALID_FORMAT" if non-numeric characters are present
- **Multiple invalid fields**: System uses fail-fast validation - returns HTTP 400 with only the first validation error encountered, not all errors at once
- **Duplicate microchip numbers**: System checks for existing announcements with the same microchip number and returns HTTP 409 if found (only when microchip number is provided)
- **Special characters and XSS prevention**: System sanitizes all text input to prevent XSS attacks by escaping/removing dangerous HTML/script tags (e.g., `<script>`, `<iframe>`, etc.) while preserving safe special characters
- **Unknown fields in request**: System rejects requests containing fields not defined in the announcement model with HTTP 400 and error code "INVALID_FIELD" (strict validation for security)
- **Management password security**: System generates unique 6-digit password for each announcement, returns it only in POST response (one-time exposure), stores it hashed in database, and never includes it in GET responses
- **User-provided management password**: System ignores any management_password provided in POST request body (system-generated only, user cannot specify)
- **Unexpected system errors**: System returns HTTP 500 with generic error response `{ error: { code: "INTERNAL_SERVER_ERROR", message: "Internal server error" } }` without exposing internal details (database errors, crashes, etc.)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST accept POST requests to `/api/v1/announcements` endpoint
- **FR-002**: Endpoint MUST be publicly accessible without authentication (no login required)
- **FR-003**: System MUST validate that each announcement has at least one contact method (email OR phone OR both)
- **FR-004**: System MUST validate email format using existing email validator when email is provided
- **FR-005**: System MUST validate phone format using existing phone validator when phone is provided
- **FR-006**: System MUST return HTTP 201 status code when announcement is successfully created
- **FR-007**: System MUST return HTTP 400 status code when validation fails
- **FR-008**: System MUST return HTTP 409 status code when an announcement with the same microchip number already exists
- **FR-009**: System MUST return HTTP 500 status code when unexpected errors occur (database failures, system errors, etc.)
- **FR-010**: System MUST return the newly created announcement in the response body using the same model as GET `/api/v1/announcements` endpoint, with the addition of the management_password field (only present in POST response)
- **FR-011**: System MUST validate all required announcement fields and reject requests missing required data (required fields: petName, species, gender, location, lastSeenDate, photoUrl, and at least one contact method - email OR phone)
- **FR-012**: System MUST accept optional fields without validation errors when not provided (optional fields: breed, description, locationRadius, microchipNumber)
- **FR-013**: System MUST treat required text fields containing only whitespace as empty and reject them with validation error
- **FR-014**: System MUST enforce maximum length validation on text fields matching database column limits (petName: 100, description: 1000, location: 255, email: 255, phone: 50, breed: 100, photoUrl: 500, microchipNumber: 15 characters)
- **FR-015**: System MUST validate that microchip number contains only digits (numeric characters only) when provided
- **FR-016**: System MUST check if an announcement with the provided microchip number already exists (when microchip number is provided)
- **FR-017**: System MUST return structured validation error responses in the format: `{ error: { code, message, field } }` where code is the specific validation error code
- **FR-018**: System MUST return conflict error responses in the format: `{ error: { code: "CONFLICT", message: "An entity with this value already exists", field: "microchipNumber" } }` when duplicate microchip detected
- **FR-019**: System MUST return generic error responses for unexpected errors in the format: `{ error: { code: "INTERNAL_SERVER_ERROR", message: "Internal server error" } }` without field property and without exposing internal details
- **FR-020**: System MUST use fail-fast validation (stop at first error and return only that single error)
- **FR-021**: System MUST set validation error codes to specific types (e.g., "NOT_EMPTY" for required fields, "INVALID_FORMAT" for format errors, "TOO_LONG" for length violations, "MISSING_CONTACT" for missing email/phone, "INVALID_FIELD" for unknown fields)
- **FR-022**: System MUST include the field name in the `field` property of validation error responses to identify which field caused the validation failure
- **FR-023**: System MUST persist successfully created announcements to the database
- **FR-024**: System MUST assign a unique identifier to each newly created announcement
- **FR-025**: System MUST set the status field to "MISSING" for all newly created announcements (user cannot specify status in request)
- **FR-026**: System MUST generate a unique 6-digit numeric management_password for each newly created announcement
- **FR-027**: System MUST return the management_password in the POST response body when creating an announcement
- **FR-028**: System MUST NOT include management_password in GET responses (list or detail views)
- **FR-029**: System MUST store management_password securely (hashed) in the database
- **FR-030**: System MUST sanitize all text input fields to prevent XSS attacks by escaping or removing dangerous HTML/script tags
- **FR-031**: System MUST reject requests containing fields not defined in the announcement model with HTTP 400 and error code "INVALID_FIELD"

#### Success Response Example

**Successful announcement creation** (HTTP 201):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "petName": "Max",
  "species": "DOG",
  "breed": "Golden Retriever",
  "gender": "MALE",
  "description": "Friendly golden retriever with red collar",
  "microchipNumber": "123456789012345",
  "location": "Central Park, New York, NY",
  "locationRadius": 5,
  "lastSeenDate": "2025-11-21",
  "email": "john@example.com",
  "phone": "+1-555-0101",
  "photoUrl": "https://example.com/photos/max.jpg",
  "status": "MISSING",
  "managementPassword": "847362"
}
```

**Notes**: 
- The `managementPassword` field is **only included in the POST response**. It will **not** appear in GET responses.
- Optional fields (breed, description, locationRadius, microchipNumber) can be omitted from the request or set to null.
- Required fields: petName, species, gender, location, lastSeenDate, photoUrl, and at least one contact method (email OR phone).

#### Error Response Examples

**Empty required field**:
```json
{
    "error": {
        "code": "NOT_EMPTY",
        "message": "cannot be empty",
        "field": "petName"
    }
}
```

**Invalid email format**:
```json
{
    "error": {
        "code": "INVALID_FORMAT",
        "message": "invalid email format",
        "field": "email"
    }
}
```

**Invalid microchip number format** (non-numeric):
```json
{
    "error": {
        "code": "INVALID_FORMAT",
        "message": "must contain only digits",
        "field": "microchipNumber"
    }
}
```

**Field exceeds maximum length**:
```json
{
    "error": {
        "code": "TOO_LONG",
        "message": "exceeds maximum length of 100 characters",
        "field": "petName"
    }
}
```

**Missing contact information**:
```json
{
    "error": {
        "code": "MISSING_CONTACT",
        "message": "at least one contact method (email or phone) is required",
        "field": "contact"
    }
}
```

**Unknown field in request** (HTTP 400):
```json
{
    "error": {
        "code": "INVALID_FIELD",
        "message": "is not a valid field",
        "field": "unknownField"
    }
}
```

**Duplicate microchip number** (HTTP 409):
```json
{
    "error": {
        "code": "CONFLICT",
        "message": "An entity with this value already exists",
        "field": "microchipNumber"
    }
}
```

**Unexpected system error** (HTTP 500):
```json
{
    "error": {
        "code": "INTERNAL_SERVER_ERROR",
        "message": "Internal server error"
    }
}
```

### Key Entities

- **Announcement**: Represents a pet announcement (lost/found pet listing) containing:
  - Unique identifier (generated by system)
  - Pet details (name max 100 chars REQUIRED, description max 1000 chars OPTIONAL, type/species REQUIRED, breed max 100 chars OPTIONAL, gender REQUIRED)
  - Microchip number (optional, max 15 chars, numeric only, must be unique across all announcements)
  - Management password (6 digits, system-generated, returned only once in POST response, never exposed in GET endpoints)
  - Contact information (email max 255 chars and/or phone max 50 chars - at least one required)
  - Location information (location max 255 chars REQUIRED, optional radius in kilometers)
  - Last seen date (ISO 8601 date format REQUIRED)
  - Photo URL (REQUIRED, max 500 chars)
  - Timestamp of creation
  - Status indicator (automatically set to MISSING for new announcements)

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can successfully submit a valid announcement and receive confirmation in under 2 seconds
- **SC-002**: System correctly rejects 100% of announcements missing both email and phone contact information
- **SC-003**: Users receive clear, actionable error messages for validation failures with specific field identification, enabling them to correct and resubmit successfully
- **SC-004**: System maintains data integrity with no duplicate announcements created for the same microchip number
- **SC-005**: System correctly rejects 100% of duplicate microchip submissions with HTTP 409 response
- **SC-006**: System prevents XSS attacks by sanitizing all text input before storage
- **SC-007**: System rejects 100% of requests containing unknown fields (strict validation)
- **SC-008**: Management password is never exposed in GET endpoints (100% secure - only returned once in POST response)
- **SC-009**: Each announcement receives a unique 6-digit management password for future management operations
- **SC-010**: Error responses follow the simplified format `{ error: { code, message, field } }` consistently for validation failures and conflicts, and `{ error: { code, message } }` (without field) for system errors

## Assumptions

- Email and phone validators are already implemented and tested in the codebase
- The GET `/api/v1/announcements` endpoint already exists and defines the announcement response model
- Database infrastructure for persisting announcements is already in place
- Standard REST API conventions apply (JSON request/response bodies, appropriate headers)
- Input sanitization library/function is available for XSS prevention
- Request body parser can be configured for strict validation (reject unknown fields)
- Cryptographic library is available for secure password hashing (e.g., bcrypt)
- Random number generation for 6-digit passwords can produce sufficiently unique values

## Out of Scope

- Updating or deleting announcements (separate features) - management_password is generated but actual management operations will be implemented separately
- Authenticating management operations using the password (this feature only generates and returns the password)
- Password recovery or reset mechanisms (if user loses their management password)
- File uploads (images of pets) - handled by separate feature
- Real-time notifications when announcements are created
- Moderation or approval workflow before announcements go live
- Duplicate detection based on other fields (e.g., pet name + location similarity) - only microchip-based duplicate detection is implemented
- Rate limiting for announcement creation
- Testing unexpected error scenarios (database failures, system crashes) - focus on happy path and validation errors only
