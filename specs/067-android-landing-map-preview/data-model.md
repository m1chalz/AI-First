# Data Model: Android Landing Page Map Preview

**Date**: 2025-12-19  
**Feature**: 067-android-landing-map-preview

## Domain Entities

### MapPin

Represents a single pet announcement pin on the map. Uses existing `AnimalStatus` enum.

```kotlin
// Location: features/mapPreview/domain/models/MapPin.kt
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus

data class MapPin(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val status: AnimalStatus  // MISSING (red), FOUND (blue), CLOSED (gray)
)
```

| Field | Type | Description | Constraints |
|-------|------|-------------|-------------|
| `id` | String | Unique announcement identifier | Required, from backend |
| `latitude` | Double | GPS latitude | Required, -90 to 90 |
| `longitude` | Double | GPS longitude | Required, -180 to 180 |
| `status` | AnimalStatus | Pet status (MISSING/FOUND/CLOSED) | Required, reuses existing enum |

### AnimalStatus (EXISTING - REUSE)

```kotlin
// EXISTING: composeapp/domain/models/AnimalStatus.kt
@Serializable
enum class AnimalStatus(val displayName: String, val badgeColor: String) {
    MISSING("MISSING", "#FF0000"),  // Red - actively missing
    FOUND("FOUND", "#0074FF"),      // Blue - animal found
    CLOSED("CLOSED", "#93A2B4"),    // Gray - case closed
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

Immutable state representing the entire map preview component.

```kotlin
// Location: features/mapPreview/presentation/mvi/MapPreviewUiState.kt
data class MapPreviewUiState(
    val permissionStatus: PermissionStatus = PermissionStatus.NOT_REQUESTED,
    val isLoading: Boolean = false,
    val userLocation: LatLng? = null,
    val pins: List<MapPin> = emptyList(),
    val error: MapPreviewError? = null
) {
    companion object {
        val Initial = MapPreviewUiState()
    }
}

enum class PermissionStatus {
    NOT_REQUESTED,
    GRANTED,
    DENIED,
    DENIED_PERMANENTLY
}

sealed interface MapPreviewError {
    data object LocationNotAvailable : MapPreviewError
    data object NetworkError : MapPreviewError
    data object MapLoadFailed : MapPreviewError
}
```

| Field | Type | Default | Description |
|-------|------|---------|-------------|
| `permissionStatus` | PermissionStatus | NOT_REQUESTED | Location permission state |
| `isLoading` | Boolean | false | Loading indicator |
| `userLocation` | LatLng? | null | User's current location |
| `pins` | List<MapPin> | emptyList() | Announcement pins to display |
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

## Repository Interface

### MapPreviewRepository

Repository interface for fetching map data.

```kotlin
// Location: features/mapPreview/domain/repositories/MapPreviewRepository.kt
interface MapPreviewRepository {
    /**
     * Fetches pet announcements within the specified radius.
     * @param latitude User's latitude
     * @param longitude User's longitude
     * @param radiusKm Search radius in kilometers
     * @return List of map pins, or empty list on error
     */
    suspend fun getNearbyAnnouncements(
        latitude: Double,
        longitude: Double,
        radiusKm: Int
    ): Result<List<MapPin>>
}
```

### MapPreviewRepositoryImpl

Repository implementation using existing API client.

```kotlin
// Location: features/mapPreview/data/repositories/MapPreviewRepositoryImpl.kt
class MapPreviewRepositoryImpl(
    private val apiClient: AnnouncementApiClient
) : MapPreviewRepository {
    
    override suspend fun getNearbyAnnouncements(
        latitude: Double,
        longitude: Double,
        radiusKm: Int
    ): Result<List<MapPin>> = runCatching {
        val response = apiClient.getAnnouncements(
            lat = latitude,
            lng = longitude,
            range = radiusKm
        )
        response.announcements.mapNotNull { it.toMapPin() }
    }
}

private fun AnnouncementDto.toMapPin(): MapPin? {
    val lat = locationLatitude ?: return null
    val lng = locationLongitude ?: return null
    return MapPin(
        id = id,
        latitude = lat,
        longitude = lng,
        status = status  // Direct mapping - AnnouncementDto already uses AnimalStatus
    )
}
```

---

## Use Case

### GetNearbyAnnouncementsUseCase

Business logic for fetching nearby announcements.

```kotlin
// Location: features/mapPreview/domain/usecases/GetNearbyAnnouncementsUseCase.kt
class GetNearbyAnnouncementsUseCase(
    private val repository: MapPreviewRepository
) {
    companion object {
        const val DEFAULT_RADIUS_KM = 10
    }
    
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        radiusKm: Int = DEFAULT_RADIUS_KM
    ): Result<List<MapPin>> {
        return repository.getNearbyAnnouncements(latitude, longitude, radiusKm)
    }
}
```

---

## Koin Module

### MapPreviewModule

Dependency injection configuration. **Reuses existing `locationModule` dependencies.**

```kotlin
// Location: di/MapPreviewModule.kt
val mapPreviewModule = module {
    // Repository (NEW)
    single<MapPreviewRepository> { 
        MapPreviewRepositoryImpl(get()) // get() injects AnnouncementApiClient
    }
    
    // Use Case (NEW)
    factory { 
        GetNearbyAnnouncementsUseCase(get()) 
    }
    
    // ViewModel (NEW)
    // Injects existing GetCurrentLocationUseCase + CheckLocationPermissionUseCase from locationModule
    viewModel { 
        MapPreviewViewModel(
            getCurrentLocationUseCase = get(),      // FROM locationModule
            checkLocationPermissionUseCase = get(), // FROM locationModule
            getNearbyAnnouncementsUseCase = get()   // FROM mapPreviewModule
        )
    }
}
```

### Existing locationModule (REUSE)

```kotlin
// EXISTING: di/LocationModule.kt - no changes needed
val locationModule = module {
    single { androidContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    single<PermissionChecker> { AndroidPermissionChecker(androidContext()) }
    single<LocationRepository> { LocationRepositoryImpl(get()) }
    factory { GetCurrentLocationUseCase(get()) }
    factory { CheckLocationPermissionUseCase(get()) }
}
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
│   GetNearbyAnnouncementsUseCase   LocationProvider              │
│              │                                                  │
│              ▼                                                  │
│     MapPreviewRepository                                        │
│              │                                                  │
│              ▼                                                  │
│    AnnouncementApiClient (existing)                             │
│              │                                                  │
│              ▼                                                  │
│      Backend API: GET /api/v1/announcements?lat=&lng=&range=    │
└─────────────────────────────────────────────────────────────────┘
```

---

## Validation Rules

| Entity | Field | Rule |
|--------|-------|------|
| MapPin | latitude | Must be between -90 and 90 |
| MapPin | longitude | Must be between -180 and 180 |
| MapPin | id | Must not be empty |
| UserLocation | latitude/longitude | Same as MapPin |

---

## Test Data

### Sample MapPin Instances

```kotlin
// For unit tests and previews
val samplePins = listOf(
    MapPin(id = "1", latitude = 52.2297, longitude = 21.0122, status = AnimalStatus.MISSING),  // Warsaw, missing
    MapPin(id = "2", latitude = 52.2350, longitude = 21.0100, status = AnimalStatus.FOUND),    // Warsaw, found
    MapPin(id = "3", latitude = 52.2280, longitude = 21.0200, status = AnimalStatus.MISSING),  // Warsaw, missing
)

val sampleUserLocation = LatLng(52.2297, 21.0122) // Warsaw center
```

