# PetSpot Project Constitution

<!--
Sync Impact Report:
Version change: 1.8.0 → 1.9.0
Modified principles:
- II. Native Presentation (Android bullet now mandates Compose + MVI loop)
Added principles:
- XIV. Android Model-View-Intent Architecture (NON-NEGOTIABLE)
Modified sections:
- Module Structure: Android subsection documents required MVI packaging and unidirectional flow contracts
- Architecture Patterns: Added Android MVI pattern guidance with reducer/intent examples
- Testing Standards: Android ViewModel tests now cover reducers, intents, and effects explicitly
- Compliance: Added Android MVI audit checklist
Templates requiring updates:
- ✅ .specify/templates/plan-template.md (added Android MVI constitution check)
- ✅ .specify/templates/tasks-template.md (Android tasks now create UiState/UserIntent/Reducer artifacts)
- ✅ README.md (documented Compose MVI architecture expectation)
Follow-up TODOs:
- None
Previous changes (v1.8.0):
- Backend Architecture & Quality Standards principle describing Node.js/Express module
- Templates updated: AGENTS.md, README.md, constitution.md
Previous changes (v1.7.0):
- Backend Module (/server) guidelines with testing, documentation requirements
- Templates updated: AGENTS.md, README.md, constitution.md
Previous changes (v1.6.0):
- XII. Given-When-Then Test Convention
- Templates updated: plan-template.md, tasks-template.md, AGENTS.md, constitution.md
Previous changes (v1.5.0):
- XI. Public API Documentation
- Templates updated: AGENTS.md, plan-template.md, tasks-template.md
-->

## Core Principles

### I. Thin Shared Layer (NON-NEGOTIABLE)

The shared module (`/shared`) MUST contain ONLY domain logic:
- Domain models (data classes, entities)
- Repository interfaces (no implementations)
- Use cases / business logic
- Utility functions with no platform dependencies

The shared module MUST NOT contain:
- UI components or composables
- ViewModels or presentation state
- Platform-specific implementations
- Navigation logic
- Any Compose UI or SwiftUI code

**Rationale**: Keeping shared layer thin maximizes platform flexibility, reduces coupling,
and allows each platform to leverage native frameworks (Jetpack Compose, SwiftUI, React)
without compromise.

### II. Native Presentation (NON-NEGOTIABLE)

Each platform MUST implement its own presentation layer using native frameworks:
- **Android**: Jetpack Compose + MVI ViewModel loop (in `/composeApp`)
- **iOS**: SwiftUI + Swift ViewModels (in `/iosApp`)
- **Web**: React + TypeScript state management (in `/webApp`)

ViewModels and UI state MUST reside in platform-specific modules, NOT in `/shared`.

Android presentation logic MUST follow the Model-View-Intent pattern:
- Compose UI renders a single `UiState` data class exposed via `StateFlow`
- UI interactions emit `UserIntent` sealed classes through a `dispatchIntent` entry point
- ViewModels reduce intents into new immutable `UiState` values and optional one-off `UiEffect` emissions (`SharedFlow`)
- Side effects (navigation, snackbars) travel through the effect channel to keep reducers pure

**Rationale**: Native presentation ensures best UX, platform idioms, and full access to
platform capabilities without workarounds.

### III. 80% Unit Test Coverage (NON-NEGOTIABLE)

**Shared Module**: The `/shared` module MUST maintain minimum 80% unit test coverage measured by:
- Line coverage
- Branch coverage
- Location: `/shared/src/commonTest/kotlin/`
- Framework: Kotlin Test (multiplatform)
- Run command: `./gradlew :shared:test koverHtmlReport`

All domain logic, use cases, and business rules MUST have corresponding unit tests.

**ViewModels**: Each platform's ViewModels MUST maintain minimum 80% unit test coverage:
- **Android**: ViewModels in `/composeApp/src/androidUnitTest/`
  - Framework: JUnit 5 + Kotlin Test + Turbine (for testing Kotlin Flow)
  - Run command: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- **iOS**: ViewModels (Swift observable objects) in `/iosApp/iosAppTests/`
  - Framework: XCTest with Swift Concurrency (async/await)
  - Run command: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- **Web**: State management hooks in `/webApp/src/__tests__/`
  - Framework: Vitest + React Testing Library
  - Run command: `npm test -- --coverage`

**Rationale**: High test coverage on domain logic AND presentation logic ensures correctness
across all platforms. ViewModels contain critical business logic orchestration and state
management that must be verified independently of UI.

### IV. Platform Independence via Expect/Actual

When shared code requires platform-specific behavior (file I/O, networking, persistence):
- Define `expect` declarations in `commonMain`
- Implement `actual` declarations in platform-specific source sets (`androidMain`, `iosMain`, `jsMain`)
- Repository implementations MUST use expect/actual or dependency injection

Shared module MUST NOT directly import platform libraries (UIKit, Android SDK, Browser APIs).

**Rationale**: Expect/actual pattern maintains platform independence while allowing
shared code to delegate platform-specific concerns.

### V. Explicit Contracts & APIs

All repository interfaces and use case public APIs in `/shared` MUST:
- Use typed return values (`Result<T>`, sealed classes for states)
- Document expected behavior with KDoc
- Define clear error cases
- Be exported to JavaScript via `@JsExport` when consumed by web

**Rationale**: Clear contracts prevent platform-specific assumptions and enable safe
consumption from Swift, Kotlin/JVM, and TypeScript.

### VI. End-to-End Testing (NON-NEGOTIABLE)

Every feature specification MUST include end-to-end tests covering all user scenarios:

**Web Platform** (`/e2e-tests/web/`):
- Framework: Playwright with TypeScript
- Config: `playwright.config.ts` at repo root
- Test location: `/e2e-tests/web/specs/[feature-name].spec.ts`
- Run command: `npx playwright test`
- Coverage: All user stories from spec.md

**Mobile Platforms** (`/e2e-tests/mobile/`):
- Framework: Appium with TypeScript + WebdriverIO
- Config: `wdio.conf.ts` for Android/iOS
- Test location: `/e2e-tests/mobile/specs/[feature-name].spec.ts`
- Run command: `npm run test:mobile:android` or `npm run test:mobile:ios`
- Coverage: All user stories from spec.md for both Android and iOS

**Requirements**:
- Tests MUST be written in TypeScript for consistency across platforms
- Each user story MUST have at least one E2E test
- Tests MUST run against real application builds (not mocked)
- Tests MUST be executable in CI/CD pipeline
- Page Object Model pattern REQUIRED for maintainability

**Rationale**: E2E tests validate complete user flows across platforms, catching integration
issues that unit tests cannot detect. TypeScript provides type safety and enables sharing
test utilities across web and mobile test suites.

### VII. Interface-Based Design (NON-NEGOTIABLE)

All domain logic classes in `/shared` MUST follow interface-based design:

**Repository Pattern**:
- Define repository interfaces in `/shared/src/commonMain/.../repositories/`
- Platform-specific implementations in platform modules
- Example:
  ```kotlin
  // In commonMain
  interface PetRepository {
      suspend fun getPets(): Result<List<Pet>>
      suspend fun getPetById(id: String): Result<Pet>
  }

  // In androidMain (via DI)
  class PetRepositoryImpl(private val api: PetApi) : PetRepository { ... }
  ```

**Use Case Pattern**:
- Use cases MAY use interfaces when multiple implementations exist
- Simple use cases MAY be concrete classes
- Example:
  ```kotlin
  // Interface when needed
  interface GetPetsUseCase {
      suspend operator fun invoke(): Result<List<Pet>>
  }

  // Or concrete when single implementation
  class GetPetsUseCase(private val repository: PetRepository) {
      suspend operator fun invoke(): Result<List<Pet>> = repository.getPets()
  }
  ```

**Benefits for Testing**:
- Interfaces enable test doubles (fakes, mocks) without mocking frameworks
- Improves testability and maintains 80% coverage requirement
- Clear contracts between layers

**Rationale**: Interface-based design enables dependency inversion, improves testability
through test doubles, and provides clear boundaries between domain and infrastructure concerns.
Prototyping with interfaces allows testing domain logic in isolation.

### VIII. Dependency Injection with Koin (NON-NEGOTIABLE)

All projects MUST use **Koin** for dependency injection across platforms:

**Shared Module DI** (`/shared/src/commonMain/.../di/`):
- Define common modules for domain dependencies
- Use `module { }` DSL for registration
- Example:
  ```kotlin
  val domainModule = module {
      // Use cases
      single { GetPetsUseCase(get()) }
      single { SavePetUseCase(get()) }

      // Repositories as interfaces (implementations provided by platforms)
      // Platform modules will provide actual implementations
  }
  ```

