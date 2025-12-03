# Feature Specification: Animal Description Screen (Android)

**Feature Branch**: `042-android-animal-description-screen`  
**Created**: December 3, 2025  
**Status**: Draft  
**Input**: User description: "Based on generic spec 022 and iOS spec 031, prepare the spec for Android platform."

This feature defines the **Android Animal Description screen** (Step 3/4 of the Missing Pet flow defined in specification 018 for Android).  
The Android UI MUST match Figma node `297-8209` from the [PetSpot wireframes design](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8209), adapted to Android Material Design patterns and Jetpack Compose implementation.

**Design References**:
- **Main screen**: Figma node `297-8209` - [PetSpot wireframes](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8209)
- **Error states**: Figma node `297-11850` - [Input field error states](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-11850)
- See `figma-design-context.md` (from iOS spec 031) for design tokens

Key visual specifications adapted to Android:
- Primary blue: `#155dfc` (buttons, borders)
- Typography: Adapt Inter Regular 32px (title), Hind Regular 16px (labels/inputs) to closest Material equivalents
- Input height: Standard OutlinedTextField sizing
- Border radius: 10dp, border width: 1dp, border color: `#d1d5dc`
- Vertical spacing: 24dp between form fields, 8dp between label and input

## Clarifications

### Session 2025-11-26 (from multi-platform spec 022, restricted to Android scope)

- If GPS capture fails, the screen MUST NOT add extra manual location text fields beyond the latitude/longitude inputs. Reporters rely solely on latitude/longitude inputs (which can be edited manually).
- When users edit auto-filled GPS coordinates, the screen simply treats the coordinates as user-provided values; it does not distinguish whether they came from GPS or manual entry in any separate state.
- Species/race taxonomy MUST remain usable when the device is offline by bundling a curated read-only taxonomy snapshot in the Android app and refreshing it silently when connectivity is available.
- No extra analytics events are required specifically for detecting taxonomy offline fallbacks; standard client logging is sufficient for debugging.

### Session 2025-12-03 (Android-specific clarifications)

- Q: What is the minimum Android SDK version for this feature? → A: API 24 (Android 7.0 Nougat) minimum, targeting API 36 (latest)
- Q: What accessibility requirements apply to this screen? → A: Only testTag modifiers for automated testing (no TalkBack/accessibility announcements in scope)
- Q: What is the GPS location request timeout? → A: No explicit timeout (assumes fast response)
- Q: When should field validation occur? → A: On submit (when Continue button tapped)
- Q: How long should session data persist? → A: Until flow completion or cancellation (in-memory flow state via NavGraph-scoped ViewModel using Koin)
- Q: How should the UI behave while fetching GPS coordinates? → A: Replace the Request GPS button label with a spinner + “Requesting…” state, disable the button until the call completes, keep the rest of the form usable

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Provide animal context before contact (Priority: P1)

Reporters using the Android Missing Pet flow who arrive from the Animal Photo screen must enter descriptive data (date, species, breed/race, gender) so responders understand the case before reading contact details.

**Why this priority**: Without these structured fields, the report lacks critical identifiers and the overall Missing Pet flow cannot progress to contact information (Step 4).

**Independent Test**: On Android, launch Step 3 directly via navigation from Step 2, populate the required fields, and advance to Step 4 without touching other flow steps.

**Acceptance Scenarios**:

1. **Given** the user just completed Step 2 on Android, **When** Step 3 loads, **Then** the TopAppBar shows title "Animal description" and progress indicator "3/4", the date defaults to today, and species/race/gender inputs appear exactly as designed in Figma.  
2. **Given** the user filled all required fields (date, species, race, gender), **When** they tap Continue, **Then** Step 4 (contact details) opens with all Step 3 values persisted in the Android Missing Pet flow state described in spec 018.

---

### User Story 2 - Capture last known location details (Priority: P2)

Caregivers using the Android app may not remember the precise latitude/longitude, so they need a **Request GPS position** shortcut plus the ability to manually edit the latitude and longitude fields when GPS is unavailable—no additional free-text location fields are provided.

**Why this priority**: Location hints dramatically improve recovery odds; providing both automated and manual entry keeps the flow usable indoors or when permissions are denied.

**Independent Test**: On an Android device with mock GPS data, tap the Request GPS position button, verify permissions and auto-fill, modify the coordinates manually, and confirm validation before proceeding.

**Acceptance Scenarios**:

1. **Given** the user is on Step 3 in the Android app, **When** they tap "Request GPS position" and grant permission, **Then** the app fetches the device location once, populates both Lat and Long fields, confirms success with helper text, and keeps the fields editable.  
2. **Given** the user edits coordinates manually, **When** they enter a value outside valid latitude (−90 to 90) or longitude (−180 to 180) ranges, **Then** inline errors appear under the offending field, Continue stays enabled, and tapping it keeps the user on Step 3 while re-running validation until both values are valid or cleared.

---

### User Story 3 - Maintain validation, persistence, and safe exits (Priority: P3)

