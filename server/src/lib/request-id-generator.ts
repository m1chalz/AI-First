import { randomInt } from 'crypto';

const CHARSET = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
const ID_LENGTH = 10;

/**
 * Generates unique 10-character alphanumeric request ID.
 * 62^10 (≈839 quadrillion) combinations - negligible collision probability.
 */
export function generateRequestId(): string {
  let id = '';
  for (let i = 0; i < ID_LENGTH; i++) {
    id += CHARSET[randomInt(CHARSET.length)];
  }
  return id;
}

