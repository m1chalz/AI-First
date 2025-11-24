# Research: Create Announcement Endpoint

**Feature**: POST `/api/v1/announcements` endpoint  
**Generated**: 2025-11-24  
**Status**: Complete

## Technical Decisions

### 1. Text Sanitization (XSS Prevention)

**Decision**: Use `xss` npm package for HTML sanitization

**Rationale**:
- Simple and lightweight (focused on XSS prevention)
- Battle-tested with 1M+ weekly downloads
- Easy to configure for stripping all HTML tags
- Minimal API surface (keep it simple)

**Configuration**:
```typescript
import xss from 'xss';

export function sanitizeText(input: string): string {
  return xss(input, {
    whiteList: {},          // No HTML tags allowed
    stripIgnoreTag: true,   // Strip all unrecognized tags
    stripIgnoreTagBody: false // Keep text content
  });
}
```

**Alternatives Considered**:
- `sanitize-html` - more complex configuration, overkill for simple text stripping
- `DOMPurify` - requires DOM environment, not suitable for Node.js backend
- Manual regex - error-prone, security risks with complex HTML edge cases

---

### 2. Password Hashing

**Decision**: Use Node.js built-in `scrypt` for management_password hashing

**Rationale**:
- Built into Node.js crypto module (no external dependencies)
- Cryptographically secure (OWASP recommended)
- Memory-hard algorithm (resistant to brute-force attacks)
- Simple API with built-in salt generation
- No native bindings or compilation needed

**Implementation**:
```typescript
import { scrypt, randomBytes, timingSafeEqual } from 'crypto';
import { promisify } from 'util';

const scryptAsync = promisify(scrypt);
const SALT_LENGTH = 16; // 128 bits
const KEY_LENGTH = 64; // 512 bits

export async function hashPassword(plainPassword: string): Promise<string> {
  const salt = randomBytes(SALT_LENGTH);
  const derivedKey = await scryptAsync(plainPassword, salt, KEY_LENGTH) as Buffer;
  return salt.toString('hex') + ':' + derivedKey.toString('hex');
}

export async function verifyPassword(plainPassword: string, hash: string): Promise<boolean> {
  const [saltHex, keyHex] = hash.split(':');
  const salt = Buffer.from(saltHex, 'hex');
  const originalKey = Buffer.from(keyHex, 'hex');
  const derivedKey = await scryptAsync(plainPassword, salt, KEY_LENGTH) as Buffer;
  return timingSafeEqual(originalKey, derivedKey);
}
```

**Alternatives Considered**:
- `bcrypt` - requires external dependency and native bindings
- `argon2` - requires native bindings (deployment complexity)
- Plain storage - rejected (violates security best practices)

---

### 3. Password Generation

**Decision**: Use Node.js built-in `crypto.randomInt()` for 6-digit password generation

**Rationale**:
- No external dependencies required (built-in since Node.js v14.10.0)
- Cryptographically secure random number generation (CSPRNG)
- Simple API for bounded integer generation
- Passwords need not be globally unique (tied to announcement ID)

**Implementation**:
```typescript
import { randomInt } from 'crypto';

export function generateManagementPassword(): string {
  // Generate random 6-digit number (100000 to 999999)
  const password = randomInt(100000, 1000000);
  return password.toString();
}
```

**Alternatives Considered**:
- `uuid` - overkill for 6-digit numeric passwords
- `nanoid` - designed for URL-safe IDs, not numeric passwords
- `Math.random()` - NOT cryptographically secure (rejected)

---

### 4. URL Validation

**Decision**: Use native Node.js `URL` constructor for photoUrl validation

**Rationale**:
- No external dependencies required (built-in)
- WHATWG URL Standard compliant
- Throws on invalid URLs (easy error handling)
- Protocol validation via `url.protocol` property

**Implementation**:
```typescript
export function isValidHttpUrl(urlString: string): boolean {
  try {
    const url = new URL(urlString);
    return url.protocol === 'http:' || url.protocol === 'https:';
  } catch {
    return false; // Invalid URL format
  }
}
```

