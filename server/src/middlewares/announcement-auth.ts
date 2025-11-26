import type { NextFunction, Response } from 'express';
import { UnauthorizedError, NotFoundError } from '../lib/errors.ts';
import { verifyPassword } from '../lib/password-management.ts';
import type { RequestWithBasicAuth } from './basic-auth.ts';
import { db } from '../database/db-utils.ts';

/**
 * Middleware for authenticating announcement management operations.
 * Requires basic-auth middleware to run first, then verifies password against announcement's hash.
 */
export default async function announcementAuthMiddleware(
  req: RequestWithBasicAuth,
  _res: Response,
  next: NextFunction
): Promise<void> {
  const announcementId = req.params.id;

  // Get password hash from database
  const row: any | undefined = await db('announcement')
    .select('management_password_hash')
    .where('id', announcementId)
    .first();

  if (!row) {
    throw new NotFoundError(`Announcement with ID ${announcementId} not found`);
  }

  // Verify password against the stored hash
  const passwordMatch = await verifyPassword(
    req.basicAuth?.password ?? '',
    row.management_password_hash
  );

  if (!passwordMatch) {
    throw new UnauthorizedError('Invalid credentials for this announcement');
  }

  next();
}

