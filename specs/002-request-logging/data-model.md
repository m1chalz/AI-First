# Data Model: Log Entry Structures

**Feature**: 002-request-logging  
**Date**: 2025-11-17  
**Status**: Phase 1 Complete

## Overview

This document defines the structured JSON format for all log entries produced by the request/response logging system. All logs conform to Pino's standard JSON structure with custom fields for request correlation and HTTP-specific metadata.

---

## Core Log Entry Structure

All log entries share a common base structure:

```typescript
interface BaseLogEntry {
  /** Log level as string */
  level: 'TRACE' | 'DEBUG' | 'INFO' | 'WARN' | 'ERROR' | 'FATAL';
  
  /** ISO8601 timestamp with milliseconds (UTC) */
  time: string; // e.g., "2025-11-17T14:23:45.123Z"
  
  /** Unique request ID (10 alphanumeric characters) */
  requestId: string; // e.g., "aBc123XyZ9"
  
  /** Human-readable message */
  msg: string;
}
```

**Example**:
```json
{
  "level": "INFO",
  "time": "2025-11-17T14:23:45.123Z",
  "requestId": "aBc123XyZ9",
  "msg": "request received"
}
```

---

## Request Log Entry

Logged when an HTTP request is received (before processing).

```typescript
interface RequestLogEntry extends BaseLogEntry {
  /** HTTP request details */
  req: {
    /** HTTP method */
    method: string; // "GET" | "POST" | "PUT" | "DELETE" | "PATCH" | "OPTIONS" | "HEAD"
    
    /** Full URL path including query parameters */
    url: string; // e.g., "/api/pets?species=dog"
    
    /** HTTP headers (Authorization redacted) */
    headers: Record<string, string | string[]>;
    
    /** Request body (truncated if >10KB, omitted if binary) */
    body?: RequestBody;
  };
  
  /** Message for request logs */
  msg: "request received";
}

type RequestBody = 
  | any // Normal body (JSON, form data, etc.)
  | TruncatedBody
  | BinaryOmittedBody;

interface TruncatedBody {
  /** Truncated content (first 10KB) */
  content: string;
  
  /** Flag indicating truncation occurred */
  truncated: true;
  
  /** Original size in bytes */
  originalSize: number;
}

interface BinaryOmittedBody {
  /** Flag indicating binary content was omitted */
  binaryOmitted: true;
  
  /** Content-Type header value */
  contentType: string;
  
  /** Content length from header */
  contentLength: string | number;
}
```

**Example - Normal Request**:
```json
{
  "level": "INFO",
  "time": "2025-11-17T14:23:45.123Z",
  "requestId": "aBc123XyZ9",
  "req": {
    "method": "GET",
    "url": "/api/pets?species=dog",
    "headers": {
      "host": "api.petspot.com",
      "user-agent": "curl/7.64.1",
      "accept": "application/json",
      "authorization": "***"
    }
  },
  "msg": "request received"
}
```

**Example - POST with Body**:
```json
{
  "level": "INFO",
  "time": "2025-11-17T14:23:45.123Z",
  "requestId": "xYz789AbC1",
  "req": {
    "method": "POST",
    "url": "/api/pets",
    "headers": {
      "host": "api.petspot.com",
      "content-type": "application/json",
      "authorization": "***"
    },
    "body": {
      "name": "Max",
      "species": "dog",
      "ownerId": "user-456"
    }
  },
  "msg": "request received"
}
```

**Example - Truncated Body**:
```json
{
  "level": "INFO",
  "time": "2025-11-17T14:23:45.123Z",
  "requestId": "pQr456StU2",
  "req": {
    "method": "POST",
    "url": "/api/pets/bulk-import",
    "headers": {
      "host": "api.petspot.com",
      "content-type": "application/json"
    },
    "body": {
      "content": "[{\"id\":1,\"name\":\"Max\"},{\"id\":2,\"name\":\"Luna\"},...truncated...",
      "truncated": true,
      "originalSize": 152340
    }
  },
  "msg": "request received"
}
```

**Example - Binary Content**:
```json
{
  "level": "INFO",
  "time": "2025-11-17T14:23:45.123Z",
  "requestId": "mNo123PqR4",
  "req": {
    "method": "POST",
    "url": "/api/pets/123/avatar",
    "headers": {
      "host": "api.petspot.com",
      "content-type": "image/jpeg"
    },
    "body": {
      "binaryOmitted": true,
      "contentType": "image/jpeg",
      "contentLength": "245678"
    }
  },
  "msg": "request received"
}
```

---

