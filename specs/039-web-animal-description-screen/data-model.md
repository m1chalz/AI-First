# Data Model: Web Animal Description Screen

**Feature**: 039-web-animal-description-screen  
**Date**: December 2, 2025  
**Phase**: 1 (Design & Contracts)

## Overview

This document defines the data structures for the Web Animal Description screen (Step 3/4 of the Missing Pet flow). The model extends the existing `ReportMissingPetFlowState` with animal description fields and defines local form state for UI management.

## Type Definitions

### Existing Types (from `/webApp/src/types/animal.ts`)

```typescript
export type AnimalSpecies = 'DOG' | 'CAT' | 'BIRD' | 'RABBIT' | 'OTHER';

export type AnimalSex = 'MALE' | 'FEMALE' | 'UNKNOWN';

export interface Animal {
  id: string;
  lastSeenDate: string;        // ISO 8601 (YYYY-MM-DD)
  species: AnimalSpecies;
  sex: AnimalSex;
  breed: string | null;
  age: number | null;
  description: string | null;
  // ... other fields omitted
}
```

## Flow State Model

### ReportMissingPetFlowState (Extended)

**Purpose**: Holds temporary state for the 4-step Missing Pet reporting flow.

**Lifecycle**:
- Created when user initiates flow from pet list
- Updated as user progresses through steps
- Cleared when user cancels flow (back arrow at Step 1, browser back button) or completes flow
- NOT persisted to browser storage (in-memory React Context only)

**State Structure**:

```typescript
interface ReportMissingPetFlowState {
  // Meta
  currentStep: number;  // 1, 2, 3, or 4
  
  // Step 1: Microchip Number (spec 034)
  microchipNumber: string;  // Empty or 15 digits (no hyphens)
  
  // Step 2: Animal Photo (spec 037)
  photo: File | null;
  photoUrl?: string;  // Blob URL for preview
  
  // Step 3: Animal Description (this spec - NEW)
  lastSeenDate: string;           // ISO 8601 (YYYY-MM-DD), required
  species: AnimalSpecies | null;  // DOG|CAT|BIRD|RABBIT|OTHER, required
  breed: string;                  // Required once species selected
  sex: AnimalSex | null;          // MALE|FEMALE (not UNKNOWN), required
  age: number | null;             // Optional, 0-40 range
  description: string;            // Optional, max 500 chars
  
  // Step 4: Contact Details (future spec)
  // To be added in separate specification
}
```

**Field Details**:

| Field | Type | Required | Default | Validation | Notes |
|-------|------|----------|---------|------------|-------|
| `currentStep` | `number` | Yes | `1` | 1-4 | Tracks current position in flow |
| `lastSeenDate` | `string` | Yes | Today | ISO 8601, not future | Display label: "Date of disappearance" |
| `species` | `AnimalSpecies \| null` | Yes | `null` | One of 5 enum values | Dropdown, display capitalized |
| `breed` | `string` | Conditional | `''` | Non-empty if species selected | Cleared when species changes |
| `sex` | `AnimalSex \| null` | Yes | `null` | MALE or FEMALE only | Binary choice, display capitalized |
| `age` | `number \| null` | No | `null` | 0-40 if provided | Integer only, no decimals |
| `description` | `string` | No | `''` | Max 500 characters | Textarea with live counter |

**State Transitions**:

```
Step 2 Complete (photo uploaded)
↓
currentStep = 3
Initialize Step 3 fields with defaults or saved values
↓
User fills form fields
↓
User clicks Continue
↓
Validate required fields (lastSeenDate, species, breed, sex)
↓
If invalid: Display toast + inline errors, stay on Step 3
If valid: Save to flow state, currentStep = 4, navigate to Step 4
```

**Access Pattern**:
```typescript
// Read state
const { flowState, updateFlowState } = useReportMissingPetFlow();

// Update Step 3 fields
updateFlowState({
  lastSeenDate: '2025-12-01',
  species: 'DOG',
  breed: 'Golden Retriever',
  sex: 'MALE',
  age: 5,
  description: 'Red collar with tags',
  currentStep: 4
});
```

---

## Local Form State Model

### AnimalDescriptionFormData

**Purpose**: Manages local UI state for the form component before submission.

**Lifecycle**:
- Created when Step3_AnimalDescription component mounts
- Updated on every user interaction (typing, selection, etc.)
- Converted to proper types and saved to flow state on valid submission
- Discarded when component unmounts or user navigates away

**State Structure**:

```typescript
interface AnimalDescriptionFormData {
  // Form field values (strings for easier binding)
  lastSeenDate: string;     // YYYY-MM-DD format
  species: string;          // Empty or enum value
  breed: string;            // Free text
  sex: string;              // Empty or enum value
  age: string;              // String for input binding
  description: string;      // Max 500 chars
  
  // UI state
  validationErrors: Record<string, string>;  // Field name → error message
  isDirty: boolean;         // Has user interacted with form?
  isSubmitting: boolean;    // Submission in progress?
}
```

**Default Values**:

