import { generateRequestId } from '../lib/requestIdGenerator.ts';
import { setRequestContext } from '../lib/requestContext.ts';
import type { Request, Response, NextFunction } from 'express';

/**
 * Request ID middleware for Express.js.
 *
 * Generates a unique 10-character alphanumeric request ID for each incoming
 * HTTP request and propagates it throughout the request lifecycle via
 * AsyncLocalStorage. The request ID enables correlation of all logs
 * generated during request processing.
 *
 * Responsibilities:
 * 1. Generate unique request ID (10 alphanumeric characters)
 * 2. Store ID in AsyncLocalStorage context for propagation
 * 3. Attach ID to req.id for middleware access
 * 4. Inject ID into response header (request-id)
 *
 * The request ID is available throughout the async call chain via
 * getRequestId() without explicit parameter passing, enabling automatic
 * correlation in application logs.
 *
 * @example
 * ```typescript
 * import requestIdMiddleware from './middlewares/requestIdMiddleware';
 * import loggerMiddleware from './middlewares/loggerMiddleware';
 *
 * // IMPORTANT: Register request ID middleware BEFORE logger middleware
 * app.use(requestIdMiddleware);
 * app.use(loggerMiddleware);
 * ```
 *
 * @param req - Express request object
 * @param res - Express response object
 * @param next - Express next function
 */
function requestIdMiddleware(req: Request, res: Response, next: NextFunction): void {
  // Generate unique 10-character alphanumeric request ID
  const requestId = generateRequestId();

  // Store request ID in AsyncLocalStorage for automatic propagation
  // This makes the ID available via getRequestId() throughout the request lifecycle
  setRequestContext({ requestId });

  // Attach request ID to req object for middleware access
  // This allows pino-http and other middleware to access the ID
  (req as any).id = requestId;

  // Inject request-id header into response
  // Clients can use this ID to search logs for debugging
  res.setHeader('request-id', requestId);

  next();
}

export default requestIdMiddleware;

