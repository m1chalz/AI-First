# Research: Pet Details API Endpoint

**Feature**: `008-pet-details-api`  
**Date**: 2025-11-21  
**Status**: Completed

## Overview

This document consolidates research findings for implementing the `GET /api/v1/announcements/:id` endpoint. All clarifications from the feature specification have been resolved, and this research focuses on technical implementation patterns, best practices, and architectural decisions.

## Research Questions & Findings

### 1. Database Query Strategy for Single Record Retrieval

**Question**: What is the most efficient way to query a single announcement by ID using Knex?

**Decision**: Use Knex's `.where('id', id).first()` pattern for single record retrieval.

**Rationale**:
- `.first()` returns a single object (not an array), making the API cleaner
- Knex optimizes `.first()` to `LIMIT 1` in SQL, ensuring efficient query execution
- Pattern is consistent with REST best practices (single resource endpoint returns single object)
- No need for additional indexing—primary key lookup is already optimized

**Implementation Pattern** (in `announcement-repository.ts`):
```typescript
async findById(id: string): Promise<Announcement | null> {
    return this.db<Announcement>('announcement')
        .where('id', id)
        .first();
}
```

**Alternatives Considered**:
- `.where('id', id).select('*')` - Returns array, requires `[0]` access, less semantic
- Raw SQL with prepared statements - Loses Knex type safety and query builder benefits

**Performance**: Primary key lookup is O(1) with B-tree index, well under the 500ms requirement.

---

### 2. Error Handling & HTTP Status Codes

**Question**: What error handling patterns should be used for REST API endpoints in Express?

**Decision**: Use structured error responses with consistent format:
```typescript
{
    error: {
        code: "NOT_FOUND",
        message: "Resource not found"
    }
}
```

**Rationale**:
- Consistent error format across all endpoints improves API predictability
- Machine-readable `code` field enables client-side error handling logic
- Human-readable `message` field helps with debugging and logging
- Follows REST API best practices (RFC 7807 Problem Details)

**HTTP Status Code Strategy**:
- **200**: Announcement found and returned successfully
- **404**: Announcement not found OR malformed UUID (treat malformed ID as non-existent per spec clarification)
- **500**: Unexpected server error (database connection failure, etc.)

**Implementation Pattern**:
```typescript
// Route handler
router.get('/announcements/:id', async (req, res, next) => {
    try {
        const announcement = await announcementService.getAnnouncementById(req.params.id);
        
        if (!announcement) {
            return res.status(404).json({
                error: {
                    code: 'NOT_FOUND',
                    message: 'Resource not found'
                }
            });
        }
        
        res.status(200).json(announcement);
    } catch (error) {
        next(error); // Delegate to error handling middleware
    }
});
```

**Alternatives Considered**:
- Throwing custom exceptions (e.g., `NotFoundException`) - More complex, unnecessary for simple 404 case
- Plain string error messages - Less structured, harder to parse programmatically

---

### 3. UUID Validation Strategy

**Question**: Should UUID validation happen at the application layer or rely on database constraints?

**Decision**: Treat malformed UUIDs as non-existent (return 404) without explicit validation.

**Rationale**:
- Feature spec explicitly states: "malformed UUIDs are treated as non-existent and return 404"
- Database query with malformed UUID will return `null` (no match), triggering 404 automatically
- No need for additional validation logic—simpler implementation, fewer edge cases
- Consistent behavior: both "valid UUID that doesn't exist" and "malformed UUID" return same 404 response

**Implementation Pattern**:
```typescript
// No explicit UUID validation needed
// Database query returns null for both non-existent and malformed IDs
const announcement = await repository.findById(id);
if (!announcement) {
    return { error: { code: 'NOT_FOUND', message: 'Resource not found' } };
}
```

**Alternatives Considered**:
- Regex validation for UUID format - Adds complexity, spec says treat malformed as 404 anyway
- Database-level CHECK constraint - Unnecessary, primary key constraint already handles valid IDs
- Third-party UUID validation library - Overkill for simple "return 404" requirement

---

### 4. Handling Optional Fields (null values)

**Question**: How should optional fields be represented in JSON responses when database value is `NULL`?

**Decision**: Include optional fields with `null` value in JSON response (per spec requirement FR-009).

**Rationale**:
- Feature spec explicitly requires: "System MUST include optional fields (photoUrl, breed, email, locationRadius) in response with `null` value when not present in the database"
- Explicit `null` values make API contract clear (field exists but has no value)
- Prevents client-side confusion between "field missing" vs "field is null"
- JSON serialization in Node.js/Express handles `null` values automatically

**Implementation Pattern**:
```typescript
// Database columns allow NULL for optional fields
// Knex returns null for NULL columns automatically
// Express res.json() serializes null values as JSON null

// Example response with optional fields:
{
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "petName": "Max",
    "species": "DOG",
    "breed": null,          // Optional, NULL in database
    "email": null,          // Optional, NULL in database
    "photoUrl": null,       // Optional, NULL in database
    "locationRadius": null, // Optional, NULL in database
    ...
}
```

**Alternatives Considered**:
- Omit fields with `null` values - Violates spec requirement FR-009
- Use empty strings for `null` text fields - Semantically incorrect (empty ≠ null)

