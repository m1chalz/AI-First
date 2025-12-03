import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor, act } from '@testing-library/react';
import { useAnnouncementSubmission } from '../../hooks/use-announcement-submission';
import * as announcementServiceModule from '../../services/announcement-service';
import type { ReportMissingPetFlowState, PhotoAttachment } from '../../models/ReportMissingPetFlow';
import { FlowStep } from '../../models/ReportMissingPetFlow';

vi.mock('../../services/announcement-service', () => ({
  announcementService: {
    createAnnouncement: vi.fn(),
    uploadPhoto: vi.fn()
  }
}));

describe('useAnnouncementSubmission - Integration', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should send all fields from flow state to backend', async () => {
    // given - complete flow state with all fields populated
    const mockFile = new File(['content'], 'pet.jpg', { type: 'image/jpeg' });
    const photo: PhotoAttachment = {
      file: mockFile,
      filename: 'pet.jpg',
      size: 1024,
      mimeType: 'image/jpeg',
      previewUrl: 'data:image/jpeg;base64,fake'
    };

    const completeFlowState: ReportMissingPetFlowState = {
      currentStep: FlowStep.Contact,
      microchipNumber: '123456789012345',
      photo,
      lastSeenDate: '2025-12-03',
      species: 'RABBIT',
      breed: 'Holland Lop',
      sex: 'MALE',
      age: 2,
      description: 'White rabbit with black spots',
      latitude: 22.0,
      longitude: 22.0,
      email: 'owner@example.com',
      phone: '+48987654321',
      reward: '1000 PLN'
    };

    let capturedDto: any = null;
    vi.spyOn(announcementServiceModule.announcementService, 'createAnnouncement').mockImplementation((dto) => {
      capturedDto = dto;
      return Promise.resolve({
        id: 'ann-123',
        managementPassword: 'pass123'
      });
    });

    vi.spyOn(announcementServiceModule.announcementService, 'uploadPhoto').mockResolvedValue(undefined);

    const { result } = renderHook(() => useAnnouncementSubmission());

    // when
    act(() => {
      result.current.submitAnnouncement(completeFlowState);
    });

    await waitFor(() => {
      expect(result.current.isSubmitting).toBe(false);
    });

    // then - verify all fields are sent
    expect(capturedDto).toBeDefined();
    expect(capturedDto.species).toBe('RABBIT');
    expect(capturedDto.sex).toBe('MALE');
    expect(capturedDto.breed).toBe('Holland Lop');
    expect(capturedDto.age).toBe(2);
    expect(capturedDto.description).toBe('White rabbit with black spots');
    expect(capturedDto.microchipNumber).toBe('123456789012345');
    expect(capturedDto.locationLatitude).toBe(22.0);
    expect(capturedDto.locationLongitude).toBe(22.0);
    expect(capturedDto.email).toBe('owner@example.com');
    expect(capturedDto.phone).toBe('+48987654321');
    expect(capturedDto.reward).toBe('1000 PLN');
    expect(capturedDto.lastSeenDate).toBe('2025-12-03');
    expect(capturedDto.status).toBe('MISSING');
  });

  it('should send minimal fields when only required fields are provided', async () => {
    // given - minimal flow state
    const minimalFlowState: ReportMissingPetFlowState = {
      currentStep: FlowStep.Contact,
      microchipNumber: '',
      photo: null,
      lastSeenDate: '2025-12-03',
      species: 'DOG',
      breed: '',
      sex: 'FEMALE',
      age: null,
      description: '',
      latitude: 52.0,
      longitude: 21.0,
      email: 'test@example.com',
      phone: '',
      reward: ''
    };

    let capturedDto: any = null;
    vi.spyOn(announcementServiceModule.announcementService, 'createAnnouncement').mockImplementation((dto) => {
      capturedDto = dto;
      return Promise.resolve({
        id: 'ann-456',
        managementPassword: 'pass456'
      });
    });

    const { result } = renderHook(() => useAnnouncementSubmission());

    // when
    act(() => {
      result.current.submitAnnouncement(minimalFlowState);
    });

    await waitFor(() => {
      expect(result.current.isSubmitting).toBe(false);
    });

    // then - verify required fields are present, optional are absent
    expect(capturedDto).toBeDefined();
    expect(capturedDto.species).toBe('DOG');
    expect(capturedDto.sex).toBe('FEMALE');
    expect(capturedDto.locationLatitude).toBe(52.0);
    expect(capturedDto.locationLongitude).toBe(21.0);
    expect(capturedDto.email).toBe('test@example.com');
    expect(capturedDto.lastSeenDate).toBe('2025-12-03');
    expect(capturedDto.status).toBe('MISSING');

    // Optional fields should not be in DTO
    expect(capturedDto.breed).toBeUndefined();
    expect(capturedDto.microchipNumber).toBeUndefined();
    expect(capturedDto.phone).toBeUndefined();
    expect(capturedDto.reward).toBeUndefined();
    expect(capturedDto.age).toBeUndefined();
    expect(capturedDto.description).toBeUndefined();
  });

  it('should verify JSON stringification includes all fields', async () => {
    // given
    const flowState: ReportMissingPetFlowState = {
      currentStep: FlowStep.Contact,
      microchipNumber: '111112222233333',
      photo: null,
      lastSeenDate: '2025-12-03',
      species: 'DOG',
      breed: 'breed',
      sex: 'FEMALE',
      age: 13,
      description: 'desc',
      latitude: 1,
      longitude: 1,
      email: 'owner@example.com',
      phone: '+48123456789',
      reward: '500 PLN'
    };

    let capturedDto: any = null;
    vi.spyOn(announcementServiceModule.announcementService, 'createAnnouncement').mockImplementation((dto) => {
      capturedDto = dto;
      return Promise.resolve({
        id: 'ann-789',
        managementPassword: 'pass789'
      });
    });

    const { result } = renderHook(() => useAnnouncementSubmission());

    // when
    act(() => {
      result.current.submitAnnouncement(flowState);
    });

    await waitFor(() => {
      expect(result.current.isSubmitting).toBe(false);
    });

    // then - JSON string should contain all values
    const jsonString = JSON.stringify(capturedDto);
    expect(jsonString).toContain('111112222233333');
    expect(jsonString).toContain('breed');
    expect(jsonString).toContain('13');
    expect(jsonString).toContain('desc');
    expect(jsonString).toContain('owner@example.com');
    expect(jsonString).toContain('+48123456789');
    expect(jsonString).toContain('500 PLN');
    expect(jsonString).toContain('DOG');
    expect(jsonString).toContain('FEMALE');
  });
});

