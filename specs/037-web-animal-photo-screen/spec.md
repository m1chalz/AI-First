# Feature Specification: Web Animal Photo Screen

**Feature Branch**: `037-web-animal-photo-screen`  
**Created**: 2025-12-01  
**Status**: Draft  
**Input**: User description: "na podstawie specyfikacji 028 przygotuj podobną dla aplikacji webowej. design w figmie: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=315-15775&m=dev"

## Scope & Background

- Builds on the iOS baseline from `028-ios-animal-photo-screen`, adapting the photo upload experience for the web platform (React + TypeScript).
- Aligns UI with Figma node `315:15775` using HTML5 file input with drag-and-drop support; colors, spacing, and copy must match the design reference.
- Assumes Android and iOS flows remain untouched; this work focuses exclusively on the web missing pet flow.
- Focus areas: HTML5 file input integration with drag-and-drop, client-side validation (format, size), persistence within the web draft session, and parity with the mandatory photo rule from the mobile implementations.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Upload photo via file picker or drag-and-drop (Priority: P1)

Web users reach the Animal Photo step (step 2/4), click "Browse" to open the native file picker OR drag-and-drop an image directly onto the upload area, and immediately see the confirmation card showing filename and size before continuing.

**Why this priority**: Without a photo the report cannot be submitted (business mandate), so the web experience must provide multiple upload methods for user convenience and accessibility.

**Independent Test**: Navigate to the Animal Photo step in the web app, perform a single photo selection via file picker or drag-and-drop, and confirm the UI state changes without touching other features.

**Acceptance Scenarios**:

1. **Given** the user is on the Animal Photo screen with no selection, **When** they click "Browse" and select a JPG/PNG/GIF/WEBP file (max 10MB) from the file picker, **Then** the picker closes, the confirmation card (green icon, filename, size, remove X button) renders matching node `315:15775`, and clicking Continue advances to the Description step without additional prompts.
2. **Given** the user is on the empty state, **When** they drag a supported image file over the upload area and drop it, **Then** the confirmation card appears with the same visual treatment as file picker selection.
3. **Given** the user already attached a photo, **When** they navigate back to prior steps and return to Animal Photo, **Then** the same confirmation card reappears without asking for another upload.

---

### User Story 2 - Enforce mandatory photo before advancing (Priority: P2)

Users who try to continue without attaching a photo or who remove an existing photo see clear validation feedback that prevents navigation while keeping the Continue button enabled, maintaining a responsive feel.

**Why this priority**: Consistent enforcement on web protects data quality and keeps parity with mobile platforms that already require photos.

**Independent Test**: Attempt to leave the screen without a photo, observe the validation message, then satisfy the rule and confirm Continue works.

**Acceptance Scenarios**:

1. **Given** no photo is selected, **When** the user clicks Continue, **Then** the app stays on the Animal Photo screen, a toast notification displays "Photo is mandatory" for 3 seconds near the bottom, and no navigation occurs.
2. **Given** a photo is attached, **When** the user clicks the Remove (X) button, **Then** the confirmation card disappears, helper text reiterates the requirement, Continue remains enabled, and any subsequent Continue click shows the toast until a new file is selected.

---

### User Story 3 - Handle validation errors and invalid files (Priority: P3)

Web users who attempt to select unsupported formats or files exceeding 10MB receive clear validation feedback and can retry without losing other form progress.

**Why this priority**: Clear validation feedback reduces support tickets and prevents user frustration during file selection.

**Independent Test**: Simulate various validation errors (oversized file, wrong format) in isolation to confirm the UI responds with appropriate error messages while preserving other form data.

**Acceptance Scenarios**:

1. **Given** the user selects a file larger than 10MB, **When** they attempt to upload, **Then** an error toast displays "File size exceeds 10MB limit" for 5 seconds, the upload area remains in empty state, and they can select a different file.
2. **Given** the user selects an unsupported format (e.g., PDF, TXT, BMP), **When** the file validation runs, **Then** an error toast displays "Please upload JPG, PNG, GIF, or WEBP format" for 5 seconds and the selection is rejected.

---

### Edge Cases