---

### 5. Test-Driven Development Strategy

**Question**: What is the optimal TDD workflow for this backend endpoint?

**Decision**: Follow Red-Green-Refactor cycle with unit tests (service layer) first, then integration tests (API endpoint).

**TDD Workflow**:

**Phase 1: Unit Tests (Service Layer)**
1. **RED**: Write failing test for `getAnnouncementById` service function
   - Test case: "should return announcement when ID exists"
   - Use fake repository returning mock announcement
   - Assert service returns correct announcement object

2. **GREEN**: Implement minimal service function
   - Call `repository.findById(id)`
   - Return result directly

3. **RED**: Write failing test for non-existent ID
   - Test case: "should return null when ID does not exist"
   - Fake repository returns `null`
   - Assert service returns `null`

4. **GREEN**: Service already handles this (passes through repository result)

5. **REFACTOR**: Extract any reusable validation/formatting logic

**Phase 2: Integration Tests (API Endpoint)**
1. **RED**: Write failing test for HTTP 200 success case
   - Test case: "should return 200 and announcement when ID exists"
   - Seed test database with mock announcement
   - Use SuperTest to make GET request
   - Assert status 200 and response body matches announcement

2. **GREEN**: Implement route handler
   - Wire route to service
   - Return 200 with announcement object

3. **RED**: Write failing test for HTTP 404 case
   - Test case: "should return 404 when ID does not exist"
   - Use non-existent UUID
   - Assert status 404 and error structure

4. **GREEN**: Add null check and 404 response

5. **RED**: Write failing test for malformed UUID
   - Test case: "should return 404 when UUID is malformed"
   - Use malformed ID like "abc-123"
   - Assert status 404 (same as non-existent)

6. **GREEN**: Already handled (database returns null for malformed ID)

7. **REFACTOR**: Extract error response structure to reusable function

**Testing Tools**:
- **Vitest**: Fast test runner with native TypeScript support
- **SuperTest**: HTTP assertion library for integration tests
- **Fake Repository Pattern**: In-memory fake for unit tests (no database dependency)

**Coverage Requirements**:
- Unit tests: 80% coverage for `announcementService.getAnnouncementById`
- Integration tests: 80% coverage for GET `/api/v1/announcements/:id` route
- All tests follow Given-When-Then structure with descriptive names

**Rationale**:
- Unit tests first ensure business logic correctness in isolation
- Fake repositories make unit tests fast and deterministic
- Integration tests verify full HTTP request/response cycle
- TDD ensures tests exist before implementation (no "test later" technical debt)

**Alternatives Considered**:
- Integration tests first - Slower feedback loop, harder to isolate failures
- Mock database with mocking library - More complex, less readable than fake repositories
- Skip unit tests - Violates constitution 80% coverage requirement for services

---

### 6. Existing Database Schema

**Question**: Does the existing `announcement` table support this endpoint without schema changes?

**Decision**: Use existing `announcement` table schema as-is (no migrations needed).

**Findings**:
- Existing table already has all required fields (based on list endpoint implementation)
- Primary key `id` is UUID string (supports unique identifier requirement)
- Optional fields already allow `NULL` (supports FR-009)
- Enum fields (species, gender, status) stored as strings (application-level validation)

**Schema Reference** (based on list endpoint model):
```sql
CREATE TABLE announcement (
    id TEXT PRIMARY KEY,              -- UUID string
    petName TEXT NOT NULL,
    species TEXT NOT NULL,            -- Enum: DOG, CAT, BIRD, RABBIT, OTHER
    breed TEXT,                       -- Optional
    gender TEXT NOT NULL,             -- Enum: MALE, FEMALE, UNKNOWN
    description TEXT NOT NULL,
    location TEXT NOT NULL,
    locationRadius REAL,              -- Optional, in kilometers
    lastSeenDate TEXT NOT NULL,       -- ISO 8601: YYYY-MM-DD
    email TEXT,                       -- Optional
    phone TEXT NOT NULL,
    photoUrl TEXT,                    -- Optional
    status TEXT NOT NULL              -- Enum: ACTIVE, FOUND, CLOSED
);
```

**No Migrations Required**: All fields already exist and support required data types.

**Rationale**:
- Reusing existing schema reduces implementation scope
- Consistent with list endpoint data model
- No breaking changes to existing API or database

---

## Summary of Key Decisions

| Decision Area | Choice | Rationale |
|--------------|--------|-----------|
| **Database Query** | Knex `.where().first()` | Efficient, type-safe, returns single object |
| **Error Handling** | Structured JSON with `code` and `message` | Consistent, machine-readable, REST best practice |
| **UUID Validation** | No explicit validation (treat malformed as 404) | Simpler, matches spec requirement |
| **Optional Fields** | Include with `null` value | Per spec FR-009, explicit contract |
| **TDD Workflow** | Red-Green-Refactor (unit → integration) | Fast feedback, isolated failures, 80% coverage |
| **Schema Changes** | None (use existing table) | Reuse existing model, no migrations |

## Open Questions

None. All clarifications resolved in feature specification.

## Next Steps

Proceed to Phase 1: Generate data model, API contract (OpenAPI), and quickstart guide.

