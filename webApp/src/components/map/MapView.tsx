import { MapContainer, TileLayer } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import { useMapState } from '../../hooks/use-map-state';
import config from '../../config/config';
import { MapErrorState } from './MapErrorState';
import { MapPermissionPrompt } from './MapPermissionPrompt';
import styles from './MapView.module.css';

function MapHeader() {
  return (
    <div className={styles.header}>
      <h2 className={styles.title}>Pet Locations Map</h2>
      <p className={styles.subtitle}>Red markers indicate missing pets, blue markers indicate found pets</p>
    </div>
  );
}

export function MapView() {
  const { center, zoom, isLoading, error, showPermissionPrompt } = useMapState();

  if (isLoading) {
    return (
      <section className={styles.section}>
        <div className={styles.container}>
          <MapHeader />
          <div className={styles.loading} data-testid="landingPage.map.loading">Loading map...</div>
        </div>
      </section>
    );
  }

  if (showPermissionPrompt) {
    return (
      <section className={styles.section}>
        <div className={styles.container}>
          <MapHeader />
          <MapPermissionPrompt />
        </div>
      </section>
    );
  }

  if (error && !error.showFallbackMap) {
    return (
      <section className={styles.section}>
        <div className={styles.container}>
          <MapHeader />
          <MapErrorState error={error} />
        </div>
      </section>
    );
  }

  return (
    <section className={styles.section}>
      <div className={styles.container}>
        <MapHeader />
        {error && error.showFallbackMap && (
          <div className={styles.errorBanner} data-testid="landingPage.map.errorBanner">
            {error.message}
          </div>
        )}
        <MapContainer center={[center.lat, center.lng]} zoom={zoom} className={styles.map} data-testid="landingPage.map">
          <TileLayer url={config.map.tileLayerUrl} attribution={config.map.attribution} />
        </MapContainer>
      </div>
    </section>
  );
}
