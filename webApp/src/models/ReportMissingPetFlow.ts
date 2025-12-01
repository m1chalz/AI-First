export enum FlowStep {
  Microchip = 'microchip',
  Photo = 'photo',
  Details = 'details',
  Contact = 'contact',
  Completed = 'completed',
}

export interface ReportMissingPetFlowState {
  currentStep: FlowStep;
  microchipNumber: string;
}

export const initialFlowState: ReportMissingPetFlowState = {
  currentStep: FlowStep.Microchip,
  microchipNumber: '',
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

