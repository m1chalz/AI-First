# Feature Specification: Report Created Confirmation Screen

**Feature Branch**: `043-ios-report-created-screen`  
**Created**: 2025-12-03  
**Status**: Draft  
**Input**: User description: "iOS Report Created Confirmation Screen (copied from spec 024)"  
**Figma Design**: [Report created screen](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8193&m=dev)  
**Platform Scope**: iOS (SwiftUI) only.

## Clarifications

### Session 2025-11-26

- Q: Which analytics payload should accompany `report.confirmation.viewed` and `report.confirmation.dismiss` events? → A: Include `{platform, codeLength, codeLastFour}` in both events.

## Design Summary

- Screen lives at the final step of the "Report a Missing Animal" flow. It is a full-screen modal/card framed inside a 375×814 reference device with 46px rounded corners and safe-area-aware top/bottom padding.
- Header text "Report created" uses Hind Regular, 32px, rgba(0,0,0,0.8) and sits 32px below the system status bar.
- Body copy is two paragraphs (16px, #545F71, Hind Regular) describing that the report is stored, notifications will be sent, and the removal code must be used to delete the report later. Line length constrained to 325px for readability.
- Removal code module centers a 6–7 digit code (`5216577` in design) inside a 328×90 px container with 10px radius. Background uses a horizontal gradient from #5C33FF to #F84BA1 plus a soft blur glow (#FB64B6 @ 20% alpha). Digits use Arial Regular, 60px, white text with -1.5px tracking.
- Primary action is a full-width button labeled "Close," 52px tall, 327px wide, rounded corners (10px), blue background #155DFC, white text 18px. Button is fixed 24px from bottom safe area.
- No icons, progress indicators, or secondary actions are shown. Focus is on confirmation messaging, reusable removal code, and closing the flow.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Understand confirmation outcome (Priority: P1)

As a pet owner who just finished submitting a missing animal report, I can immediately see that the report succeeded along with what happens next, so that I trust the system captured my information and know to watch for notifications.

**Why this priority**: Without an explicit confirmation, users may re-submit reports or abandon the app. Clear messaging prevents duplicates and support tickets.

**Independent Test**: Launch the screen with mocked data and verify the header/body copy render exactly as specified, independent of the removal code retrieval logic.

**Acceptance Scenarios**:

1. **Given** the reporting flow finishes without server errors, **When** the confirmation screen appears, **Then** the title "Report created" and both paragraphs of copy are visible with specified typography and spacing.
2. **Given** a tester triggers the confirmation via deep link or UI automation, **When** the confirmation screen loads, **Then** there is no residual loading indicator or secondary actions—only the messaging, code module, and Close button.
3. **Given** the screen is rotated (Android) or resized (web), **When** layout recalculates, **Then** the confirmation copy remains readable (min 16px font, max 600px line length) and centered content alignment is preserved.

---

### User Story 2 - Retrieve and safeguard removal code (Priority: P1)

As a pet owner, I can clearly read and copy the unique removal code assigned to my report, so that I can delete or edit the report later without contacting support.

**Why this priority**: The removal code is the only credential for managing the report post-submission. Losing it blocks users from closing their report.

**Independent Test**: Provide a mock removal code via API or local data and ensure the module renders the digits, allows copying, and handles fallback text when the code is missing.

**Acceptance Scenarios**:

1. **Given** the server returns a valid numeric removal code, **When** the screen renders, **Then** the code displays in the gradient pill with 60px white digits and sufficient contrast (AAA against background).
2. **Given** the user taps/clicks the code area, **When** the interaction completes, **Then** the code is copied to the clipboard and a non-blocking toast/snackbar confirms the action.
3. **Given** the backend temporarily fails to provide a code, **When** the confirmation screen loads, **Then** the UI shows a fallback message ("Code unavailable. Please check your email.") in place of the digits and logs a recoverable error.

---

### User Story 3 - Exit the flow safely (Priority: P2)

As a user who has reviewed the confirmation, I can tap the Close button to return to the previous surface (home/dashboard) without leaving duplicate screens on the navigation stack.

**Why this priority**: Users need a deterministic way to continue browsing or report another animal. Residual modals create confusing back stacks.

**Independent Test**: Trigger the Close button via UI automation on each platform and verify navigation resets to the correct destination while preserving previously loaded data.

**Acceptance Scenarios**:

1. **Given** the confirmation screen is the top-most route, **When** Close is tapped, **Then** the navigation stack pops back to the Report flow entry point (Android/iOS) or navigates to `/reports/success/close` redirect (web).
2. **Given** the user presses system back (Android) or ESC (web), **When** the action occurs, **Then** it mirrors the Close behavior with no duplicate events.
3. **Given** analytics are enabled, **When** Close is used, **Then** an event `report.confirmation.dismiss` fires once with metadata `{codeLastFour, platform}`.

---

### Edge Cases

- **Missing code**: Display fallback copy and surface toast instructing users to reference the emailed code; Close remains enabled.
- **Duplicate submissions**: If server flags the report as already stored, still show confirmation but change secondary sentence to "We updated your existing report...".
- **Offline after submission**: If the app loses connectivity before receiving the code, queue a retry (up to 3 attempts, 5s interval) before showing fallback.
- **Clipboard permission denied**: Show inline helper text "Copy manually" and allow long-press text selection.
- **Accessibility**: VoiceOver/TalkBack focus order must be title → body copy → code → Close. Code should announce "Removal code 5216577, double-tap to copy."

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: After a successful report submission, all platforms MUST navigate to the Report Created screen before returning to any dashboard surface.
- **FR-002**: Header text MUST read "Report created" with Hind Regular, 32px, rgba(0,0,0,0.8) (or platform equivalent typography token).
- **FR-003**: Body copy MUST match the exact two-paragraph text from the Figma design and use 16px (#545F71) typography with 1.4 line height.
- **FR-004**: Layout MUST respect 22px horizontal padding, 24px vertical spacing between sections, and safe-area insets (status bar + home indicator).
- **FR-005**: Removal code module MUST render the provided numeric code centered inside a 10px rounded container with gradient fill (#5C33FF → #F84BA1) and white digits (Arial 60px); values come from design tokens `Gradient/Primary` and `Text/OnDark`.
- **FR-006**: Tapping/clicking the removal code MUST copy the digits to the clipboard (native share sheet acceptable on iOS) and show a transient confirmation message.
- **FR-007**: If `removalCode` is null/undefined, the UI MUST display "Code unavailable. Check your email for the reference code." and log `report.confirmation.code_missing`.
- **FR-008**: Close button MUST be full-width (327px reference), 52px tall, 10px radius, background #155DFC, white Hind 18px label, and stay anchored above the bottom safe area on scroll.
- **FR-009**: Close action MUST dismiss the confirmation and clear any transient ViewModels/state machines related to report creation to prevent duplicate submissions when returning.
- **FR-010**: Provide deterministic test identifiers: Android `Modifier.testTag("reportConfirmation")`, `reportConfirmation.code`, `reportConfirmation.close`; iOS `.accessibilityIdentifier("reportConfirmation.*")`; Web `data-testid="reportConfirmation.*"`.
- **FR-011**: Screen MUST emit analytics events `report.confirmation.viewed` (on first render) and `report.confirmation.dismiss` (on Close) with `{platform, codeLength, codeLastFour}` payload.

### Key Entities

- **ReportConfirmationViewState**
  - `title`: string ("Report created")
  - `body`: string[] (array to allow paragraph splitting)
  - `removalCode`: string | null
  - `isCopySupported`: boolean
  - `isOnline`: boolean
- **ReportConfirmationAnalyticsEvent**
  - `name`: `"report.confirmation.viewed"` | `"report.confirmation.dismiss"`
  - `platform`: `"android" | "ios" | "web"`
  - `codeLength`: number
  - `timestamp`: ISO string

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: During QA visual review, 100% of inspected builds match typography, spacing, and colors from the Figma node (tolerance ±1dp/px).
- **SC-002**: Clipboard interaction succeeds in ≥95% of tested devices/browsers; failures surface fallback messaging without crashes.
- **SC-003**: Analytics dashboards show a 1:1 ratio between report submissions and `report.confirmation.viewed` events (±2%).
- **SC-004**: Support tickets related to "lost/unknown removal code" decrease by 50% after release (measured over two weeks).
