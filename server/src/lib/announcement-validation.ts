import { z } from 'zod';
import { ValidationError } from './errors.ts';
import { CreateAnnouncementDto } from '../types/announcement.js';

function isValidHttpUrl(urlString: string): boolean {
  try {
    const url = new URL(urlString);
    return url.protocol === 'http:' || url.protocol === 'https:';
  } catch {
    return false;
  }
}

const CreateAnnouncementSchema = z
  .object({
    petName: z.string().trim().min(1).optional(),
    species: z.string().trim().min(1, { message: 'cannot be empty' }),
    breed: z.string().trim().min(1).optional(),
    sex: z.string().trim().min(1, { message: 'cannot be empty' }),
    age: z.number().int().positive({ message: 'age must be a positive integer' }).optional(),
    description: z.string().trim().min(1).optional(),
    microchipNumber: z.string().regex(/^\d+$/, { message: 'must contain only digits' }).optional(),
    locationCity: z.string().trim().min(1).optional(),
    locationLatitude: z.number().min(-90, { message: 'latitude must be between -90 and 90' }).max(90, { message: 'latitude must be between -90 and 90' }),
    locationLongitude: z.number().min(-180, { message: 'longitude must be between -180 and 180' }).max(180, { message: 'longitude must be between -180 and 180' }),
    locationRadius: z.number().int().positive().optional(),
    email: z.string().email({ message: 'invalid email format' }).optional(),
    phone: z.string().regex(/\d/, { message: 'invalid phone format' }).optional(),
    photoUrl: z
      .string()
      .min(1, { message: 'cannot be empty' })
      .refine((url) => isValidHttpUrl(url), {
        message: 'must be a valid URL with http or https protocol',
      }),
    lastSeenDate: z
      .string()
      .regex(/^\d{4}-\d{2}-\d{2}$/, { message: 'invalid date format (expected YYYY-MM-DD)' })
      .refine(
        (date) => {
          const dateObj = new Date(date);
          const today = new Date();
          today.setHours(0, 0, 0, 0);
          return dateObj <= today;
        },
        { message: 'lastSeenDate cannot be in the future' }
      ),
    status: z.enum(['MISSING', 'FOUND'], {
      errorMap: () => ({ message: 'status must be either MISSING or FOUND' }),
    }),
    reward: z.string().trim().min(1).optional(),
  })
  .strict()
  .refine(
    (data) => data.email || data.phone,
    {
      message: 'at least one contact method (email or phone) is required',
      path: ['contact'],
    }
  );

function mapZodErrorCode(zodCode: string, zodError: z.ZodIssue): string {
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

export default function validateCreateAnnouncement(data: CreateAnnouncementDto): void {
  try {
    CreateAnnouncementSchema.parse(data);
  } catch (error) {
    if (error instanceof z.ZodError) {
      const firstError = error.issues[0];
      const field = firstError.path.length > 0 ? firstError.path[0].toString() : undefined;
      
      const errorCode = mapZodErrorCode(firstError.code, firstError);
      
      throw new ValidationError(errorCode, firstError.message, field);
    }
    throw error;
  }
}

