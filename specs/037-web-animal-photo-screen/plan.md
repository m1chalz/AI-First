# Implementation Plan: Web Animal Photo Screen

**Branch**: `037-web-animal-photo-screen` | **Date**: 2025-12-01 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/037-web-animal-photo-screen/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implement step 2/4 of the "report missing pet" flow for the web platform (React + TypeScript). This screen allows users to upload a pet photo via file picker or drag-and-drop, with client-side validation (format, size), mandatory photo enforcement, and flow state persistence. The implementation will heavily reuse existing components and patterns from the Microchip Number Screen (step 1/4):

**Reusable Components from Step 1/4:**
- `ReportMissingPetLayout` - shared layout wrapper with Header
- `Header` component - back arrow, title, progress indicator
- `ReportMissingPetFlowContext` - flow state management (Context API)
- `use-browser-back-handler` hook - browser back button handling
- `use-report-missing-pet-flow` hook - context wrapper
- `ReportMissingPetLayout.module.css` - all shared styles (heading, description, buttons, inputs, responsive layout)
- `Header.module.css` - header styles
- Flow state models and types

**New Components/Logic:**
- `PhotoScreen.tsx` - photo upload screen component (follows MicrochipNumberScreen pattern)
- `use-photo-upload` hook - photo file handling, validation, preview URL management
- Toast notification component/hook for validation errors
- Drag-and-drop event handlers
- File validation utilities (MIME type, size)
- Update `ReportMissingPetFlowState` to include photo data (File object, metadata)

Technical approach: HTML5 file input with drag-and-drop, in-memory React state for photo storage, client-side validation only, React Router for navigation with route guards, toast notifications for validation errors.

## Technical Context

**Language/Version**: TypeScript 5.x with React 18.x  
**Primary Dependencies**: React, React Router v6, Vitest + React Testing Library (existing)  
**Storage**: In-memory React state only (no localStorage/sessionStorage) - photo survives in-flow navigation but not browser refresh  
**Testing**: Vitest + React Testing Library (existing test infrastructure in `/webApp/src/__tests__/`)  
**Target Platform**: Modern web browsers (Chrome, Firefox, Safari, Edge with ES2015+ support)  
**Project Type**: Web application (existing `/webApp` module)  
**Performance Goals**: Photo upload interaction < 2 seconds for files up to 20MB on standard broadband (10 Mbps+)  
**Constraints**: 
- Max file size: 20MB
- Supported formats: JPG, PNG, GIF, WEBP, BMP, TIFF, HEIC, HEIF
- Client-side validation only (no backend upload in this feature)
- Photo mandatory for flow completion (enforced via toast notification)  
**Scale/Scope**: Single screen (step 2/4 of 4-step flow), reusing 90% of existing components/styles from step 1/4

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is a web-only feature (step 2/4 of web missing pet flow). Android, iOS, and backend-related checks marked as N/A. Focus on Web architecture, testing, and E2E compliance.

**Initial Check (Pre-Phase 0)**: ✅ PASSED - All checks compliant or N/A  
**Re-check (Post-Phase 1 Design)**: ✅ PASSED - Design decisions maintain full compliance with constitution

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (no Android changes)
  - iOS: N/A (no iOS changes)
  - Web: ✅ Full implementation in `/webApp` (React components, hooks, state management)
  - Backend: N/A (no backend API changes - client-side validation only)
  - NO shared compiled code between platforms
  - Violation justification: N/A - compliant

- [x] **Android MVI Architecture**: N/A - no Android changes in this feature
  - Violation justification: N/A - web-only feature

- [x] **iOS MVVM-C Architecture**: N/A - no iOS changes in this feature
  - Violation justification: N/A - web-only feature

- [x] **Interface-Based Design**: N/A - no domain repositories involved in this feature
  - Web: Photo screen uses React hooks and Context (no service layer for this feature)
  - This feature handles client-side file validation only (no backend API calls)
  - Violation justification: N/A - no domain logic requiring interfaces

