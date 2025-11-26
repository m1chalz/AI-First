# Implementation Plan: Complete KMP to Platform-Independent Migration

**Branch**: `011-migrate-from-kmp` | **Date**: 2025-11-24 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/011-migrate-from-kmp/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

This feature completes the migration from Kotlin Multiplatform (KMP) architecture to platform-independent implementations. The primary requirement is to eliminate the shared KMP module by migrating all domain models, repository interfaces, Android use cases, and DI modules to platform-specific code (Android Kotlin, iOS Swift). Migration follows a sequential approach: iOS first (with Kotlin-to-Swift translation), then Android (straightforward copy). After content migration, the shared module and all KMP build configuration will be removed. This fulfills the constitutional requirement for platform independence established in constitution v2.0.0 and includes explicit contract validation plus full-platform (Android, iOS, Web, Backend) coverage checks.

## Technical Context

**Language/Version**: 
- Android: Kotlin 2.2.20 (JVM 17)
- iOS: Swift 5.9+
- Gradle: 8.x with Kotlin DSL
- Kotlin Coroutines: 1.9.0
- Kotlin Serialization: 1.8.0

**Primary Dependencies**: 
- Android: Jetpack Compose, Koin 3.5.3 (DI), Kotlin Coroutines 1.9.0, Retrofit/Ktor (HTTP)
- iOS: SwiftUI, UIKit (coordinators), URLSession/Alamofire (HTTP)
- Build: Gradle Version Catalog (gradle/libs.versions.toml), AGP 8.11.2

**Storage**: N/A (infrastructure migration, no data storage changes)

**Testing**: 
- Android: JUnit 6 + Kotlin Test + Turbine + Kover (coverage)
- iOS: XCTest with Swift Concurrency + Xcode coverage
- Backend: Vitest + SuperTest with coverage enforced via `npm test -- --coverage` in `/server`
- Run commands documented in spec.md

**Target Platform**: 
- Android: API 24+ (Nougat)
- iOS: iOS 15+
- Multi-platform project structure

**Project Type**: Mobile (Android + iOS native apps)

**Performance Goals**: 
- Android build time: Same or faster than pre-migration baseline (no KMP overhead)
- iOS build time: 10-20% faster (removing KMP framework compilation)
- Gradle sync time: 10-30% improvement (removing KMP module processing)

**Constraints**: 
- Zero downtime: Migration must not break existing functionality
- Sequential migration: iOS first (Kotlin-to-Swift translation), then Android (copy)
- Single PR workflow: Multiple commits (one per phase), single review at end
- 80%+ test coverage maintained throughout migration
- Backend API serves as source of truth for model field structures

**Scale/Scope**: 
- 5 domain models to migrate: Animal, Location, AnimalSpecies, AnimalGender, AnimalStatus
- 1 repository interface: AnimalRepository
- 1 Android use case: GetAnimalsUseCase
- 1 DI module: DomainModule (Koin)
- Test fixtures: MockAnimalData
- 2 platforms affected: Android + iOS
- 9 migration phases documented in spec.md

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

> **Note**: This is an infrastructure migration to achieve Platform Independence. Web and Backend are already independent and unaffected. Focus is on Android and iOS migration from shared KMP module.

### Platform Architecture Compliance

- [x] **Platform Independence**: Each platform implements full stack independently
  - Android: Domain models, use cases, repositories will be migrated to `/composeApp/src/androidMain/.../domain/`
  - iOS: Domain models and repository protocols will be migrated to `/iosApp/iosApp/Domain/` while ViewModels continue calling repositories directly (no use cases per constitution)
  - Web: Already independent (not affected by this migration)
  - Backend: Already independent (not affected by this migration)
  - After migration: NO shared compiled code between platforms (shared module will be deleted)
  - **This migration ACHIEVES constitutional Platform Independence requirement**
  - Violation justification: _N/A - This migration implements the constitution_

- [x] **Android MVI Architecture**: N/A for infrastructure migration
  - Migration only affects domain layer (models, repositories, use cases)
  - Existing Android ViewModels and UI remain unchanged (already follow MVI)
  - Only import paths will be updated to reference local domain classes
  - Violation justification: _N/A - Presentation layer not modified_

- [x] **iOS MVVM-C Architecture**: N/A for infrastructure migration
  - Migration only affects domain layer (models and repository protocols; no new use cases introduced)
  - Existing iOS ViewModels and coordinators remain unchanged (already follow MVVM-C)
  - Only import statements will be updated (remove `import Shared`)
  - Violation justification: _N/A - Presentation layer not modified_

