# PetSpot - Multi-Platform Project with Independent Platform Implementations + Node.js Backend

**Architecture**: See `.specify/memory/constitution.md` for architectural principles (platform independence, 80% test coverage, native patterns)

## Build & Test Commands

### Android (Kotlin + Jetpack Compose)
- Build Android: `./gradlew :composeApp:assembleDebug`
- Run Android tests: `./gradlew :composeApp:testDebugUnitTest`
- Run Android tests with coverage: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` (view at composeApp/build/reports/kover/html/index.html)

### iOS (Swift + SwiftUI)
- Build iOS: Open `/iosApp` in Xcode and build
- Run iOS tests: `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`
- View coverage: Xcode coverage report

### Backend Server (Node.js/Express)
- Install dependencies: `npm install` (run from server/)
- Run dev server: `npm run dev` (from server/, active on http://localhost:3000)
- Run production server: `npm start` (from server/)
- Run backend tests: `npm test` (from server/)
- Run backend tests with coverage: `npm test -- --coverage` (from server/, view at server/coverage/index.html)
- Run linter: `npm run lint` (from server/)
- Create migration: `npm run knex:add-migration <name>` (from server/)

### Web Application (React + TypeScript)
- Install dependencies: `npm install` (run from webApp/)
- Run web dev server: `npm run start` (from webApp/)
- Build web app: `npm run build` (from webApp/)
- Run web tests with coverage: `npm test -- --coverage` (run from webApp/, view at webApp/coverage/index.html)

### End-to-End Tests (Java 21 + Maven + Cucumber)
- Run E2E web tests: `mvn test -Dtest=WebTestRunner` (from e2e-tests/java/)
- Run E2E Android tests: `mvn test -Dtest=AndroidTestRunner` (from e2e-tests/java/)
- Run E2E iOS tests: `mvn test -Dtest=IosTestRunner` (from e2e-tests/java/)
- View reports: `e2e-tests/java/target/cucumber-reports/{web,android,ios}/index.html`

## Project Structure

### Platform-Specific Full Stack Implementations

- `/composeApp` - Android app with Jetpack Compose (FULL STACK in Kotlin)
  - `androidMain/.../domain/models/` - Kotlin domain models
  - `androidMain/.../domain/repositories/` - Repository interfaces
  - `androidMain/.../domain/usecases/` - Business logic use cases
  - `androidMain/.../data/repositories/` - Repository implementations
  - `androidMain/.../di/` - Koin dependency injection modules (mandatory)
  - `androidMain/.../presentation/` - MVI ViewModels (single `StateFlow<UiState>`, sealed intents, `SharedFlow` effects)
  - `androidMain/.../ui/` - Jetpack Compose screens
  - `androidUnitTest/` - Unit tests for domain, data, and ViewModels (MUST achieve 80% coverage)

- `/iosApp` - iOS app with SwiftUI (FULL STACK in Swift)
  - `iosApp/Domain/Models/` - Swift domain models (structs/classes)
  - `iosApp/Domain/Repositories/` - Repository protocols
  - `iosApp/Data/Repositories/` - Repository implementations
  - `iosApp/DI/` - Manual dependency injection setup (ServiceContainer with constructor injection)
  - `iosApp/ViewModels/` - ViewModels (ObservableObject with @Published properties) - call repositories directly (NO use cases)
  - `iosApp/Coordinators/` - UIKit-based coordinators for navigation management
  - `iosApp/Views/` - SwiftUI views (wrapped in UIHostingController)
  - `iosAppTests/` - Unit tests for domain, data, and ViewModels (MUST achieve 80% coverage)

- `/webApp` - React TypeScript web app (FULL STACK in TypeScript)
  - `src/models/` - TypeScript domain models (interfaces/types)
  - `src/services/` - Service interfaces and HTTP clients consuming backend API
  - `src/hooks/` - Custom React hooks for state management
  - `src/components/` - React components
  - `src/di/` - Dependency injection setup (React Context, native patterns, or DI library)
  - `src/__tests__/` - Unit tests for models, services, hooks (MUST achieve 80% coverage)

- `/server` - Node.js/Express backend API (STANDALONE, NOT part of any platform)
  - `src/middlewares/` - Express middlewares (auth, logging, error handling)
  - `src/routes/` - REST API endpoint definitions (Express routers)
  - `src/services/` - Business logic layer (testable, pure functions, MUST achieve 80% coverage)
    - `__test__/` - Unit tests for services
  - `src/database/` - Database config, Knex migrations, query repositories (SQLite → PostgreSQL migration path)
  - `src/lib/` - Utility functions and helpers (pure, reusable, MUST achieve 80% coverage)
    - `__test__/` - Unit tests for utilities
  - `src/__test__/` - Integration tests for REST API endpoints (MUST achieve 80% coverage)
  - `src/app.ts` - Express app configuration (middleware setup, route registration)
  - `src/index.ts` - Server entry point (port binding, startup)

- `/e2e-tests/java` - End-to-end tests (Java 21 + Maven + Cucumber)
  - `pom.xml` - Maven configuration (Java 21, Selenium, Appium, Cucumber dependencies)
  - `src/test/resources/features/` - Gherkin feature files (BDD scenarios)
    - `web/` - Web platform features
    - `mobile/` - Mobile platform features (shared for Android/iOS)
  - `src/test/java/.../pages/` - Page Object Model (Web - Selenium)
  - `src/test/java/.../screens/` - Screen Object Model (Mobile - Appium, unified for iOS/Android)
  - `src/test/java/.../steps/web/` - Web step definitions
  - `src/test/java/.../steps/mobile/` - Mobile step definitions
  - `src/test/java/.../runners/` - Test runners (WebTestRunner, AndroidTestRunner, IosTestRunner)
  - `src/test/java/.../utils/` - WebDriverManager, AppiumManager, test utilities

## Architecture Principles

**Platform Independence** (NON-NEGOTIABLE):
- Each platform (Android, iOS, Web) implements its full technology stack independently
- NO shared compiled code between platforms
- Each platform has its own domain models, use cases, repositories, ViewModels, and UI
- Platforms consume backend API via HTTP/REST (common integration point)
- Platforms MAY share design patterns and architectural conventions

**Android MVI Architecture** (NON-NEGOTIABLE):
- Single `StateFlow<UiState>` source of truth with immutable data classes
- Sealed `UserIntent` for all user interactions
- Pure reducer functions for state transitions (side-effect free, unit-tested)
- `SharedFlow<UiEffect>` for one-off events (navigation, snackbars)
- Co-locate UiState, UserIntent, UiEffect, and reducers with feature packages

**iOS MVVM-C Architecture** (NON-NEGOTIABLE):
- UIKit-based coordinators manage navigation and create `UIHostingController` instances
- ViewModels conform to `ObservableObject` with `@Published` properties
- ViewModels call repositories directly (NO use cases layer)
- ViewModels communicate with coordinators via methods or closures
- SwiftUI views observe ViewModels (no business/navigation logic in views)
- Coordinator hierarchy for nested flows (parent/child pattern)
- Manual dependency injection (constructor injection via ServiceContainer)

**Backend Architecture** (NON-NEGOTIABLE):
- TDD workflow (Red-Green-Refactor): write failing test → minimal implementation → refactor
- Clean Code principles: small functions, descriptive names, max 3 nesting levels, DRY
- ESLint with TypeScript plugin MUST be enabled
- Separation of concerns: middlewares, routes (thin), services (business logic), database, lib (utilities)
- 80% test coverage for services, lib, and API endpoints

## Code Style

- Kotlin (Android): Use explicit types for public APIs, prefer `val` over `var`
- Swift (iOS): Prefer structs over classes, use `let` over `var`
- TypeScript (Web/Backend): React functional components, async/await for async operations
- Imports: Group stdlib, then third-party, then project imports
- Naming: camelCase for variables/functions, PascalCase for classes/components
- Error handling:
  - Android: `Result<T>` for use cases
  - iOS: Swift Result type or throws
  - Web: try/catch with proper error handling
  - Backend: try/catch with proper HTTP status codes
- Target: JVM 17 for Android, iOS 15+ for iOS, ES2015 for Web, Node.js v24 (LTS) for backend, Java 21 for E2E tests
- DI:
  - Android: Koin (mandatory)
  - iOS: Manual DI with constructor injection (mandatory)
  - Web: React Context (recommended)
  - Backend: Manual DI (constructor injection, factory functions)
- Architecture: Repository pattern (interfaces per platform, implementations in data layer)
- Backend uses Express routing and Knex query builder (no ORM)
- Backend Clean Code Principles (MANDATORY):
  - ESLint with TypeScript plugin (`@typescript-eslint/eslint-plugin`) MUST be enabled
  - Functions should be small, focused, and do one thing (single responsibility)
  - Descriptive naming (avoid unclear abbreviations; ok: `id`, `db`, `api`)
  - Maximum 3 nesting levels - extract nested logic into functions
  - DRY (Don't Repeat Yourself) - extract reusable logic into `/lib` or services
  - Self-documenting code with JSDoc for all public APIs
  - Dependency minimization:
    - Only add dependencies providing significant value
    - Prefer well-maintained, security-audited packages
    - Avoid micro-dependencies (e.g., "is-even", "left-pad")
    - Document rationale for each dependency in comments
    - Regular `npm audit` security checks

## Async Patterns

- Android: Kotlin Coroutines (`suspend` functions, `viewModelScope`)
- iOS: Swift Concurrency (`async`/`await` with `@MainActor`)
- Web: Native `async`/`await` (no `.then()` chains)
- Backend: Native `async`/`await` (Express with async handlers)
- Prohibited: Combine, RxJava, RxSwift, RxJS, LiveData (for new code)

## Test Identifiers (MANDATORY)

- Android: Use `Modifier.testTag("screen.element.action")` on all interactive composables
- iOS: Use `.accessibilityIdentifier("screen.element.action")` on all interactive views
- Web: Use `data-testid="screen.element.action"` on all interactive elements
- Naming: `{screen}.{element}.{action}` (e.g., `petList.addButton.click`)
- Lists: Include stable IDs (e.g., `petList.item.${pet.id}`)

## Documentation (MANDATORY)

- All public APIs MUST have documentation when purpose is not clear from name alone
- Skip documentation for self-explanatory names (e.g., `getPets()`, `isLoading`, `MAX_RETRIES`)
- Use platform-native format: KDoc (Kotlin), SwiftDoc (Swift), JSDoc (TypeScript)
- Keep concise and high-level (1-3 sentences: WHAT/WHY, not HOW)
- Example: `/** Loads pets from repository and updates UI state. */`
- Avoid obvious statements and implementation details

## Test Frameworks

- Android: JUnit 6 + Kotlin Test + Turbine (Flow testing)
  - Tests: `/composeApp/src/androidUnitTest/`
  - Coverage: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
- iOS: XCTest with Swift Concurrency (async/await)
  - Tests: `/iosApp/iosAppTests/`
  - Coverage: Xcode coverage report
- Web: Vitest + React Testing Library
  - Tests: `/webApp/src/__tests__/`
  - Coverage: `npm test -- --coverage` (from webApp/)
- Backend: Vitest + Supertest (unit + integration tests)
  - TDD Workflow (MANDATORY): Red-Green-Refactor cycle
    - RED: Write failing test first
    - GREEN: Write minimal code to pass test
    - REFACTOR: Improve code quality without changing behavior
  - Unit tests: `/server/src/services/__test__/`, `/server/src/lib/__test__/` (80% coverage)
  - Integration tests: `/server/src/__test__/` (REST API endpoints, 80% coverage)
  - Coverage: `npm test -- --coverage` (from server/)
- E2E Tests: Java 21 + Maven + Selenium/Appium + Cucumber (unified stack for all platforms)
  - Location: `/e2e-tests/java/`
  - Features: `/e2e-tests/java/src/test/resources/features/`
  - Run Web: `mvn test -Dtest=WebTestRunner` (from e2e-tests/java/)
  - Run Android: `mvn test -Dtest=AndroidTestRunner` (from e2e-tests/java/)
  - Run iOS: `mvn test -Dtest=IosTestRunner` (from e2e-tests/java/)
  - Reports: `target/cucumber-reports/{web,android,ios}/index.html`
- Test Convention: ALL tests MUST follow Given-When-Then (Arrange-Act-Assert) structure
  - Clearly separate setup (Given), action (When), verification (Then) phases
  - Use descriptive test names: Kotlin backticks, Swift camelCase_with_underscores, TypeScript strings
  - Mark test phases with comments in complex tests
  - Example (Kotlin): `@Test fun \`should return success when repository returns pets\`() = runTest { /* Given */ ... /* When */ ... /* Then */ ... }`
  - Example (Swift): `func testLoadPets_whenRepositorySucceeds_shouldUpdatePetsState() async { /* Given */ ... /* When */ ... /* Then */ ... }`
  - Example (Web): `it('should load pets successfully when service returns data', async () => { /* Given */ ... /* When */ ... /* Then */ ... })`
  - Example (Backend): `it('should return 200 when pet exists', async () => { /* Given */ ... /* When */ ... /* Then */ ... })`

