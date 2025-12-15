import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDetailsForm } from '../../hooks/use-details-form';
import { useToast } from '../../hooks/use-toast';
import { Toast } from '../Toast/Toast';
import { AppRoutes } from '../../routes/routes';
import { FlowStep } from '../../models/NewAnnouncementFlow';
import { NewAnnouncementLayout } from './NewAnnouncementLayout';
import { PetDescriptionForm } from './AnnouncementDescriptionForm/PetDescriptionForm';
import styles from './NewAnnouncementLayout.module.css';

export const DetailsScreen: React.FC = () => {
  const navigate = useNavigate();
  const { formData, updateField, handleSubmit, flowState } = useDetailsForm();
  const { message: toastMessage, showToast } = useToast();

  useEffect(() => {
    if (flowState.currentStep === FlowStep.Empty) {
      navigate(AppRoutes.microchip, { replace: true });
    }
  }, [flowState.currentStep, navigate]);

  const handleBack = () => {
    navigate(AppRoutes.photo);
  };

  const handleContinue = () => {
    const isValid = handleSubmit();
    if (isValid) {
      navigate(AppRoutes.contact);
    } else {
      showToast('Please correct the errors below', 5000);
    }
  };

  return (
    <NewAnnouncementLayout title="Animal description" progress="3/4" onBack={handleBack}>
      <h2 className={styles.heading}>Your pet&apos;s details</h2>

      <p className={styles.description}>Fill out the details about the missing animal.</p>

      <PetDescriptionForm
        formData={formData}
        onFieldChange={(field: string, value: string) => updateField(field as keyof typeof formData, value)}
        onSubmit={handleContinue}
      />

      <Toast message={toastMessage} />
    </NewAnnouncementLayout>
  );
};
