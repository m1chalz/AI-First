import { useState, useEffect, useCallback } from 'react';
import type { Coordinates, GeolocationState } from '../types/location';

export interface UseGeolocationOptions {
  /** Whether to automatically request location on mount. Default: true */
  autoRequest?: boolean;
  /** Timeout in milliseconds for geolocation request. Default: 3000 for auto, 10000 for on-demand */
  timeout?: number;
}

export interface UseGeolocationResult extends GeolocationState {
  /** Function to manually request current position, checking permissions first (useful when autoRequest is false) */
  requestPosition: () => void;
}

export function useGeolocation(options?: UseGeolocationOptions): UseGeolocationResult {
  const { autoRequest = true, timeout = autoRequest ? 3000 : 10000 } = options || {};
  const [coordinates, setCoordinates] = useState<Coordinates | null>(null);
  const [error, setError] = useState<GeolocationPositionError | null>(null);
  const [isLoading, setIsLoading] = useState(autoRequest);

  const fetchPosition = useCallback(() => {
    setIsLoading(true);
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const lat = position.coords.latitude;
        const lng = position.coords.longitude;
        setCoordinates({
          lat,
          lng
        });
        setIsLoading(false);
      },
      (err) => {
        setError(err);
        setIsLoading(false);
      },
      { timeout }
    );
  }, [timeout]);

  const checkPermissionAndFetchLocation = useCallback(() => {
    navigator.permissions.query({ name: 'geolocation' }).then((status) => {
      if (status.state === 'denied') {
        setIsLoading(false);
        return;
      }

      fetchPosition();
    }).catch(() => {
      // If permissions API is not supported, just try to fetch position
      fetchPosition();
    });
  }, [fetchPosition]);

  useEffect(() => {
    if (autoRequest) {
      checkPermissionAndFetchLocation();
    }
  }, [autoRequest, checkPermissionAndFetchLocation]);

  return {
    coordinates,
    error,
    isLoading,
    requestPosition: autoRequest ? fetchPosition : checkPermissionAndFetchLocation,
  };
}

