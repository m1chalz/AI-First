import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDetailsForm } from '../../hooks/use-details-form';
import { useToast } from '../../hooks/use-toast';
import { AnimalDescriptionForm } from './AnimalDescriptionForm/AnimalDescriptionForm';
import { ReportMissingPetLayout } from './ReportMissingPetLayout';
import { Toast } from '../Toast/Toast';
import { ReportMissingPetRoutes } from '../../routes/report-missing-pet-routes';
import styles from './ReportMissingPetLayout.module.css';

export const DetailsScreen: React.FC = () => {
  const navigate = useNavigate();
  const { formData, updateField, handleSubmit, flowState } = useDetailsForm();
  const { message: toastMessage, showToast } = useToast();

  useEffect(() => {
    if (!flowState.photo) {
      navigate(ReportMissingPetRoutes.photo, { replace: true });
    }
  }, [flowState.photo, navigate]);

  const handleBack = () => {
    navigate('/report-missing/photo');
  };

  const handleFormSubmit = () => {
    const isValid = handleSubmit();
    if (isValid) {
      navigate('/report-missing/contact');
    } else {
      showToast('Please correct the errors below', 5000);
    }
  };

  return (
    <ReportMissingPetLayout
      title="Animal description"
      progress="3/4"
      onBack={handleBack}
    >
      <h2 className={styles.heading}>Your pet&apos;s details</h2>
      
      <p className={styles.description}>
        Fill out the details about the missing animal.
      </p>

      <AnimalDescriptionForm
        formData={formData}
        onFieldChange={(field, value) => updateField(field as keyof typeof formData, value)}
        onSubmit={handleFormSubmit}
      />

      <Toast message={toastMessage} />
    </ReportMissingPetLayout>
  );
};
