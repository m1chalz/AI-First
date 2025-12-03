# Implementation Plan: Web Missing Pet Announcement Submission

**Branch**: `043-web-announcement-submission` | **Date**: 2025-12-03 | **Spec**: [spec.md](./spec.md)  
**Input**: Feature specification from `/specs/043-web-announcement-submission/spec.md`

## Summary

Integrate the web missing pet report form with the backend REST API by implementing announcement submission when the user clicks "Continue" on the contact screen. The system will: (1) POST announcement data to `/api/v1/announcements`, (2) POST photo to `/api/v1/announcements/:id/photos` using the returned management password for authentication, and (3) display the management password on the summary screen. Implementation will reuse existing HTTP client patterns, custom hooks, and styling from the report missing pet flow.

## Technical Context

**Language/Version**: TypeScript 5.x (ES2020+), React 18.x  
**Primary Dependencies**: React, React Router, Vitest, React Testing Library  
**Storage**: Browser session storage (via ReportMissingPetFlowContext)  
**Testing**: Vitest + React Testing Library  
**Target Platform**: Web (modern browsers: Chrome, Firefox, Safari, Edge)  
**Project Type**: Web application (React SPA)  
**Performance Goals**: <10s submission time (SC-001), <2s error display (SC-005)  
**Constraints**: Photo files ≤10MB (SC-004), requires one contact method (FR-012), requires location coordinates (FR-013)  
**Scale/Scope**: Single feature affecting ContactScreen, SummaryScreen, and announcement submission logic

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is a web-only feature affecting `/webApp` module. Android, iOS, and backend checks marked as N/A.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: N/A (not affected)
  - iOS: N/A (not affected)
  - Web: Domain models, services, state management in `/webApp` ✅
  - Backend: Independent Node.js/Express API in `/server` (not affected by this feature)
  - NO shared compiled code between platforms ✅
  - Violation justification: _N/A - compliant_

- [ ] **Android MVI Architecture**: N/A - Android platform not affected by this web-only feature

- [ ] **iOS MVVM-C Architecture**: N/A - iOS platform not affected by this web-only feature

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Android: N/A
  - iOS: N/A
  - Web: Service interfaces in `/webApp/src/services/` ✅
  - Backend: Repository interfaces in `/server/src/database/repositories/` (not affected)
  - Implementations in platform-specific data/repositories modules ✅
  - Use cases reference interfaces, not concrete implementations ✅
  - Violation justification: _N/A - compliant_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: N/A
  - iOS: N/A
  - Web: React Context for ReportMissingPetFlowContext ✅
  - Backend: N/A
  - Violation justification: _N/A - compliant_

- [x] **80% Test Coverage - Platform-Specific**: Plan includes unit tests for each platform
  - Android: N/A
  - iOS: N/A
  - Web: Tests in `/webApp/src/__tests__/`, run `npm test -- --coverage` ✅
  - Backend: N/A
  - Coverage target: 80% line + branch coverage per platform ✅
  - Violation justification: _N/A - compliant_

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: Selenium tests in `/e2e-tests/src/test/resources/features/web/043-announcement-submission.feature` ✅
  - Mobile: N/A
  - All tests written in Java with Cucumber ✅
  - Page Object Model used ✅
  - Each user story has at least one E2E test ✅
  - Violation justification: _N/A - compliant_

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Android: N/A
  - iOS: N/A
  - Web: Native `async`/`await` (no Promise chains) ✅
  - Backend: N/A
  - No Combine, RxJava, RxSwift, or callback-based patterns for new code ✅
  - Violation justification: _N/A - compliant_

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: N/A
  - iOS: N/A
  - Web: `data-testid` attribute on all interactive elements ✅
  - Naming convention: `{screen}.{element}.{action}` (e.g., `summary.password.text`) ✅
  - List items use stable IDs (N/A for this feature)
  - Violation justification: _N/A - compliant_

