# Feature Specification: Animal Description Screen (iOS)

**Feature Branch**: `031-ios-animal-description-screen`  
**Created**: November 28, 2025  
**Status**: Draft  
**Input**: User description: "na podstawie brancha origin/022-animal-description-screen stwórz specyfikację typowo na iOS, z pominięciem wszystkiego co dotyczy innych platform. Specyfikacja na branchu 031-ios-animal-description-screen i też ma numer 031"

This feature defines the **iOS Animal Description screen** (Step 3/4 of the Missing Pet flow defined in specification 017 for iOS).  
The iOS UI MUST match Figma node `297-8209` from the PetSpot wireframes design, focusing exclusively on the iOS application.

## Clarifications

### Session 2025-11-26 (from multi-platform spec 022, restricted to iOS scope)

- If GPS capture fails, the screen MUST NOT add extra manual location text fields beyond the latitude/longitude inputs. Reporters rely solely on latitude/longitude inputs (which can be edited manually).
- When users edit auto-filled GPS coordinates, the capture method MUST be treated as **manual** immediately after any latitude or longitude edit, even if values originated from GPS.
- Species/race taxonomy MUST remain usable when the device is offline by bundling a curated read-only taxonomy snapshot in the iOS app and refreshing it silently when connectivity is available.
- No extra analytics events are required specifically for detecting taxonomy offline fallbacks; standard client logging is sufficient for debugging.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Provide animal context before contact (Priority: P1)

Reporters using the iOS Missing Pet flow who arrive from the Animal Photo screen must enter descriptive data (date, species, breed/race, gender) so responders understand the case before reading contact details.

**Why this priority**: Without these structured fields, the report lacks critical identifiers and the overall Missing Pet flow cannot progress to contact information (Step 4).

**Independent Test**: On iOS, launch Step 3 directly via navigation from Step 2, populate the required fields, and advance to Step 4 without touching other flow steps.

**Acceptance Scenarios**:

1. **Given** the user just completed Step 2 on iOS, **When** Step 3 loads, **Then** the header shows “Animal description”, progress 3/4, the date defaults to today, and species/race/gender inputs appear exactly as designed in Figma.  
2. **Given** the user filled all required fields (date, species, race, gender), **When** they tap Continue, **Then** Step 4 (contact details) opens with all Step 3 values persisted in the iOS Missing Pet flow session state described in spec 017.

---

### User Story 2 - Capture last known location details (Priority: P2)

Caregivers using the iOS app may not remember the precise latitude/longitude, so they need a **Request GPS position** shortcut plus the ability to manually edit the latitude and longitude fields when GPS is unavailable—no additional free-text location fields are provided.

**Why this priority**: Location hints dramatically improve recovery odds; providing both automated and manual entry keeps the flow usable indoors or when permissions are denied.

**Independent Test**: On an iOS device with mock GPS data, tap the Request GPS position button, verify permissions and auto-fill, modify the coordinates manually, and confirm validation before proceeding.

**Acceptance Scenarios**:

1. **Given** the user is on Step 3 in the iOS app, **When** they tap “Request GPS position” and grant permission, **Then** the app fetches the device location once, populates both Lat and Long fields, confirms success with helper text, and keeps the fields editable.  
2. **Given** the user edits coordinates manually, **When** they enter a value outside valid latitude (−90 to 90) or longitude (−180 to 180) ranges, **Then** inline errors appear under the offending field and Continue stays disabled until both values are valid or cleared.

---

### User Story 3 - Maintain validation, persistence, and safe exits (Priority: P3)

Reporters on iOS might step away or return to previous steps; Step 3 must preserve their entries, explain optional fields, and prevent them from advancing with incomplete required data.

**Why this priority**: Error handling and persistence reduce abandonment while aligning with the iOS UX patterns described in spec 017 and the reference design.

**Independent Test**: On iOS, populate some fields, navigate backward to Step 2 via the header arrow, re-enter Step 3, confirm values persist, clear a required field, and verify Continue disables plus error messaging until resolved.

**Acceptance Scenarios**:

1. **Given** the user clears a required field (species, race, or gender), **When** they attempt to tap Continue, **Then** the CTA remains disabled and inline helper text explains what must be filled.  
2. **Given** the user presses the back arrow in the header, **When** they return to Step 2 and then forward again to Step 3, **Then** all previously entered Step 3 data re-populates on iOS and the progress indicator updates correctly (2/4 then 3/4).

---

### Edge Cases

