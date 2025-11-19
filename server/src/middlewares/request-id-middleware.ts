import { generateRequestId } from '../lib/request-id-generator.ts';
import { setRequestContext } from '../lib/request-context.ts';
import type { Request, Response, NextFunction } from 'express';

/**
 * Generates unique request ID for each request and propagates it via AsyncLocalStorage.
 * IMPORTANT: Must be registered BEFORE loggerMiddleware.
 */
export default function requestIdMiddleware(req: Request, res: Response, next: NextFunction): void {
  const requestId = generateRequestId();

  setRequestContext({ requestId });
  (req as Request & { id: string }).id = requestId;
  res.setHeader('request-id', requestId);

  next();
}

