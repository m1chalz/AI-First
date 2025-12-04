import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor, act } from '@testing-library/react';
import { usePetDetails } from '../../hooks/use-pet-details';
import * as announcementServiceModule from '../../services/announcement-service';
import type { Animal } from '../../types/animal';

vi.mock('../../services/announcement-service', () => ({
    announcementService: {
        getPetById: vi.fn()
    }
}));

describe('usePetDetails', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });
    
    it('should initialize with loading state when pet ID is provided', () => {
        // given
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
        vi.spyOn(announcementServiceModule.announcementService, 'getPetById').mockResolvedValue(mockPet);
        
        // when
        const { result } = renderHook(() => usePetDetails('pet-123'));
        
        // then
        expect(result.current.isLoading).toBe(true);
        expect(result.current.pet).toBeNull();
        expect(result.current.error).toBeNull();
    });
    
    it('should load pet details successfully when repository returns data', async () => {
        // given
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
        vi.spyOn(announcementServiceModule.announcementService, 'getPetById').mockResolvedValue(mockPet);
        
        // when
        const { result } = renderHook(() => usePetDetails('pet-123'));
        
        await waitFor(() => {
            expect(result.current.isLoading).toBe(false);
        });
        
        // then
        expect(result.current.pet).toEqual(mockPet);
        expect(result.current.error).toBeNull();
    });
    
    it('should handle error state when repository throws error', async () => {
        // given
        const mockError = new Error('Pet not found');
        vi.spyOn(announcementServiceModule.announcementService, 'getPetById').mockRejectedValue(mockError);
        
        // when
        const { result } = renderHook(() => usePetDetails('pet-123'));
        
        await waitFor(() => {
            expect(result.current.isLoading).toBe(false);
        });
        
        // then
        expect(result.current.pet).toBeNull();
        expect(result.current.error).toBe('Failed to load pet details');
    });
    
    it('should retry loading pet details when retry is called', async () => {
        // given
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
        const getPetByIdSpy = vi.spyOn(announcementServiceModule.announcementService, 'getPetById')
            .mockRejectedValueOnce(new Error('Network error'))
            .mockResolvedValueOnce(mockPet);
        
        // when
        const { result } = renderHook(() => usePetDetails('pet-123'));
        
        await waitFor(() => {
            expect(result.current.isLoading).toBe(false);
        }, { timeout: 3000 });
        
        expect(result.current.error).toBe('Failed to load pet details');
        
        act(() => {
            result.current.retry();
        });
        
        await waitFor(() => {
            expect(result.current.isLoading).toBe(false);
        }, { timeout: 3000 });
        
        // then
        expect(result.current.pet).toEqual(mockPet);
        expect(result.current.error).toBeNull();
        expect(getPetByIdSpy).toHaveBeenCalledTimes(2);
    });
});

