export interface Location {
  city: string;
  radiusKm: number;
}

export enum AnimalSpecies {
  DOG = 'Dog',
  CAT = 'Cat',
  BIRD = 'Bird',
  RABBIT = 'Rabbit',
  OTHER = 'Other'
}

export enum AnimalGender {
  MALE = 'Male',
  FEMALE = 'Female',
  UNKNOWN = 'Unknown'
}

export enum AnimalStatus {
  ACTIVE = 'Active',
  FOUND = 'Found',
  CLOSED = 'Closed'
}

export const ANIMAL_STATUS_BADGE_COLORS: Record<AnimalStatus, string> = {
  [AnimalStatus.ACTIVE]: '#FF0000',
  [AnimalStatus.FOUND]: '#0074FF',
  [AnimalStatus.CLOSED]: '#93A2B4'
};

export interface Animal {
  id: string;
  name: string;
  photoUrl: string;
  location: Location;
  species: AnimalSpecies;
  breed: string;
  gender: AnimalGender;
  status: AnimalStatus;
  lastSeenDate: string;
  description: string;
  email?: string;
  phone?: string;
}
