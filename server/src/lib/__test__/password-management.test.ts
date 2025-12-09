import { describe, it, expect } from 'vitest';
import { generateManagementPassword, hashPassword, verifyPassword } from '../password-management';

describe('generateManagementPassword', () => {
  it('should generate a 6-digit numeric password', () => {
    // when
    const password = generateManagementPassword();

    // then
    expect(password).toMatch(/^\d{6}$/);
    expect(parseInt(password, 10)).toBeGreaterThanOrEqual(100000);
    expect(parseInt(password, 10)).toBeLessThan(1000000);
  });

  it('should generate unique passwords on multiple calls', () => {
    // when
    const password1 = generateManagementPassword();
    const password2 = generateManagementPassword();
    const password3 = generateManagementPassword();

    // then
    const allSame = password1 === password2 && password2 === password3;
    expect(allSame).toBe(false);
  });
});

describe('hashPassword', () => {
  it('should hash password using scrypt', async () => {
    // given
    const plainPassword = '123456';

    // when
    const hash = await hashPassword(plainPassword);

    // then
    expect(hash).toBeDefined();
    expect(hash).not.toBe(plainPassword);
    expect(hash).toContain(':');
    const parts = hash.split(':');
    expect(parts).toHaveLength(2);
    expect(parts[0]).toMatch(/^[0-9a-f]+$/);
    expect(parts[1]).toMatch(/^[0-9a-f]+$/);
  });

  it('should generate different hashes for same password', async () => {
    // given
    const plainPassword = '123456';

    // when
    const hash1 = await hashPassword(plainPassword);
    const hash2 = await hashPassword(plainPassword);

    // then
    expect(hash1).not.toBe(hash2);
  });
});

describe('verifyPassword', () => {
  it.each([
    ['123456', '123456', true],
    ['123456', '654321', false],
    ['', '', true],
    ['password123', 'password123', true],
    ['secret', 'notasecret', false]
  ])('should return %s when comparing %s to %s', async (plainPassword, testPassword, expected) => {
    // given
    const hash = await hashPassword(plainPassword);

    // when
    const isValid = await verifyPassword(testPassword, hash);

    // then
    expect(isValid).toBe(expected);
  });
});
