# Implementation Tasks: Web Animal Description Screen

**Feature**: 039-web-animal-description-screen  
**Branch**: `039-web-animal-description-screen`  
**Spec**: [spec.md](./spec.md) | **Plan**: [plan.md](./plan.md)

## Overview

This document provides an actionable, dependency-ordered task list for implementing the Web Animal Description screen (Step 3/4 of Missing Pet flow). Tasks follow **Test-Driven Development (TDD)** principles: write tests first, implement logic, verify tests pass, ensure linting passes.

**Development Approach**: TDD (Red-Green-Refactor)
- ✅ **RED**: Write failing test
- ✅ **GREEN**: Write minimal code to pass test
- ✅ **REFACTOR**: Improve code quality
- ✅ **VERIFY**: Run tests (must pass) + lint (no issues)

## Task Statistics

- **Total Tasks**: 42
- **Setup Phase**: 4 tasks
- **Foundational Phase**: 6 tasks
- **User Story 1 (P1)**: 14 tasks
- **User Story 2 (P2)**: 10 tasks
- **Polish Phase**: 8 tasks
- **Parallelizable Tasks**: 18 marked with [P]

## Legend

- `- [ ]` - Task checkbox (mark when complete)
- `[TaskID]` - Sequential task identifier (T001, T002, etc.)
- `[P]` - Parallelizable (can run concurrently with other [P] tasks in same phase)
- `[USx]` - User Story label (US1, US2, etc.)
- File paths are absolute from workspace root

---

## Phase 1: Setup & Project Initialization

**Goal**: Initialize project structure, testing infrastructure, and development environment.

**Duration Estimate**: 1-2 hours

### Tasks

- [X] T001 Verify existing flow state context exists at `/webApp/src/contexts/ReportMissingPetFlowContext.tsx` (prerequisite check)
- [X] T002 Verify Animal types exist at `/webApp/src/types/animal.ts` with AnimalSpecies and AnimalSex enums (prerequisite check)
- [X] T003 Create component directory structure: `/webApp/src/components/AnimalDescriptionForm/`
- [X] T004 Create page component file: `/webApp/src/pages/ReportMissingPet/Step3_AnimalDescription.tsx`

---

## Phase 2: Foundational Layer

**Goal**: Build shared utilities, type definitions, and validation logic that all user stories depend on.

**Duration Estimate**: 3-4 hours

**Independent Test Criteria**: Validation functions work correctly in isolation (all edge cases covered).

### Tasks

#### Validation Utilities (TDD)

