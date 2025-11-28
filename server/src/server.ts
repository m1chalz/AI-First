import express from 'express'
import { runDbMigrations } from './database/db-utils.ts';
import routes from './routes/routes.ts';
import requestIdMiddleware from './middlewares/request-id-middleware.ts';
import loggerMiddleware from './middlewares/logger-middleware.ts';
import notFoundMiddleware from './middlewares/not-found-middleware.ts';
import log from './conf/logger.ts';
import errorHandlerMiddleware from './middlewares/error-handler-middleware.ts';

export async function prepareServer(): Promise<express.Express> {
  log.info('App starting...');

  await runDbMigrations();

  const server = express();
  server.use(express.json({ limit: '100kb' }));

  // Request ID middleware - generate unique ID and propagate via AsyncLocalStorage
  // MUST be registered BEFORE logger middleware to ensure ID is available
  server.use(requestIdMiddleware);

  // Pino HTTP logger middleware - logs all requests and responses
  server.use(loggerMiddleware);

  // Static file serving for uploaded images
  server.use('/images', express.static('public/images'));

  server.use(routes);

  server.use(notFoundMiddleware);

  server.use(errorHandlerMiddleware);

  return server;
}

export default await prepareServer();
