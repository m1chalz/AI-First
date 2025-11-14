# PetSpot - Kotlin Multiplatform Project

**Architecture**: See `.specify/memory/constitution.md` for architectural principles (thin shared layer, native presentation, 80% test coverage)

## Build & Test Commands
- Build Android: `./gradlew :composeApp:assembleDebug`
- Build shared Kotlin/JS: `./gradlew :shared:jsBrowserDevelopmentLibraryDistribution`
- Build web app: `npm install && npm run build` (run from webApp/)
- Run web dev server: `npm run start` (from webApp/)
- Run all tests: `./gradlew test`
- Run shared tests with coverage: `./gradlew :shared:test koverHtmlReport` (view at shared/build/reports/kover/html/index.html)
- Run single test: `./gradlew :shared:cleanJsTest :shared:jsTest --tests "com.intive.aifirst.petspot.SharedCommonTest.example"`
- Run Android tests: `./gradlew :composeApp:testDebugUnitTest`
- Run Android ViewModel tests with coverage: `./gradlew :composeApp:testDebugUnitTest koverHtmlReport` (view at composeApp/build/reports/kover/html/index.html)
- Run web tests with coverage: `npm test -- --coverage` (run from webApp/, view at webApp/coverage/index.html)
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
- Imports: Group stdlib, then third-party, then project imports
- Naming: camelCase for variables/functions, PascalCase for classes/components
- Export Kotlin to JS: Use `@OptIn(ExperimentalJsExport::class)` and `@JsExport`
- Error handling: Use Result<T> for Kotlin shared code
- Target JVM 11 for Android, ES2015 for JS
- DI: Use Koin for dependency injection across all platforms
- Architecture: Repository pattern (interfaces in shared, implementations in platforms)
- Async patterns:
    - Shared/Android: Kotlin Coroutines (`suspend` functions)
    - iOS: Swift Concurrency (`async`/`await` with `@MainActor`)
    - Web: Native `async`/`await` (no `.then()` chains)
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
- Web E2E: Playwright + TypeScript
- Mobile E2E: Appium + WebdriverIO + TypeScript
- Coverage tools: Kover (Kotlin shared + Android), Xcode Coverage (iOS), Vitest Coverage (Web)

## Dependencies
- Koin: Multiplatform dependency injection
    - `io.insert-koin:koin-core` - Shared module
    - `io.insert-koin:koin-android` - Android ViewModels
    - `io.insert-koin:koin-test` - Unit tests
