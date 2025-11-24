# PetSpot Project Constitution

<!--
Sync Impact Report:
Version change: 2.1.0 → 2.2.0
MINOR: Added Android Composable screen separation pattern for previews and testing

Changes (v2.2.0):
- UPDATED: Principle X "Android Model-View-Intent Architecture" - added mandatory Composable screen separation pattern
- Added requirements for state host + stateless composable pattern
- Added preview requirements with PreviewParameterProvider
- UPDATED: .specify/templates/plan-template.md - added Composable pattern compliance check
- UPDATED: .specify/templates/tasks-template.md - added preview creation tasks

Modified principles:
- X. Android Model-View-Intent Architecture (UPDATED - added Composable screen pattern requirement)

Templates requiring updates:
- ✅ .specify/templates/plan-template.md - Added Composable screen pattern check
- ✅ .specify/templates/tasks-template.md - Added preview tasks
- ✅ .specify/templates/spec-template.md (no changes needed - platform-agnostic)

Follow-up TODOs: None

Previous changes (v2.1.0):
MINOR: Added Android Navigation Component architectural requirement

Changes (v2.1.0):
- UPDATED: Principle X "Android Model-View-Intent Architecture" - added mandatory Jetpack Navigation Component requirement
- UPDATED: Module Structure - clarified that Android navigation MUST use Jetpack Navigation Component
- UPDATED: .specify/templates/plan-template.md - added Android Navigation Component compliance check
- UPDATED: .specify/templates/tasks-template.md - clarified navigation setup requirements

Modified principles:
- X. Android Model-View-Intent Architecture (UPDATED - added Navigation Component requirement)

Templates requiring updates:
- ✅ .specify/templates/plan-template.md - Added Android Navigation Component check
- ✅ .specify/templates/tasks-template.md - Updated navigation setup guidance
- ✅ .specify/templates/spec-template.md (no changes needed - platform-agnostic)

Follow-up TODOs: None

Previous changes (v2.0.1):
PATCH: Clarified dependency injection requirements and iOS architecture patterns

Clarifications (v2.0.1):
- Android MUST use Koin for DI (removed Hilt and manual DI options)
- iOS MUST use manual DI (removed Swinject option)
- iOS architecture: ViewModels call repositories directly (NO use cases layer)
- Updated all templates to reflect these requirements

Previous changes (v2.0.0):
BREAKING CHANGE: Migration from Kotlin Multiplatform (KMP) to platform-independent architecture

Major Changes (v2.0.0):
- REMOVED: Principle I "Thin Shared Layer" - no longer using shared Kotlin Multiplatform module
- REMOVED: Principle IV "Platform Independence via Expect/Actual" - no shared module to require platform abstraction
- REMOVED: `/shared` module references from Module Structure, Testing Standards, and Architecture diagrams
- REMOVED: Shared module testing requirements from 80% Test Coverage principle
- REMOVED: Koin dependency injection requirement (each platform chooses own DI solution)
- REPLACED: Principle II "Native Presentation" → Principle I "Platform Independence" (each platform implements full stack independently)
- UPDATED: Principle VII "Interface-Based Design" - repository interfaces now platform-specific
- UPDATED: Principle VIII renamed to "Dependency Injection" (removed Koin requirement, each platform chooses own solution)
- UPDATED: Architecture diagrams to show three independent platforms + backend
- UPDATED: Module Structure to reflect platform-specific domain logic
- UPDATED: Testing Standards to remove shared module tests
- UPDATED: Compliance checklist to remove shared module requirements

Modified principles:
- I. Platform Independence (NEW - replaces "Thin Shared Layer" and "Native Presentation")
- II. 80% Unit Test Coverage (UPDATED - removed shared module requirements)
- III. Interface-Based Design (UPDATED - platform-specific repositories)
- IV. Dependency Injection (UPDATED - removed Koin requirement, each platform chooses own DI)
- V. Asynchronous Programming Standards (RENUMBERED, no content changes)
- VI. Test Identifiers for UI Controls (RENUMBERED, no content changes)
- VII. Public API Documentation (RENUMBERED, no content changes)
- VIII. Given-When-Then Test Convention (RENUMBERED, no content changes)
- IX. Backend Architecture & Quality Standards (RENUMBERED, no content changes)
- X. Android Model-View-Intent Architecture (RENUMBERED, no content changes)
- XI. iOS Model-View-ViewModel-Coordinator Architecture (RENUMBERED, no content changes)
- XII. End-to-End Testing (RENUMBERED, moved from VI to XII for better organization)

Added principles: None (consolidated existing principles)
Removed principles:
- Thin Shared Layer (obsolete - no shared module)
- Platform Independence via Expect/Actual (obsolete - no shared module)
- Explicit Contracts & APIs (merged into Interface-Based Design)

Templates requiring updates:
- ⚠ .specify/templates/plan-template.md - Remove KMP Architecture Compliance checks, update to Platform Independence checks
- ⚠ .specify/templates/tasks-template.md - Remove shared module task sections, update to platform-specific tasks
- ✅ .specify/templates/spec-template.md (no changes needed - platform-agnostic)

Follow-up TODOs:
- Migrate existing /shared module code to platform-specific modules (Android, iOS, Web)
- Update build scripts to remove shared module dependencies
- Update CI/CD pipeline to remove shared module build steps
- Establish platform-specific domain models and synchronization strategy (if needed)
- Document platform-specific architectural patterns for Android, iOS, and Web
- Update developer onboarding documentation to reflect new architecture
-->

## Core Principles

### I. Platform Independence (NON-NEGOTIABLE)

Each platform MUST implement its full technology stack independently:

