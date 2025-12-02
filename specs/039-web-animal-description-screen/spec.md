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

## Clarifications

### Session 2025-12-02

- Q: How is the species list sourced - bundled with app or fetched from backend API? → A: Species are predefined as TypeScript types in webApp/src/types/animal.ts (AnimalSpecies: 'DOG' | 'CAT' | 'BIRD' | 'RABBIT' | 'OTHER')
- Q: What format should gender field values use - match existing AnimalSex type or different format? → A: Use existing AnimalSex type (MALE, FEMALE) but display with capitalized labels (Male, Female). Same for species - display as Dog, Cat, Bird, Rabbit, Other
- Q: How long should validation error toast messages display? → A: 5 seconds for all validation toasts
- Q: Should the date field use `disappearanceDate` or match existing Animal type's `lastSeenDate`? → A: Use `lastSeenDate` to match existing Animal type in webApp/src/types/animal.ts
- Q: Should the race/breed field be free-text, autocomplete, or dropdown? → A: Free-text input field with no autocomplete or suggestions
- Q: How should back arrow navigation work across all flow steps? → A: Step 1/4: closes entire form and returns to pet list. Step 2/4, 3/4: navigates to previous step while preserving flow state. This provides better multi-step form UX. NOTE: Specs 034 and 037 need updating to implement this behavior.

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

1. **Given** the user clears a required field (species, race, or gender), **When** they click Continue, **Then** a toast message displays for 5 seconds explaining what needs correction, invalid fields are marked with inline helper text, and navigation is blocked until corrected.  
2. **Given** the user clicks the in-app back arrow in the header on Step 3, **When** they navigate to Step 2 (preserving flow state) and then forward again to Step 3, **Then** all previously entered Step 3 data re-populates and progress indicator updates correctly (3/4→2/4→3/4). Note: Browser back button behavior is different - it cancels the entire flow.

---

### Edge Cases

- **Request GPS button clicked**: The button is displayed but currently non-functional (placeholder for future GPS feature); no action occurs when clicked.
- **Future "Date of disappearance"**: The date picker defaults to today and blocks selection of any future dates (past dates only); no separate error state required.
- **Species selection changes race field**: The race input is disabled until a species is selected; changing species clears previously entered race text and marks field as required once enabled.
- **Additional description exceeds 500 characters**: Further input is blocked at the limit with character counter visible; other fields remain unchanged.
- **Direct URL access to Step 3**: User is redirected to Step 1 to ensure complete data collection from the beginning (consistent with 034 and 037).
- **Browser refresh**: Flow state is cleared; user must restart from Step 1 (consistent with 034 and 037).
- **Browser back button vs in-app back arrow**: Browser back button cancels entire flow and returns to pet list (clearing all state). In-app back arrow navigates to previous step while preserving flow state (Step 3→Step 2, Step 2→Step 1, Step 1→close flow).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: On web, Step 3 MUST appear immediately after the Animal Photo screen (spec 037) and before contact details (Step 4), showing header/back arrow, title "Animal description", and progress "3/4" matching the established web form pattern.  
- **FR-002**: The back arrow button MUST navigate to the previous step (Step 2) while preserving all flow state (multi-step form navigation pattern); the progress indicator MUST update accordingly. NOTE: This differs from specs 034/037 which close the entire flow - those specs need updating for consistent UX.  
- **FR-003**: The "Date of disappearance" field (stored as `lastSeenDate` in flow state) MUST default to today's date (or last saved value if returning to Step 3) and use an HTML5 date picker allowing only past dates (including today), blocking future dates.  
- **FR-004**: Selecting a date MUST update the web Missing Pet flow state so the value persists across navigation within the flow until completion or cancellation.  
- **FR-005**: The "Animal species" dropdown MUST use the predefined AnimalSpecies type from webApp/src/types/animal.ts (stored as uppercase: DOG, CAT, BIRD, RABBIT, OTHER), display options with capitalized labels (Dog, Cat, Bird, Rabbit, Other), show a clear placeholder, and require a selection before Continue is enabled.  
- **FR-006**: The "Animal race" field (stored as `breed` in flow state matching Animal type) MUST be implemented as a free-text input with no autocomplete that stays disabled until a species is chosen; when species changes, previously entered race text MUST be cleared and field becomes required once enabled.  
- **FR-007**: The gender selector MUST present two options displaying as "Male" and "Female" (stored as AnimalSex values MALE, FEMALE from webApp/src/types/animal.ts) as mutually exclusive choices using established web component patterns; at least one MUST be selected before Continue is enabled.  
- **FR-008**: "Animal age (optional)" MUST accept numeric input from 0–40, prevent negative values and decimals, and allow empty state (truly optional field).  
- **FR-009**: The "Request GPS position" button MUST be displayed on the screen but remain non-functional (placeholder for future GPS feature to be implemented in separate specification); clicking it has no effect.  
- **FR-010**: "Animal additional description (optional)" MUST provide a multi-line textarea supporting exactly 500 characters with a live character counter; hard limit prevents further input at 500 and truncates pasted text.  
- **FR-011**: The Continue button MUST remain enabled at all times (consistent with 034 and 037); when tapped with invalid/missing required fields, it MUST validate on submit, show a toast message for 5 seconds, highlight invalid fields with inline helper text, and block navigation until corrected.  
- **FR-012**: All inputs MUST persist within the in-memory web Missing Pet flow state (React Context, Redux, or similar) so navigation within the flow does not wipe Step 3 data; state is retained until flow completion or cancellation.  
- **FR-013**: All interactive UI elements MUST have `data-testid` attributes following the `{screen}.{element}.{action}` naming convention (e.g., `animalDescription.continue.click`, `animalDescription.requestGps.click`) for automated testing.  
- **FR-014**: Screen layout MUST be responsive and adapt to different viewport sizes (mobile: 320px+, tablet: 768px+, desktop: 1024px+) using the same responsive patterns as specs 034 and 037.  
- **FR-015**: Screen MUST display in a centered white card with border on desktop/tablet viewports (consistent with 034 and 037 web form design).  
- **FR-016**: Flow state MUST be managed via React state management (Context API, custom hooks, or state management library, consistent with 034 and 037).  
- **FR-017**: Flow state MUST be cleared when user cancels the flow (clicks back arrow) or completes the flow.  
- **FR-018**: Browser back button MUST be handled to cancel the entire flow, return to pet list, and clear flow state. NOTE: This is different from in-app back arrow which navigates to previous step - browser back button always cancels the flow.  
- **FR-019**: Browser refresh MUST clear all flow state and return user to pet list (no persistence to sessionStorage or localStorage, consistent with 034 and 037).  
- **FR-020**: Navigation MUST use React Router with URL-based routing for each flow step (consistent with 034 and 037).  
- **FR-021**: Direct URL access to Step 3 without active flow state MUST redirect user to Step 1 to ensure complete data collection from the beginning (consistent with 034 and 037).  
- **FR-022**: Error messages and validation feedback MUST use the same toast and inline error patterns established in specs 034 and 037 for consistency.

