# Quickstart: Request and Response Logging

**Feature**: 002-request-logging  
**Date**: 2025-11-17  
**Audience**: Backend developers working on the PetSpot server

## Overview

The request/response logging system automatically logs all HTTP traffic through the PetSpot API with unique correlation IDs. This guide shows you how to use the logging infrastructure in your code.

---

## What You Get Automatically

Once the logging middleware is installed, every HTTP request/response is automatically logged with:

✅ **Request logs**: HTTP method, URL, headers, body  
✅ **Response logs**: Status code, headers, body, response time  
✅ **Request ID**: Unique 10-character identifier for correlation  
✅ **Structured JSON**: Easy to parse and filter  
✅ **Security**: Authorization headers redacted, binary content omitted  
✅ **Performance**: <5% overhead with body truncation at 10KB  

**No code changes required** in your existing routes or services!

---

## Quick Examples

### 1. View Logs in Development

```bash
# Start the server (logs go to stdout)
cd server
npm run dev

# Make a request
curl http://localhost:3000/api/pets

# Output (pretty-printed for readability):
{
  "level": 30,
  "time": "2025-11-17T14:23:45.123Z",
  "requestId": "aBc123XyZ9",
  "req": {
    "id": "aBc123XyZ9",
    "method": "GET",
    "url": "/api/pets"
  },
  "msg": "request received"
}

{
  "level": 30,
  "time": "2025-11-17T14:23:45.789Z",
  "requestId": "aBc123XyZ9",
  "res": {
    "statusCode": 200
  },
  "responseTime": 666,
  "msg": "request completed"
}
```

### 2. Add Request ID to Your Logs

Use the logger attached to the request object:

```typescript
// In any route handler
app.get('/api/pets/:id', async (req, res) => {
  // Request ID is automatically included
  req.log.info({ petId: req.params.id }, 'fetching pet');
  
  const pet = await petService.getPetById(req.params.id);
  
  res.json(pet);
});

// Output:
{
  "level": 30,
  "time": "2025-11-17T14:23:45.456Z",
  "requestId": "aBc123XyZ9",
  "petId": "123",
  "msg": "fetching pet"
}
```

### 3. Log Errors with Request ID

```typescript
app.get('/api/pets/:id', async (req, res, next) => {
  try {
    const pet = await petService.getPetById(req.params.id);
    res.json(pet);
  } catch (error) {
    // Error logs automatically include request ID
    req.log.error({ err: error, petId: req.params.id }, 'failed to fetch pet');
    next(error);
  }
});

// Output:
{
  "level": 50,
  "time": "2025-11-17T14:23:46.000Z",
  "requestId": "aBc123XyZ9",
  "petId": "123",
  "err": {
    "message": "Pet not found",
    "stack": "Error: Pet not found\n    at ..."
  },
  "msg": "failed to fetch pet"
}
```

### 4. Get Request ID from Response Header

Clients can retrieve the request ID from the response header:

```bash
curl -I http://localhost:3000/api/pets

# Response:
HTTP/1.1 200 OK
Content-Type: application/json
request-id: aBc123XyZ9
...
```

Use this ID to search logs for debugging:

```bash
# Find all logs for this request
grep '"requestId":"aBc123XyZ9"' logs.json
```

---

## Logging Best Practices

### ✅ DO: Use Structured Logging

```typescript
// ✅ GOOD: Structured fields for easy filtering
req.log.info({ userId: user.id, action: 'login' }, 'user logged in');

// ❌ BAD: Unstructured string concatenation
req.log.info(`User ${user.id} logged in`);
```

### ✅ DO: Choose Appropriate Log Levels