**Alternatives Considered**:
- `validator.js` - adds unnecessary dependency for single URL check
- Regex patterns - complex, error-prone for URL edge cases
- Third-party URL parsers - unnecessary complexity

---

### 5. Email and Phone Validation

**Decision**: Reuse existing validators in `/server/src/lib/`

**Rationale**:
- Spec explicitly states these are already implemented and tested
- Consistency with existing validation patterns in codebase
- No research needed - use `validateEmail()` and `validatePhone()` from `/server/src/lib/validators.ts`

**Assumption Validation**:
- Verify existing validators accept standard formats:
  - Email: RFC 5322 compliant (basic pattern: `user@domain.tld`)
  - Phone: International format support (E.164 standard recommended)

---

### 6. Request Payload Size Limiting

**Decision**: Use Express built-in `express.json({ limit })` middleware

**Rationale**:
- No external dependencies required (built-in Express feature)
- Simple configuration at middleware level
- Automatic HTTP 413 response on payload exceeding limit
- Applied globally or per-route as needed

**Implementation**:
```typescript
import express from 'express';

const app = express();

// Global 10 MB limit for all JSON requests
app.use(express.json({ limit: '10mb' }));

// Custom error handler for payload too large
app.use((err: any, req: Request, res: Response, next: NextFunction) => {
  if (err.type === 'entity.too.large') {
    return res.status(413).json({
      error: {
        code: 'PAYLOAD_TOO_LARGE',
        message: 'Request payload exceeds maximum size limit'
      }
    });
  }
  next(err);
});
```

**Alternatives Considered**:
- `body-parser` with size limit - deprecated, Express now has built-in JSON parsing
- Custom middleware - unnecessary complexity, reinvents built-in functionality
- Third-party rate limiting - out of scope (spec defers to infrastructure layer)

---

### 7. PII Redaction for Logging

**Decision**: Implement custom PII redaction utility in `/server/src/lib/pii-redaction.ts`

**Rationale**:
- No existing npm package provides the exact redaction format specified
- Simple pure function (easy to test, no dependencies)
- Customizable to project-specific privacy requirements
- Follows spec requirements exactly (last 3 digits for phone, first letter + @domain for email)

**Implementation**:
```typescript
/**
 * Redacts phone number to show only last 3 digits.
 * Example: "+1-555-123-4567" → "***-***-*567"
 */
export function redactPhone(phone: string): string {
  if (phone.length < 3) return '***';
  const lastThree = phone.slice(-3);
  return '***-***-*' + lastThree;
}

/**
 * Redacts email to show only first letter and domain.
 * Example: "john@example.com" → "j***@example.com"
 */
export function redactEmail(email: string): string {
  const [localPart, domain] = email.split('@');
  if (!localPart || !domain) return '***@***';
  const firstChar = localPart[0];
  return `${firstChar}***@${domain}`;
}
```

**Alternatives Considered**:
- Full redaction (e.g., `***@***`) - too restrictive, loses debugging context
- Partial masking libraries - overkill for two simple functions
- No redaction - violates privacy requirements in spec

---

### 8. Database Schema Design

**Decision**: Add new columns to existing `announcement` table (if exists) or create new table

**Rationale**:
- SQLite → PostgreSQL migration path requires careful schema design
- Use `TEXT` type for all string fields (as per spec: no length constraints)
- Use `REAL` type for coordinates (supports decimal values)
- Use `INTEGER` type for age (positive integer validation at app layer)
- Store management_password as hashed value (never plain text)

