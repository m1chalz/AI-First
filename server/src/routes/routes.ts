import express from 'express'
import announcementsRouter from './announcements.ts';

const router = express.Router();

router.get('/api/health', (_req, res) => {
  res.json({ status: 'ok', timestamp: new Date().toISOString() });
});

router.use('/api/v1/announcements', announcementsRouter);
router.use('/api/admin/v1/announcements', announcementsRouter);

export default router;

