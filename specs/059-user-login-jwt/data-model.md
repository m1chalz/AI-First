# Data Model: User Login with JWT Authentication

**Feature**: 059-user-login-jwt  
**Date**: 2025-12-16  
**Status**: Complete

## Overview

This document defines the data structures, entities, and contracts for the JWT-based authentication feature. The feature extends existing user management with authentication capabilities.

## Entities

### User (Existing Entity - No Changes)

The User entity represents a registered user account with authentication credentials.

**Database Table**: `users` (existing)

**Schema**:
```typescript
interface User {
  id: number;              // Primary key, auto-increment
  email: string;           // Unique, normalized (lowercase), 3-254 characters
  password_hash: string;   // Scrypt hash with salt (format: "salt:derivedKey")
  created_at: string;      // ISO 8601 timestamp
  updated_at: string;      // ISO 8601 timestamp
}
```

**Constraints**:
- `id`: Primary key, auto-increment
- `email`: Unique index, not null
- `password_hash`: Not null
- `created_at`: Not null, default CURRENT_TIMESTAMP
- `updated_at`: Not null, default CURRENT_TIMESTAMP

**Notes**:
- No schema changes required for this feature
- Existing validation rules apply (see `/server/src/lib/user-validation.ts`)
- Email is normalized to lowercase before storage

### JWT Token (Conceptual Entity - Not Persisted)

The JWT token is a stateless, self-contained authentication token. It is not stored in the database.

**Structure**:
```typescript
// JWT Payload (claims)
interface JwtPayload {
  userId: number;   // User's unique identifier (custom claim)
  iat: number;      // Issued at (Unix timestamp, automatic)
  exp: number;      // Expiration time (Unix timestamp, automatic)
}
```