**Platform-Specific DI**:
- **Android** (`/composeApp/src/androidMain/.../di/`):
  ```kotlin
  val androidDataModule = module {
      single<PetRepository> { PetRepositoryImpl(get()) }
      single { PetApi(get()) }
  }

  val androidViewModelModule = module {
      viewModel { PetListViewModel(get()) }
  }
  ```

- **iOS** (`/iosApp/iosApp/DI/`):
  ```swift
  // Use Koin from Kotlin/Native
  func initKoin() {
      KoinKt.doInitKoin()
  }

  // Or native Swift DI if preferred for ViewModels
  ```

- **Web** (`/webApp/src/services/di.ts`):
  ```typescript
  // Consume shared Koin modules or use native TS DI
  import { domainModule } from 'shared'
  ```

**DI Initialization**:
- Android: `Application.onCreate()` - `startKoin { modules(...) }`
- iOS: `@main` app entry - call Koin init from shared
- Web: App bootstrap - initialize Koin/JS

**Testing with Koin**:
- Use `koinTest` module for unit tests
- Override modules with test implementations
- Example:
  ```kotlin
  class GetPetsUseCaseTest : KoinTest {
      @Before
      fun setup() {
          startKoin {
              modules(module {
                  single<PetRepository> { FakePetRepository() }
              })
          }
      }
  }
  ```

**Rationale**: Koin provides multiplatform DI with minimal boilerplate, supports all target
platforms (Android, iOS, JS), and integrates well with testing frameworks. Centralized DI
configuration improves maintainability and enables easy swapping of implementations for testing.

### IX. Asynchronous Programming Standards (NON-NEGOTIABLE)

All asynchronous operations MUST follow platform-specific async patterns:

**Shared Module** (Kotlin Multiplatform):
- MUST use **Kotlin Coroutines** with `suspend` functions
- MUST NOT use callbacks or reactive streams (Flow is acceptable for streams)
- Example:
  ```kotlin
  interface PetRepository {
      suspend fun getPets(): Result<List<Pet>>
      suspend fun savePet(pet: Pet): Result<Unit>
  }

  class GetPetsUseCase(private val repository: PetRepository) {
      suspend operator fun invoke(): Result<List<Pet>> =
          repository.getPets()
  }
  ```

**Android**:
- ViewModels MUST use **Kotlin Coroutines** (`viewModelScope`)
- UI layer uses **Kotlin Flow** for reactive state
- MUST NOT use RxJava or LiveData for new code
- Example:
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

**iOS**:
- ViewModels MUST use **Swift Concurrency** (`async`/`await`)
- MUST NOT use Combine, RxSwift, or PromiseKit for new code
- Use `@MainActor` for UI updates
- Example:
  ```swift
  @MainActor
  class PetListViewModel: ObservableObject {
      @Published var pets: [Pet] = []
      @Published var isLoading = false

      private let getPetsUseCase: GetPetsUseCase

      func loadPets() async {
          isLoading = true
          defer { isLoading = false }

          let result = await getPetsUseCase.invoke()
          if let pets = result.getOrNil() {
              self.pets = pets
          }
      }
  }
  ```

**Web**:
- MUST use native **async/await** (ES2017+)
- MUST NOT use callbacks, Promises.then(), or RxJS for new code
- Example:
  ```typescript
  export const usePets = () => {
      const [pets, setPets] = useState<Pet[]>([]);
      const [isLoading, setIsLoading] = useState(false);

      const loadPets = async () => {
          setIsLoading(true);
          try {
              const result = await getPetsUseCase.invoke();
              setPets(result);
          } finally {
              setIsLoading(false);
          }
      };

      return { pets, isLoading, loadPets };
  };
  ```

**Prohibited Patterns**:
- ❌ Callbacks (except platform APIs that require them)
- ❌ Combine framework (iOS)
- ❌ RxJava / RxSwift / RxJS
- ❌ LiveData for new Android code
- ❌ Promise chains (`.then()` in JS/TS)

**Rationale**: Modern async/await syntax is consistent across platforms (Kotlin coroutines,
Swift Concurrency, JS async/await), making code more readable and maintainable. Eliminates
complexity of reactive frameworks while maintaining testability. Platform-native async patterns
ensure best performance and ecosystem compatibility.

### X. Test Identifiers for UI Controls (NON-NEGOTIABLE)

All interactive UI elements MUST have stable test identifiers for E2E testing:

**Android** (Jetpack Compose):
- Use `testTag` modifier on all interactive composables
- Naming convention: `{screen}.{element}.{action}` (e.g., `petList.addButton.click`)
- Example:
  ```kotlin
  Button(
      onClick = { /* ... */ },
      modifier = Modifier.testTag("petList.addButton.click")
  ) {
      Text("Add Pet")
  }

  LazyColumn(modifier = Modifier.testTag("petList.list")) {
      items(pets) { pet ->
          PetItem(
              pet = pet,
              modifier = Modifier.testTag("petList.item.${pet.id}")
          )
      }
  }
  ```

**iOS** (SwiftUI):
- Use `.accessibilityIdentifier()` modifier on all interactive views
- Naming convention: `{screen}.{element}.{action}` (e.g., `petList.addButton.click`)
- Example:
  ```swift
  Button("Add Pet") {
      // action
  }
  .accessibilityIdentifier("petList.addButton.click")

  List(pets) { pet in
      PetRow(pet: pet)
          .accessibilityIdentifier("petList.item.\(pet.id)")
  }
  .accessibilityIdentifier("petList.list")
  ```

**Web** (React):
- Use `data-testid` attribute on all interactive elements
- Naming convention: `{screen}.{element}.{action}` (e.g., `petList.addButton.click`)
- Example:
  ```tsx
  <button
      onClick={handleAdd}
      data-testid="petList.addButton.click"
  >
      Add Pet
  </button>

  <ul data-testid="petList.list">
      {pets.map(pet => (
          <li key={pet.id} data-testid={`petList.item.${pet.id}`}>
              <PetItem pet={pet} />
          </li>
      ))}
  </ul>
  ```

**Test Identifier Requirements**:
- MUST be unique within a screen/page
- MUST be stable (not change between test runs)
- MUST NOT use dynamic values EXCEPT for list item IDs
- MUST follow naming convention: `{screen}.{element}.{action?}`
- SHOULD include semantic meaning (e.g., `addButton` not `button1`)
- Lists/collections MUST use stable IDs (e.g., database ID, not array index)

**E2E Test Usage**:
```typescript
// Playwright (Web)
await page.locator('[data-testid="petList.addButton.click"]').click();
await expect(page.locator('[data-testid="petList.list"]')).toBeVisible();

// Appium (Mobile)
const addButton = await driver.$('~petList.addButton.click');
await addButton.click();
const petList = await driver.$('~petList.list');
await expect(petList).toBeDisplayed();
```

**Rationale**: Stable test identifiers prevent brittle E2E tests that break due to text changes,
layout modifications, or localization. Consistent naming across platforms enables sharing test
logic and utilities between web and mobile test suites. Explicit testIDs make tests self-documenting
and easier to maintain.

### XI. Public API Documentation (NON-NEGOTIABLE)

All public APIs MUST have concise, high-level documentation:

**Documentation Requirements**:
- MUST document all public classes, interfaces, functions, and properties
- MUST use platform-native documentation format:
  - **Kotlin**: KDoc (`/** ... */`)
  - **Swift**: SwiftDoc (`/// ...` or `/** ... */`)
  - **TypeScript/JavaScript**: JSDoc (`/** ... */`)
- MUST be **concise and high-level** (focus on WHAT and WHY, not HOW)
- MUST NOT duplicate implementation details visible in code
- SHOULD be one to three sentences maximum
- SHOULD answer: "What does this do?" and "When/why would I use it?"
- MUST NOT explain obvious things (e.g., "Returns a string" for `fun getString(): String`)

**Documentation Style** - GOOD Examples:

```kotlin
// Kotlin (Shared Module)
/**
 * Retrieves all pets from the repository with optional filtering.
 * Use for main pet list screen when fresh data is required.
 */
suspend fun GetPetsUseCase.invoke(filter: PetFilter? = null): Result<List<Pet>>

/**
 * Domain model representing a pet with owner information.
 * Shared across all platforms via KMP.
 */
data class Pet(
    val id: String,
    val name: String,
    val species: Species,
    val ownerId: String
)

/**
 * Repository interface for pet data operations.
 * Platform implementations handle persistence and network calls.
 */
interface PetRepository {
    /** Fetches all pets, returning cached data if network unavailable. */
    suspend fun getPets(): Result<List<Pet>>

    /** Retrieves single pet by ID, throwing if not found. */
    suspend fun getPetById(id: String): Result<Pet>
}
```

