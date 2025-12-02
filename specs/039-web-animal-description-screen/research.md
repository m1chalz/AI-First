# Research: Web Animal Description Screen

**Feature**: 039-web-animal-description-screen  
**Date**: December 2, 2025  
**Phase**: 0 (Research)

## Overview

This document consolidates research findings for implementing the Web Animal Description screen (Step 3/4 of the Missing Pet flow). Research focused on understanding existing web form patterns (specs 034, 037), clarifying ambiguous requirements, and defining technical approach.

## Research Tasks Completed

### 1. Existing Web Form Pattern Analysis

**Objective**: Understand patterns from specs 034 (chip number) and 037 (photo) to ensure consistency.

**Findings**:
- **Flow State Management**: Both specs use React Context for in-memory state
- **Validation Pattern**: On-submit validation with toast messages (3-5 seconds) + inline errors
- **Navigation**: Currently back arrow closes entire flow (to be updated)
- **Browser Behavior**: Refresh clears state, browser back button closes flow
- **Responsive Design**: Mobile (320px+), tablet (768px+), desktop (1024px+)
- **Test Identifiers**: `data-testid` with `{screen}.{element}.{action}` convention

**Decision**: Reuse all established patterns for consistency.

---

### 2. Field Naming and Type Alignment

**Objective**: Resolve ambiguity between spec field names and existing Animal type.

**Findings**:
- **Existing Animal Type** (`/webApp/src/types/animal.ts`):
  - `lastSeenDate` (string, ISO 8601)
  - `species` (AnimalSpecies enum: DOG|CAT|BIRD|RABBIT|OTHER)
  - `breed` (string | null)
  - `sex` (AnimalSex enum: MALE|FEMALE|UNKNOWN)
  - `age` (number | null)
  - `description` (string | null)
- **Spec Initially Used**: "disappearanceDate", "animalRace", "animalGender"
- **Clarification Session**: Resolved to use Animal type field names

**Decision**: Use Animal type field names (`lastSeenDate`, `breed`, `species`, `sex`, `age`, `description`) for consistency with existing codebase.

**Rationale**: Avoids conversion/mapping when eventually integrating with backend API. Maintains consistency across web application.

---

### 3. Species and Gender Display Format

**Objective**: Clarify whether to use uppercase enum values or capitalized labels.

**Findings**:
- **Storage**: Uppercase enum values (DOG, MALE per Animal type)
- **Display**: Capitalized labels (Dog, Male for better UX)
- **Clarification**: Confirmed in Session 2025-12-02

**Decision**: Store as uppercase enums, display as capitalized labels.

**Implementation**:
```typescript
// Display mapping
const speciesLabels: Record<AnimalSpecies, string> = {
  DOG: 'Dog',
  CAT: 'Cat',
  BIRD: 'Bird',
  RABBIT: 'Rabbit',
  OTHER: 'Other'
};

const sexLabels: Record<AnimalSex, string> = {
  MALE: 'Male',
  FEMALE: 'Female',
  UNKNOWN: 'Unknown'  // Not used in this form
};
```

**Rationale**: Uppercase enums match API/database conventions. Capitalized labels provide better readability for users.

---

### 4. Validation Strategy and Error Messaging

**Objective**: Define validation approach and error message duration.

**Findings**:
- **Spec 034**: 3 seconds for mandatory field toast
- **Spec 037**: 5 seconds for format/size error toast
- **Clarification**: Selected 5 seconds for all validation toasts

**Decision**: On-submit validation with 5-second toast + inline errors.

**Validation Rules**:
- **lastSeenDate**: Required, must not be future date
- **species**: Required, must be one of 5 options
- **breed**: Required once species selected, cleared when species changes
- **sex**: Required, must be MALE or FEMALE
- **age**: Optional, 0-40 range if provided
- **description**: Optional, max 500 characters

**Error Display**:
1. User clicks Continue with invalid/missing fields
2. Toast message displays for 5 seconds: "Please correct the errors below"
3. Invalid fields show inline helper text (e.g., "Please select a species")
4. Navigation blocked until all required fields valid

