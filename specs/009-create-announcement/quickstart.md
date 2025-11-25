# Quickstart: Create Announcement Endpoint

**Feature**: POST `/api/v1/announcements` endpoint  
**Generated**: 2025-11-24  
**Status**: Implementation Guide

## Overview

This guide walks you through implementing the create announcement endpoint from scratch using Test-Driven Development (TDD). You'll build:

1. **Database layer**: Migration and repository for announcement persistence
2. **Utilities**: Text sanitization, password management, PII redaction
3. **Service layer**: Business logic with validation
4. **Route layer**: Express endpoint handler
5. **Tests**: Unit tests for services and integration tests for the endpoint

**Total Implementation Time**: ~6-8 hours (including tests)

---

## Prerequisites

Before starting, ensure:

- [ ] Node.js v24 (LTS) installed
- [ ] Backend server running (`cd server && npm run dev`)
- [ ] Dependencies installed (`cd server && npm install`)
- [ ] ESLint configured (`npm run lint` passes)
- [ ] Database setup complete (SQLite development database)

---

## Phase 1: Dependencies Installation

### Add New Dependencies

```bash
cd server
npm install xss zod
```

**Rationale**:
- `xss`: Simple XSS prevention (FR-030) - lightweight and focused
- `zod`: TypeScript-first validation library - eliminates manual validation code

**Note**: No additional dependencies needed for password hashing. We'll use Node.js built-in `scrypt` from the crypto module.

### Verify Dependencies

```bash
npm list xss zod
# Should show versions: xss@^1.0.14, zod@^3.22.4
```

---

## Phase 2: Database Migration

### Step 1: Create Migration

```bash
cd server
npm run knex:add-migration create-announcement-table
```

**File Created**: `server/src/database/migrations/YYYYMMDDHHMMSS_create-announcement-table.ts`

### Step 2: Write Migration

```typescript
// server/src/database/migrations/YYYYMMDDHHMMSS_create-announcement-table.ts
import { Knex } from 'knex';

export async function up(knex: Knex): Promise<void> {
  await knex.schema.createTable('announcement', (table) => {
    // Primary Key
    table.text('id').primary();
    
    // Pet Information
    table.text('pet_name').nullable();
    table.text('species').notNullable();
    table.text('breed').nullable();
    table.text('sex').notNullable();
    table.integer('age').nullable();
    table.text('description').nullable();
    table.text('microchip_number').nullable().unique();
    
    // Location Information
    table.text('location_city').nullable();
    table.real('location_latitude').notNullable();
    table.real('location_longitude').notNullable();
    table.integer('location_radius').nullable();
    
    // Contact Information
    table.text('email').nullable();
    table.text('phone').nullable();
    
    // Additional Information
    table.text('photo_url').notNullable();
    table.text('last_seen_date').notNullable();
    table.text('status').notNullable();
    table.text('reward').nullable();
    
    // Security
    table.text('management_password_hash').notNullable();
    
    // Timestamps
    table.text('created_at').notNullable().defaultTo(knex.raw("(datetime('now'))"));
  });
}

export async function down(knex: Knex): Promise<void> {
  await knex.schema.dropTableIfExists('announcement');
}
```

### Step 3: Run Migration

```bash
npm run knex:migrate
```

**Expected Output**: `Batch 1 run: 1 migrations`

### Step 4: Verify Migration

```bash
sqlite3 pets.db ".schema announcement"
```

**Expected**: Table schema with all columns and indexes

---

## Phase 3: Utility Functions (TDD Approach)

### Step 1: Text Sanitization

**RED** - Write failing test first:

```typescript
// server/src/lib/__test__/text-sanitization.test.ts
import { describe, it, expect } from 'vitest';
import { sanitizeText } from '../text-sanitization';

describe('sanitizeText', () => {
  it('should strip all HTML tags from input', () => {
    // Given
    const input = '<script>alert("xss")</script>Hello';
    
    // When
    const result = sanitizeText(input);
    
    // Then
    expect(result).toBe('Hello');
  });
  
  it('should preserve plain text without HTML', () => {
    // Given
    const input = 'Plain text with special chars: @#$%';
    
    // When
    const result = sanitizeText(input);
    
    // Then
    expect(result).toBe('Plain text with special chars: @#$%');
  });
  
  it('should handle empty string', () => {
    // Given
    const input = '';
    
    // When
    const result = sanitizeText(input);
    
    // Then
    expect(result).toBe('');
  });
});
```

