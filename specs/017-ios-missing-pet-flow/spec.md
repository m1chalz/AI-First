# Feature Specification: Missing Pet Report Flow

**Feature Branch**: `017-ios-missing-pet-flow`  
**Created**: 2025-11-26  
**Status**: Draft  
**Platform**: iOS  
**Input**: User description: "Stwórz flow dodawania zaginiętego zwierzęcia. W scope wchodzi: podłączenie nawigacji pod przycisk report missing animal, stworzenie ekranów tylko z przyciskiem do przechodzenia na następny ekran, pierwszy ekran: dodanie nr chipu, drugi ekran: dodanie zdjęcia, trzeci ekran: dodanie opisu, czwarty ekran: dane kontaktowe, piąty ekran: podsumowanie, w scope jest tylko UI, platforma ios, indykator postępu też jest w scope"

## User Scenarios & Testing

### User Story 1 - Complete Missing Pet Report (Priority: P1)

A pet owner discovers their pet is missing and wants to report it through the app. They navigate through a structured 4-step flow that collects all necessary information (chip number, photo, description, contact details) and then view a summary screen before submission.

**Why this priority**: This is the core functionality - without this flow, users cannot report missing pets at all. It's the MVP that delivers immediate value.

**Independent Test**: Can be fully tested by tapping "report missing animal" button, navigating through all 4 data collection screens with progress indicator, then viewing the summary screen without progress indicator.

**Acceptance Scenarios**:

1. **Given** user is on the animal list screen, **When** user taps "report missing animal" button, **Then** system displays chip number input screen with progress indicator showing step 1 of 4
2. **Given** user is on chip number screen, **When** user taps "next" button, **Then** system displays photo upload screen with progress indicator showing step 2 of 4
3. **Given** user is on photo screen, **When** user taps "next" button, **Then** system displays description input screen with progress indicator showing step 3 of 4
4. **Given** user is on description screen, **When** user taps "next" button, **Then** system displays contact details screen with progress indicator showing step 4 of 4
5. **Given** user is on contact details screen, **When** user taps "next" button, **Then** system displays summary screen without progress indicator
6. **Given** user is on any of the 4 data collection screens, **When** user views the screen, **Then** progress indicator accurately reflects current step number (1/4, 2/4, 3/4, or 4/4)
7. **Given** user is on summary screen, **When** user views the screen, **Then** no progress indicator is displayed

---

### User Story 2 - Navigate Backwards Through Flow (Priority: P2)

A user realizes they made a mistake or want to change information on a previous screen. They need to navigate back through the flow to edit previous entries. The standard back button/gesture provides this functionality - from each screen it returns to the previous screen, and from the first screen (1/4) it exits to the animal list screen.

**Why this priority**: While not essential for MVP, this significantly improves user experience by allowing corrections without restarting the entire flow. This also provides the exit mechanism from the flow.

**Independent Test**: Can be tested by navigating to any screen (e.g., step 3), tapping back button, and verifying that previous screen displays with any previously entered data preserved. Can also test exiting by going back from step 1 to animal list.

**Acceptance Scenarios**:

1. **Given** user is on any data collection screen after step 1 (steps 2-4), **When** user taps "back" button or uses system back gesture, **Then** system displays previous screen in the flow with appropriate progress indicator
2. **Given** user is on summary screen, **When** user taps "back" button or uses system back gesture, **Then** system displays contact details screen (step 4 of 4) with progress indicator
3. **Given** user is on chip number screen (step 1 of 4), **When** user taps "back" button or uses system back gesture, **Then** system exits the flow and returns to animal list screen
4. **Given** user navigates back to a previous data collection screen, **When** screen displays, **Then** progress indicator reflects the current step number (1/4, 2/4, 3/4, or 4/4)
5. **Given** user exits the flow by going back from step 1, **When** they restart the flow later, **Then** no previously entered data is retained (fresh start)

---

### Edge Cases

- What happens when user backgrounds the app during the flow (should state be preserved or lost)?
- What happens when user rotates device during the flow?
- What happens if user tries to navigate to summary screen without completing previous screens?
- How does system handle invalid/empty inputs (should "next" button be enabled/disabled)?
- What happens when user is on the summary screen and taps "next" or tries to proceed (if such action exists)?
- Should progress indicator animate or transition when moving between steps?
- What happens to progress indicator when navigating backwards (should it update immediately)?

