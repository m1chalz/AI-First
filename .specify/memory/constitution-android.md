# PetSpot Android Constitution

> **Platform**: Android (`/composeApp`) | **Language**: Kotlin | **UI**: Jetpack Compose | **Architecture**: MVI

This document contains all Android-specific architectural rules and standards. Read this file for Android-only tasks.

## Build & Test Commands

```bash
# Build Android
./gradlew :composeApp:assembleDebug

# Run Android tests
./gradlew :composeApp:testDebugUnitTest

# Run Android tests with coverage
./gradlew :composeApp:testDebugUnitTest koverHtmlReport
# View report at: composeApp/build/reports/kover/html/index.html
```

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Navigation**: Jetpack Navigation Component
- **Architecture**: MVI (Model-View-Intent)
- **Async**: Kotlin Coroutines + Flow
- **DI**: Koin (mandatory)
- **Testing**: JUnit 6 + Kotlin Test + Turbine (Flow testing)
- **Target**: JVM 17

## Core Principles

### Platform Independence

Android (`/composeApp`) implements its full technology stack independently:

- Domain models (Kotlin data classes)
- Repository implementations (Kotlin)
- Use cases / business logic (Kotlin)
- ViewModels with MVI architecture (Kotlin + Jetpack Compose)
- UI layer (Jetpack Compose)
- Own dependency injection setup (Koin - mandatory)

**Architecture Rules**:
- MUST NOT share compiled code with other platforms
- MAY share design patterns and architectural conventions
- MUST consume backend APIs via HTTP/REST
- MUST be independently buildable, testable, and deployable
- Domain models MAY differ from other platforms based on Android-specific needs

### 80% Unit Test Coverage (NON-NEGOTIABLE)

Android MUST maintain minimum 80% unit test coverage:

- **Location**: `/composeApp/src/androidUnitTest/`
- **Framework**: JUnit 6 + Kotlin Test + Turbine (for testing Kotlin Flow)
- **Run command**: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- **Report**: `composeApp/build/reports/kover/html/index.html`
- **Scope**: Domain models, use cases, ViewModels (MVI architecture)
- **Coverage target**: 80% line + branch coverage

**Testing Requirements**:
- MUST test happy path, error cases, and edge cases
- MUST follow Given-When-Then structure
- MUST use descriptive test names with backticks
- MUST test behavior, not implementation details
- MUST use test doubles (fakes, mocks) for dependencies

### Interface-Based Design (NON-NEGOTIABLE)

**Interface definition** (`/composeApp/src/androidMain/.../domain/repositories/`):
```kotlin
interface PetRepository {
    suspend fun getPets(): List<Pet>
    suspend fun getPetById(id: String): Pet
}
```

**Implementation** (`/composeApp/src/androidMain/.../data/repositories/`):
```kotlin
class PetRepositoryImpl(private val api: PetApi) : PetRepository {
    override suspend fun getPets(): List<Pet> = /* implementation */
}
```

### Dependency Injection (NON-NEGOTIABLE)

Android MUST use Koin for dependency injection:

- MUST use Koin for dependency injection
- Koin provides lightweight, Kotlin-native DI with minimal boilerplate
- Rationale: Consistency across Android codebase, excellent Kotlin DSL, mature ecosystem

**Example** (`/composeApp/src/androidMain/.../di/`):
```kotlin
val dataModule = module {
    single<PetRepository> { PetRepositoryImpl(get()) }
    single { PetApi(get()) }
}

val viewModelModule = module {
    viewModel { PetListViewModel(get()) }
}

// In Application class
class PetSpotApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PetSpotApp)
            modules(dataModule, viewModelModule)
        }
    }
}
```

### Asynchronous Programming (NON-NEGOTIABLE)

Android MUST use Kotlin Coroutines:

- ViewModels MUST use **Kotlin Coroutines** (`viewModelScope`)
- UI layer uses **Kotlin Flow** for reactive state
- MUST NOT use RxJava or LiveData for new code

