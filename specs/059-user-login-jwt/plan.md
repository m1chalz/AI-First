# Implementation Plan: User Login with JWT Authentication

**Branch**: `059-user-login-jwt` | **Date**: 2025-12-16 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/059-user-login-jwt/spec.md`

## Summary

Implement JWT-based user authentication by adding a login endpoint (POST `/api/v1/users/login`) and extending the registration endpoint to return access tokens. The login endpoint accepts email/password credentials, validates them against stored user data, and returns a JWT access token (1-hour expiration) with user ID. The registration endpoint is extended to also return JWT tokens immediately upon successful account creation. The implementation reuses existing validation logic and DTOs, follows security best practices (minimal JWT payload with user ID only, constant-time password verification, user enumeration prevention), and maintains consistent error handling across both endpoints.

## Technical Context

**Language/Version**: Node.js v24 (LTS) with TypeScript (strict mode)
**Primary Dependencies**: Express 5.x, jsonwebtoken (JWT generation/verification), existing validation (zod), existing password management (scrypt)
**Storage**: SQLite (via Knex) - existing user table with email and password_hash columns
**Testing**: Vitest + Supertest for integration tests, Vitest for unit tests
**Target Platform**: Linux/macOS server (Node.js runtime)
**Project Type**: Backend API (Node.js/Express)
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)
**Constraints**: 
- Must reuse existing validation logic (user-validation.ts)
- Must reuse existing DTO structure (CreateUserRequest)
- Must prevent user enumeration attacks (constant-time responses, generic error messages)
- JWT signing requires secret key configuration
**Scale/Scope**: Low-traffic internal API, estimated < 1000 users

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is a backend-only feature affecting only `/server` module. Frontend-related checks are marked N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: N/A - Backend-only feature
  - Violation justification: N/A

- [x] **Android MVI Architecture**: N/A - Backend-only feature
  - Violation justification: N/A

- [x] **iOS MVVM-C Architecture**: N/A - Backend-only feature
  - Violation justification: N/A

- [x] **Interface-Based Design**: Repository pattern used
  - Backend: Repository interface `IUserRepository` in `/server/src/database/repositories/user-repository.ts`
  - Services reference interfaces, not concrete implementations
  - New JWT service will follow same pattern
  - Violation justification: N/A

- [x] **Dependency Injection**: Manual DI used in backend
  - Backend: Constructor injection via DI configuration in `/server/src/conf/di.conf.ts`
  - Services instantiated with dependencies injected
  - New JWT service and login logic will follow existing pattern
  - Violation justification: N/A

- [x] **80% Test Coverage - Platform-Specific**: Unit and integration tests planned
  - Backend: Tests in `/server/src/services/__test__/`, `/server/src/lib/__test__/`, `/server/src/__test__/`
  - Coverage target: 80% line + branch coverage
  - Test coverage: JWT generation/verification, login service logic, password verification, API endpoints
  - Run command: `npm test --coverage`
  - Violation justification: N/A

- [x] **End-to-End Tests**: N/A - Backend API only
  - E2E tests will be added when frontend implements login UI
  - Violation justification: E2E tests require frontend implementation

- [x] **Asynchronous Programming Standards**: Native async/await used
  - Backend: Native `async`/`await` (Express async handlers)
  - No Promises chains, callbacks, or reactive libraries
  - Violation justification: N/A

- [x] **Test Identifiers for UI Controls**: N/A - Backend-only feature
  - Violation justification: N/A

- [x] **Public API Documentation**: JSDoc for public APIs
  - TypeScript: JSDoc format (`/** ... */`)
  - Documentation for JWT service methods, login service methods
  - Skip documentation for self-explanatory functions
  - Violation justification: N/A

- [x] **Given-When-Then Test Structure**: All tests follow convention
  - Unit tests clearly separate Given (setup), When (action), Then (verification)
  - Integration tests structure API requests with Given-When-Then phases
  - Test names use descriptive strings (TypeScript convention)
  - Violation justification: N/A

### Backend Architecture & Quality Standards

- [x] **Backend Technology Stack**: Modern Node.js stack for `/server` module
  - Runtime: Node.js v24 (LTS)
  - Framework: Express.js 5.x
  - Language: TypeScript with strict mode enabled
  - Database: Knex query builder + SQLite (existing user table reused)
  - New dependency: jsonwebtoken for JWT generation/verification
  - Violation justification: N/A

- [x] **Backend Code Quality**: Quality standards enforced
  - ESLint with TypeScript plugin configured and enabled
  - Clean Code principles:
    - Small, focused functions (single responsibility)
    - Descriptive naming (jwtService, loginUser, generateToken)
    - Maximum 3 nesting levels
    - DRY principle (reuse validation, password verification)
    - JSDoc documentation for public APIs
  - Violation justification: N/A

- [x] **Backend Dependency Management**: Minimal dependencies
  - Only add jsonwebtoken (industry-standard JWT library, 9M+ weekly downloads, MIT license)
  - Rationale: JWT generation/verification requires cryptographic operations; jsonwebtoken is well-maintained and security-audited
  - All other logic reuses existing dependencies (zod, scrypt, Knex)
  - Regular `npm audit` security checks planned
  - Violation justification: N/A

- [x] **Backend Directory Structure**: Standardized layout followed
  - `/middlewares/` - (future) JWT authentication middleware will go here
  - `/routes/` - POST /login endpoint added to `users.ts`, existing POST / endpoint extended
  - `/services/` - New `AuthService` for login logic, `JwtService` for token operations
  - `/database/` - Reuse existing `user-repository.ts`
  - `/lib/` - New `jwt-utils.ts` for JWT generation/verification helper functions
  - `/__test__/` - Integration tests for login endpoint
  - Violation justification: N/A

- [x] **Backend TDD Workflow**: Test-Driven Development followed
  - RED: Write failing test first
  - GREEN: Write minimal code to pass test
  - REFACTOR: Improve code quality without changing behavior
  - Tests written BEFORE implementation code
  - Violation justification: N/A

- [x] **Backend Testing Strategy**: Comprehensive test coverage
  - Unit tests (Vitest):
    - Location: `/src/services/__test__/auth-service.test.ts`, `/src/lib/__test__/jwt-utils.test.ts`
    - Coverage target: 80% line + branch coverage
    - Scope: Login logic, JWT generation/verification, password verification, error cases
  - Integration tests (Vitest + SuperTest):
    - Location: `/src/__test__/http/login.test.ts`, update `/src/__test__/http/users.test.ts`
    - Coverage target: 80% for API endpoints
    - Scope: POST /login endpoint (success, invalid credentials, validation errors), POST /register extended response
  - All tests follow Given-When-Then structure
  - Run commands: `npm test`, `npm test --coverage`
  - Violation justification: N/A

### Web Architecture & Quality Standards

- [x] **Web Technology Stack**: N/A - Backend-only feature
  - Violation justification: N/A

- [x] **Web Code Quality**: N/A - Backend-only feature
  - Violation justification: N/A

- [x] **Web Dependency Management**: N/A - Backend-only feature
  - Violation justification: N/A

- [x] **Web Business Logic Extraction**: N/A - Backend-only feature
  - Violation justification: N/A

- [x] **Web TDD Workflow**: N/A - Backend-only feature
  - Violation justification: N/A

- [x] **Web Testing Strategy**: N/A - Backend-only feature
  - Violation justification: N/A

## Project Structure

### Documentation (this feature)

```text
specs/059-user-login-jwt/
├── spec.md              # Feature specification (already created)
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (JWT library selection, security best practices)
├── data-model.md        # Phase 1 output (JWT token structure, auth responses)
├── quickstart.md        # Phase 1 output (local testing guide)
├── contracts/           # Phase 1 output (OpenAPI specs for login/register)
│   ├── login.yaml       # POST /api/v1/users/login contract
│   └── register.yaml    # POST /api/v1/users contract (extended)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
server/
├── src/
│   ├── lib/
│   │   ├── jwt-utils.ts                     # [NEW] JWT generation/verification utilities
│   │   ├── __test__/
│   │   │   └── jwt-utils.test.ts            # [NEW] Unit tests for JWT utilities
│   │   ├── user-validation.ts               # [EXISTING] Reused for login validation
│   │   └── password-management.ts           # [EXISTING] Reused for password verification
│   ├── services/
│   │   ├── user-service.ts                  # [MODIFIED] Extended with login logic + JWT on registration
│   │   └── __test__/
│   │       └── user-service.test.ts         # [MODIFIED] Extended tests for login + registration JWT
│   ├── routes/
│   │   └── users.ts                         # [MODIFIED] Add POST /login endpoint, extend POST / response
│   ├── database/
│   │   └── repositories/
│   │       └── user-repository.ts           # [EXISTING] Reused for user lookup
│   ├── conf/
│   │   └── di.conf.ts                       # [EXISTING] Reused (no changes needed)
│   ├── types/
│   │   ├── user.d.ts                        # [EXISTING] User type
│   │   └── auth.d.ts                        # [NEW] AuthResponse type (id + accessToken)
│   └── __test__/
│       └── http/
│           ├── login.test.ts                # [NEW] Integration tests for POST /login
│           └── users.test.ts                # [MODIFIED] Extended tests for POST / JWT response
└── package.json                             # [MODIFIED] Add jsonwebtoken dependency
```

**Structure Decision**: Backend-only feature following existing server structure. Login logic merged into existing `UserService` (no separate AuthService), JWT utilities in `/lib`, login endpoint in `/routes/users.ts`. Reuses existing validation, password management, repository, and DI configuration.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No violations requiring justification.
