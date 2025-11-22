import type { NextFunction, Request, Response } from 'express';
import { CustomError } from '../lib/errors.ts';

const INTERNAL_SERVER_ERROR_RESPONSE = {
  error: {
    code: 'INTERNAL_SERVER_ERROR',
    message: 'Internal server error'
  }
}

export default function errorHandlerMiddleware(err: Error, req: Request, res: Response, _next: NextFunction) {
  if (err instanceof CustomError) {
    req.log.info(err);
    return res.status(err.statusCode).json(err.toErrorResponse());
  }
  req.log.error(err);
  return res.status(500).json(INTERNAL_SERVER_ERROR_RESPONSE);
}
