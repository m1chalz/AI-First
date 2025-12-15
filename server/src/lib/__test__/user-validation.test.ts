import { describe, it, expect } from 'vitest';
import validateCreateUser, { type CreateUserRequest } from '../user-validation.ts';
import { ValidationError } from '../errors.ts';

function expectValidationError(data: unknown, expectedCode: string, expectedField?: string): void {
  let error: ValidationError | undefined;
  try {
    validateCreateUser(data);
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

const VALID_USER_DATA: CreateUserRequest = {
  email: 'user@example.com',
  password: 'password123'
};

describe('validateCreateUser', () => {
  describe('valid data', () => {
    it.each([
      {
        description: 'all required fields are valid',
        data: VALID_USER_DATA
      },
      {
        description: 'email with plus sign',
        data: {
          email: 'user+tag@example.com',
          password: 'password123'
        }
      },
      {
        description: 'email with dots',
        data: {
          email: 'user.name@example.co.uk',
          password: 'password123'
        }
      },
      {
        description: 'password with special characters',
        data: {
          email: 'user@example.com',
          password: 'P@ssw0rd!#$%^&*()'
        }
      },
      {
        description: 'password at minimum length (8 chars)',
        data: {
          email: 'user@example.com',
          password: '12345678'
        }
      },
      {
        description: 'password at maximum length (128 chars)',
        data: {
          email: 'user@example.com',
          password: 'a'.repeat(128)
        }
      },
      {
        description: 'email at maximum length (254 chars)',
        data: {
          email: 'a'.repeat(242) + '@example.com',
          password: 'password123'
        }
      },
      {
        description: 'uppercase email (will be normalized)',
        data: {
          email: 'USER@EXAMPLE.COM',
          password: 'password123'
        }
      }
    ])('should not throw when $description', ({ data }) => {
      expect(() => validateCreateUser(data)).not.toThrow();
    });
  });

  describe('validation errors', () => {
    it.each([
      // Missing required fields
      { description: 'email is missing', fieldName: 'email', fieldValue: undefined, expectedCode: 'MISSING_VALUE' },
      { description: 'password is missing', fieldName: 'password', fieldValue: undefined, expectedCode: 'MISSING_VALUE' },
      // Empty string fields
      { description: 'email is empty string', fieldName: 'email', fieldValue: '', expectedCode: 'MISSING_VALUE' },
      { description: 'password is empty string', fieldName: 'password', fieldValue: '', expectedCode: 'MISSING_VALUE' },
      // Invalid email formats
      { description: 'email without @', fieldName: 'email', fieldValue: 'invalid-email', expectedCode: 'INVALID_FORMAT' },
      { description: 'email without domain', fieldName: 'email', fieldValue: 'user@', expectedCode: 'INVALID_FORMAT' },
      { description: 'email without local part', fieldName: 'email', fieldValue: '@example.com', expectedCode: 'INVALID_FORMAT' },
      { description: 'email with space', fieldName: 'email', fieldValue: 'user @example.com', expectedCode: 'INVALID_FORMAT' },
      // Email length validation
      { description: 'email exceeding 254 characters', fieldName: 'email', fieldValue: 'a'.repeat(243) + '@example.com', expectedCode: 'INVALID_FORMAT' },
      // Password length validation
      { description: 'password shorter than 8 characters', fieldName: 'password', fieldValue: 'short', expectedCode: 'INVALID_FORMAT' },
      { description: 'password with 7 characters', fieldName: 'password', fieldValue: '1234567', expectedCode: 'INVALID_FORMAT' },
      { description: 'password exceeding 128 characters', fieldName: 'password', fieldValue: 'a'.repeat(129), expectedCode: 'INVALID_FORMAT' },
      // Type mismatches
      { description: 'email is not a string', fieldName: 'email', fieldValue: 123, expectedCode: 'INVALID_FORMAT' },
      { description: 'password is not a string', fieldName: 'password', fieldValue: 123, expectedCode: 'INVALID_FORMAT' }
    ])('should throw ValidationError with $expectedCode code when $description', ({ fieldName, fieldValue, expectedCode }) => {
      // given
      const data = {
        ...VALID_USER_DATA,
        [fieldName]: fieldValue
      };

      // when & then
      expectValidationError(data, expectedCode, fieldName);
    });

    it('should throw ValidationError when unknown fields are present', () => {
      // given
      const data = {
        ...VALID_USER_DATA,
        unknownField: 'value'
      };

      // when & then
      expectValidationError(data, 'INVALID_FIELD', 'unknownField');
    });

    it('should throw ValidationError with first unknown field only (fail-fast)', () => {
      // given
      const data = {
        ...VALID_USER_DATA,
        unknownField1: 'value1',
        unknownField2: 'value2'
      };

      // when & then
      expectValidationError(data, 'INVALID_FIELD', 'unknownField1');
    });

    it('should trim whitespace from email', () => {
      // given
      const data = {
        email: '   user@example.com   ',
        password: 'password123'
      };

      // when & then
      expect(() => validateCreateUser(data)).not.toThrow();
    });

    it('should trim whitespace from password and then validate length', () => {
      // given
      const data = {
        email: 'user@example.com',
        password: '   password123   '
      };

      // when & then - after trim, still valid length and format
      expect(() => validateCreateUser(data)).not.toThrow();
    });

    it('should reject whitespace-only email after trim (fails isValidEmail)', () => {
      // given
      const data = {
        email: '   ',
        password: 'password123'
      };

      // when & then
      expectValidationError(data, 'INVALID_FORMAT', 'email');
    });

    it('should reject whitespace-only password after trim (fails isValidPassword)', () => {
      // given
      const data = {
        email: 'user@example.com',
        password: '   '
      };

      // when & then
      expectValidationError(data, 'INVALID_FORMAT', 'password');
    });
  });

  describe('boundary values', () => {
    it.each([
      {
        description: 'email with minimum length (valid format)',
        email: 'a@b.c',
        password: 'password123'
      },
      {
        description: 'password with exactly 8 characters',
        email: 'user@example.com',
        password: '12345678'
      },
      {
        description: 'password with exactly 128 characters',
        email: 'user@example.com',
        password: 'a'.repeat(128)
      }
    ])('should accept valid $description', ({ email, password }) => {
      // given
      const data = { email, password };

      // when & then
      expect(() => validateCreateUser(data)).not.toThrow();
    });

    it.each([
      {
        description: 'password with exactly 7 characters',
        email: 'user@example.com',
        password: '1234567',
        expectedField: 'password'
      },
      {
        description: 'password with exactly 129 characters',
        email: 'user@example.com',
        password: 'a'.repeat(129),
        expectedField: 'password'
      },
      {
        description: 'email exceeding exactly 254 characters',
        email: 'a'.repeat(243) + '@example.com',
        password: 'password123',
        expectedField: 'email'
      }
    ])('should reject $description', ({ email, password, expectedField }) => {
      // given
      const data = { email, password };

      // when & then
      expectValidationError(data, 'INVALID_FORMAT', expectedField);
    });
  });
});

