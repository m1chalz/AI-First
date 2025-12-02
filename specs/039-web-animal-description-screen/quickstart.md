# Quickstart: Web Animal Description Screen

**Feature**: 039-web-animal-description-screen  
**Date**: December 2, 2025  
**Phase**: 1 (Design & Contracts)

## Overview

This quickstart guide provides implementation guidance for building the Web Animal Description screen (Step 3/4 of the Missing Pet flow). Follow these steps to implement the feature efficiently.

##Prerequisites

Before starting implementation:

1. ✅ Spec 034 (Web Microchip Number) implemented
2. ✅ Spec 037 (Web Animal Photo) implemented
3. ✅ React Router configured with flow routes
4. ✅ `ReportMissingPetFlowContext` exists and functional
5. ✅ Animal types (`AnimalSpecies`, `AnimalSex`) defined in `/webApp/src/types/animal.ts`

## Implementation Steps

### Step 1: Extend Flow State Interface

**File**: `/webApp/src/contexts/ReportMissingPetFlowContext.tsx`

Add Step 3 fields to `ReportMissingPetFlowState`:

```typescript
interface ReportMissingPetFlowState {
  currentStep: number;
  
  // Step 1
  microchipNumber: string;
  
  // Step 2
  photo: File | null;
  photoUrl?: string;
  
  // Step 3 (NEW)
  lastSeenDate: string;           // ISO 8601 (YYYY-MM-DD)
  species: AnimalSpecies | null;
  breed: string;
  sex: AnimalSex | null;
  age: number | null;
  description: string;
}
```

Update initial state:

```typescript
const initialState: ReportMissingPetFlowState = {
  currentStep: 1,
  microchipNumber: '',
  photo: null,
  lastSeenDate: new Date().toISOString().split('T')[0],  // Today
  species: null,
  breed: '',
  sex: null,
  age: null,
  description: '',
};
```

---

### Step 2: Create Validation Utility

**File**: `/webApp/src/utils/form-validation.ts`

Implement validation functions (see [contracts/validationFunctions.ts](./contracts/validationFunctions.ts)):

```typescript
export const validateLastSeenDate = (date: string): string | null => {
  if (!date) {
    return "Please select the date of disappearance";
  }
  const selected = new Date(date);
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  if (selected > today) {
    return "Date cannot be in the future";
  }
  return null;
};

export const validateSpecies = (species: string): string | null => {
  if (!species) {
    return "Please select a species";
  }
  return null;
};

export const validateBreed = (breed: string, species: string): string | null => {
  if (species && !breed.trim()) {
    return "Please enter the breed";
  }
  return null;
};

export const validateSex = (sex: string): string | null => {
  if (!sex) {
    return "Please select a gender";
  }
  return null;
};

export const validateAge = (ageStr: string): string | null => {
  if (!ageStr) {
    return null;  // Optional
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

export const validateDescription = (desc: string): string | null => {
  if (desc.length > 500) {
    return "Description cannot exceed 500 characters";
  }
  return null;
};

export const validateAllFields = (formData: any): Record<string, string> => {
  const errors: Record<string, string> = {};
  
  const dateErr = validateLastSeenDate(formData.lastSeenDate);
  if (dateErr) errors.lastSeenDate = dateErr;
  
  const speciesErr = validateSpecies(formData.species);
  if (speciesErr) errors.species = speciesErr;
  
  const breedErr = validateBreed(formData.breed, formData.species);
  if (breedErr) errors.breed = breedErr;
  
  const sexErr = validateSex(formData.sex);
  if (sexErr) errors.sex = sexErr;
  
  const ageErr = validateAge(formData.age);
  if (ageErr) errors.age = ageErr;
  
  const descErr = validateDescription(formData.description);
  if (descErr) errors.description = descErr;
  
  return errors;
};
```

---

### Step 3: Create Custom Hook

**File**: `/webApp/src/hooks/useAnimalDescriptionForm.ts`

