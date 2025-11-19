import { describe, it, expect } from 'vitest';
import { isValidEmail, isValidPhone } from '../validators.ts';

const VALID_EMAILS = [
  'user@example.com',
  'john.doe@example.com',
  'test+tag@domain.co.uk',
  'admin@subdomain.example.com',
];

const INVALID_EMAILS = [
  'notanemail',
  '@example.com',
  'user@',
  'user @example.com',
  'user@.com',
  '',
  'userexample.com',
];

const VALID_PHONES = [
  '+1-555-1234',
  '(555) 123-4567',
  '5551234567',
  '555-0101',
  '+1 (312) 555-0142',
];

const INVALID_PHONES = [
  'not-a-phone',
  '',
  '---',
  '()',
];

describe('validators', () => {
  describe('isValidEmail', () => {
    it.each(VALID_EMAILS)('should return true for valid email: %s', (email) => {
      expect(isValidEmail(email)).toBe(true);
    });

    it.each(INVALID_EMAILS)('should return false for invalid email: %s', (email) => {
      expect(isValidEmail(email)).toBe(false);
    });
  });

  describe('isValidPhone', () => {
    it.each(VALID_PHONES)('should return true for phone with digits: %s', (phone) => {
      expect(isValidPhone(phone)).toBe(true);
    });

    it.each(INVALID_PHONES)('should return false for phone without digits: %s', (phone) => {
      expect(isValidPhone(phone)).toBe(false);
    });
  });
});
