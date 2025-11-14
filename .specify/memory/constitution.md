# PetSpot KMP Constitution

<!--
Sync Impact Report:
Version change: 1.4.0 → 1.5.0
Modified principles:
- (v1.4.0) X. Test Identifiers for UI Controls → Mandate testIDs for E2E testing
Added sections:
- XI. Public API Documentation (v1.5.0 - NEW principle)
Rationale: Mandate concise, high-level documentation for all public APIs (classes, methods, 
properties) using platform-native doc formats (KDoc, SwiftDoc, JSDoc). Focus on WHY and WHAT, 
not HOW (implementation details belong in code). Improves API discoverability, IDE assistance, 
and onboarding for new developers.
Templates requiring updates:
- ✅ AGENTS.md (Added documentation requirements)
- ✅ plan-template.md (Added Documentation Standards gate to Constitution Check)
- ✅ tasks-template.md (Added documentation tasks to implementation phases)
Follow-up TODOs: None
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
- **Android**: Jetpack Compose + ViewModel (in `/composeApp`)
- **iOS**: SwiftUI + Swift ViewModels (in `/iosApp`)
- **Web**: React + TypeScript state management (in `/webApp`)

ViewModels and UI state MUST reside in platform-specific modules, NOT in `/shared`.

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

## Platform Architecture Rules

### Dependency Flow

```
┌─────────────────────────────────────────┐
│  Platform Modules (composeApp, iosApp, webApp)  │
│  - ViewModels                           │
│  - UI Components                        │
│  - Navigation                           │
│  - Platform-specific repository impls   │
└──────────────┬──────────────────────────┘
               │ depends on
               ▼
┌─────────────────────────────────────────┐
│  /shared (commonMain)                   │
│  - Domain Models                        │
│  - Repository Interfaces                │
│  - Use Cases / Business Logic           │
│  - NO ViewModels, NO UI                 │
└─────────────────────────────────────────┘
```

Platform modules MAY depend on `/shared`.
`/shared` MUST NOT depend on platform modules.

### Module Structure

**`/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/`**
- `domain/models/` - Data classes, entities
- `domain/repositories/` - Repository interfaces (NOT implementations)
- `domain/usecases/` - Business logic use cases (concrete or interfaces)
- `di/` - Koin modules for domain dependencies
- `utils/` - Pure functions, no platform deps

**`/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/`**
- `ui/` - Composable functions
- `viewmodels/` - Android ViewModels
- `data/` - Android repository implementations
- `di/` - Koin modules for Android (data + ViewModel modules)
- `navigation/` - Compose Navigation

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
        val result = useCase()
        assertTrue(result.isSuccess)
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
- **Scope**: ViewModels, UI state management

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
  - TypeScript: JSDoc format
  - Focus on WHAT/WHY, not HOW (1-3 sentences)
- Check that platform-specific code resides in correct modules
- Use expect/actual for platform dependencies in shared

Violations MUST be rejected unless explicitly justified in PR description 
with temporary exception approval.

### Living Documentation

This constitution guides runtime development. For command-specific workflows, 
see `.claude/commands/speckit.*.md` files.

**Version**: 1.5.0 | **Ratified**: 2025-11-14 | **Last Amended**: 2025-11-14
