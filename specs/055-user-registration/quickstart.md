# Quickstart: User Registration Endpoint

**Feature**: 055-user-registration  
**Date**: December 15, 2025

## Overview

This guide provides a quick reference for implementing the user registration endpoint. For detailed information, see:
- **Specification**: [spec.md](./spec.md)
- **Implementation Plan**: [plan.md](./plan.md)
- **Research**: [research.md](./research.md)
- **Data Model**: [data-model.md](./data-model.md)
- **API Contract**: [contracts/post-users.yaml](./contracts/post-users.yaml)

## Prerequisites

- Node.js v24 (LTS) installed
- Project dependencies installed (`npm install` in `/server`)
- SQLite database configured
- Existing `/server` module structure in place

## Implementation Checklist

### Phase 1: Database Setup

- [ ] **Create Migration** (`/server/src/database/migrations/YYYYMMDDHHMMSS_create_user_table.ts`)
  ```bash
  cd server
  npm run knex:add-migration create_user_table
  ```
  
  ```typescript
  export async function up(knex: Knex): Promise<void> {
    await knex.schema.createTableIfNotExists('user', (table) => {
      table.text('id').primary().notNullable();
      table.text('email').unique().notNullable();
      table.text('password_hash').notNullable();
      table.timestamp('created_at').notNullable().defaultTo(knex.fn.now());
      table.timestamp('updated_at').notNullable().defaultTo(knex.fn.now());
    });
  }
  
  export async function down(knex: Knex): Promise<void> {
    await knex.schema.dropTableIfExists('user');
  }
  ```

- [ ] **Run Migration**
  ```bash
  npm run knex:migrate
  ```

### Phase 2: Validation Logic (TDD)

**Location**: `/server/src/lib/validators.ts` (extend existing file)

- [ ] **Write Unit Tests First** (`/server/src/lib/__test__/validators.test.ts` - extend existing)
  - Test email validation wrapper (required, format, max length)
  - Test password validation (required, min/max length)
  - Follow Given-When-Then structure
  - Note: `isValidEmail()` already exists and tested

- [ ] **Implement Validation Functions**
  ```typescript
  export function validateEmail(email: string): void;  // NEW - wraps existing isValidEmail()
  export function validatePassword(password: string): void;  // NEW
  ```

- [ ] **Verify 80% Coverage**
  ```bash
  npm test --coverage -- src/lib/__test__/validators.test.ts
  ```

### Phase 3: Repository Layer (TDD)

**Location**: `/server/src/database/repositories/user-repository.ts` (interface + implementation in same file)

- [ ] **Define Interface & Implementation**
  ```typescript
  // Interface
  export interface IUserRepository {
    findByEmail(email: string): Promise<UserRow | null>;
    create(user: Omit<UserRow, 'created_at' | 'updated_at'>): Promise<void>;
  }
  
  // Implementation (in same file)
  export class KnexUserRepository implements IUserRepository {
    constructor(private knex: Knex) {}
    
    async findByEmail(email: string): Promise<UserRow | null> {
      // Implementation
    }
    
    async create(user: Omit<UserRow, 'created_at' | 'updated_at'>): Promise<void> {
      // Implementation
    }
  }
  ```

- [ ] **Write Unit Tests** (mock Knex)

### Phase 4: Service Layer (TDD)

**Location**: `/server/src/services/user-service.ts`

- [ ] **Write Unit Tests First** (`/server/src/services/__test__/user-service.test.ts`)
  - Test successful registration
  - Test validation errors
  - Test duplicate email (HTTP 409)
  - Mock repository and password hashing

- [ ] **Implement Service**
  ```typescript
  export class UserService {
    constructor(
      private userRepository: IUserRepository,
      private hashPassword: (password: string) => Promise<string>
    ) {}
    
    async registerUser(email: string, password: string): Promise<string> {
      // 1. Normalize email
      // 2. Validate email
      // 3. Validate password
      // 4. Check for duplicate
      // 5. Hash password
      // 6. Generate UUID
      // 7. Create user
      // 8. Return ID
    }
  }
  ```

