import type { Announcement, AnnouncementDto, CreateAnnouncementDto } from '../types/announcement.ts';
import type { IAnnouncementRepository } from '../database/repositories/announcement-repository.ts';
import { ConflictError, NotFoundError } from '../lib/errors.ts';
import { generateManagementPassword } from '../lib/password-management.ts';

export class AnnouncementService {
  constructor(
    private repository: IAnnouncementRepository,
    private validator: (data: CreateAnnouncementDto) => void,
    private sanitizer: (data: string) => string
  ) {}

  async getAllAnnouncements(): Promise<Announcement[]> {
    return this.repository.findAll();
  }

  async getAnnouncementById(id: string): Promise<Announcement> {
    const announcement = await this.repository.findById(id);
    
    if (!announcement) {
      throw new NotFoundError();
    }
    
    return announcement;
  }

  async createAnnouncement(data: CreateAnnouncementDto): Promise<AnnouncementDto> {
    this.validator(data);

    if (data.microchipNumber) {
      const existing = await this.repository.existsByMicrochip(data.microchipNumber);
      if (existing) {
        throw new ConflictError('An entity with this value already exists', 'microchipNumber');
      }
    }

    const sanitized = {
      ...data,
      petName: data.petName ? this.sanitizer(data.petName) : undefined,
      species: this.sanitizer(data.species),
      breed: data.breed ? this.sanitizer(data.breed) : undefined,
      sex: this.sanitizer(data.sex),
      description: data.description ? this.sanitizer(data.description) : undefined,
      locationCity: data.locationCity ? this.sanitizer(data.locationCity) : undefined,
      reward: data.reward ? this.sanitizer(data.reward) : undefined,
    } as CreateAnnouncementDto;

    const managementPassword = generateManagementPassword();

    const created = await this.repository.create(sanitized, managementPassword);

    return {
      ...created,
      managementPassword,
    };
  }
}

