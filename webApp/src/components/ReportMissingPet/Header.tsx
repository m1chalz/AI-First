import styles from './Header.module.css';

interface HeaderProps {
  title: string;
  progress: string;
  onBack: () => void;
}

export function Header({ title, progress, onBack }: HeaderProps) {
  return (
    <header className={styles.header}>
      <button onClick={onBack} data-testid="reportMissingPet.header.backButton.click" className={styles.backButton} aria-label="Go back">
        ←
      </button>
      <p className={styles.title} data-testid="reportMissingPet.header.title">
        {title}
      </p>
      <div className={styles.progress} data-testid="reportMissingPet.header.progress">
        {progress}
      </div>
    </header>
  );
}
