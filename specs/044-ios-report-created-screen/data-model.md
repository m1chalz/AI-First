# Data Model: Report Created Confirmation Screen

**Feature**: iOS Report Created Confirmation Screen  
**Phase**: 1 (Design & Contracts)  
**Date**: 2025-12-03

## Overview

This feature **updates existing `SummaryView`/`SummaryViewModel`** (currently placeholder) with report confirmation UI. The screen displays data from the existing `ReportMissingPetFlowState`, which will be extended with a new `managementPassword` property.

## Domain Models (Updates to Existing)

### ReportMissingPetFlowState (UPDATE - Add New Property)

**Location**: `/iosApp/iosApp/Features/ReportMissingPet/Models/ReportMissingPetFlowState.swift`

**Description**: Existing flow state container for "Report Missing Pet" flow. Currently holds all report data (chip number, photo, animal details, contact info). **Needs new property added** for management password.

**New Property to Add**:
```swift
// MARK: - Step 5: Summary (Report Created)

/// Management password returned by backend after successful report submission.
/// Used for report management (edit/delete). Also sent to user's email.
/// Nil before submission completes.
@Published var managementPassword: String?
```

**Existing Properties** (no changes):
- `chipNumber`, `photoAttachment`, `disappearanceDate`, `animalSpecies`, etc.
- All properties from steps 1-4 remain unchanged

**Update Required in `clear()` method**:
```swift
func clear() async {
    // ... existing clears ...
    managementPassword = nil  // ADD THIS LINE
    
    try? await photoAttachmentCache.clearCurrent()
}
```

**Usage in SummaryView** (via SummaryViewModel):
- **nil value**: ViewModel returns empty string via `displayPassword` computed property
- **non-nil value**: ViewModel returns password digits via `displayPassword` computed property
- View never accesses flowState directly - always through ViewModel

**Data Source**: Backend API response during report submission (already implemented - backend sends password via email per spec Update Notes 2025-12-03)

---

## Design Constants (New)

### SummaryView.Constants (New)

**Location**: `/iosApp/iosApp/Features/ReportMissingPet/Views/Summary/SummaryView+Constants.swift`

**Description**: Design constants extension for `SummaryView` holding Figma design specifications (colors, sizes, spacing).

**Purpose**: Encapsulate Figma design specifications as named constants rather than magic numbers in SwiftUI view. Separates design values from business logic (which lives in `SummaryViewModel`).

**Definition**:
```swift
extension SummaryView {
    struct Constants {
        // Design tokens from Figma
        let gradientStartColor: Color = Color(hex: "#5C33FF")
        let gradientEndColor: Color = Color(hex: "#F84BA1")
        let glowColor: Color = Color(hex: "#FB64B6")
        let glowOpacity: Double = 0.2
        let glowBlurRadius: CGFloat = 20
        
        let titleColor: Color = Color(hex: "#CC000000")  // 80% black (ARGB format: CC = 80% opacity)
        let bodyColor: Color = Color(hex: "#545F71")
        let passwordBackgroundCornerRadius: CGFloat = 10
        let buttonBackgroundColor: Color = Color(hex: "#155DFC")
        let buttonCornerRadius: CGFloat = 10
        
        // Typography
        let titleFont: Font = .system(size: 32, weight: .regular)
        let bodyFont: Font = .system(size: 16, weight: .regular)
        let bodyLineSpacing: CGFloat = 6.4  // 16px * 0.4 for 1.4 line height
        let passwordFont: Font = .custom("Arial", size: 60)
        let passwordKerning: CGFloat = -1.5
        let buttonFont: Font = .system(size: 18, weight: .semibold)
        
        // Spacing (from FR-004)
        let horizontalPadding: CGFloat = 22
        let verticalSpacing: CGFloat = 24
        let topSafeAreaInset: CGFloat = 32
        let bottomSafeAreaInset: CGFloat = 16
        
        // Dimensions
        let passwordContainerWidth: CGFloat = 328
        let passwordContainerHeight: CGFloat = 90
        let buttonWidth: CGFloat = 327
        let buttonHeight: CGFloat = 52
    }
}
```

**Usage**:
```swift
struct SummaryView: View {
    private let constants = Constants()
    @ObservedObject var viewModel: SummaryViewModel
    
    var body: some View {
        VStack(spacing: constants.verticalSpacing) {
            Text(L10n.reportCreatedTitle)
                .font(constants.titleFont)  // Directly use Font
                .foregroundColor(constants.titleColor)  // Directly use Color
            // ...
        }
    }
}
```

