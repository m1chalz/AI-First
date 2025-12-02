# Implementation Plan: Web Animal Description Screen

**Branch**: `039-web-animal-description-screen` | **Date**: December 2, 2025 | **Spec**: [spec.md](./spec.md)  
**Input**: Feature specification from `/specs/039-web-animal-description-screen/spec.md`

## Summary

Implement Step 3/4 of the web Missing Pet flow - an animal description form collecting required fields (date last seen, species, breed, gender) and optional fields (age, description), with a placeholder GPS button for future implementation. The form validates required fields on submission, persists data in React flow state, and integrates with existing web form patterns from specs 034 (chip number) and 037 (photo).

## Technical Context

**Language/Version**: TypeScript 5.x (ES2015+), React 18.x  
**Primary Dependencies**: React, React Router, existing Animal types from `/webApp/src/types/animal.ts`  
**Storage**: In-memory React Context/state (no localStorage/sessionStorage per spec)  
**Testing**: Vitest + React Testing Library (unit tests), Selenium + Cucumber (E2E tests)  
**Target Platform**: Modern web browsers (Chrome, Firefox, Safari, Edge) - no IE11  
**Project Type**: Web (React single-page application)  
**Performance Goals**: Form interaction < 3 seconds, species dropdown load < 500ms  
**Constraints**: 
- No backend integration (separate specification)
- GPS button non-functional (placeholder only)
- Field names MUST match existing Animal type (lastSeenDate, breed, species, sex, age, description)
- Multi-step navigation (back arrow to previous step, not close entire flow)
**Scale/Scope**: Single form screen, 7 input fields (4 required + 3 optional), part of 4-step flow

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Platform Architecture Compliance

- [x] **Platform Independence**: Web platform implements full stack independently
  - Web: Domain models (Animal type), services, state management in `/webApp`
  - NO shared compiled code between platforms
  - This feature is web-only, builds on existing type definitions
  - Violation justification: N/A - compliant

- [x] **Android MVI Architecture**: N/A - web-only feature
  - Violation justification: N/A - no Android components

- [x] **iOS MVVM-C Architecture**: N/A - web-only feature
  - Violation justification: N/A - no iOS components

- [x] **Interface-Based Design**: Web services follow interface pattern
  - Service interfaces in `/webApp/src/services/` (if needed)
  - This feature primarily uses form state management (no new services needed)
  - Existing Animal type interface already defined
  - Violation justification: N/A - compliant, reuses existing types

- [x] **Dependency Injection**: React Context for flow state management
  - Flow state managed via React Context (consistent with specs 034, 037)
  - DI setup in `/webApp/src/contexts/` or custom hooks
  - Violation justification: N/A - compliant with SHOULD use React Context

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests
  - Web: Tests in `/webApp/src/__tests__/`, run `npm test -- --coverage`
  - Coverage target: 80% line + branch coverage
  - Tests: form validation logic, state updates, field interactions
  - Violation justification: N/A - tests planned

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: Selenium + Cucumber tests in `/e2e-tests/src/test/resources/features/web/039-animal-description.feature`
  - Page Object Model in `/e2e-tests/src/test/java/.../pages/AnimalDescriptionPage.java`
  - Test identifiers using `data-testid` attributes
  - Violation justification: N/A - E2E tests planned

- [x] **Asynchronous Programming Standards**: Native async/await
  - Web: Native `async`/`await` for any async operations
  - Form submission and validation are synchronous (no backend calls)
  - Violation justification: N/A - compliant

- [x] **Test Identifiers for UI Controls**: Plan includes data-testid for all elements
  - Web: `data-testid` attribute on all interactive elements
  - Naming convention: `{screen}.{element}.{action}`
  - Examples: `animalDescription.species.select`, `animalDescription.continue.click`
  - Violation justification: N/A - test IDs planned

- [x] **Public API Documentation**: JSDoc for complex logic
  - TypeScript: JSDoc format for form validation functions
  - Component props documented with TypeScript types
  - Skip documentation for self-explanatory code
  - Violation justification: N/A - documentation planned

- [x] **Given-When-Then Test Structure**: All tests follow GWT
  - Unit tests follow Given-When-Then pattern
  - E2E Cucumber scenarios use Gherkin Given-When-Then
  - Descriptive test names in TypeScript format
  - Violation justification: N/A - GWT pattern planned

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - backend not affected
  - This feature is frontend-only (no backend integration per spec)
  - Violation justification: N/A - backend out of scope

- [x] **Backend Code Quality**: N/A - backend not affected
  - Violation justification: N/A - backend out of scope

- [x] **Backend Dependency Management**: N/A - backend not affected
  - Violation justification: N/A - backend out of scope

- [x] **Backend Directory Structure**: N/A - backend not affected
  - Violation justification: N/A - backend out of scope

- [x] **Backend TDD Workflow**: N/A - backend not affected
  - Violation justification: N/A - backend out of scope

- [x] **Backend Testing Strategy**: N/A - backend not affected
  - Violation justification: N/A - backend out of scope

## Project Structure

### Documentation (this feature)

