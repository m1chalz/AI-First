import { createRequire } from 'module';
const require = createRequire(import.meta.url);

const pinoHttp = require('pino-http');
const pino = require('pino');

import { serializeBody } from '../lib/logSerializers.ts';
import { getRequestId } from '../lib/requestContext.ts';

/**
 * Pino HTTP logger middleware for Express.js.
 *
 * Provides comprehensive request/response logging with:
 * - Structured JSON output for all HTTP traffic
 * - Request body, method, URL, headers logging
 * - Response body, status, headers, response time logging
 * - Authorization header redaction (replaced with ***)
 * - Body truncation at 10KB with metadata
 * - Binary content omission (images, PDFs, etc.)
 * - ISO8601 timestamps with millisecond precision
 * - Request ID correlation via AsyncLocalStorage
 *
 * Configuration:
 * - Custom serializers handle body truncation and binary detection
 * - Redaction rules protect sensitive headers
 * - Standard Pino HTTP serializers for req/res objects
 * - Response time tracking (in milliseconds)
 *
 * @example
 * ```typescript
 * import loggerMiddleware from './middlewares/loggerMiddleware';
 * app.use(loggerMiddleware);
 * ```
 */
const loggerMiddleware = pinoHttp({
  /**
   * Pino logger instance configuration.
   * Uses ISO8601 timestamp format for consistency with log aggregation tools.
   * Formats log level as string (INFO, WARN, ERROR) instead of numeric.
   */
  logger: pino({
    timestamp: pino.stdTimeFunctions.isoTime,
    formatters: {
      level: (label: string) => {
        return { level: label.toUpperCase() };
      },
    },
  }),

  /**
   * Custom serializers for request and response objects.
   *
   * These serializers extend the standard Pino HTTP serializers to:
   * - Include request/response bodies in logs
   * - Apply truncation for large payloads (>10KB)
   * - Omit binary content (images, videos, PDFs)
   * - Maintain standard req/res fields (method, url, statusCode, etc.)
   */
  serializers: {
    /**
     * Request serializer with body logging and truncation.
     *
     * Extends standard req serializer to include:
     * - req.body (truncated if >10KB, omitted if binary)
     * - req.headers (with Authorization redacted)
     * - req.method, req.url, req.id
     */
    req(req: any) {
      // Get standard serialized request
      const serialized: any = pinoHttp.stdSerializers.req(req);

      // Add body if present (from req.raw.body)
      if (req.raw?.body) {
        const contentType = req.headers['content-type'] as string | undefined;
        serialized.body = serializeBody(req.raw.body, contentType, req.headers);
      }

      return serialized;
    },

    /**
     * Response serializer with body logging and truncation.
     *
     * Extends standard res serializer to include:
     * - res.body (truncated if >10KB, omitted if binary)
     * - res.statusCode, res.headers
     */
    res(res: any) {
      // Get standard serialized response
      const serialized: any = pinoHttp.stdSerializers.res(res);

      // Explicitly include statusCode from response object
      // The standard serializer may not capture it at the right time
      if (res.statusCode !== undefined) {
        serialized.statusCode = res.statusCode;
      }

      // Add body if present (from res.raw.body)
      if (res.raw?.body) {
        const contentType = (res.headers?.['content-type'] || res.raw.headers?.['content-type']) as string | undefined;
        const headers = res.headers || res.raw.headers || {};
        serialized.body = serializeBody(res.raw.body, contentType, headers);
      }

      return serialized;
    },
  },

  /**
   * Redaction configuration for sensitive headers.
   *
   * The Authorization header is redacted to prevent credential exposure
   * (passwords, tokens, API keys) in logs. The value is replaced with '***'.
   */
  redact: {
    paths: ['req.headers.authorization', 'res.headers.authorization'],
    censor: '***',
  },

  /**
   * Custom properties injected into every log entry.
   *
   * Currently injects:
   * - requestId: Retrieved from AsyncLocalStorage (set by requestIdMiddleware)
   *
   * This function is called for every log entry, allowing dynamic context
   * injection based on the current request state.
   */
  customProps: () => {
    const requestId = getRequestId();
    return requestId ? { requestId } : {};
  },

  /**
   * Custom attribute keys for compatibility with log aggregation systems.
   *
   * Maps Pino's default attribute names to more descriptive names:
   * - responseTime → responseTime (duration in milliseconds)
   */
  customAttributeKeys: {
    responseTime: 'responseTime',
  },

  /**
   * Automatically log request completion.
   * When true, pino-http logs a message when the response is sent.
   */
  autoLogging: true,

  /**
   * Custom message for request received log.
   * This creates the first log entry when a request arrives.
   */
  customReceivedMessage: (_req: any) => {
    return 'request received';
  },

  /**
   * Custom message for successful response log.
   * This creates the second log entry when the response is sent.
   */
  customSuccessMessage: (_req: any, _res: any) => {
    return 'request completed';
  },

  /**
   * Custom message for error response log.
   * This creates the second log entry when an error occurs.
   */
  customErrorMessage: (_req: any, _res: any, err: any) => {
    return `request failed: ${err.message}`;
  },
});

export default loggerMiddleware;