Create form management hook:

```typescript
import { useState, useEffect } from 'react';
import { useReportMissingPetFlow } from '../contexts/ReportMissingPetFlowContext';
import { validateAllFields } from '../utils/form-validation';
import { AnimalSpecies, AnimalSex } from '../types/animal';

export const useAnimalDescriptionForm = () => {
  const { flowState, updateFlowState } = useReportMissingPetFlow();
  
  const [formData, setFormData] = useState({
    lastSeenDate: flowState.lastSeenDate || new Date().toISOString().split('T')[0],
    species: flowState.species || '',
    breed: flowState.breed || '',
    sex: flowState.sex || '',
    age: flowState.age !== null ? String(flowState.age) : '',
    description: flowState.description || '',
  });
  
  const [validationErrors, setValidationErrors] = useState<Record<string, string>>({});
  const [showToast, setShowToast] = useState(false);
  
  // Clear breed when species changes
  useEffect(() => {
    if (formData.species) {
      setFormData(prev => ({ ...prev, breed: '' }));
    }
  }, [formData.species]);
  
  const handleSubmit = (): boolean => {
    const errors = validateAllFields(formData);
    
    if (Object.keys(errors).length > 0) {
      setValidationErrors(errors);
      setShowToast(true);
      setTimeout(() => setShowToast(false), 5000);  // 5 second toast
      return false;
    }
    
    // Convert and save to flow state
    updateFlowState({
      lastSeenDate: formData.lastSeenDate,
      species: formData.species as AnimalSpecies,
      breed: formData.breed,
      sex: formData.sex as AnimalSex,
      age: formData.age ? Number(formData.age) : null,
      description: formData.description,
      currentStep: 4  // Advance to Step 4
    });
    
    return true;
  };
  
  return {
    formData,
    setFormData,
    validationErrors,
    showToast,
    handleSubmit
  };
};
```

---

### Step 4: Create Page Component

**File**: `/webApp/src/pages/ReportMissingPet/Step3_AnimalDescription.tsx`

Implement the main page component:

```typescript
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAnimalDescriptionForm } from '../../hooks/useAnimalDescriptionForm';
import { AnimalDescriptionForm } from '../../components/AnimalDescriptionForm/AnimalDescriptionForm';

export const Step3_AnimalDescription: React.FC = () => {
  const navigate = useNavigate();
  const {
    formData,
    setFormData,
    validationErrors,
    showToast,
    handleSubmit
  } = useAnimalDescriptionForm();
  
  const onSubmit = () => {
    const isValid = handleSubmit();
    if (isValid) {
      navigate('/report-missing/contact');  // Step 4
    }
  };
  
  const onBack = () => {
    navigate('/report-missing/photo');  // Step 2
  };
  
  return (
    <div className="step-container">
      <header className="step-header">
        <button
          onClick={onBack}
          className="back-button"
          data-testid="animalDescription.back.click"
          aria-label="Back to previous step"
        >
          ←
        </button>
        <h1>Animal description</h1>
        <span className="progress">3/4</span>
      </header>
      
      {showToast && (
        <div className="toast" role="alert">
          Please correct the errors below
        </div>
      )}
      
      <AnimalDescriptionForm
        formData={formData}
        setFormData={setFormData}
        validationErrors={validationErrors}
        onSubmit={onSubmit}
      />
    </div>
  );
};
```

---

### Step 5: Create Form Component

**File**: `/webApp/src/components/AnimalDescriptionForm/AnimalDescriptionForm.tsx`

Implement form component with all fields:

