# Feature Specification: Web Owner's Details Screen

**Feature Branch**: `032-web-owners-details`  
**Created**: 2025-12-02  
**Status**: Draft  
**Input**: Web implementation of Owner's Details screen (Step 4/4) from spec 035

**Parent Specification**: [035-ios-owners-details-screen](../035-ios-owners-details-screen) - iOS Owner's Details specification

## Clarifications

### Session 2025-12-02

- Q: When should validation occur for the contact fields (phone/email)? → A: Same pattern as previous screens (specs 034, 037, 039) - validation on blur with inline error feedback
- Q: Should flow state persist across browser refresh? → A: Clear state on refresh - consistent with specs 034, 037, 039 (user returns to pet list)
- Q: Should Continue button be disabled until valid contact exists, or always enabled with validation on click? → A: Always enabled, validate on click - consistent with specs 034, 037, 039 (show errors on submit)
- Q: Should in-app back arrow close entire flow or navigate to previous step? → A: Navigate to previous step - Step 4→Step 3, preserve flow state (evolved pattern from spec 039)
- Q: If user has one valid contact and one invalid field, allow navigation or require fixing all non-empty fields? → A: Block navigation - require all non-empty fields to be valid (user must fix or clear invalid fields)
- Q: Should accessibility include aria attributes (aria-disabled, aria-describedby, etc.)? → A: No aria support - use standard HTML labels and semantic elements only
- Q: What should summary screen display and is it in scope? → A: Summary screen displays collected flow state (debug view like current ContactScreen), included in this spec

## Scope & Background

This specification defines the web-specific implementation of the Owner's Details screen (Step 4/4) in the Missing Pet flow. It builds on the functional requirements from specification 035 but focuses exclusively on React/TypeScript implementation, web patterns, and integration with the existing ReportMissingPetFlow context.

**Key Scope Items**:
- React component implementation matching Figma node 315-15943 exactly
- Integration with existing ReportMissingPetFlowContext session management
- Web-native validation patterns and error handling (phone OR email required)
- Data collection and session persistence only
- Summary screen displaying collected flow state (debug view)
- Responsive design for desktop and mobile viewports

**Out of Scope**:
- Backend submission and API communication (handled in separate integration spec)
- Android/iOS implementations (handled by other platform specs)
- Localization beyond English (future iteration)
- Reward field validation (free-form input, no constraints)

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Enter contact information to complete data collection (Priority: P1)

Web users who complete Steps 1-3 (chip, photo, description) reach the Owner's Details screen and must provide at least one valid contact method (phone OR email) before proceeding. Clicking Continue validates the form, and if at least one valid contact method exists, saves the contact data to the session and navigates to the summary screen.

**Why this priority**: Contact information is mandatory for the report to be actionable. Without it, shelters/finders cannot reach the owner, making the report useless. Requiring only one contact method reduces friction while maintaining reachability.

**Independent Test**: Navigate to Step 4 with prior steps pre-populated, fill only phone number with valid value, click Continue, verify session saves data and navigates to summary.

**Acceptance Scenarios**:

1. **Given** the user is on Owner's details with empty inputs, **When** they enter "+48123456789" in phone (leaving email empty) and click Continue, **Then** form validates successfully, saves contact data to session, and navigates to summary.
2. **Given** the user is on Owner's details with empty inputs, **When** they enter "owner@example.com" in email (leaving phone empty) and click Continue, **Then** form validates successfully, saves contact data to session, and navigates to summary.
3. **Given** the user enters valid phone and email and clicks Continue, **When** they navigate back from summary using the back button, **Then** both phone and email values persist exactly as entered and the progress badge returns to "4/4".

---

### User Story 2 - Receive inline validation feedback for invalid inputs (Priority: P2)

Users who mistype contact details see inline validation errors (red border + error text) on blur. If they click Continue without at least one valid contact method, validation blocks navigation and shows errors. This prevents submission of malformed contact info.