- **Location permission denied or previously blocked**: the Request GPS button shows a non-blocking message explaining that users must manually enter latitude/longitude (or leave them blank) and, where the iOS platform allows, offers a way to open system settings.  
- **Future “Date of disappearance”**: if the user attempts to set a future date, validation rejects it with inline copy and retains the prior valid date.  
- **Taxonomy service for species/race unavailable**: the screen falls back to the bundled offline taxonomy snapshot, shows a small “Using offline list” banner, and prevents Continue only if both offline and live data are unavailable (with an option to retry once online).  
- **Race dropdown opened before species selection**: the control stays disabled with helper text “Choose species first.”  
- **Additional description exceeds 500 characters**: further input is blocked, a character counter communicates the limit, and other fields remain unchanged.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: On iOS, Step 3 MUST appear immediately after the Animal Photo screen (spec 020) and before contact details (spec 017), showing the header/back arrow, title “Animal description,” and progress chip “3/4” matching the referenced Figma design.  
- **FR-002**: The top-left circular back button MUST always navigate to Step 2 with state persistence; the status text and progress indicator MUST update when re-entering Step 3.  
- **FR-003**: The “Date of disappearance” field MUST default to today’s date (or the last saved value) and open the system date picker; users MAY pick any past date but future dates MUST be rejected with inline feedback.  
- **FR-004**: Selecting a date MUST update the iOS Missing Pet session so the value persists across navigation, app backgrounding, and device rotation until the flow completes or is canceled.  
- **FR-005**: The “Animal species” dropdown MUST load curated options from the taxonomy data source, display a clear placeholder (e.g., “Select an option”), and require a selection before Continue becomes available.  
- **FR-006**: The “Animal race” dropdown MUST remain disabled until a species is chosen, then show only races associated with that species; choosing a race is required for Continue.  
- **FR-007**: The gender selector MUST present two cards (Female, Male) behaving as mutually exclusive options with accessible labels; at least one option MUST be selected before Continue activates.  
- **FR-008**: “Animal age (optional)” MUST accept numeric input from 0–40 with validation preventing negative values or decimals; empty state is valid.  
- **FR-009**: Tapping “Request GPS position” MUST trigger the standard iOS location permission flow (if needed); upon success the Lat and Long fields auto-populate with decimal degrees to 5 decimal places and store the capture time in session state.  
- **FR-010**: Lat and Long inputs MUST accept manual editing, enforce latitude (−90 to 90) and longitude (−180 to 180) ranges, and allow clearing both fields; invalid entries MUST show inline errors and block Continue. Any manual edit MUST immediately switch the capture method to manual.  
- **FR-011**: “Animal additional description (optional)” MUST provide a multi-line text area supporting at least 500 characters plus a live counter; characters beyond the limit are ignored.  
- **FR-012**: The Continue CTA MUST match the primary button style from the Figma design, stay disabled until required fields (date, species, race, gender) are valid, and on tap move to Step 4 while recording that Step 3 was completed for analytics purposes.  
- **FR-013**: All inputs MUST persist within the iOS Missing Pet flow session object so that navigating backward/forward, locking the device, or experiencing temporary offline states does not wipe Step 3 data.  
- **FR-014**: If taxonomy data or location permissions fail, the screen MUST surface inline guidance plus retry affordances without crashing; Continue remains disabled until required data is available. When offline, the app uses the bundled taxonomy snapshot and clearly indicates degraded mode while limiting diagnostics to client-side logs.  
- **FR-015**: When GPS capture fails or is skipped, only the latitude/longitude inputs serve as the manual fallback; helper text MUST clarify that no additional textual location details are collected in this step.

### Key Entities *(include if feature involves data)*

- **AnimalDescriptionDetails**: Session-bound structure containing disappearance date, species, race, gender, age, optional description, and metadata about when each value was last updated within the iOS Missing Pet flow.  
- **LocationCapture**: Holds latitude, longitude, capture method (GPS or manual), timestamp, and permission status so downstream services know whether the user granted access; the capture method flips to manual the moment a user changes either coordinate.  
- **SpeciesTaxonomyOption**: Represents each selectable species and its allowable races/breeds, sourced primarily from an offline-bundled snapshot that can refresh from the taxonomy provider when connectivity resumes.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: At least 95% of iOS users who reach Step 3 can complete it and advance to Step 4 in ≤90 seconds during usability testing.  
- **SC-002**: 0% of QA test cases on iOS allow future dates, missing species/race, or missing gender to pass validation into Step 4.  
- **SC-003**: At least 85% of iOS users who tap “Request GPS position” with permissions granted receive auto-populated coordinates within 5 seconds.  
- **SC-004**: 95% of iOS sessions preserve all Step 3 entries when the user navigates away and returns within the same reporting session (per analytics).  

## Assumptions

- Taxonomy (species + race mappings) and copy for helper/error text are provided by product and localized outside this feature.  
- Lat/Long inputs are optional but encouraged; omission does not block Continue as long as other required fields are valid.  
- Gender options remain binary for this release, matching the provided iOS design; future inclusivity updates will be handled separately.  
- The age field collects whole years; if the age is unknown the reporter can leave it blank or enter “0”.  
- The Request GPS action uses the standard iOS location permission flow and respects system privacy settings.

## Dependencies

- iOS navigation scaffolding and session container from specification 017 (Missing Pet flow).  
- Taxonomy data source providing species/race options and an offline snapshot suitable for bundling with the iOS app.  
- Device location capabilities for the Request GPS button.  
- Analytics capabilities that can track completion of Step 3 in the Missing Pet flow.

## Out of Scope

- Persisting Step 3 data to backend APIs or drafts (handled in future backend integration).  
- Copywriting/localization for helper text beyond referencing provided Figma strings.  
- Any implementation for non-iOS platforms (Android/Web will be handled in separate features).  
- Enhancements to progress indicator behavior beyond what spec 017 already defines for iOS.


