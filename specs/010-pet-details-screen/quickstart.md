# Quick Start: Pet Details Screen Implementation

**Feature**: Pet Details Screen (Android UI)  
**Date**: 2025-11-25  
**Phase**: 1 - Design & Contracts

## Overview

This guide provides a quick start for implementing the Pet Details Screen feature. The screen displays comprehensive information about a selected pet, including photo, status, identification data, location, contact information, and additional details.

## Prerequisites

- Android Studio with Kotlin support
- Jetpack Compose knowledge
- Understanding of MVI architecture pattern
- Familiarity with Koin dependency injection

## Implementation Steps

### Step 1: Update Animal Model

Add missing fields to shared Animal model:

```kotlin
// shared/src/commonMain/kotlin/.../domain/models/Animal.kt
data class Animal(
    // ... existing fields ...
    val microchipNumber: String? = null,  // NEW
    val rewardAmount: String? = null,      // NEW
    val approximateAge: String? = null    // NEW
)
```

Update `MockAnimalData` with sample values for new fields.

### Step 2: Add Repository Method

Extend `AnimalRepository` interface:

```kotlin
// shared/src/commonMain/kotlin/.../domain/repositories/AnimalRepository.kt
interface AnimalRepository {
    suspend fun getAnimals(): List<Animal>
    suspend fun getAnimalById(id: String): Animal  // NEW
}
```

Implement in `AnimalRepositoryImpl`:

```kotlin
// composeApp/src/androidMain/kotlin/.../data/AnimalRepositoryImpl.kt
override suspend fun getAnimalById(id: String): Animal {
    delay(networkDelayMs)
    return MockAnimalData.generateMockAnimals().find { it.id == id }
        ?: throw NoSuchElementException("Animal not found: $id")
}
```

### Step 3: Create GetAnimalByIdUseCase

```kotlin
// composeApp/src/androidMain/kotlin/.../domain/usecases/GetAnimalByIdUseCase.kt
class GetAnimalByIdUseCase(
    private val repository: AnimalRepository
) {
    suspend operator fun invoke(id: String): Result<Animal> =
        runCatching { repository.getAnimalById(id) }
}
```

### Step 4: Create MVI Components

#### UiState

```kotlin
// composeApp/src/androidMain/kotlin/.../features/petdetails/presentation/mvi/PetDetailsUiState.kt
data class PetDetailsUiState(
    val pet: Animal? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    companion object {
        val Initial = PetDetailsUiState()
    }
}
```

#### Intent

```kotlin
// composeApp/src/androidMain/kotlin/.../features/petdetails/presentation/mvi/PetDetailsIntent.kt
sealed interface PetDetailsIntent {
    data class LoadPet(val id: String) : PetDetailsIntent
    data object NavigateBack : PetDetailsIntent
    data object ShowOnMap : PetDetailsIntent
    data object RetryLoad : PetDetailsIntent
}
```

#### Effect

```kotlin
// composeApp/src/androidMain/kotlin/.../features/petdetails/presentation/mvi/PetDetailsEffect.kt
sealed interface PetDetailsEffect {
    data object NavigateBack : PetDetailsEffect
    data class ShowMap(val location: Location) : PetDetailsEffect
}
```

#### Reducer

```kotlin
// composeApp/src/androidMain/kotlin/.../features/petdetails/presentation/mvi/PetDetailsReducer.kt
object PetDetailsReducer {
    fun loading(state: PetDetailsUiState): PetDetailsUiState =
        state.copy(isLoading = true, error = null)
    
    fun reduce(state: PetDetailsUiState, result: Result<Animal>): PetDetailsUiState =
        result.fold(
            onSuccess = { pet -> state.copy(pet = pet, isLoading = false, error = null) },
            onFailure = { error -> state.copy(isLoading = false, error = error.message, pet = null) }
        )
}
```

### Step 5: Create ViewModel

