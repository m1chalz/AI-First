import type { NextFunction, Request, Response } from 'express';
import { CustomError } from '../lib/errors.ts';
import { getRequestId } from '../lib/request-context.ts';
import type { ErrorResponse } from '../lib/errors.ts';

export default function errorHandlerMiddleware(err: Error, _req: Request, res: Response, _next: NextFunction) {
  const requestId = getRequestId() ?? 'unknown';

  if (err instanceof Error && err.message.includes('request entity too large')) {
    return res.status(413).json(payloadTooLargeErrorResponse(requestId));
  }
  
  if (err instanceof CustomError) {
    return res.status(err.statusCode).json(err.toErrorResponse(requestId));
  }
  
  return res.status(500).json(internalServerErrorResponse(requestId));
}

function internalServerErrorResponse(requestId: string): ErrorResponse {
  return {
    error: {
      requestId: requestId,
      code: 'INTERNAL_SERVER_ERROR',
      message: 'Internal server error'
    }
  }
}

function payloadTooLargeErrorResponse(requestId: string): ErrorResponse {
  return {
    error: {
      requestId: requestId,
      code: 'PAYLOAD_TOO_LARGE',
      message: 'Request payload exceeds maximum size limit (100KB)'
    }
  }
}

