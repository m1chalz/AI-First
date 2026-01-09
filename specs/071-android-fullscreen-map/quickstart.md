# Quickstart: Android Fullscreen Interactive Map

**Feature**: 071-android-fullscreen-map  
**Date**: 2026-01-08

## Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17

## Setup

### Already Configured ✅

The following are already in place from the map preview feature (067):

- **Google Maps dependencies** in `composeApp/build.gradle.kts`:
  - `libs.maps.compose` (6.12.2)
  - `libs.play.services.maps` (19.2.0)

- **Maps API key** configured in:
  - `local.properties`: `MAPS_API_KEY=...`
  - `AndroidManifest.xml`: `<meta-data android:name="com.google.android.geo.API_KEY" .../>`

**No additional setup needed** - just start implementing!

## Build & Run

```bash
# Build debug APK
./gradlew :composeApp:assembleDebug

# Run unit tests
./gradlew :composeApp:testDebugUnitTest

# Run with coverage report
./gradlew :composeApp:testDebugUnitTest koverHtmlReport
# View report: composeApp/build/reports/kover/html/index.html
```

## Feature Structure

```
composeApp/src/androidMain/.../
├── features/
│   └── fullscreenmap/
│       ├── ui/
│       │   ├── FullscreenMapScreen.kt      # Entry point, state host
│       │   └── FullscreenMapContent.kt     # Stateless UI, previews
│       ├── presentation/
│       │   ├── mvi/
│       │   │   ├── FullscreenMapUiState.kt
│       │   │   ├── FullscreenMapIntent.kt
│       │   │   ├── FullscreenMapEffect.kt
│       │   │   └── FullscreenMapReducer.kt
│       │   └── viewmodels/
│       │       └── FullscreenMapViewModel.kt
│       └── domain/
│           └── usecases/                   # Feature-specific use case (if needed)
└── di/
    ├── ViewModelModule.kt                  # Register ViewModel here
    └── DomainModule.kt                     # Register use case here
```

## Implementation Order

### Step 1: MVI Contracts (30 min)

Create the MVI types in `presentation/mvi/`. **Reuse existing `Animal` model** - no new domain entities needed.

```kotlin
// FullscreenMapUiState.kt
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.domain.models.LocationCoordinates
import com.intive.aifirst.petspot.domain.models.PermissionStatus

data class FullscreenMapUiState(
    val userLocation: LocationCoordinates? = null,
    val animals: List<Animal> = emptyList(),
    val isLoadingAnimals: Boolean = false,
    val error: String? = null,
    val selectedAnimal: Animal? = null,  // null = popup hidden
    val permissionStatus: PermissionStatus = PermissionStatus.NotRequested,
) {
    companion object { val Initial = FullscreenMapUiState() }
    
    /** Animals with valid coordinates for map display. */
    val animalsWithLocation: List<Animal>
        get() = animals.filter { it.location.latitude != null && it.location.longitude != null }
    
    val isPopupVisible: Boolean get() = selectedAnimal != null
    val hasError: Boolean get() = error != null
}

// FullscreenMapIntent.kt
sealed interface FullscreenMapIntent {
    data object Initialize : FullscreenMapIntent
    data class OnViewportChanged(val bounds: LatLngBounds) : FullscreenMapIntent
    data class OnAnimalTapped(val animalId: String) : FullscreenMapIntent
    data object OnPopupDismissed : FullscreenMapIntent
    data object OnRetryTapped : FullscreenMapIntent
    data object OnBackPressed : FullscreenMapIntent
}

// FullscreenMapEffect.kt
sealed interface FullscreenMapEffect {
    data object NavigateBack : FullscreenMapEffect
}
```

### Step 2: Reducer (1 hour)

Pure functions for state transitions:

```kotlin
// FullscreenMapReducer.kt
object FullscreenMapReducer {
    fun reduceAnimalsLoading(state: FullscreenMapUiState): FullscreenMapUiState =
        state.copy(isLoadingAnimals = true, error = null)

    fun reduceAnimalsSuccess(state: FullscreenMapUiState, animals: List<Animal>): FullscreenMapUiState =
        state.copy(isLoadingAnimals = false, animals = animals)

    fun reduceAnimalsError(state: FullscreenMapUiState, error: String): FullscreenMapUiState =
        state.copy(isLoadingAnimals = false, error = error)

    fun reduceAnimalSelected(state: FullscreenMapUiState, animal: Animal): FullscreenMapUiState =
        state.copy(selectedAnimal = animal)

    fun reducePopupDismissed(state: FullscreenMapUiState): FullscreenMapUiState =
        state.copy(selectedAnimal = null)
}
```

