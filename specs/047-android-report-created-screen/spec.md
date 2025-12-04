# Feature Specification: Android Report Created Confirmation Screen

**Feature Branch**: `047-android-report-created-screen`  
**Created**: 2025-12-04  
**Status**: Draft  
**Platform**: Android  
**Input**: User description: "Based on generic spec 024 and iOS spec 044, prepare the spec for Android Report Created Confirmation screen (final step in Missing Pet flow)"

This feature defines the **Android Report Created Confirmation Screen** (final step of the Missing Pet flow defined in specification 018 for Android).  
The Android UI MUST match Figma node `297-8193` from the [PetSpot wireframes design](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8193), adapted to Android Material Design patterns and Jetpack Compose implementation.

**Design References**:
- **Main screen**: Figma node `297-8193` - [Report created screen](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8193&m=dev)

Key visual specifications adapted to Android:
- Title color: `rgba(0,0,0,0.8)` 
- Body text color: `#545F71`
- Gradient colors: `#5C33FF` → `#F84BA1` (for management password container)
- Glow color: `#FB64B6` @ 20% alpha
- Primary blue: `#155DFC` (Close button)
- Typography: Adapt Hind Regular 32dp (title), Hind Regular 16dp (body), Arial Regular 60sp (password digits) to Material equivalents
- Password container: 10dp radius, gradient background with soft blur glow effect
- Close button: 327dp width, 52dp height, 10dp radius, full-width blue button
- Horizontal padding: 22dp, Vertical spacing: 24dp

