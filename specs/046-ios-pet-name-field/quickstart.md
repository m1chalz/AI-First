# Quickstart: iOS - Add Pet Name Field to Animal Details Screen

**Feature**: 046-ios-pet-name-field  
**Estimated Time**: 2-3 hours (including tests)  
**Prerequisites**: Xcode, iOS Simulator or device, familiarity with SwiftUI and MVVM-C architecture

---

## Overview

Add an optional "Animal name" text field to the iOS Animal Details screen in the missing pet flow. The field will be positioned after "Date of disappearance" and before "Animal species" dropdown.

**Key Changes**:
1. Add `petName: String?` property to `ReportMissingPetFlowState`
2. Add `@Published var petName: String` to `AnimalDescriptionViewModel`
3. Add SwiftUI `TextField` to `AnimalDescriptionView`
4. Update unit tests to cover new property and logic
5. Extend E2E tests to verify pet name field

---

## Quick Implementation Steps

### 1. Update Flow State Model (2 minutes)

**File**: `/iosApp/iosApp/Features/ReportMissingPet/Models/ReportMissingPetFlowState.swift`

Add the optional `petName` property:

```swift
struct ReportMissingPetFlowState {
    // ... existing properties ...
    
    /// Name of the pet (optional). Nil if user didn't provide a name.
    var petName: String?
}
```

---

### 2. Update ViewModel (15 minutes)

**File**: `/iosApp/iosApp/Features/ReportMissingPet/ViewModels/AnimalDescriptionViewModel.swift`

**Step 2.1**: Add published property for TextField binding

```swift
@MainActor
class AnimalDescriptionViewModel: ObservableObject {
    // ... existing @Published properties ...
    
    @Published var petName: String = ""
    
    // ... rest of class ...
}
```

**Step 2.2**: Initialize `petName` from flow state in `init()`

```swift
init(flowState: ReportMissingPetFlowState) {
    // ... existing initialization ...
    
    self.petName = flowState.petName ?? ""
}
```

**Step 2.3**: Update flow state with trimmed pet name in `updateFlowState()` or similar method

```swift
func updateFlowState() {
    // ... existing flow state updates ...
    
    // Trim whitespace; if result is empty/whitespace-only, set to nil
    let trimmedPetName = petName.trimmingCharacters(in: .whitespacesAndNewlines)
    flowState.petName = trimmedPetName.isEmpty ? nil : trimmedPetName
}
```

**Step 2.4** (Optional): Add computed property for TextField model if project uses that pattern

```swift
var petNameTextFieldModel: ValidatedTextField.Model {
    ValidatedTextField.Model(
        text: $petName,
        placeholder: L10n.animalNamePlaceholder,  // "Animal name (optional)"
        accessibilityIdentifier: "animalDescription.animalNameTextField.input"
    )
}
```

---

### 3. Update SwiftUI View (10 minutes)

**File**: `/iosApp/iosApp/Features/ReportMissingPet/Views/AnimalDescription/AnimalDescriptionView.swift`

Add the TextField after "Date of disappearance" field and before "Animal species" dropdown:

```swift
struct AnimalDescriptionView: View {
    @ObservedObject var viewModel: AnimalDescriptionViewModel
    
    var body: some View {
        Form {
            // ... existing "Date of disappearance" field ...
            
            // NEW: Animal name text field
            Section {
                TextField(
                    L10n.animalNamePlaceholder,  // "Animal name (optional)"
                    text: $viewModel.petName
                )
                .textFieldStyle(.roundedBorder)
                .submitLabel(.next)
                .accessibilityIdentifier("animalDescription.animalNameTextField.input")
            } header: {
                Text(L10n.animalNameSectionHeader)  // "Animal name" or empty
            }
            
            // ... existing "Animal species" dropdown ...
            // ... rest of form ...
        }
    }
}
```

**Note**: If the project uses a custom `ValidatedTextField` component, use that instead:

```swift
ValidatedTextField(model: viewModel.petNameTextFieldModel)
```

---

### 4. Add Localized Strings (5 minutes)

**File**: `/iosApp/iosApp/Resources/en.lproj/Localizable.strings` (or SwiftGen strings file)

Add localization keys if not already present:

```
"animal_name_placeholder" = "Animal name (optional)";
"animal_name_section_header" = "Animal name";
```

Run SwiftGen to generate `L10n.swift` (if using SwiftGen):

