# Implementation Plan: Location Permission Request Timing

**Branch**: `070-location-permission-timing` | **Date**: 2026-01-08 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/070-location-permission-timing/spec.md`

## Summary

Move Android location permission request from `AnimalListScreen` (Lost Pet tab) to `MainScaffold` (main screen with bottom navigation) to match iOS timing. Use fire-and-forget pattern - no state sharing needed.

**Technical Approach**: 
1. Add Accompanist permission state to MainScaffold
2. Fire-and-forget request if permission not determined
3. Keep existing rationale logic in screens (system manages when to show)
4. Remove initial permission request from AnimalListScreen

## Technical Context

**Language/Version**: Kotlin 1.9+  
**Primary Dependencies**: Jetpack Compose, Accompanist Permissions  
**Storage**: N/A  
**Testing**: JUnit 6 + Kotlin Test + Turbine (Flow testing)  
**Target Platform**: Android (minSdk 26, targetSdk 36)  
**Project Type**: Mobile (Android platform only for this feature)  
**Performance Goals**: N/A (Performance is not a concern for this project - see Principle XIV)  
**Constraints**: N/A  
**Scale/Scope**: N/A

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: Changes confined to `/composeApp` only
  - iOS: No changes (already correct timing)
  - NO shared compiled code between platforms

- [x] **Android MVI Architecture**: No ViewModel changes needed
  - Existing `AnimalListViewModel` unchanged
  - Only UI layer change in MainScaffold and AnimalListScreen

- [ ] **iOS MVVM-C Architecture**: N/A - iOS not affected

- [x] **Interface-Based Design**: No new interfaces needed

- [x] **Dependency Injection**: No new DI registrations needed

- [x] **80% Test Coverage - Platform-Specific**: Existing tests maintained
  - No new ViewModel = no new ViewModel tests needed
  - Existing AnimalListViewModel tests still valid

- [ ] **End-to-End Tests**: E2E tests not required for this internal refactoring
  - Violation justification: Minimal code change, existing E2E tests validate behavior

- [x] **Asynchronous Programming Standards**: Kotlin Coroutines used
  - LaunchedEffect for permission request

- [x] **Test Identifiers for UI Controls**: No new UI elements

- [x] **Public API Documentation**: No new public APIs

- [x] **Given-When-Then Test Structure**: N/A - no new tests needed

### Backend/Web Architecture

All N/A - only Android affected.

## Project Structure

### Documentation (this feature)

```text
specs/070-location-permission-timing/
├── plan.md              # This file
├── research.md          # Simplified approach research
├── quickstart.md        # Implementation guide
└── tasks.md             # Phase 2 output (created by /speckit.tasks)
```

### Source Code Changes

```text
/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/
└── ui/
    └── navigation/
        └── MainScaffold.kt       # MODIFY: Add fire-and-forget permission request
        
/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/
└── features/
    └── animallist/
        └── ui/
            └── AnimalListScreen.kt  # MODIFY: Remove initial permission request
```

**What's NOT changing** (removed from original plan):
- ~~LocationPermissionViewModel~~ - Not needed
- ~~LocationPermissionHost~~ - Not needed
- ~~LocationPermissionState~~ - Not needed
- ~~CompositionLocal~~ - Not needed
- ~~New Koin module~~ - Not needed

## Architecture Decision: Fire-and-Forget Pattern

### Decision: Match iOS pattern - no state sharing

**Rationale**:
1. Android system already maintains permission state
2. Each screen can check `ContextCompat.checkSelfPermission()` or Accompanist
3. iOS uses fire-and-forget in SceneDelegate - we should match
4. CompositionLocal/shared ViewModel would be over-engineering

### Why Rationale Dialogs Can Stay in Screens

Android's `shouldShowRequestPermissionRationale()` (wrapped by Accompanist) already manages:
- Returns `true` only if user denied before (without "Don't ask again")
- Returns `false` after permission granted OR after "Don't ask again"
- System won't keep bugging the user

Screens can independently decide when to show rationale - no centralization needed.

## Component Flow

```
┌─────────────────────────────────────────────────────────────────┐
│  MainScaffold                                                    │
│                                                                  │
│  LaunchedEffect(Unit) {                                         │
│    if (permission not determined) {                             │
│      permissionState.launchMultiplePermissionRequest()  ←───────│── Fire-and-forget
│    }                                                             │
│  }                                                               │
│                                                                  │
│  NavHost                                                         │
│  ├── AnimalListScreen                                            │
│  │   └── Checks permission (system has answer)                   │
│  │   └── Shows rationale if shouldShowRationale = true          │
│  │   └── Fetches location if granted                            │
│  ├── HomeScreen                                                  │
│  │   └── Checks permission when loading map                     │
│  └── ...                                                         │
└─────────────────────────────────────────────────────────────────┘
```

## Key Implementation Notes

### 1. MainScaffold Changes (~15 lines)

```kotlin
@Composable
fun MainScaffold(modifier: Modifier = Modifier) {
    // Add permission state
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    )
    
    // Fire-and-forget: request if not determined
    var hasRequestedPermission by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!hasRequestedPermission && !permissionState.allPermissionsGranted && !permissionState.shouldShowRationale) {
            hasRequestedPermission = true
            permissionState.launchMultiplePermissionRequest()
        }
    }
    
    // ... rest of existing MainScaffold code unchanged ...
}
```

### 2. AnimalListScreen Changes (~10 lines removed)

Remove the initial permission request logic:
```kotlin
// REMOVE this block:
LaunchedEffect(anyGranted, shouldShowRationale) {
    if (hasHandledInitialPermission) return@LaunchedEffect
    hasHandledInitialPermission = true
    
    if (anyGranted) { ... }
    else if (shouldShowRationale) { ... }
    else {
        // REMOVE: Initial request - now done in MainScaffold
        locationPermissionState.launchMultiplePermissionRequest()
    }
}
```

Keep the rationale dialog logic intact - it will still work based on system state.

### 3. What Stays the Same

- Rationale dialog components (EducationalRationaleDialog, InformationalRationaleDialog)
- AnimalListViewModel permission handling
- Permission callback in rememberMultiplePermissionsState
- Dynamic permission change detection (snapshotFlow)

## Revised Estimation

| Metric | Original | Revised | Reason |
|--------|----------|---------|--------|
| Story Points | 2 | 1 | Much simpler - just moving ~15 lines |
| Budget | 10.4 days | 5.2 days | No new ViewModel, no state management |
| Files Changed | 8+ | 2 | Only MainScaffold.kt and AnimalListScreen.kt |
| New Files | 4 | 0 | No new files needed |

## Complexity Tracking

No constitution violations. Simplified approach reduces complexity significantly.

| Original Plan | Simplified Plan |
|---------------|-----------------|
| New ViewModel with MVI | No new ViewModel |
| CompositionLocal state sharing | No state sharing |
| 4 new files | 0 new files |
| New Koin module registration | No DI changes |
| Complex state synchronization | Fire-and-forget pattern |