**Run test** (should FAIL):
```bash
npm test -- text-sanitization.test.ts
```

**GREEN** - Write minimal implementation:

```typescript
// server/src/lib/text-sanitization.ts
import xss from 'xss';

/**
 * Sanitizes text by stripping all HTML tags (XSS prevention).
 * Preserves plain text and special characters.
 */
export function sanitizeText(input: string): string {
  return xss(input, {
    whiteList: {},          // No HTML tags allowed
    stripIgnoreTag: true,   // Strip all unrecognized tags
    stripIgnoreTagBody: false // Keep text content
  });
}
```

**Run test** (should PASS):
```bash
npm test -- text-sanitization.test.ts
```

**REFACTOR**: No refactoring needed (implementation is already clean).

---

### Step 2: Password Management

**RED** - Write failing tests:

```typescript
// server/src/lib/__test__/password-management.test.ts
import { describe, it, expect } from 'vitest';
import { generateManagementPassword, hashPassword, verifyPassword } from '../password-management';

describe('generateManagementPassword', () => {
  it('should generate a 6-digit numeric password', () => {
    // When
    const password = generateManagementPassword();
    
    // Then
    expect(password).toMatch(/^\d{6}$/);
    expect(parseInt(password, 10)).toBeGreaterThanOrEqual(100000);
    expect(parseInt(password, 10)).toBeLessThan(1000000);
  });
  
  it('should generate unique passwords on multiple calls', () => {
    // When
    const password1 = generateManagementPassword();
    const password2 = generateManagementPassword();
    const password3 = generateManagementPassword();
    
    // Then (not all three should be identical)
    const allSame = password1 === password2 && password2 === password3;
    expect(allSame).toBe(false);
  });
});

describe('hashPassword', () => {
  it('should hash password using bcrypt', async () => {
    // Given
    const plainPassword = '123456';
    
    // When
    const hash = await hashPassword(plainPassword);
    
    // Then
    expect(hash).toBeDefined();
    expect(hash).not.toBe(plainPassword);
    expect(hash.startsWith('$2b$')).toBe(true); // bcrypt signature
  });
});

describe('verifyPassword', () => {
  it('should return true for correct password', async () => {
    // Given
    const plainPassword = '123456';
    const hash = await hashPassword(plainPassword);
    
    // When
    const isValid = await verifyPassword(plainPassword, hash);
    
    // Then
    expect(isValid).toBe(true);
  });
  
  it('should return false for incorrect password', async () => {
    // Given
    const plainPassword = '123456';
    const wrongPassword = '654321';
    const hash = await hashPassword(plainPassword);
    
    // When
    const isValid = await verifyPassword(wrongPassword, hash);
    
    // Then
    expect(isValid).toBe(false);
  });
});
```

**Run test** (should FAIL):
```bash
npm test -- password-management.test.ts
```

**GREEN** - Write minimal implementation:

