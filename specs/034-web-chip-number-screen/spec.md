# Feature Specification: Web Microchip Number Screen

**Feature Branch**: `034-web-chip-number-screen`  
**Created**: 2025-12-01  
**Status**: Draft  
**Input**: User description: "na podstawie specyfikacji 019 przygotuj podobną dla aplikacji webowej. oprzyj się na designach z figmy dla webówki: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=315-15744&m=dev"

## Clarifications

### Session 2025-12-01

- Q: How should the application handle the browser's back button during the 4-step flow? → A: Treat browser back same as in-app back arrow (cancel flow, return to pet list, clear state)
- Q: Should flow state persist across browser refreshes? → A: No persistence - refresh clears all flow state (user must restart from pet list)
- Q: How should navigation between screens be implemented? → A: React Router - URL-based routing with routes for each step
- Q: What should happen when a user directly accesses a flow step URL without starting from step 1? → A: Redirect to step 1 - always start flow from beginning
- Q: How should the input field handle paste operations? → A: Strip non-numeric characters automatically (sanitize pasted content to digits only)

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Report Missing Pet with Known Microchip (Priority: P1)

User wants to report their missing pet and knows the pet's microchip number, which they want to include to increase chances of reuniting with their pet.

**Why this priority**: This is the core happy path - providing microchip information is the most efficient way to reunite with a lost pet. This story delivers immediate value by allowing users to complete the first step of reporting a missing pet.

**Independent Test**: Can be fully tested by navigating to this screen from the pet list, entering a valid microchip number (formatted as 00000-00000-00000), and verifying the input is saved when clicking Continue. Delivers value by capturing critical identification information.

**Acceptance Scenarios**:

1. **Given** user is on the pet list screen, **When** they initiate the "report missing pet" flow, **Then** they see the microchip number screen with title "Microchip number" and progress indicator "1/4" in the header
2. **Given** user is on the microchip number screen, **When** they enter digits into the microchip number field, **Then** the input is automatically formatted as 00000-00000-00000 (with hyphens inserted at positions 6 and 12)
3. **Given** user enters "123456789012345", **When** the formatting is applied, **Then** they see "12345-67890-12345" displayed in the input field
4. **Given** user has entered a microchip number, **When** they click the Continue button, **Then** the entered value is saved and they proceed to the next step in the flow (step 2/4)

---

### User Story 2 - Report Missing Pet Without Microchip (Priority: P2)

User wants to report their missing pet but either doesn't know the microchip number or their pet doesn't have a microchip.

**Why this priority**: Not all pets are microchipped, and not all owners remember the number. This story ensures the feature doesn't block users who can't provide this information.

**Independent Test**: Can be fully tested by navigating to the microchip screen and clicking Continue without entering any data, verifying the flow continues to step 2/4.

**Acceptance Scenarios**:

1. **Given** user is on the microchip number screen with empty input, **When** they click the Continue button, **Then** they proceed to step 2/4 without any error or validation message
2. **Given** user starts entering a microchip number then deletes all content, **When** the input field is empty, **Then** the Continue button remains enabled and functional

---

### User Story 3 - Navigate Back from Flow (Priority: P3)

User wants to cancel the "report missing pet" flow and return to the pet list without saving any data.

**Why this priority**: Users should always have an escape route from a multi-step flow. This is lower priority than the core data entry functionality but essential for good UX.

**Independent Test**: Can be fully tested by starting the flow, optionally entering data, clicking the back arrow button in the header, and verifying the user returns to the pet list screen with the flow cancelled.

**Acceptance Scenarios**:

1. **Given** user is on the microchip number screen (step 1/4), **When** they click the back arrow button in the header, **Then** they are returned to the pet list screen and the flow is cancelled
2. **Given** user has entered partial microchip data and clicks back, **When** they return to the pet list, **Then** the entered data is not persisted (flow is cancelled completely and flow state is cleared)

---

### User Story 4 - Resume Flow with Previously Entered Data (Priority: P2)

User enters microchip data, proceeds to step 2/4, then navigates back to step 1/4 to review or edit their input.

**Why this priority**: In multi-step flows, users often want to review or correct earlier entries. This story ensures data persists during forward navigation within the active flow session.

**Independent Test**: Can be fully tested by entering data on step 1/4, advancing to step 2/4, then using the back navigation within the flow to return to step 1/4 and verify the previously entered microchip number is still displayed.

**Acceptance Scenarios**:

1. **Given** user enters "12345-67890-12345" and clicks Continue to reach step 2/4, **When** they navigate back within the flow to step 1/4, **Then** they see "12345-67890-12345" still populated in the input field
2. **Given** user modifies the microchip number on step 1/4 after returning from step 2/4, **When** they click Continue again, **Then** the updated value is saved for the flow

---

### Edge Cases

