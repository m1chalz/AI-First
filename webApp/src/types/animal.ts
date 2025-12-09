export const ANIMAL_STATUSES = ['MISSING', 'FOUND', 'CLOSED'] as const;
export type AnimalStatus = (typeof ANIMAL_STATUSES)[number];

export const ANIMAL_SEXES = ['MALE', 'FEMALE', 'UNKNOWN'] as const;
export type AnimalSex = (typeof ANIMAL_SEXES)[number];

export const ANIMAL_SPECIES = ['DOG', 'CAT', 'BIRD', 'RABBIT', 'OTHER'] as const;
export type AnimalSpecies = (typeof ANIMAL_SPECIES)[number];

export const ANIMAL_STATUS_BADGE_COLORS: Record<AnimalStatus, string> = {
  MISSING: '#FF0000',
  FOUND: '#0074FF',
  CLOSED: '#93A2B4'
};

export interface Animal {
  // Required fields
  id: string; // UUID string
  photoUrl: string; // URL to pet photo
  status: AnimalStatus; // MISSING, FOUND, or CLOSED
  lastSeenDate: string; // ISO 8601 date (YYYY-MM-DD)
  species: AnimalSpecies; // Pet species (DOG, CAT, BIRD, RABBIT, OTHER)
  sex: AnimalSex; // MALE, FEMALE, or UNKNOWN

  // Basic information (nullable)
  petName: string | null; // Pet's name
  breed: string | null; // Pet breed
  description: string | null; // Additional description text

  // Location (nullable)
  locationLatitude: number | null; // Latitude coordinate
  locationLongitude: number | null; // Longitude coordinate

  // Contact information (nullable, at least one should be present)
  phone: string | null; // Phone number (may be masked by backend)
  email: string | null; // Email address

  // Optional detailed fields (nullable)
  microchipNumber: string | null; // Raw microchip number (formatted in component)
  age: number | null; // Approximate age
  reward: string | null; // Reward amount (displayed as-is)
  createdAt: string | null; // ISO 8601 timestamp
  updatedAt: string | null; // ISO 8601 timestamp
}
