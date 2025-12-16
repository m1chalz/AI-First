import { z } from 'zod';
import { ValidationError } from './errors.ts';
import { mapZodErrorCode } from './zod-errors.ts';

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const CreateUserSchema = z
  .object({
    email: z
      .string()
      .trim()
      .min(3, { message: 'email must be 3-254 characters long' })
      .max(254, { message: 'email must be 3-254 characters long' })
      .regex(EMAIL_REGEX, { message: 'email format is invalid' }),
    password: z
      .string()
      .trim()
      .min(8, { message: 'password must be 8-128 characters long' })
      .max(128, { message: 'password must be 8-128 characters long' })
  })
  .strict();

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
