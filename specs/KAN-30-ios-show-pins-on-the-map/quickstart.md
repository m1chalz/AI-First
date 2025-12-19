# Quickstart: iOS Map Preview - Display Missing Pet Pins

**Feature Branch**: `KAN-30-ios-show-pins-on-the-map`  
**Created**: 2025-12-19

## Overview

Adds pin markers to the iOS landing page map preview showing missing pet announcement locations. Pins are derived from the same data used by the announcements list (no separate fetch).

## Prerequisites

- macOS with Xcode 16+ installed
- iOS Simulator (iPhone 16)
- Backend server running at configured URL (for real data)

## Quick Setup

```bash
# 1. Switch to feature branch
git checkout KAN-30-ios-show-pins-on-the-map

# 2. Open project in Xcode
open iosApp/iosApp.xcodeproj

# 3. Select iPhone 16 simulator
# 4. Build and run (Cmd+R)
```

## Key Files to Modify

### 1. MapPreviewView_PinModel.swift (NEW)
**Path**: `/iosApp/iosApp/Features/LandingPage/Views/Components/MapPreviewView_PinModel.swift`

Add `PinModel` struct as extension of `MapPreviewView`.

### 2. MapPreviewView_Model.swift
**Path**: `/iosApp/iosApp/Features/LandingPage/Views/Components/MapPreviewView_Model.swift`

Extend `.map` case with `pins: [PinModel]` array.

### 3. MapPreviewView.swift
**Path**: `/iosApp/iosApp/Features/LandingPage/Views/Components/MapPreviewView.swift`

Update `mapView` to render `Marker` for each pin in the array.

### 4. AnnouncementListQuery.swift
**Path**: `/iosApp/iosApp/Domain/Models/AnnouncementListQuery.swift`

Add `range: Int` property and update factory methods.

### 5. LandingPageViewModel.swift
**Path**: `/iosApp/iosApp/Features/LandingPage/Views/LandingPageViewModel.swift`

Update `updateMapPreviewModel()` to create pin models from `listViewModel.cardViewModels`.

**Note**: `AnnouncementCardsListViewModel` does NOT need changes - backend returns only active/found announcements.

## Testing

### Run Unit Tests

```bash
# From terminal
xcodebuild test \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 16' \
  -enableCodeCoverage YES

# Or in Xcode: Cmd+U
```

### Manual Testing Checklist

1. **Pins appear on map**
   - Grant location permission
   - Verify pins at announcement locations
   - Check red marker style

2. **Non-interactive behavior**
   - Tap pin → nothing happens
   - Tap map → nothing happens
   - No zoom/pan gestures work

3. **Edge cases**
   - Deny location → no pins (permission prompt shows)
   - No announcements nearby → empty map
   - Overlapping pins → both visible (may overlap)

## Architecture Notes

```
LandingPageViewModel
    │
    ├── listViewModel: AnnouncementCardsListViewModel
    │       └── pinEligibleAnnouncements → [Announcement]
    │
    └── mapPreviewModel: MapPreviewView.Model
            └── .map(region:pins:onTap:) → MapPreviewView
```

### Data Flow

1. `loadData()` triggers location + announcements fetch
2. Repository uses `range=10` km for landing page query
3. `listViewModel` stores announcements, exposes `pinEligibleAnnouncements`
4. `updateMapPreviewModel()` creates `[MapPinModel]` from filtered announcements
5. `MapPreviewView` renders `Marker` for each pin

## Compliance Checklist

- [ ] 80% unit test coverage for new/modified code
- [ ] Tests use `@testable import PetSpot`
- [ ] All tests follow Given-When-Then structure
- [ ] No Combine framework usage
- [ ] `.accessibilityIdentifier` on new interactive elements (N/A - pins are non-interactive)
- [ ] SwiftGen localization for any new strings

