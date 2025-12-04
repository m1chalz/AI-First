import { AnnouncementRepository } from '../database/repositories/announcement-repository.ts';
import { AnnouncementService } from '../services/announcement-service.ts';
import { PhotoUploadService } from '../services/photo-upload-service.ts';
import validateCreateAnnouncement from '../lib/announcement-validation.ts';
import sanitizeText from '../lib/text-sanitization.ts';
import { validateImageFormat } from '../lib/file-validation.ts';
import { validateLocation } from '../lib/location-validation.ts';
import { db } from '../database/db-utils.ts';
import { promises as fs } from 'fs';
import path from 'path';

export const announcementRepository = new AnnouncementRepository(db);

export const photoUploadService = new PhotoUploadService(
  announcementRepository,
  validateImageFormat,
  db.transaction.bind(db),
  path,
  fs
);

export const announcementService = new AnnouncementService(
  announcementRepository,
  validateCreateAnnouncement,
  sanitizeText,
  validateLocation,
  photoUploadService
);