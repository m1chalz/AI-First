import { CiLocationOff } from 'react-icons/ci';
import styles from './MapPermissionPrompt.module.css';

export function MapPermissionPrompt() {
  return (
    <div className={styles.container} data-testid="landingPage.map.permissionPrompt">
      <div className={styles.content}>
        <div className={styles.icon}>
          <CiLocationOff />
        </div>
        <h3 className={styles.title}>Location Access Required</h3>
        <p className={styles.message}>
          We need your location to show pets near you on the map. Please enable location access for this website in your browser settings.
        </p>
      </div>
    </div>
  );
}