```typescript
// server/src/lib/password-management.ts
import { scrypt, randomBytes, randomInt, timingSafeEqual } from 'crypto';
import { promisify } from 'util';

const scryptAsync = promisify(scrypt);
const SALT_LENGTH = 16; // 128 bits
const KEY_LENGTH = 64; // 512 bits

/**
 * Generates a random 6-digit numeric management password.
 * Passwords are not guaranteed to be globally unique.
 */
export function generateManagementPassword(): string {
  const password = randomInt(100000, 1000000);
  return password.toString();
}

/**
 * Hashes a plain text password using scrypt (Node.js crypto module).
 * Returns salt and hash combined as 'salt:hash' format.
 */
export async function hashPassword(plainPassword: string): Promise<string> {
  const salt = randomBytes(SALT_LENGTH);
  const derivedKey = await scryptAsync(plainPassword, salt, KEY_LENGTH) as Buffer;
  return salt.toString('hex') + ':' + derivedKey.toString('hex');
}

/**
 * Verifies a plain text password against a scrypt hash.
 * Uses timing-safe comparison to prevent timing attacks.
 */
export async function verifyPassword(
  plainPassword: string,
  hash: string
): Promise<boolean> {
  const [saltHex, keyHex] = hash.split(':');
  const salt = Buffer.from(saltHex, 'hex');
  const originalKey = Buffer.from(keyHex, 'hex');
  const derivedKey = await scryptAsync(plainPassword, salt, KEY_LENGTH) as Buffer;
  return timingSafeEqual(originalKey, derivedKey);
}
```

**Run test** (should PASS):
```bash
npm test -- password-management.test.ts
```

---

### Step 3: PII Redaction

**RED** - Write failing tests:

```typescript
// server/src/lib/__test__/pii-redaction.test.ts
import { describe, it, expect } from 'vitest';
import { redactPhone, redactEmail } from '../pii-redaction';

describe('redactPhone', () => {
  it('should show only last 3 digits of phone number', () => {
    // Given
    const phone = '+1-555-123-4567';
    
    // When
    const redacted = redactPhone(phone);
    
    // Then
    expect(redacted).toBe('***-***-*567');
  });
  
  it('should handle short phone numbers', () => {
    // Given
    const phone = '12';
    
    // When
    const redacted = redactPhone(phone);
    
    // Then
    expect(redacted).toBe('***');
  });
});

describe('redactEmail', () => {
  it('should show only first letter and domain', () => {
    // Given
    const email = 'john@example.com';
    
    // When
    const redacted = redactEmail(email);
    
    // Then
    expect(redacted).toBe('j***@example.com');
  });
  
  it('should handle email without @ symbol', () => {
    // Given
    const email = 'invalidemail';
    
    // When
    const redacted = redactEmail(email);
    
    // Then
    expect(redacted).toBe('***@***');
  });
});
```

**GREEN** - Write minimal implementation:

```typescript
// server/src/lib/pii-redaction.ts

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
  const parts = email.split('@');
  if (parts.length !== 2) return '***@***';
  
  const [localPart, domain] = parts;
  if (!localPart || !domain) return '***@***';
  
  const firstChar = localPart[0];
  return `${firstChar}***@${domain}`;
}
```

**Run tests** (should PASS):
```bash
npm test -- pii-redaction.test.ts
```

---

### Step 4: URL Validation

**RED** - Write failing tests:

```typescript
// server/src/lib/__test__/url-validation.test.ts
import { describe, it, expect } from 'vitest';
import { isValidHttpUrl } from '../url-validation';

describe('isValidHttpUrl', () => {
  it('should return true for valid http URL', () => {
    expect(isValidHttpUrl('http://example.com')).toBe(true);
  });
  
  it('should return true for valid https URL', () => {
    expect(isValidHttpUrl('https://example.com/photo.jpg')).toBe(true);
  });
  
  it('should return false for ftp protocol', () => {
    expect(isValidHttpUrl('ftp://example.com')).toBe(false);
  });
  
  it('should return false for invalid URL format', () => {
    expect(isValidHttpUrl('not-a-url')).toBe(false);
  });
  
  it('should return false for empty string', () => {
    expect(isValidHttpUrl('')).toBe(false);
  });
});
```

**GREEN** - Write minimal implementation:

```typescript
// server/src/lib/url-validation.ts

/**
 * Validates URL format and ensures http/https protocol.
 */
export function isValidHttpUrl(urlString: string): boolean {
  try {
    const url = new URL(urlString);
    return url.protocol === 'http:' || url.protocol === 'https:';
  } catch {
    return false;
  }
}
```

**Run tests** (should PASS):
```bash
npm test -- url-validation.test.ts
```

---

## Phase 4: Error Classes

