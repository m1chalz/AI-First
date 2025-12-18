import { useState, useCallback } from 'react';
import { useGeolocationContext } from '../contexts/GeolocationContext';
import config from '../config/config';
import type { Coordinates } from '../types/location';

type MapErrorType = 'PERMISSION_DENIED' | 'PERMISSION_NOT_REQUESTED' | 'LOCATION_UNAVAILABLE' | 'MAP_LOAD_FAILED';

export interface MapError {
  type: MapErrorType;
  message: string;
  showFallbackMap: boolean;
}

const ERROR_MESSAGES: Record<MapErrorType, string> = {
  PERMISSION_NOT_REQUESTED: 'Location permission is required to display the map.',
  PERMISSION_DENIED: 'Location access was denied. Please enable location in your browser settings.',
  LOCATION_UNAVAILABLE: 'Unable to get your location. Please refresh the page to try again.',
  MAP_LOAD_FAILED: 'Failed to load map. Please refresh the page to try again.'
};

function createMapError(type: MapErrorType): MapError {
  return {
    type,
    message: ERROR_MESSAGES[type],
    showFallbackMap: type === 'LOCATION_UNAVAILABLE'
  };
}

interface UseMapStateReturn {
  center: Coordinates;
  zoom: number;
  isLoading: boolean;
  error: MapError | null;
  showPermissionPrompt: boolean;
  handleRequestPermission: () => void;
  handleMapLoadError: () => void;
}

function determineCenter(coordinates: Coordinates | null): Coordinates {
  if (coordinates) {
    return coordinates;
  }
  return config.map.fallbackLocation;
}

function determineError(
  geolocationError: GeolocationPositionError | null,
  mapLoadError: boolean
): MapError | null {
  if (mapLoadError) {
    return createMapError('MAP_LOAD_FAILED');
  }
  if (geolocationError) {
    if (geolocationError.code === 1) {
      return createMapError('PERMISSION_DENIED');
    }
    return createMapError('LOCATION_UNAVAILABLE');
  }
  return null;
}

function determineShowPermissionPrompt(
  geolocationError: GeolocationPositionError | null,
  coordinates: Coordinates | null,
  permissionCheckCompleted: boolean
): boolean {
  if (!permissionCheckCompleted) {
    return false;
  }
  if (geolocationError && geolocationError.code === 1) {
    return true;
  }
  if (!coordinates && !geolocationError) {
    return true;
  }
  return false;
}

export function useMapState(): UseMapStateReturn {
  const { state: geolocation } = useGeolocationContext();
  const [mapLoadError, setMapLoadError] = useState(false);

  const center = determineCenter(geolocation.coordinates);
  const error = determineError(geolocation.error, mapLoadError);
  const showPermissionPrompt = determineShowPermissionPrompt(
    geolocation.error,
    geolocation.coordinates,
    geolocation.permissionCheckCompleted
  );

  const handleRequestPermission = useCallback(() => {
    navigator.geolocation.getCurrentPosition(
      () => window.location.reload(),
      () => window.location.reload(),
      { timeout: 10000 }
    );
  }, []);

  const handleMapLoadError = useCallback(() => {
    setMapLoadError(true);
  }, []);

  return {
    center,
    zoom: config.map.defaultZoom,
    isLoading: geolocation.isLoading,
    error,
    showPermissionPrompt,
    handleRequestPermission,
    handleMapLoadError
  };
}
