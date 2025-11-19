# Research: Lost Pets API Endpoint

**Feature**: 006-pets-api  
**Date**: 2025-11-19  
**Status**: Complete

## Overview

This document captures technology decisions and architectural patterns for the Lost Pets API endpoint implementation. All research consolidates findings from the specification's clarifications and aligns with the project's Backend Architecture & Quality Standards constitution.

## Technology Decisions

### 1. Database Schema Design

**Decision**: Use SQLite with Knex migrations, designed for PostgreSQL compatibility

**Rationale**:
- **Enum values**: Store species (DOG, CAT, BIRD, RABBIT, OTHER), gender (MALE, FEMALE, UNKNOWN), and status (ACTIVE, FOUND, CLOSED) as VARCHAR without database constraints
  - **No CHECK constraints**: All enum validation happens in application layer
  - **Flexibility**: Easier to add new values without migration, simpler database schema
  - **Validation**: Application enforces allowed values and provides clear error messages
- **UUID for IDs**: Use UUID (stored as TEXT in SQLite, UUID type in PostgreSQL) for globally unique identifiers, generated via Node.js built-in `crypto.randomUUID()`
- **Date storage**: Use DATE type (stored as TEXT in SQLite, native DATE in PostgreSQL) for lastSeenDate in ISO 8601 format (YYYY-MM-DD)
- **Optional fields**: Allow NULL for breed, email, photoUrl, locationRadius per specification requirement
- **Field lengths**: Reasonable VARCHAR limits (petName: 100, location: 255, description: 1000, etc.) to prevent abuse
- **No location_radius validation**: Database accepts any integer value, application may optionally validate positive values

**Alternatives Considered**:
- **Auto-increment integer IDs**: Rejected because UUIDs provide globally unique identifiers without coordination, better for distributed systems and security
- **JSON column for pet details**: Rejected because relational columns enable future filtering/querying without JSON parsing overhead
- **CHECK constraints for enums**: Rejected for simplicity - application-layer validation provides better error messages and easier maintenance
- **Database-level radius validation**: Rejected to keep database schema simple

### 2. Email and Phone Validation

**Decision**: Consider using external validation library (validator.js) for robust validation

**Rationale**:
- **External library benefits**:
  - **validator.js**: Well-maintained, comprehensive validation (email, phone, URLs, etc.)
  - Battle-tested with edge cases handled
  - More reliable than custom regex patterns
  - Minimal dependency footprint (~5KB)
  - Example: `validator.isEmail(email)`, `validator.isMobilePhone(phone)`
- **Alternative - Custom validation**:
  - Email: Simple regex `/^[^\s@]+@[^\s@]+\.[^\s@]+$/` (basic structure check)
  - Phone: Check for digits `/\d/.test(phone)` (per spec: "phone contains digits")
- **Trade-off**: External library adds dependency but provides more robust, maintainable validation

**Recommendation**: Use validator.js for email/phone validation unless team prefers minimal dependencies

**Alternatives Considered**:
- **joi or zod schemas**: More powerful but heavier dependencies, overkill for simple field validation
- **Custom regex only**: Simpler but less robust, harder to maintain edge cases
- **No validation library**: Keep dependencies minimal, write custom validators in `/lib/validators.ts`

**Implementation Note**: Decision left to implementation phase based on team preference for dependency minimization vs. robustness

### 3. Error Handling Strategy

**Decision**: Structured error responses with appropriate HTTP status codes

**Rationale**:
- **HTTP 500 for database errors**: Per spec requirement, all database connection/query failures return `{ error: { code: "DATABASE_ERROR", message: "Service temporarily unavailable" } }`
- **HTTP 200 for all successful requests**: Even empty results return 200 with `{ data: [] }`
- **Ignore unexpected query parameters**: Per spec requirement, process request normally without validation errors
- **Centralized error handling**: Use Express error middleware to ensure consistent error response structure
- **Do NOT expose internal errors**: Stack traces and database error details logged server-side only, never sent to client

**Alternatives Considered**:
- **HTTP 204 No Content for empty results**: Rejected per spec requirement (must return 200 with empty array)
- **HTTP 400 for unexpected parameters**: Rejected per spec requirement (must ignore and process normally)
- **Detailed error messages**: Rejected for security (avoid leaking database schema or internal details)

### 4. UUID Generation

**Decision**: Use Node.js built-in `crypto.randomUUID()` method

**Rationale**:
- **Built-in method**: Available in Node.js v14.17.0+ (project uses v24 LTS)
- **No dependencies**: Avoids adding `uuid` package to dependencies (minimizes dependency footprint)
- **RFC 4122 v4 compliant**: Cryptographically secure random UUIDs
- **Simple API**: Single function call, no configuration required

**Alternatives Considered**:
- **uuid npm package**: Rejected because built-in method provides same functionality without adding dependency
- **Database-generated UUIDs**: Rejected because application-layer generation allows validating IDs before database insertion

### 5. Date Format and Storage

**Decision**: ISO 8601 date-only format (YYYY-MM-DD) stored as TEXT in SQLite