- [x] **Dependency Injection**: ✅ Uses React Context for flow state management
  - Web: ✅ Uses existing `ReportMissingPetFlowContext` (React Context API)
  - Follows recommended React Context pattern from constitution
  - All flow state injected via `useReportMissingPetFlow` hook
  - Violation justification: N/A - compliant

- [x] **80% Test Coverage - Platform-Specific**: ✅ Plan includes comprehensive unit tests
  - Android: N/A (no Android changes)
  - iOS: N/A (no iOS changes)
  - Web: ✅ Tests planned in `/webApp/src/__tests__/components/ReportMissingPet/PhotoScreen.test.tsx` and `/webApp/src/__tests__/hooks/use-photo-upload.test.ts`
    - Test coverage: PhotoScreen component (file picker, drag-and-drop, validation, navigation)
    - Test coverage: use-photo-upload hook (file handling, validation, preview URL management, cleanup)
    - Test coverage: File validation utilities (MIME type, size checks)
    - Run: `npm test -- --coverage` from `/webApp`
  - Backend: N/A (no backend changes)
  - Coverage target: 80% line + branch coverage for new code
  - Violation justification: N/A - compliant

- [x] **End-to-End Tests**: ✅ Plan includes E2E tests for all user stories
  - Web: ✅ Selenium + Cucumber tests planned in `/e2e-tests/src/test/resources/features/web/animal-photo-screen.feature` (Gherkin with `@web` tag)
  - Page Object Model: `/e2e-tests/src/test/java/.../pages/AnimalPhotoPage.java` (XPath locators)
  - Step Definitions: `/e2e-tests/src/test/java/.../steps-web/AnimalPhotoSteps.java`
  - Scenarios cover: file picker upload, drag-and-drop upload, validation errors (format, size), mandatory photo enforcement, navigation (continue, back)
  - Mobile: N/A (no mobile changes - mobile photo screen is separate feature)
  - Violation justification: N/A - compliant

- [x] **Asynchronous Programming Standards**: ✅ Uses native async/await for file operations
  - Android: N/A (no Android changes)
  - iOS: N/A (no iOS changes)
  - Web: ✅ Native async/await for file reading (FileReader API), no Promise chains
  - Backend: N/A (no backend changes)
  - No prohibited patterns (RxJS, callbacks) used
  - Violation justification: N/A - compliant

- [x] **Test Identifiers for UI Controls**: ✅ All interactive elements have test identifiers
  - Android: N/A (no Android changes)
  - iOS: N/A (no iOS changes)
  - Web: ✅ Test identifiers planned with `data-testid` attribute:
    - `animalPhoto.browse.click` - Browse button
    - `animalPhoto.fileInput.field` - Hidden file input
    - `animalPhoto.dropZone.area` - Drag-and-drop zone
    - `animalPhoto.remove.click` - Remove (X) button on confirmation card
    - `animalPhoto.continue.click` - Continue button
    - `animalPhoto.back.click` - Back arrow button (reuses Header component)
    - `animalPhoto.confirmationCard` - Photo confirmation card
    - `animalPhoto.filename.text` - Filename display
    - `animalPhoto.filesize.text` - File size display
  - Naming convention: `{screen}.{element}.{action}` - ✅ compliant
  - Violation justification: N/A - compliant

- [x] **Public API Documentation**: ✅ JSDoc documentation planned for non-obvious APIs
  - TypeScript: JSDoc format for hooks and utility functions
  - `use-photo-upload` hook - documents file handling, validation, and cleanup behavior
  - File validation utilities - document validation rules (MIME types, size limits)
  - Toast notification hook/component - document usage and timing
  - Self-explanatory functions (e.g., `handleContinue`, `handleBack`) - no documentation needed
  - Violation justification: N/A - compliant

- [x] **Given-When-Then Test Structure**: ✅ All tests follow Given-When-Then convention
  - Unit tests: Clearly separated setup (Given), action (When), verification (Then)
  - Test names use descriptive strings: `'should display validation error when file exceeds 20MB'`
  - Comments mark test phases in complex tests (e.g., file validation with multiple scenarios)
  - E2E tests: Gherkin scenarios naturally follow Given-When-Then structure
  - Violation justification: N/A - compliant

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - no backend changes (client-side validation only)
  - Violation justification: N/A - frontend-only feature

