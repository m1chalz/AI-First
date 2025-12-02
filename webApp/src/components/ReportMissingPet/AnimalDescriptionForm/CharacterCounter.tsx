import React from 'react';

export interface CharacterCounterProps {
  current: number;
  max: number;
  isExceeded: boolean;
}

export const CharacterCounter: React.FC<CharacterCounterProps> = ({
  current,
  max,
  isExceeded
}) => {
  return (
    <span
      data-testid="character-counter"
      className={isExceeded ? 'exceeded' : ''}
      style={{ color: isExceeded ? 'red' : 'gray', fontSize: '14px' }}
    >
      {current}/{max} characters
    </span>
  );
};

