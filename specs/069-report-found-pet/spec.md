# Feature Specification: Report Found Pet

**Feature Branch**: `069-report-found-pet`  
**Created**: 2026-01-07  
**Status**: Draft  
**Jira Ticket**: `KAN-5`  
**Design**: [Figma - Found Pet Reporting Wizard](https://www.figma.com/make/CLdBKnXW8HTucDAHFpnPBa/Found-Pet-Reporting-Wizard?t=A1LbdLNZE9jj2JHF-1)  
**Input**: User description: "take requirements from jira KAN-5"

## User Scenarios & Testing *(mandatory)*

<!--
  IMPORTANT: User stories should be PRIORITIZED as user journeys ordered by importance.
  Each user story/journey must be INDEPENDENTLY TESTABLE - meaning if you implement just ONE of them,
  you should still have a viable MVP (Minimum Viable Product) that delivers value.
  
  Assign priorities (P1, P2, P3, etc.) to each story, where P1 is the most critical.
  Think of each story as a standalone slice of functionality that can be:
  - Developed independently
  - Tested independently
  - Deployed independently
  - Demonstrated to users independently
-->

### User Story 1 - Report a found pet (Priority: P1)

As a user who found an animal, I want to report a found pet using a guided wizard, so that the information can be shared and the pet can be reunited with its owner.

**Why this priority**: This is the core value of the feature and the smallest independently valuable slice.

**Independent Test**: Can be fully tested by completing the 3-step wizard with valid data and verifying the report is saved and visible as a “found pet” report.

**Acceptance Scenarios**:

1. **Given** I am on the "Report Found Pet" wizard, **When** I provide all mandatory inputs and submit, **Then** the system confirms the report was created and the report is available to view.
2. **Given** I am on any wizard step, **When** I try to proceed without a mandatory field, **Then** the system prevents progress and shows a clear validation message for the missing input.
3. **Given** I submit a report successfully, **When** I submit again with the same or different data, **Then** each submission creates a new independent report (duplicates are allowed).

---

### User Story 2 - Navigate and complete the wizard smoothly (Priority: P2)

As a user, I want to move between the wizard steps (next/back) without losing my inputs, so I can correct details and complete the report confidently.

**Why this priority**: Reduces abandonment and user frustration; supports error correction without re-entry.

**Independent Test**: Can be tested by filling fields on each step, moving back and forth, and verifying data persists until the user cancels or submits.

**Acceptance Scenarios**:

1. **Given** I have entered some information in the wizard, **When** I navigate back and forth between steps, **Then** my already entered information remains populated.
2. **Given** I decide not to finish the wizard, **When** I cancel, **Then** the system exits the wizard and no report is created.

---

### User Story 3 - Consistent experience with “Report Missing Pet” (Priority: P3)

As a user familiar with reporting a missing pet, I want the “Report Found Pet” wizard to look and feel the same, so that it is easy to use without learning a new flow.

**Why this priority**: Consistency reduces cognitive load and design/development risk; it is explicitly required by Jira.

**Independent Test**: Can be tested by comparing the found-pet wizard to the missing-pet wizard (structure, step count, navigation patterns, and validation behavior).

**Acceptance Scenarios**:

1. **Given** I can access both wizards, **When** I open “Report Found Pet”, **Then** it uses the same step-based structure and interaction patterns as “Report Missing Pet”.

---

[Add more user stories as needed, each with an assigned priority]

### Edge Cases

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right edge cases.
-->

- What happens when the user selects a photo larger than 20MB?
- What happens when the user selects an unsupported photo format (not JPEG, PNG, or GIF)?
- What happens when the user temporarily loses connectivity during submission?
- What happens when the user provides neither phone nor email contact details?
- What happens when the user denies location permissions or GPS is unavailable?
- What happens when the user taps "Use Current Location" but GPS cannot determine position?
- What happens when the user provides a location that cannot be determined precisely (e.g., approximate area)?
- What happens when the user rapidly taps the submit button multiple times (double-tap)?

## Requirements *(mandatory)*

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right functional requirements.
-->

### Functional Requirements

- **FR-001**: System MUST provide a "Report Found Pet" wizard consisting of exactly 3 steps (Screen 1, Screen 2, Screen 3).
- **FR-002**: Screen 1 MUST allow the user to upload a photo of the found pet, and a photo MUST be mandatory to continue.
  - Photo MUST be in JPEG, PNG, or GIF format.
  - Photo size MUST NOT exceed 20MB.
  - System MUST display clear validation errors for unsupported formats or oversized files before upload.
- **FR-003**: Screen 2 MUST collect the following information:
  - **date of finding** (mandatory)
  - **collar data** (optional, if the animal has a collar)
  - **species** (mandatory)
  - **breed** (optional, if the user can specify)
  - **gender** (mandatory)
  - **found location** (mandatory)
    - MUST provide a "Use Current Location" button that captures device GPS coordinates when tapped
    - MUST provide text input field(s) for manual address/location entry
    - User MAY use current location button, manual text entry, or both
    - System MUST require at least location data (GPS coordinates OR text description) before proceeding
  - **additional description** (optional)
- **FR-004**: Screen 3 MUST allow the user to provide contact details for the person who found the animal:
  - **contact phone number** (optional)
  - **contact email** (optional)
  - The system MUST require at least one contact method (phone or email) before submission.
  - Phone number validation (aligned with Report Lost Pet backend):
    - If non-empty: MUST match regex `/\d/` (must contain at least one digit)
    - Any format accepted (spaces, dashes, parentheses, plus signs allowed)
    - Error message: "Enter a valid phone number"
  - Email validation (aligned with Report Lost Pet backend):
    - If non-empty: MUST match regex `/^(?=.{1,254}$)[^\s@]+@[^\s@]+\.[^\s@]+$/` (RFC 5322 basic format)
    - Max length: 254 characters
    - Error message: "Enter a valid email address"
  - At least one contact validation:
    - If both fields are empty: show error "Please provide at least one contact method (phone or email)"
    - If one or both fields are non-empty: both MUST pass their respective format validation
- **FR-005**: Screen 3 MUST allow the user to optionally provide:
  - **contact phone number of the person currently caring for the found animal**
  - **physical address where the animal is currently located**
- **FR-006**: The system MUST allow users to navigate forward and backward between wizard steps without losing entered data.
- **FR-007**: The system MUST prevent progress and submission when mandatory fields are missing and MUST provide clear, user-friendly validation feedback.
- **FR-008**: The system MUST allow the user to submit the report after completing required inputs and MUST show a clear confirmation on success.
- **FR-009**: The system MUST allow multiple submissions of found pet reports; duplicate reports are permitted (no deduplication logic required).
- **FR-010**: The design and interaction patterns of the found-pet wizard MUST be consistent with the wizard for reporting a missing animal (step-based flow, navigation patterns, and validation behavior).
- **FR-011**: All submitted found pet reports MUST be publicly viewable by all app users (no privacy restrictions or authentication requirements for viewing).

## Clarifications

### Session 2026-01-07

- Q: Should the system attempt to automatically match found pet reports with existing missing pet reports? → A: No, found and missing reports exist independently with no matching logic
- Q: What are the photo upload requirements and constraints? → A: JPEG, PNG, GIF • Max 20MB
- Q: What validation rules should apply to phone numbers and email addresses? → A: Same as Report Lost Pet - Phone: regex /\d/ (at least one digit); Email: regex /^(?=.{1,254}$)[^\s@]+@[^\s@]+\.[^\s@]+$/ (RFC 5322 basic)
- Q: How should users specify the "found location" where the animal was discovered? → A: Current location button + text inputs
- Q: Who should be able to view submitted found pet reports? → A: Public - all app users can view all found pet reports

### Assumptions

- **A-001**: Submitting the wizard creates a "found pet" report that is viewable within the product as a found-pet item.
- **A-002**: Contact details (phone/email) provided in Screen 3 are displayed on the found-pet report detail view to enable owners to reach the contact person.
- **A-003**: Found pet reports exist independently; the system does NOT attempt to match or link them to missing pet reports (no automated or suggested matching logic).
- **A-004**: All found pet reports are public and visible to all app users without authentication or privacy restrictions.

### Key Entities *(include if feature involves data)*

- **Found Pet Report**: A record representing a found animal report; includes pet info, found date, found location, description, and contacts.
- **Pet Photo**: The uploaded image associated with the found pet report.
- **Found Location**: The place where the animal was found (as selected/entered by the user).
- **Contact Details**: Phone/email for the finder and optional caretaker contact plus optional current address.

## Success Criteria *(mandatory)*

<!--
  ACTION REQUIRED: Define measurable success criteria.
  These must be technology-agnostic and measurable.
-->

### Measurable Outcomes

- **SC-001**: At least 90% of users who start the wizard successfully submit a found-pet report.
- **SC-002**: Median time to complete and submit a report is under 3 minutes for users providing the minimum required information.
- **SC-003**: At least 95% of submissions succeed on the first attempt (excluding user validation errors).
- **SC-004**: Less than 5% of submitted reports result in user-reported issues related to missing/incorrect mandatory validations within the first release cycle.

## Design Deliverables *(mandatory for UI features)*

<!--
  DESIGN WORKFLOW: Auto-generated after spec.md creation.
  See `.specify/memory/estimation-methodology.md` for full workflow.
  
  All design assets are:
  1. Generated automatically where possible
  2. Linked to Jira ticket
  3. Stored in specs/[feature]/design/ folder
-->

### Design Assets

| Asset | Status | Link |
|-------|--------|------|
| **User Flow** | _Pending_ | FigJam diagram showing navigation and logic |
| **Wireframe** | _Pending_ | FigJam layout showing screen structure |
| **Design Brief** | _Pending_ | Component specs, interactions, platform notes |
| **Figma Make Prompt** | _Pending_ | AI prompt for generating visual designs |
| **Visual Mockups** | _Pending_ | High-fidelity screens (created in Figma Make or by designer) |

### Design Requirements

- [ ] User flow diagram created
- [ ] Wireframe layout created
- [ ] Design brief with component specs
- [ ] Figma Make prompt ready
- [ ] All assets linked in Jira ticket
- [ ] Visual mockups approved (if applicable)

---


## Estimation *(mandatory)*

<!--
  ESTIMATION METHODOLOGY: See `.specify/memory/estimation-methodology.md` for full details.
  
  Story Point Definition: 1 SP = Effort to implement across ALL platforms with 80% test coverage.
  Anchor: Pet Details Screen = 3 SP (medium complexity)
  Budget Formula: SP × 4 days × 1.3 (risk buffer)
-->

### Initial Estimate

- **Story Points**: 3
- **Initial Budget**: 3 × 4 × 1.3 = 15.6 days
- **Confidence**: ±50%
- **Anchor Comparison**: Similar complexity to Pet Details (3 SP) because it requires a multi-step user flow with validation across platforms, but likely has some UX/pattern reuse from “Report Missing Pet”.

### Re-Estimation (Updated After Each Phase)

| Phase | SP | Days | Confidence | Key Discovery |
|-------|-----|------|------------|---------------|
| Initial | [from above] | [from above] | ±50% | Gut feel from feature title |
| After SPEC | — | — | ±30% | [Update when spec.md complete] |
| After PLAN | — | — | ±20% | [Update when plan.md complete] |
| After TASKS | — | — | ±15% | [Update when tasks.md complete] |

### Per-Platform Breakdown (After TASKS)

| Platform | Tasks | Days | Notes |
|----------|-------|------|-------|
| Backend | — | — | [Fill after tasks.md] |
| iOS | — | — | [Fill after tasks.md] |
| Android | — | — | [Fill after tasks.md] |
| Web | — | — | [Fill after tasks.md] |
| **Total** | | **—** | |

### Variance Tracking

| Metric | Initial | Final | Variance |
|--------|---------|-------|----------|
| **Story Points** | [X SP] | [Y SP] | [Calculate: (Y - X) / X × 100%] |
| **Budget (days)** | [X days] | [Y days] | [Calculate: (Y - X) / X × 100%] |

**Variance Reasons**: [Why was estimate different? Reuse? Native APIs? Backend already done?]

**Learning for Future Estimates**: [What pattern should the team apply to similar features?]