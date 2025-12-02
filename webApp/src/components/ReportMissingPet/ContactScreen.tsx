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
      title="Contact details"
      progress="4/4"
      onBack={handleBack}
    >
      <form onSubmit={(e) => { e.preventDefault(); handleContinue(); }}>
        <div className={styles.inputGroup}>
          <label htmlFor="phone" className={styles.label}>
            Phone number (optional)
          </label>
          <input
            id="phone"
            type="tel"
            className={styles.input}
            value={phone}
            onChange={(e) => handlePhoneChange(e.target.value)}
            placeholder="e.g., +48123456789"
            data-testid="contact.phoneNumber.input"
            style={phoneError ? { borderColor: '#dc3545' } : {}}
          />
          {phoneError && (
            <div style={{ color: '#dc3545', fontSize: '14px', marginTop: '4px' }}>
              {phoneError}
            </div>
          )}
        </div>

        <div className={styles.inputGroup}>
          <label htmlFor="email" className={styles.label}>
            Email address (optional)
          </label>
          <input
            id="email"
            type="email"
            className={styles.input}
            value={email}
            onChange={(e) => handleEmailChange(e.target.value)}
            placeholder="e.g., owner@example.com"
            data-testid="contact.email.input"
            style={emailError ? { borderColor: '#dc3545' } : {}}
          />
          {emailError && (
            <div style={{ color: '#dc3545', fontSize: '14px', marginTop: '4px' }}>
              {emailError}
            </div>
          )}
        </div>

        <div className={styles.inputGroup}>
          <label htmlFor="reward" className={styles.label}>
            Reward description (optional)
          </label>
          <textarea
            id="reward"
            className={styles.input}
            value={reward}
            onChange={(e) => handleRewardChange(e.target.value)}
            placeholder="e.g., $250 reward"
            data-testid="contact.reward.input"
            rows={3}
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
    </ReportMissingPetLayout>
  );
}

