import React from 'react';
import { Header } from './Header';
import styles from './NewAnnouncementLayout.module.css';

interface NewAnnouncementLayoutProps {
  title: string;
  progress: string;
  onBack: () => void;
  children: React.ReactNode;
}

export function NewAnnouncementLayout({ title, progress, onBack, children }: NewAnnouncementLayoutProps) {
  return (
    <div className={styles.pageContainer}>
      <div className={styles.contentCard}>
        <Header title={title} progress={progress} onBack={onBack} />
        <div className={styles.contentInner}>{children}</div>
      </div>
    </div>
  );
}