```typescript
const defaultFormData: AnimalDescriptionFormData = {
  lastSeenDate: new Date().toISOString().split('T')[0],  // Today
  species: '',
  breed: '',
  sex: '',
  age: '',
  description: '',
  validationErrors: {},
  isDirty: false,
  isSubmitting: false
};
```

**Validation Error Structure**:

```typescript
type ValidationErrors = {
  lastSeenDate?: string;  // e.g., "Date cannot be in the future"
  species?: string;       // e.g., "Please select a species"
  breed?: string;         // e.g., "Please enter the breed"
  sex?: string;           // e.g., "Please select a gender"
  age?: string;           // e.g., "Age must be between 0 and 40"
  description?: string;   // e.g., "Description cannot exceed 500 characters"
};
```

**Example Error States**:

```typescript
// Missing required field
{
  species: "Please select a species"
}

// Invalid age range
{
  age: "Age must be between 0 and 40"
}

// Multiple errors
{
  species: "Please select a species",
  breed: "Please enter the breed",
  sex: "Please select a gender"
}
```

---

## Display Label Mapping

### Species Display Labels

```typescript
const speciesLabels: Record<AnimalSpecies, string> = {
  DOG: 'Dog',
  CAT: 'Cat',
  BIRD: 'Bird',
  RABBIT: 'Rabbit',
  OTHER: 'Other'
};
```

### Sex Display Labels

```typescript
const sexLabels: Record<AnimalSex, string> = {
  MALE: 'Male',
  FEMALE: 'Female',
  UNKNOWN: 'Unknown'  // Not used in this form (only MALE/FEMALE selectable)
};
```

---

## Validation Rules

### lastSeenDate

- **Rule**: Required, must not be a future date
- **Validation**:
  ```typescript
  const validateLastSeenDate = (date: string): string | null => {
    if (!date) {
      return "Please select the date of disappearance";
    }
    const selected = new Date(date);
    const today = new Date();
    today.setHours(0, 0, 0, 0);  // Compare dates only
    if (selected > today) {
      return "Date cannot be in the future";
    }
    return null;
  };
  ```

### species

- **Rule**: Required, must be one of 5 AnimalSpecies values
- **Validation**:
  ```typescript
  const validateSpecies = (species: string): string | null => {
    if (!species) {
      return "Please select a species";
    }
    if (!Object.values(AnimalSpecies).includes(species as AnimalSpecies)) {
      return "Invalid species selected";
    }
    return null;
  };
  ```

### breed

- **Rule**: Required if species is selected, cleared when species changes
- **Validation**:
  ```typescript
  const validateBreed = (breed: string, species: string): string | null => {
    if (species && !breed.trim()) {
      return "Please enter the breed";
    }
    return null;
  };
  ```

### sex

- **Rule**: Required, must be MALE or FEMALE
- **Validation**:
  ```typescript
  const validateSex = (sex: string): string | null => {
    if (!sex) {
      return "Please select a gender";
    }
    if (sex !== 'MALE' && sex !== 'FEMALE') {
      return "Invalid gender selected";
    }
    return null;
  };
  ```

### age

- **Rule**: Optional, must be integer 0-40 if provided
- **Validation**:
  ```typescript
  const validateAge = (ageStr: string): string | null => {
    if (!ageStr) {
      return null;  // Optional field
    }
    const age = Number(ageStr);
    if (isNaN(age) || !Number.isInteger(age)) {
      return "Age must be a whole number";
    }
    if (age < 0 || age > 40) {
      return "Age must be between 0 and 40";
    }
    return null;
  };
  ```

### description

- **Rule**: Optional, max 500 characters
- **Validation**:
  ```typescript
  const validateDescription = (desc: string): string | null => {
    if (desc.length > 500) {
      return "Description cannot exceed 500 characters";
    }
    return null;
  };
  ```

---

## Data Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│  User Interaction                                                │
│  (Date picker, dropdowns, text inputs, textarea)                │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│  Local Component State (AnimalDescriptionFormData)               │
│  - lastSeenDate: "2025-12-01"                                    │
│  - species: "DOG"                                                │
│  - breed: "Golden Retriever"                                     │
│  - sex: "MALE"                                                   │
│  - age: "5"                                                      │
│  - description: "Red collar"                                     │
│  - validationErrors: {}                                          │
│                                                                   │
│  Real-time Updates:                                              │
│  - Species change clears breed field                             │
│  - Description character counter updates                         │
│  - Date picker blocks future dates                               │
│  - Age input accepts only numbers                                │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         │ User clicks Continue
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│  Validation Phase (on submit)                                    │
│  1. Validate lastSeenDate (required, not future)                 │
│  2. Validate species (required, valid enum)                      │
│  3. Validate breed (required if species selected)                │
│  4. Validate sex (required, MALE or FEMALE)                      │
│  5. Validate age (optional, 0-40 if provided)                    │
│  6. Validate description (optional, max 500 chars)               │
└────────────────────────┬────────────────────────────────────────┘
                         │
                    ┌────┴────┐
                    │  Valid? │
                    └────┬────┘
                         │
            ┌────────────┴────────────┐
            │                         │
          Invalid                   Valid
            │                         │
            ▼                         ▼
