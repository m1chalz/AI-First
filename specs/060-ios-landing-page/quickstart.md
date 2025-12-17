# Quickstart: iOS Landing Page - Top Panel

**Feature Branch**: `060-ios-landing-page`  
**Date**: 2025-12-17  
**Platform**: iOS  
**Status**: Ready for Implementation

---

## Overview

This quickstart guide helps developers set up and implement the iOS Landing Page top panel feature. Follow these steps to add the hero section and list header row above the existing Home list.

---

## Prerequisites

### Required Tools
- **Xcode**: 15.0+ (includes Swift 5.0+ compiler)
- **iOS Simulator**: iPhone 15 (iOS 18.0+) or physical device
- **swiftgen**: For localization string generation
  ```bash
  brew install swiftgen
  ```

### Required Knowledge
- Swift 5.0
- SwiftUI basics (VStack, Button, Text)
- iOS MVVM-C architecture pattern
- XCTest for unit testing

---

## Project Setup

### 1. Check Out Feature Branch

```bash
cd /Users/msz/dev/ai-first/AI-First
git checkout 060-ios-landing-page
```

### 2. Open iOS Project in Xcode

```bash
cd iosApp
open iosApp.xcodeproj
```

Or open directly in Xcode:
- **File → Open** → Navigate to `/iosApp/iosApp.xcodeproj`

### 3. Verify Build Compiles

Build the project to ensure no existing issues:
- **Product → Build** (⌘B)
- Select target: `iosApp`
- Select simulator: iPhone 15 (iOS 18.0)

Expected result: Build succeeds ✅

---

## File Structure

### New Files to Create (6 files)

```text
iosApp/iosApp/Features/LandingPage/Views/Components/
├── HeroPanel_Model.swift              # Presentation model for hero section
├── HeroPanel.swift                    # SwiftUI view for hero section
├── ListHeaderRow_Model.swift          # Presentation model for list header
└── ListHeaderRow.swift                # SwiftUI view for list header

iosApp/iosAppTests/Features/LandingPage/Views/Components/
├── HeroPanel_ModelTests.swift         # Unit tests for HeroPanel_Model
└── ListHeaderRow_ModelTests.swift     # Unit tests for ListHeaderRow_Model
```

### Existing Files to Modify (4 files)

```text
iosApp/iosApp/Features/LandingPage/Views/
├── LandingPageView.swift              # Add VStack with hero + list header
└── LandingPageViewModel.swift         # Add tab navigation closure properties

iosApp/iosApp/Features/LandingPage/Coordinators/
└── HomeCoordinator.swift              # Add tab navigation closure properties

iosApp/iosApp/Coordinators/
└── TabCoordinator.swift               # Refactor showPetDetailsFromHome, add switchToFoundPetTab
```

### Files to Keep Unchanged (per feature spec)

```text
iosApp/iosApp/Features/LandingPage/Views/
└── LandingPageViewModel.swift         # NO changes - list logic remains the same
```

---

## Implementation Steps

### Step 1: Add Localization Strings

**File**: `/iosApp/iosApp/Resources/en.lproj/Localizable.strings`

Add the following keys:

```swift
/* Landing Page Hero Section */
"LandingPage.Hero.title" = "Find Your Pet";
"LandingPage.Hero.lostPetButton" = "Lost Pet";
"LandingPage.Hero.foundPetButton" = "Found Pet";

/* Landing Page List Header */
"LandingPage.ListHeader.title" = "Recent Reports";
"LandingPage.ListHeader.viewAll" = "View All";
```

### Step 2: Generate L10n Code

Run swiftgen to generate `L10n.*` accessors:

```bash
cd iosApp
swiftgen
```

Verify: Open `/iosApp/iosApp/Generated/Strings.swift` - you should see new keys.

### Step 3: Create Presentation Models

Create **`HeroPanel_Model.swift`** and **`ListHeaderRow_Model.swift`** following the structure in `data-model.md`.