**Example**:
```kotlin
class PetListViewModel(
    private val getPetsUseCase: GetPetsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun loadPets() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val result = getPetsUseCase()
            _state.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it) }
            )
        }
    }
}
```

**Prohibited Patterns**:
- ❌ RxJava
- ❌ LiveData for new code
- ❌ Callbacks (except platform APIs that require them)

### Test Identifiers (NON-NEGOTIABLE)

All interactive UI elements MUST have stable test identifiers:

- Use `testTag` modifier on all interactive composables
- Naming convention: `{screen}.{element}` (e.g., `petList.addButton`)

**Example**:
```kotlin
Button(
    onClick = { /* ... */ },
    modifier = Modifier.testTag("petList.addButton")
) {
    Text("Add Pet")
}

LazyColumn(modifier = Modifier.testTag("petList.list")) {
    items(pets) { pet ->
        PetItem(
            pet = pet,
            modifier = Modifier.testTag("petList.item")
        )
    }
}
```

**Requirements**:
- MUST be unique within a screen/page
- MUST be stable (not change between test runs)
- MUST NOT use dynamic values (UI tests fetch list items by index)

### Public API Documentation

**Documentation Requirements**:
- MUST document public APIs ONLY when the name alone is insufficient
- MUST use KDoc format (`/** ... */`)
- MUST be concise and high-level (focus on WHAT and WHY, not HOW)
- SHOULD be one to three sentences maximum

**GOOD Example**:
```kotlin
// ✅ DOCUMENT - Complex behavior needs explanation
/**
 * Retrieves all pets with optional filtering and caching strategy.
 * Returns cached data if network unavailable, automatically syncing when reconnected.
 */
suspend fun GetPetsUseCase.invoke(filter: PetFilter? = null): Result<List<Pet>>

// ✅ NO DOCUMENTATION NEEDED - Simple, obvious data class
data class Pet(
    val id: String,
    val name: String,
    val species: Species,
    val ownerId: String
)

// ✅ DOCUMENT - Interface contract needs explanation
/**
 * Repository for pet data operations.
 * Platform implementations handle persistence and network calls.
 */
interface PetRepository {
    // NO DOCUMENTATION - Method name is self-explanatory
    suspend fun getPets(): List<Pet>
    
    /** Saves pet and invalidates related caches. */
    suspend fun savePet(pet: Pet)
}
```

### Given-When-Then Test Convention (NON-NEGOTIABLE)

All unit tests MUST follow Given-When-Then structure:

```kotlin
@Test
fun `should return success when repository returns pets`() = runTest {
    // Given - setup initial state and dependencies
    val fakePets = listOf(
        Pet(id = "1", name = "Max", species = Species.DOG),
        Pet(id = "2", name = "Luna", species = Species.CAT)
    )
    val fakeRepository = FakePetRepository(pets = fakePets)
    val useCase = GetPetsUseCase(fakeRepository)

    // When - execute the action being tested
    val result = useCase.invoke()

    // Then - verify expected outcomes
    assertTrue(result.isSuccess)
    assertEquals(2, result.getOrNull()?.size)
    assertEquals("Max", result.getOrNull()?.first()?.name)
}
```

**Test Naming Convention**: Use backticks with descriptive names: `` `should {expected} when {condition}` ``

## Android MVI Architecture (NON-NEGOTIABLE)

All Android presentation features MUST follow a deterministic Model-View-Intent (MVI) loop.

### Core Contracts

- **UiState**: Immutable data class representing the entire screen (loading flags, data, errors, pending actions). MUST provide a `default` companion for initial state.
- **UserIntent**: Sealed class capturing every user interaction or external trigger (`Refresh`, `Retry`, `SelectPet(id)`, etc.). No stringly-typed intents.
- **Reducer**: Pure function that takes current `UiState` and domain results and returns a new `UiState`. Reducers MUST remain side-effect free and unit-tested.
- **UiEffect**: Sealed class for one-off events (navigation, snackbars). Delivered via `SharedFlow`.
- **MviViewModel**: Exposes `state: StateFlow<UiState>`, `effects: SharedFlow<UiEffect>`, and `dispatchIntent(intent: UserIntent)` to receive intents.

