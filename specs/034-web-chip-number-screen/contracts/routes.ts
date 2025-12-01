/**
 * Route definitions for the "Report Missing Pet" flow.
 * 
 * This contract defines the URL structure and route configuration
 * for the 4-step flow using React Router v6.
 */

/**
 * Route paths for the "Report Missing Pet" flow.
 */
export const ReportMissingPetRoutes = {
  /** Base path for the flow (redirects to microchip) */
  base: '/report-missing',
  
  /** Step 1: Microchip number entry */
  microchip: '/report-missing/microchip',
  
  /** Step 2: Photo upload (Future implementation) */
  photo: '/report-missing/photo',
  
  /** Step 3: Details entry (Future implementation) */
  details: '/report-missing/details',
  
  /** Step 4: Contact information (Future implementation) */
  contact: '/report-missing/contact',
} as const;

/**
 * Route configuration for React Router v6.
 * 
 * Structure:
 * - /report-missing (base) → Redirects to microchip
 *   - /report-missing/microchip → MicrochipNumberScreen component
 *   - /report-missing/photo → Protected route (requires currentStep >= Photo)
 *   - /report-missing/details → Protected route (requires currentStep >= Details)
 *   - /report-missing/contact → Protected route (requires currentStep >= Contact)
 * 
 * Protection: Steps 2-4 check flow state and redirect to microchip if:
 * - Flow state is empty (user refreshed page)
 * - Current step is before the requested step
 * - User accessed URL directly
 * 
 * @example
 * // React Router configuration
 * <Route path={ReportMissingPetRoutes.base} element={<ReportMissingPetFlowProvider />}>
 *   <Route path="microchip" element={<MicrochipNumberScreen />} />
 *   <Route path="photo" element={<PhotoProtected />} />
 *   <Route index element={<Navigate to="microchip" replace />} />
 * </Route>
 */

import { FlowStep } from './FlowState';

/**
 * Route guard logic for protected steps.
 * Returns true if user can access the step, false otherwise.
 */
export interface RouteGuard {
  /**
   * Check if user can access photo step.
   * Requires: currentStep >= Photo
   */
  canAccessPhoto: (currentStep: FlowStep) => boolean;

  /**
   * Check if user can access details step.
   * Requires: currentStep >= Details
   */
  canAccessDetails: (currentStep: FlowStep) => boolean;

  /**
   * Check if user can access contact step.
   * Requires: currentStep >= Contact
   */
  canAccessContact: (currentStep: FlowStep) => boolean;
}

/**
 * Implementation of route guards (pure functions).
 */
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

/**
 * Navigation helper for programmatic navigation within the flow.
 */
export interface FlowNavigation {
  /**
   * Navigate to the next step in the flow.
   * - From microchip → photo
   * - From photo → details
   * - From details → contact
   * - From contact → pet list (flow complete)
   */
  goToNextStep: (currentStep: FlowStep) => string;

  /**
   * Navigate to the previous step in the flow (used for in-flow back navigation).
   * - From photo → microchip
   * - From details → photo
   * - From contact → details
   * - From microchip → cancel flow, go to pet list
   */
  goToPreviousStep: (currentStep: FlowStep) => string;

  /**
   * Cancel flow and return to pet list.
   */
  cancelFlow: () => string;
}

/**
 * Implementation of flow navigation helpers.
 */
export const flowNavigation: FlowNavigation = {
  goToNextStep: (currentStep) => {
    switch (currentStep) {
      case FlowStep.Microchip: return ReportMissingPetRoutes.photo;
      case FlowStep.Photo: return ReportMissingPetRoutes.details;
      case FlowStep.Details: return ReportMissingPetRoutes.contact;
      case FlowStep.Contact: 
      case FlowStep.Completed:
        return '/pets'; // Flow complete, return to pet list
    }
  },

  goToPreviousStep: (currentStep) => {
    switch (currentStep) {
      case FlowStep.Microchip: return '/pets'; // Cancel flow
      case FlowStep.Photo: return ReportMissingPetRoutes.microchip;
      case FlowStep.Details: return ReportMissingPetRoutes.photo;
      case FlowStep.Contact: return ReportMissingPetRoutes.details;
      case FlowStep.Completed: return ReportMissingPetRoutes.contact;
    }
  },

  cancelFlow: () => '/pets',
};

