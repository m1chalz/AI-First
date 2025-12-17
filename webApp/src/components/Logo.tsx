import { HiOutlineSearch } from 'react-icons/hi';
import styles from './Logo.module.css';

interface LogoProps {
  size?: 'small' | 'medium';
  variant?: 'dark' | 'light';
}

export function Logo({ size = 'small', variant = 'dark' }: LogoProps) {
  return (
    <div className={`${styles.logo} ${styles[size]}`}>
      <div className={styles.iconContainer}>
        <HiOutlineSearch className={styles.icon} />
      </div>
      <span className={`${styles.text} ${styles[variant]}`}>PetSpot</span>
    </div>
  );
}

