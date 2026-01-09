# Data Model: Android Fullscreen Interactive Map

**Feature**: 071-android-fullscreen-map  
**Date**: 2026-01-08

## Reused Domain Entities

This feature reuses existing domain models from the map preview feature. **No new domain entities needed.**

### Animal (existing)

Located at: `composeapp/domain/models/Animal.kt`

```kotlin
data class Animal(
    val id: String,
    val name: String,
    val photoUrl: String,
    val location: Location,
    val species: String,
    val breed: String,
    val gender: AnimalGender,
    val status: AnimalStatus,      // MISSING (red) or FOUND (blue)
    val lastSeenDate: String,
    val description: String,
    val email: String?,
    val phone: String?,
    val microchipNumber: String?,
    val rewardAmount: String?,
    val age: Int?,
)
```

**Used for**: Both pin display and pop-up details

### AnimalStatus (existing)

Located at: `composeapp/domain/models/AnimalStatus.kt`

```kotlin
enum class AnimalStatus(val displayName: String, val badgeColor: String) {
    MISSING("MISSING", "#FF0000"),  // Red pin
    FOUND("FOUND", "#0074FF"),      // Blue pin
}
```

### Location (existing)

Located at: `composeapp/domain/models/Location.kt`

```kotlin
data class Location(
    val latitude: Double? = null,
    val longitude: Double? = null,
)
```

### LocationCoordinates (existing)

Located at: `domain/models/LocationCoordinates.kt`

```kotlin
data class LocationCoordinates(
    val latitude: Double,
    val longitude: Double,
)
```

**Used for**: User location, map center

## MVI State Model

### FullscreenMapUiState

```kotlin
data class FullscreenMapUiState(
    val userLocation: LocationCoordinates? = null,
    val animals: List<Animal> = emptyList(),
    val isLoadingAnimals: Boolean = false,
    val error: String? = null,
    val selectedAnimal: Animal? = null,  // null = popup hidden
    val permissionStatus: PermissionStatus = PermissionStatus.NotRequested,
) {
    companion object {
        val Initial = FullscreenMapUiState()
    }
    
    /** Animals with valid coordinates for map display. */
    val animalsWithLocation: List<Animal>
        get() = animals.filter {
            it.location.latitude != null && it.location.longitude != null
        }
    
    val isPopupVisible: Boolean get() = selectedAnimal != null
    val hasError: Boolean get() = error != null
    val isPermissionGranted: Boolean get() = permissionStatus is PermissionStatus.Granted
}
```

**Note**: Pattern matches `MapPreviewUiState` from the existing map preview feature.

**State Transitions**:

```
Initial
    ├─[LocationGranted]──► Initial.copy(permissionStatus = Granted, userLocation = location)
    └─[LocationDenied]───► Initial.copy(permissionStatus = Denied)

Loading
    ├─[AnimalsLoaded]────► Success.copy(animals = loaded, isLoadingAnimals = false)
    └─[LoadFailed]───────► Error.copy(error = message, isLoadingAnimals = false)

Success
    ├─[AnimalTapped]─────► Success.copy(selectedAnimal = animal)
    ├─[ViewportChanged]──► Loading (trigger new fetch)
    └─[PopupDismissed]───► Success.copy(selectedAnimal = null)

Error
    └─[RetryTapped]──────► Loading
```

### FullscreenMapIntent

```kotlin
sealed interface FullscreenMapIntent {
    data object Initialize : FullscreenMapIntent
    data class OnViewportChanged(val bounds: LatLngBounds) : FullscreenMapIntent
    data class OnAnimalTapped(val animalId: String) : FullscreenMapIntent
    data object OnPopupDismissed : FullscreenMapIntent
    data object OnRetryTapped : FullscreenMapIntent
    data object OnBackPressed : FullscreenMapIntent
}
```

### FullscreenMapEffect

```kotlin
sealed interface FullscreenMapEffect {
    data object NavigateBack : FullscreenMapEffect
}
```

## Use Case Reuse

### GetNearbyAnimalsForMapUseCase (existing)

Located at: `features/mapPreview/domain/usecases/GetNearbyAnimalsForMapUseCase.kt`

This use case already fetches animals for a location with radius. Can be reused or extended to support viewport bounds.

```kotlin
class GetNearbyAnimalsForMapUseCase(
    private val repository: AnnouncementsRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        radiusKm: Double
    ): Result<List<Animal>>
}
```

**Option A**: Reuse as-is (calculate center + radius from viewport)  
**Option B**: Create new `GetAnimalsByViewportUseCase` with bounding box

## Data Flow

```
User Gesture (pan/zoom)
    │
    ▼
CameraPositionState.isMoving = false
    │
    ▼
ViewModel.dispatchIntent(OnViewportChanged(bounds))
    │
    ▼
GetNearbyAnimalsForMapUseCase(center.lat, center.lng, radiusKm)
    │
    ▼
AnnouncementsRepository.getByLocation(...)
    │
    ▼
API: GET /api/announcements?lat=...&lng=...&radius=...
    │
    ▼
List<Animal> (already mapped by repository)
    │
    ▼
Reducer: state.copy(animals = newAnimals, isLoadingAnimals = false)
    │
    ▼
UI: GoogleMap recomposes with Markers for animalsWithLocation
```

## UI Components

### Header Bar

```
┌──────────────────────────────────────┐
│ ←  Pet Locations                     │
└──────────────────────────────────────┘
```

- Back arrow (left) - navigates to landing page
- Title "Pet Locations" (center-aligned or start-aligned)

### Legend (Reuse Existing Component)

```
● Missing  ● Found
```

**Existing component**: `MapPreviewLegend`  
**Location**: `features/mapPreview/ui/components/MapPreviewLegend.kt`

- Already implemented for map preview on landing page
- Displays red dot for "Missing", blue dot for "Found"
- Uses `MapPreviewColors.MissingPin` and `MapPreviewColors.FoundPin`
- Horizontal layout with 16dp spacing between items

```kotlin
// Reuse in fullscreen map:
import com.intive.aifirst.petspot.features.mapPreview.ui.components.MapPreviewLegend

MapPreviewLegend(
    modifier = Modifier.testTag("fullscreenMap.legend")
)
```

### Bottom Navigation

The bottom navigation bar remains visible in fullscreen map mode. This is handled by the parent scaffold/nav host, not by the fullscreen map screen itself.

## Pin Color Mapping

```kotlin
// In Composable - using default Google Maps markers (custom icons as future enhancement)
val pinColor = when (animal.status) {
    AnimalStatus.MISSING -> BitmapDescriptorFactory.HUE_RED    // Red pin
    AnimalStatus.FOUND -> BitmapDescriptorFactory.HUE_BLUE     // Blue pin
}

// Future enhancement: Custom markers with icons
// val pinIcon = when (animal.status) {
//     AnimalStatus.MISSING -> customRedPinIcon   // #FB2C36 with ! icon
//     AnimalStatus.FOUND -> customBluePinIcon    // #2B7FFF with ✓ icon
// }
```

**Note**: Initial implementation uses default Google Maps markers with color differentiation. Custom branded markers (matching landing page preview style) will be added as a future enhancement.

## Validation Rules

1. **Coordinates**: Animals with null latitude/longitude are filtered via `animalsWithLocation`
2. **Photo URL**: If null or fails to load, show placeholder image (handled by Coil)
3. **Contact Info**: Display "Contact unavailable" if both email and phone are null
