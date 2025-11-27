# Feature Specification: iOS Animal Photo Screen

**Feature Branch**: `028-ios-animal-photo-screen`  
**Created**: 2025-11-27  
**Status**: Draft  
**Input**: User description: "na podstawie brancha 020-animal-photo-screen stwórz mi brancha 028-ios-animal-photo-screen, gdzie będziemy doszlifowywać specyfikację pod kątem iOS, nie dotykając innych platform"

## Scope & Background

- Builds on the cross-platform baseline from `020-animal-photo-screen`, but narrows scope to the iOS missing pet flow (spec 017) and the SwiftUI/UIKit coordinator stack only.  
- Aligns UI with Figma nodes `297:7991` (empty state) and `297:8041` (confirmation state); colors, spacing, and copy must match those references.  
- Assumes Android, Web, and backend flows remain untouched; this work may reference their shared behaviour but MUST NOT introduce requirements for those platforms.  
- Focus areas: native Photos permission handling, persistence within the iOS draft session, validation copy that follows Apple HIG, and parity with the mandatory photo rule introduced in feature 020.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Attach photo via native picker (Priority: P1)

iOS reporters reach the Animal Photo step, tap “Browse,” grant Photos access if needed, pick a supported image, and immediately see the confirmation card that mirrors Figma node `297:8041` before continuing.

**Why this priority**: Without a photo the report cannot be submitted (business mandate from feature 020), so iOS must guarantee the happiest path is frictionless.

**Independent Test**: Launch the missing pet flow on iOS, jump straight to the Animal Photo step, perform a single photo selection, and confirm the UI and CTA states without touching other features.

**Acceptance Scenarios**:

1. **Given** the user is on the Animal Photo screen with no selection, **When** they tap “Browse” and pick a JPG/PNG/HEIC/GIF/WEBP asset from the iOS Photos picker, **Then** the picker dismisses, the confirmation card (icon, filename, size, remove affordance) renders exactly like node `297:8041`, and Continue becomes enabled.  
2. **Given** the user already attached a photo, **When** they navigate back to prior steps and return to Animal Photo, **Then** the same confirmation card reappears without asking for another upload.

---

### User Story 2 - Enforce mandatory photo before advancing (Priority: P2)

People who ignore the requirement (e.g., mash Continue without attaching anything or remove an existing file) are blocked with a native-feeling alert and clear helper copy until a valid photo exists.

**Why this priority**: Consistent enforcement on iOS protects data quality and keeps parity with other platforms that already require photos.

**Independent Test**: Attempt to leave the screen without a photo, observe the alert copy, then satisfy the rule and confirm Continue works again.

**Acceptance Scenarios**:

1. **Given** no photo is selected, **When** the user taps Continue, **Then** a blocking modal/alert explains that a supported photo is mandatory, focus returns to the screen, and Continue stays disabled.  
2. **Given** a photo is attached and Continue is enabled, **When** the user taps the “Remove” (X icon) action, **Then** the confirmation card disappears, helper text reiterates the requirement, and Continue is disabled until a new file is selected.

---

### User Story 3 - Recover from permission or asset issues (Priority: P3)

iOS users who previously denied Photos access, encounter iCloud-only assets, or experience picker cancellation receive clear guidance, retain existing progress, and can resume the flow without starting over.

**Why this priority**: Permission friction is a top drop-off reason in iOS forms; we need resilient messaging so support isn’t flooded with “can’t upload photo” tickets.

**Independent Test**: Simulate a denied permission, a cancelled picker, and an iCloud download failure in isolation to confirm the UI responds with the correct banners while preserving prior data.

**Acceptance Scenarios**:

1. **Given** the user denied Photos permission earlier, **When** they land on the Animal Photo screen, **Then** an inline banner explains why access is needed, offers a CTA to open iOS Settings, and Continue stays disabled until access is granted and a photo is chosen.  
2. **Given** the user selects an asset that is still downloading from iCloud or fails mid-transfer, **When** the download completes or errors, **Then** the UI shows a determinate loading or error state without crashing, and the user can retry the selection without losing the rest of the form.

