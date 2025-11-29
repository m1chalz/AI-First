import './LoadingOverlay.css';

interface LoadingOverlayProps {
  message?: string;
}

export function LoadingOverlay({ message }: LoadingOverlayProps) {
  return (
    <div className="loading-overlay">
      <div className="spinner" data-testid="petList.loading.spinner" />
      {message && <p className="message">{message}</p>}
    </div>
  );
}

