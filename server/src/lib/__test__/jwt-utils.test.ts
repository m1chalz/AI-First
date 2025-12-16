import { describe, it, expect, vi } from 'vitest';
import { generateToken, verifyToken } from '../jwt-utils.ts';

describe('generateToken', () => {
  it('should generate a valid JWT token with userId, iat, and exp claims in payload', () => {
    // given
    const userId = 'user-123';

    // when
    const token = generateToken(userId);

    // then
    expect(token).toBeDefined();
    expect(typeof token).toBe('string');
    expect(token.split('.')).toHaveLength(3);

    const payload = verifyToken(token);
    expect(payload.userId).toBe(userId);
    expect(payload.iat).toBeDefined();
    expect(payload.exp).toBeDefined();
    expect(payload.exp - payload.iat).toBe(3600); // one hour in seconds
  });
});

describe('verifyToken', () => {
  it('should verify and decode a valid token', () => {
    // given
    const userId = 'verify-user-123';
    const token = generateToken(userId);

    // when
    const payload = verifyToken(token);

    // then
    expect(payload.userId).toBe(userId);
    expect(payload.iat).toBeDefined();
    expect(payload.exp).toBeDefined();
  });

  it('should throw error for expired token', async () => {
    // given
    vi.useFakeTimers();
    const userId = 'expired-user';
    const token = generateToken(userId);

    // when
    vi.advanceTimersByTime(3601 * 1000);

    // then
    expect(() => verifyToken(token)).toThrow();
    vi.useRealTimers();
  });

  it('should throw error for token with invalid signature', () => {
    // given
    const userId = 'tampered-user';
    const token = generateToken(userId);
    const tamperedToken = token.slice(0, -5) + 'xxxxx';

    // when & then
    expect(() => verifyToken(tamperedToken)).toThrow();
  });

  it('should throw error for malformed token', () => {
    // given
    const malformedTokens = ['not-a-jwt', 'only.two.parts.here.extra', '', 'abc.def', '...'];

    // when & then
    malformedTokens.forEach((token) => {
      expect(() => verifyToken(token)).toThrow();
    });
  });
});