**Tip**: Look at existing presentation models for reference:
- `/iosApp/iosApp/Features/LandingPage/Views/EmptyStateView_Model.swift`
- `/iosApp/iosApp/Features/ReportMissingPet/Views/AnimalDescription/Components/CoordinateInputView_Model.swift`

### Step 4: Create SwiftUI Views

Create **`HeroPanel.swift`** and **`ListHeaderRow.swift`**.

**Pattern**: Pure SwiftUI views that observe presentation models.

**Example structure**:
```swift
import SwiftUI

struct HeroPanel: View {
    let model: HeroPanel_Model
    
    var body: some View {
        VStack(spacing: 16) {
            Text(model.title)
                .font(.system(size: 20))
                .foregroundColor(Color(hex: "#101828"))
                .accessibilityIdentifier(model.titleAccessibilityId)
            
            HStack(spacing: 12) {
                // Lost Pet button (red, with alert icon)
                Button(action: model.onLostPetTap) {
                    HStack(spacing: 8) {
                        if let icon = model.lostPetButtonIcon {
                            Image(systemName: icon)
                                .font(.system(size: 20))
                        }
                        Text(model.lostPetButtonTitle)
                            .font(.system(size: 16))
                    }
                    .foregroundColor(.white)
                    .padding(.horizontal, 20)
                    .padding(.vertical, 16)
                    .background(Color(hex: "#FB2C36"))
                    .cornerRadius(16)
                    .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 4)
                }
                .accessibilityIdentifier(model.lostPetButtonAccessibilityId)
                
                // Found Pet button (blue, with checkmark icon)
                Button(action: model.onFoundPetTap) {
                    HStack(spacing: 8) {
                        if let icon = model.foundPetButtonIcon {
                            Image(systemName: icon)
                                .font(.system(size: 20))
                        }
                        Text(model.foundPetButtonTitle)
                            .font(.system(size: 16))
                    }
                    .foregroundColor(.white)
                    .padding(.horizontal, 20)
                    .padding(.vertical, 16)
                    .background(Color(hex: "#155DFC"))
                    .cornerRadius(16)
                    .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 4)
                }
                .accessibilityIdentifier(model.foundPetButtonAccessibilityId)
            }
        }
        .padding(.horizontal, 74)
        .padding(.vertical, 24)
        .background(
            LinearGradient(
                gradient: Gradient(colors: [Color(hex: "#EFF6FF"), Color(hex: "#E0E7FF")]),
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
        )
    }
}

#Preview {
    HeroPanel(model: HeroPanel_Model())
}
```

**Note**: This example includes the gradient background and button styling from Figma design (node 974:4669). Icons use SF Symbols (`exclamationmark.triangle`, `checkmark`).

### Step 5: Modify LandingPageView

**File**: `/iosApp/iosApp/Features/LandingPage/Views/LandingPageView.swift`

Add VStack with hero panel + list header above existing `AnnouncementCardsListView`:

