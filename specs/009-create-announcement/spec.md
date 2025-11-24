# Feature Specification: Create Announcement Endpoint

**Feature Branch**: `009-create-announcement`  
**Created**: 2025-11-20  
**Status**: Draft  
**Input**: User description: "Potrzebujemy endpointu backendowego który będzie tworzył nowe ogłoszenie"

## Clarifications

### Session 2025-11-24

- Q: What specific sanitization strategy should be used for text fields to prevent XSS attacks? → A: Strip all HTML tags completely - removes any `<tag>` content, keeps only plain text
- Q: Should the system validate that coordinates fall within valid geographic ranges? → A: Validate coordinate ranges (latitude: -90 to 90, longitude: -180 to 180) - reject invalid coordinates
- Q: Should the system validate the photoUrl field format? → A: Validate URL format only - ensure it's a valid URL structure (http/https protocol)
- Q: Should the system validate lastSeenDate to prevent illogical dates? → A: Reject future dates only - lastSeenDate must be today or in the past
- Q: How should the system handle management password collisions during generation? → A: Allow duplicates - accept that multiple announcements may share the same password (passwords are tied to specific announcement IDs for management operations)
- Q: What is the maximum request payload size limit? → A: Standard web limit (10 MB) with HTTP 413 response when exceeded
- Q: Should rate limiting be implemented in the endpoint code? → A: Defer to infrastructure/middleware layer (configure separately from this feature)
- Q: What logging strategy should be used for PII protection? → A: Log operation metadata with redacted PII - phone numbers show only last 3 digits (rest replaced with *), email addresses show only first letter and @domain (rest replaced with *)

- **Specification updates**: Updated data model and field requirements based on evolving product requirements:
  - Made `petName` field optional (was required)
  - Renamed `gender` field to `sex` (no length limit)
  - Changed status handling: users can now specify "MISSING" or "FOUND" status in POST request (no database default)
  - Added new optional fields: `reward` (string, unlimited length), `age` (positive integer)
  - Renamed `location` field to `locationCity` and made it optional (was required)
  - Added new required fields: `locationLatitude` and `locationLongitude` (both decimal/real type)
  - Changed all text field database types from varchar to text
  - **Removed all string length validation** - system accepts text fields of any length (validation only for format and required/optional status)

### Session 2025-11-21

