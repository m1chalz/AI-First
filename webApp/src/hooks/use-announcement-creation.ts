import { useState } from 'react';
import { announcementService } from '../services/announcement-service';
import { mapFlowStateToDto } from '../models/announcement-submission';
import type { ReportMissingPetFlowState } from '../models/ReportMissingPetFlow';
import type { ApiError } from '../models/api-error';

export interface UseAnnouncementCreationResult {
  isCreating: boolean;
  error: ApiError | null;
  announcementId: string | null;
  managementPassword: string | null;
  createAnnouncement: (flowState: ReportMissingPetFlowState) => Promise<void>;
}

export function useAnnouncementCreation(): UseAnnouncementCreationResult {
  const [isCreating, setIsCreating] = useState(false);
  const [error, setError] = useState<ApiError | null>(null);
  const [announcementId, setAnnouncementId] = useState<string | null>(null);
  const [managementPassword, setManagementPassword] = useState<string | null>(null);

  const createAnnouncement = async (flowState: ReportMissingPetFlowState) => {
    setIsCreating(true);
    setError(null);

    try {
      const dto = mapFlowStateToDto(flowState);
      const response = await announcementService.createAnnouncement(dto);
      setAnnouncementId(response.id);
      setManagementPassword(response.managementPassword);
    } catch (err: unknown) {
      const apiError = err as ApiError;
      setError(apiError);
      setAnnouncementId(null);
      setManagementPassword(null);
    } finally {
      setIsCreating(false);
    }
  };

  return {
    isCreating,
    error,
    announcementId,
    managementPassword,
    createAnnouncement
  };
}
