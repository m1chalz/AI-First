# Quickstart: User Login with JWT Authentication

**Feature**: 059-user-login-jwt  
**Date**: 2025-12-16  
**Audience**: Developers testing the feature locally

## Overview

This guide helps you quickly set up, test, and verify the JWT-based authentication feature on your local development environment.

## Prerequisites

- Node.js v24 (LTS) installed
- Project repository cloned
- Basic familiarity with REST APIs and curl/HTTP clients

## Setup

### 1. Install Dependencies

Navigate to the server directory and install the new JWT dependency:

```bash
cd server
npm install jsonwebtoken @types/jsonwebtoken
```

### 2. Configure JWT Secret

Generate a secure JWT secret:

```bash
node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"
```

Add the generated secret to your `.env` file:

```bash
# In server/.env
JWT_SECRET=<paste-generated-secret-here>
```

**Example `.env` file**:
```bash
# Database
DATABASE_URL=./dev.db

# JWT Configuration
JWT_SECRET=dGVzdC1zZWNyZXQta2V5LWZvci1kZXZlbG9wbWVudC0zMi1ieXRlcw==

# Server
PORT=3000
```

### 3. Start the Development Server

```bash
npm run dev
```

The server should start on `http://localhost:3000` with pretty-printed logs.

## Testing the Feature

### Using curl

#### 1. Register a New User

Creates a new user account and receives an access token immediately.

```bash
curl -X POST http://localhost:3000/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "SecurePass123!"
  }'
```

**Expected Response** (HTTP 201):
```json
{
  "id": 1,
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsImlhdCI6MTcwMjc0MjQwMCwiZXhwIjoxNzAyNzQ2MDAwfQ.signature"
}
```

**Save the token** for later use:
```bash
TOKEN="<paste-access-token-here>"
```

#### 2. Login with Existing User

Authenticates with email/password and receives a new access token.

```bash
curl -X POST http://localhost:3000/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "SecurePass123!"
  }'
```

**Expected Response** (HTTP 200):
```json
{
  "id": 1,
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsImlhdCI6MTcwMjc0MjQ2MCwiZXhwIjoxNzAyNzQ2MDYwfQ.signature"
}
```

#### 3. Test Invalid Credentials

Attempt login with wrong password (should return 401).

```bash
curl -v -X POST http://localhost:3000/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "WrongPassword123!"
  }'
```

**Expected Response** (HTTP 401):
```json
{
  "error": "Invalid email or password"
}
```

#### 4. Test Validation Errors

Attempt login with invalid email format (should return 400).

```bash
curl -v -X POST http://localhost:3000/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "not-an-email",
    "password": "SecurePass123!"
  }'
```

**Expected Response** (HTTP 400):
```json
{
  "code": "INVALID_FORMAT",
  "message": "email format is invalid",
  "field": "email"
}
```

#### 5. Test User Enumeration Prevention

Compare response times for invalid email vs. wrong password (should be similar).

```bash
# Test 1: Non-existent email
time curl -s -X POST http://localhost:3000/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nonexistent@example.com",
    "password": "SomePassword123!"
  }' > /dev/null

# Test 2: Valid email, wrong password
time curl -s -X POST http://localhost:3000/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "WrongPassword123!"
  }' > /dev/null
```

**Expected**: Both requests should take approximately the same time (within ~10ms).

### Using Postman/Insomnia

#### Import Collection

Create a new collection with these requests:

**1. Register User**
- Method: `POST`
- URL: `http://localhost:3000/api/v1/users`
- Headers: `Content-Type: application/json`
- Body (JSON):
  ```json
  {
    "email": "test@example.com",
    "password": "SecurePass123!"
  }
  ```
- Tests: Extract `accessToken` to environment variable

**2. Login User**
- Method: `POST`
- URL: `http://localhost:3000/api/v1/users/login`
- Headers: `Content-Type: application/json`
- Body (JSON):
  ```json
  {
    "email": "test@example.com",
    "password": "SecurePass123!"
  }
  ```
- Tests: Extract `accessToken` to environment variable

**3. Login - Invalid Credentials**
- Method: `POST`
- URL: `http://localhost:3000/api/v1/users/login`
- Headers: `Content-Type: application/json`
- Body (JSON):
  ```json
  {
    "email": "test@example.com",
    "password": "WrongPassword"
  }
  ```
- Tests: Assert HTTP 401 status

**4. Login - Invalid Email Format**
- Method: `POST`
- URL: `http://localhost:3000/api/v1/users/login`
- Headers: `Content-Type: application/json`
- Body (JSON):
  ```json
  {
    "email": "not-an-email",
    "password": "SecurePass123!"
  }
  ```
- Tests: Assert HTTP 400 status

## Verifying JWT Tokens

### Online JWT Debugger

1. Go to https://jwt.io/
2. Paste your access token in the "Encoded" section
3. Verify the decoded payload contains:
   - `userId`: Your user's ID
   - `iat`: Issued at timestamp
   - `exp`: Expiration timestamp (1 hour after `iat`)
4. Paste your JWT secret in the "Verify Signature" section
5. Verify signature shows "Signature Verified"

### Command Line

Decode the JWT token using Node.js:

```bash
node -e "
const jwt = require('jsonwebtoken');
const token = process.argv[1];
const decoded = jwt.decode(token, { complete: true });
console.log('Header:', JSON.stringify(decoded.header, null, 2));
console.log('Payload:', JSON.stringify(decoded.payload, null, 2));
" "<paste-token-here>"
```

**Expected Output**:
```json
Header: {
  "alg": "HS256",
  "typ": "JWT"
}
Payload: {
  "userId": 1,
  "iat": 1702742400,
  "exp": 1702746000
}
```

