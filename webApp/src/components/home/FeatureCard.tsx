import React from 'react';
import type { IconType } from 'react-icons';
import styles from './FeatureCard.module.css';

interface FeatureCardProps {
  id: string;
  icon: IconType;
  iconColor: string;
  title: string;
  description: string;
}

export const FeatureCard: React.FC<FeatureCardProps> = ({ id, icon: Icon, iconColor, title, description }) => (
  <div className={styles.card} data-testid={`landing.hero.featureCard.${id}`}>
    <div className={styles.iconContainer} style={{ backgroundColor: iconColor }}>
      <Icon className={styles.icon} />
    </div>
    <h3 className={styles.title}>{title}</h3>
    <p className={styles.description}>{description}</p>
  </div>
);

