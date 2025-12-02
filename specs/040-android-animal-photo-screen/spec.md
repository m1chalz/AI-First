# Feature Specification: Android Animal Photo Screen

**Feature Branch**: `040-android-animal-photo-screen`  
**Created**: 2025-12-02  
**Status**: Draft  
**Platform**: Android  
**Input**: User description: "Prepare the specification for Android platform basing on the 020"

## Scope & Background

- Builds on the cross-platform baseline from `020-animal-photo-screen` and the Android placeholder flow from `018-android-missing-pet-flow`, but focuses on the full Android photo selection implementation.
- Aligns UI with Figma nodes `297:7991` (empty state) and `297:8041` (confirmation state); colors, spacing, and copy must match those references.
- Assumes iOS, Web, and backend flows remain untouched; this work may reference their shared behavior but MUST NOT introduce requirements for those platforms.
- Focus areas: Android Photo Picker integration, persistence within the Android draft session through ViewModel state, validation with toast feedback, and parity with the mandatory photo rule introduced in feature 020.

## Clarifications

### Session 2025-12-02

- Q: Should the app display a loading indicator while processing the selected photo? → A: Yes, brief loading indicator (spinner/shimmer) on the card area until metadata is ready.
- Q: How long should the draft session persist if user abandons the flow? → A: Cleared when user exits the entire flow (nav graph); back navigation within the flow preserves state.
- Q: Should confirmation card show generic icon or photo thumbnail? → A: Generic icon for empty state (per Figma designs), actual photo thumbnail in confirmation card when selected.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Attach photo via Android Photo Picker (Priority: P1)

Android users reach the Animal Photo step (step 2/4), tap "Browse," select a supported image through the system Photo Picker, and immediately see the confirmation card that mirrors Figma node `297:8041` before continuing.

**Why this priority**: Without a photo the report cannot be submitted (business mandate from feature 020), so Android must guarantee the happiest path is frictionless.

**Independent Test**: Launch the missing pet flow on Android, jump straight to the Animal Photo step, perform a single photo selection, and confirm the UI and CTA states without touching other features.

**Acceptance Scenarios**:

1. **Given** the user is on the Animal Photo screen with no selection, **When** they tap "Browse" and pick a JPG/PNG/GIF/WEBP image from the Android Photo Picker, **Then** the picker dismisses, the confirmation card (icon, filename, size, remove affordance) renders exactly like node `297:8041`, and tapping Continue now advances to the Description step without additional prompts.
2. **Given** the user already attached a photo, **When** they navigate back to prior steps and return to Animal Photo, **Then** the same confirmation card reappears without asking for another upload.

---

### User Story 2 - Enforce mandatory photo before advancing (Priority: P2)

Users who ignore the requirement (e.g., tap Continue without attaching anything or remove an existing file) see a non-blocking toast that repeats the rule instead of a disabled button, so the flow feels responsive while still preventing navigation without a valid photo.

**Why this priority**: Consistent enforcement on Android protects data quality and keeps parity with other platforms that already require photos.

**Independent Test**: Attempt to leave the screen without a photo, observe the toast message, then satisfy the rule and confirm Continue works again.

**Acceptance Scenarios**:

1. **Given** no photo is selected, **When** the user taps Continue, **Then** the app stays on the Animal Photo screen, a toast displays "Photo is mandatory" for 3 seconds, and no navigation occurs.
2. **Given** a photo is attached and Continue would normally advance, **When** the user taps the "Remove" (X icon) action, **Then** the confirmation card disappears, helper text reiterates the requirement, Continue remains enabled, and any subsequent Continue tap shows the same toast until a new file is selected.

---

### User Story 3 - Recover from picker cancellation or issues (Priority: P3)

Android users who cancel the Photo Picker or encounter loading issues receive clear guidance, retain existing progress, and can resume the flow without starting over.

**Why this priority**: Permission friction and picker cancellation are common drop-off reasons in Android forms; we need resilient messaging so support isn't flooded with "can't upload photo" tickets.

**Independent Test**: Simulate a picker cancellation in isolation to confirm the UI responds with the correct state while preserving prior data.

**Acceptance Scenarios**:

1. **Given** the user cancels the Photo Picker, **When** they dismiss the picker, **Then** the screen remains on the empty state with helper text, Continue stays enabled, and tapping it shows the toast until a photo is picked.
2. **Given** the user selects an image that fails to load (corrupted or unavailable), **When** the loading fails, **Then** the confirmation card does not appear, and any Continue tap shows the toast until the user chooses a valid file.

---

### Edge Cases

