# Data Model: Missing Pet Report Flow (iOS)

**Feature**: 017-ios-missing-pet-flow  
**Date**: 2025-11-26  
**Purpose**: Define data structures for modal multi-step flow

---

## Flow State

### FlowState (ObservableObject)

Shared state object owned by `ReportMissingPetCoordinator`, passed to all ViewModels.

```swift
import Foundation
import UIKit

/// Shared state for Missing Pet Report flow.
/// Owned by ReportMissingPetCoordinator and injected into all ViewModels.
/// Persists data during forward/backward navigation within active session.
class FlowState: ObservableObject {
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
- No validation enforced in FlowState (UI may add visual feedback)
- Future features may add required field validation

---

## ViewModel State Patterns

Each ViewModel manages local UI state and communicates with FlowState.

### Pattern 1: Simple State with @Published Properties

Used for most screens (chip number, description, contact details).

```swift
@MainActor
class ChipNumberViewModel: ObservableObject {
    // MARK: - Published State
    
    /// Local input for chip number (formatted with dashes for display)
    @Published var chipNumberInput: String = ""
    
    /// Focus state for text field
    @Published var isTextFieldFocused: Bool = false
    
    // MARK: - Dependencies
    
    private let flowState: FlowState
    
    // MARK: - Coordinator Communication
    
    var onNext: (() -> Void)?
    var onBack: (() -> Void)?
    
    // MARK: - Initialization
    
    init(flowState: FlowState) {
        self.flowState = flowState
        
        // Restore state if returning to screen
        if let savedChipNumber = flowState.chipNumber {
            self.chipNumberInput = formatChipNumber(savedChipNumber)
        }
    }
    
    // MARK: - Actions
    
    func handleNext() {
        // Save to flow state (digits only, remove dashes)
        let digits = chipNumberInput.filter { $0.isNumber }
        flowState.chipNumber = digits.isEmpty ? nil : digits
        
        // Trigger navigation
        onNext?()
    }
    
    func handleBack() {
        onBack?()
    }
    
    // MARK: - Formatting (in ViewModel, not View!)
    
    /// Formats chip number as 00000-00000-00000
    private func formatChipNumber(_ input: String) -> String {
        let digits = input.filter { $0.isNumber }
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

### Pattern 2: Photo Screen with Image Handling

```swift
@MainActor
class PhotoViewModel: ObservableObject {
    // MARK: - Published State
    
    /// Preview of selected photo (from FlowState)
    @Published var selectedPhoto: UIImage?
    
    // MARK: - Dependencies
    
    private let flowState: FlowState
    
    // MARK: - Coordinator Communication
    
    var onNext: (() -> Void)?
    var onBack: (() -> Void)?
    var onSelectPhoto: (() -> Void)?  // Triggers photo picker in coordinator
    
    // MARK: - Initialization
    
    init(flowState: FlowState) {
        self.flowState = flowState
        
        // Observe flow state photo changes
        self.selectedPhoto = flowState.photo
    }
    
    // MARK: - Actions
    
    func handleSelectPhoto() {
        onSelectPhoto?()
    }
    
    func handleNext() {
        // Flow state already updated by coordinator via PHPickerViewController delegate
        onNext?()
    }
    
    func handleBack() {
        onBack?()
    }
}
```

### Pattern 3: Summary Screen (Read-Only)

```swift
@MainActor
class SummaryViewModel: ObservableObject {
    // MARK: - Dependencies
    
    private let flowState: FlowState
    
    // MARK: - Coordinator Communication
    
    var onSubmit: (() -> Void)?  // Placeholder for future backend submission
    var onBack: (() -> Void)?
    
    // MARK: - Initialization
    
    init(flowState: FlowState) {
        self.flowState = flowState
    }
    
    // MARK: - Computed Properties (for display)
    
    var chipNumberText: String {
        flowState.formattedChipNumber ?? L10n.ReportMissing.Summary.notProvided
    }
    
    var photoImage: UIImage? {
        flowState.photo
    }
    
    var descriptionText: String {
        flowState.description ?? L10n.ReportMissing.Summary.notProvided
    }
    
    var contactEmailText: String {
        flowState.contactEmail ?? L10n.ReportMissing.Summary.notProvided
    }
    
    var contactPhoneText: String {
        flowState.contactPhone ?? L10n.ReportMissing.Summary.notProvided
    }
    
    // MARK: - Actions
    
    func handleSubmit() {
        // Placeholder - no backend integration in this feature
        print("Submit tapped (placeholder)")
        onSubmit?()
    }
    
    func handleBack() {
        onBack?()
    }
}
```

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

### Mock FlowState for Unit Tests

```swift
class MockFlowState: FlowState {
    var clearCalled: Bool = false
    
    override func clear() {
        clearCalled = true
        super.clear()
    }
}
```

### Example Unit Test

```swift
func testChipNumberViewModel_whenNextTapped_shouldSaveToFlowState() {
    // Given
    let flowState = FlowState()
    let viewModel = ChipNumberViewModel(flowState: flowState)
    viewModel.chipNumberInput = "12345-67890-12345"
    
    var nextCalled = false
    viewModel.onNext = { nextCalled = true }
    
    // When
    viewModel.handleNext()
    
    // Then
    XCTAssertEqual(flowState.chipNumber, "123456789012345")  // Digits only
    XCTAssertTrue(nextCalled)
}
```

---

## Summary

| Entity | Purpose | Lifecycle |
|--------|---------|-----------|
| `FlowState` | Shared state across all steps | Created by coordinator, cleared on exit |
| `ChipNumberViewModel` | Step 1 state + actions | Created per screen, references FlowState |
| `PhotoViewModel` | Step 2 state + actions | Created per screen, references FlowState |
| `DescriptionViewModel` | Step 3 state + actions | Created per screen, references FlowState |
| `ContactDetailsViewModel` | Step 4 state + actions | Created per screen, references FlowState |
| `SummaryViewModel` | Step 5 read-only display | Created per screen, references FlowState |
| `ProgressIndicatorConfig` | Progress badge styling | Value type, created per screen |

---

## Open Questions (for future features)

1. Should FlowState persist across app restarts (e.g., via UserDefaults)?
2. Should we validate email/phone format in FlowState or leave to backend?
3. Should photo be compressed before storing in FlowState?
4. How to handle very long descriptions (character limit)?

