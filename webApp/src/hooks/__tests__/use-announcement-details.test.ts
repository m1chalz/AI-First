import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor, act } from '@testing-library/react';
import { useAnnouncementDetails } from '../use-announcement-details';
import * as announcementServiceModule from '../../services/announcement-service';
import type { Announcement } from '../../types/announcement';

vi.mock('../../services/announcement-service', () => ({
  announcementService: {
    getAnnouncementById: vi.fn()
  }
}));

describe('useAnnouncementDetails', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should initialize with loading state when announcement ID is provided', () => {
    // given
    const mockAnnouncement: Announcement = {
      id: 'pet-123',
      petName: 'Fluffy',
      photoUrl: 'https://example.com/photo.jpg',
      status: 'MISSING',
      lastSeenDate: '2025-11-18',
      species: 'CAT',
      sex: 'MALE',
      breed: null,
      description: null,
      locationLatitude: 1,
      locationLongitude: 1,
      phone: '123',
      email: 'mail@mail.com',
      microchipNumber: null,
      age: null,
      reward: null,
      createdAt: null,
      updatedAt: null
    };
    vi.spyOn(announcementServiceModule.announcementService, 'getAnnouncementById').mockResolvedValue(mockAnnouncement);

    // when
    const { result } = renderHook(() => useAnnouncementDetails('announcement-123'));

    // then
    expect(result.current.isLoading).toBe(true);
    expect(result.current.announcement).toBeNull();
    expect(result.current.error).toBeNull();
  });

  it('should load announcement details successfully when repository returns data', async () => {
    // given
    const mockAnnouncement: Announcement = {
      id: 'pet-123',
      petName: 'Fluffy',
      photoUrl: 'https://example.com/photo.jpg',
      status: 'MISSING',
      lastSeenDate: '2025-11-18',
      species: 'CAT',
      sex: 'MALE',
      breed: 'Maine Coon',
      description: 'Friendly cat',
      locationLatitude: 52.0,
      locationLongitude: 21.0,
      phone: '+48 123 456 789',
      email: 'owner@example.com',
      microchipNumber: null,
      age: 5,
      reward: null,
      createdAt: null,
      updatedAt: null
    };
    vi.spyOn(announcementServiceModule.announcementService, 'getAnnouncementById').mockResolvedValue(mockAnnouncement);

    // when
    const { result } = renderHook(() => useAnnouncementDetails('pet-123'));

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
    });

    // then
    expect(result.current.announcement).toEqual(mockAnnouncement);
    expect(result.current.error).toBeNull();
  });

  it('should handle error state when repository throws error', async () => {
    // given
    const mockError = new Error('Announcement not found');
    vi.spyOn(announcementServiceModule.announcementService, 'getAnnouncementById').mockRejectedValue(mockError);

    // when
    const { result } = renderHook(() => useAnnouncementDetails('pet-123'));

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
    });

    // then
    expect(result.current.announcement).toBeNull();
    expect(result.current.error).toBe('Failed to load announcement details');
  });

  it('should retry loading announcement details when retry is called', async () => {
    // given
    const mockAnnouncement: Announcement = {
      id: 'pet-123',
      petName: 'Fluffy',
      photoUrl: 'https://example.com/photo.jpg',
      status: 'MISSING',
      lastSeenDate: '2025-11-18',
      species: 'CAT',
      sex: 'MALE',
      breed: null,
      description: null,
      locationLatitude: 1,
      locationLongitude: 1,
      phone: '123',
      email: 'mail@mail.com',
      microchipNumber: null,
      age: null,
      reward: null,
      createdAt: null,
      updatedAt: null
    };
    const getAnnouncementByIdSpy = vi
      .spyOn(announcementServiceModule.announcementService, 'getAnnouncementById')
      .mockRejectedValueOnce(new Error('Network error'))
      .mockResolvedValueOnce(mockAnnouncement);

    // when
    const { result } = renderHook(() => useAnnouncementDetails('pet-123'));

    await waitFor(
      () => {
        expect(result.current.isLoading).toBe(false);
      },
      { timeout: 3000 }
    );

    expect(result.current.error).toBe('Failed to load announcement details');

    act(() => {
      result.current.retry();
    });

    await waitFor(
      () => {
        expect(result.current.isLoading).toBe(false);
      },
      { timeout: 3000 }
    );

    // then
    expect(result.current.announcement).toEqual(mockAnnouncement);
    expect(result.current.error).toBeNull();
    expect(getAnnouncementByIdSpy).toHaveBeenCalledTimes(2);
  });
});