### Key Entities *(include if feature involves data)*

- **Flow State** (existing): Temporary session data object that persists user inputs (microchip number, photo, animal description fields) as user progresses through the 4-step Missing Pet flow on web. Managed via React state, passed to each step component. Cleared on flow cancellation or completion. Not persisted to localStorage/sessionStorage - browser refresh clears all state.
- **Animal Description Details** (new fields added to Flow State): Contains lastSeenDate (string, ISO 8601 YYYY-MM-DD format), species (AnimalSpecies enum: DOG|CAT|BIRD|RABBIT|OTHER), breed (string, free-text), sex (AnimalSex enum: MALE|FEMALE), age (number, optional), and description (string, optional, max 500 chars). Display labels use capitalized format (e.g., Dog, Male). Field names match Animal type from webApp/src/types/animal.ts for consistency.
- **Species Taxonomy**: Predefined TypeScript type AnimalSpecies from webApp/src/types/animal.ts defining available species: DOG, CAT, BIRD, RABBIT, OTHER.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 0% of QA test cases allow future dates, missing required species/race, or missing gender to pass validation into Step 4 on web.  
- **SC-002**: 95% of web sessions preserve all Step 3 entries when the user navigates away and returns within the same reporting session (per analytics/session tracking).  
- **SC-003**: The species dropdown displays all 5 predefined species options (DOG, CAT, BIRD, RABBIT, OTHER) and loads in under 500ms on first render.  
- **SC-004**: Form interaction (entering date, selecting species, typing description) completes in under 3 seconds on standard broadband (10 Mbps+) for all fields.
- **SC-005**: 100% of test cases verify that in-app back arrow navigates to Step 2 (preserving data) while browser back button cancels flow and returns to pet list (clearing all data).

## Assumptions

- This feature targets modern web browsers (Chrome, Firefox, Safari, Edge) with ES2015+ support; no IE11 compatibility required.
- The species dropdown uses existing AnimalSpecies TypeScript type (DOG, CAT, BIRD, RABBIT, OTHER) from webApp/src/types/animal.ts.
- Gender options remain binary (Male/Female, stored as MALE/FEMALE) for this release; the existing UNKNOWN option from AnimalSex type is not included in this form; future inclusivity updates will be handled separately.
- The age field collects whole years; if unknown, user can leave blank or enter "0".
- The "Request GPS position" button is a visual placeholder only; actual GPS location capture functionality will be implemented in a separate specification.
- The date picker blocks future dates natively via HTML5 input type="date" max attribute or custom validation.
- Flow state management architecture is consistent with specs 034 and 037 (React Context or custom hooks, in-memory only, no localStorage/sessionStorage).
- Browser back button and direct URL access handling follows the same patterns as 034 and 037.
- Design assets, copy, and localization strings will be provided by Product/Design teams or extracted from the Figma design.

## Dependencies

- Web navigation scaffolding and flow state container from specs 034 (Web Microchip Number Screen) and 037 (Web Animal Photo Screen).
- Existing AnimalSpecies type definition from webApp/src/types/animal.ts.
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

## Implementation Notes

- **IMPORTANT**: Specs 034 (Web Chip Number) and 037 (Web Animal Photo) currently specify that the back arrow closes the entire flow. This needs to be updated to implement proper multi-step form navigation:
  - Step 1/4 (spec 034): back arrow closes flow and returns to pet list
  - Step 2/4 (spec 037): back arrow navigates to Step 1/4 (preserving flow state)
  - Step 3/4 (this spec): back arrow navigates to Step 2/4 (preserving flow state)
  - Browser back button: always cancels entire flow and returns to pet list (across all steps)