### Navigation Requirements

- Android navigation MUST use Jetpack Navigation Component (androidx.navigation:navigation-compose)
- Navigation graph MUST be defined declaratively using `NavHost` composable
- Deep links SHOULD be supported via Navigation Component's deep link mechanism
- ViewModels MUST NOT trigger navigation directly - use `UiEffect` for navigation events
- Navigation state MUST be managed by `NavController`, not application state

### MVI Loop Requirements

1. Compose UI collects `state` via `collectAsStateWithLifecycle()` and renders purely from `UiState`.
2. UI emits intents through callbacks, e.g., `viewModel.dispatchIntent(UserIntent.Refresh)`.
3. ViewModel handles intents inside `viewModelScope`, invokes use cases, then calls reducer to produce the next `UiState`.
4. Reducer updates the single source of truth (`MutableStateFlow`) and optionally emits `UiEffect`.
5. UI listens to `effects` using `LaunchedEffect` for navigation or transient messages.

### Implementation Rules

- Co-locate `UiState`, `UserIntent`, `UiEffect`, and reducer classes with the owning screen package.
- Never mutate Compose state directly; only emit via the `MutableStateFlow`.
- Provide exhaustive `when` handling for all intents and reducer branches.
- Write unit tests covering reducers and intent handling before wiring UI.
- Keep side effects (logging, analytics, navigation) inside dedicated effect handlers, not reducers.

### Composable Screen Pattern (NON-NEGOTIABLE)

All screen composables MUST follow a two-layer pattern:

**1. State Host Composable** (lightweight, stateful):
- Collects state from ViewModel (`collectAsStateWithLifecycle()`)
- Observes effects using `LaunchedEffect`
- Dispatches intents to ViewModel
- Delegates rendering to stateless composable
- Contains NO UI logic or layout code

**2. Stateless Composable** (pure presentation):
- Accepts `UiState` as immutable parameter
- Accepts callback lambdas for user interactions
- Contains ALL UI logic and layout code
- NO ViewModel dependency
- NO runtime dependencies (Koin, navigation, etc.)

### Preview Requirements (MANDATORY)

- Stateless composable MUST have at least one `@Preview` function
- Preview data MUST be delivered via `@PreviewParameter` with custom `PreviewParameterProvider<UiState>`
- Callback lambdas MUST be defaulted to no-ops
- Previews MUST focus on light mode only
- `PreviewParameterProvider` MUST provide realistic sample data (loading, success, error states)

### Full MVI Example

```kotlin
// UiState, Intent, Effect
data class PetListUiState(
    val pets: List<Pet> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    companion object { val Initial = PetListUiState() }
}

sealed interface PetListIntent {
    data object Refresh : PetListIntent
    data class SelectPet(val id: String) : PetListIntent
}

sealed interface PetListEffect {
    data class NavigateToDetails(val id: String) : PetListEffect
}

// ViewModel
class PetListViewModel(
    private val getPets: GetPetsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(PetListUiState.Initial)
    val state = _state.asStateFlow()
    private val _effects = MutableSharedFlow<PetListEffect>()
    val effects = _effects.asSharedFlow()

    fun dispatchIntent(intent: PetListIntent) {
        when (intent) {
            PetListIntent.Refresh -> refresh()
            is PetListIntent.SelectPet -> emitEffect(PetListEffect.NavigateToDetails(intent.id))
        }
    }

    private fun refresh() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        val result = getPets()
        _state.value = PetListReducer.reduce(_state.value, result)
    }
    
    private fun emitEffect(effect: PetListEffect) = viewModelScope.launch {
        _effects.emit(effect)
    }
}

// State host (with ViewModel dependency)
@Composable
fun PetListScreen(viewModel: PetListViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PetListEffect.NavigateToDetails -> { /* navigate */ }
            }
        }
    }
    
    PetListContent(
        state = state,
        onPetClick = { viewModel.dispatchIntent(PetListIntent.SelectPet(it)) },
        onRefresh = { viewModel.dispatchIntent(PetListIntent.Refresh) }
    )
}

// Stateless composable (pure, previewable)
@Composable
fun PetListContent(
    state: PetListUiState,
    onPetClick: (String) -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    when {
        state.isLoading -> LoadingIndicator()
        state.error != null -> ErrorView(state.error, onRefresh)
        else -> LazyColumn {
            items(state.pets) { pet ->
                PetItem(pet = pet, onClick = { onPetClick(pet.id) })
            }
        }
    }
}

// Preview with PreviewParameterProvider
class PetListUiStateProvider : PreviewParameterProvider<PetListUiState> {
    override val values = sequenceOf(
        PetListUiState.Initial.copy(isLoading = true),
        PetListUiState.Initial.copy(
            pets = listOf(
                Pet(id = "1", name = "Max", species = Species.DOG),
                Pet(id = "2", name = "Luna", species = Species.CAT)
            )
        ),
        PetListUiState.Initial.copy(error = "Network error")
    )
}

@Preview(showBackground = true)
@Composable
private fun PetListContentPreview(
    @PreviewParameter(PetListUiStateProvider::class) state: PetListUiState
) {
    MaterialTheme {
        PetListContent(state = state)
    }
}
```

