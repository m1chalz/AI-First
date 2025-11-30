import { z } from 'zod';
import { ValidationError } from './errors.ts';
import type { LocationFilter } from '../types/announcement.ts';

const DEFAULT_RANGE_KM = 5;

const LocationSchemaBase = z.object({
  lat: z
    .number({ message: 'Parameter \'lat\' must be a valid number' })
    .min(-90, { message: 'Parameter \'lat\' must be between -90 and 90' })
    .max(90, { message: 'Parameter \'lat\' must be between -90 and 90' })
    .optional(),
  lng: z
    .number({ message: 'Parameter \'lng\' must be a valid number' })
    .min(-180, { message: 'Parameter \'lng\' must be between -180 and 180' })
    .max(180, { message: 'Parameter \'lng\' must be between -180 and 180' })
    .optional(),
  range: z
    .number({ message: 'Parameter \'range\' must be a valid number' })
    .int({ message: 'Parameter \'range\' must be an integer' })
    .refine((val) => val !== 0, { message: 'Parameter \'range\' must be greater than zero' })
    .refine((val) => val > 0, { message: 'Parameter \'range\' must be a positive number' })
    .optional()
    .default(DEFAULT_RANGE_KM),
});

const LocationSchema = LocationSchemaBase.refine(
  (data) => {
    const hasLat = data.lat !== undefined;
    const hasLng = data.lng !== undefined;
    return (hasLat && hasLng) || (!hasLat && !hasLng);
  },
  (data) => {
    if (data.lat !== undefined && data.lng === undefined) {
      return { message: 'Parameter \'lng\' is required when \'lat\' is provided', path: ['lng'] };
    }
    if (data.lng !== undefined && data.lat === undefined) {
      return { message: 'Parameter \'lat\' is required when \'lng\' is provided', path: ['lat'] };
    }
    return { message: 'Coordinate validation error', path: ['coordinates'] };
  }
);

export function validateLocation(lat?: number, lng?: number, range?: number): LocationFilter | undefined {
  try {
    const result = LocationSchema.parse({ lat, lng, range });
    
    if (result.lat !== undefined && result.lng !== undefined) {
      return {
        lat: result.lat,
        lng: result.lng,
        range: result.range,
      };
    }
    
    return undefined;
  } catch (error) {
    if (error instanceof z.ZodError) {
      const firstError = error.errors[0];
      const field = firstError.path.length > 0 ? firstError.path[0].toString() : undefined;
      throw new ValidationError('INVALID_PARAMETER', firstError.message, field);
    }
    throw error;
  }
}
