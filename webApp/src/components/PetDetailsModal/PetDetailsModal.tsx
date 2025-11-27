import React, { useEffect, useRef } from 'react';
import { createPortal } from 'react-dom';
import { usePetDetails } from '../../hooks/use-pet-details';
import { PetDetailsContent } from './PetDetailsContent';
import styles from './PetDetailsModal.module.css';

interface PetDetailsModalProps {
    isOpen: boolean;
    selectedPetId: string | null;
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

function setupFocusTrap(
    isOpen: boolean,
    modalRef: React.RefObject<HTMLDivElement>
) {
    if (!isOpen || !modalRef.current) return undefined;
    
    const focusableElements = Array.from(
        modalRef.current.querySelectorAll<HTMLElement>(
            'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
        )
    ).filter(el => {
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

export const PetDetailsModal: React.FC<PetDetailsModalProps> = ({ isOpen, selectedPetId, onClose }) => {
    const { pet, isLoading, error, retry } = usePetDetails(selectedPetId);
    const modalRef = useRef<HTMLDivElement>(null);
    const previousActiveElementRef = useRef<HTMLElement | null>(null);
    
    useEffect(() => setupModalAccessibility(isOpen, previousActiveElementRef, onClose), [isOpen, onClose]);
  
    useEffect(() => setupFocusTrap(isOpen, modalRef), [isOpen]);
    
    if (!isOpen) {
        return null;
    }
    
    const handleBackdropClick = (e: React.MouseEvent<HTMLDivElement>) => {
        if (e.target === e.currentTarget) {
            onClose();
        }
    };
    
    const modalContent = (
        <div className={styles.backdrop} onClick={handleBackdropClick} aria-hidden="true">
            <div
                ref={modalRef}
                className={styles.modal}
                role="dialog"
                aria-modal="true"
                aria-labelledby="pet-details-title"
            >
                <div className={styles.modalHeader}>
                    <button
                        className={styles.closeButton}
                        onClick={onClose}
                        aria-label="Close modal"
                        data-testid="petDetails.closeButton.click"
                    >
                        ×
                    </button>
                </div>
                
                <div className={styles.modalBody} id="pet-details-title">
                    {isLoading && (
                        <div className={styles.loading}>
                            <div className={styles.spinner}></div>
                            <p>Loading pet details...</p>
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
                    
                    {!isLoading && !error && pet && (
                        <PetDetailsContent pet={pet} />
                    )}
                </div>
            </div>
        </div>
    );
    
    return createPortal(modalContent, document.body);
};

