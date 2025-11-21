# Quickstart Guide: Pet Details API Endpoint

**Feature**: `008-pet-details-api`  
**Date**: 2025-11-21  
**Branch**: `008-pet-details-api`

## Overview

This guide provides step-by-step instructions for implementing the `GET /api/v1/announcements/:id` endpoint using Test-Driven Development (TDD). Follow the Red-Green-Refactor cycle to ensure high code quality and test coverage.

## Prerequisites

- Node.js v24 (LTS) installed
- Existing `/server` backend module with Express, Knex, SQLite3, Vitest, SuperTest
- Existing `announcement` table in SQLite database
- Existing `announcement-routes.ts`, `announcement-service.ts`, `announcement-repository.ts` files (kebab-case naming)

## TDD Workflow Overview

```
Phase 1: Unit Tests (Service Layer)
├── RED:   Write failing test for service function
├── GREEN: Implement minimal service function
└── REFACTOR: Extract reusable logic

Phase 2: Integration Tests (API Endpoint)
├── RED:   Write failing test for HTTP endpoint
├── GREEN: Wire route handler to service
└── REFACTOR: Improve error handling and structure
```

## Phase 1: Unit Tests (Service Layer)

### Step 1.1: RED - Write Failing Test for Success Case

Create or extend `/server/src/services/__test__/announcement-service.test.ts`:

```typescript
import { describe, it, expect, beforeEach } from 'vitest';
import { getAnnouncementById } from '../announcement-service';
import { FakeAnnouncementRepository } from '../../database/__test__/fake-announcement-repository';
import type { Announcement } from '../../types';

describe('announcementService', () => {
    let fakeRepository: FakeAnnouncementRepository;

    beforeEach(() => {
        fakeRepository = new FakeAnnouncementRepository();
    });

    describe('getAnnouncementById', () => {
        it('should return announcement when ID exists', async () => {
            // Given - repository with test announcement
            const mockAnnouncement: Announcement = {
                id: '123e4567-e89b-12d3-a456-426614174000',
                petName: 'Max',
                species: 'DOG',
                breed: 'Golden Retriever',
                gender: 'MALE',
                description: 'Friendly dog',
                location: 'Central Park',
                locationRadius: 5.0,
                lastSeenDate: '2025-11-20',
                email: 'owner@example.com',
                phone: '+1-555-0123',
                photoUrl: 'https://example.com/max.jpg',
                status: 'ACTIVE'
            };
            fakeRepository.setAnnouncements([mockAnnouncement]);

            // When - service is called
            const result = await getAnnouncementById(fakeRepository, '123e4567-e89b-12d3-a456-426614174000');

            // Then - announcement is returned
            expect(result).toEqual(mockAnnouncement);
        });
    });
});
```

**Run test**: `npm test` (from `/server` directory)

**Expected**: ❌ Test fails (function `getAnnouncementById` does not exist)

### Step 1.2: GREEN - Implement Minimal Service Function

Extend `/server/src/services/announcement-service.ts`:

```typescript
import type { AnnouncementRepository } from '../database/announcement-repository';
import type { Announcement } from '../types';

/**
 * Retrieves a single announcement by its unique identifier.
 * 
 * @param repository - Announcement repository instance
 * @param id - Announcement UUID
 * @returns Announcement if found, null if not found
 */
export async function getAnnouncementById(
    repository: AnnouncementRepository,
    id: string
): Promise<Announcement | null> {
    return repository.findById(id);
}
```

**Run test**: `npm test`

**Expected**: ❌ Test still fails (repository method `findById` does not exist)

### Step 1.3: GREEN - Implement Repository Method

Extend `/server/src/database/announcement-repository.ts`:

