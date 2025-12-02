# Developer Quickstart: Web Owner's Details Screen

**Feature**: 041-web-owners-details  
**Date**: 2025-12-02

## Prerequisites

Before starting development on this feature:

1. **Node.js v24 (LTS)** installed
2. **Repository cloned** and on branch `041-web-owners-details`
3. **Dependencies installed**: Run `npm install` from `/webApp` directory
4. **Familiarity with existing flow**: Review Steps 1-3 (specs 034, 037, 039) for pattern consistency

---

## Quick Setup

```bash
# 1. Navigate to project root
cd /path/to/AI-First

# 2. Verify you're on the feature branch
git branch  # Should show * 041-web-owners-details

# 3. Install web app dependencies (if not already done)
cd webApp
npm install

# 4. Verify existing flow works
npm run start

# 5. In browser, navigate to http://localhost:3000
# Click "Report Missing Pet" and complete Steps 1-3
# Step 4 (Contact) currently shows debug view - this will be replaced
```

---

## Development Workflow

### 1. Run Development Server

```bash
cd webApp
npm run start
```

**Access**: http://localhost:3000  
**Hot Reload**: Enabled (changes reflect immediately)

---

### 2. Run Tests

```bash
# Run all tests
npm test

# Run tests with coverage
npm test -- --coverage

# Run tests in watch mode (during development)
npm test -- --watch

# Run specific test file
npm test use-contact-form.test.ts
```

**Coverage Target**: ≥80% line + branch coverage

**Coverage Report**: `webApp/coverage/index.html` (after running with `--coverage`)

---

### 3. File Organization

**Files to Create**:

```
webApp/src/
├── hooks/
│   ├── use-contact-form.ts                   # NEW: Main hook for contact form logic
│   └── __tests__/
│       └── use-contact-form.test.ts          # NEW: Hook unit tests
├── components/
│   └── ReportMissingPet/
│       ├── ContactScreen.tsx                 # UPDATE: Replace debug view
│       ├── SummaryScreen.tsx                 # NEW: Summary debug view
│       └── __tests__/
│           ├── ContactScreen.test.tsx        # NEW: Component tests
│           └── SummaryScreen.test.tsx        # NEW: Component tests
├── models/
│   └── ReportMissingPetFlow.ts               # UPDATE: Add phone, email, reward
└── routes/
    └── report-missing-pet-routes.tsx         # UPDATE: Add summary route
```

**Files to Reuse** (NO modifications needed):

```
webApp/src/components/ReportMissingPet/
├── ReportMissingPetLayout.tsx           # Existing layout wrapper
├── ReportMissingPetLayout.module.css    # Existing styles
├── Header.tsx                           # Existing header component
└── contexts/
    └── ReportMissingPetFlowContext.tsx  # Existing flow context
```

---

## Implementation Order (TDD Approach)

### Phase 1: Data Model Extension

**File**: `/webApp/src/models/ReportMissingPetFlow.ts`

**Task**: Add new fields to existing interface

```typescript
export interface ReportMissingPetFlowState {
  // ... existing fields
  
  // NEW:
  phone: string;
  email: string;
  reward: string;
}

export const initialFlowState: ReportMissingPetFlowState = {
  // ... existing initial values
  phone: '',
  email: '',
  reward: '',
};
```

**Test**: No unit tests needed for interface changes

---

### Phase 2: Hook Implementation (TDD)

**File**: `/webApp/src/hooks/use-contact-form.ts`  
**Test**: `/webApp/src/hooks/__tests__/use-contact-form.test.ts`

**TDD Cycle**:

1. **RED**: Write failing test

```typescript
  it('should validate phone with at least one digit', () => {
    // Test setup
    const { result } = renderHook(() => useContactForm());
    
    // Given: User enters phone with digits
    act(() => {
      result.current.handlePhoneChange({ target: { value: '+48123456789' } } as any);
      // No blur validation - validation occurs on Continue click
    });
    
    // Then: No error shown
    expect(result.current.phoneError).toBe('');
  });
```

2. **GREEN**: Implement minimum code to pass

