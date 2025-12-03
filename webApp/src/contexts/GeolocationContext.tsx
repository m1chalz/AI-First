import { createContext, useContext, useState, useEffect, ReactNode, useCallback } from 'react';
import type { GeolocationState } from '../types/location';

class GeolocationError extends Error {
  constructor(public code: number, message: string) {
    super(message);
    this.name = 'GeolocationError';
  }
}

export interface CachedGeolocationState extends GeolocationState {
  permissionCheckCompleted: boolean;
}

export interface GeolocationContextValue {
  state: CachedGeolocationState;
  requestLocation: () => void;
  clearGeolocation: () => void;
}

const GeolocationContext = createContext<GeolocationContextValue | null>(null);

export function GeolocationProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState<CachedGeolocationState>({
    coordinates: null,
    error: null,
    isLoading: false,
    permissionCheckCompleted: false,
  });

  // Check permission on mount (without requesting location)
  useEffect(() => {
    const checkPermissionOnMount = async () => {
      if (navigator.permissions) {
        try {
          const status = await navigator.permissions.query({ name: 'geolocation' });
          if (status.state === 'denied') {
            const error = new GeolocationError(1, 'User denied geolocation');
            setState(prev => ({
              ...prev,
              error: error as any,
              permissionCheckCompleted: true,
            }));
          } else {
            setState(prev => ({
              ...prev,
              permissionCheckCompleted: true,
            }));
          }
        } catch {
          // If permissions API is not supported, just mark check as completed
          setState(prev => ({
            ...prev,
            permissionCheckCompleted: true,
          }));
        }
      } else {
        setState(prev => ({
          ...prev,
          permissionCheckCompleted: true,
        }));
      }
    };

    checkPermissionOnMount();
  }, []);

  const handleGeolocationSuccess = useCallback((position: GeolocationPosition) => {
    const lat = position.coords.latitude;
    const lng = position.coords.longitude;
    setState(prev => ({
      ...prev,
      coordinates: { lat, lng },
      error: null,
      isLoading: false,
    }));
  }, []);

  const handleGeolocationError = useCallback((err: GeolocationPositionError) => {
    setState(prev => ({
      ...prev,
      error: err,
      isLoading: false,
    }));
  }, []);

  const fetchCurrentPosition = useCallback(() => {
    navigator.geolocation.getCurrentPosition(
      handleGeolocationSuccess,
      handleGeolocationError,
      { timeout: 10000 }
    );
  }, [handleGeolocationSuccess, handleGeolocationError]);

  const requestLocation = useCallback(() => {
    setState(prev => ({ ...prev, isLoading: true }));

    if (navigator.permissions) {
      navigator.permissions.query({ name: 'geolocation' }).then((status) => {
        if (status.state === 'denied') {
          const error = new GeolocationError(1, 'User denied geolocation');
          setState(prev => ({
            ...prev,
            error: error as any,
            isLoading: false,
          }));
          return;
        }

        fetchCurrentPosition();
      }).catch(() => {
        // If permissions API is not supported, just try to fetch position
        fetchCurrentPosition();
      });
    } else {
      // If permissions API is not available, try to fetch position directly
      fetchCurrentPosition();
    }
  }, [fetchCurrentPosition]);

  const clearGeolocation = useCallback(() => {
    setState(prev => ({
      ...prev,
      coordinates: null,
      error: null,
      isLoading: false,
    }));
  }, []);

  return (
    <GeolocationContext.Provider value={{ state, requestLocation, clearGeolocation }}>
      {children}
    </GeolocationContext.Provider>
  );
}

export function useGeolocationContext(): GeolocationContextValue {
  const context = useContext(GeolocationContext);

  if (!context) {
    throw new Error('useGeolocationContext must be used within GeolocationProvider');
  }

  return context;
}

