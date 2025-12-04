# Implementation Plan: Web Owner's Details Screen

**Branch**: `041-web-owners-details` | **Date**: 2025-12-02 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/041-web-owners-details/spec.md`

## Summary

Implement Step 4/4 (Owner's Details) of the Missing Pet flow for the web platform. This screen collects contact information (phone OR email, both optional but at least one required) and an optional reward description. The implementation reuses existing web flow infrastructure (ReportMissingPetLayout, styles, context, hooks patterns) established in Steps 1-3 (specs 034, 037, 039). Validation occurs only on Continue click (consistent with specs 034, 037, 039) with always-enabled Continue button. A summary screen displays collected flow state in a debug view format.

## Technical Context

**Language/Version**: TypeScript 5.x (React 18.x)  
**Primary Dependencies**: React, React Router, existing ReportMissingPetFlowContext  
**Storage**: sessionStorage (managed by existing context, cleared on refresh)  
**Testing**: Vitest + React Testing Library  
**Target Platform**: Modern web browsers (Chrome 90+, Firefox 88+, Safari 14+, Edge 90+)  
**Project Type**: Web application (React SPA)  
**Performance Goals**: N/A (standard web form performance sufficient)  
**Constraints**: Must match Figma design node 315-15943, reuse existing components/styles  
**Scale/Scope**: 2 new screens (Contact, Summary), 1 custom hook, ~200 lines of new code

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Platform Architecture Compliance

- [x] **Platform Independence**: Web platform implements full stack independently
  - Web: Domain models (TypeScript interfaces), services, state management (React hooks/Context) in `/webApp`
  - Backend: Independent Node.js/Express API in `/server` (out of scope for this spec)
  - NO shared compiled code between platforms
  - Violation justification: N/A - compliant

- [ ] **Android MVI Architecture**: N/A (Web-only feature)

- [ ] **iOS MVVM-C Architecture**: N/A (Web-only feature)

- [x] **Interface-Based Design**: Web uses TypeScript interfaces for models
  - Models defined as TypeScript interfaces in `/webApp/src/models/ReportMissingPetFlow.ts`
  - Extended with contact details fields (phone, email, reward)
  - Violation justification: N/A - compliant

- [x] **Dependency Injection**: Web uses React Context for state management
  - Existing `ReportMissingPetFlowContext` provides flow state management
  - Custom hooks consume context via `useReportMissingPetFlow()`
  - Violation justification: N/A - compliant

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for web
  - Hook tests: `/webApp/src/hooks/__tests__/use-contact-form.test.ts`
  - Component tests: `/webApp/src/components/ReportMissingPet/__tests__/ContactScreen.test.tsx`
  - Coverage target: 80% line + branch coverage
  - Run command: `npm test -- --coverage` (from webApp/)
  - Violation justification: N/A - compliant

- [ ] **End-to-End Tests**: E2E tests deferred to separate integration spec
  - This spec focuses on UI components and data collection
  - Backend submission covered in separate integration spec
  - Violation justification: E2E tests out of scope per spec clarifications

- [x] **Asynchronous Programming Standards**: Web uses native async/await
  - Form submission uses async patterns for future backend integration
  - No Promise chains or callbacks
  - Violation justification: N/A - compliant

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Web: `data-testid` attribute on all interactive elements
  - Naming convention: `contact.{element}.{action}` (e.g., `contact.phoneNumber.input`, `contact.continue.button`)
  - Violation justification: N/A - compliant

- [x] **Public API Documentation**: Plan ensures public hook has JSDoc documentation
  - Custom hook `useContactForm` will have JSDoc explaining purpose and behavior
  - Internal validation functions documented when complex
  - Violation justification: N/A - compliant

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then)
  - Test names use descriptive strings explaining scenarios
  - Violation justification: N/A - compliant

### Backend Architecture & Quality Standards (if `/server` affected)

- [ ] **Backend Technology Stack**: N/A (backend submission out of scope)
- [ ] **Backend Code Quality**: N/A (backend submission out of scope)
- [ ] **Backend Dependency Management**: N/A (backend submission out of scope)
- [ ] **Backend Directory Structure**: N/A (backend submission out of scope)
- [ ] **Backend TDD Workflow**: N/A (backend submission out of scope)
- [ ] **Backend Testing Strategy**: N/A (backend submission out of scope)

## Project Structure

### Documentation (this feature)

```text
specs/041-web-owners-details/
├── spec.md              # Feature specification
├── plan.md              # This file
├── research.md          # Phase 0 output (minimal - reusing patterns)
├── data-model.md        # Phase 1 output (contact details model)
├── quickstart.md        # Phase 1 output (dev setup guide)
└── contracts/           # Phase 1 output (minimal - no new API contracts)
```

### Source Code (repository root)

```text
webApp/src/
├── models/
│   └── ReportMissingPetFlow.ts               # EXTEND: Add phone, email, reward
├── hooks/
│   ├── use-contact-form.ts                   # NEW: Contact form validation hook
│   └── __tests__/
│       └── use-contact-form.test.ts          # NEW: Hook unit tests
├── components/
│   └── ReportMissingPet/
│       ├── ContactScreen.tsx                 # UPDATE: Replace debug view with real form
│       ├── SummaryScreen.tsx                 # NEW: Debug view showing flow state
│       ├── ReportMissingPetLayout.tsx        # REUSE: Existing layout component
│       ├── ReportMissingPetLayout.module.css # REUSE: Existing styles
│       ├── Header.tsx                        # REUSE: Existing header with back/progress
│       └── __tests__/
│           ├── ContactScreen.test.tsx        # NEW: Component tests
│           └── SummaryScreen.test.tsx        # NEW: Component tests
├── routes/
│   └── report-missing-pet-routes.tsx         # UPDATE: Add summary route
└── contexts/
    └── ReportMissingPetFlowContext.tsx       # REUSE: Existing flow context
