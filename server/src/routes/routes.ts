import express from 'express';
import adminRouter from './admin.ts';
import announcementsRouter from './announcements.ts';
import usersRouter from './users.ts';

const router = express.Router();

router.get('/api/health', (_req, res) => res.json({ status: 'ok', timestamp: new Date().toISOString() }));

router.use('/api/admin/v1', adminRouter);

router.use('/api/v1/announcements', announcementsRouter);
router.use('/api/v1/users', usersRouter);

export default router;