**Why this priority**: Invalid contact data breaks the reconnection flow. Inline validation catches errors early and guides users to correct them.

**Independent Test**: Enter invalid email (e.g., "owner@"), blur field, observe inline error, click Continue, verify navigation is blocked with error feedback, correct email, click Continue again, verify navigation succeeds.

**Acceptance Scenarios**:

1. **Given** the user enters "123" in the phone field (leaving email empty), **When** focus leaves the field, **Then** a red border appears with inline error text "Enter at least 7 digits". **When** they click Continue, **Then** navigation is blocked and validation errors persist until phone is corrected to 7-11 digits OR a valid email is entered.
2. **Given** the user enters "owner@" in email (leaving phone empty), **When** focus leaves the field, **Then** a red border + error text appears below email field explaining format requirement (e.g., "Enter a valid email address"). **When** they click Continue, **Then** navigation is blocked until email is valid OR a valid phone is entered.
3. **Given** the user has a valid phone but invalid email, **When** they click Continue, **Then** navigation is blocked, inline error persists on email field, and user must either fix the email or clear it entirely before proceeding.

---

### User Story 3 - Optionally add reward description (Priority: P3)

Some users want to offer a reward. They can enter free-text reward details (e.g., "$250 gift card + hugs") of any length. The field is optional, has no validation, and does not block Continue if left empty.

**Why this priority**: Rewards motivate community action. Free-text input with no constraints provides maximum flexibility for users to describe their reward offer.

**Independent Test**: Enter "$250 gift card + hugs" in reward field, navigate back to Step 3, return to Step 4, confirm text persists and is editable.

**Acceptance Scenarios**:

1. **Given** the user enters "$250 gift card + hugs" in reward, **When** they navigate away and back, **Then** the text persists exactly and is editable.
2. **Given** the user enters any text in reward field, **When** they click Continue, **Then** no validation occurs on the reward field and navigation depends only on phone/email validation (at least one valid contact method required).

---


---

### Edge Cases

