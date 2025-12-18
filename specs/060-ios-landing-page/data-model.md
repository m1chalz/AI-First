# Data Model: iOS Landing Page - Top Panel

**Feature Branch**: `060-ios-landing-page`  
**Date**: 2025-12-17  
**Status**: Complete

## Overview

This document describes the presentation models (UI state structures) for the iOS Landing Page top panel feature. Since this is a pure UI enhancement with no backend API or domain changes, there are no domain entities or database schemas. All models are **presentation layer** structs holding UI-specific data.

---

## Presentation Models

Presentation models follow the established iOS pattern: separate `_Model.swift` files containing structs with UI state (text, colors, closures, accessibility IDs). These models are immutable after initialization and passed to SwiftUI views.

---

### HeroPanelView_Model

**Purpose**: Holds UI state for the hero section at the top of the Home screen.

**Location**: `/iosApp/iosApp/Features/LandingPage/Views/Components/HeroPanelView_Model.swift`

**Type**: `struct` (immutable)

#### Properties

| Property | Type | Description | Source |
|----------|------|-------------|--------|
| `title` | `String` | Main title text (e.g., "Find Your Pet") | Localized via `L10n.LandingPage.Hero.title` |
| `lostPetButtonTitle` | `String` | "Lost Pet" button label | Localized via `L10n.LandingPage.Hero.lostPetButton` |
| `foundPetButtonTitle` | `String` | "Found Pet" button label | Localized via `L10n.LandingPage.Hero.foundPetButton` |
| `lostPetButtonIcon` | `String?` | SF Symbol or asset name for Lost Pet button icon | Static: `"exclamationmark.triangle"` (SF Symbol for alert triangle) |
| `foundPetButtonIcon` | `String?` | SF Symbol or asset name for Found Pet button icon | Static: `"checkmark"` (SF Symbol) |
| `onLostPetTap` | `() -> Void` | Closure invoked when "Lost Pet" button tapped | Set by `HomeCoordinator` (navigates to Lost Pet tab) |
| `onFoundPetTap` | `() -> Void` | Closure invoked when "Found Pet" button tapped | Set by `HomeCoordinator` (navigates to Found Pet tab) |
| `titleAccessibilityId` | `String` | Accessibility identifier for title | Static: `"home.hero.title"` (per FR-010) |
| `lostPetButtonAccessibilityId` | `String` | Accessibility identifier for Lost Pet button | Static: `"home.hero.lostPetButton"` (per FR-010) |
| `foundPetButtonAccessibilityId` | `String` | Accessibility identifier for Found Pet button | Static: `"home.hero.foundPetButton"` (per FR-010) |

#### Initialization

```swift
struct HeroPanelView_Model {
    let title: String
    let lostPetButtonTitle: String
    let foundPetButtonTitle: String
    let lostPetButtonIcon: String?
    let foundPetButtonIcon: String?
    let onLostPetTap: () -> Void
    let onFoundPetTap: () -> Void
    let titleAccessibilityId: String
    let lostPetButtonAccessibilityId: String
    let foundPetButtonAccessibilityId: String
    
    init(
        title: String = L10n.LandingPage.Hero.title,
        lostPetButtonTitle: String = L10n.LandingPage.Hero.lostPetButton,
        foundPetButtonTitle: String = L10n.LandingPage.Hero.foundPetButton,
        lostPetButtonIcon: String? = "exclamationmark.triangle",
        foundPetButtonIcon: String? = "checkmark",
        onLostPetTap: @escaping () -> Void = {},
        onFoundPetTap: @escaping () -> Void = {},
        titleAccessibilityId: String = "home.hero.title",
        lostPetButtonAccessibilityId: String = "home.hero.lostPetButton",
        foundPetButtonAccessibilityId: String = "home.hero.foundPetButton"
    ) {
        self.title = title
        self.lostPetButtonTitle = lostPetButtonTitle
        self.foundPetButtonTitle = foundPetButtonTitle
        self.lostPetButtonIcon = lostPetButtonIcon
        self.foundPetButtonIcon = foundPetButtonIcon
        self.onLostPetTap = onLostPetTap
        self.onFoundPetTap = onFoundPetTap
        self.titleAccessibilityId = titleAccessibilityId
        self.lostPetButtonAccessibilityId = lostPetButtonAccessibilityId
        self.foundPetButtonAccessibilityId = foundPetButtonAccessibilityId
    }
}
```

