# Data Model: Home Lost Pets Teaser

## Overview
Data model for the lost pets teaser component on the Android home screen.

## Domain Entities

### LostPet
Represents a reported lost pet with essential information for the teaser display.

```kotlin
data class LostPet(
    val id: String,
    val title: String,
    val description: String,
    val createdAt: Instant,
    val location: String,
    val imageUrl: String?,
    val contactInfo: ContactInfo
) {
    data class ContactInfo(
        val phone: String?,
        val email: String?
    )
}
```

**Validation Rules:**
- `id`: Non-empty, unique identifier
- `title`: Non-empty, max 100 characters
- `description`: Non-empty, max 500 characters  
- `createdAt`: Valid timestamp, not in future
- `imageUrl`: Valid URL format when present
- `contactInfo`: At least one contact method required

**Relationships:**
- Used by: features.lostPetsTeaser.presentation.viewmodels.LostPetsTeaserViewModel, existing LostPetsRepository, existing LostPetListItem composable
- Owned by: Domain layer, passed to presentation layer
- **Reuses existing infrastructure** - no dedicated repository or data models created

### UiState
MVI state for the lost pets teaser component in `features.lostPetsTeaser.presentation.mvi` package.

```kotlin
sealed class LostPetsTeaserUiState {
    object Loading : LostPetsTeaserUiState()

    data class Success(
        val pets: List<LostPet>
    ) : LostPetsTeaserUiState()

    data class Error(
        val message: String
    ) : LostPetsTeaserUiState()

    object Empty : LostPetsTeaserUiState()
}
```

**State Transitions:**
- Loading → Success (when data loaded)
- Loading → Error (when request fails)
- Loading → Empty (when no pets available)
- Success/Error/Empty → Loading (on refresh)

### UserIntent
Sealed class for user interactions with the teaser in `features.lostPetsTeaser.presentation.mvi` package.

```kotlin
sealed class LostPetsTeaserIntent {
    object LoadData : LostPetsTeaserIntent()
    object RefreshData : LostPetsTeaserIntent()
    data class PetClicked(val petId: String) : LostPetsTeaserIntent()
    object ViewAllClicked : LostPetsTeaserIntent()
}
```

**Intent Processing:**
- LoadData: Triggers initial data fetch
- RefreshData: Triggers data refresh (for error recovery)
- PetClicked: Navigates to pet details
- ViewAllClicked: Navigates to full lost pets list

### UiEffect
Side effects triggered by the ViewModel in `features.lostPetsTeaser.presentation.mvi` package.

```kotlin
sealed class LostPetsTeaserEffect {
    data class NavigateToPetDetails(val petId: String) : LostPetsTeaserEffect()
    object NavigateToLostPetsList : LostPetsTeaserEffect()
    data class ShowError(val message: String) : LostPetsTeaserEffect()
}
```

**Effect Handling:**
- Navigation effects: Handled by UI layer to trigger Jetpack Navigation
- Error effects: Handled by UI to show snackbars/toasts

## Repository Interface

**Reuse existing LostPetsRepository** - extended with getRecentLostPets(limit: Int) method

```kotlin
interface LostPetsRepository {
    // Existing methods...
    suspend fun getRecentLostPets(limit: Int = 5): List<LostPet>
}
```

**Contract:**
- Suspend function following project patterns (no Flow)
- Throws exceptions on failure for natural error handling
- Default limit of 5 for teaser use case
- Maintains existing repository patterns
- **No dedicated repository** - teaser reuses existing data access layer

## Use Case

```kotlin
class GetRecentLostPetsUseCase(
    private val repository: LostPetsRepository
) {
    suspend operator fun invoke(limit: Int = 5): List<LostPet> {
        return repository.getRecentLostPets(limit)
    }
}
```

**Responsibilities:**
- Orchestrates data fetching following project patterns
- Applies business rules (if any)
- Transforms data if needed
- Provides clean suspend interface to features.lostPetsTeaser.presentation.viewmodels.LostPetsTeaserViewModel
- Exceptions bubble up naturally (no Flow/Result wrapping)

## Data Flow

```
UI (Composables) → ViewModel → Use Case → Repository → API
                      ↓
                UiState Flow
                      ↓
                UiEffect Flow
```

**Async Pattern:** Following project conventions with suspend functions and exception throwing.

## Error Handling

- **Network errors**: Caught by ViewModel, triggers error UiState with retry option
- **Empty results**: Repository returns empty list, ViewModel shows empty UiState
- **Malformed data**: Repository throws exception, ViewModel handles as error state
- **Navigation failures**: Handled by UI layer, shows error message via UiEffect

## Testing Considerations

- **LostPet**: Test validation rules, equality, serialization
- **LostPetsTeaserUiState**: Test state transitions, data consistency
- **LostPetsTeaserIntent/LostPetsTeaserEffect**: Test sealed class exhaustiveness
- **LostPetsRepository**: Test success scenarios, exception throwing for errors
- **GetRecentLostPetsUseCase**: Test business logic, exception propagation
- **LostPetsTeaserViewModel**: Test MVI state management, suspend function calls, exception handling