- [x] **Public API Documentation**: Plan ensures public APIs have documentation when needed
  - Kotlin: N/A
  - Swift: N/A
  - TypeScript: JSDoc format (`/** ... */`) ✅
  - Documentation must be concise and high-level (1-3 sentences: WHAT/WHY, not HOW) ✅
  - Document only when purpose is not clear from name alone ✅
  - Skip documentation for self-explanatory methods, variables, and constants ✅
  - Violation justification: _N/A - compliant_

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then) ✅
  - ViewModel tests use Given-When-Then pattern with descriptive names ✅
  - E2E tests structure scenarios with Given-When-Then phases ✅
  - Test names follow platform conventions (descriptive strings for TypeScript) ✅
  - Comments mark test phases in complex tests ✅
  - Violation justification: _N/A - compliant_

### Backend Architecture & Quality Standards (if `/server` affected)

- [ ] **Backend Technology Stack**: N/A - `/server` module not affected by this feature

- [ ] **Backend Code Quality**: N/A - `/server` module not affected by this feature

- [ ] **Backend Dependency Management**: N/A - `/server` module not affected by this feature

- [ ] **Backend Directory Structure**: N/A - `/server` module not affected by this feature

- [ ] **Backend TDD Workflow**: N/A - `/server` module not affected by this feature

- [ ] **Backend Testing Strategy**: N/A - `/server` module not affected by this feature

## Project Structure

### Documentation (this feature)

```text
specs/043-web-announcement-submission/
├── plan.md              # This file
├── research.md          # Phase 0 output (API integration patterns, error handling)
├── data-model.md        # Phase 1 output (AnnouncementSubmissionDto, ApiError types)
├── quickstart.md        # Phase 1 output (Developer setup guide)
├── contracts/           # Phase 1 output (API request/response types)
└── tasks.md             # Phase 2 output (NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
/webApp/src/
├── services/
│   ├── announcement-service.ts          # RENAMED from animal-repository.ts - Extended with POST methods
│   └── __tests__/
│       └── announcement-service.test.ts # RENAMED from animal-repository.test.ts - Add POST tests
├── hooks/
│   ├── use-announcement-submission.ts   # NEW - Hook managing submission state and logic (evolved from use-announcement-creation)
│   │                                    # Phase 3: Created as use-announcement-creation.ts (announcement only)
│   │                                    # Phase 4: Renamed to use-announcement-submission.ts (announcement + photo)
│   └── __tests__/
│       └── use-announcement-submission.test.ts # NEW - Unit tests for submission hook (evolved naming)
├── models/
│   ├── announcement-submission.ts       # NEW - DTOs for announcement creation
│   └── api-error.ts                     # NEW - Error types for API responses
├── components/
│   └── ReportMissingPet/
│       ├── ContactScreen.tsx            # MODIFIED - Add submission logic on Continue click
│       ├── SummaryScreen.tsx            # MODIFIED - Display management password, add exit confirmation
│       └── __tests__/
│           ├── ContactScreen.test.tsx   # MODIFIED - Add submission tests
│           └── SummaryScreen.test.tsx   # MODIFIED - Add password display tests
├── utils/
│   └── __tests__/
│       └── format-file-size.test.ts     # EXISTING - Reuse for photo size validation
└── config/
    └── config.ts                        # EXISTING - Reuse for apiBaseUrl

/e2e-tests/
├── src/test/resources/features/web/
│   └── 043-announcement-submission.feature # NEW - Gherkin scenarios for announcement submission
├── src/test/java/.../pages/
│   ├── ContactPage.java                # MODIFIED - Add continue button with submission wait
│   └── SummaryPage.java                # NEW - Page object for summary screen with password
└── src/test/java/.../steps-web/
    └── AnnouncementSubmissionSteps.java # NEW - Step definitions for submission flow
```

**Structure Decision**: Web application structure with React components, custom hooks for state management, services for API calls, and models for data types. Follows existing project patterns: custom hooks (use-animal-list, use-pet-details), service layer (animal-repository), and test-driven development with Vitest + React Testing Library. E2E tests follow Selenium + Cucumber pattern with Page Object Model.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No violations - all constitution checks passed.

## Phase 0: Research & Decisions

### Research Topics

1. **Backend API Integration Patterns**
   - Decision: Use existing `fetch` API pattern from `animal-repository.ts`
   - Rationale: Consistent with existing codebase, no external dependencies needed
   - Alternatives considered: Axios (rejected - adds dependency), native `fetch` (chosen - already in use)