- Q: Should the POST `/api/v1/announcements` endpoint require authentication? → A: Public endpoint (no authentication required, anyone can create announcements)
- Q: What status should be assigned to newly created announcements? → A: User can specify status as either MISSING or FOUND in the request body (no default value assigned by database)
- Q: How should the system handle required text fields containing only whitespace? → A: Reject whitespace-only values (treat as empty/missing, return validation error)
- Q: Should the endpoint enforce maximum length validation on text fields? → A: No length validation - all text fields accept unlimited length strings
- Q: Should validation continue after finding the first error to report all problems at once? → A: Stop at first error, return only that error (fail-fast validation)
- Q: What error response format should be used with fail-fast validation? → A: Simplified format with field-level code directly: `{ error: { code, message, field } }` (no nested details array needed)
- Q: How should unexpected errors (database failures, etc.) be handled? → A: Return HTTP 500 with generic error response `{ error: { code: "INTERNAL_SERVER_ERROR", message: "Internal server error" } }` (no field property, no internal details exposed)
- Q: Should the system include pet microchip number and prevent duplicate announcements? → A: Yes, add optional microchip_number field (numeric only, no length limit). Return HTTP 409 with format `{ error: { code: "CONFLICT", message: "An entity with this value already exists", field: "microchipNumber" } }` if duplicate exists
- Q: How should the system handle special characters or encoded content in announcement fields? → A: Sanitize all text input to prevent XSS attacks (escape/remove dangerous HTML/script tags while preserving safe special characters)
- Q: What should happen if the request data contains fields not defined in the model? → A: Reject the request with HTTP 400 (strict validation for security - unknown fields indicate potential attack or API misuse)
- Q: Should the system provide a way to manage announcements without full user authentication? → A: Yes, generate a 6-digit numeric management_password for each announcement. Return it only once in POST response, never expose it in GET endpoints
- Q: Which announcement fields are required in the POST request body? → A: Required: species, sex, lastSeenDate, photoUrl, contact (email OR phone), locationLatitude, locationLongitude. Optional: petName, breed, description, locationCity, locationRadius, microchipNumber, reward, age

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
5. **Given** a user submits a valid announcement with status "MISSING", **When** the announcement is created, **Then** the returned announcement has status set to "MISSING"
6. **Given** a user submits a valid announcement with status "FOUND", **When** the announcement is created, **Then** the returned announcement has status set to "FOUND"
7. **Given** a user submits a valid announcement, **When** the announcement is created, **Then** the response includes a 6-digit management_password that can be used to manage the announcement
8. **Given** a user creates an announcement, **When** they retrieve announcements via GET endpoint, **Then** the management_password is not included in the response

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
5. **Given** a user submits an announcement without a required field (e.g., species, photoUrl, status), **When** the system validates the request, **Then** it returns HTTP 400 with error code "NOT_EMPTY" for the missing field
6. **Given** a user submits an announcement without optional fields (e.g., description, breed), **When** the system validates the request, **Then** it accepts the announcement and creates it successfully
7. **Given** a user submits an announcement with a microchip number containing non-numeric characters, **When** the system validates the request, **Then** it returns HTTP 400 with error code "INVALID_FORMAT" for the microchipNumber field
8. **Given** a user submits an announcement with unknown fields not defined in the model, **When** the system validates the request, **Then** it returns HTTP 400 with error code "INVALID_FIELD" and the name of the unknown field
9. **Given** a user submits an announcement with HTML tags in text fields (e.g., `<script>alert('xss')</script>`), **When** the system processes the input, **Then** it strips all HTML tags and stores only the plain text content
10. **Given** a user submits an announcement with multiple validation errors, **When** the system validates the request, **Then** it returns HTTP 400 with only the first validation error in the simplified format `{ error: { code, message, field } }`
11. **Given** a user submits an announcement with an invalid status value (not "MISSING" or "FOUND"), **When** the system validates the request, **Then** it returns HTTP 400 with error code "INVALID_FORMAT" for the status field
12. **Given** a user submits an announcement with a negative or zero age value, **When** the system validates the request, **Then** it returns HTTP 400 with error code "INVALID_FORMAT" for the age field
13. **Given** a user submits an announcement with non-numeric location coordinates, **When** the system validates the request, **Then** it returns HTTP 400 with error code "INVALID_FORMAT" for the locationLatitude or locationLongitude field
14. **Given** a user submits an announcement with out-of-range coordinates (e.g., latitude 100 or longitude -200), **When** the system validates the request, **Then** it returns HTTP 400 with error code "INVALID_FORMAT" for the out-of-range field
15. **Given** a user submits an announcement with an invalid photoUrl format (e.g., not a URL or missing http/https protocol), **When** the system validates the request, **Then** it returns HTTP 400 with error code "INVALID_FORMAT" for the photoUrl field
16. **Given** a user submits an announcement with a future lastSeenDate, **When** the system validates the request, **Then** it returns HTTP 400 with error code "INVALID_FORMAT" for the lastSeenDate field
17. **Given** a user submits an announcement without optional fields (petName, locationCity, age, reward), **When** the system validates the request, **Then** it accepts the announcement and creates it successfully
18. **Given** a user submits an announcement with a request payload exceeding 10 MB, **When** the system validates the request, **Then** it returns HTTP 413 with error code "PAYLOAD_TOO_LARGE"

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