**Schema Design**:
```sql
CREATE TABLE IF NOT EXISTS announcement (
  id TEXT PRIMARY KEY,                   -- UUID v4
  pet_name TEXT,                         -- Optional
  species TEXT NOT NULL,                 -- Required
  breed TEXT,                            -- Optional
  sex TEXT NOT NULL,                     -- Required (no length limit)
  age INTEGER,                           -- Optional (positive integer, app-validated)
  description TEXT,                      -- Optional
  microchip_number TEXT,                 -- Optional (numeric only, app-validated, unique)
  location_city TEXT,                    -- Optional
  location_latitude REAL NOT NULL,       -- Required (decimal, -90 to 90, app-validated)
  location_longitude REAL NOT NULL,      -- Required (decimal, -180 to 180, app-validated)
  location_radius INTEGER,               -- Optional (kilometers)
  last_seen_date TEXT NOT NULL,          -- Required (ISO 8601 date, app-validated)
  email TEXT,                            -- Optional (at least one contact required)
  phone TEXT,                            -- Optional (at least one contact required)
  photo_url TEXT NOT NULL,               -- Required (URL format, app-validated)
  status TEXT NOT NULL,                  -- Required (MISSING or FOUND, app-validated)
  reward TEXT,                           -- Optional
  management_password_hash TEXT NOT NULL, -- Hashed password (bcrypt)
  created_at TEXT NOT NULL DEFAULT (datetime('now')), -- Timestamp
  UNIQUE(microchip_number)               -- Prevent duplicate microchip numbers
);
```

**Migration Considerations**:
- All validation at application layer (no CHECK constraints for SQLite → PostgreSQL portability)
- No ENUM types (store as TEXT, validate in code)
- Use `IF EXISTS` / `IF NOT EXISTS` for idempotent migrations
- Singular table name (`announcement`, not `announcements`) per constitution

---

### 9. Validation Strategy

**Decision**: Fail-fast validation with field-specific error codes

**Rationale**:
- Spec requires fail-fast (return first error only)
- Each validation error must specify field and code
- Validation order matters (check required fields first, then formats)

**Validation Order**:
1. Unknown fields check (reject unrecognized properties)
2. Required fields presence (species, sex, lastSeenDate, photoUrl, status, coordinates)
3. Contact method presence (email OR phone)
4. Whitespace-only field detection
5. Format validation (email, phone, URL, date, coordinates, microchip, age, status)
6. Range validation (coordinates, age > 0)
7. Future date validation (lastSeenDate)
8. Duplicate microchip check (database query)

**Error Response Format**:
```typescript
// Validation error
{
  error: {
    code: "NOT_EMPTY" | "INVALID_FORMAT" | "MISSING_CONTACT" | "INVALID_FIELD",
    message: "descriptive error message",
    field: "fieldName"
  }
}

// Conflict error
{
  error: {
    code: "CONFLICT",
    message: "An entity with this value already exists",
    field: "microchipNumber"
  }
}

// System error (no field property)
{
  error: {
    code: "INTERNAL_SERVER_ERROR",
    message: "Internal server error"
  }
}
```

---

### 10. Input Validation Library

**Decision**: Use `Zod` for schema-based input validation

**Rationale**:
- TypeScript-first validation library (excellent type inference)
- Zero dependencies (lightweight)
- Excellent error messages with path information (perfect for fail-fast validation)
- Declarative schema definition (self-documenting)
- Built-in async validation support
- More idiomatic for TypeScript projects than Joi
- Eliminates manual validation code (DRY principle)

**Implementation Approach**:
```typescript
import { z } from 'zod';

const CreateAnnouncementSchema = z.object({
  petName: z.string().trim().min(1).optional(),
  species: z.string().trim().min(1, { message: 'cannot be empty' }),
  sex: z.string().trim().min(1, { message: 'cannot be empty' }),
  email: z.string().email({ message: 'invalid email format' }).optional(),
  phone: z.string().regex(/^\+?[\d\s-()]+$/, { message: 'invalid phone format' }).optional(),
  locationLatitude: z.number().min(-90).max(90, { message: 'latitude must be between -90 and 90' }),
  locationLongitude: z.number().min(-180).max(180, { message: 'longitude must be between -180 and 180' }),
  status: z.enum(['MISSING', 'FOUND'], { message: 'status must be either MISSING or FOUND' }),
  photoUrl: z.string().url({ message: 'must be a valid URL with http or https protocol' }),
  lastSeenDate: z.string().regex(/^\d{4}-\d{2}-\d{2}$/).refine(
    (date) => new Date(date) <= new Date(),
    { message: 'lastSeenDate cannot be in the future' }
  ),
  microchipNumber: z.string().regex(/^\d+$/, { message: 'must contain only digits' }).optional(),
  age: z.number().int().positive({ message: 'age must be a positive integer' }).optional(),
  // ... more fields
}).strict() // Reject unknown fields automatically
  .refine(data => data.email || data.phone, {
    message: 'at least one contact method (email or phone) is required',
    path: ['contact']
  });

// Usage with fail-fast error handling
try {
  const validated = CreateAnnouncementSchema.parse(requestBody);
} catch (error) {
  if (error instanceof z.ZodError) {
    const firstError = error.issues[0]; // Fail-fast: take first error
    throw new ValidationError(
      mapZodErrorCode(firstError.code), // Map to our error codes
      firstError.message,
      firstError.path.join('.') || undefined
    );
  }
}
```

