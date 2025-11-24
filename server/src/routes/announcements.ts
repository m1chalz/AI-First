import { Router } from 'express';
import { AnnouncementRepository } from '../database/repositories/announcement-repository.ts';
import { AnnouncementService } from '../services/announcement-service.ts';
import { db } from '../database/db-utils.ts';
import { NotFoundError } from '../lib/errors.ts';

const router = Router();
const announcementRepository = new AnnouncementRepository(db);
const announcementService = new AnnouncementService(announcementRepository);

router.get('/', async (_req, res) => {
  const announcements = await announcementService.getAllAnnouncements();
  res.json({ data: announcements });
});

router.get('/:id', async (req, res) => {
  const announcement = await announcementService.getAnnouncementById(req.params.id);
  
  if (!announcement) {
    throw new NotFoundError();
  }
  
  return res.status(200).json(announcement);
});

export default router;

