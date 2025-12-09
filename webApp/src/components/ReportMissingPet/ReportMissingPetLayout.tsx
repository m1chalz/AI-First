import React from 'react';
import { Header } from './Header';
import styles from './ReportMissingPetLayout.module.css';

interface ReportMissingPetLayoutProps {
  title: string;
  progress: string;
  onBack: () => void;
  children: React.ReactNode;
}

export function ReportMissingPetLayout({ title, progress, onBack, children }: ReportMissingPetLayoutProps) {
  return (
    <div className={styles.pageContainer}>
      <div className={styles.contentCard}>
        <Header title={title} progress={progress} onBack={onBack} />
        <div className={styles.contentInner}>{children}</div>
      </div>
    </div>
  );
}
