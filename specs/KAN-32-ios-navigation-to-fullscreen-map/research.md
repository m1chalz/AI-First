# Research: iOS Navigation to Fullscreen Map View

**Feature**: KAN-32 | **Date**: 2025-01-07

## Research Tasks

### 1. Navigation Pattern: Push vs Modal

**Question**: Should fullscreen map view be pushed or presented modally?

**Decision**: UINavigationController push

**Rationale**:
- Specification explicitly requires back button and edge swipe gesture (FR-003, FR-005)
- Push navigation provides standard iOS back button automatically
- Edge swipe gesture (`interactivePopGestureRecognizer`) works out-of-box with push
- Consistent with existing app navigation patterns (HomeCoordinator, PetDetailsCoordinator)

**Alternatives considered**:
- Modal presentation: Would require custom dismiss button, no automatic edge swipe, not specified behavior

### 2. Coordinator Pattern: Child vs Extension

**Question**: Create new child coordinator or extend HomeCoordinator?

**Decision**: Extend HomeCoordinator with direct push (no child coordinator)

**Rationale**:
- Fullscreen map has no nested flows (simple push/pop)
- HomeCoordinator already owns the navigation controller
- Child coordinator overhead unnecessary for single screen
- Pattern consistent with simple navigation (vs. ReportMissingPetCoordinator which has multi-step flow)

**Alternatives considered**:
- Child FullscreenMapCoordinator: Added complexity for no benefit; appropriate only if fullscreen map had sub-navigation (e.g., filter sheet, search flow)

### 3. ViewModel: Required vs Optional

**Question**: Does placeholder view need a ViewModel?

**Decision**: Create minimal FullscreenMapViewModel

**Rationale**:
- Constitution requires MVVM-C pattern for all screens
- ViewModel provides clear extension point for future map features (zoom, pins, search)
- Even minimal ViewModel (no state) maintains consistent architecture
- Unit test placeholder demonstrates test infrastructure

**Alternatives considered**:
- No ViewModel (pure SwiftUI view): Violates MVVM-C pattern, harder to extend later

### 4. Rapid Navigation Prevention

**Question**: How to prevent multiple navigation attempts when user taps map rapidly?

**Decision**: Rely on UIKit default behavior (no custom implementation)

**Rationale**:
- UINavigationController blocks interaction during push/pop animations
- Default behavior satisfies FR-007 without custom code
- Simpler implementation, less code to maintain

**Alternatives considered**:
- Custom flag in ViewModel: Unnecessary complexity for edge case UIKit handles

### 5. Localization Keys

**Question**: What localization keys are needed?

**Decision**: Add minimal keys to Localizable.strings

**Rationale**:
- Navigation title: `fullscreenMap.navigationTitle` = "Pet Locations" (as per clarification)
- Empty placeholder: No text required (FR-009)
- Back button: Automatic from UINavigationController (uses iOS system string)

**Keys**:
```
// en.lproj/Localizable.strings
"fullscreenMap.navigationTitle" = "Pet Locations";

// pl.lproj/Localizable.strings  
"fullscreenMap.navigationTitle" = "Lokalizacje zwierząt";
```

### 6. Test Identifiers

**Question**: What accessibility identifiers are needed?

**Decision**: Minimal identifiers for E2E testing

**Rationale**:
- Map preview tap target: `landingPage.mapPreview` (already exists)
- Fullscreen map container: `fullscreenMap.container`
- Back button: Uses system back button (no custom identifier needed)

**Naming follows**: `{screen}.{element}` convention from constitution

### 7. Existing Code Impact

**Question**: What existing code needs modification?

**Decision**: Minimal changes to existing files

| File | Change |
|------|--------|
| `HomeCoordinator.swift` | Add `showFullscreenMap()` method |
| `LandingPageViewModel.swift` | Add `onShowFullscreenMap` closure, update `handleMapTap()` |
| `Localizable.strings` (en) | Add `fullscreenMap.navigationTitle` |
| `Localizable.strings` (pl) | Add `fullscreenMap.navigationTitle` |

**New files**:
- `FullscreenMapView.swift` - SwiftUI placeholder view
- `FullscreenMapViewModel.swift` - Minimal ViewModel
- `FullscreenMapViewModelTests.swift` - Unit tests

## Summary

All research questions resolved. Implementation follows existing patterns:
- Push navigation via HomeCoordinator (no child coordinator)
- Minimal ViewModel for MVVM-C compliance
- Simple flag for rapid tap prevention
- Standard accessibility identifiers

