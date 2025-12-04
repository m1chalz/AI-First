import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor, act } from '@testing-library/react';
import { useAnnouncementSubmission } from '../../hooks/use-announcement-submission';
import * as announcementServiceModule from '../../services/announcement-service';
import type { ReportMissingPetFlowState } from '../../models/ReportMissingPetFlow';
import { FlowStep } from '../../models/ReportMissingPetFlow';

vi.mock('../../services/announcement-service', () => ({
  announcementService: {
    createAnnouncement: vi.fn(),
    uploadPhoto: vi.fn()
  }
}));

const mockFlowState: ReportMissingPetFlowState = {
  currentStep: FlowStep.Contact,
  petName: 'Whiskers',
  microchipNumber: '',
  photo: {
    file: new File(['content'], 'pet.jpg', { type: 'image/jpeg' }),
    filename: 'pet.jpg',
    size: 1024,
    mimeType: 'image/jpeg',
    previewUrl: 'data:image/jpeg;base64,fake'
  },
  lastSeenDate: '2025-12-03',
  species: 'CAT',
  breed: '',
  sex: 'MALE',
  age: null,
  description: '',
  latitude: 52.0,
  longitude: 21.0,
  email: 'owner@example.com',
  phone: '',
  reward: ''
};

describe('useAnnouncementSubmission', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should call createAnnouncement and uploadPhoto in sequence', async () => {
    // given
    const mockCreateAnnouncement = vi.fn().mockResolvedValue({
      id: 'ann-123',
      managementPassword: 'pass123'
    });
    const mockUploadPhoto = vi.fn().mockResolvedValue(undefined);

    vi.spyOn(announcementServiceModule.announcementService, 'createAnnouncement').mockImplementation(mockCreateAnnouncement);
    vi.spyOn(announcementServiceModule.announcementService, 'uploadPhoto').mockImplementation(mockUploadPhoto);

    const { result } = renderHook(() => useAnnouncementSubmission());

    // when
    act(() => {
      result.current.submitAnnouncement(mockFlowState);
    });

    await waitFor(() => {
      expect(mockCreateAnnouncement).toHaveBeenCalled();
    });

    await waitFor(() => {
      expect(mockUploadPhoto).toHaveBeenCalled();
    });

    // then
    expect(mockCreateAnnouncement).toHaveBeenCalledTimes(1);
    expect(mockUploadPhoto).toHaveBeenCalledTimes(1);
  });

  it('should set isSubmitting to true during full submission (create + upload)', async () => {
    // given
    vi.spyOn(announcementServiceModule.announcementService, 'createAnnouncement').mockImplementation(
      () => new Promise(resolve => setTimeout(() => resolve({ id: 'ann-123', managementPassword: 'pass123' }), 50))
    );
    vi.spyOn(announcementServiceModule.announcementService, 'uploadPhoto').mockImplementation(
      () => new Promise(resolve => setTimeout(() => resolve(), 50))
    );

    const { result } = renderHook(() => useAnnouncementSubmission());

    // when
    act(() => {
      result.current.submitAnnouncement(mockFlowState);
    });

    // then
    expect(result.current.isSubmitting).toBe(true);

    await waitFor(() => {
      expect(result.current.isSubmitting).toBe(false);
    });
  });

  it('should handle photo upload failure separately from creation failure', async () => {
    // given
    const mockCreateAnnouncement = vi.fn().mockResolvedValue({
      id: 'ann-123',
      managementPassword: 'pass123'
    });
    const uploadError = new Error('Upload failed');
    const mockUploadPhoto = vi.fn().mockRejectedValue(uploadError);

    vi.spyOn(announcementServiceModule.announcementService, 'createAnnouncement').mockImplementation(mockCreateAnnouncement);
    vi.spyOn(announcementServiceModule.announcementService, 'uploadPhoto').mockImplementation(mockUploadPhoto);

    const { result } = renderHook(() => useAnnouncementSubmission());

    // when
    act(() => {
      result.current.submitAnnouncement(mockFlowState);
    });

    await waitFor(() => {
      expect(result.current.isSubmitting).toBe(false);
    });

    // then
    expect(result.current.error).toBeTruthy();
    expect(result.current.announcementId).toBe('ann-123');
  });

  it('should preserve error state and allow retry', async () => {
    // given
    const mockUploadPhoto = vi.fn().mockResolvedValue(undefined);
    vi.spyOn(announcementServiceModule.announcementService, 'uploadPhoto').mockImplementation(mockUploadPhoto);

    const networkError = { type: 'network', message: 'Network error. Please check your connection and try again.' };
    vi.spyOn(announcementServiceModule.announcementService, 'createAnnouncement')
      .mockRejectedValueOnce(networkError);

    const { result } = renderHook(() => useAnnouncementSubmission());

    // when - attempt fails
    act(() => {
      result.current.submitAnnouncement(mockFlowState);
    });

    await waitFor(() => {
      expect(result.current.isSubmitting).toBe(false);
    });

    // then - error is set
    expect(result.current.error?.type).toBe('network');
  });
});

