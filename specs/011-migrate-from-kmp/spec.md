# Feature Specification: Complete KMP to Platform-Independent Migration

**Feature Branch**: `011-migrate-from-kmp`  
**Created**: 2025-11-21  
**Status**: Draft  
**Input**: User description: "I'd like to prepare the project for migrating from KMP into independent platform projects: Android, iOS and web. Current shared module should no longer exist."

## Clarifications

### Session 2025-11-21

- Q: How strict is the backend API compatibility requirement for field naming in domain models? → A: Domain models use platform conventions (Kotlin camelCase, Swift camelCase), serialization annotations handle conversion to backend format
- Q: Should the iOS use case (GetAnimalsUseCase) be migrated during this migration, or should iOS ViewModels be refactored to call repositories directly? → A: Per constitution, iOS ViewModels MUST call repositories directly; remove shared use case dependency instead of recreating a Swift use case layer
- Q: How should the Koin DomainModule from the shared module be handled during migration? → A: Create new domainModule in Android's DI directory, copy shared DomainModule bindings there
- Q: Should Android and iOS platforms be migrated in sequence or in parallel? → A: Sequential migration, iOS first (complete iOS migration fully), then Android
- Q: Should migration phases be committed and reviewed as separate PRs, or as a single large PR with multiple commits? → A: Single PR with multiple commits (one per phase), review once at end

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Platform Domain Models Independence (Priority: P1)

Developers need each platform to have its own domain models (Animal, Location, AnimalSpecies, etc.) implemented in platform-native languages and patterns, without importing from shared KMP module. This enables platforms to evolve independently.

**Why this priority**: Foundation for platform independence - domain models are used by all layers. Without independent models, no other migration can proceed. This unblocks use case and repository migration.

**Independent Test**: Can be fully tested by inspecting platform code and verifying domain models exist in platform-specific directories with no shared imports, and that platform builds succeed using local models.

**Acceptance Scenarios**:

1. **Given** Android domain models have been migrated, **When** developer inspects `/composeApp/src/androidMain/.../domain/models/`, **Then** all domain models (Animal, Location, AnimalSpecies, AnimalGender, AnimalStatus) exist as Kotlin data classes
2. **Given** iOS domain models have been migrated, **When** developer inspects `/iosApp/iosApp/Domain/Models/`, **Then** all domain models exist as Swift structs with equivalent properties
3. **Given** domain models are platform-local, **When** developer searches codebase for `import com.intive.aifirst.petspot.domain.models` (Android) or `import Shared` (iOS), **Then** no imports from shared module remain in domain layer code
4. **Given** platforms use local models, **When** developer runs platform builds, **Then** builds succeed with local model references
5. **Given** backend contracts documented in `specs/011-migrate-from-kmp/contracts/`, **When** developers compare platform models to those contracts, **Then** field names, types, and nullability match across Android/iOS implementations

---

### User Story 2 - Platform Repository Independence (Priority: P1)

Developers need repository interfaces implemented in each platform's codebase so business logic can evolve independently. Android retains its use case layer, while iOS adheres to the constitutional rule of calling repositories directly from ViewModels (no use cases).

**Why this priority**: Business logic independence enables platforms to adapt to platform-specific needs. Required before shared module can be removed.

**Independent Test**: Can be fully tested by verifying repository interfaces exist in platform directories with platform-native patterns (Android + iOS) and that Android-specific use cases remain local while ViewModels/coordinators consume the correct contracts.

**Acceptance Scenarios**:

1. **Given** Android repositories migrated, **When** developer inspects `/composeApp/src/androidMain/.../domain/repositories/`, **Then** AnimalRepository interface and use cases exist using Kotlin coroutines
2. **Given** iOS repositories migrated, **When** developer inspects `/iosApp/iosApp/Domain/Repositories/`, **Then** AnimalRepository protocol exists using Swift async/await and ViewModels reference that protocol directly
3. **Given** Android use cases migrated, **When** ViewModels reference use cases, **Then** they import from platform-local packages, not shared module
4. **Given** platforms use local business logic, **When** developer runs unit tests, **Then** all tests pass using platform-local implementations

