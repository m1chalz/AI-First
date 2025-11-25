import { describe, it, expect, vi, beforeEach } from 'vitest';
import { AnnouncementService } from '../announcement-service.ts';
import type { Announcement, CreateAnnouncementDto } from '../../types/announcement.ts';
import type { IAnnouncementRepository } from '../../database/repositories/announcement-repository.ts';
import { ConflictError } from '../../lib/errors.ts';

const MOCK_ANNOUNCEMENT: Announcement = {
  id: '550e8400-e29b-41d4-a716-446655440000',
  petName: 'Max',
  species: 'DOG',
  breed: 'Golden Retriever',
  sex: 'MALE',
  description: 'Friendly dog',
  locationLatitude: 40.7128,
  locationLongitude: -74.0060,
  lastSeenDate: '2025-11-18',
  email: 'john@example.com',
  phone: '+1-555-0101',
  photoUrl: 'https://example.com/max.jpg',
  status: 'MISSING',
  createdAt: '2025-11-19T10:00:00Z',
  updatedAt: '2025-11-19T10:00:00Z',
};

const defaultMockRepository: IAnnouncementRepository = {
  findAll: async () => [],
  findById: async () => null,
  existsByMicrochip: async () => false,
  create: async () => MOCK_ANNOUNCEMENT,
};

const VALID_CREATE_DATA: CreateAnnouncementDto = {
  species: 'DOG',
  sex: 'MALE',
  locationLatitude: 40.7128,
  locationLongitude: -74.0060,
  photoUrl: 'https://example.com/photo.jpg',
  lastSeenDate: '2025-11-19',
  status: 'MISSING',
  email: 'test@example.com',
};

