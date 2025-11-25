export type AnnouncementStatus = 'MISSING' | 'FOUND';

export interface CreateAnnouncementDto {
  petName?: string;
  species: string;
  breed?: string;
  sex: string;
  age?: number;
  description?: string;
  microchipNumber?: string;
  locationCity?: string;
  locationLatitude: number;
  locationLongitude: number;
  locationRadius?: number;
  email?: string;
  phone?: string;
  photoUrl: string;
  lastSeenDate: string;
  status: AnnouncementStatus;
  reward?: string;
}

export interface AnnouncementDto extends Omit<CreateAnnouncementDto, 'petName' | 'breed' | 'age' | 'description' | 'microchipNumber' | 'locationCity' | 'locationRadius' | 'email' | 'phone' | 'reward'> {
  id: string;
  createdAt: string;
  managementPassword?: string;
  petName?: string | null;
  breed?: string | null;
  age?: number | null;
  description?: string | null;
  microchipNumber?: string | null;
  locationCity?: string | null;
  locationRadius?: number | null;
  email?: string | null;
  phone?: string | null;
  reward?: string | null;
}

export interface Announcement {
  id: string;
  petName?: string | null;
  species: string;
  breed?: string | null;
  sex: string;
  age?: number | null;
  description?: string | null;
  microchipNumber?: string | null;
  locationCity?: string | null;
  locationLatitude: number;
  locationLongitude: number;
  locationRadius?: number | null;
  email?: string | null;
  phone?: string | null;
  photoUrl: string;
  lastSeenDate: string;
  status: AnnouncementStatus;
  reward?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface AnnouncementRow {
  id: string;
  pet_name: string | null;
  species: string;
  breed: string | null;
  sex: string;
  age: number | null;
  description: string | null;
  microchip_number: string | null;
  location_city: string | null;
  location_latitude: number;
  location_longitude: number;
  location_radius: number | null;
  email: string | null;
  phone: string | null;
  photo_url: string;
  last_seen_date: string;
  status: AnnouncementStatus;
  reward: string | null;
  management_password_hash: string;
  created_at: string;
  updated_at: string;
}

