# Quickstart: Home Lost Pets Teaser

## Overview
This guide provides a quick reference for implementing the lost pets teaser component on the Android home screen.

## Implementation Checklist

### 1. Domain Layer
- [ ] Extend existing `LostPetsRepository` interface with `getRecentLostPets(limit: Int)` method
- [ ] Implement the new method in existing `LostPetsRepositoryImpl` using Ktor HTTP client
- [ ] Create `GetRecentLostPetsUseCase` as suspend function following existing use case patterns

### 2. Presentation Layer (MVI) - Lost Pets Teaser Feature
- [ ] Create `features.lostPetsTeaser.presentation.mvi.LostPetsTeaserUiState` sealed class (Loading, Success, Error, Empty)
- [ ] Create `features.lostPetsTeaser.presentation.mvi.LostPetsTeaserIntent` sealed class (LoadData, RefreshData, PetClicked, ViewAllClicked)
- [ ] Create `features.lostPetsTeaser.presentation.mvi.LostPetsTeaserEffect` sealed class (NavigateToPetDetails, NavigateToLostPetsList, ShowError)
- [ ] Create `features.lostPetsTeaser.presentation.viewmodels.LostPetsTeaserViewModel` with MVI implementation:
  - `state: StateFlow<LostPetsTeaserUiState>`
  - `effects: SharedFlow<LostPetsTeaserEffect>`
  - `dispatchIntent(intent: LostPetsTeaserIntent)` method
  - Pure reducer function for state transitions

### 3. UI Layer - Lost Pets Teaser Feature
- [ ] Create `features.lostPetsTeaser.ui.LostPetsTeaser` composable (stateful, collects ViewModel)
- [ ] Create `features.lostPetsTeaser.ui.LostPetsTeaserContent` composable (stateless, with `@Preview`)
- [ ] Reuse existing `LostPetListItem` composable for individual pet items
- [ ] Add test tags: `lostPetsTeaser.*` and `lostPetsTeaser.item.{petId}`

### 4. UI Layer - Home Feature
- [ ] Create `features.home.ui.HomeScreen` composable (main home screen)
- [ ] Integrate teaser component into `HomeScreen` LazyColumn

### 4. Dependency Injection
- [ ] Create `HomeModule` in `/di/` with ViewModel and use case bindings
- [ ] Register module in Koin application setup

### 5. Navigation
- [ ] Handle `UiEffect.NavigateToPetDetails` using existing navigation patterns
- [ ] Handle `UiEffect.NavigateToLostPetsList` using existing navigation patterns
- [ ] Handle `UiEffect.ShowError` with snackbar/toast

### 6. Testing
- [ ] Unit tests for ViewModel (MVI state transitions)
- [ ] Unit tests for use case (business logic)
- [ ] Unit tests for repository (Ktor client integration)
- [ ] Component tests for composables with `@Preview` validation
- [ ] E2E tests for user journeys

## Key Code Patterns

### MVI ViewModel Structure
```kotlin
class LostPetsTeaserViewModel(
    private val getRecentLostPets: GetRecentLostPetsUseCase
) : MviViewModel<LostPetsTeaserUiState, LostPetsTeaserIntent, LostPetsTeaserEffect>() {

    override fun createInitialState(): LostPetsTeaserUiState = LostPetsTeaserUiState.Loading

    override suspend fun handleIntent(intent: LostPetsTeaserIntent) {
        when (intent) {
            is LostPetsTeaserIntent.LoadData -> loadData()
            // ... other intents
        }
    }

    private suspend fun loadData() {
        getRecentLostPets(limit = 5)
            .onSuccess { pets ->
                updateState { 
                    if (pets.isEmpty()) LostPetsTeaserUiState.Empty
                    else LostPetsTeaserUiState.Success(pets)
                }
            }
            .onFailure { error ->
                updateState { LostPetsTeaserUiState.Error(error.message ?: "Unknown error") }
            }
    }
}
```

### Composable Structure
```kotlin
@Composable
fun LostPetsTeaser(
    viewModel: LostPetsTeaserViewModel = koinViewModel(),
    onNavigateToPet: (String) -> Unit,
    onNavigateToList: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val effects = viewModel.effects

    // Handle effects
    LaunchedEffect(effects) {
        effects.collect { effect ->
            when (effect) {
                is LostPetsTeaserEffect.NavigateToPetDetails -> 
                    onNavigateToPet(effect.petId)
                // ... handle other effects
            }
        }
    }

    LostPetsTeaserContent(
        state = state,
        onIntent = viewModel::dispatchIntent,
        modifier = modifier
    )
}

@Preview
@Composable
private fun LostPetsTeaserContent(
    state: LostPetsTeaserUiState = LostPetsTeaserUiState.Loading,
    onIntent: (LostPetsTeaserIntent) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Implementation
}
```

## API Integration

### Request
```
GET /api/v1/announcements?type=LOST_PET&limit=5&sort=createdAt_DESC
```

### Response Mapping
```kotlin
// Announcement (API) → LostPet (Domain)
@Serializable
data class AnnouncementResponse(
    val id: String,
    val title: String,
    val description: String,
    val createdAt: String,
    val location: String,
    val images: List<String>?,
    val contactInfo: ContactInfo
)

fun AnnouncementResponse.toLostPet(): LostPet = LostPet(
    id = id,
    title = title,
    description = description,
    createdAt = Instant.parse(createdAt),
    location = location,
    imageUrl = images?.firstOrNull(),
    contactInfo = contactInfo
)
```

## Testing Strategy

### ViewModel Tests
```kotlin
@Test
fun `should emit Success state when data loads successfully`() = runTest {
    // Given
    coEvery { getRecentLostPets(limit = 5) } returns flowOf(Result.success(mockPets))
    
    // When
    viewModel.dispatchIntent(LostPetsTeaserIntent.LoadData)
    
    // Then
    assertEquals(LostPetsTeaserUiState.Success(mockPets), viewModel.state.value)
}
```

### E2E Test Structure
```kotlin
@Test
fun `user can navigate from teaser to pet details`() {
    // Given: Home screen is displayed with lost pets teaser
    
    // When: User taps on a pet in the teaser
    homePage.tapLostPetTeaserItem(petId)
    
    // Then: Pet details screen is displayed
    petDetailsPage.assertPetDetailsDisplayed(petId)
}
```

## Success Criteria

- [ ] Teaser loads within 2 seconds
- [ ] Shows up to 5 pets sorted by newest first
- [ ] Navigation works for both individual pets and "View All"
- [ ] Error handling displays appropriate messages
- [ ] Empty state shows encouraging message
- [ ] All tests pass (unit: 80% coverage, E2E: all scenarios)
- [ ] UI follows existing design patterns
