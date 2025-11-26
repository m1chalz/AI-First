# Data Model: Missing Pet Report Flow (iOS)

**Feature**: 017-ios-missing-pet-flow  
**Date**: 2025-11-26  
**Purpose**: Define data structures for modal multi-step flow

---

## Flow State

### ReportMissingPetFlowState (ObservableObject)

Shared state object owned by `ReportMissingPetCoordinator`, passed to all ViewModels.

```swift
import Foundation
import UIKit

/// Shared state for Missing Pet Report flow.
/// Owned by ReportMissingPetCoordinator and injected into all ViewModels.
/// Persists data during forward/backward navigation within active session.
class ReportMissingPetFlowState: ObservableObject {
    // MARK: - Step 1: Chip Number
    
    /// Microchip number (optional, formatted as 00000-00000-00000 for display)
    /// Stored as digits-only string (no dashes)
    @Published var chipNumber: String?
    
    // MARK: - Step 2: Photo
    
    /// Selected photo of the pet (optional)
    @Published var photo: UIImage?
    
    // MARK: - Step 3: Description
    
    /// Additional description about the pet (optional, multi-line)
    @Published var description: String?
    
    // MARK: - Step 4: Contact Details
    
    /// Owner's email address (optional)
    @Published var contactEmail: String?
    
    /// Owner's phone number (optional)
    @Published var contactPhone: String?
    
    // MARK: - Initialization
    
    init() {
        // All properties start as nil
    }
    
    // MARK: - Methods
    
    /// Clears all flow state (called when exiting flow)
    func clear() {
        chipNumber = nil
        photo = nil
        description = nil
        contactEmail = nil
        contactPhone = nil
    }
    
    // MARK: - Computed Properties (Validation)
    
    /// Returns true if chip number has been entered
    var hasChipNumber: Bool {
        guard let chip = chipNumber, !chip.isEmpty else { return false }
        return true
    }
    
    /// Returns true if photo has been selected
    var hasPhoto: Bool {
        photo != nil
    }
    
    /// Returns true if description has been entered
    var hasDescription: Bool {
        guard let desc = description, !desc.isEmpty else { return false }
        return true
    }
    
    /// Returns true if at least one contact method provided
    var hasContactInfo: Bool {
        let hasEmail = contactEmail != nil && !(contactEmail?.isEmpty ?? true)
        let hasPhone = contactPhone != nil && !(contactPhone?.isEmpty ?? true)
        return hasEmail || hasPhone
    }
    
    /// Returns formatted chip number with dashes (00000-00000-00000)
    /// Returns nil if chipNumber is nil or empty
    var formattedChipNumber: String? {
        guard let chip = chipNumber, !chip.isEmpty else { return nil }
        
        // Insert dashes at positions 5 and 10
        let digits = chip.filter { $0.isNumber }
        guard digits.count >= 5 else { return digits }
        
        var formatted = ""
        for (index, char) in digits.enumerated() {
            if index == 5 || index == 10 {
                formatted.append("-")
            }
            formatted.append(char)
        }
        
        return formatted
    }
}
```

### Field Descriptions

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `chipNumber` | `String?` | No | Microchip number (digits only, no dashes). Max 15 digits. |
| `photo` | `UIImage?` | No | Pet photo selected via PHPickerViewController. |
| `description` | `String?` | No | Additional description/notes about the pet. Multi-line text. |
| `contactEmail` | `String?` | No | Owner's email for contact. |
| `contactPhone` | `String?` | No | Owner's phone number for contact. |

### Validation Rules

- All fields optional (per spec requirements)
- No validation enforced in ReportMissingPetFlowState (UI may add visual feedback)
- Future features may add required field validation

---

## ViewModel State Patterns

**NOTE**: This implementation creates MINIMAL ViewModels for navigation skeleton only. Form fields and business logic will be added in future iterations.

### Pattern: Minimal ViewModel (Navigation Only)

All ViewModels follow this minimal pattern for now:

```swift
@MainActor
class ChipNumberViewModel: ObservableObject {
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
        // For now: just trigger navigation
        // TODO: Save form data to flowState in future implementation
        onNext?()
    }
    
    func handleBack() {
        onBack?()
    }
}
```

**Future additions (not in this scope):**
- `@Published` properties for form inputs
- Input validation logic
- Formatting helpers
- State restoration from FlowState

---

## View Examples (Minimal Placeholders)

### Empty Screen with Continue Button

All 5 screens follow this minimal pattern:

```swift
import SwiftUI

struct ChipNumberView: View {
    @ObservedObject var viewModel: ChipNumberViewModel
    
    var body: some View {
        VStack {
            Spacer()
            
            // TODO: Add chip number input field in future iteration
            Text("Chip Number Screen")
                .font(.title)
                .foregroundColor(.gray)
            
            Spacer()
            
            // Continue button at bottom
            Button(action: viewModel.handleNext) {
                Text(L10n.Common.continue)
                    .font(.system(size: 16))
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 16)
                    .background(Color(hex: "#155DFC"))
                    .cornerRadius(10)
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 30)
            .accessibilityIdentifier("chipNumber.continue.tap")
        }
        .background(Color.white)
    }
}
```

**Pattern applies to**:
- `ChipNumberView` → "Chip Number Screen"
- `PhotoView` → "Photo Screen"  
- `DescriptionView` → "Description Screen"
- `ContactDetailsView` → "Contact Details Screen"
- `SummaryView` → "Summary Screen"

