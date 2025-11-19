import { useState, useEffect } from 'react';
import { animalRepository } from '../services/animalRepository';
import type { Animal } from '../../../shared/build/js/packages/shared/kotlin/shared';

/**
 * Hook state interface for Animal List component.
 * Encapsulates loading, data, and error states with actions.
 */
interface UseAnimalListResult {
    animals: Animal[];
    isLoading: boolean;
    error: string | null;
    isEmpty: boolean;
    loadAnimals: () => Promise<void>;
    selectAnimal: (id: string) => void;
    reportMissing: () => void;
    reportFound: () => void;
}

/**
 * Custom hook for Animal List state management.
 * Encapsulates business logic and state for displaying animals.
 * 
 * Features:
 * - Automatic data loading on mount
 * - Loading, error, and empty state management
 * - Navigation actions (mocked for now)
 * 
 * @returns Hook state and actions
 */
export function useAnimalList(): UseAnimalListResult {
    const [animals, setAnimals] = useState<Animal[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    
    /**
     * Computed: true when data loaded but list is empty.
     * Distinguishes empty state from loading or error states.
     */
    const isEmpty = animals.length === 0 && !isLoading && error === null;
    
    /**
     * Loads animals from repository.
     * Updates state (animals, isLoading, error).
     * Called automatically on mount and can be called manually to refresh.
     */
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
    
    /**
     * Handles animal selection.
     * Triggers navigation to animal details (mocked for now).
     * 
     * @param id - ID of selected animal
     */
    const selectAnimal = (id: string) => {
        // Mocked navigation - will use React Router navigate in future
        console.log('Navigate to animal details:', id);
        // Future: navigate(`/animal/${id}`);
    };
    
    /**
     * Handles "Report a Missing Animal" action.
     * Triggers navigation to report form (mocked for now).
     */
    const reportMissing = () => {
        // Mocked navigation - will use React Router navigate in future
        console.log('Navigate to report missing form');
        // Future: navigate('/report-missing');
    };
    
    /**
     * Handles "Report Found Animal" action.
     * Triggers navigation to report form (mocked for now).
     * Note: Exposed in web UI per Figma design (two buttons at top-right).
     */
    const reportFound = () => {
        // Mocked navigation - will use React Router navigate in future
        console.log('Navigate to report found form');
        // Future: navigate('/report-found');
    };
    
    // Load animals on mount
    useEffect(() => {
        loadAnimals();
    }, []);
    
    return {
        animals,
        isLoading,
        error,
        isEmpty,
        loadAnimals,
        selectAnimal,
        reportMissing,
        reportFound
    };
}

