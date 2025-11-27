# Research: iOS Microchip Number Screen

**Feature Branch**: `019-ios-chip-number-screen`  
**Date**: 2025-11-27  
**Research Phase**: Phase 0 - Technical unknowns resolution

## Research Questions

### 1. SwiftUI Text Field Formatting with Live Input Transformation

**Question**: How to implement live text formatting in SwiftUI TextField that automatically inserts hyphens at specific positions (6th and 12th character) as user types, while maintaining natural cursor behavior?

**Decision**: Use `onChange(of:)` modifier with separate `MicrochipNumberFormatter` helper that transforms raw input into formatted output.

**Rationale**:
- SwiftUI `TextField` binding works with `String` and doesn't interfere with system cursor management
- `onChange(of:)` provides reactive hook to transform input after each change
- Separate helper makes formatting logic reusable and easier to test in isolation
- System handles cursor position naturally when we update the binding value
- Follows single responsibility principle (ViewModel for state, helper for formatting)

**Implementation Pattern**:
```swift
// Helper (stateless utility)
struct MicrochipNumberFormatter {
    static func format(_ input: String) -> String {
        let digits = input.filter { $0.isNumber }
        let limited = String(digits.prefix(15))
        
        var formatted = ""
        for (index, digit) in limited.enumerated() {
            if index == 5 || index == 10 {
                formatted.append("-")
            }
            formatted.append(digit)
        }
        return formatted
    }
    
    static func extractDigits(_ input: String) -> String {
        return input.filter { $0.isNumber }
    }
}

// In ViewModel
@Published var microchipNumber: String = ""

func formatMicrochipNumber(_ input: String) {
    microchipNumber = MicrochipNumberFormatter.format(input)
}

// In view
TextField("00000-00000-00000", text: $viewModel.microchipNumber)
    .keyboardType(.numberPad)
    .onChange(of: viewModel.microchipNumber) { newValue in
        viewModel.formatMicrochipNumber(newValue)
    }
```

**Alternatives Considered**:
- `UIViewRepresentable` wrapping `UITextField`: Rejected - too complex, loses SwiftUI benefits, requires manual cursor management
- Custom `TextFieldStyle`: Rejected - formatting happens at data level, not presentation style
- `Formatter` with `TextField(value:formatter:)`: Rejected - designed for numbers/dates, awkward for custom string patterns

**Edge Cases Handled**:
- User pastes "123456789012345" → formats to "12345-67890-12345"
- User pastes "12345-67890-12345" → re-formats correctly (removes hyphens first)
- User types beyond 15 digits → truncates to 15 digits max
- User deletes characters → re-formats remaining digits correctly
- Empty input → displays placeholder "00000-00000-00000"

---

### 2. NavigationBackHiding Wrapper Pattern

**Question**: What is `NavigationBackHiding` wrapper mentioned in constitution and how should it be used with UIHostingController?

**Decision**: `NavigationBackHiding` is an **EXISTING** SwiftUI wrapper view located at `/iosApp/iosApp/Views/NavigationBackHiding.swift` that hides the default navigation back button, allowing coordinator to manage back navigation explicitly.

**Rationale**:
- UIKit `UINavigationController` adds default back button automatically
- For coordinator-driven flows, we want explicit back button handling (not automatic pop)
- SwiftUI views wrapped in `UIHostingController` still get UIKit navigation chrome
- `NavigationBackHiding` uses `.navigationBarBackButtonHidden(true)` SwiftUI modifier

**Existing Implementation** (`/iosApp/iosApp/Views/NavigationBackHiding.swift`):
```swift
/// View modifier that hides the default navigation bar back button.
/// Used when implementing custom back button in navigation bar.
struct NavigationBackHiding<Content: View>: View {
    let content: Content
    
    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }
    
    var body: some View {
        content
            .navigationBarBackButtonHidden(true)
    }
}
```

