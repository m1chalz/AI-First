import { ANIMAL_SPECIES } from '../types/animal';

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
  LATITUDE_REQUIRED: 'Please enter latitude',
  LATITUDE_INVALID_NUMBER: 'Latitude must be a number',
  LATITUDE_OUT_OF_RANGE: 'Latitude must be between -90 and 90',
  LONGITUDE_REQUIRED: 'Please enter longitude',
  LONGITUDE_INVALID_NUMBER: 'Longitude must be a number',
  LONGITUDE_OUT_OF_RANGE: 'Longitude must be between -180 and 180',
} as const;

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
  
  if (!ANIMAL_SPECIES.includes(species as typeof ANIMAL_SPECIES[number])) {
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

export function validateLatitude(latitudeStr: string): string | null {
  if (!latitudeStr || !latitudeStr.trim()) {
    return null;
  }
  
  const latitude = Number(latitudeStr);
  
  if (isNaN(latitude)) {
    return VALIDATION_MESSAGES.LATITUDE_INVALID_NUMBER;
  }
  
  if (latitude < -90 || latitude > 90) {
    return VALIDATION_MESSAGES.LATITUDE_OUT_OF_RANGE;
  }
  
  return null;
}

export function validateLongitude(longitudeStr: string): string | null {
  if (!longitudeStr || !longitudeStr.trim()) {
    return null;
  }
  
  const longitude = Number(longitudeStr);
  
  if (isNaN(longitude)) {
    return VALIDATION_MESSAGES.LONGITUDE_INVALID_NUMBER;
  }
  
  if (longitude < -180 || longitude > 180) {
    return VALIDATION_MESSAGES.LONGITUDE_OUT_OF_RANGE;
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
  latitude: string;
  longitude: string;
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
  
  const latitudeError = validateLatitude(formData.latitude);
  if (latitudeError) errors.latitude = latitudeError;
  
  const longitudeError = validateLongitude(formData.longitude);
  if (longitudeError) errors.longitude = longitudeError;
  
  return errors;
}

export function isFormValid(formData: {
  lastSeenDate: string;
  species: string;
  breed: string;
  sex: string;
  age: string;
  description: string;
  latitude: string;
  longitude: string;
}): boolean {
  const errors = validateAllFields(formData);
  return Object.keys(errors).length === 0;
}