- User cancels the file picker: remain on the empty state, show helper text, keep Continue enabled, display the 3-second toast if they try to Continue without selecting a photo.
- Browser tab is closed and reopened: previously selected photo metadata (filename, size, preview URL) must still render if the draft session is persisted in localStorage/sessionStorage.
- Multiple rapid file selections: only the most recent valid selection should be displayed in the confirmation card.
- File with special characters in name: properly escape and display the filename without breaking the UI.
- Keyboard-only navigation: all upload interactions (Browse button, Remove button, Continue button) must be accessible via keyboard (Tab, Enter, Space) and announce properly to screen readers.
- Mobile browser on tablet/phone: file picker should work with device camera/photo library as appropriate for the browser.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The Web Animal Photo screen MUST appear as step 2/4 in the missing pet flow and visually match Figma node `315:15775` including typography, colors, button spacing, and helper copy.
- **FR-002**: The "Browse" button MUST trigger an HTML5 file input limited to image types (accept="image/jpeg,image/png,image/gif,image/webp") with a maximum file size of 10MB validated on the client side.
- **FR-003**: The upload area MUST support drag-and-drop for image files, providing visual feedback (e.g., border highlight) when a file is dragged over the drop zone.
- **FR-004**: After a supported image is selected (via picker or drag-and-drop), the UI MUST render the confirmation card (green icon, filename, file size, Remove X button) and store the file metadata and file object in the web draft session so it survives navigation within the app.
- **FR-005**: Clicking Continue without a stored photo MUST leave the Continue button enabled but surface a toast notification that reads "Photo is mandatory" for 3 seconds and prevent navigation to the next step.
- **FR-006**: The Remove (X) button on the confirmation card MUST clear the stored photo, revert the screen to the empty state, and ensure any subsequent Continue click replays the "Photo is mandatory" toast until another valid file is selected, while retaining other step data.
- **FR-007**: Client-side validation MUST reject files that: exceed 10MB, are not in supported formats (JPG, PNG, GIF, WEBP), or fail to load. Each rejection MUST display a specific error toast for 5 seconds with clear guidance.
- **FR-008**: All labels, helper copy, and error messages MUST use consistent typography and styling as defined in the Figma design and follow the existing web app localization patterns.
- **FR-009**: The screen MUST expose test identifiers following the `{screen}.{element}.{action}` convention (e.g., `animalPhoto.browse.click`, `animalPhoto.remove.click`, `animalPhoto.continue.click`) using `data-testid` attributes for automated E2E tests.
- **FR-010**: The file preview (if implemented) MUST use `URL.createObjectURL()` for local file preview and properly revoke the object URL when the component unmounts or file changes to prevent memory leaks.
- **FR-011**: The component MUST be keyboard accessible with proper focus management and ARIA labels for screen reader users (e.g., "Upload animal photo, Browse button", "Remove photo", "Continue to description step").

### Key Entities *(include if feature involves data)*

- **Web Missing Pet Draft Session**: Holds user progress for each step (location, animal photo, chip number, description) while the report is in progress; persists in browser storage (localStorage or sessionStorage) to survive page refreshes within the same session.
- **Photo Attachment State**: Contains the selected file object, metadata (filename, size, MIME type), preview URL (blob URL), and validation status (pending, valid, error).
- **Upload UI State**: Tracks whether the UI is in empty state, has a file selected (confirmation card visible), is showing drag-over feedback, or displaying an error message.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: During QA, 100% of attempts to click Continue without a photo display the "Photo is mandatory" toast for 3 seconds and prevent forward navigation until a photo is provided.
- **SC-002**: 95% of web draft sessions that include a selected photo still display the confirmation card after navigating away and back or after a page refresh within the same browser session.
- **SC-003**: At least 90% of testers who encounter upload errors (wrong format, oversized file) can successfully recover and upload a valid file without manual support, as measured by task completion rates in usability testing.
- **SC-004**: The upload interaction (Browse or drag-and-drop to confirmation card) completes in under 2 seconds for files up to 10MB on standard broadband connections (10 Mbps+).
- **SC-005**: 100% of keyboard-only testers can successfully navigate to the photo upload area, trigger the file picker, and proceed to the next step using only keyboard controls.

## Assumptions

- Only file picker and drag-and-drop upload methods are required for this milestone; webcam capture is deferred to a future feature.
- Image format and size validation is performed entirely on the client side; the actual photo upload to the backend server will be handled in a separate feature (photo submission with the full report).
- The draft session persistence mechanism (localStorage vs sessionStorage) will follow existing patterns used in other web form steps.
- Design assets, copy, and localization strings will be provided by Product Design and Content teams, or extracted directly from the Figma design.
- Browser support targets modern evergreen browsers (Chrome, Firefox, Safari, Edge) with ES2015+ support; no IE11 compatibility required.
- File preview (thumbnail) is optional for this MVP; confirmation card shows filename and size only, matching the Figma design.
