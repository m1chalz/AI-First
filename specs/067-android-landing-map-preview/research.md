# Research: Android Landing Page Map Preview

**Date**: 2025-12-19  
**Purpose**: Resolve technical unknowns and document best practices for implementation

## Research Summary

| Topic | Decision | Confidence |
|-------|----------|------------|
| Map SDK | Google Maps Compose (`com.google.maps.android:maps-compose`) | High |
| Permission Handling | Accompanist Permissions (UI) + existing `CheckLocationPermissionUseCase` (logic) | High |
| Location Services | **REUSE EXISTING** `GetCurrentLocationUseCase` with native `LocationManager` | High |
| API Integration | Reuse existing `AnnouncementApiClient` | High |

---

## 1. Google Maps Compose Integration

### Decision
Use `com.google.maps.android:maps-compose` library for map rendering in Jetpack Compose.

### Rationale
- Official Google library with Compose-first API
- Declarative marker placement with `Marker` composable
- Camera state management with `rememberCameraPositionState`
- Gestures can be disabled for static preview via `MapProperties`

### Implementation Pattern

```kotlin
@Composable
fun MapPreviewContent(
    userLocation: LatLng,
    pins: List<MapPin>,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, calculateZoomFor10Km())
    }
    
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = false,  // Don't show blue dot (we have custom pins)
            mapType = MapType.NORMAL
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            zoomGesturesEnabled = false,
            scrollGesturesEnabled = false,
            tiltGesturesEnabled = false,
            rotationGesturesEnabled = false,
            compassEnabled = false,
            mapToolbarEnabled = false
        )
    ) {
        pins.forEach { pin ->
            Marker(
                state = remember { MarkerState(position = LatLng(pin.latitude, pin.longitude)) },
                icon = if (pin.isMissing) redMarkerIcon else blueMarkerIcon
            )
        }
    }
}
```

### Alternatives Considered

| Option | Pros | Cons | Why Rejected |
|--------|------|------|--------------|
| Static Maps API (URL) | Simple, no SDK | No overlay support, URL complexity | Can't overlay "Tap to view" pill easily |
| Mapbox | Open source | Additional dependency, learning curve | Google Maps already standard in Android |
| OpenStreetMap | Free, no API key | Less polished, manual tile loading | User experience not as good |

### Dependencies Required

```kotlin
// build.gradle.kts (module level)
implementation("com.google.maps.android:maps-compose:6.12.2")
implementation("com.google.android.gms:play-services-maps:19.2.0")
```

### Zoom Calculation for 10km Radius

```kotlin
// Approximate zoom level for 10km visible radius
// At zoom level 12, approximately 10km is visible
private fun calculateZoomFor10Km(): Float = 12f
```

---

## 2. Location Permission Handling

### Decision
**REUSE EXISTING** Accompanist Permissions setup from `AnimalListScreen`.

### Rationale
- Already in project: `accompanist-permissions:0.37.3`
- Already used in `AnimalListScreen.kt` with `rememberMultiplePermissionsState`
- Compose-native API using `rememberPermissionState`
- Handles permission rationale automatically
- Integrates with composable lifecycle

### Implementation Pattern

```kotlin
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapPreviewSection(
    viewModel: MapPreviewViewModel = koinViewModel()
) {
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        onPermissionResult = { isGranted ->
            if (isGranted) {
                viewModel.dispatchIntent(MapPreviewIntent.PermissionGranted)
            } else {
                viewModel.dispatchIntent(MapPreviewIntent.PermissionDenied)
            }
        }
    )
    
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            viewModel.dispatchIntent(MapPreviewIntent.LoadMap)
        }
    }
    
    MapPreviewContent(
        state = state,
        onRequestPermission = { locationPermissionState.launchPermissionRequest() },
        onRetry = { viewModel.dispatchIntent(MapPreviewIntent.Retry) }
    )
}
```

### Manifest Declaration