**Android** (`/composeApp`):
- Domain models (Kotlin data classes)
- Repository implementations (Kotlin)
- Use cases / business logic (Kotlin)
- ViewModels with MVI architecture (Kotlin + Jetpack Compose)
- UI layer (Jetpack Compose)
- Own dependency injection setup (Koin - mandatory)

**iOS** (`/iosApp`):
- Domain models (Swift structs/classes)
- Repository implementations (Swift)
- ViewModels with MVVM-C architecture (Swift + SwiftUI) - call repositories directly (NO use cases)
- UI layer (SwiftUI)
- Own dependency injection setup (manual DI with constructor injection)

**Web** (`/webApp`):
- Domain models (TypeScript interfaces/types)
- Service layer / business logic (TypeScript)
- State management (React hooks, Context, or state libraries)
- UI layer (React + TypeScript)
- Own dependency injection setup (native TypeScript patterns, Context, or DI libraries)

**Backend** (`/server`):
- REST API implementation (Node.js + Express + TypeScript)
- Business logic (TypeScript services)
- Database layer (Knex + SQLite/PostgreSQL)
- NOT consumed by platform code directly (only via HTTP)

**Architecture Rules**:
- Platforms MUST NOT share compiled code (no Kotlin Multiplatform, no shared libraries)
- Platforms MAY share design patterns and architectural conventions
- Platforms MUST consume backend APIs via HTTP/REST (common integration point)
- Each platform MUST be independently buildable, testable, and deployable
- Domain models MAY differ between platforms based on platform-specific needs
- Business logic MUST be implemented per platform (no shared use cases)

**Rationale**: Platform independence maximizes flexibility, allows each platform to leverage
native frameworks and idioms without compromise, eliminates cross-platform build complexity,
and enables independent team scaling. Each platform can evolve at its own pace without
coordinating breaking changes across multiple codebases.

### II. 80% Unit Test Coverage (NON-NEGOTIABLE)

Each platform MUST maintain minimum 80% unit test coverage for business logic and presentation logic:

**Android** (`/composeApp/src/androidUnitTest/`):
- Framework: JUnit 6 + Kotlin Test + Turbine (for testing Kotlin Flow)
- Run command: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- Report: `composeApp/build/reports/kover/html/index.html`
- Scope: Domain models, use cases, ViewModels (MVI architecture)
- Coverage target: 80% line + branch coverage

**iOS** (`/iosApp/iosAppTests/`):
- Framework: XCTest with Swift Concurrency (async/await)
- Run command: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- Report: Xcode coverage report
- Scope: Domain models, use cases, ViewModels (ObservableObject with @Published properties)
- Coverage target: 80% line + branch coverage

**Web** (`/webApp/src/__tests__/`):
- Framework: Vitest + React Testing Library
- Run command: `npm test -- --coverage` (from webApp/)
- Report: `webApp/coverage/index.html`
- Scope: Domain models, services, custom hooks, state management
- Coverage target: 80% line + branch coverage

**Backend** (`/server/src/`):
- Framework: Vitest (unit) + SuperTest (integration)
- Run commands:
  - Unit tests: `npm test` (from server/)
  - With coverage: `npm test -- --coverage`
- Report: `server/coverage/index.html`
- Scope: Business logic (`/src/services`), utility functions (`/src/lib`), REST API endpoints (`/src/__test__/`)
- Coverage target: 80% line + branch coverage for both unit and integration tests

**Testing Requirements**:
- MUST test happy path, error cases, and edge cases
- MUST follow Given-When-Then structure (see Principle VIII)
- MUST use descriptive test names following platform conventions
- MUST test behavior, not implementation details
- MUST use test doubles (fakes, mocks) for dependencies

**Rationale**: High test coverage on both business logic AND presentation logic ensures correctness
across all platforms. Each platform's critical logic (domain, use cases, ViewModels) must be verified
independently to prevent regressions and enable confident refactoring.

### III. Interface-Based Design (NON-NEGOTIABLE)

All domain logic classes MUST follow interface-based design within each platform:

**Repository Pattern** (per platform):

Each platform defines and implements its own repository interfaces:

**Android Example** (`/composeApp/src/androidMain/.../repositories/`):
```kotlin
// Interface definition
interface PetRepository {
    suspend fun getPets(): Result<List<Pet>>
    suspend fun getPetById(id: String): Result<Pet>
}

// Implementation
class PetRepositoryImpl(private val api: PetApi) : PetRepository {
    override suspend fun getPets(): Result<List<Pet>> = /* implementation */
}
```

**iOS Example** (`/iosApp/iosApp/Repositories/`):
```swift
// Protocol definition
protocol PetRepository {
    func getPets() async throws -> [Pet]
    func getPetById(id: String) async throws -> Pet
}

// Implementation
class PetRepositoryImpl: PetRepository {
    func getPets() async throws -> [Pet] { /* implementation */ }
    func getPetById(id: String) async throws -> Pet { /* implementation */ }
}
```

**Web Example** (`/webApp/src/services/`):
```typescript
// Interface definition
interface PetService {
    getPets(): Promise<Pet[]>;
    getPetById(id: string): Promise<Pet>;
}

// Implementation
class PetServiceImpl implements PetService {
    async getPets(): Promise<Pet[]> { /* implementation */ }
    async getPetById(id: string): Promise<Pet> { /* implementation */ }
}
```

**Backend Example** (`/server/src/database/repositories/`):
```typescript
// Interface definition
interface IPetRepository {
    findAll(filter?: PetFilter): Promise<Pet[]>;
    findById(id: number): Promise<Pet | null>;
}

// Implementation
class PetRepository implements IPetRepository {
    constructor(private db: Knex) {}
    async findAll(filter?: PetFilter): Promise<Pet[]> { /* implementation */ }
    async findById(id: number): Promise<Pet | null> { /* implementation */ }
}
```

**Use Case Pattern** (optional, per platform):

