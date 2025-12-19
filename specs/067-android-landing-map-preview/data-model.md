# Data Model: Android Landing Page Map Preview

**Date**: 2025-12-19  
**Feature**: 067-android-landing-map-preview

## Domain Entities (ALL EXISTING - REUSE)

### Animal (EXISTING)

The existing `Animal` model has everything needed for map pins. **No new domain model needed.**

```kotlin
// EXISTING: composeapp/domain/models/Animal.kt
data class Animal(
    val id: String,
    val name: String,
    val photoUrl: String,
    val location: Location,      // Has latitude/longitude for map pins
    val species: String,
    val breed: String,
    val gender: AnimalGender,
    val status: AnimalStatus,    // MISSING/FOUND/CLOSED for pin colors
    val lastSeenDate: String,
    // ... other fields
)
```

### Location (EXISTING)

```kotlin
// EXISTING: composeapp/domain/models/Location.kt
data class Location(
    val latitude: Double? = null,
    val longitude: Double? = null,
)
```

### AnimalStatus (EXISTING)

```kotlin
// EXISTING: composeapp/domain/models/AnimalStatus.kt
@Serializable
enum class AnimalStatus(val displayName: String, val badgeColor: String) {
    MISSING("MISSING", "#FF0000"),  // Red - actively missing
    FOUND("FOUND", "#0074FF"),      // Blue - animal found
    CLOSED("CLOSED", "#93A2B4"),    // Gray - case closed
}
```

### Extension for Google Maps

```kotlin
// NEW: Extension to convert Animal location to Google Maps LatLng
fun Animal.toLatLng(): LatLng? {
    val lat = location.latitude ?: return null
    val lng = location.longitude ?: return null
    return LatLng(lat, lng)
}
```

### LocationCoordinates (EXISTING - REUSE)

Represents the user's current location. **Already exists** in the codebase.

```kotlin
// EXISTING: domain/models/LocationCoordinates.kt
data class LocationCoordinates(
    val latitude: Double,
    val longitude: Double,
) {
    init {
        require(latitude in -90.0..90.0) { "Latitude must be between -90.0 and 90.0" }
        require(longitude in -180.0..180.0) { "Longitude must be between -180.0 and 180.0" }
    }
}

// Extension for Google Maps integration
fun LocationCoordinates.toLatLng(): LatLng = LatLng(latitude, longitude)
```

---

## MVI State

### MapPreviewUiState

Immutable state representing the entire map preview component. Uses existing `Animal` model directly.

```kotlin
// Location: features/mapPreview/presentation/mvi/MapPreviewUiState.kt
data class MapPreviewUiState(
    val permissionStatus: PermissionStatus = PermissionStatus.NotRequested,  // EXISTING sealed class
    val isLoading: Boolean = false,
    val userLocation: LocationCoordinates? = null,  // EXISTING domain model
    val animals: List<Animal> = emptyList(),        // EXISTING domain model
    val error: MapPreviewError? = null
) {
    companion object {
        val Initial = MapPreviewUiState()
    }
    
    // Derived: animals with valid coordinates for map display
    val animalsWithLocation: List<Animal>
        get() = animals.filter { 
            it.location.latitude != null && it.location.longitude != null 
        }
}

sealed interface MapPreviewError {
    data object LocationNotAvailable : MapPreviewError
    data object NetworkError : MapPreviewError
    data object MapLoadFailed : MapPreviewError
}
```

**Note**: Uses existing `PermissionStatus` sealed class from `domain/models/PermissionStatus.kt`.

| Field | Type | Default | Description |
|-------|------|---------|-------------|
| `permissionStatus` | PermissionStatus | NotRequested | Location permission state (EXISTING) |
| `isLoading` | Boolean | false | Loading indicator |
| `userLocation` | LocationCoordinates? | null | User's current location (EXISTING) |
| `animals` | List<Animal> | emptyList() | Animals from repository (EXISTING) |
| `error` | MapPreviewError? | null | Current error state |

### State Transitions

```
Initial (NOT_REQUESTED)
    │
    ├── [Permission Granted] → Loading → Success (pins loaded)
    │                           │
    │                           └── Error (network/location)
    │
    └── [Permission Denied] → Permission Request UI
                                │
                                ├── [Grant] → Loading → Success
                                │
                                └── [Deny] → Denied State
```

---

## MVI Intents

### MapPreviewIntent

Sealed class capturing all user interactions.

```kotlin
// Location: features/mapPreview/presentation/mvi/MapPreviewIntent.kt
sealed interface MapPreviewIntent {
    data object LoadMap : MapPreviewIntent
    data object RequestPermission : MapPreviewIntent
    data object PermissionGranted : MapPreviewIntent
    data object PermissionDenied : MapPreviewIntent
    data object Retry : MapPreviewIntent
}
```

| Intent | Trigger | Handler Action |
|--------|---------|----------------|
| `LoadMap` | On composition with permission | Fetch location + pins |
| `RequestPermission` | "Enable Location" button tap | Launch system dialog |
| `PermissionGranted` | System callback (granted) | Update state, trigger LoadMap |
| `PermissionDenied` | System callback (denied) | Update state to denied |
| `Retry` | Retry button tap | Re-attempt LoadMap |

---

## MVI Effects

### MapPreviewEffect

One-off events (currently none needed for this feature).

```kotlin
// Location: features/mapPreview/presentation/mvi/MapPreviewEffect.kt
sealed interface MapPreviewEffect {
    // Reserved for future use (e.g., navigation to full map)
    // data class NavigateToFullMap(val location: LatLng) : MapPreviewEffect
}
```

