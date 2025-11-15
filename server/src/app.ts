import express from 'express'
import { runDbMigrations } from './database/db-utils.ts';
import routes from './routes/index.ts';

import type { Request, Response, NextFunction } from 'express'

export default async function prepareApp(): Promise<express.Express> {
  console.log('App starting...')

  await runDbMigrations();

  const app = express();
  app.use(express.json())

  app.use((req: Request, _res: Response, next: NextFunction) => {
    console.log(`Request ${req.method} ${req.url}`)
    next()
  })

  app.use(routes);

  app.use((err: Error, _req: Request, res: Response, _next: NextFunction) => {
    console.error(err);
    res.status(500)
      .send({ message: process.env.NODE_ENV === "production" ? "Internal server error" : err.message });
  });

  app.use((_req: Request, res: Response, _next: NextFunction) => {
    res.status(404)
      .send({ message: 'Not found' });
  });

  return app;
}