import React from 'react';
import { MdSearch, MdPets, MdLocationOn } from 'react-icons/md';
import { HiOutlineExclamationCircle } from 'react-icons/hi';
import { FeatureCard } from './FeatureCard';
import styles from './HeroSection.module.css';

const FEATURE_CARDS = [
  {
    id: 'search',
    icon: MdSearch,
    iconColor: '#3B82F6',
    title: 'Search Database',
    description: 'Browse through our database of lost and found pets in your area'
  },
  {
    id: 'report',
    icon: HiOutlineExclamationCircle,
    iconColor: '#EF4444',
    title: 'Report Missing',
    description: 'Quickly create a report for your missing pet with photos and details'
  },
  {
    id: 'found',
    icon: MdPets,
    iconColor: '#10B981',
    title: 'Found a Pet',
    description: 'Help reunite found pets with their owners by reporting sightings'
  },
  {
    id: 'location',
    icon: MdLocationOn,
    iconColor: '#8B5CF6',
    title: 'Location Based',
    description: 'Find pets near your location with our map-based search feature'
  }
] as const;

export const HeroSection: React.FC = () => (
  <section className={styles.hero} data-testid="landing.heroSection">
    <div className={styles.content}>
      <h1 className={styles.heading} data-testid="landing.hero.heading">
        Reunite with Your <span className={styles.highlight}>Beloved Pet</span>
      </h1>
      <p className={styles.description}>
        Helping pet owners find their lost companions and connect with people who have found pets in their community.
      </p>
      <div className={styles.cardsGrid}>
        {FEATURE_CARDS.map((card) => (
          <FeatureCard
            key={card.id}
            id={card.id}
            icon={card.icon}
            iconColor={card.iconColor}
            title={card.title}
            description={card.description}
          />
        ))}
      </div>
    </div>
  </section>
);

