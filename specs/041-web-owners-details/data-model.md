# Data Model: Web Owner's Details Screen

**Feature**: 041-web-owners-details  
**Date**: 2025-12-02

## Overview

This document defines the data structures for contact information collection in Step 4/4 of the Missing Pet flow. The model extends the existing `ReportMissingPetFlowState` interface with three new fields representing owner contact details and reward offer.

---

## Flow State Extension

### ReportMissingPetFlowState (Extended)

**Location**: `/webApp/src/models/ReportMissingPetFlow.ts`

**Extension** (add to existing interface):

```typescript
export interface ReportMissingPetFlowState {
  // ... existing fields (microchipNumber, photo, lastSeenDate, species, breed, sex, age, description, latitude, longitude)
  
  // NEW: Step 4 contact fields
  phone: string;  // Owner's phone number (optional but must be valid if provided)
  email: string;  // Owner's email address (optional but must be valid if provided)
  reward: string; // Free-form reward description (optional, no validation)
}
```

**Initial Values** (add to `initialFlowState`):

```typescript
export const initialFlowState: ReportMissingPetFlowState = {
  currentStep: FlowStep.Empty,
  // ... existing initial values
  phone: '',
  email: '',
  reward: '',
};
```

---

## Field Specifications

### phone: string

**Purpose**: Owner's phone number for contact by finders or shelters

**Constraints**:
- **Optional**: Can be empty string
- **Format**: Must contain at least one digit (any format accepted)
- **Validation**: Required only if non-empty (aligned with backend `/server/src/lib/validators.ts`)

**Validation Rules**:
- If empty: valid (no contact method provided yet)
- If non-empty: must match regex `/\d/` (contains at least one digit - matches backend)
- At least one of `phone` OR `email` must be valid to proceed

**Examples**:
- Valid: `+48123456789`, `123`, `12345678901234567890`, `+1 234 567 890`, `abc123`
- Invalid: `abc` (no digits), `+++` (no digits)

**Error Messages**:
- "Enter a valid phone number" (when non-empty but invalid)

---

### email: string

**Purpose**: Owner's email address for contact and confirmation notifications

**Constraints**:
- **Optional**: Can be empty string
- **Format**: RFC 5322 basic format (local@domain.tld)
- **Validation**: Required only if non-empty (aligned with backend `/server/src/lib/validators.ts`)
- **Case**: Case-insensitive (stored as entered, compared lowercase)
- **Trimming**: Whitespace trimmed for validation

**Validation Rules**:
- If empty: valid (no contact method provided yet)
- If non-empty: must match regex `/^[^\s@]+@[^\s@]+\.[^\s@]+$/` after trimming (matches backend)
- At least one of `phone` OR `email` must be valid to proceed

**Examples**:
- Valid: `owner@example.com`, `user+tag@domain.co.uk`, `test.user@sub.domain.com`
- Invalid: `owner@` (no domain), `@example.com` (no local), `owner` (no @), `owner @example.com` (space)

**Error Messages**:
- "Enter a valid email address" (when non-empty but invalid)

---

### reward: string

**Purpose**: Free-form text describing reward offered for finding the pet

**Constraints**:
- **Optional**: Can be empty string
- **Format**: Free-form text (any UTF-8 characters)
- **Validation**: None - no validation or character limits
- **Length**: Unlimited (no max length enforced)

**Validation Rules**:
- No validation required
- All input accepted as-is

**Examples**:
- Valid: `$250 gift card + hugs`, `Monetary reward available`, `Thank you reward`, `` (empty)
- Invalid: None - all input valid

**Error Messages**: None

---

## Validation Logic

### Combined Validation Rule

For successful form submission, the following conditions must ALL be true:

1. **Phone is valid**: Empty OR matches phone regex after sanitization
2. **Email is valid**: Empty OR matches email regex after trimming
3. **At least one contact provided**: `(phone !== '' && phoneValid) OR (email !== '' && emailValid)`

**Validation Scenarios**:

