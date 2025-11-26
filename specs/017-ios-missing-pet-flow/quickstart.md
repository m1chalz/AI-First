# Quickstart: Missing Pet Report Flow (iOS)

**Feature**: 017-ios-missing-pet-flow  
**Date**: 2025-11-26  
**Purpose**: Developer onboarding guide for working with the flow

---

## Overview

The Missing Pet Report Flow is a modal, multi-step UI flow for iOS. **This implementation creates the navigation skeleton only** - 5 empty placeholder screens with navigation infrastructure.

**Current Scope (Navigation Skeleton)**:
- Modal coordinator with own UINavigationController
- 5 placeholder screens (empty views with "Continue" button)
- Navigation bar (progress indicator + custom back button)
- Flow state object (properties defined but unused)
- Navigation between screens

**Future Scope (Separate Implementations)**:
- Input fields (chip number, description, contact details)
- Photo picker integration
- Form validation
- Data persistence

**Key Concepts**:
- **Modal Presentation**: Flow has own `UINavigationController`, presented modally
- **Child Coordinator**: `ReportMissingPetCoordinator` managed by parent `AnimalListCoordinator`
- **Shared State**: `ReportMissingPetFlowState` (ObservableObject) injected into all ViewModels (empty for now)
- **Progress Indicator**: Custom circular badge (1/4, 2/4, 3/4, 4/4) in navigation bar
- **Custom Back Button**: Chevron-left button with conditional behavior (exit on step 1, pop on steps 2-5)
- **Minimal ViewModels**: Only navigation callbacks, no form logic yet
- **Empty Views**: Just "Continue" button, screen content added in future iterations

---

## How to Trigger the Flow

### From Animal List Screen

The flow is triggered when user taps "report missing animal" button on the animal list screen.

**Location**: `/iosApp/iosApp/Features/AnimalList/Coordinators/AnimalListCoordinator.swift`

```swift
private func showReportMissing() {
    // Create child coordinator
    let reportCoordinator = ReportMissingPetCoordinator(
        parentNavigationController: self.navigationController!
    )
    reportCoordinator.parentCoordinator = self
    childCoordinators.append(reportCoordinator)
    
    // Start flow
    Task { @MainActor in
        await reportCoordinator.start(animated: true)
    }
}
```

### Coordinator Lifecycle

1. Parent creates `ReportMissingPetCoordinator`
2. Coordinator creates modal `UINavigationController`
3. Coordinator creates `ReportMissingPetFlowState` as property
4. Coordinator pushes first screen (chip number)
5. User navigates through steps
6. On exit, coordinator dismisses modal and cleans up
7. Parent removes child coordinator

---

## Project Structure

```text
iosApp/iosApp/Features/ReportMissingPet/
├── Coordinators/
│   └── ReportMissingPetCoordinator.swift      # Flow navigation logic
├── Models/
│   └── ReportMissingPetFlowState.swift           # Shared state object
└── Views/
    ├── ChipNumber/
    │   ├── ChipNumberView.swift               # Step 1 UI
    │   └── ChipNumberViewModel.swift          # Step 1 state + actions
    ├── Photo/
    │   ├── PhotoView.swift                    # Step 2 UI
    │   └── PhotoViewModel.swift               # Step 2 state + actions
    ├── Description/
    │   ├── DescriptionView.swift              # Step 3 UI
    │   └── DescriptionViewModel.swift         # Step 3 state + actions
    ├── ContactDetails/
    │   ├── ContactDetailsView.swift           # Step 4 UI
    │   └── ContactDetailsViewModel.swift      # Step 4 state + actions
    └── Summary/
        ├── SummaryView.swift                  # Step 5 UI
        └── SummaryViewModel.swift             # Step 5 state + actions
```

---

## How to Add a New Step to the Flow

### Step 1: Create ViewModel (Minimal - Navigation Only)

**File**: `/Views/NewStep/NewStepViewModel.swift`

```swift
import Foundation

@MainActor
class NewStepViewModel: ObservableObject {
    // MARK: - Dependencies
    
    private let flowState: ReportMissingPetFlowState
    
    // MARK: - Coordinator Communication
    
    var onNext: (() -> Void)?
    var onBack: (() -> Void)?
    
    // MARK: - Initialization
    
    init(flowState: ReportMissingPetFlowState) {
        self.flowState = flowState
    }
    
    // MARK: - Actions
    
    func handleNext() {
        // TODO: Save form data in future implementation
        onNext?()
    }
    
    func handleBack() {
        onBack?()
    }
}
```

**Note**: For now, ViewModels are minimal. Add `@Published` properties and form logic in future iterations.

### Step 2: Create SwiftUI View (Empty Placeholder)