- [X] T005 **[P] TDD-RED**: Write test suite for form validation functions at `/webApp/src/utils/__tests__/form-validation.test.ts`
  - Test validateLastSeenDate (required, no future dates)
  - Test validateSpecies (required, valid enum)
  - Test validateBreed (required when species selected, cleared on species change)
  - Test validateSex (required, MALE or FEMALE only)
  - Test validateAge (optional, 0-40 range if provided)
  - Test validateDescription (optional, max 500 chars)
  - Expected: All tests FAIL (functions don't exist yet)

- [X] T006 **TDD-GREEN**: Implement validation functions at `/webApp/src/utils/form-validation.ts`
  - Implement validateLastSeenDate with future date check
  - Implement validateSpecies with enum validation
  - Implement validateBreed with conditional requirement
  - Implement validateSex with MALE/FEMALE check
  - Implement validateAge with range validation
  - Implement validateDescription with length check
  - Implement validateAllFields helper
  - Expected: All tests PASS

- [X] T007 **TDD-VERIFY**: Run validation tests and lint
  - Command: `npm test -- form-validation.test.ts`
  - Command: `npm run lint -- src/utils/form-validation.ts`
  - Expected: 100% tests pass, 0 lint errors

#### Display Label Mappings (TDD)

- [X] T008 **[P] TDD-RED**: Write test suite for display label utilities at `/webApp/src/utils/__tests__/display-labels.test.ts`
  - Test SPECIES_LABELS maps enums to capitalized strings
  - Test SEX_LABELS maps enums to capitalized strings
  - Expected: All tests FAIL

- [X] T009 **[P] TDD-GREEN**: Implement display label constants at `/webApp/src/utils/display-labels.ts`
  - Export SPECIES_LABELS: Record<AnimalSpecies, string>
  - Export SEX_LABELS: Record<AnimalSex, string>
  - Expected: All tests PASS

- [X] T010 **[P] TDD-VERIFY**: Run label tests and lint
  - Command: `npm test -- display-labels.test.ts`
  - Command: `npm run lint -- src/utils/display-labels.ts`
  - Expected: 100% tests pass, 0 lint errors

---

## Phase 3: User Story 1 - Provide Animal Context (Priority: P1)

**User Story**: Web users who have uploaded the microchip number and photo arrive at the Animal Description screen to enter descriptive data (date, species, breed, gender, optional age and description) so responders understand the case.

**Goal**: Implement core form functionality with all required fields, validation, and flow state integration.

**Independent Test Criteria**: 
- User can navigate to Step 3 from Step 2
- User can fill all required fields (date, species, breed, gender)
- User can submit form and advance to Step 4
- Data persists in flow state

**Duration Estimate**: 8-10 hours

### Tasks

#### Extend Flow State Context (TDD)

- [X] T011 **[US1] TDD-RED**: Write test for extended ReportMissingPetFlowState type at `/webApp/src/contexts/__tests__/ReportMissingPetFlowContext.test.tsx`
  - Test flow state includes Step 3 fields: lastSeenDate, species, breed, sex, age, description
  - Test updateFlowState updates Step 3 fields
  - Expected: Tests FAIL (types don't exist yet)

- [X] T012 **[US1] TDD-GREEN**: Extend ReportMissingPetFlowState interface at `/webApp/src/contexts/ReportMissingPetFlowContext.tsx`
  - Add Step 3 fields with correct types (per data-model.md)
  - Update initialState with Step 3 defaults
  - Expected: Tests PASS

- [X] T013 **[US1] TDD-VERIFY**: Run flow state tests and lint
  - Command: `npm test -- ReportMissingPetFlowContext.test.tsx`
  - Command: `npm run lint -- src/contexts/ReportMissingPetFlowContext.tsx`
  - Expected: 100% tests pass, 0 lint errors

#### Custom Form Hook (TDD)

- [X] T014 **[US1] TDD-RED**: Write test suite for useAnimalDescriptionForm hook at `/webApp/src/hooks/__tests__/useAnimalDescriptionForm.test.ts`
  - Test hook initializes with flow state or defaults
  - Test formData updates on field changes
  - Test breed field clears when species changes
  - Test handleSubmit validates all fields
  - Test handleSubmit saves to flow state on valid submission
  - Expected: All tests FAIL

- [X] T015 **[US1] TDD-GREEN**: Implement useAnimalDescriptionForm hook at `/webApp/src/hooks/useAnimalDescriptionForm.ts`
  - Initialize formData from flow state or defaults
  - Handle field updates with setFormData
  - Clear breed when species changes (useEffect)
  - Implement handleSubmit with validation
  - Convert form data to flow state types on submit
  - Expected: All tests PASS

- [X] T016 **[US1] TDD-VERIFY**: Run hook tests and lint
  - Command: `npm test -- useAnimalDescriptionForm.test.ts`
  - Command: `npm run lint -- src/hooks/useAnimalDescriptionForm.ts`
  - Expected: 100% tests pass, 0 lint errors

#### Species Dropdown Component (TDD)

- [X] T017 **[P] [US1] TDD-RED**: Write test suite for SpeciesDropdown at `/webApp/src/components/AnimalDescriptionForm/__tests__/SpeciesDropdown.test.tsx`
  - Test renders all 5 species options (Dog, Cat, Bird, Rabbit, Other)
  - Test displays capitalized labels
  - Test onChange callback fires with correct enum value
  - Test displays error message when provided
  - Test includes data-testid attribute
  - Expected: All tests FAIL

- [X] T018 **[P] [US1] TDD-GREEN**: Implement SpeciesDropdown component at `/webApp/src/components/AnimalDescriptionForm/SpeciesDropdown.tsx`
  - Render select with all AnimalSpecies options
  - Map enums to capitalized labels
  - Handle onChange with proper typing
  - Display error message conditionally
  - Add data-testid attribute
  - Expected: All tests PASS

- [X] T019 **[P] [US1] TDD-VERIFY**: Run SpeciesDropdown tests and lint
  - Command: `npm test -- SpeciesDropdown.test.tsx`
  - Command: `npm run lint -- src/components/AnimalDescriptionForm/SpeciesDropdown.tsx`
  - Expected: 100% tests pass, 0 lint errors

#### Gender Selector Component (TDD)

- [X] T020 **[P] [US1] TDD-RED**: Write test suite for GenderSelector at `/webApp/src/components/AnimalDescriptionForm/__tests__/gender-selector.test.tsx`
  - Test renders Male and Female options
  - Test displays capitalized labels
  - Test mutually exclusive selection
  - Test onChange callback fires with correct enum value
  - Test displays error message when provided
  - Test includes data-testid attribute
  - Expected: All tests FAIL

- [X] T021 **[P] [US1] TDD-GREEN**: Implement GenderSelector component at `/webApp/src/components/AnimalDescriptionForm/gender-selector.tsx`
  - Render two radio buttons or cards (Male, Female)
  - Handle mutually exclusive selection
  - Display capitalized labels
  - Handle onChange with proper typing
  - Display error message conditionally
  - Add data-testid attribute
  - Expected: All tests PASS

- [X] T022 **[P] [US1] TDD-VERIFY**: Run GenderSelector tests and lint
  - Command: `npm test -- gender-selector.test.tsx`
  - Command: `npm run lint -- src/components/AnimalDescriptionForm/gender-selector.tsx`
  - Expected: 100% tests pass, 0 lint errors

#### Character Counter Component (TDD)

- [X] T023 **[P] [US1] TDD-RED**: Write test suite for CharacterCounter at `/webApp/src/components/AnimalDescriptionForm/__tests__/character-counter.test.tsx`
  - Test displays current/max format (e.g., "250/500 characters")
  - Test applies "exceeded" class when over limit
  - Expected: All tests FAIL

- [X] T024 **[P] [US1] TDD-GREEN**: Implement CharacterCounter component at `/webApp/src/components/AnimalDescriptionForm/character-counter.tsx`
  - Display "{current}/{max} characters"
  - Apply exceeded styling when current > max
  - Expected: All tests PASS

- [X] T025 **[P] [US1] TDD-VERIFY**: Run CharacterCounter tests and lint
  - Command: `npm test -- character-counter.test.tsx`
  - Command: `npm run lint -- src/components/AnimalDescriptionForm/character-counter.tsx`
  - Expected: 100% tests pass, 0 lint errors

#### Main Form Component (TDD)

- [X] T026 **[US1] TDD-RED**: Write test suite for AnimalDescriptionForm at `/webApp/src/components/AnimalDescriptionForm/__tests__/animal-description-form.test.tsx`
  - Test renders all form fields (date, species, breed, sex, age, description, GPS button)
  - Test breed field disabled until species selected
  - Test date picker has max={today}
  - Test description textarea has maxLength={500}
  - Test GPS button is disabled
  - Test Continue button calls onSubmit
  - Test all fields have correct data-testid attributes
  - Expected: All tests FAIL

- [X] T027 **[US1] TDD-GREEN**: Implement AnimalDescriptionForm component at `/webApp/src/components/AnimalDescriptionForm/animal-description-form.tsx`
  - Render all form fields with correct types
  - Implement conditional breed field disabling
  - Add date picker with max={today}
  - Add description textarea with maxLength
  - Render disabled GPS button placeholder
  - Wire all onChange handlers
  - Add all data-testid attributes per spec
  - Integrate sub-components (SpeciesDropdown, GenderSelector, CharacterCounter)
  - Expected: All tests PASS

- [X] T028 **[US1] TDD-VERIFY**: Run AnimalDescriptionForm tests and lint
  - Command: `npm test -- animal-description-form.test.tsx`
  - Command: `npm run lint -- src/components/AnimalDescriptionForm/animal-description-form.tsx`
  - Expected: 100% tests pass, 0 lint errors

#### Page Component (TDD)

- [X] T029 **[US1] TDD-RED**: Write test suite for Step3_AnimalDescription page at `/webApp/src/pages/ReportMissingPet/__tests__/step3-animal-description.test.tsx`
  - Test renders header with back arrow, title, progress (3/4)
  - Test back arrow navigates to /report-missing/photo (Step 2)
  - Test successful submit navigates to /report-missing/contact (Step 4)
  - Test integrates useAnimalDescriptionForm hook
  - Test header has correct data-testid attributes
  - Expected: All tests FAIL

- [X] T030 **[US1] TDD-GREEN**: Implement Step3_AnimalDescription page at `/webApp/src/pages/ReportMissingPet/step3-animal-description.tsx`
  - Render header with back arrow, title, progress
  - Wire back arrow to navigate to Step 2
  - Wire Continue to navigate to Step 4 on valid submission
  - Integrate useAnimalDescriptionForm hook
  - Pass formData to AnimalDescriptionForm
  - Add all data-testid attributes per spec
  - Expected: All tests PASS

- [X] T031 **[US1] TDD-VERIFY**: Run page tests and lint
  - Command: `npm test -- step3-animal-description.test.tsx`
  - Command: `npm run lint -- src/pages/ReportMissingPet/step3-animal-description.tsx`
  - Expected: 100% tests pass, 0 lint errors

#### Route Integration

- [X] T032 **[US1]** Add Step 3 route to React Router configuration at main routing file (App.tsx or routes config)
  - Import Step3_AnimalDescription component
  - Add route: `/report-missing/description` → Step3_AnimalDescription
  - Updated PhotoScreen to navigate to /report-missing/description

- [X] T033 **[US1] TDD-VERIFY**: Run all tests for User Story 1 and verify coverage
  - Command: `npm test -- --coverage`
  - Expected: 80%+ coverage for all US1 files, all tests pass
  - Verify: User can navigate Step 2 → Step 3 → Step 4 with data persistence

---

## Phase 4: User Story 2 - Validation, Persistence & Safe Exits (Priority: P2)

**User Story**: Web users might navigate backward or step away; Step 3 must preserve entries, explain optional fields, and prevent advancing with incomplete required data.

**Goal**: Implement validation error display, navigation preservation, and edge case handling.

**Independent Test Criteria**:
- User sees toast + inline errors when submitting invalid form
- User can navigate back to Step 2 and return with data preserved
- Required field validation blocks navigation until corrected
- Optional fields work without blocking

**Duration Estimate**: 5-6 hours

### Tasks

#### Validation Error Display (TDD)

- [X] T034 **[P] [US2] TDD-RED**: Write test for toast notification component at `/webApp/src/components/__tests__/Toast.test.tsx`
  - Test renders toast message when visible
  - Test auto-dismisses after 5 seconds
  - Test displays error styling
  - Expected: All tests FAIL

- [X] T035 **[P] [US2] TDD-GREEN**: Implement Toast component at `/webApp/src/components/Toast.tsx` (or integrate with existing toast system)
  - Render toast with message prop
  - Auto-dismiss after 5 seconds (useEffect with setTimeout)
  - Apply error styling
  - Position fixed at bottom center
  - Expected: All tests PASS

- [X] T036 **[P] [US2] TDD-VERIFY**: Run toast tests and lint
  - Command: `npm test -- Toast.test.tsx`
  - Command: `npm run lint -- src/components/Toast.tsx`
  - Expected: 100% tests pass, 0 lint errors

- [X] T037 **[US2]** Integrate toast in Step3_AnimalDescription page
  - Add showToast state from useAnimalDescriptionForm
  - Render Toast component conditionally
  - Display "Please correct the errors below" message
  - Test: Submit invalid form → toast appears for 5 seconds

#### Inline Error Display (TDD)

- [X] T038 **[US2] TDD-RED**: Write test for inline error display in form fields
  - Test species dropdown shows error message below select
  - Test breed input shows error message below input
  - Test sex selector shows error message below radio group
  - Add to existing component test files
  - Expected: Tests FAIL

- [X] T039 **[US2] TDD-GREEN**: Update form components to display inline errors
  - Update AnimalDescriptionForm to pass validationErrors to child components
  - Update SpeciesDropdown to render error span below select
  - Update GenderSelector to render error span below options
  - Update all input fields to render error spans
  - Apply error styling (red text, margin-top)
  - Expected: All tests PASS

- [X] T040 **[US2] TDD-VERIFY**: Run updated component tests and lint
  - Command: `npm test -- AnimalDescriptionForm.test.tsx SpeciesDropdown.test.tsx GenderSelector.test.tsx`
  - Expected: All tests pass with error display coverage

#### Navigation Preservation (TDD)

- [X] T041 **[US2] TDD-RED**: Write test for navigation preservation
  - Test data persists when navigating Step 3 → Step 2 → Step 3
  - Test currentStep updates correctly on navigation
  - Test form re-populates with saved values
  - Add to Step3_AnimalDescription.test.tsx
  - Expected: Tests FAIL

- [X] T042 **[US2] TDD-GREEN**: Implement navigation preservation in useAnimalDescriptionForm hook
  - Ensure hook initializes from flowState on mount
  - Verify flow state not cleared on Step 3 → Step 2 navigation
  - Test back arrow uses in-app navigation (not browser back)
  - Expected: All tests PASS

- [X] T043 **[US2] TDD-VERIFY**: Run navigation tests and verify preservation
  - Command: `npm test -- Step3_AnimalDescription.test.tsx`
  - Manual test: Fill form → back → forward → verify data
  - Expected: All tests pass, data persists

#### Edge Case Handling

- [X] T044 **[P] [US2] TDD-RED**: Write tests for edge cases
  - Test future date blocked in date picker
  - Test species change clears breed field
  - Test description truncated at 500 characters
  - Test age accepts only integers 0-40
  - Add to existing component test files
  - Expected: Tests FAIL (edge cases not fully handled)

- [X] T045 **[P] [US2] TDD-GREEN**: Implement edge case handling
  - Add date picker max attribute (already in T027, verify works)
  - Implement breed clear on species change (useEffect in hook)
  - Add description maxLength and paste handler
  - Add age validation (integer, range check)
  - Expected: All tests PASS

- [X] T046 **[P] [US2] TDD-VERIFY**: Run edge case tests
  - Command: `npm test -- --grep "edge case"`
  - Expected: All edge case scenarios pass

#### Direct URL Access Guard

- [X] T047 **[US2]** Implement route guard for direct URL access
  - Add guard in routing configuration
  - Redirect to Step 1 if currentStep < 3 or flowState is empty
  - Test: Access /report-missing/description directly → redirects to /report-missing/microchip

- [X] T048 **[US2] TDD-VERIFY**: Run all tests for User Story 2
  - Command: `npm test -- --coverage`
  - Expected: 80%+ coverage maintained, all US2 tests pass
  - Verify: Validation errors display correctly, navigation preserves data

---

## Phase 5: Polish & Integration

**Goal**: Complete E2E tests, add styling, ensure cross-browser compatibility, and finalize documentation.

**Duration Estimate**: 4-5 hours

### Tasks

#### Styling

- [ ] T049 **[P]** Create component styles at `/webApp/src/styles/AnimalDescriptionForm.css`
  - Implement responsive design (mobile: 320px+, tablet: 768px+, desktop: 1024px+)
  - Style form fields (height: 41px, border-radius: 10px, spacing: 24px between fields)
  - Style textarea (height: 96px)
  - Style Continue button (primary blue: #155dfc)
  - Style toast notification
  - Style GPS button placeholder (disabled appearance)
  - Style error messages (red text)
  - Style character counter
  - Match Figma design node 315-15837

- [ ] T050 **[P]** Import styles in Step3_AnimalDescription component
  - Add CSS import
  - Verify responsive behavior across viewports

#### End-to-End Tests (Cucumber + Selenium)

- [ ] T051 Write Cucumber feature file at `/e2e-tests/src/test/resources/features/web/039-animal-description.feature`
  - @web tag for web platform
  - Scenario: Successfully fill animal description form (happy path)
  - Scenario: Show validation errors for missing required fields
  - Scenario: Navigate back to Step 2 preserving data
  - Scenario: Species change clears breed field
  - Background: Complete Steps 1 and 2 first

- [ ] T052 Implement Page Object at `/e2e-tests/src/test/java/.../pages/AnimalDescriptionPage.java`
  - Define WebElement fields with @FindBy(xpath) annotations
  - Use data-testid attributes from spec
  - Implement page methods: fillForm(), clickContinue(), clickBack(), getErrorMessage()

- [ ] T053 Implement Step Definitions at `/e2e-tests/src/test/java/.../steps-web/AnimalDescriptionSteps.java`
  - Implement Given/When/Then steps for all scenarios
  - Use AnimalDescriptionPage methods
  - Add assertions for validation errors, navigation, data persistence

- [ ] T054 Run E2E tests and verify scenarios
  - Command: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web and @039"`
  - Expected: All scenarios pass
  - View report: `/e2e-tests/target/cucumber-reports/web/index.html`

#### Cross-Browser Testing

- [ ] T055 **[P]** Test in Chrome, Firefox, Safari, Edge
  - Verify form rendering
  - Verify date picker behavior (max={today} support)
  - Verify species dropdown
  - Verify validation errors
  - Document any browser-specific issues

#### Final Verification

- [ ] T056 Run full test suite and lint
  - Command: `npm test -- --coverage`
  - Command: `npm run lint`
  - Expected: 80%+ coverage, all tests pass, 0 lint errors

- [ ] T057 Verify all acceptance scenarios from spec.md
  - User Story 1, Scenario 1: Header, progress, defaults ✓
  - User Story 1, Scenario 2: Fill fields, navigate to Step 4, data persists ✓
  - User Story 2, Scenario 1: Validation errors display ✓
  - User Story 2, Scenario 2: Back navigation preserves data ✓

- [ ] T058 Update implementation summary document
  - Create IMPLEMENTATION_SUMMARY.md in feature directory
  - Document completed user stories
  - Document test coverage achieved
  - Document known limitations (GPS button placeholder)
  - List follow-up tasks (update specs 034, 037 for navigation)

---

## Dependencies & Execution Order

### User Story Dependencies

```
Phase 1 (Setup) → Phase 2 (Foundational)
                        ↓
        ┌───────────────┴───────────────┐
        ↓                               ↓
   Phase 3 (US1)                  Phase 4 (US2)
   [Independent]                  [Depends on US1]
        ↓                               ↓
        └───────────────┬───────────────┘
                        ↓
                  Phase 5 (Polish)
```

**Execution Strategy**:
1. **Phase 1-2** must complete first (foundational)
2. **Phase 3 (US1)** can start immediately after Phase 2
3. **Phase 4 (US2)** depends on Phase 3 completion (validation builds on form)
4. **Phase 5** integrates all phases

### Parallel Execution Opportunities

**Within Phase 2 (Foundational)**:
- T008-T010 (Display labels) can run parallel to T005-T007 (Validation)

**Within Phase 3 (US1)**:
- T017-T019 (SpeciesDropdown) parallel with T020-T022 (GenderSelector)
- T020-T022 (GenderSelector) parallel with T023-T025 (CharacterCounter)

**Within Phase 4 (US2)**:
- T034-T036 (Toast) parallel with T044-T046 (Edge cases)

**Within Phase 5 (Polish)**:
- T049-T050 (Styling) parallel with T051-T054 (E2E tests)
- T055 (Browser testing) parallel with styling/E2E

---

## Implementation Strategy

### MVP Scope (Minimum Viable Product)

**Deliver User Story 1 (P1) First**:
- Phase 1: Setup ✓
- Phase 2: Foundational ✓
- Phase 3: User Story 1 (Core form functionality) ✓
- Verify: User can complete Step 3 and advance to Step 4

**Value**: Delivers core reporting flow immediately. Users can enter animal description data.

### Incremental Delivery

**Iteration 1** (MVP): Tasks T001-T033 (US1)
- Core form with all required fields
- Basic validation
- Flow state integration
- Navigate to Step 4

**Iteration 2**: Tasks T034-T048 (US2)
- Enhanced error display (toast + inline)
- Navigation preservation
- Edge case handling
- Production-ready validation UX

**Iteration 3**: Tasks T049-T058 (Polish)
- Styling and responsive design
- E2E test coverage
- Cross-browser testing
- Documentation

---

## Testing Strategy

### Unit Tests (Vitest + React Testing Library)

**Coverage Target**: 80%+ line and branch coverage

**Test Files**:
- `/webApp/src/utils/__tests__/form-validation.test.ts` (T005)
- `/webApp/src/utils/__tests__/display-labels.test.ts` (T008)
- `/webApp/src/contexts/__tests__/ReportMissingPetFlowContext.test.tsx` (T011)
- `/webApp/src/hooks/__tests__/useAnimalDescriptionForm.test.ts` (T014)
- `/webApp/src/components/AnimalDescriptionForm/__tests__/SpeciesDropdown.test.tsx` (T017)
- `/webApp/src/components/AnimalDescriptionForm/__tests__/GenderSelector.test.tsx` (T020)
- `/webApp/src/components/AnimalDescriptionForm/__tests__/CharacterCounter.test.tsx` (T023)
- `/webApp/src/components/AnimalDescriptionForm/__tests__/AnimalDescriptionForm.test.tsx` (T026)
- `/webApp/src/pages/ReportMissingPet/__tests__/Step3_AnimalDescription.test.tsx` (T029)
- `/webApp/src/components/__tests__/Toast.test.tsx` (T034)

**Run Command**: `npm test -- --coverage`

### E2E Tests (Selenium + Cucumber)

**Test File**: `/e2e-tests/src/test/resources/features/web/039-animal-description.feature` (T051)

**Scenarios**:
1. Happy path: Fill form → advance to Step 4
2. Validation errors: Missing fields → errors display
3. Navigation preservation: Back → forward → data persists
4. Species change: Change species → breed clears

**Run Command**: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web and @039"`

### TDD Verification Steps

**For Each Task**:
1. ✅ **RED**: Write failing test (verify test fails)
2. ✅ **GREEN**: Implement minimal code (verify test passes)
3. ✅ **REFACTOR**: Improve code quality (verify test still passes)
4. ✅ **LINT**: Run linter (verify 0 errors)

**Verification Commands**:
```bash
# Run specific test file
npm test -- <test-file-name>

# Run all tests with coverage
npm test -- --coverage

# Run linter on specific file
npm run lint -- <file-path>

# Run all linters
npm run lint
```

---

## Success Criteria

- [ ] All 42 tasks completed
- [ ] 80%+ unit test coverage achieved
- [ ] All E2E scenarios pass
- [ ] 0 linter errors
- [ ] User Story 1 (P1) acceptance scenarios verified ✓
- [ ] User Story 2 (P2) acceptance scenarios verified ✓
- [ ] Cross-browser compatibility verified (Chrome, Firefox, Safari, Edge)
- [ ] All data-testid attributes present per spec
- [ ] Form validates correctly (required fields, ranges, types)
- [ ] Navigation preserves data (Step 3 ↔ Step 2)
- [ ] Styling matches Figma design (responsive, accessible)

---

## Notes

**TDD Reminders**:
- ALWAYS write tests BEFORE implementation
- Run tests AFTER implementation to verify they pass
- NEVER skip the lint step
- Each task must be atomic (complete RED-GREEN-REFACTOR cycle)

**Navigation Behavior**:
- In-app back arrow: Navigate to Step 2 (preserve state)
- Browser back button: Cancel flow, return to pet list (clear state)
- This spec introduces multi-step navigation pattern

**Follow-up Work** (Out of Scope):
- Update specs 034 and 037 for consistent back arrow behavior
- Implement GPS location capture (separate specification)
- Backend API integration for form submission (separate specification)

---

**Tasks Document Generated**: December 2, 2025  
**Ready for Implementation**: ✅ Yes  
**Estimated Total Duration**: 18-22 hours  
**Recommended Approach**: Start with MVP (US1), then iterate

