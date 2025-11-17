# Implementation Plan: Koin Dependency Injection for KMP

**Branch**: `001-koin-kmp` | **Date**: 2025-11-17 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-koin-kmp/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implement Koin dependency injection framework across all KMP platforms (Android, iOS, Web) with a single centralized module containing all dependency definitions. This provides foundational infrastructure for managing dependencies in a clean, testable way without manual instantiation, enabling constructor and property injection for shared repositories, use cases, and platform-specific ViewModels.

## Technical Context

**Language/Version**: Kotlin 1.9+ (KMP), Swift 5.9+ (iOS), TypeScript/ES2015+ (Web)  
**Primary Dependencies**: Koin (io.insert-koin:koin-core for shared, koin-android for Android, koin-test for tests)  
**Storage**: N/A (DI infrastructure only)  
**Testing**: Kotlin Test (multiplatform) + Koin Test for shared, JUnit 5 for Android ViewModels, XCTest for iOS ViewModels, Vitest for Web hooks  
**Target Platform**: Android/JVM (API level varies), iOS 15+, Modern browsers (ES2015+)  
**Project Type**: Mobile + Web (KMP multiplatform)  
**Performance Goals**: DI container initialization must succeed without errors at application startup (no specific time limit required per spec clarifications)  
**Constraints**: Single centralized DI module per spec clarifications; singleton and factory scopes only in MVP (no advanced scoped dependencies)  
**Scale/Scope**: Foundation infrastructure for entire application; tested indirectly through component tests per spec clarifications

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

**✅ RE-EVALUATED AFTER PHASE 1 (2025-11-17)**: All constitution checks remain valid after design phase. Koin DI architecture aligns with all KMP principles. No violations detected.

### KMP Architecture Compliance

- [x] **Thin Shared Layer**: Feature design keeps `/shared` limited to domain models, repository interfaces, and use cases
  - No UI components in `/shared` - ✅ DI infrastructure only
  - No ViewModels in `/shared` - ✅ ViewModels will be in platform-specific modules
  - No platform-specific code in `commonMain` - ✅ Koin core is multiplatform
  - Violation justification: N/A - Fully compliant

- [x] **Native Presentation**: Each platform implements its own presentation layer
  - Android ViewModels in `/composeApp` - ✅ Android DI modules will inject ViewModels
  - iOS ViewModels in Swift in `/iosApp` - ✅ iOS will use Koin from shared or native DI
  - Web state management in React in `/webApp` - ✅ Web will consume shared Koin modules
  - Violation justification: N/A - This feature provides DI infrastructure for future ViewModels

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories
  - Repository interfaces in `/shared/src/commonMain/.../repositories/` - ✅ Koin will inject interfaces
  - Implementations in platform-specific modules - ✅ Platform modules will provide implementations
  - Use cases reference interfaces, not concrete implementations - ✅ DI enables this pattern
  - Violation justification: N/A - Fully compliant

- [x] **Dependency Injection**: Plan includes Koin setup for all platforms
  - Shared domain module defined in `/shared/src/commonMain/.../di/` - ✅ Core of this feature
  - Android DI modules in `/composeApp/src/androidMain/.../di/` - ✅ Planned
  - iOS Koin initialization in `/iosApp/iosApp/DI/` - ✅ Planned
  - Web DI setup (if applicable) in `/webApp/src/di/` - ✅ Planned
  - Violation justification: N/A - This IS the Koin implementation

- [x] **80% Test Coverage - Shared Module**: Plan includes unit tests for shared domain logic
  - Tests located in `/shared/src/commonTest` - ✅ Koin configuration tests
  - Coverage target: 80% line + branch coverage - ⚠️ EXCEPTION: Per spec clarifications "No dedicated tests - DI is tested indirectly through component tests"
  - Run command: `./gradlew :shared:test koverHtmlReport`
  - Tests use Koin Test for DI in tests - ✅ Planned for future component tests
  - Violation justification: Spec explicitly states DI configuration correctness is validated indirectly through component tests, not dedicated DI infrastructure tests. Future features adding repositories/use cases will achieve 80% coverage including DI setup.

