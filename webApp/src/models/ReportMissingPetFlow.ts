import { AnimalSpecies, AnimalSex } from '../types/animal';

export enum FlowStep {
  Empty = 'emtpy',
  Microchip = 'microchip',
  Photo = 'photo',
  Details = 'details',
  Contact = 'contact',
  Summary = 'summary'
}

export interface PhotoAttachment {
  file: File;
  filename: string;
  size: number;
  mimeType: string;
  previewUrl: string | null;
}

export interface ReportMissingPetFlowState {
  currentStep: FlowStep;
  microchipNumber: string;
  photo: PhotoAttachment | null;
  lastSeenDate: string;
  species: AnimalSpecies | null;
  breed: string;
  sex: AnimalSex | null;
  age: number | null;
  petName: string;
  description: string;
  latitude: number | null;
  longitude: number | null;
  phone: string;
  email: string;
  reward: string;
}

export const initialFlowState: ReportMissingPetFlowState = {
  currentStep: FlowStep.Empty,
  microchipNumber: '',
  photo: null,
  lastSeenDate: new Date().toISOString().split('T')[0],
  species: null,
  breed: '',
  sex: null,
  age: null,
  petName: '',
  description: '',
  latitude: null,
  longitude: null,
  phone: '',
  email: '',
  reward: ''
};

export interface ReportMissingPetFlowContextValue {
  flowState: ReportMissingPetFlowState;
  updateFlowState: (updates: Partial<ReportMissingPetFlowState>) => void;
  clearFlowState: () => void;
}

export interface MicrochipNumberFormData {
  value: string;
  formattedValue: string;
}

export interface UseMicrochipFormatterReturn extends MicrochipNumberFormData {
  handleChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  handlePaste: (e: React.ClipboardEvent<HTMLInputElement>) => void;
  reset: () => void;
}