```typescript
import React from 'react';
import { AnimalSpecies, AnimalSex } from '../../types/animal';
import { SpeciesDropdown } from './SpeciesDropdown';
import { GenderSelector } from './GenderSelector';
import { CharacterCounter } from './CharacterCounter';

interface Props {
  formData: {
    lastSeenDate: string;
    species: string;
    breed: string;
    sex: string;
    age: string;
    description: string;
  };
  setFormData: React.Dispatch<React.SetStateAction<any>>;
  validationErrors: Record<string, string>;
  onSubmit: () => void;
}

export const AnimalDescriptionForm: React.FC<Props> = ({
  formData,
  setFormData,
  validationErrors,
  onSubmit
}) => {
  const today = new Date().toISOString().split('T')[0];
  
  return (
    <form onSubmit={(e) => { e.preventDefault(); onSubmit(); }}>
      {/* Date of disappearance */}
      <div className="form-field">
        <label htmlFor="lastSeenDate">Date of disappearance</label>
        <input
          type="date"
          id="lastSeenDate"
          max={today}
          value={formData.lastSeenDate}
          onChange={(e) => setFormData(prev => ({ ...prev, lastSeenDate: e.target.value }))}
          data-testid="animalDescription.lastSeenDate.input"
        />
        {validationErrors.lastSeenDate && (
          <span className="error">{validationErrors.lastSeenDate}</span>
        )}
      </div>
      
      {/* Species dropdown */}
      <SpeciesDropdown
        value={formData.species}
        onChange={(species) => setFormData(prev => ({ ...prev, species }))}
        error={validationErrors.species}
        testId="animalDescription.species.select"
      />
      
      {/* Breed (enabled only when species selected) */}
      <div className="form-field">
        <label htmlFor="breed">Animal race</label>
        <input
          type="text"
          id="breed"
          disabled={!formData.species}
          value={formData.breed}
          onChange={(e) => setFormData(prev => ({ ...prev, breed: e.target.value }))}
          data-testid="animalDescription.breed.input"
        />
        {validationErrors.breed && (
          <span className="error">{validationErrors.breed}</span>
        )}
      </div>
      
      {/* Gender selector */}
      <GenderSelector
        value={formData.sex}
        onChange={(sex) => setFormData(prev => ({ ...prev, sex }))}
        error={validationErrors.sex}
        testId="animalDescription.sex.select"
      />
      
      {/* Age (optional) */}
      <div className="form-field">
        <label htmlFor="age">Animal age (optional)</label>
        <input
          type="number"
          id="age"
          min="0"
          max="40"
          step="1"
          value={formData.age}
          onChange={(e) => setFormData(prev => ({ ...prev, age: e.target.value }))}
          data-testid="animalDescription.age.input"
        />
        {validationErrors.age && (
          <span className="error">{validationErrors.age}</span>
        )}
      </div>
      
      {/* Request GPS (placeholder button) */}
      <button
        type="button"
        disabled
        className="gps-button-placeholder"
        data-testid="animalDescription.requestGps.click"
      >
        Request GPS position
      </button>
      
      {/* Description (optional) */}
      <div className="form-field">
        <label htmlFor="description">Animal additional description (optional)</label>
        <textarea
          id="description"
          maxLength={500}
          value={formData.description}
          onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
          data-testid="animalDescription.description.input"
        />
        <CharacterCounter
          current={formData.description.length}
          max={500}
          isExceeded={formData.description.length > 500}
        />
        {validationErrors.description && (
          <span className="error">{validationErrors.description}</span>
        )}
      </div>
      
      {/* Continue button */}
      <button
        type="submit"
        className="continue-button"
        data-testid="animalDescription.continue.click"
      >
        Continue
      </button>
    </form>
  );
};
```

---

### Step 6: Create Sub-components

Create supporting components (SpeciesDropdown, GenderSelector, CharacterCounter) in separate files under `/webApp/src/components/AnimalDescriptionForm/`.

**Example - CharacterCounter.tsx**:

```typescript
import React from 'react';

interface Props {
  current: number;
  max: number;
  isExceeded: boolean;
}

export const CharacterCounter: React.FC<Props> = ({ current, max, isExceeded }) => {
  return (
    <span className={`char-counter ${isExceeded ? 'exceeded' : ''}`}>
      {current}/{max} characters
    </span>
  );
};
```