- [x] **80% Test Coverage - ViewModels**: Plan includes unit tests for ViewModels on each platform
  - Android: Tests in `/composeApp/src/androidUnitTest/`, run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
  - iOS: Tests in `/iosApp/iosAppTests/ViewModels/`, run via XCTest
  - Web: Tests in `/webApp/src/__tests__/hooks/`, run `npm test -- --coverage`
  - Coverage target: 80% line + branch coverage per platform
  - Violation justification: N/A - This feature provides DI infrastructure. Future features adding ViewModels will achieve 80% coverage.

- [x] **End-to-End Tests**: Plan includes E2E tests for all user stories
  - Web: Playwright tests in `/e2e-tests/web/specs/koin-kmp.spec.ts`
  - Mobile: Appium tests in `/e2e-tests/mobile/specs/koin-kmp.spec.ts`
  - All tests written in TypeScript - ✅ Planned
  - Page Object Model / Screen Object Model used - ✅ Planned
  - Each user story has at least one E2E test - ⚠️ MODIFIED: E2E tests will verify DI initialization success on each platform (app starts without crashes)
  - Violation justification: E2E tests will be minimal smoke tests verifying DI initialization (app startup without DI errors). Full E2E coverage applies to user-facing features built on top of this DI infrastructure.

- [x] **Platform Independence**: Shared code uses expect/actual for platform dependencies
  - No direct UIKit/Android SDK/Browser API imports in `commonMain` - ✅ Koin core is pure Kotlin
  - Platform-specific implementations in `androidMain`, `iosMain`, `jsMain` - ✅ Koin has platform-specific artifacts
  - Repository implementations provided via DI, not expect/actual - ✅ This feature enables that pattern
  - Violation justification: N/A - Fully compliant

- [x] **Clear Contracts**: Repository interfaces and use cases have explicit APIs
  - Typed return values (`Result<T>`, sealed classes) - ✅ Koin provides type-safe DI
  - KDoc documentation for public APIs - ✅ Planned (Koin modules will be documented)
  - `@JsExport` for web consumption where needed - ✅ Planned for shared modules consumed by web
  - Violation justification: N/A - Fully compliant

- [x] **Asynchronous Programming Standards**: Plan uses correct async patterns per platform
  - Shared: Kotlin Coroutines with `suspend` functions - N/A for DI setup (synchronous initialization)
  - Android: Kotlin Coroutines (`viewModelScope`) + Flow for state - ✅ Future ViewModels will use this
  - iOS: Swift Concurrency (`async`/`await`) with `@MainActor` - ✅ Future ViewModels will use this
  - Web: Native `async`/`await` (no Promise chains) - ✅ Future components will use this
  - No Combine, RxJava, RxSwift, or callback-based patterns for new code - ✅ Compliant
  - Violation justification: N/A - DI initialization is synchronous; async patterns apply to injected components

- [x] **Test Identifiers for UI Controls**: Plan includes test identifiers for all interactive elements
  - Android: `testTag` modifier on all interactive composables - N/A (DI infrastructure, no UI)
  - iOS: `accessibilityIdentifier` modifier on all interactive views - N/A (DI infrastructure, no UI)
  - Web: `data-testid` attribute on all interactive elements - N/A (DI infrastructure, no UI)
  - Naming convention: `{screen}.{element}.{action}` (e.g., `petList.addButton.click`) - N/A
  - List items use stable IDs (e.g., `petList.item.${id}`) - N/A
  - Violation justification: N/A - This feature has no UI elements (infrastructure only)

- [x] **Public API Documentation**: Plan ensures all public APIs have documentation
  - Kotlin: KDoc format (`/** ... */`) - ✅ Planned for Koin modules
  - Swift: SwiftDoc format (`/// ...`) - ✅ Planned for iOS initialization helpers
  - TypeScript: JSDoc format (`/** ... */`) - ✅ Planned for Web DI setup
  - Documentation must be concise and high-level (1-3 sentences: WHAT/WHY, not HOW) - ✅ Committed
  - All public classes, methods, and properties documented - ✅ Committed + ADR documentation per spec
  - Violation justification: N/A - Fully compliant

- [x] **Given-When-Then Test Structure**: Plan ensures all tests follow Given-When-Then convention
  - Unit tests clearly separate setup (Given), action (When), verification (Then) - ✅ Future component tests will follow
  - ViewModel tests use Given-When-Then pattern with descriptive names - ✅ Future ViewModel tests will follow
  - E2E tests structure scenarios with Given-When-Then phases - ✅ Smoke tests will follow convention
  - Test names follow platform conventions (backticks for Kotlin, camelCase_with_underscores for Swift, descriptive strings for TypeScript) - ✅ Committed
  - Comments mark test phases in complex tests - ✅ Committed
  - Violation justification: N/A - Fully compliant

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - `/server` module not affected by this feature
  - Violation justification: This feature implements Koin DI for KMP platforms only (Android, iOS, Web client). The `/server` backend is a standalone Node.js/Express service and does not use Koin.

