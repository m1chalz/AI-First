# Feature Specification: Report Created Confirmation Screen

**Feature Branch**: `043-ios-report-created-screen`  
**Created**: 2025-12-03  
**Updated**: 2025-12-03  
**Status**: Updated  
**Input**: User description: "iOS Report Created Confirmation Screen (copied from spec 024)"  
**Figma Design**: [Report created screen](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8193&m=dev)  
**Platform Scope**: iOS (SwiftUI) only.

## Update Notes

### 2025-12-03 Update
- Changed terminology from "removal code" to "management password" in code/technical context only
- UI text remains unchanged from Figma design: users see "code" and "removal form" in body copy
- Data source: `@Published var managementPassword: String?` from flowState
- When managementPassword is nil, display empty string (no error message)
- No changes to navigation structure or button placement
- Removed all analytics and performance-related requirements
- Backend sends the code via email to user's email address

## Clarifications

### Session 2025-12-03

- Q: What should the Close button do - return to the Report flow entry point or dismiss the entire flow and return to home/dashboard? → A: Dismiss the entire flow and return to home/dashboard (already implemented)
- Q: What is the exact body copy text from the Figma design? → A: Paragraph 1: "Your report has been created, and your missing animal has been added to the database. If your pet is found, you will receive a notification immediately." Paragraph 2: "If you wish to remove your report from the database, use the code provided below in the removal form. This code has also been sent to your email address"
- Q: Should the UI text be updated to use "management password" terminology or keep the Figma text with "code" and "removal form"? → A: Keep Figma text unchanged - "management password" is only the technical/code variable name; users see "code" in UI
- Q: Does the backend send the management password/code via email to the user? → A: Yes, backend already sends email with the code
- Q: What is the exact toast message text when the code is copied to clipboard? → A: "Code copied to clipboard" (EN) / "Skopiowano kod do schowka" (PL)

## Design Summary