2. **Photo Upload with Multipart/Form-Data**
   - Decision: Use `FormData` API with `fetch` for photo upload
   - Rationale: Native browser API, supports file uploads, works with backend multer middleware
   - Alternatives considered: Base64 encoding (rejected - larger payload), blob URLs (rejected - requires different backend handling)

3. **Management Password Authentication**
   - Decision: Use HTTP Basic Auth (username: announcement ID, password: management password)
   - Rationale: Backend expects Basic Auth per `basic-auth-middleware.ts` and `announcement-auth-middleware.ts`
   - Pattern: `Authorization: Basic ${btoa(`${announcementId}:${managementPassword}`)}`
   - Alternatives considered: Bearer token (rejected - backend uses Basic Auth), custom header (rejected - non-standard)

4. **Error Handling Strategy**
   - Decision: Use toast notifications for errors, preserve form data on failure (per FR-011)
   - Rationale: Consistent with existing error handling (use-toast.ts), non-blocking UX
   - Error types: Network errors, validation errors (400), duplicate microchip (409), server errors (500)
   - Alternatives considered: Modal dialogs (rejected - blocking), inline error messages (rejected - less visible)

5. **Loading State Management**
   - Decision: Use React state in custom hook (isSubmitting, submissionError)
   - Rationale: Follows existing patterns (use-animal-list.ts, use-pet-details.ts)
   - Loading indicator: Display during POST /announcements and POST /photos (sequential)
   - Alternatives considered: Global loading context (rejected - overkill for single feature), no loading state (rejected - poor UX)

6. **Hook Architecture Evolution Strategy**
   - Decision: Implement submission in two phases with progressive hook evolution
   - **Phase 1 (Announcement Creation)**: `use-announcement-creation.ts` hook
     - Scope: Create announcement via POST /api/v1/announcements (without photo upload)
     - State: `isCreating`, `error`, `announcementId`, `managementPassword`
     - Purpose: Enable independent testing of announcement creation before photo upload complexity
   - **Phase 2 (Full Submission)**: Rename to `use-announcement-submission.ts` hook
     - Scope: Extends to include photo upload via POST /api/v1/announcements/:id/photos
     - State: `isSubmitting`, `error`, `managementPassword` (combines creation + upload)
     - Purpose: Complete end-to-end submission with sequential API calls
   - Rationale: Two-phase approach enables:
     1. Independent testing of announcement creation (Phase 3 checkpoint)
     2. Clear separation of concerns (create vs upload)
     3. Easier debugging if photo upload has issues
     4. Incremental complexity (simpler hook first, then extend)
   - Implementation note: Hook is created as `use-announcement-creation` in Phase 3, then renamed and extended in Phase 4
   - Alternatives considered: Single hook from start (rejected - harder to test incrementally), separate hooks maintained (rejected - duplication of state management)

7. **Exit Confirmation on Summary Screen**
   - Decision: Use browser `beforeunload` event for navigation away warning (per User Story 3, Acceptance Scenario 3)
   - Rationale: Standard browser API, covers all navigation (back button, close tab, URL change)
   - Implementation: `useEffect` with `window.addEventListener('beforeunload', handler)`
   - Alternatives considered: React Router prompt (rejected - doesn't cover browser close), custom modal (rejected - can't intercept browser close)

### Best Practices

1. **React Hooks Pattern**
   - Follow existing custom hook conventions: `use-[feature-name].ts`
   - Return object with state and functions: `{ isSubmitting, error, submitAnnouncement }`
   - Use `useCallback` for memoized functions
   - Co-locate tests in `__tests__/` subdirectory

2. **Service Layer Pattern**
   - Follow existing service conventions: `[feature]-service.ts`
   - Implement interface-based design: `IAnnouncementService` interface
   - Export singleton instance: `export const announcementService = new AnnouncementService()`
   - Use `config.apiBaseUrl` for base URL
   - Throw errors with descriptive messages for error handling in hooks