**File**: `/Views/NewStep/NewStepView.swift`

```swift
import SwiftUI

struct NewStepView: View {
    @ObservedObject var viewModel: NewStepViewModel
    
    var body: some View {
        VStack {
            Spacer()
            
            // TODO: Add screen content in future iteration
            Text("Step Placeholder")
                .font(.title)
                .foregroundColor(.gray)
            
            Spacer()
            
            // Continue button at bottom
            Button(action: viewModel.handleNext) {
                Text(L10n.Common.continue)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color(hex: "#155DFC"))
                    .foregroundColor(.white)
                    .cornerRadius(10)
            }
            .padding()
            .accessibilityIdentifier("newStep.continueButton")
        }
        .background(Color.white)
    }
}
```

**Current Scope**: Empty placeholder + Continue button only. Form content added later.

### Step 3: Add Navigation in Coordinator

**File**: `/Coordinators/ReportMissingPetCoordinator.swift`

```swift
private func navigateToNewStep() {
    let viewModel = NewStepViewModel(flowState: flowState!)
    
    viewModel.onNext = { [weak self] in
        self?.navigateToNextScreen()
    }
    
    viewModel.onBack = { [weak self] in
        self?.navigationController?.popViewController(animated: true)
    }
    
    let view = NewStepView(viewModel: viewModel)
    let hostingController = UIHostingController(
        rootView: NavigationBackHiding { view }
    )
    
    hostingController.title = L10n.ReportMissingPet.NewStep.navigationTitle
    configureProgressIndicator(hostingController: hostingController, step: 3, total: 5)  // Update step number
    configureCustomBackButton(hostingController: hostingController, action: { [weak viewModel] in
        viewModel?.handleBack()
    })
    
    navigationController?.pushViewController(hostingController, animated: true)
}
```

### Step 4: Add Property to ReportMissingPetFlowState (Future Implementation)

**File**: `/Models/ReportMissingPetFlowState.swift`

```swift
class ReportMissingPetFlowState: ObservableObject {
    // ... existing properties ...
    
    @Published var newStepData: String?  // TODO: Use in future implementation
    
    func clear() {
        // ... existing clears ...
        newStepData = nil
    }
}
```

**Note**: FlowState properties defined but not actively used yet. They will be populated when form logic is implemented.

---

## How to Test ViewModels

### Unit Test Example

**File**: `/iosAppTests/Features/ReportMissingPet/Views/ChipNumberViewModelTests.swift`

```swift
import XCTest
@testable import iosApp

@MainActor
final class ChipNumberViewModelTests: XCTestCase {
    var flowState: ReportMissingPetFlowState!
    var viewModel: ChipNumberViewModel!
    
    override func setUp() {
        super.setUp()
        flowState = ReportMissingPetFlowState()
        viewModel = ChipNumberViewModel(flowState: flowState)
    }
    
    override func tearDown() {
        viewModel = nil
        flowState = nil
        super.tearDown()
    }
    
    func testHandleNext_shouldTriggerOnNextCallback() {
        // Given
        var nextCalled = false
        viewModel.onNext = { nextCalled = true }
        
        // When
        viewModel.handleNext()
        
        // Then
        XCTAssertTrue(nextCalled)
    }
    
    func testHandleBack_shouldTriggerOnBackCallback() {
        // Given
        var backCalled = false
        viewModel.onBack = { backCalled = true }
        
        // When
        viewModel.handleBack()
        
        // Then
        XCTAssertTrue(backCalled)
    }
    
    func testInit_shouldStoreFlowStateReference() {
        // Given/When
        let vm = ChipNumberViewModel(flowState: flowState)
        
        // Then
        XCTAssertNotNil(vm)  // ViewModel initialized successfully
    }
}
```

### Run Tests

```bash
# Run all iOS tests
xcodebuild test \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 15' \
  -enableCodeCoverage YES

# View coverage report in Xcode:
# Product → Show Build Folder in Finder → Coverage → open .xccovreport
```

---

## How to Run E2E Tests

### Prerequisites

- Appium server running
- iOS simulator or device
- Node.js installed

### Setup

```bash
cd e2e-tests
npm install
```

### Run E2E Tests (iOS)

```bash
# From repo root
npm run test:mobile:ios

# Or from e2e-tests directory
npx wdio run wdio.conf.ts --spec ./mobile/specs/017-ios-missing-pet-flow.spec.ts
```

### E2E Test Structure

**File**: `/e2e-tests/mobile/specs/017-ios-missing-pet-flow.spec.ts`

