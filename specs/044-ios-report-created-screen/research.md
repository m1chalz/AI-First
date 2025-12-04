# Research: Report Created Confirmation Screen

**Feature**: iOS Report Created Confirmation Screen  
**Phase**: 0 (Research & Outline)  
**Date**: 2025-12-03

## Research Questions

### Question 1: SwiftUI Gradient Implementation

**Context**: Figma design specifies horizontal gradient from #5C33FF to #F84BA1 with soft blur glow (#FB64B6 @ 20% alpha) for management password container.

**Decision**: Use SwiftUI `LinearGradient` with `.overlay()` modifier for glow effect.

**Rationale**: 
- Native SwiftUI API for gradients is performant and declarative
- Overlay with blur provides soft glow effect matching Figma design
- No third-party dependencies needed

**Implementation Pattern**:
```swift
RoundedRectangle(cornerRadius: 10)
    .fill(
        LinearGradient(
            colors: [Color(hex: "#5C33FF"), Color(hex: "#F84BA1")],
            startPoint: .leading,
            endPoint: .trailing
        )
    )
    .overlay(
        RoundedRectangle(cornerRadius: 10)
            .fill(Color(hex: "#FB64B6").opacity(0.2))
            .blur(radius: 20)
    )
```

**Alternatives Considered**:
- **CAGradientLayer**: Rejected - requires UIViewRepresentable wrapper, less declarative
- **Third-party gradient libraries**: Rejected - unnecessary dependency for simple gradient

---

### Question 2: Clipboard Copy with Toast Confirmation

**Context**: User taps password module → copy to clipboard → show toast "Code copied to clipboard" (EN) / "Skopiowano kod do schowka" (PL).

**Decision**: Use `UIPasteboard.general.string` for clipboard + reuse existing `ToastView` shared component.

**Rationale**:
- `UIPasteboard` is iOS standard API for clipboard operations
- Existing `ToastView` component already handles transient messages (reuse pattern)
- Localization via SwiftGen for multilingual support (EN/PL)

**Implementation Pattern**:
```swift
Button(action: {
    UIPasteboard.general.string = managementPassword
    showToast(message: L10n.reportCreatedCodeCopied)
}) {
    // Password display
}
```

**Alternatives Considered**:
- **Custom toast implementation**: Rejected - existing `ToastView` component provides consistent UX
- **Third-party clipboard libraries**: Rejected - `UIPasteboard` is sufficient for simple string copy

---

### Question 3: Typography and Font Handling

**Context**: Figma specifies Hind Regular (32px for title, 16px for body) and Arial Regular (60px for password digits).

**Decision**: Use iOS system font substitutions with weight and size matching Figma specs.

**Rationale**:
- Hind font family not bundled in iOS → use `.system(.regular)` equivalent
- Arial available on iOS as system font
- Typography consistency maintained via explicit size and weight specifications

**Implementation Pattern**:
```swift
// Title: Hind Regular 32px → .system(size: 32, weight: .regular)
Text(L10n.reportCreatedTitle)
    .font(.system(size: 32, weight: .regular))
    .foregroundColor(Color(red: 0, green: 0, blue: 0, opacity: 0.8))

// Body: Hind Regular 16px → .system(size: 16, weight: .regular)
Text(L10n.reportCreatedBodyParagraph1)
    .font(.system(size: 16, weight: .regular))
    .foregroundColor(Color(hex: "#545F71"))

// Password: Arial Regular 60px
Text(managementPassword)
    .font(.custom("Arial", size: 60))
    .foregroundColor(.white)
    .kerning(-1.5)  // -1.5px tracking from Figma
```

**Alternatives Considered**:
- **Bundling Hind font**: Rejected - increases app size, system font provides equivalent visual weight
- **Using Text Styles (e.g., .title, .body)**: Rejected - Figma has specific pixel sizes that don't map to standard styles

---

### Question 4: Safe Area and Layout Constraints

**Context**: Figma shows 375×814 device with 46px rounded corners and safe-area-aware padding. FR-004 requires 22px horizontal padding, 24px vertical spacing, and safe area insets.

**Decision**: Use SwiftUI `.padding()` with explicit values + `.edgesIgnoringSafeArea(.all).safeAreaInset()` for precise control.

**Rationale**:
- SwiftUI safe area APIs handle dynamic safe area insets (status bar, notch, home indicator)
- Explicit padding values match Figma specs exactly
- `.safeAreaInset()` ensures content remains visible on all device sizes

