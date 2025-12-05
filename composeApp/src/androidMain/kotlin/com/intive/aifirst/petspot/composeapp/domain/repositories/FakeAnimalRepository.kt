package com.intive.aifirst.petspot.composeapp.domain.repositories

import com.intive.aifirst.petspot.composeapp.domain.fixtures.MockAnimalData
import com.intive.aifirst.petspot.composeapp.domain.models.Animal

/**
 * Fake repository implementation for unit testing.
 * Implements AnimalRepository interface for controlled test scenarios.
 * Uses MockAnimalData as the single source of truth for test data.
 *
 * Allows controlling success/failure scenarios for testing:
 * - Success: Returns mock animals from MockAnimalData
 * - Failure: Throws configurable exception
 * - Empty: Returns empty list when animalCount = 0
 */
class FakeAnimalRepository(
    private val animalCount: Int = 16,
    private val shouldFail: Boolean = false,
    private val exception: Throwable = Exception("Fake repository error"),
) : AnimalRepository {
    var getAnimalsCallCount = 0
        private set

    var getAnimalByIdCallCount = 0
        private set

    override suspend fun getAnimals(
        lat: Double?,
        lng: Double?,
        range: Int?,
    ): List<Animal> {
        getAnimalsCallCount++

        return if (shouldFail) {
            throw exception
        } else {
            MockAnimalData.generateMockAnimals(animalCount)
        }
    }

    override suspend fun getAnimalById(id: String): Animal {
        getAnimalByIdCallCount++

        if (shouldFail) {
            throw exception
        }

        return MockAnimalData.generateMockAnimals(animalCount).find { it.id == id }
            ?: throw NoSuchElementException("Animal not found: $id")
    }
}