---

### User Story 3 - Clean Platform Build Without Shared Module (Priority: P1)

Developers need to build each platform (Android, iOS, Web) independently without any references to the shared KMP module after content migration completes. This ensures the platform-independent architecture defined in the constitution is fully implemented.

**Why this priority**: Final validation that migration succeeded - clean builds prove platform independence is achieved. This is the completion criteria for the entire migration.

**Independent Test**: Can be fully tested by running platform-specific build commands (`./gradlew :composeApp:assembleDebug`, Xcode build, `npm run build` from webApp) and verifying they succeed without shared module errors.

**Acceptance Scenarios**:

1. **Given** the shared module has been removed from the project, **When** developer runs `./gradlew :composeApp:assembleDebug`, **Then** Android app builds successfully without any shared module dependency errors
2. **Given** the shared module no longer exists, **When** developer opens iosApp in Xcode and builds, **Then** iOS app builds successfully without any KMP framework references
3. **Given** the shared module directory has been deleted, **When** developer runs `npm run build` from webApp directory, **Then** web app builds successfully without any shared module imports

---

### User Story 4 - Platform Test Execution (Priority: P1)

Developers need to run platform-specific unit tests to verify business logic and presentation logic work correctly after migration. All tests must pass with 80%+ coverage maintained.

**Why this priority**: Testing is essential validation that the migration didn't break functionality. Without passing tests, we cannot trust the system works.

**Independent Test**: Can be fully tested by running platform-specific test commands and verifying they pass with expected coverage thresholds.

**Acceptance Scenarios**:

1. **Given** Android code no longer depends on shared module, **When** developer runs `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`, **Then** all Android unit tests pass and coverage report shows 80%+ coverage
2. **Given** iOS code has been migrated from shared module, **When** developer runs `xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES`, **Then** all iOS unit tests pass and coverage meets 80%+ threshold
3. **Given** web code is independent, **When** developer runs `npm test -- --coverage` from webApp, **Then** all web unit tests pass and coverage report shows 80%+ coverage

---

### User Story 5 - Clean Development Environment (Priority: P2)

Developers setting up a fresh development environment should not encounter any KMP-related dependencies or build artifacts. The project structure should reflect the platform-independent architecture without legacy remnants.

**Why this priority**: Prevents confusion for new developers and ensures clean onboarding. While important for developer experience, it doesn't block immediate functionality.

**Independent Test**: Can be fully tested by cloning the repository fresh, running setup commands for each platform, and verifying no KMP-related errors or warnings appear.

**Acceptance Scenarios**:

1. **Given** a fresh clone of the repository, **When** developer runs `./gradlew clean build`, **Then** Gradle sync completes without mentioning shared module or KMP plugins
2. **Given** a fresh project setup, **When** developer opens project in IDE (Android Studio, Xcode, VS Code), **Then** IDE indexing completes without errors and no shared module references appear in project structure
3. **Given** clean build environment, **When** developer inspects `settings.gradle.kts` and root `build.gradle.kts`, **Then** no shared module inclusion or KMP plugin configuration exists

---

### User Story 6 - CI/CD Pipeline Execution (Priority: P2)

Continuous Integration pipeline should build and test all platforms successfully without any KMP-related steps. The pipeline validates that the platform-independent architecture is production-ready.

**Why this priority**: Ensures automated quality gates work correctly. Important for team velocity and production deployments, but can be validated manually initially.

**Independent Test**: Can be fully tested by triggering CI/CD pipeline and verifying all build, test, and lint jobs pass without KMP-related steps.

**Acceptance Scenarios**:

1. **Given** CI/CD pipeline configured for platform-independent architecture, **When** pipeline runs for Android platform, **Then** Android build, test, and lint jobs pass without any shared module steps
2. **Given** shared module has been removed, **When** pipeline runs for iOS platform, **Then** iOS build and test jobs pass without KMP framework build steps
3. **Given** web platform is independent, **When** pipeline runs for web platform, **Then** web build, test, and lint jobs pass without attempting to import shared module artifacts

---

### Edge Cases