---

### Step 7: Add Route Configuration

**File**: `/webApp/src/App.tsx` (or routing configuration file)

Add Step 3 route:

```typescript
import { Step3_AnimalDescription } from './pages/ReportMissingPet/Step3_AnimalDescription';

// In your routing configuration
<Route path="/report-missing/description" element={<Step3_AnimalDescription />} />
```

Add route guard to redirect if no flow state:

```typescript
// Route protection logic
if (!flowState || flowState.currentStep < 3) {
  navigate('/report-missing/microchip');  // Start from Step 1
}
```

---

### Step 8: Add Styling

**File**: `/webApp/src/styles/AnimalDescriptionForm.css`

Add responsive styles matching specs 034 and 037:

```css
.step-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 24px;
}

.step-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.form-field {
  margin-bottom: 24px;
}

.form-field label {
  display: block;
  margin-bottom: 8px;
  font-size: 16px;
}

.form-field input,
.form-field textarea {
  width: 100%;
  height: 41px;
  padding: 8px 12px;
  border: 0.667px solid #d1d5dc;
  border-radius: 10px;
}

.form-field textarea {
  height: 96px;
  resize: none;
}

.form-field input:disabled {
  background-color: #f3f4f6;
  cursor: not-allowed;
}

.form-field .error {
  display: block;
  margin-top: 4px;
  color: #dc2626;
  font-size: 14px;
}

.char-counter {
  display: block;
  margin-top: 4px;
  font-size: 14px;
  color: #6b7280;
}

.char-counter.exceeded {
  color: #dc2626;
}

.continue-button {
  width: 100%;
  padding: 12px 24px;
  background-color: #155dfc;
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  cursor: pointer;
}

.continue-button:hover {
  background-color: #0d47d4;
}

.toast {
  position: fixed;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  padding: 12px 24px;
  background-color: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.gps-button-placeholder {
  width: 100%;
  padding: 10px 24px;
  margin-bottom: 24px;
  border: 1px solid #d1d5dc;
  border-radius: 10px;
  background-color: #f9fafb;
  color: #9ca3af;
  cursor: not-allowed;
}

/* Responsive design */
@media (max-width: 768px) {
  .step-container {
    padding: 16px;
  }
}
```

---

### Step 9: Write Unit Tests

**File**: `/webApp/src/__tests__/AnimalDescriptionForm.test.tsx`

Implement unit tests (80% coverage target):

```typescript
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { Step3_AnimalDescription } from '../pages/ReportMissingPet/Step3_AnimalDescription';

describe('AnimalDescriptionForm', () => {
  it('should display all required fields', () => {
    // Given
    render(<Step3_AnimalDescription />);
    
    // When
    const dateInput = screen.getByTestId('animalDescription.lastSeenDate.input');
    const speciesSelect = screen.getByTestId('animalDescription.species.select');
    const breedInput = screen.getByTestId('animalDescription.breed.input');
    const sexSelect = screen.getByTestId('animalDescription.sex.select');
    const continueButton = screen.getByTestId('animalDescription.continue.click');
    
    // Then
    expect(dateInput).toBeInTheDocument();
    expect(speciesSelect).toBeInTheDocument();
    expect(breedInput).toBeInTheDocument();
    expect(breedInput).toBeDisabled();  // Disabled until species selected
    expect(sexSelect).toBeInTheDocument();
    expect(continueButton).toBeInTheDocument();
  });
  
  it('should show validation errors when submitting empty form', async () => {
    // Given
    render(<Step3_AnimalDescription />);
    const continueButton = screen.getByTestId('animalDescription.continue.click');
    
    // When
    fireEvent.click(continueButton);
    
    // Then
    expect(await screen.findByText('Please select a species')).toBeInTheDocument();
    expect(await screen.findByText('Please select a gender')).toBeInTheDocument();
  });
  
  it('should enable breed field when species is selected', () => {
    // Given
    render(<Step3_AnimalDescription />);
    const speciesSelect = screen.getByTestId('animalDescription.species.select');
    const breedInput = screen.getByTestId('animalDescription.breed.input');
    
    // When
    fireEvent.change(speciesSelect, { target: { value: 'DOG' } });
    
    // Then
    expect(breedInput).not.toBeDisabled();
  });
  
  // Add more tests...
});
```

