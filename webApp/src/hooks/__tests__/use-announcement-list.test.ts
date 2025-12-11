import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { useAnnouncementList } from '../use-announcement-list';
import * as announcementServiceModule from '../../services/announcement-service';

// Setup navigator.geolocation mock for tests
beforeEach(() => {
  if (!navigator.geolocation) {
    Object.defineProperty(navigator, 'geolocation', {
      value: {
        getCurrentPosition: vi.fn((success) =>
          success({
            coords: {
              latitude: 52.0,
              longitude: 21.0,
              accuracy: 100,
              altitude: null,
              altitudeAccuracy: null,
              heading: null,
              speed: null
            },
            timestamp: Date.now()
          })
        ),
        watchPosition: vi.fn(),
        clearWatch: vi.fn()
      },
      writable: true
    });
  }

  if (!navigator.permissions) {
    Object.defineProperty(navigator, 'permissions', {
      value: {
        query: vi.fn().mockResolvedValue({ state: 'granted' })
      },
      writable: true
    });
  }
});

vi.mock('../../services/announcement-service', () => ({
  announcementService: {
    getAnnouncements: vi.fn()
  }
}));

vi.mock('../../contexts/GeolocationContext', () => ({
  useGeolocationContext: vi.fn(() => ({
    state: {
      coordinates: null,
      error: null,
      isLoading: false,
      permissionCheckCompleted: true
    }
  }))
}));

describe('useAnnouncementList', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should initialize with empty state', () => {
    // given
    const mockGetAnnouncements = vi.fn().mockResolvedValue([]);
    vi.spyOn(announcementServiceModule.announcementService, 'getAnnouncements').mockImplementation(mockGetAnnouncements);

    // when
    const { result } = renderHook(() => useAnnouncementList());

    // then
    expect(result.current.isLoading).toBe(true);
    expect(result.current.announcements).toEqual([]);
    expect(result.current.error).toBeNull();
    expect(result.current.isEmpty).toBe(false);
  });

  it('should update animals state when loadAnimals succeeds', async () => {
    // given
    const mockAnimals = [
      {
        id: '1',
        petName: 'Fluffy',
        species: 'CAT',
        breed: 'Maine Coon',
        locationLatitude: 52.0,
        locationLongitude: 21.0,
        sex: 'MALE',
        status: 'MISSING',
        lastSeenDate: '2025-11-18',
        description: 'Test',
        email: null,
        phone: null,
        photoUrl: 'placeholder',
        age: null,
        microchipNumber: null,
        reward: null,
        createdAt: null,
        updatedAt: null
      },
      {
        id: '2',
        petName: 'Rex',
        species: 'DOG',
        breed: 'German Shepherd',
        locationLatitude: 52.2,
        locationLongitude: 21.0,
        sex: 'FEMALE',
        status: 'MISSING',
        lastSeenDate: '2025-11-17',
        description: 'Test',
        email: null,
        phone: null,
        photoUrl: 'placeholder',
        age: null,
        microchipNumber: null,
        reward: null,
        createdAt: null,
        updatedAt: null
      },
      {
        id: '3',
        petName: 'Bella',
        species: 'CAT',
        breed: 'Siamese',
        locationLatitude: 50.0,
        locationLongitude: 19.9,
        sex: 'FEMALE',
        status: 'FOUND',
        lastSeenDate: '2025-11-19',
        description: 'Test',
        email: null,
        phone: null,
        photoUrl: 'placeholder',
        age: null,
        microchipNumber: null,
        reward: null,
        createdAt: null,
        updatedAt: null
      }
    ];

    const mockGetAnnouncements = vi.fn().mockResolvedValue(mockAnimals);
    vi.spyOn(announcementServiceModule.announcementService, 'getAnnouncements').mockImplementation(mockGetAnnouncements);

    // when
    const { result } = renderHook(() => useAnnouncementList());

    // then
    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
    });

    // then
    expect(result.current.announcements).toHaveLength(3);
    expect(result.current.announcements[0].petName).toBe('Fluffy');
    expect(result.current.error).toBeNull();
    expect(result.current.isEmpty).toBe(false);
  });

  it('should set error state when loadAnimals fails', async () => {
    // given
    const mockError = new Error('Network error');
    const mockGetAnnouncements = vi.fn().mockRejectedValue(mockError);
    vi.spyOn(announcementServiceModule.announcementService, 'getAnnouncements').mockImplementation(mockGetAnnouncements);

    // when
    const { result } = renderHook(() => useAnnouncementList());

    // then
    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
    });

    // then
    expect(result.current.announcements).toEqual([]);
    expect(result.current.error).toBe('Network error');
    expect(result.current.isEmpty).toBe(false);
  });

  it('should return isEmpty true when no animals and no error', async () => {
    // given
    const mockGetAnnouncements = vi.fn().mockResolvedValue([]);
    vi.spyOn(announcementServiceModule.announcementService, 'getAnnouncements').mockImplementation(mockGetAnnouncements);

    // when
    const { result } = renderHook(() => useAnnouncementList());

    // then
    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
    });

    // then
    expect(result.current.isEmpty).toBe(true);
    expect(result.current.announcements).toEqual([]);
    expect(result.current.error).toBeNull();
  });
});