- What happens when Kotlin models have features not available in Swift? (Solution: Adapt to Swift idioms - e.g., sealed classes become enums with associated values)
- How do we handle code duplication between Android and iOS models going forward? (Solution: Accept duplication as architectural trade-off; synchronize via API contracts and documentation, not shared code)
- What if platform developers modify migrated models differently after migration? (Solution: Platforms may diverge intentionally - use backend API as source of truth, document platform-specific model extensions)
- What happens when a developer has cached KMP artifacts from previous builds? (Solution: Run clean builds per platform - `./gradlew clean`, clear Xcode derived data)
- How does the system handle existing git branches that still reference the shared module? (Solution: Merge or rebase from main after migration is complete, resolve conflicts by updating imports to platform-local packages)
- What if iOS uses shared Kotlin enums that compile to Objective-C enums? (Solution: Reimplement as Swift enums with same case names, update switch statements)
- How do we ensure no platform accidentally reintroduces shared module dependency? (Solution: Remove from settings.gradle.kts, add CI checks that fail if shared imports detected, code review guidelines)

## Requirements *(mandatory)*

### Functional Requirements

#### Content Migration Requirements

- **FR-001**: Android MUST have local copies of all domain models from shared module in `/composeApp/src/androidMain/.../domain/models/` (Animal, Location, AnimalSpecies, AnimalGender, AnimalStatus)
- **FR-002**: iOS MUST have Swift implementations of all domain models from shared module in `/iosApp/iosApp/Domain/Models/` with equivalent properties and behavior
- **FR-003**: Android MUST have local copies of repository interfaces in `/composeApp/src/androidMain/.../domain/repositories/` (AnimalRepository)
- **FR-004**: iOS MUST have Swift protocol definitions of repository interfaces in `/iosApp/iosApp/Domain/Repositories/`
- **FR-005**: Android MUST have local copies of use cases in `/composeApp/src/androidMain/.../domain/usecases/` (GetAnimalsUseCase)
- **FR-006**: iOS MUST NOT introduce a use case layer; ViewModels MUST call local repository protocols directly after removing `Shared` dependencies, and any prior `GetAnimalsUseCase` references MUST be eliminated
- **FR-007**: Android code MUST NOT import from `com.intive.aifirst.petspot.shared.*` or shared module packages
- **FR-008**: iOS code MUST NOT import `Shared` framework from KMP
- **FR-009**: Mock/test fixture data (MockAnimalData) MUST be migrated to platform-specific test directories or hardcoded in implementations
- **FR-009a**: Android MUST migrate Koin DomainModule from shared module to `/composeApp/src/androidMain/.../di/DomainModule.kt` with domain layer bindings (use cases, repository interfaces if needed)
- **FR-010**: Platform domain models MUST maintain structural compatibility with backend API contracts (equivalent types, nullability); platforms MAY use platform naming conventions (Kotlin camelCase, Swift camelCase) with serialization annotations handling conversion to/from backend format

#### Build Configuration Requirements

- **FR-011**: System MUST NOT include `shared` module in `settings.gradle.kts` include list
- **FR-012**: System MUST NOT reference `projects.shared` or `:shared` in any `build.gradle.kts` files
- **FR-013**: Android platform MUST compile and build successfully without `shared` module dependency in `composeApp/build.gradle.kts`
- **FR-014**: iOS platform MUST build successfully without importing any KMP-generated framework (`Shared.framework`)
- **FR-015**: iOS Xcode project MUST NOT reference KMP framework in build phases or linked frameworks
- **FR-016**: Root `build.gradle.kts` MUST NOT contain KMP plugin configuration (`kotlinMultiplatform`)
- **FR-017**: Gradle version catalog (`gradle/libs.versions.toml`) SHOULD remove unused KMP-related plugin aliases (optional cleanup)

#### Validation Requirements

- **FR-018**: System MUST maintain 80%+ test coverage on all platforms after migration (Android, iOS, Web, Backend)
- **FR-019**: All platform-specific unit tests MUST pass after content migration and shared module removal
- **FR-020**: All E2E tests MUST pass after content migration and shared module removal (web via Playwright, mobile via Appium)
- **FR-021**: Build artifacts directory (`/shared/build`) MUST be removed from repository
- **FR-022**: KMP source sets (`commonMain`, `iosMain`, `jsMain`, `androidMain` within shared module) MUST be deleted
- **FR-023**: Shared module directory (`/shared`) MUST be completely removed from repository after content migration completes

