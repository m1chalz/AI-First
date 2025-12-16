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

/**
 * Checks for e2e test location override via URL parameters.
 * Use ?e2eLat=51.1&e2eLng=17.0 to mock geolocation in e2e tests.
 */
function getE2ELocationOverride(): Coordinates | null {
  if (typeof window === 'undefined') return null;

  const params = new URLSearchParams(window.location.search);
  const e2eLat = params.get('e2eLat');
  const e2eLng = params.get('e2eLng');

  if (e2eLat && e2eLng) {
    const lat = parseFloat(e2eLat);
    const lng = parseFloat(e2eLng);
    if (!isNaN(lat) && !isNaN(lng)) {
      console.log('[E2E] Using location override from URL:', lat, lng);
      return { lat, lng };
    }
  }
  return null;
}

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

      // Check for e2e test location override first
      const e2eOverride = getE2ELocationOverride();
      if (e2eOverride) {
        setState((prev) => ({
          ...prev,
          coordinates: e2eOverride,
          error: null,
          isLoading: false,
          permissionCheckCompleted: true
        }));
        return;
      }

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
