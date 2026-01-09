# Feature Specification: iOS Align Found Flow (3-step)

**Feature Branch**: `KAN-34-ios-align-found-flow`  
**Created**: 2026-01-09  
**Status**: Draft  
**Platform**: iOS  
**Jira Ticket**: KAN-34  
**Design**: [Figma - Upload photo (Found)](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=1224-5921&m=dev), [Figma - Pet details (Found)](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=1224-5989&m=dev), [Figma - Contact information (Found)](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=1224-6415&m=dev)  
**Input**: User description: "utwórz specyfikację tylko dla iOS o nazwie KAN-34-ios-align-found-flow. Nie numeruj specki jak masz w skrypcie, nazwa dokładnie taka jak napisałem. Branch o takiej samej nazwie. Flow ma mieć tylko 3 kroki: - upload photo - pet details - contact information. W pet details dodany jest textfield do podania numeru microchipa (Collar data). trzymaj się stylu który już mamy, po prostu dodaj pole z walidacją jak jest to na ekranie microchipu. W contact information jest dodane pole Caregiver phone number i current physical address. To pierwsze jest numerem telefonu (jak your phone number), ale opcjonalne. To drugie jest multiline jak animal additional description (też opcjonalne) - trzeba będzie dodać pola do stanu. Popraw za to teksty, żeby odpowiadały (wygeneruj nowe)."

## Clarifications

### Session 2026-01-09

- Q: When the user enters a non-empty “Collar data (optional)” value, should fewer than 15 digits block moving past “Pet details”? → A: No — use the same behavior as the existing iOS “Missing pet → Microchip number” screen (digits-only, formatted, max 15, no exact-length validation).
- Q: What phone validation rule should be used for “Your phone number” (and for “Caregiver phone number” when non-empty)? → A: Use the existing iOS report flow rule: 7–11 digits (allow `+`, ignore other chars), validated on submit.
- Q: Should “Caregiver phone number” and “Current physical address” be included in the backend payload? → A: No — iOS-only fields (kept in flow state/UI); do not send to backend because the current API schema does not support them.
- Q: Should Found location (latitude & longitude) be required on “Pet details”? → A: Yes — required; block “Continue” until both are provided (consistent with existing iOS report flows and backend requirements).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Complete the 3-step “Report Found Animal” flow (Priority: P1)

As a person who found an animal, I want to go through a short, 3-step wizard (photo → pet details → contact information) so I can submit a complete found-animal report without unnecessary steps.

**Why this priority**: This is the primary user journey for reporting a found animal; reducing friction increases completion rate.

**Independent Test**: Start the “Report Found Animal” flow, complete each step with valid inputs, and verify submission succeeds and the app exits the flow.

**Acceptance Scenarios**:

1. **Given** the user starts the “Report Found Animal” flow, **When** they progress through the wizard, **Then** they see exactly three steps in this order: Upload photo (1/3), Pet details (2/3), Contact information (3/3).
2. **Given** the user has entered data on a step, **When** they navigate forward and backward within the wizard, **Then** previously entered values remain populated until the user cancels or submits.

---

### User Story 2 - Provide a microchip number as part of “Collar data” (Priority: P2)

As a person who found an animal, I want to optionally enter a microchip number (as “Collar data”) so the pet can be identified more reliably.

**Why this priority**: A microchip number can dramatically increase the chance of reunification; making it optional keeps the flow inclusive.

**Independent Test**: On Pet details, enter a microchip number and verify it is formatted as you type and preserved when navigating between steps.

**Acceptance Scenarios**:

1. **Given** the user is on Pet details, **When** they type digits into “Collar data (optional)”, **Then** the UI formats the value as `00000-00000-00000` (hyphens inserted after the 5th and 10th digit) while storing the underlying value as digits-only.
2. **Given** the “Collar data” input is empty, **When** the user proceeds to the next step, **Then** no validation error is shown for that field (it is optional).
3. **Given** the user enters 1–14 digits into “Collar data (optional)”, **When** they proceed to the next step and later return to Pet details, **Then** the value remains preserved (digits-only in state, formatted in UI) and navigation is not blocked.

---

### User Story 3 - Optionally provide caregiver contact and current address (Priority: P3)

As a person who found an animal, I want to optionally provide a caregiver phone number and the animal’s current physical address so the owner can coordinate pickup even if I’m not the caregiver.

**Why this priority**: In real scenarios, the finder and caregiver can be different people; optional fields support both cases without adding mandatory friction.

**Independent Test**: On Contact information, leave optional fields empty (still submit), then fill them (submit), verifying both cases succeed.

**Acceptance Scenarios**:

1. **Given** the user is on Contact information, **When** they leave “Caregiver phone number (optional)” and “Current physical address (optional)” empty and submit, **Then** the report submission succeeds.
2. **Given** the user enters a caregiver phone number, **When** they submit, **Then** the caregiver phone number is submitted and shown correctly wherever contact details are displayed.
3. **Given** the user enters caregiver phone number and/or current physical address, **When** they submit the report, **Then** submission succeeds and these values remain available in the in-progress state/UI, but are not included in the backend request payload.

