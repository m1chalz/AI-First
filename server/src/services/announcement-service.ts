import type { Announcement } from '../types/announcement.ts';
import type { IAnnouncementRepository } from '../database/repositories/announcement-repository.ts';

export class AnnouncementService {
  constructor(private repository: IAnnouncementRepository) {}

  async getAllAnnouncements(): Promise<Announcement[]> {
    return this.repository.findAll();
  }

  async getAnnouncementById(id: string): Promise<Announcement | null> {
    return this.repository.findById(id);
  }
}

