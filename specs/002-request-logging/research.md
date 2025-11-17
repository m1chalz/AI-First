# Research: Request and Response Logging with Correlation ID

**Feature**: 002-request-logging  
**Date**: 2025-11-17  
**Status**: Phase 0 Complete

## Research Questions

### 1. Which logging library should we use for Node.js/Express?

**Decision**: **Pino** with **pino-http** middleware

**Rationale**:
- **Performance**: Pino is one of the fastest JSON loggers for Node.js (benchmark score: 88.6/100)
  - Uses asynchronous logging with buffering to minimize I/O blocking
  - Low overhead (~5ms per request, aligns with <5% performance constraint)
  - Outperforms Winston, Bunyan, and Morgan in benchmarks
  
- **Structured JSON Logging**: Native JSON output (FR-011 requirement)
  - All log entries are JSON objects by default
  - Easy integration with log aggregation tools (ELK, Datadog, Splunk)
  - No additional formatting needed
  
- **Express Integration**: Official `pino-http` middleware (benchmark score: 85.4/100)
  - Designed specifically for HTTP request/response logging
  - Automatic request/response serialization
  - Built-in support for custom serializers (needed for body truncation, header redaction)
  - Supports custom properties injection (needed for request ID correlation)
  
- **Maturity & Maintenance**: High source reputation
  - Actively maintained by the Pino.js team
  - Large community (used by Fastify, Platformatic, and many enterprise projects)
  - Security-audited and regularly updated
  - Comprehensive documentation with TypeScript support

**Alternatives Considered**:
- **Morgan**: Simple HTTP logger but lacks structured JSON output by default, requires custom formatters, no built-in async context support
- **Winston**: Popular but slower than Pino, more complex configuration, higher overhead
- **Bunyan**: Similar to Pino but less actively maintained, smaller community
- **Custom console.log**: No structure, no performance optimization, manual serialization needed

**Supporting Evidence**:
- Pino documentation: https://github.com/pinojs/pino
- Pino-HTTP documentation: https://github.com/pinojs/pino-http
- Context7 library research: 170 code snippets for Pino, 30 for pino-http (High source reputation)

---

### 2. How should we generate unique request IDs?

**Decision**: **Custom 10-character alphanumeric generator** (no external library)

