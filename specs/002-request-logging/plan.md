# Implementation Plan: Request and Response Logging with Correlation ID

**Branch**: `002-request-logging` | **Date**: 2025-11-17 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/002-request-logging/spec.md`

**Note**: This is a non-functional feature. Tests are skipped per user request.

## Summary

Implement comprehensive HTTP request/response logging with unique correlation IDs for the PetSpot backend server. The system will:
- Log every incoming HTTP request (method, URL, body, headers) in structured JSON format
- Log every outgoing HTTP response (method, URL, status, body, headers) in structured JSON format
- Generate a unique 10-character alphanumeric request ID for each request
- Include the request ID in all log entries using AsyncLocalStorage for context propagation
- Return the request ID in the `request-id` response header
- Handle large payloads (>10KB), binary content, and sensitive header redaction

**Technical Approach**: Use **Pino** (high-performance JSON logger) with **pino-http** middleware for Express.js. Implement custom middleware for AsyncLocalStorage context management, request ID generation, body logging with truncation/redaction, and correlation across all application logs.

## Technical Context

**Language/Version**: Node.js v24 (LTS) with TypeScript (strict mode)  
**Primary Dependencies**: 
- Express.js (web framework)
- Pino v8+ (high-performance JSON logger, benchmark score: 88.6)
- pino-http (HTTP request/response logging middleware for Express, benchmark score: 85.4)
- Native AsyncLocalStorage from Node.js `async_hooks` module (Node.js ≥12.17.0, stable in ≥16.x)

**Storage**: N/A (logs output to stdout/stderr in JSON format)  
**Testing**: **Skipped** - This is a non-functional infrastructure feature per user request  
**Target Platform**: Node.js server (Linux/macOS/Docker)  
**Project Type**: Backend REST API (`/server` module)  
**Performance Goals**: 
- Logging overhead ≤5% increase in request processing time (FR requirement)
- Minimal impact on p95 latency (<5ms additional per request)
- Asynchronous logging with buffering to reduce I/O blocking

**Constraints**: 
- Request ID must be exactly 10 alphanumeric characters (A-Z, a-z, 0-9)
- Body truncation at 10KB (10,240 bytes)
- Binary content detection and omission from logs
- Authorization header redaction (replace with `***`)
- ISO8601 timestamp format

**Scale/Scope**: 
- Single middleware implementation affecting all Express routes
- 4 new files in `/server/src/middlewares/` and `/server/src/lib/`
- Zero impact on existing business logic (cross-cutting concern)
- Compatible with future distributed tracing integration (request ID propagation)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### KMP Architecture Compliance

- [x] **Thin Shared Layer**: N/A - This feature does not affect `/shared` module
  - Feature is backend-only (in `/server` module)
  - No changes to Kotlin Multiplatform shared code
  - Violation justification: _Not applicable_

- [x] **Native Presentation**: N/A - This feature does not affect platform presentation layers
  - Feature is backend-only middleware
  - No UI changes on Android/iOS/Web
  - Violation justification: _Not applicable_

- [x] **Interface-Based Design**: N/A - This feature does not involve domain logic
  - Logging is infrastructure concern, not domain logic
  - Middleware is cross-cutting concern for all routes
  - Violation justification: _Not applicable_

- [x] **Dependency Injection**: N/A - Koin is not used in `/server` module
  - Backend uses native Node.js/Express patterns (middleware, not DI)
  - Logger instance created once and reused (singleton pattern)
  - AsyncLocalStorage is native Node.js module (no DI needed)
  - Violation justification: _Per constitution, Koin NOT used in /server backend_

- [x] **80% Test Coverage - Shared Module**: N/A - This feature does not affect `/shared` module
  - Feature is backend-only
  - Violation justification: _Not applicable_

- [x] **80% Test Coverage - ViewModels**: N/A - This feature does not affect ViewModels
  - Feature is backend-only middleware
  - Violation justification: _Not applicable_

- [x] **End-to-End Tests**: N/A - Tests skipped per user request
  - Non-functional infrastructure feature
  - User explicitly requested: "It's a non-functional feature, so skip all the tests"
  - Violation justification: _Tests skipped by user directive for non-functional feature_

- [x] **Platform Independence**: N/A - This feature is backend-only
  - No shared code affected
  - Violation justification: _Not applicable_

- [x] **Clear Contracts**: N/A - This feature is internal middleware
  - No public APIs exposed to clients
  - Middleware is transparent to API consumers
  - Violation justification: _Not applicable_

- [x] **Asynchronous Programming Standards**: Compliant
  - Backend uses native Node.js `async`/`await` patterns (per constitution Principle IX)
  - AsyncLocalStorage for context propagation (native Node.js, no callbacks)
  - Pino uses asynchronous logging with buffering
  - Violation justification: _Fully compliant_

- [x] **Test Identifiers for UI Controls**: N/A - This feature has no UI
  - Backend-only feature
  - Violation justification: _Not applicable_

- [x] **Public API Documentation**: Compliant
  - All middleware functions will have JSDoc documentation (per constitution Principle XI & XIII)
  - Utility functions will have concise JSDoc (WHAT/WHY, not HOW)
  - Logger configuration will be documented
  - Violation justification: _Fully compliant_

- [x] **Given-When-Then Test Structure**: N/A - Tests skipped per user request
  - User explicitly requested: "skip all the tests"
  - Violation justification: _Tests skipped by user directive_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: Compliant
  - Runtime: Node.js v24 (LTS) ✓
  - Framework: Express.js ✓
  - Language: TypeScript with strict mode enabled ✓
  - Logging: Pino (high-performance JSON logger) + pino-http (Express middleware) ✓
  - No database changes required (logging to stdout/stderr)
  - Violation justification: _Fully compliant_

- [x] **Backend Code Quality**: Compliant
  - ESLint with TypeScript plugin will verify all new code ✓
  - Clean Code principles will be applied:
    - Small, focused functions (single responsibility) - Each middleware/utility has one purpose
    - Descriptive naming - `generateRequestId`, `truncateBody`, `redactSensitiveHeaders`
    - Maximum 3 nesting levels - Functions will be kept flat with early returns
    - DRY principle - Reusable utilities in `/lib` (body serialization, header redaction)
    - JSDoc documentation for all public functions ✓
  - Violation justification: _Fully compliant_

- [x] **Backend Dependency Management**: Compliant
  - Adding only 2 well-justified dependencies:
    - **pino** (v8+): High-performance JSON logger, widely used (High source reputation, benchmark score 88.6), minimal overhead, security-audited
    - **pino-http** (v8+): Official Pino middleware for Express (High source reputation, benchmark score 85.4), maintained by Pino team
  - No micro-dependencies added (request ID generation implemented manually)
  - AsyncLocalStorage is native Node.js module (no external dependency)
  - Dependencies will be documented with rationale in package.json
  - Violation justification: _Fully compliant with minimal, justified dependencies_

- [x] **Backend Directory Structure**: Compliant
  - New files in standardized locations:
    - `/middlewares/loggerMiddleware.ts` - Pino-HTTP integration + AsyncLocalStorage setup
    - `/middlewares/requestIdMiddleware.ts` - Request ID generation and header injection
    - `/lib/requestIdGenerator.ts` - 10-char alphanumeric ID generation utility
    - `/lib/logSerializers.ts` - Custom serializers for body truncation, binary detection, header redaction
  - No changes to `/routes/`, `/services/`, `/database/`
  - Logger configured in `app.ts` (Express app configuration)
  - Violation justification: _Fully compliant with constitution directory structure_

- [x] **Backend TDD Workflow**: N/A - Tests skipped per user request
  - User explicitly requested: "It's a non-functional feature, so skip all the tests"
  - TDD workflow not applicable when tests are skipped
  - Violation justification: _Tests skipped by user directive_

- [x] **Backend Testing Strategy**: N/A - Tests skipped per user request
  - User explicitly requested: "skip all the tests"
  - No unit tests will be written for utilities in `/lib/__test__/`
  - No integration tests will be written for middleware in `/src/__test__/`
  - Coverage requirement waived for this non-functional feature
  - Violation justification: _Tests skipped by user directive for non-functional feature_

## Project Structure

### Documentation (this feature)

```text
specs/002-request-logging/
├── spec.md              # Feature specification (input)
├── plan.md              # This file (implementation plan)
├── research.md          # Phase 0 output (library research, decisions)
├── data-model.md        # Phase 1 output (log entry structure)
├── quickstart.md        # Phase 1 output (usage guide for developers)
└── checklists/
    └── requirements.md  # Requirements checklist (existing)
