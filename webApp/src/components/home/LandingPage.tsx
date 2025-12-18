import React from 'react';
import { MapView } from '../map/MapView';
import { Footer } from './Footer';
import { HeroSection } from './HeroSection';
import { RecentPetsSection } from './RecentPetsSection';
import styles from './LandingPage.module.css';

export const LandingPage: React.FC = () => (
  <div className={styles.page}>
    <main className={styles.main}>
      <HeroSection />
      <MapView />
      <RecentPetsSection />
    </main>
    <Footer />
  </div>
);