**Usage in Coordinator**:
```swift
// Usage in Coordinator
func showMicrochipScreen() {
    let viewModel = MicrochipNumberViewModel(...)
    let view = MicrochipNumberView(viewModel: viewModel)
    
    // Wrap in NavigationBackHiding, then UIHostingController
    let hostingController = UIHostingController(
        rootView: NavigationBackHiding { view }
    )
    
    navigationController.pushViewController(hostingController, animated: true)
}
```

**Note**: This wrapper already exists in the codebase and is ready to use. No implementation needed for this feature.

---

### 3. Flow State Management in Coordinator Architecture

**Question**: How should Flow State be designed and passed between screens in a multi-step coordinator-driven flow?

**Decision**: Coordinator owns a single `ReportMissingPetFlowState` reference-type object (class) and passes it by reference to each ViewModel in the flow.

**Rationale**:
- Reference type (class) ensures all ViewModels see same state instance
- Coordinator lifecycle matches flow lifecycle - when coordinator deallocates, flow state is cleared
- ViewModels mutate flow state properties directly (e.g., `flowState.microchipNumber = "123..."`)
- No need for delegation/callbacks for state updates - shared reference handles it
- Simplifies navigation logic: coordinator decides next screen based on flow state

**Implementation Pattern**:
```swift
// Flow State (reference type - class, not struct)
class ReportMissingPetFlowState {
    var microchipNumber: String? = nil  // Step 1/4 data
    var lastSeenLocation: String? = nil // Step 2/4 data (future)
    var petDescription: String? = nil   // Step 3/4 data (future)
    var contactInfo: String? = nil      // Step 4/4 data (future)
}

// Coordinator owns flow state
class ReportMissingPetCoordinator {
    private let navigationController: UINavigationController
    private let flowState = ReportMissingPetFlowState()  // Owned by coordinator
    
    func start() {
        showMicrochipScreen()
    }
    
    private func showMicrochipScreen() {
        // Pass flow state reference to ViewModel
        let viewModel = MicrochipNumberViewModel(
            flowState: flowState,
            onContinue: { [weak self] in
                self?.showNextScreen()  // Step 2/4
            },
            onCancel: { [weak self] in
                self?.dismissFlow()
            }
        )
        
        let view = MicrochipNumberView(viewModel: viewModel)
        let hostingController = UIHostingController(rootView: NavigationBackHiding { view })
        navigationController.pushViewController(hostingController, animated: true)
    }
    
    private func showNextScreen() {
        // Step 2/4 screen gets same flowState reference
        // Can read microchipNumber from flowState.microchipNumber
    }
    
    private func dismissFlow() {
        // Pop all screens in flow, flow state deallocates with coordinator
        navigationController.popToRootViewController(animated: true)
    }
}

// ViewModel receives flow state reference
class MicrochipNumberViewModel: ObservableObject {
    @Published var microchipNumber: String = ""
    
    private let flowState: ReportMissingPetFlowState
    private let onContinue: () -> Void
    private let onCancel: () -> Void
    
    init(flowState: ReportMissingPetFlowState, onContinue: @escaping () -> Void, onCancel: @escaping () -> Void) {
        self.flowState = flowState
        self.onContinue = onContinue
        self.onCancel = onCancel
        
        // Restore state if returning to this screen
        if let saved = flowState.microchipNumber {
            self.microchipNumber = formatMicrochipNumber(saved)
        }
    }
    
    func saveToContinue() {
        // Save raw digits to flow state (no hyphens)
        let digits = microchipNumber.filter { $0.isNumber }
        flowState.microchipNumber = digits.isEmpty ? nil : digits
        
        // Trigger navigation to step 2/4
        onContinue()
    }
    
    func cancel() {
        // Dismiss entire flow
        onCancel()
    }
}
```

