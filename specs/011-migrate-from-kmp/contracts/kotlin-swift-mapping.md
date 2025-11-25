# Kotlin-to-Swift Migration Contract

**Feature**: Complete KMP to Platform-Independent Migration  
**Branch**: `011-migrate-from-kmp`  
**Date**: 2025-11-24

## Purpose

This contract defines the precise mapping between Kotlin code in the shared KMP module and Swift code in the iOS platform. It serves as the authoritative reference for iOS migration (Phases 1-2).

## General Translation Rules

### Type Mappings

| Kotlin | Swift | Notes |
|--------|-------|-------|
| `data class` | `struct` | Value semantics, immutability by default |
| `class` | `class` | Reference semantics |
| `interface` | `protocol` | Behavior contracts |
| `enum class` | `enum` | Enumeration types |
| `object` | `class` with singleton | Kotlin object → Swift class with `static let shared` |
| `val` | `let` | Immutable reference |
| `var` | `var` | Mutable reference |

### Nullability Mappings

| Kotlin | Swift | Notes |
|--------|-------|-------|
| `Type` | `Type` | Non-nullable type |
| `Type?` | `Type?` | Optional type (same syntax!) |

### Collection Mappings

| Kotlin | Swift | Notes |
|--------|-------|-------|
| `List<T>` | `[T]` | Array (immutable by default in Swift) |
| `MutableList<T>` | `[T]` (with `var`) | Mutable array |
| `Set<T>` | `Set<T>` | Set |
| `Map<K, V>` | `[K: V]` | Dictionary |

### Async Mappings

| Kotlin | Swift | Notes |
|--------|-------|-------|
| `suspend fun` | `func ... async throws` | Coroutines → Swift Concurrency |
| `Result<T>` | `throws` | Kotlin Result → Swift throwing function (more idiomatic) |
| `try/catch` | `do/try/catch` | Error handling |

### Naming Convention Mappings

| Kotlin | Swift | Notes |
|--------|-------|-------|
| `SCREAMING_SNAKE_CASE` (enum) | `camelCase` (enum) | Enum case naming |
| `camelCase` (properties/methods) | `camelCase` (properties/methods) | Same convention |
| `PascalCase` (types) | `PascalCase` (types) | Same convention |

---

## Domain Model Contracts

### Animal

**Kotlin Source** (`shared/src/commonMain/.../domain/models/Animal.kt`):
```kotlin
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

**Swift Target** (`iosApp/iosApp/Domain/Models/Animal.swift`):
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

**Translation Notes**:
- Kotlin `data class` → Swift `struct` (automatic `Equatable`, `Hashable` conformance)
- All `val` → `let` (immutability preserved)
- Optional types (`?`) have identical syntax
- Add `Codable` conformance if JSON serialization needed

**Validation**:
- [ ] All 7 properties present with correct names
- [ ] All types match (String, enums, optional types)
- [ ] Immutability preserved (all `let`, not `var`)
- [ ] No additional properties added

---

### Location

**Kotlin Source**:
```kotlin
data class Location(
    val address: String,
    val city: String,
    val country: String,
    val latitude: Double?,
    val longitude: Double?
)
```

**Swift Target**:
```swift
struct Location {
    let address: String
    let city: String
    let country: String
    let latitude: Double?
    let longitude: Double?
}
```

**Translation Notes**:
- Kotlin `Double?` → Swift `Double?` (same syntax for optional numeric types)
- All required fields (address, city, country) remain non-optional
- Geographic coordinates remain optional

**Validation**:
- [ ] All 5 properties present with correct names
- [ ] Optional properties correctly marked (`latitude`, `longitude`)
- [ ] Non-optional properties remain non-optional (address, city, country)

---

### AnimalSpecies

**Kotlin Source**:
```kotlin
enum class AnimalSpecies {
    DOG,
    CAT,
    BIRD,
    RABBIT,
    OTHER
}
```

**Swift Target**:
```swift
enum AnimalSpecies {
    case dog
    case cat
    case bird
    case rabbit
    case other
}
```

**Translation Notes**:
- Kotlin SCREAMING_SNAKE_CASE → Swift camelCase (platform convention)
- Enum case mapping table:

| Kotlin | Swift |
|--------|-------|
| DOG | dog |
| CAT | cat |
| BIRD | bird |
| RABBIT | rabbit |
| OTHER | other |

**Validation**:
- [ ] All 5 enum cases present
- [ ] Case names follow Swift convention (camelCase, not SCREAMING_SNAKE_CASE)
- [ ] Case mapping semantically equivalent

**Switch Statement Updates**:
If iOS code has switch statements on `AnimalSpecies`, update case names:

```swift
// BEFORE (with Shared framework)
switch animal.species {
case .DOG: // ...
case .CAT: // ...
// ...
}

