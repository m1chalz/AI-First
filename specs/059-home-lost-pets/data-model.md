# Data Model: Home Lost Pets Teaser

## Overview

Data model for the lost pets teaser component on the Android home screen. This feature **reuses existing domain models** (`Animal`, `AnimalRepository`) and adds a new use case for client-side filtering.

## Domain Entities

### Animal (EXISTING - No Changes)

The teaser reuses the existing `Animal` model from `composeapp/domain/models/Animal.kt`:

```kotlin
// Location: composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/domain/models/Animal.kt
data class Animal(
    val id: String,
    val name: String,
    val photoUrl: String,
    val location: Location,
    val species: String,
    val breed: String,
    val gender: AnimalGender,
    val status: AnimalStatus,  // MISSING, FOUND, CLOSED
    val lastSeenDate: String,  // Format: DD/MM/YYYY
    val description: String,
    val email: String?,
    val phone: String?,
    val microchipNumber: String? = null,
    val rewardAmount: String? = null,
    val age: Int? = null,
)
```

**Teaser Filtering**: Only animals with `status == AnimalStatus.MISSING` are displayed in the teaser.

## MVI Classes

### LostPetsTeaserUiState

Data class following codebase pattern (not sealed class). Located in `features.lostPetsTeaser.presentation.mvi` package.

```kotlin
data class LostPetsTeaserUiState(
    val animals: List<Animal> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    /**
     * Computed property: true when data loaded but list is empty.
     * Distinguishes empty state from loading or error states.
     */
    val isEmpty: Boolean
        get() = animals.isEmpty() && !isLoading && error == null

    companion object {
        val Initial = LostPetsTeaserUiState()
    }
}
```

**State Transitions:**
- `Initial` → `isLoading = true` (on LoadData)
- `isLoading = true` → `animals = [...]` (on success)
- `isLoading = true` → `error = "..."` (on failure)
- `isLoading = true` → `isEmpty = true` (when no MISSING pets)
- Any state → `isLoading = true` (on RefreshData)

### LostPetsTeaserIntent

Sealed interface following codebase pattern. Located in `features.lostPetsTeaser.presentation.mvi` package.

```kotlin
sealed interface LostPetsTeaserIntent {
    /** Triggers initial data fetch on first composition. */
    data object LoadData : LostPetsTeaserIntent
    
    /** Triggers data refresh (for error recovery via retry button). */
    data object RefreshData : LostPetsTeaserIntent
    
    /** User tapped a pet card in the teaser. */
    data class PetClicked(val petId: String) : LostPetsTeaserIntent
    
    /** User tapped "View All Lost Pets" button. */
    data object ViewAllClicked : LostPetsTeaserIntent
}
```

### LostPetsTeaserEffect

Sealed class for one-off navigation events. Located in `features.lostPetsTeaser.presentation.mvi` package.

```kotlin
sealed class LostPetsTeaserEffect {
    /** Navigate to pet details within Lost Pet tab. */
    data class NavigateToPetDetails(val petId: String) : LostPetsTeaserEffect()
    
    /** Navigate to Lost Pet tab (full list). */
    data object NavigateToLostPetsList : LostPetsTeaserEffect()
}
```

**Effect Handling:**
- Navigation effects are handled by the parent screen (HomeScreen)
- Effects abstract the "what" (navigate to pet X) from the "how" (tab switch + push)
- Enables future deep link support without changing teaser implementation

### LostPetsTeaserReducer

Pure functions for state transitions. Located in `features.lostPetsTeaser.presentation.mvi` package.

```kotlin
object LostPetsTeaserReducer {
    fun loading(state: LostPetsTeaserUiState): LostPetsTeaserUiState =
        state.copy(isLoading = true, error = null)

    fun success(state: LostPetsTeaserUiState, animals: List<Animal>): LostPetsTeaserUiState =
        state.copy(isLoading = false, animals = animals, error = null)

    fun error(state: LostPetsTeaserUiState, message: String): LostPetsTeaserUiState =
        state.copy(isLoading = false, error = message)
}
```

## Repository (EXISTING - No Changes)

Reuses existing `AnimalRepository` from `composeapp/domain/repositories/AnimalRepository.kt`:

```kotlin
interface AnimalRepository {
    suspend fun getAnimals(
        lat: Double? = null,
        lng: Double? = null,
        range: Int? = null,
    ): List<Animal>

    suspend fun getAnimalById(id: String): Animal
}
```

**No repository changes needed** - client-side filtering is done in the use case.

## Use Case (NEW)

