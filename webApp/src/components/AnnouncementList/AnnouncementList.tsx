import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAnnouncementList } from '../../hooks/use-announcement-list';
import { useModal } from '../../hooks/use-modal';
import { AnnouncementCard } from './AnnouncementCard';
import { EmptyState } from './EmptyState';
import { LocationBanner } from '../LocationBanner/LocationBanner';
import { AnnouncementDetailsModal } from '../AnnouncementDetailsModal/AnnouncementDetailsModal';
import styles from './AnnouncementList.module.css';

export const AnnouncementList: React.FC = () => {
  const navigate = useNavigate();
  const { announcements, isLoading, error, isEmpty, geolocationError } = useAnnouncementList();

  const { isOpen, selectedAnnouncementId, openModal, closeModal } = useModal();
  const [isBannerDismissed, setIsBannerDismissed] = useState(false);

  const showLocationBanner = geolocationError?.code === 1 && !isBannerDismissed;

  return (
    <div className={styles.container}>
      <div className={styles.mainContent}>
        <header className={styles.header}>
          <h1 className={styles.title}>PetSpot</h1>

          <div className={styles.headerButtons}>
            <button
              className={styles.primaryButton}
              onClick={() => navigate('/report-missing/microchip')}
              data-testid="announcementList.reportMissingButton"
            >
              Report a Missing Animal
            </button>
          </div>
        </header>

        <div className={styles.content}>
          {showLocationBanner && <LocationBanner onClose={() => setIsBannerDismissed(true)} />}
          {isLoading ? (
            <div className={styles.loading} data-testid="announcementList.loading">
              <div className={styles.spinner}></div>
              <p>Loading announcements...</p>
            </div>
          ) : error ? (
            <div className={styles.error}>
              <p>Error: {error}</p>
            </div>
          ) : isEmpty ? (
            <EmptyState />
          ) : (
            <div className={styles.announcementList} data-testid="announcementList.list">
              {announcements.map((announcement) => (
                <AnnouncementCard key={announcement.id} announcement={announcement} onDetailsClick={() => openModal(announcement.id)} />
              ))}
            </div>
          )}
        </div>
      </div>

      <AnnouncementDetailsModal isOpen={isOpen} selectedAnnouncementId={selectedAnnouncementId} onClose={closeModal} />
    </div>
  );
};

// Backward compatibility alias
export const AnimalList = AnnouncementList;