- What happens when user enters non-numeric characters into the microchip field? (Should be prevented via input type="tel" or pattern validation)
- What happens when user pastes content with non-numeric characters (e.g., "ABC123XYZ456")? (Non-numeric characters are automatically stripped; only digits remain and are formatted)
- What happens when user pastes more than 15 digits? (Accept first 15 digits only, discard excess)
- What happens when user enters fewer than 15 digits? (Should be allowed since field is optional)
- What happens when user attempts to enter more than 15 digits? (Input should be limited to 15 digits)
- What happens when user rapidly types digits? (Formatting should keep up without lag or cursor jumping)
- How does the screen handle different viewport sizes (mobile, tablet, desktop)? (Layout should be responsive and centered)
- What happens if user refreshes the browser during the flow? (All flow state is cleared; user returns to pet list and must restart flow)
- What happens when user uses browser back button instead of the in-app back arrow? (Treated same as in-app back arrow: cancels flow, returns to pet list, clears all flow state)
- What happens when user directly accesses a flow step URL (e.g., types /report-missing/step2 or uses bookmarked URL)? (Redirected to step 1 to ensure complete data collection from the beginning)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Screen MUST display a header with back arrow button (left), "Microchip number" title (center), and "1/4" progress indicator (right)
- **FR-002**: Back arrow button MUST dismiss the entire flow and return to the pet list screen
- **FR-003**: Screen MUST display a large heading "Identification by Microchip" below the header
- **FR-004**: Screen MUST display explanatory text: "Microchip identification is the most efficient way to reunite with your pet. If your pet has been microchipped and you know the microchip number, please enter it here."
- **FR-005**: Screen MUST include an input field labeled "Microchip number (optional)"
- **FR-006**: Input field MUST display placeholder text "00000-00000-00000" when empty
- **FR-007**: Input field MUST automatically format entered digits as 00000-00000-00000 (inserting hyphens at positions 6 and 12)
- **FR-008**: Input field MUST accept only numeric digits (0-9) via input type or validation
- **FR-009**: Input field MUST limit input to maximum 15 digits (not counting hyphens)
- **FR-010**: Formatting MUST insert hyphens automatically as user types without requiring manual input; cursor position should follow natural browser behavior without manual management
- **FR-011**: Screen MUST display a "Continue" button at the bottom
- **FR-012**: Continue button MUST remain enabled at all times (regardless of whether input is empty or filled)
- **FR-013**: Clicking Continue MUST save the entered microchip number as digits only without hyphens (or empty string if no input) to the flow state
- **FR-014**: Clicking Continue MUST navigate to the next screen in the flow (step 2/4)
- **FR-015**: Entered microchip data MUST persist when navigating forward to step 2/4 and back to step 1/4 within the same flow session
- **FR-016**: Clicking the back arrow button in header MUST close the entire flow and return to pet list without saving any flow data
- **FR-017**: Screen layout MUST be responsive and adapt to different viewport sizes (mobile: 320px+, tablet: 768px+, desktop: 1024px+)
- **FR-018**: Screen MUST display in a centered white card with border on desktop/tablet viewports (as shown in Figma design)
- **FR-019**: Flow State MUST be managed via React state management (Context API, custom hooks, or state management library)
- **FR-020**: Flow State MUST persist microchip number and other inputs as user progresses through the 4-step "report missing pet" flow
- **FR-021**: Flow State MUST be cleared when user cancels the flow (clicks back arrow) or completes the flow
- **FR-022**: Browser back button MUST be handled to cancel the entire flow, return to pet list, and clear flow state (same behavior as in-app back arrow)
- **FR-023**: Browser refresh MUST clear all flow state and return user to pet list (no persistence to sessionStorage or localStorage)
- **FR-024**: Navigation MUST use React Router with URL-based routing for each flow step (each step has its own route/URL)
- **FR-025**: Direct URL access to any flow step (step 2/4, 3/4, or 4/4) without active flow state MUST redirect user to step 1/4 to ensure complete data collection
- **FR-026**: Paste operations MUST automatically strip all non-numeric characters from pasted content and apply formatting to remaining digits (e.g., pasting "ABC123XYZ456" results in "12345-6" displayed)

### Key Entities

- **Microchip Number**: A 15-digit numeric identifier. Stored as string containing only digits (e.g., "123456789012345") to preserve leading zeros. Displayed to user with formatting as 00000-00000-00000 (hyphens at positions 6 and 12), but hyphens are not persisted. Optional field used to identify microchipped pets.
- **Flow State**: Temporary session data object that persists microchip number and other inputs as user progresses through the 4-step "report missing pet" flow. Managed via React state (Context API or custom hook), passed to each step component in the flow. Cleared when user cancels or completes the flow.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can enter a 15-digit microchip number and see it automatically formatted as `00000-00000-00000`
- **SC-002**: Users can proceed to the next step (2/4) whether or not they enter a microchip number
- **SC-003**: Users can cancel the flow from this screen using the header back arrow and return to the pet list
- **SC-004**: Data entered on this screen persists correctly when user navigates forward to step 2/4 and back to step 1/4 within the same flow session
- **SC-005**: Screen layout adapts correctly to mobile (320px), tablet (768px), and desktop (1024px+) viewports without content clipping or horizontal scroll
- **SC-006**: Input formatting responds instantly (< 100ms) to user typing without visible lag or cursor position issues