**Alternatives Considered**:
- Value type (struct) with delegate callbacks: Rejected - requires manual synchronization, boilerplate
- Singleton flow state: Rejected - memory leak risk, unclear lifecycle
- Coordinator passing data dictionaries: Rejected - not type-safe, error-prone
- Combine publishers for state: Rejected - overcomplicated for simple data passing

---

### 4. Numeric Keyboard (.numberPad) Behavior and Constraints

**Question**: What are the characteristics and limitations of `.numberPad` keyboard type in SwiftUI TextField?

**Decision**: Use `.keyboardType(.numberPad)` which provides numeric-only input UI, but software-level filtering is still required for paste operations and hardware keyboard input.

**Rationale**:
- `.numberPad` displays numeric keyboard on iOS devices (digits 0-9 only, no letters)
- Does NOT prevent paste operations with non-numeric content
- Does NOT prevent hardware keyboard (iPad, simulator) from typing letters
- Must filter input in `onChange(of:)` to ensure digits-only storage

**Implementation**:
```swift
TextField("00000-00000-00000", text: $viewModel.microchipNumber)
    .keyboardType(.numberPad)  // Suggests numeric keyboard
    .onChange(of: viewModel.microchipNumber) { newValue in
        // Filter non-numeric characters (handles paste, hardware keyboard)
        let digits = newValue.filter { $0.isNumber }
        let limited = String(digits.prefix(15))
        viewModel.microchipNumber = formatWithHyphens(limited)
    }
```

**Keyboard Characteristics**:
- Shows: Digits 0-9, delete button
- Does NOT show: Letters, symbols (except on iPad hardware keyboard)
- Paste behavior: User can paste any string → must be filtered
- Hardware keyboard: User can type letters → must be filtered
- iOS Simulator: May behave differently (hardware keyboard active by default)

**Testing Requirements**:
- Test paste with letters/symbols → should filter to digits only
- Test paste with pre-formatted string "12345-67890-12345" → should handle gracefully
- Test rapid typing → formatting should keep up without lag
- Test deletion → should re-format remaining digits correctly

**Alternatives Considered**:
- `.keyboardType(.decimalPad)`: Rejected - includes decimal point, not needed
- `.keyboardType(.phonePad)`: Rejected - includes +*# symbols, not needed
- Custom keyboard: Rejected - overkill, native keyboard is standard UX

---

### 5. UIKit Navigation Bar Setup in MVVM-C Architecture

**Question**: How should UIKit navigation bar (title, progress indicator, back button) be configured when using SwiftUI views in UIHostingController?

**Decision**: Configure `UINavigationItem` properties on the `UIHostingController` instance in the coordinator, before or after pushing to navigation stack.

**Rationale**:
- `UIHostingController` exposes `navigationItem` property (from `UIViewController`)
- Coordinator owns navigation logic, so it's responsible for bar configuration
- SwiftUI `.navigationTitle()` modifiers don't work reliably with UIKit navigation stack
- Keeps SwiftUI view pure (no UIKit dependencies)

**Implementation Pattern**:
```swift
// In Coordinator
func showMicrochipScreen() {
    let viewModel = MicrochipNumberViewModel(...)
    let view = MicrochipNumberView(viewModel: viewModel)
    let hostingController = UIHostingController(rootView: NavigationBackHiding { view })
    
    // Configure navigation bar (UIKit)
    hostingController.navigationItem.title = L10n.MicrochipNumber.title  // "Microchip number"
    hostingController.navigationItem.prompt = "1/4"  // Progress indicator above title
    
    // Custom back button (calls coordinator's dismissFlow)
    let backButton = UIBarButtonItem(
        image: UIImage(systemName: "chevron.left"),
        style: .plain,
        target: self,
        action: #selector(dismissFlow)
    )
    hostingController.navigationItem.leftBarButtonItem = backButton
    
    navigationController.pushViewController(hostingController, animated: true)
}

@objc private func dismissFlow() {
    // Close entire flow, return to pet list
    navigationController.popToRootViewController(animated: true)
}
```

