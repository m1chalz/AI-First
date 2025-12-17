import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { formatRelativeDate } from '../date-utils';

describe('formatRelativeDate', () => {
  beforeEach(() => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date('2025-12-17T12:00:00Z'));
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it.each([
    // today
    { input: '2025-12-17', expected: 'today' },
    { input: '2025-12-17T00:00:00Z', expected: 'today' },
    // yesterday
    { input: '2025-12-16', expected: 'yesterday' },
    { input: '2025-12-16T00:00:00Z', expected: 'yesterday' },
    // days ago
    { input: '2025-12-15', expected: '2 days ago' },
    { input: '2025-12-14', expected: '3 days ago' },
    { input: '2025-12-12', expected: '5 days ago' },
    { input: '2025-12-11', expected: '6 days ago' },
    // weeks ago
    { input: '2025-12-10', expected: '1 week ago' },
    { input: '2025-12-03', expected: '2 weeks ago' },
    { input: '2025-11-26', expected: '3 weeks ago' },
    // months ago
    { input: '2025-11-17', expected: '1 month ago' },
    { input: '2025-10-17', expected: '2 months ago' },
    { input: '2025-06-17', expected: '6 months ago' },
    // edge cases
    { input: '', expected: '' },
    { input: 'invalid-date', expected: 'invalid-date' }
  ])('formatRelativeDate("$input") should return "$expected"', ({ input, expected }) => {
    // when
    const result = formatRelativeDate(input);

    // then
    expect(result).toBe(expected);
  });
});
