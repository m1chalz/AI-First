import { describe, it, expect } from 'vitest';
import { validateLocation } from '../location-validation.ts';
import { ValidationError } from '../errors.ts';

describe('validateLocation', () => {
  it.each([
    { lat: 50.0614, lng: 19.9383, description: 'valid coordinates' },
    { lat: -90, lng: 0, description: 'minimum valid latitude' },
    { lat: 90, lng: 0, description: 'maximum valid latitude' },
    { lat: 0, lng: -180, description: 'minimum valid longitude' },
    { lat: 0, lng: 180, description: 'maximum valid longitude' },
    { lat: 50.123456, lng: 19.987654, description: 'high precision coordinates' },
  ])('should validate $description', ({ lat, lng }) => {
    // when/then
    expect(() => validateLocation(lat, lng)).not.toThrow();
  });

  it.each([
    { lat: -90.001, lng: 0, expectedError: 'Parameter \'lat\' must be between -90 and 90', expectedField: 'lat' },
    { lat: 90.001, lng: 0, expectedError: 'Parameter \'lat\' must be between -90 and 90', expectedField: 'lat' },
    { lat: -91, lng: 0, expectedError: 'Parameter \'lat\' must be between -90 and 90', expectedField: 'lat' },
    { lat: 95, lng: 0, expectedError: 'Parameter \'lat\' must be between -90 and 90', expectedField: 'lat' },
    { lat: 0, lng: -180.001, expectedError: 'Parameter \'lng\' must be between -180 and 180', expectedField: 'lng' },
    { lat: 0, lng: 180.001, expectedError: 'Parameter \'lng\' must be between -180 and 180', expectedField: 'lng' },
    { lat: 0, lng: -181, expectedError: 'Parameter \'lng\' must be between -180 and 180', expectedField: 'lng' },
    { lat: 0, lng: 200, expectedError: 'Parameter \'lng\' must be between -180 and 180', expectedField: 'lng' },
    { lat: NaN, lng: 0, expectedError: 'Parameter \'lat\' must be a valid number', expectedField: 'lat' },
    { lat: 0, lng: NaN, expectedError: 'Parameter \'lng\' must be a valid number', expectedField: 'lng' },
    { lat: '50' as unknown as number, lng: 0, expectedError: 'Parameter \'lat\' must be a valid number', expectedField: 'lat' },
    { lat: 0, lng: '19' as unknown as number, expectedError: 'Parameter \'lng\' must be a valid number', expectedField: 'lng' },
    { lat: 0, lng: undefined, expectedError: 'Parameter \'lng\' is required when \'lat\' is provided', expectedField: 'lng' },
    { lat: undefined, lng: 0, expectedError: 'Parameter \'lat\' is required when \'lng\' is provided', expectedField: 'lat' },
  ])('should throw ValidationError: $expectedError', ({ lat, lng, expectedError, expectedField }) => {
    // when/then
    expect(() => validateLocation(lat, lng)).toThrow(ValidationError);
    
    try {
      validateLocation(lat, lng);
    } catch (error) {
      expect(error).toBeInstanceOf(ValidationError);
      expect((error as ValidationError).message).toBe(expectedError);
      expect((error as ValidationError).code).toBe('INVALID_PARAMETER');
      expect((error as ValidationError).field).toBe(expectedField);
    }
  });

  it('should not throw when both lat and lng are absent', () => {
    // when/then
    expect(() => validateLocation(undefined, undefined)).not.toThrow();
  });

  it.each([
    { lat: 50.0614, lng: 19.9383, range: 1, description: 'range = 1' },
    { lat: 50.0614, lng: 19.9383, range: 100, description: 'range = 100' },
  ])('should accept valid positive range: $description', ({ lat, lng, range }) => {
    // when/then
    expect(() => validateLocation(lat, lng, range)).not.toThrow();
  });

  it.each([
    { lat: 50.0614, lng: 19.9383, range: 0, expectedError: 'Parameter \'range\' must be greater than zero' },
    { lat: 50.0614, lng: 19.9383, range: -1, expectedError: 'Parameter \'range\' must be a positive number' },
    { lat: 50.0614, lng: 19.9383, range: 0.5, expectedError: 'Parameter \'range\' must be an integer' },
    { lat: 50.0614, lng: 19.9383, range: NaN, expectedError: 'Parameter \'range\' must be a valid number' },
  ])('should throw ValidationError for invalid range: $range', ({ lat, lng, range, expectedError }) => {
    // when/then
    expect(() => validateLocation(lat, lng, range)).toThrow(ValidationError);
    
    try {
      validateLocation(lat, lng, range);
    } catch (error) {
      expect(error).toBeInstanceOf(ValidationError);
      expect((error as ValidationError).message).toBe(expectedError);
      expect((error as ValidationError).code).toBe('INVALID_PARAMETER');
      expect((error as ValidationError).field).toBe('range');
    }
  });

  it('should not throw when range is not provided', () => {
    // when/then
    expect(() => validateLocation(50.0614, 19.9383, undefined)).not.toThrow();
  });
});

