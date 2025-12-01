# Research: Web Microchip Number Screen

**Feature**: 034-web-chip-number-screen  
**Date**: 2025-12-01  
**Phase**: 0 (Research & Technology Decisions)

## Overview

This document consolidates research findings for implementing the web microchip number screen (step 1 of 4 in the "report missing pet" flow). Research covers React Router navigation patterns, input formatting approaches, browser history management, React Context for state management, and testing strategies.

## Research Tasks

### 1. React Router Multi-Step Flow Pattern

**Decision**: Use nested routes with guard logic for step access control

**Rationale**:
- React Router v6 supports nested routes declaratively via `<Routes>` and `<Outlet>`
- Each step gets its own route (e.g., `/report-missing/step1`, `/report-missing/step2`)
- Route guards can check flow state and redirect to step 1 if accessing later steps without context
- Browser back/forward buttons work naturally with URL-based navigation
- URL sharing and bookmarking work as expected (with redirect to step 1 if no state)

**Alternatives Considered**:
- **Conditional rendering without URL changes**: Simpler but breaks browser back button, no URL bookmarking, harder to debug
- **Modal overlay**: Would prevent URL-based navigation entirely, violates clarification decision to use React Router
- **Hash routing**: Not recommended for modern SPAs, less SEO-friendly

**Implementation Approach**:
```typescript
// Route structure
<Route path="/report-missing" element={<ReportMissingPetFlowProvider />}>
  <Route path="microchip" element={<MicrochipNumberScreen />} />
  <Route path="photo" element={<PhotoProtected />} />
  <Route path="details" element={<DetailsProtected />} />
  <Route path="contact" element={<ContactProtected />} />
  <Route index element={<Navigate to="microchip" replace />} />
</Route>

// Protected route component checks flow state
function PhotoProtected() {
  const { flowState } = useReportMissingPetFlow();
  if (flowState.currentStep === FlowStep.Microchip) {
    return <Navigate to="/report-missing/microchip" replace />;
  }
  return <PhotoComponent />;
}
```

**Key References**:
- React Router v6 documentation: Nested routes and protected routes patterns
- Common pattern for multi-step forms in React applications

---

### 2. Input Formatting Pattern for Microchip Numbers

**Decision**: Custom React hook (`useMicrochipFormatter`) with controlled input + formatting on change events

**Rationale**:
- Controlled input pattern is React best practice for form inputs
- Formatting logic extracted to reusable hook for testability
- Pure formatting utility functions (`formatMicrochipNumber`, `stripNonDigits`) are easily unit-tested
- Hook handles cursor position edge cases automatically via React's state management
- Performance: Formatting function runs on every keystroke but is O(n) where n=15 (trivial cost)

**Alternatives Considered**:
- **Input mask library (e.g., react-input-mask, react-number-format)**: Adds dependency, increases bundle size, may not handle paste sanitization as specified
- **Browser native input pattern attribute**: Cannot handle paste sanitization or dynamic formatting as user types
- **Uncontrolled input with ref**: Less idiomatic React, harder to test, loses React's declarative benefits

**Implementation Approach**:
```typescript
// use-microchip-formatter.ts
export function useMicrochipFormatter() {
  const [value, setValue] = useState('');
  const [formattedValue, setFormattedValue] = useState('');

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const input = e.target.value;
    // Strip non-numeric and limit to 15 digits
    const digitsOnly = input.replace(/\D/g, '').slice(0, 15);
    setValue(digitsOnly);
    // Format with hyphens (00000-00000-00000)
    setFormattedValue(formatMicrochipNumber(digitsOnly));
  };

  const handlePaste = (e: React.ClipboardEvent<HTMLInputElement>) => {
    e.preventDefault();
    const pastedText = e.clipboardData.getData('text');
    // Strip non-numeric, limit to 15, format
    const digitsOnly = pastedText.replace(/\D/g, '').slice(0, 15);
    setValue(digitsOnly);
    setFormattedValue(formatMicrochipNumber(digitsOnly));
  };

  return { value, formattedValue, handleChange, handlePaste };
}

// microchip-formatter.ts (pure utility)
export function formatMicrochipNumber(digits: string): string {
  if (digits.length <= 5) return digits;
  if (digits.length <= 10) return `${digits.slice(0, 5)}-${digits.slice(5)}`;
  return `${digits.slice(0, 5)}-${digits.slice(5, 10)}-${digits.slice(10)}`;
}
```

