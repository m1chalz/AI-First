# Feature Specification: Lost Pets API Endpoint

**Feature Branch**: `006-pets-api`  
**Created**: 2025-11-19  
**Status**: Draft  
**Input**: User description: "Potrzebujemy endpointu backendowego który będzie zwracał ogłoszenia z zaginionymi zwierzętami"

## Clarifications

### Session 2025-11-19

- Q: What are the valid values for the announcement "status" field? → A: ACTIVE, FOUND, CLOSED (announcement lifecycle states)
- Q: What should the error response structure and HTTP status code be for database failures? → A: HTTP 500 with `{ error: { code: "DATABASE_ERROR", message: "Service temporarily unavailable" } }`
- Q: How should missing optional fields (like photoUrl) be represented in the JSON response? → A: Include field with `null` value (e.g., `"photoUrl": null`)
- Q: How should the system handle malformed requests or unexpected query parameters? → A: Ignore unexpected parameters, process request normally (return HTTP 200)
- Q: What validation should be applied to email and phone fields? → A: Basic format validation (simple email regex for optional email field, phone contains digits)
- Q: Should this endpoint require authentication/authorization? → A: Public endpoint, no authentication required (completely open)
- Q: What exact date format should be used for the lastSeenDate field? → A: ISO 8601 date only (e.g., "2025-11-19")
- Q: Should species be a free-form text field or a constrained enum? → A: Constrained enum (e.g., DOG, CAT, BIRD, RABBIT, OTHER)
- Q: What are the valid values for the gender field? → A: Constrained enum (MALE, FEMALE, UNKNOWN)
- Q: Should the announcements be returned in any specific order? → A: No specific ordering (database default/insertion order)
- Q: What should the database table be named? → A: `announcement` (singular form)

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View All Lost Pet Announcements (Priority: P1)

As a mobile/web application, I need to retrieve all lost pet announcements from the backend so that users can see which pets are currently missing in their area.

**Why this priority**: This is the core functionality - without the ability to retrieve announcements, the feature cannot deliver any value. All other features depend on this working.

**Independent Test**: Can be fully tested by making a GET request to the endpoint and verifying the response structure and data. Delivers immediate value by allowing users to see lost pet information.

**Acceptance Scenarios**:

1. **Given** the database contains lost pet announcements, **When** a client requests GET /api/v1/announcements, **Then** the system returns HTTP 200 with a JSON response containing all announcements in the data array
2. **Given** the database is empty (no announcements), **When** a client requests GET /api/v1/announcements, **Then** the system returns HTTP 200 with an empty data array
3. **Given** a valid request is made, **When** the system retrieves announcements, **Then** each announcement includes all required fields (id, petName, species, breed, gender, description, location, locationRadius, lastSeenDate, email, phone, photoUrl, status)

---

### Edge Cases

- **Database Connection Failure**: System returns HTTP 500 with structured error response containing code "DATABASE_ERROR" and message "Service temporarily unavailable"
- **Unexpected Query Parameters**: System ignores any unexpected or malformed query parameters and processes the request normally, returning HTTP 200
- **Missing Optional Fields**: Optional fields (photoUrl, breed, email, locationRadius) are included in response with `null` value when not present
- **Concurrent Requests**: System handles multiple simultaneous GET requests normally; standard database read isolation applies with no special concurrency control required in this read-only endpoint

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST expose a GET endpoint at `/api/v1/announcements` that returns lost pet announcements
- **FR-002**: System MUST return HTTP status code 200 for all successful requests, including when no announcements exist
- **FR-003**: System MUST return response in JSON format with structure: `{ data: [...] }`
- **FR-004**: System MUST return an empty array in the data field when no announcements exist
- **FR-005**: Each announcement MUST include a unique identifier as a UUID string
- **FR-006**: Each announcement MUST include: petName (pet's name), species (species/type of pet, must be one of: DOG, CAT, BIRD, RABBIT, OTHER), breed (breed of the pet as free-form text, optional), gender (gender of the pet, must be one of: MALE, FEMALE, UNKNOWN), description (detailed description of the pet), location (where the pet was last seen), locationRadius (search radius around location in kilometers, optional), lastSeenDate (when the pet was last seen in ISO 8601 date format: "YYYY-MM-DD"), email (reporter's email address, optional), phone (reporter's phone number), photoUrl (optional URL to pet's photo), and status (current status of the announcement: ACTIVE, FOUND, or CLOSED)
- **FR-007**: System MUST persist announcements in a database table named `announcement`
- **FR-008**: System MUST provide seed data with example announcements for testing and development
- **FR-009**: System MUST NOT implement filtering, search, or pagination in this initial phase
- **FR-010**: System MUST handle database errors gracefully and return HTTP 500 with structured error response: `{ error: { code: "DATABASE_ERROR", message: "Service temporarily unavailable" } }`
- **FR-011**: System MUST include optional fields (photoUrl, breed, email, locationRadius) in response with `null` value when not present in the database
- **FR-012**: System MUST ignore unexpected or malformed query parameters and process requests normally
- **FR-013**: System MUST validate email using basic email format validation (simple regex pattern) when present and phone must contain digits
- **FR-014**: System MUST allow public access to the endpoint without requiring authentication or authorization
- **FR-015**: System MUST validate species field to be one of the allowed enum values: DOG, CAT, BIRD, RABBIT, OTHER
- **FR-016**: System MUST validate gender field to be one of the allowed enum values: MALE, FEMALE, UNKNOWN
- **FR-017**: System MUST return announcements in database default order with no specific sorting applied

### Key Entities

- **Lost Pet Announcement**: Represents a report of a missing pet stored in the `announcement` database table. Includes identification (UUID), pet details (name, species constrained to DOG/CAT/BIRD/RABBIT/OTHER, optional breed as free-form text, gender constrained to MALE/FEMALE/UNKNOWN, description), location information (where last seen with optional radius in kilometers), temporal information (when last seen in ISO 8601 date format: "YYYY-MM-DD"), contact information for the reporter (optional email validated by basic regex pattern when present, phone must contain digits), optional photo reference (null when absent), and current status of the announcement (ACTIVE: pet still missing, FOUND: pet has been located, CLOSED: announcement no longer active).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Clients can retrieve all lost pet announcements in under 2 seconds for datasets up to 1000 records
- **SC-002**: System returns correct HTTP 200 status code in 100% of successful requests
- **SC-003**: System returns valid JSON response structure in 100% of requests
- **SC-004**: Empty database scenario returns empty array with HTTP 200 in 100% of cases
- **SC-005**: Each announcement includes all required fields with valid data types (UUID for id, ISO 8601 date format for lastSeenDate: "YYYY-MM-DD", etc.)
- **SC-006**: Seed data successfully populates the database with at least 5-10 example announcements for testing purposes
