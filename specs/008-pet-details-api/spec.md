# Feature Specification: Pet Details Endpoint

**Feature Branch**: `008-pet-details-api`  
**Created**: 2025-11-20  
**Status**: Draft  
**Input**: User description: "Endpoint backendowy GET /api/v1/announcements/:id zwracający szczegóły ogłoszenia o podanym ID (200 jeśli istnieje, 404 jeśli nie ma)"

## Clarifications

### Session 2025-11-20

- Q: What should happen when someone requests an announcement with a malformed UUID (e.g., "abc-123" instead of proper UUID format)? → A: Return 404 (treat it as non-existent)
- Q: Given the 500ms response time requirement, should this endpoint implement caching for frequently accessed announcements? → A: No caching requirement (simple direct database query)
- Q: Should this endpoint return announcements regardless of their status (ACTIVE, FOUND, CLOSED), or should certain statuses be filtered out? → A: Return all announcements regardless of status
- Q: What should the error message text be for the 404 response when an announcement is not found? → A: Resource not found

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Retrieve Single Pet Announcement by ID (Priority: P1)

As a mobile/web application, I need to retrieve a specific lost pet announcement by its unique identifier so that users can view detailed information about a single missing pet.

**Why this priority**: This is the core functionality that enables viewing individual pet details. Without this, users cannot access specific announcement information, making the detail view unusable. This is a fundamental CRUD operation that complements the list endpoint.

**Independent Test**: Can be fully tested by making a GET request to the endpoint with a valid pet ID and verifying the response contains the complete pet announcement data. Delivers immediate value by allowing users to see detailed information about a specific lost pet.

**Acceptance Scenarios**:

1. **Given** a pet announcement with ID "123e4567-e89b-12d3-a456-426614174000" exists in the database, **When** a client requests GET /api/v1/announcements/123e4567-e89b-12d3-a456-426614174000, **Then** the system returns HTTP 200 with the pet announcement object as JSON response
2. **Given** a pet announcement does not exist for ID "non-existent-id", **When** a client requests GET /api/v1/announcements/non-existent-id, **Then** the system returns HTTP 404 with a structured error response
3. **Given** a valid pet announcement exists, **When** the system retrieves it, **Then** the response is a JSON object containing all required fields (id, petName, species, breed, gender, description, location, locationRadius, lastSeenDate, email, phone, photoUrl, status)

---

### Edge Cases

- **Non-Existent Pet ID**: System returns HTTP 404 with structured error response containing code "NOT_FOUND" and message "Resource not found"
- **Malformed UUID**: System returns HTTP 404 (treats malformed UUID as non-existent, e.g., "abc-123" returns same 404 response as a valid UUID that doesn't exist)
- **Missing Optional Fields**: Optional fields (photoUrl, breed, email, locationRadius) are included in response with `null` value when not present in the database

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST expose a GET endpoint at `/api/v1/announcements/:id` that returns a single pet announcement by its unique identifier
- **FR-002**: System MUST return HTTP status code 200 when the pet announcement exists
- **FR-003**: System MUST return HTTP status code 404 when the pet announcement does not exist
- **FR-004**: System MUST return the pet announcement object directly as JSON response (without a data wrapper)
- **FR-005**: System MUST accept UUID string format as the ID parameter (e.g., "123e4567-e89b-12d3-a456-426614174000"); malformed UUIDs are treated as non-existent and return 404
- **FR-006**: The pet announcement object MUST include: id (unique UUID string), petName (pet's name), species (species/type of pet, must be one of: DOG, CAT, BIRD, RABBIT, OTHER), breed (breed of the pet as free-form text, optional), gender (gender of the pet, must be one of: MALE, FEMALE, UNKNOWN), description (detailed description of the pet), location (where the pet was last seen), locationRadius (search radius around location in kilometers, optional), lastSeenDate (when the pet was last seen in ISO 8601 date format: "YYYY-MM-DD"), email (reporter's email address, optional), phone (reporter's phone number), photoUrl (optional URL to pet's photo), and status (current status of the announcement: ACTIVE, FOUND, or CLOSED)
- **FR-007**: System MUST retrieve the announcement from the `announcement` database table (consistent with the list endpoint)
- **FR-008**: System MUST return announcements regardless of their status (ACTIVE, FOUND, or CLOSED); no status-based filtering is performed
- **FR-009**: System MUST include optional fields (photoUrl, breed, email, locationRadius) in response with `null` value when not present in the database
- **FR-010**: System MUST allow public access to the endpoint without requiring authentication or authorization
- **FR-011**: System MUST return error response for HTTP 404 with structure: `{ error: { code: "NOT_FOUND", message: "Resource not found" } }`

### Key Entities

- **Pet Announcement**: Represents a single report of a missing pet retrieved by its unique identifier from the `announcement` database table. Includes all the same fields as the list endpoint: identification (UUID), pet details (name, species constrained to DOG/CAT/BIRD/RABBIT/OTHER, optional breed as free-form text, gender constrained to MALE/FEMALE/UNKNOWN, description), location information (where last seen with optional radius in kilometers), temporal information (when last seen in ISO 8601 date format: "YYYY-MM-DD"), contact information for the reporter (optional email, phone), optional photo reference (null when absent), and current status of the announcement (ACTIVE, FOUND, or CLOSED).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Clients can retrieve a specific pet announcement in under 500 milliseconds via direct database query (no caching layer required)
- **SC-002**: System returns correct HTTP 200 status code in 100% of successful requests for existing pets
- **SC-003**: System returns correct HTTP 404 status code in 100% of requests for non-existent pet IDs
- **SC-004**: System returns valid JSON response structure in 100% of requests
- **SC-005**: Each announcement includes all required fields with valid data types matching the list endpoint model (UUID for id, ISO 8601 date format for lastSeenDate: "YYYY-MM-DD", enum values for species and gender, etc.)
- **SC-006**: Error responses include appropriate error code and descriptive message in 100% of error cases