- Screen lives at the final step of the "Report a Missing Animal" flow. It is a full-screen modal/card framed inside a 375×814 reference device with 46px rounded corners and safe-area-aware top/bottom padding.
- Header text "Report created" uses Hind Regular, 32px, rgba(0,0,0,0.8) and sits 32px below the system status bar.
- Body copy is two paragraphs (16px, #545F71, Hind Regular). Paragraph 1: "Your report has been created, and your missing animal has been added to the database. If your pet is found, you will receive a notification immediately." Paragraph 2: "If you wish to remove your report from the database, use the code provided below in the removal form. This code has also been sent to your email address". Line length constrained to 325px for readability.
- Management password module centers a 6–7 digit code (`5216577` in design) inside a 328×90 px container with 10px radius. Background uses a horizontal gradient from #5C33FF to #F84BA1 plus a soft blur glow (#FB64B6 @ 20% alpha). Digits use Arial Regular, 60px, white text with -1.5px tracking. Data source: `@Published var managementPassword: String?` from flowState (nil maps to empty string).
- Primary action is a full-width button labeled "Close," 52px tall, 327px wide, rounded corners (10px), blue background #155DFC, white text 18px. Button position in view structure remains unchanged from current implementation.
- No icons, progress indicators, or secondary actions are shown. Focus is on confirmation messaging, reusable management password, and closing the flow.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Understand confirmation outcome (Priority: P1)

As a pet owner who just finished submitting a missing animal report, I can immediately see that the report succeeded along with what happens next, so that I trust the system captured my information and know to watch for notifications.

**Why this priority**: Without an explicit confirmation, users may re-submit reports or abandon the app. Clear messaging prevents duplicates and support tickets.

**Independent Test**: Launch the screen with mocked data and verify the header/body copy render exactly as specified, independent of the management password retrieval logic.

**Acceptance Scenarios**:

1. **Given** the reporting flow finishes without server errors, **When** the confirmation screen appears, **Then** the title "Report created" and both paragraphs of copy are visible with specified typography and spacing.
2. **Given** a tester triggers the confirmation via deep link or UI automation, **When** the confirmation screen loads, **Then** there is no residual loading indicator or secondary actions—only the messaging, password module, and Close button.

---

### User Story 2 - Retrieve and safeguard management password (Priority: P1)

As a pet owner, I can clearly read and copy the unique management password assigned to my report, so that I can delete or edit the report later without contacting support.

**Why this priority**: The management password is the only credential for managing the report post-submission. Losing it blocks users from closing their report.

**Independent Test**: Provide a mock management password via flowState and ensure the module renders the digits, allows copying, and handles empty string when the password is nil.

**Acceptance Scenarios**:

1. **Given** the flowState contains a non-nil managementPassword, **When** the screen renders, **Then** the password displays in the gradient pill with 60px white digits and sufficient contrast (AAA against background).
2. **Given** the user taps the password area, **When** the interaction completes, **Then** the password is copied to the clipboard and a toast confirms "Code copied to clipboard" (EN) / "Skopiowano kod do schowka" (PL).
3. **Given** the flowState has managementPassword set to nil, **When** the confirmation screen loads, **Then** the UI shows an empty string in place of the digits (gradient pill remains visible but empty).

---

### User Story 3 - Exit the flow safely (Priority: P2)

As a user who has reviewed the confirmation, I can tap the Close button to return to the previous surface (home/dashboard) without leaving duplicate screens on the navigation stack.

**Why this priority**: Users need a deterministic way to continue browsing or report another animal. Residual modals create confusing back stacks.

**Independent Test**: Trigger the Close button via UI automation and verify navigation resets to the correct destination while preserving previously loaded data.

**Acceptance Scenarios**:

1. **Given** the confirmation screen is the top-most route, **When** Close is tapped, **Then** the entire flow is dismissed and the app returns to home/dashboard.

---

### Edge Cases

- **Missing password (nil)**: Display empty string in gradient pill; Close remains enabled.
- **Duplicate submissions**: If server flags the report as already stored, still show confirmation but change secondary sentence to "We updated your existing report...".
- **Clipboard permission denied**: Show inline helper text "Copy manually" and allow long-press text selection.
- **Accessibility**: VoiceOver focus order must be title → body copy → password → Close. Password should announce "Management password [digits], double-tap to copy" or "Management password empty" when nil.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: After a successful report submission, iOS MUST navigate to the Report Created screen before returning to any dashboard surface. *(Already implemented - flow exists)*
- **FR-002**: Header text MUST read "Report created" with Hind Regular, 32px, rgba(0,0,0,0.8) (or platform equivalent typography token).
- **FR-003**: Body copy MUST display the exact two-paragraph text: Paragraph 1 "Your report has been created, and your missing animal has been added to the database. If your pet is found, you will receive a notification immediately." Paragraph 2 "If you wish to remove your report from the database, use the code provided below in the removal form. This code has also been sent to your email address". Typography: 16px (#545F71) with 1.4 line height.
- **FR-004**: Layout MUST respect 22px horizontal padding, 24px vertical spacing between sections, and safe-area insets (status bar + home indicator).
- **FR-005**: Management password module MUST render the password from `flowState.managementPassword` centered inside a 10px rounded container with gradient fill (#5C33FF → #F84BA1) and white digits (Arial 60px); if managementPassword is nil, display empty string; values come from design tokens `Gradient/Primary` and `Text/OnDark`.
- **FR-006**: Tapping the management password MUST copy the digits to the clipboard and show a transient confirmation toast with message "Code copied to clipboard" (EN) / "Skopiowano kod do schowka" (PL). *(Reuse existing shared toast component)*
- **FR-007**: If `flowState.managementPassword` is nil, the UI MUST display empty string in the gradient pill (no fallback message required).
- **FR-008**: Close button MUST be full-width (327px reference), 52px tall, 10px radius, background #155DFC, white Hind 18px label, and stay anchored above the bottom safe area on scroll.
- **FR-009**: Close action MUST dismiss the confirmation and clear any transient ViewModels/state machines related to report creation to prevent duplicate submissions when returning. *(Already implemented - flow cleanup exists)*
- **FR-010**: Provide deterministic test identifiers: iOS `.accessibilityIdentifier("reportConfirmation.*")` including `reportConfirmation.password` and `reportConfirmation.close`.

### Key Entities

- **ReportConfirmationFlowState** (ViewModel property)
  - `@Published var managementPassword: String?` - management password for the report (nil maps to empty string in UI)

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: During QA visual review, 100% of inspected builds match typography, spacing, and colors from the Figma node (tolerance ±1dp/px).
- **SC-002**: Clipboard interaction succeeds without crashes; failures surface fallback messaging.
- **SC-003**: Support tickets related to "lost/unknown management password" decrease after release.
