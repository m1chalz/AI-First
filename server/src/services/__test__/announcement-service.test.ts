import { describe, it, expect } from 'vitest';
import { AnnouncementService } from '../announcement-service.ts';
import type { Announcement } from '../../types/announcement.ts';
import type { AnnouncementRepository } from '../../database/repositories/announcement-repository.ts';

const MOCK_ANNOUNCEMENT: Announcement = {
  id: '550e8400-e29b-41d4-a716-446655440000',
  petName: 'Max',
  species: 'DOG',
  breed: 'Golden Retriever',
  gender: 'MALE',
  description: 'Friendly dog',
  location: 'Central Park',
  locationRadius: 5,
  lastSeenDate: '2025-11-18',
  email: 'john@example.com',
  phone: '+1-555-0101',
  photoUrl: 'https://example.com/max.jpg',
  status: 'ACTIVE',
  createdAt: '2025-11-19T10:00:00Z',
  updatedAt: '2025-11-19T10:00:00Z',
};

describe('AnnouncementService', () => {
  describe('getAllAnnouncements', () => {
    it.each([
      { announcements: [MOCK_ANNOUNCEMENT], expectedLength: 1 },
      { announcements: [], expectedLength: 0 },
    ])('should return $expectedLength announcements when repository returns $expectedLength items', async ({ announcements, expectedLength }) => {
      // Given: Repository returns specified announcements
      const fakeRepository = {
        findAll: async () => announcements,
        findById: async () => null,
      } as Pick<AnnouncementRepository, 'findAll' | 'findById'>;
      
      const service = new AnnouncementService(fakeRepository as AnnouncementRepository);
      
      // When: Service retrieves all announcements
      const result = await service.getAllAnnouncements();
      
      // Then: Returns expected announcements from repository
      expect(result).toEqual(announcements);
      expect(result.length).toBe(expectedLength);
    });
  });

  describe('getAnnouncementById', () => {
    it('should return announcement when ID exists', async () => {
      // Given: Repository with test announcement
      const fakeRepository = {
        findAll: async () => [MOCK_ANNOUNCEMENT],
        findById: async (id: string) => id === MOCK_ANNOUNCEMENT.id ? MOCK_ANNOUNCEMENT : null,
      } as Pick<AnnouncementRepository, 'findAll' | 'findById'>;
      
      const service = new AnnouncementService(fakeRepository as AnnouncementRepository);
      
      // When: Service is called with existing ID
      const result = await service.getAnnouncementById(MOCK_ANNOUNCEMENT.id);
      
      // Then: Announcement is returned
      expect(result).toEqual(MOCK_ANNOUNCEMENT);
    });

    it('should return null when ID does not exist', async () => {
      // Given: Repository with empty data
      const fakeRepository = {
        findAll: async () => [],
        findById: async () => null,
      } as Pick<AnnouncementRepository, 'findAll' | 'findById'>;
      
      const service = new AnnouncementService(fakeRepository as AnnouncementRepository);
      
      // When: Service is called with non-existent ID
      const result = await service.getAnnouncementById('non-existent-id');
      
      // Then: Null is returned
      expect(result).toBeNull();
    });

    it('should return announcement with null optional fields', async () => {
      // Given: Announcement with null optional fields
      const announcementWithNulls: Announcement = {
        ...MOCK_ANNOUNCEMENT,
        breed: null,
        email: null,
        photoUrl: null,
        locationRadius: null,
      };
      
      const fakeRepository = {
        findAll: async () => [announcementWithNulls],
        findById: async (id: string) => id === announcementWithNulls.id ? announcementWithNulls : null,
      } as Pick<AnnouncementRepository, 'findAll' | 'findById'>;
      
      const service = new AnnouncementService(fakeRepository as AnnouncementRepository);
      
      // When: Service is called
      const result = await service.getAnnouncementById(announcementWithNulls.id);
      
      // Then: Announcement with nulls is returned
      expect(result).toEqual(announcementWithNulls);
      expect(result?.breed).toBeNull();
      expect(result?.email).toBeNull();
      expect(result?.photoUrl).toBeNull();
      expect(result?.locationRadius).toBeNull();
    });
  });
});