| Phone   | Email   | Phone Valid | Email Valid | Can Submit | Reason                          |
|---------|---------|-------------|-------------|-----------|---------------------------------|
| Empty   | Empty   | ✓           | ✓           | ✗         | No contact method provided       |
| Valid   | Empty   | ✓           | ✓           | ✓         | Phone only (sufficient)          |
| Empty   | Valid   | ✓           | ✓           | ✓         | Email only (sufficient)          |
| Valid   | Valid   | ✓           | ✓           | ✓         | Both provided (ideal)            |
| Invalid | Empty   | ✗           | ✓           | ✗         | Phone invalid, no alternative    |
| Empty   | Invalid | ✓           | ✗           | ✗         | Email invalid, no alternative    |
| Valid   | Invalid | ✓           | ✗           | ✗         | All non-empty must be valid      |
| Invalid | Valid   | ✗           | ✓           | ✗         | All non-empty must be valid      |
| Invalid | Invalid | ✗           | ✗           | ✗         | Both invalid                     |

---

## State Updates

### On Field Change (Real-time)

**Trigger**: User types in input field  
**Action**: Update local component state (not flow context yet)

```typescript
handlePhoneChange = (e: React.ChangeEvent<HTMLInputElement>) => {
  setPhone(e.target.value); // Local state only
};
```

### On Continue Click (Validation)

**Trigger**: User clicks Continue button  
**Action**: Validate all fields, set error states if invalid, show toast message

```typescript
handleSubmit = () => {
  const phoneValid = validatePhone(phone);
  const emailValid = validateEmail(email);
  if (!(phoneValid && emailValid)) {
    setPhoneError('Enter a valid phone number');
  } else {
    setPhoneError('');
  }
};
```

### On Form Submit (Persist)

**Trigger**: User clicks Continue button  
**Action**: Validate all fields, persist to flow context if valid, navigate to summary

```typescript
handleSubmit = (): boolean => {
  const phoneValid = validatePhone(phone);
  const emailValid = validateEmail(email);
  const hasContact = hasAtLeastOneContact();
  
  if (phoneValid && emailValid && hasContact) {
    updateFlowState({
      phone: phone.trim(),
      email: email.trim(),
      reward: reward.trim(),
      currentStep: FlowStep.Completed
    });
    return true; // Allow navigation
  }
  
  return false; // Block navigation
};
```

---

## Flow State Lifecycle

### Initialization

**When**: User lands on Step 4  
**State**: Load existing values from flow context (if returning from summary) or empty strings (if first visit)

```typescript
const { flowState } = useReportMissingPetFlow();
const [phone, setPhone] = useState(flowState.phone);
const [email, setEmail] = useState(flowState.email);
const [reward, setReward] = useState(flowState.reward);
```

### Navigation Within Flow

**In-app back (Step 4 → Step 3)**: Flow context preserved, values persist  
**In-app forward (Step 3 → Step 4)**: Flow context preserved, previous values restored

### Navigation Outside Flow

**Browser back button**: Flow context cleared, return to pet list  
**Browser refresh**: Flow context cleared (sessionStorage not used per spec 034, 037, 039 pattern)

### Completion

**When**: User completes summary screen  
**Action**: Clear flow context via `clearFlowState()`, return to pet list

---

## Type Definitions

### Hook Return Type

```typescript
interface UseContactFormReturn {
  phone: string;
  email: string;
  reward: string;
  phoneError: string;
  emailError: string;
  isValid: boolean; // Computed: hasAtLeastOneContact()
  handlePhoneChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  handleEmailChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  handleRewardChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  // No blur handlers - validation only on Continue click
  handleSubmit: () => boolean;
}
```

---

## Data Flow Diagram