**Rationale**:
- **Per spec requirement**: "ISO 8601 date only (e.g., '2025-11-19')"
- **Date-only (no time)**: Announcements track when pet was last seen (day precision sufficient)
- **SQLite compatibility**: Store as TEXT in format "YYYY-MM-DD", easily parsed and sortable
- **PostgreSQL migration**: Convert to native DATE type in PostgreSQL (Knex handles this automatically)
- **Validation**: Ensure format matches /^\d{4}-\d{2}-\d{2}$/ and represents valid date

**Alternatives Considered**:
- **Timestamp with timezone**: Rejected because spec requires date-only format (no time component)
- **Unix timestamp**: Rejected because spec explicitly requires ISO 8601 format for API response
- **Store as JavaScript Date**: Rejected because SQLite stores as TEXT anyway, explicit format better

### 6. Response Structure and Null Handling

**Decision**: JSON response with `data` array wrapper, null for missing optional fields

**Rationale**:
- **Response wrapper**: Per spec, all responses use structure `{ data: [...] }`
  - Consistent format for success responses
  - Allows future pagination metadata (e.g., `{ data: [...], pagination: {...} }`)
- **Null for optional fields**: Per spec requirement, "include field with null value (e.g., 'photoUrl': null)"
  - Optional fields: photoUrl, breed, email, locationRadius
  - Always present in response (not omitted), value is `null` when missing
  - Simplifies client parsing (no need to check field existence, just check for null)

**Alternatives Considered**:
- **Omit optional fields from response**: Rejected per spec requirement (must include with null value)
- **Empty string for missing fields**: Rejected because null is more semantically correct for "no value"
- **Flat response array**: Rejected because wrapper structure allows future extensibility (pagination, metadata)

### 7. Database Repository Pattern

**Decision**: Separate repository layer using Knex query builder

**Rationale**:
- **Testability**: Repository interface allows using fake implementations in unit tests (no database required)
- **Knex query builder**: Type-safe queries, easier to read/maintain than raw SQL strings
- **PostgreSQL migration path**: Knex abstracts database differences (SQLite in dev, PostgreSQL in production)
- **Single Responsibility**: Repository handles database queries, service layer handles business logic
- **Interface-based**: Define `IAnnouncementRepository` interface for dependency inversion

**Alternatives Considered**:
- **Raw SQL strings**: Rejected because Knex provides type safety and database abstraction
- **ORM (TypeORM, Prisma)**: Rejected per constitution (Knex preferred for query-builder approach, avoids ORM complexity)
- **Direct database access from service**: Rejected because repository pattern improves testability and separation of concerns

### 8. Seed Data Strategy

**Decision**: Create Knex seed file with 5-10 example announcements

**Rationale**:
- **Per spec requirement**: "System MUST provide seed data with example announcements for testing and development" (FR-008)
- **Knex seed command**: Use `npm run knex:seed` to populate database
- **Idempotent**: Seed file should clear existing data and insert fresh test data
- **Variety**: Include all species types, mix of statuses, optional fields present/null
- **Realistic data**: Use plausible pet names, locations, descriptions for testing

**Alternatives Considered**:
- **Manual SQL INSERT statements**: Rejected because Knex seed files are more maintainable and version-controlled
- **Generated test data**: Rejected because realistic, curated seed data is more useful for development and demos

## Best Practices Applied

### Clean Code Principles

1. **Small, focused functions**:
   - Service functions: `getAllAnnouncements()` (single responsibility)
   - Repository methods: `findAll()`, `findById()` (clear, specific operations)
   - Validators: `isValidEmail()`, `isValidPhone()` (pure, testable)

2. **Descriptive naming**:
   - Use full words: `announcement` not `ann`, `repository` not `repo`
   - Verb-noun for functions: `getAllAnnouncements`, `createAnnouncement`
   - Avoid abbreviations except well-known ones: `id`, `db`, `api`

3. **Maximum 3 nesting levels**:
   - Use early returns to reduce nesting
   - Extract complex conditionals into well-named functions
   - Avoid callback pyramids (use async/await)

4. **DRY principle**:
   - Extract validation logic to `/lib/validators.ts` (reusable across features)
   - Share error response formatting in error handler middleware
   - Reuse database connection configuration

### TDD Workflow

1. **RED**: Write failing tests first
   - Integration test: `GET /api/v1/announcements` returns 200 with empty array (before implementation)
   - Unit test: `getAllAnnouncements()` returns empty array when repository has no data
   - Unit test: Email validator returns false for invalid email

2. **GREEN**: Write minimal code to pass tests
   - Implement repository `findAll()` method with Knex query
   - Implement service `getAllAnnouncements()` calling repository
   - Implement route handler returning service result

3. **REFACTOR**: Improve code quality
   - Extract error handling to centralized middleware
   - Extract validation logic to reusable utilities
   - Add JSDoc documentation
   - Apply Clean Code principles (naming, nesting, DRY)

### Test Coverage Strategy

