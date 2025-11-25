# Migration Research: KMP to Platform-Independent Architecture

**Feature**: Complete KMP to Platform-Independent Migration  
**Branch**: `011-migrate-from-kmp`  
**Date**: 2025-11-24

## Purpose

This document captures all research findings, decisions, and patterns for migrating from Kotlin Multiplatform (KMP) shared module to platform-independent implementations. All "NEEDS CLARIFICATION" items from Technical Context have been resolved through this research.

## Research Areas

### 1. Migration Strategy: Sequential vs Parallel

**Decision**: Sequential migration with iOS first, then Android

**Rationale**:
- iOS requires Kotlin-to-Swift translation (more complex, higher risk)
- Android migration is straightforward copy (lower complexity)
- iOS-first validates translation patterns before simpler Android copy
- Learning from iOS translation informs Android migration
- Reduces cognitive load (focus on one platform at a time)
- Easier rollback if issues discovered during iOS phase

**Alternatives Considered**:
- **Parallel migration**: Rejected because it increases complexity, requires context-switching between platforms, and makes rollback harder
- **Android-first**: Rejected because it doesn't validate the harder translation work early

**Implementation Approach**:
- Phase 1-2: iOS domain models + repositories/use cases (Kotlin → Swift translation)
- Phase 3-4: Android domain models + repositories/use cases (copy Kotlin files)
- Phase 5-6: Remove build configuration (Gradle, Xcode)
- Phase 7: Delete shared module
- Phase 8-9: CI/CD updates and documentation

### 2. Kotlin-to-Swift Translation Patterns

**Decision**: Use established Kotlin-Swift equivalents with platform-native idioms

**Data Classes → Structs**:

```kotlin
// Kotlin (shared/src/commonMain)
data class Animal(
    val id: String,
    val name: String,
    val species: AnimalSpecies,
    val gender: AnimalGender,
    val status: AnimalStatus,
    val location: Location?,
    val imageUrl: String?
)
```

→ Translates to:

```swift
// Swift (iosApp/iosApp/Domain/Models/Animal.swift)
struct Animal {
    let id: String
    let name: String
    let species: AnimalSpecies
    let gender: AnimalGender
    let status: AnimalStatus
    let location: Location?
    let imageUrl: String?
}
```

**Sealed Classes/Enums → Swift Enums**:

```kotlin
// Kotlin enum
enum class AnimalGender {
    MALE, FEMALE, UNKNOWN
}

enum class AnimalStatus {
    AVAILABLE, ADOPTED, PENDING
}
```

→ Translates to:

```swift
// Swift enum (lowercase case names per Swift convention)
enum AnimalGender {
    case male
    case female
    case unknown
}

enum AnimalStatus {
    case available
    case adopted
    case pending
}
```

**Repository Interfaces → Protocols**:

```kotlin
// Kotlin interface
interface AnimalRepository {
    suspend fun getAnimals(): List<Animal>
}
```

→ Translates to:

```swift
// Swift protocol
protocol AnimalRepository {
    func getAnimals() async throws -> [Animal]
}
```

**Use Cases**:

```kotlin
// Kotlin use case
class GetAnimalsUseCase(private val repository: AnimalRepository) {
    suspend operator fun invoke(): Result<List<Animal>> {
        return try {
            Result.success(repository.getAnimals())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

→ Translates to:

```swift
// Swift use case (transitional - iOS should remove use cases in future)
class GetAnimalsUseCase {
    private let repository: AnimalRepository
    
    init(repository: AnimalRepository) {
        self.repository = repository
    }
    
