import { randomInt } from 'crypto';

/**
 * Generates a unique 10-character alphanumeric request ID.
 *
 * The ID consists of characters from A-Z, a-z, and 0-9 (62 possible characters),
 * providing 62^10 (≈839 quadrillion) unique combinations. Collision probability
 * is negligible for typical workloads.
 *
 * @returns A 10-character alphanumeric string (e.g., "aBc123XyZ9")
 * @example
 * const requestId = generateRequestId();
 * // Returns: "aBc123XyZ9"
 */
export function generateRequestId(): string {
  const CHARSET = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  const ID_LENGTH = 10;

  let id = '';
  for (let i = 0; i < ID_LENGTH; i++) {
    id += CHARSET[randomInt(CHARSET.length)];
  }
  return id;
}

