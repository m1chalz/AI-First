# Quickstart: Android Location Permissions Handling

**Feature**: Android Location Permissions Handling  
**Date**: 2025-11-27  
**Phase**: Phase 1 - Design & Contracts

## Overview

This feature adds location permission handling to the Android Animal List screen (startup screen). Users can grant location permissions to enable location-aware animal listings, with graceful fallback to all animals when permissions are denied or location is unavailable.

## Key Concepts

### Permission States

The app handles four permission states:
1. **Not Requested** - First app launch, permission not yet asked
2. **Requesting** - System permission dialog displayed
3. **Granted** - User granted permission (fine or coarse location)
4. **Denied** - User denied permission (with or without "Don't ask again")

### Location Flow

```
App Launch → Check Permission Status
  ├─ Not Requested → Show System Dialog
  ├─ Denied (with rationale) → Show Educational Rationale → System Dialog
  ├─ Denied (no rationale) → Show Informational Rationale → Settings or Cancel
  └─ Granted → Get Last Known Location (immediate, cached) → Query Server with/without Coordinates
```

### Fallback Behavior

- **Permission Denied**: Query server without coordinates → Show all animals
- **No Cached Location**: Query server without coordinates → Show all animals (immediate fallback, no timeout)
- **Location Unavailable**: Query server without coordinates → Show all animals

## Architecture

### MVI Pattern

Follows Android MVI architecture:
- **UiState**: `AnimalListUiState` (extended with `permissionStatus` and `location`)
- **Intent**: `AnimalListIntent` (extended with permission-related intents)
- **Effect**: `AnimalListEffect` (extended with rationale dialog effects)
- **Reducer**: Pure function for state transitions
- **ViewModel**: Manages state flow and dispatches intents

### Components

1. **Use Cases**:
   - `CheckLocationPermissionUseCase` - Checks current permission status
   - `RequestLocationPermissionUseCase` - Triggers permission request
   - `GetLastKnownLocationUseCase` - Gets cached last known location (immediate, no timeout)

2. **Repository**:
   - `LocationRepository` - Interface for location operations
   - `LocationRepositoryImpl` - Implementation using LocationManager

3. **UI**:
   - `AnimalListScreen` - State host composable (extended)
   - `AnimalListContent` - Stateless composable (extended with permission dialogs)

## Implementation Steps

### 1. Add Dependencies

**build.gradle.kts** (module: composeApp):
```kotlin
dependencies {
    // Accompanist Permissions (declarative permission handling)
    implementation(libs.accompanist.permissions)
    
    // Note: LocationManager is part of Android framework, no dependency needed
}
```

### 2. Add Permissions to AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

### 3. Create Domain Models

**LocationCoordinates.kt**:
```kotlin
data class LocationCoordinates(
    val latitude: Double,
    val longitude: Double
)
```

**PermissionStatus.kt** (sealed class):
```kotlin
sealed class PermissionStatus {
    object NotRequested : PermissionStatus()
    object Requesting : PermissionStatus()
    data class Granted(val fineLocation: Boolean, val coarseLocation: Boolean) : PermissionStatus()
    data class Denied(val shouldShowRationale: Boolean) : PermissionStatus()
}
```

### 4. Extend Repository Interface

**AnimalRepository.kt**:
```kotlin
interface AnimalRepository {
    suspend fun getAnimals(location: LocationCoordinates? = null): List<Animal>
    // Keep existing method for backward compatibility
    suspend fun getAnimals(): List<Animal> = getAnimals(location = null)
}
```

### 5. Create Use Cases

**CheckLocationPermissionUseCase.kt**:
- Checks `ContextCompat.checkSelfPermission()` for both permissions
- Checks `shouldShowRequestPermissionRationale()` if denied
- Returns `PermissionStatus` sealed class

**GetLastKnownLocationUseCase.kt**:
- Uses `LocationManager` (native Android API)
- Gets cached last known location (synchronous, immediate)
- Tries GPS provider first, falls back to Network provider
- Returns `Result<LocationCoordinates?>` (null if no cached location available)
- No timeout needed (cached location returns immediately or null)

### 6. Extend ViewModel

**AnimalListViewModel.kt**:
- Add `permissionStatus` and `location` to `AnimalListUiState`
- Add permission-related intents: `CheckPermission`, `RequestPermission`, `PermissionResult`
- Add permission-related effects: `ShowRationaleDialog`, `OpenSettings`
- Handle permission state transitions in reducer
- Fetch location when permission granted
- Query server with/without coordinates based on location availability

### 7. Extend UI

**AnimalListScreen.kt**:
- Register `rememberLauncherForActivityResult` for permission request
- Handle permission result callback → dispatch to ViewModel
- Observe lifecycle for permission changes (user returns from Settings)

**AnimalListContent.kt**:
- Add rationale dialog composables (educational and informational)
- Add loading indicator for location fetch
- Display rationale dialogs based on `UiEffect`

### 8. Add Koin DI

**locationModule.kt**:
```kotlin
val locationModule = module {
    single<LocationRepository> { LocationRepositoryImpl(get()) }
    factory { CheckLocationPermissionUseCase(get()) }
    factory { RequestLocationPermissionUseCase() }
    factory { GetLastKnownLocationUseCase(get()) }
}
```