- [x] **Interface-Based Design**: Domain logic uses interfaces for repositories (per platform)
  - Android: AnimalRepository interface will be copied to `/composeApp/src/androidMain/.../domain/repositories/`
  - iOS: AnimalRepository protocol will be translated to `/iosApp/iosApp/Domain/Repositories/`
  - Web: Already independent (not affected)
  - Backend: Already independent (not affected)
  - Implementations already exist in platform-specific data/repositories (AnimalRepositoryImpl)
  - Use cases will reference migrated interfaces, not concrete implementations
  - Violation justification: _N/A - Compliant with interface-based design_

- [x] **Dependency Injection**: Plan includes DI setup for each platform
  - Android: Koin DomainModule will be migrated from shared to `/composeApp/src/androidMain/.../di/DomainModule.kt`
  - iOS: Manual DI in ServiceContainer will be updated to provide migrated domain dependencies
  - Web: Already independent (not affected)
  - Backend: Already independent (not affected)
  - Migration preserves existing DI patterns per platform (Koin for Android, manual for iOS)
  - Violation justification: _N/A - Compliant with constitutional DI requirements_

- [x] **80% Test Coverage - Platform-Specific**: Plan maintains coverage through migration
  - Android: Existing tests in `/composeApp/src/androidUnitTest/` will have imports updated, coverage maintained at 80%+
  - iOS: Existing tests in `/iosApp/iosAppTests/` will have imports updated, coverage maintained at 80%+
  - Web: Already independent (not affected)
  - Backend: Already independent (not affected)
  - Each migration phase includes test verification step
  - Pre-migration baseline will be documented
  - Violation justification: _N/A - Coverage will be maintained throughout migration_

- [x] **End-to-End Tests**: Existing E2E tests must pass after migration
  - Web: Existing Playwright tests in `/e2e-tests/web/specs/` must pass (web not affected by migration)
  - Mobile: Existing Appium tests in `/e2e-tests/mobile/specs/` must pass after Android/iOS migration
  - No new E2E tests required (infrastructure migration with no functional changes)
  - E2E validation in Phase 7 (after shared module deletion)
  - Violation justification: _N/A - Existing E2E tests sufficient for validation_

- [x] **Asynchronous Programming Standards**: N/A for infrastructure migration
  - Migration does not change async patterns (only moves code between modules)
  - Existing async patterns preserved: Kotlin Coroutines (Android), Swift Concurrency (iOS)
  - Violation justification: _N/A - No async pattern changes_

- [x] **Test Identifiers for UI Controls**: N/A for infrastructure migration
  - Migration does not modify UI layer
  - Existing test identifiers remain unchanged
  - Violation justification: _N/A - UI layer not modified_

- [x] **Public API Documentation**: Existing documentation will be preserved
  - Domain model documentation (KDoc, SwiftDoc) will be copied during migration
  - iOS translation will preserve intent of documentation with Swift-appropriate wording
  - No new documentation required (infrastructure migration)
  - Violation justification: _N/A - Documentation preserved from existing code_

- [x] **Given-When-Then Test Structure**: Existing test structure will be preserved
  - Migration updates test imports, not test structure
  - All existing tests already follow Given-When-Then convention
  - No new tests requiring structure definition
  - Violation justification: _N/A - Test structure not modified_

### Backend Architecture & Quality Standards (if `/server` affected)

- [x] **Backend Technology Stack**: N/A - Backend not affected by migration
  - Migration only affects Android and iOS platform code
  - Backend remains independent and unchanged
  - Violation justification: _N/A - /server module not affected_

- [x] **Backend Code Quality**: N/A - Backend not affected by migration
  - No backend code changes in this migration
  - Violation justification: _N/A - /server module not affected_

- [x] **Backend Dependency Management**: N/A - Backend not affected by migration
  - No backend dependency changes in this migration
  - Violation justification: _N/A - /server module not affected_

- [x] **Backend Directory Structure**: N/A - Backend not affected by migration
  - Backend directory structure remains unchanged
  - Violation justification: _N/A - /server module not affected_

- [x] **Backend TDD Workflow**: N/A - Backend not affected by migration
  - No backend implementation changes requiring TDD
  - Violation justification: _N/A - /server module not affected_

- [x] **Backend Testing Strategy**: Coverage verification required even though code is untouched
  - Run `npm test -- --coverage` in `/server` to confirm FR-018 remains satisfied post-migration
  - Violation justification: _N/A - No backend implementation changes, but we still re-run tests to prove coverage_