---

## Repository (EXISTING - REUSE)

### AnimalRepository (EXISTING)

**No new repository needed!** The existing `AnimalRepository` already has location-based filtering.

```kotlin
// EXISTING: composeapp/domain/repositories/AnimalRepository.kt
interface AnimalRepository {
    /**
     * Retrieves all animals from the data source.
     * Optionally filters by location when lat/lng are provided.
     *
     * @param lat Optional latitude for location-based filtering
     * @param lng Optional longitude for location-based filtering
     * @param range Optional search radius in kilometers
     * @return List of animals
     */
    suspend fun getAnimals(
        lat: Double? = null,
        lng: Double? = null,
        range: Int? = null,
    ): List<Animal>
}
```

### Usage for Map Preview

```kotlin
// Use existing repository with 10km range
val animals = animalRepository.getAnimals(
    lat = userLocation.latitude,
    lng = userLocation.longitude,
    range = 10
)

// Filter animals with valid locations for map display
val pinsForMap = animals.filter { it.location.latitude != null && it.location.longitude != null }
```

---

## Use Case

### GetNearbyAnimalsForMapUseCase

Business logic for fetching nearby animals. **Wraps existing `AnimalRepository`.**

```kotlin
// Location: features/mapPreview/domain/usecases/GetNearbyAnimalsForMapUseCase.kt
class GetNearbyAnimalsForMapUseCase(
    private val animalRepository: AnimalRepository  // EXISTING repository
) {
    companion object {
        const val DEFAULT_RADIUS_KM = 10
    }
    
    suspend operator fun invoke(
        location: LocationCoordinates,
        radiusKm: Int = DEFAULT_RADIUS_KM
    ): Result<List<Animal>> = runCatching {
        animalRepository.getAnimals(
            lat = location.latitude,
            lng = location.longitude,
            range = radiusKm
        )
    }
}
```

---

## Koin Module

### MapPreviewModule

Dependency injection configuration. **Reuses existing modules - minimal additions.**

```kotlin
// Location: di/MapPreviewModule.kt
val mapPreviewModule = module {
    // Use Case (NEW) - wraps existing AnimalRepository
    factory { 
        GetNearbyAnimalsForMapUseCase(get())  // get() injects AnimalRepository (from animalModule)
    }
    
    // ViewModel (NEW)
    viewModel { 
        MapPreviewViewModel(
            getCurrentLocationUseCase = get(),        // FROM locationModule
            checkLocationPermissionUseCase = get(),   // FROM locationModule
            getNearbyAnimalsForMapUseCase = get()     // FROM mapPreviewModule
        )
    }
}
```

### Existing Modules (REUSE - no changes needed)

```kotlin
// EXISTING: di/LocationModule.kt
val locationModule = module {
    single { androidContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    single<PermissionChecker> { AndroidPermissionChecker(androidContext()) }
    single<LocationRepository> { LocationRepositoryImpl(get()) }
    factory { GetCurrentLocationUseCase(get()) }
    factory { CheckLocationPermissionUseCase(get()) }
}

// EXISTING: di/AnimalModule.kt (or wherever AnimalRepository is defined)
// single<AnimalRepository> { AnimalRepositoryImpl(get(), get()) }
```

---

## Relationships

```
┌─────────────────────────────────────────────────────────────────┐
│                        MapPreviewSection                        │
│                     (Stateful Composable)                       │
│                             │                                   │
│                             ▼                                   │
│                    MapPreviewViewModel                          │
│              ┌──────────────┼──────────────┐                   │
│              ▼              ▼              ▼                   │
│   GetNearbyAnimalsForMapUseCase   GetCurrentLocationUseCase     │
│              │              (EXISTING)     CheckLocationPermissionUseCase
│              ▼                             (EXISTING)           │
│     AnimalRepository (EXISTING)                                 │
│              │                                                  │
│              ▼                                                  │
│    AnnouncementApiClient (EXISTING)                             │
│              │                                                  │
│              ▼                                                  │
│      Backend API: GET /api/v1/announcements?lat=&lng=&range=    │
└─────────────────────────────────────────────────────────────────┘
```

**Summary**: Only 2 new components needed:
1. `GetNearbyAnimalsForMapUseCase` - thin wrapper around existing `AnimalRepository`
2. `MapPreviewViewModel` - MVI ViewModel for the feature

---

## Validation Rules

All validation already exists in domain models:

| Entity | Field | Rule | Source |
|--------|-------|------|--------|
| Animal.location | latitude | Optional, -90 to 90 | Existing model |
| Animal.location | longitude | Optional, -180 to 180 | Existing model |
| Animal | id | Required, not empty | Existing model |
| LocationCoordinates | latitude | Required, -90 to 90 | Existing model with `require()` |
| LocationCoordinates | longitude | Required, -180 to 180 | Existing model with `require()` |

---

## Test Data

### Sample Animals for Map Preview

```kotlin
// For unit tests and previews - use existing Animal model
val sampleAnimals = listOf(
    Animal(
        id = "1", 
        name = "Buddy",
        photoUrl = "https://example.com/buddy.jpg",
        location = Location(latitude = 52.2297, longitude = 21.0122),  // Warsaw
        species = "Dog",
        breed = "Golden Retriever",
        gender = AnimalGender.MALE,
        status = AnimalStatus.MISSING,
        lastSeenDate = "19/12/2025",
        description = "Friendly dog, last seen near park",
        email = "owner@example.com",
        phone = "123456789"
    ),
    // ... more sample animals
)

val sampleUserLocation = LocationCoordinates(52.2297, 21.0122) // Warsaw center
```