**Implementation Pattern**:
```swift
VStack(spacing: 24) {
    // Header, body, password, button
}
.padding(.horizontal, 22)
.safeAreaInset(edge: .top) { Color.clear.frame(height: 32) }
.safeAreaInset(edge: .bottom) { Color.clear.frame(height: 16) }
```

**Alternatives Considered**:
- **Ignoring safe area entirely**: Rejected - risks content being clipped by notch/home indicator
- **Using GeometryReader**: Rejected - adds complexity, SwiftUI safe area APIs are sufficient

---

### Question 5: Data Source and State Management

**Context**: Management password sourced from `@Published var managementPassword: String?` in flowState. Nil maps to empty string (no error message). Reusing existing `SummaryView`/`SummaryViewModel`.

**Decision**: 
1. Add `@Published var managementPassword: String?` to existing `ReportMissingPetFlowState`
2. Update existing `SummaryViewModel` to expose password for display
3. Update existing `SummaryView` (currently placeholder) to display confirmation UI

**Rationale**:
- `SummaryView`/`SummaryViewModel` already exist as placeholders (TODO comment: "Display collected data in future iteration")
- `SummaryViewModel` already observes `ReportMissingPetFlowState` and has `onSubmit` callback wired
- No new ViewModel needed - extend existing one
- Maintains existing architecture pattern (all report steps use same flowState)

**Implementation Pattern**:
```swift
// 1. Add to ReportMissingPetFlowState.swift
@Published var managementPassword: String?

// 2. Update SummaryViewModel.swift (add computed property)
var displayPassword: String {
    flowState.managementPassword ?? ""
}

// 3. Update SummaryView.swift (replace placeholder with confirmation UI)
struct SummaryView: View {
    @ObservedObject var viewModel: SummaryViewModel
    
    var body: some View {
        // Confirmation UI with password display
        Text(viewModel.displayPassword)
            .accessibilityIdentifier("summary.password")
    }
}
```

**Alternatives Considered**:
- **Create new ReportCreatedView/ViewModel**: Rejected - SummaryView already exists for this purpose
- **Use @State for local copy**: Rejected - violates single source of truth (flowState already holds data)

---

### Question 6: Localization Strategy

**Context**: Screen text in English and Polish. SwiftGen MUST be used per iOS architecture rules.

**Decision**: Add all user-facing strings to `Localizable.strings` (en/pl), access via SwiftGen-generated `L10n` enum.

**Rationale**:
- Constitution mandates SwiftGen for all displayed text
- Centralized localization enables easy translation updates
- Type-safe string access prevents typos and missing translations

**Implementation**:
1. Add to `en.strings`:
   ```
   "report_created_title" = "Report created";
   "report_created_body_paragraph_1" = "Your report has been created, and your missing animal has been added to the database. If your pet is found, you will receive a notification immediately.";
   "report_created_body_paragraph_2" = "If you wish to remove your report from the database, use the code provided below in the removal form. This code has also been sent to your email address";
   "report_created_code_copied" = "Code copied to clipboard";
   "report_created_close_button" = "Close";
   ```

2. Add to `pl.strings`:
   ```
   "report_created_title" = "Raport utworzony";
   "report_created_body_paragraph_1" = "Twój raport został utworzony, a Twoje zagubione zwierzę zostało dodane do bazy danych. Jeśli Twój pupil zostanie odnaleziony, natychmiast otrzymasz powiadomienie.";
   "report_created_body_paragraph_2" = "Jeśli chcesz usunąć swój raport z bazy danych, użyj kodu podanego poniżej w formularzu usuwania. Ten kod został również wysłany na Twój adres e-mail";
   "report_created_code_copied" = "Skopiowano kod do schowka";
   "report_created_close_button" = "Zamknij";
   ```

3. Run SwiftGen to regenerate `L10n.swift`

4. Use in code:
   ```swift
   Text(L10n.reportCreatedTitle)
   ```

**Alternatives Considered**:
- **Hardcoded strings**: Rejected - violates constitution
- **NSLocalizedString directly**: Rejected - SwiftGen provides type safety and compile-time verification

---

## Best Practices Applied

### 1. iOS MVVM Pattern (Simplified, No Coordinator)

**Context**: Screen presented within existing flow, no navigation responsibility.

**Pattern**: Simple presentation view observing parent flowState.

**Benefits**:
- Minimal boilerplate for simple confirmation screen
- Reuses existing coordinator and flow state management
- Clear data flow: flowState → view

