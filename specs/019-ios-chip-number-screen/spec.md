# Feature Specification: iOS Microchip Number Screen

**Feature Branch**: `019-ios-chip-number-screen`  
**Created**: 2025-11-26  
**Status**: Draft  
**Input**: User description: "Implementacja ekranu do podania numeru chipa. Design w figmie: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-7954&m=dev. Flow jest opisane na branchu w specyfikacjach: 017 ios. Numer chipa jest opcjonalny, button continue jest zawsze aktywny. Wpisane dane powinny być zapamiętane przed przejściem do następnego ekranu flow. Numer na texfieldzie jest formatowany - postaraj się, aby wyświetlał się zawsze sformatowany odpowiednio. Nazwa ekranu i progress 1/4 powinien być na navigation barze, w UIKit - nie twórz nic w SwiftUI. przycisk back (też na navbarze) cofa do listy zwierząt (zamyka flow)"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Report Missing Pet with Known Microchip (Priority: P1)

User wants to report their missing pet and knows the pet's microchip number, which they want to include to increase chances of reuniting with their pet.

**Why this priority**: This is the core happy path - providing microchip information is the most efficient way to reunite with a lost pet. This story delivers immediate value by allowing users to complete the first step of reporting a missing pet.

**Independent Test**: Can be fully tested by navigating to this screen from the pet list, entering a valid microchip number (formatted as 00000-00000-00000), and verifying the input is saved when tapping Continue. Delivers value by capturing critical identification information.

**Acceptance Scenarios**:

1. **Given** user is on the pet list screen, **When** they initiate the "report missing pet" flow, **Then** they see the microchip number screen with title "Microchip number" and progress indicator "1/4" in the navigation bar
2. **Given** user is on the microchip number screen, **When** they enter digits into the microchip number field, **Then** the input is automatically formatted as 00000-00000-00000 (with hyphens inserted at positions 6 and 12)
3. **Given** user enters "123456789012345", **When** the formatting is applied, **Then** they see "12345-67890-12345" displayed in the input field
4. **Given** user has entered a microchip number, **When** they tap the Continue button, **Then** the entered value is saved and they proceed to the next step in the flow (step 2/4)

---

### User Story 2 - Report Missing Pet Without Microchip (Priority: P2)

User wants to report their missing pet but either doesn't know the microchip number or their pet doesn't have a microchip.

**Why this priority**: Not all pets are microchipped, and not all owners remember the number. This story ensures the feature doesn't block users who can't provide this information.

**Independent Test**: Can be fully tested by navigating to the microchip screen and tapping Continue without entering any data, verifying the flow continues to step 2/4.

**Acceptance Scenarios**:

1. **Given** user is on the microchip number screen with empty input, **When** they tap the Continue button, **Then** they proceed to step 2/4 without any error or validation message
2. **Given** user starts entering a microchip number then deletes all content, **When** the input field is empty, **Then** the Continue button remains enabled and functional

---

### User Story 3 - Navigate Back from Flow (Priority: P3)

User wants to cancel the "report missing pet" flow and return to the pet list without saving any data.

**Why this priority**: Users should always have an escape route from a multi-step flow. This is lower priority than the core data entry functionality but essential for good UX.

**Independent Test**: Can be fully tested by starting the flow, optionally entering data, tapping the back button in the navigation bar, and verifying the user returns to the pet list screen with the flow cancelled.

**Acceptance Scenarios**:

1. **Given** user is on the microchip number screen (step 1/4), **When** they tap the back button in the navigation bar, **Then** they are returned to the pet list screen, the flow is cancelled, and ReportMissingPetCoordinator is destroyed
2. **Given** user has entered partial microchip data and taps back, **When** they return to the pet list, **Then** the entered data is not persisted (flow is cancelled completely and Flow State is cleared)

---

### User Story 4 - Resume Flow with Previously Entered Data (Priority: P2)

User enters microchip data, proceeds to step 2/4, then navigates back to step 1/4 to review or edit their input.

**Why this priority**: In multi-step flows, users often want to review or correct earlier entries. This story ensures data persists during forward navigation within the active flow session.

**Independent Test**: Can be fully tested by entering data on step 1/4, advancing to step 2/4, then using the back navigation within the flow to return to step 1/4 and verify the previously entered microchip number is still displayed.

**Acceptance Scenarios**:

1. **Given** user enters "12345-67890-12345" and taps Continue to reach step 2/4, **When** they navigate back within the flow to step 1/4, **Then** they see "12345-67890-12345" still populated in the input field
2. **Given** user modifies the microchip number on step 1/4 after returning from step 2/4, **When** they tap Continue again, **Then** the updated value is saved for the flow

---

### Edge Cases

- What happens when user enters non-numeric characters into the microchip field? (Prevented via numeric keyboard)
- What happens when user enters fewer than 15 digits? (Should be allowed since field is optional)
- What happens when user attempts to enter more than 15 digits? (Input should be limited to 15 digits)
- What happens when user rapidly types digits? (Formatting should keep up without lag or cursor jumping)
- How does the screen handle different device sizes and orientations? (Layout should adapt to safe areas and screen bounds)
- What happens if user force-quits the app during the flow? (Data should not persist across app sessions unless explicitly saved)