```typescript
// server/src/lib/errors.ts

/**
 * Validation error for user input failures.
 */
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

/**
 * Conflict error for duplicate entity violations.
 */
export class ConflictError extends Error {
  constructor(
    public message: string,
    public field: string
  ) {
    super(message);
    this.name = 'ConflictError';
  }
}
```

---

## Phase 5: Service Layer (TDD Approach)

### Step 1: Type Definitions

```typescript
// server/src/types/announcement.ts

export interface CreateAnnouncementDto {
  // Pet Information
  petName?: string;
  species: string;
  breed?: string;
  sex: string;
  age?: number;
  description?: string;
  microchipNumber?: string;
  
  // Location Information
  locationCity?: string;
  locationLatitude: number;
  locationLongitude: number;
  locationRadius?: number;
  
  // Contact Information
  email?: string;
  phone?: string;
  
  // Additional Information
  photoUrl: string;
  lastSeenDate: string;
  status: 'MISSING' | 'FOUND';
  reward?: string;
}

export interface AnnouncementDto extends CreateAnnouncementDto {
  id: string;
  createdAt: string;
  managementPassword?: string; // Only in POST response
}
```

### Step 2: Service Tests (RED)

Create comprehensive service tests before implementation:

```typescript
// server/src/services/__test__/announcement-service.test.ts
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { createAnnouncement } from '../announcement-service';
import { ValidationError, ConflictError } from '../../lib/errors';

describe('createAnnouncement', () => {
  // Test 1: Success case with minimal fields
  it('should create announcement with email contact', async () => {
    // Given
    const validData = {
      species: 'Golden Retriever',
      sex: 'MALE',
      lastSeenDate: '2025-11-20',
      photoUrl: 'https://example.com/photo.jpg',
      status: 'MISSING' as const,
      locationLatitude: 40.785091,
      locationLongitude: -73.968285,
      email: 'john@example.com'
    };
    
    // When
    const result = await createAnnouncement(validData);
    
    // Then
    expect(result).toBeDefined();
    expect(result.id).toBeDefined();
    expect(result.managementPassword).toMatch(/^\d{6}$/);
    expect(result.species).toBe('Golden Retriever');
    expect(result.email).toBe('john@example.com');
  });
  
  // Test 2: Validation - missing required field
  it('should throw ValidationError when species is missing', async () => {
    // Given
    const invalidData = {
      sex: 'MALE',
      lastSeenDate: '2025-11-20',
      photoUrl: 'https://example.com/photo.jpg',
      status: 'MISSING' as const,
      locationLatitude: 40.785091,
      locationLongitude: -73.968285,
      email: 'john@example.com'
    } as any;
    
    // When/Then
    await expect(createAnnouncement(invalidData))
      .rejects
      .toThrow(ValidationError);
  });
  
  // Test 3: Validation - missing contact
  it('should throw ValidationError when no contact method provided', async () => {
    // Given
    const invalidData = {
      species: 'Golden Retriever',
      sex: 'MALE',
      lastSeenDate: '2025-11-20',
      photoUrl: 'https://example.com/photo.jpg',
      status: 'MISSING' as const,
      locationLatitude: 40.785091,
      locationLongitude: -73.968285
      // No email or phone
    };
    
    // When/Then
    await expect(createAnnouncement(invalidData))
      .rejects
      .toThrow(ValidationError);
  });
  
  // Test 4: Validation - duplicate microchip
  it('should throw ConflictError when microchip number already exists', async () => {
    // Given - create first announcement
    const data = {
      species: 'Golden Retriever',
      sex: 'MALE',
      lastSeenDate: '2025-11-20',
      photoUrl: 'https://example.com/photo.jpg',
      status: 'MISSING' as const,
      locationLatitude: 40.785091,
      locationLongitude: -73.968285,
      email: 'john@example.com',
      microchipNumber: '123456789'
    };
    
    await createAnnouncement(data);
    
    // When/Then - try to create duplicate
    await expect(createAnnouncement(data))
      .rejects
      .toThrow(ConflictError);
  });
  
  // Add more tests for all validation scenarios...
});
```

