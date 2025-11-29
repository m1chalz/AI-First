import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { useGeolocation } from '../../hooks/use-geolocation';

describe('useGeolocation', () => {
  let mockGeolocation = {
    getCurrentPosition: vi.fn(),
    clearWatch: vi.fn()
  };
  let mockPermissions = {
    query: vi.fn()
  };

  const createPosition = (lat: number, lng: number): GeolocationPosition => ({
    coords: {
      latitude: lat,
      longitude: lng
    },
  } as GeolocationPosition);

  const createError = (code: number): GeolocationPositionError => ({
    code,
    message: `Error ${code}`,
    PERMISSION_DENIED: 1,
    POSITION_UNAVAILABLE: 2,
    TIMEOUT: 3,
  });

  beforeEach(() => {
    vi.clearAllMocks();

    Object.defineProperty(navigator, 'geolocation', {
      value: mockGeolocation,
      writable: true,
    });

    Object.defineProperty(navigator, 'permissions', {
      value: mockPermissions,
      writable: true,
    });
  });

  it('should return loading state initially', () => {
    // given
    mockPermissions.query.mockResolvedValue({ state: 'prompt' });

    // when
    const { result } = renderHook(() => useGeolocation());

    // then
    expect(result.current.isLoading).toBe(true);
    expect(result.current.coordinates).toBeNull();
    expect(result.current.permissionState).toBe('loading');
  });

  it('should fetch coordinates when permission is granted', async () => {
    // given
    mockPermissions.query.mockResolvedValue({ state: 'granted' });
    mockGeolocation.getCurrentPosition.mockImplementation((success: PositionCallback) => {
      success(createPosition(52.229676, 21.012229));
    });

    // when
    const { result } = renderHook(() => useGeolocation());

    // then
    await waitFor(() => expect(result.current.isLoading).toBe(false));

    expect(result.current.permissionState).toBe('granted');
    expect(result.current.coordinates).toEqual({
      lat: 52.229676,
      lng: 21.012229,
    });
    expect(result.current.error).toBeNull();
  });

  it('should handle timeout error and fallback', async () => {
    // given
    mockPermissions.query.mockResolvedValue({ state: 'granted' });
    mockGeolocation.getCurrentPosition.mockImplementation((_success: PositionCallback, error: PositionErrorCallback) => {
      error(createError(3));
    });

    // when
    const { result } = renderHook(() => useGeolocation());

    // then
    await waitFor(() => expect(result.current.isLoading).toBe(false));

    expect(result.current.permissionState).toBe('granted');
    expect(result.current.coordinates).toBeNull();
    expect(result.current.error).toBeTruthy();
    expect(result.current.error?.code).toBe(3);
  });

  it('should handle position unavailable error', async () => {
    // given
    mockPermissions.query.mockResolvedValue({ state: 'granted' });
    mockGeolocation.getCurrentPosition.mockImplementation((_success: PositionCallback, error: PositionErrorCallback) => {
      error(createError(2));
    });

    // when
    const { result } = renderHook(() => useGeolocation());

    // then
    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
    });

    expect(result.current.coordinates).toBeNull();
    expect(result.current.error?.code).toBe(2);
  });

  it('should handle permission denied', async () => {
    // given
    mockPermissions.query.mockResolvedValue({ state: 'denied' });

    // when
    const { result } = renderHook(() => useGeolocation());

    // then
    await waitFor(() => expect(result.current.isLoading).toBe(false));

    expect(result.current.permissionState).toBe('denied');
    expect(result.current.coordinates).toBeNull();
  });

  it('should handle prompt permission state', async () => {
    // given
    mockPermissions.query.mockResolvedValue({ state: 'prompt' });
    mockGeolocation.getCurrentPosition.mockImplementation((success: PositionCallback) => {
      success(createPosition(40.7128, -74.006));
    });

    // when
    const { result } = renderHook(() => useGeolocation());

    // then
    await waitFor(() => expect(result.current.isLoading).toBe(false));

    expect(result.current.coordinates).toEqual({
      lat: 40.7128,
      lng: -74.006,
    });
  });
});