## Dependencies

### Android
- Koin for dependency injection (mandatory)
- Jetpack Compose for UI
- Kotlin Coroutines + Flow for async/state management
- Retrofit or Ktor for HTTP client

### iOS
- Manual dependency injection with constructor injection (mandatory - NO frameworks)
- SwiftUI for UI
- UIKit for coordinators
- URLSession or Alamofire for HTTP client

### Web
- React + TypeScript
- React Context or DI library for dependency injection
- Axios or Fetch API for HTTP client
- Vitest + React Testing Library for testing

### Backend (Node.js)
- Express (web framework)
- Knex (query builder)
- SQLite3 (development database)
- pg (PostgreSQL driver for production)
- Vitest (testing framework)
- Supertest (HTTP assertions)
- ESLint + @typescript-eslint/eslint-plugin (code quality)

### E2E Tests (Java 21)
- Java 21 (LTS) - required runtime
- Maven 3.9+ (build tool)
- Selenium WebDriver 4.x (web automation)
- Appium Java Client 9.x (mobile automation)
- Cucumber 7.x (BDD framework with Gherkin)
- JUnit 5 (test runner)
- WebDriverManager (automatic browser driver management)

## 80% Test Coverage Requirement (MANDATORY)

Each platform MUST maintain minimum 80% line + branch coverage:

- **Android**: Unit tests for domain models, use cases, and ViewModels in `/composeApp/src/androidUnitTest/`
- **iOS**: Unit tests for domain models, use cases, and ViewModels in `/iosApp/iosAppTests/`
- **Web**: Unit tests for models, services, and hooks in `/webApp/src/__tests__/`
- **Backend**: Unit tests for services and lib in `/server/src/services/__test__/` and `/server/src/lib/__test__/`
- **Backend API**: Integration tests for REST endpoints in `/server/src/__test__/`
