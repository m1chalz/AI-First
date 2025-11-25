interface ErrorResponse {
  error: {
    code: string;
    message: string;
    field?: string | undefined;
  };
}

export abstract class CustomError extends Error {
  constructor(public statusCode: number, public code: string, public override message: string, public field?: string) {
    super(message);
  }

  toErrorResponse(): ErrorResponse {
    return {
      error: {
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

export class NotFoundError extends CustomError {
  constructor(message: string = 'Resource not found') {
    super(404, 'NOT_FOUND', message);
  }
}

export class ConflictError extends CustomError {
  constructor(message: string = 'An entity with this value already exists', field?: string) {
    super(409, 'CONFLICT', message, field);
  }
}