# Research: iOS Landing Page - Top Panel

**Feature Branch**: `060-ios-landing-page`  
**Date**: 2025-12-17  
**Status**: Complete

## Overview

This document captures technical decisions for adding a top panel UI (hero section + list header row) above the existing Home list on iOS. All decisions follow existing iOS MVVM-C architecture patterns and SwiftUI conventions established in the codebase.

## Technical Decisions

### Decision 1: UI Component Architecture

**Decision**: Use SwiftUI reusable components with presentation models (separate `_Model.swift` + `.swift` files) following the established pattern in the codebase.

**Rationale**:
- Existing codebase uses this pattern consistently (e.g., `EmptyStateView_Model.swift` + `EmptyStateView.swift`, `LabelValueRowView_Model.swift` + `LabelValueRowView.swift`)
- Presentation models (structs) decouple UI state from business logic, making components testable and reusable
- Follows iOS Constitution Principle XI: "Presentation models hold UI-specific data (colors as hex strings, formatted dates)"
- Enables SwiftUI previews with static data without complex ViewModels

**Alternatives Considered**:
1. **Inline view properties in LandingPageViewModel**: Rejected because it violates separation of concerns (ViewModel should not contain UI-specific formatting). Presentation models keep ViewModels clean and focused on business logic.
2. **Single monolithic view component**: Rejected because it reduces reusability and testability. Separate components (`HeroPanel`, `ListHeaderRow`) allow independent testing and potential reuse in other screens.
3. **Observable presentation models**: Rejected because these are static UI configurations (title, button labels) that don't change after initialization. Using `@Published` would add unnecessary complexity.

---

### Decision 2: Layout Strategy - VStack with Spacer

**Decision**: Modify `LandingPageView.swift` to wrap content in a `VStack` with proper spacing control:
1. `HeroPanel(model: heroModel)` - top (fixed height)
2. `ListHeaderRow(model: listHeaderModel)` - middle (fixed height)
3. `Spacer(minLength: 0)` - flexible space
4. Existing `AnnouncementCardsListView` with `.frame(maxHeight: .infinity)` - bottom (takes remaining space, scrollable)

**Layout Code Pattern**:
```swift
VStack(spacing: 0) {
    HeroPanel(model: heroModel)
    
    ListHeaderRow(model: listHeaderModel)
        .padding(.top, 16)  // gap between hero and list header
    
    // AnnouncementCardsListView needs explicit height to make inner ScrollView work
    AnnouncementCardsListView(...)
        .frame(maxHeight: .infinity)  // Takes all remaining vertical space
}
```

**Rationale**:
- `AnnouncementCardsListView` has its own `ScrollView` inside (line 53), so it needs a known height to work properly
- `.frame(maxHeight: .infinity)` gives it "the rest of available space" after fixed hero + header
- When screen is small (iPhone SE), hero + header stay visible, list area shrinks but remains scrollable
- Matches Figma hierarchy: hero panel above list header row, list header row directly above list
- Top panel remains pinned while only the list content scrolls

**Alternatives Considered**:
1. **VStack without explicit frame**: Rejected because `AnnouncementCardsListView` has nested `ScrollView` which needs explicit height. Without `.frame(maxHeight: .infinity)`, the list may not render correctly or take up wrong amount of space.
2. **Single outer ScrollView wrapping hero + header + list**: Rejected because it would require refactoring `AnnouncementCardsListView` to remove its internal `ScrollView`. This violates "no changes to existing list" constraint and breaks its autonomous component pattern.
3. **GeometryReader for dynamic sizing**: Rejected as overly complex. `.frame(maxHeight: .infinity)` is simpler and idiomatic SwiftUI for "fill remaining space" behavior.
4. **LazyVStack with header sections**: Rejected because it would require refactoring the entire list implementation. Current list is an autonomous component that should remain unchanged per feature spec.
5. **Custom UICollectionView with header**: Rejected because it breaks SwiftUI conventions and requires dropping down to UIKit unnecessarily.

---

### Decision 3: Navigation Pattern - Reuse Existing Cross-Tab Navigation

**Decision**: Extend existing `TabCoordinator` method to support optional detail navigation:
- **Existing method**: `showPetDetailsFromHome(_ announcementId: String)` - switches to Lost Pet tab AND shows detail
- **Refactor to**: `switchToLostPetTab(withAnnouncementId: String? = nil)` - switches tab, optionally shows detail
  - When `announcementId` is `nil` → only switch to Lost Pet tab (hero button use case)
  - When `announcementId` is not `nil` → switch tab + show pet details (existing list tap use case)
- **New method**: `switchToFoundPetTab()` - switches to Found Pet tab (no details screen on Found Pet tab)

`HomeCoordinator` will expose these as closures to presentation models.

