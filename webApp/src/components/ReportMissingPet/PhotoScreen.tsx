import { useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useReportMissingPetFlow } from '../../hooks/use-report-missing-pet-flow';
import { useBrowserBackHandler } from '../../hooks/use-browser-back-handler';
import { usePhotoUpload } from '../../hooks/use-photo-upload';
import { useToast } from '../../hooks/use-toast';
import { FlowStep } from '../../models/ReportMissingPetFlow';
import { formatFileSize } from '../../utils/format-file-size';
import { ReportMissingPetLayout } from './ReportMissingPetLayout';
import { Toast } from '../Toast/Toast';
import styles from './ReportMissingPetLayout.module.css';
import photoStyles from './PhotoScreen.module.css';

export function PhotoScreen() {
  const navigate = useNavigate();
  const { flowState, updateFlowState, clearFlowState } = useReportMissingPetFlow();
  const { message, showToast } = useToast();
  const fileInputRef = useRef<HTMLInputElement>(null);
  
  const {
    photo,
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
    navigate('/report-missing/details');
  };

  const handleBack = () => {
    clearFlowState();
    navigate('/');
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

  useBrowserBackHandler(handleBack);

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
        <div
          className={photoStyles.uploadCard}
          onDrop={handleDrop}
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          data-testid="animalPhoto.dropZone.area"
        >
          <div className={photoStyles.uploadIcon}>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path strokeLinecap="round" strokeLinejoin="round" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
            </svg>
          </div>
          <div className={photoStyles.uploadInfo}>
            <p className={photoStyles.uploadTitle}>Upload animal photo</p>
            <p className={photoStyles.uploadHint}>JPEG, PNG, GIF • Max 20MB</p>
          </div>
          <button
            type="button"
            onClick={handleBrowseClick}
            className={photoStyles.browseButton}
            data-testid="animalPhoto.browse.click"
          >
            Browse
          </button>
          <input
            ref={fileInputRef}
            type="file"
            accept="image/jpeg,image/png,image/gif,image/webp,image/bmp,image/tiff,image/heic,image/heif"
            onChange={handleFileInputChange}
            className={photoStyles.fileInput}
            data-testid="animalPhoto.fileInput.field"
          />
        </div>
      ) : (
        <div className={photoStyles.confirmationCard} data-testid="animalPhoto.confirmationCard">
          <div className={photoStyles.confirmationContent}>
            <div className={photoStyles.successIcon}>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <div className={photoStyles.fileInfo}>
              <p className={photoStyles.filename} data-testid="animalPhoto.filename.text">
                {photo.filename}
              </p>
              <p className={photoStyles.filesize} data-testid="animalPhoto.filesize.text">
                {formatFileSize(photo.size)}
              </p>
            </div>
          </div>
          <button
            type="button"
            onClick={removePhoto}
            className={photoStyles.removeButton}
            data-testid="animalPhoto.remove.click"
            aria-label="Remove photo"
          >
            ×
          </button>
        </div>
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
