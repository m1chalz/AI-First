# PetSpot Project Constitution

<!--
Sync Impact Report:
Version change: 2.5.4 → 2.5.5
PATCH: Added variable reuse requirement for backend and webapp tests (reuse // given variables in // then phase)

Changes (v2.5.5):
- UPDATED: Principle VIII "Given-When-Then Test Convention" - added variable reuse requirement and updated test examples
- UPDATED: Principle IX "Backend Architecture & Quality Standards" - added variable reuse requirement to testing strategy
- UPDATED: Principle XIII "Web Architecture & Quality Standards" - added variable reuse requirement to testing strategy

Rationale:
- Reusing variables from // given phase in // then phase improves test maintainability
- Reduces duplication and makes tests more readable
- Ensures test data consistency between setup and assertions

Previous version (v2.5.4):
PATCH: Added parameterized test requirements and test case minimization guidelines for backend and webapp

Changes (v2.5.4):
- UPDATED: Principle VIII "Given-When-Then Test Convention" - enhanced parameterized tests section with requirements
- UPDATED: Principle IX "Backend Architecture & Quality Standards" - added test case minimization requirements
- UPDATED: Principle XIII "Web Architecture & Quality Standards" - added test case minimization requirements

Rationale:
- Parameterized tests reduce duplication and improve maintainability
- Minimizing test cases while covering all scenarios improves test suite efficiency
- Description parameters should only be added when test purpose is unclear

Previous version (v2.5.3):
PATCH: Added code reuse, simplicity, and implementation phase requirements for backend and webapp

Changes (v2.5.3):
- UPDATED: Principle IX "Backend Architecture & Quality Standards" - added code reuse, simplicity, and no summary files requirements
- UPDATED: Principle XIII "Web Architecture & Quality Standards" - added code reuse, simplicity, and no summary files requirements

Rationale:
- Code reuse reduces duplication and maintenance burden
- Simple code is easier to understand and maintain
- Summary files during implementation add unnecessary overhead and can become outdated

Previous version (v2.5.2):
PATCH: Strengthened minimal documentation/commenting requirement for backend and webapp

Changes (v2.5.2):
- UPDATED: Principle VII "Public API Documentation" - added explicit prohibition of unnecessary documentation/comments for backend and webapp
- UPDATED: Principle IX "Backend Architecture & Quality Standards" - emphasized minimal documentation (only when really unclear)
- UPDATED: Principle XIII "Web Architecture & Quality Standards" - emphasized minimal documentation (only when really unclear)

Rationale:
- Code should be self-documenting through clear naming
- Documentation/comments should only be added when code is genuinely unclear and hard to understand
- Reduces maintenance burden and code noise

Previous version (v2.5.1):
PATCH: Clarified test comment format for backend and webapp tests (Given-When-Then sections)

Changes (v2.5.1):
- UPDATED: Principle VIII "Given-When-Then Test Convention" - added mandatory comment format for backend and webapp
- UPDATED: Test examples for backend and webapp to use `// given`, `// when`, `// then` (lowercase, no additional text)
- Clarified that comment format applies specifically to backend (`/server`) and webapp (`/webApp`) tests

Rationale:
- Consistent comment format improves test readability and maintainability
- Simple `// given` format (not `// given: prepared something!`) keeps comments concise and standardized

Previous version (v2.5.0):
MINOR: Added Web Architecture & Quality Standards principle for React 18 + TypeScript webApp

Changes (v2.5.0):
- ADDED: Principle XIII "Web Architecture & Quality Standards" - Clean Code, TDD, dependency minimization, business logic extraction
- UPDATED: Module Structure - Web section to include /src/lib/ for business logic utilities
- UPDATED: Testing Standards - Web section to clarify /src/hooks/__test__/ and /src/lib/__test__/ coverage requirements
- UPDATED: Compliance checklist - added Web Architecture & Quality Standards checks

Rationale:
- Establishes consistent quality standards for webApp matching backend standards
- Ensures business logic is testable and maintainable
- TDD workflow improves code quality and reduces bugs
- Dependency minimization reduces security surface and maintenance burden

Previous version (v2.4.0):
MINOR: Removed TypeScript E2E tests (Playwright + WebdriverIO) - Java stack is now the only E2E framework

Changes (v2.4.0):
- REMOVED: TypeScript/Playwright web E2E tests (/e2e-tests/web/)
- REMOVED: TypeScript/WebdriverIO mobile E2E tests (/e2e-tests/mobile/)
- REMOVED: TypeScript E2E config files (playwright.config.ts, wdio.conf.ts, package.json, tsconfig.json)
- UPDATED: Follow-up TODOs - marked E2E migration as COMPLETED
- ADDED: New E2E specs structure (050-animal-list, 051-pet-details, 052-report-missing)

Rationale:
- Java/Maven/Cucumber stack provides unified E2E testing for all platforms
- Eliminates duplicate test infrastructure maintenance
- Single test language (Java) for entire E2E suite
- Cross-platform scenarios with Cucumber tags (@web, @ios, @android)

Previous version (v2.3.0):
MINOR: Enhanced iOS architecture patterns + Migrated E2E testing stack to Java/Maven/Selenium/Cucumber

Changes (v2.3.0):

iOS Architecture Enhancements:
- UPDATED: Module Structure for iOS - removed ViewModels/ directory, ViewModels now in Views/
- UPDATED: Module Structure for iOS - added NavigationBackHiding wrapper requirement for SwiftUI screens
- UPDATED: Architecture Patterns - iOS protocols use "Protocol" suffix, implementations without suffix
- UPDATED: Principle XI "iOS MVVM-C Architecture" - added view model/model patterns, swiftgen requirement, presentation model guidelines
- Added: iOS presentation layer guidelines (hex colors in models, Color conversion in views)
- Added: iOS localization requirement (swiftgen for all displayed texts)
- Added: Data formatting and presentation logic rules (ALL formatting in ViewModels/Models, views only display)

E2E Testing Migration:
- UPDATED: Principle XII "End-to-End Testing" - replaced Playwright/TypeScript with Selenium/Cucumber/Java
- UPDATED: Principle VI "Test Identifiers for UI Controls" - updated E2E usage examples to Java
- UPDATED: Testing Standards section - unified E2E project structure with single Maven pom.xml
- UPDATED: Compliance checklist - changed E2E test commands to Maven with Cucumber tags
- REPLACED: Web E2E framework from Playwright to Selenium WebDriver (Java)
- REPLACED: Mobile E2E framework from Appium+WebdriverIO+TypeScript to Appium+Cucumber+Java
- ADDED: Unified `/e2e-tests/` project structure with shared pom.xml
- ADDED: Cucumber tags for platform execution control (@web, @android, @ios)
- ADDED: Page Object Model with XPath locators for web (using @FindBy annotations)
- ADDED: Screen Object Model with dual annotations (@iOSXCUITFindBy, @AndroidFindBy) for mobile
- ADDED: Separate Cucumber HTML reports for web/android/ios platforms

Modified principles:
- VI. Test Identifiers for UI Controls (UPDATED - Java examples for E2E usage)
- XI. iOS Model-View-ViewModel-Coordinator Architecture (UPDATED - added model patterns, swiftgen, presentation guidelines)
- XII. End-to-End Testing (UPDATED - complete rewrite for Java/Maven/Cucumber stack)

Modified sections:
- Module Structure → iOS (UPDATED - ViewModels location, NavigationBackHiding wrapper)
- Architecture Patterns → Repository Pattern (UPDATED - iOS protocol naming with "Protocol" suffix)
- Testing Standards → End-to-End Tests (UPDATED - unified project structure)
- Compliance (UPDATED - Maven commands with Cucumber tags)

Templates requiring updates:
- ✅ .specify/templates/plan-template.md - Added Web Architecture & Quality Standards checks
- ✅ .specify/templates/tasks-template.md - Updated Web test tasks to include TDD workflow and /src/lib/ structure
- ✅ .specify/templates/spec-template.md (no changes needed - platform-agnostic)

Modified principles (v2.5.1):
- VIII. Given-When-Then Test Convention (UPDATED - added mandatory comment format for backend and webapp)

Templates requiring updates (v2.5.1):
- ✅ .specify/templates/plan-template.md (no changes needed - comment format is implementation detail)
- ✅ .specify/templates/tasks-template.md (no changes needed - comment format is implementation detail)
- ✅ .specify/templates/spec-template.md (no changes needed - platform-agnostic)

Modified principles (v2.5.2):
- VII. Public API Documentation (UPDATED - added explicit minimal documentation policy for backend and webapp)
- IX. Backend Architecture & Quality Standards (UPDATED - emphasized minimal documentation requirement)
- XIII. Web Architecture & Quality Standards (UPDATED - emphasized minimal documentation requirement)

Templates requiring updates (v2.5.2):
- ✅ .specify/templates/plan-template.md (no changes needed - documentation policy is implementation detail)
- ✅ .specify/templates/tasks-template.md (no changes needed - documentation policy is implementation detail)
- ✅ .specify/templates/spec-template.md (no changes needed - platform-agnostic)

Modified principles (v2.5.3):
- IX. Backend Architecture & Quality Standards (UPDATED - added code reuse, simplicity, and no summary files requirements)
- XIII. Web Architecture & Quality Standards (UPDATED - added code reuse, simplicity, and no summary files requirements)

Templates requiring updates (v2.5.3):
- ✅ .specify/templates/plan-template.md (no changes needed - code reuse and simplicity are implementation details)
- ✅ .specify/templates/tasks-template.md (no changes needed - code reuse and simplicity are implementation details)
- ✅ .specify/templates/spec-template.md (no changes needed - platform-agnostic)

Modified principles (v2.5.4):
- VIII. Given-When-Then Test Convention (UPDATED - enhanced parameterized tests section with requirements for backend and webapp)
- IX. Backend Architecture & Quality Standards (UPDATED - added test case minimization requirements)
- XIII. Web Architecture & Quality Standards (UPDATED - added test case minimization requirements)

Templates requiring updates (v2.5.4):
- ✅ .specify/templates/plan-template.md (no changes needed - test case minimization is implementation detail)
- ✅ .specify/templates/tasks-template.md (no changes needed - test case minimization is implementation detail)
- ✅ .specify/templates/spec-template.md (no changes needed - platform-agnostic)

Modified principles (v2.5.5):
- VIII. Given-When-Then Test Convention (UPDATED - added variable reuse requirement and updated test examples for backend and webapp)
- IX. Backend Architecture & Quality Standards (UPDATED - added variable reuse requirement to testing strategy)
- XIII. Web Architecture & Quality Standards (UPDATED - added variable reuse requirement to testing strategy)

Templates requiring updates (v2.5.5):
- ✅ .specify/templates/plan-template.md (no changes needed - variable reuse is implementation detail)
- ✅ .specify/templates/tasks-template.md (no changes needed - variable reuse is implementation detail)
- ✅ .specify/templates/spec-template.md (no changes needed - platform-agnostic)

Follow-up TODOs:
- ✅ COMPLETED (Spec 025): Removed TypeScript E2E tests (Playwright web + WebdriverIO mobile)
- ✅ COMPLETED (Spec 016): Created Maven pom.xml with Selenium, Appium, and Cucumber dependencies
- ✅ COMPLETED (Spec 016): Set up Cucumber reporting plugins in Maven configuration
- ✅ COMPLETED (Spec 016): Created .feature files for web and mobile
- ✅ COMPLETED (Spec 016): Documented Page Object and Screen Object patterns
- ⏳ PENDING: Update CI/CD pipeline to use Maven commands with Cucumber tags
- ⏳ PENDING: Implement full E2E coverage (Specs 050, 051, 052)

Previous changes (v2.2.0):
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

**Web** (`/webApp/src/`):
- Framework: Vitest + React Testing Library
- Run command: `npm test --coverage` (from webApp/)
- Report: `webApp/coverage/index.html`
- Scope: Domain models, services, custom hooks, state management
- Coverage target: 80% line + branch coverage

**Backend** (`/server/src/`):
- Framework: Vitest (unit) + SuperTest (integration)
- Run commands:
  - Unit tests: `npm test` (from server/)
  - With coverage: `npm test --coverage`
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

**iOS Example** (`/iosApp/iosApp/Domain/Repositories/` and `/iosApp/iosApp/Data/Repositories/`):
```swift
// Protocol definition in Domain/Repositories/
// MUST use "Protocol" suffix
protocol PetRepositoryProtocol {
    func getPets() async throws -> [Pet]
    func getPetById(id: String) async throws -> Pet
}

// Implementation in Data/Repositories/
// WITHOUT suffix - concrete class
class PetRepository: PetRepositoryProtocol {
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
- Naming convention: `{screen}.{element}` (e.g., `petList.addButton`)
- Example:
  ```swift
  Button("Add Pet") {
      // action
  }
  .accessibilityIdentifier("petList.addButton")

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
```java
// Selenium (Web)
@FindBy(xpath = ("//*[@data-testid='petList.addButton.click']")
private WebElement addPetButton;
public boolean isAddPetButtonDisplayed() {
    return createPinTitle.isDisplayed();
}


// Appium (Mobile - Unified iOS/Android)
// Screen Object with annotations
@AndroidFindBy(uiAutomator = "resourceId(\"petList.addButton.click\")")
@iOSXCUITFindBy(id = "petList.addButton.click")
private WebElement addPetButton;

public boolean isTitleDisplayed() {
    return createPinTitle.isDisplayed();
}
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

**Backend and WebApp Documentation Policy (MANDATORY)**:
- Backend (`/server`) and WebApp (`/webApp`) code MUST NOT be documented or commented unless it is really unclear and hard to understand
- Code MUST be self-documenting through clear, descriptive naming
- Documentation/comments MUST only be added when code is genuinely difficult to understand without them
- Prefer refactoring unclear code to adding documentation/comments
- Inline comments explaining "what" code does are PROHIBITED (code should be clear enough)
- Inline comments explaining "why" complex business logic exists MAY be acceptable if the reason is non-obvious

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

**Comment Format Requirements** (Backend and WebApp):
- Backend tests (`/server`) and WebApp tests (`/webApp`) MUST use section comments to mark test phases
- Comments MUST be lowercase: `// given`, `// when`, `// then`
- Comments MUST NOT include additional text (e.g., `// given: prepared something!` is prohibited)
- Use simple format: `// given`, `// when`, `// then` only

**Variable Reuse Requirements** (Backend and WebApp):
- When adding a test, MUST try to reuse variables created in the `// given` phase also in the `// then` phase instead of copying literals
- Prefer referencing test data variables (e.g., `mockPets[0].name`) over hardcoded literals (e.g., `'Max'`)
- This improves test maintainability and ensures consistency between setup and assertions
- Only use literals in `// then` when the expected value differs from the input (e.g., transformed data)

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
        // given
        const mockPets = [
            { id: '1', name: 'Max', species: 'dog' },
            { id: '2', name: 'Luna', species: 'cat' }
        ];
        vi.mocked(petService.getPets).mockResolvedValue(mockPets);

        const { result } = renderHook(() => usePets());

        // when
        await act(async () => {
            await result.current.loadPets();
        });

        // then
        expect(result.current.pets).toHaveLength(mockPets.length);
        expect(result.current.pets[0].name).toBe(mockPets[0].name);
        expect(result.current.isLoading).toBe(false);
    });
});
```

**Backend Tests** (Node.js/TypeScript):
```typescript
describe('petService', () => {
    it('should return all pets when repository has data', async () => {
        // given
        const mockPets = [
            { id: 1, name: 'Max', species: 'dog' },
            { id: 2, name: 'Luna', species: 'cat' }
        ];
        const fakeRepository = new FakePetRepository(mockPets);

        // when
        const result = await getAllPets(fakeRepository);

        // then
        expect(result).toHaveLength(mockPets.length);
        expect(result[0].name).toBe(mockPets[0].name);
    });
});
```

**Parameterized Tests** (Backend, Web, Android):

When tests share the same logic with different input/output pairs, SHOULD use parameterized tests:

**Parameterized Test Requirements** (Backend, Web, Android):
- MUST merge tests into parameterized ones if possible and worthwhile
- MUST add description parameter ONLY if it's unclear why it's worth testing the given set of arguments
- MUST minimize number of test cases - cover all edge cases and happy paths, but don't duplicate similar cases
- Each parameter set MUST test a distinct scenario (different edge case, different happy path, etc.)
- Avoid parameterized tests when test logic differs significantly between cases

```typescript
// TypeScript/Backend/Web (Vitest)
// ✅ GOOD - Parameterized test with clear purpose (testing different species)
describe('createPet', () => {
    it.each([
        ['Max', 'dog'],
        ['Luna', 'cat'],
        ['Buddy', 'dog']
    ])('should create pet with name=%s and species=%s', async (name, species) => {
        // given
        const pet = { name, species, ownerId: 1 };
        
        // when
        const result = await createPet(pet);
        
        // then
        expect(result.name).toBe(name);
        expect(result.species).toBe(species);
    });
});

