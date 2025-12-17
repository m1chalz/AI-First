import React from 'react';
import { HeroSection } from './HeroSection';
import { Footer } from './Footer';
import styles from './LandingPage.module.css';

export const LandingPage: React.FC = () => (
  <div className={styles.page}>
    <main className={styles.main}>
      <HeroSection />
    </main>
    <Footer />
  </div>
);

