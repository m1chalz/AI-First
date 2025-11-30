import { Router } from 'express';
import type { CreateAnnouncementDto } from '../types/announcement.ts';
import upload from '../middlewares/upload-middleware.ts';
import basicAuthMiddleware from '../middlewares/basic-auth.ts';
import announcementAuthMiddleware from '../middlewares/announcement-auth.ts';
import type { RequestWithBasicAuth } from '../middlewares/basic-auth.ts';
import { ValidationError } from '../lib/errors.ts';
import path from 'path';
import { announcementService, photoUploadService } from '../conf/di.conf.ts';
import { validateLocation } from '../lib/location-validation.ts';


const router = Router();

const imagesDir = path.join(process.cwd(), 'public', 'images');

router.get('/', async (req, res) => {
  const lat = req.query.lat ? parseFloat(req.query.lat as string) : undefined;
  const lng = req.query.lng ? parseFloat(req.query.lng as string) : undefined;
  const range = req.query.range ? parseFloat(req.query.range as string) : undefined;

  const locationFilter = validateLocation(lat, lng, range);

  const announcements = await announcementService.getAllAnnouncements(locationFilter);
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