```swift
// Swift (iOS)
/// Manages pet list state and coordinates data fetching.
/// Observes repository changes and updates UI automatically.
@MainActor
class PetListViewModel: ObservableObject {
    /// Current list of pets displayed to user.
    @Published var pets: [Pet] = []

    /// Loads pets from repository and updates published state.
    /// Call on view appearance or when refresh is needed.
    func loadPets() async {
        // implementation
    }
}
```

```typescript
// TypeScript (Web)
/**
 * Custom hook for managing pet list state and operations.
 * Handles loading, error states, and automatic refresh.
 */
export function usePets() {
    // implementation
}

/**
 * Fetches all pets from the shared Kotlin module.
 * Returns empty array if service unavailable.
 */
export async function fetchPets(): Promise<Pet[]> {
    // implementation
}
```

**Documentation Style** - BAD Examples (avoid these):

```kotlin
// ❌ TOO VERBOSE - explains HOW (implementation details)
/**
 * This use case calls the PetRepository's getPets method and then
 * maps the result to filter out any null values before returning
 * a Result wrapper containing the list of Pet objects.
 */
suspend fun GetPetsUseCase.invoke(): Result<List<Pet>>

// ❌ TOO OBVIOUS - states what's clear from signature
/** Returns a string. */
fun getString(): String

// ❌ TOO LOW-LEVEL - describes internal mechanics
/**
 * Initializes the viewModelScope coroutine dispatcher and sets up
 * the StateFlow with an initial Loading state before calling the
 * repository layer through the use case abstraction.
 */
fun loadPets()

// ✅ GOOD - concise, high-level, explains purpose
/** Loads pets from repository and updates UI state. */
fun loadPets()
```

**What to Document**:
- ✅ Public classes, interfaces, data classes
- ✅ Public functions and methods
- ✅ Public properties (especially non-obvious ones)
- ✅ Function parameters (if not self-explanatory)
- ✅ Return values (if not obvious from type)
- ✅ Exceptions thrown (if any)
- ❌ Private implementation details
- ❌ Obvious getters/setters
- ❌ Override methods (unless behavior differs significantly)

**IDE Integration Examples**:

```kotlin
// KDoc with parameter and return documentation
/**
 * Searches pets by name with fuzzy matching.
 *
 * @param query Search term (minimum 2 characters)
 * @param limit Maximum results to return (default: 20)
 * @return Matching pets ordered by relevance
 * @throws InvalidQueryException if query too short
 */
suspend fun searchPets(query: String, limit: Int = 20): Result<List<Pet>>
```

```swift
// SwiftDoc with parameter documentation
/// Saves pet data to repository with validation.
///
/// - Parameters:
///   - pet: Pet instance to save
///   - validateOwner: Whether to verify owner exists (default: true)
/// - Returns: Saved pet with generated ID
/// - Throws: `ValidationError` if pet data invalid
func savePet(_ pet: Pet, validateOwner: Bool = true) async throws -> Pet
```

```typescript
// JSDoc with complete signature documentation
/**
 * Updates pet information in the database.
 *
 * @param petId - Unique identifier of pet to update
 * @param updates - Partial pet data to merge
 * @returns Promise resolving to updated pet
 * @throws {NotFoundError} If pet doesn't exist
 * @throws {ValidationError} If updates invalid
 */
async function updatePet(
    petId: string,
    updates: Partial<Pet>
): Promise<Pet>
```

**Documentation Checklist**:
- [ ] Explains WHAT the API does (purpose)
- [ ] Explains WHY/WHEN to use it (use case)
- [ ] Avoids explaining HOW (implementation)
- [ ] 1-3 sentences maximum (concise)
- [ ] Adds value beyond what code signature shows
- [ ] Uses platform-native doc format
- [ ] Documents parameters/returns if non-obvious
- [ ] Notes exceptions/errors thrown

**Rationale**: Concise, high-level documentation improves API discoverability, enables better
IDE assistance (autocomplete hints, parameter info), and accelerates onboarding for new developers.
Focusing on WHAT/WHY rather than HOW keeps docs maintainable and prevents duplication of information
already visible in code. Platform-native formats ensure documentation appears in IDE tooltips and
generated API docs.

### XII. Given-When-Then Test Convention (NON-NEGOTIABLE)

All unit tests and E2E tests MUST follow the Given-When-Then (Arrange-Act-Assert) structure:

**Test Structure Requirements**:
- MUST clearly separate test phases: setup (Given), action (When), verification (Then)
- MUST use descriptive test names explaining the scenario being tested
- MUST test one behavior per test case
- SHOULD use backtick test names for readability (Kotlin) or descriptive strings (other platforms)

**Kotlin Tests** (Shared module, Android ViewModels):
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

@Test
fun `should return failure when repository throws exception`() = runTest {
    // Given
    val exception = IOException("Network error")
    val fakeRepository = FakePetRepository(throwException = exception)
    val useCase = GetPetsUseCase(fakeRepository)

    // When
    val result = useCase.invoke()

    // Then
    assertTrue(result.isFailure)
    assertEquals(exception, result.exceptionOrNull())
}
```

**Swift Tests** (iOS ViewModels):
```swift
func testLoadPets_whenRepositorySucceeds_shouldUpdatePetsState() async {
    // Given - setup initial state
    let expectedPets = [
        Pet(id: "1", name: "Max", species: .dog),
        Pet(id: "2", name: "Luna", species: .cat)
    ]
    let fakeRepository = FakePetRepository(pets: expectedPets)
    let viewModel = PetListViewModel(repository: fakeRepository)

    // When - perform action
    await viewModel.loadPets()

    // Then - verify results
    XCTAssertEqual(viewModel.pets.count, 2)
    XCTAssertEqual(viewModel.pets.first?.name, "Max")
    XCTAssertFalse(viewModel.isLoading)
}

func testLoadPets_whenRepositoryFails_shouldSetErrorState() async {
    // Given
    let fakeRepository = FakePetRepository(shouldFail: true)
    let viewModel = PetListViewModel(repository: fakeRepository)

    // When
    await viewModel.loadPets()

    // Then
    XCTAssertTrue(viewModel.pets.isEmpty)
    XCTAssertNotNil(viewModel.errorMessage)
    XCTAssertFalse(viewModel.isLoading)
}
```

**TypeScript Tests** (Web hooks/components):
```typescript
describe('usePets', () => {
    it('should load pets successfully when service returns data', async () => {
        // Given - setup test data and mocks
        const mockPets = [
            { id: '1', name: 'Max', species: 'dog' },
            { id: '2', name: 'Luna', species: 'cat' }
        ];
        vi.mocked(petService.getPets).mockResolvedValue(mockPets);

        const { result } = renderHook(() => usePets());

        // When - trigger the action
        await act(async () => {
            await result.current.loadPets();
        });

        // Then - verify expected state
        expect(result.current.pets).toHaveLength(2);
        expect(result.current.pets[0].name).toBe('Max');
        expect(result.current.isLoading).toBe(false);
    });

    it('should handle error when service fails', async () => {
        // Given
        const error = new Error('Network error');
        vi.mocked(petService.getPets).mockRejectedValue(error);

        const { result } = renderHook(() => usePets());

        // When
        await act(async () => {
            await result.current.loadPets();
        });

        // Then
        expect(result.current.pets).toHaveLength(0);
        expect(result.current.error).toBe(error.message);
        expect(result.current.isLoading).toBe(false);
    });
});
```

**E2E Tests** (Playwright/Appium):
```typescript
// Playwright (Web)
test('should display pet list when user navigates to pets page', async ({ page }) => {
    // Given - setup test data and navigate to initial state
    await mockApi.setupPets([
        { id: '1', name: 'Max', species: 'dog' },
        { id: '2', name: 'Luna', species: 'cat' }
    ]);

    // When - perform user action
    await page.goto('/pets');
    await page.waitForLoadState('networkidle');

    // Then - verify expected UI state
    await expect(page.locator('[data-testid="petList.list"]')).toBeVisible();
    await expect(page.locator('[data-testid="petList.item.1"]')).toContainText('Max');
    await expect(page.locator('[data-testid="petList.item.2"]')).toContainText('Luna');
});

