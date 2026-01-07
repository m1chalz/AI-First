# Data Model: iOS Fullscreen Interactive Map with Legend

**Feature Branch**: `KAN-32-ios-display-fullscreen-map`  
**Date**: 2025-01-07

## Overview

This feature is primarily UI-focused with minimal data model changes. Most entities already exist in the codebase.

## Existing Entities (Reused)

### Coordinate

**Location**: `/iosApp/iosApp/Domain/Models/Coordinate.swift`

```swift
struct Coordinate: Codable, Equatable {
    let latitude: Double
    let longitude: Double
    
    /// Creates map region with specified radius centered on this coordinate.
    func mapRegion(radiusMeters: Double = 10_000) -> MKCoordinateRegion
}
```

**Usage**: User's current location for map centering.

## Modified Entities

### MapSectionHeaderView.Model (Modified)

**Location**: `/iosApp/iosApp/Features/LandingPage/Views/Components/MapSectionHeaderView_Model.swift`

**Change**: Make `title` optional to support legend-only display in fullscreen map.

```swift
extension MapSectionHeaderView {
    struct Model {
        /// Section title text (nil = no title, legend only)
        let title: String?  // CHANGED: String → String?
        
        /// Legend items to display (color dot + label)
        let legendItems: [LegendItem]
        
        let titleAccessibilityId: String?  // CHANGED: also optional
        let legendAccessibilityIdPrefix: String
    }
}
```

**New Factory Method** (`MapSectionHeaderView_Model+FullscreenMap.swift`):
```swift
extension MapSectionHeaderView.Model {
    /// Creates legend-only model for fullscreen map (no title).
    static func fullscreenMap() -> MapSectionHeaderView.Model {
        MapSectionHeaderView.Model(
            title: nil,  // Title provided by navigation bar
            legendItems: [
                MapSectionHeaderView.LegendItem(
                    id: "missing",
                    colorHex: "#FF0000",
                    label: L10n.MapSection.Legend.missing
                ),
                MapSectionHeaderView.LegendItem(
                    id: "found",
                    colorHex: "#0074FF",
                    label: L10n.MapSection.Legend.found
                )
            ],
            titleAccessibilityId: nil,
            legendAccessibilityIdPrefix: "fullscreenMap.legend"
        )
    }
}
```

**Backward Compatibility**: Existing `.landingPage()` factory continues to work unchanged.

### FullscreenMapViewModel

**Location**: `/iosApp/iosApp/Features/LandingPage/Views/FullscreenMap/FullscreenMapViewModel.swift`

Simple data holder - receives location via constructor (already available from landing page):

```swift
@MainActor
class FullscreenMapViewModel: ObservableObject {
    /// Map region centered on user's location
    let mapRegion: MKCoordinateRegion
    
    /// Legend configuration (reuses existing MapSectionHeaderView)
    let legendModel: MapSectionHeaderView.Model
    
    init(userLocation: Coordinate) {
        self.mapRegion = userLocation.mapRegion()
        self.legendModel = .fullscreenMap()
    }
}
```

**Properties**:
| Property | Type | Description |
|----------|------|-------------|
| `mapRegion` | `MKCoordinateRegion` | Pre-calculated from user location |
| `legendModel` | `MapSectionHeaderView.Model` | Legend-only config (no title) |

**Note**: No loading state needed - location is passed from landing page where it was already fetched. Map renders immediately.

## Entity Relationships

```
┌─────────────────────────────────┐
│  LandingPageViewModel           │
│  ─────────────────────────────  │
│  currentLocation: Coordinate?   │
│  onShowFullscreenMap: ((Coordinate) -> Void)?│
└─────────────────────────────────┘
            │
            │ callback with location
            ▼
┌─────────────────────────────────┐
│  HomeCoordinator                │
│  ─────────────────────────────  │
│  showFullscreenMap(userLocation:)│
└─────────────────────────────────┘
            │
            │ passes to constructor
            ▼
┌─────────────────────────────────┐
│  FullscreenMapViewModel         │
│  ─────────────────────────────  │
│  mapRegion: MKCoordinateRegion  │◄──── calculated from Coordinate
│  legendModel: MapSectionHeaderView.Model│
└─────────────────────────────────┘
```

## No API Contracts Required

This feature is iOS-only and does not require backend API changes. The map displays user's device location (from iOS Location Services) and a static legend. No server communication is needed.

