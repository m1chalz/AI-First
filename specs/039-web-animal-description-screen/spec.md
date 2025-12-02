# Feature Specification: Web Animal Description Screen

**Feature Branch**: `039-web-animal-description-screen`  
**Created**: December 2, 2025  
**Status**: Draft  
**Input**: User description: "Na podstawie specyfikacji 022 przygotuj podobną dla aplikacji webowej. pamiętaj, że dwa poprzednie ekrany formularza (numer microchipu i zdjecie) są juz zaimplementowane, staramy się wykorzystać już istniejące rozwiazania. komunikacja z backendem będzie zaimplementowana w osobnej specyfikacji"

This feature defines the **Web Animal Description screen** (Step 3/4 of the Missing Pet flow, building on specs 034 and 037).  
The web UI MUST match Figma node `315-15837` from the [PetSpot wireframes design](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=315-15837&m=dev) while reusing the form structure and patterns established in specs 034 and 037.

**Design Reference**: See Figma design node `315-15837` for complete visual specifications.  
Key visual specifications align with established web app styling:
- Primary blue for buttons and active states
- Consistent typography and spacing from existing web screens
- Input height and border radius consistent with 034 and 037
- Vertical spacing: 24px between form fields, 8px between label and input

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Provide animal context before submission (Priority: P1)

Web users who have uploaded the microchip number and photo arrive at the Animal Description screen (Step 3/4) to enter descriptive data (date, species, breed/race, gender, optional age and description) so responders understand the case before reading contact details.

**Why this priority**: Without these structured fields, the report lacks critical identifiers. This is the core flow for the Missing Pet reporting experience on web.

**Independent Test**: Navigate to Step 3 from the pet list via Steps 1 and 2, populate all required fields, and advance to Step 4 without touching other flow steps.

**Acceptance Scenarios**:

1. **Given** the user completed Step 2 on web, **When** Step 3 loads, **Then** the header shows "Animal description", progress 3/4, the date defaults to today, and species/race/gender inputs appear matching the established web form pattern.  
2. **Given** the user filled all required fields (date, species, race, gender), **When** they click Continue, **Then** Step 4 (contact details) opens with all Step 3 values persisted in the web Missing Pet flow state.

---

### User Story 2 - Maintain validation, persistence, and safe exits (Priority: P2)

Web users might navigate backward or step away; Step 3 must preserve entries, explain optional fields, and prevent advancing with incomplete required data while respecting the established web UX patterns.

**Why this priority**: Error handling and persistence reduce abandonment while maintaining consistency with specs 034 and 037.

**Independent Test**: On web, populate some fields, click back arrow to Step 2, re-enter Step 3, confirm values persist, clear a required field, click Continue, and verify error messaging appears.

**Acceptance Scenarios**:

1. **Given** the user clears a required field (species, race, or gender), **When** they click Continue, **Then** a toast message explains what needs correction, invalid fields are marked with helper text, and navigation is blocked until corrected.  
2. **Given** the user clicks the back arrow in the header, **When** they return to Step 2 and then navigate forward to Step 3, **Then** all Step 3 data re-populates and progress indicator updates correctly (2/4 then 3/4).

---

### Edge Cases