Use cases MAY use interfaces when multiple implementations exist, or be concrete classes for single implementations.

**Benefits for Testing**:
- Interfaces enable test doubles (fakes, mocks) without mocking frameworks
- Improves testability and maintains 80% coverage requirement
- Clear contracts between layers within each platform

**Rationale**: Interface-based design enables dependency inversion, improves testability
through test doubles, and provides clear boundaries between domain and infrastructure concerns.
Each platform maintains its own interfaces and implementations, optimized for platform-specific needs.

### IV. Dependency Injection (NON-NEGOTIABLE)

Each platform MUST use dependency injection to manage dependencies:

**Platform-Specific DI Requirements**:

**Android** (MUST use Koin):
- MUST use Koin for dependency injection
- Koin provides lightweight, Kotlin-native DI with minimal boilerplate
- Rationale: Consistency across Android codebase, excellent Kotlin DSL, mature ecosystem

Example with Koin (`/composeApp/src/androidMain/.../di/`):
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

**iOS** (MUST use manual DI):
- MUST use manual dependency injection (constructor/initializer injection)
- NO DI frameworks (no Swinject, no third-party DI libraries)
- Rationale: Simplicity, no external dependencies, explicit dependency graph, Swift-native patterns

Example with manual DI (`/iosApp/iosApp/DI/`):
```swift
class ServiceContainer {
    static let shared = ServiceContainer()
    
    lazy var petRepository: PetRepository = PetRepositoryImpl(
        httpClient: httpClient
    )
    
    lazy var httpClient: HTTPClient = HTTPClientImpl()
}

// Usage in coordinator
let viewModel = PetListViewModel(
    repository: ServiceContainer.shared.petRepository
)
```

**Web** (recommended: React Context):
- SHOULD use React Context for dependency injection
- MAY use native TypeScript patterns (factory functions, service locator)
- MAY use DI libraries (InversifyJS, TSyringe) if team prefers
- Rationale: Flexibility for web ecosystem, React Context is idiomatic for React apps

Example with React Context (`/webApp/src/di/`):
```typescript
export const ServiceContext = React.createContext<Services | null>(null);

export function ServiceProvider({ children }: { children: React.ReactNode }) {
    const services = {
        petService: new PetServiceImpl(httpClient),
        httpClient: new HttpClient()
    };
    
    return (
        <ServiceContext.Provider value={services}>
            {children}
        </ServiceContext.Provider>
    );
}

// Usage in component
export function usePetService() {
    const services = React.useContext(ServiceContext);
    return services.petService;
}
```

**Backend**:
- Manual dependency injection (constructor injection)
- NO DI framework required for backend (simplicity preferred)

Example (`/server/src/`):
```typescript
// services/petService.ts
export function createPetService(repository: IPetRepository): PetService {
    return {
        async getAllPets(filter?: PetFilter): Promise<Pet[]> {
            return repository.findAll(filter);
        }
    };
}

// app.ts - wire dependencies
const db = setupDatabase();
const petRepository = new PetRepository(db);
const petService = createPetService(petRepository);
```

**DI Requirements Summary**:
- Android: MUST use Koin (non-negotiable)
- iOS: MUST use manual DI (non-negotiable)
- Web: SHOULD use React Context (recommended, but flexible)
- Backend: MUST use manual DI (constructor injection, factory functions)
- MUST enable easy swapping of implementations for testing
- MUST keep DI configuration simple and explicit

**Rationale**: Standardized DI approach per platform improves consistency, reduces onboarding
time, and ensures testability. Android uses Koin for its Kotlin-native DSL and ecosystem maturity.
iOS uses manual DI for simplicity and zero external dependencies. Web and backend remain flexible
based on team preferences and ecosystem best practices.

### V. Asynchronous Programming Standards (NON-NEGOTIABLE)

All asynchronous operations MUST follow platform-specific async patterns:

**Android** (Kotlin Coroutines):
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

**iOS** (Swift Concurrency):
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

          let result = await getPetsUseCase.execute()
          if let pets = result.success {
              self.pets = pets
          }
      }
  }
  ```

**Web** (Native async/await):
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
              const result = await petService.getPets();
              setPets(result);
          } finally {
              setIsLoading(false);
          }
      };

      return { pets, isLoading, loadPets };
  };
  ```

**Backend** (Node.js async/await):
- MUST use native **async/await**
- Express handlers MUST be async functions
- Example:
  ```typescript
  router.get('/pets', async (req, res, next) => {
      try {
          const pets = await petService.getAllPets();
          res.status(200).json(pets);
      } catch (error) {
          next(error); // Delegate to error middleware
      }
  });
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

### VI. Test Identifiers for UI Controls (NON-NEGOTIABLE)

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

### VII. Public API Documentation (NON-NEGOTIABLE)

Public APIs MUST have concise, high-level documentation when the purpose is not immediately clear from naming:

**Documentation Requirements**:
- MUST document public APIs ONLY when the name alone is insufficient to convey purpose or usage
- MUST skip documentation for self-explanatory names:
  - Methods: `getPets()`, `savePet(pet)`, `deletePet(id)` - names clearly indicate behavior
  - Variables: `isLoading`, `errorMessage`, `petList` - purpose is obvious
  - Constants: `MAX_RETRIES`, `DEFAULT_TIMEOUT`, `API_BASE_URL` - intent is clear
- MUST use platform-native documentation format when documentation is needed:
  - **Kotlin**: KDoc (`/** ... */`)
  - **Swift**: SwiftDoc (`/// ...` or `/** ... */`)
  - **TypeScript/JavaScript**: JSDoc (`/** ... */`)
