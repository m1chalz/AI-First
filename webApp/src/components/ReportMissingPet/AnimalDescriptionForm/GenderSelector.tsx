import React from 'react';
import { ANIMAL_SEXES } from '../../../types/animal';
import { SEX_LABELS } from '../../../utils/display-labels';

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
      {sexOptions.map((sex) => (
        <label key={sex} style={{ marginRight: '16px', display: 'inline-block' }}>
          <input
            type="radio"
            name="sex"
            value={sex}
            checked={value === sex}
            onChange={handleChange}
          />
          {' '}{SEX_LABELS[sex]}
        </label>
      ))}
      {error && (
        <span role="alert" style={{ color: 'red', display: 'block', marginTop: '4px' }}>
          {error}
        </span>
      )}
    </div>
  );
};