```typescript
export interface AnnouncementRepository {
    findAll(): Promise<Announcement[]>;
    findById(id: string): Promise<Announcement | null>; // Add this method
}

export class KnexAnnouncementRepository implements AnnouncementRepository {
    constructor(private db: Knex) {}

    async findAll(): Promise<Announcement[]> {
        return this.db<Announcement>('announcement').select('*');
    }

    /**
     * Retrieves a single announcement by its unique identifier.
     * 
     * @param id - Announcement UUID
     * @returns Announcement object if found, null otherwise
     */
    async findById(id: string): Promise<Announcement | null> {
        const result = await this.db<Announcement>('announcement')
            .where('id', id)
            .first();
        
        return result ?? null;
    }
}
```

### Step 1.4: GREEN - Create Fake Repository for Tests

Create `/server/src/database/__test__/fake-announcement-repository.ts`:

```typescript
import type { Announcement } from '../../types';
import type { AnnouncementRepository } from '../announcement-repository';

/**
 * In-memory fake repository for unit testing.
 * Provides deterministic behavior without database dependency.
 */
export class FakeAnnouncementRepository implements AnnouncementRepository {
    private announcements: Announcement[] = [];

    setAnnouncements(announcements: Announcement[]): void {
        this.announcements = announcements;
    }

    async findAll(): Promise<Announcement[]> {
        return this.announcements;
    }

    async findById(id: string): Promise<Announcement | null> {
        const announcement = this.announcements.find(a => a.id === id);
        return announcement ?? null;
    }
}
```

**Run test**: `npm test`

**Expected**: ✅ Test passes

### Step 1.5: RED - Write Failing Test for Not Found Case

Add to `/server/src/services/__test__/announcement-service.test.ts`:

```typescript
it('should return null when ID does not exist', async () => {
    // Given - repository with no announcements
    fakeRepository.setAnnouncements([]);

    // When - service is called with non-existent ID
    const result = await getAnnouncementById(fakeRepository, 'non-existent-id');

    // Then - null is returned
    expect(result).toBeNull();
});
```

**Run test**: `npm test`

**Expected**: ✅ Test passes (service already handles this case)

### Step 1.6: RED - Write Test for Optional Fields with Null

Add to `/server/src/services/__test__/announcement-service.test.ts`:

```typescript
it('should return announcement with null optional fields', async () => {
    // Given - announcement with null optional fields
    const mockAnnouncement: Announcement = {
        id: '987fcdeb-51a2-43d7-9876-543210fedcba',
        petName: 'Luna',
        species: 'CAT',
        breed: null,
        gender: 'FEMALE',
        description: 'Gray tabby cat',
        location: 'Downtown Seattle',
        locationRadius: null,
        lastSeenDate: '2025-11-19',
        email: null,
        phone: '+1-555-9876',
        photoUrl: null,
        status: 'FOUND'
    };
    fakeRepository.setAnnouncements([mockAnnouncement]);

    // When - service is called
    const result = await getAnnouncementById(fakeRepository, '987fcdeb-51a2-43d7-9876-543210fedcba');

    // Then - announcement with nulls is returned
    expect(result).toEqual(mockAnnouncement);
    expect(result?.breed).toBeNull();
    expect(result?.email).toBeNull();
    expect(result?.photoUrl).toBeNull();
    expect(result?.locationRadius).toBeNull();
});
```

**Run test**: `npm test`

**Expected**: ✅ Test passes (repository preserves null values)

### Step 1.7: REFACTOR (if needed)

No refactoring needed at this point—service function is already simple and focused.

**Coverage Check**: Run `npm test -- --coverage` and verify 80%+ coverage for `announcement-service.ts`.

---

## Phase 2: Integration Tests (API Endpoint)

### Step 2.1: RED - Write Failing Test for HTTP 200 Success Case

Create or extend `/server/src/__test__/announcement-routes.test.ts`:

```typescript
import { describe, it, expect, beforeAll, afterAll, beforeEach } from 'vitest';
import request from 'supertest';
import app from '../app';
import { setupTestDatabase, teardownTestDatabase, clearDatabase, seedDatabase } from './test-db-helper';
import type { Announcement } from '../types';

describe('GET /api/v1/announcements/:id', () => {
    beforeAll(async () => {
        await setupTestDatabase();
    });

    afterAll(async () => {
        await teardownTestDatabase();
    });

    beforeEach(async () => {
        await clearDatabase();
    });

    it('should return 200 and announcement when ID exists', async () => {
        // Given - database seeded with test announcement
        const mockAnnouncement: Announcement = {
            id: '123e4567-e89b-12d3-a456-426614174000',
            petName: 'Max',
            species: 'DOG',
            breed: 'Golden Retriever',
            gender: 'MALE',
            description: 'Friendly dog',
            location: 'Central Park',
            locationRadius: 5.0,
            lastSeenDate: '2025-11-20',
            email: 'owner@example.com',
            phone: '+1-555-0123',
            photoUrl: 'https://example.com/max.jpg',
            status: 'ACTIVE'
        };
        await seedDatabase([mockAnnouncement]);

        // When - client requests announcement by ID
        const response = await request(app)
            .get('/api/v1/announcements/123e4567-e89b-12d3-a456-426614174000')
            .expect('Content-Type', /json/)
            .expect(200);

        // Then - response contains announcement
        expect(response.body).toEqual(mockAnnouncement);
    });
});
```

**Run test**: `npm test`

**Expected**: ❌ Test fails (route handler does not exist)

### Step 2.2: GREEN - Implement Route Handler

Extend `/server/src/routes/announcement-routes.ts`:

```typescript
import { Router } from 'express';
import * as announcementService from '../services/announcement-service';
import { announcementRepository } from '../database/announcement-repository';

const router = Router();

// Existing routes...
router.get('/announcements', async (req, res, next) => {
    // ... existing list endpoint
});

/**
 * GET /api/v1/announcements/:id
 * Returns a single announcement by its unique identifier.
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
        const announcement = await announcementService.getAnnouncementById(
            announcementRepository,
            req.params.id
        );

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

export default router;
```

**Run test**: `npm test`

**Expected**: ✅ Test passes

### Step 2.3: RED - Write Failing Test for HTTP 404 Not Found Case

Add to `/server/src/__test__/announcement-routes.test.ts`:

```typescript
it('should return 404 when ID does not exist', async () => {
    // Given - empty database
    // (cleared by beforeEach)

    // When - client requests non-existent ID
    const response = await request(app)
        .get('/api/v1/announcements/123e4567-e89b-12d3-a456-426614174000')
        .expect('Content-Type', /json/)
        .expect(404);

    // Then - error response returned
    expect(response.body).toEqual({
        error: {
            code: 'NOT_FOUND',
            message: 'Resource not found'
        }
    });
});
```

**Run test**: `npm test`

**Expected**: ✅ Test passes (route handler already handles null case)

### Step 2.4: RED - Write Failing Test for Malformed UUID

Add to `/server/src/__test__/announcement-routes.test.ts`:

```typescript
it('should return 404 when UUID is malformed', async () => {
    // Given - empty database

    // When - client requests with malformed UUID
    const response = await request(app)
        .get('/api/v1/announcements/abc-123')
        .expect('Content-Type', /json/)
        .expect(404);

    // Then - same 404 error as non-existent ID
    expect(response.body).toEqual({
        error: {
            code: 'NOT_FOUND',
            message: 'Resource not found'
        }
    });
});
```

**Run test**: `npm test`

**Expected**: ✅ Test passes (malformed UUID returns null from database query)

### Step 2.5: RED - Write Test for Optional Fields with Null

Add to `/server/src/__test__/announcement-routes.test.ts`:

```typescript
it('should include optional fields with null values', async () => {
    // Given - announcement with null optional fields
    const mockAnnouncement: Announcement = {
        id: '987fcdeb-51a2-43d7-9876-543210fedcba',
        petName: 'Luna',
        species: 'CAT',
        breed: null,
        gender: 'FEMALE',
        description: 'Gray tabby cat',
        location: 'Downtown Seattle',
        locationRadius: null,
        lastSeenDate: '2025-11-19',
        email: null,
        phone: '+1-555-9876',
        photoUrl: null,
        status: 'FOUND'
    };
    await seedDatabase([mockAnnouncement]);

    // When - client requests announcement
    const response = await request(app)
        .get('/api/v1/announcements/987fcdeb-51a2-43d7-9876-543210fedcba')
        .expect(200);

    // Then - response includes null optional fields
    expect(response.body).toEqual(mockAnnouncement);
    expect(response.body.breed).toBeNull();
    expect(response.body.email).toBeNull();
    expect(response.body.photoUrl).toBeNull();
    expect(response.body.locationRadius).toBeNull();
});
```