// ✅ GOOD - Parameterized test with description when purpose is unclear
describe('validateEmail', () => {
    it.each([
        { email: 'user@example.com', expected: true, description: 'valid email with common domain' },
        { email: 'user+tag@example.co.uk', expected: true, description: 'valid email with plus and subdomain' },
        { email: 'invalid', expected: false, description: 'missing @ symbol' }
    ])('should return $expected for $description', ({ email, expected }) => {
        // given
        // when
        const result = validateEmail(email);
        // then
        expect(result).toBe(expected);
    });
});

// ❌ BAD - Duplicate similar cases (all test the same happy path)
it.each([
    ['Max', 'dog'],
    ['Max', 'dog'],  // duplicate
    ['Max', 'dog']   // duplicate
])('should create pet', async (name, species) => { /* ... */ });
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
  - Self-documenting code (clear naming, no unnecessary documentation/comments)
- MUST NOT document or comment code unless it is really unclear and hard to understand
  - Code MUST be self-documenting through clear naming
  - Prefer refactoring unclear code to adding documentation/comments
  - Only add JSDoc/comments when code is genuinely difficult to understand without them
- MUST reuse existing code, logic, and styles whenever possible
  - Check for existing utilities, helpers, and patterns before creating new ones
  - Extract reusable logic to `/src/lib/` for shared use
  - Reuse existing styles, components, and patterns from the codebase
  - Avoid duplicating functionality that already exists
