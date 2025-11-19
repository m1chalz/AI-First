import type { Announcement } from '../types/announcement.ts';
import type { AnnouncementRepository } from '../database/repositories/announcement-repository.ts';

export class AnnouncementService {
  constructor(private repository: AnnouncementRepository) {}

  async getAllAnnouncements(): Promise<Announcement[]> {
    return await this.repository.findAll();
  }
}

