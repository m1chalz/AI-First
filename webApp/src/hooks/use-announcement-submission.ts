import { useState } from 'react';
import { announcementService } from '../services/announcement-service';
import { mapFlowStateToDto } from '../models/announcement-submission';
import type { ReportMissingPetFlowState } from '../models/ReportMissingPetFlow';
import type { ApiError } from '../models/api-error';

export interface UseAnnouncementSubmissionResult {
  isSubmitting: boolean;
  error: ApiError | null;
  announcementId: string | null;
  managementPassword: string | null;
  submitAnnouncement: (flowState: ReportMissingPetFlowState) => Promise<void>;
}

export function useAnnouncementSubmission(): UseAnnouncementSubmissionResult {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<ApiError | null>(null);
  const [announcementId, setAnnouncementId] = useState<string | null>(null);
  const [managementPassword, setManagementPassword] = useState<string | null>(null);

  const submitAnnouncement = async (flowState: ReportMissingPetFlowState) => {
    setIsSubmitting(true);
    setError(null);

    try {
      const dto = mapFlowStateToDto(flowState);
      const response = await announcementService.createAnnouncement(dto);
      setAnnouncementId(response.id);
      setManagementPassword(response.managementPassword);

      try {
        await announcementService.uploadPhoto(response.id, response.managementPassword, flowState.photo!.file);
      } catch (uploadErr: unknown) {
        const apiError = uploadErr as ApiError;
        setError(apiError);
      }
    } catch (err: unknown) {
      const apiError = err as ApiError;
      setError(apiError);
      setAnnouncementId(null);
      setManagementPassword(null);
    } finally {
      setIsSubmitting(false);
    }
  };

  return {
    isSubmitting,
    error,
    announcementId,
    managementPassword,
    submitAnnouncement
  };
}

