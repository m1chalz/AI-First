import express from 'express'
import { runDbMigrations } from './database/db-utils.ts';
import routes from './routes/index.ts';
import requestIdMiddleware from './middlewares/request-id-middleware.ts';
import loggerMiddleware from './middlewares/logger-middleware.ts';

import type { Request, Response } from 'express'

export default async function prepareApp(): Promise<express.Express> {
  console.log('App starting...')

  await runDbMigrations();

  const app = express();
  app.use(express.json())

  // Request ID middleware - generate unique ID and propagate via AsyncLocalStorage
  // MUST be registered BEFORE logger middleware to ensure ID is available
  app.use(requestIdMiddleware);

  // Pino HTTP logger middleware - logs all requests and responses
  app.use(loggerMiddleware);

  app.use(routes);

  app.use((_req: Request, res: Response) => {
    res.status(404)
      .send({ message: 'Not found' });
  });

  return app;
}