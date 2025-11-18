import { generateRequestId } from '../lib/requestIdGenerator.ts';
import { setRequestContext } from '../lib/requestContext.ts';
import type { Request, Response, NextFunction } from 'express';

/**
 * Generates unique request ID for each request and propagates it via AsyncLocalStorage.
 * IMPORTANT: Must be registered BEFORE loggerMiddleware.
 */
function requestIdMiddleware(req: Request, res: Response, next: NextFunction): void {
  const requestId = generateRequestId();

  setRequestContext({ requestId });
  (req as Request & { id: string }).id = requestId;
  res.setHeader('request-id', requestId);

  next();
}

export default requestIdMiddleware;
