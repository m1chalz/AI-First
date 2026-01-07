package com.intive.aifirst.petspot.features.mapPreview.domain.usecases

import com.intive.aifirst.petspot.composeapp.domain.fixtures.MockAnimalData
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.repositories.AnimalRepository
import com.intive.aifirst.petspot.domain.models.LocationCoordinates
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for GetNearbyAnimalsForMapUseCase.
 * Follows Given-When-Then pattern.
 */
class GetNearbyAnimalsForMapUseCaseTest {
    private lateinit var fakeRepository: FakeAnimalRepository
    private lateinit var useCase: GetNearbyAnimalsForMapUseCase

    @BeforeEach
    fun setUp() {
        fakeRepository = FakeAnimalRepository()
        useCase = GetNearbyAnimalsForMapUseCase(fakeRepository)
    }

    @Test
    fun `should return success with animals when repository returns data`() =
        runTest {
            // Given
            val animals = MockAnimalData.generateMockAnimals(5)
            fakeRepository.setAnimals(animals)
            val location = LocationCoordinates(52.2297, 21.0122)

            // When
            val result = useCase(location)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(5, result.getOrNull()?.size)
        }

    @Test
    fun `should return success with empty list when repository returns no animals`() =
        runTest {
            // Given
            fakeRepository.setAnimals(emptyList())
            val location = LocationCoordinates(52.2297, 21.0122)

            // When
            val result = useCase(location)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(0, result.getOrNull()?.size)
        }

    @Test
    fun `should return failure when repository throws exception`() =
        runTest {
            // Given
            fakeRepository.shouldThrowException = true
            val location = LocationCoordinates(52.2297, 21.0122)

            // When
            val result = useCase(location)

            // Then
            assertTrue(result.isFailure)
        }

    @Test
    fun `should use default 10km radius when not specified`() =
        runTest {
            // Given
            val animals = MockAnimalData.generateMockAnimals(3)
            fakeRepository.setAnimals(animals)
            val location = LocationCoordinates(52.2297, 21.0122)

            // When
            useCase(location)

            // Then
            assertEquals(10, fakeRepository.lastRequestedRange)
        }

    @Test
    fun `should use custom radius when specified`() =
        runTest {
            // Given
            val animals = MockAnimalData.generateMockAnimals(3)
            fakeRepository.setAnimals(animals)
            val location = LocationCoordinates(52.2297, 21.0122)
            val customRadius = 25

            // When
            useCase(location, radiusKm = customRadius)

            // Then
            assertEquals(customRadius, fakeRepository.lastRequestedRange)
        }

    @Test
    fun `should pass correct coordinates to repository`() =
        runTest {
            // Given
            val animals = MockAnimalData.generateMockAnimals(3)
            fakeRepository.setAnimals(animals)
            val location = LocationCoordinates(52.2297, 21.0122)

            // When
            useCase(location)

            // Then
            assertEquals(52.2297, fakeRepository.lastRequestedLat)
            assertEquals(21.0122, fakeRepository.lastRequestedLng)
        }

    /**
     * Fake repository for testing.
     */
    private class FakeAnimalRepository : AnimalRepository {
        private var animals: List<Animal> = emptyList()
        var shouldThrowException = false
        var lastRequestedLat: Double? = null
        var lastRequestedLng: Double? = null
        var lastRequestedRange: Int? = null

        fun setAnimals(animals: List<Animal>) {
            this.animals = animals
        }

        override suspend fun getAnimals(
            lat: Double?,
            lng: Double?,
            range: Int?,
        ): List<Animal> {
            lastRequestedLat = lat
            lastRequestedLng = lng
            lastRequestedRange = range

            if (shouldThrowException) throw RuntimeException("Test exception")
            return animals
        }

        override suspend fun getAnimalById(id: String): Animal {
            if (shouldThrowException) throw RuntimeException("Test exception")
            return animals.find { it.id == id }
                ?: throw NoSuchElementException("Animal not found: $id")
        }
    }
}
