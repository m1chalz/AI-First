import { describe, it, expect, beforeEach, vi } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { useModal } from '../../hooks/use-modal';

// Mock useNavigate
const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
    const actual = await vi.importActual('react-router-dom');
    return {
        ...actual,
        useNavigate: () => mockNavigate,
    };
});

const wrapper = ({ children }: { children: React.ReactNode }) => (
    <BrowserRouter>{children}</BrowserRouter>
);

describe('useModal', () => {
    beforeEach(() => {
        mockNavigate.mockClear();
    });

    it('should initialize with closed state and no selected pet', () => {
        // Given: Hook is initialized
        // When: Hook is rendered without route params
        const { result } = renderHook(() => useModal(), { wrapper });
        
        // Then: Modal should be closed and no pet selected
        expect(result.current.isOpen).toBe(false);
        expect(result.current.selectedPetId).toBeNull();
    });
    
    it('should open modal and navigate when openModal is called', () => {
        // Given: Hook is initialized
        const { result } = renderHook(() => useModal(), { wrapper });
        
        // When: openModal is called with a pet ID
        act(() => {
            result.current.openModal('pet-123');
        });
        
        // Then: Should navigate to announcement URL
        expect(mockNavigate).toHaveBeenCalledWith('/announcement/pet-123');
    });
    
    it('should close modal and navigate to home when closeModal is called', () => {
        // Given: Hook is initialized
        const { result } = renderHook(() => useModal(), { wrapper });
        
        // When: closeModal is called
        act(() => {
            result.current.closeModal();
        });
        
        // Then: Should navigate to home
        expect(mockNavigate).toHaveBeenCalledWith('/');
    });
    
    it('should update navigation when openModal is called with different ID', () => {
        // Given: Hook is initialized
        const { result } = renderHook(() => useModal(), { wrapper });
        
        // When: openModal is called twice with different pet IDs
        act(() => {
            result.current.openModal('pet-123');
        });
        act(() => {
            result.current.openModal('pet-456');
        });
        
        // Then: Should navigate to both announcement URLs
        expect(mockNavigate).toHaveBeenNthCalledWith(1, '/announcement/pet-123');
        expect(mockNavigate).toHaveBeenNthCalledWith(2, '/announcement/pet-456');
    });
});