```typescript
export function useContactForm() {
  const [phone, setPhone] = useState('');
  const [phoneError, setPhoneError] = useState('');
  
  // Validation aligned with backend /server/src/lib/validators.ts
  const validatePhone = (value: string): boolean => {
    if (value.trim() === '') return true;
    const isValid = /\d/.test(value); // Must contain at least one digit
    if (!isValid) {
      setPhoneError('Enter a valid phone number');
      return false;
    }
    setPhoneError('');
    return true;
  };
  
  const handlePhoneChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPhone(e.target.value);
  };
  
  // No blur validation - validation only occurs on Continue click
  
  return { phone, phoneError, handlePhoneChange };
}
```

3. **REFACTOR**: Improve code quality, extract helpers

**Repeat** for:
- Email validation
- Reward handling (no validation)
- `hasAtLeastOneContact()` logic
- `handleSubmit()` integration with flow context

---

### Phase 3: Component Implementation

**File**: `/webApp/src/components/ReportMissingPet/ContactScreen.tsx`

**Checklist**:
- [ ] Replace existing debug view with form UI
- [ ] Use `useContactForm` hook for state/validation
- [ ] Use existing `ReportMissingPetLayout` wrapper
- [ ] Use existing styles from `.module.css`
- [ ] Add `data-testid` attributes to all interactive elements
- [ ] Implement in-app back (→ Step 3)
- [ ] Implement browser back handler (→ clear flow, return to pet list)
- [ ] Redirect to Step 1 if no active flow (`flowState.currentStep === FlowStep.Empty`)

**Test First** (component tests):

```typescript
it('should render all form fields', () => {
  render(<ContactScreen />);
  
  expect(screen.getByLabelText(/phone number/i)).toBeInTheDocument();
  expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
  expect(screen.getByLabelText(/reward/i)).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /continue/i })).toBeInTheDocument();
});
```

---

### Phase 4: Summary Screen

**File**: `/webApp/src/components/ReportMissingPet/SummaryScreen.tsx`

**Implementation**:
- Reuse existing debug view format from current `ContactScreen`
- Display all flow state fields with labels
- Add "Back to Contact" and "Complete" buttons
- "Complete" clears flow and returns to pet list

**Routing** (`report-missing-pet-routes.tsx`):

```typescript
export const ReportMissingPetRoutes = {
  // ... existing routes
  summary: '/report-missing-pet/summary',
};

// In router config:
<Route path="/report-missing-pet/summary" element={<SummaryScreen />} />
```

---

## Testing Guide

### Unit Tests (Hooks)

**Focus**: Validation logic, state management, flow integration

**Pattern**:
```typescript
describe('useContactForm', () => {
  describe('phone validation', () => {
    it('should validate phone containing at least one digit', () => { ... });
    it('should reject phone with no digits (e.g. "abc")', () => { ... });
    it('should accept phone with any format as long as it has digits', () => { ... });
  });
  
  describe('email validation', () => { ... });
  
  describe('combined validation', () => {
    it('should require at least one contact method', () => { ... });
    it('should reject valid phone + invalid email', () => { ... });
  });
});
```

**Run**: `npm test use-contact-form.test.ts`

---

### Component Tests

**Focus**: Rendering, user interactions, navigation

**Pattern**:
```typescript
describe('ContactScreen', () => {
  it('should render all form fields', () => { ... });
  
  it('should show validation errors on Continue click', () => {
    render(<ContactScreen />);
    
    const phoneInput = screen.getByTestId('contact.phoneNumber.input');
    fireEvent.change(phoneInput, { target: { value: 'abc' } });
    
    const continueButton = screen.getByTestId('contact.continue.click');
    fireEvent.click(continueButton);
    
    expect(screen.getByText(/enter a valid phone number/i)).toBeInTheDocument();
  });
  
  it('should navigate to summary on valid submission', () => { ... });
});
```

**Run**: `npm test ContactScreen.test.tsx`

---

### Manual Testing Checklist

**Happy Path**:
1. ☐ Navigate through Steps 1-3
2. ☐ On Step 4, enter phone only → Continue → see summary
3. ☐ Back to Step 4, clear phone, enter email only → Continue → see summary
4. ☐ Back to Step 4, enter both phone and email → Continue → see summary

**Validation**:
5. ☐ Enter invalid phone (e.g., "abc") → click Continue → see error + toast
6. ☐ Enter invalid email (e.g., "user@") → click Continue → see error + toast
7. ☐ Leave both empty → click Continue → stays on Step 4