## Module Structure

**`/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/`** (Android - Full Stack):

```
/composeApp/src/androidMain/.../
├── domain/
│   ├── models/           - Kotlin data classes, entities
│   ├── repositories/     - Repository interfaces
│   └── usecases/         - Business logic use cases
├── data/                 - Repository implementations (network, database)
├── features/
│   └── <feature>/
│       ├── ui/           - Composable screens (collect StateFlow, dispatch intents)
│       └── presentation/
│           ├── mvi/      - UiState, UserIntent, UiEffect, reducers
│           └── viewmodels/ - MviViewModel implementations
├── di/                   - Dependency injection modules (Koin mandatory)
└── navigation/           - Jetpack Navigation Component graph (NavHost)
```

## Testing Standards

### Unit Tests (MANDATORY)

- **Location**: `/composeApp/src/androidUnitTest/kotlin/`
- **Framework**: JUnit 6 + Kotlin Test + Turbine (Flow testing)
- **Scope**: Domain models, use cases, ViewModels (MVI), reducers
- **Coverage target**: 80% line + branch coverage

### E2E Tests (MANDATORY)

- **Framework**: Appium (Java + Cucumber)
- **Test Scenarios**: `/e2e-tests/src/test/resources/features/mobile/*.feature` (Gherkin with `@android` tag)
- **Screen Object Model**: `/e2e-tests/src/test/java/.../screens/` (Unified for iOS/Android)
  - Uses `@AndroidFindBy(uiAutomator = "...")` annotations
- **Step Definitions**: `/e2e-tests/src/test/java/.../steps-mobile/`
- **Run command**: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@android"`
- **Report**: `/e2e-tests/target/cucumber-reports/android/index.html`

## Compliance Checklist

All Android pull requests MUST:

- [ ] Run unit tests: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- [ ] Verify 80%+ test coverage in `composeApp/build/reports/kover/html/index.html`
- [ ] Verify new interactive UI elements have `testTag` modifier
- [ ] Verify screens follow MVI architecture:
  - [ ] Single `StateFlow<UiState>` source of truth with immutable data classes
  - [ ] Sealed `UserIntent` and optional `UiEffect` types
  - [ ] `dispatchIntent` entry point wired from UI actions
  - [ ] Reducers as pure functions with exhaustive `when` handling
  - [ ] Effects via `SharedFlow`/`Channel`
- [ ] Verify composable screen pattern:
  - [ ] State host composable (collects state, observes effects)
  - [ ] Stateless composable (pure presentation, previewable)
  - [ ] `@Preview` with `@PreviewParameter` for stateless composable
- [ ] Verify all new tests follow Given-When-Then structure
- [ ] Verify Koin is used for dependency injection

---

**Version**: 1.0.0 | **Based on Constitution**: v2.5.10

