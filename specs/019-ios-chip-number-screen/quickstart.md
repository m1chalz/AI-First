# Quick Start: iOS Chip Number Screen

**Feature Branch**: `019-ios-chip-number-screen`  
**Date**: 2025-11-27  
**Target**: iOS 15+

**Feature Status**: ✅ **Flow infrastructure exists** - we're completing the Chip Number screen (Step 1/4) by adding TextField UI and formatting logic to existing skeleton.

## Prerequisites

- macOS with Xcode 14+ installed
- iOS 15+ Simulator or physical device
- Swift 5.9+
- SwiftGen installed (for localization)

---

## Project Setup

### 1. Open iOS Project

```bash
cd /Users/msz/dev/ai-first/AI-First
open iosApp/iosApp.xcodeproj
```

### 2. Select Target and Device

- **Target**: `iosApp`
- **Device**: iPhone 15 Simulator (or any iOS 15+ device)

### 3. Build Project

- Press `Cmd + B` to build
- Verify no compilation errors

---

## Run Application

### Option 1: Run from Xcode

1. Select `iosApp` scheme
2. Select device/simulator
3. Press `Cmd + R` to run
4. Navigate to Pet List screen
5. Tap "Report Missing Pet" button (initiates flow)
6. **Step 1/4: Microchip Number Screen** should appear

### Option 2: Run from Terminal

```bash
cd /Users/msz/dev/ai-first/AI-First
xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -configuration Debug
```

---

## Run Tests

### Unit Tests

**Run all tests**:
```bash
cd /Users/msz/dev/ai-first/AI-First
xcodebuild test \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 15' \
  -enableCodeCoverage YES
```

**Run specific feature tests**:
```bash
xcodebuild test \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 15' \
  -only-testing:iosAppTests/MicrochipNumberViewModelTests \
  -enableCodeCoverage YES
```

**View coverage report**:
1. Open Xcode
2. Go to **Report Navigator** (Cmd + 9)
3. Select latest test run
4. Click **Coverage** tab
5. Verify `MicrochipNumberViewModel` has ≥ 80% coverage

### Run Tests from Xcode

1. Open `iosApp.xcodeproj`
2. Press `Cmd + U` to run all tests
3. Or press `Ctrl + Option + Cmd + U` to run last test class

---

## Verify Implementation

### Manual Test Checklist

1. **Launch app** → Pet List screen visible
2. **Tap "Report Missing Pet"** → Microchip Number screen (1/4) appears
3. **Navigation bar**:
   - [ ] Title: "Microchip number"
   - [ ] Progress: "1/4" displayed above title
   - [ ] Back button (chevron left) visible on left side
4. **Screen content**:
   - [ ] Heading: "Microchip number" (large text)
   - [ ] Description text visible (about microchip identification)
   - [ ] Text field labeled "Microchip number (optional)"
   - [ ] Placeholder: "00000-00000-00000"
   - [ ] Continue button at bottom
5. **Input formatting**:
   - [ ] Type "12345" → displays "12345" (no hyphen yet)
   - [ ] Type 6th digit → displays "12345-6" (first hyphen added)
   - [ ] Type 11th digit → displays "12345-67890-1" (second hyphen added)
   - [ ] Type 15 digits → displays "12345-67890-12345"
   - [ ] Attempt 16th digit → ignored (max 15 digits)
6. **Paste behavior**:
   - [ ] Paste "123456789012345" → displays "12345-67890-12345"
   - [ ] Paste "12345-67890-12345" → displays correctly
   - [ ] Paste "abc123def" → displays "12-3" (only digits extracted)
7. **Continue button**:
   - [ ] Always enabled (even when field empty)
   - [ ] Tap with empty field → navigates to step 2/4
   - [ ] Tap with data → navigates to step 2/4, data persists
8. **Back button**:
   - [ ] Tap back in nav bar → returns to Pet List
   - [ ] Flow dismissed, flow state cleared
9. **State persistence**:
   - [ ] Enter "12345-67890-12345", continue to step 2/4
   - [ ] Navigate back to step 1/4 → field still shows "12345-67890-12345"

---

## Project Structure (Relevant Files)

```
iosApp/iosApp/
├── Coordinators/
│   └── PetListCoordinator.swift              # Starts ReportMissingPetCoordinator
├── Features/
│   └── ReportMissingPet/
│       ├── Coordinators/
│       │   └── ReportMissingPetCoordinator.swift  # ✅ EXISTING - Complete (no changes)
│       ├── Models/
│       │   └── ReportMissingPetFlowState.swift    # ✅ EXISTING - Complete (no changes)
│       ├── Views/
│       │   ├── ChipNumberViewModel.swift          # ✅ EXISTING - Needs expansion
│       │   └── ChipNumberView.swift               # ✅ EXISTING - Needs expansion
│       └── Helpers/
│           └── MicrochipNumberFormatter.swift     # 🆕 NEW - To create
├── Views/
│   └── NavigationBackHiding.swift            # EXISTING - Wrapper to hide nav back button
├── Resources/
│   ├── en.lproj/
│   │   └── Localizable.strings               # UPDATED - New localization keys
│   └── pl.lproj/
│       └── Localizable.strings               # UPDATED - Polish translations
├── Generated/
│   └── Strings.swift                         # GENERATED - SwiftGen output
└── DI/
    └── ServiceContainer.swift                # UPDATED - No changes (no repos needed)

iosAppTests/
└── Features/
    └── ReportMissingPet/
        ├── Views/
        │   └── ChipNumberViewModelTests.swift     # ✅ EXISTING - Needs expansion
        └── Helpers/
            └── MicrochipNumberFormatterTests.swift # 🆕 NEW - To create
```

---

## Localization Setup

### 1. Add Localization Keys

