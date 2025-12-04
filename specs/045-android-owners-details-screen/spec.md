# Feature Specification: Android Owner's Details Screen

**Feature Branch**: `045-android-owners-details-screen`  
**Created**: 2025-12-03  
**Status**: Draft  
**Platform**: Android  
**Input**: User description: "Based on generic spec 023 and iOS spec 035, prepare a spec for Android Owner's Details screen (Step 4/4 in Missing Pet flow)"

This feature defines the **Android Owner's Details screen** (Step 4/4 of the Missing Pet flow defined in specification 018 for Android).  
The Android UI MUST match Figma node `297-8113` from the [PetSpot wireframes design](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8113), adapted to Android Material Design patterns and Jetpack Compose implementation.

**Design References**:
- **Main screen**: Figma node `297-8113` - [PetSpot wireframes](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8113)
- **Error states**: Figma node `297-11850` - [Input field error states](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-11850)

Key visual specifications adapted to Android:
- Primary blue: `#155dfc` (buttons, borders)
- Error red: `#FB2C36` (validation errors)
- Typography: Adapt Inter Regular 32px (title), Hind Regular 16px (labels/inputs) to Material equivalents
- Input height: Standard OutlinedTextField sizing
- Border radius: 10dp, border width: 1dp, border color: `#d1d5dc`
- Vertical spacing: 24dp between form fields, 8dp between label and input