```typescript
// TRACE (10): Very detailed debugging (disabled by default)
req.log.trace({ sql: query }, 'executing SQL query');

// DEBUG (20): Debugging information (disabled in production)
req.log.debug({ params: req.params }, 'processing request');

// INFO (30): Normal operation messages (default level)
req.log.info({ userId: user.id }, 'user authenticated');

// WARN (40): Warning messages (unexpected but handled)
req.log.warn({ retries: 3 }, 'retrying failed operation');

// ERROR (50): Error messages (operation failed)
req.log.error({ err: error }, 'database query failed');

// FATAL (60): Fatal errors (process termination)
req.log.fatal({ err: error }, 'critical system failure');
```

### ✅ DO: Include Context in Error Logs

```typescript
try {
  await updatePet(petId, data);
} catch (error) {
  // ✅ GOOD: Include relevant context
  req.log.error(
    { 
      err: error, 
      petId, 
      updateData: data,
      userId: req.user?.id 
    }, 
    'failed to update pet'
  );
}
```

### ✅ DO: Log Business Events

```typescript
// Track important business events
req.log.info({ orderId: order.id, amount: order.total }, 'order completed');
req.log.info({ userId: user.id, role: 'admin' }, 'admin access granted');
req.log.warn({ attempts: 5, ip: req.ip }, 'repeated login failures');
```

### ❌ DON'T: Log Sensitive Data Manually

```typescript
// ❌ BAD: Logging passwords, tokens, credit cards
req.log.info({ password: user.password }, 'user created'); // NEVER!
req.log.info({ apiKey: req.headers['x-api-key'] }, 'API call'); // NO!

// ✅ GOOD: Authorization header is auto-redacted in request logs
// ✅ GOOD: Omit sensitive fields from your logs
req.log.info({ userId: user.id }, 'user created');
```

### ❌ DON'T: Use console.log

```typescript
// ❌ BAD: No request ID, no structure, lost in production
console.log('Fetching pet', petId);

// ✅ GOOD: Request ID included automatically
req.log.info({ petId }, 'fetching pet');
```

---

## Advanced Usage

### Access Request ID in Services (No Request Object)

If you need the request ID in a service/utility that doesn't have access to `req`:

```typescript
// /server/src/services/petService.ts
import { getRequestId } from '../lib/requestContext';
import { logger } from '../lib/logger'; // Global logger instance

export async function getPetById(id: string): Promise<Pet> {
  const requestId = getRequestId(); // Get from AsyncLocalStorage
  
  logger.info({ requestId, petId: id }, 'querying database');
  
  const pet = await db.query('SELECT * FROM pets WHERE id = ?', [id]);
  
  return pet;
}
```

**Note**: `getRequestId()` only works during request processing (within middleware chain). It returns `undefined` if called outside request context (e.g., scheduled jobs).

### Custom Logger Configuration (Advanced)

For special cases, you can create a child logger with additional context:

```typescript
app.use((req, res, next) => {
  // Add permanent context to all logs in this request
  req.log = req.log.child({
    userId: req.user?.id,
    tenantId: req.headers['x-tenant-id']
  });
  
  next();
});

// Now all logs include userId and tenantId
req.log.info('processing request'); 
// {"requestId":"...", "userId":"123", "tenantId":"abc", "msg":"processing request"}
```

### Conditional Logging

```typescript
// Only log if certain conditions are met
if (req.query.debug === 'true') {
  req.log.debug({ query: req.query }, 'debug mode enabled');
}

// Log only in development
if (process.env.NODE_ENV === 'development') {
  req.log.trace({ headers: req.headers }, 'incoming headers');
}
```

---

## Searching and Filtering Logs

### Filter by Request ID

```bash
# Using grep
grep '"requestId":"aBc123XyZ9"' server/logs/app.log

# Using jq
cat server/logs/app.log | jq 'select(.requestId == "aBc123XyZ9")'
```

### Filter by Log Level

```bash
# Show only errors (level 50+)
cat logs.json | jq 'select(.level >= 50)'

# Show errors and warnings (level 40+)
cat logs.json | jq 'select(.level >= 40)'
```

### Filter by URL Pattern