Screen-specific content will be added in subsequent features.

**All other ViewModels** (Photo, Description, ContactDetails) follow the same minimal pattern:
- Only `onNext` and `onBack` callbacks
- No form logic
- No @Published properties (yet)

Photo picker, input fields, and validation will be added in future iterations.

Summary screen ViewModel follows same minimal pattern. Display of collected data will be implemented in future iteration.

---

## Navigation Events

### Coordinator Callbacks

ViewModels communicate with coordinator via closures (no enum needed for simple flow).

```swift
// Chip Number (Step 1)
viewModel.onNext = { [weak self] in
    self?.navigateToPhoto()
}
viewModel.onBack = { [weak self] in
    self?.exitFlow()  // Dismiss modal
}

// Photo (Step 2)
viewModel.onNext = { [weak self] in
    self?.navigateToDescription()
}
viewModel.onBack = { [weak self] in
    self?.navigationController?.popViewController(animated: true)
}
viewModel.onSelectPhoto = { [weak self] in
    self?.presentPhotoPicker(viewModel: viewModel)
}

// Description (Step 3)
viewModel.onNext = { [weak self] in
    self?.navigateToContactDetails()
}
viewModel.onBack = { [weak self] in
    self?.navigationController?.popViewController(animated: true)
}

// Contact Details (Step 4)
viewModel.onNext = { [weak self] in
    self?.navigateToSummary()
}
viewModel.onBack = { [weak self] in
    self?.navigationController?.popViewController(animated: true)
}

// Summary (Step 5)
viewModel.onSubmit = { [weak self] in
    // Placeholder - no backend in this feature
    self?.exitFlow()
}
viewModel.onBack = { [weak self] in
    self?.navigationController?.popViewController(animated: true)
}
```

---

## Progress Indicator Model

### ProgressIndicatorConfig

Configuration for progress badge in navigation bar.

```swift
struct ProgressIndicatorConfig {
    let step: Int
    let total: Int
    let backgroundColor: String  // Hex color
    let textColor: String        // Hex color
    
    static let standard = ProgressIndicatorConfig(
        step: 1,
        total: 4,
        backgroundColor: "#155DFC",
        textColor: "#FFFFFF"
    )
    
    func with(step: Int) -> ProgressIndicatorConfig {
        ProgressIndicatorConfig(
            step: step,
            total: self.total,
            backgroundColor: self.backgroundColor,
            textColor: self.textColor
        )
    }
    
    var displayText: String {
        "\(step)/\(total)"
    }
}
```

**Usage**:
```swift
let config = ProgressIndicatorConfig.standard.with(step: 2)
configureProgressIndicator(hostingController, config: config)
```

---

## Testing Support

### Mock ReportMissingPetFlowState for Unit Tests

```swift
class MockReportMissingPetFlowState: ReportMissingPetFlowState {
    var clearCalled: Bool = false
    
    override func clear() {
        clearCalled = true
        super.clear()
    }
}
```

### Example Unit Test (Minimal Scope)

```swift
func testChipNumberViewModel_whenNextTapped_shouldTriggerCallback() {
    // Given
    let flowState = ReportMissingPetFlowState()
    let viewModel = ChipNumberViewModel(flowState: flowState)
    
    var nextCalled = false
    viewModel.onNext = { nextCalled = true }
    
    // When
    viewModel.handleNext()
    
    // Then
    XCTAssertTrue(nextCalled)
}

func testChipNumberViewModel_whenBackTapped_shouldTriggerCallback() {
    // Given
    let flowState = ReportMissingPetFlowState()
    let viewModel = ChipNumberViewModel(flowState: flowState)
    
    var backCalled = false
    viewModel.onBack = { backCalled = true }
    
    // When
    viewModel.handleBack()
    
    // Then
    XCTAssertTrue(backCalled)
}
```

**Note**: Form validation and data persistence tests will be added when form logic is implemented.

---

## Summary

| Entity | Purpose | Lifecycle |
|--------|---------|-----------|
| `ReportMissingPetFlowState` | Shared state skeleton (properties defined, unused for now) | Created by coordinator as property, cleared on exit |
| `ChipNumberViewModel` | Step 1 navigation callbacks (minimal) | Created per screen, references ReportMissingPetFlowState |
| `PhotoViewModel` | Step 2 navigation callbacks (minimal) | Created per screen, references ReportMissingPetFlowState |
| `DescriptionViewModel` | Step 3 navigation callbacks (minimal) | Created per screen, references ReportMissingPetFlowState |
| `ContactDetailsViewModel` | Step 4 navigation callbacks (minimal) | Created per screen, references ReportMissingPetFlowState |
| `SummaryViewModel` | Step 5 navigation callbacks (minimal) | Created per screen, references ReportMissingPetFlowState |
| `ProgressIndicatorConfig` | Progress badge styling | Value type, created per screen |

**Note**: This is navigation skeleton only. Form logic, validation, and data persistence will be added in future iterations.

---

## Open Questions (for future features)

1. Should ReportMissingPetFlowState persist across app restarts (e.g., via UserDefaults)?
2. Should we validate email/phone format in ReportMissingPetFlowState or leave to backend?
3. Should photo be compressed before storing in ReportMissingPetFlowState?
4. How to handle very long descriptions (character limit)?

