import { describe, it, expect } from 'vitest';
import { formatDateDDMMYYYY } from '../date-utils';

describe('formatDateDDMMYYYY', () => {
  it.each([
    { input: '2025-12-17', expected: '17/12/2025' },
    { input: '2025-01-05', expected: '05/01/2025' },
    { input: '2025-12-17T10:30:00Z', expected: '17/12/2025' },
    { input: '1999-06-15', expected: '15/06/1999' },
    { input: null, expected: '' },
    { input: '', expected: '' },
    { input: 'invalid-date', expected: 'invalid-date' }
  ])('formatDateDDMMYYYY($input) should return "$expected"', ({ input, expected }) => {
    // when
    const result = formatDateDDMMYYYY(input);

    // then
    expect(result).toBe(expected);
  });
});
