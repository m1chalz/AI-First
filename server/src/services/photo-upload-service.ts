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
      throw new PayloadTooLargeError(`File size exceeds maximum limit of ${MAX_FILE_SIZE / (1024 * 1024)}MB`, 'file');
    }

    const mimeType = await this.validateFormat(photoBuffer);
    if (!mimeType) {
      throw new ValidationError(
        'INVALID_FILE_FORMAT',
        'File is not a valid image format (JPEG, PNG, GIF, WebP, BMP, TIFF, HEIC, HEIF)',
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

  /**
   * Deletes photo file associated with an announcement using its photoUrl.
   * Converts relative photoUrl to absolute file path and removes the file if it exists.
   */
  async deletePhotos(photoUrl: string | null): Promise<void> {
    if (!photoUrl) {
      return;
    }

    // Convert relative photoUrl (e.g., "/images/announcement-id.jpeg") to absolute file path
    // Remove leading slash and join with project public directory
    const relativePath = photoUrl.startsWith('/') ? photoUrl.slice(1) : photoUrl;
    const filePath = this.path.join(process.cwd(), 'public', relativePath);

    try {
      await this.fileSystem.unlink(filePath);
    } catch {
      // File doesn't exist or cannot be deleted, silently ignore
    }
  }

}
