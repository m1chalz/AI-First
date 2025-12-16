import { Router } from 'express';
import adminAuthMiddleware from '../middlewares/admin-auth.ts';
import { announcementService } from '../conf/di.conf.ts';

const router = Router();

router.delete('/announcements/:id', adminAuthMiddleware, async (req, res) => {
  const announcementId = req.params.id;
  await announcementService.deleteAnnouncement(announcementId);
  res.status(204).send();
});

export default router;
