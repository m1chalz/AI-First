# Data Model: Web Microchip Number Screen

**Feature**: 034-web-chip-number-screen  
**Date**: 2025-12-01  
**Phase**: 1 (Design & Contracts)

## Overview

This document defines the data structures used in the web microchip number screen feature. Since this is step 1 of a 4-step flow, the data model primarily focuses on the flow state structure that will persist across all steps.

## Entities

### ReportMissingPetFlowState

**Purpose**: Holds temporary state for the 4-step "report missing pet" flow

**Lifecycle**:
- Created when user initiates the flow from pet list
- Updated as user progresses through steps 1-4
- Cleared when user:
  - Completes the flow (submits at step 4)
  - Cancels via back arrow button
  - Uses browser back button
  - Refreshes the page
- NOT persisted to localStorage/sessionStorage (in-memory only via React Context)

**Fields**:

| Field Name | Type | Required | Default | Description | Validation Rules |
|------------|------|----------|---------|-------------|------------------|
| `currentStep` | `FlowStep` (enum) | Yes | `FlowStep.Microchip` | Current step in the flow, determines which steps are accessible | - Valid values: `Microchip`, `Photo`, `Details`, `Contact`, `Completed`<br>- Progresses forward as user completes steps<br>- Used by route guards to control access |
| `microchipNumber` | `string` | No | `''` | 15-digit microchip identifier (digits only, no hyphens) | - Max length: 15 characters<br>- Must contain only digits 0-9<br>- Empty string if not provided<br>- Leading zeros preserved |

**Example (TypeScript)**:

```typescript
enum FlowStep {
  Microchip = 'microchip',
  Photo = 'photo',
  Details = 'details',
  Contact = 'contact',
  Completed = 'completed',
}

interface ReportMissingPetFlowState {
  currentStep: FlowStep;
  microchipNumber: string;  // e.g., "123456789012345" or ""
}

// Example instances
const initialState: ReportMissingPetFlowState = {
  currentStep: FlowStep.Microchip,
  microchipNumber: '',
};

const afterMicrochipWithChip: ReportMissingPetFlowState = {
  currentStep: FlowStep.Photo,  // Ready for step 2
  microchipNumber: '123456789012345',
};

const afterMicrochipNoChip: ReportMissingPetFlowState = {
  currentStep: FlowStep.Photo,  // Ready for step 2
  microchipNumber: '',  // User skipped microchip number (optional field)
};
```

**Storage**:
- In-memory: React Context (`ReportMissingPetFlowContext`)
- NO persistence to browser storage (per specification clarification)

**Access Pattern**:
- Read: `const { flowState } = useReportMissingPetFlow();`
- Update: `updateFlowState({ microchipNumber: '123456789012345', step1Completed: true })`
- Clear: `clearFlowState()`

---

### MicrochipNumberFormData (Component-Local State)

**Purpose**: Temporary state for the microchip number input field during user interaction

**Lifecycle**:
- Created when Step1MicrochipNumber component mounts
- Updated on every keystroke/paste
- Saved to `ReportMissingPetFlowState.microchipNumber` when user clicks Continue
- Discarded when component unmounts

**Fields**:

| Field Name | Type | Required | Default | Description |
|------------|------|----------|---------|-------------|
| `value` | `string` | Yes | `''` | Raw digits entered by user (no hyphens) |
| `formattedValue` | `string` | Yes | `''` | Display value with hyphens (00000-00000-00000 format) |

**Example (TypeScript)**:

```typescript
interface MicrochipNumberFormData {
  value: string;           // e.g., "123456789012345"
  formattedValue: string;  // e.g., "12345-67890-12345"
}

// Managed by useMicrochipFormatter hook
const { value, formattedValue, handleChange, handlePaste } = useMicrochipFormatter();
```

**Relationship to FlowState**:
- `value` (digits only) is saved to `ReportMissingPetFlowState.microchipNumber` on Continue
- `formattedValue` (with hyphens) is display-only, NOT persisted

---

