import React from 'react';
import styles from './AnimalList.module.css';

/**
 * Component for displaying empty state when no animals are available.
 * Shows user-friendly message encouraging action.
 * 
 * Message per FR-009: "No animals reported yet. Tap 'Report a Missing Animal' to add the first one."
 */
export const EmptyState: React.FC = () => {
    return (
        <div className={styles.emptyState}>
            <p className={styles.emptyStateText}>
                No animals reported yet. Tap 'Report a Missing Animal' to add the first one.
            </p>
        </div>
    );
};