// Appium (Mobile)
it('should add new pet when user fills form and taps save button', async () => {
    // Given - navigate to add pet screen
    const addButton = await driver.$('~petList.addButton.click');
    await addButton.click();

    // When - fill form and submit
    const nameInput = await driver.$('~addPet.nameInput');
    await nameInput.setValue('Buddy');

    const speciesDropdown = await driver.$('~addPet.speciesDropdown');
    await speciesDropdown.click();
    const dogOption = await driver.$('~addPet.speciesOption.dog');
    await dogOption.click();

    const saveButton = await driver.$('~addPet.saveButton.click');
    await saveButton.click();

    // Then - verify pet appears in list
    const petList = await driver.$('~petList.list');
    await expect(petList).toBeDisplayed();
    const newPet = await driver.$('~petList.item.buddy');
    await expect(newPet).toBeDisplayed();
});
```

**Test Naming Conventions**:

**Kotlin/Android**:
- Backtick names: `` `should [expected behavior] when [condition]` ``
- Example: `` `should return empty list when repository has no pets` ``
- Alternative: `` `given empty repository when getting pets then returns empty list` ``

**Swift/iOS**:
- CamelCase with underscores: `test[What]_when[Condition]_should[ExpectedBehavior]`
- Example: `testLoadPets_whenRepositorySucceeds_shouldUpdatePetsState`
- Alternative: `testGetPets_givenEmptyRepository_thenReturnsEmptyList`

**TypeScript/Web**:
- Descriptive strings: `'should [expected behavior] when [condition]'`
- Example: `'should display error message when API fails'`
- Alternative: `'given failed API call when loading pets then shows error'`

**What to Test - Coverage Guidelines**:

✅ **DO Test**:
- Happy path (successful scenarios)
- Error cases (failures, exceptions)
- Edge cases (empty data, null values, boundary conditions)
- State transitions (loading → success, loading → error)
- Business logic (validation, calculations, transformations)

❌ **DON'T Test**:
- Framework internals (Kotlin coroutines, Swift async/await mechanics)
- Third-party library implementations
- Trivial getters/setters
- Private implementation details (test public behavior only)

**Given-When-Then Comments**:
- MUST include `// Given`, `// When`, `// Then` comments in complex tests
- MAY omit comments in simple tests where structure is obvious
- SHOULD use blank lines to visually separate test phases

**Benefits**:
- **Readability**: Tests serve as living documentation of system behavior
- **Maintainability**: Clear structure makes tests easier to update and debug
- **Consistency**: Uniform test structure across all platforms and test types
- **Communication**: Product owners and non-developers can understand test intent
- **Debugging**: When tests fail, it's immediately clear which phase failed

**Rationale**: Given-When-Then structure standardizes test organization across platforms,
making tests self-documenting and easier to understand. This pattern maps naturally to
user scenarios (acceptance criteria), E2E tests, and unit tests. Consistent test structure
reduces cognitive load when context-switching between platforms and improves onboarding
for new developers. Clear test names and structure also help identify gaps in test coverage
and make failed tests easier to debug.

### XIII. Backend Architecture & Quality Standards (NON-NEGOTIABLE)

The backend module (`/server`) MUST follow modern Node.js best practices with rigorous quality standards:

**Technology Stack**:
- **Runtime**: Node.js v24 (LTS)
- **Framework**: Express.js
- **Language**: TypeScript (strict mode)
- **Database**: Knex query builder + SQLite (initial phase, designed for easy migration to PostgreSQL)
- **Linting**: ESLint with TypeScript plugin
- **Testing**: Vitest (unit + integration) + SuperTest (API integration)

