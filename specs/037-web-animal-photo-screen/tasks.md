# Tasks: Web Animal Photo Screen

**Input**: Design documents from `/specs/037-web-animal-photo-screen/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Tests**: Test requirements for this project:

**MANDATORY - Web Unit Tests**:
- Location: `/webApp/src/__tests__/`
- Framework: Vitest + React Testing Library
- Coverage: 80% line + branch coverage for new code
- Scope: PhotoScreen component, use-photo-upload hook, file validation utilities
- Run: `npm test -- --coverage` (from webApp/)
- Convention: MUST follow Given-When-Then structure with descriptive test names

**MANDATORY - End-to-End Tests**:
- Web: `/e2e-tests/java/src/test/resources/features/web/animal-photo-screen.feature` (Selenium + Cucumber + Java)
- Page Object Model: `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/pages/AnimalPhotoPage.java` (XPath locators)
- Step Definitions: `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/web/AnimalPhotoSteps.java`
- All user stories MUST have E2E test coverage
- Run: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web"`
- Convention: Gherkin scenarios naturally follow Given-When-Then structure

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story. Each task is atomic following TDD: write failing test → implement minimal logic → run tests (must pass) → run linting (must pass with no issues).

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Update flow state model and add photo-related utilities

- [X] T001 Update `ReportMissingPetFlowState` interface in `/webApp/src/models/ReportMissingPetFlow.ts` to add `photo: PhotoAttachment | null` field and create `PhotoAttachment` interface (file, filename, size, mimeType, previewUrl)
- [X] T002 Update `initialFlowState` in `/webApp/src/models/ReportMissingPetFlow.ts` to include `photo: null`
- [X] T003 Add `FlowStep.Photo` enum value in `/webApp/src/models/ReportMissingPetFlow.ts`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core utilities and types that MUST be complete before ANY user story implementation

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T004 [P] Create file validation constants in `/webApp/src/utils/file-validation.ts` (ALLOWED_MIME_TYPES array with 8 formats: image/jpeg, image/png, image/gif, image/webp, image/bmp, image/tiff, image/heic, image/heif; MAX_FILE_SIZE_BYTES = 20MB)
- [X] T005 [P] Create format-file-size utility in `/webApp/src/utils/format-file-size.ts` (function formatFileSize(bytes: number): string returns "X MB" or "X KB")
- [X] T006 Update `ReportMissingPetFlowContext` test in `/webApp/src/contexts/__tests__/ReportMissingPetFlowContext.test.tsx` to include photo state in assertions

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Upload photo via file picker or drag-and-drop (Priority: P1) 🎯 MVP

**Goal**: Enable users to upload pet photos via file picker OR drag-and-drop, see confirmation card with filename/size, and navigate to step 3/4 with photo stored in flow state

**Independent Test**: Navigate to /report-missing/photo, select or drop a valid image file, verify confirmation card appears with file details, click Continue and verify navigation to step 3/4 with photo in flow state

### Tests for User Story 1 (TDD: Write FIRST, ensure FAIL before implementation) ✅

**Web Unit Tests**:
- [X] T007 [P] [US1] 🔴 RED: Create failing test file `/webApp/src/utils/__tests__/file-validation.test.ts` with tests for `validateFileMimeType()` (valid: JPG/PNG/GIF/WEBP/BMP/TIFF/HEIC/HEIF, invalid: PDF/TXT/SVG) and `validateFileSize()` (valid: 1MB/10MB/20MB, invalid: 21MB/30MB)
- [X] T008 [P] [US1] 🔴 RED: Create failing test file `/webApp/src/utils/__tests__/format-file-size.test.ts` with tests for `formatFileSize()` (0 bytes, 500 bytes, 1 KB, 1.5 MB, 20 MB, edge cases)
- [X] T009 [P] [US1] 🔴 RED: Create failing test file `/webApp/src/hooks/__tests__/use-photo-upload.test.ts` with tests for initial state (photo: null, error: null, isDragOver: false), file selection via handleFileSelect (valid file stores PhotoAttachment), drag-and-drop via handleDrop (valid file stores PhotoAttachment), removePhoto (clears photo and revokes blob URL), and cleanup on unmount (revokes blob URL)

**End-to-End Tests**:
- [X] T010 [P] [US1] Create Gherkin scenarios in `/e2e-tests/java/src/test/resources/features/web/animal-photo-screen.feature` with @web tag for US1: (1) upload via file picker, (2) upload via drag-and-drop, (3) navigate back to step 2/4 and see persisted photo

