# Research: Missing Pet Report Flow (iOS)

**Feature**: 017-ios-missing-pet-flow  
**Date**: 2025-11-26  
**Purpose**: Resolve technical unknowns for modal coordinator-based multi-step flow

**SCOPE NOTE**: This implementation creates navigation skeleton only:
- Modal coordinator with UINavigationController
- 5 empty placeholder screens
- Navigation bar (progress indicator + back button)
- "Continue" button on each screen
- Flow state object (skeleton)

Screen content (input fields, photo picker, validation) will be implemented in future iterations.

---

## 1. Modal UINavigationController Presentation Pattern

### Decision

Use dedicated UINavigationController created and owned by `ReportMissingPetCoordinator`, presented modally with `.fullScreen` or `.formSheet` presentation style.

### Implementation Pattern

```swift
class ReportMissingPetCoordinator: CoordinatorInterface {
    weak var parentCoordinator: CoordinatorInterface?
    var childCoordinators: [CoordinatorInterface] = []
    var navigationController: UINavigationController?  // Own nav controller
    
    private let parentNavigationController: UINavigationController
    private var flowState: ReportMissingPetFlowState?  // Property, not local variable
    
    init(parentNavigationController: UINavigationController) {
        self.parentNavigationController = parentNavigationController
    }
    
    func start(animated: Bool) async {
        // Create dedicated UINavigationController for modal flow
        let modalNavController = UINavigationController()
        self.navigationController = modalNavController
        
        // Configure modal presentation
        modalNavController.modalPresentationStyle = .fullScreen  // or .formSheet for card style
        
        // Create ReportMissingPetFlowState as property
        let flowState = ReportMissingPetFlowState()
        self.flowState = flowState
        
        // Create and push first screen
        let viewModel = ChipNumberViewModel(flowState: flowState)
        viewModel.onNext = { [weak self] in
            self?.navigateToPhoto()
        }
        viewModel.onBack = { [weak self] in
            self?.exitFlow()
        }
        
        let view = ChipNumberView(viewModel: viewModel)
        let hostingController = UIHostingController(
            rootView: NavigationBackHiding { view }
        )
        
        // Configure navigation bar
        hostingController.title = L10n.ReportMissingPet.chipNumberTitle
        configureProgressIndicator(hostingController: hostingController, step: 1, total: 4)
        configureCustomBackButton(hostingController: hostingController, action: { [weak viewModel] in
            viewModel?.handleBack()
        })
        
        // Push to modal nav controller
        modalNavController.setViewControllers([hostingController], animated: false)
        
        // Present modally
        parentNavigationController.present(modalNavController, animated: animated)
    }
    
    func exitFlow() {
        // Dismiss modal, clearing flow state
        navigationController?.dismiss(animated: true) { [weak self] in
            self?.parentCoordinator?.childDidFinish(self)
        }
    }
}
```

### Rationale

- Modal presentation creates clear visual separation from parent flow (animal list)
- Own `UINavigationController` allows standard push/pop navigation within flow
- `.fullScreen` style prevents accidental dismissal via swipe gesture
- Coordinator owns both modal nav controller and flow state, ensuring proper lifecycle

### Alternatives Considered

- **Alternative 1**: Push all 5 screens onto parent nav controller
  - Rejected: No visual separation, clutters parent navigation stack
- **Alternative 2**: Use SwiftUI `.sheet()` with NavigationStack
  - Rejected: Less control over navigation bar customization, mixing UIKit/SwiftUI navigation

---

## 2. Progress Indicator in Navigation Bar

### Decision

Use `UIBarButtonItem` with custom `UIView` (circular badge) displaying "\(step)/\(total)", positioned on right side of navigation bar.

### Implementation Pattern

```swift
private func configureProgressIndicator(
    hostingController: UIHostingController<some View>,
    step: Int,
    total: Int
) {
    // Create circular badge view
    let badgeSize: CGFloat = 40
    let badgeView = UIView(frame: CGRect(x: 0, y: 0, width: badgeSize, height: badgeSize))
    badgeView.backgroundColor = UIColor(hex: "#155DFC")  // Primary blue
    badgeView.layer.cornerRadius = badgeSize / 2
    badgeView.clipsToBounds = true
    
    // Add label with step text
    let label = UILabel()
    label.text = "\(step)/\(total)"
    label.font = UIFont.systemFont(ofSize: 12, weight: .bold)
    label.textColor = .white
    label.textAlignment = .center
    label.frame = badgeView.bounds
    badgeView.addSubview(label)
    
    // Wrap in bar button item
    let barButtonItem = UIBarButtonItem(customView: badgeView)
    hostingController.navigationItem.rightBarButtonItem = barButtonItem
    
    // Accessibility
    badgeView.accessibilityIdentifier = "reportMissingPet.progressBadge"
    badgeView.accessibilityLabel = L10n.ReportMissingPet.progressLabel(step, total)
}
```

