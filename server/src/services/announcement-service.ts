import type { Announcement } from '../types/announcement.ts';
import type { AnnouncementRepository } from '../database/repositories/announcement-repository.ts';

export class AnnouncementService {
  constructor(private repository: AnnouncementRepository) {}

  async getAllAnnouncements(): Promise<Announcement[]> {
    return this.repository.findAll();
  }

  async getAnnouncementById(id: string): Promise<Announcement | null> {
    return this.repository.findById(id);
  }
}

