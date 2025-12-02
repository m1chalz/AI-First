# Feature Specification: iOS Owner's Details Screen

**Feature Branch**: `035-ios-owners-details-screen`  
**Created**: 2025-12-01  
**Status**: Draft  
**Input**: iOS implementation of Owner's Details screen (Step 4/4) from spec 023

**Parent Specification**: [023-owners-details-screen](https://github.com/[repo]/tree/023-owners-details-screen) - Cross-platform Owner's Details specification

## Clarifications

### Session 2025-12-02

- Q: Backend submission flow & email confirmation – should the app wait for email delivery confirmation, or only database write success? → A: Option B – App waits only for backend database write success (HTTP 200/201), then navigates to summary; backend handles email sending asynchronously after response. Backend returns both `"id"` (UUID) and `"managementPassword"` (6-digit string) in response. ViewModel extracts `managementPassword` and passes it via closure `onReportSent(managementPassword: String)` (defined by coordinator) to summary view.
- Q: Error message display pattern – should errors auto-dismiss after timeout or persist until user action? → A: Display popup alert with "Try Again" / "Cancel" buttons (native iOS UIAlertController style). User controls dismissal and retry explicitly; no auto-dismiss timers.
- Q: Validation timing & user feedback – when should validation errors appear (on blur, on keystroke, on Continue tap)? → A: Validate only on Continue tap (same pattern as AnimalDescriptionView from spec 031). Reuse existing ValidatedTextField and TextAreaView components from spec 031 for consistency.
- Q: Backend API response format – which JSON fields are returned and what do we pass to summary? → A: Backend returns both `"id"` (UUID, e.g., `"bb3fc451-1f51-407d-bb85-2569dc9baed3"`) and `"managementPassword"` (6-digit string, e.g., `"467432"`). Extract and pass `managementPassword` to summary view (not `id`). Response also includes `photoUrl: null` (photo uploaded separately per spec 021).
- Q: Reward field component type – should reward use single-line ValidatedTextField or multi-line TextAreaView? → A: Option A – Single-line ValidatedTextField (like phone/email fields) with `maxLength: 120` parameter to enforce character limit. Simpler and faster for short reward descriptions.
- Q: Report submission flow – is it a single request or multiple steps? → A: **2-step process**: (1) POST /api/v1/announcements creates announcement and returns `id` + `managementPassword`; (2) POST /api/v1/announcements/:id/photos uploads photo using Basic auth (id:managementPassword). **Happy path only**: Both must succeed for navigation to summary. If either step fails, show generic error popup with retry (retries full 2-step flow). Partial failure handling (step 1 success, step 2 fail) is out of scope for initial implementation.

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

iOS users who complete Steps 1-3 (chip, photo, description) reach the Owner's Details screen and must provide valid phone and email before submitting the report. Once both fields are valid, tapping Continue executes a 2-step submission: (1) creates announcement and receives managementPassword, (2) uploads photo using managementPassword. After both succeed, user navigates to summary screen showing managementPassword for future reference.

**Why this priority**: Contact information is mandatory for the report to be actionable. Without it, shelters/finders cannot reach the owner, making the report useless.

**Independent Test**: Deep link to Step 4 with prior steps pre-populated, fill phone and email with valid values, tap Continue, verify 2-step backend submission (announcement + photo) and navigation to summary.

**Acceptance Scenarios**:

1. **Given** the user is on Owner's details with valid phone "+48123456789" and email "owner@example.com", **When** they tap Continue, **Then** app executes step 1 (POST /api/v1/announcements) receiving id and managementPassword, then step 2 (POST /api/v1/announcements/:id/photos) uploading photo, then navigates to summary with managementPassword, and emits analytics event `missing_pet.step4_completed`.
2. **Given** the user enters valid contact info and taps Continue, **When** they navigate back from summary using the back button, **Then** the phone and email values persist exactly as entered and the progress badge returns to "4/4".

---

### User Story 2 - Receive inline validation feedback for invalid inputs (Priority: P2)

Users who mistype contact details see immediate inline validation errors (red border + error text) and the Continue button remains disabled until both fields are valid. This prevents submission of malformed contact info.

**Why this priority**: Invalid contact data breaks the reconnection flow. Inline validation catches errors early and guides users to correct them.

**Independent Test**: Enter invalid email (e.g., "owner@"), tap Continue, observe inline validation error appears, correct email to valid format, tap Continue again, verify submission succeeds.

**Acceptance Scenarios**:

1. **Given** the user enters "123" in the phone field and taps Continue, **When** validation runs, **Then** a red border appears around phone field, inline error text shows "Enter at least 7 digits", and no navigation occurs until phone is corrected to 7-11 digits and Continue is tapped again.
2. **Given** the user enters "owner@" in email and taps Continue, **When** validation runs, **Then** a red border + error text appears below email field with message "Enter a valid email address", and no navigation occurs until email is valid and Continue is tapped again.

---

### User Story 3 - Optionally add reward description with character limit (Priority: P3)

Some users want to offer a reward. They can enter free-text reward details (e.g., "$250 gift card + hugs") up to 120 characters. The field is optional and does not block Continue if left empty.

**Why this priority**: Rewards motivate community action, but forcing structured input adds friction. Free-text balances flexibility and simplicity.

**Independent Test**: Enter "$250 gift card + hugs" in reward field, navigate back to Step 3, return to Step 4, confirm text persists and is editable.

**Acceptance Scenarios**:

1. **Given** the user enters "$250 gift card + hugs" (28 chars) in reward, **When** they navigate away and back, **Then** the text persists exactly and can proceed with Continue (if phone/email valid).
2. **Given** the user enters 120 characters in reward and tries to type the 121st character, **When** they attempt additional input, **Then** ValidatedTextField maxLength enforces the limit and rejects the additional character (input stays at 120 chars max).

---

### User Story 4 - Handle submission failure gracefully (Priority: P2)

When either step of submission fails (offline, backend error, timeout), the app stays on Step 4, shows a popup alert with error message and "Try Again" / "Cancel" buttons, keeps all inputs intact, and allows user to retry full 2-step submission when ready.

**Why this priority**: Losing contact data on submission failure creates frustration. Keeping inputs intact and offering retry ensures progress is never lost.

**Independent Test**: Disable network, fill valid phone/email, tap Continue, observe popup alert with Try Again/Cancel buttons and no navigation, tap Cancel, re-enable network, tap Continue again, verify full 2-step submission succeeds.

**Acceptance Scenarios**:

1. **Given** the device is offline and user has valid phone/email, **When** they tap Continue, **Then** the app stays on Step 4, shows popup alert "No connection. Please check your network and try again." with "Try Again" and "Cancel" buttons; tapping "Try Again" retries full 2-step submission (announcement + photo), tapping "Cancel" dismisses alert and keeps inputs intact.
2. **Given** either submission step fails due to backend error (4xx/5xx), **When** the error occurs, **Then** popup alert shows "Something went wrong. Please try again later." with "Try Again" and "Cancel" buttons; tapping "Try Again" retries full 2-step submission from step 1, tapping "Cancel" dismisses alert without clearing inputs.

---

### Edge Cases

- **Phone validation**: Accept leading "+", reject letters/symbols (except digits), enforce 7-11 digits, sanitize whitespace/dashes but preserve user-entered formatting in UI.
- **Email validation**: RFC 5322-compatible (basic local@domain.tld), case-insensitive, trim whitespace.
- **Reward truncation**: ValidatedTextField with maxLength: 120 enforces hard limit at 120 chars; additional input beyond limit is rejected by component.
- **Keyboard handling**: Inputs scroll into view when keyboard appears, Continue button remains accessible above keyboard.
- **Navigation persistence**: All inputs survive device rotation, app backgrounding (up to iOS termination), and backward/forward navigation.
- **Accessibility**: VoiceOver announces field labels, validation errors, and button states; all inputs expose `ownersDetails.*` accessibility identifiers.
- **iOS version**: Requires iOS 15+ (align with project baseline from spec 017).
- **2-step submission**: Step 1 creates announcement (POST /api/v1/announcements), step 2 uploads photo (POST /api/v1/announcements/:id/photos) using Basic auth with id:managementPassword from step 1. Both must succeed for navigation to summary.
- **Failure handling**: Any failure in either step (network, backend error, timeout) shows generic error popup and retries full 2-step submission from beginning. Partial failure handling (step 1 success, step 2 fail) is out of scope for initial implementation.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The Owner's Details screen MUST render as Step 4/4 in the Missing Pet flow (spec 017) and visually match Figma node 297-8113 including circular back button, centered "Owner's details" title, progress badge "4/4", three inputs (phone, email, reward), helper text, and blue Continue button.
- **FR-002**: The circular back button and iOS back gesture MUST navigate to Step 3 (Animal Description screen from spec 031), restore its data, and update progress to "3/4" without losing Step 4 entries.
- **FR-003**: Tapping Continue MUST trigger validation of phone (7-11 digits) and email (valid RFC 5322 format); if validation fails, show inline error messages with red borders and prevent submission; if validation succeeds, execute 2-step submission: (1) POST /api/v1/announcements with announcement data (chip, description, contact, reward, location) → receive HTTP 201 with `id` and `managementPassword`; (2) POST /api/v1/announcements/:id/photos with photo using Basic auth (id:managementPassword) → receive HTTP 201. Only after BOTH succeed, call coordinator closure `onReportSent(managementPassword: String)` to navigate to summary and emit analytics event `missing_pet.step4_completed`. Backend handles confirmation email asynchronously after step 1.
- **FR-004**: Phone input MUST accept digits and leading "+", trim spaces/dashes for validation but preserve user-entered formatting in UI; when Continue is tapped, validate for 7-11 digits and show inline error "Enter at least 7 digits" with red border if invalid.
- **FR-005**: Email input MUST validate against RFC 5322-compatible pattern (basic local@domain.tld), be case-insensitive, trim whitespace; when Continue is tapped, show inline error "Enter a valid email address" with red border if invalid.
- **FR-006**: Reward description field MUST use ValidatedTextField component with `maxLength: 120` parameter, accept up to 120 UTF-8 characters (letters, numbers, symbols, currency notation), display "(optional)" in label, allow clearing to blank without error, enforce 120 character hard limit via maxLength (reject input beyond limit), and store text verbatim in session.
- **FR-007**: All inputs MUST synchronize with ReportMissingPetFlowState (spec 017 session container), survive navigation between steps, device rotation, app backgrounding (until iOS terminates app), and remain editable until flow is submitted or canceled.
- **FR-008**: Inline helper text "Add your contact information and potential reward." MUST appear below screen title.
- **FR-009**: Error states MUST use design system red (#FB2C36 or similar) for text and borders, maintain WCAG AA contrast, and immediately restore neutral borders when errors are cleared.
- **FR-010**: Continue button MUST use primary blue (#155DFC), stretch full width (327px design, responsive in code), and expose `ownersDetails.continue.tap` accessibility identifier.
- **FR-011**: When Continue is tapped with valid inputs but device is offline, the app MUST stay on Step 4, show popup alert "No connection. Please check your network and try again." with "Try Again" and "Cancel" buttons, keep all inputs intact, retry submission on "Try Again", and dismiss alert on "Cancel" without clearing inputs.
- **FR-012**: Successful Continue action MUST complete 2-step submission: (1) create announcement via POST /api/v1/announcements → receive `id` and `managementPassword`; (2) upload photo via POST /api/v1/announcements/:id/photos. Only after BOTH succeed, pass managementPassword to summary via coordinator closure `onReportSent(managementPassword: String)` and navigate to summary screen showing managementPassword for user reference. Backend sends confirmation email asynchronously after step 1. **Failure handling**: If either step fails (network error, backend 4xx/5xx, timeout), stay on Step 4, show generic popup alert "Something went wrong. Please try again later." with "Try Again" / "Cancel" buttons, preserve all inputs, and retry full 2-step submission on "Try Again".
- **FR-013**: Returning from summary to Owner's Details MUST repopulate all fields exactly as saved and re-enable Continue if session data remains valid.

### Key Entities *(include if feature involves data)*

- **OwnersDetailsViewModel**: SwiftUI ObservableObject with @Published properties (phone, email, rewardDescription), validation computed properties (isPhoneValid, isEmailValid, canContinue), coordinator closure `onReportSent: ((String) -> Void)?` for passing managementPassword, and submitForm() action that calls backend API, extracts `managementPassword` from HTTP 201 response JSON, invokes `onReportSent(managementPassword)` on success, or shows popup alert on failure.
- **ReportMissingPetFlowState**: Existing session container from spec 017, extended to store OwnerContactDetails (phone, email, rewardDescription) and computed property hasContactInfo.
- **OwnerContactDetails**: Session-bound structure containing phone, email, rewardDescription strings plus validation flags.
- **AnnouncementCreatePayload**: DTO for step 1 (POST /api/v1/announcements): species, sex, lastSeenDate, locationLatitude, locationLongitude, email, phone, status ("MISSING"), microchipNumber (optional), description (optional), reward (optional).
- **PhotoUploadPayload**: Multipart form-data for step 2 (POST /api/v1/announcements/:id/photos): photo file + Basic auth header with base64-encoded `id:managementPassword`.
- **SubmitAnnouncementRequest**: Dedicated model encapsulating full 2-step submission (announcement data + photo) for repository layer. Implementation details deferred to planning phase.
- **AnnouncementResponse**: Backend HTTP 201 response containing `"id"` (UUID string, e.g., `"bb3fc451-1f51-407d-bb85-2569dc9baed3"`), `"managementPassword"` (6-digit string, e.g., `"467432"`), and other announcement fields. Extract `managementPassword` field for passing to summary. Note: `photoUrl` returns `null` (photo uploaded separately per spec 021).

## Success Criteria *(mandatory)*

- **SC-001**: 100% of QA test runs confirm validation prevents submission when phone (7-11 digits) or email (valid format) are invalid; validation errors appear inline on Continue tap, and no invalid submissions reach backend or summary step.
- **SC-002**: 95% of iOS draft sessions that include contact info display the same values after navigating away and back, measured via debug telemetry during QA.
- **SC-003**: Analytics show <2% of completed Step 4 sessions emit validation error events after first attempt, demonstrating clear inline guidance.
- **SC-004**: 100% of successful Continue actions complete both steps (announcement creation + photo upload), return managementPassword in step 1 HTTP 201 response, and send one confirmation email (async after step 1); verified by integration tests and instrumentation.
- **SC-005**: 0% of failed Continue attempts (either step fails) navigate away from Step 4; users always receive popup alert with "Try Again" / "Cancel" buttons until full submission succeeds or user cancels.

## Assumptions

- Copywriting will correct grammatical error ("your contact your contact information's") from Figma reference while keeping intent.
- Backend submission is 2-step process per specs 009 and 021:
  - Step 1: POST /api/v1/announcements (spec 009) accepts announcement data, returns HTTP 201 with `id` (UUID) and `managementPassword` (6-digit string), and triggers email asynchronously
  - Step 2: POST /api/v1/announcements/:id/photos (spec 021) accepts photo with Basic auth (id:managementPassword), returns HTTP 201 on success
- Summary screen (from spec 017) displays managementPassword to user for future reference.
- Photo from Steps 1-3 is stored in FlowState (from spec 017) as UIImage or Data ready for multipart upload in step 2.
- Repository layer will create dedicated DTO/model for announcement submission to encapsulate 2-step flow logic.
- Reward currency/formatting is freeform; app does not auto-format or localize reward text.
- Phone validation follows same heuristics as spec 006 (Pets API) for consistency.
- iOS version baseline is 15+ (from spec 017).

## Dependencies

- **Spec 017**: Missing Pet flow architecture, ReportMissingPetCoordinator, ReportMissingPetFlowState session container, summary screen contract.
- **Spec 031**: Animal Description screen (Step 3) - backward navigation target; reuse ValidatedTextField and TextAreaView components for consistent validation UX.
- **Spec 009**: Backend API for announcements (POST /api/v1/announcements) - returns `id` (UUID) and `managementPassword` (6-digit string) in HTTP 201 response. Backend sends confirmation email asynchronously.
- **Spec 021**: Photo upload API (POST /api/v1/announcements/:id/photos) - photos uploaded separately after announcement creation; `photoUrl` field returns `null` in announcement creation response.
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
- Partial failure handling (step 1 success, step 2 fail): advanced retry logic for photo-only retry, preventing duplicate announcements, local storage of announcement ID - deferred to future iteration.
- Offline queue for background retry (user must manually tap "Try Again").
- Automatic retry on connectivity restoration (user must manually tap "Try Again").
- Photo upload progress indicator (spec 021 scope).

## Technical Architecture Notes

### SwiftUI Component Structure

```
OwnersDetailsView (SwiftUI View)
├── NavigationBar (back button, title, progress badge)
├── ScrollView (keyboard avoidance)
│   ├── VStack (main content)
│   │   ├── Title: "Your contact info"
│   │   ├── Subtitle: "Add your contact information and potential reward."
│   │   ├── ValidatedTextField: Phone number (reused from spec 031)
│   │   ├── ValidatedTextField: Email (reused from spec 031)
│   │   ├── ValidatedTextField: Reward (optional, maxLength: 120) (reused from spec 031)
│   │   └── Helper text (static subtitle)
│   └── Spacer
└── PrimaryButton: Continue (fixed bottom)
```

**Component Reuse**: ValidatedTextField component from AnimalDescriptionView (spec 031) is reused for all three input fields. Reward field uses `maxLength: 120` parameter to enforce character limit. Validation errors are displayed inline when Continue tap triggers validation.

### ViewModel Responsibilities

- Manage @Published input state (phone, email, rewardDescription)
- Manage @Published validation error states (phoneError, emailError) shown in ValidatedTextField components
- Validate inputs only when Continue is tapped (validate phone: 7-11 digits; email: RFC 5322 basic format)
- Prevent submission if validation fails; show inline validation errors via @Published error properties
- Execute 2-step submission via dependency-injected repository if validation succeeds:
  1. POST /api/v1/announcements (announcement data) → extract `id` and `managementPassword` from HTTP 201 response
  2. POST /api/v1/announcements/:id/photos (photo with Basic auth using id:managementPassword) → wait for HTTP 201
- Invoke `onReportSent(managementPassword)` closure only after BOTH steps succeed to pass managementPassword to coordinator
- Publish alert state (@Published alertMessage, showAlert) on any failure (either step): show generic error popup "Something went wrong. Please try again later." with "Try Again" / "Cancel" buttons
- Handle retry action from alert "Try Again" button: retry full 2-step submission from step 1
- Emit analytics event on full successful submission (after both steps complete)

### Coordinator Integration

- ReportMissingPetCoordinator manages navigation stack
- OwnersDetailsView created by coordinator with injected ViewModel and FlowState
- Coordinator provides `onReportSent: (String) -> Void` closure to ViewModel during initialization
- On Continue success, ViewModel invokes closure with managementPassword (6-digit string); coordinator captures managementPassword and pushes SummaryView with FlowState and managementPassword for display to user
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
  - submitForm() 2-step flow: announcement creation → photo upload
  - Full success path (both steps succeed) → invoke onReportSent closure
  - Failure path (either step fails) → show generic error popup
  - Retry logic: full 2-step retry from step 1
- **UI Tests**: 
  - Input validation errors appear/disappear on Continue tap
  - Navigation to summary only after both submission steps succeed
  - Popup alert for submission failure with retry/cancel options
  - Retry attempts full 2-step submission
- **Integration Tests**:
  - 2-step backend submission (POST announcements → POST photos)
  - Photo upload authentication with Basic auth (id:managementPassword)
  - Email notification triggered after step 1
  - Session persistence across navigation

## Related Specifications

- **[Spec 017](../017-ios-missing-pet-flow/)**: Missing Pet flow architecture (iOS)
- **[Spec 023](https://github.com/[repo]/tree/023-owners-details-screen)**: Cross-platform Owner's Details requirements
- **[Spec 031](../031-ios-animal-description-screen/)**: iOS Animal Description screen (Step 3)
- **[Spec 028](../028-ios-animal-photo-screen/)**: iOS Animal Photo screen (Step 2) - reference implementation
- **[Spec 021](../021-announcement-photo-upload/)**: Photo upload API
- **[Spec 009](../009-create-announcement/)**: Announcements API
- **[Spec 006](../006-pets-api/)**: Pets API (phone validation reference)
