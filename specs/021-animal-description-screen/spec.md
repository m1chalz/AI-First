# Feature Specification: Animal Description Screen

**Feature Branch**: `021-animal-description-screen`  
**Created**: 2025-11-26  
**Status**: Draft  
**Input**: User description: "create specification 021 for Animal description screen"

This feature delivers Step 3/4 of the Missing Pet flow defined in specification 017 (iOS). UI must match Figma node `297-8209` from https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8209.

## Clarifications

### Session 2025-11-26

- Q: If GPS capture fails, should Step 3 add manual location text fields beyond the latitude/longitude inputs? → A: Do not add extra manual text fields; rely solely on latitude/longitude inputs (which can be edited manually).
- Q: When users edit auto-filled GPS coordinates, how should the `LocationCapture.captureMethod` be set? → A: Switch the capture method to `manual` immediately after any latitude or longitude edit, even if values originated from GPS.
- Q: How should species/race taxonomy behave offline? → A: Bundle a curated read-only taxonomy snapshot in the app and refresh it silently when connectivity is available so Step 3 remains usable offline.
- Q: Should we emit telemetry when the taxonomy fallback snapshot is used? → A: No extra analytics events; rely on client logging to debug offline fallback usage.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Provide animal context before contact (Priority: P1)

Reporters who arrive from the Animal Photo screen must enter descriptive data (date, species, breed/race, gender) so downstream responders understand the case before reading contact details.

**Why this priority**: Without these structured fields, the report lacks critical identifiers and the overall Missing Pet flow cannot progress to contact information (Step 4).

**Independent Test**: Launch Step 3 directly via deep link or navigation from Step 2, populate the required fields, and advance to Step 4 without touching other flow steps.

**Acceptance Scenarios**:

1. **Given** the user just completed Step 2, **When** Step 3 loads, **Then** the header shows “Animal description”, progress 3/4, the date defaults to today, and species/race/gender inputs appear exactly as designed.  
2. **Given** the user filled all required fields (date, species, race, gender), **When** they tap Continue, **Then** Step 4 (contact details) opens with all Step 3 values persisted in the session state described in spec 017.

---

### User Story 2 - Capture last known location details (Priority: P2)

Caregivers may not remember the precise latitude/longitude, so they need a “Request GPS position” shortcut plus the ability to manually edit the latitude and longitude fields when GPS is unavailable—no additional free-text location fields are provided.

**Why this priority**: Location hints dramatically improve recovery odds; providing both automated and manual entry keeps the flow usable indoors or when permissions are denied.

**Independent Test**: Use a device with mock GPS data, tap the Request GPS position button, verify permissions and auto-fill, modify the coordinates manually, and confirm validation before proceeding.

**Acceptance Scenarios**:

1. **Given** the user is on Step 3, **When** they tap “Request GPS position” and grant permission, **Then** the app fetches the device location once, populates both Lat and Long fields, confirms success with helper text, and keeps the fields editable.  
2. **Given** the user edits coordinates manually, **When** they enter a value outside valid latitude (−90 to 90) or longitude (−180 to 180) ranges, **Then** inline errors appear under the offending field and Continue stays disabled until both values are valid or cleared.

---

### User Story 3 - Maintain validation, persistence, and safe exits (Priority: P3)

Reporters might step away or return to previous steps; Step 3 must preserve their entries, explain optional fields, and prevent them from advancing with incomplete required data.

**Why this priority**: Error handling and persistence reduce abandonment while aligning with UX patterns described in spec 017 and the reference design.

**Independent Test**: Populate some fields, navigate backward to Step 2 via the header arrow, re-enter Step 3, confirm values persist, clear a required field, and verify Continue disables plus error messaging until resolved.

**Acceptance Scenarios**:

1. **Given** the user clears a required field (species, race, or gender), **When** they attempt to tap Continue, **Then** the CTA remains disabled and inline helper text explains what must be filled.  
2. **Given** the user presses the back arrow in the header, **When** they return to Step 2 and then forward again to Step 3, **Then** all previously entered Step 3 data re-populates and the progress indicator updates correctly (2/4 then 3/4).

---

### Edge Cases

