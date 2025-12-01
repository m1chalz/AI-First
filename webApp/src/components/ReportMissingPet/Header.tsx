import styles from './Header.module.css';

interface HeaderProps {
  title: string;
  progress: string;
  onBack: () => void;
}

export function Header({ title, progress, onBack }: HeaderProps) {
  return (
    <header className={styles.header}>
      <button 
        onClick={onBack}
        data-testid="reportMissingPet.header.backButton.click"
        className={styles.backButton}
      >
        ←
      </button>
      <h1 className={styles.title}>
        {title}
      </h1>
      <span className={styles.progress}>{progress}</span>
    </header>
  );
}

