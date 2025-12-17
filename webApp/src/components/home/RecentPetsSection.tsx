import React, { useMemo } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAnnouncementList } from '../../hooks/use-announcement-list';
import { useGeolocationContext } from '../../contexts/GeolocationContext';
import { AppRoutes } from '../../pages/routes';
import { LandingPageCard } from './LandingPageCard';
import styles from './RecentPetsSection.module.css';

const MAX_RECENT_PETS = 5;

export const RecentPetsSection: React.FC = () => {
  const { announcements, isLoading, error, isEmpty } = useAnnouncementList();
  const { state: geolocation } = useGeolocationContext();
  const navigate = useNavigate();

  const recentMissingPets = useMemo(() => {
    const missingPets = announcements.filter((a) => a.status === 'MISSING');
    const sorted = [...missingPets].sort((a, b) => {
      const dateA = a.createdAt ? new Date(a.createdAt).getTime() : 0;
      const dateB = b.createdAt ? new Date(b.createdAt).getTime() : 0;
      return dateB - dateA;
    });
    return sorted.slice(0, MAX_RECENT_PETS);
  }, [announcements]);

  const handleCardClick = (id: string) => {
    navigate(AppRoutes.lostPetDetails(id));
  };

  const showEmptyState = !isLoading && !error && (isEmpty || recentMissingPets.length === 0);

  return (
    <section className={styles.section} data-testid="landing.recentPetsSection">
      <div className={styles.container}>
        <div className={styles.header}>
          <h2 className={styles.heading} data-testid="landing.recentPets.heading">
            Recently Lost Pets
          </h2>
          <Link
            to={AppRoutes.lostPets}
            className={styles.viewAllLink}
            data-testid="landing.recentPets.viewAllLink.click"
          >
            View all →
          </Link>
        </div>

        {isLoading && (
          <div className={styles.loadingContainer} data-testid="landing.recentPets.loading">
            <div className={styles.spinner} />
            <p className={styles.loadingText}>Loading recent pets...</p>
          </div>
        )}

        {error && (
          <div className={styles.errorContainer} data-testid="landing.recentPets.error">
            <p className={styles.errorText}>Unable to load recent pets. Please refresh the page to try again.</p>
          </div>
        )}

        {showEmptyState && (
          <div className={styles.emptyContainer} data-testid="landing.recentPets.emptyState">
            <p className={styles.emptyText}>No recent lost pet reports. Check back soon!</p>
          </div>
        )}

        {!isLoading && !error && recentMissingPets.length > 0 && (
          <div className={styles.cardsGrid}>
            {recentMissingPets.map((announcement) => (
              <LandingPageCard
                key={announcement.id}
                announcement={announcement}
                userCoordinates={geolocation.coordinates}
                onClick={handleCardClick}
              />
            ))}
          </div>
        )}
      </div>
    </section>
  );
};

