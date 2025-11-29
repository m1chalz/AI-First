export interface Coordinates {
  lat: number;
  lng: number;
}

export interface GeolocationState {
  coordinates: Coordinates | null;
  error: GeolocationPositionError | null;
  isLoading: boolean;
}