3. **TypeScript Type Safety**
   - Define DTOs in `/models` directory: `AnnouncementSubmissionDto`, `AnnouncementResponse`
   - Use discriminated unions for API errors: `type ApiError = NetworkError | ValidationError | ServerError`
   - Leverage TypeScript strict mode (existing in tsconfig.json)
   - Export types from models for reuse in services and hooks

4. **Test Coverage Strategy**
   - Unit tests for services: Mock `fetch`, verify request construction, test error scenarios
   - Unit tests for hooks: Mock service, verify state transitions, test loading/error states
   - Component tests: Mock hook, verify UI rendering, test user interactions
   - E2E tests: Test full flow end-to-end with real backend (dev environment)
   - Target: 80% line + branch coverage (enforce with Vitest coverage thresholds)

5. **File Naming Conventions**
   - Components: PascalCase (ContactScreen.tsx, SummaryScreen.tsx)
   - Non-components: kebab-case (announcement-service.ts, use-announcement-submission.ts, api-error.ts)
   - Test files: Match source file name with `.test.ts` suffix
   - CSS modules: Match component name with `.module.css` suffix

## Phase 1: Design & Contracts

### Data Model

See [data-model.md](./data-model.md) for complete entity definitions.

**Key Types**:

1. **AnnouncementSubmissionDto** (request payload to POST /api/v1/announcements)
   - Fields: petName?, species, breed?, sex, age?, description?, microchipNumber?, locationLatitude, locationLongitude, email?, phone?, lastSeenDate, status, reward?
   - Validation: At least one of email/phone required, locationLatitude/locationLongitude required

2. **AnnouncementResponse** (response from POST /api/v1/announcements)
   - Fields: id (string), managementPassword (string), all announcement fields
   - Used to extract id and managementPassword for photo upload

3. **PhotoUploadRequest** (multipart/form-data to POST /api/v1/announcements/:id/photos)
   - Field: photo (File object from PhotoAttachment)
   - Headers: Authorization (Basic Auth with announcement ID and management password)

4. **ApiError** (discriminated union for error handling)
   - NetworkError: { type: 'network', message: string }
   - ValidationError: { type: 'validation', message: string, field?: string }
   - DuplicateMicrochipError: { type: 'duplicate_microchip', message: string }
   - ServerError: { type: 'server', message: string, statusCode: number }

### API Contracts

See [contracts/](./contracts/) directory for OpenAPI specifications.

**Endpoint 1**: `POST /api/v1/announcements`
- Request: `AnnouncementSubmissionDto` (application/json)
- Response 201: `AnnouncementResponse` (includes id and managementPassword)
- Response 400: Validation error (missing required fields)
- Response 409: Duplicate microchip number
- Response 500: Server error

**Endpoint 2**: `POST /api/v1/announcements/:id/photos`
- Request: multipart/form-data with `photo` field (File)
- Headers: `Authorization: Basic ${btoa(`${id}:${managementPassword}`)}`
- Response 201: Empty body (photo uploaded successfully)
- Response 401: Unauthorized (invalid credentials)
- Response 404: Announcement not found
- Response 500: Server error

### Component Changes

**ContactScreen.tsx**:
- Import `useAnnouncementSubmission` hook
- Replace `handleContinue` logic to call `submitAnnouncement()` from hook
- Show loading indicator during submission (reuse existing `primaryButton` with disabled state)
- Handle errors with toast notifications
- Preserve form data on failure (already handled by ReportMissingPetFlowContext)

**SummaryScreen.tsx**:
- Accept `managementPassword` prop from navigation state (passed by ContactScreen after successful submission)
- Display management password in highlighted card (styled similar to existing flow state summary)
- Add instructional text explaining password purpose
- Add `useEffect` with `beforeunload` event listener for exit confirmation
- Update "Complete" button to show confirmation warning

### Custom Hook Design

**use-announcement-submission.ts**:
```typescript
interface UseAnnouncementSubmissionResult {
  isSubmitting: boolean;
  error: ApiError | null;
  managementPassword: string | null;
  submitAnnouncement: (flowState: ReportMissingPetFlowState) => Promise<boolean>;
}
```