### Step 3: Service Implementation (GREEN)

```typescript
// server/src/services/announcement-service.ts
import { v4 as uuidv4 } from 'uuid';
import { CreateAnnouncementDto, AnnouncementDto } from '../types/announcement';
import { ValidationError, ConflictError } from '../lib/errors';
import { sanitizeText } from '../lib/text-sanitization';
import { generateManagementPassword, hashPassword } from '../lib/password-management';
import { isValidHttpUrl } from '../lib/url-validation';
import { validateEmail, validatePhone } from '../lib/validators';
import { getDatabase } from '../database/connection';

const ALLOWED_FIELDS = [
  'petName', 'species', 'breed', 'sex', 'age',
  'description', 'microchipNumber', 'locationCity',
  'locationLatitude', 'locationLongitude', 'locationRadius',
  'lastSeenDate', 'email', 'phone', 'photoUrl',
  'status', 'reward'
];

/**
 * Creates a new pet announcement with validation and sanitization.
 */
export async function createAnnouncement(
  data: CreateAnnouncementDto
): Promise<AnnouncementDto> {
  // 1. Validate unknown fields
  validateAllowedFields(data);
  
  // 2. Validate required fields
  validateRequiredFields(data);
  
  // 3. Validate contact method
  validateContactMethod(data);
  
  // 4. Validate formats
  validateFormats(data);
  
  // 5. Validate ranges
  validateRanges(data);
  
  // 6. Check for duplicate microchip
  if (data.microchipNumber) {
    await checkDuplicateMicrochip(data.microchipNumber);
  }
  
  // 7. Sanitize text fields
  const sanitized = sanitizeTextFields(data);
  
  // 8. Generate management password
  const managementPassword = generateManagementPassword();
  const passwordHash = await hashPassword(managementPassword);
  
  // 9. Insert into database
  const id = uuidv4();
  const db = getDatabase();
  
  await db('announcement').insert({
    id,
    pet_name: sanitized.petName || null,
    species: sanitized.species,
    breed: sanitized.breed || null,
    sex: sanitized.sex,
    age: sanitized.age || null,
    description: sanitized.description || null,
    microchip_number: sanitized.microchipNumber || null,
    location_city: sanitized.locationCity || null,
    location_latitude: sanitized.locationLatitude,
    location_longitude: sanitized.locationLongitude,
    location_radius: sanitized.locationRadius || null,
    last_seen_date: sanitized.lastSeenDate,
    email: sanitized.email || null,
    phone: sanitized.phone || null,
    photo_url: sanitized.photoUrl,
    status: sanitized.status,
    reward: sanitized.reward || null,
    management_password_hash: passwordHash
  });
  
  // 10. Fetch and return created announcement
  const created = await db('announcement')
    .where({ id })
    .first();
  
  return {
    id: created.id,
    petName: created.pet_name,
    species: created.species,
    breed: created.breed,
    sex: created.sex,
    age: created.age,
    description: created.description,
    microchipNumber: created.microchip_number,
    locationCity: created.location_city,
    locationLatitude: created.location_latitude,
    locationLongitude: created.location_longitude,
    locationRadius: created.location_radius,
    lastSeenDate: created.last_seen_date,
    email: created.email,
    phone: created.phone,
    photoUrl: created.photo_url,
    status: created.status,
    reward: created.reward,
    managementPassword, // Only returned once
    createdAt: created.created_at
  };
}

// Helper functions (implement each validation step)...
function validateAllowedFields(data: any): void {
  const fields = Object.keys(data);
  const unknownField = fields.find(field => !ALLOWED_FIELDS.includes(field));
  
  if (unknownField) {
    throw new ValidationError(
      'INVALID_FIELD',
      `${unknownField} is not a valid field`,
      unknownField
    );
  }
}

function validateRequiredFields(data: CreateAnnouncementDto): void {
  const required = [
    'species', 'sex', 'lastSeenDate', 'photoUrl',
    'status', 'locationLatitude', 'locationLongitude'
  ];
  
  for (const field of required) {
    const value = (data as any)[field];
    
    if (value === undefined || value === null) {
      throw new ValidationError('MISSING_VALUE', 'cannot be empty', field);
    }
    
    // Check whitespace-only strings
    if (typeof value === 'string' && value.trim() === '') {
      throw new ValidationError('MISSING_VALUE', 'cannot be empty', field);
    }
  }
}

function validateContactMethod(data: CreateAnnouncementDto): void {
  if (!data.email && !data.phone) {
    throw new ValidationError(
      'MISSING_CONTACT',
      'at least one contact method (email or phone) is required',
      'contact'
    );
  }
}

function validateFormats(data: CreateAnnouncementDto): void {
  // Email format
  if (data.email && !validateEmail(data.email)) {
    throw new ValidationError('INVALID_FORMAT', 'invalid email format', 'email');
  }
  
  // Phone format
  if (data.phone && !validatePhone(data.phone)) {
    throw new ValidationError('INVALID_FORMAT', 'invalid phone format', 'phone');
  }
  
  // Microchip format (numeric only)
  if (data.microchipNumber && !/^\d+$/.test(data.microchipNumber)) {
    throw new ValidationError('INVALID_FORMAT', 'must contain only digits', 'microchipNumber');
  }
  
  // Age format (positive integer)
  if (data.age !== undefined && (!Number.isInteger(data.age) || data.age <= 0)) {
    throw new ValidationError('INVALID_FORMAT', 'age must be a positive integer', 'age');
  }
  
  // Status format
  if (data.status !== 'MISSING' && data.status !== 'FOUND') {
    throw new ValidationError('INVALID_FORMAT', 'status must be either MISSING or FOUND', 'status');
  }
  
  // Photo URL format
  if (!isValidHttpUrl(data.photoUrl)) {
    throw new ValidationError('INVALID_FORMAT', 'must be a valid URL with http or https protocol', 'photoUrl');
  }
  
  // Last seen date format
  validateLastSeenDate(data.lastSeenDate);
}

function validateRanges(data: CreateAnnouncementDto): void {
  // Latitude range
  if (data.locationLatitude < -90 || data.locationLatitude > 90) {
    throw new ValidationError('INVALID_FORMAT', 'latitude must be between -90 and 90', 'locationLatitude');
  }
  
  // Longitude range
  if (data.locationLongitude < -180 || data.locationLongitude > 180) {
    throw new ValidationError('INVALID_FORMAT', 'longitude must be between -180 and 180', 'locationLongitude');
  }
}

function validateLastSeenDate(dateString: string): void {
  const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
  
  if (!dateRegex.test(dateString)) {
    throw new ValidationError('INVALID_FORMAT', 'invalid date format (expected YYYY-MM-DD)', 'lastSeenDate');
  }
  
  const date = new Date(dateString);
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  
  if (date > today) {
    throw new ValidationError('INVALID_FORMAT', 'lastSeenDate cannot be in the future', 'lastSeenDate');
  }
}

async function checkDuplicateMicrochip(microchipNumber: string): Promise<void> {
  const db = getDatabase();
  const existing = await db('announcement')
    .where('microchip_number', microchipNumber)
    .first();
  
  if (existing) {
    throw new ConflictError(
      'An entity with this value already exists',
      'microchipNumber'
    );
  }
}

function sanitizeTextFields(data: CreateAnnouncementDto): CreateAnnouncementDto {
  return {
    ...data,
    petName: data.petName ? sanitizeText(data.petName) : undefined,
    species: sanitizeText(data.species),
    breed: data.breed ? sanitizeText(data.breed) : undefined,
    sex: sanitizeText(data.sex),
    description: data.description ? sanitizeText(data.description) : undefined,
    locationCity: data.locationCity ? sanitizeText(data.locationCity) : undefined,
    reward: data.reward ? sanitizeText(data.reward) : undefined
  };
}
```

