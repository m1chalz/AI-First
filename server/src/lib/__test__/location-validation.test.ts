import { describe, it, expect } from 'vitest';
import { validateLocation } from '../location-validation.ts';

describe('validateLocation', () => {
  it.each([
    { lat: 50.0614, lng: 19.9383, description: 'valid coordinates' },
    { lat: -90, lng: 0, description: 'minimum valid latitude' },
    { lat: 90, lng: 0, description: 'maximum valid latitude' },
    { lat: 0, lng: -180, description: 'minimum valid longitude' },
    { lat: 0, lng: 180, description: 'maximum valid longitude' },
    { lat: 50.123456, lng: 19.987654, description: 'high precision coordinates' },
  ])('should validate $description', ({ lat, lng }) => {
    // when
    const result = validateLocation(lat, lng);

    // then
    expect(result.valid).toBe(true);
    expect(result.lat).toBe(lat);
    expect(result.lng).toBe(lng);
    expect(result.range).toBe(5);
  });

  it.each([
    { lat: -90.001, lng: 0, expectedError: 'Parameter \'lat\' must be between -90 and 90' },
    { lat: 90.001, lng: 0, expectedError: 'Parameter \'lat\' must be between -90 and 90' },
    { lat: -91, lng: 0, expectedError: 'Parameter \'lat\' must be between -90 and 90' },
    { lat: 95, lng: 0, expectedError: 'Parameter \'lat\' must be between -90 and 90' },
    { lat: 0, lng: -180.001, expectedError: 'Parameter \'lng\' must be between -180 and 180' },
    { lat: 0, lng: 180.001, expectedError: 'Parameter \'lng\' must be between -180 and 180' },
    { lat: 0, lng: -181, expectedError: 'Parameter \'lng\' must be between -180 and 180' },
    { lat: 0, lng: 200, expectedError: 'Parameter \'lng\' must be between -180 and 180' },
    { lat: NaN, lng: 0, expectedError: 'Parameter \'lat\' must be a valid number' },
    { lat: 0, lng: NaN, expectedError: 'Parameter \'lng\' must be a valid number' },
    { lat: '50' as unknown as number, lng: 0, expectedError: 'Parameter \'lat\' must be a valid number' },
    { lat: 0, lng: '19' as unknown as number, expectedError: 'Parameter \'lng\' must be a valid number' },
    { lat: 0, lng: undefined, expectedError: 'Parameter \'lng\' is required when \'lat\' is provided' },
    { lat: undefined, lng: 0, expectedError: 'Parameter \'lat\' is required when \'lng\' is provided' },
  ])('should reject with error', ({ lat, lng, expectedError }) => {
    // when
    const result = validateLocation(lat, lng);

    // then
    expect(result.valid).toBe(false);
    expect(result.error).toBe(expectedError);
  });

  it('should allow both lat and lng to be absent', () => {
    // given
    const lat = undefined;
    const lng = undefined;

    // when
    const result = validateLocation(lat, lng);

    // then
    expect(result.valid).toBe(true);
    expect(result.lat).toBeUndefined();
    expect(result.lng).toBeUndefined();
    expect(result.range).toBe(5);
  });

  it.each([
    { lat: 50.0614, lng: 19.9383, range: 1, description: 'range = 1' },
    { lat: 50.0614, lng: 19.9383, range: 100, description: 'range = 100' },
  ])('should accept valid positive range: $description', ({ lat, lng, range }) => {
    // when
    const result = validateLocation(lat, lng, range);

    // then
    expect(result.valid).toBe(true);
    expect(result.range).toBe(range);
  });

  it.each([
    { lat: 50.0614, lng: 19.9383, range: 0, expectedError: 'Parameter \'range\' must be greater than zero' },
    { lat: 50.0614, lng: 19.9383, range: -1, expectedError: 'Parameter \'range\' must be a positive number' },
    { lat: 50.0614, lng: 19.9383, range: 0.5, expectedError: 'Parameter \'range\' must be an integer' },
    { lat: 50.0614, lng: 19.9383, range: NaN, expectedError: 'Parameter \'range\' must be a valid number' },
  ])('should reject invalid range: $range', ({ lat, lng, range, expectedError }) => {
    // when
    const result = validateLocation(lat, lng, range);

    // then
    expect(result.valid).toBe(false);
    expect(result.error).toBe(expectedError);
  });

  it('should return default range of 5 when not provided', () => {
    // when
    const result = validateLocation(50.0614, 19.9383, undefined);

    // then
    expect(result.valid).toBe(true);
    expect(result.range).toBe(5);
  });
});