### Key Entities *(not applicable - infrastructure migration)*

This feature is infrastructure-focused and does not introduce or modify domain entities.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: All domain models exist independently in each platform (5 models × 2 platforms = 10 total implementations: Android Kotlin + iOS Swift)
- **SC-002**: All repository interfaces exist independently in each platform (1 interface × 2 platforms = 2 total implementations)
- **SC-003**: Android build completes in same time or faster compared to pre-migration baseline (no KMP overhead)
- **SC-004**: iOS build completes without KMP framework compilation step, reducing build time by estimated 10-20%
- **SC-005**: All platform-specific unit test suites pass with 80%+ coverage maintained across Android, iOS, Web, and Backend
- **SC-006**: Gradle sync time improves by 10-30% due to removal of KMP module configuration and source set processing
- **SC-007**: Repository size reduces by amount of shared module build artifacts and KMP-generated binaries (estimated 50-100MB reduction)
- **SC-008**: Zero references to shared module remain in platform code (verified by searching for `import Shared`, `import com.intive.aifirst.petspot.domain.*` from shared)
- **SC-009**: Zero references to `shared` module in build configuration (settings.gradle.kts, build.gradle.kts files)
- **SC-010**: All existing features continue to function correctly (validated by E2E test suite passing with same results as pre-migration baseline)
- **SC-011**: Platform domain models maintain API compatibility (equivalent field structures, types, nullability) across Android/iOS for backend integration; platforms use native naming conventions with serialization layer handling format conversion

## Assumptions

- **A-001**: Android and iOS platforms currently import and use domain models, repositories, and use cases from shared KMP module (verified as TRUE)
- **A-002**: Web platform already has independent implementation and does NOT use shared module (verified as TRUE)
- **A-003**: Shared module contains domain layer code (models, repositories, use cases) that needs migration to platforms
- **A-004**: No active feature development is occurring in the shared module during migration period
- **A-005**: All developers have been notified of the migration timeline and understand the platform-independent architecture goal
- **A-006**: CI/CD pipeline can be updated to remove KMP build steps without requiring infrastructure team approval
- **A-007**: No external dependencies or published artifacts reference the shared module
- **A-008**: Platform-specific implementations (e.g., AnimalRepositoryImpl) already exist in platforms and only need import updates
- **A-009**: Mock data in iOS (hardcoded animals array) can remain as-is or be replaced with platform-specific fixtures
- **A-010**: Backend API contracts are stable and will serve as source of truth for model field structures/types across platforms; platforms use native naming conventions with serialization handling format conversion

## Dependencies

- **D-001**: Shared module content inventory MUST be completed before migration (list all models, interfaces, use cases requiring migration)
- **D-002**: Platform-specific repository implementations MUST already exist (AnimalRepositoryImpl in both Android and iOS - verified as TRUE)
- **D-003**: All platform-specific tests MUST be in place and passing before content migration begins
- **D-004**: Constitution v2.0.0+ MUST be adopted and followed by team
- **D-005**: Documentation updates in `.specify/memory/constitution.md` MUST reflect platform-independent architecture (already done)
- **D-006**: Developer communication MUST occur before migration starts (announce timeline, freeze shared module changes)
- **D-007**: Backend API contracts MUST be documented to serve as source of truth for model field structures and types

## Kotlin to Swift Conversion Guidelines

For iOS migration, the following conversion patterns apply:

**Field Naming Convention**: Both platforms use their native naming conventions (camelCase for Kotlin/Swift). Serialization annotations (e.g., `@SerializedName` in Kotlin, `CodingKeys` in Swift) handle conversion to/from backend API format when needed.

### Data Classes → Structs
```kotlin
// Kotlin (shared)
data class Animal(
    val id: String,
    val name: String,
    val species: AnimalSpecies
)
```
→
```swift
// Swift (iosApp)
struct Animal {
    let id: String
    let name: String
    let species: AnimalSpecies
}
```

