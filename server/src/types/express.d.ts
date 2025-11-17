/**
 * Type declarations for Express with Pino HTTP logger.
 * 
 * This extends the Express Request interface to include the `log` property
 * that is automatically attached by pino-http middleware.
 */

import { Logger } from 'pino';

declare global {
  namespace Express {
    interface Request {
      /**
       * Pino logger instance attached by pino-http middleware.
       * 
       * This logger automatically includes the request ID and other context
       * in all log entries, enabling correlation across the request lifecycle.
       * 
       * @example
       * req.log.info({ userId: user.id }, 'user authenticated');
       * req.log.error({ err }, 'database query failed');
       */
      log: Logger;
    }
  }
}

