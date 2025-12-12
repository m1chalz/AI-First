import React from 'react';
import styles from './AnnouncementList.module.css';

export const EmptyState: React.FC = () => (
  <div className={styles.emptyState}>
    <p className={styles.emptyStateText}>No animals reported yet. Tap &apos;Report a Missing Animal&apos; to add the first one.</p>
  </div>
);
