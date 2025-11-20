import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { useAnimalList } from '../../hooks/useAnimalList';
import * as animalRepositoryModule from '../../services/animalRepository';

vi.mock('../../services/animalRepository', () => ({
    animalRepository: {
        getAnimals: vi.fn()
    }
}));

describe('useAnimalList', () => {
    
    beforeEach(() => {
        vi.clearAllMocks();
    });
    
    it('should initialize with empty state', () => {
        // Given
        const mockGetAnimals = vi.fn().mockResolvedValue([]);
        vi.spyOn(animalRepositoryModule.animalRepository, 'getAnimals').mockImplementation(mockGetAnimals);
        
        // When
        const { result } = renderHook(() => useAnimalList());
        
        // Then
        expect(result.current.isLoading).toBe(true);
        expect(result.current.animals).toEqual([]);
        expect(result.current.error).toBeNull();
        expect(result.current.isEmpty).toBe(false);
    });
    
    it('should update animals state when loadAnimals succeeds', async () => {
        // Given
        const mockAnimals = [
            { id: '1', name: 'Fluffy', species: 'CAT' as any, breed: 'Maine Coon', location: { city: 'Pruszkow', radiusKm: 5 }, gender: 'MALE' as any, status: 'ACTIVE' as any, lastSeenDate: '18/11/2025', description: 'Test', email: null, phone: null, photoUrl: 'placeholder' },
            { id: '2', name: 'Rex', species: 'DOG' as any, breed: 'German Shepherd', location: { city: 'Warsaw', radiusKm: 10 }, gender: 'FEMALE' as any, status: 'ACTIVE' as any, lastSeenDate: '17/11/2025', description: 'Test', email: null, phone: null, photoUrl: 'placeholder' },
            { id: '3', name: 'Bella', species: 'CAT' as any, breed: 'Siamese', location: { city: 'Krakow', radiusKm: 3 }, gender: 'FEMALE' as any, status: 'FOUND' as any, lastSeenDate: '19/11/2025', description: 'Test', email: null, phone: null, photoUrl: 'placeholder' }
        ];
        
        const mockGetAnimals = vi.fn().mockResolvedValue(mockAnimals);
        vi.spyOn(animalRepositoryModule.animalRepository, 'getAnimals').mockImplementation(mockGetAnimals);
        
        // When
        const { result } = renderHook(() => useAnimalList());
        
        // Then
        await waitFor(() => {
            expect(result.current.isLoading).toBe(false);
        });
        
        expect(result.current.animals).toHaveLength(3);
        expect(result.current.animals[0].name).toBe('Fluffy');
        expect(result.current.error).toBeNull();
        expect(result.current.isEmpty).toBe(false);
    });
    
    it('should set error state when loadAnimals fails', async () => {
        // Given
        const mockError = new Error('Network error');
        const mockGetAnimals = vi.fn().mockRejectedValue(mockError);
        vi.spyOn(animalRepositoryModule.animalRepository, 'getAnimals').mockImplementation(mockGetAnimals);
        
        // When
        const { result } = renderHook(() => useAnimalList());
        
        // Then
        await waitFor(() => {
            expect(result.current.isLoading).toBe(false);
        });
        
        expect(result.current.animals).toEqual([]);
        expect(result.current.error).toBe('Network error');
        expect(result.current.isEmpty).toBe(false);
    });
    
    it('should return isEmpty true when no animals and no error', async () => {
        // Given
        const mockGetAnimals = vi.fn().mockResolvedValue([]);
        vi.spyOn(animalRepositoryModule.animalRepository, 'getAnimals').mockImplementation(mockGetAnimals);
        
        // When
        const { result } = renderHook(() => useAnimalList());
        
        // Then
        await waitFor(() => {
            expect(result.current.isLoading).toBe(false);
        });
        
        expect(result.current.isEmpty).toBe(true);
        expect(result.current.animals).toEqual([]);
        expect(result.current.error).toBeNull();
    });
    
    it('should call console.log when selectAnimal is invoked', () => {
        // Given
        const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
        const mockGetAnimals = vi.fn().mockResolvedValue([]);
        vi.spyOn(animalRepositoryModule.animalRepository, 'getAnimals').mockImplementation(mockGetAnimals);
        
        const { result } = renderHook(() => useAnimalList());
        
        // When
        result.current.selectAnimal('animal-123');
        
        // Then
        expect(consoleSpy).toHaveBeenCalledWith('Navigate to animal details:', 'animal-123');
        
        consoleSpy.mockRestore();
    });
    
    it('should call console.log when reportMissing is invoked', () => {
        // Given
        const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
        const mockGetAnimals = vi.fn().mockResolvedValue([]);
        vi.spyOn(animalRepositoryModule.animalRepository, 'getAnimals').mockImplementation(mockGetAnimals);
        
        const { result } = renderHook(() => useAnimalList());
        
        // When
        result.current.reportMissing();
        
        // Then
        expect(consoleSpy).toHaveBeenCalledWith('Navigate to report missing form');
        
        consoleSpy.mockRestore();
    });
    
    it('should call console.log when reportFound is invoked', () => {
        // Given
        const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
        const mockGetAnimals = vi.fn().mockResolvedValue([]);
        vi.spyOn(animalRepositoryModule.animalRepository, 'getAnimals').mockImplementation(mockGetAnimals);
        
        const { result } = renderHook(() => useAnimalList());
        
        // When
        result.current.reportFound();
        
        // Then
        expect(consoleSpy).toHaveBeenCalledWith('Navigate to report found form');
        
        consoleSpy.mockRestore();
    });
});