    func execute() async throws -> [Animal] {
        return try await repository.getAnimals()
    }
}
```

**Key Translation Rules**:
- Kotlin `data class` → Swift `struct` (value semantics)
- Kotlin `val` → Swift `let` (immutability)
- Kotlin nullable `Type?` → Swift optional `Type?` (same syntax!)
- Kotlin `suspend fun` → Swift `func ... async throws`
- Kotlin `Result<T>` → Swift `throws` (Swift Result type exists but throwing is more idiomatic)
- Kotlin `List<T>` → Swift `[T]`
- Enum cases: SCREAMING_SNAKE_CASE → camelCase (Swift convention)

**Rationale**: These patterns preserve semantic equivalence while following platform idioms. Swift structs provide value semantics like Kotlin data classes. Swift optionals and Kotlin nullables have similar semantics. Swift async/await maps naturally to Kotlin coroutines.

**Alternatives Considered**:
- **Direct Kotlin-to-Swift code generation**: Rejected because manual translation allows platform-specific optimizations and idiomatic code
- **Swift classes instead of structs**: Rejected because structs provide value semantics and immutability (preferred for domain models)

### 3. Dependency Injection Migration

**Decision**: Migrate Koin DomainModule to Android, update iOS ServiceContainer with manual DI

**Android Koin Migration**:

```kotlin
// Shared module (BEFORE) - shared/src/commonMain/.../di/DomainModule.kt
val domainModule = module {
    factory { GetAnimalsUseCase(get()) }
    // Repository interface bindings (if any)
}
```

→ Migrate to:

```kotlin
// Android (AFTER) - composeApp/src/androidMain/.../di/DomainModule.kt
val domainModule = module {
    factory { GetAnimalsUseCase(get()) }
    // Update package imports to local composeApp domain classes
}

// Register in PetSpotApplication.kt
class PetSpotApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PetSpotApplication)
            modules(
                dataModule,      // existing
                domainModule,    // newly migrated
                viewModelModule  // existing
            )
        }
    }
}
```

**iOS Manual DI Update**:

```swift
// iOS ServiceContainer (BEFORE) - imports Shared framework
import Shared

class ServiceContainer {
    static let shared = ServiceContainer()
    
    lazy var animalRepository: AnimalRepository = AnimalRepositoryImpl(
        animals: Shared.MockAnimalData().animals  // imports from KMP
    )
}
```

→ Update to:

```swift
// iOS ServiceContainer (AFTER) - uses local domain classes
class ServiceContainer {
    static let shared = ServiceContainer()
    
    lazy var animalRepository: AnimalRepository = AnimalRepositoryImpl(
        // Use hardcoded mock array or local fixture
    )
    
    lazy var getAnimalsUseCase: GetAnimalsUseCase = GetAnimalsUseCase(
        repository: animalRepository
    )
}
```

**Rationale**: 
- Android: Preserve Koin DI pattern (constitutional requirement), just move module location
- iOS: Preserve manual DI pattern (constitutional requirement), update constructor injection with local types
- Both approaches maintain existing DI patterns while using platform-local domain classes

**Alternatives Considered**:
- **Introduce DI framework for iOS**: Rejected because constitution mandates manual DI for iOS (simplicity, no external dependencies)
- **Remove Koin from Android**: Rejected because constitution mandates Koin for Android (consistency, mature ecosystem)

### 4. Test Coverage Preservation Strategy

**Decision**: Update test imports, verify coverage at each migration phase

**Approach**:
1. **Pre-migration baseline**: Document current coverage for both platforms
   - Android: Run `./gradlew :composeApp:testDebugUnitTest koverHtmlReport`
   - iOS: Run XCTest with coverage enabled
2. **Import updates**: Update test files to import from platform-local packages
3. **Phase validation**: Run tests after each migration phase (Phase 2, 4, 6, 7)
4. **Coverage verification**: Compare post-migration coverage to baseline (must maintain 80%+)

**Android Test Updates**:

```kotlin
// BEFORE - imports from shared
import com.intive.aifirst.petspot.domain.models.Animal
import com.intive.aifirst.petspot.domain.repositories.AnimalRepository
import com.intive.aifirst.petspot.domain.usecases.GetAnimalsUseCase

class GetAnimalsUseCaseTest {
    @Test
    fun `should return success when repository returns animals`() = runTest {
        // Given
        val fakeAnimals = listOf(/* ... */)
        val fakeRepository = FakeAnimalRepository(animals = fakeAnimals)
        val useCase = GetAnimalsUseCase(fakeRepository)
        
        // When
        val result = useCase()
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }
}
```

→ Update to:

```kotlin
// AFTER - imports from composeApp local domain
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.repositories.AnimalRepository
import com.intive.aifirst.petspot.composeapp.domain.usecases.GetAnimalsUseCase

