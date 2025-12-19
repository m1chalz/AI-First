/**
 * TypeScript Interfaces for Web Map Component
 * 
 * Feature: 063-web-map-view
 * Date: 2025-12-18
 * 
 * This file defines the TypeScript interfaces and types for the interactive
 * map component on the web landing page, including map state, coordinates,
 * error handling, and component props.
 */

// ============================================================================
// Core Entities
// ============================================================================

/**
 * Geographic coordinates (latitude and longitude).
 * 
 * Validation:
 * - latitude: -90 to 90 (inclusive)
 * - longitude: -180 to 180 (inclusive)
 */
export interface Coordinates {
  /** Latitude in decimal degrees (-90 to 90) */
  latitude: number;

  /** Longitude in decimal degrees (-180 to 180) */
  longitude: number;
}

/**
 * Error types that can occur with the map component.
 */
export type MapErrorType =
  | 'PERMISSION_DENIED'        // User explicitly denied location permission
  | 'PERMISSION_NOT_REQUESTED' // Location permission not yet requested
  | 'LOCATION_UNAVAILABLE'     // Location retrieval failed (timeout, GPS off)
  | 'MAP_LOAD_FAILED';         // Map tiles failed to load (network error)

/**
 * Error state for the map component.
 */
export interface MapError {
  /** Type of error */
  type: MapErrorType;

  /** Human-readable error message */
  message: string;

  /** Whether to show map in fallback mode (true for LOCATION_UNAVAILABLE) */
  showFallbackMap: boolean;
}

/**
 * Current state of the map component.
 */
export interface MapState {
  /** Center coordinates of the map (latitude, longitude) */
  center: Coordinates;

  /** Zoom level (13 = ~10 km radius, range: 10-18) */
  zoom: number;

  /** Loading state for map initialization */
  isLoading: boolean;

  /** Current error state (null if no error) */
  error: MapError | null;

  /** Whether permission prompt should be displayed */
  showPermissionPrompt: boolean;
}

// ============================================================================
// Component Props
// ============================================================================

/**
 * Props for the main MapView component.
 */
export interface MapViewProps {
  /** Additional CSS class name for styling */
  className?: string;

  /** Test identifier for E2E tests */
  'data-testid'?: string;
}

/**
 * Props for the MapPermissionPrompt component.
 */
export interface MapPermissionPromptProps {
  /** Callback to request location permission */
  onRequestPermission: () => void;

  /** Additional CSS class name for styling */
  className?: string;

  /** Test identifier for E2E tests */
  'data-testid'?: string;
}

/**
 * Props for the MapErrorState component.
 */
export interface MapErrorStateProps {
  /** Error object to display */
  error: MapError;

  /** Additional CSS class name for styling */
  className?: string;

  /** Test identifier for E2E tests */
  'data-testid'?: string;
}

// ============================================================================
// Hook Return Types
// ============================================================================

/**
 * Return type for the useMapState hook.
 */
export interface UseMapStateReturn {
  /** Center coordinates for the map */
  center: Coordinates;

  /** Zoom level for the map */
  zoom: number;

  /** Loading state */
  isLoading: boolean;

  /** Current error state */
  error: MapError | null;

  /** Whether to show permission prompt */
  showPermissionPrompt: boolean;

  /** Handler to request location permission */
  handleRequestPermission: () => void;
}

// ============================================================================
// Constants
// ============================================================================

/**
 * Map configuration constants.
 */
export const MAP_CONFIG = {
  /** Default zoom level for 10 km radius viewport */
  DEFAULT_ZOOM: 13,

  /** Minimum allowed zoom level */
  MIN_ZOOM: 10,

  /** Maximum allowed zoom level */
  MAX_ZOOM: 18,

  /** Default fallback location (Wrocław, PL) */
  FALLBACK_LOCATION: {
    latitude: 51.1079,
    longitude: 17.0385,
  } as Coordinates,

  /** Map height in pixels */
  MAP_HEIGHT_PX: 400,

  /** OpenStreetMap tile layer URL */
  TILE_LAYER_URL: 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',

  /** Attribution text (required by OSM license) */
  ATTRIBUTION: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
} as const;

/**
 * Error messages for different error types.
 */
export const ERROR_MESSAGES: Record<MapErrorType, string> = {
  PERMISSION_NOT_REQUESTED: 'Location permission is required to display the map.',
  PERMISSION_DENIED: 'Location access was denied. Please enable location in your browser settings.',
  LOCATION_UNAVAILABLE: 'Unable to get your location. Please refresh the page to try again.',
  MAP_LOAD_FAILED: 'Failed to load map. Please refresh the page to try again.',
};

// ============================================================================
// Type Guards
// ============================================================================

/**
 * Type guard for Coordinates.
 */
export function isCoordinates(value: unknown): value is Coordinates {
  return (
    typeof value === 'object' &&
    value !== null &&
    'latitude' in value &&
    'longitude' in value &&
    typeof (value as Coordinates).latitude === 'number' &&
    typeof (value as Coordinates).longitude === 'number'
  );
}

/**
 * Creates MapError object from error type.
 * 
 * @param type - Error type
 * @returns MapError object with appropriate message and flags
 */
export function createMapError(type: MapErrorType): MapError {
  return {
    type,
    message: ERROR_MESSAGES[type],
    showFallbackMap: type === 'LOCATION_UNAVAILABLE',
  };
}