```swift
struct LandingPageView: View {
    @ObservedObject var viewModel: LandingPageViewModel
    
    var body: some View {
        VStack(spacing: 0) {
            // NEW: Hero panel (fixed height)
            HeroPanel(model: HeroPanel_Model(
                onLostPetTap: { /* TODO: Set closure from coordinator */ },
                onFoundPetTap: { /* TODO: Set closure from coordinator */ }
            ))
            
            // NEW: List header row (fixed height)
            ListHeaderRow(model: ListHeaderRow_Model(
                onActionTap: { /* TODO: Set closure from coordinator */ }
            ))
            .padding(.top, 16)  // Gap between hero and list header
            
            // EXISTING: Announcement cards list (takes remaining space)
            // IMPORTANT: .frame(maxHeight: .infinity) needed because AnnouncementCardsListView
            // has its own ScrollView inside - it needs explicit height to work properly
            AnnouncementCardsListView(
                viewModel: viewModel.listViewModel,
                emptyStateModel: viewModel.emptyStateModel,
                listAccessibilityId: viewModel.listAccessibilityId
            )
            .frame(maxHeight: .infinity)  // Takes all remaining vertical space
        }
        .task {
            await viewModel.loadData()
        }
        .alert(
            L10n.Location.Permission.Popup.title,
            isPresented: $viewModel.showPermissionDeniedAlert,
            actions: {
                Button(L10n.Location.Permission.Popup.Settings.button) {
                    viewModel.openSettings()
                }
                .accessibilityIdentifier("startup.permissionPopup.goToSettings")
                
                Button(L10n.Location.Permission.Popup.Cancel.button, role: .cancel) {
                    viewModel.continueWithoutLocation()
                }
                .accessibilityIdentifier("startup.permissionPopup.cancel")
            },
            message: {
                Text(L10n.Location.Permission.Popup.message)
                    .accessibilityIdentifier("startup.permissionPopup.message")
            }
        )
    }
}
```

### Step 6: Add Tab Navigation Closures to HomeCoordinator

**File**: `/iosApp/iosApp/Features/LandingPage/Coordinators/HomeCoordinator.swift`

Add closure properties for tab navigation (set by `TabCoordinator`):

```swift
// MARK: - Tab Navigation Closures

/// Closure to switch to Lost Pet tab (without showing details).
/// Set by TabCoordinator during initialization.
var onSwitchToLostPetTab: (() -> Void)?

/// Closure to switch to Found Pet tab.
/// Set by TabCoordinator during initialization.
var onSwitchToFoundPetTab: (() -> Void)?
```

**File**: `/iosApp/iosApp/Coordinators/TabCoordinator.swift`

Refactor existing `showPetDetailsFromHome` method to support optional detail:

```swift
/// Switches to Lost Pet tab, optionally showing pet details.
/// - Parameter announcementId: Optional ID of announcement to show details for.
///   If nil, only switches tab. If provided, also shows pet details.
private func switchToLostPetTab(withAnnouncementId announcementId: String? = nil) {
    guard let (index, lostPetCoordinator) = findAnnouncementListCoordinator() else {
        print("Warning: Could not find AnnouncementListCoordinator for cross-tab navigation")
        return
    }
    
    _tabBarController.selectedIndex = index
    
    // Only show details if announcementId provided
    if let announcementId = announcementId {
        lostPetCoordinator.showPetDetails(for: announcementId)
    }
}

/// Switches to Found Pet tab.
private func switchToFoundPetTab() {
    // Find Found Pet tab index and switch
    // Implementation similar to switchToLostPetTab but for Found Pet coordinator
}
```

Update `HomeCoordinator` initialization in `TabCoordinator` to set closures:

```swift
let homeCoordinator = HomeCoordinator(
    repository: container.announcementRepository,
    locationHandler: container.locationHandler,
    onShowPetDetails: { [weak self] announcementId in
        self?.switchToLostPetTab(withAnnouncementId: announcementId)  // With detail
    }
)

// NEW: Set hero button navigation closures
homeCoordinator.onSwitchToLostPetTab = { [weak self] in
    self?.switchToLostPetTab(withAnnouncementId: nil)  // Without detail
}
homeCoordinator.onSwitchToFoundPetTab = { [weak self] in
    self?.switchToFoundPetTab()
}
```

**Note**: This reuses existing `showPetDetailsFromHome` pattern with optional parameter.

### Step 7: Wire Closures in LandingPageView

Update `LandingPageView.swift` to create presentation models with coordinator closures:

