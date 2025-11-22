import { pinoHttp, stdSerializers } from 'pino-http';
import { serializeBody } from '../lib/log-serializers.ts';
import log from '../lib/logger.ts';

/**
 * Pino HTTP logger middleware with comprehensive request/response logging.
 * Includes body truncation, binary omission, header redaction, and request ID correlation.
 */
/* eslint-disable @typescript-eslint/no-explicit-any */
export default pinoHttp({
  logger: log,

  serializers: {
    req(req: any) {
      const serialized: any = stdSerializers.req(req);

      if (req.raw?.body) {
        const contentType = req.headers['content-type'] as string | undefined;
        serialized.body = serializeBody(req.raw.body, contentType, req.headers);
      }

      return serialized;
    },

    res(res: any) {
      const serialized: any = stdSerializers.res(res);

      // Explicitly capture statusCode (standard serializer may miss it)
      if (res.statusCode !== undefined) {
        serialized.statusCode = res.statusCode;
      }

      if (res.raw?.body) {
        const contentType = (res.headers?.['content-type'] || res.raw.headers?.['content-type']) as string | undefined;
        const headers = res.headers || res.raw.headers || {};
        serialized.body = serializeBody(res.raw.body, contentType, headers);
      }

      return serialized;
    },
  },

  redact: {
    paths: ['req.headers.authorization', 'res.headers.authorization'],
    censor: '***',
  },

  customAttributeKeys: {
    responseTime: 'responseTime',
  },

  autoLogging: true,

  customReceivedMessage: () => {
    return 'Request received';
  },

  customSuccessMessage: () => {
    return 'Request completed';
  },

  customErrorMessage: (_req: any, _res: any, err: any) => {
    return `Request failed: ${err.message}`;
  },

  customLogLevel: (_req: any, res: any) => {
    if (res.statusCode >= 500) {
      return 'error';
    }
    if (res.statusCode >= 400) {
      return 'warn';
    }
    return 'info';
  },
});
/* eslint-enable @typescript-eslint/no-explicit-any */
