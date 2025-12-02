import { useNavigate } from 'react-router-dom';
import { useReportMissingPetFlow } from '../../hooks/use-report-missing-pet-flow';
import { useBrowserBackHandler } from '../../hooks/use-browser-back-handler';
import { useContactForm } from '../../hooks/use-contact-form';
import { useToast } from '../../hooks/use-toast';
import { ReportMissingPetRoutes } from '../../routes/report-missing-pet-routes';
import { ReportMissingPetLayout } from './ReportMissingPetLayout';
import styles from './ReportMissingPetLayout.module.css';
import { useEffect } from 'react';
import { FlowStep } from '../../models/ReportMissingPetFlow';

export function ContactScreen() {
  const navigate = useNavigate();
  const { flowState } = useReportMissingPetFlow();
  const {
    phone,
    email,
    reward,
    phoneError,
    emailError,
    handlePhoneChange,
    handleEmailChange,
    handleRewardChange,
    handleSubmit,
  } = useContactForm();
  const { showToast } = useToast();

  useEffect(() => {
    if (flowState.currentStep === FlowStep.Empty) {
      navigate(ReportMissingPetRoutes.microchip, { replace: true });
    }
  }, [flowState.currentStep, navigate]);

  const handleBack = () => {
    navigate(ReportMissingPetRoutes.details);
  };

  const handleContinue = () => {
    const success = handleSubmit();
    if (!success) {
      if (phone === '' && email === '') {
        showToast('Please provide at least one contact method');
      }
      return;
    }
    navigate(ReportMissingPetRoutes.summary);
  };

  useBrowserBackHandler(handleBack);

  return (
    <ReportMissingPetLayout
      title="Owner's details"
      progress="4/4"
      onBack={handleBack}
    >
      <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', alignItems: 'center' }}>
          <h2 className={styles.heading}>Your contact info</h2>
        </div>

        <p className={styles.description}>
          Add your contact information and potential reward.
        </p>

        <form onSubmit={(e) => { e.preventDefault(); handleContinue(); }} style={{ display: 'flex', flexDirection: 'column', gap: '24px', width: '100%' }}>
        <div className={styles.inputGroup}>
          <label htmlFor="phone" className={styles.label}>
            Phone number
          </label>
          <input
            id="phone"
            type="tel"
            className={styles.input}
            value={phone}
            onChange={(e) => handlePhoneChange(e.target.value)}
            data-testid="contact.phoneNumber.input"
            style={phoneError ? { borderColor: '#FB2C36' } : {}}
          />
          {phoneError && (
            <div style={{ color: '#FB2C36', fontSize: '14px', marginTop: '4px' }}>
              {phoneError}
            </div>
          )}
        </div>

        <div className={styles.inputGroup}>
          <label htmlFor="email" className={styles.label}>
            Email
          </label>
          <input
            id="email"
            type="email"
            className={styles.input}
            value={email}
            onChange={(e) => handleEmailChange(e.target.value)}
            data-testid="contact.email.input"
            style={emailError ? { borderColor: '#FB2C36' } : {}}
          />
          {emailError && (
            <div style={{ color: '#FB2C36', fontSize: '14px', marginTop: '4px' }}>
              {emailError}
            </div>
          )}
        </div>

        <div className={styles.inputGroup}>
          <label htmlFor="reward" className={styles.label}>
            Reward for the finder (optional)
          </label>
          <textarea
            id="reward"
            className={styles.textarea}
            value={reward}
            onChange={(e) => handleRewardChange(e.target.value)}
            placeholder="Enter amount..."
            data-testid="contact.reward.input"
          />
        </div>

        <button
          type="button"
          onClick={handleContinue}
          className={styles.primaryButton}
          data-testid="contact.continue.button"
        >
          Continue
        </button>
        </form>
      </div>
    </ReportMissingPetLayout>
  );
}