**Benefits**:
- Type safety: TypeScript types automatically inferred from schema
- Built-in support for:
  - Required/optional fields
  - String trimming and whitespace handling
  - Numeric range validation
  - Enum validation
  - Regex pattern matching
  - Custom refinements (e.g., contact method check, future date check)
  - Unknown field rejection (`.strict()`)
- Single source of truth for validation rules
- Reduces boilerplate validation code by ~70%

**Alternatives Considered**:
- `Joi` - More mature but less TypeScript-friendly, larger bundle size, no type inference
- Manual validation - Error-prone, verbose, harder to maintain (current approach)
- `class-validator` - Requires decorators and classes (less idiomatic for functional approach)

---

### 11. Unknown Field Detection

**Decision**: Use Zod's `.strict()` mode for automatic unknown field rejection

**Rationale**:
- Spec requires strict validation (reject unknown fields with HTTP 400)
- Security best practice (prevents injection attacks via unexpected fields)
- Clear contract enforcement (API version stability)
- Zod handles this automatically with `.strict()` modifier

**Note**: With Zod schema validation, explicit field whitelist checking is no longer needed. Zod's `.strict()` mode will automatically reject unknown fields and provide appropriate error messages.

---

## Technology Stack Summary

| Component | Technology | Rationale |
|-----------|-----------|-----------|
| **Runtime** | Node.js v24 (LTS) | Constitution requirement |
| **Framework** | Express.js | Constitution requirement |
| **Language** | TypeScript (strict mode) | Constitution requirement |
| **Database** | SQLite (dev) → PostgreSQL (prod) | Constitution requirement |
| **Query Builder** | Knex | Constitution requirement |
| **Testing** | Vitest + SuperTest | Constitution requirement |
| **Input Validation** | `Zod` | TypeScript-first, type inference, declarative schemas |
| **Text Sanitization** | `xss` | Simple, lightweight XSS prevention |
| **Password Hashing** | Node.js `scrypt` (crypto module) | Built-in, cryptographically secure, no dependencies |
| **Password Generation** | Node.js `crypto.randomInt()` | Built-in, cryptographically secure |
| **URL Validation** | Zod URL schema | Built into validation library |
| **Email/Phone Validation** | Zod email/regex schemas | Built into validation library |
| **Payload Size Limiting** | Express `express.json({ limit })` | Built-in, automatic HTTP 413 |
| **PII Redaction** | Custom utility in `/server/src/lib/` | Project-specific requirements |
| **Linting** | ESLint + @typescript-eslint | Constitution requirement |

---

## New Dependencies Required

**Production**:
```json
{
  "xss": "^1.0.14",    // XSS prevention (text sanitization)
  "zod": "^3.22.4"     // Input validation with TypeScript type inference
}
```

**Rationale for Each Dependency**:
- **xss**: Required for XSS prevention (FR-030). Lightweight and focused on XSS attacks. 1M+ weekly downloads, actively maintained, zero security vulnerabilities. Simpler API than sanitize-html.
- **zod**: Required for comprehensive input validation (FR-012, FR-013, FR-014, FR-015, etc.). Zero dependencies, TypeScript-first design with excellent type inference. Eliminates ~70% of manual validation code. 5M+ weekly downloads.