## Response Log Entry

Logged when an HTTP response is sent (after processing).

```typescript
interface ResponseLogEntry extends BaseLogEntry {
  /** HTTP request details (for correlation) */
  req: {
    /** HTTP method */
    method: string;
    
    /** Full URL path */
    url: string;
  };
  
  /** HTTP response details */
  res: {
    /** HTTP status code */
    statusCode: number; // 200, 201, 400, 404, 500, etc.
    
    /** Response headers */
    headers?: Record<string, string | string[]>;
    
    /** Response body (truncated if >10KB, omitted if binary) */
    body?: ResponseBody;
  };
  
  /** Response time in milliseconds */
  responseTime: number;
  
  /** Message for response logs */
  msg: string; // e.g., "request completed" or "GET /api/pets completed in 15ms"
}

type ResponseBody = 
  | any // Normal body (JSON, HTML, etc.)
  | TruncatedBody
  | BinaryOmittedBody;
```

**Example - Successful Response**:
```json
{
  "level": "INFO",
  "time": "2025-11-17T14:23:45.789Z",
  "requestId": "aBc123XyZ9",
  "req": {
    "method": "GET",
    "url": "/api/pets?species=dog"
  },
  "res": {
    "statusCode": 200,
    "headers": {
      "content-type": "application/json",
      "request-id": "aBc123XyZ9"
    },
    "body": [
      {
        "id": "pet-1",
        "name": "Max",
        "species": "dog"
      },
      {
        "id": "pet-2",
        "name": "Buddy",
        "species": "dog"
      }
    ]
  },
  "responseTime": 666,
  "msg": "request completed"
}
```

**Example - Error Response**:
```json
{
  "level": "ERROR",
  "time": "2025-11-17T14:23:46.123Z",
  "requestId": "dEf456GhI7",
  "req": {
    "method": "GET",
    "url": "/api/pets/999"
  },
  "res": {
    "statusCode": 404,
    "headers": {
      "content-type": "application/json",
      "request-id": "dEf456GhI7"
    },
    "body": {
      "error": "Pet not found"
    }
  },
  "responseTime": 45,
  "msg": "request completed"
}
```

---

## Application Log Entry

Logged by business logic during request processing.

```typescript
interface ApplicationLogEntry extends BaseLogEntry {
  /** Request ID (automatically included via AsyncLocalStorage) */
  requestId: string;
  
  /** Log level appropriate to the message */
  level: 'TRACE' | 'DEBUG' | 'INFO' | 'WARN' | 'ERROR' | 'FATAL';
  
  /** Custom message from application code */
  msg: string;
  
  /** Optional custom fields */
  [key: string]: any;
}
```

**Example - Info Log**:
```json
{
  "level": "INFO",
  "time": "2025-11-17T14:23:45.500Z",
  "requestId": "aBc123XyZ9",
  "msg": "fetching pets from database",
  "query": {
    "species": "dog"
  }
}
```

**Example - Error Log**:
```json
{
  "level": "ERROR",
  "time": "2025-11-17T14:23:46.000Z",
  "requestId": "jKl789MnO3",
  "msg": "database connection failed",
  "error": {
    "message": "ECONNREFUSED",
    "code": "ECONNREFUSED",
    "errno": -61,
    "syscall": "connect"
  }
}
```

---

## Field Specifications

### Request ID (`requestId`)
- **Format**: 10 alphanumeric characters (A-Z, a-z, 0-9)
- **Example**: `"aBc123XyZ9"`
- **Generation**: Random selection from 62-character charset
- **Uniqueness**: 62^10 ≈ 839 quadrillion combinations
- **Location**: Top-level field in all log entries
- **Propagation**: Via AsyncLocalStorage, available throughout request lifecycle

### Timestamp (`time`)
- **Format**: ISO8601 with milliseconds (UTC)
- **Example**: `"2025-11-17T14:23:45.123Z"`
- **Timezone**: Always UTC (Z suffix)
- **Precision**: Milliseconds (3 decimal places)

### Log Level (`level`)
- **Type**: String
- **Values**:
  - `"TRACE"` - Very detailed debugging
  - `"DEBUG"` - Debugging information
  - `"INFO"` - Informational messages (default for request/response logs)
  - `"WARN"` - Warning messages
  - `"ERROR"` - Error messages
  - `"FATAL"` - Fatal errors (process termination)

### HTTP Method (`req.method`)
- **Format**: Uppercase string
- **Values**: `"GET"`, `"POST"`, `"PUT"`, `"DELETE"`, `"PATCH"`, `"OPTIONS"`, `"HEAD"`

