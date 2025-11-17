# PetSpot - Kotlin Multiplatform Project + Node.js Backend

**Architecture**: See `.specify/memory/constitution.md` for architectural principles (thin shared layer, native presentation, standalone backend, 80% test coverage)

## Build & Test Commands

### Kotlin Multiplatform (Shared + Android)
- Build Android: `./gradlew :composeApp:assembleDebug`
- Build shared Kotlin/JS: `./gradlew :shared:jsBrowserDevelopmentLibraryDistribution`
- Run all Gradle tests: `./gradlew test`
- Run shared tests with coverage: `./gradlew :shared:test koverHtmlReport` (view at shared/build/reports/kover/html/index.html)
- Run single test: `./gradlew :shared:cleanJsTest :shared:jsTest --tests "com.intive.aifirst.petspot.SharedCommonTest.example"`
- Run Android tests: `./gradlew :composeApp:testDebugUnitTest`
- Run Android ViewModel tests with coverage: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` (view at composeApp/build/reports/kover/html/index.html)

### Backend Server (Node.js/Express)
- Install dependencies: `npm install` (run from server/)
- Run dev server: `npm run dev` (from server/, active on http://localhost:3000)
- Run production server: `npm start` (from server/)
- Run backend tests: `npm test` (from server/)
- Run backend tests with coverage: `npm test -- --coverage` (from server/, view at server/coverage/index.html)
- Run linter: `npm run lint` (from server/)
- Create migration: `npm run knex:add-migration <name>` (from server/)

### Web Application (React + TypeScript)
- Build web app: `npm install && npm run build` (run from webApp/)
- Run web dev server: `npm run start` (from webApp/)
- Run web tests with coverage: `npm test -- --coverage` (run from webApp/, view at webApp/coverage/index.html)

### End-to-End Tests
- Run E2E web tests: `npx playwright test` (from repo root)
- Run E2E mobile tests: `npm run test:mobile:android` or `npm run test:mobile:ios` (from repo root)

## Project Structure
- `/shared` - Kotlin Multiplatform shared code (Android, iOS, JS targets)
  - `commonMain/` - Domain models, repository interfaces, use cases ONLY (NO ViewModels, NO UI)
  - `commonMain/.../di/` - Koin modules for domain dependencies
  - `androidMain/`, `iosMain/`, `jsMain/` - Platform-specific implementations (expect/actual)
  - `commonTest/` - Unit tests (MUST achieve 80% coverage)
- `/composeApp` - Android app with Jetpack Compose UI + ViewModels
  - `androidMain/.../di/` - Koin modules (data + ViewModel)
  - `androidUnitTest/` - ViewModel unit tests (MUST achieve 80% coverage)
- `/webApp` - React TypeScript web app with hooks/state + consuming Kotlin/JS
  - `src/__tests__/` - Hook/state unit tests (MUST achieve 80% coverage)
- `/iosApp` - iOS Swift app with SwiftUI + ViewModels
  - `iosApp/DI/` - Koin initialization
  - `iosAppTests/ViewModels/` - ViewModel unit tests (MUST achieve 80% coverage)
- `/server` - Node.js/Express backend (NOT part of KMP)
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
- `/e2e-tests` - End-to-end tests (TypeScript)
  - `web/` - Playwright tests for web platform
    - `specs/` - Test specifications
    - `pages/` - Page Object Model
  - `mobile/` - Appium tests for Android/iOS
    - `specs/` - Test specifications
    - `screens/` - Screen Object Model

## Code Style
- Package: `com.intive.aifirst.petspot`
- Kotlin: Use explicit types for public APIs, prefer `val` over `var`
- TypeScript: React functional components with TypeScript types
- Backend: Express route handlers with TypeScript types, async/await for all async operations
- Imports: Group stdlib, then third-party, then project imports
- Naming: camelCase for variables/functions, PascalCase for classes/components
- Export Kotlin to JS: Use `@OptIn(ExperimentalJsExport::class)` and `@JsExport`
- Error handling: Use Result<T> for Kotlin shared code, try/catch with proper HTTP status codes for backend
- Target JVM 17 for Android, ES2015 for JS, Node.js v24 (LTS) for backend
- DI: Use Koin for dependency injection across KMP platforms (NOT used in /server backend)
- Architecture: Repository pattern (interfaces in shared, implementations in platforms)
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
- Async patterns:
  - Shared/Android: Kotlin Coroutines (`suspend` functions)
  - iOS: Swift Concurrency (`async`/`await` with `@MainActor`)
  - Web: Native `async`/`await` (no `.then()` chains)
  - Backend: Native `async`/`await` (Express with async handlers)
  - Prohibited: Combine, RxJava, RxSwift, RxJS, LiveData (for new code)
- Test Identifiers (MANDATORY):
  - Android: Use `Modifier.testTag("screen.element.action")` on all interactive composables
  - iOS: Use `.accessibilityIdentifier("screen.element.action")` on all interactive views
  - Web: Use `data-testid="screen.element.action"` on all interactive elements
  - Naming: `{screen}.{element}.{action}` (e.g., `petList.addButton.click`)
  - Lists: Include stable IDs (e.g., `petList.item.${pet.id}`)
- Documentation (MANDATORY):
  - All public APIs (classes, methods, properties) MUST have documentation
  - Use platform-native format: KDoc (Kotlin), SwiftDoc (Swift), JSDoc (TypeScript)
  - Keep concise and high-level (1-3 sentences: WHAT/WHY, not HOW)
  - Example: `/** Loads pets from repository and updates UI state. */`
  - Avoid obvious statements and implementation details

## Test Frameworks
- Shared module: Kotlin Test (multiplatform) + Koin Test
- Android ViewModels: JUnit 5 + Kotlin Test + Turbine (Flow testing)
- iOS ViewModels: XCTest with Swift Concurrency (async/await)
- Web hooks/state: Vitest + React Testing Library
- Backend API: Vitest + Supertest (unit + integration tests)
  - TDD Workflow (MANDATORY): Red-Green-Refactor cycle
    - RED: Write failing test first
    - GREEN: Write minimal code to pass test
    - REFACTOR: Improve code quality without changing behavior
  - Unit tests: `/src/services/__test__/`, `/src/lib/__test__/` (80% coverage)
  - Integration tests: `/src/__test__/` (REST API endpoints, 80% coverage)
- Web E2E: Playwright + TypeScript
- Mobile E2E: Appium + WebdriverIO + TypeScript
- Coverage tools: Kover (Kotlin shared + Android), Xcode Coverage (iOS), Vitest Coverage (Web + Backend)
- Test Convention: ALL tests MUST follow Given-When-Then (Arrange-Act-Assert) structure
  - Clearly separate setup (Given), action (When), verification (Then) phases
  - Use descriptive test names: Kotlin backticks, Swift camelCase_with_underscores, TypeScript strings
  - Mark test phases with comments in complex tests
  - Example (Kotlin): `@Test fun \`should return success when repository returns pets\`() = runTest { /* Given */ ... /* When */ ... /* Then */ ... }`
  - Example (Backend): `it('should return 200 when pet exists', async () => { /* Given */ ... /* When */ ... /* Then */ ... })`

## Dependencies
- Koin: Multiplatform dependency injection (used in KMP modules, NOT in /server backend)
  - `io.insert-koin:koin-core` - Shared module
  - `io.insert-koin:koin-android` - Android ViewModels
  - `io.insert-koin:koin-test` - Unit tests
- Backend (Node.js): Express, Knex, SQLite3, Vitest, Supertest