- MUST be **concise and high-level** (focus on WHAT and WHY, not HOW)
- MUST NOT duplicate implementation details visible in code
- SHOULD be one to three sentences maximum
- SHOULD answer: "What does this do?" and "When/why would I use it?"
- MUST NOT state the obvious (e.g., "Returns a string" for `fun getString(): String`)

**Documentation Style** - GOOD Examples:

```kotlin
// Kotlin (Android)

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
    // ✅ NO DOCUMENTATION - Method name is self-explanatory
    suspend fun getPets(): Result<List<Pet>>
    
    // ✅ NO DOCUMENTATION - Obvious what this does
    suspend fun getPetById(id: String): Result<Pet>
    
    // ✅ DOCUMENT - Non-obvious caching behavior
    /** Saves pet and invalidates related caches. */
    suspend fun savePet(pet: Pet): Result<Unit>
}
```

```swift
// Swift (iOS)

// ✅ DOCUMENT - Class role needs context
/// Manages pet list state with automatic repository synchronization.
@MainActor
class PetListViewModel: ObservableObject {
    // ✅ NO DOCUMENTATION - Property names are clear
    @Published var pets: [Pet] = []
    @Published var isLoading = false
    @Published var errorMessage: String?

    // ✅ NO DOCUMENTATION - Method name is self-explanatory
    func loadPets() async {
        // implementation
    }
    
    // ✅ DOCUMENT - Complex retry behavior
    /// Retries failed pet sync with exponential backoff.
    func retrySync() async {
        // implementation
    }
}
```

```typescript
// TypeScript (Web)

// ✅ DOCUMENT - Hook manages complex state
/**
 * Manages pet list state with automatic refresh and error recovery.
 */
export function usePets() {
    // implementation
}

// ✅ NO DOCUMENTATION - Function name clearly explains behavior
export async function fetchPets(): Promise<Pet[]> {
    // implementation
}

// ✅ NO DOCUMENTATION - Constants are self-explanatory
export const MAX_RETRIES = 3;
export const DEFAULT_TIMEOUT = 5000;
export const API_BASE_URL = 'https://api.petspot.com';
```

**Documentation Decision Checklist**:
- [ ] Is the purpose unclear from the name alone? → Document
- [ ] Does it have non-obvious side effects? → Document
- [ ] Does it use complex algorithms or business rules? → Document
- [ ] Are parameters or return values ambiguous? → Document
- [ ] Does it throw exceptions? → Document exceptions only
- [ ] Is it a simple CRUD operation with obvious name? → Skip
- [ ] Is it a self-explanatory variable or constant? → Skip
- [ ] Would documentation just repeat the name? → Skip

**Rationale**: Selective, purposeful documentation improves code maintainability by reducing noise
and focusing attention on truly complex or non-obvious APIs. Self-explanatory names eliminate the
need for redundant comments, while strategic documentation highlights important behavior, edge cases,
and usage patterns that aren't immediately apparent. This approach accelerates onboarding by making
critical information stand out rather than being buried in obvious statements. Platform-native formats
ensure meaningful documentation appears in IDE tooltips.

### VIII. Given-When-Then Test Convention (NON-NEGOTIABLE)

All unit tests and E2E tests MUST follow the Given-When-Then (Arrange-Act-Assert) structure:

**Test Structure Requirements**:
- MUST clearly separate test phases: setup (Given), action (When), verification (Then)
- MUST use descriptive test names explaining the scenario being tested
- MUST test one behavior per test case
- SHOULD use backtick test names for readability (Kotlin) or descriptive strings (other platforms)

**Kotlin Tests** (Android):
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

**Swift Tests** (iOS):
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
```

**TypeScript Tests** (Web):
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
});
```

**Backend Tests** (Node.js/TypeScript):
```typescript
describe('petService', () => {
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
});
```

**Parameterized Tests** (Backend, Web, Android):

When tests share the same logic with different input/output pairs, SHOULD use parameterized tests:

```typescript
// TypeScript/Backend/Web (Vitest)
describe('createPet', () => {
    it.each([
        ['Max', 'dog'],
        ['Luna', 'cat'],
        ['Buddy', 'dog']
    ])('should create pet with name=%s and species=%s', async (name, species) => {
        // Given
        const pet = { name, species, ownerId: 1 };
        
        // When
        const result = await createPet(pet);
        
        // Then
        expect(result.name).toBe(name);
        expect(result.species).toBe(species);
    });
});
```

**Rationale**: Given-When-Then structure standardizes test organization across platforms,
making tests self-documenting and easier to understand. This pattern maps naturally to
user scenarios (acceptance criteria), E2E tests, and unit tests. Consistent test structure
reduces cognitive load when context-switching between platforms and improves onboarding
for new developers.

### IX. Backend Architecture & Quality Standards (NON-NEGOTIABLE)

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
  - MUST be testable in isolation
  - MUST NOT contain business logic
- **`/src/routes`**: Endpoint definitions and request routing ONLY
  - MUST be thin - delegate to services for business logic
  - MUST handle HTTP concerns (status codes, request/response mapping)
- **`/src/services`**: Business logic layer (pure functions, testable in isolation)
  - MUST contain all business rules, validation, calculations
  - MUST be framework-agnostic (no Express-specific code)
  - MUST be covered by unit tests in `/src/services/__test__/`
- **`/src/database`**: Database access layer (Knex queries, migrations, repositories)
  - MUST use Knex query builder (no raw SQL strings except complex queries)
  - MUST separate repository interfaces from implementations
  - Migration files MUST be versioned and reversible
- **`/src/lib`**: Utility functions and helpers (pure, reusable, no side effects)
  - MUST be framework-agnostic
  - MUST be covered by unit tests in `/src/lib/__test__/`

**Test-Driven Development (TDD) Workflow**:

Backend development MUST follow TDD (Red-Green-Refactor):

