# Feature Specification: Web Animal Photo Screen

**Feature Branch**: `037-web-animal-photo-screen`  
**Created**: 2025-12-01  
**Status**: Draft  
**Input**: User description: "na podstawie specyfikacji 028 przygotuj podobną dla aplikacji webowej. design w figmie: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=315-15775&m=dev"

## Clarifications

### Session 2025-12-01

- Q: How should flow state be managed across the 4-step flow? → A: React state management (Context API or custom hooks) - no persistence to localStorage/sessionStorage
- Q: Should flow state persist across browser refreshes? → A: No persistence - refresh clears all flow state (user must restart from pet list)
- Q: How should the browser's back button be handled during the flow? → A: Treat browser back same as in-app back arrow (cancel flow, return to pet list, clear state)
- Q: What should happen when a user directly accesses step 2/4 URL without starting from step 1/4? → A: Redirect to step 1 - always start flow from beginning
- Q: Which storage mechanism for photo file within active flow session? → A: In-memory React state only (no localStorage/sessionStorage) - photo survives in-flow navigation but not browser refresh
- Q: What file content validation is required beyond MIME type and file size? → A: Only MIME type and file size validation on client side - no additional content validation (e.g., magic number checking)

## Scope & Background

- Builds on the iOS baseline from `028-ios-animal-photo-screen`, adapting the photo upload experience for the web platform (React + TypeScript).
- This is step 2/4 in the same 4-step "report missing pet" flow as spec 034 (Web Microchip Number Screen). Flow management architecture MUST be consistent across all steps.
- Aligns UI with Figma node `315:15775` using HTML5 file input with drag-and-drop support; colors, spacing, and copy must match the design reference.
- Assumes Android and iOS flows remain untouched; this work focuses exclusively on the web missing pet flow.
- Focus areas: HTML5 file input integration with drag-and-drop, client-side validation (format, size), React state management for photo storage within active flow session, browser back/refresh handling, and parity with the mandatory photo rule from the mobile implementations.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Upload photo via file picker or drag-and-drop (Priority: P1)

Web users reach the Animal Photo step (step 2/4), click "Browse" to open the native file picker OR drag-and-drop an image directly onto the upload area, and immediately see the confirmation card showing filename and size before continuing.

**Why this priority**: Without a photo the report cannot be submitted (business mandate), so the web experience must provide multiple upload methods for user convenience and accessibility.

**Independent Test**: Navigate to the Animal Photo step in the web app, perform a single photo selection via file picker or drag-and-drop, and confirm the UI state changes without touching other features.

**Acceptance Scenarios**:

1. **Given** the user is on the Animal Photo screen with no selection, **When** they click "Browse" and select a JPG/PNG/GIF/WEBP/BMP/TIFF/HEIC/HEIF file (max 20MB) from the file picker, **Then** the picker closes, the confirmation card (green icon, filename, size, remove X button) renders matching node `315:15775`, and clicking Continue advances to the next step (step 3/4) without additional prompts.
2. **Given** the user is on the empty state, **When** they drag a supported image file over the upload area and drop it, **Then** the confirmation card appears with the same visual treatment as file picker selection.
3. **Given** the user already attached a photo and navigated to step 3/4, **When** they navigate back within the flow to step 2/4 (Animal Photo), **Then** the same confirmation card reappears with the previously selected photo still displayed.

---

### User Story 2 - Enforce mandatory photo before advancing (Priority: P2)

Users who try to continue without attaching a photo or who remove an existing photo see clear validation feedback that prevents navigation while keeping the Continue button enabled, maintaining a responsive feel.

**Why this priority**: Consistent enforcement on web protects data quality and keeps parity with mobile platforms that already require photos.

**Independent Test**: Attempt to leave the screen without a photo via Continue button, observe the validation message, then satisfy the rule and confirm Continue works. Also test that back arrow allows canceling the flow regardless of validation.

**Acceptance Scenarios**:

