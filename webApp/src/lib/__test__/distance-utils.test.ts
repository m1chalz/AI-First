import { describe, it, expect } from 'vitest';
import { formatDistance } from '../distance-utils';

describe('formatDistance', () => {
  it.each([
    // kilometers (>= 1 km)
    { input: 1.0, expected: '1.0 km away' },
    { input: 1.5, expected: '1.5 km away' },
    { input: 2.5, expected: '2.5 km away' },
    { input: 10.0, expected: '10.0 km away' },
    { input: 100.5, expected: '100.5 km away' },
    // meters (< 1 km)
    { input: 0.5, expected: '500 m away' },
    { input: 0.1, expected: '100 m away' },
    { input: 0.05, expected: '50 m away' },
    { input: 0.999, expected: '999 m away' },
    // edge cases
    { input: 0, expected: '0 m away' },
    { input: 0.001, expected: '1 m away' },
    { input: undefined, expected: 'Location unknown' }
  ])('formatDistance($input) should return "$expected"', ({ input, expected }) => {
    // when
    const result = formatDistance(input);

    // then
    expect(result).toBe(expected);
  });
});