---

### Edge Cases

- The user tries to proceed without a photo: the flow blocks progress and shows a clear message.
- The user tries to proceed without a found location: the flow blocks progress and shows clear inline validation for missing latitude/longitude.
- The user enters non-numeric characters into “Collar data”: the UI rejects non-digit characters.
- The user enters more than 15 digits into “Collar data”: extra digits are not accepted.
- The user enters an invalid caregiver phone number: show a validation message and block submission (only if the field is non-empty; 7–11 digits after sanitization).
- The user enters a very long “Current physical address”: input is capped at a reasonable maximum and remains usable (no UI clipping).
- The user loses connectivity during submission: show a user-friendly error and allow retry without losing entered data.

## Requirements *(mandatory)*

### Functional Requirements

#### Flow structure & navigation

- **FR-001**: The iOS app MUST implement a “Report Found Animal” wizard consisting of exactly 3 steps in this order: Upload photo → Pet details → Contact information.
- **FR-002**: The wizard MUST display a progress indicator reflecting the current step out of 3 (1/3, 2/3, 3/3) on all three steps.
- **FR-003**: The wizard MUST allow backward navigation to the previous step and MUST preserve entered data while the wizard is active.
- **FR-004**: Exiting/cancelling the wizard MUST clear the in-progress found-report data (a new wizard start is a fresh session).

#### Step 1: Upload photo

- **FR-005**: Upload photo MUST require the user to attach a photo before proceeding to Pet details.
- **FR-006**: Upload photo MUST show helper copy and placeholders per “Copy requirements” in this spec.

#### Step 2: Pet details

- **FR-007**: Pet details MUST collect at least the following:
  - Date found (required)
  - Animal species (required)
  - Sex (required)
  - Found location (required) as latitude and longitude:
    - Users MUST be able to request the device’s current location to populate latitude and longitude.
    - Users MUST be able to enter latitude and longitude manually.
    - Users MUST provide both latitude and longitude (either populated via current location or entered manually) before proceeding.
- **FR-008**: Pet details MUST include “Collar data (optional)” which captures an optional microchip number and behaves as follows:
  - Accepts digits only (0-9).
  - Stores digits-only (no hyphens).
  - Formats display as `00000-00000-00000` while typing (hyphens after 5th and 10th digit).
  - Limits input to 15 digits.
  - Does not require exactly 15 digits (no length validation beyond the 15-digit cap).
  - Does not block navigation if empty.
- **FR-009**: Pet details MUST preserve all entered values when navigating away and back within the wizard.

#### Step 3: Contact information

- **FR-010**: Contact information MUST collect the finder’s contact details:
  - Your phone number (required)
  - Your email (required)
- **FR-010a**: Your phone number and your email MUST be validated on submit and MUST block submission when invalid, using the same validation rules as existing iOS report flows:
  - Phone number: must be non-empty and contain 7–11 digits after sanitization (allow `+`, ignore other characters).
  - Email: must be non-empty and match a basic `local@domain.tld` pattern.
- **FR-011**: Contact information MUST include the following optional fields:
  - Caregiver phone number (optional phone input)
  - Current physical address (optional multiline text area)
- **FR-012**: Caregiver phone number MUST validate using the same rules as “Your phone number”, but ONLY when the field is non-empty.
- **FR-013**: Current physical address MUST allow multiple lines and MUST NOT block submission when empty.
- **FR-014**: Contact information MUST preserve all entered values when navigating away and back within the wizard.

#### State & data handling (iOS scope)

- **FR-015**: The iOS app MUST store the following values in the found-report in-progress state while the wizard is active:
  - Photo attachment
  - Date found
  - Collar data (digits-only microchip number, optional)
  - Species
  - Breed (optional, if present in current UI)
  - Sex
  - Found location (GPS coordinates and/or user-entered location text, per existing patterns)
  - Additional description (optional, if present in current UI)
  - Finder phone number
  - Finder email
  - Caregiver phone number (optional)
  - Current physical address (optional, multiline)
- **FR-016**: On successful submission, the iOS app MUST NOT include the new optional fields (caregiver phone number, current physical address) in the backend payload (iOS-only fields); they are captured for UI purposes and kept in the in-progress state.

#### Copy requirements (updated texts)