**Run tests** (should PASS):
```bash
npm test -- announcement-service.test.ts
```

---

## Phase 6: Route Layer

### Step 1: Express Route

```typescript
// server/src/routes/announcements.ts
import { Router } from 'express';
import { createAnnouncement } from '../services/announcement-service';

const router = Router();

/**
 * POST /api/v1/announcements
 * Creates a new pet announcement
 */
router.post('/announcements', async (req, res, next) => {
  try {
    const announcement = await createAnnouncement(req.body);
    
    // Log with PII redaction (if logging enabled)
    // logAnnouncementCreation(announcement);
    
    res.status(201).json(announcement);
  } catch (error) {
    next(error); // Delegate to error middleware
  }
});

export default router;
```

### Step 2: Error Middleware

```typescript
// server/src/middlewares/error-handler.ts
import { Request, Response, NextFunction } from 'express';
import { ValidationError, ConflictError } from '../lib/errors';

export function errorHandler(
  err: Error,
  req: Request,
  res: Response,
  next: NextFunction
): void {
  // Validation errors (HTTP 400)
  if (err instanceof ValidationError) {
    res.status(400).json({
      error: {
        code: err.code,
        message: err.message,
        field: err.field
      }
    });
    return;
  }
  
  // Conflict errors (HTTP 409)
  if (err instanceof ConflictError) {
    res.status(409).json({
      error: {
        code: 'CONFLICT',
        message: err.message,
        field: err.field
      }
    });
    return;
  }
  
  // Payload too large (HTTP 413)
  if ((err as any).type === 'entity.too.large') {
    res.status(413).json({
      error: {
        code: 'PAYLOAD_TOO_LARGE',
        message: 'Request payload exceeds maximum size limit'
      }
    });
    return;
  }
  
  // Internal server errors (HTTP 500)
  console.error('Unexpected error:', err);
  res.status(500).json({
    error: {
      code: 'INTERNAL_SERVER_ERROR',
      message: 'Internal server error'
    }
  });
}
```