## Requirements

### Functional Requirements

- **FR-001**: System MUST display a "report missing animal" button accessible from the animal list screen
- **FR-002**: System MUST provide a chip number input screen as the first step of the flow (step 1 of 4)
- **FR-003**: System MUST provide a photo upload/capture screen as the second step of the flow (step 2 of 4)
- **FR-004**: System MUST provide a description input screen as the third step of the flow (step 3 of 4)
- **FR-005**: System MUST provide a contact details input screen as the fourth step of the flow (step 4 of 4)
- **FR-006**: System MUST provide a summary screen displayed after completing the 4-step flow
- **FR-007**: System MUST display a progress indicator on the 4 data collection screens showing current step (1 of 4, 2 of 4, 3 of 4, 4 of 4)
- **FR-008**: System MUST NOT display a progress indicator on the summary screen
- **FR-009**: System MUST provide a "next" button or equivalent navigation control on each screen to proceed to the next step
- **FR-010**: System MUST support backward navigation from any screen to the previous screen (including from summary back to step 4, and from step 1 back to animal list screen)
- **FR-011**: System MUST connect navigation from "report missing animal" button on animal list screen to the first screen of the flow (chip number input, step 1 of 4)
- **FR-012**: Progress indicator MUST update automatically as user navigates between the 4 data collection screens
- **FR-013**: Each screen MUST display appropriate input controls for its designated purpose (text field for chip number, image picker for photo, text area for description, contact form fields for contact details, read-only summary for final screen)

### Key Entities

**Note**: This feature is UI-only. No data persistence or backend integration is in scope. Data entities will be defined in future backend integration features.

- **MissingPetReport**: Represents the information collected through the flow (chip number, photo, description, contact details) - structure to be defined when backend integration is added
- **ProgressState**: Represents current position in the flow (current step number, total steps)

## Success Criteria

### Measurable Outcomes

- **SC-001**: Users can navigate through all 5 screens of the missing pet report flow (4 data collection screens + summary) without errors
- **SC-002**: Progress indicator accurately displays current step out of 4 total steps (1/4, 2/4, 3/4, 4/4) on data collection screens only, and is not displayed on summary screen
- **SC-003**: UI renders correctly on all supported iOS device sizes without layout issues
- **SC-004**: Users can successfully navigate backwards to any previous screen without data loss, including from summary screen back to step 4

## Assumptions

- This feature implements UI flow only - no data submission or persistence functionality
- Backend API integration will be implemented in a separate feature
- User authentication/authorization is handled separately and assumed to be complete before accessing this flow
- Standard iOS navigation patterns (navigation bar, back button) are acceptable
- Photo capture/selection uses standard iOS photo picker component
- All text labels and UI copy will be in English (localization in future feature if needed)
- No offline mode required for this MVP - app assumes network connectivity
- No autosave functionality - user must complete flow in one session

## Scope

### In Scope

- iOS platform only
- UI screens for 4 data collection steps (chip number, photo, description, contact details)
- Summary screen (displayed after completing 4 steps, without progress indicator)
- Progress indicator component (displayed only on 4 data collection screens, showing 1/4 through 4/4)
- Navigation controls (next/back buttons)
- Connection of "report missing animal" button to flow entry point
- Basic input controls for each screen (text fields, photo picker, text areas)

### Out of Scope

- Data validation logic (beyond basic UI state)
- Backend API integration
- Data persistence/storage
- Form submission functionality
- Android or Web platform implementations
- Error handling for network/API failures
- User authentication
- Photo editing capabilities
- Map integration for location selection
- Push notifications
- Email/SMS confirmation
- Search for existing missing pet reports

## Dependencies

- Existing animal list screen where "report missing animal" button will be placed
- iOS photo picker/camera access permissions (assumed to be requested appropriately)
- Navigation framework setup in iOS app (UIKit/SwiftUI coordinators)
- Design system/UI components library (if available) for consistent styling

## Open Questions

None - feature scope is clearly defined as UI-only implementation.