## Data Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│  User Interaction                                                │
│  (Typing / Paste)                                                │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│  Component State (useMicrochipFormatter hook)                    │
│  - value: "123456789012345"                                      │
│  - formattedValue: "12345-67890-12345"                           │
│                                                                   │
│  Formatting Logic:                                               │
│  1. Strip non-numeric characters                                 │
│  2. Limit to 15 digits                                           │
│  3. Insert hyphens at positions 5 and 10                         │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         │ User clicks "Continue"
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│  Flow State (ReportMissingPetFlowContext)                        │
│  - microchipNumber: "123456789012345" (digits only)              │
│  - step1Completed: true                                          │
│                                                                   │
│  Persists across:                                                │
│  - Step 1 → Step 2 → Step 3 → Step 4                             │
│  - Forward/backward navigation within flow                       │
│                                                                   │
│  Cleared on:                                                     │
│  - Flow cancellation (back arrow click)                          │
│  - Browser back button                                           │
│  - Page refresh                                                  │
│  - Flow completion                                               │
└─────────────────────────────────────────────────────────────────┘
```

---

## Validation Rules

### Microchip Number Validation

**Client-Side (Component)**:
1. **Input filtering**: Only accept digits 0-9 (via `input.replace(/\D/g, '')`)
2. **Length limit**: Maximum 15 digits (enforced during typing and paste)
3. **Format display**: Insert hyphens at positions 5 and 10 for display
4. **Optional field**: Empty string is valid (user can skip)

**No Server-Side Validation**: This step does NOT submit to backend - data stays in flow state

**Examples**:

| User Input | After Filtering | After Formatting | Stored Value | Valid? |
|------------|----------------|------------------|--------------|--------|
| `123456789012345` | `123456789012345` | `12345-67890-12345` | `123456789012345` | ✅ Yes |
| `ABC123XYZ456` | `123456` | `12345-6` | `123456` | ✅ Yes (paste sanitization) |
| `123456789012345678` | `123456789012345` | `12345-67890-12345` | `123456789012345` | ✅ Yes (truncated to 15) |
| `12345` | `12345` | `12345` | `12345` | ✅ Yes (partial entry) |
| `` (empty) | `` | `` | `` | ✅ Yes (optional field) |

---

## State Transitions

### Step 1 Microchip Number Screen

```
Initial State
↓
[User arrives at /report-missing/microchip]
↓
flowState.currentStep = FlowStep.Microchip
flowState.microchipNumber = ''
↓
[User types digits OR pastes content]
↓
Component state updates (value, formattedValue)
↓
[User clicks "Continue"]
↓
flowState.microchipNumber = value (digits only)
flowState.currentStep = FlowStep.Photo
↓
Navigate to /report-missing/photo
```

### Flow Cancellation

```
[User at any step in flow]
↓
[User clicks back arrow OR browser back button]
↓
clearFlowState()
flowState = initialState (all fields reset)
↓
Navigate to /pets (pet list)
```

### Page Refresh

```
[User at /report-missing/{photo|details|contact}]
↓
[User refreshes page]
↓
React Context re-initializes
flowState = initialState (currentStep = Microchip, state lost)
↓
Route guard detects photo/details/contact with currentStep = Microchip
↓
Redirect to /report-missing/microchip (per clarification)
```

---

## Future Considerations

**Step 2-4 Fields** (Not implemented in this feature):
- Will be added to `ReportMissingPetFlowState` interface as additional properties
- Examples: `photoUrl`, `lastSeenLocation`, `lastSeenDate`, `additionalNotes`, `contactPhone`, etc.
- Each step advances `currentStep` enum value (Microchip → Photo → Details → Contact → Completed)

**Backend Integration** (Not in this feature):
- Flow completion (step 4) will POST full `ReportMissingPetFlowState` to backend API
- Backend will validate all fields and create announcement
- API endpoint (future): `POST /api/announcements` with `ReportMissingPetFlowState` payload

---

## TypeScript Type Definitions

See [contracts/FlowState.ts](./contracts/FlowState.ts) for complete TypeScript interface definitions.

---

**Data model completed**: 2025-12-01  
**Next**: Generate contracts and quickstart guide