### HTTP Status Code (`res.statusCode`)
- **Type**: Integer
- **Ranges**:
  - `2xx` - Success (200 OK, 201 Created, 204 No Content)
  - `3xx` - Redirection (301 Moved, 302 Found, 304 Not Modified)
  - `4xx` - Client errors (400 Bad Request, 401 Unauthorized, 404 Not Found)
  - `5xx` - Server errors (500 Internal Server Error, 503 Service Unavailable)

### Response Time (`responseTime`)
- **Type**: Number (milliseconds)
- **Precision**: Integer (rounded)
- **Example**: `666` (666ms)

### Headers (`req.headers`, `res.headers`)
- **Type**: `Record<string, string | string[]>`
- **Normalization**: Keys lowercase (Express convention)
- **Redaction**: `authorization` header value replaced with `"***"`

### Body (`body`)
- **Type**: `any` (JSON-serializable) OR `TruncatedBody` OR `BinaryOmittedBody`
- **Truncation**: Applied when serialized size exceeds 10KB (10,240 bytes)
- **Binary Detection**: Based on `Content-Type` header
- **Binary MIME Types**: `image/*`, `video/*`, `audio/*`, `application/pdf`, `application/octet-stream`, `application/zip`, etc.

---

## Validation Rules

### Request ID Validation
- MUST be exactly 10 characters
- MUST contain only alphanumeric characters (A-Z, a-z, 0-9)
- MUST be unique per request (collision probability negligible)
- MUST appear in all log entries for the same request

### Timestamp Validation
- MUST be ISO8601 format
- MUST include timezone (Z for UTC)
- MUST include milliseconds

### Body Truncation Validation
- MUST truncate at exactly 10,240 bytes (10KB)
- MUST include `truncated: true` flag
- MUST include `originalSize` field

### Binary Content Validation
- MUST detect binary via Content-Type header
- MUST include `binaryOmitted: true` flag
- MUST include `contentType` and `contentLength` fields

### Header Redaction Validation
- Authorization header MUST be redacted to `"***"`
- Other headers MUST be preserved as-is

---

## Log Correlation

All logs related to a single HTTP request share the same `requestId`, enabling correlation:

**Query Example** (grep):
```bash
# Find all logs for request ID "aBc123XyZ9"
grep '"requestId":"aBc123XyZ9"' logs.json
```

**Query Example** (jq):
```bash
# Parse and filter by request ID
cat logs.json | jq 'select(.requestId == "aBc123XyZ9")'
```

**Query Example** (Elasticsearch):
```json
{
  "query": {
    "match": {
      "requestId": "aBc123XyZ9"
    }
  }
}
```

---

## TypeScript Type Definitions

Complete TypeScript types for implementation:

```typescript
// /server/src/types/log-entries.ts

/** Base structure for all log entries */
export interface BaseLogEntry {
  level: 'TRACE' | 'DEBUG' | 'INFO' | 'WARN' | 'ERROR' | 'FATAL';
  time: string;
  requestId: string;
  msg: string;
}

/** HTTP request log entry */
export interface RequestLogEntry extends BaseLogEntry {
  req: {
    method: string;
    url: string;
    headers: Record<string, string | string[]>;
    body?: RequestBody;
  };
  msg: "request received";
}

/** HTTP response log entry */
export interface ResponseLogEntry extends BaseLogEntry {
  req: {
    method: string;
    url: string;
  };
  res: {
    statusCode: number;
    headers?: Record<string, string | string[]>;
    body?: ResponseBody;
  };
  responseTime: number;
  msg: string;
}

/** Application log entry */
export interface ApplicationLogEntry extends BaseLogEntry {
  requestId: string;
  [key: string]: any;
}

/** Normal, truncated, or binary-omitted body */
export type RequestBody = any | TruncatedBody | BinaryOmittedBody;
export type ResponseBody = any | TruncatedBody | BinaryOmittedBody;

/** Body truncated due to size limit */
export interface TruncatedBody {
  content: string;
  truncated: true;
  originalSize: number;
}

/** Binary content omitted from log */
export interface BinaryOmittedBody {
  binaryOmitted: true;
  contentType: string;
  contentLength: string | number;
}
```

---

## Examples by Scenario

