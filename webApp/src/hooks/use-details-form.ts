import { useState, useEffect } from 'react';
import { useReportMissingPetFlow } from '../contexts/ReportMissingPetFlowContext';
import { validateAllFields } from '../utils/form-validation';
import { AnimalSpecies, AnimalSex } from '../types/animal';
import { FlowStep } from '../models/ReportMissingPetFlow';

export interface DetailsFormData {
  lastSeenDate: string;
  species: string;
  breed: string;
  sex: string;
  age: string;
  description: string;
  latitude: string;
  longitude: string;
  validationErrors: Record<string, string>;
}

export interface UseDetailsFormReturn {
  formData: DetailsFormData;
  updateField: (field: keyof DetailsFormData, value: string) => void;
  handleSubmit: () => boolean;
  flowState: ReturnType<typeof useReportMissingPetFlow>['flowState'];
}

export function useDetailsForm(): UseDetailsFormReturn {
  const { flowState, updateFlowState } = useReportMissingPetFlow();
  
  const [formData, setFormData] = useState<DetailsFormData>({
    lastSeenDate: flowState.lastSeenDate || new Date().toISOString().split('T')[0],
    species: flowState.species || '',
    breed: flowState.breed || '',
    sex: flowState.sex || '',
    age: flowState.age !== null ? String(flowState.age) : '',
    description: flowState.description || '',
    latitude: flowState.latitude !== null ? String(flowState.latitude) : '',
    longitude: flowState.longitude !== null ? String(flowState.longitude) : '',
    validationErrors: {},
  });

  const [previousSpecies, setPreviousSpecies] = useState(formData.species);

  useEffect(() => {
    if (formData.species !== previousSpecies && previousSpecies !== '') {
      setFormData(prev => ({ ...prev, breed: '' }));
      setPreviousSpecies(formData.species);
    }
  }, [formData.species, previousSpecies]);

  const updateField = (field: keyof DetailsFormData, value: string) => {
    if (field === 'validationErrors') {
      return;
    }
    
    if (field === 'species' && value !== formData.species && formData.species !== '') {
      setFormData(prev => ({ ...prev, [field]: value, breed: '' }));
      setPreviousSpecies(value);
    } else {
      setFormData(prev => ({ ...prev, [field]: value }));
    }
  };

  const handleSubmit = (): boolean => {
    const errors = validateAllFields({
      lastSeenDate: formData.lastSeenDate,
      species: formData.species,
      breed: formData.breed,
      sex: formData.sex,
      age: formData.age,
      description: formData.description,
      latitude: formData.latitude,
      longitude: formData.longitude,
    });

    if (Object.keys(errors).length > 0) {
      setFormData(prev => ({ ...prev, validationErrors: errors }));
      return false;
    }

    setFormData(prev => ({ ...prev, validationErrors: {} }));

    updateFlowState({
      lastSeenDate: formData.lastSeenDate,
      species: formData.species as AnimalSpecies,
      breed: formData.breed,
      sex: formData.sex as AnimalSex,
      age: formData.age ? Number(formData.age) : null,
      description: formData.description,
      latitude: formData.latitude ? Number(formData.latitude) : null,
      longitude: formData.longitude ? Number(formData.longitude) : null,
      currentStep: FlowStep.Contact,
    });

    return true;
  };

  return {
    formData,
    updateField,
    handleSubmit,
    flowState,
  };
}

