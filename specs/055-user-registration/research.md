# Research: User Registration Endpoint

**Feature**: 055-user-registration  
**Date**: December 15, 2025

## Overview

This document consolidates research findings for implementing the user registration endpoint. All technical unknowns from the Technical Context section have been resolved through codebase analysis.

## Research Findings

### 1. Password Hashing (Scrypt Implementation)

**Decision**: Reuse existing `hashPassword()` function from `/server/src/lib/password-management.ts`

**Rationale**:
- Already implements scrypt with secure parameters (SALT_LENGTH=16, KEY_LENGTH=64)
- Uses Node.js built-in `crypto.scrypt` (no external dependencies)
- Properly salted (16 bytes random salt per password)
- Returns format: `{salt_hex}:{derived_key_hex}`
- Well-tested with comprehensive unit tests
- Currently used for announcement management passwords

**Implementation Details**:
```typescript
import { hashPassword } from '../lib/password-management.ts';

// Usage in user service:
const passwordHash = await hashPassword(plainPassword);
// Store passwordHash in database
```

**Alternatives Considered**:
- bcrypt: Popular but slower than scrypt
- argon2: Most secure but requires native addon (complexity)
- PBKDF2: Older standard, less secure than scrypt

**Rejected Because**: Existing scrypt implementation is secure, well-tested, and meets all requirements. No need for additional dependencies or implementations.

---

### 2. UUID Generation

**Decision**: Use existing `uuid` package (v4) already installed in project

**Rationale**:
- Package `uuid@^13.0.0` already in `/server/package.json`
- Currently used in `AnnouncementRepository` for ID generation
- Industry standard, cryptographically secure UUIDs (v4 = random)
- Zero additional dependencies needed
- Consistent with existing codebase patterns

**Implementation Details**:
```typescript
import { v4 as uuidv4 } from 'uuid';

// Usage in user repository:
const id = uuidv4(); // Generates: "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d"
```

**Alternatives Considered**:
- Node.js `crypto.randomUUID()`: Built-in but requires Node 14.17+
- Custom UUID implementation: Unnecessary complexity
- Database-generated UUIDs: Less portable across databases

**Rejected Because**: `uuid` package is already a project dependency and provides consistent API across Node versions.

---

### 3. Email Validation

**Decision**: Reuse existing `isValidEmail()` function from `/server/src/lib/validators.ts` + add length/presence checks

**Rationale**:
- Existing validation function already implements RFC 5322-compliant regex: `/^[^\s@]+@[^\s@]+\.[^\s@]+$/`
- Max length: 254 characters (RFC 5321 standard) - needs to be added
- No external validation libraries needed (keeps dependencies minimal)
- Extend existing validators.ts file (maintain consistency)

**Existing Implementation** (from `/server/src/lib/validators.ts`):
```typescript
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export function isValidEmail(email: string): boolean {
  return EMAIL_REGEX.test(email);
}
```

**Enhancement Needed** (add to `/server/src/lib/validators.ts`):
```typescript
import { ValidationError } from './errors.ts';

const MAX_EMAIL_LENGTH = 254;

export function validateEmail(email: string): void {
  if (!email || email.trim().length === 0) {
    throw new ValidationError('MISSING_EMAIL', 'Email is required', 'email');
  }
  if (email.length > MAX_EMAIL_LENGTH) {
    throw new ValidationError('EMAIL_TOO_LONG', `Email must not exceed ${MAX_EMAIL_LENGTH} characters`, 'email');
  }
  if (!isValidEmail(email)) {
    throw new ValidationError('INVALID_EMAIL_FORMAT', 'Email format is invalid', 'email');
  }
}
```

**Alternatives Considered**:
- Create separate user-validation.ts: Unnecessary duplication, validators.ts already exists
- `validator` npm package: Adds dependency, overkill when we have existing validation
- Complex RFC 5322 regex: Overly complex, simple regex sufficient

**Rejected Because**: Existing `isValidEmail()` covers 99%+ of valid emails, maintains codebase consistency, no new files needed.