- [x] **Backend Code Quality**: N/A - `/server` module not affected by this feature
  - Violation justification: No changes to `/server` code in this feature.

- [x] **Backend Dependency Management**: N/A - `/server` module not affected by this feature
  - Violation justification: No changes to `/server/package.json` in this feature.

- [x] **Backend Directory Structure**: N/A - `/server` module not affected by this feature
  - Violation justification: No changes to `/server/src/` structure in this feature.

- [x] **Backend TDD Workflow**: N/A - `/server` module not affected by this feature
  - Violation justification: No backend implementation in this feature.

- [x] **Backend Testing Strategy**: N/A - `/server` module not affected by this feature
  - Violation justification: No backend tests in this feature.

## Project Structure

### Documentation (this feature)

```text
specs/001-koin-kmp/
├── spec.md              # Feature specification (input)
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output: Koin best practices, KMP DI patterns
├── data-model.md        # Phase 1 output: DI module structure and relationships
├── quickstart.md        # Phase 1 output: Developer guide for adding dependencies
├── contracts/           # Phase 1 output: Koin module API contracts
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
# Kotlin Multiplatform Project Structure (Koin DI Feature)

shared/
└── src/
    ├── commonMain/kotlin/com/intive/aifirst/petspot/
    │   └── di/
    │       └── DomainModule.kt       # Single centralized Koin module (NEW)
    └── commonTest/kotlin/com/intive/aifirst/petspot/
        └── di/
            └── KoinModuleTest.kt     # Koin module verification tests (NEW)

composeApp/
└── src/
    ├── androidMain/kotlin/com/intive/aifirst/petspot/
    │   ├── PetSpotApplication.kt     # Koin initialization in Application.onCreate() (MODIFIED)
    │   └── di/
    │       ├── DataModule.kt         # Android data layer module (NEW)
    │       └── ViewModelModule.kt    # Android ViewModel module (NEW)
    └── androidUnitTest/kotlin/com/intive/aifirst/petspot/
        └── di/
            └── KoinAndroidTest.kt    # Android Koin setup tests (NEW)

iosApp/
└── iosApp/
    ├── PetSpotApp.swift              # Koin initialization in @main entry (MODIFIED)
    └── DI/
        └── KoinInitializer.swift     # iOS Koin initialization helper (NEW)

webApp/
└── src/
    ├── di/
    │   └── koinSetup.ts              # Web Koin initialization (NEW)
    └── index.tsx                     # Koin initialization in app bootstrap (MODIFIED)

e2e-tests/
├── web/
│   └── specs/
│       └── koin-initialization.spec.ts    # Web DI smoke tests (NEW)
└── mobile/
    └── specs/
        └── koin-initialization.spec.ts    # Android/iOS DI smoke tests (NEW)

docs/
└── adr/
    └── 001-koin-dependency-injection.md   # Architecture Decision Record (NEW)

build.gradle.kts                       # Add Koin dependencies (MODIFIED)
libs.versions.toml                     # Add Koin version catalog (MODIFIED)
```

**Structure Decision**: This is a Kotlin Multiplatform mobile + web project. The feature adds Koin dependency injection infrastructure across all three platforms:

1. **Shared Module** (`/shared`): Contains the centralized domain Koin module (`DomainModule.kt`) that defines dependencies available to all platforms (future repositories, use cases).

2. **Android** (`/composeApp`): Android-specific DI modules (`DataModule`, `ViewModelModule`) and Koin initialization in `Application` class.

3. **iOS** (`/iosApp`): Swift Koin initialization helper that starts the shared Koin instance from Kotlin/Native.

4. **Web** (`/webApp`): TypeScript Koin setup consuming shared Koin modules via Kotlin/JS.

5. **E2E Tests** (`/e2e-tests`): Smoke tests verifying DI initialization on each platform (app starts without crashes).

6. **Documentation** (`/docs/adr`): Architecture Decision Record documenting the choice of Koin and DI strategy.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., 4th project] | [current need] | [why 3 projects insufficient] |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient] |