Verify the token signature:

```bash
node -e "
const jwt = require('jsonwebtoken');
const token = process.argv[1];
const secret = process.env.JWT_SECRET;
try {
  const decoded = jwt.verify(token, secret);
  console.log('✓ Token is valid');
  console.log('Payload:', JSON.stringify(decoded, null, 2));
} catch (err) {
  console.error('✗ Token is invalid:', err.message);
}
" "<paste-token-here>"
```

**Expected Output**:
```
✓ Token is valid
Payload: {
  "userId": 1,
  "iat": 1702742400,
  "exp": 1702746000
}
```

## Testing Token Expiration

JWT tokens expire after 1 hour. To test expiration behavior:

### Option 1: Wait 1 Hour

Register/login, save the token, wait 1 hour, then try to use it (future authentication middleware will reject it).

### Option 2: Generate Short-Lived Token

Temporarily modify the code to generate a token with 5-second expiration:

```typescript
// In jwt-utils.ts (temporary change for testing)
export function generateToken(userId: number): string {
  return jwt.sign({ userId }, secret, { expiresIn: '5s' }); // Changed from '1h'
}
```

Then:
1. Login and save the token
2. Wait 6 seconds
3. Verify the token is expired:

```bash
node -e "
const jwt = require('jsonwebtoken');
const token = process.argv[1];
const secret = process.env.JWT_SECRET;
try {
  jwt.verify(token, secret);
  console.log('✗ Token should be expired but is still valid');
} catch (err) {
  if (err.name === 'TokenExpiredError') {
    console.log('✓ Token correctly expired');
    console.log('Expired at:', new Date(err.expiredAt));
  } else {
    console.error('✗ Unexpected error:', err.message);
  }
}
" "<paste-token-here>"
```

**Expected Output**:
```
✓ Token correctly expired
Expired at: 2023-12-16T12:00:05.000Z
```

**Remember**: Revert the code change after testing!

## Common Issues & Troubleshooting

### Issue: "JWT_SECRET is not configured"

**Solution**: Add `JWT_SECRET` to your `.env` file (see Setup step 2).

### Issue: "Email already exists" (HTTP 409)

**Solution**: The email is already registered. Either:
- Use a different email
- Delete the user from the database: `rm server/dev.db` and restart server
- Use the login endpoint instead of registration

### Issue: Token verification fails

**Possible causes**:
1. **Wrong secret**: Ensure `JWT_SECRET` in `.env` matches the secret used to sign the token
2. **Token expired**: Generate a new token (tokens are valid for 1 hour)
3. **Token corrupted**: Ensure you copied the full token without truncation

### Issue: Response times differ for invalid email vs. wrong password

**Expected**: Both should be similar (within ~10-20ms on modern hardware).  
**If significantly different**: Check that password verification is performed even for non-existent users (see user enumeration prevention in data-model.md).

### Issue: Server won't start

**Possible causes**:
1. Port 3000 already in use: `lsof -i :3000` to find process, kill it or change PORT in `.env`
2. Missing dependencies: Run `npm install`
3. Database locked: `rm server/dev.db-journal` if exists

## Testing Checklist

Use this checklist to verify the feature works correctly:

- [ ] Server starts without errors
- [ ] **Registration Flow**:
  - [ ] POST /users with valid data returns 201
  - [ ] Response includes `id` and `accessToken`
  - [ ] Token is valid JWT with correct payload (userId, iat, exp)
  - [ ] Token expires after 1 hour
- [ ] **Login Flow**:
  - [ ] POST /login with valid credentials returns 200
  - [ ] Response includes `id` and `accessToken`
  - [ ] Token is valid JWT with correct payload
  - [ ] Token from login works the same as token from registration
- [ ] **Error Cases - Login**:
  - [ ] Invalid email format returns 400 with INVALID_FORMAT code
  - [ ] Missing email returns 400 with MISSING_FIELD code
  - [ ] Missing password returns 400 with MISSING_FIELD code
  - [ ] Password too short returns 400 with INVALID_LENGTH code
  - [ ] Extra fields returns 400 with INVALID_FIELD code
  - [ ] Non-existent email returns 401 with generic message
  - [ ] Wrong password returns 401 with generic message
  - [ ] Error message identical for invalid email and wrong password
- [ ] **Error Cases - Registration**:
  - [ ] Duplicate email returns 409 with "Email already exists"
  - [ ] Invalid email format returns 400
  - [ ] Password too short returns 400
- [ ] **Security**:
  - [ ] Response times similar for invalid email vs. wrong password
  - [ ] Token payload contains only userId (no email, no PII)
  - [ ] Token signature verifies correctly
  - [ ] Expired token fails verification

## Next Steps

After verifying the feature works locally:

1. **Write tests**: Follow TDD workflow (see research.md for test strategy)
2. **Code review**: Ensure code follows project conventions
3. **Integration**: Test with frontend applications (web, mobile)
4. **Documentation**: Update API documentation with new endpoints
5. **Deployment**: Add JWT_SECRET to production environment variables

## Additional Resources

- Feature Specification: [spec.md](./spec.md)
- Data Model: [data-model.md](./data-model.md)
- Research Findings: [research.md](./research.md)
- API Contracts: [contracts/login.yaml](./contracts/login.yaml), [contracts/register.yaml](./contracts/register.yaml)
- Implementation Plan: [plan.md](./plan.md)

## Support

For questions or issues:
- Check existing tests for usage examples
- Review error handler middleware for error response formats
- Consult JWT library documentation: https://github.com/auth0/node-jsonwebtoken

