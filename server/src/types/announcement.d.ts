export type AnnouncementStatus = 'MISSING' | 'FOUND';

export interface LocationFilter {
  lat: number;
  lng: number;
  range: number;
}

export interface CreateAnnouncementDto {
  petName?: string;
  species: string;
  breed?: string;
  sex: string;
  age?: number;
  description?: string;
  microchipNumber?: string;
  locationLatitude: number;
  locationLongitude: number;
  email?: string;
  phone?: string;
  lastSeenDate: string;
  status: AnnouncementStatus;
  reward?: string;
}

export interface AnnouncementWithManagementPassword extends Announcement {
  managementPassword: string;
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
  locationLatitude: number;
  locationLongitude: number;
  email?: string | null;
  phone?: string | null;
  photoUrl: string | null;
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
  location_latitude: number;
  location_longitude: number;
  email: string | null;
  phone: string | null;
  photo_url: string | null;
  last_seen_date: string;
  status: AnnouncementStatus;
  reward: string | null;
  management_password_hash: string;
  created_at: string;
  updated_at: string;
}