```bash
# All logs for /api/pets endpoints
cat logs.json | jq 'select(.req.url | contains("/api/pets"))'
```

### Filter by Status Code

```bash
# All 404 responses
cat logs.json | jq 'select(.res.statusCode == 404)'

# All 5xx errors
cat logs.json | jq 'select(.res.statusCode >= 500)'
```

### Trace Complete Request Lifecycle

```bash
# Get request ID from any log entry
REQUEST_ID=$(cat logs.json | jq -r 'select(.req.url == "/api/pets/123") | .requestId' | head -1)

# Show all logs for that request
cat logs.json | jq "select(.requestId == \"$REQUEST_ID\")"
```

---

## Troubleshooting

### Problem: Logs not showing request ID

**Solution**: Ensure you're using `req.log` instead of `console.log`:

```typescript
// ❌ BAD: No request ID
console.log('Processing request');

// ✅ GOOD: Request ID included automatically
req.log.info('processing request');
```

### Problem: Request ID undefined in services

**Solution**: Services should accept `logger` as parameter or use global `getRequestId()`:

```typescript
// Option 1: Pass logger from route
app.get('/api/pets', (req, res) => {
  const pets = await petService.getPets(req.log);
});

// Option 2: Use getRequestId() in service
import { getRequestId } from '../lib/requestContext';

function getPets() {
  const requestId = getRequestId();
  logger.info({ requestId }, 'fetching pets');
}
```

### Problem: Logs too verbose in production

**Solution**: Set log level via environment variable:

```bash
# Development (shows DEBUG and above)
LOG_LEVEL=debug npm run dev

# Production (shows INFO and above)
LOG_LEVEL=info npm start

# Production (shows only WARN and above)
LOG_LEVEL=warn npm start
```

### Problem: Binary content logged despite configuration

**Solution**: Ensure Content-Type header is set correctly:

```typescript
// ✅ GOOD: Content-Type header set
res.setHeader('Content-Type', 'image/jpeg');
res.send(imageBuffer);

// ❌ BAD: No Content-Type, binary content logged as text
res.send(imageBuffer);
```

---

## Log Aggregation Tools

### Local Development: `pino-pretty`

For human-readable logs in development:

```bash
# Install pino-pretty (dev dependency)
npm install --save-dev pino-pretty

# Pipe logs through pino-pretty
npm run dev | pino-pretty
```

### Production: ELK Stack, Datadog, Splunk

Structured JSON logs work seamlessly with log aggregation tools:

**Elasticsearch Query**:
```json
{
  "query": {
    "bool": {
      "must": [
        { "match": { "requestId": "aBc123XyZ9" } }
      ]
    }
  }
}
```

**Datadog Query**:
```
requestId:aBc123XyZ9
```

**Splunk Query**:
```
source="petspot-api" requestId="aBc123XyZ9"
```

---

## Migration Guide (Existing Code)

### Step 1: Remove Old Middleware

The existing simple logging middleware in `app.ts` will be removed:

```typescript
// ❌ REMOVE THIS (lines 15-18 in app.ts)
app.use((req: Request, _res: Response, next: NextFunction) => {
  console.log(`Request ${req.method} ${req.url}`)
  next()
})
```

This is replaced by the new Pino-based logging that provides structured JSON, request IDs, body logging, and more.

### Step 2: Automatic Logging

No other changes needed! Request/response logging works immediately after middleware installation.

### Step 3: Replace console.log (Gradual)

Gradually replace `console.log` with structured logging:

```typescript
// BEFORE
console.log('Fetching user', userId);

// AFTER
req.log.info({ userId }, 'fetching user');
```

### Step 4: Add Context to Services

For services without `req` object, use `getRequestId()`:

```typescript
// BEFORE
function processOrder(orderId) {
  console.log('Processing order', orderId);
}

// AFTER
import { getRequestId } from '../lib/requestContext';
import { logger } from '../lib/logger';

function processOrder(orderId) {
  const requestId = getRequestId();
  logger.info({ requestId, orderId }, 'processing order');
}
```

