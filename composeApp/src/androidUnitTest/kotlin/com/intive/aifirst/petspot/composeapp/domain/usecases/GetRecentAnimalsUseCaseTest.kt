package com.intive.aifirst.petspot.composeapp.domain.usecases

import com.intive.aifirst.petspot.composeapp.domain.fixtures.MockAnimalData
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalGender
import com.intive.aifirst.petspot.composeapp.domain.models.AnimalStatus
import com.intive.aifirst.petspot.composeapp.domain.models.Location
import com.intive.aifirst.petspot.composeapp.domain.repositories.AnimalRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for GetRecentAnimalsUseCase.
 * Follows Given-When-Then pattern.
 */
class GetRecentAnimalsUseCaseTest {
    private lateinit var useCase: GetRecentAnimalsUseCase
    private lateinit var repository: FakeAnimalRepository

    @BeforeEach
    fun setUp() {
        repository = FakeAnimalRepository()
        useCase = GetRecentAnimalsUseCase(repository)
    }

    @Test
    fun `should filter only MISSING status animals`() =
        runTest {
            // Given - mixed statuses in repository
            val allAnimals = MockAnimalData.generateMockAnimals(16)
            repository.setAnimals(allAnimals)

            // When
            val result = useCase(limit = 10)

            // Then - only MISSING status animals returned
            assertTrue(result.all { it.status == AnimalStatus.MISSING })
        }

    @Test
    fun `should sort by lastSeenDate descending (newest first)`() =
        runTest {
            // Given - animals with ISO date format (as returned by backend)
            val animalsWithIsoDate =
                listOf(
                    createAnimal("1", "2025-12-15", AnimalStatus.MISSING),
                    createAnimal("2", "2025-12-18", AnimalStatus.MISSING),
                    createAnimal("3", "2025-12-10", AnimalStatus.MISSING),
                    createAnimal("4", "2025-12-20", AnimalStatus.MISSING),
                    createAnimal("5", "2025-12-12", AnimalStatus.MISSING),
                )
            repository.setAnimals(animalsWithIsoDate)

            // When
            val result = useCase(limit = 5)

            // Then - should be sorted newest first: 20, 18, 15, 12, 10
            assertEquals(5, result.size)
            assertEquals("4", result[0].id) // 2025-12-20
            assertEquals("2", result[1].id) // 2025-12-18
            assertEquals("1", result[2].id) // 2025-12-15
            assertEquals("5", result[3].id) // 2025-12-12
            assertEquals("3", result[4].id) // 2025-12-10
        }

    private fun createAnimal(
        id: String,
        lastSeenDate: String,
        status: AnimalStatus,
    ): Animal =
        Animal(
            id = id,
            name = "Pet $id",
            photoUrl = "",
            location = Location(latitude = 52.0, longitude = 21.0),
            species = "Dog",
            breed = "Mixed",
            gender = AnimalGender.MALE,
            status = status,
            lastSeenDate = lastSeenDate,
            description = "Test pet",
            email = null,
            phone = null,
        )

    @Test
    fun `should limit results to 5 by default`() =
        runTest {
            // Given - many animals in repository
            val allAnimals = MockAnimalData.generateMockAnimals(16)
            repository.setAnimals(allAnimals)

            // When - call with default limit
            val result = useCase()

            // Then - exactly 5 results (or less if fewer MISSING animals)
            assertTrue(result.size <= 5, "Should not exceed 5 results")
        }

    @Test
    fun `should limit results to specified count`() =
        runTest {
            // Given - many animals in repository
            val allAnimals = MockAnimalData.generateMockAnimals(16)
            repository.setAnimals(allAnimals)

            // When - call with limit of 3
            val result = useCase(limit = 3)

            // Then - at most 3 results
            assertTrue(result.size <= 3, "Should not exceed 3 results")
        }

    @Test
    fun `should return empty list when no MISSING animals`() =
        runTest {
            // Given - only FOUND and CLOSED animals
            val allAnimals =
                MockAnimalData.generateMockAnimals(16)
                    .filter { it.status != AnimalStatus.MISSING }
            repository.setAnimals(allAnimals)

            // When
            val result = useCase()

            // Then
            assertTrue(result.isEmpty(), "Should return empty list when no MISSING animals")
        }

    @Test
    fun `should propagate repository exception`() =
        runTest {
            // Given
            repository.shouldThrowException = true

            // When & Then
            assertThrows(RuntimeException::class.java) {
                kotlinx.coroutines.runBlocking {
                    useCase()
                }
            }
        }

    /**
     * Fake repository for testing.
     */
    private class FakeAnimalRepository : AnimalRepository {
        private var animals: List<Animal> = emptyList()
        var shouldThrowException = false

        fun setAnimals(animals: List<Animal>) {
            this.animals = animals
        }

        override suspend fun getAnimals(
            lat: Double?,
            lng: Double?,
            range: Int?,
        ): List<Animal> {
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
