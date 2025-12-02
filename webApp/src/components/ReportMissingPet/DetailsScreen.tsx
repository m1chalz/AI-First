import { useNavigate } from 'react-router-dom';
import { useReportMissingPetFlow } from '../../hooks/use-report-missing-pet-flow';
import { useBrowserBackHandler } from '../../hooks/use-browser-back-handler';
import { formatFileSize } from '../../utils/format-file-size';
import { ReportMissingPetLayout } from './ReportMissingPetLayout';
import styles from './ReportMissingPetLayout.module.css';

export function DetailsScreen() {
  const navigate = useNavigate();
  const { flowState, clearFlowState } = useReportMissingPetFlow();

  const handleBack = () => {
    navigate('/report-missing/photo');
  };

  const handleCancel = () => {
    clearFlowState();
    navigate('/');
  };

  useBrowserBackHandler(handleBack);

  return (
    <ReportMissingPetLayout
      title="Animal details"
      progress="3/4"
      onBack={handleBack}
    >
      <h2 className={styles.heading}>Flow State Debug</h2>
      
      <p className={styles.description}>
        This is a temporary screen showing the current flow state.
      </p>

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
          Current Flow State:
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
              <div style={{ marginTop: '4px' }}>
                <strong>Preview URL:</strong> {flowState.photo.previewUrl ? '✓ Generated' : '✗ None'}
              </div>
              {flowState.photo.previewUrl && (
                <div style={{ marginTop: '12px' }}>
                  <img 
                    src={flowState.photo.previewUrl} 
                    alt="Preview" 
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
      </div>

      <div style={{ display: 'flex', gap: '12px' }}>
        <button
          onClick={handleBack}
          className={styles.primaryButton}
          style={{ flex: 1 }}
        >
          Back to Photo
        </button>
        <button
          onClick={handleCancel}
          className={styles.primaryButton}
          style={{ flex: 1, backgroundColor: '#6c757d' }}
        >
          Cancel Flow
        </button>
      </div>
    </ReportMissingPetLayout>
  );
}

