import { z } from 'zod';

export function mapZodErrorCode(zodCode: string, zodError: z.ZodIssue): string {
  if (zodCode === 'unrecognized_keys') {
    return 'INVALID_FIELD';
  }

  if (zodCode === 'invalid_type' && 'received' in zodError && zodError.received === 'undefined') {
    return 'MISSING_VALUE';
  }

  if (zodCode === 'too_small' && 'minimum' in zodError && zodError.minimum === 1) {
    return 'MISSING_VALUE';
  }

  if (zodCode === 'custom' && zodError.path[0] === 'contact') {
    return 'MISSING_CONTACT';
  }

  return 'INVALID_FORMAT';
}