## Clarifications

### Session 2025-11-26

- Q: How should the input field handle attempts to enter non-numeric characters (letters, special chars)? → A: Use numeric keyboard (.numberPad) - prevents non-numeric input at source
- Q: Should hyphens be stored as part of the microchip number data in Flow State? → A: Store only digits (no hyphens) - hyphens are display formatting only
- Q: Where should Flow State be implemented and managed? → A: Coordinator owns flow state object and passes reference to each ViewModel in the flow
- Q: How should cursor position behave when hyphens are auto-inserted during formatting? → A: Natural system behavior - no manual cursor position management
- Q: Which coordinator should manage the 4-step "report missing pet" flow? → A: New ReportMissingPetCoordinator as child coordinator dedicated to the entire flow

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Screen MUST display "Microchip number" as the title in the UIKit navigation bar
- **FR-002**: Screen MUST display "1/4" as a progress indicator in the UIKit navigation bar
- **FR-003**: Navigation bar MUST include a back button that dismisses the entire flow and returns to the pet list screen
- **FR-004**: Screen MUST display a large heading "Microchip number" below the navigation bar
- **FR-005**: Screen MUST display explanatory text: "Microchip identification is the most efficient way to reunite with your pet. If your pet has been microchipped and you know the microchip number, please enter it here."
- **FR-006**: Screen MUST include an input field labeled "Microchip number (optional)"
- **FR-007**: Input field MUST display placeholder text "00000-00000-00000" when empty
- **FR-008**: Input field MUST automatically format entered digits as 00000-00000-00000 (inserting hyphens at positions 6 and 12)
- **FR-009**: Input field MUST use numeric keyboard type (.numberPad) to accept only numeric digits (0-9)
- **FR-010**: Input field MUST limit input to maximum 15 digits (not counting hyphens)
- **FR-011**: Formatting MUST insert hyphens automatically as user types without requiring manual input; cursor position should follow natural system behavior without manual management
- **FR-012**: Screen MUST display a "Continue" button at the bottom
- **FR-013**: Continue button MUST remain enabled at all times (regardless of whether input is empty or filled)
- **FR-014**: Tapping Continue MUST save the entered microchip number as digits only without hyphens (or empty string if no input) to the flow state
- **FR-015**: Tapping Continue MUST navigate to the next screen in the flow (step 2/4)
- **FR-016**: Entered microchip data MUST persist when navigating forward to step 2/4 and back to step 1/4 within the same flow session
- **FR-017**: Tapping the back button in navigation bar MUST close the entire flow and return to pet list without saving any flow data
- **FR-018**: Navigation bar components (title, progress, back button) MUST be implemented in UIKit, not SwiftUI
- **FR-019**: Main screen content (heading, description, input, button) SHOULD be implemented in SwiftUI wrapped in UIHostingController
- **FR-020**: Flow State object MUST be owned by ReportMissingPetCoordinator and passed by reference to each ViewModel in the 4-step flow
- **FR-021**: The 4-step "report missing pet" flow MUST be managed by a dedicated ReportMissingPetCoordinator (child coordinator pattern)
- **FR-022**: Parent coordinator (e.g., PetListCoordinator) MUST create ReportMissingPetCoordinator when starting the flow and destroy it when flow completes or is cancelled

### Key Entities

- **Microchip Number**: A 15-digit numeric identifier. Stored as string containing only digits (e.g., "123456789012345") to preserve leading zeros. Displayed to user with formatting as 00000-00000-00000 (hyphens at positions 6 and 12), but hyphens are not persisted. Optional field used to identify microchipped pets.
- **Flow State**: Temporary session data object that persists microchip number and other inputs as user progresses through the 4-step "report missing pet" flow. Owned and managed by ReportMissingPetCoordinator, which passes a reference to each ViewModel in the flow. Cleared when coordinator completes or cancels the flow.
- **ReportMissingPetCoordinator**: Child coordinator responsible for managing the entire 4-step "report missing pet" flow. Created by parent coordinator (e.g., PetListCoordinator) when flow starts, owns the Flow State object, and is destroyed when flow completes or is cancelled.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can enter a 15-digit microchip number and see it automatically formatted with hyphens in under 2 seconds
- **SC-002**: Users can proceed to the next step (2/4) in under 5 seconds whether or not they enter a microchip number
- **SC-003**: Users can navigate back to pet list from this screen in under 2 seconds using the navigation bar back button
- **SC-004**: Input formatting works smoothly without lag or cursor jumping even when user types rapidly (10+ characters per second)
- **SC-005**: Data entered on this screen persists correctly when user navigates forward to step 2/4 and back to step 1/4 at least 95% of the time
- **SC-006**: Screen layout adapts correctly to all iPhone screen sizes (iPhone SE to iPhone Pro Max) without content clipping or overlap
