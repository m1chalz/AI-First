import { AnimalSpecies, AnimalSex } from '../types/animal';

export const SPECIES_LABELS: Record<AnimalSpecies, string> = {
  DOG: 'Dog',
  CAT: 'Cat',
  BIRD: 'Bird',
  RABBIT: 'Rabbit',
  OTHER: 'Other'
};

export const SEX_LABELS: Record<AnimalSex, string> = {
  MALE: 'Male',
  FEMALE: 'Female',
  UNKNOWN: 'Unknown'
};
