import React from 'react';
import { SpeciesDropdown } from './SpeciesDropdown';
import { GenderSelector } from './GenderSelector';
import { CharacterCounter } from './CharacterCounter';

export interface AnimalDescriptionFormProps {
  formData: {
    lastSeenDate: string;
    species: string;
    breed: string;
    sex: string;
    age: string;
    description: string;
    latitude: string;
    longitude: string;
    validationErrors: Record<string, string>;
  };
  onFieldChange: (field: string, value: string) => void;
  onSubmit: () => void;
}

export const AnimalDescriptionForm: React.FC<AnimalDescriptionFormProps> = ({
  formData,
  onFieldChange,
  onSubmit
}) => {
  const today = new Date().toISOString().split('T')[0];

  return (
    <form onSubmit={(e) => { e.preventDefault(); onSubmit(); }}>
      <div>
        <label htmlFor="lastSeenDate">Date of disappearance</label>
        <input
          id="lastSeenDate"
          type="date"
          value={formData.lastSeenDate}
          max={today}
          onChange={(e) => onFieldChange('lastSeenDate', e.target.value)}
          data-testid="details.lastSeenDate.input"
        />
        {formData.validationErrors.lastSeenDate && (
          <span role="alert" style={{ color: 'red', display: 'block', marginTop: '4px' }}>
            {formData.validationErrors.lastSeenDate}
          </span>
        )}
      </div>

      <div>
        <label htmlFor="species">Species</label>
        <SpeciesDropdown
          value={formData.species}
          onChange={(value) => onFieldChange('species', value)}
          error={formData.validationErrors.species}
        />
      </div>

      <div>
        <label htmlFor="breed">Breed/Race</label>
        <input
          id="breed"
          type="text"
          value={formData.breed}
          onChange={(e) => onFieldChange('breed', e.target.value)}
          disabled={!formData.species}
          data-testid="details.breed.input"
        />
        {formData.validationErrors.breed && (
          <span role="alert" style={{ color: 'red', display: 'block', marginTop: '4px' }}>
            {formData.validationErrors.breed}
          </span>
        )}
      </div>

      <div>
        <label>Gender</label>
        <GenderSelector
          value={formData.sex}
          onChange={(value) => onFieldChange('sex', value)}
          error={formData.validationErrors.sex}
        />
      </div>

      <div>
        <label htmlFor="age">Age (optional)</label>
        <input
          id="age"
          type="number"
          value={formData.age}
          onChange={(e) => onFieldChange('age', e.target.value)}
          min="0"
          max="40"
          data-testid="details.age.input"
        />
        {formData.validationErrors.age && (
          <span role="alert" style={{ color: 'red', display: 'block', marginTop: '4px' }}>
            {formData.validationErrors.age}
          </span>
        )}
      </div>

      <div>
        <label htmlFor="description">Additional description (optional)</label>
        <textarea
          id="description"
          value={formData.description}
          onChange={(e) => onFieldChange('description', e.target.value)}
          maxLength={500}
          data-testid="details.description.textarea"
        />
        <CharacterCounter
          current={formData.description.length}
          max={500}
          isExceeded={formData.description.length > 500}
        />
        {formData.validationErrors.description && (
          <span role="alert" style={{ color: 'red', display: 'block', marginTop: '4px' }}>
            {formData.validationErrors.description}
          </span>
        )}
      </div>

      <div>
        <button
          type="button"
          disabled
          data-testid="details.gpsButton.click"
        >
          Request GPS position
        </button>
      </div>

      <div>
        <button
          type="submit"
          data-testid="details.continue.click"
        >
          Continue
        </button>
      </div>
    </form>
  );
};