1. **RED**: Write a failing test first
2. **GREEN**: Write minimal code to make test pass
3. **REFACTOR**: Improve code quality without changing behavior

**Testing Strategy**:

**Unit Tests** (Vitest) - MUST achieve 80% coverage:
- Location: `/src/services/__test__/`, `/src/lib/__test__/`
- Scope: Business logic and utilities in isolation
- Run command: `npm test -- --coverage` (from `/server`)

**Integration Tests** (Vitest + SuperTest) - MUST achieve 80% coverage:
- Location: `/src/__test__/`
- Scope: REST API endpoints end-to-end (request → response)
- Run command: `npm test` (from `/server`)

**Database Layer Standards**:
- MUST use singular table names (e.g., `pet`, `user`)
- MUST use `IF EXISTS` / `IF NOT EXISTS` in all DDL statements for idempotent migrations
- MUST NOT use database-level enum types or CHECK constraints (store enums as strings, validate at application layer)
- SHOULD design for easy migration from SQLite to PostgreSQL

For complete backend standards, testing examples, and error handling patterns, see full constitution sections (preserved from v1.12.0).

**Rationale**: Modern backend architecture requires clear separation of concerns to maintain
testability and scalability. TDD ensures business logic correctness before integration.
Clean Code principles and minimal dependencies reduce maintenance burden and security surface.

### X. Android Model-View-Intent Architecture (NON-NEGOTIABLE)

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

**Navigation requirements**:
- Android navigation MUST use Jetpack Navigation Component (androidx.navigation:navigation-compose)
- Navigation graph MUST be defined declaratively using `NavHost` composable
- Deep links SHOULD be supported via Navigation Component's deep link mechanism
- ViewModels MUST NOT trigger navigation directly - use `UiEffect` for navigation events
- Navigation state MUST be managed by `NavController`, not application state

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

**Composable screen pattern** (NON-NEGOTIABLE):

All screen composables MUST follow a two-layer pattern for testability and preview support:

1. **State Host Composable** (lightweight, stateful):
   - Collects state from ViewModel (`collectAsStateWithLifecycle()`)
   - Observes effects using `LaunchedEffect`
   - Dispatches intents to ViewModel
   - Delegates rendering to stateless composable
   - Contains NO UI logic or layout code
   - Example name: `PetListScreen(viewModel: PetListViewModel)`

2. **Stateless Composable** (pure presentation):
   - Accepts `UiState` as immutable parameter
   - Accepts callback lambdas for user interactions (e.g., `onPetClick: (String) -> Unit`)
   - Contains ALL UI logic and layout code
   - NO ViewModel dependency
   - NO runtime dependencies (Koin, navigation, etc.)
   - Example name: `PetListContent(state: PetListUiState, onPetClick: (String) -> Unit)`

**Preview requirements** (MANDATORY):
- Stateless composable MUST have at least one `@Preview` function
- Preview data MUST be delivered via `@PreviewParameter` with custom `PreviewParameterProvider<UiState>`
- Callback lambdas MUST be defaulted to no-ops (e.g., `onPetClick: (String) -> Unit = {}`)
- Previews MUST focus on light mode only (no `@Preview(uiMode = UI_MODE_NIGHT_YES)` required)
- Preview functions MUST be co-located with stateless composable in same file
- `PreviewParameterProvider` MUST provide realistic sample data (loading, success, error states)

Example:
```kotlin
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
    // All UI layout code here
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

**Rationale**: Separating stateful orchestration from stateless presentation enables:
- **Previews without runtime dependencies**: Stateless composables can be previewed without Koin, ViewModel, or navigation setup
- **Faster preview rendering**: No dependency injection or lifecycle overhead
- **Easier testing**: Pure functions with predictable inputs/outputs
- **Design iteration**: UI changes can be previewed instantly with various states via `PreviewParameterProvider`
- **Consistent pattern**: Clear separation of concerns across all screens

Example MVI ViewModel:
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
```

**Rationale**: MVI enforces unidirectional data flow, simplifies reasoning about screen behavior,
and makes reducers easy to unit test. Immutable `UiState` snapshots prevent UI drift, while
explicit intents capture every interaction for analytics and debugging. Effect channels keep
navigation and transient events isolated from state, reducing Compose recompositions and bugs.

### XI. iOS Model-View-ViewModel-Coordinator Architecture (NON-NEGOTIABLE)

All iOS presentation features MUST follow the Model-View-ViewModel-Coordinator (MVVM-C) pattern to
maintain clear separation of concerns and enable testable, coordinator-driven navigation.

**Core components**:
- `Model`: Domain models defined in iOS platform code (Swift structs/classes)
- `View`: SwiftUI views that observe ViewModels via `@ObservedObject` or `@StateObject`
- `ViewModel`: `ObservableObject` classes containing presentation logic and `@Published` state properties
- `Coordinator`: UIKit-based objects managing navigation flow and creating `UIHostingController` instances

**Architecture rules**:
1. **Coordinators manage navigation**: All screen transitions, modal presentations, and flow logic
   reside in coordinator classes (UIKit-based). SwiftUI views MUST NOT directly trigger navigation.

2. **ViewModels own presentation state**: All UI-related state (loading flags, data, errors) lives
   in ViewModel `@Published` properties. Views observe and render based on these properties.

3. **ViewModel-Coordinator communication**: ViewModels communicate with coordinators via:
   - Direct method calls (e.g., `coordinator.showDetails(petId:)`)
   - Closure/callback properties set by coordinator during ViewModel initialization

4. **UIHostingController wrapping**: Coordinators create SwiftUI views and wrap them in
   `UIHostingController` for UIKit integration.

5. **SwiftUI views remain pure**: Views MUST NOT contain business logic, navigation logic, or
   direct use case calls. Views only render UI based on ViewModel state and trigger ViewModel methods.

