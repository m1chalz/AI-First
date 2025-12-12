import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import { useMicrochipFormatter } from '../../hooks/use-microchip-formatter';
import { useNewAnnouncementFlow } from '../../hooks/use-new-announcement-flow';
import { FlowStep } from '../../models/NewAnnouncementFlow';
import { AppRoutes } from '../../routes/routes';
import { NewAnnouncementLayout } from './NewAnnouncementLayout';
import styles from './NewAnnouncementLayout.module.css';

export function MicrochipNumberScreen() {
  const navigate = useNavigate();
  const { flowState, updateFlowState, clearFlowState } = useNewAnnouncementFlow();
  const { value, formattedValue, handleChange, handlePaste } = useMicrochipFormatter(flowState.microchipNumber);

  useEffect(() => {
    if (flowState.currentStep === FlowStep.Empty) {
      updateFlowState({
        currentStep: FlowStep.Microchip
      });
    }
  }, [flowState.currentStep, updateFlowState]);

  const handleContinue = () => {
    updateFlowState({
      microchipNumber: value,
      currentStep: FlowStep.Photo
    });
    navigate(AppRoutes.photo);
  };

  const handleBack = () => {
    clearFlowState();
    navigate('/');
  };

  return (
    <NewAnnouncementLayout title="Microchip number" progress="1/4" onBack={handleBack}>
      <h2 className={styles.heading}>Identification by Microchip</h2>

      <p className={styles.description}>
        Microchip identification is the most efficient way to reunite with your pet. If your pet has been microchipped and you know the
        microchip number, please enter it here.
      </p>

      <div className={styles.inputGroup}>
        <label htmlFor="microchip-input" className={styles.label}>
          Microchip number (optional)
        </label>
        <input
          id="microchip-input"
          type="tel"
          pattern="[0-9]*"
          value={formattedValue}
          onChange={handleChange}
          onPaste={handlePaste}
          placeholder="00000-00000-00000"
          className={styles.input}
          data-testid="newAnnouncement.step1.microchipInput.field"
        />
      </div>

      <button onClick={handleContinue} className={styles.primaryButton} data-testid="newAnnouncement.step1.continueButton.click">
        Continue
      </button>
    </NewAnnouncementLayout>
  );
}