```swift
struct LandingPageView: View {
    @ObservedObject var viewModel: LandingPageViewModel
    
    var body: some View {
        VStack(spacing: 0) {
            // Hero panel with coordinator closures
            HeroPanel(model: HeroPanel_Model(
                onLostPetTap: {
                    viewModel.onSwitchToLostPetTab?()  // Closure set by coordinator
                },
                onFoundPetTap: {
                    viewModel.onSwitchToFoundPetTab?()  // Closure set by coordinator
                }
            ))
            
            // List header row with "View All" action
            ListHeaderRow(model: ListHeaderRow_Model(
                onActionTap: {
                    viewModel.onSwitchToLostPetTab?()  // "View All" → Lost Pet tab
                }
            ))
            .padding(.top, 16)
            
            // Existing list component
            AnnouncementCardsListView(...)
                .frame(maxHeight: .infinity)
        }
        .task {
            await viewModel.loadData()
        }
        // ... permission alert ...
    }
}
```

Add closure properties to `LandingPageViewModel`:

```swift
class LandingPageViewModel: ObservableObject {
    // ... existing properties ...
    
    /// Called when user taps "Lost Pet" button or "View All"
    var onSwitchToLostPetTab: (() -> Void)?
    
    /// Called when user taps "Found Pet" button
    var onSwitchToFoundPetTab: (() -> Void)?
    
    // ... rest of implementation ...
}
```

Update `HomeCoordinator.start()` to set these closures:

```swift
let viewModel = LandingPageViewModel(
    repository: repository,
    locationHandler: locationHandler,
    onAnnouncementTapped: onShowPetDetails
)

// Set tab navigation closures (set by TabCoordinator during HomeCoordinator init)
viewModel.onSwitchToLostPetTab = onSwitchToLostPetTab
viewModel.onSwitchToFoundPetTab = onSwitchToFoundPetTab

// ... rest of start() method ...
```

---

## Running the App

### Build and Run on Simulator

1. **Select target**: iosApp
2. **Select simulator**: iPhone 15 (iOS 18.0)
3. **Run**: Product → Run (⌘R)

### Verify Visual Appearance

1. App launches → Home tab is selected
2. **Hero panel** appears at the top:
   - Title: "Find Your Pet"
   - Two buttons: "Lost Pet" | "Found Pet"
3. **List header row** appears below hero:
   - Title: "Recent Reports"
   - Action: "View All"
4. **Announcement list** appears below list header (existing behavior)

### Test Navigation

1. Tap **"Lost Pet"** button → App switches to Lost Pet tab ✅
2. Tap **"Found Pet"** button → App switches to Found Pet tab ✅
3. Return to Home tab
4. Tap **"View All"** → App switches to Lost Pet tab (full announcements list) ✅

---

## Running Tests

### Unit Tests

Run unit tests for presentation models:

```bash
# From command line
cd iosApp
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -only-testing:iosAppTests/Features/LandingPage/Views/Components
```

Or in Xcode:
- **Product → Test** (⌘U)
- Or run specific test files: `HeroPanel_ModelTests`, `ListHeaderRow_ModelTests`

**Coverage target**: 80% line + branch coverage

Generate coverage report:
```bash
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES
```

View coverage in Xcode:
- **Product → Show Test Coverage** (⌘9 → Coverage tab)

### E2E Tests

Run Appium tests for iOS (from Java E2E project):

```bash
cd e2e-tests/java
mvn test -Dtest=IosTestRunner
```

View E2E test report:
- Open `e2e-tests/java/target/cucumber-reports/ios/index.html` in browser

---

## Debugging Tips

### Issue: Localization strings not found (build error)

**Solution**:
1. Verify strings added to `Localizable.strings`
2. Run swiftgen: `cd iosApp && swiftgen`
3. Clean build folder: Product → Clean Build Folder (⌘⇧K)
4. Rebuild: Product → Build (⌘B)

### Issue: Hero panel not visible

**Solution**:
1. Verify `LandingPageView` has VStack with all three components
2. Check SwiftUI preview: Canvas → Show Preview (⌥⌘↩)
3. Add background colors to debug layout:
   ```swift
   HeroPanel(model: heroModel)
       .background(Color.red.opacity(0.3))  // Debug: red tint
   ```

### Issue: Tab navigation not working

