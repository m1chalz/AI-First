import { useState, useEffect } from 'react';
import type { Coordinates } from '../types/location';
import type { Announcement } from '../types/announcement';
import { announcementService } from '../services/announcement-service';

interface MapPinsState {
  pins: Announcement[];
  loading: boolean;
  error: Error | null;
}

export function useMapPins(userLocation: Coordinates | null): MapPinsState {
  const [pins, setPins] = useState<Announcement[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    if (!userLocation) {
      setPins([]);
      setLoading(false);
      setError(null);
      return;
    }

    const fetchPins = async () => {
      setLoading(true);
      setError(null);

      try {
        const announcements = await announcementService.getAnnouncements(userLocation);
        setPins(announcements.filter(a => a.status !== 'CLOSED'));
      } catch (err) {
        setError(err instanceof Error ? err : new Error('Failed to fetch pins'));
        setPins([]);
      } finally {
        setLoading(false);
      }
    };

    fetchPins();
  }, [userLocation]);

  return { pins, loading, error };
}
