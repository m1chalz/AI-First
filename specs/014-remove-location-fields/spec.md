# Feature Specification: Remove Location Fields

**Feature Branch**: `014-remove-location-fields`  
**Created**: 2025-01-27  
**Status**: Draft  
**Input**: User description: "usuń pola location, locationCity i locationRadius z bazy danych, modeli, DTO i dokumentacji API"

## Clarifications

### Session 2025-01-27

- Q: For existing announcements with location data, should the migration drop columns directly (data lost) or log/transform data first? → A: Simply drop the columns (data is permanently lost, migration is fast and simple)
- Q: When clients send location fields, should the API treat them as unknown fields (existing INVALID_FIELD code) or use a specific deprecated field error? → A: Treat as unknown fields (existing INVALID_FIELD code via Zod strict mode, no custom handling needed)

## User Scenarios & Testing *(mandatory)*

### User Story 1 - API Consumers Cannot Send Location Fields (Priority: P1)

API consumers attempting to create or update announcements should not be able to include location-related fields (`location`, `locationCity`, `locationRadius`) in their requests. The API should reject these fields if provided, ensuring the data model is consistent and simplified.

**Why this priority**: This is the primary user-facing change that prevents invalid data from being submitted. It ensures API consumers understand the simplified data model immediately.

**Independent Test**: Can be fully tested by sending a POST request with location fields and verifying the API rejects them with appropriate error messages, delivering immediate feedback about the simplified data model.

**Acceptance Scenarios**:

1. **Given** an API consumer wants to create an announcement, **When** they include `locationCity` or `locationRadius` in the request body, **Then** the API rejects the request with `INVALID_FIELD` error code (treated as unknown fields via strict validation)
2. **Given** an API consumer sends a request with the old `location` field, **When** the request is processed, **Then** the API rejects the request with `INVALID_FIELD` error code (treated as unknown field via strict validation)
3. **Given** an API consumer sends a valid request without any location fields, **When** the request is processed, **Then** the announcement is created successfully

---

### User Story 2 - API Responses Do Not Include Location Fields (Priority: P1)

API responses for announcements should not include location-related fields (`location`, `locationCity`, `locationRadius`). Consumers should only receive location data via latitude and longitude coordinates.

**Why this priority**: This ensures API consumers receive consistent, simplified responses without deprecated fields, preventing confusion and reducing payload size.

**Independent Test**: Can be fully tested by retrieving announcements via GET endpoints and verifying that location fields are absent from all response payloads, delivering clean API responses.

**Acceptance Scenarios**:

1. **Given** an announcement exists in the system, **When** a consumer retrieves it via GET `/announcements/:id`, **Then** the response does not contain `location`, `locationCity`, or `locationRadius` fields
2. **Given** multiple announcements exist, **When** a consumer retrieves the list via GET `/announcements`, **Then** none of the announcements in the response contain location fields
3. **Given** an announcement was created with location fields in the past, **When** it is retrieved, **Then** the response does not include those fields even if they exist in the database

---

### User Story 3 - Database Schema Does Not Store Location Fields (Priority: P2)

The database schema should be updated to remove columns for `location`, `location_city`, and `location_radius`. Existing data migration should handle any historical records appropriately.

**Why this priority**: This ensures data consistency at the storage layer and prevents accidental use of deprecated fields. While less visible to end users, it's critical for long-term maintainability.

**Independent Test**: Can be fully tested by verifying database schema changes and ensuring new announcements cannot be stored with location fields, delivering a clean database structure.

**Acceptance Scenarios**:

1. **Given** the database migration is executed, **When** the schema is inspected, **Then** the `announcement` table does not contain `location`, `location_city`, or `location_radius` columns
2. **Given** existing announcements have location data, **When** the migration runs, **Then** the location columns are dropped and the data is permanently removed
3. **Given** a new announcement is created, **When** it is stored in the database, **Then** no location fields are persisted

---

### Edge Cases

- What happens when existing API clients still send location fields? (Rejected with `INVALID_FIELD` error code via strict validation mode)
- How does the system handle database rollback if migration fails? (Migration should be reversible)
- What if there are existing announcements with location data? (Migration drops columns directly, permanently removing the data)
- How are API documentation examples updated? (All examples should reflect the new schema without location fields)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST reject API requests that include `location`, `locationCity`, or `locationRadius` fields with `INVALID_FIELD` error code (via Zod strict mode validation, no custom error handling required)
- **FR-002**: System MUST exclude `location`, `locationCity`, and `locationRadius` from all API response payloads
- **FR-003**: System MUST remove `location`, `location_city`, and `location_radius` columns from the database schema via migration
- **FR-004**: System MUST update all TypeScript type definitions (DTOs, models, interfaces) to remove location field references
- **FR-005**: System MUST remove location fields from validation schemas, allowing strict mode to automatically reject them as unknown fields with `INVALID_FIELD` code
- **FR-006**: System MUST update repository layer to stop mapping location fields between database and application models
- **FR-007**: System MUST update service layer to stop processing or sanitizing location fields
- **FR-008**: System MUST update all unit and integration tests to remove location field references
- **FR-009**: System MUST update API documentation (README.md) to remove location field descriptions and examples
- **FR-010**: System MUST update database seed data to exclude location fields
- **FR-011**: Database migration MUST drop location columns directly, permanently removing any existing location data (no logging or transformation required)

### Key Entities *(include if feature involves data)*

- **Announcement**: Represents a pet announcement. After this change, location information is provided only via `locationLatitude` and `locationLongitude` coordinates. The deprecated text-based location fields (`location`, `locationCity`) and search radius (`locationRadius`) are removed.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of API requests containing location fields are rejected with clear validation errors
- **SC-002**: 100% of API responses exclude location fields from all announcement objects
- **SC-003**: Database schema migration completes successfully with zero data loss for non-location fields
- **SC-004**: All test suites pass with 80%+ coverage maintained after removing location field tests
- **SC-005**: API documentation accurately reflects the simplified data model without location field references
- **SC-006**: Type system compilation succeeds with no references to removed location fields in types, DTOs, or models

## Assumptions

- Existing API consumers can be updated to stop sending location fields (backward compatibility break is acceptable)
- Location data is sufficiently represented by latitude/longitude coordinates alone
- Historical location data in the database can be safely removed without business impact
- No external systems depend on the location fields being present in API responses

## Dependencies

- Database migration tools (Knex) must support column removal
- All tests referencing location fields must be updated or removed
- API documentation must be synchronized with code changes

## Out of Scope

- Adding new location-related fields or functionality
- Migrating location data to a different format or storage
- Updating client applications (mobile/web) that consume the API (handled separately)
- Backward compatibility layer for old API clients