**Solution**:
1. Verify `HomeCoordinator` has `onSwitchToTab` closure property
2. Verify `TabCoordinator` sets this closure during initialization
3. Add `print()` statements in navigation methods to verify calls
4. Check Xcode console for any errors during tab switch

### Issue: Unit tests fail

**Solution**:
1. Verify test names follow Swift convention: `test{Description}_when{Condition}_should{ExpectedResult}`
2. Follow Given-When-Then structure (Principle VIII)
3. Run tests in isolation to identify failures
4. Check accessibility identifiers match FR-010 requirements

---

## Common Commands

### Build iOS app
```bash
cd iosApp
xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15'
```

### Run unit tests
```bash
cd iosApp
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15'
```

### Run unit tests with coverage
```bash
cd iosApp
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES
```

### Generate swiftgen code
```bash
cd iosApp
swiftgen
```

### Run E2E tests (iOS)
```bash
cd e2e-tests/java
mvn test -Dtest=IosTestRunner
```

### Clean build
```bash
cd iosApp
xcodebuild clean -scheme iosApp
```

---

## Useful Resources

### Project Documentation
- **Feature Spec**: `specs/060-ios-landing-page/spec.md`
- **Implementation Plan**: `specs/060-ios-landing-page/plan.md`
- **Research**: `specs/060-ios-landing-page/research.md`
- **Data Model**: `specs/060-ios-landing-page/data-model.md`
- **Constitution**: `.specify/memory/constitution.md` (iOS MVVM-C Architecture: Principle XI)

### Code Examples (Existing Patterns)
- **Presentation Models**: `/iosApp/iosApp/Features/ReportMissingPet/Views/AnimalDescription/Components/`
- **SwiftUI Composable Views**: `/iosApp/iosApp/Features/LandingPage/Views/LandingPageView.swift`
- **Coordinators**: `/iosApp/iosApp/Features/LandingPage/Coordinators/HomeCoordinator.swift`
- **Unit Tests**: `/iosApp/iosAppTests/Features/LandingPage/`

### External Documentation
- [SwiftUI Documentation](https://developer.apple.com/documentation/swiftui)
- [Swift Concurrency](https://docs.swift.org/swift-book/LanguageGuide/Concurrency.html)
- [XCTest Framework](https://developer.apple.com/documentation/xctest)
- [swiftgen Documentation](https://github.com/SwiftGen/SwiftGen)

---

## Next Steps

After completing implementation:

1. **Verify all acceptance criteria** (Feature Spec sections for User Story 1 & 2)
2. **Run full test suite** (unit + E2E)
3. **Check code coverage** (80% target)
4. **Visual QA** against Figma design (node `974:4667`)
5. **Manual testing** on different screen sizes (iPhone SE, iPhone 15, iPad)
6. **Create pull request** with implemented feature
7. **Code review** by team
8. **Merge to main** after approval

---

## Support

If you encounter issues:
1. Check this quickstart guide
2. Review `research.md` for technical decisions
3. Review `data-model.md` for model structure
4. Check constitution for architecture patterns (Principle XI)
5. Look at existing code examples in the codebase
6. Ask team for help on Slack/MS Teams

---

## Summary

**Implementation time estimate**: 4-6 hours (including tests)

**Key files to create**: 8 new files (4 presentation models + 4 views/components + 2 test files)

**Key files to modify**: 4 files (`LandingPageView.swift`, `LandingPageViewModel.swift`, `HomeCoordinator.swift`, `TabCoordinator.swift`)

**Key pattern reuse**: Refactors existing `showPetDetailsFromHome()` to `switchToLostPetTab(withAnnouncementId: String? = nil)` for both list taps (with detail) and hero buttons (without detail)

**Testing**: Unit tests + E2E tests (80% coverage target)

**Dependencies**: No new dependencies (pure SwiftUI + Foundation + SF Symbols for icons)

Happy coding! 🚀