- Location permission denied or previously blocked: the Request GPS button shows a toast explaining that users must manually enter latitude/longitude (or leave them blank) and deep links to system settings if possible.  
- User attempts to set a future “Date of disappearance”: validation rejects it with inline copy and retains the prior valid date.  
- Taxonomy service for species/race fails to load: fall back to the bundled offline snapshot, show a non-blocking “Using offline list” banner, and prevent Continue only if both offline and live data are unavailable (with retry affordance once online).  
- Race dropdown opened before species selection: keep control disabled with helper text “Choose species first.”  
- Additional description exceeds 500 characters: stop additional input, show character counter, and leave other fields untouched.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Step 3 MUST appear immediately after the Animal Photo screen (spec 020) and before contact details (spec 017), showing the header/back arrow, title “Animal description,” and progress chip “3/4” matching Figma node 297-8209.  
- **FR-002**: The top-left circular back button MUST always navigate to Step 2 with state persistence; the status text and progress indicator MUST update when re-entering Step 3.  
- **FR-003**: The “Date of disappearance” field MUST default to today’s date (or the last saved value) and open the native date picker; users MAY pick any past date but future dates MUST be rejected with inline feedback.  
- **FR-004**: Selecting a date MUST update local session storage so the value persists across navigation, app backgrounding, and device rotation until the flow completes or is canceled.  
- **FR-005**: The “Animal species” dropdown MUST load curated options from the taxonomy data source, display “Select an option” placeholder, and require a selection before Continue becomes available.  
- **FR-006**: The “Animal race” dropdown MUST remain disabled until a species is chosen, then show only races associated with that species; choosing a race is required for Continue (per design emphasis).  
- **FR-007**: The gender selector MUST present two cards (Female, Male) behaving as mutually exclusive radios with accessible labels; at least one option MUST be selected before Continue activates.  
- **FR-008**: “Animal age (optional)” MUST accept numeric input from 0–40 with validation preventing negative values or decimals; empty state is valid.  
- **FR-009**: Tapping “Request GPS position” MUST trigger a one-time location permission prompt (if needed); upon success the Lat and Long fields auto-populate with decimal degrees to 5 decimal places and timestamp the capture in session state.  
- **FR-010**: Lat and Long inputs MUST accept manual editing, enforce latitude (−90 to 90) and longitude (−180 to 180) ranges, and allow clearing both fields; invalid entries MUST show inline errors and block Continue. Any manual edit MUST immediately update `LocationCapture.captureMethod` to `manual`.  
- **FR-011**: “Animal additional description (optional)” MUST provide a multi-line text area supporting at least 500 characters plus a live counter; characters beyond the limit are ignored.  
- **FR-012**: The Continue CTA MUST match the blue primary button from Figma node 297-8209, stay disabled until required fields (date, species, race, gender) are valid, and on tap move to Step 4 while emitting analytics event `missing_pet.step3_completed`.  
- **FR-013**: All inputs MUST persist within the Missing Pet flow session object so that navigating backward/forward, locking the device, or experiencing temporary offline states does not wipe Step 3 data.  
- **FR-014**: If taxonomy data or location permissions fail, the screen MUST surface inline guidance plus retry affordances without crashing; Continue remains disabled until required data is available. When offline, use the bundled taxonomy snapshot and clearly indicate degraded mode while limiting diagnostics to client-side logs (no dedicated analytics events).  
- **FR-015**: When GPS capture fails or is skipped, only the latitude/longitude inputs serve as the manual fallback; display helper text reinforcing that no additional textual location details are collected on this step.

### Key Entities *(include if feature involves data)*

- **AnimalDescriptionDetails**: Session-bound structure containing disappearance date, species, race, gender, age, optional description, and metadata about when each value was last updated.  
- **LocationCapture**: Holds latitude, longitude, capture method (`gps` or `manual`), timestamp, and permission status so downstream services know whether the user granted access; capture method flips to `manual` the moment a user changes either coordinate.
- **SpeciesTaxonomyOption**: Represents each selectable species and its allowable races/breeds, sourced from an offline-bundled snapshot that can refresh from the taxonomy provider when connectivity resumes.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 95% of users who reach Step 3 can complete it and advance to Step 4 in ≤90 seconds during usability testing.  
- **SC-002**: 0% of QA test cases allow future dates, missing species/race, or missing gender to pass validation into Step 4.  
- **SC-003**: At least 85% of users who tap “Request GPS position” with permissions granted receive auto-populated coordinates within 5 seconds.  
- **SC-004**: 95% of sessions preserve all Step 3 entries when the user navigates away and returns within the same reporting session (per analytics).

## Assumptions

- Taxonomy (species + race mappings) and copy for helper/error text are provided by product and localized outside this feature.  
- Lat/Long inputs are optional but encouraged; omission does not block Continue as long as other required fields are valid.  
- Gender options remain binary for this release, matching the provided design; future inclusivity updates will be handled separately.  
- The age field collects whole years; if the age is unknown the reporter can leave it blank or enter “0”.  
- The Request GPS action uses the existing iOS location permission flow and respects system privacy settings.

## Dependencies

- Navigation scaffolding and session container from specification 017 (Missing Pet flow).  
- Taxonomy data service or static JSON providing species/race options.  
- Platform location services (Core Location) for the Request GPS button.  
- Analytics instrumentation pipeline for the `missing_pet.step3_completed` event.

## Out of Scope

- Persisting Step 3 data to backend APIs or drafts (handled in future backend integration).  
- Copywriting/localization for helper text beyond referencing provided Figma strings.  
- Non-iOS platform implementations (Android/Web follow-up features).  
- Enhancements to progress indicator behavior beyond what spec 017 already defines.