### Scenario 1: Simple GET Request
```json
// REQUEST LOG
{
  "level": "INFO",
  "time": "2025-11-17T14:23:45.123Z",
  "requestId": "aBc123XyZ9",
  "req": {
    "method": "GET",
    "url": "/api/pets/123",
    "headers": {
      "host": "api.petspot.com",
      "accept": "application/json"
    }
  },
  "msg": "request received"
}

// APPLICATION LOG
{
  "level": "INFO",
  "time": "2025-11-17T14:23:45.456Z",
  "requestId": "aBc123XyZ9",
  "msg": "querying database for pet",
  "petId": "123"
}

// RESPONSE LOG
{
  "level": "INFO",
  "time": "2025-11-17T14:23:45.789Z",
  "requestId": "aBc123XyZ9",
  "req": {
    "method": "GET",
    "url": "/api/pets/123"
  },
  "res": {
    "statusCode": 200,
    "headers": {
      "content-type": "application/json",
      "request-id": "aBc123XyZ9"
    },
    "body": {
      "id": "123",
      "name": "Max",
      "species": "dog"
    }
  },
  "responseTime": 666,
  "msg": "request completed"
}
```

### Scenario 2: POST with Authentication
```json
// REQUEST LOG
{
  "level": "INFO",
  "time": "2025-11-17T15:00:00.000Z",
  "requestId": "xYz789AbC1",
  "req": {
    "method": "POST",
    "url": "/api/pets",
    "headers": {
      "host": "api.petspot.com",
      "content-type": "application/json",
      "authorization": "***"
    },
    "body": {
      "name": "Luna",
      "species": "cat",
      "ownerId": "user-789"
    }
  },
  "msg": "request received"
}

// RESPONSE LOG
{
  "level": "INFO",
  "time": "2025-11-17T15:00:00.234Z",
  "requestId": "xYz789AbC1",
  "req": {
    "method": "POST",
    "url": "/api/pets"
  },
  "res": {
    "statusCode": 201,
    "headers": {
      "content-type": "application/json",
      "request-id": "xYz789AbC1"
    },
    "body": {
      "id": "pet-456",
      "name": "Luna",
      "species": "cat",
      "ownerId": "user-789"
    }
  },
  "responseTime": 234,
  "msg": "request completed"
}
```

### Scenario 3: Error Handling
```json
// REQUEST LOG
{
  "level": "INFO",
  "time": "2025-11-17T16:00:00.000Z",
  "requestId": "pQr456StU2",
  "req": {
    "method": "DELETE",
    "url": "/api/pets/999",
    "headers": {
      "host": "api.petspot.com",
      "authorization": "***"
    }
  },
  "msg": "request received"
}

// APPLICATION LOG (ERROR)
{
  "level": "ERROR",
  "time": "2025-11-17T16:00:00.100Z",
  "requestId": "pQr456StU2",
  "msg": "pet not found in database",
  "petId": "999"
}

// RESPONSE LOG (ERROR)
{
  "level": "ERROR",
  "time": "2025-11-17T16:00:00.150Z",
  "requestId": "pQr456StU2",
  "req": {
    "method": "DELETE",
    "url": "/api/pets/999"
  },
  "res": {
    "statusCode": 404,
    "headers": {
      "content-type": "application/json",
      "request-id": "pQr456StU2"
    },
    "body": {
      "error": "Pet not found"
    }
  },
  "responseTime": 150,
  "msg": "request completed"
}
```

---

## Compliance Matrix

| Requirement | Field/Feature | Validation |
|-------------|---------------|-----------|
| FR-001 | `req.method`, `req.url`, `body`, `req.headers` | ✅ All present in RequestLogEntry |
| FR-002 | `res.statusCode`, `body`, `res.headers` | ✅ All present in ResponseLogEntry |
| FR-003 | `requestId` field | ✅ Generated for every request |
| FR-004 | 10-char alphanumeric | ✅ Enforced by generator |
| FR-005 | `requestId` in all logs | ✅ Propagated via AsyncLocalStorage |
| FR-006 | AsyncLocalStorage | ✅ Native Node.js module |
| FR-007 | `request-id` response header | ✅ Injected by middleware |
| FR-008 | ISO8601 timestamps | ✅ Pino default format |
| FR-009 | Log type distinction | ✅ Via `msg` field patterns |
| FR-010 | Correlation | ✅ Via shared `requestId` |
| FR-011 | Structured JSON | ✅ Pino native format |
| FR-012 | Body truncation at 10KB | ✅ `TruncatedBody` type |
| FR-013 | Binary content omission | ✅ `BinaryOmittedBody` type |
| FR-014 | Authorization redaction | ✅ Pino redact option |

---

**Data Model Status**: ✅ Complete - All log entry structures defined
**Next Phase**: Implementation (create middleware and utilities)

