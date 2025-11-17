# Feature Specification: Request and Response Logging with Correlation ID

**Feature Branch**: `002-request-logging`  
**Created**: November 17, 2025  
**Status**: Draft  
**Input**: User description: "Aplikacja backendowa powinna logować: każdy przychodzący request - metoda HTTP, URL, body, headers; każdy response w formie metoda HTTP, URL, status HTTP, response body, headers; każdy nadchodzący request powinien mieć nadawane request ID które będzie umożliwiało korelację logów; Request ID ma być generowane jako losowy alfanumeryczny string o długości 10 znaków; Request ID ma być też zwracane w odpowiedzi w headerze request-id; wszystkie inne logi wyprodukowane podczas obsługiwania requestu również powinny zawierać request ID"

## Clarifications

### Session 2025-11-17

- Q: What log output format should be used for request/response logging? → A: Structured JSON logs (e.g., `{"timestamp": "...", "requestId": "...", "method": "GET", ...}`)
- Q: How should large request/response bodies be handled in logs? → A: Truncate bodies exceeding 10KB and add `"truncated": true` field in log entry
- Q: How should binary content (images, files) in requests/responses be handled in logs? → A: Log content type and size only, omit body, add `"binaryOmitted": true` field
- Q: How should sensitive data in headers (passwords, tokens, API keys) be handled in logs? → A: Replace Authorization header value with `***`
- Q: How should the request ID be made available to all application code for logging? → A: Use AsyncLocalStorage (Node.js native) to store request ID in async context
- Q: What format should be used for timestamps in log entries? → A: ISO8601 format

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Request/Response Tracing for Debugging (Priority: P1)

As a developer or operations engineer, I need to trace the complete lifecycle of an HTTP request through the system (incoming request and outgoing response) so that I can quickly debug issues and understand what data was sent and received.

**Why this priority**: This is the core functionality - without comprehensive logging of both requests and responses, the entire feature has no value. This enables basic troubleshooting and system monitoring.

**Independent Test**: Can be fully tested by making any HTTP request to the backend and verifying that both the request details (method, URL, body, headers) and response details (method, URL, status, body, headers) appear in the logs.

**Acceptance Scenarios**:

1. **Given** the backend server is running, **When** a client sends an HTTP request with specific method, URL, body, and headers, **Then** all these details are logged with timestamp
2. **Given** the backend has processed a request, **When** it returns a response, **Then** the response method, URL, status code, body, and headers are logged with timestamp
3. **Given** multiple requests are processed, **When** reviewing logs, **Then** each request-response pair can be identified and distinguished from others

---

### User Story 2 - Request Correlation with Request ID (Priority: P1)

As a developer troubleshooting a specific user issue, I need each request to have a unique identifier that links the request log entry with its corresponding response log entry and all other application logs generated during request processing, so that I can quickly filter and find all logs related to a single transaction.

**Why this priority**: Without request correlation, logs become nearly useless in high-traffic scenarios. This is essential for the feature to be practical in production environments. Since the user explicitly requires this functionality, it's P1.

**Independent Test**: Can be fully tested by making a single HTTP request that triggers application logging (e.g., database queries, business logic logs) and verifying that: (1) a unique request ID is generated, (2) the same request ID appears in request, response, and all application log entries, (3) the request ID is included in the response header named `request-id`, (4) the request ID is exactly 10 alphanumeric characters.

**Acceptance Scenarios**:

1. **Given** the backend receives an HTTP request, **When** processing begins, **Then** a unique 10-character alphanumeric request ID is generated
2. **Given** a request ID has been generated, **When** the request is logged, **Then** the request ID is included in the log entry
3. **Given** a request ID exists for the current request, **When** the response is logged, **Then** the same request ID is included in the response log entry
4. **Given** a request is being processed, **When** any application code generates log entries during request handling (e.g., business logic logs, database query logs, error logs), **Then** all these log entries include the same request ID
5. **Given** a request has been processed, **When** the response is sent to the client, **Then** the response includes a header named `request-id` containing the generated request ID
6. **Given** multiple concurrent requests are being processed, **When** reviewing logs by request ID, **Then** all log entries for a specific request ID relate to the same request-response cycle

---

### User Story 3 - Log Search and Filtering (Priority: P2)

As an operations engineer investigating a production issue, I need to quickly search logs by request ID to retrieve all related log entries, so that I can trace the complete flow of a problematic request without manually correlating timestamps.

**Why this priority**: This enhances the usability of the logging system but the basic logging and ID generation (P1) must work first. This story focuses on the operational value of the correlation mechanism.

**Independent Test**: Can be fully tested by: (1) making several HTTP requests to generate logs with different request IDs, (2) selecting one request ID from a response header, (3) searching/filtering logs by that request ID, (4) verifying that only logs related to that specific request appear in results.

**Acceptance Scenarios**:

1. **Given** logs contain entries for multiple requests, **When** searching by a specific request ID, **Then** only log entries matching that request ID are returned (including request logs, response logs, and all application logs)
2. **Given** a client reports an issue and provides their request ID from the response header, **When** operations searches logs by that ID, **Then** complete transaction details are retrieved including request, response, and all intermediate application logs
3. **Given** logs are formatted with structured data, **When** using log aggregation tools, **Then** request IDs can be used as a filter/search key to retrieve all related logs

---

### Edge Cases

