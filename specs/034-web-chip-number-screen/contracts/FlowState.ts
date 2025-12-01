/**
 * TypeScript interfaces for the "Report Missing Pet" flow state.
 * 
 * This contract defines the data structure shared across all 4 steps of the flow.
 * Implementation: React Context (ReportMissingPetFlowContext)
 * Persistence: In-memory only (no localStorage/sessionStorage)
 */

/**
 * Flow steps enum for the "Report Missing Pet" flow.
 */
export enum FlowStep {
  /** Step 1: Microchip number entry */
  Microchip = 'microchip',
  /** Step 2: Photo upload */
  Photo = 'photo',
  /** Step 3: Details entry */
  Details = 'details',
  /** Step 4: Contact information */
  Contact = 'contact',
  /** Flow completed */
  Completed = 'completed',
}

/**
 * Complete flow state for the "Report Missing Pet" 4-step flow.
 * 
 * This state persists across steps as user progresses through the flow.
 * Cleared when user cancels (back button), refreshes page, or completes flow.
 */
export interface ReportMissingPetFlowState {
  /**
   * Current step in the flow.
   * Determines which steps the user can access via route guards.
   * 
   * - 'microchip': User is on or has completed step 1
   * - 'photo': User has completed step 1 and is on/completed step 2
   * - 'details': User has completed steps 1-2 and is on/completed step 3
   * - 'contact': User has completed steps 1-3 and is on/completed step 4
   * - 'completed': User has finished all steps
   */
  currentStep: FlowStep;

  /**
   * Microchip number entered in step 1 (digits only, no hyphens).
   * - Format: String of 0-15 digits (e.g., "123456789012345")
   * - Empty string if user skipped this optional field
   * - Leading zeros preserved
   */
  microchipNumber: string;
}

/**
 * Initial/default state for the flow.
 * Used when flow is created or reset.
 */
export const initialFlowState: ReportMissingPetFlowState = {
  currentStep: FlowStep.Microchip,
  microchipNumber: '',
};

/**
 * React Context value type for the flow state.
 * Provides state access and update methods to components.
 */
export interface ReportMissingPetFlowContextValue {
  /**
   * Current flow state.
   */
  flowState: ReportMissingPetFlowState;

  /**
   * Update specific fields in the flow state.
   * Merges provided fields with current state (immutable update).
   * 
   * @param updates - Partial state object with fields to update
   * 
   * @example
   * updateFlowState({ microchipNumber: '123456789012345', currentStep: FlowStep.Photo });
   */
  updateFlowState: (updates: Partial<ReportMissingPetFlowState>) => void;

  /**
   * Clear all flow state and reset to initial values.
   * Called when user cancels flow or completes it.
   * 
   * @example
   * clearFlowState(); // Resets to initialFlowState
   */
  clearFlowState: () => void;
}

/**
 * Component-local state for the microchip number input field.
 * Managed by useMicrochipFormatter hook.
 * NOT persisted to flow state until user clicks "Continue".
 */
export interface MicrochipNumberFormData {
  /**
   * Raw digits entered by user (no hyphens).
   * Max 15 characters, digits only.
   * 
   * @example "123456789012345"
   */
  value: string;

  /**
   * Formatted value with hyphens for display (00000-00000-00000 format).
   * Used only for rendering in input field.
   * 
   * @example "12345-67890-12345"
   */
  formattedValue: string;
}

/**
 * Return type for useMicrochipFormatter hook.
 */
export interface UseMicrochipFormatterReturn extends MicrochipNumberFormData {
  /**
   * Handler for input onChange event.
   * Strips non-numeric characters, limits to 15 digits, updates state.
   */
  handleChange: (e: React.ChangeEvent<HTMLInputElement>) => void;

  /**
   * Handler for input onPaste event.
   * Sanitizes pasted content (strips non-numeric), limits to 15 digits.
   */
  handlePaste: (e: React.ClipboardEvent<HTMLInputElement>) => void;

  /**
   * Reset the form to initial state (empty string).
   * Used when component re-mounts or needs to clear input.
   */
  reset: () => void;
}

