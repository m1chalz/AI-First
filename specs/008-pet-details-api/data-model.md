# Data Model: Pet Details API Endpoint

**Feature**: `008-pet-details-api`  
**Date**: 2025-11-21  
**Status**: Completed

## Overview

This document defines the data model for the Pet Details API endpoint. The endpoint retrieves a single pet announcement by ID from the existing `announcement` database table. No new entities or schema changes are required—this endpoint exposes the same data model as the list endpoint but for a single resource.

## Entities

### Announcement (Pet Announcement)

Represents a single report of a missing pet retrieved by its unique identifier.

**Source**: Database table `announcement` (existing schema, no changes required)

**Lifecycle**: Read-only for this endpoint (no create/update/delete operations)

#### Fields

| Field Name | Type | Required | Constraints | Description |
|------------|------|----------|-------------|-------------|
| `id` | `string` (UUID) | ✅ Yes | Primary key, unique | Unique identifier for the announcement |
| `petName` | `string` | ✅ Yes | Non-empty | Name of the missing pet |
| `species` | `enum` | ✅ Yes | One of: `DOG`, `CAT`, `BIRD`, `RABBIT`, `OTHER` | Species/type of the pet |
| `breed` | `string \| null` | ❌ No | Optional, free-form text | Breed of the pet (null if not specified) |
| `gender` | `enum` | ✅ Yes | One of: `MALE`, `FEMALE`, `UNKNOWN` | Gender of the pet |
| `description` | `string` | ✅ Yes | Non-empty | Detailed description of the pet (appearance, behavior, etc.) |
| `location` | `string` | ✅ Yes | Non-empty | Location where the pet was last seen |
| `locationRadius` | `number \| null` | ❌ No | Optional, positive number in kilometers | Search radius around the last seen location |
| `lastSeenDate` | `string` | ✅ Yes | ISO 8601 date format: `YYYY-MM-DD` | Date when the pet was last seen |
| `email` | `string \| null` | ❌ No | Optional, valid email format | Reporter's email address |
| `phone` | `string` | ✅ Yes | Non-empty | Reporter's phone number |
| `photoUrl` | `string \| null` | ❌ No | Optional, valid URL | URL to the pet's photo |
| `status` | `enum` | ✅ Yes | One of: `ACTIVE`, `FOUND`, `CLOSED` | Current status of the announcement |

#### Validation Rules

**Application-Level Validation** (TypeScript):

1. **ID (UUID)**:
   - Format: Standard UUID format (e.g., `123e4567-e89b-12d3-a456-426614174000`)
   - Malformed UUIDs are treated as non-existent (return 404, no validation error)
   - Empty or missing ID returns 404 (handled by Express routing)

2. **Species Enum**:
   ```typescript
   type Species = 'DOG' | 'CAT' | 'BIRD' | 'RABBIT' | 'OTHER';
   ```
   - Database stores as string (VARCHAR)
   - Application validates enum values (no database CHECK constraint)
   - Invalid values should not exist in database (validated on write)

3. **Gender Enum**:
   ```typescript
   type Gender = 'MALE' | 'FEMALE' | 'UNKNOWN';
   ```
   - Database stores as string (VARCHAR)
   - Application validates enum values

4. **Status Enum**:
   ```typescript
   type Status = 'ACTIVE' | 'FOUND' | 'CLOSED';
   ```
   - Database stores as string (VARCHAR)
   - No filtering by status (endpoint returns all statuses per FR-008)

5. **Date Format**:
   - ISO 8601 date format: `YYYY-MM-DD`
   - Example: `"2025-11-21"`
   - Database stores as TEXT (SQLite) or DATE (PostgreSQL)

6. **Optional Fields**:
   - Optional fields (`breed`, `email`, `photoUrl`, `locationRadius`) are included in response with `null` value when `NULL` in database
   - Never omit optional fields from JSON response (per FR-009)

#### TypeScript Type Definition

