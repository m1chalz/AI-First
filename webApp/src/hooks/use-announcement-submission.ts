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
        await announcementService.uploadPhoto(response.id, response.managementPassword, flowState.photo!.file); // photo is never null at this step
      } catch (uploadErr: unknown) {
        if (typeof uploadErr === 'object' && uploadErr !== null && 'type' in uploadErr) {
          setError(uploadErr as ApiError);
        } else {
          setError({ type: 'network', message: 'Photo upload failed' });
        }
      }
    } catch (err: unknown) {
      if (typeof err === 'object' && err !== null && 'type' in err) {
        setError(err as ApiError);
      } else {
        setError({ type: 'network', message: 'Submission failed' });
      }
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

