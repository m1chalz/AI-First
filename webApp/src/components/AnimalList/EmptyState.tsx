import React from 'react';
import styles from './AnimalList.module.css';

export const EmptyState: React.FC = () => {
    return (
        <div className={styles.emptyState}>
            <p className={styles.emptyStateText}>
                No animals reported yet. Tap 'Report a Missing Animal' to add the first one.
            </p>
        </div>
    );
};