### Step 3: Register Route and Middleware

```typescript
// server/src/app.ts (modify existing file)
import express from 'express';
import announcementRoutes from './routes/announcements';
import { errorHandler } from './middlewares/error-handler';

const app = express();

// Payload size limit (10 MB)
app.use(express.json({ limit: '10mb' }));

// Routes
app.use('/api/v1', announcementRoutes);

// Error handler (must be AFTER routes)
app.use(errorHandler);

export default app;
```

---

## Phase 7: Integration Tests

```typescript
// server/src/__test__/announcements.test.ts
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import request from 'supertest';
import app from '../app';
import { getDatabase } from '../database/connection';

describe('POST /api/v1/announcements', () => {
  beforeEach(async () => {
    // Clear database before each test
    const db = getDatabase();
    await db('announcement').delete();
  });
  
  it('should create announcement and return 201', async () => {
    // Given
    const validData = {
      species: 'Golden Retriever',
      sex: 'MALE',
      lastSeenDate: '2025-11-20',
      photoUrl: 'https://example.com/photo.jpg',
      status: 'MISSING',
      locationLatitude: 40.785091,
      locationLongitude: -73.968285,
      email: 'john@example.com'
    };
    
    // When
    const response = await request(app)
      .post('/api/v1/announcements')
      .send(validData)
      .expect(201);
    
    // Then
    expect(response.body.id).toBeDefined();
    expect(response.body.managementPassword).toMatch(/^\d{6}$/);
    expect(response.body.species).toBe('Golden Retriever');
    expect(response.body.email).toBe('john@example.com');
  });
  
  it('should return 400 when species is missing', async () => {
    // Given
    const invalidData = {
      sex: 'MALE',
      lastSeenDate: '2025-11-20',
      photoUrl: 'https://example.com/photo.jpg',
      status: 'MISSING',
      locationLatitude: 40.785091,
      locationLongitude: -73.968285,
      email: 'john@example.com'
    };
    
    // When
    const response = await request(app)
      .post('/api/v1/announcements')
      .send(invalidData)
      .expect(400);
    
    // Then
    expect(response.body.error.code).toBe('MISSING_VALUE');
    expect(response.body.error.field).toBe('species');
  });
  
  it('should return 409 when microchip number is duplicate', async () => {
    // Given - create first announcement
    const data = {
      species: 'Golden Retriever',
      sex: 'MALE',
      lastSeenDate: '2025-11-20',
      photoUrl: 'https://example.com/photo.jpg',
      status: 'MISSING',
      locationLatitude: 40.785091,
      locationLongitude: -73.968285,
      email: 'john@example.com',
      microchipNumber: '123456789'
    };
    
    await request(app)
      .post('/api/v1/announcements')
      .send(data)
      .expect(201);
    
    // When - try to create duplicate
    const response = await request(app)
      .post('/api/v1/announcements')
      .send(data)
      .expect(409);
    
    // Then
    expect(response.body.error.code).toBe('CONFLICT');
    expect(response.body.error.field).toBe('microchipNumber');
  });
  
  // Add more integration tests for all scenarios...
});
```

