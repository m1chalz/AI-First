import { describe, it, expect } from 'vitest';
import { formatMicrochip, stripNonDigits } from '../microchip-formatter';

describe('formatMicrochipNumber', () => {
  it.each([
    // given: input digits, when: formatted, then: expected output
    ['', ''],
    ['1', '1'],
    ['12345', '12345'],
    ['123456', '12345-6'],
    ['1234567890', '12345-67890'],
    ['123456789012345', '12345-67890-12345'],
    ['12345678901234567890', '12345-67890-12345'] // truncates to 15
  ])('formats "%s" as "%s"', (input, expected) => {
    expect(formatMicrochip(input)).toBe(expected);
  });
});

describe('stripNonDigits', () => {
  it.each([
    // given: input string, when: stripped, then: digits only
    ['123456789012345', '123456789012345'],
    ['ABC123XYZ456', '123456'],
    ['12-34-56', '123456'],
    ['', ''],
    ['ABCXYZ', ''],
    ['123.456.789', '123456789'],
    ['(123) 456-7890', '1234567890']
  ])('strips non-digits from "%s" to "%s"', (input, expected) => {
    expect(stripNonDigits(input)).toBe(expected);
  });
});