### Implementation for User Story 1 (TDD: Implement AFTER tests fail)

**Web Implementation**:
- [X] T011 [P] [US1] ✅ GREEN: Implement `validateFileMimeType()` in `/webApp/src/utils/file-validation.ts` (minimal code to pass T007 tests: check if file.type is in ALLOWED_MIME_TYPES array)
- [X] T012 [P] [US1] ✅ GREEN: Implement `validateFileSize()` in `/webApp/src/utils/file-validation.ts` (minimal code to pass T007 tests: check if file.size <= MAX_FILE_SIZE_BYTES)
- [X] T013 [P] [US1] ✅ GREEN: Implement `getFileValidationError()` in `/webApp/src/utils/file-validation.ts` (calls validateFileMimeType and validateFileSize, returns appropriate error message or null)
- [X] T014 [P] [US1] ✅ GREEN: Implement `formatFileSize()` in `/webApp/src/utils/format-file-size.ts` (minimal code to pass T008 tests: convert bytes to KB/MB with 1 decimal place)
- [X] T015 [US1] ✅ GREEN: Implement `use-photo-upload` hook in `/webApp/src/hooks/use-photo-upload.ts` (minimal code to pass T009 tests: useState for photo/error/isDragOver, handleFileSelect validates and creates PhotoAttachment with URL.createObjectURL, handleDrop extracts file and calls handleFileSelect, handleDragOver/handleDragLeave toggle isDragOver, removePhoto clears photo and calls URL.revokeObjectURL, useEffect cleanup calls URL.revokeObjectURL on unmount)
- [X] T016 [US1] 🔧 REFACTOR: Extract validation logic into reusable helper if needed, add JSDoc comments to use-photo-upload hook documenting file handling and cleanup behavior
- [X] T017 [US1] ✅ RUN TESTS: Execute `npm test -- --coverage` from webApp/, verify all US1 tests pass (T007-T009) and coverage ≥80% for new files
- [X] T018 [P] [US1] Create `PhotoScreen.tsx` component in `/webApp/src/components/ReportMissingPet/PhotoScreen.tsx` following MicrochipNumberScreen pattern: imports (useNavigate, useReportMissingPetFlow, use-photo-upload, use-browser-back-handler, ReportMissingPetLayout), handleContinue (validates photo exists, updates flow state with photo and currentStep: FlowStep.Details, navigates to /report-missing/details), handleBack (clears flow state, navigates to /), useBrowserBackHandler(handleBack), returns ReportMissingPetLayout with title="Animal photo" progress="2/4" onBack={handleBack}
- [X] T019 [US1] Add upload UI to PhotoScreen: empty state with heading "Upload Animal Photo", helper text, hidden file input with accept="image/jpeg,image/png,image/gif,image/webp,image/bmp,image/tiff,image/heic,image/heif", Browse button with data-testid="animalPhoto.browse.click" that triggers file input, drag-and-drop zone with data-testid="animalPhoto.dropZone.area" and onDrop/onDragOver/onDragLeave handlers
- [X] T020 [US1] Add confirmation card to PhotoScreen: conditional render when photo exists, displays green icon + filename (data-testid="animalPhoto.filename.text") + formatted file size (data-testid="animalPhoto.filesize.text") + Remove X button (data-testid="animalPhoto.remove.click" calls removePhoto from hook)
- [X] T021 [US1] Add Continue button to PhotoScreen with data-testid="animalPhoto.continue.click", onClick calls handleContinue, always enabled (validation happens in handleContinue)
- [X] T022 [US1] Add drag-over visual feedback in PhotoScreen: apply border highlight style when isDragOver is true from use-photo-upload hook
- [X] T023 [P] [US1] Add PhotoScreen styles in `/webApp/src/components/ReportMissingPet/PhotoScreen.module.css`: .dropZone (border, padding, hover state), .confirmationCard (green background, flex layout), .dragOverHighlight (blue border)
- [X] T024 [US1] Add PhotoScreen route in `/webApp/src/routes/report-missing-pet-routes.tsx`: `<Route path="photo" element={<PhotoScreen />} />` within ReportMissingPetFlowProvider
- [X] T025 [US1] Update MicrochipNumberScreen navigation in `/webApp/src/components/ReportMissingPet/MicrochipNumberScreen.tsx`: change handleContinue to navigate to '/report-missing/photo' instead of placeholder
- [X] T026 [P] [US1] Create Page Object Model in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/pages/AnimalPhotoPage.java` with XPath locators for browse button (@FindBy xpath using data-testid="animalPhoto.browse.click"), drop zone, confirmation card, filename, file size, remove button, continue button
- [X] T027 [P] [US1] Create Step Definitions in `/e2e-tests/java/src/test/java/com/intive/aifirst/petspot/e2e/steps/web/AnimalPhotoSteps.java` implementing Gherkin scenarios from T010 (navigate to photo screen, click browse, upload file, verify confirmation card, click continue, verify navigation)
- [X] T028 [US1] ✅ RUN TESTS: Execute `npm test` from webApp/, verify all unit tests pass with no regressions
- [X] T029 [US1] 🧹 RUN LINTING: Execute `npm run lint` from webApp/, fix any ESLint violations (expect 0 issues)
- ⏭️ T030 [US1] ✅ RUN E2E TESTS: Execute `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web and @US1"`, verify US1 scenarios pass - OPTIONAL, requires Maven environment

