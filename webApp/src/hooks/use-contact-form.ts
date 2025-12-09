import { useState } from 'react';
import { useReportMissingPetFlow } from '../contexts/ReportMissingPetFlowContext';
import { validatePhoneNumber, validateEmailAddress, validateContactForm } from '../utils/form-validation';
import { FlowStep } from '../models/ReportMissingPetFlow';

export interface UseContactFormReturn {
  phone: string;
  email: string;
  reward: string;
  phoneError: string;
  emailError: string;
  handlePhoneChange: (value: string) => void;
  handleEmailChange: (value: string) => void;
  handleRewardChange: (value: string) => void;
  validatePhone: (value: string) => void;
  validateEmail: (value: string) => void;
  handleSubmit: () => boolean;
}

export function useContactForm(): UseContactFormReturn {
  const { flowState, updateFlowState } = useReportMissingPetFlow();
  const [phone, setPhone] = useState(flowState.phone || '');
  const [email, setEmail] = useState(flowState.email || '');
  const [reward, setReward] = useState(flowState.reward || '');
  const [phoneError, setPhoneError] = useState('');
  const [emailError, setEmailError] = useState('');

  const validatePhone = (value: string) => {
    const error = validatePhoneNumber(value);
    setPhoneError(error || '');
  };

  const validateEmail = (value: string) => {
    const error = validateEmailAddress(value);
    setEmailError(error || '');
  };

  const handlePhoneChange = (value: string) => {
    setPhone(value);
  };

  const handleEmailChange = (value: string) => {
    setEmail(value);
  };

  const handleRewardChange = (value: string) => {
    setReward(value);
  };

  const handleSubmit = (): boolean => {
    const validation = validateContactForm({ phone, email });

    setPhoneError(validation.phoneError);
    setEmailError(validation.emailError);

    if (!validation.isValid) {
      return false;
    }

    updateFlowState({
      phone,
      email,
      reward,
      currentStep: FlowStep.Summary
    });

    return true;
  };

  return {
    phone,
    email,
    reward,
    phoneError,
    emailError,
    handlePhoneChange,
    handleEmailChange,
    handleRewardChange,
    validatePhone,
    validateEmail,
    handleSubmit
  };
}