- MUST keep code simple
  - Prefer simple, straightforward solutions over complex ones
  - Avoid over-engineering and premature optimization
  - Use the simplest approach that solves the problem
- MUST NOT produce any summary files during the implementation phase
  - No README files, summary documents, or implementation notes
  - Focus on code implementation only
  - Documentation belongs in code comments (only when necessary) or existing documentation files
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
- Run command: `npm test --coverage` (from `/server`)
- MUST minimize number of test cases - cover all edge cases and happy paths, but don't duplicate similar cases
- MUST use parameterized tests when possible and worthwhile to merge similar test logic
- MUST add description parameter to parameterized tests ONLY if it's unclear why it's worth testing the given set of arguments
- MUST try to reuse variables created in the `// given` phase also in the `// then` phase instead of copying literals

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

4. **UIHostingController wrapping**: Coordinators create SwiftUI views and wrap them:
   - First in `NavigationBackHiding` wrapper (hides back button when needed)
   - Then in `UIHostingController` for UIKit integration
   - Example: `UIHostingController(rootView: NavigationBackHiding { PetListView(viewModel: viewModel) })`

5. **SwiftUI views remain pure**: Views MUST NOT contain business logic, navigation logic, or
   direct use case calls. Views only render UI based on ViewModel state and trigger ViewModel methods.

