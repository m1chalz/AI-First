# Research: Android Location Permissions Handling

**Feature**: Android Location Permissions Handling  
**Date**: 2025-11-27  
**Phase**: Phase 0 - Research & Clarification

## Research Tasks

### 1. Android Location Services API Selection

**Task**: Determine best location API for getting location in Android Compose app.

**Decision**: Use **LocationManager** (native Android API) with two-stage fetching approach

**Rationale**:
- LocationManager is part of Android framework (no external dependencies)
- `getLastKnownLocation()` is synchronous and immediate (no async complexity for cached location)
- `requestSingleUpdate()` provides fresh location when cached is unavailable
- Works on all Android devices (no Google Play Services requirement)
- Supports both fine and coarse location permissions
- Aligns with dependency minimization principle (no Google Play Services dependency)

**Alternatives Considered**:
- **FusedLocationProviderClient**: Google Play Services API with automatic provider optimization
- **Rejected because**: 
  - Adds Google Play Services dependency (not available on all devices)
  - LocationManager provides all needed functionality for two-stage approach
  - FusedLocationProvider adds complexity without significant benefit for single location fetch

**Two-Stage Location Fetching Implementation**:

**Stage 1: Cached Last Known Location (Instant)**
- Use `Context.getSystemService(Context.LOCATION_SERVICE)` to obtain LocationManager
- Try `getLastKnownLocation(LocationManager.GPS_PROVIDER)` first (most accurate)
- Fallback to `getLastKnownLocation(LocationManager.NETWORK_PROVIDER)` if GPS returns null
- Check if cached location is not too old (optional: configurable staleness threshold)
- If valid cached location found → use immediately, skip Stage 2

**Stage 2: Fresh Location Update (10s timeout)**
- If Stage 1 returns null or location is too old
- Use `requestSingleUpdate()` with LocationListener callback
- Apply 10-second timeout using coroutine `withTimeout` or Handler
- Try GPS provider first, Network provider as fallback
- Cancel request after receiving location or on timeout

**Fallback**
- If both stages fail → proceed without location coordinates
- Query server with no location filtering (all animals returned)

**Implementation Notes**:
- Check location permissions before any location API calls
- Use Kotlin Coroutines for async handling of Stage 2
- Stage 1 is synchronous (instant return)
- Stage 2 is asynchronous with timeout
- LocationListener receives location updates in Stage 2
- Cancel pending location request on timeout to free resources

---

### 2. Permission Request Flow Patterns in Android Compose

**Task**: Determine best pattern for handling permission requests in Jetpack Compose with MVI architecture.

**Decision**: Use **Accompanist Permissions library** with **rememberMultiplePermissionsState** for declarative permission handling

**Rationale**:
- Accompanist Permissions provides declarative, Compose-native API that fits well with reactive UI patterns
- Google-maintained library (high source reputation, well-maintained)
- Reduces boilerplate code compared to Activity Result API
- Built-in state management for permission status (granted, denied, shouldShowRationale)
- Lifecycle-aware permission state that automatically updates
- `rememberMultiplePermissionsState` handles both FINE and COARSE location permissions together
- Declarative approach aligns with Compose philosophy (state-driven UI)
- Permission state can be observed directly in Composable, dispatched to ViewModel when needed

**Pattern Structure**:
1. Composable uses `rememberMultiplePermissionsState` for location permissions
2. Permission state observed in Composable (declarative)
3. ViewModel checks permission status via use case (for initial state)
4. ViewModel determines if rationale needed, emits effect for custom dialog
5. Composable triggers permission request via `permissionState.launchMultiplePermissionRequest()`
6. Permission state updates automatically → Composable observes change → dispatches to ViewModel
7. ViewModel updates state based on permission result

**Alternatives Considered**:
- **Activity Result API with rememberLauncherForActivityResult**: Native Android approach, no dependencies
- **Rejected because**: More boilerplate code, less declarative, requires manual state management. Accompanist provides better developer experience and fits Compose patterns better.

**Implementation Notes**:
- Add dependency: `com.google.accompanist:accompanist-permissions` (latest stable version)
- Use `@OptIn(ExperimentalPermissionsApi::class)` annotation (API is experimental but stable)
- Use `rememberMultiplePermissionsState(permissions = listOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))`
- Check `permissionState.allPermissionsGranted` for granted state
- Check `permissionState.shouldShowRationale` for rationale display
- Permission state changes trigger recomposition automatically
- Dispatch permission result to ViewModel via intent when state changes
- Custom rationale dialogs triggered via `UiEffect` from ViewModel

---

### 3. Lifecycle-Aware Permission Observation

**Task**: Determine how to observe permission changes while app is in foreground (e.g., user returns from Settings).

**Decision**: Use **Accompanist Permissions automatic state updates** + **LaunchedEffect** to observe permission state changes

