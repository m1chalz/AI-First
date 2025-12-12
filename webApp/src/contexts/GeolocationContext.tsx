import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import type { Coordinates } from '../types/location';

class GeolocationError extends Error {
  constructor(
    public code: number,
    message: string
  ) {
    super(message);
    this.name = 'GeolocationError';
  }
}

interface GeolocationState {
  coordinates: Coordinates | null;
  error: GeolocationPositionError | null;
  isLoading: boolean;
  permissionCheckCompleted: boolean;
}

export interface GeolocationContextValue {
  state: GeolocationState;
}

const GeolocationContext = createContext<GeolocationContextValue | null>(null);

export function GeolocationProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState<GeolocationState>({
    coordinates: null,
    error: null,
    isLoading: false,
    permissionCheckCompleted: false
  });

  const fetchPosition = () => {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const lat = position.coords.latitude;
        const lng = position.coords.longitude;
        setState((prev) => ({
          ...prev,
          coordinates: { lat, lng },
          error: null,
          isLoading: false
        }));
      },
      (err) => {
        setState((prev) => ({
          ...prev,
          error: err,
          isLoading: false
        }));
      },
      { timeout: 10000 }
    );
  };

  // Auto-fetch location on mount after permission check
  useEffect(() => {
    const fetchLocationOnMount = async () => {
      setState((prev) => ({ ...prev, isLoading: true }));

      if (navigator.permissions) {
        try {
          const status = await navigator.permissions.query({ name: 'geolocation' });
          setState((prev) => ({ ...prev, permissionCheckCompleted: true }));

          if (status.state === 'denied') {
            const error = new GeolocationError(1, 'User denied geolocation');
            setState((prev) => ({
              ...prev,
              error: error as unknown as GeolocationPositionError,
              isLoading: false
            }));
            return;
          }

          fetchPosition();
        } catch {
          // If permissions API is not supported, just try to fetch position
          setState((prev) => ({
            ...prev,
            permissionCheckCompleted: true
          }));
          fetchPosition();
        }
      } else {
        // No permissions API available, just fetch position
        setState((prev) => ({
          ...prev,
          permissionCheckCompleted: true
        }));
        fetchPosition();
      }
    };

    fetchLocationOnMount();
  }, []);

  return <GeolocationContext.Provider value={{ state }}>{children}</GeolocationContext.Provider>;
}

export function useGeolocationContext(): GeolocationContextValue {
  const context = useContext(GeolocationContext);

  if (!context) {
    throw new Error('useGeolocationContext must be used within GeolocationProvider');
  }

  return context;
}
