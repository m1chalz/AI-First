import { useState, useEffect, useCallback } from 'react';
import { useGeolocationContext } from '../contexts/GeolocationContext';
import { announcementService } from '../services/announcement-service';
import type { Announcement } from '../types/announcement';

interface UseAnnouncementListResult {
  announcements: Announcement[];
  isLoading: boolean;
  error: string | null;
  isEmpty: boolean;
  loadAnnouncements: () => Promise<void>;
  geolocationError: GeolocationPositionError | null;
}

export function useAnnouncementList(): UseAnnouncementListResult {
  const [announcements, setAnnouncements] = useState<Announcement[]>([]);
  const [isFetchingAnnouncements, setIsFetchingAnnouncements] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { state: geolocation } = useGeolocationContext();

  const isLoading = geolocation.isLoading || isFetchingAnnouncements;
  const isEmpty = announcements.length === 0 && !isLoading && error === null;

  const loadAnnouncements = useCallback(async () => {
    setIsFetchingAnnouncements(true);
    setError(null);

    try {
      const result = await announcementService.getAnnouncements(geolocation.coordinates);
      setAnnouncements(result);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    } finally {
      setIsFetchingAnnouncements(false);
    }
  }, [geolocation.coordinates]);

  // Load announcements when geolocation finishes loading
  useEffect(() => {
    if (!geolocation.isLoading && geolocation.permissionCheckCompleted) {
      loadAnnouncements();
    }
  }, [loadAnnouncements, geolocation.isLoading, geolocation.permissionCheckCompleted]);

  return {
    announcements,
    isLoading,
    error,
    isEmpty,
    loadAnnouncements,
    geolocationError: null
  };
}

// Backward compatibility alias
export function useAnimalList(): UseAnnouncementListResult {
  return useAnnouncementList();
}
