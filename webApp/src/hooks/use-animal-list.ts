import { useState, useEffect, useCallback } from 'react';
import { animalRepository } from '../services/animal-repository';
import { useGeolocation } from './use-geolocation';
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
    const geolocation = useGeolocation();
    
    // isLoading is true when either waiting for geolocation or fetching animals
    const isLoading = geolocation.isLoading || isFetchingAnimals;
    
    const isEmpty = animals.length === 0 && !isLoading && error === null;
    
    const loadAnimals = useCallback(async () => {
        setIsFetchingAnimals(true);
        setError(null);
        
        try {
            const result = await animalRepository.getAnimals(geolocation.coordinates);
            setAnimals(result);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Unknown error');
        } finally {
            setIsFetchingAnimals(false);
        }
    }, [geolocation.coordinates]);
    
    useEffect(() => {
        // Wait for geolocation to finish loading before fetching animals
        if (!geolocation.isLoading) {
            loadAnimals();
        }
    }, [loadAnimals, geolocation.isLoading]);
    
    return {
        animals,
        isLoading,
        error,
        isEmpty,
        loadAnimals,
        geolocationError: geolocation.error
    };
}