#### Validation Rules

- **Localization**: All displayed text (`title`, `lostPetButtonTitle`, `foundPetButtonTitle`) MUST use `L10n.*` (swiftgen-generated strings)
- **Accessibility**: All accessibility IDs MUST follow iOS naming convention `{screen}.{element}` (Principle VI)
- **Closures**: Default to no-op closures for SwiftUI previews (Principle XI)

#### Relationships

- **Used by**: `HeroPanelView.swift` (SwiftUI view)
- **Created by**: `LandingPageView.swift` (initializes model with coordinator closures)
- **Navigation**: Closures set by `HomeCoordinator` (MVVM-C pattern)

---

### ListHeaderRowView_Model

**Purpose**: Holds UI state for the list header row ("Recent Reports" / "View All") directly above the announcement list.

**Location**: `/iosApp/iosApp/Features/LandingPage/Views/Components/ListHeaderRowView_Model.swift`

**Type**: `struct` (immutable)

#### Properties

| Property | Type | Description | Source |
|----------|------|-------------|--------|
| `title` | `String` | Section title text (e.g., "Recent Reports") | Localized via `L10n.LandingPage.ListHeader.title` |
| `actionTitle` | `String` | "View All" action label | Localized via `L10n.LandingPage.ListHeader.viewAll` |
| `onActionTap` | `() -> Void` | Closure invoked when "View All" tapped | Set by `HomeCoordinator` (switches to Lost Pet tab) |
| `titleAccessibilityId` | `String` | Accessibility identifier for title | Static: `"home.recentReports.title"` (per FR-010) |
| `actionAccessibilityId` | `String` | Accessibility identifier for "View All" | Static: `"home.recentReports.viewAll"` (per FR-010) |

#### Initialization

```swift
struct ListHeaderRowView_Model {
    let title: String
    let actionTitle: String
    let onActionTap: () -> Void
    let titleAccessibilityId: String
    let actionAccessibilityId: String
    
    init(
        title: String = L10n.LandingPage.ListHeader.title,
        actionTitle: String = L10n.LandingPage.ListHeader.viewAll,
        onActionTap: @escaping () -> Void = {},
        titleAccessibilityId: String = "home.recentReports.title",
        actionAccessibilityId: String = "home.recentReports.viewAll"
    ) {
        self.title = title
        self.actionTitle = actionTitle
        self.onActionTap = onActionTap
        self.titleAccessibilityId = titleAccessibilityId
        self.actionAccessibilityId = actionAccessibilityId
    }
}
```

#### Validation Rules

- **Localization**: All displayed text (`title`, `actionTitle`) MUST use `L10n.*` (swiftgen-generated strings)
- **Accessibility**: All accessibility IDs MUST follow iOS naming convention `{screen}.{element}` (Principle VI)
- **Closures**: Default to no-op closure for SwiftUI previews (Principle XI)

#### Relationships

- **Used by**: `ListHeaderRowView.swift` (SwiftUI view)
- **Created by**: `LandingPageView.swift` (initializes model with coordinator closure)
- **Navigation**: Closure set by `HomeCoordinator` (MVVM-C pattern)

---

## Model Relationships Diagram

```text
┌─────────────────────┐
│  HomeCoordinator    │  (MVVM-C: Navigation)
│  ┌───────────────┐  │
│  │ Tab navigation│  │  Sets closures
│  │   closures    │  │      ↓
│  └───────────────┘  │      ↓
└─────────────────────┘      ↓
                             ↓
┌─────────────────────────────────────────────┐
│         LandingPageView                     │
│  ┌─────────────────────────────────────┐   │
│  │ Creates models with closures:       │   │
│  │ - HeroPanelView_Model               │   │
│  │ - ListHeaderRowView_Model           │   │
│  └─────────────────────────────────────┘   │
└─────────────────────────────────────────────┘
            │                    │
            │ passes             │ passes
            ↓                    ↓
┌────────────────────┐   ┌────────────────────┐
│   HeroPanelView    │   │  ListHeaderRowView │
│   (SwiftUI view)   │   │  (SwiftUI view)    │
│                    │   │                    │
│ - Displays title   │   │ - Displays title   │
│ - Renders buttons  │   │ - Renders action   │
│ - Invokes closures │   │ - Invokes closure  │
└────────────────────┘   └────────────────────┘
```

