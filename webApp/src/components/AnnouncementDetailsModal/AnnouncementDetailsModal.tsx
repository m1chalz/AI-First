import React, { useEffect, useRef, useState } from 'react';
import { createPortal } from 'react-dom';
import { useAnnouncementDetails } from '../../hooks/use-announcement-details';
import { AnnouncementDetailsContent } from './AnnouncementDetailsContent';
import styles from './AnnouncementDetailsModal.module.css';

interface AnnouncementDetailsModalProps {
  isOpen: boolean;
  selectedAnnouncementId: string | null;
  onClose: () => void;
}

function setupModalAccessibility(
  isOpen: boolean,
  previousActiveElementRef: React.MutableRefObject<HTMLElement | null>,
  onClose: () => void
): (() => void) | undefined {
  if (!isOpen) return undefined;

  previousActiveElementRef.current = document.activeElement as HTMLElement;
  document.body.style.overflow = 'hidden';

  const handleEscape = (e: KeyboardEvent) => {
    if (e.key === 'Escape') {
      onClose();
    }
  };

  document.addEventListener('keydown', handleEscape);

  return () => {
    document.removeEventListener('keydown', handleEscape);
    document.body.style.overflow = '';
    if (previousActiveElementRef.current) {
      previousActiveElementRef.current.focus();
    }
  };
}

function setupFocusTrap(isOpen: boolean, modalRef: React.RefObject<HTMLDivElement>) {
  if (!isOpen || !modalRef.current) return undefined;

  const focusableElements = Array.from(
    modalRef.current.querySelectorAll<HTMLElement>('button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])')
  ).filter((el) => {
    const htmlEl = el as HTMLElement & { disabled?: boolean };
    return !htmlEl.disabled;
  });

  if (focusableElements.length === 0) return undefined;

  const firstElement = focusableElements[0];
  const lastElement = focusableElements[focusableElements.length - 1];

  firstElement.focus();

  const handleTabKey = (e: KeyboardEvent) => {
    if (e.key !== 'Tab') return;

    if (e.shiftKey) {
      if (document.activeElement === firstElement) {
        e.preventDefault();
        lastElement.focus();
      }
    } else {
      if (document.activeElement === lastElement) {
        e.preventDefault();
        firstElement.focus();
      }
    }
  };

  modalRef.current.addEventListener('keydown', handleTabKey);

  return () => modalRef.current?.removeEventListener('keydown', handleTabKey);
}

export const AnnouncementDetailsModal: React.FC<AnnouncementDetailsModalProps> = ({ isOpen, selectedAnnouncementId, onClose }) => {
  const { announcement, isLoading, error, retry } = useAnnouncementDetails(selectedAnnouncementId);
  const modalRef = useRef<HTMLDivElement>(null);
  const previousActiveElementRef = useRef<HTMLElement | null>(null);
  const [isClosing, setIsClosing] = useState(false);

  const handleClose = () => {
    setIsClosing(true);
    setTimeout(() => {
      onClose();
    }, 300); // Match animation duration
  };

  useEffect(() => {
    if (!isOpen) {
      setIsClosing(false);
    }
  }, [isOpen]);

  useEffect(() => setupModalAccessibility(isOpen, previousActiveElementRef, handleClose), [isOpen]);

  useEffect(() => setupFocusTrap(isOpen, modalRef), [isOpen]);

  const handleBackdropClick = (e: React.MouseEvent<HTMLDivElement>) => {
    if (e.target === e.currentTarget) {
      handleClose();
    }
  };

  if (!isOpen && !isClosing) {
    return null;
  }

  const modalContent = (
    <div
      className={`${styles.backdrop} ${isClosing ? styles.closing : ''}`}
      onClick={handleBackdropClick}
      aria-hidden="true"
      data-testid="announcementDetails.backdrop"
    >
      <div
        ref={modalRef}
        className={styles.modal}
        role="dialog"
        aria-modal="true"
        aria-labelledby="announcement-details-title"
        data-testid="announcementDetails.modal"
      >
        <div className={styles.modalHeader}>
          <button
            className={styles.closeButton}
            onClick={handleClose}
            aria-label="Close modal"
            data-testid="announcementDetails.closeButton.click"
          >
            ×
          </button>
        </div>

        <div className={styles.modalBody} id="announcement-details-title">
          {isLoading && (
            <div className={styles.loading}>
              <div className={styles.spinner}></div>
              <p>Loading announcement details...</p>
            </div>
          )}

          {error && (
            <div className={styles.error}>
              <p>{error}</p>
              <button onClick={retry} className={styles.retryButton}>
                Retry
              </button>
            </div>
          )}

          {!isLoading && !error && announcement && <AnnouncementDetailsContent announcement={announcement} />}
        </div>
      </div>
    </div>
  );

  return createPortal(modalContent, document.body);
};
