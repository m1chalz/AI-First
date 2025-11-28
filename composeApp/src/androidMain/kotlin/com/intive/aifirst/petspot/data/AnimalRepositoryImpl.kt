package com.intive.aifirst.petspot.data

import com.intive.aifirst.petspot.composeapp.domain.fixtures.MockAnimalData
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.repositories.AnimalRepository
import com.intive.aifirst.petspot.data.api.AnnouncementApiClient
import kotlinx.coroutines.delay

/**
 * Repository implementation that fetches pet announcements from the backend API.
 * Currently uses mock data - will be updated in Phase 3 to use real API calls.
 *
 * @property apiClient HTTP client for backend API communication
 */
class AnimalRepositoryImpl(
    private val apiClient: AnnouncementApiClient,
) : AnimalRepository {
    /** Simulated network delay in milliseconds (temporary - removed in Phase 3) */
    private val networkDelayMs: Long = 500

    /**
     * Retrieves all pet announcements from the backend API.
     * TODO: Replace with API call in Phase 3 (T017)
     *
     * @return List of animals from backend
     */
    override suspend fun getAnimals(): List<Animal> {
        // Simulate network delay (temporary)
        delay(networkDelayMs)

        // Return mock data (temporary - replaced with API call in Phase 3)
        return MockAnimalData.generateMockAnimals()
    }

    /**
     * Retrieves a single pet announcement by ID from the backend API.
     * TODO: Replace with API call in Phase 3 (T024)
     *
     * @param id Unique identifier of the announcement
     * @return Animal entity
     * @throws NoSuchElementException if animal not found
     */
    override suspend fun getAnimalById(id: String): Animal {
        delay(networkDelayMs)
        return MockAnimalData.generateMockAnimals().find { it.id == id }
            ?: throw NoSuchElementException("Animal not found: $id")
    }
}
