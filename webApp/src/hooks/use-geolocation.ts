import { useState, useEffect } from 'react';
import type { Coordinates, GeolocationState } from '../types/location';

export function useGeolocation(): GeolocationState {
  const [coordinates, setCoordinates] = useState<Coordinates | null>(null);
  const [error, setError] = useState<GeolocationPositionError | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  function fetchPosition() {
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
      { timeout: 3000 }
    );
  };

  function checkPermissionAndFetchLocation() {
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
  }

  useEffect(() => {
    checkPermissionAndFetchLocation();
  }, []);

  return {
    coordinates,
    error,
    isLoading,
  };
}

