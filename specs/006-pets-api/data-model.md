# Data Model: Lost Pets API Endpoint

**Feature**: 006-pets-api  
**Date**: 2025-11-19  
**Database**: SQLite (initial), PostgreSQL (migration target)

## Overview

This document defines the database schema, entities, and relationships for the Lost Pets API feature. The schema is designed for SQLite compatibility with a clear migration path to PostgreSQL.

## Entities

### Announcement

Represents a lost pet announcement submitted by a user. Contains pet identification details, location information, contact details, and announcement status.

**Table Name**: `announcement` (singular form per spec clarification)

**Primary Key**: `id` (UUID, TEXT in SQLite, UUID in PostgreSQL)

## Database Schema

### Table: `announcement`

| Column Name      | Type             | Nullable | Default | Constraints | Description |
|------------------|------------------|----------|---------|-------------|-------------|
| `id`             | TEXT (SQLite) / UUID (PostgreSQL) | NOT NULL | - | PRIMARY KEY | Unique identifier for announcement (UUID v4) |
| `pet_name`       | VARCHAR(100)     | NOT NULL | - | - | Name of the lost pet |
| `species`        | VARCHAR(20)      | NOT NULL | - | - | Type of animal (enum values: DOG, CAT, BIRD, RABBIT, OTHER) |
| `breed`          | VARCHAR(100)     | NULL     | NULL | - | Breed of the pet (optional, free-form text) |
| `gender`         | VARCHAR(20)      | NOT NULL | - | - | Gender of the pet (enum values: MALE, FEMALE, UNKNOWN) |
| `description`    | VARCHAR(1000)    | NOT NULL | - | - | Detailed description of the pet (appearance, behavior, etc.) |
| `location`       | VARCHAR(255)     | NOT NULL | - | - | Location where pet was last seen (e.g., "Central Park, NYC") |
| `location_radius` | INTEGER         | NULL     | NULL | - | Search radius around location in kilometers (optional, no validation) |
| `last_seen_date` | TEXT (SQLite) / DATE (PostgreSQL) | NOT NULL | - | - | Date when pet was last seen (ISO 8601: YYYY-MM-DD) |
| `email`          | VARCHAR(255)     | NULL     | NULL | - | Reporter's email address (optional, basic validation) |
| `phone`          | VARCHAR(50)      | NOT NULL | - | - | Reporter's phone number (must contain digits) |
| `photo_url`      | VARCHAR(500)     | NULL     | NULL | - | URL to photo of the pet (optional) |
| `status`         | VARCHAR(20)      | NOT NULL | 'ACTIVE' | - | Current status of announcement (enum values: ACTIVE, FOUND, CLOSED) |
| `created_at`     | TIMESTAMP        | NOT NULL | CURRENT_TIMESTAMP | - | When announcement was created (system-generated) |
| `updated_at`     | TIMESTAMP        | NOT NULL | CURRENT_TIMESTAMP | - | When announcement was last updated (system-generated) |

### Field Details

#### `id` (Primary Key)
- **Format**: UUID v4 (RFC 4122 compliant)
- **Generation**: Application-layer using Node.js `crypto.randomUUID()`
- **Storage**: TEXT in SQLite (e.g., "550e8400-e29b-41d4-a716-446655440000"), UUID type in PostgreSQL
- **Purpose**: Globally unique identifier, no coordination required, secure (non-sequential)

#### `pet_name`
- **Max Length**: 100 characters
- **Required**: Yes
- **Examples**: "Fluffy", "Max", "Shadow"
- **Validation**: Non-empty string

#### `species` (Enum)
- **Allowed Values**: 
  - `DOG` - Dog
  - `CAT` - Cat
  - `BIRD` - Bird (parrots, canaries, etc.)
  - `RABBIT` - Rabbit
  - `OTHER` - Other species (ferrets, reptiles, etc.)
- **Storage**: VARCHAR (no database constraints, validation in application layer)
- **Case**: Uppercase in database, API accepts/returns uppercase
- **Rationale**: Allowed values per spec clarification (FR-015), validated by application

#### `breed`
- **Max Length**: 100 characters
- **Required**: No (optional field per spec)
- **Examples**: "Golden Retriever", "Persian Cat", "Budgerigar", null
- **Validation**: Free-form text, no constraints
- **Null Handling**: Stored as NULL in database, returned as `"breed": null` in API response

#### `gender` (Enum)
- **Allowed Values**:
  - `MALE` - Male
  - `FEMALE` - Female
  - `UNKNOWN` - Gender unknown or not specified