---

### Step 10: Write E2E Tests

**File**: `/e2e-tests/src/test/resources/features/web/039-animal-description.feature`

Create Cucumber scenario:

```gherkin
@web
Feature: Web Animal Description Screen
  As a user reporting a missing pet
  I want to provide animal description details
  So that responders can identify the pet

  Background:
    Given I am on the pet list page
    When I start the "Report Missing Pet" flow
    And I complete Step 1 (microchip number)
    And I complete Step 2 (animal photo)

  Scenario: Successfully fill animal description form
    Given I am on Step 3 (animal description)
    When I select "Dog" as the species
    And I enter "Golden Retriever" as the breed
    And I select "Male" as the gender
    And I enter "5" as the age
    And I enter "Red collar with tags" as the description
    And I click the Continue button
    Then I should be navigated to Step 4 (contact details)
    And the flow state should contain all Step 3 data

  Scenario: Show validation errors for missing required fields
    Given I am on Step 3 (animal description)
    When I click the Continue button without filling required fields
    Then I should see a validation toast message
    And I should see inline errors for species, breed, and gender
    And I should remain on Step 3

  Scenario: Navigate back to Step 2 preserving data
    Given I am on Step 3 (animal description)
    And I have filled some form fields
    When I click the back arrow button
    Then I should be navigated to Step 2 (animal photo)
    When I click Continue to return to Step 3
    Then my previously entered data should still be displayed
```

**Page Object**: `/e2e-tests/src/test/java/pages/AnimalDescriptionPage.java`

---

## Testing Checklist

- [ ] Unit tests for form validation logic (80% coverage)
- [ ] Unit tests for state management
- [ ] E2E tests for happy path (fill form → navigate to Step 4)
- [ ] E2E tests for validation errors
- [ ] E2E tests for back navigation (data persistence)
- [ ] E2E tests for species change clearing breed
- [ ] Browser testing (Chrome, Firefox, Safari, Edge)
- [ ] Mobile responsive testing (320px, 768px, 1024px+)

## Common Issues & Solutions

### Issue: Breed field not clearing when species changes

**Solution**: Use useEffect hook to watch species changes:
```typescript
useEffect(() => {
  setFormData(prev => ({ ...prev, breed: '' }));
}, [formData.species]);
```

### Issue: Date picker allows future dates in some browsers

**Solution**: Add client-side validation as backup:
```typescript
const validateLastSeenDate = (date: string): string | null => {
  const selected = new Date(date);
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  if (selected > today) {
    return "Date cannot be in the future";
  }
  return null;
};
```

### Issue: Character counter not updating immediately

**Solution**: Use controlled textarea with onChange:
```typescript
<textarea
  value={formData.description}
  onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
  maxLength={500}
/>
```

## Performance Tips

1. **Memoize species options**: Use `useMemo` to avoid recalculating on every render
2. **Debounce validation**: Optional for description field if performance issues
3. **Lazy load components**: Split sub-components for code splitting

## Next Steps

After implementation:
1. Run unit tests: `npm test -- --coverage` (target: 80%)
2. Run E2E tests: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web"`
3. Test multi-step navigation (back arrow behavior)
4. Update specs 034 and 037 for consistent back arrow navigation
5. Proceed to `/speckit.tasks` to generate task breakdown

---

**Quickstart completed**: December 2, 2025  
**Ready for implementation**: Yes

