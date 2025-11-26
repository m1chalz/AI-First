# Feature Specification: Owner's Details Screen

**Feature Branch**: `023-owners-details-screen`  
**Created**: 2025-11-26  
**Status**: Draft  
**Input**: `/speckit.specify create specification 023 for Owners details screen. Use figma https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8113&m=dev. Flow is described on specification branch 017.`

This feature delivers Step 4/4 (“Owner's details”) of the Missing Pet flow defined in specification 017 for iOS. The UI, layout, and copy must match Figma node `297-8113`, including the circular back button, the centered title, the 4/4 progress indicator, three vertically stacked inputs (phone, email, optional reward description), helper text, and the primary blue “Continue” CTA. Completing this step unlocks the summary review screen defined in specification 017, which intentionally hides the progress chip.

## Clarifications

### Session 2025-11-26

- Q: Should Step 4 keep the reward input as a single free-text field or introduce structured amount/currency fields? → A: Keep a single free-text reward description; do not add structured amount or currency controls in this release.
- Q: What should happen if the user taps Continue while offline? → A: Remain on Step 4, show inline “No connection. Try again” messaging, keep all input intact, and re-enable Continue once connectivity returns for a manual retry.
- Q: Should the backend submission and confirmation email trigger on Step 4 or Step 5? → A: Step 4 must submit the full payload to the backend and trigger the confirmation email before showing the summary.
- Q: Which Missing Pet flow screen does this specification cover? → A: Step 4/4 Owner's Details (contact information) screen.
- Q: Are both contact fields mandatory? → A: Yes. The user must provide a valid phone number and a valid email address before Continue unlocks.
- Q: What happens after Continue succeeds? → A: Step 4 now finalizes the report: the app persists the entire Missing Pet payload (Steps 1–4 data) to the backend database and triggers the outbound notification email.
- Q: How should the optional “Reward for the finder” field behave? → A: Treat it as a free-text description that can include numbers and words (e.g., “$250 gift card” or “Warm thanks”). Preserve the entered text exactly, but limit it to 120 characters and allow empty state without validation errors.
- Q: Does the progress indicator stay visible after leaving Owner's details? → A: No. Step 4 shows “4/4”; once Continue succeeds and the summary screen loads, the progress indicator is hidden in accordance with specification 017.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Provide reliable contact channel before summary (Priority: P1)

Reporters who reach Step 4 must enter both contact methods (phone and email) so potential finders or shelter staff can respond through their preferred channel. Once both inputs are valid, Continue becomes available and, on tap, persists the entire Missing Pet payload (chip number, photo, description, contact details, reward) to the backend while kicking off an email notification.

**Why this priority**: Having two reliable contact paths drastically improves the chance of reconnecting with the owner and keeps shelter workflows consistent.

**Independent Test**: Deep link directly into Step 4 with prior steps pre-populated, fill both phone and email fields with valid values, observe Continue enablement, proceed to summary, and confirm the collected contact methods appear in session data.

**Acceptance Scenarios**:

1. **Given** the user is on Owner's details with empty inputs, **When** they enter a valid phone number and a valid email address, **Then** helper text confirms both requirements are satisfied, Continue enables, and tapping Continue both persists the full report to the backend (including earlier steps) and navigates to the summary screen with the 4/4 badge disappearing.  
2. **Given** the user entered both phone and email, **When** they return from the summary screen using the back button, **Then** both values persist exactly as entered (including formatting) and the progress chip returns to 4/4.

---

### User Story 2 - Enforce inline validation and helpful feedback (Priority: P2)

Owners may mistype contact details; the screen must catch invalid formats immediately, show inline errors, and keep the CTA disabled until both methods are valid simultaneously.

**Why this priority**: Preventing malformed contact info reduces failed outreach and avoids support tickets once backend validation arrives.

**Independent Test**: Attempt to submit with an invalid email, observe inline error and disabled Continue, correct the email, and verify the CTA re-enables without touching other fields.

**Acceptance Scenarios**:

1. **Given** the user enters letters into the phone field, **When** focus leaves the field, **Then** the input trims whitespace, rejects non-numeric characters (except leading “+”), and shows inline copy such as “Enter at least 7 digits” until corrected, keeping Continue disabled.  
2. **Given** the user provides `owner@` in the email field, **When** they attempt to continue, **Then** Continue stays disabled and an inline message explains the email format requirement until the address is valid (e.g., `owner@example.com`).

---

### User Story 3 - Capture optional reward details with safe persistence (Priority: P3)

Some owners want to offer a reward or leave textual instructions. They need to enter a short description (numbers, symbols, or plain text), optionally leave it empty, and trust that the value persists when navigating backward or after temporarily backgrounding the app.

**Why this priority**: Rewards can motivate community action; letting owners describe them in natural language avoids confusion (e.g., “$250 gift card + hugs”).

**Independent Test**: Enter “$250 gift card + hugs” in the reward description, navigate back to Step 3, return to Step 4, and confirm the text is preserved and still editable; clear it and ensure the summary reflects “No reward offered.”

**Acceptance Scenarios**:

1. **Given** the user enters “$250 gift card + hugs” in the reward description, **When** they leave and re-enter Step 4, **Then** the text persists exactly and the summary mirrors it verbatim.  
2. **Given** the reward description exceeds 120 characters, **When** focus leaves the field, **Then** input beyond the limit is rejected, an inline helper states “Keep reward details under 120 characters,” and Continue remains enabled as long as phone and email are valid.

---

### Edge Cases

- Any phone or email invalid (including empty): Continue stays disabled and a helper text below the group explains “Provide a valid phone number and email address.”  
- User pastes a formatted phone number (spaces, dashes, parentheses): the UI sanitizes input for validation but preserves the user-entered formatting in the text field.  
- Reward text exceeds 120 characters: display an inline warning and prevent additional input.  
- Device loses network connectivity before tapping Continue: allow the user to keep editing; when Continue is tapped offline, stay on Step 4, show inline “No connection. Try again” copy, and re-enable the CTA once the device reconnects without clearing inputs.  
- User backgrounds the app or rotates the device: all inputs persist via the flow session without resetting validation state.  
- Accessibility: each control exposes `ownersDetails.*` test identifiers and accessibility labels matching the visible copy.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Owner's details MUST render immediately after Step 3 (spec 021) with the header layout, helper text, and input order exactly as in Figma node `297-8113`, including the back circle, centered title “Owner's details,” and progress badge “4/4.”  
- **FR-002**: The circular back button and iOS back gesture MUST navigate to Step 3, restore its data, and update the progress indicator back to “3/4” without losing any Step 4 entries.  
- **FR-003**: Continue MUST remain disabled until both contact methods (phone and email) pass validation; tapping Continue when enabled MUST persist all Step 4 values to the Missing Pet flow session and present the summary screen (Step 5) while emitting analytics event `missing_pet.step4_completed`.  
- **FR-004**: The phone number input MUST accept digits and a single leading “+”, trim other characters, enforce 7–11 digits, show inline errors for invalid entries, and store the sanitized digits-only value while leaving the user-visible formatting untouched.  
- **FR-005**: The email input MUST validate against RFC 5322–compatible patterns (basic local@domain.tld check), be case-insensitive, and surface inline errors plus helper copy when invalid; empty email is never allowed once Continue is tapped.  
- **FR-006**: The reward description field MUST accept up to 120 UTF-8 characters (letters, numbers, symbols, or currency notation), show the `(optional)` label, allow clearing to blank without error, display the text verbatim on the summary screen, and remain the only reward input (no structured amount/currency fields).  
- **FR-007**: All three inputs MUST synchronize with the shared `ReportMissingPetFlowState`, survive navigation between steps, device rotation, app backgrounding, and remain editable until the flow is submitted or canceled via spec 017 exit paths.  
- **FR-008**: Inline helper text beneath the subtitle MUST clarify “Add your contact information and potential reward,” and an additional helper below the input stack MUST display when validation blocks Continue (e.g., “Provide a valid phone number and email address”).
- **FR-009**: Error states MUST use the product palette (red text + border) while maintaining WCAG AA contrast; clearing the error should immediately restore the neutral border.  
- **FR-010**: The Continue button MUST use the design-system blue (#155DFC), stretch full width (327 px in design, responsive in code), and expose `ownersDetails.continue.tap` as the automation identifier.  
- **FR-011**: Returning from the summary screen to Owner's details MUST repopulate all fields exactly as saved and re-disable Continue only if the underlying session data became invalid (e.g., cleared externally).  
- **FR-012**: When the user taps “Continue” with both contact methods valid, the app MUST advance to the summary screen without re-surfacing those inputs; the collected contact data remains available in session/backend storage for future presentation or submission steps.
- **FR-013**: Successful Continue action MUST persist the entire collected dataset (Steps 1–4) to the backend database via the Missing Pet API contract and trigger an outbound email confirmation to the owner using the provided email address before displaying the summary screen; failures (including offline) MUST keep the user on Step 4, show inline messaging like “No connection. Try again,” preserve all inputs, and allow a manual retry once connectivity returns.

### Key Entities *(include if feature involves data)*

- **OwnerContactDetails**: Session-bound structure containing `phone`, `email`, `rewardDescription`, and timestamps of the last edits for analytics. Ensures both contact channels are truthy before progression.  
- **ContactValidationState**: Derived flags indicating `isPhoneValid`, `isEmailValid`, `areContactsValid` (logical AND), plus optional `rewardValidationMessage` used to surface the 120-character limit without blocking Continue.  
- **MissingPetFlowSession (ReportMissingPetFlowState)**: Existing shared object from specification 017 extended to store OwnerContactDetails plus computed property `hasContactInfo`.
- **MissingPetBackendPayload**: Aggregate DTO composed of chip number, photo reference, animal description, location metadata, contact details, and reward description; sent to backend when Continue is confirmed to create/update the missing pet record and trigger notification workflows.

## Success Criteria *(mandatory)*

- **SC-001**: 100% of QA test runs confirm Continue stays disabled until both phone and email are valid; no invalid submissions reach the summary step.  
- **SC-002**: 95% of users who reach Step 4 during usability studies complete it in ≤60 seconds, indicating the form is lightweight.  
- **SC-003**: Analytics show <2% of completed Step 4 sessions emit validation error events after the first attempt, demonstrating clear inline guidance.  
- **SC-004**: At least 90% of sessions that leave Step 4 and return (via Back or app resume) retain identical values, measured via debug telemetry during QA.  
- **SC-005**: 100% of successful Continue actions create a row in the Missing Pet backend database and send one confirmation email, verified by integration tests and instrumentation.
- **SC-006**: 0% of offline Continue attempts navigate away from Step 4; users always receive inline “No connection. Try again” messaging until the submission succeeds online.

## Assumptions

- Copywriting will correct the subtitle grammar while keeping the intent from the Figma reference; localization is handled in a later pass.  
- Backend submission and email dispatch now occur when Continue succeeds; retries and detailed error handling will be defined in the implementation plan.  
- Reward currency or numbering is whatever the user types; the app does not auto-format or localize the optional reward text.  
- Phone validation follows the same heuristics already used in specification 006 (Pets API) to keep formats consistent.

## Dependencies

- Navigation scaffolding, session container, and summary screen contract from specification 017.  
- Analytics instrumentation pipeline for the `missing_pet.step4_completed` event.  
- Validation helpers or shared lib functions for phone/email formatting (reuse from backend/web specs when available).  
- Design tokens (colors, typography) defined in `.specify/memory/constitution.md`.

## Out of Scope

- Allowing multiple phone numbers or emails; the current design collects one of each.  
- Currency selection, tipping, or attaching reward terms.  
- Android/Web implementations (handled by their platform-specific specs).  
- Summary screen layout changes (spec 017 governs that step).