```text
specs/039-web-animal-description-screen/
├── spec.md              # Feature specification
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output (TypeScript interfaces)
│   └── AnimalDescriptionFormData.ts
├── checklists/
│   └── requirements.md  # Quality checklist
└── tasks.md             # Phase 2 output (created by /speckit.tasks)
```

### Source Code (repository root)

```text
webApp/
├── src/
│   ├── types/
│   │   └── animal.ts                    # Existing (AnimalSpecies, AnimalSex)
│   ├── contexts/
│   │   └── ReportMissingPetFlowContext.tsx  # Existing flow state context
│   ├── components/
│   │   └── AnimalDescriptionForm/
│   │       ├── AnimalDescriptionForm.tsx    # Step 3 form component
│   │       ├── AnimalDescriptionForm.test.tsx
│   │       ├── SpeciesDropdown.tsx          # Species selector
│   │       ├── GenderSelector.tsx           # Male/Female selector
│   │       └── CharacterCounter.tsx         # 500 char counter
│   ├── pages/
│   │   └── ReportMissingPet/
│   │       ├── Step1_MicrochipNumber.tsx    # Existing (spec 034)
│   │       ├── Step2_AnimalPhoto.tsx        # Existing (spec 037)
│   │       ├── Step3_AnimalDescription.tsx  # NEW (this feature)
│   │       └── Step4_ContactDetails.tsx     # Future spec
│   ├── utils/
│   │   └── form-validation.ts                # Validation helpers
│   └── __tests__/
│       └── AnimalDescriptionForm.test.tsx   # Unit tests
│
e2e-tests/
├── src/
│   └── test/
│       ├── java/.../pages/
│       │   └── AnimalDescriptionPage.java   # Page Object Model
│       ├── java/.../steps-web/
│       │   └── AnimalDescriptionSteps.java  # Step definitions
│       └── resources/features/web/
│           └── 039-animal-description.feature  # Cucumber scenarios
```

**Structure Decision**: Web application structure with React components organized by feature (form components) and pages (step screens). Follows existing patterns from specs 034 and 037. E2E tests use unified Java/Cucumber structure with `@web` tags.

## Complexity Tracking

> No Constitution violations - all checks passed. This feature follows established web patterns and complies with all applicable principles.

## Phase 0: Research

See [research.md](./research.md) for complete research findings.

**Key Decisions**:
1. **Form State Management**: React Context (consistent with specs 034, 037)
2. **Field Names**: Match Animal type from `/webApp/src/types/animal.ts`
3. **Validation Strategy**: On-submit validation with 5-second toast + inline errors
4. **Navigation Pattern**: Multi-step (back arrow navigates to previous step, not close flow)
5. **Species Dropdown**: Static data from AnimalSpecies type (DOG, CAT, BIRD, RABBIT, OTHER)

## Phase 1: Design & Contracts

See [data-model.md](./data-model.md) and [contracts/](./contracts/) for complete design artifacts.

**Key Entities**:
- `AnimalDescriptionFormData`: Local form state with validation
- `ReportMissingPetFlowState`: Flow-level state (extends existing with Step 3 fields)
- `AnimalSpecies`: Existing enum type (DOG|CAT|BIRD|RABBIT|OTHER)
- `AnimalSex`: Existing enum type (MALE|FEMALE)

**API Contracts**: N/A - no backend integration in this spec

## Implementation Notes

### Multi-Step Navigation Change

**IMPORTANT**: This specification introduces a change to back arrow navigation behavior:
- **Current behavior (specs 034, 037)**: Back arrow closes entire flow
- **New behavior (spec 039 and future updates)**:
  - Step 1/4: Back arrow closes flow → pet list
  - Step 2/4: Back arrow navigates to Step 1 (preserving flow state)
  - Step 3/4: Back arrow navigates to Step 2 (preserving flow state)
  - Browser back button: Always closes flow → pet list

**Action Required**: Specs 034 and 037 need updating to implement consistent multi-step navigation.

### Integration Points

1. **Existing Flow State Context**: Extend `ReportMissingPetFlowState` with Step 3 fields
2. **Existing Animal Types**: Use `AnimalSpecies` and `AnimalSex` from `/webApp/src/types/animal.ts`
3. **Existing Routing**: Add Step 3 route to React Router configuration
4. **Existing Form Patterns**: Reuse styling, validation, and toast patterns from specs 034/037

### Testing Strategy

**Unit Tests** (Vitest + React Testing Library):
- Form validation logic (required fields, date validation, age range)
- State updates when user interacts with fields
- Species change clears breed field
- Character counter for description textarea
- Submit button enables/disables based on validation

**E2E Tests** (Selenium + Cucumber):
- Happy path: Fill all required fields → navigate to Step 4
- Validation errors: Missing required fields → toast + inline errors
- Back navigation: Step 3 → Step 2 → Step 3 (data persists)
- Field interactions: Species selection enables breed, date picker blocks future dates

### Future Enhancements (Out of Scope)

- GPS location capture functionality (separate specification)
- Backend API integration for form submission (separate specification)
- Latitude/longitude input fields (with GPS feature)
- Autocomplete for breed/race field
- Enhanced localization beyond Figma strings
