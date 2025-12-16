import type { Request, Response } from 'express';
import express from 'express';
import { userService } from '../conf/di.conf.ts';
const router = express.Router();

router.post('/', async (req: Request, res: Response) => {
  const result = await userService.registerUser(req.body);
  res.status(201).json(result);
});

router.post('/login', async (req: Request, res: Response) => {
  const result = await userService.loginUser(req.body);
  res.status(200).json(result);
});

export default router;
