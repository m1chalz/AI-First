import { AnnouncementSpecies, AnnouncementSex } from '../types/announcement';

export const SPECIES_LABELS: Record<AnnouncementSpecies, string> = {
  DOG: 'Dog',
  CAT: 'Cat',
  BIRD: 'Bird',
  RABBIT: 'Rabbit',
  OTHER: 'Other'
};

export const SEX_LABELS: Record<AnnouncementSex, string> = {
  MALE: 'Male',
  FEMALE: 'Female',
  UNKNOWN: 'Unknown'
};
