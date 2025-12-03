# Feature Specification: Web Missing Pet Announcement Submission

**Feature Branch**: `043-web-announcement-submission`  
**Created**: 2025-12-03  
**Status**: Draft  
**Input**: User description: "integracja formularza w aplikacji webowej z backendem. po kliknięiu continue na ekranie z danymi kontaktowymi, aplikacja ma utworzyć ogłoszenie przez wywołanie endpointu POST /api/v1/announcements a następnie zuploadowć zdjęcie przez POST /api/v1/announcements/:id/photo. Po wszystkim przechodzimy do ekranu z podsumowaniem i wyświeltamy użytkownikowi management password."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Submit Missing Pet Announcement (Priority: P1)

A pet owner completes the missing pet report form (microchip, photo, details, contact info) and submits it to create a public announcement that others can view.

**Why this priority**: This is the core functionality that enables users to report their missing pets. Without this, the entire flow is unusable - users can fill out the form but cannot actually create an announcement. This delivers immediate value by making announcements visible to potential finders.

**Independent Test**: Can be fully tested by completing the report flow through all screens, clicking Continue on the contact screen, and verifying that a new announcement appears in the public announcement list and the user receives a management password.

**Acceptance Scenarios**:

1. **Given** a user has completed all form steps (microchip, photo, animal details, contact info), **When** they click "Continue" on the contact screen, **Then** the system creates an announcement via the backend API and navigates to the summary screen
2. **Given** an announcement was successfully created, **When** the user views the summary screen, **Then** they see their management password displayed prominently with instructions to save it
3. **Given** an announcement includes a photo, **When** the announcement is created, **Then** the photo is uploaded to the backend and associated with the announcement
4. **Given** a user views the public announcement list, **When** they look for their recently created announcement, **Then** it appears with all submitted details visible

---

### User Story 2 - Handle Submission Errors (Priority: P2)

When network issues or validation errors occur during submission, users receive clear feedback and can retry without losing their data.

**Why this priority**: Error handling is critical for user trust and data integrity, but the happy path (P1) must work first. This prevents frustration when submissions fail but doesn't block the core functionality.

**Independent Test**: Can be tested by simulating network failures or sending invalid data, verifying that users see appropriate error messages and can retry submission with their form data preserved.

**Acceptance Scenarios**:

1. **Given** a user submits their announcement, **When** the network request fails, **Then** they see an error message and can retry the submission
2. **Given** a user submits invalid data, **When** the backend returns validation errors, **Then** they see specific error messages indicating which fields need correction
3. **Given** a submission fails, **When** the user retries, **Then** all their previously entered data remains in the form

---

### User Story 3 - Display Management Password Securely (Priority: P2)

After successfully creating an announcement, users receive their management password with clear instructions on how to use and store it safely.

**Why this priority**: While receiving the password is essential (covered in P1), providing proper UX guidance on password security and usage is a quality-of-life improvement that enhances user experience but doesn't block core functionality.

**Independent Test**: Can be tested by creating an announcement and verifying that the summary screen displays the management password with appropriate warnings and usage instructions.

**Acceptance Scenarios**:

1. **Given** a user successfully submits their announcement, **When** they reach the summary screen, **Then** the management password is displayed in a highlighted, easily readable format
2. **Given** a management password is displayed, **When** the user views the screen, **Then** they see clear instructions explaining that this password is needed to edit or delete the announcement later
3. **Given** a user is viewing their management password, **When** they navigate away from the summary screen, **Then** they receive a confirmation prompt warning them to save the password before leaving

---

### Edge Cases

- How does the system handle duplicate microchip numbers (if the backend enforces uniqueness)?
- What happens if the user closes their browser during submission?
- How does the system handle very large photo files that might timeout during upload?
- What happens if the user tries to submit without required location data (latitude/longitude)?
- How does the system handle submissions with only email or only phone (at least one contact method required)?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST submit announcement data to the backend API endpoint POST /api/v1/announcements when the user clicks "Continue" on the contact screen
- **FR-002**: System MUST include all collected form data in the announcement submission: microchip number, species, breed, sex, age, description, last seen date, location coordinates, contact info (phone/email), and reward amount
- **FR-003**: System MUST set announcement status to "MISSING" when creating the announcement
- **FR-004**: System MUST upload the selected photo to the backend API endpoint POST /api/v1/announcements/:id/photos after the announcement is created
- **FR-005**: System MUST use the management password returned from announcement creation for authentication when uploading the photo
- **FR-006**: System MUST navigate to the summary screen after successful announcement creation and photo upload
- **FR-007**: System MUST display the management password to the user on the summary screen
- **FR-008**: System MUST show a loading indicator while the submission is in progress
- **FR-009**: System MUST handle and display user-friendly error messages if the announcement creation fails
- **FR-010**: System MUST handle and display user-friendly error messages if the photo upload fails
- **FR-011**: System MUST preserve all form data if submission fails so users can retry without re-entering information
- **FR-012**: System MUST validate that at least one contact method (phone or email) is provided before allowing submission
- **FR-013**: System MUST validate that location coordinates (latitude/longitude) are available before allowing submission
- **FR-014**: System MUST include the announcement ID returned from the creation endpoint when uploading the photo

### Key Entities

- **Announcement**: Represents a missing pet report with all details including pet information, location, contact details, and status
- **PhotoAttachment**: Represents the uploaded photo file with metadata (filename, size, MIME type, preview URL)
- **ManagementPassword**: The authentication credential returned after announcement creation that allows future edits/deletions

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can successfully submit a complete missing pet announcement in under 10 seconds (from clicking Continue to seeing the summary screen)
- **SC-002**: 95% of valid announcement submissions complete successfully without errors
- **SC-003**: Users see their new announcement appear in the public list within 5 seconds of submission
- **SC-004**: Photo uploads complete successfully for files up to 10MB in size
- **SC-005**: Error messages are displayed within 2 seconds when submission fails
- **SC-006**: Users can successfully retry failed submissions without losing any form data
- **SC-007**: 100% of successful submissions display a management password to the user