- **Phone validation**: Accept leading "+", reject letters/symbols (except digits), enforce 7-11 digits, sanitize whitespace/dashes but preserve user-entered formatting in UI. Field is optional but must be valid if provided.
- **Email validation**: RFC 5322-compatible (basic local@domain.tld), case-insensitive, trim whitespace. Field is optional but must be valid if provided.
- **At least one contact required**: User must provide either valid phone OR valid email (or both) to proceed. All non-empty fields must be valid - partial invalid data (e.g., valid phone + malformed email) blocks navigation until user fixes or clears invalid fields.
- **Reward field**: No validation, no character limits, accepts any text input. Completely optional.
- **Keyboard handling**: Inputs remain accessible on mobile viewports, Continue button remains visible.
- **Navigation persistence**: All inputs survive backward/forward navigation within active flow session. Browser refresh clears all flow state and returns user to pet list (consistent with specs 034, 037, 039).
- **Accessibility**: Screen readers announce field labels, validation errors, and button states; all inputs expose `data-testid="contact.*"` attributes.
- **Browser support**: Modern browsers (Chrome 90+, Firefox 88+, Safari 14+, Edge 90+).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The Owner's Details screen MUST render as Step 4/4 in the Missing Pet flow and visually match Figma node 315-15943 including circular back button, centered "Owner's details" title, progress badge "4/4", three inputs (phone, email, reward), helper text, and blue Continue button.
- **FR-002**: The circular back button (in-app navigation) MUST navigate to Step 3 (Animal Description screen), restore its data, update progress to "3/4", and preserve all Step 4 entries in flow state (consistent with spec 039 evolved pattern). Browser back button separately cancels entire flow.
- **FR-003**: Continue MUST remain enabled at all times (consistent with specs 034, 037, 039); clicking Continue MUST validate that (a) at least one contact method (phone OR email) is valid AND (b) all non-empty fields are valid. If validation passes, MUST save contact data to ReportMissingPetFlowState and navigate to summary screen. If validation fails, MUST show inline errors and block navigation until user fixes or clears invalid fields.
- **FR-004**: Phone input MUST be optional but when provided MUST accept digits and leading "+", trim spaces/dashes for validation but preserve user-entered formatting in UI, enforce 7-11 digits, and show inline error "Enter at least 7 digits" with red border on blur when invalid (non-empty but incorrect format).
- **FR-005**: Email input MUST be optional but when provided MUST validate against RFC 5322-compatible pattern (basic local@domain.tld), be case-insensitive, trim whitespace, and show inline error "Enter a valid email address" with red border on blur when invalid (non-empty but incorrect format).
- **FR-006**: Reward description field MUST accept any text input without validation or character limits, display "(optional)" in label, allow clearing to blank without error, and store text verbatim in session.
- **FR-007**: All inputs MUST synchronize with ReportMissingPetFlowState (existing session container), survive navigation between steps, and remain editable until flow is completed or canceled. Browser refresh clears all flow state (consistent with specs 034, 037, 039).
- **FR-008**: Inline helper text "Add your contact information and potential reward." MUST appear below screen title.
- **FR-009**: Error states MUST use design system red (#FB2C36 or similar) for text and borders, maintain WCAG AA contrast, and immediately restore neutral borders when errors are cleared.
- **FR-010**: Continue button MUST use primary blue (#155DFC), stretch full width on mobile (responsive), expose `data-testid="contact.continue.button"` attribute, and remain enabled at all times (consistent with specs 034, 037, 039).
- **FR-011**: Returning from summary to Owner's Details MUST repopulate all fields exactly as saved with Continue remaining enabled.
- **FR-012**: The screen MUST be responsive and work on mobile (320px+), tablet (768px+), and desktop (1024px+) viewports with appropriate layout adjustments.
- **FR-013**: Summary screen MUST display all collected flow state data (microchip number, photo, last seen date, species, breed, sex, age, description, location, phone, email, reward) in a debug-style view similar to current ContactScreen implementation, with options to navigate back to Step 4 or complete/cancel the flow.

### Key Entities *(include if feature involves data)*

- **ReportMissingPetFlowState**: Existing session container extended to store contact details (phone, email, rewardDescription) and computed property hasContactInfo.
- **ContactDetails**: Session-bound structure containing phone (optional), email (optional), rewardDescription (optional) strings.

## Success Criteria *(mandatory)*

- **SC-001**: 100% of QA test runs confirm Continue validates that at least one valid contact method (phone OR email) is provided before allowing navigation; no invalid data reaches summary step.
- **SC-002**: 95% of web draft sessions that include contact info display the same values after navigating away and back, measured via debug telemetry during QA.
- **SC-003**: At least 90% of users who reach Step 4 during usability studies complete it in ≤60 seconds, indicating form is lightweight.
- **SC-004**: Analytics show <2% of completed Step 4 sessions emit validation error events after first attempt, demonstrating clear inline guidance.
- **SC-005**: 100% of users can proceed with only phone OR only email (not requiring both), verified by QA test scenarios.

## Assumptions

- Copywriting will correct grammatical error ("your contact your contact information's") from Figma reference while keeping intent.
- Backend submission is handled in a separate integration spec; this screen only collects and persists data to session.
- Reward field has no validation, character limits, or formatting constraints.
- Phone validation follows same heuristics as spec 006 (Pets API) for consistency.
- At least one contact method (phone OR email) is sufficient for the report to be actionable.
- Browser support baseline is modern browsers (Chrome 90+, Firefox 88+, Safari 14+, Edge 90+).

## Dependencies

- **Existing Web Flow**: ReportMissingPetFlowContext, ReportMissingPetLayout, existing routing structure
- **Spec 035**: iOS Owner's Details screen - functional requirements reference
- **Design System**: Colors (#155DFC blue, #FB2C36 red, #364153 labels), typography (Hind, Inter), spacing tokens
- **Figma**: Design reference node 315-15943 for exact UI layout

## Out of Scope

- Backend submission and API communication (separate integration spec)
- Multiple phone numbers or emails (design collects one of each)
- Reward field validation or character limits
- Android/iOS implementations (platform-specific specs handle those)
- Polished summary screen UI (current iteration uses debug view)
- Localization beyond English (future iteration)
- Dark mode support (not specified in design or requirements)

## Technical Architecture Notes

### React Component Structure

```
ContactScreen (React Component)
├── ReportMissingPetLayout (wrapper with back button, title, progress)
├── Form Container
│   ├── Heading: "Your contact info"
│   ├── Subtitle: "Add your contact information and potential reward."
│   ├── ValidatedInput: Phone number
│   ├── ValidatedInput: Email
│   ├── ValidatedInput: Reward (optional)
│   └── Helper text (conditional)
└── PrimaryButton: Continue (fixed bottom on mobile)
```

### Hook Responsibilities

- Custom hook `useContactForm` to manage input state, validation, and submission
- Integrate with existing `useReportMissingPetFlow` for session management
- Use existing `useBrowserBackHandler` for browser back button (cancels entire flow)
- In-app back arrow navigates to Step 3 while preserving flow state

### Validation Logic

- Phone: Optional, but if provided must match regex `/^\+?\d{7,11}$/` after sanitizing spaces/dashes
- Email: Optional, but if provided must match standard email regex with RFC 5322 basic compliance
- Reward: No validation
- Continue validation requires: (a) at least one of phone OR email must be valid AND (b) all non-empty fields must be valid
- Validation timing: On blur for individual fields (consistent with specs 034, 037, 039)

### Accessibility

- All inputs have associated `<label>` elements with proper for/id linkage
- Error messages display visually below invalid inputs
- Focus management on validation errors
- No aria attributes required - use semantic HTML only

### Test Identifiers

- `data-testid="contact.back.button"`
- `data-testid="contact.phoneNumber.input"`
- `data-testid="contact.email.input"`
- `data-testid="contact.reward.input"`
- `data-testid="contact.continue.button"`
- `data-testid="contact.progress.badge"`
- `data-testid="contact.title.label"`
- `data-testid="contact.subtitle.label"`

### Test Coverage Requirements

- **Unit Tests** (Hook): 80% coverage
  - Phone validation logic (7-11 digits, leading +, reject letters) when field is non-empty
  - Email validation (RFC 5322 basic) when field is non-empty
  - At least one contact method required logic (phone OR email)
  - All non-empty fields must be valid (block navigation if any field is invalid)
  - No reward field validation
  - Session save on Continue click
- **Component Tests**: 
  - Input validation errors appear/disappear on blur
  - Continue always enabled (consistent with specs 034, 037, 039)
  - Continue blocks navigation when no valid contact method exists
  - Continue blocks navigation when valid phone + invalid email (all non-empty must be valid)
  - Continue blocks navigation when invalid phone + valid email (all non-empty must be valid)
  - Continue allows navigation when phone only is valid (email empty)
  - Continue allows navigation when email only is valid (phone empty)
  - Continue allows navigation when both phone and email are valid
  - Navigation to summary on successful Continue validation
- **Integration Tests**:
  - Session persistence across navigation within flow
  - Browser refresh clears all flow state (returns to pet list)

## Related Specifications

- **[Spec 035](../035-ios-owners-details-screen/)**: iOS Owner's Details screen (functional requirements reference)
- **[Spec 034](../034-web-chip-number-screen/)**: Web Chip Number screen (Step 1) - reference implementation
- **[Spec 037](../037-web-animal-photo-screen/)**: Web Animal Photo screen (Step 2) - reference implementation
- **[Spec 039](../039-web-animal-description-screen/)**: Web Animal Description screen (Step 3) - reference implementation
- **[Spec 021](../021-announcement-photo-upload/)**: Photo upload API
- **[Spec 009](../009-create-announcement/)**: Announcements API
- **[Spec 006](../006-pets-api/)**: Pets API (phone validation reference)
