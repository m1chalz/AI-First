# Feature Specification: Animal Photo Screen

**Feature Branch**: `020-animal-photo-screen`  
**Created**: 2025-11-26  
**Status**: Draft  
**Input**: User description: "Create specification 020 for Animal photo screen. Two Figma references (add photo: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-7991&m=dev and after photo added: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8041&m=dev). Flow aligns with specification 017; attaching a photo is now mandatory, Browse opens the OS picker limited to image files, selected photo persists until the flow completes, Back buttons return to the prior step, attempting to continue without a photo must show an error popup, and the confirmation state uses the simple icon treatment shown in node 297-8041 (no live preview)."

## Clarifications

### Session 2025-11-26

- Q: Which exact formats should the “Browse” picker treat as valid? → A: Option D – JPG, PNG, HEIC, GIF, WEBP
- Q: What maximum file size should validation enforce for the required photo? → A: Option D – No explicit limit

## User Scenarios & Testing *(mandatory)*

<!--
  IMPORTANT: User stories should be PRIORITIZED as user journeys ordered by importance.
  Each user story/journey must be INDEPENDENTLY TESTABLE - meaning if you implement just ONE of them,
  you should still have a viable MVP (Minimum Viable Product) that delivers value.
  
  Assign priorities (P1, P2, P3, etc.) to each story, where P1 is the most critical.
  Think of each story as a standalone slice of functionality that can be:
  - Developed independently
  - Tested independently
  - Deployed independently
  - Demonstrated to users independently
-->

### User Story 1 - Attach animal photo and confirm (Priority: P1)

Caregivers in the missing pet flow open the Animal Photo screen, choose “Browse,” select a valid image file, see the confirmation icon plus filename, and proceed knowing the image will travel with the report.

**Why this priority**: A recognizable photo dramatically improves the chance that other users identify the missing pet; attaching it is now mandatory to publish the case.

**Independent Test**: Launch the Animal Photo screen, attach one supported image file, confirm the icon state and stored file details without touching other flow steps.

**Acceptance Scenarios**:

1. **Given** the user is on the Animal Photo screen with no image selected, **When** they tap “Browse” and pick a supported image file, **Then** the OS picker closes, the confirmation state from Figma node 297-8041 (icon + filename text) appears, and the Continue CTA becomes available.
2. **Given** the user already selected a photo, **When** they navigate away to another screen in the flow and return, **Then** the same confirmation icon and file details reappear without requiring re-upload.

---

### User Story 2 - Enforce photo before continuing (Priority: P2)

Reporters who try to advance without attaching a supported image file (JPG, PNG, HEIC, GIF, or WEBP) receive an inline modal/popup explaining the requirement and remain on the screen until a photo is selected.

**Why this priority**: Validation preserves data quality and ensures every missing pet report contains a visual reference, which the business has deemed mandatory.

**Independent Test**: Start the Animal Photo screen, attempt to press Continue without a file, verify the error flow, then add a photo and continue successfully.

**Acceptance Scenarios**:

1. **Given** the user landed on the Animal Photo screen with no file selected, **When** they tap Continue, **Then** an error popup appears describing that one of the supported formats (JPG/PNG/HEIC/GIF/WEBP) is required and the flow does not advance.
2. **Given** the error popup is visible, **When** the user confirms/dismisses it and attaches a valid image file, **Then** the popup disappears, the confirmation icon appears, and Continue works normally.

---

### User Story 3 - Replace photo or go back (Priority: P3)

Users can revisit the previous step (Back button or navbar back) to adjust earlier answers, return to the Animal Photo screen, and replace the image before final submission.

**Why this priority**: Allowing edits before submission prevents support overhead and ensures the final report is accurate without restarting the entire flow.

**Independent Test**: Select a photo, navigate back using both Back affordances, return, and pick a different supported format (JPG/PNG/HEIC/GIF/WEBP) to verify the state refresh.

**Acceptance Scenarios**:

1. **Given** a user selected a photo, **When** they tap the on-screen Back button or the navbar back control, **Then** they are returned to the immediately preceding step of the missing pet flow defined in spec 017.
2. **Given** the user re-enters the Animal Photo screen, **When** they tap Browse again and choose a different supported image, **Then** the confirmation icon state updates to reflect the new file while the previous selection is discarded and the mandatory validation remains satisfied.

---

### Edge Cases

- User cancels the OS file picker: the screen should stay unchanged, Continue remains disabled, and helper text reiterates that a photo is required.  
- User attempts to pick a non-image file: selection is rejected with guidance to choose a supported image type (photo stays untouched if one was already chosen).  
- Network becomes unavailable after selecting a photo: persist the local reference and surface the connectivity warning only when the flow later needs to upload.  
- User refreshes the app or returns after device lock before finishing the flow: the in-progress session should restore the previously selected photo details/icon so the mandatory requirement stays satisfied.

## Requirements *(mandatory)*

- **FR-001**: The Animal Photo screen MUST be inserted after the step defined in specification 017 and clearly state that adding a picture is required.  
- **FR-002**: Selecting “Browse” MUST open the native OS picker constrained to JPG, PNG, HEIC, GIF, or WEBP files only, preventing non-image selections.  
- **FR-003**: After a valid image is chosen, the screen MUST display the confirmation state from Figma design 297-8041 (icon placeholder + filename + helper text) while enabling Continue.  
- **FR-004**: Attempting to tap Continue without an attached photo MUST open a blocking popup/modal detailing the requirement and prevent navigation until a valid image is selected.  
- **FR-005**: The selected photo (file reference and displayed metadata/icon) MUST persist for the entire missing pet flow session, surviving navigation away and back until the flow completes or is cancelled.  
- **FR-006**: Users MUST be able to re-open the picker and replace the existing photo; the most recent valid selection becomes the one stored with the report and keeps Continue enabled.  
- **FR-007**: The screen MUST present Back controls (in-content button and navbar affordance) that always return to the immediately preceding flow step without losing previously completed inputs.  
- **FR-008**: If the user cancels the picker or chooses an invalid file, the UI MUST explain the issue, keep the prior state untouched, and allow another attempt; Continue remains disabled until a valid file exists.  
- **FR-009**: Once the flow ends (submission or cancellation), any transient copies of the photo MUST be cleared unless the report is successfully stored according to backend policies.
- **FR-010**: The Animal Photo screen MUST NOT impose a client-side maximum file size; any supported image format is accepted regardless of size, and backend policies handle oversized uploads later in the flow.

### Key Entities *(include if feature involves data)*

- **Animal Photo Attachment**: Represents the mandatory supported image (JPG/PNG/HEIC/GIF/WEBP) selected for the missing pet report; attributes include filename, file size, simple confirmation icon state, and storage status (in-session vs. uploaded).  
- **Missing Pet Flow Session**: Captures the user’s in-progress reporting journey; stores references to previously completed steps (location, description, photo) so the user can navigate backward without data loss.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: File validation rejects 100% of non-image selections during QA without requiring engineering assistance.  
- **SC-002**: 95% of sessions that include a selected photo still show the attached file details/icon after navigating away and returning before submission.  
- **SC-003**: Support tickets related to “unable to add photo” for the missing pet flow decrease by at least 50% compared to the previous release despite the mandatory rule.

## Assumptions

- Persistence “until the flow is finished” refers to the single user session covering every step of the missing pet report; data may be cleared once the report is submitted or the user intentionally abandons the flow.  
- All copy and visual treatments will follow the referenced Figma nodes (297-7991 for empty state, 297-8041 for populated state); product design will supply localized strings if needed, including the mandatory error message and the confirmation icon label copy.