### Sealed Classes/Enums → Swift Enums
```kotlin
// Kotlin
enum class AnimalGender {
    MALE, FEMALE, UNKNOWN
}
```
→
```swift
// Swift
enum AnimalGender {
    case male, female, unknown
}
```

### Repository Interfaces → Protocols
```kotlin
// Kotlin
interface AnimalRepository {
    suspend fun getAnimals(): List<Animal>
}
```
→
```swift
// Swift
protocol AnimalRepository {
    func getAnimals() async throws -> [Animal]
}
```

### Nullable Types → Optionals
```kotlin
val email: String?  // Kotlin nullable
```
→
```swift
let email: String?  // Swift optional
```

## Out of Scope

The following are explicitly NOT part of this migration:

- **OOS-001**: Rewriting platform-specific ViewModels, UI components, or presentation logic (only domain layer migrates)
- **OOS-002**: Refactoring existing platform implementations beyond necessary import updates
- **OOS-003**: Adding new features or functionality during migration
- **OOS-004**: Changing platform-specific architectural patterns (MVI for Android, MVVM-C for iOS remain as-is)
- **OOS-005**: Updating third-party dependencies or library versions (except removing KMP plugins)
- **OOS-006**: Performance optimization beyond build time improvements from removing KMP
- **OOS-007**: Changing backend API contracts or server implementation
- **OOS-008**: Migrating or refactoring platform-specific data layer (repositories implementations stay as-is)
- **OOS-009**: Creating new test fixtures beyond copying existing MockAnimalData
- **OOS-010**: Changing dependency injection framework or approach (Koin for Android, manual DI for iOS remain; only domain layer DI bindings are migrated)
- **OOS-011**: Refactoring iOS ViewModels to remove use case layer and call repositories directly (use cases migrated as transitional code for future refactoring)

## Risks & Mitigations

| Risk | Impact | Probability | Mitigation |
|------|--------|-------------|------------|
| Kotlin to Swift conversion introduces behavioral differences | High (app bugs) | Medium | Carefully translate Kotlin idioms to Swift equivalents; write unit tests for migrated models; cross-reference with Android implementation; use backend API as source of truth |
| Platform models diverge unintentionally during migration | High (API incompatibility) | Medium | Document exact field names/types required by backend; validate models against API contracts; create checklist comparing Android/iOS model properties |
| Missing imports discovered late in migration process | High (build breaks) | Low | Perform comprehensive import audit before Phase 5; search entire codebase for `import Shared` and shared package imports; test builds after each phase |
| Tests fail after migration due to missing test fixtures | High (quality loss) | Medium | Migrate MockAnimalData early; verify test coverage before each phase; run full test suites after each content migration; compare coverage reports against baseline |
| iOS enum conversion breaks switch statements | Medium (compilation errors) | Medium | Search for all switch statements on shared enums; update case names to match new Swift enum; compile and test after each enum migration |
| CI/CD pipeline breaks due to hardcoded shared module steps | Medium (deployment blocked) | Medium | Review pipeline config before migration; update pipeline in separate commit; test pipeline in non-production branch |
| Developers working on feature branches encounter merge conflicts | Medium (developer friction) | High | Communicate migration timeline; provide clear migration guide; freeze shared module changes; assist with merge conflict resolution; provide rebase instructions |
| Dependency injection breaks after removing shared domain module | Low (DI errors) | Low | Verify Koin modules reference local packages; test DI graph initialization; run integration tests |
| Git history becomes unclear after large changes | Low (historical reference) | Low | Document migration in commit message; tag each phase; preserve shared module in separate branch for reference |

## Documentation Updates Required

- **DOC-001**: Update root `README.md` to reflect platform-independent architecture and remove shared module references
- **DOC-002**: Update build instructions in platform-specific README files (if they reference shared module)
- **DOC-003**: Update developer onboarding documentation to remove KMP setup steps
- **DOC-004**: Create migration guide for developers with open feature branches (`docs/migration/kmp-removal.md`)
- **DOC-005**: Update CONTRIBUTING.md (if exists) to reflect platform-independent contribution workflow

