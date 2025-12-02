export const VALIDATION_MESSAGES = {
  LAST_SEEN_DATE_REQUIRED: 'Please select the date of disappearance',
  LAST_SEEN_DATE_FUTURE: 'Date cannot be in the future',
  SPECIES_REQUIRED: 'Please select a species',
  SPECIES_INVALID: 'Invalid species selected',
  BREED_REQUIRED: 'Please enter the breed',
  SEX_REQUIRED: 'Please select a gender',
  SEX_INVALID: 'Invalid gender selected',
  AGE_INVALID_NUMBER: 'Age must be a whole number',
  AGE_OUT_OF_RANGE: 'Age must be between 0 and 40',
  DESCRIPTION_TOO_LONG: 'Description cannot exceed 500 characters',
} as const;

const VALID_SPECIES: string[] = ['DOG', 'CAT', 'BIRD', 'RABBIT', 'OTHER'];

export function validateLastSeenDate(date: string): string | null {
  if (!date) {
    return VALIDATION_MESSAGES.LAST_SEEN_DATE_REQUIRED;
  }
  
  const selected = new Date(date + 'T00:00:00');
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  
  if (selected > today) {
    return VALIDATION_MESSAGES.LAST_SEEN_DATE_FUTURE;
  }
  
  return null;
}

export function validateSpecies(species: string): string | null {
  if (!species) {
    return VALIDATION_MESSAGES.SPECIES_REQUIRED;
  }
  
  if (!VALID_SPECIES.includes(species)) {
    return VALIDATION_MESSAGES.SPECIES_INVALID;
  }
  
  return null;
}

export function validateBreed(breed: string, species: string): string | null {
  if (species && !breed.trim()) {
    return VALIDATION_MESSAGES.BREED_REQUIRED;
  }
  
  return null;
}

export function validateSex(sex: string): string | null {
  if (!sex) {
    return VALIDATION_MESSAGES.SEX_REQUIRED;
  }
  
  if (sex !== 'MALE' && sex !== 'FEMALE') {
    return VALIDATION_MESSAGES.SEX_INVALID;
  }
  
  return null;
}

export function validateAge(ageStr: string): string | null {
  if (!ageStr) {
    return null;
  }
  
  const age = Number(ageStr);
  
  if (isNaN(age) || !Number.isInteger(age)) {
    return VALIDATION_MESSAGES.AGE_INVALID_NUMBER;
  }
  
  if (age < 0 || age > 40) {
    return VALIDATION_MESSAGES.AGE_OUT_OF_RANGE;
  }
  
  return null;
}

export function validateDescription(description: string): string | null {
  if (description.length > 500) {
    return VALIDATION_MESSAGES.DESCRIPTION_TOO_LONG;
  }
  
  return null;
}

export function validateAllFields(formData: {
  lastSeenDate: string;
  species: string;
  breed: string;
  sex: string;
  age: string;
  description: string;
}): Record<string, string> {
  const errors: Record<string, string> = {};
  
  const dateError = validateLastSeenDate(formData.lastSeenDate);
  if (dateError) errors.lastSeenDate = dateError;
  
  const speciesError = validateSpecies(formData.species);
  if (speciesError) errors.species = speciesError;
  
  const breedError = validateBreed(formData.breed, formData.species);
  if (breedError) errors.breed = breedError;
  
  const sexError = validateSex(formData.sex);
  if (sexError) errors.sex = sexError;
  
  const ageError = validateAge(formData.age);
  if (ageError) errors.age = ageError;
  
  const descError = validateDescription(formData.description);
  if (descError) errors.description = descError;
  
  return errors;
}

export function isFormValid(formData: {
  lastSeenDate: string;
  species: string;
  breed: string;
  sex: string;
  age: string;
  description: string;
}): boolean {
  const errors = validateAllFields(formData);
  return Object.keys(errors).length === 0;
}

