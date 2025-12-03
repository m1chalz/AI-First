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

export function isValidationError(error: ApiError): error is ValidationError {
  return error.type === 'validation';
}

export function isDuplicateMicrochipError(error: ApiError): error is DuplicateMicrochipError {
  return error.type === 'duplicate-microchip';
}

export function isNetworkError(error: ApiError): error is NetworkError {
  return error.type === 'network';
}

export function isServerError(error: ApiError): error is ServerError {
  return error.type === 'server';
}

