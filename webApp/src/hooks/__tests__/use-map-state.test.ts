import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor, act } from '@testing-library/react';
import { useMapState } from '../use-map-state';
import config from '../../config/config';

vi.mock('../../contexts/GeolocationContext', () => ({
  useGeolocationContext: vi.fn()
}));

import { useGeolocationContext } from '../../contexts/GeolocationContext';

const mockUseGeolocationContext = vi.mocked(useGeolocationContext);

describe('useMapState', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('when permission granted', () => {
    it('should return user location as center with default zoom', async () => {
      // given
      const userLocation = { lat: 52.2297, lng: 21.0122 };
      mockUseGeolocationContext.mockReturnValue({
        state: {
          coordinates: userLocation,
          error: null,
          isLoading: false,
          permissionCheckCompleted: true
        }
      });

      // when
      const { result } = renderHook(() => useMapState());

      // then
      await waitFor(() => {
        expect(result.current.isLoading).toBe(false);
      });
      expect(result.current.center).toEqual(userLocation);
      expect(result.current.zoom).toBe(config.map.defaultZoom);
      expect(result.current.error).toBeNull();
      expect(result.current.showPermissionPrompt).toBe(false);
    });
  });

  describe('when location unavailable', () => {
    it('should return fallback location with showFallbackMap error', async () => {
      // given
      const geolocationError = {
        code: 2,
        message: 'Position unavailable',
        PERMISSION_DENIED: 1,
        POSITION_UNAVAILABLE: 2,
        TIMEOUT: 3
      } as GeolocationPositionError;

      mockUseGeolocationContext.mockReturnValue({
        state: {
          coordinates: null,
          error: geolocationError,
          isLoading: false,
          permissionCheckCompleted: true
        }
      });

      // when
      const { result } = renderHook(() => useMapState());

      // then
      await waitFor(() => {
        expect(result.current.isLoading).toBe(false);
      });
      expect(result.current.center).toEqual(config.map.fallbackLocation);
      expect(result.current.error?.type).toBe('LOCATION_UNAVAILABLE');
      expect(result.current.error?.showFallbackMap).toBe(true);
      expect(result.current.showPermissionPrompt).toBe(false);
    });
  });

  describe('when map load fails', () => {
    it('should return MAP_LOAD_FAILED error when coordinates valid but map fails', async () => {
      // given
      const userLocation = { lat: 52.2297, lng: 21.0122 };
      mockUseGeolocationContext.mockReturnValue({
        state: {
          coordinates: userLocation,
          error: null,
          isLoading: false,
          permissionCheckCompleted: true
        }
      });

      // when
      const { result } = renderHook(() => useMapState());

      // then
      await waitFor(() => {
        expect(result.current.isLoading).toBe(false);
      });

      // when
      act(() => {
        result.current.handleMapLoadError();
      });

      // then
      await waitFor(() => {
        expect(result.current.error?.type).toBe('MAP_LOAD_FAILED');
      });
      expect(result.current.error?.showFallbackMap).toBe(false);
    });
  });

  describe('loading state', () => {
    it('should return isLoading true when geolocation is loading', () => {
      // given
      mockUseGeolocationContext.mockReturnValue({
        state: {
          coordinates: null,
          error: null,
          isLoading: true,
          permissionCheckCompleted: false
        }
      });

      // when
      const { result } = renderHook(() => useMapState());

      // then
      expect(result.current.isLoading).toBe(true);
    });
  });
});
