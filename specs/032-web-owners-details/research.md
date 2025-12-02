# Research & Pattern Analysis: Web Owner's Details Screen

**Feature**: 032-web-owners-details  
**Date**: 2025-12-02

## Executive Summary

This feature reuses established patterns from Steps 1-3 (specs 034, 037, 039) of the Missing Pet flow. Minimal research required as all technical decisions leverage existing implementations. Key clarifications from spec ensure consistency with prior screens (validation on Continue click, always-enabled Continue, browser refresh clears state, in-app back preserves state).

## Reusable Patterns

### 1. Layout & Styling

**Pattern**: `ReportMissingPetLayout` component with shared CSS module  
**Source**: `/webApp/src/components/ReportMissingPet/ReportMissingPetLayout.tsx`

**Reusable Elements**:
- `pageContainer` - Full-height centered layout with gray background
- `contentCard` - White card with border (680px width, responsive)
- `heading` - 42px title typography (21272a color)
- `description` - 16px subtitle (545f71 color)
- `inputGroup` - Vertical form field layout with 8px gap
- `label` - Form label typography (364153 color)
- `input` - Text input with border, focus states, placeholder
- `primaryButton` - Blue Continue button (155dfc color)

**Decision**: Reuse all existing styles without modifications. Figma design node 315-15943 matches existing design system.

---

### 2. Flow State Management

**Pattern**: `ReportMissingPetFlowContext` with React Context  
**Source**: `/webApp/src/contexts/ReportMissingPetFlowContext.tsx`

**API**:
```typescript
const { flowState, updateFlowState, clearFlowState } = useReportMissingPetFlow();

// Usage:
updateFlowState({ phone: '+48123456789', email: 'user@example.com' });
```

**Behavior** (from specs 034, 037, 039):
- Browser refresh clears all flow state (returns to pet list)
- In-app navigation preserves flow state
- `clearFlowState()` used by browser back button handler

**Decision**: Extend existing `ReportMissingPetFlowState` interface with contact fields.

---

### 3. Validation Pattern

**Pattern**: Validate on Continue click only, always-enabled Continue  
**Source**: Consistent with specs 034, 037, 039

**Implementation**:
1. State tracks input values and error messages
2. No blur validation (to avoid interrupting user input)
3. Continue button always enabled (no disabled state)
4. `handleSubmit` validates all fields on Continue click, returns boolean
5. Component checks return value to decide navigation
6. Shows toast message + inline errors on validation failure