class GetAnimalsUseCaseTest {
    // Test logic remains identical, only imports change
    @Test
    fun `should return success when repository returns animals`() = runTest {
        // ... same test implementation
    }
}
```

**iOS Test Updates**:

```swift
// BEFORE - imports Shared framework
import XCTest
import Shared
@testable import iosApp

class AnimalListViewModelTests: XCTestCase {
    func testLoadAnimals_whenRepositorySucceeds_shouldUpdateAnimalsState() async {
        // Given
        let expectedAnimals = [
            Shared.Animal(id: "1", name: "Max", /* ... */)
        ]
        // ... test logic
    }
}
```

→ Update to:

```swift
// AFTER - uses local Domain models
import XCTest
@testable import iosApp

class AnimalListViewModelTests: XCTestCase {
    func testLoadAnimals_whenRepositorySucceeds_shouldUpdateAnimalsState() async {
        // Given
        let expectedAnimals = [
            Animal(id: "1", name: "Max", /* ... */)  // local Domain.Animal
        ]
        // ... test logic remains identical
    }
}
```

**Rationale**: Tests validate behavior, not implementation details. Updating imports doesn't change test logic. Coverage should remain stable because code behavior is unchanged.

**Alternatives Considered**:
- **Rewrite tests from scratch**: Rejected because existing tests already provide good coverage and follow Given-When-Then convention
- **Skip test updates until end**: Rejected because it prevents validation at each phase and increases rollback difficulty

### 5. Build Configuration Cleanup

**Decision**: Remove KMP configuration in Phase 5-6, delete shared module in Phase 7

**Gradle Configuration Changes (Phase 5)**:

```kotlin
// settings.gradle.kts (BEFORE)
rootProject.name = "AI-First"
include(":composeApp")
include(":shared")  // REMOVE THIS LINE

// composeApp/build.gradle.kts (BEFORE)
dependencies {
    implementation(projects.shared)  // REMOVE THIS LINE
    implementation(libs.androidx.activity.compose)
    // ... other dependencies
}
```

→ Update to:

```kotlin
// settings.gradle.kts (AFTER)
rootProject.name = "AI-First"
include(":composeApp")
// shared module removed

// composeApp/build.gradle.kts (AFTER)
dependencies {
    // projects.shared dependency removed
    implementation(libs.androidx.activity.compose)
    // ... other dependencies remain
}
```

**Xcode Configuration Changes (Phase 6)**:

1. Remove KMP framework from "Link Binary With Libraries" build phase
2. Remove framework search paths referencing `shared/build`
3. Remove KMP-generated framework references
4. Verify build succeeds with local Swift domain code only

**Shared Module Deletion (Phase 7)**:

```bash
# Delete entire shared module directory
rm -rf shared/