```bash
cd iosApp
swiftgen
```

---

### 5. Write Unit Tests (30-45 minutes)

**File**: `/iosApp/iosAppTests/Features/ReportMissingPet/ViewModels/AnimalDescriptionViewModelTests.swift`

Add test cases for pet name handling:

```swift
import XCTest
@testable import iosApp

@MainActor
final class AnimalDescriptionViewModelTests: XCTestCase {
    
    // MARK: - Pet Name Tests
    
    func test_petName_whenUserEntersText_shouldUpdatePublishedProperty() {
        // Given
        let flowState = ReportMissingPetFlowState()
        let viewModel = AnimalDescriptionViewModel(flowState: flowState)
        
        // When
        viewModel.petName = "Max"
        
        // Then
        XCTAssertEqual(viewModel.petName, "Max")
    }
    
    func test_petName_whenFlowStateHasPetName_shouldInitializeProperty() {
        // Given
        var flowState = ReportMissingPetFlowState()
        flowState.petName = "Max"
        
        // When
        let viewModel = AnimalDescriptionViewModel(flowState: flowState)
        
        // Then
        XCTAssertEqual(viewModel.petName, "Max")
    }
    
    func test_petName_whenFlowStateHasNoPetName_shouldInitializeToEmptyString() {
        // Given
        let flowState = ReportMissingPetFlowState()  // petName = nil
        
        // When
        let viewModel = AnimalDescriptionViewModel(flowState: flowState)
        
        // Then
        XCTAssertEqual(viewModel.petName, "")
    }
    
    func test_updateFlowState_whenPetNameHasText_shouldStoreTrimmedValue() {
        // Given
        let flowState = ReportMissingPetFlowState()
        let viewModel = AnimalDescriptionViewModel(flowState: flowState)
        viewModel.petName = "  Max  "
        
        // When
        viewModel.updateFlowState()
        
        // Then
        XCTAssertEqual(viewModel.flowState.petName, "Max")
    }
    
    func test_updateFlowState_whenPetNameIsEmpty_shouldStoreNil() {
        // Given
        let flowState = ReportMissingPetFlowState()
        let viewModel = AnimalDescriptionViewModel(flowState: flowState)
        viewModel.petName = ""
        
        // When
        viewModel.updateFlowState()
        
        // Then
        XCTAssertNil(viewModel.flowState.petName)
    }
    
    func test_updateFlowState_whenPetNameIsWhitespaceOnly_shouldStoreNil() {
        // Given
        let flowState = ReportMissingPetFlowState()
        let viewModel = AnimalDescriptionViewModel(flowState: flowState)
        viewModel.petName = "   "
        
        // When
        viewModel.updateFlowState()
        
        // Then
        XCTAssertNil(viewModel.flowState.petName)
    }
}
```

Run tests to verify:

```bash
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES
```

---

### 6. Extend E2E Tests (30-45 minutes)

**Step 6.1**: Update Screen Object Model

**File**: `/e2e-tests/mobile/screens/AnimalDescriptionScreen.ts`

Add locator for the pet name text field:

```typescript
import { $ } from '@wdio/globals';

class AnimalDescriptionScreen {
    // ... existing locators ...
    
    get petNameTextField() {
        return $('~animalDescription.animalNameTextField.input');
    }
    
    // ... existing methods ...
    
    async enterPetName(petName: string) {
        await this.petNameTextField.waitForDisplayed();
        await this.petNameTextField.setValue(petName);
    }
    
    async getPetNameValue(): Promise<string> {
        await this.petNameTextField.waitForDisplayed();
        return await this.petNameTextField.getText();
    }
}

export default new AnimalDescriptionScreen();
```

**Step 6.2**: Update Step Definitions

**File**: `/e2e-tests/mobile/steps/reportMissingPet.steps.ts`

Add step for entering pet name:

```typescript
import { Given, When, Then } from '@cucumber/cucumber';
import AnimalDescriptionScreen from '../screens/AnimalDescriptionScreen';

// ... existing steps ...

When('the user enters {string} as the animal name', async (petName: string) => {
    await AnimalDescriptionScreen.enterPetName(petName);
});

Then('the animal name field should display {string}', async (expectedName: string) => {
    const actualName = await AnimalDescriptionScreen.getPetNameValue();
    expect(actualName).toBe(expectedName);
});
```

**Step 6.3**: Extend Feature File