```kotlin
// composeApp/src/androidMain/kotlin/.../features/petdetails/presentation/viewmodels/PetDetailsViewModel.kt
class PetDetailsViewModel(
    private val getAnimalByIdUseCase: GetAnimalByIdUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(PetDetailsUiState.Initial)
    val state: StateFlow<PetDetailsUiState> = _state.asStateFlow()
    
    private val _effects = MutableSharedFlow<PetDetailsEffect>()
    val effects: SharedFlow<PetDetailsEffect> = _effects.asSharedFlow()
    
    fun dispatchIntent(intent: PetDetailsIntent) {
        when (intent) {
            is PetDetailsIntent.LoadPet -> loadPet(intent.id)
            is PetDetailsIntent.NavigateBack -> emitEffect(PetDetailsEffect.NavigateBack)
            is PetDetailsIntent.ShowOnMap -> showOnMap()
            is PetDetailsIntent.RetryLoad -> _state.value.pet?.id?.let { loadPet(it) }
        }
    }
    
    private fun loadPet(id: String) {
        viewModelScope.launch {
            _state.value = PetDetailsReducer.loading(_state.value)
            val result = getAnimalByIdUseCase(id)
            _state.value = PetDetailsReducer.reduce(_state.value, result)
        }
    }
    
    private fun showOnMap() {
        viewModelScope.launch {
            _state.value.pet?.location?.let { location ->
                _effects.emit(PetDetailsEffect.ShowMap(location))
            }
        }
    }
    
    private fun emitEffect(effect: PetDetailsEffect) {
        viewModelScope.launch {
            _effects.emit(effect)
        }
    }
}
```

### Step 6: Create UI Composables

Start with main screen:

```kotlin
// composeApp/src/androidMain/kotlin/.../features/petdetails/ui/PetDetailsScreen.kt
@Composable
fun PetDetailsScreen(
    animalId: String,
    navController: NavController,
    viewModel: PetDetailsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val effects = viewModel.effects
    
    // Handle effects
    LaunchedEffect(effects) {
        effects.collect { effect ->
            when (effect) {
                is PetDetailsEffect.NavigateBack -> navController.popBackStack()
                is PetDetailsEffect.ShowMap -> { /* Launch external map app via Intent */ }
            }
        }
    }
    
    // Load pet on screen creation
    LaunchedEffect(animalId) {
        viewModel.dispatchIntent(PetDetailsIntent.LoadPet(animalId))
    }
    
    // Render UI based on state
    when {
        state.isLoading -> FullScreenLoading()
        state.error != null -> ErrorState(
            error = state.error,
            onRetryClick = { viewModel.dispatchIntent(PetDetailsIntent.RetryLoad) }
        )
        state.pet != null -> PetDetailsContent(
            pet = state.pet,
            onBackClick = { viewModel.dispatchIntent(PetDetailsIntent.NavigateBack) },
            onShowMapClick = { viewModel.dispatchIntent(PetDetailsIntent.ShowOnMap) }
        )
    }
}
```

### Step 7: Update Navigation

Enable navigation route in `NavGraph.kt`:

```kotlin
composable<NavRoute.AnimalDetail> { backStackEntry ->
    val route = backStackEntry.toRoute<NavRoute.AnimalDetail>()
    PetDetailsScreen(
        animalId = route.animalId,
        navController = navController
    )
}
```

Update `NavControllerExt.kt` to enable navigation:

```kotlin
fun NavController.navigateToAnimalDetail(
    animalId: String,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(NavRoute.AnimalDetail(animalId), builder)
}
```

### Step 8: Update Dependency Injection

Add to `DomainModule.kt`:

```kotlin
single { GetAnimalByIdUseCase(get()) }
```

Add to `ViewModelModule.kt`:

```kotlin
viewModel { PetDetailsViewModel(get()) }
```

### Step 9: Add Coil Dependency

Add to `composeApp/build.gradle.kts`:

```kotlin
androidMain.dependencies {
    // ... existing dependencies ...
    implementation("io.coil-kt:coil-compose:2.5.0")
}
```

### Step 10: Create Utility Functions

Create date and microchip formatters in `lib/` directory.

## Testing Checklist

- [ ] Unit tests for `PetDetailsReducer` (all state transitions)
- [ ] Unit tests for `PetDetailsViewModel` (intent handling, state updates, effects)
- [ ] Unit tests for `GetAnimalByIdUseCase` (success and error cases)
- [ ] UI tests for `PetDetailsScreen` (loading, success, error states)
- [ ] E2E tests for all 6 user stories from spec

## Next Steps

1. Implement UI composables for each section (header, photo, info, location, contact, description)
2. Add test identifiers to all interactive elements
3. Implement date and microchip formatting utilities
4. Add image loading with Coil
5. Handle edge cases (missing data, image failures, etc.)
6. Write comprehensive unit tests
7. Write E2E tests for mobile platform

## References

- Feature Spec: `/specs/010-pet-details-screen/spec.md`
- Research: `/specs/010-pet-details-screen/research.md`
- Data Model: `/specs/010-pet-details-screen/data-model.md`
- Existing Pattern: `AnimalListScreen` implementation