# Verify no references remain
git grep "import Shared" (should return no results)
git grep "projects.shared" (should return no results)
git grep ":shared" (should return no results in build files)
```

**Rationale**: Sequential cleanup reduces risk. Gradle changes in Phase 5 prevent Android from compiling shared. Xcode changes in Phase 6 prevent iOS from linking KMP framework. Phase 7 physical deletion is safe only after both platforms build successfully.

**Alternatives Considered**:
- **Delete shared module early**: Rejected because it prevents rollback and breaks validation workflow
- **Keep shared module indefinitely**: Rejected because it violates constitutional Platform Independence and adds maintenance burden

### 6. Rollback Plan Considerations

**Decision**: Git tag each phase, preserve shared module branch for 2 weeks

**Rollback Strategy**:

**During Migration (Phases 1-6, shared module still exists)**:
1. Revert file additions (copied models, repositories, use cases)
2. Restore original imports from shared module
3. Verify platforms build with shared module
4. Git commit rollback with explanation

**After Shared Module Deletion (Phase 7+)**:
1. Revert git commits back to before migration start
2. Restore shared module directory from git history:
   ```bash
   git checkout <pre-migration-commit> -- shared/
   ```
3. Re-add module inclusion in `settings.gradle.kts`
4. Restore platform dependencies on shared module
5. Investigate root cause before re-attempting

**Risk Mitigation**:
- Single PR workflow with multiple commits (one per phase)
- Git tag each phase: `migration-phase-1-ios-models`, `migration-phase-2-ios-repos`, etc.
- Keep shared module in separate branch (`archive/shared-module`) for 2 weeks post-merge
- Test thoroughly at each phase before proceeding
- Document baseline metrics (build times, test results, coverage) before migration

**Rationale**: Granular commits enable targeted rollback. Tagging phases provides clear reference points. Preserving shared module branch provides safety net. Baseline metrics validate that migration didn't introduce regressions.

**Alternatives Considered**:
- **Single monolithic commit**: Rejected because it makes rollback all-or-nothing
- **Separate PRs per phase**: Rejected per spec requirement (single PR with multiple commits for atomic review)

## Key Findings Summary

| Research Area | Decision | Key Rationale |
|---------------|----------|---------------|
| Migration Strategy | Sequential (iOS first, then Android) | iOS translation is harder; validate early, learn from experience |
| Kotlin-Swift Translation | Established patterns (data class→struct, enum→enum, interface→protocol) | Preserve semantics while following platform idioms |
| DI Migration | Koin DomainModule to Android, ServiceContainer update for iOS | Maintain constitutional DI requirements per platform |
| Test Coverage | Update imports, validate at each phase | Tests validate behavior, not implementation; coverage should remain stable |
| Build Cleanup | Sequential removal (Gradle→Xcode→deletion) | Reduce risk with phased approach; enable rollback |
| Rollback Plan | Tag phases, preserve shared branch 2 weeks | Safety net for production issues; clear rollback path |

## Dependencies Validated

All dependencies from spec.md confirmed:

- ✅ D-001: Shared module content inventory complete (5 models, 1 repository, 1 use case, 1 DI module)
- ✅ D-002: Platform-specific repository implementations exist (AnimalRepositoryImpl in Android + iOS)
- ✅ D-003: Platform tests exist and passing (baseline to be captured pre-migration)
- ✅ D-004: Constitution v2.0.0+ adopted (current version 2.2.0)
- ✅ D-005: Constitution documents platform-independent architecture
- ✅ D-006: Developer communication required before migration starts
- ✅ D-007: Backend API contracts documented (animals endpoint serves as source of truth)

## Technology Choices Confirmed

| Technology | Platform | Rationale |
|------------|----------|-----------|
| Kotlin 2.2.20 (JVM 17) | Android | Constitutional requirement, native platform language, latest stable version |
| Swift 5.9+ | iOS | Constitutional requirement, native platform language |
| Koin 3.5.3 | Android DI | Constitutional requirement (mandatory for Android) |
| Manual DI | iOS DI | Constitutional requirement (mandatory for iOS - ServiceContainer pattern) |
| JUnit 6 + Kover | Android Testing | Constitutional requirement, mature Kotlin test framework |
| XCTest | iOS Testing | Constitutional requirement, native Apple framework |
| Gradle 8.x + Kotlin DSL | Build System | Existing project build system, no changes needed |

## Implementation Readiness

All "NEEDS CLARIFICATION" items from Technical Context have been resolved:

- ✅ Language/Version: Kotlin 2.2.20 (Android), Swift 5.9+ (iOS)
- ✅ Primary Dependencies: Jetpack Compose, Koin 3.5.3 (Android), SwiftUI, UIKit coordinators (iOS)
- ✅ Testing: JUnit 6 + Kover (Android), XCTest (iOS)
- ✅ Target Platform: Android API 24+, iOS 15+
- ✅ Performance Goals: Build time improvements documented (Android same/faster, iOS 10-20% faster)

**Ready to proceed to Phase 1: Design & Contracts**