**File**: `/e2e-tests/mobile/specs/report-missing-pet-flow.spec.ts` (or `.feature` file if using Gherkin)

Add scenarios for pet name field:

```gherkin
@ios @missing-pet-flow
Feature: Report Missing Pet with Pet Name

  Scenario: User enters pet name and creates announcement
    Given the user is on the Animal Details screen
    When the user enters "Max" as the animal name
    And the user enters valid animal details
    And the user taps "Continue"
    And the user completes the flow
    Then the announcement should be created successfully
    And the announcement should include pet name "Max"

  Scenario: User leaves pet name empty and creates announcement
    Given the user is on the Animal Details screen
    When the user leaves the animal name field empty
    And the user enters valid animal details
    And the user taps "Continue"
    And the user completes the flow
    Then the announcement should be created successfully
    And the announcement should not include a pet name

  Scenario: Pet name persists when navigating back and forward
    Given the user is on the Animal Details screen
    When the user enters "Max" as the animal name
    And the user taps "Back"
    And the user taps "Continue" to return to Animal Details
    Then the animal name field should display "Max"
```

Run E2E tests:

```bash
npm run test:mobile:ios -- --spec e2e-tests/mobile/specs/report-missing-pet-flow.spec.ts
```

---

## Verification Checklist

- [ ] **Flow State**: `ReportMissingPetFlowState` has `var petName: String?` property
- [ ] **ViewModel**: `AnimalDescriptionViewModel` has `@Published var petName: String` property
- [ ] **ViewModel Initialization**: `petName` loaded from `flowState.petName ?? ""`
- [ ] **ViewModel Update**: `petName` trimmed and stored in `flowState.petName` (nil if empty)
- [ ] **View**: TextField added with correct placeholder and accessibility identifier
- [ ] **Localization**: Strings added to `Localizable.strings` and SwiftGen regenerated
- [ ] **Unit Tests**: 80%+ coverage for pet name logic (6+ test cases)
- [ ] **E2E Tests**: 3+ scenarios covering happy path, empty field, and persistence
- [ ] **Manual Test**: Launch iOS app, enter pet name, submit, verify in backend response

---

## Common Issues & Solutions

### Issue 1: TextField binding not updating ViewModel

**Solution**: Ensure ViewModel property is `@Published var petName: String` (not optional) and TextField uses `$viewModel.petName` binding.

### Issue 2: Pet name not persisting when navigating back

**Solution**: Verify `updateFlowState()` is called before navigation and `init(flowState:)` loads `flowState.petName ?? ""`.

### Issue 3: Whitespace not trimmed before API submission

**Solution**: Check that `trimmingCharacters(in: .whitespacesAndNewlines)` is called in `updateFlowState()` or API submission method.

### Issue 4: E2E test can't find TextField

**Solution**: Verify accessibility identifier is set to `"animalDescription.animalNameTextField.input"` and matches Screen Object locator.

### Issue 5: Unit tests fail with MainActor warnings

**Solution**: Mark test methods with `@MainActor` since `AnimalDescriptionViewModel` is `@MainActor`.

---

## Next Steps

After implementation:

1. **Manual Testing**: Test on iOS Simulator and real device
2. **Code Review**: Request review from iOS team
3. **Merge**: Merge to main branch after approval
4. **Documentation**: Update user-facing documentation (if applicable)
5. **Monitor**: Check backend logs for `petName` field usage

---

## References

- **Feature Spec**: [spec.md](./spec.md)
- **Data Model**: [data-model.md](./data-model.md)
- **API Contract**: [contracts/announcements-api.yaml](./contracts/announcements-api.yaml)
- **Research**: [research.md](./research.md)
- **Constitution**: `/.specify/memory/constitution.md` (iOS MVVM-C Architecture section)

---

## Estimated Breakdown

| Task | Time | Notes |
|------|------|-------|
| Update flow state model | 2 min | Add one property |
| Update ViewModel | 15 min | Add property, init, update logic |
| Update SwiftUI view | 10 min | Add TextField with styling |
| Add localized strings | 5 min | Add 2 localization keys |
| Write unit tests | 30-45 min | 6+ test cases |
| Extend E2E tests | 30-45 min | Update screen object, steps, scenarios |
| Manual testing | 15 min | Test on simulator |
| **Total** | **~2-3 hours** | Including all tasks |

---

**Ready to start?** Follow steps 1-6 in order. If you encounter issues, consult the "Common Issues & Solutions" section.

