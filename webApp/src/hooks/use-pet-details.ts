import { useState, useEffect, useCallback } from 'react';
import { announcementService } from '../services/announcement-service';
import type { Animal } from '../types/animal';

export interface UsePetDetailsResult {
    pet: Animal | null;
    isLoading: boolean;
    error: string | null;
    retry: () => void;
}

const TIMEOUT_MS = 10000;

export function usePetDetails(petId: string | null): UsePetDetailsResult {
    const [pet, setPet] = useState<Animal | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    
    const loadPetDetails = useCallback(async () => {
        if (!petId) {
            setPet(null);
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
            
            const petPromise = announcementService.getPetById(petId);
            
            const petData = await Promise.race([petPromise, timeoutPromise]);
            setPet(petData);
            setError(null);
        } catch {
            setPet(null);
            setError('Failed to load pet details');
        } finally {
            setIsLoading(false);
        }
    }, [petId]);
    
    useEffect(() => {
        loadPetDetails();
    }, [loadPetDetails]);
    
    return {
        pet,
        isLoading,
        error,
        retry: loadPetDetails
    };
}