**Run test**: `npm test`

**Expected**: ✅ Test passes (Express res.json() serializes nulls correctly)

### Step 2.6: RED - Write Test for Different Status Values

Add to `/server/src/__test__/announcement-routes.test.ts`:

```typescript
it.each([
    ['ACTIVE'],
    ['FOUND'],
    ['CLOSED']
])('should return announcement with status %s', async (status) => {
    // Given - announcement with specific status
    const mockAnnouncement: Announcement = {
        id: '111e1111-e11b-11d1-a111-111111111111',
        petName: 'Test Pet',
        species: 'DOG',
        breed: 'Mixed',
        gender: 'MALE',
        description: 'Test description',
        location: 'Test location',
        locationRadius: 1.0,
        lastSeenDate: '2025-11-21',
        email: 'test@example.com',
        phone: '+1-555-0000',
        photoUrl: null,
        status: status as 'ACTIVE' | 'FOUND' | 'CLOSED'
    };
    await seedDatabase([mockAnnouncement]);

    // When - client requests announcement
    const response = await request(app)
        .get('/api/v1/announcements/111e1111-e11b-11d1-a111-111111111111')
        .expect(200);

    // Then - announcement with correct status returned
    expect(response.body.status).toBe(status);
});
```

**Run test**: `npm test`

**Expected**: ✅ Test passes (endpoint returns all status values)

### Step 2.7: REFACTOR - Extract Error Response Function (Optional)

If error response structure is duplicated across multiple routes, consider extracting to `/server/src/lib/error-responses.ts`:

```typescript
/**
 * Creates a structured error response object.
 * 
 * @param code - Machine-readable error code
 * @param message - Human-readable error message
 * @returns Structured error response
 */
export function createErrorResponse(code: string, message: string) {
    return {
        error: {
            code,
            message
        }
    };
}

export const ErrorResponses = {
    NOT_FOUND: createErrorResponse('NOT_FOUND', 'Resource not found'),
    INTERNAL_ERROR: createErrorResponse('INTERNAL_ERROR', 'Internal server error')
};
```

Update route handler:

```typescript
import { ErrorResponses } from '../lib/error-responses';

router.get('/announcements/:id', async (req, res, next) => {
    try {
        const announcement = await announcementService.getAnnouncementById(
            announcementRepository,
            req.params.id
        );

        if (!announcement) {
            return res.status(404).json(ErrorResponses.NOT_FOUND);
        }

        res.status(200).json(announcement);
    } catch (error) {
        next(error);
    }
});
```

**Run test**: `npm test`

**Expected**: ✅ All tests still pass

**Coverage Check**: Run `npm test -- --coverage` and verify 80%+ coverage for `announcement-routes.ts` and `announcement-service.ts`.

---

## Verification Checklist

Before committing your implementation, verify:

- [ ] All unit tests pass (`npm test` from `/server` directory)
- [ ] All integration tests pass
- [ ] Coverage ≥ 80% for service layer (`announcement-service.ts`)
- [ ] Coverage ≥ 80% for API endpoint (`announcement-routes.ts`)
- [ ] ESLint passes (`npm run lint` from `/server` directory)
- [ ] All tests follow Given-When-Then structure
- [ ] JSDoc documentation added for public functions
- [ ] Route handler includes JSDoc with HTTP method, path, params, responses
- [ ] Service function includes JSDoc with params, returns, description
- [ ] Repository method includes JSDoc with params, returns, description
- [ ] Error responses follow structured format (code + message)
- [ ] Optional fields included with `null` values (not omitted)
- [ ] Malformed UUIDs return 404 (same as non-existent)
- [ ] All status values (ACTIVE, FOUND, CLOSED) are returned

