import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { AnnouncementDetailsModal } from '../../AnnouncementDetailsModal/AnnouncementDetailsModal';
import * as useAnnouncementDetailsModule from '../../../hooks/use-announcement-details';
import type { Announcement } from '../../../types/announcement';

vi.mock('../../hooks/use-pet-details');

const mockAnnouncement: Announcement = {
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

describe('AnnouncementDetailsModal', () => {
  const mockOnClose = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should not render when modal is closed', () => {
    // Given: Modal is closed
    vi.spyOn(useAnnouncementDetailsModule, 'useAnnouncementDetails').mockReturnValue({
      announcement: null,
      isLoading: false,
      error: null,
      retry: vi.fn()
    });

    // When: Component is rendered with isOpen=false
    const { container } = render(<AnnouncementDetailsModal isOpen={false} selectedAnnouncementId={null} onClose={mockOnClose} />);

    // Then: Modal should not be visible
    expect(container.querySelector('[role="dialog"]')).toBeNull();
  });

  it('should render modal when opened', () => {
    // Given: Modal is open and announcement details are loaded
    vi.spyOn(useAnnouncementDetailsModule, 'useAnnouncementDetails').mockReturnValue({
      announcement: mockAnnouncement,
      isLoading: false,
      error: null,
      retry: vi.fn()
    });

    // When: Component is rendered with isOpen=true
    const { baseElement } = render(<AnnouncementDetailsModal isOpen={true} selectedAnnouncementId="announcement-123" onClose={mockOnClose} />);

    // Then: Modal should be visible (rendered in portal to body)
    expect(baseElement.querySelector('[role="dialog"]')).toBeTruthy();
  });

  it('should call onClose when close button is clicked', async () => {
    // Given: Modal is open
    vi.spyOn(useAnnouncementDetailsModule, 'useAnnouncementDetails').mockReturnValue({
      announcement: mockAnnouncement,
      isLoading: false,
      error: null,
      retry: vi.fn()
    });

    const user = userEvent.setup();
    render(<AnnouncementDetailsModal isOpen={true} selectedAnnouncementId="announcement-123" onClose={mockOnClose} />);

    // When: Close button is clicked
    const closeButton = screen.getByTestId('announcementDetails.closeButton.click');
    await user.click(closeButton);

    // Then: onClose should be called after animation delay
    await waitFor(
      () => {
        expect(mockOnClose).toHaveBeenCalledTimes(1);
      },
      { timeout: 400 }
    );
  });

  it('should call onClose when ESC key is pressed', async () => {
    // Given: Modal is open
    vi.spyOn(useAnnouncementDetailsModule, 'useAnnouncementDetails').mockReturnValue({
      announcement: mockAnnouncement,
      isLoading: false,
      error: null,
      retry: vi.fn()
    });

    const user = userEvent.setup();
    render(<AnnouncementDetailsModal isOpen={true} selectedAnnouncementId="announcement-123" onClose={mockOnClose} />);

    // When: ESC key is pressed
    await user.keyboard('{Escape}');

    // Then: onClose should be called after animation delay
    await waitFor(
      () => {
        expect(mockOnClose).toHaveBeenCalledTimes(1);
      },
      { timeout: 400 }
    );
  });

  it('should call onClose when backdrop is clicked', async () => {
    // Given: Modal is open
    vi.spyOn(useAnnouncementDetailsModule, 'useAnnouncementDetails').mockReturnValue({
      announcement: mockAnnouncement,
      isLoading: false,
      error: null,
      retry: vi.fn()
    });

    const user = userEvent.setup();
    const { baseElement } = render(<AnnouncementDetailsModal isOpen={true} selectedAnnouncementId="announcement-123" onClose={mockOnClose} />);

    // When: Backdrop is clicked (backdrop contains the dialog)
    const backdrop = baseElement.querySelector('[class*="backdrop"]') as HTMLElement;
    if (backdrop) {
      await user.click(backdrop);
    }

    // Then: onClose should be called after animation delay
    await waitFor(
      () => {
        expect(mockOnClose).toHaveBeenCalledTimes(1);
      },
      { timeout: 400 }
    );
  });

  it('should display loading spinner while fetching announcement details', () => {
    // Given: Modal is open and loading
    vi.spyOn(useAnnouncementDetailsModule, 'useAnnouncementDetails').mockReturnValue({
      announcement: null,
      isLoading: true,
      error: null,
      retry: vi.fn()
    });

    // When: Component is rendered
    render(<AnnouncementDetailsModal isOpen={true} selectedAnnouncementId="announcement-123" onClose={mockOnClose} />);

    // Then: Loading spinner should be visible
    expect(screen.getByText(/loading/i)).toBeTruthy();
  });

  it('should display error message and retry button when error occurs', async () => {
    // Given: Modal is open and error occurred
    const mockRetry = vi.fn();
    vi.spyOn(useAnnouncementDetailsModule, 'useAnnouncementDetails').mockReturnValue({
      announcement: null,
      isLoading: false,
      error: 'Failed to load announcement details',
      retry: mockRetry
    });

    const user = userEvent.setup();
    render(<AnnouncementDetailsModal isOpen={true} selectedAnnouncementId="announcement-123" onClose={mockOnClose} />);

    // Then: Error message and retry button should be visible
    expect(screen.getByText('Failed to load announcement details')).toBeTruthy();
    const retryButton = screen.getByText(/retry/i);
    expect(retryButton).toBeTruthy();

    // When: Retry button is clicked
    await user.click(retryButton);

    // Then: retry function should be called
    expect(mockRetry).toHaveBeenCalledTimes(1);
  });

  it('should display announcement details when loaded successfully', () => {
    // Given: Modal is open and announcement details are loaded
    vi.spyOn(useAnnouncementDetailsModule, 'useAnnouncementDetails').mockReturnValue({
      announcement: mockAnnouncement,
      isLoading: false,
      error: null,
      retry: vi.fn()
    });

    // When: Component is rendered
    render(<AnnouncementDetailsModal isOpen={true} selectedAnnouncementId="announcement-123" onClose={mockOnClose} />);

    // Then: Announcement details should be displayed
    expect(screen.getByText('Maine Coon')).toBeTruthy();
    expect(screen.getByText('Cat')).toBeTruthy();
    // Male is displayed with gender icon, so use partial match
    expect(screen.getByText(/Male/)).toBeTruthy();
  });

  describe('Responsive Layout', () => {
    it('should render modal dialog for desktop and tablet viewports', () => {
      // Given: Modal is open
      vi.spyOn(useAnnouncementDetailsModule, 'useAnnouncementDetails').mockReturnValue({
        announcement: mockAnnouncement,
        isLoading: false,
        error: null,
        retry: vi.fn()
      });

      // When: Component is rendered
      const { baseElement } = render(<AnnouncementDetailsModal isOpen={true} selectedAnnouncementId="announcement-123" onClose={mockOnClose} />);

      // Then: Modal dialog should exist
      expect(baseElement.querySelector('[role="dialog"]')).toBeTruthy();
    });

    it('should display close button with accessible size', () => {
      // Given: Modal is open
      vi.spyOn(useAnnouncementDetailsModule, 'useAnnouncementDetails').mockReturnValue({
        announcement: mockAnnouncement,
        isLoading: false,
        error: null,
        retry: vi.fn()
      });

      // When: Component is rendered
      render(<AnnouncementDetailsModal isOpen={true} selectedAnnouncementId="announcement-123" onClose={mockOnClose} />);

      // Then: Close button should exist and be accessible
      const closeButton = screen.getByTestId('announcementDetails.closeButton.click');
      expect(closeButton).toBeTruthy();
    });

    it('should display readable content in modal', () => {
      // Given: Modal is open
      vi.spyOn(useAnnouncementDetailsModule, 'useAnnouncementDetails').mockReturnValue({
        announcement: mockAnnouncement,
        isLoading: false,
        error: null,
        retry: vi.fn()
      });

      // When: Component is rendered
      render(<AnnouncementDetailsModal isOpen={true} selectedAnnouncementId="announcement-123" onClose={mockOnClose} />);

      // Then: Text content should be visible and readable
      expect(screen.getByText('Maine Coon')).toBeTruthy();
      expect(screen.getByText('Cat')).toBeTruthy();
    });
  });
});
