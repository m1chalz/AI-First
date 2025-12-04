import styles from './PhotoScreen.module.css';

interface PhotoUploadCardProps {
  isDragOver: boolean;
  onDrop: (e: React.DragEvent<HTMLDivElement>) => void;
  onDragOver: (e: React.DragEvent<HTMLDivElement>) => void;
  onDragLeave: () => void;
  onBrowseClick: () => void;
  fileInputRef: React.RefObject<HTMLInputElement>;
  onFileInputChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

export function PhotoUploadCard({
  isDragOver,
  onDrop,
  onDragOver,
  onDragLeave,
  onBrowseClick,
  fileInputRef,
  onFileInputChange,
}: PhotoUploadCardProps) {
  return (
    <div
      className={`${styles.uploadCard} ${isDragOver ? styles.dragOverHighlight : ''}`}
      onDrop={onDrop}
      onDragOver={onDragOver}
      onDragLeave={onDragLeave}
      data-testid="animalPhoto.dropZone.area"
    >
      <div className={styles.uploadIcon}>
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path strokeLinecap="round" strokeLinejoin="round" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
        </svg>
      </div>
      <div className={styles.uploadInfo}>
        <p className={styles.uploadTitle}>Upload animal photo</p>
        <p className={styles.uploadHint}>JPEG, PNG, GIF • Max 20MB</p>
      </div>
      <button
        type="button"
        onClick={onBrowseClick}
        className={styles.browseButton}
        data-testid="animalPhoto.browse.click"
      >
        Browse
      </button>
      <input
        ref={fileInputRef}
        type="file"
        accept="image/jpeg,image/png,image/gif,image/webp,image/bmp,image/tiff,image/heic,image/heif"
        onChange={onFileInputChange}
        className={styles.fileInput}
        data-testid="animalPhoto.fileInput.field"
      />
    </div>
  );
}

