# Data Model: Android Location Permissions Handling

**Feature**: Android Location Permissions Handling  
**Date**: 2025-11-27  
**Phase**: Phase 1 - Design & Contracts

## Entities

### LocationCoordinates

Represents the device's current geographic location (latitude/longitude) obtained from Android location services.

**Fields**:
- `latitude: Double` - Latitude coordinate (-90.0 to 90.0)
- `longitude: Double` - Longitude coordinate (-180.0 to 180.0)

**Validation Rules**:
- Latitude must be between -90.0 and 90.0 (inclusive)
- Longitude must be between -180.0 and 180.0 (inclusive)
- Both values are required (non-nullable)

**Relationships**:
- Used as optional parameter in `AnimalRepository.getAnimals(location: LocationCoordinates?)` query
- Created from Android `Location` object via `LocationCoordinates(location.latitude, location.longitude)`

**State Transitions**: N/A (immutable data class)

---

### PermissionStatus (Sealed Class)

Represents the current location permission authorization state on Android.

**States**:
- `NotRequested` - Permission has not been requested yet (first app launch)
- `Requesting` - Permission request is in progress (system dialog displayed)
- `Granted(fineLocation: Boolean, coarseLocation: Boolean)` - Permission granted
  - `fineLocation: true` when ACCESS_FINE_LOCATION granted
  - `coarseLocation: true` when ACCESS_COARSE_LOCATION granted (may be true without fineLocation on Android 12+)
- `Denied(shouldShowRationale: Boolean)` - Permission denied by user
  - `shouldShowRationale: true` when educational rationale should be shown before next request
  - `shouldShowRationale: false` when user selected "Don't ask again"

**Validation Rules**:
- Only one state active at a time (mutually exclusive)
- `Granted` state requires at least one permission type to be true
- `Denied` state always has `shouldShowRationale` flag

**State Transitions**:
```
NotRequested → Requesting (user/system triggers permission request)
Requesting → Granted (user allows permission)
Requesting → Denied (user denies permission)
Denied → Granted (user enables via Settings and returns to app)
Granted → Denied (user revokes via Settings - rare, but handled)
```

**Relationships**:
- Part of `AnimalListUiState.permissionStatus`
- Determines UI behavior (show rationale dialog, fetch location, etc.)

---

### AnimalListUiState (Extended)

Extended existing UI state to include location permission and coordinates.

**New Fields** (added to existing `AnimalListUiState`):
- `permissionStatus: PermissionStatus = PermissionStatus.NotRequested` - Current permission state
- `location: LocationCoordinates? = null` - Device's current location coordinates (null when unavailable)

**Existing Fields** (unchanged):
- `animals: List<Animal> = emptyList()` - Animal listings from server
- `isLoading: Boolean = false` - Loading indicator state
- `error: String? = null` - Error message (if any)

**Validation Rules**:
- `location` is null when permission not granted or location fetch failed
- `location` is non-null only when permission is `Granted` and location fetch succeeded
- `isLoading` is true during permission request, location fetch, or server query

**State Transitions**:
- Permission state changes trigger location fetch (if granted) or fallback to no-location query
- Location fetch success → `location` set, server query with coordinates
- Location fetch failure/timeout → `location` remains null, server query without coordinates

---

### RationaleDialogType (Sealed Class)

Represents the type of custom permission rationale dialog to display.

**Types**:
- `Educational` - Shown before system permission dialog when `shouldShowRequestPermissionRationale` returns true
- `Informational` - Shown when permission is denied (user selected "Don't Allow" or "Don't ask again")

**Validation Rules**:
- Only one dialog type displayed at a time
- Dialog displayed once per app session when applicable (per FR-015)

**Relationships**:
- Triggered via `AnimalListEffect.ShowRationaleDialog(type: RationaleDialogType)`
- User actions: "Continue" (educational) → triggers system dialog, "Not Now" (educational) → dismisses, "Go to Settings" (informational) → opens Settings, "Cancel" (informational) → dismisses

---

## Domain Models (Existing - Unchanged)

### Location (Existing)

Represents a geographic search area with city and radius. **Not modified** by this feature.

**Fields**:
- `city: String` - City or area name
- `radiusKm: Int` - Search radius in kilometers

**Note**: This model is distinct from `LocationCoordinates`. `Location` represents a search area, while `LocationCoordinates` represents device's current position.

---

### Animal (Existing)

Represents an animal listing. **Not modified** by this feature.

**Fields**: (unchanged - see existing codebase)

---

## Repository Interface Changes

### AnimalRepository (Extended)

**New Method** (added to existing interface):
```kotlin
suspend fun getAnimals(location: LocationCoordinates? = null): List<Animal>
```

**Parameters**:
- `location: LocationCoordinates?` - Optional device coordinates for location-aware filtering
  - `null` when permission denied or location unavailable → returns all animals (no location filtering)
  - Non-null when permission granted and location available → server may filter by proximity

**Behavior**:
- When `location` is null: Query server without location parameters (fallback mode)
- When `location` is non-null: Query server with latitude/longitude parameters (location-aware mode)
- Server handles location filtering logic (backend responsibility)

**Existing Method** (maintained for backward compatibility):
```kotlin
suspend fun getAnimals(): List<Animal>
```
- Defaults to `getAnimals(location = null)` (no location filtering)

---

## Use Case Models

### CheckLocationPermissionUseCase

**Input**: None (uses Android Context internally)  
**Output**: `PermissionStatus` - Current permission state

**Behavior**:
- Checks `ContextCompat.checkSelfPermission()` for ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION
- Checks `Activity.shouldShowRequestPermissionRationale()` if permission denied
- Returns appropriate `PermissionStatus` sealed class instance

---

### RequestLocationPermissionUseCase

**Input**: `Activity` (for permission request launcher)  
**Output**: `Unit` (permission request launched, result handled via callback)

**Behavior**:
- Triggers Android system permission dialog via Activity Result launcher
- Does not return permission result (handled asynchronously via callback)

---

### GetLastKnownLocationUseCase

**Input**: `Context` (for LocationManager)  
**Output**: `Result<LocationCoordinates?>` - Success with coordinates (or null if no cached location) or failure

**Behavior**:
- Uses LocationManager (native Android API) to get last known location
- Synchronous operation (returns immediately, no timeout needed)
- Tries GPS provider first (most accurate), falls back to Network provider if GPS unavailable
- Returns `Result.success(LocationCoordinates)` if cached location available
- Returns `Result.success(null)` if no cached location available (not an error - fallback to no-location mode)
- Returns `Result.failure(SecurityException)` if permission not granted
- Returns `Result.failure(Exception)` on other errors
- Handles both fine and coarse location permissions

---

## Summary

**New Entities**:
1. `LocationCoordinates` - Device location (latitude/longitude)
2. `PermissionStatus` - Permission state (sealed class)
3. `RationaleDialogType` - Dialog type (sealed class)

**Extended Entities**:
1. `AnimalListUiState` - Added `permissionStatus` and `location` fields
2. `AnimalRepository` - Added optional `location` parameter to `getAnimals()`

**No Changes**:
- `Location` (city/radius model) - remains unchanged
- `Animal` - remains unchanged
- Other domain models - remain unchanged