**Token Format** (encoded):
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEyMywiaWF0IjoxNjM5NTg0MDAwLCJleHAiOjE2Mzk1ODc2MDB9.signature
```

**Lifecycle**:
- **Created**: When user registers or logs in successfully
- **Valid**: For 1 hour (3600 seconds) from issuance time
- **Expired**: After 1 hour, token becomes invalid (verified by `exp` claim)
- **Not Revocable**: Stateless design (no database lookup), revocation out of scope

**Security Properties**:
- **Signed**: HMAC SHA-256 signature prevents tampering
- **Stateless**: Self-contained, no database lookup required for validation
- **Time-Limited**: 1-hour expiration reduces exposure window
- **Minimal Payload**: Only user ID included (no PII, roles, or permissions)

## Request/Response Data Transfer Objects

### LoginRequest

Request body for POST `/api/v1/users/login`

```typescript
interface LoginRequest {
  email: string;     // User's email (3-254 characters, valid format)
  password: string;  // User's password (8-128 characters)
}
```

**Validation Rules** (reuses `CreateUserRequest` validation):
- `email`:
  - Required field
  - Type: string
  - Min length: 3 characters
  - Max length: 254 characters
  - Format: Valid email regex (`/^[^\s@]+@[^\s@]+\.[^\s@]+$/`)
  - Trimmed before validation
- `password`:
  - Required field
  - Type: string
  - Min length: 8 characters
  - Max length: 128 characters
  - Trimmed before validation
- No additional fields allowed (strict schema)

**Validation Error Response** (HTTP 400):
```json
{
  "code": "MISSING_FIELD" | "INVALID_FORMAT" | "INVALID_LENGTH" | "INVALID_FIELD",
  "message": "Descriptive error message",
  "field": "email" | "password"
}
```

### CreateUserRequest (Existing - No Changes)

Request body for POST `/api/v1/users`

```typescript
interface CreateUserRequest {
  email: string;     // User's email (3-254 characters, valid format)
  password: string;  // User's password (8-128 characters)
}
```

**Note**: Same validation rules as LoginRequest (enforced by same validator function)

### AuthResponse

Response body for both POST `/api/v1/users/login` and POST `/api/v1/users`

```typescript
interface AuthResponse {
  id: number;           // User's unique identifier
  accessToken: string;  // JWT access token (valid for 1 hour)
}
```

**Success Response** (HTTP 200 for login, HTTP 201 for registration):
```json
{
  "id": 123,
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Properties**:
- `id`: User's database ID (positive integer)
- `accessToken`: Signed JWT token string (base64-encoded, approximately 150-200 characters)

### Error Responses

#### Authentication Error (HTTP 401)

Returned when credentials are invalid (wrong email/password combination)

```json
{
  "error": "Invalid email or password"
}
```

**Trigger Conditions**:
- Email does not exist in database
- Password does not match stored hash
- **Note**: Same error message for both cases (prevents user enumeration)

#### Validation Error (HTTP 400)

Returned when request data fails validation

```json
{
  "code": "MISSING_FIELD",
  "message": "email is required",
  "field": "email"
}
```

**Error Codes**:
- `MISSING_FIELD`: Required field not provided
- `INVALID_FORMAT`: Email format is invalid
- `INVALID_LENGTH`: Field length out of range
- `INVALID_FIELD`: Unknown field provided

#### Conflict Error (HTTP 409)

Returned during registration when email already exists (registration endpoint only)

```json
{
  "error": "Email already exists"
}
```

## Data Flow Diagrams

### Login Flow

```
Client                    API                     Service Layer            Database
  |                        |                           |                       |
  |-- POST /login -------->|                           |                       |
  |  { email, password }   |                           |                       |
  |                        |                           |                       |
  |                        |-- validateLoginRequest -->|                       |
  |                        |                           |                       |
  |                        |-- authService.login ----->|                       |
  |                        |                           |                       |
  |                        |                           |-- findByEmail ------->|
  |                        |                           |                       |
  |                        |                           |<-- user or null ------|
  |                        |                           |                       |
  |                        |                           |-- verifyPassword -----|
  |                        |                           |   (constant-time)     |
  |                        |                           |                       |
  |                        |                           |-- generateToken ------|
  |                        |                           |   (JWT with userId)   |
  |                        |                           |                       |
  |                        |<-- { id, accessToken } ---|                       |
  |                        |                           |                       |
  |<-- 200 OK -------------|                           |                       |
  | { id, accessToken }    |                           |                       |
```

### Registration Flow (Extended)

```
Client                    API                     Service Layer            Database
  |                        |                           |                       |
  |-- POST /users -------->|                           |                       |
  |  { email, password }   |                           |                       |
  |                        |                           |                       |
  |                        |-- validateCreateUser ---->|                       |
  |                        |                           |                       |
  |                        |-- userService.register -->|                       |
  |                        |                           |                       |
  |                        |                           |-- findByEmail ------->|
  |                        |                           |                       |
  |                        |                           |<-- null (not exists) -|
  |                        |                           |                       |
  |                        |                           |-- hashPassword -------|
  |                        |                           |                       |
  |                        |                           |-- create ------------>|
  |                        |                           |                       |
  |                        |                           |<-- user --------------|
  |                        |                           |                       |
  |                        |                           |-- generateToken ------|
  |                        |                           |   (JWT with userId)   |
  |                        |                           |                       |
  |                        |<-- { id, accessToken } ---|                       |
  |                        |                           |                       |
  |<-- 201 Created --------|                           |                       |
  | { id, accessToken }    |                           |                       |
```

## State Transitions

### User Authentication State

```
┌─────────────────┐
│  Unauthenticated │
│   (no token)     │
└────────┬─────────┘
         │
         │ POST /login (valid credentials)
         │ or
         │ POST /users (registration)
         │
         ▼
┌─────────────────┐
│  Authenticated   │
│  (valid token)   │
└────────┬─────────┘
         │
         │ After 1 hour
         │ (token expires)
         │
         ▼
┌─────────────────┐
│  Unauthenticated │
│  (expired token) │
└──────────────────┘
```

**Notes**:
- Token expiration handling is out of scope (authentication middleware feature)
- No explicit logout mechanism in current scope (stateless tokens)
- No token refresh mechanism in current scope

## JWT Token Details

### Token Claims

**Standard Claims** (automatic):
- `iat` (Issued At): Unix timestamp when token was created
- `exp` (Expiration): Unix timestamp when token expires (iat + 3600 seconds)

**Custom Claims**:
- `userId`: User's unique identifier from database

**Example Decoded Payload**:
```json
{
  "userId": 123,
  "iat": 1702742400,
  "exp": 1702746000
}
```

### Token Header

**Algorithm**: HS256 (HMAC with SHA-256)
**Type**: JWT

**Example Decoded Header**:
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

### Token Signature

**Algorithm**: HMAC SHA-256
**Secret**: Stored in environment variable `JWT_SECRET`
**Minimum Secret Length**: 32 bytes (256 bits)

## Validation Rules Summary

### Email Validation

| Rule | Value | Error Code | Error Message |
|------|-------|------------|---------------|
| Required | Yes | MISSING_FIELD | "email is required" |
| Type | string | INVALID_FORMAT | "email must be a string" |
| Min Length | 3 | INVALID_LENGTH | "email must be 3-254 characters long" |
| Max Length | 254 | INVALID_LENGTH | "email must be 3-254 characters long" |
| Format | Regex | INVALID_FORMAT | "email format is invalid" |
| Trimmed | Yes | N/A | Automatic |
| Case | Lowercase | N/A | Normalized before storage |

### Password Validation

| Rule | Value | Error Code | Error Message |
|------|-------|------------|---------------|
| Required | Yes | MISSING_FIELD | "password is required" |
| Type | string | INVALID_FORMAT | "password must be a string" |
| Min Length | 8 | INVALID_LENGTH | "password must be 8-128 characters long" |
| Max Length | 128 | INVALID_LENGTH | "password must be 8-128 characters long" |
| Trimmed | Yes | N/A | Automatic |

### Request Body Validation

| Rule | Value | Error Code | Error Message |
|------|-------|------------|---------------|
| Extra Fields | Not Allowed | INVALID_FIELD | "{fieldName} is not a valid field" |
| Schema Mode | Strict | INVALID_FIELD | Rejects unknown properties |

## Database Queries

### Find User by Email

**Purpose**: Retrieve user record for authentication

```sql
SELECT id, email, password_hash, created_at, updated_at
FROM users
WHERE email = ?
LIMIT 1;
```

**Parameters**: 
- `email`: Normalized email address (lowercase)

**Returns**:
- User object if found
- `null` if not found

**Index**: Uses unique index on `email` column (existing)

**Performance**: O(1) with index lookup

## Security Considerations

### User Enumeration Prevention

**Requirement**: Prevent attackers from discovering which emails are registered

**Implementation**:
1. **Identical Error Messages**: Return "Invalid email or password" for both:
   - Email not found
   - Incorrect password
2. **Consistent Timing**: Perform password verification even when email doesn't exist (dummy hash)
3. **Same HTTP Status**: Use 401 for both cases

**Timing Analysis Mitigation**:
```typescript
async function loginUser(email: string, password: string) {
  const user = await findByEmail(email);
  const hash = user?.passwordHash || 'dummy-hash-here';
  
  // Always perform verification (constant time)
  const isValid = await verifyPassword(password, hash);
  
  if (!user || !isValid) {
    throw new UnauthorizedError('Invalid email or password');
  }
  
  return generateAuthResponse(user);
}
```

### Password Security

**Hash Algorithm**: scrypt (existing implementation)
**Salt**: 16 bytes random (per password)
**Key Length**: 64 bytes
**Comparison**: Constant-time (`timingSafeEqual`)

**Benefits**:
- Memory-hard algorithm (resistant to GPU attacks)
- Unique salt per password (prevents rainbow table attacks)
- Constant-time comparison (prevents timing attacks)

### JWT Security

**Signature**: HMAC SHA-256
**Secret Management**: Environment variable
**Payload**: Minimal (user ID only, no PII)
**Expiration**: 1 hour (limits exposure)

**Attack Resistance**:
- **Tampering**: Signature verification detects modifications
- **Replay**: Expiration limits token lifetime
- **Data Exposure**: Minimal payload reduces risk

## Type Definitions

**File**: `/server/src/types/auth.d.ts` (new file)

```typescript
/**
 * Authentication response returned after successful login or registration.
 * Contains user identifier and JWT access token.
 */
export interface AuthResponse {
  /** User's unique identifier */
  id: number;
  
  /** JWT access token valid for 1 hour */
  accessToken: string;
}

/**
 * Decoded JWT token payload.
 * Contains user identification and standard JWT claims.
 */
export interface JwtPayload {
  /** User's unique identifier */
  userId: number;
  
  /** Token issued at time (Unix timestamp) */
  iat: number;
  
  /** Token expiration time (Unix timestamp) */
  exp: number;
}
```

## Implementation Notes

1. **Reuse Validation**: Login endpoint reuses `validateCreateUser()` function
2. **Reuse Password Verification**: Uses existing `verifyPassword()` function
3. **Reuse Repository**: Uses existing `IUserRepository.findByEmail()` method
4. **Extended Service**: `UserService` extended with `loginUser()` method for login logic
5. **New Utility**: `jwt-utils.ts` wraps jsonwebtoken library
6. **Extended Service**: `UserService.registerUser()` extended to return token
7. **New Error**: `UnauthorizedError` class for 401 responses
8. **New Type**: `AuthResponse` interface for consistent response structure