1. **Given** no photo is selected, **When** the user clicks Continue, **Then** the app stays on the Animal Photo screen, a toast notification displays "Photo is mandatory" for 3 seconds near the bottom, and no navigation occurs.
2. **Given** a photo is attached, **When** the user clicks the Remove (X) button, **Then** the confirmation card disappears, helper text reiterates the requirement, Continue remains enabled, and any subsequent Continue click shows the toast until a new file is selected.
3. **Given** the user is on step 2/4 with or without a photo, **When** they click the back arrow button in the header, **Then** the entire flow is cancelled, they return to the pet list, and all flow state (including any selected photo) is cleared.

---

### User Story 3 - Handle validation errors and invalid files (Priority: P3)

Web users who attempt to select unsupported formats or files exceeding 20MB receive clear validation feedback and can retry without losing other form progress.

**Why this priority**: Clear validation feedback reduces support tickets and prevents user frustration during file selection.

**Independent Test**: Simulate various validation errors (oversized file, wrong format) in isolation to confirm the UI responds with appropriate error messages while preserving other form data.

**Acceptance Scenarios**:

1. **Given** the user selects a file larger than 20MB, **When** they attempt to upload, **Then** an error toast displays "File size exceeds 20MB limit" for 5 seconds, the upload area remains in empty state, and they can select a different file.
2. **Given** the user selects an unsupported format (e.g., PDF, TXT, SVG), **When** the file validation runs, **Then** an error toast displays "Please upload JPG, PNG, GIF, WEBP, BMP, TIFF, HEIC, or HEIF format" for 5 seconds and the selection is rejected.

---

### Edge Cases

- User cancels the file picker: remain on the empty state, show helper text, keep Continue enabled, display the 3-second toast if they try to Continue without selecting a photo.
- Browser refresh: all flow state is cleared (including selected photo); user returns to pet list and must restart flow from step 1/4.
- Browser back button: treated same as in-app back arrow (cancels entire flow, returns to pet list, clears all flow state including photo).
- Direct URL access to step 2/4: user is redirected to step 1/4 to ensure complete data collection from the beginning.
- Multiple rapid file selections: only the most recent valid selection should be displayed in the confirmation card.
- File with special characters in name: properly escape and display the filename without breaking the UI.
- User navigates forward (step 2→3) then back (step 3→2): previously selected photo persists within the active flow session (React state).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Screen MUST display a header with back arrow button (left), "Animal photo" title (center), and "2/4" progress indicator (right)
- **FR-002**: Back arrow button MUST dismiss the entire flow, return to the pet list screen, and clear all flow state (including selected photo)
- **FR-003**: The Web Animal Photo screen MUST appear as step 2/4 in the missing pet flow and visually match Figma node `315:15775` including typography, colors, button spacing, and helper copy
- **FR-004**: The "Browse" button MUST trigger an HTML5 file input limited to image types (accept="image/jpeg,image/png,image/gif,image/webp,image/bmp,image/tiff,image/heic,image/heif") with a maximum file size of 20MB validated on the client side
- **FR-005**: The upload area MUST support drag-and-drop for image files, providing visual feedback (e.g., border highlight) when a file is dragged over the drop zone
- **FR-006**: After a supported image is selected (via picker or drag-and-drop), the UI MUST render the confirmation card (green icon, filename, file size, Remove X button) and store the file object and metadata in React flow state (Context or custom hook)
- **FR-007**: Clicking Continue without a stored photo MUST leave the Continue button enabled but surface a toast notification that reads "Photo is mandatory" for 3 seconds and prevent navigation to the next step
- **FR-008**: The Remove (X) button on the confirmation card MUST clear the stored photo from React state, revert the screen to the empty state, and ensure any subsequent Continue click replays the "Photo is mandatory" toast until another valid file is selected, while retaining other step data
- **FR-009**: Client-side validation MUST reject files that: exceed 20MB, are not in supported formats (JPG, PNG, GIF, WEBP, BMP, TIFF, HEIC, HEIF), or fail to load. Each rejection MUST display a specific error toast for 5 seconds with clear guidance
- **FR-010**: Clicking Continue with a valid photo MUST save the file object to flow state and navigate to the next screen in the flow (step 3/4)
- **FR-011**: Selected photo data MUST persist when navigating forward to step 3/4 and back to step 2/4 within the same flow session (via React state management)
- **FR-012**: All labels, helper copy, and error messages MUST use consistent typography and styling as defined in the Figma design and follow the existing web app localization patterns
- **FR-013**: The screen MUST expose test identifiers following the `{screen}.{element}.{action}` convention (e.g., `animalPhoto.browse.click`, `animalPhoto.remove.click`, `animalPhoto.continue.click`, `animalPhoto.back.click`) using `data-testid` attributes for automated E2E tests
- **FR-014**: The file preview (if implemented) MUST use `URL.createObjectURL()` for local file preview and properly revoke the object URL when the component unmounts or file changes to prevent memory leaks
- **FR-016**: Screen layout MUST be responsive and adapt to different viewport sizes (mobile: 320px+, tablet: 768px+, desktop: 1024px+)
- **FR-017**: Screen MUST display in a centered white card with border on desktop/tablet viewports (as shown in Figma design)
- **FR-018**: Flow State MUST be managed via React state management (Context API, custom hooks, or state management library)
- **FR-019**: Flow State MUST persist photo file object and metadata as user progresses through the 4-step "report missing pet" flow
- **FR-020**: Flow State MUST be cleared when user cancels the flow (clicks back arrow) or completes the flow
- **FR-021**: Browser back button MUST be handled to cancel the entire flow, return to pet list, and clear flow state (same behavior as in-app back arrow)
- **FR-022**: Browser refresh MUST clear all flow state and return user to pet list (no persistence to sessionStorage or localStorage)
- **FR-023**: Navigation MUST use React Router with URL-based routing for each flow step (each step has its own route/URL)
- **FR-024**: Direct URL access to step 2/4 without active flow state MUST redirect user to step 1/4 to ensure complete data collection from the beginning

