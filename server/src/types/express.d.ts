/**
 * Type declarations for Express with Pino HTTP logger and Multer.
 * 
 * This extends the Express Request interface to include the `log` property
 * that is automatically attached by pino-http middleware, and the `file` property
 * from multer middleware.
 */

import { Logger } from 'pino';

interface MulterFile {
  buffer: Buffer;
  originalname: string;
  mimetype: string;
  size: number;
}

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

      /**
       * File uploaded via multer middleware with single() configuration.
       * Only present when multer middleware has processed the request.
       */
      file?: MulterFile;
    }
  }
}

