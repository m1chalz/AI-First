import type { Request, Response, NextFunction } from 'express';
import { UnauthenticatedError } from '../lib/errors.ts';

interface BasicAuthCredentials {
  username: string;
  password: string;
}

export interface RequestWithBasicAuth extends Request {
  basicAuth?: BasicAuthCredentials;
}

/**
 * Extracts Basic authentication credentials from the Authorization header.
 * Parses Base64-encoded username:password format and attaches to req.basicAuth.
 * 
 * Throws UnauthenticatedError (401) if:
 * - Authorization header is missing
 * - Authorization scheme is not "Basic"
 * - Base64 encoding is invalid
 * - Credentials format is invalid (not username:password)
 * - Username or password is empty
 */
export default function basicAuthMiddleware(req: RequestWithBasicAuth, _res: Response, next: NextFunction): void {
  const authHeader = req.headers.authorization;

  if (!authHeader) {
    throw new UnauthenticatedError();
  }

  // Parse scheme and credentials (format: "Basic base64(username:password)")
  const [scheme, credentials] = authHeader.split(' ');

  if (!scheme || scheme.toLowerCase() !== 'basic' || !credentials) {
    throw new UnauthenticatedError('Invalid Authorization header format');
  }

  // Decode Base64 credentials
  let decodedCredentials: string;
  try {
    decodedCredentials = Buffer.from(credentials, 'base64').toString('utf-8');
  } catch {
    throw new UnauthenticatedError('Invalid Base64 encoding in Authorization header');
  }

  // Parse username:password format
  const colonIndex = decodedCredentials.indexOf(':');
  if (colonIndex === -1) {
    throw new UnauthenticatedError('Credentials must be in username:password format');
  }

  const username = decodedCredentials.substring(0, colonIndex);
  const password = decodedCredentials.substring(colonIndex + 1);

  // Validate that both username and password are non-empty
  if (!username || !password) {
    throw new UnauthenticatedError('Username and password cannot be empty');
  }

  // Attach parsed credentials to request object
  req.basicAuth = { username, password };

  next();
}