6. **Coordinator hierarchy**: Parent coordinators manage child coordinators for nested flows.
   Child coordinators notify parents via delegation or closures when flow completes.

**View model vs model pattern**:

iOS views MUST follow one of two patterns:

1. **ViewModel pattern** (for full-screen views with dynamic data):
   - Use for screens with network calls, complex state, or business logic
   - ViewModel is `ObservableObject` with `@Published` properties
   - View observes ViewModel via `@StateObject` or `@ObservedObject`
   - Example: List screens, detail screens with editing, forms with validation

2. **Model pattern** (for simple views with static data):
   - Use for reusable components, list items, cards without own state management
   - Define `struct Model` in extension to view struct
   - Model passed via initializer parameter (no `@Published` properties)
   - Example: `PetCardView(model: PetCardView.Model(name: "Max", species: "Dog"))`

Example with Model pattern:
```swift
struct PetCardView: View {
    let model: Model
    
    var body: some View {
        VStack {
            Text(model.name)
            Text(model.species)
        }
    }
}

extension PetCardView {
    struct Model {
        let name: String
        let species: String
    }
}
```

**Localization requirement (MANDATORY)**:
- Project MUST use SwiftGen for all displayed text
- ALL user-facing strings MUST be localized (no hardcoded strings in views)
- Access localized strings via SwiftGen-generated code: `L10n.petListTitle`
- String keys defined in `Localizable.strings` files

