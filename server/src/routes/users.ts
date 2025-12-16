import type { Request, Response } from 'express';
import express from 'express';
import { userService } from '../conf/di.conf.ts';
const router = express.Router();

router.post('/', async (req: Request, res: Response) => {
  const { email, password } = req.body;
  const result = await userService.registerUser(email, password);
  res.status(201).json({ id: result.id });
});

router.post('/login', async (req: Request, res: Response) => {
  const { email, password } = req.body;
  const result = await userService.loginUser(email, password);
  res.status(200).json(result);
});

export default router;