**Navigation Bar Anatomy**:
```
┌─────────────────────────────────────┐
│ < Back          1/4                 │  ← prompt (progress)
│ Microchip number                    │  ← title
└─────────────────────────────────────┘
```

**Localization**:
- ALL strings MUST use SwiftGen: `L10n.MicrochipNumber.title`, `L10n.MicrochipNumber.progress`
- Update `/iosApp/iosApp/Resources/en.lproj/Localizable.strings` with new keys

**Alternatives Considered**:
- SwiftUI `.navigationTitle()` and `.toolbar()`: Rejected - unreliable with UIKit nav stack
- Setting title directly in SwiftUI view: Rejected - violates MVVM-C (view shouldn't know navigation context)
- Custom UINavigationBar subclass: Rejected - standard nav bar is sufficient

---

## Technology Stack Summary

Based on research and constitution requirements:

**Language/Version**: Swift 5.9+ (iOS 15+ target)  
**Primary Dependencies**:
- SwiftUI (UI layer)
- UIKit (coordinators, navigation)
- Foundation (data types, formatters)
- SwiftGen (localization - mandatory)

**Storage**: 
- Flow State (in-memory, reference type class)
- Owned by `ReportMissingPetCoordinator`
- Lifetime: duration of 4-step flow

**Testing**: 
- XCTest with Swift Concurrency (async/await)
- Location: `/iosApp/iosAppTests/Features/ReportMissingPet/`
- Target: 80% line + branch coverage

**Target Platform**: iOS 15+  
**Project Type**: Mobile (iOS - MVVM-C architecture)

**Architectural Patterns**:
- MVVM-C (mandatory per constitution)
- Manual DI via ServiceContainer (mandatory)
- ViewModels call repositories directly (NO use cases layer)
- Coordinators manage navigation and flow
- SwiftUI views wrapped in `UIHostingController`
- `NavigationBackHiding` wrapper for coordinator-controlled back button

**Performance Goals**: 
- Input formatting: < 50ms latency per keystroke
- Screen transition: < 300ms (standard iOS animation)

**Constraints**:
- Field MUST limit to 15 digits maximum
- Formatting MUST not cause cursor jumping
- Back button MUST close entire flow (not just current screen)
- Flow state MUST persist during forward/back navigation within flow
- Flow state MUST clear when flow is cancelled or completed

**Scale/Scope**: Single screen (1/4 of larger flow), ~200-300 LOC total

---

## Key Decisions Summary

| Decision Area | Chosen Approach | Rationale |
|---------------|----------------|-----------|
| Text formatting | `onChange(of:)` with custom formatter | Natural cursor behavior, simple implementation |
| Flow state | Reference type (class) owned by coordinator | Shared state across screens, clear lifecycle |
| Navigation bar | UIKit `navigationItem` configured by coordinator | Coordinator owns navigation, pure SwiftUI view |
| Back button | Custom `leftBarButtonItem` calling coordinator | Explicit flow cancellation logic |
| Keyboard type | `.numberPad` + software filtering | Prevents accidental letters, handles paste/hardware keyboard |
| Architecture | MVVM-C (per constitution) | Mandatory pattern, separation of concerns |
| Localization | SwiftGen for all strings | Mandatory per constitution, type-safe |
| DI | Manual (ServiceContainer) | Mandatory per constitution, no frameworks |

---

## Open Questions / Future Considerations

None - all technical unknowns resolved.

---

## References

- Constitution: `.specify/memory/constitution.md` (Principle XI - iOS MVVM-C Architecture)
- Feature Spec: `/specs/019-ios-chip-number-screen/spec.md`
- Related Flow Spec: `/specs/017-ios-missing-pet-flow/` (describes full 4-step flow)
- Existing coordinators: `/iosApp/iosApp/Coordinators/` (for reference patterns)
- Existing ViewModels: `/iosApp/iosApp/Features/*/ViewModels/` (for MVVM patterns)