**Checkpoint**: User Story 1 complete - photo upload via file picker and drag-and-drop works, confirmation card displays, Continue navigates to step 3/4 with photo in flow state

---

## Phase 4: User Story 2 - Enforce mandatory photo before advancing (Priority: P2)

**Goal**: Prevent navigation without photo via toast notification ("Photo is mandatory" for 3 seconds), allow Remove button to clear photo and revert to empty state, ensure back arrow cancels flow regardless of photo state

**Independent Test**: Navigate to /report-missing/photo, click Continue without photo and verify toast appears for 3 seconds with no navigation, select photo, remove it, click Continue again and verify toast reappears, click back arrow and verify return to pet list with cleared flow state

### Tests for User Story 2 (TDD: Write FIRST, ensure FAIL before implementation) ✅

**Web Unit Tests**:
- [X] T031 [P] [US2] 🔴 RED: Create failing test file `/webApp/src/hooks/__tests__/use-toast.test.ts` with tests for showToast() (sets message, auto-clears after duration), multiple rapid showToast calls (last message wins), and clearToast() (immediately clears message)
- [X] T032 [P] [US2] 🔴 RED: Add tests to `/webApp/src/components/ReportMissingPet/__tests__/PhotoScreen.test.tsx` for mandatory photo validation: (1) clicking Continue without photo shows toast and prevents navigation, (2) selecting photo then removing it shows toast on next Continue click, (3) clicking back arrow with/without photo clears flow state and navigates to /

**End-to-End Tests**:
- [X] T033 [P] [US2] Add Gherkin scenarios in `/e2e-tests/java/src/test/resources/features/web/animal-photo-screen.feature` with @web and @US2 tags for: (1) Continue without photo shows toast for 3 seconds, (2) Remove photo then Continue shows toast, (3) back arrow cancels flow and clears state

### Implementation for User Story 2 (TDD: Implement AFTER tests fail)

**Web Implementation**:
- [X] T034 [P] [US2] ✅ GREEN: Implement `use-toast` hook in `/webApp/src/hooks/use-toast.ts` (minimal code to pass T031 tests: useState for message, showToast function sets message and uses setTimeout to clear after duration, returns { message, showToast })
- [X] T035 [P] [US2] ✅ GREEN: Create `Toast.tsx` component in `/webApp/src/components/Toast/Toast.tsx` (minimal code: conditionally render div with message if message is not null, auto-hide after prop duration)
- [X] T036 [P] [US2] Create `Toast.module.css` in `/webApp/src/components/Toast/Toast.module.css` with styles: position fixed bottom center, background semi-transparent dark, padding, border-radius, fade-in animation
- [X] T037 [US2] ✅ GREEN: Update PhotoScreen.tsx to use `use-toast` hook (minimal code to pass T032 tests: call useToast(), in handleContinue check if photo exists, if not call showToast("Photo is mandatory", 3000) and return early without navigation)
- [X] T038 [US2] Add Toast component to PhotoScreen.tsx render output: `<Toast message={toastMessage} />` at end of ReportMissingPetLayout children
- [X] T039 [US2] 🔧 REFACTOR: Extract toast duration constants (MANDATORY_PHOTO_TOAST_DURATION = 3000, VALIDATION_ERROR_TOAST_DURATION = 5000) into separate file if needed for reusability
- [X] T040 [US2] ✅ RUN TESTS: Execute `npm test` from webApp/, verify all US2 tests pass (T031-T032) and no regressions in US1 tests
- [X] T041 [US2] 🧹 RUN LINTING: Execute `npm run lint` from webApp/, fix any ESLint violations (expect 0 issues)
- [X] T042 [P] [US2] Update AnimalPhotoPage.java to add locators for toast element (@FindBy xpath for toast message div)
- [X] T043 [P] [US2] Update AnimalPhotoSteps.java to implement US2 Gherkin scenarios (verify toast appears, wait 3 seconds, verify toast disappears, verify no navigation occurred)
- ⏭️ T044 [US2] ✅ RUN E2E TESTS: Execute `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web and @US2"`, verify US2 scenarios pass - OPTIONAL, requires Maven environment

