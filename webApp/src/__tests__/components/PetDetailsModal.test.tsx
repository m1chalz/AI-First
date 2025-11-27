import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { PetDetailsModal } from '../../components/PetDetailsModal/PetDetailsModal';
import * as usePetDetailsModule from '../../hooks/use-pet-details';
import type { Animal } from '../../types/animal';

vi.mock('../../hooks/use-pet-details');

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

describe('PetDetailsModal', () => {
    const mockOnClose = vi.fn();
    
    beforeEach(() => {
        vi.clearAllMocks();
    });
    
    it('should not render when modal is closed', () => {
        // Given: Modal is closed
        vi.spyOn(usePetDetailsModule, 'usePetDetails').mockReturnValue({
            pet: null,
            isLoading: false,
            error: null,
            retry: vi.fn()
        });
        
        // When: Component is rendered with isOpen=false
        const { container } = render(
            <PetDetailsModal
                isOpen={false}
                selectedPetId={null}
                onClose={mockOnClose}
            />
        );
        
        // Then: Modal should not be visible
        expect(container.querySelector('[role="dialog"]')).toBeNull();
    });
    
    it('should render modal when opened', () => {
        // Given: Modal is open and pet details are loaded
        vi.spyOn(usePetDetailsModule, 'usePetDetails').mockReturnValue({
            pet: mockPet,
            isLoading: false,
            error: null,
            retry: vi.fn()
        });
        
        // When: Component is rendered with isOpen=true
        const { baseElement } = render(
            <PetDetailsModal
                isOpen={true}
                selectedPetId="pet-123"
                onClose={mockOnClose}
            />
        );
        
        // Then: Modal should be visible (rendered in portal to body)
        expect(baseElement.querySelector('[role="dialog"]')).toBeTruthy();
    });
    
    it('should call onClose when close button is clicked', async () => {
        // Given: Modal is open
        vi.spyOn(usePetDetailsModule, 'usePetDetails').mockReturnValue({
            pet: mockPet,
            isLoading: false,
            error: null,
            retry: vi.fn()
        });
        
        const user = userEvent.setup();
        render(
            <PetDetailsModal
                isOpen={true}
                selectedPetId="pet-123"
                onClose={mockOnClose}
            />
        );
        
        // When: Close button is clicked
        const closeButton = screen.getByTestId('petDetails.closeButton.click');
        await user.click(closeButton);
        
        // Then: onClose should be called
        expect(mockOnClose).toHaveBeenCalledTimes(1);
    });
    
    it('should call onClose when ESC key is pressed', async () => {
        // Given: Modal is open
        vi.spyOn(usePetDetailsModule, 'usePetDetails').mockReturnValue({
            pet: mockPet,
            isLoading: false,
            error: null,
            retry: vi.fn()
        });
        
        const user = userEvent.setup();
        render(
            <PetDetailsModal
                isOpen={true}
                selectedPetId="pet-123"
                onClose={mockOnClose}
            />
        );
        
        // When: ESC key is pressed
        await user.keyboard('{Escape}');
        
        // Then: onClose should be called
        expect(mockOnClose).toHaveBeenCalledTimes(1);
    });
    
    it('should call onClose when backdrop is clicked', async () => {
        // Given: Modal is open
        vi.spyOn(usePetDetailsModule, 'usePetDetails').mockReturnValue({
            pet: mockPet,
            isLoading: false,
            error: null,
            retry: vi.fn()
        });
        
        const user = userEvent.setup();
        const { baseElement } = render(
            <PetDetailsModal
                isOpen={true}
                selectedPetId="pet-123"
                onClose={mockOnClose}
            />
        );
        
        // When: Backdrop is clicked (backdrop contains the dialog)
        const backdrop = baseElement.querySelector('[class*="backdrop"]') as HTMLElement;
        if (backdrop) {
            await user.click(backdrop);
        }
        
        // Then: onClose should be called
        expect(mockOnClose).toHaveBeenCalledTimes(1);
    });
    
    it('should display loading spinner while fetching pet details', () => {
        // Given: Modal is open and loading
        vi.spyOn(usePetDetailsModule, 'usePetDetails').mockReturnValue({
            pet: null,
            isLoading: true,
            error: null,
            retry: vi.fn()
        });
        
        // When: Component is rendered
        render(
            <PetDetailsModal
                isOpen={true}
                selectedPetId="pet-123"
                onClose={mockOnClose}
            />
        );
        
        // Then: Loading spinner should be visible
        expect(screen.getByText(/loading/i)).toBeTruthy();
    });
    
    it('should display error message and retry button when error occurs', async () => {
        // Given: Modal is open and error occurred
        const mockRetry = vi.fn();
        vi.spyOn(usePetDetailsModule, 'usePetDetails').mockReturnValue({
            pet: null,
            isLoading: false,
            error: 'Failed to load pet details',
            retry: mockRetry
        });
        
        const user = userEvent.setup();
        render(
            <PetDetailsModal
                isOpen={true}
                selectedPetId="pet-123"
                onClose={mockOnClose}
            />
        );
        
        // Then: Error message and retry button should be visible
        expect(screen.getByText('Failed to load pet details')).toBeTruthy();
        const retryButton = screen.getByText(/retry/i);
        expect(retryButton).toBeTruthy();
        
        // When: Retry button is clicked
        await user.click(retryButton);
        
        // Then: retry function should be called
        expect(mockRetry).toHaveBeenCalledTimes(1);
    });
    
    it('should display pet details when loaded successfully', () => {
        // Given: Modal is open and pet details are loaded
        vi.spyOn(usePetDetailsModule, 'usePetDetails').mockReturnValue({
            pet: mockPet,
            isLoading: false,
            error: null,
            retry: vi.fn()
        });
        
        // When: Component is rendered
        render(
            <PetDetailsModal
                isOpen={true}
                selectedPetId="pet-123"
                onClose={mockOnClose}
            />
        );
        
        // Then: Pet details should be displayed
        expect(screen.getByText('Fluffy')).toBeTruthy();
        expect(screen.getByText('Maine Coon')).toBeTruthy();
    });
});