- [ ] **Verify 80% Coverage**
  ```bash
  npm test --coverage -- src/services/__test__/user-service.test.ts
  ```

### Phase 5: Route Handler (TDD)

**Location**: `/server/src/routes/users.ts`

- [ ] **Write Integration Tests First** (`/server/src/__test__/user-registration.test.ts`)
  - Use SuperTest for HTTP testing
  - Test successful registration (HTTP 201)
  - Test validation errors (HTTP 400)
  - Test duplicate email (HTTP 409)
  - Follow Given-When-Then structure

- [ ] **Implement Route Handler**
  ```typescript
  import express from 'express';
  
  const router = express.Router();
  
  router.post('/users', async (req, res, next) => {
    try {
      const { email, password } = req.body;
      const id = await userService.registerUser(email, password);
      return res.status(201).json({ id });
    } catch (error) {
      next(error);
    }
  });
  
  export default router;
  ```

- [ ] **Register Routes** (`/server/src/app.ts`)
  ```typescript
  import userRoutes from './routes/users.ts';
  app.use('/api/v1', userRoutes);
  ```

- [ ] **Verify 80% Coverage**
  ```bash
  npm test --coverage -- src/__test__/user-registration.test.ts
  ```

### Phase 6: Dependency Injection

**Location**: `/server/src/routes/users.ts` (or dedicated DI setup)

- [ ] **Setup Manual DI**
  ```typescript
  import { db } from '../database/db.ts';
  import { KnexUserRepository } from '../database/repositories/user-repository.ts';
  import { UserService } from '../services/user-service.ts';
  import { hashPassword } from '../lib/password-management.ts';
  
  const userRepository = new KnexUserRepository(db);
  const userService = new UserService(userRepository, hashPassword);
  ```

### Phase 7: Error Handling

- [ ] **Verify Error Middleware Catches All Errors**
  - Existing middleware in `/server/src/middlewares/error-handler-middleware.ts` should handle:
    - `ValidationError` → HTTP 400
    - Custom conflict error → HTTP 409
    - Database errors → HTTP 500

- [ ] **Add Custom Error for Conflicts** (if not exists)
  ```typescript
  export class ConflictError extends CustomError {
    constructor(code: string, message: string) {
      super(409, code, message);
    }
  }
  ```

### Phase 8: Format Code

- [ ] **Run Code Formatter**
  ```bash
  npm run format
  ```

### Phase 9: Final Verification

- [ ] **Run All Tests**
  ```bash
  npm test
  ```

- [ ] **Check Coverage**
  ```bash
  npm test --coverage
  ```
  - Verify 80%+ line and branch coverage for:
    - `/server/src/lib/user-validation.ts`
    - `/server/src/services/user-service.ts`
    - `/server/src/__test__/user-registration.test.ts`

- [ ] **Run Linter**
  ```bash
  npm run lint
  ```

- [ ] **Manual Testing** (optional)
  ```bash
  npm run dev
  
  # Test successful registration
  curl -X POST http://localhost:3000/api/v1/users \
    -H "Content-Type: application/json" \
    -d '{"email":"test@example.com","password":"password123"}'
  
  # Test duplicate email (should return 409)
  curl -X POST http://localhost:3000/api/v1/users \
    -H "Content-Type: application/json" \
    -d '{"email":"test@example.com","password":"password123"}'
  
  # Test validation error (should return 400)
  curl -X POST http://localhost:3000/api/v1/users \
    -H "Content-Type: application/json" \
    -d '{"email":"invalid-email","password":"short"}'
  ```

## Key Files

| File | Purpose |
|------|---------|
| `/server/src/routes/users.ts` | Route handler for POST /api/v1/users |
| `/server/src/services/user-service.ts` | Business logic for user registration |
| `/server/src/database/repositories/user-repository.ts` | Interface + Knex implementation for user table |
| `/server/src/lib/validators.ts` | Email and password validation (extend existing) |
| `/server/src/lib/password-management.ts` | Existing scrypt hashing (REUSE) |
| `/server/src/__test__/user-registration.test.ts` | Integration tests |
| `/server/src/services/__test__/user-service.test.ts` | Unit tests for service |
| `/server/src/lib/__test__/validators.test.ts` | Unit tests for validation (extend existing) |
| `/server/src/database/migrations/YYYYMMDDHHMMSS_create_user_table.ts` | Database migration |