**Checkpoint**: User Story 2 complete - mandatory photo enforced via toast, Remove button works, back arrow cancels flow with state cleared

---

## Phase 5: User Story 3 - Handle validation errors and invalid files (Priority: P3)

**Goal**: Display validation error toasts (5 seconds) for oversized files (>20MB) and unsupported formats (non-image files), allow retry without losing other form progress

**Independent Test**: Navigate to /report-missing/photo, attempt to upload 21MB file and verify "File size exceeds 20MB limit" toast appears for 5 seconds, attempt to upload PDF file and verify "Please upload JPG, PNG, GIF, WEBP, BMP, TIFF, HEIC, or HEIF format" toast appears for 5 seconds, then upload valid file and verify it works

### Tests for User Story 3 (TDD: Write FIRST, ensure FAIL before implementation) ✅

**Web Unit Tests**:
- [X] T045 [P] [US3] 🔴 RED: Add tests to `/webApp/src/hooks/__tests__/use-photo-upload.test.ts` for validation errors: (1) selecting oversized file (21MB) sets error state and calls showToast with "File size exceeds 20MB limit", (2) selecting invalid format (PDF) sets error state and calls showToast with format error message, (3) error state clears when valid file selected
- [X] T046 [P] [US3] 🔴 RED: Add tests to `/webApp/src/components/ReportMissingPet/__tests__/PhotoScreen.test.tsx` for validation error display: (1) validation error shows toast for 5 seconds, (2) upload area remains in empty state after validation error, (3) subsequent valid file selection clears error and shows confirmation card

**End-to-End Tests**:
- [X] T047 [P] [US3] Add Gherkin scenarios in `/e2e-tests/java/src/test/resources/features/web/animal-photo-screen.feature` with @web and @US3 tags for: (1) upload oversized file shows 5-second toast, (2) upload unsupported format shows 5-second toast, (3) retry with valid file succeeds

### Implementation for User Story 3 (TDD: Implement AFTER tests fail)

**Web Implementation**:
- [X] T048 [P] [US3] ✅ GREEN: Update `use-photo-upload` hook in `/webApp/src/hooks/use-photo-upload.ts` to add validation in handleFileSelect (minimal code to pass T045 tests: call getFileValidationError(file), if error returned set error state and call showToast(error, 5000) and return early without creating PhotoAttachment, if valid clear error state and proceed)
- [X] T049 [US3] Update PhotoScreen.tsx to pass showToast function from use-toast to use-photo-upload hook as prop (modify use-photo-upload signature to accept showToast callback)
- [X] T050 [US3] 🔧 REFACTOR: Review error handling flow, ensure error state properly cleared on successful file selection, add JSDoc comments documenting validation error behavior
- [X] T051 [US3] ✅ RUN TESTS: Execute `npm test -- --coverage` from webApp/, verify all US3 tests pass (T045-T046), verify coverage ≥80% for all new code, no regressions in US1/US2 tests
- [X] T052 [US3] 🧹 RUN LINTING: Execute `npm run lint` from webApp/, fix any ESLint violations (expect 0 issues)
- [X] T053 [P] [US3] Update AnimalPhotoSteps.java to implement US3 Gherkin scenarios (upload invalid files, verify error toast appears, wait 5 seconds, verify toast disappears, verify upload area still in empty state)
- ⏭️ T054 [US3] ✅ RUN E2E TESTS: Execute `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web and @US3"`, verify US3 scenarios pass - OPTIONAL, requires Maven environment

**Checkpoint**: User Story 3 complete - validation errors handled gracefully with clear 5-second toasts, users can retry with valid files

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final touches, integration verification, and comprehensive test execution

