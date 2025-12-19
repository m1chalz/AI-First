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
    mockGetAnnouncements.mockResolvedValueOnce([
      {
        id: '123',
        petName: 'Buddy',
        species: 'DOG',
        status: 'MISSING',
        locationLatitude: 52.23,
        locationLongitude: 21.01,
        photoUrl: 'http://example.com/photo.jpg',
        phone: '+48123456789',
        email: 'test@example.com',
        createdAt: '2025-01-01T10:00:00Z',
        lastSeenDate: '2025-01-01',
        sex: 'MALE',
        breed: null,
        description: null,
        microchipNumber: null,
        age: null,
        reward: null,
        updatedAt: null
      }
    ]);

    // when
    const { result } = renderHook(() => useMapPins(userLocation));

    // then
    expect(result.current.loading).toBe(true);

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.pins).toHaveLength(1);
    expect(result.current.pins[0]).toEqual({
      id: '123',
      name: 'Buddy',
      species: 'DOG',
      status: 'MISSING',
      latitude: 52.23,
      longitude: 21.01,
      photoUrl: 'http://example.com/photo.jpg',
      phoneNumber: '+48123456789',
      email: 'test@example.com',
      createdAt: '2025-01-01T10:00:00Z'
    });
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
      {
        id: '1',
        petName: 'Buddy',
        species: 'DOG',
        status: 'MISSING',
        locationLatitude: 52.23,
        locationLongitude: 21.01,
        photoUrl: 'http://example.com/1.jpg',
        phone: '+48123',
        email: 'a@b.com',
        createdAt: '2025-01-01T10:00:00Z',
        lastSeenDate: '2025-01-01',
        sex: 'MALE',
        breed: null,
        description: null,
        microchipNumber: null,
        age: null,
        reward: null,
        updatedAt: null
      },
      {
        id: '2',
        petName: 'Closed Pet',
        species: 'CAT',
        status: 'CLOSED',
        locationLatitude: 52.24,
        locationLongitude: 21.02,
        photoUrl: 'http://example.com/2.jpg',
        phone: '+48456',
        email: 'c@d.com',
        createdAt: '2025-01-02T10:00:00Z',
        lastSeenDate: '2025-01-02',
        sex: 'FEMALE',
        breed: null,
        description: null,
        microchipNumber: null,
        age: null,
        reward: null,
        updatedAt: null
      }
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

  it.each([
    { field: 'petName', value: null, expected: '' },
    { field: 'createdAt', value: null, expected: '' }
  ])('transforms null $field to empty string', async ({ field, value, expected }) => {
    // given
    const userLocation: Coordinates = { lat: 52.2297, lng: 21.0122 };
    const announcement = {
      id: '1',
      petName: 'Buddy',
      species: 'DOG',
      status: 'MISSING',
      locationLatitude: 52.23,
      locationLongitude: 21.01,
      photoUrl: 'http://example.com/1.jpg',
      phone: '+48123',
      email: 'a@b.com',
      createdAt: '2025-01-01T10:00:00Z',
      lastSeenDate: '2025-01-01',
      sex: 'MALE',
      breed: null,
      description: null,
      microchipNumber: null,
      age: null,
      reward: null,
      updatedAt: null,
      [field]: value
    };
    mockGetAnnouncements.mockResolvedValueOnce([announcement]);

    // when
    const { result } = renderHook(() => useMapPins(userLocation));

    // then
    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    const pin = result.current.pins[0];
    const pinField = field === 'petName' ? 'name' : field;
    expect(pin[pinField as keyof typeof pin]).toBe(expected);
  });
});