- **Required vs Optional fields**: System validates that all required fields (species, sex, lastSeenDate, photoUrl, locationLatitude, locationLongitude, status, and email OR phone) are present and non-empty. Optional fields (petName, breed, description, locationCity, locationRadius, microchipNumber, reward, age) can be omitted without error.
- **Whitespace-only fields**: System treats whitespace-only values in required text fields as empty and returns HTTP 400 validation error
- **Invalid microchip format**: System validates that microchip numbers contain only digits (numeric characters) and returns HTTP 400 with "INVALID_FORMAT" if non-numeric characters are present
- **Invalid age value**: System validates that age is a positive integer (greater than 0) when provided and returns HTTP 400 with "INVALID_FORMAT" if negative, zero, or non-integer value is provided
- **Invalid location coordinates**: System validates that locationLatitude and locationLongitude are valid decimal numbers within geographic ranges (latitude: -90 to 90, longitude: -180 to 180) and returns HTTP 400 with "INVALID_FORMAT" if non-numeric or out-of-range values are provided
- **Invalid status value**: System validates that status is either "MISSING" or "FOUND" and returns HTTP 400 with "INVALID_FORMAT" if other values are provided
- **Invalid photoUrl format**: System validates that photoUrl is a valid URL with http or https protocol and returns HTTP 400 with "INVALID_FORMAT" if malformed or using unsupported protocol
- **Invalid lastSeenDate**: System validates that lastSeenDate is in ISO 8601 format and is not a future date (must be today or in the past) and returns HTTP 400 with "INVALID_FORMAT" if date is in the future or has invalid format
- **Multiple invalid fields**: System uses fail-fast validation - returns HTTP 400 with only the first validation error encountered, not all errors at once
- **Duplicate microchip numbers**: System checks for existing announcements with the same microchip number and returns HTTP 409 if found (only when microchip number is provided)
- **Special characters and XSS prevention**: System sanitizes all text input to prevent XSS attacks by stripping all HTML tags completely (removes any `<tag>` content). Plain text with special characters (quotes, apostrophes, etc.) is preserved
- **Unknown fields in request**: System rejects requests containing fields not defined in the announcement model with HTTP 400 and error code "INVALID_FIELD" (strict validation for security)
- **Management password security**: System generates 6-digit password for each announcement (not guaranteed globally unique), returns it only in POST response (one-time exposure), stores it hashed in database, and never includes it in GET responses. Management operations use both announcement ID and password for authentication.
- **User-provided management password**: System ignores any management_password provided in POST request body (system-generated only, user cannot specify)
- **Unexpected system errors**: System returns HTTP 500 with generic error response `{ error: { code: "INTERNAL_SERVER_ERROR", message: "Internal server error" } }` without exposing internal details (database errors, crashes, etc.)
- **Large request payloads**: System enforces a maximum request body size of 10 MB to prevent DoS attacks. Returns HTTP 413 (Payload Too Large) with error response `{ error: { code: "PAYLOAD_TOO_LARGE", message: "Request payload exceeds maximum size limit" } }` when exceeded
- **Logging with PII protection**: System logs announcement creation events with redacted contact information for privacy protection. Phone numbers are redacted to show only last 3 digits (e.g., `***-***-*101`), email addresses are redacted to show only first letter and domain (e.g., `j***@example.com`). Logs include timestamp, announcement ID, HTTP status code, and validation error codes (if any) but exclude full PII, pet descriptions, and other sensitive content

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
- **FR-009**: System MUST return HTTP 413 status code when request payload exceeds maximum size limit (10 MB)
- **FR-010**: System MUST return HTTP 500 status code when unexpected errors occur (database failures, system errors, etc.)
- **FR-011**: System MUST return the newly created announcement in the response body using the same model as GET `/api/v1/announcements` endpoint, with the addition of the management_password field (only present in POST response)
- **FR-012**: System MUST validate all required announcement fields and reject requests missing required data (required fields: species, sex, lastSeenDate, photoUrl, status, locationLatitude, locationLongitude, and at least one contact method - email OR phone)
- **FR-013**: System MUST accept optional fields without validation errors when not provided (optional fields: petName, breed, description, locationCity, locationRadius, microchipNumber, reward, age)
- **FR-014**: System MUST treat required text fields containing only whitespace as empty and reject them with validation error
- **FR-015**: System MUST validate that microchip number contains only digits (numeric characters only) when provided
- **FR-016**: System MUST check if an announcement with the provided microchip number already exists (when microchip number is provided)
- **FR-017**: System MUST return structured validation error responses in the format: `{ error: { code, message, field } }` where code is the specific validation error code
- **FR-018**: System MUST return conflict error responses in the format: `{ error: { code: "CONFLICT", message: "An entity with this value already exists", field: "microchipNumber" } }` when duplicate microchip detected
- **FR-019**: System MUST return generic error responses for unexpected errors in the format: `{ error: { code: "INTERNAL_SERVER_ERROR", message: "Internal server error" } }` without field property and without exposing internal details
- **FR-020**: System MUST use fail-fast validation (stop at first error and return only that single error)
- **FR-021**: System MUST set validation error codes to specific types (e.g., "NOT_EMPTY" for required fields, "INVALID_FORMAT" for format errors, "MISSING_CONTACT" for missing email/phone, "INVALID_FIELD" for unknown fields, "PAYLOAD_TOO_LARGE" for oversized requests)
- **FR-022**: System MUST include the field name in the `field` property of validation error responses to identify which field caused the validation failure
- **FR-023**: System MUST persist successfully created announcements to the database
- **FR-024**: System MUST assign a unique identifier to each newly created announcement
- **FR-025**: System MUST accept and validate the status field from the request body, allowing only "MISSING" or "FOUND" values (no default value assigned by database)
- **FR-026**: System MUST generate a 6-digit numeric management_password for each newly created announcement (passwords are not required to be globally unique across all announcements)
- **FR-027**: System MUST return the management_password in the POST response body when creating an announcement
- **FR-028**: System MUST NOT include management_password in GET responses (list or detail views)
- **FR-029**: System MUST store management_password securely (hashed) in the database
- **FR-030**: System MUST sanitize all text input fields to prevent XSS attacks by stripping all HTML tags completely (removes any `<tag>` content, keeps only plain text)
- **FR-031**: System MUST reject requests containing fields not defined in the announcement model with HTTP 400 and error code "INVALID_FIELD"
- **FR-032**: System MUST validate that age is a positive integer (greater than 0) when provided and reject negative, zero, or non-integer values with HTTP 400 and error code "INVALID_FORMAT"
- **FR-033**: System MUST validate that locationLatitude and locationLongitude are valid decimal numbers within geographic ranges (latitude: -90 to 90, longitude: -180 to 180) and reject non-numeric or out-of-range values with HTTP 400 and error code "INVALID_FORMAT"
- **FR-034**: System MUST validate that status field contains only "MISSING" or "FOUND" values and reject other values with HTTP 400 and error code "INVALID_FORMAT"
- **FR-035**: System MUST validate that photoUrl is a valid URL with http or https protocol and reject invalid URL formats with HTTP 400 and error code "INVALID_FORMAT"
- **FR-036**: System MUST validate that lastSeenDate is in ISO 8601 format and is not a future date (must be today or in the past) and reject invalid or future dates with HTTP 400 and error code "INVALID_FORMAT"
- **FR-037**: System MUST enforce a maximum request payload size of 10 MB and return HTTP 413 (Payload Too Large) when the request body exceeds this limit
- **FR-038**: System MUST log announcement creation events with redacted PII for privacy protection - phone numbers MUST show only last 3 digits (rest replaced with *), email addresses MUST show only first letter and @domain portion (rest replaced with *), logging MUST include timestamp, announcement ID, HTTP status code, and validation error codes (if any)

