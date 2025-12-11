import { useNavigate, useLocation } from 'react-router-dom';
import { useNewAnnouncementFlow } from '../../hooks/use-report-missing-pet-flow';
import { useEffect } from 'react';
import { AppRoutes } from '../../routes/routes';
import layoutStyles from './NewAnnouncementLayout.module.css';
import styles from './SummaryScreen.module.css';

export function SummaryScreen() {
  const navigate = useNavigate();
  const location = useLocation();
  const { clearFlowState } = useNewAnnouncementFlow();
  const managementPassword = (location.state as { managementPassword?: string })?.managementPassword;

  // Check if we have a valid announcement
  useEffect(() => {
    if (!managementPassword) {
      navigate(AppRoutes.microchip, { replace: true });
    }
  }, [managementPassword, navigate]);

  const handleClose = () => {
    clearFlowState();
    navigate('/');
  };

  return (
    <div className={layoutStyles.pageContainer}>
      <div className={layoutStyles.contentCard}>
        <div className={layoutStyles.contentInner}>
          <div>
            <h1 className={layoutStyles.heading}>Report created</h1>
          </div>

          <div>
            <p className={layoutStyles.description}>
              Your report has been created, and your missing animal has been added to the database. If your pet is found, you will receive a
              notification immediately.
            </p>
            <p className={layoutStyles.description}>
              If you wish to remove your report from the database, use the code provided below in the removal form. This code has also been
              sent to your email address
            </p>
          </div>

          {managementPassword && (
            <div className={styles.passwordContainer}>
              <div className={styles.passwordCard}>
                <div className={styles.passwordGradientBg} />
                <p className={styles.passwordText} data-testid="summary.password.text">
                  {managementPassword}
                </p>
              </div>
            </div>
          )}

          <button onClick={handleClose} className={layoutStyles.primaryButton} data-testid="summary.close.button">
            Close
          </button>
        </div>
      </div>
    </div>
  );
}
