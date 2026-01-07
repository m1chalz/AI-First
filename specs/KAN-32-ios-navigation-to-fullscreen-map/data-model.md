# Data Model: iOS Navigation to Fullscreen Map View

**Feature**: KAN-32 | **Date**: 2025-01-07

## Overview

This feature introduces no new domain entities. It adds navigation infrastructure and a placeholder view. The data model section documents the minimal ViewModel state required for navigation flow.

## Entities

### FullscreenMapViewModel (Presentation Layer)

**Purpose**: Minimal ViewModel for MVVM-C compliance. Provides extension point for future map features.

```swift
@MainActor
class FullscreenMapViewModel: ObservableObject {
    // Currently empty - placeholder for future state
    // Future additions:
    // - @Published var annotations: [MapAnnotation] = []
    // - @Published var selectedAnnotation: MapAnnotation?
    // - @Published var isLoading: Bool = false
}
```

**Fields**: None (placeholder)

**Relationships**: None

**Validation Rules**: None

**State Transitions**: None

---

### LandingPageViewModel (Modification)

**Purpose**: Add navigation callback for fullscreen map.

**New Fields**:

| Field | Type | Description |
|-------|------|-------------|
| `onShowFullscreenMap` | `(() -> Void)?` | Coordinator callback for fullscreen map navigation |

**Note**: Rapid-tap prevention handled by UIKit's default navigation controller behavior (blocks interaction during animations).

---

## Localization Model

### New Localization Keys

| Key | EN Value | PL Value |
|-----|----------|----------|
| `fullscreenMap.navigationTitle` | Pet Locations | Lokalizacje zwierząt |

**Location**: 
- `iosApp/iosApp/Resources/en.lproj/Localizable.strings`
- `iosApp/iosApp/Resources/pl.lproj/Localizable.strings`

**SwiftGen Access**: `L10n.FullscreenMap.navigationTitle`

---

## No Domain Model Changes

This feature does not introduce:
- New domain entities
- New repository protocols
- New data layer models
- API request/response models

The fullscreen map view is a UI-only placeholder. Future tickets (map interactions, pin display) will introduce appropriate domain models.

