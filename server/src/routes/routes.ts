import express from 'express'
import announcementsRouter from './announcements.ts';

const router = express.Router();

router.use('/api/v1/announcements', announcementsRouter);

export default router;

