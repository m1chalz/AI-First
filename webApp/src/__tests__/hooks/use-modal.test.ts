import { describe, it, expect } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { useModal } from '../../hooks/use-modal';

describe('useModal', () => {
    it('should initialize with closed state and no selected pet', () => {
        // Given: Hook is initialized
        // When: Hook is rendered
        const { result } = renderHook(() => useModal());
        
        // Then: Modal should be closed and no pet selected
        expect(result.current.isOpen).toBe(false);
        expect(result.current.selectedPetId).toBeNull();
    });
    
    it('should open modal and set selected pet ID when openModal is called', () => {
        // Given: Hook is initialized
        const { result } = renderHook(() => useModal());
        
        // When: openModal is called with a pet ID
        act(() => {
            result.current.openModal('pet-123');
        });
        
        // Then: Modal should be open with selected pet ID
        expect(result.current.isOpen).toBe(true);
        expect(result.current.selectedPetId).toBe('pet-123');
    });
    
    it('should close modal and clear selected pet ID when closeModal is called', () => {
        // Given: Modal is open with a selected pet
        const { result } = renderHook(() => useModal());
        act(() => {
            result.current.openModal('pet-123');
        });
        
        // When: closeModal is called
        act(() => {
            result.current.closeModal();
        });
        
        // Then: Modal should be closed and pet ID cleared
        expect(result.current.isOpen).toBe(false);
        expect(result.current.selectedPetId).toBeNull();
    });
    
    it('should update selected pet ID when openModal is called with different ID', () => {
        // Given: Modal is open with one pet selected
        const { result } = renderHook(() => useModal());
        act(() => {
            result.current.openModal('pet-123');
        });
        
        // When: openModal is called with a different pet ID
        act(() => {
            result.current.openModal('pet-456');
        });
        
        // Then: Selected pet ID should be updated
        expect(result.current.isOpen).toBe(true);
        expect(result.current.selectedPetId).toBe('pet-456');
    });
});