// AFTER (with local Swift enum)
switch animal.species {
case .dog: // ...
case .cat: // ...
// ...
}
```

---

### AnimalGender

**Kotlin Source**:
```kotlin
enum class AnimalGender {
    MALE,
    FEMALE,
    UNKNOWN
}
```

**Swift Target**:
```swift
enum AnimalGender {
    case male
    case female
    case unknown
}
```

**Translation Notes**:
- Enum case mapping table:

| Kotlin | Swift |
|--------|-------|
| MALE | male |
| FEMALE | female |
| UNKNOWN | unknown |

**Validation**:
- [ ] All 3 enum cases present
- [ ] Case names follow Swift convention

---

### AnimalStatus

**Kotlin Source**:
```kotlin
enum class AnimalStatus {
    AVAILABLE,
    PENDING,
    ADOPTED
}
```

**Swift Target**:
```swift
enum AnimalStatus {
    case available
    case pending
    case adopted
}
```

**Translation Notes**:
- Enum case mapping table:

| Kotlin | Swift |
|--------|-------|
| AVAILABLE | available |
| PENDING | pending |
| ADOPTED | adopted |

**Validation**:
- [ ] All 3 enum cases present
- [ ] Case names follow Swift convention

---

## Repository Interface Contract

### AnimalRepository

**Kotlin Source**:
```kotlin
interface AnimalRepository {
    suspend fun getAnimals(): List<Animal>
}
```

**Swift Target**:
```swift
protocol AnimalRepository {
    func getAnimals() async throws -> [Animal]
}
```

**Translation Notes**:
- Kotlin `interface` → Swift `protocol`
- Kotlin `suspend fun` → Swift `func ... async throws`
- Kotlin `List<Animal>` → Swift `[Animal]`
- Error handling: Kotlin uses `Result<T>` or throws, Swift uses `throws` (more idiomatic)

**Validation**:
- [ ] Protocol defined (not class or struct)
- [ ] Method signature uses `async throws`
- [ ] Return type is array `[Animal]`, not `List` or other type

---

## Use Case Contract

### GetAnimalsUseCase

**Kotlin Source**:
```kotlin
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

**Swift Target**:
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