**Unit Tests** (80% target):
- **Service layer** (`/src/services/__test__/announcement-service.test.ts`):
  - Test `getAllAnnouncements()` with fake repository
  - Scenarios: empty database, data exists, database error
  - Use Given-When-Then structure
  
- **Validation utilities** (`/src/lib/__test__/validators.test.ts`):
  - Test `isValidEmail()`: valid emails, invalid formats, edge cases (no @, no domain, etc.)
  - Test `isValidPhone()`: valid formats, no digits, empty string
  - Each validator has 5+ test cases covering happy path and edge cases

**Integration Tests** (80% target):
- **API endpoints** (`/src/__test__/announcements.test.ts`):
  - Test `GET /api/v1/announcements`: 200 with data, 200 empty array, 500 database error
  - Test query parameter handling: ignore unexpected parameters
  - Test response structure: `{ data: [...] }` with correct fields
  - Test null handling: optional fields present with null value
  - Use SuperTest for HTTP assertions
  - Use test database (separate from development database)

## Security Considerations

1. **Public endpoint (no authentication)**:
   - Per spec requirement, endpoint is completely open
   - Rate limiting SHOULD be added in future (not in this phase)
   - No sensitive data in announcements (email/phone are public by design)

2. **SQL injection prevention**:
   - Knex query builder uses parameterized queries automatically
   - Never concatenate user input into SQL strings

3. **Input validation**:
   - Validate email/phone formats (basic validation)
   - Database constraints enforce enum values (species, gender, status)
   - Field length limits prevent DOS attacks via large payloads

4. **Error information disclosure**:
   - Generic error messages for clients ("Service temporarily unavailable")
   - Detailed errors logged server-side only
   - No stack traces or database details in API responses

## Performance Considerations

1. **Query performance**:
   - No indexes in initial phase (dataset small: 5-10 announcements)
   - Future optimization: Add index on `status` column when filtering added
   - Target: <2 seconds response time for up to 1000 records (per spec SC-001)

2. **No pagination in this phase**:
   - Per spec requirement (FR-009), no pagination implemented initially
   - Response wrapper structure (`{ data: [...] }`) allows adding pagination metadata later
   - Future: Add `limit` and `offset` query parameters

## Implementation Order

1. **Database schema** (migration + seed):
   - Create `announcement` table migration
   - Define CHECK constraints for enums
   - Create seed file with example data

2. **Repository layer**:
   - Define `IAnnouncementRepository` interface
   - Implement `AnnouncementRepository` with Knex
   - Write unit tests with in-memory SQLite database

3. **Service layer**:
   - Implement `getAllAnnouncements()` service function
   - Write unit tests with fake repository
   - Add JSDoc documentation

4. **Validation utilities**:
   - Implement `isValidEmail()` and `isValidPhone()`
   - Write unit tests for validators
   - Add JSDoc documentation

5. **Route handler**:
   - Implement `GET /api/v1/announcements` route
   - Register router in `app.ts`
   - Write integration tests with SuperTest

6. **Error handling**:
   - Ensure centralized error middleware handles database errors
   - Return structured error response for 500 errors
   - Log errors with request context

## Migration Path to PostgreSQL

When migrating from SQLite to PostgreSQL:

1. **Enum types**: Convert VARCHAR CHECK constraints to native PostgreSQL ENUM types
2. **UUID type**: Change TEXT to native UUID type
3. **Date type**: SQLite TEXT → PostgreSQL DATE (Knex handles automatically)
4. **Indexes**: Add indexes on frequently queried columns (status, species, etc.)
5. **Connection pooling**: Configure Knex pool size for PostgreSQL production workload

Knex migration files designed to be database-agnostic (use Knex schema builder, not raw SQL).

## Open Questions

**None**. All clarifications resolved in specification (Session 2025-11-19):
- ✅ Status enum values defined (ACTIVE, FOUND, CLOSED)
- ✅ Error response structure defined (HTTP 500 with DATABASE_ERROR code)
- ✅ Null handling clarified (include field with null value)
- ✅ Unexpected parameters handling (ignore, process normally)
- ✅ Validation requirements (basic email regex, phone contains digits)
- ✅ Authentication requirement (public endpoint, no auth)
- ✅ Date format specified (ISO 8601 date-only: YYYY-MM-DD)
- ✅ Species enum values (DOG, CAT, BIRD, RABBIT, OTHER)
- ✅ Gender enum values (MALE, FEMALE, UNKNOWN)
- ✅ Ordering requirement (no specific ordering, database default)
- ✅ Table name (announcement, singular form)

## References

- Feature Specification: [spec.md](./spec.md)
- Implementation Plan: [plan.md](./plan.md)
- Constitution: Backend Architecture & Quality Standards (Principle XIII)
- Knex Documentation: https://knexjs.org/guide/migrations.html
- Node.js crypto.randomUUID(): https://nodejs.org/api/crypto.html#cryptorandomuuidoptions
- ISO 8601 Date Format: https://www.iso.org/iso-8601-date-and-time-format.html

