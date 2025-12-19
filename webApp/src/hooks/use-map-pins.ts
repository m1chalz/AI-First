import { useState, useEffect } from 'react';
import type { Coordinates } from '../types/location';
import type { Announcement, AnnouncementStatus } from '../types/announcement';
import { announcementService } from '../services/announcement-service';

type MapPinStatus = Exclude<AnnouncementStatus, 'CLOSED'>;

export interface MapPin {
  id: string;
  name: string;
  species: string;
  status: MapPinStatus;
  latitude: number;
  longitude: number;
  photoUrl: string;
  phoneNumber: string;
  email: string;
  createdAt: string;
}

interface MapPinsState {
  pins: MapPin[];
  loading: boolean;
  error: Error | null;
}

function transformToMapPin(announcement: Announcement): MapPin {
  return {
    id: announcement.id,
    name: announcement.petName ?? '',
    species: announcement.species,
    status: announcement.status as MapPinStatus,
    latitude: announcement.locationLatitude,
    longitude: announcement.locationLongitude,
    photoUrl: announcement.photoUrl,
    phoneNumber: announcement.phone,
    email: announcement.email,
    createdAt: announcement.createdAt ?? ''
  };
}

export function useMapPins(userLocation: Coordinates | null): MapPinsState {
  const [pins, setPins] = useState<MapPin[]>([]);
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
        const mapPins = announcements
          .filter(a => a.status !== 'CLOSED')
          .map(transformToMapPin);
        setPins(mapPins);
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