```
┌──────────────────────────────────────────────────────────────┐
│  User Input (ContactScreen)                                   │
│  ┌──────────┐  ┌───────────┐  ┌─────────────────────┐        │
│  │  Phone   │  │   Email   │  │  Reward Description │        │
│  │  Input   │  │   Input   │  │      Input          │        │
│  └────┬─────┘  └─────┬─────┘  └──────────┬──────────┘        │
│       │              │                     │                   │
│       ▼              ▼                     ▼                   │
│  ┌────────────────────────────────────────────────┐           │
│  │  useContactForm Hook                           │           │
│  │  - Local state (phone, email, reward)         │           │
│  │  - Error state (phoneError, emailError)       │           │
│  │  - Validation logic                           │           │
│  └────────────────────┬───────────────────────────┘           │
│                       │                                       │
│                       │ handleSubmit() called                 │
│                       ▼                                       │
│          ┌────────────────────────────┐                      │
│          │  Validation Check          │                      │
│          │  - Phone valid?            │                      │
│          │  - Email valid?            │                      │
│          │  - At least one contact?   │                      │
│          └────────┬─────────────┬─────┘                      │
│                   │             │                            │
│         Valid ────┤             ├──── Invalid               │
│                   │             │                            │
│                   ▼             ▼                            │
│   ┌────────────────────┐  ┌──────────────────┐              │
│   │ updateFlowState()  │  │ Return false     │              │
│   │ - Save phone       │  │ Block navigation │              │
│   │ - Save email       │  │ Keep errors      │              │
│   │ - Save reward      │  └──────────────────┘              │
│   │ - Set step=Complete│                                    │
│   └────────┬───────────┘                                    │
│            │                                                 │
│            ▼                                                 │
│   ┌──────────────────────────────────┐                      │
│   │  ReportMissingPetFlowContext     │                      │
│   │  Persists data in React Context  │                      │
│   └──────────────────────────────────┘                      │
│            │                                                 │
│            ▼                                                 │
│   ┌──────────────────────────────────┐                      │
│   │  Navigate to Summary Screen      │                      │
│   │  Display all collected flow data │                      │
│   └──────────────────────────────────┘                      │
└──────────────────────────────────────────────────────────────┘
```

---

## Testing Data

### Test Fixtures

```typescript
// Valid phone numbers (must contain at least one digit)
export const VALID_PHONES = [
  '+48123456789',
  '1234567',
  '12345678901',
  '+1 234 567 890',
  '123-456-7890',
  'abc123',        // Valid - contains digits
  '++48123',       // Valid - contains digits
];

// Invalid phone numbers
export const INVALID_PHONES = [
  'abc',           // No digits
  '+++',           // No digits
  '',              // Empty (but treated as valid in validation)
];

// Valid emails
export const VALID_EMAILS = [
  'user@example.com',
  'test.user@domain.co.uk',
  'user+tag@example.com',
];

// Invalid emails
export const INVALID_EMAILS = [
  'user@',         // No domain
  '@example.com',  // No local
  'user',          // No @
  'user @example.com', // Space
  '',              // Empty (but treated as valid in validation)
];

// Test scenarios
export const TEST_SCENARIOS = [
  { phone: '+48123456789', email: '', reward: '', expected: true }, // Phone only
  { phone: '', email: 'user@example.com', reward: '', expected: true }, // Email only
  { phone: '+48123456789', email: 'user@example.com', reward: '$250', expected: true }, // Both
  { phone: '', email: '', reward: '', expected: false }, // Neither
  { phone: 'abc', email: '', reward: '', expected: false }, // Invalid phone only
  { phone: '', email: 'invalid', reward: '', expected: false }, // Invalid email only
  { phone: '+48123456789', email: 'invalid', reward: '', expected: false }, // Valid phone + invalid email
  { phone: 'abc', email: 'user@example.com', reward: '', expected: false }, // Invalid phone + valid email
];
```

---

## Migration Notes

**No database migrations required** - this is a frontend-only data model stored in React Context. Backend submission is out of scope for this specification.

---

## Future Considerations

When backend submission is implemented (separate integration spec):

1. **API Payload**: Include `phone`, `email`, `reward` in POST /api/announcements request body
2. **Backend Validation**: Server-side validation using `/server/src/lib/validators.ts` (defense in depth)
3. **Email Confirmation**: Server sends confirmation email to `email` field (if provided)
4. **SMS Notification**: Potential SMS to `phone` field (if provided and SMS service available)

