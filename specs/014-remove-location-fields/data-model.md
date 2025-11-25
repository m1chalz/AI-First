# Data Model: Remove Location Fields

**Feature**: Remove Location Fields  
**Date**: 2025-01-27  
**Status**: Design Complete

## Overview

This document describes the updated announcement data model after removing deprecated location fields (`location`, `locationCity`, `locationRadius`). Location information is now provided solely via `locationLatitude` and `locationLongitude` coordinates.

## Entity: Announcement

### Fields Removed

The following fields are **removed** from all data models:

- `location` (string) - Text-based location description
- `locationCity` (string) - City name
- `locationRadius` (number) - Search radius in kilometers

### Fields Retained

Location information is provided via:

- `locationLatitude` (number, required) - Latitude coordinate (-90 to 90)
- `locationLongitude` (number, required) - Longitude coordinate (-180 to 180)

### Complete Field List

**Required Fields**:
- `id` (string) - Unique identifier (UUID)
- `species` (string) - Pet species
- `sex` (string) - Pet sex/gender
- `locationLatitude` (number) - Latitude coordinate
- `locationLongitude` (number) - Longitude coordinate
- `photoUrl` (string) - Photo URL (HTTP/HTTPS)
- `lastSeenDate` (string) - Date in YYYY-MM-DD format
- `status` (AnnouncementStatus) - 'MISSING' or 'FOUND'
- `managementPasswordHash` (string) - Hashed management password
- `createdAt` (string) - ISO 8601 timestamp
- `updatedAt` (string) - ISO 8601 timestamp

**Optional Fields**:
- `petName` (string | null)
- `breed` (string | null)
- `age` (number | null)
- `description` (string | null)
- `microchipNumber` (string | null, unique)
- `email` (string | null)
- `phone` (string | null)
- `reward` (string | null)

**Contact Requirement**: At least one of `email` or `phone` must be provided.

## Database Schema

### Table: `announcement`

**Columns Removed**:
- `location` (TEXT, NOT NULL) - Removed from original migration
- `location_city` (TEXT, NULLABLE) - Removed
- `location_radius` (INTEGER, NULLABLE) - Removed

**Columns Retained**:
- `location_latitude` (FLOAT, NOT NULL)
- `location_longitude` (FLOAT, NOT NULL)

### Migration Details

**Migration File**: `[timestamp]_remove_location_fields.ts`

**Up Migration**:
- Drops `location_city` column (if exists)
- Drops `location_radius` column (if exists)
- Note: `location` column already removed in previous migration

**Down Migration**:
- Recreates `location_city` as TEXT NULLABLE
- Recreates `location_radius` as INTEGER NULLABLE
- Allows rollback without data loss concerns

## TypeScript Types

### CreateAnnouncementDto

**Removed Fields**:
```typescript
locationCity?: string;
locationRadius?: number;
```

**Retained Fields**:
```typescript
locationLatitude: number;
locationLongitude: number;
```

### AnnouncementDto

**Removed Fields**:
```typescript
locationCity?: string | null;
locationRadius?: number | null;
```

**Retained Fields**:
```typescript
locationLatitude: number;
locationLongitude: number;
```

### Announcement (Domain Model)

**Removed Fields**:
```typescript
locationCity?: string | null;
locationRadius?: number | null;
```

**Retained Fields**:
```typescript
locationLatitude: number;
locationLongitude: number;
```

### AnnouncementRow (Database Row)

**Removed Fields**:
```typescript
location_city: string | null;
location_radius: number | null;
```

**Retained Fields**:
```typescript
location_latitude: number;
location_longitude: number;
```

## Validation Rules

### Removed Validations

- `locationCity`: String validation (trim, optional)
- `locationRadius`: Positive integer validation (optional)

### Retained Validations

- `locationLatitude`: Number between -90 and 90 (required)
- `locationLongitude`: Number between -180 and 180 (required)

### Unknown Field Rejection

When clients send `location`, `locationCity`, or `locationRadius`:
- Zod strict mode rejects them as unknown fields
- Error code: `INVALID_FIELD`
- Error message: `"{field} is not a valid field"`
- Field name included in error response

## Repository Mapping

### Database Row → Domain Model

**Removed Mappings**:
```typescript
locationCity: row.location_city,
locationRadius: row.location_radius,
```

**Retained Mappings**:
```typescript
locationLatitude: row.location_latitude,
locationLongitude: row.location_longitude,
```

### Domain Model → Database Row

**Removed Mappings**:
```typescript
location_city: data.locationCity ?? null,
location_radius: data.locationRadius ?? null,
```

**Retained Mappings**:
```typescript
location_latitude: data.locationLatitude,
location_longitude: data.locationLongitude,
```

## Service Layer

### Removed Processing

- `locationCity` sanitization removed from `AnnouncementService.createAnnouncement()`

### Retained Processing

- All other field sanitization remains unchanged
- Latitude/longitude validation handled by Zod schema

## API Contract

### Request Body (POST /announcements)

**Removed Fields**:
- `locationCity` (string, optional)
- `locationRadius` (number, optional)

**Retained Fields**:
- `locationLatitude` (number, required)
- `locationLongitude` (number, required)

### Response Body

**Removed Fields**:
- `locationCity` (string | null)
- `locationRadius` (number | null)

**Retained Fields**:
- `locationLatitude` (number)
- `locationLongitude` (number)

### Error Response

When location fields are sent:
```json
{
  "error": {
    "code": "INVALID_FIELD",
    "message": "{field} is not a valid field",
    "field": "locationCity"
  }
}
```

## Seed Data

### Updated Seed Records

All seed data in `001_announcements.ts` updated to:
- Remove `location_city` values
- Remove `location_radius` values
- Retain `location_latitude` and `location_longitude` values

## Summary

The announcement data model is simplified to use only coordinate-based location (`locationLatitude`, `locationLongitude`). All text-based location fields (`location`, `locationCity`) and search radius (`locationRadius`) are removed from:

- Database schema (columns dropped)
- TypeScript types (interfaces updated)
- Validation schemas (fields removed, strict mode rejects)
- Repository layer (mapping removed)
- Service layer (processing removed)
- API documentation (examples updated)
- Seed data (values removed)