**Translation Notes**:
- Kotlin constructor parameter → Swift `init` with parameter
- Kotlin `operator fun invoke()` → Swift `func execute()` (Swift doesn't have invoke operator)
- Kotlin `Result<T>` → Swift `throws` (error handling via exceptions)
- Kotlin `suspend` → Swift `async throws`

**Rationale for Translation Choices**:
- **invoke → execute**: Swift doesn't have Kotlin's invoke operator. Using `execute()` is idiomatic for command/use case pattern in Swift.
- **Result → throws**: Swift throwing functions are more idiomatic than explicit Result types for simple error propagation.

**Validation**:
- [ ] Class defined (not struct or protocol)
- [ ] Constructor injection for repository dependency
- [ ] Execute method (not invoke - Swift doesn't support invoke operator)
- [ ] Async throws signature

**iOS Architecture Note**:
This use case is migrated as **transitional code**. Per constitutional iOS MVVM-C guidelines, iOS ViewModels SHOULD call repositories directly (no use case layer). This use case preserves current architecture during migration but SHOULD be removed in future refactoring:

```swift
// Future iOS pattern (remove use case, call repository directly)
class AnimalListViewModel: ObservableObject {
    private let repository: AnimalRepository
    
    func loadAnimals() async {
        do {
            self.animals = try await repository.getAnimals()  // Direct call
        } catch {
            // Handle error
        }
    }
}
```

---

## Test Fixture Contracts

### FakeAnimalRepository

**Kotlin Source**:
```kotlin
class FakeAnimalRepository(
    private val animals: List<Animal> = emptyList()
) : AnimalRepository {
    override suspend fun getAnimals(): List<Animal> = animals
}
```

**Swift Target**:
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

**Translation Notes**:
- Kotlin constructor default parameter → Swift `init` with default parameter
- Kotlin `emptyList()` → Swift `[]` (empty array literal)
- Protocol conformance indicated with `: AnimalRepository`

**Validation**:
- [ ] Conforms to AnimalRepository protocol
- [ ] Constructor with default empty array
- [ ] Returns provided animals synchronously

---

### MockAnimalData

**Kotlin Source**:
```kotlin
object MockAnimalData {
    val animals = listOf(
        Animal(/* ... */),
        Animal(/* ... */)
    )
}
```

**Swift Target**:
iOS already has hardcoded mock array in `AnimalRepositoryImpl`. No migration needed.

**Alternative** (if extracting to separate fixture):
```swift
class MockAnimalData {
    static let animals: [Animal] = [
        Animal(/* ... */),
        Animal(/* ... */)
    ]
}
```

**Translation Notes**:
- Kotlin `object` → Swift `class` with `static` property (singleton pattern)
- Kotlin `listOf()` → Swift array literal `[...]`

---

## Dependency Injection Contract

### ServiceContainer Update

**Before Migration**:
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

**Translation Notes**:
- Remove `import Shared` statement
- Update repository initialization to use local Swift types
- Add `getAnimalsUseCase` lazy property (transitional)

**Validation**:
- [ ] No `import Shared` statement present
- [ ] Repository provided with local Swift types
- [ ] Use case provided with repository dependency
- [ ] All properties use `lazy var` (lazy initialization)

---

## Import Statement Updates

### Before Migration

**iOS ViewModel imports Shared**:
```swift
import Shared
import Foundation

class AnimalListViewModel: ObservableObject {
    @Published var animals: [Shared.Animal] = []
    
    private let repository: Shared.AnimalRepository
    
    func loadAnimals() async {
        self.animals = await repository.getAnimals()
    }
}
```

### After Migration

**iOS ViewModel uses local types**:
```swift
import Foundation

class AnimalListViewModel: ObservableObject {
    @Published var animals: [Animal] = []  // Local Domain.Animal
    
    private let repository: AnimalRepository  // Local Domain protocol
    
    func loadAnimals() async {
        do {
            self.animals = try await repository.getAnimals()
        } catch {
            // Handle error
        }
    }
}
```

**Import Changes**:
- Remove: `import Shared`
- Types now resolve to local `iosApp/Domain/` types automatically

**Validation**:
- [ ] No `import Shared` statements remain in any iOS file
- [ ] No `Shared.` type prefixes remain
- [ ] All domain types resolve to local Swift implementations

---

## Migration Checklist

### Phase 1: iOS Domain Models (Kotlin → Swift Translation)

- [ ] **Animal.swift**: Struct with 7 properties, all correct types
- [ ] **Location.swift**: Struct with 5 properties, optional coordinates
- [ ] **AnimalSpecies.swift**: Enum with 5 cases (camelCase)
- [ ] **AnimalGender.swift**: Enum with 3 cases (camelCase)
- [ ] **AnimalStatus.swift**: Enum with 3 cases (camelCase)
- [ ] All files in `/iosApp/iosApp/Domain/Models/`
- [ ] Remove `import Shared` from all iOS files
- [ ] Update enum case names in switch statements (SCREAMING_SNAKE_CASE → camelCase)
- [ ] iOS builds successfully
- [ ] iOS unit tests pass

### Phase 2: iOS Repositories and Use Cases (Kotlin → Swift Translation)

- [ ] **AnimalRepository.swift**: Protocol with `getAnimals() async throws -> [Animal]`
- [ ] **GetAnimalsUseCase.swift**: Class with `execute() async throws -> [Animal]`
- [ ] **FakeAnimalRepository.swift**: Test fake in `/iosApp/iosAppTests/Fakes/`
- [ ] **ServiceContainer.swift**: Updated with local types, no `import Shared`
- [ ] ViewModels updated with local type imports
- [ ] iOS builds successfully
- [ ] iOS unit tests pass with 80%+ coverage

---

## Semantic Equivalence Validation

After iOS migration, the following must be semantically equivalent:

| Concept | Kotlin (Android) | Swift (iOS) | Equivalent? |
|---------|------------------|-------------|-------------|
| Animal entity | `data class Animal(...)` | `struct Animal {...}` | ✅ |
| Nullable location | `location: Location?` | `location: Location?` | ✅ |
| Species enum | `AnimalSpecies.DOG` | `AnimalSpecies.dog` | ✅ (semantic) |
| Repository interface | `interface AnimalRepository` | `protocol AnimalRepository` | ✅ |
| Async method | `suspend fun getAnimals()` | `func getAnimals() async throws` | ✅ |
| Use case execution | `useCase.invoke()` | `useCase.execute()` | ✅ (semantic) |

**Key Difference Accepted**:
- Enum case naming: Kotlin SCREAMING_SNAKE_CASE vs Swift camelCase (platform convention, semantically equivalent)
- Use case method: Kotlin `invoke()` vs Swift `execute()` (Swift lacks invoke operator, semantically equivalent)

---

## Post-Migration Verification

### iOS Build Verification
```bash
# No Shared imports remain
git grep "import Shared" iosApp/
# Expected: No results

# Build succeeds
xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' build

# Tests pass
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' -enableCodeCoverage YES
```

### Type Safety Verification
```swift
// Compiler should accept
let animal = Animal(
    id: "1",
    name: "Max",
    species: .dog,  // Local Swift enum
    gender: .male,
    status: .available,
    location: nil,
    imageUrl: nil
)

// Should compile and run
let repository: AnimalRepository = AnimalRepositoryImpl()
let animals = try await repository.getAnimals()
```

### Test Coverage Verification
```bash
# iOS test coverage report should show 80%+ coverage
# Xcode → Product → Test → Show Code Coverage
# Verify Domain models, repositories, use cases covered
```

---

## Summary

This contract ensures:
1. **Type equivalence**: Kotlin types map to semantically equivalent Swift types
2. **Behavior preservation**: Business logic remains functionally identical
3. **Platform idioms**: Each platform follows its native conventions
4. **Testability**: Test fixtures and fakes are migrated for iOS testing

**Successful migration criteria**:
- ✅ All domain models translated to Swift structs/enums
- ✅ All repository interfaces translated to Swift protocols
- ✅ All use cases translated to Swift classes
- ✅ No `import Shared` statements remain in iOS codebase
- ✅ iOS builds successfully without KMP framework
- ✅ iOS tests pass with 80%+ coverage maintained