## Migration Strategy

### Pre-Migration Validation

1. Verify all platforms build independently with current shared module dependency
2. Run full test suites (unit + E2E) and document baseline results
3. Audit all platform code for shared module imports:
   - Android: Search for `import com.intive.aifirst.petspot.domain.*` referencing shared
   - iOS: Search for `import Shared` in all Swift files
   - Document all files requiring import updates
4. Create inventory of shared module contents:
   - Domain models: Animal, Location, AnimalSpecies, AnimalGender, AnimalStatus
   - Repository interfaces: AnimalRepository, FakeAnimalRepository
   - Use cases: GetAnimalsUseCase
   - Test fixtures: MockAnimalData
   - DI module: DomainModule (Koin configuration)
5. Review CI/CD pipeline configuration and identify shared module build steps

### Migration Execution Order

**Strategy**: Sequential migration with iOS first, then Android. This allows validating iOS migration (which requires Kotlin-to-Swift translation) before proceeding to Android (simpler copy), learning from iOS experience to inform Android migration.

#### Phase 1: Migrate iOS Domain Models (Content + Translation)
1. Create Swift structs in `/iosApp/iosApp/Domain/Models/` equivalent to Kotlin models:
   - Animal.swift: Convert Kotlin data class to Swift struct with equivalent properties
   - Location.swift: Convert to Swift struct
   - AnimalSpecies.swift: Convert sealed class/enum to Swift enum
   - AnimalGender.swift: Convert to Swift enum
   - AnimalStatus.swift: Convert to Swift enum
2. Ensure Swift models use platform naming conventions (camelCase); add Codable conformance with CodingKeys for backend API compatibility if needed
3. Update iOS imports:
   - Remove `import Shared` from all files
   - Add imports for local model structs as needed
4. Update MockAnimalData usage:
   - iOS already has hardcoded mock array in AnimalRepositoryImpl
   - Verify mock data matches expected structure
5. Verify iOS builds successfully
6. Run iOS unit tests and verify they pass

#### Phase 2: Migrate iOS Repositories and Use Cases (Content + Translation)
1. Create Swift protocols in `/iosApp/iosApp/Domain/Repositories/`:
   - AnimalRepository.swift (protocol equivalent to Kotlin interface)
   - Implementation already exists in Features/AnimalList/Repositories/
2. Migrate use cases as transitional code:
   - Create GetAnimalsUseCase.swift in `/iosApp/iosApp/Domain/UseCases/` translating from Kotlin
   - Preserve current iOS ViewModel architecture (ViewModels continue using use case)
   - Document in code comments that iOS should refactor to call repositories directly in future (per constitution)
   - Update ViewModel imports to use local types
3. Update ServiceContainer (manual DI) to provide domain layer dependencies
4. Verify iOS builds and all tests pass