- **FR-017**: The iOS app MUST use the following English copy for the found-report wizard screens (text-only changes; layout and styling follow existing iOS patterns):

  - **Step 1 (1/3) - Upload photo**
    - Screen title: "Upload photo"
    - Heading: "Upload photo"
    - Body: "Upload a clear photo of the pet you found. This helps the owner recognize them quickly."
    - Upload empty state:
      - Primary: "Tap to upload a photo"
      - Secondary: "PNG or JPG up to 10 MB"
    - Primary button: "Continue"

  - **Step 2 (2/3) - Pet details**
    - Screen title: "Pet details"
    - Heading: "Pet details"
    - Body: "Tell us what you know about the animal you found."
    - Labels / placeholders:
      - Date found: label "Date found", placeholder "DD/MM/YYYY"
      - Collar data (optional): label "Collar data (optional)", placeholder "00000-00000-00000"
      - Additional description (optional): label "Additional description (optional)", placeholder "Any details that could help identify the pet..."
    - GPS button: "Use current location"
    - Primary button: "Continue"

  - **Step 3 (3/3) - Contact information**
    - Screen title: "Contact info"
    - Heading: "Contact information"
    - Body: "Share your contact details so the pet’s owner can reach you."
    - Labels / placeholders:
      - Your phone number: label "Your phone number", placeholder "+1 (555) 000-0000"
      - Your email: label "Your email", placeholder "email@example.com"
      - Caregiver phone number: label "Caregiver phone number (optional)", placeholder "+1 (555) 000-0000"
      - Current physical address: label "Current physical address (optional)", placeholder "Full address where the animal is currently located"
    - Primary button: "Submit report"

### Key Entities *(include if feature involves data)*

- **Found report (in-progress)**: A temporary, in-memory (or session-scoped) data object that stores the user’s inputs across the 3-step wizard until submission or cancellation.
- **Microchip number**: An optional 15-digit identifier captured as digits-only and formatted for display as `00000-00000-00000`.
- **Caregiver contact**: Optional phone number for a caregiver and optional current physical address (multiline) for where the animal is currently located.

### Assumptions

- The iOS “Report Found Animal” entry point and base flow wiring already exist (scaffolded) and are in scope only to the extent needed to make the 3-step flow behave as specified here.
- Validation patterns and UI components should remain consistent with existing iOS report flows (missing/found).

### Dependencies

- Existing iOS validation behavior for phone and email in report flows (used as the baseline for caregiver phone validation).
- Existing iOS handling for photo attachment requirement and found/missing report submission retry UX.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can complete the found-report wizard in under 3 minutes on average (measured in QA sessions with a stopwatch).
- **SC-002**: In QA, 100% of attempts to proceed without a photo are blocked with a clear, user-friendly message.
- **SC-003**: In QA, 100% of entered “Collar data” values are formatted correctly as `00000-00000-00000` and preserved when navigating between steps.
- **SC-004**: Optional fields (caregiver phone number, current physical address) do not block submission when empty, and are included in the report when provided.

## Design Deliverables *(mandatory for UI features)*

### Design Assets

| Asset | Status | Link |
|-------|--------|------|
| **Wireframe** | Done | [Figma - iOS Found flow (3 screens)](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=1224-5921&m=dev) |
| **Visual Mockups** | Done | [Figma - Upload photo](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=1224-5921&m=dev), [Figma - Pet details](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=1224-5989&m=dev), [Figma - Contact information](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=1224-6415&m=dev) |

### Design Requirements

- [ ] Copy matches this spec exactly
- [ ] Optional fields are visually indicated as optional using existing iOS patterns
- [ ] New fields match existing components and spacing

---

## Estimation *(mandatory)*

### Initial Estimate

- **Story Points**: 1
- **Initial Budget**: 1 × 4 × 1.3 = 5.2 days
- **Confidence**: ±50%
- **Anchor Comparison**: Simpler than Pet Details (3 SP) because this is iOS-only alignment work: adjust step count, add two optional fields, and reuse existing validation behavior for microchip and phone.

### Re-Estimation (Updated After Each Phase)

| Phase | SP | Days | Confidence | Key Discovery |
|-------|-----|------|------------|---------------|
| Initial | 1 | 5.2 | ±50% | Gut feel from feature title - iOS-only flow alignment |
| After SPEC | 1 | 5.2 | ±30% | Scope confirmed: iOS-only, exactly 3 steps, new optional fields + microchip formatting and validation rules clarified |
| After PLAN | 1 | 5.2 | ±20% | Research complete: reuse existing components, 7 files to delete (incl. Summary), no backend changes, ~10 files to modify/create |
| After TASKS | 1 | 5.5 | ±10% | Task breakdown complete: 42 tasks total (6 foundational, 9 US1 tests/impl, 6 US2, 7 US3, 14 polish/cleanup) - sequential strategy fits 5.2-day budget with 0.3-day buffer |

### Per-Platform Breakdown (After TASKS)

| Platform | Tasks | Days | Notes |
|----------|-------|------|-------|
| Backend | 0 | 0 | No backend changes in this spec |
| iOS | 42 | 5.5 | Setup (4), Foundational (2), US1 (9), US2 (6), US3 (7), Polish (14) - includes unit tests, implementation, deletion of 7 obsolete files |
| Android | 0 | 0 | No Android changes |
| Web | 0 | 0 | No web changes |
| **Total** | **42** | **5.5** | iOS-only feature |

### Variance Tracking

| Metric | Initial | Final | Variance |
|--------|---------|-------|----------|
| **Story Points** | 1 SP | [Y SP] | [Calculate: (Y - 1) / 1 × 100%] |
| **Budget (days)** | 5.2 days | [Y days] | [Calculate: (Y - 5.2) / 5.2 × 100%] |

**Variance Reasons**: [Update after delivery]  
**Learning for Future Estimates**: [Update after delivery]


