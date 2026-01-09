# Quickstart Guide: Location Permission Request Timing

**Feature**: 070-location-permission-timing  
**Date**: 2026-01-08  
**Updated**: 2026-01-08 (simplified approach)

## Overview

Move Android's location permission request from `AnimalListScreen` (Lost Pet tab) to `MainScaffold` (main screen with bottom navigation) using a fire-and-forget pattern.

**Scope**: 2 files changed, ~15 lines added, ~10 lines removed.

## Prerequisites

- Android Studio with Kotlin 1.9+
- Project builds successfully: `./gradlew :composeApp:assembleDebug`
- Existing tests pass: `./gradlew :composeApp:testDebugUnitTest`

---

## Step 1: Modify MainScaffold.kt

Add Accompanist permission state and fire-and-forget request.

**File**: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/ui/navigation/MainScaffold.kt`

### 1.1 Add imports

```kotlin
import android.Manifest
import androidx.compose.runtime.mutableStateOf
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
```

### 1.2 Add permission logic at the start of MainScaffold

```kotlin
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScaffold(modifier: Modifier = Modifier) {
    // === NEW: Fire-and-forget permission request ===
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    )
    
    // Request permission once if not yet determined
    // (not granted AND shouldShowRationale is false = first time)
    var hasRequestedPermission by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val notDetermined = !permissionState.allPermissionsGranted && 
                           !permissionState.shouldShowRationale
        if (!hasRequestedPermission && notDetermined) {
            hasRequestedPermission = true
            permissionState.launchMultiplePermissionRequest()
        }
    }
    // === END NEW ===
    
    val navController = rememberNavController()
    // ... rest of existing code unchanged ...
}
```

---

## Step 2: Simplify AnimalListScreen.kt

Remove the initial permission request since it's now handled by MainScaffold.

**File**: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/animallist/ui/AnimalListScreen.kt`

### 2.1 Modify the initial permission check LaunchedEffect

**Before** (lines ~108-137):
```kotlin
// Initial permission check
LaunchedEffect(anyGranted, shouldShowRationale) {
    if (hasHandledInitialPermission) return@LaunchedEffect
    hasHandledInitialPermission = true

    if (anyGranted) {
        viewModel.dispatchIntent(
            AnimalListIntent.PermissionResult(
                granted = true,
                fineLocation = fineGranted,
                coarseLocation = coarseGranted,
                shouldShowRationale = false,
            ),
        )
    } else if (shouldShowRationale) {
        viewModel.dispatchIntent(
            AnimalListIntent.PermissionResult(
                granted = false,
                fineLocation = false,
                coarseLocation = false,
                shouldShowRationale = true,
            ),
        )
    } else {
        // First time - show system dialog  <-- REMOVE THIS BRANCH
        locationPermissionState.launchMultiplePermissionRequest()
    }
}
```

**After**:
```kotlin
// Initial permission check - MainScaffold handles first-time request
LaunchedEffect(anyGranted, shouldShowRationale) {
    if (hasHandledInitialPermission) return@LaunchedEffect
    hasHandledInitialPermission = true

    // Dispatch current permission status to ViewModel
    viewModel.dispatchIntent(
        AnimalListIntent.PermissionResult(
            granted = anyGranted,
            fineLocation = fineGranted,
            coarseLocation = coarseGranted,
            shouldShowRationale = shouldShowRationale,
        ),
    )
}
```

### 2.2 Keep everything else unchanged

The following should remain as-is:
- Rationale dialog display logic
- `rememberMultiplePermissionsState` callback
- Dynamic permission change detection (snapshotFlow)
- Effect handlers for navigation and dialogs

---

## Step 3: Verify

### Build
```bash
./gradlew :composeApp:assembleDebug
```

### Run Tests
```bash
./gradlew :composeApp:testDebugUnitTest
```

### Manual Testing Checklist

- [ ] Fresh install: Permission dialog appears when main screen loads
- [ ] Fresh install: Navigating to Lost Pet tab does NOT show another permission dialog
- [ ] After denial: Rationale dialog appears on Lost Pet tab (as before)
- [ ] After "Don't ask again": Informational dialog with Settings link appears
- [ ] Permission grant: Location is used for animal listings

---

## Summary of Changes

| File | Lines Added | Lines Removed | Change |
|------|-------------|---------------|--------|
| `MainScaffold.kt` | ~15 | 0 | Add fire-and-forget permission request |
| `AnimalListScreen.kt` | 0 | ~10 | Remove initial request, simplify LaunchedEffect |

## What's NOT Changed

- `AnimalListViewModel` - No changes needed
- Rationale dialog components - Unchanged
- Permission callback logic - Unchanged
- Dynamic permission detection - Unchanged
- Tests - Existing tests should still pass

## Troubleshooting

### Permission dialog appears twice
**Unlikely** - System prevents duplicate requests. Check `hasRequestedPermission` flag is working.

### Rationale not showing
**Check**: `shouldShowRationale` value in AnimalListScreen. It's only true if user denied without "Don't ask again".

### Permission status not detected in AnimalListScreen
**Check**: Ensure you didn't remove the `rememberMultiplePermissionsState` from AnimalListScreen - it's still needed to check current status.