```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

### Dependencies Required

**NONE** - already in project:

```kotlin
// Already in composeApp/build.gradle.kts
implementation(libs.accompanist.permissions)  // v0.37.3
```

---

## 3. Location Services

### Decision
**REUSE EXISTING** `GetCurrentLocationUseCase` with native `LocationManager` (no Play Services dependency).

### Rationale
- Already implemented and tested in the codebase
- Uses native Android `LocationManager` (no Google Play Services dependency)
- Two-stage approach: cached location first, then fresh request with timeout
- Already integrated with Koin via `locationModule`

### Existing Implementation

```kotlin
// Already exists: domain/usecases/GetCurrentLocationUseCase.kt
class GetCurrentLocationUseCase(
    private val locationRepository: LocationRepository,
    private val defaultTimeoutMs: Long = DEFAULT_TIMEOUT_MS,
) {
    suspend operator fun invoke(): Result<LocationCoordinates?> = runCatching {
        // Stage 1: Try cached location first (instant)
        val cachedLocation = locationRepository.getLastKnownLocation()
        if (cachedLocation != null) {
            return@runCatching cachedLocation
        }
        // Stage 2: Request fresh location with timeout
        locationRepository.requestFreshLocation(defaultTimeoutMs)
    }
}
```

### Usage in MapPreviewViewModel

```kotlin
// In MapPreviewViewModel
private fun loadLocation() = viewModelScope.launch {
    val locationResult = getCurrentLocationUseCase()
    locationResult.fold(
        onSuccess = { coords ->
            if (coords != null) {
                loadPins(coords.latitude, coords.longitude)
            } else {
                _state.update { it.copy(error = MapPreviewError.LocationNotAvailable) }
            }
        },
        onFailure = { error ->
            _state.update { it.copy(error = MapPreviewError.LocationNotAvailable) }
        }
    )
}
```

### Conversion to Google Maps LatLng

```kotlin
// Extension function for Google Maps integration
fun LocationCoordinates.toLatLng(): LatLng = LatLng(latitude, longitude)
```

### Dependencies Required

**NONE** - existing `locationModule` already provides all dependencies.

---

## 4. API Integration

### Decision
Reuse existing `AnnouncementApiClient.getAnnouncements(lat, lng, range)` endpoint.

### Rationale
- API already supports location-based filtering
- No backend changes required
- Consistent with existing data flow patterns

### Existing API Signature

```kotlin
// Already implemented in AnnouncementApiClient.kt
suspend fun getAnnouncements(
    lat: Double? = null,
    lng: Double? = null,
    range: Int? = null,  // in kilometers
): AnnouncementsResponseDto
```

### Usage for Map Preview

```kotlin
// Fetch announcements within 10km radius
val announcements = apiClient.getAnnouncements(
    lat = userLocation.latitude,
    lng = userLocation.longitude,
    range = 10
)
```

### DTO Mapping to Domain Model

```kotlin
// AnnouncementDto already has:
// - locationLatitude: Double?
// - locationLongitude: Double?
// - status: AnimalStatus (LOST/FOUND)

fun AnnouncementDto.toMapPin(): MapPin? {
    val lat = locationLatitude ?: return null
    val lng = locationLongitude ?: return null
    
    return MapPin(
        id = id,
        latitude = lat,
        longitude = lng,
        isMissing = status == AnimalStatus.LOST
    )
}
```

---

## 5. Custom Marker Icons

### Decision
Use `BitmapDescriptorFactory.defaultMarker()` with custom hue for simplicity, or create vector drawables for custom pins.

### Option A: Default Markers with Hue (Simple)

```kotlin
val redMarkerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
val blueMarkerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
```

### Option B: Custom Vector Drawables (Advanced)

```kotlin
// If custom pin assets are provided
fun bitmapDescriptorFromVector(context: Context, @DrawableRes vectorResId: Int): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)!!
    vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
```

### Recommendation
Start with Option A (default markers with hue) for MVP. Upgrade to Option B if design requires custom pin icons.

---

## 6. Error Handling Strategy

### Decision
Map errors to sealed class for exhaustive handling in UI.

### Implementation

```kotlin
sealed interface MapPreviewError {
    data object LocationNotAvailable : MapPreviewError
    data object NetworkError : MapPreviewError
    data object MapLoadFailed : MapPreviewError
}

// In reducer
fun handleError(error: Throwable): MapPreviewUiState = when (error) {
    is LocationNotFoundException -> state.copy(error = MapPreviewError.LocationNotAvailable)
    is IOException -> state.copy(error = MapPreviewError.NetworkError)
    else -> state.copy(error = MapPreviewError.MapLoadFailed)
}
```

---

## Dependencies Summary

```kotlin
// build.gradle.kts (:composeApp)
dependencies {
    // Google Maps Compose (NEW - ONLY NEW DEPENDENCY)
    implementation("com.google.maps.android:maps-compose:6.12.2")
    implementation("com.google.android.gms:play-services-maps:19.2.0")
    
    // ALREADY IN PROJECT - no changes needed:
    // - libs.accompanist.permissions (v0.37.3) - used in AnimalListScreen
    // - Koin, Ktor, Compose, locationModule, etc.
}
```

### Reused Existing Infrastructure

| Component | Location | Purpose |
|-----------|----------|---------|
| `GetCurrentLocationUseCase` | `domain/usecases/` | Two-stage location fetch |
| `CheckLocationPermissionUseCase` | `domain/usecases/` | Permission status check |
| `LocationRepository` | `domain/repositories/` | Location abstraction |
| `LocationRepositoryImpl` | `data/repositories/` | Native LocationManager impl |
| `LocationCoordinates` | `domain/models/` | Domain model with validation |
| `PermissionStatus` | `domain/models/` | Rich permission states |
| `locationModule` | `di/` | Koin DI configuration |

---

## API Key Configuration

Google Maps SDK requires an API key. This should be configured in:

```xml
<!-- AndroidManifest.xml -->
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="${GOOGLE_MAPS_API_KEY}" />
```

```properties
# local.properties (not committed to git)
GOOGLE_MAPS_API_KEY=your_api_key_here
```

**Note**: Check if API key is already configured in the project. If not, this will need to be obtained from Google Cloud Console.