**Rationale**: 5 seconds gives users time to read error message. Inline errors provide specific guidance per field.

---

### 5. Multi-Step Navigation Pattern

**Objective**: Resolve back arrow behavior for better multi-step UX.

**Findings**:
- **Current (specs 034, 037)**: Back arrow closes entire flow
- **Better UX Pattern**: Back arrow navigates to previous step (preserving state)
- **Distinction**: Browser back button vs in-app back arrow

**Decision**: Implement multi-step navigation with state preservation.

**Navigation Flow**:
- **Step 1/4**: Back arrow → close flow, return to pet list (no previous step)
- **Step 2/4**: Back arrow → Step 1 (preserve flow state)
- **Step 3/4**: Back arrow → Step 2 (preserve flow state)
- **Browser Back Button**: Always close flow, return to pet list (all steps)

**Impact**: Specs 034 and 037 will require updates to implement this pattern.

**Rationale**: Standard multi-step form UX. Users expect to navigate between steps without losing data. Browser back button provides escape hatch to cancel flow.

---

### 6. Form State Management Architecture

**Objective**: Define how form state integrates with existing flow state.

**Findings**:
- **Existing Flow State Context**: `ReportMissingPetFlowContext` (from specs 034, 037)
- **Current Fields**: `microchipNumber`, `photoUrl` (or similar)
- **Needed Extension**: Add Step 3 fields

**Decision**: Extend existing flow state context with Step 3 fields.

**Flow State Extension**:
```typescript
interface ReportMissingPetFlowState {
  // Step 1 (existing)
  microchipNumber: string;
  
  // Step 2 (existing)
  photo: File | null;
  photoUrl?: string;
  
  // Step 3 (NEW)
  lastSeenDate: string;         // ISO 8601 (YYYY-MM-DD)
  species: AnimalSpecies | null;
  breed: string;
  sex: AnimalSex | null;
  age: number | null;
  description: string;
  
  // Flow meta
  currentStep: number;  // 1, 2, 3, or 4
}
```

**Local Component State**:
```typescript
interface AnimalDescriptionFormData {
  lastSeenDate: string;
  species: string;  // Empty string or enum value
  breed: string;
  sex: string;  // Empty string or enum value
  age: string;  // String for easier form binding
  description: string;
  validationErrors: Record<string, string>;
}
```

**Rationale**: Separates form UI state (strings, easy binding) from flow state (typed enums, ready for submission). Form converts to proper types on valid submission.

---

### 7. Species Dropdown Implementation

**Objective**: Determine species data source and rendering approach.

**Findings**:
- **Existing Type**: `AnimalSpecies` enum in `/webApp/src/types/animal.ts`
- **Values**: DOG, CAT, BIRD, RABBIT, OTHER (5 options)
- **No Backend Call Needed**: Static enum

**Decision**: Render dropdown from static AnimalSpecies enum.

**Implementation**:
```typescript
const speciesOptions = Object.values(AnimalSpecies).map(species => ({
  value: species,
  label: speciesLabels[species]
}));
```

**Rationale**: Simple, fast, no network dependency. Aligns with clarification that species are predefined types.

---

### 8. Date Picker Configuration

**Objective**: Define how to block future dates in date picker.

**Findings**:
- **Requirement**: Allow only past dates (including today)
- **HTML5 Support**: `input type="date"` with `max` attribute
- **Fallback**: Custom validation if browser doesn't support `max`

**Decision**: Use HTML5 date input with `max={today}` attribute.

**Implementation**:
```typescript
<input
  type="date"
  max={new Date().toISOString().split('T')[0]}
  value={formData.lastSeenDate}
  onChange={handleDateChange}
/>
```

**Rationale**: Native HTML5 validation where supported. Simple, accessible, no external library needed.

---

### 9. Character Counter for Description

**Objective**: Define behavior for 500-character limit.

**Findings**:
- **Requirement**: Exactly 500 characters, hard limit
- **Behavior**: Prevent further input at 500, truncate pasted text

**Decision**: Implement controlled textarea with character counter.

