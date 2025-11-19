# Contract: AnimalRepository Interface

**Feature**: Animal List Screen  
**Date**: 2025-11-19  
**Type**: Repository Interface (Kotlin Multiplatform)

## Overview

This document defines the `AnimalRepository` interface contract. The interface resides in the shared Kotlin Multiplatform module and is implemented by platform-specific mock repositories for this UI-only phase.

In future iterations, real implementations will fetch data from REST API backends.

## Interface Definition

**Location**: `/shared/src/commonMain/kotlin/com/intive/aifirst/petspot/domain/repositories/AnimalRepository.kt`

```kotlin
package com.intive.aifirst.petspot.domain.repositories

import com.intive.aifirst.petspot.domain.models.Animal

/**
 * Repository interface for animal data operations.
 * Defines contract for fetching animal data from various sources.
 *
 * Platform-specific implementations:
 * - Mock implementation: Returns hardcoded test data (current phase)
 * - Real implementation: Fetches from REST API (future phase)
 *
 * All operations are suspend functions using Kotlin Coroutines.
 * Returns are wrapped in Result<T> for explicit error handling.
 */
interface AnimalRepository {
    /**
     * Retrieves all animals from the data source.
     * Mock implementation returns fixed list of 8-12 animals.
     * Real implementation will support pagination and filtering.
     *
     * @return Result.success with list of animals, or Result.failure with exception
     */
    suspend fun getAnimals(): Result<List<Animal>>
}
```

## Contract Specifications

### Method: `getAnimals()`

**Signature**: `suspend fun getAnimals(): Result<List<Animal>>`

**Description**: Fetches all animals from the data source (mock data or API).

**Parameters**: None (future: add pagination, filtering)

**Return Type**: `Result<List<Animal>>`
- **Success**: `Result.success(animals)` where `animals` is non-null List (can be empty)
- **Failure**: `Result.failure(exception)` where exception is:
  - `IOException` - Network error (future API implementation)
  - `HttpException` - HTTP error response (future API implementation)
  - `Exception` - Generic error (parsing, unexpected)

**Behavior**:
1. **Success Case**:
   - Returns list of 0 or more Animal entities
   - Empty list is valid (represents no animals in system)
   - Animals are sorted by date (most recent first) - future enhancement
   
2. **Failure Case**:
   - Network timeout (future)
   - Server error 5xx (future)
   - Invalid response parsing (future)
   - Mock implementation should NOT fail (unless explicitly testing error states)

**Threading**:
- Suspending function - caller must invoke from coroutine scope
- Implementation can perform blocking I/O or network calls
- Android: Call from `viewModelScope`
- iOS: Call from `async` context with `@MainActor` updates
- Web: Call from async function

**Caching** (future):
- Real implementation may cache results locally
- Mock implementation does not cache (returns same list every time)

---

## Mock Implementation

### Android Mock Repository

**Location**: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/data/MockAnimalRepository.kt`

```kotlin
package com.intive.aifirst.petspot.data

import com.intive.aifirst.petspot.domain.models.*
import com.intive.aifirst.petspot.domain.repositories.AnimalRepository
import kotlinx.coroutines.delay

/**
 * Mock implementation of AnimalRepository.
 * Returns hardcoded list of 8-12 animals for UI testing.
 * Simulates network delay to test loading states.
 */
class MockAnimalRepository : AnimalRepository {
    
    /** Simulated network delay in milliseconds */
    private val networkDelayMs: Long = 500
    
    override suspend fun getAnimals(): Result<List<Animal>> {
        // Simulate network delay
        delay(networkDelayMs)
        
        // Return mock data
        return Result.success(getMockAnimals())
    }
    
