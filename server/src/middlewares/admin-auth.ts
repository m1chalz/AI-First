import type { Request, Response, NextFunction } from 'express';
import { UnauthenticatedError } from '../lib/errors.ts';

const ADMIN_TOKEN = 'tajnehasloadmina';

/**
 * Middleware for simple admin authentication.
 * Checks Authorization header for exact token match.
 * Used for internal admin operations (e.g., delete announcements).
 */
export default function adminAuthMiddleware(_req: Request, _res: Response, next: NextFunction): void {
  const authHeader = _req.headers.authorization;

  if (!authHeader) {
    throw new UnauthenticatedError('Missing Authorization header');
  }

  // Simply compare the entire header value with the token
  if (authHeader !== ADMIN_TOKEN) {
    throw new UnauthenticatedError('Invalid admin token');
  }

  next();
}
