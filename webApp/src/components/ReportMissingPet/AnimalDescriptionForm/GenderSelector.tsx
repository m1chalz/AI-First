import React from 'react';
import { ANIMAL_SEXES } from '../../../types/animal';
import { SEX_LABELS } from '../../../utils/display-labels';
import styles from './AnimalDescriptionForm.module.css';

export interface GenderSelectorProps {
  value: string;
  onChange: (sex: string) => void;
  error?: string;
  testId?: string;
}

export const GenderSelector: React.FC<GenderSelectorProps> = ({
  value,
  onChange,
  error,
  testId = 'details.sex.select'
}) => {
  const sexOptions = ANIMAL_SEXES.filter(s => s !== 'UNKNOWN');

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    onChange(e.target.value);
  };

  return (
    <div data-testid={testId}>
      <div className={styles.genderContainer}>
        {sexOptions.map((sex) => (
          <div key={sex} className={styles.genderOption}>
            <input
              type="radio"
              name="sex"
              id={`sex-${sex}`}
              value={sex}
              checked={value === sex}
              onChange={handleChange}
            />
            <label htmlFor={`sex-${sex}`}>{SEX_LABELS[sex]}</label>
          </div>
        ))}
      </div>
      {error && (
        <span role="alert" className={styles.errorMessage}>
          {error}
        </span>
      )}
    </div>
  );
};