**Validation Rules** (spec clarification #1):
- Validate only on Continue click (no blur validation)
- Consistent with specs 034, 037, 039

**Decision**: Follow existing pattern exactly. Create `use-contact-form` hook mirroring `use-details-form` structure.

---

### 4. Navigation Pattern

**Pattern**: React Router with browser back handler  
**Source**: `/webApp/src/components/ReportMissingPet/DetailsScreen.tsx`

**Implementation**:
```typescript
const navigate = useNavigate();

// In-app back arrow
const handleBack = () => navigate(ReportMissingPetRoutes.details);

// Browser back button (cancels entire flow)
useBrowserBackHandler(() => {
  clearFlowState();
  navigate('/');
});

// Protect from direct URL access
useEffect(() => {
  if (flowState.currentStep === FlowStep.Empty) {
    navigate(ReportMissingPetRoutes.microchip, { replace: true });
  }
}, [flowState.currentStep, navigate]);
```

**Navigation Behavior** (spec clarification #4):
- In-app back arrow: Navigate to Step 3, preserve flow state
- Browser back button: Cancel flow, clear state, return to pet list
- Direct URL access: Redirect to Step 1 if no active flow

**Decision**: Implement identical navigation pattern. Step 4 back goes to Step 3 (DetailsScreen).

---

## Key Technical Decisions

### Decision 1: Validation Logic

**Requirement**: Phone OR email required, all non-empty must be valid

**Implementation** (aligned with backend `/server/src/lib/validators.ts`):
```typescript
// Validation rules:
validatePhone(value: string): boolean {
  if (value.trim() === '') return true; // Empty is valid
  return /\d/.test(value); // Must contain at least one digit (matches backend)
}

validateEmail(value: string): boolean {
  if (value.trim() === '') return true; // Empty is valid
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value.trim()); // Matches backend
}

hasAtLeastOneContact(): boolean {
  const phoneValid = phone !== '' && validatePhone(phone);
  const emailValid = email !== '' && validateEmail(email);
  return phoneValid || emailValid;
}

handleSubmit(): boolean {
  const phoneValid = validatePhone(phone);
  const emailValid = validateEmail(email);
  const allValid = phoneValid && emailValid; // All non-empty must be valid
  const hasContact = hasAtLeastOneContact(); // At least one must exist
  return allValid && hasContact;
}
```

**Rationale**: Per spec clarification #5 - stricter validation prevents partial invalid data (e.g., valid phone + malformed email). Validation logic aligned with backend `/server/src/lib/validators.ts`.

**Alternatives Considered**: Allow navigation with one valid + one invalid (rejected per user choice).

---

### Decision 2: No Accessibility Attributes

**Requirement**: No aria attributes, semantic HTML only

**Implementation**:
```typescript
<label htmlFor="phone" className={styles.label}>Phone number</label>
<input
  id="phone"
  type="tel"
  // No aria-describedby, aria-invalid, aria-disabled
/>
{phoneError && <span className={styles.error}>{phoneError}</span>}
```

**Rationale**: Per spec clarification #6 - simplified accessibility approach using semantic HTML and proper `<label>` elements with `for`/`id` linkage.

**Alternatives Considered**: Full aria support (rejected - explicitly excluded by user).

---

### Decision 3: Summary Screen Format

**Requirement**: Debug view showing all collected flow state

**Implementation**: Replicate current `ContactScreen` debug view format:
- Monospace font family
- Gray background container
- All flow state fields displayed with labels
- "Back" and "Complete" buttons

**Rationale**: Per spec clarification #7 - provides immediate visibility into collected data. Polished summary UI deferred to future iteration.

**Alternatives Considered**: Polished summary screen (rejected - out of scope).

---

### Decision 4: File Naming Convention

**Hook File**: `use-contact-form.ts` (kebab-case)

**Rationale**: All existing non-component files use kebab-case:
- `use-details-form.ts`
- `use-microchip-formatter.ts`
- `use-photo-upload.ts`
- `use-browser-back-handler.ts`

**Alternatives Considered**: PascalCase (rejected - breaks project convention).

---

## Technology Stack

All dependencies already available in project:

- **React 18.x**: Component framework
- **React Router 6.x**: Navigation and routing
- **TypeScript 5.x**: Type safety
- **Vitest**: Unit testing
- **React Testing Library**: Component testing

**No new dependencies required.**

---

## Testing Strategy

### Unit Tests (use-contact-form.test.ts)

**Coverage Target**: 80% line + branch

**Test Scenarios**:
1. Phone validation (valid, invalid, empty, sanitization)
2. Email validation (valid, invalid, empty)
3. At least one contact required (none, phone only, email only, both)
4. All non-empty must be valid (mixed valid/invalid scenarios)
5. Reward field (no validation)
6. Flow state integration (save on submit)

### Component Tests (ContactScreen.test.tsx)

**Coverage Target**: 80% line + branch

**Test Scenarios**:
1. Renders all form fields
2. Shows validation errors only on Continue click (consistent with specs 034, 037, 039)
3. Continue always enabled
4. Blocks navigation when invalid
5. Navigates to summary when valid
6. Back button behavior
7. Browser back handler
8. Redirect on direct URL access

### Component Tests (SummaryScreen.test.tsx)

**Test Scenarios**:
1. Displays all flow state
2. Back button to contact
3. Complete button clears flow

---

## Best Practices Applied

1. **Reuse Over Duplication**: Leverage existing components, styles, hooks, context
2. **Consistent Patterns**: Follow validation, navigation, error handling patterns from prior screens
3. **Test-Driven Development**: Write tests before implementation (Red-Green-Refactor)
4. **Semantic HTML**: Use proper `<label>`, `<input type="tel">`, `<input type="email">`
5. **Separation of Concerns**: Logic in hooks, presentation in components
6. **Type Safety**: TypeScript interfaces for all data structures

---

## Implementation Notes

### Error Styling

Add error styling to `ReportMissingPetLayout.module.css` (if not already present):

```css
.error {
  font-size: 14px;
  color: #fb2c36;
  margin-top: 4px;
}

.input.hasError {
  border-color: #fb2c36;
}
```

### Input Type Attributes

- Phone: `type="tel"` (enables numeric keyboard on mobile)
- Email: `type="email"` (enables email keyboard on mobile)
- Reward: `type="text"` (standard text input)

---

## Risks & Mitigations

**Risk**: Validation logic edge cases (e.g., phone with only letters, email with multiple @)  
**Mitigation**: Comprehensive unit tests covering edge cases, validation aligned with backend `/server/src/lib/validators.ts`

**Risk**: Browser back button clears flow unexpectedly  
**Mitigation**: Follow existing pattern from specs 034, 037, 039 - this behavior is intentional and consistent

**Risk**: Direct URL access bypasses flow  
**Mitigation**: Redirect to Step 1 if `flowState.currentStep === FlowStep.Empty` (existing pattern)

---

## References

- **Spec 034**: Web Chip Number screen (Step 1) - validation patterns, layout
- **Spec 037**: Web Animal Photo screen (Step 2) - flow integration
- **Spec 039**: Web Animal Description screen (Step 3) - form validation, navigation
- **Figma Design**: Node 315-15943 - visual specifications
- **Constitution**: Web architecture guidelines, testing standards

