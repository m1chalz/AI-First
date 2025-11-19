import express from 'express'
import { runDbMigrations } from './database/db-utils.ts';
import routes from './routes/routes.ts';
import requestIdMiddleware from './middlewares/request-id-middleware.ts';
import loggerMiddleware from './middlewares/logger-middleware.ts';
import notFoundMiddleware from './middlewares/not-found-middleware.ts';
import log from './lib/logger.ts';

export default async function prepareApp(): Promise<express.Express> {
  log.info('App starting...')

  await runDbMigrations();

  const app = express();
  app.use(express.json())

  // Request ID middleware - generate unique ID and propagate via AsyncLocalStorage
  // MUST be registered BEFORE logger middleware to ensure ID is available
  app.use(requestIdMiddleware);

  // Pino HTTP logger middleware - logs all requests and responses
  app.use(loggerMiddleware);

  app.use(routes);

  app.use(notFoundMiddleware);

  return app;
}

export const app = await prepareApp();