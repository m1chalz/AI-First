import React from 'react';
import { MdClose, MdInfoOutline } from 'react-icons/md';
import styles from './LocationBanner.module.css';

interface LocationBannerProps {
  onClose: () => void;
}

/**
 * Informational banner displayed when location permissions are blocked.
 * Shows benefits and instructions for enabling location access.
 */
export const LocationBanner: React.FC<LocationBannerProps> = ({ onClose }) => {
  return (
    <div className={styles.banner} data-testid="petList.locationBanner">
      <div className={styles.content}>
        <MdInfoOutline className={styles.icon} aria-hidden="true" />
        <div className={styles.text}>
          <p className={styles.message}>
            <strong>See pets near you!</strong> Enable location access in your browser settings to discover lost and found pets in your area.
          </p>
        </div>
      </div>
      <button
        className={styles.closeButton}
        onClick={onClose}
        aria-label="Close banner"
        data-testid="petList.locationBanner.close"
      >
        <MdClose aria-hidden="true" />
      </button>
    </div>
  );
};
