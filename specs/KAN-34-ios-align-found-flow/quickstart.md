# Quickstart: iOS Align Found Flow (3-step)

**Branch**: `KAN-34-ios-align-found-flow` | **Date**: 2026-01-09

## Overview

This guide covers the implementation of the restructured 3-step Found Pet flow on iOS.

---

## Prerequisites

- Xcode with iOS 18+ SDK
- iPhone 16 Simulator
- SwiftGen installed (`swiftgen` command available)

---

## Quick Start

### 1. Checkout Branch

```bash
git checkout KAN-34-ios-align-found-flow
cd /Users/msz/dev/ai-first/AI-First
```

### 2. Build and Run

```bash
# Open in Xcode
open iosApp/iosApp.xcodeproj

# Or build from command line
xcodebuild build -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16'
```

### 3. Run Tests

```bash
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' -enableCodeCoverage YES
```

### 4. Regenerate SwiftGen

After modifying `Localizable.strings`:

```bash
cd iosApp && swiftgen
```

---

## Implementation Order

### Phase 1: Data Model Changes

1. **Update `FoundPetReportFlowState.swift`**
   - Add `caregiverPhoneNumber: String?`
   - Add `currentPhysicalAddress: String?`
   - Update `clear()` method

### Phase 2: Create Pet Details Screen (Step 2/3)

2. **Create `FoundPetPetDetailsView.swift`**
   - Combine fields from ChipNumber + AnimalDescription screens
   - Include: date, species, gender, location (required), collar data, race, age, description

3. **Create `FoundPetPetDetailsViewModel.swift`**
   - Validation logic with required location
   - Microchip formatting using `MicrochipNumberFormatter`
   - Flow state persistence

### Phase 3: Update Contact Information Screen (Step 3/3)

4. **Update `FoundPetContactDetailsView.swift`**
   - Add caregiver phone field
   - Add current address TextAreaView

5. **Update `FoundPetContactDetailsViewModel.swift`**
   - Handle new optional fields
   - Caregiver phone validation (when non-empty)
   - Do NOT send new fields to backend

### Phase 4: Restructure Coordinator

6. **Update `FoundPetReportCoordinator.swift`**
   - Change entry point: `navigateToPhoto()` instead of `navigateToChipNumber()`
   - Update step indicators: `/3` instead of `/4`
   - Add `navigateToPetDetails()` method
   - Rename `navigateToContactDetails()` → `navigateToContactInformation()`
   - Update navigation flow callbacks

### Phase 5: Update Photo Screen (Step 1/3)

7. **Update `FoundPetPhotoView.swift`**
   - Update heading and body text per FR-017
   
8. **Update `FoundPetPhotoViewModel.swift`**
   - Step indicator: 1/3

### Phase 6: Localization

9. **Update `Localizable.strings` (en + pl)**
   - Add new `reportFoundPet.*` strings
   - Run `swiftgen`

### Phase 7: Cleanup

10. **Delete obsolete files**
    - `FoundPetChipNumberView.swift`
    - `FoundPetChipNumberViewModel.swift`
    - `FoundPetAnimalDescriptionView.swift`
    - `FoundPetAnimalDescriptionViewModel.swift`
    - `FoundPetSummaryView.swift`
    - `FoundPetSummaryView+Constants.swift`
    - `FoundPetSummaryViewModel.swift`

### Phase 8: Tests

11. **Update/Create tests**
    - `FoundPetPetDetailsViewModelTests.swift` (new)
    - Update `FoundPetContactDetailsViewModelTests.swift`
    - Update `FoundPetPhotoViewModelTests.swift`
    - Delete obsolete test files

---

## Key Code Patterns

### Microchip Input Handling

```swift
// In FoundPetPetDetailsViewModel
@Published var collarData: String = ""

// Format for display, store digits-only
var formattedCollarData: String {
    MicrochipNumberFormatter.format(collarData)
}

func updateCollarData(_ newValue: String) {
    // Extract digits and limit to 15
    let digits = MicrochipNumberFormatter.extractDigits(newValue)
    collarData = String(digits.prefix(MicrochipNumberFormatter.maxDigits))
}
```