**Rationale**:
- Separates design constants from view logic and business logic (ViewModel)
- Makes Figma specs explicit and auditable (single source of truth for design values)
- Enables easy design updates without touching view or ViewModel logic
- Uses SwiftUI types directly (`Color`, `Font`) because Constants is already in presentation layer (extension of SwiftUI View)
  - Hex strings / raw sizes are only needed when values come from ViewModel/Model (framework separation)
  - Here: static values defined in View extension → can use SwiftUI types directly
  - Usage: `.font(constants.titleFont)` instead of `.font(.system(size: constants.titleFontSize, weight: .regular))`
- Clear naming: Constants = static design values, ViewModel = dynamic business logic

---

## State Transitions

### Flow State Machine

```
[Report Submission Complete]
           ↓
    (Backend API Response)
           ↓
   flowState.managementPassword = "5216577" (example)
           ↓
   [Show SummaryView - already in flow]
           ↓
   [Display password + confirmation messaging]
           ↓
   [User taps Close] → Dismiss flow → Return to home/dashboard
```

**States**:
1. **Presenting**: Screen visible, displaying management password (or empty string if nil)
2. **Copying**: User tapped password, clipboard operation in progress
3. **Toast Shown**: Toast confirmation "Code copied to clipboard" displayed
4. **Dismissing**: User tapped Close, flow being dismissed

**No State Persistence**: This is a transient confirmation screen. State is held temporarily in `flowState` during the flow and is cleared when the flow is dismissed (per FR-009 - coordinator clears transient state machines).

---

## Validation Rules

### Management Password Display

**Rule 1**: If `managementPassword` is nil, display empty string (no error message).

**Implementation** (in SummaryViewModel):
```swift
var displayPassword: String {
    flowState.managementPassword ?? ""
}
```

**Usage in View**:
```swift
Text(viewModel.displayPassword)  // ViewModel handles nil → empty string
```

**Rationale**: Per spec FR-007, nil maps to empty string with no fallback message required. Backend guarantees password generation, but defensive nil handling prevents crashes. **ViewModel makes this decision, not View** - View only displays what ViewModel provides.

---

**Rule 2**: Password must be displayed as-is (no formatting, no masking).

**Implementation** (ViewModel passes through unchanged):
```swift
var displayPassword: String {
    flowState.managementPassword ?? ""  // No formatting
}
```

**Usage in View**:
```swift
Text(viewModel.displayPassword)
    .font(constants.passwordFont)
    .kerning(constants.passwordKerning)
```

**Rationale**: Password is meant to be copied and used immediately. No security masking needed for transient confirmation screen. ViewModel provides raw value, View applies only visual styling (font, kerning).

---

## Entity Relationships

```
┌─────────────────────────────────┐
│ ReportMissingPetFlowState       │
│ (UPDATE - Add New Property)     │
│                                 │
│ @Published var                  │
│ managementPassword: String?     │
│ (+ all existing Step 1-4 props) │
└────────────┬────────────────────┘
             │ Injected into
             ▼
┌─────────────────────────────────┐
│   ToastScheduler (Service)      │
│   (REUSE - Existing)            │
│                                 │
│  - schedule(duration, handler)  │
│  - cancel()                     │
└────────────┬────────────────────┘
             │ Injected into
             ▼
┌─────────────────────────────────┐
│   SummaryViewModel              │
│   (UPDATE - Existing)           │
│                                 │
│  - displayPassword: String      │
│  - showsCodeCopiedToast: Bool   │
│  - copyPasswordToClipboard()    │
│  - handleSubmit() (existing)    │
└────────────┬────────────────────┘
             │ Observed by
             ▼
┌─────────────────────────────────┐
│   SummaryView                   │
│   (UPDATE - Replace Placeholder)│
│                                 │
│  - Displays password (from VM)  │
│  - Handles clipboard copy       │
│  - Existing Close button        │
└────────────┬────────────────────┘
             │ Uses
             ▼
┌─────────────────────────────────┐
│   SummaryView.Constants         │
│   (NEW Design Constants)        │
│                                 │
│  - Colors (Color type)          │
│  - Fonts (Font type)            │
│  - Spacing (CGFloat)            │
│  - Dimensions (CGFloat)         │
└─────────────────────────────────┘
```

**Key Points**:
- **Update existing domain model**: Add `managementPassword` to `ReportMissingPetFlowState`
- **Update existing ViewModels/Views**: Replace placeholder SummaryView with confirmation UI
- **Separation of concerns**: 
  - `SummaryViewModel` = business logic (password display, validation)
  - `SummaryView.Constants` = design values (Figma specs)
  - `SummaryView` = presentation (connects ViewModel + Constants)
- **Observer pattern**: View observes existing `SummaryViewModel`, which reads from flowState
- **No persistence**: Data is transient, cleared when flow is dismissed (existing `clear()` method)

---

## Data Flow Diagram

