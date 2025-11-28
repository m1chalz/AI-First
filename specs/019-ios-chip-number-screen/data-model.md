# Data Model: iOS Microchip Number Screen

**Feature Branch**: `019-ios-chip-number-screen`  
**Date**: 2025-11-27  
**Phase**: Phase 1 - Data Model Design

## Entity Overview

This feature works with two existing data structures and adds one new helper:

1. **ReportMissingPetFlowState** - Flow-level state shared across all 4 steps (✅ EXISTING)
2. **ChipNumberViewModel** - ViewModel with `@Published` properties (✅ EXISTING - needs expansion)
3. **MicrochipNumberFormatter** - Helper for formatting chip numbers (🆕 NEW - reusable utility)

**Note**: iOS MVVM pattern uses `@Published` properties directly in ViewModel - no separate UiState struct needed (that's an Android MVI pattern).

---

## 1. ReportMissingPetFlowState ✅ EXISTING

**Type**: `class` (reference type - IMPORTANT)  
**Location**: `/iosApp/iosApp/Features/ReportMissingPet/Models/ReportMissingPetFlowState.swift` ✅ **ALREADY EXISTS**  
**Purpose**: Shared mutable state for the entire 4-step "Report Missing Pet" flow  
**Lifecycle**: Owned by `ReportMissingPetCoordinator`, lifetime = duration of flow

### Properties

| Property | Type | Description | Constraints | Status |
|----------|------|-------------|-------------|--------|
| `chipNumber` | `String?` | Raw microchip number (digits only, no hyphens) | Optional, 15 digits max | ✅ EXISTING |
| `photo` | `UIImage?` | Step 2/4 - Pet photo | Optional | ✅ EXISTING |
| `description` | `String?` | Step 3/4 - Pet description | Optional | ✅ EXISTING |
| `contactEmail` | `String?` | Step 4/4 - Contact email | Optional | ✅ EXISTING |
| `contactPhone` | `String?` | Step 4/4 - Contact phone | Optional | ✅ EXISTING |

### Design Rationale

**Why reference type (class)?**
- Shared across multiple ViewModels (steps 1/4, 2/4, 3/4, 4/4)
- Mutations in one screen visible in all others
- Coordinator owns single instance, passes by reference
- Automatic cleanup when coordinator deallocates

**Why store digits only (no hyphens)?**
- Hyphens are presentation-only formatting
- Backend API (if any) expects raw digits
- Easier validation (just count digits, not deal with hyphens)
- Formatting applied in ViewModel when loading from flow state

### Swift Implementation (EXISTING - from `/iosApp/iosApp/Features/ReportMissingPet/Models/ReportMissingPetFlowState.swift`)

```swift
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
}
```

---

## 2. MicrochipNumberFormatter (Helper) 🆕 NEW

**Type**: `struct` (stateless utility)  
**Location**: `/iosApp/iosApp/Features/ReportMissingPet/Helpers/MicrochipNumberFormatter.swift`  
**Purpose**: Formats microchip numbers with hyphens (00000-00000-00000) and extracts raw digits

### Methods

| Method | Parameters | Returns | Purpose |
|--------|-----------|---------|---------|
| `format` | `input: String` | `String` | Formats raw digits with hyphens at positions 6 and 12, limits to 15 digits |
| `extractDigits` | `input: String` | `String` | Extracts only digits from input (removes hyphens, letters, special chars) |

### Swift Implementation

```swift
/// Formats microchip numbers for display and extracts raw digits for storage.
struct MicrochipNumberFormatter {
    
    /// Maximum number of digits allowed in a microchip number
    static let maxDigits = 15
    
    /// Formats raw digits with hyphens: 00000-00000-00000
    ///
    /// Examples:
    /// - "12345" → "12345"
    /// - "123456" → "12345-6"
    /// - "123456789012345" → "12345-67890-12345"
    /// - "1234567890123456789" → "12345-67890-12345" (truncated)
    ///
    /// - Parameter input: String containing digits (may include non-numeric characters)
    /// - Returns: Formatted string with hyphens at positions 6 and 12
    static func format(_ input: String) -> String {
        let digits = extractDigits(input)
        let limited = String(digits.prefix(maxDigits))
        
        var formatted = ""
        for (index, digit) in limited.enumerated() {
            if index == 5 || index == 10 {
                formatted.append("-")
            }
            formatted.append(digit)
        }
        
        return formatted
    }
    
    /// Extracts only numeric digits from input string.
    ///
    /// Examples:
    /// - "12345-67890-12345" → "123456789012345"
    /// - "abc123def456" → "123456"
    /// - "00000-00000-00000" → "000000000000000"
    ///
    /// - Parameter input: String that may contain digits, hyphens, letters, special chars
    /// - Returns: String containing only digits (0-9)
    static func extractDigits(_ input: String) -> String {
        return input.filter { $0.isNumber }
    }
}
```

### Test Cases

**Location**: `/iosApp/iosAppTests/Features/ReportMissingPet/Helpers/MicrochipNumberFormatterTests.swift`

```swift
final class MicrochipNumberFormatterTests: XCTestCase {
    
    // MARK: - format() Tests
    
    func testFormat_whenInputHasFewerThan5Digits_shouldReturnUnformatted() {
        // Given
        let input = "1234"
        
        // When
        let result = MicrochipNumberFormatter.format(input)
        
        // Then
        XCTAssertEqual(result, "1234")
    }
    
    func testFormat_whenInputHas6Digits_shouldAddFirstHyphen() {
        // Given
        let input = "123456"
        
        // When
        let result = MicrochipNumberFormatter.format(input)
        
        // Then
        XCTAssertEqual(result, "12345-6")
    }
    
    func testFormat_whenInputHas11Digits_shouldAddSecondHyphen() {
        // Given
        let input = "12345678901"
        
        // When
        let result = MicrochipNumberFormatter.format(input)
        
        // Then
        XCTAssertEqual(result, "12345-67890-1")
    }
    
    func testFormat_whenInputHas15Digits_shouldFormatCorrectly() {
        // Given
        let input = "123456789012345"
        
        // When
        let result = MicrochipNumberFormatter.format(input)
        
        // Then
        XCTAssertEqual(result, "12345-67890-12345")
    }
    
    func testFormat_whenInputHasMoreThan15Digits_shouldTruncateTo15() {
        // Given
        let input = "123456789012345678"
        
        // When
        let result = MicrochipNumberFormatter.format(input)
        
        // Then
        XCTAssertEqual(result, "12345-67890-12345")
    }
    
    func testFormat_whenInputHasHyphens_shouldReformat() {
        // Given
        let input = "12345-67890-12345"
        
        // When
        let result = MicrochipNumberFormatter.format(input)
        
        // Then
        XCTAssertEqual(result, "12345-67890-12345")
    }
    
    func testFormat_whenInputHasLetters_shouldExtractDigitsOnly() {
        // Given
        let input = "abc123def456ghi"
        
        // When
        let result = MicrochipNumberFormatter.format(input)
        
        // Then
        XCTAssertEqual(result, "12345-6")
    }
    
    func testFormat_whenInputIsEmpty_shouldReturnEmpty() {
        // Given
        let input = ""
        
        // When
        let result = MicrochipNumberFormatter.format(input)
        
        // Then
        XCTAssertEqual(result, "")
    }
    
    // MARK: - extractDigits() Tests
    
    func testExtractDigits_whenInputHasOnlyDigits_shouldReturnUnchanged() {
        // Given
        let input = "123456789012345"
        
        // When
        let result = MicrochipNumberFormatter.extractDigits(input)
        
        // Then
        XCTAssertEqual(result, "123456789012345")
    }
    
    func testExtractDigits_whenInputHasHyphens_shouldRemoveThem() {
        // Given
        let input = "12345-67890-12345"
        
        // When
        let result = MicrochipNumberFormatter.extractDigits(input)
        
        // Then
        XCTAssertEqual(result, "123456789012345")
    }
    
    func testExtractDigits_whenInputHasLetters_shouldRemoveThem() {
        // Given
        let input = "abc123def456"
        
        // When
        let result = MicrochipNumberFormatter.extractDigits(input)
        
        // Then
        XCTAssertEqual(result, "123456")
    }
    
    func testExtractDigits_whenInputHasSpecialChars_shouldRemoveThem() {
        // Given
        let input = "123!@#456$%^789"
        
        // When
        let result = MicrochipNumberFormatter.extractDigits(input)
        
        // Then
        XCTAssertEqual(result, "123456789")
    }
    
    func testExtractDigits_whenInputIsEmpty_shouldReturnEmpty() {
        // Given
        let input = ""
        
        // When
        let result = MicrochipNumberFormatter.extractDigits(input)
        
        // Then
        XCTAssertEqual(result, "")
    }
}
```

---

## 3. ChipNumberViewModel ✅ EXISTING (needs expansion)

**Type**: `class` conforming to `ObservableObject`  
**Location**: `/iosApp/iosApp/Features/ReportMissingPet/Views/ChipNumberViewModel.swift` ✅ **SKELETON EXISTS**  
**Purpose**: Presentation logic and state management for chip number screen

**Current Status**: Skeleton implementation with only navigation callbacks. **Needs expansion** to add:
- `@Published var chipNumber: String` property for formatted display
- `formatChipNumber(_ input: String)` method using `MicrochipNumberFormatter`
- Logic to save/restore from `flowState.chipNumber`

### Published Properties (to add)

| Property | Type | Description | Publisher | Status |
|----------|------|-------------|-----------|--------|
| `chipNumber` | `String` | Formatted chip number for display | `@Published` | 🆕 TO ADD |

### Dependencies

| Dependency | Type | Purpose | Status |
|------------|------|---------|--------|
| `flowState` | `ReportMissingPetFlowState` | Reference to shared flow state | ✅ EXISTS |
| `onNext` | `(() -> Void)?` | Callback to coordinator - navigate to step 2/4 | ✅ EXISTS |
| `onBack` | `(() -> Void)?` | Callback to coordinator - dismiss entire flow | ✅ EXISTS |

### Methods

| Method | Parameters | Returns | Purpose | Status |
|--------|-----------|---------|---------|--------|
| `init` | `flowState` | - | Initialize with flow state | ✅ EXISTS |
| `handleNext` | - | `Void` | Triggers `onNext?()` navigation | ✅ EXISTS |
| `handleBack` | - | `Void` | Triggers `onBack?()` navigation | ✅ EXISTS |
| `formatChipNumber` | `input: String` | `Void` | Formats input with hyphens, updates `@Published` property | 🆕 TO ADD |

### Swift Implementation

**EXISTING CODE** (current skeleton):
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
    
    /// Navigate to next screen (Photo).
    /// TODO: Save chip number to flowState in future implementation.
    func handleNext() {
        onNext?()
    }
    
    /// Navigate back (exit flow from step 1).
    func handleBack() {
        onBack?()
    }
}
```

**EXPANDED CODE** (what we need to add):
```swift
@MainActor
class ChipNumberViewModel: ObservableObject {
    // MARK: - Published State (🆕 ADD THIS)
    
