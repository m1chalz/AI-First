# Design Reference: Owner's Details Screen (Step 4/4)

## Figma Design

**URL**: https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=297-8113&m=dev

**Node ID**: 297-8113

**Reference Frame**: "ReportAMissingAnimal Step 5" (Owner's details screen - Step 4/4 of Missing Pet flow)

## Design Overview

The Owner's Details screen is Step 4/4 in the Missing Pet flow (spec 017). It collects contact information (phone, email) and an optional reward description before finalizing the report submission. The design follows iOS conventions with a clean, vertical form layout.

### Header Section
- **Navigation Bar**:
  - Left: Circular back button (40x40px, border color #E5E9EC)
  - Center: Screen title "Owner's details" (Inter Regular 14px, #2D2D2D)
  - Right: Progress indicator badge showing "4/4" (40x40px circular badge)
- **iOS Status Bar**: Standard device status bar with time, signal, battery

### Main Content Section

The content is contained in a white container with 22px horizontal padding:

1. **Screen Title**
   - Text: "Your contact info"
   - Typography: Inter Regular 32px, rgba(0,0,0,0.8)
   - Position: Below navigation, with 24px top spacing

2. **Helper Text**
   - Text: "Add your contact your contact information's and potential reward."
   - Typography: Hind Regular 16px, #545F71
   - Note: Copy contains grammatical error in Figma ("your contact" repeated) - should be corrected in implementation

3. **Form Inputs** (Vertical stack with 16px gap)

   **a) Phone Number Input** (Mandatory)
   - Label: "Phone number" (Hind Regular 16px, #364153)
   - Input field:
     - Placeholder: "Enter phone number..."
     - Height: 41.323px
     - Border: 0.667px solid #D1D5DC
     - Border radius: 10px
     - Padding: 16px horizontal
     - Text: Hind Regular 16px, rgba(10,10,10,0.5) for placeholder
   - Gap between label and input: 8px

   **b) Email Input** (Mandatory)
   - Label: "Email" (Hind Regular 16px, #364153)
   - Input field:
     - Placeholder: "username@example.com"
     - Height: 41.323px
     - Border: 0.667px solid #D1D5DC
     - Border radius: 10px
     - Padding: 16px horizontal
     - Text: Hind Regular 16px, rgba(10,10,10,0.5) for placeholder
   - Gap between label and input: 8px

   **c) Reward Input** (Optional)
   - Label: "Reward for the finder(optional)" (Hind Regular 16px, #364153)
   - Input field:
     - Placeholder: "Enter amount..."
     - Height: 41.323px
     - Border: 0.667px solid #D1D5DC
     - Border radius: 10px
     - Padding: 16px horizontal
     - Text: Hind Regular 16px, rgba(10,10,10,0.5) for placeholder
   - Gap between label and input: 8px
   - Note: Label shows "(optional)" inline with "Reward for the finder"

### Action Button Section

- **Continue Button**:
  - Position: Fixed at bottom, 22px from left/right edges
  - Width: 327px (full width with container padding)
  - Height: 52px
  - Background: #155DFC (Primary Blue)
  - Text: "Continue" (Hind Regular 18px, white)
  - Border radius: 10px
  - Padding: 12px vertical, 24px horizontal
  - State: Disabled until both phone and email are valid

## Design System Notes

### Typography

- **Title**: Inter Regular 32px, rgba(0,0,0,0.8)
- **Subtitle/Helper**: Hind Regular 16px, #545F71
- **Labels**: Hind Regular 16px, #364153
- **Input Text**: Hind Regular 16px, #0A0A0A
- **Placeholders**: Hind Regular 16px, rgba(10,10,10,0.5)
- **Button Text**: Hind Regular 18px, white
- **Navigation Title**: Inter Regular 14px, #2D2D2D
- **Progress Badge**: Plus Jakarta Sans Bold 11.667px, #2D2D2D

### Colors

- **Primary Blue**: #155DFC (Continue button)
- **Text Colors**:
  - Title: rgba(0,0,0,0.8)
  - Subtitle: #545F71
  - Labels: #364153
  - Input text: #0A0A0A
  - Placeholder: rgba(10,10,10,0.5)
  - Navigation: #2D2D2D
- **Border Colors**:
  - Input border: #D1D5DC
  - Container border: #E8ECF0
  - Back button border: #E5E9EC
- **Background**: White (#FFFFFF)
- **Error State** (not shown in design, per spec 023):
  - Error text: Red (design system red, likely #FB2C36)
  - Error border: Red

### Spacing

- **Container Padding**: 22px horizontal
- **Section Gaps**: 24px between major sections
- **Input Stack Gap**: 16px between input groups
- **Label-to-Input Gap**: 8px (32px in design coordinates from label top to input top)
- **Input Height**: 41.323px (~41px)
- **Input Horizontal Padding**: 16px
- **Button Padding**: 12px vertical, 24px horizontal
- **Border Width**: 0.667px (~0.7px, render as 1px on non-retina or 0.5-1 UIKit/SwiftUI)
- **Border Radius**: 
  - Inputs & Button: 10px
  - Back button: 112px (circular)
  - Container: 46px

### Layout Dimensions

- **Screen Width**: 375px (iPhone standard)
- **Content Width**: 328px (375 - 22 - 22 = 331, design shows 328)
- **Back Button**: 40x40px circular
- **Progress Badge**: 40x40px circular
- **Continue Button**: 327x52px
- **Input Fields**: 328px width, 41.323px height

### Icons & Graphics

- **Back Arrow**: Left-pointing chevron/arrow icon in circular button
- **Progress Badge**: Circular badge with "4/4" text and progress ring visual

## Validation & Error States

Per specification 023, the following validation states must be implemented (not all visible in Figma):

### Input Validation

1. **Phone Number**:
   - Accept digits and leading "+"
   - Enforce 7-11 digits
   - Show inline error: "Enter at least 7 digits" when invalid
   - Error styling: Red border + red text

2. **Email**:
   - Validate RFC 5322 format (basic local@domain.tld)
   - Case-insensitive
   - Show inline error when invalid
   - Error styling: Red border + red text

3. **Reward** (Optional):
   - Max 120 characters
   - Show warning: "Keep reward details under 120 characters" when exceeded
   - No error styling (optional field)

### Continue Button States

- **Disabled State**: Gray/muted appearance when phone OR email invalid
- **Enabled State**: Full blue (#155DFC) when both required fields valid
- **Loading State** (offline/submission): May show loading indicator

### Offline State

When Continue tapped offline:
- Stay on screen
- Show inline message: "No connection. Try again"
- Keep all inputs intact
- Re-enable Continue once connectivity returns

### Helper Text

Below input stack (when validation blocks Continue):
- "Provide a valid phone number and email address."
- Typography: Hind Regular 16px, likely error red or gray

## Implementation Notes for iOS

### Technical Requirements

- **Framework**: SwiftUI + UIKit Coordinator (MVVM-C architecture from spec 017)
- **iOS Version**: iOS 15+ (align with project baseline)
- **Navigation**: Coordinator-managed (ReportMissingPetCoordinator)
- **Session Management**: ReportMissingPetFlowState stores contact data

### Component Structure

Suggested SwiftUI component breakdown:

1. **OwnersDetailsView** (Main container)
   - NavigationBar (title, back button, progress)
   - ScrollView (handles keyboard dismissal)
   - Form content (title, subtitle, inputs)
   - Continue button (fixed at bottom)

2. **Reusable Components** (from existing codebase if available):
   - **ValidatedTextField**: Text input with label, placeholder, validation state
   - **PrimaryButton**: Blue CTA button with enabled/disabled/loading states
   - **ProgressBadge**: Circular badge with step indicator
   - **ToastView**: Temporary message overlay for "Photo is mandatory" style alerts

3. **ViewModel**: OwnersDetailsViewModel
   - @Published properties: phone, email, rewardDescription
   - Validation logic: isPhoneValid, isEmailValid, canContinue
   - Actions: validatePhone(), validateEmail(), submitForm()
   - Navigation callback to coordinator

### Accessibility

- All interactive elements MUST have accessibility identifiers:
  - `ownersDetails.back.button`
  - `ownersDetails.phoneNumber.input`
  - `ownersDetails.email.input`
  - `ownersDetails.reward.input`
  - `ownersDetails.continue.tap`
  - `ownersDetails.progress.badge`
- Labels should expose descriptive accessibility labels (e.g., "Phone number, required field")
- Error messages should announce to VoiceOver when validation fails
- Continue button should announce disabled state

### State Management

- Inputs sync with `ReportMissingPetFlowState` (spec 017)
- Validation states live in ViewModel
- Navigation events communicated to ReportMissingPetCoordinator
- Analytics event: `missing_pet.step4_completed` on successful Continue

### Backend Integration

Per spec 023, Step 4 finalizes the report:
- On Continue tap (when valid), submit full payload to backend
- API call: POST /api/announcements (or similar endpoint from spec 009/021)
- Trigger confirmation email to owner's email address
- On success: Navigate to summary screen (Step 5)
- On failure: Show inline error, keep user on Step 4, allow retry

### Design Considerations

1. **Copy Correction**: Fix grammatical error in helper text from Figma
2. **Typography Fallback**: Use SF Pro/System font if Hind/Inter not available
3. **Border Rendering**: 0.667px borders may render as 1px on standard displays
4. **Keyboard Handling**: Inputs should scroll when keyboard appears
5. **Safe Area**: Respect iPhone safe area (notch, home indicator)
6. **Dark Mode**: Not specified - follow project conventions (likely not in scope)

### Testing

- Unit tests: Validation logic in ViewModel (80% coverage requirement)
- UI tests: Input validation, Continue button states, navigation flow
- E2E tests: Complete Missing Pet flow from Step 1 to Step 5
- Test identifiers: Use `ownersDetails.*` convention

## Related Specifications

- **Spec 017**: Missing Pet flow architecture and session management
- **Spec 023**: Functional requirements and acceptance criteria
- **Spec 009**: Backend API for announcements/reports
- **Spec 021**: Photo upload (Step 2)
- **Spec 028**: iOS Animal Photo screen (reference implementation)
- **Spec 031**: iOS Animal Description screen (reference implementation)

## Notes

- Progress badge shows "4/4" indicating this is the final data collection step
- Summary screen (Step 5) intentionally hides the progress badge per spec 017
- Reward field accepts free-text (e.g., "$250 gift card + hugs") - no structured amount/currency
- Backend submission happens on Step 4 Continue, not deferred to Step 5
- Offline handling: Keep user on Step 4 with retry messaging until submission succeeds

