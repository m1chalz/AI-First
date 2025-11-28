# Feature Specification: iOS Animal Photo Screen

**Feature Branch**: `028-ios-animal-photo-screen`  
**Created**: 2025-11-27  
**Status**: Draft  
**Input**: User description: "na podstawie brancha 020-animal-photo-screen stwórz mi brancha 028-ios-animal-photo-screen, gdzie będziemy doszlifowywać specyfikację pod kątem iOS, nie dotykając innych platform"

## Scope & Background

- Builds on the cross-platform baseline from `020-animal-photo-screen`, but narrows scope to the iOS missing pet flow (spec 017) and the SwiftUI/UIKit coordinator stack only.  
 - Aligns UI with Figma nodes `297:7991` (empty state) and `297:8041` (confirmation state) using SwiftUI `PhotosPicker`; colors, spacing, and copy must match those references.  
- Assumes Android, Web, and backend flows remain untouched; this work may reference their shared behaviour but MUST NOT introduce requirements for those platforms.  
- Focus areas: SwiftUI PhotosPicker integration (iOS 16+ requirement), persistence within the iOS draft session, validation copy that follows Apple HIG, and parity with the mandatory photo rule introduced in feature 020.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Attach photo via SwiftUI PhotosPicker (Priority: P1)

iOS reporters reach the Animal Photo step, tap “Browse,” select a supported image through SwiftUI `PhotosPicker` (no explicit permission prompt), and immediately see the confirmation card that mirrors Figma node `297:8041` before continuing.

**Why this priority**: Without a photo the report cannot be submitted (business mandate from feature 020), so iOS must guarantee the happiest path is frictionless.

**Independent Test**: Launch the missing pet flow on iOS, jump straight to the Animal Photo step, perform a single photo selection, and confirm the UI and CTA states without touching other features.

**Acceptance Scenarios**:

1. **Given** the user is on the Animal Photo screen with no selection, **When** they tap “Browse” and pick a JPG/PNG/HEIC/GIF/WEBP asset from the iOS Photos picker, **Then** the picker dismisses, the confirmation card (icon, filename, size, remove affordance) renders exactly like node `297:8041`, and tapping Continue now advances to the Description step without additional prompts.  
2. **Given** the user already attached a photo, **When** they navigate back to prior steps and return to Animal Photo, **Then** the same confirmation card reappears without asking for another upload.

---

### User Story 2 - Enforce mandatory photo before advancing (Priority: P2)

People who ignore the requirement (e.g., mash Continue without attaching anything or remove an existing file) see a non-blocking toast that repeats the rule instead of a disabled button, so the flow feels responsive while still preventing navigation without a valid photo.

**Why this priority**: Consistent enforcement on iOS protects data quality and keeps parity with other platforms that already require photos.

**Independent Test**: Attempt to leave the screen without a photo, observe the alert copy, then satisfy the rule and confirm Continue works again.

**Acceptance Scenarios**:

1. **Given** no photo is selected, **When** the user taps Continue, **Then** the app stays on the Animal Photo screen, a toast anchored near the bottom displays “Photo is mandatory” for 3 seconds, and no navigation occurs.  
2. **Given** a photo is attached and Continue would normally advance, **When** the user taps the “Remove” (X icon) action, **Then** the confirmation card disappears, helper text reiterates the requirement, Continue remains enabled, and any subsequent Continue tap shows the same toast until a new file is selected.

---

### User Story 3 - Recover from picker or asset issues (Priority: P3)

iOS users who cancel `PhotosPicker`, attempt to load iCloud-only assets, or hit transfer errors receive clear guidance, retain existing progress, and can resume the flow without starting over.

**Why this priority**: Permission friction is a top drop-off reason in iOS forms; we need resilient messaging so support isn’t flooded with “can’t upload photo” tickets.

**Independent Test**: Simulate a picker cancellation and an iCloud download failure in isolation to confirm the UI responds with the correct banners/menus while preserving prior data.

**Acceptance Scenarios**:

1. **Given** the user cancels PhotosPicker, **When** they dismiss the sheet, **Then** the screen remains on the empty state with helper text, Continue stays enabled, and tapping it shows the toast until a photo is picked.  
2. **Given** the user selects an asset that is still downloading from iCloud or fails mid-transfer, **When** the download completes, **Then** the UI shows the confirmation card; **When** the transfer errors, **Then** the confirmation card simply disappears (no error copy), and any Continue tap shows the toast until the user chooses a new file.

---

### Edge Cases

