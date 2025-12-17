import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor, act } from '@testing-library/react';

const mockGetAnnouncements = vi.fn();
const mockUseGeolocationContext = vi.fn();

vi.mock('../../services/announcement-service', () => ({
  announcementService: {
    getAnnouncements: (...args: unknown[]) => mockGetAnnouncements(...args)
  }
}));

vi.mock('../../contexts/GeolocationContext', () => ({
  useGeolocationContext: () => mockUseGeolocationContext()
}));

import { useAnnouncementList } from '../use-animal-list';

describe('useAnnouncementList', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should return loading state while geolocation is loading', () => {
    // Given
    mockUseGeolocationContext.mockReturnValue({
      state: {
        isLoading: true,
        permissionCheckCompleted: false,
        coordinates: null
      }
    });

    // When
    const { result } = renderHook(() => useAnnouncementList());

    // Then
    expect(result.current.isLoading).toBe(true);
    expect(result.current.announcements).toEqual([]);
    expect(result.current.error).toBeNull();
  });

  it('should load announcements when geolocation is ready', async () => {
    // Given
    const mockAnnouncements = [
      { id: '1', petName: 'Buddy', description: 'Lost dog' },
      { id: '2', petName: 'Mittens', description: 'Lost cat' }
    ];
    mockGetAnnouncements.mockResolvedValue(mockAnnouncements);
    mockUseGeolocationContext.mockReturnValue({
      state: {
        isLoading: false,
        permissionCheckCompleted: true,
        coordinates: { latitude: 52.0, longitude: 21.0 }
      }
    });

    // When
    const { result } = renderHook(() => useAnnouncementList());

    // Then
    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
    });
    expect(result.current.announcements).toEqual(mockAnnouncements);
    expect(result.current.error).toBeNull();
    expect(result.current.isEmpty).toBe(false);
    expect(mockGetAnnouncements).toHaveBeenCalledWith({ latitude: 52.0, longitude: 21.0 });
  });

  it('should return empty state when no announcements', async () => {
    // Given
    mockGetAnnouncements.mockResolvedValue([]);
    mockUseGeolocationContext.mockReturnValue({
      state: {
        isLoading: false,
        permissionCheckCompleted: true,
        coordinates: null
      }
    });

    // When
    const { result } = renderHook(() => useAnnouncementList());

    // Then
    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
    });
    expect(result.current.isEmpty).toBe(true);
    expect(result.current.announcements).toEqual([]);
  });

  it('should set error when service fails', async () => {
    // Given
    mockGetAnnouncements.mockRejectedValue(new Error('Network error'));
    mockUseGeolocationContext.mockReturnValue({
      state: {
        isLoading: false,
        permissionCheckCompleted: true,
        coordinates: null
      }
    });

    // When
    const { result } = renderHook(() => useAnnouncementList());

    // Then
    await waitFor(() => {
      expect(result.current.error).toBe('Network error');
    });
    expect(result.current.isLoading).toBe(false);
    expect(result.current.announcements).toEqual([]);
  });

  it('should handle unknown error type', async () => {
    // Given
    mockGetAnnouncements.mockRejectedValue('Unknown error');
    mockUseGeolocationContext.mockReturnValue({
      state: {
        isLoading: false,
        permissionCheckCompleted: true,
        coordinates: null
      }
    });

    // When
    const { result } = renderHook(() => useAnnouncementList());

    // Then
    await waitFor(() => {
      expect(result.current.error).toBe('Unknown error');
    });
  });

  it('should allow manual reload via loadAnnouncements', async () => {
    // Given
    mockGetAnnouncements.mockResolvedValue([{ id: '1', petName: 'Buddy' }]);
    mockUseGeolocationContext.mockReturnValue({
      state: {
        isLoading: false,
        permissionCheckCompleted: true,
        coordinates: null
      }
    });

    const { result } = renderHook(() => useAnnouncementList());

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
    });

    // When
    mockGetAnnouncements.mockResolvedValue([{ id: '2', petName: 'Max' }]);
    await act(async () => {
      await result.current.loadAnnouncements();
    });

    // Then
    expect(result.current.announcements).toEqual([{ id: '2', petName: 'Max' }]);
  });

  it('should return null for geolocationError', () => {
    // Given
    mockUseGeolocationContext.mockReturnValue({
      state: {
        isLoading: true,
        permissionCheckCompleted: false,
        coordinates: null
      }
    });

    // When
    const { result } = renderHook(() => useAnnouncementList());

    // Then
    expect(result.current.geolocationError).toBeNull();
  });
});
