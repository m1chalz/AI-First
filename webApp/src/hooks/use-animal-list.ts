import { useState, useEffect } from 'react';
import { animalRepository } from '../services/animal-repository';
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
    
    const isEmpty = animals.length === 0 && !isLoading && error === null;
    
    const loadAnimals = async () => {
        setIsLoading(true);
        setError(null);
        
        try {
            const result = await animalRepository.getAnimals();
            setAnimals(result);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Unknown error');
        } finally {
            setIsLoading(false);
        }
    };
    
    useEffect(() => {
        loadAnimals();
    }, []);
    
    return {
        animals,
        isLoading,
        error,
        isEmpty,
        loadAnimals,
    };
}

