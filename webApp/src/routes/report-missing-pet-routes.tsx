import { FlowStep } from '../models/ReportMissingPetFlow';

export const ReportMissingPetRoutes = {
  base: '/report-missing',
  microchip: '/report-missing/microchip',
  photo: '/report-missing/photo',
  details: '/report-missing/details',
  contact: '/report-missing/contact',
} as const;

export interface RouteGuard {
  canAccessPhoto: (currentStep: FlowStep) => boolean;
  canAccessDetails: (currentStep: FlowStep) => boolean;
  canAccessContact: (currentStep: FlowStep) => boolean;
}

export const routeGuards: RouteGuard = {
  canAccessPhoto: (currentStep) => 
    currentStep === FlowStep.Photo || 
    currentStep === FlowStep.Details || 
    currentStep === FlowStep.Contact || 
    currentStep === FlowStep.Completed,
  
  canAccessDetails: (currentStep) => 
    currentStep === FlowStep.Details || 
    currentStep === FlowStep.Contact || 
    currentStep === FlowStep.Completed,
  
  canAccessContact: (currentStep) => 
    currentStep === FlowStep.Contact || 
    currentStep === FlowStep.Completed,
};

export interface FlowNavigation {
  goToNextStep: (currentStep: FlowStep) => string;
  goToPreviousStep: (currentStep: FlowStep) => string;
  cancelFlow: () => string;
}

export const flowNavigation: FlowNavigation = {
  goToNextStep: (currentStep) => {
    switch (currentStep) {
      case FlowStep.Microchip: return ReportMissingPetRoutes.photo;
      case FlowStep.Photo: return ReportMissingPetRoutes.details;
      case FlowStep.Details: return ReportMissingPetRoutes.contact;
      case FlowStep.Contact: 
      case FlowStep.Completed:
        return '/';
      default:
        return ReportMissingPetRoutes.microchip;
    }
  },

  goToPreviousStep: (currentStep) => {
    switch (currentStep) {
      case FlowStep.Microchip: return '/';
      case FlowStep.Photo: return ReportMissingPetRoutes.microchip;
      case FlowStep.Details: return ReportMissingPetRoutes.photo;
      case FlowStep.Contact: return ReportMissingPetRoutes.details;
      case FlowStep.Completed: return ReportMissingPetRoutes.contact;
      default:
        return '/';
    }
  },

  cancelFlow: () => '/',
};

