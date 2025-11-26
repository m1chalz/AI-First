import type { promises as fs } from 'fs';
import type { IAnnouncementRepository } from '../database/repositories/announcement-repository.ts';
import type { TransactionalWrapper } from '../database/db-utils.ts';
import { NotFoundError, PayloadTooLargeError, ValidationError } from '../lib/errors.ts';

const MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB

export class PhotoUploadService {
  constructor(
    private repository: IAnnouncementRepository,
    private validateFormat: (buffer: Buffer) => Promise<string | null>,
    private withTransaction: TransactionalWrapper,
    private path: typeof import('path'),
    private fileSystem: typeof fs
  ) { }

  async uploadPhoto(
    announcementId: string,
    photoBuffer: Buffer,
    uploadPath: string
  ): Promise<string> {
    const announcement = await this.repository.findById(announcementId);
    if (!announcement) {
      throw new NotFoundError(`Announcement with ID ${announcementId} not found`);
    }

    if (photoBuffer.length > MAX_FILE_SIZE) {
      throw new PayloadTooLargeError('File is not a valid image format (JPEG, PNG, GIF, WebP, BMP, TIFF, HEIC, HEIF)', 'file');
    }

    const mimeType = await this.validateFormat(photoBuffer);
    if (!mimeType) {
      throw new ValidationError(
        'PAYLOAD_TOO_LARGE',
        `File size exceeds maximum limit of ${MAX_FILE_SIZE / (1024 * 1024)}MB`,
        'file'
      );
    }

    // Determine file extension and path
    const ext = mimeType.split('/')[1];
    const filename = `${announcementId}.${ext}`;
    const filePath = this.path.join(uploadPath, filename);
    const relativePhotoUrl = `/images/${filename}`;

    await this.withTransaction(async (trx) => {
      await this.repository.updatePhotoUrl(trx, announcementId, relativePhotoUrl);
      try {
        await this.fileSystem.writeFile(filePath, photoBuffer);
      } catch (error) {
        throw new Error(`Failed to save photo file: ${error instanceof Error ? error.message : 'unknown error'}`);
      }
    });

    return relativePhotoUrl;
  }

}
