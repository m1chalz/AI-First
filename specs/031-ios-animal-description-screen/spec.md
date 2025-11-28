# Feature Specification: Animal Description Screen (iOS)

**Feature Branch**: `031-ios-animal-description-screen`  
**Created**: November 28, 2025  
**Status**: Draft  
**Input**: User description: "na podstawie brancha origin/022-animal-description-screen stwórz specyfikację typowo na iOS, z pominięciem wszystkiego co dotyczy innych platform. Specyfikacja na branchu 031-ios-animal-description-screen i też ma numer 031"

This feature defines the **iOS Animal Description screen** (Step 3/4 of the Missing Pet flow defined in specification 017 for iOS).  
The iOS UI MUST match Figma node `297-8209` from the [PetSpot wireframes design](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8209), focusing exclusively on the iOS application.

**Design Reference**: See `figma-design-context.md` for complete design tokens (colors, typography, spacing). Key visual specifications:
- Primary blue: `#155dfc` (buttons, borders)
- Typography: Inter Regular 32px (title), Hind Regular 16px (labels/inputs)
- Input height: 41px (standard), 49px (date picker), 96px (description textarea)
- Border radius: 10px, border width: 0.667px, border color: `#d1d5dc`
- Vertical spacing: 24px between form fields, 8px between label and input

## Clarifications

### Session 2025-11-26 (from multi-platform spec 022, restricted to iOS scope)

- If GPS capture fails, the screen MUST NOT add extra manual location text fields beyond the latitude/longitude inputs. Reporters rely solely on latitude/longitude inputs (which can be edited manually).
- When users edit auto-filled GPS coordinates, the screen simply treats the coordinates as user-provided values; it does not distinguish whether they came from GPS or manual entry in any separate state.
- Species/race taxonomy MUST remain usable when the device is offline by bundling a curated read-only taxonomy snapshot in the iOS app and refreshing it silently when connectivity is available.
- No extra analytics events are required specifically for detecting taxonomy offline fallbacks; standard client logging is sufficient for debugging.

### Session 2025-11-28

- Q: What is the minimum iOS version for this feature? → A: iOS 18+ (92% device coverage per adoption metrics)
- Q: What accessibility requirements apply to this screen? → A: Only accessibilityIdentifier for testing (no VoiceOver support)
- Q: What is the GPS location request timeout? → A: No explicit timeout (assumes fast response)
- Q: When should field validation occur? → A: On submit (when Continue button tapped)
- Q: How long should session data persist? → A: Until flow completion or cancellation (in-memory session via constructor-injected container)

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

**Independent Test**: On iOS, populate some fields, navigate backward to Step 2 via the header arrow, re-enter Step 3, confirm values persist, clear a required field, tap Continue, and verify a toast plus inline error messaging appear and block navigation until the issues are resolved.

**Acceptance Scenarios**:

1. **Given** the user clears a required field (species, race, or gender), **When** they tap Continue, **Then** a toast message explains that some information must be corrected, the specific invalid or missing fields are visually marked with inline helper text, and navigation to Step 4 does not occur until the fields are corrected.  
2. **Given** the user presses the back arrow in the header, **When** they return to Step 2 and then forward again to Step 3, **Then** all previously entered Step 3 data re-populates on iOS and the progress indicator updates correctly (2/4 then 3/4).

---

### Edge Cases