### Optional Phone Validation

```swift
// In FoundPetContactDetailsViewModel
private var isCaregiverPhoneValid: Bool {
    // Empty is valid (optional field)
    if caregiverPhone.isEmpty { return true }
    
    // Non-empty must have 7-11 digits
    let sanitized = caregiverPhone.filter { $0.isNumber || $0 == "+" }
    let digitCount = sanitized.filter { $0.isNumber }.count
    return digitCount >= 7 && digitCount <= 11
}
```

### Required Location Validation

```swift
// In FoundPetPetDetailsViewModel - reuse existing pattern from FoundPetAnimalDescriptionViewModel
// (coordinates are ALREADY required in both Missing and Found flows)
private func validateCoordinates() -> CoordinateValidationResult {
    // Both required - must be filled and valid
    let latTrimmed = latitude.trimmingCharacters(in: .whitespacesAndNewlines)
    let longTrimmed = longitude.trimmingCharacters(in: .whitespacesAndNewlines)
    
    var latError: String?
    var longError: String?
    
    if latTrimmed.isEmpty {
        latError = L10n.AnimalDescription.Error.missingLatitude
    } else if let latValue = Double(latTrimmed), !(-90...90).contains(latValue) {
        latError = L10n.AnimalDescription.Error.invalidLatitude
    }
    
    if longTrimmed.isEmpty {
        longError = L10n.AnimalDescription.Error.missingLongitude
    } else if let longValue = Double(longTrimmed), !(-180...180).contains(longValue) {
        longError = L10n.AnimalDescription.Error.invalidLongitude
    }
    
    if latError != nil || longError != nil {
        return .invalid(latError: latError, longError: longError)
    }
    return .valid
}
```

---

## Coordinator Navigation Flow

```
start() ──► navigateToPhoto() (1/3)
                   │
                   │ onNext
                   ▼
            navigateToPetDetails() (2/3)
                   │
                   │ onContinue
                   ▼
            navigateToContactInformation() (3/3)
                   │
                   │ onReportSent (successful submission)
                   ▼
              exitFlow()
                   
Note: Summary screen REMOVED - flow exits immediately after successful submission
```

---

## Testing Checklist

### Unit Tests

- [ ] `FoundPetPetDetailsViewModel` - all validation cases
- [ ] `FoundPetContactDetailsViewModel` - caregiver phone validation
- [ ] `FoundPetPhotoViewModel` - step indicator update

### Manual QA

- [ ] 3-step flow completes (photo → pet details → contact info → exit)
- [ ] Collar data formats as `00000-00000-00000`
- [ ] Collar data accepts 1-14 digits without blocking Continue
- [ ] Location fields required - blocks Continue if empty
- [ ] Caregiver phone validates only when non-empty
- [ ] Current address allows multiline, max 500 chars
- [ ] Back navigation preserves all entered data
- [ ] Cancel/exit clears all state
- [ ] Submit succeeds without caregiver/address fields sent to backend

---

## Files Reference

### Modified
- `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Models/FoundPetReportFlowState.swift`
- `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Coordinators/FoundPetReportCoordinator.swift`
- `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/Photo/FoundPetPhotoView.swift`
- `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/Photo/FoundPetPhotoViewModel.swift`
- `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/ContactDetails/FoundPetContactDetailsView.swift`
- `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/ContactDetails/FoundPetContactDetailsViewModel.swift`
- `iosApp/iosApp/Resources/en.lproj/Localizable.strings`
- `iosApp/iosApp/Resources/pl.lproj/Localizable.strings`

### Created
- `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/PetDetails/FoundPetPetDetailsView.swift`
- `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/PetDetails/FoundPetPetDetailsViewModel.swift`
- `iosApp/iosAppTests/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/PetDetails/FoundPetPetDetailsViewModelTests.swift`

### Deleted
- `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/ChipNumber/` (folder)
- `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/AnimalDescription/` (folder)
- `iosApp/iosApp/Features/ReportMissingAndFoundPet/ReportFoundPet/Views/Summary/` (folder)
- Related test files