**Rationale**:
- **Reuses existing pattern**: `showPetDetailsFromHome` already demonstrates cross-tab navigation from Home
- **Single responsibility**: One method handles "navigate to Lost Pet tab" with optional detail parameter
- **Less duplication**: No need for separate `navigateToLostPetTab()` and `showPetDetailsFromHome()` methods
- Follows existing MVVM-C pattern: coordinators handle all navigation, views trigger via closures
- Tab switching is cross-coordinator communication via parent `TabCoordinator`

**Implementation Details**:
```swift
// TabCoordinator.swift - Refactored method
private func switchToLostPetTab(withAnnouncementId announcementId: String? = nil) {
    guard let (index, lostPetCoordinator) = findAnnouncementListCoordinator() else { return }
    
    _tabBarController.selectedIndex = index
    
    if let announcementId = announcementId {
        lostPetCoordinator.showPetDetails(for: announcementId)
    }
}

// HomeCoordinator.swift - Expose closures
var onSwitchToLostPetTab: (() -> Void)?  // Set by TabCoordinator
var onSwitchToFoundPetTab: (() -> Void)?  // Set by TabCoordinator
```

**Alternatives Considered**:
1. **Separate methods for each use case**: Rejected because it duplicates tab switching logic. Single method with optional parameter is cleaner.
2. **Direct UITabBarController access in view**: Rejected because it violates MVVM-C (views should not know about navigation infrastructure).
3. **ViewModel-based navigation**: Rejected because navigation is coordinator's responsibility, not ViewModel's.
4. **NotificationCenter for tab switching**: Rejected because closures are more explicit and testable.

---

### Decision 4: Test Strategy

**Decision**: 
- **Unit tests**: Test presentation models (`HeroPanel_Model`, `ListHeaderRow_Model`) for property initialization and SwiftUI preview data
- **E2E tests**: Java/Appium tests for complete user flows (User Story 1: See top panel, User Story 2: Use top panel actions to navigate)
- **No ViewModel tests**: `LandingPageViewModel` remains unchanged (no new logic to test)

**Rationale**:
- Presentation models are simple structs but should have tests to verify accessibility identifiers and localized strings are correctly set
- E2E tests verify complete integration: UI rendering + tab navigation + no regression in existing list behavior
- Follows 80% test coverage requirement (Principle II)

**Alternatives Considered**:
1. **SwiftUI View snapshot tests**: Considered but rejected as out of scope. Snapshot testing is not established in the codebase. E2E tests sufficiently verify UI appearance.
2. **Manual testing only**: Rejected because it doesn't meet 80% coverage requirement and lacks regression protection.

---

## Best Practices Applied

### SwiftUI Components (Principle XI: iOS MVVM-C Architecture)

- **Presentation model pattern**: Separate `_Model.swift` files with structs containing UI state (title, button labels, accessibility IDs, closures)
- **View files**: Pure SwiftUI views observing presentation models, no business/navigation logic
- **Localization**: All displayed text uses `L10n` generated by swiftgen (e.g., `L10n.LandingPage.Hero.title`)
- **Accessibility**: All interactive elements have `.accessibilityIdentifier()` following naming convention (`home.hero.lostPetButton`, `home.hero.foundPetButton`, etc.)
- **Previews**: SwiftUI `#Preview` with static presentation model data (no ViewModels in previews)

### UIKit Coordinators (Principle XI: iOS MVVM-C Architecture)

- **Constructor injection**: Tab navigation closures injected via `HomeCoordinator` init (passed from parent `TabCoordinator`)
- **Navigation responsibility**: Coordinator handles all navigation, including cross-tab navigation via parent coordinator callbacks
- **UIHostingController**: Wraps SwiftUI views for UIKit navigation controller integration

### Tab Navigation (Existing Pattern)

- **Parent coordinator pattern**: `TabCoordinator` owns `UITabBarController` and child coordinators (Home, Lost Pet, Found Pet)
- **Cross-tab navigation**: Child coordinators communicate back to parent via closures to switch tabs
- **Example**: Existing `onShowPetDetails` closure in `HomeCoordinator` demonstrates cross-tab navigation for announcement details

---

## Dependencies & Integrations

### Existing Dependencies (No Changes)
- **SwiftUI**: UI framework for all views
- **UIKit**: Coordinators and `UIHostingController`
- **swiftgen**: Localization string generation (`L10n.*`)
- **XCTest**: Unit testing framework

### Integrations
- **Tab Navigation**: Extends existing `TabCoordinator` cross-tab navigation pattern (already implemented for announcement details)
- **Announcement List**: Composes with existing `AnnouncementCardsListView` (no modifications)
- **Localization**: Reuses existing swiftgen setup (`L10n.*` strings)

---

## Implementation Notes

