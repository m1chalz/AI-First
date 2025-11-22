interface ErrorResponse {
  error: {
    code: string;
    message: string;
  };
}

export abstract class CustomError extends Error {
  constructor(public statusCode: number, public code: string, public override message: string) {
    super(message);
  }

  toErrorResponse(): ErrorResponse {
    return {
      error: {
        code: this.code,
        message: this.message
      }
    };
  }
}

export class NotFoundError extends CustomError {
  constructor() {
    super(404, 'NOT_FOUND', 'Resource not found');
  }
}
