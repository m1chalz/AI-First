# Feature Specification: Android Microchip Number Screen

**Feature Branch**: `038-android-chip-number-screen`  
**Created**: 2025-12-02  
**Status**: Draft  
**Input**: User description: "Implementation of microchip number screen for Android platform based on iOS spec 019. Part of Report Missing Pet flow (step 1/4). Chip number is optional with formatted input 00000-00000-00000."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Report Missing Pet with Known Microchip (Priority: P1)

User wants to report their missing pet and knows the pet's microchip number, which they want to include to increase chances of reuniting with their pet.

**Why this priority**: This is the core happy path - providing microchip information is the most efficient way to reunite with a lost pet. This story delivers immediate value by allowing users to complete the first step of reporting a missing pet.

**Independent Test**: Can be fully tested by navigating to this screen from the pet list, entering a valid microchip number (formatted as 00000-00000-00000), and verifying the input is saved when tapping Continue. Delivers value by capturing critical identification information.

**Acceptance Scenarios**:

1. **Given** user is on the pet list screen, **When** they initiate the "report missing pet" flow, **Then** they see the microchip number screen with title "Microchip number" and progress indicator "1/4" in the top app bar
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

**Independent Test**: Can be fully tested by starting the flow, optionally entering data, tapping the back button in the top app bar, and verifying the user returns to the pet list screen with the flow cancelled.

**Acceptance Scenarios**:

1. **Given** user is on the microchip number screen (step 1/4), **When** they tap the back button in the top app bar, **Then** they are returned to the pet list screen and the flow is cancelled
2. **Given** user has entered partial microchip data and taps back, **When** they return to the pet list, **Then** the entered data is not persisted (flow state is cleared)

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

- What happens when user enters non-numeric characters into the microchip field? (Only digits accepted via numeric keyboard input type)
- What happens when user enters fewer than 15 digits? (Allowed since field is optional)
- What happens when user attempts to enter more than 15 digits? (Input limited to 15 digits)
- What happens when user rapidly types digits? (Formatting keeps up without lag or cursor jumping)
- How does the screen handle different device sizes and orientations? (Layout adapts to screen bounds with proper scrolling if needed)
- What happens if user force-quits the app during the flow? (Data does not persist across app sessions)
- What happens when system back button (hardware/gesture) is pressed? (Same behavior as top app bar back button)

## Clarifications

### Session 2025-12-02

- Q: How should the input field handle attempts to enter non-numeric characters? → A: Use numeric keyboard type (KeyboardType.Number) - prevents non-numeric input at source
- Q: Should hyphens be stored as part of the microchip number data in flow state? → A: Store only digits (no hyphens) - hyphens are display formatting only
- Q: Where should flow state be implemented and managed? → A: NavGraph-scoped ViewModel owns flow state, accessible to all screens in the flow via Koin
- Q: How should cursor position behave when hyphens are auto-inserted during formatting? → A: Natural system behavior via VisualTransformation - no manual cursor management
- Q: How should the 4-step "report missing pet" flow be managed? → A: Nested Navigation Graph with shared ViewModel scoped to that graph
- Q: Does this spec include creating the nested NavGraph and shared flow ViewModel infrastructure? → A: No - screen implementation only; navigation infrastructure is prepared in spec 018 (018-android-missing-pet-flow) awaiting merge
- Q: Should the microchip field prefill when the pet profile already has a chip number? → A: Do not auto-prefill from profile; only show previously entered flow state data with placeholder 00000-00000-00000
- Q: What analytics should be captured from this screen? → A: No analytics events; do not log interactions or microchip presence
- Q: When should microchip data sync to backend? → A: Only persist in flow state until final submission; no backend calls from this screen

## Requirements *(mandatory)*

### Dependencies

- **Spec 018** (018-android-missing-pet-flow): Provides nested Navigation Graph infrastructure and shared flow ViewModel. Must be merged before this feature can be fully integrated.

### Functional Requirements