Responsibilities:
- Convert `ReportMissingPetFlowState` to `AnnouncementSubmissionDto`
- Call `announcementService.createAnnouncement(dto)`
- Extract `id` and `managementPassword` from response
- Call `announcementService.uploadPhoto(id, photo, managementPassword)`
- Manage loading state (`isSubmitting`) and error state (`error`)
- Return `true` on success, `false` on failure
- Store `managementPassword` in state for passing to SummaryScreen

### Service Design

**announcement-service.ts**:
```typescript
interface IAnnouncementService {
  createAnnouncement(dto: AnnouncementSubmissionDto): Promise<AnnouncementResponse>;
  uploadPhoto(announcementId: string, photo: File, managementPassword: string): Promise<void>;
}

class AnnouncementService implements IAnnouncementService {
  async createAnnouncement(dto: AnnouncementSubmissionDto): Promise<AnnouncementResponse> {
    // POST to /api/v1/announcements with JSON body
    // Throw ApiError on failure
  }

  async uploadPhoto(announcementId: string, photo: File, managementPassword: string): Promise<void> {
    // Create FormData with photo
    // POST to /api/v1/announcements/:id/photos with Basic Auth
    // Throw ApiError on failure
  }
}

export const announcementService = new AnnouncementService();
```

### Quickstart Guide

See [quickstart.md](./quickstart.md) for complete developer setup instructions.

**Prerequisites**:
- Node.js v24 (LTS)
- Backend server running on http://localhost:3000
- Web dev server running on http://localhost:5173 (or configured port)

**Development Workflow**:
1. Start backend: `cd server && npm run dev`
2. Start web app: `cd webApp && npm run start`
3. Navigate to http://localhost:5173/report-missing
4. Complete flow through Contact screen
5. Click "Continue" to trigger submission
6. Verify management password displayed on Summary screen

**Testing Workflow**:
1. Unit tests: `cd webApp && npm test -- --coverage`
2. E2E tests: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web"`
3. Coverage report: `webApp/coverage/index.html`
4. E2E report: `e2e-tests/target/cucumber-reports/web/index.html`

## Implementation Notes

**Rename Strategy**:
- **RENAME** `animal-repository.ts` → `announcement-service.ts`
- **RENAME** `animal-repository.test.ts` → `announcement-service.test.ts`
- **RENAME** class `AnimalRepository` → `AnnouncementService`
- **UPDATE** all imports: `import { animalRepository }` → `import { announcementService }`
- **EXTEND** renamed service with new POST methods: `createAnnouncement()`, `uploadPhoto()`
- **KEEP** existing GET methods: `getAnimals()`, `getPetById()`
- **RATIONALE**: Consolidate all announcement-related API calls in one service

**Files Requiring Import Updates**:
- `hooks/use-animal-list.ts`
- `hooks/use-pet-details.ts`
- `hooks/__tests__/hooks/use-animal-list.test.ts`
- `hooks/__tests__/use-pet-details.test.ts`

**Reuse Existing Code**:
- HTTP client pattern from current `animal-repository.ts` (native `fetch`)
- Custom hook pattern from `use-animal-list.ts`, `use-pet-details.ts`
- Toast notifications from `use-toast.ts`
- Flow context from `ReportMissingPetFlowContext`
- Styles from `ReportMissingPetLayout.module.css`
- Test patterns from existing test files

**File Naming**:
- Components: PascalCase (ContactScreen.tsx)
- Services, hooks, models: kebab-case (announcement-service.ts, use-announcement-submission.ts)

**Key Success Criteria**:
- SC-001: <10s submission time (measured from Continue click to Summary screen display)
- SC-002: 95% success rate (validated through E2E test reliability)
- SC-004: Photo files up to 10MB (already validated in PhotoScreen)
- SC-005: <2s error display (toast appears within 2s of error)
- SC-007: 100% of successful submissions display management password

**Risk Mitigation**:
- Network failures: Preserve form data, allow retry (FR-011)
- Duplicate microchip: Display helpful error with guidance (per clarification)
- Photo upload failure: Treat as submission failure, allow full retry (per clarification)
- Browser closure: No persistence, user must restart (per clarification)
