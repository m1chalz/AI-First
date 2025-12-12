import React from 'react';
import { MdLocationOn } from 'react-icons/md';
import { ANNOUNCEMENT_STATUS_BADGE_COLORS, type Announcement, type AnnouncementSex } from '../../types/announcement';
import { formatCoordinates } from '../../utils/coordinate-formatter';
import toPascalCase from '../../utils/pascal-case-formatter';
import config from '../../config/config';
import styles from './AnnouncementList.module.css';

interface AnnouncementCardProps {
  announcement: Announcement;
  onDetailsClick: (announcementId: string) => void;
}

export const AnnouncementCard: React.FC<AnnouncementCardProps> = ({ announcement, onDetailsClick }) => {
  const statusColor = ANNOUNCEMENT_STATUS_BADGE_COLORS[announcement.status] || '#FF0000';

  const getGenderIcon = (sex: AnnouncementSex): string => {
    if (sex === 'MALE') return '♂';
    if (sex === 'FEMALE') return '♀';
    return '';
  };

  return (
    <div className={styles.announcementCard} data-testid={`announcementList.item.${announcement.id}`}>
      {/* Photo placeholder - circular with optional photo */}
      <div className={styles.photoPlaceholder}>
        {announcement.photoUrl ? (
          <img
            src={`${config.apiBaseUrl}${announcement.photoUrl}`}
            alt={`${announcement.petName || 'Announcement'} photo`}
            className={styles.photoImage}
            loading="lazy"
          />
        ) : (
          <span className={styles.photoIcon} aria-hidden="true">
            🐾
          </span>
        )}
      </div>

      {/* Left section: Location and species/breed info */}
      <div className={styles.announcementBasicInfo}>
        <div className={styles.locationRow}>
          <MdLocationOn className={styles.locationIcon} />
          <span className={styles.locationText}>
            {announcement.locationLatitude !== null && announcement.locationLongitude !== null
              ? formatCoordinates(announcement.locationLatitude, announcement.locationLongitude)
              : 'Location not available'}
          </span>
        </div>

        <div className={styles.speciesRow}>
          <span className={styles.speciesText}>{toPascalCase(announcement.species)}</span>
          <span className={styles.separator}>|</span>
          {announcement.breed && <span className={styles.breedText}>{announcement.breed}</span>}
          {announcement.sex !== 'UNKNOWN' && <span className={styles.genderIcon}>{getGenderIcon(announcement.sex)}</span>}
        </div>
      </div>

      {/* Center section: Description */}
      <div className={styles.announcementDescription}>{announcement.description}</div>

      {/* Right section: Status and date (left column) + Details button (right column) */}
      <div className={styles.announcementStatusSection}>
        <div className={styles.statusDateGroup}>
          <div className={styles.statusBadge} style={{ backgroundColor: statusColor }}>
            {announcement.status}
          </div>

          <div className={styles.announcementDate}>{announcement.lastSeenDate}</div>
        </div>

        <button
          className={styles.detailsButton}
          onClick={() => onDetailsClick(announcement.id)}
          data-testid="announcementList.card.detailsButton.click"
        >
          Details
        </button>
      </div>
    </div>
  );
};