#### Phase 3: Migrate Android Domain Models (Content)
1. Copy domain models from `/shared/src/commonMain/.../domain/models/` to `/composeApp/src/androidMain/.../domain/models/`
   - Animal.kt, Location.kt, AnimalSpecies.kt, AnimalGender.kt, AnimalStatus.kt
   - Remove `@JsExport` annotations (Android doesn't need them)
   - Keep package structure consistent
   - Ensure models use platform naming conventions (camelCase); add @SerializedName annotations for backend API compatibility if needed
2. Update Android imports:
   - Find all files importing `com.intive.aifirst.petspot.domain.models.*`
   - Update to import from composeApp package structure
3. Verify Android builds successfully
4. Run Android unit tests and verify they pass

#### Phase 4: Migrate Android Repositories and Use Cases (Content)
1. Copy repository interfaces from `/shared/src/commonMain/.../domain/repositories/` to `/composeApp/src/androidMain/.../domain/repositories/`
   - AnimalRepository.kt (interface only, impl already exists in composeApp)
   - Copy FakeAnimalRepository.kt to test directory if needed
2. Copy use cases from `/shared/src/commonMain/.../domain/usecases/` to `/composeApp/src/androidMain/.../domain/usecases/`
   - GetAnimalsUseCase.kt
3. Copy test fixtures from `/shared/src/commonMain/.../domain/fixtures/` to `/composeApp/src/androidUnitTest/.../domain/fixtures/`
   - MockAnimalData.kt (used by tests and mocks)
4. Migrate Koin DomainModule:
   - Copy `/shared/src/commonMain/.../di/DomainModule.kt` to `/composeApp/src/androidMain/.../di/DomainModule.kt`
   - Update module to reference local domain classes (use cases, repository interfaces)
   - Register domainModule in PetSpotApplication alongside existing dataModule and viewModelModule
5. Update all Android imports for repositories, use cases, and DI modules
6. Verify Android builds and all tests pass

#### Phase 5: Remove Gradle Configuration
1. Remove `include(":shared")` from `settings.gradle.kts`
2. Remove `implementation(projects.shared)` from `composeApp/build.gradle.kts`
3. Verify Gradle sync succeeds
4. Run Android build and tests

#### Phase 6: Remove iOS Framework References
1. Remove KMP Shared framework from Xcode project:
   - Remove from "Link Binary With Libraries" build phase
   - Remove framework search paths referencing shared/build
   - Remove any KMP-generated framework references
2. Verify iOS build and tests pass

#### Phase 7: Delete Shared Module Directory
1. Delete `/shared` directory and all contents
2. Run full build across all platforms
3. Run all test suites (unit + E2E)
4. Verify no references to shared module remain in code

#### Phase 8: CI/CD Pipeline Updates
1. Update pipeline config to remove KMP build steps:
   - Remove shared module build job
   - Remove KMP framework generation for iOS
   - Remove shared module test execution
2. Trigger pipeline and verify all jobs pass
3. Verify build times and performance

#### Phase 9: Documentation and Cleanup
1. Update documentation per DOC-001 through DOC-005
2. Create migration guide for developers with open feature branches
3. Update platform README files to remove shared module references
4. Communicate completion to team
5. Optional: Remove unused KMP plugins from `gradle/libs.versions.toml`

### Rollback Plan

If critical issues are discovered:

**Before Phase 5 (while shared module still exists)**:
1. Revert file additions (copied models, repositories, use cases)
2. Restore original imports from shared module
3. Verify platforms build with shared module again

**After Phase 7 (shared module deleted)**:
1. Revert git commits back to before migration
2. Restore shared module directory from git history
3. Re-add module inclusion in `settings.gradle.kts`
4. Restore platform dependencies on shared module
5. Investigate root cause before re-attempting migration

**Mitigation Strategy**:
- Single PR workflow with multiple commits (one commit per phase)
- Commit each phase separately for granular rollback within PR
- Tag each phase commit for easy reference (e.g., `phase-1-ios-models`, `phase-2-ios-repos`)
- Review complete migration as single PR once all phases done
- Keep shared module branch alive for 2 weeks after PR merge
- Test thoroughly at each phase before committing next phase

## Notes

- This migration completes the architectural decision made in constitution v2.0.0 (breaking change from KMP to platform-independent)
- The migration includes TWO major phases: content duplication (copy code to platforms) + infrastructure removal (delete shared module)
- Migration executes sequentially: iOS first (Phases 1-2 with Kotlin-to-Swift translation), then Android (Phases 3-4 with straightforward copy)
- iOS-first strategy validates more complex translation work before simpler Android copy
- iOS content migration requires Kotlin-to-Swift translation (data classes → structs, sealed classes → enums, interfaces → protocols)
- Android content migration is straightforward (copy Kotlin files as-is, update imports)
- Code duplication across platforms is an intentional architectural trade-off for platform independence
- The shared module removal is a one-way migration - once complete, there is no plan to reintroduce KMP
- Each platform MUST maintain independent implementations of domain logic, following platform-native patterns
- Platforms MAY diverge after migration - backend API contracts serve as the integration point, not shared code
- The backend (`/server`) remains the single source of truth for business rules and data, consumed by all platforms via REST API
- Migration should be executed in a single coordinated effort within one PR, with separate commits per phase for granular rollback capability
- PR should be reviewed once after all phases complete, not per-phase, to validate migration as atomic unit