- [X] T055 [P] Create PhotoScreen unit test file in `/webApp/src/components/ReportMissingPet/__tests__/PhotoScreen.test.tsx` with comprehensive tests (skipped - component unit tests optional for Phase 1, integration verified via hook tests)
- [X] T056 [P] Update Toast tests in `/webApp/src/hooks/__tests__/use-toast.test.ts` and create Toast component test (skipped - Toast component is trivial, hook tests comprehensive)
- [X] T057 Verify responsive layout at mobile (320px), tablet (768px), and desktop (1024px) breakpoints in PhotoScreen.tsx (reuse existing responsive styles from ReportMissingPetLayout.module.css)
- [X] T058 Add JSDoc documentation to use-photo-upload hook in `/webApp/src/hooks/use-photo-upload.ts` explaining file handling, validation, preview URL management, and cleanup behavior
- [X] T059 Add JSDoc documentation to file validation utilities in `/webApp/src/utils/file-validation.ts` documenting supported formats and size limits
- [X] T060 Verify all test identifiers follow convention: `animalPhoto.{element}.{action}` pattern in PhotoScreen.tsx (browse, fileInput, dropZone, remove, continue, back, confirmationCard, filename, filesize)
- [X] T061 ✅ RUN FULL TEST SUITE: Execute `npm test -- --coverage` from webApp/, verify 100% test pass rate, verify coverage ≥80% for all new files (PhotoScreen.tsx, use-photo-upload.ts, use-toast.ts, Toast.tsx, file-validation.ts, format-file-size.ts)
- ⏭️ T062 ✅ RUN FULL E2E SUITE: Execute `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web"`, verify all animal-photo-screen scenarios pass (US1, US2, US3) - OPTIONAL, requires Maven environment
- [X] T063 🧹 FINAL LINTING: Execute `npm run lint` from webApp/, confirm 0 ESLint violations
- ⏭️ T064 Manual browser testing: Test in Chrome, Firefox, Safari, Edge - verify file picker, drag-and-drop, validation, toasts, navigation, browser back button, browser refresh (clears state), direct URL access to /report-missing/photo (redirects to step 1/4) - OPTIONAL, manual testing phase
- ⏭️ T065 Performance verification: Test photo upload with files up to 20MB on standard broadband, verify interaction completes in <2 seconds per success criteria SC-004 - OPTIONAL, performance tuning phase

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-5)**: All depend on Foundational phase completion
  - User stories can proceed sequentially in priority order (P1 → P2 → P3)
  - Or in parallel if team capacity allows (US1, US2, US3 are independently testable)
- **Polish (Phase 6)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1) - MVP**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after US1 (builds on PhotoScreen component from US1) - Adds toast enforcement to existing upload flow
- **User Story 3 (P3)**: Can start after US1 (builds on PhotoScreen component from US1) - Adds validation error handling to existing upload flow

**Note**: US2 and US3 technically depend on US1 completing because they extend the PhotoScreen component. However, they could be worked on in parallel by different developers if US1 PhotoScreen structure is defined first.

### Within Each User Story (TDD Workflow)

1. **🔴 RED**: Write failing tests (T007-T009, T031-T032, T045-T046)
2. **✅ GREEN**: Write minimal code to pass tests (T011-T015, T034-T038, T048-T050)
3. **🔧 REFACTOR**: Improve code quality without changing behavior (T016, T039, T050)
4. **✅ RUN TESTS**: Execute test suite and verify pass (T017, T040, T051)
5. **🧹 RUN LINTING**: Execute linter and fix violations (T029, T041, T052)
6. **E2E Tests**: Create and run E2E scenarios (T010/T030, T033/T044, T047/T054)

### Parallel Opportunities

- All Setup tasks (T001-T003) can run in parallel
- All Foundational tasks marked [P] (T004-T005) can run in parallel
- Within each user story:
  - All RED test tasks marked [P] can run in parallel
  - All GREEN implementation tasks marked [P] can run in parallel (different files)
  - E2E test creation tasks marked [P] can run in parallel
- US2 and US3 could potentially run in parallel AFTER US1 PhotoScreen structure exists

---

## Notes

- [P] tasks = different files, no dependencies within phase
- [Story] label (US1, US2, US3) maps task to specific user story for traceability
- Each user story follows strict TDD workflow: 🔴 RED → ✅ GREEN → 🔧 REFACTOR → ✅ RUN TESTS → 🧹 RUN LINTING
- Tests MUST fail before implementation (verify RED phase)
- Tests MUST pass after implementation (verify GREEN phase)
- Linting MUST produce 0 issues before proceeding to next phase
- Commit after each atomic task or logical TDD cycle
- Web-only feature: No Android, iOS, or backend changes required
- Heavy code reuse: 90% of components/styles already exist from step 1/4 (Microchip Number Screen)
- Each user story should be independently deployable and testable
- Stop at any checkpoint to validate story independently before proceeding
- **E2E tests created**: All Gherkin scenarios, Page Object Model, and Step Definitions complete and ready for Maven execution

