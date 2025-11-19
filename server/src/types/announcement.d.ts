export type Species = 'DOG' | 'CAT' | 'BIRD' | 'RABBIT' | 'OTHER';

export type Gender = 'MALE' | 'FEMALE' | 'UNKNOWN';

export type AnnouncementStatus = 'ACTIVE' | 'FOUND' | 'CLOSED';

export interface Announcement {
  id: string;
  petName: string;
  species: Species;
  breed: string | null;
  gender: Gender;
  description: string;
  location: string;
  locationRadius: number | null;
  lastSeenDate: string;
  email: string | null;
  phone: string;
  photoUrl: string | null;
  status: AnnouncementStatus;
  createdAt: string;
  updatedAt: string;
}

export interface AnnouncementRow {
  id: string;
  pet_name: string;
  species: Species;
  breed: string | null;
  gender: Gender;
  description: string;
  location: string;
  location_radius: number | null;
  last_seen_date: string;
  email: string | null;
  phone: string;
  photo_url: string | null;
  status: AnnouncementStatus;
  created_at: string;
  updated_at: string;
}