**Parent Specifications**:
- [024-report-created-screen](https://github.com/[repo]/tree/024-report-created-screen) - Cross-platform Report Created Confirmation requirements
- [044-ios-report-created-screen](../044-ios-report-created-screen) - iOS implementation reference

## Clarifications

### Session 2025-11-26 (from cross-platform spec 024, adapted to Android scope)

- Q: Which analytics payload should accompany confirmation events? → A: Analytics events (`report.confirmation.viewed`, `report.confirmation.dismiss`) are deferred to a future iteration for Android. No analytics for initial release.
- Q: What should the Close button do - return to the Report flow entry point or dismiss the entire flow and return to home/dashboard? → A: Dismiss the entire flow and return to home/dashboard (pet list screen).
- Q: What is the exact body copy text from the Figma design? → A: Paragraph 1: "Your report has been created, and your missing animal has been added to the database. If your pet is found, you will receive a notification immediately." Paragraph 2: "If you wish to remove your report from the database, use the code provided below in the removal form. This code has also been sent to your email address"

### Session 2025-12-04 (from iOS spec 044 updates)

- Q: Should the UI text be updated to use "management password" terminology or keep the Figma text with "code" and "removal form"? → A: Keep Figma text unchanged - "management password" is only the technical/code variable name; users see "code" in UI.
- Q: Does the backend send the management password/code via email to the user? → A: Yes, backend already sends email with the code.
- Q: What is the exact Snackbar message text when the code is copied to clipboard? → A: "Code copied to clipboard"

### Session 2025-12-04 (Android-specific clarifications)

- Q: What is the minimum Android SDK version for this feature? → A: API 26 (Android 8.0 Oreo) minimum, targeting API 36 (latest), consistent with project configuration.
- Q: What accessibility requirements apply to this screen? → A: Only testTag modifiers for automated testing (no TalkBack/accessibility announcements in scope for initial release).
- Q: How should the gradient background be implemented for the password container? → A: Use Brush.horizontalGradient with Color(0xFF5C33FF) to Color(0xFFF84BA1), apply soft shadow/glow effect via graphicsLayer or shadow modifier.
- Q: Should the screen support device rotation? → A: Yes, the UI must adapt to landscape orientation without content clipping; ViewModel state survives rotation.
- Q: How should the managementPassword be sourced? → A: From `ReportMissingFlowState.managementPassword` property (set by OwnerDetailsViewModel after successful announcement submission from spec 045).
- Q: Should there be a back button in the TopAppBar? → A: No back button. Only a Close button at the bottom to exit the flow completely.
- Q: Should analytics events be emitted on this screen? → A: No analytics events for this screen; analytics will be added in a future iteration if needed.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Understand confirmation outcome (Priority: P1)

Android users who successfully submit a missing animal report via the Owner's Details screen (spec 045) are navigated to the Report Created Confirmation screen. They can immediately see that the report succeeded along with what happens next, building trust that the system captured their information and prompting them to watch for notifications.

**Why this priority**: Without an explicit confirmation, users may re-submit reports or abandon the app. Clear messaging prevents duplicates and support tickets.

**Independent Test**: Launch the screen with mocked flowState data and verify the header/body copy render exactly as specified, independent of the management password display logic.

**Acceptance Scenarios**:

1. **Given** the reporting flow finishes successfully (both announcement creation and photo upload succeed), **When** the confirmation screen appears, **Then** the title "Report created" and both paragraphs of copy are visible with specified typography and spacing.
2. **Given** a tester triggers the confirmation via navigation from OwnerDetailsViewModel.NavigateToSummary effect, **When** the confirmation screen loads, **Then** there is no loading indicator or back button—only the messaging, password module, and Close button.

---

### User Story 2 - Retrieve and safeguard management password (Priority: P1)

Android users can clearly read and copy the unique management password (displayed as "code" in UI) assigned to their report, so they can delete or edit the report later without contacting support.

**Why this priority**: The management password is the only credential for managing the report post-submission. Losing it blocks users from closing their report.

**Independent Test**: Provide a mock management password via flowState and ensure the module renders the digits, allows copying via tap/long-press, and handles empty/null state gracefully.

**Acceptance Scenarios**:

1. **Given** the flowState contains a non-null managementPassword (e.g., "5216577"), **When** the screen renders, **Then** the password displays in the gradient container with 60sp white digits and sufficient contrast against the gradient background.
2. **Given** the user taps the password container, **When** the interaction completes, **Then** the password is copied to the clipboard and a Snackbar confirms "Code copied to clipboard".
3. **Given** the flowState has managementPassword set to null or empty, **When** the confirmation screen loads, **Then** the UI shows an empty string in place of the digits (gradient container remains visible but empty); no crash occurs.

---

### User Story 3 - Exit the flow safely (Priority: P2)

Android users who have reviewed the confirmation can exit the flow via Close button OR system back navigation. Both actions return the user to the pet list screen, clear the flow state, and leave no residual navigation stack entries. This is a terminal screen with no back navigation to previous flow steps.

**Why this priority**: Users need a deterministic way to continue browsing or report another animal. Residual screens/state create confusing navigation and potential data corruption. System back must be handled explicitly to prevent accidental navigation to previous flow steps after submission.

**Independent Test**: 
1. Trigger the Close button via UI automation and verify navigation resets to pet list screen
2. Trigger system back gesture/button and verify identical behavior to Close button
3. Verify flow state is cleared and no back-stack entries remain from the report flow

**Acceptance Scenarios**:

1. **Given** the confirmation screen is displayed, **When** the user taps the Close button, **Then** the entire Missing Pet flow is dismissed, user returns to pet list screen, and flow state is cleared.
2. **Given** the confirmation screen is displayed, **When** the user presses the system back button, **Then** the entire Missing Pet flow is dismissed (same as Close), user returns to pet list screen, and flow state is cleared - NO navigation to Owner's Details or previous flow screens.
3. **Given** the confirmation screen is displayed, **When** the user performs the system back gesture (swipe from edge), **Then** the behavior is identical to pressing the system back button - flow is dismissed, not navigated backward within.

---

### Edge Cases

- **Missing password (null/empty)**: Display empty string in gradient container; Close remains enabled; no error message displayed.
- **Clipboard permission**: Android clipboard operations don't require runtime permission; copy should always succeed.
- **Clipboard confirmation**: Show Snackbar at bottom of screen with message "Code copied to clipboard" for 2 seconds.
- **Configuration changes**: ViewModel state survives device rotation; UI adapts to landscape orientation without clipping.
- **Process death**: After process death, if user returns to this screen without valid flowState, navigate back to pet list (flow is complete anyway).
- **Deep link access**: This screen should NOT be accessible via deep link; it's only reachable through successful flow completion.

## Requirements *(mandatory)*

### Dependencies

- **Spec 018** (018-android-missing-pet-flow): Provides navigation scaffolding (nested Navigation Graph infrastructure), shared flow ViewModel, and the Summary route placeholder. This spec implements the actual Summary screen content.
- **Spec 045** (045-android-owners-details-screen): Provides the preceding screen (Owner's Details) which navigates to this screen on successful submission, passing the managementPassword via NavigateToSummary effect.

### Functional Requirements

- **FR-001**: The Report Created Confirmation screen MUST render as the final step of the Missing Pet flow (spec 018) and visually match Figma node 297-8193 including header "Report created", two body paragraphs, gradient password container, and blue Close button adapted to Material Design 3.
- **FR-002**: Header text MUST read "Report created" with typography matching Hind Regular 32dp (or Material equivalent), color rgba(0,0,0,0.8), positioned 32dp below the status bar.
- **FR-003**: Body copy MUST display exact two-paragraph text: Paragraph 1 "Your report has been created, and your missing animal has been added to the database. If your pet is found, you will receive a notification immediately." Paragraph 2 "If you wish to remove your report from the database, use the code provided below in the removal form. This code has also been sent to your email address". Typography: 16dp, color #545F71, line height 1.4.
- **FR-004**: Layout MUST respect 22dp horizontal padding, 24dp vertical spacing between sections, and system bar insets (status bar + navigation bar).
- **FR-005**: Management password container MUST render the password from `flowState.managementPassword` centered inside a 10dp rounded container with horizontal gradient fill (#5C33FF → #F84BA1), soft glow overlay (#FB64B6 @ 20% alpha), and white digits (60sp, -1.5sp letter spacing); if managementPassword is null/empty, display empty string.
- **FR-006**: Tapping the password container MUST copy the password text to the system clipboard via ClipboardManager and show a Snackbar with message "Code copied to clipboard" for approximately 2 seconds.
- **FR-007**: If `flowState.managementPassword` is null or empty, the UI MUST display empty string in the gradient container (container remains visible, no fallback error message).
- **FR-008**: Close button MUST be full-width (327dp reference, or match parent with padding), 52dp tall, 10dp radius, background #155DFC, white text 18dp, positioned above the bottom navigation bar inset.
- **FR-009**: Close action MUST dismiss the confirmation screen and entire Missing Pet flow, navigate to pet list screen, and clear the ReportMissingFlowState to prevent stale data on next flow entry.
- **FR-010**: System back button/gesture MUST behave identically to Close button (dismiss flow, clear state, return to pet list) - no back navigation to previous flow steps is allowed from this screen.
- **FR-011**: TopAppBar MUST NOT include a navigation icon (back arrow) - this is a terminal screen with only the Close button for exiting.
- **FR-012**: All interactive Composables MUST have Modifier.testTag() attributes following the `{screen}.{element}` naming convention for automated testing.
- **FR-013**: ViewModel MUST follow MVI pattern with single StateFlow<UiState>, sealed UserIntent, and SharedFlow<UiEffect> as mandated by project architecture.
- **FR-014**: Screen layout MUST adapt correctly to both portrait and landscape orientations, using scrollable content if needed, without content clipping or overlap.

### Key Entities *(include if feature involves data)*

- **ReportMissingFlowState** (existing): Flow state container (managed by NavGraph-scoped ViewModel) containing `managementPassword: String?` property set by OwnerDetailsViewModel after successful announcement submission.
- **SummaryUiState**: Immutable data class representing the current UI state of the Summary screen. Contains `managementPassword: String` (empty string if null in flowState), `isPasswordCopied: Boolean` for UI feedback.
- **SummaryUserIntent**: Sealed class representing all possible user actions on this screen (CopyPasswordClicked, CloseClicked).
- **SummaryUiEffect**: Sealed class for one-off events (ShowSnackbar(message: String), DismissFlow).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: During QA visual review, 100% of inspected builds match typography, spacing, colors, and gradient from the Figma node (tolerance ±1dp).
- **SC-002**: Clipboard copy interaction succeeds without crashes on all tested Android API levels (26-36); Snackbar confirmation displays correctly.
- **SC-003**: Unit tests for ViewModel achieve 80% line and branch coverage (mandated by project rules).
- **SC-004**: 100% of Close button taps dismiss the flow, clear state, and return to pet list screen; no orphaned navigation stack entries remain.
- **SC-005**: Screen layout renders correctly without content clipping on common Android device sizes (phone portrait/landscape, tablet) - verified during QA.
- **SC-006**: 0% of back button/gesture interactions navigate to previous flow steps; all exit the flow entirely.

## Assumptions

- `ReportMissingFlowState` already contains `managementPassword: String?` property (added by spec 045 implementation).
- Summary screen route already exists in ReportMissingNavGraph as placeholder (from spec 018) - this implementation replaces placeholder content.
- Backend has already sent confirmation email by the time this screen displays (email sent async after POST /api/v1/announcements in spec 045).
- Password is typically 6-7 digits as shown in Figma design ("5216577").
- This feature targets Android API 26 (Android 8.0 Oreo) minimum, targeting API 36 (latest), consistent with project-wide configuration.
- Implementation will use Jetpack Compose for UI.
- Koin dependency injection is mandatory per project architecture.
- ClipboardManager is available via LocalContext in Compose and doesn't require runtime permissions.

## Dependencies

- Navigation scaffolding and flow state container from specification 018 (Missing Pet flow): Nested NavGraph and shared ViewModel.
- Owner's Details screen (Step 4) from specification 045 - navigation source that passes managementPassword.
- Design tokens (colors, typography) defined in `.specify/memory/constitution.md`.

## Out of Scope

- Backend changes (backend already complete from specs 009, 021).
- iOS/Web implementations (handled by their platform-specific specs - iOS is spec 044).
- Edit/delete report functionality (requires separate spec for report management screens).
- Custom share functionality (user can manually share the code if needed).
- Multiple language support beyond English (future iteration).
- TalkBack/accessibility announcements (only testTag for automated testing is in scope).
- Analytics events (e.g., `missing_pet.confirmation_viewed`) - deferred to future iteration.
- Animated transitions for the gradient glow effect (static glow is sufficient).

## Technical Architecture Notes

### Jetpack Compose Structure

**File to Create/Modify**: `SummaryContent.kt` (replace placeholder from spec 018)

```
SummaryContent (Composable)
├── Column (fillMaxSize, verticalArrangement, horizontalPadding=22dp)
│   ├── Spacer (statusBarsPadding + 32dp)
│   ├── Text: "Report created" (title)
│   │   └── style: headlineSmall, color: rgba(0,0,0,0.8)
│   ├── Spacer (24dp)
│   ├── Text: Body paragraph 1
│   │   └── style: bodyMedium, color: #545F71, lineHeight: 1.4
│   ├── Spacer (16dp)
│   ├── Text: Body paragraph 2
│   │   └── style: bodyMedium, color: #545F71, lineHeight: 1.4
│   ├── Spacer (32dp)
│   ├── PasswordContainer (clickable)
│   │   ├── Box with gradient background (#5C33FF → #F84BA1)
│   │   ├── Glow overlay (#FB64B6 @ 20% alpha, blur)
│   │   └── Text: password digits (60sp, white, -1.5sp tracking)
│   ├── Spacer (weight=1f - pushes Close button to bottom)
│   └── Button: Close (full width, above navigationBarsPadding)
│       └── style: 327dp width, 52dp height, 10dp radius, #155DFC
└── SnackbarHost (for clipboard confirmation)
```

### ViewModel Responsibilities

**File to Create**: `SummaryViewModel.kt`

- Manage StateFlow<SummaryUiState> with password display state
- Read managementPassword from ReportMissingFlowState on init
- Process UserIntent via reducer pattern:
  - CopyPasswordClicked → copy to clipboard, emit ShowSnackbar effect
  - CloseClicked → clear flow state, emit DismissFlow effect
- Expose `displayPassword: String` computed from flowState (empty string if null)

### Test Identifiers

- `summary.title`
- `summary.bodyParagraph1`
- `summary.bodyParagraph2`
- `summary.passwordContainer`
- `summary.passwordText`
- `summary.closeButton`
- `summary.snackbar`

### Test Coverage Requirements

- **Unit Tests** (ViewModel): 80% coverage
  - Password display logic (null → empty string, non-null → value)
  - Clipboard copy logic (ClipboardManager interaction)
  - Close button logic (flow state clearing, navigation effect)
  - UserIntent processing via reducer
- **UI Tests**: 
  - Title and body paragraphs render correctly
  - Password displays in gradient container
  - Tap password → clipboard copy + Snackbar
  - Close button dismisses flow
  - System back dismisses flow
- **Integration Tests**:
  - Full flow: Owner's Details → Submit → Summary screen with password
  - Flow state cleared after Close

## Related Specifications

- **[Spec 024](https://github.com/[repo]/tree/024-report-created-screen)**: Cross-platform Report Created Confirmation requirements (parent spec)
- **[Spec 018](../018-android-missing-pet-flow/)**: Missing Pet flow architecture (Android)
- **[Spec 044](../044-ios-report-created-screen/)**: iOS Report Created screen reference
- **[Spec 045](../045-android-owners-details-screen/)**: Android Owner's Details screen (Step 4) - navigation source
- **[Spec 042](../042-android-animal-description-screen/)**: Android Animal Description screen (Step 3) - MVI pattern reference
- **[Spec 040](../040-android-animal-photo-screen/)**: Android Animal Photo screen (Step 2) - reference implementation
- **[Spec 038](../038-android-chip-number-screen/)**: Android Chip Number screen (Step 1) - reference implementation
