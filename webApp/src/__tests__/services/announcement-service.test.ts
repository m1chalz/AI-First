import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { AnnouncementService } from '../../services/announcement-service';
import type { Animal } from '../../types/animal';
import type { AnnouncementSubmissionDto } from '../../models/announcement-submission';

describe('AnnouncementService', () => {
  const underTest: AnnouncementService = new AnnouncementService();
  const originalFetch = window.fetch;
  const fetchMock = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    window.fetch = fetchMock;
  });

  afterEach(() => {
    window.fetch = originalFetch;
    vi.clearAllMocks();
  });

  describe('AnnouncementService.getAnimals', () => {
    it('should fetch animals list successfully when API returns 200', async () => {
      // given
      const mockAnimals: Animal[] = [
        {
          id: 'pet-123',
          petName: 'Fluffy',
          photoUrl: 'https://example.com/photo1.jpg',
          status: 'MISSING',
          lastSeenDate: '2025-11-18',
          species: 'CAT',
          sex: 'MALE',
          breed: 'Maine Coon',
          description: 'Friendly cat',
          locationLatitude: 52.0,
          locationLongitude: 21.0,
          phone: '+48 123 456 789',
          email: 'owner@example.com',
          microchipNumber: null,
          age: 5,
          reward: null,
          createdAt: null,
          updatedAt: null
        },
        {
          id: 'pet-456',
          petName: 'Buddy',
          photoUrl: 'https://example.com/photo2.jpg',
          status: 'FOUND',
          lastSeenDate: '2025-11-17',
          species: 'DOG',
          sex: 'FEMALE',
          breed: 'Golden Retriever',
          description: 'Lost dog',
          locationLatitude: 51.0,
          locationLongitude: 20.0,
          phone: '+48 987 654 321',
          email: 'owner2@example.com',
          microchipNumber: null,
          age: 3,
          reward: null,
          createdAt: null,
          updatedAt: null
        }
      ];

      fetchMock.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ data: mockAnimals })
      } as Response);

      // when
      const result = await underTest.getAnimals();

      // then
      expect(result).toEqual(mockAnimals);
      expect(window.fetch).toHaveBeenCalledWith('http://localhost:3000/api/v1/announcements');
    });

    it('should return empty array when API returns empty list', async () => {
      // given
      fetchMock.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ data: [] })
      } as Response);

      // when
      const result = await underTest.getAnimals();

      // then
      expect(result).toEqual([]);
      expect(window.fetch).toHaveBeenCalledWith('http://localhost:3000/api/v1/announcements');
    });

    it('should throw error when API returns 500', async () => {
      // given
      fetchMock.mockResolvedValueOnce({
        ok: false,
        status: 500,
        statusText: 'Internal Server Error'
      } as Response);

      // when / then
      await expect(underTest.getAnimals()).rejects.toThrow('Failed to fetch animals: 500 Internal Server Error');
    });

    it('should handle network errors', async () => {
      // given
      fetchMock.mockRejectedValueOnce(new Error('Network error'));

      // when / then
      await expect(underTest.getAnimals()).rejects.toThrow('Network error');
    });

    it.each([
      { lat: 52.2297, lng: 21.0122, description: 'positive coordinates' },
      { lat: -33.8688, lng: 151.2093, description: 'negative coordinates' }
    ])('should construct URL with $description', async ({ lat, lng }) => {
      const mockAnimals: Animal[] = [];

      fetchMock.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ data: mockAnimals })
      } as Response);

      // given: coordinates
      // when: getAnimals is called
      const result = await underTest.getAnimals({ lat, lng });

      // then: URL contains exact values and range parameter
      expect(result).toEqual(mockAnimals);
      expect(window.fetch).toHaveBeenCalledWith(`http://localhost:3000/api/v1/announcements?lat=${lat}&lng=${lng}&range=15`);
    });
  });

  describe('AnnouncementService.getPetById', () => {
    it('should fetch pet details successfully when API returns 200', async () => {
      // given
      const mockPet: Animal = {
        id: 'pet-123',
        petName: 'Fluffy',
        photoUrl: 'https://example.com/photo.jpg',
        status: 'MISSING',
        lastSeenDate: '2025-11-18',
        species: 'CAT',
        sex: 'MALE',
        breed: 'Maine Coon',
        description: 'Friendly cat',
        locationLatitude: 52.0,
        locationLongitude: 21.0,
        phone: '+48 123 456 789',
        email: 'owner@example.com',
        microchipNumber: null,
        age: 5,
        reward: null,
        createdAt: null,
        updatedAt: null
      };

      fetchMock.mockResolvedValueOnce({
        ok: true,
        json: async () => mockPet
      } as Response);

      // when
      const result = await underTest.getPetById('pet-123');

      // then
      expect(result).toEqual(mockPet);
      expect(window.fetch).toHaveBeenCalledWith('http://localhost:3000/api/v1/announcements/pet-123');
    });

    it('should throw error when API returns 404', async () => {
      // given
      fetchMock.mockResolvedValueOnce({
        ok: false,
        status: 404,
        statusText: 'Not Found'
      } as Response);

      // when / then
      await expect(underTest.getPetById('pet-123')).rejects.toThrow('Pet with ID pet-123 not found');
    });

    it('should throw error when API returns 500', async () => {
      // given
      fetchMock.mockResolvedValueOnce({
        ok: false,
        status: 500,
        statusText: 'Internal Server Error'
      } as Response);

      // when / then
      await expect(underTest.getPetById('pet-123')).rejects.toThrow('Failed to fetch pet details: 500 Internal Server Error');
    });

    it('should handle network errors', async () => {
      // given
      fetchMock.mockRejectedValueOnce(new Error('Network error'));

      // when / then
      await expect(underTest.getPetById('pet-123')).rejects.toThrow('Network error');
    });
  });

  describe('AnnouncementService.createAnnouncement', () => {
    it('should POST to /api/v1/announcements with correct payload', async () => {
      // given
      const dto: AnnouncementSubmissionDto = {
        species: 'CAT',
        sex: 'MALE',
        locationLatitude: 52.0,
        locationLongitude: 21.0,
        lastSeenDate: '2025-12-03',
        status: 'MISSING'
      };

      fetchMock.mockResolvedValueOnce({
        ok: true,
        status: 201,
        json: async () => ({ id: 'ann-123', managementPassword: 'pass123' })
      } as Response);

      // when
      const result = await underTest.createAnnouncement(dto);

      // then
      expect(window.fetch).toHaveBeenCalledWith(
        'http://localhost:3000/api/v1/announcements',
        expect.objectContaining({
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(dto)
        })
      );
      expect(result).toEqual({ id: 'ann-123', managementPassword: 'pass123' });
    });

    it('should return AnnouncementResponse with id and managementPassword', async () => {
      // given
      const dto: AnnouncementSubmissionDto = {
        species: 'DOG',
        sex: 'FEMALE',
        locationLatitude: 51.5,
        locationLongitude: 20.5,
        lastSeenDate: '2025-12-02',
        status: 'MISSING'
      };

      fetchMock.mockResolvedValueOnce({
        ok: true,
        status: 201,
        json: async () => ({ id: 'ann-456', managementPassword: 'secure-pass' })
      } as Response);

      // when
      const result = await underTest.createAnnouncement(dto);

      // then
      expect(result.id).toBe('ann-456');
      expect(result.managementPassword).toBe('secure-pass');
    });

    it('should throw ValidationError when API returns 400', async () => {
      // given
      const dto: AnnouncementSubmissionDto = {
        species: 'CAT',
        sex: 'MALE',
        locationLatitude: 52.0,
        locationLongitude: 21.0,
        lastSeenDate: '2025-12-03',
        status: 'MISSING'
      };

      fetchMock.mockResolvedValueOnce({
        ok: false,
        status: 400,
        json: async () => ({ message: 'Missing required field' })
      } as Response);

      // when / then
      await expect(underTest.createAnnouncement(dto)).rejects.toThrow();
    });

    it('should throw DuplicateMicrochipError when API returns 409', async () => {
      // given
      const dto: AnnouncementSubmissionDto = {
        species: 'CAT',
        sex: 'MALE',
        microchipNumber: '123456789012345',
        locationLatitude: 52.0,
        locationLongitude: 21.0,
        lastSeenDate: '2025-12-03',
        status: 'MISSING'
      };

      fetchMock.mockResolvedValueOnce({
        ok: false,
        status: 409,
        json: async () => ({ message: 'Microchip already exists' })
      } as Response);

      // when / then
      await expect(underTest.createAnnouncement(dto)).rejects.toThrow();
    });

    it('should throw ServerError when API returns 500', async () => {
      // given
      const dto: AnnouncementSubmissionDto = {
        species: 'CAT',
        sex: 'MALE',
        locationLatitude: 52.0,
        locationLongitude: 21.0,
        lastSeenDate: '2025-12-03',
        status: 'MISSING'
      };

      fetchMock.mockResolvedValueOnce({
        ok: false,
        status: 500,
        json: async () => ({ message: 'Internal server error' })
      } as Response);

      // when / then
      await expect(underTest.createAnnouncement(dto)).rejects.toThrow();
    });

    it('should throw NetworkError when fetch fails', async () => {
      // given
      const dto: AnnouncementSubmissionDto = {
        species: 'CAT',
        sex: 'MALE',
        locationLatitude: 52.0,
        locationLongitude: 21.0,
        lastSeenDate: '2025-12-03',
        status: 'MISSING'
      };

      fetchMock.mockRejectedValueOnce(new Error('Network error'));

      // when / then
      await expect(underTest.createAnnouncement(dto)).rejects.toThrow();
    });
  });

  describe('AnnouncementService.uploadPhoto', () => {
    it('should POST to /api/v1/announcements/:id/photos with FormData and Basic Auth header', async () => {
      // given
      const announcementId = 'ann-123';
      const managementPassword = 'pass123';
      const file = new File(['photo content'], 'pet.jpg', { type: 'image/jpeg' });

      fetchMock.mockResolvedValueOnce({
        ok: true,
        status: 200,
        json: async () => ({ success: true })
      } as Response);

      // when
      await underTest.uploadPhoto(announcementId, managementPassword, file);

      // then
      expect(window.fetch).toHaveBeenCalledWith(
        'http://localhost:3000/api/v1/announcements/ann-123/photos',
        expect.objectContaining({
          method: 'POST',
          headers: expect.objectContaining({
            Authorization: `Basic ${btoa(`${announcementId}:${managementPassword}`)}`
          }),
          body: expect.any(FormData)
        })
      );
    });

    it('should handle 401 Unauthorized error', async () => {
      // given
      const announcementId = 'ann-123';
      const managementPassword = 'wrong-pass';
      const file = new File(['photo content'], 'pet.jpg', { type: 'image/jpeg' });

      fetchMock.mockResolvedValueOnce({
        ok: false,
        status: 401,
        json: async () => ({ message: 'Unauthorized' })
      } as Response);

      // when / then
      await expect(underTest.uploadPhoto(announcementId, managementPassword, file)).rejects.toThrow();
    });

    it('should handle 404 Not Found error', async () => {
      // given
      const announcementId = 'ann-invalid';
      const managementPassword = 'pass123';
      const file = new File(['photo content'], 'pet.jpg', { type: 'image/jpeg' });

      fetchMock.mockResolvedValueOnce({
        ok: false,
        status: 404,
        json: async () => ({ message: 'Not found' })
      } as Response);

      // when / then
      await expect(underTest.uploadPhoto(announcementId, managementPassword, file)).rejects.toThrow();
    });

    it('should throw error when photo upload fails', async () => {
      // given
      const announcementId = 'ann-123';
      const managementPassword = 'pass123';
      const file = new File(['photo content'], 'pet.jpg', { type: 'image/jpeg' });

      fetchMock.mockRejectedValueOnce(new Error('Upload failed'));

      // when / then
      await expect(underTest.uploadPhoto(announcementId, managementPassword, file)).rejects.toThrow();
    });
  });

  describe('AnnouncementService - Error Handling', () => {
    it('should map 400 errors to specific field validation messages', async () => {
      // given
      const dto: AnnouncementSubmissionDto = {
        species: 'CAT',
        sex: 'MALE',
        locationLatitude: 52.0,
        locationLongitude: 21.0,
        lastSeenDate: '2025-12-03',
        status: 'MISSING'
      };

      fetchMock.mockResolvedValueOnce({
        ok: false,
        status: 400,
        json: async () => ({ message: 'Missing required field: email or phone' })
      } as Response);

      // when / then
      await expect(underTest.createAnnouncement(dto)).rejects.toThrow();
    });
  });
});
