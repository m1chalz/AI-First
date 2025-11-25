# Quickstart: Remove Location Fields

**Feature**: Remove Location Fields  
**Date**: 2025-01-27

## Overview

Quick reference for developers implementing the removal of deprecated location fields (`location`, `locationCity`, `locationRadius`) from the announcement data model.

## What's Changing

### Fields Removed
- `location` (string) - Already removed in previous migration
- `locationCity` (string) - Removed
- `locationRadius` (number) - Removed

### Fields Retained
- `locationLatitude` (number) - Required
- `locationLongitude` (number) - Required

## Implementation Checklist

### 1. Database Migration
- [ ] Create migration: `[timestamp]_remove_location_fields.ts`
- [ ] Drop `location_city` column
- [ ] Drop `location_radius` column
- [ ] Implement `down()` function for rollback
- [ ] Run migration: `npm run knex:migrate:latest` (from server/)

### 2. TypeScript Types
- [ ] Update `CreateAnnouncementDto` - Remove `locationCity`, `locationRadius`
- [ ] Update `AnnouncementDto` - Remove `locationCity`, `locationRadius`
- [ ] Update `Announcement` - Remove `locationCity`, `locationRadius`
- [ ] Update `AnnouncementRow` - Remove `location_city`, `location_radius`

### 3. Validation Schema
- [ ] Remove `locationCity` from Zod schema
- [ ] Remove `locationRadius` from Zod schema
- [ ] Verify strict mode rejects unknown fields (automatic)

### 4. Repository Layer
- [ ] Remove `location_city` mapping in `mapRowToAnnouncement()`
- [ ] Remove `location_radius` mapping in `mapRowToAnnouncement()`
- [ ] Remove `location_city` mapping in `create()` method
- [ ] Remove `location_radius` mapping in `create()` method

### 5. Service Layer
- [ ] Remove `locationCity` sanitization from `createAnnouncement()`

### 6. Tests
- [ ] Update unit tests in `announcement-validation.test.ts`
- [ ] Update unit tests in `announcement-service.test.ts`
- [ ] Update integration tests in `announcements.test.ts`
- [ ] Remove all location field test cases
- [ ] Verify 80% coverage maintained

### 7. Seed Data
- [ ] Update `001_announcements.ts` - Remove `location_city` values
- [ ] Update `001_announcements.ts` - Remove `location_radius` values

### 8. Documentation
- [ ] Update `README.md` - Remove location field descriptions
- [ ] Update `README.md` - Remove location field examples
- [ ] Update API examples to exclude location fields

## Testing

### Run Tests
```bash
cd server
npm test
npm test -- --coverage
```

### Verify Migration
```bash
cd server
npm run knex:migrate:latest
npm run knex:migrate:rollback  # Test rollback
npm run knex:migrate:latest     # Re-apply
```

### Manual API Testing

**Test 1: Reject location fields**
```bash
curl -X POST http://localhost:3000/announcements \
  -H "Content-Type: application/json" \
  -d '{
    "species": "Dog",
    "sex": "MALE",
    "locationLatitude": 40.785091,
    "locationLongitude": -73.968285,
    "locationCity": "New York",
    "photoUrl": "https://example.com/photo.jpg",
    "lastSeenDate": "2025-01-20",
    "status": "MISSING",
    "email": "test@example.com"
  }'
```

Expected: 400 Bad Request with `INVALID_FIELD` error

**Test 2: Valid request without location fields**
```bash
curl -X POST http://localhost:3000/announcements \
  -H "Content-Type: application/json" \
  -d '{
    "species": "Dog",
    "sex": "MALE",
    "locationLatitude": 40.785091,
    "locationLongitude": -73.968285,
    "photoUrl": "https://example.com/photo.jpg",
    "lastSeenDate": "2025-01-20",
    "status": "MISSING",
    "email": "test@example.com"
  }'
```

Expected: 201 Created with announcement (no location fields in response)

**Test 3: Retrieve announcement**
```bash
curl http://localhost:3000/announcements/{id}
```

Expected: 200 OK with announcement (no `locationCity` or `locationRadius` in response)

## Key Files

### Database
- `server/src/database/migrations/[timestamp]_remove_location_fields.ts`
- `server/src/database/repositories/announcement-repository.ts`
- `server/src/database/seeds/001_announcements.ts`

### Types
- `server/src/types/announcement.d.ts`

### Validation
- `server/src/lib/announcement-validation.ts`
- `server/src/lib/__test__/announcement-validation.test.ts`

### Services
- `server/src/services/announcement-service.ts`
- `server/src/services/__test__/announcement-service.test.ts`

### Integration Tests
- `server/src/__test__/announcements.test.ts`

### Documentation
- `server/README.md`

## Common Pitfalls

1. **Forgetting to update all type definitions** - Check `CreateAnnouncementDto`, `AnnouncementDto`, `Announcement`, and `AnnouncementRow`
2. **Missing repository mapping updates** - Both `mapRowToAnnouncement()` and `create()` need updates
3. **Leaving test data with location fields** - Update seed data and test fixtures
4. **Not testing migration rollback** - Verify `down()` function works correctly
5. **Missing API documentation updates** - Update README.md examples

## Success Criteria

- [ ] All tests pass with 80%+ coverage
- [ ] Migration runs successfully (up and down)
- [ ] API rejects location fields with `INVALID_FIELD` error
- [ ] API responses exclude location fields
- [ ] TypeScript compilation succeeds
- [ ] Documentation updated