**Performance Considerations**:
- Formatting function O(n) complexity (n=15 max) = negligible
- No debouncing needed - formatting is synchronous and fast
- React's reconciliation handles updates efficiently

**Key References**:
- React documentation: Controlled components
- Common input formatting patterns in React applications

---

### 3. Browser History Management & Back Button Handling

**Decision**: Use React Router's navigation with `window.popstate` event listener for back button detection

**Rationale**:
- React Router automatically manages browser history via `pushState`/`replaceState`
- `popstate` event fires when user clicks browser back/forward buttons
- Can differentiate between programmatic navigation and browser back button
- Specification requires browser back button to behave same as in-app back arrow (cancel flow, return to pet list)

**Alternatives Considered**:
- **Prevent browser back with `window.history.pushState`**: Poor UX, violates browser conventions, not recommended
- **Modal confirmation on back**: Adds friction, specification doesn't require confirmation
- **Ignore browser back button**: Violates specification requirement

**Implementation Approach**:
```typescript
// useBrowserBackHandler.ts
export function useBrowserBackHandler(onBack: () => void) {
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const handlePopState = () => {
      // Browser back button pressed
      // Cancel flow and return to pet list
      onBack();
      navigate('/pets', { replace: true });
    };

    window.addEventListener('popstate', handlePopState);
    return () => window.removeEventListener('popstate', handlePopState);
  }, [onBack, navigate]);
}
```

**Edge Cases Handled**:
- Browser back from step 1 → cancel flow, go to pet list
- Browser back from step 2 → go to step 1 (within flow)
- Browser refresh → clear flow state, redirect to pet list (handled by route guard)

**Key References**:
- MDN Web Docs: History API (`pushState`, `popstate` event)
- React Router documentation: Navigation and history management

---

### 4. React Context for Flow State Management

**Decision**: React Context API with custom provider and consumer hook

**Rationale**:
- React Context is idiomatic for sharing state across component tree without prop drilling
- Spec requires flow state to persist across steps (step 1 → step 2 → back to step 1)
- No persistence to localStorage/sessionStorage (per clarification)
- Context wraps the entire flow route, cleared when flow exits
- Custom hook (`useReportMissingPetFlow`) provides type-safe access to context

**Alternatives Considered**:
- **Redux/Zustand**: Overkill for single flow state, adds dependency, increases complexity
- **URL query parameters**: Exposes sensitive data in URL, poor UX for complex state
- **localStorage**: Violates specification (no persistence across refresh)
- **Prop drilling**: Becomes unmaintainable with 4 steps, violates component independence

**Implementation Approach**:
```typescript
// ReportMissingPetFlowContext.tsx
enum FlowStep {
  Microchip = 'microchip',
  Photo = 'photo',
  Details = 'details',
  Contact = 'contact',
  Completed = 'completed',
}

interface FlowState {
  currentStep: FlowStep;
  microchipNumber: string;  // Digits only (no hyphens)
  // Future steps will add more fields
}

const ReportMissingPetFlowContext = React.createContext<{
  flowState: FlowState;
  updateFlowState: (updates: Partial<FlowState>) => void;
  clearFlowState: () => void;
} | null>(null);

export function ReportMissingPetFlowProvider({ children }: { children: React.ReactNode }) {
  const [flowState, setFlowState] = useState<FlowState>({
    currentStep: FlowStep.Microchip,
    microchipNumber: '',
  });

  const updateFlowState = (updates: Partial<FlowState>) => {
    setFlowState(prev => ({ ...prev, ...updates }));
  };

  const clearFlowState = () => {
    setFlowState({ currentStep: FlowStep.Microchip, microchipNumber: '' });
  };

  return (
    <ReportMissingPetFlowContext.Provider value={{ flowState, updateFlowState, clearFlowState }}>
      {children}
    </ReportMissingPetFlowContext.Provider>
  );
}

// Custom hook for type-safe access
export function useReportMissingPetFlow() {
  const context = useContext(ReportMissingPetFlowContext);
  if (!context) {
    throw new Error('useReportMissingPetFlow must be used within ReportMissingPetFlowProvider');
  }
  return context;
}
```