**Parent Specifications**:
- [023-owners-details-screen](https://github.com/[repo]/tree/023-owners-details-screen) - Cross-platform Owner's Details requirements
- [035-ios-owners-details-screen](../035-ios-owners-details-screen) - iOS implementation reference

## Clarifications

### Session 2025-11-26 (from cross-platform spec 023, adapted to Android scope)

- Q: Should Step 4 keep the reward input as a single free-text field or introduce structured amount/currency fields? → A: Keep a single free-text reward description; do not add structured amount or currency controls in this release.
- Q: What should happen if submission fails (network error, timeout, etc.)? → A: Remain on Step 4, show Snackbar with "Something went wrong. Please try again." with "Retry" action, keep all input intact, and allow manual retry. The app does not actively check online status - it attempts submission and reactively handles any errors.
- Q: Should the backend submission and confirmation email trigger on Step 4? → A: Yes. Step 4 must submit the full payload to the backend and trigger the confirmation email before showing the summary.
- Q: Are both contact fields mandatory? → A: Yes. The user must provide a valid phone number and a valid email address before submission can proceed.
- Q: How should the optional "Reward for the finder" field behave? → A: Treat it as a free-text description that can include numbers and words (e.g., "$250 gift card" or "Warm thanks"). Preserve the entered text exactly, but limit it to 120 characters and allow empty state without validation errors.

### Session 2025-12-04

- Q: Should Android emit analytics event (`missing_pet.step4_completed`) on successful submission like iOS? → A: No analytics events for this screen; analytics will be added in a future iteration if needed.

### Session 2025-12-03 (Android-specific clarifications)

- Q: What is the minimum Android SDK version for this feature? → A: API 26 (Android 8.0 Oreo) minimum, targeting API 36 (latest), consistent with project configuration.
- Q: What accessibility requirements apply to this screen? → A: Only testTag modifiers for automated testing (no TalkBack/accessibility announcements in scope).
- Q: When should field validation occur? → A: On submit (when Continue button tapped), consistent with other Android flow screens (specs 038, 040, 042).
- Q: How long should session data persist? → A: Until flow completion or cancellation (in-memory flow state via NavGraph-scoped ViewModel using Koin).
- Q: How should the UI behave during 2-step submission (announcement + photo upload)? → A: Continue button shows CircularProgressIndicator spinner and disables; TopAppBar back navigation disables; all input fields remain visible but form is non-submittable until completion or error.
- Q: Should back navigation be allowed during submission? → A: No. Back navigation (TopAppBar and system back) is disabled while isSubmitting = true to prevent data inconsistency and orphaned announcements.
- Q: What error display pattern should be used? → A: Snackbar for submission errors (network, backend failures) with "Retry" action; inline OutlinedTextField error states for validation failures on Continue tap.
- Q: How should photo data be handled for upload? → A: Photo is stored in ReportMissingPetFlowState as content URI (from Photo Picker in spec 040). Repository handles loading photo data from URI during step 2 upload.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Provide contact information and submit report (Priority: P1)

Android users who complete Steps 1-3 (chip, photo, description) reach the Owner's Details screen and must provide valid phone and email before submitting the report. Once both fields are valid, tapping Continue executes a 2-step submission: (1) creates announcement and receives managementPassword, (2) uploads photo using managementPassword. After both succeed, user navigates to summary screen showing managementPassword for future reference.

**Why this priority**: Contact information is mandatory for the report to be actionable. Without it, shelters/finders cannot reach the owner, making the report useless.

**Independent Test**: On Android, launch Step 4 directly via navigation from Step 3, fill phone and email with valid values, tap Continue, verify 2-step backend submission (announcement + photo) and navigation to summary.

**Acceptance Scenarios**:

1. **Given** the user is on Owner's details with valid phone "+48123456789" and email "owner@example.com", **When** they tap Continue, **Then** app executes step 1 (POST /api/v1/announcements) receiving id and managementPassword, then step 2 (POST /api/v1/announcements/:id/photos) uploading photo, then navigates to summary screen with managementPassword displayed for user reference.
2. **Given** the user completes submission and reaches the summary screen, **When** they view the screen, **Then** there is no back button - only a close button that exits the entire flow and returns to the pet list.

---

### User Story 2 - Receive inline validation feedback for invalid inputs (Priority: P2)

Users who mistype contact details see inline validation errors (OutlinedTextField error state with red border + error text) when they tap Continue, and no submission occurs until both fields are valid. This prevents submission of malformed contact info.

**Why this priority**: Invalid contact data breaks the reconnection flow. Inline validation catches errors early and guides users to correct them.

**Independent Test**: Enter invalid email (e.g., "owner@"), tap Continue, observe inline validation error appears, correct email to valid format, tap Continue again, verify submission succeeds.

**Acceptance Scenarios**:

1. **Given** the user enters "123" in the phone field and taps Continue, **When** validation runs, **Then** OutlinedTextField shows isError=true with red border, supportingText displays "Enter at least 7 digits", Snackbar shows "Please correct the highlighted fields", and no submission occurs until phone is corrected to 7-11 digits and Continue is tapped again.
2. **Given** the user enters "owner@" in email and taps Continue, **When** validation runs, **Then** OutlinedTextField shows isError=true with red border, supportingText displays "Enter a valid email address", Snackbar shows "Please correct the highlighted fields", and no submission occurs until email is valid and Continue is tapped again.

---

### User Story 3 - Optionally add reward description with character limit (Priority: P3)

Some users want to offer a reward. They can enter free-text reward details (e.g., "$250 gift card + hugs") up to 120 characters. The field is optional and does not block Continue if left empty.

**Why this priority**: Rewards motivate community action, but forcing structured input adds friction. Free-text balances flexibility and simplicity.

**Independent Test**: Enter "$250 gift card + hugs" in reward field, navigate back to Step 3, return to Step 4, confirm text persists and is editable.

**Acceptance Scenarios**:

1. **Given** the user enters "$250 gift card + hugs" (28 chars) in reward, **When** they navigate away and back, **Then** the text persists exactly and can proceed with Continue (if phone/email valid).
2. **Given** the user enters 120 characters in reward and tries to type the 121st character, **When** they attempt additional input, **Then** OutlinedTextField maxLines with counter enforces the limit and rejects the additional character (input stays at 120 chars max).

---

### User Story 4 - Handle submission failure gracefully (Priority: P2)

When either step of submission fails (network timeout, connection error, backend error), the app stays on Step 4, shows a Snackbar with error message and "Retry" action, keeps all inputs intact, and allows user to retry full 2-step submission when ready. The app does not actively check online status - it attempts submission and reactively handles any errors.

**Why this priority**: Losing contact data on submission failure creates frustration. Keeping inputs intact and offering retry ensures progress is never lost.

**Independent Test**: Fill valid phone/email, tap Continue, simulate network failure during submission, observe Snackbar with Retry action and no navigation, tap Retry after network recovers, verify full 2-step submission succeeds.

**Acceptance Scenarios**:

1. **Given** the user has valid phone/email and taps Continue, **When** submission fails due to network timeout or connection error, **Then** the app stays on Step 4, shows Snackbar "Something went wrong. Please try again." with "Retry" action; tapping "Retry" retries full 2-step submission from step 1.
2. **Given** the user has valid phone/email and taps Continue, **When** submission fails due to backend error (4xx/5xx), **Then** the app stays on Step 4, shows Snackbar "Something went wrong. Please try again." with "Retry" action; tapping "Retry" retries full 2-step submission from step 1.

---

### Edge Cases

- **Phone validation**: Accept leading "+", reject letters/symbols (except digits), enforce 7-11 digits, sanitize whitespace/dashes but preserve user-entered formatting in UI.
- **Email validation**: RFC 5322-compatible (basic local@domain.tld), case-insensitive, trim whitespace.
- **Reward truncation**: OutlinedTextField with maxLength: 120 enforces hard limit at 120 chars; additional input beyond limit is rejected by component.
- **Keyboard handling**: IME scrolling ensures inputs remain visible when keyboard appears, Continue button remains accessible above keyboard.
- **Navigation persistence**: All inputs survive device rotation, app backgrounding (up to process death), and backward/forward navigation within the flow session.
- **Configuration changes**: ViewModel state survives device rotation; process death clears state (consistent with other flow screens).
- **2-step submission**: Step 1 creates announcement (POST /api/v1/announcements), step 2 uploads photo (POST /api/v1/announcements/:id/photos) using Basic auth with id:managementPassword from step 1. Both must succeed for navigation to summary.
- **Submission loading state**: During 2-step submission, Continue button displays CircularProgressIndicator and is disabled; TopAppBar back button is also disabled to prevent navigation away mid-submission; input fields remain visible but not submittable.
- **Back navigation blocking**: TopAppBar navigation icon and system back gesture are disabled (non-responsive) while isSubmitting = true to prevent data inconsistency from partial submission.
- **Failure handling**: Any failure in either step (network timeout, connection error, backend error) shows Snackbar with "Retry" action and retries full 2-step submission from beginning. No active online/offline check - errors are handled reactively. Partial failure handling (step 1 success, step 2 fail) is out of scope for initial implementation.

## Requirements *(mandatory)*

### Dependencies

- **Spec 018** (018-android-missing-pet-flow): Provides navigation scaffolding (nested Navigation Graph infrastructure), shared flow ViewModel, and placeholder screens. This spec enhances the existing placeholder with actual contact details input functionality.
- **Spec 042** (042-android-animal-description-screen): Provides patterns for Android form implementation, validation on submit, and MVI architecture.
- **Spec 009** (009-create-announcement): Backend API contract for POST /api/v1/announcements.
- **Spec 021** (021-announcement-photo-upload): Backend API contract for POST /api/v1/announcements/:id/photos.

### Functional Requirements

- **FR-001**: The Owner's Details screen MUST render as Step 4/4 in the Missing Pet flow (spec 018) and visually match Figma node 297-8113 including TopAppBar with navigation icon (back arrow), centered "Owner's details" title, progress badge "4/4", three vertically stacked inputs (phone, email, reward), helper text, and blue Continue button adapted to Material Design 3.
- **FR-002**: The TopAppBar navigation icon (back arrow) MUST navigate to Step 3 (Animal Description screen from spec 042), restore its data, and update progress to "3/4" without losing Step 4 entries, unless isSubmitting = true (in which case back navigation is disabled).
- **FR-003**: Tapping Continue MUST trigger validation of phone (7-11 digits) and email (valid RFC 5322 format); if validation fails, show inline error messages via OutlinedTextField isError + supportingText and display Snackbar with summary message; if validation succeeds, execute 2-step submission: (1) POST /api/v1/announcements with announcement data (chip, description, contact, reward, location, photo metadata) → receive HTTP 201 with `id` and `managementPassword`; (2) POST /api/v1/announcements/:id/photos with photo using Basic auth (id:managementPassword) → receive HTTP 201. Only after BOTH succeed, navigate to summary screen with managementPassword. Backend handles confirmation email asynchronously after step 1.
- **FR-004**: Phone input MUST be implemented as OutlinedTextField accepting digits and leading "+", trim spaces/dashes for validation but preserve user-entered formatting in UI; validation occurs when Continue is tapped, showing supportingText "Enter at least 7 digits" with isError=true if invalid.
- **FR-005**: Email input MUST be implemented as OutlinedTextField validating against RFC 5322-compatible pattern (basic local@domain.tld), be case-insensitive, trim whitespace; validation occurs when Continue is tapped, showing supportingText "Enter a valid email address" with isError=true if invalid.
- **FR-006**: Reward description field MUST be implemented as OutlinedTextField with maxLength: 120, display "(optional)" label, accept up to 120 UTF-8 characters (letters, numbers, symbols, currency notation), show live character counter, allow clearing to blank without error, enforce 120 character hard limit, and store text verbatim in session.
- **FR-007**: All inputs MUST synchronize with ReportMissingPetFlowState (spec 018 session container), survive navigation between steps, device rotation, app backgrounding (until process death), and remain editable until flow is submitted or canceled.
- **FR-008**: Inline helper text "Add your contact information and potential reward." MUST appear below screen title.
- **FR-009**: Error states MUST use Material Design 3 error color (red) for OutlinedTextField isError state and supportingText, maintaining WCAG AA contrast; clearing the error should immediately restore the default border.
- **FR-010**: Continue button MUST use primary blue (#155DFC), stretch full width matching design system, and expose testTag `ownersDetails.continueButton` for automation.
- **FR-011**: When Continue is tapped with valid inputs and submission fails (network timeout, connection error, or any other failure), the app MUST stay on Step 4, show Snackbar "Something went wrong. Please try again." with "Retry" action, keep all inputs intact; tapping "Retry" retries full 2-step submission. The app MUST NOT actively check online status - it attempts submission and reactively handles errors.
- **FR-012**: Successful Continue action MUST complete 2-step submission: (1) create announcement via POST /api/v1/announcements → receive `id` and `managementPassword`; (2) upload photo via POST /api/v1/announcements/:id/photos. During submission, Continue button MUST display CircularProgressIndicator spinner and remain disabled (prevent double submission). Only after BOTH steps succeed, navigate to summary screen with managementPassword for user reference. Backend sends confirmation email asynchronously after step 1. **Failure handling**: If either step fails (network error, backend 4xx/5xx, timeout), stay on Step 4, show Snackbar with "Retry" action, re-enable Continue button, preserve all inputs, and retry full 2-step submission on "Retry".
- **FR-013**: Summary screen MUST NOT include a back button; user can only exit the flow via close button which returns to the pet list screen. Flow state is cleared upon exiting from summary.
- **FR-014**: TopAppBar navigation icon and system back gesture MUST be disabled during 2-step submission (while isSubmitting = true) to prevent data inconsistency and orphaned announcements. Navigation icon should remain visible but non-responsive until submission completes or fails.
- **FR-015**: System back gesture MUST behave the same as TopAppBar navigation icon (navigate to Step 3 when not submitting, disabled when submitting).
- **FR-016**: All interactive Composables MUST have Modifier.testTag() attributes following the `{screen}.{element}` naming convention for automated testing (e.g., `ownersDetails.phoneInput`, `ownersDetails.emailInput`, `ownersDetails.rewardInput`, `ownersDetails.continueButton`).
- **FR-017**: ViewModel MUST follow MVI pattern with single StateFlow<UiState>, sealed UserIntent, and SharedFlow<UiEffect> as mandated by project architecture.

### Key Entities *(include if feature involves data)*

- **ReportMissingPetFlowState** (existing): Flow state container (managed by NavGraph-scoped ViewModel) extended to store contact details: phone, email, rewardDescription (as flat string properties).
- **OwnerDetailsUiState**: Immutable data class representing the current UI state of the Owner's Details screen. Contains all form field values (phone, email, reward), validation error states (phoneError, emailError), isSubmitting flag, and other UI-relevant properties.
- **OwnerDetailsUserIntent**: Sealed class representing all possible user actions on this screen (UpdatePhone, UpdateEmail, UpdateReward, ContinueClicked, BackClicked, RetryClicked, SnackbarDismissed).
- **OwnerDetailsUiEffect**: Sealed class for one-off events (NavigateToSummary(managementPassword: String), NavigateBack, ShowSnackbar(message: String, action: SnackbarAction?)).
- **AnnouncementCreatePayload**: DTO for step 1 (POST /api/v1/announcements): species, sex, lastSeenDate, locationLatitude, locationLongitude, email, phone, status ("MISSING"), microchipNumber (optional), description (optional), reward (optional).
- **AnnouncementResponse**: Backend HTTP 201 response containing `"id"` (UUID string), `"managementPassword"` (6-digit string), and other announcement fields. Extract `managementPassword` field for passing to summary.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of QA test runs confirm validation prevents submission when phone (7-11 digits) or email (valid format) are invalid; validation errors appear inline on Continue tap, and no invalid submissions reach backend or summary step.
- **SC-002**: 95% of Android draft sessions that include contact info display the same values after navigating away and back, measured via debug telemetry during QA.
- **SC-003**: Unit tests for ViewModel achieve 80% line and branch coverage (mandated by project rules).
- **SC-004**: 100% of successful Continue actions complete both steps (announcement creation + photo upload), return managementPassword in step 1 HTTP 201 response, and send one confirmation email (async after step 1); verified by integration tests.
- **SC-005**: 0% of failed Continue attempts (either step fails) navigate away from Step 4; users always receive Snackbar with "Retry" action until full submission succeeds.
- **SC-006**: Screen layout adapts correctly to common Android device sizes and orientations without content clipping or overlap.

## Assumptions

- Copywriting will correct grammatical error ("your contact your contact information's") from Figma reference while keeping intent.
- Backend submission is 2-step process per specs 009 and 021:
  - Step 1: POST /api/v1/announcements (spec 009) accepts announcement data, returns HTTP 201 with `id` (UUID) and `managementPassword` (6-digit string), and triggers email asynchronously
  - Step 2: POST /api/v1/announcements/:id/photos (spec 021) accepts photo with Basic auth (id:managementPassword), returns HTTP 201 on success
- Summary screen (from spec 018) displays managementPassword to user for future reference.
- Photo from Step 2 is stored in FlowState as content URI (from Android Photo Picker in spec 040). Repository handles loading photo data from URI during step 2 photo upload.
- Reward currency/formatting is freeform; app does not auto-format or localize reward text (user-entered text is submitted as-is to backend).
- Phone validation follows same heuristics as spec 006 (Pets API) for consistency.
- This feature targets Android API 26 (Android 8.0 Oreo) as minimum, targeting API 36 (latest), consistent with project-wide configuration.
- Implementation will use Jetpack Compose for UI.
- Koin dependency injection is mandatory per project architecture.
- ContactDetailsPlaceholder screen already exists in the codebase from spec 018. This implementation will replace placeholder content with full functionality.

## Dependencies

- Navigation scaffolding and flow state container from specification 018 (Missing Pet flow): Nested NavGraph and shared ViewModel.
- Animal Description screen (Step 3) from specification 042 - backward navigation target.
- Backend API for announcements from specification 009 (POST /api/v1/announcements) - returns `id` and `managementPassword`.
- Photo upload API from specification 021 (POST /api/v1/announcements/:id/photos).
- Design tokens (colors, typography) defined in `.specify/memory/constitution.md`.

## Out of Scope

- Multiple phone numbers or emails (design collects one of each).
- Currency selection, structured reward amount, or reward terms/conditions.
- iOS/Web implementations (handled by their platform-specific specs).
- Summary screen layout changes (governed by spec 018).
- Partial failure handling (step 1 success, step 2 fail): advanced retry logic for photo-only retry, preventing duplicate announcements, local storage of announcement ID - deferred to future iteration.
- Background retry queue (user must manually tap "Retry").
- Automatic retry on error recovery (user must manually tap "Retry").
- Active online/offline status checking (errors are handled reactively).
- Photo upload progress indicator (spec 021 scope).
- TalkBack/accessibility announcements (only testTag for automated testing is in scope).
- Localization beyond English (future iteration).
- Analytics events (e.g., `missing_pet.step4_completed`) - deferred to future iteration.

## Technical Architecture Notes

### Jetpack Compose Structure

**File to Create/Modify**: `ContactDetailsContent.kt` (replace placeholder from spec 018)

```
ContactDetailsContent (Composable)
├── Scaffold
│   ├── TopAppBar (navigation icon, title "Owner's details", progress badge "4/4")
│   └── Content
│       ├── Column (scrollable with IME padding)
│       │   ├── Title: "Your contact info"
│       │   ├── Subtitle: "Add your contact information and potential reward."
│       │   ├── OutlinedTextField: Phone number
│       │   │   ├── label: "Phone number"
│       │   │   ├── keyboardOptions: KeyboardType.Phone
│       │   │   ├── isError: phoneError != null
│       │   │   └── supportingText: phoneError (when invalid)
│       │   ├── OutlinedTextField: Email
│       │   │   ├── label: "Email"
│       │   │   ├── keyboardOptions: KeyboardType.Email
│       │   │   ├── isError: emailError != null
│       │   │   └── supportingText: emailError (when invalid)
│       │   ├── OutlinedTextField: Reward (optional)
│       │   │   ├── label: "Reward (optional)"
│       │   │   ├── maxLength: 120
│       │   │   └── supportingText: character counter
│       │   └── Spacer
│       └── Button: Continue (full width, bottom)
│           ├── enabled: !isSubmitting
│           └── content: if (isSubmitting) CircularProgressIndicator else Text("Continue")
└── SnackbarHost (for error messages)
```

### ViewModel Responsibilities

**File to Create**: `OwnerDetailsViewModel.kt`

- Manage StateFlow<OwnerDetailsUiState> with input state (phone, email, reward)
- Manage validation error states (phoneError, emailError) in UiState
- Manage isSubmitting state (true during 2-step submission, false otherwise) to control Continue button spinner and disabled state
- Process UserIntent via reducer pattern:
  - UpdatePhone → update phone value, clear phoneError
  - UpdateEmail → update email value, clear emailError
  - UpdateReward → update reward value (with 120 char limit)
  - ContinueClicked → validate, then submit if valid
  - BackClicked → emit NavigateBack effect
  - RetryClicked → retry submission
- Validate inputs only when ContinueClicked is processed:
  - Phone: 7-11 digits (after stripping non-digits except leading +)
  - Email: RFC 5322 basic pattern
- Execute 2-step submission via dependency-injected repository if validation succeeds:
  1. Set isSubmitting = true
  2. POST /api/v1/announcements → extract `id` and `managementPassword` from HTTP 201 response
  3. POST /api/v1/announcements/:id/photos → wait for HTTP 201
  4. Set isSubmitting = false on completion (success or failure)
- Emit NavigateToSummary(managementPassword) effect only after BOTH steps succeed
- Emit ShowSnackbar effect on any failure (either step): set isSubmitting = false, show error message with "Retry" action
- Handle RetryClicked: retry full 2-step submission from step 1 (sets isSubmitting = true again)

### Test Identifiers

- `ownersDetails.backButton`
- `ownersDetails.phoneInput`
- `ownersDetails.emailInput`
- `ownersDetails.rewardInput`
- `ownersDetails.continueButton`
- `ownersDetails.progressBadge`
- `ownersDetails.title`
- `ownersDetails.subtitle`

### Test Coverage Requirements

- **Unit Tests** (ViewModel): 80% coverage
  - Phone validation logic (7-11 digits, leading +, reject letters)
  - Email validation (RFC 5322 basic)
  - Reward character limit (120 chars)
  - submitForm() 2-step flow: announcement creation → photo upload
  - Loading state management: isSubmitting = true during submission, false on completion
  - Full success path (both steps succeed) → emit NavigateToSummary effect, set isSubmitting = false
  - Failure path (either step fails) → set isSubmitting = false, emit ShowSnackbar effect
  - Retry logic: full 2-step retry from step 1, set isSubmitting = true again
  - UserIntent processing via reducer
- **UI Tests**: 
  - Input validation errors appear/disappear on Continue tap
  - Continue button shows spinner and disables during submission
  - Back navigation disabled during submission (non-interactive state)
  - Continue and back re-enable after submission failure
  - Navigation to summary only after both submission steps succeed
  - Snackbar for submission failure with retry action
  - Retry attempts full 2-step submission
- **Integration Tests**:
  - 2-step backend submission (POST announcements → POST photos)
  - Photo upload authentication with Basic auth (id:managementPassword)
  - Session persistence across navigation

## Related Specifications

- **[Spec 018](../018-android-missing-pet-flow/)**: Missing Pet flow architecture (Android)
- **[Spec 023](https://github.com/[repo]/tree/023-owners-details-screen)**: Cross-platform Owner's Details requirements
- **[Spec 035](../035-ios-owners-details-screen/)**: iOS Owner's Details screen reference
- **[Spec 042](https://github.com/[repo]/tree/042-android-animal-description-screen)**: Android Animal Description screen (Step 3) - backward navigation target, MVI pattern reference
- **[Spec 040](../040-android-animal-photo-screen/)**: Android Animal Photo screen (Step 2) - reference implementation
- **[Spec 038](../038-android-chip-number-screen/)**: Android Chip Number screen (Step 1) - reference implementation
- **[Spec 021](../021-announcement-photo-upload/)**: Photo upload API
- **[Spec 009](../009-create-announcement/)**: Announcements API
- **[Spec 006](../006-pets-api/)**: Pets API (phone validation reference)