---

## Performance Considerations

### Logging Overhead

- **Request ID generation**: <1ms per request
- **AsyncLocalStorage**: <0.5ms per request
- **Pino logging**: ~5ms per request (asynchronous buffering)
- **Serialization**: <2ms per request
- **Total**: ~8.5ms per request (<5% for requests >170ms)

### When Logs Are Written

- **Buffering**: Pino buffers logs in 4KB chunks before writing
- **Async I/O**: Log writes don't block request processing
- **Backpressure**: If logs can't be written fast enough, oldest entries are dropped (configurable)

### Monitoring Overhead

Check if logging overhead exceeds 5%:

```typescript
// Response time is automatically logged
// Monitor average responseTime in logs
cat logs.json | jq '.responseTime' | awk '{sum+=$1; count++} END {print sum/count}'
```

---

## API Reference

### Request Logger (req.log)

Available on all `req` objects:

```typescript
interface Logger {
  trace(obj: object, msg: string): void;
  debug(obj: object, msg: string): void;
  info(obj: object, msg: string): void;
  warn(obj: object, msg: string): void;
  error(obj: object, msg: string): void;
  fatal(obj: object, msg: string): void;
  
  // Convenience: message only
  info(msg: string): void;
  error(msg: string): void;
  // ... etc.
}
```

### Request Context API

```typescript
import { getRequestId, setRequestContext, requestContextStorage } from '../lib/requestContext';

// Get current request ID (or undefined if outside request context)
const requestId: string | undefined = getRequestId();

// Set context (used by middleware, rarely needed in application code)
setRequestContext({ requestId: 'aBc123XyZ9' });

// Direct access to AsyncLocalStorage (advanced)
const context = requestContextStorage.getStore();
```

### Request ID Generator

```typescript
import { generateRequestId } from '../lib/requestIdGenerator';

// Generate a new 10-character alphanumeric ID
const id: string = generateRequestId(); // e.g., "aBc123XyZ9"
```

---

## FAQ

### Q: Can I change the request ID format?

**A**: No, the spec requires exactly 10 alphanumeric characters. If you need a different format for external systems, generate a separate trace ID.

### Q: Are request IDs unique across server instances?

**A**: Yes, with 62^10 (839 quadrillion) combinations, collision probability is negligible. For distributed tracing, consider adding server/pod ID prefix.

### Q: How do I log outside request handlers (e.g., scheduled jobs)?

**A**: Use the global logger without `req`:

```typescript
import { logger } from '../lib/logger';

// Scheduled job (no request context)
cron.schedule('0 0 * * *', () => {
  logger.info('starting nightly cleanup job');
  // No requestId in logs (expected for background jobs)
});
```

### Q: Can I disable body logging for specific routes?

**A**: Currently, body logging is global. For sensitive routes, ensure data is sanitized before reaching Express (e.g., in middleware).

### Q: How do I rotate logs?

**A**: Use external tools like `logrotate` (Linux) or log aggregation services. Pino outputs to stdout; rotation is handled by the environment.

---

## Summary

**✅ Automatic request/response logging with unique IDs**  
**✅ Use `req.log` instead of `console.log` for correlation**  
**✅ Structured JSON logs for easy searching and filtering**  
**✅ Authorization headers auto-redacted, binary content omitted**  
**✅ <5% performance overhead with asynchronous logging**

**Next Steps**:
1. Start using `req.log` in your route handlers
2. Gradually replace `console.log` calls with structured logging
3. Use request IDs for debugging production issues

For implementation details, see:
- [research.md](./research.md) - Library decisions and rationale
- [data-model.md](./data-model.md) - Log entry structure and validation

---

**Status**: ✅ Ready for Implementation
**Last Updated**: 2025-11-17

