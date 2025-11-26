import { v4 as uuidv4 } from 'uuid';
import type { Knex } from 'knex';
import type { Announcement, AnnouncementRow, CreateAnnouncementDto } from '../../types/announcement.ts';
import { hashPassword } from '../../lib/password-management.ts';

export interface IAnnouncementRepository {
  findAll(): Promise<Announcement[]>;
  findById(id: string): Promise<Announcement | null>;
  findPasswordHashById(id: string): Promise<string | null>;
  existsByMicrochip(microchipNumber: string): Promise<boolean>;
  create(data: CreateAnnouncementDto, managementPassword: string): Promise<Announcement>;
  updatePhotoUrl(trx: Knex.Transaction, id: string, photoUrl: string): Promise<void>;
}

export class AnnouncementRepository implements IAnnouncementRepository {
  constructor(private db: Knex) {}

  async findAll(): Promise<Announcement[]> {
    const rows: AnnouncementRow[] = await this.db('announcement').select('*');
    
    return rows.map(this.mapRowToAnnouncement);
  }

  async findById(id: string): Promise<Announcement | null> {
    const row: AnnouncementRow | undefined = await this.db('announcement')
      .where('id', id)
      .first();
    
    return row ? this.mapRowToAnnouncement(row) : null;
  }

  async findPasswordHashById(id: string): Promise<string | null> {
    const hash: AnnouncementRow | undefined = await this.db('announcement')
      .select('management_password_hash')
      .where('id', id)
      .first();
    
    return hash?.management_password_hash ?? null;
  }

  async existsByMicrochip(microchipNumber: string): Promise<boolean> {
    const row: AnnouncementRow | undefined = await this.db('announcement')
      .where('microchip_number', microchipNumber)
      .first();
    
    return !!row;
  }

  async create(data: CreateAnnouncementDto, managementPassword: string): Promise<Announcement> {
    const passwordHash = await hashPassword(managementPassword);
    const id = uuidv4();
    const now = new Date().toISOString();
    
    const row: AnnouncementRow = {
      id,
      pet_name: data.petName ?? null,
      species: data.species,
      breed: data.breed ?? null,
      sex: data.sex,
      age: data.age ?? null,
      description: data.description ?? null,
      microchip_number: data.microchipNumber ?? null,
      location_latitude: data.locationLatitude,
      location_longitude: data.locationLongitude,
      last_seen_date: data.lastSeenDate,
      email: data.email ?? null,
      phone: data.phone ?? null,
      photo_url: data.photoUrl,
      status: data.status,
      reward: data.reward ?? null,
      management_password_hash: passwordHash,
      created_at: now,
      updated_at: now,
    };

    await this.db('announcement').insert(row);
    return this.findById(id) as Promise<Announcement>;
  }

  async updatePhotoUrl(trx: Knex.Transaction, id: string, photoUrl: string): Promise<void> {
    await trx('announcement')
      .where('id', id)
      .update({
        photo_url: photoUrl,
        updated_at: new Date().toISOString(),
      });
  }

  private mapRowToAnnouncement(row: AnnouncementRow): Announcement {
    return {
      id: row.id,
      petName: row.pet_name,
      species: row.species,
      breed: row.breed,
      sex: row.sex,
      age: row.age,
      description: row.description,
      microchipNumber: row.microchip_number,
      locationLatitude: row.location_latitude,
      locationLongitude: row.location_longitude,
      lastSeenDate: row.last_seen_date,
      email: row.email,
      phone: row.phone,
      photoUrl: row.photo_url,
      status: row.status,
      reward: row.reward,
      createdAt: row.created_at,
      updatedAt: row.updated_at,
    };
  }
}