```

**Structure Decision**: Standard web application structure with React components. Reusing existing `ReportMissingPetLayout`, styles from `ReportMissingPetLayout.module.css`, and `ReportMissingPetFlowContext` established in previous flow steps (specs 034, 037, 039). New hook follows kebab-case naming convention for non-component files.

## Complexity Tracking

> No violations requiring justification

## Phase 0: Research & Pattern Analysis

### Existing Patterns to Reuse

1. **Layout Component** (`ReportMissingPetLayout.tsx`):
   - Provides consistent header with back button, title, progress badge
   - Wraps content in styled card with responsive design
   - Already used in Steps 1-3

2. **Shared Styles** (`ReportMissingPetLayout.module.css`):
   - `.input` - Text input styling with focus states
   - `.label` - Form label typography
   - `.primaryButton` - Blue Continue button
   - `.inputGroup` - Vertical form field layout
   - `.heading`, `.description` - Typography styles
   - All styles match Figma design system

3. **Flow Context** (`ReportMissingPetFlowContext.tsx`):
   - Manages flow state with `updateFlowState`, `clearFlowState`
   - Persists data across navigation within active session
   - Browser refresh clears state (specs 034, 037, 039 pattern)

4. **Validation Pattern** (from `use-details-form.ts`):
   - Validation only on Continue click (no blur validation)
   - Always-enabled Continue button
   - Validation check on Continue click
   - Toast notifications for errors
   - Returns boolean from `handleSubmit` for navigation decision

5. **Navigation Pattern** (from `DetailsScreen.tsx`):
   - `useNavigate` for programmatic navigation
   - `useBrowserBackHandler` for browser back button (cancels flow)
   - In-app back arrow navigates to previous step
   - Redirect to step 1 if `flowState.currentStep === FlowStep.Empty`

### Research Decisions

**Decision 1**: Reuse existing `ReportMissingPetLayout` and styles  
**Rationale**: Maintains visual consistency with Steps 1-3, reduces code duplication, already matches Figma design system  
**Alternatives considered**: Create new layout components (rejected - unnecessary duplication)

**Decision 2**: Create custom hook `use-contact-form` following existing patterns  
**Rationale**: Consistent with `use-details-form`, `use-photo-upload`, `use-microchip-formatter` pattern. Separates validation logic from UI.  
**Alternatives considered**: Inline validation in component (rejected - harder to test)

**Decision 3**: Use kebab-case for hook filename  
**Rationale**: Follows project convention for non-component files (all existing hooks use kebab-case)  
**Alternatives considered**: PascalCase (rejected - breaks convention)

**Decision 4**: Validate "at least one contact method + all non-empty must be valid"  
**Rationale**: Per spec clarification #5 - stricter validation prevents partial invalid data  
**Alternatives considered**: Allow invalid fields if one valid exists (rejected - user selected this option)

**Decision 5**: No aria attributes, semantic HTML only  
**Rationale**: Per spec clarification #6 - simplified accessibility approach  
**Alternatives considered**: Full aria attributes (rejected - explicitly excluded)

**Decision 6**: Summary screen as debug view (like current ContactScreen)  
**Rationale**: Per spec clarification #7 - provides visibility into collected data, defers polished UI  
**Alternatives considered**: Polished summary UI (rejected - marked as future iteration)

## Phase 1: Design & Contracts

### Data Model

**Extended Flow State** (add to `ReportMissingPetFlow.ts`):

```typescript
export interface ReportMissingPetFlowState {
  // ... existing fields (microchipNumber, photo, lastSeenDate, etc.)
  