- User cancels the Photos picker: remain on the empty state, show helper text, keep Continue enabled, and surface the 3-second toast if they try to Continue without selecting a photo.  
- Device resumes from background after >30 minutes: previously selected photo metadata/card must still render because the draft session persists in local storage.  
- Live Photos or HEIC assets with depth data: treat them as static images (first frame) and reflect the chosen filename/size without conversion prompts.  
- Storage almost full on device: surface the OS error while keeping the last good selection; prevent data loss.  
- VoiceOver enabled: announce upload instructions, filename, size, and the Remove control with clear accessibility labels.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The iOS Animal Photo screen MUST appear as step 2/4 in the missing pet flow (spec 017) and visually match Figma nodes `297:7991` (empty) and `297:8041` (attached) including typography, button spacing, and helper copy.  
- **FR-002**: The “Browse” button MUST use SwiftUI `PhotosPicker` scoped to image media types only (JPG, PNG, HEIC, GIF, WEBP). Devices below iOS 16 are out of scope for this feature.  
- **FR-003**: After a supported image is selected, the UI MUST render the confirmation card (icon, filename, size, remove control) and enable the Continue CTA while storing the selection inside the iOS missing pet draft session so it survives navigation and app backgrounding.  
- **FR-004**: Tapping Continue without a stored photo MUST leave the CTA enabled but surface a toast message that literally reads “Photo is mandatory” for 3 seconds and prevent navigation.  
- **FR-005**: The Remove (X) control MUST clear the stored photo, revert the screen to the empty state, and ensure any subsequent Continue tap replays the “Photo is mandatory” toast until another valid file is selected, while retaining other step data.  
- **FR-006**: Because `PhotosPicker` does not require Photos Library permission, the app MUST NOT request `PHPhotoLibrary` authorization directly; any access issues rely on picker-provided flows. The UI MAY still show helper copy if `PhotosPicker` reports `.userCancelled` or `TransferError.notAvailable`.  
- **FR-007**: When the selected asset resides in iCloud, the UI MUST show a non-blocking loading indicator and only allow navigation forward once the download completes—Continue taps during loading should either be ignored or show the “Photo is mandatory” toast until the cached metadata is ready. If the download fails or the transfer errors at any stage, the selection MUST be cleared automatically with no additional messaging, and the user must re-select a photo before proceeding (the toast will appear on Continue until they do).  
- **FR-008**: All labels, helper copy, and error messaging MUST follow Apple localization casing rules and be supplied via the iOS strings catalog so translations stay consistent with other steps.  
- **FR-009**: The screen MUST expose accessibility identifiers following the `{screen}.{element}` convention (e.g., `animalPhoto.browse`, `animalPhoto.remove`, `animalPhoto.continue`) for automated UI tests.  

### Key Entities *(include if feature involves data)*

- **iOS Missing Pet Draft Session**: Holds user progress for each step (location, description, photo) while the report is in progress; persists across in-app navigation and short backgrounding intervals.  
- **Photo Attachment State**: Encapsulates the selected image metadata (filename, size, asset identifier, download status) plus UI flags (empty, loading, confirmed, error).  
- **Picker Status Context**: Tracks whether `PhotosPicker` is presenting, cancelled, or currently downloading iCloud assets so the UI can show helper copy/toasts as needed.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: During QA, 100% of attempts to tap Continue without a photo display the “Photo is mandatory” toast for 3 seconds and prevent forward navigation until a photo is provided.  
- **SC-002**: 95% of iOS draft sessions that include a selected photo still display the confirmation card after navigating away and back or after a background/resume shorter than one hour.  
- **SC-003**: At least 90% of testers who cancel the picker or encounter iCloud download issues can recover without manual support by following the inline guidance and retry affordances.  
- **SC-004**: Support tickets tagged “iOS photo upload missing” drop by 40% within one month of release compared to the prior version.

## Assumptions

- Only the Photos picker (“Browse”) is required for this milestone; capturing a new photo with the camera is deferred.  
- Image format validation relies on `PhotosPicker` filters; no additional compression or client-side resizing is introduced for iOS.  
- Copywriting, localization, and icon assets will be delivered by Product Design and Content teams before implementation begins.  
- Backend upload policies (no size limit, formats allowed) remain identical to those defined in feature 020; this spec only covers the iOS front-end experience.
- This feature requires iOS 16+ (PhotosPicker-only). Devices running iOS 15 or earlier will continue to show the previous placeholder experience until the OS is upgraded.