- **Location permission denied or previously blocked**: the Request GPS button shows an alert with two options – **Cancel** (closes the alert and lets users manually enter or leave latitude/longitude blank) and **Go to Settings** (opens the iOS Settings screen for this app so users can change location permissions).  
- **Future “Date of disappearance”**: the date picker defaults to today and proactively blocks selection of any future dates (user can only choose today or a past date), so no separate error state is shown for future dates.  
- **Species list source**: species options always come from the fixed curated list bundled with the app; no external taxonomy service or runtime loading is attempted on this screen.  
- **Race input before species selection**: the race input is a disabled text field until a species is selected; when the user changes species, any previously entered race text is cleared, and the field remains required once enabled.  
- **Additional description exceeds 500 characters**: further input is blocked (keyboard input prevented at limit, pasted text truncated to 500 characters), a character counter communicates the limit, and other fields remain unchanged.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: On iOS, Step 3 MUST appear immediately after the Animal Photo screen (spec 020) and before contact details (spec 017), showing the header/back arrow, title “Animal description,” and progress chip “3/4” matching the referenced Figma design.  
- **FR-002**: The top-left circular back button MUST always navigate to Step 2 with state persistence; the status text and progress indicator MUST update when re-entering Step 3.  
- **FR-003**: The “Date of disappearance” field MUST default to today’s date (or the last saved value) and open the system date picker configured so users MAY pick any past date (including today) but future dates are disabled and cannot be selected.  
- **FR-004**: Selecting a date MUST update the iOS Missing Pet flow state (`ReportMissingPetFlowState`) so the value persists across navigation, app backgrounding, and device rotation until the flow completes or is canceled.  
- **FR-005**: The “Animal species” dropdown MUST use a fixed curated list of species bundled with the app (no runtime taxonomy service), display a clear placeholder (e.g., “Select an option”), and require a selection before Continue becomes available.  
- **FR-006**: The “Animal race” field MUST be implemented as a text field that stays disabled until a species is chosen; when the user changes species, any previously entered race text MUST be cleared, and once enabled the race field is required for Continue.  
- **FR-007**: The gender selector MUST present two cards (Female, Male) behaving as mutually exclusive options with accessibilityIdentifier attributes for testing; at least one option MUST be selected before Continue activates.  
- **FR-008**: “Animal age (optional)” MUST accept numeric input from 0–40 with validation preventing negative values or decimals; empty state is valid.  
- **FR-009**: Tapping "Request GPS position" MUST trigger the standard iOS location permission flow (if needed); upon success the Lat and Long fields auto-populate with decimal degrees to 5 decimal places (capture time from LocationService is not persisted in flow state, only coordinates).  
- **FR-010**: Lat and Long inputs MUST accept manual editing, enforce latitude (−90 to 90) and longitude (−180 to 180) ranges, and allow clearing both fields; invalid entries MUST show inline errors and block Continue, regardless of whether the values came from GPS or were entered manually.  
- **FR-011**: "Animal additional description (optional)" MUST provide a multi-line text area supporting exactly 500 characters with a live counter; the component MUST enforce a hard limit by preventing further input when 500 characters are reached and truncating pasted text that exceeds the limit.  
- **FR-012**: The Continue CTA MUST match the primary button style from the Figma design and remain enabled at all times; when tapped with invalid or missing required fields (date, species, race, gender), it MUST validate all fields on submit, show a toast message, and highlight the specific fields with inline helper text while keeping the user on Step 3, and only when all required fields are valid MAY it navigate to Step 4 while updating `ReportMissingPetFlowState` with all animal description data.  
- **FR-013**: All inputs MUST persist within the in-memory iOS Missing Pet flow state container (`ReportMissingPetFlowState`, constructor-injected to ViewModel) so that navigating backward/forward, locking the device, or experiencing temporary offline states does not wipe Step 3 data; flow state is retained until flow completion or explicit cancellation.  
- **FR-014**: If location permissions fail, the screen MUST surface inline guidance plus retry affordances without crashing; Continue remains disabled only when location-related validation rules require it (e.g., invalid coordinate ranges), and the app must remain usable without a taxonomy service because species are loaded from a static bundled list.  
- **FR-015**: When GPS capture fails or is skipped, only the latitude/longitude inputs serve as the manual fallback; helper text MUST clarify that no additional textual location details are collected in this step.
- **FR-016**: All interactive UI elements MUST have accessibilityIdentifier attributes following the `{screen}.{element}.{action}` naming convention for automated testing; VoiceOver support and other end-user accessibility features are explicitly out of scope for this release.

### Key Entities *(include if feature involves data)*

- **ReportMissingPetFlowState** (existing): Flow state container (owned by `ReportMissingPetCoordinator`) that will be extended with animal description fields: disappearanceDate, animalSpecies, animalRace, animalGender, animalAge, latitude, longitude (as flat optional properties).  
- **SpeciesTaxonomyOption**: Represents each selectable species from a fixed curated list bundled with the app; there is no runtime taxonomy service or online refresh in this step.  
- **UserLocation** (reused): Existing domain model (`/iosApp/iosApp/Domain/UserLocation.swift`) containing latitude, longitude, and timestamp; used by LocationService for GPS capture but not stored directly in AnimalDescriptionDetails (coordinates are flattened).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 0% of QA test cases on iOS allow future dates, missing species/race, or missing gender to pass validation into Step 4.  
- **SC-002**: 95% of iOS sessions preserve all Step 3 entries when the user navigates away and returns within the same reporting session (per analytics).  

## Assumptions

- This feature targets iOS 18+ as the minimum supported version (92% device adoption coverage).
- Curated species list and copy for helper/error text are provided by product and bundled statically with the iOS app; no runtime taxonomy service or remote lookup is used on this screen.  
- Lat/Long inputs are optional but encouraged; omission does not block Continue as long as other required fields are valid.  
- Gender options remain binary for this release, matching the provided iOS design; future inclusivity updates will be handled separately.  
- The age field collects whole years; if the age is unknown the reporter can leave it blank or enter "0".  
- The Request GPS action uses the standard iOS location permission flow and respects system privacy settings.

## Dependencies

- iOS navigation scaffolding and flow state container from specification 017 (Missing Pet flow): `ReportMissingPetCoordinator` and `ReportMissingPetFlowState`.  
- Static configuration for the curated species list bundled with the iOS app.  
- Device location capabilities for the Request GPS button.  
- Analytics capabilities that can track completion of Step 3 in the Missing Pet flow.

## Out of Scope

- Persisting Step 3 data to backend APIs or drafts (handled in future backend integration).  
- Copywriting/localization for helper text beyond referencing provided Figma strings.  
- Any implementation for non-iOS platforms (Android/Web will be handled in separate features).  
- Enhancements to progress indicator behavior beyond what spec 017 already defines for iOS.


