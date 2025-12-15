import { z } from 'zod';
import { ValidationError } from './errors.ts';
import { isValidEmail, isValidPassword } from './validators.ts';
import { mapZodErrorCode } from './zod-errors.ts';

const CreateUserSchema = z
  .object({
    email: z
      .string()
      .min(1, { message: 'email is required' })
      .trim()
      .refine(isValidEmail, {
        message: 'email format is invalid'
      }),
    password: z
      .string()
      .min(1, { message: 'password is required' })
      .refine(isValidPassword, {
        message: 'password must be 8-128 characters long'
      })
  })
  .strict();

export interface CreateUserRequest {
  email: string;
  password: string;
}

export default function validateCreateUser(data: unknown): void {
  try {
    CreateUserSchema.parse(data);
  } catch (error) {
    if (error instanceof z.ZodError) {
      const firstError = error.issues[0];

      // Handle unrecognized keys (unknown fields)
      if (firstError.code === 'unrecognized_keys' && 'keys' in firstError) {
        const unknownField = firstError.keys[0];
        throw new ValidationError('INVALID_FIELD', `${unknownField} is not a valid field`, unknownField);
      }

      const field = firstError.path.length > 0 ? firstError.path[0].toString() : 'email';
      const code = mapZodErrorCode(firstError.code, firstError);

      throw new ValidationError(code, firstError.message, field);
    }
    throw error;
  }
}