**Built-in Node.js Modules** (no external dependencies):
- **scrypt** (crypto module): Password hashing (FR-029). Built-in, cryptographically secure, no compilation needed.
- **randomInt** (crypto module): Password generation. Built-in, cryptographically secure.

**Development** (no new dependencies required):
- Vitest and SuperTest already available in `/server` for testing
- ESLint already configured for code quality

**Total New Dependencies**: 2 production packages (both lightweight, zero sub-dependencies for Zod, minimal for xss)

---

## Best Practices & Patterns

### 1. Service Layer Separation

**Pattern**: Thin routes, fat services

```typescript
// routes/announcements.ts - HTTP concerns only
router.post('/announcements', async (req, res, next) => {
  try {
    const announcement = await createAnnouncementService(req.body);
    res.status(201).json(announcement);
  } catch (error) {
    next(error); // Delegate to error middleware
  }
});

// services/announcement-service.ts - Business logic
export async function createAnnouncementService(data: CreateAnnouncementDto) {
  // Validation
  // Sanitization
  // Database operations
  // Password generation
  // Return result
}
```

**Rationale**:
- Routes remain thin and testable via integration tests
- Services contain business logic and are unit-testable in isolation
- Clear separation of concerns (HTTP vs domain logic)

### 2. Validation Middleware

**Pattern**: Reusable validation middleware for common checks

```typescript
// middlewares/validation.ts
export function validateRequestBody(allowedFields: string[]) {
  return (req: Request, res: Response, next: NextFunction) => {
    const unknownField = Object.keys(req.body).find(
      field => !allowedFields.includes(field)
    );
    
    if (unknownField) {
      return res.status(400).json({
        error: {
          code: 'INVALID_FIELD',
          message: `${unknownField} is not a valid field`,
          field: unknownField
        }
      });
    }
    
    next();
  };
}
```

**Rationale**:
- Reusable across multiple endpoints
- Fail-fast at middleware layer (before service logic)
- Easy to unit test independently

### 3. Error Handling

**Pattern**: Custom error classes with Express error middleware

```typescript
// lib/errors.ts
export class ValidationError extends Error {
  constructor(
    public code: string,
    public message: string,
    public field?: string
  ) {
    super(message);
    this.name = 'ValidationError';
  }
}

export class ConflictError extends Error {
  constructor(
    public message: string,
    public field: string
  ) {
    super(message);
    this.name = 'ConflictError';
  }
}

// middlewares/error-handler.ts
export function errorHandler(err: Error, req: Request, res: Response, next: NextFunction) {
  if (err instanceof ValidationError) {
    return res.status(400).json({
      error: {
        code: err.code,
        message: err.message,
        field: err.field
      }
    });
  }
  
  if (err instanceof ConflictError) {
    return res.status(409).json({
      error: {
        code: 'CONFLICT',
        message: err.message,
        field: err.field
      }
    });
  }
  
  // Unexpected errors (FR-019)
  console.error('Unexpected error:', err);
  return res.status(500).json({
    error: {
      code: 'INTERNAL_SERVER_ERROR',
      message: 'Internal server error'
    }
  });
}
```

**Rationale**:
- Centralized error handling (DRY principle)
- Consistent error response format across all endpoints
- Clear separation between expected (validation) and unexpected (system) errors
- Easy to unit test error classes and middleware

---

## Open Questions (RESOLVED)

All technical unknowns from the feature spec have been resolved through this research phase. No clarifications needed before proceeding to Phase 1 (data model and contracts).

---

## Next Steps (Phase 1)

1. ✅ Generate `data-model.md` with complete database schema and validation rules
2. ✅ Generate OpenAPI contract in `/contracts/openapi.yaml` for POST endpoint
3. ✅ Generate `quickstart.md` with implementation guide for developers
4. ✅ Update agent context (`.specify/memory/agent-cursor.md`) with new technologies

