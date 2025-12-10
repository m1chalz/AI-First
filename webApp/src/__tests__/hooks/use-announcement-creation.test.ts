import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor, act } from '@testing-library/react';
import { useAnnouncementCreation } from '../../hooks/use-announcement-creation';
import * as announcementServiceModule from '../../services/announcement-service';
import type { NewAnnouncementFlowState } from '../../models/NewAnnouncementFlow';
import { FlowStep } from '../../models/NewAnnouncementFlow';

vi.mock('../../services/announcement-service', () => ({
  announcementService: {
    createAnnouncement: vi.fn()
  }
}));

const mockFlowState: NewAnnouncementFlowState = {
  currentStep: FlowStep.Contact,
  microchipNumber: '',
  photo: null,
  lastSeenDate: '2025-12-03',
  species: 'CAT',
  breed: '',
  sex: 'MALE',
  age: null,
  petName: 'Whiskers',
  description: '',
  latitude: 52.0,
  longitude: 21.0,
  email: 'owner@example.com',
  phone: '',
  reward: ''
};

describe('useAnnouncementCreation', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should call service.createAnnouncement', async () => {
    // given
    vi.spyOn(announcementServiceModule.announcementService, 'createAnnouncement').mockResolvedValue({
      id: 'ann-123',
      managementPassword: 'pass123'
    });

    const { result } = renderHook(() => useAnnouncementCreation());

    // when
    act(() => {
      result.current.createAnnouncement(mockFlowState);
    });

    await waitFor(() => {
      expect(announcementServiceModule.announcementService.createAnnouncement).toHaveBeenCalled();
    });

    // then
    expect(announcementServiceModule.announcementService.createAnnouncement).toHaveBeenCalledTimes(1);
  });

  it('should set isCreating to true during creation', async () => {
    // given
    vi.spyOn(announcementServiceModule.announcementService, 'createAnnouncement').mockImplementation(
      () => new Promise((resolve) => setTimeout(() => resolve({ id: 'ann-123', managementPassword: 'pass123' }), 100))
    );

    const { result } = renderHook(() => useAnnouncementCreation());

    // when
    act(() => {
      result.current.createAnnouncement(mockFlowState);
    });

    // then
    expect(result.current.isCreating).toBe(true);

    await waitFor(() => {
      expect(result.current.isCreating).toBe(false);
    });
  });

  it('should store announcementId and managementPassword on successful creation', async () => {
    // given
    vi.spyOn(announcementServiceModule.announcementService, 'createAnnouncement').mockResolvedValue({
      id: 'ann-456',
      managementPassword: 'secure-pass'
    });

    const { result } = renderHook(() => useAnnouncementCreation());

    // when
    act(() => {
      result.current.createAnnouncement(mockFlowState);
    });

    await waitFor(() => {
      expect(result.current.isCreating).toBe(false);
    });

    // then
    expect(result.current.announcementId).toBe('ann-456');
    expect(result.current.managementPassword).toBe('secure-pass');
    expect(result.current.error).toBeNull();
  });

  it('should set error state when creation fails', async () => {
    // given
    const testError = new Error('Creation failed');
    vi.spyOn(announcementServiceModule.announcementService, 'createAnnouncement').mockRejectedValue(testError);

    const { result } = renderHook(() => useAnnouncementCreation());

    // when
    act(() => {
      result.current.createAnnouncement(mockFlowState);
    });

    await waitFor(() => {
      expect(result.current.isCreating).toBe(false);
    });

    // then
    expect(result.current.error).toBeTruthy();
    expect(result.current.announcementId).toBeNull();
    expect(result.current.managementPassword).toBeNull();
  });

  it('should return announcement data on success, null on failure', async () => {
    // given
    vi.spyOn(announcementServiceModule.announcementService, 'createAnnouncement').mockResolvedValue({
      id: 'ann-789',
      managementPassword: 'pass789'
    });

    const { result: successResult } = renderHook(() => useAnnouncementCreation());

    // when
    act(() => {
      successResult.current.createAnnouncement(mockFlowState);
    });

    await waitFor(() => {
      expect(successResult.current.isCreating).toBe(false);
    });

    // then
    expect(successResult.current.announcementId).toBe('ann-789');
    expect(successResult.current.managementPassword).toBe('pass789');
  });

  it('should include petName in the announcement DTO when provided', async () => {
    // given
    const createAnnouncementSpy = vi.spyOn(announcementServiceModule.announcementService, 'createAnnouncement').mockResolvedValue({
      id: 'ann-with-name',
      managementPassword: 'pass-with-name'
    });

    const { result } = renderHook(() => useAnnouncementCreation());

    // when
    act(() => {
      result.current.createAnnouncement(mockFlowState);
    });

    await waitFor(() => {
      expect(result.current.isCreating).toBe(false);
    });

    // then
    expect(createAnnouncementSpy).toHaveBeenCalledWith(
      expect.objectContaining({
        petName: 'Whiskers',
        species: 'CAT',
        sex: 'MALE'
      })
    );
  });
});
