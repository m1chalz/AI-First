# Feature Specification: Create Announcement Endpoint

**Feature Branch**: `009-create-announcement`  
**Created**: 2025-11-20  
**Status**: Draft  
**Input**: User description: "Potrzebujemy endpointu backendowego który będzie tworzył nowe ogłoszenie"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Submit New Announcement (Priority: P1)

A user wants to post a new pet announcement (lost/found pet) by submitting announcement details including contact information to the system.

**Why this priority**: Core functionality - without the ability to create announcements, the platform has no content. This is the most critical user action.

**Independent Test**: Can be fully tested by submitting a POST request with valid announcement data and verifying the announcement is created and returned with HTTP 201 status.

**Acceptance Scenarios**:

1. **Given** a user has valid announcement details with email contact, **When** they submit the announcement, **Then** the system creates the announcement and returns it with HTTP 201 status
2. **Given** a user has valid announcement details with phone contact, **When** they submit the announcement, **Then** the system creates the announcement and returns it with HTTP 201 status
3. **Given** a user has valid announcement details with both email and phone, **When** they submit the announcement, **Then** the system creates the announcement and returns it with HTTP 201 status

---

### User Story 2 - Receive Clear Validation Errors (Priority: P1)

A user submits an announcement with invalid or missing required fields and receives clear, actionable error messages explaining what needs to be corrected.

**Why this priority**: Essential for user experience - users need to understand what went wrong and how to fix it. Poor error handling leads to frustration and abandoned submissions.

**Independent Test**: Can be fully tested by submitting invalid data (missing fields, wrong formats) and verifying that HTTP 400 is returned with detailed error messages in the specified format.

**Acceptance Scenarios**:

1. **Given** a user submits an announcement without any contact information (no email, no phone), **When** the system validates the request, **Then** it returns HTTP 400 with error details indicating at least one contact method is required
2. **Given** a user submits an announcement with an invalid email format, **When** the system validates the request, **Then** it returns HTTP 400 with error details specifying the email format is invalid
3. **Given** a user submits an announcement with an invalid phone format, **When** the system validates the request, **Then** it returns HTTP 400 with error details specifying the phone format is invalid
4. **Given** a user submits an announcement with multiple validation errors, **When** the system validates the request, **Then** it returns HTTP 400 with all validation errors listed in the details array

---

### Edge Cases

- What happens when a user submits an announcement with only whitespace in required text fields?
- How does the system handle extremely long input values (potential DoS attack)?
- What happens when a user submits both email and phone, but both are invalid?
- How does the system handle special characters or encoded content in announcement fields?
- What happens if the announcement data contains fields not defined in the model?
- How does the system handle concurrent requests trying to create identical announcements?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST accept POST requests to `/api/v1/announcements` endpoint
- **FR-002**: System MUST validate that each announcement has at least one contact method (email OR phone OR both)
- **FR-003**: System MUST validate email format using existing email validator when email is provided
- **FR-004**: System MUST validate phone format using existing phone validator when phone is provided
- **FR-005**: System MUST return HTTP 201 status code when announcement is successfully created
- **FR-006**: System MUST return HTTP 400 status code when validation fails
- **FR-007**: System MUST return the newly created announcement in the response body using the same model as GET `/api/v1/announcements` endpoint
- **FR-008**: System MUST validate all required announcement fields and reject requests missing required data
- **FR-009**: System MUST return structured error responses in the format: `{ error: { code, message, details: [{ field, code, message }] } }`
- **FR-010**: System MUST set error code to "INVALID_PAYLOAD" for validation failures
- **FR-011**: System MUST include field-level error details with field name, error code, and error message for each validation failure
- **FR-012**: System MUST persist successfully created announcements to the database
- **FR-013**: System MUST assign a unique identifier to each newly created announcement

### Key Entities

- **Announcement**: Represents a pet announcement (lost/found pet listing) containing:
  - Unique identifier (generated by system)
  - Pet details (name, description, type, breed, etc.)
  - Contact information (email and/or phone number)
  - Location information
  - Timestamp of creation
  - Status indicator

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can successfully submit a valid announcement and receive confirmation in under 2 seconds
- **SC-002**: System correctly rejects 100% of announcements missing both email and phone contact information
- **SC-003**: Users receive clear, actionable error messages for all validation failures, enabling them to correct and resubmit successfully
- **SC-004**: System maintains data integrity with no duplicate or corrupted announcements created
- **SC-005**: Error responses follow the specified format consistently for all validation failures

## Assumptions

- Email and phone validators are already implemented and tested in the codebase
- The GET `/api/v1/announcements` endpoint already exists and defines the announcement response model
- Database infrastructure for persisting announcements is already in place
- Authentication/authorization for creating announcements is handled by existing middleware (or not required for MVP)
- Standard REST API conventions apply (JSON request/response bodies, appropriate headers)

## Out of Scope

- Updating or deleting announcements (separate features)
- File uploads (images of pets) - handled by separate feature
- Real-time notifications when announcements are created
- Moderation or approval workflow before announcements go live
- Duplicate detection logic (e.g., preventing multiple identical submissions)
- Rate limiting for announcement creation
