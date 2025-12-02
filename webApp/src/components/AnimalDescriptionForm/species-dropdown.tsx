import React from 'react';
import { ANIMAL_SPECIES } from '../../types/animal';
import { SPECIES_LABELS } from '../../utils/display-labels';

export interface SpeciesDropdownProps {
  value: string;
  onChange: (species: string) => void;
  error?: string;
  testId?: string;
}

export const SpeciesDropdown: React.FC<SpeciesDropdownProps> = ({
  value,
  onChange,
  error,
  testId = 'animalDescription.species.select'
}) => {

  const handleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    onChange(e.target.value);
  };

  return (
    <div>
      <select
        value={value}
        onChange={handleChange}
        data-testid={testId}
      >
        <option value="">Select species</option>
        {ANIMAL_SPECIES.map((s) => (
          <option key={s} value={s}>
            {SPECIES_LABELS[s]}
          </option>
        ))}
      </select>
      {error && (
        <span role="alert" style={{ color: 'red', display: 'block', marginTop: '4px' }}>
          {error}
        </span>
      )}
    </div>
  );
};