## Testing

### Unit Tests

**LocationRepositoryImplTest.kt**:
- Test last known location retrieval (GPS provider)
- Test fallback to Network provider when GPS returns null
- Test null location handling (no cached location)
- Test permission denied case

**GetLastKnownLocationUseCaseTest.kt**:
- Test success case with GPS location
- Test fallback to Network location
- Test null location (no cached location available)
- Test permission denied case

**AnimalListViewModelTest.kt**:
- Test permission state transitions
- Test location fetch on permission grant
- Test fallback when location unavailable
- Test rationale dialog effects

**AnimalListReducerTest.kt**:
- Test all permission state transitions
- Test location updates
- Test error handling

### E2E Tests

**android-location-permissions.feature** (Cucumber Gherkin):
- User Story 1: Location-authorized users
- User Story 2: First-time permission request
- User Story 3: Denied permissions recovery
- User Story 4: Permission rationale before system dialog
- User Story 5: Dynamic permission change handling

## Key Files

### Domain Layer
- `domain/models/LocationCoordinates.kt` - Device coordinates model
- `domain/models/PermissionStatus.kt` - Permission state sealed class
- `domain/repositories/LocationRepository.kt` - Location operations interface
- `domain/usecases/CheckLocationPermissionUseCase.kt`
- `domain/usecases/GetLastKnownLocationUseCase.kt`

### Data Layer
- `data/repositories/LocationRepositoryImpl.kt` - LocationManager implementation

### Presentation Layer
- `features/animallist/presentation/mvi/AnimalListUiState.kt` - Extended with permission/location
- `features/animallist/presentation/mvi/AnimalListIntent.kt` - Extended with permission intents
- `features/animallist/presentation/mvi/AnimalListEffect.kt` - Extended with dialog effects
- `features/animallist/presentation/mvi/AnimalListReducer.kt` - Extended with permission logic
- `features/animallist/presentation/viewmodels/AnimalListViewModel.kt` - Extended with permission handling

### UI Layer
- `features/animallist/ui/AnimalListScreen.kt` - Extended with permission launcher
- `features/animallist/ui/AnimalListContent.kt` - Extended with rationale dialogs

### DI
- `di/locationModule.kt` - Koin module for location dependencies

## Common Patterns

### Permission Request Flow

```kotlin
// In Composable
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AnimalListScreen(viewModel: AnimalListViewModel = koinViewModel()) {
    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    
    // Observe permission state changes and dispatch to ViewModel
    LaunchedEffect(locationPermissionState.allPermissionsGranted) {
        val granted = locationPermissionState.allPermissionsGranted
        viewModel.dispatchIntent(AnimalListIntent.PermissionResult(granted))
    }
    
    // Handle effects for triggering permission request
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is AnimalListEffect.RequestPermission -> {
                    locationPermissionState.launchMultiplePermissionRequest()
                }
                // ... other effects
            }
        }
    }
    
    // ... rest of UI
}

// In ViewModel
when (intent) {
    is AnimalListIntent.RequestPermission -> {
        // Trigger permission request via effect
        emitEffect(AnimalListEffect.RequestPermission)
    }
    is AnimalListIntent.PermissionResult -> {
        if (intent.granted) {
            fetchLocation()
        } else {
            updateState { it.copy(permissionStatus = PermissionStatus.Denied(...)) }
        }
    }
}
```

### Get Last Known Location

```kotlin
fun getLastKnownLocation(context: Context): Result<LocationCoordinates?> {
    return try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        
        // Try GPS provider first (most accurate)
        var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        
        // Fallback to Network provider if GPS unavailable
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }
        
        if (location != null) {
            Result.success(LocationCoordinates(location.latitude, location.longitude))
        } else {
            // No cached location available - return null (not an error)
            Result.success(null)
        }
    } catch (e: SecurityException) {
        Result.failure(e) // Permission not granted
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

### Lifecycle-Aware Permission Check

```kotlin
// In Composable
// Accompanist automatically updates permission state on lifecycle resume
// Observe permission state changes
LaunchedEffect(locationPermissionState.allPermissionsGranted) {
    val granted = locationPermissionState.allPermissionsGranted
    val shouldShowRationale = locationPermissionState.shouldShowRationale
    
    // Dispatch to ViewModel when permission state changes
    viewModel.dispatchIntent(
        AnimalListIntent.PermissionStateChanged(
            granted = granted,
            shouldShowRationale = shouldShowRationale
        )
    )
}
```

## Next Steps

1. Review `data-model.md` for complete entity definitions
2. Review `contracts/api-contract.md` for API expectations
3. Review `research.md` for technical decisions
4. Implement domain models and use cases
5. Implement repository and ViewModel
6. Extend UI with permission handling
7. Write unit tests (80% coverage target)
8. Write E2E tests (all user stories)

## References

- [Android Location Services Guide](https://developer.android.com/training/location)
- [Android Permissions Best Practices](https://developer.android.com/training/permissions/usage-notes)
- [Activity Result API](https://developer.android.com/training/basics/intents/result)
- [LocationManager](https://developer.android.com/reference/android/location/LocationManager)