describe('AnnouncementService', () => {
  const mockValidator = vi.fn();
  const mockSanitizer = vi.fn((input: string) => input);

  const createService = (repository: IAnnouncementRepository, validator = mockValidator, sanitizer = mockSanitizer) => {
    return new AnnouncementService(repository, validator, sanitizer);
  };

  beforeEach(() => {
    vi.clearAllMocks();
    mockValidator.mockClear();
    mockSanitizer.mockImplementation((input: string) => input);
  });

  describe('getAllAnnouncements', () => {
    it.each([
      { announcements: [MOCK_ANNOUNCEMENT], expectedLength: 1 },
      { announcements: [], expectedLength: 0 },
    ])('should return $expectedLength announcements when repository returns $expectedLength items', async ({ announcements, expectedLength }) => {
      // Given: Repository returns specified announcements
      const fakeRepository = {
        ...defaultMockRepository,
        findAll: async () => announcements,
      };
      
      const service = createService(fakeRepository);
      
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
        ...defaultMockRepository,
        findById: async (_id: string) => MOCK_ANNOUNCEMENT
      };
      
      const service = createService(fakeRepository);
      
      // When: Service is called with existing ID
      const result = await service.getAnnouncementById(MOCK_ANNOUNCEMENT.id);
      
      // Then: Announcement is returned
      expect(result).toEqual(MOCK_ANNOUNCEMENT);
    });

    it('should return null when ID does not exist', async () => {
      // Given: Repository with empty data
      const fakeRepository = defaultMockRepository;
      
      const service = createService(fakeRepository);
      
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
        locationRadius: null,
      };
      
      const fakeRepository = {
        ...defaultMockRepository,
        findById: async (_id: string) => announcementWithNulls
      };
      
      const service = createService(fakeRepository);
      
      // When: Service is called
      const result = await service.getAnnouncementById(announcementWithNulls.id);
      
      // Then: Announcement with nulls is returned
      expect(result).toEqual(announcementWithNulls);
      expect(result?.breed).toBeNull();
      expect(result?.email).toBeNull();
      expect(result?.locationRadius).toBeNull();
    });
  });

  describe('createAnnouncement', () => {
    it('should create announcement successfully with valid data', async () => {
      // Given
      const createdAnnouncement: Announcement = {
        id: 'new-id-123',
        species: 'DOG',
        sex: 'MALE',
        locationLatitude: 40.7128,
        locationLongitude: -74.0060,
        photoUrl: 'https://example.com/photo.jpg',
        lastSeenDate: '2025-11-19',
        status: 'MISSING',
        email: 'test@example.com',
        createdAt: '2025-11-19T10:00:00Z',
        updatedAt: '2025-11-19T10:00:00Z',
      };

      const fakeRepository = {
        ...defaultMockRepository,
        existsByMicrochip: async () => false,
        create: vi.fn(async () => createdAnnouncement),
      };

      const service = createService(fakeRepository);

      // When
      const result = await service.createAnnouncement(VALID_CREATE_DATA);

      // Then
      expect(result).toMatchObject({
        ...createdAnnouncement,
        managementPassword: expect.stringMatching(/^\d{6}$/),
      });
      expect(fakeRepository.create).toHaveBeenCalledWith(
        expect.objectContaining({
          species: 'DOG',
          sex: 'MALE',
        }),
        expect.stringMatching(/^\d{6}$/)
      );
    });

    it('should sanitize text fields before creating', async () => {
      // Given: Announcement data with potentially unsafe text
      const dataWithUnsafeText: CreateAnnouncementDto = {
        ...VALID_CREATE_DATA,
        petName: '<script>alert("xss")</script>Max',
        species: '<b>DOG</b>',
        breed: '<img src=x onerror=alert(1)>',
        sex: 'MALE',
        description: '<script>malicious</script>',
        locationCity: '<div>City</div>',
        reward: '<p>Reward</p>',
      };

      const createdAnnouncement: Announcement = {
        id: 'new-id-123',
        species: 'DOG',
        sex: 'MALE',
        locationLatitude: 40.7128,
        locationLongitude: -74.0060,
        photoUrl: 'https://example.com/photo.jpg',
        lastSeenDate: '2025-11-19',
        status: 'MISSING',
        email: 'test@example.com',
        createdAt: '2025-11-19T10:00:00Z',
        updatedAt: '2025-11-19T10:00:00Z',
      };

      const testSanitizer = vi.fn((input: string) => input);
      const fakeRepository = {
        ...defaultMockRepository,
        existsByMicrochip: async () => false,
        create: vi.fn(async () => createdAnnouncement),
      };

      const service = createService(fakeRepository, mockValidator, testSanitizer);

      // When: Service creates announcement
      await service.createAnnouncement(dataWithUnsafeText);

      // Then: Sanitization function is called for all text fields
      expect(testSanitizer).toHaveBeenCalledWith(dataWithUnsafeText.petName);
      expect(testSanitizer).toHaveBeenCalledWith(dataWithUnsafeText.species);
      expect(testSanitizer).toHaveBeenCalledWith(dataWithUnsafeText.breed);
      expect(testSanitizer).toHaveBeenCalledWith(dataWithUnsafeText.sex);
      expect(testSanitizer).toHaveBeenCalledWith(dataWithUnsafeText.description);
      expect(testSanitizer).toHaveBeenCalledWith(dataWithUnsafeText.locationCity);
      expect(testSanitizer).toHaveBeenCalledWith(dataWithUnsafeText.reward);
      expect(testSanitizer).toHaveBeenCalledTimes(7);
    });

    it('should generate unique management password for each announcement', async () => {
      // Given: Repository that creates announcements
      const createdAnnouncement: Announcement = {
        id: 'new-id-123',
        species: 'DOG',
        sex: 'MALE',
        locationLatitude: 40.7128,
        locationLongitude: -74.0060,
        photoUrl: 'https://example.com/photo.jpg',
        lastSeenDate: '2025-11-19',
        status: 'MISSING',
        email: 'test@example.com',
        createdAt: '2025-11-19T10:00:00Z',
        updatedAt: '2025-11-19T10:00:00Z',
      };

      const fakeRepository = {
        ...defaultMockRepository,
        existsByMicrochip: async () => false,
        create: vi.fn(async () => createdAnnouncement),
      };

      const service = createService(fakeRepository);

      // When: Service creates multiple announcements
      const result1 = await service.createAnnouncement(VALID_CREATE_DATA);
      const result2 = await service.createAnnouncement(VALID_CREATE_DATA);

      // Then: Each announcement has a unique management password
      expect(result1.managementPassword).toBeDefined();
      expect(result2.managementPassword).toBeDefined();
      expect(result1.managementPassword).toMatch(/^\d{6}$/);
      expect(result2.managementPassword).toMatch(/^\d{6}$/);
      // Passwords should be different (very unlikely to be the same)
      expect(result1.managementPassword).not.toBe(result2.managementPassword);
    });

    it('should check for duplicate microchip number before creating', async () => {
      // Given: Announcement data with microchip number that already exists
      const dataWithMicrochip: CreateAnnouncementDto = {
        ...VALID_CREATE_DATA,
        microchipNumber: '123456789',
      };

      const fakeRepository = {
        ...defaultMockRepository,
        existsByMicrochip: vi.fn(async () => true),
        create: vi.fn(),
      };

      const service = createService(fakeRepository);

      // When: Service creates announcement with duplicate microchip
      // Then: ConflictError is thrown
      await expect(service.createAnnouncement(dataWithMicrochip)).rejects.toThrow(ConflictError);
      await expect(service.createAnnouncement(dataWithMicrochip)).rejects.toThrow('An entity with this value already exists');
      
      expect(fakeRepository.existsByMicrochip).toHaveBeenCalledWith('123456789');
      expect(fakeRepository.create).not.toHaveBeenCalled();
    });

    it('should not check microchip when microchipNumber is not provided', async () => {
      // Given: Announcement data without microchip number
      const createdAnnouncement: Announcement = {
        id: 'new-id-123',
        species: 'DOG',
        sex: 'MALE',
        locationLatitude: 40.7128,
        locationLongitude: -74.0060,
        photoUrl: 'https://example.com/photo.jpg',
        lastSeenDate: '2025-11-19',
        status: 'MISSING',
        email: 'test@example.com',
        createdAt: '2025-11-19T10:00:00Z',
        updatedAt: '2025-11-19T10:00:00Z',
      };

      const fakeRepository = {
        ...defaultMockRepository,
        existsByMicrochip: vi.fn(),
        create: vi.fn(async () => createdAnnouncement),
      };

      const service = createService(fakeRepository);

      // When: Service creates announcement without microchip
      await service.createAnnouncement(VALID_CREATE_DATA);

      // Then: Microchip check is not called
      expect(fakeRepository.existsByMicrochip).not.toHaveBeenCalled();
      expect(fakeRepository.create).toHaveBeenCalled();
    });

    it('should call validator function when creating announcement', async () => {
      // Given: Announcement data and mock validator function
      const mockValidator = vi.fn();
      const mockSanitizer = vi.fn((input: string) => input);

      const fakeRepository = {
        ...defaultMockRepository,
        existsByMicrochip: async () => false,
        create: vi.fn(async (): Promise<Announcement> => ({
          id: 'new-id-123',
          species: 'DOG',
          sex: 'MALE',
          locationLatitude: 40.7128,
          locationLongitude: -74.0060,
          photoUrl: 'https://example.com/photo.jpg',
          lastSeenDate: '2025-11-19',
          status: 'MISSING',
          email: 'test@example.com',
          createdAt: '2025-11-19T10:00:00Z',
          updatedAt: '2025-11-19T10:00:00Z',
        })),
      };

      const service = createService(fakeRepository, mockValidator, mockSanitizer);

      // When: Service creates announcement
      await service.createAnnouncement(VALID_CREATE_DATA);

      // Then: Validator function is called with the data
      expect(mockValidator).toHaveBeenCalledWith(VALID_CREATE_DATA);
      expect(mockValidator).toHaveBeenCalledTimes(1);
    });

    it('should handle optional fields correctly', async () => {
      // Given: Announcement data with all optional fields
      const dataWithAllFields: CreateAnnouncementDto = {
        ...VALID_CREATE_DATA,
        petName: 'Max',
        breed: 'Golden Retriever',
        age: 3,
        description: 'Friendly dog',
        microchipNumber: '123456789',
        locationCity: 'New York',
        locationRadius: 5,
        phone: '+1-555-0101',
        reward: '$100',
      };

      const createdAnnouncement: Announcement = {
        id: 'new-id-123',
        petName: 'Max',
        species: 'DOG',
        breed: 'Golden Retriever',
        sex: 'MALE',
        age: 3,
        description: 'Friendly dog',
        microchipNumber: '123456789',
        locationCity: 'New York',
        locationLatitude: 40.7128,
        locationLongitude: -74.0060,
        locationRadius: 5,
        photoUrl: 'https://example.com/photo.jpg',
        lastSeenDate: '2025-11-19',
        status: 'MISSING',
        email: 'test@example.com',
        phone: '+1-555-0101',
        reward: '$100',
        createdAt: '2025-11-19T10:00:00Z',
        updatedAt: '2025-11-19T10:00:00Z',
      };

      const fakeRepository = {
        ...defaultMockRepository,
        existsByMicrochip: async () => false,
        create: vi.fn(async () => createdAnnouncement),
      };

      const service = createService(fakeRepository);

      // When: Service creates announcement with all fields
      const result = await service.createAnnouncement(dataWithAllFields);

      // Then: All fields are included in the result
      expect(result).toMatchObject({
        ...createdAnnouncement,
        managementPassword: expect.stringMatching(/^\d{6}$/),
      });
      expect(fakeRepository.create).toHaveBeenCalled();
    });
  });
});