**Presentation model extensions (MANDATORY)**:
- Domain models for presentation MUST have extensions in `/iosApp/iosApp/Features/Shared/`
- Extensions provide formatting, colors, derived properties - NOT in domain model definition
- Example: `extension Pet { var displayName: String { ... } }`
- MUST NOT include localization or color logic directly in domain model definition

**Presentation layer independence (MANDATORY)**:
- ViewModels and Models MUST be independent of SwiftUI presentation layer
- Colors stored as hex strings in models (e.g., `statusColor: "#FF5733"`)
- Presentation layer converts hex to `Color` or `UIColor` when rendering
- Example: `Color(hex: model.statusColor)`
- Rationale: Enables easy testing, platform portability, and theme changes without modifying business logic

**Data formatting and presentation logic (MANDATORY)**:
- ALL data formatting logic MUST reside in ViewModels or Models, NOT in SwiftUI views
- Views MUST only display already-formatted data from ViewModel/Model properties
- Examples of formatting logic that belongs in ViewModel/Model:
  - Date formatting: `var formattedDate: String { dateFormatter.string(from: date) }`
  - Phone number formatting: `var formattedPhone: String { formatPhoneNumber(phone) }`
  - Currency formatting: `var priceText: String { currencyFormatter.string(from: price) }`
  - Number formatting: `var distanceText: String { "\(distance) km" }`
  - Pluralization: `var itemCountText: String { "\(count) \(count == 1 ? "item" : "items")" }`
- Views MUST NOT contain formatters, calculations, or conditional text logic
- Rationale: Keeps views "dumb" (pure rendering), enables easy testing of formatting logic,
  centralizes formatting rules in testable ViewModels/Models

Example - GOOD (formatting in ViewModel):
```swift
@MainActor
class PetDetailViewModel: ObservableObject {
    @Published var pet: Pet?
    
    // Formatted properties for view consumption
    var birthDateText: String {
        guard let birthDate = pet?.birthDate else { return L10n.unknown }
        return DateFormatter.shortDate.string(from: birthDate)
    }
    
    var ownerPhoneText: String {
        guard let phone = pet?.ownerPhone else { return L10n.noPhone }
        return formatPhoneNumber(phone)  // +48 123 456 789
    }
    
    private func formatPhoneNumber(_ phone: String) -> String {
        // Formatting logic here
    }
}

struct PetDetailView: View {
    @ObservedObject var viewModel: PetDetailViewModel
    
    var body: some View {
        VStack {
            Text(viewModel.birthDateText)  // Just display
            Text(viewModel.ownerPhoneText)  // Just display
        }
    }
}
```

Example - BAD (formatting in View):
```swift
struct PetDetailView: View {
    @ObservedObject var viewModel: PetDetailViewModel
    
    var body: some View {
        VStack {
            // ❌ BAD - formatting in view
            if let birthDate = viewModel.pet?.birthDate {
                Text(DateFormatter.shortDate.string(from: birthDate))
            } else {
                Text("Unknown")
            }
            
            // ❌ BAD - phone formatting in view
            if let phone = viewModel.pet?.ownerPhone {
                Text(formatPhoneNumber(phone))
            }
        }
    }
    
    private func formatPhoneNumber(_ phone: String) -> String {
        // ❌ BAD - logic in view
    }
}
```

**Repository protocol naming (MANDATORY)**:
- Domain repository protocols MUST use "Protocol" suffix: `PetRepositoryProtocol`
- Data layer implementations WITHOUT suffix: `PetRepository: PetRepositoryProtocol`

Example MVVM-C (iOS ViewModels call repositories directly - NO use cases):
```swift
// ViewModel
@MainActor
class PetListViewModel: ObservableObject {
    @Published var pets: [Pet] = []
    @Published var isLoading = false
    
    // Coordinator callback for navigation
    var onPetSelected: ((String) -> Void)?
    
    private let petRepository: PetRepositoryProtocol
    
    init(petRepository: PetRepositoryProtocol) {
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
    private let petRepository: PetRepositoryProtocol
    
    init(navigationController: UINavigationController, petRepository: PetRepositoryProtocol) {
        self.navigationController = navigationController
        self.petRepository = petRepository
    }
    
    func start() {
        // Manual DI - inject repository protocol into ViewModel
        let viewModel = PetListViewModel(petRepository: petRepository)
        viewModel.onPetSelected = { [weak self] petId in
            self?.showPetDetails(petId: petId)
        }
        
        let view = PetListView(viewModel: viewModel)
        // Wrap in NavigationBackHiding, then UIHostingController
        let hostingController = UIHostingController(
            rootView: NavigationBackHiding { view }
        )
        navigationController.pushViewController(hostingController, animated: true)
    }
    
    private func showPetDetails(petId: String) {
        let detailCoordinator = PetDetailCoordinator(
            navigationController: navigationController,
            petRepository: petRepository  // Pass repository protocol down
        )
        detailCoordinator.start()
    }
}
```