Reporters on Android might step away or return to previous steps; Step 3 must preserve their entries, explain optional fields, and prevent them from advancing with incomplete required data.

**Why this priority**: Error handling and persistence reduce abandonment while aligning with the Android UX patterns described in spec 018 and the reference design.

**Independent Test**: On Android, populate some fields, navigate backward to Step 2 via the TopAppBar back button, re-enter Step 3, confirm values persist, clear a required field, tap Continue, and verify a Snackbar plus inline error messaging appear and block navigation until the issues are resolved.

**Acceptance Scenarios**:

1. **Given** the user clears a required field (species, race, or gender), **When** they tap Continue, **Then** a Snackbar message explains that some information must be corrected, the specific invalid or missing fields are visually marked with inline helper text (Material error state), and navigation to Step 4 does not occur until the fields are corrected.  
2. **Given** the user presses the back button in the TopAppBar, **When** they return to Step 2 and then forward again to Step 3, **Then** all previously entered Step 3 data re-populates on Android and the progress indicator updates correctly (2/4 then 3/4).

---

### Edge Cases

- **Location permission denied or previously blocked**: the Request GPS button shows a Snackbar with an action to open Android Settings so users can change location permissions; users may manually enter or leave latitude/longitude blank.  
- **Future "Date of disappearance"**: the date picker defaults to today and proactively blocks selection of any future dates (user can only choose today or a past date), so no separate error state is shown for future dates.  
- **Species list source**: species options always come from the fixed curated list bundled with the app; no external taxonomy service or runtime loading is attempted on this screen.  
- **Race input before species selection**: the race input is a disabled OutlinedTextField until a species is selected; when the user changes species, any previously entered race text is cleared, and the field remains required once enabled.  
- **Additional description exceeds 500 characters**: further input is blocked (text truncated at limit), a character counter communicates the limit, and other fields remain unchanged.

## Requirements *(mandatory)*

### Dependencies

- **Spec 018** (018-android-missing-pet-flow): Provides navigation scaffolding (nested Navigation Graph infrastructure), shared flow ViewModel, and placeholder screens. This spec enhances the existing placeholder with actual animal description input functionality.
- **Spec 026** (026-android-location-permissions): Provides location permission handling patterns and utilities for the "Request GPS position" feature.

### Functional Requirements

- **FR-001**: On Android, Step 3 MUST appear immediately after the Animal Photo screen and before contact details (spec 018), showing the TopAppBar with navigation icon (back arrow), title "Animal description," and progress indicator "3/4" matching the referenced Figma design adapted to Material Design 3.  
- **FR-002**: The TopAppBar navigation icon (back arrow) MUST always navigate to Step 2 with state persistence; the progress indicator MUST update when re-entering Step 3.  
- **FR-002a**: "Pet name (optional)" MUST be the first form field displayed after the title and subtitle, implemented as an OutlinedTextField; empty state is valid and does not block Continue.
- **FR-003**: The "Date of disappearance" field MUST be displayed after the pet name field, default to today's date (or the last saved value) and open the Android DatePickerDialog configured so users MAY pick any past date (including today) but future dates are disabled and cannot be selected.  
- **FR-004**: Selecting a date MUST update the Android Missing Pet flow state so the value persists across navigation, app backgrounding, and device rotation until the flow completes or is canceled.  
- **FR-005**: The "Animal species" dropdown MUST use a fixed curated list of species bundled with the app (no runtime taxonomy service), display a clear placeholder (e.g., "Select an option"), and be validated as a required field when Continue is tapped.  
- **FR-006**: The "Animal race" field MUST be implemented as an OutlinedTextField that stays disabled until a species is chosen; when the user changes species, any previously entered race text MUST be cleared, and once enabled the race field is required for Continue.  
- **FR-007**: The gender selector MUST present two selectable cards (Female, Male) behaving as mutually exclusive options with testTag modifiers for testing; selection is validated as required when Continue is tapped.  
- **FR-008**: "Animal age (optional)" MUST accept numeric input from 0–40 with validation preventing negative values or decimals; empty state is valid. Use numeric keyboard type.  
- **FR-009**: Tapping "Request GPS position" MUST trigger the Android location permission flow (if needed); upon success the Lat and Long fields auto-populate with decimal degrees to 5 decimal places.  
- **FR-009a**: While a GPS request is in progress, the "Request GPS position" button MUST disable itself and swap its label with a small inline progress indicator plus "Requesting…" text, while all other inputs remain interactive.
- **FR-010**: Lat and Long inputs MUST accept manual editing, enforce latitude (−90 to 90) and longitude (−180 to 180) ranges, and allow clearing both fields; invalid entries MUST show inline errors (Material error state) when Continue is tapped and block navigation to Step 4 until corrected, regardless of whether the values came from GPS or were entered manually.  
- **FR-011**: "Animal additional description (optional)" MUST provide a multi-line OutlinedTextField supporting exactly 500 characters with a live counter; the component MUST enforce a hard limit by preventing further input when 500 characters are reached and truncating pasted text that exceeds the limit.  
- **FR-012**: The Continue Button MUST match the primary button style from the Figma design (filled button, primary blue color) and remain enabled at all times; when tapped with invalid or missing required fields (date, species, race, gender), it MUST validate all fields on submit, show a Snackbar message, and highlight the specific fields with inline helper text (Material error state) while keeping the user on Step 3, and only when all required fields are valid MAY it navigate to Step 4 while updating the flow state with all animal description data.  
- **FR-013**: All inputs MUST persist within the in-memory Android Missing Pet flow state (NavGraph-scoped ViewModel via Koin) so that navigating backward/forward, locking the device, or experiencing temporary offline states does not wipe Step 3 data; flow state is retained until flow completion or explicit cancellation.  
- **FR-014**: If location permissions fail, the screen MUST surface inline guidance plus retry affordances (Snackbar with Settings action) without crashing; even when location-related validation rules fail (e.g., invalid coordinate ranges), the Continue button remains enabled but tapping it MUST keep the user on Step 3, show inline errors, and block navigation until the coordinates are valid, and the app must remain usable without a taxonomy service because species are loaded from a static bundled list.  
- **FR-015**: When GPS capture fails or is skipped, only the latitude/longitude inputs serve as the manual fallback; helper text MUST clarify that no additional textual location details are collected in this step.
- **FR-016**: All interactive Composables MUST have Modifier.testTag() attributes following the `{screen}.{element}` naming convention for automated testing (e.g., `animalDescription.speciesDropdown`, `animalDescription.continueButton`).
- **FR-017**: ViewModel MUST follow MVI pattern with single StateFlow<UiState>, sealed UserIntent, and SharedFlow<UiEffect> as mandated by project architecture.
- **FR-018**: Flow state MUST be shared via NavGraph-scoped ViewModel using Koin's navigation scope, consistent with spec 038 (Chip Number Screen) pattern.