### Key Entities *(include if feature involves data)*

- **Flow State**: Temporary session data object that persists user inputs (microchip number, animal photo file object, location, description) as user progresses through the 4-step "report missing pet" flow. Managed via React state (Context API or custom hook), passed to each step component in the flow. Cleared when user cancels or completes the flow. Not persisted to localStorage/sessionStorage - browser refresh clears all state.
- **Photo Attachment**: File object selected by user, along with metadata (filename, size, MIME type), preview URL (blob URL created via `URL.createObjectURL()`), and validation status. Stored in Flow State and survives in-flow navigation but not browser refresh.
- **Upload UI State**: Local component state tracking whether the UI is in empty state, has a file selected (confirmation card visible), is showing drag-over feedback, or displaying an error message.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: During QA, 100% of attempts to click Continue without a photo display the "Photo is mandatory" toast for 3 seconds and prevent forward navigation until a photo is provided
- **SC-002**: 100% of flow sessions that include a selected photo still display the confirmation card after navigating forward to step 3/4 and back to step 2/4 within the same active flow session (without browser refresh)
- **SC-003**: At least 90% of testers who encounter upload errors (wrong format, oversized file) can successfully recover and upload a valid file without manual support, as measured by task completion rates in usability testing
- **SC-004**: The upload interaction (Browse or drag-and-drop to confirmation card) completes in under 2 seconds for files up to 20MB on standard broadband connections (10 Mbps+)
- **SC-006**: Browser back button and refresh correctly cancel flow and clear state in 100% of test scenarios

## Assumptions

- Only file picker and drag-and-drop upload methods are required for this milestone; webcam capture is deferred to a future feature.
- Image format and size validation is performed entirely on the client side; the actual photo upload to the backend server will be handled in a separate feature (photo submission with the full report).
- Flow state management follows the same architecture as spec 034 (Web Microchip Number Screen): React state management (Context API or custom hooks), no localStorage/sessionStorage persistence, browser refresh clears all state.
- Browser back button and direct URL access handling follows the same patterns as spec 034: back button cancels flow, direct access to step 2/4 redirects to step 1/4.
- Design assets, copy, and localization strings will be provided by Product Design and Content teams, or extracted directly from the Figma design.
- Browser support targets modern evergreen browsers (Chrome, Firefox, Safari, Edge) with ES2015+ support; no IE11 compatibility required.
- File preview (thumbnail) is optional for this MVP; confirmation card shows filename and size only, matching the Figma design.