- **Storage**: VARCHAR (no database constraints, validation in application layer)
- **Case**: Uppercase in database, API accepts/returns uppercase
- **Rationale**: Allowed values per spec clarification (FR-016), validated by application

#### `description`
- **Max Length**: 1000 characters
- **Required**: Yes
- **Purpose**: Detailed description of pet (appearance, behavior, special markings, etc.)
- **Examples**: "White cat with black spots on left ear, very friendly, answers to 'Snowball'"
- **Validation**: Non-empty string

#### `location`
- **Max Length**: 255 characters
- **Required**: Yes
- **Format**: Free-form text (e.g., "Central Park near Bethesda Fountain", "123 Main St, Springfield, IL")
- **Purpose**: Where the pet was last seen
- **Validation**: Non-empty string
- **Note**: No geocoding in this phase (future enhancement)

#### `location_radius`
- **Type**: Integer (kilometers)
- **Required**: No (optional field per spec)
- **Constraint**: None (no database validation)
- **Examples**: 5, 10, null
- **Purpose**: Search radius around location for finding the pet
- **Null Handling**: Stored as NULL in database, returned as `"locationRadius": null` in API response
- **Note**: Application may validate positive values, but database has no constraints

#### `last_seen_date`
- **Format**: ISO 8601 date-only (YYYY-MM-DD)
- **Required**: Yes
- **Storage**: TEXT in SQLite (e.g., "2025-11-19"), DATE type in PostgreSQL
- **Examples**: "2025-11-19", "2025-10-15"
- **Validation**: Must match regex /^\d{4}-\d{2}-\d{2}$/ and represent valid date
- **Rationale**: Date-only format per spec clarification (no time component needed)

#### `email`
- **Max Length**: 255 characters
- **Required**: No (optional field per spec)
- **Format**: Basic email format (local@domain.tld)
- **Validation**: Simple regex `/^[^\s@]+@[^\s@]+\.[^\s@]+$/` per spec clarification
- **Examples**: "john@example.com", "reporter+pets@gmail.com", null
- **Null Handling**: Stored as NULL in database, returned as `"email": null` in API response

#### `phone`
- **Max Length**: 50 characters
- **Required**: Yes
- **Format**: Flexible (supports various formats: "+1-555-1234", "(555) 123-4567", "5551234567")
- **Validation**: Must contain at least one digit (regex `/\d/`) per spec clarification
- **Purpose**: Contact number for pet reporter
- **Rationale**: No strict format enforcement for international compatibility

#### `photo_url`
- **Max Length**: 500 characters
- **Required**: No (optional field per spec)
- **Format**: URL string (e.g., "https://example.com/photos/pet123.jpg")
- **Validation**: None in this phase (future: validate URL format)
- **Purpose**: Link to photo of the lost pet
- **Null Handling**: Stored as NULL in database, returned as `"photoUrl": null` in API response

#### `status` (Enum)
- **Allowed Values**:
  - `ACTIVE` - Pet still missing, announcement active
  - `FOUND` - Pet has been found and reunited with owner
  - `CLOSED` - Announcement closed (no longer relevant)
- **Default**: `ACTIVE`
- **Storage**: VARCHAR (no database constraints, validation in application layer)
- **Case**: Uppercase in database, API accepts/returns uppercase
- **Rationale**: Lifecycle states per spec clarification, validated by application

#### `created_at`
- **Type**: TIMESTAMP
- **Required**: Yes (system-generated)
- **Default**: CURRENT_TIMESTAMP
- **Purpose**: Audit trail, when announcement was created
- **Format**: ISO 8601 timestamp with timezone

#### `updated_at`
- **Type**: TIMESTAMP
- **Required**: Yes (system-generated)
- **Default**: CURRENT_TIMESTAMP
- **Purpose**: Audit trail, when announcement was last modified
- **Format**: ISO 8601 timestamp with timezone
- **Behavior**: Updated automatically on row modification (database trigger or application layer)

## Relationships

**None** in this phase. The `announcement` table is standalone with no foreign key relationships.

**Future Enhancements** (not in scope):
- Relationship to `user` table for authentication/ownership
- Relationship to `location` table for geocoded coordinates
- Relationship to `photo` table for multiple photos per announcement

## Indexes

**Initial Phase**: No indexes created (dataset small: 5-10 announcements)