    @Published var chipNumber: String = ""
    
    // MARK: - Dependencies
    
    private let flowState: ReportMissingPetFlowState
    
    // MARK: - Coordinator Communication
    
    var onNext: (() -> Void)?
    var onBack: (() -> Void)?
    
    // MARK: - Initialization
    
    init(flowState: ReportMissingPetFlowState) {
        self.flowState = flowState
        
        // 🆕 ADD: Restore state if user navigates back to this screen
        if let savedDigits = flowState.chipNumber {
            self.chipNumber = MicrochipNumberFormatter.format(savedDigits)
        }
    }
    
    // MARK: - Public Methods (🆕 ADD THIS)
    
    /// Formats input with hyphens and updates published property.
    /// Called from view's `onChange(of:)` modifier.
    func formatChipNumber(_ input: String) {
        chipNumber = MicrochipNumberFormatter.format(input)
    }
    
    // MARK: - Actions
    
    /// Navigate to next screen (Photo).
    /// 🆕 MODIFY: Save chip number to flowState before navigating.
    func handleNext() {
        let digits = MicrochipNumberFormatter.extractDigits(chipNumber)
        flowState.chipNumber = digits.isEmpty ? nil : digits
        onNext?()
    }
    
    /// Navigate back (exit flow from step 1).
    func handleBack() {
        onBack?()
    }
}
```

---

## 4. ReportMissingPetCoordinator ✅ EXISTING (complete)

**Type**: `class` conforming to `CoordinatorInterface`  
**Location**: `/iosApp/iosApp/Features/ReportMissingPet/Coordinators/ReportMissingPetCoordinator.swift` ✅ **FULLY IMPLEMENTED**  
**Purpose**: Manages navigation and lifecycle for 4-step "Report Missing Pet" flow + Summary screen

**Status**: **Complete implementation** - NO CHANGES NEEDED for this feature. The coordinator already:
- Creates modal `UINavigationController` with `.fullScreen` presentation
- Owns `ReportMissingPetFlowState` instance
- Configures navigation bar (title, progress indicator, custom back/dismiss buttons)
- Manages navigation between 5 screens (4 steps + summary)
- Wraps views in `NavigationBackHiding`
- Handles flow exit and cleanup

### Key Implementation Details (from existing code)

The coordinator already handles:
- **Navigation bar configuration**: Title, progress indicator (1/4), custom dismiss button (X icon)
- **ViewM odel initialization**: Passes `flowState` and sets `onNext`/`onBack` callbacks
- **View wrapping**: `UIHostingController(rootView: NavigationBackHiding { view })`
- **Flow lifecycle**: Modal presentation, cleanup on exit, `flowState.clear()`

**Coordinator methods already used**:
- `navigateToChipNumber()` - Shows chip number screen (step 1/4)
- `navigateToPhoto()` - Shows photo screen (step 2/4)
- `navigateToDescription()` - Shows description screen (step 3/4)
- `navigateToContactDetails()` - Shows contact details screen (step 4/4)
- `navigateToSummary()` - Shows summary screen (step 5)
- `exitFlow()` - Dismisses modal and cleans up

**Helper methods**:
- `configureProgressIndicator(hostingController:step:total:)` - Adds "1/4" label to nav bar
- `configureCustomBackButton(hostingController:action:)` - Adds chevron-left button
- `configureCustomDismissButton(hostingController:action:)` - Adds X button (step 1 only)

---

## Validation Rules

| Field | Required | Min Length | Max Length | Format | Error Message |
|-------|----------|------------|------------|--------|---------------|
| Chip Number | No | 0 | 15 digits | Digits only (0-9) | N/A (field is optional, no validation) |

**Note**: Since the field is optional and Continue button is always enabled, there are NO validation rules or error messages for this screen. The field accepts any length from 0-15 digits.

---

## State Persistence

### Within Flow Session (In-Memory)

- **Storage**: `ReportMissingPetFlowState` (owned by coordinator)
- **Lifetime**: Duration of flow (from step 1/4 start to completion/cancellation)
- **Behavior**:
  - User enters "12345-67890-12345" on step 1/4 → saved as "123456789012345" (digits only)
  - User continues to step 2/4 → microchip number persists in flow state
  - User navigates back to step 1/4 → input field pre-populated with "12345-67890-12345"
  - User cancels flow → flow state reset, memory deallocated

### Across App Sessions (NOT Persisted)

- Flow state does NOT persist across app restarts
- If user force-quits app during flow, data is lost
- Rationale: This is a transient wizard flow, not a draft/autosave feature

---

## Testing Strategy

### Unit Tests (XCTest)

**Location**: `/iosApp/iosAppTests/Features/ReportMissingPet/MicrochipNumberViewModelTests.swift`

**Test Cases**:

1. **Formatting Tests**:
   - `testFormatMicrochipNumber_whenEntering5Digits_shouldNotAddHyphen()`
   - `testFormatMicrochipNumber_whenEntering6Digits_shouldAddFirstHyphen()`
   - `testFormatMicrochipNumber_whenEntering11Digits_shouldAddSecondHyphen()`
   - `testFormatMicrochipNumber_whenEntering15Digits_shouldFormatCorrectly()`
   - `testFormatMicrochipNumber_whenEnteringMoreThan15Digits_shouldTruncateTo15()`
   - `testFormatMicrochipNumber_whenPastingFormattedString_shouldReformat()`

2. **State Persistence Tests**:
   - `testInit_whenFlowStateHasSavedNumber_shouldRestoreFormatted()`
   - `testContinue_whenMicrochipNumberEntered_shouldSaveDigitsOnlyToFlowState()`
   - `testContinue_whenFieldEmpty_shouldSaveNilToFlowState()`

3. **Navigation Tests**:
   - `testContinue_shouldCallOnContinueCallback()`
   - `testCancel_shouldCallOnCancelCallback()`

**Coverage Target**: 80% line + branch coverage (per constitution)

### Manual Testing Checklist

- [ ] Type "12345" → displays "12345"
- [ ] Type 6th digit → hyphen auto-inserted: "12345-6"
- [ ] Type 11th digit → second hyphen auto-inserted: "12345-67890-1"
- [ ] Type 15 digits → formatted: "12345-67890-12345"
- [ ] Attempt to type 16th digit → ignored (max 15 digits)
- [ ] Paste "123456789012345" → formatted: "12345-67890-12345"
- [ ] Paste "12345-67890-12345" → stays formatted correctly
- [ ] Paste "abc123def456" → only digits extracted: "12345-6"
- [ ] Delete all characters → field empty, Continue still enabled
- [ ] Tap Continue with empty field → navigates to step 2/4, flowState.microchipNumber = nil
- [ ] Tap Continue with "12345-67890-12345" → navigates to step 2/4, flowState.microchipNumber = "123456789012345"
- [ ] Navigate to step 2/4 then back → field pre-populated with previous value
- [ ] Tap back button in nav bar → returns to pet list, flow dismissed

---

## Localization Keys (SwiftGen)

**File**: `/iosApp/iosApp/Resources/en.lproj/Localizable.strings`

```
// Microchip Number Screen
"microchip_number.title" = "Microchip number";
"microchip_number.progress" = "1/4";
"microchip_number.heading" = "Microchip number";
"microchip_number.description" = "Microchip identification is the most efficient way to reunite with your pet. If your pet has been microchipped and you know the microchip number, please enter it here.";
"microchip_number.field_label" = "Microchip number (optional)";
"microchip_number.field_placeholder" = "00000-00000-00000";
"microchip_number.continue_button" = "Continue";
```

**Usage in Code**:
```swift
L10n.MicrochipNumber.title            // "Microchip number"
L10n.MicrochipNumber.progress         // "1/4"
L10n.MicrochipNumber.heading          // "Microchip number"
L10n.MicrochipNumber.description      // "Microchip identification is..."
L10n.MicrochipNumber.fieldLabel       // "Microchip number (optional)"
L10n.MicrochipNumber.fieldPlaceholder // "00000-00000-00000"
L10n.MicrochipNumber.continueButton   // "Continue"
```

---

## Dependencies

No external dependencies or repositories required for this screen:

- **Repositories**: None (UI-only screen, no data fetching)
- **Use Cases**: None (per iOS architecture - ViewModels don't use use cases)
- **Services**: None (no network calls, no persistence)
- **External Libraries**: None (only Foundation + SwiftUI + UIKit)

---

## Summary

**Feature Status**: This feature **builds on existing infrastructure** rather than creating from scratch.

### ✅ What Already Exists:
- `ReportMissingPetCoordinator` - Complete 4-step flow navigation (305 lines, fully functional)
- `ReportMissingPetFlowState` - Flow state with `chipNumber` property (`@Published`, class type)
- `ChipNumberViewModel` - Skeleton with navigation callbacks (40 lines)
- `ChipNumberView` - Placeholder with Continue button (38 lines)
- `ChipNumberViewModelTests` - Test file structure
- `NavigationBackHiding` - Reusable wrapper for hiding nav back button

### 🆕 What Needs to Be Implemented:
1. **MicrochipNumberFormatter** helper (stateless utility, ~30-40 lines)
2. **Expand ChipNumberViewModel**: Add `@Published var chipNumber`, `formatChipNumber()`, save/restore logic (~20 lines added)
3. **Expand ChipNumberView**: Replace placeholder with TextField, heading, description, proper layout (~60-80 lines)
4. **Expand ChipNumberViewModelTests**: Add formatting tests, state persistence tests (~150 lines)
5. **MicrochipNumberFormatterTests**: Complete test suite (~200 lines)

### Architecture Compliance:
- ✅ MVVM-C: Coordinator manages navigation, ViewModel has presentation logic, View is pure SwiftUI
- ✅ Flow state as reference type owned by coordinator
- ✅ Manual DI via closures (`onNext`/`onBack`)
- ✅ Data formatting in helper (not ViewModel or View)
- ✅ SwiftGen for localization
- ✅ 80% test coverage target

### Next Steps:
See `tasks.md` (Phase 2) for detailed implementation tasks.
