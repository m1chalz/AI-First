import { useState, useEffect, useCallback } from 'react';
import { useGeolocationContext } from '../contexts/GeolocationContext';
import { announcementService } from '../services/announcement-service';
import type { Animal } from '../types/animal';

interface UseAnimalListResult {
  animals: Animal[];
  isLoading: boolean;
  error: string | null;
  isEmpty: boolean;
  loadAnimals: () => Promise<void>;
  geolocationError: GeolocationPositionError | null;
}

export function useAnimalList(): UseAnimalListResult {
  const [animals, setAnimals] = useState<Animal[]>([]);
  const [isFetchingAnimals, setIsFetchingAnimals] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { state: geolocation } = useGeolocationContext();

  const isLoading = geolocation.isLoading || isFetchingAnimals;
  const isEmpty = animals.length === 0 && !isLoading && error === null;

  const loadAnimals = useCallback(async () => {
    setIsFetchingAnimals(true);
    setError(null);

    try {
      const result = await announcementService.getAnimals(geolocation.coordinates);
      setAnimals(result);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    } finally {
      setIsFetchingAnimals(false);
    }
  }, [geolocation.coordinates]);

  // Load animals when geolocation finishes loading
  useEffect(() => {
    if (!geolocation.isLoading && geolocation.permissionCheckCompleted) {
      loadAnimals();
    }
  }, [loadAnimals, geolocation.isLoading, geolocation.permissionCheckCompleted]);

  return {
    animals,
    isLoading,
    error,
    isEmpty,
    loadAnimals,
    geolocationError: null
  };
}
