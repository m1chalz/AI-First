import type { Announcement } from '../types/announcement.js';
import type { AnnouncementRepository } from '../database/repositories/announcement-repository.js';

export class AnnouncementService {
  constructor(private repository: AnnouncementRepository) {}

  async getAllAnnouncements(): Promise<Announcement[]> {
    return await this.repository.findAll();
  }
}

