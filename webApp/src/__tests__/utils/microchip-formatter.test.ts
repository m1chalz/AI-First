import { describe, it, expect } from 'vitest';
import { formatMicrochip } from '../../utils/microchip-formatter';

describe('formatMicrochip', () => {
    describe('Given edge cases', () => {
        it.each([
            { input: '', expected: '', description: 'should return empty string for empty input' },
            { input: '12345', expected: '12345', description: 'should return original input for numbers shorter than 15 digits' },
            { input: '1234567890123451111', expected: '12345-67890-12345', description: 'should handle numbers longer than 15 digits (take first 15)' },
        ])('$description', ({ input, expected }) => {
            // When
            const result = formatMicrochip(input);

            // Then
            expect(result).toBe(expected);
        });
    });

    describe('Given real-world microchip scenarios', () => {
        it.each([
            { input: '882097601234567', expected: '88209-76012-34567', description: 'should format European microchip format correctly' },
            { input: '123004560078901', expected: '12300-45600-78901', description: 'should handle microchip with mixed digit patterns' },
        ])('$description', ({ input, expected }) => {
            // When
            const result = formatMicrochip(input);

            // Then
            expect(result).toBe(expected);
        });
    });
});

