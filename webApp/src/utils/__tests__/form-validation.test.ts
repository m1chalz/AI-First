import { describe, it, expect } from 'vitest';
import {
  validateLastSeenDate,
  validateSpecies,
  validateBreed,
  validateSex,
  validateAge,
  validateDescription,
  validateAllFields,
  isFormValid,
  VALIDATION_MESSAGES
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

describe('validateBreed', () => {
  it.each([
    ['empty with species selected', '', 'DOG', VALIDATION_MESSAGES.BREED_REQUIRED],
    ['whitespace only with species selected', '   ', 'DOG', VALIDATION_MESSAGES.BREED_REQUIRED],
  ])('should return error when breed is %s', (_, breed, species, expectedError) => {
    expect(validateBreed(breed, species)).toBe(expectedError);
  });

  it.each([
    ['species not selected', '', ''],
    ['species selected and breed provided', 'Golden Retriever', 'DOG'],
  ])('should return null when %s', (_, breed, species) => {
    expect(validateBreed(breed, species)).toBeNull();
  });
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

describe('validateAllFields', () => {
  it('should return errors for all invalid required fields', () => {
    const formData = {
      lastSeenDate: '',
      species: '',
      breed: '',
      sex: '',
      age: '',
      description: ''
    };

    const errors = validateAllFields(formData);
    
    expect(errors.lastSeenDate).toBe(VALIDATION_MESSAGES.LAST_SEEN_DATE_REQUIRED);
    expect(errors.species).toBe(VALIDATION_MESSAGES.SPECIES_REQUIRED);
    expect(errors.sex).toBe(VALIDATION_MESSAGES.SEX_REQUIRED);
    expect(errors.breed).toBeUndefined();
  });

  it('should return empty object when all fields are valid', () => {
    const formData = {
      lastSeenDate: '2025-01-01',
      species: 'DOG',
      breed: 'Golden Retriever',
      sex: 'MALE',
      age: '5',
      description: 'Friendly dog'
    };

    const errors = validateAllFields(formData);
    expect(Object.keys(errors).length).toBe(0);
  });

  it('should validate breed when species is selected', () => {
    const formData = {
      lastSeenDate: '2025-01-01',
      species: 'DOG',
      breed: '',
      sex: 'MALE',
      age: '',
      description: ''
    };

    const errors = validateAllFields(formData);
    expect(errors.breed).toBe(VALIDATION_MESSAGES.BREED_REQUIRED);
  });
});

describe('isFormValid', () => {
  it.each([
    [
      'required fields are missing',
      { lastSeenDate: '', species: '', breed: '', sex: '', age: '', description: '' },
      false
    ],
    [
      'all required fields are valid',
      { lastSeenDate: '2025-01-01', species: 'DOG', breed: 'Golden Retriever', sex: 'MALE', age: '', description: '' },
      true
    ],
    [
      'optional field is invalid',
      { lastSeenDate: '2025-01-01', species: 'DOG', breed: 'Golden Retriever', sex: 'MALE', age: '50', description: '' },
      false
    ],
  ])('should return correct result when %s', (_, formData, result) => {
    expect(isFormValid(formData)).toBe(result);
  });
});