┌─────────────────────┐   ┌────────────────────────────────────┐
│  Show Errors        │   │  Convert & Save to Flow State      │
│  - Toast (5 sec)    │   │  - Parse age to number             │
│  - Inline errors    │   │  - Convert enums                   │
│  - Stay on Step 3   │   │  - Update currentStep = 4          │
└─────────────────────┘   │  - Navigate to Step 4               │
                          └────────────────────────────────────┘
                                         │
                                         ▼
                          ┌──────────────────────────────────┐
                          │  Flow State (React Context)      │
                          │  - lastSeenDate: "2025-12-01"    │
                          │  - species: AnimalSpecies.DOG    │
                          │  - breed: "Golden Retriever"     │
                          │  - sex: AnimalSex.MALE           │
                          │  - age: 5 (number)               │
                          │  - description: "Red collar"     │
                          │  - currentStep: 4                │
                          │                                  │
                          │  Persists across navigation:     │
                          │  - Step 3 → Step 4 → Step 3      │
                          │  - Cleared on cancel/complete    │
                          └──────────────────────────────────┘
```

---

## Type Conversion

### Form Data → Flow State

```typescript
const convertFormDataToFlowState = (
  formData: AnimalDescriptionFormData
): Partial<ReportMissingPetFlowState> => {
  return {
    lastSeenDate: formData.lastSeenDate,
    species: formData.species as AnimalSpecies,
    breed: formData.breed,
    sex: formData.sex as AnimalSex,
    age: formData.age ? Number(formData.age) : null,
    description: formData.description,
    currentStep: 4  // Advance to next step
  };
};
```

### Flow State → Form Data (when returning to Step 3)

```typescript
const convertFlowStateToFormData = (
  flowState: ReportMissingPetFlowState
): AnimalDescriptionFormData => {
  return {
    lastSeenDate: flowState.lastSeenDate || new Date().toISOString().split('T')[0],
    species: flowState.species || '',
    breed: flowState.breed || '',
    sex: flowState.sex || '',
    age: flowState.age !== null ? String(flowState.age) : '',
    description: flowState.description || '',
    validationErrors: {},
    isDirty: false,
    isSubmitting: false
  };
};
```

---

## State Management Pattern

### React Context Provider

```typescript
interface ReportMissingPetFlowContextValue {
  flowState: ReportMissingPetFlowState;
  updateFlowState: (updates: Partial<ReportMissingPetFlowState>) => void;
  clearFlowState: () => void;
}

const ReportMissingPetFlowContext = React.createContext<ReportMissingPetFlowContextValue | null>(null);

export const useReportMissingPetFlow = () => {
  const context = React.useContext(ReportMissingPetFlowContext);
  if (!context) {
    throw new Error('useReportMissingPetFlow must be used within ReportMissingPetFlowProvider');
  }
  return context;
};
```

### Custom Hook for Form Management

```typescript
const useAnimalDescriptionForm = () => {
  const { flowState, updateFlowState } = useReportMissingPetFlow();
  const [formData, setFormData] = useState<AnimalDescriptionFormData>(() =>
    convertFlowStateToFormData(flowState)
  );

  const validateForm = (): boolean => {
    const errors: Record<string, string> = {};
    
    const dateError = validateLastSeenDate(formData.lastSeenDate);
    if (dateError) errors.lastSeenDate = dateError;
    
    const speciesError = validateSpecies(formData.species);
    if (speciesError) errors.species = speciesError;
    
    const breedError = validateBreed(formData.breed, formData.species);
    if (breedError) errors.breed = breedError;
    
    const sexError = validateSex(formData.sex);
    if (sexError) errors.sex = sexError;
    
    const ageError = validateAge(formData.age);
    if (ageError) errors.age = ageError;
    
    const descError = validateDescription(formData.description);
    if (descError) errors.description = descError;
    
    setFormData(prev => ({ ...prev, validationErrors: errors }));
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = () => {
    if (validateForm()) {
      updateFlowState(convertFormDataToFlowState(formData));
      return true;  // Navigate to Step 4
    }
    return false;  // Stay on Step 3, show errors
  };

  return { formData, setFormData, handleSubmit };
};
```

---

## Performance Considerations

### Memoization

- Species dropdown options memoized (static data)
- Validation functions pure (no side effects)
- Character counter debounced if performance issues

### State Updates

- Use functional updates for form state to avoid race conditions
- Batch related state updates where possible
- Avoid unnecessary re-renders via React.memo for child components

---

## Future Extensions (Out of Scope)

- GPS location fields (`latitude`, `longitude`) - separate specification
- Backend API integration - separate specification
- Draft persistence to localStorage - not planned (per spec)
- Photo preview in Step 3 - not required

---

**Data model completed**: December 2, 2025  
**Next**: Generate contracts and quickstart guide