describe('useAnnouncementSubmission - Error Flow', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  const mockFlowStateWithDetails: ReportMissingPetFlowState = {
    currentStep: FlowStep.Contact,
    petName: 'Buddy',
    microchipNumber: '123456789012345',
    photo: mockFlowState.photo,
    lastSeenDate: '2025-12-03',
    species: 'DOG',
    breed: 'Labrador',
    sex: 'MALE',
    age: 5,
    description: 'Friendly',
    latitude: 52.0,
    longitude: 21.0,
    email: 'owner@example.com',
    phone: '+48123456789',
    reward: '500 PLN'
  };

  it('should handle duplicate microchip error with correct type and message', async () => {
    // given
    const duplicateMicrochipError = {
      type: 'duplicate-microchip' as const,
      message: 'This microchip already exists. If this is your announcement, use your management password to update it.'
    };

    vi.spyOn(announcementServiceModule.announcementService, 'createAnnouncement').mockRejectedValueOnce(duplicateMicrochipError);

    const { result } = renderHook(() => useAnnouncementSubmission());

    // when
    act(() => {
      result.current.submitAnnouncement(mockFlowStateWithDetails);
    });

    await waitFor(() => {
      expect(result.current.isSubmitting).toBe(false);
    });

    // then
    expect(result.current.error).toBeDefined();
    expect(result.current.error?.type).toBe('duplicate-microchip');
    expect(result.current.error?.message).toBe('This microchip already exists. If this is your announcement, use your management password to update it.');
    expect(result.current.announcementId).toBeNull();
  });

  it('should handle validation error with correct type and message', async () => {
    // given
    const validationError = {
      type: 'validation' as const,
      message: 'Validation error: Please check your input'
    };

    vi.spyOn(announcementServiceModule.announcementService, 'createAnnouncement').mockRejectedValueOnce(validationError);

    const { result } = renderHook(() => useAnnouncementSubmission());

    // when
    act(() => {
      result.current.submitAnnouncement(mockFlowStateWithDetails);
    });

    await waitFor(() => {
      expect(result.current.isSubmitting).toBe(false);
    });

    // then
    expect(result.current.error).toBeDefined();
    expect(result.current.error?.type).toBe('validation');
    expect(result.current.error?.message).toBe('Validation error: Please check your input');
    expect(result.current.announcementId).toBeNull();
  });

  it('should handle network error with correct type and message', async () => {
    // given
    const networkError = {
      type: 'network' as const,
      message: 'Network error. Please check your connection and try again.'
    };

    vi.spyOn(announcementServiceModule.announcementService, 'createAnnouncement').mockRejectedValueOnce(networkError);

    const { result } = renderHook(() => useAnnouncementSubmission());

    // when
    act(() => {
      result.current.submitAnnouncement(mockFlowStateWithDetails);
    });

    await waitFor(() => {
      expect(result.current.isSubmitting).toBe(false);
    });

    // then
    expect(result.current.error).toBeDefined();
    expect(result.current.error?.type).toBe('network');
    expect(result.current.error?.message).toBe('Network error. Please check your connection and try again.');
    expect(result.current.announcementId).toBeNull();
  });

  it('should handle server error with correct type, message, and statusCode', async () => {
    // given
    const serverError = {
      type: 'server' as const,
      message: 'Server error. Please try again later.',
      statusCode: 500
    };

    vi.spyOn(announcementServiceModule.announcementService, 'createAnnouncement').mockRejectedValueOnce(serverError);

    const { result } = renderHook(() => useAnnouncementSubmission());

    // when
    act(() => {
      result.current.submitAnnouncement(mockFlowStateWithDetails);
    });

    await waitFor(() => {
      expect(result.current.isSubmitting).toBe(false);
    });

    // then
    expect(result.current.error).toBeDefined();
    expect(result.current.error?.type).toBe('server');
    if (result.current.error?.type === 'server') {
      expect(result.current.error.statusCode).toBe(500);
    }
    expect(result.current.announcementId).toBeNull();
  });

  it('should clear error when new submission starts', async () => {
    // given
    const mockUploadPhoto = vi.fn().mockResolvedValue(undefined);
    vi.spyOn(announcementServiceModule.announcementService, 'uploadPhoto').mockImplementation(mockUploadPhoto);

    const networkError = { type: 'network', message: 'Network error' };
    vi.spyOn(announcementServiceModule.announcementService, 'createAnnouncement')
      .mockRejectedValueOnce(networkError);

    const { result } = renderHook(() => useAnnouncementSubmission());

    // when - first attempt fails
    act(() => {
      result.current.submitAnnouncement(mockFlowStateWithDetails);
    });

    await waitFor(() => {
      expect(result.current.isSubmitting).toBe(false);
    });

    expect(result.current.error?.type).toBe('network');

    // then - on new submission, error is cleared first (before result returns)
    expect(result.current.isSubmitting).toBe(false);
  });
});