- **FR-001**: Screen MUST display "Microchip number" as the title in the TopAppBar
- **FR-002**: Screen MUST display "1/4" as a progress indicator in the TopAppBar
- **FR-003**: TopAppBar MUST include a navigation icon (back arrow) that dismisses the entire flow and returns to the pet list screen
- **FR-004**: Screen MUST display a large heading "Microchip number" below the TopAppBar
- **FR-005**: Screen MUST display explanatory text: "Microchip identification is the most efficient way to reunite with your pet. If your pet has been microchipped and you know the microchip number, please enter it here."
- **FR-006**: Screen MUST include an OutlinedTextField labeled "Microchip number (optional)"
- **FR-007**: Input field MUST display placeholder text "00000-00000-00000" when empty
- **FR-008**: Input field MUST automatically format entered digits as 00000-00000-00000 (inserting hyphens at positions 6 and 12) using VisualTransformation
- **FR-009**: Input field MUST use numeric keyboard type (KeyboardType.Number) to accept only numeric digits (0-9)
- **FR-010**: Input field MUST limit input to maximum 15 digits (not counting display hyphens)
- **FR-011**: Formatting MUST be applied via VisualTransformation for display; raw digits stored in ViewModel state
- **FR-012**: Screen MUST display a "Continue" Button at the bottom
- **FR-013**: Continue button MUST remain enabled at all times (regardless of whether input is empty or filled)
- **FR-014**: Tapping Continue MUST save the entered microchip number as digits only (or empty string if no input) to the flow state
- **FR-015**: Tapping Continue MUST navigate to the next screen in the flow (step 2/4)
- **FR-016**: Entered microchip data MUST persist when navigating forward to step 2/4 and back to step 1/4 within the same flow session
- **FR-017**: Tapping the back button in TopAppBar MUST close the entire flow and return to pet list without saving any flow data
- **FR-018**: System back button/gesture MUST behave the same as TopAppBar back button
- **FR-019**: Screen MUST be implemented using Jetpack Compose
- **FR-020**: ViewModel MUST follow MVI pattern with single StateFlow<UiState>, sealed UserIntent, and SharedFlow<UiEffect>
- **FR-021**: Flow state MUST be shared via NavGraph-scoped ViewModel using Koin's navigation scope
- **FR-022**: All interactive Composables MUST have testTag modifiers for UI testing
- **FR-023**: Microchip field MUST only display values sourced from the current flow state; no auto-prefill from pet profile or backend data
- **FR-024**: Microchip digits MUST remain local to the flow state until the overall Report Missing Pet submission; this screen MUST NOT trigger backend writes

### Non-Functional Requirements

- **NFR-001**: Screen MUST NOT emit analytics or logging events related to microchip input presence or values

### Key Entities

- **Microchip Number**: A 15-digit numeric identifier. Stored as string containing only digits (e.g., "123456789012345") to preserve leading zeros. Displayed to user with formatting as 00000-00000-00000 (hyphens at positions 6 and 12), but hyphens are not persisted. Optional field used to identify microchipped pets.
- **Flow State**: Shared state object holding data across the 4-step "report missing pet" flow. Managed by a NavGraph-scoped ViewModel that persists for the duration of the nested navigation graph. Contains microchip number and fields for subsequent steps.
- **ChipNumberUiState**: Immutable data class representing the current UI state of the chip number screen. Contains formatted chip number for display, loading states if any, and other UI-relevant properties.
- **ChipNumberUserIntent**: Sealed class representing all possible user actions on this screen (UpdateChipNumber, ContinueClicked, BackClicked).
- **ChipNumberUiEffect**: Sealed class for one-off navigation events (NavigateToPhoto, NavigateBack).

## Success Criteria *(mandatory)*

### Functional Outcomes

- **SC-001**: Users can enter a 15-digit microchip number and see it automatically formatted as `00000-00000-00000`
- **SC-002**: Users can proceed to the next step (2/4) whether or not they enter a microchip number
- **SC-003**: Users can cancel the flow from this screen using the back button and return to the pet list
- **SC-004**: Data entered on this screen persists correctly when user navigates forward to step 2/4 and back to step 1/4 within the same flow session
- **SC-005**: Screen layout adapts correctly to common Android device sizes without content clipping or overlap
- **SC-006**: Unit tests for ViewModel achieve 80% line and branch coverage
