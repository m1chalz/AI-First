/**
 * TypeScript Contracts: Animal Description Form Data
 * 
 * Feature: 039-web-animal-description-screen
 * Date: December 2, 2025
 * 
 * This file defines the TypeScript interfaces for the Animal Description Form
 * (Step 3/4 of the Missing Pet flow). These contracts should be implemented
 * in the web application.
 */

import { AnimalSpecies, AnimalSex } from '../../../webApp/src/types/announcement';

/**
 * Local form state for Animal Description screen (Step 3/4).
 * 
 * This state is managed within the component for UI interactions
 * and converted to proper types before saving to flow state.
 */
export interface AnimalDescriptionFormData {
  /** Date when animal was last seen (YYYY-MM-DD format) */
  lastSeenDate: string;

  /** Selected species (empty string or enum value) */
  species: string;

  /** Breed or race of the animal (free text) */
  breed: string;

  /** Selected sex (empty string or enum value: MALE, FEMALE) */
  sex: string;

  /** Age in years (string for easier form binding) */
  age: string;

  /** Additional description (max 500 characters) */
  description: string;

  /** Validation errors keyed by field name */
  validationErrors: Record<string, string>;

  /** Has user interacted with form? */
  isDirty: boolean;

  /** Is form submission in progress? */
  isSubmitting: boolean;
}

/**
 * Flow state extension for Step 3 (Animal Description).
 * 
 * These fields are added to ReportMissingPetFlowState when
 * user successfully completes Step 3.
 */
export interface AnimalDescriptionFlowData {
  /** Date when animal was last seen (ISO 8601: YYYY-MM-DD) */
  lastSeenDate: string;

  /** Selected species (typed enum) */
  species: AnimalSpecies | null;

  /** Breed or race of the animal */
  breed: string;

  /** Selected sex (MALE or FEMALE, not UNKNOWN) */
  sex: AnimalSex | null;

  /** Age in years (integer 0-40, optional) */
  age: number | null;

  /** Additional description (max 500 chars, optional) */
  description: string;
}

/**
 * Validation error structure for form fields.
 * 
 * Each field can have an optional error message string.
 */
export interface ValidationErrors {
  lastSeenDate?: string;
  species?: string;
  breed?: string;
  sex?: string;
  age?: string;
  description?: string;
}

/**
 * Props for AnimalDescriptionForm component.
 */
export interface AnimalDescriptionFormProps {
  /** Initial form data (from flow state if returning to Step 3) */
  initialData?: Partial<AnimalDescriptionFlowData>;

  /** Callback when form is successfully submitted */
  onSubmit: (data: AnimalDescriptionFlowData) => void;

  /** Callback when user clicks back arrow */
  onBack: () => void;
}

/**
 * Props for species dropdown component.
 */
export interface SpeciesDropdownProps {
  /** Currently selected species (empty or enum value) */
  value: string;

  /** Callback when species changes */
  onChange: (species: string) => void;

  /** Error message to display */
  error?: string;

  /** Test identifier for E2E tests */
  testId?: string;
}

/**
 * Props for gender selector component.
 */
export interface GenderSelectorProps {
  /** Currently selected sex (empty or enum value) */
  value: string;

  /** Callback when sex changes */
  onChange: (sex: string) => void;

  /** Error message to display */
  error?: string;

  /** Test identifier for E2E tests */
  testId?: string;
}

/**
 * Props for character counter component.
 */
export interface CharacterCounterProps {
  /** Current character count */
  current: number;

  /** Maximum allowed characters */
  max: number;

  /** Whether limit is exceeded */
  isExceeded: boolean;
}

/**
 * Display label mappings for enums.
 */
export interface SpeciesLabels {
  DOG: string;
  CAT: string;
  BIRD: string;
  RABBIT: string;
  OTHER: string;
}

export interface SexLabels {
  MALE: string;
  FEMALE: string;
  UNKNOWN: string;
}

/**
 * Constants for form constraints.
 */
export const FORM_CONSTRAINTS = {
  AGE_MIN: 0,
  AGE_MAX: 40,
  DESCRIPTION_MAX_LENGTH: 500,
} as const;

/**
 * Default form data values.
 */
export const DEFAULT_FORM_DATA: AnimalDescriptionFormData = {
  lastSeenDate: new Date().toISOString().split('T')[0],
  species: '',
  breed: '',
  sex: '',
  age: '',
  description: '',
  validationErrors: {},
  isDirty: false,
  isSubmitting: false,
};

/**
 * Species display labels (capitalized for UI).
 */
export const SPECIES_LABELS: SpeciesLabels = {
  DOG: 'Dog',
  CAT: 'Cat',
  BIRD: 'Bird',
  RABBIT: 'Rabbit',
  OTHER: 'Other',
};

/**
 * Sex display labels (capitalized for UI).
 */
export const SEX_LABELS: SexLabels = {
  MALE: 'Male',
  FEMALE: 'Female',
  UNKNOWN: 'Unknown',
};

