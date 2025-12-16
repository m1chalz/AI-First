# Research: User Login with JWT Authentication

**Feature**: 059-user-login-jwt  
**Date**: 2025-12-16  
**Status**: Complete

## Overview

This document captures technical research findings for implementing JWT-based user authentication in the PetSpot backend API. The research focuses on JWT library selection, security best practices, and integration patterns with the existing Express/TypeScript stack.

## Research Questions & Findings

### 1. JWT Library Selection

**Question**: Which JWT library should be used for token generation and verification in Node.js?

**Decision**: `jsonwebtoken` by Auth0 (npm package: `jsonwebtoken`)

**Rationale**:
- **Industry Standard**: Most widely used JWT library in Node.js ecosystem (9M+ weekly downloads)
- **Security Audited**: Maintained by Auth0, a security-focused organization with reputation for JWT expertise
- **Feature Complete**: Supports all required features:
  - Synchronous and asynchronous token signing/verification
  - HS256 algorithm (symmetric, suitable for single-server deployments)
  - Expiration time control (`expiresIn` option)
  - Custom payload support
  - Standard JWT claims (iat, exp, sub)
- **Well Documented**: Extensive documentation and code examples available
- **Active Maintenance**: Regular security updates and bug fixes
- **MIT License**: Permissive license compatible with project requirements

**Alternatives Considered**:
- **fast-jwt** (NearForm): Performance-focused alternative, but less mature ecosystem
- **jose** (Panva): Modern library with better TypeScript support, but introduces breaking changes from jsonwebtoken
- **Custom Implementation**: Rejected due to security risks and maintenance burden

**Implementation Pattern**:
```javascript
const jwt = require('jsonwebtoken');

// Sign token (synchronous)
const token = jwt.sign(
  { userId: 12345 },
  process.env.JWT_SECRET,
  { expiresIn: '1h' }
);

// Verify token (synchronous)
const decoded = jwt.verify(token, process.env.JWT_SECRET);
```

### 2. JWT Algorithm Selection

**Question**: Which signing algorithm should be used for JWT tokens?

**Decision**: HS256 (HMAC with SHA-256)

**Rationale**:
- **Symmetric Approach**: Single secret key for signing and verification (suitable for single-server or trusted server cluster)
- **Performance**: Faster than RSA/ECDSA asymmetric algorithms (no public key infrastructure needed)
- **Simplicity**: Easier to configure and manage (single environment variable for secret)
- **Security**: SHA-256 provides strong cryptographic security when using sufficiently long secret (recommended 256+ bits)
- **Current Architecture**: Backend API is single deployment, no need for distributed verification with public keys

**Alternatives Considered**:
- **RS256** (RSA with SHA-256): Asymmetric algorithm requiring private/public key pair. Rejected because:
  - Overkill for current architecture (no third-party verifiers)
  - Additional complexity (key generation, storage, rotation)
  - Slower performance
- **ES256** (ECDSA with P-256): Modern asymmetric algorithm. Rejected for same reasons as RS256
- **HS512** (HMAC with SHA-512): Stronger hash but unnecessary for current threat model and slower

**Security Considerations**:
- Secret key must be generated using cryptographically secure random number generator
- Minimum secret length: 32 bytes (256 bits)
- Secret must be stored securely (environment variable, never committed to repository)
- Secret rotation strategy should be planned for production

### 3. JWT Payload Structure

**Question**: What data should be included in the JWT payload?

**Decision**: Minimal payload with user ID only

**Rationale**:
- **Security**: Minimal payload principle - reduce sensitive data exposure if token is compromised
- **Token Size**: Smaller tokens mean lower bandwidth overhead
- **Freshness**: User data can be fetched from database when needed, ensuring up-to-date information
- **Privacy**: Email, roles, and other PII not stored in token (tokens may be logged, cached, or exposed)
- **Spec Compliance**: Aligns with clarification decision (user ID only in payload)

**Payload Structure**:
```typescript
{
  userId: number,  // User's unique identifier
  iat: number,     // Issued at (automatic, Unix timestamp)
  exp: number      // Expiration (automatic, Unix timestamp)
}
```

**Alternatives Considered**:
- **Email in Payload**: Rejected due to PII concerns and spec clarification
- **Roles/Permissions**: Rejected; out of scope for this feature (authorization is separate concern)
- **Username/Display Name**: Rejected due to size and PII concerns

### 4. Token Expiration Strategy

**Question**: How should token expiration be configured?

**Decision**: 1-hour expiration (3600 seconds)

