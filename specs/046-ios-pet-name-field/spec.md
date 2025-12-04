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
  - System should trim the input; if result is empty/whitespace-only, send `null` or omit field from API request
- What happens when the user enters a very long pet name (e.g., 200+ characters)?
  - No client-side character limit; backend validation handles maximum length and returns standard error if exceeded
- What happens when the user switches between screens in the flow?
  - The entered pet name should be preserved in the flow state

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST display an optional "Animal name" text field on the animal details screen (step 3/4 of missing pet flow) positioned after "Date of disappearance" field and before "Animal species" dropdown
- **FR-002**: System MUST allow users to enter and edit the pet name without any required validation or input filtering (accepts all Unicode characters including emoji and special characters)
- **FR-003**: System MUST store the pet name value in the flow state alongside other animal details (species, breed, sex, age, etc.)
- **FR-004**: System MUST include the pet name in the API request body as the `petName` field when creating an announcement (non-empty values only)
- **FR-005**: System MUST handle empty/null pet name values gracefully (field is optional)
- **FR-006**: System MUST trim whitespace from the pet name input; if result is empty or whitespace-only, send `null` or omit the field from JSON payload
- **FR-007**: System MUST preserve the entered pet name value when users navigate backward/forward in the multi-step flow
- **FR-008**: System MUST handle API errors (network, server errors, validation failures) using the existing error handling mechanism from the missing pet flow (display error alert, allow retry)
- **FR-009**: System MUST NOT impose client-side character limit on the pet name field (backend validation handles maximum length)

### Key Entities *(include if feature involves data)*

- **ReportMissingPetFlowState**: iOS flow state container. New optional property `petName: String?` stores the animal name value throughout the multi-step flow.
- **AnimalDescriptionViewModel**: Manages the Animal Details screen form. New published property for animal name text input, new computed property for text field model with accessibility identifier `animalDescription.animalNameTextField.input`.
- **Announcement API**: Backend `/api/announcements` endpoint accepts optional `petName` field in request body (maps to animal name from UI).

### Out of Scope

- **Accessibility/VoiceOver support**: The pet name field will not include VoiceOver labels, hints, or other accessibility features (consistent with project-wide approach of no accessibility support)

## Clarifications

### Session 2025-12-04

- Q: Should we use "Animal name" or "Pet name" consistently in UI labels and code? → A: Use "Animal name" consistently (matches Figma design)
- Q: Where should the "Animal name" field be positioned on the Animal Details screen? → A: After "Date of disappearance" field, before "Animal species" dropdown (per Figma node 706:8443)
- Q: Should the API field be named `petName` or `animalName`? → A: Use `petName` (maintains consistency with existing backend API)
- Q: What test identifier should be used for the animal name text field? → A: `animalDescription.animalNameTextField.input` (follows existing pattern from AnimalDescriptionViewModel)
- Q: What should the flow state property be named? → A: `petName` (matches API field name for data layer consistency)
- Q: Should VoiceOver/accessibility labels be added for the pet name field? → A: No accessibility support (not supported anywhere in project)
- Q: Should character input be filtered (e.g., only letters, no emoji)? → A: No filtering, accept all Unicode (default iOS behavior)
- Q: What should be sent to API when field is empty after trimming whitespace? → A: Send null or omit field from JSON
- Q: How should API errors be handled when submitting petName? → A: Use standard flow error handling (existing mechanism)
- Q: Should there be a client-side character limit for the pet name field? → A: No client-side limit, backend validates

### Naming Conventions Summary

- **UI Label**: "Animal name (optional)" - as shown in Figma design node 706:8443
- **Flow State Property**: `petName: String?` - matches API field name
- **API Field**: `petName` - existing backend field
- **Test Identifier**: `animalDescription.animalNameTextField.input` - follows existing screen pattern
- **ViewModel Property**: `petName: String` (published) - matches flow state
- **TextField Model**: `petNameTextFieldModel` - computed property returning ValidatedTextField.Model

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can enter an animal name on the animal details screen and see it appear in the text field
- **SC-002**: Announcements created with an animal name display the name correctly when retrieved from the API
- **SC-003**: Announcements created without an animal name are saved successfully with a null/empty `petName` field
- **SC-004**: Animal name value persists when users navigate backward and forward within the multi-step flow
- **SC-005**: iOS implementation follows SwiftUI best practices and integrates seamlessly with existing missing pet flow