- User cancels the Photo Picker: remain on the empty state, show helper text, keep Continue enabled, and surface the 3-second toast if they try to Continue without selecting a photo.
- Device resumes from background after extended period: previously selected photo metadata/card must still render because the draft session survives configuration changes via ViewModel; note: process death clears state (consistent with chip number step); draft is only cleared when exiting the entire flow (nav graph).
- Configuration change (device rotation): all UI state, including selected photo, must persist without re-prompting the user.
- Storage permission denied (Android 12 and below): surface guidance to enable permissions in Settings while keeping the last good selection; prevent data loss.
- Photo Picker unavailable (older devices without Google Play Services updates): fall back to intent-based gallery picker with the same behavior.
- TalkBack enabled: announce upload instructions, filename, size, and the Remove control with clear content descriptions.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The Android Animal Photo screen MUST appear as step 2/4 in the missing pet flow (spec 018) and visually match Figma nodes `297:7991` (empty) and `297:8041` (attached) including typography, button spacing, and helper copy.
- **FR-002**: The "Browse" button MUST launch the Android Photo Picker scoped to image media types only (JPG, PNG, GIF, WEBP). On devices where Photo Picker is unavailable, MUST fall back to ACTION_PICK or ACTION_GET_CONTENT intent.
- **FR-003**: After a supported image is selected, the UI MUST show a brief loading indicator (spinner or shimmer) on the card area while processing the image metadata, then render the confirmation card (icon, filename, size, remove control) and enable the Continue CTA while storing the selection inside the ViewModel state so it survives navigation and configuration changes.
- **FR-004**: Tapping Continue without a stored photo MUST leave the CTA enabled but surface a toast message that literally reads "Photo is mandatory" for 3 seconds and prevent navigation.
- **FR-005**: The Remove (X) control MUST clear the stored photo, revert the screen to the empty state, and ensure any subsequent Continue tap replays the "Photo is mandatory" toast until another valid file is selected, while retaining other step data.
- **FR-006**: The Photo Picker does not require runtime permissions on Android 13+; for Android 12 and below, the app MUST request READ_EXTERNAL_STORAGE permission if needed and handle denial gracefully with guidance to Settings.
- **FR-007**: All state (selected photo metadata, other flow inputs) MUST persist through configuration changes and navigation between steps using ViewModel and shared flow state (consistent with chip number step). Process death survival is not required.
- **FR-008**: All labels, helper copy, and error messaging MUST use Android string resources so translations stay consistent with other steps.
- **FR-009**: The screen MUST expose test tags following the `{screen}.{element}.{action}` convention (e.g., `animalPhoto.browse.click`, `animalPhoto.remove.click`, `animalPhoto.continue.click`) for automated UI tests.
- **FR-010**: ViewModel MUST follow MVI pattern with single StateFlow<UiState>, sealed UserIntent, and SharedFlow<UiEffect> as per project architecture.
- **FR-011**: The confirmation card MUST display an actual thumbnail preview of the selected photo, the filename (truncated to 20 characters with ellipsis if longer), and file size formatted in human-readable units (KB or MB). The empty state displays a generic placeholder icon per Figma node `297:7991`.
- **FR-012**: When the Photo Picker returns a content URI, the app MUST take persistable URI permissions to ensure the image remains accessible for the duration of the flow.

### Key Entities

- **Android Missing Pet Draft State**: Holds user progress for each step (chip number, photo, description, contact details) while the report is in progress; persists across in-app navigation (including back navigation) and configuration changes; cleared only when exiting the entire flow (nav graph) or on process death.
- **Photo Attachment State**: Encapsulates the selected image metadata (filename, size in bytes, content URI, thumbnail) plus UI flags: empty (no selection), loading (processing metadata after picker returns), confirmed (card visible with data), error (load failed).
- **Photo Selection Intent**: Sealed class representing user interactions: SelectPhoto, RemovePhoto, DismissPicker, ConfirmAndContinue.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: During QA, 100% of attempts to tap Continue without a photo display the "Photo is mandatory" toast for 3 seconds and prevent forward navigation until a photo is provided.
- **SC-002**: 95% of Android draft sessions that include a selected photo still display the confirmation card after navigating away and back within the same app session (process death clears state, consistent with chip number step).
- **SC-003**: At least 90% of testers who cancel the picker or encounter loading issues can recover without manual support by following the inline guidance and retry affordances.
- **SC-004**: Support tickets tagged "Android photo upload missing" drop by 40% within one month of release compared to the prior version.

## Assumptions

- Only the Photo Picker ("Browse") is required for this milestone; capturing a new photo with the camera is deferred.
- Image format validation relies on Photo Picker filters; no additional compression or client-side resizing is introduced for Android.
- Copywriting, localization, and icon assets will be delivered by Product Design and Content teams before implementation begins.
- Backend upload policies (no size limit, formats allowed) remain identical to those defined in feature 020; this spec only covers the Android front-end experience.
- This feature targets Android 7.0 (API 24) and above, consistent with the app's minimum SDK.
- The Photo Picker (available on Android 13+ and backported via Google Play Services) will be used where available; older devices fall back to intent-based picker.
