import { useNavigate } from 'react-router-dom';
import { useReportMissingPetFlow } from '../../hooks/use-report-missing-pet-flow';
import { FlowStep } from '../../models/ReportMissingPetFlow';
import { flowNavigation } from '../../routes/report-missing-pet-routes';
import { ReportMissingPetLayout } from './ReportMissingPetLayout';
import styles from './ReportMissingPetLayout.module.css';

export function PhotoScreen() {
  const navigate = useNavigate();
  const { flowState, clearFlowState } = useReportMissingPetFlow();

  const handleBack = () => {
    navigate(flowNavigation.goToPreviousStep(FlowStep.Photo));
  };

  const handleContinue = () => {
    clearFlowState();
    navigate('/', { replace: true });
  };

  return (
    <ReportMissingPetLayout
      title="Photo"
      progress="2/4"
      onBack={handleBack}
    >
      <h2 className={styles.heading}>Photo Upload</h2>
      
      <p className={styles.description}>
        This is a placeholder for the photo upload step. Future implementation will allow uploading pet photos.
      </p>
      
      <div className={styles.description} style={{ marginTop: '16px' }}>
        <strong>Current Flow State:</strong>
        <br />
        Microchip Number: {flowState.microchipNumber || 'N/A'}
        <br />
        Current Step: {flowState.currentStep}
      </div>
      
      <button onClick={handleContinue} className={styles.primaryButton}>
        Complete Flow (Placeholder)
      </button>
    </ReportMissingPetLayout>
  );
}