**Implementation**:
```typescript
const MAX_DESCRIPTION_LENGTH = 500;

const handleDescriptionChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
  const value = e.target.value.slice(0, MAX_DESCRIPTION_LENGTH);
  setFormData(prev => ({ ...prev, description: value }));
};

const handleDescriptionPaste = (e: React.ClipboardEvent<HTMLTextAreaElement>) => {
  e.preventDefault();
  const pastedText = e.clipboardData.getData('text');
  const truncated = pastedText.slice(0, MAX_DESCRIPTION_LENGTH);
  // Insert truncated text at cursor position
};
```

**Display**: `{formData.description.length}/500 characters`

**Rationale**: Clear feedback to user. Hard limit prevents server-side validation issues.

---

### 10. GPS Button Placeholder

**Objective**: Clarify GPS button behavior in this spec.

**Findings**:
- **Requirement**: Display button but no functionality
- **Future Spec**: GPS location capture in separate specification
- **Current State**: Visual placeholder only

**Decision**: Render disabled/non-functional button with appropriate styling.

**Implementation**:
```typescript
<button
  type="button"
  disabled
  className="gps-button-placeholder"
  data-testid="animalDescription.requestGps.click"
>
  Request GPS position
</button>
```

**Styling**: Indicate disabled state visually (grayed out or informational styling).

**Rationale**: Matches design, prepares UI for future GPS feature without implementing non-functional code.

---

## Technology Stack Summary

| Component | Technology | Version | Rationale |
|-----------|-----------|---------|-----------|
| Language | TypeScript | 5.x | Type safety, IDE support |
| Framework | React | 18.x | Existing web app framework |
| State Management | React Context | 18.x | Consistent with specs 034, 037 |
| Routing | React Router | 6.x | Existing routing solution |
| Testing (Unit) | Vitest + React Testing Library | Latest | Project standard |
| Testing (E2E) | Selenium + Cucumber | Latest | Java/Maven E2E stack |
| Form Library | None (vanilla React) | N/A | Simple form, no need for library |
| Date Picker | HTML5 native | N/A | Browser support adequate |

## Alternatives Considered

### 1. Form State Library (Formik, React Hook Form)

**Rejected**: Form is relatively simple (7 fields, basic validation). Vanilla React provides sufficient control without adding dependency.

### 2. Date Picker Library (react-datepicker, date-fns)

**Rejected**: HTML5 `input type="date"` provides adequate functionality with built-in browser support. No need for external library.

### 3. Validation Library (Yup, Zod)

**Rejected**: Validation rules are straightforward (required checks, range validation). Custom validation functions are lighter weight and more maintainable.

### 4. Direct URL Field Names in Spec

**Rejected**: Clarified to use Animal type field names for consistency with existing codebase.

## Risk Assessment

| Risk | Impact | Mitigation |
|------|--------|-----------|
| Specs 034/037 back arrow behavior inconsistent | Medium | Document navigation change, create follow-up tickets to update those specs |
| Future GPS feature requires refactoring | Low | Button already placed, state structure accommodates future lat/long fields |
| Browser date picker inconsistencies | Low | Test across target browsers, add custom validation fallback |
| Character counter UX unclear | Low | Follow standard pattern (live counter, prevent input at limit) |

## Open Questions (Resolved)

All questions from spec clarification session resolved:
1. ✅ Species data source → Predefined TypeScript types
2. ✅ Gender format → Uppercase storage, capitalized display
3. ✅ Toast duration → 5 seconds
4. ✅ Field names → Match Animal type
5. ✅ Breed input type → Free-text (no autocomplete)
6. ✅ Back arrow navigation → Multi-step pattern (to previous step)

## Next Steps

Proceed to Phase 1:
1. Generate [data-model.md](./data-model.md) with complete type definitions
2. Generate [contracts/](./contracts/) with TypeScript interfaces
3. Generate [quickstart.md](./quickstart.md) with implementation guide

---

**Research completed**: December 2, 2025  
**Status**: Ready for Phase 1 (Design & Contracts)