6. **Coordinator hierarchy**: Parent coordinators manage child coordinators for nested flows.
   Child coordinators notify parents via delegation or closures when flow completes.

Example MVVM-C (iOS ViewModels call repositories directly - NO use cases):
```swift
// ViewModel
@MainActor
class PetListViewModel: ObservableObject {
    @Published var pets: [Pet] = []
    @Published var isLoading = false
    
    // Coordinator callback for navigation
    var onPetSelected: ((String) -> Void)?
    
    private let petRepository: PetRepository
    
    init(petRepository: PetRepository) {
        self.petRepository = petRepository
    }
    
    func loadPets() async {
        isLoading = true
        defer { isLoading = false }
        
        do {
            // iOS ViewModels call repositories directly (NO use cases)
            self.pets = try await petRepository.getPets()
        } catch {
            // Handle error
            print("Failed to load pets: \(error)")
        }
    }
    
    func selectPet(id: String) {
        onPetSelected?(id)  // Coordinator handles navigation
    }
}

// Coordinator with manual DI
class PetListCoordinator {
    private let navigationController: UINavigationController
    private let petRepository: PetRepository
    
    init(navigationController: UINavigationController, petRepository: PetRepository) {
        self.navigationController = navigationController
        self.petRepository = petRepository
    }
    
    func start() {
        // Manual DI - inject repository directly into ViewModel
        let viewModel = PetListViewModel(petRepository: petRepository)
        viewModel.onPetSelected = { [weak self] petId in
            self?.showPetDetails(petId: petId)
        }
        
        let view = PetListView(viewModel: viewModel)
        let hostingController = UIHostingController(rootView: view)
        navigationController.pushViewController(hostingController, animated: true)
    }
    
    private func showPetDetails(petId: String) {
        let detailCoordinator = PetDetailCoordinator(
            navigationController: navigationController,
            petRepository: petRepository  // Pass repository down
        )
        detailCoordinator.start()
    }
}
```

**Testing requirements**:
- ViewModels MUST have unit tests in `/iosApp/iosAppTests/ViewModels/`
- ViewModel tests MUST verify `@Published` property updates
- ViewModel tests MUST verify coordinator callback invocations

**Rationale**: MVVM-C separates navigation responsibility from presentation logic, making both
independently testable. Coordinators provide a centralized place for flow management and enable
complex navigation patterns. ViewModels remain portable and can be tested without UIKit dependencies.

### XII. End-to-End Testing (NON-NEGOTIABLE)

Every feature specification MUST include end-to-end tests covering all user scenarios:

**Web Platform** (`/e2e-tests/web/`):
- Framework: Playwright with TypeScript
- Config: `playwright.config.ts` at repo root
- Test location: `/e2e-tests/web/specs/[feature-name].spec.ts`
- Step definitions: `/e2e-tests/web/steps/` (reusable test steps)
- Run command: `npx playwright test`
- Coverage: All user stories from spec.md

**Mobile Platforms** (`/e2e-tests/mobile/`):
- Framework: Appium with TypeScript + WebdriverIO
- Config: `wdio.conf.ts` for Android/iOS
- Test location: `/e2e-tests/mobile/specs/[feature-name].spec.ts`
- Step definitions: `/e2e-tests/mobile/steps/` (reusable test steps)
- Run command: `npm run test:mobile:android` or `npm run test:mobile:ios`
- Coverage: All user stories from spec.md for both Android and iOS

**Requirements**:
- Tests MUST be written in TypeScript for consistency across platforms
- Each user story MUST have at least one E2E test
- Tests MUST run against real application builds (not mocked)
- Tests MUST be executable in CI/CD pipeline
- Page Object Model pattern REQUIRED for web maintainability
- Screen Object Model pattern REQUIRED for mobile maintainability
- Step definitions MUST be used for reusable test steps (Given/When/Then actions)

**Rationale**: E2E tests validate complete user flows across platforms, catching integration
issues that unit tests cannot detect. TypeScript provides type safety and enables sharing
test utilities across web and mobile test suites.

## Platform Architecture Rules

### Dependency Flow

```
┌─────────────────────────────────────────────────────────────┐
│  Android (composeApp) - INDEPENDENT PLATFORM                │
│  - Domain models (Kotlin)                                   │
│  - Use cases (Kotlin)                                       │
│  - Repository implementations (Kotlin)                      │
│  - ViewModels (MVI architecture)                            │
│  - UI (Jetpack Compose)                                     │
│  - DI (Koin, Hilt, or manual)                               │
│  - HTTP client consuming /server REST API                   │
└──────────────────────────────┬──────────────────────────────┘
                               │ HTTP requests
                               ▼
                    ┌────────────────────────┐
                    │  /server (Node.js)     │
                    │  - REST API endpoints  │
                    │  - Business logic      │
                    │  - Database (SQLite)   │
                    └────────────┬───────────┘
                                 │ HTTP requests
          ┌──────────────────────┴─────────────────────────┐
          │                                                 │
          ▼                                                 ▼
┌───────────────────────────────────┐   ┌──────────────────────────────────┐
│  iOS (iosApp) - INDEPENDENT       │   │  Web (webApp) - INDEPENDENT      │
│  - Domain models (Swift)          │   │  - Domain models (TypeScript)    │
│  - Use cases (Swift)              │   │  - Services (TypeScript)         │
│  - Repository implementations     │   │  - State management (React)      │
│  - ViewModels (MVVM-C)            │   │  - UI (React + TypeScript)       │
│  - UI (SwiftUI)                   │   │  - DI (Context, native patterns) │
│  - DI (native Swift DI, Swinject) │   │  - HTTP client consuming backend │
│  - HTTP client consuming backend  │   └──────────────────────────────────┘
└───────────────────────────────────┘
```

