import path from 'path';
import { Router } from 'express';
import type { CreateAnnouncementDto } from '../types/announcement.ts';
import upload from '../middlewares/upload-middleware.ts';
import basicAuthMiddleware from '../middlewares/basic-auth.ts';
import announcementAuthMiddleware from '../middlewares/announcement-auth.ts';
import adminAuthMiddleware from '../middlewares/admin-auth.ts';
import type { RequestWithBasicAuth } from '../middlewares/basic-auth.ts';
import { ValidationError } from '../lib/errors.ts';
import { announcementService, photoUploadService } from '../conf/di.conf.ts';

const router = Router();

const imagesDir = path.join(process.cwd(), 'public', 'images');

router.get('/', async (req, res) => {
  const lat = req.query.lat ? parseFloat(req.query.lat as string) : undefined;
  const lng = req.query.lng ? parseFloat(req.query.lng as string) : undefined;
  const range = req.query.range ? parseFloat(req.query.range as string) : undefined;

  const announcements = await announcementService.getAllAnnouncements(lat, lng, range);
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

router.delete('/:id', adminAuthMiddleware, async (req, res) => {
  const announcementId = req.params.id;
  await announcementService.deleteAnnouncement(announcementId);
  res.status(204).send();
});

export default router;