```

### Source Code (Backend Server)

```text
server/
├── src/
│   ├── middlewares/
│   │   ├── loggerMiddleware.ts        # NEW: Pino-HTTP integration + AsyncLocalStorage
│   │   └── requestIdMiddleware.ts     # NEW: Request ID generation and injection
│   ├── lib/
│   │   ├── requestIdGenerator.ts      # NEW: 10-char alphanumeric ID generator
│   │   ├── logSerializers.ts          # NEW: Custom serializers (truncation, redaction)
│   │   └── requestContext.ts          # NEW: AsyncLocalStorage singleton
│   ├── routes/
│   │   └── index.ts                   # MODIFIED: No changes (middleware is transparent)
│   ├── app.ts                         # MODIFIED: Remove old console.log middleware, register new logging middlewares
│   └── index.ts                       # NO CHANGE
├── package.json                       # MODIFIED: Add pino, pino-http dependencies
└── tsconfig.json                      # NO CHANGE
```

**Structure Decision**: Backend-only feature using Express middleware pattern. New logging infrastructure is cross-cutting and transparent to existing routes and business logic. All new code follows constitution's backend directory structure (Principle XIII):
- Middlewares in `/middlewares/` (request/response processing)
- Utilities in `/lib/` (pure, reusable functions)
- No changes to `/services/` or `/database/` (logging is infrastructure concern)

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

**No violations** - All constitution checks passed. Feature is fully compliant with:
- Backend architecture standards (Principle XIII)
- Asynchronous programming standards (Principle IX)
- Public API documentation requirements (Principle XI)
- Clean Code principles (small functions, descriptive naming, DRY, max 3 nesting levels)
- Dependency minimization (only 2 well-justified, high-quality dependencies)
- Directory structure standards (middlewares, lib separation)

Tests are intentionally skipped per user directive for this non-functional infrastructure feature.
