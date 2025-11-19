import { describe, it, expect } from 'vitest';
import { AnnouncementService } from '../announcement-service.js';
import type { Announcement } from '../../types/announcement.js';
import type { AnnouncementRepository } from '../../database/repositories/announcement-repository.js';

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
      } as AnnouncementRepository;
      
      const service = new AnnouncementService(fakeRepository);
      
      // When: Service retrieves all announcements
      const result = await service.getAllAnnouncements();
      
      // Then: Returns expected announcements from repository
      expect(result).toEqual(announcements);
      expect(result.length).toBe(expectedLength);
    });
  });
});
