import React from 'react';
import { MdCalendarToday, MdPhone, MdEmail, MdLocationOn, MdAttachMoney } from 'react-icons/md';
import type { Announcement, AnnouncementStatus } from '../../types/announcement';
import { ANNOUNCEMENT_STATUS_BADGE_COLORS } from '../../types/announcement';
import { formatDate } from '../../utils/date-formatter';
import { formatCoordinates } from '../../utils/coordinate-formatter';
import { formatMicrochip } from '../../utils/microchip-formatter';
import toPascalCase from '../../utils/pascal-case-formatter';
import { buildMapUrl } from '../../utils/map-url-builder';
import styles from './AnnouncementDetailsContent.module.css';
import config from '../../config/config';

interface AnnouncementDetailsContentProps {
  announcement: Announcement;
}

const getGenderIcon = (sex: string): string => {
  if (sex === 'MALE') return '♂';
  if (sex === 'FEMALE') return '♀';
  return '';
};

const getStatusBadgeColor = (status: AnnouncementStatus): string => ANNOUNCEMENT_STATUS_BADGE_COLORS[status] || '#93A2B4';

export const AnnouncementDetailsContent: React.FC<AnnouncementDetailsContentProps> = ({ announcement }) => {
  const statusColor = getStatusBadgeColor(announcement.status);
  const formattedDate = formatDate(announcement.lastSeenDate);
  const hasLocation = announcement.locationLatitude !== null && announcement.locationLongitude !== null;
  const mapUrl =
    hasLocation && announcement.locationLatitude !== null && announcement.locationLongitude !== null
      ? buildMapUrl(announcement.locationLatitude, announcement.locationLongitude)
      : null;

  return (
    <div className={styles.content}>
      {/* Hero Image Section */}
      {announcement.photoUrl && (
        <div className={styles.heroSection}>
          <img
            src={`${config.apiBaseUrl}${announcement.photoUrl}`}
            alt={announcement.petName ? `${announcement.petName} photo` : 'Announcement photo'}
            className={styles.heroImage}
          />
          {/* Status Badge Overlay - Top Right */}
          <div className={styles.statusBadge} style={{ backgroundColor: statusColor }}>
            {announcement.status}
          </div>
          {/* Reward Badge Overlay - Bottom Left */}
          {announcement.reward && (
            <div className={styles.rewardBadge}>
              <MdAttachMoney className={styles.rewardIcon} />
              <span>Reward {announcement.reward}</span>
            </div>
          )}
        </div>
      )}

      {/* Content Section */}
      <div className={styles.contentSection}>
        {/* Header Row - 3 columns: Date, Phone, Email */}
        <div className={styles.headerRow}>
          <div className={styles.headerItem}>
            <MdCalendarToday className={styles.headerIcon} />
            <div className={styles.headerContent}>
              <span className={styles.headerLabel}>Date of Disappearance</span>
              <span className={styles.headerValue}>{formattedDate}</span>
            </div>
          </div>
          <div className={styles.headerItem}>
            <MdPhone className={styles.headerIcon} />
            <div className={styles.headerContent}>
              <span className={styles.headerLabel}>Contact owner</span>
              <span className={styles.headerValue}>{announcement.phone || '—'}</span>
            </div>
          </div>
          <div className={styles.headerItem}>
            <MdEmail className={styles.headerIcon} />
            <div className={styles.headerContent}>
              <span className={styles.headerLabel}>Contact owner</span>
              <span className={styles.headerValue}>{announcement.email || '—'}</span>
            </div>
          </div>
        </div>

        {/* Details - 2-column grid */}
        <div className={styles.section}>
          <div className={styles.gridRow}>
            <div className={styles.gridItem}>
              <span className={styles.fieldLabel}>Animal Name</span>
              <span className={styles.fieldValue} data-testid="announcementDetails.name.value">
                {announcement.petName || '—'}
              </span>
            </div>
            <div className={styles.gridItem}>
              <span className={styles.fieldLabel}>Microchip number</span>
              <span className={styles.fieldValue} data-testid="announcementDetails.microchip.value">
                {announcement.microchipNumber ? formatMicrochip(announcement.microchipNumber) : '—'}
              </span>
            </div>
          </div>
          <div className={styles.gridRow}>
            <div className={styles.gridItem}>
              <span className={styles.fieldLabel}>Animal Species</span>
              <span className={styles.fieldValue} data-testid="announcementDetails.species.value">
                {toPascalCase(announcement.species)}
              </span>
            </div>
            <div className={styles.gridItem}>
              <span className={styles.fieldLabel}>Animal Race</span>
              <span className={styles.fieldValue} data-testid="announcementDetails.breed.value">
                {announcement.breed || '—'}
              </span>
            </div>
          </div>
          <div className={styles.gridRow}>
            <div className={styles.gridItem}>
              <span className={styles.fieldLabel}>Animal Sex</span>
              <span className={styles.fieldValue} data-testid="announcementDetails.sex.value">
                {announcement.sex !== 'UNKNOWN' ? `${toPascalCase(announcement.sex)} ${getGenderIcon(announcement.sex)}` : '—'}
              </span>
            </div>
            <div className={styles.gridItem}>
              <span className={styles.fieldLabel}>Animal Approx. Age</span>
              <span className={styles.fieldValue} data-testid="announcementDetails.age.value">
                {announcement.age !== null ? `${announcement.age} years` : '—'}
              </span>
            </div>
          </div>
        </div>

        {/* Location Information */}
        {hasLocation && (
          <div className={styles.section}>
            <div className={styles.locationRow}>
              <MdLocationOn className={styles.locationIcon} />
              <div className={styles.locationContent}>
                <span className={styles.fieldLabel}>Lat / Long</span>
                <span className={styles.fieldValue}>
                  {announcement.locationLatitude !== null && announcement.locationLongitude !== null
                    ? formatCoordinates(announcement.locationLatitude, announcement.locationLongitude)
                    : '—'}
                </span>
              </div>
              {mapUrl && (
                <a
                  href={mapUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className={styles.mapButton}
                  data-testid="announcementDetails.mapButton.click"
                >
                  Show on the map
                </a>
              )}
            </div>
          </div>
        )}

        {/* Descriptive Information */}
        <div className={styles.section}>
          <div className={styles.fieldRow}>
            <span className={styles.fieldLabel}>Animal Additional Description</span>
            <span className={styles.fieldValue}>{announcement.description || '—'}</span>
          </div>
        </div>
      </div>
    </div>
  );
};