### Key Entities *(include if feature involves data)*

- **ReportMissingPetFlowState** (existing): Flow state container (managed by NavGraph-scoped ViewModel) that will be extended with animal description fields: petName, disappearanceDate, animalSpecies, animalRace, animalGender, animalAge, latitude, longitude, animalDescription (as flat optional properties).  
- **SpeciesTaxonomyOption**: Represents each selectable species from a fixed curated list bundled with the app; there is no runtime taxonomy service or online refresh in this step.  
- **AnimalDescriptionUiState**: Immutable data class representing the current UI state of the animal description screen. Contains all form field values, validation errors, loading states, and other UI-relevant properties.
- **AnimalDescriptionUserIntent**: Sealed class representing all possible user actions on this screen (UpdatePetName, UpdateDate, UpdateSpecies, UpdateRace, UpdateGender, UpdateAge, RequestGPSPosition, UpdateLatitude, UpdateLongitude, UpdateDescription, ContinueClicked, BackClicked).
- **AnimalDescriptionUiEffect**: Sealed class for one-off events (NavigateToContactDetails, NavigateBack, ShowSnackbar, OpenDatePicker, OpenSettings).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 0% of QA test cases on Android allow future dates, missing species/race, or missing gender to pass validation into Step 4.  
- **SC-002**: 95% of Android sessions preserve all Step 3 entries when the user navigates away and returns within the same reporting session (per analytics).
- **SC-003**: Unit tests for ViewModel achieve 80% line and branch coverage (mandated by project rules).
- **SC-004**: Screen layout adapts correctly to common Android device sizes and orientations without content clipping or overlap.

## Assumptions

- This feature targets Android API 24 (Android 7.0 Nougat) as minimum, targeting API 36 (latest), consistent with project-wide configuration.
- Curated species list and copy for helper/error text are provided by product and bundled statically with the Android app; no runtime taxonomy service or remote lookup is used on this screen.  
- Lat/Long inputs are optional but encouraged; omission does not block Continue as long as other required fields are valid.  
- Gender options remain binary for this release, matching the provided design; future inclusivity updates will be handled separately.  
- The age field collects whole years; if the age is unknown the reporter can leave it blank or enter "0".  
- The Request GPS action uses Android location services and respects system privacy settings.
- Implementation will use Jetpack Compose for UI.
- Koin dependency injection is mandatory per project architecture.

## Dependencies

- Android navigation scaffolding and flow state container from specification 018 (Missing Pet flow): Nested NavGraph and shared ViewModel.
- Location permission handling patterns from specification 026 (Android Location Permissions).
- Static configuration for the curated species list bundled with the Android app.
- Android device location capabilities (FusedLocationProviderClient) for the Request GPS button.

## Out of Scope

- Persisting Step 3 data to backend APIs or drafts (handled in future backend integration).  
- Copywriting/localization for helper text beyond referencing provided Figma strings.  
- Any implementation for non-Android platforms (iOS/Web are handled in separate features).  
- Enhancements to progress indicator behavior beyond what spec 018 already defines for Android.
- TalkBack/accessibility announcements (only testTag for automated testing is in scope).
