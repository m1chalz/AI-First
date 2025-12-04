import type { Announcement, AnnouncementWithManagementPassword, CreateAnnouncementDto, LocationFilter } from '../types/announcement.ts';
import type { IAnnouncementRepository } from '../database/repositories/announcement-repository.ts';
import type { PhotoUploadService } from './photo-upload-service.ts';
import { ConflictError, NotFoundError } from '../lib/errors.ts';
import { generateManagementPassword } from '../lib/password-management.ts';

const DEFAULT_RANGE_KM = 5;

export class AnnouncementService {
  constructor(
    private repository: IAnnouncementRepository,
    private validator: (data: CreateAnnouncementDto) => void,
    private sanitizer: (data: string) => string,
    private locationValidator: (lat?: number, lng?: number, range?: number) => void,
    private photoUploadService?: PhotoUploadService
  ) {}

  async getAllAnnouncements(lat?: number, lng?: number, range?: number): Promise<Announcement[]> {
    this.locationValidator(lat, lng, range);
    
    let locationFilter: LocationFilter | undefined;
    if (lat !== undefined && lng !== undefined) {
      locationFilter = {
        lat,
        lng,
        range: range ?? DEFAULT_RANGE_KM,
      };
    }
    
    return this.repository.findAll(locationFilter);
  }

  async getAnnouncementById(id: string): Promise<Announcement> {
    const announcement = await this.repository.findById(id);
    
    if (!announcement) {
      throw new NotFoundError();
    }
    
    return announcement;
  }

  async createAnnouncement(data: CreateAnnouncementDto): Promise<AnnouncementWithManagementPassword> {
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
      reward: data.reward ? this.sanitizer(data.reward) : undefined,
    } as CreateAnnouncementDto;

    const managementPassword = generateManagementPassword();

    const created: Announcement = await this.repository.create(sanitized, managementPassword);

    return {
      ...created,
      managementPassword,
    };
  }

  async deleteAnnouncement(id: string): Promise<void> {
    const announcement = await this.repository.findById(id);
    
    if (!announcement) {
      throw new NotFoundError();
    }

    // Delete associated photos if photoUploadService is available
    if (this.photoUploadService) {
      await this.photoUploadService.deletePhotos(announcement.photoUrl);
    }

    await this.repository.delete(id);
  }
}

