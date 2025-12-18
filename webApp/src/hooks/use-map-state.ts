import { useState, useCallback } from 'react';
import { useGeolocationContext } from '../contexts/GeolocationContext';
import { MAP_CONFIG, createMapError } from '../types/map';
import type { UseMapStateReturn, MapError } from '../types/map';
import type { Coordinates } from '../types/location';

function determineCenter(coordinates: Coordinates | null): Coordinates {
  if (coordinates) {
    return coordinates;
  }
  return MAP_CONFIG.FALLBACK_LOCATION;
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
    zoom: MAP_CONFIG.DEFAULT_ZOOM,
    isLoading: geolocation.isLoading,
    error,
    showPermissionPrompt,
    handleRequestPermission,
    handleMapLoadError
  };
}
