import React from 'react';
import { SpeciesDropdown } from './SpeciesDropdown';
import { GenderSelector } from './GenderSelector';
import { CharacterCounter } from './CharacterCounter';
import sharedStyles from '../ReportMissingPetLayout.module.css';
import styles from './AnimalDescriptionForm.module.css';

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
    <form className={styles.form} onSubmit={(e) => { e.preventDefault(); onSubmit(); }}>
      <div className={sharedStyles.inputGroup}>
        <label htmlFor="lastSeenDate" className={sharedStyles.label}>Date of disappearance</label>
        <div className={styles.dateInput}>
          <input
            id="lastSeenDate"
            type="date"
            value={formData.lastSeenDate}
            max={today}
            onChange={(e) => onFieldChange('lastSeenDate', e.target.value)}
            className={sharedStyles.input}
            data-testid="details.lastSeenDate.input"
          />
        </div>
        {formData.validationErrors.lastSeenDate && (
          <span role="alert" className={styles.errorMessage}>
            {formData.validationErrors.lastSeenDate}
          </span>
        )}
      </div>

      <div className={sharedStyles.inputGroup}>
        <label htmlFor="species" className={sharedStyles.label}>Animal species</label>
        <SpeciesDropdown
          value={formData.species}
          onChange={(value) => onFieldChange('species', value)}
          error={formData.validationErrors.species}
        />
      </div>

      <div className={sharedStyles.inputGroup}>
        <label htmlFor="breed" className={sharedStyles.label}>Animal race (optional)</label>
        <input
          id="breed"
          type="text"
          value={formData.breed}
          onChange={(e) => onFieldChange('breed', e.target.value)}
          disabled={!formData.species}
          className={sharedStyles.input}
          data-testid="details.breed.input"
        />
      </div>

      <div className={sharedStyles.inputGroup}>
        <label className={sharedStyles.label}>Gender</label>
        <GenderSelector
          value={formData.sex}
          onChange={(value) => onFieldChange('sex', value)}
          error={formData.validationErrors.sex}
        />
      </div>

      <div className={sharedStyles.inputGroup}>
        <label htmlFor="age" className={sharedStyles.label}>Animal age (optional)</label>
        <input
          id="age"
          type="number"
          value={formData.age}
          onChange={(e) => onFieldChange('age', e.target.value)}
          min="0"
          max="40"
          className={sharedStyles.input}
          data-testid="details.age.input"
        />
        {formData.validationErrors.age && (
          <span role="alert" className={styles.errorMessage}>
            {formData.validationErrors.age}
          </span>
        )}
      </div>

      <div>
        <button
          type="button"
          disabled
          className={styles.gpsButton}
          data-testid="details.gpsButton.click"
        >
          Request GPS position
        </button>
      </div>

      <div className={sharedStyles.inputGroup}>
        <label htmlFor="latitude" className={sharedStyles.label}>Lat / Long</label>
        <div className={styles.latLongContainer}>
          <input
            id="latitude"
            type="text"
            value={formData.latitude}
            onChange={(e) => onFieldChange('latitude', e.target.value)}
            className={sharedStyles.input}
            data-testid="details.latitude.input"
            placeholder="0.0000"
          />
          <input
            id="longitude"
            type="text"
            value={formData.longitude}
            onChange={(e) => onFieldChange('longitude', e.target.value)}
            className={sharedStyles.input}
            data-testid="details.longitude.input"
            placeholder="0.0000"
          />
        </div>
        {(formData.validationErrors.latitude || formData.validationErrors.longitude) && (
          <span role="alert" className={styles.errorMessage}>
            {formData.validationErrors.latitude || formData.validationErrors.longitude}
          </span>
        )}
      </div>

      <div className={sharedStyles.inputGroup}>
        <label htmlFor="description" className={sharedStyles.label}>Animal additional description (optional)</label>
        <textarea
          id="description"
          value={formData.description}
          onChange={(e) => onFieldChange('description', e.target.value)}
          maxLength={500}
          className={sharedStyles.textarea}
          data-testid="details.description.textarea"
        />
        <CharacterCounter
          current={formData.description.length}
          max={500}
          isExceeded={formData.description.length > 500}
        />
        {formData.validationErrors.description && (
          <span role="alert" className={styles.errorMessage}>
            {formData.validationErrors.description}
          </span>
        )}
      </div>

      <div>
        <button
          type="submit"
          className={sharedStyles.primaryButton}
          data-testid="details.continue.click"
        >
          Continue
        </button>
      </div>
    </form>
  );
};

