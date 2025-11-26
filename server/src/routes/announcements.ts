import { Router } from 'express';
import type { CreateAnnouncementDto } from '../types/announcement.ts';
import { AnnouncementRepository } from '../database/repositories/announcement-repository.ts';
import { AnnouncementService } from '../services/announcement-service.ts';
import { PhotoUploadService } from '../services/photo-upload-service.ts';
import validateCreateAnnouncement from '../lib/announcement-validation.ts';
import sanitizeText from '../lib/text-sanitization.ts';
import { validateImageFormat } from '../lib/file-validation.ts';
import { db } from '../database/db-utils.ts';
import upload from '../middlewares/upload.ts';
import basicAuthMiddleware from '../middlewares/basic-auth.ts';
import announcementAuthMiddleware from '../middlewares/announcement-auth.ts';
import type { RequestWithBasicAuth } from '../middlewares/basic-auth.ts';
import { ValidationError } from '../lib/errors.ts';
import path from 'path';
import { promises as fs } from 'fs';


const router = Router();
const announcementRepository = new AnnouncementRepository(db);
const announcementService = new AnnouncementService(announcementRepository, validateCreateAnnouncement, sanitizeText);
const photoUploadService = new PhotoUploadService(
  announcementRepository,
  validateImageFormat,
  db.transaction.bind(db),
  path,
  fs
);
const imagesDir = path.join(process.cwd(), 'public', 'images');

router.get('/', async (_req, res) => {
  const announcements = await announcementService.getAllAnnouncements();
  res.json({ data: announcements });
});

router.get('/:id', async (req, res) => {
  const announcement = await announcementService.getAnnouncementById(req.params.id);
  return res.status(200).json(announcement);
});

router.post('/', async (req, res) => {
  const data: CreateAnnouncementDto = req.body;
  const announcement = await announcementService.createAnnouncement(data);
  res.status(201).json(announcement);
});

router.post(
  '/:id/photos',
  basicAuthMiddleware,
  announcementAuthMiddleware,
  upload.single('photo'),
  async (req: RequestWithBasicAuth, res) => {
    if (!req.file) {
      throw new ValidationError('MISSING_FILE', 'Photo field is required', 'photo');
    }

    const announcementId = req.params.id;
    const photoBuffer = req.file.buffer;
    await photoUploadService.uploadPhoto(announcementId, photoBuffer, imagesDir);

    res.status(201).json({});
  }
);

export default router;