---

## Localization Strings

**File**: `/iosApp/iosApp/Resources/en.lproj/Localizable.strings`

Add the following keys (swiftgen will generate `L10n.*` accessors):

```swift
/* Landing Page Hero Section */
"LandingPage.Hero.title" = "Find Your Pet";
"LandingPage.Hero.lostPetButton" = "Lost Pet";
"LandingPage.Hero.foundPetButton" = "Found Pet";

/* Landing Page List Header */
"LandingPage.ListHeader.title" = "Recent Reports";
"LandingPage.ListHeader.viewAll" = "View All";
```

After adding strings, run swiftgen to generate code:
```bash
cd iosApp && swiftgen
```

---

## State Transitions

**N/A** - These are static presentation models (no state machines or transitions). Models are immutable after initialization and do not change during the view lifecycle.

---

## Validation & Testing

### Unit Tests

**Test Coverage Requirements** (Principle II: 80% coverage):

1. **`HeroPanelView_ModelTests.swift`**:
   - Test default initialization with localized strings
   - Test accessibility identifiers match FR-010 requirements
   - Test closure invocation (verify `onLostPetTap` and `onFoundPetTap` are called)
   - Test custom initialization for SwiftUI previews

2. **`ListHeaderRowView_ModelTests.swift`**:
   - Test default initialization with localized strings
   - Test accessibility identifiers match FR-010 requirements
   - Test closure invocation (verify `onActionTap` is called)
   - Test custom initialization for SwiftUI previews

### E2E Tests

**Java/Appium tests** (Principle XII):
- Verify hero panel renders with correct text (User Story 1, Scenario 1)
- Verify list header row renders with correct text (User Story 1, Scenario 2)
- Verify "Lost Pet" button navigates to Lost Pet tab (User Story 2, Scenario 1)
- Verify "Found Pet" button navigates to Found Pet tab (User Story 2, Scenario 2)
- Verify "View All" navigates to full announcements list (User Story 2, Scenario 4)

---

## Dependencies

### Framework Dependencies
- **SwiftUI**: UI rendering (built-in, no version constraint)
- **Foundation**: Base types (`String`, closures)

### Project Dependencies
- **swiftgen**: Localization string generation (`L10n.*`)
- **Existing localization infrastructure**: `Localizable.strings`, swiftgen config

### No External Dependencies
No third-party libraries required (pure SwiftUI + Foundation).

---

## Implementation Checklist

- [ ] Create `HeroPanelView_Model.swift` with properties and init
- [ ] Create `ListHeaderRowView_Model.swift` with properties and init
- [ ] Add localization strings to `Localizable.strings`
- [ ] Run swiftgen to generate `L10n.*` accessors
- [ ] Create unit tests for both presentation models
- [ ] Create SwiftUI views (`HeroPanelView.swift`, `ListHeaderRowView.swift`)
- [ ] Modify `LandingPageView.swift` to use new models
- [ ] Extend `HomeCoordinator.swift` to set navigation closures
- [ ] Run unit tests and verify 80% coverage
- [ ] Run E2E tests for User Story 1 & 2

---

## Summary

Two presentation models capture all UI state for the top panel feature:
1. **HeroPanelView_Model**: Hero section with title + two action buttons
2. **ListHeaderRowView_Model**: List header with title + "View All" action

Both models follow iOS MVVM-C architecture (Principle XI):
- Immutable structs
- Localized strings via swiftgen
- Accessibility identifiers per convention
- Navigation closures set by coordinator
- Default no-op closures for previews
- 80% test coverage via unit + E2E tests

