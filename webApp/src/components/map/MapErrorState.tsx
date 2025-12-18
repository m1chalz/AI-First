import type { MapError } from '../../types/map';
import styles from './MapErrorState.module.css';

export function MapErrorState({ error }: { error: MapError }) {
  return (
    <div className={styles.container} data-testid="landingPage.map.error">
      <div className={styles.icon}>⚠️</div>
      <p className={styles.message}>{error.message}</p>
    </div>
  );
}