**Key References**:
- React documentation: Context API
- React patterns: Custom Context hooks for type safety

---

### 5. Testing Strategy

**Decision**: Layered testing approach with unit tests, component tests, and E2E tests

**Rationale**:
- Constitution requires 80% coverage
- Unit tests for pure functions (formatting utility)
- Component tests for React components (React Testing Library)
- E2E tests for user scenarios (Selenium + Cucumber)
- Layered approach ensures comprehensive coverage at different abstraction levels

**Test Breakdown**:

1. **Unit Tests (Vitest)**:
   - `microchip-formatter.ts`: Pure formatting functions
   - `formatMicrochipNumber()` - test various input lengths
   - `stripNonDigits()` - test alphanumeric strings, special chars
   - Coverage target: 100% (simple pure functions)

2. **Hook Tests (Vitest + @testing-library/react)**:
   - `use-microchip-formatter.test.ts`: Test hook behavior
   - Test typing digits, paste with non-numeric chars, max length enforcement
   - Test state updates and formatted value correctness
   - Coverage target: 100% (all branches)

3. **Component Tests (Vitest + React Testing Library)**:
   - `MicrochipNumberContent.test.tsx`: Presentational component
   - Test rendering with various state values
   - Test callback invocations (onContinue, onBack)
   - `Step1MicrochipNumber.test.tsx`: Integration with hooks and context
   - Test flow state updates, navigation, browser back handling
   - Coverage target: 80%+ (per constitution)

4. **E2E Tests (Selenium + Cucumber)**:
   - `/e2e-tests/src/test/resources/features/web/report-missing-pet-step1.feature`
   - 4 scenarios (one per user story from spec)
   - Page Object Model: `ReportMissingPetStep1Page.java`
   - Test identifiers: All interactive elements have `data-testid` attributes
   - Coverage: All user stories P1-P3

**Testing Tools & Versions**:
- Vitest: Already configured in webApp (existing project setup)
- React Testing Library: Already configured
- Selenium WebDriver: Version specified in `/e2e-tests/pom.xml`
- Cucumber: Version specified in `/e2e-tests/pom.xml`

**Key References**:
- React Testing Library documentation: Best practices for testing React components
- Vitest documentation: Testing hooks with renderHook
- Selenium documentation: Page Object Model pattern

---

## Summary of Decisions

| Decision Area | Choice | Key Rationale |
|---------------|--------|---------------|
| **Navigation** | React Router v6 with nested routes | URL-based navigation, browser back button support, route guards for access control |
| **Input Formatting** | Custom hook + controlled input | Testable, reusable, handles paste sanitization, no additional dependencies |
| **Browser Back** | `popstate` event listener | Detects browser back button, triggers flow cancellation per spec |
| **State Management** | React Context API | Idiomatic React, no persistence (per spec), type-safe with custom hook |
| **Testing** | Vitest + RTL + Selenium/Cucumber | Layered approach (unit/component/E2E), achieves 80%+ coverage requirement |

---

## Risks & Mitigations

| Risk | Mitigation |
|------|------------|
| **Browser refresh clears flow state** | Expected behavior per clarification - document in user guide if needed |
| **URL bookmarking of step 2-4** | Route guards redirect to step 1 (per clarification) |
| **Input formatting performance** | Formatting is O(n) with n=15 (trivial cost), no debouncing needed |
| **React Context re-renders** | Context value object is stable (useState), only updates on flow state changes |
| **E2E test flakiness** | Use explicit waits, Page Object Model, stable test identifiers (`data-testid`) |

---

## Implementation Dependencies

**No new npm dependencies required**:
- React Router: Already in project
- Vitest: Already in project
- React Testing Library: Already in project
- TypeScript: Already in project

**E2E test dependencies**:
- Selenium WebDriver: Already in `/e2e-tests/pom.xml`
- Cucumber: Already in `/e2e-tests/pom.xml`

---

## Next Steps (Phase 1)

1. Generate data model (`data-model.md`) for flow state structure
2. Generate contracts (`contracts/`) for TypeScript interfaces
3. Generate quickstart guide (`quickstart.md`) for local development and testing
4. Update agent context with new routes and components

---

**Research completed**: 2025-12-01  
**Ready for**: Phase 1 (Design & Contracts)

