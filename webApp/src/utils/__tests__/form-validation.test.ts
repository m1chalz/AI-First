import { describe, it, expect } from 'vitest';
import {
  validateLastSeenDate,
  validateSpecies,
  validateSex,
  validateAge,
  validateDescription,
  validateLatitude,
  validateLongitude,
  validateAllFields,
  isFormValid,
  VALIDATION_MESSAGES,
  validatePhoneNumber,
  validateEmailAddress,
  validateContactForm,
} from '../form-validation';

describe('validateLastSeenDate', () => {
  const futureDate = new Date();
  futureDate.setDate(futureDate.getDate() + 1);
  const futureDateStr = futureDate.toISOString().split('T')[0];
  const today = new Date().toISOString().split('T')[0];

  it.each([
    ['empty', '', VALIDATION_MESSAGES.LAST_SEEN_DATE_REQUIRED],
    ['future date', futureDateStr, VALIDATION_MESSAGES.LAST_SEEN_DATE_FUTURE],
  ])('should return error when date is %s', (_, date, expectedError) => {
    expect(validateLastSeenDate(date)).toBe(expectedError);
  });

  it.each([
    ['today', today],
    ['past date', '2025-01-01'],
  ])('should return null when date is %s', (_, date) => {
    expect(validateLastSeenDate(date)).toBeNull();
  });
});

describe('validateSpecies', () => {
  it.each([
    ['empty', '', VALIDATION_MESSAGES.SPECIES_REQUIRED],
    ['invalid', 'ELEPHANT', VALIDATION_MESSAGES.SPECIES_INVALID],
  ])('should return error when species is %s', (_, species, expectedError) => {
    expect(validateSpecies(species)).toBe(expectedError);
  });

  it.each(['DOG', 'CAT', 'BIRD', 'RABBIT', 'OTHER'])(
    'should return null when species is %s',
    (species) => {
      expect(validateSpecies(species)).toBeNull();
    }
  );
});

describe('validateSex', () => {
  it.each([
    ['empty', '', VALIDATION_MESSAGES.SEX_REQUIRED],
    ['UNKNOWN', 'UNKNOWN', VALIDATION_MESSAGES.SEX_INVALID],
    ['invalid', 'OTHER', VALIDATION_MESSAGES.SEX_INVALID],
  ])('should return error when sex is %s', (_, sex, expectedError) => {
    expect(validateSex(sex)).toBe(expectedError);
  });

  it.each(['MALE', 'FEMALE'])(
    'should return null when sex is %s',
    (sex) => {
      expect(validateSex(sex)).toBeNull();
    }
  );
});

describe('validateAge', () => {
  it.each([
    ['not a number', 'abc', VALIDATION_MESSAGES.AGE_INVALID_NUMBER],
    ['decimal', '5.5', VALIDATION_MESSAGES.AGE_INVALID_NUMBER],
    ['negative', '-1', VALIDATION_MESSAGES.AGE_OUT_OF_RANGE],
    ['greater than 40', '41', VALIDATION_MESSAGES.AGE_OUT_OF_RANGE],
  ])('should return error when age is %s', (_, age, expectedError) => {
    expect(validateAge(age)).toBe(expectedError);
  });

  it.each([
    ['empty', ''],
    ['0', '0'],
    ['40', '40'],
    ['valid integer within range', '5'],
  ])('should return null when age is %s', (_, age) => {
    expect(validateAge(age)).toBeNull();
  });
});

describe('validateDescription', () => {
  it('should return error when description exceeds 500 characters', () => {
    const longDescription = 'a'.repeat(501);
    expect(validateDescription(longDescription)).toBe(VALIDATION_MESSAGES.DESCRIPTION_TOO_LONG);
  });

  it.each([
    ['empty', ''],
    ['exactly 500 characters', 'a'.repeat(500)],
    ['under 500 characters', 'Valid description'],
  ])('should return null when description is %s', (_, description) => {
    expect(validateDescription(description)).toBeNull();
  });
});

describe('validateLatitude', () => {
  it.each([
    ['empty', '', VALIDATION_MESSAGES.LATITUDE_REQUIRED],
    ['not a number', 'abc', VALIDATION_MESSAGES.LATITUDE_INVALID_NUMBER],
    ['less than -90', '-91', VALIDATION_MESSAGES.LATITUDE_OUT_OF_RANGE],
    ['greater than 90', '91', VALIDATION_MESSAGES.LATITUDE_OUT_OF_RANGE],
  ])('should return error when latitude is %s', (_, latitude, expectedError) => {
    expect(validateLatitude(latitude)).toBe(expectedError);
  });

  it.each([
    ['-90', '-90'],
    ['90', '90'],
    ['0', '0'],
    ['valid value', '52.5200'],
  ])('should return null when latitude is %s', (_, latitude) => {
    expect(validateLatitude(latitude)).toBeNull();
  });
});

describe('validateLongitude', () => {
  it.each([
    ['empty', '', VALIDATION_MESSAGES.LONGITUDE_REQUIRED],
    ['not a number', 'abc', VALIDATION_MESSAGES.LONGITUDE_INVALID_NUMBER],
    ['less than -180', '-181', VALIDATION_MESSAGES.LONGITUDE_OUT_OF_RANGE],
    ['greater than 180', '181', VALIDATION_MESSAGES.LONGITUDE_OUT_OF_RANGE],
  ])('should return error when longitude is %s', (_, longitude, expectedError) => {
    expect(validateLongitude(longitude)).toBe(expectedError);
  });

  it.each([
    ['-180', '-180'],
    ['180', '180'],
    ['0', '0'],
    ['valid value', '13.4050'],
  ])('should return null when longitude is %s', (_, longitude) => {
    expect(validateLongitude(longitude)).toBeNull();
  });
});