### Step 3: ViewModel (2 hours)

```kotlin
// FullscreenMapViewModel.kt
class FullscreenMapViewModel(
    private val getAnnouncementsByLocation: GetAnnouncementsByLocationUseCase,
    private val locationProvider: LocationProvider
) : ViewModel() {
    
    private val _state = MutableStateFlow(FullscreenMapUiState.Initial)
    val state: StateFlow<FullscreenMapUiState> = _state.asStateFlow()
    
    private val _effects = MutableSharedFlow<FullscreenMapEffect>()
    val effects: SharedFlow<FullscreenMapEffect> = _effects.asSharedFlow()
    
    fun dispatchIntent(intent: FullscreenMapIntent) {
        when (intent) {
            is FullscreenMapIntent.Initialize -> initialize()
            is FullscreenMapIntent.OnCameraIdle -> loadPins(intent.viewport)
            is FullscreenMapIntent.OnPinTapped -> selectPin(intent.petId)
            is FullscreenMapIntent.OnPopupDismissed -> dismissPopup()
            is FullscreenMapIntent.OnRetryTapped -> retryLoad()
            is FullscreenMapIntent.OnBackPressed -> navigateBack()
        }
    }
    
    // Implementation details...
}
```

### Step 4: Stateless Composable (2 hours)

```kotlin
// FullscreenMapContent.kt
@Composable
fun FullscreenMapContent(
    state: FullscreenMapUiState,
    cameraPositionState: CameraPositionState,
    onBackClick: () -> Unit = {},
    onAnimalClick: (String) -> Unit = {},
    onPopupDismiss: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Header bar with back arrow and title
        TopAppBar(
            title = {
                Text(
                    text = "Pet Locations",
                    modifier = Modifier.testTag("fullscreenMap.title")
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("fullscreenMap.backButton")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            },
            modifier = Modifier.testTag("fullscreenMap.header")
        )
        
        // Legend row - REUSE existing component from map preview
        MapPreviewLegend(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("fullscreenMap.legend")
        )
        // Import: com.intive.aifirst.petspot.features.mapPreview.ui.components.MapPreviewLegend
        
        // Map container
        Box(modifier = Modifier.weight(1f)) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("fullscreenMap.container"),
                cameraPositionState = cameraPositionState
            ) {
                // Reuse existing Animal model - filter for valid coordinates
                // Uses default Google Maps markers (custom icons as future enhancement)
                state.animalsWithLocation.forEach { animal ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(animal.location.latitude!!, animal.location.longitude!!)
                        ),
                        icon = when (animal.status) {
                            AnimalStatus.MISSING -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                            AnimalStatus.FOUND -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                        },
                        onClick = { onAnimalClick(animal.id); true }
                    )
                }
            }
            
            // Loading overlay
            if (state.isLoadingAnimals) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .testTag("fullscreenMap.loading")
                        .align(Alignment.Center)
                )
            }
            
            // Error state
            if (state.hasError) {
                ErrorCard(
                    message = state.error!!,
                    onRetry = onRetryClick,
                    modifier = Modifier.testTag("fullscreenMap.error")
                )
            }
        }
        
        // Animal details popup (reuses Animal model)
        if (state.selectedAnimal != null) {
            ModalBottomSheet(onDismissRequest = onPopupDismiss) {
                AnimalDetailsContent(
                    animal = state.selectedAnimal,
                    modifier = Modifier.testTag("fullscreenMap.petPopup")
                )
            }
        }
        
        // Note: Bottom navigation is handled by the parent scaffold/nav host
    }
}

// Preview
@Preview(showBackground = true)
@Composable
private fun FullscreenMapContentPreview(
    @PreviewParameter(FullscreenMapStateProvider::class) state: FullscreenMapUiState
) {
    MaterialTheme {
        FullscreenMapContent(
            state = state,
            cameraPositionState = rememberCameraPositionState()
        )
    }
}
```

### Step 5: State Host Composable (30 min)