**Mixed Valid/Invalid**:
8. ☐ Enter valid phone + invalid email → click Continue → stays on Step 4, shows email error
9. ☐ Enter invalid phone + valid email → click Continue → stays on Step 4, shows phone error

**Navigation**:
10. ☐ Click in-app back arrow → returns to Step 3, data persists
11. ☐ Browser back button → clears flow, returns to pet list
12. ☐ Direct URL to `/report-missing-pet/contact` without flow → redirects to Step 1

**Reward Field**:
13. ☐ Enter any text in reward → no validation errors, text persists

---

## Common Patterns & Utilities

### Reusable Validation Helpers

```typescript
// Phone sanitization
export function sanitizePhone(phone: string): string {
  return phone.replace(/[\s\-]/g, '');
}

// Email normalization
export function normalizeEmail(email: string): string {
  return email.trim().toLowerCase();
}

// Phone regex
export const PHONE_REGEX = /\d/; // Must contain at least one digit (matches backend)

// Email regex
export const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
```

### Error Message Constants

```typescript
export const ERROR_MESSAGES = {
  PHONE_INVALID: 'Enter a valid phone number',
  EMAIL_INVALID: 'Enter a valid email address',
  NO_CONTACT: 'Please provide at least one contact method (phone or email)',
};
```

---

## Debugging Tips

### Issue: Validation not triggering on Continue click

**Solution**: Ensure `handleSubmit` is called in Continue button handler and validates all fields

```typescript
const handleContinue = () => {
  if (handleSubmit()) {  // ← Must validate before navigation
    navigate(ReportMissingPetRoutes.summary);
  }
};
```

### Issue: Continue button blocks navigation incorrectly

**Solution**: Check `handleSubmit` return value

```typescript
const handleContinue = () => {
  const isValid = handleSubmit();  // ← Must check return value
  if (isValid) {
    navigate(ReportMissingPetRoutes.summary);
  }
  // If false, do nothing (stay on page)
};
```

### Issue: Flow state not persisting

**Solution**: Verify `updateFlowState` is called on submit

```typescript
const handleSubmit = (): boolean => {
  // ... validation logic ...
  
  if (allValid && hasContact) {
    updateFlowState({  // ← Must call this
      phone: phone.trim(),
      email: email.trim(),
      reward: reward.trim(),
    });
    return true;
  }
  return false;
};
```

### Issue: Tests failing with "Cannot find module 'ReportMissingPetFlowContext'"

**Solution**: Mock the context in test setup

```typescript
vi.mock('../../contexts/ReportMissingPetFlowContext', () => ({
  useReportMissingPetFlow: vi.fn(() => ({
    flowState: mockFlowState,
    updateFlowState: vi.fn(),
    clearFlowState: vi.fn(),
  })),
}));
```

---

## Code Style Guidelines

1. **File Naming**: Kebab-case for non-components (`use-contact-form.ts`)
2. **Test IDs**: `contact.{element}.{action}` (e.g., `contact.phoneNumber.input`)
3. **TypeScript**: Explicit types for all public APIs
4. **Imports**: Group stdlib → third-party → project imports
5. **Comments**: JSDoc for hooks and complex functions
6. **Line Length**: Max 100 characters
7. **Semicolons**: Required

---

## Resources

- **Spec Document**: [spec.md](./spec.md)
- **Implementation Plan**: [plan.md](./plan.md)
- **Data Model**: [data-model.md](./data-model.md)
- **Reference Implementations**:
  - Step 3 Details: `/webApp/src/components/ReportMissingPet/DetailsScreen.tsx`
  - Validation Hook: `/webApp/src/hooks/use-details-form.ts`
  - Layout: `/webApp/src/components/ReportMissingPetLayout.tsx`

---

## Getting Help

**Questions about**:
- **Existing patterns**: Review specs 034, 037, 039
- **Test setup**: Check existing test files in `__tests__/` directories
- **Constitution compliance**: See `.specify/memory/constitution.md`
- **Git workflow**: Feature branch already created, commit regularly

---

## Ready to Start?

1. ☐ Read spec.md (understand requirements)
2. ☐ Read plan.md (understand architecture)
3. ☐ Read data-model.md (understand data structures)
4. ☐ Run `npm install` and `npm run start` (verify setup)
5. ☐ Start with TDD: Write first test for `useContactForm`
6. ☐ Follow implementation order above
7. ☐ Commit early and often

**Good luck!** 🚀

