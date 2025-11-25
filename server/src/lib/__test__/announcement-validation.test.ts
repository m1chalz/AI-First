import { describe, it, expect } from 'vitest';
import validateCreateAnnouncement from '../announcement-validation.ts';
import { ValidationError } from '../errors.ts';

function expectValidationError(data: unknown, expectedCode: string, expectedField?: string): void {
  let error: ValidationError | undefined;
  try {
    validateCreateAnnouncement(data as Parameters<typeof validateCreateAnnouncement>[0]);
  } catch (e) {
    error = e as ValidationError;
  }
  expect(error).toBeDefined();
  expect(error).toBeInstanceOf(ValidationError);
  expect(error!.code).toBe(expectedCode);
  if (expectedField !== undefined) {
    expect(error!.field).toBe(expectedField);
  }
}

const VALID_ANNOUNCEMENT_DATA = {
  species: 'DOG',
  sex: 'MALE',
  locationLatitude: 40.7128,
  locationLongitude: -74.0060,
  photoUrl: 'https://example.com/photo.jpg',
  lastSeenDate: '2025-11-19',
  status: 'MISSING' as const,
  email: 'test@example.com',
};

describe('validateCreateAnnouncement', () => {
  describe('valid data', () => {
    it.each([
      {
        description: 'all required fields are valid',
        data: VALID_ANNOUNCEMENT_DATA,
      },
      {
        description: 'optional fields are provided',
        data: {
          ...VALID_ANNOUNCEMENT_DATA,
          petName: 'Max',
          breed: 'Golden Retriever',
          age: 3,
          description: 'Friendly dog',
          microchipNumber: '123456789',
          locationCity: 'New York',
          locationRadius: 5,
          phone: '+1-555-0101',
          reward: '$100',
        },
      },
      {
        description: 'only email is provided (no phone)',
        data: {
          ...VALID_ANNOUNCEMENT_DATA,
          email: 'test@example.com',
        },
      },
      {
        description: 'only phone is provided (no email)',
        data: {
          ...VALID_ANNOUNCEMENT_DATA,
          email: undefined,
          phone: '+1-555-0101',
        },
      },
    ])('should not throw when $description', ({ data }) => {
      expect(() => validateCreateAnnouncement(data)).not.toThrow();
    });
  });

  describe('validation errors', () => {
    it.each([
      // Missing required fields
      { description: 'species is missing', fieldName: 'species', fieldValue: undefined, expectedCode: 'MISSING_VALUE' },
      { description: 'sex is missing', fieldName: 'sex', fieldValue: undefined, expectedCode: 'MISSING_VALUE' },
      { description: 'photoUrl is missing', fieldName: 'photoUrl', fieldValue: undefined, expectedCode: 'MISSING_VALUE' },
      { description: 'lastSeenDate is missing', fieldName: 'lastSeenDate', fieldValue: undefined, expectedCode: 'MISSING_VALUE' },
      { description: 'status is missing', fieldName: 'status', fieldValue: undefined, expectedCode: 'MISSING_VALUE' },
      { description: 'locationLatitude is missing', fieldName: 'locationLatitude', fieldValue: undefined, expectedCode: 'MISSING_VALUE' },
      { description: 'locationLongitude is missing', fieldName: 'locationLongitude', fieldValue: undefined, expectedCode: 'MISSING_VALUE' },
      // Empty string fields
      { description: 'species is empty string', fieldName: 'species', fieldValue: '', expectedCode: 'MISSING_VALUE' },
      { description: 'sex is empty string', fieldName: 'sex', fieldValue: '', expectedCode: 'MISSING_VALUE' },
      // Invalid formats
      { description: 'email format is invalid', fieldName: 'email', fieldValue: 'invalid-email', expectedCode: 'INVALID_FORMAT' },
      { description: 'phone format is invalid', fieldName: 'phone', fieldValue: 'no-digits', expectedCode: 'INVALID_FORMAT' },
      { description: 'photoUrl is not a valid URL', fieldName: 'photoUrl', fieldValue: 'not-a-url', expectedCode: 'INVALID_FORMAT' },
      { description: 'photoUrl uses non-HTTP protocol', fieldName: 'photoUrl', fieldValue: 'ftp://example.com/photo.jpg', expectedCode: 'INVALID_FORMAT' },
      { description: 'lastSeenDate format is invalid', fieldName: 'lastSeenDate', fieldValue: '2025/11/19', expectedCode: 'INVALID_FORMAT' },
      { description: 'lastSeenDate is in the future', fieldName: 'lastSeenDate', fieldValue: (() => { const tomorrow = new Date(); tomorrow.setDate(tomorrow.getDate() + 1); return tomorrow.toISOString().split('T')[0]; })(), expectedCode: 'INVALID_FORMAT' },
      { description: 'status is not MISSING or FOUND', fieldName: 'status', fieldValue: 'INVALID_STATUS', expectedCode: 'INVALID_FORMAT' },
      { description: 'microchipNumber contains non-digits', fieldName: 'microchipNumber', fieldValue: '123abc', expectedCode: 'INVALID_FORMAT' },
      { description: 'age is negative', fieldName: 'age', fieldValue: -1, expectedCode: 'INVALID_FORMAT' },
      { description: 'age is zero', fieldName: 'age', fieldValue: 0, expectedCode: 'INVALID_FORMAT' },
      { description: 'age is non-integer', fieldName: 'age', fieldValue: 3.5, expectedCode: 'INVALID_FORMAT' },
      { description: 'locationLatitude is greater than 90', fieldName: 'locationLatitude', fieldValue: 91, expectedCode: 'INVALID_FORMAT' },
      { description: 'locationLatitude is less than -90', fieldName: 'locationLatitude', fieldValue: -91, expectedCode: 'INVALID_FORMAT' },
      { description: 'locationLongitude is greater than 180', fieldName: 'locationLongitude', fieldValue: 181, expectedCode: 'INVALID_FORMAT' },
      { description: 'locationLongitude is less than -180', fieldName: 'locationLongitude', fieldValue: -181, expectedCode: 'INVALID_FORMAT' },
      { description: 'locationRadius is not a positive integer', fieldName: 'locationRadius', fieldValue: -1, expectedCode: 'INVALID_FORMAT' },
      // Type mismatches
      { description: 'species is not a string', fieldName: 'species', fieldValue: 123, expectedCode: 'INVALID_FORMAT' },
      { description: 'age is not a number', fieldName: 'age', fieldValue: 'three', expectedCode: 'INVALID_FORMAT' },
    ])('should throw ValidationError with $expectedCode code when $description', ({ description, fieldName, fieldValue, expectedCode }) => {
      // Given: Data with invalid field
      const data = {
        ...VALID_ANNOUNCEMENT_DATA,
        [fieldName]: fieldValue,
      };

      // When: Validation is called
      // Then: ValidationError is thrown with expected code
      expectValidationError(data, expectedCode, fieldName);
    });

    it('should throw ValidationError when both email and phone are missing', () => {
      // Given: Data without email or phone
      const data = {
        ...VALID_ANNOUNCEMENT_DATA,
        email: undefined,
        phone: undefined,
      };

      // When: Validation is called
      // Then: ValidationError is thrown with MISSING_CONTACT code
      expectValidationError(data, 'MISSING_CONTACT', 'contact');
    });

    it('should throw ValidationError when unknown fields are present', () => {
      // Given: Data with unknown field
      const data = {
        ...VALID_ANNOUNCEMENT_DATA,
        unknownField: 'value',
      };

      // When: Validation is called
      // Then: ValidationError is thrown
      expect(() => validateCreateAnnouncement(data)).toThrow(ValidationError);
    });
  });
});

