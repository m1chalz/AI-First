export interface ErrorResponse {
  error: {
    requestId: string;
    code: string;
    message: string;
    field?: string | undefined;
  };
}

export abstract class CustomError extends Error {
  constructor(public statusCode: number, public code: string, public override message: string, public field?: string) {
    super(message);
  }

  toErrorResponse(requestId: string): ErrorResponse {
    return {
      error: {
        requestId: requestId,
        code: this.code,
        message: this.message,
        field: this.field
      }
    };
  }
}

export class ValidationError extends CustomError {
  constructor(code: string, message: string, field?: string) {
    super(400, code, message, field);
  }
}

export class UnauthenticatedError extends CustomError {
  constructor(message = 'Authorization header is required') {
    super(401, 'UNAUTHENTICATED', message);
  }
}

export class UnauthorizedError extends CustomError {
  constructor(message = 'Invalid credentials') {
    super(403, 'UNAUTHORIZED', message);
  }
}

export class NotFoundError extends CustomError {
  constructor(message = 'Resource not found') {
    super(404, 'NOT_FOUND', message);
  }
}

export class ConflictError extends CustomError {
  constructor(message = 'An entity with this value already exists', field?: string) {
    super(409, 'CONFLICT', message, field);
  }
}