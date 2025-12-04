# Implementation Plan: Pet Details Screen (Web UI)

**Branch**: `027-web-pet-details-screen` | **Date**: 2025-11-27 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/027-web-pet-details-screen/spec.md`

**Note**: This plan starts with updating the animal list page to match the Figma design, then implements the pet details modal.

## Summary

This feature implements a web pet details modal that displays comprehensive pet information. The modal is opened from the animal list page when users click the "Details" button on any animal card. The implementation will start by updating the existing animal list page to match the Figma design (node-id=168-4656, without filters/search - those will be in a separate spec), then implement the pet details modal (node-id=168-4985).

**Technical Approach**:
- React + TypeScript for UI components
- React state (useState) for modal state management
- React Portal for modal rendering
- Formatting utilities for dates, coordinates, and microchip numbers
- External map integration (Google Maps/OpenStreetMap) via URL links

## Technical Context

**Language/Version**: TypeScript 5.0+ (ES2020 target), React 18.2.0  
**Primary Dependencies**: React, React DOM, React Portal (built-in), CSS Modules  
**Storage**: N/A (consumes backend API)  
**Testing**: Vitest + React Testing Library  
**Target Platform**: Modern browsers (Chrome, Firefox, Safari, Edge - last 2 versions)  
**Project Type**: Web application (`/webApp`)  
**Performance Goals**: Modal opens and displays content within 2 seconds on standard broadband (10 Mbps)  
**Constraints**: 
- Must support mobile (320px+), tablet (768px+), desktop (1024px+) viewports
- Must achieve Lighthouse accessibility score of 90+
- Must trap focus and lock body scroll when modal is open
**Scale/Scope**: Single feature (pet details modal + list page update), ~10-15 components

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Web: Domain models, services, state management in `/webApp`
  - Backend: Independent Node.js/Express API in `/server`
  - NO shared compiled code between platforms
  - Violation justification: _N/A - compliant_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Web: Service interfaces in `/webApp/src/services/`
  - Backend: Repository interfaces in `/server/src/database/repositories/`
  - Implementations in platform-specific data/repositories modules
  - Violation justification: _N/A - compliant_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Web: Uses React Context for service injection (recommended pattern)
  - Backend: Manual DI in `/server/src/` (constructor injection, factory functions)
  - Violation justification: _N/A - compliant_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Web: Tests in `/webApp/src/__tests__/`, run `npm test -- --coverage`
  - Coverage target: 80% line + branch coverage
  - Violation justification: _N/A - compliant_

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: Selenium + Cucumber tests in `/e2e-tests/src/test/resources/features/web/`
  - Page Object Model used with XPath locators
  - Each user story has at least one E2E test
  - Violation justification: _N/A - compliant_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Web: Native `async`/`await` (no Promise chains)
  - Backend: Native `async`/`await` (Express async handlers)
  - No RxJS or callback-based patterns for new code
  - Violation justification: _N/A - compliant_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Web: `data-testid` attribute on all interactive elements
  - Naming convention: `{screen}.{element}.{action}` (e.g., `petDetails.closeButton.click`)
  - List items use stable IDs (e.g., `petDetails.item.${id}`)
  - Violation justification: _N/A - compliant_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - TypeScript: JSDoc format (`/** ... */`)
  - Documentation must be concise and high-level (1-3 sentences: WHAT/WHY, not HOW)
  - Document only when purpose is not clear from name alone
  - Violation justification: _N/A - compliant_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then)
  - Test names follow platform conventions (descriptive strings for TypeScript)
  - Comments mark test phases in complex tests
  - Violation justification: _N/A - compliant_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: Plan uses modern Node.js stack for `/server` module
  - Runtime: Node.js v24 (LTS)
  - Framework: Express.js
  - Language: TypeScript with strict mode enabled
  - Database: Knex query builder + SQLite (designed for PostgreSQL migration)
  - Violation justification: _N/A - backend API endpoint already exists (GET /api/v1/announcements/:id)_

- [x] **Backend Code Quality**: Plan enforces quality standards for `/server` code
  - ESLint with TypeScript plugin configured and enabled
  - Clean Code principles applied
  - Violation justification: _N/A - backend endpoint already implemented_

- [x] **Backend Testing Strategy**: Plan includes comprehensive test coverage for `/server`
  - Unit tests (Vitest): `/src/services/__test__/`, `/src/lib/__test__/`
  - Integration tests (Vitest + SuperTest): `/src/__test__/`
  - Coverage target: 80% line + branch coverage
  - Violation justification: _N/A - backend endpoint already implemented and tested_

## Project Structure

### Documentation (this feature)

```text
specs/027-web-pet-details-screen/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md         # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
webApp/
├── src/
│   ├── components/
│   │   ├── AnimalList/
│   │   │   ├── AnimalList.tsx          # Updated to match Figma design
│   │   │   ├── AnimalList.module.css   # Updated styles for new layout
│   │   │   ├── AnimalCard.tsx          # Updated card structure
│   │   │   ├── AnimalCard.module.css   # Updated card styles
│   │   │   ├── Sidebar.tsx             # NEW: Left sidebar component
│   │   │   ├── Sidebar.module.css
│   │   │   └── EmptyState.tsx          # Existing (may need updates)
│   │   │
│   │   └── PetDetailsModal/            # NEW: Modal component
│   │       ├── PetDetailsModal.tsx     # Main modal component
│   │       ├── PetDetailsModal.module.css
│   │       ├── PetDetailsContent.tsx   # Modal content (stateless)
│   │       ├── PetDetailsContent.module.css
│   │       ├── PetDetailsHeader.tsx    # Header with close button, date, contacts
│   │       ├── PetDetailsHeader.module.css
│   │       ├── PetHeroImage.tsx        # Hero image with badges
│   │       ├── PetHeroImage.module.css
│   │       └── PetDetailsFields.tsx    # Form fields section
│   │       └── PetDetailsFields.module.css
│   │
│   ├── hooks/
│   │   ├── use-animal-list.ts          # Updated for new list structure
│   │   ├── use-pet-details.ts          # NEW: Hook for fetching pet details
│   │   └── use-modal.ts                # NEW: Hook for modal state management
│   │
│   ├── services/
│   │   └── animal-repository.ts        # UPDATE: Replace mock data with backend API calls (GET /api/v1/announcements, GET /api/v1/announcements/:id)
│   │
│   ├── utils/
│   │   ├── date-formatter.ts           # NEW: Date formatting utilities
│   │   ├── coordinate-formatter.ts     # NEW: Coordinate formatting utilities
│   │   ├── microchip-formatter.ts      # NEW: Microchip formatting utilities
│   │   └── map-url-builder.ts          # NEW: External map URL builder
│   │
│   ├── types/
│   │   ├── animal.ts                   # UPDATE: Change Location interface from {city, radiusKm} to {latitude?, longitude?}
│   │   └── pet-details.ts              # NEW: PetDetails type definition
│   │
│   └── __tests__/
│       ├── components/
│       │   ├── AnimalList.test.tsx     # Updated tests
│       │   ├── AnimalCard.test.tsx     # Updated tests
│       │   ├── PetDetailsModal.test.tsx # NEW: Modal tests
│       │   └── PetDetailsContent.test.tsx # NEW: Content tests
│       │
│       ├── hooks/
│       │   ├── use-animal-list.test.ts # Updated tests
│       │   ├── use-pet-details.test.ts # NEW: Pet details hook tests
│       │   └── use-modal.test.ts       # NEW: Modal hook tests
│       │
│       └── utils/
│           ├── date-formatter.test.ts  # NEW: Date formatter tests
│           ├── coordinate-formatter.test.ts # NEW: Coordinate formatter tests
│           └── microchip-formatter.test.ts  # NEW: Microchip formatter tests
```

**Structure Decision**: Web application structure following React component organization. Components are organized by feature (AnimalList, PetDetailsModal) with co-located CSS modules. Utility functions are separated into `/utils` for reusability. Tests mirror source structure.

## Phase 0: Research ✅ Complete

**Status**: All research completed, no unknowns remain.

**Output**: `research.md` - Contains implementation decisions for:
- Modal implementation pattern (React Portal)
- State management (React useState)
- Focus management and accessibility (manual implementation)
- Data formatting utilities (date, coordinates, microchip)
- External map integration (Google Maps/OpenStreetMap URLs)
- Error handling strategy (generic message with retry)
- Responsive design strategy (CSS media queries)
- API integration (extend existing AnimalRepository)

## Phase 1: Design & Contracts ✅ Complete

**Status**: Data models and API contracts defined.

**Outputs**:
- `data-model.md` - Defines PetDetails, Animal, ModalState entities with formatting rules
- `contracts/pet-details-api.json` - API contract for GET /api/v1/announcements/:id endpoint
- `quickstart.md` - Implementation guide with step-by-step instructions

**Key Design Decisions**:
- PetDetails entity matches backend API response format
- Formatting utilities separate from components (testable, reusable)
- Modal state managed via React useState (no URL parameters)
- Error handling uses generic message with unlimited retry

**Agent Context Updated**: ✅ Cursor IDE context file updated with TypeScript/React stack information.

## Phase 2: Task Breakdown

**Status**: Ready for `/speckit.tasks` command.

**Next Steps**:
1. Run `/speckit.tasks 027` to generate detailed task breakdown
2. Tasks will be organized by implementation phase:
   - Phase 1: Update Animal List Page (START HERE)
   - Phase 2: Implement Pet Details Modal
   - Phase 3: Testing

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| _None_ | _N/A_ | _N/A_ |
