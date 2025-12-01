package com.intive.aifirst.petspot.data

import com.intive.aifirst.petspot.composeapp.domain.fixtures.MockAnimalData
import com.intive.aifirst.petspot.composeapp.domain.models.Animal
import com.intive.aifirst.petspot.composeapp.domain.repositories.AnimalRepository
import com.intive.aifirst.petspot.data.api.AnnouncementApiClient
import com.intive.aifirst.petspot.data.mappers.toDomain
import kotlinx.coroutines.delay

/**
 * Repository implementation that fetches pet announcements from the backend API.
 *
 * @property apiClient HTTP client for backend API communication
 */
class AnimalRepositoryImpl(
    private val apiClient: AnnouncementApiClient,
) : AnimalRepository {
    /**
     * Retrieves all pet announcements from the backend API.
     *
     * @return List of animals from backend
     * @throws Exception on network or API errors
     */
    override suspend fun getAnimals(): List<Animal> {
        val response = apiClient.getAnnouncements()
        return response.data.map { it.toDomain() }
    }

    /**
     * Retrieves a single pet announcement by ID from the backend API.
     * TODO: Replace with API call in Phase 4 (T024)
     *
     * @param id Unique identifier of the announcement
     * @return Animal entity
     * @throws NoSuchElementException if animal not found
     */
    override suspend fun getAnimalById(id: String): Animal {
        delay(500)
        return MockAnimalData.generateMockAnimals().find { it.id == id }
            ?: throw NoSuchElementException("Animal not found: $id")
    }
}