**Clarifications**:
- **Android, iOS, Web platforms** are fully independent with their own domain logic
- All platforms consume `/server` via REST API (HTTP requests)
- `/server` is a standalone Node.js backend (NOT part of any platform)
- NO shared compiled code between platforms
- Platforms MAY share design patterns and architectural conventions (MVI, MVVM-C, etc.)

### Module Structure

**`/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/`** (Android - Full Stack):
- `domain/models/` - Kotlin data classes, entities
- `domain/repositories/` - Repository interfaces
- `domain/usecases/` - Business logic use cases
- `data/` - Repository implementations (network, database)
- `features/<feature>/ui/` - Composable screens (collect StateFlow<UiState>, dispatch intents)
- `features/<feature>/presentation/mvi/` - UiState, UserIntent, UiEffect, reducers
- `features/<feature>/presentation/viewmodels/` - MviViewModel implementations
- `di/` - Dependency injection modules (Koin mandatory)
- `navigation/` - Jetpack Navigation Component graph (NavHost) and effect handlers

**`/iosApp/iosApp/`** (iOS - Full Stack):
- `Domain/Models/` - Swift structs/classes for domain entities
- `Domain/Repositories/` - Repository protocols
- `Data/Repositories/` - Repository implementations
- `Coordinators/` - UIKit-based coordinators managing navigation
- `Views/` - SwiftUI views (wrapped in UIHostingController)
- `ViewModels/` - Swift ObservableObject classes with @Published properties (call repositories directly)
- `DI/` - Manual dependency injection setup (ServiceContainer or similar)

**`/webApp/src/`** (Web - Full Stack):
- `models/` - TypeScript interfaces/types for domain models
- `services/` - HTTP services consuming backend REST API, business logic
- `components/` - React components
- `hooks/` - React hooks for state management
- `utils/` - Utility functions and helpers
- `di/` - Dependency injection setup (React Context, service locator, or DI libraries)

**`/server/src/`** (Backend - Node.js/Express):
- `middlewares/` - Express middlewares (auth, logging, error handling, validation)
- `routes/` - REST API route handlers (Express routers)
- `services/` - Business logic layer (testable, pure functions)
- `database/` - Database access layer (Knex queries, migrations, repositories)
- `lib/` - Utility functions, helpers (pure, reusable)
- `__test__/` - Integration tests for REST API endpoints (Vitest + SuperTest)
- `app.ts` - Express app configuration (middleware setup, route registration)
- `index.ts` - Server entry point (port binding, startup)

## Architecture Patterns

### Repository Pattern (MANDATORY per platform)

Each platform implements its own repository pattern:

**Android Example**:
```kotlin
// Interface in /composeApp/src/androidMain/.../domain/repositories/
interface PetRepository {
    suspend fun getPets(): Result<List<Pet>>
    suspend fun getPetById(id: String): Result<Pet>
}

// Implementation in /composeApp/src/androidMain/.../data/repositories/
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

**iOS Example**:
```swift
// Protocol in /iosApp/iosApp/Domain/Repositories/
protocol PetRepository {
    func getPets() async throws -> [Pet]
    func getPetById(id: String) async throws -> Pet
}

// Implementation in /iosApp/iosApp/Data/Repositories/
class PetRepositoryImpl: PetRepository {
    private let httpClient: HTTPClient
    
    init(httpClient: HTTPClient) {
        self.httpClient = httpClient
    }
    
    func getPets() async throws -> [Pet] {
        return try await httpClient.get("/api/pets")
    }
    
    func getPetById(id: String) async throws -> Pet {
        return try await httpClient.get("/api/pets/\(id)")
    }
}
```

**Web Example**:
```typescript
// Interface in /webApp/src/services/
interface PetService {
    getPets(): Promise<Pet[]>;
    getPetById(id: string): Promise<Pet>;
}

// Implementation in /webApp/src/services/
class PetServiceImpl implements PetService {
    constructor(private httpClient: HttpClient) {}
    
    async getPets(): Promise<Pet[]> {
        return this.httpClient.get<Pet[]>('/api/pets');
    }
    
    async getPetById(id: string): Promise<Pet> {
        return this.httpClient.get<Pet>(`/api/pets/${id}`);
    }
}
```

**Backend Example**:
```typescript
// Interface in /server/src/database/repositories/
interface IPetRepository {
    findAll(filter?: PetFilter): Promise<Pet[]>;
    findById(id: number): Promise<Pet | null>;
}

// Implementation in /server/src/database/repositories/
class PetRepository implements IPetRepository {
    constructor(private db: Knex) {}
    
    async findAll(filter?: PetFilter): Promise<Pet[]> {
        let query = this.db<Pet>('pet');
        
        if (filter?.species) {
            query = query.where('species', filter.species);
        }
        
        return query.select('*');
    }
    
    async findById(id: number): Promise<Pet | null> {
        return this.db<Pet>('pet').where('id', id).first();
    }
}
```

### Use Case Pattern (Platform-Specific)

Business logic encapsulation varies by platform:

**Android** (SHOULD use use cases):
- Use cases encapsulate business logic and orchestrate repository calls
- Provides clear separation between ViewModels and data layer
- Example:
```kotlin
class GetPetsUseCase(private val repository: PetRepository) {
    suspend operator fun invoke(): Result<List<Pet>> =
        repository.getPets()
}
```

**iOS** (NO use cases - ViewModels call repositories directly):
- ViewModels call repositories directly without use case layer
- Simplifies architecture and reduces boilerplate
- Business logic lives in ViewModels when needed
- Rationale: iOS architecture prioritizes simplicity; use cases add unnecessary indirection for typical CRUD operations
- Example:
```swift
// iOS ViewModel calls repository directly
@MainActor
class PetListViewModel: ObservableObject {
    private let repository: PetRepository
    