#### Success Response Example

**Successful announcement creation** (HTTP 201):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "petName": "Max",
  "species": "Golden Retriever",
  "breed": "Golden Retriever",
  "sex": "MALE",
  "age": 3,
  "description": "Friendly golden retriever with red collar",
  "microchipNumber": "123456789012345",
  "locationCity": "New York",
  "locationLatitude": 40.785091,
  "locationLongitude": -73.968285,
  "locationRadius": 5,
  "lastSeenDate": "2025-11-21",
  "email": "john@example.com",
  "phone": "+1-555-0101",
  "photoUrl": "https://example.com/photos/max.jpg",
  "status": "MISSING",
  "reward": "500 USD reward for safe return",
  "managementPassword": "847362"
}
```

**Notes**: 
- The `managementPassword` field is **only included in the POST response**. It will **not** appear in GET responses.
- Optional fields (petName, breed, description, locationCity, locationRadius, microchipNumber, reward, age) can be omitted from the request or set to null.
- Required fields: species, sex, lastSeenDate, photoUrl, status, locationLatitude, locationLongitude, and at least one contact method (email OR phone).

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

**Invalid status value** (HTTP 400):
```json
{
    "error": {
        "code": "INVALID_FORMAT",
        "message": "status must be either MISSING or FOUND",
        "field": "status"
    }
}
```

**Invalid age value** (HTTP 400):
```json
{
    "error": {
        "code": "INVALID_FORMAT",
        "message": "age must be a positive integer",
        "field": "age"
    }
}
```

**Invalid location coordinate format** (HTTP 400):
```json
{
    "error": {
        "code": "INVALID_FORMAT",
        "message": "must be a valid decimal number",
        "field": "locationLatitude"
    }
}
```

**Out-of-range location coordinate** (HTTP 400):
```json
{
    "error": {
        "code": "INVALID_FORMAT",
        "message": "latitude must be between -90 and 90",
        "field": "locationLatitude"
    }
}
```

**Invalid photoUrl format** (HTTP 400):
```json
{
    "error": {
        "code": "INVALID_FORMAT",
        "message": "must be a valid URL with http or https protocol",
        "field": "photoUrl"
    }
}
```

**Future lastSeenDate** (HTTP 400):
```json
{
    "error": {
        "code": "INVALID_FORMAT",
        "message": "lastSeenDate cannot be in the future",
        "field": "lastSeenDate"
    }
}
```

**Payload too large** (HTTP 413):
```json
{
    "error": {
        "code": "PAYLOAD_TOO_LARGE",
        "message": "Request payload exceeds maximum size limit"
    }
}
```

### Key Entities

- **Announcement**: Represents a pet announcement (lost/found pet listing) containing:
  - Unique identifier (generated by system)
  - Pet details (petName OPTIONAL, description OPTIONAL, species REQUIRED, breed OPTIONAL, sex REQUIRED, age positive integer OPTIONAL)
  - Microchip number (OPTIONAL, numeric only, must be unique across all announcements)
  - Management password (6 digits, system-generated, returned only once in POST response, never exposed in GET endpoints)
  - Contact information (email and/or phone - at least one required)
  - Location information (locationCity OPTIONAL, locationLatitude REQUIRED real/float, locationLongitude REQUIRED real/float, locationRadius OPTIONAL in kilometers)
  - Last seen date (ISO 8601 date format REQUIRED)
  - Photo URL (REQUIRED)
  - Timestamp of creation
  - Status indicator (REQUIRED, must be "MISSING" or "FOUND", no default value)
  - Reward information (OPTIONAL, string describing reward offered)
  
  **Note**: All text fields are stored as text type in database with no length constraints. No length validation is performed at application level - all text fields accept unlimited length.

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
- **SC-009**: Each announcement receives a 6-digit management password for future management operations (tied to announcement ID)
- **SC-010**: Error responses follow the simplified format `{ error: { code, message, field } }` consistently for validation failures and conflicts, and `{ error: { code, message } }` (without field) for system errors
- **SC-011**: All logged data protects user privacy by redacting PII (phone numbers show only last 3 digits, emails show only first letter and @domain) while maintaining operational visibility for debugging and monitoring

## Assumptions

- Email and phone validators are already implemented and tested in the codebase
- The GET `/api/v1/announcements` endpoint already exists and defines the announcement response model
- Database infrastructure for persisting announcements is already in place
- Standard REST API conventions apply (JSON request/response bodies, appropriate headers)
- Input sanitization library/function is available for XSS prevention
- Request body parser can be configured for strict validation (reject unknown fields)
- Cryptographic library is available for secure password hashing (e.g., bcrypt)
- Random number generation for 6-digit passwords is available (passwords need not be globally unique, as they're tied to specific announcement IDs)
- Rate limiting (if needed) will be configured at infrastructure/middleware layer (API Gateway, reverse proxy, or Express middleware) independently of this endpoint implementation
- Logging infrastructure is available for recording operation events with PII redaction capabilities

## Out of Scope

- Updating or deleting announcements (separate features) - management_password is generated but actual management operations will be implemented separately
- Authenticating management operations using the password (this feature only generates and returns the password)
- Password recovery or reset mechanisms (if user loses their management password)
- File uploads (images of pets) - handled by separate feature
- Real-time notifications when announcements are created
- Moderation or approval workflow before announcements go live
- Duplicate detection based on other fields (e.g., pet name + location similarity) - only microchip-based duplicate detection is implemented
- Rate limiting implementation in endpoint code - rate limiting should be configured at infrastructure/middleware layer (API Gateway, reverse proxy, or Express middleware) separately from this feature
- Testing unexpected error scenarios (database failures, system crashes) - focus on happy path and validation errors only
