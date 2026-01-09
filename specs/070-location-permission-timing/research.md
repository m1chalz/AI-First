# Research: Location Permission Request Timing

**Feature**: 070-location-permission-timing  
**Date**: 2026-01-08  
**Updated**: 2026-01-08 (simplified approach)

## Research Questions

### RQ-1: Should permission state be shared across screens?

**Decision**: No - let Android system be the source of truth

**Rationale**: 
- Android system already maintains permission state
- Each screen can check permission status via `ContextCompat.checkSelfPermission()` or Accompanist
- iOS uses fire-and-forget pattern in SceneDelegate - no state sharing
- CompositionLocal/shared ViewModel would be over-engineering

**Key Insight**: The system is already the source of truth. We don't need to duplicate it in app state.

### RQ-2: Should rationale dialogs be centralized at MainScaffold?

**Decision**: No - screens can manage their own rationale dialogs

**Rationale**:
- Android's `shouldShowRequestPermissionRationale()` already manages when rationale is appropriate
- Accompanist wraps this as `shouldShowRationale` property
- The system won't keep showing rationale unnecessarily:
  - Returns `true` only if user denied before (without "Don't ask again")
  - Returns `false` after permission granted OR after "Don't ask again"
- Each screen can decide if/how to show rationale based on its needs

**Benefit**: Screens remain self-contained and independently testable.

### RQ-3: What exactly needs to change?

**Decision**: Minimal change - add fire-and-forget request to MainScaffold, simplify AnimalListScreen

**Current Flow (AnimalListScreen)**:
1. Check permission status
2. If not determined → request permission
3. If denied with shouldShowRationale → show educational rationale → request
4. If denied without shouldShowRationale → show informational rationale → Settings
5. Fetch location if granted

**New Flow**:

**MainScaffold**:
1. On first composition, if permission not determined → fire-and-forget request

**AnimalListScreen** (simplified):
1. Check permission status (system already has the answer from MainScaffold request)
2. If denied with shouldShowRationale → show educational rationale → request
3. If denied without shouldShowRationale → show informational rationale → Settings
4. Fetch location if granted

**What changes**:
- Initial permission request moves from AnimalListScreen to MainScaffold
- Rationale dialog logic stays in AnimalListScreen (or any screen that needs it)
- No new ViewModel, no CompositionLocal, no state sharing

### RQ-4: How does iOS handle this?

**iOS Pattern** (for reference):
```swift
// SceneDelegate.swift - fire-and-forget at app launch
Task {
    let locationService = LocationService()
    _ = await locationService.requestWhenInUseAuthorization()
}

// Each ViewModel checks permission when loading data
// No shared permission state between screens
```

Android should follow the same pattern.

## Simplified Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│  MainScaffold                                                    │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  LaunchedEffect(Unit) {                                      ││
│  │    if (permission not determined) {                          ││
│  │      permissionState.launchMultiplePermissionRequest()       ││
│  │    }                                                         ││
│  │  }                                                           ││
│  └─────────────────────────────────────────────────────────────┘│
│                              │                                   │
│                              ▼                                   │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  NavHost                                                     ││
│  │  ├── AnimalListScreen                                        ││
│  │  │   └── Checks permission, shows rationale if needed        ││
│  │  ├── HomeScreen                                              ││
│  │  │   └── Checks permission when loading map data             ││
│  │  └── ...                                                     ││
│  └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

## What We're NOT Doing (Rejected Over-Engineering)

| Rejected Approach | Why Rejected |
|-------------------|--------------|
| LocationPermissionViewModel | No need - system is source of truth |
| CompositionLocal for permission state | Over-engineering - screens check system directly |
| Centralized rationale dialogs | Not needed - system manages when to show rationale |
| Shared state synchronization | Unnecessary complexity |

## Implementation Summary

**Files to modify**:
1. `MainScaffold.kt` - Add fire-and-forget permission request (~15 lines)
2. `AnimalListScreen.kt` - Remove initial permission request, keep rationale logic (~10 lines removed)

**Files NOT needed**:
- ~~LocationPermissionViewModel~~ 
- ~~LocationPermissionHost~~
- ~~LocationPermissionState~~
- ~~CompositionLocal~~

**Estimated effort**: Much smaller than original estimate. This is essentially moving ~15 lines of code.

## Risk Assessment

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| User sees permission dialog twice | Very Low | Low | System prevents duplicate requests |
| Rationale shown unexpectedly | Very Low | Low | System manages shouldShowRationale |
| AnimalListScreen breaks | Low | Medium | Keep rationale logic intact, only remove initial request |

## Conclusion

The solution is dramatically simpler than originally planned:
1. Add Accompanist permission state to MainScaffold
2. Fire-and-forget request if permission not determined
3. Keep existing rationale logic in screens
4. No new ViewModels, no state sharing, no CompositionLocal

This matches iOS's fire-and-forget pattern exactly.
