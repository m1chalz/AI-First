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
  microchipNumber: '',
  photo: null,
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

    const flowStateWithPhoto = {
      ...mockFlowState,
      photo: {
        file: new File(['content'], 'pet.jpg', { type: 'image/jpeg' }),
        filename: 'pet.jpg',
        size: 1024,
        mimeType: 'image/jpeg',
        previewUrl: 'data:image/jpeg;base64,fake'
      }
    };

    const { result } = renderHook(() => useAnnouncementSubmission());

    // when
    act(() => {
      result.current.submitAnnouncement(flowStateWithPhoto);
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

    const flowStateWithPhoto = {
      ...mockFlowState,
      photo: {
        file: new File(['content'], 'pet.jpg', { type: 'image/jpeg' }),
        filename: 'pet.jpg',
        size: 1024,
        mimeType: 'image/jpeg',
        previewUrl: 'data:image/jpeg;base64,fake'
      }
    };

    const { result } = renderHook(() => useAnnouncementSubmission());

    // when
    act(() => {
      result.current.submitAnnouncement(flowStateWithPhoto);
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

    const flowStateWithPhoto = {
      ...mockFlowState,
      photo: {
        file: new File(['content'], 'pet.jpg', { type: 'image/jpeg' }),
        filename: 'pet.jpg',
        size: 1024,
        mimeType: 'image/jpeg',
        previewUrl: 'data:image/jpeg;base64,fake'
      }
    };

    const { result } = renderHook(() => useAnnouncementSubmission());

    // when
    act(() => {
      result.current.submitAnnouncement(flowStateWithPhoto);
    });

    await waitFor(() => {
      expect(result.current.isSubmitting).toBe(false);
    });

    // then
    expect(result.current.error).toBeTruthy();
    expect(result.current.announcementId).toBe('ann-123');
  });

});

