# Research: Android Fullscreen Interactive Map

**Feature**: 071-android-fullscreen-map  
**Date**: 2026-01-08  
**Status**: Complete

## Research Topics

### 1. Google Maps SDK for Jetpack Compose

**Decision**: Use `maps-compose` library (official Google Maps Compose wrapper)

**Rationale**:
- Official Google library with active maintenance
- Native Compose integration via `GoogleMap` composable
- Built-in gesture support (zoom, pan, double-tap)
- Marker support with custom icons and click handling
- Camera position control for centering on user location

**Alternatives Considered**:
- MapLibre/Mapbox: More customization but requires separate API key and additional complexity
- OpenStreetMap: Free but less polished Compose integration
- Raw Maps SDK with AndroidView: Works but less idiomatic for Compose

**Implementation Notes**:
```kotlin
// Dependencies already configured in libs.versions.toml
// maps-compose = "6.12.2"
// play-services-maps = "19.2.0"
implementation(libs.maps.compose)
implementation(libs.play.services.maps)

// Basic usage
GoogleMap(
    modifier = Modifier.fillMaxSize(),
    cameraPositionState = cameraPositionState,
    onMapLoaded = { /* pins can be loaded */ }
) {
    pets.forEach { pet ->
        Marker(
            state = MarkerState(position = LatLng(pet.latitude, pet.longitude)),
            icon = if (pet.isMissing) redPinIcon else bluePinIcon,
            onClick = { onPinClick(pet); true }
        )
    }
}
```

### 2. MVI State Design for Map Feature

**Decision**: Single UiState reusing existing `Animal` model from map preview

**Rationale**:
- Follows existing MVI patterns in codebase (matches `MapPreviewUiState`)
- Reuses `Animal` domain model - no new entities needed
- Single source of truth for all UI elements
- Pop-up visibility as state property (not separate state)
- Easy to test with Turbine

**State Structure**:
```kotlin
// Reuses existing Animal model from composeapp/domain/models/Animal.kt
data class FullscreenMapUiState(
    val userLocation: LocationCoordinates? = null,
    val animals: List<Animal> = emptyList(),
    val isLoadingAnimals: Boolean = false,
    val error: String? = null,
    val selectedAnimal: Animal? = null,  // null = popup hidden
    val permissionStatus: PermissionStatus = PermissionStatus.NotRequested,
) {
    companion object { val Initial = FullscreenMapUiState() }
    
    val animalsWithLocation: List<Animal>
        get() = animals.filter { it.location.latitude != null && it.location.longitude != null }
}
```

### 3. Pin Data Loading Strategy

**Decision**: Load pins after camera idle (debounced), not during gesture

**Rationale**:
- Matches spec requirement: "pins update after gesture completes"
- Reduces API calls during rapid pan/zoom
- CameraPositionState provides `isMoving` property for detection

**Implementation Notes**:
```kotlin
// In ViewModel
private fun observeCameraPosition(cameraState: CameraPositionState) {
    viewModelScope.launch {
        snapshotFlow { cameraState.isMoving }
            .distinctUntilChanged()
            .filter { !it } // Camera stopped moving
            .debounce(300) // Wait for gesture to complete
            .collect { loadPinsForViewport(cameraState.position.target, cameraState.position.zoom) }
    }
}
```

### 4. Bottom Sheet Pop-up Pattern

**Decision**: Use Material3 `ModalBottomSheet` for pet details

**Rationale**:
- Native Material Design component
- Built-in swipe-to-dismiss
- Handles outside tap dismissal
- Matches design spec: "bottom sheet style, rounded corners"

**Implementation Notes**:
```kotlin
if (state.selectedPet != null) {
    ModalBottomSheet(
        onDismissRequest = { onDismissPopup() },
        sheetState = rememberModalBottomSheetState()
    ) {
        PetDetailsContent(pet = state.selectedPet)
    }
}
```

### 5. Navigation Integration

**Decision**: Add fullscreen map as NavGraph destination, navigate via Effect

**Rationale**:
- Follows constitution requirement for Jetpack Navigation Component
- Back button handling automatic via NavController
- ViewModel emits NavigateBack effect, Screen handles navigation

**Implementation Notes**:
```kotlin
// In NavGraph
composable("fullscreenMap") {
    FullscreenMapScreen(
        onNavigateBack = { navController.popBackStack() }
    )
}

// In FullscreenMapScreen
LaunchedEffect(Unit) {
    viewModel.effects.collect { effect ->
        when (effect) {
            FullscreenMapEffect.NavigateBack -> onNavigateBack()
        }
    }
}
```

### 6. Custom Pin Icons

**Decision**: Use BitmapDescriptor from vector drawables

**Rationale**:
- Consistent with Material Design
- Vector scales cleanly at different densities
- Can use existing color constants (#FB2C36 red, #2B7FFF blue)

**Implementation Notes**:
```kotlin
// Create pin icons (cache these)
val redPinIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_missing)
val bluePinIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_found)

// Or create programmatically with vector
fun createPinIcon(context: Context, @DrawableRes drawableRes: Int, tintColor: Int): BitmapDescriptor {
    val drawable = ContextCompat.getDrawable(context, drawableRes)?.mutate()
    drawable?.setTint(tintColor)
    // ... convert to bitmap
}
```

### 7. Existing API Integration

**Decision**: Use existing announcements API with bounding box filter

**Rationale**:
- No backend changes needed
- API already supports location-based filtering
- Reuse existing `AnnouncementsRepository`

**API Parameters**:
```
GET /api/announcements?
  minLat={bottom}&maxLat={top}&
  minLng={left}&maxLng={right}&
  status=missing,found
```

### 8. Error Handling Pattern

**Decision**: Error state replaces pin content, retry reloads current viewport

**Rationale**:
- Matches spec: "error state with Retry button"
- Map background visible during error
- Simple recovery flow

**Implementation Notes**:
```kotlin
// In reducer - uses existing Animal model
fun reduce(state: FullscreenMapUiState, result: LoadAnimalsResult): FullscreenMapUiState {
    return when (result) {
        is LoadAnimalsResult.Loading -> state.copy(isLoadingAnimals = true, error = null)
        is LoadAnimalsResult.Success -> state.copy(isLoadingAnimals = false, animals = result.animals)
        is LoadAnimalsResult.Error -> state.copy(isLoadingAnimals = false, error = result.message)
    }
}
```

## Dependencies Summary

| Dependency | Version | Purpose |
|------------|---------|---------|
| maps-compose | 6.12.2 | Google Maps Compose wrapper |
| play-services-maps | 19.2.0 | Google Maps SDK |
| compose (material3) | 1.9.1 | ModalBottomSheet |
| navigation-compose | 2.9.0 | Jetpack Navigation |
| accompanist-permissions | 0.37.3 | Declarative permission handling |

## Open Questions Resolved

All technical questions have been resolved. No NEEDS CLARIFICATION items remain.
