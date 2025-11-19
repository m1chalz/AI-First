import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { useAnimalList } from '../../hooks/useAnimalList';
import * as animalRepositoryModule from '../../services/animalRepository';

/**
 * Unit tests for useAnimalList hook.
 * Tests state updates, loading states, and error handling.
 * Follows Given-When-Then structure per project constitution.
 */

// Mock the animal repository
vi.mock('../../services/animalRepository', () => ({
    animalRepository: {
        getAnimals: vi.fn()
    }
}));

describe('useAnimalList', () => {
    
    beforeEach(() => {
        // Reset mocks before each test
        vi.clearAllMocks();
    });
    
    // MARK: - Test Initial State
    
    it('should initialize with empty state', () => {
        // Given - fresh hook
        const mockGetAnimals = vi.fn().mockResolvedValue([]);
        vi.spyOn(animalRepositoryModule.animalRepository, 'getAnimals').mockImplementation(mockGetAnimals);
        
        // When - hook is rendered
        const { result } = renderHook(() => useAnimalList());
        
        // Then - initial state should be loading
        expect(result.current.isLoading).toBe(true);
        expect(result.current.animals).toEqual([]);
        expect(result.current.error).toBeNull();
        expect(result.current.isEmpty).toBe(false); // Not empty while loading
    });
    
    // MARK: - Test loadAnimals Success
    
    it('should update animals state when loadAnimals succeeds', async () => {
        // Given - mock repository returning 3 animals
        const mockAnimals = [
            { id: '1', name: 'Fluffy', species: 'CAT' as any, breed: 'Maine Coon', location: { city: 'Pruszkow', radiusKm: 5 }, gender: 'MALE' as any, status: 'ACTIVE' as any, lastSeenDate: '18/11/2025', description: 'Test', email: null, phone: null, photoUrl: 'placeholder' },
            { id: '2', name: 'Rex', species: 'DOG' as any, breed: 'German Shepherd', location: { city: 'Warsaw', radiusKm: 10 }, gender: 'FEMALE' as any, status: 'ACTIVE' as any, lastSeenDate: '17/11/2025', description: 'Test', email: null, phone: null, photoUrl: 'placeholder' },
            { id: '3', name: 'Bella', species: 'CAT' as any, breed: 'Siamese', location: { city: 'Krakow', radiusKm: 3 }, gender: 'FEMALE' as any, status: 'FOUND' as any, lastSeenDate: '19/11/2025', description: 'Test', email: null, phone: null, photoUrl: 'placeholder' }
        ];
        
        const mockGetAnimals = vi.fn().mockResolvedValue(mockAnimals);
        vi.spyOn(animalRepositoryModule.animalRepository, 'getAnimals').mockImplementation(mockGetAnimals);
        
        // When - hook loads animals
        const { result } = renderHook(() => useAnimalList());
        
        // Then - wait for loading to complete and verify animals state
        await waitFor(() => {
            expect(result.current.isLoading).toBe(false);
        });
        
        expect(result.current.animals).toHaveLength(3);
        expect(result.current.animals[0].name).toBe('Fluffy');
        expect(result.current.error).toBeNull();
        expect(result.current.isEmpty).toBe(false);
    });
    
    // MARK: - Test loadAnimals Failure
    
    it('should set error state when loadAnimals fails', async () => {
        // Given - mock repository throwing error
        const mockError = new Error('Network error');
        const mockGetAnimals = vi.fn().mockRejectedValue(mockError);
        vi.spyOn(animalRepositoryModule.animalRepository, 'getAnimals').mockImplementation(mockGetAnimals);
        
        // When - hook attempts to load animals
        const { result } = renderHook(() => useAnimalList());
        
        // Then - wait for loading to complete and verify error state
        await waitFor(() => {
            expect(result.current.isLoading).toBe(false);
        });
        
        expect(result.current.animals).toEqual([]);
        expect(result.current.error).toBe('Network error');
        expect(result.current.isEmpty).toBe(false); // Not empty when error occurred
    });
    
    // MARK: - Test isEmpty Derived State
    
    it('should return isEmpty true when no animals and no error', async () => {
        // Given - mock repository returning empty array
        const mockGetAnimals = vi.fn().mockResolvedValue([]);
        vi.spyOn(animalRepositoryModule.animalRepository, 'getAnimals').mockImplementation(mockGetAnimals);
        
        // When - hook loads empty animals list
        const { result } = renderHook(() => useAnimalList());
        
        // Then - wait for loading to complete and verify isEmpty
        await waitFor(() => {
            expect(result.current.isLoading).toBe(false);
        });
        
        expect(result.current.isEmpty).toBe(true);
        expect(result.current.animals).toEqual([]);
        expect(result.current.error).toBeNull();
    });
    
    // MARK: - Test selectAnimal Method
    
    it('should call console.log when selectAnimal is invoked', () => {
        // Given - hook with mocked console.log
        const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
        const mockGetAnimals = vi.fn().mockResolvedValue([]);
        vi.spyOn(animalRepositoryModule.animalRepository, 'getAnimals').mockImplementation(mockGetAnimals);
        
        const { result } = renderHook(() => useAnimalList());
        
        // When - selectAnimal is called
        result.current.selectAnimal('animal-123');
        
        // Then - should log navigation (mocked)
        expect(consoleSpy).toHaveBeenCalledWith('Navigate to animal details:', 'animal-123');
        
        consoleSpy.mockRestore();
    });
    
    // MARK: - Test reportMissing Method
    
    it('should call console.log when reportMissing is invoked', () => {
        // Given - hook with mocked console.log
        const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
        const mockGetAnimals = vi.fn().mockResolvedValue([]);
        vi.spyOn(animalRepositoryModule.animalRepository, 'getAnimals').mockImplementation(mockGetAnimals);
        
        const { result } = renderHook(() => useAnimalList());
        
        // When - reportMissing is called
        result.current.reportMissing();
        
        // Then - should log navigation (mocked)
        expect(consoleSpy).toHaveBeenCalledWith('Navigate to report missing form');
        
        consoleSpy.mockRestore();
    });
    
    // MARK: - Test reportFound Method
    
    it('should call console.log when reportFound is invoked', () => {
        // Given - hook with mocked console.log
        const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
        const mockGetAnimals = vi.fn().mockResolvedValue([]);
        vi.spyOn(animalRepositoryModule.animalRepository, 'getAnimals').mockImplementation(mockGetAnimals);
        
        const { result } = renderHook(() => useAnimalList());
        
        // When - reportFound is called
        result.current.reportFound();
        
        // Then - should log navigation (mocked)
        expect(consoleSpy).toHaveBeenCalledWith('Navigate to report found form');
        
        consoleSpy.mockRestore();
    });
});
