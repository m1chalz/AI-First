import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { useMapPins } from '../use-map-pins';
import type { Coordinates } from '../../types/location';
import { announcementService } from '../../services/announcement-service';

vi.mock('../../services/announcement-service', () => ({
  announcementService: {
    getAnnouncements: vi.fn()
  }
}));

const mockGetAnnouncements = vi.mocked(announcementService.getAnnouncements);

const createAnnouncement = (overrides = {}) => ({
  id: '123',
  petName: 'Buddy',
  species: 'DOG' as const,
  status: 'MISSING' as const,
  sex: 'MALE' as const,
  locationLatitude: 52.23,
  locationLongitude: 21.01,
  photoUrl: 'http://example.com/photo.jpg',
  phone: '+48123456789',
  email: 'test@example.com',
  lastSeenDate: '2025-01-01',
  breed: null,
  description: null,
  microchipNumber: null,
  age: null,
  reward: null,
  createdAt: null,
  updatedAt: null,
  ...overrides
});

describe('useMapPins', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('returns empty pins and loading false when userLocation is null', () => {
    // when
    const { result } = renderHook(() => useMapPins(null));

    // then
    expect(result.current.pins).toEqual([]);
    expect(result.current.loading).toBe(false);
    expect(result.current.error).toBeNull();
  });

  it('fetches pins when userLocation is provided', async () => {
    // given
    const userLocation: Coordinates = { lat: 52.2297, lng: 21.0122 };
    const announcement = createAnnouncement();
    mockGetAnnouncements.mockResolvedValueOnce([announcement]);

    // when
    const { result } = renderHook(() => useMapPins(userLocation));

    // then
    expect(result.current.loading).toBe(true);

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.pins).toHaveLength(1);
    expect(result.current.pins[0]).toEqual(announcement);
    expect(result.current.error).toBeNull();
  });

  it('sets error when fetch fails', async () => {
    // given
    const userLocation: Coordinates = { lat: 52.2297, lng: 21.0122 };
    mockGetAnnouncements.mockRejectedValueOnce(new Error('Network error'));

    // when
    const { result } = renderHook(() => useMapPins(userLocation));

    // then
    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.pins).toEqual([]);
    expect(result.current.error?.message).toBe('Network error');
  });

  it('calls service with userLocation', async () => {
    // given
    const userLocation: Coordinates = { lat: 52.2297, lng: 21.0122 };
    mockGetAnnouncements.mockResolvedValueOnce([]);

    // when
    renderHook(() => useMapPins(userLocation));

    // then
    await waitFor(() => {
      expect(mockGetAnnouncements).toHaveBeenCalledWith(userLocation);
    });
  });

  it('filters out CLOSED announcements', async () => {
    // given
    const userLocation: Coordinates = { lat: 52.2297, lng: 21.0122 };
    mockGetAnnouncements.mockResolvedValueOnce([
      createAnnouncement({ id: '1', status: 'MISSING' }),
      createAnnouncement({ id: '2', status: 'CLOSED' })
    ]);

    // when
    const { result } = renderHook(() => useMapPins(userLocation));

    // then
    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.pins).toHaveLength(1);
    expect(result.current.pins[0].id).toBe('1');
  });
});