## Project Structure

### Documentation (this feature)

```text
specs/011-migrate-from-kmp/
├── spec.md              # Feature specification (already exists)
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (migration decisions and patterns)
├── data-model.md        # Phase 1 output (domain entities being migrated)
├── quickstart.md        # Phase 1 output (setup and validation instructions)
├── contracts/           # Phase 1 output (Kotlin-to-Swift mappings)
│   └── kotlin-swift-mapping.md
└── checklists/          # Existing checklists directory
    └── migration-checklist.md
```

### Source Code (repository root)

```text
# Android Platform (BEFORE migration)
composeApp/src/androidMain/.../
├── data/
│   └── repositories/
│       └── AnimalRepositoryImpl.kt (already exists, imports from shared)
├── presentation/
│   └── viewmodels/
│       └── AnimalListViewModel.kt (already exists, imports from shared)
└── di/
    ├── DataModule.kt (already exists)
    └── ViewModelModule.kt (already exists)

# Android Platform (AFTER migration - Phase 3-4)
composeApp/src/androidMain/.../
├── domain/
│   ├── models/
│   │   ├── Animal.kt (migrated from shared)
│   │   ├── Location.kt (migrated from shared)
│   │   ├── AnimalSpecies.kt (migrated from shared)
│   │   ├── AnimalGender.kt (migrated from shared)
│   │   └── AnimalStatus.kt (migrated from shared)
│   ├── repositories/
│   │   └── AnimalRepository.kt (migrated from shared)
│   └── usecases/
│       └── GetAnimalsUseCase.kt (migrated from shared)
├── data/
│   └── repositories/
│       └── AnimalRepositoryImpl.kt (imports updated to local domain)
├── presentation/
│   └── viewmodels/
│       └── AnimalListViewModel.kt (imports updated to local domain)
└── di/
    ├── DataModule.kt (existing)
    ├── DomainModule.kt (migrated from shared)
    └── ViewModelModule.kt (existing)

# iOS Platform (BEFORE migration)
iosApp/iosApp/
├── Features/AnimalList/
│   ├── Repositories/
│   │   └── AnimalRepositoryImpl.swift (already exists, imports Shared)
│   └── ViewModels/
│       └── AnimalListViewModel.swift (already exists, imports Shared)
└── DI/
    └── ServiceContainer.swift (already exists)

# iOS Platform (AFTER migration - Phase 1-2)
iosApp/iosApp/
├── Domain/
│   ├── Models/
│   │   ├── Animal.swift (translated from Kotlin)
│   │   ├── Location.swift (translated from Kotlin)
│   │   ├── AnimalSpecies.swift (translated from Kotlin)
│   │   ├── AnimalGender.swift (translated from Kotlin)
│   │   └── AnimalStatus.swift (translated from Kotlin)
│   ├── Repositories/
│   │   └── AnimalRepository.swift (translated from Kotlin)
├── Features/AnimalList/
│   ├── Repositories/
│   │   └── AnimalRepositoryImpl.swift (imports updated to local Domain)
│   └── ViewModels/
│       └── AnimalListViewModel.swift (imports updated to local Domain)
└── DI/
    └── ServiceContainer.swift (updated to provide domain dependencies)

# Shared Module (DELETED in Phase 7)
shared/
└── [ENTIRE DIRECTORY REMOVED]

# Test Directories (imports updated, coverage maintained)
composeApp/src/androidUnitTest/kotlin/.../
└── domain/ (tests updated with local imports)

iosApp/iosAppTests/
└── Features/ (tests updated with local imports)
```

**Contract Validation Artifacts**: `specs/011-migrate-from-kmp/contracts/` remains the canonical mapping reference, and verification notes will be captured in `specs/011-migrate-from-kmp/contract-validation.md` once Android/iOS parity reviews are complete.

**Structure Decision**: Multi-platform mobile architecture with independent platform implementations. Migration moves domain layer code from shared KMP module to platform-specific modules (Android in Kotlin, iOS in Swift). After migration, each platform has a complete domain layer with no cross-platform dependencies. The shared module is completely removed, achieving constitutional Platform Independence.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

**No violations** - This migration implements the constitutional requirement for Platform Independence. All Constitution Check items are compliant or N/A (infrastructure migration affecting only domain layer, not presentation/UI). The migration reduces architectural complexity by removing the shared KMP module and eliminating cross-platform build dependencies.