**Testing requirements**:
- ViewModels MUST have unit tests in `/iosApp/iosAppTests/Features/`
- ViewModel tests MUST verify `@Published` property updates
- ViewModel tests MUST verify coordinator callback invocations
- Model structs (simple data) MAY skip tests if purely data containers

**Rationale**: MVVM-C separates navigation responsibility from presentation logic, making both
independently testable. Coordinators provide a centralized place for flow management and enable
complex navigation patterns. ViewModels remain portable and can be tested without UIKit dependencies.
ViewModel/Model pattern distinction reduces boilerplate for simple components while maintaining
consistency for complex screens. SwiftGen ensures type-safe localization. Hex color strings in
models enable theme changes and testability without SwiftUI dependencies.

### XII. End-to-End Testing (NON-NEGOTIABLE)

Every feature specification MUST include end-to-end tests covering all user scenarios:

**Unified Project Structure** (`/e2e-tests/`):
- Single Maven project with shared `pom.xml` at root (`/e2e-tests/pom.xml`)
- Single Java codebase for Web, Android, and iOS
- Execution controlled via Cucumber tags: `@web`, `@android`, `@ios`
- Generates 3 separate Cucumber HTML reports (web/android/ios)

**Web E2E Tests**:
- Framework: Selenium WebDriver (Java + Cucumber)
- Test Scenarios: `/e2e-tests/src/test/resources/features/web/*.feature` (Gherkin with `@web` tag)
- Page Object Model: `/e2e-tests/src/test/java/.../pages/` (XPath locators)
  - Uses `@FindBy(xpath = "...")` annotations
- Step Definitions: `/e2e-tests/src/test/java/.../steps-web/`
- Run command: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web"`
- Report: `/e2e-tests/target/cucumber-reports/web/index.html`

**Mobile E2E Tests**:
- Framework: Appium (Java + Cucumber)
- Test Scenarios: `/e2e-tests/src/test/resources/features/mobile/*.feature` (Gherkin with `@android`/`@ios` tags)
- Screen Object Model: `/e2e-tests/src/test/java/.../screens/` (Unified for iOS/Android)
  - Uses `@iOSXCUITFindBy(id = "...")` and `@AndroidFindBy(uiAutomator = "...")` annotations
- Step Definitions: `/e2e-tests/src/test/java/.../steps-mobile/`
  - Handles platform differences (Android vs iOS) dynamically within steps
- Run command (Android): `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@android"`
- Run command (iOS): `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@ios"`
- Report (Android): `/e2e-tests/target/cucumber-reports/android/index.html`
- Report (iOS): `/e2e-tests/target/cucumber-reports/ios/index.html`

**Requirements**:
- Java 21 (LTS) REQUIRED - E2E tests target Java 21 runtime
- Tests MUST be written in Java (Maven) using Cucumber (Gherkin) for scenarios
- Shared `pom.xml` for dependency management (configured for Java 21)
- Test scenarios defined in `.feature` files with platform-specific tags
- Platform-specific execution controlled by tags (`@web`, `@android`, `@ios`)
- Page Object Model pattern REQUIRED for web (XPath locators)
- Screen Object Model pattern REQUIRED for mobile (supporting both iOS/Android via annotations)
- Step definitions MUST be split by platform type (`steps-web/` vs `steps-mobile/`)

**Rationale**: Unified Java + Cucumber stack reduces context switching and enables code sharing
where appropriate. Cucumber tags allow flexible execution of platform-specific suites from a
single codebase. Mobile screen objects with dual annotations reduce duplication between Android
and iOS test code.

### XIII. Web Architecture & Quality Standards (NON-NEGOTIABLE)

The web application (`/webApp`) MUST follow modern React 18 + TypeScript best practices with rigorous quality standards:

**Technology Stack**:
- **Framework**: React 18
- **Language**: TypeScript (strict mode)
- **Build Tool**: Vite
- **Testing**: Vitest + React Testing Library
- **Linting**: ESLint with TypeScript plugin

