# Feature Specification: User Login with JWT Authentication

**Feature Branch**: `059-user-login-jwt`  
**Created**: 2025-12-16  
**Status**: Draft  
**Input**: User description: "endpoint do logowania POST /api/v1/users/login. Body takie samo jak w POST /api/v1/users (wykorzystaj ponownie istniejące DTO). W odpowiedzi endpoint zwraca HTTP 200 z polami id (userId) i accessToken w body. Odpowiedź z POST /api/v1/users ma być rozszerzona żeby również zwracała id i accessToken. Access token to JWT. HTTP 401 gdy kombinacja email/hasło jest nieprawidłowa, HTTP 400 gdy walidacja nie przeszła (wykorzystaj istniejącą logikę walidacji z POST /api/v1/users"

## Clarifications

### Session 2025-12-16

- Q: How long should JWT access tokens remain valid before expiring? → A: 1 hour
- Q: What rate limit should be applied to login attempts to prevent brute force attacks? → A: No rate limiting (out of scope for this feature)
- Q: What user information should be included in the JWT token payload? → A: User ID only
- Q: What HTTP status should be returned when a user attempts to use an expired JWT token? → A: Out of scope - Handle in authentication middleware feature
- Q: Should users be allowed to login if their email address hasn't been verified? → A: No email verification system - Out of scope entirely

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Existing User Authentication (Priority: P1)

A registered user wants to log into the application using their email and password credentials to access their account and protected features.

**Why this priority**: Core authentication functionality is essential for any user to access the system. Without this, no user can authenticate and use protected features.

**Independent Test**: Can be fully tested by submitting valid credentials to the login endpoint and verifying that a valid access token and user ID are returned, enabling the user to access protected resources.

**Acceptance Scenarios**:

1. **Given** a user has registered with email "user@example.com" and password "SecurePass123!", **When** they submit these credentials to the login endpoint, **Then** they receive HTTP 200 with their user ID and a valid JWT access token with 1 hour expiration
2. **Given** a user has a valid access token, **When** they use it to access protected endpoints, **Then** they are successfully authenticated
3. **Given** a user provides valid email but incorrect password, **When** they attempt to login, **Then** they receive HTTP 401 with an appropriate error message
4. **Given** a user provides an email that doesn't exist in the system, **When** they attempt to login, **Then** they receive HTTP 401 with an appropriate error message

---

### User Story 2 - Automatic Authentication After Registration (Priority: P2)

A new user who just completed registration should automatically receive authentication credentials so they can immediately access the application without having to login separately.

**Why this priority**: Improves user experience by eliminating the extra login step after registration, reducing friction in the onboarding process.

**Independent Test**: Can be fully tested by creating a new user account and verifying that the registration response includes both user ID and JWT access token, allowing immediate access to protected features.

**Acceptance Scenarios**:

1. **Given** a user submits valid registration data, **When** the registration completes successfully, **Then** they receive HTTP 201 with their user ID and a valid JWT access token
2. **Given** a user just registered and received an access token, **When** they use this token to access protected endpoints, **Then** they are successfully authenticated without additional login

---

### User Story 3 - Clear Error Feedback on Invalid Input (Priority: P3)

Users who make mistakes during login (typos, validation errors, malformed input) should receive clear, specific error messages to help them correct their input.

**Why this priority**: Good error handling improves user experience and reduces support requests, but the system can function with basic error messages.

**Independent Test**: Can be fully tested by submitting various invalid inputs and verifying that appropriate HTTP status codes and error messages are returned.

**Acceptance Scenarios**:

1. **Given** a user submits a login request with an invalid email format, **When** validation runs, **Then** they receive HTTP 400 with a clear validation error message
2. **Given** a user submits a login request with missing required fields, **When** validation runs, **Then** they receive HTTP 400 with details of which fields are missing
3. **Given** a user submits a login request with a password that's too short, **When** validation runs, **Then** they receive HTTP 400 with password requirement details

---

### Edge Cases

- How does the system handle concurrent login attempts from the same user?
- What happens when a user attempts to login while already having an active session?
- How does the system handle extremely long email or password strings?
- What happens when special characters are used in passwords?

### Explicitly Out of Scope

- Rate limiting / brute force attack prevention (to be addressed in separate security feature)
- Expired token handling and error responses (to be addressed in authentication middleware feature)
- Email verification system (to be addressed in separate feature if needed)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide a POST endpoint at `/api/v1/users/login` that accepts user credentials
- **FR-002**: System MUST accept login requests using the same data structure (DTO) as the user registration endpoint
- **FR-003**: System MUST validate login request data using the same validation rules as user registration
- **FR-004**: System MUST verify that the provided email exists in the user database
- **FR-005**: System MUST securely verify that the provided password matches the stored password for the user
- **FR-006**: System MUST return HTTP 200 on successful authentication with response body containing `id` (user ID) and `accessToken` (JWT) fields
- **FR-007**: System MUST generate a valid JWT access token upon successful authentication
- **FR-007a**: System MUST set JWT access token expiration to 1 hour from issuance time
- **FR-007b**: System MUST include only the user ID in the JWT token payload (minimal payload principle)
- **FR-008**: System MUST return HTTP 401 when the email/password combination is incorrect
- **FR-009**: System MUST return HTTP 400 when request data fails validation
- **FR-010**: System MUST extend the POST `/api/v1/users` (registration) endpoint response to include `id` and `accessToken` fields
- **FR-011**: System MUST generate and return a JWT access token immediately upon successful user registration
- **FR-012**: System MUST use consistent error message format for validation failures across both login and registration endpoints
- **FR-013**: System MUST not reveal whether an email exists in the system through different error messages (prevent user enumeration)

### Key Entities

- **User**: Represents a registered user account with stored credentials (email, password hash) and a unique identifier
- **Access Token**: JWT token that authenticates user requests, contains user ID in payload and expires after 1 hour from issuance

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can successfully authenticate using valid credentials in under 2 seconds
- **SC-002**: Newly registered users receive immediate access credentials without requiring a separate login action
- **SC-003**: Invalid login attempts receive appropriate error responses (401 or 400) within 2 seconds
- **SC-004**: 95% of users with valid credentials complete authentication successfully on first attempt
- **SC-005**: Error messages for validation failures are clear enough that 80% of users can correct their input without assistance
- **SC-006**: The authentication system handles at least 100 concurrent login requests without performance degradation