```typescript
/**
 * Represents a pet announcement retrieved from the database.
 * All fields match the database schema for the announcement table.
 */
export interface Announcement {
    /** Unique identifier (UUID) */
    id: string;
    
    /** Name of the missing pet */
    petName: string;
    
    /** Species/type of pet */
    species: 'DOG' | 'CAT' | 'BIRD' | 'RABBIT' | 'OTHER';
    
    /** Breed of the pet (optional) */
    breed: string | null;
    
    /** Gender of the pet */
    gender: 'MALE' | 'FEMALE' | 'UNKNOWN';
    
    /** Detailed description of the pet */
    description: string;
    
    /** Location where pet was last seen */
    location: string;
    
    /** Search radius in kilometers (optional) */
    locationRadius: number | null;
    
    /** Date when pet was last seen (ISO 8601: YYYY-MM-DD) */
    lastSeenDate: string;
    
    /** Reporter's email address (optional) */
    email: string | null;
    
    /** Reporter's phone number */
    phone: string;
    
    /** URL to pet's photo (optional) */
    photoUrl: string | null;
    
    /** Current announcement status */
    status: 'ACTIVE' | 'FOUND' | 'CLOSED';
}
```

#### Database Schema (Existing)

**Table**: `announcement` (no changes required)

```sql
CREATE TABLE IF NOT EXISTS announcement (
    id TEXT PRIMARY KEY,              -- UUID string
    petName TEXT NOT NULL,
    species TEXT NOT NULL,            -- Enum stored as string
    breed TEXT,                       -- Optional (NULL allowed)
    gender TEXT NOT NULL,             -- Enum stored as string
    description TEXT NOT NULL,
    location TEXT NOT NULL,
    locationRadius REAL,              -- Optional (NULL allowed)
    lastSeenDate TEXT NOT NULL,       -- ISO 8601 date
    email TEXT,                       -- Optional (NULL allowed)
    phone TEXT NOT NULL,
    photoUrl TEXT,                    -- Optional (NULL allowed)
    status TEXT NOT NULL              -- Enum stored as string
);
```

**Notes**:
- All enum fields stored as `TEXT` (string) for flexibility
- Enum validation happens at application layer (TypeScript types)
- No database-level CHECK constraints for enums (per constitution)
- Primary key `id` ensures unique announcement IDs

#### Relationships

**None** - This endpoint retrieves a single standalone entity. No related entities or foreign key relationships are involved in this feature.

Future considerations:
- If a `user` or `reporter` table is added, `announcement` could have a foreign key to the reporter
- If a `pet` table is created, `announcement` could reference a shared pet entity
- For now, all information is self-contained in the `announcement` record

## State Transitions

The `status` field represents the lifecycle of an announcement:

```
┌──────────────┐
│    ACTIVE    │  ← Initial state (pet is lost, announcement is live)
└──────┬───────┘
       │
       ├──────→ FOUND   (pet has been found, announcement resolved)
       │
       └──────→ CLOSED  (announcement closed without finding pet)
```

**State Descriptions**:
- **ACTIVE**: Pet is still missing, announcement is actively seeking information
- **FOUND**: Pet has been found and reunited with owner
- **CLOSED**: Announcement closed for other reasons (no longer searching, duplicate, etc.)

**Important**: This endpoint returns announcements in **all states** (per FR-008). No status-based filtering is performed.

## Error Cases

### 404 Not Found

**Trigger**: Announcement with given ID does not exist OR ID is malformed.

**Response Structure**:
```json
{
    "error": {
        "code": "NOT_FOUND",
        "message": "Resource not found"
    }
}
```

**Examples**:
- Valid UUID that doesn't exist: `GET /api/v1/announcements/123e4567-e89b-12d3-a456-426614174000` → 404
- Malformed UUID: `GET /api/v1/announcements/abc-123` → 404 (treated as non-existent)
- Empty ID: `GET /api/v1/announcements/` → 404 (route not matched)

### 500 Internal Server Error

**Trigger**: Unexpected server error (database connection failure, etc.).

**Response Structure**:
```json
{
    "error": {
        "code": "INTERNAL_ERROR",
        "message": "Internal server error"
    }
}
```

**Note**: Sensitive error details (stack traces, database errors) are logged server-side but not exposed to clients.

## Example Responses

### Success Case (200)

**Request**: `GET /api/v1/announcements/123e4567-e89b-12d3-a456-426614174000`

