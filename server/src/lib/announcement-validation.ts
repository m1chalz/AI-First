import { z } from 'zod';
import type { CreateAnnouncementDto } from '../types/announcement.ts';
import { ValidationError } from './errors.ts';
import { mapZodErrorCode } from './zod-errors.ts';

const EMAIL_REGEX = /^(?=.{1,254}$)[^\s@]+@[^\s@]+\.[^\s@]+$/;

function isNotFutureDate(dateString: string): boolean {
  const dateObj = new Date(dateString);
  const today = new Date();
  return dateObj <= today;
}

const CreateAnnouncementSchema = z
  .object({
    petName: z.string().trim().optional(),
    species: z.string().trim().min(1, { message: 'cannot be empty' }),
    breed: z.string().trim().optional(),
    sex: z.string().trim().min(1, { message: 'cannot be empty' }),
    age: z.number().int().positive({ message: 'age must be a positive integer' }).optional(),
    description: z.string().trim().optional(),
    microchipNumber: z.string().trim().regex(/^\d+$/, { message: 'must contain only digits' }).optional(),
    locationLatitude: z
      .number()
      .min(-90, { message: 'latitude must be between -90 and 90' })
      .max(90, { message: 'latitude must be between -90 and 90' }),
    locationLongitude: z
      .number()
      .min(-180, { message: 'longitude must be between -180 and 180' })
      .max(180, { message: 'longitude must be between -180 and 180' }),
    email: z
      .string()
      .trim()
      .regex(EMAIL_REGEX, { message: 'email format is invalid' })
      .optional(),
    phone: z
      .string()
      .trim()
      .regex(/\d/, { message: 'invalid phone format' })
      .optional(),
    lastSeenDate: z
      .string()
      .trim()
      .regex(/^\d{4}-\d{2}-\d{2}$/, { message: 'invalid date format (expected YYYY-MM-DD)' })
      .refine(isNotFutureDate, {
        message: 'lastSeenDate cannot be in the future'
      }),
    status: z.enum(['MISSING', 'FOUND'], {
      errorMap: () => ({ message: 'status must be either MISSING or FOUND' })
    }),
    reward: z.string().trim().optional()
  })
  .strict()
  .refine((data) => data.email || data.phone, {
    message: 'at least one contact method (email or phone) is required',
    path: ['contact']
  });


export default function validateCreateAnnouncement(data: CreateAnnouncementDto): void {
  try {
    CreateAnnouncementSchema.parse(data);
  } catch (error) {
    if (error instanceof z.ZodError) {
      const firstError = error.issues[0];

      // Handle unrecognized keys (unknown fields)
      if (firstError.code === 'unrecognized_keys' && 'keys' in firstError) {
        const unknownField = firstError.keys[0];
        throw new ValidationError('INVALID_FIELD', `${unknownField} is not a valid field`, unknownField);
      }

      const field = firstError.path.length > 0 ? firstError.path[0].toString() : undefined;
      const errorCode = mapZodErrorCode(firstError.code, firstError);

      throw new ValidationError(errorCode, firstError.message, field);
    }
    throw error;
  }
}
