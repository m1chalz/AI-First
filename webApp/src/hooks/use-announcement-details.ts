import { useState, useEffect, useCallback } from 'react';
import { announcementService } from '../services/announcement-service';
import type { Announcement } from '../types/animal';

export interface UseAnnouncementDetailsResult {
  announcement: Announcement | null;
  isLoading: boolean;
  error: string | null;
  retry: () => void;
}

const TIMEOUT_MS = 10000;

export function useAnnouncementDetails(announcementId: string | null): UseAnnouncementDetailsResult {
  const [announcement, setAnnouncement] = useState<Announcement | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadAnnouncementDetails = useCallback(async () => {
    if (!announcementId) {
      setAnnouncement(null);
      setIsLoading(false);
      setError(null);
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      const timeoutPromise = new Promise<never>((_, reject) => {
        setTimeout(() => reject(new Error('Request timeout')), TIMEOUT_MS);
      });

      const announcementPromise = announcementService.getAnnouncementById(announcementId);

      const announcementData = await Promise.race([announcementPromise, timeoutPromise]);
      setAnnouncement(announcementData);
      setError(null);
    } catch {
      setAnnouncement(null);
      setError('Failed to load announcement details');
    } finally {
      setIsLoading(false);
    }
  }, [announcementId]);

  useEffect(() => {
    loadAnnouncementDetails();
  }, [loadAnnouncementDetails]);

  return {
    announcement,
    isLoading,
    error,
    retry: loadAnnouncementDetails
  };
}

// Backward compatibility alias
export function usePetDetails(petId: string | null): UseAnnouncementDetailsResult {
  return useAnnouncementDetails(petId);
}
