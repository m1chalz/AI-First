import { describe, it, expect } from 'vitest';
import { buildMapUrl } from '../../utils/map-url-builder';

describe('Map URL Builder', () => {
    describe('buildMapUrl', () => {
        it.each([
            { lat: 52.2297, lng: 21.0122, expected: 'https://www.google.com/maps?q=52.2297,21.0122', description: 'should build Google Maps URL for positive coordinates' },
            { lat: -33.8688, lng: 151.2093, expected: 'https://www.google.com/maps?q=-33.8688,151.2093', description: 'should build Google Maps URL for negative latitude' },
            { lat: 37.7749, lng: -122.4194, expected: 'https://www.google.com/maps?q=37.7749,-122.4194', description: 'should build Google Maps URL for negative longitude' },
            { lat: 0, lng: 0, expected: 'https://www.google.com/maps?q=0,0', description: 'should build Google Maps URL for origin coordinates' },
            { lat: 40.7128, lng: -74.006, expected: 'https://www.google.com/maps?q=40.7128,-74.006', description: 'should build Google Maps URL for New York' },
        ])('$description', ({ lat, lng, expected }) => {
            // When
            const result = buildMapUrl(lat, lng);

            // Then
            expect(result).toBe(expected);
        });
    });
});

