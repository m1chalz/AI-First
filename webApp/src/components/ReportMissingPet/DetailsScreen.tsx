import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDetailsForm } from '../../hooks/use-details-form';
import { useToast } from '../../hooks/use-toast';
import { AnimalDescriptionForm } from './AnnouncementDescriptionForm/AnimalDescriptionForm';
import { ReportMissingPetLayout } from './ReportMissingPetLayout';
import { Toast } from '../Toast/Toast';
import { ReportMissingPetRoutes } from '../../routes/report-missing-pet-routes';
import { FlowStep } from '../../models/ReportMissingPetFlow';
import styles from './ReportMissingPetLayout.module.css';

export const DetailsScreen: React.FC = () => {
  const navigate = useNavigate();
  const { formData, updateField, handleSubmit, flowState } = useDetailsForm();
  const { message: toastMessage, showToast } = useToast();

  useEffect(() => {
    if (flowState.currentStep === FlowStep.Empty) {
      navigate(ReportMissingPetRoutes.microchip, { replace: true });
    }
  }, [flowState.currentStep, navigate]);

  const handleBack = () => {
    navigate(ReportMissingPetRoutes.photo);
  };

  const handleContinue = () => {
    const isValid = handleSubmit();
    if (isValid) {
      navigate(ReportMissingPetRoutes.contact);
    } else {
      showToast('Please correct the errors below', 5000);
    }
  };

  return (
    <ReportMissingPetLayout title="Animal description" progress="3/4" onBack={handleBack}>
      <h2 className={styles.heading}>Your pet&apos;s details</h2>

      <p className={styles.description}>Fill out the details about the missing animal.</p>

      <AnimalDescriptionForm
        formData={formData}
        onFieldChange={(field: string, value: string) => updateField(field as keyof typeof formData, value)}
        onSubmit={handleContinue}
      />

      <Toast message={toastMessage} />
    </ReportMissingPetLayout>
  );
};
