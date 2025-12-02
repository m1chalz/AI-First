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
  if (error) {
    expect(error.code).toBe(expectedCode);
    if (expectedField !== undefined) {
      expect(error.field).toBe(expectedField);
    }
  }
}

const VALID_ANNOUNCEMENT_DATA = {
  species: 'DOG',
  sex: 'MALE',
  locationLatitude: 40.7128,
  locationLongitude: -74.0060,
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

    it('should accept boundary values for latitude', () => {
      // Given: Data with boundary latitude values
      const testCases = [
        { latitude: -90, description: 'minimum latitude' },
        { latitude: 90, description: 'maximum latitude' },
        { latitude: 0, description: 'equator' },
      ];

      testCases.forEach(({ latitude }) => {
        const data = {
          ...VALID_ANNOUNCEMENT_DATA,
          locationLatitude: latitude,
        };

        // When: Validation is called
        // Then: Validation passes
        expect(() => validateCreateAnnouncement(data)).not.toThrow();
      });
    });

    it('should accept boundary values for longitude', () => {
      // Given: Data with boundary longitude values
      const testCases = [
        { longitude: -180, description: 'minimum longitude' },
        { longitude: 180, description: 'maximum longitude' },
        { longitude: 0, description: 'prime meridian' },
      ];

      testCases.forEach(({ longitude }) => {
        const data = {
          ...VALID_ANNOUNCEMENT_DATA,
          locationLongitude: longitude,
        };

        // When: Validation is called
        // Then: Validation passes
        expect(() => validateCreateAnnouncement(data)).not.toThrow();
      });
    });

    it('should accept today\'s date as valid lastSeenDate', () => {
      // Given: Data with today's date
      const today = new Date();
      const todayString = today.toISOString().split('T')[0];
      
      const data = {
        ...VALID_ANNOUNCEMENT_DATA,
        lastSeenDate: todayString,
      };

      // When: Validation is called
      // Then: Validation passes
      expect(() => validateCreateAnnouncement(data)).not.toThrow();
    });

    it('should accept yesterday\'s date as valid lastSeenDate', () => {
      // Given: Data with yesterday's date
      const yesterday = new Date();
      yesterday.setDate(yesterday.getDate() - 1);
      const yesterdayString = yesterday.toISOString().split('T')[0];
      
      const data = {
        ...VALID_ANNOUNCEMENT_DATA,
        lastSeenDate: yesterdayString,
      };

      // When: Validation is called
      // Then: Validation passes
      expect(() => validateCreateAnnouncement(data)).not.toThrow();
    });

    it('should accept valid microchip number with only digits', () => {
      // Given: Data with valid microchip number
      const data = {
        ...VALID_ANNOUNCEMENT_DATA,
        microchipNumber: '123456789012345',
      };

      // When: Validation is called
      // Then: Validation passes
      expect(() => validateCreateAnnouncement(data)).not.toThrow();
    });

    it.each([
      { status: 'MISSING' as const },
      { status: 'FOUND' as const },
    ])('should accept status $description', ({ status }) => {
      // Given: Data with status
      const data = {
        ...VALID_ANNOUNCEMENT_DATA,
        status,
      };

      // When: Validation is called
      // Then: Validation passes
      expect(() => validateCreateAnnouncement(data)).not.toThrow();
    });

    it('should accept valid positive integer age', () => {
      // Given: Data with valid age
      const testCases = [1, 2, 5, 10, 20];

      testCases.forEach((age) => {
        const data = {
          ...VALID_ANNOUNCEMENT_DATA,
          age,
        };

        // When: Validation is called
        // Then: Validation passes
        expect(() => validateCreateAnnouncement(data)).not.toThrow();
      });
    });
  });

  describe('validation errors', () => {
    it.each([
      // Missing required fields
      { description: 'species is missing', fieldName: 'species', fieldValue: undefined, expectedCode: 'MISSING_VALUE' },
      { description: 'sex is missing', fieldName: 'sex', fieldValue: undefined, expectedCode: 'MISSING_VALUE' },
      { description: 'lastSeenDate is missing', fieldName: 'lastSeenDate', fieldValue: undefined, expectedCode: 'MISSING_VALUE' },
      { description: 'status is missing', fieldName: 'status', fieldValue: undefined, expectedCode: 'MISSING_VALUE' },
      { description: 'locationLatitude is missing', fieldName: 'locationLatitude', fieldValue: undefined, expectedCode: 'MISSING_VALUE' },
      { description: 'locationLongitude is missing', fieldName: 'locationLongitude', fieldValue: undefined, expectedCode: 'MISSING_VALUE' },
      // Empty string fields
      { description: 'species is empty string', fieldName: 'species', fieldValue: '', expectedCode: 'MISSING_VALUE' },
      { description: 'sex is empty string', fieldName: 'sex', fieldValue: '', expectedCode: 'MISSING_VALUE' },
      // lastSeenDate empty string fails regex first, so returns INVALID_FORMAT
      { description: 'lastSeenDate is empty string', fieldName: 'lastSeenDate', fieldValue: '', expectedCode: 'INVALID_FORMAT' },
      // Invalid formats
      { description: 'email format is invalid', fieldName: 'email', fieldValue: 'invalid-email', expectedCode: 'INVALID_FORMAT' },
      { description: 'phone format is invalid', fieldName: 'phone', fieldValue: 'no-digits', expectedCode: 'INVALID_FORMAT' },
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
      // Type mismatches
      { description: 'species is not a string', fieldName: 'species', fieldValue: 123, expectedCode: 'INVALID_FORMAT' },
      { description: 'age is not a number', fieldName: 'age', fieldValue: 'three', expectedCode: 'INVALID_FORMAT' },
    ])('should throw ValidationError with $expectedCode code when $description', ({ fieldName, fieldValue, expectedCode }) => {
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
      // Then: ValidationError is thrown with INVALID_FIELD code
      expectValidationError(data, 'INVALID_FIELD', 'unknownField');
    });

    it('should throw ValidationError with first unknown field only (fail-fast)', () => {
      // Given: Data with multiple unknown fields
      const data = {
        ...VALID_ANNOUNCEMENT_DATA,
        unknownField1: 'value1',
        unknownField2: 'value2',
      };

      // When: Validation is called
      // Then: Only first unknown field error is thrown
      expectValidationError(data, 'INVALID_FIELD', 'unknownField1');
    });

    it('should trim whitespace from string fields', () => {
      // Given: Data with whitespace-only required fields
      const dataWithWhitespace = {
        ...VALID_ANNOUNCEMENT_DATA,
        species: '   ',
      };

      // When: Validation is called
      // Then: ValidationError is thrown with MISSING_VALUE code
      expectValidationError(dataWithWhitespace, 'MISSING_VALUE', 'species');
    });
  });
});

