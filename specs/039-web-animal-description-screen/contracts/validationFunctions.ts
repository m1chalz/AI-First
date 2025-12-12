/**
 * TypeScript Contracts: Validation Functions
 * 
 * Feature: 039-web-animal-description-screen
 * Date: December 2, 2025
 * 
 * This file defines the validation function signatures for the Animal
 * Description Form. Implementation should follow these contracts.
 */

import { AnimalSpecies } from '../../../webApp/src/types/announcement';

/**
 * Validation function type.
 * Returns error message string if invalid, null if valid.
 */
export type ValidationFunction<T = string> = (value: T) => string | null;

/**
 * Validates lastSeenDate field.
 * 
 * Rules:
 * - Required (cannot be empty)
 * - Must not be a future date (compare to today)
 * 
 * @param date - Date string in YYYY-MM-DD format
 * @returns Error message or null
 */
export declare function validateLastSeenDate(date: string): string | null;

/**
 * Validates species field.
 * 
 * Rules:
 * - Required (cannot be empty)
 * - Must be one of valid AnimalSpecies enum values
 * 
 * @param species - Species string (empty or enum value)
 * @returns Error message or null
 */
export declare function validateSpecies(species: string): string | null;

/**
 * Validates breed field.
 * 
 * Rules:
 * - Required if species is selected
 * - Cleared when species changes
 * - Must not be empty/whitespace-only when species is set
 * 
 * @param breed - Breed string
 * @param species - Current species value (affects whether breed is required)
 * @returns Error message or null
 */
export declare function validateBreed(breed: string, species: string): string | null;

/**
 * Validates sex field.
 * 
 * Rules:
 * - Required (cannot be empty)
 * - Must be exactly 'MALE' or 'FEMALE' (not UNKNOWN)
 * 
 * @param sex - Sex string (empty or enum value)
 * @returns Error message or null
 */
export declare function validateSex(sex: string): string | null;

/**
 * Validates age field.
 * 
 * Rules:
 * - Optional (can be empty)
 * - If provided, must be integer 0-40
 * - No decimals, no negative values
 * 
 * @param ageStr - Age as string (from input)
 * @returns Error message or null
 */
export declare function validateAge(ageStr: string): string | null;

/**
 * Validates description field.
 * 
 * Rules:
 * - Optional (can be empty)
 * - Max 500 characters
 * 
 * @param description - Description text
 * @returns Error message or null
 */
export declare function validateDescription(description: string): string | null;

/**
 * Validates all form fields and returns error object.
 * 
 * @param formData - Complete form data to validate
 * @returns Object with field names as keys and error messages as values
 */
export declare function validateAllFields(formData: {
  lastSeenDate: string;
  species: string;
  breed: string;
  sex: string;
  age: string;
  description: string;
}): Record<string, string>;

/**
 * Checks if form is valid (no validation errors).
 * 
 * @param formData - Complete form data to validate
 * @returns true if all fields valid, false otherwise
 */
export declare function isFormValid(formData: {
  lastSeenDate: string;
  species: string;
  breed: string;
  sex: string;
  age: string;
  description: string;
}): boolean;

/**
 * Validation error messages (constants for consistency).
 */
export const VALIDATION_MESSAGES = {
  LAST_SEEN_DATE_REQUIRED: 'Please select the date of disappearance',
  LAST_SEEN_DATE_FUTURE: 'Date cannot be in the future',
  SPECIES_REQUIRED: 'Please select a species',
  SPECIES_INVALID: 'Invalid species selected',
  BREED_REQUIRED: 'Please enter the breed',
  SEX_REQUIRED: 'Please select a gender',
  SEX_INVALID: 'Invalid gender selected',
  AGE_INVALID_NUMBER: 'Age must be a whole number',
  AGE_OUT_OF_RANGE: 'Age must be between 0 and 40',
  DESCRIPTION_TOO_LONG: 'Description cannot exceed 500 characters',
} as const;

