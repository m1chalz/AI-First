import express, { type Request, type Response } from 'express'

const router = express.Router();

router.get('/pets', async (_req: Request, res: Response) => {
  res.json([
    { name: 'Fafik' },
    { name: 'Reksio' },
    { name: 'Burek' },
  ])
})

export default router;