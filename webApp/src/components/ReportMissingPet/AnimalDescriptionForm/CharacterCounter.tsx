import React from 'react';
import styles from './AnimalDescriptionForm.module.css';

export interface CharacterCounterProps {
  current: number;
  max: number;
  isExceeded: boolean;
}

export const CharacterCounter: React.FC<CharacterCounterProps> = ({
  current,
  max,
  isExceeded
}) => (
  <span
    data-testid="character-counter"
    className={`${styles.characterCounter} ${isExceeded ? styles.exceeded : ''}`}
  >
    {current}/{max} characters
  </span>
);