```
[Backend API]
      ↓ (POST /announcements response includes managementPassword)
      ↓
[Repository]
      ↓ (parses JSON, returns domain model)
      ↓
[ReportMissingPetFlowState]
      ↓ (updates @Published var managementPassword)
      ↓
[SummaryViewModel]  ← Observes flowState.managementPassword
      ↓ (exposes displayPassword computed property)
      ↓
[SummaryView]  ← Observes viewModel
      ↓
[Display Password in Gradient Container]
      ↓
[User Taps Password] → Copy to UIPasteboard → Show Toast
      ↓
[User Taps Close] → viewModel.handleSubmit() → Coordinator dismisses flow → Clear state
```

**Notes**:
- **Unidirectional data flow**: Backend → Repository → FlowState → ViewModel → View
- **No mutations from view**: View only reads via ViewModel, never writes
- **Existing callback mechanism**: `handleSubmit()` already wired to coordinator's `onSubmit` closure
- **Clipboard is side effect**: Handled in view action, not part of state management
- **Flow cleanup**: Coordinator calls `flowState.clear()` when flow is dismissed (update method to clear password)

---

## Localization Model

### Localized Strings (New)

**Location**: `/iosApp/iosApp/Resources/Localizable.strings` (en, pl)

**Entity**: Localization keys for screen text

**English Keys**:
```
"report_created_title" = "Report created";
"report_created_body_paragraph_1" = "Your report has been created, and your missing animal has been added to the database. If your pet is found, you will receive a notification immediately.";
"report_created_body_paragraph_2" = "If you wish to remove your report from the database, use the code provided below in the removal form. This code has also been sent to your email address";
"report_created_code_copied" = "Code copied to clipboard";
"report_created_close_button" = "Close";
```

**Polish Keys**:
```
"report_created_title" = "Raport utworzony";
"report_created_body_paragraph_1" = "Twój raport został utworzony, a Twoje zagubione zwierzę zostało dodane do bazy danych. Jeśli Twój pupil zostanie odnaleziony, natychmiast otrzymasz powiadomienie.";
"report_created_body_paragraph_2" = "Jeśli chcesz usunąć swój raport z bazy danych, użyj kodu podanego poniżej w formularzu usuwania. Ten kod został również wysłany na Twój adres e-mail";
"report_created_code_copied" = "Skopiowano kod do schowka";
"report_created_close_button" = "Zamknij";
```

**SwiftGen Integration**:
- Run `swiftgen` to generate `L10n.swift` after adding keys
- Access in code: `L10n.reportCreatedTitle`, `L10n.reportCreatedBodyParagraph1`, etc.

---

## Testing Model

### Test Data (for Unit Tests)

**Scenario 1: Password present**
```swift
let flowState = ReportFlowState()
flowState.managementPassword = "5216577"  // Typical 7-digit code
```

**Scenario 2: Password nil**
```swift
let flowState = ReportFlowState()
flowState.managementPassword = nil  // Should display empty string
```

**Scenario 3: Edge cases**
```swift
let flowState = ReportFlowState()
flowState.managementPassword = ""  // Empty string (valid, not nil)

let flowState2 = ReportFlowState()
flowState2.managementPassword = "123"  // Short code (valid)

let flowState3 = ReportFlowState()
flowState3.managementPassword = "12345678901234567890"  // Very long code (valid, should display as-is)
```

---

## Summary

### Domain Model Updates
- **UPDATE** `ReportMissingPetFlowState`: Add `@Published var managementPassword: String?`
- **UPDATE** `ReportMissingPetFlowState.clear()`: Clear managementPassword when exiting flow
- No database changes, no new API contracts

### View/ViewModel Updates
- **UPDATE** `SummaryView`: Replace placeholder with report confirmation UI, use existing ToastView component
- **UPDATE** `SummaryViewModel`: Add `displayPassword` computed property, `copyPasswordToClipboard()` action, `showsCodeCopiedToast` state
- **INJECT** `ToastScheduler` into `SummaryViewModel` (reuse existing service from PhotoViewModel pattern)
- **NEW** `SummaryView.Constants`: Design constants and typography specs from Figma (static values only)

### New Localization Entities
- 5 new localization keys (en/pl) for screen text

### Data Flow
- Unidirectional: Backend → Repository → FlowState → ViewModel → View
- View observes existing `SummaryViewModel`, which reads from flowState
- Existing `handleSubmit()` callback already wired for Close action
- No state persistence (transient confirmation screen)

### Validation
- Nil password → empty string display (no error message)
- Password displayed as-is (no formatting, no masking)

### Testing
- **UPDATE** existing unit tests: Add password display tests to `SummaryViewModelTests`
- **UPDATE** existing flow state tests: Add managementPassword tests to `ReportMissingPetFlowStateTests`
- **NEW** E2E tests: Verify full user flow (confirmation → copy → close)