**Code Quality Requirements**:
- MUST follow Clean Code principles:
  - Functions should be small, focused, and do one thing (single responsibility)
  - Descriptive naming (no abbreviations except well-known ones like `id`, `api`, `http`)
  - Avoid deep nesting (max 3 levels)
  - Prefer composition over inheritance
  - DRY (Don't Repeat Yourself) - extract reusable logic
  - Self-documenting code (clear naming, no unnecessary documentation/comments)
- MUST NOT document or comment code unless it is really unclear and hard to understand
  - Code MUST be self-documenting through clear naming
  - Prefer refactoring unclear code to adding documentation/comments
  - Only add JSDoc/comments when code is genuinely difficult to understand without them
- MUST reuse existing code, logic, and styles whenever possible
  - Check for existing utilities, helpers, hooks, and patterns before creating new ones
  - Extract reusable logic to `/src/lib/` or `/src/hooks/` for shared use
  - Reuse existing styles, components, and patterns from the codebase
  - Avoid duplicating functionality that already exists
- MUST keep code simple
  - Prefer simple, straightforward solutions over complex ones
  - Avoid over-engineering and premature optimization
  - Use the simplest approach that solves the problem
- MUST NOT produce any summary files during the implementation phase
  - No README files, summary documents, or implementation notes
  - Focus on code implementation only
  - Documentation belongs in code comments (only when necessary) or existing documentation files
- MUST minimize dependencies in `package.json`:
  - Only add dependencies that provide significant value
  - Prefer well-maintained, security-audited packages
  - Avoid micro-dependencies (e.g., "is-even", "left-pad")
  - Document rationale for each dependency in comments
  - Regularly audit dependencies with `npm audit`

**Business Logic Extraction (MANDATORY)**:

All business logic MUST be extracted to separate, testable functions:

- **`/src/hooks/`**: Custom React hooks for state management and business logic
  - Hooks encapsulate complex stateful logic
  - Hooks are pure functions (deterministic outputs for given inputs)
  - Hooks MUST be covered by unit tests in `/src/hooks/__test__/`
- **`/src/lib/`**: Pure utility functions and business logic helpers
  - Framework-agnostic functions (no React dependencies)
  - Reusable across components and hooks
  - Pure functions (no side effects, testable in isolation)
  - MUST be covered by unit tests in `/src/lib/__test__/`
- **`/src/components/`**: React components (presentation layer ONLY)
  - Components SHOULD be thin and delegate to hooks/lib for logic
  - Components focus on rendering and user interaction handling
  - Complex logic extracted to hooks or lib functions

**Test-Driven Development (TDD) Workflow**:

Web development MUST follow TDD (Red-Green-Refactor):

1. **RED**: Write a failing test first
2. **GREEN**: Write minimal code to make test pass
3. **REFACTOR**: Improve code quality without changing behavior

**Testing Strategy**:

**Unit Tests** (Vitest) - MUST achieve 80% coverage:
- Location: `/src/hooks/__test__/`, `/src/lib/__test__/`
- Scope: All business logic in hooks and lib functions
- Run command: `npm test --coverage` (from `/webApp`)
- Report: `webApp/coverage/index.html`
- Framework: Vitest + React Testing Library (for hooks that use React features)
- MUST minimize number of test cases - cover all edge cases and happy paths, but don't duplicate similar cases
- MUST use parameterized tests when possible and worthwhile to merge similar test logic
- MUST add description parameter to parameterized tests ONLY if it's unclear why it's worth testing the given set of arguments
- MUST try to reuse variables created in the `// given` phase also in the `// then` phase instead of copying literals

**Component Tests** (Vitest + React Testing Library) - Recommended:
- Location: `/src/components/.../__tests__/`
- Scope: Component rendering and user interactions
- Focus on behavior, not implementation details

**Testing Requirements**:
- MUST test happy path, error cases, and edge cases
- MUST follow Given-When-Then structure (see Principle VIII)
- MUST use descriptive test names
- MUST use test doubles (mocks, fakes) for dependencies
- MUST test behavior, not implementation details
- MUST minimize number of test cases - cover all scenarios without duplicating similar cases

**Directory Structure** (inside `/webApp/src/`):

```
/webApp/src/
├── models/          - TypeScript domain models (interfaces/types)
├── services/        - HTTP services consuming backend REST API
├── hooks/           - Custom React hooks (state management, business logic)
│   └── __test__/    - Unit tests for hooks (MUST achieve 80% coverage)
├── lib/             - Pure utility functions and business logic helpers
│   └── __test__/    - Unit tests for lib functions (MUST achieve 80% coverage)
├── components/      - React components (presentation layer)
│   └── __tests__/   - Component tests (recommended)
├── contexts/        - React Context providers (DI, global state)
├── routes/          - Route definitions
├── config/          - Configuration files
└── types/           - TypeScript type definitions
```

**Separation of Concerns**:
- **`/src/hooks/`**: Stateful business logic, React-specific logic
  - MUST be testable in isolation
  - MUST NOT contain presentation logic
- **`/src/lib/`**: Pure functions, utilities, framework-agnostic business logic
  - MUST be framework-agnostic (no React dependencies)
  - MUST be pure functions (no side effects)
  - MUST be covered by unit tests
- **`/src/components/`**: Presentation layer (rendering, user interactions)
  - SHOULD delegate complex logic to hooks or lib functions
  - SHOULD be thin and focused on UI concerns

**Rationale**: Clean Code principles and business logic extraction ensure maintainability,
testability, and scalability. TDD workflow catches bugs early and improves code quality.
Minimal dependencies reduce security surface and maintenance burden. Separating business logic
from presentation enables easy unit testing and code reuse across components.

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
- `Domain/Repositories/` - Repository protocols (MUST use "Protocol" suffix, e.g., `PetRepositoryProtocol`)
- `Data/Repositories/` - Repository implementations (WITHOUT suffix, e.g., `PetRepository` implements `PetRepositoryProtocol`)
- `Coordinators/` - UIKit-based coordinators managing navigation
- `Views/` - SwiftUI views + ViewModels (views presented by UIKit navigation wrapped in NavigationBackHiding, then UIHostingController)
- `Features/Shared/` - Presentation model extensions (colors, formatting - NO localization in domain models)
- `DI/` - Manual dependency injection setup (ServiceContainer or similar)

**`/webApp/src/`** (Web - Full Stack):
- `models/` - TypeScript interfaces/types for domain models
- `services/` - HTTP services consuming backend REST API
- `hooks/` - Custom React hooks for state management and business logic
  - `__test__/` - Unit tests for hooks (MUST achieve 80% coverage)
- `lib/` - Pure utility functions and business logic helpers (framework-agnostic)
  - `__test__/` - Unit tests for lib functions (MUST achieve 80% coverage)
- `components/` - React components (presentation layer)
  - `__tests__/` - Component tests (recommended)
- `contexts/` - React Context providers (DI, global state)
- `routes/` - Route definitions
- `config/` - Configuration files
- `types/` - TypeScript type definitions

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
// MUST use "Protocol" suffix
protocol PetRepositoryProtocol {
    func getPets() async throws -> [Pet]
    func getPetById(id: String) async throws -> Pet
}

// Implementation in /iosApp/iosApp/Data/Repositories/
// WITHOUT suffix - implements protocol
class PetRepository: PetRepositoryProtocol {
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
// iOS ViewModel calls repository directly (using Protocol suffix)
@MainActor
class PetListViewModel: ObservableObject {
    private let repository: PetRepositoryProtocol
    
    init(repository: PetRepositoryProtocol) {
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
- Location: `/webApp/src/hooks/__test__/`, `/webApp/src/lib/__test__/`
- Framework: Vitest + React Testing Library
- Run command: `npm test --coverage` (from webApp/)
- Report: `webApp/coverage/index.html`
- Scope: Business logic in hooks and lib functions (MUST achieve 80% coverage)
- TDD Workflow: Red-Green-Refactor cycle (write failing test, minimal implementation, refactor)

**Backend**:
- Location: `/server/src/services/__test__/`, `/server/src/lib/__test__/`, `/server/src/__test__/`
- Framework: Vitest (unit) + SuperTest (integration)
- Run command: `npm test --coverage` (from server/)
- Report: `server/coverage/index.html`
- Scope: Business logic, utilities, REST API endpoints

### End-to-End Tests (MANDATORY)

**Unified E2E Project**:
- Config: `/e2e-tests/pom.xml` (shared Maven project for web and mobile)
- Platform execution controlled by Cucumber tags: `@web`, `@android`, `@ios`

**Web E2E Tests**:
- Framework: Selenium WebDriver (Java + Cucumber)
- Page Object Model: `/e2e-tests/src/test/java/.../pages/`
  - MUST use `@FindBy(xpath = "...")` for element location (consistent with mobile)
- Step Definitions: `/e2e-tests/src/test/java/.../steps-web/`
- Features: `/e2e-tests/src/test/resources/features/web/*.feature`
- Run command: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web"`
- Report: `/e2e-tests/target/cucumber-reports/web/index.html`

**Mobile E2E Tests**:
- Framework: Appium (Java + Cucumber)
- Screen Object Model: `/e2e-tests/src/test/java/.../screens/` (Unified for iOS/Android)
  - Uses `@iOSXCUITFindBy(id = "...")` and `@AndroidFindBy(uiAutomator = "...")` annotations
- Step Definitions: `/e2e-tests/src/test/java/.../steps-mobile/`
  - Handles platform differences (Android vs iOS) dynamically within steps
- Features: `/e2e-tests/src/test/resources/features/mobile/*.feature`
- Run command (Android): `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@android"`
- Run command (iOS): `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@ios"`
- Report (Android): `/e2e-tests/target/cucumber-reports/android/index.html`
- Report (iOS): `/e2e-tests/target/cucumber-reports/ios/index.html`

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
  - Web: `npm test --coverage` (from webApp/)
  - Backend: `npm test --coverage` (from server/)
- Run E2E tests for affected features:
  - Web: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@web"`
  - Mobile: `mvn -f e2e-tests/pom.xml test -Dcucumber.filter.tags="@android"` or `@ios`
  - Reports: Verify Cucumber HTML reports in `target/cucumber-reports/`
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
- Verify Web application follows Architecture & Quality Standards (if /webApp affected):
  - Business logic extracted to `/src/hooks/` or `/src/lib/` (not in components)
  - All hooks and lib functions covered by unit tests (80% coverage)
  - TDD workflow followed (tests written before implementation)
  - Clean Code principles applied (small functions, max 3 nesting, DRY)
  - Dependencies minimized in `package.json`
  - Run ESLint: `npm run lint` (from webApp/)
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

**Version**: 2.5.5 | **Ratified**: 2025-11-14 | **Last Amended**: 2025-01-27
