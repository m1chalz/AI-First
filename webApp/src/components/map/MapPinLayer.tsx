import L from 'leaflet';
import { Marker, Popup } from 'react-leaflet';
import { useMapPins } from '../../hooks/use-map-pins';
import { ANNOUNCEMENT_STATUS_BADGE_COLORS } from '../../types/announcement';
import type { Coordinates } from '../../types/location';
import config from '../../config/config';
import toPascalCase from '../../utils/pascal-case-formatter';
import { formatDate } from '../../utils/date-formatter';
import styles from './MapPinLayer.module.css';

interface MapPinLayerProps {
  userLocation: Coordinates | null;
}

function createIcon(color: string, symbol: string): L.DivIcon {
  const svg = `
    <svg width="30" height="40" viewBox="0 0 30 40" xmlns="http://www.w3.org/2000/svg">
      <path d="M15 0C6.716 0 0 6.716 0 15c0 8.284 15 25 15 25s15-16.716 15-25C30 6.716 23.284 0 15 0z" fill="${color}"/>
      <text x="15" y="18" text-anchor="middle" fill="white" font-size="14" font-weight="bold" font-family="sans-serif">${symbol}</text>
    </svg>
  `;

  return L.divIcon({
    html: svg,
    className: 'pet-pin-marker',
    iconSize: [30, 40],
    iconAnchor: [15, 40],
    popupAnchor: [0, -40]
  });
}

const MISSING_PIN_ICON = createIcon(ANNOUNCEMENT_STATUS_BADGE_COLORS.MISSING, '!');
const FOUND_PIN_ICON = createIcon(ANNOUNCEMENT_STATUS_BADGE_COLORS.FOUND, '✓');

export function MapPinLayer({ userLocation }: MapPinLayerProps) {
  const { pins, loading, error } = useMapPins(userLocation);

  if (!userLocation) {
    return null;
  }

  return (
    <>
      {loading && (
        <div className={styles.overlay} data-testid="landingPage.map.pinsLoading">
          Loading pins...
        </div>
      )}

      {error && (
        <div className={styles.overlay} data-testid="landingPage.map.pinsError">
          Failed to load pins
        </div>
      )}

      {pins.map(pin => (
        <Marker
          key={pin.id}
          position={[pin.locationLatitude, pin.locationLongitude]}
          icon={pin.status === 'MISSING' ? MISSING_PIN_ICON : FOUND_PIN_ICON}
          data-testid={`landingPage.map.pin.${pin.id}`}
        >
          <Popup className={styles.petPopup}>
            <div className={styles.popup} data-testid={`landingPage.map.popup.${pin.id}`}>
              <div className={styles.photoContainer}>
                <img
                  src={`${config.apiBaseUrl}${pin.photoUrl}`}
                  alt={pin.petName || 'Unknown'}
                  className={styles.popupImage}
                  onError={e => {
                    e.currentTarget.style.display = 'none';
                    (e.currentTarget.parentElement?.querySelector(`.${styles.popupImagePlaceholder}`) as HTMLElement)?.classList.remove(styles.hidden);
                  }}
                />
                <div className={`${styles.popupImagePlaceholder} ${styles.hidden}`}>
                  🐾
                </div>
                <span
                  className={styles.statusBadge}
                  style={{ backgroundColor: ANNOUNCEMENT_STATUS_BADGE_COLORS[pin.status] }}
                >
                  {pin.status}
                </span>
              </div>
              <div className={styles.popupContent}>
                <h3 className={styles.popupName}>{pin.petName || 'Unknown'}</h3>
                <p className={styles.popupInfo}>{toPascalCase(pin.species)} | {formatDate(pin.lastSeenDate)}</p>
                {pin.description && (
                  <p className={styles.popupDescription} data-testid="landingPage.map.popup.description">
                    {pin.description}
                  </p>
                )}
                <p className={styles.popupContact}>
                  {pin.phone} | {pin.email}
                </p>
              </div>
            </div>
          </Popup>
        </Marker>
      ))}
    </>
  );
}