## Testing Strategy

### Unit Tests (Vitest)

**Validation** (`/server/src/lib/__test__/validators.test.ts` - extend existing tests):
- Email validation wrapper (required, format, max length)
- Password validation (required, min/max length)
- Note: `isValidEmail()` already has tests

**Service** (`/server/src/services/__test__/user-service.test.ts`):
- Successful registration flow
- Email normalization
- Validation errors
- Duplicate email handling
- Password hashing integration

**Repository** (optional, can use integration tests instead):
- Database operations (create, findByEmail)

### Integration Tests (Vitest + SuperTest)

**POST /api/v1/users** (`/server/src/__test__/user-registration.test.ts`):
- HTTP 201: Successful registration
- HTTP 400: Missing email
- HTTP 400: Invalid email format
- HTTP 400: Email too long
- HTTP 400: Missing password
- HTTP 400: Password too short
- HTTP 400: Password too long
- HTTP 409: Duplicate email
- HTTP 400: Malformed JSON

## Common Pitfalls

1. **Forgetting to normalize email to lowercase**
   - Always call `email.toLowerCase().trim()` before validation and storage

2. **Not handling database constraint violations**
   - Catch unique constraint errors and map to HTTP 409

3. **Storing plaintext passwords**
   - ALWAYS hash passwords with scrypt before storage

4. **Testing after implementation**
   - Follow TDD: Write tests FIRST, then implement

5. **Missing error codes**
   - Every validation error needs a unique code (e.g., `MISSING_EMAIL`)

6. **Incorrect response format**
   - Success: `{"id": "uuid"}` (not full user object)
   - Error: Existing format with requestId, code, message, field

7. **Not reusing existing scrypt implementation**
   - Import from `/server/src/lib/password-management.ts`

8. **Not reusing existing email validator**
   - Use existing `isValidEmail()` from `/server/src/lib/validators.ts`

9. **Forgetting to run formatter**
   - Always run `npm run format` at end of each task

## Example Test Structure

```typescript
describe('POST /api/v1/users', () => {
  it('should return 201 with user ID when registration succeeds', async () => {
    // Given
    const requestBody = {
      email: 'newuser@example.com',
      password: 'securePassword123'
    };
    
    // When
    const response = await request(server)
      .post('/api/v1/users')
      .send(requestBody)
      .expect('Content-Type', /json/)
      .expect(201);
    
    // Then
    expect(response.body).toHaveProperty('id');
    expect(response.body.id).toMatch(/^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i);
    
    // Verify user created in database
    const user = await userRepository.findByEmail('newuser@example.com');
    expect(user).not.toBeNull();
    expect(user?.email).toBe('newuser@example.com');
  });
});
```

## Success Criteria Reference

From [spec.md](./spec.md):

- ✅ **SC-001**: Users can register in under 30 seconds
- ✅ **SC-002**: System rejects 100% of duplicate emails
- ✅ **SC-003**: System rejects invalid password lengths
- ✅ **SC-004**: System validates 95%+ valid emails
- ✅ **SC-005**: Zero plaintext passwords in database
- ✅ **SC-006**: Endpoint responds within 2 seconds
- ✅ **SC-007**: Clear error messages for all failures

## Next Steps

After completing implementation:

1. Run `/speckit.tasks` to generate task breakdown (if not already done)
2. Follow task-by-task implementation in [tasks.md](./tasks.md)
3. Commit code after each task completion
4. Update Constitution if any violations discovered

## References

- **API Contract**: [contracts/post-users.yaml](./contracts/post-users.yaml)
- **Data Model**: [data-model.md](./data-model.md)
- **Research**: [research.md](./research.md)
- **Plan**: [plan.md](./plan.md)
- **Spec**: [spec.md](./spec.md)
- **Constitution**: `/.specify/memory/constitution.md`

---

**Last Updated**: December 15, 2025  
**Status**: Ready for implementation