- **Request GPS button clicked**: The button is displayed but currently non-functional (placeholder for future GPS feature); no action occurs when clicked.
- **Future "Date of disappearance"**: The date picker defaults to today and blocks selection of any future dates (past dates only); no separate error state required.
- **Species selection changes race field**: The race input is disabled until a species is selected; changing species clears previously entered race text and marks field as required once enabled.
- **Additional description exceeds 500 characters**: Further input is blocked at the limit with character counter visible; other fields remain unchanged.
- **Direct URL access to Step 3**: User is redirected to Step 1 to ensure complete data collection from the beginning (consistent with 034 and 037).
- **Browser refresh or back button**: Flow state is cleared; user must restart from Step 1 (consistent with 034 and 037).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: On web, Step 3 MUST appear immediately after the Animal Photo screen (spec 037) and before contact details (Step 4), showing header/back arrow, title "Animal description", and progress "3/4" matching the established web form pattern.  
- **FR-002**: The back arrow button MUST navigate to Step 2 with state persistence; the progress indicator MUST update when re-entering Step 3 (consistent with specs 034 and 037).  
- **FR-003**: The "Date of disappearance" field MUST default to today's date (or last saved value if returning to Step 3) and use an HTML5 date picker allowing only past dates (including today), blocking future dates.  
- **FR-004**: Selecting a date MUST update the web Missing Pet flow state so the value persists across navigation within the flow until completion or cancellation.  
- **FR-005**: The "Animal species" dropdown MUST use a fixed curated list of species (bundled with app or fetched from backend API), display a clear placeholder, and require a selection before Continue is enabled.  
- **FR-006**: The "Animal race" field MUST be implemented as a text input that stays disabled until a species is chosen; when species changes, previously entered race text MUST be cleared and field becomes required once enabled.  
- **FR-007**: The gender selector MUST present two options (Female, Male) as mutually exclusive choices using established web component patterns; at least one MUST be selected before Continue is enabled.  
- **FR-008**: "Animal age (optional)" MUST accept numeric input from 0–40, prevent negative values and decimals, and allow empty state (truly optional field).  
- **FR-009**: The "Request GPS position" button MUST be displayed on the screen but remain non-functional (placeholder for future GPS feature to be implemented in separate specification); clicking it has no effect.  
- **FR-010**: "Animal additional description (optional)" MUST provide a multi-line textarea supporting exactly 500 characters with a live character counter; hard limit prevents further input at 500 and truncates pasted text.  
- **FR-011**: The Continue button MUST remain enabled at all times (consistent with 034 and 037); when tapped with invalid/missing required fields, it MUST validate on submit, show a toast message, highlight invalid fields with helper text, and block navigation until corrected.  
- **FR-012**: All inputs MUST persist within the in-memory web Missing Pet flow state (React Context, Redux, or similar) so navigation within the flow does not wipe Step 3 data; state is retained until flow completion or cancellation.  
- **FR-013**: All interactive UI elements MUST have `data-testid` attributes following the `{screen}.{element}.{action}` naming convention (e.g., `animalDescription.continue.click`, `animalDescription.requestGps.click`) for automated testing.  
- **FR-014**: Screen layout MUST be responsive and adapt to different viewport sizes (mobile: 320px+, tablet: 768px+, desktop: 1024px+) using the same responsive patterns as specs 034 and 037.  
- **FR-015**: Screen MUST display in a centered white card with border on desktop/tablet viewports (consistent with 034 and 037 web form design).  
- **FR-016**: Flow state MUST be managed via React state management (Context API, custom hooks, or state management library, consistent with 034 and 037).  
- **FR-017**: Flow state MUST be cleared when user cancels the flow (clicks back arrow) or completes the flow.  
- **FR-018**: Browser back button MUST be handled to cancel the entire flow, return to pet list, and clear flow state (same behavior as in-app back arrow, consistent with 034 and 037).  
- **FR-019**: Browser refresh MUST clear all flow state and return user to pet list (no persistence to sessionStorage or localStorage, consistent with 034 and 037).  
- **FR-020**: Navigation MUST use React Router with URL-based routing for each flow step (consistent with 034 and 037).  
- **FR-021**: Direct URL access to Step 3 without active flow state MUST redirect user to Step 1 to ensure complete data collection from the beginning (consistent with 034 and 037).  
- **FR-022**: Error messages and validation feedback MUST use the same toast and inline error patterns established in specs 034 and 037 for consistency.

### Key Entities *(include if feature involves data)*

- **Flow State** (existing): Temporary session data object that persists user inputs (microchip number, photo, animal description fields) as user progresses through the 4-step Missing Pet flow on web. Managed via React state, passed to each step component. Cleared on flow cancellation or completion. Not persisted to localStorage/sessionStorage - browser refresh clears all state.
- **Animal Description Details** (new fields added to Flow State): Contains disappearanceDate, animalSpecies, animalRace, animalGender, animalAge (optional), and animalDescription (optional, max 500 chars).
- **Species Taxonomy**: Fixed curated list of animal species available for selection. If fetched from backend, should be cached in Flow State for offline availability during step progression.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 0% of QA test cases allow future dates, missing required species/race, or missing gender to pass validation into Step 4 on web.  
- **SC-002**: 95% of web sessions preserve all Step 3 entries when the user navigates away and returns within the same reporting session (per analytics/session tracking).  
- **SC-003**: The species dropdown displays at least 15 common animal species in the curated list and loads in under 500ms on first render.  
- **SC-004**: Form interaction (entering date, selecting species, typing description) completes in under 3 seconds on standard broadband (10 Mbps+) for all fields.

## Assumptions

- This feature targets modern web browsers (Chrome, Firefox, Safari, Edge) with ES2015+ support; no IE11 compatibility required.
- The curated species list is either bundled with the web app or fetched from a backend API. Backend integration (if needed) will be handled in a separate specification.
- Gender options remain binary (Female/Male) for this release; future inclusivity updates will be handled separately.
- The age field collects whole years; if unknown, user can leave blank or enter "0".
- The "Request GPS position" button is a visual placeholder only; actual GPS location capture functionality will be implemented in a separate specification.
- The date picker blocks future dates natively via HTML5 input type="date" max attribute or custom validation.
- Flow state management architecture is consistent with specs 034 and 037 (React Context or custom hooks, in-memory only, no localStorage/sessionStorage).
- Browser back button and direct URL access handling follows the same patterns as 034 and 037.
- Design assets, copy, and localization strings will be provided by Product/Design teams or extracted from the Figma design.

## Dependencies

- Web navigation scaffolding and flow state container from specs 034 (Web Microchip Number Screen) and 037 (Web Animal Photo Screen).
- Static configuration or backend API for the curated species list.
- React Router for URL-based routing (consistent with 034 and 037).
- Existing React component patterns and styling from 034 and 037 for consistency.

## Out of Scope

- Persisting Step 3 data to backend APIs or drafts (handled in future backend integration specification).
- Copywriting/localization for helper text beyond referencing provided Figma/product strings.
- Any implementation for non-web platforms (Android/iOS already handled in separate specs).
- Enhancements to progress indicator behavior beyond what existing web app already defines.
- GPS location capture functionality - the "Request GPS position" button is a placeholder only; actual GPS implementation will be in a separate specification.
- Manual latitude/longitude input fields (to be added with GPS feature in separate specification).
- Image preview functionality (handled separately in Step 2 photo upload).

