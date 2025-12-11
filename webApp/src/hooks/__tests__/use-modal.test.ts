import { describe, it, expect, beforeEach, vi } from 'vitest';
import { renderHook, act } from '@testing-library/react';

const { mockNavigate, mockUseParams } = vi.hoisted(() => ({
    mockNavigate: vi.fn(),
    mockUseParams: vi.fn(() => ({}))
}));

vi.mock('react-router-dom', () => ({
    useNavigate: () => mockNavigate,
    useParams: mockUseParams
}));

import { useModal } from '../use-modal';

describe('useModal', () => {
    beforeEach(() => {
        mockNavigate.mockClear();
        mockUseParams.mockClear();
        mockUseParams.mockReturnValue({});
    });

    it('should initialize with closed state and no selected pet when no params', () => {
        // Given: No announcementId in route params
        mockUseParams.mockReturnValue({});

        // When: Hook is rendered
        const { result } = renderHook(() => useModal());

        // Then: Modal should be closed
        expect(result.current.isOpen).toBe(false);
        expect(result.current.selectedAnnouncementId).toBeNull();
    });

    it('should open modal and set selected pet when announcementId in params', () => {
        // Given: announcementId in route params
        mockUseParams.mockReturnValue({ announcementId: 'pet-123' });

        // When: Hook is rendered
        const { result } = renderHook(() => useModal());

        // Then: Modal should be open with selected pet
        expect(result.current.isOpen).toBe(true);
        expect(result.current.selectedAnnouncementId).toBe('pet-123');
    });

    it('should navigate to announcement URL when openModal is called', () => {
        // Given: Hook is initialized
        mockUseParams.mockReturnValue({});
        const { result } = renderHook(() => useModal());

        // When: openModal is called with a pet ID
        act(() => {
            result.current.openModal('pet-456');
        });

        // Then: Should navigate to announcement URL
        expect(mockNavigate).toHaveBeenCalledWith('/announcement/pet-456');
    });

    it('should navigate to home when closeModal is called', () => {
        // Given: Hook is initialized with an open modal
        mockUseParams.mockReturnValue({ announcementId: 'pet-123' });
        const { result } = renderHook(() => useModal());

        // When: closeModal is called
        act(() => {
            result.current.closeModal();
        });

        // Then: Should navigate to home
        expect(mockNavigate).toHaveBeenCalledWith('/');
    });

    it('should navigate to different announcement when openModal called with different ID', () => {
        // Given: Hook is initialized
        mockUseParams.mockReturnValue({});
        const { result } = renderHook(() => useModal());

        // When: openModal is called twice with different pet IDs
        act(() => {
            result.current.openModal('pet-123');
        });
        act(() => {
            result.current.openModal('pet-789');
        });

        // Then: Should navigate to both announcement URLs in order
        expect(mockNavigate).toHaveBeenNthCalledWith(1, '/announcement/pet-123');
        expect(mockNavigate).toHaveBeenNthCalledWith(2, '/announcement/pet-789');
    });

    it('should reflect different selectedAnnouncementId when params change', () => {
        // Given: Hook with one announcement
        mockUseParams.mockReturnValue({ announcementId: 'pet-111' });
        const { result, rerender } = renderHook(() => useModal());

        expect(result.current.selectedAnnouncementId).toBe('pet-111');
        expect(result.current.isOpen).toBe(true);

        // When: Params change to different announcement
        mockUseParams.mockReturnValue({ announcementId: 'pet-222' });
        rerender();

        // Then: Hook should reflect new announcement
        expect(result.current.selectedAnnouncementId).toBe('pet-222');
        expect(result.current.isOpen).toBe(true);
    });

    it('should close modal when params are cleared', () => {
        // Given: Hook with open modal
        mockUseParams.mockReturnValue({ announcementId: 'pet-333' });
        const { result, rerender } = renderHook(() => useModal());

        expect(result.current.isOpen).toBe(true);

        // When: Params are cleared
        mockUseParams.mockReturnValue({});
        rerender();

        // Then: Modal should be closed
        expect(result.current.isOpen).toBe(false);
        expect(result.current.selectedAnnouncementId).toBeNull();
    });
});