**Run integration tests**:
```bash
npm test -- announcements.test.ts
```

---

## Phase 8: Manual Testing

### Start Server

```bash
cd server
npm run dev
```

### Test with cURL

**Success case**:
```bash
curl -X POST http://localhost:3000/api/v1/announcements \
  -H "Content-Type: application/json" \
  -d '{
    "species": "Golden Retriever",
    "sex": "MALE",
    "lastSeenDate": "2025-11-20",
    "photoUrl": "https://example.com/photo.jpg",
    "status": "MISSING",
    "locationLatitude": 40.785091,
    "locationLongitude": -73.968285,
    "email": "john@example.com"
  }'
```

**Expected**: HTTP 201 with announcement data and `managementPassword`

**Validation error**:
```bash
curl -X POST http://localhost:3000/api/v1/announcements \
  -H "Content-Type: application/json" \
  -d '{
    "sex": "MALE"
  }'
```

**Expected**: HTTP 400 with error details

---

## Verification Checklist

Before marking complete, verify:

- [ ] All unit tests pass (`npm test`)
- [ ] All integration tests pass (`npm test`)
- [ ] Test coverage ≥ 80% (`npm test -- --coverage`)
- [ ] ESLint passes (`npm run lint`)
- [ ] Manual testing with cURL succeeds
- [ ] Database migration applied successfully
- [ ] All acceptance scenarios from spec.md covered by tests
- [ ] Error responses match spec format exactly
- [ ] Management password returned only in POST (not GET)
- [ ] PII redaction applied in logs (if logging implemented)

---

## Next Steps

After completing this quickstart:

1. ✅ Run `/speckit.tasks` command to generate detailed task breakdown
2. ✅ Implement E2E tests in `/e2e-tests/web/` using Playwright
3. ✅ Update API documentation with new endpoint
4. ✅ Deploy to staging environment for QA testing

---

## Common Pitfalls

1. **Forgetting to hash password**: Always use `hashPassword()` before storing (scrypt format: `salt:hash`)
2. **Not sanitizing text**: Apply `sanitizeText()` to all user text fields
3. **Wrong error response format**: Ensure `field` property present for validation errors
4. **Missing fail-fast**: Stop at first validation error, don't accumulate (Zod handles this automatically)
5. **Exposing management_password**: Only return in POST response, never in GET
6. **Future date validation**: Check `lastSeenDate` is not in future (Zod custom refinement)
7. **Missing unknown field check**: Zod's `.strict()` mode handles this automatically

## Validation with Zod

**Recommendation**: Consider using Zod schema for validation instead of manual validation code. Benefits:
- Eliminates ~70% of manual validation code
- Type safety with automatic TypeScript type inference
- Built-in support for all validation requirements (required fields, formats, ranges, custom refinements)
- Automatic unknown field rejection with `.strict()` mode
- Consistent error messages

See `research.md` section 10 for complete Zod schema example.

---

## Resources

- [Feature Spec](./spec.md) - Complete functional requirements
- [Data Model](./data-model.md) - Database schema and validation rules
- [OpenAPI Contract](./contracts/openapi.yaml) - API specification
- [Research Document](./research.md) - Technology decisions and rationale

