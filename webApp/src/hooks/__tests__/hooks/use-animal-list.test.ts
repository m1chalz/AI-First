import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { useAnimalList } from '../../use-animal-list';
import * as animalRepositoryModule from '../../../services/animal-repository';

vi.mock('../../../services/animal-repository', () => ({
    animalRepository: {
        getAnimals: vi.fn()
    }
}));

vi.mock('../../../contexts/GeolocationContext', () => ({
    useGeolocationContext: vi.fn(() => ({
        state: {
            coordinates: null,
            error: null,
            isLoading: false,
            permissionCheckCompleted: true,
        },
    }))
}));

describe('useAnimalList', () => {
    
    beforeEach(() => {
        vi.clearAllMocks();
    });
    
    it('should initialize with empty state', () => {
        // given
        const mockGetAnimals = vi.fn().mockResolvedValue([]);
        vi.spyOn(animalRepositoryModule.animalRepository, 'getAnimals').mockImplementation(mockGetAnimals);
        
        // when
        const { result } = renderHook(() => useAnimalList());
        
        // then
        expect(result.current.isLoading).toBe(true);
        expect(result.current.animals).toEqual([]);
        expect(result.current.error).toBeNull();
        expect(result.current.isEmpty).toBe(false);
    });
    
    it('should update animals state when loadAnimals succeeds', async () => {
        // given
        const mockAnimals = [
            { id: '1', petName: 'Fluffy', species: 'CAT', breed: 'Maine Coon', locationLatitude: 52.0, locationLongitude: 21.0, sex: 'MALE', status: 'MISSING', lastSeenDate: '2025-11-18', description: 'Test', email: null, phone: null, photoUrl: 'placeholder', age: null, microchipNumber: null, reward: null, createdAt: null, updatedAt: null },
            { id: '2', petName: 'Rex', species: 'DOG', breed: 'German Shepherd', locationLatitude: 52.2, locationLongitude: 21.0, sex: 'FEMALE', status: 'MISSING', lastSeenDate: '2025-11-17', description: 'Test', email: null, phone: null, photoUrl: 'placeholder', age: null, microchipNumber: null, reward: null, createdAt: null, updatedAt: null },
            { id: '3', petName: 'Bella', species: 'CAT', breed: 'Siamese', locationLatitude: 50.0, locationLongitude: 19.9, sex: 'FEMALE', status: 'FOUND', lastSeenDate: '2025-11-19', description: 'Test', email: null, phone: null, photoUrl: 'placeholder', age: null, microchipNumber: null, reward: null, createdAt: null, updatedAt: null }
        ];
        
        const mockGetAnimals = vi.fn().mockResolvedValue(mockAnimals);
        vi.spyOn(animalRepositoryModule.animalRepository, 'getAnimals').mockImplementation(mockGetAnimals);
        
        // when
        const { result } = renderHook(() => useAnimalList());
        
        // then
        await waitFor(() => {
            expect(result.current.isLoading).toBe(false);
        });
        
        expect(result.current.animals).toHaveLength(3);
        expect(result.current.animals[0].petName).toBe('Fluffy');
        expect(result.current.error).toBeNull();
        expect(result.current.isEmpty).toBe(false);
    });
    
    it('should set error state when loadAnimals fails', async () => {
        // given
        const mockError = new Error('Network error');
        const mockGetAnimals = vi.fn().mockRejectedValue(mockError);
        vi.spyOn(animalRepositoryModule.animalRepository, 'getAnimals').mockImplementation(mockGetAnimals);
        
        // when
        const { result } = renderHook(() => useAnimalList());
        
        // then
        await waitFor(() => {
            expect(result.current.isLoading).toBe(false);
        });
        
        expect(result.current.animals).toEqual([]);
        expect(result.current.error).toBe('Network error');
        expect(result.current.isEmpty).toBe(false);
    });
    
    it('should return isEmpty true when no animals and no error', async () => {
        // given
        const mockGetAnimals = vi.fn().mockResolvedValue([]);
        vi.spyOn(animalRepositoryModule.animalRepository, 'getAnimals').mockImplementation(mockGetAnimals);
        
        // when
        const { result } = renderHook(() => useAnimalList());
        
        // then
        await waitFor(() => {
            expect(result.current.isLoading).toBe(false);
        });
        
        expect(result.current.isEmpty).toBe(true);
        expect(result.current.animals).toEqual([]);
        expect(result.current.error).toBeNull();
    });

});