**Response**:
```json
{
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "petName": "Max",
    "species": "DOG",
    "breed": "Golden Retriever",
    "gender": "MALE",
    "description": "Friendly golden retriever, answers to Max. Has a red collar with name tag.",
    "location": "Central Park, New York",
    "locationRadius": 5.0,
    "lastSeenDate": "2025-11-20",
    "email": "owner@example.com",
    "phone": "+1-555-0123",
    "photoUrl": "https://example.com/photos/max.jpg",
    "status": "ACTIVE"
}
```

### Success Case with Optional Fields Null (200)

**Request**: `GET /api/v1/announcements/987fcdeb-51a2-43d7-9876-543210fedcba`

**Response**:
```json
{
    "id": "987fcdeb-51a2-43d7-9876-543210fedcba",
    "petName": "Luna",
    "species": "CAT",
    "breed": null,
    "gender": "FEMALE",
    "description": "Gray tabby cat with white paws. Very shy.",
    "location": "Downtown Seattle",
    "locationRadius": null,
    "lastSeenDate": "2025-11-19",
    "email": null,
    "phone": "+1-555-9876",
    "photoUrl": null,
    "status": "FOUND"
}
```

**Note**: Optional fields (`breed`, `email`, `photoUrl`, `locationRadius`) are included with `null` value, not omitted.

### Error Case (404)

**Request**: `GET /api/v1/announcements/non-existent-id`

**Response**:
```json
{
    "error": {
        "code": "NOT_FOUND",
        "message": "Resource not found"
    }
}
```

## Implementation Notes

### Repository Layer (`announcement-repository.ts`)

```typescript
/**
 * Retrieves a single announcement by its unique identifier.
 * 
 * @param id - UUID string identifying the announcement
 * @returns Announcement object if found, null otherwise
 */
async findById(id: string): Promise<Announcement | null> {
    return this.db<Announcement>('announcement')
        .where('id', id)
        .first(); // Returns single object or undefined (coalesced to null)
}
```

### Service Layer (`announcement-service.ts`)

```typescript
/**
 * Retrieves announcement by ID.
 * 
 * @param id - Announcement UUID
 * @returns Announcement if found, null if not found
 */
export async function getAnnouncementById(id: string): Promise<Announcement | null> {
    return announcementRepository.findById(id);
}
```

### Route Handler (`announcement-routes.ts`)

```typescript
/**
 * GET /api/v1/announcements/:id
 * Returns a single announcement by ID.
 * 
 * Path parameters:
 * - id: Announcement UUID
 * 
 * Responses:
 * - 200: Success - Returns Announcement object
 * - 404: Not found - ID does not exist or is malformed
 * - 500: Internal error - Server/database error
 */
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

## Testing Considerations

### Unit Test Scenarios (Service Layer)

1. ✅ **should return announcement when ID exists** (fake repository returns mock announcement)
2. ✅ **should return null when ID does not exist** (fake repository returns null)
3. ✅ **should return announcement with null optional fields** (fake repository returns announcement with nulls)

### Integration Test Scenarios (API Endpoint)

1. ✅ **should return 200 and announcement when ID exists** (seed test database, verify response body)
2. ✅ **should return 404 when ID does not exist** (query non-existent UUID)
3. ✅ **should return 404 when UUID is malformed** (query with "abc-123")
4. ✅ **should include optional fields with null values** (verify JSON includes null fields)
5. ✅ **should return announcements regardless of status** (query ACTIVE, FOUND, CLOSED)

### Coverage Requirements

- **Service Layer**: 80% line + branch coverage for `getAnnouncementById`
- **API Route**: 80% coverage for GET `/api/v1/announcements/:id` endpoint
- All tests follow Given-When-Then structure with descriptive names

## Migration Strategy

**No database migrations required**. The existing `announcement` table schema fully supports this endpoint without any schema changes.

## Future Enhancements

Potential future improvements (out of scope for this feature):

1. **Caching**: Add Redis/in-memory cache for frequently accessed announcements
2. **Field Filtering**: Add query parameter to return only specific fields (e.g., `?fields=id,petName,status`)
3. **Related Resources**: Add links to related resources (e.g., reporter profile, pet history)
4. **ETag Support**: Add HTTP ETag header for conditional requests (304 Not Modified)
5. **Soft Deletes**: Add `deletedAt` field to support soft deletion instead of hard deletion
6. **Audit Trail**: Track view count or last accessed timestamp for analytics

These enhancements are not required for the initial implementation and can be considered in future iterations.