**Rationale**:
- **Spec Requirement**: Must be exactly 10 characters, alphanumeric (A-Z, a-z, 0-9) - 62 possible characters
- **Uniqueness**: 62^10 ≈ 839 quadrillion combinations (collision probability negligible for typical workloads)
- **No Dependencies**: Implementing manually avoids micro-dependency (aligns with constitution's dependency minimization)
- **Performance**: Simple random selection is fast (<1ms per ID)
- **Implementation**: Use Node.js `crypto.randomInt()` for cryptographic randomness

**Alternatives Considered**:
- **UUID (v4)**: Too long (36 characters with hyphens), doesn't meet spec requirement
- **nanoid**: External dependency for simple functionality (micro-dependency anti-pattern)
- **shortid**: Deprecated and vulnerable to collisions
- **Timestamp-based**: Not random enough, predictable, timezone issues

**Implementation Approach**:
```typescript
// /server/src/lib/requestIdGenerator.ts
import { randomInt } from 'crypto';

const CHARSET = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
const ID_LENGTH = 10;

export function generateRequestId(): string {
  let id = '';
  for (let i = 0; i < ID_LENGTH; i++) {
    id += CHARSET[randomInt(CHARSET.length)];
  }
  return id;
}
```

---

### 3. How should we propagate request ID to all application logs?

**Decision**: **AsyncLocalStorage** (native Node.js module from `async_hooks`)

**Rationale**:
- **Native Solution**: No external dependencies (available in Node.js ≥12.17.0, stable in ≥16.x)
- **Automatic Propagation**: Request ID available throughout async call chain without explicit parameter passing
- **Zero Code Changes**: Existing services and routes can access request ID via context without refactoring
- **Performance**: Minimal overhead (uses V8 async context tracking)
- **Constitution Compliance**: Aligns with Principle IX (no callbacks, native async patterns)

**Alternatives Considered**:
- **Continuation-Local-Storage (cls-hooked)**: Deprecated, replaced by native AsyncLocalStorage
- **Thread-local storage libraries**: Not needed, Node.js provides native solution
- **Manual parameter passing**: Requires refactoring all functions, violates DRY principle
- **Global variable**: Not safe for concurrent requests, leads to race conditions

**Implementation Approach**:
```typescript
// /server/src/lib/requestContext.ts
import { AsyncLocalStorage } from 'async_hooks';

interface RequestContext {
  requestId: string;
}

export const requestContextStorage = new AsyncLocalStorage<RequestContext>();

export function getRequestId(): string | undefined {
  return requestContextStorage.getStore()?.requestId;
}

export function setRequestContext(context: RequestContext): void {
  requestContextStorage.enterWith(context);
}
```

**Usage in Middleware**:
```typescript
// Set context at request start
app.use((req, res, next) => {
  const requestId = generateRequestId();
  setRequestContext({ requestId });
  req.id = requestId;
  next();
});

// Access anywhere in application
import { getRequestId } from './lib/requestContext';

function someBusinessLogic() {
  const requestId = getRequestId();
  logger.info({ requestId }, 'processing business logic');
}
```

---

### 4. How should we handle large request/response bodies in logs?

**Decision**: **Truncate at 10KB with custom serializer** + `"truncated": true` flag

**Rationale**:
- **Spec Requirement**: Bodies exceeding 10KB must be truncated (FR-012)
- **Performance**: Prevents memory exhaustion and excessive log storage
- **Pino Support**: Custom serializers can modify objects before logging
- **Visibility**: Truncation flag alerts developers that content was partial

**Implementation Approach**:
```typescript
// /server/src/lib/logSerializers.ts
const MAX_BODY_SIZE = 10240; // 10KB

export function truncateBody(body: any): any {
  if (!body) return body;
  
  const bodyString = typeof body === 'string' ? body : JSON.stringify(body);
  
  if (bodyString.length > MAX_BODY_SIZE) {
    return {
      content: bodyString.substring(0, MAX_BODY_SIZE),
      truncated: true,
      originalSize: bodyString.length
    };
  }
  
  return body;
}
```

**Alternatives Considered**:
- **Full logging**: Risks memory/storage issues, violates spec
- **Binary omission only**: Doesn't handle large JSON payloads
- **Sampling**: Complex, loses important debug data
- **Compression**: Adds CPU overhead, complicates log parsing

---

### 5. How should we detect and handle binary content?

**Decision**: **Content-Type header check** + custom serializer

**Rationale**:
- **Spec Requirement**: Binary content (images, PDFs, files) must be omitted from logs (FR-013)
- **Detection Method**: Check `Content-Type` header for binary MIME types
- **Logging Strategy**: Log Content-Type, size, and `"binaryOmitted": true` flag
- **Pino Support**: Custom serializers can inspect headers and conditionally omit body

**Binary MIME Types to Detect**:
- `image/*` (image/jpeg, image/png, image/gif, image/webp, etc.)
- `application/pdf`
- `application/octet-stream`
- `video/*`
- `audio/*`
- `application/zip`, `application/gzip`
- Any Content-Type NOT starting with `text/` or `application/json` or `application/xml`

**Implementation Approach**:
```typescript
// /server/src/lib/logSerializers.ts
const BINARY_CONTENT_TYPES = [
  /^image\//,
  /^video\//,
  /^audio\//,
  /^application\/pdf/,
  /^application\/octet-stream/,
  /^application\/zip/,
  /^application\/gzip/
];

export function isBinaryContent(contentType: string | undefined): boolean {
  if (!contentType) return false;
  return BINARY_CONTENT_TYPES.some(pattern => pattern.test(contentType));
}

export function serializeBody(body: any, contentType: string | undefined, headers: any): any {
  if (isBinaryContent(contentType)) {
    return {
      binaryOmitted: true,
      contentType,
      contentLength: headers['content-length'] || 'unknown'
    };
  }
  
  return truncateBody(body);
}
```

**Alternatives Considered**:
- **File extension detection**: Unreliable, not available in HTTP context
- **Magic number detection**: Too expensive (requires reading file bytes)
- **Size-based heuristic**: Inaccurate, large JSON is not binary
- **Manual configuration**: Error-prone, requires maintenance

---

### 6. How should we redact sensitive data in headers?

**Decision**: **Custom serializer for Authorization header** + Pino redact option

**Rationale**:
- **Spec Requirement**: Authorization header value must be replaced with `***` (FR-014)
- **Security**: Prevents credential exposure in logs (passwords, tokens, API keys)
- **Pino Support**: Built-in `redact` option with custom censor
- **Extensibility**: Easy to add more sensitive headers in future

**Implementation Approach**:
```typescript
// /server/src/middlewares/loggerMiddleware.ts
import pinoHttp from 'pino-http';

const logger = pinoHttp({
  redact: {
    paths: ['req.headers.authorization', 'res.headers.authorization'],
    censor: '***'
  },
  serializers: {
    req(req) {
      // Additional custom redaction if needed
      return req;
    }
  }
});
```

**Headers to Redact**:
- `Authorization` (primary target per spec)
- Future candidates (not in spec, but recommended):
  - `Cookie`
  - `X-API-Key`
  - `X-Auth-Token`

**Alternatives Considered**:
- **Full header omission**: Loses debugging info (header presence matters)
- **Hash values**: Irreversible but still identifiable, not compliant with `***` requirement
- **Manual string replacement**: Error-prone, less performant than built-in redaction
- **No redaction**: Security risk, violates spec

---

### 7. What timestamp format should be used?

**Decision**: **ISO8601 format** (default in Pino)

**Rationale**:
- **Spec Requirement**: FR-008 mandates ISO8601 format (e.g., `2025-11-17T14:23:45.123Z`)
- **Pino Default**: Pino uses ISO8601 by default (no configuration needed)
- **Benefits**: 
  - Timezone-agnostic (UTC)
  - Sortable as strings
  - Parseable by all log aggregation tools
  - Human-readable
  - Includes milliseconds for precision

**Example Output**:
```json
{
  "level": 30,
  "time": "2025-11-17T14:23:45.123Z",
  "requestId": "aBc123XyZ9",
  "msg": "request received"
}
```

**Alternatives Considered**:
- **Unix timestamp**: Not human-readable, requires conversion
- **Custom format**: Parsing issues, non-standard
- **Local timezone**: Ambiguous, server-dependent

---

### 8. How should we distinguish between request, response, and application logs?

**Decision**: **Consistent message prefixes** + log level conventions

**Rationale**:
- **Spec Requirement**: FR-009 requires clear distinction between log types
- **Pino-HTTP Behavior**: 
  - Automatic "request completed" message for responses
  - Custom messages for request received
  - Application logs use custom messages
- **Filtering**: Message patterns enable easy filtering in log aggregation tools

**Log Patterns**:
```json
// Request log
{
  "level": 30,
  "time": "2025-11-17T14:23:45.123Z",
  "requestId": "aBc123XyZ9",
  "req": { "method": "GET", "url": "/api/pets" },
  "msg": "request received"
}

// Response log
{
  "level": 30,
  "time": "2025-11-17T14:23:45.789Z",
  "requestId": "aBc123XyZ9",
  "res": { "statusCode": 200 },
  "responseTime": 666,
  "msg": "request completed"
}

// Application log
{
  "level": 30,
  "time": "2025-11-17T14:23:45.500Z",
  "requestId": "aBc123XyZ9",
  "msg": "fetching pets from database"
}
```

**Alternatives Considered**:
- **Separate log files**: Complicates correlation, violates structured logging
- **Log level distinction**: Confusing (not all requests are "info" level)
- **Custom field "logType"**: Redundant, message pattern sufficient

---

## Technology Stack Summary

| Component | Technology | Version | Rationale |
|-----------|-----------|---------|-----------|
| **Logger** | Pino | v8+ | High performance (88.6 score), native JSON, low overhead |
| **HTTP Middleware** | pino-http | v8+ | Official Pino Express integration (85.4 score) |
| **Request ID** | Custom generator | N/A | Meets 10-char spec, no dependencies |
| **Context Propagation** | AsyncLocalStorage | Native Node.js | Zero dependencies, automatic propagation |
| **TypeScript** | TypeScript | v5.2+ | Type safety, strict mode enabled |
| **Runtime** | Node.js | v24 LTS | Latest LTS, AsyncLocalStorage stable |

---

## Performance Considerations

### Expected Overhead
- **Request ID Generation**: <1ms per request (simple random selection)
- **AsyncLocalStorage**: <0.5ms per request (V8 native)
- **Pino Logging**: ~5ms per request (asynchronous buffering)
- **Serialization (truncation/redaction)**: <2ms per request (string operations)
- **Total Overhead**: ~8.5ms per request (<5% for requests >170ms)

### Optimization Strategies
1. **Asynchronous Logging**: Pino buffers writes (4KB chunks) before flushing
2. **Lazy Serialization**: Only serialize bodies when actually logged
3. **Early Binary Detection**: Check Content-Type before reading body
4. **Minimal Allocations**: Reuse serializers, avoid unnecessary object creation

### Monitoring
- Track response times before/after feature deployment
- Monitor log volume (truncation should keep it manageable)
- Alert if logging overhead exceeds 5% (FR requirement)

---

## Security Considerations

### Sensitive Data Protection
- **Authorization header**: Redacted with `***` (FR-014 compliant)
- **Binary content**: Omitted from logs (prevents accidental logging of user uploads)
- **Body truncation**: Limits exposure of potentially sensitive large payloads

### Future Enhancements (Out of Scope)
- PII detection in request/response bodies (regex-based)
- Cookie redaction (not in spec, but recommended)
- Credit card number detection (Luhn algorithm)
- Dynamic redaction rules (configuration-based)

---

## Integration with Existing Code

### Replacing Existing Logging
**Current State**: `app.ts` contains a simple console.log middleware (lines 15-18):
```typescript
app.use((req: Request, _res: Response, next: NextFunction) => {
  console.log(`Request ${req.method} ${req.url}`)
  next()
})
```

**Action Required**: This middleware will be **removed** and replaced with Pino-based logging that provides:
- Structured JSON output (vs plain text)
- Request/response body logging (vs URL only)
- Request ID correlation
- Header logging with redaction
- Response time tracking

### Zero-Impact Design
- **Middleware-based**: No changes to existing routes or business logic
- **Transparent**: Request ID automatically available via AsyncLocalStorage
- **Backward Compatible**: Existing `console.log` calls in other files still work (but won't include request ID)

### Migration Path
1. **Phase 1** (This Feature): Remove old middleware, install new Pino middleware, automatic request/response logging
2. **Phase 2** (Future): Gradually replace `console.log` with `req.log` in routes/services
3. **Phase 3** (Future): Structured logging guidelines for all application code

---

## Compliance with Constitution

### Principle IX: Asynchronous Programming Standards
- ✅ Uses native `async`/`await` (AsyncLocalStorage)
- ✅ No callbacks or Promise chains
- ✅ No deprecated patterns (cls-hooked)

### Principle XI: Public API Documentation
- ✅ All utilities will have JSDoc documentation
- ✅ Concise, high-level (WHAT/WHY, not HOW)

### Principle XIII: Backend Architecture & Quality Standards
- ✅ Node.js v24 with TypeScript strict mode
- ✅ ESLint with TypeScript plugin
- ✅ Clean Code principles (small functions, descriptive naming, max 3 nesting)
- ✅ Minimal dependencies (only 2, both well-justified)
- ✅ Directory structure compliant (middlewares, lib separation)

---

## References

- Pino Official Documentation: https://github.com/pinojs/pino
- Pino-HTTP Documentation: https://github.com/pinojs/pino-http
- AsyncLocalStorage API: https://nodejs.org/api/async_context.html
- Node.js Crypto Module: https://nodejs.org/api/crypto.html
- Express Middleware Guide: https://expressjs.com/en/guide/using-middleware.html
- ISO8601 Standard: https://www.iso.org/iso-8601-date-and-time-format.html

---

**Research Status**: ✅ Complete - All technical decisions made and justified
**Next Phase**: Phase 1 - Design (data-model.md, quickstart.md)