**Rationale**:
- **Spec Requirement**: Explicitly defined in feature clarifications
- **Security vs UX Balance**: Long enough for typical session but short enough to limit exposure window
- **Stateless Tokens**: No token refresh mechanism in current scope (out of scope per clarifications)

**Implementation**:
```javascript
jwt.sign(payload, secret, { expiresIn: '1h' })
```

**Future Considerations** (out of current scope):
- Refresh token mechanism for long-lived sessions
- Token revocation/blacklisting strategy
- Sliding expiration windows

### 5. Secret Key Management

**Question**: How should the JWT secret key be managed?

**Decision**: Environment variable (`JWT_SECRET`)

**Rationale**:
- **Separation of Concerns**: Configuration separated from code
- **Security**: Secret not committed to version control
- **Flexibility**: Different secrets for development/staging/production environments
- **Standard Practice**: Aligns with existing project patterns (database credentials, API keys)

**Implementation Requirements**:
- Add `JWT_SECRET` to `.env` file (development)
- Add to `.env.example` with placeholder value
- Document required minimum length (256 bits / 32 bytes)
- Application should fail fast if JWT_SECRET is not configured
- Generate strong secret using: `node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"`

**Development vs Production**:
- Development: Can use fixed secret for local testing
- Production: Must use cryptographically secure random secret, rotated periodically

### 6. Password Verification Security

**Question**: How to prevent timing attacks during password verification?

**Decision**: Reuse existing `verifyPassword()` function from `password-management.ts`

**Rationale**:
- **Existing Implementation**: Already uses `timingSafeEqual` for constant-time comparison
- **Best Practice**: Prevents timing attacks where attacker infers password correctness by measuring response time
- **No Additional Work**: Implementation already meets security requirements
- **Tested**: Existing unit tests cover the function

**Code Reference** (`/server/src/lib/password-management.ts`):
```typescript
export async function verifyPassword(plainPassword: string, hash: string): Promise<boolean> {
  const [saltHex, keyHex] = hash.split(':');
  const salt = Buffer.from(saltHex, 'hex');
  const originalKey = Buffer.from(keyHex, 'hex');
  const derivedKey = (await scryptAsync(plainPassword, salt, KEY_LENGTH)) as Buffer;
  return timingSafeEqual(originalKey, derivedKey); // Constant-time comparison
}
```

### 7. User Enumeration Prevention

**Question**: How to prevent attackers from discovering which emails are registered?

**Decision**: Return identical HTTP 401 responses for both "email not found" and "incorrect password" cases

**Rationale**:
- **Spec Requirement**: FR-013 explicitly requires user enumeration prevention
- **Security Best Practice**: Attackers should not be able to distinguish between valid/invalid emails
- **Generic Error Message**: Use same error message for both cases (e.g., "Invalid email or password")
- **Consistent Timing**: Both paths should take similar time (password verification always performed, even for non-existent users)

**Implementation Strategy**:
```typescript
// Pseudocode
async function loginUser(email: string, password: string) {
  const user = await repository.findByEmail(email);
  
  if (!user) {
    // Perform dummy password verification to maintain timing consistency
    await verifyPassword(password, 'dummy-hash');
    throw new UnauthorizedError('Invalid email or password');
  }
  
  const isValid = await verifyPassword(password, user.passwordHash);
  if (!isValid) {
    throw new UnauthorizedError('Invalid email or password');
  }
  
  return generateToken(user.id);
}
```

**Security Note**: Even with constant-time comparison, a determined attacker might use statistical timing analysis over many requests. Rate limiting (out of scope for this feature) provides additional protection.

### 8. Login Validation Strategy

**Question**: Should login endpoint reuse registration validation or have separate validation?

**Decision**: Reuse existing `validateCreateUser()` function from `user-validation.ts`

**Rationale**:
- **Spec Requirement**: FR-003 explicitly requires reusing same validation rules
- **DRY Principle**: Single source of truth for email and password validation
- **Consistency**: Ensures identical validation behavior across registration and login
- **Minimal Changes**: No need to duplicate validation logic

**Existing Validation Rules** (`/server/src/lib/user-validation.ts`):
- Email: 3-254 characters, valid email format (regex)
- Password: 8-128 characters
- No extra fields allowed (strict schema)
- Returns standardized ValidationError with field and code

**HTTP Status Mapping**:
- Validation failure → HTTP 400
- Authentication failure → HTTP 401

### 9. Response Structure

**Question**: What should the login and registration responses contain?

**Decision**: 
- Login response: `{ id: number, accessToken: string }`
- Registration response: `{ id: number, accessToken: string }` (extended from current `{ id: number }`)

