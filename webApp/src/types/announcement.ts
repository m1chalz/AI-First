export const ANNOUNCEMENT_STATUSES = ['MISSING', 'FOUND', 'CLOSED'] as const;
export type AnnouncementStatus = (typeof ANNOUNCEMENT_STATUSES)[number];

export const ANNOUNCEMENT_SEXES = ['MALE', 'FEMALE', 'UNKNOWN'] as const;
export type AnnouncementSex = (typeof ANNOUNCEMENT_SEXES)[number];

export const ANNOUNCEMENT_SPECIES = ['DOG', 'CAT', 'BIRD', 'RABBIT', 'OTHER'] as const;
export type AnnouncementSpecies = (typeof ANNOUNCEMENT_SPECIES)[number];

export const ANNOUNCEMENT_STATUS_BADGE_COLORS: Record<AnnouncementStatus, string> = {
  MISSING: '#FF0000',
  FOUND: '#0074FF',
  CLOSED: '#93A2B4'
};

export interface Announcement {
  // Required fields
  id: string; // UUID string
  photoUrl: string; // URL to announcement photo
  status: AnnouncementStatus; // MISSING, FOUND, or CLOSED
  lastSeenDate: string; // ISO 8601 date (YYYY-MM-DD)
  species: AnnouncementSpecies; // Species (DOG, CAT, BIRD, RABBIT, OTHER)
  sex: AnnouncementSex; // MALE, FEMALE, or UNKNOWN

  // Basic information (nullable)
  petName: string | null; // Name
  breed: string | null; // Breed
  description: string | null; // Additional description text

  // Location (nullable)
  locationLatitude: number; // Latitude coordinate
  locationLongitude: number; // Longitude coordinate

  // Contact information (nullable, at least one should be present)
  phone: string; // Phone number (may be masked by backend)
  email: string; // Email address

  // Optional detailed fields (nullable)
  microchipNumber: string | null; // Raw microchip number (formatted in component)
  age: number | null; // Approximate age
  reward: string | null; // Reward amount (displayed as-is)
  createdAt: string | null; // ISO 8601 timestamp
  updatedAt: string | null; // ISO 8601 timestamp
}
