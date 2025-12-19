# Data Model: iOS Landing Page Full Content Scroll

**Feature**: KAN-30 iOS Landing Page Full Content Scroll  
**Date**: 2025-12-19  
**Status**: N/A - No Data Model Changes

## Overview

This feature is a UI-only refactoring that changes the scroll architecture of the iOS landing page. No data models, entities, or data structures are added, modified, or removed.

## Existing Models (Unchanged)

The following domain models are used by the landing page but remain unchanged:

### Announcement (Existing)

**Location**: `/iosApp/iosApp/Domain/Models/Announcement.swift`

**Purpose**: Represents a pet announcement (lost/found pet report)

**Fields**: (Unchanged - no modifications needed)
- `id: String` - Unique announcement identifier
- `petName: String` - Name of the pet
- `species: PetSpecies` - Dog, cat, etc.
- `location: Coordinate` - Geographic location
- `distance: Double?` - Distance from user location (optional)
- `imageUrl: String?` - URL to pet photo (optional)
- Additional metadata fields

**Usage**: Displayed in announcement cards list on landing page

### AnnouncementCardViewModel (Existing)

**Location**: `/iosApp/iosApp/Features/AnnouncementCard/AnnouncementCardViewModel.swift`

**Purpose**: Presentation model for individual announcement cards

**Properties**: (Unchanged - no modifications needed)
- `id: String` - Stable identifier for list rendering
- `petName: String` - Displayed pet name
- `distance: String` - Formatted distance text
- `imageUrl: String?` - Pet photo URL
- Additional presentation properties

**Usage**: Array of card ViewModels rendered in landing page list

## State Models (Unchanged)

### LandingPageViewModel State (Existing)

**Location**: `/iosApp/iosApp/Features/LandingPage/Views/LandingPageViewModel.swift`

**State Properties**: (Unchanged - no modifications needed)
- `@Published var listViewModel: AnnouncementCardsListViewModel` - Child list component state
- `@Published var showPermissionDeniedAlert: Bool` - Permission popup state
- Navigation closures for coordinator callbacks

**Rationale**: UI refactoring doesn't change state management logic

### AnnouncementCardsListViewModel State (Existing)

**Location**: `/iosApp/iosApp/Views/AnnouncementCardsListViewModel.swift`

**State Properties**: (Unchanged - no modifications needed)
- `@Published var cardViewModels: [AnnouncementCardViewModel]` - List of cards
- `@Published var isLoading: Bool` - Loading indicator state
- `@Published var errorMessage: String?` - Error message text
- `var query: AnnouncementQuery?` - Query parameters (triggers reload)

**Rationale**: Scroll architecture change doesn't affect list state management

## Validation Rules (Unchanged)

No validation rules added or modified. Existing validation for announcements, location permissions, and query parameters remain unchanged.

## State Transitions (Unchanged)

Landing page state transitions remain unchanged:

```
Initial State
    ↓
Loading (locationHandler requests permission)
    ↓
[Permission Granted] → Loading announcements
    ↓
Success (display list) OR Error (show error view) OR Empty (show empty state)
    
[Permission Denied] → Show permission popup → Continue without location OR Open Settings
```

**Note**: These state transitions are unchanged by scroll architecture refactoring.

## Impact Analysis

**No Data Model Changes Required Because**:
- Scroll architecture is purely presentational (View layer change)
- ViewModels maintain same `@Published` properties (same state contract)
- Domain models unchanged (Announcement, Coordinate, etc.)
- Repository interfaces unchanged (no new data fetching)
- No new entities, fields, or relationships introduced

## Summary

This feature requires no data model changes. All existing domain models (Announcement), presentation models (AnnouncementCardViewModel), and state models (LandingPageViewModel, AnnouncementCardsListViewModel) remain unchanged. The scroll architecture change is purely a View layer refactoring.

