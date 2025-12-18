import type { Coordinates } from './location';

export type MapErrorType =
  | 'PERMISSION_DENIED'
  | 'PERMISSION_NOT_REQUESTED'
  | 'LOCATION_UNAVAILABLE'
  | 'MAP_LOAD_FAILED';

export interface MapError {
  type: MapErrorType;
  message: string;
  showFallbackMap: boolean;
}

export interface MapState {
  center: Coordinates;
  zoom: number;
  isLoading: boolean;
  error: MapError | null;
  showPermissionPrompt: boolean;
}

export interface MapViewProps {
  className?: string;
  'data-testid'?: string;
}

export interface MapPermissionPromptProps {
  onRequestPermission: () => void;
  className?: string;
  'data-testid'?: string;
}

export interface MapErrorStateProps {
  error: MapError;
  className?: string;
  'data-testid'?: string;
}

export interface UseMapStateReturn {
  center: Coordinates;
  zoom: number;
  isLoading: boolean;
  error: MapError | null;
  showPermissionPrompt: boolean;
  handleRequestPermission: () => void;
}

export const MAP_CONFIG = {
  DEFAULT_ZOOM: 13,
  MIN_ZOOM: 10,
  MAX_ZOOM: 18,
  FALLBACK_LOCATION: { lat: 51.1079, lng: 17.0385 } as Coordinates,
  MAP_HEIGHT_PX: 400,
  TILE_LAYER_URL: 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
  ATTRIBUTION:
    '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
} as const;

export const ERROR_MESSAGES: Record<MapErrorType, string> = {
  PERMISSION_NOT_REQUESTED:
    'Location permission is required to display the map.',
  PERMISSION_DENIED:
    'Location access was denied. Please enable location in your browser settings.',
  LOCATION_UNAVAILABLE:
    'Unable to get your location. Please refresh the page to try again.',
  MAP_LOAD_FAILED: 'Failed to load map. Please refresh the page to try again.',
};

export function isCoordinates(value: unknown): value is Coordinates {
  return (
    typeof value === 'object' &&
    value !== null &&
    'lat' in value &&
    'lng' in value &&
    typeof (value as Coordinates).lat === 'number' &&
    typeof (value as Coordinates).lng === 'number'
  );
}

export function createMapError(type: MapErrorType): MapError {
  return {
    type,
    message: ERROR_MESSAGES[type],
    showFallbackMap: type === 'LOCATION_UNAVAILABLE',
  };
}