### Styling Details

- **Size**: 40x40pt circular badge
- **Background**: Primary blue (#155DFC)
- **Text**: White, bold, 12pt
- **Position**: Right bar button item

### Animation

**Decision**: No animation on progress change (instant update).

**Rationale**: Progress updates only when user navigates between screens. Instant update is clear and performant. Animation would add complexity without significant UX benefit.

### Alternatives Considered

- **Alternative 1**: Text-only progress in title (e.g., "Chip Number - 1/4")
  - Rejected: Clutters title, less visually prominent
- **Alternative 2**: Step dots/circles (••○○ style)
  - Rejected: Less explicit than numeric indicator, harder to parse at glance

---

## 3. ReportMissingPetFlowState Lifecycle Management

### Decision

`ReportMissingPetFlowState` (ObservableObject class) created by coordinator as property on `start()`, injected into all ViewModels via initializer. State cleared when flow exits (modal dismissed).

### Implementation Pattern

```swift
class ReportMissingPetFlowState: ObservableObject {
    @Published var chipNumber: String?
    @Published var photo: UIImage?
    @Published var description: String?
    @Published var contactEmail: String?
    @Published var contactPhone: String?
    
    init() {
        // Initialize with nil values
    }
    
    func clear() {
        chipNumber = nil
        photo = nil
        description = nil
        contactEmail = nil
        contactPhone = nil
    }
    
    // Computed properties for validation (if needed)
    var hasChipNumber: Bool {
        chipNumber != nil && !(chipNumber?.isEmpty ?? true)
    }
    
    var hasPhoto: Bool {
        photo != nil
    }
}

// Coordinator ownership
class ReportMissingPetCoordinator {
    private var flowState: ReportMissingPetFlowState?  // Property (strong reference)
    
    func start(animated: Bool) async {
        let flowState = ReportMissingPetFlowState()
        self.flowState = flowState
        
        // Inject into ViewModels
        let chipNumberVM = ChipNumberViewModel(flowState: flowState)
        let photoVM = PhotoViewModel(flowState: flowState)
        // ... etc
    }
    
    func exitFlow() {
        flowState?.clear()  // Clear state on exit
        flowState = nil
        navigationController?.dismiss(animated: true) { [weak self] in
            self?.parentCoordinator?.childDidFinish(self)
        }
    }
}
```

### State Persistence Rules

- **Within active session**: State persists when navigating forward/backward
- **On exit**: State cleared when user taps back from step 1 (modal dismissed)
- **On app background**: State preserved if flow remains active (modal still presented)
- **On app termination**: State lost (no disk persistence)

### Rationale

- Coordinator owns state → clear lifecycle and ownership
- ObservableObject enables reactive updates across ViewModels
- Clearing on exit ensures fresh state on next flow start

### Alternatives Considered

- **Alternative 1**: Each ViewModel owns piece of state
  - Rejected: Scattered state, hard to clear, complex coordination
- **Alternative 2**: Persist state to UserDefaults
  - Rejected: Out of scope (UI-only), no requirement for cross-session persistence

---

## 4. Back Button Behavior

### Decision

Use custom chevron-left back button (UIBarButtonItem) with `NavigationBackHiding` wrapper to hide default system button.

### Implementation Pattern

```swift
private func configureCustomBackButton(
    hostingController: UIHostingController<some View>,
    action: @escaping () -> Void
) {
    // Create chevron-left button
    let backButton = UIButton(type: .system)
    backButton.setImage(UIImage(systemName: "chevron.left"), for: .normal)
    backButton.tintColor = UIColor(hex: "#2D2D2D")  // Dark gray
    backButton.addAction(UIAction { _ in
        action()
    }, for: .touchUpInside)
    
    // Wrap in bar button item
    let backBarButtonItem = UIBarButtonItem(customView: backButton)
    hostingController.navigationItem.leftBarButtonItem = backBarButtonItem
    
    // Accessibility
    backButton.accessibilityIdentifier = "reportMissingPet.backButton"
    backButton.accessibilityLabel = L10n.Common.back
}

// NavigationBackHiding wrapper (already exists in project)
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

// Usage
let view = ChipNumberView(viewModel: viewModel)
let hostingController = UIHostingController(
    rootView: NavigationBackHiding { view }
)
```

### Back Button Action Logic

- **Steps 2-4**: Navigate to previous screen (pop view controller)
- **Step 1**: Exit flow (dismiss modal, clear state)
- **Summary**: Navigate back to step 4

### Rationale

- Custom button provides consistent styling across flow
- NavigationBackHiding prevents default system back button conflicts
- ViewModel callback pattern keeps navigation logic in coordinator

### Alternatives Considered

- **Alternative 1**: Use default system back button
  - Rejected: No control over styling, behavior inconsistent (exit on step 1 vs pop on others)
- **Alternative 2**: Swipe gesture only
  - Rejected: Not discoverable, harder to implement custom exit logic

---

## 5. Photo Picker Integration

### Decision

**OUT OF SCOPE** for this iteration. Photo screen will be empty placeholder with just "Continue" button.

### Future Implementation

Photo picker will be implemented in future iteration. For now:
- Photo screen is empty placeholder
- Just "Continue" button
- No PHPickerViewController integration
- FlowState.photo property defined but unused

**Planned approach** (when implemented):
- Use `PHPickerViewController` (iOS 14+)
- Coordinator presents picker
- Result saved to `flowState.photo`

---

## 6. Answers to Open Questions

### Q: Should progress indicator animate when changing steps?

**Answer**: No animation.

**Rationale**: Progress updates only on screen transitions. Instant update is clear and avoids complexity.

---

### Q: Should flow state persist if app backgrounds during flow?

**Answer**: Yes, state preserved while flow active.

**Rationale**: User expects to resume where they left off if they briefly background app. State cleared only when flow explicitly exited.

---

### Q: How to handle device rotation during flow?

**Answer**: Support portrait and landscape via auto-layout constraints.

**Rationale**: Standard iOS behavior. SwiftUI views adapt automatically. Progress indicator and back button remain visible in both orientations.

---

### Q: Should "next" button be disabled for invalid inputs?

**Answer**: Always enabled (per spec requirements).

**Rationale**: Spec states all fields optional except contact details. Empty inputs allowed. Future feature may add validation.

---

### Q: How to style progress indicator badge?

**Answer**: 
- Size: 40x40pt circular
- Background: #155DFC (primary blue)
- Text: White, bold, 12pt, "\(step)/\(total)"
- Position: Right bar button item

**Rationale**: Matches design system from branch 012 (PetDetails feature). Prominent but not intrusive.

---

## Summary of Decisions

| Topic | Decision | Key Points |
|-------|----------|------------|
| Modal Presentation | Dedicated UINavigationController with `.fullScreen` style | Clear separation, own navigation stack |
| Progress Indicator | Custom circular badge (40x40pt) on right bar button | Blue background, white text, no animation |
| ReportMissingPetFlowState | ObservableObject owned by coordinator as property, injected to VMs | Skeleton only (properties unused), cleared on exit |
| Back Button | Custom chevron-left with NavigationBackHiding wrapper | Exit on step 1, pop on steps 2-5 |
| Photo Picker | OUT OF SCOPE - empty placeholder screen | Will be implemented in future iteration |
| Screen Content | Empty placeholders with Continue button only | Form fields added in future iterations |

---

## References

- Existing pattern: `/iosApp/iosApp/Features/PetDetails/Coordinators/PetDetailsCoordinator.swift`
- Constitution v2.3.0: 
  - Principle XI: iOS MVVM-C Architecture section
  - **Principle XII: End-to-End Testing (Java/Maven/Cucumber)** ⚠️ Updated Nov 2025
- Apple documentation: PHPickerViewController, UINavigationController modal presentation

**NOTE - E2E Testing Framework Migration**: E2E tests for this feature MUST be implemented in Java/Maven/Cucumber format per Constitution v2.3.0 Principle XII, NOT TypeScript. See tasks.md for updated E2E task structure.

