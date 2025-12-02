import { useNavigate } from 'react-router-dom';
import { useReportMissingPetFlow } from '../../hooks/use-report-missing-pet-flow';
import { useBrowserBackHandler } from '../../hooks/use-browser-back-handler';
import { formatFileSize } from '../../utils/format-file-size';
import { ReportMissingPetLayout } from './ReportMissingPetLayout';
import styles from './ReportMissingPetLayout.module.css';
import { useEffect } from 'react';
import { FlowStep } from '../../models/ReportMissingPetFlow';
import { ReportMissingPetRoutes } from '../../routes/report-missing-pet-routes';

export function SummaryScreen() {
  const navigate = useNavigate();
  const { flowState, clearFlowState } = useReportMissingPetFlow();

  useEffect(() => {
    if (flowState.currentStep === FlowStep.Empty) {
      navigate(ReportMissingPetRoutes.microchip, { replace: true });
    }
  }, [flowState.currentStep, navigate]);

  const handleBack = () => {
    navigate(ReportMissingPetRoutes.contact);
  };

  const handleComplete = () => {
    clearFlowState();
    navigate('/');
  };

  useBrowserBackHandler(handleBack);

  return (
    <ReportMissingPetLayout
      title="Summary"
      progress="4/4"
      onBack={handleBack}
    >
      <div style={{
        backgroundColor: '#f8f9fa',
        border: '1px solid #dee2e6',
        borderRadius: '8px',
        padding: '20px',
        marginBottom: '24px',
        fontFamily: 'monospace',
        fontSize: '14px',
      }}>
        <h3 style={{ marginTop: 0, marginBottom: '16px', fontSize: '16px', fontWeight: 600 }}>
          Flow State Summary:
        </h3>

        <div style={{ marginBottom: '12px' }}>
          <strong>Current Step:</strong> {flowState.currentStep}
        </div>

        <div style={{ marginBottom: '12px' }}>
          <strong>Microchip Number:</strong> {flowState.microchipNumber || '(empty)'}
        </div>

        <div style={{ marginBottom: '12px' }}>
          <strong>Photo:</strong>
          {flowState.photo ? (
            <div style={{ marginTop: '8px', paddingLeft: '16px' }}>
              <div>✓ Photo uploaded</div>
              <div style={{ marginTop: '4px' }}>
                <strong>Filename:</strong> {flowState.photo.filename}
              </div>
              <div style={{ marginTop: '4px' }}>
                <strong>Size:</strong> {formatFileSize(flowState.photo.size)}
              </div>
              <div style={{ marginTop: '4px' }}>
                <strong>MIME Type:</strong> {flowState.photo.mimeType}
              </div>
              {flowState.photo.previewUrl && (
                <div style={{ marginTop: '12px' }}>
                  <img
                    src={flowState.photo.previewUrl}
                    alt="Pet preview"
                    style={{
                      maxWidth: '200px',
                      maxHeight: '200px',
                      border: '1px solid #ddd',
                      borderRadius: '4px'
                    }}
                  />
                </div>
              )}
            </div>
          ) : (
            <span> (no photo)</span>
          )}
        </div>

        <div style={{ marginBottom: '12px' }}>
          <strong>Last Seen Date:</strong> {flowState.lastSeenDate || '(empty)'}
        </div>

        <div style={{ marginBottom: '12px' }}>
          <strong>Species:</strong> {flowState.species || '(empty)'}
        </div>

        <div style={{ marginBottom: '12px' }}>
          <strong>Breed:</strong> {flowState.breed || '(empty)'}
        </div>

        <div style={{ marginBottom: '12px' }}>
          <strong>Sex:</strong> {flowState.sex || '(empty)'}
        </div>

        <div style={{ marginBottom: '12px' }}>
          <strong>Age:</strong> {flowState.age !== null ? flowState.age : '(empty)'}
        </div>

        <div style={{ marginBottom: '12px' }}>
          <strong>Description:</strong> {flowState.description || '(empty)'}
        </div>

        <div style={{ marginBottom: '12px' }}>
          <strong>Latitude:</strong> {flowState.latitude !== null ? flowState.latitude : '(empty)'}
        </div>

        <div style={{ marginBottom: '12px' }}>
          <strong>Longitude:</strong> {flowState.longitude !== null ? flowState.longitude : '(empty)'}
        </div>

        <div style={{ marginBottom: '12px' }}>
          <strong>Phone:</strong> {flowState.phone || '(empty)'}
        </div>

        <div style={{ marginBottom: '12px' }}>
          <strong>Email:</strong> {flowState.email || '(empty)'}
        </div>

        <div>
          <strong>Reward:</strong> {flowState.reward || '(empty)'}
        </div>
      </div>

      <div style={{ display: 'flex', gap: '12px' }}>
        <button
          onClick={handleBack}
          className={styles.primaryButton}
          style={{ flex: 1 }}
          data-testid="summary.back.button"
        >
          Back
        </button>
        <button
          onClick={handleComplete}
          className={styles.primaryButton}
          style={{ flex: 1 }}
          data-testid="summary.complete.button"
        >
          Complete
        </button>
      </div>
    </ReportMissingPetLayout>
  );
}

