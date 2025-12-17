import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';

const mockGetAnnouncementById = vi.fn();

vi.mock('../../services/announcement-service', () => ({
  announcementService: {
    getAnnouncementById: (...args: unknown[]) => mockGetAnnouncementById(...args)
  }
}));

import { useAnnouncementDetails } from '../use-pet-details';

describe('useAnnouncementDetails', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should return initial state when announcementId is null', () => {
    // Given/When
    const { result } = renderHook(() => useAnnouncementDetails(null));

    // Then
    expect(result.current.announcement).toBeNull();
    expect(result.current.isLoading).toBe(false);
    expect(result.current.error).toBeNull();
  });

  it('should load announcement details when id is provided', async () => {
    // Given
    const mockAnnouncement = {
      id: 'pet-123',
      petName: 'Buddy',
      description: 'Lost golden retriever'
    };
    mockGetAnnouncementById.mockResolvedValue(mockAnnouncement);

    // When
    const { result } = renderHook(() => useAnnouncementDetails('pet-123'));

    // Then
    expect(result.current.isLoading).toBe(true);

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
    });

    expect(result.current.announcement).toEqual(mockAnnouncement);
    expect(result.current.error).toBeNull();
    expect(mockGetAnnouncementById).toHaveBeenCalledWith('pet-123');
  });

  it('should set error when loading fails', async () => {
    // Given
    mockGetAnnouncementById.mockRejectedValue(new Error('Network error'));

    // When
    const { result } = renderHook(() => useAnnouncementDetails('pet-456'));

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
    });

    // Then
    expect(result.current.error).toBe('Failed to load announcement details');
    expect(result.current.announcement).toBeNull();
  });

  it('should reset state when id changes to null', async () => {
    // Given
    const mockAnnouncement = { id: 'pet-123', petName: 'Buddy' };
    mockGetAnnouncementById.mockResolvedValue(mockAnnouncement);

    const { result, rerender } = renderHook(
      ({ id }) => useAnnouncementDetails(id),
      { initialProps: { id: 'pet-123' as string | null } }
    );

    await waitFor(() => {
      expect(result.current.announcement).toEqual(mockAnnouncement);
    });

    // When
    rerender({ id: null });

    // Then
    expect(result.current.announcement).toBeNull();
    expect(result.current.isLoading).toBe(false);
    expect(result.current.error).toBeNull();
  });

  it('should provide retry function', async () => {
    // Given
    mockGetAnnouncementById.mockResolvedValue({ id: 'pet-123', petName: 'Buddy' });

    const { result } = renderHook(() => useAnnouncementDetails('pet-123'));

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
    });

    // Then
    expect(typeof result.current.retry).toBe('function');
  });
});

