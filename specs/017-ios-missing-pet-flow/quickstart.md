# Quickstart: Missing Pet Report Flow (iOS)

**Feature**: 017-ios-missing-pet-flow  
**Date**: 2025-11-26  
**Purpose**: Developer onboarding guide for working with the flow

---

## Overview

The Missing Pet Report Flow is a modal, multi-step UI flow for iOS that collects information about a missing pet across 5 screens (4 data collection + 1 summary). It uses MVVM-C architecture with a dedicated coordinator managing flow navigation and state.

**Key Concepts**:
- **Modal Presentation**: Flow has own `UINavigationController`, presented modally
- **Child Coordinator**: `ReportMissingPetCoordinator` managed by parent `AnimalListCoordinator`
- **Shared State**: `FlowState` (ObservableObject) injected into all ViewModels
- **Progress Indicator**: Custom circular badge (1/4, 2/4, 3/4, 4/4) in navigation bar
- **Custom Back Button**: Chevron-left button with conditional behavior (exit on step 1, pop on steps 2-5)

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
3. Coordinator creates `FlowState`
4. Coordinator pushes first screen (chip number)
5. User navigates through steps
6. On exit, coordinator dismisses modal and cleans up
7. Parent removes child coordinator

---

## Project Structure

```text
iosApp/iosApp/Features/ReportMissing/
├── Coordinators/
│   └── ReportMissingPetCoordinator.swift      # Flow navigation logic
├── Models/
│   └── FlowState.swift                        # Shared state object
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

### Step 1: Create ViewModel

**File**: `/Views/NewStep/NewStepViewModel.swift`

```swift
import Foundation

@MainActor
class NewStepViewModel: ObservableObject {
    // MARK: - Published State
    
    @Published var inputText: String = ""
    
    // MARK: - Dependencies
    
    private let flowState: FlowState
    
    // MARK: - Coordinator Communication
    
    var onNext: (() -> Void)?
    var onBack: (() -> Void)?
    
    // MARK: - Initialization
    
    init(flowState: FlowState) {
        self.flowState = flowState
        
        // Restore state if returning
        if let saved = flowState.newStepData {
            self.inputText = saved
        }
    }
    
    // MARK: - Actions
    
    func handleNext() {
        // Save to flow state
        flowState.newStepData = inputText.isEmpty ? nil : inputText
        onNext?()
    }
    
    func handleBack() {
        onBack?()
    }
}
```

### Step 2: Create SwiftUI View

**File**: `/Views/NewStep/NewStepView.swift`

```swift
import SwiftUI

struct NewStepView: View {
    @ObservedObject var viewModel: NewStepViewModel
    
    var body: some View {
        VStack(spacing: 20) {
            Text(L10n.ReportMissing.NewStep.title)
                .font(.largeTitle)
            
            TextField(L10n.ReportMissing.NewStep.placeholder, text: $viewModel.inputText)
                .textFieldStyle(.roundedBorder)
                .accessibilityIdentifier("newStep.input.field")
            
            Button(action: viewModel.handleNext) {
                Text(L10n.Common.next)
            }
            .accessibilityIdentifier("newStep.next.tap")
        }
        .padding()
    }
}
```

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
    
    hostingController.title = L10n.ReportMissing.NewStep.navigationTitle
    configureProgressIndicator(hostingController: hostingController, step: 3, total: 5)  // Update step number
    configureCustomBackButton(hostingController: hostingController, action: { [weak viewModel] in
        viewModel?.handleBack()
    })
    
    navigationController?.pushViewController(hostingController, animated: true)
}
```

### Step 4: Add Property to FlowState

**File**: `/Models/FlowState.swift`

```swift
class FlowState: ObservableObject {
    // ... existing properties ...
    
    @Published var newStepData: String?
    
    func clear() {
        // ... existing clears ...
        newStepData = nil
    }
}
```

---

## How to Test ViewModels

### Unit Test Example

**File**: `/iosAppTests/Features/ReportMissing/Views/ChipNumberViewModelTests.swift`

```swift
import XCTest
@testable import iosApp

@MainActor
final class ChipNumberViewModelTests: XCTestCase {
    var flowState: FlowState!
    var viewModel: ChipNumberViewModel!
    
    override func setUp() {
        super.setUp()
        flowState = FlowState()
        viewModel = ChipNumberViewModel(flowState: flowState)
    }
    
    override func tearDown() {
        viewModel = nil
        flowState = nil
        super.tearDown()
    }
    
    func testHandleNext_whenChipNumberEntered_shouldSaveToFlowState() {
        // Given
        viewModel.chipNumberInput = "12345-67890-12345"
        var nextCalled = false
        viewModel.onNext = { nextCalled = true }
        
        // When
        viewModel.handleNext()
        
        // Then
        XCTAssertEqual(flowState.chipNumber, "123456789012345")  // Digits only
        XCTAssertTrue(nextCalled)
    }
    
    func testHandleNext_whenChipNumberEmpty_shouldSaveNilToFlowState() {
        // Given
        viewModel.chipNumberInput = ""
        var nextCalled = false
        viewModel.onNext = { nextCalled = true }
        
        // When
        viewModel.handleNext()
        
        // Then
        XCTAssertNil(flowState.chipNumber)
        XCTAssertTrue(nextCalled)
    }
    
    func testInit_whenFlowStateHasChipNumber_shouldRestoreInput() {
        // Given
        flowState.chipNumber = "123456789012345"
        
        // When
        let vm = ChipNumberViewModel(flowState: flowState)
        
        // Then
        XCTAssertEqual(vm.chipNumberInput, "12345-67890-12345")  // Formatted
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
Text(L10n.ReportMissing.ChipNumber.title)
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

### Check FlowState Values

Add breakpoint in ViewModel and inspect `flowState` object:

```swift
func handleNext() {
    print("FlowState: \(flowState)")  // Add breakpoint here
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

### Pattern 1: ViewModel ↔ FlowState

```swift
// ViewModel reads from FlowState on init
init(flowState: FlowState) {
    self.flowState = flowState
    self.inputText = flowState.savedData ?? ""
}

// ViewModel writes to FlowState on next
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