  // NEW: Contact details fields
  phone: string;  // Optional but must be valid if non-empty (must contain at least one digit)
  email: string;  // Optional but must be valid if non-empty (RFC 5322 basic)
  reward: string; // Optional, no validation, no length limit
}
```

**Initial values** (add to `initialFlowState`):

```typescript
export const initialFlowState: ReportMissingPetFlowState = {
  // ... existing initial values
  phone: '',
  email: '',
  reward: '',
};
```

**Validation Rules** (aligned with backend `/server/src/lib/validators.ts`):

1. **Phone**: 
   - Regex: `/\d/` (must contain at least one digit)
   - Error message: "Enter a valid phone number"
   - Optional but must be valid if non-empty

2. **Email**:
   - Regex: `/^[^\s@]+@[^\s@]+\.[^\s@]+$/` (matches backend validator)
   - Error message: "Enter a valid email address"
   - Optional but must be valid if non-empty

3. **At least one contact**:
   - Must have valid phone OR valid email (or both)
   - Error message: "Please provide at least one contact method (phone or email)"

4. **All non-empty must be valid**:
   - If user enters both phone and email, both must be valid
   - If one is invalid, user must fix or clear it before proceeding

### Component Design

**ContactScreen.tsx** (replace existing debug view):

```typescript
export function ContactScreen() {
  const navigate = useNavigate();
  const { 
    phone, 
    email, 
    reward,
    phoneError,
    emailError,
    isValid,
    handlePhoneChange,
    handleEmailChange,
    handleRewardChange,
    handleSubmit
  } = useContactForm();
  
  const handleBack = () => navigate(ReportMissingPetRoutes.details);
  
  const handleContinue = () => {
    if (handleSubmit()) {
      navigate(ReportMissingPetRoutes.summary);
    }
  };
  
  useBrowserBackHandler(() => {
    // Clear flow and return to pet list
    clearFlowState();
    navigate('/');
  });
  
  return (
    <ReportMissingPetLayout title="Owner's details" progress="4/4" onBack={handleBack}>
      <h2 className={styles.heading}>Your contact info</h2>
      <p className={styles.description}>
        Add your contact information and potential reward.
      </p>
      
      <div className={styles.inputGroup}>
        <label htmlFor="phone" className={styles.label}>Phone number</label>
        <input
          id="phone"
          type="tel"
          className={styles.input}
          placeholder="Enter phone number..."
          value={phone}
          onChange={handlePhoneChange}
          data-testid="contact.phoneNumber.input"
        />
        {phoneError && <span className={styles.error}>{phoneError}</span>}
      </div>
      
      <div className={styles.inputGroup}>
        <label htmlFor="email" className={styles.label}>Email</label>
        <input
          id="email"
          type="email"
          className={styles.input}
          placeholder="username@example.com"
          value={email}
          onChange={handleEmailChange}
          data-testid="contact.email.input"
        />
        {emailError && <span className={styles.error}>{emailError}</span>}
      </div>
      
      <div className={styles.inputGroup}>
        <label htmlFor="reward" className={styles.label}>
          Reward for the finder (optional)
        </label>
        <input
          id="reward"
          type="text"
          className={styles.input}
          placeholder="Enter amount..."
          value={reward}
          onChange={handleRewardChange}
          data-testid="contact.reward.input"
        />
      </div>
      
      <button
        onClick={handleContinue}
        className={styles.primaryButton}
        data-testid="contact.continue.button"
      >
        Continue
      </button>
    </ReportMissingPetLayout>
  );
}
```

**SummaryScreen.tsx** (new debug view):

```typescript
export function SummaryScreen() {
  const { flowState, clearFlowState } = useReportMissingPetFlow();
  const navigate = useNavigate();
  
  const handleBack = () => navigate(ReportMissingPetRoutes.contact);
  
  const handleComplete = () => {
    clearFlowState();
    navigate('/');
  };
  
  return (
    <ReportMissingPetLayout title="Summary" progress="4/4" onBack={handleBack}>
      <h2 className={styles.heading}>Flow State Summary</h2>
      
      <div style={{ /* debug view styles */ }}>
        <h3>Collected Data:</h3>
        
        <div><strong>Step 1 - Microchip:</strong> {flowState.microchipNumber || '(empty)'}</div>
        
        <div><strong>Step 2 - Photo:</strong> 
          {flowState.photo ? `${flowState.photo.filename} (${formatFileSize(flowState.photo.size)})` : '(no photo)'}
        </div>
        
        <div><strong>Step 3 - Last Seen:</strong> {flowState.lastSeenDate || '(empty)'}</div>
        <div><strong>Step 3 - Species:</strong> {flowState.species || '(empty)'}</div>
        <div><strong>Step 3 - Breed:</strong> {flowState.breed || '(empty)'}</div>
        <div><strong>Step 3 - Sex:</strong> {flowState.sex || '(empty)'}</div>
        <div><strong>Step 3 - Age:</strong> {flowState.age !== null ? flowState.age : '(empty)'}</div>
        <div><strong>Step 3 - Description:</strong> {flowState.description || '(empty)'}</div>
        
        <div><strong>Step 4 - Phone:</strong> {flowState.phone || '(empty)'}</div>
        <div><strong>Step 4 - Email:</strong> {flowState.email || '(empty)'}</div>
        <div><strong>Step 4 - Reward:</strong> {flowState.reward || '(empty)'}</div>
      </div>
      
      <div style={{ display: 'flex', gap: '12px' }}>
        <button onClick={handleBack} className={styles.secondaryButton}>
          Back to Contact
        </button>
        <button onClick={handleComplete} className={styles.primaryButton}>
          Complete
        </button>
      </div>
    </ReportMissingPetLayout>
  );
}
```

**use-contact-form.ts** (new custom hook):

```typescript
/**
 * Custom hook for managing contact form state and validation in Step 4/4.
 * Validates phone OR email (at least one required), all non-empty must be valid.
 * Integrates with ReportMissingPetFlowContext for session persistence.
 */