## Manual Testing

Start the development server and test the endpoint manually:

### 1. Start Server

```bash
cd server
npm run dev
```

Server should start on `http://localhost:3000`

### 2. Test Success Case (200)

**Prerequisite**: Ensure database has at least one announcement (seed from list endpoint test data)

```bash
curl -X GET http://localhost:3000/api/v1/announcements/123e4567-e89b-12d3-a456-426614174000
```

**Expected Response** (200):
```json
{
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "petName": "Max",
    "species": "DOG",
    "breed": "Golden Retriever",
    "gender": "MALE",
    "description": "Friendly dog",
    "location": "Central Park",
    "locationRadius": 5.0,
    "lastSeenDate": "2025-11-20",
    "email": "owner@example.com",
    "phone": "+1-555-0123",
    "photoUrl": "https://example.com/max.jpg",
    "status": "ACTIVE"
}
```

### 3. Test Not Found Case (404)

```bash
curl -X GET http://localhost:3000/api/v1/announcements/non-existent-id
```

**Expected Response** (404):
```json
{
    "error": {
        "code": "NOT_FOUND",
        "message": "Resource not found"
    }
}
```

### 4. Test Malformed UUID (404)

```bash
curl -X GET http://localhost:3000/api/v1/announcements/abc-123
```

**Expected Response** (404):
```json
{
    "error": {
        "code": "NOT_FOUND",
        "message": "Resource not found"
    }
}
```

## Performance Testing

Verify response time < 500ms:

```bash
# Install Apache Bench (if not already installed)
# macOS: brew install httpd (includes ab)
# Linux: apt-get install apache2-utils

# Run 100 requests with concurrency of 10
ab -n 100 -c 10 http://localhost:3000/api/v1/announcements/123e4567-e89b-12d3-a456-426614174000
```

**Expected**: Mean response time < 500ms (should be much lower for direct database query)

## Common Issues & Solutions

### Issue: Test fails with "Cannot find module 'supertest'"

**Solution**: Install SuperTest as dev dependency
```bash
npm install --save-dev supertest @types/supertest
```

### Issue: Test fails with "announcement table does not exist"

**Solution**: Run migrations to create the table
```bash
npm run knex:migrate:latest
```

### Issue: Coverage below 80%

**Solution**: Ensure all test cases are written:
- Unit tests: success, not found, optional fields with null
- Integration tests: 200, 404 (not found), 404 (malformed UUID), optional fields, all status values

### Issue: Route handler returns 500 instead of 404

**Solution**: Check error handling middleware. Ensure it's registered after route handlers in `app.ts`.

## Next Steps

After completing this feature:

1. **Commit Changes**: Commit your implementation to the `008-pet-details-api` branch
2. **Code Review**: Request code review focusing on test coverage and TDD adherence
3. **Update Documentation**: Update API documentation with new endpoint (if separate from OpenAPI spec)
4. **Deploy**: Deploy to staging environment for QA testing
5. **Frontend Integration**: Update mobile/web clients to use new detail endpoint

## Reference Files

- **Feature Spec**: `specs/008-pet-details-api/spec.md`
- **Research**: `specs/008-pet-details-api/research.md`
- **Data Model**: `specs/008-pet-details-api/data-model.md`
- **OpenAPI Contract**: `specs/008-pet-details-api/contracts/announcement-api.openapi.yaml`
- **Implementation Plan**: `specs/008-pet-details-api/plan.md`

## Questions?

If you encounter issues or have questions during implementation, refer to:
- Constitution: `.specify/memory/constitution.md` (Backend Architecture & Quality Standards)
- Workspace Rules: `.specify/memory/README.md` (Build commands, testing guidelines)
- Research Document: `specs/008-pet-details-api/research.md` (Technical decisions and rationale)