**Rationale**:
- **Spec Requirement**: FR-006 and FR-010 explicitly define response structure
- **Consistency**: Both endpoints return identical structure
- **Minimal Data**: Only essential information (user ID and token)
- **Client Convenience**: Client receives token immediately without second request

**TypeScript Types**:
```typescript
// /server/src/types/auth.d.ts
export interface AuthResponse {
  id: number;
  accessToken: string;
}
```

### 10. Error Handling Strategy

**Question**: How should authentication errors be handled consistently?

**Decision**: Define new `UnauthorizedError` class extending base error system

**Rationale**:
- **Existing Pattern**: Project uses custom error classes (`ValidationError`, `ConflictError`, etc.)
- **Consistent Structure**: All errors follow same pattern (type, message, code)
- **Middleware Integration**: Error handler middleware already maps error types to HTTP status codes
- **Clear Intent**: Distinguishes authentication failures (401) from authorization failures (future feature)

**Implementation** (add to `/server/src/lib/errors.ts`):
```typescript
export class UnauthorizedError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'UnauthorizedError';
  }
}
```

**Error Handler Middleware Update** (in `/server/src/middlewares/error-handler-middleware.ts`):
```typescript
if (error instanceof UnauthorizedError) {
  return res.status(401).json({ error: error.message });
}
```

## Security Checklist

- [x] Use industry-standard JWT library (jsonwebtoken)
- [x] HS256 algorithm selected (appropriate for architecture)
- [x] Minimal JWT payload (user ID only)
- [x] 1-hour token expiration configured
- [x] Secret key via environment variable
- [x] Constant-time password verification (existing implementation)
- [x] User enumeration prevention (identical error responses)
- [x] Validation reuse (DRY principle)
- [x] Clear separation of 400 (validation) vs 401 (authentication) errors

## Configuration Requirements

### Environment Variables

Add to `/server/.env`:
```bash
# JWT Configuration
JWT_SECRET=<generate using: node -e "console.log(require('crypto').randomBytes(32).toString('base64'))">
```

Add to `/server/.env.example`:
```bash
# JWT Configuration
# Generate a secure secret: node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"
# Minimum 32 bytes (256 bits) required
JWT_SECRET=your-secret-here-replace-in-production
```

### Package Dependencies

Add to `/server/package.json`:
```json
{
  "dependencies": {
    "jsonwebtoken": "^9.0.2"
  },
  "devDependencies": {
    "@types/jsonwebtoken": "^9.0.5"
  }
}
```

## Testing Strategy

### Unit Tests

1. **JWT Utilities** (`/lib/__test__/jwt-utils.test.ts`):
   - Generate valid token with user ID and expiration
   - Verify valid token returns payload
   - Verify expired token throws error
   - Verify token with invalid signature throws error
   - Verify token with malformed structure throws error

2. **User Service** (`/services/__test__/user-service.test.ts` - extended):
   - **Login tests**:
     - Login with valid credentials returns token and user ID
     - Login with non-existent email returns 401
     - Login with incorrect password returns 401
     - Login with invalid email format returns 400
     - Login with missing password returns 400
     - Error messages are identical for invalid email and wrong password
   - **Registration tests** (extended):
     - Registration returns user ID and token
     - Token contains correct user ID
     - Token has correct expiration

### Integration Tests

1. **Login Endpoint** (`/__test__/http/login.test.ts`):
   - POST /login with valid credentials returns 200, id, and accessToken
   - POST /login with invalid email returns 401
   - POST /login with wrong password returns 401
   - POST /login with missing fields returns 400
   - POST /login with invalid email format returns 400
   - Response time similar for invalid email vs wrong password (timing attack mitigation)

2. **Registration Endpoint** (`/__test__/http/users.test.ts` - extended):
   - POST /users returns 201 with id and accessToken
   - Access token is valid and contains user ID
   - Access token expires after 1 hour

## Implementation Checklist

- [ ] Phase 1: Create data model and contracts
- [ ] Install jsonwebtoken dependency
- [ ] Create JWT utilities module (`/lib/jwt-utils.ts`)
- [ ] Extend UserService with login logic (`/services/user-service.ts`)
- [ ] Add UnauthorizedError to errors module
- [ ] Update error handler middleware for 401 status
- [ ] Add login endpoint to users router
- [ ] Extend registration endpoint response
- [ ] Create TypeScript types for auth responses
- [ ] Write unit tests (80% coverage target)
- [ ] Write integration tests (80% coverage target)
- [ ] Update environment configuration docs
- [ ] Manual testing of complete flow

## References

- [jsonwebtoken Documentation](https://github.com/auth0/node-jsonwebtoken)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)
- Feature Spec: `/specs/059-user-login-jwt/spec.md`