```typescript
import { expect } from '@wdio/globals';
import { animalListScreen, chipNumberScreen, photoScreen } from '../screens/ReportMissingPetScreens';

describe('Missing Pet Report Flow', () => {
    it('should navigate through all 5 screens', async () => {
        // Given: User is on animal list
        await animalListScreen.waitForDisplayed();
        
        // When: User taps "report missing animal"
        await animalListScreen.tapReportMissingButton();
        
        // Then: Chip number screen displays with progress 1/4
        await chipNumberScreen.waitForDisplayed();
        expect(await chipNumberScreen.getProgressText()).toBe('1/4');
        
        // When: User taps Next
        await chipNumberScreen.tapNext();
        
        // Then: Photo screen displays with progress 2/4
        await photoScreen.waitForDisplayed();
        expect(await photoScreen.getProgressText()).toBe('2/4');
        
        // ... continue for all screens
    });
});
```

---

## Common Tasks

### Update Progress Indicator Styling

**File**: `/Coordinators/ReportMissingPetCoordinator.swift`

```swift
private func configureProgressIndicator(...) {
    let badgeSize: CGFloat = 40  // Change size
    badgeView.backgroundColor = UIColor(hex: "#155DFC")  // Change color
    label.font = UIFont.systemFont(ofSize: 12, weight: .bold)  // Change font
}
```

### Add Localized Strings

**File**: `/Resources/en.lproj/Localizable.strings`

```text
"report_missing.chip_number.title" = "Microchip Number";
"report_missing.chip_number.placeholder" = "00000-00000-00000";
```

**Regenerate SwiftGen**:
```bash
cd iosApp
swiftgen
```

**Usage**:
```swift
Text(L10n.ReportMissingPet.ChipNumber.title)
```

### Change Flow Order

Modify navigation methods in `ReportMissingPetCoordinator.swift`:

```swift
// Current order
start() → navigateToPhoto() → navigateToDescription() → navigateToContactDetails() → navigateToSummary()

// To change order, update onNext closures
viewModel.onNext = { [weak self] in
    self?.navigateToDescription()  // Skip photo screen
}
```

---

## Debugging Tips

### Check ReportMissingPetFlowState Values

Add breakpoint in ViewModel and inspect `flowState` object:

```swift
func handleNext() {
    print("ReportMissingPetFlowState: \(flowState)")  // Add breakpoint here
    flowState.chipNumber = ...
}
```

### Verify Navigation Bar Configuration

Add logging to coordinator navigation methods:

```swift
private func navigateToPhoto() {
    print("Navigating to Photo screen, step 2/4")
    // ... rest of method
}
```

### Test Modal Presentation

Run app in simulator and verify:
- Modal covers entire screen (`.fullScreen` style)
- Swipe gesture doesn't dismiss modal
- Back button on step 1 dismisses modal

---

## Architecture Patterns

### Pattern 1: ViewModel ↔ ReportMissingPetFlowState

```swift
// ViewModel reads from ReportMissingPetFlowState on init
init(flowState: ReportMissingPetFlowState) {
    self.flowState = flowState
    self.inputText = flowState.savedData ?? ""
}

// ViewModel writes to ReportMissingPetFlowState on next
func handleNext() {
    flowState.savedData = inputText
    onNext?()
}
```

### Pattern 2: ViewModel ↔ Coordinator

```swift
// Coordinator sets closures
viewModel.onNext = { [weak self] in
    self?.navigateToNextScreen()
}

// ViewModel invokes closures
func handleNext() {
    onNext?()
}
```

### Pattern 3: Coordinator ↔ Parent

```swift
// Child notifies parent on completion
func exitFlow() {
    navigationController?.dismiss(animated: true) { [weak self] in
        guard let self = self else { return }
        self.parentCoordinator?.childDidFinish(self)
    }
}

// Parent removes child
func childDidFinish(_ child: CoordinatorInterface) {
    childCoordinators.removeAll { $0 === child }
}
```

---

## References

- **Spec**: [spec.md](./spec.md)
- **Plan**: [plan.md](./plan.md)
- **Research**: [research.md](./research.md)
- **Data Model**: [data-model.md](./data-model.md)
- **Constitution**: `/.specify/memory/constitution.md` (v2.3.0, iOS MVVM-C section)
- **Example Feature**: `/iosApp/iosApp/Features/PetDetails/` (branch 012)

---

## Next Steps

1. Read [data-model.md](./data-model.md) for detailed data structures
2. Review [research.md](./research.md) for technical decisions
3. Start implementation with coordinator setup
4. Add screens one by one (chip number → photo → description → contact → summary)
5. Write unit tests for each ViewModel (80% coverage target)
6. Write E2E tests for user flows
7. Test on multiple device sizes (iPhone SE, iPhone 15 Pro Max)

