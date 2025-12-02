import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useDetailsForm } from '../../hooks/use-details-form';
import { AnimalDescriptionForm } from './AnimalDescriptionForm/AnimalDescriptionForm';

export const DetailsScreen: React.FC = () => {
  const navigate = useNavigate();
  const { formData, updateField, handleSubmit } = useDetailsForm();

  const handleBack = () => {
    navigate('/report-missing/photo');
  };

  const handleFormSubmit = () => {
    const isValid = handleSubmit();
    if (isValid) {
      navigate('/report-missing/contact');
    }
  };

  return (
    <div>
      <header>
        <button
          onClick={handleBack}
          data-testid="details.back.click"
          type="button"
        >
          ← Back
        </button>
        <h1>Animal Description</h1>
        <span data-testid="details.progress.text">Step 3/4</span>
      </header>

      <AnimalDescriptionForm
        formData={formData}
        onFieldChange={(field, value) => updateField(field as keyof typeof formData, value)}
        onSubmit={handleFormSubmit}
      />
    </div>
  );
};