**Future Optimization** (when filtering/search added):
- Index on `status` for filtering active announcements
- Index on `species` for filtering by animal type
- Index on `last_seen_date` for sorting by recency
- Full-text index on `description` for search functionality

## Constraints Summary

1. **Primary Key**: `id` (UUID)
2. **NOT NULL Constraints**: `id`, `pet_name`, `species`, `gender`, `description`, `location`, `last_seen_date`, `phone`, `status`, `created_at`, `updated_at`
3. **CHECK Constraints**: None (all validation in application layer)
4. **UNIQUE Constraints**: None
5. **Foreign Key Constraints**: None

## TypeScript Types

TypeScript types for the entity (defined in `/server/src/types/announcement.d.ts`):

```typescript
/**
 * Species enum for pet types.
 */
export type Species = 'DOG' | 'CAT' | 'BIRD' | 'RABBIT' | 'OTHER';

/**
 * Gender enum for pet gender.
 */
export type Gender = 'MALE' | 'FEMALE' | 'UNKNOWN';

/**
 * Status enum for announcement lifecycle.
 */
export type AnnouncementStatus = 'ACTIVE' | 'FOUND' | 'CLOSED';

/**
 * Lost pet announcement entity representing database row.
 */
export interface Announcement {
  /** Unique identifier (UUID v4) */
  id: string;
  
  /** Name of the lost pet */
  petName: string;
  
  /** Type of animal (constrained enum) */
  species: Species;
  
  /** Breed of the pet (optional) */
  breed: string | null;
  
  /** Gender of the pet (constrained enum) */
  gender: Gender;
  
  /** Detailed description of the pet */
  description: string;
  
  /** Location where pet was last seen */
  location: string;
  
  /** Search radius around location in kilometers (optional) */
  locationRadius: number | null;
  
  /** Date when pet was last seen (ISO 8601: YYYY-MM-DD) */
  lastSeenDate: string;
  
  /** Reporter's email address (optional, basic validation) */
  email: string | null;
  
  /** Reporter's phone number (must contain digits) */
  phone: string;
  
  /** URL to photo of the pet (optional) */
  photoUrl: string | null;
  
  /** Current status of announcement (lifecycle state) */
  status: AnnouncementStatus;
  
  /** When announcement was created (ISO 8601 timestamp) */
  createdAt: string;
  
  /** When announcement was last updated (ISO 8601 timestamp) */
  updatedAt: string;
}

/**
 * Database row (snake_case field names).
 * Used by repository layer when querying database.
 */
export interface AnnouncementRow {
  id: string;
  pet_name: string;
  species: Species;
  breed: string | null;
  gender: Gender;
  description: string;
  location: string;
  location_radius: number | null;
  last_seen_date: string;
  email: string | null;
  phone: string;
  photo_url: string | null;
  status: AnnouncementStatus;
  created_at: string;
  updated_at: string;
}
```

## Naming Conventions

**Database**: snake_case (e.g., `pet_name`, `location_radius`, `last_seen_date`)
- Standard SQL convention
- PostgreSQL default
- Consistent with existing backend schema

**TypeScript/API**: camelCase (e.g., `petName`, `locationRadius`, `lastSeenDate`)
- JavaScript/TypeScript convention
- JSON API best practice
- Repository layer transforms snake_case → camelCase

**Example Mapping**:
- Database: `pet_name` → TypeScript: `petName`
- Database: `location_radius` → TypeScript: `locationRadius`
- Database: `last_seen_date` → TypeScript: `lastSeenDate`

## Migration Strategy

### SQLite Schema (Initial)

