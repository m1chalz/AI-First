import { Router } from 'express';
import { AnnouncementRepository } from '../database/repositories/announcement-repository.js';
import { AnnouncementService } from '../services/announcement-service.js';
import { db } from '../database/db-utils.js';

const router = Router();
const announcementRepository = new AnnouncementRepository(db);
const announcementService = new AnnouncementService(announcementRepository);

router.get('/', async (_req, res) => {
  const announcements = await announcementService.getAllAnnouncements();
  res.json({ data: announcements });
});

export default router;