    /**
     * Generates mock animal data for testing.
     * Returns 10 animals with varied attributes.
     */
    private fun getMockAnimals(): List<Animal> = listOf(
        Animal(
            id = "1",
            image = "placeholder_cat",
            location = Location("Pruszkow", 5),
            species = AnimalSpecies.CAT,
            breed = "Maine Coon",
            gender = AnimalGender.MALE,
            status = AnimalStatus.MISSING,
            date = "18/11/2025",
            description = "Friendly orange tabby cat, last seen near the park."
        ),
        Animal(
            id = "2",
            image = "placeholder_dog",
            location = Location("Warsaw", 10),
            species = AnimalSpecies.DOG,
            breed = "German Shepherd",
            gender = AnimalGender.FEMALE,
            status = AnimalStatus.MISSING,
            date = "17/11/2025",
            description = "Large black and tan dog, wearing red collar."
        ),
        Animal(
            id = "3",
            image = "placeholder_cat",
            location = Location("Krakow", 3),
            species = AnimalSpecies.CAT,
            breed = "Siamese",
            gender = AnimalGender.FEMALE,
            status = AnimalStatus.FOUND,
            date = "19/11/2025",
            description = "Blue-eyed white cat found near train station."
        ),
        Animal(
            id = "4",
            image = "placeholder_dog",
            location = Location("Wroclaw", 7),
            species = AnimalSpecies.DOG,
            breed = "Labrador Retriever",
            gender = AnimalGender.MALE,
            status = AnimalStatus.MISSING,
            date = "16/11/2025",
            description = "Yellow lab, very friendly, responds to 'Buddy'."
        ),
        Animal(
            id = "5",
            image = "placeholder_bird",
            location = Location("Gdansk", 15),
            species = AnimalSpecies.BIRD,
            breed = "Cockatiel",
            gender = AnimalGender.UNKNOWN,
            status = AnimalStatus.MISSING,
            date = "15/11/2025",
            description = "Gray and yellow bird, escaped from balcony."
        ),
        Animal(
            id = "6",
            image = "placeholder_cat",
            location = Location("Poznan", 8),
            species = AnimalSpecies.CAT,
            breed = "Persian",
            gender = AnimalGender.FEMALE,
            status = AnimalStatus.MISSING,
            date = "14/11/2025",
            description = "White long-haired cat, very shy."
        ),
        Animal(
            id = "7",
            image = "placeholder_dog",
            location = Location("Lodz", 12),
            species = AnimalSpecies.DOG,
            breed = "Beagle",
            gender = AnimalGender.MALE,
            status = AnimalStatus.FOUND,
            date = "20/11/2025",
            description = "Tri-color beagle found wandering near shopping center."
        ),
        Animal(
            id = "8",
            image = "placeholder_rabbit",
            location = Location("Katowice", 6),
            species = AnimalSpecies.RABBIT,
            breed = "Dwarf Rabbit",
            gender = AnimalGender.FEMALE,
            status = AnimalStatus.MISSING,
            date = "13/11/2025",
            description = "Small gray rabbit, very timid."
        ),
        Animal(
            id = "9",
            image = "placeholder_dog",
            location = Location("Szczecin", 20),
            species = AnimalSpecies.DOG,
            breed = "Husky",
            gender = AnimalGender.MALE,
            status = AnimalStatus.MISSING,
            date = "12/11/2025",
            description = "Blue-eyed Siberian Husky, very energetic."
        ),
        Animal(
            id = "10",
            image = "placeholder_cat",
            location = Location("Bialystok", 4),
            species = AnimalSpecies.CAT,
            breed = "British Shorthair",
            gender = AnimalGender.MALE,
            status = AnimalStatus.MISSING,
            date = "11/11/2025",
            description = "Gray tabby cat with green eyes."
        )
    )
}
```

**Key Points**:
- Returns 10 animals (within 8-12 range from spec)
- Mix of species: Dogs (5), Cats (4), Bird (1), Rabbit (1)
- Mix of statuses: Missing (8), Found (2)
- Varied locations (different Polish cities)
- Simulates 500ms network delay for realistic loading state
- No error simulation (always succeeds) - error tests use FakeRepository

---

### iOS Mock Repository

**Location**: `/iosApp/iosApp/Repositories/MockAnimalRepository.swift`

```swift
import shared

/**
 * Mock implementation of AnimalRepository for iOS.
 * Returns same hardcoded data as Android mock.
 * Simulates network delay for testing loading states.
 */
class MockAnimalRepository {
    /// Network delay simulation (0.5 seconds)
    private let networkDelaySeconds: Double = 0.5
    
    /**
     * Fetches mock animal data.
     * Returns list of 10 animals after simulated delay.
     */
    func getAnimals() async throws -> [Animal] {
        // Simulate network delay
        try await Task.sleep(nanoseconds: UInt64(networkDelaySeconds * 1_000_000_000))
        
        // Return mock data
        return getMockAnimals()
    }
    
    /**
     * Generates mock animal list.
     * Data matches Android mock for consistency.
     */
    private func getMockAnimals() -> [Animal] {
        // Return same mock data as Android
        // (Swift translation of Kotlin mock data)
        [
            Animal(
                id: "1",
                image: "placeholder_cat",
                location: Location(city: "Pruszkow", radiusKm: 5),
                species: .cat,
                breed: "Maine Coon",
                gender: .male,
                status: .missing,
                date: "18/11/2025",
                description: "Friendly orange tabby cat, last seen near the park."
            ),
            // ... other 9 animals (same as Android)
        ]
    }
}
```

---

### Web Mock Repository

**Location**: `/webApp/src/services/mockAnimalRepository.ts`

```typescript
import type { Animal } from 'shared';

/**
 * Mock implementation of animal repository for web.
 * Returns hardcoded test data matching Android/iOS mocks.
 */
export class MockAnimalRepository {
    private readonly networkDelayMs = 500;
    
    /**
     * Fetches mock animal data.
     * Simulates network delay and returns list of animals.
     */
    async getAnimals(): Promise<Animal[]> {
        // Simulate network delay
        await new Promise(resolve => setTimeout(resolve, this.networkDelayMs));
        
        // Return mock data
        return this.getMockAnimals();
    }
    
