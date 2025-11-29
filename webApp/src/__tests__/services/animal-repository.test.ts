import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { AnimalRepository } from '../../services/animal-repository';
import type { Animal } from '../../types/animal';

describe('AnimalRepository', () => {

  const underTest: AnimalRepository = new AnimalRepository();
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

  describe('AnimalRepository.getAnimals', () => {

    it('should fetch animals list successfully when API returns 200', async () => {
      // Given: API returns successful response with animals array
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

      // When: getAnimals is called
      const result = await underTest.getAnimals();

      // Then: Should return animals array
      expect(result).toEqual(mockAnimals);
      expect(window.fetch).toHaveBeenCalledWith('http://localhost:3000/api/v1/announcements');
    });

    it('should return empty array when API returns empty list', async () => {
      // Given: API returns empty array
      fetchMock.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ data: [] })
      } as Response);

      // When: getAnimals is called
      const result = await underTest.getAnimals();

      // Then: Should return empty array
      expect(result).toEqual([]);
      expect(window.fetch).toHaveBeenCalledWith('http://localhost:3000/api/v1/announcements');
    });

    it('should throw error when API returns 500', async () => {
      // Given: API returns 500 Internal Server Error
      fetchMock.mockResolvedValueOnce({
        ok: false,
        status: 500,
        statusText: 'Internal Server Error'
      } as Response);

      // When: getAnimals is called
      // Then: Should throw error with appropriate message
      await expect(underTest.getAnimals()).rejects.toThrow('Failed to fetch animals: 500 Internal Server Error');
    });

    it('should handle network errors', async () => {
      // Given: Network request fails
      fetchMock.mockRejectedValueOnce(new Error('Network error'));

      // When: getAnimals is called
      // Then: Should propagate network error
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

      // then: URL contains exact values
      expect(result).toEqual(mockAnimals);
      expect(window.fetch).toHaveBeenCalledWith(`http://localhost:3000/api/v1/announcements?lat=${lat}&lng=${lng}`);
    });
  });

  describe('AnimalRepository.getPetById', () => {

    it('should fetch pet details successfully when API returns 200', async () => {
      // Given: API returns successful response with pet data
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

      // When: getPetById is called
      const result = await underTest.getPetById('pet-123');

      // Then: Should return pet data
      expect(result).toEqual(mockPet);
      expect(window.fetch).toHaveBeenCalledWith('http://localhost:3000/api/v1/announcements/pet-123');
    });

    it('should throw error when API returns 404', async () => {
      // Given: API returns 404 Not Found
      fetchMock.mockResolvedValueOnce({
        ok: false,
        status: 404,
        statusText: 'Not Found'
      } as Response);

      // When: getPetById is called
      // Then: Should throw error with appropriate message
      await expect(underTest.getPetById('pet-123')).rejects.toThrow('Pet with ID pet-123 not found');
    });

    it('should throw error when API returns 500', async () => {
      // Given: API returns 500 Internal Server Error
      fetchMock.mockResolvedValueOnce({
        ok: false,
        status: 500,
        statusText: 'Internal Server Error'
      } as Response);

      // When: getPetById is called
      // Then: Should throw error with appropriate message
      await expect(underTest.getPetById('pet-123')).rejects.toThrow('Failed to fetch pet details: 500 Internal Server Error');
    });

    it('should handle network errors', async () => {
      // Given: Network request fails
      fetchMock.mockRejectedValueOnce(new Error('Network error'));

      // When: getPetById is called
      // Then: Should propagate network error
      await expect(underTest.getPetById('pet-123')).rejects.toThrow('Network error');
    });
  });
});