**File**: `iosApp/iosApp/Resources/en.lproj/Localizable.strings`

```
"microchip_number.title" = "Microchip number";
"microchip_number.progress" = "1/4";
"microchip_number.heading" = "Microchip number";
"microchip_number.description" = "Microchip identification is the most efficient way to reunite with your pet. If your pet has been microchipped and you know the microchip number, please enter it here.";
"microchip_number.field_label" = "Microchip number (optional)";
"microchip_number.field_placeholder" = "00000-00000-00000";
"microchip_number.continue_button" = "Continue";
```

### 2. Generate SwiftGen Code

```bash
cd /Users/msz/dev/ai-first/AI-First/iosApp
swiftgen config run --config swiftgen.yml
```

**Verify**:
- Check `iosApp/iosApp/Generated/Strings.swift` updated
- Verify `L10n.MicrochipNumber.title` etc. available in code

### 3. Polish Localization (Optional)

**File**: `iosApp/iosApp/Resources/pl.lproj/Localizable.strings`

```
"microchip_number.title" = "Numer czipa";
"microchip_number.progress" = "1/4";
"microchip_number.heading" = "Numer czipa";
"microchip_number.description" = "Identyfikacja mikroczipem to najskuteczniejszy sposób na odnalezienie zwierzęcia. Jeśli Twój pupil ma wszczepiony mikroczip i znasz jego numer, wprowadź go tutaj.";
"microchip_number.field_label" = "Numer mikroczipa (opcjonalnie)";
"microchip_number.field_placeholder" = "00000-00000-00000";
"microchip_number.continue_button" = "Dalej";
```

---

## Troubleshooting

### Build Errors

**Error**: `Cannot find 'L10n' in scope`

**Solution**:
```bash
cd /Users/msz/dev/ai-first/AI-First/iosApp
swiftgen config run --config swiftgen.yml
```

**Error**: `No such module 'SwiftUI'`

**Solution**: Update deployment target to iOS 15+ in Xcode project settings

---

### Test Failures

**Error**: Coverage below 80%

**Solution**:
- Run tests with coverage: `xcodebuild test -enableCodeCoverage YES`
- Check coverage report in Xcode (Cmd + 9 → Coverage tab)
- Add missing test cases for untested branches

---

### Runtime Issues

**Issue**: Navigation bar shows default back button instead of custom

**Solution**:
- Verify `NavigationBackHiding` wrapper is used
- Check `leftBarButtonItem` is set in coordinator
- Ensure `hidesBackButton = true` is applied

**Issue**: Text field shows letters when using hardware keyboard

**Solution**:
- Verify `.keyboardType(.numberPad)` is applied
- Ensure `onChange(of:)` filters non-numeric characters
- Add software filtering: `input.filter { $0.isNumber }`

---

## Development Workflow

### 1. Make Changes

Edit relevant files:
- ViewModel: `/iosApp/iosApp/Features/ReportMissingPet/ViewModels/MicrochipNumberViewModel.swift`
- View: `/iosApp/iosApp/Features/ReportMissingPet/Views/MicrochipNumberView.swift`
- Coordinator: `/iosApp/iosApp/Coordinators/ReportMissingPetCoordinator.swift`
- Helper: `/iosApp/iosApp/Features/ReportMissingPet/Helpers/MicrochipNumberFormatter.swift`

### 2. Run Tests

```bash
xcodebuild test \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 15' \
  -only-testing:iosAppTests/MicrochipNumberViewModelTests
```

### 3. Verify Coverage

- Check coverage report in Xcode
- Ensure ≥ 80% line + branch coverage

### 4. Manual Test

- Run app in simulator
- Verify all acceptance criteria from spec.md

### 5. Commit Changes

```bash
git add .
git commit -m "[019-ios-chip-number-screen] Complete chip number screen implementation

- Created MicrochipNumberFormatter helper for formatting logic (stateless utility)
- Expanded ChipNumberViewModel with @Published chipNumber and formatting method
- Expanded ChipNumberView with TextField, heading, description, proper layout
- Added logic to save/restore chip number from flowState
- Expanded ChipNumberViewModelTests with formatting and state persistence tests
- Created MicrochipNumberFormatterTests with comprehensive test coverage
- Updated localization keys if needed

Decision logic:
- Formatting extracted to separate helper (single responsibility, testability, reusability)
- ViewModel uses flowState.chipNumber (digits-only storage, no hyphens)
- Formatting with onChange(of:) for natural cursor behavior
- Coordinator already handles navigation bar, progress indicator, modal presentation
- Manual DI via closures (onNext/onBack) - no changes to coordinator needed"
```

---

## Next Steps (Future Work)

1. **Step 2/4 Screen**: Last Seen Location and Date
2. **Step 3/4 Screen**: Pet Description
3. **Step 4/4 Screen**: Contact Information
4. **API Integration**: Submit flow data to backend `/server` API
5. **E2E Tests**: Appium tests for complete flow

---

## References

- **Feature Spec**: `/specs/019-ios-chip-number-screen/spec.md`
- **Research**: `/specs/019-ios-chip-number-screen/research.md`
- **Data Model**: `/specs/019-ios-chip-number-screen/data-model.md`
- **Constitution**: `.specify/memory/constitution.md` (Principle XI - iOS MVVM-C)
- **Full Flow Spec**: `/specs/017-ios-missing-pet-flow/` (4-step wizard context)

---

## Support

For questions or issues:
- Review feature spec: `/specs/019-ios-chip-number-screen/spec.md`
- Review architecture: `.specify/memory/constitution.md`
- Check existing iOS patterns: `/iosApp/iosApp/Features/*/` directories
- Review coordinator patterns: `/iosApp/iosApp/Coordinators/`
