import type { Knex } from 'knex';
import type { Announcement, AnnouncementRow } from '../../types/announcement.js';

export class AnnouncementRepository {
  constructor(private db: Knex) {}

  async findAll(): Promise<Announcement[]> {
    const rows: AnnouncementRow[] = await this.db('announcement').select('*');
    
    return rows.map((row) => ({
      id: row.id,
      petName: row.pet_name,
      species: row.species,
      breed: row.breed,
      gender: row.gender,
      description: row.description,
      location: row.location,
      locationRadius: row.location_radius,
      lastSeenDate: row.last_seen_date,
      email: row.email,
      phone: row.phone,
      photoUrl: row.photo_url,
      status: row.status,
      createdAt: row.created_at,
      updatedAt: row.updated_at,
    }));
  }
}

