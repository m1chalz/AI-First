import express, { type Request, type Response } from 'express'

const router = express.Router();

router.get('/pets', async (req: Request, res: Response) => {
  const output = [
    { name: 'Fafik' },
    { name: 'Reksio' },
    { name: 'Burek' },
  ]
  req.log.info(`Output size: ${output.length}`)
  res.json(output)
})

export default router;