---

### 2. Test Identifiers (Constitution Principle VI)

**Context**: All interactive elements MUST have `.accessibilityIdentifier()` for E2E tests.

**Pattern**:
```swift
// Password module (View uses ViewModel, not flowState directly)
Text(viewModel.displayPassword)
    .accessibilityIdentifier("summary.password")

// Close button
Button(action: viewModel.handleSubmit) {
    Text(L10n.ReportMissingPet.Button.close)
}
.accessibilityIdentifier("summary.closeButton")
```

**Benefits**:
- Stable test identifiers for Appium E2E tests
- Consistent naming convention across app
- Enables automated UI testing

---

### 3. Given-When-Then Test Structure (Constitution Principle VIII)

**Context**: All tests MUST follow Given-When-Then pattern.

**Pattern**:
```swift
func test_whenManagementPasswordIsNil_shouldDisplayEmptyString() {
    // Given - flowState with nil password
    let flowState = ReportFlowState()
    flowState.managementPassword = nil
    
    // When - view is rendered
    let view = ReportCreatedView(flowState: flowState)
    
    // Then - password displays empty string
    // (assertion logic here)
}
```

---

### 4. SwiftUI Presentation Model Pattern

**Context**: Colors stored as hex strings in models, converted to SwiftUI `Color` in views.

**Pattern**:
```swift
// Constants extension (actual implementation)
extension SummaryView {
    struct Constants {
        let gradientStartColor: Color = Color(hex: "#5C33FF")
        let gradientEndColor: Color = Color(hex: "#F84BA1")
        let glowColor: Color = Color(hex: "#FB64B6")
    }
}

// View uses constants directly
constants.gradientStartColor  // Already a Color
```

**Benefits**:
- Decouples presentation from SwiftUI framework
- Enables easy theme changes
- Testable color logic

---

## Integration Points

### 1. Existing Components

**ToastView + ToastScheduler** (existing pattern from PhotoView):
- Location: `/iosApp/iosApp/Features/ReportMissingPet/Views/Components/ToastView.swift`
- Service: `/iosApp/iosApp/Features/ReportMissingPet/Services/ToastScheduler.swift`
- Usage: Display "Code copied to clipboard" confirmation
- Integration Pattern (matching PhotoView):
  1. Inject `ToastScheduler` into ViewModel
  2. ViewModel has `@Published var showsCodeCopiedToast = false`
  3. ViewModel's `copyPasswordToClipboard()` sets flag and schedules hide after 2s
  4. View displays toast in VStack above button when flag is true
  5. Toast uses `.transition(.move(edge: .bottom).combined(with: .opacity))`

---

### 2. Existing Coordinator

**ReportMissingPetCoordinator**:
- Location: `/iosApp/iosApp/Features/ReportMissingPet/Coordinators/`
- Responsibility: Already presents `SummaryView` after successful report submission (flow exists)
- Integration: NO CHANGES NEEDED - coordinator already shows SummaryView with flowState, we only update SummaryView content

---

### 3. Localization System

**SwiftGen**:
- Configuration: `swiftgen.yml` at root
- Input: `Localizable.strings` (en/pl)
- Output: `L10n.swift` (generated)
- Integration: Run `swiftgen` after adding new strings

---

## Risk Assessment

### Low Risk Items
- **Gradient implementation**: Standard SwiftUI API, well-documented
- **Clipboard operations**: Native iOS API, straightforward
- **Localization**: Existing SwiftGen setup in project

### Medium Risk Items
- **Typography matching Figma**: System font substitution may have slight visual differences (mitigated by explicit size/weight specs)
- **Blur effect performance**: Multiple overlays with blur could impact performance on older devices (mitigated by simple layout, single blur)

### High Risk Items
- None identified

---

## Dependencies

### No New External Dependencies
All functionality implemented with native iOS APIs and existing project components.

### Existing Dependencies Used
- SwiftUI (iOS 15+)
- Foundation (UIPasteboard)
- SwiftGen (already configured)
- Existing `ToastView` component

---

## Summary

All technical questions resolved. Feature can be implemented using:
- Native SwiftUI for UI (gradients, layout, typography)
- Native iOS clipboard API
- Existing shared components (ToastView)
- Existing flow coordinator (no new coordinator needed)
- SwiftGen for localization (add new strings to existing setup)

No architectural violations. No new external dependencies. All patterns align with iOS constitution (MVVM, test identifiers, Given-When-Then tests, SwiftGen localization).

