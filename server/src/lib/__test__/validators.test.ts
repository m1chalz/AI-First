import { describe, it, expect } from 'vitest';
import { isValidPassword, isValidEmail, isValidPhone } from '../validators';

describe('Email Validation', () => {
  describe('isValidEmail', () => {
    it.each([
      ['user@example.com', true],
      ['test.email+tag@domain.co.uk', true],
      ['simple@example.org', true],
      ['invalid.email', false],
      ['user@', false],
      ['@example.com', false],
      ['user @example.com', false],
      ['', false],
      ['   ', false],
      ['a'.repeat(242) + '@example.com', true],
      ['a'.repeat(243) + '@example.com', false]
    ])('should validate %s as %s', (email, expected) => {
      expect(isValidEmail(email)).toBe(expected);
    });
  });
});

describe('Password Validation', () => {
  describe('isValidPassword', () => {
    it.each([
      ['password123', true],
      ['MyP@ss123', true],
      ['a'.repeat(128), true],
      ['12345678', true],
      ['pass', false],
      ['1234567', false],
      ['', false],
      ['a'.repeat(129), false],
      ['a'.repeat(200), false],
      ['P@ssw0rd!', true],
      ['MyP@$$#%^&*()', true],
      ['my pass word 123', true]
    ])('should validate %s as %s', (password, expected) => {
      expect(isValidPassword(password)).toBe(expected);
    });
  });
});

describe('Phone Validation', () => {
  describe('isValidPhone', () => {
    it.each([
      ['123-456-7890', true],
      ['555.1234', true],
      ['no digits here', false]
    ])('should validate %s as %s', (phone, expected) => {
      expect(isValidPhone(phone)).toBe(expected);
    });
  });
});