    init(repository: PetRepository) {
        self.repository = repository
    }
    
    func loadPets() async {
        do {
            self.pets = try await repository.getPets()
        } catch {
            // Handle error
        }
    }
}
```

**Web Example**:
```typescript
class GetPetsUseCase {
    constructor(private petService: PetService) {}
    
    async execute(): Promise<Pet[]> {
        return this.petService.getPets();
    }
}
```

## Testing Standards

### Unit Tests - Platform-Specific (MANDATORY)

Each platform MUST maintain 80% unit test coverage for domain logic and ViewModels:

**Android**:
- Location: `/composeApp/src/androidUnitTest/kotlin/`
- Framework: JUnit 6 + Kotlin Test + Turbine (Flow testing)
- Run command: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- Report: `composeApp/build/reports/kover/html/index.html`
- Scope: Domain models, use cases, ViewModels (MVI), reducers

**iOS**:
- Location: `/iosApp/iosAppTests/`
- Framework: XCTest with Swift Concurrency (async/await)
- Run command: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- Report: Xcode coverage report
- Scope: Domain models, use cases, ViewModels (ObservableObject), coordinators (optional)

**Web**:
- Location: `/webApp/src/__tests__/`
- Framework: Vitest + React Testing Library
- Run command: `npm test -- --coverage` (from webApp/)
- Report: `webApp/coverage/index.html`
- Scope: Domain models, services, custom hooks, state management

**Backend**:
- Location: `/server/src/services/__test__/`, `/server/src/lib/__test__/`, `/server/src/__test__/`
- Framework: Vitest (unit) + SuperTest (integration)
- Run command: `npm test -- --coverage` (from server/)
- Report: `server/coverage/index.html`
- Scope: Business logic, utilities, REST API endpoints

### End-to-End Tests (MANDATORY)

**Web E2E Tests**:
- Location: `/e2e-tests/web/specs/`
- Framework: Playwright + TypeScript
- Config: `playwright.config.ts` (repo root)
- Run command: `npx playwright test`
- Report: `playwright-report/index.html`
- Requirements:
  - Page Object Model in `/e2e-tests/web/pages/`
  - Step definitions in `/e2e-tests/web/steps/`
  - One spec file per feature
  - All user stories covered

**Mobile E2E Tests**:
- Location: `/e2e-tests/mobile/specs/`
- Framework: Appium + WebdriverIO + TypeScript
- Config: `wdio.conf.ts` (repo root)
- Run commands: `npm run test:mobile:android`, `npm run test:mobile:ios`
- Report: `e2e-tests/mobile/reports/`
- Requirements:
  - Screen Object Model in `/e2e-tests/mobile/screens/`
  - Step definitions in `/e2e-tests/mobile/steps/`
  - One spec file per feature (covers both platforms)
  - All user stories covered

## Governance

This constitution supersedes all other architectural guidelines.

### Amendment Process

1. Propose amendment with rationale and migration plan
2. Review impact on existing features
3. Update constitution version (see semantic versioning below)
4. Update all affected templates and documentation
5. Communicate changes to all contributors

### Versioning

- **MAJOR**: Breaking changes to core principles (e.g., removing shared module, changing platform architecture)
- **MINOR**: New principle added or significant clarification
- **PATCH**: Typo fixes, wording improvements, non-semantic changes

### Compliance

All pull requests MUST:
- Verify platform-specific code resides in correct modules (no shared compiled code)
- Run platform-specific unit tests and ensure 80%+ coverage:
  - Android: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
  - iOS: XCTest with coverage enabled
  - Web: `npm test -- --coverage` (from webApp/)
  - Backend: `npm test -- --coverage` (from server/)
- Run E2E tests for affected features:
  - Web: `npx playwright test`
  - Mobile: `npm run test:mobile:android`, `npm run test:mobile:ios`
- Verify all new interactive UI elements have test identifiers:
  - Android: `testTag` modifier
  - iOS: `accessibilityIdentifier` modifier
  - Web: `data-testid` attribute
- Verify all new public APIs have concise, high-level documentation:
  - Kotlin: KDoc format
  - Swift: SwiftDoc format
  - TypeScript: JSDoc format
  - Focus on WHAT/WHY, not HOW (1-3 sentences)
- Verify Android Compose screens follow MVI architecture:
  - Single `StateFlow<UiState>` source of truth with immutable data classes
  - Sealed `UserIntent` and optional `UiEffect` types
  - `dispatchIntent` entry point wired from UI actions
  - Reducers as pure functions with exhaustive `when` handling
  - Effects via `SharedFlow`/`Channel`
- Verify iOS SwiftUI screens follow MVVM-C architecture:
  - UIKit-based coordinators manage navigation
  - ViewModels conform to `ObservableObject` with `@Published` properties
  - ViewModels communicate with coordinators via methods or closures
  - SwiftUI views observe ViewModels (no business/navigation logic)
- Verify all new tests follow Given-When-Then structure:
  - Clear separation of setup (Given), action (When), verification (Then)
  - Descriptive test names following platform conventions
  - Comments marking test phases in complex tests
- Backend code quality checks (if /server affected):
  - Run ESLint: `npm run lint` (from server/)
  - Verify Clean Code principles (small functions, max 3 nesting, DRY)
  - Verify TDD workflow (tests written before implementation)
  - Verify JSDoc documentation for complex APIs

Violations MUST be rejected unless explicitly justified in PR description
with temporary exception approval.

### Living Documentation

This constitution guides runtime development. For command-specific workflows,
see `.specify/templates/commands/*.md` files (if present).

**Version**: 2.2.0 | **Ratified**: 2025-11-14 | **Last Amended**: 2025-11-21
