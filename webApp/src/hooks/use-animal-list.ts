import { useState, useEffect } from 'react';
import { animalRepository } from '../services/animal-repository';
import { useGeolocation } from './use-geolocation';
import type { Animal } from '../types/animal';

interface UseAnimalListResult {
    animals: Animal[];
    isLoading: boolean;
    error: string | null;
    isEmpty: boolean;
    loadAnimals: () => Promise<void>;
}

export function useAnimalList(): UseAnimalListResult {
    const [animals, setAnimals] = useState<Animal[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const geolocation = useGeolocation();
    
    const isEmpty = animals.length === 0 && !isLoading && error === null;
    
    const loadAnimals = async () => {
        setIsLoading(true);
        setError(null);
        
        try {
            const result = await animalRepository.getAnimals(geolocation.coordinates);
            setAnimals(result);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Unknown error');
        } finally {
            setIsLoading(false);
        }
    };
    
    useEffect(() => {
        loadAnimals();
    }, [geolocation.coordinates]);
    
    return {
        animals,
        isLoading,
        error,
        isEmpty,
        loadAnimals,
    };
}

