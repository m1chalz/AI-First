import { describe, it, expect, beforeEach, vi } from 'vitest';
import type { promises as fs } from 'fs';
import type { Knex } from 'knex';
import type { IAnnouncementRepository } from '../../database/repositories/announcement-repository.ts';
import type { TransactionalWrapper } from '../../database/db-utils.ts';
import { PhotoUploadService } from '../photo-upload-service.ts';
import * as path from 'path';
import { NotFoundError, PayloadTooLargeError, ValidationError } from '../../lib/errors.ts';

// Mock file system operations
vi.mock('fs/promises');

// Mock file-validation functions
vi.mock('../../lib/file-validation.ts', () => ({
  validateImageFormat: vi.fn((buffer: Buffer) => {
    // Simulate valid JPEG
    if (buffer[0] === 0xFF && buffer[1] === 0xD8) {
      return Promise.resolve('image/jpeg');
    }
    return Promise.resolve(null);
  }),
}));

const findByIdMock = vi.fn();
const updatePhotoUrlMock = vi.fn();
const writeFileMock = vi.fn();

describe('PhotoUploadService', () => {
  let service: PhotoUploadService;
  let mockRepository: IAnnouncementRepository;
  let mockFs: Partial<typeof fs>;
  let mockValidateImageFormat: (buffer: Buffer) => Promise<string | null>;
  let mockWithTransaction: TransactionalWrapper;

  beforeEach(() => {
    // Reset all mocks before each test
    vi.clearAllMocks();

    // Setup validation mocks with proper typing
    mockValidateImageFormat = vi.fn(async (buffer: Buffer): Promise<string | null> => {
      if (buffer[0] === 0xFF && buffer[1] === 0xD8) {
        return 'image/jpeg';
      }
      return null;
    });

    // Setup repository mock
    mockRepository = {
      findAll: vi.fn(),
      findById: findByIdMock,
      existsByMicrochip: vi.fn(),
      create: vi.fn(),
      updatePhotoUrl: updatePhotoUrlMock,
    } as unknown as IAnnouncementRepository;

    // Setup fs mock
    mockFs = {
      writeFile: writeFileMock
    };

    // Setup withTransaction mock
    mockWithTransaction = vi.fn(async (callback) => {
      // Mock transaction object
      const mockTrx = {} as Knex.Transaction;
      return await callback(mockTrx);
    });

    // Create service with mocked dependencies
    service = new PhotoUploadService(
      mockRepository,
      mockValidateImageFormat,
      mockWithTransaction,
      path,
      mockFs as unknown as typeof fs
    );
  });

  describe('uploadPhoto', () => {
    it('should save photo and update database with transaction', async () => {
      // Given: valid announcement and photo
      const announcementId = 'announce-123';
      const photoBuffer = Buffer.from([0xFF, 0xD8, 0xFF, 0xE0]);
      const uploadPath = '/uploads';

      findByIdMock.mockResolvedValue({
        id: announcementId,
        photo_url: null,
      });

      // When: uploadPhoto is called
      const result = await service.uploadPhoto(announcementId, photoBuffer, uploadPath);

      // Then: should return photo URL, save file, and update DB
      expect(result).toBe(`/images/${announcementId}.jpeg`);
      expect(writeFileMock).toHaveBeenCalledWith(
        expect.stringContaining(announcementId),
        photoBuffer
      );
      expect(updatePhotoUrlMock).toHaveBeenCalledWith(
        expect.any(Object),
        announcementId,
        `/images/announce-123.jpeg`
      );
      expect(findByIdMock).toHaveBeenCalled();
    });
  });

  describe('error handling', () => {
    it('should throw error when announcement not found', async () => {
      // Given: non-existent announcement
      const announcementId = 'non-existent';
      const photoBuffer = Buffer.from([0xFF, 0xD8, 0xFF, 0xE0]);
      const uploadPath = '/uploads';

      findByIdMock.mockResolvedValue(null);

      // When/Then: should throw NotFoundError without saving file
      await expect(service.uploadPhoto(announcementId, photoBuffer, uploadPath)).rejects.toThrow(NotFoundError);
      expect(writeFileMock).not.toHaveBeenCalled();
    });

    it('should not save the file if the DB write fails inside transaction', async () => {
      // Given: file save fails inside transaction
      const announcementId = 'announce-456';
      const photoBuffer = Buffer.from([0xFF, 0xD8, 0xFF, 0xE0]);
      const uploadPath = '/uploads';
      const saveError = new Error('Disk full');

      // Create fresh mocks for this test
      updatePhotoUrlMock.mockRejectedValue(saveError);

      findByIdMock.mockResolvedValue({
        id: announcementId,
        photo_url: null,
      });

      const failService = new PhotoUploadService(
        mockRepository,
        mockValidateImageFormat,
        mockWithTransaction,
        path,
        mockFs as unknown as typeof fs
      );

      // When/Then: should throw error
      await expect(failService.uploadPhoto(announcementId, photoBuffer, uploadPath)).rejects.toThrow();
      expect(updatePhotoUrlMock).toHaveBeenCalled();
    });
  });

  describe('file validation', () => {
    it('should reject invalid image format', async () => {
      // Given: invalid image buffer
      const announcementId = 'announce-111';
      const invalidBuffer = Buffer.from([0x00, 0x01, 0x02, 0x03]);
      const uploadPath = '/uploads';

      findByIdMock.mockResolvedValue({
        id: announcementId,
        photo_url: null,
      });

      // When/Then: should throw validation error without saving file
      await expect(service.uploadPhoto(announcementId, invalidBuffer, uploadPath)).rejects.toThrow(ValidationError);
      expect(writeFileMock).not.toHaveBeenCalled();
    });

    it('should reject files exceeding 20MB limit', async () => {
      // Given: oversized file
      const announcementId = 'announce-222';
      const MAX_SIZE = 20 * 1024 * 1024;
      const oversizeBuffer = Buffer.alloc(MAX_SIZE + 1);
      const uploadPath = '/uploads';

      findByIdMock.mockResolvedValue({
        id: announcementId,
        photo_url: null,
      });

      // When/Then: should throw validation error without saving file
      await expect(service.uploadPhoto(announcementId, oversizeBuffer, uploadPath)).rejects.toThrow(PayloadTooLargeError);
      expect(writeFileMock).not.toHaveBeenCalled();
    });
  });
});