### Files to Create (8 new files)
1. `/iosApp/iosApp/Features/LandingPage/Views/Components/HeroPanel_Model.swift` - Presentation model
2. `/iosApp/iosApp/Features/LandingPage/Views/Components/HeroPanel.swift` - SwiftUI view
3. `/iosApp/iosApp/Features/LandingPage/Views/Components/HeroButton_Model.swift` - Presentation model for hero buttons (icon + text)
4. `/iosApp/iosApp/Features/LandingPage/Views/Components/HeroButton.swift` - SwiftUI view for hero buttons (reusable button component with icon)
5. `/iosApp/iosApp/Features/LandingPage/Views/Components/ListHeaderRow_Model.swift` - Presentation model
6. `/iosApp/iosApp/Features/LandingPage/Views/Components/ListHeaderRow.swift` - SwiftUI view
7. `/iosApp/iosAppTests/Features/LandingPage/Views/Components/HeroPanel_ModelTests.swift` - Unit tests
8. `/iosApp/iosAppTests/Features/LandingPage/Views/Components/ListHeaderRow_ModelTests.swift` - Unit tests

### Files to Modify (4 files)
1. `/iosApp/iosApp/Features/LandingPage/Views/LandingPageView.swift` - Add VStack with hero panel + list header row
2. `/iosApp/iosApp/Features/LandingPage/Views/LandingPageViewModel.swift` - Add tab navigation closure properties
3. `/iosApp/iosApp/Features/LandingPage/Coordinators/HomeCoordinator.swift` - Add tab navigation closure properties
4. `/iosApp/iosApp/Coordinators/TabCoordinator.swift` - Refactor `showPetDetailsFromHome` to `switchToLostPetTab(withAnnouncementId:)`, add `switchToFoundPetTab()`, set closures on HomeCoordinator

### Files Unchanged (NO modifications per feature spec)
- List logic in `LandingPageViewModel` remains the same (only adding closure properties for navigation)
- `AnnouncementCardsListView.swift` - List component unchanged
- All repository, domain, and data layer files - UI-only feature

### Reusable Pattern Note
**FloatingActionButton** (existing component in `/iosApp/iosApp/Views/`) inspired the button styling pattern, but **will NOT be reused** directly because:
- FloatingActionButton is for overlay buttons (floating above content with ZStack)
- Hero buttons are inline in VStack layout (not floating)
- Hero buttons have different sizing and positioning requirements
- However, color scheme (`#FB2C36` for red, `#155DFC` for blue) and shadow style can be reused for consistency

---

## Risks & Mitigations

### Risk 1: Layout overlap on small screens
**Mitigation**: Use SwiftUI's automatic layout with priority on list scrolling. If hero panel + list header exceed available space, list shrinks (remains scrollable) while top panel stays visible. Test on iPhone SE (smallest screen).

### Risk 2: Tab navigation not working if parent coordinator not set
**Mitigation**: Defensive programming in `HomeCoordinator` - log error if tab navigation closure not set during initialization. E2E tests verify tab switching works end-to-end.

### Risk 3: Localization strings missing
**Mitigation**: Run swiftgen after adding new strings to `Localizable.strings`. Build will fail if `L10n.*` references are missing, catching errors early.

---

## Figma Design Reference

**Figma Node**: `974:4667` - [PetSpot wireframes - Landing page](https://www.figma.com/design/3jKkbGNFwMUgsejhr3XFvt/PetSpot-wireframes?node-id=974-4667&m=dev)

**Visual Hierarchy (top to bottom)**:
1. Navigation bar (already implemented by coordinator - title only)
2. Hero panel: "Find Your Pet" title + "Lost Pet" / "Found Pet" buttons (NEW)
3. List header row: "Recent Reports" title + "View All" action (NEW)
4. Announcement cards list (existing `AnnouncementCardsListView`)

**Design Tokens** (extracted from Figma node 974:4669):
- **Typography**: 
  - Title: 20px (Arial), color: `#101828`
  - Button text: 16px (Arial), color: white
  - List header: 18px (Arial), color: `#101828`
  - "View All": 14px (Arial), color: `#155dfc`
- **Spacing**: 
  - Hero panel padding: 74px horizontal, 24px top (80px from screen top minus nav bar)
  - Gap between title and buttons: 16px
  - Gap between buttons: 12px
  - List header gap: 16px below hero, 16px above list
- **Colors**: 
  - Hero background: Linear gradient `160.93deg, #EFF6FF 0%, #E0E7FF 100%`
  - Lost Pet button: `#FB2C36` (red)
  - Found Pet button: `#155DFC` (blue)
  - Button shadow: `0px 4px 6px -1px rgba(0,0,0,0.1), 0px 2px 4px -2px rgba(0,0,0,0.1)`
- **Button icons** (extracted from Figma):
  - Lost Pet: Alert triangle icon (URL: `imgIcon` from Figma asset)
  - Found Pet: Checkmark icon (URL: `imgIcon1` from Figma asset)
  - Icon size: 20x20px, gap from text: 8px
- **Button styles**: Primary buttons (rounded 16px corners, shadow, icon + text)

---

## Summary

All technical decisions align with existing iOS MVVM-C architecture patterns. No new frameworks or dependencies required. Implementation extends existing components and navigation patterns without breaking changes. E2E + unit tests ensure 80% coverage and regression protection for existing list behavior.