export function useContactForm() {
  const { flowState, updateFlowState } = useReportMissingPetFlow();
  
  const [phone, setPhone] = useState(flowState.phone);
  const [email, setEmail] = useState(flowState.email);
  const [reward, setReward] = useState(flowState.reward);
  const [phoneError, setPhoneError] = useState('');
  const [emailError, setEmailError] = useState('');
  
  // Validation aligned with backend /server/src/lib/validators.ts
  const validatePhone = (value: string): boolean => {
    if (value.trim() === '') return true; // Empty is valid (optional field)
    const isValid = /\d/.test(value); // Must contain at least one digit (matches backend)
    if (!isValid) {
      setPhoneError('Enter a valid phone number');
      return false;
    }
    setPhoneError('');
    return true;
  };
  
  const validateEmail = (value: string): boolean => {
    if (value.trim() === '') return true; // Empty is valid (optional field)
    const isValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value.trim()); // Matches backend
    if (!isValid) {
      setEmailError('Enter a valid email address');
      return false;
    }
    setEmailError('');
    return true;
  };
  
  const hasAtLeastOneContact = (): boolean => {
    const phoneValid = phone.trim() !== '' && validatePhone(phone);
    const emailValid = email.trim() !== '' && validateEmail(email);
    return phoneValid || emailValid;
  };
  
  const handlePhoneChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setPhone(value);
  };
  
  const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setEmail(value);
  };
  
  const handleRewardChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setReward(e.target.value);
  };
  
  const handleSubmit = (): boolean => {
    const phoneValid = validatePhone(phone);
    const emailValid = validateEmail(email);
    
    // All non-empty must be valid AND at least one must exist
    const allFieldsValid = phoneValid && emailValid;
    const hasContact = hasAtLeastOneContact();
    
    if (allFieldsValid && hasContact) {
      updateFlowState({
        phone: phone.trim(),
        email: email.trim(),
        reward: reward.trim(),
        currentStep: FlowStep.Completed
      });
      return true;
    }
    
    return false;
  };
  
  return {
    phone,
    email,
    reward,
    phoneError,
    emailError,
    isValid: hasAtLeastOneContact(),
    handlePhoneChange,
    handleEmailChange,
    handleRewardChange,
    handleSubmit
  };
}
```

### Routing Updates

**report-missing-pet-routes.tsx** (add summary route):

```typescript
export const ReportMissingPetRoutes = {
  microchip: '/report-missing-pet/microchip',
  photo: '/report-missing-pet/photo',
  details: '/report-missing-pet/details',
  contact: '/report-missing-pet/contact',
  summary: '/report-missing-pet/summary', // NEW
};