    /**
     * Generates mock animal list.
     * Data matches Android/iOS for consistency across platforms.
     */
    private getMockAnimals(): Animal[] {
        return [
            {
                id: '1',
                image: 'placeholder_cat',
                location: { city: 'Pruszkow', radiusKm: 5 },
                species: 'CAT',
                breed: 'Maine Coon',
                gender: 'MALE',
                status: 'MISSING',
                date: '18/11/2025',
                description: 'Friendly orange tabby cat, last seen near the park.'
            },
            // ... other 9 animals (same as Android)
        ];
    }
}
```

---

## Test Implementations

### Fake Repository for Testing

Used in unit tests to control behavior and test error scenarios.

**Location**: `/shared/src/commonTest/kotlin/com/intive/aifirst/petspot/domain/repositories/FakeAnimalRepository.kt`

```kotlin
package com.intive.aifirst.petspot.domain.repositories

import com.intive.aifirst.petspot.domain.models.Animal

/**
 * Fake repository implementation for unit testing.
 * Allows controlling success/failure scenarios.
 */
class FakeAnimalRepository(
    private val animals: List<Animal> = emptyList(),
    private val shouldFail: Boolean = false,
    private val exception: Throwable = Exception("Fake repository error")
) : AnimalRepository {
    
    var getAnimalsCallCount = 0
        private set
    
    override suspend fun getAnimals(): Result<List<Animal>> {
        getAnimalsCallCount++
        
        return if (shouldFail) {
            Result.failure(exception)
        } else {
            Result.success(animals)
        }
    }
}
```

**Usage in Tests**:
```kotlin
@Test
fun `should return animals when repository succeeds`() = runTest {
    // Given - fake repository with test data
    val mockAnimals = listOf(
        Animal(/* ... */),
        Animal(/* ... */)
    )
    val fakeRepository = FakeAnimalRepository(animals = mockAnimals)
    val useCase = GetAnimalsUseCase(fakeRepository)
    
    // When - use case invoked
    val result = useCase()
    
    // Then - success with animals
    assertTrue(result.isSuccess)
    assertEquals(2, result.getOrNull()?.size)
    assertEquals(1, fakeRepository.getAnimalsCallCount)
}

@Test
fun `should return failure when repository fails`() = runTest {
    // Given - failing fake repository
    val fakeRepository = FakeAnimalRepository(
        shouldFail = true,
        exception = IOException("Network error")
    )
    val useCase = GetAnimalsUseCase(fakeRepository)
    
    // When
    val result = useCase()
    
    // Then - failure with exception
    assertTrue(result.isFailure)
    assertTrue(result.exceptionOrNull() is IOException)
}
```

---

## Future Real Implementation

When backend API is ready, create real repository implementation.

**Location**: `/composeApp/src/androidMain/kotlin/com/intive/aifirst/petspot/data/RemoteAnimalRepository.kt`

```kotlin
/**
 * Real repository implementation using REST API.
 * Fetches animal data from backend server.
 *
 * NOT IMPLEMENTED YET - placeholder for future iteration.
 */
class RemoteAnimalRepository(
    private val httpClient: HttpClient,  // Ktor client
    private val baseUrl: String
) : AnimalRepository {
    
    override suspend fun getAnimals(): Result<List<Animal>> {
        return try {
            // GET /api/animals
            val response = httpClient.get("$baseUrl/api/animals")
            val animals = response.body<List<AnimalDto>>()
                .map { it.toDomain() }  // Map DTO to domain model
            Result.success(animals)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        }
    }
}
```

**Migration Steps**:
1. Add Ktor HTTP client dependency
2. Create AnimalDto data class matching API response
3. Implement RemoteAnimalRepository
4. Update Koin DI module to use RemoteAnimalRepository instead of Mock
5. Add integration tests with MockWebServer
6. No changes to use cases or ViewModels required

---

## Contract Validation

### Success Criteria

Repository implementation MUST:
- [ ] Implement `AnimalRepository` interface
- [ ] Return `Result<List<Animal>>` (not nullable)
- [ ] Use `suspend` modifier (coroutine-based)
- [ ] Return `Result.success` for successful data fetch
- [ ] Return `Result.failure` for errors (network, parsing, etc.)
- [ ] Allow empty list (valid scenario)
- [ ] Not throw exceptions directly (wrap in Result.failure)

### Mock Implementation Requirements

Mock repository MUST:
- [ ] Return 8-12 Animal entities (per spec clarification)
- [ ] Include mix of species (Dog, Cat, Bird, Rabbit)
- [ ] Include mix of statuses (majority Missing, some Found)
- [ ] Include varied locations (different cities)
- [ ] Simulate realistic network delay (100-1000ms)
- [ ] Never fail (unless explicitly testing error states with Fake)
- [ ] Return consistent data (same list every call for UI predictability)

---

## Summary

- ✅ `AnimalRepository` interface defined with `getAnimals()` method
- ✅ Contract uses Kotlin Coroutines (`suspend`) and `Result<T>` wrapper
- ✅ Mock implementations defined for Android, iOS, Web
- ✅ Mock data consistent across platforms (10 animals with varied attributes)
- ✅ Fake repository provided for unit testing (control success/failure)
- ✅ Future real implementation path documented
- ✅ Contract validation criteria defined

**Status**: Contract complete. Ready for quickstart documentation.

