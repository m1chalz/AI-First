# Feature Specification: iOS Owner's Details Screen

**Feature Branch**: `029-ios-owners-details-screen`  
**Created**: 2025-12-01  
**Status**: Draft  
**Input**: iOS implementation of Owner's Details screen (Step 4/4) from spec 023

**Parent Specification**: [023-owners-details-screen](https://github.com/[repo]/tree/023-owners-details-screen) - Cross-platform Owner's Details specification

## Scope & Background

This specification defines the iOS-specific implementation of the Owner's Details screen (Step 4/4) in the Missing Pet flow. It builds on the functional requirements from specification 023 but focuses exclusively on SwiftUI/UIKit implementation, iOS patterns, and integration with the ReportMissingPetCoordinator from specification 017.

**Key Scope Items**:
- SwiftUI UI implementation matching Figma node 297-8113 exactly
- MVVM-C architecture with coordinator-based navigation
- Integration with ReportMissingPetFlowState session management
- iOS-native validation patterns and error handling
- Backend submission on Continue (finalizes report before summary)
- Offline handling with inline retry messaging

**Out of Scope**:
- Android/Web implementations (handled by other platform specs)
- Backend API changes (spec 009/021 contracts remain unchanged)
- Summary screen layout (governed by spec 017)
- Localization beyond English (future iteration)

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Enter contact information to finalize report (Priority: P1)

iOS users who complete Steps 1-3 (chip, photo, description) reach the Owner's Details screen and must provide valid phone and email before submitting the report. Once both fields are valid, tapping Continue submits the full payload to the backend, triggers a confirmation email, and navigates to the summary screen.

**Why this priority**: Contact information is mandatory for the report to be actionable. Without it, shelters/finders cannot reach the owner, making the report useless.

**Independent Test**: Deep link to Step 4 with prior steps pre-populated, fill phone and email with valid values, tap Continue, verify backend submission and navigation to summary.

**Acceptance Scenarios**:

1. **Given** the user is on Owner's details with empty inputs, **When** they enter "+48123456789" in phone and "owner@example.com" in email, **Then** Continue enables and tapping it submits the report to backend, triggers confirmation email, and navigates to summary with analytics event `missing_pet.step4_completed`.
2. **Given** the user enters valid contact info and taps Continue, **When** they navigate back from summary using the back button, **Then** the phone and email values persist exactly as entered and the progress badge returns to "4/4".

---

### User Story 2 - Receive inline validation feedback for invalid inputs (Priority: P2)

Users who mistype contact details see immediate inline validation errors (red border + error text) and the Continue button remains disabled until both fields are valid. This prevents submission of malformed contact info.

**Why this priority**: Invalid contact data breaks the reconnection flow. Inline validation catches errors early and guides users to correct them.

**Independent Test**: Enter invalid email (e.g., "owner@"), blur field, observe inline error and disabled Continue, correct email, verify Continue re-enables.

**Acceptance Scenarios**:

1. **Given** the user enters "123" in the phone field, **When** focus leaves the field, **Then** a red border appears, inline error text shows "Enter at least 7 digits", and Continue stays disabled until phone is corrected to 7-11 digits.
2. **Given** the user enters "owner@" in email, **When** they attempt to continue, **Then** Continue stays disabled, a red border + error text appears below email field explaining format requirement (e.g., "Enter a valid email address"), and tapping Continue does nothing until email is valid.

---

### User Story 3 - Optionally add reward description with character limit (Priority: P3)

Some users want to offer a reward. They can enter free-text reward details (e.g., "$250 gift card + hugs") up to 120 characters. The field is optional and does not block Continue if left empty.

**Why this priority**: Rewards motivate community action, but forcing structured input adds friction. Free-text balances flexibility and simplicity.

**Independent Test**: Enter "$250 gift card + hugs" in reward field, navigate back to Step 3, return to Step 4, confirm text persists and is editable.

**Acceptance Scenarios**:

1. **Given** the user enters "$250 gift card + hugs" (28 chars) in reward, **When** they navigate away and back, **Then** the text persists exactly and Continue remains enabled (if phone/email valid).
2. **Given** the user enters 121 characters in reward, **When** they try to type the 121st character, **Then** input is rejected, inline helper text shows "Keep reward details under 120 characters", and Continue remains enabled as long as phone/email are valid.

---

### User Story 4 - Handle offline submission gracefully (Priority: P2)

When the device lacks connectivity and the user taps Continue (with valid inputs), the app stays on Step 4, shows inline "No connection. Try again" messaging, keeps all inputs intact, and re-enables Continue once connectivity returns for manual retry.

**Why this priority**: Losing contact data on submission failure creates frustration. Offline resilience ensures progress is never lost.

**Independent Test**: Disable network, fill valid phone/email, tap Continue, observe inline error and no navigation, re-enable network, tap Continue again, verify submission succeeds.

**Acceptance Scenarios**:

1. **Given** the device is offline and user has valid phone/email, **When** they tap Continue, **Then** the app stays on Step 4, shows inline text "No connection. Try again" for 3 seconds, disables Continue briefly, then re-enables it once connectivity returns without clearing inputs.
2. **Given** the submission fails due to backend error (500), **When** the error occurs, **Then** inline text shows "Something went wrong. Try again", Continue re-enables after 2 seconds, and user can retry without losing inputs.

---

### Edge Cases

- **Phone validation**: Accept leading "+", reject letters/symbols (except digits), enforce 7-11 digits, sanitize whitespace/dashes but preserve user-entered formatting in UI.
- **Email validation**: RFC 5322-compatible (basic local@domain.tld), case-insensitive, trim whitespace.
- **Reward truncation**: Hard limit at 120 chars, show warning at 110+ chars, reject additional input beyond limit.
- **Keyboard handling**: Inputs scroll into view when keyboard appears, Continue button remains accessible above keyboard.
- **Navigation persistence**: All inputs survive device rotation, app backgrounding (up to iOS termination), and backward/forward navigation.
- **Accessibility**: VoiceOver announces field labels, validation errors, and button states; all inputs expose `ownersDetails.*` accessibility identifiers.
- **iOS version**: Requires iOS 15+ (align with project baseline from spec 017).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The Owner's Details screen MUST render as Step 4/4 in the Missing Pet flow (spec 017) and visually match Figma node 297-8113 including circular back button, centered "Owner's details" title, progress badge "4/4", three inputs (phone, email, reward), helper text, and blue Continue button.
- **FR-002**: The circular back button and iOS back gesture MUST navigate to Step 3 (Animal Description screen from spec 031), restore its data, and update progress to "3/4" without losing Step 4 entries.
- **FR-003**: Continue MUST remain disabled until both phone (7-11 digits) and email (valid RFC 5322 format) pass validation; tapping Continue when enabled MUST submit the full Missing Pet payload (chip, photo, description, contact, reward) to backend via POST /api/announcements, trigger confirmation email to owner's email, emit analytics event `missing_pet.step4_completed`, and navigate to summary screen.
- **FR-004**: Phone input MUST accept digits and leading "+", trim spaces/dashes for validation but preserve user-entered formatting in UI, enforce 7-11 digits, and show inline error "Enter at least 7 digits" with red border when invalid.
- **FR-005**: Email input MUST validate against RFC 5322-compatible pattern (basic local@domain.tld), be case-insensitive, trim whitespace, and show inline error "Enter a valid email address" with red border when invalid.
- **FR-006**: Reward description field MUST accept up to 120 UTF-8 characters (letters, numbers, symbols, currency notation), display "(optional)" in label, allow clearing to blank without error, reject input beyond 120 chars, show inline warning "Keep reward details under 120 characters" at limit, and store text verbatim in session.
- **FR-007**: All inputs MUST synchronize with ReportMissingPetFlowState (spec 017 session container), survive navigation between steps, device rotation, app backgrounding (until iOS terminates app), and remain editable until flow is submitted or canceled.
- **FR-008**: Inline helper text "Add your contact information and potential reward." MUST appear below screen title; additional helper "Provide a valid phone number and email address" MUST appear below input stack when Continue is disabled due to validation.
- **FR-009**: Error states MUST use design system red (#FB2C36 or similar) for text and borders, maintain WCAG AA contrast, and immediately restore neutral borders when errors are cleared.
- **FR-010**: Continue button MUST use primary blue (#155DFC), stretch full width (327px design, responsive in code), expose `ownersDetails.continue.tap` accessibility identifier, and disable with gray appearance when validation fails.
- **FR-011**: When Continue is tapped with valid inputs but device is offline, the app MUST stay on Step 4, show inline "No connection. Try again" for 3 seconds, keep all inputs intact, and re-enable Continue once connectivity returns for manual retry.
- **FR-012**: Successful Continue action MUST persist entire dataset (Steps 1-4) to backend database, trigger outbound confirmation email to owner's email address, and navigate to summary screen; failures MUST keep user on Step 4, show inline error messaging, preserve inputs, and allow retry.
- **FR-013**: Returning from summary to Owner's Details MUST repopulate all fields exactly as saved and re-enable Continue if session data remains valid.

### Key Entities *(include if feature involves data)*

- **OwnersDetailsViewModel**: SwiftUI ObservableObject with @Published properties (phone, email, rewardDescription), validation computed properties (isPhoneValid, isEmailValid, canContinue), and submitForm() action that calls backend API and notifies coordinator on success/failure.
- **ReportMissingPetFlowState**: Existing session container from spec 017, extended to store OwnerContactDetails (phone, email, rewardDescription) and computed property hasContactInfo.
- **OwnerContactDetails**: Session-bound structure containing phone, email, rewardDescription strings plus validation flags.
- **MissingPetBackendPayload**: Aggregate DTO sent to backend: chipNumber, photoReference, animalDescription, locationMetadata, contactDetails, rewardDescription.

## Success Criteria *(mandatory)*

- **SC-001**: 100% of QA test runs confirm Continue stays disabled until both phone (7-11 digits) and email (valid format) are provided; no invalid submissions reach summary step.
- **SC-002**: 95% of iOS draft sessions that include contact info display the same values after navigating away and back, measured via debug telemetry during QA.
- **SC-003**: At least 90% of users who reach Step 4 during usability studies complete it in ≤60 seconds, indicating form is lightweight.
- **SC-004**: Analytics show <2% of completed Step 4 sessions emit validation error events after first attempt, demonstrating clear inline guidance.
- **SC-005**: 100% of successful Continue actions create a backend record and send one confirmation email, verified by integration tests and instrumentation.
- **SC-006**: 0% of offline Continue attempts navigate away from Step 4; users always receive inline "No connection. Try again" messaging until submission succeeds.

## Assumptions

- Copywriting will correct grammatical error ("your contact your contact information's") from Figma reference while keeping intent.
- Backend submission endpoint (POST /api/announcements from spec 009/021) accepts full payload and triggers email without additional client-side steps.
- Reward currency/formatting is freeform; app does not auto-format or localize reward text.
- Phone validation follows same heuristics as spec 006 (Pets API) for consistency.
- iOS version baseline is 15+ (from spec 017).

## Dependencies

- **Spec 017**: Missing Pet flow architecture, ReportMissingPetCoordinator, ReportMissingPetFlowState session container, summary screen contract.
- **Spec 031**: Animal Description screen (Step 3) - backward navigation target.
- **Spec 009/021**: Backend API for announcements (POST /api/announcements), photo upload, and email notification trigger.
- **Design System**: Colors (#155DFC blue, #FB2C36 red, #364153 labels), typography (Hind, Inter, SF Pro fallback), spacing tokens from `.specify/memory/constitution.md`.
- **Analytics**: Instrumentation pipeline for `missing_pet.step4_completed` event.
- **Figma**: Design reference node 297-8113 for exact UI layout.

## Out of Scope

- Multiple phone numbers or emails (design collects one of each).
- Currency selection, structured reward amount, or reward terms/conditions.
- Android/Web implementations (platform-specific specs handle those).
- Summary screen layout changes (governed by spec 017).
- Localization beyond English (future iteration).
- Dark mode support (not specified in design or requirements).

## Technical Architecture Notes

### SwiftUI Component Structure

```
OwnersDetailsView (SwiftUI View)
├── NavigationBar (back button, title, progress badge)
├── ScrollView (keyboard avoidance)
│   ├── VStack (main content)
│   │   ├── Title: "Your contact info"
│   │   ├── Subtitle: "Add your contact information and potential reward."
│   │   ├── ValidatedTextField: Phone number
│   │   ├── ValidatedTextField: Email
│   │   ├── ValidatedTextField: Reward (optional)
│   │   └── Helper text (conditional)
│   └── Spacer
└── PrimaryButton: Continue (fixed bottom)
```

### ViewModel Responsibilities

- Manage @Published input state (phone, email, rewardDescription)
- Compute validation properties (isPhoneValid, isEmailValid, canContinue)
- Validate on field blur and on Continue tap
- Call backend API via dependency-injected service
- Notify coordinator on success (navigate to summary) or failure (show inline error)
- Emit analytics event on successful submission

### Coordinator Integration

- ReportMissingPetCoordinator manages navigation stack
- OwnersDetailsView created by coordinator with injected ViewModel and FlowState
- On Continue success, coordinator pushes SummaryView with FlowState
- On back button, coordinator pops to AnimalDescriptionView
- FlowState persists across navigation

### Accessibility Identifiers

- `ownersDetails.back.button`
- `ownersDetails.phoneNumber.input`
- `ownersDetails.email.input`
- `ownersDetails.reward.input`
- `ownersDetails.continue.tap`
- `ownersDetails.progress.badge`
- `ownersDetails.title.label`
- `ownersDetails.subtitle.label`

### Test Coverage Requirements

- **Unit Tests** (ViewModel): 80% coverage
  - Phone validation logic (7-11 digits, leading +, reject letters)
  - Email validation (RFC 5322 basic)
  - Reward character limit (120 chars)
  - canContinue computed property
  - submitForm() success/failure paths
- **UI Tests**: 
  - Input validation errors appear/disappear
  - Continue disabled/enabled based on validation
  - Navigation to summary on success
  - Offline error handling
- **Integration Tests**:
  - Backend submission with valid payload
  - Email notification triggered
  - Session persistence across navigation

## Related Specifications

- **[Spec 017](../017-ios-missing-pet-flow/)**: Missing Pet flow architecture (iOS)
- **[Spec 023](https://github.com/[repo]/tree/023-owners-details-screen)**: Cross-platform Owner's Details requirements
- **[Spec 031](../031-ios-animal-description-screen/)**: iOS Animal Description screen (Step 3)
- **[Spec 028](../028-ios-animal-photo-screen/)**: iOS Animal Photo screen (Step 2) - reference implementation
- **[Spec 021](../021-announcement-photo-upload/)**: Photo upload API
- **[Spec 009](../009-create-announcement/)**: Announcements API
- **[Spec 006](../006-pets-api/)**: Pets API (phone validation reference)
