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
    iconBgColor: '#DBEAFE',
    title: 'Search Database',
    description: 'Browse through our extensive database of lost and found pets in your area.'
  },
  {
    id: 'report',
    icon: HiOutlineExclamationCircle,
    iconColor: '#EF4444',
    iconBgColor: '#FFE2E2',
    title: 'Report Missing',
    description: 'Quickly report your missing pet with photos and details to help others identify them.'
  },
  {
    id: 'found',
    icon: MdPets,
    iconColor: '#10B981',
    iconBgColor: '#DCFCE7',
    title: 'Found a Pet',
    description: 'Help reunite a found pet with their family by posting their information.'
  },
  {
    id: 'location',
    icon: MdLocationOn,
    iconColor: '#8B5CF6',
    iconBgColor: '#F3E8FF',
    title: 'Location Based',
    description: 'Find pets near you with our location-based search and notification system.'
  }
] as const;

export const HeroSection: React.FC = () => (
  <section className={styles.hero} data-testid="landing.heroSection">
    <div className={styles.content}>
      <h1 className={styles.heading} data-testid="landing.hero.heading">
        Reuniting Lost Pets with Their Families
      </h1>
      <p className={styles.description}>
        Our portal helps connect pet owners with their lost companions. Report a missing pet, browse found animals, and join our community dedicated to bringing pets home safely.
      </p>
      <div className={styles.cardsGrid}>
        {FEATURE_CARDS.map((card) => (
          <FeatureCard
            key={card.id}
            id={card.id}
            icon={card.icon}
            iconColor={card.iconColor}
            iconBgColor={card.iconBgColor}
            title={card.title}
            description={card.description}
          />
        ))}
      </div>
    </div>
  </section>
);

