import React from 'react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { PhotoScreen } from '../PhotoScreen';
import { AppRoutes } from '../../../pages/routes';
import { FlowStep } from '../../../models/NewAnnouncementFlow';

const mockNavigate = vi.fn();

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate
  };
});

// Mock useNewAnnouncementFlow hook
const mockUpdateFlowState = vi.fn();
const mockFlowState = {
  currentStep: FlowStep.Photo,
  microchipNumber: '12345-67890-12345',
  photo: null as File | null
};

vi.mock('../../../hooks/use-new-announcement-flow', () => ({
  useNewAnnouncementFlow: () => ({
    flowState: mockFlowState,
    updateFlowState: mockUpdateFlowState
  })
}));

// Mock useToast hook
const mockShowToast = vi.fn();
vi.mock('../../../hooks/use-toast', () => ({
  useToast: () => ({
    message: null,
    showToast: mockShowToast
  })
}));

// Mock usePhotoUpload hook
const mockHandleFileSelect = vi.fn();
const mockHandleDrop = vi.fn();
const mockHandleDragOver = vi.fn();
const mockHandleDragLeave = vi.fn();
const mockRemovePhoto = vi.fn();
let mockPhoto: File | null = null;
let mockIsDragOver = false;

vi.mock('../../../hooks/use-photo-upload', () => ({
  usePhotoUpload: () => ({
    photo: mockPhoto,
    isDragOver: mockIsDragOver,
    handleFileSelect: mockHandleFileSelect,
    handleDrop: mockHandleDrop,
    handleDragOver: mockHandleDragOver,
    handleDragLeave: mockHandleDragLeave,
    removePhoto: mockRemovePhoto
  })
}));

const renderWithProviders = (component: React.ReactElement) => render(<MemoryRouter>{component}</MemoryRouter>);

describe('PhotoScreen', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mockFlowState.currentStep = FlowStep.Photo;
    mockFlowState.photo = null;
    mockPhoto = null;
    mockIsDragOver = false;
  });

  it('renders photo upload screen', () => {
    // When
    renderWithProviders(<PhotoScreen />);

    // Then
    expect(screen.getByText("Your pet's photo")).toBeTruthy();
    expect(screen.getByText('Please upload a photo of the missing animal.')).toBeTruthy();
  });

  it('redirects to microchip screen when flow state is empty', () => {
    // Given
    mockFlowState.currentStep = FlowStep.Empty;

    // When
    renderWithProviders(<PhotoScreen />);

    // Then
    expect(mockNavigate).toHaveBeenCalledWith(AppRoutes.reportMissing.microchip, { replace: true });
  });

  it('navigates back to microchip screen when back button clicked', () => {
    // Given
    renderWithProviders(<PhotoScreen />);

    // When
    const backButton = screen.getByTestId('newAnnouncement.header.backButton.click');
    fireEvent.click(backButton);

    // Then
    expect(mockNavigate).toHaveBeenCalledWith(AppRoutes.reportMissing.microchip);
  });

  it('shows toast when continue clicked without photo', () => {
    // Given
    renderWithProviders(<PhotoScreen />);

    // When
    const continueButton = screen.getByTestId('animalPhoto.continue.click');
    fireEvent.click(continueButton);

    // Then
    expect(mockShowToast).toHaveBeenCalledWith('Photo is mandatory', 3000);
    expect(mockNavigate).not.toHaveBeenCalledWith(AppRoutes.reportMissing.details);
  });

  it('navigates to details screen when continue clicked with photo', () => {
    // Given
    mockPhoto = new File(['test'], 'test.jpg', { type: 'image/jpeg' });
    renderWithProviders(<PhotoScreen />);

    // When
    const continueButton = screen.getByTestId('animalPhoto.continue.click');
    fireEvent.click(continueButton);

    // Then
    expect(mockUpdateFlowState).toHaveBeenCalledWith({
      photo: mockPhoto,
      currentStep: FlowStep.Details
    });
    expect(mockNavigate).toHaveBeenCalledWith(AppRoutes.reportMissing.details);
  });

  it('shows photo upload card when no photo', () => {
    // When
    renderWithProviders(<PhotoScreen />);

    // Then
    expect(screen.getByTestId('animalPhoto.browse.click')).toBeTruthy();
    expect(screen.getByText('Upload animal photo')).toBeTruthy();
  });

  it('shows photo confirmation card when photo is selected', () => {
    // Given
    mockPhoto = new File(['test'], 'cat.jpg', { type: 'image/jpeg' });

    // When
    renderWithProviders(<PhotoScreen />);

    // Then
    expect(screen.queryByTestId('animalPhoto.browse.click')).toBeNull();
  });

  it('displays progress indicator', () => {
    // When
    renderWithProviders(<PhotoScreen />);

    // Then
    expect(screen.getByText('2/4')).toBeTruthy();
  });
});