```kotlin
// FullscreenMapScreen.kt
@Composable
fun FullscreenMapScreen(
    onNavigateBack: () -> Unit,
    viewModel: FullscreenMapViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(state.mapCenter, state.zoomLevel)
    }
    
    LaunchedEffect(Unit) {
        viewModel.dispatchIntent(FullscreenMapIntent.Initialize)
    }
    
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            viewModel.dispatchIntent(
                FullscreenMapIntent.OnCameraIdle(
                    MapViewport(
                        center = cameraPositionState.position.target,
                        zoomLevel = cameraPositionState.position.zoom,
                        bounds = cameraPositionState.projection?.visibleRegion?.latLngBounds
                            ?: return@LaunchedEffect
                    )
                )
            )
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                FullscreenMapEffect.NavigateBack -> onNavigateBack()
            }
        }
    }
    
    BackHandler { viewModel.dispatchIntent(FullscreenMapIntent.OnBackPressed) }
    
    FullscreenMapContent(
        state = state,
        cameraPositionState = cameraPositionState,
        onBackClick = { viewModel.dispatchIntent(FullscreenMapIntent.OnBackPressed) },
        onPinClick = { viewModel.dispatchIntent(FullscreenMapIntent.OnPinTapped(it)) },
        onPopupDismiss = { viewModel.dispatchIntent(FullscreenMapIntent.OnPopupDismissed) },
        onRetryClick = { viewModel.dispatchIntent(FullscreenMapIntent.OnRetryTapped) }
    )
}
```

### Step 6: Register in Koin Modules (15 min)

Add ViewModel to existing `ViewModelModule.kt`:

```kotlin
// In di/ViewModelModule.kt - add to existing module
val viewModelModule = module {
    // ... existing ViewModels ...
    
    // Fullscreen Map
    viewModel { FullscreenMapViewModel(get(), get()) }
}
```

Add use case to existing `DomainModule.kt` (if creating new use case):

```kotlin
// In di/DomainModule.kt - add to existing module
val domainModule = module {
    // ... existing use cases ...
    
    // Fullscreen Map (reuse GetNearbyAnimalsForMapUseCase or add new)
    factory { GetAnnouncementsByViewportUseCase(get()) }
}
```

**Note**: The project uses centralized DI modules, not per-feature modules.

### Step 7: Navigation (30 min)

Add to NavGraph:

```kotlin
// In navigation graph
composable("fullscreenMap") {
    FullscreenMapScreen(
        onNavigateBack = { navController.popBackStack() }
    )
}

// Navigate from landing page preview
fun navigateToFullscreenMap() {
    navController.navigate("fullscreenMap")
}
```

## Testing

### Unit Tests

```kotlin
// FullscreenMapViewModelTest.kt
class FullscreenMapViewModelTest {
    
    @Test
    fun `should load animals when viewport changes`() = runTest {
        // Given
        val fakeAnimals = listOf(
            Animal(
                id = "1", name = "Max", photoUrl = "", 
                location = Location(52.0, 21.0),
                species = "Dog", breed = "", gender = AnimalGender.MALE,
                status = AnimalStatus.MISSING, lastSeenDate = "01/01/2026",
                description = "", email = null, phone = null
            )
        )
        val fakeUseCase = FakeGetNearbyAnimalsForMapUseCase(fakeAnimals)
        val viewModel = FullscreenMapViewModel(fakeUseCase, FakeLocationProvider())
        
        // When
        viewModel.dispatchIntent(FullscreenMapIntent.OnViewportChanged(testBounds))
        
        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(fakeAnimals, state.animals)
            assertFalse(state.isLoadingAnimals)
        }
    }
}
```

### E2E Tests

```gherkin
# fullscreen-map.feature
@android
Feature: Fullscreen Interactive Map

  Scenario: User opens fullscreen map from preview
    Given user is on landing page with map preview visible
    When user taps the map preview
    Then fullscreen interactive map is displayed
    And back button is visible in top-left corner

  Scenario: User views pet details from pin
    Given fullscreen map is displayed with pet pins
    When user taps a red pin
    Then pet details popup appears
    And popup shows pet photo, name, species, and contact info
```

## Test Identifiers Reference

| Element | Test Tag |
|---------|----------|
| Header bar | `fullscreenMap.header` |
| Back button | `fullscreenMap.backButton` |
| Title | `fullscreenMap.title` |
| Legend | `fullscreenMap.legend` |
| Map container | `fullscreenMap.container` |
| Loading indicator | `fullscreenMap.loading` |
| Error state | `fullscreenMap.error` |
| Retry button | `fullscreenMap.retryButton` |
| Pin marker | `fullscreenMap.pin.${petId}` |
| Pet popup | `fullscreenMap.petPopup` |
| Popup close | `fullscreenMap.petPopup.close` |

## Common Issues

### Map not showing
- API key is already configured; check Google Cloud Console for API key restrictions
- Verify the emulator/device has Google Play Services installed

### Pins not loading
- Verify backend server is running (`npm run dev` in /server)
- Check network permissions in AndroidManifest.xml
- Verify API endpoint URL in repository

### Pop-up not dismissing
- Ensure `ModalBottomSheet` has `onDismissRequest` wired
- Check that `OnPopupDismissed` intent is dispatched
