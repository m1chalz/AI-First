# Feature Specification: iOS - Add Pet Name Field to Animal Details Screen

**Feature Branch**: `046-ios-pet-name-field`  
**Created**: December 4, 2025  
**Status**: Draft  
**Platform**: iOS only  
**Input**: User description: "Add optional pet name field to iOS animal details screen. The screen already exists and works, just need to add one text field, handle it in flow state, and send to server."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Add Pet Name During Missing Pet Report (Priority: P1)

When an iOS user is filling out a missing pet report, they can optionally enter the pet's name to help identify the animal and make the announcement more personal.

**Why this priority**: This is the core functionality - allowing iOS users to provide their pet's name enhances the quality of missing pet announcements and increases the likelihood of successful identification.

**Independent Test**: Can be fully tested by navigating to the animal details screen in the iOS missing pet flow, entering a pet name, submitting the form, and verifying the name appears in the created announcement.

**Acceptance Scenarios**:

1. **Given** the user is on the animal details screen, **When** they enter a pet name in the optional "Animal name" field, **Then** the name is captured in the form state and submitted with the announcement
2. **Given** the user is on the animal details screen, **When** they leave the pet name field empty, **Then** the form remains valid and the announcement is created without a pet name
3. **Given** the user has entered a pet name, **When** they submit the form, **Then** the pet name is sent to the server as the `petName` field in the API request

---

### Edge Cases

- What happens when the user enters only whitespace in the pet name field?
  - System should trim the input and treat it as empty (optional field)
- What happens when the user enters a very long pet name (e.g., 200+ characters)?
  - System should accept the input (backend validation will handle maximum length if needed)
- What happens when the user switches between screens in the flow?
  - The entered pet name should be preserved in the flow state

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST display an optional "Animal name" text field on the animal details screen (step 3/4 of missing pet flow)
- **FR-002**: System MUST allow users to enter and edit the pet name without any required validation
- **FR-003**: System MUST store the pet name value in the flow state alongside other animal details (species, breed, sex, age, etc.)
- **FR-004**: System MUST include the pet name in the API request body as the `petName` field when creating an announcement
- **FR-005**: System MUST handle empty/null pet name values gracefully (field is optional)
- **FR-006**: System MUST trim whitespace from the pet name input before sending to the API
- **FR-007**: System MUST preserve the entered pet name value when users navigate backward/forward in the multi-step flow

### Key Entities *(include if feature involves data)*

- **AnimalDetails**: Represents the animal information collected in the missing pet flow. Includes species, breed, sex, age, date of disappearance, location, and now the optional pet name.
- **Announcement**: The announcement entity created from the flow data. Contains all animal details including the optional `petName` field.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can enter a pet name on the animal details screen and see it appear in the text field
- **SC-002**: Announcements created with a pet name display the name correctly when retrieved from the API
- **SC-003**: Announcements created without a pet name are saved successfully with a null/empty `petName` field
- **SC-004**: Pet name value persists when users navigate backward and forward within the multi-step flow
- **SC-005**: iOS implementation follows SwiftUI best practices and integrates seamlessly with existing missing pet flow

