import type { NextFunction, Request, Response } from 'express';
import { CustomError } from '../lib/errors.ts';

const INTERNAL_SERVER_ERROR_RESPONSE = {
  error: {
    code: 'INTERNAL_SERVER_ERROR',
    message: 'Internal server error'
  }
}

const PAYLOAD_TOO_LARGE_RESPONSE = {
  error: {
    code: 'PAYLOAD_TOO_LARGE',
    message: 'Request payload exceeds maximum size limit'
  }
}

export default function errorHandlerMiddleware(err: Error, _req: Request, res: Response, _next: NextFunction) {
  if (err instanceof Error && err.message.includes('request entity too large')) {
    return res.status(413).json(PAYLOAD_TOO_LARGE_RESPONSE);
  }
  
  if (err instanceof CustomError) {
    return res.status(err.statusCode).json(err.toErrorResponse());
  }
  
  return res.status(500).json(INTERNAL_SERVER_ERROR_RESPONSE);
}

