import { useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useReportMissingPetFlow } from '../../hooks/use-report-missing-pet-flow';
import { usePhotoUpload } from '../../hooks/use-photo-upload';
import { useToast } from '../../hooks/use-toast';
import { FlowStep } from '../../models/ReportMissingPetFlow';
import { ReportMissingPetRoutes } from '../../routes/report-missing-pet-routes';
import { ReportMissingPetLayout } from './ReportMissingPetLayout';
import { PhotoUploadCard } from './PhotoUploadCard';
import { PhotoConfirmationCard } from './PhotoConfirmationCard';
import { Toast } from '../Toast/Toast';
import styles from './ReportMissingPetLayout.module.css';

export function PhotoScreen() {
  const navigate = useNavigate();
  const { flowState, updateFlowState } = useReportMissingPetFlow();
  const { message, showToast } = useToast();
  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (flowState.currentStep === FlowStep.Empty) {
      navigate(ReportMissingPetRoutes.microchip, { replace: true });
    }
  }, [flowState.currentStep, navigate]);

  const handleBack = () => {
    navigate(ReportMissingPetRoutes.microchip);
  };
  
  const {
    photo,
    isDragOver,
    handleFileSelect,
    handleDrop,
    handleDragOver,
    handleDragLeave,
    removePhoto,
  } = usePhotoUpload(flowState.photo, showToast);

  const handleContinue = () => {
    if (!photo) {
      showToast('Photo is mandatory', 3000);
      return;
    }

    updateFlowState({
      photo,
      currentStep: FlowStep.Details,
    });
    navigate(ReportMissingPetRoutes.details);
  };

  const handleBrowseClick = () => {
    fileInputRef.current?.click();
  };

  const handleFileInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      handleFileSelect(file);
    }
  };

  return (
    <ReportMissingPetLayout
      title="Animal photo"
      progress="2/4"
      onBack={handleBack}
    >
      <h2 className={styles.heading}>Your pet&apos;s photo</h2>
      
      <p className={styles.description}>
        Please upload a photo of the missing animal.
      </p>

      {!photo ? (
        <PhotoUploadCard
          isDragOver={isDragOver}
          onDrop={handleDrop}
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          onBrowseClick={handleBrowseClick}
          fileInputRef={fileInputRef}
          onFileInputChange={handleFileInputChange}
        />
      ) : (
        <PhotoConfirmationCard photo={photo} onRemove={removePhoto} />
      )}
      
      <button
        onClick={handleContinue}
        className={styles.primaryButton}
        data-testid="animalPhoto.continue.click"
      >
        Continue
      </button>

      <Toast message={message} />
    </ReportMissingPetLayout>
  );
}