**Rationale**:
- Accompanist `rememberMultiplePermissionsState` automatically updates when permission status changes
- When user returns from Settings after granting permission, Accompanist detects the change automatically
- Permission state is reactive - Composable recomposes when state changes
- Use `LaunchedEffect` with permission state as key to observe changes
- When permission state changes from denied to granted, dispatch intent to ViewModel
- ViewModel handles permission change, fetches location, refreshes data

**Pattern Structure**:
1. Composable uses `rememberMultiplePermissionsState` (lifecycle-aware, automatically updates)
2. Use `LaunchedEffect(permissionState.allPermissionsGranted)` to observe permission changes
3. When permission state changes (user returns from Settings), effect triggers
4. Dispatch intent to ViewModel with new permission status
5. ViewModel handles permission change, fetches location, refreshes data

**Alternatives Considered**:
- **Manual lifecycle callbacks with onResume()**: Requires manual state tracking and comparison
- **Rejected because**: Accompanist handles this automatically, less boilerplate, more reliable

**Implementation Notes**:
- Accompanist permission state automatically updates on lifecycle resume
- Use `LaunchedEffect(permissionState.allPermissionsGranted)` to observe changes
- Compare previous state with current state to detect transitions (denied → granted)
- Only trigger location fetch if permission changed from denied to granted (avoid unnecessary fetches)
- Permission state is already lifecycle-aware (no manual lifecycle observer needed)

---

### 4. Permission State Management in MVI

**Task**: Determine how to model permission states in MVI architecture (UiState, Intent, Effect).

**Decision**: Include permission state in `UiState`, use sealed intents for permission actions, use effects for dialogs

**Rationale**:
- Permission status is part of screen state (affects UI rendering)
- Permission checking and requesting are user/system actions → map to intents
- Custom rationale dialogs are one-off events → use `UiEffect`
- System permission dialog triggered by Activity Result launcher (not ViewModel effect)
- Permission state transitions: NotRequested → Requesting → Granted/Denied

**State Structure**:
```kotlin
sealed class PermissionStatus {
    object NotRequested : PermissionStatus()
    object Requesting : PermissionStatus()
    data class Granted(val fineLocation: Boolean, val coarseLocation: Boolean) : PermissionStatus()
    data class Denied(val shouldShowRationale: Boolean) : PermissionStatus()
}

data class StartupUiState(
    val permissionStatus: PermissionStatus = PermissionStatus.NotRequested,
    val location: LocationCoordinates? = null,
    val isLoading: Boolean = false,
    // ... other state
)
```

**Intent Structure**:
```kotlin
sealed interface StartupIntent {
    object CheckPermission : StartupIntent
    object RequestPermission : StartupIntent
    data class PermissionResult(val granted: Boolean) : StartupIntent
    // ... other intents
}
```

**Effect Structure**:
```kotlin
sealed interface StartupEffect {
    data class ShowRationaleDialog(val type: RationaleType) : StartupEffect
    object OpenSettings : StartupEffect
    // ... other effects
}
```

**Alternatives Considered**:
- **Separate permission state outside UiState**: Would require separate state flow
- **Rejected because**: Permission state affects UI rendering, should be part of unified UiState

---

### 5. Location Coordinates Domain Model

**Task**: Determine location coordinates model structure (separate from existing Location city/radius model).

**Decision**: Create new `LocationCoordinates` data class for latitude/longitude, keep existing `Location` model for city/radius

**Rationale**:
- Existing `Location` model represents city/radius (search area), not device coordinates
- Need separate model for device's current location (latitude/longitude)
- Clear separation of concerns: `LocationCoordinates` for device location, `Location` for search area
- `LocationCoordinates` used for server API query parameters
- `Location` used for search filtering (future feature)

**Model Structure**:
```kotlin
data class LocationCoordinates(
    val latitude: Double,
    val longitude: Double
)
```

**Alternatives Considered**:
- **Extend existing Location model**: Would mix device coordinates with search area
- **Rejected because**: Different use cases, different semantics, violates single responsibility

---

## Summary

All research tasks completed. Key decisions:
1. **LocationManager** (native Android API) for getting last known location (no dependencies, simpler for cached location)
2. **Accompanist Permissions library** with `rememberMultiplePermissionsState` for declarative permission handling (Google-maintained, Compose-native API)
3. **Lifecycle callbacks** (onResume) for detecting permission changes from Settings
4. **MVI state management** with permission status in UiState, intents for actions, effects for dialogs
5. **Separate LocationCoordinates model** for device coordinates (distinct from Location city/radius model)

All decisions align with Android best practices and MVI architecture requirements. LocationManager chosen for simplicity and zero dependencies (perfect for last known location use case). Accompanist Permissions chosen for better developer experience and declarative Compose patterns, despite adding a dependency (justified by Google maintenance and significant value).

