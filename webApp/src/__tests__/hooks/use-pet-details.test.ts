import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor, act } from '@testing-library/react';
import { usePetDetails } from '../../hooks/use-pet-details';
import * as animalRepositoryModule from '../../services/animal-repository';
import type { Animal } from '../../types/animal';

vi.mock('../../services/animal-repository', () => ({
    animalRepository: {
        getPetById: vi.fn()
    }
}));

describe('usePetDetails', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });
    
    it('should initialize with loading state when pet ID is provided', () => {
        // Given: Mock repository returns a pet
        const mockPet: Animal = {
            id: 'pet-123',
            petName: 'Fluffy',
            photoUrl: 'https://example.com/photo.jpg',
            status: 'MISSING',
            lastSeenDate: '2025-11-18',
            species: 'CAT',
            sex: 'MALE',
            breed: null,
            description: null,
            locationLatitude: null,
            locationLongitude: null,
            phone: null,
            email: null,
            microchipNumber: null,
            age: null,
            reward: null,
            createdAt: null,
            updatedAt: null
        };
        vi.spyOn(animalRepositoryModule.animalRepository, 'getPetById').mockResolvedValue(mockPet);
        
        // When: Hook is initialized with pet ID
        const { result } = renderHook(() => usePetDetails('pet-123'));
        
        // Then: Should be in loading state initially
        expect(result.current.isLoading).toBe(true);
        expect(result.current.pet).toBeNull();
        expect(result.current.error).toBeNull();
    });
    
    it('should load pet details successfully when repository returns data', async () => {
        // Given: Mock repository returns a pet
        const mockPet: Animal = {
            id: 'pet-123',
            petName: 'Fluffy',
            photoUrl: 'https://example.com/photo.jpg',
            status: 'MISSING',
            lastSeenDate: '2025-11-18',
            species: 'CAT',
            sex: 'MALE',
            breed: 'Maine Coon',
            description: 'Friendly cat',
            locationLatitude: 52.0,
            locationLongitude: 21.0,
            phone: '+48 123 456 789',
            email: 'owner@example.com',
            microchipNumber: null,
            age: 5,
            reward: null,
            createdAt: null,
            updatedAt: null
        };
        vi.spyOn(animalRepositoryModule.animalRepository, 'getPetById').mockResolvedValue(mockPet);
        
        // When: Hook fetches pet details
        const { result } = renderHook(() => usePetDetails('pet-123'));
        
        // Then: Should load pet details successfully
        await waitFor(() => {
            expect(result.current.isLoading).toBe(false);
        });
        
        expect(result.current.pet).toEqual(mockPet);
        expect(result.current.error).toBeNull();
    });
    
    it('should handle error state when repository throws error', async () => {
        // Given: Mock repository throws an error
        const mockError = new Error('Pet not found');
        vi.spyOn(animalRepositoryModule.animalRepository, 'getPetById').mockRejectedValue(mockError);
        
        // When: Hook fetches pet details
        const { result } = renderHook(() => usePetDetails('pet-123'));
        
        // Then: Should handle error state
        await waitFor(() => {
            expect(result.current.isLoading).toBe(false);
        });
        
        expect(result.current.pet).toBeNull();
        expect(result.current.error).toBe('Failed to load pet details');
    });
    
    it('should retry loading pet details when retry is called', async () => {
        // Given: Mock repository fails first, then succeeds
        const mockPet: Animal = {
            id: 'pet-123',
            petName: 'Fluffy',
            photoUrl: 'https://example.com/photo.jpg',
            status: 'MISSING',
            lastSeenDate: '2025-11-18',
            species: 'CAT',
            sex: 'MALE',
            breed: null,
            description: null,
            locationLatitude: null,
            locationLongitude: null,
            phone: null,
            email: null,
            microchipNumber: null,
            age: null,
            reward: null,
            createdAt: null,
            updatedAt: null
        };
        const getPetByIdSpy = vi.spyOn(animalRepositoryModule.animalRepository, 'getPetById')
            .mockRejectedValueOnce(new Error('Network error'))
            .mockResolvedValueOnce(mockPet);
        
        // When: Hook fetches pet details, fails, then retries
        const { result } = renderHook(() => usePetDetails('pet-123'));
        
        await waitFor(() => {
            expect(result.current.isLoading).toBe(false);
        }, { timeout: 3000 });
        
        expect(result.current.error).toBe('Failed to load pet details');
        
        // When: Retry is called
        act(() => {
            result.current.retry();
        });
        
        await waitFor(() => {
            expect(result.current.isLoading).toBe(false);
        }, { timeout: 3000 });
        
        // Then: Should retry and succeed
        expect(result.current.pet).toEqual(mockPet);
        expect(result.current.error).toBeNull();
        expect(getPetByIdSpy).toHaveBeenCalledTimes(2);
    });
});

