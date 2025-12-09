export type ApiError = ValidationError | DuplicateMicrochipError | NetworkError | ServerError;

export interface ValidationError {
  type: 'validation';
  message: string;
  fieldName?: string;
}

export interface DuplicateMicrochipError {
  type: 'duplicate-microchip';
  message: string;
}

export interface NetworkError {
  type: 'network';
  message: string;
}

export interface ServerError {
  type: 'server';
  message: string;
  statusCode: number;
}