// In router configuration:
<Route path="/report-missing-pet/summary" element={<SummaryScreen />} />
```

### Test Coverage Strategy

**Hook Tests** (`use-contact-form.test.ts`):

1. Phone validation:
   - Must contain at least one digit (matches backend `/server/src/lib/validators.ts`)
   - Accept any format as long as it has a digit

2. Email validation:
   - Valid RFC 5322 basic format
   - Reject invalid formats (missing @, missing domain, etc.)

3. At least one contact required:
   - Block submission with no contacts
   - Allow phone only
   - Allow email only
   - Allow both

4. All non-empty must be valid:
   - Block submission with valid phone + invalid email
   - Block submission with invalid phone + valid email
   - Allow submission with valid phone + empty email
   - Allow submission with empty phone + valid email

5. Reward field: No validation needed

6. Session integration:
   - Save to flow state on successful submit
   - Update currentStep to Completed

**Component Tests** (`ContactScreen.test.tsx`):

1. Renders form with all fields
2. Shows validation errors only on Continue click (consistent with specs 034, 037, 039)
3. Continue always enabled
4. Blocks navigation when invalid
5. Navigates to summary when valid
6. Back button navigates to details step
7. Browser back clears flow and returns to pet list

**Component Tests** (`SummaryScreen.test.tsx`):

1. Displays all collected flow state data
2. Back button navigates to contact step
3. Complete button clears flow and returns to pet list

## Contracts

No new backend API contracts required. This implementation focuses on data collection and session persistence only. Backend submission will be handled in a separate integration specification.

## Next Steps

After `/speckit.plan` completion:

1. Run `/speckit.tasks` to break this plan into actionable tasks
2. Implement `use-contact-form` hook with unit tests (TDD approach)
3. Update `ContactScreen` component with real form
4. Create `SummaryScreen` component
5. Add component tests
6. Verify test coverage ≥ 80%
7. Manual testing across target browsers