---

### Edge Cases

- User cancels the Photos picker: remain on the empty state, show helper text, and keep Continue disabled.  
- Device resumes from background after >30 minutes: previously selected photo metadata/card must still render because the draft session persists in local storage.  
- Live Photos or HEIC assets with depth data: treat them as static images (first frame) and reflect the chosen filename/size without conversion prompts.  
- Storage almost full on device: surface the OS error while keeping the last good selection; prevent data loss.  
- VoiceOver enabled: announce upload instructions, filename, size, and the Remove control with clear accessibility labels.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The iOS Animal Photo screen MUST appear as step 2/4 in the missing pet flow (spec 017) and visually match Figma nodes `297:7991` (empty) and `297:8041` (attached) including typography, button spacing, and helper copy.  
- **FR-002**: The “Browse” button MUST invoke the native iOS Photos picker scoped to image media types only (JPG, PNG, HEIC, GIF, WEBP) and surface the system permission prompt the first time access is requested.  
- **FR-003**: After a supported image is selected, the UI MUST render the confirmation card (icon, filename, size, remove control) and enable the Continue CTA while storing the selection inside the iOS missing pet draft session so it survives navigation and app backgrounding.  
- **FR-004**: Tapping Continue without a stored photo MUST trigger a blocking alert or sheet that explains the requirement, references supported formats, and prevents forward navigation until a valid image exists.  
- **FR-005**: The Remove (X) control MUST clear the stored photo, revert the screen to the empty state, and disable Continue while retaining any other data captured in previous steps.  
- **FR-006**: If Photos permission is denied (either initially or later via Settings), the screen MUST display inline guidance plus a CTA that deep-links to the iOS Settings page for PetSpot; Continue remains disabled until permission is restored and a photo is attached.  
- **FR-007**: When the selected asset resides in iCloud, the UI MUST show a non-blocking loading indicator and only enable Continue after the download completes; failure states must provide retry guidance without forcing the user to restart the flow.  
- **FR-008**: All labels, helper copy, and error messaging MUST follow Apple localization casing rules and be supplied via the iOS strings catalog so translations stay consistent with other steps.  
- **FR-009**: The screen MUST expose accessibility identifiers following the `{screen}.{element}.{action}` convention (e.g., `animalPhoto.browse.tap`, `animalPhoto.remove.tap`, `animalPhoto.continue.tap`) for automated UI tests.  
- **FR-010**: Upon successful submission or explicit cancellation of the missing pet flow, any locally cached photo data MUST be purged from the device cache unless the backend confirms the upload completed; this keeps sensitive media from lingering on shared devices.

### Key Entities *(include if feature involves data)*

- **iOS Missing Pet Draft Session**: Holds user progress for each step (location, description, photo) while the report is in progress; persists across in-app navigation and short backgrounding intervals.  
- **Photo Attachment State**: Encapsulates the selected image metadata (filename, size, asset identifier, download status) plus UI flags (empty, loading, confirmed, error).  
- **Permission Status Context**: Tracks whether the Photos permission is unknown, authorized, limited, or denied and determines which banner/CTA copy to show on the screen.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: During QA, 100% of attempts to tap Continue without a photo show the blocking alert and prevent forward navigation until a photo is provided.  
- **SC-002**: 95% of iOS draft sessions that include a selected photo still display the confirmation card after navigating away and back or after a background/resume shorter than one hour.  
- **SC-003**: At least 90% of testers who initially deny Photos permission can complete the step without manual support by following the inline banner guidance.  
- **SC-004**: Support tickets tagged “iOS photo upload missing” drop by 40% within one month of release compared to the prior version.

## Assumptions

- Only the Photos picker (“Browse”) is required for this milestone; capturing a new photo with the camera is deferred.  
- Image format validation relies on the Photos picker filters; no additional compression or client-side resizing is introduced for iOS.  
- Copywriting, localization, and icon assets will be delivered by Product Design and Content teams before implementation begins.  
- Backend upload policies (no size limit, formats allowed) remain identical to those defined in feature 020; this spec only covers the iOS front-end experience.