- **Large request/response bodies**: Bodies exceeding 10KB will be truncated in logs with a `"truncated": true` indicator field to prevent performance degradation and excessive log storage consumption
- **Binary content**: Binary content (images, PDFs, file uploads) will not be logged; instead, the log entry will include Content-Type, content size, and a `"binaryOmitted": true` field to preserve log readability and reduce storage costs
- **Sensitive data in logs**: The `Authorization` header value will be redacted (replaced with `***`) to prevent credential exposure in logs while preserving header presence for debugging
- **Missing or malformed headers**: How should the system handle requests with no headers or unusual header formats?
- **Request ID uniqueness**: How is uniqueness guaranteed across multiple server instances in a distributed system? What is the collision probability with 10 alphanumeric characters?
- **High traffic volume**: How will logging perform under high request volume? Will it impact request processing time or require asynchronous logging?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST log every incoming HTTP request with the following details: HTTP method, full URL (including query parameters), request body, and all request headers
- **FR-002**: System MUST log every outgoing HTTP response with the following details: HTTP method, URL, HTTP status code, response body, and all response headers
- **FR-003**: System MUST generate a unique request ID for each incoming HTTP request
- **FR-004**: Request ID MUST be a random alphanumeric string (characters: A-Z, a-z, 0-9) of exactly 10 characters in length
- **FR-005**: System MUST include the generated request ID in all log entries associated with that request (including but not limited to: request logs, response logs, business logic logs, database operation logs, error logs, and any other application logs generated during request processing)
- **FR-006**: System MUST make the request ID available to all application code handling the request using AsyncLocalStorage (Node.js native async context) so that any logging performed during request processing can include the request ID without explicit parameter passing
- **FR-007**: System MUST include the request ID in the HTTP response sent to the client via a header named `request-id`
- **FR-008**: System MUST include timestamps in ISO8601 format (e.g., `2025-11-17T14:23:45.123Z`) in all log entries to enable temporal analysis and ensure consistent parsing across log aggregation tools
- **FR-009**: Log entries MUST clearly distinguish between request logs, response logs, and application logs
- **FR-010**: System MUST preserve the correlation between all logs related to a single request using the request ID as the linking key
- **FR-011**: All log entries MUST be formatted as structured JSON objects to enable automatic parsing and filtering by log aggregation tools
- **FR-012**: System MUST truncate request and response bodies exceeding 10KB (10,240 bytes) and include a `"truncated": true` field in the log entry to indicate partial content
- **FR-013**: System MUST detect binary content (non-text Content-Type such as image/*, application/pdf, application/octet-stream) and omit the body from logs, instead logging Content-Type, content size, and a `"binaryOmitted": true` field
- **FR-014**: System MUST redact the value of the `Authorization` header by replacing it with `***` in all logged request and response headers to prevent credential exposure

### Key Entities

- **HTTP Request Log Entry**: Represents a logged incoming request with attributes: request ID, timestamp (ISO8601), HTTP method, URL, request body, request headers
- **HTTP Response Log Entry**: Represents a logged outgoing response with attributes: request ID, timestamp (ISO8601), HTTP method, URL, HTTP status code, response body, response headers
- **Application Log Entry**: Represents any log generated during request processing (e.g., business logic, database operations, errors) with attributes: request ID, timestamp (ISO8601), log level, message, context
- **Request ID**: A unique 10-character alphanumeric identifier that correlates all log entries (request, response, and application logs) for the same transaction

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of HTTP requests processed by the backend are logged with complete details (method, URL, body, headers)
- **SC-002**: 100% of HTTP responses sent by the backend are logged with complete details (method, URL, status, body, headers)
- **SC-003**: 100% of application logs generated during request processing include the request ID
- **SC-004**: Every request-response pair and all associated application logs can be correlated using a unique request ID present in all log entries
- **SC-005**: Every HTTP response includes a `request-id` header that clients can use for support inquiries
- **SC-006**: Operations engineers can locate all logs (request, response, and application logs) for a specific transaction in under 30 seconds using the request ID
- **SC-007**: Request IDs are unique with no collisions observed during typical daily operation (assuming reasonable daily request volume)
- **SC-008**: Logging overhead does not increase average request processing time by more than 5%

## Assumptions

1. **Logging infrastructure**: The backend will output logs to stdout/stderr in JSON format; log aggregation and storage (if needed) is handled externally
2. **Log storage**: Structured JSON logs enable searching and filtering by request ID using standard tools (grep, jq, log aggregation systems)
3. **Performance acceptable**: The overhead of synchronous logging with truncation/redaction rules (SC-008: ≤5% increase) is acceptable for initial implementation
4. **Request ID collision**: With 10 alphanumeric characters (62^10 ≈ 839 quadrillion combinations), collision probability is negligible for typical workloads
5. **Synchronous logging**: Initial implementation will use synchronous logging; asynchronous logging can be optimized later if performance requires
6. **Single server instance**: Initial implementation assumes single server instance; distributed ID generation strategy will be addressed if needed for multi-instance deployments
7. **Node.js version**: AsyncLocalStorage is available (Node.js ≥12.17.0, stable in ≥16.x)

## Dependencies

- **AsyncLocalStorage**: Native Node.js module (available in `async_hooks` package, Node.js ≥12.17.0, no external dependency)
- No external logging libraries required for initial implementation (uses native `console` with JSON.stringify)

## Out of Scope

- Log aggregation and visualization tools (assumed to exist or be implemented separately)
- Advanced sensitive data redaction beyond Authorization header (e.g., PII in bodies, additional sensitive headers like Cookie, X-API-Key - requires separate security specification)
- Log retention policies and archival
- Performance optimization for high-traffic scenarios (async logging, sampling)
- Integration with distributed tracing systems (e.g., OpenTelemetry, Jaeger)
- Request ID propagation to downstream services in microservice architectures
