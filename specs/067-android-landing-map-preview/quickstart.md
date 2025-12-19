# Quickstart: Android Landing Page Map Preview

**Date**: 2025-12-19  
**Feature**: 067-android-landing-map-preview  
**Branch**: `067-android-landing-map-preview`

## Prerequisites

Before starting implementation:

1. ✅ Android Studio with Kotlin plugin
2. ✅ Google Maps API key (configured in `local.properties`)
3. ✅ Existing `AnnouncementApiClient` working
4. ✅ HomeScreen with LazyColumn structure

## Quick Setup

### 1. Add Dependencies

Add to `composeApp/build.gradle.kts`:

```kotlin
dependencies {
    // Google Maps Compose (NEW)
    implementation("com.google.maps.android:maps-compose:4.4.1")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    
    // Accompanist Permissions (NEW) - for UI permission request
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
    
    // Location Services - NOT NEEDED (reusing existing LocationManager-based code)
    // The app already has: GetCurrentLocationUseCase, CheckLocationPermissionUseCase, locationModule
}
```

### 2. Configure API Key

Add to `AndroidManifest.xml`:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="${GOOGLE_MAPS_API_KEY}" />
```

Add to `local.properties`:

```properties
GOOGLE_MAPS_API_KEY=your_api_key_here
```

### 3. Add Permission

Add to `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

### 4. Create Feature Module Structure

```bash
mkdir -p composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/features/mapPreview/{domain/{models,repositories,usecases},data/repositories,presentation/{mvi,viewmodels},ui}
mkdir -p composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/features/mapPreview/{domain/usecases,presentation/{mvi,viewmodels}}
```

## Implementation Order

Follow this order for incremental development:

### Phase 0: Verify Existing Infrastructure

**Existing code to reuse** (no changes needed):
- `GetCurrentLocationUseCase` - location fetching
- `CheckLocationPermissionUseCase` - permission checking
- `LocationCoordinates` - domain model
- `PermissionStatus` - permission states
- `locationModule` - Koin dependencies

### Phase 1: Domain Layer

1. **MapPin.kt** - NEW domain model for map pins
2. **MapPreviewRepository.kt** - NEW repository interface
3. **GetNearbyAnnouncementsUseCase.kt** - NEW business logic
4. **Unit tests** for use case

### Phase 2: Data Layer

1. **MapPreviewRepositoryImpl.kt** - NEW repository implementation
2. **LocationCoordinates.toLatLng()** - Extension function for Google Maps
3. **Unit tests** for repository

### Phase 3: Presentation Layer (MVI)

1. **MapPreviewUiState.kt** - UI state data class
2. **MapPreviewIntent.kt** - User intent sealed class
3. **MapPreviewEffect.kt** - Effects (if needed)
4. **MapPreviewReducer.kt** - State reducer (pure function)
5. **MapPreviewViewModel.kt** - MVI ViewModel
6. **Unit tests** for ViewModel and reducer

### Phase 4: UI Layer

1. **MapPreviewContent.kt** - Stateless composable (pure UI)
2. **MapPreviewLegend.kt** - Legend component
3. **MapPreviewOverlay.kt** - "Tap to view" overlay
4. **PermissionRequestContent.kt** - Permission UI
5. **MapPreviewSection.kt** - Stateful composable (state host)
6. **Add previews** with PreviewParameterProvider

### Phase 5: Integration

1. **MapPreviewModule.kt** - Koin DI module
2. **Register in PetSpotApp.kt** - Add module to Koin
3. **Integrate in HomeScreen.kt** - Add MapPreviewSection to LazyColumn
4. **E2E smoke test**

## Key Patterns

### MVI State Management

```kotlin
// ViewModel exposes
val state: StateFlow<MapPreviewUiState>
val effects: SharedFlow<MapPreviewEffect>
fun dispatchIntent(intent: MapPreviewIntent)

// UI collects state
val state by viewModel.state.collectAsStateWithLifecycle()
```

### Two-Layer Composable Pattern

```kotlin
// State Host (with ViewModel)
@Composable
fun MapPreviewSection(viewModel: MapPreviewViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    MapPreviewContent(
        state = state,
        onRequestPermission = { viewModel.dispatchIntent(MapPreviewIntent.RequestPermission) }
    )
}

// Stateless (pure, previewable)
@Composable
fun MapPreviewContent(
    state: MapPreviewUiState,
    onRequestPermission: () -> Unit = {}
) {
    // All UI logic here
}
```

### Test Identifiers

```kotlin
Modifier.testTag("mapPreview.container")
Modifier.testTag("mapPreview.header")
Modifier.testTag("mapPreview.legend")
Modifier.testTag("mapPreview.map")
Modifier.testTag("mapPreview.overlay")
Modifier.testTag("mapPreview.permissionButton")
Modifier.testTag("mapPreview.retryButton")
Modifier.testTag("mapPreview.loading")
```

## Testing

### Run Unit Tests

```bash
./gradlew :composeApp:testDebugUnitTest
```

### Run with Coverage

```bash
./gradlew :composeApp:testDebugUnitTest koverHtmlReport
# View: composeApp/build/reports/kover/html/index.html
```

### Key Test Cases

| Component | Test Case |
|-----------|-----------|
| UseCase | Returns pins when API succeeds |
| UseCase | Returns empty list when API returns empty |
| UseCase | Returns failure when API throws |
| Reducer | Sets loading state on LoadMap intent |
| Reducer | Sets pins and clears loading on success |
| Reducer | Sets error state on failure |
| ViewModel | Emits loading then success states |
| ViewModel | Handles permission granted flow |
| ViewModel | Handles permission denied flow |

## Common Issues

### Issue: Map not showing

**Solution**: Check Google Maps API key is correctly configured in `local.properties` and `AndroidManifest.xml`.

### Issue: Location permission always denied

**Solution**: Ensure `ACCESS_COARSE_LOCATION` is declared in manifest and the Accompanist permission state is correctly observed.

### Issue: Pins not appearing

**Solution**: Verify announcements have valid `locationLatitude` and `locationLongitude` values. Check API response in logs.

### Issue: Preview crashes

**Solution**: Ensure stateless composable has no ViewModel dependency and uses default no-op callbacks.

## Checklist

Before PR:

- [ ] All unit tests pass (`./gradlew :composeApp:testDebugUnitTest`)
- [ ] 80%+ test coverage for new code
- [ ] All composables have test tags
- [ ] Stateless composable has @Preview
- [ ] Koin module registered in PetSpotApp
- [ ] MapPreviewSection added to HomeScreen LazyColumn
- [ ] Manual testing on device/emulator
- [ ] No lint errors

