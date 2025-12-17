import React from 'react';
import { MdLocationOn } from 'react-icons/md';
import { HiOutlineCalendar } from 'react-icons/hi';
import { ANNOUNCEMENT_STATUS_BADGE_COLORS, type Announcement } from '../../types/announcement';
import { formatDateDDMMYYYY } from '../../lib/date-utils';
import { formatLocationOrDistance } from '../../lib/distance-utils';
import { formatCoordinates } from '../../utils/coordinate-formatter';
import toPascalCase from '../../utils/pascal-case-formatter';
import config from '../../config/config';
import type { Coordinates } from '../../types/location';
import styles from './LandingPageCard.module.css';

interface LandingPageCardProps {
  announcement: Announcement;
  userCoordinates: Coordinates | null;
  onClick: (id: string) => void;
}

export const LandingPageCard: React.FC<LandingPageCardProps> = ({ announcement, userCoordinates, onClick }) => {
  const statusColor = ANNOUNCEMENT_STATUS_BADGE_COLORS[announcement.status];
  const speciesBreed = announcement.breed
    ? `${toPascalCase(announcement.species)} • ${announcement.breed}`
    : toPascalCase(announcement.species);

  const locationText = formatLocationOrDistance(
    userCoordinates,
    announcement.locationLatitude,
    announcement.locationLongitude,
    formatCoordinates
  );

  const handleClick = () => onClick(announcement.id);
  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      onClick(announcement.id);
    }
  };

  return (
    <div
      className={styles.card}
      data-testid={`landing.recentPets.petCard.${announcement.id}`}
      onClick={handleClick}
      onKeyDown={handleKeyDown}
      role="button"
      tabIndex={0}
    >
      <div className={styles.photoContainer}>
        {announcement.photoUrl ? (
          <img
            src={`${config.apiBaseUrl}${announcement.photoUrl}`}
            alt={announcement.petName || 'Pet photo'}
            className={styles.photo}
            loading="lazy"
          />
        ) : (
          <div className={styles.photoPlaceholder}>
            <span aria-hidden="true">🐾</span>
          </div>
        )}
        <span className={styles.statusBadge} style={{ backgroundColor: statusColor }}>
          {announcement.status}
        </span>
      </div>

      <div className={styles.content}>
        <p className={styles.speciesBreed}>{speciesBreed}</p>

        <div className={styles.infoRow}>
          <MdLocationOn className={styles.icon} />
          <span className={styles.infoText}>{locationText}</span>
        </div>

        <div className={styles.infoRow}>
          <HiOutlineCalendar className={styles.icon} />
          <span className={styles.infoText}>
            {formatDateDDMMYYYY(announcement.createdAt)}
          </span>
        </div>
      </div>
    </div>
  );
};
