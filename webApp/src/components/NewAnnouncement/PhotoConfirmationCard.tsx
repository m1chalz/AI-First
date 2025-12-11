import { PhotoAttachment } from '../../models/NewAnnouncementFlow';
import { formatFileSize } from '../../utils/format-file-size';
import styles from './PhotoScreen.module.css';

interface PhotoConfirmationCardProps {
  photo: PhotoAttachment;
  onRemove: () => void;
}

export function PhotoConfirmationCard({ photo, onRemove }: PhotoConfirmationCardProps) {
  return (
    <div className={styles.confirmationCard} data-testid="animalPhoto.confirmationCard">
      <div className={styles.confirmationContent}>
        <div className={styles.successIcon}>
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
          </svg>
        </div>
        <div className={styles.fileInfo}>
          <p className={styles.filename} data-testid="animalPhoto.filename.text">
            {photo.filename}
          </p>
          <p className={styles.filesize} data-testid="animalPhoto.filesize.text">
            {formatFileSize(photo.size)}
          </p>
        </div>
      </div>
      <button
        type="button"
        onClick={onRemove}
        className={styles.removeButton}
        data-testid="animalPhoto.remove.click"
        aria-label="Remove photo"
      >
        ×
      </button>
    </div>
  );
}