**Code Quality Requirements**:
- MUST enable ESLint with TypeScript plugin (`@typescript-eslint/eslint-plugin`)
- MUST follow Clean Code principles:
  - Functions should be small, focused, and do one thing
  - Descriptive naming (no abbreviations except well-known ones like `id`, `db`, `api`)
  - Avoid deep nesting (max 3 levels)
  - Prefer composition over inheritance
  - DRY (Don't Repeat Yourself) - extract reusable logic
  - Self-documenting code with JSDoc for public APIs
- MUST minimize dependencies in `package.json`:
  - Only add dependencies that provide significant value
  - Prefer well-maintained, security-audited packages
  - Avoid micro-dependencies (e.g., "is-even", "left-pad")
  - Document rationale for each dependency in comments
  - Regularly audit dependencies with `npm audit`

**Directory Structure** (inside `/server/src/`):

```
/server/src/
├── middlewares/       - Express middlewares (auth, logging, error handling)
├── routes/           - REST API endpoint definitions (Express routers)
├── services/         - Business logic layer (testable, pure functions)
├── database/         - Database configuration, migrations, query repositories
├── lib/              - Utility functions, helpers (pure, reusable)
├── __test__/         - Integration tests for REST API endpoints
├── app.ts            - Express app configuration (middleware setup, route registration)
└── index.ts          - Server entry point (port binding, startup)
```

**Separation of Concerns**:
- **`/src/middlewares`**: Request/response processing (authentication, logging, validation, error handling)
  - Example: `authMiddleware.ts`, `loggerMiddleware.ts`, `errorHandler.ts`
  - MUST be testable in isolation
  - MUST NOT contain business logic

- **`/src/routes`**: Endpoint definitions and request routing ONLY
  - Example: `petRoutes.ts`, `userRoutes.ts`
  - MUST be thin - delegate to services for business logic
  - MUST handle HTTP concerns (status codes, request/response mapping)
  - Example:
    ```typescript
    router.get('/pets', async (req, res, next) => {
        try {
            const pets = await petService.getAllPets();
            res.status(200).json(pets);
        } catch (error) {
            next(error); // Delegate to error handling middleware
        }
    });
    ```

- **`/src/services`**: Business logic layer (pure functions, testable in isolation)
  - Example: `petService.ts`, `userService.ts`
  - MUST contain all business rules, validation, calculations
  - MUST be framework-agnostic (no Express-specific code)
  - MUST be covered by unit tests in `/src/services/__test__/`
  - Example:
    ```typescript
    /**
     * Retrieves all pets with optional filtering.
     * @param filter - Optional filter criteria
     * @returns Array of pets matching filter
     */
    export async function getAllPets(filter?: PetFilter): Promise<Pet[]> {
        const pets = await petRepository.findAll(filter);
        return pets.map(normalizePet); // Business logic
    }
    ```

- **`/src/database`**: Database access layer (Knex queries, migrations, repositories)
  - Example: `knexConfig.ts`, `migrations/`, `petRepository.ts`
  - MUST use Knex query builder (no raw SQL strings except for complex queries)
  - MUST separate repository interfaces from implementations for testability
  - Migration files MUST be versioned and reversible (up/down)

- **`/src/lib`**: Utility functions and helpers (pure, reusable, no side effects)
  - Example: `validators.ts`, `formatters.ts`, `constants.ts`
  - MUST be framework-agnostic
  - MUST be covered by unit tests in `/src/lib/__test__/`
  - Example:
    ```typescript
    /**
     * Validates email format using RFC 5322 regex.
     * @param email - Email string to validate
     * @returns True if valid email format
     */
    export function isValidEmail(email: string): boolean {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }
    ```

**Test-Driven Development (TDD) Workflow**:

Backend development MUST follow TDD (Red-Green-Refactor):

1. **RED**: Write a failing test first
   - For new feature: write unit test in `/src/services/__test__/` or `/src/lib/__test__/`
   - For new endpoint: write integration test in `/src/__test__/`
   - Run test → verify it fails (red)

2. **GREEN**: Write minimal code to make test pass
   - Implement function/endpoint
   - Run test → verify it passes (green)

3. **REFACTOR**: Improve code quality without changing behavior
   - Apply Clean Code principles
   - Extract reusable functions
   - Run tests → verify they still pass

**Testing Strategy**:

**Unit Tests** (Vitest) - MUST achieve 80% coverage:
- **Location**:
  - `/src/services/__test__/` - Business logic tests
  - `/src/lib/__test__/` - Utility function tests
- **Scope**: Test business logic and utilities in isolation
- **Framework**: Vitest
- **Run command**: `npm test` (from `/server` directory)
- **Coverage command**: `npm test -- --coverage`
- **Coverage target**: 80% line + branch coverage
- **Requirements**:
  - MUST follow Given-When-Then structure
  - MUST use descriptive test names
  - MUST test happy path, error cases, edge cases
  - MUST NOT mock framework internals (test behavior, not implementation)

Example unit test:
```typescript
// /src/services/__test__/petService.test.ts
import { describe, it, expect, beforeEach } from 'vitest';
import { getAllPets, createPet } from '../petService';
import { FakePetRepository } from '../../database/__test__/FakePetRepository';

describe('petService', () => {
    let fakeRepository: FakePetRepository;

    beforeEach(() => {
        fakeRepository = new FakePetRepository();
    });

    describe('getAllPets', () => {
        it('should return all pets when repository has data', async () => {
            // Given - repository with test data
            const mockPets = [
                { id: 1, name: 'Max', species: 'dog' },
                { id: 2, name: 'Luna', species: 'cat' }
            ];
            fakeRepository.setPets(mockPets);

            // When - service is called
            const result = await getAllPets();

            // Then - all pets are returned
            expect(result).toHaveLength(2);
            expect(result[0].name).toBe('Max');
        });

        it('should return empty array when no pets exist', async () => {
            // Given - empty repository
            fakeRepository.setPets([]);

            // When
            const result = await getAllPets();

            // Then
            expect(result).toHaveLength(0);
        });
    });

    describe('createPet', () => {
        it('should throw ValidationError when name is missing', async () => {
            // Given - invalid pet data
            const invalidPet = { name: '', species: 'dog' };

            // When & Then - validation error is thrown
            await expect(createPet(invalidPet)).rejects.toThrow('Pet name is required');
        });
    });
});
```

**Integration Tests** (Vitest + SuperTest) - MUST achieve 80% coverage:
- **Location**: `/src/__test__/`
- **Scope**: Test REST API endpoints end-to-end (request → response)
- **Framework**: Vitest + SuperTest
- **Run command**: `npm test` (from `/server` directory)
- **Coverage command**: `npm test -- --coverage`
- **Coverage target**: 80% line + branch coverage (API routes and business logic combined)
- **Requirements**:
  - MUST follow Given-When-Then structure
  - MUST test all HTTP methods (GET, POST, PUT, DELETE)
  - MUST test success scenarios (2xx status codes)
  - MUST test error scenarios (4xx, 5xx status codes)
  - MUST test request validation (missing fields, invalid data)
  - MUST test authentication/authorization (when implemented)
  - MUST use test database (separate from development database)

Example integration test:
```typescript
// /src/__test__/petRoutes.test.ts
import { describe, it, expect, beforeAll, afterAll, beforeEach } from 'vitest';
import request from 'supertest';
import app from '../app';
import { setupTestDatabase, teardownTestDatabase, clearDatabase } from './testDbHelper';

describe('GET /api/pets', () => {
    beforeAll(async () => {
        await setupTestDatabase();
    });

    afterAll(async () => {
        await teardownTestDatabase();
    });

    beforeEach(async () => {
        await clearDatabase();
    });

    it('should return 200 and all pets when database has data', async () => {
        // Given - database seeded with test pets
        await seedDatabase([
            { id: 1, name: 'Max', species: 'dog' },
            { id: 2, name: 'Luna', species: 'cat' }
        ]);

        // When - client requests all pets
        const response = await request(app)
            .get('/api/pets')
            .expect('Content-Type', /json/)
            .expect(200);

        // Then - response contains all pets
        expect(response.body).toHaveLength(2);
        expect(response.body[0].name).toBe('Max');
        expect(response.body[1].name).toBe('Luna');
    });

    it('should return 200 and empty array when no pets exist', async () => {
        // Given - empty database
        // (cleared by beforeEach)

        // When
        const response = await request(app)
            .get('/api/pets')
            .expect(200);

        // Then
        expect(response.body).toHaveLength(0);
    });
});

describe('POST /api/pets', () => {
    it('should return 201 and created pet when valid data provided', async () => {
        // Given - valid pet data
        const newPet = { name: 'Buddy', species: 'dog', ownerId: 1 };

        // When - client creates new pet
        const response = await request(app)
            .post('/api/pets')
            .send(newPet)
            .expect('Content-Type', /json/)
            .expect(201);

        // Then - pet is created and returned
        expect(response.body.name).toBe('Buddy');
        expect(response.body.id).toBeDefined();
    });

    it('should return 400 when name is missing', async () => {
        // Given - invalid pet data (missing name)
        const invalidPet = { species: 'dog', ownerId: 1 };

        // When - client attempts to create pet
        const response = await request(app)
            .post('/api/pets')
            .send(invalidPet)
            .expect(400);

        // Then - validation error returned
        expect(response.body.error).toContain('name');
    });

    it('should return 404 when owner does not exist', async () => {
        // Given - valid pet data with non-existent owner
        const petWithInvalidOwner = { name: 'Buddy', species: 'dog', ownerId: 999 };

        // When
        const response = await request(app)
            .post('/api/pets')
            .send(petWithInvalidOwner)
            .expect(404);

        // Then
        expect(response.body.error).toBe('Owner not found');
    });
});
```

**JSDoc Documentation** (MANDATORY):
- All public functions in `/src/services` and `/src/lib` MUST have JSDoc
- All REST API endpoints (route handlers) MUST have JSDoc describing:
  - HTTP method and path
  - Request parameters/body schema
  - Response status codes and body schema
  - Error scenarios
- Example:
  ```typescript
  /**
   * Retrieves all pets with optional filtering.
   *
   * @param filter - Optional filter criteria (species, ownerId)
   * @returns Promise resolving to array of pets
   * @throws {DatabaseError} If database query fails
   */
  export async function getAllPets(filter?: PetFilter): Promise<Pet[]> {
      // implementation
  }

  /**
   * GET /api/pets
   * Returns all pets with optional filtering by species or owner.
   *
   * Query parameters:
   * - species (optional): Filter by species (dog, cat, bird, etc.)
   * - ownerId (optional): Filter by owner ID
   *
   * Responses:
   * - 200: Success - Returns array of Pet objects
   * - 500: Server error - Database query failed
   *
   * @route GET /api/pets
   */
  router.get('/pets', async (req, res, next) => {
      // implementation
  });
  ```

**Database Layer Standards**:
- MUST use Knex query builder (avoid raw SQL)
- MUST create migrations for schema changes (versioned, reversible)
- MUST use repository pattern for data access (enables test doubles)
- SHOULD design for easy migration from SQLite to PostgreSQL:
  - Avoid SQLite-specific features
  - Use Knex-supported data types
  - Test migrations on both SQLite and PostgreSQL (when possible)
- Example repository:
  ```typescript
  // /src/database/petRepository.ts
  import { Knex } from 'knex';
  import { Pet, PetFilter } from '../types';

  export interface IPetRepository {
      findAll(filter?: PetFilter): Promise<Pet[]>;
      findById(id: number): Promise<Pet | null>;
      create(pet: Omit<Pet, 'id'>): Promise<Pet>;
      update(id: number, pet: Partial<Pet>): Promise<Pet>;
      delete(id: number): Promise<void>;
  }

  export class PetRepository implements IPetRepository {
      constructor(private db: Knex) {}

      async findAll(filter?: PetFilter): Promise<Pet[]> {
          let query = this.db<Pet>('pets');

          if (filter?.species) {
              query = query.where('species', filter.species);
          }
          if (filter?.ownerId) {
              query = query.where('ownerId', filter.ownerId);
          }

          return query.select('*');
      }

      // ... other methods
  }
  ```

**Dependency Management**:
- MUST document each dependency's purpose in `package.json` via comments (using `//` syntax)
- MUST justify any dependency that adds >100KB to bundle size
- MUST NOT add dependencies for trivial functionality (e.g., "is-even", "left-pad")
- SHOULD prefer:
  - Native Node.js APIs over third-party packages
  - Well-maintained packages with active communities
  - Packages with minimal transitive dependencies
  - Security-audited packages (check npm audit, Snyk)
- MUST run `npm audit` before each release
- Example `package.json` with rationale:
  ```json
  {
    "dependencies": {
      "express": "^4.18.2",           // Web framework - industry standard
      "knex": "^3.0.1",                // Query builder - type-safe SQL
      "sqlite3": "^5.1.6",             // SQLite driver for development
      "pg": "^8.11.3"                  // PostgreSQL driver for production
    },
    "devDependencies": {
      "@typescript-eslint/eslint-plugin": "^6.0.0",  // TypeScript linting
      "@typescript-eslint/parser": "^6.0.0",         // TypeScript parser for ESLint
      "eslint": "^8.50.0",                          // Code quality enforcement
      "vitest": "^1.0.0",                           // Fast test runner with TypeScript
      "supertest": "^6.3.3",                        // HTTP assertions for API tests
      "typescript": "^5.2.0"                         // Type safety and modern JS features
    }
  }
  ```

**Prohibited Patterns**:
- ❌ Business logic in route handlers (routes should only handle HTTP concerns)
- ❌ Raw SQL strings (use Knex query builder)
- ❌ Synchronous file I/O in request handlers (blocks event loop)
- ❌ Callback-based async patterns (use async/await)
- ❌ Mutable global state (leads to bugs in concurrent requests)
- ❌ Magic numbers/strings (define constants with descriptive names)
- ❌ Deep nesting (>3 levels - extract functions or use early returns)
- ❌ Micro-dependencies (e.g., "is-even", "left-pad" - implement yourself)

**Error Handling**:
- MUST use centralized error handling middleware
- MUST return appropriate HTTP status codes:
  - 400: Bad request (validation errors)
  - 401: Unauthorized (authentication required)
  - 403: Forbidden (authenticated but insufficient permissions)
  - 404: Not found (resource doesn't exist)
  - 500: Internal server error (unexpected errors)
- MUST log errors with context (request ID, user ID, timestamp)
- MUST NOT expose sensitive information in error responses (stack traces, DB errors)
- Example:
  ```typescript
  // /src/middlewares/errorHandler.ts
  export function errorHandler(err: Error, req: Request, res: Response, next: NextFunction) {
      logger.error('Request failed', {
          error: err.message,
          stack: err.stack,
          requestId: req.id,
          path: req.path
      });

      if (err instanceof ValidationError) {
          return res.status(400).json({ error: err.message });
      }

      if (err instanceof NotFoundError) {
          return res.status(404).json({ error: err.message });
      }

      // Generic error response (don't expose internals)
      res.status(500).json({ error: 'Internal server error' });
  }
  ```

**Rationale**: Modern backend architecture requires clear separation of concerns to maintain
testability and scalability. TDD ensures business logic correctness before integration.
Clean Code principles and minimal dependencies reduce maintenance burden and security surface.
Strict TypeScript with ESLint catches bugs early. Repository pattern enables easy testing
with test doubles. Knex provides type-safe database queries while remaining database-agnostic
(SQLite for development, PostgreSQL for production). 80% test coverage on both unit and
integration levels ensures API reliability and correctness.

### XIV. Android Model-View-Intent Architecture (NON-NEGOTIABLE)

All Android presentation features MUST follow a deterministic Model-View-Intent (MVI) loop to
keep Compose UI declarative and testable.

**Core contracts**:
- `UiState`: Immutable data class representing the entire screen (loading flags, data, errors,
  pending actions). MUST provide a `default` companion for initial state.
- `UserIntent`: Sealed class capturing every user interaction or external trigger
  (`Refresh`, `Retry`, `SelectPet(id)`, etc.). No stringly-typed intents.
- `Reducer`: Pure function that takes current `UiState` and domain results (or `PartialState`)
  and returns a new `UiState`. Reducers MUST remain side-effect free and unit-tested.
- `UiEffect`: Sealed class for one-off events (navigation, snackbars). Delivered via `SharedFlow`.
- `MviViewModel`: Exposes `state: StateFlow<UiState>`, `effects: SharedFlow<UiEffect>`, and
  `dispatchIntent(intent: UserIntent)` to receive intents.

**Loop requirements**:
1. Compose UI collects `state` via `collectAsStateWithLifecycle()` and renders purely from `UiState`.
2. UI emits intents through callbacks, e.g., `viewModel.dispatchIntent(UserIntent.Refresh)`.
3. ViewModel handles intents inside `viewModelScope`, invokes use cases, then calls reducer to produce
   the next `UiState`.
4. Reducer updates the single source of truth (`MutableStateFlow`) and optionally emits `UiEffect`.
5. UI listens to `effects` using `LaunchedEffect` for navigation or transient messages.

**Implementation rules**:
- Co-locate `UiState`, `UserIntent`, `UiEffect`, and reducer classes with the owning screen package.
- Never mutate Compose state directly; only emit via the `MutableStateFlow`.
- Provide exhaustive `when` handling for all intents and reducer branches.
- Write unit tests covering reducers and intent handling before wiring UI.
- Keep side effects (logging, analytics, navigation) inside dedicated effect handlers, not reducers.

```kotlin
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

class PetListViewModel(
    private val getPets: GetPetsUseCase
) : ViewModel(), MviViewModel<PetListUiState, PetListEffect, PetListIntent> {
    private val _state = MutableStateFlow(PetListUiState.Initial)
    override val state = _state.asStateFlow()
    private val _effects = MutableSharedFlow<PetListEffect>()
    override val effects = _effects.asSharedFlow()

    override fun dispatchIntent(intent: PetListIntent) {
        when (intent) {
            PetListIntent.Refresh -> refresh()
            is PetListIntent.SelectPet -> emitEffect(intent.id)
        }
    }

    private fun refresh() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        val result = getPets()
        _state.value = PetListReducer.reduce(_state.value, result)
    }
}
```

**Rationale**: MVI enforces unidirectional data flow, simplifies reasoning about screen behavior,
and makes reducers easy to unit test. Immutable `UiState` snapshots prevent UI drift, while
explicit intents capture every interaction for analytics and debugging. Effect channels keep
navigation and transient events isolated from state, reducing Compose recompositions and bugs.

## Platform Architecture Rules

### Dependency Flow

```
┌─────────────────────────────────────────────────────┐
│  Platform Clients (composeApp, iosApp, webApp)     │
│  - ViewModels                                       │
│  - UI Components                                    │
│  - Navigation                                       │
│  - HTTP clients consuming /server REST API         │
└──────────────┬──────────────────┬───────────────────┘
               │ depends on       │ HTTP requests
               ▼                  ▼
┌─────────────────────────┐   ┌────────────────────────┐
│  /shared (commonMain)   │   │  /server (Node.js)     │
│  - Domain Models        │   │  - REST API endpoints  │
│  - Repository Interfaces│   │  - Business logic      │
│  - Use Cases            │   │  - Database (SQLite)   │
│  - NO ViewModels, NO UI │   │  - NOT part of KMP     │
└─────────────────────────┘   └────────────────────────┘
```

**Clarifications**:
- Platform clients (Android/iOS/Web) MAY depend on `/shared` for domain models
- Platform clients MUST consume `/server` via REST API (HTTP requests)
- `/shared` MUST NOT depend on platform modules or `/server`
- `/server` is a standalone Node.js backend (NOT part of KMP)
- `/server` MAY define its own TypeScript models (does not need to use `/shared`)

### Module Structure

**`/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/`**
- `domain/models/` - Data classes, entities
- `domain/repositories/` - Repository interfaces (NOT implementations)
- `domain/usecases/` - Business logic use cases (concrete or interfaces)
- `di/` - Koin modules for domain dependencies
- `utils/` - Pure functions, no platform deps

**`/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/`**
- `features/<feature>/ui/` - Composable screens that collect `StateFlow<UiState>` and emit intents via callbacks
- `features/<feature>/presentation/mvi/` - `UiState`, `UserIntent`, `UiEffect`, reducers, and MVI helpers
- `features/<feature>/presentation/viewmodels/` - `MviViewModel` implementations exposing `state`, `effects`, and `dispatchIntent`
- `data/` - Android repository implementations
- `di/` - Koin modules for Android (data + ViewModel modules)
- `navigation/` - Compose Navigation and effect handlers

**`/iosApp/iosApp/`**
- `Views/` - SwiftUI views
- `ViewModels/` - Swift observable objects
- `Repositories/` - iOS repository implementations
- `DI/` - Koin initialization or native Swift DI

**`/webApp/src/`**
- `components/` - React components
- `hooks/` - React hooks for state
- `services/` - Repository implementations consuming shared Kotlin/JS
- `di/` - DI setup for web (if using Koin/JS)

**`/server/src/`** (Node.js/Express Backend - NOT part of KMP):
- `middlewares/` - Express middlewares (auth, logging, error handling, validation)
  - Examples: `authMiddleware.ts`, `loggerMiddleware.ts`, `errorHandler.ts`
  - MUST be testable in isolation
  - MUST NOT contain business logic
- `routes/` - REST API route handlers (Express routers)
  - Examples: `petRoutes.ts`, `userRoutes.ts`
  - MUST be thin - delegate to services for business logic
  - MUST handle HTTP concerns (status codes, request/response mapping)
- `services/` - Business logic layer (testable, pure functions)
  - Examples: `petService.ts`, `userService.ts`
  - MUST contain all business rules, validation, calculations
  - MUST be framework-agnostic (no Express-specific code)
  - MUST be covered by unit tests in `/src/services/__test__/`
- `database/` - Database access layer (Knex queries, migrations, repositories)
  - Examples: `knexConfig.ts`, `migrations/`, `petRepository.ts`
  - MUST use Knex query builder (no raw SQL strings except complex queries)
  - MUST separate repository interfaces from implementations
  - Migration files MUST be versioned and reversible
- `lib/` - Utility functions, helpers (pure, reusable)
  - Examples: `validators.ts`, `formatters.ts`, `constants.ts`
  - MUST be framework-agnostic
  - MUST be covered by unit tests in `/src/lib/__test__/`
- `__test__/` - Integration tests for REST API endpoints (Vitest + SuperTest)
- `app.ts` - Express app configuration (middleware setup, route registration)
- `index.ts` - Server entry point (port binding, startup)

**Backend Guidelines**:
- `/server` is a standalone Node.js v24/TypeScript service
- Uses Express for REST API, Knex for database access (SQLite initially, designed for PostgreSQL migration)
- NOT part of Kotlin Multiplatform shared code
- Clients (Android/iOS/Web) consume server API via HTTP
- MUST use ESLint with TypeScript plugin for code quality
- MUST follow Clean Code principles (small functions, descriptive names, DRY, max 3 nesting levels)
- MUST minimize dependencies in `package.json` (document rationale, avoid micro-dependencies)
- MUST separate concerns: middlewares, routes (thin), services (business logic), database, lib (utilities)
- MUST follow TDD workflow (Red-Green-Refactor)
- MUST have JSDoc documentation for all public API endpoints and business logic
- MUST follow Given-When-Then convention for API tests
- MUST achieve 80% test coverage for business logic (`/src/services`, `/src/lib`) and API routes
- Unit tests: `/src/services/__test__/` and `/src/lib/__test__/` (Vitest)
- Integration tests: `/src/__test__/` (Vitest + SuperTest)
- Run tests: `npm test` (from `/server` directory)
- Run with coverage: `npm test -- --coverage`
- Run dev server: `npm run dev` (from `/server` directory)

## Architecture Patterns

### Repository Pattern (MANDATORY)

All data access MUST follow the Repository pattern:

**Interface Definition** (in `/shared/src/commonMain/.../repositories/`):
```kotlin
interface PetRepository {
    suspend fun getPets(): Result<List<Pet>>
    suspend fun getPetById(id: String): Result<Pet>
    suspend fun savePet(pet: Pet): Result<Unit>
}
```

**Platform Implementation** (in platform-specific modules):
```kotlin
// Android: /composeApp/src/androidMain/.../data/
class PetRepositoryImpl(
    private val api: PetApi,
    private val database: PetDatabase
) : PetRepository {
    override suspend fun getPets(): Result<List<Pet>> =
        try {
            val pets = api.fetchPets()
            database.insertAll(pets)
            Result.success(pets)
        } catch (e: Exception) {
            Result.failure(e)
        }
}
```

**Benefits**:
- Testable with fake implementations
- Platform-agnostic domain layer
- Clear separation of concerns

### Use Case Pattern (RECOMMENDED)

Business logic SHOULD be encapsulated in use cases:

**Simple Use Case** (concrete class):
```kotlin
class GetPetsUseCase(private val repository: PetRepository) {
    suspend operator fun invoke(): Result<List<Pet>> =
        repository.getPets()
}
```

**Complex Use Case** (with business rules):
```kotlin
class SearchPetsUseCase(
    private val repository: PetRepository,
    private val validator: SearchValidator
) {
    suspend operator fun invoke(query: String): Result<List<Pet>> {
        if (!validator.isValid(query)) {
            return Result.failure(InvalidQueryException())
        }
        return repository.getPets()
            .map { pets -> pets.filter { it.matches(query) } }
    }
}
```

**Benefits**:
- Single Responsibility Principle
- Reusable across platforms
- Easy to test in isolation

### Android MVI Pattern (NON-NEGOTIABLE)

Android screens MUST implement a unidirectional MVI loop:

- **State**: Immutable `UiState` data class with full screen snapshot and sensible defaults.
- **Intent**: Sealed class describing every user/system trigger. UI dispatches intents via `viewModel.dispatchIntent()`.
- **Reducer**: Pure function (often `object FeatureReducer`) converting current `UiState` plus domain result into the next `UiState`.
- **Effects**: Optional sealed class for one-off events emitted through `SharedFlow`.
- **ViewModel**: Implements `MviViewModel<UiState, UiEffect, UserIntent>` (or equivalent contract) exposing:
  - `val state: StateFlow<UiState>`
  - `val effects: SharedFlow<UiEffect>`
  - `fun dispatchIntent(intent: UserIntent)`
- **Compose Binding**: UI collects `state` via `collectAsStateWithLifecycle()`, renders purely from `UiState`,
  and uses `LaunchedEffect`/`Flow.collect` to handle `effects`.

Testing expectations:
- Reducers MUST have unit tests covering each branch.
- Intent handlers MUST be tested with Turbine to assert emitted states/effects.
- `StateFlow` should never emit mutable references—copy the state before updates.

Prohibited shortcuts:
- No mutable Compose `var` or `mutableStateOf` in ViewModels.
- No multiple state sources per screen—`StateFlow<UiState>` is the single truth.
- No direct navigation calls from UI; emit an effect and handle it centrally.

### Dependency Injection Setup

**Shared Module** (`/shared/src/commonMain/.../di/DomainModule.kt`):
```kotlin
val domainModule = module {
    // Use cases
    factory { GetPetsUseCase(get()) }
    factory { SearchPetsUseCase(get(), get()) }
    single { SearchValidator() }
}
```

**Android** (`/composeApp/src/androidMain/.../di/`):
```kotlin
val dataModule = module {
    single<PetRepository> { PetRepositoryImpl(get(), get()) }
    single { PetApi(get()) }
    single { PetDatabase.getInstance(androidContext()) }
}

val viewModelModule = module {
    viewModel { PetListViewModel(get(), get()) }
}

// In Application class
class PetSpotApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PetSpotApp)
            modules(domainModule, dataModule, viewModelModule)
        }
    }
}
```

**iOS** (`/iosApp/iosApp/DI/KoinInitializer.swift`):
```swift
import shared

func initKoin() {
    let koinApp = KoinKt.startKoin(
        appDeclaration: { app in
            app.modules([
                DomainModuleKt.domainModule,
                IosDataModuleKt.iosDataModule
            ])
        }
    )
}

// In @main
@main
struct PetSpotApp: App {
    init() {
        initKoin()
    }
}
```

## Testing Standards

### Unit Tests - Shared Module (MANDATORY)

- **Location**: `/shared/src/commonTest/kotlin/`
- **Target**: 80% line + branch coverage on commonMain
- **Framework**: Kotlin Test (multiplatform) + Koin Test
- **Run command**: `./gradlew :shared:test koverHtmlReport`
- **Report**: `shared/build/reports/kover/html/index.html`
- **Scope**: Domain models, use cases, business logic

**Testing with Koin**:
```kotlin
class GetPetsUseCaseTest : KoinTest {
    private val useCase: GetPetsUseCase by inject()

    @Before
    fun setup() {
        startKoin {
            modules(module {
                single<PetRepository> { FakePetRepository() }
                single { GetPetsUseCase(get()) }
            })
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `should return pets when repository succeeds`() = runTest {
        // Given - repository configured with test data in setup

        // When - execute use case
        val result = useCase()

        // Then - verify success
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun `should return failure when repository throws exception`() = runTest {
        // Given - reconfigure with failing repository
        stopKoin()
        startKoin {
            modules(module {
                single<PetRepository> { FakePetRepository(shouldFail = true) }
                single { GetPetsUseCase(get()) }
            })
        }
        val failingUseCase: GetPetsUseCase by inject()

        // When
        val result = failingUseCase()

        // Then
        assertTrue(result.isFailure)
    }
}
```

### Unit Tests - ViewModels (MANDATORY)

Platform-specific ViewModel tests with 80% coverage requirement:

**Android**:
- **Location**: `/composeApp/src/androidUnitTest/kotlin/`
- **Framework**: JUnit 5 + Kotlin Test + Turbine (for Flow testing)
- **Run command**: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- **Report**: `composeApp/build/reports/kover/html/index.html`
- **Scope**: MVI ViewModels (reducers, intents, effects, Flow pipelines)
- **Requirements**:
  - Write reducer tests that assert `UiState` transitions for every branch
  - Use Turbine to verify `state` and `effects` emissions after `dispatchIntent`
  - Mock use cases via Koin to keep tests deterministic
  - Assert that `UiEffect` emissions occur only once per intent

**iOS**:
- **Location**: `/iosApp/iosAppTests/ViewModels/`
- **Framework**: XCTest with Swift Concurrency (async/await)
- **Run command**: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- **Report**: Xcode coverage report
- **Scope**: ViewModels (ObservableObject), state management with async/await

**Web**:
- **Location**: `/webApp/src/__tests__/hooks/`
- **Framework**: Vitest + React Testing Library
- **Run command**: `npm test -- --coverage`
- **Report**: `webApp/coverage/index.html`
- **Scope**: Custom hooks, state management, view logic

### Unit Tests - Backend Business Logic (MANDATORY)

**Backend Unit Tests**:
- **Location**:
  - `/server/src/services/__test__/` - Business logic unit tests
  - `/server/src/lib/__test__/` - Utility function unit tests
- **Target**: 80% line + branch coverage on business logic
- **Framework**: Vitest
- **Run command**: `npm test` (from `/server` directory)
- **Run with coverage**: `npm test -- --coverage`
- **Report**: `server/coverage/index.html`
- **Scope**: Business logic (`/src/services`), utility functions (`/src/lib`)
- **TDD Requirement**: MUST write failing tests BEFORE implementing functions (Red-Green-Refactor)

**Unit Test Requirements**:
- MUST follow Given-When-Then structure
- MUST use descriptive test names (e.g., `'should return all pets when repository has data'`)
- MUST test happy path, error cases, and edge cases
- MUST use fake repositories (test doubles) instead of real database
- MUST NOT mock framework internals (test behavior, not implementation)
- Example:
  ```typescript
  // /src/services/__test__/petService.test.ts
  describe('getAllPets', () => {
      it('should return all pets when repository has data', async () => {
          // Given - repository with test data
          const mockPets = [
              { id: 1, name: 'Max', species: 'dog' },
              { id: 2, name: 'Luna', species: 'cat' }
          ];
          const fakeRepository = new FakePetRepository(mockPets);

          // When - service is called
          const result = await getAllPets(fakeRepository);

          // Then - all pets are returned
          expect(result).toHaveLength(2);
          expect(result[0].name).toBe('Max');
      });

      it('should return empty array when no pets exist', async () => {
          // Given - empty repository
          const fakeRepository = new FakePetRepository([]);

          // When
          const result = await getAllPets(fakeRepository);

          // Then
          expect(result).toHaveLength(0);
      });
  });
  ```

### Integration Tests - Backend API (MANDATORY)

**Backend API Integration Tests**:
- **Location**: `/server/src/__test__/`
- **Target**: 80% line + branch coverage on API routes and business logic combined
- **Framework**: Vitest + SuperTest
- **Run command**: `npm test` (from `/server` directory)
- **Run with coverage**: `npm test -- --coverage`
- **Report**: `server/coverage/index.html`
- **Scope**: REST API endpoints (end-to-end request/response testing)
- **TDD Requirement**: MUST write failing tests BEFORE implementing endpoints

**API Integration Test Requirements**:
- MUST follow Given-When-Then structure
- MUST test all HTTP methods (GET, POST, PUT, DELETE)
- MUST test success scenarios (2xx responses)
- MUST test error scenarios (4xx, 5xx responses)
- MUST test request validation (invalid data, missing fields)
- MUST test authentication/authorization (when implemented)
- MUST test edge cases (empty results, duplicate data)
- MUST use test database (separate from development database)
- Example:
  ```typescript
  // /src/__test__/petRoutes.test.ts
  describe('GET /api/pets', () => {
      it('should return 200 and all pets when database has data', async () => {
          // Given - database seeded with test pets
          await seedDatabase([
              { id: 1, name: 'Max', species: 'dog' },
              { id: 2, name: 'Luna', species: 'cat' }
          ]);

          // When - client requests all pets
          const response = await request(app)
              .get('/api/pets')
              .expect('Content-Type', /json/)
              .expect(200);

          // Then - response contains all pets
          expect(response.body).toHaveLength(2);
          expect(response.body[0].name).toBe('Max');
      });

      it('should return 404 when pet not found', async () => {
          // Given - empty database
          await clearDatabase();

          // When - client requests non-existent pet
          const response = await request(app)
              .get('/api/pets/999')
              .expect(404);

          // Then - error message returned
          expect(response.body.error).toBe('Pet not found');
      });
  });

  describe('POST /api/pets', () => {
      it('should return 400 when name is missing', async () => {
          // Given - invalid pet data (missing name)
          const invalidPet = { species: 'dog', ownerId: 1 };

          // When - client attempts to create pet
          const response = await request(app)
              .post('/api/pets')
              .send(invalidPet)
              .expect(400);

          // Then - validation error returned
          expect(response.body.error).toContain('name');
      });
  });
  ```

### End-to-End Tests (MANDATORY)

**Web E2E Tests**:
- **Location**: `/e2e-tests/web/specs/`
- **Framework**: Playwright + TypeScript
- **Config**: `playwright.config.ts` (repo root)
- **Run command**: `npx playwright test`
- **Run with UI**: `npx playwright test --ui`
- **Report**: `playwright-report/index.html`
- **Requirements**:
  - Page Object Model in `/e2e-tests/web/pages/`
  - Test data fixtures in `/e2e-tests/web/fixtures/`
  - One spec file per feature
  - All user stories from spec.md covered

**Mobile E2E Tests**:
- **Location**: `/e2e-tests/mobile/specs/`
- **Framework**: Appium + WebdriverIO + TypeScript
- **Config**: `wdio.conf.ts` (repo root with platform-specific overrides)
- **Run commands**:
  - Android: `npm run test:mobile:android`
  - iOS: `npm run test:mobile:ios`
- **Report**: `e2e-tests/mobile/reports/`
- **Requirements**:
  - Screen Object Model in `/e2e-tests/mobile/screens/`
  - Shared test utilities in `/e2e-tests/mobile/utils/`
  - One spec file per feature (covers both platforms)
  - Platform-specific conditionals when needed
  - All user stories from spec.md covered

### Integration Tests (OPTIONAL)

Platform-specific integration tests MAY be added for complex scenarios:
- Android: `/composeApp/src/androidTest/` (Compose UI tests)
- iOS: XCUITest in `iosApp.xcodeproj`
- Web: Integration tests in `/webApp/src/__tests__/integration/`

Integration tests do NOT count toward 80% coverage requirement but are encouraged
for testing component interactions.

### Contract Tests (RECOMMENDED)

When shared module defines repository interfaces, platform implementations SHOULD
include contract tests verifying they satisfy interface contracts.

## Governance

This constitution supersedes all other architectural guidelines.

### Amendment Process

1. Propose amendment with rationale and migration plan
2. Review impact on existing features
3. Update constitution version (see semantic versioning below)
4. Update all affected templates and documentation
5. Communicate changes to all contributors

### Versioning

- **MAJOR**: Breaking changes to core principles (e.g., allowing UI in shared)
- **MINOR**: New principle added or significant clarification
- **PATCH**: Typo fixes, wording improvements, non-semantic changes

### Compliance

All pull requests MUST:
- Verify shared module contains no UI/ViewModel code
- Run `./gradlew :shared:test koverHtmlReport` and ensure 80%+ coverage on shared
- Run platform-specific ViewModel tests and ensure 80%+ coverage:
  - Android: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
  - iOS: XCTest with coverage enabled
  - Web: `npm test -- --coverage`
- Run backend tests and ensure 80%+ coverage:
  - Backend unit tests: `npm test -- --coverage` (from `/server` directory)
  - Verify coverage for `/src/services` and `/src/lib`
  - Verify coverage for `/src/__test__/` (API integration tests)
- Backend code quality checks:
  - Run ESLint: `npm run lint` (from `/server` directory)
  - Verify Clean Code principles (small functions, descriptive names, max 3 nesting levels, DRY)
  - Verify dependency minimization (document rationale for each dependency)
  - Verify TDD workflow followed (tests written before implementation)
- Run E2E tests for affected features:
  - Web: `npx playwright test`
  - Mobile: `npm run test:mobile:android` and `npm run test:mobile:ios`
- Verify all new interactive UI elements have test identifiers:
  - Android: `testTag` modifier
  - iOS: `accessibilityIdentifier` modifier
  - Web: `data-testid` attribute
- Verify all new public APIs have concise, high-level documentation:
  - Kotlin: KDoc format
  - Swift: SwiftDoc format
  - TypeScript: JSDoc format (including backend API endpoints and business logic)
  - Focus on WHAT/WHY, not HOW (1-3 sentences)
- Verify Android Compose screens follow MVI architecture:
  - Single `StateFlow<UiState>` source of truth with immutable data classes
  - Sealed `UserIntent` and optional `UiEffect` types co-located with the feature
  - `dispatchIntent` entry point wired from UI actions
  - Reducers implemented as pure functions with exhaustive `when` handling
  - Effects delivered via `SharedFlow`/`Channel` and handled in Compose through `LaunchedEffect`
- Verify all new tests follow Given-When-Then structure:
  - Clear separation of setup (Given), action (When), verification (Then)
  - Descriptive test names following platform conventions
  - Comments marking test phases in complex tests
  - Backend tests MUST use Given-When-Then for both unit and integration tests
- Check that platform-specific code resides in correct modules
- Use expect/actual for platform dependencies in shared
- Verify backend `/server` remains independent (not part of KMP)
- Verify backend directory structure follows standards:
  - Business logic in `/src/services` (with unit tests in `__test__/`)
  - Utilities in `/src/lib` (with unit tests in `__test__/`)
  - API integration tests in `/src/__test__/`
  - Middlewares in `/src/middlewares`
  - Routes (thin) in `/src/routes`
  - Database layer in `/src/database`

Violations MUST be rejected unless explicitly justified in PR description
with temporary exception approval.

### Living Documentation

This constitution guides runtime development. For command-specific workflows,
see `.claude/commands/speckit.*.md` files.

**Version**: 1.9.0 | **Ratified**: 2025-11-14 | **Last Amended**: 2025-11-18