```sql
CREATE TABLE IF NOT EXISTS announcement (
  id TEXT PRIMARY KEY NOT NULL,
  pet_name VARCHAR(100) NOT NULL,
  species VARCHAR(20) NOT NULL,
  breed VARCHAR(100),
  gender VARCHAR(20) NOT NULL,
  description VARCHAR(1000) NOT NULL,
  location VARCHAR(255) NOT NULL,
  location_radius INTEGER,
  last_seen_date TEXT NOT NULL,
  email VARCHAR(255),
  phone VARCHAR(50) NOT NULL,
  photo_url VARCHAR(500),
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Note**: No CHECK constraints - all enum and business logic validation happens in application layer.

### PostgreSQL Schema (Migration Target)

```sql
-- Create table (no ENUM types, use VARCHAR)
CREATE TABLE IF NOT EXISTS announcement (
  id UUID PRIMARY KEY NOT NULL,
  pet_name VARCHAR(100) NOT NULL,
  species VARCHAR(20) NOT NULL,
  breed VARCHAR(100),
  gender VARCHAR(20) NOT NULL,
  description VARCHAR(1000) NOT NULL,
  location VARCHAR(255) NOT NULL,
  location_radius INTEGER,
  last_seen_date DATE NOT NULL,
  email VARCHAR(255),
  phone VARCHAR(50) NOT NULL,
  photo_url VARCHAR(500),
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create trigger for updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS update_announcement_updated_at ON announcement;
CREATE TRIGGER update_announcement_updated_at
BEFORE UPDATE ON announcement
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Create indexes (future optimization)
-- CREATE INDEX IF NOT EXISTS idx_announcement_status ON announcement(status);
-- CREATE INDEX IF NOT EXISTS idx_announcement_species ON announcement(species);
-- CREATE INDEX IF NOT EXISTS idx_announcement_last_seen_date ON announcement(last_seen_date);
```

**Note**: No database-level enum types - store as VARCHAR strings. Application layer validates allowed values.

## Seed Data

Seed file will include 5-10 example announcements covering:
- All species types (DOG, CAT, BIRD, RABBIT, OTHER)
- Mix of statuses (ACTIVE, FOUND, CLOSED)
- Optional fields both present and null (breed, email, locationRadius, photoUrl)
- Variety of locations and descriptions
- Realistic dates (recent last seen dates)

Example seed data:

```typescript
[
  {
    id: crypto.randomUUID(),
    petName: 'Max',
    species: 'DOG',
    breed: 'Golden Retriever',
    gender: 'MALE',
    description: 'Friendly golden retriever with red collar. Answers to Max.',
    location: 'Central Park, New York, NY',
    locationRadius: 5,
    lastSeenDate: '2025-11-18',
    email: 'john@example.com',
    phone: '+1-555-0101',
    photoUrl: 'https://example.com/photos/max.jpg',
    status: 'ACTIVE'
  },
  {
    id: crypto.randomUUID(),
    petName: 'Luna',
    species: 'CAT',
    breed: null,
    gender: 'FEMALE',
    description: 'Black cat with white paws. Very shy.',
    location: 'Downtown Portland, OR',
    locationRadius: null,
    lastSeenDate: '2025-11-15',
    email: null,
    phone: '555-0102',
    photoUrl: null,
    status: 'ACTIVE'
  },
  // ... 8 more examples
]
```

## Validation Rules

### Application-Layer Validation

All validation is performed at the application layer (no database constraints):

1. **Email** (if provided): Use validation library (e.g., validator.js) or basic format validation
2. **Phone**: Must contain digits (basic check)
3. **lastSeenDate**: Must match ISO 8601 date format and be valid date
4. **locationRadius** (if provided): Optional validation for positive values (not enforced)
5. **species**: Must be one of allowed values (DOG, CAT, BIRD, RABBIT, OTHER) - case-insensitive, converted to uppercase
6. **gender**: Must be one of allowed values (MALE, FEMALE, UNKNOWN) - case-insensitive, converted to uppercase
7. **status**: Must be one of allowed values (ACTIVE, FOUND, CLOSED) - case-insensitive, converted to uppercase

**Note**: Consider using external validation library (e.g., validator.js, joi, zod) for email/phone validation instead of custom regex.

### Database-Layer Validation

1. **NOT NULL constraints**: Enforce required fields
2. **VARCHAR length limits**: Prevent excessively long inputs
3. **No CHECK constraints**: All business logic validation happens in application layer

## Future Enhancements (Out of Scope)

1. **Geolocation**: Add `latitude` and `longitude` columns for map display
2. **Multiple Photos**: Create separate `photo` table with one-to-many relationship
3. **User Authentication**: Add `user_id` foreign key for ownership
4. **Comments/Updates**: Allow adding updates to announcements (e.g., "Spotted near X")
5. **Expiration**: Automatically close announcements after N days
6. **Full-Text Search**: Add GIN index on description for advanced search
7. **Soft Deletes**: Add `deleted_at` column instead of hard deletes

## References

- Feature Specification: [spec.md](./spec.md)
- Research Document: [research.md](./research.md)
- OpenAPI Contract: [contracts/announcements-api.yaml](./contracts/announcements-api.yaml)
- Knex Migrations: https://knexjs.org/guide/migrations.html
- PostgreSQL ENUM Types: https://www.postgresql.org/docs/current/datatype-enum.html
- ISO 8601 Date Format: https://www.iso.org/iso-8601-date-and-time-format.html