describe('validateAllFields', () => {
  it('should return errors for all invalid required fields', () => {
    const formData = {
      lastSeenDate: '',
      species: '',
      breed: '',
      sex: '',
      age: '',
      description: '',
      latitude: '',
      longitude: ''
    };

    const errors = validateAllFields(formData);
    
    expect(errors.lastSeenDate).toBe(VALIDATION_MESSAGES.LAST_SEEN_DATE_REQUIRED);
    expect(errors.species).toBe(VALIDATION_MESSAGES.SPECIES_REQUIRED);
    expect(errors.sex).toBe(VALIDATION_MESSAGES.SEX_REQUIRED);
    expect(errors.latitude).toBe(VALIDATION_MESSAGES.LATITUDE_REQUIRED);
    expect(errors.longitude).toBe(VALIDATION_MESSAGES.LONGITUDE_REQUIRED);
    expect(errors.breed).toBeUndefined();
  });

  it('should return empty object when all fields are valid', () => {
    const formData = {
      lastSeenDate: '2025-01-01',
      species: 'DOG',
      breed: 'Golden Retriever',
      sex: 'MALE',
      age: '5',
      description: 'Friendly dog',
      latitude: '52.5200',
      longitude: '13.4050'
    };

    const errors = validateAllFields(formData);
    expect(Object.keys(errors).length).toBe(0);
  });

  it('should return errors for invalid latitude/longitude', () => {
    const formData = {
      lastSeenDate: '2025-01-01',
      species: 'DOG',
      breed: 'Golden Retriever',
      sex: 'MALE',
      age: '',
      description: '',
      latitude: '100',
      longitude: '200'
    };

    const errors = validateAllFields(formData);
    expect(errors.latitude).toBe(VALIDATION_MESSAGES.LATITUDE_OUT_OF_RANGE);
    expect(errors.longitude).toBe(VALIDATION_MESSAGES.LONGITUDE_OUT_OF_RANGE);
  });
});

describe('isFormValid', () => {
  it.each([
    [
      'required fields are missing',
      { lastSeenDate: '', species: '', breed: '', sex: '', age: '', description: '', latitude: '', longitude: '' },
      false
    ],
    [
      'all required fields are valid',
      { lastSeenDate: '2025-01-01', species: 'DOG', breed: 'Golden Retriever', sex: 'MALE', age: '', description: '', latitude: '52.52', longitude: '13.40' },
      true
    ],
    [
      'optional field age is invalid',
      { lastSeenDate: '2025-01-01', species: 'DOG', breed: 'Golden Retriever', sex: 'MALE', age: '50', description: '', latitude: '52.52', longitude: '13.40' },
      false
    ],
    [
      'latitude is invalid',
      { lastSeenDate: '2025-01-01', species: 'DOG', breed: 'Golden Retriever', sex: 'MALE', age: '', description: '', latitude: '100', longitude: '13.40' },
      false
    ],
    [
      'longitude is invalid',
      { lastSeenDate: '2025-01-01', species: 'DOG', breed: 'Golden Retriever', sex: 'MALE', age: '', description: '', latitude: '52.52', longitude: '200' },
      false
    ],
  ])('should return correct result when %s', (_, formData, result) => {
    expect(isFormValid(formData)).toBe(result);
  });
});

// Contact form validation tests
describe('validatePhoneNumber', () => {
  it.each([
    { phone: '123', expectedError: null },
    { phone: '+1 234 567 890', expectedError: null },
    { phone: 'abc123def', expectedError: null },
    { phone: '', expectedError: null },
    { phone: 'abc', expectedError: 'Enter a valid phone number' },
    { phone: '+++', expectedError: 'Enter a valid phone number' },
  ])('should validate phone "$phone" with error "$expectedError"', ({ phone, expectedError }) => {
    expect(validatePhoneNumber(phone)).toBe(expectedError);
  });
});

describe('validateEmailAddress', () => {
  it.each([
    { email: 'user@example.com', expectedError: null },
    { email: 'user+tag@domain.co.uk', expectedError: null },
    { email: 'test.user@sub.domain.com', expectedError: null },
    { email: '', expectedError: null },
    { email: 'invalid@', expectedError: 'Enter a valid email address' },
    { email: '@example.com', expectedError: 'Enter a valid email address' },
    { email: 'owner', expectedError: 'Enter a valid email address' },
    { email: 'owner @example.com', expectedError: 'Enter a valid email address' },
  ])('should validate email "$email" with error "$expectedError"', ({ email, expectedError }) => {
    expect(validateEmailAddress(email)).toBe(expectedError);
  });
});

describe('validateContactForm', () => {
  it.each([
    { phone: '123', email: '', isValid: true },
    { phone: '', email: 'user@example.com', isValid: true },
    { phone: '123', email: 'user@example.com', isValid: true },
    { phone: '', email: '', isValid: false },
    { phone: 'abc', email: '', isValid: false },
    { phone: '', email: 'invalid@', isValid: false },
    { phone: '123', email: 'invalid', isValid: false },
    { phone: 'abc', email: 'invalid@', isValid: false },
  ])('phone="$phone" email="$email" should isValid=$isValid', ({ phone, email, isValid }) => {
    const result = validateContactForm({ phone, email });
    expect(result.isValid).toBe(isValid);
  });

  it('should return appropriate error messages', () => {
    const result = validateContactForm({ phone: 'abc', email: 'invalid@' });
    expect(result.phoneError).toBe('Enter a valid phone number');
    expect(result.emailError).toBe('Enter a valid email address');
  });

  it('should return empty errors for valid inputs', () => {
    const result = validateContactForm({ phone: '123', email: '' });
    expect(result.phoneError).toBe('');
    expect(result.emailError).toBe('');
    expect(result.isValid).toBe(true);
  });
});