```kotlin
// Location: composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/domain/usecases/GetRecentAnimalsUseCase.kt

/**
 * Retrieves recent lost (MISSING status) animals for the home teaser.
 * Performs client-side filtering, sorting, and limiting.
 */
class GetRecentAnimalsUseCase(
    private val repository: AnimalRepository,
) {
    /**
     * Fetches animals and filters to show only MISSING status,
     * sorted by lastSeenDate (newest first), limited to specified count.
     *
     * @param limit Maximum number of animals to return (default: 5)
     * @return List of recent lost animals
     * @throws Exception if data fetch fails
     */
    suspend operator fun invoke(limit: Int = 5): List<Animal> {
        return repository.getAnimals()
            .filter { it.status == AnimalStatus.MISSING }
            .sortedByDescending { it.lastSeenDate }  // Assumes DD/MM/YYYY format
            .take(limit)
    }
}
```

**Responsibilities:**
- Calls existing `AnimalRepository.getAnimals()` 
- Filters to only MISSING status (client-side)
- Sorts by `lastSeenDate` descending (newest first)
- Limits to 5 items (configurable)
- Exceptions bubble up naturally (no Result wrapping)

**Note:** Date sorting by string comparison works for DD/MM/YYYY format within the same year. For production, consider parsing to a proper date type.

## Data Flow

```
┌─────────────────┐
│  LostPetsTeaser │  (UI - Composable)
│   Composable    │
└────────┬────────┘
         │ dispatchIntent()
         ▼
┌─────────────────┐
│ LostPetsTeaser  │  (Presentation - ViewModel)
│    ViewModel    │
└────────┬────────┘
         │ invoke()
         ▼
┌─────────────────┐
│ GetRecentAnimals│  (Domain - Use Case)
│    UseCase      │  ← Filters MISSING, sorts, limits 5
└────────┬────────┘
         │ getAnimals()
         ▼
┌─────────────────┐
│ AnimalRepository│  (Domain - Interface)
│   (existing)    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│AnimalRepository │  (Data - Implementation)
│     Impl        │  → Backend API
└─────────────────┘
```

**Async Pattern:** 
- Kotlin Coroutines with `viewModelScope`
- `StateFlow<UiState>` for reactive UI updates
- `SharedFlow<Effect>` for one-off navigation events

## Error Handling

| Scenario | Handler | Result |
|----------|---------|--------|
| Network errors | ViewModel catches exception | `error` field set, retry button shown |
| Empty results | Use case returns empty list | `isEmpty` computed property = true |
| Malformed data | Repository throws exception | ViewModel handles as error state |
| Navigation failures | Parent screen (HomeScreen) | Toast/Snackbar via standard patterns |

## Testing Considerations

### Unit Tests Required

| Component | Test Focus | Location |
|-----------|------------|----------|
| `LostPetsTeaserUiState` | Computed `isEmpty` property | N/A (trivial, covered by ViewModel tests) |
| `LostPetsTeaserReducer` | State transitions are pure | `mvi/LostPetsTeaserReducerTest.kt` |
| `GetRecentAnimalsUseCase` | Filtering, sorting, limiting logic | `usecases/GetRecentAnimalsUseCaseTest.kt` |
| `LostPetsTeaserViewModel` | Intent handling, state flow, effects | `viewmodels/LostPetsTeaserViewModelTest.kt` |

### Test Patterns

```kotlin
// Example: Use case test
@Test
fun `invoke should filter only MISSING status animals`() = runTest {
    // Given - Repository with mixed statuses
    val fakeRepository = FakeAnimalRepository(animals = listOf(
        animalWithStatus(AnimalStatus.MISSING),
        animalWithStatus(AnimalStatus.FOUND),
        animalWithStatus(AnimalStatus.MISSING),
    ))
    val useCase = GetRecentAnimalsUseCase(fakeRepository)

    // When
    val result = useCase()

    // Then
    assertEquals(2, result.size)
    assertTrue(result.all { it.status == AnimalStatus.MISSING })
}

// Example: ViewModel test with Turbine
@Test
fun `dispatchIntent LoadData should emit loading then success state`() = runTest {
    // Given
    val useCase = GetRecentAnimalsUseCase(FakeAnimalRepository(animalCount = 5))
    val viewModel = LostPetsTeaserViewModel(useCase)

    // When - observing state
    viewModel.state.test {
        val initialState = awaitItem()
        assertTrue(initialState == LostPetsTeaserUiState.Initial)

        viewModel.dispatchIntent(LostPetsTeaserIntent.LoadData)
        advanceUntilIdle()

        val loadingState = awaitItem()
        assertTrue(loadingState.isLoading)

        val successState = awaitItem()
        assertFalse(successState.isLoading)
        assertEquals(5, successState.animals.size)

        cancelAndIgnoreRemainingEvents()
    }
}
```
