export interface Coordinates {
  lat: number;
  lng: number;
}

export type PermissionState = 'granted' | 'denied' | 'prompt' | 'loading';

export interface GeolocationState {
  coordinates: Coordinates | null;
  permissionState: PermissionState;
  error: GeolocationPositionError | null;
  isLoading: boolean;
}