- [x] **Backend Code Quality**: N/A - no backend code affected
  - Violation justification: N/A - frontend-only feature

- [x] **Backend Dependency Management**: N/A - no backend dependencies added
  - Violation justification: N/A - frontend-only feature

- [x] **Backend Directory Structure**: N/A - no backend structure changes
  - Violation justification: N/A - frontend-only feature

- [x] **Backend TDD Workflow**: N/A - no backend code to test
  - Violation justification: N/A - frontend-only feature

- [x] **Backend Testing Strategy**: N/A - no backend tests required
  - Violation justification: N/A - frontend-only feature

## Project Structure

### Documentation (this feature)

```text
specs/037-web-animal-photo-screen/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
/webApp/src/
├── components/
│   └── ReportMissingPet/
│       ├── Header.tsx                          # REUSED - existing header component
│       ├── Header.module.css                   # REUSED - header styles
│       ├── ReportMissingPetLayout.tsx          # REUSED - layout wrapper
│       ├── ReportMissingPetLayout.module.css   # REUSED - shared styles (heading, button, input, etc.)
│       ├── MicrochipNumberScreen.tsx           # REUSED - step 1/4 (existing)
│       ├── PhotoScreen.tsx                     # NEW - step 2/4 (this feature)
│       └── __tests__/
│           ├── Header.test.tsx                 # REUSED - existing tests
│           ├── MicrochipNumberScreen.test.tsx  # REUSED - existing tests
│           └── PhotoScreen.test.tsx            # NEW - photo screen tests
├── contexts/
│   ├── ReportMissingPetFlowContext.tsx         # UPDATED - add photo to flow state
│   └── __tests__/
│       └── ReportMissingPetFlowContext.test.tsx # UPDATED - test photo state
├── hooks/
│   ├── use-report-missing-pet-flow.ts          # REUSED - context wrapper
│   ├── use-browser-back-handler.ts             # REUSED - browser back handling
│   ├── use-microchip-formatter.ts              # REUSED - formatter pattern reference
│   ├── use-photo-upload.ts                     # NEW - photo upload logic
│   └── __tests__/
│       ├── use-browser-back-handler.test.ts    # REUSED - existing tests
│       ├── use-microchip-formatter.test.ts     # REUSED - existing tests
│       └── use-photo-upload.test.ts            # NEW - photo upload hook tests
├── models/
│   └── ReportMissingPetFlow.ts                 # UPDATED - add photo types
├── utils/
│   ├── file-validation.ts                      # NEW - MIME type and size validation
│   ├── format-file-size.ts                     # NEW - format bytes to human-readable
│   └── __tests__/
│       ├── file-validation.test.ts             # NEW - validation tests
│       └── format-file-size.test.ts            # NEW - formatting tests
└── routes/
    └── report-missing-pet-routes.tsx           # UPDATED - add PhotoScreen route

/e2e-tests/
├── src/test/resources/features/web/
│   └── animal-photo-screen.feature             # NEW - Gherkin scenarios
├── src/test/java/.../pages/
│   └── AnimalPhotoPage.java                    # NEW - Page Object Model
└── src/test/java/.../steps-web/
    └── AnimalPhotoSteps.java                   # NEW - Step definitions
```

**Structure Decision**: Web application structure using existing `/webApp` module. This feature heavily reuses components, styles, and patterns from the Microchip Number Screen (step 1/4). Key reusable elements:
- `ReportMissingPetLayout` and `Header` components (no changes needed)
- All shared styles from `ReportMissingPetLayout.module.css` (heading, description, buttons, inputs, responsive layout)
- `ReportMissingPetFlowContext` for state management (minor update to add photo data)
- `use-browser-back-handler` hook (no changes needed)
- Testing patterns from existing tests

New components are minimal: `PhotoScreen.tsx` component, `use-photo-upload` hook, file validation utilities, and corresponding tests.

## Complexity Tracking

> **No violations** - All Constitution Check items are compliant or N/A. This feature follows existing patterns and reuses 90% of components/styles from step 1/4.
