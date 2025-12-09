import { describe, it, expect } from 'vitest';
import { formatCoordinates } from '../../utils/coordinate-formatter';

describe('formatCoordinates', () => {
  describe('Given valid coordinates', () => {
    it.each([
      { lat: 52.2297, lng: 21.0122, expected: '52.2297° N, 21.0122° E', description: 'should format positive coordinates (North, East)' },
      { lat: -33.8688, lng: 151.2093, expected: '33.8688° S, 151.2093° E', description: 'should format negative latitude (South, East)' },
      { lat: 37.7749, lng: -122.4194, expected: '37.7749° N, 122.4194° W', description: 'should format negative longitude (North, West)' },
      { lat: -23.5505, lng: -46.6333, expected: '23.5505° S, 46.6333° W', description: 'should format both negative (South, West)' }
    ])('$description', ({ lat, lng, expected }) => {
      // When
      const result = formatCoordinates(lat, lng);

      // Then
      expect(result).toBe(expected);
    });
  });

  describe('Given edge cases', () => {
    it.each([
      { lat: 0, lng: 0, expected: '0.0000° N, 0.0000° E', description: 'should handle coordinates at origin (0, 0)' },
      { lat: 90, lng: 180, expected: '90.0000° N, 180.0000° E', description: 'should handle maximum coordinates' },
      { lat: -90, lng: -180, expected: '90.0000° S, 180.0000° W', description: 'should handle minimum coordinates' },
      { lat: 51.5074, lng: -0.1278, expected: '51.5074° N, 0.1278° W', description: 'should handle London coordinates' }
    ])('$description', ({ lat, lng, expected }) => {
      // When
      const result = formatCoordinates(lat, lng);

      // Then
      expect(result).toBe(expected);
    });
  });

  describe('Given real-world location scenarios', () => {
    it.each([
      { lat: 48.8566, lng: 2.3522, expected: '48.8566° N, 2.3522° E', description: 'should format Paris coordinates correctly' },
      { lat: 35.6762, lng: 139.6503, expected: '35.6762° N, 139.6503° E', description: 'should format Tokyo coordinates correctly' },
      { lat: -33.9249, lng: 18.4241, expected: '33.9249° S, 18.4241° E', description: 'should format Cape Town coordinates correctly' },
      { lat: 40.7128, lng: -74.006, expected: '40.7128° N, 74.0060° W', description: 'should format New York coordinates correctly' }
    ])('$description', ({ lat, lng, expected }) => {
      // When
      const result = formatCoordinates(lat, lng);

      // Then
      expect(result).toBe(expected);
    });
  });

  describe('Given decimal precision', () => {
    it.each([
      { lat: 1.123456789, lng: 2.987654321, expected: '1.1235° N, 2.9877° E', description: 'should round to 4 decimal places' },
      { lat: 0.00001, lng: 0.00002, expected: '0.0000° N, 0.0000° E', description: 'should handle very small values' },
      { lat: 89.99994, lng: 179.99994, expected: '89.9999° N, 179.9999° E', description: 'should handle near-boundary values' }
    ])('$description', ({ lat, lng, expected }) => {
      // When
      const result = formatCoordinates(lat, lng);

      // Then
      expect(result).toBe(expected);
    });
  });
});