---

### 4. Password Validation

**Decision**: Add password validation to existing `/server/src/lib/validators.ts` (8-128 characters)

**Rationale**:
- Spec requirements: minimum 8 chars, maximum 128 chars (FR-005, FR-006)
- No complexity requirements (no uppercase/numbers/symbols)
- Simple validation, easily testable
- Max length prevents DoS via excessive scrypt hashing time
- Keep all validators in one place (consistency with existing email validation)

**Implementation Pattern** (add to `/server/src/lib/validators.ts`):
```typescript
// /server/src/lib/validators.ts
const MIN_PASSWORD_LENGTH = 8;
const MAX_PASSWORD_LENGTH = 128;

export function validatePassword(password: string): void {
  if (!password || password.length === 0) {
    throw new ValidationError('MISSING_PASSWORD', 'Password is required', 'password');
  }
  if (password.length < MIN_PASSWORD_LENGTH) {
    throw new ValidationError('PASSWORD_TOO_SHORT', `Password must be at least ${MIN_PASSWORD_LENGTH} characters`, 'password');
  }
  if (password.length > MAX_PASSWORD_LENGTH) {
    throw new ValidationError('PASSWORD_TOO_LONG', `Password must not exceed ${MAX_PASSWORD_LENGTH} characters`, 'password');
  }
}
```

**Alternatives Considered**:
- Create separate user-validation.ts: Unnecessary file, validators.ts already exists
- Password complexity validation (uppercase, numbers, symbols): Out of scope per spec
- Zod schema validation: Overkill for simple length checks
- Common password blacklist: Future enhancement, out of scope

**Rejected Because**: Spec explicitly states "minimum 8 znaków (nic więcej)" - nothing more than length requirement. Consolidating validators improves maintainability.

---

### 5. Database Schema & Migration

**Decision**: Create Knex migration for `user` table following existing patterns

**Rationale**:
- Project uses Knex migrations (see `/server/src/database/migrations/`)
- Existing pattern: `YYYYMMDDHHMMSS_<description>.ts` filename format
- SQLite for development, PostgreSQL-compatible design
- Knex migration commands already configured in `/server/package.json`

**Schema Design**:
```sql
CREATE TABLE user (
  id TEXT PRIMARY KEY NOT NULL,              -- UUID stored as text (SQLite compatible)
  email TEXT UNIQUE NOT NULL,                 -- Unique constraint for duplicate prevention
  password_hash TEXT NOT NULL,                -- Scrypt hash (salt:key format)
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_user_email ON user(email);  -- Index for duplicate checking performance
```

**Migration File**: `/server/src/database/migrations/YYYYMMDDHHMMSS_create_user_table.ts`

**Alternatives Considered**:
- UUID column type: Not supported in SQLite, TEXT is standard
- VARCHAR(254) for email: TEXT is SQLite standard, CHECK constraint for length
- Separate salt column: Scrypt implementation stores salt:key in single string

**Rejected Because**: TEXT is SQLite standard, existing announcement table uses TEXT for IDs, maintains consistency.

---

### 6. Error Response Format

**Decision**: Reuse existing `CustomError` and error handler middleware

**Rationale**:
- Project has standardized error format in `/server/src/lib/errors.ts`:
  ```json
  {
    "error": {
      "requestId": "string",
      "code": "ERROR_CODE",
      "message": "description",
      "field": "optional"
    }
  }
  ```
- `CustomError` base class + subclasses (`ValidationError`, etc.)
- Error handler middleware in `/server/src/middlewares/error-handler-middleware.ts`
- Consistent with all existing endpoints

**Error Codes for User Registration**:
- `MISSING_EMAIL` - Email field missing or empty
- `EMAIL_TOO_LONG` - Email exceeds 254 characters
- `INVALID_EMAIL_FORMAT` - Email fails RFC 5322 validation
- `MISSING_PASSWORD` - Password field missing or empty
- `PASSWORD_TOO_SHORT` - Password < 8 characters
- `PASSWORD_TOO_LONG` - Password > 128 characters
- `EMAIL_ALREADY_EXISTS` - Duplicate email (HTTP 409)

