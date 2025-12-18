import { MapContainer, TileLayer } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import { useMapState } from '../../hooks/use-map-state';
import config from '../../config/config';
import { MapErrorState } from './MapErrorState';
import { MapPermissionPrompt } from './MapPermissionPrompt';
import styles from './MapView.module.css';

export function MapView() {
  const { center, zoom, isLoading, error, showPermissionPrompt } = useMapState();

  if (isLoading) {
    return (
      <div className={styles.container} data-testid="landingPage.map.loading">
        <div className={styles.loading}>Loading map...</div>
      </div>
    );
  }

  if (showPermissionPrompt) {
    return (
      <div className={styles.container}>
        <MapPermissionPrompt />
      </div>
    );
  }

  if (error && !error.showFallbackMap) {
    return (
      <div className={styles.container}>
        <MapErrorState error={error} />
      </div>
    );
  }

  return (
    <div className={styles.container}>
      {error && error.showFallbackMap && (
        <div className={styles.errorBanner} data-testid="landingPage.map.errorBanner">
          {error.message}
        </div>
      )}
      <MapContainer center={[center.lat, center.lng]} zoom={zoom} className={styles.map} data-testid="landingPage.map">
        <TileLayer url={config.map.tileLayerUrl} attribution={config.map.attribution} />
      </MapContainer>
    </div>
  );
}
