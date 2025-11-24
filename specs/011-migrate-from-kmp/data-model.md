# Data Model: KMP to Platform-Independent Migration

**Feature**: Complete KMP to Platform-Independent Migration  
**Branch**: `011-migrate-from-kmp`  
**Date**: 2025-11-24

## Purpose

This document defines all domain entities being migrated from the shared Kotlin Multiplatform module to platform-specific implementations. Each entity will exist independently in Android (Kotlin) and iOS (Swift) after migration.

## Migration Scope

**Source**: `/shared/src/commonMain/.../domain/`  
**Android Target**: `/composeApp/src/androidMain/.../domain/`  
**iOS Target**: `/iosApp/iosApp/Domain/`

## Domain Entities

### 1. Animal

**Description**: Core domain entity representing an animal available for adoption.

**Shared Module (Kotlin)**:
```kotlin
package com.intive.aifirst.petspot.domain.models

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

**Android Migration**:
- **Location**: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/domain/models/Animal.kt`
- **Changes**: 
  - Remove `@JsExport` annotation (Android doesn't need it)
  - Update package to `composeapp.domain.models`
  - Preserve `data class` structure (Kotlin idiomatic)
  - Add `@SerializedName` annotations if needed for backend API compatibility

**iOS Translation**:
- **Location**: `/iosApp/iosApp/Domain/Models/Animal.swift`
- **Translation**:
  ```swift
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
- **Changes**:
  - Kotlin `data class` → Swift `struct` (value semantics)
  - Kotlin `val` → Swift `let` (immutability)
  - Optional types remain `?` (same syntax in both languages)
  - Add `Codable` conformance if needed for JSON serialization

**Fields**:
| Field | Type | Nullable | Description |
|-------|------|----------|-------------|
| id | String | No | Unique identifier for the animal |
| name | String | No | Animal's name |
| species | AnimalSpecies (enum) | No | Type of animal (dog, cat, etc.) |
| gender | AnimalGender (enum) | No | Animal's gender |
| status | AnimalStatus (enum) | No | Adoption status |
| location | Location | Yes | Animal's current location |
| imageUrl | String | Yes | URL to animal's photo |

**Business Rules**:
- `id` must be unique across all animals
- `name` cannot be empty
- Backend API serves as source of truth for field values

---

### 2. Location

**Description**: Represents a geographic location for an animal.

**Shared Module (Kotlin)**:
```kotlin
package com.intive.aifirst.petspot.domain.models

data class Location(
    val address: String,
    val city: String,
    val country: String,
    val latitude: Double?,
    val longitude: Double?
)
```

**Android Migration**:
- **Location**: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/domain/models/Location.kt`
- **Changes**: Update package, preserve structure

**iOS Translation**:
- **Location**: `/iosApp/iosApp/Domain/Models/Location.swift`
- **Translation**:
  ```swift
  struct Location {
      let address: String
      let city: String
      let country: String
      let latitude: Double?
      let longitude: Double?
  }
  ```

**Fields**:
| Field | Type | Nullable | Description |
|-------|------|----------|-------------|
| address | String | No | Street address |
| city | String | No | City name |
| country | String | No | Country name |
| latitude | Double | Yes | Geographic latitude coordinate |
| longitude | Double | Yes | Geographic longitude coordinate |

**Business Rules**:
- All address fields (address, city, country) are required
- Coordinates (latitude, longitude) are optional (may not be available for all locations)
- Latitude range: -90 to +90 degrees
- Longitude range: -180 to +180 degrees

---

### 3. AnimalSpecies (Enum)

**Description**: Enumeration of animal types supported by the system.

**Shared Module (Kotlin)**:
```kotlin
package com.intive.aifirst.petspot.domain.models

enum class AnimalSpecies {
    DOG,
    CAT,
    BIRD,
    RABBIT,
    OTHER
}
```

**Android Migration**:
- **Location**: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/domain/models/AnimalSpecies.kt`
- **Changes**: Update package, preserve enum structure (SCREAMING_SNAKE_CASE per Kotlin convention)

**iOS Translation**:
- **Location**: `/iosApp/iosApp/Domain/Models/AnimalSpecies.swift`
- **Translation**:
  ```swift
  enum AnimalSpecies {
      case dog
      case cat
      case bird
      case rabbit
      case other
  }
  ```
- **Changes**: Kotlin SCREAMING_SNAKE_CASE → Swift camelCase (per Swift convention)

**Values**:
| Kotlin | Swift | Description |
|--------|-------|-------------|
| DOG | dog | Canine species |
| CAT | cat | Feline species |
| BIRD | bird | Avian species |
| RABBIT | rabbit | Lagomorph species |
| OTHER | other | Other/unknown species |

**Business Rules**:
- If species not in predefined list, use `OTHER`/`other`
- Backend API may define additional species values in future

---

### 4. AnimalGender (Enum)

**Description**: Enumeration of animal genders.

**Shared Module (Kotlin)**:
```kotlin
package com.intive.aifirst.petspot.domain.models

enum class AnimalGender {
    MALE,
    FEMALE,
    UNKNOWN
}
```

**Android Migration**:
- **Location**: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/domain/models/AnimalGender.kt`
- **Changes**: Update package, preserve enum structure

**iOS Translation**:
- **Location**: `/iosApp/iosApp/Domain/Models/AnimalGender.swift`
- **Translation**:
  ```swift
  enum AnimalGender {
      case male
      case female
      case unknown
  }
  ```

**Values**:
| Kotlin | Swift | Description |
|--------|-------|-------------|
| MALE | male | Male animal |
| FEMALE | female | Female animal |
| UNKNOWN | unknown | Gender not determined or not specified |

**Business Rules**:
- Default to `UNKNOWN`/`unknown` if gender not specified
- Gender may be updated after initial entry

---

### 5. AnimalStatus (Enum)

**Description**: Enumeration of adoption statuses for animals.

**Shared Module (Kotlin)**:
```kotlin
package com.intive.aifirst.petspot.domain.models

enum class AnimalStatus {
    AVAILABLE,
    PENDING,
    ADOPTED
}
```

**Android Migration**:
- **Location**: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/domain/models/AnimalStatus.kt`
- **Changes**: Update package, preserve enum structure

**iOS Translation**:
- **Location**: `/iosApp/iosApp/Domain/Models/AnimalStatus.swift`
- **Translation**:
  ```swift
  enum AnimalStatus {
      case available
      case pending
      case adopted
  }
  ```

**Values**:
| Kotlin | Swift | Description |
|--------|-------|-------------|
| AVAILABLE | available | Animal is available for adoption |
| PENDING | pending | Adoption in progress (application submitted) |
| ADOPTED | adopted | Animal has been adopted |

**Business Rules**:
- Status transitions: AVAILABLE → PENDING → ADOPTED
- Cannot transition from ADOPTED back to AVAILABLE (one-way)
- Backend API controls status transitions

---

## Repository Interfaces

### 1. AnimalRepository

**Description**: Interface for accessing animal data. Implementations exist in platform-specific data layers.

**Shared Module (Kotlin)**:
```kotlin
package com.intive.aifirst.petspot.domain.repositories

interface AnimalRepository {
    suspend fun getAnimals(): List<Animal>
}
```

**Android Migration**:
- **Location**: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/domain/repositories/AnimalRepository.kt`
- **Changes**: Update package
- **Implementation**: `AnimalRepositoryImpl` already exists in `/composeApp/src/androidMain/.../data/repositories/`

**iOS Translation**:
- **Location**: `/iosApp/iosApp/Domain/Repositories/AnimalRepository.swift`
- **Translation**:
  ```swift
  protocol AnimalRepository {
      func getAnimals() async throws -> [Animal]
  }
  ```
- **Implementation**: `AnimalRepositoryImpl` already exists in `/iosApp/iosApp/Features/AnimalList/Repositories/`

**Methods**:
| Method | Kotlin Signature | Swift Signature | Description |
|--------|------------------|-----------------|-------------|
| getAnimals | `suspend fun getAnimals(): List<Animal>` | `func getAnimals() async throws -> [Animal]` | Retrieves all animals from data source |

**Notes**:
- Kotlin uses `suspend` for coroutines, Swift uses `async` for Swift Concurrency
- Kotlin uses `Result<T>` or throws, Swift uses `throws` (more idiomatic)
- Implementations handle network calls, caching, error handling

---

### 2. FakeAnimalRepository (Test Fixture)

**Description**: In-memory fake repository for testing purposes.

**Shared Module (Kotlin)**:
```kotlin
package com.intive.aifirst.petspot.domain.repositories

class FakeAnimalRepository(
    private val animals: List<Animal> = emptyList()
) : AnimalRepository {
    override suspend fun getAnimals(): List<Animal> = animals
}
```

**Android Migration**:
- **Location**: `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/composeapp/domain/repositories/FakeAnimalRepository.kt`
- **Changes**: Update package, move to test directory

**iOS Translation**:
- **Location**: `/iosApp/iosAppTests/Fakes/FakeAnimalRepository.swift`
- **Translation**:
  ```swift
  class FakeAnimalRepository: AnimalRepository {
      private let animals: [Animal]
      
      init(animals: [Animal] = []) {
          self.animals = animals
      }
      
      func getAnimals() async throws -> [Animal] {
          return animals
      }
  }
  ```

**Usage**: Test fixture for unit tests, replaces real repository with predictable data

---

## Use Cases

### 1. GetAnimalsUseCase

**Description**: Business logic for retrieving animals. Orchestrates repository call.

**Shared Module (Kotlin)**:
```kotlin
package com.intive.aifirst.petspot.domain.usecases

class GetAnimalsUseCase(
    private val repository: AnimalRepository
) {
    suspend operator fun invoke(): Result<List<Animal>> {
        return try {
            Result.success(repository.getAnimals())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

**Android Migration**:
- **Location**: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/domain/usecases/GetAnimalsUseCase.kt`
- **Changes**: Update package, preserve structure
- **DI**: Provided by Koin DomainModule

**iOS Translation**:
- **Location**: `/iosApp/iosApp/Domain/UseCases/GetAnimalsUseCase.swift`
- **Translation**:
  ```swift
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
- **Note**: iOS architecture SHOULD remove use case layer in future (ViewModels call repositories directly per constitution). Migrated as transitional code for this migration.
- **DI**: Provided by ServiceContainer

**Rationale**: Use case provides error handling and potential for business logic. Android follows standard architecture. iOS includes as transitional code during migration but should be removed in future refactoring (per constitutional iOS MVVM-C guidelines).

---

## Test Fixtures

### MockAnimalData

**Description**: Predefined test data for development and testing.

**Shared Module (Kotlin)**:
```kotlin
package com.intive.aifirst.petspot.domain.fixtures

object MockAnimalData {
    val animals = listOf(
        Animal(
            id = "1",
            name = "Max",
            species = AnimalSpecies.DOG,
            gender = AnimalGender.MALE,
            status = AnimalStatus.AVAILABLE,
            location = Location(
                address = "123 Main St",
                city = "Warsaw",
                country = "Poland",
                latitude = 52.2297,
                longitude = 21.0122
            ),
            imageUrl = "https://example.com/max.jpg"
        ),
        Animal(
            id = "2",
            name = "Luna",
            species = AnimalSpecies.CAT,
            gender = AnimalGender.FEMALE,
            status = AnimalStatus.AVAILABLE,
            location = null,
            imageUrl = null
        )
    )
}
```

**Android Migration**:
- **Location**: `/composeApp/src/androidUnitTest/kotlin/com/intive/aifirst/petspot/composeapp/domain/fixtures/MockAnimalData.kt`
- **Changes**: Move to test directory, update package

**iOS Migration**:
- **Status**: iOS already has hardcoded mock array in `AnimalRepositoryImpl`
- **Action**: Leave iOS mock as-is (already platform-specific)
- **Optional**: Extract to separate fixture file if needed for test reuse

---

## Dependency Injection Modules

### DomainModule (Android Koin)

**Description**: Koin module providing domain layer dependencies for Android.

**Shared Module (Kotlin)**:
```kotlin
package com.intive.aifirst.petspot.domain.di

import org.koin.dsl.module

val domainModule = module {
    factory { GetAnimalsUseCase(get()) }
}
```

**Android Migration**:
- **Location**: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/di/DomainModule.kt`
- **Changes**: 
  - Update package to `composeapp.di`
  - Update imports to reference local composeApp domain classes
  - Register in `PetSpotApplication.kt` alongside existing `dataModule` and `viewModelModule`

**Registration**:
```kotlin
// PetSpotApplication.kt
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

---

### ServiceContainer (iOS Manual DI)

**Description**: Manual dependency injection container for iOS.

**Current State**:
```swift
import Shared

class ServiceContainer {
    static let shared = ServiceContainer()
    
    lazy var animalRepository: AnimalRepository = AnimalRepositoryImpl(
        animals: Shared.MockAnimalData().animals
    )
}
```

**After Migration**:
```swift
class ServiceContainer {
    static let shared = ServiceContainer()
    
    lazy var animalRepository: AnimalRepository = AnimalRepositoryImpl(
        // Use local mock data or hardcoded array
    )
    
    lazy var getAnimalsUseCase: GetAnimalsUseCase = GetAnimalsUseCase(
        repository: animalRepository
    )
}
```

**Changes**:
- Remove `import Shared`
- Update to use local Swift domain models
- Add `getAnimalsUseCase` lazy property (transitional)

---

## Entity Relationships

```
Animal
├── species: AnimalSpecies (enum)
├── gender: AnimalGender (enum)
├── status: AnimalStatus (enum)
└── location: Location? (optional)

AnimalRepository (interface)
└── getAnimals() -> List<Animal>

GetAnimalsUseCase (business logic)
└── repository: AnimalRepository (dependency)
```

**Relationship Rules**:
- Animal has required reference to AnimalSpecies, AnimalGender, AnimalStatus
- Animal has optional reference to Location (nullable)
- GetAnimalsUseCase depends on AnimalRepository interface (not implementation)
- Platform implementations inject concrete AnimalRepositoryImpl via DI

---

## Migration Validation

**Android Validation**:
```bash
# Verify all models exist
ls composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/domain/models/
# Expected: Animal.kt, Location.kt, AnimalSpecies.kt, AnimalGender.kt, AnimalStatus.kt

# Verify repository interface exists
ls composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/domain/repositories/
# Expected: AnimalRepository.kt

# Verify use case exists
ls composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/domain/usecases/
# Expected: GetAnimalsUseCase.kt

# Verify DI module exists
ls composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/composeapp/di/
# Expected: DomainModule.kt (alongside existing DataModule.kt, ViewModelModule.kt)

# Verify no shared imports remain
git grep "import com.intive.aifirst.petspot.domain" composeApp/src/androidMain/
# Expected: No results (or only imports from composeapp.domain)

# Build succeeds
./gradlew :composeApp:assembleDebug

# Tests pass with 80%+ coverage
./gradlew :composeApp:testDebugUnitTest koverHtmlReport
```

**iOS Validation**:
```bash
# Verify all models exist
ls iosApp/iosApp/Domain/Models/
# Expected: Animal.swift, Location.swift, AnimalSpecies.swift, AnimalGender.swift, AnimalStatus.swift

# Verify repository protocol exists
ls iosApp/iosApp/Domain/Repositories/
# Expected: AnimalRepository.swift

# Verify use case exists
ls iosApp/iosApp/Domain/UseCases/
# Expected: GetAnimalsUseCase.swift

# Verify no Shared imports remain
git grep "import Shared" iosApp/
# Expected: No results

# Build succeeds in Xcode
xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' build

# Tests pass with 80%+ coverage
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES
```

---

## Summary

**Total Entities to Migrate**: 10

**Domain Models**: 5
- Animal (data class/struct)
- Location (data class/struct)
- AnimalSpecies (enum)
- AnimalGender (enum)
- AnimalStatus (enum)

**Repository Interfaces**: 1
- AnimalRepository (interface/protocol)

**Use Cases**: 1
- GetAnimalsUseCase (business logic)

**Test Fixtures**: 2
- FakeAnimalRepository (test double)
- MockAnimalData (test data)

**DI Modules**: 1
- DomainModule (Android Koin) / ServiceContainer update (iOS manual DI)

**Migration Impact**:
- Android: Copy Kotlin files, update package names and imports
- iOS: Translate Kotlin to Swift, follow platform idioms
- Both: Preserve business logic semantics, maintain 80%+ test coverage

