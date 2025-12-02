import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useDetailsForm } from '../../hooks/use-details-form';
import { AnimalDescriptionForm } from './AnimalDescriptionForm/AnimalDescriptionForm';
import { ReportMissingPetLayout } from './ReportMissingPetLayout';
import styles from './ReportMissingPetLayout.module.css';

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
    <ReportMissingPetLayout
      title="Animal description"
      progress="3/4"
      onBack={handleBack}
    >
      <h2 className={styles.heading}>Your pet's details</h2>
      
      <p className={styles.description}>
        Fill out the details about the missing animal.
      </p>

      <AnimalDescriptionForm
        formData={formData}
        onFieldChange={(field, value) => updateField(field as keyof typeof formData, value)}
        onSubmit={handleFormSubmit}
      />
    </ReportMissingPetLayout>
  );
};