**Alternatives Considered**:
- Custom error format for user endpoints: Inconsistent with existing API
- Plain text errors: Not structured, hard to parse client-side

**Rejected Because**: Consistency across API is critical for client integration.

---

### 7. Race Condition Handling

**Decision**: Database UNIQUE constraint on email column + catch database error

**Rationale**:
- Spec clarification: "Database unique constraint on email column"
- Atomic at database level (handles concurrent requests)
- Knex will throw error on constraint violation
- Service layer catches error and returns HTTP 409
- No application-level locking needed

**Implementation Pattern**:
```typescript
try {
  await userRepository.create({ id, email, passwordHash, createdAt, updatedAt });
  return res.status(201).json({ id });
} catch (error) {
  if (isUniqueConstraintError(error)) {
    throw new ConflictError('EMAIL_ALREADY_EXISTS', 'Email address is already registered');
  }
  throw error;
}
```

**Alternatives Considered**:
- Pessimistic locking: Overkill, reduces concurrency
- Application-level duplicate check: Race condition remains
- Optimistic locking: Complex, not needed for simple create

**Rejected Because**: UNIQUE constraint is standard database feature, atomic, performant.

---

### 8. Email Normalization

**Decision**: Normalize to lowercase before storage and duplicate checking

**Rationale**:
- Spec requirement: "normalize email addresses for consistency (e.g., lowercase)" (FR-015)
- Case-insensitive uniqueness (User@Example.COM === user@example.com)
- Simple implementation: `email.toLowerCase().trim()`
- Consistent with email standards (local-part can be case-sensitive but most providers ignore it)

**Implementation**:
```typescript
const normalizedEmail = email.toLowerCase().trim();
```

**Alternatives Considered**:
- Case-insensitive collation in database: SQLite doesn't support CITEXT, complex
- Store original + normalized: Unnecessary complexity
- Punycode for international domains: Future enhancement, out of scope

**Rejected Because**: Lowercase normalization is simple, effective, and spec-approved.

---

## Technology Stack Summary

**Confirmed Stack** (no new dependencies required):
- **Runtime**: Node.js v24 (LTS)
- **Language**: TypeScript (strict mode)
- **Framework**: Express.js
- **Database**: Knex + SQLite (PostgreSQL-compatible)
- **Password Hashing**: scrypt (via existing `/lib/password-management.ts`)
- **UUID Generation**: `uuid@^13.0.0` (already installed)
- **Testing**: Vitest + SuperTest
- **Validation**: Custom validators (regex + length checks)

**No New Dependencies**: ✅ All requirements met with existing packages

---

## Best Practices Applied

1. **Security**:
   - Scrypt with proper salting (16 bytes)
   - Password length limits prevent DoS
   - Email normalization prevents case-sensitive duplicates
   - Database unique constraint ensures atomicity

2. **Testing**:
   - TDD workflow (tests before implementation)
   - 80% coverage target
   - Unit tests for validation, service logic
   - Integration tests for HTTP endpoint

3. **Code Quality**:
   - Validation logic extracted to `/lib` (reusable, testable)
   - Service layer for business logic
   - Repository pattern for data access
   - Clean Code principles (single responsibility, DRY)

4. **Consistency**:
   - Follows existing error response format
   - Matches existing migration patterns
   - Reuses existing scrypt implementation
   - Consistent with existing repository pattern

---

## Open Questions

**None** - All technical unknowns resolved through codebase analysis and spec clarifications.

---

## References

- Existing scrypt implementation: `/server/src/lib/password-management.ts`
- Existing error handling: `/server/src/lib/errors.ts`
- Existing migration pattern: `/server/src/database/migrations/20251119154714_create_announcement_table.ts`
- Existing repository pattern: `/server/src/database/repositories/announcement-repository.ts`
- Existing validation pattern: `/server/src/lib/announcement-validation.ts`

