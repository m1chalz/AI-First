import jwt from 'jsonwebtoken';
import type { JwtPayload } from '../types/auth.js';
import config from '../conf/config.ts';

const TOKEN_EXPIRATION = '1h';

export function generateToken(userId: string): string {
  return jwt.sign({ userId }, config.jwtSecret, { expiresIn: TOKEN_EXPIRATION });
}

export function verifyToken(token: string): JwtPayload {
  return jwt.verify(token, config.jwtSecret) as JwtPayload;
}
